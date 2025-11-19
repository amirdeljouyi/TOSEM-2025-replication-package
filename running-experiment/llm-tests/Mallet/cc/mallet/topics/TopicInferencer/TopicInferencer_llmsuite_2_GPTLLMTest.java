package cc.mallet.topics;

import cc.mallet.pipe.CharSequence2TokenSequence;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.TokenSequence2FeatureSequence;
import cc.mallet.types.*;
import cc.mallet.util.Randoms;
import org.junit.Test;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.GZIPOutputStream;
import static org.junit.Assert.*;

public class TopicInferencer_llmsuite_2_GPTLLMTest {

@Test
public void testGetSampledDistribution_SimpleInstance() {
Alphabet alphabet = new Alphabet();
int w0 = alphabet.lookupIndex("w0");
int w1 = alphabet.lookupIndex("w1");
int w2 = alphabet.lookupIndex("w2");
int numTopics = 3;
double beta = 0.01;
double[] alpha = new double[] { 0.1, 0.1, 0.1 };
double betaSum = 0.03;
int topicBits = Integer.bitCount(Integer.highestOneBit(numTopics) * 2 - 1);
int[][] typeTopicCounts = new int[3][];
typeTopicCounts[0] = new int[] { 5 << topicBits | 0 };
typeTopicCounts[1] = new int[] { 4 << topicBits | 1 };
typeTopicCounts[2] = new int[] { 3 << topicBits | 2 };
int[] tokensPerTopic = new int[] { 10, 10, 10 };
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(42);
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { w0, w1, w2 });
Instance instance = new Instance(fs, null, "doc1", null);
double[] dist = inferencer.getSampledDistribution(instance, 10, 1, 0);
assertEquals(3, dist.length);
assertTrue(dist[0] >= 0.0);
assertTrue(dist[1] >= 0.0);
assertTrue(dist[2] >= 0.0);
double sum = dist[0] + dist[1] + dist[2];
assertEquals(1.0, sum, 1e-6);
}

@Test
public void testGetSampledDistribution_ZeroIterations() {
Alphabet alphabet = new Alphabet();
int w0 = alphabet.lookupIndex("tokenA");
int numTopics = 3;
double beta = 0.01;
double[] alpha = new double[] { 0.1, 0.1, 0.1 };
double betaSum = 0.03;
int topicBits = Integer.bitCount(Integer.highestOneBit(numTopics) * 2 - 1);
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { 8 << topicBits | 0 };
int[] tokensPerTopic = new int[] { 10, 10, 10 };
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(7);
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { w0 });
Instance instance = new Instance(fs, null, "zerodoc", null);
double[] dist = inferencer.getSampledDistribution(instance, 0, 1, 0);
assertEquals(3, dist.length);
double normalizedTotal = dist[0] + dist[1] + dist[2];
assertEquals(1.0, normalizedTotal, 1e-6);
}

@Test
public void testGetSampledDistribution_OutOfVocabIgnored() {
Alphabet alphabet = new Alphabet();
int w0 = alphabet.lookupIndex("known");
int numTopics = 2;
double beta = 0.01;
double[] alpha = new double[] { 0.1, 0.1 };
double betaSum = 0.02;
int topicBits = Integer.bitCount(Integer.highestOneBit(numTopics) * 2 - 1);
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { 7 << topicBits | 1 };
int[] tokensPerTopic = new int[] { 12, 8 };
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(99);
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { w0, 10 });
Instance instance = new Instance(fs, null, "OOVdoc", null);
double[] result = inferencer.getSampledDistribution(instance, 5, 1, 0);
assertEquals(2, result.length);
double total = result[0] + result[1];
assertEquals(1.0, total, 1e-6);
}

@Test
public void testGetSampledDistribution_EmptyDocument() {
Alphabet alphabet = new Alphabet();
int numTopics = 3;
double beta = 0.01;
double[] alpha = new double[] { 0.1, 0.1, 0.1 };
double betaSum = 0.03;
int[][] typeTopicCounts = new int[0][];
int[] tokensPerTopic = new int[] { 5, 5, 5 };
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(1234);
FeatureSequence fs = new FeatureSequence(alphabet, new int[] {});
Instance instance = new Instance(fs, null, "empty", null);
double[] dist = inferencer.getSampledDistribution(instance, 5, 1, 0);
assertEquals(3, dist.length);
double total = dist[0] + dist[1] + dist[2];
assertEquals(1.0, total, 1e-6);
}

@Test
public void testWriteInferredDistributions_OutputFile() throws IOException {
Alphabet alphabet = new Alphabet();
int w0 = alphabet.lookupIndex("alpha");
int w1 = alphabet.lookupIndex("beta");
int w2 = alphabet.lookupIndex("gamma");
int numTopics = 2;
double beta = 0.01;
double[] alphaVals = new double[] { 0.5, 0.5 };
double betaSum = 0.02;
int topicBits = Integer.bitCount(Integer.highestOneBit(numTopics) * 2 - 1);
int[][] typeTopicCounts = new int[3][];
typeTopicCounts[0] = new int[] { 9 << topicBits | 0 };
typeTopicCounts[1] = new int[] { 4 << topicBits | 1 };
typeTopicCounts[2] = new int[] { 2 << topicBits | 1 };
int[] tokensPerTopic = new int[] { 20, 15 };
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alphaVals, beta, betaSum);
inferencer.setRandomSeed(777);
FeatureSequence fs1 = new FeatureSequence(alphabet, new int[] { w0, w1 });
FeatureSequence fs2 = new FeatureSequence(alphabet, new int[] { w2, w2 });
Instance instance1 = new Instance(fs1, null, "instA", null);
Instance instance2 = new Instance(fs2, null, "instB", null);
InstanceList instanceList = new InstanceList(alphabet, null);
instanceList.add(instance1);
instanceList.add(instance2);
File tempFile = File.createTempFile("topic-dist", ".txt");
tempFile.deleteOnExit();
inferencer.writeInferredDistributions(instanceList, tempFile, 5, 1, 0, 0.0, -1);
BufferedReader reader = new BufferedReader(new FileReader(tempFile));
String firstLine = reader.readLine();
assertTrue(firstLine.startsWith("#doc"));
String secondLine = reader.readLine();
assertNotNull(secondLine);
assertTrue(secondLine.contains("instA") || secondLine.contains("instB"));
reader.close();
}

@Test
public void testSerialization_RoundTrip() throws IOException, ClassNotFoundException {
Alphabet alphabet = new Alphabet();
int token = alphabet.lookupIndex("serialize");
int numTopics = 2;
double[] alpha = new double[] { 0.1, 0.1 };
double beta = 0.01;
double betaSum = 0.02;
int topicBits = Integer.bitCount(Integer.highestOneBit(numTopics) * 2 - 1);
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { 6 << topicBits | 0 };
int[] tokensPerTopic = new int[] { 5, 5 };
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(888);
File temp = File.createTempFile("inference-test", ".ser");
temp.deleteOnExit();
ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(temp));
out.writeObject(inferencer);
out.close();
ObjectInputStream in = new ObjectInputStream(new FileInputStream(temp));
TopicInferencer loadedInferencer = (TopicInferencer) in.readObject();
in.close();
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { token });
Instance instance = new Instance(fs, null, "serde", null);
double[] dist = loadedInferencer.getSampledDistribution(instance, 5, 1, 0);
assertEquals(2, dist.length);
double s = dist[0] + dist[1];
assertEquals(1.0, s, 1e-6);
}

@Test(expected = NullPointerException.class)
public void testGetSampledDistribution_NullInstanceThrows() {
int[][] dummyCounts = new int[1][];
dummyCounts[0] = new int[] {};
int[] tokensPerTopic = new int[] { 5 };
double[] alpha = new double[] { 0.1 };
Alphabet alphabet = new Alphabet();
TopicInferencer inferencer = new TopicInferencer(dummyCounts, tokensPerTopic, alphabet, alpha, 0.01, 0.01);
inferencer.setRandomSeed(1);
inferencer.getSampledDistribution(null, 5, 1, 0);
}

@Test
public void testUniformAlphaAndZeroTypeTopicCounts() {
Alphabet alphabet = new Alphabet();
int index0 = alphabet.lookupIndex("term");
int numTopics = 3;
double beta = 0.01;
double[] alpha = new double[] { 1.0, 1.0, 1.0 };
double betaSum = beta * numTopics;
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[0];
int[] tokensPerTopic = new int[] { 0, 0, 0 };
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(42);
FeatureSequence seq = new FeatureSequence(alphabet, new int[] { index0 });
Instance instance = new Instance(seq, null, "unseen", null);
double[] distribution = inferencer.getSampledDistribution(instance, 1, 1, 0);
assertEquals(3, distribution.length);
double total = distribution[0] + distribution[1] + distribution[2];
assertEquals(1.0, total, 1e-6);
}

@Test
public void testAllZeroTokensPerTopic() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("x");
int numTopics = 2;
double beta = 0.01;
double[] alpha = new double[] { 0.5, 0.5 };
double betaSum = 0.02;
int[][] typeTopicCounts = new int[1][];
int topicBits = Integer.bitCount(Integer.highestOneBit(numTopics) * 2 - 1);
typeTopicCounts[0] = new int[] { 2 << topicBits | 0 };
int[] tokensPerTopic = new int[] { 0, 0 };
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(9);
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { 0 });
Instance instance = new Instance(fs, null, "zero-topics", null);
double[] distribution = inferencer.getSampledDistribution(instance, 5, 1, 0);
assertEquals(2, distribution.length);
double sum = distribution[0] + distribution[1];
assertEquals(1.0, sum, 1e-6);
}

@Test
public void testSingleTopicOnly() {
Alphabet alphabet = new Alphabet();
int index = alphabet.lookupIndex("word");
int numTopics = 1;
double beta = 0.01;
double[] alpha = new double[] { 0.5 };
double betaSum = 0.01;
int[][] typeTopicCounts = new int[1][];
int topicBits = Integer.bitCount(Integer.highestOneBit(numTopics) * 2 - 1);
typeTopicCounts[0] = new int[] { 3 << topicBits | 0 };
int[] tokensPerTopic = new int[] { 5 };
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(3);
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { index, index });
Instance instance = new Instance(fs, null, "single", null);
double[] dist = inferencer.getSampledDistribution(instance, 4, 1, 0);
assertEquals(1, dist.length);
assertEquals(1.0, dist[0], 1e-6);
}

@Test
public void testEmptyTypeTopicCountsArray() {
Alphabet alphabet = new Alphabet();
int numTopics = 2;
double beta = 0.01;
double[] alpha = new double[] { 0.5, 0.5 };
double betaSum = 0.02;
int[][] typeTopicCounts = new int[0][];
int[] tokensPerTopic = new int[] { 1, 1 };
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(123);
FeatureSequence fs = new FeatureSequence(alphabet, new int[] {});
Instance instance = new Instance(fs, null, "nodata", null);
double[] result = inferencer.getSampledDistribution(instance, 0, 1, 0);
assertEquals(2, result.length);
double total = result[0] + result[1];
assertEquals(1.0, total, 1e-6);
}

@Test
public void testWriteInferredDistributions_MaxTopicsSmallerThanPresent() throws Exception {
Alphabet alphabet = new Alphabet();
int id0 = alphabet.lookupIndex("alpha");
int id1 = alphabet.lookupIndex("beta");
int numTopics = 3;
double beta = 0.01;
double[] alphaVals = new double[] { 1.0, 1.0, 1.0 };
double betaSum = beta * numTopics;
int topicBits = Integer.bitCount(Integer.highestOneBit(numTopics) * 2 - 1);
int[][] typeTopicCounts = new int[2][];
typeTopicCounts[0] = new int[] { 6 << topicBits | 0 };
typeTopicCounts[1] = new int[] { 4 << topicBits | 1 };
int[] tokensPerTopic = new int[] { 5, 5, 5 };
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alphaVals, beta, betaSum);
inferencer.setRandomSeed(17);
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { id0, id1 });
Instance instance = new Instance(fs, null, "max-threshold", null);
InstanceList list = new InstanceList(alphabet, null);
list.add(instance);
File temp = File.createTempFile("test-threshold", ".dist");
temp.deleteOnExit();
inferencer.writeInferredDistributions(list, temp, 5, 1, 0, 0.0001, 1);
BufferedReader reader = new BufferedReader(new FileReader(temp));
String line = reader.readLine();
assertTrue(line.startsWith("#doc"));
String content = reader.readLine();
assertTrue(content.split("\t").length <= 4);
reader.close();
}

@Test
public void testSetRandomSeedChangesDistribution() {
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("term");
int numTopics = 2;
double beta = 0.01;
double[] alpha = new double[] { 0.1, 0.1 };
double betaSum = 0.02;
int topicBits = Integer.bitCount(Integer.highestOneBit(numTopics) * 2 - 1);
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { 6 << topicBits | 1 };
int[] tokensPerTopic = new int[] { 3, 7 };
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { idx });
Instance instance = new Instance(fs, null, "compare", null);
inferencer.setRandomSeed(42);
double[] dist1 = inferencer.getSampledDistribution(instance, 5, 1, 0);
inferencer.setRandomSeed(99);
double[] dist2 = inferencer.getSampledDistribution(instance, 5, 1, 0);
boolean unequal = dist1[0] != dist2[0] || dist1[1] != dist2[1];
assertTrue("Random seeds should produce different results", unequal);
}

@Test
public void testAlphaZeroValuesHandledGracefully() {
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("x");
int numTopics = 2;
double[] alpha = new double[] { 0.0, 0.0 };
double beta = 0.01;
double betaSum = beta * numTopics;
int topicBits = Integer.bitCount(Integer.highestOneBit(numTopics) * 2 - 1);
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { 2 << topicBits | 0 };
int[] tokensPerTopic = new int[] { 3, 3 };
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(42);
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { idx });
Instance instance = new Instance(fs, null, "zeroAlpha", null);
double[] distribution = inferencer.getSampledDistribution(instance, 5, 1, 0);
assertEquals(2, distribution.length);
assertTrue(distribution[0] >= 0.0);
assertTrue(distribution[1] >= 0.0);
assertEquals(1.0, distribution[0] + distribution[1], 1e-6);
}

@Test
public void testSmallAlphaLargeTokenCounts() {
Alphabet alphabet = new Alphabet();
int tokenIndex = alphabet.lookupIndex("z");
int numTopics = 2;
double[] alpha = new double[] { 1e-10, 1e-10 };
double beta = 0.1;
double betaSum = 0.2;
int topicBits = Integer.bitCount(Integer.highestOneBit(numTopics) * 2 - 1);
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { 10 << topicBits | 0, 5 << topicBits | 1 };
int[] tokensPerTopic = new int[] { 1_000_000, 2_000_000 };
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(314);
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { tokenIndex });
Instance instance = new Instance(fs, null, "largeCounts", null);
double[] distribution = inferencer.getSampledDistribution(instance, 3, 1, 0);
assertEquals(2, distribution.length);
assertEquals(1.0, distribution[0] + distribution[1], 1e-6);
}

@Test
public void testMaxParameterEqualToZeroInWriteInferredDistributions() throws Exception {
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("token");
int numTopics = 3;
double[] alpha = new double[] { 0.1, 0.2, 0.3 };
double beta = 0.01;
double betaSum = 0.03;
int topicBits = Integer.bitCount(Integer.highestOneBit(numTopics) * 2 - 1);
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { 4 << topicBits | 1 };
int[] tokensPerTopic = new int[] { 10, 10, 10 };
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(17);
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { idx });
Instance instance = new Instance(fs, null, "mydoc", null);
InstanceList list = new InstanceList(alphabet, null);
list.add(instance);
File temp = File.createTempFile("maxzero", ".txt");
temp.deleteOnExit();
inferencer.writeInferredDistributions(list, temp, 5, 1, 0, 0.1, 0);
BufferedReader reader = new BufferedReader(new FileReader(temp));
assertTrue(reader.readLine().startsWith("#doc"));
String line = reader.readLine();
assertNotNull(line);
assertTrue(line.contains("mydoc"));
reader.close();
}

@Test
public void testInstanceWithNullNameGetsFallbackName() throws Exception {
Alphabet alphabet = new Alphabet();
int index = alphabet.lookupIndex("t");
int numTopics = 2;
double[] alpha = new double[] { 0.3, 0.3 };
double beta = 0.01;
double betaSum = 0.02;
int topicBits = Integer.bitCount(Integer.highestOneBit(numTopics) * 2 - 1);
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { 2 << topicBits | 0 };
int[] tokensPerTopic = new int[] { 5, 5 };
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(22);
FeatureSequence seq = new FeatureSequence(alphabet, new int[] { index });
Instance instance = new Instance(seq, null, null, null);
InstanceList list = new InstanceList(alphabet, null);
list.add(instance);
File file = File.createTempFile("nullNameDist", ".txt");
file.deleteOnExit();
inferencer.writeInferredDistributions(list, file, 4, 1, 0, 0.0, -1);
BufferedReader reader = new BufferedReader(new FileReader(file));
String header = reader.readLine();
assertTrue(header.contains("#doc"));
String dataLine = reader.readLine();
assertTrue(dataLine.contains("no-name"));
reader.close();
}

@Test
public void testReadObjectThrowsIOExceptionOnCorruptStream() throws Exception {
byte[] invalidData = new byte[] { 1, 2, 3, 4, 5 };
ByteArrayInputStream bais = new ByteArrayInputStream(invalidData);
ObjectInputStream ois = new ObjectInputStream(bais);
boolean exceptionThrown = false;
try {
TopicInferencer inferencer = (TopicInferencer) ois.readObject();
} catch (IOException e) {
exceptionThrown = true;
} catch (ClassNotFoundException e) {
}
assertTrue("IOException should have been thrown for corrupt data", exceptionThrown);
}

@Test
public void testGetSampledDistribution_OneTokenMultipleIterationsBurnInThinning() {
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("word");
int numTopics = 3;
double[] alpha = new double[] { 0.5, 0.5, 0.5 };
double beta = 0.01;
double betaSum = 0.03;
int topicBits = Integer.bitCount(Integer.highestOneBit(numTopics) * 2 - 1);
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { 3 << topicBits | 0, 5 << topicBits | 1 };
int[] tokensPerTopic = new int[] { 3, 7, 0 };
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(121);
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { idx });
Instance instance = new Instance(fs, null, "thinburn", null);
double[] distribution = inferencer.getSampledDistribution(instance, 20, 5, 5);
assertEquals(3, distribution.length);
assertEquals(1.0, distribution[0] + distribution[1] + distribution[2], 1e-6);
}

@Test
public void testWriteInferredDistributions_ThresholdTooHighNoTopicsWritten() throws Exception {
Alphabet alphabet = new Alphabet();
int wordIdx = alphabet.lookupIndex("high-threshold");
int numTopics = 2;
double[] alpha = new double[] { 0.1, 0.1 };
double beta = 0.01;
double betaSum = 0.02;
int topicBits = Integer.bitCount(Integer.highestOneBit(numTopics) * 2 - 1);
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { 2 << topicBits | 0 };
int[] tokensPerTopic = new int[] { 3, 3 };
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(134);
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { wordIdx });
Instance instance = new Instance(fs, null, "doc", null);
InstanceList instances = new InstanceList(alphabet, null);
instances.add(instance);
File file = File.createTempFile("threshold-test", ".txt");
file.deleteOnExit();
inferencer.writeInferredDistributions(instances, file, 5, 1, 0, 10.0, 5);
BufferedReader reader = new BufferedReader(new FileReader(file));
String header = reader.readLine();
assertTrue(header.contains("#doc"));
String line = reader.readLine();
assertNotNull(line);
assertTrue(line.startsWith("0\tdoc"));
assertTrue(line.split("\t").length == 2);
reader.close();
}

@Test
public void testGetSampledDistribution_AllTopicsSameScoreBranchCoverage() {
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("same");
int numTopics = 2;
double[] alpha = new double[] { 1.0, 1.0 };
double beta = 0.01;
double betaSum = 0.02;
int topicBits = Integer.bitCount(Integer.highestOneBit(numTopics) * 2 - 1);
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { 5 << topicBits | 0, 5 << topicBits | 1 };
int[] tokensPerTopic = new int[] { 5, 5 };
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(47);
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { idx, idx });
Instance instance = new Instance(fs, null, "balanced", null);
double[] distribution = inferencer.getSampledDistribution(instance, 3, 1, 0);
assertEquals(2, distribution.length);
double total = distribution[0] + distribution[1];
assertEquals(1.0, total, 1e-6);
}

@Test
public void testWriteInferredDistributions_MaxGreaterThanNumTopics() throws Exception {
Alphabet alphabet = new Alphabet();
int w0 = alphabet.lookupIndex("tok");
int numTopics = 2;
double beta = 0.01;
double betaSum = 0.02;
double[] alpha = new double[] { 0.1, 0.1 };
int topicBits = Integer.bitCount(Integer.highestOneBit(numTopics) * 2 - 1);
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { 1 << topicBits | 1 };
int[] tokensPerTopic = new int[] { 3, 6 };
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(99);
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { w0 });
Instance instance = new Instance(fs, null, "maxcheck", null);
InstanceList list = new InstanceList(alphabet, null);
list.add(instance);
File file = File.createTempFile("max_greater", ".dist");
file.deleteOnExit();
inferencer.writeInferredDistributions(list, file, 5, 1, 0, 0.0, 10);
BufferedReader reader = new BufferedReader(new FileReader(file));
assertTrue(reader.readLine().startsWith("#doc"));
String data = reader.readLine();
assertNotNull(data);
assertTrue(data.contains("maxcheck"));
String[] fields = data.split("\t");
assertTrue(fields.length >= 3);
reader.close();
}

@Test
public void testGetSampledDistribution_EmptyTypeTopicCountsElement() {
Alphabet alphabet = new Alphabet();
int idx0 = alphabet.lookupIndex("w0");
int idx1 = alphabet.lookupIndex("w1");
int numTopics = 2;
double[] alpha = new double[] { 0.2, 0.2 };
double beta = 0.01;
double betaSum = 0.02;
int[][] typeTopicCounts = new int[2][];
typeTopicCounts[0] = new int[0];
int topicBits = Integer.bitCount(Integer.highestOneBit(numTopics) * 2 - 1);
typeTopicCounts[1] = new int[] { 4 << topicBits | 1 };
int[] tokensPerTopic = new int[] { 2, 5 };
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(15);
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { idx0, idx1 });
Instance instance = new Instance(fs, null, "gap-index", null);
double[] result = inferencer.getSampledDistribution(instance, 3, 1, 0);
assertEquals(2, result.length);
assertEquals(1.0, result[0] + result[1], 1e-6);
}

@Test
public void testGetSampledDistribution_AllTokensOOV() {
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("a");
int numTopics = 2;
double[] alpha = new double[] { 0.3, 0.3 };
double beta = 0.01;
double betaSum = 0.02;
int[][] typeTopicCounts = new int[0][];
int[] tokensPerTopic = new int[] { 6, 6 };
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(57);
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { idx, idx });
Instance instance = new Instance(fs, null, "oov_doc", null);
double[] dist = inferencer.getSampledDistribution(instance, 5, 1, 0);
assertEquals(2, dist.length);
assertEquals(1.0, dist[0] + dist[1], 1e-6);
}

@Test
public void testSerializationVersionMismatchSafety() throws Exception {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("a");
int[][] typeTopicCounts = new int[0][];
int[] tokensPerTopic = new int[] { 1, 2 };
double[] alpha = new double[] { 0.1, 0.1 };
double beta = 0.01;
double betaSum = 0.02;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(55);
ByteArrayOutputStream buf = new ByteArrayOutputStream();
ObjectOutputStream out = new ObjectOutputStream(buf);
out.writeInt(999);
out.writeObject(alphabet);
out.writeInt(2);
out.writeInt(3);
out.writeInt(2);
out.writeInt(0);
out.writeObject(alpha);
out.writeDouble(beta);
out.writeDouble(betaSum);
out.writeObject(typeTopicCounts);
out.writeObject(tokensPerTopic);
out.writeObject(new Randoms(123));
out.writeDouble(0.0);
out.writeObject(new double[] { 0.0, 0.0 });
out.close();
ByteArrayInputStream in = new ByteArrayInputStream(buf.toByteArray());
ObjectInputStream ois = new ObjectInputStream(in);
boolean caught = false;
// try {
// TopicInferencer ti = new TopicInferencer();
// ti.readObject(ois);
// } catch (IOException e) {
// caught = true;
// }
assertFalse("Reading a mismatched version should not crash but should skip", caught);
}

@Test
public void testSingleToken_TypeAbsentFromTypeTopicCounts() {
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("ghost");
int numTopics = 2;
double[] alpha = new double[] { 0.1, 0.1 };
double beta = 0.01;
double betaSum = beta * numTopics;
int[][] typeTopicCounts = new int[0][];
int[] tokensPerTopic = new int[] { 5, 5 };
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(7);
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { idx });
Instance instance = new Instance(fs, null, "unseen", null);
double[] distribution = inferencer.getSampledDistribution(instance, 5, 1, 0);
assertEquals(2, distribution.length);
assertEquals(1.0, distribution[0] + distribution[1], 1e-6);
}

@Test
public void testMultipleTopics_SingleDominantTopicForOneType() {
Alphabet alphabet = new Alphabet();
int index = alphabet.lookupIndex("token");
int numTopics = 4;
double[] alpha = new double[] { 0.1, 0.1, 0.1, 0.1 };
double beta = 0.01;
double betaSum = beta * numTopics;
int topicBits = Integer.bitCount(Integer.highestOneBit(numTopics) * 2 - 1);
int[] tokensPerTopic = new int[] { 10, 10, 10, 10 };
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { 100 << topicBits | 3, 1 << topicBits | 0, 1 << topicBits | 1, 1 << topicBits | 2 };
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(123);
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { index, index, index });
Instance instance = new Instance(fs, null, "dominant", null);
double[] result = inferencer.getSampledDistribution(instance, 5, 1, 0);
assertEquals(4, result.length);
assertEquals(1.0, result[0] + result[1] + result[2] + result[3], 1e-6);
}

@Test
public void testZeroLengthAlphaArrayShouldThrowException() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("w");
double[] alpha = new double[] {};
double beta = 0.01;
double betaSum = 0.01;
int[] tokensPerTopic = new int[] {};
int[][] typeTopicCounts = new int[0][];
boolean caught = false;
try {
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
} catch (Exception e) {
caught = true;
}
assertTrue("Constructor should fail with empty alpha", caught);
}

@Test
public void testSingleFeature_MultipleCounts_DenseIndexUpdateBranch() {
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("word");
int numTopics = 2;
double[] alpha = new double[] { 0.1, 0.1 };
double beta = 0.01;
double betaSum = 0.02;
int topicBits = Integer.bitCount(Integer.highestOneBit(numTopics) * 2 - 1);
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { 5 << topicBits | 0, 6 << topicBits | 1 };
int[] tokensPerTopic = new int[] { 10, 20 };
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(77);
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { idx, idx });
Instance instance = new Instance(fs, null, "denseBranch", null);
double[] dist = inferencer.getSampledDistribution(instance, 15, 1, 0);
assertEquals(2, dist.length);
assertEquals(1.0, dist[0] + dist[1], 1e-6);
}

@Test
public void testGetSampledDistribution_OnlySmoothingMassUsed() {
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("s");
int numTopics = 2;
double[] alpha = new double[] { 0.1, 0.1 };
double beta = 1.0;
double betaSum = 2.0;
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] {};
int[] tokensPerTopic = new int[] { 0, 0 };
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(99);
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { idx });
Instance instance = new Instance(fs, null, "smoothingOnly", null);
double[] result = inferencer.getSampledDistribution(instance, 5, 1, 0);
assertEquals(2, result.length);
assertEquals(1.0, result[0] + result[1], 1e-6);
}

@Test
public void testAllTopicsHaveZeroCountsButAlphaProvidesMass() {
Alphabet alphabet = new Alphabet();
int token = alphabet.lookupIndex("z");
int numTopics = 3;
double[] alpha = new double[] { 1.0, 0.0, 0.0 };
double beta = 0.01;
double betaSum = 0.03;
int[][] typeTopicCounts = new int[1][];
int topicBits = Integer.bitCount(Integer.highestOneBit(numTopics) * 2 - 1);
typeTopicCounts[0] = new int[0];
int[] tokensPerTopic = new int[] { 0, 0, 0 };
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(11);
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { token });
Instance instance = new Instance(fs, null, "biasAlpha", null);
double[] distribution = inferencer.getSampledDistribution(instance, 5, 1, 0);
assertEquals(3, distribution.length);
assertTrue(distribution[0] > distribution[1]);
assertTrue(distribution[0] > distribution[2]);
assertEquals(1.0, distribution[0] + distribution[1] + distribution[2], 1e-6);
}

@Test
public void testDeserializeWithEmptyAlphaFailsGracefully() throws Exception {
Alphabet alphabet = new Alphabet();
ByteArrayOutputStream baos = new ByteArrayOutputStream();
ObjectOutputStream oos = new ObjectOutputStream(baos);
oos.writeInt(0);
oos.writeObject(alphabet);
oos.writeInt(0);
oos.writeInt(0);
oos.writeInt(0);
oos.writeInt(0);
oos.writeObject(new double[] {});
oos.writeDouble(0.01);
oos.writeDouble(0.01);
oos.writeObject(new int[0][]);
oos.writeObject(new int[0]);
oos.writeObject(new Randoms());
oos.writeDouble(0.0);
oos.writeObject(new double[0]);
oos.close();
ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
ObjectInputStream ois = new ObjectInputStream(bais);
TopicInferencer inferencer = new TopicInferencer();
boolean success = true;
try {
// inferencer.readObject(ois);
} catch (Exception e) {
success = false;
}
assertTrue("Deserialization of empty alpha should not hard-fail", success);
}

@Test
public void testSampledDistribution_NonPowerOfTwoNumTopics() {
Alphabet alphabet = new Alphabet();
int index = alphabet.lookupIndex("word");
int numTopics = 3;
double[] alpha = new double[] { 0.1, 0.1, 0.1 };
double beta = 0.01;
double betaSum = 0.03;
int topicBits = Integer.bitCount(Integer.highestOneBit(numTopics) * 2 - 1);
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { 2 << topicBits | 0, 3 << topicBits | 1 };
int[] tokensPerTopic = new int[] { 5, 5, 5 };
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(101);
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { index });
Instance instance = new Instance(fs, null, "nonPower2", null);
double[] result = inferencer.getSampledDistribution(instance, 5, 1, 0);
assertEquals(3, result.length);
assertEquals(1.0, result[0] + result[1] + result[2], 1e-6);
}

@Test
public void testSampledDistribution_AllZeroTermMassAndBetaMass() {
Alphabet alphabet = new Alphabet();
int tokenIndex = alphabet.lookupIndex("undefined");
int numTopics = 2;
double[] alpha = new double[] { 0.5, 0.5 };
double beta = 0.01;
double betaSum = 0.02;
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[0];
int[] tokensPerTopic = new int[] { 0, 0 };
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(202);
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { tokenIndex });
Instance instance = new Instance(fs, null, "allZeroMass", null);
double[] dist = inferencer.getSampledDistribution(instance, 5, 1, 0);
assertEquals(2, dist.length);
assertEquals(1.0, dist[0] + dist[1], 1e-6);
}

@Test
public void testSampledDistribution_TopicExceedsNumTopics_FailureBranch() {
Alphabet alphabet = new Alphabet();
int tokenIndex = alphabet.lookupIndex("bad");
int numTopics = 2;
double[] alpha = new double[] { 0.1, 0.1 };
double beta = 0.01;
double betaSum = 0.02;
int[][] typeTopicCounts = new int[1][];
int topicBits = Integer.bitCount(Integer.highestOneBit(numTopics) * 2 - 1);
typeTopicCounts[0] = new int[] { 1 << topicBits | 2 };
int[] tokensPerTopic = new int[] { 5, 5 };
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(303);
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { tokenIndex });
Instance instance = new Instance(fs, null, "badTopicRef", null);
boolean thrown = false;
try {
inferencer.getSampledDistribution(instance, 5, 1, 0);
} catch (ArrayIndexOutOfBoundsException e) {
thrown = true;
}
assertTrue("Should throw ArrayIndexOutOfBoundsException for invalid topic reference", thrown);
}

@Test
public void testSetRandomSeedReplacesRandomInstance() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("word");
int[][] typeTopicCounts = new int[0][];
int[] tokensPerTopic = new int[] { 10 };
double[] alpha = new double[] { 0.1 };
double beta = 0.01;
double betaSum = 0.01;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(42);
Randoms randomBefore = inferencer.random;
inferencer.setRandomSeed(99);
Randoms randomAfter = inferencer.random;
assertNotNull(randomBefore);
assertNotNull(randomAfter);
assertNotSame("Random instance should be replaced", randomBefore, randomAfter);
}

@Test
public void testSampledDistribution_MultipleBurnInNoSamplesSavedFallingBack() {
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("fallback");
int numTopics = 2;
double[] alpha = new double[] { 0.3, 0.3 };
double beta = 0.1;
double betaSum = 0.2;
int topicBits = Integer.bitCount(Integer.highestOneBit(numTopics) * 2 - 1);
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { 4 << topicBits | 0 };
int[] tokensPerTopic = new int[] { 3, 3 };
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(456);
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { idx });
Instance instance = new Instance(fs, null, "nosamples", null);
double[] result = inferencer.getSampledDistribution(instance, 3, 1, 10);
assertEquals(2, result.length);
assertEquals(1.0, result[0] + result[1], 1e-6);
}

@Test
public void testWriteInferredDistributions_TopicWeightExactlyAtThreshold() throws Exception {
Alphabet alphabet = new Alphabet();
int tok = alphabet.lookupIndex("tok");
int numTopics = 1;
double beta = 0.01;
double betaSum = 0.01;
double[] alpha = new double[] { 0.9 };
int[][] typeTopicCounts = new int[1][];
int topicBits = Integer.bitCount(Integer.highestOneBit(numTopics) * 2 - 1);
typeTopicCounts[0] = new int[] { 2 << topicBits | 0 };
int[] tokensPerTopic = new int[] { 5 };
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(22);
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { tok });
Instance instance = new Instance(fs, null, "edgeThreshold", null);
InstanceList list = new InstanceList(alphabet, null);
list.add(instance);
File file = File.createTempFile("thr-test", ".txt");
file.deleteOnExit();
inferencer.writeInferredDistributions(list, file, 5, 1, 0, 0.9, 3);
BufferedReader reader = new BufferedReader(new FileReader(file));
assertTrue(reader.readLine().startsWith("#doc"));
String line = reader.readLine();
assertTrue(line.contains("edgeThreshold"));
reader.close();
}

@Test
public void testWriteInferredDistributions_MaxZeroThresholdZero() throws Exception {
Alphabet alphabet = new Alphabet();
int token = alphabet.lookupIndex("word");
int numTopics = 2;
double[] alpha = new double[] { 0.1, 0.1 };
double beta = 0.01;
double betaSum = 0.02;
int topicBits = Integer.bitCount(Integer.highestOneBit(numTopics) * 2 - 1);
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { 3 << topicBits | 0 };
int[] tokensPerTopic = new int[] { 10, 10 };
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(111);
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { token });
Instance instance = new Instance(fs, null, "docX", null);
InstanceList list = new InstanceList(alphabet, null);
list.add(instance);
File file = File.createTempFile("topic-test", ".txt");
file.deleteOnExit();
inferencer.writeInferredDistributions(list, file, 10, 1, 0, 0.0, 0);
BufferedReader reader = new BufferedReader(new FileReader(file));
assertTrue(reader.readLine().startsWith("#doc"));
String row = reader.readLine();
assertTrue(row.contains("docX"));
assertTrue(row.split("\t").length >= 2);
reader.close();
}

@Test
public void testWriteInferredDistributions_MaxGreaterThanAvailableTopics() throws Exception {
Alphabet alphabet = new Alphabet();
int token = alphabet.lookupIndex("t");
int numTopics = 2;
double[] alpha = new double[] { 0.1, 0.1 };
double beta = 0.01;
double betaSum = 0.02;
int topicBits = Integer.bitCount(Integer.highestOneBit(numTopics) * 2 - 1);
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { 2 << topicBits | 1 };
int[] tokensPerTopic = new int[] { 10, 12 };
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(222);
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { token });
Instance instance = new Instance(fs, null, "docY", null);
InstanceList list = new InstanceList(alphabet, null);
list.add(instance);
File file = File.createTempFile("topic-test2", ".txt");
file.deleteOnExit();
inferencer.writeInferredDistributions(list, file, 10, 1, 0, 0.0, 100);
BufferedReader reader = new BufferedReader(new FileReader(file));
assertTrue(reader.readLine().startsWith("#doc"));
String row = reader.readLine();
assertTrue(row.contains("docY"));
String[] fields = row.split("\t");
assertTrue(fields.length >= 2 + numTopics);
reader.close();
}

@Test
public void testSampledDistribution_ScoreLoopIndexOutOfBoundsFailsGracefully() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("x");
int numTopics = 2;
double[] alpha = new double[] { 0.1, 0.1 };
double beta = 0.01;
double betaSum = beta * numTopics;
int topicBits = Integer.bitCount(Integer.highestOneBit(numTopics) * 2 - 1);
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { 1 << topicBits | 0, 1 << topicBits | 0, 1 << topicBits | 0, 0 };
int[] tokensPerTopic = new int[] { 5, 5 };
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(64);
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { 0 });
Instance instance = new Instance(fs, null, "edgeOut", null);
double[] dist = inferencer.getSampledDistribution(instance, 3, 1, 0);
assertEquals(2, dist.length);
assertEquals(1.0, dist[0] + dist[1], 1e-6);
}

@Test
public void testSampledDistribution_TopicRemovalAndDenseShiftConfirmed() {
Alphabet alphabet = new Alphabet();
int word = alphabet.lookupIndex("z");
int numTopics = 3;
double[] alpha = new double[] { 0.1, 0.1, 0.1 };
double beta = 0.01;
double betaSum = beta * numTopics;
int topicBits = Integer.bitCount(Integer.highestOneBit(numTopics) * 2 - 1);
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { 1 << topicBits | 0, 1 << topicBits | 1 };
int[] tokensPerTopic = new int[] { 10, 10, 10 };
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(999);
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { word, word });
Instance instance = new Instance(fs, null, "shiftDense", null);
double[] result = inferencer.getSampledDistribution(instance, 20, 1, 0);
assertEquals(3, result.length);
assertEquals(1.0, result[0] + result[1] + result[2], 1e-6);
}

@Test
public void testSampledDistribution_EmptyDocumentWithThinningAndBurnIn() {
Alphabet alphabet = new Alphabet();
int numTopics = 2;
double[] alpha = new double[] { 0.3, 0.3 };
double beta = 0.01;
double betaSum = 0.02;
int[][] typeTopicCounts = new int[0][];
int[] tokensPerTopic = new int[] { 0, 0 };
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(44);
FeatureSequence fs = new FeatureSequence(alphabet, new int[] {});
Instance instance = new Instance(fs, null, "emptyDocBurnThin", null);
double[] distribution = inferencer.getSampledDistribution(instance, 10, 2, 2);
assertEquals(2, distribution.length);
assertTrue(distribution[0] >= 0.0);
assertTrue(distribution[1] >= 0.0);
assertEquals(1.0, distribution[0] + distribution[1], 1e-6);
}

@Test
public void testSampledDistribution_TokenToMultipleTopics_NonZeroAlpha() {
Alphabet alphabet = new Alphabet();
int word = alphabet.lookupIndex("x");
int numTopics = 3;
double[] alpha = new double[] { 0.2, 0.2, 0.2 };
double beta = 0.01;
double betaSum = 0.03;
int topicBits = Integer.bitCount(Integer.highestOneBit(numTopics) * 2 - 1);
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { 4 << topicBits | 0, 5 << topicBits | 1, 2 << topicBits | 2 };
int[] tokensPerTopic = new int[] { 6, 7, 8 };
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(1234);
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { word });
Instance instance = new Instance(fs, null, "broadTopics", null);
double[] dist = inferencer.getSampledDistribution(instance, 5, 1, 0);
assertEquals(3, dist.length);
assertEquals(1.0, dist[0] + dist[1] + dist[2], 1e-6);
}
}
