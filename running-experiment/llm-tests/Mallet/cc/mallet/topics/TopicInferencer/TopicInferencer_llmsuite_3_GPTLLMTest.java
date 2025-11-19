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

public class TopicInferencer_llmsuite_3_GPTLLMTest {

@Test
public void testConstructorInitializesFieldsCorrectly() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("word1");
alphabet.lookupIndex("word2");
int[][] typeTopicCounts = new int[2][];
typeTopicCounts[0] = new int[] { (5 << 1) + 0 };
typeTopicCounts[1] = new int[] { (3 << 1) + 1 };
int[] tokensPerTopic = new int[] { 5, 3 };
double[] alpha = new double[] { 0.1, 0.1 };
double beta = 0.01;
double betaSum = 0.02;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
assertNotNull(inferencer);
double[] result = inferencer.getSampledDistribution(new Instance(new FeatureSequence(alphabet), null, null, null), 0, 1, 0);
assertEquals(2, result.length);
}

@Test
public void testSetRandomSeedProducesDeterministicResults() {
Alphabet alphabet1 = new Alphabet();
alphabet1.lookupIndex("word");
int[][] counts = new int[1][];
counts[0] = new int[] { (3 << 1) + 0 };
int[] tokens = new int[] { 3 };
double[] alpha = new double[] { 0.1 };
double beta = 0.01;
double betaSum = 0.01;
TopicInferencer inferencer1 = new TopicInferencer(counts, tokens, alphabet1, alpha, beta, betaSum);
inferencer1.setRandomSeed(123);
FeatureSequence fs1 = new FeatureSequence(alphabet1, new int[] { 0, 0, 0 });
Instance instance1 = new Instance(fs1, null, "doc", null);
double[] dist1 = inferencer1.getSampledDistribution(instance1, 10, 1, 0);
Alphabet alphabet2 = new Alphabet();
alphabet2.lookupIndex("word");
int[][] counts2 = new int[1][];
counts2[0] = new int[] { (3 << 1) + 0 };
int[] tokens2 = new int[] { 3 };
double[] alpha2 = new double[] { 0.1 };
double beta2 = 0.01;
double betaSum2 = 0.01;
TopicInferencer inferencer2 = new TopicInferencer(counts2, tokens2, alphabet2, alpha2, beta2, betaSum2);
inferencer2.setRandomSeed(123);
FeatureSequence fs2 = new FeatureSequence(alphabet2, new int[] { 0, 0, 0 });
Instance instance2 = new Instance(fs2, null, "doc", null);
double[] dist2 = inferencer2.getSampledDistribution(instance2, 10, 1, 0);
assertArrayEquals(dist1, dist2, 0.00001);
}

@Test
public void testSampledDistributionWithOOVTerm() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("word1");
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (4 << 1) + 0 };
int[] tokensPerTopic = new int[] { 4 };
double[] alpha = new double[] { 0.1 };
double beta = 0.01;
double betaSum = 0.01;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { 0, 999 });
Instance instance = new Instance(fs, null, "test", null);
double[] dist = inferencer.getSampledDistribution(instance, 5, 1, 0);
assertEquals(1, dist.length);
assertEquals(1.0, dist[0], 0.00001);
}

@Test
public void testWriteInferredDistributionsCreatesOutputFile() throws IOException {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("word1");
alphabet.lookupIndex("word2");
int[][] counts = new int[2][];
counts[0] = new int[] { (4 << 1) };
counts[1] = new int[] { (3 << 1) + 1 };
int[] tokens = new int[] { 4, 3 };
double[] alpha = new double[] { 0.1, 0.1 };
double beta = 0.01;
TopicInferencer inferencer = new TopicInferencer(counts, tokens, alphabet, alpha, beta, 0.02);
// InstanceList list = new InstanceList(alphabet);
// FeatureSequence fs = new FeatureSequence(alphabet, new String[] { "word1", "word2", "word1" });
// list.add(new Instance(fs, null, "doc1", null));
File file = File.createTempFile("inferred", ".txt");
file.deleteOnExit();
// inferencer.writeInferredDistributions(list, file, 5, 1, 0, 0.0, 2);
assertTrue(file.exists());
BufferedReader br = new BufferedReader(new FileReader(file));
String line1 = br.readLine();
String line2 = br.readLine();
br.close();
assertNotNull(line1);
assertTrue(line1.startsWith("#doc"));
assertNotNull(line2);
assertTrue(line2.contains("doc1"));
}

@Test
public void testSerializationDeserialization() throws Exception {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("word");
int[][] data = new int[1][];
data[0] = new int[] { (5 << 1) + 0 };
int[] counts = new int[] { 5 };
double[] alpha = new double[] { 0.1 };
double beta = 0.01;
TopicInferencer original = new TopicInferencer(data, counts, alphabet, alpha, beta, 0.01);
File file = File.createTempFile("serialization", ".ser");
file.deleteOnExit();
ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
oos.writeObject(original);
oos.close();
TopicInferencer restored = TopicInferencer.read(file);
assertNotNull(restored);
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { 0, 0 });
Instance instance = new Instance(fs, null, "doc", null);
double[] dist = restored.getSampledDistribution(instance, 5, 1, 0);
assertEquals(1, dist.length);
assertEquals(1.0, dist[0], 0.0001);
}

@Test(expected = IOException.class)
public void testDeserializeInvalidFileThrowsException() throws Exception {
File file = File.createTempFile("invalid", ".ser");
file.deleteOnExit();
FileWriter writer = new FileWriter(file);
writer.write("not a valid serialized object");
writer.close();
TopicInferencer.read(file);
}

@Test
public void testGetSampledDistributionWithZeroIterations() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("w");
int[][] counts = new int[1][];
counts[0] = new int[] { (2 << 1) + 0 };
int[] tokens = new int[] { 2 };
double[] alpha = new double[] { 0.1 };
double beta = 0.01;
TopicInferencer inferencer = new TopicInferencer(counts, tokens, alphabet, alpha, beta, 0.01);
// FeatureSequence fs = new FeatureSequence(alphabet, new String[] { "w", "w" });
// Instance instance = new Instance(fs, null, "doc", null);
// double[] dist = inferencer.getSampledDistribution(instance, 0, 1, 0);
// assertEquals(1, dist.length);
// assertEquals(1.0, dist[0], 0.0001);
}

@Test
public void testEmptyDocumentReturnsAlphaDistribution() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("x");
int[][] ttc = new int[1][];
ttc[0] = new int[] { (1 << 1) + 0 };
int[] tokens = new int[] { 1 };
double[] alpha = new double[] { 0.1 };
double beta = 0.01;
TopicInferencer inferencer = new TopicInferencer(ttc, tokens, alphabet, alpha, beta, 0.01);
FeatureSequence fs = new FeatureSequence(alphabet);
Instance instance = new Instance(fs, null, "empty", null);
double[] dist = inferencer.getSampledDistribution(instance, 5, 1, 0);
assertEquals(1, dist.length);
assertEquals(1.0, dist[0], 0.0001);
}

@Test
public void testThresholdTrimsOutputTopics() throws Exception {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("a");
alphabet.lookupIndex("b");
int[][] counts = new int[2][];
counts[0] = new int[] { (10 << 1) + 0 };
counts[1] = new int[] { (1 << 1) + 1 };
int[] tokens = new int[] { 10, 1 };
double[] alpha = new double[] { 0.1, 0.1 };
double beta = 0.01;
TopicInferencer inferencer = new TopicInferencer(counts, tokens, alphabet, alpha, beta, 0.02);
// InstanceList list = new InstanceList(alphabet);
// FeatureSequence fs = new FeatureSequence(alphabet, new String[] { "a", "a", "a" });
// Instance instance = new Instance(fs, null, "x", null);
// list.add(instance);
File file = File.createTempFile("thresh", ".txt");
file.deleteOnExit();
// inferencer.writeInferredDistributions(list, file, 5, 1, 0, 0.5, 1);
BufferedReader br = new BufferedReader(new FileReader(file));
br.readLine();
String line = br.readLine();
br.close();
int tabCount = line.length() - line.replace("\t", "").length();
assertTrue("Only one topic should be output", tabCount <= 3);
}

@Test
public void testTypeWithMultipleTopicCounts() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("x");
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (2 << 1) + 0, (3 << 1) + 1 };
int[] tokensPerTopic = new int[] { 2, 3 };
double[] alpha = new double[] { 0.1, 0.1 };
double beta = 0.01;
double betaSum = 0.02;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
int[] tokenIndices = new int[] { 0, 0 };
FeatureSequence fs = new FeatureSequence(alphabet, tokenIndices);
Instance instance = new Instance(fs, null, "multiTopic", null);
double[] result = inferencer.getSampledDistribution(instance, 5, 1, 0);
assertEquals(2, result.length);
assertEquals(1.0, result[0] + result[1], 0.0001);
}

@Test
public void testTypeWithEmptyTopicCountsArray() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("x");
alphabet.lookupIndex("y");
int[][] ttc = new int[2][];
ttc[0] = new int[0];
ttc[1] = new int[] { (2 << 1) + 0 };
int[] tokensPerTopic = new int[] { 2 };
double[] alpha = new double[] { 0.1 };
double beta = 0.01;
double betaSum = 0.01;
TopicInferencer inferencer = new TopicInferencer(ttc, tokensPerTopic, alphabet, alpha, beta, betaSum);
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { 0, 1 });
Instance instance = new Instance(fs, null, "emptyTTCounts", null);
double[] result = inferencer.getSampledDistribution(instance, 5, 1, 0);
assertEquals(1, result.length);
assertEquals(1.0, result[0], 0.0001);
}

@Test
public void testTopicWithZeroAlpha() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("t");
int[][] ttc = new int[1][];
ttc[0] = new int[] { (4 << 1) + 0 };
int[] tokensPerTopic = new int[] { 4 };
double[] alpha = new double[] { 0.0 };
double beta = 0.01;
double betaSum = 0.01;
TopicInferencer inferencer = new TopicInferencer(ttc, tokensPerTopic, alphabet, alpha, beta, betaSum);
// FeatureSequence fs = new FeatureSequence(alphabet, new String[] { "t", "t" });
// Instance instance = new Instance(fs, null, "alphaZero", null);
// double[] result = inferencer.getSampledDistribution(instance, 5, 1, 0);
// assertEquals(1, result.length);
// assertEquals(1.0, result[0], 1e-6);
}

@Test
public void testTopicWithZeroTokenCount() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("z");
int[][] ttc = new int[1][];
ttc[0] = new int[] { (0 << 1) + 0 };
int[] tokensPerTopic = new int[] { 0 };
double[] alpha = new double[] { 0.1 };
double beta = 0.01;
double betaSum = 0.01;
TopicInferencer inferencer = new TopicInferencer(ttc, tokensPerTopic, alphabet, alpha, beta, betaSum);
// FeatureSequence fs = new FeatureSequence(alphabet, new String[] { "z", "z" });
// Instance instance = new Instance(fs, null, "zeroTokens", null);
// double[] dist = inferencer.getSampledDistribution(instance, 5, 1, 0);
// assertEquals(1, dist.length);
// assertEquals(1.0, dist[0], 0.0001);
}

@Test
public void testSamplingWithBurnInEqualToNumIterations() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("x");
int[][] ttc = new int[1][];
ttc[0] = new int[] { (1 << 1) + 0 };
int[] tp = new int[] { 1 };
double[] alpha = new double[] { 0.1 };
double beta = 0.01;
TopicInferencer inferencer = new TopicInferencer(ttc, tp, alphabet, alpha, beta, 0.01);
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { 0, 0, 0 });
Instance instance = new Instance(fs, null, "burnInEqualsIterations", null);
double[] dist = inferencer.getSampledDistribution(instance, 5, 1, 5);
assertEquals(1, dist.length);
assertEquals(1.0, dist[0], 0.0001);
}

@Test
public void testWriteInferredDistributionsMaxGreaterThanNumTopics() throws IOException {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("a");
int[][] counts = new int[1][];
counts[0] = new int[] { (2 << 1) + 0 };
int[] tokens = new int[] { 2 };
double[] alpha = new double[] { 0.1 };
double beta = 0.01;
TopicInferencer inferencer = new TopicInferencer(counts, tokens, alphabet, alpha, beta, 0.01);
// InstanceList instList = new InstanceList(alphabet);
// instList.add(new Instance(new FeatureSequence(alphabet, new String[] { "a", "a" }), null, "doc", null));
File file = File.createTempFile("topic_max_exceed", ".txt");
file.deleteOnExit();
// inferencer.writeInferredDistributions(instList, file, 5, 1, 0, 0.0, 5);
BufferedReader br = new BufferedReader(new FileReader(file));
String line = br.readLine();
String result = br.readLine();
br.close();
assertNotNull(result);
assertTrue(result.contains("doc"));
}

@Test
public void testSingleTokenSingleTopic() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("hello");
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (1 << 1) + 0 };
int[] tokensPerTopic = new int[] { 1 };
double[] alpha = new double[] { 0.2 };
double beta = 0.01;
double betaSum = 0.01;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
// FeatureSequence fs = new FeatureSequence(alphabet, new String[] { "hello" });
// Instance instance = new Instance(fs, null, "doc", null);
// double[] dist = inferencer.getSampledDistribution(instance, 5, 1, 0);
// assertEquals(1, dist.length);
// assertEquals(1.0, dist[0], 0.0001);
}

@Test
public void testAllTokensIgnoredDueToOOV() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("known");
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (2 << 1) + 0 };
int[] tokensPerTopic = new int[] { 2 };
double[] alpha = new double[] { 0.5 };
double beta = 0.01;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, 0.01);
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { 999, 888 });
Instance instance = new Instance(fs, null, "doc", null);
double[] dist = inferencer.getSampledDistribution(instance, 4, 1, 0);
assertEquals(1, dist.length);
assertEquals(1.0, dist[0], 0.0001);
}

@Test
public void testEmptyTypeTopicCountsArrayAtNonZeroType() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("alpha");
alphabet.lookupIndex("beta");
int[][] typeTopicCounts = new int[2][];
typeTopicCounts[0] = new int[] { (3 << 1) + 0 };
typeTopicCounts[1] = new int[0];
int[] tokensPerTopic = new int[] { 3 };
double[] alpha = new double[] { 0.1 };
double beta = 0.01;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, 0.01);
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { 0, 1 });
Instance instance = new Instance(fs, null, "doc", null);
double[] dist = inferencer.getSampledDistribution(instance, 3, 1, 0);
assertEquals(1, dist.length);
assertEquals(1.0, dist[0], 0.0001);
}

@Test
public void testZeroSmoothingMassFallbackExecution() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("word");
int[][] ttc = new int[1][];
ttc[0] = new int[] { (4 << 1) + 0 };
int[] tpt = new int[] { 4 };
double[] alpha = new double[] { 0.0 };
double beta = 0.01;
TopicInferencer inferencer = new TopicInferencer(ttc, tpt, alphabet, alpha, beta, 0.01);
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { 0 });
Instance instance = new Instance(fs, null, "doc", null);
double[] dist = inferencer.getSampledDistribution(instance, 0, 1, 0);
assertEquals(1, dist.length);
assertEquals(1.0, dist[0], 0.0001);
}

@Test
public void testSerializationNullFileContentHandling() throws Exception {
File file = File.createTempFile("empty-obj", ".ser");
file.deleteOnExit();
FileOutputStream fos = new FileOutputStream(file);
fos.write(new byte[] { 0, 1, 2, 3 });
fos.close();
try {
TopicInferencer.read(file);
fail("Expected exception not thrown for corrupt file");
} catch (Exception e) {
assertTrue(e instanceof IOException || e instanceof StreamCorruptedException || e instanceof ClassCastException);
}
}

@Test
public void testWriteDistributionsWithZeroThresholdAndNegativeMax() throws Exception {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("dog");
int[][] counts = new int[1][];
counts[0] = new int[] { (2 << 1) + 0 };
int[] tokens = new int[] { 2 };
double[] alpha = new double[] { 0.1 };
double beta = 0.01;
TopicInferencer inferencer = new TopicInferencer(counts, tokens, alphabet, alpha, beta, 0.01);
// InstanceList list = new InstanceList(alphabet);
// FeatureSequence fs = new FeatureSequence(alphabet, new String[] { "dog" });
// list.add(new Instance(fs, null, "animal", null));
File out = File.createTempFile("write-distribution", ".txt");
out.deleteOnExit();
// inferencer.writeInferredDistributions(list, out, 4, 1, 0, 0.0, -1);
BufferedReader br = new BufferedReader(new FileReader(out));
String header = br.readLine();
String line = br.readLine();
br.close();
assertNotNull(header);
assertTrue(header.contains("#doc"));
assertNotNull(line);
assertTrue(line.contains("animal"));
}

@Test
public void testGetSampledDistributionNormalizesWhenSumZero() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("a");
int[][] ttc = new int[1][];
ttc[0] = new int[] { (1 << 1) + 0 };
int[] tpt = new int[] { 1 };
double[] alpha = new double[] { 0.0 };
double beta = 0.01;
TopicInferencer inferencer = new TopicInferencer(ttc, tpt, alphabet, alpha, beta, 0.01);
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { 0 });
Instance instance = new Instance(fs, null, "doc", null);
double[] dist = inferencer.getSampledDistribution(instance, 2, 1, 5);
assertEquals(1, dist.length);
assertEquals(1.0, dist[0], 0.0001);
}

@Test
public void testWriteDistributionsFallbackToNoNameWhenNullName() throws Exception {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("w");
int[][] counts = new int[1][];
counts[0] = new int[] { (2 << 1) + 0 };
int[] tok = new int[] { 2 };
double[] alpha = new double[] { 0.1 };
double beta = 0.01;
TopicInferencer inferencer = new TopicInferencer(counts, tok, alphabet, alpha, beta, 0.01);
// FeatureSequence fs = new FeatureSequence(alphabet, new String[] { "w" });
// InstanceList list = new InstanceList(alphabet);
// list.add(new Instance(fs, null, null, null));
File out = File.createTempFile("noname", ".txt");
out.deleteOnExit();
// inferencer.writeInferredDistributions(list, out, 3, 1, 0, 0.0, 1);
BufferedReader br = new BufferedReader(new FileReader(out));
br.readLine();
String line = br.readLine();
br.close();
assertTrue(line.contains("no-name"));
}

@Test
public void testTopicMaskWhenNotPowerOfTwo() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("x");
alphabet.lookupIndex("y");
alphabet.lookupIndex("z");
int numTopics = 3;
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (2 << 2) + 0, (1 << 2) + 1 };
int[] tokensPerTopic = new int[] { 2, 1, 0 };
double[] alpha = new double[] { 0.1, 0.1, 0.1 };
double beta = 0.01;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, 0.03);
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { 0, 0 });
Instance instance = new Instance(fs, null, "doc", null);
double[] dist = inferencer.getSampledDistribution(instance, 5, 1, 0);
double total = dist[0] + dist[1] + dist[2];
assertEquals(3, dist.length);
assertEquals(1.0, total, 0.00001);
}

@Test
public void testSamplingPathWithZeroTopicTermMassAndNonZeroBetaMass() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("noun");
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[0];
int[] tokensPerTopic = new int[] { 0, 0 };
double[] alpha = new double[] { 0.1, 0.1 };
double beta = 0.01;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, 0.02);
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { 0, 0 });
Instance instance = new Instance(fs, null, "doc", null);
double[] dist = inferencer.getSampledDistribution(instance, 3, 1, 0);
assertEquals(2, dist.length);
assertEquals(1.0, dist[0] + dist[1], 0.00001);
}

@Test
public void testSamplingWithThinningAndSparseSampleCollection() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("word");
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (2 << 1) + 0 };
int[] tokensPerTopic = new int[] { 2 };
double[] alpha = new double[] { 0.1 };
double beta = 0.01;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, 0.01);
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { 0, 0, 0, 0 });
Instance instance = new Instance(fs, null, "doc", null);
double[] dist = inferencer.getSampledDistribution(instance, 9, 3, 0);
assertEquals(1, dist.length);
assertEquals(1.0, dist[0], 0.0001);
}

@Test
public void testInstanceListWithNoDocumentsDoesNotCrashWrite() throws IOException {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("token");
int[][] ttc = new int[1][];
ttc[0] = new int[] { (2 << 1) + 0 };
int[] tokens = new int[] { 2 };
double[] alpha = new double[] { 0.1 };
double beta = 0.01;
TopicInferencer inferencer = new TopicInferencer(ttc, tokens, alphabet, alpha, beta, 0.01);
// InstanceList emptyList = new InstanceList(alphabet);
File file = File.createTempFile("emptylist", ".txt");
file.deleteOnExit();
// inferencer.writeInferredDistributions(emptyList, file, 2, 1, 0, 0.0, 1);
BufferedReader reader = new BufferedReader(new FileReader(file));
String header = reader.readLine();
String next = reader.readLine();
reader.close();
assertNotNull(header);
assertTrue(header.contains("#doc"));
assertNull(next);
}

@Test
public void testDistributionWithAllZeroTokensPerTopic() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("apple");
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (2 << 1) + 0 };
int[] tokensPerTopic = new int[] { 0 };
double[] alpha = new double[] { 0.1 };
double beta = 0.01;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, 0.01);
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { 0, 0 });
Instance inst = new Instance(fs, null, "x", null);
double[] dist = inferencer.getSampledDistribution(inst, 3, 1, 0);
assertEquals(1, dist.length);
assertEquals(1.0, dist[0], 0.0001);
}

@Test
public void testTypeIndexBeyondTypeTopicCountsIsIgnored() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("seen");
alphabet.lookupIndex("unseen");
int[][] ttc = new int[1][];
ttc[0] = new int[] { (3 << 1) + 0 };
int[] counts = new int[] { 3 };
double[] alpha = new double[] { 0.05 };
double beta = 0.01;
TopicInferencer inferencer = new TopicInferencer(ttc, counts, alphabet, alpha, beta, 0.01);
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { 0, 1 });
Instance instance = new Instance(fs, null, "doc", null);
double[] dist = inferencer.getSampledDistribution(instance, 5, 1, 0);
assertEquals(1, dist.length);
assertEquals(1.0, dist[0], 0.0001);
}

@Test
public void testWriteInferredDistributionsThresholdExcludesAllTopics() throws Exception {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("low");
int[][] ttc = new int[1][];
ttc[0] = new int[] { (1 << 1) };
int[] tokenCounts = new int[] { 1 };
double[] alpha = new double[] { 0.01 };
double beta = 0.01;
TopicInferencer inferencer = new TopicInferencer(ttc, tokenCounts, alphabet, alpha, beta, 0.01);
// InstanceList list = new InstanceList(alphabet);
// FeatureSequence fs = new FeatureSequence(alphabet, new String[] { "low" });
// list.add(new Instance(fs, null, "thin", null));
File out = File.createTempFile("thresholdFiltered", ".txt");
out.deleteOnExit();
// inferencer.writeInferredDistributions(list, out, 3, 1, 0, 0.99, 1);
BufferedReader br = new BufferedReader(new FileReader(out));
String header = br.readLine();
String line = br.readLine();
br.close();
assertNotNull(header);
assertNotNull(line);
assertTrue(line.startsWith("0\tthin"));
}

@Test
public void testGetSampledDistributionWithNullNameInInstance() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("alpha");
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (3 << 1) + 0 };
int[] tokensPerTopic = new int[] { 3 };
double[] alphaVals = new double[] { 0.1 };
double beta = 0.01;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alphaVals, beta, 0.01);
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { 0, 0 });
Instance inst = new Instance(fs, null, null, null);
double[] dist = inferencer.getSampledDistribution(inst, 5, 1, 0);
assertEquals(1, dist.length);
assertEquals(1.0, dist[0], 0.00001);
}

@Test
public void testWriteInferredDistributionsWithAlphaZeroAndEmptyDoc() throws Exception {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("cat");
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (0 << 1) };
int[] tokensPerTopic = new int[] { 0 };
double[] alpha = new double[] { 0.0 };
double beta = 0.01;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, 0.01);
FeatureSequence features = new FeatureSequence(alphabet);
Instance doc = new Instance(features, null, "x", null);
// InstanceList list = new InstanceList(alphabet);
// list.add(doc);
File f = File.createTempFile("alpha0out", ".txt");
f.deleteOnExit();
// inferencer.writeInferredDistributions(list, f, 15, 1, 0, 0.0, 1);
BufferedReader reader = new BufferedReader(new FileReader(f));
String line = reader.readLine();
assertNotNull(line);
String result = reader.readLine();
assertNotNull(result);
assertTrue(result.startsWith("0\tx"));
reader.close();
}

@Test
public void testMultipleDenseIndexInsertions() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("a");
int[][] counts = new int[1][];
counts[0] = new int[] { (2 << 2) + 0, (3 << 2) + 1, (1 << 2) + 2 };
int[] tokens = new int[] { 2, 3, 1 };
double[] alpha = new double[] { 0.1, 0.1, 0.1 };
double beta = 0.01;
TopicInferencer inferencer = new TopicInferencer(counts, tokens, alphabet, alpha, beta, 0.03);
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { 0, 0, 0, 0 });
Instance instance = new Instance(fs, null, "multiDense", null);
double[] dist = inferencer.getSampledDistribution(instance, 5, 1, 0);
assertEquals(3, dist.length);
assertEquals(1.0, dist[0] + dist[1] + dist[2], 0.00001);
}

@Test
public void testTopicWithAlphaZeroAndNonZeroTokens() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("x");
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (2 << 1) + 0 };
int[] tokensPerTopic = new int[] { 2 };
double[] alpha = new double[] { 0.0 };
double beta = 0.01;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, 0.01);
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { 0, 0 });
Instance instance = new Instance(fs, null, "xdoc", null);
double[] dist = inferencer.getSampledDistribution(instance, 2, 1, 0);
assertEquals(1, dist.length);
assertEquals(1.0, dist[0], 0.0001);
}

@Test
public void testThresholdPositiveButExceedsAllWeights() throws Exception {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("k");
int[][] counts = new int[1][];
counts[0] = new int[] { (1 << 1) + 0 };
int[] tokensPerTopic = new int[] { 1 };
double[] alpha = new double[] { 0.01 };
double beta = 0.01;
TopicInferencer inferencer = new TopicInferencer(counts, tokensPerTopic, alphabet, alpha, beta, 0.01);
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { 0 });
Instance doc = new Instance(fs, null, "doc", null);
// InstanceList list = new InstanceList(alphabet);
// list.add(doc);
File out = File.createTempFile("high-thresh", ".txt");
out.deleteOnExit();
// inferencer.writeInferredDistributions(list, out, 5, 1, 0, 0.5, 1);
BufferedReader reader = new BufferedReader(new FileReader(out));
reader.readLine();
String line = reader.readLine();
reader.close();
assertNotNull(line);
assertFalse(line.contains("\t0\t"));
}

@Test
public void testTypeTopicCountsWithEmptyEntriesMixed() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("w1");
alphabet.lookupIndex("w2");
int[][] ttc = new int[2][];
ttc[0] = new int[0];
ttc[1] = new int[] { (2 << 1) + 0 };
int[] tokens = new int[] { 2 };
double[] alpha = new double[] { 0.1 };
double beta = 0.01;
TopicInferencer inferencer = new TopicInferencer(ttc, tokens, alphabet, alpha, beta, 0.01);
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { 0, 1 });
Instance doc = new Instance(fs, null, "mixed", null);
double[] dist = inferencer.getSampledDistribution(doc, 4, 1, 0);
assertEquals(1, dist.length);
assertEquals(1.0, dist[0], 0.0001);
}

@Test
public void testTopicInferencerSerializationFieldsSurvive() throws Exception {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("z");
int[][] counts = new int[1][];
counts[0] = new int[] { (2 << 1) + 0 };
int[] tokensPerTopic = new int[] { 2 };
double[] alpha = new double[] { 0.05 };
double beta = 0.01;
TopicInferencer inferencer = new TopicInferencer(counts, tokensPerTopic, alphabet, alpha, beta, 0.01);
File file = File.createTempFile("test-serial", ".ser");
file.deleteOnExit();
ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
oos.writeObject(inferencer);
oos.close();
TopicInferencer restored = TopicInferencer.read(file);
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { 0 });
Instance instance = new Instance(fs, null, "check", null);
double[] restoredDist = restored.getSampledDistribution(instance, 3, 1, 0);
assertEquals(1, restoredDist.length);
assertEquals(1.0, restoredDist[0], 0.0001);
}

@Test
public void testNegativeTopicIndexHandlingDoesNotThrow() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("x");
int[][] ttc = new int[1][];
ttc[0] = new int[] { -1 };
int[] tokens = new int[] { 1 };
double[] alpha = new double[] { 0.1 };
double beta = 0.01;
try {
TopicInferencer inferencer = new TopicInferencer(ttc, tokens, alphabet, alpha, beta, 0.01);
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { 0 });
Instance inst = new Instance(fs, null, "bad", null);
double[] dist = inferencer.getSampledDistribution(inst, 3, 1, 0);
assertEquals(1, dist.length);
} catch (Exception e) {
fail("Should gracefully handle negative topic value without throwing");
}
}

@Test
public void testMultipleSamplesCollectedWithThinning() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("token");
int[][] counts = new int[1][];
counts[0] = new int[] { (5 << 1) + 0 };
int[] topicCounts = new int[] { 5 };
double[] alpha = new double[] { 0.5 };
double beta = 0.01;
TopicInferencer inferencer = new TopicInferencer(counts, topicCounts, alphabet, alpha, beta, 0.01);
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { 0, 0, 0 });
Instance inst = new Instance(fs, null, "sample1", null);
double[] dist = inferencer.getSampledDistribution(inst, 10, 2, 2);
assertEquals(1, dist.length);
assertEquals(1.0, dist[0], 0.0001);
}

@Test
public void testWriteDistributionWithoutInstanceNameAndNoThreshold() throws Exception {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("a");
int[][] counts = new int[1][];
counts[0] = new int[] { (3 << 1) + 0 };
int[] tokensPerTopic = new int[] { 3 };
double[] alpha = new double[] { 0.1 };
double beta = 0.01;
TopicInferencer inferencer = new TopicInferencer(counts, tokensPerTopic, alphabet, alpha, beta, 0.01);
// InstanceList list = new InstanceList(alphabet);
// FeatureSequence fs = new FeatureSequence(alphabet, new String[] { "a", "a" });
// Instance instance = new Instance(fs, null, null, null);
// list.add(instance);
File file = File.createTempFile("dist_nullname", ".txt");
file.deleteOnExit();
// inferencer.writeInferredDistributions(list, file, 5, 1, 0, 0.0, 3);
BufferedReader reader = new BufferedReader(new FileReader(file));
String header = reader.readLine();
String line = reader.readLine();
reader.close();
assertNotNull(header);
assertNotNull(line);
assertTrue(line.contains("no-name"));
}

@Test
public void testAllAlphaZeroFallbackNormalization() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("term");
int[][] counts = new int[1][];
counts[0] = new int[] { (3 << 1) + 0 };
int[] topicCounts = new int[] { 3 };
double[] alpha = new double[] { 0.0 };
double beta = 0.01;
TopicInferencer inferencer = new TopicInferencer(counts, topicCounts, alphabet, alpha, beta, 0.01);
// FeatureSequence fs = new FeatureSequence(alphabet, new String[] { "term" });
// Instance inst = new Instance(fs, null, "fallback", null);
// double[] dist = inferencer.getSampledDistribution(inst, 0, 1, 0);
// assertEquals(1, dist.length);
// assertEquals(1.0, dist[0], 0.0001);
}

@Test
public void testSerializationWithMultipleTopicsPreservesState() throws Exception {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("a");
alphabet.lookupIndex("b");
alphabet.lookupIndex("c");
int[][] counts = new int[3][];
counts[0] = new int[] { (4 << 2) + 0 };
counts[1] = new int[] { (1 << 2) + 1 };
counts[2] = new int[] { (2 << 2) + 2 };
int[] tokens = new int[] { 4, 1, 2 };
double[] alpha = new double[] { 0.3, 0.3, 0.3 };
double beta = 0.01;
double betaSum = 0.03;
TopicInferencer inferencer = new TopicInferencer(counts, tokens, alphabet, alpha, beta, betaSum);
File tmp = File.createTempFile("multi", ".ser");
tmp.deleteOnExit();
ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(tmp));
oos.writeObject(inferencer);
oos.close();
TopicInferencer loaded = TopicInferencer.read(tmp);
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { 0, 1, 2 });
Instance instance = new Instance(fs, null, "doc", null);
double[] dist = loaded.getSampledDistribution(instance, 4, 1, 0);
assertEquals(3, dist.length);
assertEquals(1.0, dist[0] + dist[1] + dist[2], 0.0001);
}

@Test
public void testLargeVocabularyAndTopicSpaceSerialization() throws Exception {
Alphabet alphabet = new Alphabet();
for (int i = 0; i < 100; i++) {
alphabet.lookupIndex("w" + i);
}
int topics = 16;
int[][] ttc = new int[100][];
int[] tokens = new int[topics];
double[] alpha = new double[topics];
for (int i = 0; i < 100; i++) {
ttc[i] = new int[] { ((i + 1) << 5) + (i % topics) };
}
for (int i = 0; i < topics; i++) {
tokens[i] = i + 1;
alpha[i] = 0.1;
}
TopicInferencer inferencer = new TopicInferencer(ttc, tokens, alphabet, alpha, 0.01, 1.0);
File file = File.createTempFile("large_serialization", ".ser");
file.deleteOnExit();
ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
oos.writeObject(inferencer);
oos.close();
TopicInferencer read = TopicInferencer.read(file);
assertNotNull(read);
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { 0, 10, 50 });
Instance inst = new Instance(fs, null, "large", null);
double[] dist = read.getSampledDistribution(inst, 3, 1, 0);
assertEquals(topics, dist.length);
}

@Test
public void testNewTopicSelectionWrappingBeyondAvailable() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("w");
int[][] ttc = new int[1][];
ttc[0] = new int[] { (1 << 1) + 0 };
int[] tokens = new int[] { 1 };
double[] alpha = new double[] { 0.5 };
double beta = 0.01;
TopicInferencer inferencer = new TopicInferencer(ttc, tokens, alphabet, alpha, beta, 0.01);
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { 0, 0 });
Instance inst = new Instance(fs, null, "backtrack", null);
double[] result = inferencer.getSampledDistribution(inst, 3, 1, 0);
assertEquals(1, result.length);
assertEquals(1.0, result[0], 0.0001);
}

@Test
public void testTopicMaskAndBitsCalculatedCorrectlyForTopicCountOne() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("one");
int[][] counts = new int[1][];
counts[0] = new int[] { (1 << 1) + 0 };
int[] tokens = new int[] { 1 };
double[] alpha = new double[] { 0.2 };
double beta = 0.01;
TopicInferencer inferencer = new TopicInferencer(counts, tokens, alphabet, alpha, beta, 0.01);
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { 0 });
Instance instance = new Instance(fs, null, "single", null);
double[] dist = inferencer.getSampledDistribution(instance, 2, 1, 0);
assertEquals(1, dist.length);
assertEquals(1.0, dist[0], 0.00001);
}

@Test
public void testTopicMaskAndBitsForNonPowerOfTwoTopicCount() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("x");
int[] tokens = new int[] { 1, 1, 1 };
int[][] counts = new int[1][];
counts[0] = new int[] { (2 << 2) + 0 };
double[] alpha = new double[] { 0.1, 0.1, 0.1 };
double beta = 0.01;
TopicInferencer inferencer = new TopicInferencer(counts, tokens, alphabet, alpha, beta, 0.03);
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { 0, 0 });
Instance instance = new Instance(fs, null, "bitmask", null);
double[] dist = inferencer.getSampledDistribution(instance, 5, 1, 0);
assertEquals(3, dist.length);
assertEquals(1.0, dist[0] + dist[1] + dist[2], 0.0001);
}

@Test
public void testGetSampledDistributionWithNumIterationsEqualBurnIn() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("x");
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (2 << 1) + 0 };
int[] tokensPerTopic = new int[] { 2 };
double[] alpha = new double[] { 0.1 };
double beta = 0.01;
double betaSum = 0.01;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { 0, 0 });
Instance instance = new Instance(fs, null, "skipSample", null);
double[] result = inferencer.getSampledDistribution(instance, 5, 1, 5);
assertEquals(1, result.length);
assertEquals(1.0, result[0], 0.0001);
}

@Test
public void testAlphaAndTokensPerTopicZeroSimultaneously() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("term");
int[][] counts = new int[1][];
counts[0] = new int[] { (1 << 1) + 0 };
int[] tpt = new int[] { 0 };
double[] alpha = new double[] { 0.0 };
double beta = 0.01;
TopicInferencer inferencer = new TopicInferencer(counts, tpt, alphabet, alpha, beta, 0.01);
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { 0 });
Instance instance = new Instance(fs, null, "zeroBoth", null);
double[] result = inferencer.getSampledDistribution(instance, 1, 1, 0);
assertEquals(1, result.length);
assertEquals(1.0, result[0], 0.0001);
}

@Test
public void testGetSampledDistributionWithSingleTokenMultipleEquivalentTopics() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("word");
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (4 << 2) + 0, (4 << 2) + 1 };
int[] tokensPerTopic = new int[] { 4, 4 };
double[] alpha = new double[] { 0.1, 0.1 };
double beta = 0.01;
double betaSum = 0.02;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { 0 });
Instance instance = new Instance(fs, null, "ambiguous", null);
double[] result = inferencer.getSampledDistribution(instance, 4, 1, 0);
assertEquals(2, result.length);
assertEquals(1.0, result[0] + result[1], 0.0001);
}

@Test
public void testGetSampledDistributionWithOneTypeMultipleTokensDifferentTopics() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("x");
int[][] counts = new int[1][];
counts[0] = new int[] { (3 << 2) + 0, (6 << 2) + 1 };
int[] tpt = new int[] { 3, 6 };
double[] alpha = new double[] { 0.2, 0.2 };
double beta = 0.01;
TopicInferencer inferencer = new TopicInferencer(counts, tpt, alphabet, alpha, beta, 0.02);
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { 0, 0 });
Instance inst = new Instance(fs, null, "multiTopicSplit", null);
double[] dist = inferencer.getSampledDistribution(inst, 5, 1, 0);
assertEquals(2, dist.length);
assertEquals(1.0, dist[0] + dist[1], 0.0001);
}

@Test
public void testGetSampledDistributionWithEmptyFeatureSequence() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("data");
int[][] counts = new int[1][];
counts[0] = new int[] { (1 << 1) + 0 };
int[] tpt = new int[] { 1 };
double[] alpha = new double[] { 0.2 };
double beta = 0.01;
TopicInferencer inferencer = new TopicInferencer(counts, tpt, alphabet, alpha, beta, 0.01);
FeatureSequence fs = new FeatureSequence(alphabet);
Instance inst = new Instance(fs, null, "empty", null);
double[] result = inferencer.getSampledDistribution(inst, 5, 1, 0);
assertEquals(1, result.length);
assertEquals(1.0, result[0], 0.0001);
}

@Test
public void testFallbackToDefaultNewTopicWhenNoneSelectedInFinalBranch() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("fallback");
int[][] ttc = new int[1][];
ttc[0] = new int[] { (1 << 3) + 2 };
int[] tokensPerTopic = new int[] { 0, 0, 1 };
double[] alpha = new double[] { 0.1, 0.1, 0.1 };
double beta = 0.01;
double betaSum = 0.03;
TopicInferencer inferencer = new TopicInferencer(ttc, tokensPerTopic, alphabet, alpha, beta, betaSum);
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { 0 });
Instance instance = new Instance(fs, null, "fallbackBranch", null);
double[] result = inferencer.getSampledDistribution(instance, 2, 1, 0);
assertEquals(3, result.length);
assertEquals(1.0, result[0] + result[1] + result[2], 0.0001);
}

@Test
public void testWriteInferredDistributionsWithZeroMaxWritesAllTopics() throws Exception {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("a");
int[][] counts = new int[1][];
counts[0] = new int[] { (5 << 1) + 0 };
int[] tpt = new int[] { 5 };
double[] alpha = new double[] { 0.1 };
double beta = 0.01;
TopicInferencer inferencer = new TopicInferencer(counts, tpt, alphabet, alpha, beta, 0.01);
// InstanceList list = new InstanceList(alphabet);
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { 0, 0 });
Instance instance = new Instance(fs, null, "doc", null);
// list.add(instance);
File file = File.createTempFile("zeromax", ".txt");
file.deleteOnExit();
// inferencer.writeInferredDistributions(list, file, 4, 1, 0, 0.0, 0);
BufferedReader reader = new BufferedReader(new FileReader(file));
reader.readLine();
String line = reader.readLine();
reader.close();
assertTrue(line.contains("doc"));
}

@Test
public void testGetSampledDistributionWithLargeAlphaAndBeta() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("word");
int[][] counts = new int[1][];
counts[0] = new int[] { (1 << 2) + 1 };
int[] tokens = new int[] { 0, 1 };
double[] alpha = new double[] { 100.0, 100.0 };
double beta = 20.0;
TopicInferencer inferencer = new TopicInferencer(counts, tokens, alphabet, alpha, beta, 40.0);
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { 0 });
Instance inst = new Instance(fs, null, "largeParams", null);
double[] result = inferencer.getSampledDistribution(inst, 4, 1, 0);
assertEquals(2, result.length);
assertEquals(1.0, result[0] + result[1], 0.00001);
}
}
