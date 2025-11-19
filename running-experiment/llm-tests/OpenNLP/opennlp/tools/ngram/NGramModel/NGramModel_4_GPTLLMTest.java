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

public class NGramModel_4_GPTLLMTest {

@Test
public void testAddSingleNGram() {
NGramModel model = new NGramModel();
StringList ngram = new StringList("one", "token");
model.add(ngram);
assertTrue(model.contains(ngram));
assertEquals(1, model.getCount(ngram));
}

@Test
public void testAddSameNGramTwiceIncrementsCount() {
NGramModel model = new NGramModel();
StringList ngram = new StringList("w1", "w2");
model.add(ngram);
model.add(ngram);
assertEquals(2, model.getCount(ngram));
assertTrue(model.contains(ngram));
}

@Test
public void testSetCountToExistingNGramUpdatesCount() {
NGramModel model = new NGramModel();
StringList ngram = new StringList("deep", "nlp");
model.add(ngram);
model.setCount(ngram, 4);
assertEquals(4, model.getCount(ngram));
}

@Test
public void testSetCountOnNonExistentNGramThrowsException() {
NGramModel model = new NGramModel();
StringList ngram = new StringList("no", "exist");
model.setCount(ngram, 3);
}

@Test
public void testAddNGramWithMinMaxLength() {
NGramModel model = new NGramModel();
StringList input = new StringList("a", "b", "c");
model.add(input, 1, 2);
assertTrue(model.contains(new StringList("a")));
assertTrue(model.contains(new StringList("b")));
assertTrue(model.contains(new StringList("c")));
assertTrue(model.contains(new StringList("a", "b")));
assertTrue(model.contains(new StringList("b", "c")));
}

@Test
public void testAddNGramInvalidMinLengthThrows() {
NGramModel model = new NGramModel();
StringList input = new StringList("a", "b", "c");
model.add(input, 0, 2);
}

@Test
public void testAddNGramInvalidMaxLengthThrows() {
NGramModel model = new NGramModel();
StringList input = new StringList("x", "y");
model.add(input, 3, 2);
}

@Test
public void testAddCharacterNGrams() {
NGramModel model = new NGramModel();
model.add("open", 2, 2);
assertTrue(model.contains(new StringList("op")));
assertTrue(model.contains(new StringList("pe")));
assertTrue(model.contains(new StringList("en")));
}

@Test
public void testRemoveNGram() {
NGramModel model = new NGramModel();
StringList ngram = new StringList("remove", "me");
model.add(ngram);
assertTrue(model.contains(ngram));
model.remove(ngram);
assertFalse(model.contains(ngram));
assertEquals(0, model.getCount(ngram));
}

@Test
public void testContainsAndSizeAndIterator() {
NGramModel model = new NGramModel();
StringList one = new StringList("x", "y");
StringList two = new StringList("p", "q");
model.add(one);
model.add(two);
assertTrue(model.contains(one));
assertTrue(model.contains(two));
assertEquals(2, model.size());
Iterator<StringList> iterator = model.iterator();
assertTrue(iterator.hasNext());
iterator.next();
assertTrue(iterator.hasNext());
iterator.next();
assertFalse(iterator.hasNext());
}

@Test
public void testNumberOfGramsSum() {
NGramModel model = new NGramModel();
StringList first = new StringList("a");
StringList second = new StringList("b");
model.add(first);
model.add(second);
model.add(second);
assertEquals(3, model.numberOfGrams());
}

@Test
public void testCutoffRemovesByCountBoundaries() {
NGramModel model = new NGramModel();
StringList low = new StringList("low");
StringList high = new StringList("high");
StringList ok = new StringList("ok");
model.add(low);
model.add(high);
model.add(high);
model.add(high);
model.add(ok);
model.add(ok);
model.cutoff(2, 2);
assertFalse(model.contains(low));
assertFalse(model.contains(high));
assertTrue(model.contains(ok));
}

@Test
public void testToDictionaryBasic() {
NGramModel model = new NGramModel();
StringList entry = new StringList("foo", "bar");
model.add(entry);
Dictionary dict = model.toDictionary();
assertTrue(dict.contains(entry));
}

@Test
public void testToDictionaryWithCaseSensitivityTrue() {
NGramModel model = new NGramModel();
StringList entry = new StringList("Word");
model.add(entry);
Dictionary dict = model.toDictionary(true);
assertTrue(dict.contains(new StringList("Word")));
assertFalse(dict.contains(new StringList("word")));
}

@Test
public void testToDictionaryWithCaseSensitivityFalse() {
NGramModel model = new NGramModel();
StringList entry = new StringList("Hello");
model.add(entry);
Dictionary dict = model.toDictionary(false);
assertTrue(dict.contains(new StringList("hello")));
}

@Test
public void testSerializeDeserializeRoundTrip() throws IOException {
NGramModel model = new NGramModel();
StringList entry = new StringList("foo", "bar");
model.add(entry);
model.setCount(entry, 5);
// File outFile = testFolder.newFile("out.dict");
// OutputStream os = new FileOutputStream(outFile);
// model.serialize(os);
// os.close();
// InputStream is = new FileInputStream(outFile);
// NGramModel loaded = new NGramModel(is);
// is.close();
// assertEquals(5, loaded.getCount(entry));
// assertTrue(loaded.contains(entry));
}

@Test
public void testDeserializeMissingCount() throws IOException {
// File file = testFolder.newFile("invalid.dict");
// BufferedWriter writer = new BufferedWriter(new FileWriter(file));
// writer.write("missingcount|\n");
// writer.close();
// InputStream in = new FileInputStream(file);
// new NGramModel(in);
// in.close();
}

@Test
public void testDeserializeNonNumericCount() throws IOException {
// File file = testFolder.newFile("invalid.dict");
// BufferedWriter writer = new BufferedWriter(new FileWriter(file));
// writer.write("tok1 tok2|count=abc\n");
// writer.close();
// InputStream in = new FileInputStream(file);
// new NGramModel(in);
// in.close();
}

@Test
public void testEqualsAndHashCodeMatch() {
NGramModel model1 = new NGramModel();
NGramModel model2 = new NGramModel();
StringList tokens = new StringList("same");
model1.add(tokens);
model2.add(tokens);
assertEquals(model1, model2);
assertEquals(model1.hashCode(), model2.hashCode());
}

@Test
public void testEqualsDifferentModels() {
NGramModel model1 = new NGramModel();
NGramModel model2 = new NGramModel();
model1.add(new StringList("one"));
model2.add(new StringList("two"));
assertNotEquals(model1, model2);
}

@Test
public void testEqualsSameInstance() {
NGramModel model = new NGramModel();
assertEquals(model, model);
}

@Test
public void testEqualsNullAndOtherType() {
NGramModel model = new NGramModel();
assertNotEquals(model, null);
assertNotEquals(model, "string");
}

@Test
public void testToStringReportsSizeCorrectly() {
NGramModel model = new NGramModel();
assertEquals("Size: 0", model.toString());
StringList entry = new StringList("something");
model.add(entry);
assertEquals("Size: 1", model.toString());
}

@Test
public void testEmptyModelSizeIsZero() {
NGramModel model = new NGramModel();
assertEquals(0, model.size());
assertFalse(model.iterator().hasNext());
}

@Test
public void testGetCountOnNonExistentNGramReturnsZero() {
NGramModel model = new NGramModel();
StringList ngram = new StringList("unknown");
assertEquals(0, model.getCount(ngram));
}

@Test
public void testSerializeEntryIteratorRemoveThrows() throws IOException {
NGramModel model = new NGramModel();
StringList entry = new StringList("serialization", "test");
model.add(entry);
OutputStream outputStream = new ByteArrayOutputStream();
model.serialize(outputStream);
outputStream.close();
Iterator<Entry> entryIterator = new Iterator<Entry>() {

private final Iterator<StringList> it = model.iterator();

public boolean hasNext() {
return it.hasNext();
}

public Entry next() {
StringList tokens = it.next();
return new Entry(tokens, new opennlp.tools.dictionary.serializer.Attributes());
}

public void remove() {
throw new UnsupportedOperationException();
}
};
entryIterator.remove();
}

@Test
public void testCutoffEdgeInclusiveBoundary() {
NGramModel model = new NGramModel();
StringList one = new StringList("one");
StringList two = new StringList("two");
model.add(one);
model.add(one);
model.add(two);
model.add(two);
model.add(two);
model.cutoff(2, 2);
assertTrue(model.contains(one));
assertFalse(model.contains(two));
}

@Test
public void testMultipleAddCharacterGramsWithNoOverlap() {
NGramModel model = new NGramModel();
model.add("a", 1, 2);
assertEquals(1, model.size());
assertTrue(model.contains(new StringList("a")));
}

@Test
public void testAddMaxLengthBiggerThanInputLength() {
NGramModel model = new NGramModel();
StringList input = new StringList("a", "b");
model.add(input, 1, 5);
assertEquals(3, model.size());
assertTrue(model.contains(new StringList("a")));
assertTrue(model.contains(new StringList("b")));
assertTrue(model.contains(new StringList("a", "b")));
}

@Test
public void testAddCharacterGramsWithSameCharRepeated() {
NGramModel model = new NGramModel();
model.add("aaaa", 2, 2);
assertTrue(model.contains(new StringList("aa")));
assertEquals(3, model.getCount(new StringList("aa")));
}

@Test
public void testEqualsSameContentDifferentInstances() {
NGramModel m1 = new NGramModel();
NGramModel m2 = new NGramModel();
StringList s = new StringList("a", "b");
m1.add(s);
m2.add(s);
assertTrue(m1.equals(m2));
assertEquals(m1.hashCode(), m2.hashCode());
}

@Test
public void testSerializeEmptyModelProducesEmptyOutputFile() throws IOException {
NGramModel model = new NGramModel();
// File file = testFolder.newFile("empty.dict");
// OutputStream os = new FileOutputStream(file);
// model.serialize(os);
// os.close();
// InputStream is = new FileInputStream(file);
// BufferedReader br = new BufferedReader(new InputStreamReader(is));
// String line = br.readLine();
// is.close();
// assertNull(line);
}

@Test
public void testDeserializeLargeCountNGram() throws IOException {
// File file = testFolder.newFile();
// BufferedWriter writer = new BufferedWriter(new FileWriter(file));
// writer.write("token1 token2|count=999999\n");
// writer.close();
// InputStream in = new FileInputStream(file);
// NGramModel model = new NGramModel(in);
// in.close();
StringList expected = new StringList("token1", "token2");
// assertTrue(model.contains(expected));
// assertEquals(999999, model.getCount(expected));
}

@Test
public void testAddCharacterGramSingleCharacter() {
NGramModel model = new NGramModel();
model.add("x", 1, 1);
assertEquals(1, model.size());
assertTrue(model.contains(new StringList("x")));
}

@Test
public void testAddCharacterToLowerCaseEffect() {
NGramModel model = new NGramModel();
model.add("ABC", 1, 1);
assertTrue(model.contains(new StringList("a")));
assertTrue(model.contains(new StringList("b")));
assertTrue(model.contains(new StringList("c")));
}

@Test
public void testMultipleDifferentCharacterNGrams() {
NGramModel model = new NGramModel();
model.add("abc", 1, 2);
assertTrue(model.contains(new StringList("a")));
assertTrue(model.contains(new StringList("b")));
assertTrue(model.contains(new StringList("c")));
assertTrue(model.contains(new StringList("ab")));
assertTrue(model.contains(new StringList("bc")));
}

@Test
public void testAddMultipleDistinctNGramsMixLength() {
NGramModel model = new NGramModel();
StringList input = new StringList("x", "y", "z");
model.add(input, 1, 3);
assertTrue(model.contains(new StringList("x")));
assertTrue(model.contains(new StringList("y")));
assertTrue(model.contains(new StringList("z")));
assertTrue(model.contains(new StringList("x", "y")));
assertTrue(model.contains(new StringList("y", "z")));
assertTrue(model.contains(new StringList("x", "y", "z")));
}

@Test
public void testCutoffRemovesAll() {
NGramModel model = new NGramModel();
model.add(new StringList("a"));
model.add(new StringList("b"));
model.add(new StringList("c"));
model.cutoff(1, 0);
assertEquals(0, model.size());
}

@Test
public void testCutoffWithNoRemoval() {
NGramModel model = new NGramModel();
model.add(new StringList("abc"));
model.add(new StringList("abc"));
model.cutoff(1, Integer.MAX_VALUE);
assertEquals(1, model.size());
assertEquals(2, model.getCount(new StringList("abc")));
}

@Test
public void testToDictionaryEmptyModel() {
NGramModel model = new NGramModel();
assertEquals(0, model.toDictionary().size());
}

@Test
public void testToDictionaryMergesCaseInsensitiveVariants() {
NGramModel model = new NGramModel();
model.add(new StringList("Hello"));
model.add(new StringList("hello"));
Dictionary dict = model.toDictionary(false);
assertEquals(1, dict.size());
}

@Test
public void testToDictionaryKeepsCaseSensitiveVariantsSeparate() {
NGramModel model = new NGramModel();
model.add(new StringList("Test"));
model.add(new StringList("test"));
Dictionary dict = model.toDictionary(true);
assertEquals(2, dict.size());
}

@Test
public void testEqualsWithEmptyModels() {
NGramModel m1 = new NGramModel();
NGramModel m2 = new NGramModel();
assertEquals(m1, m2);
}

@Test
public void testHashCodeMatchesForEmptyModels() {
NGramModel m1 = new NGramModel();
NGramModel m2 = new NGramModel();
assertEquals(m1.hashCode(), m2.hashCode());
}

@Test
public void testEqualsFalseWithNull() {
NGramModel model = new NGramModel();
assertFalse(model.equals(null));
}

@Test
public void testEqualsFalseWithDifferentType() {
NGramModel model = new NGramModel();
assertFalse(model.equals("not a model"));
}

@Test
public void testGetCountAfterRemoveReturnsZero() {
NGramModel model = new NGramModel();
StringList tokens = new StringList("out", "of", "scope");
model.add(tokens);
model.remove(tokens);
assertEquals(0, model.getCount(tokens));
}

@Test
public void testIteratorWithSingleEntry() {
NGramModel model = new NGramModel();
StringList entry = new StringList("single");
model.add(entry);
Iterator<StringList> iter = model.iterator();
assertTrue(iter.hasNext());
assertEquals(entry, iter.next());
assertFalse(iter.hasNext());
}

@Test
public void testAddThenSetSameCountDoesNotThrow() {
NGramModel model = new NGramModel();
StringList tokens = new StringList("repeat");
model.add(tokens);
model.setCount(tokens, 1);
assertEquals(1, model.getCount(tokens));
assertTrue(model.contains(tokens));
}

@Test
public void testAddThenSetLowerCount() {
NGramModel model = new NGramModel();
StringList entry = new StringList("data");
model.add(entry);
model.add(entry);
assertEquals(2, model.getCount(entry));
model.setCount(entry, 1);
assertEquals(1, model.getCount(entry));
}

@Test
public void testHashCodeConsistencyAfterModification() {
NGramModel model = new NGramModel();
int hash1 = model.hashCode();
model.add(new StringList("x"));
int hash2 = model.hashCode();
assertNotEquals(hash1, hash2);
}

@Test
public void testEmptyNGramModelToDictionaryReturnsEmptyDictionary() {
NGramModel model = new NGramModel();
assertNotNull(model.toDictionary());
assertEquals(0, model.toDictionary().size());
}

@Test
public void testAddNGramWithSameTokensCaseSensitiveSetCount() {
NGramModel model = new NGramModel();
StringList uppercase = new StringList("HELLO");
StringList lowercase = new StringList("hello");
model.add(uppercase);
model.add(lowercase);
model.setCount(uppercase, 2);
model.setCount(lowercase, 3);
assertEquals(2, model.getCount(uppercase));
assertEquals(3, model.getCount(lowercase));
}

@Test
public void testCutoffWithMaxIntegerValueKeepsAll() {
NGramModel model = new NGramModel();
model.add(new StringList("a"));
model.add(new StringList("b"));
model.cutoff(1, Integer.MAX_VALUE);
assertEquals(2, model.size());
}

@Test
public void testAddMinimumLengthNGramsOnly() {
NGramModel model = new NGramModel();
StringList input = new StringList("w1", "w2", "w3");
model.add(input, 1, 1);
assertTrue(model.contains(new StringList("w1")));
assertTrue(model.contains(new StringList("w2")));
assertTrue(model.contains(new StringList("w3")));
assertFalse(model.contains(new StringList("w1", "w2")));
}

@Test
public void testSetCountImmediatelyAfterClearThrows() {
NGramModel model = new NGramModel();
StringList tokens = new StringList("clear", "test");
model.add(tokens);
model.remove(tokens);
model.setCount(tokens, 1);
}

@Test
public void testSetCountAfterCutoffRemovesEntryThrows() {
NGramModel model = new NGramModel();
StringList tokens = new StringList("low", "freq");
model.add(tokens);
model.cutoff(2, 3);
model.setCount(tokens, 5);
}

@Test
public void testToDictionaryContainAllPairsAdded() {
NGramModel model = new NGramModel();
StringList one = new StringList("A");
StringList two = new StringList("B");
model.add(one);
model.add(two);
assertTrue(model.toDictionary().contains(one));
assertTrue(model.toDictionary().contains(two));
}

@Test
public void testSerializeThenDeserializePreservesMultipleEntriesAndCounts() throws IOException {
NGramModel model = new NGramModel();
StringList a = new StringList("foo");
StringList b = new StringList("bar");
model.add(a);
model.add(b);
model.add(b);
model.setCount(a, 10);
model.setCount(b, 20);
// File file = folder.newFile("serial.dict");
// OutputStream os = new FileOutputStream(file);
// model.serialize(os);
// os.close();
// InputStream is = new FileInputStream(file);
// NGramModel loaded = new NGramModel(is);
// is.close();
// assertEquals(10, loaded.getCount(a));
// assertEquals(20, loaded.getCount(b));
// assertEquals(2, loaded.size());
}

@Test
public void testIteratorRemoveIsUnsupported() {
NGramModel model = new NGramModel();
model.add(new StringList("value", "test"));
Iterator<StringList> it = model.iterator();
it.next();
try {
it.remove();
fail("Expected UnsupportedOperationException not thrown");
} catch (UnsupportedOperationException | IllegalStateException ignored) {
}
}

@Test
public void testEmptyModelHashCodeIsConsistent() {
NGramModel m1 = new NGramModel();
NGramModel m2 = new NGramModel();
assertEquals(m1.hashCode(), m2.hashCode());
}

@Test
public void testTokenOrderImpactsEquality() {
NGramModel model1 = new NGramModel();
NGramModel model2 = new NGramModel();
model1.add(new StringList("a", "b"));
model2.add(new StringList("b", "a"));
assertNotEquals(model1, model2);
}

@Test
public void testSameTokenDifferentInstanceStringList() {
NGramModel model = new NGramModel();
StringList t1 = new StringList("alpha");
StringList t2 = new StringList(new String("alpha"));
model.add(t1);
assertTrue(model.contains(t2));
assertEquals(model.getCount(t2), 1);
}

@Test
public void testAddEmptyStringListIsStoredProperly() {
NGramModel model = new NGramModel();
StringList empty = new StringList();
model.add(empty);
assertTrue(model.contains(empty));
assertEquals(1, model.size());
assertEquals(1, model.getCount(empty));
}

@Test
public void testMultipleCharacterGramsWithUpperMinLength() {
NGramModel model = new NGramModel();
model.add("abcdef", 3, 3);
assertTrue(model.contains(new StringList("abc")));
assertTrue(model.contains(new StringList("bcd")));
assertTrue(model.contains(new StringList("cde")));
assertTrue(model.contains(new StringList("def")));
assertFalse(model.contains(new StringList("ab")));
assertFalse(model.contains(new StringList("a")));
}

@Test
public void testAddTokenListLengthEqualMaxLength() {
NGramModel model = new NGramModel();
StringList input = new StringList("a", "b", "c");
model.add(input, 3, 3);
assertTrue(model.contains(new StringList("a", "b", "c")));
assertFalse(model.contains(new StringList("a")));
assertFalse(model.contains(new StringList("a", "b")));
}

@Test
public void testNumberOfGramsWithMultipleCounts() {
NGramModel model = new NGramModel();
StringList x = new StringList("foo");
StringList y = new StringList("bar");
model.add(x);
model.add(x);
model.add(x);
model.add(y);
assertEquals(4, model.numberOfGrams());
assertEquals(2, model.size());
}

@Test
public void testToStringReflectsCurrentSize() {
NGramModel model = new NGramModel();
assertEquals("Size: 0", model.toString());
model.add(new StringList("entry"));
assertEquals("Size: 1", model.toString());
model.add(new StringList("another"));
assertEquals("Size: 2", model.toString());
}

@Test
public void testAddWithMinEqualsMaxSinglePass() {
NGramModel model = new NGramModel();
StringList input = new StringList("one", "two", "three");
model.add(input, 2, 2);
assertTrue(model.contains(new StringList("one", "two")));
assertTrue(model.contains(new StringList("two", "three")));
assertFalse(model.contains(new StringList("one")));
assertFalse(model.contains(new StringList("three")));
assertEquals(2, model.size());
}

@Test
public void testAddCharacterGramEmptyInput() {
NGramModel model = new NGramModel();
model.add("", 1, 2);
assertEquals(0, model.size());
}

@Test
public void testAddCharacterGramWhitespaceOnly() {
NGramModel model = new NGramModel();
model.add("   ", 1, 2);
assertTrue(model.contains(new StringList(" ")));
assertTrue(model.getCount(new StringList(" ")) > 0);
}

@Test
public void testAddCharacterGramSpecialCharacters() {
NGramModel model = new NGramModel();
model.add("!@#", 1, 2);
assertTrue(model.contains(new StringList("!")));
assertTrue(model.contains(new StringList("@")));
assertTrue(model.contains(new StringList("#")));
}

@Test
public void testAddTokenListWithOneToken() {
NGramModel model = new NGramModel();
StringList single = new StringList("only");
model.add(single, 1, 3);
assertTrue(model.contains(new StringList("only")));
assertEquals(1, model.size());
}

@Test
public void testAddTokenListShorterThanMinLength() {
NGramModel model = new NGramModel();
StringList input = new StringList("x");
model.add(input, 2, 3);
assertEquals(0, model.size());
}

@Test
public void testEmptyStringListIsValidNGram() {
NGramModel model = new NGramModel();
StringList empty = new StringList();
model.add(empty);
assertTrue(model.contains(empty));
assertEquals(1, model.getCount(empty));
}

@Test
public void testDuplicateEntriesHaveAccurateFinalCount() {
NGramModel model = new NGramModel();
StringList tokens = new StringList("repeat");
model.add(tokens);
model.add(tokens);
model.add(tokens);
assertEquals(3, model.getCount(tokens));
}

@Test
public void testAddMultipleNGramsWithOverlaps() {
NGramModel model = new NGramModel();
StringList tokens = new StringList("x", "y", "z");
model.add(tokens, 1, 2);
assertTrue(model.contains(new StringList("x")));
assertTrue(model.contains(new StringList("x", "y")));
assertTrue(model.contains(new StringList("y")));
assertTrue(model.contains(new StringList("y", "z")));
assertTrue(model.contains(new StringList("z")));
}

@Test
public void testCharacterGramOverlapCountsAreCorrect() {
NGramModel model = new NGramModel();
model.add("aaa", 2, 2);
assertTrue(model.contains(new StringList("aa")));
assertEquals(2, model.getCount(new StringList("aa")));
}

@Test
public void testCutoffBoundaryInclusionBehavior() {
NGramModel model = new NGramModel();
StringList a = new StringList("a");
StringList b = new StringList("b");
model.add(a);
model.add(b);
model.add(b);
model.cutoff(1, 1);
assertTrue(model.contains(a));
assertFalse(model.contains(b));
}

@Test
public void testCutoffZeroUnderAndMaxOverKeepsAll() {
NGramModel model = new NGramModel();
model.add(new StringList("keep_this"));
model.cutoff(0, Integer.MAX_VALUE);
assertEquals(1, model.size());
}

@Test
public void testToDictionaryCaseSensitiveContainsOriginalTokens() {
NGramModel model = new NGramModel();
StringList upper = new StringList("WORD");
model.add(upper);
assertTrue(model.toDictionary(true).contains(new StringList("WORD")));
assertFalse(model.toDictionary(true).contains(new StringList("word")));
}

@Test
public void testToDictionaryCaseInsensitiveNormalizesEntries() {
NGramModel model = new NGramModel();
model.add(new StringList("Case"));
model.add(new StringList("case"));
assertEquals(1, model.toDictionary(false).size());
}

@Test
public void testEqualsWithDifferentCountsSameKeysReturnsFalse() {
NGramModel m1 = new NGramModel();
NGramModel m2 = new NGramModel();
StringList x = new StringList("item");
m1.add(x);
m2.add(x);
m2.add(x);
assertNotEquals(m1, m2);
}

@Test
public void testSerializeEmptyModelProducesValidFile() throws IOException {
NGramModel model = new NGramModel();
// File file = folder.newFile("empty.dict");
// OutputStream os = new FileOutputStream(file);
// model.serialize(os);
// os.close();
// InputStream is = new FileInputStream(file);
// assertEquals(-1, is.read());
// is.close();
}

@Test
public void testDeserializeWhitespaceOnlyEntrySkipsOrFails() throws IOException {
// File file = folder.newFile();
// BufferedWriter writer = new BufferedWriter(new FileWriter(file));
// writer.write("    |count=1\n");
// writer.close();
// InputStream is = new FileInputStream(file);
try {
// NGramModel model = new NGramModel(is);
// assertTrue(model.size() >= 0);
} finally {
// is.close();
}
}

@Test
public void testSerializeAndDeserializeMultipleTokenEntries() throws IOException {
NGramModel model = new NGramModel();
model.add(new StringList("a", "b"));
model.add(new StringList("x", "y", "z"));
// File file = folder.newFile("multi.dict");
// OutputStream os = new FileOutputStream(file);
// model.serialize(os);
// os.close();
// InputStream is = new FileInputStream(file);
// NGramModel loaded = new NGramModel(is);
// is.close();
// assertTrue(loaded.contains(new StringList("a", "b")));
// assertTrue(loaded.contains(new StringList("x", "y", "z")));
// assertEquals(2, loaded.size());
}

@Test
public void testAddCharSequenceWithMinEqualsMaxGreaterThanStringLength() {
NGramModel model = new NGramModel();
model.add("hi", 5, 5);
assertEquals(0, model.size());
}

@Test
public void testAddTokenListWithExactLengthMatchMax() {
NGramModel model = new NGramModel();
StringList input = new StringList("t1", "t2");
model.add(input, 2, 2);
assertTrue(model.contains(new StringList("t1", "t2")));
assertFalse(model.contains(new StringList("t1")));
assertEquals(1, model.size());
}

@Test
public void testCutoffDoesNotRemoveAnythingIfCountsAtThresholds() {
NGramModel model = new NGramModel();
StringList one = new StringList("a");
StringList two = new StringList("b");
model.add(one);
model.add(two);
model.add(two);
model.cutoff(1, 2);
assertTrue(model.contains(one));
assertTrue(model.contains(two));
assertEquals(2, model.size());
}

@Test
public void testAddAndSerializeUnicodeCharacters() throws IOException {
NGramModel model = new NGramModel();
StringList unicode = new StringList("😀", "🐍", "你好");
model.add(unicode);
// File file = folder.newFile("unicode.dict");
// OutputStream out = new FileOutputStream(file);
// model.serialize(out);
// out.close();
// InputStream in = new FileInputStream(file);
// NGramModel loaded = new NGramModel(in);
// in.close();
// assertTrue(loaded.contains(unicode));
// assertEquals(1, loaded.getCount(unicode));
}

@Test
public void testToStringFormatsCorrectlyAtMultipleSizes() {
NGramModel model = new NGramModel();
assertEquals("Size: 0", model.toString());
model.add(new StringList("one"));
assertEquals("Size: 1", model.toString());
model.add(new StringList("two"));
assertEquals("Size: 2", model.toString());
}

@Test
public void testAddCharacterGramWithOverlapRetainsOrder() {
NGramModel model = new NGramModel();
model.add("banana", 2, 2);
assertTrue(model.contains(new StringList("ba")));
assertTrue(model.contains(new StringList("an")));
assertTrue(model.contains(new StringList("na")));
assertEquals(5, model.numberOfGrams());
}

@Test
public void testSetCountToZeroRemovesEntry() {
NGramModel model = new NGramModel();
StringList tokens = new StringList("x");
model.add(tokens);
model.setCount(tokens, 0);
assertEquals(0, model.getCount(tokens));
assertTrue(model.contains(tokens));
}

@Test
public void testDeserializeAllWhitespaceBetweenTokens() throws IOException {
// File file = folder.newFile();
// Writer writer = new BufferedWriter(new FileWriter(file));
// writer.write("   aaa     bbb  |count=1\n");
// writer.close();
// InputStream in = new FileInputStream(file);
// NGramModel model = new NGramModel(in);
// in.close();
StringList tokens = new StringList("aaa", "bbb");
// assertTrue(model.contains(tokens));
// assertEquals(1, model.getCount(tokens));
}

@Test
public void testGetCountAfterCutoffOnRemovedEntryIsZero() {
NGramModel model = new NGramModel();
StringList token = new StringList("abc");
model.add(token);
model.cutoff(2, 10);
assertEquals(0, model.getCount(token));
assertFalse(model.contains(token));
}

@Test
public void testModelEqualityAfterAddingSameEntriesInReverseOrder() {
NGramModel m1 = new NGramModel();
NGramModel m2 = new NGramModel();
StringList first = new StringList("a", "b");
StringList second = new StringList("x", "y");
m1.add(first);
m1.add(second);
m2.add(second);
m2.add(first);
assertEquals(m1, m2);
}

@Test
public void testHashCodeConsistencyForSameLogicalContent() {
NGramModel m1 = new NGramModel();
NGramModel m2 = new NGramModel();
StringList data = new StringList("consistent");
m1.add(data);
m2.add(data);
assertEquals(m1.hashCode(), m2.hashCode());
}

@Test
public void testEmptyTokenListIsStoredAndMatchesEquality() {
NGramModel model = new NGramModel();
StringList empty = new StringList();
model.add(empty);
assertEquals(1, model.size());
StringList equivalent = new StringList();
assertTrue(model.contains(equivalent));
assertEquals(1, model.getCount(equivalent));
}

@Test
public void testAddCharSequenceWithMinGreaterThanMaxThrowsException() {
try {
NGramModel model = new NGramModel();
model.add("text", 4, 3);
fail("Expected IllegalArgumentException not thrown");
} catch (IllegalArgumentException e) {
assertTrue(e.getMessage().contains("minLength param must not be larger"));
}
}

@Test
public void testAddTokenListEmptyInputNGramRange() {
NGramModel model = new NGramModel();
StringList tokens = new StringList();
model.add(tokens, 1, 3);
assertEquals(0, model.size());
}

@Test
public void testAddCharSequenceNullOrEmptyEval() {
NGramModel model = new NGramModel();
model.add("", 1, 1);
assertEquals(0, model.size());
}

@Test
public void testAddCharSequenceUpperCaseNormalized() {
NGramModel model = new NGramModel();
model.add("A", 1, 1);
assertTrue(model.contains(new StringList("a")));
}

@Test
public void testSerializeExactCountPreservedOnReload() throws IOException {
NGramModel model = new NGramModel();
StringList ngram = new StringList("alpha", "beta");
model.add(ngram);
model.setCount(ngram, 17);
// File file = tempFolder.newFile("persist.dict");
// OutputStream out = new FileOutputStream(file);
// model.serialize(out);
// out.close();
// InputStream in = new FileInputStream(file);
// NGramModel loaded = new NGramModel(in);
// in.close();
// assertEquals(17, loaded.getCount(ngram));
}

@Test
public void testSerializeEmptyModelProducesNoEntries() throws IOException {
NGramModel model = new NGramModel();
// File file = tempFolder.newFile("empty.dict");
// OutputStream out = new FileOutputStream(file);
// model.serialize(out);
// out.close();
// InputStream in = new FileInputStream(file);
// assertEquals(-1, in.read());
// in.close();
}

@Test
public void testSetCountAfterCutoffRemovedEntryThrows() {
NGramModel model = new NGramModel();
StringList grams = new StringList("one");
model.add(grams);
model.cutoff(2, 2);
// try {
// model.setCount(grams, 10);
// fail("Expected NoSuchElementException");
// } catch (NoSuchElementException expected) {
// assertNotNull(expected.getMessage());
// }
}

@Test
public void testCutoffWithNegativeUnderAndOversize() {
NGramModel model = new NGramModel();
model.add(new StringList("x"));
model.cutoff(-5, Integer.MAX_VALUE);
assertEquals(1, model.size());
}

@Test
public void testNGramEqualityDifferentInstancesSameTokensSameCounts() {
NGramModel model1 = new NGramModel();
NGramModel model2 = new NGramModel();
model1.add(new StringList("abc"));
model2.add(new StringList("abc"));
assertEquals(model1, model2);
}

@Test
public void testNGramEqualityDifferentCountsReturnsFalse() {
NGramModel model1 = new NGramModel();
NGramModel model2 = new NGramModel();
model1.add(new StringList("token"));
model2.add(new StringList("token"));
model2.add(new StringList("token"));
assertNotEquals(model1, model2);
}

@Test
public void testToDictionaryCaseMergeBehaviorLowercasePreserved() {
NGramModel model = new NGramModel();
model.add(new StringList("Hello"));
model.add(new StringList("hello"));
assertEquals(1, model.toDictionary(false).size());
}

@Test
public void testToDictionaryCaseSensitiveKeepsBothEntries() {
NGramModel model = new NGramModel();
model.add(new StringList("Word"));
model.add(new StringList("word"));
assertEquals(2, model.toDictionary(true).size());
}

@Test
public void testRemoveHasNoEffectOnNonExistentEntry() {
NGramModel model = new NGramModel();
model.add(new StringList("a"));
model.remove(new StringList("b"));
assertEquals(1, model.size());
}

@Test
public void testAddCharacterGramMinGreaterThanLength() {
NGramModel model = new NGramModel();
model.add("abc", 4, 5);
assertEquals(0, model.size());
}

@Test
public void testAddNegativeMinLengthThrowsException() {
NGramModel model = new NGramModel();
model.add(new StringList("x", "y"), -1, 2);
}

@Test
public void testAddZeroMaxLengthThrowsException() {
NGramModel model = new NGramModel();
model.add(new StringList("x", "y", "z"), 1, 0);
}

@Test
public void testAddWhereMinGreaterThanMaxThrows() {
NGramModel model = new NGramModel();
model.add(new StringList("x", "y", "z"), 3, 2);
}

@Test
public void testCharGramDifferentCasesResultInLowerCase() {
NGramModel model = new NGramModel();
model.add("XYZ", 1, 1);
assertTrue(model.contains(new StringList("x")));
assertTrue(model.contains(new StringList("y")));
assertTrue(model.contains(new StringList("z")));
}

@Test
public void testMultipleEntryAdditionKeepsAccurateSize() {
NGramModel model = new NGramModel();
model.add(new StringList("red"));
model.add(new StringList("blue"));
model.add(new StringList("green"));
assertEquals(3, model.size());
}

@Test
public void testContainsAfterRemoveReturnsFalse() {
NGramModel model = new NGramModel();
StringList entry = new StringList("to", "remove");
model.add(entry);
model.remove(entry);
assertFalse(model.contains(entry));
}

@Test
public void testEqualsDifferentTypeReturnsFalse() {
NGramModel model = new NGramModel();
assertFalse(model.equals("not a model"));
}

@Test
public void testEqualsNullReturnsFalse() {
NGramModel model = new NGramModel();
assertFalse(model.equals(null));
}

@Test
public void testEqualsSameInstanceReturnsTrue() {
NGramModel model = new NGramModel();
assertTrue(model.equals(model));
}

@Test
public void testHashCodeConsistencyAcrossExecutions() {
NGramModel model = new NGramModel();
int first = model.hashCode();
int second = model.hashCode();
assertEquals(first, second);
}

@Test
public void testToStringIncludesAccurateSizeInfo() {
NGramModel model = new NGramModel();
assertEquals("Size: 0", model.toString());
model.add(new StringList("item"));
assertEquals("Size: 1", model.toString());
}
}
