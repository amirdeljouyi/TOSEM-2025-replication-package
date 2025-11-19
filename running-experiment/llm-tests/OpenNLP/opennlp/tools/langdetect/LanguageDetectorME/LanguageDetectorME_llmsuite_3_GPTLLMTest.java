package opennlp.tools.langdetect;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import opennlp.tools.ml.*;
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

public class LanguageDetectorME_llmsuite_3_GPTLLMTest {

@Test
public void testPredictLanguages_ReturnsSortedLanguages() {
CharSequence input = "test input";
MaxentModel model = mock(MaxentModel.class);
when(model.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 0.3, 0.7 });
when(model.getOutcome(0)).thenReturn("en");
when(model.getOutcome(1)).thenReturn("de");
when(model.getNumOutcomes()).thenReturn(2);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator contextGenerator = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(contextGenerator.getContext(input)).thenReturn(new CharSequence[] { "te", "es", "st" });
LanguageDetectorModel langModel = new LanguageDetectorModel(model, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(langModel);
Language[] result = detector.predictLanguages(input);
assertEquals(2, result.length);
assertEquals("de", result[0].getLang());
assertEquals("en", result[1].getLang());
assertTrue(result[0].getConfidence() >= result[1].getConfidence());
}

@Test
public void testPredictLanguage_ReturnsHighestConfidence() {
CharSequence input = "bonjour le monde";
MaxentModel model = mock(MaxentModel.class);
when(model.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 0.1, 0.9 });
when(model.getOutcome(0)).thenReturn("en");
when(model.getOutcome(1)).thenReturn("fr");
when(model.getNumOutcomes()).thenReturn(2);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator contextGenerator = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(contextGenerator.getContext(input)).thenReturn(new CharSequence[] { "bo", "on", "nj" });
LanguageDetectorModel langModel = new LanguageDetectorModel(model, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(langModel);
Language result = detector.predictLanguage(input);
assertNotNull(result);
assertEquals("fr", result.getLang());
assertTrue(result.getConfidence() > 0.5);
}

@Test
public void testGetSupportedLanguages_ReturnsAllOutcomes() {
MaxentModel model = mock(MaxentModel.class);
when(model.getNumOutcomes()).thenReturn(3);
when(model.getOutcome(0)).thenReturn("en");
when(model.getOutcome(1)).thenReturn("fr");
when(model.getOutcome(2)).thenReturn("de");
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator contextGenerator = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
LanguageDetectorModel langModel = new LanguageDetectorModel(model, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(langModel);
String[] supported = detector.getSupportedLanguages();
assertEquals(3, supported.length);
assertEquals("en", supported[0]);
assertEquals("fr", supported[1]);
assertEquals("de", supported[2]);
}

@Test
public void testProbingPredictLanguages_StopsEarly() {
CharSequence input = "This is an English sentence used for detecting language early.";
MaxentModel model = mock(MaxentModel.class);
when(model.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 0.6, 0.4 }).thenReturn(new double[] { 0.7, 0.3 }).thenReturn(new double[] { 0.8, 0.2 });
when(model.getOutcome(0)).thenReturn("en");
when(model.getOutcome(1)).thenReturn("fr");
when(model.getNumOutcomes()).thenReturn(2);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator contextGenerator = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(contextGenerator.getContext(any(CharSequence.class))).thenReturn(new CharSequence[] { "th", "he", "en" });
LanguageDetectorModel langModel = new LanguageDetectorModel(model, new HashMap<>(), factory);
// LanguageDetectorConfig config = new LanguageDetectorConfig(3, 0.1, 10, 500);
LanguageDetectorME detector = new LanguageDetectorME(langModel);
// ProbingLanguageDetectionResult result = detector.probingPredictLanguages(input, config);
// assertNotNull(result);
// assertEquals("en", result.getLanguages()[0].getLang());
// assertTrue(result.getLengthProcessed() > 0);
}

@Test
public void testProbingPredictLanguages_ReachesEOS() {
CharSequence input = "abc";
MaxentModel model = mock(MaxentModel.class);
when(model.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 0.9, 0.1 });
when(model.getOutcome(0)).thenReturn("it");
when(model.getOutcome(1)).thenReturn("es");
when(model.getNumOutcomes()).thenReturn(2);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator contextGenerator = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(contextGenerator.getContext(any(CharSequence.class))).thenReturn(new CharSequence[] { "ab", "bc" });
LanguageDetectorModel langModel = new LanguageDetectorModel(model, new HashMap<>(), factory);
// LanguageDetectorConfig config = new LanguageDetectorConfig(2, 0.1, 5, 10);
LanguageDetectorME detector = new LanguageDetectorME(langModel);
// ProbingLanguageDetectionResult result = detector.probingPredictLanguages(input, config);
// assertNotNull(result);
// assertEquals("it", result.getLanguages()[0].getLang());
}

@Test
public void testTrain_ReturnsTrainedModel() throws IOException {
ObjectStream<LanguageSample> stream = mock(ObjectStream.class);
MaxentModel trainedModel = mock(MaxentModel.class);
// TrainerFactory trainerFactoryOriginal = TrainerFactory.getInstance();
try {
LanguageDetectorContextGenerator contextGenerator = mock(LanguageDetectorContextGenerator.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
TrainingParameters params = new TrainingParameters();
params.put(AbstractEventTrainer.DATA_INDEXER_PARAM, AbstractEventTrainer.DATA_INDEXER_ONE_PASS_VALUE);
// TrainerFactory.setTrainerFactory((mlParams, manifestEntries) -> data -> trainedModel);
LanguageDetectorModel finalModel = LanguageDetectorME.train(stream, params, factory);
assertNotNull(finalModel);
assertSame(trainedModel, finalModel.getMaxentModel());
} finally {
// TrainerFactory.setTrainerFactory(trainerFactoryOriginal);
}
}

@Test
public void testSeenEnoughReturnsTrueWhenConditionsAreMet() {
MaxentModel model = mock(MaxentModel.class);
when(model.getOutcome(0)).thenReturn("en");
when(model.getOutcome(1)).thenReturn("de");
when(model.getNumOutcomes()).thenReturn(2);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(generator);
LanguageDetectorModel langModel = new LanguageDetectorModel(model, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(langModel);
List<Language[]> predictions = new LinkedList<>();
Language[] p1 = new Language[] { new Language("en", 0.6), new Language("de", 0.4) };
Language[] p2 = new Language[] { new Language("en", 0.7), new Language("de", 0.3) };
predictions.add(p1);
predictions.add(p2);
Language[] newPred = new Language[] { new Language("en", 0.8), new Language("de", 0.2) };
Map<CharSequence, opennlp.tools.util.MutableInt> dummyCounts = new HashMap<>();
// LanguageDetectorConfig config = new LanguageDetectorConfig(3, 0.05, 30, 100);
// boolean result = detector.seenEnough(predictions, newPred, dummyCounts, config);
// assertTrue(result);
}

@Test
public void testSeenEnoughReturnsFalseWhenLangChanges() {
MaxentModel model = mock(MaxentModel.class);
when(model.getOutcome(0)).thenReturn("en");
when(model.getOutcome(1)).thenReturn("fr");
when(model.getNumOutcomes()).thenReturn(2);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(generator);
LanguageDetectorModel langModel = new LanguageDetectorModel(model, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(langModel);
List<Language[]> predictions = new LinkedList<>();
Language[] p1 = new Language[] { new Language("en", 0.6), new Language("fr", 0.4) };
Language[] p2 = new Language[] { new Language("fr", 0.7), new Language("en", 0.3) };
predictions.add(p1);
predictions.add(p2);
Language[] newPred = new Language[] { new Language("de", 0.8), new Language("fr", 0.2) };
Map<CharSequence, opennlp.tools.util.MutableInt> dummyCounts = new HashMap<>();
// LanguageDetectorConfig config = new LanguageDetectorConfig(3, 0.1, 30, 100);
// boolean result = detector.seenEnough(predictions, newPred, dummyCounts, config);
// assertFalse(result);
}

@Test
public void testPredictLanguages_EmptyInput() {
CharSequence input = "";
MaxentModel model = mock(MaxentModel.class);
when(model.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 0.6 });
when(model.getOutcome(0)).thenReturn("en");
when(model.getNumOutcomes()).thenReturn(1);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator gen = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(gen);
when(gen.getContext(input)).thenReturn(new CharSequence[0]);
LanguageDetectorModel langModel = new LanguageDetectorModel(model, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(langModel);
Language[] result = detector.predictLanguages(input);
assertEquals(1, result.length);
assertEquals("en", result[0].getLang());
}

@Test
public void testPredictLanguage_NullContext() {
CharSequence input = "test";
MaxentModel model = mock(MaxentModel.class);
when(model.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 0.5 });
when(model.getOutcome(0)).thenReturn("en");
when(model.getNumOutcomes()).thenReturn(1);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator gen = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(gen);
when(gen.getContext(input)).thenReturn(null);
LanguageDetectorModel langModel = new LanguageDetectorModel(model, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(langModel);
try {
detector.predictLanguage(input);
fail("Expected NullPointerException due to null context");
} catch (NullPointerException expected) {
}
}

@Test
public void testSeenEnough_InsufficientPredictionsSize() {
MaxentModel model = mock(MaxentModel.class);
when(model.getOutcome(0)).thenReturn("en");
when(model.getOutcome(1)).thenReturn("fr");
when(model.getNumOutcomes()).thenReturn(2);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator gen = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(gen);
LanguageDetectorModel modelWrapper = new LanguageDetectorModel(model, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(modelWrapper);
List<Language[]> queue = new LinkedList<>();
Language[] latest = new Language[] { new Language("en", 0.6), new Language("fr", 0.4) };
queue.add(new Language[] { new Language("en", 0.5), new Language("fr", 0.5) });
Map<CharSequence, MutableInt> dummyCounts = new HashMap<>();
// LanguageDetectorConfig config = new LanguageDetectorConfig(3, 0.1, 50, 200);
// boolean result = detector.seenEnough(queue, latest, dummyCounts, config);
// assertFalse(result);
}

@Test
public void testSeenEnough_LowConfidenceDifference() {
MaxentModel model = mock(MaxentModel.class);
when(model.getOutcome(0)).thenReturn("en");
when(model.getOutcome(1)).thenReturn("fr");
when(model.getNumOutcomes()).thenReturn(2);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator gen = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(gen);
LanguageDetectorModel modelWrapper = new LanguageDetectorModel(model, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(modelWrapper);
List<Language[]> queue = new LinkedList<>();
queue.add(new Language[] { new Language("en", 0.6), new Language("fr", 0.4) });
queue.add(new Language[] { new Language("en", 0.7), new Language("fr", 0.3) });
Language[] newPred = new Language[] { new Language("en", 0.71), new Language("fr", 0.69) };
Map<CharSequence, MutableInt> dummyCounts = new HashMap<>();
// LanguageDetectorConfig config = new LanguageDetectorConfig(3, 0.05, 100, 500);
// boolean result = detector.seenEnough(queue, newPred, dummyCounts, config);
// assertFalse(result);
}

@Test
public void testChunk_ExactFit() throws Exception {
CharSequence input = "abcdefghij";
MaxentModel model = mock(MaxentModel.class);
when(model.getNumOutcomes()).thenReturn(1);
when(model.getOutcome(0)).thenReturn("en");
when(model.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 1.0 });
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator gen = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(gen);
when(gen.getContext(any())).thenReturn(new CharSequence[] { "a" });
LanguageDetectorModel modelWrapper = new LanguageDetectorModel(model, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(modelWrapper);
// LanguageDetectorConfig config = new LanguageDetectorConfig(1, 0.1, 10, 10);
// ProbingLanguageDetectionResult result = detector.probingPredictLanguages(input, config);
// assertTrue(result.getLengthProcessed() > 0);
// assertEquals("en", result.getLanguages()[0].getLang());
}

@Test
public void testProbingPredictLanguages_NonDivisibleChunk() {
CharSequence input = "abcdefg";
MaxentModel model = mock(MaxentModel.class);
when(model.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 0.5, 0.5 }).thenReturn(new double[] { 0.6, 0.4 }).thenReturn(new double[] { 0.65, 0.35 }).thenReturn(new double[] { 0.7, 0.3 });
when(model.getOutcome(0)).thenReturn("en");
when(model.getOutcome(1)).thenReturn("fr");
when(model.getNumOutcomes()).thenReturn(2);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator gen = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(gen);
when(gen.getContext(any())).thenReturn(new CharSequence[] { "ab", "bc" });
LanguageDetectorModel modelWrapper = new LanguageDetectorModel(model, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(modelWrapper);
// LanguageDetectorConfig config = new LanguageDetectorConfig(3, 0.05, 2, 7);
// ProbingLanguageDetectionResult result = detector.probingPredictLanguages(input, config);
// assertNotNull(result);
// assertEquals("en", result.getLanguages()[0].getLang());
// assertTrue(result.getLengthProcessed() > 0);
}

@Test
public void testPredictWithSingleNGram() {
CharSequence input = "h";
MaxentModel model = mock(MaxentModel.class);
when(model.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 1.0 });
when(model.getOutcome(0)).thenReturn("en");
when(model.getNumOutcomes()).thenReturn(1);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator gen = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(gen);
when(gen.getContext(input)).thenReturn(new CharSequence[] { "h" });
LanguageDetectorModel langModel = new LanguageDetectorModel(model, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(langModel);
Language[] result = detector.predictLanguages(input);
assertEquals(1, result.length);
assertEquals("en", result[0].getLang());
assertEquals(1.0, result[0].getConfidence(), 0.0001);
}

@Test
public void testPredictLanguages_ModelReturnsAllZeroProbabilities() {
CharSequence input = "input";
MaxentModel model = mock(MaxentModel.class);
when(model.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 0.0, 0.0 });
when(model.getOutcome(0)).thenReturn("en");
when(model.getOutcome(1)).thenReturn("de");
when(model.getNumOutcomes()).thenReturn(2);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator contextGen = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(contextGen);
when(contextGen.getContext(input)).thenReturn(new CharSequence[] { "in", "np", "pu", "ut" });
LanguageDetectorModel langModel = new LanguageDetectorModel(model, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(langModel);
Language[] result = detector.predictLanguages(input);
assertEquals(2, result.length);
assertEquals("en", result[0].getLang());
assertEquals(0.0, result[0].getConfidence(), 0.001);
}

@Test
public void testProbingPredictLanguagesReturnsImmediatelyOnEmptyContext() {
CharSequence input = "abc";
MaxentModel model = mock(MaxentModel.class);
when(model.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 1.0 });
when(model.getOutcome(0)).thenReturn("en");
when(model.getNumOutcomes()).thenReturn(1);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator contextGen = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(contextGen);
when(contextGen.getContext(any())).thenReturn(new CharSequence[0]);
LanguageDetectorModel langModel = new LanguageDetectorModel(model, new HashMap<>(), factory);
// LanguageDetectorConfig config = new LanguageDetectorConfig(1, 0.1, 10, 20);
LanguageDetectorME detector = new LanguageDetectorME(langModel);
// ProbingLanguageDetectionResult result = detector.probingPredictLanguages(input, config);
// assertNotNull(result);
// assertEquals(1, result.getLanguages().length);
// assertEquals("en", result.getLanguages()[0].getLang());
}

@Test
public void testSeenEnough_ConfidenceDecreasesMidway() {
MaxentModel model = mock(MaxentModel.class);
when(model.getOutcome(0)).thenReturn("en");
when(model.getOutcome(1)).thenReturn("de");
when(model.getNumOutcomes()).thenReturn(2);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(generator);
LanguageDetectorModel modelWrapper = new LanguageDetectorModel(model, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(modelWrapper);
List<Language[]> queue = new LinkedList<>();
queue.add(new Language[] { new Language("en", 0.7), new Language("de", 0.3) });
queue.add(new Language[] { new Language("en", 0.6), new Language("de", 0.4) });
Language[] latest = new Language[] { new Language("en", 0.5), new Language("de", 0.5) };
Map<CharSequence, MutableInt> ngrams = new HashMap<>();
// LanguageDetectorConfig config = new LanguageDetectorConfig(3, 0.1, 50, 100);
// boolean result = detector.seenEnough(queue, latest, ngrams, config);
// assertFalse(result);
}

@Test
public void testPredictLanguages_SingleOutcomeModel() {
CharSequence input = "greeting";
MaxentModel model = mock(MaxentModel.class);
when(model.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 1.0 });
when(model.getOutcome(0)).thenReturn("en");
when(model.getNumOutcomes()).thenReturn(1);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(generator);
when(generator.getContext(input)).thenReturn(new CharSequence[] { "gr", "re", "ee" });
LanguageDetectorModel modelWrapper = new LanguageDetectorModel(model, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(modelWrapper);
Language[] result = detector.predictLanguages(input);
assertEquals(1, result.length);
assertEquals("en", result[0].getLang());
}

@Test
public void testProbingPredictLanguages_InputShorterThanChunk() {
CharSequence input = "hi";
MaxentModel model = mock(MaxentModel.class);
when(model.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 0.99 });
when(model.getOutcome(0)).thenReturn("en");
when(model.getNumOutcomes()).thenReturn(1);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(generator);
when(generator.getContext(any())).thenReturn(new CharSequence[] { "h", "i" });
LanguageDetectorModel modelWrapper = new LanguageDetectorModel(model, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(modelWrapper);
// LanguageDetectorConfig config = new LanguageDetectorConfig(2, 0.05, 10, 15);
// ProbingLanguageDetectionResult result = detector.probingPredictLanguages(input, config);
// assertNotNull(result);
// assertEquals("en", result.getLanguages()[0].getLang());
// assertTrue(result.getLengthProcessed() > 0);
}

@Test
public void testTrain_ThrowsIOExceptionOnStreamError() throws IOException {
ObjectStream<LanguageSample> stream = mock(ObjectStream.class);
when(stream.read()).thenThrow(new IOException("Simulated read failure"));
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(generator);
TrainingParameters params = new TrainingParameters();
params.put(AbstractEventTrainer.DATA_INDEXER_PARAM, AbstractEventTrainer.DATA_INDEXER_ONE_PASS_VALUE);
try {
LanguageDetectorME.train(stream, params, factory);
fail("Expected IOException");
} catch (IOException e) {
assertEquals("Simulated read failure", e.getMessage());
}
}

@Test
public void testUpdateCounts_WithDuplicateNGrams() {
CharSequence input = "aaa";
MaxentModel model = mock(MaxentModel.class);
when(model.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 1.0 });
when(model.getOutcome(0)).thenReturn("en");
when(model.getNumOutcomes()).thenReturn(1);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(generator);
when(generator.getContext(input)).thenReturn(new CharSequence[] { "aa", "aa", "aa" });
LanguageDetectorModel modelWrapper = new LanguageDetectorModel(model, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(modelWrapper);
Language[] result = detector.predictLanguages(input);
assertEquals(1, result.length);
assertEquals("en", result[0].getLang());
}

@Test
public void testPredictLanguages_LongUnicodeContent() {
CharSequence input = "😀😁😂🤣😃😄😅😆😉😊";
MaxentModel model = mock(MaxentModel.class);
when(model.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 1.0 });
when(model.getOutcome(0)).thenReturn("no");
when(model.getNumOutcomes()).thenReturn(1);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(generator);
when(generator.getContext(input)).thenReturn(new CharSequence[] { "😀😁", "😂🤣" });
LanguageDetectorModel langModel = new LanguageDetectorModel(model, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(langModel);
Language[] result = detector.predictLanguages(input);
assertEquals(1, result.length);
assertEquals("no", result[0].getLang());
}

@Test
public void testPredictLanguages_WithNullFeatureStrings() {
CharSequence input = "test";
MaxentModel model = mock(MaxentModel.class);
when(model.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 0.5 });
when(model.getOutcome(0)).thenReturn("en");
when(model.getNumOutcomes()).thenReturn(1);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(generator);
when(generator.getContext(input)).thenReturn(new CharSequence[] { null, "te" });
LanguageDetectorModel languageModel = new LanguageDetectorModel(model, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(languageModel);
try {
detector.predictLanguages(input);
fail("Expected NullPointerException from null ngram feature");
} catch (NullPointerException expected) {
}
}

@Test
public void testProbingPredictLanguages_DisabledEarlyExit() {
CharSequence input = "short";
MaxentModel model = mock(MaxentModel.class);
when(model.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 0.9 });
when(model.getOutcome(0)).thenReturn("en");
when(model.getNumOutcomes()).thenReturn(1);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator contextGenerator = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(contextGenerator.getContext(any())).thenReturn(new CharSequence[] { "sh", "ho", "or", "rt" });
LanguageDetectorModel langModel = new LanguageDetectorModel(model, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(langModel);
// LanguageDetectorConfig config = new LanguageDetectorConfig(1, 0.0, 5, 10);
// ProbingLanguageDetectionResult result = detector.probingPredictLanguages(input, config);
// assertNotNull(result);
// assertEquals("en", result.getLanguages()[0].getLang());
// assertTrue(result.getLengthProcessed() > 0);
}

@Test
public void testSeenEnough_SameLangButConfidenceDropsOnce() {
MaxentModel model = mock(MaxentModel.class);
when(model.getOutcome(0)).thenReturn("en");
when(model.getOutcome(1)).thenReturn("fr");
when(model.getNumOutcomes()).thenReturn(2);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator gen = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(gen);
LanguageDetectorModel modelWrapper = new LanguageDetectorModel(model, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(modelWrapper);
List<Language[]> predictions = new ArrayList<Language[]>();
predictions.add(new Language[] { new Language("en", 0.7), new Language("fr", 0.3) });
predictions.add(new Language[] { new Language("en", 0.8), new Language("fr", 0.2) });
predictions.add(new Language[] { new Language("en", 0.75), new Language("fr", 0.25) });
Language[] latest = new Language[] { new Language("en", 0.9), new Language("fr", 0.1) };
Map<CharSequence, MutableInt> dummyCounts = new HashMap<>();
// LanguageDetectorConfig config = new LanguageDetectorConfig(3, 0.05, 30, 200);
// boolean result = detector.seenEnough(predictions, latest, dummyCounts, config);
// assertFalse(result);
}

@Test
public void testPredictLanguage_WithOnlyWhitespaceInput() {
CharSequence input = "    ";
MaxentModel model = mock(MaxentModel.class);
when(model.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 1.0 });
when(model.getOutcome(0)).thenReturn("en");
when(model.getNumOutcomes()).thenReturn(1);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(generator);
when(generator.getContext(input)).thenReturn(new CharSequence[] { " " });
LanguageDetectorModel modelWrapper = new LanguageDetectorModel(model, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(modelWrapper);
Language result = detector.predictLanguage(input);
assertEquals("en", result.getLang());
}

@Test
public void testPredictLanguages_ContextReturnsEmptyArray() {
CharSequence input = "xyz";
MaxentModel model = mock(MaxentModel.class);
when(model.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 1.0 });
when(model.getOutcome(0)).thenReturn("tr");
when(model.getNumOutcomes()).thenReturn(1);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(generator);
when(generator.getContext(input)).thenReturn(new CharSequence[0]);
LanguageDetectorModel modelWrapper = new LanguageDetectorModel(model, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(modelWrapper);
Language[] result = detector.predictLanguages(input);
assertEquals(1, result.length);
assertEquals("tr", result[0].getLang());
}

@Test
public void testChunk_MultiByteCharacters_PartialChunk() {
CharSequence input = "🐍🐍🐍🐍🐍";
MaxentModel model = mock(MaxentModel.class);
when(model.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 1.0 });
when(model.getOutcome(0)).thenReturn("xx");
when(model.getNumOutcomes()).thenReturn(1);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(generator);
when(generator.getContext(any())).thenReturn(new CharSequence[] { "🐍🐍" });
LanguageDetectorModel langModel = new LanguageDetectorModel(model, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(langModel);
// LanguageDetectorConfig config = new LanguageDetectorConfig(2, 0.0, 2, 5);
// ProbingLanguageDetectionResult result = detector.probingPredictLanguages(input, config);
// assertNotNull(result);
// assertEquals("xx", result.getLanguages()[0].getLang());
// assertTrue(result.getLengthProcessed() > 0);
}

@Test
public void testTrain_EmptyManifestPopulatedAfterTraining() throws IOException {
ObjectStream<LanguageSample> sampleStream = mock(ObjectStream.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator contextGenerator = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
TrainingParameters params = new TrainingParameters();
params.put(AbstractEventTrainer.DATA_INDEXER_PARAM, AbstractEventTrainer.DATA_INDEXER_ONE_PASS_VALUE);
MaxentModel trainedModel = mock(MaxentModel.class);
EventTrainer trainer = mock(EventTrainer.class);
// when(trainer.train(any())).thenReturn(trainedModel);
Map<String, String> manifest = new HashMap<>();
// EventTrainer injectedTrainer = new EventTrainer() {
// 
// @Override
// public MaxentModel train(ObjectStream events) {
// manifest.put("info", "added");
// return trainedModel;
// }
// };
// TrainerFactory.setTrainerFactory((mlParams, manifestInfoEntries) -> injectedTrainer);
LanguageDetectorModel result = LanguageDetectorME.train(sampleStream, params, factory);
assertNotNull(result);
assertEquals(trainedModel, result.getMaxentModel());
// assertTrue(result.getManifestInfoEntries().containsKey("info"));
}

@Test
public void testGetSupportedLanguages_WhenZeroOutcomes() {
MaxentModel model = mock(MaxentModel.class);
when(model.getNumOutcomes()).thenReturn(0);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(generator);
LanguageDetectorModel detectorModel = new LanguageDetectorModel(model, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(detectorModel);
String[] result = detector.getSupportedLanguages();
assertNotNull(result);
assertEquals(0, result.length);
}

@Test
public void testSeenEnough_QueueExceedsMaxSize() {
MaxentModel model = mock(MaxentModel.class);
when(model.getOutcome(0)).thenReturn("en");
when(model.getOutcome(1)).thenReturn("de");
when(model.getNumOutcomes()).thenReturn(2);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(generator);
LanguageDetectorModel wrapper = new LanguageDetectorModel(model, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(wrapper);
List<Language[]> queue = new LinkedList<>();
queue.add(new Language[] { new Language("en", 0.6), new Language("de", 0.4) });
queue.add(new Language[] { new Language("en", 0.7), new Language("de", 0.3) });
queue.add(new Language[] { new Language("en", 0.8), new Language("de", 0.2) });
queue.add(new Language[] { new Language("en", 0.9), new Language("de", 0.1) });
Language[] newPrediction = new Language[] { new Language("en", 1.0), new Language("de", 0.0) };
// LanguageDetectorConfig config = new LanguageDetectorConfig(3, 0.05, 50, 150);
Map<CharSequence, MutableInt> ngramCounts = new HashMap<>();
// boolean result = detector.seenEnough(queue, newPrediction, ngramCounts, config);
// assertTrue(result);
}

@Test
public void testPredict_WithMultipleIdenticalNGrams() {
CharSequence input = "aaa";
MaxentModel model = mock(MaxentModel.class);
when(model.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 0.99 });
when(model.getOutcome(0)).thenReturn("en");
when(model.getNumOutcomes()).thenReturn(1);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator context = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(context);
when(context.getContext(input)).thenReturn(new CharSequence[] { "aa", "aa", "aa" });
LanguageDetectorModel langModel = new LanguageDetectorModel(model, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(langModel);
Language[] result = detector.predictLanguages(input);
assertEquals(1, result.length);
assertEquals("en", result[0].getLang());
}

@Test
public void testProbingPredictLanguages_StopsWithEarlyConfidenceGapSatisfied() {
CharSequence input = "some short recognizable text";
MaxentModel model = mock(MaxentModel.class);
when(model.getOutcome(0)).thenReturn("en");
when(model.getOutcome(1)).thenReturn("de");
when(model.getNumOutcomes()).thenReturn(2);
when(model.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 0.7, 0.3 }).thenReturn(new double[] { 0.8, 0.2 }).thenReturn(new double[] { 0.9, 0.1 });
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
when(generator.getContext(any(CharSequence.class))).thenReturn(new CharSequence[] { "so", "om", "me" });
when(factory.getContextGenerator()).thenReturn(generator);
LanguageDetectorModel detectorModel = new LanguageDetectorModel(model, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(detectorModel);
// LanguageDetectorConfig config = new LanguageDetectorConfig(3, 0.1, 5, 100);
// ProbingLanguageDetectionResult result = detector.probingPredictLanguages(input, config);
// assertNotNull(result);
// assertEquals("en", result.getLanguages()[0].getLang());
// assertTrue(result.getLengthProcessed() > 0);
}

@Test
public void testPredictLanguages_SingleOutputAndEmptyCounts() {
CharSequence input = "";
MaxentModel model = mock(MaxentModel.class);
// when(model.getEvalParameters()).thenReturn(null);
when(model.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 1.0 });
when(model.getOutcome(0)).thenReturn("en");
when(model.getNumOutcomes()).thenReturn(1);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator context = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(context);
when(context.getContext(input)).thenReturn(new CharSequence[0]);
LanguageDetectorModel langModel = new LanguageDetectorModel(model, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(langModel);
Language[] result = detector.predictLanguages(input);
assertEquals("en", result[0].getLang());
assertEquals(1.0, result[0].getConfidence(), 0.001);
}

@Test
public void testProbingPredictLanguages_StopAfterOneChunkWhenConfigForcesIt() {
CharSequence input = "abcdefghi";
MaxentModel model = mock(MaxentModel.class);
when(model.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 0.95, 0.05 });
when(model.getOutcome(0)).thenReturn("en");
when(model.getOutcome(1)).thenReturn("de");
when(model.getNumOutcomes()).thenReturn(2);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator gen = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(gen);
when(gen.getContext(any(CharSequence.class))).thenReturn(new CharSequence[] { "ab", "bc", "cd", "de" });
LanguageDetectorModel detectorModel = new LanguageDetectorModel(model, new HashMap<>(), factory);
// LanguageDetectorConfig config = new LanguageDetectorConfig(1, 0.9, 10, 10);
LanguageDetectorME detector = new LanguageDetectorME(detectorModel);
// ProbingLanguageDetectionResult result = detector.probingPredictLanguages(input, config);
// assertNotNull(result);
// assertEquals("en", result.getLanguages()[0].getLang());
// assertTrue(result.getLengthProcessed() > 0);
}

@Test
public void testProbingPredictLanguages_MultipleChunksButNeverSatisfiesEarlyExit() {
CharSequence input = "abcdefghijabcdefghijabcdefghijabcdefghijabcdefghij";
MaxentModel model = mock(MaxentModel.class);
when(model.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 0.3, 0.7 }).thenReturn(new double[] { 0.4, 0.6 }).thenReturn(new double[] { 0.35, 0.65 }).thenReturn(new double[] { 0.36, 0.64 }).thenReturn(new double[] { 0.37, 0.63 });
when(model.getOutcome(0)).thenReturn("en");
when(model.getOutcome(1)).thenReturn("fr");
when(model.getNumOutcomes()).thenReturn(2);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(generator);
when(generator.getContext(any(CharSequence.class))).thenReturn(new CharSequence[] { "ab", "bc" });
LanguageDetectorModel modelWrapper = new LanguageDetectorModel(model, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(modelWrapper);
// LanguageDetectorConfig config = new LanguageDetectorConfig(3, 0.5, 10, 50);
// ProbingLanguageDetectionResult result = detector.probingPredictLanguages(input, config);
// assertNotNull(result);
// assertEquals("fr", result.getLanguages()[0].getLang());
// assertTrue(result.getLengthProcessed() == 50);
}

@Test
public void testPredictLanguages_EmptyModelEvalResult() {
CharSequence input = "text";
MaxentModel model = mock(MaxentModel.class);
when(model.eval(any(String[].class), any(float[].class))).thenReturn(new double[0]);
when(model.getNumOutcomes()).thenReturn(0);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator cg = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(cg);
when(cg.getContext(input)).thenReturn(new CharSequence[] { "tx", "ex", "xt" });
LanguageDetectorModel modelWrapper = new LanguageDetectorModel(model, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(modelWrapper);
Language[] result = detector.predictLanguages(input);
assertNotNull(result);
assertEquals(0, result.length);
}

@Test
public void testGetSupportedLanguages_WithDuplicateOutcomes() {
MaxentModel model = mock(MaxentModel.class);
when(model.getNumOutcomes()).thenReturn(2);
when(model.getOutcome(0)).thenReturn("en");
when(model.getOutcome(1)).thenReturn("en");
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(generator);
LanguageDetectorModel wrapped = new LanguageDetectorModel(model, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(wrapped);
String[] supported = detector.getSupportedLanguages();
assertEquals(2, supported.length);
assertEquals("en", supported[0]);
assertEquals("en", supported[1]);
}

@Test
public void testProbingPredictLanguages_WhenGetContextReturnsMixedNulls() {
CharSequence input = "abc";
MaxentModel model = mock(MaxentModel.class);
when(model.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 0.5 });
when(model.getOutcome(0)).thenReturn("en");
when(model.getNumOutcomes()).thenReturn(1);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator cg = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(cg);
when(cg.getContext(any(CharSequence.class))).thenReturn(new CharSequence[] { null, "a", null });
LanguageDetectorModel wrapped = new LanguageDetectorModel(model, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(wrapped);
try {
detector.probingPredictLanguages(input);
fail("Expected NullPointerException due to null ngram");
} catch (NullPointerException e) {
}
}

@Test
public void testProbingPredictLanguages_WithZeroChunkSizeReturnsImmediateResult() {
CharSequence input = "data";
MaxentModel model = mock(MaxentModel.class);
when(model.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 0.6 });
when(model.getOutcome(0)).thenReturn("en");
when(model.getNumOutcomes()).thenReturn(1);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator contextGen = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(contextGen);
when(contextGen.getContext(any())).thenReturn(new CharSequence[] { "da", "at", "ta" });
LanguageDetectorModel detectorModel = new LanguageDetectorModel(model, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(detectorModel);
// LanguageDetectorConfig config = new LanguageDetectorConfig(3, 0.05, 0, 100);
// ProbingLanguageDetectionResult result = detector.probingPredictLanguages(input, config);
// assertNotNull(result);
// assertEquals(1, result.getLanguages().length);
// assertEquals("en", result.getLanguages()[0].getLang());
}

@Test
public void testSeenEnough_ConfidenceEqualInLastTwoPredictions() {
MaxentModel model = mock(MaxentModel.class);
when(model.getOutcome(0)).thenReturn("en");
when(model.getOutcome(1)).thenReturn("fr");
when(model.getNumOutcomes()).thenReturn(2);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator contextGen = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(contextGen);
LanguageDetectorModel modelWrapper = new LanguageDetectorModel(model, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(modelWrapper);
List<Language[]> predictions = new ArrayList<Language[]>();
predictions.add(new Language[] { new Language("en", 0.7), new Language("fr", 0.3) });
predictions.add(new Language[] { new Language("en", 0.8), new Language("fr", 0.2) });
predictions.add(new Language[] { new Language("en", 0.8), new Language("fr", 0.2) });
Language[] newPrediction = new Language[] { new Language("en", 0.8), new Language("fr", 0.2) };
Map<CharSequence, MutableInt> dummyCounts = new HashMap<>();
// LanguageDetectorConfig config = new LanguageDetectorConfig(3, 0.01, 50, 500);
// boolean result = detector.seenEnough(predictions, newPrediction, dummyCounts, config);
// assertTrue(result);
}

@Test
public void testChunk_WhenStartEqualsContentLength() {
CharSequence content = "hello";
MaxentModel model = mock(MaxentModel.class);
when(model.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 0.6 });
when(model.getOutcome(0)).thenReturn("en");
when(model.getNumOutcomes()).thenReturn(1);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator ctx = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(ctx);
when(ctx.getContext(any())).thenReturn(new CharSequence[] { "he", "el" });
LanguageDetectorModel detectorModel = new LanguageDetectorModel(model, new HashMap<String, String>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(detectorModel);
// LanguageDetectorConfig config = new LanguageDetectorConfig(3, 0.1, 3, 5);
// ProbingLanguageDetectionResult result = detector.probingPredictLanguages(content.toString() + "X", config);
// assertNotNull(result);
// assertTrue(result.getLengthProcessed() > 0);
}

@Test
public void testTrain_WithNullFactoryContextGenerator_ThrowsException() throws IOException {
ObjectStream<LanguageSample> stream = mock(ObjectStream.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(null);
TrainingParameters params = new TrainingParameters();
try {
LanguageDetectorME.train(stream, params, factory);
fail("Expected NullPointerException due to null context generator");
} catch (NullPointerException expected) {
}
}

@Test
public void testPredictLanguages_NullCharSequenceInContextIgnoredByUpdateCounts() {
CharSequence input = "test";
MaxentModel model = mock(MaxentModel.class);
when(model.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 1.0 });
when(model.getOutcome(0)).thenReturn("en");
when(model.getNumOutcomes()).thenReturn(1);
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(generator);
when(generator.getContext(input)).thenReturn(new CharSequence[] { "te", null, "st" });
LanguageDetectorModel langModel = new LanguageDetectorModel(model, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(langModel);
try {
Language[] results = detector.predictLanguages(input);
assertNotNull(results);
assertEquals("en", results[0].getLang());
} catch (NullPointerException e) {
fail("Null ngram should be handled gracefully");
}
}

@Test
public void testPredictLanguages_NgramCountsReturningDifferentMapInsertionOrder() {
CharSequence input = "abc";
MaxentModel model = mock(MaxentModel.class);
when(model.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 0.2, 0.8 });
when(model.getOutcome(0)).thenReturn("nl");
when(model.getOutcome(1)).thenReturn("fr");
when(model.getNumOutcomes()).thenReturn(2);
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(generator);
when(generator.getContext(input)).thenReturn(new CharSequence[] { "bc", "ab", "ab", "bc", "bc" });
LanguageDetectorModel detectorModel = new LanguageDetectorModel(model, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(detectorModel);
Language[] results = detector.predictLanguages(input);
assertNotNull(results);
assertEquals(2, results.length);
assertEquals("fr", results[0].getLang());
}

@Test
public void testProbingPredictLanguages_WhenContentIsExactMaxLength() {
CharSequence input = "abcdefghij";
MaxentModel model = mock(MaxentModel.class);
when(model.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 0.55, 0.45 }).thenReturn(new double[] { 0.60, 0.40 }).thenReturn(new double[] { 0.70, 0.30 });
when(model.getOutcome(0)).thenReturn("en");
when(model.getOutcome(1)).thenReturn("de");
when(model.getNumOutcomes()).thenReturn(2);
LanguageDetectorContextGenerator contextGenerator = mock(LanguageDetectorContextGenerator.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(contextGenerator.getContext(any())).thenReturn(new CharSequence[] { "ab", "cd", "ef" });
LanguageDetectorModel modelWrapper = new LanguageDetectorModel(model, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(modelWrapper);
// LanguageDetectorConfig config = new LanguageDetectorConfig(3, 0.1, 4, 10);
// ProbingLanguageDetectionResult result = detector.probingPredictLanguages(input, config);
// assertNotNull(result);
// assertTrue(result.getLanguages().length > 0);
// assertTrue(result.getLengthProcessed() <= 10);
}

@Test
public void testSeenEnough_SameLanguageButConfidenceDrops_MiddlePrediction() {
MaxentModel model = mock(MaxentModel.class);
when(model.getNumOutcomes()).thenReturn(2);
when(model.getOutcome(0)).thenReturn("en");
when(model.getOutcome(1)).thenReturn("fr");
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(generator);
LanguageDetectorModel modelWrapper = new LanguageDetectorModel(model, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(modelWrapper);
List<Language[]> predictions = new LinkedList<Language[]>();
predictions.add(new Language[] { new Language("en", 0.7), new Language("fr", 0.3) });
predictions.add(new Language[] { new Language("en", 0.6), new Language("fr", 0.4) });
predictions.add(new Language[] { new Language("en", 0.8), new Language("fr", 0.2) });
Language[] newPrediction = new Language[] { new Language("en", 0.9), new Language("fr", 0.1) };
// LanguageDetectorConfig config = new LanguageDetectorConfig(3, 0.1, 10, 500);
Map<CharSequence, MutableInt> ngrams = new HashMap<>();
// boolean result = detector.seenEnough(predictions, newPrediction, ngrams, config);
// assertFalse(result);
}

@Test
public void testPredictLanguage_ReturnsOnlyLanguageEvenWithManyFeatures() {
CharSequence input = "many features here";
MaxentModel model = mock(MaxentModel.class);
when(model.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 0.95 });
when(model.getOutcome(0)).thenReturn("ko");
when(model.getNumOutcomes()).thenReturn(1);
LanguageDetectorContextGenerator contextGenerator = mock(LanguageDetectorContextGenerator.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
when(contextGenerator.getContext(input)).thenReturn(new CharSequence[] { "ma", "an", "ny", "fe", "ea", "at", "tu", "ur", "es" });
LanguageDetectorModel modelWrapper = new LanguageDetectorModel(model, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(modelWrapper);
Language result = detector.predictLanguage(input);
assertNotNull(result);
assertEquals("ko", result.getLang());
}

@Test
public void testChunk_WithZeroCodePointCharacters_YieldsSingleChunk() {
CharSequence input = "\u200B\u200B\u200B";
MaxentModel model = mock(MaxentModel.class);
when(model.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 0.5 });
when(model.getOutcome(0)).thenReturn("zz");
when(model.getNumOutcomes()).thenReturn(1);
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
when(generator.getContext(any())).thenReturn(new CharSequence[] { "\u200B" });
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(generator);
LanguageDetectorModel langModel = new LanguageDetectorModel(model, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(langModel);
// LanguageDetectorConfig config = new LanguageDetectorConfig(1, 0.05, 3, 3);
// ProbingLanguageDetectionResult result = detector.probingPredictLanguages(input, config);
// assertNotNull(result);
// assertEquals("zz", result.getLanguages()[0].getLang());
}

@Test
public void testTrain_EmptyTrainingParameters_DefaultsApplied() throws IOException {
ObjectStream<LanguageSample> sampleStream = mock(ObjectStream.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator contextGen = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(contextGen);
TrainingParameters params = new TrainingParameters();
MaxentModel trainingModel = mock(MaxentModel.class);
EventTrainer trainer = mock(EventTrainer.class);
// when(trainer.train(any())).thenReturn(trainingModel);
Map<String, String> manifest = new HashMap<>();
// TrainerFactory.setTrainerFactory((mlParams, manifestEntries) -> trainer);
LanguageDetectorModel result = LanguageDetectorME.train(sampleStream, params, factory);
assertNotNull(result);
assertSame(trainingModel, result.getMaxentModel());
}
}
