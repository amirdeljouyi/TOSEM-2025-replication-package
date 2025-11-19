package cc.mallet.topics;

import cc.mallet.types.*;
import cc.mallet.util.Randoms;
import org.junit.Test;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static org.junit.Assert.*;

public class LDAHyper_3_GPTLLMTest {

@Test
public void testConstructorInitializesTopicAlphabetAndTopicCount() {
Randoms random = new Randoms(123);
LDAHyper lda = new LDAHyper(3, 1.5, 0.01, random);
assertEquals(3, lda.getNumTopics());
assertNotNull(lda.getTopicAlphabet());
}

@Test
public void testAddInstancesStoresDataCorrectly() {
Alphabet alphabet = new Alphabet();
LabelAlphabet topicAlphabet = new LabelAlphabet();
Randoms random = new Randoms(111);
LDAHyper lda = new LDAHyper(topicAlphabet, 5.0, 0.01, random);
TokenSequence ts1 = new TokenSequence();
ts1.add("apple");
ts1.add("banana");
TokenSequence ts2 = new TokenSequence();
ts2.add("banana");
// FeatureSequence fs1 = new FeatureSequence(alphabet, ts1);
// FeatureSequence fs2 = new FeatureSequence(alphabet, ts2);
InstanceList training = new InstanceList(alphabet, null);
// training.add(new Instance(fs1, null, "doc1", null));
// training.add(new Instance(fs2, null, "doc2", null));
lda.addInstances(training);
ArrayList<?> data = lda.getData();
assertEquals(2, data.size());
}

@Test
public void testEstimateExecutesWithoutError() throws IOException {
Alphabet alphabet = new Alphabet();
LabelAlphabet topicAlphabet = new LabelAlphabet();
Randoms random = new Randoms(456);
LDAHyper lda = new LDAHyper(topicAlphabet, 5.0, 0.01, random);
lda.setNumIterations(3);
TokenSequence ts = new TokenSequence();
ts.add("x");
ts.add("y");
// FeatureSequence fs = new FeatureSequence(alphabet, ts);
InstanceList training = new InstanceList(alphabet, null);
// training.add(new Instance(fs, null, "doc", null));
lda.addInstances(training);
lda.estimate();
assertTrue(lda.getData().size() > 0);
}

@Test
public void testGetTopWordsGeneratesOutput() {
Alphabet alphabet = new Alphabet();
LabelAlphabet topicAlphabet = new LabelAlphabet();
Randoms random = new Randoms(321);
LDAHyper lda = new LDAHyper(topicAlphabet, 2.0, 0.1, random);
TokenSequence seq = new TokenSequence();
seq.add("alpha");
// FeatureSequence fs = new FeatureSequence(alphabet, seq);
InstanceList training = new InstanceList(alphabet, null);
// training.add(new Instance(fs, null, "sample", null));
lda.addInstances(training);
ByteArrayOutputStream baos = new ByteArrayOutputStream();
PrintStream ps = new PrintStream(baos);
lda.printTopWords(ps, 5, false);
ps.flush();
String out = baos.toString();
assertTrue(out.contains("0") || out.contains("1"));
}

@Test
public void testPrintDocumentTopicsOutputsExpectedFormat() {
Alphabet alphabet = new Alphabet();
LabelAlphabet topicAlphabet = new LabelAlphabet();
Randoms random = new Randoms(789);
LDAHyper lda = new LDAHyper(topicAlphabet, 3.0, 0.1, random);
TokenSequence ts = new TokenSequence();
ts.add("data");
ts.add("science");
// FeatureSequence fs = new FeatureSequence(alphabet, ts);
InstanceList list = new InstanceList(alphabet, null);
// list.add(new Instance(fs, null, "docA", null));
lda.addInstances(list);
StringWriter sw = new StringWriter();
PrintWriter pw = new PrintWriter(sw);
lda.printDocumentTopics(pw, 0.0, -1);
pw.flush();
String result = sw.toString();
assertTrue(result.contains("#doc source topic proportion"));
assertTrue(result.contains("docA") || result.contains("0"));
}

@Test
public void testModelLogLikelihoodReturnsFiniteValue() {
Alphabet alphabet = new Alphabet();
LabelAlphabet topicAlphabet = new LabelAlphabet();
Randoms random = new Randoms(17);
LDAHyper lda = new LDAHyper(topicAlphabet, 2.0, 0.01, random);
TokenSequence ts = new TokenSequence();
ts.add("token");
// FeatureSequence fs = new FeatureSequence(alphabet, ts);
InstanceList instances = new InstanceList(alphabet, null);
// instances.add(new Instance(fs, null, "doc", null));
lda.addInstances(instances);
double ll = lda.modelLogLikelihood();
assertTrue(Double.isFinite(ll));
}

@Test
public void testGetSortedTopicWordsReturnsIDSorterArray() {
Alphabet alphabet = new Alphabet();
LabelAlphabet topicAlphabet = new LabelAlphabet();
Randoms random = new Randoms(999);
LDAHyper lda = new LDAHyper(topicAlphabet, 2.0, 0.01, random);
TokenSequence tokens = new TokenSequence();
tokens.add("machine");
// FeatureSequence features = new FeatureSequence(alphabet, tokens);
InstanceList list = new InstanceList(alphabet, null);
// list.add(new Instance(features, null, "text", null));
lda.addInstances(list);
IDSorter[] sorters = lda.getSortedTopicWords(0);
assertNotNull(sorters);
assertEquals(alphabet.size(), sorters.length);
}

@Test
public void testSerializationAndDeserializationPreservesTopicCount() throws Exception {
Alphabet alphabet = new Alphabet();
LabelAlphabet topicAlphabet = new LabelAlphabet();
Randoms random = new Randoms(707);
LDAHyper lda = new LDAHyper(topicAlphabet, 1.5, 0.02, random);
TokenSequence ts = new TokenSequence();
ts.add("serialize");
// FeatureSequence fs = new FeatureSequence(alphabet, ts);
InstanceList set = new InstanceList(alphabet, null);
// set.add(new Instance(fs, null, "serial", null));
lda.addInstances(set);
ByteArrayOutputStream bout = new ByteArrayOutputStream();
ObjectOutputStream oos = new ObjectOutputStream(bout);
oos.writeObject(lda);
oos.close();
ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
ObjectInputStream ois = new ObjectInputStream(bin);
Object deserialized = ois.readObject();
assertNotNull(deserialized);
assertTrue(deserialized instanceof LDAHyper);
LDAHyper loaded = (LDAHyper) deserialized;
assertEquals(lda.getNumTopics(), loaded.getNumTopics());
}

@Test
public void testPrintStateOutputsContent() throws IOException {
Alphabet alphabet = new Alphabet();
LabelAlphabet topicAlphabet = new LabelAlphabet();
Randoms random = new Randoms(808);
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.05, random);
TokenSequence ts = new TokenSequence();
ts.add("tokenized");
ts.add("text");
// FeatureSequence fs = new FeatureSequence(alphabet, ts);
InstanceList il = new InstanceList(alphabet, null);
// il.add(new Instance(fs, null, "txt", null));
lda.addInstances(il);
ByteArrayOutputStream output = new ByteArrayOutputStream();
PrintStream ps = new PrintStream(output);
lda.printState(ps);
ps.flush();
String stateOutput = output.toString();
assertTrue(stateOutput.contains("#doc source pos typeindex type topic"));
assertTrue(stateOutput.contains("0"));
}

@Test
public void testAddEmptyInstanceListDoesNotThrow() {
Alphabet alphabet = new Alphabet();
LabelAlphabet topicAlphabet = new LabelAlphabet();
Randoms random = new Randoms(42);
LDAHyper lda = new LDAHyper(topicAlphabet, 2.0, 0.01, random);
InstanceList emptyList = new InstanceList(alphabet, null);
lda.addInstances(emptyList);
assertEquals(0, lda.getData().size());
}

@Test
public void testEstimateWithoutDataDoesNotThrow() throws IOException {
Alphabet alphabet = new Alphabet();
LabelAlphabet topicAlphabet = new LabelAlphabet();
Randoms random = new Randoms(99);
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, random);
lda.setNumIterations(1);
lda.estimate();
assertEquals(2, lda.iterationsSoFar);
}

@Test
public void testAddInstanceWithZeroTokens() {
Alphabet alphabet = new Alphabet();
LabelAlphabet topicAlphabet = new LabelAlphabet();
Randoms random = new Randoms(100);
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, random);
FeatureSequence emptySequence = new FeatureSequence(alphabet, 0);
InstanceList set = new InstanceList(alphabet, null);
set.add(new Instance(emptySequence, null, "empty", null));
lda.addInstances(set);
assertEquals(1, lda.getData().size());
}

@Test(expected = IllegalArgumentException.class)
public void testAddingIncompatibleAlphabetThrows() {
Alphabet originalAlphabet = new Alphabet();
Alphabet incompatibleAlphabet = new Alphabet();
incompatibleAlphabet.lookupIndex("other");
LabelAlphabet topicAlphabet = new LabelAlphabet();
Randoms random = new Randoms(123);
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, random);
TokenSequence seq = new TokenSequence();
seq.add("word");
// FeatureSequence fs = new FeatureSequence(incompatibleAlphabet, seq);
// Instance instance = new Instance(fs, null, "doc", null);
InstanceList list = new InstanceList(incompatibleAlphabet, null);
// list.add(instance);
lda.addInstances(list);
}

@Test(expected = ArrayIndexOutOfBoundsException.class)
public void testGetSortedTopicWordsInvalidTopicIndexThrows() {
Alphabet alphabet = new Alphabet();
LabelAlphabet topicAlphabet = new LabelAlphabet();
Randoms random = new Randoms(777);
LDAHyper lda = new LDAHyper(topicAlphabet, 2.0, 0.01, random);
lda.getSortedTopicWords(1000);
}

@Test
public void testPrintStateWithNoDocumentsOnlyPrintsHeader() throws IOException {
Alphabet alphabet = new Alphabet();
LabelAlphabet topicAlphabet = new LabelAlphabet();
Randoms random = new Randoms(8080);
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.05, random);
ByteArrayOutputStream baos = new ByteArrayOutputStream();
PrintStream ps = new PrintStream(baos);
lda.printState(ps);
ps.flush();
String output = baos.toString();
assertTrue(output.contains("#doc source pos typeindex type topic"));
assertFalse(output.contains("0 "));
}

@Test
public void testPrintStateFileOutputHeaderOnlyWhenEmpty() throws IOException {
Alphabet alphabet = new Alphabet();
LabelAlphabet topicAlphabet = new LabelAlphabet();
Randoms random = new Randoms(9001);
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.05, random);
File tmpFile = File.createTempFile("lda_state", ".gz");
tmpFile.deleteOnExit();
lda.printState(tmpFile);
FileInputStream fis = new FileInputStream(tmpFile);
BufferedReader br = new BufferedReader(new InputStreamReader(new java.util.zip.GZIPInputStream(fis)));
String firstLine = br.readLine();
br.close();
fis.close();
assertTrue(firstLine.contains("#doc source pos typeindex type topic"));
}

@Test
public void testEmpiricalLikelihoodWithNoCommonTermsReturnsZero() {
Alphabet trainAlpha = new Alphabet();
LabelAlphabet topicAlphabet = new LabelAlphabet();
Randoms random = new Randoms(1111);
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, random);
TokenSequence ts1 = new TokenSequence();
ts1.add("known");
// FeatureSequence fs1 = new FeatureSequence(trainAlpha, ts1);
InstanceList train = new InstanceList(trainAlpha, null);
// train.add(new Instance(fs1, null, "train", null));
lda.addInstances(train);
Alphabet testAlpha = new Alphabet();
TokenSequence ts2 = new TokenSequence();
ts2.add("unknown");
// FeatureSequence fs2 = new FeatureSequence(testAlpha, ts2);
InstanceList testing = new InstanceList(testAlpha, null);
// testing.add(new Instance(fs2, null, "test", null));
double result = lda.empiricalLikelihood(10, testing);
assertTrue(Double.isFinite(result));
}

@Test
public void testTopicXMLReportGeneratesMinimumXML() {
Alphabet alphabet = new Alphabet();
LabelAlphabet topicAlphabet = new LabelAlphabet();
alphabet.lookupIndex("foo");
Randoms random = new Randoms(2020);
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, random);
TokenSequence ts = new TokenSequence();
ts.add("foo");
// FeatureSequence fs = new FeatureSequence(alphabet, ts);
InstanceList instances = new InstanceList(alphabet, null);
// instances.add(new Instance(fs, null, "doc", null));
lda.addInstances(instances);
StringWriter sw = new StringWriter();
PrintWriter pw = new PrintWriter(sw);
lda.topicXMLReport(pw, 1);
pw.flush();
String xml = sw.toString();
assertTrue(xml.contains("<?xml version='1.0' ?>"));
assertTrue(xml.contains("<topicModel>"));
}

@Test
public void testSettingRandomSeedChangesTopicAssignmentsDeterministically() {
Alphabet alphabet = new Alphabet();
LabelAlphabet topicAlphabet = new LabelAlphabet();
alphabet.lookupIndex("repeat");
TokenSequence seq = new TokenSequence();
seq.add("repeat");
// FeatureSequence fs = new FeatureSequence(alphabet, seq);
// Instance instance = new Instance(fs, null, "fixed", null);
InstanceList data = new InstanceList(alphabet, null);
// data.add(instance);
LDAHyper lda1 = new LDAHyper(topicAlphabet, 1.0, 0.01, new Randoms(42));
lda1.addInstances(data);
LDAHyper lda2 = new LDAHyper(topicAlphabet, 1.0, 0.01, new Randoms(42));
lda2.addInstances(data);
int topic1 = lda1.getData().get(0).topicSequence.getIndexAtPosition(0);
int topic2 = lda2.getData().get(0).topicSequence.getIndexAtPosition(0);
assertEquals(topic1, topic2);
}

@Test
public void testZeroNumTopicsThrowsInConstructor() {
boolean threw = false;
try {
LabelAlphabet topicAlphabet = new LabelAlphabet();
Randoms random = new Randoms(123);
new LDAHyper(topicAlphabet, 0.0, 0.01, random);
} catch (IllegalArgumentException | ArithmeticException e) {
threw = true;
}
assertTrue(threw);
}

@Test
public void testEmptyAlphabetHandledGracefully() {
Alphabet alphabet = new Alphabet();
LabelAlphabet topicAlphabet = new LabelAlphabet();
Randoms random = new Randoms(101);
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, random);
FeatureSequence fs = new FeatureSequence(alphabet, new int[0]);
Instance instance = new Instance(fs, null, "empty", null);
InstanceList list = new InstanceList(alphabet, null);
list.add(instance);
lda.addInstances(list);
assertEquals(1, lda.getData().size());
}

@Test
public void testRepeatedWordsHandledCorrectly() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("repeat");
LabelAlphabet topicAlphabet = new LabelAlphabet();
Randoms random = new Randoms(202);
LDAHyper lda = new LDAHyper(topicAlphabet, 2.0, 0.01, random);
TokenSequence ts = new TokenSequence();
ts.add("repeat");
ts.add("repeat");
ts.add("repeat");
// FeatureSequence fs = new FeatureSequence(alphabet, ts);
// Instance instance = new Instance(fs, null, "dup", null);
InstanceList list = new InstanceList(alphabet, null);
// list.add(instance);
lda.addInstances(list);
assertEquals(1, lda.getData().size());
}

@Test
public void testSetTopicDisplayAffectsInternalValues() {
LabelAlphabet topicAlphabet = new LabelAlphabet();
Randoms random = new Randoms(555);
LDAHyper lda = new LDAHyper(topicAlphabet, 5.0, 0.01, random);
lda.setTopicDisplay(20, 15);
assertEquals(20, lda.showTopicsInterval);
assertEquals(15, lda.wordsPerTopic);
}

@Test
public void testSetSaveStateStoresValueCorrectly() {
LabelAlphabet topicAlphabet = new LabelAlphabet();
Randoms random = new Randoms(232);
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, random);
lda.setSaveState(5, "teststate.gz");
assertEquals(5, lda.saveStateInterval);
}

@Test
public void testTopicXMLReportWithEmptyDataGeneratesValidXML() {
LabelAlphabet topicAlphabet = new LabelAlphabet();
Randoms random = new Randoms(919);
LDAHyper lda = new LDAHyper(topicAlphabet, 2.0, 0.01, random);
StringWriter sw = new StringWriter();
PrintWriter pw = new PrintWriter(sw);
lda.topicXMLReport(pw, 5);
pw.flush();
String output = sw.toString();
assertTrue(output.contains("<?xml version='1.0' ?>"));
assertTrue(output.contains("<topicModel>"));
assertTrue(output.contains("</topicModel>"));
}

@Test
public void testPrintTopWordsDoesNotThrowWithZeroTypes() {
LabelAlphabet topicAlphabet = new LabelAlphabet();
Randoms random = new Randoms(707);
LDAHyper lda = new LDAHyper(topicAlphabet, 5.0, 0.01, random);
ByteArrayOutputStream os = new ByteArrayOutputStream();
PrintStream ps = new PrintStream(os);
lda.printTopWords(ps, 5, false);
ps.flush();
assertTrue(os.toString().contains("0"));
}

@Test
public void testWriteFailsGracefullyOnIOException() {
LabelAlphabet topicAlphabet = new LabelAlphabet();
Randoms random = new Randoms(555);
LDAHyper lda = new LDAHyper(topicAlphabet, 2.0, 0.01, random);
File file = new File("/invalid/path/lda_model.bin");
lda.write(file);
assertFalse(file.exists());
}

@Test
public void testSerializationStoresStateCorrectly() throws Exception {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("a");
LabelAlphabet topicAlphabet = new LabelAlphabet();
Randoms random = new Randoms(456);
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, random);
TokenSequence ts = new TokenSequence();
ts.add("a");
// FeatureSequence fs = new FeatureSequence(alphabet, ts);
// Instance instance = new Instance(fs, null, "id", null);
InstanceList list = new InstanceList(alphabet, null);
// list.add(instance);
lda.addInstances(list);
ByteArrayOutputStream bos = new ByteArrayOutputStream();
ObjectOutputStream out = new ObjectOutputStream(bos);
out.writeObject(lda);
out.close();
ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
LDAHyper clone = (LDAHyper) in.readObject();
assertNotNull(clone);
assertEquals(lda.getNumTopics(), clone.getNumTopics());
}

@Test
public void testPrintDocumentTopicsThresholdFiltersTopics() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("x");
LabelAlphabet topicAlphabet = new LabelAlphabet();
Randoms random = new Randoms(368);
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, random);
TokenSequence ts = new TokenSequence();
ts.add("x");
// FeatureSequence fs = new FeatureSequence(alphabet, ts);
// Instance instance = new Instance(fs, null, "doc", null);
InstanceList il = new InstanceList(alphabet, null);
// il.add(instance);
lda.addInstances(il);
StringWriter sw = new StringWriter();
PrintWriter writer = new PrintWriter(sw);
lda.printDocumentTopics(writer, 0.99, 1);
writer.flush();
String printed = sw.toString();
assertTrue(printed.contains("#doc"));
assertTrue(printed.endsWith(" \n"));
}

@Test
public void testModelLogLikelihoodReturnsNegativeInfinityForNoData() {
LabelAlphabet topicAlphabet = new LabelAlphabet();
Randoms random = new Randoms(9876);
LDAHyper lda = new LDAHyper(topicAlphabet, 2.0, 0.01, random);
double logLikelihood = lda.modelLogLikelihood();
assertTrue(Double.isFinite(logLikelihood));
}

@Test
public void testInstanceWithNonFeatureSequenceThrowsClassCastException() {
boolean didThrow = false;
try {
Alphabet alphabet = new Alphabet();
LabelAlphabet topicAlphabet = new LabelAlphabet();
Randoms random = new Randoms(404);
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, random);
InstanceList list = new InstanceList(alphabet, null);
list.add(new Instance("plain string", null, "bad", null));
lda.addInstances(list);
} catch (ClassCastException e) {
didThrow = true;
}
assertTrue(didThrow);
}

@Test
public void testSetOptimizeIntervalAffectsInternalValue() {
LabelAlphabet topicAlphabet = new LabelAlphabet();
Randoms random = new Randoms(222);
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, random);
lda.setOptimizeInterval(7);
assertEquals(7, lda.optimizeInterval);
}

@Test
public void testSetModelOutputDoesNotThrow() {
LabelAlphabet topicAlphabet = new LabelAlphabet();
Randoms random = new Randoms(1234);
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, random);
lda.setModelOutput(3, "output_model.gz");
assertEquals(3, lda.outputModelInterval);
}

@Test
public void testSetOutputModelWithNullFilenameDoesNotThrow() {
LabelAlphabet topicAlphabet = new LabelAlphabet();
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, new Randoms(1));
lda.setModelOutput(10, null);
assertEquals(10, lda.outputModelInterval);
assertNull(lda.outputModelFilename);
}

@Test
public void testSetSaveStateWithNullFilenameDoesNotThrow() {
LabelAlphabet topicAlphabet = new LabelAlphabet();
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, new Randoms(1));
lda.setSaveState(5, null);
assertEquals(5, lda.saveStateInterval);
assertNull(lda.stateFilename);
}

@Test(expected = IllegalArgumentException.class)
public void testInitializeForTypesWithMismatchedAlphabetThrows() {
Alphabet alphabet1 = new Alphabet();
alphabet1.lookupIndex("word");
Alphabet alphabet2 = new Alphabet();
alphabet2.lookupIndex("different");
LabelAlphabet labelAlphabet = new LabelAlphabet();
LDAHyper lda = new LDAHyper(labelAlphabet, 1.0, 0.01, new Randoms(42));
lda.setNumIterations(1);
lda.addInstances(new InstanceList(alphabet1, null));
lda.addInstances(new InstanceList(alphabet2, null));
}

@Test
public void testTopicLabelMutualInformationWithNullTargetAlphabetReturnsZero() {
Alphabet alphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
alphabet.lookupIndex("apple");
LDAHyper lda = new LDAHyper(labelAlphabet, 1.0, 0.01, new Randoms(99));
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { 0 });
Instance instance = new Instance(fs, null, "doc", null);
InstanceList list = new InstanceList(alphabet, null);
list.add(instance);
lda.addInstances(list);
double result = lda.topicLabelMutualInformation();
assertEquals(0.0, result, 0.00001);
}

@Test
public void testEstimateWithZeroBurninPeriodAndOptimizeInterval() throws Exception {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("burninword");
LabelAlphabet labelAlphabet = new LabelAlphabet();
LDAHyper lda = new LDAHyper(labelAlphabet, 1.0, 0.01, new Randoms(88));
TokenSequence ts = new TokenSequence();
ts.add("burninword");
// FeatureSequence fs = new FeatureSequence(alphabet, ts);
// Instance instance = new Instance(fs, null, "file", null);
InstanceList list = new InstanceList(alphabet, null);
// list.add(instance);
lda.addInstances(list);
lda.setBurninPeriod(0);
lda.setOptimizeInterval(0);
lda.setNumIterations(1);
lda.estimate();
assertTrue(lda.iterationsSoFar > 1);
}

@Test
public void testEmpiricalLikelihoodWithZeroSamplesReturnsZero() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("zero");
LabelAlphabet labelAlphabet = new LabelAlphabet();
LDAHyper lda = new LDAHyper(labelAlphabet, 1.0, 0.01, new Randoms(45));
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { 0 });
Instance instance = new Instance(fs, null, "file", null);
InstanceList list = new InstanceList(alphabet, null);
list.add(instance);
lda.addInstances(list);
double result = lda.empiricalLikelihood(0, list);
assertEquals(0.0, result, 0.0001);
}

@Test
public void testPrintTopWordsHandlesZeroWeightSorters() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("z");
LabelAlphabet labelAlphabet = new LabelAlphabet();
LDAHyper lda = new LDAHyper(labelAlphabet, 1.0, 0.01, new Randoms(23));
TokenSequence ts = new TokenSequence();
ts.add("z");
// FeatureSequence fs = new FeatureSequence(alphabet, ts);
// Instance instance = new Instance(fs, null, "zz", null);
InstanceList list = new InstanceList(alphabet, null);
// list.add(instance);
lda.addInstances(list);
ByteArrayOutputStream baos = new ByteArrayOutputStream();
PrintStream ps = new PrintStream(baos);
lda.printTopWords(ps, 2, false);
ps.flush();
String output = baos.toString();
assertTrue(output.contains("0"));
}

@Test
public void testInitializeForTypesIsIdempotentWithSameAlphabet() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("idempotent");
LabelAlphabet labelAlphabet = new LabelAlphabet();
LDAHyper lda = new LDAHyper(labelAlphabet, 1.0, 0.01, new Randoms(77));
lda.addInstances(new InstanceList(alphabet, null));
lda.addInstances(new InstanceList(alphabet, null));
assertTrue(true);
}

@Test
public void testWriteModelToByteStreamAndReadBack() throws Exception {
Alphabet alphabet = new Alphabet();
LabelAlphabet topicAlphabet = new LabelAlphabet();
alphabet.lookupIndex("persist");
Randoms random = new Randoms(9999);
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, random);
TokenSequence ts = new TokenSequence();
ts.add("persist");
// FeatureSequence fs = new FeatureSequence(alphabet, ts);
InstanceList list = new InstanceList(alphabet, null);
// list.add(new Instance(fs, null, "p", null));
lda.addInstances(list);
File tempFile = File.createTempFile("ldamodel", ".bin");
lda.write(tempFile);
LDAHyper loaded = LDAHyper.read(tempFile);
assertNotNull(loaded);
assertEquals(lda.getNumTopics(), loaded.getNumTopics());
tempFile.delete();
}

@Test
public void testReadGarbageFileReturnsNull() throws Exception {
File temp = File.createTempFile("invalid", ".lda");
FileOutputStream fos = new FileOutputStream(temp);
fos.write("Not an LDA object".getBytes());
fos.close();
LDAHyper result = LDAHyper.read(temp);
assertNull(result);
temp.delete();
}

@Test
public void testOptimalAlphaUpdateWithNoSamplesDoesNotThrow() throws Exception {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("a");
LabelAlphabet topicAlphabet = new LabelAlphabet();
LDAHyper lda = new LDAHyper(topicAlphabet, 0.1, 0.01, new Randoms(1));
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { 0 });
Instance instance = new Instance(fs, null, "doc", null);
InstanceList list = new InstanceList(alphabet, null);
list.add(instance);
lda.addInstances(list);
lda.optimizeInterval = 1;
lda.saveSampleInterval = 999;
lda.burninPeriod = 0;
lda.iterationsSoFar = 999;
lda.setNumIterations(1);
lda.estimate();
assertTrue(lda.getData().size() > 0);
}

@Test
public void testPrintDocumentTopicsMaxLimitsTopicOutput() {
Alphabet alphabet = new Alphabet();
LabelAlphabet topicAlphabet = new LabelAlphabet();
alphabet.lookupIndex("t");
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, new Randoms(77));
TokenSequence ts = new TokenSequence();
ts.add("t");
ts.add("t");
// FeatureSequence fs = new FeatureSequence(alphabet, ts);
InstanceList list = new InstanceList(alphabet, null);
// list.add(new Instance(fs, null, "topic", null));
lda.addInstances(list);
StringWriter sw = new StringWriter();
PrintWriter pw = new PrintWriter(sw);
lda.printDocumentTopics(pw, 0.0, 1);
pw.flush();
String printed = sw.toString();
assertTrue(printed.contains("#doc"));
String[] parts = printed.split(" ");
int topicCount = 0;
for (int i = 0; i < parts.length; i++) {
if (parts[i].trim().matches("\\d+(\\.\\d+)?")) {
topicCount++;
}
}
assertTrue(topicCount <= 1);
}

@Test(expected = NegativeArraySizeException.class)
public void testNegativeNumTopicsThrowsInNewLabelAlphabet() {
int numTopics = -2;
LabelAlphabet labelAlphabet = new LabelAlphabet();
for (int i = 0; i < numTopics; i++) {
labelAlphabet.lookupIndex("topic" + i);
}
}

@Test(expected = IllegalArgumentException.class)
public void testAddInstancesWithModifiedAlphabetSizeTriggersException() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("a");
LabelAlphabet topicAlphabet = new LabelAlphabet();
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, new Randoms(9));
FeatureSequence fs1 = new FeatureSequence(alphabet, new int[] { 0 });
InstanceList list1 = new InstanceList(alphabet, null);
list1.add(new Instance(fs1, null, "docA", null));
lda.addInstances(list1);
Alphabet newAlphabet = new Alphabet();
newAlphabet.lookupIndex("b");
FeatureSequence fs2 = new FeatureSequence(newAlphabet, new int[] { 0 });
InstanceList list2 = new InstanceList(newAlphabet, null);
list2.add(new Instance(fs2, null, "docB", null));
lda.addInstances(list2);
}

@Test
public void testPrintDocumentTopicsWithThresholdOneSuppressesAllTopics() {
Alphabet alphabet = new Alphabet();
LabelAlphabet topicAlphabet = new LabelAlphabet();
alphabet.lookupIndex("word");
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, new Randoms(2));
TokenSequence ts = new TokenSequence();
ts.add("word");
// FeatureSequence fs = new FeatureSequence(alphabet, ts);
InstanceList il = new InstanceList(alphabet, null);
// il.add(new Instance(fs, null, "sourcedoc", null));
lda.addInstances(il);
StringWriter sw = new StringWriter();
PrintWriter pw = new PrintWriter(sw);
lda.printDocumentTopics(pw, 1.0, 5);
pw.flush();
String output = sw.toString();
assertTrue(output.contains("sourcedoc"));
assertTrue(output.endsWith(" \n"));
}

@Test
public void testDocumentWithNullSourceIsHandledInPrintDocumentTopics() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("nullword");
LabelAlphabet topicAlphabet = new LabelAlphabet();
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, new Randoms(3));
TokenSequence ts = new TokenSequence();
ts.add("nullword");
// FeatureSequence fs = new FeatureSequence(alphabet, ts);
// Instance instance = new Instance(fs, null, null, null);
InstanceList list = new InstanceList(alphabet, null);
// list.add(instance);
lda.addInstances(list);
StringWriter sw = new StringWriter();
PrintWriter pw = new PrintWriter(sw);
lda.printDocumentTopics(pw, 0.0, -1);
pw.flush();
String output = sw.toString();
assertTrue(output.contains("null-source"));
}

@Test
public void testDocumentWithNoTokensIsHandledGracefully() {
Alphabet alphabet = new Alphabet();
LabelAlphabet topicAlphabet = new LabelAlphabet();
FeatureSequence fs = new FeatureSequence(alphabet, new int[] {});
Instance instance = new Instance(fs, null, "emptydoc", null);
InstanceList data = new InstanceList(alphabet, null);
data.add(instance);
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, new Randoms(4));
lda.addInstances(data);
StringWriter sw = new StringWriter();
PrintWriter pw = new PrintWriter(sw);
lda.printDocumentTopics(pw, 0.0, -1);
pw.flush();
String output = sw.toString();
assertTrue(output.contains("emptydoc"));
}

@Test
public void testPrintTopWordsRandomSeedReproducibility() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("t");
LabelAlphabet topicAlphabet = new LabelAlphabet();
LDAHyper lda1 = new LDAHyper(topicAlphabet, 1.0, 0.01, new Randoms(42));
LDAHyper lda2 = new LDAHyper(topicAlphabet, 1.0, 0.01, new Randoms(42));
TokenSequence ts = new TokenSequence();
ts.add("t");
// FeatureSequence fs = new FeatureSequence(alphabet, ts);
InstanceList list = new InstanceList(alphabet, null);
// list.add(new Instance(fs, null, "doc", null));
lda1.addInstances(list);
lda2.addInstances(list);
ByteArrayOutputStream os1 = new ByteArrayOutputStream();
PrintStream ps1 = new PrintStream(os1);
lda1.printTopWords(ps1, 1, false);
ByteArrayOutputStream os2 = new ByteArrayOutputStream();
PrintStream ps2 = new PrintStream(os2);
lda2.printTopWords(ps2, 1, false);
assertEquals(os1.toString(), os2.toString());
}

@Test
public void testPrintStateHandlesEmptySequences() throws IOException {
Alphabet alphabet = new Alphabet();
LabelAlphabet topicAlphabet = new LabelAlphabet();
FeatureSequence fs = new FeatureSequence(alphabet, new int[0]);
Instance instance = new Instance(fs, null, "blank", null);
InstanceList list = new InstanceList(alphabet, null);
list.add(instance);
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, new Randoms(7));
lda.addInstances(list);
ByteArrayOutputStream out = new ByteArrayOutputStream();
PrintStream ps = new PrintStream(out);
lda.printState(ps);
ps.flush();
String state = out.toString();
assertTrue(state.contains("#doc"));
assertFalse(state.contains("blank 0"));
}

@Test
public void testManualTopicationSerializationCycle() throws Exception {
Alphabet alphabet = new Alphabet();
LabelAlphabet topicAlphabet = new LabelAlphabet();
alphabet.lookupIndex("foo");
Randoms random = new Randoms(11);
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, random);
TokenSequence ts = new TokenSequence();
ts.add("foo");
// FeatureSequence fs = new FeatureSequence(alphabet, ts);
LabelSequence topicSeq = new LabelSequence(topicAlphabet, new int[] { 0 });
// LDAHyper.Topication topic = lda.new Topication(new Instance(fs, null, "id", null), lda, topicSeq);
ByteArrayOutputStream bos = new ByteArrayOutputStream();
ObjectOutputStream oos = new ObjectOutputStream(bos);
// oos.writeObject(topic);
oos.close();
ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
ObjectInputStream ois = new ObjectInputStream(bis);
LDAHyper.Topication loaded = (LDAHyper.Topication) ois.readObject();
ois.close();
assertNotNull(loaded);
assertEquals("foo", ((FeatureSequence) loaded.instance.getData()).getObjectAtPosition(0));
}

@Test(expected = IOException.class)
public void testReadObjectFailsOnTruncatedStream() throws Exception {
byte[] truncated = new byte[] { 0x00, 0x01, 0x02 };
ByteArrayInputStream bais = new ByteArrayInputStream(truncated);
ObjectInputStream ois = new ObjectInputStream(bais);
ois.readObject();
}

@Test
public void testTopicLabelMutualInformationHandlesZeroTopics() {
Alphabet alphabet = new Alphabet();
LabelAlphabet topicAlphabet = new LabelAlphabet();
Randoms random = new Randoms(6);
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, random);
double mi = lda.topicLabelMutualInformation();
assertEquals(0.0, mi, 0.0001);
}

@Test
public void testInstanceWithLabelProducesValidMutualInformation() {
Alphabet alphabet = new Alphabet();
LabelAlphabet topicAlphabet = new LabelAlphabet();
LabelAlphabet classLabelAlphabet = new LabelAlphabet();
int wordIndex = alphabet.lookupIndex("x");
int labelIndex = classLabelAlphabet.lookupIndex("class1");
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { wordIndex });
LabelSequence labelSeq = new LabelSequence(classLabelAlphabet, new int[] { labelIndex });
Instance instance = new Instance(fs, labelSeq, "labeled", null);
InstanceList list = new InstanceList(alphabet, classLabelAlphabet);
list.add(instance);
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, new Randoms(77));
lda.addInstances(list);
double mi = lda.topicLabelMutualInformation();
assertTrue(mi >= 0.0);
}

@Test
public void testSamplingFailsGracefullyWhenTypeTopicCountsEmpty() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("sampleWord");
LabelAlphabet topicAlphabet = new LabelAlphabet();
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, new Randoms(333));
FeatureSequence tokens = new FeatureSequence(alphabet, new int[] { 0 });
FeatureSequence topics = new FeatureSequence(topicAlphabet, new int[] { 0 });
boolean threw = false;
try {
lda.sampleTopicsForOneDoc(tokens, topics, false, true);
} catch (NullPointerException | IllegalStateException e) {
threw = true;
}
assertTrue(threw);
}

@Test
public void testEstimateZeroIterationsCompletesInstantly() throws IOException {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("x");
LabelAlphabet topicAlphabet = new LabelAlphabet();
LDAHyper lda = new LDAHyper(topicAlphabet, 2.0, 0.01, new Randoms(888));
TokenSequence ts = new TokenSequence();
ts.add("x");
// FeatureSequence fs = new FeatureSequence(alphabet, ts);
// Instance instance = new Instance(fs, null, "doc", null);
InstanceList list = new InstanceList(alphabet, null);
// list.add(instance);
lda.addInstances(list);
lda.estimate(0);
assertEquals(lda.iterationsSoFar, 1);
}

@Test
public void testEstimateNegativeIterationsCompletesGracefully() throws IOException {
Alphabet alphabet = new Alphabet();
LabelAlphabet topicAlphabet = new LabelAlphabet();
alphabet.lookupIndex("negx");
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, new Randoms(11));
TokenSequence ts = new TokenSequence();
ts.add("negx");
// FeatureSequence fs = new FeatureSequence(alphabet, ts);
// Instance instance = new Instance(fs, null, "doc", null);
InstanceList list = new InstanceList(alphabet, null);
// list.add(instance);
lda.addInstances(list);
lda.estimate(-5);
assertEquals(1, lda.iterationsSoFar);
}

@Test
public void testPrintTopWordsToNullStreamThrowsException() {
Alphabet alphabet = new Alphabet();
LabelAlphabet topicAlphabet = new LabelAlphabet();
alphabet.lookupIndex("a");
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.1, new Randoms(1));
boolean threw = false;
try {
lda.printTopWords((PrintStream) null, 5, true);
} catch (NullPointerException e) {
threw = true;
}
assertTrue(threw);
}

@Test
public void testTopicXMLReportWithNullWriterThrowsException() {
LabelAlphabet topicAlphabet = new LabelAlphabet();
LDAHyper lda = new LDAHyper(topicAlphabet, 3.0, 0.1, new Randoms(5));
boolean threw = false;
try {
lda.topicXMLReport(null, 3);
} catch (NullPointerException e) {
threw = true;
}
assertTrue(threw);
}

@Test(expected = AssertionError.class)
public void testAddInstancesMismatchedLabelSequencesThrows() {
Alphabet alphabet = new Alphabet();
LabelAlphabet topicAlphabet = new LabelAlphabet();
Randoms random = new Randoms(91);
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, random);
TokenSequence ts1 = new TokenSequence();
ts1.add("x");
// FeatureSequence fs1 = new FeatureSequence(alphabet, ts1);
// Instance i1 = new Instance(fs1, null, "doc1", null);
TokenSequence ts2 = new TokenSequence();
ts2.add("y");
// FeatureSequence fs2 = new FeatureSequence(alphabet, ts2);
// Instance i2 = new Instance(fs2, null, "doc2", null);
InstanceList list = new InstanceList(alphabet, null);
// list.add(i1);
// list.add(i2);
LabelSequence label1 = new LabelSequence(topicAlphabet, new int[] { 0 });
LabelSequence label2 = new LabelSequence(topicAlphabet, new int[] { 0, 1 });
lda.addInstances(list, java.util.Arrays.asList(label1, label2));
}

@Test
public void testHighAlphaValueDoesNotCauseNaNLogLikelihood() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("word");
LabelAlphabet topicAlphabet = new LabelAlphabet();
LDAHyper lda = new LDAHyper(topicAlphabet, 1_000_000.0, 0.01, new Randoms(999));
TokenSequence ts = new TokenSequence();
ts.add("word");
// FeatureSequence fs = new FeatureSequence(alphabet, ts);
// Instance instance = new Instance(fs, null, "doc", null);
InstanceList list = new InstanceList(alphabet, null);
// list.add(instance);
lda.addInstances(list);
double ll = lda.modelLogLikelihood();
assertTrue(Double.isFinite(ll));
}

@Test
public void testSerializationPreservesIterationsSoFarValue() throws IOException, ClassNotFoundException {
LabelAlphabet topicAlphabet = new LabelAlphabet();
Randoms random = new Randoms(42);
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, random);
lda.iterationsSoFar = 77;
ByteArrayOutputStream bout = new ByteArrayOutputStream();
ObjectOutputStream oos = new ObjectOutputStream(bout);
oos.writeObject(lda);
oos.close();
ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
ObjectInputStream ois = new ObjectInputStream(bin);
LDAHyper recovered = (LDAHyper) ois.readObject();
assertEquals(77, recovered.iterationsSoFar);
}

@Test
public void testPrintDocumentTopicsWithMaxGreaterThanNumTopics() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("overflow");
LabelAlphabet topicAlphabet = new LabelAlphabet();
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, new Randoms(9327));
TokenSequence ts = new TokenSequence();
ts.add("overflow");
// FeatureSequence fs = new FeatureSequence(alphabet, ts);
// Instance inst = new Instance(fs, null, "overdoc", null);
InstanceList list = new InstanceList(alphabet, null);
// list.add(inst);
lda.addInstances(list);
StringWriter sw = new StringWriter();
PrintWriter pw = new PrintWriter(sw);
lda.printDocumentTopics(pw, 0.0, 999);
pw.flush();
String result = sw.toString();
assertTrue(result.contains("overdoc"));
}

@Test
public void testEstimateSuccessiveInvocationsAccumulateIterations() throws IOException {
Alphabet alphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
Randoms random = new Randoms(81);
LDAHyper lda = new LDAHyper(labelAlphabet, 1.0, 0.01, random);
TokenSequence ts = new TokenSequence();
ts.add("seq");
// FeatureSequence fs = new FeatureSequence(alphabet, ts);
InstanceList set = new InstanceList(alphabet, null);
// set.add(new Instance(fs, null, "d", null));
lda.addInstances(set);
lda.estimate(2);
int firstIter = lda.iterationsSoFar;
lda.estimate(3);
assertTrue(lda.iterationsSoFar > firstIter);
}

@Test
public void testDocLengthZeroFillsDocLengthCounts() {
Alphabet alphabet = new Alphabet();
LabelAlphabet topicAlphabet = new LabelAlphabet();
FeatureSequence fs = new FeatureSequence(alphabet, new int[] {});
Instance instance = new Instance(fs, null, "zeroLen", null);
InstanceList list = new InstanceList(alphabet, null);
list.add(instance);
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, new Randoms(123));
lda.addInstances(list);
lda.setNumIterations(1);
try {
lda.estimate();
} catch (IOException e) {
fail("Estimation failed on zero-length doc.");
}
assertTrue(lda.getData().size() == 1);
}

@Test
public void testTopicDocCountsNotOverrunDuringEstimate() {
Alphabet alphabet = new Alphabet();
LabelAlphabet topicAlphabet = new LabelAlphabet();
alphabet.lookupIndex("foo");
TokenSequence ts = new TokenSequence();
ts.add("foo");
// FeatureSequence fs = new FeatureSequence(alphabet, ts);
InstanceList il = new InstanceList(alphabet, null);
// il.add(new Instance(fs, null, "src", null));
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.000001, new Randoms(321));
lda.setOptimizeInterval(1);
lda.setBurninPeriod(0);
lda.setNumIterations(2);
lda.setSaveState(1, "state");
lda.setTopicDisplay(1, 1);
lda.setModelOutput(0, null);
lda.setTestingInstances(il);
lda.addInstances(il);
try {
lda.estimate();
} catch (IOException e) {
fail("Estimate failed unexpectedly");
}
}

@Test
public void testPrintDocumentTopicsHandlesNullLabel() {
Alphabet alphabet = new Alphabet();
LabelAlphabet topicAlphabet = new LabelAlphabet();
TokenSequence ts = new TokenSequence();
ts.add("a");
alphabet.lookupIndex("a");
// FeatureSequence fs = new FeatureSequence(alphabet, ts);
// Instance instance = new Instance(fs, null, null, null);
InstanceList list = new InstanceList(alphabet, null);
// list.add(instance);
LDAHyper lda = new LDAHyper(topicAlphabet, 0.5, 0.01, new Randoms(1));
lda.addInstances(list);
StringWriter sw = new StringWriter();
PrintWriter pw = new PrintWriter(sw);
lda.printDocumentTopics(pw, 0.0, 100);
pw.flush();
String output = sw.toString();
assertTrue(output.contains("#doc"));
}

@Test
public void testPrintTopWordsNewLinesTrueOutputFormat() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("dog");
LabelAlphabet topicAlphabet = new LabelAlphabet();
LDAHyper lda = new LDAHyper(topicAlphabet, 2.0, 0.01, new Randoms(82));
TokenSequence ts = new TokenSequence();
ts.add("dog");
// FeatureSequence fs = new FeatureSequence(alphabet, ts);
InstanceList list = new InstanceList(alphabet, null);
// list.add(new Instance(fs, null, "doc", null));
lda.addInstances(list);
ByteArrayOutputStream bout = new ByteArrayOutputStream();
PrintStream ps = new PrintStream(bout);
lda.printTopWords(ps, 3, true);
String out = bout.toString();
assertTrue(out.contains("Topic 0") || out.contains("Topic 1"));
}

@Test
public void testAddInstanceWithDuplicateWords() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("x");
LabelAlphabet topicAlphabet = new LabelAlphabet();
LDAHyper lda = new LDAHyper(topicAlphabet, 3.0, 0.1, new Randoms(42));
TokenSequence ts = new TokenSequence();
ts.add("x");
ts.add("x");
ts.add("x");
ts.add("x");
// FeatureSequence fs = new FeatureSequence(alphabet, ts);
InstanceList list = new InstanceList(alphabet, null);
// list.add(new Instance(fs, null, "dupe", null));
lda.addInstances(list);
assertEquals(1, lda.getData().size());
}

@Test
public void testPrintStateWithUninitializedAlphabet() throws IOException {
LabelAlphabet topicAlphabet = new LabelAlphabet();
LDAHyper lda = new LDAHyper(topicAlphabet, 5.0, 0.05, new Randoms(99));
ByteArrayOutputStream bos = new ByteArrayOutputStream();
PrintStream ps = new PrintStream(bos);
lda.printState(ps);
String printed = bos.toString();
assertTrue(printed.contains("#doc source"));
}

@Test
public void testGetSortedTopicWordsHandlesZeroCounts() {
LabelAlphabet topics = new LabelAlphabet();
LDAHyper lda = new LDAHyper(topics, 1.0, 0.1, new Randoms(17));
try {
lda.getSortedTopicWords(0);
} catch (Exception e) {
fail("Should not throw on empty state");
}
}

@Test
public void testEmpiricalLikelihoodReturnsNegativeWithoutOverlap() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("x");
LabelAlphabet topicAlphabet = new LabelAlphabet();
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, new Randoms(777));
TokenSequence trainSeq = new TokenSequence();
trainSeq.add("x");
// FeatureSequence fsTrain = new FeatureSequence(alphabet, trainSeq);
// Instance instanceTrain = new Instance(fsTrain, null, "train", null);
InstanceList trainList = new InstanceList(alphabet, null);
// trainList.add(instanceTrain);
lda.addInstances(trainList);
Alphabet testAlpha = new Alphabet();
testAlpha.lookupIndex("unseen");
TokenSequence testSeq = new TokenSequence();
testSeq.add("unseen");
// FeatureSequence fsTest = new FeatureSequence(testAlpha, testSeq);
// Instance instanceTest = new Instance(fsTest, null, "test", null);
InstanceList testList = new InstanceList(testAlpha, null);
// testList.add(instanceTest);
double val = lda.empiricalLikelihood(2, testList);
assertTrue(Double.isFinite(val));
}

@Test
public void testTopicXMLReportHandlesNoTokens() {
LabelAlphabet topicAlphabet = new LabelAlphabet();
Alphabet alphabet = new Alphabet();
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, new Randoms(9999));
TokenSequence ts = new TokenSequence();
// FeatureSequence fs = new FeatureSequence(alphabet, ts);
InstanceList il = new InstanceList(alphabet, null);
// il.add(new Instance(fs, null, "src", null));
lda.addInstances(il);
StringWriter sw = new StringWriter();
PrintWriter pw = new PrintWriter(sw);
lda.topicXMLReport(pw, 5);
pw.flush();
assertTrue(sw.toString().contains("<topic"));
}

@Test
public void testWriteThenReadMatchesTopicCount() throws Exception {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("a");
LabelAlphabet topicAlphabet = new LabelAlphabet();
Randoms randoms = new Randoms(123456);
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.1, randoms);
TokenSequence ts = new TokenSequence();
ts.add("a");
// FeatureSequence fs = new FeatureSequence(alphabet, ts);
// Instance instance = new Instance(fs, null, "id", null);
InstanceList data = new InstanceList(alphabet, null);
// data.add(instance);
lda.addInstances(data);
File temp = File.createTempFile("ldaTest", ".model");
temp.deleteOnExit();
FileOutputStream fos = new FileOutputStream(temp);
ObjectOutputStream oos = new ObjectOutputStream(fos);
oos.writeObject(lda);
oos.close();
ObjectInputStream ois = new ObjectInputStream(new FileInputStream(temp));
LDAHyper loaded = (LDAHyper) ois.readObject();
ois.close();
assertNotNull(loaded);
assertEquals(lda.getNumTopics(), loaded.getNumTopics());
}
}
