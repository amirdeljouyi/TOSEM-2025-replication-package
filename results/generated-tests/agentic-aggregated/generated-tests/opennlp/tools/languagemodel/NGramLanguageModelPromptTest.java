package opennlp.tools.languagemodel;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import opennlp.tools.util.StringList;
import org.junit.Test;

public class NGramLanguageModelPromptTest {

  @Test
  public void defaultModelIsInitiallyEmpty() {
    NGramLanguageModel model = new NGramLanguageModel();

    assertEquals(0, model.size());
    assertEquals(0, model.numberOfGrams());
    assertEquals(0.0, model.calculateProbability("anything"), 0.0);
    assertNull(model.predictNextTokens("some", "context"));
  }

  @Test
  public void constructorRejectsZeroOrder() {
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> new NGramLanguageModel(0));

    assertEquals("Parameter 'n' must be greater than 0.", exception.getMessage());
  }

  @Test
  public void constructorRejectsNegativeOrder() {
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> new NGramLanguageModel(-7));

    assertEquals("Parameter 'n' must be greater than 0.", exception.getMessage());
  }

  @Test
  public void addBuildsEveryGramThroughConfiguredOrder() {
    NGramLanguageModel model = new NGramLanguageModel(3);

    model.add("one", "two", "three", "four");

    assertEquals(9, model.size());
    assertEquals(1, model.getCount(new StringList("one")));
    assertEquals(1, model.getCount(new StringList("two", "three")));
    assertEquals(1, model.getCount(new StringList("two", "three", "four")));
    assertEquals(0, model.getCount(new StringList("one", "two", "three", "four")));
  }

  @Test
  public void repeatedTrainingIncreasesCountsWithoutIncreasingVocabularySize() {
    NGramLanguageModel model = new NGramLanguageModel(2);

    model.add("a", "b", "a");
    model.add("a", "b", "a");

    assertEquals(4, model.size());
    assertEquals(4, model.getCount(new StringList("a")));
    assertEquals(2, model.getCount(new StringList("b")));
    assertEquals(2, model.getCount(new StringList("a", "b")));
    assertEquals(2, model.getCount(new StringList("b", "a")));
    assertEquals(10, model.numberOfGrams());
  }

  @Test
  public void unigramProbabilityUsesEntryCountAsDenominator() {
    NGramLanguageModel model = new NGramLanguageModel(1);
    model.add("red", "red", "blue");

    assertEquals(1.0, model.calculateProbability("red"), 0.0);
    assertEquals(0.5, model.calculateProbability("blue"), 0.0);
    assertEquals(0.0, model.calculateProbability("green"), 0.0);
  }

  @Test
  public void observedTrigramProbabilityUsesPrefixCount() {
    NGramLanguageModel model = new NGramLanguageModel(3);
    model.add("a", "b", "c");
    model.add("a", "b", "d");

    assertEquals(0.5, model.calculateProbability("a", "b", "c"), 0.0);
    assertEquals(0.5, model.calculateProbability("a", "b", "d"), 0.0);
  }

  @Test
  public void unseenTrigramBacksOffToObservedSuffix() {
    NGramLanguageModel model = new NGramLanguageModel(3);
    model.add("a", "b", "c");

    assertEquals(0.4, model.calculateProbability("x", "b", "c"), 1.0e-15);
  }

  @Test
  public void multipleMissingPrefixesApplyBackoffRepeatedly() {
    NGramLanguageModel model = new NGramLanguageModel(3);
    model.add("a", "b", "c");

    assertEquals(0.16 / 6.0, model.calculateProbability("x", "y", "c"), 1.0e-15);
    assertEquals(0.0, model.calculateProbability("x", "y", "z"), 0.0);
  }

  @Test
  public void longSequenceMultipliesOverlappingWindowProbabilities() {
    NGramLanguageModel model = new NGramLanguageModel(2);
    model.add("a", "b", "a", "c");

    assertEquals(0.5, model.calculateProbability("a", "b", "a"), 1.0e-15);
    assertEquals(0.4 / 6.0, model.calculateProbability("x", "b", "a"), 1.0e-15);
  }

  @Test
  public void shorterSequenceIsScoredAsOneGram() {
    NGramLanguageModel model = new NGramLanguageModel(4);
    model.add("a", "b", "c");

    assertEquals(1.0, model.calculateProbability("a", "b"), 0.0);
    assertEquals(0.4 / 6.0, model.calculateProbability("x", "b"), 1.0e-15);
  }

  @Test
  public void absentPrefixCountFallsBackToVocabularySize() {
    NGramLanguageModel model = new NGramLanguageModel(2);
    model.add(new StringList("p", "q"));

    assertEquals(1.0, model.calculateProbability("p", "q"), 0.0);
  }

  @Test
  public void zeroAndNegativeCountsExerciseNumericalBoundaries() {
    NGramLanguageModel zeroModel = new NGramLanguageModel(1);
    zeroModel.add("zero");
    zeroModel.setCount(new StringList("zero"), 0);
    NGramLanguageModel negativeModel = new NGramLanguageModel(1);
    negativeModel.add("negative");
    negativeModel.setCount(new StringList("negative"), -1);

    assertEquals(0.0, zeroModel.calculateProbability("zero"), 0.0);
    assertEquals(1.0, negativeModel.calculateProbability("negative"), 0.0);
  }

  @Test
  public void emptyTokenArrayDependsOnWhetherModelHasEntries() {
    NGramLanguageModel emptyModel = new NGramLanguageModel();
    NGramLanguageModel trainedModel = new NGramLanguageModel();
    trainedModel.add("a");

    assertEquals(0.0, emptyModel.calculateProbability(new String[0]), 0.0);
    assertThrows(IllegalArgumentException.class,
        () -> trainedModel.calculateProbability(new String[0]));
    assertThrows(IllegalArgumentException.class, () -> trainedModel.add(new String[0]));
  }

  @Test
  public void nullTokenArrayIsRejectedByAddAndScoring() {
    NGramLanguageModel model = new NGramLanguageModel(2);
    model.add("seed");

    assertThrows(NullPointerException.class, () -> model.add((String[]) null));
    assertThrows(NullPointerException.class,
        () -> model.calculateProbability((String[]) null));
  }

  @Test
  public void predictionReturnsHighestCountUnigram() {
    NGramLanguageModel model = new NGramLanguageModel(1);
    model.add("cat", "dog", "dog", "dog");

    assertArrayEquals(new String[] {"dog"}, model.predictNextTokens());
    assertArrayEquals(new String[] {"dog"}, model.predictNextTokens("cat"));
  }

  @Test
  public void predictionCanReturnAnEntireStoredNgram() {
    NGramLanguageModel model = new NGramLanguageModel(2);
    model.add("i", "like");

    assertArrayEquals(new String[] {"i", "like"}, model.predictNextTokens());
  }

  @Test
  public void predictionTieKeepsFirstInsertedCandidate() {
    NGramLanguageModel model = new NGramLanguageModel(1);
    model.add("first", "second");

    assertArrayEquals(new String[] {"first"}, model.predictNextTokens());
  }

  @Test
  public void removalImmediatelyChangesScoringAndPrediction() {
    NGramLanguageModel model = new NGramLanguageModel(1);
    model.add("first", "second", "second");
    model.remove(new StringList("second"));

    assertEquals(0.0, model.calculateProbability("second"), 0.0);
    assertEquals(1.0, model.calculateProbability("first"), 0.0);
    assertArrayEquals(new String[] {"first"}, model.predictNextTokens());
  }

  @Test
  public void cutoffChangesVocabularyUsedByLanguageModel() {
    NGramLanguageModel model = new NGramLanguageModel(1);
    model.add("rare", "common", "common", "common");
    model.cutoff(2, Integer.MAX_VALUE);

    assertEquals(1, model.size());
    assertEquals(0.0, model.calculateProbability("rare"), 0.0);
    assertEquals(3.0, model.calculateProbability("common"), 1.0e-15);
    assertArrayEquals(new String[] {"common"}, model.predictNextTokens("rare"));
  }

  @Test
  public void serializedModelRetainsCountsAndSupportsDefaultOrder() throws IOException {
    NGramLanguageModel original = new NGramLanguageModel(3);
    original.add("the", "quick", "fox");
    original.add("the", "quick", "dog");
    ByteArrayOutputStream serialized = new ByteArrayOutputStream();
    original.serialize(serialized);

    NGramLanguageModel loaded =
        new NGramLanguageModel(new ByteArrayInputStream(serialized.toByteArray()));

    assertEquals(original, loaded);
    assertEquals(0.5, loaded.calculateProbability("the", "quick", "fox"), 0.0);
    assertArrayEquals(original.predictNextTokens("the"), loaded.predictNextTokens("the"));
  }

  @Test
  public void serializedModelCanBeLoadedWithDifferentOrder() throws IOException {
    NGramLanguageModel original = new NGramLanguageModel(3);
    original.add("a", "b", "c");
    ByteArrayOutputStream serialized = new ByteArrayOutputStream();
    original.serialize(serialized);

    NGramLanguageModel loaded =
        new NGramLanguageModel(new ByteArrayInputStream(serialized.toByteArray()), 1);

    assertEquals(1.0 / 6.0, loaded.calculateProbability("a"), 1.0e-15);
    assertEquals(1.0 / 36.0, loaded.calculateProbability("a", "b"), 1.0e-15);
  }

  @Test
  public void streamConstructorValidatesOrderAfterReadingValidData() throws IOException {
    NGramLanguageModel original = new NGramLanguageModel(1);
    original.add("token");
    ByteArrayOutputStream serialized = new ByteArrayOutputStream();
    original.serialize(serialized);

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> new NGramLanguageModel(
            new ByteArrayInputStream(serialized.toByteArray()), 0));

    assertEquals("Parameter 'n' must be greater than 0.", exception.getMessage());
  }

  @Test
  public void malformedSerializedInputPropagatesIOException() {
    byte[] malformed = "not a serialized ngram model".getBytes(StandardCharsets.UTF_8);

    assertThrows(IOException.class,
        () -> new NGramLanguageModel(new ByteArrayInputStream(malformed)));
  }

  @Test
  public void caseDistinctTokensRemainDistinctCandidates() {
    NGramLanguageModel model = new NGramLanguageModel(1);
    model.add("Word", "word", "Word");

    assertEquals(2, model.size());
    assertEquals(1.0, model.calculateProbability("Word"), 0.0);
    assertEquals(0.5, model.calculateProbability("word"), 0.0);
    assertTrue(model.calculateProbability("WORD") == 0.0);
    assertArrayEquals(new String[] {"Word"}, model.predictNextTokens());
  }
}
