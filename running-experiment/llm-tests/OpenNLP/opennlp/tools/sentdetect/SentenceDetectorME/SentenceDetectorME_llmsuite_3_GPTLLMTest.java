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

public class SentenceDetectorME_llmsuite_3_GPTLLMTest {

@Test
public void testConstructorWithSentenceModel() {
MaxentModel model = mock(MaxentModel.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
SDContextGenerator contextGenerator = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getAbbreviations()).thenReturn(null);
when(sentenceModel.getFactory()).thenReturn(factory);
when(factory.getSDContextGenerator()).thenReturn(contextGenerator);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
assertNotNull(detector);
}

@Test
public void testConstructorWithDeprecatedFactory() {
MaxentModel model = mock(MaxentModel.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
Factory factory = mock(Factory.class);
SDContextGenerator contextGenerator = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getAbbreviations()).thenReturn(null);
when(sentenceModel.getLanguage()).thenReturn("en");
when(factory.createSentenceContextGenerator(eq("en"), anySet())).thenReturn(contextGenerator);
when(factory.createEndOfSentenceScanner(eq("en"))).thenReturn(scanner);
when(sentenceModel.useTokenEnd()).thenReturn(false);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel, factory);
assertNotNull(detector);
}

@Test
public void testSentDetect_BasicTwoSentences() {
MaxentModel model = mock(MaxentModel.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
SDContextGenerator contextGenerator = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getAbbreviations()).thenReturn(null);
when(sentenceModel.getFactory()).thenReturn(factory);
when(factory.getSDContextGenerator()).thenReturn(contextGenerator);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
CharSequence text = "Hello world. Bye now!";
when(scanner.getPositions(eq(text))).thenReturn(Arrays.asList(11));
when(contextGenerator.getContext(eq(text), eq(11))).thenReturn(new String[] { "x" });
when(model.eval(any(String[].class))).thenReturn(new double[] { 0.2, 0.8 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
String[] result = detector.sentDetect(text);
assertEquals(2, result.length);
assertEquals("Hello world.", result[0]);
assertEquals("Bye now!", result[1]);
double[] probs = detector.getSentenceProbabilities();
assertEquals(2, probs.length);
assertEquals(0.8, probs[0], 0.001);
assertEquals(1.0, probs[1], 0.001);
}

@Test
public void testSentDetect_NoEndCharacterReturnsOneSpan() {
MaxentModel model = mock(MaxentModel.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
SDContextGenerator contextGenerator = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getAbbreviations()).thenReturn(null);
when(sentenceModel.getFactory()).thenReturn(factory);
when(factory.getSDContextGenerator()).thenReturn(contextGenerator);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
CharSequence text = "No period or punctuation here";
when(scanner.getPositions(eq(text))).thenReturn(Collections.emptyList());
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
String[] result = detector.sentDetect(text);
assertEquals(1, result.length);
assertEquals("No period or punctuation here", result[0]);
double[] probs = detector.getSentenceProbabilities();
assertEquals(1, probs.length);
assertEquals(1.0, probs[0], 0.001);
}

@Test
public void testSentDetect_EmptyInputGivesZeroSentences() {
MaxentModel model = mock(MaxentModel.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
SDContextGenerator contextGenerator = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getAbbreviations()).thenReturn(null);
when(sentenceModel.getFactory()).thenReturn(factory);
when(factory.getSDContextGenerator()).thenReturn(contextGenerator);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
String[] result = detector.sentDetect("");
assertEquals(0, result.length);
assertEquals(0, detector.getSentenceProbabilities().length);
}

@Test
public void testIsAcceptableBreak_WithAbbreviation_False() {
Dictionary dict = mock(Dictionary.class);
Iterator<StringList> it = Arrays.asList(new StringList("Dr.")).iterator();
// when(dict.iterator()).thenReturn(it);
MaxentModel model = mock(MaxentModel.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
SDContextGenerator cg = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getFactory()).thenReturn(factory);
when(factory.getSDContextGenerator()).thenReturn(cg);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
// SentenceDetectorME detector = new SentenceDetectorME(sentenceModel, dict);
// boolean result = detector.isAcceptableBreak("Dr. Smith is here.", 0, 3);
// assertFalse(result);
}

@Test
public void testIsAcceptableBreak_NoAbbreviations_ReturnsTrue() {
MaxentModel model = mock(MaxentModel.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
SDContextGenerator cg = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(factory.getSDContextGenerator()).thenReturn(cg);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
when(sentenceModel.getFactory()).thenReturn(factory);
when(sentenceModel.getAbbreviations()).thenReturn(null);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
boolean result = detector.isAcceptableBreak("Hello there!", 0, 11);
assertTrue(result);
}

@Test
public void testGetSentenceProbabilities_NotCalled_ReturnsEmpty() {
MaxentModel model = mock(MaxentModel.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
SDContextGenerator cg = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(factory.getSDContextGenerator()).thenReturn(cg);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
when(sentenceModel.getFactory()).thenReturn(factory);
when(sentenceModel.getAbbreviations()).thenReturn(null);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
double[] result = detector.getSentenceProbabilities();
assertNotNull(result);
assertEquals(0, result.length);
}

@Test
public void testTrainReturnsValidModel() throws IOException {
ObjectStream<SentenceSample> sampleStream = mock(ObjectStream.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
TrainingParameters parameters = new TrainingParameters();
EventTrainer trainer = mock(EventTrainer.class);
MaxentModel trainedModel = mock(MaxentModel.class);
EndOfSentenceScanner eosScanner = mock(EndOfSentenceScanner.class);
SDContextGenerator contextGenerator = mock(SDContextGenerator.class);
when(factory.getEndOfSentenceScanner()).thenReturn(eosScanner);
when(factory.getSDContextGenerator()).thenReturn(contextGenerator);
mockStatic(TrainerFactory.class);
when(TrainerFactory.getEventTrainer(eq(parameters), any())).thenReturn(trainer);
when(trainer.train(any(ObjectStream.class))).thenReturn(trainedModel);
SentenceModel model = SentenceDetectorME.train("en", sampleStream, factory, parameters);
assertNotNull(model);
}

@Test
public void testSentDetect_SentenceWithOnlyWhitespaces_ReturnsEmptyArray() {
MaxentModel model = mock(MaxentModel.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
SDContextGenerator contextGenerator = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getAbbreviations()).thenReturn(null);
when(sentenceModel.getFactory()).thenReturn(factory);
when(factory.getSDContextGenerator()).thenReturn(contextGenerator);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
String[] sentences = detector.sentDetect("     \t  \n   ");
assertNotNull(sentences);
assertEquals(0, sentences.length);
}

@Test
public void testSentPosDetect_SplitRejectedByAcceptableBreak() {
MaxentModel model = mock(MaxentModel.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
SDContextGenerator contextGenerator = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
Dictionary dictionary = mock(Dictionary.class);
Iterator<StringList> iterator = Arrays.asList(new StringList("Dr.")).iterator();
SentenceModel sentenceModel = mock(SentenceModel.class);
when(sentenceModel.getMaxentModel()).thenReturn(model);
// when(sentenceModel.getAbbreviations()).thenReturn(dictionary);
when(sentenceModel.getFactory()).thenReturn(factory);
when(factory.getSDContextGenerator()).thenReturn(contextGenerator);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
when(scanner.getPositions(eq("Dr. Smith is here."))).thenReturn(Arrays.asList(3));
when(contextGenerator.getContext(eq("Dr. Smith is here."), eq(3))).thenReturn(new String[] { "x" });
when(model.eval(any(String[].class))).thenReturn(new double[] { 0.1, 0.9 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
// when(dictionary.iterator()).thenReturn(iterator);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
Span[] spans = detector.sentPosDetect("Dr. Smith is here.");
assertEquals(1, spans.length);
assertEquals("Dr. Smith is here.", spans[0].getCoveredText("Dr. Smith is here.").toString());
}

@Test
public void testSentDetect_MultipleTerminatorsBackToBack() {
MaxentModel model = mock(MaxentModel.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
SDContextGenerator contextGenerator = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getAbbreviations()).thenReturn(null);
when(sentenceModel.getFactory()).thenReturn(factory);
when(factory.getSDContextGenerator()).thenReturn(contextGenerator);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
String text = "Hello... How are you?";
when(scanner.getPositions(eq(text))).thenReturn(Arrays.asList(7, 8, 9, 23));
when(contextGenerator.getContext(eq(text), eq(7))).thenReturn(new String[] { "x" });
when(contextGenerator.getContext(eq(text), eq(8))).thenReturn(new String[] { "x" });
when(contextGenerator.getContext(eq(text), eq(9))).thenReturn(new String[] { "x" });
when(contextGenerator.getContext(eq(text), eq(23))).thenReturn(new String[] { "x" });
when(model.eval(any(String[].class))).thenReturn(new double[] { 0.2, 0.8 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
String[] result = detector.sentDetect(text);
assertEquals(2, result.length);
assertEquals("Hello...", result[0]);
assertEquals("How are you?", result[1]);
}

@Test
public void testSentDetect_TerminatorAtEndOnly() {
MaxentModel model = mock(MaxentModel.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
SDContextGenerator contextGenerator = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getAbbreviations()).thenReturn(null);
when(sentenceModel.getFactory()).thenReturn(factory);
when(factory.getSDContextGenerator()).thenReturn(contextGenerator);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
CharSequence input = "This is a sentence.";
when(scanner.getPositions(eq(input))).thenReturn(Collections.singletonList(20));
when(contextGenerator.getContext(eq(input), eq(20))).thenReturn(new String[] { "x" });
when(model.eval(any(String[].class))).thenReturn(new double[] { 0.4, 0.6 });
when(model.getBestOutcome(any(double[].class))).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
String[] result = detector.sentDetect(input);
assertEquals(1, result.length);
assertEquals("This is a sentence.", result[0]);
}

@Test
public void testSentPosDetect_ConsecutiveWhitespaceSpanTrim() {
MaxentModel model = mock(MaxentModel.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
SDContextGenerator contextGenerator = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getAbbreviations()).thenReturn(null);
when(sentenceModel.getFactory()).thenReturn(factory);
when(factory.getSDContextGenerator()).thenReturn(contextGenerator);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
CharSequence text = "Hello.   Goodbye.";
when(scanner.getPositions(eq(text))).thenReturn(Arrays.asList(5, 13));
when(contextGenerator.getContext(eq(text), eq(5))).thenReturn(new String[] { "x" });
when(contextGenerator.getContext(eq(text), eq(13))).thenReturn(new String[] { "x" });
when(model.eval(any(String[].class))).thenReturn(new double[] { 0.4, 0.6 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
Span[] spans = detector.sentPosDetect(text);
assertEquals(2, spans.length);
assertEquals("Hello.", spans[0].getCoveredText(text).toString());
assertEquals("Goodbye.", spans[1].getCoveredText(text).toString());
}

@Test
public void testSentDetect_AllSplitRejectedNoOutput() {
MaxentModel model = mock(MaxentModel.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
SDContextGenerator contextGenerator = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
Dictionary dict = mock(Dictionary.class);
Iterator<StringList> iter = Arrays.asList(new StringList("etc.")).iterator();
// when(dict.iterator()).thenReturn(iter);
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getFactory()).thenReturn(factory);
// when(sentenceModel.getAbbreviations()).thenReturn(dict);
when(factory.getSDContextGenerator()).thenReturn(contextGenerator);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
CharSequence input = "Misc. subjects, etc. are discussed.";
when(scanner.getPositions(eq(input))).thenReturn(Arrays.asList(4, 18, 33));
when(contextGenerator.getContext(eq(input), eq(4))).thenReturn(new String[] { "x" });
when(contextGenerator.getContext(eq(input), eq(18))).thenReturn(new String[] { "x" });
when(contextGenerator.getContext(eq(input), eq(33))).thenReturn(new String[] { "x" });
when(model.eval(any(String[].class))).thenReturn(new double[] { 0.4, 0.6 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
// SentenceDetectorME detector = new SentenceDetectorME(sentenceModel, dict);
// String[] result = detector.sentDetect(input);
// assertEquals(1, result.length);
// assertEquals("Misc. subjects, etc. are discussed.", result[0]);
}

@Test
public void testSentDetect_InputWithLeadingAndTrailingWhitespace() {
MaxentModel model = mock(MaxentModel.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
SDContextGenerator contextGenerator = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getAbbreviations()).thenReturn(null);
when(sentenceModel.getFactory()).thenReturn(factory);
when(factory.getSDContextGenerator()).thenReturn(contextGenerator);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
CharSequence input = "   Hello world.   ";
when(scanner.getPositions(eq(input))).thenReturn(Collections.singletonList(13));
when(contextGenerator.getContext(eq(input), eq(13))).thenReturn(new String[] { "x" });
when(model.eval(any(String[].class))).thenReturn(new double[] { 0.1, 0.9 });
when(model.getBestOutcome(any(double[].class))).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
String[] result = detector.sentDetect(input);
assertEquals(1, result.length);
assertEquals("Hello world.", result[0]);
double[] probs = detector.getSentenceProbabilities();
assertEquals(1, probs.length);
assertEquals(0.9, probs[0], 0.001);
}

@Test
public void testSentDetect_MultipleSplitPointsOnlyFirstAccepted() {
MaxentModel model = mock(MaxentModel.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
SDContextGenerator contextGenerator = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getAbbreviations()).thenReturn(null);
when(sentenceModel.getFactory()).thenReturn(factory);
when(factory.getSDContextGenerator()).thenReturn(contextGenerator);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
CharSequence input = "Yes. Maybe. No.";
when(scanner.getPositions(eq(input))).thenReturn(Arrays.asList(3, 10, 13));
when(contextGenerator.getContext(eq(input), eq(3))).thenReturn(new String[] { "x" });
when(contextGenerator.getContext(eq(input), eq(10))).thenReturn(new String[] { "x" });
when(contextGenerator.getContext(eq(input), eq(13))).thenReturn(new String[] { "x" });
when(model.eval(any(String[].class))).thenReturn(new double[] { 0.1, 0.9 }).thenReturn(new double[] { 0.7, 0.3 }).thenReturn(new double[] { 0.1, 0.9 });
when(model.getBestOutcome(any(double[].class))).thenReturn(SentenceDetectorME.SPLIT).thenReturn(SentenceDetectorME.NO_SPLIT).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
String[] result = detector.sentDetect(input);
assertEquals(2, result.length);
assertEquals("Yes.", result[0]);
assertEquals("No.", result[1]);
double[] probs = detector.getSentenceProbabilities();
assertEquals(2, probs.length);
assertEquals(0.9, probs[0], 0.001);
assertEquals(1.0, probs[1], 0.001);
}

@Test
public void testSentPosDetect_SkipOverlappingTokenPositions() {
MaxentModel model = mock(MaxentModel.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
SDContextGenerator contextGenerator = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getAbbreviations()).thenReturn(null);
when(sentenceModel.getFactory()).thenReturn(factory);
when(factory.getSDContextGenerator()).thenReturn(contextGenerator);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
CharSequence input = "Hi!? Are you there?";
when(scanner.getPositions(eq(input))).thenReturn(Arrays.asList(2, 3, 20));
when(contextGenerator.getContext(eq(input), eq(2))).thenReturn(new String[] { "x" });
when(contextGenerator.getContext(eq(input), eq(3))).thenReturn(new String[] { "x" });
when(contextGenerator.getContext(eq(input), eq(20))).thenReturn(new String[] { "x" });
when(model.eval(any(String[].class))).thenReturn(new double[] { 0.1, 0.9 });
when(model.getBestOutcome(any(double[].class))).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
Span[] spans = detector.sentPosDetect(input);
assertEquals(2, spans.length);
assertEquals("Hi!?", spans[0].getCoveredText(input).toString());
assertEquals("Are you there?", spans[1].getCoveredText(input).toString());
double[] probs = detector.getSentenceProbabilities();
assertEquals(2, probs.length);
}

@Test
public void testSentDetect_SentenceEndsInWhitespaceAfterSplitAccepted() {
MaxentModel model = mock(MaxentModel.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
SDContextGenerator contextGenerator = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getAbbreviations()).thenReturn(null);
when(sentenceModel.getFactory()).thenReturn(factory);
when(factory.getSDContextGenerator()).thenReturn(contextGenerator);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
CharSequence input = "First sentence.  ";
when(scanner.getPositions(eq(input))).thenReturn(Collections.singletonList(14));
when(contextGenerator.getContext(eq(input), eq(14))).thenReturn(new String[] { "x" });
when(model.eval(any(String[].class))).thenReturn(new double[] { 0.3, 0.7 });
when(model.getBestOutcome(any(double[].class))).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
String[] result = detector.sentDetect(input);
assertEquals(1, result.length);
assertEquals("First sentence.", result[0]);
}

@Test
public void testSentDetect_CustomEOSCharacterInSentenceModel() {
MaxentModel model = mock(MaxentModel.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
Factory factory = mock(Factory.class);
SDContextGenerator contextGenerator = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getLanguage()).thenReturn("en");
when(sentenceModel.getAbbreviations()).thenReturn(null);
when(sentenceModel.useTokenEnd()).thenReturn(false);
when(sentenceModel.getEosCharacters()).thenReturn(new char[] { '~' });
when(factory.createSentenceContextGenerator(anySet(), eq(new char[] { '~' }))).thenReturn(contextGenerator);
when(factory.createEndOfSentenceScanner(eq(new char[] { '~' }))).thenReturn(scanner);
when(scanner.getPositions(eq("Custom EOS~ Next."))).thenReturn(Collections.singletonList(11));
when(contextGenerator.getContext(eq("Custom EOS~ Next."), eq(11))).thenReturn(new String[] { "x" });
when(model.eval(any())).thenReturn(new double[] { 0.1, 0.9 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel, factory);
String[] result = detector.sentDetect("Custom EOS~ Next.");
assertEquals(2, result.length);
assertEquals("Custom EOS~", result[0]);
assertEquals("Next.", result[1]);
}

@Test
public void testSentDetect_SingleCharacterEOS_NoContextMatch() {
MaxentModel model = mock(MaxentModel.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
SDContextGenerator contextGenerator = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getFactory()).thenReturn(factory);
when(sentenceModel.getAbbreviations()).thenReturn(null);
when(factory.getSDContextGenerator()).thenReturn(contextGenerator);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
CharSequence input = "A.";
when(scanner.getPositions(eq(input))).thenReturn(Collections.singletonList(1));
when(contextGenerator.getContext(eq(input), eq(1))).thenReturn(new String[] { "x" });
when(model.eval(any(String[].class))).thenReturn(new double[] { 0.9, 0.1 });
when(model.getBestOutcome(any(double[].class))).thenReturn(SentenceDetectorME.NO_SPLIT);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
String[] result = detector.sentDetect(input);
assertEquals(1, result.length);
assertEquals("A.", result[0]);
assertEquals(1, detector.getSentenceProbabilities().length);
assertEquals(1.0, detector.getSentenceProbabilities()[0], 0.001);
}

@Test
public void testSentDetect_MultipleEOS_WithOverlapSkipsBadSplit() {
MaxentModel model = mock(MaxentModel.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
SDContextGenerator contextGenerator = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getFactory()).thenReturn(factory);
when(sentenceModel.getAbbreviations()).thenReturn(null);
when(factory.getSDContextGenerator()).thenReturn(contextGenerator);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
CharSequence input = "One.. Two.";
when(scanner.getPositions(eq(input))).thenReturn(Arrays.asList(3, 4, 9));
when(contextGenerator.getContext(eq(input), eq(3))).thenReturn(new String[] { "x" });
when(contextGenerator.getContext(eq(input), eq(4))).thenReturn(new String[] { "x" });
when(contextGenerator.getContext(eq(input), eq(9))).thenReturn(new String[] { "x" });
when(model.eval(any(String[].class))).thenReturn(new double[] { 0.1, 0.9 });
when(model.getBestOutcome(any(double[].class))).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
String[] result = detector.sentDetect(input);
assertEquals(2, result.length);
assertEquals("One..", result[0]);
assertEquals("Two.", result[1]);
}

@Test
public void testSentPosDetect_EmptyStringInput() {
MaxentModel model = mock(MaxentModel.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
SDContextGenerator contextGenerator = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getFactory()).thenReturn(factory);
when(sentenceModel.getAbbreviations()).thenReturn(null);
when(factory.getSDContextGenerator()).thenReturn(contextGenerator);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
CharSequence input = "";
when(scanner.getPositions(eq(input))).thenReturn(Collections.emptyList());
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
Span[] result = detector.sentPosDetect(input);
assertEquals(0, result.length);
assertEquals(0, detector.getSentenceProbabilities().length);
}

@Test
public void testSentDetect_SentenceWithAllSpacesAfterEOS_TrimCheck() {
MaxentModel model = mock(MaxentModel.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
SDContextGenerator contextGenerator = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getFactory()).thenReturn(factory);
when(factory.getSDContextGenerator()).thenReturn(contextGenerator);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
when(sentenceModel.getAbbreviations()).thenReturn(null);
CharSequence input = "Test.     ";
when(scanner.getPositions(eq(input))).thenReturn(Collections.singletonList(4));
when(contextGenerator.getContext(eq(input), eq(4))).thenReturn(new String[] { "x" });
when(model.eval(any(String[].class))).thenReturn(new double[] { 0.2, 0.8 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
String[] result = detector.sentDetect(input);
assertEquals(1, result.length);
assertEquals("Test.", result[0]);
}

@Test
public void testIsAcceptableBreak_AbbreviationPartiallyMatchingTokenInsideRange_DoesNotReject() {
Dictionary dict = mock(Dictionary.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
SDContextGenerator contextGenerator = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
StringList entry = new StringList("etc...");
// when(dict.iterator()).thenReturn(Collections.singletonList(entry).iterator());
when(sentenceModel.getMaxentModel()).thenReturn(mock(MaxentModel.class));
when(sentenceModel.getFactory()).thenReturn(factory);
// when(sentenceModel.getAbbreviations()).thenReturn(dict);
when(factory.getSDContextGenerator()).thenReturn(contextGenerator);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
// SentenceDetectorME detector = new SentenceDetectorME(sentenceModel, dict);
// boolean result = detector.isAcceptableBreak("List includes etc. and more...", 20, 24);
// assertTrue(result);
}

@Test
public void testIsAcceptableBreak_StartsWithAbbreviationButOutsideCandidateIndex_Accepted() {
Dictionary dict = mock(Dictionary.class);
StringList entry = new StringList("Dr.");
Iterator<StringList> iterator = Collections.singletonList(entry).iterator();
SentenceModel sentenceModel = mock(SentenceModel.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
SDContextGenerator contextGenerator = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
// when(dict.iterator()).thenReturn(iterator);
when(sentenceModel.getMaxentModel()).thenReturn(mock(MaxentModel.class));
when(sentenceModel.getFactory()).thenReturn(factory);
// when(sentenceModel.getAbbreviations()).thenReturn(dict);
when(factory.getSDContextGenerator()).thenReturn(contextGenerator);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
// SentenceDetectorME detector = new SentenceDetectorME(sentenceModel, dict);
// boolean result = detector.isAcceptableBreak("Dr. Smith lives nearby.", 0, 20);
// assertTrue(result);
}

@Test
public void testSentDetect_EmptyStringWithLeadingTrailingWhitespace() {
MaxentModel model = mock(MaxentModel.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
SDContextGenerator contextGenerator = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getAbbreviations()).thenReturn(null);
when(sentenceModel.getFactory()).thenReturn(factory);
when(factory.getSDContextGenerator()).thenReturn(contextGenerator);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
CharSequence input = "     ";
when(scanner.getPositions(eq(input))).thenReturn(Collections.emptyList());
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
String[] result = detector.sentDetect(input);
assertNotNull(result);
assertEquals(0, result.length);
assertEquals(0, detector.getSentenceProbabilities().length);
}

@Test
public void testSentDetect_OnlyEOSCharacters_ReturnsValidSentence() {
MaxentModel model = mock(MaxentModel.class);
SentenceModel modelWrapper = mock(SentenceModel.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SDContextGenerator contextGenerator = mock(SDContextGenerator.class);
when(modelWrapper.getMaxentModel()).thenReturn(model);
when(modelWrapper.getAbbreviations()).thenReturn(null);
when(modelWrapper.getFactory()).thenReturn(factory);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.getSDContextGenerator()).thenReturn(contextGenerator);
when(factory.isUseTokenEnd()).thenReturn(false);
CharSequence input = "...";
when(scanner.getPositions(eq(input))).thenReturn(Arrays.asList(0, 1, 2));
when(contextGenerator.getContext(eq(input), eq(0))).thenReturn(new String[] { "x" });
when(contextGenerator.getContext(eq(input), eq(1))).thenReturn(new String[] { "x" });
when(contextGenerator.getContext(eq(input), eq(2))).thenReturn(new String[] { "x" });
when(model.eval(any())).thenReturn(new double[] { 0.1, 0.9 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
SentenceDetectorME detector = new SentenceDetectorME(modelWrapper);
String[] result = detector.sentDetect(input);
assertEquals(1, result.length);
assertEquals("...", result[0]);
}

@Test
public void testSentDetect_UseTokenEnd_True_ShouldSkipExtraWS() {
MaxentModel model = mock(MaxentModel.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
SDContextGenerator contextGenerator = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getAbbreviations()).thenReturn(null);
when(sentenceModel.getFactory()).thenReturn(factory);
when(factory.getSDContextGenerator()).thenReturn(contextGenerator);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(true);
CharSequence input = "Hello.  World!";
when(scanner.getPositions(eq(input))).thenReturn(Arrays.asList(5));
when(contextGenerator.getContext(eq(input), eq(5))).thenReturn(new String[] { "a" });
when(model.eval(any())).thenReturn(new double[] { 0.3, 0.7 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(eq(SentenceDetectorME.SPLIT))).thenReturn(1);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
String[] result = detector.sentDetect(input);
assertEquals(2, result.length);
assertEquals("Hello.", result[0]);
assertEquals("World!", result[1]);
}

@Test
public void testSentPosDetect_SpanTrimRemovedDueToOnlyWhitespace() {
MaxentModel model = mock(MaxentModel.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SDContextGenerator contextGenerator = mock(SDContextGenerator.class);
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getAbbreviations()).thenReturn(null);
when(sentenceModel.getFactory()).thenReturn(factory);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.getSDContextGenerator()).thenReturn(contextGenerator);
when(factory.isUseTokenEnd()).thenReturn(false);
CharSequence input = "Hello.   ";
when(scanner.getPositions(eq(input))).thenReturn(Collections.singletonList(5));
when(contextGenerator.getContext(eq(input), eq(5))).thenReturn(new String[] { "x" });
when(model.eval(any(String[].class))).thenReturn(new double[] { 0.2, 0.8 });
when(model.getBestOutcome(any(double[].class))).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
Span[] spans = detector.sentPosDetect(input);
assertEquals(1, spans.length);
assertEquals("Hello.", spans[0].getCoveredText(input).toString());
}

@Test
public void testSentDetect_MultipleEOSWithWhitespaceOnlySpanInBetween() {
MaxentModel model = mock(MaxentModel.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SDContextGenerator contextGenerator = mock(SDContextGenerator.class);
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getAbbreviations()).thenReturn(null);
when(sentenceModel.getFactory()).thenReturn(factory);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.getSDContextGenerator()).thenReturn(contextGenerator);
when(factory.isUseTokenEnd()).thenReturn(false);
CharSequence input = "A.  .B";
when(scanner.getPositions(eq(input))).thenReturn(Arrays.asList(1, 4));
when(contextGenerator.getContext(eq(input), eq(1))).thenReturn(new String[] { "x" });
when(contextGenerator.getContext(eq(input), eq(4))).thenReturn(new String[] { "x" });
when(model.eval(any())).thenReturn(new double[] { 0.2, 0.8 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
String[] result = detector.sentDetect(input);
assertEquals(2, result.length);
assertEquals("A.", result[0]);
assertEquals(".B", result[1]);
}

@Test
public void testTrain_EventTrainerThrowsIOException_PropagatedCorrectly() throws IOException {
ObjectStream<SentenceSample> sentenceSampleStream = mock(ObjectStream.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
TrainingParameters params = new TrainingParameters();
EventTrainer trainer = mock(EventTrainer.class);
MaxentModel model = mock(MaxentModel.class);
SDContextGenerator contextGenerator = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.getSDContextGenerator()).thenReturn(contextGenerator);
// when(trainer.train(any())).thenThrow(new IOException("Failed to train"));
mockStatic(TrainerFactory.class).when(() -> TrainerFactory.getEventTrainer(eq(params), anyMap())).thenReturn(trainer);
boolean exceptionThrown = false;
try {
SentenceDetectorME.train("en", sentenceSampleStream, factory, params);
} catch (IOException e) {
exceptionThrown = true;
assertEquals("Failed to train", e.getMessage());
}
assertTrue(exceptionThrown);
}

@Test
public void testSentPosDetect_InputExactlyOneEOSAtEnd_TrimLastSpan() {
MaxentModel model = mock(MaxentModel.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SDContextGenerator contextGenerator = mock(SDContextGenerator.class);
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getAbbreviations()).thenReturn(null);
when(sentenceModel.getFactory()).thenReturn(factory);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.getSDContextGenerator()).thenReturn(contextGenerator);
when(factory.isUseTokenEnd()).thenReturn(false);
CharSequence input = "This ends here.";
when(scanner.getPositions(eq(input))).thenReturn(Collections.singletonList(14));
when(contextGenerator.getContext(eq(input), eq(14))).thenReturn(new String[] { "x" });
when(model.eval(any())).thenReturn(new double[] { 0.3, 0.7 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
Span[] result = detector.sentPosDetect(input);
assertEquals(1, result.length);
assertEquals("This ends here.", result[0].getCoveredText(input).toString());
}

@Test
public void testSentDetect_EOSInsideAbbreviation_RejectedAsSplit() {
MaxentModel model = mock(MaxentModel.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
Dictionary dictionary = mock(Dictionary.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SDContextGenerator contextGenerator = mock(SDContextGenerator.class);
CharSequence input = "Inc. is short for Incorporated.";
when(sentenceModel.getMaxentModel()).thenReturn(model);
// when(sentenceModel.getAbbreviations()).thenReturn(dictionary);
when(sentenceModel.getFactory()).thenReturn(factory);
when(factory.getSDContextGenerator()).thenReturn(contextGenerator);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
when(scanner.getPositions(eq(input))).thenReturn(Collections.singletonList(3));
when(contextGenerator.getContext(eq(input), eq(3))).thenReturn(new String[] { "x" });
when(model.eval(any())).thenReturn(new double[] { 0.3, 0.7 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
StringList entry = new StringList("Inc.");
Iterator<StringList> iter = Collections.singletonList(entry).iterator();
// when(dictionary.iterator()).thenReturn(iter);
// SentenceDetectorME detector = new SentenceDetectorME(sentenceModel, dictionary);
// String[] result = detector.sentDetect(input);
// assertEquals(1, result.length);
// assertEquals("Inc. is short for Incorporated.", result[0]);
}

@Test
public void testSentDetect_SingleEOSWithOnlyWhitespaceRightOfSplit_TrimEmptySpan() {
MaxentModel model = mock(MaxentModel.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SDContextGenerator contextGenerator = mock(SDContextGenerator.class);
CharSequence input = "Final.     ";
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getAbbreviations()).thenReturn(null);
when(sentenceModel.getFactory()).thenReturn(factory);
when(factory.getSDContextGenerator()).thenReturn(contextGenerator);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
when(scanner.getPositions(eq(input))).thenReturn(Collections.singletonList(5));
when(contextGenerator.getContext(eq(input), eq(5))).thenReturn(new String[] { "x" });
when(model.eval(any())).thenReturn(new double[] { 0.1, 0.9 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
String[] result = detector.sentDetect(input);
assertEquals(1, result.length);
assertEquals("Final.", result[0]);
double[] probs = detector.getSentenceProbabilities();
assertEquals(1, probs.length);
assertEquals(0.9, probs[0], 0.001);
}

@Test
public void testSentDetect_ScannerReturnsOverlappingEOS_SecondIsSkipped() {
MaxentModel model = mock(MaxentModel.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SDContextGenerator contextGenerator = mock(SDContextGenerator.class);
CharSequence input = "Hi!? Okay.";
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getAbbreviations()).thenReturn(null);
when(sentenceModel.getFactory()).thenReturn(factory);
when(factory.getSDContextGenerator()).thenReturn(contextGenerator);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
when(scanner.getPositions(eq(input))).thenReturn(Arrays.asList(2, 3, 9));
when(contextGenerator.getContext(eq(input), eq(2))).thenReturn(new String[] { "x" });
when(contextGenerator.getContext(eq(input), eq(3))).thenReturn(new String[] { "x" });
when(contextGenerator.getContext(eq(input), eq(9))).thenReturn(new String[] { "x" });
when(model.eval(any())).thenReturn(new double[] { 0.2, 0.8 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
String[] result = detector.sentDetect(input);
assertEquals(2, result.length);
assertEquals("Hi!?", result[0]);
assertEquals("Okay.", result[1]);
}

@Test
public void testSentDetect_AbbreviationStartsAtInvalidIndex_NoFalseReject() {
MaxentModel model = mock(MaxentModel.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
Dictionary dict = mock(Dictionary.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SDContextGenerator contextGenerator = mock(SDContextGenerator.class);
CharSequence input = "RandomInc. is a company.";
when(sentenceModel.getMaxentModel()).thenReturn(model);
// when(sentenceModel.getAbbreviations()).thenReturn(dict);
when(sentenceModel.getFactory()).thenReturn(factory);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.getSDContextGenerator()).thenReturn(contextGenerator);
when(factory.isUseTokenEnd()).thenReturn(false);
when(scanner.getPositions(eq(input))).thenReturn(Collections.singletonList(9));
when(contextGenerator.getContext(eq(input), eq(9))).thenReturn(new String[] { "x" });
when(model.eval(any())).thenReturn(new double[] { 0.2, 0.8 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
StringList entry = new StringList("Inc.");
Iterator<StringList> iter = Collections.singletonList(entry).iterator();
// when(dict.iterator()).thenReturn(iter);
// SentenceDetectorME detector = new SentenceDetectorME(sentenceModel, dict);
// String[] result = detector.sentDetect(input);
// assertEquals(2, result.length);
// assertEquals("RandomInc.", result[0]);
// assertEquals("is a company.", result[1]);
}

@Test
public void testSentDetect_TrimmedSpanAfterEOSRemovesBlank() {
MaxentModel model = mock(MaxentModel.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
SDContextGenerator contextGenerator = mock(SDContextGenerator.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
CharSequence input = "Done.   ";
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getFactory()).thenReturn(factory);
when(factory.getSDContextGenerator()).thenReturn(contextGenerator);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.isUseTokenEnd()).thenReturn(false);
when(sentenceModel.getAbbreviations()).thenReturn(null);
when(scanner.getPositions(eq(input))).thenReturn(Collections.singletonList(4));
when(contextGenerator.getContext(eq(input), eq(4))).thenReturn(new String[] { "x" });
when(model.eval(any())).thenReturn(new double[] { 0.1, 0.9 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(SentenceDetectorME.SPLIT)).thenReturn(1);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
Span[] spans = detector.sentPosDetect(input);
assertEquals(1, spans.length);
assertEquals("Done.", spans[0].getCoveredText(input).toString());
}

@Test
public void testSentDetect_EOSAtStartOfText() {
MaxentModel model = mock(MaxentModel.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SDContextGenerator contextGenerator = mock(SDContextGenerator.class);
CharSequence input = ". Starts with punctuation.";
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getAbbreviations()).thenReturn(null);
when(sentenceModel.getFactory()).thenReturn(factory);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.getSDContextGenerator()).thenReturn(contextGenerator);
when(factory.isUseTokenEnd()).thenReturn(false);
when(scanner.getPositions(eq(input))).thenReturn(Collections.singletonList(0));
when(contextGenerator.getContext(eq(input), eq(0))).thenReturn(new String[] { "x" });
when(model.eval(any())).thenReturn(new double[] { 0.2, 0.8 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(eq(SentenceDetectorME.SPLIT))).thenReturn(1);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
String[] result = detector.sentDetect(input);
assertEquals(2, result.length);
assertEquals(".", result[0]);
assertEquals("Starts with punctuation.", result[1]);
}

@Test
public void testSentDetect_EOSAfterSingleCharacterFollowedByPunctuation() {
MaxentModel model = mock(MaxentModel.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SDContextGenerator contextGenerator = mock(SDContextGenerator.class);
CharSequence input = "I... continue.";
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getAbbreviations()).thenReturn(null);
when(sentenceModel.getFactory()).thenReturn(factory);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.getSDContextGenerator()).thenReturn(contextGenerator);
when(factory.isUseTokenEnd()).thenReturn(false);
when(scanner.getPositions(eq(input))).thenReturn(Arrays.asList(1, 2, 3, 14));
when(contextGenerator.getContext(eq(input), anyInt())).thenReturn(new String[] { "context" });
when(model.eval(any())).thenReturn(new double[] { 0.2, 0.8 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(eq(SentenceDetectorME.SPLIT))).thenReturn(1);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
String[] result = detector.sentDetect(input);
assertEquals(2, result.length);
assertEquals("I...", result[0]);
assertEquals("continue.", result[1]);
}

@Test
public void testSentDetect_EOSAtLastCharacterOnly() {
MaxentModel model = mock(MaxentModel.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SDContextGenerator contextGenerator = mock(SDContextGenerator.class);
CharSequence input = "Ends here.";
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getAbbreviations()).thenReturn(null);
when(sentenceModel.getFactory()).thenReturn(factory);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.getSDContextGenerator()).thenReturn(contextGenerator);
when(factory.isUseTokenEnd()).thenReturn(false);
when(scanner.getPositions(eq(input))).thenReturn(Collections.singletonList(9));
when(contextGenerator.getContext(eq(input), eq(9))).thenReturn(new String[] { "x" });
when(model.eval(any())).thenReturn(new double[] { 0.2, 0.8 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(eq(SentenceDetectorME.SPLIT))).thenReturn(1);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
String[] result = detector.sentDetect(input);
assertEquals(1, result.length);
assertEquals("Ends here.", result[0]);
}

@Test
public void testSentDetect_MultipleEOS_WhitespaceOnlyInSecondSpan_DetectedCorrectly() {
MaxentModel model = mock(MaxentModel.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SDContextGenerator contextGenerator = mock(SDContextGenerator.class);
CharSequence input = "First.    .Second";
when(sentenceModel.getMaxentModel()).thenReturn(model);
when(sentenceModel.getAbbreviations()).thenReturn(null);
when(sentenceModel.getFactory()).thenReturn(factory);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.getSDContextGenerator()).thenReturn(contextGenerator);
when(factory.isUseTokenEnd()).thenReturn(false);
when(scanner.getPositions(eq(input))).thenReturn(Arrays.asList(5, 10, 16));
when(contextGenerator.getContext(eq(input), anyInt())).thenReturn(new String[] { "x" });
when(model.eval(any())).thenReturn(new double[] { 0.2, 0.8 });
when(model.getBestOutcome(any())).thenReturn(SentenceDetectorME.SPLIT);
when(model.getIndex(eq(SentenceDetectorME.SPLIT))).thenReturn(1);
SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
Span[] spans = detector.sentPosDetect(input);
assertEquals(2, spans.length);
assertEquals("First.", spans[0].getCoveredText(input).toString());
assertEquals(".Second", spans[1].getCoveredText(input).toString());
}

@Test
public void testIsAcceptableBreak_AbbrevCompletelyPastCandidateIndex() {
SentenceModel sentenceModel = mock(SentenceModel.class);
MaxentModel model = mock(MaxentModel.class);
Dictionary dictionary = mock(Dictionary.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SDContextGenerator contextGenerator = mock(SDContextGenerator.class);
String text = "Example sentence. Etc follows.";
when(sentenceModel.getMaxentModel()).thenReturn(model);
// when(sentenceModel.getAbbreviations()).thenReturn(dictionary);
when(sentenceModel.getFactory()).thenReturn(factory);
when(sentenceModel.useTokenEnd()).thenReturn(false);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.getSDContextGenerator()).thenReturn(contextGenerator);
StringList abbrev = new StringList("Etc.");
// when(dictionary.iterator()).thenReturn(Collections.singletonList(abbrev).iterator());
// SentenceDetectorME detector = new SentenceDetectorME(sentenceModel, dictionary);
// boolean accepted = detector.isAcceptableBreak(text, 0, 17);
// assertTrue(accepted);
}

@Test
public void testIsAcceptableBreak_NoAbbreviationMatchReturnsTrue() {
Dictionary dictionary = mock(Dictionary.class);
SentenceModel sentenceModel = mock(SentenceModel.class);
MaxentModel model = mock(MaxentModel.class);
SentenceDetectorFactory factory = mock(SentenceDetectorFactory.class);
EndOfSentenceScanner scanner = mock(EndOfSentenceScanner.class);
SDContextGenerator contextGenerator = mock(SDContextGenerator.class);
when(sentenceModel.getMaxentModel()).thenReturn(model);
// when(sentenceModel.getAbbreviations()).thenReturn(dictionary);
when(sentenceModel.getFactory()).thenReturn(factory);
when(sentenceModel.useTokenEnd()).thenReturn(false);
when(factory.getEndOfSentenceScanner()).thenReturn(scanner);
when(factory.getSDContextGenerator()).thenReturn(contextGenerator);
// when(dictionary.iterator()).thenReturn(Collections.emptyIterator());
// SentenceDetectorME detector = new SentenceDetectorME(sentenceModel, dictionary);
// boolean result = detector.isAcceptableBreak("Text with period.", 0, 17);
// assertTrue(result);
}
}
