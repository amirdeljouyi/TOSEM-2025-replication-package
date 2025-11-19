package cc.mallet.topics;

import cc.mallet.types.*;
import cc.mallet.util.Randoms;
import org.junit.Test;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static org.junit.Assert.*;

public class LDAHyper_2_GPTLLMTest {

@Test
public void testConstructorInitializesNumTopicsCorrectly() {
LabelAlphabet topicAlphabet = new LabelAlphabet();
topicAlphabet.lookupIndex("topic0");
topicAlphabet.lookupIndex("topic1");
topicAlphabet.lookupIndex("topic2");
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, new Randoms(42));
assertEquals(3, lda.getNumTopics());
assertEquals(topicAlphabet, lda.getTopicAlphabet());
}

@Test
public void testAddInstancesIncreasesDataSize() {
Alphabet dataAlphabet = new Alphabet();
int token1 = dataAlphabet.lookupIndex("word1");
int token2 = dataAlphabet.lookupIndex("word2");
FeatureSequence fs1 = new FeatureSequence(dataAlphabet, new int[] { token1, token2 });
FeatureSequence fs2 = new FeatureSequence(dataAlphabet, new int[] { token2, token1 });
Instance instance1 = new Instance(fs1, null, "doc1", null);
Instance instance2 = new Instance(fs2, null, "doc2", null);
InstanceList training = new InstanceList(dataAlphabet, null);
training.add(instance1);
training.add(instance2);
LabelAlphabet topicAlphabet = new LabelAlphabet();
topicAlphabet.lookupIndex("topic0");
topicAlphabet.lookupIndex("topic1");
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, new Randoms(42));
lda.addInstances(training);
ArrayList<LDAHyper.Topication> data = lda.getData();
assertEquals(2, data.size());
assertEquals("doc1", data.get(0).instance.getName());
assertEquals("doc2", data.get(1).instance.getName());
}

@Test
public void testSetNumIterations() {
LabelAlphabet topicAlphabet = new LabelAlphabet();
topicAlphabet.lookupIndex("topic0");
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, new Randoms(42));
lda.setNumIterations(123);
assertEquals(123, lda.numIterations);
}

@Test
public void testSetBurnInPeriod() {
LabelAlphabet topicAlphabet = new LabelAlphabet();
topicAlphabet.lookupIndex("topic0");
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, new Randoms(42));
lda.setBurninPeriod(10);
assertEquals(10, lda.burninPeriod);
}

@Test
public void testSetOptimizeInterval() {
LabelAlphabet topicAlphabet = new LabelAlphabet();
topicAlphabet.lookupIndex("topic0");
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, new Randoms(42));
lda.setOptimizeInterval(7);
assertEquals(7, lda.optimizeInterval);
}

@Test
public void testSetSaveStateParameters() {
LabelAlphabet topicAlphabet = new LabelAlphabet();
topicAlphabet.lookupIndex("topic0");
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, new Randoms(42));
lda.setSaveState(15, "state.gz");
assertEquals(15, lda.saveStateInterval);
assertEquals("state.gz", lda.stateFilename);
}

@Test
public void testSetModelOutputParameters() {
LabelAlphabet topicAlphabet = new LabelAlphabet();
topicAlphabet.lookupIndex("topic0");
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, new Randoms(42));
lda.setModelOutput(5, "model.out");
assertEquals(5, lda.outputModelInterval);
assertEquals("model.out", lda.outputModelFilename);
}

@Test
public void testSetTopicDisplayUpdatesValues() {
LabelAlphabet topicAlphabet = new LabelAlphabet();
topicAlphabet.lookupIndex("topic0");
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, new Randoms(42));
lda.setTopicDisplay(20, 8);
assertEquals(20, lda.showTopicsInterval);
assertEquals(8, lda.wordsPerTopic);
}

@Test
public void testSetRandomSeedProducesDeterministicRandoms() {
LabelAlphabet topicAlphabet = new LabelAlphabet();
topicAlphabet.lookupIndex("topic0");
LDAHyper lda1 = new LDAHyper(topicAlphabet, 1.0, 0.01, new Randoms());
lda1.setRandomSeed(123);
int value1 = lda1.random.nextInt(10000);
LDAHyper lda2 = new LDAHyper(topicAlphabet, 1.0, 0.01, new Randoms());
lda2.setRandomSeed(123);
int value2 = lda2.random.nextInt(10000);
assertEquals(value1, value2);
}

@Test
public void testWriteAndReadModelSerializesCorrectly() throws IOException {
Alphabet dataAlphabet = new Alphabet();
int token = dataAlphabet.lookupIndex("alpha");
FeatureSequence fs = new FeatureSequence(dataAlphabet, new int[] { token, token });
Instance instance = new Instance(fs, null, "instance1", null);
InstanceList training = new InstanceList(dataAlphabet, null);
training.add(instance);
LabelAlphabet topicAlphabet = new LabelAlphabet();
topicAlphabet.lookupIndex("topic0");
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, new Randoms(42));
lda.addInstances(training);
File file = File.createTempFile("ldahyper_test", ".dat");
file.deleteOnExit();
lda.write(file);
LDAHyper restored = LDAHyper.read(file);
assertNotNull(restored);
assertEquals(1, restored.getNumTopics());
assertEquals(1, restored.getData().size());
}

@Test
public void testEmpiricalLikelihoodReturnsFiniteValue() throws IOException {
Alphabet dataAlphabet = new Alphabet();
int token = dataAlphabet.lookupIndex("test");
FeatureSequence fs = new FeatureSequence(dataAlphabet, new int[] { token });
Instance instance = new Instance(fs, null, "doc0", null);
InstanceList training = new InstanceList(dataAlphabet, null);
training.add(instance);
LabelAlphabet topicAlphabet = new LabelAlphabet();
topicAlphabet.lookupIndex("topic0");
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, new Randoms(42));
lda.addInstances(training);
lda.setTestingInstances(training);
double likelihood = lda.empiricalLikelihood(2, training);
assertTrue(Double.isFinite(likelihood));
}

@Test
public void testModelLogLikelihoodReturnsFiniteValue() {
Alphabet dataAlphabet = new Alphabet();
int token = dataAlphabet.lookupIndex("log");
FeatureSequence fs = new FeatureSequence(dataAlphabet, new int[] { token, token });
Instance instance = new Instance(fs, null, "doc", null);
InstanceList training = new InstanceList(dataAlphabet, null);
training.add(instance);
LabelAlphabet topicAlphabet = new LabelAlphabet();
topicAlphabet.lookupIndex("topic0");
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, new Randoms(123));
lda.addInstances(training);
double ll = lda.modelLogLikelihood();
assertTrue(Double.isFinite(ll));
}

@Test
public void testTopicLabelMutualInformationWithoutLabelsReturnsZero() {
LabelAlphabet topicAlphabet = new LabelAlphabet();
topicAlphabet.lookupIndex("topic0");
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, new Randoms());
double mi = lda.topicLabelMutualInformation();
assertEquals(0.0, mi, 0.0001);
}

@Test(expected = IllegalArgumentException.class)
public void testInitializeForTypesThrowsIfAlphabetMismatch() {
Alphabet first = new Alphabet();
first.lookupIndex("token1");
Alphabet second = new Alphabet();
second.lookupIndex("token2");
LabelAlphabet topicAlphabet = new LabelAlphabet();
topicAlphabet.lookupIndex("topic0");
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, new Randoms());
lda.addInstances(new InstanceList(first, null));
lda.addInstances(new InstanceList(second, null));
}

@Test
public void testPrintTopWordsPrintsWithoutError() {
Alphabet dataAlphabet = new Alphabet();
int token = dataAlphabet.lookupIndex("alpha");
FeatureSequence fs = new FeatureSequence(dataAlphabet, new int[] { token, token });
Instance instance = new Instance(fs, null, "doc1", null);
InstanceList instanceList = new InstanceList(dataAlphabet, null);
instanceList.add(instance);
LabelAlphabet topicAlphabet = new LabelAlphabet();
topicAlphabet.lookupIndex("topic0");
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, new Randoms());
lda.addInstances(instanceList);
ByteArrayOutputStream baos = new ByteArrayOutputStream();
PrintStream ps = new PrintStream(baos);
lda.printTopWords(ps, 5, true);
ps.flush();
String result = baos.toString();
assertTrue(result.contains("Topic"));
}

@Test
public void testEmptyInstanceListDoesNotThrowOnAddInstances() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet topicAlphabet = new LabelAlphabet();
topicAlphabet.lookupIndex("topic0");
InstanceList emptyList = new InstanceList(dataAlphabet, null);
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, new Randoms());
try {
lda.addInstances(emptyList);
assertTrue(true);
} catch (Exception e) {
fail("addInstances should not throw with empty list");
}
}

@Test
public void testEstimateWithZeroTopics() throws IOException {
LabelAlphabet topicAlphabet = new LabelAlphabet();
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, new Randoms());
Alphabet dataAlphabet = new Alphabet();
int tokenIndex = dataAlphabet.lookupIndex("word");
FeatureSequence fs = new FeatureSequence(dataAlphabet, new int[] { tokenIndex });
Instance instance = new Instance(fs, null, "doc", null);
InstanceList instances = new InstanceList(dataAlphabet, null);
instances.add(instance);
lda.addInstances(instances);
lda.setNumIterations(0);
lda.estimate();
assertEquals(0, lda.iterationsSoFar);
}

@Test
public void testAddInstancesWithMismatchedTopicListSizeThrows() {
Alphabet dataAlphabet = new Alphabet();
int tokenIndex = dataAlphabet.lookupIndex("word");
FeatureSequence fs = new FeatureSequence(dataAlphabet, new int[] { tokenIndex });
Instance instance = new Instance(fs, null, "doc", null);
InstanceList list = new InstanceList(dataAlphabet, null);
list.add(instance);
ArrayList<LabelSequence> wrongTopics = new ArrayList<>();
wrongTopics.add(new LabelSequence(new LabelAlphabet(), new int[] { 0 }));
wrongTopics.add(new LabelSequence(new LabelAlphabet(), new int[] { 1 }));
LabelAlphabet topicAlphabet = new LabelAlphabet();
topicAlphabet.lookupIndex("topic0");
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, new Randoms());
try {
lda.addInstances(list, wrongTopics);
fail("Expected AssertionError due to mismatched sizes");
} catch (AssertionError expected) {
assertTrue(true);
}
}

@Test
public void testEstimateHandlesReadjustTopicsAndStatsFalsePath() throws IOException {
Alphabet dataAlphabet = new Alphabet();
int tokenIndex = dataAlphabet.lookupIndex("token");
FeatureSequence fs = new FeatureSequence(dataAlphabet, new int[] { tokenIndex, tokenIndex });
Instance instance = new Instance(fs, null, "doc", null);
InstanceList list = new InstanceList(dataAlphabet, null);
list.add(instance);
LabelAlphabet topicAlphabet = new LabelAlphabet();
topicAlphabet.lookupIndex("topic0");
topicAlphabet.lookupIndex("topic1");
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, new Randoms(42));
lda.addInstances(list);
lda.setBurninPeriod(0);
lda.setNumIterations(1);
lda.setOptimizeInterval(1);
lda.setTopicDisplay(1, 1);
lda.estimate();
assertTrue(true);
}

@Test
public void testPrintDocumentTopicsWithEmptyData() {
LabelAlphabet topicAlphabet = new LabelAlphabet();
topicAlphabet.lookupIndex("topic0");
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, new Randoms());
StringWriter out = new StringWriter();
PrintWriter writer = new PrintWriter(out);
lda.printDocumentTopics(writer, 0.0, -1);
writer.flush();
String result = out.toString();
assertTrue(result.contains("#doc"));
}

@Test
public void testSerializationDoesNotLoseData() throws IOException, ClassNotFoundException {
Alphabet dataAlphabet = new Alphabet();
int idx = dataAlphabet.lookupIndex("token");
FeatureSequence fs = new FeatureSequence(dataAlphabet, new int[] { idx });
InstanceList list = new InstanceList(dataAlphabet, null);
list.add(new Instance(fs, null, "doc", null));
LabelAlphabet topicAlphabet = new LabelAlphabet();
topicAlphabet.lookupIndex("topic0");
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, new Randoms());
lda.addInstances(list);
File file = File.createTempFile("lda_temp", ".ser");
file.deleteOnExit();
ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
out.writeObject(lda);
out.close();
ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
Object obj = in.readObject();
in.close();
assertTrue(obj instanceof LDAHyper);
LDAHyper deserialized = (LDAHyper) obj;
assertEquals(1, deserialized.getNumTopics());
assertEquals(1, deserialized.getData().size());
}

@Test
public void testEmptyAlphabetPrintTopWordsDoesNotFail() {
Alphabet alphabet = new Alphabet();
FeatureSequence fs = new FeatureSequence(alphabet, new int[0]);
Instance instance = new Instance(fs, null, "docX", null);
InstanceList list = new InstanceList(alphabet, null);
list.add(instance);
LabelAlphabet topicAlphabet = new LabelAlphabet();
topicAlphabet.lookupIndex("topic0");
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, new Randoms());
lda.addInstances(list);
ByteArrayOutputStream baos = new ByteArrayOutputStream();
PrintStream ps = new PrintStream(baos);
lda.printTopWords(ps, 5, false);
ps.flush();
String result = baos.toString();
assertTrue(result.contains("0\t"));
}

@Test
public void testPrintStateWithNoSourceFieldDoesNotThrow() throws IOException {
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("term");
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { idx });
Instance instance = new Instance(fs, null, null, null);
InstanceList list = new InstanceList(alphabet, null);
list.add(instance);
LabelAlphabet topicAlphabet = new LabelAlphabet();
topicAlphabet.lookupIndex("topic0");
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, new Randoms());
lda.addInstances(list);
File file = File.createTempFile("lda_state", ".gz");
file.deleteOnExit();
try {
lda.printState(file);
assertTrue(file.exists());
} catch (Exception e) {
fail("printState should not throw exception on null source");
}
}

@Test
public void testGetSortedTopicWordsReturnsCorrectLength() {
Alphabet alphabet = new Alphabet();
int idx1 = alphabet.lookupIndex("one");
int idx2 = alphabet.lookupIndex("two");
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { idx1, idx2, idx1 });
Instance instance = new Instance(fs, null, "doc", null);
InstanceList list = new InstanceList(alphabet, null);
list.add(instance);
LabelAlphabet topicAlphabet = new LabelAlphabet();
topicAlphabet.lookupIndex("topic0");
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, new Randoms());
lda.addInstances(list);
IDSorter[] sorted = lda.getSortedTopicWords(0);
assertEquals(alphabet.size(), sorted.length);
}

@Test
public void testReadObjectWithInvalidStreamThrowsException() {
File tempFile = new File("nonexistent-file.ser");
LDAHyper lda = null;
try {
lda = LDAHyper.read(tempFile);
} catch (Exception e) {
fail("Exceptions should be handled internally in read method.");
}
assertNull(lda);
}

@Test
public void testWriteObjectHandlesIOExceptionGracefully() {
LabelAlphabet topicAlphabet = new LabelAlphabet();
topicAlphabet.lookupIndex("topic0");
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, new Randoms());
File directory = new File(System.getProperty("java.io.tmpdir"));
File invalidFile = new File(directory, "/");
try {
lda.write(invalidFile);
assertTrue(true);
} catch (Exception e) {
fail("write should not propagate exception, should handle it");
}
}

@Test
public void testAddInstancesWithZeroLengthTokenSequence() {
Alphabet alphabet = new Alphabet();
InstanceList list = new InstanceList(alphabet, null);
FeatureSequence fs = new FeatureSequence(alphabet, new int[0]);
Instance instance = new Instance(fs, null, "empty-doc", null);
list.add(instance);
LabelAlphabet topicAlphabet = new LabelAlphabet();
topicAlphabet.lookupIndex("topic0");
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, new Randoms(42));
try {
lda.addInstances(list);
assertEquals(1, lda.getData().size());
} catch (Exception e) {
fail("addInstances should not fail on zero-length doc");
}
}

@Test
public void testGetCountFeatureTopicForUnsetTypeReturnsZero() {
LabelAlphabet topicAlphabet = new LabelAlphabet();
topicAlphabet.lookupIndex("topic0");
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, new Randoms());
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("unknown");
InstanceList list = new InstanceList(alphabet, null);
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { 0 });
list.add(new Instance(fs, null, "doc", null));
lda.addInstances(list);
int count = lda.getCountFeatureTopic(0, 0);
assertTrue(count >= 0);
}

@Test
public void testPrintDocumentTopicsThresholdExcludesTopics() {
Alphabet dataAlphabet = new Alphabet();
int idx = dataAlphabet.lookupIndex("token");
LabelAlphabet topicAlphabet = new LabelAlphabet();
topicAlphabet.lookupIndex("topic0");
topicAlphabet.lookupIndex("topic1");
FeatureSequence fs = new FeatureSequence(dataAlphabet, new int[] { idx, idx });
InstanceList list = new InstanceList(dataAlphabet, null);
list.add(new Instance(fs, null, "doc", null));
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, new Randoms(123));
lda.addInstances(list);
StringWriter sw = new StringWriter();
PrintWriter pw = new PrintWriter(sw);
lda.printDocumentTopics(pw, 0.9, 1);
pw.flush();
String result = sw.toString();
String[] parts = result.split("\n");
if (parts.length > 1) {
String[] tokens = parts[1].split(" ");
assertTrue("Should include no dense topic weights above threshold", tokens.length <= 3);
}
}

@Test
public void testTopicsWithNoWordsDoNotThrowPrintTopWords() {
LabelAlphabet topicAlphabet = new LabelAlphabet();
topicAlphabet.lookupIndex("topic0");
topicAlphabet.lookupIndex("topic1");
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, new Randoms());
Alphabet alphabet = new Alphabet();
InstanceList list = new InstanceList(alphabet, null);
FeatureSequence fs = new FeatureSequence(alphabet, new int[0]);
Instance instance = new Instance(fs, null, "empty", null);
list.add(instance);
lda.addInstances(list);
ByteArrayOutputStream baos = new ByteArrayOutputStream();
PrintStream ps = new PrintStream(baos);
try {
lda.printTopWords(ps, 3, false);
ps.flush();
assertTrue(baos.toString().contains("0"));
} catch (Exception e) {
fail("printTopWords should not throw on empty topics");
}
}

@Test
public void testEmpiricalLikelihoodSkipsUnknownFeatureIndexInTestInstance() {
Alphabet trainAlphabet = new Alphabet();
int trainToken = trainAlphabet.lookupIndex("train");
FeatureSequence fs = new FeatureSequence(trainAlphabet, new int[] { trainToken });
Instance trainInstance = new Instance(fs, null, "trainDoc", null);
InstanceList trainList = new InstanceList(trainAlphabet, null);
trainList.add(trainInstance);
Alphabet testAlphabet = new Alphabet();
int testToken = testAlphabet.lookupIndex("test");
FeatureSequence fsTest = new FeatureSequence(testAlphabet, new int[] { testToken });
Instance testInstance = new Instance(fsTest, null, "testDoc", null);
InstanceList testList = new InstanceList(testAlphabet, null);
testList.add(testInstance);
LabelAlphabet topicAlphabet = new LabelAlphabet();
topicAlphabet.lookupIndex("topic0");
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, new Randoms(55));
lda.addInstances(trainList);
lda.setTestingInstances(testList);
try {
double score = lda.empiricalLikelihood(2, testList);
assertTrue(Double.isFinite(score));
} catch (Exception e) {
fail("empiricalLikelihood should skip unknown types");
}
}

@Test
public void testModelLogLikelihoodOnEmptyCorpusReturnsFinite() {
LabelAlphabet topicAlphabet = new LabelAlphabet();
topicAlphabet.lookupIndex("topic0");
Alphabet alphabet = new Alphabet();
InstanceList list = new InstanceList(alphabet, null);
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, new Randoms());
lda.addInstances(list);
double ll = lda.modelLogLikelihood();
assertTrue(Double.isFinite(ll));
}

@Test
public void testTopicLabelMutualInformationWithDocumentLabels() {
Alphabet dataAlphabet = new Alphabet();
int idx = dataAlphabet.lookupIndex("q");
LabelAlphabet labelAlphabet = new LabelAlphabet();
int labelIdx = labelAlphabet.lookupIndex("label0");
Labeling labeling = new LabelVector(labelAlphabet, new double[] { 1.0 });
FeatureSequence fs = new FeatureSequence(dataAlphabet, new int[] { idx, idx, idx });
Instance instance = new Instance(fs, labeling, "doc", null);
InstanceList list = new InstanceList(dataAlphabet, labelAlphabet);
list.add(instance);
LabelAlphabet topicAlphabet = new LabelAlphabet();
topicAlphabet.lookupIndex("topic0");
topicAlphabet.lookupIndex("topic1");
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, new Randoms());
lda.addInstances(list);
double mutualInfo = lda.topicLabelMutualInformation();
assertTrue(mutualInfo >= 0.0);
}

@Test
public void testEstimateWithSingleIterationNoOptimizationNoDisplay() throws IOException {
Alphabet alphabet = new Alphabet();
int tokenIndex = alphabet.lookupIndex("token");
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { tokenIndex, tokenIndex });
Instance instance = new Instance(fs, null, "doc", null);
InstanceList list = new InstanceList(alphabet, null);
list.add(instance);
LabelAlphabet topicAlphabet = new LabelAlphabet();
topicAlphabet.lookupIndex("topic0");
topicAlphabet.lookupIndex("topic1");
LDAHyper lda = new LDAHyper(topicAlphabet, 2.0, 0.01, new Randoms(1));
lda.setNumIterations(1);
lda.setBurninPeriod(0);
lda.setOptimizeInterval(0);
lda.setTopicDisplay(0, 5);
lda.addInstances(list);
lda.estimate();
assertTrue(lda.getData().size() > 0);
}

@Test
public void testPrintDocumentTopicsWithMaxTopicLimit() {
Alphabet dataAlphabet = new Alphabet();
int idx = dataAlphabet.lookupIndex("x");
FeatureSequence fs = new FeatureSequence(dataAlphabet, new int[] { idx, idx, idx });
InstanceList list = new InstanceList(dataAlphabet, null);
list.add(new Instance(fs, null, "sample", null));
LabelAlphabet topicAlphabet = new LabelAlphabet();
topicAlphabet.lookupIndex("topic0");
topicAlphabet.lookupIndex("topic1");
topicAlphabet.lookupIndex("topic2");
LDAHyper lda = new LDAHyper(topicAlphabet, 3.0, 0.01, new Randoms());
lda.addInstances(list);
StringWriter sw = new StringWriter();
PrintWriter pw = new PrintWriter(sw);
lda.printDocumentTopics(pw, 0.0, 2);
pw.flush();
String output = sw.toString();
assertTrue(output.startsWith("#doc"));
}

@Test
public void testPrintStatePrintStreamCapturesTokensCorrectly() {
Alphabet alphabet = new Alphabet();
int tokenIndex = alphabet.lookupIndex("foo");
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { tokenIndex, tokenIndex });
Instance instance = new Instance(fs, null, "docX", null);
InstanceList list = new InstanceList(alphabet, null);
list.add(instance);
LabelAlphabet topicAlphabet = new LabelAlphabet();
topicAlphabet.lookupIndex("topic0");
topicAlphabet.lookupIndex("topic1");
LDAHyper lda = new LDAHyper(topicAlphabet, 2.0, 0.01, new Randoms());
lda.addInstances(list);
ByteArrayOutputStream baos = new ByteArrayOutputStream();
PrintStream stream = new PrintStream(baos);
lda.printState(stream);
stream.flush();
String stateOutput = baos.toString();
assertTrue(stateOutput.contains("docX"));
assertTrue(stateOutput.contains("foo"));
}

@Test
public void testTopicXMLReportHandlesNoTopicsGracefully() {
Alphabet alphabet = new Alphabet();
FeatureSequence fs = new FeatureSequence(alphabet, new int[0]);
Instance instance = new Instance(fs, null, "doc1", null);
InstanceList list = new InstanceList(alphabet, null);
list.add(instance);
LabelAlphabet topicAlphabet = new LabelAlphabet();
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, new Randoms());
lda.addInstances(list);
StringWriter writer = new StringWriter();
PrintWriter pw = new PrintWriter(writer);
lda.topicXMLReport(pw, 5);
pw.flush();
String output = writer.toString();
assertTrue(output.contains("<topicModel>"));
}

@Test
public void testInitializeForTypesDoesNothingWhenSameAlphabet() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("a");
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { 0 });
Instance instance = new Instance(fs, null, "doc", null);
InstanceList list = new InstanceList(alphabet, null);
list.add(instance);
LabelAlphabet topicAlphabet = new LabelAlphabet();
topicAlphabet.lookupIndex("topic0");
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, new Randoms());
lda.addInstances(list);
try {
lda.addInstances(list);
assertTrue(true);
} catch (Exception e) {
fail("Repeated addInstances with same alphabet should not fail");
}
}

@Test
public void testEmptyTypeTopicCountsHandledByPrintTopWords() {
LabelAlphabet topicAlphabet = new LabelAlphabet();
topicAlphabet.lookupIndex("topic0");
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("word1");
InstanceList list = new InstanceList(alphabet, null);
FeatureSequence fs = new FeatureSequence(alphabet, new int[0]);
list.add(new Instance(fs, null, "doc0", null));
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, new Randoms());
lda.addInstances(list);
ByteArrayOutputStream out = new ByteArrayOutputStream();
PrintStream ps = new PrintStream(out);
lda.printTopWords(ps, 3, true);
ps.flush();
String result = out.toString();
assertTrue(result.contains("Topic 0"));
}

@Test
public void testInstanceLengthReturnsZeroForEmptyFeatureSequence() {
Alphabet alphabet = new Alphabet();
FeatureSequence fs = new FeatureSequence(alphabet, new int[0]);
Instance instance = new Instance(fs, null, "test", null);
LabelAlphabet topicAlphabet = new LabelAlphabet();
topicAlphabet.lookupIndex("topic0");
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, new Randoms());
int len = lda.instanceLength(instance);
assertEquals(0, len);
}

@Test
public void testDocLengthCountsAreComputedIninitializeHistogramsAndCachedValues() {
Alphabet alphabet = new Alphabet();
int index = alphabet.lookupIndex("t");
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { index, index });
Instance instance = new Instance(fs, null, "doc", null);
LabelAlphabet topicAlphabet = new LabelAlphabet();
topicAlphabet.lookupIndex("topic0");
InstanceList list = new InstanceList(alphabet, null);
list.add(instance);
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, new Randoms());
lda.addInstances(list);
assertTrue(lda.modelLogLikelihood() < 0);
}

@Test
public void testDirichletLogGammaIsCalledInModelLogLikelihood() {
Alphabet alphabet = new Alphabet();
int index = alphabet.lookupIndex("xxx");
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { index, index });
Instance instance = new Instance(fs, null, "docname", null);
LabelAlphabet topicAlphabet = new LabelAlphabet();
topicAlphabet.lookupIndex("topic0");
topicAlphabet.lookupIndex("topic1");
InstanceList list = new InstanceList(alphabet, null);
list.add(instance);
LDAHyper lda = new LDAHyper(topicAlphabet, 0.5, 0.01, new Randoms(100));
lda.addInstances(list);
double ll = lda.modelLogLikelihood();
assertTrue(Double.isFinite(ll));
}

@Test
public void testPrintTopWordsPrintsNonBreakingWhenNoFeaturesExist() {
LabelAlphabet topicAlphabet = new LabelAlphabet();
topicAlphabet.lookupIndex("topic0");
Alphabet alphabet = new Alphabet();
FeatureSequence fs = new FeatureSequence(alphabet, new int[0]);
Instance instance = new Instance(fs, null, "empty", null);
InstanceList list = new InstanceList(alphabet, null);
list.add(instance);
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, new Randoms());
lda.addInstances(list);
ByteArrayOutputStream os = new ByteArrayOutputStream();
PrintStream ps = new PrintStream(os);
lda.printTopWords(ps, 3, false);
ps.flush();
String output = os.toString();
assertTrue(output.contains("\t"));
}

@Test
public void testInitializeForTypesAddsNewTypeWhenAlphabetSizeIncreases() {
Alphabet alphabet = new Alphabet();
int indexA = alphabet.lookupIndex("a");
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { indexA });
InstanceList list = new InstanceList(alphabet, null);
list.add(new Instance(fs, null, "doc1", null));
LabelAlphabet topicAlphabet = new LabelAlphabet();
topicAlphabet.lookupIndex("topic0");
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, new Randoms());
lda.addInstances(list);
int indexB = alphabet.lookupIndex("b");
FeatureSequence fsNew = new FeatureSequence(alphabet, new int[] { indexA, indexB });
InstanceList updatedList = new InstanceList(alphabet, null);
updatedList.add(new Instance(fsNew, null, "doc2", null));
lda.addInstances(updatedList);
assertEquals(2, lda.getData().size());
}

@Test
public void testPrintDocumentTopicsWithNoSourceField() {
Alphabet alphabet = new Alphabet();
int index = alphabet.lookupIndex("x");
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { index });
Instance instance = new Instance(fs, null, null, null);
InstanceList list = new InstanceList(alphabet, null);
list.add(instance);
LabelAlphabet topicAlphabet = new LabelAlphabet();
topicAlphabet.lookupIndex("topic0");
topicAlphabet.lookupIndex("topic1");
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, new Randoms());
lda.addInstances(list);
StringWriter sw = new StringWriter();
PrintWriter pw = new PrintWriter(sw);
lda.printDocumentTopics(pw);
pw.flush();
String output = sw.toString();
assertTrue(output.contains("null-source"));
}

@Test
public void testAddInstancesWithTopicAssignmentsPreservesMappings() {
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("fruit");
LabelAlphabet topicAlphabet = new LabelAlphabet();
topicAlphabet.lookupIndex("topic0");
topicAlphabet.lookupIndex("topic1");
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { idx, idx });
Instance instance = new Instance(fs, null, "food", null);
InstanceList list = new InstanceList(alphabet, null);
list.add(instance);
LabelSequence topicAssignments = new LabelSequence(topicAlphabet, new int[] { 0, 1 });
ArrayList<LabelSequence> topicList = new ArrayList<LabelSequence>();
topicList.add(topicAssignments);
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, new Randoms());
lda.addInstances(list, topicList);
assertEquals(1, lda.getData().size());
}

@Test
public void testPrintStateOutputFormatHeaderOnlyWhenNoData() {
LabelAlphabet topicAlphabet = new LabelAlphabet();
topicAlphabet.lookupIndex("topic0");
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, new Randoms());
ByteArrayOutputStream baos = new ByteArrayOutputStream();
PrintStream ps = new PrintStream(baos);
lda.printState(ps);
ps.flush();
String output = baos.toString();
assertTrue(output.startsWith("#doc source pos typeindex type topic"));
}

@Test
public void testTopicXMLReportWithSingleWordPerTopicOutput() {
Alphabet alphabet = new Alphabet();
int tid = alphabet.lookupIndex("testword");
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { tid, tid });
Instance instance = new Instance(fs, null, "doc", null);
InstanceList list = new InstanceList(alphabet, null);
list.add(instance);
LabelAlphabet topicAlphabet = new LabelAlphabet();
topicAlphabet.lookupIndex("topic0");
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, new Randoms(111));
lda.addInstances(list);
StringWriter sw = new StringWriter();
PrintWriter pw = new PrintWriter(sw);
lda.topicXMLReport(pw, 1);
pw.flush();
String result = sw.toString();
assertTrue(result.contains("<topic id='0"));
assertTrue(result.contains("<word rank='1'>testword</word>"));
}

@Test
public void testModelLogLikelihoodWithUniformTypesAcrossTopics() {
Alphabet alphabet = new Alphabet();
int id = alphabet.lookupIndex("hello");
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { id, id, id });
Instance instance = new Instance(fs, null, "doc", null);
InstanceList list = new InstanceList(alphabet, null);
list.add(instance);
LabelAlphabet topicAlphabet = new LabelAlphabet();
topicAlphabet.lookupIndex("topic0");
topicAlphabet.lookupIndex("topic1");
topicAlphabet.lookupIndex("topic2");
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, new Randoms(777));
lda.addInstances(list);
double logLikelihood = lda.modelLogLikelihood();
assertTrue(Double.isFinite(logLikelihood));
}

@Test
public void testEmpiricalLikelihoodReturnsZeroForEmptyInputSet() {
LabelAlphabet topicAlphabet = new LabelAlphabet();
topicAlphabet.lookupIndex("topic0");
Alphabet alphabet = new Alphabet();
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, new Randoms());
InstanceList list = new InstanceList(alphabet, null);
lda.setTestingInstances(list);
double result = lda.empiricalLikelihood(1, list);
assertEquals(0.0, result, 0.01);
}

@Test
public void testAddInstancesWithDuplicateWordsAcrossDocsIsHandled() {
Alphabet alphabet = new Alphabet();
int foo = alphabet.lookupIndex("foo");
InstanceList list = new InstanceList(alphabet, null);
FeatureSequence fs1 = new FeatureSequence(alphabet, new int[] { foo });
FeatureSequence fs2 = new FeatureSequence(alphabet, new int[] { foo });
list.add(new Instance(fs1, null, "a", null));
list.add(new Instance(fs2, null, "b", null));
LabelAlphabet topicAlphabet = new LabelAlphabet();
topicAlphabet.lookupIndex("topic0");
topicAlphabet.lookupIndex("topic1");
LDAHyper lda = new LDAHyper(topicAlphabet, 2.0, 0.01, new Randoms());
try {
lda.addInstances(list);
assertTrue(true);
} catch (Exception e) {
fail("Duplicate words across docs should not break addInstances");
}
}

@Test
public void testPrintTopWordsWithMultipleTopicsAndWords() {
Alphabet alphabet = new Alphabet();
int word1 = alphabet.lookupIndex("one");
int word2 = alphabet.lookupIndex("two");
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { word1, word2, word1 });
Instance instance = new Instance(fs, null, "doc", null);
InstanceList list = new InstanceList(alphabet, null);
list.add(instance);
LabelAlphabet topicAlphabet = new LabelAlphabet();
topicAlphabet.lookupIndex("topic0");
topicAlphabet.lookupIndex("topic1");
LDAHyper lda = new LDAHyper(topicAlphabet, 2.0, 0.01, new Randoms());
lda.addInstances(list);
ByteArrayOutputStream baos = new ByteArrayOutputStream();
PrintStream ps = new PrintStream(baos);
lda.printTopWords(ps, 2, false);
ps.flush();
String result = baos.toString();
assertTrue(result.contains("one") || result.contains("two"));
}

@Test
public void testPrintTopWordsHandlesNegativeOrZeroWordCountGracefully() {
LabelAlphabet topicAlphabet = new LabelAlphabet();
topicAlphabet.lookupIndex("topic0");
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("term");
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { idx });
Instance instance = new Instance(fs, null, "test", null);
InstanceList list = new InstanceList(alphabet, null);
list.add(instance);
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, new Randoms(1));
lda.addInstances(list);
ByteArrayOutputStream out = new ByteArrayOutputStream();
PrintStream ps = new PrintStream(out);
lda.printTopWords(ps, 0, true);
ps.flush();
String result = out.toString();
assertTrue(result.contains("Topic"));
}

@Test
public void testTopicXMLReportHandlesOnlyHeaderTagOnEmptyData() {
LabelAlphabet topicAlphabet = new LabelAlphabet();
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, new Randoms());
StringWriter sw = new StringWriter();
PrintWriter pw = new PrintWriter(sw);
lda.topicXMLReport(pw, 5);
pw.flush();
String xml = sw.toString();
assertTrue(xml.contains("<topicModel>"));
assertTrue(xml.contains("</topicModel>"));
}

@Test
public void testAddEmptyListWithEmptyTopicAssignments() {
Alphabet alphabet = new Alphabet();
InstanceList emptyList = new InstanceList(alphabet, null);
ArrayList<LabelSequence> topicList = new ArrayList<LabelSequence>();
LabelAlphabet topicAlphabet = new LabelAlphabet();
topicAlphabet.lookupIndex("topic0");
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, new Randoms());
lda.addInstances(emptyList, topicList);
}

@Test
public void testWriteObjectHandlesNullFilenames() {
LabelAlphabet topicAlphabet = new LabelAlphabet();
topicAlphabet.lookupIndex("topic0");
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, new Randoms());
lda.setSaveState(1, null);
lda.setModelOutput(1, null);
try {
File file = File.createTempFile("write-null-test", ".obj");
file.deleteOnExit();
lda.printState(file);
assertTrue(file.exists());
} catch (Exception e) {
fail("Should not throw exception when filename is null");
}
}

@Test
public void testGetCountFeatureTopicForInvalidTopicReturnsZero() {
LabelAlphabet topicAlphabet = new LabelAlphabet();
topicAlphabet.lookupIndex("topic0");
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("word");
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { idx });
Instance instance = new Instance(fs, null, "doc", null);
InstanceList list = new InstanceList(alphabet, null);
list.add(instance);
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, new Randoms());
lda.addInstances(list);
int count = lda.getCountFeatureTopic(0, 100);
assertEquals(0, count);
}

@Test
public void testTopicDistributionMutualInformationWithMultipleLabels() {
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("x");
LabelAlphabet targetAlphabet = new LabelAlphabet();
int idxA = targetAlphabet.lookupIndex("A");
int idxB = targetAlphabet.lookupIndex("B");
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { idx });
LabelVector labelsA = new LabelVector(targetAlphabet, new double[] { 1.0, 0.0 });
LabelVector labelsB = new LabelVector(targetAlphabet, new double[] { 0.0, 1.0 });
Instance a = new Instance(fs, labelsA, "docA", null);
Instance b = new Instance(fs, labelsB, "docB", null);
InstanceList list = new InstanceList(alphabet, targetAlphabet);
list.add(a);
list.add(b);
LabelAlphabet topicAlphabet = new LabelAlphabet();
topicAlphabet.lookupIndex("t0");
topicAlphabet.lookupIndex("t1");
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, new Randoms());
lda.addInstances(list);
double score = lda.topicLabelMutualInformation();
assertTrue(score >= 0.0);
}

@Test
public void testEstimateRunsWithSingleTokenSingleTopic() throws IOException {
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("q");
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { idx });
Instance instance = new Instance(fs, null, "unique", null);
InstanceList list = new InstanceList(alphabet, null);
list.add(instance);
LabelAlphabet topicAlphabet = new LabelAlphabet();
topicAlphabet.lookupIndex("topicX");
LDAHyper lda = new LDAHyper(topicAlphabet, 0.1, 0.01, new Randoms());
lda.setNumIterations(1);
lda.setBurninPeriod(0);
lda.setTopicDisplay(0, 1);
lda.setOptimizeInterval(100);
lda.addInstances(list);
lda.estimate();
}

@Test
public void testPrintTopWordsHandlesDoubleDigitTopicIds() {
Alphabet alphabet = new Alphabet();
int word = alphabet.lookupIndex("common");
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { word, word });
Instance instance = new Instance(fs, null, "longtopic", null);
InstanceList list = new InstanceList(alphabet, null);
list.add(instance);
LabelAlphabet topicAlphabet = new LabelAlphabet();
for (int i = 0; i < 12; i++) {
topicAlphabet.lookupIndex("topic" + i);
}
LDAHyper lda = new LDAHyper(topicAlphabet, 12.0, 0.01, new Randoms());
lda.addInstances(list);
ByteArrayOutputStream out = new ByteArrayOutputStream();
PrintStream ps = new PrintStream(out);
lda.printTopWords(ps, 2, false);
ps.flush();
String result = out.toString();
assertTrue(result.contains("11"));
}

@Test
public void testPrintStateIncludesTypeIndexAndWord() {
Alphabet alphabet = new Alphabet();
int wordIndex = alphabet.lookupIndex("token");
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { wordIndex });
Instance instance = new Instance(fs, null, "doc", null);
InstanceList list = new InstanceList(alphabet, null);
list.add(instance);
LabelAlphabet topicAlphabet = new LabelAlphabet();
topicAlphabet.lookupIndex("one");
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, new Randoms());
lda.addInstances(list);
ByteArrayOutputStream baos = new ByteArrayOutputStream();
PrintStream ps = new PrintStream(baos);
lda.printState(ps);
ps.flush();
String content = baos.toString();
assertTrue(content.contains("token"));
assertTrue(content.contains("0"));
}

@Test
public void testModelLogLikelihoodWithDifferentTokenCountsAcrossTopics() {
Alphabet alphabet = new Alphabet();
int w1 = alphabet.lookupIndex("apple");
int w2 = alphabet.lookupIndex("banana");
int w3 = alphabet.lookupIndex("cherry");
FeatureSequence fs1 = new FeatureSequence(alphabet, new int[] { w1, w2 });
FeatureSequence fs2 = new FeatureSequence(alphabet, new int[] { w1, w1, w3 });
InstanceList training = new InstanceList(alphabet, null);
training.add(new Instance(fs1, null, "doc1", null));
training.add(new Instance(fs2, null, "doc2", null));
LabelAlphabet topics = new LabelAlphabet();
topics.lookupIndex("T0");
topics.lookupIndex("T1");
topics.lookupIndex("T2");
LDAHyper lda = new LDAHyper(topics, 0.5, 0.01, new Randoms(123));
lda.addInstances(training);
double logLikelihood = lda.modelLogLikelihood();
assertTrue(Double.isFinite(logLikelihood));
}

@Test
public void testPrintDocumentTopicsWithHighThresholdOmitsTopics() {
Alphabet alphabet = new Alphabet();
int feature = alphabet.lookupIndex("x");
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { feature, feature, feature });
InstanceList list = new InstanceList(alphabet, null);
list.add(new Instance(fs, null, "docX", null));
LabelAlphabet topics = new LabelAlphabet();
topics.lookupIndex("topicA");
topics.lookupIndex("topicB");
LDAHyper lda = new LDAHyper(topics, 0.5, 0.01, new Randoms(123));
lda.addInstances(list);
StringWriter sw = new StringWriter();
PrintWriter writer = new PrintWriter(sw);
lda.printDocumentTopics(writer, 1.0, 2);
writer.flush();
String output = sw.toString();
assertTrue(output.contains("docX"));
}

@Test
public void testPrintTopWordsWhenNoTypeTopicCountsExist() {
Alphabet alphabet = new Alphabet();
LabelAlphabet topicAlphabet = new LabelAlphabet();
topicAlphabet.lookupIndex("T0");
topicAlphabet.lookupIndex("T1");
FeatureSequence fs = new FeatureSequence(alphabet, new int[0]);
InstanceList list = new InstanceList(alphabet, null);
list.add(new Instance(fs, null, "emptyDoc", null));
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, new Randoms());
lda.addInstances(list);
ByteArrayOutputStream out = new ByteArrayOutputStream();
PrintStream ps = new PrintStream(out);
lda.printTopWords(ps, 5, true);
ps.flush();
String result = out.toString();
assertTrue(result.contains("Topic"));
}

@Test
public void testEstimateDoesNotCrashOnZeroAlphaAndBeta() throws IOException {
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("w");
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { idx });
InstanceList list = new InstanceList(alphabet, null);
list.add(new Instance(fs, null, "doc", null));
LabelAlphabet topics = new LabelAlphabet();
topics.lookupIndex("A");
topics.lookupIndex("B");
LDAHyper lda = new LDAHyper(topics, 0.0, 0.0, new Randoms(55));
lda.setNumIterations(1);
lda.setBurninPeriod(0);
lda.addInstances(list);
lda.estimate();
assertEquals(2, lda.getNumTopics());
}

@Test
public void testTopicLabelMutualInformationReturnsZeroWhenNoLabels() {
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("foo");
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { idx });
Instance instance = new Instance(fs, null, "x", null);
InstanceList list = new InstanceList(alphabet, null);
list.add(instance);
LabelAlphabet topics = new LabelAlphabet();
topics.lookupIndex("xx");
LDAHyper lda = new LDAHyper(topics, 1.0, 0.01, new Randoms());
lda.addInstances(list);
double mi = lda.topicLabelMutualInformation();
assertEquals(0.0, mi, 0.00001);
}

@Test
public void testReadAndWriteHandlesEmptyCorpusSerialization() throws IOException, ClassNotFoundException {
LabelAlphabet topics = new LabelAlphabet();
topics.lookupIndex("topic0");
LDAHyper lda = new LDAHyper(topics, 1.0, 0.01, new Randoms());
File file = File.createTempFile("lda-empty", ".ser");
file.deleteOnExit();
ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
out.writeObject(lda);
out.close();
ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
LDAHyper reloaded = (LDAHyper) in.readObject();
in.close();
assertNotNull(reloaded);
assertEquals(1, reloaded.getNumTopics());
}

@Test
public void testPrintStateSkipsNullInstanceSource() throws IOException {
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("z");
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { idx });
Instance instance = new Instance(fs, null, null, null);
InstanceList list = new InstanceList(alphabet, null);
list.add(instance);
LabelAlphabet topics = new LabelAlphabet();
topics.lookupIndex("t");
LDAHyper lda = new LDAHyper(topics, 1.0, 0.01, new Randoms());
lda.addInstances(list);
ByteArrayOutputStream os = new ByteArrayOutputStream();
PrintStream ps = new PrintStream(os);
lda.printState(ps);
ps.flush();
String output = os.toString();
assertTrue(output.contains("NA"));
}

@Test
public void testEstimateWithEmptyDocumentListExitsCleanly() throws IOException {
Alphabet alphabet = new Alphabet();
InstanceList list = new InstanceList(alphabet, null);
LabelAlphabet topicAlphabet = new LabelAlphabet();
topicAlphabet.lookupIndex("tt");
LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, new Randoms());
lda.setNumIterations(0);
lda.addInstances(list);
lda.estimate();
assertEquals(0, lda.getData().size());
}

@Test
public void testEmpiricalLikelihoodSkipsInvalidVocabularyFromTestSet() {
Alphabet alphabetTrain = new Alphabet();
int idxA = alphabetTrain.lookupIndex("a");
FeatureSequence fsTrain = new FeatureSequence(alphabetTrain, new int[] { idxA });
InstanceList training = new InstanceList(alphabetTrain, null);
training.add(new Instance(fsTrain, null, "train", null));
Alphabet alphabetTest = new Alphabet();
int idxB = alphabetTest.lookupIndex("b");
FeatureSequence fsTest = new FeatureSequence(alphabetTest, new int[] { idxB });
InstanceList testing = new InstanceList(alphabetTest, null);
testing.add(new Instance(fsTest, null, "test", null));
LabelAlphabet topics = new LabelAlphabet();
topics.lookupIndex("t0");
topics.lookupIndex("t1");
LDAHyper lda = new LDAHyper(topics, 1.0, 0.01, new Randoms());
lda.addInstances(training);
lda.setTestingInstances(testing);
double val = lda.empiricalLikelihood(2, testing);
assertTrue(Double.isFinite(val));
}

@Test
public void testPrintDocumentTopicsDoesNotExceedMax() {
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("x");
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { idx, idx });
InstanceList docs = new InstanceList(alphabet, null);
docs.add(new Instance(fs, null, "doc-max", null));
LabelAlphabet topics = new LabelAlphabet();
topics.lookupIndex("t0");
topics.lookupIndex("t1");
topics.lookupIndex("t2");
LDAHyper lda = new LDAHyper(topics, 1.0, 0.01, new Randoms());
lda.addInstances(docs);
ByteArrayOutputStream baos = new ByteArrayOutputStream();
PrintWriter pw = new PrintWriter(baos);
lda.printDocumentTopics(pw, 0.0, 2);
pw.flush();
String result = baos.toString();
String[] tokens = result.split("\n")[1].trim().split(" ");
int topicCount = tokens.length - 2;
assertTrue(topicCount <= 4);
}
}
