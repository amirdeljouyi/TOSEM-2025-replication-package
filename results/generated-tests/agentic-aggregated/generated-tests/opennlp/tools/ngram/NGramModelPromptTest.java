package opennlp.tools.ngram;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.NoSuchElementException;

import opennlp.tools.dictionary.Dictionary;
import opennlp.tools.util.StringList;
import org.junit.Test;

public class NGramModelPromptTest {

  @Test
  public void emptyModelHasNoGrams() {
    NGramModel model = new NGramModel();

    assertEquals(0, model.size());
    assertEquals(0, model.numberOfGrams());
    assertFalse(model.iterator().hasNext());
    assertEquals("Size: 0", model.toString());
    assertEquals(0, model.getCount(new StringList("missing")));
    assertFalse(model.contains(new StringList("missing")));
  }

  @Test
  public void addNewAndEquivalentNGramIncrementsCount() {
    NGramModel model = new NGramModel();
    StringList first = new StringList("natural", "language");

    model.add(first);
    model.add(new StringList("natural", "language"));
    model.add(new StringList("other"));

    assertEquals(2, model.size());
    assertEquals(2, model.getCount(first));
    assertEquals(3, model.numberOfGrams());
  }

  @Test
  public void differentlyCasedNGramsRemainDistinct() {
    NGramModel model = new NGramModel();

    model.add(new StringList("Word"));
    model.add(new StringList("word"));

    assertEquals(2, model.size());
    assertEquals(1, model.getCount(new StringList("Word")));
    assertEquals(1, model.getCount(new StringList("word")));
  }

  @Test
  public void setCountReplacesExistingCount() {
    NGramModel model = new NGramModel();
    StringList ngram = new StringList("a", "b");
    model.add(ngram);

    model.setCount(new StringList("a", "b"), 17);

    assertEquals(17, model.getCount(ngram));
    assertEquals(17, model.numberOfGrams());
    assertEquals(1, model.size());
  }

  @Test
  public void setCountAcceptsZeroAndNegativeValues() {
    NGramModel model = new NGramModel();
    StringList zero = new StringList("zero");
    StringList negative = new StringList("negative");
    model.add(zero);
    model.add(negative);

    model.setCount(zero, 0);
    model.setCount(negative, -4);

    assertTrue(model.contains(zero));
    assertTrue(model.contains(negative));
    assertEquals(0, model.getCount(zero));
    assertEquals(-4, model.getCount(negative));
    assertEquals(-4, model.numberOfGrams());
  }

  @Test
  public void setCountOnMissingNGramThrowsAndDoesNotInsert() {
    NGramModel model = new NGramModel();
    StringList missing = new StringList("missing");

    try {
      model.setCount(missing, 3);
      fail("Expected NoSuchElementException");
    } catch (NoSuchElementException expected) {
      assertFalse(model.contains(missing));
      assertEquals(0, model.size());
    }
  }

  @Test
  public void addAfterZeroCountStartsFromOne() {
    NGramModel model = new NGramModel();
    StringList ngram = new StringList("reset");
    model.add(ngram);
    model.setCount(ngram, 0);

    model.add(new StringList("reset"));

    assertEquals(1, model.getCount(ngram));
    assertEquals(1, model.numberOfGrams());
  }

  @Test
  public void removeExistingAndMissingNGramsIsSafe() {
    NGramModel model = new NGramModel();
    StringList kept = new StringList("kept");
    StringList removed = new StringList("removed");
    model.add(kept);
    model.add(removed);
    model.add(removed);

    model.remove(new StringList("removed"));
    model.remove(new StringList("never-added"));

    assertEquals(1, model.size());
    assertTrue(model.contains(kept));
    assertFalse(model.contains(removed));
    assertEquals(1, model.numberOfGrams());
  }

  @Test
  public void tokenRangeCreatesUnigramsAndBigramsWithDuplicateCounts() {
    NGramModel model = new NGramModel();

    model.add(new StringList("a", "b", "a"), 1, 2);

    assertEquals(4, model.size());
    assertEquals(2, model.getCount(new StringList("a")));
    assertEquals(1, model.getCount(new StringList("b")));
    assertEquals(1, model.getCount(new StringList("a", "b")));
    assertEquals(1, model.getCount(new StringList("b", "a")));
    assertEquals(5, model.numberOfGrams());
  }

  @Test
  public void tokenRangeIncludesMaximumLengthAndIgnoresOversizedLengths() {
    NGramModel model = new NGramModel();

    model.add(new StringList("one", "two", "three"), 2, 5);

    assertEquals(3, model.size());
    assertTrue(model.contains(new StringList("one", "two")));
    assertTrue(model.contains(new StringList("two", "three")));
    assertTrue(model.contains(new StringList("one", "two", "three")));
    assertFalse(model.contains(new StringList("one")));
  }

  @Test
  public void tokenRangeLongerThanInputAddsNothing() {
    NGramModel model = new NGramModel();

    model.add(new StringList("short"), 2, 4);

    assertEquals(0, model.size());
    assertEquals(0, model.numberOfGrams());
  }

  @Test
  public void tokenRangeRejectsZeroMinimum() {
    NGramModel model = new NGramModel();

    try {
      model.add(new StringList("a", "b"), 0, 2);
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException expected) {
      assertTrue(expected.getMessage().contains("at least 1"));
      assertEquals(0, model.size());
    }
  }

  @Test
  public void tokenRangeRejectsZeroMaximum() {
    NGramModel model = new NGramModel();

    try {
      model.add(new StringList("a"), 1, 0);
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException expected) {
      assertTrue(expected.getMessage().contains("maxLength= 0"));
      assertEquals(0, model.size());
    }
  }

  @Test
  public void tokenRangeRejectsMinimumGreaterThanMaximum() {
    NGramModel model = new NGramModel();

    try {
      model.add(new StringList("a", "b", "c"), 3, 2);
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException expected) {
      assertTrue(expected.getMessage().contains("must not be larger"));
      assertEquals(0, model.size());
    }
  }

  @Test
  public void characterRangeLowercasesAndCombinesDuplicateGrams() {
    NGramModel model = new NGramModel();

    model.add(new StringBuilder("AaA"), 1, 2);

    assertEquals(2, model.size());
    assertEquals(3, model.getCount(new StringList("a")));
    assertEquals(2, model.getCount(new StringList("aa")));
    assertEquals(5, model.numberOfGrams());
    assertFalse(model.contains(new StringList("A")));
  }

  @Test
  public void characterRangeWithMinimumGreaterThanMaximumAddsNothing() {
    NGramModel model = new NGramModel();

    model.add("text", 4, 2);

    assertEquals(0, model.size());
  }

  @Test
  public void zeroLengthCharacterRangeProducesEmptyAndSingleCharacterTokens() {
    NGramModel model = new NGramModel();

    model.add("Ab", 0, 1);

    assertEquals(3, model.size());
    assertEquals(3, model.getCount(new StringList("")));
    assertEquals(1, model.getCount(new StringList("a")));
    assertEquals(1, model.getCount(new StringList("b")));
    assertEquals(5, model.numberOfGrams());
  }

  @Test
  public void emptyCharacterSequenceAddsNothingForPositiveLengths() {
    NGramModel model = new NGramModel();

    model.add("", 1, 3);

    assertEquals(0, model.size());
  }

  @Test
  public void iteratorPreservesFirstInsertionOrder() {
    NGramModel model = new NGramModel();
    model.add(new StringList("first"));
    model.add(new StringList("second"));
    model.add(new StringList("first"));
    model.add(new StringList("third"));
    Iterator<StringList> iterator = model.iterator();

    assertEquals(new StringList("first"), iterator.next());
    assertEquals(new StringList("second"), iterator.next());
    assertEquals(new StringList("third"), iterator.next());
    assertFalse(iterator.hasNext());
  }

  @Test
  public void iteratorRemoveUpdatesModelAndTotals() {
    NGramModel model = new NGramModel();
    model.add(new StringList("first"));
    model.add(new StringList("second"));
    model.add(new StringList("second"));
    Iterator<StringList> iterator = model.iterator();

    assertEquals(new StringList("first"), iterator.next());
    iterator.remove();

    assertFalse(model.contains(new StringList("first")));
    assertEquals(1, model.size());
    assertEquals(2, model.numberOfGrams());
  }

  @Test
  public void exhaustedIteratorThrowsNoSuchElementException() {
    NGramModel model = new NGramModel();
    Iterator<StringList> iterator = model.iterator();

    try {
      iterator.next();
      fail("Expected NoSuchElementException");
    } catch (NoSuchElementException expected) {
      assertFalse(iterator.hasNext());
    }
  }

  @Test
  public void cutoffRetainsCountsExactlyOnBothBoundaries() {
    NGramModel model = new NGramModel();
    StringList low = new StringList("low");
    StringList middle = new StringList("middle");
    StringList high = new StringList("high");
    model.add(low);
    model.add(middle);
    model.add(high);
    model.setCount(low, 2);
    model.setCount(middle, 3);
    model.setCount(high, 4);

    model.cutoff(2, 4);

    assertEquals(3, model.size());
    assertEquals(9, model.numberOfGrams());
  }

  @Test
  public void cutoffRemovesCountsOutsideRange() {
    NGramModel model = new NGramModel();
    StringList tooLow = new StringList("too-low");
    StringList retained = new StringList("retained");
    StringList tooHigh = new StringList("too-high");
    model.add(tooLow);
    model.add(retained);
    model.add(tooHigh);
    model.setCount(tooLow, 1);
    model.setCount(retained, 3);
    model.setCount(tooHigh, 5);

    model.cutoff(2, 4);

    assertFalse(model.contains(tooLow));
    assertTrue(model.contains(retained));
    assertFalse(model.contains(tooHigh));
    assertEquals(1, model.size());
  }

  @Test
  public void cutoffDefaultLimitsLeavesModelUntouched() {
    NGramModel model = new NGramModel();
    StringList negative = new StringList("negative");
    StringList ordinary = new StringList("ordinary");
    model.add(negative);
    model.add(ordinary);
    model.setCount(negative, -2);

    model.cutoff(0, Integer.MAX_VALUE);

    assertEquals(2, model.size());
    assertEquals(-1, model.numberOfGrams());
  }

  @Test
  public void reversedCutoffBoundsRemoveEveryEntry() {
    NGramModel model = new NGramModel();
    model.add(new StringList("one"));
    model.add(new StringList("two"));
    model.setCount(new StringList("two"), 10);

    model.cutoff(5, 2);

    assertEquals(0, model.size());
  }

  @Test
  public void defaultDictionaryMergesEntriesDifferingOnlyByCase() {
    NGramModel model = new NGramModel();
    model.add(new StringList("Open", "NLP"));
    model.add(new StringList("open", "nlp"));

    Dictionary dictionary = model.toDictionary();

    assertEquals(1, dictionary.size());
    assertTrue(dictionary.contains(new StringList("OPEN", "NLP")));
    assertEquals(2, dictionary.getMinTokenCount());
    assertEquals(2, dictionary.getMaxTokenCount());
  }

  @Test
  public void caseSensitiveDictionaryRetainsCaseVariants() {
    NGramModel model = new NGramModel();
    model.add(new StringList("Token"));
    model.add(new StringList("token"));

    Dictionary dictionary = model.toDictionary(true);

    assertEquals(2, dictionary.size());
    assertTrue(dictionary.contains(new StringList("Token")));
    assertTrue(dictionary.contains(new StringList("token")));
    assertFalse(dictionary.contains(new StringList("TOKEN")));
  }

  @Test
  public void dictionaryIsAnIndependentSnapshot() {
    NGramModel model = new NGramModel();
    StringList original = new StringList("original");
    model.add(original);
    Dictionary dictionary = model.toDictionary(true);

    model.remove(original);
    model.add(new StringList("later"));

    assertTrue(dictionary.contains(original));
    assertFalse(dictionary.contains(new StringList("later")));
    assertEquals(1, dictionary.size());
  }

  @Test
  public void serializationRoundTripPreservesTokensCountsAndOrder() throws IOException {
    NGramModel original = new NGramModel();
    original.add(new StringList("first"));
    original.add(new StringList("two", "tokens"));
    original.setCount(new StringList("first"), 7);
    original.setCount(new StringList("two", "tokens"), -2);
    ByteArrayOutputStream output = new ByteArrayOutputStream();

    original.serialize(output);
    NGramModel restored = new NGramModel(new ByteArrayInputStream(output.toByteArray()));
    Iterator<StringList> iterator = restored.iterator();

    assertEquals(original, restored);
    assertEquals(7, restored.getCount(new StringList("first")));
    assertEquals(-2, restored.getCount(new StringList("two", "tokens")));
    assertEquals(new StringList("first"), iterator.next());
    assertEquals(new StringList("two", "tokens"), iterator.next());
    assertFalse(iterator.hasNext());
  }

  @Test
  public void serializedXmlEscapesTokensAndOutputRemainsUsable() throws IOException {
    NGramModel model = new NGramModel();
    model.add(new StringList("<&>"));
    ByteArrayOutputStream output = new ByteArrayOutputStream();

    model.serialize(output);
    output.write(' ');
    byte[] bytes = output.toByteArray();
    NGramModel restored = new NGramModel(
        new ByteArrayInputStream(new String(bytes, StandardCharsets.UTF_8).trim()
            .getBytes(StandardCharsets.UTF_8)));

    assertTrue(restored.contains(new StringList("<&>")));
    assertEquals(1, restored.getCount(new StringList("<&>")));
  }

  @Test
  public void constructorRejectsEntryWithoutCountAttribute() {
    String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
        + "<dictionary case_sensitive=\"false\"><entry><token>x</token></entry></dictionary>";

    try {
      new NGramModel(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
      fail("Expected IOException");
    } catch (IOException expected) {
      assertTrue(expected.getMessage().contains("invalid format"));
    }
  }

  @Test
  public void constructorRejectsNonNumericCountAttribute() {
    String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
        + "<dictionary case_sensitive=\"false\"><entry count=\"many\">"
        + "<token>x</token></entry></dictionary>";

    try {
      new NGramModel(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
      fail("Expected IOException");
    } catch (IOException expected) {
      assertTrue(expected.getMessage().contains("invalid format"));
    }
  }

  @Test
  public void constructorRejectsMalformedXml() {
    String xml = "<dictionary><entry count=\"1\"><token>broken</entry></dictionary>";

    try {
      new NGramModel(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
      fail("Expected IOException");
    } catch (IOException expected) {
      assertTrue(expected.getMessage().contains("invalid format"));
    }
  }

  @Test
  public void equalityAndHashCodeDependOnNGramsAndCountsNotInsertionOrder() {
    NGramModel left = new NGramModel();
    NGramModel right = new NGramModel();
    left.add(new StringList("a"));
    left.add(new StringList("b"));
    left.setCount(new StringList("b"), 4);
    right.add(new StringList("b"));
    right.add(new StringList("a"));
    right.setCount(new StringList("b"), 4);

    assertEquals(left, right);
    assertEquals(left.hashCode(), right.hashCode());
    assertEquals(left, left);
    assertNotEquals(left, new Object());
    assertNotEquals(left, null);

    right.setCount(new StringList("b"), 5);
    assertNotEquals(left, right);
  }

  @Test
  public void toStringTracksSizeAcrossOperations() {
    NGramModel model = new NGramModel();
    model.add(new StringList("same"));
    model.add(new StringList("same"));
    model.add(new StringList("different"));

    assertEquals("Size: 2", model.toString());

    model.remove(new StringList("same"));

    assertEquals("Size: 1", model.toString());
  }

  @Test
  public void generatedNGramsSurviveDictionaryAndSerializationWorkflow() throws IOException {
    NGramModel generated = new NGramModel();
    generated.add(new StringList("New", "York", "New"), 1, 2);
    generated.add("NLP", 2, 3);
    generated.cutoff(1, 2);
    Dictionary dictionary = generated.toDictionary(false);
    ByteArrayOutputStream output = new ByteArrayOutputStream();

    generated.serialize(output);
    NGramModel restored = new NGramModel(new ByteArrayInputStream(output.toByteArray()));

    assertEquals(generated, restored);
    assertEquals(generated.size(), dictionary.size());
    assertTrue(dictionary.contains(new StringList("new")));
    assertTrue(dictionary.contains(new StringList("New", "York")));
    assertTrue(restored.contains(new StringList("nl")));
    assertTrue(restored.contains(new StringList("nlp")));
    assertEquals(8, restored.numberOfGrams());
  }
}
