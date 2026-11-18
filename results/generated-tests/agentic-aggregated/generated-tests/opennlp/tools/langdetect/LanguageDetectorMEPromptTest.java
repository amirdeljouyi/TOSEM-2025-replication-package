package opennlp.tools.langdetect;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import opennlp.tools.ml.AbstractEventTrainer;
import opennlp.tools.ml.maxent.GISModel;
import opennlp.tools.ml.model.Context;
import opennlp.tools.util.CollectionObjectStream;
import opennlp.tools.util.TrainingParameters;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class LanguageDetectorMEPromptTest {

  @Test
  public void getSupportedLanguagesPreservesModelOutcomeOrder() {
    GISModel classifier = new GISModel(
        new Context[] {new Context(new int[] {0, 1, 2}, new double[] {4, 0, -2})},
        new String[] {"a"}, new String[] {"eng", "fra", "deu"});
    LanguageDetectorModel model = new LanguageDetectorModel(classifier, new HashMap<>(),
        new LanguageDetectorFactory());
    LanguageDetectorME detector = new LanguageDetectorME(model);

    assertArrayEquals(new String[] {"eng", "fra", "deu"}, detector.getSupportedLanguages());
  }

  @Test
  public void getSupportedLanguagesReturnsANewArrayOnEveryCall() {
    GISModel classifier = new GISModel(
        new Context[] {new Context(new int[] {0, 1}, new double[] {2, -2})},
        new String[] {"a"}, new String[] {"eng", "fra"});
    LanguageDetectorModel model = new LanguageDetectorModel(classifier, new HashMap<>(),
        new LanguageDetectorFactory());
    LanguageDetectorME detector = new LanguageDetectorME(model);

    String[] first = detector.getSupportedLanguages();
    first[0] = "changed";
    String[] second = detector.getSupportedLanguages();

    assertNotSame(first, second);
    assertArrayEquals(new String[] {"eng", "fra"}, second);
  }

  @Test
  public void predictLanguagesSortsOutcomesByDescendingConfidence() {
    GISModel classifier = new GISModel(
        new Context[] {new Context(new int[] {0, 1, 2}, new double[] {-2, 0, 4})},
        new String[] {"a"}, new String[] {"eng", "fra", "deu"});
    LanguageDetectorModel model = new LanguageDetectorModel(classifier, new HashMap<>(),
        new LanguageDetectorFactory());
    LanguageDetectorME detector = new LanguageDetectorME(model);

    Language[] languages = detector.predictLanguages("a");

    assertEquals("deu", languages[0].getLang());
    assertEquals("fra", languages[1].getLang());
    assertEquals("eng", languages[2].getLang());
    assertTrue(languages[0].getConfidence() > languages[1].getConfidence());
    assertTrue(languages[1].getConfidence() > languages[2].getConfidence());
  }

  @Test
  public void predictLanguageReturnsTopPredictionFromFullPrediction() {
    GISModel classifier = new GISModel(
        new Context[] {new Context(new int[] {0, 1}, new double[] {-3, 3})},
        new String[] {"b"}, new String[] {"eng", "fra"});
    LanguageDetectorModel model = new LanguageDetectorModel(classifier, new HashMap<>(),
        new LanguageDetectorFactory());
    LanguageDetectorME detector = new LanguageDetectorME(model);

    Language best = detector.predictLanguage("b");
    Language[] all = detector.predictLanguages("b");

    assertEquals("fra", best.getLang());
    assertEquals(all[0].getLang(), best.getLang());
    assertEquals(all[0].getConfidence(), best.getConfidence(), 0.0);
  }

  @Test
  public void repeatedNgramOccurrencesAreTreatedAsBinaryFeatures() {
    GISModel classifier = new GISModel(
        new Context[] {new Context(new int[] {0, 1}, new double[] {3, -3})},
        new String[] {"a"}, new String[] {"eng", "fra"});
    LanguageDetectorModel model = new LanguageDetectorModel(classifier, new HashMap<>(),
        new LanguageDetectorFactory());
    LanguageDetectorME detector = new LanguageDetectorME(model);

    Language[] once = detector.predictLanguages("a");
    Language[] repeated = detector.predictLanguages("aaaaaa");

    assertEquals("eng", repeated[0].getLang());
    assertEquals(once[0].getConfidence(), repeated[0].getConfidence(), 0.0);
    assertEquals(once[1].getConfidence(), repeated[1].getConfidence(), 0.0);
  }

  @Test
  public void unknownFeaturesProduceUniformProbabilities() {
    GISModel classifier = new GISModel(
        new Context[] {new Context(new int[] {0, 1, 2}, new double[] {4, 0, -2})},
        new String[] {"a"}, new String[] {"eng", "fra", "deu"});
    LanguageDetectorModel model = new LanguageDetectorModel(classifier, new HashMap<>(),
        new LanguageDetectorFactory());
    LanguageDetectorME detector = new LanguageDetectorME(model);

    Language[] languages = detector.predictLanguages("zzz");

    assertEquals("eng", languages[0].getLang());
    assertEquals("fra", languages[1].getLang());
    assertEquals("deu", languages[2].getLang());
    assertEquals(1.0 / 3.0, languages[0].getConfidence(), 0.0000001);
    assertEquals(1.0 / 3.0, languages[1].getConfidence(), 0.0000001);
    assertEquals(1.0 / 3.0, languages[2].getConfidence(), 0.0000001);
  }

  @Test
  public void emptyContentCanStillBePredicted() {
    GISModel classifier = new GISModel(
        new Context[] {new Context(new int[] {0, 1}, new double[] {3, -3})},
        new String[] {"a"}, new String[] {"eng", "fra"});
    LanguageDetectorModel model = new LanguageDetectorModel(classifier, new HashMap<>(),
        new LanguageDetectorFactory());
    LanguageDetectorME detector = new LanguageDetectorME(model);

    Language[] languages = detector.predictLanguages("");

    assertEquals(2, languages.length);
    assertEquals("eng", languages[0].getLang());
    assertEquals(0.5, languages[0].getConfidence(), 0.0000001);
    assertEquals(0.5, languages[1].getConfidence(), 0.0000001);
  }

  @Test
  public void contributionsFromDifferentNgramsAreCombined() {
    GISModel classifier = new GISModel(
        new Context[] {
            new Context(new int[] {0, 1, 2}, new double[] {4, 0, -2}),
            new Context(new int[] {0, 1, 2}, new double[] {0, 4, -2}),
            new Context(new int[] {0, 1, 2}, new double[] {2, 2, -2})},
        new String[] {"a", "b", "ab"}, new String[] {"eng", "fra", "deu"});
    LanguageDetectorModel model = new LanguageDetectorModel(classifier, new HashMap<>(),
        new LanguageDetectorFactory());
    LanguageDetectorME detector = new LanguageDetectorME(model);

    Language[] languages = detector.predictLanguages("ab");

    assertEquals("eng", languages[0].getLang());
    assertEquals("fra", languages[1].getLang());
    assertEquals(languages[0].getConfidence(), languages[1].getConfidence(), 0.0);
    assertTrue(languages[1].getConfidence() > languages[2].getConfidence());
  }

  @Test
  public void acceptsNonStringCharSequenceInput() {
    GISModel classifier = new GISModel(
        new Context[] {
            new Context(new int[] {0, 1}, new double[] {3, -3}),
            new Context(new int[] {0, 1}, new double[] {-3, 3})},
        new String[] {"a", "b"}, new String[] {"eng", "fra"});
    LanguageDetectorModel model = new LanguageDetectorModel(classifier, new HashMap<>(),
        new LanguageDetectorFactory());
    LanguageDetectorME detector = new LanguageDetectorME(model);

    Language best = detector.predictLanguage(new StringBuilder("bbb"));

    assertEquals("fra", best.getLang());
    assertTrue(best.getConfidence() > 0.99);
  }

  @Test
  public void nullContentIsRejected() {
    GISModel classifier = new GISModel(
        new Context[] {new Context(new int[] {0, 1}, new double[] {3, -3})},
        new String[] {"a"}, new String[] {"eng", "fra"});
    LanguageDetectorModel model = new LanguageDetectorModel(classifier, new HashMap<>(),
        new LanguageDetectorFactory());
    LanguageDetectorME detector = new LanguageDetectorME(model);

    assertThrows(NullPointerException.class, () -> detector.predictLanguages(null));
  }

  @Test
  public void nullModelIsRejectedByConstructor() {
    assertThrows(NullPointerException.class, () -> new LanguageDetectorME(null));
  }

  @Test
  public void probingStopsAfterRequiredConsecutiveStablePredictions() throws Exception {
    GISModel classifier = new GISModel(
        new Context[] {new Context(new int[] {0, 1}, new double[] {3, -3})},
        new String[] {"a"}, new String[] {"eng", "fra"});
    LanguageDetectorModel model = new LanguageDetectorModel(classifier, new HashMap<>(),
        new LanguageDetectorFactory());
    LanguageDetectorME detector = new LanguageDetectorME(model);
    LanguageDetectorConfig config = new LanguageDetectorConfig();
    config.setChunkSize(1);
    config.setMaxLength(20);
    config.setMinConsecImprovements(2);
    config.setMinDiff(0.5);

    Object result = detector.probingPredictLanguages("aaaaaaaaaa", config);
    int length = (Integer) result.getClass().getMethod("getLength").invoke(result);
    Language[] languages = (Language[]) result.getClass().getMethod("getLanguages").invoke(result);

    assertEquals(3, length);
    assertEquals("eng", languages[0].getLang());
    assertTrue(languages[0].getConfidence() > 0.99);
  }

  @Test
  public void probingProcessesAllContentWhenConfidenceDifferenceIsTooSmall() throws Exception {
    GISModel classifier = new GISModel(
        new Context[] {new Context(new int[] {0, 1}, new double[] {0.1, 0})},
        new String[] {"a"}, new String[] {"eng", "fra"});
    LanguageDetectorModel model = new LanguageDetectorModel(classifier, new HashMap<>(),
        new LanguageDetectorFactory());
    LanguageDetectorME detector = new LanguageDetectorME(model);
    LanguageDetectorConfig config = new LanguageDetectorConfig();
    config.setChunkSize(1);
    config.setMaxLength(20);
    config.setMinConsecImprovements(0);
    config.setMinDiff(0.2);

    Object result = detector.probingPredictLanguages("aaaaa", config);
    int length = (Integer) result.getClass().getMethod("getLength").invoke(result);
    Language[] languages = (Language[]) result.getClass().getMethod("getLanguages").invoke(result);

    assertEquals(5, length);
    assertEquals("eng", languages[0].getLang());
  }

  @Test
  public void probingHonorsMaximumCodePointLength() throws Exception {
    GISModel classifier = new GISModel(
        new Context[] {new Context(new int[] {0, 1}, new double[] {3, -3})},
        new String[] {"a"}, new String[] {"eng", "fra"});
    LanguageDetectorModel model = new LanguageDetectorModel(classifier, new HashMap<>(),
        new LanguageDetectorFactory());
    LanguageDetectorME detector = new LanguageDetectorME(model);
    LanguageDetectorConfig config = new LanguageDetectorConfig();
    config.setChunkSize(1);
    config.setMaxLength(2);
    config.setMinConsecImprovements(10);
    config.setMinDiff(0.0);

    Object result = detector.probingPredictLanguages("aaaaaa", config);
    int length = (Integer) result.getClass().getMethod("getLength").invoke(result);
    Language[] languages = (Language[]) result.getClass().getMethod("getLanguages").invoke(result);

    assertEquals(2, length);
    assertEquals("eng", languages[0].getLang());
  }

  @Test
  public void probingCountsSupplementaryCharactersAsSingleCodePoints() throws Exception {
    GISModel classifier = new GISModel(
        new Context[] {new Context(new int[] {0, 1}, new double[] {3, -3})},
        new String[] {"a"}, new String[] {"eng", "fra"});
    LanguageDetectorModel model = new LanguageDetectorModel(classifier, new HashMap<>(),
        new LanguageDetectorFactory());
    LanguageDetectorME detector = new LanguageDetectorME(model);
    LanguageDetectorConfig config = new LanguageDetectorConfig();
    config.setChunkSize(1);
    config.setMaxLength(3);
    config.setMinConsecImprovements(10);
    config.setMinDiff(0.0);

    Object result = detector.probingPredictLanguages("a\uD83D\uDE00a", config);
    int length = (Integer) result.getClass().getMethod("getLength").invoke(result);
    Language[] languages = (Language[]) result.getClass().getMethod("getLanguages").invoke(result);

    assertEquals(3, length);
    assertEquals("eng", languages[0].getLang());
  }

  @Test
  public void probingEmptyContentReturnsZeroProcessedLengthAndUniformPrediction() throws Exception {
    GISModel classifier = new GISModel(
        new Context[] {new Context(new int[] {0, 1}, new double[] {3, -3})},
        new String[] {"a"}, new String[] {"eng", "fra"});
    LanguageDetectorModel model = new LanguageDetectorModel(classifier, new HashMap<>(),
        new LanguageDetectorFactory());
    LanguageDetectorME detector = new LanguageDetectorME(model);

    Object result = detector.probingPredictLanguages("");
    int length = (Integer) result.getClass().getMethod("getLength").invoke(result);
    Language[] languages = (Language[]) result.getClass().getMethod("getLanguages").invoke(result);

    assertEquals(0, length);
    assertEquals(2, languages.length);
    assertEquals(0.5, languages[0].getConfidence(), 0.0000001);
  }

  @Test
  public void probingWithZeroConsecutiveRequirementCanStopAfterFirstChunk() throws Exception {
    GISModel classifier = new GISModel(
        new Context[] {new Context(new int[] {0, 1}, new double[] {3, -3})},
        new String[] {"a"}, new String[] {"eng", "fra"});
    LanguageDetectorModel model = new LanguageDetectorModel(classifier, new HashMap<>(),
        new LanguageDetectorFactory());
    LanguageDetectorME detector = new LanguageDetectorME(model);
    LanguageDetectorConfig config = new LanguageDetectorConfig();
    config.setChunkSize(2);
    config.setMaxLength(20);
    config.setMinConsecImprovements(0);
    config.setMinDiff(0.5);

    Object result = detector.probingPredictLanguages("aaaaaaaa", config);
    int length = (Integer) result.getClass().getMethod("getLength").invoke(result);
    Language[] languages = (Language[]) result.getClass().getMethod("getLanguages").invoke(result);

    assertEquals(2, length);
    assertEquals("eng", languages[0].getLang());
  }

  @Test
  public void probingDoesNotAcceptADecreasingTopConfidenceSequence() throws Exception {
    GISModel classifier = new GISModel(
        new Context[] {
            new Context(new int[] {0, 1}, new double[] {4, 0}),
            new Context(new int[] {0, 1}, new double[] {0, 4})},
        new String[] {"a", "b"}, new String[] {"eng", "fra"});
    LanguageDetectorModel model = new LanguageDetectorModel(classifier, new HashMap<>(),
        new LanguageDetectorFactory());
    LanguageDetectorME detector = new LanguageDetectorME(model);
    LanguageDetectorConfig config = new LanguageDetectorConfig();
    config.setChunkSize(1);
    config.setMaxLength(20);
    config.setMinConsecImprovements(1);
    config.setMinDiff(0.0);

    Object result = detector.probingPredictLanguages("ab", config);
    int length = (Integer) result.getClass().getMethod("getLength").invoke(result);
    Language[] languages = (Language[]) result.getClass().getMethod("getLanguages").invoke(result);

    assertEquals(2, length);
    assertEquals(0.5, languages[0].getConfidence(), 0.0000001);
  }

  @Test
  public void probingRejectsNullConfiguration() {
    GISModel classifier = new GISModel(
        new Context[] {new Context(new int[] {0, 1}, new double[] {3, -3})},
        new String[] {"a"}, new String[] {"eng", "fra"});
    LanguageDetectorModel model = new LanguageDetectorModel(classifier, new HashMap<>(),
        new LanguageDetectorFactory());
    LanguageDetectorME detector = new LanguageDetectorME(model);

    assertThrows(NullPointerException.class,
        () -> detector.probingPredictLanguages("content", null));
  }

  @Test
  public void positiveMinimumDifferenceRequiresAtLeastTwoModelOutcomes() {
    GISModel classifier = new GISModel(
        new Context[] {new Context(new int[] {0}, new double[] {1})},
        new String[] {"a"}, new String[] {"eng"});
    LanguageDetectorModel model = new LanguageDetectorModel(classifier, new HashMap<>(),
        new LanguageDetectorFactory());
    LanguageDetectorME detector = new LanguageDetectorME(model);
    LanguageDetectorConfig config = new LanguageDetectorConfig();
    config.setChunkSize(1);
    config.setMaxLength(10);
    config.setMinConsecImprovements(0);
    config.setMinDiff(0.1);

    assertThrows(ArrayIndexOutOfBoundsException.class,
        () -> detector.probingPredictLanguages("aaa", config));
  }

  @Test
  public void trainBuildsUsableDetectorAndAddsDefaultDataIndexer() throws Exception {
    CollectionObjectStream<LanguageSample> samples = new CollectionObjectStream<>(Arrays.asList(
        new LanguageSample(new Language("eng"), "hello hello world"),
        new LanguageSample(new Language("eng"), "hello world again"),
        new LanguageSample(new Language("eng"), "world hello friend"),
        new LanguageSample(new Language("fra"), "bonjour bonjour monde"),
        new LanguageSample(new Language("fra"), "bonjour monde encore"),
        new LanguageSample(new Language("fra"), "monde bonjour ami")));
    TrainingParameters parameters = TrainingParameters.defaultParams();
    parameters.put(TrainingParameters.CUTOFF_PARAM, 0);
    parameters.put(TrainingParameters.ITERATIONS_PARAM, 20);
    LanguageDetectorFactory factory = new LanguageDetectorFactory();

    LanguageDetectorModel trained = LanguageDetectorME.train(samples, parameters, factory);
    LanguageDetectorME detector = new LanguageDetectorME(trained);
    Language english = detector.predictLanguage("hello world hello");
    Language french = detector.predictLanguage("bonjour monde bonjour");

    assertEquals("OnePass", parameters.getStringParameter(
        AbstractEventTrainer.DATA_INDEXER_PARAM, null));
    assertArrayEquals(new String[] {"eng", "fra"}, detector.getSupportedLanguages());
    assertEquals("eng", english.getLang());
    assertEquals("fra", french.getLang());
  }

  @Test
  public void trainingAnEmptySampleStreamFails() {
    CollectionObjectStream<LanguageSample> samples = new CollectionObjectStream<>(
        Collections.emptyList());
    TrainingParameters parameters = TrainingParameters.defaultParams();
    parameters.put(TrainingParameters.CUTOFF_PARAM, 0);
    parameters.put(TrainingParameters.ITERATIONS_PARAM, 1);

    assertThrows(IOException.class,
        () -> LanguageDetectorME.train(samples, parameters, new LanguageDetectorFactory()));
  }
}
