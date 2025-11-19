package opennlp.tools.ngram;

import opennlp.tools.dictionary.Dictionary;
import opennlp.tools.dictionary.serializer.Attributes;
import opennlp.tools.dictionary.serializer.DictionaryEntryPersistor;
import opennlp.tools.dictionary.serializer.Entry;
import opennlp.tools.ml.model.*;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.StringList;
import opennlp.tools.util.TrainingParameters;
import org.junit.jupiter.api.Test;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NGramModel_5_GPTLLMTest {

@Test
public void testAddAndContainsSingleToken() {
NGramModel model = new NGramModel();
StringList unigram = new StringList("hello");
model.add(unigram);
assertTrue(model.contains(unigram));
assertEquals(1, model.getCount(unigram));
}

@Test
public void testAddDuplicateIncrementsCount() {
NGramModel model = new NGramModel();
StringList unigram = new StringList("hello");
model.add(unigram);
model.add(unigram);
assertEquals(2, model.getCount(unigram));
}

@Test
public void testAddRangeGrams() {
NGramModel model = new NGramModel();
StringList tokens = new StringList("open", "nlp", "rocks");
model.add(tokens, 1, 2);
assertTrue(model.contains(new StringList("open")));
assertTrue(model.contains(new StringList("nlp")));
assertTrue(model.contains(new StringList("rocks")));
assertTrue(model.contains(new StringList("open", "nlp")));
assertTrue(model.contains(new StringList("nlp", "rocks")));
}

@Test
public void testAddRangeIllegalMinZero() {
NGramModel model = new NGramModel();
model.add(new StringList("a", "b", "c"), 0, 2);
}

@Test
public void testAddRangeIllegalMinGreaterThanMax() {
NGramModel model = new NGramModel();
model.add(new StringList("a", "b"), 3, 2);
}

@Test
public void testAddCharacterLevelNGrams() {
NGramModel model = new NGramModel();
CharSequence input = "abc";
model.add(input, 1, 2);
assertTrue(model.contains(new StringList("a")));
assertTrue(model.contains(new StringList("b")));
assertTrue(model.contains(new StringList("c")));
assertTrue(model.contains(new StringList("ab")));
assertTrue(model.contains(new StringList("bc")));
}

@Test
public void testRemoveNGram() {
NGramModel model = new NGramModel();
StringList bigram = new StringList("hello", "world");
model.add(bigram);
assertTrue(model.contains(bigram));
model.remove(bigram);
assertFalse(model.contains(bigram));
assertEquals(0, model.getCount(bigram));
}

@Test
public void testSizeAndNumberOfGrams() {
NGramModel model = new NGramModel();
model.add(new StringList("hi"));
model.add(new StringList("hi"));
model.add(new StringList("there"));
assertEquals(2, model.size());
assertEquals(3, model.numberOfGrams());
}

@Test
public void testSetCountOnNonExistentGramThrows() {
NGramModel model = new NGramModel();
model.setCount(new StringList("this", "is", "missing"), 1);
}

@Test
public void testCutoffUnderThreshold() {
NGramModel model = new NGramModel();
model.add(new StringList("one"));
model.add(new StringList("two"));
model.add(new StringList("two"));
model.add(new StringList("three"));
model.add(new StringList("three"));
model.add(new StringList("three"));
model.cutoff(2, Integer.MAX_VALUE);
assertFalse(model.contains(new StringList("one")));
assertTrue(model.contains(new StringList("two")));
assertTrue(model.contains(new StringList("three")));
}

@Test
public void testCutoffOverThreshold() {
NGramModel model = new NGramModel();
model.add(new StringList("one"));
model.add(new StringList("one"));
model.add(new StringList("one"));
model.add(new StringList("two"));
model.cutoff(0, 2);
assertFalse(model.contains(new StringList("one")));
assertTrue(model.contains(new StringList("two")));
}

@Test
public void testToDictionaryCaseSensitivity() {
NGramModel model = new NGramModel();
model.add(new StringList("Hello"));
model.add(new StringList("hello"));
Dictionary dictCaseInsensitive = model.toDictionary(false);
Dictionary dictCaseSensitive = model.toDictionary(true);
assertTrue(dictCaseInsensitive.contains(new StringList("hello")) || dictCaseInsensitive.contains(new StringList("Hello")));
assertEquals(1, dictCaseInsensitive.size());
assertEquals(2, dictCaseSensitive.size());
}

@Test
public void testEqualsAndHashCode() {
NGramModel model1 = new NGramModel();
NGramModel model2 = new NGramModel();
StringList tokens = new StringList("compare", "these");
model1.add(tokens);
model2.add(tokens);
assertEquals(model1, model2);
assertEquals(model1.hashCode(), model2.hashCode());
}

@Test
public void testEmptyModelEquals() {
NGramModel model1 = new NGramModel();
NGramModel model2 = new NGramModel();
assertEquals(model1, model2);
}

@Test
public void testToStringPrintsSize() {
NGramModel model = new NGramModel();
model.add(new StringList("test"));
assertTrue(model.toString().startsWith("Size:"));
}

@Test
public void testIteratorFunctionality() {
NGramModel model = new NGramModel();
model.add(new StringList("word1"));
model.add(new StringList("word2"));
Iterator<StringList> it = model.iterator();
assertTrue(it.hasNext());
StringList first = it.next();
assertNotNull(first);
boolean secondExists = it.hasNext();
boolean firstEqualsSecond = it.hasNext() && first.equals(it.next());
assertTrue(secondExists);
assertFalse(firstEqualsSecond);
}

@Test
public void testSerializationAndDeserialization() throws IOException {
NGramModel original = new NGramModel();
original.add(new StringList("serialize"));
original.add(new StringList("ngram"));
ByteArrayOutputStream out = new ByteArrayOutputStream();
original.serialize(out);
ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
NGramModel deserialized = new NGramModel(in);
assertEquals(original, deserialized);
assertEquals(original.getCount(new StringList("serialize")), deserialized.getCount(new StringList("serialize")));
}

@Test
public void testDeserializationWithInvalidCount() throws IOException {
ByteArrayOutputStream out = new ByteArrayOutputStream();
Entry badEntry = new Entry(new StringList("bad"), new Attributes());
// badEntry.getAttributes().setValue("count", "notANumber");
List<Entry> entries = new ArrayList<>();
entries.add(badEntry);
DictionaryEntryPersistor.serialize(out, entries.iterator(), false);
ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
new NGramModel(in);
}

@Test
public void testDeserializationMissingCount() throws IOException {
ByteArrayOutputStream out = new ByteArrayOutputStream();
Entry entry = new Entry(new StringList("noCount"), new Attributes());
List<Entry> entries = new ArrayList<>();
entries.add(entry);
DictionaryEntryPersistor.serialize(out, entries.iterator(), false);
ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
new NGramModel(in);
}

@Test
public void testToDictionaryDefault() {
NGramModel model = new NGramModel();
model.add(new StringList("default"));
Dictionary dict = model.toDictionary();
assertTrue(dict.contains(new StringList("default")));
}

@Test
public void testMultipleAddWithGetCount() {
NGramModel model = new NGramModel();
model.add(new StringList("repeat"));
model.add(new StringList("repeat"));
model.add(new StringList("repeat"));
assertEquals(3, model.getCount(new StringList("repeat")));
}

@Test
public void testHashCodeConsistency() {
NGramModel model = new NGramModel();
model.add(new StringList("hash"));
int hashBefore = model.hashCode();
int hashAfter = model.hashCode();
assertEquals(hashBefore, hashAfter);
}

@Test
public void testGetCountForInvalidNGramReturnsZero() {
NGramModel model = new NGramModel();
StringList missing = new StringList("not", "present");
assertEquals(0, model.getCount(missing));
}

@Test
public void testEmptyModelHasZeroSizeAndZeroGrams() {
NGramModel model = new NGramModel();
assertEquals(0, model.size());
assertEquals(0, model.numberOfGrams());
}

@Test
public void testSerializeInternalEntryIteratorRemoveThrows() throws IOException {
NGramModel model = new NGramModel();
ByteArrayOutputStream out = new ByteArrayOutputStream();
model.serialize(out);
ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
final NGramModel loaded = new NGramModel(in);
Iterator<Entry> entryIterator = new Iterator<Entry>() {

Iterator<StringList> internal = loaded.iterator();

public boolean hasNext() {
return internal.hasNext();
}

public Entry next() {
StringList slist = internal.next();
Attributes attrs = new Attributes();
attrs.setValue("count", "1");
return new Entry(slist, attrs);
}

public void remove() {
throw new UnsupportedOperationException();
}
};
entryIterator.remove();
}

@Test
public void testContainsWithExactCase() {
NGramModel model = new NGramModel();
StringList term = new StringList("Test");
model.add(term);
assertTrue(model.contains(new StringList("Test")));
assertFalse(model.contains(new StringList("test")));
}

@Test
public void testCutoffWithEqualToThresholdValue() {
NGramModel model = new NGramModel();
model.add(new StringList("one"));
model.add(new StringList("one"));
model.cutoff(2, 2);
assertTrue(model.contains(new StringList("one")));
}

@Test
public void testCutoffRemovesAllWhenOutOfRange() {
NGramModel model = new NGramModel();
model.add(new StringList("alpha"));
model.add(new StringList("beta"));
model.add(new StringList("beta"));
model.cutoff(3, 1);
assertEquals(0, model.size());
}

@Test
public void testToDictionaryAfterCutoffMaintainsRemainingNGrams() {
NGramModel model = new NGramModel();
model.add(new StringList("x"));
model.add(new StringList("y"));
model.add(new StringList("y"));
model.cutoff(2, 2);
Dictionary dict = model.toDictionary();
assertTrue(dict.contains(new StringList("y")));
assertFalse(dict.contains(new StringList("x")));
}

@Test
public void testAddCharSequenceBoundaryLength() {
NGramModel model = new NGramModel();
model.add("xyz", 3, 3);
assertTrue(model.contains(new StringList("xyz")));
assertEquals(1, model.size());
}

@Test
public void testAddCharSequenceLengthExceedsInput() {
NGramModel model = new NGramModel();
model.add("ab", 3, 5);
assertEquals(0, model.size());
}

@Test
public void testAddCharSequenceWithMinLengthZero() {
NGramModel model = new NGramModel();
model.add("invalid", 0, 3);
}

@Test
public void testAddCharSequenceWithMinGreaterThanMax() {
NGramModel model = new NGramModel();
model.add("invalid", 4, 3);
}

@Test
public void testMultipleAddMixingCharAndTokenNGrams() {
NGramModel model = new NGramModel();
model.add("test", 1, 2);
model.add(new StringList("test"));
assertTrue(model.contains(new StringList("t")));
assertTrue(model.contains(new StringList("te")));
assertTrue(model.contains(new StringList("test")));
assertEquals(1, model.getCount(new StringList("test")));
}

@Test
public void testToDictionaryPreservesNGramCountThroughRoundTrip() throws IOException {
NGramModel model = new NGramModel();
model.add(new StringList("persist"));
model.add(new StringList("persist"));
ByteArrayOutputStream out = new ByteArrayOutputStream();
model.serialize(out);
ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
NGramModel restored = new NGramModel(in);
assertEquals(2, restored.getCount(new StringList("persist")));
}

@Test
public void testEqualsReturnsFalseForDifferentContent() {
NGramModel model1 = new NGramModel();
NGramModel model2 = new NGramModel();
model1.add(new StringList("word1"));
model2.add(new StringList("word2"));
assertFalse(model1.equals(model2));
}

@Test
public void testEqualsWithDifferentObjectTypes() {
NGramModel model = new NGramModel();
assertFalse(model.equals("not an NGramModel"));
}

@Test
public void testEqualsWithNull() {
NGramModel model = new NGramModel();
assertFalse(model.equals(null));
}

@Test
public void testAddEmptyStringList() {
NGramModel model = new NGramModel();
StringList emptyTokens = new StringList(new String[] {});
model.add(emptyTokens);
assertTrue(model.contains(emptyTokens));
assertEquals(1, model.getCount(emptyTokens));
}

@Test
public void testAddStringListWithEmptyToken() {
NGramModel model = new NGramModel();
StringList withEmptyToken = new StringList("");
model.add(withEmptyToken);
assertTrue(model.contains(withEmptyToken));
assertEquals(1, model.getCount(withEmptyToken));
}

@Test
public void testAddTokenGramsWithRepeatedTokens() {
NGramModel model = new NGramModel();
StringList tokens = new StringList("repeat", "repeat", "repeat");
model.add(tokens, 1, 2);
assertTrue(model.contains(new StringList("repeat")));
assertTrue(model.contains(new StringList("repeat", "repeat")));
}

@Test
public void testEmptyCharSequenceInputAddsNoGrams() {
NGramModel model = new NGramModel();
model.add("", 1, 3);
assertEquals(0, model.size());
}

@Test
public void testCharSequenceLowerCasing() {
NGramModel model = new NGramModel();
model.add("aAbB", 1, 2);
assertTrue(model.contains(new StringList("a")));
assertTrue(model.contains(new StringList("aa")));
assertTrue(model.contains(new StringList("ab")));
assertTrue(model.contains(new StringList("bb")));
assertFalse(model.contains(new StringList("AA")));
}

@Test
public void testAddStringListWhereTokensMatchButObjectDiffers() {
NGramModel model = new NGramModel();
StringList a = new StringList("token");
StringList b = new StringList("token");
model.add(a);
assertTrue(model.contains(b));
assertEquals(1, model.getCount(b));
assertEquals(a, b);
}

@Test
public void testAddCharSequenceMinEqualsMaxAndSingleCharInput() {
NGramModel model = new NGramModel();
model.add("x", 1, 1);
assertTrue(model.contains(new StringList("x")));
assertEquals(1, model.size());
}

@Test
public void testAddCharSequenceWithWhitespaceOnly() {
NGramModel model = new NGramModel();
model.add("   ", 1, 2);
assertTrue(model.contains(new StringList(" ")));
assertTrue(model.contains(new StringList("  ")));
}

@Test
public void testSerializeAndDeserializeEmptyModel() throws IOException {
NGramModel model = new NGramModel();
ByteArrayOutputStream out = new ByteArrayOutputStream();
model.serialize(out);
ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
NGramModel loaded = new NGramModel(in);
assertEquals(0, loaded.size());
}

@Test
public void testToDictionaryCaseSensitiveRetainsExactInputs() {
NGramModel model = new NGramModel();
model.add(new StringList("Case"));
model.add(new StringList("case"));
assertTrue(model.toDictionary(true).contains(new StringList("Case")));
assertTrue(model.toDictionary(true).contains(new StringList("case")));
}

@Test
public void testToDictionaryCaseInsensitiveMergesInputs() {
NGramModel model = new NGramModel();
model.add(new StringList("UPPER"));
model.add(new StringList("upper"));
assertEquals(1, model.toDictionary(false).size());
}

@Test
public void testDeserializeWithEmptyAttributesThrows() throws IOException {
Entry entry = new Entry(new StringList("data"), new Attributes());
ByteArrayOutputStream out = new ByteArrayOutputStream();
DictionaryEntryPersistor.serialize(out, Collections.singleton(entry).iterator(), false);
ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
new NGramModel(in);
}

@Test
public void testDeserializeCountNullStringThrowsWithBrokenStream() throws IOException {
Attributes attributes = new Attributes();
attributes.setValue("count", null);
Entry entry = new Entry(new StringList("x"), attributes);
ByteArrayOutputStream out = new ByteArrayOutputStream();
DictionaryEntryPersistor.serialize(out, Collections.singleton(entry).iterator(), false);
ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
new NGramModel(in);
}

@Test
public void testEqualsSameReference() {
NGramModel model = new NGramModel();
assertTrue(model.equals(model));
}

@Test
public void testEqualsDifferentClassReturnsFalse() {
NGramModel model = new NGramModel();
assertFalse(model.equals("Not a model"));
}

@Test
public void testToStringOnEmptyModel() {
NGramModel model = new NGramModel();
String result = model.toString();
assertTrue(result.startsWith("Size: 0"));
}

@Test
public void testSetCountOnExistingKeyReplacesValue() {
NGramModel model = new NGramModel();
StringList key = new StringList("replace");
model.add(key);
assertEquals(1, model.getCount(key));
model.setCount(key, 5);
assertEquals(5, model.getCount(key));
}

@Test
public void testAddOverlappingTokensInSlidingWindow() {
NGramModel model = new NGramModel();
StringList tokens = new StringList("a", "b", "c");
model.add(tokens, 1, 3);
assertTrue(model.contains(new StringList("a")));
assertTrue(model.contains(new StringList("a", "b")));
assertTrue(model.contains(new StringList("a", "b", "c")));
}

@Test
public void testAddCharSequenceAllLengthsFrom1ToN() {
NGramModel model = new NGramModel();
model.add("abcd", 1, 4);
assertTrue(model.contains(new StringList("a")));
assertTrue(model.contains(new StringList("ab")));
assertTrue(model.contains(new StringList("abc")));
assertTrue(model.contains(new StringList("abcd")));
}

@Test
public void testCutoffKeepsOnlyExactMatches() {
NGramModel model = new NGramModel();
model.add(new StringList("test"));
model.add(new StringList("test"));
model.add(new StringList("keep"));
model.add(new StringList("keep"));
model.setCount(new StringList("test"), 2);
model.setCount(new StringList("keep"), 2);
model.cutoff(2, 2);
assertTrue(model.contains(new StringList("test")));
assertTrue(model.contains(new StringList("keep")));
assertEquals(2, model.size());
}

@Test
public void testAddWithSingleTokenMultipleTimes_CustomCountVerified() {
NGramModel model = new NGramModel();
StringList token = new StringList("repeat");
model.add(token);
model.add(token);
model.add(token);
assertEquals(3, model.getCount(token));
}

@Test
public void testAddWithTokensOfVaryingLengths_MinEqualsMax() {
NGramModel model = new NGramModel();
StringList tokens = new StringList("a", "b", "c", "d");
model.add(tokens, 2, 2);
assertTrue(model.contains(new StringList("a", "b")));
assertTrue(model.contains(new StringList("b", "c")));
assertTrue(model.contains(new StringList("c", "d")));
assertFalse(model.contains(new StringList("a")));
}

@Test
public void testAddCharSequenceMultipleTimesCountsAccurately() {
NGramModel model = new NGramModel();
model.add("abc", 1, 1);
model.add("abc", 1, 1);
assertEquals(2, model.getCount(new StringList("a")));
assertEquals(2, model.getCount(new StringList("b")));
assertEquals(2, model.getCount(new StringList("c")));
}

@Test
public void testCutoffWithNoMatchingThresholdRemovesAll() {
NGramModel model = new NGramModel();
model.add(new StringList("low"));
model.add(new StringList("low"));
model.cutoff(5, 10);
assertEquals(0, model.size());
}

@Test
public void testCutoffWithNegativeUnderAndMaxOver_PreservesAll() {
NGramModel model = new NGramModel();
model.add(new StringList("a"));
model.add(new StringList("a"));
model.cutoff(-1, Integer.MAX_VALUE);
assertEquals(1, model.size());
assertTrue(model.contains(new StringList("a")));
}

@Test
public void testEqualsReturnsFalseForSameTokensDifferentCounts() {
NGramModel model1 = new NGramModel();
NGramModel model2 = new NGramModel();
StringList token = new StringList("token");
model1.add(token);
model2.add(token);
model2.add(token);
assertFalse(model1.equals(model2));
}

@Test
public void testEqualsReturnsTrueForEqualContentDifferentObjectRefs() {
NGramModel model1 = new NGramModel();
NGramModel model2 = new NGramModel();
StringList a = new StringList("alpha");
StringList b = new StringList("alpha");
model1.add(a);
model2.add(b);
assertEquals(model1, model2);
}

@Test
public void testHashCodeConsistentOverTime() {
NGramModel model = new NGramModel();
model.add(new StringList("stable"));
int hash1 = model.hashCode();
int hash2 = model.hashCode();
assertEquals(hash1, hash2);
}

@Test
public void testSerializeDeserializePreservesSizeAndCounts() throws IOException {
NGramModel model = new NGramModel();
model.add(new StringList("one"));
model.add(new StringList("one"));
model.add(new StringList("two"));
ByteArrayOutputStream out = new ByteArrayOutputStream();
model.serialize(out);
ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
NGramModel restored = new NGramModel(in);
assertEquals(2, restored.size());
assertEquals(2, restored.getCount(new StringList("one")));
assertEquals(1, restored.getCount(new StringList("two")));
}

@Test
public void testCharNGramsPreserveCanonicalLowercase() {
NGramModel model = new NGramModel();
model.add("ABC", 1, 1);
model.add("abc", 1, 1);
assertEquals(2, model.getCount(new StringList("a")));
assertEquals(2, model.getCount(new StringList("b")));
assertEquals(2, model.getCount(new StringList("c")));
}

@Test
public void testToDictionaryPreservesSizeAfterMixedInputs() {
NGramModel model = new NGramModel();
model.add(new StringList("Apple"));
model.add(new StringList("apple"));
int dictSizeCaseInsensitive = model.toDictionary(false).size();
int dictSizeCaseSensitive = model.toDictionary(true).size();
assertEquals(1, dictSizeCaseInsensitive);
assertEquals(2, dictSizeCaseSensitive);
}

@Test
public void testEmptyStringListAddThenRemove() {
NGramModel model = new NGramModel();
StringList empty = new StringList(new String[] {});
model.add(empty);
assertTrue(model.contains(empty));
model.remove(empty);
assertFalse(model.contains(empty));
}

@Test
public void testRemovingNonExistentEntryDoesNotFail() {
NGramModel model = new NGramModel();
StringList notExists = new StringList("none");
model.remove(notExists);
assertFalse(model.contains(notExists));
}

@Test
public void testAddSameCharSequenceDifferentLengths() {
NGramModel model = new NGramModel();
model.add("hello", 1, 2);
assertEquals(1, model.getCount(new StringList("h")));
assertEquals(1, model.getCount(new StringList("he")));
model.add("hello", 1, 1);
assertEquals(2, model.getCount(new StringList("h")));
}

@Test
public void testIteratorExhaustionBehavior() {
NGramModel model = new NGramModel();
model.add(new StringList("sample"));
StringList first = model.iterator().next();
assertNotNull(first);
assertFalse(model.iterator().hasNext());
}

@Test
public void testCallingSetCountWithoutAddThrows() {
NGramModel model = new NGramModel();
model.setCount(new StringList("noadd"), 1);
}

@Test
public void testCannotParseInvalidNumberManually() {
Integer.parseInt("NaN");
}

@Test
public void testMultipleDistinctEntriesWithEqualCountsDoNotMerge() {
NGramModel model = new NGramModel();
model.add(new StringList("a"));
model.add(new StringList("b"));
model.setCount(new StringList("a"), 2);
model.setCount(new StringList("b"), 2);
assertEquals(2, model.size());
assertEquals(4, model.numberOfGrams());
}

@Test
public void testAddCharSequenceWithSurroundingWhitespace() {
NGramModel model = new NGramModel();
model.add(" x ", 1, 1);
assertTrue(model.contains(new StringList(" ")));
assertTrue(model.contains(new StringList("x")));
}

@Test
public void testToStringIncludesCorrectSizeAfterAdditions() {
NGramModel model = new NGramModel();
model.add(new StringList("one"));
model.add(new StringList("two"));
String output = model.toString();
assertTrue(output.contains("Size: 2"));
}

@Test
public void testAddIdenticalStringListInstances_tracksSameEntry() {
NGramModel model = new NGramModel();
StringList list = new StringList("a", "b");
model.add(list);
int countAfterFirst = model.getCount(new StringList("a", "b"));
model.add(list);
int countAfterSecond = model.getCount(new StringList("a", "b"));
assertEquals(1, countAfterFirst);
assertEquals(2, countAfterSecond);
}

@Test
public void testSetCountWithZeroRemovesExistingEntry() {
NGramModel model = new NGramModel();
StringList ngram = new StringList("z");
model.add(ngram);
assertEquals(1, model.getCount(ngram));
model.setCount(ngram, 0);
assertEquals(0, model.getCount(ngram));
assertFalse(model.contains(ngram));
}

@Test
public void testAddTokenRangeThatCreatesNoNGrams() {
NGramModel model = new NGramModel();
StringList tokens = new StringList("only");
model.add(tokens, 2, 4);
assertEquals(0, model.size());
assertEquals(0, model.numberOfGrams());
}

@Test
public void testCutoffWithEqualUnderAndOverRemovesMismatchedCounts() {
NGramModel model = new NGramModel();
StringList a = new StringList("a");
StringList b = new StringList("b");
model.add(a);
model.add(a);
model.add(b);
model.add(b);
model.add(b);
model.cutoff(2, 2);
assertTrue(model.contains(a));
assertFalse(model.contains(b));
}

@Test
public void testCutoffWithExactMaxValueDoesNotRemoveValid() {
NGramModel model = new NGramModel();
StringList t = new StringList("max");
for (int i = 0; i < Integer.MAX_VALUE - 1; i++) {
model.add(t);
}
model.cutoff(1, Integer.MAX_VALUE);
assertTrue(model.contains(t));
}

@Test
public void testEmptyStringListToDictionary() {
NGramModel model = new NGramModel();
StringList empty = new StringList();
model.add(empty);
assertTrue(model.toDictionary(true).contains(empty));
}

@Test
public void testToDictionaryCaseInsensitiveCollapsesCaseVariants() {
NGramModel model = new NGramModel();
model.add(new StringList("Case"));
model.add(new StringList("case"));
assertEquals(1, model.toDictionary(false).size());
}

@Test
public void testToDictionaryCaseSensitivePreservesDuplicates() {
NGramModel model = new NGramModel();
model.add(new StringList("Test"));
model.add(new StringList("test"));
assertEquals(2, model.toDictionary(true).size());
}

@Test
public void testEqualsWithDifferentInstanceSameTokensDifferentCount() {
NGramModel model1 = new NGramModel();
NGramModel model2 = new NGramModel();
StringList list1 = new StringList("one");
StringList list2 = new StringList("one");
model1.add(list1);
model2.add(list2);
model2.add(list2);
assertFalse(model1.equals(model2));
}

@Test
public void testHashCodeDiffersWithDifferentTokenCounts() {
NGramModel model1 = new NGramModel();
NGramModel model2 = new NGramModel();
model1.add(new StringList("hash"));
model2.add(new StringList("hash"));
model2.add(new StringList("hash"));
int hash1 = model1.hashCode();
int hash2 = model2.hashCode();
assertNotEquals(hash1, hash2);
}

@Test
public void testSerializationRoundTripWithMultipleNGrams() throws Exception {
NGramModel model = new NGramModel();
model.add(new StringList("a"));
model.add(new StringList("b"));
model.add(new StringList("b"));
model.add(new StringList("c"));
model.add(new StringList("c"));
model.add(new StringList("c"));
ByteArrayOutputStream out = new ByteArrayOutputStream();
model.serialize(out);
ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
NGramModel deserialized = new NGramModel(in);
assertEquals(model.size(), deserialized.size());
assertEquals(model.numberOfGrams(), deserialized.numberOfGrams());
assertEquals(model.getCount(new StringList("a")), deserialized.getCount(new StringList("a")));
assertEquals(model.getCount(new StringList("b")), deserialized.getCount(new StringList("b")));
assertEquals(model.getCount(new StringList("c")), deserialized.getCount(new StringList("c")));
}

@Test
public void testToDictionaryEmptyModel() {
NGramModel model = new NGramModel();
assertEquals(0, model.toDictionary().size());
}

@Test
public void testCharNGramsGeneratedWithLengthRange1to1() {
NGramModel model = new NGramModel();
model.add("ab", 1, 1);
assertTrue(model.contains(new StringList("a")));
assertTrue(model.contains(new StringList("b")));
assertFalse(model.contains(new StringList("ab")));
}

@Test
public void testCharNGramsGeneratedWithLengthRange2to2() {
NGramModel model = new NGramModel();
model.add("abc", 2, 2);
assertTrue(model.contains(new StringList("ab")));
assertTrue(model.contains(new StringList("bc")));
assertFalse(model.contains(new StringList("a")));
}

@Test
public void testAddThenCutoffThenAddAgain() {
NGramModel model = new NGramModel();
StringList gram = new StringList("reset");
model.add(gram);
model.cutoff(2, Integer.MAX_VALUE);
assertEquals(0, model.getCount(gram));
assertFalse(model.contains(gram));
model.add(gram);
assertEquals(1, model.getCount(gram));
assertTrue(model.contains(gram));
}

@Test
public void testMultipleCaseNGramsPreserveInCaseSensitiveDictionary() {
NGramModel model = new NGramModel();
model.add(new StringList("X"));
model.add(new StringList("x"));
model.add(new StringList("X"));
model.add(new StringList("x"));
assertEquals(2, model.toDictionary(true).size());
assertEquals(1, model.toDictionary(false).size());
}

@Test
public void testSerializeEntryIteratorRemoveThrowsException() throws IOException {
NGramModel model = new NGramModel();
model.add(new StringList("unremovable"));
final ByteArrayOutputStream out = new ByteArrayOutputStream();
DictionaryEntryPersistor.serialize(out, new Iterator<Entry>() {

Iterator<StringList> it = model.iterator();

public boolean hasNext() {
return it.hasNext();
}

public Entry next() {
StringList tokens = it.next();
Attributes attrs = new Attributes();
attrs.setValue("count", Integer.toString(model.getCount(tokens)));
return new Entry(tokens, attrs);
}

public void remove() {
throw new UnsupportedOperationException("remove not allowed");
}
}, false);
}

@Test
public void testAddCharSequenceMinEqualsMaxEqualsLength() {
NGramModel model = new NGramModel();
model.add("dog", 3, 3);
assertTrue(model.contains(new StringList("dog")));
assertEquals(1, model.getCount(new StringList("dog")));
}

@Test
public void testAddCharSequenceMaxGreaterThanLengthGeneratesNothing() {
NGramModel model = new NGramModel();
model.add("dog", 4, 5);
assertEquals(0, model.size());
}

@Test
public void testAddCharSequenceExactlyWholeInputLength() {
NGramModel model = new NGramModel();
model.add("cat", 3, 3);
assertEquals(1, model.size());
assertTrue(model.contains(new StringList("cat")));
}

@Test
public void testAddLeadingTrailingSpacesPreservedAsCharNGrams() {
NGramModel model = new NGramModel();
model.add(" x ", 1, 1);
assertTrue(model.contains(new StringList(" ")));
assertTrue(model.contains(new StringList("x")));
}

@Test
public void testSetCountToSameValueKeepsEntryUnchanged() {
NGramModel model = new NGramModel();
StringList entry = new StringList("unchanged");
model.add(entry);
model.setCount(entry, 1);
assertEquals(1, model.getCount(entry));
}

@Test
public void testSetCountToNonExistentKeyThrows() {
NGramModel model = new NGramModel();
model.setCount(new StringList("ghost"), 3);
}

@Test
public void testAddEmptyCharSequenceProducesNoNGrams() {
NGramModel model = new NGramModel();
model.add("", 1, 3);
assertEquals(0, model.size());
}

@Test
public void testRemoveExistingReducesSize() {
NGramModel model = new NGramModel();
StringList l1 = new StringList("first");
StringList l2 = new StringList("second");
model.add(l1);
model.add(l2);
assertEquals(2, model.size());
model.remove(l1);
assertEquals(1, model.size());
model.remove(l2);
assertEquals(0, model.size());
}

@Test
public void testToDictionarySizeMatchesUnderlyingModel() {
NGramModel model = new NGramModel();
model.add(new StringList("alpha"));
model.add(new StringList("beta"));
assertEquals(model.size(), model.toDictionary(true).size());
}

@Test
public void testContainsByIdentityAndContentEquality() {
NGramModel model = new NGramModel();
StringList a = new StringList("abc");
StringList b = new StringList("abc");
model.add(a);
assertTrue(model.contains(b));
}

@Test
public void testGetCountForUnseenEntryIsZero() {
NGramModel model = new NGramModel();
assertEquals(0, model.getCount(new StringList("unseen")));
}

@Test
public void testCutoffUpperOnlyRemovesOverLimitEntries() {
NGramModel model = new NGramModel();
StringList a = new StringList("a");
StringList b = new StringList("b");
model.add(a);
model.add(a);
model.add(a);
model.add(b);
model.add(b);
model.cutoff(0, 2);
assertFalse(model.contains(a));
assertTrue(model.contains(b));
}

@Test
public void testEqualsSymmetryWithDifferentInstancesSameContent() {
NGramModel m1 = new NGramModel();
NGramModel m2 = new NGramModel();
m1.add(new StringList("X"));
m2.add(new StringList("X"));
assertTrue(m1.equals(m2));
assertTrue(m2.equals(m1));
}

@Test
public void testEqualsReflexivityWithSelf() {
NGramModel model = new NGramModel();
model.add(new StringList("reflex"));
assertTrue(model.equals(model));
}

@Test
public void testEqualsWithNullReturnsFalse() {
NGramModel model = new NGramModel();
assertFalse(model.equals(null));
}

@Test
public void testEqualsWithDifferentObjectTypeReturnsFalse() {
NGramModel model = new NGramModel();
assertFalse(model.equals("not a model"));
}

@Test
public void testToStringReportsCorrectSize() {
NGramModel model = new NGramModel();
model.add(new StringList("hello"));
model.add(new StringList("world"));
String output = model.toString();
assertTrue(output.contains("Size: 2"));
}

@Test
public void testSerializationWithOnlyCharGrams() throws Exception {
NGramModel model = new NGramModel();
model.add("xyz", 1, 1);
model.add("xyz", 1, 1);
ByteArrayOutputStream out = new ByteArrayOutputStream();
model.serialize(out);
ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
NGramModel loaded = new NGramModel(in);
assertEquals(model.size(), loaded.size());
assertEquals(model.numberOfGrams(), loaded.numberOfGrams());
}

@Test
public void testDeserializeEntryWithInvalidCountValueThrows() throws Exception {
ByteArrayOutputStream out = new ByteArrayOutputStream();
Attributes attributes = new Attributes();
attributes.setValue("count", "notANumber");
Entry entry = new Entry(new StringList("bad"), attributes);
DictionaryEntryPersistor.serialize(out, java.util.Collections.singleton(entry).iterator(), false);
ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
new NGramModel(in);
}

@Test
public void testDeserializeEntryWithMissingCountThrows() throws Exception {
ByteArrayOutputStream out = new ByteArrayOutputStream();
Attributes attributes = new Attributes();
Entry entry = new Entry(new StringList("missing"), attributes);
DictionaryEntryPersistor.serialize(out, java.util.Collections.singleton(entry).iterator(), false);
ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
new NGramModel(in);
}

@Test
public void testAddNGramAlreadyExistsThenSetToSameCount() {
NGramModel model = new NGramModel();
StringList token = new StringList("static");
model.add(token);
model.setCount(token, 1);
assertEquals(1, model.getCount(token));
}

@Test
public void testAddCharSequenceWithNonAlphabeticCharacters() {
NGramModel model = new NGramModel();
model.add("a1!", 1, 1);
assertEquals(3, model.size());
assertTrue(model.contains(new StringList("a")));
assertTrue(model.contains(new StringList("1")));
assertTrue(model.contains(new StringList("!")));
}

@Test
public void testSerializeEmptyModelProducesValidStream() throws Exception {
NGramModel model = new NGramModel();
ByteArrayOutputStream out = new ByteArrayOutputStream();
model.serialize(out);
byte[] data = out.toByteArray();
assertTrue(data.length > 0);
ByteArrayInputStream in = new ByteArrayInputStream(data);
NGramModel loaded = new NGramModel(in);
assertEquals(0, loaded.size());
}

@Test
public void testCutoffWithExactMatchEntryIsPreserved() {
NGramModel model = new NGramModel();
StringList token = new StringList("cutoff");
model.add(token);
model.add(token);
model.cutoff(2, 2);
assertTrue(model.contains(token));
assertEquals(1, model.size());
}

@Test
public void testCutoffWithZeroZeroRemovesAll() {
NGramModel model = new NGramModel();
model.add(new StringList("a"));
model.add(new StringList("b"));
model.add(new StringList("b"));
model.cutoff(0, 0);
assertEquals(0, model.size());
}

@Test
public void testToDictionaryWithEmptyModelReturnsEmptyDictionary() {
NGramModel model = new NGramModel();
assertEquals(0, model.toDictionary().size());
assertEquals(0, model.toDictionary(true).size());
}

@Test
public void testAddTokenRangeWhereMinEqualsMaxEqualsOne() {
NGramModel model = new NGramModel();
StringList tokens = new StringList("x", "y", "z");
model.add(tokens, 1, 1);
assertTrue(model.contains(new StringList("x")));
assertTrue(model.contains(new StringList("y")));
assertTrue(model.contains(new StringList("z")));
assertFalse(model.contains(new StringList("x", "y")));
}

@Test
public void testAddTokenRangeWhereMinEqualsMaxEqualsFullLength() {
NGramModel model = new NGramModel();
StringList tokens = new StringList("x", "y", "z");
model.add(tokens, 3, 3);
assertTrue(model.contains(new StringList("x", "y", "z")));
assertEquals(1, model.size());
}

@Test
public void testEqualsReturnsFalseForSameTokenDifferentCounts() {
NGramModel m1 = new NGramModel();
NGramModel m2 = new NGramModel();
StringList token = new StringList("diff");
m1.add(token);
m2.add(token);
m2.add(token);
assertFalse(m1.equals(m2));
}

@Test
public void testSerializeEntryIteratorRemoveThrows() throws Exception {
NGramModel model = new NGramModel();
model.add(new StringList("remove"));
ByteArrayOutputStream out = new ByteArrayOutputStream();
// DictionaryEntryPersistor.serialize(out, new DictionaryEntryPersistor.EntryIterator() {
// 
// final java.util.Iterator<StringList> it = model.iterator();
// 
// public boolean hasNext() {
// return it.hasNext();
// }
// 
// public Entry next() {
// StringList tokens = it.next();
// Attributes attrs = new Attributes();
// attrs.setValue("count", "1");
// return new Entry(tokens, attrs);
// }
// 
// public void remove() {
// throw new UnsupportedOperationException("Not supported");
// }
// }, false);
}

@Test
public void testAddSameCharSequenceMultipleTimesIncrementsEachUniGram() {
NGramModel model = new NGramModel();
model.add("xy", 1, 1);
model.add("xy", 1, 1);
assertEquals(2, model.getCount(new StringList("x")));
assertEquals(2, model.getCount(new StringList("y")));
}

@Test
public void testSerializationPreservesTokenCountsExactly() throws Exception {
NGramModel model = new NGramModel();
model.add(new StringList("serialize"));
model.add(new StringList("serialize"));
model.add(new StringList("persist"));
ByteArrayOutputStream out = new ByteArrayOutputStream();
model.serialize(out);
ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
NGramModel restored = new NGramModel(in);
assertEquals(2, restored.getCount(new StringList("serialize")));
assertEquals(1, restored.getCount(new StringList("persist")));
assertEquals(2, restored.size());
}

@Test
public void testToStringOnEmptyModelReturnsSizeZero() {
NGramModel model = new NGramModel();
assertEquals("Size: 0", model.toString());
}

@Test
public void testHashCodeConsistencyAfterAdd() {
NGramModel model = new NGramModel();
int before = model.hashCode();
model.add(new StringList("hash"));
int after = model.hashCode();
assertNotEquals(before, after);
}

@Test
public void testContainsReturnsFalseForNonExistentKey() {
NGramModel model = new NGramModel();
assertFalse(model.contains(new StringList("nope")));
}

@Test
public void testAddTokenRangePartialGeneration() {
NGramModel model = new NGramModel();
StringList input = new StringList("a", "b", "c");
model.add(input, 2, 2);
assertTrue(model.contains(new StringList("a", "b")));
assertTrue(model.contains(new StringList("b", "c")));
assertFalse(model.contains(new StringList("a")));
assertFalse(model.contains(new StringList("a", "b", "c")));
}

@Test
public void testCharSequenceToLowerCaseBehavior() {
NGramModel model = new NGramModel();
model.add("XyZ", 1, 1);
assertTrue(model.contains(new StringList("x")));
assertTrue(model.contains(new StringList("y")));
assertTrue(model.contains(new StringList("z")));
}

@Test
public void testCharSequenceOverlapNGrams1to2() {
NGramModel model = new NGramModel();
model.add("abc", 1, 2);
assertTrue(model.contains(new StringList("a")));
assertTrue(model.contains(new StringList("b")));
assertTrue(model.contains(new StringList("c")));
assertTrue(model.contains(new StringList("ab")));
assertTrue(model.contains(new StringList("bc")));
assertFalse(model.contains(new StringList("abc")));
}
}
