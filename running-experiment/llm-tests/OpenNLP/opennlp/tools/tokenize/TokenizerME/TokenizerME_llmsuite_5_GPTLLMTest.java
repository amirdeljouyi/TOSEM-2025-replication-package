package opennlp.tools.tokenize;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

import opennlp.tools.ml.BeamSearch;
import opennlp.tools.ml.EventTrainer;
import opennlp.tools.ml.SequenceTrainer;
import opennlp.tools.ml.TrainerFactory;
import opennlp.tools.ml.model.AbstractModel;
import opennlp.tools.ml.model.Event;
import opennlp.tools.ml.model.MaxentModel;
import opennlp.tools.ml.model.SequenceClassificationModel;
import opennlp.tools.ml.perceptron.PerceptronTrainer;
import opennlp.tools.namefind.NameSample;
import opennlp.tools.sentdetect.lang.Factory;
import opennlp.tools.util.*;
import opennlp.tools.util.featuregen.AdaptiveFeatureGenerator;
import opennlp.tools.util.featuregen.GeneratorFactory;
import opennlp.tools.util.featuregen.TokenFeatureGenerator;
import opennlp.tools.util.featuregen.WindowFeatureGenerator;
import opennlp.tools.util.model.ArtifactProvider;
import opennlp.tools.util.model.ArtifactSerializer;
import opennlp.tools.util.model.UncloseableInputStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import opennlp.tools.formats.ResourceAsStreamFactory;
import opennlp.tools.namefind.NameFinderME;

import static opennlp.tools.formats.Conll02NameSampleStream.*;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class TokenizerME_llmsuite_5_GPTLLMTest {

 @Test
  public void testTokenizePos_SingleWord() {
    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator contextGenerator = mock(TokenContextGenerator.class);

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(contextGenerator);
    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[A-Za-z0-9]+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel, null);

    Span[] spans = tokenizer.tokenizePos("Hello");

    assertEquals(1, spans.length);
    assertEquals("Hello", "Hello".substring(spans[0].getStart(), spans[0].getEnd()));
    double[] probs = tokenizer.getTokenProbabilities();
    assertEquals(1, probs.length);
    assertEquals(1.0, probs[0], 0.0001);
  }
@Test
  public void testTokenizePos_MultipleWordsWithSpaces() {
    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator contextGenerator = mock(TokenContextGenerator.class);

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(contextGenerator);
    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[A-Za-z0-9]+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel, null);

    Span[] spans = tokenizer.tokenizePos("Hello world");

    assertEquals(2, spans.length);
    assertEquals("Hello", "Hello world".substring(spans[0].getStart(), spans[0].getEnd()));
    assertEquals("world", "Hello world".substring(spans[1].getStart(), spans[1].getEnd()));
  }
@Test
  public void testTokenizePos_SingleCharacter() {
    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator contextGenerator = mock(TokenContextGenerator.class);

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(contextGenerator);
    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[A-Za-z0-9]+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel, null);

    Span[] spans = tokenizer.tokenizePos("A");

    assertEquals(1, spans.length);
    assertEquals("A", "A".substring(spans[0].getStart(), spans[0].getEnd()));
    double[] probs = tokenizer.getTokenProbabilities();
    assertEquals(1, probs.length);
    assertEquals(1.0, probs[0], 0.0001);
  }
@Test
  public void testUseAlphaNumericOptimizationReturnsTrue() {
    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator contextGenerator = mock(TokenContextGenerator.class);

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(contextGenerator);
    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[a-zA-Z0-9]+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel, null);

    assertTrue(tokenizer.useAlphaNumericOptimization());
  }
@Test
  public void testGetTokenProbabilitiesReturnsEmptyArrayInitially() {
    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator contextGenerator = mock(TokenContextGenerator.class);

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(contextGenerator);
    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[a-zA-Z0-9]+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel, null);
    double[] probs = tokenizer.getTokenProbabilities();

    assertNotNull(probs);
    assertEquals(0, probs.length);
  }
@Test
  public void testIsAcceptableAbbreviation_WithDictionary() {
    Dictionary dictionary = new Dictionary();
    dictionary.put(new StringList("Dr."));

    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator contextGenerator = mock(TokenContextGenerator.class);
    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(contextGenerator);
    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[a-zA-Z0-9]+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel, dictionary);

    boolean result = tokenizer.isAcceptableAbbreviation("Dr.");

    assertTrue(result);
  }
@Test
  public void testIsAcceptableAbbreviation_NoDictionary() {
    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator contextGenerator = mock(TokenContextGenerator.class);
    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(contextGenerator);
    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[a-zA-Z0-9]+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel, null);

    boolean result = tokenizer.isAcceptableAbbreviation("Dr.");

    assertFalse(result);
  }
@Test
  public void testTokenizePos_SplitByModel() {
    MaxentModel model = mock(MaxentModel.class);

    when(model.eval(any(Object[].class))).thenReturn(new double[]{0.9});
    when(model.getBestOutcome(new double[]{0.9})).thenReturn(TokenizerME.SPLIT);
    when(model.getIndex(TokenizerME.SPLIT)).thenReturn(0);

    TokenContextGenerator contextGenerator = mock(TokenContextGenerator.class);
    when(contextGenerator.getContext(eq("dog,cat"), anyInt())).thenReturn(new String[]{"a", "b"});

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(contextGenerator);
    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[a-zA-Z0-9]+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false); 

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel, null);

    Span[] spans = tokenizer.tokenizePos("dog,cat");

    assertTrue(spans.length >= 2);
  }
@Test
  public void testTokenizePos_AbbreviationSkipped() {
    Dictionary dictionary = new Dictionary();
    dictionary.put(new StringList("U.S.A."));

    MaxentModel model = mock(MaxentModel.class);
    double[] probs = new double[]{0.8};
    when(model.eval(any(Object[].class))).thenReturn(probs);
    when(model.getBestOutcome(probs)).thenReturn(TokenizerME.SPLIT);
    when(model.getIndex(TokenizerME.SPLIT)).thenReturn(0);

    TokenContextGenerator contextGenerator = mock(TokenContextGenerator.class);
    when(contextGenerator.getContext(eq("U.S.A."), anyInt())).thenReturn(new String[]{"x"});

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(contextGenerator);
    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[A-Za-z0-9]+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel, dictionary);

    Span[] spans = tokenizer.tokenizePos("U.S.A.");

    assertEquals(1, spans.length);
    assertEquals("U.S.A.", "U.S.A.".substring(spans[0].getStart(), spans[0].getEnd()));
  }
@Test
  public void testTokenizePos_EmptyString() {
    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator contextGen = mock(TokenContextGenerator.class);
    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(contextGen);
    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[A-Za-z0-9]+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel, null);
    Span[] spans = tokenizer.tokenizePos("");

    assertEquals(0, spans.length);
    assertEquals(0, tokenizer.getTokenProbabilities().length);
  }
@Test
  public void testTokenizePos_OnlyWhitespace() {
    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator contextGen = mock(TokenContextGenerator.class);
    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(contextGen);
    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[A-Za-z0-9]+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel, null);
    Span[] spans = tokenizer.tokenizePos("     ");

    assertEquals(0, spans.length);
    assertEquals(0, tokenizer.getTokenProbabilities().length);
  }
@Test
  public void testTokenizePos_PunctuationOnly_NoAbbreviationHandling() {
    MaxentModel model = mock(MaxentModel.class);
    double[] probs = new double[]{0.9};
    when(model.eval(any(Object[].class))).thenReturn(probs);
    when(model.getBestOutcome(probs)).thenReturn(TokenizerME.SPLIT);
    when(model.getIndex(TokenizerME.SPLIT)).thenReturn(0);

    TokenContextGenerator contextGen = mock(TokenContextGenerator.class);
    when(contextGen.getContext(eq("..."), anyInt())).thenReturn(new String[]{"x"});

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(contextGen);
    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[A-Za-z0-9]+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel, null);
    Span[] spans = tokenizer.tokenizePos("...");

    assertTrue(spans.length >= 1);
  }
@Test
  public void testTokenizePos_MultipleAbbreviations_AllRecognized() {
    Dictionary dictionary = new Dictionary();
    dictionary.put(new StringList("Mr."));
    dictionary.put(new StringList("Dr."));
    dictionary.put(new StringList("Inc."));

    MaxentModel model = mock(MaxentModel.class);
    double[] probs = new double[]{0.95};
    when(model.eval(any(Object[].class))).thenReturn(probs);
    when(model.getBestOutcome(probs)).thenReturn(TokenizerME.SPLIT);
    when(model.getIndex(TokenizerME.SPLIT)).thenReturn(0);

    TokenContextGenerator contextGen = mock(TokenContextGenerator.class);
    when(contextGen.getContext(anyString(), anyInt())).thenReturn(new String[]{"x"});

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(contextGen);
    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[A-Za-z0-9]+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel, dictionary);
    Span[] spans = tokenizer.tokenizePos("Mr. Smith works at Dr. Wong's Inc.");

    assertTrue(spans.length >= 4); 
  }
@Test
  public void testTokenizePos_AlphanumericMixedWithSymbol_OptimizationEnabled() {
    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator contextGen = mock(TokenContextGenerator.class);
    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(contextGen);
    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[A-Za-z0-9]+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel, null);
    Span[] spans = tokenizer.tokenizePos("Test123#code");

    assertTrue(spans.length >= 1); 
  }
@Test
  public void testTokenizePos_NoSplitsPredictedOnLongWord() {
    MaxentModel model = mock(MaxentModel.class);
    double[] probs = new double[]{0.1};
    when(model.eval(any(Object[].class))).thenReturn(probs);
    when(model.getBestOutcome(probs)).thenReturn(TokenizerME.NO_SPLIT);
    when(model.getIndex(TokenizerME.NO_SPLIT)).thenReturn(0);

    TokenContextGenerator contextGen = mock(TokenContextGenerator.class);
    when(contextGen.getContext(anyString(), anyInt())).thenReturn(new String[]{"z"});

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(contextGen);
    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[a-zA-Z0-9]+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel, null);
    Span[] spans = tokenizer.tokenizePos("unbreakablewordwithoutanysplit");

    assertEquals(1, spans.length);
    assertEquals("unbreakablewordwithoutanysplit",
        "unbreakablewordwithoutanysplit".substring(spans[0].getStart(), spans[0].getEnd()));
  }
@Test
  public void testTokenizePos_VeryLongTokenOver100Characters() {
    String longToken = "ThisIsAReallyLongTokenThatExceedsOneHundredCharactersInLengthAndShouldStillBeATokenAccordingToTheLogicOfTheTokenizerMEClass";
    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator contextGen = mock(TokenContextGenerator.class);
    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(contextGen);
    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[A-Za-z0-9]+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel, null);
    Span[] spans = tokenizer.tokenizePos(longToken);

    assertEquals(1, spans.length);
    assertEquals(longToken, longToken.substring(spans[0].getStart(), spans[0].getEnd()));
    assertEquals(1.0, tokenizer.getTokenProbabilities()[0], 0.0001);
  }
@Test
  public void testTokenizePos_ReturnsCorrectSpanIndexesForComplexToken() {
    MaxentModel model = mock(MaxentModel.class);
    double[] probs = new double[]{0.8};
    when(model.eval(any(Object[].class))).thenReturn(probs);
    when(model.getBestOutcome(probs)).thenReturn(TokenizerME.SPLIT);
    when(model.getIndex(TokenizerME.SPLIT)).thenReturn(0);

    TokenContextGenerator contextGen = mock(TokenContextGenerator.class);
    when(contextGen.getContext(eq("email@example.com"), anyInt())).thenReturn(new String[]{"context"});

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(contextGen);
    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[A-Za-z0-9]+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel, null);
    Span[] spans = tokenizer.tokenizePos("email@example.com");

    assertTrue(spans.length >= 1);
    assertTrue(spans[0].getStart() >= 0);
    assertTrue(spans[0].getEnd() <= "email@example.com".length());
  }
@Test
  public void testTokenizePos_InputWithNewlineCharacters() {
    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator contextGen = mock(TokenContextGenerator.class);

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(contextGen);
    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[A-Za-z0-9]+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel, null);

    Span[] spans = tokenizer.tokenizePos("This is\na test.");
    assertTrue(spans.length >= 3);
  }
@Test
  public void testTokenizePos_AbbreviationWithMultipleDotsHandledCorrectly() {
    Dictionary dict = new Dictionary();
    dict.put(new StringList("e.g."));

    MaxentModel model = mock(MaxentModel.class);
    double[] probs = new double[]{0.9};
    when(model.eval(any(Object[].class))).thenReturn(probs);
    when(model.getBestOutcome(probs)).thenReturn(TokenizerME.SPLIT);
    when(model.getIndex(TokenizerME.SPLIT)).thenReturn(0);

    TokenContextGenerator contextGen = mock(TokenContextGenerator.class);
    when(contextGen.getContext(anyString(), anyInt())).thenReturn(new String[]{"x"});

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(contextGen);
    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[A-Za-z0-9]+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel, dict);

    Span[] spans = tokenizer.tokenizePos("This is an example (e.g.) of abbreviation.");
    assertTrue(spans.length >= 1);
  }
@Test
  public void testTokenizePos_SpecialCharactersNoAlphaNumericOptimization() {
    MaxentModel model = mock(MaxentModel.class);
    double[] probs = new double[]{0.6};
    when(model.eval(any(Object[].class))).thenReturn(probs);
    when(model.getBestOutcome(probs)).thenReturn(TokenizerME.SPLIT);
    when(model.getIndex(TokenizerME.SPLIT)).thenReturn(0);

    TokenContextGenerator contextGen = mock(TokenContextGenerator.class);
    when(contextGen.getContext(any(String.class), anyInt())).thenReturn(new String[]{"ctx"});

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(contextGen);
    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[a-zA-Z0-9]+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel, null);
    Span[] spans = tokenizer.tokenizePos("@hashtag!");

    assertTrue(spans.length >= 1);
  }
@Test
  public void testTokenizePos_NumberTokenSplitByModel() {
    MaxentModel model = mock(MaxentModel.class);
    double[] probs = new double[]{0.75};
    when(model.eval(any(Object[].class))).thenReturn(probs);
    when(model.getBestOutcome(probs)).thenReturn(TokenizerME.SPLIT);
    when(model.getIndex(TokenizerME.SPLIT)).thenReturn(0);

    TokenContextGenerator contextGen = mock(TokenContextGenerator.class);
    when(contextGen.getContext(eq("123,456"), anyInt())).thenReturn(new String[]{"n"});

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(contextGen);
    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[0-9,]+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel, null);
    Span[] spans = tokenizer.tokenizePos("123,456");

    assertTrue(spans.length >= 1);
  }
@Test
  public void testTokenizePos_InputWithLeadingTrailingSpaces() {
    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator contextGen = mock(TokenContextGenerator.class);

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(contextGen);
    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("\\w+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel, null);

    Span[] spans = tokenizer.tokenizePos("  padded text  ");
    assertTrue(spans.length >= 1);
  }
@Test
  public void testTokenizePos_CurrencySymbolHandled() {
    MaxentModel model = mock(MaxentModel.class);
    double[] probs = new double[]{0.85};
    when(model.eval(any(Object[].class))).thenReturn(probs);
    when(model.getBestOutcome(probs)).thenReturn(TokenizerME.SPLIT);
    when(model.getIndex(TokenizerME.SPLIT)).thenReturn(0);

    TokenContextGenerator contextGen = mock(TokenContextGenerator.class);
    when(contextGen.getContext(eq("$100"), anyInt())).thenReturn(new String[]{"curr"});

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(contextGen);
    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("\\$?[0-9]+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel, null);

    Span[] spans = tokenizer.tokenizePos("$100");
    assertTrue(spans.length >= 1);
  }
@Test
  public void testTokenizePos_TokenEndsAtLastChar() {
    MaxentModel model = mock(MaxentModel.class);
    double[] probs = new double[]{0.7};
    when(model.eval(any(Object[].class))).thenReturn(probs);
    when(model.getBestOutcome(probs)).thenReturn(TokenizerME.NO_SPLIT);
    when(model.getIndex(TokenizerME.NO_SPLIT)).thenReturn(0);

    TokenContextGenerator contextGen = mock(TokenContextGenerator.class);
    when(contextGen.getContext(anyString(), anyInt())).thenReturn(new String[]{"ctx"});

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(contextGen);
    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[A-Za-z0-9]+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel, null);

    Span[] spans = tokenizer.tokenizePos("abc123");

    assertEquals(1, spans.length);
    assertEquals("abc123", "abc123".substring(spans[0].getStart(), spans[0].getEnd()));
  }
@Test
  public void testTokenizePos_TokenSplitFollowedByMultipleAbbreviations() {
    Dictionary dict = new Dictionary();
    dict.put(new StringList("e.g."));
    dict.put(new StringList("i.e."));

    MaxentModel model = mock(MaxentModel.class);
    double[] probs = new double[]{0.9};
    when(model.eval(any(Object[].class))).thenReturn(probs);
    when(model.getBestOutcome(probs)).thenReturn(TokenizerME.SPLIT);
    when(model.getIndex(TokenizerME.SPLIT)).thenReturn(0);

    TokenContextGenerator contextGen = mock(TokenContextGenerator.class);
    when(contextGen.getContext(anyString(), anyInt())).thenReturn(new String[]{"x"});

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(contextGen);
    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[A-Za-z.]+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel, dict);

    Span[] spans = tokenizer.tokenizePos("Some text. e.g. examples i.e. clarifications.");

    assertTrue(spans.length >= 3);
  }
@Test
  public void testTokenizePos_EndsWithDot_NotAbbreviation() {
    MaxentModel model = mock(MaxentModel.class);
    double[] probs = new double[]{0.8};
    when(model.eval(any(Object[].class))).thenReturn(probs);
    when(model.getBestOutcome(probs)).thenReturn(TokenizerME.SPLIT);
    when(model.getIndex(TokenizerME.SPLIT)).thenReturn(0);

    Dictionary dict = new Dictionary(); 

    TokenContextGenerator contextGen = mock(TokenContextGenerator.class);
    when(contextGen.getContext(anyString(), anyInt())).thenReturn(new String[]{"a"});

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(contextGen);
    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[a-zA-Z]+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel, dict);

    Span[] spans = tokenizer.tokenizePos("word.");

    assertTrue(spans.length >= 1);
    assertFalse(tokenizer.isAcceptableAbbreviation("word."));
  }
@Test
  public void testTokenizePos_InputContainsTabs() {
    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator contextGen = mock(TokenContextGenerator.class);

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(contextGen);
    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("\\w+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel, null);

    Span[] spans = tokenizer.tokenizePos("field1\tfield2\tfield3");

    assertEquals(3, spans.length);
  }
@Test
  public void testTokenizePos_HyphenatedWordIsTokenizedWhenOptimizationDisabled() {
    MaxentModel model = mock(MaxentModel.class);
    double[] probs = new double[]{0.95};
    when(model.eval(any(Object[].class))).thenReturn(probs);
    when(model.getBestOutcome(probs)).thenReturn(TokenizerME.SPLIT);
    when(model.getIndex(TokenizerME.SPLIT)).thenReturn(0);

    TokenContextGenerator contextGen = mock(TokenContextGenerator.class);
    when(contextGen.getContext(eq("state-of-the-art"), anyInt())).thenReturn(new String[]{"ctx"});

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(contextGen);
    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[A-Za-z0-9]+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel, null);

    Span[] spans = tokenizer.tokenizePos("state-of-the-art");

    assertTrue(spans.length >= 1);
  }
@Test
  public void testIsAcceptableAbbreviation_WithEmptyDictionary() {
    Dictionary emptyDict = new Dictionary(); 

    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator contextGen = mock(TokenContextGenerator.class);
    TokenizerFactory factory = mock(TokenizerFactory.class);

    when(factory.getContextGenerator()).thenReturn(contextGen);
    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[A-Za-z.]+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel, emptyDict);

    assertFalse(tokenizer.isAcceptableAbbreviation("etc."));
  }
@Test
  public void testTokenizePos_PreservesTokenOrder() {
    MaxentModel model = mock(MaxentModel.class);
    double[] probs = new double[]{0.6};
    when(model.eval(any(Object[].class))).thenReturn(probs);
    when(model.getBestOutcome(probs)).thenReturn(TokenizerME.SPLIT);
    when(model.getIndex(TokenizerME.SPLIT)).thenReturn(0);

    TokenContextGenerator contextGen = mock(TokenContextGenerator.class);
    when(contextGen.getContext(anyString(), anyInt())).thenReturn(new String[]{"ctx"});

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(contextGen);
    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[\\w]+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel, null);

    Span[] spans = tokenizer.tokenizePos("one.two");

    assertTrue(spans[0].getStart() < spans[0].getEnd());
    if (spans.length > 1) {
      assertTrue(spans[1].getStart() > spans[0].getStart());
    }
  }
@Test
  public void testTokenizePos_TokenWithSingleNonAlphaCharacter_OptimizationEnabled() {
    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator contextGen = mock(TokenContextGenerator.class);

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(contextGen);
    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[a-zA-Z0-9]+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel, null);

    Span[] spans = tokenizer.tokenizePos("!");

    assertEquals(1, spans.length);
    assertEquals("!", "!".substring(spans[0].getStart(), spans[0].getEnd()));
    assertEquals(1, tokenizer.getTokenProbabilities().length);
    assertEquals(1.0, tokenizer.getTokenProbabilities()[0], 0.0001);
  }
@Test
  public void testTokenizePos_TokenThatIsAbbreviationButTooShortToTokenize() {
    Dictionary dict = new Dictionary();
    dict.put(new StringList("M."));

    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator contextGen = mock(TokenContextGenerator.class);

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(contextGen);
    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile(".+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel, dict);

    Span[] spans = tokenizer.tokenizePos("M.");

    assertEquals(1, spans.length);
    assertTrue(tokenizer.isAcceptableAbbreviation("M."));
    assertEquals(1.0, tokenizer.getTokenProbabilities()[0], 0.0001);
  }
@Test
  public void testTokenizePos_EachCharacterSplits_ModelForcesSplitOnEveryChar() {
    MaxentModel model = mock(MaxentModel.class);
    double[] probs = new double[]{0.99};
    when(model.eval(any(Object[].class))).thenReturn(probs);
    when(model.getBestOutcome(probs)).thenReturn(TokenizerME.SPLIT);
    when(model.getIndex(TokenizerME.SPLIT)).thenReturn(0);

    TokenContextGenerator contextGen = mock(TokenContextGenerator.class);
    when(contextGen.getContext(eq("1234"), anyInt())).thenReturn(new String[]{"ctx"});

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(contextGen);
    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[0-9]+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel, null);

    Span[] spans = tokenizer.tokenizePos("1234");

    assertTrue(spans.length > 1);
  }
@Test
  public void testTokenizePos_RepeatedKnownAbbreviation_PreservesEach() {
    Dictionary dict = new Dictionary();
    dict.put(new StringList("Dr."));
    dict.put(new StringList("Mr."));

    MaxentModel model = mock(MaxentModel.class);
    double[] probs = new double[]{0.8};
    when(model.eval(any(Object[].class))).thenReturn(probs);
    when(model.getBestOutcome(probs)).thenReturn(TokenizerME.SPLIT);
    when(model.getIndex(TokenizerME.SPLIT)).thenReturn(0);

    TokenContextGenerator contextGen = mock(TokenContextGenerator.class);
    when(contextGen.getContext(anyString(), anyInt())).thenReturn(new String[]{"abbrev"});

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(contextGen);
    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[A-Za-z.]+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel, dict);

    Span[] spans = tokenizer.tokenizePos("Dr.Mr.");

    assertEquals(2, spans.length);
    assertEquals("Dr.", "Dr.Mr.".substring(spans[0].getStart(), spans[0].getEnd()));
    assertEquals("Mr.", "Dr.Mr.".substring(spans[1].getStart(), spans[1].getEnd()));
  }
@Test
  public void testTokenizePos_MixAlphaAndNonAlpha_SegmentAtFirstNonAlpha_ModelSplit() {
    MaxentModel model = mock(MaxentModel.class);
    double[] probs = new double[]{0.95};
    when(model.eval(any(Object[].class))).thenReturn(probs);
    when(model.getBestOutcome(probs)).thenReturn(TokenizerME.SPLIT);
    when(model.getIndex(TokenizerME.SPLIT)).thenReturn(0);

    TokenContextGenerator contextGen = mock(TokenContextGenerator.class);
    when(contextGen.getContext(eq("word*word"), anyInt())).thenReturn(new String[]{"split"});

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(contextGen);
    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[a-zA-Z0-9]+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel, null);

    Span[] spans = tokenizer.tokenizePos("word*word");

    assertTrue(spans.length >= 2);
  }
@Test
  public void testTokenizePos_LoneAbbreviationFollowedByDifferentWord() {
    Dictionary dict = new Dictionary();
    dict.put(new StringList("etc."));

    MaxentModel model = mock(MaxentModel.class);
    double[] probs = new double[]{0.99};
    when(model.eval(any(Object[].class))).thenReturn(probs);
    when(model.getBestOutcome(probs)).thenReturn(TokenizerME.SPLIT);
    when(model.getIndex(TokenizerME.SPLIT)).thenReturn(0);

    TokenContextGenerator contextGen = mock(TokenContextGenerator.class);
    when(contextGen.getContext(eq("etc.otherword"), anyInt())).thenReturn(new String[]{"abbrev"});

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(contextGen);
    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[a-zA-Z.]+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel, dict);

    Span[] spans = tokenizer.tokenizePos("etc.otherword");

    assertEquals(2, spans.length);
    assertEquals("etc.", "etc.otherword".substring(spans[0].getStart(), spans[0].getEnd()));
    assertEquals("otherword", "etc.otherword".substring(spans[1].getStart(), spans[1].getEnd()));
  }
@Test
  public void testTokenizePos_TokenizationAcrossSpecialUnicodeCharacter() {
    MaxentModel model = mock(MaxentModel.class);
    double[] probs = new double[]{0.85};
    when(model.eval(any(Object[].class))).thenReturn(probs);
    when(model.getBestOutcome(probs)).thenReturn(TokenizerME.SPLIT);
    when(model.getIndex(TokenizerME.SPLIT)).thenReturn(0);

    TokenContextGenerator contextGen = mock(TokenContextGenerator.class);
    when(contextGen.getContext(eq("smile😊face"), anyInt())).thenReturn(new String[]{"emoji"});

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(contextGen);
    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[a-zA-Z0-9]+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel, null);

    Span[] spans = tokenizer.tokenizePos("smile😊face");

    assertTrue(spans.length >= 2);
  }
@Test
  public void testTokenizePos_InputWithOnlyDotCharacter() {
    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator contextGen = mock(TokenContextGenerator.class);
    
    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(contextGen);
    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[\\w]+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);
    
    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel, null);
    
    Span[] spans = tokenizer.tokenizePos(".");
    
    assertEquals(1, spans.length);
    assertEquals(".", ".".substring(spans[0].getStart(), spans[0].getEnd()));
    assertEquals(1.0, tokenizer.getTokenProbabilities()[0], 0.0001);
  }
@Test
  public void testTokenizePos_AlphanumericOptimizationFalse_MatchesAlphaPattern() {
    MaxentModel model = mock(MaxentModel.class);
    double[] probs = new double[]{0.3};
    when(model.eval(any(Object[].class))).thenReturn(probs);
    when(model.getBestOutcome(probs)).thenReturn(TokenizerME.NO_SPLIT);
    when(model.getIndex(TokenizerME.NO_SPLIT)).thenReturn(0);

    TokenContextGenerator contextGen = mock(TokenContextGenerator.class);
    when(contextGen.getContext(eq("TestToken123"), anyInt())).thenReturn(new String[]{"ctx"});
    
    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(contextGen);
    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[A-Za-z0-9]+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel, null);

    Span[] spans = tokenizer.tokenizePos("TestToken123");

    assertEquals(1, spans.length);
    assertEquals("TestToken123", "TestToken123".substring(spans[0].getStart(), spans[0].getEnd()));
  }
@Test
  public void testTokenizePos_NonMatchingAlphanumeric_OptimizationEnabled() {
    MaxentModel model = mock(MaxentModel.class);
    double[] probs = new double[]{0.6};
    when(model.eval(any(Object[].class))).thenReturn(probs);
    when(model.getBestOutcome(probs)).thenReturn(TokenizerME.NO_SPLIT);
    when(model.getIndex(TokenizerME.NO_SPLIT)).thenReturn(0);

    TokenContextGenerator contextGen = mock(TokenContextGenerator.class);
    when(contextGen.getContext(eq("abc$def"), anyInt())).thenReturn(new String[]{"ctx"});

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(contextGen);
    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[A-Za-z0-9]+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel, null);

    Span[] spans = tokenizer.tokenizePos("abc$def");

    assertTrue(spans.length >= 1);
  }
@Test
  public void testTokenizePos_MultipleModelSplitsWithoutAbbreviation() {
    MaxentModel model = mock(MaxentModel.class);
    double[] probs = new double[]{0.95};
    when(model.eval(any(Object[].class))).thenReturn(probs);
    when(model.getBestOutcome(probs)).thenReturn(TokenizerME.SPLIT);
    when(model.getIndex(TokenizerME.SPLIT)).thenReturn(0);

    TokenContextGenerator contextGen = mock(TokenContextGenerator.class);
    when(contextGen.getContext(eq("ab1c2#d$"), anyInt())).thenReturn(new String[]{"ctx"});

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(contextGen);
    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[\\w]+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel, null);

    Span[] spans = tokenizer.tokenizePos("ab1c2#d$");

    assertTrue(spans.length > 1);
  }
@Test
  public void testTokenizePos_AllModelProbabilitiesAreZero() {
    MaxentModel model = mock(MaxentModel.class);
    double[] zeroProbs = new double[]{0.0, 0.0};
    when(model.eval(any(Object[].class))).thenReturn(zeroProbs);
    when(model.getBestOutcome(zeroProbs)).thenReturn(TokenizerME.NO_SPLIT);
    when(model.getIndex(TokenizerME.NO_SPLIT)).thenReturn(1);

    TokenContextGenerator contextGen = mock(TokenContextGenerator.class);
    when(contextGen.getContext(eq("00"), anyInt())).thenReturn(new String[]{"z"});

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(contextGen);
    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[0-9]+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel, null);

    Span[] spans = tokenizer.tokenizePos("00");

    assertEquals(1, spans.length);
    assertEquals("00", "00".substring(spans[0].getStart(), spans[0].getEnd()));
  }
@Test
  public void testTokenizePos_AbbreviationWithMoreThanOneDot_ModelSplitIgnorance() {
    Dictionary dict = new Dictionary();
    dict.put(new StringList("A.B."));

    MaxentModel model = mock(MaxentModel.class);
    double[] probs = new double[]{0.85};
    when(model.eval(any(Object[].class))).thenReturn(probs);
    when(model.getBestOutcome(probs)).thenReturn(TokenizerME.SPLIT);
    when(model.getIndex(TokenizerME.SPLIT)).thenReturn(0);

    TokenContextGenerator contextGen = mock(TokenContextGenerator.class);
    when(contextGen.getContext(eq("A.B."), anyInt())).thenReturn(new String[]{"abbrev"});

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(contextGen);
    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[A-Z\\.]+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel, dict);

    Span[] spans = tokenizer.tokenizePos("A.B.");

    assertEquals(1, spans.length);
    assertEquals("A.B.", "A.B.".substring(spans[0].getStart(), spans[0].getEnd()));
  }
@Test
  public void testIsAcceptableAbbreviation_ReturnsFalseForUnknown() {
    Dictionary dict = new Dictionary();
    dict.put(new StringList("U.S."));

    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator contextGen = mock(TokenContextGenerator.class);
    TokenizerFactory factory = mock(TokenizerFactory.class);

    when(factory.getContextGenerator()).thenReturn(contextGen);
    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile(".+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel, dict);

    boolean result = tokenizer.isAcceptableAbbreviation("XYZ.");
    assertFalse(result);
  }
@Test
  public void testTokenizePos_OnlyNewlineCharacter() {
    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator contextGen = mock(TokenContextGenerator.class);
    TokenizerFactory factory = mock(TokenizerFactory.class);

    when(factory.getContextGenerator()).thenReturn(contextGen);
    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile(".+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel, null);

    Span[] spans = tokenizer.tokenizePos("\n");

    assertEquals(1, spans.length);
    assertEquals("\n", "\n".substring(spans[0].getStart(), spans[0].getEnd()));
    assertEquals(1.0, tokenizer.getTokenProbabilities()[0], 0.0001);
  }
@Test
  public void testIsAcceptableAbbreviation_WithNullDictionary_ShouldReturnFalse() {
    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator contextGen = mock(TokenContextGenerator.class);
    TokenizerFactory factory = mock(TokenizerFactory.class);

    when(factory.getContextGenerator()).thenReturn(contextGen);
    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile(".+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel, null);

    assertFalse(tokenizer.isAcceptableAbbreviation("Dr."));
  }
@Test
  public void testTokenizePos_AbbreviationInsideToken_SimulateManualModelSplit() {
    Dictionary dictionary = new Dictionary();
    dictionary.put(new StringList("U.S."));

    MaxentModel model = mock(MaxentModel.class);
    double[] splitProb = new double[]{0.95};
    when(model.eval(any(Object[].class))).thenReturn(splitProb);
    when(model.getBestOutcome(splitProb)).thenReturn(TokenizerME.SPLIT);
    when(model.getIndex(TokenizerME.SPLIT)).thenReturn(0);

    TokenContextGenerator contextGen = mock(TokenContextGenerator.class);
    when(contextGen.getContext(eq("U.S.test"), anyInt())).thenReturn(new String[]{"abbrev"});

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(contextGen);
    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[a-zA-Z.]+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel, dictionary);

    Span[] spans = tokenizer.tokenizePos("U.S.test");

    assertTrue(spans.length >= 2);
    assertEquals("U.S.", "U.S.test".substring(spans[0].getStart(), spans[0].getEnd()));
    assertEquals("test", "U.S.test".substring(spans[1].getStart(), spans[1].getEnd()));
  }
@Test
  public void testTokenizePos_EmptyString_ClearPreviousProbabilities() {
    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator contextGen = mock(TokenContextGenerator.class);
    TokenizerFactory factory = mock(TokenizerFactory.class);

    when(factory.getContextGenerator()).thenReturn(contextGen);
    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[a-zA-Z0-9]+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel, null);

    tokenizer.tokenizePos("NonEmpty");
    assertTrue(tokenizer.getTokenProbabilities().length >= 1);

    Span[] spans = tokenizer.tokenizePos("");
    assertEquals(0, spans.length);
    assertEquals(0, tokenizer.getTokenProbabilities().length);
  }
@Test
  public void testTokenizePos_TokenMatchAlphaPatternButOptimizationDisabled() {
    MaxentModel model = mock(MaxentModel.class);
    double[] probs = new double[]{0.4};
    when(model.eval(any(Object[].class))).thenReturn(probs);
    when(model.getBestOutcome(probs)).thenReturn(TokenizerME.NO_SPLIT);
    when(model.getIndex(TokenizerME.NO_SPLIT)).thenReturn(0);

    TokenContextGenerator contextGen = mock(TokenContextGenerator.class);
    when(contextGen.getContext(eq("Word123"), anyInt())).thenReturn(new String[]{"test"});

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(contextGen);
    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[A-Za-z0-9]+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel, null);
    Span[] spans = tokenizer.tokenizePos("Word123");

    assertEquals(1, spans.length);
    assertEquals("Word123", "Word123".substring(spans[0].getStart(), spans[0].getEnd()));
  }
@Test
  public void testTokenizePos_TokenWithDot_AbbreviationIgnoredDueToOptimizationEnabled() {
    Dictionary dictionary = new Dictionary();
    dictionary.put(new StringList("No."));

    MaxentModel model = mock(MaxentModel.class);
    double[] probs = new double[]{0.7};
    when(model.eval(any(Object[].class))).thenReturn(probs);
    when(model.getBestOutcome(probs)).thenReturn(TokenizerME.SPLIT);
    when(model.getIndex(TokenizerME.SPLIT)).thenReturn(0);

    TokenContextGenerator contextGen = mock(TokenContextGenerator.class);
    when(contextGen.getContext(eq("No."), anyInt())).thenReturn(new String[]{"ctx"});

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(contextGen);
    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[A-Za-z.]+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel, dictionary);

    Span[] spans = tokenizer.tokenizePos("No.");

    assertEquals(1, spans.length);
    assertEquals("No.", "No.".substring(spans[0].getStart(), spans[0].getEnd()));
  }
@Test
  public void testUseAlphaNumericOptimization_ReturnsFalseWhenDisabled() {
    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator contextGen = mock(TokenContextGenerator.class);
    TokenizerFactory factory = mock(TokenizerFactory.class);

    when(factory.getContextGenerator()).thenReturn(contextGen);
    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile(".+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel, null);

    assertFalse(tokenizer.useAlphaNumericOptimization());
  } 
}