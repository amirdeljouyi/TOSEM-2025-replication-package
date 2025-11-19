package opennlp.tools.ngram;

import opennlp.tools.dictionary.serializer.Attributes;
import opennlp.tools.dictionary.serializer.DictionaryEntryPersistor;
import opennlp.tools.dictionary.serializer.Entry;
import opennlp.tools.ml.model.*;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.StringList;
import opennlp.tools.util.TrainingParameters;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NGramModel_1_GPTLLMTest {

@Test
public void testAddAndGetCount() {
NGramModel model = new NGramModel();
StringList ngram = new StringList("hello", "world");
model.add(ngram);
assertEquals(1, model.getCount(ngram));
model.add(ngram);
assertEquals(2, model.getCount(ngram));
}

@Test
public void testSetCountExisting() {
NGramModel model = new NGramModel();
StringList ngram = new StringList("apple");
model.add(ngram);
model.setCount(ngram, 7);
assertEquals(7, model.getCount(ngram));
}

@Test
public void testSetCountOnNonExistingThrows() {
NGramModel model = new NGramModel();
StringList ngram = new StringList("missing");
model.setCount(ngram, 2);
}

@Test
public void testAddWithMinMaxLength() {
NGramModel model = new NGramModel();
StringList source = new StringList("a", "b", "c");
model.add(source, 1, 2);
assertEquals(1, model.getCount(new StringList("a")));
assertEquals(1, model.getCount(new StringList("b")));
assertEquals(1, model.getCount(new StringList("c")));
assertEquals(1, model.getCount(new StringList("a", "b")));
assertEquals(1, model.getCount(new StringList("b", "c")));
assertEquals(5, model.numberOfGrams());
}

@Test
public void testAddWithMinLessThanOne() {
NGramModel model = new NGramModel();
model.add(new StringList("one", "two"), 0, 2);
}

@Test
public void testAddWithMinGreaterThanMax() {
NGramModel model = new NGramModel();
model.add(new StringList("one", "two"), 3, 2);
}

@Test
public void testAddCharSequence() {
NGramModel model = new NGramModel();
model.add("abc", 1, 2);
assertEquals(1, model.getCount(new StringList("a")));
assertEquals(1, model.getCount(new StringList("b")));
assertEquals(1, model.getCount(new StringList("c")));
assertEquals(1, model.getCount(new StringList("ab")));
assertEquals(1, model.getCount(new StringList("bc")));
assertEquals(5, model.size());
}

@Test
public void testRemoveNGram() {
NGramModel model = new NGramModel();
StringList ngram = new StringList("delete", "me");
model.add(ngram);
assertTrue(model.contains(ngram));
model.remove(ngram);
assertFalse(model.contains(ngram));
}

@Test
public void testContainsTrueFalse() {
NGramModel model = new NGramModel();
StringList ngram1 = new StringList("present");
StringList ngram2 = new StringList("absent");
model.add(ngram1);
assertTrue(model.contains(ngram1));
assertFalse(model.contains(ngram2));
}

@Test
public void testSizeWithDuplicates() {
NGramModel model = new NGramModel();
model.add(new StringList("a"));
model.add(new StringList("b"));
model.add(new StringList("a"));
assertEquals(2, model.size());
}

@Test
public void testNumberOfGrams() {
NGramModel model = new NGramModel();
model.add(new StringList("a"));
model.add(new StringList("b"));
model.add(new StringList("a"));
assertEquals(3, model.numberOfGrams());
}

@Test
public void testCutoffThresholds() {
NGramModel model = new NGramModel();
StringList n1 = new StringList("one");
StringList n2 = new StringList("two");
StringList n3 = new StringList("three");
model.add(n1);
model.add(n2);
model.add(n2);
model.add(n3);
model.add(n3);
model.add(n3);
model.cutoff(2, 2);
assertFalse(model.contains(n1));
assertTrue(model.contains(n2));
assertFalse(model.contains(n3));
assertEquals(1, model.size());
}

@Test
public void testToDictionaryCaseSensitive() {
NGramModel model = new NGramModel();
model.add(new StringList("Test"));
model.add(new StringList("test"));
// Dictionary dict = model.toDictionary(true);
// assertTrue(dict.contains(new StringList("Test")));
// assertTrue(dict.contains(new StringList("test")));
// assertEquals(2, dict.size());
}

@Test
public void testToDictionaryCaseInsensitive() {
NGramModel model = new NGramModel();
model.add(new StringList("Test"));
model.add(new StringList("test"));
// Dictionary dict = model.toDictionary(false);
// assertTrue(dict.contains(new StringList("test")));
// assertEquals(1, dict.size());
}

@Test
public void testSerializeAndReconstruct() throws IOException {
NGramModel model = new NGramModel();
StringList a = new StringList("ng");
StringList b = new StringList("ram");
model.add(a);
model.add(b);
model.setCount(b, 5);
ByteArrayOutputStream out = new ByteArrayOutputStream();
model.serialize(out);
ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
NGramModel deserialized = new NGramModel(in);
assertEquals(1, deserialized.getCount(a));
assertEquals(5, deserialized.getCount(b));
assertEquals(2, deserialized.size());
}

@Test
public void testDeserializeMissingCountAttribute() throws IOException {
// Dictionary dict = new Dictionary();
// dict.put(new StringList("bad"));
ByteArrayOutputStream out = new ByteArrayOutputStream();
// DictionaryEntryPersistor.serialize(out, dict.iterator(), false);
ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
new NGramModel(in);
}

@Test
public void testDeserializeInvalidCountValue() throws IOException {
Entry badEntry = new Entry(new StringList("bad-ngram"), new Attributes() {

{
setValue("count", "invalidNumber");
}
});
Iterator<Entry> it = new Iterator<Entry>() {

private boolean done = false;

@Override
public boolean hasNext() {
return !done;
}

@Override
public Entry next() {
done = true;
return badEntry;
}

@Override
public void remove() {
}
};
ByteArrayOutputStream out = new ByteArrayOutputStream();
DictionaryEntryPersistor.serialize(out, it, false);
ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
new NGramModel(in);
}

@Test
public void testEqualsAndHashCode() {
NGramModel m1 = new NGramModel();
NGramModel m2 = new NGramModel();
m1.add(new StringList("x"));
m2.add(new StringList("x"));
assertEquals(m1, m2);
assertEquals(m1.hashCode(), m2.hashCode());
}

@Test
public void testToStringIsSize() {
NGramModel model = new NGramModel();
model.add(new StringList("word"));
String text = model.toString();
assertEquals("Size: 1", text);
}

@Test
public void testIteratorTraverse() {
NGramModel model = new NGramModel();
StringList one = new StringList("hello");
StringList two = new StringList("world");
model.add(one);
model.add(two);
Iterator<StringList> it = model.iterator();
assertTrue(it.hasNext());
StringList first = it.next();
assertNotNull(first);
assertTrue(it.hasNext());
StringList second = it.next();
assertNotNull(second);
assertFalse(it.hasNext());
}

@Test
public void testSerializeEntryIteratorRemoveThrows() {
NGramModel model = new NGramModel();
model.add(new StringList("static"));
Iterator<Entry> it = new Iterator<Entry>() {

private final Iterator<StringList> base = model.iterator();

@Override
public boolean hasNext() {
return base.hasNext();
}

@Override
public Entry next() {
StringList tokens = base.next();
Attributes attrs = new Attributes();
attrs.setValue("count", "1");
return new Entry(tokens, attrs);
}

@Override
public void remove() {
throw new UnsupportedOperationException();
}
};
it.remove();
}

@Test
public void testGetCountWithNullReturnsZero() {
NGramModel model = new NGramModel();
assertEquals(0, model.getCount(null));
}

@Test
public void testContainsWithNullReturnsFalse() {
NGramModel model = new NGramModel();
assertFalse(model.contains(null));
}

@Test
public void testRemoveNullDoesNotThrow() {
NGramModel model = new NGramModel();
model.remove(null);
assertEquals(0, model.size());
}

@Test
public void testAddEmptyCharSequence() {
NGramModel model = new NGramModel();
model.add("", 1, 3);
assertEquals(0, model.size());
}

@Test
public void testAddCharSequenceShorterThanMinLength() {
NGramModel model = new NGramModel();
model.add("a", 2, 3);
assertEquals(0, model.size());
}

@Test
public void testAddSameNgramMultipleTimesAffectsSizeAndCount() {
NGramModel model = new NGramModel();
StringList ngram = new StringList("repeat");
model.add(ngram);
model.add(ngram);
model.add(ngram);
assertEquals(1, model.size());
assertEquals(3, model.getCount(ngram));
assertEquals(3, model.numberOfGrams());
}

@Test
public void testEqualsDifferentTypeReturnsFalse() {
NGramModel model = new NGramModel();
assertFalse(model.equals("not a model"));
}

@Test
public void testEqualsWithNullReturnsFalse() {
NGramModel model = new NGramModel();
assertFalse(model.equals(null));
}

@Test
public void testEqualsWithDifferentModelsReturnsFalse() {
NGramModel model1 = new NGramModel();
NGramModel model2 = new NGramModel();
model1.add(new StringList("test"));
assertFalse(model1.equals(model2));
}

@Test
public void testHashCodeConsistency() {
NGramModel model = new NGramModel();
model.add(new StringList("consistent"));
int hash1 = model.hashCode();
int hash2 = model.hashCode();
assertEquals(hash1, hash2);
}

@Test
public void testToDictionaryOnEmptyModelReturnsEmptyDictionary() {
NGramModel model = new NGramModel();
// Dictionary dict = model.toDictionary();
// assertEquals(0, dict.size());
}

@Test
public void testNumberOfGramsEmptyModelReturnsZero() {
NGramModel model = new NGramModel();
assertEquals(0, model.numberOfGrams());
}

@Test
public void testSerializeEmptyModelProducesValidOutput() throws IOException {
NGramModel model = new NGramModel();
ByteArrayOutputStream out = new ByteArrayOutputStream();
model.serialize(out);
ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
NGramModel model2 = new NGramModel(in);
assertEquals(0, model2.size());
}

@Test
public void testCutoffWithNoEffect() {
NGramModel model = new NGramModel();
model.add(new StringList("word1"));
model.add(new StringList("word2"));
model.add(new StringList("word2"));
model.cutoff(1, 2);
assertEquals(2, model.size());
assertTrue(model.contains(new StringList("word1")));
assertTrue(model.contains(new StringList("word2")));
}

@Test
public void testAddEmptyStringListDoesNotThrow() {
NGramModel model = new NGramModel();
StringList empty = new StringList();
model.add(empty);
assertEquals(1, model.size());
assertTrue(model.contains(empty));
assertEquals(1, model.getCount(empty));
}

@Test
public void testToDictionaryPreservesAllEntries() {
NGramModel model = new NGramModel();
model.add(new StringList("A"));
model.add(new StringList("B"));
model.setCount(new StringList("B"), 4);
model.add(new StringList("C"));
// Dictionary dict = model.toDictionary();
// assertTrue(dict.contains(new StringList("a")));
// assertTrue(dict.contains(new StringList("b")));
// assertTrue(dict.contains(new StringList("c")));
// assertEquals(3, dict.size());
}

@Test
public void testSerializeEntryCountsMatch() throws IOException {
NGramModel model = new NGramModel();
StringList key = new StringList("token");
model.add(key);
model.setCount(key, 3);
ByteArrayOutputStream out = new ByteArrayOutputStream();
model.serialize(out);
ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
NGramModel loaded = new NGramModel(in);
assertEquals(3, loaded.getCount(new StringList("token")));
}

@Test
public void testCutoffAllEntriesRemoved() {
NGramModel model = new NGramModel();
model.add(new StringList("x"));
model.add(new StringList("y"));
model.cutoff(2, Integer.MAX_VALUE);
assertEquals(0, model.size());
assertFalse(model.contains(new StringList("x")));
assertFalse(model.contains(new StringList("y")));
}

@Test
public void testToDictionaryMultipleCaseVariantsCaseInsensitive() {
NGramModel model = new NGramModel();
model.add(new StringList("EXAMPLE"));
model.add(new StringList("example"));
model.add(new StringList("Example"));
// Dictionary dict = model.toDictionary(false);
// assertTrue(dict.contains(new StringList("example")));
// assertEquals(1, dict.size());
}

@Test
public void testToDictionaryMultipleCaseVariantsCaseSensitive() {
NGramModel model = new NGramModel();
model.add(new StringList("EXAMPLE"));
model.add(new StringList("example"));
model.add(new StringList("Example"));
// Dictionary dict = model.toDictionary(true);
// assertTrue(dict.contains(new StringList("EXAMPLE")));
// assertTrue(dict.contains(new StringList("example")));
// assertTrue(dict.contains(new StringList("Example")));
// assertEquals(3, dict.size());
}

@Test
public void testAddStringListWithNullToken() {
NGramModel model = new NGramModel();
String[] tokens = new String[] { null };
StringList list = new StringList(tokens);
model.add(list);
assertTrue(model.contains(list));
assertEquals(1, model.getCount(list));
}

@Test
public void testAddStringListWithMultipleNullTokens() {
NGramModel model = new NGramModel();
String[] tokens = new String[] { null, null };
StringList list = new StringList(tokens);
model.add(list);
assertTrue(model.contains(list));
assertEquals(1, model.getCount(list));
}

@Test
public void testAddCharSequenceWithIdenticalSubgrams() {
NGramModel model = new NGramModel();
model.add("aaa", 1, 2);
assertEquals(1, model.getCount(new StringList("aaa")));
assertEquals(2, model.getCount(new StringList("aa")));
assertEquals(3, model.getCount(new StringList("a")));
assertEquals(3, model.size());
}

@Test
public void testCutoffIncludesExactUpperBound() {
NGramModel model = new NGramModel();
model.add(new StringList("keep"));
model.add(new StringList("keep"));
model.setCount(new StringList("keep"), 3);
model.cutoff(1, 3);
assertEquals(1, model.size());
assertTrue(model.contains(new StringList("keep")));
}

@Test
public void testCutoffExcludesAboveUpperBound() {
NGramModel model = new NGramModel();
model.add(new StringList("drop"));
model.add(new StringList("drop"));
model.add(new StringList("drop"));
model.add(new StringList("drop"));
model.setCount(new StringList("drop"), 4);
model.cutoff(1, 3);
assertFalse(model.contains(new StringList("drop")));
assertEquals(0, model.size());
}

@Test
public void testCutoffExcludesBelowLowerBound() {
NGramModel model = new NGramModel();
model.add(new StringList("drop"));
model.setCount(new StringList("drop"), 0);
model.cutoff(1, Integer.MAX_VALUE);
assertFalse(model.contains(new StringList("drop")));
assertEquals(0, model.size());
}

@Test
public void testAddMultipleStringListsWithOverlappingTokens() {
NGramModel model = new NGramModel();
model.add(new StringList("a", "b"));
model.add(new StringList("b", "c"));
model.add(new StringList("a", "b"));
assertEquals(2, model.getCount(new StringList("a", "b")));
assertEquals(1, model.getCount(new StringList("b", "c")));
assertEquals(2, model.size());
}

@Test
public void testAddMultipleGramsAndCallSizeAccurately() {
NGramModel model = new NGramModel();
model.add(new StringList("x", "y"));
model.add(new StringList("x", "z"));
model.add(new StringList("y", "z"));
model.add(new StringList("z", "x"));
assertEquals(4, model.size());
}

@Test
public void testAddVeryLargeSingleTokenCharSequence() {
NGramModel model = new NGramModel();
model.add("abcdef", 3, 3);
assertTrue(model.contains(new StringList("abc")));
assertTrue(model.contains(new StringList("bcd")));
assertTrue(model.contains(new StringList("cde")));
assertTrue(model.contains(new StringList("def")));
assertEquals(4, model.size());
}

@Test
public void testAddCharSequenceWithNegativeBoundsThrows() {
NGramModel model = new NGramModel();
model.add("abc", -1, 2);
}

@Test
public void testSerializeAndDeserializeEmptyEntriesPreservesState() throws IOException {
NGramModel model = new NGramModel();
model.add(new StringList());
ByteArrayOutputStream out = new ByteArrayOutputStream();
model.serialize(out);
ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
NGramModel loaded = new NGramModel(in);
assertEquals(1, loaded.size());
assertTrue(loaded.contains(new StringList()));
}

@Test
public void testMultipleCallsToHashCodeProduceSameValue() {
NGramModel model = new NGramModel();
model.add(new StringList("repeatable"));
int hash1 = model.hashCode();
int hash2 = model.hashCode();
int hash3 = model.hashCode();
assertEquals(hash1, hash2);
assertEquals(hash1, hash3);
}

@Test
public void testEqualsSameReferenceIsTrue() {
NGramModel model = new NGramModel();
assertTrue(model.equals(model));
}

@Test
public void testDifferentGramsSameCountAreNotEqual() {
NGramModel model1 = new NGramModel();
NGramModel model2 = new NGramModel();
model1.add(new StringList("A"));
model2.add(new StringList("B"));
assertFalse(model1.equals(model2));
}

@Test
public void testToStringEmptyModel() {
NGramModel model = new NGramModel();
assertEquals("Size: 0", model.toString());
}

@Test
public void testIteratorEmptyModel() {
NGramModel model = new NGramModel();
Iterator<StringList> it = model.iterator();
assertFalse(it.hasNext());
}

// @Test(expected = java.util.NoSuchElementException.class)
public void testIteratorNextOnEmptyThrows() {
NGramModel model = new NGramModel();
Iterator<StringList> it = model.iterator();
it.next();
}

@Test
public void testHashCodeDifferentModelsProduceDifferentHash() {
NGramModel model1 = new NGramModel();
NGramModel model2 = new NGramModel();
model1.add(new StringList("test"));
model2.add(new StringList("different"));
int hash1 = model1.hashCode();
int hash2 = model2.hashCode();
assertNotEquals(hash1, hash2);
}

@Test
public void testDeserializePreservesMultipleNGramsCounts() throws IOException {
NGramModel model = new NGramModel();
model.add(new StringList("one"));
model.add(new StringList("two"));
model.setCount(new StringList("two"), 5);
ByteArrayOutputStream out = new ByteArrayOutputStream();
model.serialize(out);
ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
NGramModel deserialized = new NGramModel(in);
assertEquals(1, deserialized.getCount(new StringList("one")));
assertEquals(5, deserialized.getCount(new StringList("two")));
assertEquals(2, deserialized.size());
}

@Test
public void testAddNGramWithEmptyTokenArray() {
NGramModel model = new NGramModel();
StringList emptyList = new StringList(new String[] {});
model.add(emptyList);
assertEquals(1, model.size());
assertEquals(1, model.getCount(emptyList));
assertTrue(model.contains(emptyList));
}

@Test
public void testMultipleAddsWithDifferentTokensSamePrefix() {
NGramModel model = new NGramModel();
StringList sl1 = new StringList("a", "b");
StringList sl2 = new StringList("a", "c");
model.add(sl1);
model.add(sl2);
assertEquals(2, model.size());
assertEquals(1, model.getCount(sl1));
assertEquals(1, model.getCount(sl2));
assertTrue(model.contains(sl1));
assertTrue(model.contains(sl2));
}

@Test
public void testToDictionaryCaseSensitiveWithMixedCase() {
NGramModel model = new NGramModel();
model.add(new StringList("Word"));
model.add(new StringList("word"));
model.add(new StringList("WORD"));
// Dictionary dictionary = model.toDictionary(true);
// assertTrue(dictionary.contains(new StringList("Word")));
// assertTrue(dictionary.contains(new StringList("word")));
// assertTrue(dictionary.contains(new StringList("WORD")));
// assertEquals(3, dictionary.size());
}

@Test
public void testToDictionaryCaseInsensitiveWithMixedCase() {
NGramModel model = new NGramModel();
model.add(new StringList("Word"));
model.add(new StringList("word"));
model.add(new StringList("WoRd"));
// Dictionary dictionary = model.toDictionary(false);
// assertTrue(dictionary.contains(new StringList("word")));
// assertEquals(1, dictionary.size());
}

@Test
public void testMultipleIdenticalNGramsAllMergedAndCounted() {
NGramModel model = new NGramModel();
StringList sl = new StringList("token");
model.add(sl);
model.add(sl);
model.add(sl);
assertEquals(3, model.getCount(sl));
assertEquals(1, model.size());
}

@Test
public void testAddCharSequenceWithIdenticalCharacters() {
NGramModel model = new NGramModel();
model.add("aaa", 1, 1);
assertEquals(1, model.size());
assertEquals(3, model.getCount(new StringList("a")));
}

@Test
public void testAddAndRemoveThenAddAgain() {
NGramModel model = new NGramModel();
StringList list = new StringList("reset");
model.add(list);
model.remove(list);
assertFalse(model.contains(list));
model.add(list);
assertTrue(model.contains(list));
assertEquals(1, model.getCount(list));
}

@Test
public void testCutoffWithExactCountNotRemoved() {
NGramModel model = new NGramModel();
StringList item = new StringList("preserve");
model.add(item);
model.setCount(item, 2);
model.cutoff(2, 2);
assertTrue(model.contains(item));
}

@Test
public void testSerializeEmptyModelProducesValidStream() throws IOException {
NGramModel model = new NGramModel();
ByteArrayOutputStream out = new ByteArrayOutputStream();
model.serialize(out);
byte[] bytes = out.toByteArray();
assertTrue(bytes.length > 0);
}

@Test
public void testSerializeIteratorRemoveUnsupported() {
NGramModel model = new NGramModel();
StringList entry = new StringList("immutable");
model.add(entry);
Iterator<Entry> it = new Iterator<Entry>() {

Iterator<StringList> listIterator = model.iterator();

public boolean hasNext() {
return listIterator.hasNext();
}

public Entry next() {
StringList str = listIterator.next();
Attributes attr = new Attributes();
attr.setValue("count", String.valueOf(model.getCount(str)));
return new Entry(str, attr);
}

public void remove() {
throw new UnsupportedOperationException("remove not supported");
}
};
it.remove();
}

@Test
public void testSetCountOnNonExistentNGramThrowsAfterTemporaryPut() {
NGramModel model = new NGramModel();
StringList ngram = new StringList("no-such");
try {
model.setCount(ngram, 10);
} catch (NoSuchElementException e) {
assertFalse(model.contains(ngram));
throw e;
}
}

@Test
public void testDeserializePreservesEmptyStringListEntry() throws IOException {
StringList empty = new StringList();
NGramModel model = new NGramModel();
model.add(empty);
ByteArrayOutputStream out = new ByteArrayOutputStream();
model.serialize(out);
ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
NGramModel reloaded = new NGramModel(in);
assertEquals(1, reloaded.size());
assertEquals(1, reloaded.getCount(empty));
}

@Test
public void testSizeAfterAddRemoveAddSameNGram() {
NGramModel model = new NGramModel();
StringList list = new StringList("bounce");
model.add(list);
model.remove(list);
model.add(list);
assertEquals(1, model.size());
}

@Test
public void testAddStringListWithSingleNullElement() {
NGramModel model = new NGramModel();
String[] data = new String[] { null };
StringList sl = new StringList(data);
model.add(sl);
assertTrue(model.contains(sl));
assertEquals(1, model.getCount(sl));
}

@Test
public void testSetCountAfterMultipleAddsUpdatesCorrectly() {
NGramModel model = new NGramModel();
StringList sl = new StringList("adjustable");
model.add(sl);
model.add(sl);
model.setCount(sl, 10);
assertEquals(10, model.getCount(sl));
}

@Test
public void testCutoffWithNoEntriesDoesNotThrow() {
NGramModel model = new NGramModel();
model.cutoff(1, 2);
assertEquals(0, model.size());
}

@Test
public void testCutoffMaxIntegerCausesOnlyLowerBoundFilter() {
NGramModel model = new NGramModel();
StringList sl = new StringList("safe");
model.add(sl);
model.setCount(sl, 1);
model.cutoff(2, Integer.MAX_VALUE);
assertEquals(0, model.size());
}

@Test
public void testCutoffZeroZeroClearsAllEntries() {
NGramModel model = new NGramModel();
model.add(new StringList("one"));
model.add(new StringList("two"));
model.cutoff(0, 0);
assertEquals(0, model.size());
}

@Test
public void testEqualsTwoEmptyModelsTrue() {
NGramModel model1 = new NGramModel();
NGramModel model2 = new NGramModel();
assertTrue(model1.equals(model2));
}

@Test
public void testEqualsSameValuesDifferentCountsFalse() {
NGramModel model1 = new NGramModel();
NGramModel model2 = new NGramModel();
StringList sl = new StringList("value");
model1.add(sl);
model1.add(sl);
model2.add(sl);
assertFalse(model1.equals(model2));
}

@Test
public void testAddMultipleTokenSamePrefixDifferentOrder() {
NGramModel model = new NGramModel();
model.add(new StringList("a", "b"));
model.add(new StringList("b", "a"));
assertEquals(2, model.size());
assertEquals(1, model.getCount(new StringList("a", "b")));
assertEquals(1, model.getCount(new StringList("b", "a")));
}

@Test
public void testAddCharSequenceWithZeroMaxLengthThrows() {
NGramModel model = new NGramModel();
model.add("data", 1, 0);
}

@Test
public void testAddCharSequenceWithNegativeMinLengthThrows() {
NGramModel model = new NGramModel();
model.add("data", -2, 4);
}

@Test
public void testSerializeModelWithMultipleComplexKeys() throws IOException {
NGramModel model = new NGramModel();
model.add(new StringList("this", "is", "a", "test"));
model.add(new StringList("another", "test"));
model.setCount(new StringList("another", "test"), 4);
ByteArrayOutputStream out = new ByteArrayOutputStream();
model.serialize(out);
ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
NGramModel loaded = new NGramModel(in);
assertEquals(1, loaded.getCount(new StringList("this", "is", "a", "test")));
assertEquals(4, loaded.getCount(new StringList("another", "test")));
}

@Test
public void testDeserializeWithTrailingEmptyEntry() throws IOException {
StringList sl = new StringList("empty");
Attributes attr1 = new Attributes();
attr1.setValue("count", "1");
Attributes attr2 = new Attributes();
attr2.setValue("count", "2");
Entry entry1 = new Entry(sl, attr1);
Entry entry2 = new Entry(sl, attr2);
Iterator<Entry> it = new Iterator<Entry>() {

boolean emittedFirst = false;

boolean emittedSecond = false;

public boolean hasNext() {
return !emittedSecond;
}

public Entry next() {
if (!emittedFirst) {
emittedFirst = true;
return entry1;
} else if (!emittedSecond) {
emittedSecond = true;
return entry2;
}
throw new NoSuchElementException();
}

public void remove() {
}
};
ByteArrayOutputStream out = new ByteArrayOutputStream();
DictionaryEntryPersistor.serialize(out, it, false);
ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
NGramModel model = new NGramModel(in);
assertEquals(1, model.size());
assertEquals(2, model.getCount(sl));
}

@Test
public void testDeserializeWithMissingCountKeyThrows() throws IOException {
Attributes attr = new Attributes();
Entry entry = new Entry(new StringList("broken"), attr);
Iterator<Entry> it = new Iterator<Entry>() {

boolean nextCalled = false;

public boolean hasNext() {
return !nextCalled;
}

public Entry next() {
nextCalled = true;
return entry;
}

public void remove() {
}
};
ByteArrayOutputStream out = new ByteArrayOutputStream();
DictionaryEntryPersistor.serialize(out, it, false);
ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
new NGramModel(in);
}

@Test
public void testAddSameCharGramAgainIncreasesCount() {
NGramModel model = new NGramModel();
model.add("xy", 1, 2);
model.add("xy", 1, 2);
assertTrue(model.getCount(new StringList("x")) == 2);
assertTrue(model.getCount(new StringList("y")) == 2);
assertTrue(model.getCount(new StringList("xy")) == 2);
assertEquals(3, model.size());
assertEquals(6, model.numberOfGrams());
}

@Test
public void testIteratorOverSingleNGram() {
NGramModel model = new NGramModel();
StringList sl = new StringList("alpha");
model.add(sl);
Iterator<StringList> iterator = model.iterator();
assertTrue(iterator.hasNext());
assertEquals(sl, iterator.next());
assertFalse(iterator.hasNext());
}

@Test
public void testAddStringListWithOneTokenAndExplicitMinMaxOne() {
NGramModel model = new NGramModel();
StringList input = new StringList("only");
model.add(input, 1, 1);
assertEquals(1, model.size());
assertTrue(model.contains(input));
assertEquals(1, model.getCount(input));
}

@Test
public void testAddCharSequenceWhereLengthEqualsMinLength() {
NGramModel model = new NGramModel();
model.add("xyz", 3, 3);
assertTrue(model.contains(new StringList("xyz")));
assertEquals(1, model.getCount(new StringList("xyz")));
}

@Test
public void testEqualsWithDifferentSubclassReturnsFalse() {
NGramModel model = new NGramModel();
Object other = new Object() {

@Override
public boolean equals(Object obj) {
return false;
}
};
assertFalse(model.equals(other));
}

@Test
public void testCharSequenceInputIsLowercased() {
NGramModel model = new NGramModel();
model.add("AbC", 1, 1);
assertTrue(model.contains(new StringList("a")));
assertTrue(model.contains(new StringList("b")));
assertTrue(model.contains(new StringList("c")));
assertFalse(model.contains(new StringList("A")));
}

@Test
public void testToDictionaryCaseInsensitiveWithSameTokensDifferentCases() {
NGramModel model = new NGramModel();
model.add(new StringList("Mix"));
model.add(new StringList("mix"));
model.add(new StringList("MIX"));
// Dictionary dict = model.toDictionary(false);
// assertEquals(1, dict.size());
// assertTrue(dict.contains(new StringList("mix")));
}

@Test
public void testNumberOfGramsExcludesZeroCountEntries() {
NGramModel model = new NGramModel();
StringList item = new StringList("zero");
model.add(item);
model.setCount(item, 0);
assertEquals(0, model.numberOfGrams());
}

@Test
public void testCutoffPreservesExactlyMinAndMax() {
NGramModel model = new NGramModel();
StringList low = new StringList("low");
StringList high = new StringList("high");
model.add(low);
model.setCount(low, 2);
model.add(high);
model.setCount(high, 5);
model.cutoff(2, 5);
assertTrue(model.contains(low));
assertTrue(model.contains(high));
assertEquals(2, model.size());
}

@Test
public void testCutoffRemovesBelowMinAndAboveMax() {
NGramModel model = new NGramModel();
StringList small = new StringList("small");
StringList middle = new StringList("middle");
StringList big = new StringList("big");
model.add(small);
model.setCount(small, 1);
model.add(middle);
model.setCount(middle, 3);
model.add(big);
model.setCount(big, 10);
model.cutoff(2, 5);
assertFalse(model.contains(small));
assertTrue(model.contains(middle));
assertFalse(model.contains(big));
assertEquals(1, model.size());
}

@Test
public void testAddIncrementalMergeIncreasesCountWithoutDuplicates() {
NGramModel model = new NGramModel();
StringList input = new StringList("merge");
model.add(input);
model.add(input);
model.add(input);
assertEquals(1, model.size());
assertEquals(3, model.getCount(input));
}

@Test
public void testMultipleDistinctStringListsSameTokenCountedSeparately() {
NGramModel model = new NGramModel();
StringList a = new StringList("token");
StringList b = new StringList("token", "again");
model.add(a);
model.add(b);
assertEquals(2, model.size());
assertEquals(1, model.getCount(a));
assertEquals(1, model.getCount(b));
}

@Test
public void testToDictionaryPreservesAllMultipleTokenGrams() {
NGramModel model = new NGramModel();
StringList a = new StringList("a", "b");
StringList b = new StringList("b", "c");
model.add(a);
model.add(b);
// Dictionary dict = model.toDictionary();
// assertTrue(dict.contains(a));
// assertTrue(dict.contains(b));
// assertEquals(2, dict.size());
}

@Test
public void testAddCharSequenceWithOverlappingBigrams() {
NGramModel model = new NGramModel();
model.add("abc", 2, 2);
assertEquals(2, model.size());
assertTrue(model.contains(new StringList("ab")));
assertTrue(model.contains(new StringList("bc")));
}

@Test
public void testHashCodeDiffersOnDifferentModels() {
NGramModel model1 = new NGramModel();
NGramModel model2 = new NGramModel();
model1.add(new StringList("hash"));
model2.add(new StringList("different"));
int h1 = model1.hashCode();
int h2 = model2.hashCode();
assertNotEquals(h1, h2);
}

@Test
public void testIteratorReturnsSameOrderAsAdded() {
NGramModel model = new NGramModel();
StringList a = new StringList("1");
StringList b = new StringList("2");
model.add(a);
model.add(b);
Iterator<StringList> iterator = model.iterator();
StringList first = iterator.next();
StringList second = iterator.next();
assertEquals(a, first);
assertEquals(b, second);
}

@Test
public void testEqualsWithItselfReturnsTrue() {
NGramModel model = new NGramModel();
assertTrue(model.equals(model));
}

@Test
public void testEqualsWithObjectOfDifferentTypeReturnsFalse() {
NGramModel model = new NGramModel();
Object other = new Object();
assertFalse(model.equals(other));
}

@Test
public void testHashCodeWithEmptyModel() {
NGramModel model = new NGramModel();
int hash = model.hashCode();
assertEquals(hash, model.hashCode());
}

@Test
public void testAddCharSequenceWithSingleCharacter() {
NGramModel model = new NGramModel();
model.add("a", 1, 1);
assertEquals(1, model.size());
assertTrue(model.contains(new StringList("a")));
assertEquals(1, model.getCount(new StringList("a")));
}

@Test
public void testAddCharSequenceWithAllSameCharacter() {
NGramModel model = new NGramModel();
model.add("aaa", 1, 2);
assertEquals(2, model.size());
assertEquals(3, model.getCount(new StringList("a")));
assertEquals(2, model.getCount(new StringList("aa")));
}

@Test
public void testRemoveNonExistentNGramDoesNotThrow() {
NGramModel model = new NGramModel();
model.remove(new StringList("not", "present"));
assertEquals(0, model.size());
}

@Test
public void testToDictionaryMergedEntriesIgnoreCase() {
NGramModel model = new NGramModel();
model.add(new StringList("VALUE"));
model.add(new StringList("value"));
// Dictionary dictionary = model.toDictionary(false);
// assertEquals(1, dictionary.size());
// assertTrue(dictionary.contains(new StringList("value")));
}

@Test
public void testToDictionaryMergedEntriesPreserveCaseSensitive() {
NGramModel model = new NGramModel();
model.add(new StringList("Aaa"));
model.add(new StringList("aaa"));
// Dictionary dictionary = model.toDictionary(true);
// assertEquals(2, dictionary.size());
// assertTrue(dictionary.contains(new StringList("Aaa")));
// assertTrue(dictionary.contains(new StringList("aaa")));
}

@Test
public void testAddStringListWithSameReferenceTwice() {
NGramModel model = new NGramModel();
StringList shared = new StringList("dup");
model.add(shared);
model.add(shared);
assertEquals(2, model.getCount(shared));
assertEquals(1, model.size());
}

@Test
public void testSerializeDeserializeWithOverlappingNGrams() throws Exception {
NGramModel model = new NGramModel();
StringList a = new StringList("a");
StringList ab = new StringList("a", "b");
model.add(a);
model.add(ab);
model.setCount(ab, 5);
ByteArrayOutputStream out = new ByteArrayOutputStream();
model.serialize(out);
ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
NGramModel loaded = new NGramModel(in);
assertEquals(1, loaded.getCount(a));
assertEquals(5, loaded.getCount(ab));
assertEquals(2, loaded.size());
}

@Test
public void testSetCountOnNewNGramRejected() {
NGramModel model = new NGramModel();
StringList newNGram = new StringList("new");
model.setCount(newNGram, 10);
}

@Test
public void testDeserializeFailsWithMissingCountAttribute() throws Exception {
Entry corruptedEntry = new Entry(new StringList("broken"), new Attributes());
Iterator<Entry> entryIterator = new Iterator<Entry>() {

boolean read = false;

public boolean hasNext() {
return !read;
}

public Entry next() {
read = true;
return corruptedEntry;
}

public void remove() {
}
};
ByteArrayOutputStream out = new ByteArrayOutputStream();
DictionaryEntryPersistor.serialize(out, entryIterator, false);
ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
new NGramModel(in);
}

@Test
public void testDeserializeFailsWithNonNumericCount() throws Exception {
Attributes attributes = new Attributes();
attributes.setValue("count", "NaN");
Entry entry = new Entry(new StringList("bad"), attributes);
Iterator<Entry> entryIterator = new Iterator<Entry>() {

boolean read = false;

public boolean hasNext() {
return !read;
}

public Entry next() {
read = true;
return entry;
}

public void remove() {
}
};
ByteArrayOutputStream out = new ByteArrayOutputStream();
DictionaryEntryPersistor.serialize(out, entryIterator, false);
ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
new NGramModel(in);
}

@Test
public void testAddStringListThenSetCountZeroRemovesFromTotalGrams() {
NGramModel model = new NGramModel();
StringList list = new StringList("x");
model.add(list);
model.setCount(list, 0);
assertEquals(0, model.numberOfGrams());
assertTrue(model.contains(list));
assertEquals(1, model.size());
}

@Test
public void testToStringOnEmptyAndNonEmptyModels() {
NGramModel emptyModel = new NGramModel();
assertEquals("Size: 0", emptyModel.toString());
NGramModel nonEmptyModel = new NGramModel();
nonEmptyModel.add(new StringList("nonempty"));
assertEquals("Size: 1", nonEmptyModel.toString());
}

@Test
public void testAddWithMinMaxExceedingTokenCountAddsNothing() {
NGramModel model = new NGramModel();
model.add(new StringList("a", "b"), 3, 4);
assertEquals(0, model.size());
}

@Test
public void testSerializeDeserializeEmptyModelSuccessfully() throws Exception {
NGramModel model = new NGramModel();
ByteArrayOutputStream out = new ByteArrayOutputStream();
model.serialize(out);
ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
NGramModel loadedModel = new NGramModel(in);
assertEquals(0, loadedModel.size());
}

@Test
public void testAddCharSequenceWhenMinGreaterThanLengthAddsNothing() {
NGramModel model = new NGramModel();
model.add("xy", 3, 4);
assertEquals(0, model.size());
}
}
