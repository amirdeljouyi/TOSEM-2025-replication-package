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

public class TokenizerME_llmsuite_4_GPTLLMTest {

 @Test
  public void testSingleCharacterToken() {
    String input = "A.";

    MaxentModel mockedModel = mock(MaxentModel.class);
    TokenContextGenerator mockedContextGenerator = mock(TokenContextGenerator.class);
    TokenizerFactory mockedFactory = mock(TokenizerFactory.class);
    Dictionary mockedDict = mock(Dictionary.class);

    when(mockedFactory.getContextGenerator()).thenReturn(mockedContextGenerator);
    when(mockedFactory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[A-Za-z0-9]+"));
    when(mockedFactory.isUseAlphaNumericOptimization()).thenReturn(true);

    TokenizerModel model = new TokenizerModel(mockedModel, new HashMap<>(), mockedFactory);
    TokenizerME tokenizer = new TokenizerME(model, mockedDict);

    Span[] spans = tokenizer.tokenizePos(input);

    assertEquals(2, spans.length);
    assertEquals("A", input.substring(spans[0].getStart(), spans[0].getEnd()));
    assertEquals(".", input.substring(spans[1].getStart(), spans[1].getEnd()));

    double[] probs = tokenizer.getTokenProbabilities();
    assertEquals(2, probs.length);
    assertEquals(1.0, probs[0], 0.001);
    assertEquals(1.0, probs[1], 0.001);
  }
@Test
  public void testPureAlphanumericToken() {
    String input = "Hello123";

    MaxentModel mockedModel = mock(MaxentModel.class);
    TokenContextGenerator mockedContextGenerator = mock(TokenContextGenerator.class);
    TokenizerFactory mockedFactory = mock(TokenizerFactory.class);
    Dictionary mockedDict = mock(Dictionary.class);

    when(mockedFactory.isUseAlphaNumericOptimization()).thenReturn(true);
    when(mockedFactory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[A-Za-z0-9]+"));
    when(mockedFactory.getContextGenerator()).thenReturn(mockedContextGenerator);

    TokenizerModel model = new TokenizerModel(mockedModel, new HashMap<>(), mockedFactory);
    TokenizerME tokenizer = new TokenizerME(model, mockedDict);

    Span[] spans = tokenizer.tokenizePos(input);

    assertEquals(1, spans.length);
    assertEquals("Hello123", input.substring(spans[0].getStart(), spans[0].getEnd()));

    double[] probs = tokenizer.getTokenProbabilities();
    assertEquals(1, probs.length);
    assertEquals(1.0, probs[0], 0.001);
  }
@Test
  public void testTokenizeWithSplitDecision() {
    String input = "email@example.com";

    MaxentModel mockedModel = mock(MaxentModel.class);
    TokenContextGenerator mockedContextGenerator = mock(TokenContextGenerator.class);
    TokenizerFactory mockedFactory = mock(TokenizerFactory.class);

    when(mockedFactory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[A-Za-z0-9]+"));
    when(mockedFactory.getContextGenerator()).thenReturn(mockedContextGenerator);
    when(mockedFactory.isUseAlphaNumericOptimization()).thenReturn(false);

    when(mockedContextGenerator.getContext(eq(input), anyInt())).thenReturn(new String[] { "f1", "f2" });
    when(mockedModel.eval(any(String[].class))).thenReturn(new double[] { 0.1, 0.9 });
    when(mockedModel.getBestOutcome(any(double[].class))).thenReturn(TokenizerME.SPLIT);
    when(mockedModel.getIndex(TokenizerME.SPLIT)).thenReturn(1);

    TokenizerModel model = new TokenizerModel(mockedModel, new HashMap<>(), mockedFactory);
    TokenizerME tokenizer = new TokenizerME(model);

    Span[] spans = tokenizer.tokenizePos(input);

    assertNotNull(spans);
    assertTrue(spans.length >= 1);

    double[] probs = tokenizer.getTokenProbabilities();
    assertEquals(spans.length, probs.length);
    assertTrue(probs[0] > 0.0);
  }
@Test
  public void testAbbreviationHandlingWithDictionary() {
    String input = "U.S.A.";

    MaxentModel mockedModel = mock(MaxentModel.class);
    TokenContextGenerator mockedContextGenerator = mock(TokenContextGenerator.class);
    TokenizerFactory mockedFactory = mock(TokenizerFactory.class);
    Dictionary mockedDict = mock(Dictionary.class);

    when(mockedFactory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[A-Za-z0-9]+"));
    when(mockedFactory.getContextGenerator()).thenReturn(mockedContextGenerator);
    when(mockedFactory.isUseAlphaNumericOptimization()).thenReturn(false);

    when(mockedDict.contains(new StringList("U.S.A."))).thenReturn(true);

    when(mockedContextGenerator.getContext(eq("U.S.A."), anyInt())).thenReturn(new String[] { "f1" });
    when(mockedModel.eval(any(String[].class))).thenReturn(new double[] { 0.1, 0.9 });
    when(mockedModel.getBestOutcome(any(double[].class))).thenReturn(TokenizerME.SPLIT);
    when(mockedModel.getIndex(TokenizerME.SPLIT)).thenReturn(1);

    TokenizerModel model = new TokenizerModel(mockedModel, new HashMap<>(), mockedFactory);
    TokenizerME tokenizer = new TokenizerME(model, mockedDict);

    Span[] spans = tokenizer.tokenizePos(input);

    assertEquals(1, spans.length);
    assertEquals("U.S.A.", input.substring(spans[0].getStart(), spans[0].getEnd()));
  }
@Test
  public void testEmptyInput() {
    String input = "";

    MaxentModel mockedModel = mock(MaxentModel.class);
    TokenContextGenerator mockedContextGenerator = mock(TokenContextGenerator.class);
    TokenizerFactory mockedFactory = mock(TokenizerFactory.class);
    Dictionary mockedDict = mock(Dictionary.class);

    when(mockedFactory.isUseAlphaNumericOptimization()).thenReturn(true);
    when(mockedFactory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[A-Za-z0-9]+"));
    when(mockedFactory.getContextGenerator()).thenReturn(mockedContextGenerator);

    TokenizerModel model = new TokenizerModel(mockedModel, new HashMap<>(), mockedFactory);
    TokenizerME tokenizer = new TokenizerME(model, mockedDict);

    Span[] spans = tokenizer.tokenizePos(input);

    assertEquals(0, spans.length);
    assertEquals(0, tokenizer.getTokenProbabilities().length);
  }
@Test
  public void testWhiteSpaceOnlyInput() {
    String input = "   \t\n   ";

    MaxentModel mockedModel = mock(MaxentModel.class);
    TokenContextGenerator mockedContextGenerator = mock(TokenContextGenerator.class);
    TokenizerFactory mockedFactory = mock(TokenizerFactory.class);
    Dictionary mockedDict = mock(Dictionary.class);

    when(mockedFactory.isUseAlphaNumericOptimization()).thenReturn(true);
    when(mockedFactory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[A-Za-z0-9]+"));
    when(mockedFactory.getContextGenerator()).thenReturn(mockedContextGenerator);

    TokenizerModel model = new TokenizerModel(mockedModel, new HashMap<>(), mockedFactory);
    TokenizerME tokenizer = new TokenizerME(model, mockedDict);

    Span[] spans = tokenizer.tokenizePos(input);

    assertEquals(0, spans.length);
  }
@Test
  public void testNoAbbreviationDictionary() {
    MaxentModel mockedModel = mock(MaxentModel.class);
    TokenizerFactory mockedFactory = mock(TokenizerFactory.class);
    TokenContextGenerator mockedContextGenerator = mock(TokenContextGenerator.class);

    when(mockedFactory.isUseAlphaNumericOptimization()).thenReturn(false);
    when(mockedFactory.getContextGenerator()).thenReturn(mockedContextGenerator);
    when(mockedFactory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[A-Za-z0-9]+"));

    TokenizerModel model = new TokenizerModel(mockedModel, new HashMap<>(), mockedFactory);
    TokenizerME tokenizer = new TokenizerME(model, null);

    boolean result = tokenizer.isAcceptableAbbreviation("Dr.");
    assertFalse(result);
  }
@Test
  public void testUseAlphaNumericOptimizationFlagTrue() {
    MaxentModel mockedModel = mock(MaxentModel.class);
    TokenizerFactory mockedFactory = mock(TokenizerFactory.class);
    TokenContextGenerator mockedContextGenerator = mock(TokenContextGenerator.class);

    when(mockedFactory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[A-Z]+"));
    when(mockedFactory.isUseAlphaNumericOptimization()).thenReturn(true);
    when(mockedFactory.getContextGenerator()).thenReturn(mockedContextGenerator);

    TokenizerModel model = new TokenizerModel(mockedModel, new HashMap<>(), mockedFactory);
    TokenizerME tokenizer = new TokenizerME(model);

    assertTrue(tokenizer.useAlphaNumericOptimization());
  }
@Test
  public void testTrainReturnsTokenizerModel() throws IOException {
    ObjectStream<TokenSample> sampleStream = mock(ObjectStream.class);
    TrainingParameters params = new TrainingParameters();

    TokenizerFactory mockedFactory = mock(TokenizerFactory.class);
    EventTrainer trainer = mock(EventTrainer.class);
    MaxentModel trainedModel = mock(MaxentModel.class);

    TrainerFactory.setEventTrainerFactory((trainParams, manifestInfo) -> trainer);
    when(trainer.train(any())).thenReturn(trainedModel);
    when(mockedFactory.isUseAlphaNumericOptimization()).thenReturn(true);
    when(mockedFactory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[a-z]+"));
    when(mockedFactory.getContextGenerator()).thenReturn(mock(TokenContextGenerator.class));

    TokenizerModel model = TokenizerME.train(sampleStream, mockedFactory, params);

    assertNotNull(model);
    assertSame(trainedModel, model.getMaxentModel());
  }
@Test
  public void testTokenizeNullInputThrowsNPE() {
    MaxentModel mockedModel = mock(MaxentModel.class);
    TokenizerFactory mockedFactory = mock(TokenizerFactory.class);
    TokenContextGenerator mockedContextGenerator = mock(TokenContextGenerator.class);
    Dictionary mockedDict = mock(Dictionary.class);

    when(mockedFactory.getContextGenerator()).thenReturn(mockedContextGenerator);
    when(mockedFactory.getAlphaNumericPattern()).thenReturn(Pattern.compile(".+"));
    when(mockedFactory.isUseAlphaNumericOptimization()).thenReturn(true);

    TokenizerModel model = new TokenizerModel(mockedModel, new HashMap<>(), mockedFactory);
    TokenizerME tokenizer = new TokenizerME(model, mockedDict);

    try {
      tokenizer.tokenizePos(null);
      fail("Expected NullPointerException");
    } catch (NullPointerException expected) {
      
    }
  }
@Test
  public void testNullDictionaryInConstructor() {
    MaxentModel mockedModel = mock(MaxentModel.class);
    TokenizerFactory factory = mock(TokenizerFactory.class);
    TokenContextGenerator contextGen = mock(TokenContextGenerator.class);

    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[A-Za-z]+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);
    when(factory.getContextGenerator()).thenReturn(contextGen);

    TokenizerModel model = new TokenizerModel(mockedModel, new HashMap<String, String>(), factory);
    TokenizerME tokenizer = new TokenizerME(model, null);

    assertNotNull(tokenizer);
    assertFalse(tokenizer.isAcceptableAbbreviation("e.g."));
  }
@Test
  public void testAbbreviationWithNonMatchingDictEntry() {
    MaxentModel mockedModel = mock(MaxentModel.class);
    TokenizerFactory factory = mock(TokenizerFactory.class);
    TokenContextGenerator cg = mock(TokenContextGenerator.class);
    Dictionary dictionary = mock(Dictionary.class);

    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile(".+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);
    when(factory.getContextGenerator()).thenReturn(cg);
    when(dictionary.contains(new StringList("NotAbbrev"))).thenReturn(false);

    TokenizerModel model = new TokenizerModel(mockedModel, new HashMap<String, String>(), factory);
    TokenizerME tokenizer = new TokenizerME(model, dictionary);

    assertFalse(tokenizer.isAcceptableAbbreviation("NotAbbrev"));
  }
@Test
  public void testPunctuationOnlyToken() {
    String input = "!!!";

    MaxentModel mockedModel = mock(MaxentModel.class);
    TokenizerFactory factory = mock(TokenizerFactory.class);
    TokenContextGenerator cg = mock(TokenContextGenerator.class);

    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[A-Za-z0-9]+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);
    when(factory.getContextGenerator()).thenReturn(cg);

    TokenizerModel model = new TokenizerModel(mockedModel, new HashMap<String, String>(), factory);
    TokenizerME tokenizer = new TokenizerME(model);

    Span[] spans = tokenizer.tokenizePos(input);

    assertNotNull(spans);
    assertTrue(spans.length >= 1);
    assertEquals("!", input.substring(spans[0].getStart(), spans[0].getEnd()));
  }
@Test
  public void testLongContinuousAlphaTokenSplitEvaluation() {
    String input = "supercalifragilisticexpialidocious";

    MaxentModel mockedModel = mock(MaxentModel.class);
    TokenizerFactory factory = mock(TokenizerFactory.class);
    TokenContextGenerator cg = mock(TokenContextGenerator.class);

    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[A-Za-z]+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);
    when(factory.getContextGenerator()).thenReturn(cg);

    when(cg.getContext(eq(input), anyInt())).thenReturn(new String[]{"f"});
    when(mockedModel.eval(any(String[].class))).thenReturn(new double[]{0.3, 0.7});
    when(mockedModel.getBestOutcome(any(double[].class))).thenReturn(TokenizerME.SPLIT);
    when(mockedModel.getIndex(TokenizerME.SPLIT)).thenReturn(1);

    TokenizerModel model = new TokenizerModel(mockedModel, new HashMap<String, String>(), factory);
    TokenizerME tokenizer = new TokenizerME(model);

    Span[] spans = tokenizer.tokenizePos(input);

    double[] probs = tokenizer.getTokenProbabilities();
    assertEquals(spans.length, probs.length);
    assertTrue(spans.length >= 1);
    assertTrue(probs[0] > 0);
  }
@Test
  public void testWhitespaceOnlyTabsAndNewlines() {
    String input = "\t\t\n\n  ";

    MaxentModel mockedModel = mock(MaxentModel.class);
    TokenizerFactory factory = mock(TokenizerFactory.class);
    TokenContextGenerator cg = mock(TokenContextGenerator.class);

    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[A-Za-z0-9]+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);
    when(factory.getContextGenerator()).thenReturn(cg);

    TokenizerModel model = new TokenizerModel(mockedModel, new HashMap<String, String>(), factory);
    TokenizerME tokenizer = new TokenizerME(model);

    Span[] spans = tokenizer.tokenizePos(input);

    assertEquals(0, spans.length);
    assertEquals(0, tokenizer.getTokenProbabilities().length);
  }
@Test
  public void testAbbreviationFollowedByNumberOfDotsMoreThanOne() {
    String input = "U..S..A..";

    MaxentModel mockedModel = mock(MaxentModel.class);
    TokenizerFactory factory = mock(TokenizerFactory.class);
    TokenContextGenerator contextGenerator = mock(TokenContextGenerator.class);
    Dictionary dictionary = mock(Dictionary.class);

    when(dictionary.contains(new StringList("U..S..A.."))).thenReturn(true);
    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[A-Z.]+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);
    when(factory.getContextGenerator()).thenReturn(contextGenerator);

    when(contextGenerator.getContext(eq("U..S..A.."), anyInt())).thenReturn(new String[]{"f"});
    when(mockedModel.eval(any(String[].class))).thenReturn(new double[]{0.1, 0.9});
    when(mockedModel.getBestOutcome(any(double[].class))).thenReturn(TokenizerME.SPLIT);
    when(mockedModel.getIndex(TokenizerME.SPLIT)).thenReturn(1);

    TokenizerModel model = new TokenizerModel(mockedModel, new HashMap<String, String>(), factory);
    TokenizerME tokenizer = new TokenizerME(model, dictionary);

    Span[] spans = tokenizer.tokenizePos(input);

    assertEquals(1, spans.length);
    assertEquals("U..S..A..", input.substring(spans[0].getStart(), spans[0].getEnd()));
  }
@Test
  public void testUnexpectedOutcomeValueFromModel() {
    String input = "tokenize.me";

    MaxentModel mockedModel = mock(MaxentModel.class);
    TokenizerFactory factory = mock(TokenizerFactory.class);
    TokenContextGenerator contextGenerator = mock(TokenContextGenerator.class);

    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[a-zA-Z0-9]+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);
    when(factory.getContextGenerator()).thenReturn(contextGenerator);

    when(contextGenerator.getContext(eq("tokenize.me"), anyInt())).thenReturn(new String[]{"dummy"});
    when(mockedModel.eval(any(String[].class))).thenReturn(new double[]{0.2, 0.5});
    when(mockedModel.getBestOutcome(any(double[].class))).thenReturn("UNKNOWN");
    when(mockedModel.getIndex("UNKNOWN")).thenReturn(0);

    TokenizerModel model = new TokenizerModel(mockedModel, new HashMap<String, String>(), factory);
    TokenizerME tokenizer = new TokenizerME(model);

    Span[] spans = tokenizer.tokenizePos(input);

    assertNotNull(spans);
    assertTrue(spans.length > 0);
    double[] probs = tokenizer.getTokenProbabilities();
    assertEquals(spans.length, probs.length);
  }
@Test
  public void testHighUnicodeCharacters() {
    String input = "emoji🙂test";

    MaxentModel mockedModel = mock(MaxentModel.class);
    TokenizerFactory factory = mock(TokenizerFactory.class);
    TokenContextGenerator contextGenerator = mock(TokenContextGenerator.class);

    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[\\p{L}\\p{N}]+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);
    when(factory.getContextGenerator()).thenReturn(contextGenerator);

    TokenizerModel model = new TokenizerModel(mockedModel, new HashMap<String, String>(), factory);
    TokenizerME tokenizer = new TokenizerME(model);

    Span[] spans = tokenizer.tokenizePos(input);

    assertNotNull(spans);
    assertTrue(spans.length >= 1);
  }
@Test
  public void testShortNonAlphanumericToken() {
    String input = ".";

    MaxentModel mockedModel = mock(MaxentModel.class);
    TokenizerFactory factory = mock(TokenizerFactory.class);
    TokenContextGenerator contextGenerator = mock(TokenContextGenerator.class);

    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[A-Za-z0-9]+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);
    when(factory.getContextGenerator()).thenReturn(contextGenerator);

    TokenizerModel model = new TokenizerModel(mockedModel, new HashMap<String, String>(), factory);
    TokenizerME tokenizer = new TokenizerME(model);

    Span[] spans = tokenizer.tokenizePos(input);

    assertEquals(1, spans.length);
    assertEquals(".", input.substring(spans[0].getStart(), spans[0].getEnd()));
    double[] probs = tokenizer.getTokenProbabilities();
    assertEquals(1, probs.length);
    assertEquals(1.0, probs[0], 0.001);
  }
@Test
  public void testTokenLongerThanOneButNoSplitOccurs() {
    String input = "unchoppable";

    MaxentModel mockedModel = mock(MaxentModel.class);
    TokenContextGenerator contextGen = mock(TokenContextGenerator.class);
    TokenizerFactory factory = mock(TokenizerFactory.class);

    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[a-z]+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);
    when(factory.getContextGenerator()).thenReturn(contextGen);

    when(contextGen.getContext(eq("unchoppable"), anyInt())).thenReturn(new String[]{"f"});
    when(mockedModel.eval(any(String[].class))).thenReturn(new double[]{0.99, 0.01});
    when(mockedModel.getBestOutcome(any(double[].class))).thenReturn(TokenizerME.NO_SPLIT);
    when(mockedModel.getIndex(TokenizerME.NO_SPLIT)).thenReturn(0);

    TokenizerModel model = new TokenizerModel(mockedModel, new HashMap<String, String>(), factory);
    TokenizerME tokenizer = new TokenizerME(model);

    Span[] spans = tokenizer.tokenizePos(input);

    assertEquals(1, spans.length);
    assertEquals("unchoppable", input.substring(spans[0].getStart(), spans[0].getEnd()));

    double[] probs = tokenizer.getTokenProbabilities();
    assertEquals(1, probs.length);
    assertTrue(probs[0] < 1.0); 
  }
@Test
  public void testSplitCreatesTailFragment() {
    String input = "abcdef";

    MaxentModel mockedModel = mock(MaxentModel.class);
    TokenContextGenerator contextGen = mock(TokenContextGenerator.class);
    TokenizerFactory factory = mock(TokenizerFactory.class);

    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[a-z]+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);
    when(factory.getContextGenerator()).thenReturn(contextGen);

    when(contextGen.getContext(eq("abcdef"), anyInt())).thenReturn(new String[]{"ctx"});
    when(mockedModel.eval(any(String[].class))).thenReturn(new double[]{0.4, 0.6});
    when(mockedModel.getBestOutcome(any(double[].class))).thenReturn(TokenizerME.SPLIT);
    when(mockedModel.getIndex(TokenizerME.SPLIT)).thenReturn(1);

    TokenizerModel model = new TokenizerModel(mockedModel, new HashMap<String, String>(), factory);
    TokenizerME tokenizer = new TokenizerME(model);

    Span[] spans = tokenizer.tokenizePos(input);

    assertTrue(spans.length >= 1);  
    double[] probs = tokenizer.getTokenProbabilities();
    assertEquals(spans.length, probs.length);
  }
@Test
  public void testTokenizationWithAllSplits() {
    String input = "xyz";

    MaxentModel mockedModel = mock(MaxentModel.class);
    TokenContextGenerator contextGen = mock(TokenContextGenerator.class);
    TokenizerFactory factory = mock(TokenizerFactory.class);

    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[a-z]+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);
    when(factory.getContextGenerator()).thenReturn(contextGen);

    when(contextGen.getContext(eq("xyz"), anyInt())).thenReturn(new String[]{"ctx"});
    when(mockedModel.eval(any(String[].class))).thenReturn(new double[]{0.1, 0.9});
    when(mockedModel.getBestOutcome(any(double[].class))).thenReturn(TokenizerME.SPLIT);
    when(mockedModel.getIndex(TokenizerME.SPLIT)).thenReturn(1);

    TokenizerModel model = new TokenizerModel(mockedModel, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(model);

    Span[] spans = tokenizer.tokenizePos(input);
    assertTrue(spans.length > 1);
    double[] probs = tokenizer.getTokenProbabilities();
    assertEquals(spans.length, probs.length);
  }
@Test
  public void testWhitespaceTokenizerSingleSpaceDelimiters() {
    String input = "one two";

    MaxentModel mockedModel = mock(MaxentModel.class);
    TokenContextGenerator contextGen = mock(TokenContextGenerator.class);
    TokenizerFactory factory = mock(TokenizerFactory.class);

    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[a-z]+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);
    when(factory.getContextGenerator()).thenReturn(contextGen);

    TokenizerModel model = new TokenizerModel(mockedModel, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(model);

    Span[] spans = tokenizer.tokenizePos(input);

    assertEquals(2, spans.length);
    assertEquals("one", input.substring(spans[0].getStart(), spans[0].getEnd()));
    assertEquals("two", input.substring(spans[1].getStart(), spans[1].getEnd()));
    double[] probs = tokenizer.getTokenProbabilities();
    assertEquals(2, probs.length);
    assertEquals(1.0, probs[0], 0.01);
    assertEquals(1.0, probs[1], 0.01);
  }
@Test
  public void testAlphanumericOptimizationTurnedOff() {
    String input = "DATA42";

    MaxentModel mockedModel = mock(MaxentModel.class);
    TokenContextGenerator contextGen = mock(TokenContextGenerator.class);
    TokenizerFactory factory = mock(TokenizerFactory.class);

    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[A-Z0-9]+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false); 
    when(factory.getContextGenerator()).thenReturn(contextGen);

    when(contextGen.getContext(eq("DATA42"), anyInt())).thenReturn(new String[]{"context"});
    when(mockedModel.eval(any(String[].class))).thenReturn(new double[]{0.8, 0.2});
    when(mockedModel.getBestOutcome(any(double[].class))).thenReturn(TokenizerME.NO_SPLIT);
    when(mockedModel.getIndex(TokenizerME.NO_SPLIT)).thenReturn(0);

    TokenizerModel model = new TokenizerModel(mockedModel, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(model);

    Span[] spans = tokenizer.tokenizePos(input);

    assertEquals(1, spans.length);
    assertEquals("DATA42", input.substring(spans[0].getStart(), spans[0].getEnd()));
  }
@Test
  public void testSingleCharacterNonAlphanumericSymbol() {
    String input = "#";

    MaxentModel mockedModel = mock(MaxentModel.class);
    TokenContextGenerator contextGen = mock(TokenContextGenerator.class);
    TokenizerFactory factory = mock(TokenizerFactory.class);

    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[A-Za-z0-9]+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);
    when(factory.getContextGenerator()).thenReturn(contextGen);

    TokenizerModel model = new TokenizerModel(mockedModel, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(model);

    Span[] spans = tokenizer.tokenizePos(input);

    assertEquals(1, spans.length);
    assertEquals("#", input.substring(spans[0].getStart(), spans[0].getEnd()));
    double[] probs = tokenizer.getTokenProbabilities();
    assertEquals(1, probs.length);
    assertEquals(1.0, probs[0], 0.01); 
  }
@Test
  public void testGetProbabilitiesReturnsCopy() {
    String input = "splitme";

    MaxentModel mockedModel = mock(MaxentModel.class);
    TokenContextGenerator contextGen = mock(TokenContextGenerator.class);
    TokenizerFactory factory = mock(TokenizerFactory.class);

    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile(".+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);
    when(factory.getContextGenerator()).thenReturn(contextGen);

    when(contextGen.getContext(eq("splitme"), anyInt())).thenReturn(new String[]{"con"});
    when(mockedModel.eval(any(String[].class))).thenReturn(new double[]{0.5, 0.5});
    when(mockedModel.getBestOutcome(any(double[].class))).thenReturn(TokenizerME.SPLIT);
    when(mockedModel.getIndex(TokenizerME.SPLIT)).thenReturn(1);

    TokenizerModel model = new TokenizerModel(mockedModel, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(model);

    Span[] spans = tokenizer.tokenizePos(input);
    double[] firstCall = tokenizer.getTokenProbabilities();
    double[] secondCall = tokenizer.getTokenProbabilities();

    assertNotSame(firstCall, secondCall);
    assertArrayEquals(firstCall, secondCall, 0.0001);
  }
@Test
  public void testMultibyteUnicodeCharacterInput() {
    String input = "café_日本";

    MaxentModel mockedModel = mock(MaxentModel.class);
    TokenContextGenerator contextGenerator = mock(TokenContextGenerator.class);
    TokenizerFactory factory = mock(TokenizerFactory.class);

    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("\\p{L}+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);
    when(factory.getContextGenerator()).thenReturn(contextGenerator);

    TokenizerModel model = new TokenizerModel(mockedModel, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(model);

    Span[] spans = tokenizer.tokenizePos(input);

    assertNotNull(spans);
    assertTrue(spans.length >= 1);
  }
@Test
  public void testInputWithNoAlphaNumericCharacters() {
    String input = "***%%%!!!";

    MaxentModel mockedModel = mock(MaxentModel.class);
    TokenContextGenerator contextGenerator = mock(TokenContextGenerator.class);
    TokenizerFactory factory = mock(TokenizerFactory.class);

    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[a-zA-Z0-9]+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);
    when(factory.getContextGenerator()).thenReturn(contextGenerator);

    TokenizerModel model = new TokenizerModel(mockedModel, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(model);

    Span[] spans = tokenizer.tokenizePos(input);

    assertNotNull(spans);
    assertTrue(spans.length >= 1);
  }
@Test
  public void testTokenWithOnlyWhitespaceCharactersBetween() {
    String input = "one    two\tthree\nfour";

    MaxentModel mockedModel = mock(MaxentModel.class);
    TokenContextGenerator contextGen = mock(TokenContextGenerator.class);
    TokenizerFactory factory = mock(TokenizerFactory.class);

    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[a-z]+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);
    when(factory.getContextGenerator()).thenReturn(contextGen);

    TokenizerModel model = new TokenizerModel(mockedModel, new HashMap<String, String>(), factory);
    TokenizerME tokenizer = new TokenizerME(model);

    Span[] spans = tokenizer.tokenizePos(input);

    assertEquals(4, spans.length);
    assertEquals("one", input.substring(spans[0].getStart(), spans[0].getEnd()));
    assertEquals("two", input.substring(spans[1].getStart(), spans[1].getEnd()));
    assertEquals("three", input.substring(spans[2].getStart(), spans[2].getEnd()));
    assertEquals("four", input.substring(spans[3].getStart(), spans[3].getEnd()));
  }
@Test
  public void testGetTokenProbabilitiesAfterEmptyInput() {
    String input = "";

    MaxentModel mockedModel = mock(MaxentModel.class);
    TokenContextGenerator contextGen = mock(TokenContextGenerator.class);
    TokenizerFactory factory = mock(TokenizerFactory.class);

    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[a-zA-Z]+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);
    when(factory.getContextGenerator()).thenReturn(contextGen);

    TokenizerModel model = new TokenizerModel(mockedModel, new HashMap<String, String>(), factory);
    TokenizerME tokenizer = new TokenizerME(model);

    Span[] spans = tokenizer.tokenizePos(input);
    double[] probs = tokenizer.getTokenProbabilities();

    assertEquals(0, spans.length);
    assertEquals(0, probs.length);
  }
@Test
  public void testAbbreviationWithMultipleDotsResetsProbability() {
    String input = "e.g.";

    MaxentModel mockedModel = mock(MaxentModel.class);
    TokenContextGenerator contextGen = mock(TokenContextGenerator.class);
    TokenizerFactory factory = mock(TokenizerFactory.class);
    Dictionary dictionary = mock(Dictionary.class);

    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[a-z.]+"));
    when(factory.getContextGenerator()).thenReturn(contextGen);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);
    when(dictionary.contains(new StringList("e.g."))).thenReturn(true);

    when(contextGen.getContext(eq("e.g."), anyInt())).thenReturn(new String[]{"ctx"});
    when(mockedModel.eval(any())).thenReturn(new double[]{0.2, 0.8});
    when(mockedModel.getBestOutcome(any())).thenReturn(TokenizerME.SPLIT);
    when(mockedModel.getIndex(TokenizerME.SPLIT)).thenReturn(1);

    TokenizerModel model = new TokenizerModel(mockedModel, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(model, dictionary);

    Span[] spans = tokenizer.tokenizePos(input);
    double[] probs = tokenizer.getTokenProbabilities();

    assertEquals(1, spans.length);
    assertEquals("e.g.", input.substring(spans[0].getStart(), spans[0].getEnd()));
    assertEquals(1, probs.length);
    assertTrue(probs[0] > 0);
  }
@Test
  public void testAllCharactersSingleTokenNoSplit() {
    String input = "abcde";

    MaxentModel mockedModel = mock(MaxentModel.class);
    TokenContextGenerator cg = mock(TokenContextGenerator.class);
    TokenizerFactory factory = mock(TokenizerFactory.class);

    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[a-z]+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);
    when(factory.getContextGenerator()).thenReturn(cg);

    when(cg.getContext(eq("abcde"), anyInt())).thenReturn(new String[]{"cx"});
    when(mockedModel.eval(any())).thenReturn(new double[]{1.0, 0.0});
    when(mockedModel.getBestOutcome(any())).thenReturn(TokenizerME.NO_SPLIT);
    when(mockedModel.getIndex(TokenizerME.NO_SPLIT)).thenReturn(0);

    TokenizerModel model = new TokenizerModel(mockedModel, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(model);

    Span[] spans = tokenizer.tokenizePos(input);

    assertEquals(1, spans.length);
    assertEquals("abcde", input.substring(spans[0].getStart(), spans[0].getEnd()));
    double[] probs = tokenizer.getTokenProbabilities();
    assertEquals(1, probs.length);
    assertEquals(1.0, probs[0], 0.0001);
  }
@Test
  public void testTokenizeOnlyOneCharNotAlphaNumeric() {
    String input = "@";

    MaxentModel mockedModel = mock(MaxentModel.class);
    TokenizerFactory factory = mock(TokenizerFactory.class);
    TokenContextGenerator contextGen = mock(TokenContextGenerator.class);

    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[a-zA-Z0-9]+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);
    when(factory.getContextGenerator()).thenReturn(contextGen);

    TokenizerModel model = new TokenizerModel(mockedModel, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(model);

    Span[] spans = tokenizer.tokenizePos(input);

    assertEquals(1, spans.length);
    assertEquals("@", input.substring(spans[0].getStart(), spans[0].getEnd()));
    double[] probs = tokenizer.getTokenProbabilities();
    assertEquals(1, probs.length);
    assertEquals(1.0, probs[0], 0.01);
  }
@Test
  public void testTokenizePosDoesNotReusePreviousResults() {
    String input1 = "first";
    String input2 = "second";

    MaxentModel mockedModel = mock(MaxentModel.class);
    TokenizerFactory factory = mock(TokenizerFactory.class);
    TokenContextGenerator contextGen = mock(TokenContextGenerator.class);

    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile(".+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);
    when(factory.getContextGenerator()).thenReturn(contextGen);

    TokenizerModel model = new TokenizerModel(mockedModel, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(model);

    tokenizer.tokenizePos(input1);
    double[] probs1 = tokenizer.getTokenProbabilities();

    tokenizer.tokenizePos(input2);
    double[] probs2 = tokenizer.getTokenProbabilities();

    assertNotSame(probs1, probs2);
  }
@Test
  public void testTokenWithInitialSplitThenEndMismatch() {
    String input = "ab.cd";

    MaxentModel mockedModel = mock(MaxentModel.class);
    TokenContextGenerator contextGen = mock(TokenContextGenerator.class);
    TokenizerFactory factory = mock(TokenizerFactory.class);

    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[a-z]+"));
    when(factory.getContextGenerator()).thenReturn(contextGen);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

    when(contextGen.getContext(eq("ab.cd"), anyInt())).thenReturn(new String[]{"ctx"});
    when(mockedModel.eval(any())).thenReturn(new double[]{0.3, 0.7});
    when(mockedModel.getBestOutcome(any())).thenReturn(TokenizerME.SPLIT);
    when(mockedModel.getIndex(TokenizerME.SPLIT)).thenReturn(1);

    TokenizerModel model = new TokenizerModel(mockedModel, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(model);

    Span[] spans = tokenizer.tokenizePos(input);
    double[] probs = tokenizer.getTokenProbabilities();

    assertNotNull(spans);
    assertTrue(spans.length >= 1);
    assertEquals(spans.length, probs.length);
  }
@Test
  public void testMultipleWhitespaceTokensWithKeepNewLinesEnabled() {
    String input = "aaa\nbbb\tccc ddd\n";

    MaxentModel mockedModel = mock(MaxentModel.class);
    TokenContextGenerator contextGen = mock(TokenContextGenerator.class);
    TokenizerFactory factory = mock(TokenizerFactory.class);

    when(factory.getContextGenerator()).thenReturn(contextGen);
    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[a-z]+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

    TokenizerModel model = new TokenizerModel(mockedModel, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(model);
    tokenizer.setKeepNewLines(true);

    Span[] spans = tokenizer.tokenizePos(input);
    double[] probs = tokenizer.getTokenProbabilities();

    assertNotNull(spans);
    assertTrue(spans.length >= 4);
    assertEquals(spans.length, probs.length);
  }
@Test
  public void testTokenThatMatchesAlphaPatternButOptimizationDisabled() {
    String input = "AlphaToken";

    MaxentModel mockedModel = mock(MaxentModel.class);
    TokenContextGenerator contextGen = mock(TokenContextGenerator.class);
    TokenizerFactory factory = mock(TokenizerFactory.class);

    when(factory.getContextGenerator()).thenReturn(contextGen);
    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[A-Za-z]+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

    when(contextGen.getContext(eq("AlphaToken"), anyInt())).thenReturn(new String[]{"ctx"});
    when(mockedModel.eval(any())).thenReturn(new double[]{0.9, 0.1});
    when(mockedModel.getBestOutcome(any())).thenReturn(TokenizerME.NO_SPLIT);
    when(mockedModel.getIndex(TokenizerME.NO_SPLIT)).thenReturn(0);

    TokenizerModel model = new TokenizerModel(mockedModel, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(model);

    Span[] spans = tokenizer.tokenizePos(input);
    double[] probs = tokenizer.getTokenProbabilities();

    assertEquals(1, spans.length);
    assertEquals("AlphaToken", input.substring(spans[0].getStart(), spans[0].getEnd()));
    assertEquals(1, probs.length);
    assertTrue(probs[0] <= 1.0);
  }
@Test
  public void testTokenSplitWithAbbreviationSkipLogicTriggered() {
    String input = "Ph.D.";

    MaxentModel mockedModel = mock(MaxentModel.class);
    TokenContextGenerator contextGen = mock(TokenContextGenerator.class);
    TokenizerFactory factory = mock(TokenizerFactory.class);
    Dictionary dict = mock(Dictionary.class);

    when(factory.getContextGenerator()).thenReturn(contextGen);
    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[A-Za-z.]+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);
    when(dict.contains(new StringList("Ph.D."))).thenReturn(true);

    when(contextGen.getContext(eq("Ph.D."), anyInt())).thenReturn(new String[]{"ctx"});
    when(mockedModel.eval(any())).thenReturn(new double[]{0.2, 0.8});
    when(mockedModel.getBestOutcome(any())).thenReturn(TokenizerME.SPLIT);
    when(mockedModel.getIndex(TokenizerME.SPLIT)).thenReturn(1);

    TokenizerModel model = new TokenizerModel(mockedModel, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(model, dict);

    Span[] spans = tokenizer.tokenizePos(input);
    double[] probs = tokenizer.getTokenProbabilities();

    assertEquals(1, spans.length);
    assertEquals("Ph.D.", input.substring(spans[0].getStart(), spans[0].getEnd()));
    assertEquals(1, probs.length);
    assertTrue(probs[0] > 0.0);
  }
@Test
  public void testTokenWithSplitAtEveryIndex() {
    String input = "abc";

    MaxentModel mockedModel = mock(MaxentModel.class);
    TokenContextGenerator contextGen = mock(TokenContextGenerator.class);
    TokenizerFactory factory = mock(TokenizerFactory.class);

    when(factory.getContextGenerator()).thenReturn(contextGen);
    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[a-z]+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

    when(contextGen.getContext(eq("abc"), anyInt())).thenReturn(new String[]{"ctx"});
    when(mockedModel.eval(any())).thenReturn(new double[]{0.1, 0.9});
    when(mockedModel.getBestOutcome(any())).thenReturn(TokenizerME.SPLIT);
    when(mockedModel.getIndex(TokenizerME.SPLIT)).thenReturn(1);

    TokenizerModel model = new TokenizerModel(mockedModel, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(model);

    Span[] spans = tokenizer.tokenizePos(input);
    double[] probs = tokenizer.getTokenProbabilities();

    assertTrue(spans.length >= 2);
    assertEquals(spans.length, probs.length);
  }
@Test
  public void testMultipleTokenizePosInvocationsProduceCorrectReset() {
    String input1 = "one";
    String input2 = "two";

    MaxentModel mockedModel = mock(MaxentModel.class);
    TokenContextGenerator contextGen = mock(TokenContextGenerator.class);
    TokenizerFactory factory = mock(TokenizerFactory.class);

    when(factory.getContextGenerator()).thenReturn(contextGen);
    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[a-z]+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

    TokenizerModel model = new TokenizerModel(mockedModel, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(model);

    Span[] spans1 = tokenizer.tokenizePos(input1);
    assertEquals(1, spans1.length);
    double[] probs1 = tokenizer.getTokenProbabilities();
    assertEquals(1, probs1.length);

    Span[] spans2 = tokenizer.tokenizePos(input2);
    assertEquals(1, spans2.length);
    double[] probs2 = tokenizer.getTokenProbabilities();
    assertEquals(1, probs2.length);

    assertNotSame(probs1, probs2);
  }
@Test
  public void testTokenNotInAbbrDictReturnsFalse() {
    Dictionary dict = mock(Dictionary.class);
    when(dict.contains(new StringList("xyz."))).thenReturn(false);

    MaxentModel mockedModel = mock(MaxentModel.class);
    TokenizerFactory factory = mock(TokenizerFactory.class);
    TokenContextGenerator contextGen = mock(TokenContextGenerator.class);

    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[a-z.]+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);
    when(factory.getContextGenerator()).thenReturn(contextGen);

    TokenizerModel model = new TokenizerModel(mockedModel, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(model, dict);

    boolean result = tokenizer.isAcceptableAbbreviation("xyz.");
    assertFalse(result);
  }
@Test
  public void testTokenInAbbrDictReturnsTrue() {
    Dictionary dict = mock(Dictionary.class);
    when(dict.contains(new StringList("e.g."))).thenReturn(true);

    MaxentModel mockedModel = mock(MaxentModel.class);
    TokenizerFactory factory = mock(TokenizerFactory.class);
    TokenContextGenerator contextGen = mock(TokenContextGenerator.class);

    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[a-z.]+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);
    when(factory.getContextGenerator()).thenReturn(contextGen);

    TokenizerModel model = new TokenizerModel(mockedModel, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(model, dict);

    boolean result = tokenizer.isAcceptableAbbreviation("e.g.");
    assertTrue(result);
  }
@Test
  public void testAbbreviationHandlingSkipsSplitForMultipleDots() {
    String input = "U.S.A.";

    MaxentModel mockedModel = mock(MaxentModel.class);
    TokenContextGenerator contextGenerator = mock(TokenContextGenerator.class);
    Dictionary dict = mock(Dictionary.class);
    TokenizerFactory factory = mock(TokenizerFactory.class);

    when(dict.contains(new StringList("U.S.A."))).thenReturn(true);
    when(factory.getContextGenerator()).thenReturn(contextGenerator);
    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[A-Z.]+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);
    when(contextGenerator.getContext(eq("U.S.A."), anyInt())).thenReturn(new String[]{"ctx"});
    when(mockedModel.eval(any())).thenReturn(new double[]{0.25, 0.75});
    when(mockedModel.getBestOutcome(any())).thenReturn(TokenizerME.SPLIT);
    when(mockedModel.getIndex(TokenizerME.SPLIT)).thenReturn(1);

    TokenizerModel model = new TokenizerModel(mockedModel, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(model, dict);

    Span[] spans = tokenizer.tokenizePos(input);
    assertEquals(1, spans.length);
    assertEquals("U.S.A.", input.substring(spans[0].getStart(), spans[0].getEnd()));

    double[] probs = tokenizer.getTokenProbabilities();
    assertEquals(1, probs.length);
    assertTrue(probs[0] > 0);
  }
@Test
  public void testTokenSplitContinuesAfterAbbreviationDotIndex() {
    String input = "Mr.A.B.C.D";

    MaxentModel mockedModel = mock(MaxentModel.class);
    TokenContextGenerator contextGenerator = mock(TokenContextGenerator.class);
    Dictionary dict = mock(Dictionary.class);
    TokenizerFactory factory = mock(TokenizerFactory.class);

    when(dict.contains(new StringList("Mr."))).thenReturn(true);
    when(factory.getContextGenerator()).thenReturn(contextGenerator);
    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[A-Za-z.]+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);
    when(contextGenerator.getContext(anyString(), anyInt())).thenReturn(new String[]{"ctx"});
    when(mockedModel.eval(any())).thenReturn(new double[]{0.25, 0.75});
    when(mockedModel.getBestOutcome(any())).thenReturn(TokenizerME.SPLIT);
    when(mockedModel.getIndex(TokenizerME.SPLIT)).thenReturn(1);

    TokenizerModel model = new TokenizerModel(mockedModel, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(model, dict);

    Span[] spans = tokenizer.tokenizePos(input);
    assertTrue(spans.length >= 2);

    double[] probs = tokenizer.getTokenProbabilities();
    assertEquals(spans.length, probs.length);
    assertTrue(probs[0] > 0);
  }
@Test
  public void testConsecutiveAbbreviationsInSentence() {
    String input = "e.g. i.e. etc.";

    MaxentModel mockedModel = mock(MaxentModel.class);
    TokenContextGenerator contextGenerator = mock(TokenContextGenerator.class);
    Dictionary dict = mock(Dictionary.class);
    TokenizerFactory factory = mock(TokenizerFactory.class);

    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[a-z.]+"));
    when(factory.getContextGenerator()).thenReturn(contextGenerator);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

    when(dict.contains(new StringList("e.g."))).thenReturn(true);
    when(dict.contains(new StringList("i.e."))).thenReturn(true);
    when(dict.contains(new StringList("etc."))).thenReturn(true);
    when(contextGenerator.getContext(anyString(), anyInt())).thenReturn(new String[]{"ctx"});
    when(mockedModel.eval(any())).thenReturn(new double[]{0.1, 0.9});
    when(mockedModel.getBestOutcome(any())).thenReturn(TokenizerME.SPLIT);
    when(mockedModel.getIndex(TokenizerME.SPLIT)).thenReturn(1);

    TokenizerModel model = new TokenizerModel(mockedModel, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(model, dict);

    Span[] spans = tokenizer.tokenizePos(input);
    assertEquals(3, spans.length);
    assertEquals("e.g.", input.substring(spans[0].getStart(), spans[0].getEnd()));
    assertEquals("i.e.", input.substring(spans[1].getStart(), spans[1].getEnd()));
    assertEquals("etc.", input.substring(spans[2].getStart(), spans[2].getEnd()));

    double[] probs = tokenizer.getTokenProbabilities();
    assertEquals(3, probs.length);
  }
@Test
  public void testTokenWithOnlyDigitsAndAlphanumericOptimizationEnabled() {
    String input = "123456";

    MaxentModel mockedModel = mock(MaxentModel.class);
    TokenizerFactory factory = mock(TokenizerFactory.class);
    TokenContextGenerator contextGenerator = mock(TokenContextGenerator.class);

    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("\\d+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);
    when(factory.getContextGenerator()).thenReturn(contextGenerator);

    TokenizerModel model = new TokenizerModel(mockedModel, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(model);

    Span[] spans = tokenizer.tokenizePos(input);
    assertEquals(1, spans.length);
    assertEquals("123456", input.substring(spans[0].getStart(), spans[0].getEnd()));

    double[] probs = tokenizer.getTokenProbabilities();
    assertEquals(1, probs.length);
    assertEquals(1.0, probs[0], 0.001);
  }
@Test
  public void testTokenWithOnlyNonAlphaNumericCharactersAndOptimizationEnabled() {
    String input = "?!";

    MaxentModel mockedModel = mock(MaxentModel.class);
    TokenContextGenerator contextGenerator = mock(TokenContextGenerator.class);
    TokenizerFactory factory = mock(TokenizerFactory.class);

    when(factory.getContextGenerator()).thenReturn(contextGenerator);
    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[a-zA-Z0-9]+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

    TokenizerModel model = new TokenizerModel(mockedModel, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(model);

    Span[] spans = tokenizer.tokenizePos(input);
    assertEquals(1, spans.length);
    assertEquals("?!", input.substring(spans[0].getStart(), spans[0].getEnd()));
    double[] probs = tokenizer.getTokenProbabilities();
    assertEquals(1, probs.length);
    assertEquals(1.0, probs[0], 0.001);
  }
@Test
  public void testMultipleShortNonAlphaTokens() {
    String input = "! ! !";

    MaxentModel mockedModel = mock(MaxentModel.class);
    TokenContextGenerator contextGenerator = mock(TokenContextGenerator.class);
    TokenizerFactory factory = mock(TokenizerFactory.class);

    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[a-zA-Z0-9]+"));
    when(factory.getContextGenerator()).thenReturn(contextGenerator);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

    TokenizerModel model = new TokenizerModel(mockedModel, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(model);

    Span[] spans = tokenizer.tokenizePos(input);
    assertEquals(3, spans.length);
    assertEquals("!", input.substring(spans[0].getStart(), spans[0].getEnd()));
    assertEquals("!", input.substring(spans[1].getStart(), spans[1].getEnd()));
    assertEquals("!", input.substring(spans[2].getStart(), spans[2].getEnd()));

    double[] probs = tokenizer.getTokenProbabilities();
    assertEquals(3, probs.length);
    assertEquals(1.0, probs[0], 0.001);
  }
@Test
  public void testEmptySpanTokenResetsTracking() {
    String input = "a b";

    MaxentModel mockedModel = mock(MaxentModel.class);
    TokenContextGenerator contextGenerator = mock(TokenContextGenerator.class);
    TokenizerFactory factory = mock(TokenizerFactory.class);

    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[a-z]+"));
    when(factory.getContextGenerator()).thenReturn(contextGenerator);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

    TokenizerModel model = new TokenizerModel(mockedModel, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(model);

    Span[] spans1 = tokenizer.tokenizePos(input);
    double[] probs1 = tokenizer.getTokenProbabilities();

    Span[] spans2 = tokenizer.tokenizePos("x");
    double[] probs2 = tokenizer.getTokenProbabilities();

    assertEquals(1, spans2.length);
    assertEquals("x", "x".substring(spans2[0].getStart(), spans2[0].getEnd()));
    assertEquals(1, probs2.length);
    assertEquals(1.0, probs2[0], 0.001);
  }
@Test
  public void testInputWithTabAndSpaceMixedSeparators() {
    String input = "word1\tword2 word3";

    MaxentModel mockedModel = mock(MaxentModel.class);
    TokenContextGenerator contextGenerator = mock(TokenContextGenerator.class);
    TokenizerFactory factory = mock(TokenizerFactory.class);

    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("\\w+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);
    when(factory.getContextGenerator()).thenReturn(contextGenerator);

    TokenizerModel model = new TokenizerModel(mockedModel, new HashMap<String, String>(), factory);
    TokenizerME tokenizer = new TokenizerME(model);
    tokenizer.setKeepNewLines(true);

    Span[] spans = tokenizer.tokenizePos(input);
    assertEquals(3, spans.length);
    assertEquals("word1", input.substring(spans[0].getStart(), spans[0].getEnd()));
    assertEquals("word2", input.substring(spans[1].getStart(), spans[1].getEnd()));
    assertEquals("word3", input.substring(spans[2].getStart(), spans[2].getEnd()));

    double[] probs = tokenizer.getTokenProbabilities();
    assertEquals(3, probs.length);
    assertEquals(1.0, probs[0], 0.001);
    assertEquals(1.0, probs[1], 0.001);
    assertEquals(1.0, probs[2], 0.001);
  }
@Test
  public void testAlphanumericOptimizationFalseUsesModelEvaluation() {
    String input = "abc123";

    MaxentModel mockedModel = mock(MaxentModel.class);
    TokenContextGenerator contextGenerator = mock(TokenContextGenerator.class);
    TokenizerFactory factory = mock(TokenizerFactory.class);

    when(contextGenerator.getContext(eq("abc123"), anyInt())).thenReturn(new String[]{"ctx"});
    when(mockedModel.eval(any())).thenReturn(new double[]{0.2, 0.8});
    when(mockedModel.getBestOutcome(any())).thenReturn(TokenizerME.SPLIT);
    when(mockedModel.getIndex(TokenizerME.SPLIT)).thenReturn(1);

    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[a-zA-Z0-9]+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);
    when(factory.getContextGenerator()).thenReturn(contextGenerator);

    TokenizerModel model = new TokenizerModel(mockedModel, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(model);

    Span[] spans = tokenizer.tokenizePos(input);
    assertTrue(spans.length >= 2);
    double[] probs = tokenizer.getTokenProbabilities();
    assertEquals(spans.length, probs.length);
  }
@Test
  public void testAbbreviationEndsWithMultipleDotsGreedySpan() {
    String input = "A.B.C.D.";

    MaxentModel mockedModel = mock(MaxentModel.class);
    TokenContextGenerator contextGenerator = mock(TokenContextGenerator.class);
    Dictionary dict = mock(Dictionary.class);
    TokenizerFactory factory = mock(TokenizerFactory.class);

    when(contextGenerator.getContext(eq("A.B.C.D."), anyInt())).thenReturn(new String[]{"ctx"});
    when(mockedModel.eval(any())).thenReturn(new double[]{0.3, 0.7});
    when(mockedModel.getBestOutcome(any())).thenReturn(TokenizerME.SPLIT);
    when(mockedModel.getIndex(TokenizerME.SPLIT)).thenReturn(1);
    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[A-Z.]+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);
    when(factory.getContextGenerator()).thenReturn(contextGenerator);
    when(dict.contains(new StringList("A.B.C.D."))).thenReturn(true);

    TokenizerModel model = new TokenizerModel(mockedModel, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(model, dict);

    Span[] spans = tokenizer.tokenizePos(input);
    assertEquals(1, spans.length);
    assertEquals("A.B.C.D.", input.substring(spans[0].getStart(), spans[0].getEnd()));

    double[] probs = tokenizer.getTokenProbabilities();
    assertEquals(1, probs.length);
    assertTrue(probs[0] > 0);
  }
@Test
  public void testTokenContainingEmoji() {
    String input = "hello🙂world";

    MaxentModel mockedModel = mock(MaxentModel.class);
    TokenContextGenerator contextGenerator = mock(TokenContextGenerator.class);
    TokenizerFactory factory = mock(TokenizerFactory.class);

    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[a-zA-Z]+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);
    when(factory.getContextGenerator()).thenReturn(contextGenerator);
    when(contextGenerator.getContext(eq("hello🙂world"), anyInt())).thenReturn(new String[]{"ctx"});
    when(mockedModel.eval(any())).thenReturn(new double[]{0.3, 0.7});
    when(mockedModel.getBestOutcome(any())).thenReturn(TokenizerME.SPLIT);
    when(mockedModel.getIndex(TokenizerME.SPLIT)).thenReturn(1);

    TokenizerModel model = new TokenizerModel(mockedModel, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(model);

    Span[] spans = tokenizer.tokenizePos(input);
    assertNotNull(spans);
    assertTrue(spans.length >= 2);
    double[] probs = tokenizer.getTokenProbabilities();
    assertEquals(spans.length, probs.length);
  }
@Test
  public void testGetTokenProbabilitiesWithoutTokenizeCall() {
    MaxentModel mockedModel = mock(MaxentModel.class);
    TokenizerFactory factory = mock(TokenizerFactory.class);
    TokenContextGenerator contextGenerator = mock(TokenContextGenerator.class);

    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile(".+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);
    when(factory.getContextGenerator()).thenReturn(contextGenerator);

    TokenizerModel model = new TokenizerModel(mockedModel, new HashMap<String, String>(), factory);
    TokenizerME tokenizer = new TokenizerME(model);
    double[] probs = tokenizer.getTokenProbabilities();

    assertNotNull(probs);
    assertEquals(0, probs.length);
  }
@Test
  public void testNewLineHandlingWhenDisabled() {
    String input = "abc\ndef";

    MaxentModel mockedModel = mock(MaxentModel.class);
    TokenizerFactory factory = mock(TokenizerFactory.class);
    TokenContextGenerator contextGenerator = mock(TokenContextGenerator.class);

    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[a-z]+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);
    when(factory.getContextGenerator()).thenReturn(contextGenerator);

    TokenizerModel model = new TokenizerModel(mockedModel, new HashMap<String, String>(), factory);
    TokenizerME tokenizer = new TokenizerME(model);
    tokenizer.setKeepNewLines(false);

    Span[] spans = tokenizer.tokenizePos(input);
    assertEquals(2, spans.length);
    assertEquals("abc", input.substring(spans[0].getStart(), spans[0].getEnd()));
    assertEquals("def", input.substring(spans[1].getStart(), spans[1].getEnd()));

    double[] probs = tokenizer.getTokenProbabilities();
    assertEquals(2, probs.length);
    assertEquals(1.0, probs[0], 0.001);
    assertEquals(1.0, probs[1], 0.001);
  }
@Test
  public void testMixedDigitsAndSymbolsInput() {
    String input = "2023-07-10!";

    MaxentModel mockedModel = mock(MaxentModel.class);
    TokenizerFactory factory = mock(TokenizerFactory.class);
    TokenContextGenerator contextGenerator = mock(TokenContextGenerator.class);

    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[0-9A-Za-z-]+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);
    when(factory.getContextGenerator()).thenReturn(contextGenerator);

    when(contextGenerator.getContext(eq("2023-07-10!"), anyInt())).thenReturn(new String[]{"ctx"});
    when(mockedModel.eval(any())).thenReturn(new double[]{0.2, 0.8});
    when(mockedModel.getBestOutcome(any())).thenReturn(TokenizerME.SPLIT);
    when(mockedModel.getIndex(TokenizerME.SPLIT)).thenReturn(1);

    TokenizerModel model = new TokenizerModel(mockedModel, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(model);

    Span[] spans = tokenizer.tokenizePos(input);
    assertTrue(spans.length > 1);
    double[] probs = tokenizer.getTokenProbabilities();
    assertEquals(spans.length, probs.length);
  } 
}