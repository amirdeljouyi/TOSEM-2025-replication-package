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

public class TokenizerME_llmsuite_3_GPTLLMTest {

 @Test
  public void testTokenizeSingleChar() {
    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator cg = mock(TokenContextGenerator.class);
    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[a-zA-Z0-9]+"));
    when(factory.getContextGenerator()).thenReturn(cg);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel);

    String input = ":";
    Span[] spans = tokenizer.tokenizePos(input);

    assertEquals(1, spans.length);
    assertEquals(":", input.substring(spans[0].getStart(), spans[0].getEnd()));
    assertEquals(1, tokenizer.getTokenProbabilities().length);
    assertEquals(1.0, tokenizer.getTokenProbabilities()[0], 0.0001);
  }
@Test
  public void testTokenizeAlphaNumericOptimizationMatch() {
    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator cg = mock(TokenContextGenerator.class);
    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[a-zA-Z0-9]+"));
    when(factory.getContextGenerator()).thenReturn(cg);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel);

    String input = "Simple Token123 Test";
    Span[] spans = tokenizer.tokenizePos(input);

    assertEquals(3, spans.length);
    assertEquals("Simple", input.substring(spans[0].getStart(), spans[0].getEnd()));
    assertEquals("Token123", input.substring(spans[1].getStart(), spans[1].getEnd()));
    assertEquals("Test", input.substring(spans[2].getStart(), spans[2].getEnd()));
  }
@Test
  public void testTokenizeWithModelSplitDecision() {
    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator cg = mock(TokenContextGenerator.class);

    when(cg.getContext(eq("abc"), eq(1))).thenReturn(new String[]{"c1"});
    when(cg.getContext(eq("abc"), eq(2))).thenReturn(new String[]{"c2"});

    when(model.eval(any())).thenReturn(new double[]{0.1, 0.9});
    when(model.getBestOutcome(any())).thenReturn(TokenizerME.SPLIT);
    when(model.getIndex(TokenizerME.SPLIT)).thenReturn(1);

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[a-zA-Z0-9]+"));
    when(factory.getContextGenerator()).thenReturn(cg);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel);

    String input = "abc";
    Span[] spans = tokenizer.tokenizePos(input);

    assertTrue(spans.length >= 1);
    assertEquals(spans.length, tokenizer.getTokenProbabilities().length);
    assertTrue(tokenizer.getTokenProbabilities()[0] > 0.0);
  }
@Test
  public void testIsAcceptableAbbreviationTrue() {
    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator cg = mock(TokenContextGenerator.class);
    TokenizerFactory factory = mock(TokenizerFactory.class);

    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[a-zA-Z0-9]+"));
    when(factory.getContextGenerator()).thenReturn(cg);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

    Dictionary dict = new Dictionary();
    dict.put(new StringList("e.g."));

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel, dict);

    boolean result = tokenizer.isAcceptableAbbreviation("e.g.");
    assertTrue(result);
  }
@Test
  public void testIsAcceptableAbbreviationFalse_NoDict() {
    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator cg = mock(TokenContextGenerator.class);
    TokenizerFactory factory = mock(TokenizerFactory.class);

    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[a-zA-Z0-9]+"));
    when(factory.getContextGenerator()).thenReturn(cg);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel, null);

    boolean result = tokenizer.isAcceptableAbbreviation("e.g.");
    assertFalse(result);
  }
@Test
  public void testUseAlphaNumericOptimizationTrue() {
    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator cg = mock(TokenContextGenerator.class);
    TokenizerFactory factory = mock(TokenizerFactory.class);

    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[a-zA-Z]+"));
    when(factory.getContextGenerator()).thenReturn(cg);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel);

    assertTrue(tokenizer.useAlphaNumericOptimization());
  }
@Test
  public void testUseAlphaNumericOptimizationFalse() {
    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator cg = mock(TokenContextGenerator.class);
    TokenizerFactory factory = mock(TokenizerFactory.class);

    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[a-zA-Z]+"));
    when(factory.getContextGenerator()).thenReturn(cg);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel);

    assertFalse(tokenizer.useAlphaNumericOptimization());
  }
@Test
  public void testTokenProbabilitiesEmptyBeforeTokenize() {
    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator cg = mock(TokenContextGenerator.class);
    TokenizerFactory factory = mock(TokenizerFactory.class);

    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[a-zA-Z]+"));
    when(factory.getContextGenerator()).thenReturn(cg);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel);

    double[] probs = tokenizer.getTokenProbabilities();
    assertNotNull(probs);
    assertEquals(0, probs.length);
  }
@Test
  public void testTokenizeMultipleWhitespace() {
    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator cg = mock(TokenContextGenerator.class);
    TokenizerFactory factory = mock(TokenizerFactory.class);

    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[a-zA-Z0-9]+"));
    when(factory.getContextGenerator()).thenReturn(cg);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel);

    String input = "word1    word2   word3";
    Span[] spans = tokenizer.tokenizePos(input);

    assertEquals(3, spans.length);
    assertEquals("word1", input.substring(spans[0].getStart(), spans[0].getEnd()));
    assertEquals("word2", input.substring(spans[1].getStart(), spans[1].getEnd()));
    assertEquals("word3", input.substring(spans[2].getStart(), spans[2].getEnd()));
  }
@Test
  public void testConstructTokenizerMEFromTokenizerModel() {
    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator cg = mock(TokenContextGenerator.class);
    TokenizerFactory factory = mock(TokenizerFactory.class);

    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[a-zA-Z0-9]+"));
    when(factory.getContextGenerator()).thenReturn(cg);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

    Map<String, String> manifest = new HashMap<>();
    TokenizerModel tokenizerModel = new TokenizerModel(model, manifest, factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel);

    assertNotNull(tokenizer);
  }
@Test
  public void testConstructorWithLanguageThrowsIOExceptionForInvalidLang() {
    boolean exceptionThrown = false;

    try {
      new TokenizerME("invalidlang");
    } catch (IOException e) {
      exceptionThrown = true;
    }

    assertTrue(exceptionThrown);
  }
@Test
  public void testTokenizeEmptyInput() {
    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator cg = mock(TokenContextGenerator.class);
    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[a-zA-Z0-9]+"));
    when(factory.getContextGenerator()).thenReturn(cg);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel);

    String input = "";
    Span[] spans = tokenizer.tokenizePos(input);

    assertEquals(0, spans.length);
    assertEquals(0, tokenizer.getTokenProbabilities().length);
  }
@Test
  public void testTokenizeOnlyWhitespaceInput() {
    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator cg = mock(TokenContextGenerator.class);
    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[a-zA-Z0-9]+"));
    when(factory.getContextGenerator()).thenReturn(cg);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel);

    String input = "   \t   \n";
    Span[] spans = tokenizer.tokenizePos(input);

    assertEquals(0, spans.length);
    assertEquals(0, tokenizer.getTokenProbabilities().length);
  }
@Test
  public void testIsAcceptableAbbreviationFalse_EmptyString() {
    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator cg = mock(TokenContextGenerator.class);
    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[a-zA-Z]+"));
    when(factory.getContextGenerator()).thenReturn(cg);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

    Dictionary dict = new Dictionary();
    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel, dict);

    boolean result = tokenizer.isAcceptableAbbreviation("");
    assertFalse(result);
  }
@Test
  public void testTokenizeWithPunctuationAndSplits() {
    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator cg = mock(TokenContextGenerator.class);

    when(cg.getContext(eq("Hi,there!"), eq(2))).thenReturn(new String[]{"c1"});  
    when(cg.getContext(eq("Hi,there!"), eq(7))).thenReturn(new String[]{"c2"});  

    when(model.eval(any())).thenReturn(new double[]{0.3, 0.7});
    when(model.getBestOutcome(any())).thenReturn(TokenizerME.SPLIT);
    when(model.getIndex(TokenizerME.SPLIT)).thenReturn(1);

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[a-zA-Z0-9]+"));
    when(factory.getContextGenerator()).thenReturn(cg);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel);

    String input = "Hi,there!";
    Span[] spans = tokenizer.tokenizePos(input);
    assertTrue(spans.length > 1);
    assertEquals(spans.length, tokenizer.getTokenProbabilities().length);
  }
@Test
  public void testTokenizeNumberWithDots_NotAbbreviation() {
    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator cg = mock(TokenContextGenerator.class);

    when(cg.getContext(eq("3.14.15"), eq(1))).thenReturn(new String[]{"c1"});
    when(cg.getContext(eq("3.14.15"), eq(4))).thenReturn(new String[]{"c2"});

    when(model.eval(any())).thenReturn(new double[]{0.4, 0.6});
    when(model.getBestOutcome(any())).thenReturn(TokenizerME.SPLIT);
    when(model.getIndex(TokenizerME.SPLIT)).thenReturn(1);

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[0-9.]+"));
    when(factory.getContextGenerator()).thenReturn(cg);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel);

    String input = "3.14.15";
    Span[] spans = tokenizer.tokenizePos(input);
    assertTrue(spans.length > 1);
    assertEquals(spans.length, tokenizer.getTokenProbabilities().length);
  }
@Test
  public void testMultipleTokensOneIsSingleChar() {
    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator cg = mock(TokenContextGenerator.class);

    when(cg.getContext(any(), anyInt())).thenReturn(new String[]{"context"});
    when(model.eval(any())).thenReturn(new double[]{0.45, 0.55});
    when(model.getBestOutcome(any())).thenReturn(TokenizerME.NO_SPLIT);
    when(model.getIndex(TokenizerME.NO_SPLIT)).thenReturn(0);

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[a-zA-Z0-9]+"));
    when(factory.getContextGenerator()).thenReturn(cg);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel);

    String input = "one a test";
    Span[] spans = tokenizer.tokenizePos(input);
    assertEquals(3, spans.length);
    assertEquals("one", input.substring(spans[0].getStart(), spans[0].getEnd()));
    assertEquals("a", input.substring(spans[1].getStart(), spans[1].getEnd()));
    assertEquals("test", input.substring(spans[2].getStart(), spans[2].getEnd()));
  }
@Test
  public void testTokenizePunctuationOnly() {
    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator cg = mock(TokenContextGenerator.class);

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[a-zA-Z0-9]+"));
    when(factory.getContextGenerator()).thenReturn(cg);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel);

    String input = "!!";
    Span[] spans = tokenizer.tokenizePos(input);
    assertEquals(1, spans.length);
    assertEquals("!!", input.substring(spans[0].getStart(), spans[0].getEnd()));
    assertEquals(1.0, tokenizer.getTokenProbabilities()[0], 0.00001);
  }
@Test
  public void testGetTokenProbabilitiesAfterMultipleCalls() {
    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator cg = mock(TokenContextGenerator.class);
    TokenizerFactory factory = mock(TokenizerFactory.class);

    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[a-zA-Z]+"));
    when(factory.getContextGenerator()).thenReturn(cg);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel);

    String input1 = "TokenA TokenB";
    Span[] spans1 = tokenizer.tokenizePos(input1);
    double[] probs1 = tokenizer.getTokenProbabilities();

    String input2 = "TokenC TokenD";
    Span[] spans2 = tokenizer.tokenizePos(input2);
    double[] probs2 = tokenizer.getTokenProbabilities();

    assertEquals(spans2.length, probs2.length);
    assertNotEquals(probs1.length, probs2.length); 
  }
@Test
  public void testTokenizeLongContinuousTokenWithNoSplits() {
    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator cg = mock(TokenContextGenerator.class);

    when(model.eval(any())).thenReturn(new double[]{0.9, 0.1});
    when(model.getBestOutcome(any())).thenReturn(TokenizerME.NO_SPLIT);
    when(model.getIndex(TokenizerME.NO_SPLIT)).thenReturn(0);

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[a-zA-Z0-9]+"));
    when(factory.getContextGenerator()).thenReturn(cg);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel);

    String input = "Supercalifragilisticexpialidocious";
    Span[] spans = tokenizer.tokenizePos(input);

    assertEquals(1, spans.length);
    assertEquals("Supercalifragilisticexpialidocious", input.substring(spans[0].getStart(), spans[0].getEnd()));
    assertEquals(1, tokenizer.getTokenProbabilities().length);
  }
@Test
  public void testTokenizeMultipleSplitPointsInSameToken() {
    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator cg = mock(TokenContextGenerator.class);

    when(cg.getContext(eq("helloworldagain"), eq(4))).thenReturn(new String[]{"ctx1"});
    when(cg.getContext(eq("helloworldagain"), eq(9))).thenReturn(new String[]{"ctx2"});
    when(model.eval(any())).thenReturn(new double[]{0.3, 0.7});
    when(model.getBestOutcome(any())).thenReturn(TokenizerME.SPLIT);
    when(model.getIndex(any())).thenReturn(1);

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[a-zA-Z]+"));
    when(factory.getContextGenerator()).thenReturn(cg);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel);

    String input = "helloworldagain";
    Span[] spans = tokenizer.tokenizePos(input);

    assertTrue(spans.length >= 2);
    assertEquals(spans.length, tokenizer.getTokenProbabilities().length);
  }
@Test
  public void testTokenizeNewLinesKeptAsWhitespaceTokens() {
    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator cg = mock(TokenContextGenerator.class);
    TokenizerFactory factory = mock(TokenizerFactory.class);

    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[a-zA-Z0-9]+"));
    when(factory.getContextGenerator()).thenReturn(cg);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);

    TokenizerME tokenizer = new TokenizerME(tokenizerModel);
    tokenizer.setKeepNewLines(true);

    String input = "Hello\nWorld\n";
    Span[] spans = tokenizer.tokenizePos(input);

    assertEquals(2, spans.length);
    assertEquals("Hello", input.substring(spans[0].getStart(), spans[0].getEnd()));
    assertEquals("World", input.substring(spans[1].getStart(), spans[1].getEnd()));
  }
@Test
  public void testTrainReturnsValidTokenizerModel() throws Exception {
    ObjectStream<TokenSample> sampleStream = mock(ObjectStream.class);
    TokenizerFactory factory = mock(TokenizerFactory.class);
    TokenContextGenerator cg = mock(TokenContextGenerator.class);
    Pattern alphaNumPattern = Pattern.compile("[a-zA-Z]+");

    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);
    when(factory.getContextGenerator()).thenReturn(cg);
    when(factory.getAlphaNumericPattern()).thenReturn(alphaNumPattern);

    TrainingParameters params = new TrainingParameters();
    params.put(TrainingParameters.ALGORITHM_PARAM, "MAXENT");
    params.put(TrainingParameters.CUTOFF_PARAM, "1");
    params.put(TrainingParameters.ITERATIONS_PARAM, "10");

    TokenizerModel model = TokenizerME.train(sampleStream, factory, params);

    assertNotNull(model);
    assertNotNull(model.getMaxentModel());
    assertEquals(factory, model.getFactory());
  }
@Test
  public void testTokenizeWithAbbreviationContainingMultipleDots() {
    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator cg = mock(TokenContextGenerator.class);

    when(cg.getContext(eq("U.S.A."), eq(1))).thenReturn(new String[]{"ctx1"});
    when(cg.getContext(eq("U.S.A."), eq(3))).thenReturn(new String[]{"ctx2"});
    when(model.eval(any())).thenReturn(new double[]{0.2, 0.8});
    when(model.getBestOutcome(any())).thenReturn(TokenizerME.SPLIT);
    when(model.getIndex(any())).thenReturn(1);

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[a-zA-Z.]+"));
    when(factory.getContextGenerator()).thenReturn(cg);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

    Dictionary dict = new Dictionary();
    dict.put(new StringList("U.S.A."));

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel, dict);

    String input = "U.S.A.";
    Span[] spans = tokenizer.tokenizePos(input);

    assertEquals(1, spans.length);
    assertEquals("U.S.A.", input.substring(spans[0].getStart(), spans[0].getEnd()));
    assertEquals(1, tokenizer.getTokenProbabilities().length);
  }
@Test
  public void testTokenizeSingleTokenWithSplitAtFirstCharIgnored() {
    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator cg = mock(TokenContextGenerator.class);

    when(cg.getContext(eq("thing"), eq(1))).thenReturn(new String[]{"ctx1"});
    when(model.eval(any())).thenReturn(new double[]{0.5, 0.5});
    when(model.getBestOutcome(any())).thenReturn(TokenizerME.SPLIT);
    when(model.getIndex(any())).thenReturn(1);

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[a-zA-Z]+"));
    when(factory.getContextGenerator()).thenReturn(cg);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel);

    String input = "thing";
    Span[] spans = tokenizer.tokenizePos(input);

    assertTrue(spans.length >= 1);
    for (int i = 0; i < spans.length; i++) {
      assertTrue(spans[i].getStart() < spans[i].getEnd());
    }
    assertEquals(spans.length, tokenizer.getTokenProbabilities().length);
  }
@Test
  public void testTokenizeTokenExactlyTwoCharacters() {
    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator cg = mock(TokenContextGenerator.class);

    when(cg.getContext(eq("Hi"), eq(1))).thenReturn(new String[]{"ctx1"});
    when(model.eval(any())).thenReturn(new double[]{0.6, 0.4});
    when(model.getBestOutcome(any())).thenReturn(TokenizerME.NO_SPLIT);
    when(model.getIndex(any())).thenReturn(0);

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[a-zA-Z]+"));
    when(factory.getContextGenerator()).thenReturn(cg);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel);

    String input = "Hi";
    Span[] spans = tokenizer.tokenizePos(input);

    assertEquals(1, spans.length);
    assertEquals("Hi", input.substring(spans[0].getStart(), spans[0].getEnd()));
    assertEquals(1, tokenizer.getTokenProbabilities().length);
  }
@Test
public void testTokenizeWithSingleNonAlphanumericCharAndNoOptimization() {
  MaxentModel model = mock(MaxentModel.class);
  TokenContextGenerator cg = mock(TokenContextGenerator.class);
  TokenizerFactory factory = mock(TokenizerFactory.class);

  when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[a-zA-Z0-9]+"));
  when(factory.getContextGenerator()).thenReturn(cg);
  when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

  TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
  TokenizerME tokenizer = new TokenizerME(tokenizerModel);

  String input = "$";
  Span[] spans = tokenizer.tokenizePos(input);

  assertEquals(1, spans.length);
  assertEquals("$", input.substring(spans[0].getStart(), spans[0].getEnd()));
  assertEquals(1, tokenizer.getTokenProbabilities().length);
  assertEquals(1.0, tokenizer.getTokenProbabilities()[0], 0.00001);
}
@Test
public void testTokenizeWithMixedAlphaNumericAndPunctuation_ToggleOptimization() {
  MaxentModel model = mock(MaxentModel.class);
  TokenContextGenerator cg = mock(TokenContextGenerator.class);

  when(model.eval(any())).thenReturn(new double[]{0.2, 0.8});
  when(model.getBestOutcome(any())).thenReturn(TokenizerME.SPLIT);
  when(model.getIndex(eq(TokenizerME.SPLIT))).thenReturn(1);

  TokenizerFactory factory = mock(TokenizerFactory.class);
  when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[a-zA-Z0-9]+"));
  when(factory.getContextGenerator()).thenReturn(cg);
  when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

  TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
  TokenizerME tokenizer = new TokenizerME(tokenizerModel);

  String input = "abc,def.ghi";
  Span[] spans = tokenizer.tokenizePos(input);

  assertTrue(spans.length >= 2);
  for (int i = 0; i < spans.length; i++) {
    int start = spans[i].getStart();
    int end = spans[i].getEnd();
    assertTrue(start < end);
    assertTrue(end <= input.length());
  }
  assertEquals(spans.length, tokenizer.getTokenProbabilities().length);
}
@Test
public void testAbbreviationDictNullBehavior() {
  MaxentModel model = mock(MaxentModel.class);
  TokenContextGenerator cg = mock(TokenContextGenerator.class);
  TokenizerFactory factory = mock(TokenizerFactory.class);

  when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[a-zA-Z.]+"));
  when(factory.getContextGenerator()).thenReturn(cg);
  when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

  TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
  TokenizerME tokenizer = new TokenizerME(tokenizerModel, null);

  boolean result = tokenizer.isAcceptableAbbreviation("e.g.");
  assertFalse(result);
}
@Test
public void testTokenizeSpanComparisonWithWhitespaceTokenizerBehavior() {
  MaxentModel model = mock(MaxentModel.class);
  TokenContextGenerator cg = mock(TokenContextGenerator.class);
  TokenizerFactory factory = mock(TokenizerFactory.class);

  when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[a-zA-Z]+"));
  when(factory.getContextGenerator()).thenReturn(cg);
  when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

  TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
  TokenizerME tokenizer = new TokenizerME(tokenizerModel);

  String input = "  leading and   trailing  ";
  Span[] spans = tokenizer.tokenizePos(input);

  assertEquals(3, spans.length);
  assertEquals("leading", input.substring(spans[0].getStart(), spans[0].getEnd()));
  assertEquals("and", input.substring(spans[1].getStart(), spans[1].getEnd()));
  assertEquals("trailing", input.substring(spans[2].getStart(), spans[2].getEnd()));
}
@Test
public void testTokenizeAbbreviationIsSplitIfNotInDictionary() {
  MaxentModel model = mock(MaxentModel.class);
  TokenContextGenerator cg = mock(TokenContextGenerator.class);

  
  when(model.eval(any())).thenReturn(new double[]{0.2, 0.8});
  when(model.getBestOutcome(any())).thenReturn(TokenizerME.SPLIT);
  when(model.getIndex(TokenizerME.SPLIT)).thenReturn(1);

  TokenizerFactory factory = mock(TokenizerFactory.class);
  when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[.a-zA-Z]+"));
  when(factory.getContextGenerator()).thenReturn(cg);
  when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

  Dictionary dict = new Dictionary(); 
  TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
  TokenizerME tokenizer = new TokenizerME(tokenizerModel, dict);

  String input = "Ph.D.";
  Span[] spans = tokenizer.tokenizePos(input);

  assertTrue(spans.length > 1);
  assertEquals(spans.length, tokenizer.getTokenProbabilities().length);
}
@Test
public void testTokenizeWithRealisticTextContainingAbbreviationsAndNumbers() {
  MaxentModel model = mock(MaxentModel.class);
  TokenContextGenerator cg = mock(TokenContextGenerator.class);

  when(model.eval(any())).thenReturn(new double[]{0.1, 0.9});
  when(model.getBestOutcome(any())).thenReturn(TokenizerME.SPLIT);
  when(model.getIndex(TokenizerME.SPLIT)).thenReturn(1);

  Dictionary dict = new Dictionary();
  dict.put(new StringList("Dr."));
  dict.put(new StringList("Mr."));

  TokenizerFactory factory = mock(TokenizerFactory.class);
  when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[a-zA-Z0-9.]+"));
  when(factory.getContextGenerator()).thenReturn(cg);
  when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

  TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
  TokenizerME tokenizer = new TokenizerME(tokenizerModel, dict);

  String input = "Dr. Smith met Mr. Taylor on 5.6.2022.";
  Span[] spans = tokenizer.tokenizePos(input);

  assertTrue(spans.length > 4);
  assertEquals(spans.length, tokenizer.getTokenProbabilities().length);
}
@Test
public void testTokenizePosWithUnicodeCharacters() {
  MaxentModel model = mock(MaxentModel.class);
  TokenContextGenerator cg = mock(TokenContextGenerator.class);

  when(model.eval(any())).thenReturn(new double[]{0.4, 0.6});
  when(model.getBestOutcome(any())).thenReturn(TokenizerME.NO_SPLIT);
  when(model.getIndex(eq(TokenizerME.NO_SPLIT))).thenReturn(0);

  TokenizerFactory factory = mock(TokenizerFactory.class);
  when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[\\p{L}\\p{N}]+")); 
  when(factory.getContextGenerator()).thenReturn(cg);
  when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

  TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
  TokenizerME tokenizer = new TokenizerME(tokenizerModel);

  String input = "こんにちは 世界";
  Span[] spans = tokenizer.tokenizePos(input);

  assertEquals(2, spans.length);
  assertEquals("こんにちは", input.substring(spans[0].getStart(), spans[0].getEnd()));
  assertEquals("世界", input.substring(spans[1].getStart(), spans[1].getEnd()));
  assertEquals(spans.length, tokenizer.getTokenProbabilities().length);
}
@Test
public void testTokenizeInputThatStartsAndEndsWithSpaces() {
  MaxentModel model = mock(MaxentModel.class);
  TokenContextGenerator cg = mock(TokenContextGenerator.class);

  TokenizerFactory factory = mock(TokenizerFactory.class);
  when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[a-zA-Z]+"));
  when(factory.getContextGenerator()).thenReturn(cg);
  when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

  TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
  TokenizerME tokenizer = new TokenizerME(tokenizerModel);

  String input = "  Hello World  ";
  Span[] spans = tokenizer.tokenizePos(input);

  assertEquals(2, spans.length);
  assertEquals("Hello", input.substring(spans[0].getStart(), spans[0].getEnd()));
  assertEquals("World", input.substring(spans[1].getStart(), spans[1].getEnd()));
  assertEquals(spans.length, tokenizer.getTokenProbabilities().length);
}
@Test
public void testTokenizeWithComplexPunctuationPatterns() {
  MaxentModel model = mock(MaxentModel.class);
  TokenContextGenerator cg = mock(TokenContextGenerator.class);

  when(cg.getContext(eq("well...yes!"), eq(4))).thenReturn(new String[]{"ctx1"});
  when(cg.getContext(eq("well...yes!"), eq(7))).thenReturn(new String[]{"ctx2"});
  when(model.eval(any())).thenReturn(new double[]{0.3, 0.7});
  when(model.getBestOutcome(any())).thenReturn(TokenizerME.SPLIT);
  when(model.getIndex(eq(TokenizerME.SPLIT))).thenReturn(1);

  TokenizerFactory factory = mock(TokenizerFactory.class);
  when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[a-zA-Z.]+"));
  when(factory.getContextGenerator()).thenReturn(cg);
  when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

  TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
  TokenizerME tokenizer = new TokenizerME(tokenizerModel);

  String input = "well...yes!";
  Span[] spans = tokenizer.tokenizePos(input);

  assertTrue(spans.length >= 2);
  assertEquals(spans.length, tokenizer.getTokenProbabilities().length);
  for (int i = 0; i < spans.length; i++) {
    int start = spans[i].getStart();
    int end = spans[i].getEnd();
    assertTrue(start < end);
  }
}
@Test
public void testGetTokenProbabilitiesCalledBeforeAnyTokenizeCallReturnsEmptyArray() {
  MaxentModel model = mock(MaxentModel.class);
  TokenContextGenerator cg = mock(TokenContextGenerator.class);

  TokenizerFactory factory = mock(TokenizerFactory.class);
  when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[a-zA-Z]+"));
  when(factory.getContextGenerator()).thenReturn(cg);
  when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

  TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
  TokenizerME tokenizer = new TokenizerME(tokenizerModel);

  double[] probabilities = tokenizer.getTokenProbabilities();

  assertNotNull(probabilities);
  assertEquals(0, probabilities.length);
}
@Test
public void testTokenizeMultipleSpacesBetweenWordsWithoutOptimization() {
  MaxentModel model = mock(MaxentModel.class);
  TokenContextGenerator cg = mock(TokenContextGenerator.class);

  TokenizerFactory factory = mock(TokenizerFactory.class);
  when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[a-zA-Z]+"));
  when(factory.getContextGenerator()).thenReturn(cg);
  when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

  when(model.eval(any())).thenReturn(new double[]{0.5, 0.5});
  when(model.getBestOutcome(any())).thenReturn(TokenizerME.NO_SPLIT);
  when(model.getIndex(TokenizerME.NO_SPLIT)).thenReturn(0);

  TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
  TokenizerME tokenizer = new TokenizerME(tokenizerModel);

  String input = "word1     word2";
  Span[] spans = tokenizer.tokenizePos(input);

  assertEquals(2, spans.length);
  assertEquals("word1", input.substring(spans[0].getStart(), spans[0].getEnd()));
  assertEquals("word2", input.substring(spans[1].getStart(), spans[1].getEnd()));
  assertEquals(2, tokenizer.getTokenProbabilities().length);
}
@Test
public void testTokenizeWithSplitOnEveryCharacterAfterFirst() {
  MaxentModel model = mock(MaxentModel.class);
  TokenContextGenerator cg = mock(TokenContextGenerator.class);

  when(model.eval(any())).thenReturn(new double[]{0.1, 0.9});
  when(model.getBestOutcome(any())).thenReturn(TokenizerME.SPLIT);
  when(model.getIndex(eq(TokenizerME.SPLIT))).thenReturn(1);
  when(cg.getContext(anyString(), anyInt())).thenReturn(new String[]{"ctx"});

  TokenizerFactory factory = mock(TokenizerFactory.class);
  when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[a-z]+"));
  when(factory.getContextGenerator()).thenReturn(cg);
  when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

  TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
  TokenizerME tokenizer = new TokenizerME(tokenizerModel);

  String input = "abc";
  Span[] spans = tokenizer.tokenizePos(input);

  assertEquals(3, spans.length);
  assertEquals("a", input.substring(spans[0].getStart(), spans[0].getEnd()));
  assertEquals("b", input.substring(spans[1].getStart(), spans[1].getEnd()));
  assertEquals("c", input.substring(spans[2].getStart(), spans[2].getEnd()));
  assertEquals(3, tokenizer.getTokenProbabilities().length);
}
@Test
public void testTokenizeWithNewLineAndKeepNewLineTrue() {
  MaxentModel model = mock(MaxentModel.class);
  TokenContextGenerator cg = mock(TokenContextGenerator.class);

  TokenizerFactory factory = mock(TokenizerFactory.class);
  when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[\\w]+"));
  when(factory.getContextGenerator()).thenReturn(cg);
  when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

  TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
  TokenizerME tokenizer = new TokenizerME(tokenizerModel);

  tokenizer.setKeepNewLines(true);

  String input = "first\nsecond";
  Span[] spans = tokenizer.tokenizePos(input);

  assertEquals(2, spans.length);
  assertEquals("first", input.substring(spans[0].getStart(), spans[0].getEnd()));
  assertEquals("second", input.substring(spans[1].getStart(), spans[1].getEnd()));
}
@Test
public void testTokenizeOnlyTabCharacter() {
  MaxentModel model = mock(MaxentModel.class);
  TokenContextGenerator cg = mock(TokenContextGenerator.class);

  TokenizerFactory factory = mock(TokenizerFactory.class);
  when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("\\w+"));
  when(factory.getContextGenerator()).thenReturn(cg);
  when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

  TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
  TokenizerME tokenizer = new TokenizerME(tokenizerModel);

  String input = "\t";
  Span[] spans = tokenizer.tokenizePos(input);

  assertEquals(0, spans.length);
  assertEquals(0, tokenizer.getTokenProbabilities().length);
}
@Test
public void testTokenizePosWithLargeInputIncludingNonPrintableChars() {
  MaxentModel model = mock(MaxentModel.class);
  TokenContextGenerator cg = mock(TokenContextGenerator.class);
  when(model.eval(any())).thenReturn(new double[] {0.1, 0.9});
  when(model.getBestOutcome(any())).thenReturn(TokenizerME.SPLIT);
  when(model.getIndex(TokenizerME.SPLIT)).thenReturn(1);
  when(cg.getContext(anyString(), anyInt())).thenReturn(new String[] {"ctx"});

  TokenizerFactory factory = mock(TokenizerFactory.class);
  when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[\\p{Print}]+"));
  when(factory.getContextGenerator()).thenReturn(cg);
  when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

  TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
  TokenizerME tokenizer = new TokenizerME(tokenizerModel);

  String input = "This\u0000is\u0007a\u0008test\u000Cinput.";
  Span[] spans = tokenizer.tokenizePos(input);

  assertTrue(spans.length > 1);
  assertEquals(spans.length, tokenizer.getTokenProbabilities().length);
}
@Test
public void testTokenizeStringWithLineFeedAndCarriageReturnMix() {
  MaxentModel model = mock(MaxentModel.class);
  TokenContextGenerator cg = mock(TokenContextGenerator.class);

  TokenizerFactory factory = mock(TokenizerFactory.class);
  when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("\\w+"));
  when(factory.getContextGenerator()).thenReturn(cg);
  when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

  TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
  TokenizerME tokenizer = new TokenizerME(tokenizerModel);
  tokenizer.setKeepNewLines(true);

  String input = "line1\rline2\nline3\r\nline4";
  Span[] spans = tokenizer.tokenizePos(input);

  assertEquals(4, spans.length);
  assertEquals("line1", input.substring(spans[0].getStart(), spans[0].getEnd()));
  assertEquals("line2", input.substring(spans[1].getStart(), spans[1].getEnd()));
  assertEquals("line3", input.substring(spans[2].getStart(), spans[2].getEnd()));
  assertEquals("line4", input.substring(spans[3].getStart(), spans[3].getEnd()));
}
@Test
public void testSplitModelReturnsUnrecognizedOutcome() {
  MaxentModel model = mock(MaxentModel.class);
  TokenContextGenerator cg = mock(TokenContextGenerator.class);

  when(model.eval(any())).thenReturn(new double[] {0.6, 0.4});
  when(model.getBestOutcome(any())).thenReturn("UNKNOWN");
  when(model.getIndex("UNKNOWN")).thenReturn(0);
  when(cg.getContext(anyString(), anyInt())).thenReturn(new String[] {"ctx"});

  TokenizerFactory factory = mock(TokenizerFactory.class);
  when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("\\w+"));
  when(factory.getContextGenerator()).thenReturn(cg);
  when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

  TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
  TokenizerME tokenizer = new TokenizerME(tokenizerModel);

  String input = "token";
  Span[] spans = tokenizer.tokenizePos(input);

  assertEquals(1, spans.length);
  assertEquals("token", input.substring(spans[0].getStart(), spans[0].getEnd()));
  assertEquals(spans.length, tokenizer.getTokenProbabilities().length);
}
@Test
public void testTokenizeWithNoWhitespaceBetweenTokens() {
  MaxentModel model = mock(MaxentModel.class);
  TokenContextGenerator cg = mock(TokenContextGenerator.class);

  when(model.eval(any())).thenReturn(new double[] {0.1, 0.9});
  when(model.getBestOutcome(any())).thenReturn(TokenizerME.SPLIT);
  when(model.getIndex(TokenizerME.SPLIT)).thenReturn(1);
  when(cg.getContext(anyString(), anyInt())).thenReturn(new String[] {"ctx"});

  TokenizerFactory factory = mock(TokenizerFactory.class);
  when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[a-zA-Z]+"));
  when(factory.getContextGenerator()).thenReturn(cg);
  when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

  TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
  TokenizerME tokenizer = new TokenizerME(tokenizerModel);

  String input = "onetwo";
  Span[] spans = tokenizer.tokenizePos(input);

  assertTrue(spans.length >= 1);
  assertEquals(spans.length, tokenizer.getTokenProbabilities().length);
}
@Test
public void testTokenizeOnlyEmojis() {
  MaxentModel model = mock(MaxentModel.class);
  TokenContextGenerator cg = mock(TokenContextGenerator.class);

  TokenizerFactory factory = mock(TokenizerFactory.class);
  when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile(".+")); 
  when(factory.getContextGenerator()).thenReturn(cg);
  when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

  TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
  TokenizerME tokenizer = new TokenizerME(tokenizerModel);

  String input = "😀😁😂";
  Span[] spans = tokenizer.tokenizePos(input);

  assertEquals(1, spans.length);
  assertEquals(input, input.substring(spans[0].getStart(), spans[0].getEnd()));
  assertEquals(1, tokenizer.getTokenProbabilities().length);
  assertEquals(1.0, tokenizer.getTokenProbabilities()[0], 0.0001);
}
@Test
public void testEmptyModelEvalReturnsEmptyArray() {
  MaxentModel model = mock(MaxentModel.class);
  TokenContextGenerator cg = mock(TokenContextGenerator.class);

  when(model.eval(any())).thenReturn(new double[] {});
  when(model.getBestOutcome(any())).thenReturn(TokenizerME.NO_SPLIT);
  when(model.getIndex(TokenizerME.NO_SPLIT)).thenReturn(0);
  when(cg.getContext(anyString(), anyInt())).thenReturn(new String[]{"ctx"});

  TokenizerFactory factory = mock(TokenizerFactory.class);
  when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("\\w+"));
  when(factory.getContextGenerator()).thenReturn(cg);
  when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

  TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
  TokenizerME tokenizer = new TokenizerME(tokenizerModel);

  String input = "token";
  Span[] spans = tokenizer.tokenizePos(input);

  assertEquals(1, spans.length);
  assertEquals("token", input.substring(spans[0].getStart(), spans[0].getEnd()));
}
@Test
public void testTokenizeMultipleAbbreviationsBackToBack() {
  MaxentModel model = mock(MaxentModel.class);
  TokenContextGenerator cg = mock(TokenContextGenerator.class);

  when(model.eval(any())).thenReturn(new double[]{0.2, 0.8});
  when(model.getBestOutcome(any())).thenReturn(TokenizerME.SPLIT);
  when(model.getIndex(TokenizerME.SPLIT)).thenReturn(1);
  when(cg.getContext(anyString(), anyInt())).thenReturn(new String[]{"ctx"});

  Dictionary dict = new Dictionary();
  dict.put(new StringList("U.S."));
  dict.put(new StringList("D.C."));

  TokenizerFactory factory = mock(TokenizerFactory.class);
  when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[a-zA-Z.]+"));
  when(factory.getContextGenerator()).thenReturn(cg);
  when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

  TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
  TokenizerME tokenizer = new TokenizerME(tokenizerModel, dict);

  String input = "U.S.D.C.";
  Span[] spans = tokenizer.tokenizePos(input);

  assertEquals(1, spans.length);
  assertEquals("U.S.D.C.", input.substring(spans[0].getStart(), spans[0].getEnd()));
  assertEquals(1, tokenizer.getTokenProbabilities().length);
}
@Test
public void testTokenizeInputWithDigitFollowedByPunctuation() {
  MaxentModel model = mock(MaxentModel.class);
  TokenContextGenerator cg = mock(TokenContextGenerator.class);

  when(model.eval(any())).thenReturn(new double[]{0.5, 0.5});
  when(model.getBestOutcome(any())).thenReturn(TokenizerME.SPLIT);
  when(model.getIndex(TokenizerME.SPLIT)).thenReturn(1);
  when(cg.getContext(anyString(), anyInt())).thenReturn(new String[]{"ctx"});

  TokenizerFactory factory = mock(TokenizerFactory.class);
  when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[0-9\\.!]+"));
  when(factory.getContextGenerator()).thenReturn(cg);
  when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

  TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
  TokenizerME tokenizer = new TokenizerME(tokenizerModel);

  String input = "3.14!";
  Span[] spans = tokenizer.tokenizePos(input);

  assertTrue(spans.length > 1);
  assertEquals(spans.length, tokenizer.getTokenProbabilities().length);
}
@Test
public void testTokenizeWithNonAsciiLetters() {
  MaxentModel model = mock(MaxentModel.class);
  TokenContextGenerator cg = mock(TokenContextGenerator.class);

  TokenizerFactory factory = mock(TokenizerFactory.class);
  when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[\\p{L}]+"));
  when(factory.getContextGenerator()).thenReturn(cg);
  when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

  TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
  TokenizerME tokenizer = new TokenizerME(tokenizerModel);

  String input = "naïve résumé café";
  Span[] spans = tokenizer.tokenizePos(input);

  assertEquals(3, spans.length);
  assertEquals("naïve", input.substring(spans[0].getStart(), spans[0].getEnd()));
  assertEquals("résumé", input.substring(spans[1].getStart(), spans[1].getEnd()));
  assertEquals("café", input.substring(spans[2].getStart(), spans[2].getEnd()));
  assertEquals(3, tokenizer.getTokenProbabilities().length);
}
@Test
public void testTokenizeWithNumericSequenceOnly() {
  MaxentModel model = mock(MaxentModel.class);
  TokenContextGenerator cg = mock(TokenContextGenerator.class);

  TokenizerFactory factory = mock(TokenizerFactory.class);
  when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[0-9]+"));
  when(factory.getContextGenerator()).thenReturn(cg);
  when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

  TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
  TokenizerME tokenizer = new TokenizerME(tokenizerModel);

  String input = "1234567890";
  Span[] spans = tokenizer.tokenizePos(input);

  assertEquals(1, spans.length);
  assertEquals("1234567890", input.substring(spans[0].getStart(), spans[0].getEnd()));
  assertEquals(1, tokenizer.getTokenProbabilities().length);
  assertEquals(1.0, tokenizer.getTokenProbabilities()[0], 0.00001);
}
@Test
public void testTokenizeInputWithHyphenatedWordsWithoutSplit() {
  MaxentModel model = mock(MaxentModel.class);
  TokenContextGenerator cg = mock(TokenContextGenerator.class);

  TokenizerFactory factory = mock(TokenizerFactory.class);
  when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[\\w\\-]+"));
  when(factory.getContextGenerator()).thenReturn(cg);
  when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

  TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
  TokenizerME tokenizer = new TokenizerME(tokenizerModel);

  String input = "well-known high-quality";
  Span[] spans = tokenizer.tokenizePos(input);

  assertEquals(2, spans.length);
  assertEquals("well-known", input.substring(spans[0].getStart(), spans[0].getEnd()));
  assertEquals("high-quality", input.substring(spans[1].getStart(), spans[1].getEnd()));
  assertEquals(2, tokenizer.getTokenProbabilities().length);
}
@Test
public void testTokenizeMixedCaseTokenOptimization() {
  MaxentModel model = mock(MaxentModel.class);
  TokenContextGenerator cg = mock(TokenContextGenerator.class);

  TokenizerFactory factory = mock(TokenizerFactory.class);
  when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[a-zA-Z0-9]+"));
  when(factory.getContextGenerator()).thenReturn(cg);
  when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

  TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
  TokenizerME tokenizer = new TokenizerME(tokenizerModel);

  String input = "CamelCase1";
  Span[] spans = tokenizer.tokenizePos(input);

  assertEquals(1, spans.length);
  assertEquals("CamelCase1", input.substring(spans[0].getStart(), spans[0].getEnd()));
  assertEquals(1, tokenizer.getTokenProbabilities().length);
  assertEquals(1.0, tokenizer.getTokenProbabilities()[0], 0.00001);
}
@Test
public void testTokenizeWithIntermittentWhitespaceAndControlCharacters() {
  MaxentModel model = mock(MaxentModel.class);
  TokenContextGenerator cg = mock(TokenContextGenerator.class);

  TokenizerFactory factory = mock(TokenizerFactory.class);
  when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("\\w+"));
  when(factory.getContextGenerator()).thenReturn(cg);
  when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

  TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
  TokenizerME tokenizer = new TokenizerME(tokenizerModel);

  String input = "one\u0009two\nthree\u000Cone";
  Span[] spans = tokenizer.tokenizePos(input);

  assertEquals(4, spans.length);
  assertEquals("one", input.substring(spans[0].getStart(), spans[0].getEnd()));
  assertEquals("two", input.substring(spans[1].getStart(), spans[1].getEnd()));
  assertEquals("three", input.substring(spans[2].getStart(), spans[2].getEnd()));
  assertEquals("one", input.substring(spans[3].getStart(), spans[3].getEnd()));
  assertEquals(spans.length, tokenizer.getTokenProbabilities().length);
}
@Test
public void testTokenizeTokenContainingOnlySymbols_NoOptimization() {
  MaxentModel model = mock(MaxentModel.class);
  TokenContextGenerator cg = mock(TokenContextGenerator.class);

  when(model.eval(any())).thenReturn(new double[]{0.5, 0.5});
  when(model.getBestOutcome(any())).thenReturn(TokenizerME.NO_SPLIT);
  when(model.getIndex(TokenizerME.NO_SPLIT)).thenReturn(0);
  when(cg.getContext(anyString(), anyInt())).thenReturn(new String[]{"ctx"});

  TokenizerFactory factory = mock(TokenizerFactory.class);
  when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[!@#\\$\\^&\\*]+"));
  when(factory.getContextGenerator()).thenReturn(cg);
  when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

  TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
  TokenizerME tokenizer = new TokenizerME(tokenizerModel);

  String input = "!@#";
  Span[] spans = tokenizer.tokenizePos(input);

  assertEquals(1, spans.length);
  assertEquals("!@#", input.substring(spans[0].getStart(), spans[0].getEnd()));
  assertEquals(1, tokenizer.getTokenProbabilities().length);
}
@Test
public void testTokenizeTextWithMultipleConsecutiveDotsButNotInDict() {
  MaxentModel model = mock(MaxentModel.class);
  TokenContextGenerator cg = mock(TokenContextGenerator.class);

  when(model.eval(any())).thenReturn(new double[]{0.2, 0.8});
  when(model.getBestOutcome(any())).thenReturn(TokenizerME.SPLIT);
  when(model.getIndex(TokenizerME.SPLIT)).thenReturn(1);
  when(cg.getContext(anyString(), anyInt())).thenReturn(new String[]{"ctx"});

  Dictionary emptyDict = new Dictionary(); 

  TokenizerFactory factory = mock(TokenizerFactory.class);
  when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[.]+"));
  when(factory.getContextGenerator()).thenReturn(cg);
  when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

  TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
  TokenizerME tokenizer = new TokenizerME(tokenizerModel, emptyDict);

  String input = "...";
  Span[] spans = tokenizer.tokenizePos(input);

  assertTrue(spans.length >= 1);
  assertEquals(spans.length, tokenizer.getTokenProbabilities().length);
} 
}