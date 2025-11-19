package opennlp.tools.sentdetect;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.*;
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
// import opennlp.tools.formats.ResourceAsStreamFactory;
import opennlp.tools.namefind.NameFinderME;
import static opennlp.tools.formats.Conll02NameSampleStream.*;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class SentenceDetectorME_llmsuite_2_GPTLLMTest {

@Test
public void testSingleSentenceDetection() {
MaxentModel model = mock(MaxentModel.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
String text = "Hello world.";
when(scanner.getPositions(text)).thenReturn(Arrays.asList(11));
when(contextGen.getContext(text, 11)).thenReturn(new String[] { "ctx" });
when(model.eval(new String[] { "ctx" })).thenReturn(new double[] { 0.1, 0.9 });
when(model.getBestOutcome(new double[] { 0.1, 0.9 })).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
Map<String, String> manifest = new HashMap<>();
SentenceModel sentenceModel = new SentenceModel("en", model, manifest, factory);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
String[] sentences = detector.sentDetect(text);
assertEquals(1, sentences.length);
assertEquals("Hello world.", sentences[0]);
double[] probs = detector.getSentenceProbabilities();
assertEquals(1, probs.length);
assertEquals(0.9, probs[0], 0.0001);
}

@Test
public void testTwoSentenceDetection() {
MaxentModel model = mock(MaxentModel.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
String text = "First. Second.";
when(scanner.getPositions(text)).thenReturn(Arrays.asList(5, 13));
when(contextGen.getContext(text, 5)).thenReturn(new String[] { "ctx1" });
when(contextGen.getContext(text, 13)).thenReturn(new String[] { "ctx2" });
when(model.eval(new String[] { "ctx1" })).thenReturn(new double[] { 0.2, 0.8 });
when(model.getBestOutcome(new double[] { 0.2, 0.8 })).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
when(model.eval(new String[] { "ctx2" })).thenReturn(new double[] { 0.3, 0.7 });
when(model.getBestOutcome(new double[] { 0.3, 0.7 })).thenReturn(SentenceDetectorME.SPLIT);
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
Map<String, String> manifest = new HashMap<>();
SentenceModel sentenceModel = new SentenceModel("en", model, manifest, factory);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
String[] sentences = detector.sentDetect(text);
assertEquals(2, sentences.length);
assertEquals("First.", sentences[0]);
assertEquals("Second.", sentences[1]);
double[] probs = detector.getSentenceProbabilities();
assertEquals(2, probs.length);
assertEquals(0.8, probs[0], 0.01);
assertEquals(0.7, probs[1], 0.01);
}

@Test
public void testEmptyInput() {
MaxentModel model = mock(MaxentModel.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
String text = "";
when(scanner.getPositions(text)).thenReturn(Collections.emptyList());
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
Map<String, String> manifest = new HashMap<>();
SentenceModel modelObj = new SentenceModel("en", model, manifest, factory);
SentenceDetectorME detector = new SentenceDetectorME(modelObj);
String[] sentences = detector.sentDetect(text);
assertEquals(0, sentences.length);
double[] probs = detector.getSentenceProbabilities();
assertEquals(0, probs.length);
}

@Test
public void testWhitespaceOnlyInput() {
MaxentModel model = mock(MaxentModel.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
String text = "   \n\t   ";
when(scanner.getPositions(text)).thenReturn(Collections.emptyList());
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
Map<String, String> manifest = new HashMap<>();
SentenceModel modelObj = new SentenceModel("en", model, manifest, factory);
SentenceDetectorME detector = new SentenceDetectorME(modelObj);
String[] sentences = detector.sentDetect(text);
assertEquals(0, sentences.length);
}

@Test
public void testAbbreviationPreventsSplit() {
MaxentModel model = mock(MaxentModel.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
String text = "This is Dr. Smith.";
when(scanner.getPositions(text)).thenReturn(Arrays.asList(10, 17));
when(contextGen.getContext(text, 10)).thenReturn(new String[] { "ctx1" });
when(contextGen.getContext(text, 17)).thenReturn(new String[] { "ctx2" });
when(model.eval(new String[] { "ctx1" })).thenReturn(new double[] { 0.3, 0.7 });
when(model.eval(new String[] { "ctx2" })).thenReturn(new double[] { 0.2, 0.8 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
// Dictionary dict = new Dictionary();
// dict.put(new StringList("Dr."));
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
SentenceModel modelObj = new SentenceModel("en", model, new HashMap<>(), factory);
// SentenceDetectorME detector = new SentenceDetectorME(modelObj, dict);
// String[] sentences = detector.sentDetect(text);
// assertEquals(1, sentences.length);
// assertEquals("This is Dr. Smith.", sentences[0]);
}

@Test
public void testIsAcceptableBreakReturnsTrueWithoutDict() {
MaxentModel model = mock(MaxentModel.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
SentenceModel modelObj = new SentenceModel("en", model, new HashMap<>(), factory);
SentenceDetectorME detector = new SentenceDetectorME(modelObj, null);
boolean result = detector.isAcceptableBreak("Hello Mr. Smith", 0, 9);
assertTrue(result);
}

@Test
public void testIsAcceptableBreakReturnsFalseWithAbbreviation() {
MaxentModel model = mock(MaxentModel.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
// Dictionary dict = new Dictionary();
// dict.put(new StringList("Mr."));
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
SentenceModel modelObj = new SentenceModel("en", model, new HashMap<>(), factory);
// SentenceDetectorME detector = new SentenceDetectorME(modelObj, dict);
// boolean result = detector.isAcceptableBreak("Hi Mr. Smith", 0, 6);
// assertFalse(result);
}

@Test
public void testSentenceTrainIOException() {
ObjectStream<SentenceSample> stream = new ObjectStream<SentenceSample>() {

@Override
public SentenceSample read() throws IOException {
throw new IOException("Training IO failure");
}

@Override
public void reset() {
}

@Override
public void close() {
}
};
TrainingParameters params = new TrainingParameters();
SentenceDetectorFactory factory;
// try {
// factory = SentenceDetectorFactory.create("en", true, null, null);
// SentenceDetectorME.train("en", stream, factory, params);
// fail("IOException expected");
// } catch (IOException e) {
// assertEquals("Training IO failure", e.getMessage());
// }
}

@Test
public void testDetectWithTokenEndTrue() {
MaxentModel model = mock(MaxentModel.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(true);
String text = "He smiled. She left.";
when(scanner.getPositions(text)).thenReturn(Arrays.asList(10, 20));
when(contextGen.getContext(text, 10)).thenReturn(new String[] { "ctx1" });
when(contextGen.getContext(text, 20)).thenReturn(new String[] { "ctx2" });
when(model.eval(any())).thenReturn(new double[] { 0.1, 0.9 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
SentenceModel modelObj = new SentenceModel("en", model, new HashMap<>(), factory);
SentenceDetectorME detector = new SentenceDetectorME(modelObj);
String[] sentences = detector.sentDetect(text);
assertEquals(2, sentences.length);
assertEquals("He smiled.", sentences[0]);
assertEquals("She left.", sentences[1]);
}

@Test
public void testTrimmedSpanWithLeadingWhitespace() {
MaxentModel model = mock(MaxentModel.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
String text = "   Hello.";
when(scanner.getPositions(text)).thenReturn(Arrays.asList(8));
when(contextGen.getContext(text, 8)).thenReturn(new String[] { "ctx" });
when(model.eval(new String[] { "ctx" })).thenReturn(new double[] { 0.1, 0.9 });
when(model.getBestOutcome(new double[] { 0.1, 0.9 })).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
SentenceModel modelObj = new SentenceModel("en", model, new HashMap<>(), factory);
SentenceDetectorME detector = new SentenceDetectorME(modelObj);
String[] sentences = detector.sentDetect(text);
assertEquals(1, sentences.length);
assertEquals("Hello.", sentences[0]);
}

@Test
public void testNoSplitOutcomeFromModel() {
MaxentModel model = mock(MaxentModel.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
String text = "Sentence one. Sentence two.";
when(scanner.getPositions(text)).thenReturn(Arrays.asList(12, 26));
when(contextGen.getContext(text, 12)).thenReturn(new String[] { "ctx" });
when(model.eval(new String[] { "ctx" })).thenReturn(new double[] { 0.9, 0.1 });
when(model.getBestOutcome(new double[] { 0.9, 0.1 })).thenReturn(SentenceDetectorME.NO_SPLIT);
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
SentenceModel modelObj = new SentenceModel("en", model, new HashMap<>(), factory);
SentenceDetectorME detector = new SentenceDetectorME(modelObj);
String[] sentences = detector.sentDetect(text);
assertEquals(1, sentences.length);
assertEquals("Sentence one. Sentence two.", sentences[0]);
}

@Test
public void testLastSpanExactlyAtEndOfString() {
MaxentModel model = mock(MaxentModel.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
String text = "End.";
when(scanner.getPositions(text)).thenReturn(Arrays.asList(3));
when(contextGen.getContext(text, 3)).thenReturn(new String[] { "ctx" });
when(model.eval(new String[] { "ctx" })).thenReturn(new double[] { 0.2, 0.8 });
when(model.getBestOutcome(new double[] { 0.2, 0.8 })).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
SentenceModel modelObj = new SentenceModel("en", model, new HashMap<>(), factory);
SentenceDetectorME detector = new SentenceDetectorME(modelObj);
String[] sentences = detector.sentDetect(text);
assertEquals(1, sentences.length);
assertEquals("End.", sentences[0]);
}

@Test
public void testOverlappingEOSPositionsIgnored() {
MaxentModel model = mock(MaxentModel.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
String text = "A. B.";
when(scanner.getPositions(text)).thenReturn(Arrays.asList(1, 3));
when(contextGen.getContext(text, 1)).thenReturn(new String[] { "ctx1" });
when(contextGen.getContext(text, 3)).thenReturn(new String[] { "ctx2" });
when(model.eval(new String[] { "ctx1" })).thenReturn(new double[] { 0.1, 0.9 });
when(model.eval(new String[] { "ctx2" })).thenReturn(new double[] { 0.1, 0.9 });
when(model.getBestOutcome(any(double[].class))).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
SentenceModel modelObj = new SentenceModel("en", model, new HashMap<>(), factory);
SentenceDetectorME detector = new SentenceDetectorME(modelObj);
String[] sentences = detector.sentDetect(text);
assertEquals(2, sentences.length);
assertEquals("A.", sentences[0]);
assertEquals("B.", sentences[1]);
}

@Test
public void testAbbreviationThatDoesNotMatchSplit() {
MaxentModel model = mock(MaxentModel.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
String text = "This is Madam. Yes.";
when(scanner.getPositions(text)).thenReturn(Arrays.asList(14, 19));
when(contextGen.getContext(text, 14)).thenReturn(new String[] { "ctx1" });
when(contextGen.getContext(text, 19)).thenReturn(new String[] { "ctx2" });
when(model.eval(new String[] { "ctx1" })).thenReturn(new double[] { 0.2, 0.8 });
when(model.eval(new String[] { "ctx2" })).thenReturn(new double[] { 0.1, 0.9 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
// Dictionary dict = new Dictionary();
// dict.put(new StringList("Dr."));
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
SentenceModel modelObj = new SentenceModel("en", model, new HashMap<>(), factory);
// SentenceDetectorME detector = new SentenceDetectorME(modelObj, dict);
// String[] sentences = detector.sentDetect(text);
// assertEquals(2, sentences.length);
// assertEquals("This is Madam.", sentences[0]);
// assertEquals("Yes.", sentences[1]);
}

@Test
public void testFirstSpanTrimsToZeroLength() {
MaxentModel model = mock(MaxentModel.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
String text = "   \nTest.";
when(scanner.getPositions(text)).thenReturn(Arrays.asList(8));
when(contextGen.getContext(text, 8)).thenReturn(new String[] { "ctx" });
when(model.eval(new String[] { "ctx" })).thenReturn(new double[] { 0.1, 0.9 });
when(model.getBestOutcome(new double[] { 0.1, 0.9 })).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
SentenceModel modelObj = new SentenceModel("en", model, new HashMap<>(), factory);
SentenceDetectorME detector = new SentenceDetectorME(modelObj);
String[] sentences = detector.sentDetect(text);
assertEquals(1, sentences.length);
assertEquals("Test.", sentences[0]);
}

@Test
public void testMultipleSpacesBetweenSentences() {
MaxentModel model = mock(MaxentModel.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
String text = "First.      Second.";
when(scanner.getPositions(text)).thenReturn(Arrays.asList(6, 19));
when(contextGen.getContext(text, 6)).thenReturn(new String[] { "ctx1" });
when(contextGen.getContext(text, 19)).thenReturn(new String[] { "ctx2" });
when(model.eval(new String[] { "ctx1" })).thenReturn(new double[] { 0.1, 0.9 });
when(model.eval(new String[] { "ctx2" })).thenReturn(new double[] { 0.1, 0.9 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
SentenceModel modelObj = new SentenceModel("en", model, new HashMap<>(), factory);
SentenceDetectorME detector = new SentenceDetectorME(modelObj);
String[] sentences = detector.sentDetect(text);
assertEquals(2, sentences.length);
assertEquals("First.", sentences[0]);
assertEquals("Second.", sentences[1]);
}

@Test
public void testSentPosDetectReturnsEmptySpanArrayForOnlyWhitespace() {
MaxentModel model = mock(MaxentModel.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
String text = "    \n   \t  ";
when(scanner.getPositions(text)).thenReturn(Collections.emptyList());
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
SentenceModel modelObj = new SentenceModel("en", model, new HashMap<>(), factory);
SentenceDetectorME detector = new SentenceDetectorME(modelObj);
Span[] spans = detector.sentPosDetect(text);
assertEquals(0, spans.length);
}

@Test
public void testSentenceEndingWithMultipleNewlinesAtEnd() {
MaxentModel model = mock(MaxentModel.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
String text = "Sentence one.\n\n\n";
when(scanner.getPositions(text)).thenReturn(Arrays.asList(12));
when(contextGen.getContext(text, 12)).thenReturn(new String[] { "ctx" });
when(model.eval(new String[] { "ctx" })).thenReturn(new double[] { 0.1, 0.9 });
when(model.getBestOutcome(new double[] { 0.1, 0.9 })).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
SentenceModel modelObj = new SentenceModel("en", model, new HashMap<>(), factory);
SentenceDetectorME detector = new SentenceDetectorME(modelObj);
String[] sentences = detector.sentDetect(text);
assertEquals(1, sentences.length);
assertEquals("Sentence one.", sentences[0]);
double[] probs = detector.getSentenceProbabilities();
assertEquals(1, probs.length);
assertEquals(0.9, probs[0], 0.0001);
}

@Test
public void testEOSInsideAbbreviationAtEndIgnoresSplit() {
MaxentModel model = mock(MaxentModel.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
String text = "He works at U.S.A.";
when(scanner.getPositions(text)).thenReturn(Arrays.asList(7, 9, 13, 17));
when(contextGen.getContext(text, 7)).thenReturn(new String[] { "ctx1" });
when(contextGen.getContext(text, 9)).thenReturn(new String[] { "ctx2" });
when(contextGen.getContext(text, 13)).thenReturn(new String[] { "ctx3" });
when(contextGen.getContext(text, 17)).thenReturn(new String[] { "ctx4" });
when(model.eval(any())).thenReturn(new double[] { 0.1, 0.9 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
// Dictionary dict = new Dictionary();
// dict.put(new StringList("U.S.A."));
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
SentenceModel modelObj = new SentenceModel("en", model, new HashMap<>(), factory);
// SentenceDetectorME detector = new SentenceDetectorME(modelObj, dict);
// String[] sentences = detector.sentDetect(text);
// assertEquals(1, sentences.length);
// assertEquals("He works at U.S.A.", sentences[0]);
// double[] probs = detector.getSentenceProbabilities();
// assertEquals(1, probs.length);
// assertEquals(1.0, probs[0], 0.0001);
}

@Test
public void testMultipleSentenceBoundariesButIndexSkipsDueToEosTokenOverlap() {
MaxentModel model = mock(MaxentModel.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
String text = "A...B.";
when(scanner.getPositions(text)).thenReturn(Arrays.asList(1, 2, 3, 5));
when(contextGen.getContext(eq(text), eq(1))).thenReturn(new String[] { "ctx1" });
when(contextGen.getContext(eq(text), eq(2))).thenReturn(new String[] { "ctx2" });
when(contextGen.getContext(eq(text), eq(3))).thenReturn(new String[] { "ctx3" });
when(contextGen.getContext(eq(text), eq(5))).thenReturn(new String[] { "ctx4" });
when(model.eval(any())).thenReturn(new double[] { 0.1, 0.9 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
SentenceModel modelObj = new SentenceModel("en", model, new HashMap<>(), factory);
SentenceDetectorME detector = new SentenceDetectorME(modelObj);
String[] sentences = detector.sentDetect(text);
assertEquals(2, sentences.length);
assertEquals("A...", sentences[0]);
assertEquals("B.", sentences[1]);
}

@Test
public void testSentPosDetectWithSingleNonSplittableEOS() {
MaxentModel model = mock(MaxentModel.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
String text = "This is something!";
when(scanner.getPositions(text)).thenReturn(Arrays.asList(18));
when(contextGen.getContext(text, 18)).thenReturn(new String[] { "ctx" });
when(model.eval(new String[] { "ctx" })).thenReturn(new double[] { 0.95, 0.05 });
when(model.getBestOutcome(new double[] { 0.95, 0.05 })).thenReturn(SentenceDetectorME.NO_SPLIT);
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
SentenceModel modelObj = new SentenceModel("en", model, new HashMap<>(), factory);
SentenceDetectorME detector = new SentenceDetectorME(modelObj);
String[] sentences = detector.sentDetect(text);
assertEquals(1, sentences.length);
assertEquals("This is something!", sentences[0]);
double[] probs = detector.getSentenceProbabilities();
assertEquals(1, probs.length);
assertEquals(1.0, probs[0], 0.0001);
}

@Test
public void testEndOfSentenceFollowedByWhitespaceSplitPositioning() {
MaxentModel model = mock(MaxentModel.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
String text = "Hello world.  Another.";
when(scanner.getPositions(text)).thenReturn(Arrays.asList(11, 21));
when(contextGen.getContext(text, 11)).thenReturn(new String[] { "ctx1" });
when(contextGen.getContext(text, 21)).thenReturn(new String[] { "ctx2" });
when(model.eval(new String[] { "ctx1" })).thenReturn(new double[] { 0.2, 0.8 });
when(model.eval(new String[] { "ctx2" })).thenReturn(new double[] { 0.2, 0.8 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
SentenceDetectorME detector = new SentenceDetectorME(new SentenceModel("en", model, new HashMap<>(), factory));
String[] sentences = detector.sentDetect(text);
assertEquals(2, sentences.length);
assertEquals("Hello world.", sentences[0]);
assertEquals("Another.", sentences[1]);
}

@Test
public void testModelReturnsNegativeIndexProbabilityIgnoredGracefully() {
MaxentModel model = mock(MaxentModel.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
String text = "Hello world.";
when(scanner.getPositions(text)).thenReturn(Collections.singletonList(11));
when(contextGen.getContext(text, 11)).thenReturn(new String[] { "ctx" });
when(model.eval(new String[] { "ctx" })).thenReturn(new double[] { 0.3, 0.7 });
when(model.getBestOutcome(new double[] { 0.3, 0.7 })).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(-1);
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
SentenceModel sentenceModel = new SentenceModel("en", model, new HashMap<>(), factory);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
String[] sentences = detector.sentDetect(text);
assertEquals(1, sentences.length);
assertEquals("Hello world.", sentences[0]);
double[] probs = detector.getSentenceProbabilities();
assertEquals(1, probs.length);
assertTrue(Double.isNaN(probs[0]) || probs[0] == 0.0);
}

@Test
public void testLeftoverWhitespaceOnlySpanNotAdded() {
MaxentModel model = mock(MaxentModel.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
String text = "Sentence one.   ";
when(scanner.getPositions(text)).thenReturn(Arrays.asList(12));
when(contextGen.getContext(text, 12)).thenReturn(new String[] { "ctx" });
when(model.eval(new String[] { "ctx" })).thenReturn(new double[] { 0.3, 0.7 });
when(model.getBestOutcome(new double[] { 0.3, 0.7 })).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
SentenceDetectorME detector = new SentenceDetectorME(new SentenceModel("en", model, new HashMap<>(), factory));
String[] sentences = detector.sentDetect(text);
assertEquals(1, sentences.length);
assertEquals("Sentence one.", sentences[0]);
}

@Test
public void testAbbreviationOutOfSpanIgnoredInIsAcceptableBreak() {
MaxentModel model = mock(MaxentModel.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
String text = "This is an Image. Test.";
when(scanner.getPositions(text)).thenReturn(Arrays.asList(17, 23));
when(contextGen.getContext(text, 17)).thenReturn(new String[] { "ctx1" });
when(contextGen.getContext(text, 23)).thenReturn(new String[] { "ctx2" });
when(model.eval(any())).thenReturn(new double[] { 0.3, 0.7 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
// Dictionary dict = new Dictionary();
// dict.put(new StringList("Fig."));
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
// SentenceDetectorME detector = new SentenceDetectorME(new SentenceModel("en", model, new HashMap<>(), factory), dict);
// String[] sentences = detector.sentDetect(text);
// assertEquals(2, sentences.length);
// assertEquals("This is an Image.", sentences[0]);
// assertEquals("Test.", sentences[1]);
}

@Test
public void testGetFirstWSStopsCorrectlyAtLength() {
MaxentModel model = mock(MaxentModel.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
String text = "EndOfSentence.";
when(scanner.getPositions(text)).thenReturn(Collections.singletonList(14));
when(contextGen.getContext(text, 14)).thenReturn(new String[] { "ctx" });
when(model.eval(new String[] { "ctx" })).thenReturn(new double[] { 0.1, 0.9 });
when(model.getBestOutcome(new double[] { 0.1, 0.9 })).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
SentenceDetectorME detector = new SentenceDetectorME(new SentenceModel("en", model, new HashMap<>(), factory));
String[] sentences = detector.sentDetect(text);
assertEquals(1, sentences.length);
assertEquals("EndOfSentence.", sentences[0]);
}

@Test
public void testCandidateIndexEqualsCurrentIndexSkipsSplit() {
MaxentModel model = mock(MaxentModel.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
String text = "Word.";
when(scanner.getPositions(text)).thenReturn(Arrays.asList(4));
when(contextGen.getContext(text, 4)).thenReturn(new String[] { "ctx" });
when(model.eval(new String[] { "ctx" })).thenReturn(new double[] { 0.2, 0.8 });
when(model.getBestOutcome(new double[] { 0.2, 0.8 })).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
SentenceDetectorME detector = new SentenceDetectorME(new SentenceModel("en", model, new HashMap<>(), factory));
String[] sentences = detector.sentDetect(text);
assertEquals(1, sentences.length);
assertEquals("Word.", sentences[0]);
}

@Test
public void testInputWithNoPunctuationYieldsFullSpan() {
MaxentModel model = mock(MaxentModel.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
String text = "Hello this is just a sentence with no punctuation";
when(scanner.getPositions(text)).thenReturn(Collections.emptyList());
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
SentenceDetectorME detector = new SentenceDetectorME(new SentenceModel("en", model, new HashMap<>(), factory));
String[] sentences = detector.sentDetect(text);
assertEquals(1, sentences.length);
assertEquals(text, sentences[0]);
}

@Test
public void testUseTokenEndTrueWithPunctuationImmediatelyFollowedByLetter() {
MaxentModel model = mock(MaxentModel.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
String text = "Hi.Morning.";
when(scanner.getPositions(text)).thenReturn(Arrays.asList(2, 10));
when(contextGen.getContext(text, 2)).thenReturn(new String[] { "ctx1" });
when(contextGen.getContext(text, 10)).thenReturn(new String[] { "ctx2" });
when(model.eval(new String[] { "ctx1" })).thenReturn(new double[] { 0.1, 0.9 });
when(model.eval(new String[] { "ctx2" })).thenReturn(new double[] { 0.1, 0.9 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(true);
SentenceDetectorME detector = new SentenceDetectorME(new SentenceModel("en", model, new HashMap<>(), factory));
String[] sentences = detector.sentDetect(text);
assertEquals(2, sentences.length);
assertEquals("Hi.", sentences[0]);
assertEquals("Morning.", sentences[1]);
}

@Test
public void testAbbreviationTokenEndsBeforeCandidateIndexIgnored() {
MaxentModel model = mock(MaxentModel.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
// Dictionary dict = new Dictionary();
// dict.put(new StringList("Dr"));
String text = "The person is Dr played the role.";
when(scanner.getPositions(text)).thenReturn(Arrays.asList(16));
when(contextGen.getContext(text, 16)).thenReturn(new String[] { "ctx" });
when(model.eval(new String[] { "ctx" })).thenReturn(new double[] { 0.1, 0.9 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
// SentenceDetectorME detector = new SentenceDetectorME(new SentenceModel("en", model, new HashMap<>(), factory), dict);
// String[] sentences = detector.sentDetect(text);
// assertEquals(2, sentences.length);
// assertEquals("The person is Dr", sentences[0]);
// assertEquals("played the role.", sentences[1]);
}

@Test
public void testEosAtBeginningOffsetZero() {
MaxentModel model = mock(MaxentModel.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
String text = ".Starts with punctuation.";
when(scanner.getPositions(text)).thenReturn(Arrays.asList(0, 26));
when(contextGen.getContext(text, 0)).thenReturn(new String[] { "ctx1" });
when(contextGen.getContext(text, 26)).thenReturn(new String[] { "ctx2" });
when(model.eval(new String[] { "ctx1" })).thenReturn(new double[] { 0.1, 0.9 });
when(model.eval(new String[] { "ctx2" })).thenReturn(new double[] { 0.1, 0.9 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
SentenceDetectorME detector = new SentenceDetectorME(new SentenceModel("en", model, new HashMap<>(), factory));
String[] sentences = detector.sentDetect(text);
assertEquals(2, sentences.length);
assertEquals(".", sentences[0]);
assertEquals("Starts with punctuation.", sentences[1]);
}

@Test
public void testSplitThatGetsTrimmedToEmptySpanIsRemoved() {
MaxentModel model = mock(MaxentModel.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
String text = "A.     ";
when(scanner.getPositions(text)).thenReturn(Arrays.asList(1));
when(contextGen.getContext(text, 1)).thenReturn(new String[] { "ctx" });
when(model.eval(new String[] { "ctx" })).thenReturn(new double[] { 0.1, 0.9 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
SentenceDetectorME detector = new SentenceDetectorME(new SentenceModel("en", model, new HashMap<>(), factory));
Span[] spans = detector.sentPosDetect(text);
assertEquals(1, spans.length);
assertEquals("A.", spans[0].getCoveredText(text));
}

@Test
public void testModelReturnsEqualSplitAndNoSplitProbabilities() {
MaxentModel model = mock(MaxentModel.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
String text = "Same prob.";
when(scanner.getPositions(text)).thenReturn(Arrays.asList(9));
when(contextGen.getContext(text, 9)).thenReturn(new String[] { "ctx" });
when(model.eval(new String[] { "ctx" })).thenReturn(new double[] { 0.5, 0.5 });
when(model.getBestOutcome(new double[] { 0.5, 0.5 })).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
SentenceModel sentModel = new SentenceModel("en", model, new HashMap<>(), factory);
SentenceDetectorME detector = new SentenceDetectorME(sentModel);
String[] sentences = detector.sentDetect(text);
assertEquals(1, sentences.length);
assertEquals("Same prob.", sentences[0]);
double[] probs = detector.getSentenceProbabilities();
assertEquals(1, probs.length);
assertEquals(0.5, probs[0], 0.0001);
}

@Test
public void testConstructorWithNullDictionaryDoesNotFailInIsAcceptableBreak() {
MaxentModel model = mock(MaxentModel.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
SentenceModel sentenceModel = new SentenceModel("en", model, new HashMap<>(), factory);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel, null);
boolean result = detector.isAcceptableBreak("This is text.", 0, 11);
assertTrue(result);
}

@Test
public void testAbbreviationOverlapsCandidateButNotAtIndex() {
MaxentModel model = mock(MaxentModel.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
// Dictionary dict = new Dictionary();
// dict.put(new StringList("Inc."));
String text = "Company Inc. is successful.";
when(scanner.getPositions(text)).thenReturn(Arrays.asList(11, 26));
when(contextGen.getContext(text, 11)).thenReturn(new String[] { "ctx1" });
when(contextGen.getContext(text, 26)).thenReturn(new String[] { "ctx2" });
when(model.eval(any())).thenReturn(new double[] { 0.1, 0.9 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
// SentenceDetectorME detector = new SentenceDetectorME(new SentenceModel("en", model, new HashMap<>(), factory), dict);
// String[] sentences = detector.sentDetect(text);
// assertEquals(2, sentences.length);
// assertEquals("Company Inc.", sentences[0]);
// assertEquals("is successful.", sentences[1]);
}

@Test
public void testEmptyAbbreviationDictionaryIsHandled() {
MaxentModel model = mock(MaxentModel.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
// Dictionary emptyDict = new Dictionary();
String text = "Hello there. How are you?";
when(scanner.getPositions(text)).thenReturn(Arrays.asList(12, 25));
when(contextGen.getContext(text, 12)).thenReturn(new String[] { "ctx1" });
when(contextGen.getContext(text, 25)).thenReturn(new String[] { "ctx2" });
when(model.eval(any())).thenReturn(new double[] { 0.2, 0.8 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
// SentenceDetectorME detector = new SentenceDetectorME(new SentenceModel("en", model, new HashMap<>(), factory), emptyDict);
// String[] sentences = detector.sentDetect(text);
// assertEquals(2, sentences.length);
// assertEquals("Hello there.", sentences[0]);
// assertEquals("How are you?", sentences[1]);
}

@Test
public void testMisorderedScannerPositionIsSkipped() {
MaxentModel model = mock(MaxentModel.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
String text = "A. B. C.";
when(scanner.getPositions(text)).thenReturn(Arrays.asList(1, 3, 2, 6));
when(contextGen.getContext(any(), anyInt())).thenReturn(new String[] { "ctx" });
when(model.eval(any())).thenReturn(new double[] { 0.1, 0.9 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
SentenceDetectorME detector = new SentenceDetectorME(new SentenceModel("en", model, new HashMap<>(), factory));
String[] sentences = detector.sentDetect(text);
assertEquals(3, sentences.length);
assertEquals("A.", sentences[0]);
assertEquals("B.", sentences[1]);
assertEquals("C.", sentences[2]);
}

@Test
public void testSentenceWithNewlineOnlyBetweenEOS() {
MaxentModel model = mock(MaxentModel.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
String text = "First.\nSecond.";
when(scanner.getPositions(text)).thenReturn(Arrays.asList(5, 13));
when(contextGen.getContext(text, 5)).thenReturn(new String[] { "ctx1" });
when(contextGen.getContext(text, 13)).thenReturn(new String[] { "ctx2" });
when(model.eval(any())).thenReturn(new double[] { 0.3, 0.7 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
SentenceDetectorME detector = new SentenceDetectorME(new SentenceModel("en", model, new HashMap<>(), factory));
String[] sentences = detector.sentDetect(text);
assertEquals(2, sentences.length);
assertEquals("First.", sentences[0]);
assertEquals("Second.", sentences[1]);
}

@Test
public void testUnicodePunctuationSentenceDetection() {
MaxentModel model = mock(MaxentModel.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
String text = "你好。欢迎使用 OpenNLP。";
when(scanner.getPositions(text)).thenReturn(Arrays.asList(2, 17));
when(contextGen.getContext(text, 2)).thenReturn(new String[] { "c1" });
when(contextGen.getContext(text, 17)).thenReturn(new String[] { "c2" });
when(model.eval(any())).thenReturn(new double[] { 0.3, 0.7 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
SentenceDetectorME detector = new SentenceDetectorME(new SentenceModel("zh", model, new HashMap<>(), factory));
String[] sentences = detector.sentDetect(text);
assertEquals(2, sentences.length);
assertEquals("你好。", sentences[0]);
assertEquals("欢迎使用 OpenNLP。", sentences[1]);
}

@Test
public void testConsecutiveEOSCharactersEvaluatedIndependently() {
MaxentModel model = mock(MaxentModel.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
String text = "Whoa.. That's odd.";
when(scanner.getPositions(text)).thenReturn(Arrays.asList(5, 6, 19));
when(contextGen.getContext(text, 5)).thenReturn(new String[] { "ctx1" });
when(contextGen.getContext(text, 6)).thenReturn(new String[] { "ctx2" });
when(contextGen.getContext(text, 19)).thenReturn(new String[] { "ctx3" });
when(model.eval(any())).thenReturn(new double[] { 0.2, 0.8 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
SentenceModel modelObj = new SentenceModel("en", model, new HashMap<>(), factory);
SentenceDetectorME detector = new SentenceDetectorME(modelObj);
String[] sentences = detector.sentDetect(text);
assertEquals(2, sentences.length);
assertEquals("Whoa..", sentences[0]);
assertEquals("That's odd.", sentences[1]);
}

@Test
public void testSentenceWithTabsAndIrregularWhiteSpaces() {
MaxentModel model = mock(MaxentModel.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
String text = "First.\t\t   Second.";
when(scanner.getPositions(text)).thenReturn(Arrays.asList(5, 18));
when(contextGen.getContext(text, 5)).thenReturn(new String[] { "ctx1" });
when(contextGen.getContext(text, 18)).thenReturn(new String[] { "ctx2" });
when(model.eval(any())).thenReturn(new double[] { 0.2, 0.8 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(true);
SentenceDetectorME detector = new SentenceDetectorME(new SentenceModel("en", model, new HashMap<>(), factory));
String[] sentences = detector.sentDetect(text);
assertEquals(2, sentences.length);
assertEquals("First.", sentences[0]);
assertEquals("Second.", sentences[1]);
}

@Test
public void testModelEvalReturnsNullProbsArray() {
MaxentModel model = mock(MaxentModel.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
String text = "Broken model.";
when(scanner.getPositions(text)).thenReturn(Collections.singletonList(13));
when(contextGen.getContext(text, 13)).thenReturn(new String[] { "ctx" });
when(model.eval(new String[] { "ctx" })).thenReturn(null);
when(model.getBestOutcome(null)).thenReturn(SentenceDetectorME.NO_SPLIT);
when(model.getIndex(SentenceDetectorME.NO_SPLIT)).thenReturn(0);
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
SentenceDetectorME detector = new SentenceDetectorME(new SentenceModel("en", model, new HashMap<>(), factory));
String[] sentences = detector.sentDetect(text);
assertEquals(1, sentences.length);
assertEquals(text, sentences[0]);
}

@Test
public void testEOSPositionAfterWhitespaceGetsIgnoredByPositionOrdering() {
MaxentModel model = mock(MaxentModel.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
String text = "First.  Second.";
when(scanner.getPositions(text)).thenReturn(Arrays.asList(6, 7));
when(contextGen.getContext(text, 6)).thenReturn(new String[] { "ctx1" });
when(contextGen.getContext(text, 7)).thenReturn(new String[] { "ctx2" });
when(model.eval(any())).thenReturn(new double[] { 0.1, 0.9 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
SentenceDetectorME detector = new SentenceDetectorME(new SentenceModel("en", model, new HashMap<>(), factory));
String[] result = detector.sentDetect(text);
assertEquals(2, result.length);
assertEquals("First.", result[0]);
assertEquals("Second.", result[1]);
}

@Test
public void testTrimSpanToOnlyWhitespaceResultsInExclusionFromFinalSet() {
MaxentModel model = mock(MaxentModel.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
String text = "A.     B.";
when(scanner.getPositions(text)).thenReturn(Arrays.asList(1, 8));
when(contextGen.getContext(text, 1)).thenReturn(new String[] { "ctx1" });
when(contextGen.getContext(text, 8)).thenReturn(new String[] { "ctx2" });
when(model.eval(any())).thenReturn(new double[] { 0.2, 0.8 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(anyString())).thenReturn(1);
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
SentenceDetectorME detector = new SentenceDetectorME(new SentenceModel("en", model, new HashMap<>(), factory));
Span[] spans = detector.sentPosDetect(text);
assertEquals(2, spans.length);
assertEquals("A.", spans[0].getCoveredText(text));
assertEquals("B.", spans[1].getCoveredText(text));
}

@Test
public void testMultipleConsecutiveEOSHandledSeparatelyWithSkipOverlappingWhitespace() {
MaxentModel model = mock(MaxentModel.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
String text = "A... B.";
when(scanner.getPositions(text)).thenReturn(Arrays.asList(1, 2, 3, 6));
when(contextGen.getContext(eq(text), eq(1))).thenReturn(new String[] { "ctx1" });
when(contextGen.getContext(eq(text), eq(6))).thenReturn(new String[] { "ctx2" });
when(model.eval(any())).thenReturn(new double[] { 0.2, 0.8 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(anyString())).thenReturn(1);
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
SentenceDetectorME detector = new SentenceDetectorME(new SentenceModel("en", model, new HashMap<>(), factory));
String[] sentences = detector.sentDetect(text);
assertEquals(2, sentences.length);
assertEquals("A...", sentences[0]);
assertEquals("B.", sentences[1]);
}

@Test
public void testAbbreviationSpanningCandidateIndexRejectsSplit() {
MaxentModel model = mock(MaxentModel.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
// Dictionary dict = new Dictionary();
// dict.put(new StringList("A.M."));
String text = "Today is 10 A.M. Meeting starts.";
when(scanner.getPositions(text)).thenReturn(Arrays.asList(15, 33));
when(contextGen.getContext(eq(text), eq(15))).thenReturn(new String[] { "ctx1" });
when(contextGen.getContext(eq(text), eq(33))).thenReturn(new String[] { "ctx2" });
when(model.eval(any())).thenReturn(new double[] { 0.3, 0.7 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(anyString())).thenReturn(1);
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
// SentenceDetectorME detector = new SentenceDetectorME(new SentenceModel("en", model, new HashMap<>(), factory), dict);
// String[] sentences = detector.sentDetect(text);
// assertEquals(1, sentences.length);
// assertEquals("Today is 10 A.M. Meeting starts.", sentences[0]);
}

@Test
public void testSentPosDetectReturnsSingleSpanWithWhitespaceWithinSpan() {
MaxentModel model = mock(MaxentModel.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SDContextGenerator contextGen = mock(SDContextGenerator.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
String text = "   Start here.   ";
when(scanner.getPositions(text)).thenReturn(Collections.singletonList(14));
when(contextGen.getContext(text, 14)).thenReturn(new String[] { "ctx1" });
when(model.eval(any())).thenReturn(new double[] { 0.1, 0.9 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(anyString())).thenReturn(1);
when(factory.getSDContextGenerator()).thenReturn(contextGen);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
SentenceDetectorME detector = new SentenceDetectorME(new SentenceModel("en", model, new HashMap<>(), factory));
Span[] spans = detector.sentPosDetect(text);
assertEquals(1, spans.length);
assertEquals("Start here.", spans[0].getCoveredText(text));
}
}
