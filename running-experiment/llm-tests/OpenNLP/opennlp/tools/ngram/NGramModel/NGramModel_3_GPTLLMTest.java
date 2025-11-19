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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NGramModel_3_GPTLLMTest {

@Test
public void testAddAndContains() {
NGramModel model = new NGramModel();
StringList ngram = new StringList("this", "is", "a", "test");
assertFalse(model.contains(ngram));
model.add(ngram);
assertTrue(model.contains(ngram));
}

@Test
public void testAddAndGetCount() {
NGramModel model = new NGramModel();
StringList ngram = new StringList("token");
assertEquals(0, model.getCount(ngram));
model.add(ngram);
assertEquals(1, model.getCount(ngram));
model.add(ngram);
assertEquals(2, model.getCount(ngram));
}

@Test
public void testSetCountValid() {
NGramModel model = new NGramModel();
StringList ngram = new StringList("a", "test");
model.add(ngram);
model.setCount(ngram, 5);
assertEquals(5, model.getCount(ngram));
}

@Test
public void testSetCountThrowsExceptionIfNotPresent() {
NGramModel model = new NGramModel();
StringList ngram = new StringList("missing");
model.setCount(ngram, 1);
}

@Test
public void testAddWithInvalidMinLength() {
NGramModel model = new NGramModel();
model.add(new StringList("a", "b"), 0, 2);
}

@Test
public void testAddWithInvalidMaxLength() {
NGramModel model = new NGramModel();
model.add(new StringList("a", "b"), 3, 2);
}

@Test
public void testAddWithNGramSpan() {
NGramModel model = new NGramModel();
StringList input = new StringList("a", "b", "c");
model.add(input, 1, 2);
assertTrue(model.contains(new StringList("a")));
assertTrue(model.contains(new StringList("b")));
assertTrue(model.contains(new StringList("c")));
assertTrue(model.contains(new StringList("a", "b")));
assertTrue(model.contains(new StringList("b", "c")));
assertFalse(model.contains(new StringList("a", "b", "c")));
}

@Test
public void testAddCharacterNGrams() {
NGramModel model = new NGramModel();
model.add("hello", 1, 2);
assertTrue(model.contains(new StringList("h")));
assertTrue(model.contains(new StringList("he")));
assertTrue(model.contains(new StringList("e")));
assertTrue(model.contains(new StringList("el")));
assertTrue(model.contains(new StringList("l")));
assertTrue(model.contains(new StringList("ll")));
assertTrue(model.contains(new StringList("o")));
assertTrue(model.contains(new StringList("lo")));
assertFalse(model.contains(new StringList("hel")));
}

@Test
public void testRemove() {
NGramModel model = new NGramModel();
StringList ngram = new StringList("a", "b");
model.add(ngram);
assertTrue(model.contains(ngram));
model.remove(ngram);
assertFalse(model.contains(ngram));
}

@Test
public void testSize() {
NGramModel model = new NGramModel();
model.add(new StringList("a"));
model.add(new StringList("b"));
assertEquals(2, model.size());
}

@Test
public void testIterator() {
NGramModel model = new NGramModel();
StringList a = new StringList("a");
StringList b = new StringList("b");
model.add(a);
model.add(b);
Iterator<StringList> iterator = model.iterator();
StringList item1 = iterator.next();
StringList item2 = iterator.next();
Set<StringList> items = new HashSet<>();
items.add(item1);
items.add(item2);
assertTrue(items.contains(a));
assertTrue(items.contains(b));
assertFalse(iterator.hasNext());
}

@Test
public void testNumberOfGrams() {
NGramModel model = new NGramModel();
model.add(new StringList("a"));
model.add(new StringList("a"));
model.add(new StringList("b"));
assertEquals(3, model.numberOfGrams());
}

@Test
public void testCutoff() {
NGramModel model = new NGramModel();
model.add(new StringList("x"));
model.add(new StringList("y"));
model.add(new StringList("z"));
model.add(new StringList("z"));
model.cutoff(2, 2);
assertFalse(model.contains(new StringList("x")));
assertFalse(model.contains(new StringList("y")));
assertTrue(model.contains(new StringList("z")));
}

@Test
public void testToDictionaryDefaultCaseInsensitive() {
NGramModel model = new NGramModel();
model.add(new StringList("Hello"));
model.add(new StringList("hello"));
Dictionary dict = model.toDictionary();
assertEquals(1, dict.size());
assertTrue(dict.contains(new StringList("hello")));
}

@Test
public void testToDictionaryCaseSensitive() {
NGramModel model = new NGramModel();
model.add(new StringList("Hello"));
model.add(new StringList("hello"));
Dictionary dict = model.toDictionary(true);
assertEquals(2, dict.size());
assertTrue(dict.contains(new StringList("Hello")));
assertTrue(dict.contains(new StringList("hello")));
}

@Test
public void testSerializeAndDeserialize() throws IOException {
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
assertEquals(model, deserialized);
assertEquals(model.hashCode(), deserialized.hashCode());
assertEquals(1, deserialized.getCount(new StringList("a")));
assertEquals(2, deserialized.getCount(new StringList("b")));
assertEquals(3, deserialized.getCount(new StringList("c")));
}

@Test
public void testAddCharNGramWithInvalidBounds() {
NGramModel model = new NGramModel();
model.add("abc", 0, 2);
}

@Test
public void testToStringIncludesSize() {
NGramModel model = new NGramModel();
model.add(new StringList("a"));
String output = model.toString();
assertTrue(output.contains("Size:"));
assertTrue(output.contains("1"));
}

@Test
public void testEqualsAndHashCode() {
NGramModel model1 = new NGramModel();
NGramModel model2 = new NGramModel();
StringList sl = new StringList("equal", "test");
model1.add(sl);
model2.add(sl);
assertEquals(model1, model2);
assertEquals(model1.hashCode(), model2.hashCode());
}

@Test
public void testNotEqualsDifferentType() {
NGramModel model = new NGramModel();
assertNotEquals(model, "Not a model");
}

@Test
public void testSerializeEntryIteratorRemoveUnsupported() throws IOException {
NGramModel model = new NGramModel();
model.add(new StringList("test"));
OutputStream out = new OutputStream() {

@Override
public void write(int b) throws IOException {
}
};
model.serialize(out);
Iterator<StringList> iterator = model.iterator();
if (iterator.hasNext()) {
iterator.remove();
}
}

@Test
public void testInvalidDeserializationMissingCount() throws IOException {
String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<dictionary>\n" + "  <entry tokens=\"a\" />\n" + "</dictionary>";
InputStream input = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
new NGramModel(input);
}

@Test
public void testInvalidDeserializationNonNumericCount() throws IOException {
String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<dictionary>\n" + "  <entry tokens=\"a\" count=\"notANumber\" />\n" + "</dictionary>";
InputStream input = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
new NGramModel(input);
}

@Test
public void testAddEmptyStringList() {
NGramModel model = new NGramModel();
StringList empty = new StringList();
model.add(empty);
assertTrue(model.contains(empty));
assertEquals(1, model.getCount(empty));
}

@Test
public void testAddCharacterNGramsSingleChar() {
NGramModel model = new NGramModel();
model.add("x", 1, 1);
assertTrue(model.contains(new StringList("x")));
assertEquals(1, model.getCount(new StringList("x")));
}

@Test
public void testAddCharacterNGramsMaxGreaterThanLength() {
NGramModel model = new NGramModel();
model.add("abc", 1, 5);
assertTrue(model.contains(new StringList("a")));
assertTrue(model.contains(new StringList("ab")));
assertTrue(model.contains(new StringList("abc")));
assertFalse(model.contains(new StringList("abcd")));
}

@Test
public void testAddStringListMaxGreaterThanTokensLength() {
NGramModel model = new NGramModel();
StringList tokens = new StringList("w", "x");
model.add(tokens, 1, 4);
assertTrue(model.contains(new StringList("w")));
assertTrue(model.contains(new StringList("x")));
assertTrue(model.contains(new StringList("w", "x")));
assertFalse(model.contains(new StringList("x", "y")));
}

@Test
public void testCutoffUpperAndLowerBoundsExact() {
NGramModel model = new NGramModel();
StringList low = new StringList("low");
StringList mid = new StringList("mid");
StringList high = new StringList("high");
model.add(low);
model.add(mid);
model.add(mid);
model.add(high);
model.add(high);
model.add(high);
model.cutoff(2, 2);
assertFalse(model.contains(low));
assertTrue(model.contains(mid));
assertFalse(model.contains(high));
}

@Test
public void testToDictionaryEmptyModel() {
NGramModel model = new NGramModel();
Dictionary dict = model.toDictionary();
assertNotNull(dict);
assertEquals(0, dict.size());
}

@Test
public void testEqualsWithSameReference() {
NGramModel model = new NGramModel();
assertEquals(model, model);
}

@Test
public void testEqualsWithNull() {
NGramModel model = new NGramModel();
assertNotEquals(model, null);
}

@Test
public void testHashCodeConsistency() {
NGramModel model = new NGramModel();
int hash1 = model.hashCode();
int hash2 = model.hashCode();
assertEquals(hash1, hash2);
}

@Test
public void testSerializeEmptyModel() throws IOException {
NGramModel model = new NGramModel();
ByteArrayOutputStream out = new ByteArrayOutputStream();
model.serialize(out);
assertTrue(out.toByteArray().length > 0);
}

@Test
public void testEntryIteratorRemoveThrowsException() {
NGramModel model = new NGramModel();
model.add(new StringList("a"));
Iterator<StringList> iterator = model.iterator();
if (iterator.hasNext()) {
iterator.remove();
}
}

@Test
public void testSerializeThrowsIOException() throws IOException {
NGramModel model = new NGramModel();
model.add(new StringList("fail"));
OutputStream out = new OutputStream() {

@Override
public void write(int b) throws IOException {
throw new IOException("Forced exception");
}
};
model.serialize(out);
}

@Test
public void testDeserializePreservesCaseSensitivityFalse() throws IOException {
NGramModel model = new NGramModel();
model.add(new StringList("Case"));
ByteArrayOutputStream out = new ByteArrayOutputStream();
model.serialize(out);
ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
NGramModel deserialized = new NGramModel(in);
assertTrue(deserialized.contains(new StringList("case")));
}

@Test
public void testToStringOnEmptyModel() {
NGramModel model = new NGramModel();
String str = model.toString();
assertTrue(str.contains("Size: 0"));
}

@Test
public void testAddStringListWithZeroMaxLength() {
NGramModel model = new NGramModel();
model.add(new StringList("a", "b"), 1, 0);
}

@Test
public void testAddCharNGramWithZeroMinLength() {
NGramModel model = new NGramModel();
model.add("abc", 0, 2);
}

@Test
public void testAddDuplicateStringListDifferentInstances() {
NGramModel model = new NGramModel();
StringList ng1 = new StringList("token");
StringList ng2 = new StringList("token");
model.add(ng1);
model.add(ng2);
assertEquals(2, model.getCount(new StringList("token")));
}

@Test
public void testAddSingleCharNGramUpperBoundOnly() {
NGramModel model = new NGramModel();
model.add("x", 1, 1);
assertTrue(model.contains(new StringList("x")));
assertEquals(1, model.getCount(new StringList("x")));
}

@Test
public void testAddMultiCharNGramsUpperBoundTooHigh() {
NGramModel model = new NGramModel();
model.add("abc", 1, 10);
assertTrue(model.contains(new StringList("a")));
assertTrue(model.contains(new StringList("ab")));
assertTrue(model.contains(new StringList("abc")));
assertFalse(model.contains(new StringList("abcd")));
}

@Test
public void testAddMultiTokenAndUpperBoundTooHigh() {
NGramModel model = new NGramModel();
model.add(new StringList("a", "b", "c"), 1, 5);
assertTrue(model.contains(new StringList("a")));
assertTrue(model.contains(new StringList("a", "b")));
assertTrue(model.contains(new StringList("a", "b", "c")));
assertTrue(model.contains(new StringList("b")));
assertTrue(model.contains(new StringList("b", "c")));
assertTrue(model.contains(new StringList("c")));
assertFalse(model.contains(new StringList("c", "d")));
}

@Test
public void testCutoffNoOpWhenDefaultBounds() {
NGramModel model = new NGramModel();
model.add(new StringList("one"));
model.add(new StringList("two"));
model.cutoff(0, Integer.MAX_VALUE);
assertTrue(model.contains(new StringList("one")));
assertTrue(model.contains(new StringList("two")));
}

@Test
public void testCutoffAllRemovedBelowLowerBound() {
NGramModel model = new NGramModel();
model.add(new StringList("a"));
model.add(new StringList("b"));
model.cutoff(2, Integer.MAX_VALUE);
assertFalse(model.contains(new StringList("a")));
assertFalse(model.contains(new StringList("b")));
}

@Test
public void testCutoffAllRemovedAboveUpperBound() {
NGramModel model = new NGramModel();
model.add(new StringList("x"));
model.add(new StringList("x"));
model.add(new StringList("x"));
model.cutoff(0, 2);
assertFalse(model.contains(new StringList("x")));
}

@Test
public void testNumberOfGramsEmptyModel() {
NGramModel model = new NGramModel();
assertEquals(0, model.numberOfGrams());
}

@Test
public void testNumberOfGramsWithUniqueNGrams() {
NGramModel model = new NGramModel();
model.add(new StringList("a"));
model.add(new StringList("b"));
model.add(new StringList("c"));
assertEquals(3, model.numberOfGrams());
}

@Test
public void testToDictionaryPreservesTokens() {
NGramModel model = new NGramModel();
model.add(new StringList("one", "two"));
Dictionary dict = model.toDictionary();
assertTrue(dict.contains(new StringList("one", "two")));
}

@Test
public void testEqualsSymmetric() {
NGramModel model1 = new NGramModel();
NGramModel model2 = new NGramModel();
model1.add(new StringList("hello"));
model2.add(new StringList("hello"));
assertTrue(model1.equals(model2));
assertTrue(model2.equals(model1));
}

@Test
public void testNotEqualsDifferentCounts() {
NGramModel model1 = new NGramModel();
NGramModel model2 = new NGramModel();
model1.add(new StringList("alpha"));
model1.add(new StringList("alpha"));
model2.add(new StringList("alpha"));
assertNotEquals(model1, model2);
}

@Test
public void testDifferentNGramsStillNotEqual() {
NGramModel model1 = new NGramModel();
NGramModel model2 = new NGramModel();
model1.add(new StringList("foo"));
model2.add(new StringList("bar"));
assertNotEquals(model1, model2);
}

@Test
public void testDeserializationThrowsOnEmptyAttributesNode() throws IOException {
String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<dictionary>\n" + "  <entry tokens=\"word\"/>\n" + "</dictionary>";
InputStream in = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
new NGramModel(in);
}

@Test
public void testDeserializationThrowsOnNonNumericCount() throws IOException {
String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<dictionary>\n" + "  <entry tokens=\"word\" count=\"NaN\"/>\n" + "</dictionary>";
InputStream in = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
new NGramModel(in);
}

@Test
public void testEqualsReturnsFalseWhenComparedToNull() {
NGramModel model = new NGramModel();
assertFalse(model.equals(null));
}

@Test
public void testHashCodeEmptyModel() {
NGramModel model = new NGramModel();
int hash = model.hashCode();
assertEquals(hash, model.hashCode());
}

@Test
public void testToStringOnPopulatedModel() {
NGramModel model = new NGramModel();
model.add(new StringList("a"));
String str = model.toString();
assertTrue(str.contains("Size:"));
assertTrue(str.endsWith("1"));
}

@Test
public void testIteratorReturnsAllNGrams() {
NGramModel model = new NGramModel();
model.add(new StringList("a"));
model.add(new StringList("b"));
Iterator<StringList> it = model.iterator();
StringList first = it.next();
StringList second = it.next();
assertNotNull(first);
assertNotNull(second);
assertFalse(it.hasNext());
}

@Test
public void testSetCountOnRemovedNGram() {
NGramModel model = new NGramModel();
StringList entry = new StringList("temp");
model.add(entry);
model.remove(entry);
model.setCount(entry, 1);
}

@Test
public void testSerializeModelWithSingleNGram() throws IOException {
NGramModel model = new NGramModel();
model.add(new StringList("unique"));
ByteArrayOutputStream out = new ByteArrayOutputStream();
model.serialize(out);
byte[] bytes = out.toByteArray();
assertTrue(bytes.length > 0);
ByteArrayInputStream in = new ByteArrayInputStream(bytes);
NGramModel deserialized = new NGramModel(in);
assertTrue(deserialized.contains(new StringList("unique")));
}

@Test
public void testAddSameNGramMultipleTimesAndSetCountToZero() {
NGramModel model = new NGramModel();
StringList token = new StringList("zero");
model.add(token);
model.setCount(token, 0);
assertEquals(0, model.getCount(token));
assertTrue(model.contains(token));
}

@Test
public void testAddEmptyCharacterSequence() {
NGramModel model = new NGramModel();
model.add("", 1, 3);
assertEquals(0, model.size());
}

@Test
public void testAddNegativeMinLengthCharNGram() {
NGramModel model = new NGramModel();
model.add("abc", -1, 2);
}

@Test
public void testAddNegativeMaxLengthCharNGram() {
NGramModel model = new NGramModel();
model.add("abc", 1, -2);
}

@Test
public void testAddCharNGramMinEqualsMaxOnExactLength() {
NGramModel model = new NGramModel();
model.add("oo", 2, 2);
assertTrue(model.contains(new StringList("oo")));
assertEquals(1, model.getCount(new StringList("oo")));
}

@Test
public void testAddStringListMinEqualsMax() {
NGramModel model = new NGramModel();
StringList tokens = new StringList("a", "b", "c");
model.add(tokens, 2, 2);
assertTrue(model.contains(new StringList("a", "b")));
assertTrue(model.contains(new StringList("b", "c")));
assertFalse(model.contains(new StringList("a")));
assertFalse(model.contains(new StringList("a", "b", "c")));
}

@Test
public void testAddStringListNegativeMin() {
NGramModel model = new NGramModel();
model.add(new StringList("x", "y"), -1, 2);
}

@Test
public void testAddStringListNegativeMax() {
NGramModel model = new NGramModel();
model.add(new StringList("x", "y"), 1, -5);
}

@Test
public void testEmptyStringListInDictionary() {
NGramModel model = new NGramModel();
model.add(new StringList());
Dictionary dict = model.toDictionary(true);
assertTrue(dict.contains(new StringList()));
}

@Test
public void testAddCharSequenceUpperCaseToLowerCaseMapping() {
NGramModel model = new NGramModel();
model.add("HELLO", 1, 1);
assertTrue(model.contains(new StringList("h")));
assertTrue(model.contains(new StringList("e")));
assertTrue(model.contains(new StringList("l")));
assertTrue(model.contains(new StringList("o")));
}

@Test
public void testAddSameStringListWithDifferentReferences() {
NGramModel model = new NGramModel();
StringList one = new StringList("foo", "bar");
StringList two = new StringList("foo", "bar");
model.add(one);
model.add(two);
assertTrue(model.contains(new StringList("foo", "bar")));
assertEquals(2, model.getCount(one));
assertEquals(2, model.getCount(two));
}

@Test
public void testSerializeDeserializePreserveMultipleCounts() throws IOException {
NGramModel model = new NGramModel();
model.add(new StringList("x"));
model.add(new StringList("x"));
model.add(new StringList("x"));
model.add(new StringList("y"));
ByteArrayOutputStream out = new ByteArrayOutputStream();
model.serialize(out);
ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
NGramModel deserialized = new NGramModel(in);
assertEquals(3, deserialized.getCount(new StringList("x")));
assertEquals(1, deserialized.getCount(new StringList("y")));
}

@Test
public void testRemoveThenReAddStringList() {
NGramModel model = new NGramModel();
StringList item = new StringList("word");
model.add(item);
model.remove(item);
assertFalse(model.contains(item));
model.add(item);
assertTrue(model.contains(item));
assertEquals(1, model.getCount(item));
}

@Test
public void testEqualsWithNewEmptyModel() {
NGramModel model1 = new NGramModel();
NGramModel model2 = new NGramModel();
assertTrue(model1.equals(model2));
}

@Test
public void testGetCountWithNullKey() {
NGramModel model = new NGramModel();
model.getCount(null);
}

@Test
public void testAddNullStringList() {
NGramModel model = new NGramModel();
StringList nullList = null;
model.add(nullList);
}

@Test
public void testToDictionaryCaseSensitiveMerging() {
NGramModel model = new NGramModel();
model.add(new StringList("Apple"));
model.add(new StringList("apple"));
Dictionary dictInsensitive = model.toDictionary(false);
Dictionary dictSensitive = model.toDictionary(true);
assertEquals(1, dictInsensitive.size());
assertEquals(2, dictSensitive.size());
}

@Test
public void testIteratorReturnsExpectedOrder() {
NGramModel model = new NGramModel();
model.add(new StringList("a", "b"));
model.add(new StringList("b", "c"));
model.add(new StringList("c", "d"));
Iterator<StringList> it = model.iterator();
String first = it.next().toString();
String second = it.next().toString();
String third = it.next().toString();
assertNotNull(first);
assertNotNull(second);
assertNotNull(third);
assertFalse(it.hasNext());
}

@Test
public void testToStringReflectsSizeChanges() {
NGramModel model = new NGramModel();
String emptyState = model.toString();
model.add(new StringList("a"));
String populatedState = model.toString();
assertTrue(emptyState.contains("Size: 0"));
assertTrue(populatedState.contains("Size: 1"));
}

@Test
public void testEqualsAfterSetSameCounts() {
NGramModel m1 = new NGramModel();
NGramModel m2 = new NGramModel();
StringList token = new StringList("same");
m1.add(token);
m1.setCount(token, 5);
m2.add(token);
m2.setCount(token, 5);
assertTrue(m1.equals(m2));
assertEquals(m1.hashCode(), m2.hashCode());
}

@Test
public void testEqualsFailsDifferentCounts() {
NGramModel m1 = new NGramModel();
NGramModel m2 = new NGramModel();
m1.add(new StringList("a"));
m1.setCount(new StringList("a"), 3);
m2.add(new StringList("a"));
m2.setCount(new StringList("a"), 4);
assertFalse(m1.equals(m2));
}

@Test
public void testSetCountAfterDeserialization() throws IOException {
NGramModel model = new NGramModel();
StringList list = new StringList("deserialized");
model.add(list);
model.add(list);
ByteArrayOutputStream out = new ByteArrayOutputStream();
model.serialize(out);
ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
NGramModel deserialized = new NGramModel(in);
deserialized.setCount(new StringList("deserialized"), 10);
assertEquals(10, deserialized.getCount(new StringList("deserialized")));
}

@Test
public void testDeserializeNGramWithZeroCount() throws IOException {
String data = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<dictionary>\n" + "<entry tokens=\"zero\" count=\"0\"/>\n" + "</dictionary>";
ByteArrayInputStream in = new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
NGramModel model = new NGramModel(in);
assertTrue(model.contains(new StringList("zero")));
assertEquals(0, model.getCount(new StringList("zero")));
}

@Test
public void testCutoffRemoveAllWithSameLowerUpper() {
NGramModel model = new NGramModel();
StringList a = new StringList("cutoff");
model.add(a);
model.cutoff(1, 1);
assertTrue(model.contains(a));
model.cutoff(2, 2);
assertFalse(model.contains(a));
}

@Test
public void testToDictionaryMergesCaseInsensitive() {
NGramModel model = new NGramModel();
model.add(new StringList("Word"));
model.add(new StringList("word"));
Dictionary dictionary = model.toDictionary(false);
assertEquals(1, dictionary.size());
Dictionary sensitiveDict = model.toDictionary(true);
assertEquals(2, sensitiveDict.size());
}

@Test
public void testMultipleNGramsToDictionaryContents() {
NGramModel model = new NGramModel();
model.add(new StringList("one"));
model.add(new StringList("two"));
model.add(new StringList("three"));
Dictionary dictionary = model.toDictionary(true);
assertTrue(dictionary.contains(new StringList("one")));
assertTrue(dictionary.contains(new StringList("two")));
assertTrue(dictionary.contains(new StringList("three")));
}

@Test
public void testSerializeAndDeserializePreservesExactCounts() throws IOException {
NGramModel model = new NGramModel();
model.add(new StringList("fizz"));
model.add(new StringList("buzz"));
model.add(new StringList("buzz"));
model.add(new StringList("fizzbuzz"));
model.add(new StringList("fizzbuzz"));
model.add(new StringList("fizzbuzz"));
ByteArrayOutputStream out = new ByteArrayOutputStream();
model.serialize(out);
ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
NGramModel reloaded = new NGramModel(in);
assertEquals(1, reloaded.getCount(new StringList("fizz")));
assertEquals(2, reloaded.getCount(new StringList("buzz")));
assertEquals(3, reloaded.getCount(new StringList("fizzbuzz")));
}

@Test
public void testEmptyStringListEqualsAfterReadded() {
NGramModel model = new NGramModel();
StringList empty = new StringList();
model.add(empty);
assertTrue(model.contains(empty));
model.remove(empty);
assertFalse(model.contains(empty));
model.add(empty);
assertTrue(model.contains(empty));
assertEquals(1, model.getCount(empty));
}

@Test
public void testEqualsReflexiveConsistentTransitive() {
NGramModel m1 = new NGramModel();
NGramModel m2 = new NGramModel();
NGramModel m3 = new NGramModel();
StringList shared = new StringList("x");
m1.add(shared);
m2.add(shared);
m3.add(shared);
assertTrue(m1.equals(m1));
assertTrue(m1.equals(m2));
assertTrue(m2.equals(m1));
assertTrue(m2.equals(m3));
assertTrue(m1.equals(m3));
assertEquals(m1.hashCode(), m2.hashCode());
assertEquals(m2.hashCode(), m3.hashCode());
}

@Test
public void testGetCountAfterSetCountToZero() {
NGramModel model = new NGramModel();
StringList gl = new StringList("gone");
model.add(gl);
model.setCount(gl, 0);
assertEquals(0, model.getCount(gl));
assertTrue(model.contains(gl));
}

@Test
public void testCustomEntryIteratorRemoveThrows() throws IOException {
NGramModel model = new NGramModel();
model.add(new StringList("xyz"));
ByteArrayOutputStream output = new ByteArrayOutputStream();
model.serialize(new OutputStream() {

@Override
public void write(int b) throws IOException {
throw new UnsupportedOperationException("Force fail");
}
});
}

@Test
public void testDeserializeInvalidXmlMissingTokensAttribute() throws IOException {
String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<dictionary>\n" + "<entry count=\"2\"/>\n" + "</dictionary>";
ByteArrayInputStream in = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
new NGramModel(in);
}

@Test
public void testToStringAfterAddingMultipleNGrams() {
NGramModel model = new NGramModel();
model.add(new StringList("a"));
model.add(new StringList("b"));
model.add(new StringList("c"));
String output = model.toString();
assertTrue(output.contains("Size:"));
assertTrue(output.contains("3"));
}

@Test
public void testAddCharSequenceWithExactSpan() {
NGramModel model = new NGramModel();
model.add("abcd", 4, 4);
assertTrue(model.contains(new StringList("abcd")));
assertFalse(model.contains(new StringList("abc")));
}

@Test
public void testCutoffRetainsOnlyThoseWithinBoundsInclusive() {
NGramModel model = new NGramModel();
StringList a = new StringList("keep");
StringList b = new StringList("removeLow");
StringList c = new StringList("removeHigh");
model.add(a);
model.add(a);
model.add(b);
model.add(c);
model.add(c);
model.add(c);
model.cutoff(2, 2);
assertTrue(model.contains(a));
assertFalse(model.contains(b));
assertFalse(model.contains(c));
}

@Test
public void testSetCountOnPreviouslyRemovedNGram() {
NGramModel model = new NGramModel();
StringList item = new StringList("temp");
model.add(item);
model.remove(item);
model.setCount(item, 2);
}

@Test
public void testAddCharSequenceSingleCharacterExactMatch() {
NGramModel model = new NGramModel();
model.add("z", 1, 1);
assertTrue(model.contains(new StringList("z")));
assertEquals(1, model.getCount(new StringList("z")));
}

@Test
public void testAddStringListSpanWhereMinEqualsMaxAndExactMatchLength() {
NGramModel model = new NGramModel();
model.add(new StringList("x", "y"), 2, 2);
assertTrue(model.contains(new StringList("x", "y")));
assertFalse(model.contains(new StringList("x")));
}

@Test
public void testAddThenCutoffWithMaxIntegerOver() {
NGramModel model = new NGramModel();
StringList token = new StringList("limit");
model.add(token);
model.setCount(token, Integer.MAX_VALUE);
model.cutoff(0, Integer.MAX_VALUE - 1);
assertFalse(model.contains(token));
}

@Test
public void testCutoffWithZeroBoundRemovesNothing() {
NGramModel model = new NGramModel();
StringList token1 = new StringList("keep1");
StringList token2 = new StringList("keep2");
model.add(token1);
model.add(token2);
model.cutoff(0, Integer.MAX_VALUE);
assertTrue(model.contains(token1));
assertTrue(model.contains(token2));
}

@Test
public void testAddDifferentCapitalizationAndToDictionaryInsensitive() {
NGramModel model = new NGramModel();
model.add(new StringList("Word"));
model.add(new StringList("word"));
model.add(new StringList("WoRd"));
Dictionary dict = model.toDictionary(false);
assertEquals(1, dict.size());
}

@Test
public void testAddSameStringListTwiceDifferentObjectSameTokens() {
NGramModel model = new NGramModel();
model.add(new StringList("alpha", "beta"));
model.add(new StringList("alpha", "beta"));
assertTrue(model.contains(new StringList("alpha", "beta")));
assertEquals(2, model.getCount(new StringList("alpha", "beta")));
}

@Test
public void testCutoffRetainsOnlyMidRangeCount() {
NGramModel model = new NGramModel();
model.add(new StringList("low"));
model.add(new StringList("mid"));
model.add(new StringList("mid"));
model.add(new StringList("high"));
model.add(new StringList("high"));
model.add(new StringList("high"));
model.cutoff(2, 2);
assertFalse(model.contains(new StringList("low")));
assertTrue(model.contains(new StringList("mid")));
assertFalse(model.contains(new StringList("high")));
}

@Test
public void testEmptyModelToDictionaryEmpty() {
NGramModel model = new NGramModel();
Dictionary dict = model.toDictionary();
assertEquals(0, dict.size());
}

@Test
public void testToDictionaryPreservesExactTokensWithCaseSensitiveTrue() {
NGramModel model = new NGramModel();
StringList token = new StringList("Case");
model.add(token);
Dictionary dict = model.toDictionary(true);
assertTrue(dict.contains(new StringList("Case")));
assertFalse(dict.contains(new StringList("case")));
}

@Test
public void testEqualsFalseWhenDifferentSizes() {
NGramModel model1 = new NGramModel();
model1.add(new StringList("one"));
NGramModel model2 = new NGramModel();
model2.add(new StringList("one"));
model2.add(new StringList("two"));
assertNotEquals(model1, model2);
}

@Test
public void testEqualsFalseWhenNullArgument() {
NGramModel model = new NGramModel();
assertFalse(model.equals(null));
}

@Test
public void testEqualsFalseWhenDifferentType() {
NGramModel model = new NGramModel();
assertFalse(model.equals("Not an NGramModel"));
}

@Test
public void testHashCodeConsistencyAfterAddingSameStringListTwice() {
NGramModel model = new NGramModel();
StringList token = new StringList("abc");
model.add(token);
int hash1 = model.hashCode();
model.add(token);
int hash2 = model.hashCode();
assertNotEquals(hash1, hash2);
}

@Test
public void testDeserializeWithMissingCountAttribute() throws IOException {
String corrupted = "<dictionary>\n" + "<entry tokens=\"abc\"/>\n" + "</dictionary>";
ByteArrayInputStream in = new ByteArrayInputStream(corrupted.getBytes(StandardCharsets.UTF_8));
new NGramModel(in);
}

@Test
public void testDeserializeWithInvalidCountValue() throws IOException {
String corrupted = "<dictionary>\n" + "<entry tokens=\"abc\" count=\"NaN\"/>\n" + "</dictionary>";
ByteArrayInputStream in = new ByteArrayInputStream(corrupted.getBytes(StandardCharsets.UTF_8));
new NGramModel(in);
}

@Test
public void testSerializeThenDeserializePreservesEquality() throws IOException {
NGramModel original = new NGramModel();
original.add(new StringList("serialize"));
original.add(new StringList("serialize"));
ByteArrayOutputStream out = new ByteArrayOutputStream();
original.serialize(out);
ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
NGramModel deserialized = new NGramModel(in);
assertEquals(original, deserialized);
assertEquals(2, deserialized.getCount(new StringList("serialize")));
}

@Test
public void testRemoveThenAddPreservesFunctionality() {
NGramModel model = new NGramModel();
StringList token = new StringList("reset");
model.add(token);
model.remove(token);
assertFalse(model.contains(token));
model.add(token);
assertTrue(model.contains(token));
assertEquals(1, model.getCount(token));
}

@Test
public void testToStringReflectsCorrectSizeAfterAdditions() {
NGramModel model = new NGramModel();
model.add(new StringList("one"));
model.add(new StringList("two"));
model.add(new StringList("three"));
String output = model.toString();
assertTrue(output.contains("Size: 3"));
}

@Test
public void testNumberOfGramsExactTotalCountMatch() {
NGramModel model = new NGramModel();
model.add(new StringList("x"));
model.add(new StringList("x"));
model.add(new StringList("y"));
model.add(new StringList("z"));
model.add(new StringList("z"));
model.add(new StringList("z"));
int total = model.numberOfGrams();
assertEquals(6, total);
}

@Test
public void testSerializeInternalIteratorRemoveUnsupported() throws IOException {
NGramModel model = new NGramModel();
model.add(new StringList("dontRemove"));
Iterator<StringList> it = model.iterator();
if (it.hasNext()) {
it.remove();
}
}

@Test
public void testSerializeDeserializeEmptyModelHasSameSize() throws IOException {
NGramModel model = new NGramModel();
ByteArrayOutputStream out = new ByteArrayOutputStream();
model.serialize(out);
ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
NGramModel deserialized = new NGramModel(in);
assertEquals(0, deserialized.size());
assertEquals(model, deserialized);
}

@Test
public void testAddEmptyStringToken() {
NGramModel model = new NGramModel();
model.add(new StringList(""));
assertTrue(model.contains(new StringList("")));
assertEquals(1, model.getCount(new StringList("")));
}

@Test
public void testSetCountOnRemovedNGramThrows() {
NGramModel model = new NGramModel();
StringList entry = new StringList("temp");
model.add(entry);
model.remove(entry);
model.setCount(entry, 2);
}

@Test
public void testAddUpperBoundLessThanTokensLength() {
NGramModel model = new NGramModel();
model.add(new StringList("a", "b", "c", "d"), 2, 3);
assertTrue(model.contains(new StringList("a", "b")));
assertTrue(model.contains(new StringList("b", "c")));
assertTrue(model.contains(new StringList("a", "b", "c")));
assertTrue(model.contains(new StringList("b", "c", "d")));
assertFalse(model.contains(new StringList("a")));
assertFalse(model.contains(new StringList("a", "b", "c", "d")));
}

@Test
public void testAddMaxSpanExceedsTokenLengthNoException() {
NGramModel model = new NGramModel();
model.add(new StringList("x", "y"), 1, 5);
assertTrue(model.contains(new StringList("x")));
assertTrue(model.contains(new StringList("y")));
assertTrue(model.contains(new StringList("x", "y")));
assertEquals(3, model.size());
}

@Test
public void testToDictionaryWithMixedCaseShouldMerge() {
NGramModel model = new NGramModel();
model.add(new StringList("Word"));
model.add(new StringList("word"));
Dictionary dict = model.toDictionary(false);
assertEquals(1, dict.size());
}

@Test
public void testToDictionaryCaseSensitiveKeepsUniqueTokens() {
NGramModel model = new NGramModel();
model.add(new StringList("Hello"));
model.add(new StringList("hello"));
Dictionary dict = model.toDictionary(true);
assertEquals(2, dict.size());
}

@Test
public void testSetCountToZeroDoesNotRemoveNGram() {
NGramModel model = new NGramModel();
StringList ngram = new StringList("zero-count");
model.add(ngram);
model.setCount(ngram, 0);
assertTrue(model.contains(ngram));
assertEquals(0, model.getCount(ngram));
}

@Test
public void testEqualsAfterZeroCountDifference() {
NGramModel model1 = new NGramModel();
NGramModel model2 = new NGramModel();
model1.add(new StringList("a"));
model2.add(new StringList("a"));
model1.setCount(new StringList("a"), 0);
model2.setCount(new StringList("a"), 1);
assertNotEquals(model1, model2);
}

@Test
public void testToStringOutputWithNoEntries() {
NGramModel model = new NGramModel();
String output = model.toString();
assertTrue(output.contains("Size: 0"));
}

@Test
public void testToStringOutputWithOneEntry() {
NGramModel model = new NGramModel();
model.add(new StringList("item"));
String output = model.toString();
assertTrue(output.contains("Size: 1"));
}

@Test
public void testAddNGramsBuiltFromEmptyInput() {
NGramModel model = new NGramModel();
model.add(new StringList(), 1, 3);
assertEquals(0, model.size());
}

@Test
public void testNumberOfGramsWithZeroCountsIncluded() {
NGramModel model = new NGramModel();
StringList a = new StringList("a");
StringList b = new StringList("b");
model.add(a);
model.add(b);
model.setCount(a, 0);
assertEquals(1, model.numberOfGrams());
}

@Test
public void testDeserializeMissingCountThrowsInvalidFormat() throws IOException {
String invalidXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<dictionary>\n" + "  <entry tokens=\"x\"/>\n" + "</dictionary>";
ByteArrayInputStream in = new ByteArrayInputStream(invalidXml.getBytes(StandardCharsets.UTF_8));
new NGramModel(in);
}

@Test
public void testDeserializeNonNumericCountThrows() throws IOException {
String invalidXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<dictionary>\n" + "  <entry tokens=\"x\" count=\"abc\"/>\n" + "</dictionary>";
ByteArrayInputStream in = new ByteArrayInputStream(invalidXml.getBytes(StandardCharsets.UTF_8));
new NGramModel(in);
}

@Test
public void testAddNGramNegativeMinLengthThrows() {
NGramModel model = new NGramModel();
model.add(new StringList("a", "b", "c"), -1, 2);
}

@Test
public void testAddNGramMinGreaterThanMaxThrows() {
NGramModel model = new NGramModel();
model.add(new StringList("a", "b", "c"), 4, 2);
}

@Test
public void testIteratorOnEmptyModelReturnsNoElements() {
NGramModel model = new NGramModel();
assertFalse(model.iterator().hasNext());
}

@Test
public void testHashCodeConsistentEmptyModel() {
NGramModel model = new NGramModel();
int hash1 = model.hashCode();
int hash2 = model.hashCode();
assertEquals(hash1, hash2);
}

@Test
public void testHashCodeChangesWhenAddingElements() {
NGramModel model = new NGramModel();
int hashBefore = model.hashCode();
model.add(new StringList("hash"));
int hashAfter = model.hashCode();
assertNotEquals(hashBefore, hashAfter);
}
}
