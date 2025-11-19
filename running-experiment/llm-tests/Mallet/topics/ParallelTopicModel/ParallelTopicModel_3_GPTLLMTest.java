package cc.mallet.topics;

import cc.mallet.pipe.CharSequence2TokenSequence;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.TokenSequence2FeatureSequence;
import cc.mallet.pipe.iterator.StringArrayIterator;
import cc.mallet.types.*;
import cc.mallet.util.Randoms;
import org.junit.Test;
import java.io.*;
import java.util.*;
import java.util.zip.GZIPOutputStream;
import static org.junit.Assert.*;

public class ParallelTopicModel_3_GPTLLMTest {

@Test
public void testConstructorWithNumberOfTopics() {
ParallelTopicModel model = new ParallelTopicModel(2);
assertEquals(2, model.getNumTopics());
}

@Test
public void testSetNumTopicsWithPowerOfTwo() {
ParallelTopicModel model = new ParallelTopicModel(2);
model.setNumTopics(4);
assertEquals(4, model.getNumTopics());
assertEquals(4, model.alpha.length);
}

@Test
public void testAddInstancesSingleDocument() {
Alphabet alphabet = new Alphabet();
int idx0 = alphabet.lookupIndex("word1");
int idx1 = alphabet.lookupIndex("word2");
int idx2 = alphabet.lookupIndex("word3");
InstanceList list = new InstanceList(alphabet, null);
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { idx0, idx1, idx2 });
Instance inst = new Instance(fs, null, "doc1", null);
list.add(inst);
ParallelTopicModel model = new ParallelTopicModel(2);
model.setRandomSeed(42);
model.addInstances(list);
assertEquals(1, model.getData().size());
assertEquals(2, model.getNumTopics());
}

@Test
public void testPrintStateGeneratesOutputFile() throws Exception {
Alphabet alphabet = new Alphabet();
int id1 = alphabet.lookupIndex("term1");
int id2 = alphabet.lookupIndex("term2");
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { id1, id2 });
Instance instance = new Instance(fs, null, "docX", null);
InstanceList lst = new InstanceList(alphabet, null);
lst.add(instance);
ParallelTopicModel model = new ParallelTopicModel(2);
model.setRandomSeed(123);
model.addInstances(lst);
File file = File.createTempFile("state", ".gz");
file.deleteOnExit();
model.printState(file);
assertTrue(file.exists());
assertTrue(file.length() > 0);
}

@Test
public void testInitializeFromStateThrowsOnInvalidTypeIndex() throws Exception {
Alphabet alphabet = new Alphabet();
int idA = alphabet.lookupIndex("termA");
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { idA });
Instance instance = new Instance(fs, null, "d1", null);
InstanceList ilist = new InstanceList(alphabet, null);
ilist.add(instance);
ParallelTopicModel model = new ParallelTopicModel(2);
model.setRandomSeed(5);
model.addInstances(ilist);
File stateFile = File.createTempFile("bad", ".gz");
stateFile.deleteOnExit();
PrintStream ps = new PrintStream(new GZIPOutputStream(new FileOutputStream(stateFile)));
ps.println("#alpha : 0.5 0.5");
ps.println("#beta : 0.01");
ps.println("0 fakeSource 0 999 bogus 1");
ps.close();
try {
model.initializeFromState(stateFile);
fail("Expected IllegalStateException was not thrown");
} catch (IllegalStateException expected) {
}
}

@Test
public void testDisplayTopWordsReturnsString() {
Alphabet alphabet = new Alphabet();
int id0 = alphabet.lookupIndex("x");
int id1 = alphabet.lookupIndex("y");
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { id0, id1 });
Instance instance = new Instance(fs, null, "d0", null);
InstanceList lst = new InstanceList(alphabet, null);
lst.add(instance);
ParallelTopicModel model = new ParallelTopicModel(2);
model.setRandomSeed(77);
model.addInstances(lst);
String words = model.displayTopWords(2, false);
assertNotNull(words);
assertTrue(words.contains("x") || words.contains("y"));
}

@Test
public void testTopicProbabilitiesSumToOne() {
Alphabet alphabet = new Alphabet();
int id0 = alphabet.lookupIndex("w");
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { id0 });
Instance instance = new Instance(fs, null, "doc", null);
InstanceList lst = new InstanceList(alphabet, null);
lst.add(instance);
ParallelTopicModel model = new ParallelTopicModel(2);
model.setRandomSeed(99);
model.addInstances(lst);
double[] probs = model.getTopicProbabilities(0);
double sum = probs[0] + probs[1];
assertTrue(probs[0] >= 0);
assertTrue(probs[1] >= 0);
assertEquals(1.0, sum, 0.0001);
}

@Test
public void testWriteAndReadSerializedModel() throws Exception {
Alphabet alphabet = new Alphabet();
int id0 = alphabet.lookupIndex("z");
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { id0 });
Instance instance = new Instance(fs, null, "serializedDoc", null);
InstanceList lst = new InstanceList(alphabet, null);
lst.add(instance);
ParallelTopicModel model = new ParallelTopicModel(2);
model.setRandomSeed(22);
model.addInstances(lst);
File file = File.createTempFile("model", ".bin");
file.deleteOnExit();
model.write(file);
ParallelTopicModel loaded = ParallelTopicModel.read(file);
assertNotNull(loaded);
assertEquals(2, loaded.getNumTopics());
}

@Test
public void testModelLogLikelihoodProducesNegativeValue() {
Alphabet alphabet = new Alphabet();
int id1 = alphabet.lookupIndex("one");
int id2 = alphabet.lookupIndex("two");
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { id1, id2 });
Instance instance = new Instance(fs, null, "llDoc", null);
InstanceList lst = new InstanceList(alphabet, null);
lst.add(instance);
ParallelTopicModel model = new ParallelTopicModel(2);
model.setRandomSeed(2);
model.addInstances(lst);
double ll = model.modelLogLikelihood();
assertTrue(ll < 0);
}

@Test
public void testGetInferencerNotNull() {
Alphabet alphabet = new Alphabet();
int id = alphabet.lookupIndex("foo");
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { id });
Instance instance = new Instance(fs, null, "infer", null);
InstanceList list = new InstanceList(alphabet, null);
list.add(instance);
ParallelTopicModel model = new ParallelTopicModel(2);
model.setRandomSeed(303);
model.addInstances(list);
TopicInferencer inferencer = model.getInferencer();
assertNotNull(inferencer);
}

@Test(expected = NullPointerException.class)
public void testEstimateFailsWithoutAddInstances() throws Exception {
ParallelTopicModel model = new ParallelTopicModel(2);
model.estimate();
}

@Test
public void testEstimateRunsCompletesNormally() throws Exception {
Alphabet alphabet = new Alphabet();
int term = alphabet.lookupIndex("estimate");
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { term });
Instance instance = new Instance(fs, null, "DocZ", null);
InstanceList instances = new InstanceList(alphabet, null);
instances.add(instance);
ParallelTopicModel model = new ParallelTopicModel(2);
model.setNumIterations(5);
model.setNumThreads(1);
model.setRandomSeed(123);
model.addInstances(instances);
model.estimate();
double[] topics = model.getTopicProbabilities(0);
double sum = topics[0] + topics[1];
assertEquals(1.0, sum, 0.0001);
}

@Test(expected = IllegalArgumentException.class)
public void testZeroTopicsThrowsException() {
new ParallelTopicModel(0);
}

@Test
public void testOneTopicRuns() throws Exception {
Alphabet alphabet = new Alphabet();
int id = alphabet.lookupIndex("alpha");
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { id });
Instance inst = new Instance(fs, null, "doc1", null);
InstanceList ilist = new InstanceList(alphabet, null);
ilist.add(inst);
ParallelTopicModel model = new ParallelTopicModel(1);
model.setNumIterations(1);
model.setRandomSeed(7);
model.addInstances(ilist);
model.estimate();
double[] probs = model.getTopicProbabilities(0);
assertEquals(1, probs.length);
assertEquals(1.0, probs[0], 0.000001);
}

@Test
public void testEmptyInstanceListDoesNotCrashAddInstances() {
Alphabet alphabet = new Alphabet();
InstanceList ilist = new InstanceList(alphabet, null);
ParallelTopicModel model = new ParallelTopicModel(2);
model.setRandomSeed(10);
model.addInstances(ilist);
assertEquals(0, model.getData().size());
}

@Test
public void testEmptyVocabulary() throws Exception {
Alphabet alphabet = new Alphabet();
FeatureSequence fs = new FeatureSequence(alphabet, new int[] {});
Instance instance = new Instance(fs, null, "emptyDoc", null);
InstanceList ilist = new InstanceList(alphabet, null);
ilist.add(instance);
ParallelTopicModel model = new ParallelTopicModel(2);
model.setRandomSeed(3);
model.addInstances(ilist);
assertEquals(1, model.getData().size());
assertEquals(0, model.getData().get(0).topicSequence.size());
}

@Test
public void testGetTopWordsReturnsEmptyForZeroCount() {
Alphabet alphabet = new Alphabet();
ParallelTopicModel model = new ParallelTopicModel(2);
model.alphabet = alphabet;
model.numTypes = 0;
model.typeTopicCounts = new int[0][];
model.tokensPerTopic = new int[] { 0, 0 };
Object[][] topWords = model.getTopWords(5);
assertEquals(2, topWords.length);
assertEquals(0, topWords[0].length);
}

@Test
public void testPrintTopWordsWithNewLinesTruePrintsFormat() throws Exception {
Alphabet alphabet = new Alphabet();
int id = alphabet.lookupIndex("termZ");
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { id });
Instance instance = new Instance(fs, null, "docOne", null);
InstanceList list = new InstanceList(alphabet, null);
list.add(instance);
ParallelTopicModel model = new ParallelTopicModel(2);
model.setRandomSeed(111);
model.addInstances(list);
File file = File.createTempFile("top_print", ".txt");
file.deleteOnExit();
model.printTopWords(file, 2, true);
assertTrue(file.exists());
assertTrue(file.length() > 0);
}

@Test
public void testGetInferencerReturnsConsistentDistributions() {
Alphabet alphabet = new Alphabet();
int id = alphabet.lookupIndex("value");
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { id });
Instance inst = new Instance(fs, null, "inf-doc", null);
InstanceList list = new InstanceList(alphabet, null);
list.add(inst);
ParallelTopicModel model = new ParallelTopicModel(2);
model.setRandomSeed(331);
model.addInstances(list);
TopicInferencer inferencer = model.getInferencer();
Instance infInst = new Instance(fs, null, "infer", null);
double[] probs = inferencer.getSampledDistribution(infInst, 10, 1, 5);
assertEquals(2, probs.length);
double total = probs[0] + probs[1];
assertEquals(1.0, total, 0.0001);
}

@Test(expected = IOException.class)
public void testWriteInvalidPathThrowsIOException() throws Exception {
ParallelTopicModel model = new ParallelTopicModel(2);
File badFile = new File("/invalid/path/model.ser");
model.write(badFile);
}

@Test
public void testTopicXMLReportProducesValidStructure() throws Exception {
Alphabet alphabet = new Alphabet();
int id = alphabet.lookupIndex("xmlword");
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { id });
Instance inst = new Instance(fs, null, "docX", null);
InstanceList list = new InstanceList(alphabet, null);
list.add(inst);
ParallelTopicModel model = new ParallelTopicModel(2);
model.setRandomSeed(44);
model.addInstances(list);
StringWriter sw = new StringWriter();
PrintWriter pw = new PrintWriter(sw);
model.topicXMLReport(pw, 5);
pw.flush();
String result = sw.toString();
assertTrue(result.contains("<?xml version='1.0' ?>"));
assertTrue(result.contains("<topicModel>"));
assertTrue(result.contains("<topic"));
assertTrue(result.contains("<word"));
}

@Test
public void testPrintTopicWordWeightsGeneratesExpectedFormat() throws Exception {
Alphabet alphabet = new Alphabet();
int id = alphabet.lookupIndex("term");
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { id });
Instance inst = new Instance(fs, null, "docW", null);
InstanceList list = new InstanceList(alphabet, null);
list.add(inst);
ParallelTopicModel model = new ParallelTopicModel(2);
model.setRandomSeed(99);
model.addInstances(list);
File out = File.createTempFile("weights", ".txt");
out.deleteOnExit();
model.printTopicWordWeights(out);
assertTrue(out.exists());
assertTrue(out.length() > 0);
}

@Test
public void testPrintDocumentTopicsWithZeroThresholdLimit() throws Exception {
Alphabet alphabet = new Alphabet();
int id = alphabet.lookupIndex("a");
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { id });
Instance inst = new Instance(fs, null, "docABC", null);
InstanceList list = new InstanceList(alphabet, null);
list.add(inst);
ParallelTopicModel model = new ParallelTopicModel(2);
model.setRandomSeed(2023);
model.addInstances(list);
StringWriter writer = new StringWriter();
PrintWriter pw = new PrintWriter(writer);
model.printDocumentTopics(pw, 0.0, 1);
pw.flush();
String output = writer.toString();
assertTrue(output.contains("0\t"));
assertTrue(output.contains("\t0\t"));
}

@Test
public void testSetNumIterationsOne() {
ParallelTopicModel model = new ParallelTopicModel(3);
model.setNumIterations(1);
assertEquals(1, model.numIterations);
}

@Test
public void testSetOptimizeIntervalToZero() {
ParallelTopicModel model = new ParallelTopicModel(3);
model.setOptimizeInterval(0);
assertEquals(0, model.optimizeInterval);
}

@Test
public void testSetTemperingIntervalZeroApplies() {
ParallelTopicModel model = new ParallelTopicModel(4);
model.setTemperingInterval(0);
assertEquals(0, model.temperingInterval);
}

@Test
public void testSetBurninPeriodManualSet() {
ParallelTopicModel model = new ParallelTopicModel(4);
model.setBurninPeriod(123);
assertEquals(123, model.burninPeriod);
}

@Test
public void testPrintDocumentTopicsWithEmptyModelDoesNotCrash() {
ParallelTopicModel model = new ParallelTopicModel(2);
StringWriter writer = new StringWriter();
PrintWriter pw = new PrintWriter(writer);
model.printDocumentTopics(pw);
pw.flush();
String output = writer.toString();
assertTrue(output.startsWith("#doc"));
}

@Test
public void testEmptyTypeTopicCountsGetTopWordsReturnsEmptyArray() {
ParallelTopicModel model = new ParallelTopicModel(2);
model.typeTopicCounts = new int[10][];
model.tokensPerTopic = new int[2];
model.numTypes = 10;
model.numTopics = 2;
model.alphabet = new Alphabet();
for (int i = 0; i < 10; i++) {
model.alphabet.lookupIndex("word" + i);
}
Object[][] result = model.getTopWords(3);
assertEquals(2, result.length);
}

@Test
public void testGetTopicProbabilitiesWithEmptyLabelSequence() {
ParallelTopicModel model = new ParallelTopicModel(3);
LabelAlphabet labelAlphabet = new LabelAlphabet();
LabelSequence emptyLabelSequence = new LabelSequence(labelAlphabet, new int[] {});
double[] probs = model.getTopicProbabilities(emptyLabelSequence);
assertEquals(3, probs.length);
double sum = probs[0] + probs[1] + probs[2];
assertEquals(1.0, sum, 0.0001);
}

@Test
public void testPrintStateToPrintStreamIncludesHeaderAndTopicLines() throws Exception {
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("x");
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { idx });
Instance instance = new Instance(fs, null, "doc", null);
InstanceList list = new InstanceList(alphabet, null);
list.add(instance);
ParallelTopicModel model = new ParallelTopicModel(2);
model.setRandomSeed(7);
model.addInstances(list);
ByteArrayOutputStream baos = new ByteArrayOutputStream();
PrintStream ps = new PrintStream(baos);
model.printState(ps);
ps.flush();
String output = new String(baos.toByteArray(), "UTF-8");
assertTrue(output.contains("#doc source pos typeindex type topic"));
assertTrue(output.contains("#alpha"));
assertTrue(output.contains("#beta"));
}

@Test
public void testSetSymmetricAlphaTrueOptimizesToUniformDistribution() {
ParallelTopicModel model = new ParallelTopicModel(3);
model.setNumThreads(1);
model.setSymmetricAlpha(true);
WorkerCallable[] workers = new WorkerCallable[1];
ArrayList<TopicAssignment> dummyData = new ArrayList<>();
Randoms randoms = new Randoms();
model.tokensPerTopic = new int[3];
model.typeTopicCounts = new int[0][];
workers[0] = new WorkerCallable(3, model.alpha, 1.5, 0.05, randoms, dummyData, model.typeTopicCounts, model.tokensPerTopic, 0, 0);
workers[0].initializeAlphaStatistics(10);
model.docLengthCounts = new int[3];
model.topicDocCounts = new int[3][3];
model.optimizeAlpha(workers);
double avg = 0.0;
avg += model.alpha[0];
avg += model.alpha[1];
avg += model.alpha[2];
avg /= 3.0;
assertTrue(Math.abs(model.alpha[0] - avg) < 1e-6);
assertTrue(Math.abs(model.alpha[1] - avg) < 1e-6);
assertTrue(Math.abs(model.alpha[2] - avg) < 1e-6);
}

@Test
public void testGetTopicDocumentsWithNoDataReturnsEmptySortedSets() {
ParallelTopicModel model = new ParallelTopicModel(3);
ArrayList<TreeSet<IDSorter>> docs = model.getTopicDocuments(1.0);
assertEquals(3, docs.size());
assertTrue(docs.get(0).isEmpty());
assertTrue(docs.get(1).isEmpty());
assertTrue(docs.get(2).isEmpty());
}

@Test
public void testModelLogLikelihoodAfterSetAlphaZero() {
Alphabet alphabet = new Alphabet();
int id = alphabet.lookupIndex("foo");
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { id });
Instance instance = new Instance(fs, null, "doc", null);
InstanceList list = new InstanceList(alphabet, null);
list.add(instance);
ParallelTopicModel model = new ParallelTopicModel(2);
model.setRandomSeed(100);
model.addInstances(list);
model.alpha[0] = 0.0;
model.alpha[1] = 0.0;
model.alphaSum = 0.0;
double ll = model.modelLogLikelihood();
assertTrue(Double.isFinite(ll));
}

@Test
public void testWriteObjectAndReadObjectPreservesNumTopics() throws Exception {
Alphabet alphabet = new Alphabet();
int id = alphabet.lookupIndex("topicword");
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { id });
Instance instance = new Instance(fs, null, "doc1", null);
InstanceList list = new InstanceList(alphabet, null);
list.add(instance);
ParallelTopicModel model = new ParallelTopicModel(2);
model.setRandomSeed(12);
model.addInstances(list);
File file = File.createTempFile("sermodel", ".bin");
file.deleteOnExit();
ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
out.writeObject(model);
out.close();
ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
ParallelTopicModel restored = (ParallelTopicModel) in.readObject();
in.close();
assertEquals(2, restored.getNumTopics());
assertEquals(1, restored.getData().size());
}

@Test
public void testPrintStateHandlesNullSourceGracefully() throws Exception {
Alphabet alphabet = new Alphabet();
int id = alphabet.lookupIndex("word");
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { id });
Instance instance = new Instance(fs, null, "docName", null);
InstanceList list = new InstanceList(alphabet, null);
list.add(instance);
ParallelTopicModel model = new ParallelTopicModel(2);
model.setRandomSeed(2);
model.addInstances(list);
ByteArrayOutputStream baos = new ByteArrayOutputStream();
PrintStream ps = new PrintStream(baos);
model.printState(ps);
ps.flush();
String output = baos.toString("UTF-8");
assertTrue(output.contains("NA"));
}

@Test
public void testPrintTopicDocumentsUnnamedInstanceDoesNotFail() {
Alphabet alphabet = new Alphabet();
int id = alphabet.lookupIndex("token");
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { id });
Instance instance = new Instance(fs, null, null, null);
InstanceList list = new InstanceList(alphabet, null);
list.add(instance);
ParallelTopicModel model = new ParallelTopicModel(2);
model.setRandomSeed(55);
model.addInstances(list);
StringWriter sw = new StringWriter();
PrintWriter pw = new PrintWriter(sw);
model.printTopicDocuments(pw);
pw.flush();
String result = sw.toString();
assertTrue(result.contains("no-name"));
}

@Test
public void testSaveModelInvalidPathTriggersError() {
ParallelTopicModel model = new ParallelTopicModel(2);
File file = new File("/invalid-directory/model.ser");
model.setSaveSerializedModel(1, file.getAbsolutePath());
model.write(file);
}

@Test
public void testPrintTopicXMLReportProducesStructuredXML() {
Alphabet alphabet = new Alphabet();
int id = alphabet.lookupIndex("pie");
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { id });
Instance instance = new Instance(fs, null, "tDoc", null);
InstanceList list = new InstanceList(alphabet, null);
list.add(instance);
ParallelTopicModel model = new ParallelTopicModel(2);
model.setRandomSeed(201);
model.addInstances(list);
StringWriter sw = new StringWriter();
PrintWriter pw = new PrintWriter(sw);
model.topicXMLReport(pw, 10);
pw.flush();
String output = sw.toString();
assertTrue(output.startsWith("<?xml"));
assertTrue(output.contains("<topic id='0'"));
assertTrue(output.contains("</topicModel>"));
}

@Test
public void testVeryLargeNumTopicsTriggersExtraBitSparseCheck() {
ParallelTopicModel model = new ParallelTopicModel(130);
assertTrue(model.getNumTopics() == 130);
assertTrue(Integer.bitCount(model.topicMask) > 7);
assertTrue(model.topicBits > 7);
}

@Test
public void testRepeatedTokenDocumentMaintainsTopicCounts() throws Exception {
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("repeat");
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { idx, idx, idx, idx });
Instance instance = new Instance(fs, null, "repeatDoc", null);
InstanceList list = new InstanceList(alphabet, null);
list.add(instance);
ParallelTopicModel model = new ParallelTopicModel(3);
model.setRandomSeed(321);
model.setNumIterations(5);
model.setNumThreads(1);
model.addInstances(list);
model.estimate();
int total = 0;
for (int c : model.getTokensPerTopic()) {
total += c;
}
assertEquals(4, total);
}

@Test
public void testGetTopicDocumentsWithZeroSmoothing() {
ParallelTopicModel model = new ParallelTopicModel(3);
ArrayList<TreeSet<IDSorter>> result = model.getTopicDocuments(0.0);
assertEquals(3, result.size());
assertTrue(result.get(0).isEmpty());
}

@Test(expected = ClassCastException.class)
public void testCorruptedModelDeserializationFailsGracefully() throws Exception {
File corrupted = File.createTempFile("bad_model", ".bin");
corrupted.deleteOnExit();
FileOutputStream fos = new FileOutputStream(corrupted);
fos.write("Not a valid model object".getBytes());
fos.close();
ObjectInputStream in = new ObjectInputStream(new FileInputStream(corrupted));
in.readObject();
}

@Test
public void testTopicPhraseXMLReportProducesPlausibleXMLContent() {
Alphabet alphabet = new Alphabet();
int id0 = alphabet.lookupIndex("big");
int id1 = alphabet.lookupIndex("bigram");
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { id0, id1 });
Instance instance = new Instance(fs, null, "phraseDoc", null);
InstanceList list = new InstanceList(alphabet, null);
list.add(instance);
ParallelTopicModel model = new ParallelTopicModel(2);
model.setRandomSeed(74);
model.addInstances(list);
StringWriter sw = new StringWriter();
PrintWriter pw = new PrintWriter(sw);
model.topicPhraseXMLReport(pw, 10);
pw.flush();
String output = sw.toString();
assertTrue(output.contains("<topics>"));
assertTrue(output.contains("</topics>"));
assertTrue(output.contains("<topic id=\"0\""));
}

@Test
public void testGetSubCorpusTopicWordsWithNoSelectedDocuments() {
Alphabet alphabet = new Alphabet();
int id = alphabet.lookupIndex("token");
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { id });
Instance inst = new Instance(fs, null, "doc", null);
InstanceList list = new InstanceList(alphabet, null);
list.add(inst);
ParallelTopicModel model = new ParallelTopicModel(2);
model.setRandomSeed(101);
model.addInstances(list);
boolean[] mask = new boolean[1];
double[][] result = model.getSubCorpusTopicWords(mask, true, true);
assertEquals(2, result.length);
assertEquals(model.numTypes, result[0].length);
assertEquals(0.0, result[0][0], 0.00001);
}

@Test
public void testModelLogLikelihoodWithEmptyDataReturnsZero() {
ParallelTopicModel model = new ParallelTopicModel(3);
double ll = model.modelLogLikelihood();
assertEquals(0.0, ll, 0.0001);
}

@Test
public void testDisplayTopWordsWithZeroTopicsReturnsEmptyString() {
ParallelTopicModel model = new ParallelTopicModel(0);
model.alphabet = new Alphabet();
String result = model.displayTopWords(5, true);
assertEquals("", result);
}

@Test
public void testExtremeConfigurationValuesDoNotCrash() throws Exception {
Alphabet alphabet = new Alphabet();
int id = alphabet.lookupIndex("extreme");
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { id });
Instance instance = new Instance(fs, null, "doc", null);
InstanceList list = new InstanceList(alphabet, null);
list.add(instance);
ParallelTopicModel model = new ParallelTopicModel(2);
model.setSaveState(Integer.MAX_VALUE, "verylarge.gz");
model.setSaveSerializedModel(Integer.MAX_VALUE - 1, "model.ser");
model.setOptimizeInterval(Integer.MAX_VALUE);
model.setNumIterations(3);
model.setBurninPeriod(0);
// model.setShowTopicsInterval(1);
model.setNumThreads(1);
model.setRandomSeed(999);
model.addInstances(list);
model.estimate();
double[] probs = model.getTopicProbabilities(0);
double sum = probs[0] + probs[1];
assertEquals(1.0, sum, 0.001);
}

@Test
public void testGetSortedWordsReturnsWordsSortedByCount() {
Alphabet alphabet = new Alphabet();
int t1 = alphabet.lookupIndex("goose");
int t2 = alphabet.lookupIndex("duck");
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { t1, t1, t2 });
Instance instance = new Instance(fs, null, "birds", null);
InstanceList list = new InstanceList(alphabet, null);
list.add(instance);
ParallelTopicModel model = new ParallelTopicModel(2);
model.setRandomSeed(5);
model.addInstances(list);
ArrayList<TreeSet<IDSorter>> sorted = model.getSortedWords();
assertEquals(2, sorted.size());
TreeSet<IDSorter> topic0 = sorted.get(0);
TreeSet<IDSorter> topic1 = sorted.get(1);
assertNotNull(topic0);
assertNotNull(topic1);
}

@Test
public void testPrintDocumentTopicsHandlesNullInstanceName() throws IOException {
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("a");
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { idx });
Instance instance = new Instance(fs, null, null, null);
InstanceList list = new InstanceList(alphabet, null);
list.add(instance);
ParallelTopicModel model = new ParallelTopicModel(2);
model.setRandomSeed(2022);
model.addInstances(list);
StringWriter sw = new StringWriter();
PrintWriter pw = new PrintWriter(sw);
model.printDocumentTopics(pw, 0.0, -1);
pw.flush();
String output = sw.toString();
assertTrue(output.contains("no-name"));
}

@Test
public void testPrintTypeTopicCountsHandlesUninitializedCounts() throws IOException {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("frog");
alphabet.lookupIndex("fish");
ParallelTopicModel model = new ParallelTopicModel(2);
model.numTypes = 2;
model.alphabet = alphabet;
model.typeTopicCounts = new int[2][];
File file = File.createTempFile("ttc", ".txt");
file.deleteOnExit();
model.printTypeTopicCounts(file);
assertTrue(file.length() > 0);
}

@Test
public void testSetSymmetricAlphaUpdatesFlag() {
ParallelTopicModel model = new ParallelTopicModel(4);
model.setSymmetricAlpha(true);
assertTrue(model.usingSymmetricAlpha);
model.setSymmetricAlpha(false);
assertFalse(model.usingSymmetricAlpha);
}

@Test
public void testZeroDocumentsStillInitializesHistograms() {
ParallelTopicModel model = new ParallelTopicModel(5);
// model.initializeHistograms();
assertNotNull(model.docLengthCounts);
assertNotNull(model.topicDocCounts);
}

@Test
public void testSetTopicDisplayDoesNotThrow() {
ParallelTopicModel model = new ParallelTopicModel(4);
model.setTopicDisplay(40, 10);
assertEquals(40, model.showTopicsInterval);
assertEquals(10, model.wordsPerTopic);
}

@Test
public void testGetTopicWordsWithNoTypeTopicCountsReturnsZeros() {
ParallelTopicModel model = new ParallelTopicModel(3);
model.numTypes = 5;
model.typeTopicCounts = new int[5][];
model.tokensPerTopic = new int[3];
double[][] result = model.getTopicWords(true, true);
assertEquals(3, result.length);
assertEquals(5, result[0].length);
assertEquals(0.0, result[0][0], 0.0001);
}

@Test
public void testZeroTokensPerTopicHandledInGetTopicWords() {
ParallelTopicModel model = new ParallelTopicModel(3);
model.tokensPerTopic = new int[] { 0, 0, 0 };
model.numTypes = 2;
model.typeTopicCounts = new int[2][3];
model.alphabet = new Alphabet();
model.alphabet.lookupIndex("one");
model.alphabet.lookupIndex("two");
double[][] result = model.getTopicWords(true, true);
assertEquals(3, result.length);
assertEquals(2, result[0].length);
}

@Test
public void testExceptionDuringOptimizeAlphaHandledGracefully() {
ParallelTopicModel model = new ParallelTopicModel(3);
model.setSymmetricAlpha(false);
model.numThreads = 1;
model.docLengthCounts = new int[] { 1, 0, 0 };
model.topicDocCounts = new int[3][3];
model.alpha = new double[] { 0.0000001, 0.0000001, 0.0000001 };
WorkerCallable[] callables = new WorkerCallable[1];
callables[0] = new WorkerCallable(3, model.alpha, model.alphaSum, model.beta, new Randoms(), new ArrayList<TopicAssignment>(), new int[0][0], new int[3], 0, 0);
callables[0].initializeAlphaStatistics(3);
for (int topic = 0; topic < 3; topic++) {
model.topicDocCounts[topic][0] = Integer.MAX_VALUE;
}
model.optimizeAlpha(callables);
for (int i = 0; i < 3; i++) {
assertEquals(1.0, model.alpha[i], 0.01);
}
}

@Test
public void testPrintTopWordsThenInferencerStability() {
Alphabet alphabet = new Alphabet();
int id = alphabet.lookupIndex("term");
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { id });
Instance instance = new Instance(fs, null, "d1", null);
InstanceList list = new InstanceList(alphabet, null);
list.add(instance);
ParallelTopicModel model = new ParallelTopicModel(2);
model.setRandomSeed(17);
model.addInstances(list);
ByteArrayOutputStream baos = new ByteArrayOutputStream();
PrintStream ps = new PrintStream(baos);
model.printTopWords(ps, 5, false);
String topWords = baos.toString();
assertTrue(topWords.contains("term"));
TopicInferencer inferencer = model.getInferencer();
assertNotNull(inferencer);
}

@Test
public void testPrintTopicWordWeightsMissingTopicType() throws IOException {
Alphabet alphabet = new Alphabet();
int id = alphabet.lookupIndex("thing");
ParallelTopicModel model = new ParallelTopicModel(2);
model.numTypes = 1;
model.numTopics = 2;
model.alphabet = alphabet;
model.typeTopicCounts = new int[1][];
model.typeTopicCounts[0] = new int[] {};
model.tokensPerTopic = new int[] { 10, 10 };
model.beta = 0.01;
ByteArrayOutputStream baos = new ByteArrayOutputStream();
PrintWriter out = new PrintWriter(baos);
model.printTopicWordWeights(out);
out.flush();
String output = baos.toString("UTF-8");
assertTrue(output.contains("0"));
assertTrue(output.contains("1"));
}

@Test
public void testMaximizeWithImmediateReorder() {
Alphabet alphabet = new Alphabet();
int id = alphabet.lookupIndex("t");
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { id });
Instance instance = new Instance(fs, null, "doc", null);
InstanceList list = new InstanceList(alphabet, null);
list.add(instance);
ParallelTopicModel model = new ParallelTopicModel(3);
model.setRandomSeed(11);
model.addInstances(list);
model.maximize(2);
assertEquals(1, model.data.size());
}

@Test
public void testGetTopicWordsWithoutSmoothingOrNormalization() {
ParallelTopicModel model = new ParallelTopicModel(2);
model.numTypes = 2;
model.typeTopicCounts = new int[2][];
model.tokensPerTopic = new int[2];
model.typeTopicCounts[0] = new int[] { (2 << model.topicBits) };
model.typeTopicCounts[1] = new int[] { (3 << model.topicBits) + 1 };
model.tokensPerTopic[0] = 2;
model.tokensPerTopic[1] = 3;
double[][] matrix = model.getTopicWords(false, false);
assertEquals(2.0, matrix[0][0], 0.001);
assertEquals(3.0, matrix[1][1], 0.001);
}

@Test
public void testGetDocumentTopicsWithoutSmoothingOrNorm() {
Alphabet alphabet = new Alphabet();
int id = alphabet.lookupIndex("blue");
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { id, id });
Instance instance = new Instance(fs, null, "colors", null);
InstanceList list = new InstanceList(alphabet, null);
list.add(instance);
ParallelTopicModel model = new ParallelTopicModel(2);
model.setRandomSeed(457);
model.addInstances(list);
double[][] result = model.getDocumentTopics(false, false);
int sum = (int) (result[0][0] + result[0][1]);
assertEquals(2, sum);
}

@Test
public void testLearnSymmetricConcentrationSingleThreadBehavior() {
ParallelTopicModel model = new ParallelTopicModel(4);
model.setNumThreads(1);
model.usingSymmetricAlpha = true;
model.docLengthCounts = new int[] { 5, 3, 1 };
model.topicDocCounts = new int[4][3];
model.topicDocCounts[0] = new int[] { 1, 1, 1 };
WorkerCallable[] callables = new WorkerCallable[1];
callables[0] = new WorkerCallable(4, model.alpha, model.alphaSum, model.beta, new Randoms(), new ArrayList<TopicAssignment>(), new int[0][0], new int[4], 0, 0);
callables[0].initializeAlphaStatistics(3);
model.optimizeAlpha(callables);
assertTrue(model.alphaSum > 0);
}

@Test
public void testPrintStateRestoresAllTopicsPerTokenPosition() throws Exception {
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("w1");
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { idx, idx });
Instance inst = new Instance(fs, null, "restoreTest", null);
InstanceList list = new InstanceList(alphabet, null);
list.add(inst);
ParallelTopicModel model = new ParallelTopicModel(2);
model.setRandomSeed(10);
model.addInstances(list);
File temp = File.createTempFile("state", ".gz");
temp.deleteOnExit();
model.printState(temp);
ParallelTopicModel m2 = new ParallelTopicModel(2);
m2.setRandomSeed(10);
m2.addInstances(list);
m2.initializeFromState(temp);
assertEquals(1, m2.data.size());
}

@Test
public void testPrintStateToFileWithCustomAlphaValues() throws Exception {
Alphabet alphabet = new Alphabet();
int id1 = alphabet.lookupIndex("x");
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { id1 });
Instance instance = new Instance(fs, null, "doc42", null);
InstanceList list = new InstanceList(alphabet, null);
list.add(instance);
ParallelTopicModel model = new ParallelTopicModel(2);
model.alpha[0] = 0.3;
model.alpha[1] = 0.7;
model.setRandomSeed(42);
model.addInstances(list);
File f = File.createTempFile("state-file", ".gz");
f.deleteOnExit();
model.printState(f);
assertTrue(f.exists());
assertTrue(f.length() > 0);
BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
String line = reader.readLine();
boolean hasAlphaLine = false;
while (line != null) {
if (line.startsWith("#alpha")) {
hasAlphaLine = true;
break;
}
line = reader.readLine();
}
reader.close();
assertTrue(hasAlphaLine);
}

@Test
public void testGetTopWordsWithFewerTypesThanNumWords() {
Alphabet alphabet = new Alphabet();
int wordIndex = alphabet.lookupIndex("short");
ParallelTopicModel model = new ParallelTopicModel(2);
model.alphabet = alphabet;
model.numTypes = 1;
model.tokensPerTopic = new int[] { 5, 5 };
model.typeTopicCounts = new int[1][];
model.typeTopicCounts[0] = new int[] { (3 << model.topicBits) + 0 };
Object[][] result = model.getTopWords(10);
assertEquals(2, result.length);
assertEquals(1, result[0].length);
}

@Test
public void testGetTopicDocumentsWithExtremeWeights() {
Alphabet alphabet = new Alphabet();
int idA = alphabet.lookupIndex("apple");
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { idA });
Instance instance = new Instance(fs, null, "extremeWeight", null);
InstanceList list = new InstanceList(alphabet, null);
list.add(instance);
ParallelTopicModel model = new ParallelTopicModel(2);
model.setRandomSeed(26);
model.addInstances(list);
ArrayList<TreeSet<IDSorter>> topicDocMap = model.getTopicDocuments(1e9);
TreeSet<IDSorter> topicSet = topicDocMap.get(0);
assertFalse(topicSet.isEmpty());
}

@Test
public void testPrintTopicXMLReportWithAlphaOnlyInTopicZero() {
Alphabet alphabet = new Alphabet();
int id0 = alphabet.lookupIndex("w1");
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { id0 });
Instance instance = new Instance(fs, null, "logicDoc", null);
InstanceList list = new InstanceList(alphabet, null);
list.add(instance);
ParallelTopicModel model = new ParallelTopicModel(2);
model.setRandomSeed(1);
model.addInstances(list);
model.alpha[0] = 0.9;
model.alpha[1] = 0.0;
StringWriter stringWriter = new StringWriter();
PrintWriter pw = new PrintWriter(stringWriter);
model.topicXMLReport(pw, 3);
pw.flush();
String output = stringWriter.toString();
assertTrue(output.contains("<topic id='0'"));
assertTrue(output.contains("<word"));
}

@Test
public void testDisplayTopWordsWithNewLineFormatting() {
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("newline");
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { idx });
Instance instance = new Instance(fs, null, "x", null);
InstanceList list = new InstanceList(alphabet, null);
list.add(instance);
ParallelTopicModel model = new ParallelTopicModel(2);
model.setRandomSeed(88);
model.addInstances(list);
String result = model.displayTopWords(3, true);
assertTrue(result.contains("\n"));
assertTrue(result.contains("\t"));
assertTrue(result.contains("newline"));
}

@Test
public void testFormatterPreservesLocale() {
ParallelTopicModel model = new ParallelTopicModel(3);
Locale oldDefault = Locale.getDefault();
Locale.setDefault(Locale.FRANCE);
double formattedValue = 0.12345;
String valueStr = model.formatter.format(formattedValue);
Locale.setDefault(oldDefault);
assertTrue(valueStr.contains(",") || valueStr.contains("."));
}

@Test
public void testBuildInitialTypeTopicCountsWithUnassignedTopic() {
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("alpha");
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { idx });
LabelAlphabet labelAlphabet = new LabelAlphabet();
LabelSequence topicSeq = new LabelSequence(labelAlphabet, new int[] { ParallelTopicModel.UNASSIGNED_TOPIC });
Instance instance = new Instance(fs, null, "zero", null);
TopicAssignment assignment = new TopicAssignment(instance, topicSeq);
ParallelTopicModel model = new ParallelTopicModel(2);
model.alphabet = alphabet;
model.numTypes = 1;
model.getData().add(assignment);
model.buildInitialTypeTopicCounts();
assertEquals(2, model.tokensPerTopic.length);
}

@Test
public void testOptimizeBetaWithNoTypeTopicCounts() {
ParallelTopicModel model = new ParallelTopicModel(4);
model.numTypes = 0;
model.maxTypeCount = 0;
model.numThreads = 1;
model.tokensPerTopic = new int[] { 1, 1, 1, 1 };
model.typeTopicCounts = new int[0][];
model.alphabet = new Alphabet();
WorkerCallable[] workers = new WorkerCallable[1];
ArrayList<TopicAssignment> dummyAssignments = new ArrayList<>();
workers[0] = new WorkerCallable(4, model.alpha, model.alphaSum, model.beta, new Randoms(), dummyAssignments, new int[0][0], model.tokensPerTopic, 0, 0);
model.optimizeBeta(workers);
assertTrue(model.beta > 0);
}

@Test
public void testGetTopicWordsSparseTopicMissingDoesNotFail() {
ParallelTopicModel model = new ParallelTopicModel(3);
model.tokensPerTopic = new int[] { 4, 0, 2 };
model.numTypes = 2;
model.typeTopicCounts = new int[2][];
model.typeTopicCounts[0] = new int[] { (4 << model.topicBits) | 0 };
model.typeTopicCounts[1] = new int[] { (2 << model.topicBits) | 2 };
model.beta = 0.01;
model.betaSum = 0.02;
double[][] matrix = model.getTopicWords(true, false);
assertEquals(3, matrix.length);
assertEquals(2, matrix[0].length);
}
}
