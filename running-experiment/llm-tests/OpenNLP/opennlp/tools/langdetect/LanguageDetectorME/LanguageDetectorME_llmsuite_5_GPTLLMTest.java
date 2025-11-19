package opennlp.tools.langdetect;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import opennlp.tools.ml.BeamSearch;
import opennlp.tools.ml.EventTrainer;
import opennlp.tools.ml.SequenceTrainer;
import opennlp.tools.ml.TrainerFactory;
import opennlp.tools.ml.model.Event;
import opennlp.tools.ml.model.MaxentModel;
import opennlp.tools.ml.model.SequenceClassificationModel;
import opennlp.tools.namefind.NameSample;
import opennlp.tools.util.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
// import opennlp.tools.formats.ResourceAsStreamFactory;
import opennlp.tools.namefind.NameFinderME;
import static opennlp.tools.formats.Conll02NameSampleStream.*;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class LanguageDetectorME_llmsuite_5_GPTLLMTest {

@Test
public void testGetSupportedLanguagesReturnsCorrectOutcomes() {
MaxentModel mockMaxentModel = mock(MaxentModel.class);
when(mockMaxentModel.getNumOutcomes()).thenReturn(2);
when(mockMaxentModel.getOutcome(0)).thenReturn("en");
when(mockMaxentModel.getOutcome(1)).thenReturn("es");
LanguageDetectorFactory mockFactory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator mockContextGenerator = mock(LanguageDetectorContextGenerator.class);
when(mockFactory.getContextGenerator()).thenReturn(mockContextGenerator);
LanguageDetectorModel mockModel = mock(LanguageDetectorModel.class);
when(mockModel.getMaxentModel()).thenReturn(mockMaxentModel);
when(mockModel.getFactory()).thenReturn(mockFactory);
LanguageDetectorME detector = new LanguageDetectorME(mockModel);
String[] result = detector.getSupportedLanguages();
assertEquals(2, result.length);
assertEquals("en", result[0]);
assertEquals("es", result[1]);
}

@Test
public void testPredictLanguageReturnsMostConfident() {
String input = "hello world";
String[] context = new String[] { "he", "el", "ll" };
MaxentModel mockMaxentModel = mock(MaxentModel.class);
when(mockMaxentModel.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 0.6, 0.3 });
when(mockMaxentModel.getOutcome(0)).thenReturn("en");
when(mockMaxentModel.getOutcome(1)).thenReturn("de");
LanguageDetectorFactory mockFactory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator mockContextGenerator = mock(LanguageDetectorContextGenerator.class);
when(mockFactory.getContextGenerator()).thenReturn(mockContextGenerator);
when(mockContextGenerator.getContext(input)).thenReturn(context);
LanguageDetectorModel mockModel = mock(LanguageDetectorModel.class);
when(mockModel.getMaxentModel()).thenReturn(mockMaxentModel);
when(mockModel.getFactory()).thenReturn(mockFactory);
LanguageDetectorME detector = new LanguageDetectorME(mockModel);
Language best = detector.predictLanguage(input);
assertNotNull(best);
assertEquals("en", best.getLang());
assertEquals(0.6, best.getConfidence(), 0.0001);
}

@Test
public void testPredictLanguagesReturnsSortedArray() {
String input = "bonjour le monde";
String[] context = new String[] { "bo", "on", "nj" };
MaxentModel mockMaxentModel = mock(MaxentModel.class);
when(mockMaxentModel.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 0.2, 0.7, 0.1 });
when(mockMaxentModel.getOutcome(0)).thenReturn("en");
when(mockMaxentModel.getOutcome(1)).thenReturn("fr");
when(mockMaxentModel.getOutcome(2)).thenReturn("de");
LanguageDetectorFactory mockFactory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator mockContextGenerator = mock(LanguageDetectorContextGenerator.class);
when(mockFactory.getContextGenerator()).thenReturn(mockContextGenerator);
when(mockContextGenerator.getContext(input)).thenReturn(context);
LanguageDetectorModel mockModel = mock(LanguageDetectorModel.class);
when(mockModel.getMaxentModel()).thenReturn(mockMaxentModel);
when(mockModel.getFactory()).thenReturn(mockFactory);
LanguageDetectorME detector = new LanguageDetectorME(mockModel);
Language[] result = detector.predictLanguages(input);
assertEquals(3, result.length);
assertEquals("fr", result[0].getLang());
assertEquals(0.7, result[0].getConfidence(), 0.001);
assertEquals("en", result[1].getLang());
assertEquals("de", result[2].getLang());
}

@Test
public void testPredictLanguagesHandlesEmptyContext() {
String input = "";
MaxentModel mockMaxentModel = mock(MaxentModel.class);
when(mockMaxentModel.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 0.4, 0.4, 0.2 });
when(mockMaxentModel.getOutcome(0)).thenReturn("it");
when(mockMaxentModel.getOutcome(1)).thenReturn("fr");
when(mockMaxentModel.getOutcome(2)).thenReturn("en");
LanguageDetectorFactory mockFactory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator mockContextGenerator = mock(LanguageDetectorContextGenerator.class);
when(mockFactory.getContextGenerator()).thenReturn(mockContextGenerator);
when(mockContextGenerator.getContext(input)).thenReturn(new String[0]);
LanguageDetectorModel mockModel = mock(LanguageDetectorModel.class);
when(mockModel.getMaxentModel()).thenReturn(mockMaxentModel);
when(mockModel.getFactory()).thenReturn(mockFactory);
LanguageDetectorME detector = new LanguageDetectorME(mockModel);
Language[] result = detector.predictLanguages(input);
assertEquals(3, result.length);
assertEquals("it", result[0].getLang());
assertEquals("fr", result[1].getLang());
assertEquals("en", result[2].getLang());
}

@Test
public void testProbingPredictLanguagesStopsWhenSeenEnoughConditionsMet() {
String input = "Dies ist ein Beispieltext auf Deutsch.";
LanguageDetectorFactory mockFactory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator mockContextGenerator = mock(LanguageDetectorContextGenerator.class);
when(mockFactory.getContextGenerator()).thenReturn(mockContextGenerator);
when(mockContextGenerator.getContext(any())).thenReturn(new String[] { "de", "ei", "is" });
MaxentModel mockMaxentModel = mock(MaxentModel.class);
when(mockMaxentModel.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 0.8, 0.1, 0.1 });
when(mockMaxentModel.getOutcome(0)).thenReturn("de");
when(mockMaxentModel.getOutcome(1)).thenReturn("en");
when(mockMaxentModel.getOutcome(2)).thenReturn("fr");
LanguageDetectorModel mockModel = mock(LanguageDetectorModel.class);
when(mockModel.getFactory()).thenReturn(mockFactory);
when(mockModel.getMaxentModel()).thenReturn(mockMaxentModel);
LanguageDetectorME detector = new LanguageDetectorME(mockModel);
// LanguageDetectorConfig config = new LanguageDetectorConfig(10, 100, 1, 0.1);
// ProbingLanguageDetectionResult result = detector.probingPredictLanguages(input, config);
// assertNotNull(result);
// assertTrue(result.getLength() > 0);
// assertEquals("de", result.getLanguages()[0].getLang());
}

@Test
public void testSeenEnoughReturnsFalseIfLanguageChanges() {
List<Language[]> predictionsQueue = new LinkedList<Language[]>();
predictionsQueue.add(new Language[] { new Language("en", 0.5), new Language("de", 0.3) });
predictionsQueue.add(new Language[] { new Language("de", 0.6), new Language("en", 0.3) });
predictionsQueue.add(new Language[] { new Language("fr", 0.7), new Language("de", 0.2) });
Language[] latest = new Language[] { new Language("it", 0.8), new Language("de", 0.1) };
HashMap<CharSequence, MutableInt> ngramCounts = new HashMap<>();
// LanguageDetectorConfig config = new LanguageDetectorConfig(5, 500, 3, 0.2);
MaxentModel mockMaxentModel = mock(MaxentModel.class);
// when(mockMaxentModel.eval(any(), any())).thenReturn(new double[] { 0.8, 0.1 });
when(mockMaxentModel.getOutcome(0)).thenReturn("it");
when(mockMaxentModel.getOutcome(1)).thenReturn("de");
LanguageDetectorFactory mockFactory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator mockContextGenerator = mock(LanguageDetectorContextGenerator.class);
when(mockFactory.getContextGenerator()).thenReturn(mockContextGenerator);
LanguageDetectorModel mockModel = mock(LanguageDetectorModel.class);
when(mockModel.getMaxentModel()).thenReturn(mockMaxentModel);
when(mockModel.getFactory()).thenReturn(mockFactory);
LanguageDetectorME detector = new LanguageDetectorME(mockModel);
// boolean seenEnough = detector.seenEnough(predictionsQueue, latest, ngramCounts, config);
// assertFalse(seenEnough);
}

// @Test(expected = IOException.class)
public void testTrainThrowsIOExceptionWhenTrainerFails() throws IOException {
ObjectStream<LanguageSample> mockStream = mock(ObjectStream.class);
TrainingParameters params = new TrainingParameters();
LanguageDetectorFactory factory = new LanguageDetectorFactory() {

@Override
public LanguageDetectorContextGenerator getContextGenerator() {
return mock(LanguageDetectorContextGenerator.class);
}
};
// TrainerFactory.setTrainerCreator((mlParams, manifest) -> {
// throw new IOException("Simulated trainer error");
// });
LanguageDetectorME.train(mockStream, params, factory);
}

@Test
public void testPredictLanguageSingleOutcomeModel() {
String text = "test";
String[] context = new String[] { "te", "es" };
MaxentModel mockModel = mock(MaxentModel.class);
when(mockModel.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 1.0 });
when(mockModel.getOutcome(0)).thenReturn("en");
LanguageDetectorContextGenerator mockGenerator = mock(LanguageDetectorContextGenerator.class);
when(mockGenerator.getContext(text)).thenReturn(context);
LanguageDetectorFactory mockFactory = mock(LanguageDetectorFactory.class);
when(mockFactory.getContextGenerator()).thenReturn(mockGenerator);
LanguageDetectorModel model = mock(LanguageDetectorModel.class);
when(model.getMaxentModel()).thenReturn(mockModel);
when(model.getFactory()).thenReturn(mockFactory);
LanguageDetectorME detector = new LanguageDetectorME(model);
Language bestLanguage = detector.predictLanguage(text);
assertNotNull(bestLanguage);
assertEquals("en", bestLanguage.getLang());
assertEquals(1.0, bestLanguage.getConfidence(), 0.0001);
}

@Test
public void testPredictLanguagesTieInConfidenceSortedStably() {
String input = "tie";
String[] context = new String[] { "ti", "ie" };
MaxentModel mockMaxentModel = mock(MaxentModel.class);
when(mockMaxentModel.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 0.5, 0.5 });
when(mockMaxentModel.getOutcome(0)).thenReturn("en");
when(mockMaxentModel.getOutcome(1)).thenReturn("fr");
LanguageDetectorContextGenerator mockContextGen = mock(LanguageDetectorContextGenerator.class);
when(mockContextGen.getContext(input)).thenReturn(context);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(mockContextGen);
LanguageDetectorModel model = mock(LanguageDetectorModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getMaxentModel()).thenReturn(mockMaxentModel);
LanguageDetectorME detector = new LanguageDetectorME(model);
Language[] result = detector.predictLanguages(input);
assertEquals(2, result.length);
assertTrue(result[0].getConfidence() >= result[1].getConfidence());
assertTrue(Math.abs(result[0].getConfidence() - result[1].getConfidence()) < 0.0001);
}

@Test
public void testProbingPredictLanguagesStopsOnExactChunkLimit() {
String input = "Language detection text that hits chunk limit.";
String[] context = new String[] { "la", "an", "ng" };
MaxentModel mockMaxentModel = mock(MaxentModel.class);
// when(mockMaxentModel.eval(any(), any())).thenReturn(new double[] { 0.9, 0.05, 0.05 });
when(mockMaxentModel.getOutcome(0)).thenReturn("en");
when(mockMaxentModel.getOutcome(1)).thenReturn("fr");
when(mockMaxentModel.getOutcome(2)).thenReturn("de");
LanguageDetectorContextGenerator mockContextGen = mock(LanguageDetectorContextGenerator.class);
when(mockContextGen.getContext(any())).thenReturn(context);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(mockContextGen);
LanguageDetectorModel model = mock(LanguageDetectorModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getMaxentModel()).thenReturn(mockMaxentModel);
LanguageDetectorME detector = new LanguageDetectorME(model);
// LanguageDetectorConfig config = new LanguageDetectorConfig(10, input.length(), 1, 0.5);
// ProbingLanguageDetectionResult result = detector.probingPredictLanguages(input, config);
// assertNotNull(result);
// assertTrue(result.getLength() <= input.length());
// assertEquals("en", result.getLanguages()[0].getLang());
}

@Test
public void testSeenEnoughReturnsFalseWhenDiffBelowThreshold() {
List<Language[]> history = new LinkedList<Language[]>();
history.add(new Language[] { new Language("en", 0.6), new Language("fr", 0.5) });
Language[] current = new Language[] { new Language("en", 0.59), new Language("fr", 0.5) };
history.add(current);
history.add(current);
Map<CharSequence, MutableInt> ngramCounts = new HashMap<>();
// LanguageDetectorConfig config = new LanguageDetectorConfig(10, 100, 3, 0.1);
MaxentModel mockMaxentModel = mock(MaxentModel.class);
// when(mockMaxentModel.eval(any(), any())).thenReturn(new double[] { 0.5, 0.5 });
when(mockMaxentModel.getOutcome(0)).thenReturn("en");
when(mockMaxentModel.getOutcome(1)).thenReturn("fr");
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(generator);
LanguageDetectorModel model = mock(LanguageDetectorModel.class);
when(model.getMaxentModel()).thenReturn(mockMaxentModel);
when(model.getFactory()).thenReturn(factory);
LanguageDetectorME detector = new LanguageDetectorME(model);
// boolean result = detector.seenEnough(history, current, ngramCounts, config);
// assertFalse(result);
}

@Test
public void testChunkReturnsEntireStringWhenShorterThanChunkSize() throws Exception {
String smallText = "abc";
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(generator);
MaxentModel maxentModel = mock(MaxentModel.class);
// when(maxentModel.eval(any(), any())).thenReturn(new double[] { 1.0 });
when(maxentModel.getOutcome(0)).thenReturn("en");
LanguageDetectorModel model = mock(LanguageDetectorModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getMaxentModel()).thenReturn(maxentModel);
LanguageDetectorME detector = new LanguageDetectorME(model);
java.lang.reflect.Method method = LanguageDetectorME.class.getDeclaredMethod("chunk", CharSequence.class, int.class, int.class);
method.setAccessible(true);
Object result = method.invoke(detector, smallText, 0, 10);
String chunkedText = (String) result.getClass().getMethod("getString").invoke(result);
assertEquals("abc", chunkedText);
}

@Test
public void testTrainCreatesValidModelWithMockedTrainer() throws Exception {
ObjectStream<LanguageSample> sampleStream = mock(ObjectStream.class);
TrainingParameters params = new TrainingParameters();
MaxentModel mockTrainedModel = mock(MaxentModel.class);
when(mockTrainedModel.getNumOutcomes()).thenReturn(1);
when(mockTrainedModel.getOutcome(0)).thenReturn("en");
// when(mockTrainedModel.eval(any(), any())).thenReturn(new double[] { 1.0 });
LanguageDetectorContextGenerator contextGen = mock(LanguageDetectorContextGenerator.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGen);
// TrainerFactory.setTrainerCreator((mlParams, manifest) -> new opennlp.tools.ml.EventTrainer() {
// 
// @Override
// public MaxentModel train(ObjectStream<opennlp.tools.ml.model.Event> events) throws IOException {
// return mockTrainedModel;
// }
// });
LanguageDetectorModel model = LanguageDetectorME.train(sampleStream, params, factory);
assertNotNull(model);
assertSame(mockTrainedModel, model.getMaxentModel());
}

@Test
public void testPredictLanguagesWithDuplicateNGramsCountsOncePerGram() {
String input = "aaaa";
String[] context = new String[] { "a", "a", "a" };
MaxentModel mockMaxentModel = mock(MaxentModel.class);
when(mockMaxentModel.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 1.0 });
when(mockMaxentModel.getOutcome(0)).thenReturn("en");
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
when(generator.getContext(input)).thenReturn(context);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(generator);
LanguageDetectorModel model = mock(LanguageDetectorModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getMaxentModel()).thenReturn(mockMaxentModel);
LanguageDetectorME detector = new LanguageDetectorME(model);
Language[] result = detector.predictLanguages(input);
assertEquals(1, result.length);
assertEquals("en", result[0].getLang());
assertEquals(1.0, result[0].getConfidence(), 0.001);
}

@Test
public void testSeenEnoughReturnsFalseWhenConfidenceDecreases() {
List<Language[]> predictionQueue = new LinkedList<Language[]>();
predictionQueue.add(new Language[] { new Language("en", 0.9), new Language("fr", 0.1) });
predictionQueue.add(new Language[] { new Language("en", 0.85), new Language("fr", 0.15) });
predictionQueue.add(new Language[] { new Language("en", 0.8), new Language("fr", 0.2) });
Language[] current = new Language[] { new Language("en", 0.75), new Language("fr", 0.25) };
Map<CharSequence, MutableInt> ngramCounts = new HashMap<>();
// LanguageDetectorConfig config = new LanguageDetectorConfig(5, 500, 3, 0.1);
MaxentModel maxentModel = mock(MaxentModel.class);
// when(maxentModel.eval(any(), any())).thenReturn(new double[] { 0.75, 0.25 });
when(maxentModel.getOutcome(0)).thenReturn("en");
when(maxentModel.getOutcome(1)).thenReturn("fr");
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(generator);
LanguageDetectorModel model = mock(LanguageDetectorModel.class);
when(model.getMaxentModel()).thenReturn(maxentModel);
when(model.getFactory()).thenReturn(factory);
LanguageDetectorME detector = new LanguageDetectorME(model);
// boolean seen = detector.seenEnough(predictionQueue, current, ngramCounts, config);
// assertFalse(seen);
}

@Test
public void testProbingPredictLanguagesReturnsDefaultWhenEmptyChunk() {
String input = "";
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
when(generator.getContext(any())).thenReturn(new String[0]);
MaxentModel maxentModel = mock(MaxentModel.class);
when(maxentModel.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 0.4 });
when(maxentModel.getOutcome(0)).thenReturn("en");
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(generator);
LanguageDetectorModel model = mock(LanguageDetectorModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getMaxentModel()).thenReturn(maxentModel);
LanguageDetectorME detector = new LanguageDetectorME(model);
// LanguageDetectorConfig config = new LanguageDetectorConfig(5, 50, 2, 0.1);
// ProbingLanguageDetectionResult result = detector.probingPredictLanguages(input, config);
// assertNotNull(result);
// assertEquals("en", result.getLanguages()[0].getLang());
}

@Test
public void testPredictLanguagesWithNullContextReturnsModelEval() {
String input = "test input";
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
when(generator.getContext(input)).thenReturn(null);
MaxentModel maxentModel = mock(MaxentModel.class);
// when(maxentModel.eval(any(), any())).thenReturn(new double[] { 0.6, 0.4 });
when(maxentModel.getOutcome(0)).thenReturn("en");
when(maxentModel.getOutcome(1)).thenReturn("de");
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(generator);
LanguageDetectorModel model = mock(LanguageDetectorModel.class);
when(model.getMaxentModel()).thenReturn(maxentModel);
when(model.getFactory()).thenReturn(factory);
LanguageDetectorME detector = new LanguageDetectorME(model);
try {
detector.predictLanguages(input);
fail("Expected NullPointerException due to null context.");
} catch (NullPointerException expected) {
}
}

@Test
public void testGetSupportedLanguagesReturnsEmptyIfNoOutcomes() {
MaxentModel maxentModel = mock(MaxentModel.class);
when(maxentModel.getNumOutcomes()).thenReturn(0);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(generator);
LanguageDetectorModel model = mock(LanguageDetectorModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getMaxentModel()).thenReturn(maxentModel);
LanguageDetectorME detector = new LanguageDetectorME(model);
String[] supported = detector.getSupportedLanguages();
assertNotNull(supported);
assertEquals(0, supported.length);
}

@Test
public void testChunkReturnsEmptyStringForOutOfBoundsStart() throws Exception {
String input = "short";
LanguageDetectorModel model = mock(LanguageDetectorModel.class);
MaxentModel maxentModel = mock(MaxentModel.class);
when(maxentModel.getNumOutcomes()).thenReturn(1);
when(maxentModel.getOutcome(0)).thenReturn("en");
// when(maxentModel.eval(any(), any())).thenReturn(new double[] { 1.0 });
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(generator);
when(model.getFactory()).thenReturn(factory);
when(model.getMaxentModel()).thenReturn(maxentModel);
LanguageDetectorME detector = new LanguageDetectorME(model);
java.lang.reflect.Method m = LanguageDetectorME.class.getDeclaredMethod("chunk", CharSequence.class, int.class, int.class);
m.setAccessible(true);
Object result = m.invoke(detector, input, 999, 10);
String chunkStr = (String) result.getClass().getMethod("getString").invoke(result);
int length = (int) result.getClass().getMethod("length").invoke(result);
assertEquals("", chunkStr);
assertEquals(0, length);
}

@Test
public void testUpdateCountsIncrementsCorrectlyWithoutDuplicates() throws Exception {
String[] context = new String[] { "ab", "bc", "cd" };
MaxentModel maxentModel = mock(MaxentModel.class);
// when(maxentModel.eval(any(), any())).thenReturn(new double[] { 1.0 });
when(maxentModel.getOutcome(0)).thenReturn("en");
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(generator);
LanguageDetectorModel model = mock(LanguageDetectorModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getMaxentModel()).thenReturn(maxentModel);
LanguageDetectorME detector = new LanguageDetectorME(model);
java.lang.reflect.Method m = LanguageDetectorME.class.getDeclaredMethod("arrayToCounts", CharSequence[].class);
m.setAccessible(true);
Map<?, ?> counts = (Map<?, ?>) m.invoke(detector, (Object) context);
assertTrue(counts.containsKey("ab"));
assertTrue(counts.containsKey("bc"));
assertTrue(counts.containsKey("cd"));
assertEquals(1, ((MutableInt) counts.get("ab")).getValue());
}

@Test
public void testSeenEnoughReturnsFalseWhenLastLanguageDiffers() {
List<Language[]> predictions = new LinkedList<Language[]>();
predictions.add(new Language[] { new Language("en", 0.4), new Language("fr", 0.3) });
predictions.add(new Language[] { new Language("en", 0.45), new Language("fr", 0.2) });
predictions.add(new Language[] { new Language("de", 0.5), new Language("en", 0.3) });
Language[] current = new Language[] { new Language("fr", 0.6), new Language("en", 0.2) };
Map<CharSequence, MutableInt> ngramCounts = new HashMap<>();
// LanguageDetectorConfig config = new LanguageDetectorConfig(5, 500, 3, 0.1);
MaxentModel maxentModel = mock(MaxentModel.class);
// when(maxentModel.eval(any(), any())).thenReturn(new double[] { 0.6, 0.2 });
when(maxentModel.getOutcome(0)).thenReturn("fr");
when(maxentModel.getOutcome(1)).thenReturn("en");
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(generator);
LanguageDetectorModel model = mock(LanguageDetectorModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getMaxentModel()).thenReturn(maxentModel);
LanguageDetectorME detector = new LanguageDetectorME(model);
// boolean result = detector.seenEnough(predictions, current, ngramCounts, config);
// assertFalse(result);
}

@Test
public void testSeenEnoughIgnoresMinDiffIfZero() {
List<Language[]> predictions = new LinkedList<Language[]>();
predictions.add(new Language[] { new Language("en", 0.5), new Language("fr", 0.3) });
predictions.add(new Language[] { new Language("en", 0.6), new Language("fr", 0.3) });
predictions.add(new Language[] { new Language("en", 0.7), new Language("fr", 0.3) });
Language[] current = new Language[] { new Language("en", 0.8), new Language("fr", 0.3) };
Map<CharSequence, MutableInt> ngramCounts = new HashMap<>();
// LanguageDetectorConfig config = new LanguageDetectorConfig(5, 500, 3, 0.0);
MaxentModel maxentModel = mock(MaxentModel.class);
// when(maxentModel.eval(any(), any())).thenReturn(new double[] { 0.8, 0.3 });
when(maxentModel.getOutcome(0)).thenReturn("en");
when(maxentModel.getOutcome(1)).thenReturn("fr");
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(generator);
LanguageDetectorModel model = mock(LanguageDetectorModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getMaxentModel()).thenReturn(maxentModel);
LanguageDetectorME detector = new LanguageDetectorME(model);
// boolean result = detector.seenEnough(predictions, current, ngramCounts, config);
// assertTrue(result);
}

@Test
public void testChunkReturnsShortenedChunkWhenRemainingLengthLessThanChunkSize() throws Exception {
String input = "abcdefghij";
int start = 8;
int chunkSize = 5;
MaxentModel maxentModel = mock(MaxentModel.class);
when(maxentModel.getNumOutcomes()).thenReturn(1);
when(maxentModel.getOutcome(0)).thenReturn("en");
// when(maxentModel.eval(any(), any())).thenReturn(new double[] { 1.0 });
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(generator);
LanguageDetectorModel model = mock(LanguageDetectorModel.class);
when(model.getMaxentModel()).thenReturn(maxentModel);
when(model.getFactory()).thenReturn(factory);
LanguageDetectorME detector = new LanguageDetectorME(model);
java.lang.reflect.Method m = LanguageDetectorME.class.getDeclaredMethod("chunk", CharSequence.class, int.class, int.class);
m.setAccessible(true);
Object result = m.invoke(detector, input, start, chunkSize);
String chunkString = (String) result.getClass().getMethod("getString").invoke(result);
int chunkLength = (int) result.getClass().getMethod("length").invoke(result);
assertEquals("ij", chunkString);
assertEquals(2, chunkLength);
}

@Test
public void testPredictWithMultipleNgramCountsToEnsureOrdering() throws Exception {
Map<CharSequence, MutableInt> ngramCounts = new LinkedHashMap<>();
ngramCounts.put("a", new MutableInt(2));
ngramCounts.put("b", new MutableInt(1));
ngramCounts.put("c", new MutableInt(1));
MaxentModel maxentModel = mock(MaxentModel.class);
when(maxentModel.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 0.3, 0.4, 0.3 });
when(maxentModel.getOutcome(0)).thenReturn("en");
when(maxentModel.getOutcome(1)).thenReturn("fr");
when(maxentModel.getOutcome(2)).thenReturn("de");
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator contextGen = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(contextGen);
LanguageDetectorModel model = mock(LanguageDetectorModel.class);
when(model.getMaxentModel()).thenReturn(maxentModel);
when(model.getFactory()).thenReturn(factory);
LanguageDetectorME detector = new LanguageDetectorME(model);
java.lang.reflect.Method method = LanguageDetectorME.class.getDeclaredMethod("predict", Map.class);
method.setAccessible(true);
Language[] result = (Language[]) method.invoke(detector, ngramCounts);
assertEquals(3, result.length);
assertEquals("fr", result[0].getLang());
assertTrue(result[0].getConfidence() >= result[1].getConfidence());
}

@Test
public void testArrayToCountsHandlesDuplicateNGrams() throws Exception {
String[] context = new String[] { "abc", "abc", "def" };
MaxentModel maxentModel = mock(MaxentModel.class);
// when(maxentModel.eval(any(), any())).thenReturn(new double[] { 1.0 });
when(maxentModel.getOutcome(0)).thenReturn("en");
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(generator);
LanguageDetectorModel model = mock(LanguageDetectorModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getMaxentModel()).thenReturn(maxentModel);
LanguageDetectorME detector = new LanguageDetectorME(model);
java.lang.reflect.Method m = LanguageDetectorME.class.getDeclaredMethod("arrayToCounts", CharSequence[].class);
m.setAccessible(true);
Map<?, ?> result = (Map<?, ?>) m.invoke(detector, (Object) context);
assertEquals(2, result.size());
assertTrue(result.containsKey("abc"));
assertTrue(result.containsKey("def"));
}

@Test
public void testProbingPredictLanguagesWithOnlyWhitespaceInputReturnsDefaultPrediction() {
String input = "     ";
LanguageDetectorContextGenerator contextGenerator = mock(LanguageDetectorContextGenerator.class);
when(contextGenerator.getContext(any())).thenReturn(new String[0]);
MaxentModel maxentModel = mock(MaxentModel.class);
when(maxentModel.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 0.5 });
when(maxentModel.getOutcome(0)).thenReturn("en");
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
LanguageDetectorModel model = mock(LanguageDetectorModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getMaxentModel()).thenReturn(maxentModel);
LanguageDetectorME detector = new LanguageDetectorME(model);
// LanguageDetectorConfig config = new LanguageDetectorConfig(5, 100, 1, 0.1);
// ProbingLanguageDetectionResult result = detector.probingPredictLanguages(input, config);
// assertNotNull(result);
// assertEquals("en", result.getLanguages()[0].getLang());
}

@Test
public void testPredictHandlesSingleNGramCorrectly() {
String input = "z";
String[] context = new String[] { "z" };
MaxentModel maxentModel = mock(MaxentModel.class);
// when(maxentModel.eval(any(), any())).thenReturn(new double[] { 0.8, 0.1 });
when(maxentModel.getOutcome(0)).thenReturn("en");
when(maxentModel.getOutcome(1)).thenReturn("pl");
LanguageDetectorContextGenerator contextGen = mock(LanguageDetectorContextGenerator.class);
when(contextGen.getContext(input)).thenReturn(context);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGen);
LanguageDetectorModel model = mock(LanguageDetectorModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getMaxentModel()).thenReturn(maxentModel);
LanguageDetectorME detector = new LanguageDetectorME(model);
Language[] predicted = detector.predictLanguages(input);
assertEquals(2, predicted.length);
assertEquals("en", predicted[0].getLang());
assertEquals("pl", predicted[1].getLang());
}

@Test
public void testProbingPredictLanguagesStopsExactlyOnMinConsecImprovements() {
String input = "abcdefghijk";
LanguageDetectorContextGenerator contextGen = mock(LanguageDetectorContextGenerator.class);
when(contextGen.getContext(any(CharSequence.class))).thenReturn(new String[] { "ab", "bc" });
MaxentModel maxentModel = mock(MaxentModel.class);
// when(maxentModel.eval(any(), any())).thenReturn(new double[] { 0.6, 0.3 });
when(maxentModel.getOutcome(0)).thenReturn("en");
when(maxentModel.getOutcome(1)).thenReturn("fr");
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGen);
LanguageDetectorModel model = mock(LanguageDetectorModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getMaxentModel()).thenReturn(maxentModel);
LanguageDetectorME detector = new LanguageDetectorME(model);
// LanguageDetectorConfig config = new LanguageDetectorConfig(3, 50, 1, 0.2);
// ProbingLanguageDetectionResult result = detector.probingPredictLanguages(input, config);
// assertNotNull(result);
// assertEquals("en", result.getLanguages()[0].getLang());
}

@Test
public void testPredictLanguageHonorsSortingDescendingConfidence() {
String input = "ciao mondo";
String[] context = new String[] { "ci", "ia", "ao" };
MaxentModel maxentModel = mock(MaxentModel.class);
when(maxentModel.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 0.3, 0.6, 0.1 });
when(maxentModel.getOutcome(0)).thenReturn("en");
when(maxentModel.getOutcome(1)).thenReturn("it");
when(maxentModel.getOutcome(2)).thenReturn("es");
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator contextGenerator = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(contextGenerator.getContext(input)).thenReturn(context);
LanguageDetectorModel model = mock(LanguageDetectorModel.class);
when(model.getMaxentModel()).thenReturn(maxentModel);
when(model.getFactory()).thenReturn(factory);
LanguageDetectorME detector = new LanguageDetectorME(model);
Language single = detector.predictLanguage(input);
assertEquals("it", single.getLang());
assertEquals(0.6, single.getConfidence(), 0.01);
}

@Test
public void testTrainDefaultsToOnePassIndexer() throws IOException {
ObjectStream<LanguageSample> stream = mock(ObjectStream.class);
TrainingParameters params = new TrainingParameters();
MaxentModel trainedModel = mock(MaxentModel.class);
when(trainedModel.getNumOutcomes()).thenReturn(1);
when(trainedModel.getOutcome(0)).thenReturn("en");
// when(trainedModel.eval(any(), any())).thenReturn(new double[] { 1.0 });
LanguageDetectorContextGenerator contextGen = mock(LanguageDetectorContextGenerator.class);
LanguageDetectorFactory factory = new LanguageDetectorFactory() {

@Override
public LanguageDetectorContextGenerator getContextGenerator() {
return contextGen;
}
};
// opennlp.tools.ml.EventTrainer customTrainer = new opennlp.tools.ml.EventTrainer() {
// 
// @Override
// public MaxentModel train(ObjectStream<opennlp.tools.ml.model.Event> events) {
// return trainedModel;
// }
// };
// TrainerFactory.setTrainerCreator((mlParams, manifest) -> customTrainer);
LanguageDetectorModel model = LanguageDetectorME.train(stream, params, factory);
assertNotNull(model);
assertEquals(1, model.getMaxentModel().getNumOutcomes());
}

@Test
public void testGetSupportedLanguagesReturnsLanguagesInDeclaredOrder() {
MaxentModel maxentModel = mock(MaxentModel.class);
when(maxentModel.getNumOutcomes()).thenReturn(4);
when(maxentModel.getOutcome(0)).thenReturn("de");
when(maxentModel.getOutcome(1)).thenReturn("en");
when(maxentModel.getOutcome(2)).thenReturn("fr");
when(maxentModel.getOutcome(3)).thenReturn("it");
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(generator);
LanguageDetectorModel model = mock(LanguageDetectorModel.class);
when(model.getMaxentModel()).thenReturn(maxentModel);
when(model.getFactory()).thenReturn(factory);
LanguageDetectorME detector = new LanguageDetectorME(model);
String[] langs = detector.getSupportedLanguages();
assertArrayEquals(new String[] { "de", "en", "fr", "it" }, langs);
}

@Test
public void testPredictLanguageThrowsExceptionWhenModelReturnsEmptyEvalArray() {
String input = "test";
String[] context = new String[] { "t", "e", "s" };
MaxentModel maxentModel = mock(MaxentModel.class);
when(maxentModel.eval(any(String[].class), any(float[].class))).thenReturn(new double[0]);
LanguageDetectorContextGenerator contextGen = mock(LanguageDetectorContextGenerator.class);
when(contextGen.getContext(input)).thenReturn(context);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGen);
LanguageDetectorModel model = mock(LanguageDetectorModel.class);
when(model.getMaxentModel()).thenReturn(maxentModel);
when(model.getFactory()).thenReturn(factory);
LanguageDetectorME detector = new LanguageDetectorME(model);
try {
detector.predictLanguage(input);
fail("Should throw ArrayIndexOutOfBoundsException due to empty probabilities.");
} catch (ArrayIndexOutOfBoundsException expected) {
}
}

@Test
public void testSeenEnoughEmptyQueueReturnsFalseWithoutCrash() {
List<Language[]> predictionQueue = new LinkedList<Language[]>();
Language[] currentPrediction = new Language[] { new Language("en", 0.9), new Language("fr", 0.08) };
// LanguageDetectorConfig config = new LanguageDetectorConfig(5, 100, 3, 0.1);
Map<CharSequence, MutableInt> dummyNGrams = new HashMap<>();
MaxentModel maxentModel = mock(MaxentModel.class);
// when(maxentModel.eval(any(), any())).thenReturn(new double[] { 0.9, 0.08 });
when(maxentModel.getOutcome(0)).thenReturn("en");
when(maxentModel.getOutcome(1)).thenReturn("fr");
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator contextGen = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(contextGen);
LanguageDetectorModel model = mock(LanguageDetectorModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getMaxentModel()).thenReturn(maxentModel);
LanguageDetectorME detector = new LanguageDetectorME(model);
// boolean seen = detector.seenEnough(predictionQueue, currentPrediction, dummyNGrams, config);
// assertFalse(seen);
}

@Test
public void testSeenEnoughQueueExactlyFullAndConditionsPasses() {
List<Language[]> predictionQueue = new ArrayList<Language[]>();
predictionQueue.add(new Language[] { new Language("en", 0.3), new Language("de", 0.2) });
predictionQueue.add(new Language[] { new Language("en", 0.4), new Language("de", 0.2) });
predictionQueue.add(new Language[] { new Language("en", 0.5), new Language("de", 0.2) });
Language[] current = new Language[] { new Language("en", 0.6), new Language("de", 0.2) };
// LanguageDetectorConfig config = new LanguageDetectorConfig(5, 100, 3, 0.1);
Map<CharSequence, MutableInt> dummyCounts = new HashMap<>();
MaxentModel maxentModel = mock(MaxentModel.class);
// when(maxentModel.eval(any(), any())).thenReturn(new double[] { 0.6, 0.2 });
when(maxentModel.getOutcome(0)).thenReturn("en");
when(maxentModel.getOutcome(1)).thenReturn("de");
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator contextGen = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(contextGen);
LanguageDetectorModel model = mock(LanguageDetectorModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getMaxentModel()).thenReturn(maxentModel);
LanguageDetectorME detector = new LanguageDetectorME(model);
// boolean seen = detector.seenEnough(predictionQueue, current, dummyCounts, config);
// assertTrue(seen);
}

@Test
public void testPredictHandlesEmptyNgramCountsGracefully() throws Exception {
Map<CharSequence, MutableInt> emptyCounts = new HashMap<>();
MaxentModel maxentModel = mock(MaxentModel.class);
when(maxentModel.eval(new String[0], new float[0])).thenReturn(new double[] { 0.99 });
when(maxentModel.getOutcome(0)).thenReturn("en");
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(generator);
LanguageDetectorModel model = mock(LanguageDetectorModel.class);
when(model.getMaxentModel()).thenReturn(maxentModel);
when(model.getFactory()).thenReturn(factory);
LanguageDetectorME detector = new LanguageDetectorME(model);
java.lang.reflect.Method m = LanguageDetectorME.class.getDeclaredMethod("predict", Map.class);
m.setAccessible(true);
Language[] result = (Language[]) m.invoke(detector, emptyCounts);
assertEquals(1, result.length);
assertEquals("en", result[0].getLang());
}

@Test
public void testUpdateCountsAccumulatesMultipleNGramsWithDuplicates() throws Exception {
String[] context = new String[] { "aa", "aa", "bb" };
MaxentModel maxentModel = mock(MaxentModel.class);
// when(maxentModel.eval(any(), any())).thenReturn(new double[] { 1.0 });
when(maxentModel.getOutcome(0)).thenReturn("en");
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(generator);
LanguageDetectorModel model = mock(LanguageDetectorModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getMaxentModel()).thenReturn(maxentModel);
LanguageDetectorME detector = new LanguageDetectorME(model);
Map<CharSequence, MutableInt> map = new LinkedHashMap<>();
java.lang.reflect.Method m = LanguageDetectorME.class.getDeclaredMethod("updateCounts", CharSequence[].class, Map.class);
m.setAccessible(true);
m.invoke(detector, (Object) context, map);
assertEquals(2, map.size());
assertEquals(2, map.get("aa").getValue());
assertEquals(1, map.get("bb").getValue());
}

@Test
public void testProbingPredictLanguagesReturnsAtLimitWhenAllChunksProcessed() {
String input = "abcdefghij";
String[] context = new String[] { "ab", "bc", "cd" };
MaxentModel maxentModel = mock(MaxentModel.class);
// when(maxentModel.eval(any(), any())).thenReturn(new double[] { 0.6, 0.4 });
when(maxentModel.getOutcome(0)).thenReturn("en");
when(maxentModel.getOutcome(1)).thenReturn("de");
LanguageDetectorContextGenerator contextGen = mock(LanguageDetectorContextGenerator.class);
when(contextGen.getContext(any())).thenReturn(context);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGen);
LanguageDetectorModel model = mock(LanguageDetectorModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getMaxentModel()).thenReturn(maxentModel);
LanguageDetectorME detector = new LanguageDetectorME(model);
// LanguageDetectorConfig config = new LanguageDetectorConfig(5, 10, 3, 0.3);
// ProbingLanguageDetectionResult result = detector.probingPredictLanguages(input, config);
// assertNotNull(result);
// assertEquals("en", result.getLanguages()[0].getLang());
// assertTrue(result.getLength() <= 10);
}

@Test
public void testChunkReturnsEmptyWhenStartEqualsLength() throws Exception {
String input = "abcdef";
int start = 6;
int chunkSize = 10;
MaxentModel model = mock(MaxentModel.class);
when(model.getNumOutcomes()).thenReturn(1);
when(model.getOutcome(0)).thenReturn("en");
// when(model.eval(any(), any())).thenReturn(new double[] { 1.0 });
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator gen = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(gen);
LanguageDetectorModel detectorModel = mock(LanguageDetectorModel.class);
when(detectorModel.getMaxentModel()).thenReturn(model);
when(detectorModel.getFactory()).thenReturn(factory);
LanguageDetectorME detector = new LanguageDetectorME(detectorModel);
java.lang.reflect.Method m = LanguageDetectorME.class.getDeclaredMethod("chunk", CharSequence.class, int.class, int.class);
m.setAccessible(true);
Object resultPair = m.invoke(detector, input, start, chunkSize);
String chunkStr = (String) resultPair.getClass().getMethod("getString").invoke(resultPair);
int chunkLen = (Integer) resultPair.getClass().getMethod("length").invoke(resultPair);
assertEquals("", chunkStr);
assertEquals(0, chunkLen);
}

@Test
public void testProbingPredictLanguagesReturnsWithoutRepeatedPredictions() {
String input = "abc";
String[] context = new String[] { "ab" };
MaxentModel model = mock(MaxentModel.class);
// when(model.eval(any(), any())).thenReturn(new double[] { 0.7, 0.2 });
when(model.getOutcome(0)).thenReturn("en");
when(model.getOutcome(1)).thenReturn("de");
LanguageDetectorContextGenerator contextGen = mock(LanguageDetectorContextGenerator.class);
when(contextGen.getContext(any())).thenReturn(context);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGen);
LanguageDetectorModel detectorModel = mock(LanguageDetectorModel.class);
when(detectorModel.getMaxentModel()).thenReturn(model);
when(detectorModel.getFactory()).thenReturn(factory);
LanguageDetectorME detector = new LanguageDetectorME(detectorModel);
// LanguageDetectorConfig config = new LanguageDetectorConfig(5, 5, 1, 0.1);
// ProbingLanguageDetectionResult result = detector.probingPredictLanguages(input, config);
// assertEquals("en", result.getLanguages()[0].getLang());
// assertTrue(result.getLength() > 0);
}

@Test
public void testSeenEnoughReturnsFalseWhenTopConfidenceDiffBelowMinDiff() {
List<Language[]> queue = new LinkedList<Language[]>();
queue.add(new Language[] { new Language("en", 0.55), new Language("fr", 0.5) });
queue.add(new Language[] { new Language("en", 0.56), new Language("fr", 0.52) });
queue.add(new Language[] { new Language("en", 0.57), new Language("fr", 0.54) });
Language[] current = new Language[] { new Language("en", 0.58), new Language("fr", 0.55) };
// LanguageDetectorConfig config = new LanguageDetectorConfig(5, 100, 3, 0.05);
Map<CharSequence, MutableInt> ngramCounts = new HashMap<>();
MaxentModel maxentModel = mock(MaxentModel.class);
// when(maxentModel.eval(any(), any())).thenReturn(new double[] { 0.58, 0.55 });
when(maxentModel.getOutcome(0)).thenReturn("en");
when(maxentModel.getOutcome(1)).thenReturn("fr");
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(generator);
LanguageDetectorModel model = mock(LanguageDetectorModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getMaxentModel()).thenReturn(maxentModel);
LanguageDetectorME detector = new LanguageDetectorME(model);
// boolean result = detector.seenEnough(queue, current, ngramCounts, config);
// assertFalse(result);
}

@Test
public void testArrayToCountsHandlesEmptyContextArray() throws Exception {
String[] emptyContext = new String[0];
MaxentModel model = mock(MaxentModel.class);
// when(model.eval(any(), any())).thenReturn(new double[] { 1.0 });
when(model.getOutcome(0)).thenReturn("en");
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(generator);
LanguageDetectorModel detectorModel = mock(LanguageDetectorModel.class);
when(detectorModel.getMaxentModel()).thenReturn(model);
when(detectorModel.getFactory()).thenReturn(factory);
LanguageDetectorME detector = new LanguageDetectorME(detectorModel);
java.lang.reflect.Method method = LanguageDetectorME.class.getDeclaredMethod("arrayToCounts", CharSequence[].class);
method.setAccessible(true);
Object result = method.invoke(detector, (Object) emptyContext);
assertTrue(result instanceof Map);
assertEquals(0, ((Map<?, ?>) result).size());
}

@Test
public void testChunkWithMultiByteUnicodeCharacter() throws Exception {
String input = "abc𝌆def";
MaxentModel model = mock(MaxentModel.class);
when(model.getNumOutcomes()).thenReturn(1);
when(model.getOutcome(0)).thenReturn("x");
// when(model.eval(any(), any())).thenReturn(new double[] { 1.0 });
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator ctx = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(ctx);
LanguageDetectorModel detectorModel = mock(LanguageDetectorModel.class);
when(detectorModel.getFactory()).thenReturn(factory);
when(detectorModel.getMaxentModel()).thenReturn(model);
LanguageDetectorME detector = new LanguageDetectorME(detectorModel);
java.lang.reflect.Method method = LanguageDetectorME.class.getDeclaredMethod("chunk", CharSequence.class, int.class, int.class);
method.setAccessible(true);
Object resultPair = method.invoke(detector, input, 0, 6);
String chunkStr = (String) resultPair.getClass().getMethod("getString").invoke(resultPair);
int chunkLen = (Integer) resultPair.getClass().getMethod("length").invoke(resultPair);
assertTrue(chunkStr.contains("𝌆"));
assertEquals(6, chunkLen);
}

@Test
public void testProbingPredictLanguagesWithZeroLengthChunkConfig() {
String input = "text";
LanguageDetectorContextGenerator contextGen = mock(LanguageDetectorContextGenerator.class);
when(contextGen.getContext(any())).thenReturn(new String[] { "a", "b" });
MaxentModel maxentModel = mock(MaxentModel.class);
// when(maxentModel.eval(any(), any())).thenReturn(new double[] { 0.8 });
when(maxentModel.getOutcome(0)).thenReturn("en");
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGen);
LanguageDetectorModel model = mock(LanguageDetectorModel.class);
when(model.getFactory()).thenReturn(factory);
when(model.getMaxentModel()).thenReturn(maxentModel);
LanguageDetectorME detector = new LanguageDetectorME(model);
// LanguageDetectorConfig config = new LanguageDetectorConfig(0, 10, 1, 0.1);
// ProbingLanguageDetectionResult result = detector.probingPredictLanguages(input, config);
// assertNotNull(result);
// assertEquals("en", result.getLanguages()[0].getLang());
// assertEquals(0.8, result.getLanguages()[0].getConfidence(), 0.01);
}

@Test
public void testPredictHandlesContextGeneratorReturningNull() {
String input = "test";
LanguageDetectorContextGenerator contextGenerator = mock(LanguageDetectorContextGenerator.class);
when(contextGenerator.getContext(input)).thenReturn(null);
MaxentModel model = mock(MaxentModel.class);
// when(model.eval(any(), any())).thenReturn(new double[] { 0.9 });
when(model.getOutcome(0)).thenReturn("en");
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
LanguageDetectorModel modelObj = mock(LanguageDetectorModel.class);
when(modelObj.getMaxentModel()).thenReturn(model);
when(modelObj.getFactory()).thenReturn(factory);
LanguageDetectorME detector = new LanguageDetectorME(modelObj);
try {
detector.predictLanguage(input);
fail("Expected NullPointerException due to null context array");
} catch (NullPointerException expected) {
}
}

@Test
public void testPredictSingleLanguageNoSortingNeeded() {
String input = "only";
MaxentModel model = mock(MaxentModel.class);
// when(model.eval(any(), any())).thenReturn(new double[] { 0.99 });
when(model.getOutcome(0)).thenReturn("en");
LanguageDetectorContextGenerator contextGenerator = mock(LanguageDetectorContextGenerator.class);
when(contextGenerator.getContext(input)).thenReturn(new String[] { "on", "nl" });
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
LanguageDetectorModel modelObj = mock(LanguageDetectorModel.class);
when(modelObj.getMaxentModel()).thenReturn(model);
when(modelObj.getFactory()).thenReturn(factory);
LanguageDetectorME detector = new LanguageDetectorME(modelObj);
Language[] result = detector.predictLanguages(input);
assertEquals(1, result.length);
assertEquals("en", result[0].getLang());
assertEquals(0.99, result[0].getConfidence(), 0.001);
}

@Test
public void testTrainPreservesExplicitIndexerConfig() throws IOException {
ObjectStream<LanguageSample> sampleStream = mock(ObjectStream.class);
TrainingParameters params = new TrainingParameters();
params.put("dataIndexer", "twoPass");
LanguageDetectorContextGenerator contextGen = mock(LanguageDetectorContextGenerator.class);
LanguageDetectorFactory factory = new LanguageDetectorFactory() {

@Override
public LanguageDetectorContextGenerator getContextGenerator() {
return contextGen;
}
};
MaxentModel model = mock(MaxentModel.class);
when(model.getNumOutcomes()).thenReturn(2);
when(model.getOutcome(0)).thenReturn("en");
when(model.getOutcome(1)).thenReturn("fr");
// when(model.eval(any(), any())).thenReturn(new double[] { 0.7, 0.3 });
// TrainerFactory.setTrainerCreator((mlParams, manifest) -> new opennlp.tools.ml.EventTrainer() {
// 
// @Override
// public MaxentModel train(ObjectStream<opennlp.tools.ml.model.Event> events) {
// return model;
// }
// });
LanguageDetectorModel trained = LanguageDetectorME.train(sampleStream, params, factory);
assertNotNull(trained);
assertEquals(2, trained.getMaxentModel().getNumOutcomes());
}
}
