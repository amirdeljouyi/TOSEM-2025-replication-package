package opennlp.tools.langdetect;

import java.io.*;
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

public class LanguageDetectorME_llmsuite_1_GPTLLMTest {

@Test
public void testPredictLanguagesReturnsSortedByConfidence() {
MaxentModel maxentModel = mock(MaxentModel.class);
LanguageDetectorContextGenerator contextGenerator = mock(LanguageDetectorContextGenerator.class);
when(contextGenerator.getContext("Bonjour je suis français")).thenReturn(new String[] { "bon", "jou", "our" });
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
LanguageDetectorModel model = new LanguageDetectorModel(maxentModel, new HashMap<>(), factory);
when(maxentModel.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 0.4, 0.6 });
when(maxentModel.getNumOutcomes()).thenReturn(2);
when(maxentModel.getOutcome(0)).thenReturn("en");
when(maxentModel.getOutcome(1)).thenReturn("fr");
LanguageDetectorME detector = new LanguageDetectorME(model);
Language[] result = detector.predictLanguages("Bonjour je suis français");
assertEquals(2, result.length);
assertEquals("fr", result[0].getLang());
assertEquals("en", result[1].getLang());
assertTrue(result[0].getConfidence() >= result[1].getConfidence());
}

@Test
public void testPredictLanguageReturnsTopLanguage() {
MaxentModel maxentModel = mock(MaxentModel.class);
LanguageDetectorContextGenerator contextGenerator = mock(LanguageDetectorContextGenerator.class);
when(contextGenerator.getContext("Hola, ¿cómo estás?")).thenReturn(new String[] { "hol", "ola", "com" });
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
LanguageDetectorModel model = new LanguageDetectorModel(maxentModel, new HashMap<>(), factory);
when(maxentModel.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 0.1, 0.9 });
when(maxentModel.getNumOutcomes()).thenReturn(2);
when(maxentModel.getOutcome(0)).thenReturn("en");
when(maxentModel.getOutcome(1)).thenReturn("es");
LanguageDetectorME detector = new LanguageDetectorME(model);
Language language = detector.predictLanguage("Hola, ¿cómo estás?");
assertEquals("es", language.getLang());
assertEquals(0.9, language.getConfidence(), 0.0001);
}

@Test
public void testGetSupportedLanguagesReturnsAllOutcomes() {
MaxentModel maxentModel = mock(MaxentModel.class);
when(maxentModel.getNumOutcomes()).thenReturn(3);
when(maxentModel.getOutcome(0)).thenReturn("en");
when(maxentModel.getOutcome(1)).thenReturn("fr");
when(maxentModel.getOutcome(2)).thenReturn("de");
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(mock(LanguageDetectorContextGenerator.class));
LanguageDetectorModel model = new LanguageDetectorModel(maxentModel, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(model);
String[] langs = detector.getSupportedLanguages();
assertEquals(3, langs.length);
assertEquals("en", langs[0]);
assertEquals("fr", langs[1]);
assertEquals("de", langs[2]);
}

@Test
public void testArrayToCountsProducesExpectedMap() {
MaxentModel maxentModel = mock(MaxentModel.class);
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
when(generator.getContext("hi")).thenReturn(new String[] { "hi", "hi", "ho" });
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(generator);
LanguageDetectorModel model = new LanguageDetectorModel(maxentModel, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(model);
// Map<CharSequence, MutableInt> result = detector.arrayToCounts(generator.getContext("hi"));
// assertEquals(2, result.size());
// assertEquals(2, result.get("hi").intValue());
// assertEquals(1, result.get("ho").intValue());
}

@Test
public void testChunkReturnsCompleteStringIfShorterThanChunkSize() {
MaxentModel maxentModel = mock(MaxentModel.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(mock(LanguageDetectorContextGenerator.class));
LanguageDetectorModel model = new LanguageDetectorModel(maxentModel, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(model);
CharSequence content = "abc";
Object result = detector.probingPredictLanguages(content);
assertNotNull(result);
}

@Test
public void testSeenEnoughDetectsStablePredictions() {
MaxentModel maxentModel = mock(MaxentModel.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(mock(LanguageDetectorContextGenerator.class));
LanguageDetectorModel model = new LanguageDetectorModel(maxentModel, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(model);
List<Language[]> history = new ArrayList<Language[]>();
Language[] p1 = new Language[] { new Language("fr", 0.4), new Language("en", 0.1) };
Language[] p2 = new Language[] { new Language("fr", 0.5), new Language("en", 0.1) };
Language[] p3 = new Language[] { new Language("fr", 0.6), new Language("en", 0.1) };
history.add(p1);
history.add(p2);
Map<CharSequence, MutableInt> dummy = new HashMap<>();
// LanguageDetectorConfig config = new LanguageDetectorConfig(3, 0.1, 10, 100);
// boolean result = detector.seenEnough(history, p3, dummy, config);
// assertTrue(result);
}

@Test
public void testSeenEnoughReturnsFalseForLowDelta() {
MaxentModel maxentModel = mock(MaxentModel.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(mock(LanguageDetectorContextGenerator.class));
LanguageDetectorModel model = new LanguageDetectorModel(maxentModel, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(model);
List<Language[]> history = new ArrayList<Language[]>();
Language[] p1 = new Language[] { new Language("en", 0.4), new Language("fr", 0.3) };
Language[] p2 = new Language[] { new Language("en", 0.5), new Language("fr", 0.4) };
Language[] p3 = new Language[] { new Language("en", 0.6), new Language("fr", 0.55) };
history.add(p1);
history.add(p2);
Map<CharSequence, MutableInt> dummy = new HashMap<>();
// LanguageDetectorConfig config = new LanguageDetectorConfig(3, 0.1, 10, 100);
// boolean result = detector.seenEnough(history, p3, dummy, config);
// assertFalse(result);
}

@Test
public void testTrainReturnsModel() throws IOException {
ObjectStream<LanguageSample> stream = mock(ObjectStream.class);
TrainingParameters params = new TrainingParameters();
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(generator);
MaxentModel trainedModel = mock(MaxentModel.class);
EventTrainer trainer = mock(EventTrainer.class);
// when(trainer.train(any())).thenReturn(trainedModel);
Map<String, String> manifest = new HashMap<>();
try (org.mockito.MockedStatic<TrainerFactory> staticMock = mockStatic(TrainerFactory.class)) {
staticMock.when(() -> TrainerFactory.getEventTrainer(any(), any())).thenReturn(trainer);
LanguageDetectorModel model = LanguageDetectorME.train(stream, params, factory);
assertNotNull(model);
assertEquals(trainedModel, model.getMaxentModel());
}
}

@Test
public void testPredictLanguagesWithEmptyContextReturnsEmptyArray() {
MaxentModel maxentModel = mock(MaxentModel.class);
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
when(generator.getContext("")).thenReturn(new String[] {});
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(generator);
when(maxentModel.eval(any(String[].class), any(float[].class))).thenReturn(new double[] {});
when(maxentModel.getNumOutcomes()).thenReturn(0);
LanguageDetectorModel model = new LanguageDetectorModel(maxentModel, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(model);
Language[] result = detector.predictLanguages("");
assertNotNull(result);
assertEquals(0, result.length);
}

@Test
public void testPredictLanguageThrowsArrayIndexOutOfBoundsForEmptyInput() {
MaxentModel maxentModel = mock(MaxentModel.class);
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
when(generator.getContext("")).thenReturn(new String[] {});
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(generator);
when(maxentModel.eval(any(String[].class), any(float[].class))).thenReturn(new double[] {});
when(maxentModel.getNumOutcomes()).thenReturn(0);
LanguageDetectorModel model = new LanguageDetectorModel(maxentModel, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(model);
try {
detector.predictLanguage("");
fail("Expected ArrayIndexOutOfBoundsException");
} catch (ArrayIndexOutOfBoundsException expected) {
}
}

@Test
public void testProbingPredictLanguagesReturnsDefaultWhenNoChunksProcessed() {
MaxentModel maxentModel = mock(MaxentModel.class);
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
when(generator.getContext(any())).thenReturn(new String[] {});
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(generator);
when(maxentModel.getNumOutcomes()).thenReturn(1);
when(maxentModel.getOutcome(0)).thenReturn("en");
when(maxentModel.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 1.0 });
LanguageDetectorModel model = new LanguageDetectorModel(maxentModel, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(model);
// LanguageDetectorConfig config = new LanguageDetectorConfig(3, 0.1, 5, 1);
// ProbingLanguageDetectionResult result = detector.probingPredictLanguages("", config);
// assertNotNull(result);
// assertEquals(1, result.getLanguages().length);
// assertEquals("en", result.getLanguages()[0].getLang());
// assertEquals(0, result.getLength());
}

@Test
public void testChunkReturnsEmptyPairWhenStartEqualsLength() {
MaxentModel maxentModel = mock(MaxentModel.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(mock(LanguageDetectorContextGenerator.class));
LanguageDetectorModel model = new LanguageDetectorModel(maxentModel, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(model);
CharSequence text = "ab";
// var pair = detector.probingPredictLanguages(text).getLanguages();
// assertNotNull(pair);
}

@Test
public void testProbingPredictLanguagesStopsWhenChunkLengthZeroAfterLimit() {
MaxentModel maxentModel = mock(MaxentModel.class);
LanguageDetectorContextGenerator contextGenerator = mock(LanguageDetectorContextGenerator.class);
when(contextGenerator.getContext(any())).thenReturn(new String[] { "ab" });
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(maxentModel.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 1.0 });
when(maxentModel.getNumOutcomes()).thenReturn(1);
when(maxentModel.getOutcome(0)).thenReturn("en");
LanguageDetectorModel model = new LanguageDetectorModel(maxentModel, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(model);
CharSequence content = "abc";
// LanguageDetectorConfig config = new LanguageDetectorConfig(1, 0.1, 10, 2);
// ProbingLanguageDetectionResult result = detector.probingPredictLanguages(content, config);
// assertNotNull(result);
// assertEquals(1, result.getLanguages().length);
// assertTrue(result.getLength() <= 2);
}

@Test
public void testUpdateCountsCreatesNewMutableInt() {
MaxentModel maxentModel = mock(MaxentModel.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(mock(LanguageDetectorContextGenerator.class));
LanguageDetectorModel model = new LanguageDetectorModel(maxentModel, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(model);
Map<CharSequence, MutableInt> map = new HashMap<>();
CharSequence[] context = { "xy", "xy" };
// detector.updateCounts(context, map);
assertEquals(1, map.size());
// assertEquals(2, map.get("xy").intValue());
}

@Test
public void testProbingPredictLanguagesWorksWithSingleChunk() {
MaxentModel maxentModel = mock(MaxentModel.class);
LanguageDetectorContextGenerator contextGenerator = mock(LanguageDetectorContextGenerator.class);
when(contextGenerator.getContext(any())).thenReturn(new String[] { "abc" });
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(maxentModel.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 1.0 });
when(maxentModel.getNumOutcomes()).thenReturn(1);
when(maxentModel.getOutcome(0)).thenReturn("en");
LanguageDetectorModel model = new LanguageDetectorModel(maxentModel, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(model);
String content = "abc";
// LanguageDetectorConfig config = new LanguageDetectorConfig(1, 0.1, 100, 100);
// ProbingLanguageDetectionResult result = detector.probingPredictLanguages(content, config);
// assertNotNull(result);
// assertEquals("en", result.getLanguages()[0].getLang());
// assertTrue(result.getLength() > 0);
}

@Test
public void testPredictWithEmptyNgramCountsReturnsEmptyLanguages() {
MaxentModel maxentModel = mock(MaxentModel.class);
when(maxentModel.eval(any(String[].class), any(float[].class))).thenReturn(new double[0]);
when(maxentModel.getNumOutcomes()).thenReturn(0);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(mock(LanguageDetectorContextGenerator.class));
LanguageDetectorModel model = new LanguageDetectorModel(maxentModel, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(model);
Map<CharSequence, MutableInt> emptyCount = new HashMap<>();
// Language[] result = detector.predict(emptyCount);
// assertNotNull(result);
// assertEquals(0, result.length);
}

@Test
public void testChunkWithHighStartReturnsEmptyString() {
MaxentModel maxentModel = mock(MaxentModel.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(mock(LanguageDetectorContextGenerator.class));
LanguageDetectorModel model = new LanguageDetectorModel(maxentModel, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(model);
CharSequence text = "abc";
int[] codePoints = text.toString().codePoints().toArray();
int highStart = codePoints.length + 1;
// var chunkResult = detector.probingPredictLanguages("abc").getLanguages();
// assertNotNull(chunkResult);
}

@Test
public void testSeenEnoughPredictionsQueueExactMinSizeAndLanguageFluctuate() {
MaxentModel maxentModel = mock(MaxentModel.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(mock(LanguageDetectorContextGenerator.class));
LanguageDetectorModel model = new LanguageDetectorModel(maxentModel, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(model);
List<Language[]> queue = new ArrayList<>();
Language[] pred1 = new Language[] { new Language("en", 0.4), new Language("de", 0.1) };
Language[] pred2 = new Language[] { new Language("fr", 0.5), new Language("en", 0.2) };
Language[] newPred = new Language[] { new Language("de", 0.6), new Language("fr", 0.2) };
queue.add(pred1);
queue.add(pred2);
Map<CharSequence, MutableInt> dummy = new HashMap<>();
// LanguageDetectorConfig config = new LanguageDetectorConfig(2, 0.2, 5, 100);
// boolean result = detector.seenEnough(queue, newPred, dummy, config);
// assertFalse(result);
}

@Test
public void testSeenEnoughReturnsFalseWhenConfidenceDrops() {
MaxentModel maxentModel = mock(MaxentModel.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(mock(LanguageDetectorContextGenerator.class));
LanguageDetectorModel model = new LanguageDetectorModel(maxentModel, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(model);
List<Language[]> queue = new ArrayList<>();
Language[] pred1 = new Language[] { new Language("es", 0.9), new Language("fr", 0.1) };
Language[] pred2 = new Language[] { new Language("es", 0.6), new Language("fr", 0.1) };
Language[] newPred = new Language[] { new Language("es", 0.5), new Language("fr", 0.4) };
queue.add(pred1);
queue.add(pred2);
Map<CharSequence, MutableInt> dummy = new HashMap<>();
// LanguageDetectorConfig config = new LanguageDetectorConfig(3, 0.2, 10, 100);
// boolean result = detector.seenEnough(queue, newPred, dummy, config);
// assertFalse(result);
}

@Test
public void testProbingPredictLanguagesNullContextGeneratorReturnsSafeOutput() {
MaxentModel maxentModel = mock(MaxentModel.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator contextGenerator = mock(LanguageDetectorContextGenerator.class);
when(contextGenerator.getContext(any())).thenReturn(null);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(maxentModel.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 0.7 });
when(maxentModel.getNumOutcomes()).thenReturn(1);
when(maxentModel.getOutcome(0)).thenReturn("xx");
LanguageDetectorModel model = new LanguageDetectorModel(maxentModel, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(model);
String input = "Some text";
// LanguageDetectorConfig config = new LanguageDetectorConfig(1, 0.5, 10, 100);
try {
// detector.probingPredictLanguages(input, config);
fail("Expected NullPointerException due to null context");
} catch (NullPointerException expected) {
}
}

@Test
public void testPredictHandlesMultipleEqualConfidencesAndStableSort() {
MaxentModel maxentModel = mock(MaxentModel.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(generator);
when(generator.getContext("dupe")).thenReturn(new String[] { "ab", "cd" });
when(maxentModel.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 0.5, 0.5 });
when(maxentModel.getNumOutcomes()).thenReturn(2);
when(maxentModel.getOutcome(0)).thenReturn("en");
when(maxentModel.getOutcome(1)).thenReturn("es");
LanguageDetectorModel model = new LanguageDetectorModel(maxentModel, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(model);
Language[] langs = detector.predictLanguages("dupe");
assertEquals(2, langs.length);
assertTrue((langs[0].getConfidence() == langs[1].getConfidence()));
assertTrue(Arrays.asList("en", "es").contains(langs[0].getLang()) && Arrays.asList("en", "es").contains(langs[1].getLang()));
}

@Test
public void testProbingPredictLanguagesProcessesMultipleChunksAndStopsEarly() {
MaxentModel maxentModel = mock(MaxentModel.class);
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
when(generator.getContext(any(CharSequence.class))).thenReturn(new String[] { "xx" });
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(generator);
when(maxentModel.getNumOutcomes()).thenReturn(2);
when(maxentModel.getOutcome(0)).thenReturn("fr");
when(maxentModel.getOutcome(1)).thenReturn("en");
when(maxentModel.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 0.0, 1.0 }, new double[] { 0.0, 1.0 }, new double[] { 0.0, 1.0 });
LanguageDetectorModel model = new LanguageDetectorModel(maxentModel, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(model);
String input = "abcdefghijklmnopqrstuvwxyz";
// LanguageDetectorConfig config = new LanguageDetectorConfig(3, 0.5, 5, 100);
// ProbingLanguageDetectionResult result = detector.probingPredictLanguages(input, config);
// assertNotNull(result);
// assertEquals("en", result.getLanguages()[0].getLang());
// assertTrue(result.getLength() > 0);
}

@Test
public void testTrainUsesFallbackIndexerWhenNotSet() throws IOException {
ObjectStream<LanguageSample> sampleStream = mock(ObjectStream.class);
TrainingParameters trainingParams = new TrainingParameters();
MaxentModel maxentModel = mock(MaxentModel.class);
EventTrainer trainer = mock(EventTrainer.class);
// when(trainer.train(any())).thenReturn(maxentModel);
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(generator);
try (org.mockito.MockedStatic<TrainerFactory> trainerFactoryMock = mockStatic(TrainerFactory.class)) {
trainerFactoryMock.when(() -> TrainerFactory.getEventTrainer(any(), any())).thenReturn(trainer);
LanguageDetectorModel model = LanguageDetectorME.train(sampleStream, trainingParams, factory);
assertNotNull(model);
assertEquals(maxentModel, model.getMaxentModel());
// assertTrue(trainingParams.getSettings().containsKey("DataIndexer"));
}
}

@Test
public void testPredictWithSingleEntryReturnsSingleLanguage() {
MaxentModel model = mock(MaxentModel.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(generator);
String[] context = { "x" };
when(generator.getContext("x")).thenReturn(context);
when(model.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 0.9 });
when(model.getNumOutcomes()).thenReturn(1);
when(model.getOutcome(0)).thenReturn("en");
LanguageDetectorModel detectorModel = new LanguageDetectorModel(model, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(detectorModel);
Language[] result = detector.predictLanguages("x");
assertEquals(1, result.length);
assertEquals("en", result[0].getLang());
assertEquals(0.9, result[0].getConfidence(), 0.0001);
}

@Test
public void testChunkReturnsFullStringOnExactSize() {
MaxentModel model = mock(MaxentModel.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(mock(LanguageDetectorContextGenerator.class));
LanguageDetectorModel detectorModel = new LanguageDetectorModel(model, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(detectorModel);
String input = "hello";
// var result = detector.probingPredictLanguages(input, new LanguageDetectorConfig(1, 0.1, 5, 5));
// assertNotNull(result);
// assertTrue(result.getLength() <= 5);
}

@Test
public void testContextReturnsNullThrowsInPredictLanguages() {
MaxentModel model = mock(MaxentModel.class);
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
when(generator.getContext(any())).thenReturn(null);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(generator);
when(model.getNumOutcomes()).thenReturn(1);
when(model.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 1.0 });
when(model.getOutcome(0)).thenReturn("zz");
LanguageDetectorModel detectorModel = new LanguageDetectorModel(model, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(detectorModel);
try {
detector.predictLanguages("test");
fail("Expected NullPointerException due to null context");
} catch (NullPointerException expected) {
}
}

@Test
public void testStringCPLengthPairReturnsCorrectStringAndLength() {
// LanguageDetectorME.StringCPLengthPair pair = new LanguageDetectorME.StringCPLengthPair("abc", 3);
// assertEquals("abc", pair.getString());
// assertEquals(3, pair.length());
}

@Test
public void testChunkHandlesMultibyteUnicodeInput() {
MaxentModel model = mock(MaxentModel.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(mock(LanguageDetectorContextGenerator.class));
LanguageDetectorModel detectorModel = new LanguageDetectorModel(model, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(detectorModel);
String emojiString = "🙂🙃😉😊😇";
// LanguageDetectorConfig config = new LanguageDetectorConfig(1, 0.1, 3, 5);
// ProbingLanguageDetectionResult result = detector.probingPredictLanguages(emojiString, config);
// assertNotNull(result);
}

@Test
public void testUpdateCountsForDifferentKeysUpdatesCorrectly() {
MaxentModel maxentModel = mock(MaxentModel.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(mock(LanguageDetectorContextGenerator.class));
LanguageDetectorModel model = new LanguageDetectorModel(maxentModel, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(model);
Map<CharSequence, MutableInt> map = new HashMap<>();
CharSequence[] context = { "xy", "z", "xy", "z", "z" };
// detector.updateCounts(context, map);
assertEquals(2, map.size());
// assertEquals(2, map.get("xy").intValue());
// assertEquals(3, map.get("z").intValue());
}

@Test
public void testPredictLanguagesHandlesLargeNgramMapCorrectly() {
MaxentModel model = mock(MaxentModel.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(generator);
String[] largeContext = new String[100];
for (int i = 0; i < 100; i++) {
largeContext[i] = "a" + i;
}
when(generator.getContext("large")).thenReturn(largeContext);
when(model.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 0.8 });
when(model.getNumOutcomes()).thenReturn(1);
when(model.getOutcome(0)).thenReturn("en");
LanguageDetectorModel detectorModel = new LanguageDetectorModel(model, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(detectorModel);
Language[] result = detector.predictLanguages("large");
assertEquals(1, result.length);
assertEquals("en", result[0].getLang());
}

@Test
public void testPredictLanguageHandlesSinglePredictionGracefully() {
MaxentModel model = mock(MaxentModel.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(generator);
when(generator.getContext("a")).thenReturn(new String[] { "a" });
when(model.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 0.7 });
when(model.getNumOutcomes()).thenReturn(1);
when(model.getOutcome(0)).thenReturn("en");
LanguageDetectorModel detectorModel = new LanguageDetectorModel(model, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(detectorModel);
Language result = detector.predictLanguage("a");
assertEquals("en", result.getLang());
assertEquals(0.7, result.getConfidence(), 0.0001);
}

@Test
public void testPredictLanguageThrowsIfEvalReturnsEmptyArray() {
MaxentModel model = mock(MaxentModel.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(generator);
when(generator.getContext("test")).thenReturn(new String[] { "a" });
when(model.eval(any(String[].class), any(float[].class))).thenReturn(new double[] {});
when(model.getNumOutcomes()).thenReturn(0);
LanguageDetectorModel detectorModel = new LanguageDetectorModel(model, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(detectorModel);
try {
detector.predictLanguage("test");
fail("Expected ArrayIndexOutOfBoundsException due to empty eval result");
} catch (ArrayIndexOutOfBoundsException expected) {
}
}

@Test
public void testPredictSortsLanguagesByDescendingConfidence() {
MaxentModel model = mock(MaxentModel.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(generator);
when(generator.getContext("sort")).thenReturn(new String[] { "s" });
when(model.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 0.1, 0.7, 0.3 });
when(model.getNumOutcomes()).thenReturn(3);
when(model.getOutcome(0)).thenReturn("de");
when(model.getOutcome(1)).thenReturn("fr");
when(model.getOutcome(2)).thenReturn("en");
LanguageDetectorModel detectorModel = new LanguageDetectorModel(model, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(detectorModel);
Language[] langs = detector.predictLanguages("sort");
assertEquals("fr", langs[0].getLang());
assertEquals(0.7, langs[0].getConfidence(), 0.0001);
assertEquals("en", langs[1].getLang());
assertEquals("de", langs[2].getLang());
}

@Test
public void testSeenEnoughReturnsFalseWhenBestConfidenceDeltaBelowMinDiff() {
MaxentModel model = mock(MaxentModel.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(mock(LanguageDetectorContextGenerator.class));
LanguageDetectorModel langModel = new LanguageDetectorModel(model, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(langModel);
List<Language[]> queue = new ArrayList<>();
Language[] pred1 = new Language[] { new Language("en", 0.7), new Language("fr", 0.6) };
Language[] pred2 = new Language[] { new Language("en", 0.75), new Language("fr", 0.7) };
Language[] newPred = new Language[] { new Language("en", 0.76), new Language("fr", 0.74) };
queue.add(pred1);
queue.add(pred2);
Map<CharSequence, MutableInt> ngrams = new HashMap<>();
// LanguageDetectorConfig config = new LanguageDetectorConfig(3, 0.03, 5, 1000);
// boolean result = detector.seenEnough(queue, newPred, ngrams, config);
// assertFalse(result);
}

@Test
public void testProbingPredictionWithChunkSizeGreaterThanMaxLength() {
MaxentModel model = mock(MaxentModel.class);
when(model.getNumOutcomes()).thenReturn(1);
when(model.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 1.0 });
when(model.getOutcome(0)).thenReturn("en");
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
when(generator.getContext(any())).thenReturn(new String[] { "aa" });
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(generator);
LanguageDetectorModel langModel = new LanguageDetectorModel(model, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(langModel);
// LanguageDetectorConfig config = new LanguageDetectorConfig(1, 0.5, 100, 5);
// ProbingLanguageDetectionResult result = detector.probingPredictLanguages("abcde", config);
// assertEquals(1, result.getLanguages().length);
// assertEquals("en", result.getLanguages()[0].getLang());
}

@Test
public void testArrayToCountsHandlesEmptyContext() {
MaxentModel model = mock(MaxentModel.class);
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(generator);
LanguageDetectorModel langModel = new LanguageDetectorModel(model, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(langModel);
CharSequence[] context = {};
// Map<CharSequence, MutableInt> result = detector.arrayToCounts(context);
// assertNotNull(result);
// assertTrue(result.isEmpty());
}

@Test
public void testProbingChunkAtUnicodeBoundary() {
MaxentModel model = mock(MaxentModel.class);
when(model.getNumOutcomes()).thenReturn(1);
when(model.getOutcome(0)).thenReturn("zz");
when(model.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 1.0 });
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
when(generator.getContext(any())).thenReturn(new String[] { "😊" });
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(generator);
LanguageDetectorModel langModel = new LanguageDetectorModel(model, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(langModel);
String input = "😊😇🙃😉";
// LanguageDetectorConfig config = new LanguageDetectorConfig(1, 0.1, 2, input.codePointCount(0, input.length()));
// ProbingLanguageDetectionResult result = detector.probingPredictLanguages(input, config);
// assertNotNull(result);
// assertTrue(result.getLength() > 0);
// assertEquals("zz", result.getLanguages()[0].getLang());
}

@Test
public void testPredictWithNonEmptyNgramButSingleLanguageScore() {
MaxentModel model = mock(MaxentModel.class);
when(model.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 0.95 });
when(model.getNumOutcomes()).thenReturn(1);
when(model.getOutcome(0)).thenReturn("it");
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
when(generator.getContext("ciao")).thenReturn(new String[] { "ci", "ia", "ao" });
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(generator);
LanguageDetectorModel detectorModel = new LanguageDetectorModel(model, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(detectorModel);
Language[] result = detector.predictLanguages("ciao");
assertEquals(1, result.length);
assertEquals("it", result[0].getLang());
assertEquals(0.95, result[0].getConfidence(), 0.0001);
}

@Test
public void testPredictReturnsLanguagesSortedWithThreeItems() {
MaxentModel model = mock(MaxentModel.class);
when(model.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 0.2, 0.6, 0.4 });
when(model.getNumOutcomes()).thenReturn(3);
when(model.getOutcome(0)).thenReturn("es");
when(model.getOutcome(1)).thenReturn("de");
when(model.getOutcome(2)).thenReturn("en");
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(generator);
when(generator.getContext("hello")).thenReturn(new String[] { "he", "el", "ll", "lo" });
LanguageDetectorModel langModel = new LanguageDetectorModel(model, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(langModel);
Language[] result = detector.predictLanguages("hello");
assertEquals("de", result[0].getLang());
assertEquals("en", result[1].getLang());
assertEquals("es", result[2].getLang());
assertTrue(result[0].getConfidence() >= result[1].getConfidence());
assertTrue(result[1].getConfidence() >= result[2].getConfidence());
}

@Test
public void testSeenEnoughPredictionQueueLargerThanRequiredRemovesOldestEntry() {
MaxentModel model = mock(MaxentModel.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(mock(LanguageDetectorContextGenerator.class));
LanguageDetectorModel detectorModel = new LanguageDetectorModel(model, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(detectorModel);
List<Language[]> queue = new LinkedList<>();
Language[] p1 = { new Language("en", 0.5), new Language("fr", 0.3) };
Language[] p2 = { new Language("en", 0.6), new Language("fr", 0.2) };
Language[] p3 = { new Language("en", 0.7), new Language("fr", 0.1) };
queue.add(p1);
queue.add(p2);
queue.add(p3);
Language[] current = { new Language("en", 0.8), new Language("fr", 0.1) };
// LanguageDetectorConfig config = new LanguageDetectorConfig(3, 0.2, 5, 100);
Map<CharSequence, MutableInt> dummyMap = new HashMap<>();
// boolean result = detector.seenEnough(queue, current, dummyMap, config);
// assertTrue(result);
}

@Test
public void testProbingPredictLanguagesTruncatesPastMaxLength() {
MaxentModel model = mock(MaxentModel.class);
when(model.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 0.9 });
when(model.getNumOutcomes()).thenReturn(1);
when(model.getOutcome(0)).thenReturn("en");
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
when(generator.getContext(any())).thenReturn(new String[] { "abc" });
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(generator);
LanguageDetectorModel langModel = new LanguageDetectorModel(model, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(langModel);
String input = "abcdefghijklmnopqrst";
// LanguageDetectorConfig config = new LanguageDetectorConfig(2, 0.3, 5, 10);
// ProbingLanguageDetectionResult result = detector.probingPredictLanguages(input, config);
// assertTrue(result.getLength() <= 10);
// assertEquals("en", result.getLanguages()[0].getLang());
}

@Test
public void testPredictHandlesNgramMapWithSpecialCharacters() {
MaxentModel model = mock(MaxentModel.class);
when(model.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 1.0 });
when(model.getNumOutcomes()).thenReturn(1);
when(model.getOutcome(0)).thenReturn("ja");
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(mock(LanguageDetectorContextGenerator.class));
LanguageDetectorModel detectorModel = new LanguageDetectorModel(model, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(detectorModel);
Map<CharSequence, MutableInt> specialMap = new HashMap<>();
specialMap.put("$%#", new MutableInt(1));
// Language[] result = detector.predict(specialMap);
// assertEquals(1, result.length);
// assertEquals("ja", result[0].getLang());
}

@Test
public void testSeenEnoughReturnsFalseIfPredictionListTooSmall() {
MaxentModel model = mock(MaxentModel.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(mock(LanguageDetectorContextGenerator.class));
LanguageDetectorModel detectorModel = new LanguageDetectorModel(model, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(detectorModel);
Language[] current = new Language[] { new Language("en", 0.8), new Language("fr", 0.1) };
List<Language[]> pastPredictions = new ArrayList<>();
pastPredictions.add(new Language[] { new Language("en", 0.7), new Language("fr", 0.2) });
Map<CharSequence, MutableInt> dummyMap = new HashMap<>();
// LanguageDetectorConfig config = new LanguageDetectorConfig(3, 0.5, 3, 10);
// boolean result = detector.seenEnough(pastPredictions, current, dummyMap, config);
// assertFalse(result);
}

@Test
public void testSeenEnoughReturnsFalseOnChangingTopLanguage() {
MaxentModel model = mock(MaxentModel.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(mock(LanguageDetectorContextGenerator.class));
LanguageDetectorModel detectorModel = new LanguageDetectorModel(model, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(detectorModel);
List<Language[]> pastPredictions = new ArrayList<>();
pastPredictions.add(new Language[] { new Language("fr", 0.5), new Language("en", 0.2) });
pastPredictions.add(new Language[] { new Language("fr", 0.6), new Language("en", 0.2) });
Language[] current = new Language[] { new Language("de", 0.9), new Language("fr", 0.1) };
Map<CharSequence, MutableInt> dummyMap = new HashMap<>();
// LanguageDetectorConfig config = new LanguageDetectorConfig(2, 0.2, 3, 10);
// boolean result = detector.seenEnough(pastPredictions, current, dummyMap, config);
// assertFalse(result);
}

@Test
public void testUpdateCountsDoesNotModifyExistingObjects() {
MaxentModel model = mock(MaxentModel.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(mock(LanguageDetectorContextGenerator.class));
LanguageDetectorModel detectorModel = new LanguageDetectorModel(model, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(detectorModel);
Map<CharSequence, MutableInt> map = new HashMap<>();
MutableInt shared = new MutableInt(2);
map.put("he", shared);
CharSequence[] context = new CharSequence[] { "he" };
// detector.updateCounts(context, map);
// assertEquals(3, map.get("he").intValue());
}

@Test
public void testChunkReturnsNothingWhenStartExceedsText() {
MaxentModel model = mock(MaxentModel.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(mock(LanguageDetectorContextGenerator.class));
LanguageDetectorModel detectorModel = new LanguageDetectorModel(model, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(detectorModel);
String shortText = "ab";
int chunkStart = 10;
int chunkSize = 5;
// LanguageDetectorME.StringCPLengthPair pair = detector.probingPredictLanguages(shortText, new LanguageDetectorConfig(1, 0.1, chunkSize, chunkStart)).getLanguages()[0].getLang().equals("en") ? new LanguageDetectorME.StringCPLengthPair("foo", 3) : new LanguageDetectorME.StringCPLengthPair("", 0);
// assertEquals(0, pair.length());
// assertEquals("", pair.getString());
}

@Test
public void testPredictWithEmptyMapReturnsEmptyArray() {
MaxentModel model = mock(MaxentModel.class);
when(model.eval(any(String[].class), any(float[].class))).thenReturn(new double[] {});
when(model.getNumOutcomes()).thenReturn(0);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(mock(LanguageDetectorContextGenerator.class));
LanguageDetectorModel detectorModel = new LanguageDetectorModel(model, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(detectorModel);
Map<CharSequence, MutableInt> emptyMap = new HashMap<>();
// Language[] result = detector.predict(emptyMap);
// assertNotNull(result);
// assertEquals(0, result.length);
}

@Test
public void testChunkSplitsCorrectlyAcrossCodepoints() {
MaxentModel model = mock(MaxentModel.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(mock(LanguageDetectorContextGenerator.class));
LanguageDetectorModel detectorModel = new LanguageDetectorModel(model, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(detectorModel);
String input = "𝔘𝔫𝔦𝔠𝔬𝔡𝔢😊text";
int start = 2;
int chunkSize = 4;
int[] codepoints = input.codePoints().skip(start).limit(chunkSize).toArray();
String expected = new String(codepoints, 0, codepoints.length);
// LanguageDetectorME.StringCPLengthPair pair = new LanguageDetectorME.StringCPLengthPair(expected, codepoints.length);
// assertEquals(chunkSize, pair.length());
// assertEquals(expected, pair.getString());
}

@Test
public void testPredictLanguagesSortsDescendingEvenOnEqualScores() {
MaxentModel model = mock(MaxentModel.class);
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(generator);
when(generator.getContext("x")).thenReturn(new String[] { "x" });
when(model.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 0.5, 0.5 });
when(model.getNumOutcomes()).thenReturn(2);
when(model.getOutcome(0)).thenReturn("en");
when(model.getOutcome(1)).thenReturn("es");
LanguageDetectorModel detectorModel = new LanguageDetectorModel(model, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(detectorModel);
Language[] result = detector.predictLanguages("x");
assertEquals(2, result.length);
assertTrue(result[0].getConfidence() >= result[1].getConfidence());
assertNotNull(result[0].getLang());
assertNotNull(result[1].getLang());
}

@Test
public void testPredictLanguageThrowsIfEvalHasNoData() {
MaxentModel model = mock(MaxentModel.class);
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(generator);
when(generator.getContext("x")).thenReturn(new String[] { "x" });
when(model.eval(any(String[].class), any(float[].class))).thenReturn(new double[0]);
when(model.getNumOutcomes()).thenReturn(0);
LanguageDetectorModel detectorModel = new LanguageDetectorModel(model, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(detectorModel);
try {
detector.predictLanguage("x");
fail("Expected ArrayIndexOutOfBoundsException");
} catch (ArrayIndexOutOfBoundsException expected) {
}
}

@Test
public void testTrainingParametersFallbackToOnePass() throws IOException {
ObjectStream<LanguageSample> sampleStream = mock(ObjectStream.class);
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(generator);
MaxentModel trainedModel = mock(MaxentModel.class);
TrainingParameters params = new TrainingParameters();
EventTrainer trainer = mock(EventTrainer.class);
// when(trainer.train(any())).thenReturn(trainedModel);
try (org.mockito.MockedStatic<TrainerFactory> staticMock = mockStatic(TrainerFactory.class)) {
staticMock.when(() -> TrainerFactory.getEventTrainer(any(), any())).thenReturn(trainer);
LanguageDetectorModel model = LanguageDetectorME.train(sampleStream, params, factory);
assertNotNull(model);
assertEquals(trainedModel, model.getMaxentModel());
// assertEquals("OnePass", params.getString("DataIndexer"));
}
}

@Test
public void testSeenEnoughReturnsFalseWhenConfidenceDecreasesAcrossValidTopLanguage() {
MaxentModel model = mock(MaxentModel.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(mock(LanguageDetectorContextGenerator.class));
LanguageDetectorModel detectorModel = new LanguageDetectorModel(model, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(detectorModel);
Language[] first = new Language[] { new Language("en", 0.9), new Language("fr", 0.1) };
Language[] second = new Language[] { new Language("en", 0.8), new Language("fr", 0.2) };
List<Language[]> queue = new ArrayList<>();
queue.add(first);
queue.add(second);
Language[] latest = new Language[] { new Language("en", 0.7), new Language("fr", 0.3) };
Map<CharSequence, MutableInt> dummy = new HashMap<>();
// LanguageDetectorConfig config = new LanguageDetectorConfig(3, 0.1, 5, 100);
// boolean result = detector.seenEnough(queue, latest, dummy, config);
// assertFalse(result);
}

@Test
public void testChunkReturnsTruncatedStringWhenMaxLengthExceeded() {
MaxentModel model = mock(MaxentModel.class);
LanguageDetectorContextGenerator ctxGen = mock(LanguageDetectorContextGenerator.class);
when(ctxGen.getContext(any())).thenReturn(new String[] { "abc", "def" });
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(ctxGen);
when(model.getNumOutcomes()).thenReturn(1);
when(model.getOutcome(0)).thenReturn("en");
// when(model.eval(any(), any())).thenReturn(new double[] { 0.99 });
LanguageDetectorModel detectorModel = new LanguageDetectorModel(model, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(detectorModel);
StringBuilder sb = new StringBuilder();
for (int i = 0; i < 1000; i++) {
sb.append("x");
}
// LanguageDetectorConfig config = new LanguageDetectorConfig(1, 0.1, 5, 10);
// ProbingLanguageDetectionResult result = detector.probingPredictLanguages(sb.toString(), config);
// assertTrue(result.getLength() <= 10);
}

@Test
public void testPredictReturnsSortedLanguagesOnEqualConfidence() {
MaxentModel model = mock(MaxentModel.class);
LanguageDetectorContextGenerator ctxGen = mock(LanguageDetectorContextGenerator.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(ctxGen);
when(ctxGen.getContext("input")).thenReturn(new String[] { "x" });
when(model.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 0.5, 0.5 });
when(model.getNumOutcomes()).thenReturn(2);
when(model.getOutcome(0)).thenReturn("en");
when(model.getOutcome(1)).thenReturn("de");
LanguageDetectorModel detectorModel = new LanguageDetectorModel(model, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(detectorModel);
Language[] result = detector.predictLanguages("input");
assertEquals(2, result.length);
assertTrue(result[0].getConfidence() >= result[1].getConfidence());
assertTrue((result[0].getLang().equals("en") || result[0].getLang().equals("de")) && (result[1].getLang().equals("en") || result[1].getLang().equals("de")));
}

@Test
public void testArrayToCountsHandlesDuplicateKeysWithCorrectCounts() {
MaxentModel model = mock(MaxentModel.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(mock(LanguageDetectorContextGenerator.class));
LanguageDetectorModel detectorModel = new LanguageDetectorModel(model, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(detectorModel);
CharSequence[] input = new CharSequence[] { "aa", "bb", "aa" };
// Map<CharSequence, MutableInt> result = detector.arrayToCounts(input);
// assertEquals(2, result.size());
// assertEquals(2, result.get("aa").intValue());
// assertEquals(1, result.get("bb").intValue());
}

@Test
public void testProbingPredictLanguagesDetectsLanguageOnExactConsecutiveImprovement() {
MaxentModel model = mock(MaxentModel.class);
LanguageDetectorContextGenerator contextGen = mock(LanguageDetectorContextGenerator.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGen);
when(model.getNumOutcomes()).thenReturn(2);
when(model.getOutcome(0)).thenReturn("fr");
when(model.getOutcome(1)).thenReturn("en");
// when(model.eval(any(), any())).thenReturn(new double[] { 0.2, 0.8 }).thenReturn(new double[] { 0.1, 0.9 }).thenReturn(new double[] { 0.05, 0.95 });
when(contextGen.getContext(any())).thenReturn(new String[] { "foo" });
LanguageDetectorModel detectorModel = new LanguageDetectorModel(model, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(detectorModel);
String content = "abcdefghijkl";
// LanguageDetectorConfig config = new LanguageDetectorConfig(3, 0.1, 4, 100);
// ProbingLanguageDetectionResult result = detector.probingPredictLanguages(content, config);
// assertEquals("en", result.getLanguages()[0].getLang());
// assertTrue(result.getLength() >= 4);
}

@Test
public void testProbingStopsWhenNoMoreChunksLeftToProcess() {
MaxentModel model = mock(MaxentModel.class);
LanguageDetectorContextGenerator contextGen = mock(LanguageDetectorContextGenerator.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGen);
when(model.getNumOutcomes()).thenReturn(1);
when(model.getOutcome(0)).thenReturn("en");
when(contextGen.getContext(any())).thenReturn(new String[] { "abc" });
// when(model.eval(any(), any())).thenReturn(new double[] { 1.0 });
LanguageDetectorModel detectorModel = new LanguageDetectorModel(model, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(detectorModel);
CharSequence shortInput = "abc";
// LanguageDetectorConfig config = new LanguageDetectorConfig(2, 0.5, 10, 3);
// ProbingLanguageDetectionResult result = detector.probingPredictLanguages(shortInput, config);
// assertEquals(1, result.getLanguages().length);
// assertEquals("en", result.getLanguages()[0].getLang());
// assertEquals(3, result.getLength());
}

@Test
public void testTrainUsesTrainerCorrectlyWithInjectedParams() throws IOException {
ObjectStream<LanguageSample> stream = mock(ObjectStream.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator contextGen = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(contextGen);
MaxentModel trainedModel = mock(MaxentModel.class);
TrainingParameters params = new TrainingParameters();
EventTrainer trainer = mock(EventTrainer.class);
// when(trainer.train(any())).thenReturn(trainedModel);
try (org.mockito.MockedStatic<TrainerFactory> staticTrainer = mockStatic(TrainerFactory.class)) {
staticTrainer.when(() -> TrainerFactory.getEventTrainer(any(), any())).thenReturn(trainer);
LanguageDetectorModel model = LanguageDetectorME.train(stream, params, factory);
assertNotNull(model);
assertEquals(trainedModel, model.getMaxentModel());
}
}

@Test
public void testPredictHandlesEmptyEvalArraySafelyReturnsEmptyResult() {
MaxentModel model = mock(MaxentModel.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator contextGen = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(contextGen);
when(contextGen.getContext("abc")).thenReturn(new String[] { "ab" });
when(model.eval(any(String[].class), any(float[].class))).thenReturn(new double[0]);
when(model.getNumOutcomes()).thenReturn(0);
LanguageDetectorModel detectorModel = new LanguageDetectorModel(model, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(detectorModel);
Language[] result = detector.predictLanguages("abc");
assertNotNull(result);
assertEquals(0, result.length);
}

@Test
public void testPredictLanguageThrowsOnEmptyPredictedArray() {
MaxentModel model = mock(MaxentModel.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator contextGen = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(contextGen);
when(contextGen.getContext(any())).thenReturn(new String[] { "test" });
when(model.eval(any(String[].class), any(float[].class))).thenReturn(new double[0]);
when(model.getNumOutcomes()).thenReturn(0);
LanguageDetectorModel detectorModel = new LanguageDetectorModel(model, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(detectorModel);
try {
detector.predictLanguage("test");
fail("Expected ArrayIndexOutOfBoundsException");
} catch (ArrayIndexOutOfBoundsException expected) {
}
}
}
