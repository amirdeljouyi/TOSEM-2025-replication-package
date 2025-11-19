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

public class TokenizerME_llmsuite_1_GPTLLMTest {

 @Test
  public void testSingleCharacterTokensAreReturnedUnchanged() {
    String input = "A B";
    Pattern pattern = Pattern.compile("^[a-zA-Z0-9]+$");

    MaxentModel mockModel = mock(MaxentModel.class);
    TokenContextGenerator contextGenerator = mock(TokenContextGenerator.class);

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(contextGenerator);
    when(factory.getAlphaNumericPattern()).thenReturn(pattern);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

    Dictionary dict = mock(Dictionary.class);

    TokenizerModel model = new TokenizerModel(mockModel, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(model, dict);

    Span[] spans = tokenizer.tokenizePos(input);

    assertEquals(2, spans.length);
    assertEquals("A", input.substring(spans[0].getStart(), spans[0].getEnd()));
    assertEquals("B", input.substring(spans[1].getStart(), spans[1].getEnd()));

    double[] probs = tokenizer.getTokenProbabilities();
    assertEquals(2, probs.length);
    assertEquals(1.0, probs[0], 0.0001);
    assertEquals(1.0, probs[1], 0.0001);
  }
@Test
  public void testAlphaNumericOptimizationSkipsModelEvaluation() {
    String input = "Token123";

    Pattern pattern = Pattern.compile("^[a-zA-Z0-9]+$");

    MaxentModel mockModel = mock(MaxentModel.class);
    TokenContextGenerator contextGenerator = mock(TokenContextGenerator.class);

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(contextGenerator);
    when(factory.getAlphaNumericPattern()).thenReturn(pattern);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

    TokenizerModel model = new TokenizerModel(mockModel, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(model);

    Span[] spans = tokenizer.tokenizePos(input);

    verify(mockModel, never()).eval(any(String[].class));

    assertEquals(1, spans.length);
    assertEquals("Token123", input.substring(spans[0].getStart(), spans[0].getEnd()));

    double[] probs = tokenizer.getTokenProbabilities();
    assertEquals(1.0, probs[0], 0.0001);
  }
@Test
  public void testModelSplitBehavior() {
    String input = "abc.def";

    Pattern pattern = Pattern.compile("^[a-zA-Z0-9]+$");

    MaxentModel mockModel = mock(MaxentModel.class);
    TokenContextGenerator contextGenerator = mock(TokenContextGenerator.class);
    when(contextGenerator.getContext(eq("abc.def"), eq(1))).thenReturn(new String[]{"f1"});
    when(contextGenerator.getContext(eq("abc.def"), eq(2))).thenReturn(new String[]{"f2"});
    when(contextGenerator.getContext(eq("abc.def"), eq(3))).thenReturn(new String[]{"f3"});
    when(contextGenerator.getContext(eq("abc.def"), eq(4))).thenReturn(new String[]{"f4"});
    when(contextGenerator.getContext(eq("abc.def"), eq(5))).thenReturn(new String[]{"f5"});
    when(contextGenerator.getContext(eq("abc.def"), eq(6))).thenReturn(new String[]{"f6"});

    double[] splitProbs = new double[]{0.2, 0.8};
    double[] noSplitProbs = new double[]{0.9, 0.1};

    when(mockModel.eval(new String[]{"f1"})).thenReturn(noSplitProbs);
    when(mockModel.eval(new String[]{"f2"})).thenReturn(noSplitProbs);
    when(mockModel.eval(new String[]{"f3"})).thenReturn(noSplitProbs);
    when(mockModel.eval(new String[]{"f4"})).thenReturn(splitProbs);
    when(mockModel.eval(new String[]{"f5"})).thenReturn(noSplitProbs);
    when(mockModel.eval(new String[]{"f6"})).thenReturn(noSplitProbs);

    when(mockModel.getBestOutcome(noSplitProbs)).thenReturn("F");
    when(mockModel.getBestOutcome(splitProbs)).thenReturn("T");

    when(mockModel.getIndex("F")).thenReturn(0);
    when(mockModel.getIndex("T")).thenReturn(1);

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(contextGenerator);
    when(factory.getAlphaNumericPattern()).thenReturn(pattern);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

    Dictionary dict = mock(Dictionary.class);
    when(dict.contains(new StringList("abc.def"))).thenReturn(false);

    TokenizerModel model = new TokenizerModel(mockModel, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(model, dict);

    Span[] spans = tokenizer.tokenizePos(input);

    assertEquals(2, spans.length);
    assertEquals("abc.", input.substring(spans[0].getStart(), spans[0].getEnd()));
    assertEquals("def", input.substring(spans[1].getStart(), spans[1].getEnd()));
  }
@Test
  public void testTokenizationRecognizesAbbreviation() {
    String input = "Mr.Smith";

    Pattern pattern = Pattern.compile("^[a-zA-Z0-9]+$");

    MaxentModel mockModel = mock(MaxentModel.class);
    TokenContextGenerator contextGenerator = mock(TokenContextGenerator.class);
    when(contextGenerator.getContext(anyString(), anyInt())).thenReturn(new String[]{"abbr"});

    double[] split = new double[]{0.2, 0.8};
    when(mockModel.eval(any(String[].class))).thenReturn(split);
    when(mockModel.getBestOutcome(split)).thenReturn("T");
    when(mockModel.getIndex("T")).thenReturn(1);

    Dictionary dict = mock(Dictionary.class);
    when(dict.contains(new StringList("Mr.Smith"))).thenReturn(true);

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(contextGenerator);
    when(factory.getAlphaNumericPattern()).thenReturn(pattern);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

    TokenizerModel model = new TokenizerModel(mockModel, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(model, dict);

    Span[] spans = tokenizer.tokenizePos(input);

    assertEquals(1, spans.length);
    assertEquals("Mr.Smith", input.substring(spans[0].getStart(), spans[0].getEnd()));
  }
@Test
  public void testEmptyStringYieldsNoTokens() {
    String input = "";

    Pattern pattern = Pattern.compile("^[a-zA-Z0-9]+$");

    MaxentModel mockModel = mock(MaxentModel.class);
    TokenContextGenerator contextGenerator = mock(TokenContextGenerator.class);

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(contextGenerator);
    when(factory.getAlphaNumericPattern()).thenReturn(pattern);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

    TokenizerModel model = new TokenizerModel(mockModel, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(model);

    Span[] spans = tokenizer.tokenizePos(input);
    assertEquals(0, spans.length);

    double[] probs = tokenizer.getTokenProbabilities();
    assertEquals(0, probs.length);
  }
@Test
  public void testUseAlphaNumericOptimizationReturnsExpectedValue() {
    Pattern pattern = Pattern.compile("^[a-z]+$");

    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator generator = mock(TokenContextGenerator.class);

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(generator);
    when(factory.getAlphaNumericPattern()).thenReturn(pattern);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

    TokenizerModel tModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tModel);

    boolean result = tokenizer.useAlphaNumericOptimization();
    assertTrue(result);
  }
@Test
  public void testIsAcceptableAbbreviationReturnsFalseWithoutDictionary() {
    Pattern pattern = Pattern.compile("[a-z]+");

    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator generator = mock(TokenContextGenerator.class);

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(generator);
    when(factory.getAlphaNumericPattern()).thenReturn(pattern);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

    TokenizerModel tModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tModel, null);

    boolean result = tokenizer.isAcceptableAbbreviation("e.g.");
    assertFalse(result);
  }
@Test
  public void testTrainTokenizerReturnsModel() throws IOException {
    ObjectStream<TokenSample> sampleStream = mock(ObjectStream.class);
    TokenSample sample = new TokenSample("A simple test.", new Span[]{new Span(0, 1), new Span(2, 8)});
    when(sampleStream.read()).thenReturn(sample).thenReturn(null);

    TrainingParameters params = TrainingParameters.defaultParams();

    TokenContextGenerator contextGenerator = context -> new String[]{"ctx"};

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(contextGenerator);
    when(factory.getAlphaNumericPattern()).thenReturn(Pattern.compile("[a-zA-Z]+"));
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

    EventTrainer trainer = mock(EventTrainer.class);
    MaxentModel trainedModel = mock(MaxentModel.class);
    when(trainer.train(any())).thenReturn(trainedModel);

    String algorithm = "MAXENT";
    TrainerFactory.registerTrainer(algorithm, () -> trainer);

    TokenizerModel model = TokenizerME.train(sampleStream, factory, params);
    assertNotNull(model);
  }
@Test
  public void testTokenizeWithMultipleSpacesBetweenTokens() {
    String input = "hello     world";

    Pattern alnumPattern = Pattern.compile("^[a-zA-Z0-9]+$");

    MaxentModel mockModel = mock(MaxentModel.class);
    TokenContextGenerator contextGenerator = mock(TokenContextGenerator.class);

    TokenizerFactory mockFactory = mock(TokenizerFactory.class);
    when(mockFactory.getContextGenerator()).thenReturn(contextGenerator);
    when(mockFactory.getAlphaNumericPattern()).thenReturn(alnumPattern);
    when(mockFactory.isUseAlphaNumericOptimization()).thenReturn(true);

    TokenizerModel model = new TokenizerModel(mockModel, new HashMap<>(), mockFactory);
    TokenizerME tokenizer = new TokenizerME(model, null);

    Span[] spans = tokenizer.tokenizePos(input);
    
    assertEquals(2, spans.length);
    assertEquals("hello", input.substring(spans[0].getStart(), spans[0].getEnd()));
    assertEquals("world", input.substring(spans[1].getStart(), spans[1].getEnd()));
  }
@Test
  public void testTokenizeOnlyWhitespaceInputReturnsNoTokens() {
    String input = "    \t   \n";

    Pattern alnumPattern = Pattern.compile("^[a-zA-Z0-9]+$");

    MaxentModel mockModel = mock(MaxentModel.class);
    TokenContextGenerator contextGenerator = mock(TokenContextGenerator.class);

    TokenizerFactory mockFactory = mock(TokenizerFactory.class);
    when(mockFactory.getContextGenerator()).thenReturn(contextGenerator);
    when(mockFactory.getAlphaNumericPattern()).thenReturn(alnumPattern);
    when(mockFactory.isUseAlphaNumericOptimization()).thenReturn(true);

    TokenizerModel model = new TokenizerModel(mockModel, new HashMap<>(), mockFactory);
    TokenizerME tokenizer = new TokenizerME(model, null);

    Span[] spans = tokenizer.tokenizePos(input);

    assertEquals(0, spans.length);
    assertEquals(0, tokenizer.getTokenProbabilities().length);
  }
@Test
  public void testTokenizeWithNewLinePreserved() {
    String input = "first\nsecond";

    Pattern pattern = Pattern.compile("^[a-zA-Z0-9]+$");

    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator contextGenerator = mock(TokenContextGenerator.class);

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(contextGenerator);
    when(factory.getAlphaNumericPattern()).thenReturn(pattern);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel, null);
    tokenizer.setKeepNewLines(true);

    Span[] spans = tokenizer.tokenizePos(input);
    
    assertEquals(2, spans.length);
    assertEquals("first", input.substring(spans[0].getStart(), spans[0].getEnd()));
    assertEquals("second", input.substring(spans[1].getStart(), spans[1].getEnd()));
  }
@Test
  public void testTokenizeWithPunctuationOnly() {
    String input = "!!!!!";

    Pattern pattern = Pattern.compile("^[a-zA-Z0-9]+$");

    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator contextGenerator = mock(TokenContextGenerator.class);
    when(contextGenerator.getContext(eq("!!!!!"), anyInt())).thenReturn(new String[]{"ctx"});

    double[] splitProbs = new double[]{0.3, 0.7};
    when(model.eval(any(String[].class))).thenReturn(splitProbs);
    when(model.getBestOutcome(splitProbs)).thenReturn("T");
    when(model.getIndex("T")).thenReturn(1);

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(contextGenerator);
    when(factory.getAlphaNumericPattern()).thenReturn(pattern);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel, null);

    Span[] spans = tokenizer.tokenizePos(input);

    assertTrue(spans.length >= 1);
  }
@Test
  public void testAbbreviationSplitWithMultipleDots() {
    String input = "U.S.A.";

    Pattern alnumPattern = Pattern.compile("^[a-zA-Z0-9]+$");

    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator contextGenerator = mock(TokenContextGenerator.class);
    when(contextGenerator.getContext(anyString(), anyInt())).thenReturn(new String[]{"ctx"});

    double[] probs = new double[]{0.4, 0.6};
    when(model.eval(any(String[].class))).thenReturn(probs);
    when(model.getBestOutcome(probs)).thenReturn("T");
    when(model.getIndex("T")).thenReturn(1);

    Dictionary dict = mock(Dictionary.class);
    when(dict.contains(new StringList("U.S.A."))).thenReturn(true);

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(contextGenerator);
    when(factory.getAlphaNumericPattern()).thenReturn(alnumPattern);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel, dict);

    Span[] spans = tokenizer.tokenizePos(input);

    assertEquals(1, spans.length);
    assertEquals("U.S.A.", input.substring(spans[0].getStart(), spans[0].getEnd()));
  }
@Test
  public void testTokenizeLongSingleTokenAlphanumericOptimization() {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < 1000; i++) {
      builder.append("A");
    }
    String input = builder.toString();

    Pattern pattern = Pattern.compile("^[a-zA-Z0-9]+$");

    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator ctx = mock(TokenContextGenerator.class);

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(ctx);
    when(factory.getAlphaNumericPattern()).thenReturn(pattern);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel);

    Span[] spans = tokenizer.tokenizePos(input);

    assertEquals(1, spans.length);
    assertEquals(1000, spans[0].length());
    assertEquals(input, input.substring(spans[0].getStart(), spans[0].getEnd()));
  }
@Test
  public void testMultipleSplitsInOneToken() {
    String input = "abc.def.ghi";

    Pattern pattern = Pattern.compile("^[a-zA-Z0-9]+$");

    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator ctxGen = mock(TokenContextGenerator.class);
    when(ctxGen.getContext(anyString(), anyInt())).thenReturn(new String[]{"ctx"});

    double[] split = new double[]{0.2, 0.8};
    when(model.eval(any(String[].class))).thenReturn(split);
    when(model.getBestOutcome(split)).thenReturn("T");
    when(model.getIndex("T")).thenReturn(1);

    Dictionary dict = mock(Dictionary.class);
    when(dict.contains(new StringList("abc.def.ghi"))).thenReturn(false);

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(ctxGen);
    when(factory.getAlphaNumericPattern()).thenReturn(pattern);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel, dict);

    Span[] spans = tokenizer.tokenizePos(input);

    assertTrue(spans.length >= 3); 
  }
@Test
  public void testSingleWordWithNoSplit() {
    String input = "OpenNLP";

    Pattern alnumPattern = Pattern.compile("^[a-zA-Z0-9]+$");

    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator context = mock(TokenContextGenerator.class);

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(context);
    when(factory.getAlphaNumericPattern()).thenReturn(alnumPattern);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel);

    Span[] spans = tokenizer.tokenizePos(input);

    assertEquals(1, spans.length);
    assertEquals("OpenNLP", input.substring(spans[0].getStart(), spans[0].getEnd()));
    assertEquals(1.0, tokenizer.getTokenProbabilities()[0], 0.0001);
  }
@Test
  public void testAbbreviationRejectedByNullDictionary() {
    String input = "e.g.";

    Pattern pattern = Pattern.compile("^[a-zA-Z0-9]+$");

    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator contextGenerator = mock(TokenContextGenerator.class);
    when(contextGenerator.getContext(anyString(), anyInt())).thenReturn(new String[]{"ctx"});

    double[] probs = new double[]{0.4, 0.6};
    when(model.eval(any(String[].class))).thenReturn(probs);
    when(model.getBestOutcome(probs)).thenReturn("T");
    when(model.getIndex("T")).thenReturn(1);

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(contextGenerator);
    when(factory.getAlphaNumericPattern()).thenReturn(pattern);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel, null);

    Span[] spans = tokenizer.tokenizePos(input);

    assertTrue(spans.length > 1);
  }
@Test
  public void testTokenizeWithUnicodeCharacters() {
    String input = "Hello ☃️ World";

    Pattern alnumPattern = Pattern.compile("^[a-zA-Z0-9]+$");
    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator ctxGen = mock(TokenContextGenerator.class);

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(ctxGen);
    when(factory.getAlphaNumericPattern()).thenReturn(alnumPattern);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel);

    Span[] spans = tokenizer.tokenizePos(input);

    assertEquals(3, spans.length);
    assertEquals("Hello", input.substring(spans[0].getStart(), spans[0].getEnd()));
    assertEquals("☃️", input.substring(spans[1].getStart(), spans[1].getEnd()));
    assertEquals("World", input.substring(spans[2].getStart(), spans[2].getEnd()));
  }
@Test
  public void testTokenizeSingleTokenThatFailsAlphaNumericOptimizationUsesModel() {
    String input = "abc.def";

    Pattern alnumPattern = Pattern.compile("^[a-zA-Z0-9]+$");

    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator ctxGen = mock(TokenContextGenerator.class);
    when(ctxGen.getContext(anyString(), anyInt())).thenReturn(new String[]{"ctx"});

    double[] probs = new double[]{0.6, 0.4};
    when(model.eval(any(String[].class))).thenReturn(probs);
    when(model.getBestOutcome(probs)).thenReturn("F");
    when(model.getIndex("F")).thenReturn(0);

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(ctxGen);
    when(factory.getAlphaNumericPattern()).thenReturn(alnumPattern);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel);

    Span[] spans = tokenizer.tokenizePos(input);

    verify(model).eval(any(String[].class));
    assertEquals(1, spans.length);
    assertEquals("abc.def", input.substring(spans[0].getStart(), spans[0].getEnd()));
  }
@Test
  public void testTokenizeWithDigitTokenSplitsCorrectly() {
    String input = "abc123def";

    Pattern alnumPattern = Pattern.compile("^[a-zA-Z0-9]+$");
    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator ctxGen = mock(TokenContextGenerator.class);

    when(ctxGen.getContext(eq("abc123def"), eq(1))).thenReturn(new String[]{"f1"});
    when(ctxGen.getContext(eq("abc123def"), eq(2))).thenReturn(new String[]{"f2"});
    when(ctxGen.getContext(eq("abc123def"), eq(3))).thenReturn(new String[]{"f3"});
    when(ctxGen.getContext(eq("abc123def"), eq(4))).thenReturn(new String[]{"f4"});
    when(ctxGen.getContext(eq("abc123def"), eq(5))).thenReturn(new String[]{"f5"});
    when(ctxGen.getContext(eq("abc123def"), eq(6))).thenReturn(new String[]{"f6"});
    when(ctxGen.getContext(eq("abc123def"), eq(7))).thenReturn(new String[]{"f7"});
    when(ctxGen.getContext(eq("abc123def"), eq(8))).thenReturn(new String[]{"f8"});

    double[] noSplit = new double[]{0.7, 0.3};
    double[] split = new double[]{0.2, 0.8};

    when(model.eval(new String[]{"f1"})).thenReturn(noSplit);
    when(model.eval(new String[]{"f2"})).thenReturn(noSplit);
    when(model.eval(new String[]{"f3"})).thenReturn(split); 
    when(model.eval(new String[]{"f4"})).thenReturn(noSplit);
    when(model.eval(new String[]{"f5"})).thenReturn(noSplit);
    when(model.eval(new String[]{"f6"})).thenReturn(split); 
    when(model.eval(new String[]{"f7"})).thenReturn(noSplit);
    when(model.eval(new String[]{"f8"})).thenReturn(noSplit);

    when(model.getBestOutcome(split)).thenReturn("T");
    when(model.getBestOutcome(noSplit)).thenReturn("F");
    when(model.getIndex("T")).thenReturn(1);
    when(model.getIndex("F")).thenReturn(0);

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(ctxGen);
    when(factory.getAlphaNumericPattern()).thenReturn(alnumPattern);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel);

    Span[] spans = tokenizer.tokenizePos(input);

    assertEquals(3, spans.length);
    assertEquals("abc", input.substring(spans[0].getStart(), spans[0].getEnd()));
    assertEquals("123", input.substring(spans[1].getStart(), spans[1].getEnd()));
    assertEquals("def", input.substring(spans[2].getStart(), spans[2].getEnd()));
  }
@Test
  public void testTokenProbabilitiesReflectSplitDecisions() {
    String input = "abc.def";

    Pattern alnumPattern = Pattern.compile("[a-zA-Z0-9]+");
    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator contextGenerator = mock(TokenContextGenerator.class);
    when(contextGenerator.getContext(anyString(), anyInt())).thenReturn(new String[]{"ctx"});

    double[] probs = new double[]{0.3, 0.7}; 
    when(model.eval(any(String[].class))).thenReturn(probs);
    when(model.getBestOutcome(probs)).thenReturn("T");
    when(model.getIndex("T")).thenReturn(1);

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(contextGenerator);
    when(factory.getAlphaNumericPattern()).thenReturn(alnumPattern);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel);

    Span[] spans = tokenizer.tokenizePos(input);
    double[] returnedProbs = tokenizer.getTokenProbabilities();

    assertEquals(spans.length, returnedProbs.length);
    for (double p : returnedProbs) {
      assertTrue(p <= 1.0 && p >= 0.0);
    }
  }
@Test
  public void testTokenizeInputEndingWithPunctuation() {
    String input = "This is a test.";

    Pattern pattern = Pattern.compile("[a-zA-Z0-9]+");

    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator ctxGen = mock(TokenContextGenerator.class);

    when(ctxGen.getContext(eq("test."), anyInt())).thenReturn(new String[]{"ctx"});
    double[] split = new double[]{0.1, 0.9};

    when(model.eval(any(String[].class))).thenReturn(split);
    when(model.getBestOutcome(split)).thenReturn("T");
    when(model.getIndex("T")).thenReturn(1);

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(ctxGen);
    when(factory.getAlphaNumericPattern()).thenReturn(pattern);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel);

    Span[] spans = tokenizer.tokenizePos(input);

    assertNotNull(spans);
    assertTrue(spans.length >= 4);
    assertEquals(".", input.substring(spans[spans.length - 1].getStart(),
                                      spans[spans.length - 1].getEnd()));
  }
@Test
  public void testMultipleTokenizePosCallsResetProbabilities() {
    String input1 = "First call.";
    String input2 = "Second call.";

    Pattern pattern = Pattern.compile("[a-zA-Z]+");

    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator ctxGen = mock(TokenContextGenerator.class);

    when(ctxGen.getContext(anyString(), anyInt())).thenReturn(new String[]{"ctx"});
    double[] probs = new double[]{0.2, 0.8};

    when(model.eval(any(String[].class))).thenReturn(probs);
    when(model.getBestOutcome(probs)).thenReturn("T");
    when(model.getIndex("T")).thenReturn(1);

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(ctxGen);
    when(factory.getAlphaNumericPattern()).thenReturn(pattern);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel);

    tokenizer.tokenizePos(input1);
    double[] probsAfterFirst = tokenizer.getTokenProbabilities();

    tokenizer.tokenizePos(input2);
    double[] probsAfterSecond = tokenizer.getTokenProbabilities();

    assertNotSame(probsAfterFirst, probsAfterSecond);
    assertNotEquals(probsAfterFirst.length, 0);
    assertNotEquals(probsAfterSecond.length, 0);
  }
@Test
  public void testTokenizeWithNullAbbreviationDictionarySkipsCheck() {
    String input = "Dr.";

    Pattern pattern = Pattern.compile("^[a-zA-Z]+$");
    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator ctxGen = mock(TokenContextGenerator.class);
    when(ctxGen.getContext(anyString(), anyInt())).thenReturn(new String[]{"ctx"});

    double[] probs = new double[]{0.3, 0.7};
    when(model.eval(any(String[].class))).thenReturn(probs);
    when(model.getBestOutcome(probs)).thenReturn("T");
    when(model.getIndex("T")).thenReturn(1);

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(ctxGen);
    when(factory.getAlphaNumericPattern()).thenReturn(pattern);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);

    TokenizerME tokenizer = new TokenizerME(tokenizerModel, null);

    Span[] spans = tokenizer.tokenizePos(input);

    assertTrue(spans.length > 1);
  }
@Test
  public void testTokenizeMergedAdjacentAbbreviationsNoSplit() {
    String input = "e.g.i.e.";

    Pattern pattern = Pattern.compile("^[a-zA-Z]+$");
    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator ctxGen = mock(TokenContextGenerator.class);
    when(ctxGen.getContext(anyString(), anyInt())).thenReturn(new String[]{"ctx"});

    double[] probs = new double[]{0.4, 0.6};
    when(model.eval(any(String[].class))).thenReturn(probs);
    when(model.getBestOutcome(probs)).thenReturn("T");
    when(model.getIndex("T")).thenReturn(1);

    Dictionary dict = mock(Dictionary.class);
    when(dict.contains(new StringList("e.g.i.e."))).thenReturn(true);

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(ctxGen);
    when(factory.getAlphaNumericPattern()).thenReturn(pattern);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);

    TokenizerME tokenizer = new TokenizerME(tokenizerModel, dict);

    Span[] spans = tokenizer.tokenizePos(input);

    assertEquals(1, spans.length);
    assertEquals("e.g.i.e.", input.substring(spans[0].getStart(), spans[0].getEnd()));
  }
@Test
  public void testTokenizeLongInputMixedAlphanumericAndSymbols() {
    String input = "ThisIsALongTokenWith1234And@@@Symbols#";

    Pattern pattern = Pattern.compile("^[a-zA-Z0-9]+$");

    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator ctxGen = mock(TokenContextGenerator.class);
    when(ctxGen.getContext(anyString(), anyInt())).thenReturn(new String[]{"ctx"});

    double[] noSplit = new double[]{0.8, 0.2};
    double[] split = new double[]{0.1, 0.9};

    when(model.eval(any(String[].class))).thenReturn(noSplit, split, split, noSplit);

    when(model.getBestOutcome(noSplit)).thenReturn("F");
    when(model.getBestOutcome(split)).thenReturn("T");

    when(model.getIndex("F")).thenReturn(0);
    when(model.getIndex("T")).thenReturn(1);

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(ctxGen);
    when(factory.getAlphaNumericPattern()).thenReturn(pattern);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel);

    Span[] spans = tokenizer.tokenizePos(input);
    assertTrue(spans.length >= 2);
  }
@Test
  public void testTokenizeMultipleShortTokensAndPunctuation() {
    String input = "A.B,C!D";

    Pattern pattern = Pattern.compile("^[a-zA-Z]+$");

    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator ctxGen = mock(TokenContextGenerator.class);
    when(ctxGen.getContext(anyString(), anyInt())).thenReturn(new String[]{"ctx"});

    double[] splitProbs = new double[]{0.2, 0.8};
    when(model.eval(any(String[].class))).thenReturn(splitProbs);
    when(model.getBestOutcome(splitProbs)).thenReturn("T");
    when(model.getIndex("T")).thenReturn(1);

    Dictionary dict = mock(Dictionary.class);
    when(dict.contains(any(StringList.class))).thenReturn(false);

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(ctxGen);
    when(factory.getAlphaNumericPattern()).thenReturn(pattern);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);

    TokenizerME tokenizer = new TokenizerME(tokenizerModel, dict);

    Span[] spans = tokenizer.tokenizePos(input);

    assertTrue(spans.length > 1);
  }
@Test
  public void testTokenizeHandlesNewLineWhenDisabled() {
    String input = "line1\nline2";

    Pattern pattern = Pattern.compile("[a-zA-Z0-9]+");

    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator ctxGen = mock(TokenContextGenerator.class);

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(ctxGen);
    when(factory.getAlphaNumericPattern()).thenReturn(pattern);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);

    TokenizerME tokenizer = new TokenizerME(tokenizerModel);
    tokenizer.setKeepNewLines(false);

    Span[] spans = tokenizer.tokenizePos(input);

    assertEquals(2, spans.length);
    assertEquals("line1", input.substring(spans[0].getStart(), spans[0].getEnd()));
    assertEquals("line2", input.substring(spans[1].getStart(), spans[1].getEnd()));
  }
@Test
  public void testTokenizeMixedLanguageCharacters() {
    String input = "This是中文Mixed语言123。";

    Pattern pattern = Pattern.compile("^[a-zA-Z0-9]+$");

    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator ctxGen = mock(TokenContextGenerator.class);

    when(ctxGen.getContext(anyString(), anyInt())).thenReturn(new String[]{"ctx"});

    double[] probs = new double[]{0.5, 0.5};
    when(model.eval(any(String[].class))).thenReturn(probs);
    when(model.getBestOutcome(probs)).thenReturn("F");
    when(model.getIndex("F")).thenReturn(0);

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(ctxGen);
    when(factory.getAlphaNumericPattern()).thenReturn(pattern);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);

    TokenizerME tokenizer = new TokenizerME(tokenizerModel);

    Span[] spans = tokenizer.tokenizePos(input);

    assertNotNull(spans);
    assertTrue(spans.length >= 1);
  }
@Test
  public void testTokenizeWithWhitespaceTabsAndNewlines() {
    String input = "This\tis\na test";

    Pattern pattern = Pattern.compile("^[a-zA-Z0-9]+$");

    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator ctxGen = mock(TokenContextGenerator.class);

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(ctxGen);
    when(factory.getAlphaNumericPattern()).thenReturn(pattern);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);

    TokenizerME tokenizer = new TokenizerME(tokenizerModel);
    tokenizer.setKeepNewLines(true);

    Span[] spans = tokenizer.tokenizePos(input);

    assertEquals(4, spans.length);
    assertEquals("This", input.substring(spans[0].getStart(), spans[0].getEnd()));
    assertEquals("is", input.substring(spans[1].getStart(), spans[1].getEnd()));
    assertEquals("a", input.substring(spans[2].getStart(), spans[2].getEnd()));
    assertEquals("test", input.substring(spans[3].getStart(), spans[3].getEnd()));
  }
@Test
  public void testTokenizeWithVeryShortInput() {
    String input = ".";

    Pattern pattern = Pattern.compile("^[a-zA-Z0-9]+$");

    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator ctxGen = mock(TokenContextGenerator.class);

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(ctxGen);
    when(factory.getAlphaNumericPattern()).thenReturn(pattern);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);

    TokenizerME tokenizer = new TokenizerME(tokenizerModel);

    Span[] spans = tokenizer.tokenizePos(input);

    assertEquals(1, spans.length);
    assertEquals(".", input.substring(spans[0].getStart(), spans[0].getEnd()));
  }
@Test
  public void testTokenizeModelReturnsEmptyProbs() {
    String input = "token";

    Pattern pattern = Pattern.compile("^[a-zA-Z0-9]+$");

    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator ctxGen = mock(TokenContextGenerator.class);

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(ctxGen);
    when(factory.getAlphaNumericPattern()).thenReturn(pattern);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);

    TokenizerME tokenizer = new TokenizerME(tokenizerModel);

    tokenizer.tokenizePos(input);

    double[] probs = tokenizer.getTokenProbabilities();
    assertEquals(1, probs.length);
    assertEquals(1.0d, probs[0], 0.00001);
  }
@Test
  public void testTokenizeAlphaNumericOptimizationFalseWithMatchingToken() {
    String input = "Alphanumeric123";

    Pattern alnumPattern = Pattern.compile("^[a-zA-Z0-9]+$");

    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator contextGenerator = mock(TokenContextGenerator.class);
    when(contextGenerator.getContext(anyString(), anyInt())).thenReturn(new String[]{"ctx"});

    double[] probs = new double[]{0.7, 0.3};
    when(model.eval(any(String[].class))).thenReturn(probs);
    when(model.getBestOutcome(probs)).thenReturn("F");
    when(model.getIndex("F")).thenReturn(0);

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(contextGenerator);
    when(factory.getAlphaNumericPattern()).thenReturn(alnumPattern);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false); 

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel);

    Span[] spans = tokenizer.tokenizePos(input);
    assertEquals(1, spans.length);
    assertEquals("Alphanumeric123", input.substring(spans[0].getStart(), spans[0].getEnd()));
  }
@Test
  public void testTokenizeOnSingleDigitToken() {
    String input = "7";

    Pattern pattern = Pattern.compile("^[a-zA-Z0-9]+$");

    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator contextGenerator = mock(TokenContextGenerator.class);

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(contextGenerator);
    when(factory.getAlphaNumericPattern()).thenReturn(pattern);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel);

    Span[] spans = tokenizer.tokenizePos(input);
    assertEquals(1, spans.length);
    assertEquals("7", input.substring(spans[0].getStart(), spans[0].getEnd()));
    assertEquals(1.0d, tokenizer.getTokenProbabilities()[0], 0.00001);
  }
@Test
  public void testTokenizeNoAlphaNumericOptimizationOnPunctuationOnlyToken() {
    String input = "...";

    Pattern pattern = Pattern.compile("^[a-zA-Z0-9]+$");

    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator contextGen = mock(TokenContextGenerator.class);
    when(contextGen.getContext(anyString(), anyInt())).thenReturn(new String[]{"ctx"});

    double[] probs = new double[]{0.4, 0.6}; 
    when(model.eval(any(String[].class))).thenReturn(probs);
    when(model.getBestOutcome(probs)).thenReturn("T");
    when(model.getIndex("T")).thenReturn(1);

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(contextGen);
    when(factory.getAlphaNumericPattern()).thenReturn(pattern);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false); 

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel);

    Span[] spans = tokenizer.tokenizePos(input);
    assertTrue(spans.length > 1);
  }
@Test
  public void testTokenizeMultipleAbbreviationsEachInDict() {
    String input = "Dr.Mr.Smith";

    Pattern pattern = Pattern.compile("^[a-zA-Z0-9.]+$");

    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator contextGen = mock(TokenContextGenerator.class);
    when(contextGen.getContext(anyString(), anyInt())).thenReturn(new String[]{"ctx"});

    double[] probs = new double[]{0.2, 0.8}; 
    when(model.eval(any(String[].class))).thenReturn(probs);
    when(model.getBestOutcome(probs)).thenReturn("T");
    when(model.getIndex("T")).thenReturn(1);

    Dictionary dict = mock(Dictionary.class);
    when(dict.contains(new StringList("Dr.Mr.Smith"))).thenReturn(false);
    when(dict.contains(new StringList("Dr."))).thenReturn(true);
    when(dict.contains(new StringList("Mr."))).thenReturn(true);

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(contextGen);
    when(factory.getAlphaNumericPattern()).thenReturn(pattern);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

    TokenizerModel modelObj = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(modelObj, dict);

    Span[] spans = tokenizer.tokenizePos(input);

    assertTrue(spans.length >= 3);
    assertEquals("Dr.", input.substring(spans[0].getStart(), spans[0].getEnd()));
    assertEquals("Mr.", input.substring(spans[1].getStart(), spans[1].getEnd()));
    assertEquals("Smith", input.substring(spans[2].getStart(), spans[2].getEnd()));
  }
@Test
  public void testTokenizeReturnsNoTokensForNullString() {
    String input = "";

    Pattern pattern = Pattern.compile("[a-zA-Z0-9]+");

    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator contextGen = mock(TokenContextGenerator.class);

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(contextGen);
    when(factory.getAlphaNumericPattern()).thenReturn(pattern);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

    TokenizerModel modelObj = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(modelObj);

    Span[] spans = tokenizer.tokenizePos(input);
    assertNotNull(spans);
    assertEquals(0, spans.length);

    double[] probs = tokenizer.getTokenProbabilities();
    assertNotNull(probs);
    assertEquals(0, probs.length);
  }
@Test
  public void testTokenizeTwoCharacterNonAlphanumericInputSkipsOptimization() {
    String input = "?!";

    Pattern pattern = Pattern.compile("^[a-zA-Z0-9]+$");

    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator contextGen = mock(TokenContextGenerator.class);
    when(contextGen.getContext(anyString(), anyInt())).thenReturn(new String[]{"ctx"});

    double[] probs = new double[]{0.5, 0.5};
    when(model.eval(any(String[].class))).thenReturn(probs);
    when(model.getBestOutcome(probs)).thenReturn("T");
    when(model.getIndex("T")).thenReturn(1);

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(contextGen);
    when(factory.getAlphaNumericPattern()).thenReturn(pattern);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true); 

    TokenizerModel modelObj = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(modelObj);

    Span[] spans = tokenizer.tokenizePos(input);
    assertTrue(spans.length > 1);
  }
@Test
  public void testTokenizeMultibyteUnicodeWordToken() {
    String input = "こんにちは";

    Pattern pattern = Pattern.compile("^[a-zA-Z0-9]+$");

    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator contextGen = mock(TokenContextGenerator.class);
    when(contextGen.getContext(anyString(), anyInt())).thenReturn(new String[]{"ctx"});

    double[] probs = new double[]{0.6, 0.4};
    when(model.eval(any(String[].class))).thenReturn(probs);
    when(model.getBestOutcome(probs)).thenReturn("F");
    when(model.getIndex("F")).thenReturn(0);

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(contextGen);
    when(factory.getAlphaNumericPattern()).thenReturn(pattern);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

    TokenizerModel modelObj = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(modelObj);

    Span[] spans = tokenizer.tokenizePos(input);
    assertTrue(spans.length >= 1);
    assertEquals("こんにちは", input.substring(spans[0].getStart(), spans[0].getEnd()));
  }
@Test
  public void testTokenizeHandlesSpacesBeforeAndAfterToken() {
    String input = "   token   ";

    Pattern pattern = Pattern.compile("[a-zA-Z]+");

    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator ctxGen = mock(TokenContextGenerator.class);

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(ctxGen);
    when(factory.getAlphaNumericPattern()).thenReturn(pattern);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

    TokenizerModel modelObj = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(modelObj);
    Span[] spans = tokenizer.tokenizePos(input);

    assertTrue(spans.length >= 1);
    assertEquals("token", input.substring(spans[0].getStart(), spans[0].getEnd()));
  }
@Test
  public void testTokenizeWithEmptyTokenAfterSplit() {
    String input = "hello.";

    Pattern pattern = Pattern.compile("^[a-zA-Z0-9]+$");

    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator contextGen = mock(TokenContextGenerator.class);
    when(contextGen.getContext(anyString(), anyInt())).thenReturn(new String[]{"ctx"});

    double[] splitProbs = new double[]{0.2, 0.8};
    when(model.eval(any(String[].class))).thenReturn(splitProbs);
    when(model.getBestOutcome(splitProbs)).thenReturn("T");
    when(model.getIndex("T")).thenReturn(1);

    Dictionary dict = mock(Dictionary.class);
    when(dict.contains(any(StringList.class))).thenReturn(false);

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(contextGen);
    when(factory.getAlphaNumericPattern()).thenReturn(pattern);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel, dict);

    Span[] spans = tokenizer.tokenizePos(input);
    assertTrue(spans.length >= 2);
    assertEquals("hello", input.substring(spans[0].getStart(), spans[0].getEnd()));
  }
@Test
  public void testTokenizeTokenThatEndsWithMultipleDotsAndIsAbbreviation() {
    String input = "U.K.";

    Pattern pattern = Pattern.compile("^[a-zA-Z.]+$");

    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator contextGenerator = mock(TokenContextGenerator.class);
    when(contextGenerator.getContext(anyString(), anyInt())).thenReturn(new String[]{"ctx"});

    double[] probs = new double[]{0.2, 0.8};
    when(model.eval(any(String[].class))).thenReturn(probs);
    when(model.getBestOutcome(probs)).thenReturn("T");
    when(model.getIndex("T")).thenReturn(1);

    Dictionary dict = mock(Dictionary.class);
    when(dict.contains(new StringList("U.K."))).thenReturn(true);

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(contextGenerator);
    when(factory.getAlphaNumericPattern()).thenReturn(pattern);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

    TokenizerModel modelObj = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(modelObj, dict);

    Span[] spans = tokenizer.tokenizePos(input);

    assertEquals(1, spans.length);
    assertEquals("U.K.", input.substring(spans[0].getStart(), spans[0].getEnd()));
  }
@Test
  public void testTokenizeIgnoresEmptySpanAfterAbbreviationWhenDotsSkipped() {
    String input = "Mr.";

    Pattern pattern = Pattern.compile("^[a-zA-Z.]+$");

    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator contextGenerator = mock(TokenContextGenerator.class);
    when(contextGenerator.getContext(anyString(), anyInt())).thenReturn(new String[]{"ctx"});

    double[] probs = new double[]{0.2, 0.8};
    when(model.eval(any(String[].class))).thenReturn(probs);
    when(model.getBestOutcome(probs)).thenReturn("T");
    when(model.getIndex("T")).thenReturn(1);

    Dictionary dict = mock(Dictionary.class);
    when(dict.contains(new StringList("Mr."))).thenReturn(true);

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(contextGenerator);
    when(factory.getAlphaNumericPattern()).thenReturn(pattern);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

    TokenizerModel modelObj = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(modelObj, dict);

    Span[] spans = tokenizer.tokenizePos(input);
    assertEquals(1, spans.length);
    assertEquals("Mr.", input.substring(spans[0].getStart(), spans[0].getEnd()));
  }
@Test
  public void testTokenizeMixedDigitAndSpecialCharacters() {
    String input = "abc$123.def";

    Pattern pattern = Pattern.compile("^[a-zA-Z0-9]+$");

    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator contextGenerator = mock(TokenContextGenerator.class);
    when(contextGenerator.getContext(anyString(), anyInt())).thenReturn(new String[]{"ctx"});

    double[] split = new double[]{0.3, 0.7};
    double[] noSplit = new double[]{0.9, 0.1};

    when(model.eval(any(String[].class)))
        .thenReturn(noSplit)  
        .thenReturn(noSplit)  
        .thenReturn(split)    
        .thenReturn(noSplit)  
        .thenReturn(noSplit)  
        .thenReturn(split)    
        .thenReturn(noSplit); 

    when(model.getBestOutcome(split)).thenReturn("T");
    when(model.getBestOutcome(noSplit)).thenReturn("F");

    when(model.getIndex("T")).thenReturn(1);
    when(model.getIndex("F")).thenReturn(0);

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(contextGenerator);
    when(factory.getAlphaNumericPattern()).thenReturn(pattern);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

    Dictionary dict = mock(Dictionary.class);
    when(dict.contains(any(StringList.class))).thenReturn(false);

    TokenizerModel modelObj = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(modelObj, dict);

    Span[] spans = tokenizer.tokenizePos(input);

    assertTrue(spans.length >= 2);
    for (Span span : spans) {
      assertFalse(span.length() == 0);
    }
  }
@Test
  public void testTokenizeWithNoContextFeaturesReturnsNoSplit() {
    String input = "example";

    Pattern pattern = Pattern.compile("[a-zA-Z]+");

    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator contextGenerator = mock(TokenContextGenerator.class);
    when(contextGenerator.getContext(anyString(), anyInt())).thenReturn(new String[0]);

    double[] probs = new double[]{0.5, 0.5};
    when(model.eval(any(String[].class))).thenReturn(probs);
    when(model.getBestOutcome(probs)).thenReturn("F");
    when(model.getIndex("F")).thenReturn(0);

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(contextGenerator);
    when(factory.getAlphaNumericPattern()).thenReturn(pattern);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel);

    Span[] spans = tokenizer.tokenizePos(input);
    assertEquals(1, spans.length);
    assertEquals("example", input.substring(spans[0].getStart(), spans[0].getEnd()));
  }
@Test
  public void testIsAcceptableAbbreviationReturnsFalseForEmptyString() {
    Pattern pattern = Pattern.compile("^[a-zA-Z0-9]+$");

    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator contextGenerator = mock(TokenContextGenerator.class);

    Dictionary dict = mock(Dictionary.class);
    when(dict.contains(new StringList(""))).thenReturn(false);

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(contextGenerator);
    when(factory.getAlphaNumericPattern()).thenReturn(pattern);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel, dict);

    boolean result = tokenizer.isAcceptableAbbreviation("");
    assertFalse(result);
  }
@Test
  public void testGetTokenProbabilitiesBeforeTokenizationReturnsEmptyArray() {
    Pattern pattern = Pattern.compile("^[a-zA-Z0-9]+$");

    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator contextGenerator = mock(TokenContextGenerator.class);

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(contextGenerator);
    when(factory.getAlphaNumericPattern()).thenReturn(pattern);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel);

    double[] probabilities = tokenizer.getTokenProbabilities();

    assertNotNull(probabilities);
    assertEquals(0, probabilities.length);
  }
@Test
  public void testTokenizeEndsWithNewLineCharacter() {
    String input = "This is a line\n";

    Pattern pattern = Pattern.compile("^[a-zA-Z]+$");

    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator contextGenerator = mock(TokenContextGenerator.class);

    TokenizerFactory tokenizerFactory = mock(TokenizerFactory.class);
    when(tokenizerFactory.getContextGenerator()).thenReturn(contextGenerator);
    when(tokenizerFactory.getAlphaNumericPattern()).thenReturn(pattern);
    when(tokenizerFactory.isUseAlphaNumericOptimization()).thenReturn(true);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), tokenizerFactory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel);
    tokenizer.setKeepNewLines(true);

    Span[] spans = tokenizer.tokenizePos(input);

    assertEquals(4, spans.length);
    assertEquals("This", input.substring(spans[0].getStart(), spans[0].getEnd()));
    assertEquals("is", input.substring(spans[1].getStart(), spans[1].getEnd()));
    assertEquals("a", input.substring(spans[2].getStart(), spans[2].getEnd()));
    assertEquals("line", input.substring(spans[3].getStart(), spans[3].getEnd()));
  }
@Test
  public void testTokenizeSequenceOfSpecialCharacters() {
    String input = "*** ### >>>";

    Pattern pattern = Pattern.compile("^[a-zA-Z0-9]+$");

    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator contextGenerator = mock(TokenContextGenerator.class);
    when(contextGenerator.getContext(anyString(), anyInt())).thenReturn(new String[]{"ctx"});

    double[] split = new double[]{0.2, 0.8};
    when(model.eval(any(String[].class))).thenReturn(split);
    when(model.getBestOutcome(split)).thenReturn("T");
    when(model.getIndex("T")).thenReturn(1);

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(contextGenerator);
    when(factory.getAlphaNumericPattern()).thenReturn(pattern);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

    Dictionary dict = mock(Dictionary.class);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel, dict);

    Span[] spans = tokenizer.tokenizePos(input);
    assertTrue(spans.length > 3); 
  }
@Test
  public void testTokenizeWithMultipleConsecutiveDotsAndAbbreviationMatch() {
    String input = "U.S.A...";

    Pattern pattern = Pattern.compile("^[a-zA-Z.]+$");

    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator contextGen = mock(TokenContextGenerator.class);
    when(contextGen.getContext(anyString(), anyInt())).thenReturn(new String[]{"ctx"});

    double[] probs = new double[]{0.1, 0.9}; 
    when(model.eval(any(String[].class))).thenReturn(probs);
    when(model.getBestOutcome(probs)).thenReturn("T");
    when(model.getIndex("T")).thenReturn(1);

    Dictionary dict = mock(Dictionary.class);
    when(dict.contains(new StringList("U.S.A..."))).thenReturn(true);

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(contextGen);
    when(factory.getAlphaNumericPattern()).thenReturn(pattern);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

    TokenizerModel tokenizerModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokenizerModel, dict);

    Span[] spans = tokenizer.tokenizePos(input);

    assertEquals(1, spans.length);
    assertEquals("U.S.A...", input.substring(spans[0].getStart(), spans[0].getEnd()));
  }
@Test
  public void testTokenizeInputWithSurrogatePairsEmoji() {
    String input = "Testing 😀 token";

    Pattern pattern = Pattern.compile("^[a-zA-Z0-9]+$");
    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator contextGen = mock(TokenContextGenerator.class);
    when(contextGen.getContext(anyString(), anyInt())).thenReturn(new String[]{"ctx"});

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(contextGen);
    when(factory.getAlphaNumericPattern()).thenReturn(pattern);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

    TokenizerModel modelObj = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(modelObj);

    Span[] spans = tokenizer.tokenizePos(input);

    assertEquals(3, spans.length);
    assertEquals("Testing", input.substring(spans[0].getStart(), spans[0].getEnd()));
    assertEquals("😀", input.substring(spans[1].getStart(), spans[1].getEnd()));
    assertEquals("token", input.substring(spans[2].getStart(), spans[2].getEnd()));
  }
@Test
  public void testTokenizeAbbreviationDictMismatchDespiteSplitPrediction() {
    String input = "e.g.";

    Pattern pattern = Pattern.compile("^[a-zA-Z.]+$");

    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator ctxGen = mock(TokenContextGenerator.class);
    when(ctxGen.getContext(anyString(), anyInt())).thenReturn(new String[]{"ctx"});

    double[] split = new double[]{0.1, 0.9};
    when(model.eval(any(String[].class))).thenReturn(split);
    when(model.getBestOutcome(split)).thenReturn("T");
    when(model.getIndex("T")).thenReturn(1);

    Dictionary dict = mock(Dictionary.class);
    when(dict.contains(new StringList("e.g."))).thenReturn(false);

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(ctxGen);
    when(factory.getAlphaNumericPattern()).thenReturn(pattern);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

    TokenizerModel modelObj = new TokenizerModel(model, new HashMap<>(), factory);

    TokenizerME tokenizer = new TokenizerME(modelObj, dict);
    Span[] spans = tokenizer.tokenizePos(input);

    assertTrue(spans.length > 1);
  }
@Test
  public void testTokenizeWithMultipleWhitespaceTypesBetweenTokens() {
    String input = "one\ttwo  three\nfour";

    Pattern pattern = Pattern.compile("[a-zA-Z]+");

    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator contextGen = mock(TokenContextGenerator.class);

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(contextGen);
    when(factory.getAlphaNumericPattern()).thenReturn(pattern);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

    TokenizerModel tokModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tokModel);
    tokenizer.setKeepNewLines(true);

    Span[] spans = tokenizer.tokenizePos(input);

    assertEquals(4, spans.length);
    assertEquals("one", input.substring(spans[0].getStart(), spans[0].getEnd()));
    assertEquals("two", input.substring(spans[1].getStart(), spans[1].getEnd()));
    assertEquals("three", input.substring(spans[2].getStart(), spans[2].getEnd()));
    assertEquals("four", input.substring(spans[3].getStart(), spans[3].getEnd()));
  }
@Test
  public void testTokenProbabilitiesAfterCallToTokenizeMultipleTimes() {
    String input1 = "First sentence";
    String input2 = "Second sentence";

    Pattern pattern = Pattern.compile("^[a-zA-Z]+$");
    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator ctxGen = mock(TokenContextGenerator.class);

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(ctxGen);
    when(factory.getAlphaNumericPattern()).thenReturn(pattern);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

    TokenizerModel tModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tModel);

    Span[] spans1 = tokenizer.tokenizePos(input1);
    double[] probs1 = tokenizer.getTokenProbabilities();
    assertEquals(spans1.length, probs1.length);

    Span[] spans2 = tokenizer.tokenizePos(input2);
    double[] probs2 = tokenizer.getTokenProbabilities();
    assertEquals(spans2.length, probs2.length);
    assertNotEquals(probs1.length, 0);
    assertNotEquals(probs2.length, 0);
  }
@Test
  public void testTokenizeSingleCharacterNonAlphaNumericStillReturned() {
    String input = "@";

    Pattern pattern = Pattern.compile("^[a-zA-Z0-9]+$");
    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator ctxGen = mock(TokenContextGenerator.class);

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(ctxGen);
    when(factory.getAlphaNumericPattern()).thenReturn(pattern);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(true);

    TokenizerModel tModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tModel);

    Span[] spans = tokenizer.tokenizePos(input);

    assertEquals(1, spans.length);
    assertEquals("@", input.substring(spans[0].getStart(), spans[0].getEnd()));
    assertEquals(1.0, tokenizer.getTokenProbabilities()[0], 0.0001);
  }
@Test
  public void testTokenizeTokenThatHasNoPossibleSplitPoint() {
    String input = "XY";

    Pattern pattern = Pattern.compile("^[a-zA-Z]+$");
    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator contextGen = mock(TokenContextGenerator.class);
    when(contextGen.getContext(eq("XY"), eq(1))).thenReturn(new String[]{"ctx"});

    double[] noSplit = new double[]{0.9, 0.1};
    when(model.eval(new String[]{"ctx"})).thenReturn(noSplit);
    when(model.getBestOutcome(noSplit)).thenReturn("F");
    when(model.getIndex("F")).thenReturn(0);

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(contextGen);
    when(factory.getAlphaNumericPattern()).thenReturn(pattern);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

    TokenizerModel tModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tModel);

    Span[] spans = tokenizer.tokenizePos(input);

    assertEquals(1, spans.length);
    assertEquals("XY", input.substring(spans[0].getStart(), spans[0].getEnd()));
  }
@Test
  public void testTokenizeInputWithAbbreviationPrefixNonAbbreviation() {
    String input = "Mrbrown";

    Pattern pattern = Pattern.compile("^[a-zA-Z]+$");

    MaxentModel model = mock(MaxentModel.class);
    TokenContextGenerator contextGen = mock(TokenContextGenerator.class);
    when(contextGen.getContext(anyString(), anyInt())).thenReturn(new String[]{"ctx"});

    double[] split = new double[]{0.3, 0.7};
    when(model.eval(any())).thenReturn(split);
    when(model.getBestOutcome(split)).thenReturn("T");
    when(model.getIndex("T")).thenReturn(1);

    Dictionary dict = mock(Dictionary.class);
    when(dict.contains(new StringList("Mrbrown"))).thenReturn(false);

    TokenizerFactory factory = mock(TokenizerFactory.class);
    when(factory.getContextGenerator()).thenReturn(contextGen);
    when(factory.getAlphaNumericPattern()).thenReturn(pattern);
    when(factory.isUseAlphaNumericOptimization()).thenReturn(false);

    TokenizerModel tModel = new TokenizerModel(model, new HashMap<>(), factory);
    TokenizerME tokenizer = new TokenizerME(tModel, dict);

    Span[] spans = tokenizer.tokenizePos(input);

    assertTrue(spans.length >= 1);
    String token = input.substring(spans[0].getStart(), spans[0].getEnd());
    assertTrue(token.startsWith("M"));
  } 
}