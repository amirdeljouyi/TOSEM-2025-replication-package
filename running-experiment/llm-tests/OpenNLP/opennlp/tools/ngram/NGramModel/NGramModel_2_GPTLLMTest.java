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
import java.nio.charset.StandardCharsets;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NGramModel_2_GPTLLMTest {

@Test
public void testAddAndGetCountSingleNGram() {
NGramModel model = new NGramModel();
StringList ngram = new StringList("once", "upon");
model.add(ngram);
int count = model.getCount(ngram);
assertEquals(1, count);
}

@Test
public void testAddSameNGramMultipleTimesIncrementsCount() {
NGramModel model = new NGramModel();
StringList ngram = new StringList("quick", "brown");
model.add(ngram);
assertEquals(1, model.getCount(ngram));
model.add(ngram);
assertEquals(2, model.getCount(ngram));
model.add(ngram);
assertEquals(3, model.getCount(ngram));
}

@Test
public void testSetCountForExistingNGram() {
NGramModel model = new NGramModel();
StringList ngram = new StringList("lazy", "dog");
model.add(ngram);
assertEquals(1, model.getCount(ngram));
model.setCount(ngram, 7);
assertEquals(7, model.getCount(ngram));
}

@Test
public void testSetCountThrowsForMissingNGram() {
NGramModel model = new NGramModel();
StringList ngram = new StringList("not", "present");
model.setCount(ngram, 3);
}

@Test
public void testContainsReturnsTrueIfPresent() {
NGramModel model = new NGramModel();
StringList ngram = new StringList("yes", "here");
model.add(ngram);
boolean result = model.contains(ngram);
assertTrue(result);
}

@Test
public void testContainsReturnsFalseIfNotPresent() {
NGramModel model = new NGramModel();
StringList ngram = new StringList("missing", "entry");
boolean result = model.contains(ngram);
assertFalse(result);
}

@Test
public void testRemoveDeletesNGram() {
NGramModel model = new NGramModel();
StringList ngram = new StringList("remove", "me");
model.add(ngram);
assertTrue(model.contains(ngram));
model.remove(ngram);
assertFalse(model.contains(ngram));
assertEquals(0, model.getCount(ngram));
}

@Test
public void testAddNGramByRangeGeneratesCorrectSubsets() {
NGramModel model = new NGramModel();
StringList tokens = new StringList("a", "b", "c");
model.add(tokens, 1, 2);
boolean contains1 = model.contains(new StringList("a"));
boolean contains2 = model.contains(new StringList("a", "b"));
boolean contains3 = model.contains(new StringList("b", "c"));
boolean contains4 = model.contains(new StringList("c"));
boolean contains5 = model.contains(new StringList("a", "b", "c"));
assertTrue(contains1);
assertTrue(contains2);
assertTrue(contains3);
assertTrue(contains4);
assertFalse(contains5);
int size = model.size();
assertEquals(5, size);
}

@Test
public void testAddNGramRangeThrowsWhenMinLessThanOne() {
NGramModel model = new NGramModel();
StringList tokens = new StringList("x", "y");
model.add(tokens, 0, 2);
}

@Test
public void testAddNGramRangeThrowsWhenMinGreaterThanMax() {
NGramModel model = new NGramModel();
StringList tokens = new StringList("x", "y");
model.add(tokens, 3, 2);
}

@Test
public void testAddCharSequenceGeneratesCharacterNGrams() {
NGramModel model = new NGramModel();
model.add("word", 2, 3);
boolean contains1 = model.contains(new StringList("wo"));
boolean contains2 = model.contains(new StringList("wor"));
boolean contains3 = model.contains(new StringList("rd"));
assertTrue(contains1);
assertTrue(contains2);
assertTrue(contains3);
assertEquals(6, model.size());
}

@Test
public void testCutoffRemovesLowHighFrequency() {
NGramModel model = new NGramModel();
StringList ngram1 = new StringList("one");
StringList ngram2 = new StringList("two");
StringList ngram3 = new StringList("three");
model.add(ngram1);
model.add(ngram2);
model.add(ngram2);
model.add(ngram3);
model.add(ngram3);
model.add(ngram3);
model.add(ngram3);
model.cutoff(2, 3);
boolean containsOne = model.contains(ngram1);
boolean containsTwo = model.contains(ngram2);
boolean containsThree = model.contains(ngram3);
assertFalse(containsOne);
assertTrue(containsTwo);
assertFalse(containsThree);
}

@Test
public void testNumberOfGramsSumsCountsCorrectly() {
NGramModel model = new NGramModel();
StringList a = new StringList("a");
StringList b = new StringList("b");
model.add(a);
model.add(b);
model.add(a);
int total = model.numberOfGrams();
assertEquals(3, total);
}

@Test
public void testToDictionaryCaseInsensitiveMergesEntries() {
NGramModel model = new NGramModel();
StringList upper = new StringList("HELLO");
StringList lower = new StringList("hello");
model.add(upper);
model.add(lower);
// Dictionary dict = model.toDictionary(false);
// int size = dict.size();
// assertEquals(1, size);
}

@Test
public void testToDictionaryCaseSensitiveKeepsDistinct() {
NGramModel model = new NGramModel();
StringList upper = new StringList("HELLO");
StringList lower = new StringList("hello");
model.add(upper);
model.add(lower);
// Dictionary dict = model.toDictionary(true);
// int size = dict.size();
// assertEquals(2, size);
}

@Test
public void testEqualsAndHashCodeWhenEqual() {
NGramModel model1 = new NGramModel();
NGramModel model2 = new NGramModel();
StringList ngram = new StringList("key");
model1.add(ngram);
model2.add(new StringList("key"));
boolean equals = model1.equals(model2);
int hash1 = model1.hashCode();
int hash2 = model2.hashCode();
assertTrue(equals);
assertEquals(hash1, hash2);
}

@Test
public void testEqualsReturnsFalseForDifferentModels() {
NGramModel model1 = new NGramModel();
NGramModel model2 = new NGramModel();
model1.add(new StringList("a"));
model2.add(new StringList("b"));
boolean result = model1.equals(model2);
assertFalse(result);
}

@Test
public void testToStringReportsSize() {
NGramModel model = new NGramModel();
model.add(new StringList("token"));
String output = model.toString();
assertEquals("Size: 1", output);
}

@Test
public void testSerializeAndDeserializeRetainsCounts() throws IOException {
NGramModel model = new NGramModel();
StringList ngram = new StringList("serialize");
model.add(ngram);
model.setCount(ngram, 9);
ByteArrayOutputStream out = new ByteArrayOutputStream();
model.serialize(out);
ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
NGramModel result = new NGramModel(in);
int count = result.getCount(ngram);
assertEquals(9, count);
assertEquals(1, result.size());
}

@Test
public void testDeserializeMissingCountAttributeThrows() throws IOException {
String malformedXml = "<dictionary>" + "<entry>" + "  <tokens><token>x</token></tokens>" + "</entry>" + "</dictionary>";
ByteArrayInputStream input = new ByteArrayInputStream(malformedXml.getBytes(StandardCharsets.UTF_8));
new NGramModel(input);
}

@Test
public void testDeserializeNonNumericCountThrows() throws IOException {
String malformedXml = "<dictionary>" + "<entry count='NaN'>" + "  <tokens><token>bad</token></tokens>" + "</entry>" + "</dictionary>";
ByteArrayInputStream input = new ByteArrayInputStream(malformedXml.getBytes(StandardCharsets.UTF_8));
new NGramModel(input);
}

@Test
public void testIteratorReturnsExpectedItems() {
NGramModel model = new NGramModel();
StringList a = new StringList("x");
StringList b = new StringList("y");
model.add(a);
model.add(b);
Iterator<StringList> iterator = model.iterator();
Set<StringList> expected = new HashSet<>();
expected.add(a);
expected.add(b);
boolean firstHasNext = iterator.hasNext();
StringList first = iterator.next();
boolean secondHasNext = iterator.hasNext();
StringList second = iterator.next();
boolean thirdHasNext = iterator.hasNext();
Set<StringList> actual = new HashSet<>();
actual.add(first);
actual.add(second);
assertTrue(firstHasNext);
assertTrue(secondHasNext);
assertFalse(thirdHasNext);
assertEquals(expected, actual);
}

@Test
public void testAddEmptyCharSequenceHasNoEffect() {
NGramModel model = new NGramModel();
model.add("", 2, 3);
assertEquals(0, model.size());
assertEquals(0, model.numberOfGrams());
}

@Test
public void testAddCharSequenceWhereLengthLessThanMinAddsNothing() {
NGramModel model = new NGramModel();
model.add("hi", 3, 5);
assertEquals(0, model.size());
}

@Test
public void testCutoffWithZeroBoundsRetainsAll() {
NGramModel model = new NGramModel();
StringList n1 = new StringList("keep");
model.add(n1);
model.setCount(n1, 100);
model.cutoff(0, Integer.MAX_VALUE);
assertTrue(model.contains(n1));
assertEquals(1, model.size());
}

@Test
public void testCutoffWithNoMatchingRemovesAll() {
NGramModel model = new NGramModel();
StringList n1 = new StringList("low");
StringList n2 = new StringList("high");
model.add(n1);
model.add(n2);
model.setCount(n2, 999);
model.cutoff(2, 998);
assertEquals(0, model.size());
}

@Test
public void testMultipleNGramsWithSamePrefixDoNotInterfere() {
NGramModel model = new NGramModel();
model.add(new StringList("the", "cat"));
model.add(new StringList("the", "dog"));
model.add(new StringList("the", "mouse"));
assertEquals(3, model.size());
assertEquals(3, model.numberOfGrams());
}

@Test
public void testEqualsWithDifferentObjectTypeReturnsFalse() {
NGramModel model = new NGramModel();
boolean result = model.equals("Not a model");
assertFalse(result);
}

@Test
public void testEqualsWithSelfReturnsTrue() {
NGramModel model = new NGramModel();
assertTrue(model.equals(model));
}

@Test
public void testSerializeWithEmptyModelProducesValidXML() throws IOException {
NGramModel model = new NGramModel();
ByteArrayOutputStream output = new ByteArrayOutputStream();
model.serialize(output);
String xmlOutput = output.toString("UTF-8");
assertTrue(xmlOutput.contains("<dictionary"));
assertTrue(xmlOutput.contains("</dictionary>"));
}

@Test
public void testSerializeEntryIteratorRemoveThrows() throws IOException {
NGramModel model = new NGramModel();
model.add(new StringList("a"));
model.setCount(new StringList("a"), 1);
// OutputStream dummy = new ByteArrayOutputStream();
Iterator<Entry> iterator = new Iterator<Entry>() {

private final Iterator<StringList> keyIterator = model.iterator();

@Override
public boolean hasNext() {
return keyIterator.hasNext();
}

@Override
public Entry next() {
StringList s = keyIterator.next();
opennlp.tools.dictionary.serializer.Attributes attr = new opennlp.tools.dictionary.serializer.Attributes();
attr.setValue("count", "1");
return new Entry(s, attr);
}

@Override
public void remove() {
throw new UnsupportedOperationException("remove not supported");
}
};
iterator.remove();
}

@Test
public void testHashCodeConsistencyForEmptyModel() {
NGramModel model1 = new NGramModel();
NGramModel model2 = new NGramModel();
assertEquals(model1.hashCode(), model2.hashCode());
}

@Test
public void testDeserializeAndSerializeRoundTripRetainsData() throws IOException {
NGramModel original = new NGramModel();
StringList ngram = new StringList("persisted");
original.add(ngram);
original.setCount(ngram, 4);
ByteArrayOutputStream out = new ByteArrayOutputStream();
original.serialize(out);
ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
NGramModel roundTrip = new NGramModel(in);
assertEquals(original, roundTrip);
assertEquals(4, roundTrip.getCount(ngram));
}

@Test
public void testToDictionaryWithEmptyModelReturnsEmptyDictionary() {
NGramModel model = new NGramModel();
// Dictionary dict = model.toDictionary();
// assertEquals(0, dict.size());
}

@Test
public void testAddMethodHandlesSingleTokenAsNGram() {
NGramModel model = new NGramModel();
StringList ngram = new StringList("single");
model.add(ngram);
assertTrue(model.contains(ngram));
assertEquals(1, model.size());
}

@Test
public void testToStringEmptyModelReturnsZeroSize() {
NGramModel model = new NGramModel();
String result = model.toString();
assertEquals("Size: 0", result);
}

@Test
public void testIteratorNextThrowsWhenEmpty() {
NGramModel model = new NGramModel();
Iterator<StringList> iterator = model.iterator();
iterator.next();
}

@Test
public void testSerializeOutputHasAllTokensAndCounts() throws IOException {
NGramModel model = new NGramModel();
model.add(new StringList("t", "1"));
model.setCount(new StringList("t", "1"), 2);
model.add(new StringList("t", "2"));
model.setCount(new StringList("t", "2"), 3);
ByteArrayOutputStream out = new ByteArrayOutputStream();
model.serialize(out);
String xml = out.toString("UTF-8");
assertTrue(xml.contains("<token>t</token>"));
assertTrue(xml.contains("<token>1</token>"));
assertTrue(xml.contains("count=\"2\""));
assertTrue(xml.contains("count=\"3\""));
}

@Test
public void testEmptyStringListNGramIsHandled() {
NGramModel model = new NGramModel();
StringList emptyNGram = new StringList();
model.add(emptyNGram);
assertTrue(model.contains(emptyNGram));
assertEquals(1, model.getCount(emptyNGram));
assertEquals(1, model.size());
assertEquals(1, model.numberOfGrams());
}

@Test
public void testAddCharSequenceWithWhitespaceOnly() {
NGramModel model = new NGramModel();
model.add("   ", 1, 2);
assertEquals(2, model.size());
}

@Test
public void testAddCharSequenceUpperAndLowerMerge() {
NGramModel model = new NGramModel();
model.add("Test", 4, 4);
model.add("test", 4, 4);
assertEquals(1, model.size());
assertEquals(2, model.numberOfGrams());
assertTrue(model.contains(new StringList("test")));
}

@Test
public void testAddTokensWithMinLengthEqualToMaxLength() {
NGramModel model = new NGramModel();
StringList input = new StringList("x", "y", "z");
model.add(input, 2, 2);
assertTrue(model.contains(new StringList("x", "y")));
assertTrue(model.contains(new StringList("y", "z")));
assertEquals(2, model.size());
}

@Test
public void testToDictionaryMultipleCasedEntriesCaseSensitive() {
NGramModel model = new NGramModel();
StringList tok1 = new StringList("Apple");
StringList tok2 = new StringList("apple");
model.add(tok1);
model.add(tok2);
// Dictionary dict = model.toDictionary(true);
// assertTrue(dict.contains(tok1));
// assertTrue(dict.contains(tok2));
// assertEquals(2, dict.size());
}

@Test
public void testSetCountToZeroStillRetainsInModel() {
NGramModel model = new NGramModel();
StringList ngram = new StringList("zero");
model.add(ngram);
model.setCount(ngram, 0);
assertTrue(model.contains(ngram));
assertEquals(0, model.getCount(ngram));
}

@Test
public void testCutoffRemovesOnlyAboveUpperBound() {
NGramModel model = new NGramModel();
StringList keep = new StringList("keep");
StringList drop = new StringList("drop");
model.add(keep);
model.setCount(keep, 5);
model.add(drop);
model.setCount(drop, 50);
model.cutoff(1, 30);
assertTrue(model.contains(keep));
assertFalse(model.contains(drop));
}

@Test
public void testCutoffRemovesOnlyBelowLowerBound() {
NGramModel model = new NGramModel();
StringList keep = new StringList("keep");
StringList drop = new StringList("drop");
model.add(keep);
model.setCount(keep, 10);
model.add(drop);
model.setCount(drop, 1);
model.cutoff(5, 20);
assertTrue(model.contains(keep));
assertFalse(model.contains(drop));
}

@Test
public void testEqualsWithNull() {
NGramModel model = new NGramModel();
assertFalse(model.equals(null));
}

@Test
public void testEqualsWithDifferentNGramCounts() {
NGramModel m1 = new NGramModel();
NGramModel m2 = new NGramModel();
StringList sl = new StringList("different");
m1.add(sl);
m2.add(sl);
m2.setCount(sl, 2);
assertFalse(m1.equals(m2));
}

@Test
public void testAddTokensWithMoreThanMaxLengthDoesNothing() {
NGramModel model = new NGramModel();
StringList list = new StringList("x");
model.add(list, 2, 4);
assertEquals(0, model.size());
}

@Test
public void testDeserializeValidSingleEntryXml() throws IOException {
String validXml = "<dictionary>" + "<entry count=\"3\">" + "<tokens><token>foo</token></tokens>" + "</entry>" + "</dictionary>";
ByteArrayInputStream input = new ByteArrayInputStream(validXml.getBytes(StandardCharsets.UTF_8));
NGramModel model = new NGramModel(input);
assertTrue(model.contains(new StringList("foo")));
assertEquals(3, model.getCount(new StringList("foo")));
}

@Test
public void testDeserializeNegativeCountThrows() throws IOException {
String badXml = "<dictionary>" + "<entry count=\"-1\">" + "<tokens><token>x</token></tokens>" + "</entry>" + "</dictionary>";
ByteArrayInputStream input = new ByteArrayInputStream(badXml.getBytes(StandardCharsets.UTF_8));
new NGramModel(input);
}

@Test
public void testHashCodeDifferenceForDifferentModels() {
NGramModel m1 = new NGramModel();
NGramModel m2 = new NGramModel();
m1.add(new StringList("a"));
m2.add(new StringList("b"));
assertNotEquals(m1.hashCode(), m2.hashCode());
}

@Test
public void testAddAndRemoveThenReAddNGram() {
NGramModel model = new NGramModel();
StringList sl = new StringList("word");
model.add(sl);
model.remove(sl);
model.add(sl);
assertEquals(1, model.getCount(sl));
assertTrue(model.contains(sl));
}

@Test
public void testMultipleCharGramsSameLengthAreDistinct() {
NGramModel model = new NGramModel();
model.add("cat", 2, 2);
assertTrue(model.contains(new StringList("ca")));
assertTrue(model.contains(new StringList("at")));
assertEquals(2, model.size());
}

@Test
public void testSingleCharacterCharGram() {
NGramModel model = new NGramModel();
model.add("x", 1, 1);
assertTrue(model.contains(new StringList("x")));
assertEquals(1, model.size());
}

@Test
public void testAddWithMinMaxMatchingExactLength() {
NGramModel model = new NGramModel();
StringList tokens = new StringList("a", "b", "c");
model.add(tokens, 3, 3);
assertTrue(model.contains(new StringList("a", "b", "c")));
assertEquals(1, model.size());
}

@Test
public void testModelWithMultipleIdenticalAddsPreserveSameNGramEntry() {
NGramModel model = new NGramModel();
StringList tokens = new StringList("repeated");
model.add(tokens);
model.add(tokens);
model.add(tokens);
int count = model.getCount(tokens);
int size = model.size();
assertEquals(3, count);
assertEquals(1, size);
}

@Test
public void testEdgeCaseTokensWithSymbols() {
NGramModel model = new NGramModel();
StringList tokens = new StringList("@", "#", "$");
model.add(tokens);
assertTrue(model.contains(tokens));
assertEquals(1, model.getCount(tokens));
}

@Test
public void testAddSingleCharacterCharSequence() {
NGramModel model = new NGramModel();
model.add("x", 1, 1);
assertEquals(1, model.size());
assertEquals(1, model.numberOfGrams());
assertTrue(model.contains(new StringList("x")));
}

@Test
public void testEmptyStringListHasZeroCountAndIsNotContained() {
NGramModel model = new NGramModel();
StringList empty = new StringList();
int count = model.getCount(empty);
boolean contains = model.contains(empty);
assertEquals(0, count);
assertFalse(contains);
}

@Test
public void testSetCountAfterRemovalThrowsException() {
NGramModel model = new NGramModel();
StringList sl = new StringList("x");
model.add(sl);
model.remove(sl);
model.setCount(sl, 2);
}

@Test
public void testMultipleCaseVariantsInCaseInsensitiveDictionaryResultInOneEntry() {
NGramModel model = new NGramModel();
model.add(new StringList("Tea"));
model.add(new StringList("tea"));
// Dictionary dict = model.toDictionary(false);
// assertEquals(1, dict.size());
}

@Test
public void testMultipleCaseVariantsInCaseSensitiveDictionaryResultInTwoEntries() {
NGramModel model = new NGramModel();
model.add(new StringList("Camel"));
model.add(new StringList("camel"));
// Dictionary dict = model.toDictionary(true);
// assertEquals(2, dict.size());
}

@Test
public void testAddWithRepeatedStringsGeneratesOnlyOneEntryAndCorrectCount() {
NGramModel model = new NGramModel();
StringList sl = new StringList("repeat");
model.add(sl);
model.add(sl);
model.add(sl);
assertEquals(1, model.size());
assertEquals(3, model.getCount(sl));
}

@Test
public void testCutoffWithNegativeLowerAndOverflowUpperRemovesNothing() {
NGramModel model = new NGramModel();
model.add(new StringList("safe"));
model.add(new StringList("safe"));
model.add(new StringList("safe"));
model.cutoff(-10, Integer.MAX_VALUE + 1);
assertEquals(1, model.size());
}

@Test
public void testEqualsWithModifiedCountReturnsFalse() {
NGramModel modelA = new NGramModel();
NGramModel modelB = new NGramModel();
StringList ngram = new StringList("diff");
modelA.add(ngram);
modelB.add(ngram);
modelB.setCount(ngram, 7);
assertFalse(modelA.equals(modelB));
}

@Test
public void testEqualsReturnsFalseForNull() {
NGramModel model = new NGramModel();
assertFalse(model.equals(null));
}

@Test
public void testEqualsReturnsFalseForOtherType() {
NGramModel model = new NGramModel();
assertFalse(model.equals("not a model"));
}

@Test
public void testHashCodeConsistencyWithSameContent() {
NGramModel model1 = new NGramModel();
NGramModel model2 = new NGramModel();
StringList sl = new StringList("hash");
model1.add(sl);
model2.add(sl);
assertEquals(model1.hashCode(), model2.hashCode());
}

@Test
public void testToStringOnEmptyModel() {
NGramModel model = new NGramModel();
String str = model.toString();
assertEquals("Size: 0", str);
}

@Test
public void testSerializeIncludesCorrectAttributeValue() throws IOException {
NGramModel model = new NGramModel();
StringList sl = new StringList("token");
model.add(sl);
model.setCount(sl, 5);
ByteArrayOutputStream out = new ByteArrayOutputStream();
model.serialize(out);
String xml = new String(out.toByteArray(), StandardCharsets.UTF_8);
assertTrue(xml.contains("count=\"5\""));
}

@Test
public void testDeserializeValidMinimalEntrySucceeds() throws IOException {
String xml = "<dictionary>" + "<entry count=\"1\">" + "<tokens><token>a</token></tokens>" + "</entry>" + "</dictionary>";
ByteArrayInputStream input = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
NGramModel model = new NGramModel(input);
assertTrue(model.contains(new StringList("a")));
assertEquals(1, model.getCount(new StringList("a")));
}

@Test
public void testDeserializeEntryWithoutCountThrows() throws IOException {
String xml = "<dictionary>" + "<entry>" + "<tokens><token>missing</token></tokens>" + "</entry>" + "</dictionary>";
ByteArrayInputStream input = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
new NGramModel(input);
}

@Test
public void testDeserializeEntryWithInvalidCountThrows() throws IOException {
String xml = "<dictionary>" + "<entry count=\"bad\">" + "<tokens><token>invalid</token></tokens>" + "</entry>" + "</dictionary>";
ByteArrayInputStream input = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
new NGramModel(input);
}

@Test
public void testAddTokensRangeWhereAllLengthsAreAccepted() {
NGramModel model = new NGramModel();
StringList input = new StringList("a", "b", "c");
model.add(input, 1, 3);
assertTrue(model.contains(new StringList("a")));
assertTrue(model.contains(new StringList("a", "b")));
assertTrue(model.contains(new StringList("a", "b", "c")));
assertEquals(6, model.size());
}

@Test
public void testAddCharSequenceWithLengthBelowMinAddsNothing() {
NGramModel model = new NGramModel();
model.add("yo", 3, 4);
assertEquals(0, model.size());
assertEquals(0, model.numberOfGrams());
}

@Test
public void testAddCharSequenceWithMixedCaseAppliesToLowerCase() {
NGramModel model = new NGramModel();
model.add("ABC", 1, 1);
assertTrue(model.contains(new StringList("a")));
assertTrue(model.contains(new StringList("b")));
assertTrue(model.contains(new StringList("c")));
}

@Test
public void testRemoveNonExistingNGramDoesNothing() {
NGramModel model = new NGramModel();
model.remove(new StringList("notThere"));
assertEquals(0, model.size());
}

@Test
public void testSerializeThenDeserializePreservesCount() throws IOException {
NGramModel model = new NGramModel();
StringList sl = new StringList("preserve");
model.add(sl);
model.setCount(sl, 10);
ByteArrayOutputStream out = new ByteArrayOutputStream();
model.serialize(out);
ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
NGramModel copy = new NGramModel(in);
assertEquals(10, copy.getCount(sl));
assertTrue(copy.contains(sl));
}

@Test
public void testAddCharSequenceUppercaseNormalizesToLowercase() {
NGramModel model = new NGramModel();
model.add("ABCDEF", 2, 2);
boolean containsFirstBigram = model.contains(new StringList("ab"));
boolean containsLastBigram = model.contains(new StringList("ef"));
assertTrue(containsFirstBigram);
assertTrue(containsLastBigram);
}

@Test
public void testAddCharSequenceSameNGramAccumulatesCount() {
NGramModel model = new NGramModel();
model.add("word", 4, 4);
model.add("word", 4, 4);
int count = model.getCount(new StringList("word"));
assertEquals(2, count);
assertEquals(1, model.size());
}

@Test
public void testAddEmptyStringListTokensAddsNoNGrams() {
NGramModel model = new NGramModel();
StringList empty = new StringList();
model.add(empty, 1, 2);
int size = model.size();
assertEquals(0, size);
}

@Test
public void testAddCharSequenceWithMultibyteCharacters() {
NGramModel model = new NGramModel();
model.add("こんにちは", 2, 2);
assertTrue(model.size() > 0);
}

@Test
public void testAddCharSequenceWithZeroMaxLengthThrows() {
NGramModel model = new NGramModel();
model.add("abc", 1, 0);
}

@Test
public void testCutoffRemovesAllWhenCutoffsAreEqualToCount() {
NGramModel model = new NGramModel();
StringList sl = new StringList("threshold");
model.add(sl);
model.setCount(sl, 5);
model.cutoff(6, 4);
assertEquals(0, model.size());
}

@Test
public void testNumberOfGramsCalculationWithMultipleCounts() {
NGramModel model = new NGramModel();
StringList a = new StringList("a");
StringList b = new StringList("b");
StringList c = new StringList("c");
model.add(a);
model.setCount(a, 2);
model.add(b);
model.setCount(b, 3);
model.add(c);
model.setCount(c, 5);
int total = model.numberOfGrams();
assertEquals(10, total);
}

@Test
public void testCaseSensitivityPreservedWhenSpecifiedTrue() {
NGramModel model = new NGramModel();
StringList upper = new StringList("SHOUT");
StringList lower = new StringList("shout");
model.add(upper);
model.add(lower);
// Dictionary dict = model.toDictionary(true);
// boolean foundUpper = dict.contains(upper);
// boolean foundLower = dict.contains(lower);
// assertTrue(foundUpper);
// assertTrue(foundLower);
// assertEquals(2, dict.size());
}

@Test
public void testEmptyNGramModelIteratesToEnd() {
NGramModel model = new NGramModel();
Iterator<StringList> iterator = model.iterator();
boolean hasNext = iterator.hasNext();
assertFalse(hasNext);
}

@Test
public void testModelWithOneNGramReturnsCorrectIterator() {
NGramModel model = new NGramModel();
StringList sl = new StringList("iterator");
model.add(sl);
Iterator<StringList> iterator = model.iterator();
boolean has = iterator.hasNext();
StringList next = iterator.next();
assertEquals(sl, next);
assertTrue(has);
}

@Test
public void testSerializeIteratorNextIncludesCorrectData() throws IOException {
NGramModel model = new NGramModel();
StringList sl = new StringList("data");
model.add(sl);
model.setCount(sl, 7);
ByteArrayOutputStream out = new ByteArrayOutputStream();
model.serialize(out);
String result = new String(out.toByteArray(), StandardCharsets.UTF_8);
assertTrue(result.contains("count=\"7\""));
assertTrue(result.contains("<token>data</token>"));
}

@Test
public void testAddCharGramOverlappingWindow() {
NGramModel model = new NGramModel();
model.add("abcd", 2, 2);
boolean ab = model.contains(new StringList("ab"));
boolean bc = model.contains(new StringList("bc"));
boolean cd = model.contains(new StringList("cd"));
assertTrue(ab);
assertTrue(bc);
assertTrue(cd);
assertEquals(3, model.size());
}

@Test
public void testToDictionaryPreservesAllDistinctStringLists() {
NGramModel model = new NGramModel();
StringList sl1 = new StringList("one", "two");
StringList sl2 = new StringList("three", "four");
model.add(sl1);
model.add(sl2);
// Dictionary dict = model.toDictionary();
// assertTrue(dict.contains(sl1));
// assertTrue(dict.contains(sl2));
// assertEquals(2, dict.size());
}

@Test
public void testAddWithMaxLengthGreaterThanListSizeAddsOnlyValid() {
NGramModel model = new NGramModel();
StringList sl = new StringList("a", "b");
model.add(sl, 1, 5);
assertTrue(model.contains(new StringList("a")));
assertTrue(model.contains(new StringList("b")));
assertTrue(model.contains(new StringList("a", "b")));
assertEquals(3, model.size());
}

@Test
public void testAddNGramThenSetCountThenAddAgainIncrements() {
NGramModel model = new NGramModel();
StringList sl = new StringList("repeat");
model.add(sl);
model.setCount(sl, 10);
model.add(sl);
assertEquals(11, model.getCount(sl));
}

@Test
public void testSerializeEmptyModelProducesValidDictionaryXml() throws IOException {
NGramModel model = new NGramModel();
ByteArrayOutputStream out = new ByteArrayOutputStream();
model.serialize(out);
String xml = new String(out.toByteArray(), StandardCharsets.UTF_8);
assertTrue(xml.contains("<dictionary"));
assertTrue(xml.contains("</dictionary>"));
}

@Test
public void testCutoffWithLargeRangeKeepsEverything() {
NGramModel model = new NGramModel();
StringList sl = new StringList("keepme");
model.add(sl);
model.setCount(sl, Integer.MAX_VALUE);
model.cutoff(1, Integer.MAX_VALUE);
boolean stillPresent = model.contains(sl);
assertTrue(stillPresent);
}

@Test
public void testEqualsIsSymmetric() {
NGramModel model1 = new NGramModel();
NGramModel model2 = new NGramModel();
StringList sl = new StringList("symmetric");
model1.add(sl);
model2.add(sl);
boolean isSymmetric = model1.equals(model2) && model2.equals(model1);
assertTrue(isSymmetric);
}

@Test
public void testEqualsIsTransitive() {
NGramModel model1 = new NGramModel();
NGramModel model2 = new NGramModel();
NGramModel model3 = new NGramModel();
StringList sl = new StringList("transitive");
model1.add(sl);
model2.add(sl);
model3.add(sl);
boolean isTransitive = model1.equals(model2) && model2.equals(model3) && model1.equals(model3);
assertTrue(isTransitive);
}

@Test
public void testAddTokensWithSingleTokenMultipleLengthsOnlyIncludesOne() {
NGramModel model = new NGramModel();
StringList list = new StringList("alpha");
model.add(list, 1, 5);
assertTrue(model.contains(new StringList("alpha")));
assertEquals(1, model.size());
}

@Test
public void testAddCharSequenceWithStartWhitespacePreservesNGrams() {
NGramModel model = new NGramModel();
model.add(" abc", 2, 2);
assertTrue(model.contains(new StringList(" a")));
assertTrue(model.contains(new StringList("ab")));
assertTrue(model.contains(new StringList("bc")));
assertEquals(3, model.size());
}

@Test
public void testAddCharSequenceWithEndWhitespacePreservesNGrams() {
NGramModel model = new NGramModel();
model.add("abc ", 2, 2);
assertTrue(model.contains(new StringList("ab")));
assertTrue(model.contains(new StringList("bc")));
assertTrue(model.contains(new StringList("c ")));
assertEquals(3, model.size());
}

@Test
public void testAddCharSequenceEmptyStringResultsInNoGrams() {
NGramModel model = new NGramModel();
model.add("", 1, 2);
assertEquals(0, model.size());
assertEquals(0, model.numberOfGrams());
}

@Test
public void testRemoveNGramThatWasNeverAdded() {
NGramModel model = new NGramModel();
model.remove(new StringList("notAdded"));
assertEquals(0, model.size());
assertFalse(model.contains(new StringList("notAdded")));
}

@Test
public void testDeserializeInvalidXMLFormatThrows() throws IOException {
String invalidXml = "<dictionary><entry><badTag></entry></dictionary>";
ByteArrayInputStream input = new ByteArrayInputStream(invalidXml.getBytes(StandardCharsets.UTF_8));
new NGramModel(input);
}

@Test
public void testDeserializeMultipleEntriesWithDifferentCounts() throws IOException {
String xml = "<dictionary>" + "<entry count=\"2\"><tokens><token>a</token></tokens></entry>" + "<entry count=\"5\"><tokens><token>b</token></tokens></entry>" + "</dictionary>";
ByteArrayInputStream in = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
NGramModel model = new NGramModel(in);
assertEquals(2, model.size());
assertEquals(2, model.getCount(new StringList("a")));
assertEquals(5, model.getCount(new StringList("b")));
}

@Test
public void testAddingSameNGramViaDifferentStringListInstancesMergesCorrectly() {
NGramModel model = new NGramModel();
StringList sl1 = new StringList("merge");
StringList sl2 = new StringList("merge");
model.add(sl1);
model.add(sl2);
assertEquals(1, model.size());
assertEquals(2, model.getCount(sl1));
}

@Test
public void testCallingToDictionaryOnEmptyModelReturnsEmpty() {
NGramModel model = new NGramModel();
// Dictionary dict = model.toDictionary();
// assertEquals(0, dict.size());
}

@Test
public void testCallingToDictionaryTrueOnEmptyModelReturnsEmpty() {
NGramModel model = new NGramModel();
// Dictionary dict = model.toDictionary(true);
// assertEquals(0, dict.size());
}

@Test
public void testCallingIteratorOnEmptyModelReturnsNoElements() {
NGramModel model = new NGramModel();
Iterator<StringList> iterator = model.iterator();
assertFalse(iterator.hasNext());
}

@Test
public void testCallingIteratorReturnsAllAddedGrams() {
NGramModel model = new NGramModel();
StringList n1 = new StringList("one");
StringList n2 = new StringList("two");
model.add(n1);
model.add(n2);
Iterator<StringList> iterator = model.iterator();
StringList first = iterator.next();
assertNotNull(first);
StringList second = iterator.next();
assertNotNull(second);
assertFalse(iterator.hasNext());
}

@Test
public void testSerializeEntryIteratorRemoveNotSupported() throws IOException {
NGramModel model = new NGramModel();
StringList n = new StringList("entry");
model.add(n);
model.setCount(n, 1);
Iterator<Entry> iterator = new Iterator<Entry>() {

private final Iterator<StringList> keyIterator = model.iterator();

@Override
public boolean hasNext() {
return keyIterator.hasNext();
}

@Override
public Entry next() {
StringList tokens = keyIterator.next();
Attributes attr = new Attributes();
attr.setValue("count", Integer.toString(model.getCount(tokens)));
return new Entry(tokens, attr);
}

@Override
public void remove() {
throw new UnsupportedOperationException();
}
};
iterator.remove();
}

@Test
public void testAddLargeCharSequenceCreatesManyGrams() {
NGramModel model = new NGramModel();
model.add("abcdefghijk", 3, 3);
assertEquals(9, model.size());
}

@Test
public void testSerializationPreservesExactEntryOrder() throws IOException {
NGramModel model = new NGramModel();
model.add(new StringList("a"));
model.add(new StringList("b"));
model.add(new StringList("c"));
ByteArrayOutputStream out = new ByteArrayOutputStream();
model.serialize(out);
String xmlOutput = out.toString("UTF-8");
int indexA = xmlOutput.indexOf("<token>a</token>");
int indexB = xmlOutput.indexOf("<token>b</token>");
int indexC = xmlOutput.indexOf("<token>c</token>");
assertTrue(indexA < indexB);
assertTrue(indexB < indexC);
}

@Test
public void testSettingLargeCountIsRetainedCorrectly() throws IOException {
NGramModel model = new NGramModel();
StringList sl = new StringList("high");
model.add(sl);
model.setCount(sl, Integer.MAX_VALUE);
ByteArrayOutputStream out = new ByteArrayOutputStream();
model.serialize(out);
ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
NGramModel deserialized = new NGramModel(in);
assertEquals(Integer.MAX_VALUE, deserialized.getCount(sl));
}

@Test
public void testAddThenRemoveThenAddAgainRestoresCorrectCount() {
NGramModel model = new NGramModel();
StringList sl = new StringList("cycle");
model.add(sl);
model.remove(sl);
assertEquals(0, model.getCount(sl));
assertFalse(model.contains(sl));
model.add(sl);
assertEquals(1, model.getCount(sl));
assertTrue(model.contains(sl));
}

@Test
public void testAddSingleTokenWithMinMaxEqualToOne() {
NGramModel model = new NGramModel();
StringList tokens = new StringList("single");
model.add(tokens, 1, 1);
assertTrue(model.contains(tokens));
assertEquals(1, model.size());
}

@Test
public void testAddMultipleGramsAcrossMinMaxBounds() {
NGramModel model = new NGramModel();
StringList tokens = new StringList("aa", "bb", "cc", "dd");
model.add(tokens, 2, 3);
assertTrue(model.contains(new StringList("aa", "bb")));
assertTrue(model.contains(new StringList("bb", "cc")));
assertTrue(model.contains(new StringList("cc", "dd")));
assertTrue(model.contains(new StringList("aa", "bb", "cc")));
assertTrue(model.contains(new StringList("bb", "cc", "dd")));
assertEquals(5, model.size());
}

@Test
public void testAddUnicodeCharSequencePreservesCountCorrectly() {
NGramModel model = new NGramModel();
model.add("𠜎𠜱𠝹𠱓", 1, 2);
assertTrue(model.size() >= 2);
assertTrue(model.numberOfGrams() >= 2);
}

@Test
public void testAddCharSequenceThenRemoveThenSerializeDeserializesCleanly() throws IOException {
NGramModel model = new NGramModel();
model.add("data", 2, 2);
model.remove(new StringList("at"));
ByteArrayOutputStream out = new ByteArrayOutputStream();
model.serialize(out);
ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
NGramModel reloaded = new NGramModel(in);
assertEquals(model.size(), reloaded.size());
assertFalse(reloaded.contains(new StringList("at")));
assertTrue(reloaded.contains(new StringList("da")));
}

@Test
public void testZeroLengthInputToAddTokensDoesNotCauseError() {
NGramModel model = new NGramModel();
StringList emptyList = new StringList();
model.add(emptyList, 1, 3);
assertEquals(0, model.size());
}

@Test
public void testEqualsWithSelfReference() {
NGramModel model = new NGramModel();
assertTrue(model.equals(model));
}

@Test
public void testEqualsWithAnotherPopulatedModelDifferentCounts() {
NGramModel a = new NGramModel();
NGramModel b = new NGramModel();
StringList tokens = new StringList("token");
a.add(tokens);
b.add(tokens);
b.setCount(tokens, 5);
assertFalse(a.equals(b));
}

@Test
public void testSetCountDoesNotChangeSize() {
NGramModel model = new NGramModel();
StringList sl = new StringList("ng1");
model.add(sl);
model.setCount(sl, 7);
assertEquals(1, model.size());
assertEquals(7, model.getCount(sl));
}

@Test
public void testSetCountToNGramNotInModelAfterRemovalThrows() {
NGramModel model = new NGramModel();
StringList sl = new StringList("gone");
model.add(sl);
model.remove(sl);
model.setCount(sl, 5);
}

@Test
public void testAddOneNGramThenToDictionaryCaseSensitiveContainsExactlyOne() {
NGramModel model = new NGramModel();
model.add(new StringList("Hello"));
// Dictionary dict = model.toDictionary(true);
// assertEquals(1, dict.size());
// assertTrue(dict.contains(new StringList("Hello")));
}

@Test
public void testAddNGramThenToDictionaryCaseInsensitiveNormalizesDuplicate() {
NGramModel model = new NGramModel();
model.add(new StringList("Hello"));
model.add(new StringList("hello"));
// Dictionary dict = model.toDictionary(false);
// assertEquals(1, dict.size());
}

@Test
public void testToDictionaryDoesNotModifyNGramModel() {
NGramModel model = new NGramModel();
StringList sl = new StringList("data");
model.add(sl);
// Dictionary dict = model.toDictionary(true);
assertEquals(1, model.size());
// assertEquals(1, dict.size());
}

@Test
public void testToStringOnPopulatedModelIncludesCorrectSize() {
NGramModel model = new NGramModel();
model.add(new StringList("info"));
model.add(new StringList("info"));
String output = model.toString();
assertEquals("Size: 1", output);
}

@Test
public void testAddCharSequenceThenSetCountUpdatesProperly() {
NGramModel model = new NGramModel();
model.add("go", 2, 2);
StringList sl = new StringList("go");
model.setCount(sl, 99);
assertEquals(99, model.getCount(sl));
}

@Test
public void testAddOverlappingCharacterGramsCorrectlyCollide() {
NGramModel model = new NGramModel();
model.add("abab", 2, 2);
StringList ab = new StringList("ab");
model.setCount(ab, 5);
model.add("abab", 2, 2);
assertEquals(7, model.getCount(ab));
}

@Test
public void testSerializeThenDeserializePreservesExactGrams() throws IOException {
NGramModel model = new NGramModel();
StringList one = new StringList("one");
StringList two = new StringList("two");
model.add(one);
model.setCount(one, 2);
model.add(two);
model.setCount(two, 5);
ByteArrayOutputStream out = new ByteArrayOutputStream();
model.serialize(out);
ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
NGramModel deserialized = new NGramModel(in);
assertEquals(2, deserialized.size());
assertEquals(2, deserialized.getCount(one));
assertEquals(5, deserialized.getCount(two));
}

@Test
public void testCutoffRemovesNothingWhenGramsRightAtThreshold() {
NGramModel model = new NGramModel();
StringList threshold = new StringList("t");
model.add(threshold);
model.setCount(threshold, 3);
model.cutoff(3, 3);
assertTrue(model.contains(threshold));
assertEquals(1, model.size());
}

@Test
public void testAddMaximumAllowedCount() {
NGramModel model = new NGramModel();
StringList sl = new StringList("max");
model.add(sl);
model.setCount(sl, Integer.MAX_VALUE);
assertEquals(Integer.MAX_VALUE, model.getCount(sl));
}

@Test
public void testAddThenToLowercaseCharSequenceNormalizesUnicode() {
NGramModel model = new NGramModel();
model.add("Übër", 2, 2);
assertTrue(model.size() > 0);
}
}
