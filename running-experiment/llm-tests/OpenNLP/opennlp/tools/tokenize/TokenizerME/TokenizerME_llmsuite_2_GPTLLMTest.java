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

public class TokenizerME_llmsuite_2_GPTLLMTest {

 @Test
  public void testConstructorWithModelInitialization() {
    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator cg = mock(TokenContextGenerator.class);
    Dictionary dict = mock(Dictionary.class);
    Pattern pattern = Pattern.compile("[a-zA-Z0-9]+");

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(cg);
    when(factory.getAlphaNumericPattern()).thenReturn(pattern);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

    TokenizerModel tokenizerModel = mock(TokenizerModel.class);
    when(tokenizerModel.getMaxentModel()).thenReturn(model);
    when(tokenizerModel.getAbbreviations()).thenReturn(dict);
    when(tokenizerModel.getFactory()).thenReturn(factory);

    TokenizerME tokenizer = new TokenizerME(tokenizerModel);
    assertTrue(tokenizer.useAlphaNumericOptimization());
  }
@Test
  public void testSingleCharacterReturnsAsIs() {
    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator cg = mock(TokenContextGenerator.class);
    Pattern pattern = Pattern.compile("[a-zA-Z0-9]+");

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(cg);
    when(factory.getAlphaNumericPattern()).thenReturn(pattern);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

    TokenizerModel tokenizerModel = mock(TokenizerModel.class);
    when(tokenizerModel.getMaxentModel()).thenReturn(model);
    when(tokenizerModel.getAbbreviations()).thenReturn(null);
    when(tokenizerModel.getFactory()).thenReturn(factory);

    TokenizerME tokenizer = new TokenizerME(tokenizerModel);
    Span[] spans = tokenizer.tokenizePos("x");

    assertEquals(1, spans.length);
    assertEquals(0, spans[0].getStart());
    assertEquals(1, spans[0].getEnd());

    double[] probs = tokenizer.getTokenProbabilities();
    assertEquals(1, probs.length);
    assertEquals(1.0, probs[0], 0.0001);
  }
@Test
  public void testAlphaNumericOptimizationBypassModel() {
    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator cg = mock(TokenContextGenerator.class);
    Pattern pattern = Pattern.compile("[a-zA-Z0-9]+");

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(cg);
    when(factory.getAlphaNumericPattern()).thenReturn(pattern);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

    TokenizerModel tokenizerModel = mock(TokenizerModel.class);
    when(tokenizerModel.getMaxentModel()).thenReturn(model);
    when(tokenizerModel.getAbbreviations()).thenReturn(null);
    when(tokenizerModel.getFactory()).thenReturn(factory);

    TokenizerME tokenizer = new TokenizerME(tokenizerModel);

    String text = "hello42";
    Span[] spans = tokenizer.tokenizePos(text);
    assertEquals(1, spans.length);
    assertEquals(0, spans[0].getStart());
    assertEquals(7, spans[0].getEnd());

    double[] probs = tokenizer.getTokenProbabilities();
    assertEquals(1, probs.length);
    assertEquals(1.0, probs[0], 0.0001);
  }
@Test
  public void testTokenSplitByModelOutcome() {
    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator cg = mock(TokenContextGenerator.class);
    Pattern pattern = Pattern.compile("[a-zA-Z]+");

    when(cg.getContext("abcde", 1)).thenReturn(new String[]{"ctx1"});
    when(cg.getContext("abcde", 2)).thenReturn(new String[]{"ctx2"});
    when(cg.getContext("abcde", 3)).thenReturn(new String[]{"ctx3"});
    when(cg.getContext("abcde", 4)).thenReturn(new String[]{"ctx4"});

    when(model.eval(any(String[].class))).thenReturn(new double[]{0.3, 0.7});
    when(model.getBestOutcome(any(double[].class))).thenReturn(TokenizerME.SPLIT);
    when(model.getIndex(TokenizerME.SPLIT)).thenReturn(1);

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(cg);
    when(factory.getAlphaNumericPattern()).thenReturn(pattern);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

    TokenizerModel tokenizerModel = mock(TokenizerModel.class);
    when(tokenizerModel.getMaxentModel()).thenReturn(model);
    when(tokenizerModel.getAbbreviations()).thenReturn(null);
    when(tokenizerModel.getFactory()).thenReturn(factory);

    TokenizerME tokenizer = new TokenizerME(tokenizerModel);

    String text = "abcde";
    Span[] spans = tokenizer.tokenizePos(text);
    assertTrue(spans.length >= 1);
  }
@Test
  public void testKnownAbbreviationStopsSplit() {
    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator cg = mock(TokenContextGenerator.class);
    Dictionary dict = mock(Dictionary.class);
    Pattern pattern = Pattern.compile("[a-zA-Z]+");

    when(dict.contains(new StringList("U.S.A."))).thenReturn(true);

    when(cg.getContext("U.S.A.", 1)).thenReturn(new String[]{"ctx1"});
    when(model.eval(any(String[].class))).thenReturn(new double[]{0.1, 0.9});
    when(model.getBestOutcome(any(double[].class))).thenReturn(TokenizerME.SPLIT);
    when(model.getIndex(TokenizerME.SPLIT)).thenReturn(1);

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(cg);
    when(factory.getAlphaNumericPattern()).thenReturn(pattern);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

    TokenizerModel tokenizerModel = mock(TokenizerModel.class);
    when(tokenizerModel.getMaxentModel()).thenReturn(model);
    when(tokenizerModel.getAbbreviations()).thenReturn(dict);
    when(tokenizerModel.getFactory()).thenReturn(factory);

    TokenizerME tokenizer = new TokenizerME(tokenizerModel);

    String text = "U.S.A.";
    Span[] spans = tokenizer.tokenizePos(text);
    assertEquals(1, spans.length);
    assertEquals(0, spans[0].getStart());
    assertEquals(7, spans[0].getEnd());
  }
@Test
  public void testEmptyInputReturnsNoTokens() {
    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator cg = mock(TokenContextGenerator.class);
    Pattern pattern = Pattern.compile("[a-zA-Z]+");

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(cg);
    when(factory.getAlphaNumericPattern()).thenReturn(pattern);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

    TokenizerModel tokenizerModel = mock(TokenizerModel.class);
    when(tokenizerModel.getMaxentModel()).thenReturn(model);
    when(tokenizerModel.getAbbreviations()).thenReturn(null);
    when(tokenizerModel.getFactory()).thenReturn(factory);

    TokenizerME tokenizer = new TokenizerME(tokenizerModel);

    Span[] spans = tokenizer.tokenizePos("");
    assertEquals(0, spans.length);
    assertEquals(0, tokenizer.getTokenProbabilities().length);
  }
@Test
  public void testUseAlphaNumericOptimizationIsReturnedProperly() {
    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator cg = mock(TokenContextGenerator.class);
    Pattern pattern = Pattern.compile("[a-zA-Z]+");

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(cg);
    when(factory.getAlphaNumericPattern()).thenReturn(pattern);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

    TokenizerModel tokenizerModel = mock(TokenizerModel.class);
    when(tokenizerModel.getMaxentModel()).thenReturn(model);
    when(tokenizerModel.getAbbreviations()).thenReturn(null);
    when(tokenizerModel.getFactory()).thenReturn(factory);

    TokenizerME tokenizer = new TokenizerME(tokenizerModel);
    assertTrue(tokenizer.useAlphaNumericOptimization());
  }
@Test
  public void testTrainReturnsTokenizerModel() throws IOException {
    @SuppressWarnings("unchecked")
    ObjectStream<TokenSample> sampleStream = mock(ObjectStream.class);
    TokenContextGenerator cg = mock(TokenContextGenerator.class);
    Pattern pattern = Pattern.compile("[a-zA-Z]+");

    TrainingParameters params = new TrainingParameters();
    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(cg);
    when(factory.getAlphaNumericPattern()).thenReturn(pattern);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

    EventTrainer trainer = mock(EventTrainer.class);
    MaxentModel maxentModel = mock(MaxentModel.class);
    when(trainer.train(any())).thenReturn(maxentModel);

    TokenizerModel model = TokenizerME.train(sampleStream, factory, params);
    assertNotNull(model);
  }
@Test
  public void testMultipleSpaceSeparatedTokens() {
    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator cg = mock(TokenContextGenerator.class);
    Pattern pattern = Pattern.compile("[a-zA-Z]+");

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(cg);
    when(factory.getAlphaNumericPattern()).thenReturn(pattern);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

    TokenizerModel tokenizerModel = mock(TokenizerModel.class);
    when(tokenizerModel.getMaxentModel()).thenReturn(model);
    when(tokenizerModel.getAbbreviations()).thenReturn(null);
    when(tokenizerModel.getFactory()).thenReturn(factory);

    TokenizerME tokenizer = new TokenizerME(tokenizerModel);
    String text = "New York City";

    Span[] spans = tokenizer.tokenizePos(text);
    assertEquals(3, spans.length);

    assertEquals("New", text.substring(spans[0].getStart(), spans[0].getEnd()));
    assertEquals("York", text.substring(spans[1].getStart(), spans[1].getEnd()));
    assertEquals("City", text.substring(spans[2].getStart(), spans[2].getEnd()));
  }
@Test
public void testAbbreviationDictionaryIsNull_shouldReturnFalseFromIsAcceptableAbbreviation() {
  MaxentModel model = mock(MaxentModel.class);
  TokenContextGenerator cg = mock(TokenContextGenerator.class);
  Pattern pattern = Pattern.compile("[a-zA-Z]+");

  TokenizerFactory factory = mock(TokenizerFactory.class);
  when(factory.getContextGenerator()).thenReturn(cg);
  when(factory.getAlphaNumericPattern()).thenReturn(pattern);
  when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

  TokenizerModel tokenizerModel = mock(TokenizerModel.class);
  when(tokenizerModel.getMaxentModel()).thenReturn(model);
  when(tokenizerModel.getAbbreviations()).thenReturn(null);
  when(tokenizerModel.getFactory()).thenReturn(factory);

  TokenizerME tokenizer = new TokenizerME(tokenizerModel);
  assertFalse(tokenizer.isAcceptableAbbreviation("Inc."));
}
@Test
public void testTokenWithOnlyPunctuation_ShouldNotMatchAlphanumeric_shouldTriggerModelCall() {
  MaxentModel model = mock(MaxentModel.class);
  TokenContextGenerator cg = mock(TokenContextGenerator.class);
  Pattern pattern = Pattern.compile("[a-zA-Z0-9]+");

  when(cg.getContext("!!!", 1)).thenReturn(new String[]{"ctx1"});
  when(model.eval(any(String[].class))).thenReturn(new double[]{0.6, 0.4});
  when(model.getBestOutcome(any(double[].class))).thenReturn(TokenizerME.NO_SPLIT);
  when(model.getIndex(TokenizerME.NO_SPLIT)).thenReturn(0);

  TokenizerFactory factory = mock(TokenizerFactory.class);
  when(factory.getContextGenerator()).thenReturn(cg);
  when(factory.getAlphaNumericPattern()).thenReturn(pattern);
  when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

  TokenizerModel tokenizerModel = mock(TokenizerModel.class);
  when(tokenizerModel.getMaxentModel()).thenReturn(model);
  when(tokenizerModel.getAbbreviations()).thenReturn(null);
  when(tokenizerModel.getFactory()).thenReturn(factory);

  TokenizerME tokenizer = new TokenizerME(tokenizerModel);
  Span[] spans = tokenizer.tokenizePos("!!!");

  assertEquals(1, spans.length);
  assertEquals(0, spans[0].getStart());
  assertEquals(3, spans[0].getEnd());

  double[] probs = tokenizer.getTokenProbabilities();
  assertEquals(1, probs.length);
  assertTrue(probs[0] > 0.0 && probs[0] <= 1.0);
}
@Test
public void testTokenWithMultipleSplitPoints_shouldProduceMultipleSpans() {
  MaxentModel model = mock(MaxentModel.class);
  TokenContextGenerator cg = mock(TokenContextGenerator.class);
  Pattern pattern = Pattern.compile("[a-zA-Z0-9]+");

  when(cg.getContext("abcdef", 1)).thenReturn(new String[]{"ctx1"});
  when(cg.getContext("abcdef", 2)).thenReturn(new String[]{"ctx2"});
  when(cg.getContext("abcdef", 3)).thenReturn(new String[]{"ctx3"});
  when(cg.getContext("abcdef", 4)).thenReturn(new String[]{"ctx4"});
  when(cg.getContext("abcdef", 5)).thenReturn(new String[]{"ctx5"});

  when(model.eval(any(String[].class))).thenReturn(new double[]{0.3, 0.7});
  when(model.getBestOutcome(any(double[].class))).thenReturn(TokenizerME.SPLIT);
  when(model.getIndex(TokenizerME.SPLIT)).thenReturn(1);

  TokenizerFactory factory = mock(TokenizerFactory.class);
  when(factory.getContextGenerator()).thenReturn(cg);
  when(factory.getAlphaNumericPattern()).thenReturn(pattern);
  when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

  TokenizerModel tokenizerModel = mock(TokenizerModel.class);
  when(tokenizerModel.getMaxentModel()).thenReturn(model);
  when(tokenizerModel.getAbbreviations()).thenReturn(null);
  when(tokenizerModel.getFactory()).thenReturn(factory);

  TokenizerME tokenizer = new TokenizerME(tokenizerModel);
  Span[] spans = tokenizer.tokenizePos("abcdef");

  assertTrue(spans.length >= 2);
  assertTrue(tokenizer.getTokenProbabilities().length >= 2);
}
@Test
public void testWhitespaceAndNewlines_handledByWhitespaceTokenizer() {
  MaxentModel model = mock(MaxentModel.class);
  TokenContextGenerator cg = mock(TokenContextGenerator.class);
  Pattern pattern = Pattern.compile("[a-zA-Z0-9]+");

  TokenizerFactory factory = mock(TokenizerFactory.class);
  when(factory.getContextGenerator()).thenReturn(cg);
  when(factory.getAlphaNumericPattern()).thenReturn(pattern);
  when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

  TokenizerModel tokenizerModel = mock(TokenizerModel.class);
  when(tokenizerModel.getMaxentModel()).thenReturn(model);
  when(tokenizerModel.getAbbreviations()).thenReturn(null);
  when(tokenizerModel.getFactory()).thenReturn(factory);

  TokenizerME tokenizer = new TokenizerME(tokenizerModel);

  Span[] spans = tokenizer.tokenizePos("Line1\nLine2\tLine3 Line4");
  assertEquals(4, spans.length);
  assertEquals("Line1", "Line1\nLine2\tLine3 Line4".substring(spans[0].getStart(), spans[0].getEnd()));
  assertEquals("Line2", "Line1\nLine2\tLine3 Line4".substring(spans[1].getStart(), spans[1].getEnd()));
  assertEquals("Line3", "Line1\nLine2\tLine3 Line4".substring(spans[2].getStart(), spans[2].getEnd()));
  assertEquals("Line4", "Line1\nLine2\tLine3 Line4".substring(spans[3].getStart(), spans[3].getEnd()));
}
@Test
public void testTokenProbabilitiesClearedBetweenCalls() {
  MaxentModel model = mock(MaxentModel.class);
  TokenContextGenerator cg = mock(TokenContextGenerator.class);
  Pattern pattern = Pattern.compile("[a-zA-Z]+");

  TokenizerFactory factory = mock(TokenizerFactory.class);
  when(factory.getContextGenerator()).thenReturn(cg);
  when(factory.getAlphaNumericPattern()).thenReturn(pattern);
  when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

  TokenizerModel tokenizerModel = mock(TokenizerModel.class);
  when(tokenizerModel.getMaxentModel()).thenReturn(model);
  when(tokenizerModel.getAbbreviations()).thenReturn(null);
  when(tokenizerModel.getFactory()).thenReturn(factory);

  TokenizerME tokenizer = new TokenizerME(tokenizerModel);

  Span[] spans1 = tokenizer.tokenizePos("abc");
  double[] probs1 = tokenizer.getTokenProbabilities();

  Span[] spans2 = tokenizer.tokenizePos("xyz");
  double[] probs2 = tokenizer.getTokenProbabilities();

  assertEquals(spans2.length, probs2.length);
  assertNotEquals(probs1.length, probs2.length); 
}
@Test
public void testTokenEndingWithSplit_shouldNotAddEmptySpan() {
  MaxentModel model = mock(MaxentModel.class);
  TokenContextGenerator cg = mock(TokenContextGenerator.class);
  Pattern pattern = Pattern.compile("[a-zA-Z]+");

  when(cg.getContext("abcd", 1)).thenReturn(new String[]{"ctx1"});
  when(cg.getContext("abcd", 2)).thenReturn(new String[]{"ctx2"});
  when(cg.getContext("abcd", 3)).thenReturn(new String[]{"ctx3"});

  when(model.eval(any(String[].class))).thenReturn(new double[]{0.1, 0.9});
  when(model.getBestOutcome(any(double[].class))).thenReturn(TokenizerME.SPLIT);
  when(model.getIndex(TokenizerME.SPLIT)).thenReturn(1);

  TokenizerFactory factory = mock(TokenizerFactory.class);
  when(factory.getContextGenerator()).thenReturn(cg);
  when(factory.getAlphaNumericPattern()).thenReturn(pattern);
  when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

  TokenizerModel tokenizerModel = mock(TokenizerModel.class);
  when(tokenizerModel.getMaxentModel()).thenReturn(model);
  when(tokenizerModel.getAbbreviations()).thenReturn(null);
  when(tokenizerModel.getFactory()).thenReturn(factory);

  TokenizerME tokenizer = new TokenizerME(tokenizerModel);
  Span[] spans = tokenizer.tokenizePos("abcd");

  
  for (Span span : spans) {
    assertTrue(span.length() > 0);
  }
}
@Test
public void testWhitespaceOnlyInput_shouldReturnEmptySpansAndProbabilities() {
  MaxentModel model = mock(MaxentModel.class);
  TokenContextGenerator cg = mock(TokenContextGenerator.class);
  Pattern pattern = Pattern.compile("[a-zA-Z]+");

  TokenizerFactory factory = mock(TokenizerFactory.class);
  when(factory.getContextGenerator()).thenReturn(cg);
  when(factory.getAlphaNumericPattern()).thenReturn(pattern);
  when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

  TokenizerModel tokenizerModel = mock(TokenizerModel.class);
  when(tokenizerModel.getMaxentModel()).thenReturn(model);
  when(tokenizerModel.getAbbreviations()).thenReturn(null);
  when(tokenizerModel.getFactory()).thenReturn(factory);

  TokenizerME tokenizer = new TokenizerME(tokenizerModel);
  Span[] spans = tokenizer.tokenizePos("   ");
  double[] probs = tokenizer.getTokenProbabilities();

  assertEquals(0, spans.length);
  assertEquals(0, probs.length);
}
@Test
public void testTokenWithSplitAndIntermediateAbbreviationDot_handledCorrectly() {
  MaxentModel model = mock(MaxentModel.class);
  TokenContextGenerator cg = mock(TokenContextGenerator.class);
  Dictionary abbreviationDict = mock(Dictionary.class);
  Pattern pattern = Pattern.compile("[a-zA-Z]+");

  when(cg.getContext("e.g.", 1)).thenReturn(new String[]{"ctx1"});
  when(cg.getContext("e.g.", 2)).thenReturn(new String[]{"ctx2"});

  when(model.eval(any(String[].class))).thenReturn(new double[]{0.1, 0.9});
  when(model.getBestOutcome(any(double[].class))).thenReturn(TokenizerME.SPLIT);
  when(model.getIndex(TokenizerME.SPLIT)).thenReturn(1);

  when(abbreviationDict.contains(new StringList("e.g."))).thenReturn(true);

  TokenizerFactory factory = mock(TokenizerFactory.class);
  when(factory.getContextGenerator()).thenReturn(cg);
  when(factory.getAlphaNumericPattern()).thenReturn(pattern);
  when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

  TokenizerModel tokenizerModel = mock(TokenizerModel.class);
  when(tokenizerModel.getMaxentModel()).thenReturn(model);
  when(tokenizerModel.getAbbreviations()).thenReturn(abbreviationDict);
  when(tokenizerModel.getFactory()).thenReturn(factory);

  TokenizerME tokenizer = new TokenizerME(tokenizerModel);
  Span[] spans = tokenizer.tokenizePos("e.g.");

  assertEquals(1, spans.length);
  assertEquals("e.g.", "e.g.".substring(spans[0].getStart(), spans[0].getEnd()));

  double[] probs = tokenizer.getTokenProbabilities();
  assertEquals(1, probs.length);
  assertTrue(probs[0] > 0.0);
}
@Test
public void testTokenSampleTrainingIOException_handledGracefully() {
  ObjectStream<TokenSample> sampleStream = new ObjectStream<TokenSample>() {
    @Override
    public TokenSample read() throws IOException {
      throw new IOException("Simulated I/O error");
    }

    @Override
    public void reset() throws IOException {}

    @Override
    public void close() throws IOException {}
  };

  TokenContextGenerator cg = mock(TokenContextGenerator.class);
  Pattern pattern = Pattern.compile("[a-zA-Z]+");

  TokenizerFactory factory = mock(TokenizerFactory.class);
  when(factory.getContextGenerator()).thenReturn(cg);
  when(factory.getAlphaNumericPattern()).thenReturn(pattern);
  when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

  TrainingParameters params = new TrainingParameters();

  try {
    TokenizerME.train(sampleStream, factory, params);
    fail("Expected IOException during training");
  } catch (IOException e) {
    assertEquals("Simulated I/O error", e.getMessage());
  }
}
@Test
public void testAbbreviationWithMultipleDots_shouldAdjustSpanCorrectly() {
  MaxentModel model = mock(MaxentModel.class);
  TokenContextGenerator cg = mock(TokenContextGenerator.class);
  Dictionary abbreviationDict = mock(Dictionary.class);
  Pattern pattern = Pattern.compile("[a-zA-Z.]+");

  when(abbreviationDict.contains(new StringList("U.S.A."))).thenReturn(true);

  when(cg.getContext("U.S.A.", 1)).thenReturn(new String[]{"ctx1"});
  when(model.eval(any(String[].class))).thenReturn(new double[]{0.2, 0.8});
  when(model.getBestOutcome(any(double[].class))).thenReturn(TokenizerME.SPLIT);
  when(model.getIndex(TokenizerME.SPLIT)).thenReturn(1);

  TokenizerFactory factory = mock(TokenizerFactory.class);
  when(factory.getContextGenerator()).thenReturn(cg);
  when(factory.getAlphaNumericPattern()).thenReturn(pattern);
  when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

  TokenizerModel tokenizerModel = mock(TokenizerModel.class);
  when(tokenizerModel.getMaxentModel()).thenReturn(model);
  when(tokenizerModel.getAbbreviations()).thenReturn(abbreviationDict);
  when(tokenizerModel.getFactory()).thenReturn(factory);

  TokenizerME tokenizer = new TokenizerME(tokenizerModel);
  Span[] spans = tokenizer.tokenizePos("U.S.A.");

  assertEquals(1, spans.length);
  assertEquals("U.S.A.", "U.S.A.".substring(spans[0].getStart(), spans[0].getEnd()));

  double[] probs = tokenizer.getTokenProbabilities();
  assertEquals(1, probs.length);
  assertTrue(probs[0] > 0.0);
}
@Test
public void testNonAlphanumericSingleChar_shouldReturnWithProbOne() {
  MaxentModel model = mock(MaxentModel.class);
  TokenContextGenerator cg = mock(TokenContextGenerator.class);
  Pattern alphaPattern = Pattern.compile("[a-zA-Z]+");

  TokenizerFactory factory = mock(TokenizerFactory.class);
  when(factory.getContextGenerator()).thenReturn(cg);
  when(factory.getAlphaNumericPattern()).thenReturn(alphaPattern);
  when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

  TokenizerModel tokenizerModel = mock(TokenizerModel.class);
  when(tokenizerModel.getMaxentModel()).thenReturn(model);
  when(tokenizerModel.getAbbreviations()).thenReturn(null);
  when(tokenizerModel.getFactory()).thenReturn(factory);

  TokenizerME tokenizer = new TokenizerME(tokenizerModel);
  Span[] spans = tokenizer.tokenizePos("!");

  assertEquals(1, spans.length);
  assertEquals(0, spans[0].getStart());
  assertEquals(1, spans[0].getEnd());
  double[] probs = tokenizer.getTokenProbabilities();
  assertEquals(1.0, probs[0], 0.0001);
}
@Test
public void testInputWithLeadingTrailingWhitespace_shouldIgnoreSpaces() {
  MaxentModel model = mock(MaxentModel.class);
  TokenContextGenerator cg = mock(TokenContextGenerator.class);
  Pattern alphaPattern = Pattern.compile("[a-zA-Z]+");

  TokenizerFactory factory = mock(TokenizerFactory.class);
  when(factory.getContextGenerator()).thenReturn(cg);
  when(factory.getAlphaNumericPattern()).thenReturn(alphaPattern);
  when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

  TokenizerModel tokenizerModel = mock(TokenizerModel.class);
  when(tokenizerModel.getMaxentModel()).thenReturn(model);
  when(tokenizerModel.getAbbreviations()).thenReturn(null);
  when(tokenizerModel.getFactory()).thenReturn(factory);

  TokenizerME tokenizer = new TokenizerME(tokenizerModel);

  Span[] spans = tokenizer.tokenizePos("  hello   ");
  assertEquals(1, spans.length);
  assertEquals("hello", "  hello   ".substring(spans[0].getStart(), spans[0].getEnd()));
  double[] probs = tokenizer.getTokenProbabilities();
  assertEquals(1.0, probs[0], 0.0001);
}
@Test
public void testTokenWithNoSplitOutcome_shouldRemainSingleSpan() {
  MaxentModel model = mock(MaxentModel.class);
  TokenContextGenerator cg = mock(TokenContextGenerator.class);
  Pattern alphaPattern = Pattern.compile("[a-zA-Z]+");

  when(cg.getContext("block", 1)).thenReturn(new String[]{"ctx1"});
  when(cg.getContext("block", 2)).thenReturn(new String[]{"ctx2"});
  when(cg.getContext("block", 3)).thenReturn(new String[]{"ctx3"});
  when(cg.getContext("block", 4)).thenReturn(new String[]{"ctx4"});

  when(model.eval(any(String[].class))).thenReturn(new double[]{0.9, 0.1});
  when(model.getBestOutcome(any(double[].class))).thenReturn(TokenizerME.NO_SPLIT);
  when(model.getIndex(TokenizerME.NO_SPLIT)).thenReturn(0);

  TokenizerFactory factory = mock(TokenizerFactory.class);
  when(factory.getContextGenerator()).thenReturn(cg);
  when(factory.getAlphaNumericPattern()).thenReturn(alphaPattern);
  when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

  TokenizerModel tokenizerModel = mock(TokenizerModel.class);
  when(tokenizerModel.getMaxentModel()).thenReturn(model);
  when(tokenizerModel.getAbbreviations()).thenReturn(null);
  when(tokenizerModel.getFactory()).thenReturn(factory);

  TokenizerME tokenizer = new TokenizerME(tokenizerModel);

  Span[] spans = tokenizer.tokenizePos("block");

  assertEquals(1, spans.length);
  assertEquals("block", "block".substring(spans[0].getStart(), spans[0].getEnd()));
  double[] probs = tokenizer.getTokenProbabilities();
  assertEquals(1, probs.length);
  assertTrue(probs[0] > 0.0 && probs[0] <= 1.0);
}
@Test
public void testSplitFollowedImmediatelyByNoSplit_shouldCreateCorrectSpans() {
  MaxentModel model = mock(MaxentModel.class);
  TokenContextGenerator cg = mock(TokenContextGenerator.class);
  Pattern alphaPattern = Pattern.compile("[a-zA-Z]+");

  when(cg.getContext("tokenize", 1)).thenReturn(new String[]{"ctx1"});
  when(cg.getContext("tokenize", 2)).thenReturn(new String[]{"ctx2"});
  when(cg.getContext("tokenize", 3)).thenReturn(new String[]{"ctx3"});
  when(cg.getContext("tokenize", 4)).thenReturn(new String[]{"ctx4"});
  when(cg.getContext("tokenize", 5)).thenReturn(new String[]{"ctx5"});
  when(cg.getContext("tokenize", 6)).thenReturn(new String[]{"ctx6"});
  when(cg.getContext("tokenize", 7)).thenReturn(new String[]{"ctx7"});

  when(model.eval(any(String[].class)))
      .thenReturn(new double[]{0.3, 0.7}) 
      .thenReturn(new double[]{0.8, 0.2}) 
      .thenReturn(new double[]{0.2, 0.8}) 
      .thenReturn(new double[]{0.7, 0.3}) 
      .thenReturn(new double[]{0.4, 0.6}) 
      .thenReturn(new double[]{0.9, 0.1}); 

  when(model.getBestOutcome(new double[]{0.3, 0.7})).thenReturn(TokenizerME.SPLIT);
  when(model.getBestOutcome(new double[]{0.8, 0.2})).thenReturn(TokenizerME.NO_SPLIT);
  when(model.getBestOutcome(new double[]{0.2, 0.8})).thenReturn(TokenizerME.SPLIT);
  when(model.getBestOutcome(new double[]{0.7, 0.3})).thenReturn(TokenizerME.NO_SPLIT);
  when(model.getBestOutcome(new double[]{0.4, 0.6})).thenReturn(TokenizerME.SPLIT);
  when(model.getBestOutcome(new double[]{0.9, 0.1})).thenReturn(TokenizerME.NO_SPLIT);

  when(model.getIndex(TokenizerME.SPLIT)).thenReturn(1);
  when(model.getIndex(TokenizerME.NO_SPLIT)).thenReturn(0);

  TokenizerFactory factory = mock(TokenizerFactory.class);
  when(factory.getContextGenerator()).thenReturn(cg);
  when(factory.getAlphaNumericPattern()).thenReturn(alphaPattern);
  when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

  TokenizerModel tokenizerModel = mock(TokenizerModel.class);
  when(tokenizerModel.getMaxentModel()).thenReturn(model);
  when(tokenizerModel.getAbbreviations()).thenReturn(null);
  when(tokenizerModel.getFactory()).thenReturn(factory);

  TokenizerME tokenizer = new TokenizerME(tokenizerModel);

  Span[] spans = tokenizer.tokenizePos("tokenize");

  assertTrue(spans.length >= 2);
  for (Span span : spans) {
    assertTrue(span.length() > 0);
    String token = "tokenize".substring(span.getStart(), span.getEnd());
    assertFalse(token.trim().isEmpty());
  }
}
@Test
public void testTokenizeCalledTwice_shouldResetIntermediateState() {
  MaxentModel model = mock(MaxentModel.class);
  TokenContextGenerator cg = mock(TokenContextGenerator.class);
  Pattern pattern = Pattern.compile("[a-zA-Z]+");

  TokenizerFactory factory = mock(TokenizerFactory.class);
  when(factory.getContextGenerator()).thenReturn(cg);
  when(factory.getAlphaNumericPattern()).thenReturn(pattern);
  when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

  TokenizerModel tokenizerModel = mock(TokenizerModel.class);
  when(tokenizerModel.getMaxentModel()).thenReturn(model);
  when(tokenizerModel.getAbbreviations()).thenReturn(null);
  when(tokenizerModel.getFactory()).thenReturn(factory);

  TokenizerME tokenizer = new TokenizerME(tokenizerModel);

  Span[] spans1 = tokenizer.tokenizePos("one");
  double[] probs1 = tokenizer.getTokenProbabilities();

  Span[] spans2 = tokenizer.tokenizePos("two");
  double[] probs2 = tokenizer.getTokenProbabilities();

  assertEquals(1, spans2.length);
  assertEquals("two", "two".substring(spans2[0].getStart(), spans2[0].getEnd()));
  assertEquals(1, probs2.length);
  assertEquals(1.0, probs2[0], 0.0001);
}
@Test
public void testTokenSpanningMultipleWhitespace_shouldHandleProperly() {
  MaxentModel model = mock(MaxentModel.class);
  TokenContextGenerator cg = mock(TokenContextGenerator.class);
  Pattern alphaPattern = Pattern.compile("[a-zA-Z]+");

  TokenizerFactory factory = mock(TokenizerFactory.class);
  when(factory.getContextGenerator()).thenReturn(cg);
  when(factory.getAlphaNumericPattern()).thenReturn(alphaPattern);
  when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

  TokenizerModel tokenizerModel = mock(TokenizerModel.class);
  when(tokenizerModel.getMaxentModel()).thenReturn(model);
  when(tokenizerModel.getAbbreviations()).thenReturn(null);
  when(tokenizerModel.getFactory()).thenReturn(factory);

  TokenizerME tokenizer = new TokenizerME(tokenizerModel);

  String text = "one   two\tthree\nfour";
  Span[] spans = tokenizer.tokenizePos(text);

  assertEquals(4, spans.length);
  assertEquals("one", text.substring(spans[0].getStart(), spans[0].getEnd()));
  assertEquals("two", text.substring(spans[1].getStart(), spans[1].getEnd()));
  assertEquals("three", text.substring(spans[2].getStart(), spans[2].getEnd()));
  assertEquals("four", text.substring(spans[3].getStart(), spans[3].getEnd()));
}
@Test
public void testSpacesBetweenSingleCharacterTokens_shouldNotSkipSingleChars() {
  MaxentModel model = mock(MaxentModel.class);
  TokenContextGenerator cg = mock(TokenContextGenerator.class);
  Pattern alphaPattern = Pattern.compile("[a-zA-Z]+");

  TokenizerFactory factory = mock(TokenizerFactory.class);
  when(factory.getContextGenerator()).thenReturn(cg);
  when(factory.getAlphaNumericPattern()).thenReturn(alphaPattern);
  when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

  TokenizerModel tokenizerModel = mock(TokenizerModel.class);
  when(tokenizerModel.getMaxentModel()).thenReturn(model);
  when(tokenizerModel.getAbbreviations()).thenReturn(null);
  when(tokenizerModel.getFactory()).thenReturn(factory);

  TokenizerME tokenizer = new TokenizerME(tokenizerModel);

  String text = "a b c";
  Span[] spans = tokenizer.tokenizePos(text);

  assertEquals(3, spans.length);
  assertEquals("a", text.substring(spans[0].getStart(), spans[0].getEnd()));
  assertEquals("b", text.substring(spans[1].getStart(), spans[1].getEnd()));
  assertEquals("c", text.substring(spans[2].getStart(), spans[2].getEnd()));

  double[] probs = tokenizer.getTokenProbabilities();
  assertEquals(3, probs.length);
  assertEquals(1.0, probs[0], 0.0001);
  assertEquals(1.0, probs[1], 0.0001);
  assertEquals(1.0, probs[2], 0.0001);
}
@Test
public void testTokenWithDigitsOnly_shouldUseAlphaNumericOptimization() {
  MaxentModel model = mock(MaxentModel.class);
  TokenContextGenerator cg = mock(TokenContextGenerator.class);
  Pattern alphaPattern = Pattern.compile("[0-9]+");

  TokenizerFactory factory = mock(TokenizerFactory.class);
  when(factory.getContextGenerator()).thenReturn(cg);
  when(factory.getAlphaNumericPattern()).thenReturn(alphaPattern);
  when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

  TokenizerModel tokenizerModel = mock(TokenizerModel.class);
  when(tokenizerModel.getMaxentModel()).thenReturn(model);
  when(tokenizerModel.getAbbreviations()).thenReturn(null);
  when(tokenizerModel.getFactory()).thenReturn(factory);

  TokenizerME tokenizer = new TokenizerME(tokenizerModel);

  Span[] spans = tokenizer.tokenizePos("12345");

  assertEquals(1, spans.length);
  assertEquals("12345", "12345".substring(spans[0].getStart(), spans[0].getEnd()));

  double[] probs = tokenizer.getTokenProbabilities();
  assertEquals(1, probs.length);
  assertEquals(1.0, probs[0], 0.0001);
}
@Test
public void testMultipleTokenizePosCalls_withVaryingLengths_shouldResetTokProbsEachTime() {
  MaxentModel model = mock(MaxentModel.class);
  TokenContextGenerator cg = mock(TokenContextGenerator.class);
  Pattern alphaPattern = Pattern.compile("[a-zA-Z0-9]+");

  TokenizerFactory factory = mock(TokenizerFactory.class);
  when(factory.getContextGenerator()).thenReturn(cg);
  when(factory.getAlphaNumericPattern()).thenReturn(alphaPattern);
  when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

  TokenizerModel tokenizerModel = mock(TokenizerModel.class);
  when(tokenizerModel.getMaxentModel()).thenReturn(model);
  when(tokenizerModel.getAbbreviations()).thenReturn(null);
  when(tokenizerModel.getFactory()).thenReturn(factory);

  TokenizerME tokenizer = new TokenizerME(tokenizerModel);

  Span[] spans1 = tokenizer.tokenizePos("hello world");
  double[] probs1 = tokenizer.getTokenProbabilities();

  assertEquals(2, spans1.length);
  assertEquals(2, probs1.length);

  Span[] spans2 = tokenizer.tokenizePos("hi");
  double[] probs2 = tokenizer.getTokenProbabilities();

  assertEquals(1, spans2.length);
  assertEquals(1, probs2.length);

  assertEquals(1.0, probs2[0], 0.0001);
}
@Test
public void testUseAlphaNumericOptimization_false_shouldApplyModelEvenForAlphaNumericTokens() {
  MaxentModel model = mock(MaxentModel.class);
  TokenContextGenerator cg = mock(TokenContextGenerator.class);
  Pattern alphaPattern = Pattern.compile("[a-zA-Z0-9]+");

  when(cg.getContext("hello123", 1)).thenReturn(new String[]{"ctx1"});
  when(cg.getContext("hello123", 2)).thenReturn(new String[]{"ctx2"});
  when(model.eval(any(String[].class))).thenReturn(new double[]{0.2, 0.8});
  when(model.getBestOutcome(any(double[].class))).thenReturn(TokenizerME.SPLIT);
  when(model.getIndex(TokenizerME.SPLIT)).thenReturn(1);

  TokenizerFactory factory = mock(TokenizerFactory.class);
  when(factory.getContextGenerator()).thenReturn(cg);
  when(factory.getAlphaNumericPattern()).thenReturn(alphaPattern);
  when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

  TokenizerModel tokenizerModel = mock(TokenizerModel.class);
  when(tokenizerModel.getMaxentModel()).thenReturn(model);
  when(tokenizerModel.getAbbreviations()).thenReturn(null);
  when(tokenizerModel.getFactory()).thenReturn(factory);

  TokenizerME tokenizer = new TokenizerME(tokenizerModel);

  Span[] spans = tokenizer.tokenizePos("hello123");

  assertTrue(spans.length >= 1);
  double[] probs = tokenizer.getTokenProbabilities();
  assertEquals(spans.length, probs.length);
}
@Test
public void testFactoryReturnsNullContextGenerator_shouldThrowException() {
  MaxentModel model = mock(MaxentModel.class);
  Pattern pattern = Pattern.compile("[a-zA-Z]+");

  TokenizerFactory factory = mock(TokenizerFactory.class);
  when(factory.getContextGenerator()).thenReturn(null); 
  when(factory.getAlphaNumericPattern()).thenReturn(pattern);
  when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

  TokenizerModel tokenizerModel = mock(TokenizerModel.class);
  when(tokenizerModel.getFactory()).thenReturn(factory);
  when(tokenizerModel.getMaxentModel()).thenReturn(model);
  when(tokenizerModel.getAbbreviations()).thenReturn(null);

  try {
    new TokenizerME(tokenizerModel);
    fail("Expected NullPointerException due to null context generator");
  } catch (NullPointerException e) {
    
  }
}
@Test
public void testAlphaNumericPatternThatNeverMatches_shouldAlwaysEvalModel() {
  MaxentModel model = mock(MaxentModel.class);
  TokenContextGenerator cg = mock(TokenContextGenerator.class);
  Pattern pattern = Pattern.compile("^[^a-zA-Z0-9]+$"); 

  when(cg.getContext("abc123", 1)).thenReturn(new String[]{"ctx1"});
  when(cg.getContext("abc123", 2)).thenReturn(new String[]{"ctx2"});
  when(model.eval(any(String[].class))).thenReturn(new double[]{0.4, 0.6});
  when(model.getBestOutcome(any(double[].class))).thenReturn(TokenizerME.SPLIT);
  when(model.getIndex(TokenizerME.SPLIT)).thenReturn(1);

  TokenizerFactory factory = mock(TokenizerFactory.class);
  when(factory.getContextGenerator()).thenReturn(cg);
  when(factory.getAlphaNumericPattern()).thenReturn(pattern);
  when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

  TokenizerModel tokenizerModel = mock(TokenizerModel.class);
  when(tokenizerModel.getFactory()).thenReturn(factory);
  when(tokenizerModel.getMaxentModel()).thenReturn(model);
  when(tokenizerModel.getAbbreviations()).thenReturn(null);

  TokenizerME tokenizer = new TokenizerME(tokenizerModel);

  Span[] spans = tokenizer.tokenizePos("abc123");

  assertTrue(spans.length > 1);
  double[] probs = tokenizer.getTokenProbabilities();
  assertEquals(spans.length, probs.length);
}
@Test
public void testEmptyTokenAfterSplit_shouldNotAddZeroLengthSpan() {
  MaxentModel model = mock(MaxentModel.class);
  TokenContextGenerator cg = mock(TokenContextGenerator.class);
  Pattern pattern = Pattern.compile("[a-zA-Z]+");

  when(cg.getContext(".", 1)).thenReturn(new String[]{"ctx1"});
  when(model.eval(any(String[].class))).thenReturn(new double[]{0.5, 0.5});
  when(model.getBestOutcome(any(double[].class))).thenReturn(TokenizerME.SPLIT);
  when(model.getIndex(TokenizerME.SPLIT)).thenReturn(1);

  TokenizerFactory factory = mock(TokenizerFactory.class);
  when(factory.getContextGenerator()).thenReturn(cg);
  when(factory.getAlphaNumericPattern()).thenReturn(pattern);
  when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

  TokenizerModel tokenizerModel = mock(TokenizerModel.class);
  when(tokenizerModel.getFactory()).thenReturn(factory);
  when(tokenizerModel.getMaxentModel()).thenReturn(model);
  when(tokenizerModel.getAbbreviations()).thenReturn(null);

  TokenizerME tokenizer = new TokenizerME(tokenizerModel);
  Span[] spans = tokenizer.tokenizePos(".");

  for (Span span : spans) {
    assertTrue(span.length() > 0);
  }
}
@Test
public void testVeryLongSingleToken_shouldNotThrowOnLength() {
  MaxentModel model = mock(MaxentModel.class);
  TokenContextGenerator cg = mock(TokenContextGenerator.class);
  Pattern pattern = Pattern.compile("^[a-z]+$");

  StringBuilder longToken = new StringBuilder();
  for (int i = 0; i < 10000; i++) {
    longToken.append("a");
  }
  String input = longToken.toString();

  TokenizerFactory factory = mock(TokenizerFactory.class);
  when(factory.getContextGenerator()).thenReturn(cg);
  when(factory.getAlphaNumericPattern()).thenReturn(pattern);
  when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

  TokenizerModel tokenizerModel = mock(TokenizerModel.class);
  when(tokenizerModel.getFactory()).thenReturn(factory);
  when(tokenizerModel.getMaxentModel()).thenReturn(model);
  when(tokenizerModel.getAbbreviations()).thenReturn(null);

  TokenizerME tokenizer = new TokenizerME(tokenizerModel);
  Span[] spans = tokenizer.tokenizePos(input);

  assertEquals(1, spans.length);
  assertEquals(0, spans[0].getStart());
  assertEquals(input.length(), spans[0].getEnd());
  double[] probs = tokenizer.getTokenProbabilities();
  assertEquals(1, probs.length);
  assertEquals(1.0, probs[0], 0.0001);
}
@Test
public void testTokenWithMultipleDotsAndAbbreviationShouldBeSingleSpan() {
  MaxentModel model = mock(MaxentModel.class);
  TokenContextGenerator cg = mock(TokenContextGenerator.class);
  Dictionary abbreviationDict = mock(Dictionary.class);
  Pattern pattern = Pattern.compile("[a-zA-Z.]+");

  when(cg.getContext("U.S.A.", 1)).thenReturn(new String[]{"ctx1"});
  when(cg.getContext("U.S.A.", 2)).thenReturn(new String[]{"ctx2"});
  when(cg.getContext("U.S.A.", 3)).thenReturn(new String[]{"ctx3"});
  when(cg.getContext("U.S.A.", 4)).thenReturn(new String[]{"ctx4"});

  when(abbreviationDict.contains(new StringList("U.S.A."))).thenReturn(true);

  when(model.eval(any(String[].class))).thenReturn(new double[]{0.1, 0.9});
  when(model.getBestOutcome(any(double[].class))).thenReturn(TokenizerME.SPLIT);
  when(model.getIndex(TokenizerME.SPLIT)).thenReturn(1);

  TokenizerFactory factory = mock(TokenizerFactory.class);
  when(factory.getContextGenerator()).thenReturn(cg);
  when(factory.getAlphaNumericPattern()).thenReturn(pattern);
  when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

  TokenizerModel tokenizerModel = mock(TokenizerModel.class);
  when(tokenizerModel.getFactory()).thenReturn(factory);
  when(tokenizerModel.getMaxentModel()).thenReturn(model);
  when(tokenizerModel.getAbbreviations()).thenReturn(abbreviationDict);

  TokenizerME tokenizer = new TokenizerME(tokenizerModel);

  Span[] spans = tokenizer.tokenizePos("U.S.A.");

  assertEquals(1, spans.length);
  assertEquals("U.S.A.", "U.S.A.".substring(spans[0].getStart(), spans[0].getEnd()));

  double[] probs = tokenizer.getTokenProbabilities();
  assertEquals(1, probs.length);
  assertTrue(probs[0] > 0.0);
}
@Test
public void testTokenizePosNullInput_shouldThrowNullPointerException() {
  MaxentModel model = mock(MaxentModel.class);
  TokenContextGenerator cg = mock(TokenContextGenerator.class);
  Pattern pattern = Pattern.compile("[a-zA-Z]+");

  TokenizerFactory factory = mock(TokenizerFactory.class);
  when(factory.getContextGenerator()).thenReturn(cg);
  when(factory.getAlphaNumericPattern()).thenReturn(pattern);
  when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

  TokenizerModel tokenizerModel = mock(TokenizerModel.class);
  when(tokenizerModel.getFactory()).thenReturn(factory);
  when(tokenizerModel.getMaxentModel()).thenReturn(model);
  when(tokenizerModel.getAbbreviations()).thenReturn(null);

  TokenizerME tokenizer = new TokenizerME(tokenizerModel);

  try {
    tokenizer.tokenizePos(null);
    fail("Expected NullPointerException for null input");
  } catch (NullPointerException expected) {
    
  }
}
@Test
public void testAbbreviationDictionaryEmpty_shouldReturnFalseForAnyInput() {
  Dictionary dict = new Dictionary();
  boolean contains = dict.contains(new StringList("Mr."));
  assertFalse(contains);
}
@Test
public void testAbbreviationDictionaryContainsDifferentCase_shouldBeCaseSensitive() {
  Dictionary dict = new Dictionary();
  dict.put(new StringList("Dr."));

  boolean resultLowercase = dict.contains(new StringList("dr."));
  boolean resultExact = dict.contains(new StringList("Dr."));

  assertFalse(resultLowercase); 
  assertTrue(resultExact);
}
@Test
public void testGetTokenProbabilitiesCalledBeforeTokenize_shouldReturnEmptyArray() {
  MaxentModel model = mock(MaxentModel.class);
  TokenContextGenerator cg = mock(TokenContextGenerator.class);
  Pattern pattern = Pattern.compile("[a-zA-Z]+");

  TokenizerFactory factory = mock(TokenizerFactory.class);
  when(factory.getContextGenerator()).thenReturn(cg);
  when(factory.getAlphaNumericPattern()).thenReturn(pattern);
  when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

  TokenizerModel tokenizerModel = mock(TokenizerModel.class);
  when(tokenizerModel.getFactory()).thenReturn(factory);
  when(tokenizerModel.getMaxentModel()).thenReturn(model);
  when(tokenizerModel.getAbbreviations()).thenReturn(null);

  TokenizerME tokenizer = new TokenizerME(tokenizerModel);
  double[] probs = tokenizer.getTokenProbabilities();
  assertNotNull(probs);
  assertEquals(0, probs.length);
}
@Test
public void testMultipleSplitsWithinOneToken_shouldResultInMultipleSpans() {
  MaxentModel model = mock(MaxentModel.class);
  TokenContextGenerator cg = mock(TokenContextGenerator.class);
  Pattern pattern = Pattern.compile("[a-zA-Z]+");

  when(cg.getContext("breaking", 1)).thenReturn(new String[]{"ctx1"});
  when(cg.getContext("breaking", 2)).thenReturn(new String[]{"ctx2"});
  when(cg.getContext("breaking", 3)).thenReturn(new String[]{"ctx3"});
  when(cg.getContext("breaking", 4)).thenReturn(new String[]{"ctx4"});
  when(cg.getContext("breaking", 5)).thenReturn(new String[]{"ctx5"});
  when(cg.getContext("breaking", 6)).thenReturn(new String[]{"ctx6"});
  when(cg.getContext("breaking", 7)).thenReturn(new String[]{"ctx7"});

  when(model.eval(any(String[].class)))
      .thenReturn(new double[]{0.3, 0.7}) 
      .thenReturn(new double[]{0.2, 0.8}) 
      .thenReturn(new double[]{0.6, 0.4}) 
      .thenReturn(new double[]{0.7, 0.3}) 
      .thenReturn(new double[]{0.2, 0.8}) 
      .thenReturn(new double[]{0.8, 0.2}); 

  when(model.getBestOutcome(any(double[].class)))
      .thenReturn(TokenizerME.SPLIT)
      .thenReturn(TokenizerME.SPLIT)
      .thenReturn(TokenizerME.NO_SPLIT)
      .thenReturn(TokenizerME.NO_SPLIT)
      .thenReturn(TokenizerME.SPLIT)
      .thenReturn(TokenizerME.NO_SPLIT);

  when(model.getIndex(TokenizerME.SPLIT)).thenReturn(1);
  when(model.getIndex(TokenizerME.NO_SPLIT)).thenReturn(0);

  TokenizerFactory factory = mock(TokenizerFactory.class);
  when(factory.getContextGenerator()).thenReturn(cg);
  when(factory.getAlphaNumericPattern()).thenReturn(pattern);
  when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

  TokenizerModel tokenizerModel = mock(TokenizerModel.class);
  when(tokenizerModel.getFactory()).thenReturn(factory);
  when(tokenizerModel.getMaxentModel()).thenReturn(model);
  when(tokenizerModel.getAbbreviations()).thenReturn(null);

  TokenizerME tokenizer = new TokenizerME(tokenizerModel);
  Span[] spans = tokenizer.tokenizePos("breaking");

  assertTrue(spans.length >= 2);
  for (Span span : spans) {
    assertTrue(span.length() > 0);
  }
}
@Test
public void testModelEvaluationReturnsSplitMultipleTimesWithAbbreviation_shouldSkipSplit() {
  MaxentModel model = mock(MaxentModel.class);
  TokenContextGenerator cg = mock(TokenContextGenerator.class);
  Dictionary abbrDict = mock(Dictionary.class);
  Pattern pattern = Pattern.compile("[a-zA-Z.]+");

  when(abbrDict.contains(new StringList("Prof.Dr."))).thenReturn(true);

  when(cg.getContext("Prof.Dr.", 1)).thenReturn(new String[]{"ctx1"});
  when(cg.getContext("Prof.Dr.", 2)).thenReturn(new String[]{"ctx2"});
  when(cg.getContext("Prof.Dr.", 3)).thenReturn(new String[]{"ctx3"});
  when(cg.getContext("Prof.Dr.", 4)).thenReturn(new String[]{"ctx4"});
  when(cg.getContext("Prof.Dr.", 5)).thenReturn(new String[]{"ctx5"});
  when(cg.getContext("Prof.Dr.", 6)).thenReturn(new String[]{"ctx6"});
  when(cg.getContext("Prof.Dr.", 7)).thenReturn(new String[]{"ctx7"});

  when(model.eval(any(String[].class))).thenReturn(new double[]{0.2, 0.8});
  when(model.getBestOutcome(any(double[].class))).thenReturn(TokenizerME.SPLIT);
  when(model.getIndex(TokenizerME.SPLIT)).thenReturn(1);

  TokenizerFactory factory = mock(TokenizerFactory.class);
  when(factory.getContextGenerator()).thenReturn(cg);
  when(factory.getAlphaNumericPattern()).thenReturn(pattern);
  when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

  TokenizerModel tokenizerModel = mock(TokenizerModel.class);
  when(tokenizerModel.getFactory()).thenReturn(factory);
  when(tokenizerModel.getMaxentModel()).thenReturn(model);
  when(tokenizerModel.getAbbreviations()).thenReturn(abbrDict);

  TokenizerME tokenizer = new TokenizerME(tokenizerModel);
  Span[] spans = tokenizer.tokenizePos("Prof.Dr.");

  assertEquals(1, spans.length);
  assertEquals("Prof.Dr.", "Prof.Dr.".substring(spans[0].getStart(), spans[0].getEnd()));
} 
}