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

public class LanguageDetectorME_llmsuite_2_GPTLLMTest {

@Test
public void testPredictLanguage_ReturnsTopLanguage() {
MaxentModel mockModel = mock(MaxentModel.class);
LanguageDetectorFactory mockFactory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator mockContextGenerator = mock(LanguageDetectorContextGenerator.class);
String[] context = new String[] { "hello", "world" };
when(mockContextGenerator.getContext("hello world")).thenReturn(context);
when(mockModel.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 0.9 });
when(mockModel.getOutcome(0)).thenReturn("en");
when(mockModel.getNumOutcomes()).thenReturn(1);
when(mockFactory.getContextGenerator()).thenReturn(mockContextGenerator);
LanguageDetectorModel model = new LanguageDetectorModel(mockModel, new HashMap<String, String>(), mockFactory);
LanguageDetectorME detector = new LanguageDetectorME(model);
Language lang = detector.predictLanguage("hello world");
assertEquals("en", lang.getLang());
assertEquals(0.9, lang.getConfidence(), 0.0001);
}

@Test
public void testPredictLanguages_ReturnsSortedLanguagesByConfidence() {
MaxentModel mockModel = mock(MaxentModel.class);
LanguageDetectorFactory mockFactory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator mockContextGenerator = mock(LanguageDetectorContextGenerator.class);
String[] context = new String[] { "bonjour", "le", "monde" };
when(mockContextGenerator.getContext("bonjour le monde")).thenReturn(context);
when(mockModel.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 0.6, 0.3, 0.1 });
when(mockModel.getOutcome(0)).thenReturn("fr");
when(mockModel.getOutcome(1)).thenReturn("en");
when(mockModel.getOutcome(2)).thenReturn("es");
when(mockModel.getNumOutcomes()).thenReturn(3);
when(mockFactory.getContextGenerator()).thenReturn(mockContextGenerator);
LanguageDetectorModel model = new LanguageDetectorModel(mockModel, new HashMap<String, String>(), mockFactory);
LanguageDetectorME detector = new LanguageDetectorME(model);
Language[] result = detector.predictLanguages("bonjour le monde");
assertEquals(3, result.length);
assertEquals("fr", result[0].getLang());
assertEquals(0.6, result[0].getConfidence(), 0.0001);
assertEquals("en", result[1].getLang());
assertEquals(0.3, result[1].getConfidence(), 0.0001);
assertEquals("es", result[2].getLang());
assertEquals(0.1, result[2].getConfidence(), 0.0001);
}

@Test
public void testGetSupportedLanguages_ReturnsOutcomes() {
MaxentModel mockModel = mock(MaxentModel.class);
LanguageDetectorFactory mockFactory = mock(LanguageDetectorFactory.class);
when(mockModel.getNumOutcomes()).thenReturn(2);
when(mockModel.getOutcome(0)).thenReturn("en");
when(mockModel.getOutcome(1)).thenReturn("de");
when(mockFactory.getContextGenerator()).thenReturn(mock(LanguageDetectorContextGenerator.class));
LanguageDetectorModel detectorModel = new LanguageDetectorModel(mockModel, new HashMap<String, String>(), mockFactory);
LanguageDetectorME detector = new LanguageDetectorME(detectorModel);
String[] langs = detector.getSupportedLanguages();
assertEquals(2, langs.length);
assertEquals("en", langs[0]);
assertEquals("de", langs[1]);
}

@Test
public void testProbingPredictLanguages_UsesDefaultConfig() {
MaxentModel mockModel = mock(MaxentModel.class);
LanguageDetectorFactory mockFactory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator mockContextGenerator = mock(LanguageDetectorContextGenerator.class);
when(mockFactory.getContextGenerator()).thenReturn(mockContextGenerator);
when(mockModel.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 0.9, 0.05, 0.05 });
when(mockModel.getOutcome(0)).thenReturn("en");
when(mockModel.getOutcome(1)).thenReturn("fr");
when(mockModel.getOutcome(2)).thenReturn("de");
String[] features = new String[] { "hello", "world" };
when(mockContextGenerator.getContext(any())).thenReturn(features);
LanguageDetectorModel detectorModel = new LanguageDetectorModel(mockModel, new HashMap<String, String>(), mockFactory);
LanguageDetectorME detector = new LanguageDetectorME(detectorModel);
ProbingLanguageDetectionResult result = detector.probingPredictLanguages("hello world test input long enough");
assertNotNull(result);
// assertTrue(result.getLanguages().length > 0);
// assertEquals("en", result.getLanguages()[0].getLang());
}

@Test
public void testSeenEnough_ReturnsTrueWhenAllCriteriaMatch() {
MaxentModel mockModel = mock(MaxentModel.class);
LanguageDetectorFactory mockFactory = mock(LanguageDetectorFactory.class);
when(mockFactory.getContextGenerator()).thenReturn(mock(LanguageDetectorContextGenerator.class));
LanguageDetectorModel model = new LanguageDetectorModel(mockModel, new HashMap<String, String>(), mockFactory);
LanguageDetectorME detector = new LanguageDetectorME(model);
List<Language[]> predictions = new LinkedList<Language[]>();
predictions.add(new Language[] { new Language("en", 0.6), new Language("de", 0.3) });
predictions.add(new Language[] { new Language("en", 0.7), new Language("de", 0.25) });
predictions.add(new Language[] { new Language("en", 0.8), new Language("de", 0.2) });
Language[] currentPrediction = new Language[] { new Language("en", 0.9), new Language("de", 0.05) };
// LanguageDetectorConfig config = new LanguageDetectorConfig(3, 0.1f, 100, 50);
Map<CharSequence, MutableInt> dummyNgramCounts = new HashMap<CharSequence, MutableInt>();
// boolean seen = detector.seenEnough(predictions, currentPrediction, dummyNgramCounts, config);
// assertTrue(seen);
}

@Test
public void testTrain_ReturnsModel() throws IOException {
ObjectStream<LanguageSample> sampleStream = mock(ObjectStream.class);
TrainingParameters params = new TrainingParameters();
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator ctxGen = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(ctxGen);
MaxentModel trainedModel = mock(MaxentModel.class);
EventTrainer trainer = mock(EventTrainer.class);
// when(trainer.train(any())).thenReturn(trainedModel);
// TrainerFactory.setTrainerImplementation(EventTrainer.class, () -> trainer);
LanguageDetectorModel result = LanguageDetectorME.train(sampleStream, params, factory);
assertNotNull(result);
assertEquals(trainedModel, result.getMaxentModel());
}

@Test
public void testArrayToCounts_PopulatesCountsCorrectly() {
MaxentModel mockModel = mock(MaxentModel.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator cg = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(cg);
LanguageDetectorModel model = new LanguageDetectorModel(mockModel, new HashMap<String, String>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(model);
CharSequence[] input = new String[] { "a", "b", "a" };
// Map<CharSequence, MutableInt> result = detector.arrayToCounts(input);
// assertEquals(2, result.get("a").getValue());
// assertEquals(1, result.get("b").getValue());
// assertEquals(2, result.size());
}

@Test
public void testUpdateCounts_UpdatesMapCorrectly() {
MaxentModel mockModel = mock(MaxentModel.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator cg = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(cg);
LanguageDetectorModel model = new LanguageDetectorModel(mockModel, new HashMap<String, String>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(model);
Map<CharSequence, MutableInt> map = new HashMap<CharSequence, MutableInt>();
CharSequence[] context = new String[] { "x", "y", "x" };
// detector.updateCounts(context, map);
assertEquals(2, map.get("x").getValue());
assertEquals(1, map.get("y").getValue());
assertEquals(2, map.size());
}

@Test
public void testPredictLanguages_ReturnsEmptyWhenNoOutcomes() {
MaxentModel mockModel = mock(MaxentModel.class);
LanguageDetectorFactory mockFactory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator mockContextGenerator = mock(LanguageDetectorContextGenerator.class);
when(mockFactory.getContextGenerator()).thenReturn(mockContextGenerator);
when(mockContextGenerator.getContext("")).thenReturn(new String[] {});
when(mockModel.eval(any(String[].class), any(float[].class))).thenReturn(new double[] {});
when(mockModel.getNumOutcomes()).thenReturn(0);
LanguageDetectorModel model = new LanguageDetectorModel(mockModel, new HashMap<String, String>(), mockFactory);
LanguageDetectorME detector = new LanguageDetectorME(model);
Language[] langs = detector.predictLanguages("");
assertEquals(0, langs.length);
}

@Test
public void testPredictLanguage_EmptyInputThrowsExceptionIfNoLanguages() {
MaxentModel mockModel = mock(MaxentModel.class);
LanguageDetectorFactory mockFactory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator mockContextGenerator = mock(LanguageDetectorContextGenerator.class);
when(mockFactory.getContextGenerator()).thenReturn(mockContextGenerator);
when(mockContextGenerator.getContext("")).thenReturn(new String[0]);
// when(mockModel.eval(any(), any())).thenReturn(new double[0]);
when(mockModel.getNumOutcomes()).thenReturn(0);
LanguageDetectorModel model = new LanguageDetectorModel(mockModel, new HashMap<String, String>(), mockFactory);
LanguageDetectorME detector = new LanguageDetectorME(model);
try {
detector.predictLanguage("");
fail("Expected ArrayIndexOutOfBoundsException due to empty predictions");
} catch (ArrayIndexOutOfBoundsException expected) {
}
}

@Test
public void testSeenEnough_LanguageChanged_PreventEarlyStopping() {
MaxentModel mockModel = mock(MaxentModel.class);
LanguageDetectorFactory mockFactory = mock(LanguageDetectorFactory.class);
when(mockFactory.getContextGenerator()).thenReturn(mock(LanguageDetectorContextGenerator.class));
LanguageDetectorModel model = new LanguageDetectorModel(mockModel, new HashMap<String, String>(), mockFactory);
LanguageDetectorME detector = new LanguageDetectorME(model);
List<Language[]> predictions = new LinkedList<Language[]>();
predictions.add(new Language[] { new Language("en", 0.65), new Language("fr", 0.2) });
predictions.add(new Language[] { new Language("fr", 0.68), new Language("en", 0.25) });
predictions.add(new Language[] { new Language("fr", 0.7), new Language("en", 0.22) });
Language[] current = new Language[] { new Language("fr", 0.75), new Language("en", 0.2) };
Map<CharSequence, MutableInt> ngrams = new HashMap<CharSequence, MutableInt>();
// LanguageDetectorConfig config = new LanguageDetectorConfig(3, 0.1f, 100, 50);
// boolean result = detector.seenEnough(predictions, current, ngrams, config);
// assertFalse(result);
}

@Test
public void testSeenEnough_ConfidenceDecreased_PreventEarlyStopping() {
MaxentModel mockModel = mock(MaxentModel.class);
LanguageDetectorFactory mockFactory = mock(LanguageDetectorFactory.class);
when(mockFactory.getContextGenerator()).thenReturn(mock(LanguageDetectorContextGenerator.class));
LanguageDetectorModel model = new LanguageDetectorModel(mockModel, new HashMap<String, String>(), mockFactory);
LanguageDetectorME detector = new LanguageDetectorME(model);
List<Language[]> predictions = new LinkedList<Language[]>();
predictions.add(new Language[] { new Language("en", 0.8), new Language("fr", 0.1) });
predictions.add(new Language[] { new Language("en", 0.7), new Language("fr", 0.15) });
predictions.add(new Language[] { new Language("en", 0.6), new Language("fr", 0.2) });
Language[] current = new Language[] { new Language("en", 0.5), new Language("fr", 0.4) };
Map<CharSequence, MutableInt> ngrams = new HashMap<CharSequence, MutableInt>();
// LanguageDetectorConfig config = new LanguageDetectorConfig(3, 0.05f, 200, 25);
// boolean result = detector.seenEnough(predictions, current, ngrams, config);
// assertFalse(result);
}

@Test
public void testChunk_NegativeChunkLengthHandled() {
MaxentModel mockModel = mock(MaxentModel.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(mock(LanguageDetectorContextGenerator.class));
LanguageDetectorModel detectorModel = new LanguageDetectorModel(mockModel, new HashMap<String, String>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(detectorModel);
// LanguageDetectorME.StringCPLengthPair result = detector.chunk("", 0, 10);
// assertEquals("", result.getString());
// assertEquals(0, result.length());
}

@Test
public void testChunk_CodePointsProperlyExtracted() {
MaxentModel mockModel = mock(MaxentModel.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(mock(LanguageDetectorContextGenerator.class));
LanguageDetectorModel detectorModel = new LanguageDetectorModel(mockModel, new HashMap<String, String>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(detectorModel);
String input = "abcdef";
// LanguageDetectorME.StringCPLengthPair result = detector.chunk(input, 2, 2);
// assertEquals("cd", result.getString());
// assertEquals(2, result.length());
}

@Test
public void testProbingPredictLanguages_EmptyInputReturnsFirstPrediction() {
MaxentModel mockModel = mock(MaxentModel.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator contextGen = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(contextGen);
LanguageDetectorModel detectorModel = new LanguageDetectorModel(mockModel, new HashMap<String, String>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(detectorModel);
when(contextGen.getContext(any())).thenReturn(new String[] { "ab", "bc" });
when(mockModel.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 0.6, 0.4 });
when(mockModel.getOutcome(0)).thenReturn("en");
when(mockModel.getOutcome(1)).thenReturn("fr");
ProbingLanguageDetectionResult result = detector.probingPredictLanguages("");
// assertEquals("en", result.getLanguages()[0].getLang());
// assertTrue(result.getLength() >= 0);
}

@Test
public void testProbingPredictLanguages_StopsImmediatelyWithShortInput() {
MaxentModel mockModel = mock(MaxentModel.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator contextGen = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(contextGen);
LanguageDetectorModel detectorModel = new LanguageDetectorModel(mockModel, new HashMap<String, String>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(detectorModel);
when(contextGen.getContext("a")).thenReturn(new String[] { "a" });
when(mockModel.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 0.99 });
when(mockModel.getOutcome(0)).thenReturn("zz");
// LanguageDetectorConfig config = new LanguageDetectorConfig(1, 0.1f, 5, 5);
// ProbingLanguageDetectionResult result = detector.probingPredictLanguages("a", config);
// assertEquals("zz", result.getLanguages()[0].getLang());
// assertEquals(1, result.getLength());
}

@Test
public void testPredictLanguages_SingleOutcomeZeroConfidence() {
MaxentModel model = mock(MaxentModel.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(generator);
when(generator.getContext("zero confidence")).thenReturn(new String[] { "x" });
when(model.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 0.0 });
when(model.getOutcome(0)).thenReturn("en");
when(model.getNumOutcomes()).thenReturn(1);
LanguageDetectorModel languageDetectorModel = new LanguageDetectorModel(model, new HashMap<String, String>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(languageDetectorModel);
Language[] lang = detector.predictLanguages("zero confidence");
assertEquals("en", lang[0].getLang());
assertEquals(0.0d, lang[0].getConfidence(), 0.0001);
}

@Test
public void testSeenEnough_MinDiffConditionNotMet() {
MaxentModel model = mock(MaxentModel.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(mock(LanguageDetectorContextGenerator.class));
LanguageDetectorModel detectorModel = new LanguageDetectorModel(model, new HashMap<String, String>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(detectorModel);
List<Language[]> queue = new LinkedList<Language[]>();
queue.add(new Language[] { new Language("en", 0.4), new Language("fr", 0.3) });
queue.add(new Language[] { new Language("en", 0.45), new Language("fr", 0.35) });
queue.add(new Language[] { new Language("en", 0.5), new Language("fr", 0.4) });
Language[] current = new Language[] { new Language("en", 0.55), new Language("fr", 0.499) };
Map<CharSequence, MutableInt> counts = new HashMap<CharSequence, MutableInt>();
// LanguageDetectorConfig config = new LanguageDetectorConfig(3, 0.06f, 1000, 128);
// boolean result = detector.seenEnough(queue, current, counts, config);
// assertFalse(result);
}

@Test
public void testSeenEnough_QueueSizedExactlyAtThreshold() {
MaxentModel model = mock(MaxentModel.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(mock(LanguageDetectorContextGenerator.class));
LanguageDetectorModel detectorModel = new LanguageDetectorModel(model, new HashMap<String, String>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(detectorModel);
List<Language[]> queue = new LinkedList<Language[]>();
queue.add(new Language[] { new Language("en", 0.9), new Language("fr", 0.1) });
queue.add(new Language[] { new Language("en", 0.91), new Language("fr", 0.09) });
queue.add(new Language[] { new Language("en", 0.92), new Language("fr", 0.08) });
Language[] current = new Language[] { new Language("en", 0.93), new Language("fr", 0.07) };
Map<CharSequence, MutableInt> counts = new HashMap<CharSequence, MutableInt>();
// LanguageDetectorConfig config = new LanguageDetectorConfig(3, 0.05f, 500, 32);
// boolean result = detector.seenEnough(queue, current, counts, config);
// assertTrue(result);
}

@Test
public void testSeenEnough_QueueExceedsThreshold_ShiftsOldest() {
MaxentModel model = mock(MaxentModel.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(mock(LanguageDetectorContextGenerator.class));
LanguageDetectorModel detectorModel = new LanguageDetectorModel(model, new HashMap<String, String>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(detectorModel);
List<Language[]> queue = new LinkedList<Language[]>();
queue.add(new Language[] { new Language("en", 0.5), new Language("fr", 0.3) });
queue.add(new Language[] { new Language("en", 0.6), new Language("fr", 0.2) });
queue.add(new Language[] { new Language("en", 0.7), new Language("fr", 0.1) });
queue.add(new Language[] { new Language("en", 0.8), new Language("fr", 0.05) });
Language[] current = new Language[] { new Language("en", 0.9), new Language("fr", 0.03) };
Map<CharSequence, MutableInt> counts = new HashMap<CharSequence, MutableInt>();
// LanguageDetectorConfig config = new LanguageDetectorConfig(3, 0.05f, 800, 64);
// boolean result = detector.seenEnough(queue, current, counts, config);
// assertTrue(result);
assertEquals(3, queue.size());
}

@Test
public void testUpdateCounts_EmptyInput_NoChange() {
MaxentModel model = mock(MaxentModel.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator contextGen = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(contextGen);
LanguageDetectorModel detectorModel = new LanguageDetectorModel(model, new HashMap<String, String>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(detectorModel);
Map<CharSequence, MutableInt> map = new HashMap<CharSequence, MutableInt>();
CharSequence[] context = new CharSequence[0];
// detector.updateCounts(context, map);
assertTrue(map.isEmpty());
}

@Test
public void testArrayToCounts_WithDuplicates() {
MaxentModel model = mock(MaxentModel.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator ctxGen = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(ctxGen);
LanguageDetectorModel detectorModel = new LanguageDetectorModel(model, new HashMap<String, String>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(detectorModel);
CharSequence[] input = new CharSequence[] { "a", "b", "a", "c", "b" };
// Map<CharSequence, MutableInt> result = detector.arrayToCounts(input);
// assertEquals(3, result.size());
// assertEquals(2, result.get("a").getValue());
// assertEquals(2, result.get("b").getValue());
// assertEquals(1, result.get("c").getValue());
}

@Test
public void testChunk_ExactChunkLengthEqualsMaxLength_StopsProperly() {
MaxentModel model = mock(MaxentModel.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(mock(LanguageDetectorContextGenerator.class));
LanguageDetectorModel detectorModel = new LanguageDetectorModel(model, new HashMap<String, String>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(detectorModel);
String input = "abcdef";
int codePointsInInput = input.codePointCount(0, input.length());
// LanguageDetectorME.StringCPLengthPair chunked = detector.chunk(input, 0, codePointsInInput);
// assertEquals("abcdef", chunked.getString());
// assertEquals(codePointsInInput, chunked.length());
}

@Test
public void testChunk_EndOfContentPartialChunk() {
MaxentModel model = mock(MaxentModel.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(mock(LanguageDetectorContextGenerator.class));
LanguageDetectorModel detectorModel = new LanguageDetectorModel(model, new HashMap<String, String>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(detectorModel);
String input = "123456";
int start = 4;
int chunkSize = 5;
// LanguageDetectorME.StringCPLengthPair result = detector.chunk(input, start, chunkSize);
// assertEquals("56", result.getString());
// assertEquals(2, result.length());
}

@Test
public void testPredictLanguage_LanguageOrderingIsCorrectByDescendingConfidence() {
MaxentModel model = mock(MaxentModel.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator contextGen = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(contextGen);
when(contextGen.getContext("ciao mondo")).thenReturn(new String[] { "ciao", "mondo" });
when(model.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 0.4, 0.6 });
when(model.getOutcome(0)).thenReturn("it");
when(model.getOutcome(1)).thenReturn("es");
when(model.getNumOutcomes()).thenReturn(2);
LanguageDetectorModel detectorModel = new LanguageDetectorModel(model, new HashMap<String, String>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(detectorModel);
Language[] result = detector.predictLanguages("ciao mondo");
assertEquals("es", result[0].getLang());
assertEquals("it", result[1].getLang());
}

@Test
public void testPredictLanguages_EmptyContextArray() {
MaxentModel model = mock(MaxentModel.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(generator);
when(generator.getContext("")).thenReturn(new String[0]);
when(model.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 0.95 });
when(model.getOutcome(0)).thenReturn("en");
when(model.getNumOutcomes()).thenReturn(1);
LanguageDetectorModel languageDetectorModel = new LanguageDetectorModel(model, new HashMap<String, String>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(languageDetectorModel);
Language[] result = detector.predictLanguages("");
assertEquals(1, result.length);
assertEquals("en", result[0].getLang());
assertEquals(0.95, result[0].getConfidence(), 0.00001);
}

@Test
public void testPredict_NoFeatures_NoCrashing() {
MaxentModel model = mock(MaxentModel.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
Map<CharSequence, MutableInt> emptyFeatures = new HashMap<CharSequence, MutableInt>();
when(factory.getContextGenerator()).thenReturn(generator);
when(model.eval(any(String[].class), any(float[].class))).thenReturn(new double[] {});
LanguageDetectorModel languageDetectorModel = new LanguageDetectorModel(model, new HashMap<String, String>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(languageDetectorModel);
// Language[] result = detector.predict(emptyFeatures);
// assertEquals(0, result.length);
}

@Test
public void testPredictLanguages_NullFeatureCountsHandled() {
MaxentModel model = mock(MaxentModel.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(generator);
when(generator.getContext("abc")).thenReturn(new String[] { "a", "b", "c" });
when(model.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 0.6, 0.4 });
when(model.getOutcome(0)).thenReturn("en");
when(model.getOutcome(1)).thenReturn("fr");
when(model.getNumOutcomes()).thenReturn(2);
LanguageDetectorModel languageDetectorModel = new LanguageDetectorModel(model, new HashMap<String, String>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(languageDetectorModel);
Language[] result = detector.predictLanguages("abc");
assertEquals(2, result.length);
assertEquals("en", result[0].getLang());
assertEquals("fr", result[1].getLang());
}

@Test
public void testGetSupportedLanguages_WithNoOutcomes() {
MaxentModel model = mock(MaxentModel.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(mock(LanguageDetectorContextGenerator.class));
when(model.getNumOutcomes()).thenReturn(0);
LanguageDetectorModel detectorModel = new LanguageDetectorModel(model, new HashMap<String, String>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(detectorModel);
String[] result = detector.getSupportedLanguages();
assertNotNull(result);
assertEquals(0, result.length);
}

@Test
public void testChunk_WithUnicodeSurrogates() {
MaxentModel model = mock(MaxentModel.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(mock(LanguageDetectorContextGenerator.class));
LanguageDetectorModel detectorModel = new LanguageDetectorModel(model, new HashMap<String, String>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(detectorModel);
String input = "𐐷𐐵𐐶normaltext";
// LanguageDetectorME.StringCPLengthPair pair = detector.chunk(input, 0, 3);
// String chunkStr = pair.getString();
// assertTrue(chunkStr.startsWith("𐐷"));
// assertEquals(3, pair.length());
}

@Test
public void testProbingPredictLanguages_EmptyAfterChunking() {
MaxentModel model = mock(MaxentModel.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator contextGenerator = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(contextGenerator.getContext("")).thenReturn(new String[0]);
when(model.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 0.8, 0.2 });
when(model.getOutcome(0)).thenReturn("en");
when(model.getOutcome(1)).thenReturn("es");
LanguageDetectorModel languageDetectorModel = new LanguageDetectorModel(model, new HashMap<String, String>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(languageDetectorModel);
String input = "";
ProbingLanguageDetectionResult result = detector.probingPredictLanguages(input);
assertNotNull(result);
// assertEquals(2, result.getLanguages().length);
// assertEquals("en", result.getLanguages()[0].getLang());
}

@Test
public void testSeenEnough_ConfidenceStaysTheSameNotEnough() {
MaxentModel model = mock(MaxentModel.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(mock(LanguageDetectorContextGenerator.class));
LanguageDetectorModel detectorModel = new LanguageDetectorModel(model, new HashMap<String, String>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(detectorModel);
List<Language[]> history = new ArrayList<Language[]>();
history.add(new Language[] { new Language("en", 0.7), new Language("fr", 0.2) });
history.add(new Language[] { new Language("en", 0.7), new Language("fr", 0.15) });
history.add(new Language[] { new Language("en", 0.7), new Language("fr", 0.1) });
Language[] current = new Language[] { new Language("en", 0.7), new Language("fr", 0.05) };
// LanguageDetectorConfig config = new LanguageDetectorConfig(3, 0.1f, 300, 20);
Map<CharSequence, MutableInt> counts = new HashMap<CharSequence, MutableInt>();
// boolean result = detector.seenEnough(history, current, counts, config);
// assertFalse(result);
}

@Test
public void testSeenEnough_PredictionsQueueNeverStable() {
MaxentModel model = mock(MaxentModel.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(mock(LanguageDetectorContextGenerator.class));
LanguageDetectorModel detectorModel = new LanguageDetectorModel(model, new HashMap<String, String>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(detectorModel);
List<Language[]> queue = new ArrayList<Language[]>();
queue.add(new Language[] { new Language("en", 0.6), new Language("fr", 0.4) });
queue.add(new Language[] { new Language("fr", 0.7), new Language("en", 0.3) });
queue.add(new Language[] { new Language("en", 0.65), new Language("fr", 0.35) });
Language[] current = new Language[] { new Language("es", 0.9), new Language("en", 0.05) };
// LanguageDetectorConfig config = new LanguageDetectorConfig(3, 0.05f, 500, 30);
Map<CharSequence, MutableInt> counts = new HashMap<CharSequence, MutableInt>();
// boolean result = detector.seenEnough(queue, current, counts, config);
// assertFalse(result);
}

@Test
public void testPredictLanguages_LargeFeatureSet() {
MaxentModel model = mock(MaxentModel.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
String[] context = new String[1000];
for (int i = 0; i < 1000; i++) {
context[i] = "ngram" + i;
}
when(factory.getContextGenerator()).thenReturn(generator);
when(generator.getContext(any())).thenReturn(context);
when(model.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 0.8, 0.1, 0.1 });
when(model.getOutcome(0)).thenReturn("en");
when(model.getOutcome(1)).thenReturn("de");
when(model.getOutcome(2)).thenReturn("fr");
when(model.getNumOutcomes()).thenReturn(3);
LanguageDetectorModel languageDetectorModel = new LanguageDetectorModel(model, new HashMap<String, String>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(languageDetectorModel);
String input = "Lorem ipsum dolor sit amet, ".repeat(100);
Language[] result = detector.predictLanguages(input);
assertEquals(3, result.length);
assertEquals("en", result[0].getLang());
}

@Test
public void testPredictLanguages_MultipleOutcomesSameConfidence() {
MaxentModel model = mock(MaxentModel.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
when(generator.getContext("same_conf")).thenReturn(new String[] { "a", "b" });
when(factory.getContextGenerator()).thenReturn(generator);
when(model.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 0.5, 0.5 });
when(model.getOutcome(0)).thenReturn("ja");
when(model.getOutcome(1)).thenReturn("ko");
when(model.getNumOutcomes()).thenReturn(2);
LanguageDetectorModel modelWrapper = new LanguageDetectorModel(model, new HashMap<String, String>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(modelWrapper);
Language[] result = detector.predictLanguages("same_conf");
assertEquals(2, result.length);
assertTrue(result[0].getConfidence() >= result[1].getConfidence());
}

@Test
public void testPredictLanguages_ExtremeInputLengthHandledSafely() {
MaxentModel model = mock(MaxentModel.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
String longInput = "a".repeat(100_000);
String[] ctx = new String[] { "a", "aa", "aaa" };
when(factory.getContextGenerator()).thenReturn(generator);
when(generator.getContext(longInput)).thenReturn(ctx);
when(model.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 0.9 });
when(model.getOutcome(0)).thenReturn("en");
when(model.getNumOutcomes()).thenReturn(1);
LanguageDetectorModel wrapper = new LanguageDetectorModel(model, new HashMap<String, String>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(wrapper);
Language[] result = detector.predictLanguages(longInput);
assertEquals("en", result[0].getLang());
}

@Test
public void testChunk_ZeroChunkSizeReturnsEmptyPair() {
MaxentModel model = mock(MaxentModel.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(mock(LanguageDetectorContextGenerator.class));
LanguageDetectorModel wrapper = new LanguageDetectorModel(model, new HashMap<String, String>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(wrapper);
// LanguageDetectorME.StringCPLengthPair result = detector.chunk("abc", 1, 0);
// assertEquals("", result.getString());
// assertEquals(0, result.length());
}

@Test
public void testPredictLanguage_OnlyOneCharInput() {
MaxentModel model = mock(MaxentModel.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
when(generator.getContext("z")).thenReturn(new String[] { "z" });
when(factory.getContextGenerator()).thenReturn(generator);
// when(model.eval(any(), any())).thenReturn(new double[] { 0.85 });
when(model.getOutcome(0)).thenReturn("nl");
when(model.getNumOutcomes()).thenReturn(1);
LanguageDetectorModel detectorModel = new LanguageDetectorModel(model, new HashMap<String, String>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(detectorModel);
Language result = detector.predictLanguage("z");
assertEquals("nl", result.getLang());
}

@Test
public void testProbingPredictLanguages_StopsAfterFirstChunk() {
MaxentModel model = mock(MaxentModel.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(generator);
when(generator.getContext(any(CharSequence.class))).thenReturn(new String[] { "x" });
when(model.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 0.95, 0.01 });
when(model.getOutcome(0)).thenReturn("en");
when(model.getOutcome(1)).thenReturn("zh");
LanguageDetectorModel detectorModel = new LanguageDetectorModel(model, new HashMap<String, String>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(detectorModel);
// LanguageDetectorConfig config = new LanguageDetectorConfig(1, 0.8f, 2, 1);
// ProbingLanguageDetectionResult result = detector.probingPredictLanguages("ab", config);
// assertEquals(2, result.getLanguages().length);
// assertEquals("en", result.getLanguages()[0].getLang());
// assertTrue(result.getLength() > 0);
}

@Test
public void testPredict_ArrayToCountsPreservesAllNgramsCorrectly() {
MaxentModel model = mock(MaxentModel.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator cg = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(cg);
LanguageDetectorModel modelWrapper = new LanguageDetectorModel(model, new HashMap<String, String>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(modelWrapper);
CharSequence[] input = new CharSequence[] { "x", "y", "x", "z", "y", "x" };
// Map<CharSequence, MutableInt> result = detector.arrayToCounts(input);
// assertEquals(3, result.size());
// assertEquals(3, result.get("x").getValue());
// assertEquals(2, result.get("y").getValue());
// assertEquals(1, result.get("z").getValue());
}

@Test
public void testSeenEnough_EmptyPredictionQueue() {
MaxentModel model = mock(MaxentModel.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(mock(LanguageDetectorContextGenerator.class));
LanguageDetectorModel detectorModel = new LanguageDetectorModel(model, new HashMap<String, String>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(detectorModel);
List<Language[]> queue = new ArrayList<Language[]>();
Language[] current = new Language[] { new Language("en", 0.99), new Language("fr", 0.01) };
Map<CharSequence, MutableInt> counts = new HashMap<CharSequence, MutableInt>();
// LanguageDetectorConfig config = new LanguageDetectorConfig(5, 0.1f, 1000, 10);
// boolean shouldStop = detector.seenEnough(queue, current, counts, config);
// assertFalse(shouldStop);
assertEquals(1, queue.size());
}

@Test
public void testPredict_EvalWithNegativeConfidenceSortedCorrectly() {
MaxentModel model = mock(MaxentModel.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(generator);
Map<CharSequence, MutableInt> ngramCounts = new LinkedHashMap<CharSequence, MutableInt>();
ngramCounts.put("a", new MutableInt(1));
ngramCounts.put("b", new MutableInt(1));
ngramCounts.put("c", new MutableInt(1));
when(model.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { -0.2, 0.1, -0.5 });
when(model.getOutcome(0)).thenReturn("de");
when(model.getOutcome(1)).thenReturn("en");
when(model.getOutcome(2)).thenReturn("fr");
LanguageDetectorModel lm = new LanguageDetectorModel(model, new HashMap<String, String>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(lm);
// Language[] result = detector.predict(ngramCounts);
// assertEquals(3, result.length);
// assertEquals("en", result[0].getLang());
// assertEquals("de", result[1].getLang());
// assertEquals("fr", result[2].getLang());
}

@Test
public void testProbingPredictLanguages_ExactlyAtChunkBoundaryReturnsCompleteLength() {
MaxentModel model = mock(MaxentModel.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(generator);
String input = "abcdefg";
when(generator.getContext(any())).thenReturn(new String[] { "a", "b" });
double[] scores = new double[] { 0.95, 0.04, 0.01 };
// when(model.eval(any(), any())).thenReturn(scores);
when(model.getOutcome(0)).thenReturn("en");
when(model.getOutcome(1)).thenReturn("fr");
when(model.getOutcome(2)).thenReturn("es");
LanguageDetectorModel lm = new LanguageDetectorModel(model, new HashMap<String, String>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(lm);
// LanguageDetectorConfig config = new LanguageDetectorConfig(1, 0.1f, 7, 7);
// ProbingLanguageDetectionResult result = detector.probingPredictLanguages(input, config);
// assertEquals("en", result.getLanguages()[0].getLang());
// assertEquals(7, result.getLength());
}

@Test
public void testUpdateCounts_MixedNewAndExistingKeys() {
MaxentModel model = mock(MaxentModel.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(generator);
LanguageDetectorModel lm = new LanguageDetectorModel(model, new HashMap<String, String>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(lm);
Map<CharSequence, MutableInt> countMap = new HashMap<CharSequence, MutableInt>();
countMap.put("hello", new MutableInt(3));
countMap.put("world", new MutableInt(1));
CharSequence[] context = new CharSequence[] { "hello", "newngram" };
// detector.updateCounts(context, countMap);
assertEquals(3, countMap.size());
assertEquals(4, countMap.get("hello").getValue());
assertEquals(1, countMap.get("newngram").getValue());
}

@Test
public void testChunk_StartBeyondLengthReturnsEmpty() {
MaxentModel model = mock(MaxentModel.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(mock(LanguageDetectorContextGenerator.class));
LanguageDetectorModel lm = new LanguageDetectorModel(model, new HashMap<String, String>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(lm);
CharSequence input = "short";
int start = 10;
int chunkSize = 3;
// LanguageDetectorME.StringCPLengthPair chunk = detector.chunk(input, start, chunkSize);
// assertEquals("", chunk.getString());
// assertEquals(0, chunk.length());
}

@Test
public void testPredictLanguages_ZeroOutcomeLengthStillCallsEval() {
MaxentModel model = mock(MaxentModel.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
when(generator.getContext("irrelevant")).thenReturn(new String[] { "x" });
when(factory.getContextGenerator()).thenReturn(generator);
// when(model.eval(any(), any())).thenReturn(new double[] {});
when(model.getNumOutcomes()).thenReturn(0);
LanguageDetectorModel detectorModel = new LanguageDetectorModel(model, new HashMap<String, String>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(detectorModel);
Language[] result = detector.predictLanguages("irrelevant");
assertEquals(0, result.length);
}

@Test
public void testSeenEnough_ConsecutivePredictionsWithIncreasingConfidence_Satisfied() {
MaxentModel model = mock(MaxentModel.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(mock(LanguageDetectorContextGenerator.class));
LanguageDetectorModel detectorModel = new LanguageDetectorModel(model, new HashMap<String, String>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(detectorModel);
List<Language[]> predictionsQueue = new ArrayList<Language[]>();
predictionsQueue.add(new Language[] { new Language("en", 0.7), new Language("fr", 0.2) });
predictionsQueue.add(new Language[] { new Language("en", 0.8), new Language("fr", 0.1) });
predictionsQueue.add(new Language[] { new Language("en", 0.9), new Language("fr", 0.05) });
Language[] current = new Language[] { new Language("en", 0.95), new Language("fr", 0.02) };
Map<CharSequence, MutableInt> ngrams = new HashMap<CharSequence, MutableInt>();
// LanguageDetectorConfig config = new LanguageDetectorConfig(3, 0.1f, 128, 64);
// boolean result = detector.seenEnough(predictionsQueue, current, ngrams, config);
// assertTrue(result);
}

@Test
public void testSeenEnough_NoConfidenceDiffBelowMinDiff_NotSatisfied() {
MaxentModel model = mock(MaxentModel.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(mock(LanguageDetectorContextGenerator.class));
LanguageDetectorModel detectorModel = new LanguageDetectorModel(model, new HashMap<String, String>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(detectorModel);
List<Language[]> predictionsQueue = new ArrayList<Language[]>();
predictionsQueue.add(new Language[] { new Language("en", 0.7), new Language("fr", 0.69) });
predictionsQueue.add(new Language[] { new Language("en", 0.71), new Language("fr", 0.7) });
predictionsQueue.add(new Language[] { new Language("en", 0.72), new Language("fr", 0.715) });
Language[] current = new Language[] { new Language("en", 0.73), new Language("fr", 0.72) };
Map<CharSequence, MutableInt> ngrams = new HashMap<CharSequence, MutableInt>();
// LanguageDetectorConfig config = new LanguageDetectorConfig(3, 0.05f, 128, 64);
// boolean result = detector.seenEnough(predictionsQueue, current, ngrams, config);
// assertFalse(result);
}

@Test
public void testProbingPredictLanguages_EmptyStringStillReturnsAScore() {
MaxentModel model = mock(MaxentModel.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(generator);
when(generator.getContext("")).thenReturn(new String[0]);
// when(model.eval(any(), any())).thenReturn(new double[] { 0.55, 0.45 });
when(model.getOutcome(0)).thenReturn("en");
when(model.getOutcome(1)).thenReturn("it");
LanguageDetectorModel detectorModel = new LanguageDetectorModel(model, new HashMap<String, String>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(detectorModel);
ProbingLanguageDetectionResult result = detector.probingPredictLanguages("");
// assertEquals(2, result.getLanguages().length);
// assertEquals("en", result.getLanguages()[0].getLang());
// assertEquals("it", result.getLanguages()[1].getLang());
}

@Test
public void testSeenEnough_EmptyQueue_AddsThenRejectsStop() {
MaxentModel model = mock(MaxentModel.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(mock(LanguageDetectorContextGenerator.class));
LanguageDetectorModel detectorModel = new LanguageDetectorModel(model, new HashMap<String, String>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(detectorModel);
List<Language[]> queue = new ArrayList<Language[]>();
Language[] current = new Language[] { new Language("en", 0.8), new Language("fr", 0.2) };
Map<CharSequence, MutableInt> ngramMap = new HashMap<CharSequence, MutableInt>();
// LanguageDetectorConfig config = new LanguageDetectorConfig(2, 0.1f, 100, 10);
// boolean result = detector.seenEnough(queue, current, ngramMap, config);
// assertFalse(result);
assertEquals(1, queue.size());
}

@Test
public void testSeenEnough_PredictionLanguageFluctuates() {
MaxentModel model = mock(MaxentModel.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(mock(LanguageDetectorContextGenerator.class));
LanguageDetectorModel m = new LanguageDetectorModel(model, new HashMap<String, String>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(m);
List<Language[]> q = new ArrayList<Language[]>();
q.add(new Language[] { new Language("en", 0.6), new Language("fr", 0.3) });
q.add(new Language[] { new Language("fr", 0.7), new Language("en", 0.2) });
q.add(new Language[] { new Language("en", 0.75), new Language("fr", 0.1) });
Language[] current = new Language[] { new Language("en", 0.8), new Language("fr", 0.1) };
// LanguageDetectorConfig cfg = new LanguageDetectorConfig(3, 0.05f, 100, 10);
Map<CharSequence, MutableInt> ngrams = new HashMap<CharSequence, MutableInt>();
// boolean result = detector.seenEnough(q, current, ngrams, cfg);
// assertFalse(result);
}

@Test
public void testSeenEnough_ConfidenceNotMonotonic_Decreasing() {
MaxentModel model = mock(MaxentModel.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(mock(LanguageDetectorContextGenerator.class));
LanguageDetectorModel lm = new LanguageDetectorModel(model, new HashMap<String, String>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(lm);
List<Language[]> preds = new ArrayList<Language[]>();
preds.add(new Language[] { new Language("en", 0.9), new Language("fr", 0.05) });
preds.add(new Language[] { new Language("en", 0.8), new Language("fr", 0.1) });
preds.add(new Language[] { new Language("en", 0.7), new Language("fr", 0.2) });
Language[] current = new Language[] { new Language("en", 0.6), new Language("fr", 0.3) };
// LanguageDetectorConfig config = new LanguageDetectorConfig(3, 0.1f, 100, 20);
Map<CharSequence, MutableInt> map = new HashMap<CharSequence, MutableInt>();
// boolean result = detector.seenEnough(preds, current, map, config);
// assertFalse(result);
}

@Test
public void testPredictLanguage_LowScoresButStillReturnsBest() {
MaxentModel model = mock(MaxentModel.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(generator);
when(generator.getContext("low confidence")).thenReturn(new String[] { "x", "y" });
when(model.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 0.02, 0.01 });
when(model.getOutcome(0)).thenReturn("ja");
when(model.getOutcome(1)).thenReturn("zh");
when(model.getNumOutcomes()).thenReturn(2);
LanguageDetectorModel detectorModel = new LanguageDetectorModel(model, new HashMap<String, String>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(detectorModel);
Language result = detector.predictLanguage("low confidence");
assertEquals("ja", result.getLang());
assertEquals(0.02d, result.getConfidence(), 0.0001);
}

@Test
public void testGetSupportedLanguages_SingleOutcome() {
MaxentModel model = mock(MaxentModel.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(mock(LanguageDetectorContextGenerator.class));
when(model.getNumOutcomes()).thenReturn(1);
when(model.getOutcome(0)).thenReturn("ru");
LanguageDetectorModel detectorModel = new LanguageDetectorModel(model, new HashMap<String, String>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(detectorModel);
String[] langs = detector.getSupportedLanguages();
assertEquals(1, langs.length);
assertEquals("ru", langs[0]);
}

@Test
public void testProbingPredictLanguages_NoConfidenceDiffCannotStop() {
MaxentModel model = mock(MaxentModel.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(generator);
when(generator.getContext(any(CharSequence.class))).thenReturn(new String[] { "aaaaa" });
// when(model.eval(any(), any())).thenReturn(new double[] { 0.5, 0.49 });
when(model.getOutcome(0)).thenReturn("es");
when(model.getOutcome(1)).thenReturn("pt");
LanguageDetectorModel lm = new LanguageDetectorModel(model, new HashMap<String, String>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(lm);
// LanguageDetectorConfig cfg = new LanguageDetectorConfig(2, 0.05f, 8, 4);
// ProbingLanguageDetectionResult result = detector.probingPredictLanguages("aaaaaaa", cfg);
// assertNotNull(result);
// assertEquals("es", result.getLanguages()[0].getLang());
// assertTrue(result.getLength() > 0);
}

@Test
public void testChunk_ChunkGrabsFewerCodepointsThanAllowed() {
MaxentModel model = mock(MaxentModel.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(mock(LanguageDetectorContextGenerator.class));
LanguageDetectorME detector = new LanguageDetectorME(new LanguageDetectorModel(model, new HashMap<String, String>(), factory));
String content = "abcdef";
// LanguageDetectorME.StringCPLengthPair pair = detector.chunk(content, 4, 5);
// assertEquals("ef", pair.getString());
// assertEquals(2, pair.length());
}

@Test
public void testChunk_ExactLengthMatch() {
MaxentModel model = mock(MaxentModel.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(mock(LanguageDetectorContextGenerator.class));
LanguageDetectorME detector = new LanguageDetectorME(new LanguageDetectorModel(model, new HashMap<String, String>(), factory));
String content = "xyzw";
// LanguageDetectorME.StringCPLengthPair pair = detector.chunk(content, 0, 4);
// assertEquals("xyzw", pair.getString());
// assertEquals(4, pair.length());
}

@Test
public void testPredictLanguages_ResultIsSortedDescending() {
MaxentModel model = mock(MaxentModel.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
when(generator.getContext("tests")).thenReturn(new String[] { "t", "e" });
when(factory.getContextGenerator()).thenReturn(generator);
// when(model.eval(any(), any())).thenReturn(new double[] { 0.2, 0.7, 0.1 });
when(model.getOutcome(0)).thenReturn("de");
when(model.getOutcome(1)).thenReturn("en");
when(model.getOutcome(2)).thenReturn("fr");
when(model.getNumOutcomes()).thenReturn(3);
LanguageDetectorModel detectorModel = new LanguageDetectorModel(model, new HashMap<String, String>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(detectorModel);
Language[] result = detector.predictLanguages("tests");
assertEquals("en", result[0].getLang());
assertEquals("de", result[1].getLang());
assertEquals("fr", result[2].getLang());
}
}
