package opennlp.tools.tokenize;

import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Pattern;

import opennlp.tools.dictionary.Dictionary;
import opennlp.tools.ml.maxent.GISModel;
import opennlp.tools.ml.model.Context;
import opennlp.tools.tokenize.lang.Factory;
import opennlp.tools.util.CollectionObjectStream;
import opennlp.tools.util.Span;
import opennlp.tools.util.StringList;
import opennlp.tools.util.TrainingParameters;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TokenizerMEPromptTest {

  @Test
  public void probabilitiesAreInitiallyEmptyAndReturnedDefensively() {
    GISModel maxent = new GISModel(new Context[0], new String[0],
        new String[] {"F", "T"});
    TokenizerFactory factory = new TokenizerFactory("en", null, false, null);
    TokenizerModel model = new TokenizerModel(maxent, new HashMap<String, String>(), factory);
    TokenizerME tokenizer = new TokenizerME(model);

    double[] first = tokenizer.getTokenProbabilities();
    double[] second = tokenizer.getTokenProbabilities();

    assertArrayEquals(new double[0], first, 0d);
    assertArrayEquals(new double[0], second, 0d);
    assertNotSame(first, second);
  }

  @Test
  public void emptyInputProducesNoTokensAndClearsProbabilities() {
    GISModel maxent = new GISModel(new Context[0], new String[0],
        new String[] {"F", "T"});
    TokenizerFactory factory = new TokenizerFactory("en", null, false, null);
    TokenizerME tokenizer = new TokenizerME(
        new TokenizerModel(maxent, new HashMap<String, String>(), factory));
    tokenizer.tokenize("prior");

    assertArrayEquals(new String[0], tokenizer.tokenize(""));
    assertArrayEquals(new Span[0], tokenizer.tokenizePos(""));
    assertArrayEquals(new double[0], tokenizer.getTokenProbabilities(), 0d);
  }

  @Test
  public void whitespaceOnlyInputProducesNoTokens() {
    GISModel maxent = new GISModel(new Context[0], new String[0],
        new String[] {"F", "T"});
    TokenizerFactory factory = new TokenizerFactory("en", null, false, null);
    TokenizerME tokenizer = new TokenizerME(
        new TokenizerModel(maxent, new HashMap<String, String>(), factory));

    assertArrayEquals(new Span[0], tokenizer.tokenizePos(" \t\n\r  "));
    assertArrayEquals(new double[0], tokenizer.getTokenProbabilities(), 0d);
  }

  @Test
  public void singleCharacterTokensBypassStatisticalModel() {
    GISModel maxent = new GISModel(new Context[0], new String[0],
        new String[] {"T", "F"});
    TokenizerFactory factory = new TokenizerFactory("en", null, false, null);
    TokenizerME tokenizer = new TokenizerME(
        new TokenizerModel(maxent, new HashMap<String, String>(), factory));

    Span[] spans = tokenizer.tokenizePos("a ! z");

    assertArrayEquals(new Span[] {new Span(0, 1), new Span(2, 3), new Span(4, 5)}, spans);
    assertArrayEquals(new double[] {1d, 1d, 1d}, tokenizer.getTokenProbabilities(), 0d);
  }

  @Test
  public void noSplitModelPreservesWhitespaceDelimitedTokensAndOffsets() {
    GISModel maxent = new GISModel(new Context[0], new String[0],
        new String[] {"F", "T"});
    TokenizerFactory factory = new TokenizerFactory("en", null, false, null);
    TokenizerME tokenizer = new TokenizerME(
        new TokenizerModel(maxent, new HashMap<String, String>(), factory));

    Span[] spans = tokenizer.tokenizePos("  ab.cd\txyz  ");

    assertArrayEquals(new Span[] {new Span(2, 7), new Span(8, 11)}, spans);
    assertArrayEquals(new double[] {0.0625d, 0.25d},
        tokenizer.getTokenProbabilities(), 0d);
    assertArrayEquals(new String[] {"ab.cd", "xyz"}, tokenizer.tokenize("  ab.cd\txyz  "));
  }

  @Test
  public void splitModelSplitsAtEveryInternalBoundary() {
    GISModel maxent = new GISModel(new Context[0], new String[0],
        new String[] {"T", "F"});
    TokenizerFactory factory = new TokenizerFactory("en", null, false, null);
    TokenizerME tokenizer = new TokenizerME(
        new TokenizerModel(maxent, new HashMap<String, String>(), factory));

    Span[] spans = tokenizer.tokenizePos("abc");

    assertArrayEquals(new Span[] {new Span(0, 1), new Span(1, 2), new Span(2, 3)}, spans);
    assertArrayEquals(new double[] {0.5d, 0.5d, 1d},
        tokenizer.getTokenProbabilities(), 0d);
    assertArrayEquals(new String[] {"a", "b", "c"}, tokenizer.tokenize("abc"));
  }

  @Test
  public void splitModelMaintainsAbsoluteOffsetsAcrossMultipleCallsAndSpaces() {
    GISModel maxent = new GISModel(new Context[0], new String[0],
        new String[] {"T", "F"});
    TokenizerFactory factory = new TokenizerFactory("en", null, false, null);
    TokenizerME tokenizer = new TokenizerME(
        new TokenizerModel(maxent, new HashMap<String, String>(), factory));

    assertArrayEquals(new Span[] {new Span(1, 2), new Span(2, 3), new Span(5, 6),
        new Span(6, 7)}, tokenizer.tokenizePos(" xy  zw"));
    assertArrayEquals(new double[] {0.5d, 1d, 0.5d, 1d},
        tokenizer.getTokenProbabilities(), 0d);

    assertArrayEquals(new Span[] {new Span(0, 1)}, tokenizer.tokenizePos("q"));
    assertArrayEquals(new double[] {1d}, tokenizer.getTokenProbabilities(), 0d);
  }

  @Test
  public void mutatingReturnedProbabilityArrayDoesNotChangeTokenizerState() {
    GISModel maxent = new GISModel(new Context[0], new String[0],
        new String[] {"T", "F"});
    TokenizerFactory factory = new TokenizerFactory("en", null, false, null);
    TokenizerME tokenizer = new TokenizerME(
        new TokenizerModel(maxent, new HashMap<String, String>(), factory));
    tokenizer.tokenizePos("xy");

    double[] probabilities = tokenizer.getTokenProbabilities();
    probabilities[0] = 0d;

    assertArrayEquals(new double[] {0.5d, 1d}, tokenizer.getTokenProbabilities(), 0d);
  }

  @Test
  public void alphanumericOptimizationSkipsModelForMatchingTokens() {
    GISModel maxent = new GISModel(new Context[0], new String[0],
        new String[] {"T", "F"});
    TokenizerFactory factory = new TokenizerFactory("en", null, true, null);
    TokenizerME tokenizer = new TokenizerME(
        new TokenizerModel(maxent, new HashMap<String, String>(), factory));

    assertTrue(tokenizer.useAlphaNumericOptimization());
    assertArrayEquals(new String[] {"Alpha42", "x", ".", "y"},
        tokenizer.tokenize("Alpha42 x.y"));
    assertArrayEquals(new double[] {1d, 0.5d, 0.5d, 1d},
        tokenizer.getTokenProbabilities(), 0d);
  }

  @Test
  public void disabledOptimizationReportsFalseAndEvaluatesLetters() {
    GISModel maxent = new GISModel(new Context[0], new String[0],
        new String[] {"T", "F"});
    TokenizerFactory factory = new TokenizerFactory("en", null, false, null);
    TokenizerME tokenizer = new TokenizerME(
        new TokenizerModel(maxent, new HashMap<String, String>(), factory));

    assertFalse(tokenizer.useAlphaNumericOptimization());
    assertArrayEquals(new String[] {"A", "b", "1", "2"}, tokenizer.tokenize("Ab12"));
    assertArrayEquals(new double[] {0.5d, 0.5d, 0.5d, 1d},
        tokenizer.getTokenProbabilities(), 0d);
  }

  @Test
  public void customOptimizationPatternCanPreserveHyphenatedToken() {
    GISModel maxent = new GISModel(new Context[0], new String[0],
        new String[] {"T", "F"});
    Pattern wordsWithHyphens = Pattern.compile("^[A-Za-z-]+$");
    TokenizerFactory factory = new TokenizerFactory("en", null, true, wordsWithHyphens);
    TokenizerME tokenizer = new TokenizerME(
        new TokenizerModel(maxent, new HashMap<String, String>(), factory));

    assertArrayEquals(new String[] {"state-of-the-art", "4", "2"},
        tokenizer.tokenize("state-of-the-art 42"));
    assertArrayEquals(new double[] {1d, 0.5d, 1d},
        tokenizer.getTokenProbabilities(), 0d);
  }

  @Test
  public void languageFactoryConstructorUsesModelOptimizationSetting() {
    GISModel maxent = new GISModel(new Context[0], new String[0],
        new String[] {"T", "F"});
    TokenizerFactory tokenizerFactory = new TokenizerFactory("de", null, true, null);
    TokenizerModel model = new TokenizerModel(maxent, new HashMap<String, String>(),
        tokenizerFactory);
    TokenizerME tokenizer = new TokenizerME(model, new Factory());

    assertTrue(tokenizer.useAlphaNumericOptimization());
    assertArrayEquals(new String[] {"Caf\u00e9", "A", ".", "B"}, tokenizer.tokenize("Caf\u00e9 A.B"));
    assertArrayEquals(new double[] {1d, 0.5d, 0.5d, 1d},
        tokenizer.getTokenProbabilities(), 0d);
  }

  @Test
  public void abbreviationDictionaryPreventsInternalSplitForTwoCharacterEntry() {
    GISModel maxent = new GISModel(new Context[0], new String[0],
        new String[] {"T", "F"});
    Dictionary abbreviations = new Dictionary();
    abbreviations.put(new StringList("X."));
    TokenizerFactory factory = new TokenizerFactory("en", abbreviations, false, null);
    TokenizerME tokenizer = new TokenizerME(
        new TokenizerModel(maxent, new HashMap<String, String>(), factory));

    assertArrayEquals(new String[] {"X.", "Y", "."}, tokenizer.tokenize("X. Y."));
    assertArrayEquals(new double[] {0.5d, 0.5d, 1d},
        tokenizer.getTokenProbabilities(), 0d);
  }

  @Test
  public void absentAbbreviationDictionaryAllowsSameCandidateToSplit() {
    GISModel maxent = new GISModel(new Context[0], new String[0],
        new String[] {"T", "F"});
    TokenizerFactory factory = new TokenizerFactory("en", null, false, null);
    TokenizerME tokenizer = new TokenizerME(
        new TokenizerModel(maxent, new HashMap<String, String>(), factory));

    assertArrayEquals(new String[] {"X", "."}, tokenizer.tokenize("X."));
    assertArrayEquals(new double[] {0.5d, 1d}, tokenizer.getTokenProbabilities(), 0d);
  }

  @Test
  public void keepNewLinesAddsCrAndLfAsIndividualTokens() {
    GISModel maxent = new GISModel(new Context[0], new String[0],
        new String[] {"F", "T"});
    TokenizerFactory factory = new TokenizerFactory("en", null, false, null);
    TokenizerME tokenizer = new TokenizerME(
        new TokenizerModel(maxent, new HashMap<String, String>(), factory));

    assertArrayEquals(new String[] {"left", "right"}, tokenizer.tokenize("left\r\nright"));
    tokenizer.setKeepNewLines(true);
    assertArrayEquals(new Span[] {new Span(0, 4), new Span(4, 5), new Span(5, 6),
        new Span(6, 11)}, tokenizer.tokenizePos("left\r\nright"));
    assertArrayEquals(new String[] {"left", "\r", "\n", "right"},
        tokenizer.tokenize("left\r\nright"));
  }

  @Test
  public void keepNewLinesCanBeTurnedOffAgain() {
    GISModel maxent = new GISModel(new Context[0], new String[0],
        new String[] {"F", "T"});
    TokenizerFactory factory = new TokenizerFactory("en", null, false, null);
    TokenizerME tokenizer = new TokenizerME(
        new TokenizerModel(maxent, new HashMap<String, String>(), factory));

    tokenizer.setKeepNewLines(true);
    assertArrayEquals(new String[] {"a", "\n", "b"}, tokenizer.tokenize("a\nb"));
    tokenizer.setKeepNewLines(false);
    assertArrayEquals(new String[] {"a", "b"}, tokenizer.tokenize("a\nb"));
    assertArrayEquals(new double[] {1d, 1d}, tokenizer.getTokenProbabilities(), 0d);
  }

  @Test
  public void nullInputFailsAndLeavesPreviousProbabilitiesUntouched() {
    GISModel maxent = new GISModel(new Context[0], new String[0],
        new String[] {"F", "T"});
    TokenizerFactory factory = new TokenizerFactory("en", null, false, null);
    TokenizerME tokenizer = new TokenizerME(
        new TokenizerModel(maxent, new HashMap<String, String>(), factory));
    tokenizer.tokenize("abc");

    try {
      tokenizer.tokenizePos(null);
      fail("Expected null input to be rejected");
    } catch (NullPointerException expected) {
      assertNotNull(expected);
    }

    assertArrayEquals(new double[] {0.25d}, tokenizer.getTokenProbabilities(), 0d);
  }

  @Test(expected = NullPointerException.class)
  public void nullModelIsRejectedByConstructor() {
    new TokenizerME((TokenizerModel) null);
  }

  @Test
  public void trainingIntegratesSamplesFactoryParametersAndTokenizer() throws Exception {
    TokenSample splitSample = new TokenSample("abcd ef",
        new Span[] {new Span(0, 2), new Span(2, 4), new Span(5, 7)});
    TokenSample unsplitSample = new TokenSample("wxyz ij",
        new Span[] {new Span(0, 4), new Span(5, 7)});
    CollectionObjectStream<TokenSample> samples = new CollectionObjectStream<>(
        Arrays.asList(splitSample, unsplitSample));
    TokenizerFactory factory = new TokenizerFactory("en", null, false, null);
    TrainingParameters parameters = TrainingParameters.defaultParams();
    parameters.put("Cutoff", 0);
    parameters.put("Iterations", 5);

    TokenizerModel trainedModel = TokenizerME.train(samples, factory, parameters);
    TokenizerME tokenizer = new TokenizerME(trainedModel);
    String[] tokens = tokenizer.tokenize("abcd ij");

    assertNotNull(trainedModel);
    assertNotNull(tokens);
    assertTrue(tokens.length >= 2);
    assertEquals(tokens.length, tokenizer.getTokenProbabilities().length);
  }
}
