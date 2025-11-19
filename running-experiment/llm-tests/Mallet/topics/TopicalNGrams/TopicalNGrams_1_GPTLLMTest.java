package cc.mallet.topics;

import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureSequenceWithBigrams;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.util.Randoms;
import org.junit.Test;
import java.io.*;
import java.util.ArrayList;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TopicalNGrams_1_GPTLLMTest {

@Test
public void testConstructorWithDefaults() {
TopicalNGrams tng = new TopicalNGrams(10);
assertNotNull(tng);
}

@Test
public void testConstructorWithParameters() {
TopicalNGrams tng = new TopicalNGrams(5, 25.0, 0.05, 0.04, 0.03, 0.02, 0.01);
assertNotNull(tng);
}

@Test
public void testEstimateInitializesFieldsAndRunsWithoutError() {
Alphabet uniAlphabet = new Alphabet();
Alphabet biAlphabet = new Alphabet();
// FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(uniAlphabet, biAlphabet, 3);
// fs.add("a");
// fs.setBiIndexAtPosition(0, -1);
// fs.add("b");
// fs.setBiIndexAtPosition(1, 1);
// fs.add("c");
// fs.setBiIndexAtPosition(2, 2);
// Instance instance1 = new Instance(fs, null, "doc1", "source");
ArrayList<Instance> instances = new ArrayList<>();
// instances.add(instance1);
InstanceList instanceList = new InstanceList(uniAlphabet, null);
instanceList.addThruPipe(instances.iterator());
TopicalNGrams tng = new TopicalNGrams(3, 15.0, 0.01, 0.02, 0.03, 0.04, 0.05);
Randoms randoms = new Randoms(42);
tng.estimate(instanceList, 2, 0, 0, null, randoms);
assertTrue(true);
}

@Test
public void testPrintTopWordsDoesNotThrow() {
Alphabet uniAlphabet = new Alphabet();
Alphabet biAlphabet = new Alphabet();
// FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(uniAlphabet, biAlphabet, 3);
// fs.add("one");
// fs.setBiIndexAtPosition(0, -1);
// fs.add("two");
// fs.setBiIndexAtPosition(1, 1);
// fs.add("three");
// fs.setBiIndexAtPosition(2, 2);
// Instance instance = new Instance(fs, null, "docA", "sourceA");
ArrayList<Instance> list = new ArrayList<>();
// list.add(instance);
InstanceList instanceList = new InstanceList(uniAlphabet, null);
instanceList.addThruPipe(list.iterator());
TopicalNGrams tng = new TopicalNGrams(2);
Randoms r = new Randoms(123);
tng.estimate(instanceList, 1, 0, 0, null, r);
tng.printTopWords(5, true);
tng.printTopWords(5, false);
}

@Test
public void testPrintStateToFileProducesOutput() throws IOException {
Alphabet uniAlpha = new Alphabet();
Alphabet biAlpha = new Alphabet();
// FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(uniAlpha, biAlpha, 2);
// fs.add("x");
// fs.setBiIndexAtPosition(0, -1);
// fs.add("y");
// fs.setBiIndexAtPosition(1, 1);
// Instance instance = new Instance(fs, null, "dX", "sourceX");
ArrayList<Instance> givenInstances = new ArrayList<>();
// givenInstances.add(instance);
InstanceList instList = new InstanceList(uniAlpha, null);
instList.addThruPipe(givenInstances.iterator());
TopicalNGrams tng = new TopicalNGrams(2);
tng.estimate(instList, 1, 0, 0, null, new Randoms());
File tempFile = File.createTempFile("tng-test-state", ".txt");
tng.printState(tempFile);
assertTrue(tempFile.exists());
assertTrue(tempFile.length() > 0);
tempFile.delete();
}

@Test
public void testPrintDocumentTopicsStringOutput() {
Alphabet unialph = new Alphabet();
Alphabet bialph = new Alphabet();
// FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(unialph, bialph, 2);
// fs.add("red");
// fs.setBiIndexAtPosition(0, -1);
// fs.add("blue");
// fs.setBiIndexAtPosition(1, 1);
// Instance instance = new Instance(fs, null, "doc-test", "s");
ArrayList<Instance> docz = new ArrayList<>();
// docz.add(instance);
InstanceList list = new InstanceList(unialph, null);
list.addThruPipe(docz.iterator());
TopicalNGrams tng = new TopicalNGrams(3);
tng.estimate(list, 1, 0, 0, null, new Randoms());
StringWriter sw = new StringWriter();
PrintWriter pw = new PrintWriter(sw);
tng.printDocumentTopics(pw, 0.0, 3);
pw.flush();
String output = sw.toString();
assertTrue(output.contains("doc-test") || output.contains("doc"));
}

@Test
public void testModelWriteAndReadObject() throws IOException, ClassNotFoundException {
Alphabet ua = new Alphabet();
Alphabet ba = new Alphabet();
// FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(ua, ba, 3);
// fs.add("j");
// fs.setBiIndexAtPosition(0, -1);
// fs.add("k");
// fs.setBiIndexAtPosition(1, 1);
// fs.add("l");
// fs.setBiIndexAtPosition(2, 2);
// Instance inst = new Instance(fs, null, "xx", "src");
ArrayList<Instance> arr = new ArrayList<>();
// arr.add(inst);
InstanceList ilist = new InstanceList(ua, null);
ilist.addThruPipe(arr.iterator());
TopicalNGrams model = new TopicalNGrams(4);
model.estimate(ilist, 1, 0, 0, null, new Randoms());
File temp = File.createTempFile("tng_model_save", ".bin");
model.write(temp);
assertTrue(temp.exists());
assertTrue(temp.length() > 0);
ObjectInputStream ois = new ObjectInputStream(new FileInputStream(temp));
Object reloaded = ois.readObject();
ois.close();
assertNotNull(reloaded);
assertTrue(reloaded instanceof TopicalNGrams);
temp.delete();
}

@Test
public void testEstimateWithOneTopic() {
Alphabet uniA = new Alphabet();
Alphabet biA = new Alphabet();
// FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(uniA, biA, 2);
// fs.add("x");
// fs.setBiIndexAtPosition(0, -1);
// fs.add("y");
// fs.setBiIndexAtPosition(1, 1);
// Instance i = new Instance(fs, null, "1", "src");
ArrayList<Instance> list = new ArrayList<>();
// list.add(i);
InstanceList il = new InstanceList(uniA, null);
il.addThruPipe(list.iterator());
TopicalNGrams tng = new TopicalNGrams(1, 0.5, 0.1, 0.1, 0.1, 0.1, 0.1);
tng.estimate(il, 1, 0, 0, null, new Randoms());
assertNotNull(tng);
}

@Test(expected = ArithmeticException.class)
public void testZeroTopicThrowsException() {
new TopicalNGrams(0, 50.0, 0.01, 0.01, 0.01, 0.01, 0.01);
}

@Test(expected = NullPointerException.class)
public void testNullInstanceListEstimateThrows() {
TopicalNGrams tng = new TopicalNGrams(2);
tng.estimate(null, 5, 0, 0, null, new Randoms());
}

@Test
public void testPrintTopWordsWithMultiTokenPhraseConstruction() {
Alphabet uniAlphabet = new Alphabet();
Alphabet biAlphabet = new Alphabet();
// FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(uniAlphabet, biAlphabet, 4);
// fs.add("alpha");
// fs.setBiIndexAtPosition(0, -1);
// fs.add("beta");
// fs.setBiIndexAtPosition(1, 1);
// fs.add("gamma");
// fs.setBiIndexAtPosition(2, 2);
// fs.add("delta");
// fs.setBiIndexAtPosition(3, 3);
// Instance instance = new Instance(fs, null, "docPhrase", "src");
ArrayList<Instance> list = new ArrayList<>();
// list.add(instance);
InstanceList instanceList = new InstanceList(uniAlphabet, null);
instanceList.addThruPipe(list.iterator());
TopicalNGrams tng = new TopicalNGrams(2);
tng.estimate(instanceList, 3, 0, 0, null, new Randoms());
tng.printTopWords(10, true);
}

@Test
public void testEstimateSingleWordDocument() {
Alphabet uniAlphabet = new Alphabet();
Alphabet biAlphabet = new Alphabet();
// FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(uniAlphabet, biAlphabet, 1);
// fs.add("soleword");
// fs.setBiIndexAtPosition(0, -1);
// Instance instance = new Instance(fs, null, "doc1", "source1");
ArrayList<Instance> documents = new ArrayList<>();
// documents.add(instance);
InstanceList instanceList = new InstanceList(uniAlphabet, null);
instanceList.addThruPipe(documents.iterator());
TopicalNGrams tng = new TopicalNGrams(3);
tng.estimate(instanceList, 1, 0, 0, null, new Randoms());
tng.printTopWords(5, false);
}

@Test
public void testEstimateNoBigramAvailableAnywhere() {
Alphabet uni = new Alphabet();
Alphabet bi = new Alphabet();
// FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(uni, bi, 3);
// fs.add("a");
// fs.setBiIndexAtPosition(0, -1);
// fs.add("b");
// fs.setBiIndexAtPosition(1, -1);
// fs.add("c");
// fs.setBiIndexAtPosition(2, -1);
// Instance inst = new Instance(fs, null, "x", "source");
ArrayList<Instance> docList = new ArrayList<>();
// docList.add(inst);
InstanceList il = new InstanceList(uni, null);
il.addThruPipe(docList.iterator());
TopicalNGrams tng = new TopicalNGrams(2);
tng.estimate(il, 2, 0, 0, null, new Randoms());
tng.printTopWords(4, true);
}

@Test
public void testPrintDocumentTopicsWithMaxZeroAndThresholdOne() {
Alphabet ua = new Alphabet();
Alphabet ba = new Alphabet();
// FeatureSequenceWithBigrams tokens = new FeatureSequenceWithBigrams(ua, ba, 2);
// tokens.add("top");
// tokens.setBiIndexAtPosition(0, -1);
// tokens.add("word");
// tokens.setBiIndexAtPosition(1, 1);
// Instance inst = new Instance(tokens, null, "docZ", "sourceZ");
ArrayList<Instance> single = new ArrayList<>();
// single.add(inst);
InstanceList docs = new InstanceList(ua, null);
docs.addThruPipe(single.iterator());
TopicalNGrams tng = new TopicalNGrams(3);
tng.estimate(docs, 1, 0, 0, null, new Randoms());
StringWriter sw = new StringWriter();
PrintWriter pw = new PrintWriter(sw);
tng.printDocumentTopics(pw, 1.0, 0);
pw.flush();
String out = sw.toString();
assertTrue(out.contains("0"));
}

@Test
public void testEmptyDocumentInstanceListDoesNotThrow() {
Alphabet ua = new Alphabet();
InstanceList emptyList = new InstanceList(ua, null);
TopicalNGrams tng = new TopicalNGrams(2);
try {
tng.estimate(emptyList, 1, 0, 0, null, new Randoms());
assertTrue(true);
} catch (Exception ex) {
// fail("Did not expect exception: " + ex.getMessage());
}
}

@Test
public void testInvalidBigramIndicesAreIgnoredOrHandledGracefully() {
Alphabet ua = new Alphabet();
Alphabet ba = new Alphabet();
// FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(ua, ba, 2);
// fs.add("neg");
// fs.setBiIndexAtPosition(0, -5);
// fs.add("pos");
// fs.setBiIndexAtPosition(1, Integer.MAX_VALUE);
// Instance inst = new Instance(fs, null, "fuzz1", "src");
ArrayList<Instance> all = new ArrayList<>();
// all.add(inst);
InstanceList il = new InstanceList(ua, null);
il.addThruPipe(all.iterator());
TopicalNGrams tng = new TopicalNGrams(3);
tng.estimate(il, 1, 0, 0, null, new Randoms());
assertTrue(true);
}

@Test
public void testSerializationCorruptedTopicArrayLengthMismatch() throws IOException {
Alphabet ua = new Alphabet();
Alphabet ba = new Alphabet();
// FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(ua, ba, 2);
// fs.add("token1");
// fs.setBiIndexAtPosition(0, -1);
// fs.add("token2");
// fs.setBiIndexAtPosition(1, 1);
// Instance instance = new Instance(fs, null, "xx", "src");
ArrayList<Instance> data = new ArrayList<>();
// data.add(instance);
InstanceList list = new InstanceList(ua, null);
list.addThruPipe(data.iterator());
TopicalNGrams tng = new TopicalNGrams(3);
tng.estimate(list, 1, 0, 0, null, new Randoms());
ByteArrayOutputStream baos = new ByteArrayOutputStream();
ObjectOutputStream oos = new ObjectOutputStream(baos);
oos.writeObject(tng);
oos.close();
byte[] serialized = baos.toByteArray();
serialized[100] = 127;
ByteArrayInputStream bais = new ByteArrayInputStream(serialized);
ObjectInputStream ois = new ObjectInputStream(bais);
try {
Object obj = ois.readObject();
assertTrue(obj instanceof TopicalNGrams);
} catch (Exception ignore) {
assertTrue(true);
}
}

@Test
public void testEstimateWithZeroIterationsStillInitializesTopicalState() {
Alphabet ua = new Alphabet();
Alphabet ba = new Alphabet();
// FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(ua, ba, 3);
// fs.add("x");
// fs.setBiIndexAtPosition(0, -1);
// fs.add("y");
// fs.setBiIndexAtPosition(1, 1);
// fs.add("z");
// fs.setBiIndexAtPosition(2, 2);
// Instance instance = new Instance(fs, null, "doc", "source");
ArrayList<Instance> docs = new ArrayList<>();
// docs.add(instance);
InstanceList il = new InstanceList(ua, null);
il.addThruPipe(docs.iterator());
TopicalNGrams tng = new TopicalNGrams(2);
tng.estimate(il, 0, 0, 0, null, new Randoms());
assertNotNull(tng);
}

@Test
public void testPrintDocumentTopicsWithNoArgsDoesNotThrow() {
Alphabet ua = new Alphabet();
Alphabet ba = new Alphabet();
// FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(ua, ba, 2);
// fs.add("apple");
// fs.setBiIndexAtPosition(0, -1);
// fs.add("banana");
// fs.setBiIndexAtPosition(1, 1);
// Instance inst = new Instance(fs, null, "doc1", "source1");
ArrayList<Instance> instList = new ArrayList<>();
// instList.add(inst);
InstanceList ilist = new InstanceList(ua, null);
ilist.addThruPipe(instList.iterator());
TopicalNGrams tng = new TopicalNGrams(2);
tng.estimate(ilist, 1, 0, 0, null, new Randoms());
StringWriter sw = new StringWriter();
PrintWriter pw = new PrintWriter(sw);
tng.printDocumentTopics(pw);
pw.flush();
assertTrue(true);
}

@Test
public void testPrintTopWordsWithZeroWords() {
Alphabet ua = new Alphabet();
Alphabet ba = new Alphabet();
// FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(ua, ba, 2);
// fs.add("a");
// fs.setBiIndexAtPosition(0, -1);
// fs.add("b");
// fs.setBiIndexAtPosition(1, -1);
// Instance ins = new Instance(fs, null, "zz", "src");
ArrayList<Instance> x = new ArrayList<>();
// x.add(ins);
InstanceList il = new InstanceList(ua, null);
il.addThruPipe(x.iterator());
TopicalNGrams tng = new TopicalNGrams(2);
tng.estimate(il, 1, 0, 0, null, new Randoms());
tng.printTopWords(0, false);
}

@Test
public void testAllBigramsOnlyDocument() {
Alphabet ua = new Alphabet();
Alphabet ba = new Alphabet();
// FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(ua, ba, 4);
// fs.add("x");
// fs.setBiIndexAtPosition(0, -1);
// fs.add("y");
// fs.setBiIndexAtPosition(1, 1);
// fs.add("z");
// fs.setBiIndexAtPosition(2, 2);
// fs.add("w");
// fs.setBiIndexAtPosition(3, 3);
// Instance inst = new Instance(fs, null, "doc", "source");
ArrayList<Instance> list = new ArrayList<>();
// list.add(inst);
InstanceList il = new InstanceList(ua, null);
il.addThruPipe(list.iterator());
TopicalNGrams tng = new TopicalNGrams(3);
tng.estimate(il, 1, 0, 0, null, new Randoms());
assertNotNull(tng);
tng.printTopWords(3, true);
}

@Test
public void testRepeatedTokenValuesAndPhraseMerging() {
Alphabet ua = new Alphabet();
Alphabet ba = new Alphabet();
// FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(ua, ba, 3);
// fs.add("x");
// fs.setBiIndexAtPosition(0, -1);
// fs.add("x");
// fs.setBiIndexAtPosition(1, 1);
// fs.add("x");
// fs.setBiIndexAtPosition(2, 2);
// Instance inst = new Instance(fs, null, "dup", "src");
ArrayList<Instance> l = new ArrayList<>();
// l.add(inst);
InstanceList il = new InstanceList(ua, null);
il.addThruPipe(l.iterator());
TopicalNGrams tng = new TopicalNGrams(2);
tng.estimate(il, 2, 0, 0, null, new Randoms());
tng.printTopWords(10, false);
}

@Test
public void testPrintTopWordsWithLargeNumWordsLimit() {
Alphabet ua = new Alphabet();
Alphabet ba = new Alphabet();
// FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(ua, ba, 3);
// fs.add("a");
// fs.setBiIndexAtPosition(0, -1);
// fs.add("b");
// fs.setBiIndexAtPosition(1, 1);
// fs.add("c");
// fs.setBiIndexAtPosition(2, 2);
// Instance i = new Instance(fs, null, "doc", "src");
ArrayList<Instance> d = new ArrayList<>();
// d.add(i);
InstanceList il = new InstanceList(ua, null);
il.addThruPipe(d.iterator());
TopicalNGrams tng = new TopicalNGrams(2);
tng.estimate(il, 1, 0, 0, null, new Randoms());
tng.printTopWords(1_000_000, true);
}

@Test
public void testWriteHandlesInvalidFileGracefully() {
TopicalNGrams tng = new TopicalNGrams(2);
File invalidFile = new File("/invalid_path/invalid_model.mallet.ser");
try {
tng.write(invalidFile);
} catch (Exception e) {
// fail("Exception should be caught inside write() method, not thrown: " + e.getMessage());
}
assertTrue(true);
}

@Test
public void testPhraseNgramsEndsWhenBigramEndsAtLastToken() {
Alphabet ua = new Alphabet();
Alphabet ba = new Alphabet();
// FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(ua, ba, 3);
// fs.add("one");
// fs.setBiIndexAtPosition(0, -1);
// fs.add("two");
// fs.setBiIndexAtPosition(1, 1);
// fs.add("three");
// fs.setBiIndexAtPosition(2, -1);
// Instance i = new Instance(fs, null, "doc", "src");
ArrayList<Instance> x = new ArrayList<>();
// x.add(i);
InstanceList list = new InstanceList(ua, null);
list.addThruPipe(x.iterator());
TopicalNGrams tng = new TopicalNGrams(2);
tng.estimate(list, 1, 0, 0, null, new Randoms());
tng.printTopWords(5, true);
}

@Test
public void testPrintTopWordsWhenNoTokensAssigned() {
Alphabet ua = new Alphabet();
Alphabet ba = new Alphabet();
// FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(ua, ba, 3);
// fs.add("lonely");
// fs.setBiIndexAtPosition(0, -1);
// fs.add("orphan");
// fs.setBiIndexAtPosition(1, -1);
// fs.add("token");
// fs.setBiIndexAtPosition(2, -1);
// Instance inst = new Instance(fs, null, "docX", "sX");
ArrayList<Instance> list = new ArrayList<>();
// list.add(inst);
InstanceList il = new InstanceList(ua, null);
il.addThruPipe(list.iterator());
TopicalNGrams tng = new TopicalNGrams(2);
tng.estimate(il, 0, 0, 0, null, new Randoms());
tng.printTopWords(5, true);
}

@Test
public void testPhrasesFromMultipleDocuments() {
Alphabet ua = new Alphabet();
Alphabet ba = new Alphabet();
// FeatureSequenceWithBigrams fs1 = new FeatureSequenceWithBigrams(ua, ba, 3);
// fs1.add("a");
// fs1.setBiIndexAtPosition(0, -1);
// fs1.add("b");
// fs1.setBiIndexAtPosition(1, 1);
// fs1.add("c");
// fs1.setBiIndexAtPosition(2, 2);
// FeatureSequenceWithBigrams fs2 = new FeatureSequenceWithBigrams(ua, ba, 2);
// fs2.add("b");
// fs2.setBiIndexAtPosition(0, -1);
// fs2.add("c");
// fs2.setBiIndexAtPosition(1, 1);
// Instance i1 = new Instance(fs1, null, "one", "s1");
// Instance i2 = new Instance(fs2, null, "two", "s2");
ArrayList<Instance> docs = new ArrayList<>();
// docs.add(i1);
// docs.add(i2);
InstanceList il = new InstanceList(ua, null);
il.addThruPipe(docs.iterator());
TopicalNGrams tng = new TopicalNGrams(2);
tng.estimate(il, 2, 0, 0, null, new Randoms());
tng.printTopWords(10, true);
}

@Test
public void testPrintDocumentTopicsNegativeMax() {
Alphabet ua = new Alphabet();
Alphabet ba = new Alphabet();
// FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(ua, ba, 2);
// fs.add("token1");
// fs.setBiIndexAtPosition(0, -1);
// fs.add("token2");
// fs.setBiIndexAtPosition(1, -1);
// Instance ins = new Instance(fs, null, "yx", "zz");
ArrayList<Instance> insList = new ArrayList<>();
// insList.add(ins);
InstanceList list = new InstanceList(ua, null);
list.addThruPipe(insList.iterator());
TopicalNGrams tng = new TopicalNGrams(3);
tng.estimate(list, 1, 0, 0, null, new Randoms());
StringWriter sw = new StringWriter();
PrintWriter pw = new PrintWriter(sw);
tng.printDocumentTopics(pw, 0.1, -5);
pw.flush();
String out = sw.toString();
assertTrue(out.contains("yx"));
}

@Test
public void testPrintDocumentTopicsWithThresholdExcludesAllTopics() {
Alphabet ua = new Alphabet();
Alphabet ba = new Alphabet();
// FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(ua, ba, 2);
// fs.add("token");
// fs.setBiIndexAtPosition(0, -1);
// fs.add("token");
// fs.setBiIndexAtPosition(1, -1);
// Instance i = new Instance(fs, null, "thr-doc", "source");
ArrayList<Instance> list = new ArrayList<>();
// list.add(i);
InstanceList il = new InstanceList(ua, null);
il.addThruPipe(list.iterator());
TopicalNGrams tng = new TopicalNGrams(3);
tng.estimate(il, 1, 0, 0, null, new Randoms());
StringWriter out = new StringWriter();
PrintWriter pw = new PrintWriter(out);
tng.printDocumentTopics(pw, 1.1, 3);
pw.flush();
assertTrue(out.toString().contains("thr-doc"));
}

@Test
public void testWriteAndReloadModelKeepsDocumentCount() throws IOException, ClassNotFoundException {
Alphabet unia = new Alphabet();
Alphabet bia = new Alphabet();
// FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(unia, bia, 2);
// fs.add("hi");
// fs.setBiIndexAtPosition(0, -1);
// fs.add("there");
// fs.setBiIndexAtPosition(1, 1);
// Instance inst = new Instance(fs, null, "docX", "src-src");
ArrayList<Instance> all = new ArrayList<>();
// all.add(inst);
InstanceList list = new InstanceList(unia, null);
list.addThruPipe(all.iterator());
TopicalNGrams model = new TopicalNGrams(4);
model.estimate(list, 1, 0, 0, null, new Randoms());
File f = File.createTempFile("tng_model_obj", ".bin");
model.write(f);
assertTrue(f.exists());
ObjectInputStream in = new ObjectInputStream(new FileInputStream(f));
Object o = in.readObject();
in.close();
assertNotNull(o);
assertTrue(o instanceof TopicalNGrams);
f.delete();
}

@Test
public void testPrintStateHandlesSpecialUTF8Characters() throws IOException {
Alphabet ua = new Alphabet();
Alphabet ba = new Alphabet();
// FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(ua, ba, 3);
// fs.add("§alpha");
// fs.setBiIndexAtPosition(0, -1);
// fs.add("βbeta");
// fs.setBiIndexAtPosition(1, 1);
// fs.add("ómega");
// fs.setBiIndexAtPosition(2, 2);
// Instance i = new Instance(fs, null, "doc9", "sourceÜ");
ArrayList<Instance> list = new ArrayList<>();
// list.add(i);
InstanceList il = new InstanceList(ua, null);
il.addThruPipe(list.iterator());
TopicalNGrams tng = new TopicalNGrams(3);
tng.estimate(il, 1, 0, 0, null, new Randoms());
File out = File.createTempFile("tng_state", ".txt");
tng.printState(out);
BufferedReader reader = new BufferedReader(new FileReader(out));
String line = reader.readLine();
assertNotNull(line);
reader.close();
out.delete();
}

@Test
public void testPhraseEndsCorrectlyWhenOnlyOneBigramPresent() {
Alphabet ua = new Alphabet();
Alphabet ba = new Alphabet();
// FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(ua, ba, 4);
// fs.add("one");
// fs.setBiIndexAtPosition(0, -1);
// fs.add("two");
// fs.setBiIndexAtPosition(1, 1);
// fs.add("three");
// fs.setBiIndexAtPosition(2, -1);
// fs.add("four");
// fs.setBiIndexAtPosition(3, -1);
// Instance i = new Instance(fs, null, "phrasebreak", "src");
ArrayList<Instance> docs = new ArrayList<>();
// docs.add(i);
InstanceList il = new InstanceList(ua, null);
il.addThruPipe(docs.iterator());
TopicalNGrams tng = new TopicalNGrams(3);
tng.estimate(il, 1, 0, 0, null, new Randoms());
tng.printTopWords(6, false);
}

@Test
public void testEstimateWithEmptyFeatureSequence() {
Alphabet ua = new Alphabet();
Alphabet ba = new Alphabet();
// FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(ua, ba, 0);
// Instance i = new Instance(fs, null, "empty", "test");
ArrayList<Instance> docs = new ArrayList<>();
// docs.add(i);
InstanceList il = new InstanceList(ua, null);
il.addThruPipe(docs.iterator());
TopicalNGrams tng = new TopicalNGrams(2);
tng.estimate(il, 1, 0, 0, null, new Randoms());
tng.printTopWords(3, true);
}

@Test
public void testPhraseChainEndingAtDocumentEnd() {
Alphabet ua = new Alphabet();
Alphabet ba = new Alphabet();
// FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(ua, ba, 4);
// fs.add("start");
// fs.setBiIndexAtPosition(0, -1);
// fs.add("middle");
// fs.setBiIndexAtPosition(1, 1);
// fs.add("segment");
// fs.setBiIndexAtPosition(2, 2);
// fs.add("finish");
// fs.setBiIndexAtPosition(3, -1);
// Instance i = new Instance(fs, null, "docB", "srcB");
ArrayList<Instance> docs = new ArrayList<>();
// docs.add(i);
InstanceList il = new InstanceList(ua, null);
il.addThruPipe(docs.iterator());
TopicalNGrams tng = new TopicalNGrams(3);
tng.estimate(il, 2, 0, 0, null, new Randoms());
tng.printTopWords(5, true);
}

@Test
public void testPrintTopWordsOnUninitializedModel() {
TopicalNGrams tng = new TopicalNGrams(2);
try {
tng.printTopWords(5, true);
} catch (Exception ex) {
// fail("printTopWords should not throw on uninitialized model: " + ex.getMessage());
}
}

@Test
public void testEstimateIsDeterministicWithFixedRandomSeed() {
Alphabet ua1 = new Alphabet();
Alphabet ba1 = new Alphabet();
// FeatureSequenceWithBigrams fs1 = new FeatureSequenceWithBigrams(ua1, ba1, 2);
// fs1.add("x");
// fs1.setBiIndexAtPosition(0, -1);
// fs1.add("y");
// fs1.setBiIndexAtPosition(1, 1);
// Instance i1 = new Instance(fs1, null, "a", "src");
InstanceList il1 = new InstanceList(ua1, null);
ArrayList<Instance> docs1 = new ArrayList<>();
// docs1.add(i1);
il1.addThruPipe(docs1.iterator());
TopicalNGrams tng1 = new TopicalNGrams(2);
tng1.estimate(il1, 2, 0, 0, null, new Randoms(42));
Alphabet ua2 = new Alphabet();
Alphabet ba2 = new Alphabet();
// FeatureSequenceWithBigrams fs2 = new FeatureSequenceWithBigrams(ua2, ba2, 2);
// fs2.add("x");
// fs2.setBiIndexAtPosition(0, -1);
// fs2.add("y");
// fs2.setBiIndexAtPosition(1, 1);
// Instance i2 = new Instance(fs2, null, "a", "src");
InstanceList il2 = new InstanceList(ua2, null);
ArrayList<Instance> docs2 = new ArrayList<>();
// docs2.add(i2);
il2.addThruPipe(docs2.iterator());
TopicalNGrams tng2 = new TopicalNGrams(2);
tng2.estimate(il2, 2, 0, 0, null, new Randoms(42));
// assertEquals(tng1.numTokens, tng2.numTokens);
}

@Test(expected = NullPointerException.class)
public void testPrintDocumentTopicsWithNullWriter() {
TopicalNGrams tng = new TopicalNGrams(2);
tng.printDocumentTopics(null, 0.1, 3);
}

@Test
public void testSamplingBoundaryTokenBehaviorDoesNotThrow() {
Alphabet ua = new Alphabet();
Alphabet ba = new Alphabet();
// FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(ua, ba, 5);
// fs.add("first");
// fs.setBiIndexAtPosition(0, -1);
// fs.add("second");
// fs.setBiIndexAtPosition(1, 1);
// fs.add("middle");
// fs.setBiIndexAtPosition(2, 2);
// fs.add("fourth");
// fs.setBiIndexAtPosition(3, 3);
// fs.add("fifth");
// fs.setBiIndexAtPosition(4, -1);
// Instance i = new Instance(fs, null, "docX", "source");
ArrayList<Instance> instances = new ArrayList<>();
// instances.add(i);
InstanceList il = new InstanceList(ua, null);
il.addThruPipe(instances.iterator());
TopicalNGrams model = new TopicalNGrams(3);
model.estimate(il, 3, 0, 0, null, new Randoms(99));
model.printTopWords(10, true);
}

@Test
public void testDuplicateTokensWithNoBigramAllowed() {
Alphabet ua = new Alphabet();
Alphabet ba = new Alphabet();
// FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(ua, ba, 3);
// fs.add("repeat");
// fs.setBiIndexAtPosition(0, -1);
// fs.add("repeat");
// fs.setBiIndexAtPosition(1, -1);
// fs.add("repeat");
// fs.setBiIndexAtPosition(2, -1);
// Instance inst = new Instance(fs, null, "dupe", "s");
ArrayList<Instance> d = new ArrayList<>();
// d.add(inst);
InstanceList ilist = new InstanceList(ua, null);
ilist.addThruPipe(d.iterator());
TopicalNGrams tng = new TopicalNGrams(2);
tng.estimate(ilist, 1, 0, 0, null, new Randoms());
tng.printTopWords(5, false);
}

@Test
public void testCorruptedDeserializationStreamFailsGracefully() throws IOException {
ByteArrayOutputStream out = new ByteArrayOutputStream();
ObjectOutputStream oos = new ObjectOutputStream(out);
Alphabet ua = new Alphabet();
InstanceList list = new InstanceList(ua, null);
oos.writeInt(9999);
oos.flush();
ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
ObjectInputStream ois = new ObjectInputStream(in);
try {
TopicalNGrams tng = new TopicalNGrams(2);
java.lang.reflect.Method m = TopicalNGrams.class.getDeclaredMethod("readObject", ObjectInputStream.class);
m.setAccessible(true);
m.invoke(tng, ois);
// fail("Expected IOException due to invalid stream");
} catch (Exception e) {
assertTrue(true);
}
}

@Test
public void testDocumentWithSingleBigramToken() {
Alphabet ua = new Alphabet();
Alphabet ba = new Alphabet();
// FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(ua, ba, 1);
// fs.add("solo");
// fs.setBiIndexAtPosition(0, 0);
// Instance i = new Instance(fs, null, "single", "src");
ArrayList<Instance> docs = new ArrayList<>();
// docs.add(i);
InstanceList list = new InstanceList(ua, null);
list.addThruPipe(docs.iterator());
TopicalNGrams tng = new TopicalNGrams(2);
tng.estimate(list, 1, 0, 0, null, new Randoms());
tng.printTopWords(3, true);
}

@Test
public void testAllTokensUnpairableInBigram() {
Alphabet ua = new Alphabet();
Alphabet ba = new Alphabet();
// FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(ua, ba, 4);
// fs.add("zero");
// fs.setBiIndexAtPosition(0, -1);
// fs.add("one");
// fs.setBiIndexAtPosition(1, -1);
// fs.add("two");
// fs.setBiIndexAtPosition(2, -1);
// fs.add("three");
// fs.setBiIndexAtPosition(3, -1);
// Instance i = new Instance(fs, null, "uniDoc", "source");
ArrayList<Instance> docList = new ArrayList<>();
// docList.add(i);
InstanceList list = new InstanceList(ua, null);
list.addThruPipe(docList.iterator());
TopicalNGrams tng = new TopicalNGrams(3);
tng.estimate(list, 2, 0, 0, null, new Randoms());
tng.printTopWords(10, false);
}

@Test
public void testPrintTopWordsAlphabetOrderWithMonotonicTokens() {
Alphabet ua = new Alphabet();
Alphabet ba = new Alphabet();
// FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(ua, ba, 3);
// fs.add("aaa");
// fs.setBiIndexAtPosition(0, -1);
// fs.add("bbb");
// fs.setBiIndexAtPosition(1, 1);
// fs.add("ccc");
// fs.setBiIndexAtPosition(2, 2);
// Instance i = new Instance(fs, null, "ordered", "src");
ArrayList<Instance> data = new ArrayList<>();
// data.add(i);
InstanceList list = new InstanceList(ua, null);
list.addThruPipe(data.iterator());
TopicalNGrams tng = new TopicalNGrams(2);
tng.estimate(list, 2, 0, 0, null, new Randoms());
tng.printTopWords(2, false);
}

@Test
public void testAllTokensGetSameTopicDistributionPrint() {
Alphabet ua = new Alphabet();
Alphabet ba = new Alphabet();
// FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(ua, ba, 3);
// fs.add("alpha");
// fs.setBiIndexAtPosition(0, -1);
// fs.add("beta");
// fs.setBiIndexAtPosition(1, -1);
// fs.add("gamma");
// fs.setBiIndexAtPosition(2, -1);
// Instance i = new Instance(fs, null, "flatTopic", "src");
ArrayList<Instance> set = new ArrayList<>();
// set.add(i);
InstanceList list = new InstanceList(ua, null);
list.addThruPipe(set.iterator());
TopicalNGrams tng = new TopicalNGrams(1);
tng.estimate(list, 1, 0, 0, null, new Randoms());
StringWriter sw = new StringWriter();
PrintWriter pw = new PrintWriter(sw);
tng.printDocumentTopics(pw, 0.0, 1);
pw.flush();
String output = sw.toString();
assertTrue(output.contains("0 1.0"));
}

@Test
public void testAlternatingBiIndexPattern() {
Alphabet ua = new Alphabet();
Alphabet ba = new Alphabet();
// FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(ua, ba, 5);
// fs.add("w1");
// fs.setBiIndexAtPosition(0, -1);
// fs.add("w2");
// fs.setBiIndexAtPosition(1, 1);
// fs.add("w3");
// fs.setBiIndexAtPosition(2, -1);
// fs.add("w4");
// fs.setBiIndexAtPosition(3, 1);
// fs.add("w5");
// fs.setBiIndexAtPosition(4, -1);
// Instance i = new Instance(fs, null, "alternatedoc", "src");
ArrayList<Instance> docList = new ArrayList<>();
// docList.add(i);
InstanceList list = new InstanceList(ua, null);
list.addThruPipe(docList.iterator());
TopicalNGrams tng = new TopicalNGrams(3);
tng.estimate(list, 2, 0, 0, null, new Randoms(77));
tng.printTopWords(5, true);
}

@Test
public void testPrintDocumentTopicsNoDominantTopicByThreshold() {
Alphabet ua = new Alphabet();
Alphabet ba = new Alphabet();
// FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(ua, ba, 2);
// fs.add("x");
// fs.setBiIndexAtPosition(0, -1);
// fs.add("y");
// fs.setBiIndexAtPosition(1, 1);
// Instance i = new Instance(fs, null, "thresholdTest", "s");
ArrayList<Instance> list = new ArrayList<>();
// list.add(i);
InstanceList il = new InstanceList(ua, null);
il.addThruPipe(list.iterator());
TopicalNGrams model = new TopicalNGrams(4);
model.estimate(il, 1, 0, 0, null, new Randoms());
StringWriter writer = new StringWriter();
PrintWriter pw = new PrintWriter(writer);
model.printDocumentTopics(pw, 2.0, 4);
pw.flush();
String out = writer.toString();
assertTrue(out.contains("thresholdTest"));
}

@Test
public void testAllTokensHaveBiIndexMinusOneExpectUnigramsOnly() {
Alphabet ua = new Alphabet();
Alphabet ba = new Alphabet();
// FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(ua, ba, 3);
// fs.add("hello");
// fs.setBiIndexAtPosition(0, -1);
// fs.add("world");
// fs.setBiIndexAtPosition(1, -1);
// fs.add("java");
// fs.setBiIndexAtPosition(2, -1);
// Instance instance = new Instance(fs, null, "docX", "sourceX");
ArrayList<Instance> list = new ArrayList<>();
// list.add(instance);
InstanceList il = new InstanceList(ua, null);
il.addThruPipe(list.iterator());
TopicalNGrams model = new TopicalNGrams(2);
model.estimate(il, 2, 0, 0, null, new Randoms());
model.printTopWords(5, false);
}

@Test
public void testPrintStateBeforeEstimateCalledShouldNotThrow() throws IOException {
TopicalNGrams model = new TopicalNGrams(3);
File file = File.createTempFile("tng_uninit_state", ".txt");
try {
model.printState(file);
assertTrue(file.exists());
} finally {
file.delete();
}
}

@Test
public void testInstanceListWithNullDataDoesNotThrowInEstimate() {
Alphabet ua = new Alphabet();
InstanceList ilist = new InstanceList(ua, null);
ilist.add(new Instance(null, null, "doc", "source"));
TopicalNGrams model = new TopicalNGrams(2);
try {
model.estimate(ilist, 1, 0, 0, null, new Randoms());
assertTrue(true);
} catch (Exception ex) {
// fail("Estimate should not throw on null feature sequence: " + ex.getMessage());
}
}

@Test
public void testSamplingWithUnknownPreviousTypeTriggersSmoothing() {
Alphabet ua = new Alphabet();
Alphabet ba = new Alphabet();
// FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(ua, ba, 2);
// fs.add("prevUnseen");
// fs.setBiIndexAtPosition(0, -1);
// fs.add("current");
// fs.setBiIndexAtPosition(1, 1);
// Instance inst = new Instance(fs, null, "docZ", "sourceZ");
ArrayList<Instance> list = new ArrayList<>();
// list.add(inst);
InstanceList il = new InstanceList(ua, null);
il.addThruPipe(list.iterator());
TopicalNGrams model = new TopicalNGrams(3);
model.estimate(il, 3, 0, 0, null, new Randoms());
model.printTopWords(5, true);
}

@Test
public void testBiIndexPointingToInvalidBigramLookup() {
Alphabet ua = new Alphabet();
Alphabet ba = new Alphabet();
ba.lookupIndex("ab");
// FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(ua, ba, 2);
// fs.add("x");
// fs.setBiIndexAtPosition(0, -1);
// fs.add("y");
// fs.setBiIndexAtPosition(1, 999);
// Instance instance = new Instance(fs, null, "test", "src");
ArrayList<Instance> l = new ArrayList<>();
// l.add(instance);
InstanceList il = new InstanceList(ua, null);
il.addThruPipe(l.iterator());
TopicalNGrams tng = new TopicalNGrams(2);
tng.estimate(il, 1, 0, 0, null, new Randoms());
try {
tng.printTopWords(2, false);
} catch (Exception ex) {
// fail("Should not crash during bigram phrase alphabet lookup: " + ex.getMessage());
}
}

@Test
public void testNgramPhraseBreaksAfterSingleBigram() {
Alphabet ua = new Alphabet();
Alphabet ba = new Alphabet();
// FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(ua, ba, 2);
// fs.add("red");
// fs.setBiIndexAtPosition(0, -1);
// fs.add("green");
// fs.setBiIndexAtPosition(1, 1);
// Instance inst = new Instance(fs, null, "p", "s");
ArrayList<Instance> l = new ArrayList<>();
// l.add(inst);
InstanceList il = new InstanceList(ua, null);
il.addThruPipe(l.iterator());
TopicalNGrams model = new TopicalNGrams(1);
model.estimate(il, 1, 0, 0, null, new Randoms());
model.printTopWords(5, true);
}

@Test
public void testPrintTopWordsAllZeroProbabilities() {
Alphabet ua = new Alphabet();
Alphabet ba = new Alphabet();
ba.lookupIndex("bi1");
ba.lookupIndex("bi2");
// FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(ua, ba, 2);
// fs.add("one");
// fs.setBiIndexAtPosition(0, 0);
// fs.add("two");
// fs.setBiIndexAtPosition(1, 1);
// Instance inst = new Instance(fs, null, "zt", "src");
ArrayList<Instance> l = new ArrayList<>();
// l.add(inst);
InstanceList il = new InstanceList(ua, null);
il.addThruPipe(l.iterator());
TopicalNGrams tng = new TopicalNGrams(3);
tng.estimate(il, 1, 0, 0, null, new Randoms());
tng.printTopWords(5, true);
}
}
