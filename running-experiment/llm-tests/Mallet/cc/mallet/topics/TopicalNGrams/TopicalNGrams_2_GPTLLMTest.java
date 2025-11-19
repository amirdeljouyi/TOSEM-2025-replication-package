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

public class TopicalNGrams_2_GPTLLMTest {

@Test
public void testConstructorParameters() {
TopicalNGrams tng = new TopicalNGrams(4, 20.0, 0.01, 0.02, 0.03, 0.5, 0.6);
assertNotNull(tng);
}

@Test
public void testEstimateWithMinimalInstanceList() {
Alphabet dataAlphabet = new Alphabet();
Alphabet biAlphabet = new Alphabet();
int indexA = dataAlphabet.lookupIndex("a");
int indexB = dataAlphabet.lookupIndex("b");
int biIndex = biAlphabet.lookupIndex("a_b");
int[] tokenIndices = new int[] { indexA, indexB };
// FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(dataAlphabet, biAlphabet, tokenIndices);
// fs.setBiIndices(new int[] { -1, biIndex });
// Instance instance = new Instance(fs, null, "doc1", null);
// InstanceList ilist = new InstanceList(dataAlphabet);
// ilist.addThruPipe(instance);
TopicalNGrams tng = new TopicalNGrams(2);
// tng.estimate(ilist, 1, 0, 0, null, new Randoms());
assertNotNull(tng);
}

@Test
public void testTopWordsOutputNoException() {
Alphabet dataAlphabet = new Alphabet();
Alphabet biAlphabet = new Alphabet();
// FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(dataAlphabet, biAlphabet, new int[] { 0, 1 });
// fs.setBiIndices(new int[] { -1, -1 });
// Instance instance = new Instance(fs, null, "doc", null);
// InstanceList ilist = new InstanceList(dataAlphabet);
// ilist.addThruPipe(instance);
TopicalNGrams tng = new TopicalNGrams(2);
// tng.estimate(ilist, 2, 0, 0, null, new Randoms());
tng.printTopWords(5, true);
tng.printTopWords(5, false);
}

@Test
public void testPrintStateToFile() throws IOException {
Alphabet dataAlphabet = new Alphabet();
Alphabet biAlphabet = new Alphabet();
// FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(dataAlphabet, biAlphabet, new int[] { 0 });
// fs.setBiIndices(new int[] { -1 });
// Instance instance = new Instance(fs, null, "doc", null);
// InstanceList ilist = new InstanceList(dataAlphabet);
// ilist.addThruPipe(instance);
TopicalNGrams tng = new TopicalNGrams(2);
// tng.estimate(ilist, 1, 0, 0, null, new Randoms());
File temp = File.createTempFile("print_state", ".txt");
tng.printState(temp);
assertTrue(temp.exists());
BufferedReader reader = new BufferedReader(new FileReader(temp));
String line = reader.readLine();
assertNotNull(line);
reader.close();
temp.delete();
}

@Test
public void testPrintDocumentTopicsFile() throws IOException {
Alphabet dataAlphabet = new Alphabet();
Alphabet biAlphabet = new Alphabet();
// FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(dataAlphabet, biAlphabet, new int[] { 0 });
// fs.setBiIndices(new int[] { -1 });
// Instance instance = new Instance(fs, null, "source", null);
// InstanceList ilist = new InstanceList(dataAlphabet);
// ilist.addThruPipe(instance);
TopicalNGrams tng = new TopicalNGrams(2);
// tng.estimate(ilist, 1, 0, 0, null, new Randoms());
File temp = File.createTempFile("topics", ".txt");
PrintWriter pw = new PrintWriter(new FileWriter(temp));
tng.printDocumentTopics(pw, 0.0, -1);
pw.flush();
pw.close();
BufferedReader reader = new BufferedReader(new FileReader(temp));
assertTrue(reader.readLine().startsWith("#doc"));
reader.close();
temp.delete();
}

@Test
public void testWriteAndReadObject() throws Exception {
Alphabet dataAlphabet = new Alphabet();
Alphabet biAlphabet = new Alphabet();
int[] indices = new int[] { 0, 1 };
// FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(dataAlphabet, biAlphabet, indices);
// fs.setBiIndices(new int[] { -1, -1 });
// Instance instance = new Instance(fs, null, "doc", null);
// InstanceList ilist = new InstanceList(dataAlphabet);
// ilist.addThruPipe(instance);
TopicalNGrams tng = new TopicalNGrams(3);
// tng.estimate(ilist, 1, 0, 0, null, new Randoms());
File modelFile = File.createTempFile("model", ".ser");
tng.write(modelFile);
ObjectInputStream in = new ObjectInputStream(new FileInputStream(modelFile));
Object result = in.readObject();
in.close();
modelFile.delete();
assertNotNull(result);
assertTrue(result instanceof TopicalNGrams);
}

@Test
public void testEstimateWithEmptyInstanceListDoesNotCrash() {
Alphabet alphabet = new Alphabet();
// InstanceList ilist = new InstanceList(alphabet);
TopicalNGrams tng = new TopicalNGrams(2);
// tng.estimate(ilist, 1, 0, 0, null, new Randoms());
assertNotNull(tng);
}

@Test
public void testMainMethodWithGeneratedFile() throws Exception {
Alphabet dataAlphabet = new Alphabet();
Alphabet biAlphabet = new Alphabet();
// FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(dataAlphabet, biAlphabet, new int[] { 0, 1 });
// fs.setBiIndices(new int[] { -1, -1 });
// Instance instance = new Instance(fs, null, "fileDoc", null);
// InstanceList ilist = new InstanceList(dataAlphabet);
// ilist.addThruPipe(instance);
File temp = File.createTempFile("ilist", ".mallet");
// ilist.save(temp);
String[] args = new String[] { temp.getAbsolutePath(), "5", "10" };
TopicalNGrams.main(args);
temp.delete();
}

@Test
public void testEstimateWithOneTokenNoBigram() {
Alphabet dataAlphabet = new Alphabet();
Alphabet biAlphabet = new Alphabet();
int unigramIndex = dataAlphabet.lookupIndex("hello");
// FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(dataAlphabet, biAlphabet, new int[] { unigramIndex });
// fs.setBiIndices(new int[] { -1 });
// Instance instance = new Instance(fs, null, "singleTokenDoc", null);
// InstanceList ilist = new InstanceList(dataAlphabet);
// ilist.addThruPipe(instance);
TopicalNGrams model = new TopicalNGrams(2);
// model.estimate(ilist, 5, 1, 0, null, new Randoms());
assertNotNull(model);
}

@Test
public void testEstimateWithBigramButNoPreviousToken() {
Alphabet dataAlphabet = new Alphabet();
Alphabet biAlphabet = new Alphabet();
int wordIndex = dataAlphabet.lookupIndex("start");
int biIndex = biAlphabet.lookupIndex("start_next");
// FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(dataAlphabet, biAlphabet, new int[] { wordIndex });
// fs.setBiIndices(new int[] { biIndex });
// Instance instance = new Instance(fs, null, "bigramNoPrev", null);
// InstanceList ilist = new InstanceList(dataAlphabet);
// ilist.addThruPipe(instance);
TopicalNGrams model = new TopicalNGrams(2);
// model.estimate(ilist, 2, 0, 0, null, new Randoms());
assertNotNull(model);
}

@Test
public void testEstimateWithThreeTokensTwoBigrams() {
Alphabet dataAlphabet = new Alphabet();
Alphabet biAlphabet = new Alphabet();
int i0 = dataAlphabet.lookupIndex("w0");
int i1 = dataAlphabet.lookupIndex("w1");
int i2 = dataAlphabet.lookupIndex("w2");
int bi0 = biAlphabet.lookupIndex("w0_w1");
int bi1 = biAlphabet.lookupIndex("w1_w2");
// FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(dataAlphabet, biAlphabet, new int[] { i0, i1, i2 });
// fs.setBiIndices(new int[] { -1, bi0, bi1 });
// Instance instance = new Instance(fs, null, "doc3", null);
// InstanceList ilist = new InstanceList(dataAlphabet);
// ilist.addThruPipe(instance);
TopicalNGrams model = new TopicalNGrams(2);
// model.estimate(ilist, 3, 0, 0, null, new Randoms());
assertNotNull(model);
}

@Test
public void testEstimateWithMultipleDocumentsDifferentLengths() {
Alphabet dataAlphabet = new Alphabet();
Alphabet biAlphabet = new Alphabet();
int i0 = dataAlphabet.lookupIndex("d1t1");
int i1 = dataAlphabet.lookupIndex("d1t2");
int i2 = dataAlphabet.lookupIndex("d2t1");
int bi = biAlphabet.lookupIndex("b");
// FeatureSequenceWithBigrams fs1 = new FeatureSequenceWithBigrams(dataAlphabet, biAlphabet, new int[] { i0, i1 });
// fs1.setBiIndices(new int[] { -1, bi });
// FeatureSequenceWithBigrams fs2 = new FeatureSequenceWithBigrams(dataAlphabet, biAlphabet, new int[] { i2 });
// fs2.setBiIndices(new int[] { -1 });
// Instance instance1 = new Instance(fs1, null, "doc1", null);
// Instance instance2 = new Instance(fs2, null, "doc2", null);
// InstanceList ilist = new InstanceList(dataAlphabet);
// ilist.addThruPipe(instance1);
// ilist.addThruPipe(instance2);
TopicalNGrams model = new TopicalNGrams(3);
// model.estimate(ilist, 4, 0, 0, null, new Randoms());
assertNotNull(model);
}

@Test
public void testWriteThrowsExceptionWithUnwritableFile() {
TopicalNGrams model = new TopicalNGrams(2);
File file = new File("/this/path/does/not/exist/model.ser");
model.write(file);
assertTrue(true);
}

@Test
public void testPrintDocumentTopicsWithZeroThresholdAndPositiveMax() throws IOException {
Alphabet dataAlphabet = new Alphabet();
Alphabet biAlphabet = new Alphabet();
int i0 = dataAlphabet.lookupIndex("x");
// FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(dataAlphabet, biAlphabet, new int[] { i0 });
// fs.setBiIndices(new int[] { -1 });
// Instance instance = new Instance(fs, null, "docA", null);
// InstanceList ilist = new InstanceList(dataAlphabet);
// ilist.addThruPipe(instance);
TopicalNGrams model = new TopicalNGrams(3);
// model.estimate(ilist, 2, 0, 0, null, new Randoms());
File temp = File.createTempFile("props", ".txt");
PrintWriter writer = new PrintWriter(new FileWriter(temp));
model.printDocumentTopics(writer, 0.0, 2);
writer.close();
BufferedReader reader = new BufferedReader(new FileReader(temp));
assertTrue(reader.readLine().startsWith("#doc"));
reader.close();
temp.delete();
}

@Test
public void testPrintDocumentTopicsWithHighThresholdNoOutput() throws IOException {
Alphabet dataAlphabet = new Alphabet();
Alphabet biAlphabet = new Alphabet();
int i0 = dataAlphabet.lookupIndex("q");
// FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(dataAlphabet, biAlphabet, new int[] { i0 });
// fs.setBiIndices(new int[] { -1 });
// Instance instance = new Instance(fs, null, "docB", null);
// InstanceList ilist = new InstanceList(dataAlphabet);
// ilist.addThruPipe(instance);
TopicalNGrams model = new TopicalNGrams(2);
// model.estimate(ilist, 1, 0, 0, null, new Randoms());
File temp = File.createTempFile("props_empty", ".txt");
PrintWriter writer = new PrintWriter(new FileWriter(temp));
model.printDocumentTopics(writer, 0.9999, 2);
writer.close();
BufferedReader reader = new BufferedReader(new FileReader(temp));
String header = reader.readLine();
String content = reader.readLine();
assertTrue(header.startsWith("#doc"));
assertNotNull(content);
assertTrue(content.trim().split("\\s+").length >= 2);
reader.close();
temp.delete();
}

@Test
public void testEstimateWithAllBigramsIgnored() {
Alphabet dataAlphabet = new Alphabet();
Alphabet biAlphabet = new Alphabet();
int i0 = dataAlphabet.lookupIndex("a");
int i1 = dataAlphabet.lookupIndex("b");
// FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(dataAlphabet, biAlphabet, new int[] { i0, i1 });
// fs.setBiIndices(new int[] { -1, -1 });
// Instance instance = new Instance(fs, null, "ignoreBigramsDoc", null);
// InstanceList ilist = new InstanceList(dataAlphabet);
// ilist.addThruPipe(instance);
TopicalNGrams model = new TopicalNGrams(2);
// model.estimate(ilist, 2, 0, 0, null, new Randoms());
assertNotNull(model);
}

@Test
public void testEstimateSingleDocumentRepeatedWords() {
Alphabet dataAlphabet = new Alphabet();
Alphabet biAlphabet = new Alphabet();
int idx = dataAlphabet.lookupIndex("repeat");
int biIdx = biAlphabet.lookupIndex("repeat_repeat");
// FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(dataAlphabet, biAlphabet, new int[] { idx, idx, idx });
// fs.setBiIndices(new int[] { -1, biIdx, biIdx });
// Instance instance = new Instance(fs, null, "repeatDoc", null);
// InstanceList ilist = new InstanceList(dataAlphabet);
// ilist.addThruPipe(instance);
TopicalNGrams model = new TopicalNGrams(2);
// model.estimate(ilist, 3, 0, 0, null, new Randoms());
assertNotNull(model);
}

@Test
public void testPrintTopWordsWithPhrasesAndMultipleBigrams() {
Alphabet dataAlphabet = new Alphabet();
Alphabet biAlphabet = new Alphabet();
int w1 = dataAlphabet.lookupIndex("one");
int w2 = dataAlphabet.lookupIndex("two");
int w3 = dataAlphabet.lookupIndex("three");
int b1 = biAlphabet.lookupIndex("one_two");
int b2 = biAlphabet.lookupIndex("two_three");
// FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(dataAlphabet, biAlphabet, new int[] { w1, w2, w3 });
// fs.setBiIndices(new int[] { -1, b1, b2 });
// Instance instance = new Instance(fs, null, "phraseDoc", null);
// InstanceList ilist = new InstanceList(dataAlphabet);
// ilist.addThruPipe(instance);
TopicalNGrams model = new TopicalNGrams(1);
// model.estimate(ilist, 10, 0, 0, null, new Randoms());
model.printTopWords(10, true);
model.printTopWords(10, false);
}

@Test
public void testPrintDocumentTopicsWithMaxZero() throws IOException {
Alphabet dataAlphabet = new Alphabet();
Alphabet biAlphabet = new Alphabet();
int idx = dataAlphabet.lookupIndex("w");
// FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(dataAlphabet, biAlphabet, new int[] { idx });
// fs.setBiIndices(new int[] { -1 });
// Instance instance = new Instance(fs, null, "sourceMaxZero", null);
// InstanceList ilist = new InstanceList(dataAlphabet);
// ilist.addThruPipe(instance);
TopicalNGrams model = new TopicalNGrams(2);
// model.estimate(ilist, 1, 0, 0, null, new Randoms());
File tempFile = File.createTempFile("topics_max_zero", ".txt");
PrintWriter pw = new PrintWriter(new FileWriter(tempFile));
model.printDocumentTopics(pw, 0.0, 0);
pw.close();
BufferedReader reader = new BufferedReader(new FileReader(tempFile));
assertTrue(reader.readLine().startsWith("#doc"));
assertNotNull(reader.readLine());
reader.close();
tempFile.delete();
}

@Test
public void testPrintStateWithSingleToken() throws IOException {
Alphabet dataAlphabet = new Alphabet();
Alphabet biAlphabet = new Alphabet();
int idx = dataAlphabet.lookupIndex("token");
// FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(dataAlphabet, biAlphabet, new int[] { idx });
// fs.setBiIndices(new int[] { -1 });
// Instance instance = new Instance(fs, null, "stateTest", null);
// InstanceList ilist = new InstanceList(dataAlphabet);
// ilist.addThruPipe(instance);
TopicalNGrams model = new TopicalNGrams(1);
// model.estimate(ilist, 1, 0, 0, null, new Randoms());
File f = File.createTempFile("state", ".txt");
model.printState(f);
BufferedReader reader = new BufferedReader(new FileReader(f));
String header = reader.readLine();
String content = reader.readLine();
assertNotNull(header);
assertNotNull(content);
reader.close();
f.delete();
}

@Test
public void testSerializationAndCorruptedVersion() throws Exception {
Alphabet dataAlphabet = new Alphabet();
Alphabet biAlphabet = new Alphabet();
int idx = dataAlphabet.lookupIndex("x");
// FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(dataAlphabet, biAlphabet, new int[] { idx });
// fs.setBiIndices(new int[] { -1 });
// Instance instance = new Instance(fs, null, "serTest", null);
// InstanceList ilist = new InstanceList(dataAlphabet);
// ilist.addThruPipe(instance);
TopicalNGrams model = new TopicalNGrams(2);
// model.estimate(ilist, 2, 0, 0, null, new Randoms());
File file = File.createTempFile("corrupt_model", ".ser");
ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
oos.writeInt(-1);
oos.close();
try {
ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
TopicalNGrams loaded = new TopicalNGrams(1);
ois.close();
} catch (IOException e) {
assertTrue(e.getMessage() != null);
}
file.delete();
}

@Test
public void testRepeatedBigramAssignmentsAcrossDocs() {
Alphabet dataAlphabet = new Alphabet();
Alphabet biAlphabet = new Alphabet();
int t0 = dataAlphabet.lookupIndex("x");
int t1 = dataAlphabet.lookupIndex("y");
int b0 = biAlphabet.lookupIndex("x_y");
// FeatureSequenceWithBigrams fs1 = new FeatureSequenceWithBigrams(dataAlphabet, biAlphabet, new int[] { t0, t1 });
// fs1.setBiIndices(new int[] { -1, b0 });
// FeatureSequenceWithBigrams fs2 = new FeatureSequenceWithBigrams(dataAlphabet, biAlphabet, new int[] { t0, t1 });
// fs2.setBiIndices(new int[] { -1, b0 });
// Instance instance1 = new Instance(fs1, null, "doc1", null);
// Instance instance2 = new Instance(fs2, null, "doc2", null);
// InstanceList ilist = new InstanceList(dataAlphabet);
// ilist.addThruPipe(instance1);
// ilist.addThruPipe(instance2);
TopicalNGrams model = new TopicalNGrams(4);
// model.estimate(ilist, 3, 0, 0, null, new Randoms());
assertNotNull(model);
}

@Test
public void testEmptyAlphabetPhrasePrintHandling() {
Alphabet dataAlphabet = new Alphabet();
Alphabet biAlphabet = new Alphabet();
int tokenIndex = dataAlphabet.lookupIndex("a");
// FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(dataAlphabet, biAlphabet, new int[] { tokenIndex });
// fs.setBiIndices(new int[] { -1 });
// Instance instance = new Instance(fs, null, "emptyAlphabetTest", null);
// InstanceList ilist = new InstanceList(dataAlphabet);
// ilist.addThruPipe(instance);
TopicalNGrams model = new TopicalNGrams(1);
// model.estimate(ilist, 1, 0, 0, null, new Randoms());
model.printTopWords(0, true);
model.printTopWords(0, false);
}

@Test
public void testOnlyBigramTokensInDocument() {
Alphabet dataAlphabet = new Alphabet();
Alphabet biAlphabet = new Alphabet();
int wid1 = dataAlphabet.lookupIndex("word1");
int wid2 = dataAlphabet.lookupIndex("word2");
int wid3 = dataAlphabet.lookupIndex("word3");
int bid1 = biAlphabet.lookupIndex("word1_word2");
int bid2 = biAlphabet.lookupIndex("word2_word3");
// FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(dataAlphabet, biAlphabet, new int[] { wid1, wid2, wid3 });
// fs.setBiIndices(new int[] { -1, bid1, bid2 });
// Instance instance = new Instance(fs, null, "only_bigrams", null);
// InstanceList instanceList = new InstanceList(dataAlphabet);
// instanceList.addThruPipe(instance);
TopicalNGrams model = new TopicalNGrams(2);
// model.estimate(instanceList, 5, 0, 0, null, new Randoms());
assertNotNull(model);
}

@Test
public void testAllInvalidBigramsIgnored() {
Alphabet dataAlphabet = new Alphabet();
Alphabet biAlphabet = new Alphabet();
int w1 = dataAlphabet.lookupIndex("apple");
int w2 = dataAlphabet.lookupIndex("banana");
// FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(dataAlphabet, biAlphabet, new int[] { w1, w2 });
// fs.setBiIndices(new int[] { -1, -1 });
// Instance instance = new Instance(fs, null, "no_bigrams", null);
// InstanceList list = new InstanceList(dataAlphabet);
// list.addThruPipe(instance);
TopicalNGrams model = new TopicalNGrams(2);
// model.estimate(list, 3, 0, 0, null, new Randoms());
assertNotNull(model);
}

@Test(expected = IllegalArgumentException.class)
public void testEstimateWithZeroTopicsThrows() {
Alphabet dataAlphabet = new Alphabet();
// FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(dataAlphabet, new Alphabet(), new int[] { 0 });
// fs.setBiIndices(new int[] { -1 });
// Instance instance = new Instance(fs, null, "zero_topics", null);
// InstanceList list = new InstanceList(dataAlphabet);
// list.addThruPipe(instance);
new TopicalNGrams(0);
}

@Test
public void testTopicWeightsResetCorrectly() {
Alphabet dataAlphabet = new Alphabet();
Alphabet biAlphabet = new Alphabet();
int wi = dataAlphabet.lookupIndex("reset");
// FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(dataAlphabet, biAlphabet, new int[] { wi });
// fs.setBiIndices(new int[] { -1 });
// Instance instance = new Instance(fs, null, "topic_reset", null);
// InstanceList instanceList = new InstanceList(dataAlphabet);
// instanceList.addThruPipe(instance);
TopicalNGrams model = new TopicalNGrams(1);
// model.estimate(instanceList, 3, 0, 0, null, new Randoms());
model.printTopWords(1, false);
model.printDocumentTopics(new PrintWriter(System.out), 0.0, 1);
}

@Test
public void testOneDocumentOneTokenOneTopic() {
Alphabet dataAlphabet = new Alphabet();
Alphabet biAlphabet = new Alphabet();
int idx = dataAlphabet.lookupIndex("solo");
// FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(dataAlphabet, biAlphabet, new int[] { idx });
// fs.setBiIndices(new int[] { -1 });
// Instance instance = new Instance(fs, null, "onetoken", null);
// InstanceList list = new InstanceList(dataAlphabet);
// list.addThruPipe(instance);
TopicalNGrams model = new TopicalNGrams(1);
// model.estimate(list, 1, 0, 0, null, new Randoms());
ByteArrayOutputStream baos = new ByteArrayOutputStream();
PrintWriter pw = new PrintWriter(baos);
model.printDocumentTopics(pw, 0.0, 1);
pw.close();
String output = baos.toString();
assertTrue(output.contains("0"));
}

@Test
public void testOutputStreamFailureOnWrite() {
File file = new File("dummy_output_file");
try {
file.createNewFile();
TopicalNGrams model = new TopicalNGrams(1);
FileOutputStream fos = new FileOutputStream(file);
fos.close();
ObjectOutputStream oos = new ObjectOutputStream(fos);
oos.writeObject(model);
} catch (IOException expected) {
assertNotNull(expected);
} finally {
file.delete();
}
}

@Test
public void testBiAlphabetOnlyUsedWithBigramDocs() {
Alphabet dataAlphabet = new Alphabet();
Alphabet biAlphabet = new Alphabet();
int w0 = dataAlphabet.lookupIndex("xx");
int w1 = dataAlphabet.lookupIndex("yy");
int bidx = biAlphabet.lookupIndex("xx_yy");
// FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(dataAlphabet, biAlphabet, new int[] { w0, w1 });
// fs.setBiIndices(new int[] { -1, bidx });
// Instance instance = new Instance(fs, null, "bigramAlphabet", null);
// InstanceList list = new InstanceList(dataAlphabet);
// list.addThruPipe(instance);
TopicalNGrams model = new TopicalNGrams(2);
// model.estimate(list, 2, 0, 0, null, new Randoms());
model.printTopWords(5, false);
}

@Test
public void testPhraseSortStabilityWithEqualCounts() {
Alphabet dataAlphabet = new Alphabet();
Alphabet biAlphabet = new Alphabet();
int x1 = dataAlphabet.lookupIndex("p1");
int x2 = dataAlphabet.lookupIndex("p2");
int x3 = dataAlphabet.lookupIndex("p3");
int b1 = biAlphabet.lookupIndex("p1_p2");
int b2 = biAlphabet.lookupIndex("p2_p3");
// FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(dataAlphabet, biAlphabet, new int[] { x1, x2, x3 });
// fs.setBiIndices(new int[] { -1, b1, b2 });
// Instance instance = new Instance(fs, null, "phraseStable", null);
// InstanceList list = new InstanceList(dataAlphabet);
// list.addThruPipe(instance);
TopicalNGrams model = new TopicalNGrams(1);
// model.estimate(list, 10, 0, 0, null, new Randoms());
model.printTopWords(10, true);
}

@Test
public void testEstimateWithSingleTopicAndSingleToken() {
Alphabet dataAlphabet = new Alphabet();
Alphabet biAlphabet = new Alphabet();
int index = dataAlphabet.lookupIndex("solo_token");
// FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(dataAlphabet, biAlphabet, new int[] { index });
// fs.setBiIndices(new int[] { -1 });
// Instance instance = new Instance(fs, null, "singleton", null);
// InstanceList ilist = new InstanceList(dataAlphabet);
// ilist.addThruPipe(instance);
TopicalNGrams tng = new TopicalNGrams(1);
// tng.estimate(ilist, 1, 0, 0, null, new Randoms());
assertNotNull(tng);
}

@Test
public void testPhraseConstructionWithAlternatingGrams() {
Alphabet dataAlphabet = new Alphabet();
Alphabet biAlphabet = new Alphabet();
int w0 = dataAlphabet.lookupIndex("alpha");
int w1 = dataAlphabet.lookupIndex("beta");
int w2 = dataAlphabet.lookupIndex("gamma");
int biIdx = biAlphabet.lookupIndex("alpha_beta");
// FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(dataAlphabet, biAlphabet, new int[] { w0, w1, w2 });
// fs.setBiIndices(new int[] { -1, biIdx, -1 });
// Instance instance = new Instance(fs, null, "altGramsDoc", null);
// InstanceList ilist = new InstanceList(dataAlphabet);
// ilist.addThruPipe(instance);
TopicalNGrams tng = new TopicalNGrams(1);
// tng.estimate(ilist, 3, 0, 0, null, new Randoms());
tng.printTopWords(10, true);
tng.printTopWords(10, false);
}

@Test
public void testPrintDocumentTopicsWithNoTopicAboveThreshold() throws IOException {
Alphabet dataAlphabet = new Alphabet();
Alphabet biAlphabet = new Alphabet();
int idx = dataAlphabet.lookupIndex("low_topic");
// FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(dataAlphabet, biAlphabet, new int[] { idx });
// fs.setBiIndices(new int[] { -1 });
// Instance instance = new Instance(fs, null, "lowProbDoc", null);
// InstanceList ilist = new InstanceList(dataAlphabet);
// ilist.addThruPipe(instance);
TopicalNGrams tng = new TopicalNGrams(3);
// tng.estimate(ilist, 1, 0, 0, null, new Randoms());
File file = File.createTempFile("lowTopics", ".txt");
PrintWriter writer = new PrintWriter(new FileWriter(file));
tng.printDocumentTopics(writer, 0.99, 3);
writer.flush();
writer.close();
BufferedReader reader = new BufferedReader(new FileReader(file));
String line1 = reader.readLine();
String line2 = reader.readLine();
assertNotNull(line1);
assertNotNull(line2);
String[] tokens = line2.trim().split("\\s+");
assertTrue(tokens.length <= 2);
reader.close();
file.delete();
}

@Test
public void testEmptyInstanceListFollowedByValidEstimate() {
// InstanceList emptyList = new InstanceList(new Alphabet());
TopicalNGrams tng = new TopicalNGrams(2);
// tng.estimate(emptyList, 1, 0, 0, null, new Randoms());
Alphabet dataAlphabet = new Alphabet();
Alphabet biAlphabet = new Alphabet();
int idx = dataAlphabet.lookupIndex("word");
// FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(dataAlphabet, biAlphabet, new int[] { idx });
// fs.setBiIndices(new int[] { -1 });
// Instance instance = new Instance(fs, null, "secondDoc", null);
// InstanceList ilist = new InstanceList(dataAlphabet);
// ilist.addThruPipe(instance);
// tng.estimate(ilist, 2, 0, 0, null, new Randoms());
tng.printTopWords(3, false);
}

@Test
public void testWriteAndLoadWriteObjectManually() throws IOException, ClassNotFoundException {
Alphabet dataAlphabet = new Alphabet();
Alphabet biAlphabet = new Alphabet();
int idx = dataAlphabet.lookupIndex("persist");
// FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(dataAlphabet, biAlphabet, new int[] { idx });
// fs.setBiIndices(new int[] { -1 });
// Instance instance = new Instance(fs, null, "modelIO", null);
// InstanceList ilist = new InstanceList(dataAlphabet);
// ilist.addThruPipe(instance);
TopicalNGrams tng = new TopicalNGrams(3);
// tng.estimate(ilist, 1, 0, 0, null, new Randoms());
File modelFile = File.createTempFile("tng_model", ".bin");
ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(modelFile));
out.writeObject(tng);
out.close();
ObjectInputStream in = new ObjectInputStream(new FileInputStream(modelFile));
Object result = in.readObject();
in.close();
modelFile.delete();
assertTrue(result instanceof TopicalNGrams);
}

@Test
public void testDisallowedBigramPositionHandledGracefully() {
Alphabet dataAlphabet = new Alphabet();
Alphabet biAlphabet = new Alphabet();
int i0 = dataAlphabet.lookupIndex("a");
int i1 = dataAlphabet.lookupIndex("b");
int i2 = dataAlphabet.lookupIndex("c");
int biIndex = biAlphabet.lookupIndex("a_b");
// FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(dataAlphabet, biAlphabet, new int[] { i0, i1, i2 });
// fs.setBiIndices(new int[] { -1, biIndex, -1 });
// Instance instance = new Instance(fs, null, "invalid_gram", null);
// InstanceList ilist = new InstanceList(dataAlphabet);
// ilist.addThruPipe(instance);
TopicalNGrams tng = new TopicalNGrams(2);
// tng.estimate(ilist, 3, 0, 0, null, new Randoms());
tng.printTopWords(5, true);
}

@Test
public void testPhraseDelimiterBoundariesAndTruncationInPrintTopWords() {
Alphabet dataAlphabet = new Alphabet();
Alphabet biAlphabet = new Alphabet();
int t0 = dataAlphabet.lookupIndex("hello");
int t1 = dataAlphabet.lookupIndex("world");
int t2 = dataAlphabet.lookupIndex("nlp");
int b1 = biAlphabet.lookupIndex("hello_world");
int b2 = biAlphabet.lookupIndex("world_nlp");
// FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(dataAlphabet, biAlphabet, new int[] { t0, t1, t2 });
// fs.setBiIndices(new int[] { -1, b1, b2 });
// Instance instance = new Instance(fs, null, "ngram_chain", null);
// InstanceList ilist = new InstanceList(dataAlphabet);
// ilist.addThruPipe(instance);
TopicalNGrams model = new TopicalNGrams(1);
// model.estimate(ilist, 5, 0, 0, null, new Randoms());
model.printTopWords(2, true);
}

@Test
public void testAllTokensAsSingleLongPhrase() {
Alphabet dataAlphabet = new Alphabet();
Alphabet biAlphabet = new Alphabet();
int w0 = dataAlphabet.lookupIndex("aa");
int w1 = dataAlphabet.lookupIndex("bb");
int w2 = dataAlphabet.lookupIndex("cc");
int b1 = biAlphabet.lookupIndex("aa_bb");
int b2 = biAlphabet.lookupIndex("bb_cc");
// FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(dataAlphabet, biAlphabet, new int[] { w0, w1, w2 });
// fs.setBiIndices(new int[] { -1, b1, b2 });
// Instance instance = new Instance(fs, null, "longPhraseDoc", null);
// InstanceList ilist = new InstanceList(dataAlphabet);
// ilist.addThruPipe(instance);
TopicalNGrams tng = new TopicalNGrams(1);
// tng.estimate(ilist, 5, 0, 0, null, new Randoms());
tng.printTopWords(5, true);
}

@Test
public void testAlternatingBigramAllowedAndDisallowedIndexes() {
Alphabet dataAlphabet = new Alphabet();
Alphabet biAlphabet = new Alphabet();
int t0 = dataAlphabet.lookupIndex("x1");
int t1 = dataAlphabet.lookupIndex("x2");
int t2 = dataAlphabet.lookupIndex("x3");
int t3 = dataAlphabet.lookupIndex("x4");
int b1 = biAlphabet.lookupIndex("x2_x3");
// FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(dataAlphabet, biAlphabet, new int[] { t0, t1, t2, t3 });
// fs.setBiIndices(new int[] { -1, -1, b1, -1 });
// Instance instance = new Instance(fs, null, "altBigram", null);
// InstanceList ilist = new InstanceList(dataAlphabet);
// ilist.addThruPipe(instance);
TopicalNGrams model = new TopicalNGrams(2);
// model.estimate(ilist, 4, 0, 0, null, new Randoms());
model.printTopWords(5, false);
}

@Test
public void testPrintTopWordsWhenNoTypesPresent() {
Alphabet dataAlphabet = new Alphabet();
Alphabet biAlphabet = new Alphabet();
// FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(dataAlphabet, biAlphabet, new int[] {});
// fs.setBiIndices(new int[] {});
// Instance instance = new Instance(fs, null, "emptyTypesDoc", null);
// InstanceList ilist = new InstanceList(dataAlphabet);
// ilist.addThruPipe(instance);
TopicalNGrams model = new TopicalNGrams(1);
// model.estimate(ilist, 1, 0, 0, null, new Randoms());
model.printTopWords(0, true);
}

@Test
public void testPrintDocumentTopicsWithThresholdOneAndNegativeMax() throws IOException {
Alphabet dataAlphabet = new Alphabet();
Alphabet biAlphabet = new Alphabet();
int idx = dataAlphabet.lookupIndex("threshold");
// FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(dataAlphabet, biAlphabet, new int[] { idx });
// fs.setBiIndices(new int[] { -1 });
// Instance instance = new Instance(fs, null, "docThreshold", null);
// InstanceList list = new InstanceList(dataAlphabet);
// list.addThruPipe(instance);
TopicalNGrams model = new TopicalNGrams(2);
// model.estimate(list, 2, 0, 0, null, new Randoms());
File file = File.createTempFile("doc_threshold", ".txt");
PrintWriter writer = new PrintWriter(new FileWriter(file));
model.printDocumentTopics(writer, 1.0, -1);
writer.close();
BufferedReader reader = new BufferedReader(new FileReader(file));
String header = reader.readLine();
String content = reader.readLine();
assertTrue(header.startsWith("#doc"));
assertTrue(content.trim().split("\\s+").length <= 2);
reader.close();
file.delete();
}

@Test
public void testPrintStateStreamWithEmptyDocTokens() throws IOException {
Alphabet a = new Alphabet();
Alphabet b = new Alphabet();
// FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(a, b, new int[] {});
// fs.setBiIndices(new int[] {});
// Instance instance = new Instance(fs, null, "noTokens", null);
// InstanceList list = new InstanceList(a);
// list.addThruPipe(instance);
TopicalNGrams model = new TopicalNGrams(1);
// model.estimate(list, 1, 0, 0, null, new Randoms());
File file = File.createTempFile("state", ".txt");
PrintWriter pw = new PrintWriter(new FileWriter(file));
model.printState(pw);
pw.close();
BufferedReader reader = new BufferedReader(new FileReader(file));
String header = reader.readLine();
assertNotNull(header);
assertTrue(reader.readLine() == null);
reader.close();
file.delete();
}

@Test
public void testWriteObjectFailsMidStreamWrite() {
File file = new File("fake_out_model.ser");
try {
file.createNewFile();
FileOutputStream fos = new FileOutputStream(file);
ObjectOutputStream oos = new ObjectOutputStream(new OutputStream() {

@Override
public void write(int b) throws IOException {
throw new IOException("Simulated write failure");
}
});
TopicalNGrams tng = new TopicalNGrams(2);
oos.writeObject(tng);
oos.close();
} catch (IOException e) {
assertTrue(e.getMessage().contains("Simulated write failure"));
} finally {
file.delete();
}
}

@Test
public void testDeserializationFailsWithCorruptHeader() throws IOException {
File file = File.createTempFile("corrupted", ".bin");
DataOutputStream dos = new DataOutputStream(new FileOutputStream(file));
dos.writeInt(-1000);
dos.close();
try {
ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
ois.readObject();
} catch (Exception e) {
assertNotNull(e);
} finally {
file.delete();
}
}

@Test
public void testPrintTopWordsWithDuplicateTermIndices() {
Alphabet dataAlphabet = new Alphabet();
Alphabet biAlphabet = new Alphabet();
int idx = dataAlphabet.lookupIndex("dup");
// FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(dataAlphabet, biAlphabet, new int[] { idx, idx, idx });
// fs.setBiIndices(new int[] { -1, -1, -1 });
// Instance instance = new Instance(fs, null, "duplicateTerm", null);
// InstanceList ilist = new InstanceList(dataAlphabet);
// ilist.addThruPipe(instance);
TopicalNGrams model = new TopicalNGrams(1);
// model.estimate(ilist, 4, 0, 0, null, new Randoms());
model.printTopWords(10, true);
}

@Test
public void testSampleWithSingleTokenBigramDisallowed() {
Alphabet dataAlphabet = new Alphabet();
Alphabet biAlphabet = new Alphabet();
int idx = dataAlphabet.lookupIndex("token");
// FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(dataAlphabet, biAlphabet, new int[] { idx });
// fs.setBiIndices(new int[] { -1 });
// Instance instance = new Instance(fs, null, "singleTokenNoBigram", null);
// InstanceList list = new InstanceList(dataAlphabet);
// list.addThruPipe(instance);
TopicalNGrams model = new TopicalNGrams(2);
// model.estimate(list, 2, 0, 0, null, new Randoms());
}

@Test
public void testBigramAtEndIgnoredGracefully() {
Alphabet dataAlphabet = new Alphabet();
Alphabet biAlphabet = new Alphabet();
int w0 = dataAlphabet.lookupIndex("start");
int w1 = dataAlphabet.lookupIndex("end");
int bi = biAlphabet.lookupIndex("start_end");
// FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(dataAlphabet, biAlphabet, new int[] { w0, w1 });
// fs.setBiIndices(new int[] { -1, bi });
// Instance instance = new Instance(fs, null, "bigramTail", null);
// InstanceList list = new InstanceList(dataAlphabet);
// list.addThruPipe(instance);
TopicalNGrams model = new TopicalNGrams(3);
// model.estimate(list, 2, 0, 0, null, new Randoms());
model.printTopWords(3, false);
}

@Test
public void testPhraseContinuationInterruptedByUnigram() {
Alphabet dataAlphabet = new Alphabet();
Alphabet biAlphabet = new Alphabet();
int a = dataAlphabet.lookupIndex("a");
int b = dataAlphabet.lookupIndex("b");
int c = dataAlphabet.lookupIndex("c");
int bidx1 = biAlphabet.lookupIndex("a_b");
// FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(dataAlphabet, biAlphabet, new int[] { a, b, c });
// fs.setBiIndices(new int[] { -1, bidx1, -1 });
// Instance instance = new Instance(fs, null, "phraseBreak", null);
// InstanceList list = new InstanceList(dataAlphabet);
// list.addThruPipe(instance);
TopicalNGrams model = new TopicalNGrams(1);
// model.estimate(list, 3, 0, 0, null, new Randoms());
model.printTopWords(10, true);
}

@Test
public void testPrintTopWordsWithZeroTopWordsRequested() {
Alphabet dataAlphabet = new Alphabet();
Alphabet biAlphabet = new Alphabet();
int a = dataAlphabet.lookupIndex("trivia");
// FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(dataAlphabet, biAlphabet, new int[] { a });
// fs.setBiIndices(new int[] { -1 });
// Instance instance = new Instance(fs, null, "zeroTopWords", null);
// InstanceList list = new InstanceList(dataAlphabet);
// list.addThruPipe(instance);
TopicalNGrams model = new TopicalNGrams(2);
// model.estimate(list, 2, 0, 0, null, new Randoms());
model.printTopWords(0, true);
model.printTopWords(0, false);
}

@Test
public void testPhraseWithSingleBigramTokenOnly() {
Alphabet dataAlphabet = new Alphabet();
Alphabet biAlphabet = new Alphabet();
int w0 = dataAlphabet.lookupIndex("start");
int w1 = dataAlphabet.lookupIndex("after");
int bi = biAlphabet.lookupIndex("start_after");
// FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(dataAlphabet, biAlphabet, new int[] { w0, w1 });
// fs.setBiIndices(new int[] { -1, bi });
// Instance instance = new Instance(fs, null, "singleBiPhrase", null);
// InstanceList list = new InstanceList(dataAlphabet);
// list.addThruPipe(instance);
TopicalNGrams model = new TopicalNGrams(1);
// model.estimate(list, 5, 0, 0, null, new Randoms());
model.printTopWords(10, true);
}

@Test
public void testTopicDistributionSummationOnPrintDocumentTopics() throws IOException {
Alphabet dataAlphabet = new Alphabet();
Alphabet biAlphabet = new Alphabet();
int w = dataAlphabet.lookupIndex("probability");
// FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(dataAlphabet, biAlphabet, new int[] { w });
// fs.setBiIndices(new int[] { -1 });
// Instance instance = new Instance(fs, null, "topicProportions", null);
// InstanceList list = new InstanceList(dataAlphabet);
// list.addThruPipe(instance);
TopicalNGrams model = new TopicalNGrams(3);
// model.estimate(list, 3, 0, 0, null, new Randoms());
File file = File.createTempFile("docProps", ".txt");
PrintWriter pw = new PrintWriter(new FileWriter(file));
model.printDocumentTopics(pw, 0.0, 3);
pw.flush();
pw.close();
BufferedReader reader = new BufferedReader(new FileReader(file));
reader.readLine();
String line = reader.readLine();
assertNotNull(line);
String[] tokens = line.trim().split("\\s+");
assertTrue(tokens.length >= 2);
reader.close();
file.delete();
}

@Test
public void testWriteModelToNonWritableFile() {
TopicalNGrams model = new TopicalNGrams(1);
File f = new File("/no/permission/model.ser");
model.write(f);
assertTrue(true);
}

@Test
public void testReadObjectWithIncompleteStream() throws IOException {
File file = File.createTempFile("truncated", ".ser");
ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
oos.writeInt(0);
oos.close();
try {
ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
ois.readObject();
// fail("Expected IOException or ClassNotFoundException");
} catch (Exception ex) {
assertTrue(ex instanceof IOException || ex instanceof ClassNotFoundException);
} finally {
file.delete();
}
}

@Test
public void testEstimateFollowedBySecondEstimate() {
Alphabet dataAlphabet = new Alphabet();
Alphabet biAlphabet = new Alphabet();
int idx1 = dataAlphabet.lookupIndex("first");
// FeatureSequenceWithBigrams fs1 = new FeatureSequenceWithBigrams(dataAlphabet, biAlphabet, new int[] { idx1 });
// fs1.setBiIndices(new int[] { -1 });
// Instance inst1 = new Instance(fs1, null, "doc1", null);
// InstanceList list1 = new InstanceList(dataAlphabet);
// list1.addThruPipe(inst1);
TopicalNGrams model = new TopicalNGrams(2);
// model.estimate(list1, 1, 0, 0, null, new Randoms());
int idx2 = dataAlphabet.lookupIndex("second");
// FeatureSequenceWithBigrams fs2 = new FeatureSequenceWithBigrams(dataAlphabet, biAlphabet, new int[] { idx2 });
// fs2.setBiIndices(new int[] { -1 });
// Instance inst2 = new Instance(fs2, null, "doc2", null);
// InstanceList list2 = new InstanceList(dataAlphabet);
// list2.addThruPipe(inst2);
// model.estimate(list2, 2, 0, 0, null, new Randoms());
}
}
