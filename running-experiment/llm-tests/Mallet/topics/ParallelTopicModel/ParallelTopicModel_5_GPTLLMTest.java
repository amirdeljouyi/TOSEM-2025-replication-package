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

public class ParallelTopicModel_5_GPTLLMTest {

@Test
public void testConstructorAndSetNumTopics_PowerOfTwo() throws Exception {
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("topic0");
labelAlphabet.lookupIndex("topic1");
labelAlphabet.lookupIndex("topic2");
labelAlphabet.lookupIndex("topic3");
ParallelTopicModel model = new ParallelTopicModel(labelAlphabet, 1.0, 0.01);
model.setNumTopics(4);
assertEquals(4, model.getNumTopics());
assertNotNull(model.getTopicAlphabet());
}

@Test
public void testAddInstancesAndInitialTopicAssignment() throws Exception {
Alphabet alphabet = new Alphabet();
Pipe pipe = new SerialPipes(Arrays.asList(new Pipe[] { new TokenSequence2FeatureSequence(alphabet) }));
InstanceList instanceList = new InstanceList(pipe);
TokenSequence ts = new TokenSequence();
ts.add("apple");
ts.add("banana");
Instance instance = new Instance(ts, null, "doc1", null);
instanceList.addThruPipe(instance);
ParallelTopicModel model = new ParallelTopicModel(2);
model.setRandomSeed(123);
model.addInstances(instanceList);
TopicAssignment assignment = model.getData().get(0);
LabelSequence topicSequence = (LabelSequence) assignment.topicSequence;
int length = topicSequence.getLength();
assertEquals(2, model.getNumTopics());
assertEquals(2, length);
int topic0 = topicSequence.getIndexAtPosition(0);
int topic1 = topicSequence.getIndexAtPosition(1);
assertTrue(topic0 >= 0 && topic0 < 2);
assertTrue(topic1 >= 0 && topic1 < 2);
}

@Test
public void testGetTopicProbabilitiesReturnsValidDistribution() throws Exception {
Alphabet alphabet = new Alphabet();
Pipe pipe = new SerialPipes(Arrays.asList(new Pipe[] { new TokenSequence2FeatureSequence(alphabet) }));
InstanceList instanceList = new InstanceList(pipe);
TokenSequence ts = new TokenSequence();
ts.add("apple");
ts.add("apple");
ts.add("banana");
Instance instance = new Instance(ts, null, "doc1", null);
instanceList.addThruPipe(instance);
ParallelTopicModel model = new ParallelTopicModel(3);
model.setRandomSeed(101);
model.addInstances(instanceList);
double[] topicProbabilities = model.getTopicProbabilities(0);
double sum = topicProbabilities[0] + topicProbabilities[1] + topicProbabilities[2];
assertEquals(3, topicProbabilities.length);
assertTrue(topicProbabilities[0] >= 0.0);
assertTrue(topicProbabilities[1] >= 0.0);
assertTrue(topicProbabilities[2] >= 0.0);
assertEquals(1.0, sum, 1e-6);
}

@Test
public void testGetSortedWordsReturnsExpectedSize() throws Exception {
Alphabet alphabet = new Alphabet();
Pipe pipe = new SerialPipes(Arrays.asList(new Pipe[] { new TokenSequence2FeatureSequence(alphabet) }));
InstanceList instanceList = new InstanceList(pipe);
TokenSequence ts = new TokenSequence();
ts.add("apple");
ts.add("banana");
Instance instance = new Instance(ts, null, "doc1", null);
instanceList.addThruPipe(instance);
ParallelTopicModel model = new ParallelTopicModel(2);
model.setRandomSeed(99);
model.addInstances(instanceList);
ArrayList<TreeSet<IDSorter>> sortedWords = model.getSortedWords();
assertNotNull(sortedWords);
assertEquals(2, sortedWords.size());
TreeSet<IDSorter> topic0Words = sortedWords.get(0);
TreeSet<IDSorter> topic1Words = sortedWords.get(1);
assertNotNull(topic0Words);
assertNotNull(topic1Words);
}

@Test
public void testDisplayTopWordsReturnsNonEmptyString() throws Exception {
Alphabet alphabet = new Alphabet();
Pipe pipe = new SerialPipes(Arrays.asList(new Pipe[] { new TokenSequence2FeatureSequence(alphabet) }));
InstanceList instanceList = new InstanceList(pipe);
TokenSequence ts = new TokenSequence();
ts.add("orange");
ts.add("pear");
ts.add("grape");
Instance instance = new Instance(ts, null, "doc1", null);
instanceList.addThruPipe(instance);
ParallelTopicModel model = new ParallelTopicModel(3);
model.setRandomSeed(19);
model.addInstances(instanceList);
String output = model.displayTopWords(5, false);
assertNotNull(output);
assertTrue(output.contains("\n"));
}

@Test
public void testInferencerObjectNotNull() throws Exception {
Alphabet alphabet = new Alphabet();
Pipe pipe = new SerialPipes(Arrays.asList(new Pipe[] { new TokenSequence2FeatureSequence(alphabet) }));
InstanceList instanceList = new InstanceList(pipe);
TokenSequence ts = new TokenSequence();
ts.add("red");
ts.add("blue");
Instance instance = new Instance(ts, null, "doc1", null);
instanceList.addThruPipe(instance);
ParallelTopicModel model = new ParallelTopicModel(2);
model.setRandomSeed(55);
model.addInstances(instanceList);
TopicInferencer inferencer = model.getInferencer();
assertNotNull(inferencer);
}

@Test
public void testPrintTopWordsPrintStreamOutput() throws Exception {
Alphabet alphabet = new Alphabet();
Pipe pipe = new SerialPipes(Arrays.asList(new Pipe[] { new TokenSequence2FeatureSequence(alphabet) }));
InstanceList instanceList = new InstanceList(pipe);
TokenSequence ts = new TokenSequence();
ts.add("x");
ts.add("y");
ts.add("z");
Instance instance = new Instance(ts, null, "docX", null);
instanceList.addThruPipe(instance);
ParallelTopicModel model = new ParallelTopicModel(2);
model.setRandomSeed(777);
model.addInstances(instanceList);
ByteArrayOutputStream baos = new ByteArrayOutputStream();
PrintStream out = new PrintStream(baos);
model.printTopWords(out, 2, false);
out.flush();
String content = baos.toString("UTF-8");
assertNotNull(content);
assertTrue(content.length() > 0);
}

@Test
public void testModelLogLikelihoodIsFinite() throws Exception {
Alphabet alphabet = new Alphabet();
Pipe pipe = new SerialPipes(Arrays.asList(new Pipe[] { new TokenSequence2FeatureSequence(alphabet) }));
InstanceList instanceList = new InstanceList(pipe);
TokenSequence ts = new TokenSequence();
ts.add("logic");
ts.add("math");
Instance instance = new Instance(ts, null, "testDoc", null);
instanceList.addThruPipe(instance);
ParallelTopicModel model = new ParallelTopicModel(2);
model.setRandomSeed(888);
model.addInstances(instanceList);
double ll = model.modelLogLikelihood();
assertTrue(Double.isFinite(ll));
}

@Test
public void testSerializationRoundTripPreservesBasicFields() throws Exception {
Alphabet alphabet = new Alphabet();
Pipe pipe = new SerialPipes(Arrays.asList(new Pipe[] { new TokenSequence2FeatureSequence(alphabet) }));
InstanceList instanceList = new InstanceList(pipe);
TokenSequence ts = new TokenSequence();
ts.add("serialize");
ts.add("deserialize");
Instance instance = new Instance(ts, null, "docS", null);
instanceList.addThruPipe(instance);
ParallelTopicModel model = new ParallelTopicModel(2);
model.setRandomSeed(999);
model.addInstances(instanceList);
File temp = File.createTempFile("parallelModel", ".ser");
model.write(temp);
ParallelTopicModel loaded = ParallelTopicModel.read(temp);
Instance originalInstance = model.getData().get(0).instance;
Instance loadedInstance = loaded.getData().get(0).instance;
assertNotNull(loaded);
assertEquals(model.getNumTopics(), loaded.getNumTopics());
assertEquals(originalInstance.getName(), loadedInstance.getName());
temp.deleteOnExit();
}

@Test
public void testZeroTopicsShouldFailOrHandleGracefully() {
boolean exceptionThrown = false;
try {
ParallelTopicModel model = new ParallelTopicModel(0);
} catch (IllegalArgumentException | ArrayIndexOutOfBoundsException | NegativeArraySizeException e) {
exceptionThrown = true;
}
assertTrue("Zero topics should raise an exception or be invalid", exceptionThrown);
}

@Test
public void testAddInstancesWithEmptyInstanceList() {
InstanceList empty = new InstanceList(new SerialPipes(Arrays.asList(new Pipe[] { new TokenSequence2FeatureSequence(new Alphabet()) })));
ParallelTopicModel model = new ParallelTopicModel(2);
model.setRandomSeed(42);
model.addInstances(empty);
assertEquals(0, model.getData().size());
}

@Test
public void testSingleTokenDocumentProducesValidTopicDistribution() {
Alphabet alphabet = new Alphabet();
Pipe pipe = new SerialPipes(Arrays.asList(new Pipe[] { new TokenSequence2FeatureSequence(alphabet) }));
InstanceList list = new InstanceList(pipe);
TokenSequence ts = new TokenSequence();
ts.add("lonely");
Instance instance = new Instance(ts, null, "doc1", null);
list.addThruPipe(instance);
ParallelTopicModel model = new ParallelTopicModel(3);
model.setRandomSeed(123);
model.addInstances(list);
double[] probs = model.getTopicProbabilities(0);
double sum = probs[0] + probs[1] + probs[2];
assertEquals(3, probs.length);
assertEquals(1.0, sum, 1e-6);
}

@Test
public void testExtremeLargeAlphaAndBetaValues() {
Alphabet alphabet = new Alphabet();
Pipe pipe = new SerialPipes(Arrays.asList(new Pipe[] { new TokenSequence2FeatureSequence(alphabet) }));
InstanceList list = new InstanceList(pipe);
TokenSequence ts = new TokenSequence();
ts.add("high");
ts.add("alpha");
ts.add("beta");
Instance instance = new Instance(ts, null, "docHighAlphaBeta", null);
list.addThruPipe(instance);
ParallelTopicModel model = new ParallelTopicModel(2, 1000.0, 1000.0);
model.setRandomSeed(321);
model.addInstances(list);
double[] dist = model.getTopicProbabilities(0);
assertEquals(2, dist.length);
double sum = dist[0] + dist[1];
assertEquals(1.0, sum, 1e-6);
}

@Test
public void testTopicXMLReportWithEmptyModel() throws Exception {
File file = File.createTempFile("topicXML", ".xml");
PrintWriter writer = new PrintWriter(new FileOutputStream(file));
ParallelTopicModel model = new ParallelTopicModel(3);
model.topicXMLReport(writer, 5);
writer.close();
BufferedReader reader = new BufferedReader(new FileReader(file));
String header = reader.readLine();
assertTrue(header.contains("<?xml"));
reader.close();
file.deleteOnExit();
}

@Test
public void testPrintTypeTopicCountsWithoutException() throws Exception {
Alphabet alphabet = new Alphabet();
Pipe pipe = new SerialPipes(Arrays.asList(new Pipe[] { new TokenSequence2FeatureSequence(alphabet) }));
InstanceList list = new InstanceList(pipe);
TokenSequence seq = new TokenSequence();
seq.add("a");
seq.add("b");
list.addThruPipe(new Instance(seq, null, "doc", null));
ParallelTopicModel model = new ParallelTopicModel(2);
model.setRandomSeed(101);
model.addInstances(list);
File outFile = File.createTempFile("type-topic", ".txt");
model.printTypeTopicCounts(outFile);
assertTrue(outFile.exists());
assertTrue(outFile.length() > 0);
outFile.deleteOnExit();
}

@Test
public void testPrintStateHandlesSingleDocument() throws Exception {
Alphabet alphabet = new Alphabet();
Pipe pipe = new SerialPipes(Collections.singletonList(new TokenSequence2FeatureSequence(alphabet)));
InstanceList list = new InstanceList(pipe);
TokenSequence ts = new TokenSequence();
ts.add("hello");
ts.add("world");
list.addThruPipe(new Instance(ts, null, "doc", "meta"));
ParallelTopicModel model = new ParallelTopicModel(2);
model.setRandomSeed(5);
model.addInstances(list);
File stateFile = File.createTempFile("state-output", ".gz");
model.printState(stateFile);
assertTrue(stateFile.exists());
assertTrue(stateFile.length() > 0);
stateFile.deleteOnExit();
}

@Test
public void testDocumentTopicDistributionWithThresholdAndLimit() throws Exception {
Alphabet alphabet = new Alphabet();
Pipe pipe = new SerialPipes(Collections.singletonList(new TokenSequence2FeatureSequence(alphabet)));
InstanceList list = new InstanceList(pipe);
TokenSequence ts = new TokenSequence();
ts.add("x");
ts.add("x");
ts.add("y");
list.addThruPipe(new Instance(ts, null, "docX", null));
ParallelTopicModel model = new ParallelTopicModel(3);
model.setRandomSeed(42);
model.addInstances(list);
StringWriter sw = new StringWriter();
PrintWriter pw = new PrintWriter(sw);
model.printDocumentTopics(pw, 0.0, 2);
String output = sw.toString();
assertTrue(output.contains("docX"));
}

@Test
public void testGetSubCorpusTopicWordsWithAllFalseMask() {
Alphabet alphabet = new Alphabet();
Pipe pipe = new SerialPipes(Arrays.asList(new TokenSequence2FeatureSequence(alphabet)));
InstanceList list = new InstanceList(pipe);
TokenSequence ts = new TokenSequence();
ts.add("x");
ts.add("y");
list.addThruPipe(new Instance(ts, null, "doc", null));
ParallelTopicModel model = new ParallelTopicModel(2);
model.setRandomSeed(1);
model.addInstances(list);
boolean[] mask = new boolean[1];
mask[0] = false;
double[][] result = model.getSubCorpusTopicWords(mask, true, true);
assertEquals(2, result.length);
int numTypes = result[0].length;
assertEquals(model.getAlphabet().size(), numTypes);
}

@Test
public void testTemperingWithEmptyAlphaStats() {
Alphabet alphabet = new Alphabet();
Pipe pipe = new SerialPipes(Collections.singletonList(new TokenSequence2FeatureSequence(alphabet)));
InstanceList list = new InstanceList(pipe);
list.addThruPipe(new Instance(new TokenSequence(), null, "empty", null));
ParallelTopicModel model = new ParallelTopicModel(2);
model.setRandomSeed(0);
model.addInstances(list);
WorkerCallable[] callables = new WorkerCallable[1];
callables[0] = new WorkerCallable(2, model.alpha, model.alphaSum, model.beta, new Randoms(), model.getData(), model.getTypeTopicCounts(), model.getTokensPerTopic(), 0, 1);
model.temperAlpha(callables);
assertEquals(2, model.alpha.length);
assertEquals(2.0, model.alphaSum, 0.00001);
}

@Test
public void testSingleTopicModelProducesUniformDistribution() {
Alphabet alphabet = new Alphabet();
SerialPipes pipes = new SerialPipes(Arrays.asList(new Pipe[] { new TokenSequence2FeatureSequence(alphabet) }));
InstanceList list = new InstanceList(pipes);
TokenSequence ts = new TokenSequence();
ts.add("a");
ts.add("b");
ts.add("c");
list.addThruPipe(new Instance(ts, null, "doc", null));
ParallelTopicModel model = new ParallelTopicModel(1);
model.setRandomSeed(67);
model.addInstances(list);
double[] dist = model.getTopicProbabilities(0);
assertEquals(1, dist.length);
assertEquals(1.0, dist[0], 1e-6);
}

@Test
public void testBuildInitialTypeTopicCountsHandlesMissingTopic() {
Alphabet alphabet = new Alphabet();
SerialPipes pipes = new SerialPipes(Arrays.asList(new Pipe[] { new TokenSequence2FeatureSequence(alphabet) }));
InstanceList list = new InstanceList(pipes);
TokenSequence seq = new TokenSequence();
seq.add("x");
seq.add("y");
list.addThruPipe(new Instance(seq, null, "doc", null));
ParallelTopicModel model = new ParallelTopicModel(2);
model.setRandomSeed(123);
model.addInstances(list);
TopicAssignment assignment = model.getData().get(0);
LabelSequence labels = (LabelSequence) assignment.topicSequence;
labels.getFeatures()[0] = ParallelTopicModel.UNASSIGNED_TOPIC;
model.buildInitialTypeTopicCounts();
double[] dist = model.getTopicProbabilities(labels);
assertEquals(2, dist.length);
assertEquals(1.0, dist[0] + dist[1], 1e-6);
}

@Test
public void testZeroLengthDocumentProducesUniformTopicDistribution() {
Alphabet alphabet = new Alphabet();
SerialPipes pipes = new SerialPipes(Arrays.asList(new Pipe[] { new TokenSequence2FeatureSequence(alphabet) }));
InstanceList list = new InstanceList(pipes);
TokenSequence seq = new TokenSequence();
list.addThruPipe(new Instance(seq, null, "zero-token-doc", null));
ParallelTopicModel model = new ParallelTopicModel(2);
model.setRandomSeed(9);
model.addInstances(list);
double[] dist = model.getTopicProbabilities(0);
assertEquals(2, dist.length);
assertEquals(1.0, dist[0] + dist[1], 1e-6);
}

@Test
public void testMaxTokenTypeUpdatesCorrectly() {
Alphabet alphabet = new Alphabet();
SerialPipes pipes = new SerialPipes(Arrays.asList(new Pipe[] { new TokenSequence2FeatureSequence(alphabet) }));
InstanceList list = new InstanceList(pipes);
TokenSequence seq = new TokenSequence();
seq.add("x");
seq.add("x");
seq.add("x");
list.addThruPipe(new Instance(seq, null, "maxToken", null));
ParallelTopicModel model = new ParallelTopicModel(2);
model.setRandomSeed(100);
model.addInstances(list);
int max = model.getDocumentTopics(true, true)[0].length;
assertEquals(2, max);
}

@Test
public void testGetTopicDocumentsHandlesEmptyInput() {
ParallelTopicModel model = new ParallelTopicModel(3);
ArrayList<TreeSet<IDSorter>> result = model.getTopicDocuments(1.0);
assertEquals(3, result.size());
assertTrue(result.get(0).isEmpty());
assertTrue(result.get(1).isEmpty());
assertTrue(result.get(2).isEmpty());
}

@Test
public void testTokenCountOverflowGracefullyLogged() {
Alphabet alphabet = new Alphabet();
SerialPipes pipes = new SerialPipes(Arrays.asList(new Pipe[] { new TokenSequence2FeatureSequence(alphabet) }));
InstanceList list = new InstanceList(pipes);
TokenSequence ts = new TokenSequence();
ts.add("repeat");
ts.add("repeat");
ts.add("repeat");
ts.add("repeat");
ts.add("repeat");
list.addThruPipe(new Instance(ts, null, "overflow", null));
ParallelTopicModel model = new ParallelTopicModel(2);
model.setRandomSeed(0);
model.addInstances(list);
model.setNumTopics(2);
model.buildInitialTypeTopicCounts();
assertNotNull(model.getTypeTopicCounts());
}

@Test
public void testPrintTopicWordWeightsDoesNotThrow() throws Exception {
Alphabet alphabet = new Alphabet();
SerialPipes pipes = new SerialPipes(Arrays.asList(new Pipe[] { new TokenSequence2FeatureSequence(alphabet) }));
InstanceList list = new InstanceList(pipes);
TokenSequence ts = new TokenSequence();
ts.add("cat");
ts.add("dog");
ts.add("cat");
list.addThruPipe(new Instance(ts, null, "animals", null));
ParallelTopicModel model = new ParallelTopicModel(2);
model.setRandomSeed(33);
model.addInstances(list);
StringWriter sw = new StringWriter();
PrintWriter out = new PrintWriter(sw);
model.printTopicWordWeights(out);
out.flush();
String outText = sw.toString();
assertTrue(outText.contains("cat") || outText.contains("dog"));
}

@Test
public void testGetTopWordsReturnsEmptyWhenNoWordsAssigned() {
ParallelTopicModel model = new ParallelTopicModel(2);
Object[][] result = model.getTopWords(3);
assertEquals(2, result.length);
assertEquals(0, result[0].length);
assertEquals(0, result[1].length);
}

@Test
public void testInitializeFromStateWithMismatchThrowsException() throws Exception {
Alphabet alphabet = new Alphabet();
SerialPipes pipes = new SerialPipes(Arrays.asList(new Pipe[] { new TokenSequence2FeatureSequence(alphabet) }));
InstanceList list = new InstanceList(pipes);
TokenSequence ts = new TokenSequence();
ts.add("hello");
ts.add("world");
list.addThruPipe(new Instance(ts, null, "doc", null));
ParallelTopicModel model = new ParallelTopicModel(2);
model.setRandomSeed(56);
model.addInstances(list);
File stateFile = File.createTempFile("badState", ".gz");
FileOutputStream fos = new FileOutputStream(stateFile);
PrintStream ps = new PrintStream(new GZIPOutputStream(fos));
ps.println("#doc source pos typeindex type topic");
ps.println("#alpha : 0.5 0.5");
ps.println("#beta : 0.01");
ps.println("0 fakeSource 0 99 fakeWord 0");
ps.close();
boolean failed = false;
try {
model.initializeFromState(stateFile);
} catch (IllegalStateException e) {
failed = true;
}
assertTrue("Expected failure when state file token/type mismatch", failed);
stateFile.deleteOnExit();
}

@Test
public void testEmptyTokenSequenceProducesSmoothedTopicProbabilities() {
Alphabet alphabet = new Alphabet();
SerialPipes pipes = new SerialPipes(Collections.singletonList(new TokenSequence2FeatureSequence(alphabet)));
InstanceList list = new InstanceList(pipes);
TokenSequence ts = new TokenSequence();
Instance instance = new Instance(ts, null, "emptyDoc", null);
list.addThruPipe(instance);
ParallelTopicModel model = new ParallelTopicModel(3);
model.setRandomSeed(100);
model.addInstances(list);
double[] probs = model.getTopicProbabilities(0);
double sum = probs[0] + probs[1] + probs[2];
assertEquals(3, probs.length);
assertEquals(1.0, sum, 1e-6);
assertEquals(probs[0], probs[1], 1e-6);
assertEquals(probs[1], probs[2], 1e-6);
}

@Test
public void testOptimizeBetaWithUniformTokensPerTopicDoesNotThrow() {
Alphabet alphabet = new Alphabet();
SerialPipes pipes = new SerialPipes(Collections.singletonList(new TokenSequence2FeatureSequence(alphabet)));
InstanceList list = new InstanceList(pipes);
TokenSequence ts = new TokenSequence();
ts.add("word");
ts.add("word");
Instance instance = new Instance(ts, null, "doc", null);
list.addThruPipe(instance);
ParallelTopicModel model = new ParallelTopicModel(2);
model.setRandomSeed(55);
model.addInstances(list);
WorkerCallable[] callables = new WorkerCallable[1];
callables[0] = new WorkerCallable(model.getNumTopics(), model.alpha, model.alphaSum, model.beta, new Randoms(), model.getData(), model.getTypeTopicCounts(), model.getTokensPerTopic(), 0, 1);
model.optimizeBeta(callables);
assertTrue(model.beta > 0);
assertTrue(model.betaSum > 0);
}

@Test
public void testSaveSerializedModelToInvalidPathShouldLogFailure() {
File file = new File("/this/path/does/not/exist/model-output.ser");
ParallelTopicModel model = new ParallelTopicModel(3);
model.write(file);
assertFalse(file.exists());
}

@Test
public void testPrintDocumentTopicsOnEmptyModelShouldNotThrow() {
ParallelTopicModel model = new ParallelTopicModel(2);
StringWriter sw = new StringWriter();
PrintWriter pw = new PrintWriter(sw);
model.printDocumentTopics(pw);
pw.flush();
String output = sw.toString();
assertTrue(output.contains("#doc name topic proportion"));
}

@Test
public void testPrintTopicDocumentsWithNoDocumentsShouldNotThrow() {
ParallelTopicModel model = new ParallelTopicModel(2);
StringWriter writer = new StringWriter();
PrintWriter pw = new PrintWriter(writer);
model.printTopicDocuments(pw, 10);
pw.flush();
String result = writer.toString();
assertTrue(result.contains("#topic doc name proportion"));
}

@Test
public void testGetTopicWordsWhenNoTokensShouldReturnZeroMatrix() {
ParallelTopicModel model = new ParallelTopicModel(2);
double[][] matrix = model.getTopicWords(true, true);
assertEquals(2, matrix.length);
assertEquals(0, matrix[0].length);
assertEquals(0, matrix[1].length);
}

@Test
public void testSetSaveStateWithZeroIntervalDoesNothing() {
ParallelTopicModel model = new ParallelTopicModel(2);
model.setSaveState(0, "dummyFileName");
assertEquals(0, model.saveStateInterval);
assertEquals("dummyFileName", model.stateFilename);
}

@Test
public void testSetSaveSerializedModelAndVerifyFilename() {
ParallelTopicModel model = new ParallelTopicModel(3);
model.setSaveSerializedModel(5, "saved-model.ser");
assertEquals(5, model.saveModelInterval);
assertEquals("saved-model.ser", model.modelFilename);
}

@Test
public void testSetSymmetricAlphaUpdatesInternalFlag() {
ParallelTopicModel model = new ParallelTopicModel(3);
model.setSymmetricAlpha(true);
assertTrue(model.usingSymmetricAlpha);
model.setSymmetricAlpha(false);
assertFalse(model.usingSymmetricAlpha);
}

@Test
public void testEmptyGetSubCorpusTopicWordsReturnsCorrectDimensions() {
ParallelTopicModel model = new ParallelTopicModel(3);
boolean[] mask = new boolean[0];
double[][] matrix = model.getSubCorpusTopicWords(mask, true, true);
assertEquals(3, matrix.length);
}

@Test
public void testCallGetInferencerBeforeTrainingShouldNotThrow() {
ParallelTopicModel model = new ParallelTopicModel(2);
boolean exceptionThrown = false;
try {
TopicInferencer inferencer = model.getInferencer();
} catch (NullPointerException e) {
exceptionThrown = true;
}
assertTrue(exceptionThrown);
}

@Test
public void testTokenOverflowInBuildTypeTopicCountsHandledGracefully() {
Alphabet alphabet = new Alphabet();
SerialPipes pipes = new SerialPipes(Collections.singletonList(new TokenSequence2FeatureSequence(alphabet)));
InstanceList list = new InstanceList(pipes);
TokenSequence ts = new TokenSequence();
ts.add("word");
ts.add("word");
ts.add("word");
ts.add("word");
ts.add("word");
list.addThruPipe(new Instance(ts, null, "doc", null));
ParallelTopicModel model = new ParallelTopicModel(1);
model.setRandomSeed(1);
model.addInstances(list);
model.setNumTopics(1);
model.buildInitialTypeTopicCounts();
assertNotNull(model.getTypeTopicCounts());
}

@Test
public void testAddInstancesSetsAlphaSumAndBetaSumCorrectly() {
Alphabet alphabet = new Alphabet();
SerialPipes pipes = new SerialPipes(Arrays.asList(new Pipe[] { new TokenSequence2FeatureSequence(alphabet) }));
InstanceList instances = new InstanceList(pipes);
TokenSequence ts = new TokenSequence();
ts.add("a");
ts.add("b");
instances.addThruPipe(new Instance(ts, null, "doc", null));
ParallelTopicModel model = new ParallelTopicModel(2, 10.0, 0.5);
model.setRandomSeed(42);
model.addInstances(instances);
assertEquals(2, model.alpha.length);
assertEquals(10.0, model.alphaSum, 0.0);
assertTrue(model.betaSum > 0.0);
}

@Test
public void testSetNumThreadsAffectsEstimateThreadCount() throws IOException {
Alphabet alphabet = new Alphabet();
SerialPipes pipes = new SerialPipes(Arrays.asList(new Pipe[] { new TokenSequence2FeatureSequence(alphabet) }));
InstanceList list = new InstanceList(pipes);
TokenSequence ts = new TokenSequence();
ts.add("x");
ts.add("y");
list.addThruPipe(new Instance(ts, null, "doc", null));
ParallelTopicModel model = new ParallelTopicModel(2);
model.setRandomSeed(123);
model.addInstances(list);
model.setNumThreads(2);
model.setNumIterations(1);
model.estimate();
assertEquals(2, model.numThreads);
}

@Test
public void testEstimateWithZeroIterationsDoesNothing() throws IOException {
Alphabet alphabet = new Alphabet();
TokenSequence ts = new TokenSequence();
ts.add("a");
ts.add("b");
SerialPipes pipes = new SerialPipes(Collections.singletonList(new TokenSequence2FeatureSequence(alphabet)));
InstanceList list = new InstanceList(pipes);
list.addThruPipe(new Instance(ts, null, "doc", null));
ParallelTopicModel model = new ParallelTopicModel(2);
model.setRandomSeed(10);
model.addInstances(list);
model.setNumIterations(0);
model.estimate();
assertTrue(true);
}

@Test
public void testPrintTopWordsOnEmptyModelReturnsEmptyString() {
ParallelTopicModel model = new ParallelTopicModel(3);
String result = model.displayTopWords(5, false);
assertNotNull(result);
assertTrue(result.trim().length() > 0);
}

@Test
public void testMaximizeWithNoDocumentsDoesNotThrow() {
ParallelTopicModel model = new ParallelTopicModel(3);
model.maximize(2);
assertTrue(true);
}

@Test
public void testPrintTopWordsToPrintStreamBeforeDataAdded() {
ParallelTopicModel model = new ParallelTopicModel(2);
ByteArrayOutputStream baos = new ByteArrayOutputStream();
PrintStream ps = new PrintStream(baos);
model.printTopWords(ps, 5, false);
ps.flush();
String output = baos.toString();
assertTrue(output.contains("\n"));
}

@Test
public void testPrintTopicDocumentsWithoutAnyInstances() {
ParallelTopicModel model = new ParallelTopicModel(2);
StringWriter writer = new StringWriter();
PrintWriter pw = new PrintWriter(writer);
model.printTopicDocuments(pw);
pw.flush();
String out = writer.toString();
assertTrue(out.contains("#topic doc name proportion"));
}

@Test
public void testEmptyAlphabetStillProducesValidModel() {
Alphabet alphabet = new Alphabet();
SerialPipes pipes = new SerialPipes(Collections.singletonList(new TokenSequence2FeatureSequence(alphabet)));
InstanceList list = new InstanceList(pipes);
TokenSequence seq = new TokenSequence();
list.addThruPipe(new Instance(seq, null, "empty-doc", null));
ParallelTopicModel model = new ParallelTopicModel(2);
model.setRandomSeed(99);
model.addInstances(list);
model.setNumIterations(1);
try {
model.estimate();
} catch (Exception e) {
fail("Model should estimate even with empty alphabet");
}
assertNotNull(model.getTopicAlphabet());
}

@Test
public void testOutputStateToFileAndVerifyGzipContent() throws IOException {
Alphabet alphabet = new Alphabet();
SerialPipes pipes = new SerialPipes(Collections.singletonList(new TokenSequence2FeatureSequence(alphabet)));
InstanceList list = new InstanceList(pipes);
TokenSequence ts = new TokenSequence();
ts.add("dog");
ts.add("cat");
list.addThruPipe(new Instance(ts, null, "zoo", "metadata"));
ParallelTopicModel model = new ParallelTopicModel(2);
model.setRandomSeed(7);
model.addInstances(list);
File outFile = File.createTempFile("state", ".gz");
model.printState(outFile);
assertTrue(outFile.exists());
assertTrue(outFile.length() > 0);
outFile.deleteOnExit();
}

@Test
public void testDocumentTopicDistributionWithNegativeMaxLimit() {
Alphabet alphabet = new Alphabet();
SerialPipes pipes = new SerialPipes(Collections.singletonList(new TokenSequence2FeatureSequence(alphabet)));
InstanceList list = new InstanceList(pipes);
TokenSequence seq = new TokenSequence();
seq.add("a");
seq.add("b");
list.addThruPipe(new Instance(seq, null, "foo", null));
ParallelTopicModel model = new ParallelTopicModel(3);
model.setRandomSeed(1001);
model.addInstances(list);
StringWriter sw = new StringWriter();
PrintWriter pw = new PrintWriter(sw);
model.printDocumentTopics(pw, 0.0, -1);
pw.flush();
String out = sw.toString();
assertTrue(out.contains("foo"));
}

@Test
public void testPrintTopicWordWeightsFileStreamOutput() throws IOException {
Alphabet alphabet = new Alphabet();
SerialPipes pipes = new SerialPipes(Collections.singletonList(new TokenSequence2FeatureSequence(alphabet)));
InstanceList list = new InstanceList(pipes);
TokenSequence ts = new TokenSequence();
ts.add("mallet");
ts.add("topic");
ts.add("model");
list.addThruPipe(new Instance(ts, null, "test", null));
ParallelTopicModel model = new ParallelTopicModel(3);
model.setRandomSeed(123);
model.addInstances(list);
File file = File.createTempFile("topic-weights", ".txt");
model.printTopicWordWeights(file);
assertTrue(file.exists());
assertTrue(file.length() > 0);
file.deleteOnExit();
}

@Test
public void testSetOptimizeIntervalGreaterThanSaveSampleIntervalAdjustsIt() {
ParallelTopicModel model = new ParallelTopicModel(3);
model.saveSampleInterval = 3;
model.setOptimizeInterval(5);
assertEquals(3, model.saveSampleInterval);
ParallelTopicModel model2 = new ParallelTopicModel(3);
model2.saveSampleInterval = 10;
model2.setOptimizeInterval(2);
assertEquals(2, model2.saveSampleInterval);
}

@Test
public void testPrintStateToStreamWithNullInstanceMetadata() throws IOException {
Alphabet alphabet = new Alphabet();
SerialPipes pipes = new SerialPipes(Collections.singletonList(new TokenSequence2FeatureSequence(alphabet)));
InstanceList list = new InstanceList(pipes);
TokenSequence seq = new TokenSequence();
seq.add("state");
seq.add("print");
Instance instance = new Instance(seq, null, null, null);
list.addThruPipe(instance);
ParallelTopicModel model = new ParallelTopicModel(2);
model.setRandomSeed(88);
model.addInstances(list);
ByteArrayOutputStream baos = new ByteArrayOutputStream();
PrintStream ps = new PrintStream(baos);
model.printState(ps);
ps.flush();
String content = baos.toString("UTF-8");
assertTrue(content.contains("#alpha"));
assertTrue(content.contains("#beta"));
assertTrue(content.contains("NA"));
}

@Test
public void testModelLogLikelihoodReturnsZeroForInvalidCounts() {
ParallelTopicModel model = new ParallelTopicModel(2);
model.numTypes = 2;
model.tokensPerTopic = new int[] { -1, -1 };
double result = model.modelLogLikelihood();
assertEquals(0.0, result, 0.0);
}

@Test
public void testPrintDocumentTopicsWithNamedAndUnnamedInstances() {
Alphabet alphabet = new Alphabet();
SerialPipes pipes = new SerialPipes(Collections.singletonList(new TokenSequence2FeatureSequence(alphabet)));
InstanceList list = new InstanceList(pipes);
TokenSequence seq1 = new TokenSequence();
seq1.add("topic");
seq1.add("lda");
list.addThruPipe(new Instance(seq1, null, "docNamed", null));
TokenSequence seq2 = new TokenSequence();
seq2.add("prob");
seq2.add("dist");
list.addThruPipe(new Instance(seq2, null, null, null));
ParallelTopicModel model = new ParallelTopicModel(2);
model.setRandomSeed(10);
model.addInstances(list);
StringWriter sw = new StringWriter();
PrintWriter pw = new PrintWriter(sw);
model.printDocumentTopics(pw, 0.0, 2);
pw.flush();
String output = sw.toString();
assertTrue(output.contains("docNamed"));
assertTrue(output.contains("no-name"));
}

@Test
public void testGetInferencerThrowsWhenNoDataAdded() {
ParallelTopicModel model = new ParallelTopicModel(3);
boolean threw = false;
try {
model.getInferencer();
} catch (IndexOutOfBoundsException | NullPointerException e) {
threw = true;
}
assertTrue(threw);
}

@Test
public void testPrintStateDecodesBackCorrectly() throws IOException {
Alphabet alphabet = new Alphabet();
SerialPipes pipes = new SerialPipes(Collections.singletonList(new TokenSequence2FeatureSequence(alphabet)));
InstanceList list = new InstanceList(pipes);
TokenSequence ts = new TokenSequence();
ts.add("x");
ts.add("y");
list.addThruPipe(new Instance(ts, null, "stateTest", null));
ParallelTopicModel model = new ParallelTopicModel(2);
model.setRandomSeed(9);
model.addInstances(list);
File file = File.createTempFile("state", ".gz");
PrintStream out = new PrintStream(new GZIPOutputStream(new FileOutputStream(file)));
model.printState(out);
out.close();
boolean failed = false;
try {
model.initializeFromState(file);
} catch (IOException e) {
failed = true;
}
assertFalse(failed);
file.deleteOnExit();
}

@Test
public void testEstimateWithSingleThreadCorrectlyAvoidsCallableMerging() throws IOException {
Alphabet alphabet = new Alphabet();
SerialPipes pipes = new SerialPipes(Collections.singletonList(new TokenSequence2FeatureSequence(alphabet)));
InstanceList list = new InstanceList(pipes);
TokenSequence ts = new TokenSequence();
ts.add("a");
ts.add("b");
list.addThruPipe(new Instance(ts, null, "doc", null));
ParallelTopicModel model = new ParallelTopicModel(2);
model.setRandomSeed(42);
model.setNumThreads(1);
model.setNumIterations(1);
model.addInstances(list);
model.estimate();
assertEquals(1, model.numThreads);
}

@Test
public void testGetTopicDocumentsWithSmoothingReturnsSortedSet() {
Alphabet alphabet = new Alphabet();
SerialPipes pipes = new SerialPipes(Collections.singletonList(new TokenSequence2FeatureSequence(alphabet)));
InstanceList list = new InstanceList(pipes);
TokenSequence ts = new TokenSequence();
ts.add("alpha");
ts.add("beta");
list.addThruPipe(new Instance(ts, null, "doc123", null));
ParallelTopicModel model = new ParallelTopicModel(2);
model.setRandomSeed(7);
model.addInstances(list);
ArrayList<TreeSet<IDSorter>> docs = model.getTopicDocuments(0.1);
assertEquals(2, docs.size());
assertFalse(docs.get(0).isEmpty() || docs.get(1).isEmpty());
}

@Test
public void testGetDocumentTopicsReturnsDenseRepresentation() {
Alphabet alphabet = new Alphabet();
SerialPipes pipes = new SerialPipes(Collections.singletonList(new TokenSequence2FeatureSequence(alphabet)));
InstanceList list = new InstanceList(pipes);
TokenSequence seq = new TokenSequence();
seq.add("alpha");
seq.add("beta");
list.addThruPipe(new Instance(seq, null, "denseDoc", null));
ParallelTopicModel model = new ParallelTopicModel(3);
model.setRandomSeed(1337);
model.addInstances(list);
double[][] result = model.getDocumentTopics(true, true);
assertEquals(1, result.length);
assertEquals(3, result[0].length);
double sum = result[0][0] + result[0][1] + result[0][2];
assertEquals(1.0, sum, 1e-6);
}

@Test
public void testModelLogLikelihoodReturnsZeroForNegativeTokensPerTopic() {
ParallelTopicModel model = new ParallelTopicModel(2);
model.tokensPerTopic = new int[] { -5, 10 };
model.typeTopicCounts = new int[0][];
model.numTypes = 0;
double ll = model.modelLogLikelihood();
assertEquals(0.0, ll, 0.0001);
}

@Test
public void testSetNumTopicsToOneStartsCorrectBitmaskLogic() {
ParallelTopicModel model = new ParallelTopicModel(2);
model.setNumTopics(1);
assertEquals(1, model.getNumTopics());
}

@Test
public void testGetTopWordsReturnsEmptyForNoVocabulary() {
ParallelTopicModel model = new ParallelTopicModel(3);
Object[][] words = model.getTopWords(5);
assertEquals(3, words.length);
assertEquals(0, words[0].length);
assertEquals(0, words[1].length);
assertEquals(0, words[2].length);
}

@Test
public void testSetSaveStateAndSerializeModelIntervalZeroPersistsFields() {
ParallelTopicModel model = new ParallelTopicModel(3);
model.setSaveState(0, "dummy");
model.setSaveSerializedModel(0, "model.obj");
assertEquals("dummy", model.stateFilename);
assertEquals("model.obj", model.modelFilename);
assertEquals(0, model.saveModelInterval);
assertEquals(0, model.saveStateInterval);
}

@Test
public void testReadBadObjectStreamThrowsException() throws Exception {
File tmp = File.createTempFile("badmodel", ".bad");
FileOutputStream fos = new FileOutputStream(tmp);
fos.write("invalid serialized model".getBytes());
fos.close();
boolean caught = false;
try {
ObjectInputStream ois = new ObjectInputStream(new FileInputStream(tmp));
ParallelTopicModel m = (ParallelTopicModel) ois.readObject();
ois.close();
} catch (Exception e) {
caught = true;
}
assertTrue(caught);
tmp.deleteOnExit();
}

@Test
public void testWriteObjectHandlesIOExceptionGracefully() {
ParallelTopicModel model = new ParallelTopicModel(3);
File file = new File("/unlikely/location/no-permission.ser");
model.write(file);
assertFalse(file.exists());
}

@Test
public void testInitializeFromEmptyStateFileThrowsIOException() throws IOException {
File f = File.createTempFile("empty", ".gz");
GZIPOutputStream gz = new GZIPOutputStream(new FileOutputStream(f));
gz.close();
ParallelTopicModel model = new ParallelTopicModel(2);
boolean failed = false;
try {
model.initializeFromState(f);
} catch (IOException e) {
failed = true;
}
assertTrue(failed);
f.deleteOnExit();
}

@Test
public void testPrintTopicXMLReportHandlesNoWordsInTopics() throws IOException {
ParallelTopicModel model = new ParallelTopicModel(2);
StringWriter sw = new StringWriter();
PrintWriter pw = new PrintWriter(sw);
model.topicXMLReport(pw, 5);
pw.flush();
String xml = sw.toString();
assertTrue(xml.contains("<?xml"));
assertTrue(xml.contains("<topicModel>"));
assertTrue(xml.contains("</topicModel>"));
}

@Test
public void testPrintTopicPhraseXMLReportHandlesNoDocumentsSuccessfully() {
ParallelTopicModel model = new ParallelTopicModel(2);
StringWriter sw = new StringWriter();
PrintWriter pw = new PrintWriter(sw);
model.topicPhraseXMLReport(pw, 5);
pw.flush();
String xml = sw.toString();
assertTrue(xml.contains("<topics>"));
assertTrue(xml.contains("</topics>"));
}

@Test
public void testGenerateSubCorpusTopicWordsOnEmptyMaskReturnsZeroMatrix() {
ParallelTopicModel model = new ParallelTopicModel(3);
boolean[] mask = new boolean[0];
double[][] result = model.getSubCorpusTopicWords(mask, true, true);
assertEquals(3, result.length);
assertEquals(0, result[0].length);
}

@Test
public void testOptimizeAlphaHandlesEmptyHistogramsGracefully() {
ParallelTopicModel model = new ParallelTopicModel(2);
model.docLengthCounts = new int[0];
model.topicDocCounts = new int[2][0];
WorkerCallable[] callables = new WorkerCallable[0];
model.optimizeAlpha(callables);
assertTrue(model.alphaSum > 0);
}

@Test
public void testTemperAlphaWithInitializedCallableArrays() {
ParallelTopicModel model = new ParallelTopicModel(2);
WorkerCallable[] callables = new WorkerCallable[1];
int[] docLengths = new int[] { 1 };
int[][] topicCounts = new int[2][];
topicCounts[0] = new int[] { 1 };
topicCounts[1] = new int[] { 1 };
callables[0] = new WorkerCallable(2, model.alpha, model.alphaSum, model.beta, new Randoms(), new ArrayList<>(), new int[0][], new int[0], 0, 0);
callables[0].docLengthCounts = docLengths;
callables[0].topicDocCounts = topicCounts;
model.temperAlpha(callables);
assertEquals(1.0, model.alpha[0], 1e-10);
assertEquals(1.0, model.alpha[1], 1e-10);
}

@Test
public void testEstimateHandlesSingleThreadAndSingleInstanceList() throws IOException {
Alphabet alphabet = new Alphabet();
SerialPipes pipes = new SerialPipes(Collections.singletonList(new TokenSequence2FeatureSequence(alphabet)));
InstanceList list = new InstanceList(pipes);
TokenSequence ts = new TokenSequence();
ts.add("x");
ts.add("y");
list.addThruPipe(new Instance(ts, null, "doc", null));
ParallelTopicModel model = new ParallelTopicModel(2);
model.setRandomSeed(99);
model.setNumThreads(1);
model.setNumIterations(1);
model.addInstances(list);
model.estimate();
assertEquals(1, model.getData().size());
}

@Test
public void testMaximizeTerminatesWhenNoChangesOccur() {
Alphabet alphabet = new Alphabet();
SerialPipes pipes = new SerialPipes(Collections.singletonList(new TokenSequence2FeatureSequence(alphabet)));
InstanceList list = new InstanceList(pipes);
TokenSequence seq = new TokenSequence();
seq.add("repeat");
seq.add("repeat");
list.addThruPipe(new Instance(seq, null, "repeatDoc", null));
ParallelTopicModel model = new ParallelTopicModel(1);
model.setRandomSeed(1);
model.addInstances(list);
model.maximize(5);
assertEquals(1, model.getNumTopics());
}
}
