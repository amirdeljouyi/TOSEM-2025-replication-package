package cc.mallet.topics;

import cc.mallet.pipe.CharSequence2TokenSequence;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.TokenSequence2FeatureSequence;
import cc.mallet.types.*;
import cc.mallet.util.Randoms;
import org.junit.Test;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.zip.GZIPOutputStream;
import static org.junit.Assert.*;

public class TopicInferencer_llmsuite_5_GPTLLMTest {

@Test
public void testGetSampledDistribution_SimpleDocument() {
Alphabet alphabet = new Alphabet();
int type0 = alphabet.lookupIndex("alpha", true);
int type1 = alphabet.lookupIndex("beta", true);
int type2 = alphabet.lookupIndex("gamma", true);
double[] alpha = new double[] { 0.1, 0.1, 0.1, 0.1 };
int[] tokensPerTopic = new int[] { 10, 20, 30, 40 };
double beta = 0.01;
double betaSum = beta * 3;
int[][] typeTopicCounts = new int[3][];
typeTopicCounts[0] = new int[] { (5 << 2) + 0, (3 << 2) + 1 };
typeTopicCounts[1] = new int[] { (4 << 2) + 1 };
typeTopicCounts[2] = new int[] { (2 << 2) + 2 };
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(42);
TokenSequence ts = new TokenSequence();
ts.add("alpha");
ts.add("beta");
ts.add("gamma");
FeatureSequence fs = new FeatureSequence(alphabet, ts.size());
fs.add("alpha");
fs.add("beta");
fs.add("gamma");
Instance instance = new Instance(fs, null, "doc1", null);
double[] result = inferencer.getSampledDistribution(instance, 10, 1, 5);
assertNotNull(result);
assertEquals(4, result.length);
assertTrue(result[0] >= 0.0);
assertTrue(result[1] >= 0.0);
assertTrue(result[2] >= 0.0);
assertTrue(result[3] >= 0.0);
double total = result[0] + result[1] + result[2] + result[3];
assertEquals(1.0, total, 1e-6);
}

@Test
public void testGetSampledDistribution_EmptyDocument() {
Alphabet alphabet = new Alphabet();
double[] alpha = new double[] { 0.1, 0.1, 0.1, 0.1 };
int[] tokensPerTopic = new int[] { 10, 10, 10, 10 };
double beta = 0.01;
double betaSum = beta * 3;
int[][] typeTopicCounts = new int[3][];
typeTopicCounts[0] = new int[] { (4 << 2) + 1 };
typeTopicCounts[1] = new int[] { (2 << 2) + 0 };
typeTopicCounts[2] = new int[] { (3 << 2) + 2 };
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(99);
FeatureSequence fs = new FeatureSequence(alphabet, 0);
Instance instance = new Instance(fs, null, "empty", null);
double[] result = inferencer.getSampledDistribution(instance, 5, 1, 2);
assertNotNull(result);
assertEquals(4, result.length);
assertTrue(result[0] >= 0.0);
assertTrue(result[1] >= 0.0);
assertTrue(result[2] >= 0.0);
assertTrue(result[3] >= 0.0);
double total = result[0] + result[1] + result[2] + result[3];
assertEquals(1.0, total, 1e-6);
}

@Test
public void testSetRandomSeed_MakesSamplingDeterministic() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("x", true);
double[] alpha = new double[] { 0.2, 0.2 };
int[] tokensPerTopic = new int[] { 5, 15 };
double beta = 0.01;
double betaSum = beta * 1;
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (10 << 1) + 0, (5 << 1) + 1 };
TopicInferencer inferencer1 = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer1.setRandomSeed(123);
TopicInferencer inferencer2 = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer2.setRandomSeed(123);
TokenSequence ts = new TokenSequence();
ts.add("x");
FeatureSequence fs = new FeatureSequence(alphabet, ts.size());
fs.add("x");
Instance instance = new Instance(fs, null, "consistency", null);
double[] dist1 = inferencer1.getSampledDistribution(instance, 5, 1, 2);
double[] dist2 = inferencer2.getSampledDistribution(instance, 5, 1, 2);
assertArrayEquals(dist1, dist2, 1e-8);
}

@Test
public void testWriteInferredDistributions_OutputFormat() throws Exception {
Alphabet alphabet = new Alphabet();
int type = alphabet.lookupIndex("alpha", true);
double[] alpha = new double[] { 0.3, 0.3 };
int[] tokensPerTopic = new int[] { 6, 4 };
double beta = 0.01;
double betaSum = beta * 1;
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (3 << 1) + 0, (2 << 1) + 1 };
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(55);
// InstanceList instances = new InstanceList(new SerialPipes(Arrays.asList(new TokenSequence2FeatureSequence(alphabet, true))));
// instances.addThruPipe(new Instance("alpha alpha alpha", null, "docA", null));
File file = File.createTempFile("dist_test", ".txt");
file.deleteOnExit();
// inferencer.writeInferredDistributions(instances, file, 5, 1, 2, 0.0, -1);
BufferedReader reader = new BufferedReader(new FileReader(file));
String header = reader.readLine();
assertTrue(header.contains("#doc"));
String line = reader.readLine();
assertNotNull(line);
assertTrue(line.startsWith("0\tdocA"));
String[] parts = line.split("\t");
assertEquals(4, parts.length);
reader.close();
}

@Test
public void testSerializationAndDeserializationMaintainsBehavior() throws Exception {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("z", true);
double[] alpha = new double[] { 0.5 };
int[] tokensPerTopic = new int[] { 3 };
double beta = 0.01;
double betaSum = 0.01;
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (2 << 0) + 0 };
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(77);
File tempFile = File.createTempFile("inferencer", ".ser");
tempFile.deleteOnExit();
ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(tempFile));
oos.writeObject(inferencer);
oos.close();
TopicInferencer loaded = TopicInferencer.read(tempFile);
TokenSequence ts = new TokenSequence();
ts.add("z");
FeatureSequence fs = new FeatureSequence(alphabet, ts.size());
fs.add("z");
Instance instance = new Instance(fs, null, "serialized", null);
double[] originalResult = inferencer.getSampledDistribution(instance, 2, 1, 1);
double[] loadedResult = loaded.getSampledDistribution(instance, 2, 1, 1);
assertNotNull(originalResult);
assertNotNull(loadedResult);
assertEquals(originalResult.length, loadedResult.length);
}

@Test
public void testGetSampledDistribution_AllTokensOOV() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("train", true);
double[] alpha = new double[] { 0.1, 0.1 };
int[] tokensPerTopic = new int[] { 5, 5 };
double beta = 0.01;
double betaSum = 0.02;
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (2 << 1) + 0, (3 << 1) + 1 };
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(11);
cc.mallet.types.TokenSequence ts = new cc.mallet.types.TokenSequence();
ts.add("unknown1");
ts.add("unknown2");
cc.mallet.types.FeatureSequence fs = new cc.mallet.types.FeatureSequence(alphabet, ts.size());
fs.add("unknown1");
fs.add("unknown2");
cc.mallet.types.Instance instance = new cc.mallet.types.Instance(fs, null, "missing-tokens", null);
double[] result = inferencer.getSampledDistribution(instance, 5, 1, 2);
assertNotNull(result);
assertEquals(2, result.length);
double total = result[0] + result[1];
assertEquals(1.0, total, 1e-6);
}

@Test
public void testGetSampledDistribution_ZeroIterationsStillReturnsResult() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("word", true);
double[] alpha = new double[] { 0.05 };
int[] tokensPerTopic = new int[] { 7 };
double beta = 0.01;
double betaSum = beta;
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (2 << 0) + 0 };
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(1234);
TokenSequence ts = new TokenSequence();
ts.add("word");
FeatureSequence fs = new FeatureSequence(alphabet, ts.size());
fs.add("word");
Instance instance = new Instance(fs, null, "no-iter", null);
double[] result = inferencer.getSampledDistribution(instance, 0, 1, 0);
assertNotNull(result);
assertEquals(1, result.length);
assertTrue(result[0] > 0.0);
assertEquals(1.0, result[0], 1e-6);
}

@Test
public void testWriteInferredDistributions_WithThresholdThatExcludesAllTopics() throws Exception {
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("a", true);
double[] alpha = new double[] { 0.01, 0.01 };
int[] tokensPerTopic = new int[] { 3, 3 };
double beta = 0.01;
double betaSum = beta;
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (2 << 1) + 0 };
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(44);
// InstanceList list = new InstanceList(new SerialPipes(Arrays.asList(new TokenSequence2FeatureSequence(alphabet, true))));
// list.addThruPipe(new Instance("a a a", null, "tiny", null));
File file = File.createTempFile("topiccutoff", ".txt");
file.deleteOnExit();
// inferencer.writeInferredDistributions(list, file, 5, 1, 2, 1.0, 2);
BufferedReader reader = new BufferedReader(new FileReader(file));
String header = reader.readLine();
String line = reader.readLine();
reader.close();
assertNotNull(header);
assertTrue(header.contains("#doc"));
assertNotNull(line);
String[] fields = line.split("\t");
assertEquals(2, fields.length);
}

@Test
public void testWriteInferredDistributions_WithMaxSmallerThanTopics() throws Exception {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("tok", true);
double[] alpha = new double[] { 0.2, 0.2, 0.2 };
int[] tokensPerTopic = new int[] { 1, 1, 1 };
double beta = 0.01;
double betaSum = 0.03;
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (2 << 2) + 0, (2 << 2) + 1, (2 << 2) + 2 };
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(99);
// InstanceList list = new InstanceList(new SerialPipes(Arrays.asList(new TokenSequence2FeatureSequence(alphabet, true))));
// list.addThruPipe(new Instance("tok tok tok", null, "capped", null));
File file = File.createTempFile("maxcap", ".txt");
file.deleteOnExit();
// inferencer.writeInferredDistributions(list, file, 10, 1, 2, 0.05, 1);
BufferedReader reader = new BufferedReader(new FileReader(file));
reader.readLine();
String line = reader.readLine();
reader.close();
assertNotNull(line);
String[] parts = line.split("\t");
assertTrue(parts.length == 2 || parts.length == 4);
}

@Test
public void testReadMethodThrowsWhenFileMissing() {
try {
TopicInferencer.read(new File("nonexistent_path_" + System.nanoTime() + ".ser"));
fail("Expected exception due to missing file");
} catch (Exception e) {
assertTrue(e instanceof FileNotFoundException);
}
}

@Test
public void testSingleTopicInferencerReturnsValidDistribution() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("solo", true);
double[] alpha = new double[] { 0.3 };
int[] tokensPerTopic = new int[] { 5 };
double beta = 0.01;
double betaSum = 0.01;
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (5 << 0) + 0 };
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(1);
TokenSequence ts = new TokenSequence();
ts.add("solo");
FeatureSequence fs = new FeatureSequence(alphabet, ts.size());
fs.add("solo");
Instance instance = new Instance(fs, null, "single", null);
double[] result = inferencer.getSampledDistribution(instance, 5, 1, 2);
assertEquals(1, result.length);
assertEquals(1.0, result[0], 1e-6);
}

@Test
public void testGetSampledDistribution_TypeWithNoTopicAssignments() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("missing", true);
double[] alpha = new double[] { 0.2, 0.2 };
int[] tokensPerTopic = new int[] { 3, 3 };
double beta = 0.01;
double betaSum = 0.02;
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[0];
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(1234);
TokenSequence ts = new TokenSequence();
ts.add("missing");
FeatureSequence fs = new FeatureSequence(alphabet, ts.size());
fs.add("missing");
Instance instance = new Instance(fs, null, "no-topic-type", null);
double[] result = inferencer.getSampledDistribution(instance, 5, 1, 2);
assertNotNull(result);
assertEquals(2, result.length);
double sum = result[0] + result[1];
assertEquals(1.0, sum, 1e-6);
}

@Test
public void testGetSampledDistribution_SingleTokenDocument() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("token", true);
double[] alpha = new double[] { 0.3, 0.3 };
int[] tokensPerTopic = new int[] { 2, 4 };
double beta = 0.01;
double betaSum = beta;
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (3 << 1) + 0 };
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(2023);
TokenSequence ts = new TokenSequence();
ts.add("token");
FeatureSequence fs = new FeatureSequence(alphabet, 1);
fs.add("token");
Instance instance = new Instance(fs, null, "one-token", null);
double[] result = inferencer.getSampledDistribution(instance, 3, 1, 1);
assertNotNull(result);
assertEquals(2, result.length);
double total = result[0] + result[1];
assertEquals(1.0, total, 1e-6);
}

@Test
public void testGetSampledDistribution_WithZeroAlpha() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("zeroalpha", true);
double[] alpha = new double[] { 0.0, 0.0 };
int[] tokensPerTopic = new int[] { 1, 1 };
double beta = 0.01;
double betaSum = 0.01;
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (1 << 1) + 0, (1 << 1) + 1 };
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(888);
TokenSequence ts = new TokenSequence();
ts.add("zeroalpha");
FeatureSequence fs = new FeatureSequence(alphabet, ts.size());
fs.add("zeroalpha");
Instance instance = new Instance(fs, null, "zero-alpha", null);
double[] dist = inferencer.getSampledDistribution(instance, 5, 1, 2);
assertNotNull(dist);
assertEquals(2, dist.length);
assertTrue(dist[0] >= 0.0);
assertTrue(dist[1] >= 0.0);
double sum = dist[0] + dist[1];
assertEquals(1.0, sum, 1e-6);
}

@Test
public void testTopicInferencer_WithNonPowerOfTwoTopicCount() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("mixed", true);
double[] alpha = new double[] { 0.1, 0.1, 0.1 };
int[] tokensPerTopic = new int[] { 5, 5, 5 };
double beta = 0.01;
double betaSum = 0.03;
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (2 << 3) + 0, (3 << 3) + 1, (1 << 3) + 2 };
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(333);
TokenSequence ts = new TokenSequence();
ts.add("mixed");
FeatureSequence fs = new FeatureSequence(alphabet, ts.size());
fs.add("mixed");
Instance instance = new Instance(fs, null, "non-power-2", null);
double[] dist = inferencer.getSampledDistribution(instance, 6, 1, 2);
assertNotNull(dist);
assertEquals(3, dist.length);
assertEquals(1.0, dist[0] + dist[1] + dist[2], 1e-6);
}

@Test
public void testWriteInferredDistributions_InstanceWithNullName() throws Exception {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("anon", true);
double[] alpha = new double[] { 0.2, 0.2 };
int[] tokensPerTopic = new int[] { 2, 2 };
double beta = 0.01;
double betaSum = 0.02;
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (3 << 1) + 0 };
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(99);
// InstanceList instanceList = new InstanceList(new SerialPipes(Arrays.asList(new TokenSequence2FeatureSequence(alphabet, true))));
// instanceList.addThruPipe(new Instance("anon anon", null, null, null));
File output = File.createTempFile("nullname", ".txt");
output.deleteOnExit();
// inferencer.writeInferredDistributions(instanceList, output, 5, 1, 1, 0.0, -1);
BufferedReader reader = new BufferedReader(new FileReader(output));
String header = reader.readLine();
String line = reader.readLine();
reader.close();
assertNotNull(header);
assertTrue(header.contains("#doc"));
assertNotNull(line);
assertTrue(line.contains("no-name"));
}

@Test
public void testWriteInferredDistributions_MaxZeroSuppressesAllTopics() throws Exception {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("tok", true);
double[] alpha = new double[] { 0.1, 0.1 };
int[] tokensPerTopic = new int[] { 2, 2 };
double beta = 0.01;
double betaSum = 0.02;
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (3 << 1) + 0, (2 << 1) + 1 };
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(77);
// InstanceList instanceList = new InstanceList(new SerialPipes(Arrays.asList(new TokenSequence2FeatureSequence(alphabet, true))));
// instanceList.addThruPipe(new Instance("tok tok", null, "capzero", null));
File file = File.createTempFile("max-zero", ".txt");
file.deleteOnExit();
// inferencer.writeInferredDistributions(instanceList, file, 5, 1, 2, 0.0, 0);
BufferedReader reader = new BufferedReader(new FileReader(file));
reader.readLine();
String line = reader.readLine();
reader.close();
assertNotNull(line);
String[] tokens = line.split("\t");
assertEquals(2, tokens.length);
}

@Test
public void testGetSampledDistribution_OnlyOneUnseenTokenIgnored() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("seen", true);
double[] alpha = new double[] { 0.1, 0.1 };
int[] tokensPerTopic = new int[] { 5, 5 };
double beta = 0.01;
double betaSum = 0.02;
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (3 << 1) + 0 };
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(100);
cc.mallet.types.TokenSequence ts = new cc.mallet.types.TokenSequence();
ts.add("seen");
ts.add("unseen");
cc.mallet.types.FeatureSequence fs = new cc.mallet.types.FeatureSequence(alphabet, ts.size());
fs.add("seen");
fs.add("unseen");
cc.mallet.types.Instance instance = new cc.mallet.types.Instance(fs, null, "mixed", null);
double[] result = inferencer.getSampledDistribution(instance, 4, 1, 1);
assertNotNull(result);
assertEquals(2, result.length);
assertTrue(result[0] >= 0.0);
assertTrue(result[1] >= 0.0);
double sum = result[0] + result[1];
assertEquals(1.0, sum, 1e-6);
}

@Test
public void testGetSampledDistribution_AlphaAndBetaCauseZeroCoefficient() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("term", true);
double[] alpha = new double[] { 0.0 };
int[] tokensPerTopic = new int[] { 0 };
double beta = 0.0;
double betaSum = 0.0;
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (0 << 0) + 0 };
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(321);
TokenSequence ts = new TokenSequence();
ts.add("term");
FeatureSequence fs = new FeatureSequence(alphabet, ts.size());
fs.add("term");
Instance instance = new Instance(fs, null, "edge-zero", null);
double[] result = inferencer.getSampledDistribution(instance, 3, 1, 0);
assertNotNull(result);
assertEquals(1, result.length);
assertTrue(result[0] >= 0.0);
assertEquals(1.0, result[0], 1e-6);
}

@Test
public void testWriteInferredDistributions_WriteMultipleInstancesCorrectly() throws Exception {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("multi", true);
double[] alpha = new double[] { 0.1, 0.1 };
int[] tokensPerTopic = new int[] { 3, 3 };
double beta = 0.01;
double betaSum = 0.02;
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (1 << 1) + 0, (2 << 1) + 1 };
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(222);
// cc.mallet.types.InstanceList instanceList = new cc.mallet.types.InstanceList(new SerialPipes(Arrays.asList(new cc.mallet.pipe.TokenSequence2FeatureSequence(alphabet, true))));
// instanceList.addThruPipe(new cc.mallet.types.Instance("multi multi", null, "doc0", null));
// instanceList.addThruPipe(new cc.mallet.types.Instance("multi", null, "doc1", null));
File outputFile = File.createTempFile("multipledocs", ".txt");
outputFile.deleteOnExit();
// inferencer.writeInferredDistributions(instanceList, outputFile, 5, 1, 0, 0.0, -1);
BufferedReader reader = new BufferedReader(new FileReader(outputFile));
String line1 = reader.readLine();
String line2 = reader.readLine();
String line3 = reader.readLine();
reader.close();
assertNotNull(line1);
assertTrue(line1.contains("#doc"));
assertNotNull(line2);
assertTrue(line2.contains("doc0"));
assertNotNull(line3);
assertTrue(line3.contains("doc1"));
}

@Test
public void testWriteInferredDistributions_ThresholdExactCutoff() throws Exception {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("cut", true);
double[] alpha = new double[] { 0.5, 0.5 };
int[] tokensPerTopic = new int[] { 2, 2 };
double beta = 0.01;
double betaSum = 0.02;
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (1 << 1) + 0, (1 << 1) + 1 };
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(555);
// InstanceList list = new InstanceList(new SerialPipes(Arrays.asList(new TokenSequence2FeatureSequence(alphabet, true))));
// list.addThruPipe(new Instance("cut cut", null, "docThreshold", null));
File file = File.createTempFile("eqcutoff", ".txt");
file.deleteOnExit();
// inferencer.writeInferredDistributions(list, file, 5, 1, 2, 0.5, 2);
BufferedReader reader = new BufferedReader(new FileReader(file));
String header = reader.readLine();
String line = reader.readLine();
reader.close();
assertNotNull(header);
assertTrue(header.startsWith("#doc"));
assertNotNull(line);
assertTrue(line.contains("docThreshold"));
}

@Test
public void testGetSampledDistribution_CachedCoefficientNeverNegative() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("negcheck", true);
double[] alpha = new double[] { 0.1, 0.1 };
int[] tokensPerTopic = new int[] { 0, 0 };
double beta = 0.01;
double betaSum = 0.02;
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (1 << 1) + 0, (1 << 1) + 1 };
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(606);
TokenSequence ts = new TokenSequence();
ts.add("negcheck");
FeatureSequence fs = new FeatureSequence(alphabet, ts.size());
fs.add("negcheck");
Instance instance = new Instance(fs, null, "never-neg", null);
double[] result = inferencer.getSampledDistribution(instance, 2, 1, 0);
assertNotNull(result);
assertEquals(2, result.length);
assertTrue(result[0] >= 0.0);
assertTrue(result[1] >= 0.0);
assertEquals(1.0, result[0] + result[1], 1e-6);
}

@Test
public void testGetSampledDistribution_AllAlphaZero_AllTokenCountsZero() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("zz", true);
double[] alpha = new double[] { 0.0, 0.0 };
int[] tokensPerTopic = new int[] { 0, 0 };
double beta = 0.0;
double betaSum = 0.0;
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (1 << 1) + 0, (1 << 1) + 1 };
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
TokenSequence ts = new TokenSequence();
ts.add("zz");
FeatureSequence fs = new FeatureSequence(alphabet, ts.size());
fs.add("zz");
Instance instance = new Instance(fs, null, "alphazero", null);
double[] result = inferencer.getSampledDistribution(instance, 0, 1, 0);
assertNotNull(result);
assertEquals(2, result.length);
double sum = result[0] + result[1];
assertEquals(1.0, sum, 1e-6);
}

@Test
public void testGetSampledDistribution_TokenAppearsMultipleTimes() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("rpt", true);
double[] alpha = new double[] { 0.1, 0.1 };
int[] tokensPerTopic = new int[] { 1, 1 };
double beta = 0.01;
double betaSum = 0.02;
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (3 << 1) + 0, (2 << 1) + 1 };
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(777);
TokenSequence ts = new TokenSequence();
ts.add("rpt");
ts.add("rpt");
ts.add("rpt");
FeatureSequence fs = new FeatureSequence(alphabet, ts.size());
fs.add("rpt");
fs.add("rpt");
fs.add("rpt");
Instance instance = new Instance(fs, null, "repeats", null);
double[] result = inferencer.getSampledDistribution(instance, 5, 1, 1);
assertNotNull(result);
assertEquals(2, result.length);
assertTrue(result[0] >= 0.0);
assertTrue(result[1] >= 0.0);
assertEquals(1.0, result[0] + result[1], 1e-6);
}

@Test
public void testWriteInferredDistributions_NegativeMaxTruncatedToNumTopics() throws Exception {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("tok", true);
double[] alpha = new double[] { 0.2, 0.2, 0.2 };
int[] tokensPerTopic = new int[] { 2, 2, 2 };
double beta = 0.01;
double betaSum = 0.03;
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (1 << 2) + 0, (1 << 2) + 1, (1 << 2) + 2 };
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
// InstanceList list = new InstanceList(new SerialPipes(Arrays.asList(new TokenSequence2FeatureSequence(alphabet, true))));
// list.addThruPipe(new Instance("tok tok", null, "docNegMax", null));
File output = File.createTempFile("negmax", ".txt");
output.deleteOnExit();
// inferencer.writeInferredDistributions(list, output, 5, 1, 1, 0.0, -1);
BufferedReader reader = new BufferedReader(new FileReader(output));
String header = reader.readLine();
String line = reader.readLine();
reader.close();
assertNotNull(header);
assertTrue(header.contains("#doc"));
assertNotNull(line);
assertTrue(line.contains("docNegMax"));
String[] parts = line.split("\t");
assertTrue(parts.length > 3);
}

@Test
public void testWriteInferredDistributions_ZeroIterations() throws Exception {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("x", true);
double[] alpha = new double[] { 0.2 };
int[] tokensPerTopic = new int[] { 1 };
double beta = 0.01;
double betaSum = 0.01;
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (2 << 0) + 0 };
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
// InstanceList list = new InstanceList(new SerialPipes(Arrays.asList(new TokenSequence2FeatureSequence(alphabet, true))));
// list.addThruPipe(new Instance("x", null, "docZeroIt", null));
File file = File.createTempFile("zerocase", ".txt");
file.deleteOnExit();
// inferencer.writeInferredDistributions(list, file, 0, 1, 0, 0.0, -1);
BufferedReader reader = new BufferedReader(new FileReader(file));
reader.readLine();
String content = reader.readLine();
reader.close();
assertNotNull(content);
assertTrue(content.contains("docZeroIt"));
String[] tokens = content.split("\t");
assertTrue(tokens.length > 2);
}

@Test
public void testReadObjectFailsOnInvalidFile() throws Exception {
File tempFile = File.createTempFile("invalid", ".dat");
FileOutputStream fos = new FileOutputStream(tempFile);
fos.write("corrupt".getBytes());
fos.close();
try {
FileInputStream fis = new FileInputStream(tempFile);
ObjectInputStream ois = new ObjectInputStream(fis);
ois.readObject();
ois.close();
fail("Expected StreamCorruptedException or IOException");
} catch (Exception e) {
assertTrue(e instanceof StreamCorruptedException || e instanceof EOFException || e instanceof IOException);
}
}

@Test
public void testGetSampledDistribution_DocumentWithOnlyOOVAndValidTokens() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("valid", true);
double[] alpha = new double[] { 0.1, 0.1 };
int[] tokensPerTopic = new int[] { 4, 4 };
double beta = 0.01;
double betaSum = 0.02;
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (2 << 1) + 1 };
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(1010);
TokenSequence ts = new TokenSequence();
ts.add("valid");
ts.add("unknown");
ts.add("unknown2");
FeatureSequence fs = new FeatureSequence(alphabet, ts.size());
fs.add("valid");
fs.add("unknown");
fs.add("unknown2");
Instance instance = new Instance(fs, null, "mixed-OOV", null);
double[] result = inferencer.getSampledDistribution(instance, 5, 1, 2);
assertNotNull(result);
assertEquals(2, result.length);
assertTrue(result[0] >= 0.0);
assertTrue(result[1] >= 0.0);
assertEquals(1.0, result[0] + result[1], 1e-6);
}

@Test
public void testGetSampledDistribution_TypeTopicCountsEntryWithZeroShiftedValue() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("zeroCount", true);
double[] alpha = new double[] { 0.5, 0.5 };
int[] tokensPerTopic = new int[] { 10, 10 };
double beta = 0.01;
double betaSum = 0.02;
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (0 << 1) + 0, (0 << 1) + 1 };
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(1919);
TokenSequence ts = new TokenSequence();
ts.add("zeroCount");
FeatureSequence fs = new FeatureSequence(alphabet, ts.size());
fs.add("zeroCount");
Instance instance = new Instance(fs, null, "zeroShifted", null);
double[] result = inferencer.getSampledDistribution(instance, 5, 1, 1);
assertNotNull(result);
assertEquals(2, result.length);
assertTrue(result[0] >= 0.0);
assertTrue(result[1] >= 0.0);
assertEquals(1.0, result[0] + result[1], 1e-6);
}

@Test
public void testWriteInferredDistributions_ThresholdHigherThanAllTopics() throws Exception {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("high", true);
double[] alpha = new double[] { 0.1, 0.1, 0.1 };
int[] tokensPerTopic = new int[] { 5, 5, 5 };
double beta = 0.01;
double betaSum = 0.03;
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (1 << 2) + 0, (1 << 2) + 1, (1 << 2) + 2 };
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
// InstanceList list = new InstanceList(new SerialPipes(Arrays.asList(new TokenSequence2FeatureSequence(alphabet, true))));
// list.addThruPipe(new Instance("high high", null, "thrHiDoc", null));
File file = File.createTempFile("high-threshold", ".txt");
file.deleteOnExit();
// inferencer.writeInferredDistributions(list, file, 5, 1, 2, 1.0, 2);
BufferedReader reader = new BufferedReader(new FileReader(file));
String header = reader.readLine();
String line = reader.readLine();
reader.close();
assertNotNull(header);
assertTrue(header.contains("#doc"));
assertNotNull(line);
String[] parts = line.split("\t");
assertEquals(2, parts.length);
}

@Test
public void testGetSampledDistribution_TokenTopicNotRecordedInDenseIndex() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("token", true);
double[] alpha = new double[] { 0.1, 0.1 };
int[] tokensPerTopic = new int[] { 1, 1000 };
double beta = 0.01;
double betaSum = 0.02;
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (10 << 1) + 1 };
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(9090);
TokenSequence ts = new TokenSequence();
ts.add("token");
FeatureSequence fs = new FeatureSequence(alphabet, ts.size());
fs.add("token");
Instance instance = new Instance(fs, null, "sparseTight", null);
double[] dist = inferencer.getSampledDistribution(instance, 1, 1, 0);
assertNotNull(dist);
assertEquals(2, dist.length);
assertTrue(dist[0] >= 0.0);
assertTrue(dist[1] >= 0.0);
assertEquals(1.0, dist[0] + dist[1], 1e-6);
}

@Test
public void testGetSampledDistribution_TopicMassOnlyFromAlphaFallback() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("fallback", true);
double[] alpha = new double[] { 0.7, 0.3 };
int[] tokensPerTopic = new int[] { 0, 0 };
double beta = 0.01;
double betaSum = 0.02;
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[0];
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(818);
TokenSequence ts = new TokenSequence();
ts.add("fallback");
FeatureSequence fs = new FeatureSequence(alphabet, ts.size());
fs.add("fallback");
Instance instance = new Instance(fs, null, "alphaback", null);
double[] dist = inferencer.getSampledDistribution(instance, 3, 1, 1);
assertNotNull(dist);
assertEquals(2, dist.length);
assertTrue(dist[0] >= 0.0);
assertTrue(dist[1] >= 0.0);
assertEquals(1.0, dist[0] + dist[1], 1e-6);
}

@Test
public void testGetSampledDistribution_TypeWithMultipleIdenticalTopicAssignments() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("duplicate", true);
double[] alpha = new double[] { 0.1, 0.1 };
int[] tokensPerTopic = new int[] { 5, 5 };
double beta = 0.01;
double betaSum = 0.02;
int topicMask = 1;
int topicBits = Integer.bitCount(topicMask);
int maskedTopic = 0;
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (3 << topicBits) + maskedTopic, (2 << topicBits) + maskedTopic };
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(123);
TokenSequence ts = new TokenSequence();
ts.add("duplicate");
ts.add("duplicate");
FeatureSequence fs = new FeatureSequence(alphabet, ts.size());
fs.add("duplicate");
fs.add("duplicate");
Instance instance = new Instance(fs, null, "multi-same-topic", null);
double[] result = inferencer.getSampledDistribution(instance, 3, 1, 1);
assertNotNull(result);
assertEquals(2, result.length);
assertTrue(result[0] >= 0.0 && result[1] >= 0.0);
assertEquals(1.0, result[0] + result[1], 1e-6);
}

@Test
public void testGetSampledDistribution_NegativeAlphaShouldNotCrash() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("token", true);
double[] alpha = new double[] { -0.1, 0.2 };
int[] tokensPerTopic = new int[] { 1, 1 };
double beta = 0.01;
double betaSum = 0.02;
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (2 << 1) + 0, (2 << 1) + 1 };
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(42);
TokenSequence ts = new TokenSequence();
ts.add("token");
FeatureSequence fs = new FeatureSequence(alphabet, ts.size());
fs.add("token");
Instance instance = new Instance(fs, null, "neg-alpha", null);
double[] result = inferencer.getSampledDistribution(instance, 3, 1, 1);
assertNotNull(result);
assertEquals(2, result.length);
assertTrue(result[0] >= 0.0 && result[1] >= 0.0);
assertEquals(1.0, result[0] + result[1], 1e-6);
}

@Test
public void testGetSampledDistribution_TypeOutOfBounds_ShouldIgnore() {
Alphabet alphabet = new Alphabet();
double[] alpha = new double[] { 0.1, 0.2 };
int[] tokensPerTopic = new int[] { 1, 1 };
double beta = 0.01;
double betaSum = 0.02;
int[][] typeTopicCounts = new int[0][];
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(77);
TokenSequence ts = new TokenSequence();
ts.add("unseen");
FeatureSequence fs = new FeatureSequence(alphabet, ts.size());
fs.add("unseen");
Instance instance = new Instance(fs, null, "type-oob", null);
double[] result = inferencer.getSampledDistribution(instance, 5, 1, 2);
assertNotNull(result);
assertEquals(2, result.length);
assertTrue(result[0] >= 0.0 && result[1] >= 0.0);
assertEquals(1.0, result[0] + result[1], 1e-6);
}

@Test
public void testWriteInferredDistributions_SortedTopicsTieBreakingStability() throws Exception {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("tie", true);
double[] alpha = new double[] { 0.1, 0.1 };
int[] tokensPerTopic = new int[] { 2, 2 };
double beta = 0.01;
double betaSum = 0.02;
int shiftedBits = 1;
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (3 << shiftedBits) + 0, (3 << shiftedBits) + 1 };
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(6);
// InstanceList list = new InstanceList(new SerialPipes(Arrays.asList(new TokenSequence2FeatureSequence(alphabet, true))));
// list.addThruPipe(new Instance("tie tie", null, "tiecase", null));
File file = File.createTempFile("tiecase", ".txt");
file.deleteOnExit();
// inferencer.writeInferredDistributions(list, file, 5, 1, 1, 0.0, 2);
BufferedReader reader = new BufferedReader(new FileReader(file));
reader.readLine();
String line = reader.readLine();
reader.close();
assertNotNull(line);
assertTrue(line.contains("tiecase"));
assertTrue(line.split("\t").length >= 4);
}

@Test
public void testReadObject_WithInvalidTypeCast_ThrowsException() throws Exception {
File file = File.createTempFile("invalidcast", ".ser");
file.deleteOnExit();
ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
oos.writeInt(0);
oos.writeObject("not-an-alphabet");
oos.writeInt(2);
oos.writeInt(1);
oos.writeInt(1);
oos.writeInt(0);
oos.writeObject(new double[] { 0.1, 0.1 });
oos.writeDouble(0.01);
oos.writeDouble(0.02);
oos.writeObject(new int[0][]);
oos.writeObject(new int[2]);
oos.writeObject(new Randoms());
oos.writeDouble(0.0);
oos.writeObject(new double[] { 0.1, 0.1 });
oos.close();
try {
ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
TopicInferencer loaded = new TopicInferencer();
java.lang.reflect.Method m = TopicInferencer.class.getDeclaredMethod("readObject", ObjectInputStream.class);
m.setAccessible(true);
m.invoke(loaded, ois);
fail("Expected exception due to wrong object type");
} catch (Exception e) {
assertTrue(e.getCause() instanceof ClassCastException);
}
}
}
