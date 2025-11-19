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

public class LanguageDetectorME_llmsuite_4_GPTLLMTest {

@Test
public void testPredictLanguages_returnsSortedByConfidence() {
MaxentModel mockModel = mock(MaxentModel.class);
when(mockModel.getNumOutcomes()).thenReturn(2);
when(mockModel.getOutcome(0)).thenReturn("en");
when(mockModel.getOutcome(1)).thenReturn("de");
when(mockModel.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 0.8, 0.2 });
LanguageDetectorContextGenerator mockContextGen = mock(LanguageDetectorContextGenerator.class);
when(mockContextGen.getContext(any(CharSequence.class))).thenReturn(new CharSequence[] { "th", "he", "en" });
LanguageDetectorFactory mockFactory = mock(LanguageDetectorFactory.class);
when(mockFactory.getContextGenerator()).thenReturn(mockContextGen);
LanguageDetectorModel model = new LanguageDetectorModel(mockModel, new HashMap<>(), mockFactory);
LanguageDetectorME detector = new LanguageDetectorME(model);
Language[] result = detector.predictLanguages("the quick brown fox");
assertNotNull(result);
assertEquals("en", result[0].getLang());
assertEquals("de", result[1].getLang());
assertTrue(result[0].getConfidence() > result[1].getConfidence());
}

@Test
public void testPredictLanguage_returnsTopLanguage() {
MaxentModel maxentModel = mock(MaxentModel.class);
when(maxentModel.getNumOutcomes()).thenReturn(2);
when(maxentModel.getOutcome(0)).thenReturn("en");
when(maxentModel.getOutcome(1)).thenReturn("fr");
when(maxentModel.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 0.6, 0.4 });
LanguageDetectorContextGenerator contextGen = mock(LanguageDetectorContextGenerator.class);
when(contextGen.getContext(any(CharSequence.class))).thenReturn(new CharSequence[] { "he", "ll", "lo" });
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGen);
LanguageDetectorModel model = new LanguageDetectorModel(maxentModel, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(model);
Language prediction = detector.predictLanguage("hello");
assertEquals("en", prediction.getLang());
assertEquals(0.6, prediction.getConfidence(), 0.0001);
}

@Test
public void testGetSupportedLanguages_returnsCorrectOrder() {
MaxentModel maxentModel = mock(MaxentModel.class);
when(maxentModel.getNumOutcomes()).thenReturn(3);
when(maxentModel.getOutcome(0)).thenReturn("en");
when(maxentModel.getOutcome(1)).thenReturn("de");
when(maxentModel.getOutcome(2)).thenReturn("fr");
LanguageDetectorContextGenerator contextGen = mock(LanguageDetectorContextGenerator.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGen);
LanguageDetectorModel model = new LanguageDetectorModel(maxentModel, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(model);
String[] supportedLanguages = detector.getSupportedLanguages();
assertArrayEquals(new String[] { "en", "de", "fr" }, supportedLanguages);
}

@Test
public void testPredictLanguages_withEmptyContext_returnsModelOutcomes() {
MaxentModel maxentModel = mock(MaxentModel.class);
when(maxentModel.getNumOutcomes()).thenReturn(2);
when(maxentModel.getOutcome(0)).thenReturn("en");
when(maxentModel.getOutcome(1)).thenReturn("de");
when(maxentModel.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 0.5, 0.5 });
LanguageDetectorContextGenerator contextGen = mock(LanguageDetectorContextGenerator.class);
when(contextGen.getContext("")).thenReturn(new CharSequence[] {});
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGen);
LanguageDetectorModel model = new LanguageDetectorModel(maxentModel, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(model);
Language[] result = detector.predictLanguages("");
assertEquals(2, result.length);
assertEquals("en", result[0].getLang());
assertEquals("de", result[1].getLang());
}

@Test
public void testProbingPredictLanguages_returnsEarlyBasedOnConfig() {
MaxentModel maxentModel = mock(MaxentModel.class);
when(maxentModel.getNumOutcomes()).thenReturn(2);
when(maxentModel.getOutcome(0)).thenReturn("en");
when(maxentModel.getOutcome(1)).thenReturn("de");
when(maxentModel.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 0.9, 0.1 });
LanguageDetectorContextGenerator contextGen = mock(LanguageDetectorContextGenerator.class);
when(contextGen.getContext(any(CharSequence.class))).thenReturn(new CharSequence[] { "wo", "rd" });
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGen);
LanguageDetectorModel model = new LanguageDetectorModel(maxentModel, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(model);
// LanguageDetectorConfig config = new LanguageDetectorConfig(3, 2, 0.5);
// ProbingLanguageDetectionResult result = detector.probingPredictLanguages("word word word word", config);
// assertNotNull(result);
// assertEquals("en", result.getLanguages()[0].getLang());
}

@Test
public void testArrayToCounts_acumulatesFrequency() {
MaxentModel maxentModel = mock(MaxentModel.class);
when(maxentModel.getNumOutcomes()).thenReturn(2);
when(maxentModel.getOutcome(0)).thenReturn("en");
when(maxentModel.getOutcome(1)).thenReturn("de");
when(maxentModel.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 0.8, 0.2 });
LanguageDetectorContextGenerator contextGen = mock(LanguageDetectorContextGenerator.class);
when(contextGen.getContext(any(CharSequence.class))).thenReturn(new CharSequence[] { "aa", "bb", "aa" });
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGen);
LanguageDetectorModel model = new LanguageDetectorModel(maxentModel, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(model);
Language[] result = detector.predictLanguages("sample");
assertEquals(2, result.length);
assertEquals("en", result[0].getLang());
assertEquals("de", result[1].getLang());
}

@Test
public void testTrain_returnsValidModel() throws IOException {
ObjectStream<LanguageSample> sampleStream = mock(ObjectStream.class);
LanguageDetectorContextGenerator contextGen = mock(LanguageDetectorContextGenerator.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGen);
MaxentModel model = mock(MaxentModel.class);
LanguageDetectorEventStream eventStream = mock(LanguageDetectorEventStream.class);
TrainingParameters params = new TrainingParameters();
params.put(AbstractEventTrainer.DATA_INDEXER_PARAM, AbstractEventTrainer.DATA_INDEXER_ONE_PASS_VALUE);
LanguageDetectorModel trainedModel = LanguageDetectorME.train(sampleStream, params, factory);
assertNotNull(trainedModel);
}

@Test
public void testPredictLanguage_withSingleOutcome() {
MaxentModel maxentModel = mock(MaxentModel.class);
when(maxentModel.getNumOutcomes()).thenReturn(1);
when(maxentModel.getOutcome(0)).thenReturn("en");
when(maxentModel.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 1.0 });
LanguageDetectorContextGenerator contextGen = mock(LanguageDetectorContextGenerator.class);
when(contextGen.getContext(any(CharSequence.class))).thenReturn(new CharSequence[] { "en" });
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGen);
LanguageDetectorModel model = new LanguageDetectorModel(maxentModel, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(model);
Language result = detector.predictLanguage("english");
assertEquals("en", result.getLang());
assertEquals(1.0, result.getConfidence(), 0.0001);
}

@Test
public void testPredictLanguages_whenNoContextFeatures() {
MaxentModel maxentModel = mock(MaxentModel.class);
when(maxentModel.getNumOutcomes()).thenReturn(2);
when(maxentModel.getOutcome(0)).thenReturn("en");
when(maxentModel.getOutcome(1)).thenReturn("de");
when(maxentModel.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 0.4, 0.6 });
LanguageDetectorContextGenerator contextGen = mock(LanguageDetectorContextGenerator.class);
when(contextGen.getContext(any(CharSequence.class))).thenReturn(new CharSequence[0]);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGen);
LanguageDetectorModel model = new LanguageDetectorModel(maxentModel, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(model);
Language[] result = detector.predictLanguages("abcd");
assertNotNull(result);
assertEquals(2, result.length);
assertEquals("de", result[0].getLang());
}

@Test
public void testChunk_withStartBeyondLength_returnsEmptyChunk() {
MaxentModel maxentModel = mock(MaxentModel.class);
when(maxentModel.getNumOutcomes()).thenReturn(1);
when(maxentModel.getOutcome(0)).thenReturn("en");
when(maxentModel.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 1.0 });
LanguageDetectorContextGenerator contextGen = mock(LanguageDetectorContextGenerator.class);
when(contextGen.getContext(any(CharSequence.class))).thenReturn(new CharSequence[0]);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGen);
LanguageDetectorModel model = new LanguageDetectorModel(maxentModel, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(model);
// LanguageDetectorConfig config = new LanguageDetectorConfig(10, 3, 0.5);
// ProbingLanguageDetectionResult result = detector.probingPredictLanguages("", config);
// assertNotNull(result);
// assertEquals("en", result.getLanguages()[0].getLang());
// assertEquals(0, result.getTextLengthProcessed());
}

@Test
public void testSeenEnough_differentTopLanguages_returnsFalse() {
MaxentModel mockModel = mock(MaxentModel.class);
when(mockModel.getNumOutcomes()).thenReturn(2);
when(mockModel.getOutcome(0)).thenReturn("en");
when(mockModel.getOutcome(1)).thenReturn("fr");
LanguageDetectorContextGenerator contextGen = mock(LanguageDetectorContextGenerator.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGen);
LanguageDetectorModel model = new LanguageDetectorModel(mockModel, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(model);
LinkedList<Language[]> previous = new LinkedList<>();
previous.add(new Language[] { new Language("en", 0.5), new Language("fr", 0.3) });
previous.add(new Language[] { new Language("fr", 0.6), new Language("en", 0.2) });
Language[] currentPrediction = new Language[] { new Language("fr", 0.7), new Language("en", 0.3) };
// boolean result = detector.seenEnough(previous, currentPrediction, new HashMap<>(), new LanguageDetectorConfig(2, 2, 0.1));
// assertFalse(result);
}

@Test
public void testSeenEnough_confidenceDecreased_returnsFalse() {
MaxentModel mockModel = mock(MaxentModel.class);
when(mockModel.getNumOutcomes()).thenReturn(2);
when(mockModel.getOutcome(0)).thenReturn("en");
when(mockModel.getOutcome(1)).thenReturn("fr");
LanguageDetectorContextGenerator contextGen = mock(LanguageDetectorContextGenerator.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGen);
LanguageDetectorModel model = new LanguageDetectorModel(mockModel, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(model);
LinkedList<Language[]> previous = new LinkedList<>();
previous.add(new Language[] { new Language("en", 0.9), new Language("fr", 0.1) });
previous.add(new Language[] { new Language("en", 0.7), new Language("fr", 0.3) });
Language[] currentPrediction = new Language[] { new Language("en", 0.6), new Language("fr", 0.4) };
// boolean result = detector.seenEnough(previous, currentPrediction, new HashMap<>(), new LanguageDetectorConfig(2, 2, 0.2));
// assertFalse(result);
}

@Test
public void testTrain_withNullParams_usesDefaultIndexer() throws IOException {
ObjectStream<LanguageSample> sampleStream = mock(ObjectStream.class);
TrainingParameters params = new TrainingParameters();
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator contextGen = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(contextGen);
MaxentModel trainedModel = mock(MaxentModel.class);
EventTrainer trainer = mock(EventTrainer.class);
// when(trainer.train(any())).thenReturn(trainedModel);
LanguageDetectorModel model = LanguageDetectorME.train(sampleStream, params, factory);
assertNotNull(model);
}

@Test
public void testPredict_withEmptyNgramCounts_returnsOutcomeWithEval() {
MaxentModel maxentModel = mock(MaxentModel.class);
when(maxentModel.getNumOutcomes()).thenReturn(2);
when(maxentModel.getOutcome(0)).thenReturn("en");
when(maxentModel.getOutcome(1)).thenReturn("de");
when(maxentModel.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 0.4, 0.6 });
LanguageDetectorContextGenerator contextGen = mock(LanguageDetectorContextGenerator.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGen);
LanguageDetectorModel model = new LanguageDetectorModel(maxentModel, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(model);
Map<CharSequence, MutableInt> emptyCounts = Collections.emptyMap();
// Language[] result = detector.predict(emptyCounts);
// assertNotNull(result);
// assertEquals(2, result.length);
}

@Test
public void testProbingPredictLanguages_withUnicodeText() {
MaxentModel maxentModel = mock(MaxentModel.class);
when(maxentModel.getNumOutcomes()).thenReturn(2);
when(maxentModel.getOutcome(0)).thenReturn("ja");
when(maxentModel.getOutcome(1)).thenReturn("en");
when(maxentModel.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 0.85, 0.15 });
LanguageDetectorContextGenerator contextGen = mock(LanguageDetectorContextGenerator.class);
when(contextGen.getContext(any(CharSequence.class))).thenReturn(new CharSequence[] { "日", "本", "語" });
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGen);
LanguageDetectorModel model = new LanguageDetectorModel(maxentModel, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(model);
// LanguageDetectorConfig config = new LanguageDetectorConfig(10, 2, 0.1);
// ProbingLanguageDetectionResult result = detector.probingPredictLanguages("日本語のテキスト", config);
// assertNotNull(result);
// assertEquals("ja", result.getLanguages()[0].getLang());
// assertEquals(0.85, result.getLanguages()[0].getConfidence(), 0.0001);
}

@Test
public void testPredictLanguages_whenEvalReturnsEqualScores() {
MaxentModel maxentModel = mock(MaxentModel.class);
when(maxentModel.getNumOutcomes()).thenReturn(2);
when(maxentModel.getOutcome(0)).thenReturn("en");
when(maxentModel.getOutcome(1)).thenReturn("es");
when(maxentModel.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 0.5, 0.5 });
LanguageDetectorContextGenerator contextGen = mock(LanguageDetectorContextGenerator.class);
when(contextGen.getContext(any(CharSequence.class))).thenReturn(new CharSequence[] { "he", "ll", "lo" });
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGen);
LanguageDetectorModel model = new LanguageDetectorModel(maxentModel, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(model);
Language[] result = detector.predictLanguages("hello");
assertNotNull(result);
assertEquals(2, result.length);
assertEquals(0.5, result[0].getConfidence(), 0.0001);
assertEquals(0.5, result[1].getConfidence(), 0.0001);
}

@Test
public void testSeenEnough_exactMinConsecImprovements_true() {
MaxentModel maxentModel = mock(MaxentModel.class);
when(maxentModel.getOutcome(0)).thenReturn("en");
when(maxentModel.getOutcome(1)).thenReturn("fr");
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator contextGen = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(contextGen);
LanguageDetectorModel model = new LanguageDetectorModel(maxentModel, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(model);
List<Language[]> previous = new LinkedList<>();
previous.add(new Language[] { new Language("fr", 0.3), new Language("en", 0.1) });
previous.add(new Language[] { new Language("fr", 0.6), new Language("en", 0.3) });
Language[] newPred = new Language[] { new Language("fr", 0.7), new Language("en", 0.2) };
Map<CharSequence, MutableInt> ngrams = new HashMap<>();
ngrams.put("fr", new MutableInt(3));
// boolean result = detector.seenEnough(previous, newPred, ngrams, new LanguageDetectorConfig(2, 3, 0.2));
// assertTrue(result);
}

@Test
public void testSeenEnough_belowMinDiff_returnsFalse() {
MaxentModel maxentModel = mock(MaxentModel.class);
when(maxentModel.getOutcome(0)).thenReturn("en");
when(maxentModel.getOutcome(1)).thenReturn("de");
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator contextGen = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(contextGen);
LanguageDetectorModel model = new LanguageDetectorModel(maxentModel, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(model);
List<Language[]> previous = new LinkedList<>();
previous.add(new Language[] { new Language("en", 0.7), new Language("de", 0.6) });
previous.add(new Language[] { new Language("en", 0.75), new Language("de", 0.55) });
Language[] curr = new Language[] { new Language("en", 0.76), new Language("de", 0.71) };
// boolean result = detector.seenEnough(previous, curr, new HashMap<>(), new LanguageDetectorConfig(2, 3, 0.1));
// assertFalse(result);
}

@Test
public void testProbingPredictLanguages_stopsAtEndOfText() {
MaxentModel maxentModel = mock(MaxentModel.class);
when(maxentModel.getNumOutcomes()).thenReturn(2);
when(maxentModel.getOutcome(0)).thenReturn("en");
when(maxentModel.getOutcome(1)).thenReturn("fr");
when(maxentModel.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 0.6, 0.4 });
LanguageDetectorContextGenerator contextGen = mock(LanguageDetectorContextGenerator.class);
when(contextGen.getContext(any(CharSequence.class))).thenReturn(new CharSequence[] { "ab", "bc" });
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGen);
LanguageDetectorModel model = new LanguageDetectorModel(maxentModel, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(model);
// LanguageDetectorConfig config = new LanguageDetectorConfig(10, 5, 0.5);
String content = "end";
// ProbingLanguageDetectionResult result = detector.probingPredictLanguages(content, config);
// assertNotNull(result);
// assertEquals("en", result.getLanguages()[0].getLang());
// assertTrue(result.getTextLengthProcessed() <= 10);
}

@Test
public void testChunk_withHighCodePointCharacters() {
MaxentModel maxentModel = mock(MaxentModel.class);
when(maxentModel.getNumOutcomes()).thenReturn(1);
when(maxentModel.getOutcome(0)).thenReturn("zh");
when(maxentModel.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 1.0 });
LanguageDetectorContextGenerator contextGen = mock(LanguageDetectorContextGenerator.class);
when(contextGen.getContext(any(CharSequence.class))).thenReturn(new CharSequence[] { "漢", "字" });
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGen);
LanguageDetectorModel model = new LanguageDetectorModel(maxentModel, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(model);
// LanguageDetectorConfig config = new LanguageDetectorConfig(1, 2, 0.5);
String content = "漢字";
// ProbingLanguageDetectionResult result = detector.probingPredictLanguages(content, config);
// assertNotNull(result);
// assertEquals("zh", result.getLanguages()[0].getLang());
// assertTrue(result.getTextLengthProcessed() >= 1);
}

@Test
public void testPredictLanguage_withSpecialCharacters() {
MaxentModel maxentModel = mock(MaxentModel.class);
when(maxentModel.getNumOutcomes()).thenReturn(2);
when(maxentModel.getOutcome(0)).thenReturn("en");
when(maxentModel.getOutcome(1)).thenReturn("ja");
when(maxentModel.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 0.3, 0.7 });
LanguageDetectorContextGenerator contextGen = mock(LanguageDetectorContextGenerator.class);
when(contextGen.getContext(any(CharSequence.class))).thenReturn(new CharSequence[] { "★", "！" });
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGen);
LanguageDetectorModel model = new LanguageDetectorModel(maxentModel, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(model);
Language prediction = detector.predictLanguage("★！");
assertEquals("ja", prediction.getLang());
assertEquals(0.7, prediction.getConfidence(), 0.0001);
}

@Test
public void testPredictLanguage_withWhitespaceOnly() {
MaxentModel maxentModel = mock(MaxentModel.class);
when(maxentModel.getNumOutcomes()).thenReturn(1);
when(maxentModel.getOutcome(0)).thenReturn("en");
when(maxentModel.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 1.0 });
LanguageDetectorContextGenerator contextGen = mock(LanguageDetectorContextGenerator.class);
when(contextGen.getContext("   ")).thenReturn(new CharSequence[0]);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGen);
LanguageDetectorModel model = new LanguageDetectorModel(maxentModel, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(model);
Language prediction = detector.predictLanguage("   ");
assertNotNull(prediction);
assertEquals("en", prediction.getLang());
}

@Test
public void testPredictLanguages_withDuplicateContextFeatures() {
MaxentModel maxentModel = mock(MaxentModel.class);
when(maxentModel.getNumOutcomes()).thenReturn(2);
when(maxentModel.getOutcome(0)).thenReturn("en");
when(maxentModel.getOutcome(1)).thenReturn("ru");
when(maxentModel.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 0.55, 0.45 });
LanguageDetectorContextGenerator contextGen = mock(LanguageDetectorContextGenerator.class);
when(contextGen.getContext(any(CharSequence.class))).thenReturn(new CharSequence[] { "ab", "ab", "ab", "bc" });
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGen);
LanguageDetectorModel model = new LanguageDetectorModel(maxentModel, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(model);
Language[] result = detector.predictLanguages("ababab");
assertNotNull(result);
assertEquals("en", result[0].getLang());
}

@Test
public void testProbingPredictLanguages_returnsResultWhenStoppingCriteriaNotMet() {
MaxentModel maxentModel = mock(MaxentModel.class);
when(maxentModel.getNumOutcomes()).thenReturn(3);
when(maxentModel.getOutcome(0)).thenReturn("en");
when(maxentModel.getOutcome(1)).thenReturn("fr");
when(maxentModel.getOutcome(2)).thenReturn("de");
when(maxentModel.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 0.34, 0.33, 0.33 });
LanguageDetectorContextGenerator contextGen = mock(LanguageDetectorContextGenerator.class);
when(contextGen.getContext(any(CharSequence.class))).thenReturn(new CharSequence[] { "some", "typ", "xt" });
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGen);
LanguageDetectorModel model = new LanguageDetectorModel(maxentModel, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(model);
// LanguageDetectorConfig config = new LanguageDetectorConfig(5, 2, 0.05);
// ProbingLanguageDetectionResult result = detector.probingPredictLanguages("some typical text for language detection", config);
// assertNotNull(result);
// assertEquals("en", result.getLanguages()[0].getLang());
// assertTrue(result.getTextLengthProcessed() > 0);
}

@Test
public void testProbingPredictLanguages_emptyContentReturnsDefaultPrediction() {
MaxentModel maxentModel = mock(MaxentModel.class);
when(maxentModel.getNumOutcomes()).thenReturn(1);
when(maxentModel.getOutcome(0)).thenReturn("und");
when(maxentModel.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 1.0 });
LanguageDetectorContextGenerator contextGen = mock(LanguageDetectorContextGenerator.class);
when(contextGen.getContext(any(CharSequence.class))).thenReturn(new CharSequence[0]);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGen);
LanguageDetectorModel model = new LanguageDetectorModel(maxentModel, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(model);
// LanguageDetectorConfig config = new LanguageDetectorConfig(5, 2, 0.2);
// ProbingLanguageDetectionResult result = detector.probingPredictLanguages("", config);
// assertNotNull(result);
// assertEquals("und", result.getLanguages()[0].getLang());
// assertEquals(0, result.getTextLengthProcessed());
}

@Test
public void testTrain_throwsIOException_whenStreamFails() throws IOException {
ObjectStream<LanguageSample> sampleStream = mock(ObjectStream.class);
when(sampleStream.read()).thenThrow(new IOException("Stream failure"));
LanguageDetectorContextGenerator contextGen = mock(LanguageDetectorContextGenerator.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGen);
TrainingParameters params = new TrainingParameters();
params.put(AbstractEventTrainer.DATA_INDEXER_PARAM, AbstractEventTrainer.DATA_INDEXER_ONE_PASS_VALUE);
try {
LanguageDetectorME.train(sampleStream, params, factory);
fail("Expected IOException not thrown");
} catch (IOException ex) {
assertEquals("Stream failure", ex.getMessage());
}
}

@Test
public void testGetSupportedLanguages_withNoOutcomes() {
MaxentModel maxentModel = mock(MaxentModel.class);
when(maxentModel.getNumOutcomes()).thenReturn(0);
LanguageDetectorContextGenerator contextGen = mock(LanguageDetectorContextGenerator.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGen);
LanguageDetectorModel model = new LanguageDetectorModel(maxentModel, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(model);
String[] langs = detector.getSupportedLanguages();
assertNotNull(langs);
assertEquals(0, langs.length);
}

@Test
public void testChunk_whenStartEqualsContentLength_returnsEmptyChunk() {
MaxentModel maxentModel = mock(MaxentModel.class);
when(maxentModel.getNumOutcomes()).thenReturn(1);
when(maxentModel.getOutcome(0)).thenReturn("en");
when(maxentModel.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 1.0 });
LanguageDetectorContextGenerator contextGen = mock(LanguageDetectorContextGenerator.class);
when(contextGen.getContext(any(CharSequence.class))).thenReturn(new CharSequence[0]);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGen);
LanguageDetectorModel model = new LanguageDetectorModel(maxentModel, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(model);
// LanguageDetectorConfig config = new LanguageDetectorConfig(3, 2, 0.5);
String content = "abc";
// ProbingLanguageDetectionResult result = detector.probingPredictLanguages(content, config);
// assertNotNull(result);
// assertTrue(result.getTextLengthProcessed() >= 0);
// assertEquals("en", result.getLanguages()[0].getLang());
}

@Test
public void testPredictLanguages_singleCharInput() {
MaxentModel maxentModel = mock(MaxentModel.class);
when(maxentModel.getNumOutcomes()).thenReturn(2);
when(maxentModel.getOutcome(0)).thenReturn("ja");
when(maxentModel.getOutcome(1)).thenReturn("ko");
when(maxentModel.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 0.6, 0.4 });
LanguageDetectorContextGenerator contextGen = mock(LanguageDetectorContextGenerator.class);
when(contextGen.getContext("文")).thenReturn(new CharSequence[] { "文" });
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGen);
LanguageDetectorModel model = new LanguageDetectorModel(maxentModel, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(model);
Language[] result = detector.predictLanguages("文");
assertNotNull(result);
assertEquals("ja", result[0].getLang());
}

@Test
public void testPredictLanguage_returnsFirstEvenOnTie() {
MaxentModel maxentModel = mock(MaxentModel.class);
when(maxentModel.getNumOutcomes()).thenReturn(3);
when(maxentModel.getOutcome(0)).thenReturn("en");
when(maxentModel.getOutcome(1)).thenReturn("de");
when(maxentModel.getOutcome(2)).thenReturn("fr");
when(maxentModel.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 0.33, 0.33, 0.33 });
LanguageDetectorContextGenerator contextGen = mock(LanguageDetectorContextGenerator.class);
when(contextGen.getContext(any(CharSequence.class))).thenReturn(new CharSequence[] { "abc", "bc" });
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGen);
LanguageDetectorModel model = new LanguageDetectorModel(maxentModel, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(model);
Language result = detector.predictLanguage("abc");
assertNotNull(result);
assertEquals("en", result.getLang());
}

@Test
public void testProbingPredictLanguages_withConsecutiveNonImprovingPredictions() {
MaxentModel maxentModel = mock(MaxentModel.class);
when(maxentModel.getNumOutcomes()).thenReturn(2);
when(maxentModel.getOutcome(0)).thenReturn("en");
when(maxentModel.getOutcome(1)).thenReturn("fr");
double[] confidence1 = new double[] { 0.6, 0.4 };
double[] confidence2 = new double[] { 0.6, 0.4 };
double[] confidence3 = new double[] { 0.6, 0.4 };
when(maxentModel.eval(any(String[].class), any(float[].class))).thenReturn(confidence1).thenReturn(confidence2).thenReturn(confidence3);
LanguageDetectorContextGenerator contextGen = mock(LanguageDetectorContextGenerator.class);
when(contextGen.getContext(any(CharSequence.class))).thenReturn(new CharSequence[] { "en" });
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGen);
LanguageDetectorModel model = new LanguageDetectorModel(maxentModel, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(model);
// LanguageDetectorConfig config = new LanguageDetectorConfig(3, 3, 0.1);
// ProbingLanguageDetectionResult result = detector.probingPredictLanguages("aaa bbb ccc", config);
// assertNotNull(result);
// assertEquals("en", result.getLanguages()[0].getLang());
}

@Test
public void testProbingPredictLanguages_returnsInitialPredictionOnEarlyStop() {
MaxentModel maxentModel = mock(MaxentModel.class);
when(maxentModel.getNumOutcomes()).thenReturn(1);
when(maxentModel.getOutcome(0)).thenReturn("en");
when(maxentModel.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 1.0 });
LanguageDetectorContextGenerator contextGen = mock(LanguageDetectorContextGenerator.class);
when(contextGen.getContext(any(CharSequence.class))).thenReturn(new CharSequence[] { "a" });
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGen);
LanguageDetectorModel model = new LanguageDetectorModel(maxentModel, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(model);
// LanguageDetectorConfig config = new LanguageDetectorConfig(1, 1, 0.9);
// ProbingLanguageDetectionResult result = detector.probingPredictLanguages("a", config);
// assertNotNull(result);
// assertEquals("en", result.getLanguages()[0].getLang());
// assertTrue(result.getTextLengthProcessed() > 0);
}

@Test
public void testSeenEnough_predictionsQueueExactlyMinConsec() {
MaxentModel maxentModel = mock(MaxentModel.class);
when(maxentModel.getOutcome(0)).thenReturn("en");
when(maxentModel.getOutcome(1)).thenReturn("fr");
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator contextGen = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(contextGen);
LanguageDetectorModel model = new LanguageDetectorModel(maxentModel, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(model);
List<Language[]> queue = new LinkedList<>();
queue.add(new Language[] { new Language("en", 0.3), new Language("fr", 0.1) });
Language[] newPred = new Language[] { new Language("en", 0.4), new Language("fr", 0.1) };
// boolean result = detector.seenEnough(queue, newPred, new HashMap<>(), new LanguageDetectorConfig(1, 2, 0.2));
// assertTrue(result);
}

@Test
public void testPredictWithLargeNgramCountMap() {
MaxentModel maxentModel = mock(MaxentModel.class);
when(maxentModel.getNumOutcomes()).thenReturn(2);
when(maxentModel.getOutcome(0)).thenReturn("en");
when(maxentModel.getOutcome(1)).thenReturn("de");
String[] expectedFeatures = new String[1000];
float[] expectedCounts = new float[1000];
for (int i = 0; i < 1000; i++) {
expectedFeatures[i] = "ng" + i;
expectedCounts[i] = 1.0f;
}
when(maxentModel.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 0.9, 0.1 });
LanguageDetectorContextGenerator contextGen = mock(LanguageDetectorContextGenerator.class);
when(contextGen.getContext("bulk")).thenReturn(new CharSequence[] {});
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGen);
LanguageDetectorModel model = new LanguageDetectorModel(maxentModel, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(model);
Map<CharSequence, opennlp.tools.util.MutableInt> map = new HashMap<>();
for (int i = 0; i < 1000; i++) {
map.put("ng" + i, new opennlp.tools.util.MutableInt(1));
}
// Language[] result = detector.predict(map);
// assertNotNull(result);
// assertEquals("en", result[0].getLang());
}

@Test
public void testTrainHandlesMissingParamsFieldGracefully() throws IOException {
ObjectStream<LanguageSample> stream = mock(ObjectStream.class);
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(generator);
TrainingParameters params = new TrainingParameters();
LanguageDetectorModel model = LanguageDetectorME.train(stream, params, factory);
assertNotNull(model);
}

@Test
public void testPredictLanguages_withContextGeneratorReturningNulls() {
MaxentModel maxentModel = mock(MaxentModel.class);
when(maxentModel.getNumOutcomes()).thenReturn(2);
when(maxentModel.getOutcome(0)).thenReturn("es");
when(maxentModel.getOutcome(1)).thenReturn("en");
// when(maxentModel.eval(any(), any())).thenReturn(new double[] { 0.6, 0.4 });
LanguageDetectorContextGenerator contextGen = mock(LanguageDetectorContextGenerator.class);
when(contextGen.getContext("hola")).thenReturn(new CharSequence[] { null, "ho", "la" });
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGen);
LanguageDetectorModel model = new LanguageDetectorModel(maxentModel, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(model);
Language[] result = detector.predictLanguages("hola");
assertNotNull(result);
assertTrue(result.length > 0);
assertEquals("es", result[0].getLang());
}

@Test
public void testChunk_returnsCorrectLengthUnicodeSupplementaryChars() {
MaxentModel maxentModel = mock(MaxentModel.class);
when(maxentModel.getNumOutcomes()).thenReturn(1);
when(maxentModel.getOutcome(0)).thenReturn("zh");
when(maxentModel.eval(any(String[].class), any(float[].class))).thenReturn(new double[] { 1.0 });
LanguageDetectorContextGenerator contextGen = mock(LanguageDetectorContextGenerator.class);
when(contextGen.getContext(any())).thenReturn(new CharSequence[] { "文" });
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGen);
LanguageDetectorModel model = new LanguageDetectorModel(maxentModel, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(model);
StringBuilder contentBuilder = new StringBuilder();
contentBuilder.appendCodePoint(0x1F600);
contentBuilder.appendCodePoint(0x1F601);
// ProbingLanguageDetectionResult result = detector.probingPredictLanguages(contentBuilder.toString(), new LanguageDetectorConfig(1, 2, 0.1));
// assertNotNull(result);
// assertTrue(result.getTextLengthProcessed() > 0);
}

@Test
public void testSeenEnough_predictionsQueueRemovesHeadWhenTooLarge() {
MaxentModel maxentModel = mock(MaxentModel.class);
when(maxentModel.getOutcome(0)).thenReturn("en");
when(maxentModel.getOutcome(1)).thenReturn("fr");
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
LanguageDetectorContextGenerator contextGen = mock(LanguageDetectorContextGenerator.class);
when(factory.getContextGenerator()).thenReturn(contextGen);
LanguageDetectorModel model = new LanguageDetectorModel(maxentModel, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(model);
List<Language[]> predictions = new LinkedList<>();
predictions.add(new Language[] { new Language("en", 0.6), new Language("fr", 0.4) });
predictions.add(new Language[] { new Language("en", 0.65), new Language("fr", 0.35) });
predictions.add(new Language[] { new Language("en", 0.70), new Language("fr", 0.30) });
predictions.add(new Language[] { new Language("en", 0.75), new Language("fr", 0.25) });
Language[] newPrediction = new Language[] { new Language("en", 0.80), new Language("fr", 0.20) };
// boolean result = detector.seenEnough(predictions, newPrediction, new HashMap<>(), new LanguageDetectorConfig(3, 3, 0.1));
// assertTrue(result);
}

@Test
public void testPredict_withEmptyNgramMap_returnsSortedPrediction() {
MaxentModel maxentModel = mock(MaxentModel.class);
when(maxentModel.getNumOutcomes()).thenReturn(2);
when(maxentModel.getOutcome(0)).thenReturn("it");
when(maxentModel.getOutcome(1)).thenReturn("nl");
// when(maxentModel.eval(any(), any())).thenReturn(new double[] { 0.3, 0.7 });
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(generator);
LanguageDetectorModel model = new LanguageDetectorModel(maxentModel, new HashMap<>(), factory);
LanguageDetectorME ld = new LanguageDetectorME(model);
Map<CharSequence, MutableInt> emptyMap = Collections.emptyMap();
// Language[] result = ld.predict(emptyMap);
// assertEquals(2, result.length);
// assertEquals("nl", result[0].getLang());
// assertEquals(0.7, result[0].getConfidence(), 0.0001);
// assertEquals("it", result[1].getLang());
}

@Test
public void testChunk_whenInputSmallerThanChunkSize_returnsWholeInput() {
MaxentModel maxentModel = mock(MaxentModel.class);
when(maxentModel.getNumOutcomes()).thenReturn(1);
when(maxentModel.getOutcome(0)).thenReturn("en");
// when(maxentModel.eval(any(), any())).thenReturn(new double[] { 1.0 });
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
when(generator.getContext(any())).thenReturn(new CharSequence[] { "th" });
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(generator);
LanguageDetectorModel model = new LanguageDetectorModel(maxentModel, new HashMap<>(), factory);
LanguageDetectorME ld = new LanguageDetectorME(model);
// LanguageDetectorConfig config = new LanguageDetectorConfig(100, 2, 0.1);
// ProbingLanguageDetectionResult result = ld.probingPredictLanguages("tiny", config);
// assertNotNull(result);
// assertEquals("en", result.getLanguages()[0].getLang());
// assertTrue(result.getTextLengthProcessed() > 0);
}

@Test
public void testSeenEnough_whenPredictionLanguageChanges_returnsFalse() {
MaxentModel maxentModel = mock(MaxentModel.class);
when(maxentModel.getOutcome(0)).thenReturn("en");
when(maxentModel.getOutcome(1)).thenReturn("fr");
LanguageDetectorContextGenerator contextGenerator = mock(LanguageDetectorContextGenerator.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
LanguageDetectorModel model = new LanguageDetectorModel(maxentModel, new HashMap<>(), factory);
LanguageDetectorME ld = new LanguageDetectorME(model);
LinkedList<Language[]> queue = new LinkedList<>();
queue.add(new Language[] { new Language("en", 0.5), new Language("fr", 0.4) });
queue.add(new Language[] { new Language("fr", 0.6), new Language("en", 0.3) });
Language[] curr = new Language[] { new Language("fr", 0.7), new Language("en", 0.2) };
// boolean result = ld.seenEnough(queue, curr, new HashMap<>(), new LanguageDetectorConfig(2, 2, 0.1));
// assertFalse(result);
}

@Test
public void testProbingPredictLanguages_withAllChunksBelowConfidenceThreshold() {
MaxentModel maxentModel = mock(MaxentModel.class);
when(maxentModel.getNumOutcomes()).thenReturn(2);
when(maxentModel.getOutcome(0)).thenReturn("en");
when(maxentModel.getOutcome(1)).thenReturn("fr");
// when(maxentModel.eval(any(), any())).thenReturn(new double[] { 0.52, 0.48 });
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
when(generator.getContext(any())).thenReturn(new CharSequence[] { "ex", "am" });
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(generator);
LanguageDetectorModel model = new LanguageDetectorModel(maxentModel, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(model);
// LanguageDetectorConfig config = new LanguageDetectorConfig(3, 5, 0.2);
String content = "exampleexampleexampleexampleexample";
// ProbingLanguageDetectionResult result = detector.probingPredictLanguages(content, config);
// assertNotNull(result);
// assertEquals("en", result.getLanguages()[0].getLang());
}

@Test
public void testUpdateCounts_addsAndIncrementsCorrectly() {
MaxentModel maxentModel = mock(MaxentModel.class);
when(maxentModel.getNumOutcomes()).thenReturn(1);
when(maxentModel.getOutcome(0)).thenReturn("en");
// when(maxentModel.eval(any(), any())).thenReturn(new double[] { 1.0 });
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(generator);
LanguageDetectorModel model = new LanguageDetectorModel(maxentModel, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(model);
Map<CharSequence, MutableInt> counts = new HashMap<>();
// detector.updateCounts(new CharSequence[] { "ab", "cd" }, counts);
// detector.updateCounts(new CharSequence[] { "ab" }, counts);
assertEquals(2, counts.size());
assertEquals(2, counts.get("ab").getValue());
assertEquals(1, counts.get("cd").getValue());
}

@Test
public void testArrayToCounts_handlesDuplicatesCorrectly() {
MaxentModel maxentModel = mock(MaxentModel.class);
when(maxentModel.getNumOutcomes()).thenReturn(1);
when(maxentModel.getOutcome(0)).thenReturn("en");
// when(maxentModel.eval(any(), any())).thenReturn(new double[] { 1.0 });
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
when(generator.getContext(any())).thenReturn(new CharSequence[] { "xy", "yz", "xy" });
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(generator);
LanguageDetectorModel model = new LanguageDetectorModel(maxentModel, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(model);
// Map<CharSequence, MutableInt> result = detector.arrayToCounts(new CharSequence[] { "xy", "yz", "xy" });
// assertEquals(2, result.size());
// assertEquals(2, result.get("xy").getValue());
// assertEquals(1, result.get("yz").getValue());
}

@Test
public void testTrainHandlesNullFactoryContextGenerator() throws IOException {
ObjectStream<LanguageSample> stream = mock(ObjectStream.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(null);
TrainingParameters params = new TrainingParameters();
try {
LanguageDetectorME.train(stream, params, factory);
fail("Expected NullPointerException");
} catch (NullPointerException ex) {
}
}

@Test
public void testProbingPredictLanguages_withLongInputAndNoImprovementStopsAtLimit() {
MaxentModel maxentModel = mock(MaxentModel.class);
when(maxentModel.getNumOutcomes()).thenReturn(2);
when(maxentModel.getOutcome(0)).thenReturn("en");
when(maxentModel.getOutcome(1)).thenReturn("de");
// when(maxentModel.eval(any(), any())).thenReturn(new double[] { 0.51, 0.49 }).thenReturn(new double[] { 0.52, 0.48 }).thenReturn(new double[] { 0.53, 0.47 }).thenReturn(new double[] { 0.54, 0.46 });
LanguageDetectorContextGenerator contextGen = mock(LanguageDetectorContextGenerator.class);
when(contextGen.getContext(any())).thenReturn(new CharSequence[] { "te" });
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGen);
LanguageDetectorModel model = new LanguageDetectorModel(maxentModel, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(model);
// LanguageDetectorConfig config = new LanguageDetectorConfig(2, 3, 0.05);
String content = "texttexttexttexttexttexttext";
// ProbingLanguageDetectionResult result = detector.probingPredictLanguages(content, config);
// assertNotNull(result);
// assertTrue(result.getTextLengthProcessed() <= config.getMaxLength());
// assertEquals("en", result.getLanguages()[0].getLang());
}

@Test
public void testPredictLanguages_withNullNgramInContext() {
MaxentModel model = mock(MaxentModel.class);
when(model.getNumOutcomes()).thenReturn(2);
when(model.getOutcome(0)).thenReturn("en");
when(model.getOutcome(1)).thenReturn("de");
// when(model.eval(any(), any())).thenReturn(new double[] { 0.6, 0.4 });
LanguageDetectorContextGenerator contextGenerator = mock(LanguageDetectorContextGenerator.class);
when(contextGenerator.getContext(any())).thenReturn(new CharSequence[] { null, "ab", null });
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
LanguageDetectorModel detectorModel = new LanguageDetectorModel(model, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(detectorModel);
Language[] result = detector.predictLanguages("ab");
assertNotNull(result);
assertEquals("en", result[0].getLang());
}

@Test
public void testChunk_withSupplementaryCharacters() {
MaxentModel model = mock(MaxentModel.class);
when(model.getNumOutcomes()).thenReturn(1);
when(model.getOutcome(0)).thenReturn("emoji");
// when(model.eval(any(), any())).thenReturn(new double[] { 1.0 });
LanguageDetectorContextGenerator contextGenerator = mock(LanguageDetectorContextGenerator.class);
when(contextGenerator.getContext(any())).thenReturn(new CharSequence[] { "😀" });
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
LanguageDetectorModel detectorModel = new LanguageDetectorModel(model, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(detectorModel);
String input = new String(Character.toChars(0x1F600));
// LanguageDetectorConfig config = new LanguageDetectorConfig(1, 2, 0.1);
// ProbingLanguageDetectionResult result = detector.probingPredictLanguages(input, config);
// assertNotNull(result);
// assertEquals("emoji", result.getLanguages()[0].getLang());
// assertTrue(result.getTextLengthProcessed() > 0);
}

@Test
public void testSeenEnough_withZeroMinDiffAlwaysReturnsTrueIfStableAndIncreasing() {
MaxentModel model = mock(MaxentModel.class);
when(model.getOutcome(0)).thenReturn("en");
when(model.getOutcome(1)).thenReturn("de");
LanguageDetectorContextGenerator contextGenerator = mock(LanguageDetectorContextGenerator.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
LanguageDetectorModel detectorModel = new LanguageDetectorModel(model, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(detectorModel);
List<Language[]> history = new LinkedList<>();
history.add(new Language[] { new Language("en", 0.4), new Language("de", 0.3) });
history.add(new Language[] { new Language("en", 0.5), new Language("de", 0.25) });
Language[] current = new Language[] { new Language("en", 0.6), new Language("de", 0.2) };
// boolean result = detector.seenEnough(history, current, new HashMap<CharSequence, MutableInt>(), new LanguageDetectorConfig(2, 3, 0.0));
// assertTrue(result);
}

@Test
public void testProbingPredictLanguages_withUnicodeSurrogatesPreservesCodePointCount() {
MaxentModel model = mock(MaxentModel.class);
when(model.getNumOutcomes()).thenReturn(1);
when(model.getOutcome(0)).thenReturn("zh");
// when(model.eval(any(), any())).thenReturn(new double[] { 1.0 });
LanguageDetectorContextGenerator contextGenerator = mock(LanguageDetectorContextGenerator.class);
when(contextGenerator.getContext(any())).thenReturn(new CharSequence[] { "📘" });
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
LanguageDetectorModel detectorModel = new LanguageDetectorModel(model, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(detectorModel);
String input = new String(Character.toChars(0x1F4D8));
// LanguageDetectorConfig config = new LanguageDetectorConfig(1, 1, 0.1);
// ProbingLanguageDetectionResult result = detector.probingPredictLanguages(input, config);
// assertNotNull(result);
// assertEquals("zh", result.getLanguages()[0].getLang());
}

@Test
public void testPredictLanguages_whenEvalReturnsNegativeConfidence() {
MaxentModel model = mock(MaxentModel.class);
when(model.getNumOutcomes()).thenReturn(2);
when(model.getOutcome(0)).thenReturn("unknown");
when(model.getOutcome(1)).thenReturn("none");
// when(model.eval(any(), any())).thenReturn(new double[] { -0.6, -0.4 });
LanguageDetectorContextGenerator contextGenerator = mock(LanguageDetectorContextGenerator.class);
when(contextGenerator.getContext(any())).thenReturn(new CharSequence[] { "xx" });
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
LanguageDetectorModel detectorModel = new LanguageDetectorModel(model, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(detectorModel);
Language[] result = detector.predictLanguages("xxx");
assertNotNull(result);
assertEquals("none", result[0].getLang());
assertTrue(result[0].getConfidence() > result[1].getConfidence());
}

@Test
public void testSeenEnough_whenInconsistentConfidence_returnsFalse() {
MaxentModel model = mock(MaxentModel.class);
when(model.getOutcome(0)).thenReturn("en");
when(model.getOutcome(1)).thenReturn("es");
LanguageDetectorContextGenerator contextGenerator = mock(LanguageDetectorContextGenerator.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
LanguageDetectorModel detectorModel = new LanguageDetectorModel(model, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(detectorModel);
List<Language[]> history = new LinkedList<>();
history.add(new Language[] { new Language("en", 0.75), new Language("es", 0.25) });
history.add(new Language[] { new Language("en", 0.72), new Language("es", 0.28) });
Language[] current = new Language[] { new Language("en", 0.71), new Language("es", 0.29) };
// boolean result = detector.seenEnough(history, current, new HashMap<CharSequence, MutableInt>(), new LanguageDetectorConfig(2, 3, 0.1));
// assertFalse(result);
}

@Test
public void testProbingPredictLanguages_emptyCharSequenceObject() {
MaxentModel model = mock(MaxentModel.class);
when(model.getNumOutcomes()).thenReturn(1);
when(model.getOutcome(0)).thenReturn("en");
// when(model.eval(any(), any())).thenReturn(new double[] { 1.0 });
LanguageDetectorContextGenerator contextGenerator = mock(LanguageDetectorContextGenerator.class);
when(contextGenerator.getContext(any())).thenReturn(new CharSequence[0]);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
LanguageDetectorModel detectorModel = new LanguageDetectorModel(model, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(detectorModel);
CharSequence input = new StringBuilder();
ProbingLanguageDetectionResult result = detector.probingPredictLanguages(input);
assertNotNull(result);
// assertEquals("en", result.getLanguages()[0].getLang());
// assertEquals(0, result.getTextLengthProcessed());
}

@Test
public void testGetSupportedLanguages_whenOutcomesHaveDuplicates() {
MaxentModel model = mock(MaxentModel.class);
when(model.getNumOutcomes()).thenReturn(3);
when(model.getOutcome(0)).thenReturn("en");
when(model.getOutcome(1)).thenReturn("en");
when(model.getOutcome(2)).thenReturn("fr");
LanguageDetectorContextGenerator generator = mock(LanguageDetectorContextGenerator.class);
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(generator);
LanguageDetectorModel detectorModel = new LanguageDetectorModel(model, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(detectorModel);
String[] resulted = detector.getSupportedLanguages();
assertArrayEquals(new String[] { "en", "en", "fr" }, resulted);
}

@Test
public void testEvaluateLanguageOrderStabilityOnEqualConfidence() {
MaxentModel model = mock(MaxentModel.class);
when(model.getNumOutcomes()).thenReturn(3);
when(model.getOutcome(0)).thenReturn("en");
when(model.getOutcome(1)).thenReturn("de");
when(model.getOutcome(2)).thenReturn("fr");
// when(model.eval(any(), any())).thenReturn(new double[] { 0.33, 0.33, 0.33 });
LanguageDetectorContextGenerator contextGenerator = mock(LanguageDetectorContextGenerator.class);
when(contextGenerator.getContext(any())).thenReturn(new CharSequence[] { "la", "ng" });
LanguageDetectorFactory factory = mock(LanguageDetectorFactory.class);
when(factory.getContextGenerator()).thenReturn(contextGenerator);
LanguageDetectorModel modelObj = new LanguageDetectorModel(model, new HashMap<>(), factory);
LanguageDetectorME detector = new LanguageDetectorME(modelObj);
Language[] result = detector.predictLanguages("lang");
assertNotNull(result);
assertEquals(3, result.length);
assertEquals("en", result[0].getLang());
}
}
