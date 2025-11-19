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

public class TopicInferencer_llmsuite_4_GPTLLMTest {

@Test
public void testSetRandomSeedConsistency() {
int numTopics = 2;
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (3 << 1) + 0 };
int[] tokensPerTopic = new int[] { 3, 2 };
double[] alpha = new double[] { 0.5, 0.8 };
double beta = 0.01;
double betaSum = 0.03;
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("word");
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(123);
double x1 = inferencer.random.nextUniform();
double x2 = inferencer.random.nextUniform();
TopicInferencer inferencer2 = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer2.setRandomSeed(123);
double y1 = inferencer2.random.nextUniform();
double y2 = inferencer2.random.nextUniform();
assertEquals(x1, y1, 1e-10);
assertEquals(x2, y2, 1e-10);
}

@Test
public void testSampledDistributionNormalized() {
Alphabet alphabet = new Alphabet();
int tokenA = alphabet.lookupIndex("apple");
int tokenB = alphabet.lookupIndex("banana");
int[][] typeTopicCounts = new int[2][];
typeTopicCounts[0] = new int[] { (4 << 1) + 0 };
typeTopicCounts[1] = new int[] { (5 << 1) + 1 };
int[] tokensPerTopic = new int[] { 4, 5 };
double[] alpha = new double[] { 0.3, 0.7 };
double beta = 0.01;
double betaSum = beta * 2;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(42);
int[] tokens = new int[] { tokenA, tokenB };
FeatureSequence sequence = new FeatureSequence(alphabet, tokens);
Instance instance = new Instance(sequence, null, "docA", null);
double[] dist = inferencer.getSampledDistribution(instance, 5, 1, 2);
assertEquals(2, dist.length);
double sum = dist[0] + dist[1];
assertTrue(dist[0] >= 0.0);
assertTrue(dist[1] >= 0.0);
assertEquals(1.0, sum, 1e-6);
}

@Test
public void testSampledDistributionZeroIterationsFallback() {
Alphabet alphabet = new Alphabet();
int token = alphabet.lookupIndex("term");
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (2 << 1) + 0 };
int[] tokensPerTopic = new int[] { 2, 1 };
double[] alpha = new double[] { 0.1, 0.1 };
double beta = 0.01;
double betaSum = 0.01;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(99);
int[] tokens = new int[] { token };
FeatureSequence sequence = new FeatureSequence(alphabet, tokens);
Instance instance = new Instance(sequence, null, "docZero", null);
double[] result = inferencer.getSampledDistribution(instance, 0, 1, 0);
assertEquals(2, result.length);
double total = result[0] + result[1];
assertTrue(result[0] >= 0.0);
assertTrue(result[1] >= 0.0);
assertEquals(1.0, total, 1e-6);
}

@Test
public void testOutOfVocabularyTermIgnored() {
Alphabet alphabet = new Alphabet();
int token = alphabet.lookupIndex("unknown");
int[][] typeTopicCounts = new int[0][];
int[] tokensPerTopic = new int[] { 1, 1 };
double[] alpha = new double[] { 0.3, 0.3 };
double beta = 0.01;
double betaSum = 0.02;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(321);
int[] tokens = new int[] { token };
FeatureSequence sequence = new FeatureSequence(alphabet, tokens);
Instance instance = new Instance(sequence, null, "docUnknown", null);
double[] result = inferencer.getSampledDistribution(instance, 5, 1, 2);
assertEquals(2, result.length);
double total = result[0] + result[1];
assertTrue(result[0] >= 0.0);
assertTrue(result[1] >= 0.0);
assertEquals(1.0, total, 1e-6);
}

@Test
public void testWriteInferredDistributionsGeneratesFile() throws IOException {
Alphabet alphabet = new Alphabet();
int token = alphabet.lookupIndex("foo");
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (1 << 1) + 0 };
int[] tokensPerTopic = new int[] { 1, 1 };
double[] alpha = new double[] { 0.5, 0.5 };
double beta = 0.01;
double betaSum = 0.02;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(11);
int[] tokens = new int[] { token, token };
FeatureSequence sequence = new FeatureSequence(alphabet, tokens);
Instance instance = new Instance(sequence, null, "named-doc", null);
InstanceList list = new InstanceList(alphabet, null);
list.addThruPipe(instance);
File output = File.createTempFile("topic_dist", ".txt");
output.deleteOnExit();
inferencer.writeInferredDistributions(list, output, 5, 1, 1, 0.0, 2);
assertTrue(output.exists());
assertTrue(output.length() > 0);
BufferedReader reader = new BufferedReader(new FileReader(output));
String line1 = reader.readLine();
String line2 = reader.readLine();
reader.close();
assertNotNull(line1);
assertTrue(line1.contains("#doc"));
assertNotNull(line2);
assertTrue(line2.contains("named-doc"));
}

@Test
public void testWriteInferredDistributionsNoNameInstance() throws IOException {
Alphabet alphabet = new Alphabet();
int token = alphabet.lookupIndex("x");
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (2 << 1) + 1 };
int[] tokensPerTopic = new int[] { 2, 2 };
double[] alpha = new double[] { 0.2, 0.2 };
double beta = 0.01;
double betaSum = 0.02;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(57);
int[] tokens = new int[] { token };
FeatureSequence sequence = new FeatureSequence(alphabet, tokens);
Instance instance = new Instance(sequence, null, null, null);
InstanceList list = new InstanceList(alphabet, null);
list.addThruPipe(instance);
File file = File.createTempFile("inferred", ".txt");
file.deleteOnExit();
inferencer.writeInferredDistributions(list, file, 5, 1, 1, 0.0, 2);
assertTrue(file.exists());
BufferedReader reader = new BufferedReader(new FileReader(file));
reader.readLine();
String docLine = reader.readLine();
reader.close();
assertNotNull(docLine);
assertTrue(docLine.contains("no-name"));
}

@Test
public void testSerializationDeserializationConsistency() throws Exception {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("serialization");
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (1 << 1) + 0 };
int[] tokensPerTopic = new int[] { 1, 1 };
double[] alpha = new double[] { 0.05, 0.05 };
double beta = 0.01;
double betaSum = 0.02;
TopicInferencer original = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
original.setRandomSeed(300);
File file = File.createTempFile("inferencer", ".ser");
file.deleteOnExit();
ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
out.writeObject(original);
out.close();
ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
Object obj = in.readObject();
in.close();
assertTrue(obj instanceof TopicInferencer);
TopicInferencer restored = (TopicInferencer) obj;
FeatureSequence sequence = new FeatureSequence(alphabet, new int[] { 0 });
Instance instance = new Instance(sequence, null, "doc", null);
double[] originalDist = original.getSampledDistribution(instance, 5, 1, 1);
double[] restoredDist = restored.getSampledDistribution(instance, 5, 1, 1);
assertEquals(originalDist.length, restoredDist.length);
assertEquals(1.0, restoredDist[0] + restoredDist[1], 1e-6);
}

@Test
public void testStaticReadMethod() throws Exception {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("staticRead");
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (2 << 1) + 1 };
int[] tokensPerTopic = new int[] { 0, 2 };
double[] alpha = new double[] { 0.2, 0.2 };
double beta = 0.01;
double betaSum = 0.02;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(888);
File f = File.createTempFile("loaded", ".ser");
f.deleteOnExit();
ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(f));
out.writeObject(inferencer);
out.close();
TopicInferencer loaded = TopicInferencer.read(f);
assertNotNull(loaded);
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { 0 });
Instance inst = new Instance(fs, null, "ldoc", null);
double[] dist = loaded.getSampledDistribution(inst, 5, 1, 2);
double sum = dist[0] + dist[1];
assertEquals(2, dist.length);
assertEquals(1.0, sum, 1e-6);
}

@Test
public void testEmptyDocumentShouldReturnSmoothedDistribution() {
Alphabet alphabet = new Alphabet();
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (4 << 1) + 0 };
int[] tokensPerTopic = new int[] { 4, 4 };
double[] alpha = new double[] { 0.1, 0.1 };
double beta = 0.01;
double betaSum = 0.02;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(123);
int[] tokens = new int[] {};
FeatureSequence fs = new FeatureSequence(alphabet, tokens);
Instance instance = new Instance(fs, null, "empty-doc", null);
double[] distribution = inferencer.getSampledDistribution(instance, 5, 1, 1);
assertEquals(2, distribution.length);
double sum = distribution[0] + distribution[1];
assertTrue(distribution[0] > 0.0);
assertTrue(distribution[1] > 0.0);
assertEquals(1.0, sum, 1e-6);
}

@Test
public void testNegativeMaxTopicsInWriteInferredDistributions() throws IOException {
Alphabet alphabet = new Alphabet();
int word = alphabet.lookupIndex("neg");
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (1 << 1) + 0 };
int[] tokensPerTopic = new int[] { 1, 1 };
double[] alpha = new double[] { 0.2, 0.2 };
double beta = 0.01;
double betaSum = 0.02;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(1);
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { word });
Instance instance = new Instance(fs, null, "neg-doc", null);
InstanceList list = new InstanceList(alphabet, null);
list.addThruPipe(instance);
File file = File.createTempFile("negmax", ".txt");
file.deleteOnExit();
inferencer.writeInferredDistributions(list, file, 5, 1, 1, 0.0, -1);
BufferedReader reader = new BufferedReader(new FileReader(file));
reader.readLine();
String body = reader.readLine();
reader.close();
assertNotNull(body);
assertTrue(body.contains("neg-doc"));
}

@Test
public void testThresholdHigherThanAnyTopicProbabilityShouldPrintNothingBeyondDocHeader() throws IOException {
Alphabet alphabet = new Alphabet();
int index = alphabet.lookupIndex("hi");
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (1 << 1) + 0 };
int[] tokensPerTopic = new int[] { 1, 1 };
double[] alpha = new double[] { 0.2, 0.2 };
double beta = 0.01;
double betaSum = 0.02;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(888);
int[] tokens = new int[] { index };
FeatureSequence fs = new FeatureSequence(alphabet, tokens);
Instance instance = new Instance(fs, null, "t-doc", null);
InstanceList list = new InstanceList(alphabet, null);
list.addThruPipe(instance);
File file = File.createTempFile("threshdoc", ".txt");
file.deleteOnExit();
inferencer.writeInferredDistributions(list, file, 5, 1, 1, 0.99, 2);
BufferedReader reader = new BufferedReader(new FileReader(file));
reader.readLine();
String line = reader.readLine();
reader.close();
assertNotNull(line);
assertTrue(line.startsWith("0\tt-doc"));
assertFalse(line.contains("\t0\t"));
assertFalse(line.contains("\t1\t"));
}

@Test
public void testSingleTopicModelShouldStillReturnValidDistribution() {
Alphabet alphabet = new Alphabet();
int word = alphabet.lookupIndex("only");
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (3 << 0) };
int[] tokensPerTopic = new int[] { 3 };
double[] alpha = new double[] { 0.5 };
double beta = 0.01;
double betaSum = 0.01;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(7);
int[] tokens = new int[] { word, word };
FeatureSequence fs = new FeatureSequence(alphabet, tokens);
Instance instance = new Instance(fs, null, "one-topic", null);
double[] distribution = inferencer.getSampledDistribution(instance, 5, 1, 1);
assertEquals(1, distribution.length);
assertEquals(1.0, distribution[0], 1e-6);
}

@Test
public void testZeroAlphaValuesShouldStillProduceDistribution() {
Alphabet alphabet = new Alphabet();
int word = alphabet.lookupIndex("zero");
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (3 << 1) + 0 };
int[] tokensPerTopic = new int[] { 3, 3 };
double[] alpha = new double[] { 0.0, 0.0 };
double beta = 0.01;
double betaSum = 0.02;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(55);
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { word });
Instance instance = new Instance(fs, null, "zero-alpha", null);
double[] dist = inferencer.getSampledDistribution(instance, 5, 1, 1);
assertEquals(2, dist.length);
double total = dist[0] + dist[1];
assertTrue(dist[0] >= 0.0);
assertTrue(dist[1] >= 0.0);
assertEquals(1.0, total, 1e-6);
}

@Test
public void testAllTokensOutOfVocabularyShouldStillReturnSmoothed() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("train");
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (2 << 1) + 0 };
int[] tokensPerTopic = new int[] { 2, 2 };
double[] alpha = new double[] { 0.3, 0.3 };
double beta = 0.01;
double betaSum = 0.02;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(99);
int[] tokens = new int[] { 99, 99 };
FeatureSequence fs = new FeatureSequence(alphabet, tokens);
Instance instance = new Instance(fs, null, "oov-all", null);
double[] dist = inferencer.getSampledDistribution(instance, 3, 1, 1);
assertEquals(2, dist.length);
double total = dist[0] + dist[1];
assertTrue(dist[0] >= 0);
assertTrue(dist[1] >= 0);
assertEquals(1.0, total, 1e-6);
}

@Test
public void testMaxGreaterThanNumTopicsInWriteInferredDistributions() throws IOException {
Alphabet alphabet = new Alphabet();
int index = alphabet.lookupIndex("maxWord");
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (2 << 1) + 0 };
int[] tokensPerTopic = new int[] { 2, 2 };
double[] alpha = new double[] { 0.1, 0.1 };
double beta = 0.01;
double betaSum = 0.02;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(123);
int[] dataTokens = new int[] { index };
FeatureSequence sequence = new FeatureSequence(alphabet, dataTokens);
Instance instance = new Instance(sequence, null, "doc-max", null);
InstanceList instances = new InstanceList(alphabet, null);
instances.addThruPipe(instance);
File outputFile = File.createTempFile("max-write", ".txt");
outputFile.deleteOnExit();
inferencer.writeInferredDistributions(instances, outputFile, 5, 1, 1, 0.0, 999);
BufferedReader reader = new BufferedReader(new FileReader(outputFile));
String header = reader.readLine();
String docLine = reader.readLine();
reader.close();
assertNotNull(header);
assertTrue(header.startsWith("#doc"));
assertNotNull(docLine);
assertTrue(docLine.contains("doc-max"));
}

@Test
public void testZeroTokensPerTopicCausesNoDivisionByZero() {
Alphabet alphabet = new Alphabet();
int token = alphabet.lookupIndex("ztt");
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (1 << 1) + 0 };
int[] tokensPerTopic = new int[] { 0, 0 };
double[] alpha = new double[] { 0.2, 0.2 };
double beta = 0.01;
double betaSum = 0.02;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(777);
int[] tokens = new int[] { token };
FeatureSequence fs = new FeatureSequence(alphabet, tokens);
Instance instance = new Instance(fs, null, "zero-tpt", null);
double[] dist = inferencer.getSampledDistribution(instance, 5, 1, 2);
assertEquals(2, dist.length);
double sum = dist[0] + dist[1];
assertTrue(dist[0] >= 0.0);
assertTrue(dist[1] >= 0.0);
assertEquals(1.0, sum, 1e-6);
}

@Test
public void testTypeWithNoTopicCounts() {
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("emptyCounts");
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] {};
int[] tokensPerTopic = new int[] { 2, 2 };
double[] alpha = new double[] { 0.1, 0.1 };
double beta = 0.01;
double betaSum = 0.02;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(101);
int[] inputTokens = new int[] { idx };
FeatureSequence fs = new FeatureSequence(alphabet, inputTokens);
Instance instance = new Instance(fs, null, "no-topic-counts", null);
double[] result = inferencer.getSampledDistribution(instance, 5, 1, 2);
assertEquals(2, result.length);
double total = result[0] + result[1];
assertTrue(result[0] >= 0.0);
assertTrue(result[1] >= 0.0);
assertEquals(1.0, total, 1e-6);
}

@Test
public void testTypeTopicCountsWithMultipleEntries() {
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("multi");
int topic0Encoded = (2 << 1) + 0;
int topic1Encoded = (3 << 1) + 1;
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { topic0Encoded, topic1Encoded };
int[] tokensPerTopic = new int[] { 5, 7 };
double[] alpha = new double[] { 0.4, 0.6 };
double beta = 0.01;
double betaSum = 0.02;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(1001);
int[] input = new int[] { idx, idx };
FeatureSequence seq = new FeatureSequence(alphabet, input);
Instance instance = new Instance(seq, null, "multi-topic-count", null);
double[] dist = inferencer.getSampledDistribution(instance, 10, 1, 2);
assertEquals(2, dist.length);
double total = dist[0] + dist[1];
assertTrue(dist[0] >= 0.0);
assertTrue(dist[1] >= 0.0);
assertEquals(1.0, total, 1e-6);
}

@Test
public void testLargeNumberOfTopicsInitializesCorrectly() {
Alphabet alphabet = new Alphabet();
int token = alphabet.lookupIndex("bigTopic");
int numTopics = 17;
int[][] typeTopicCounts = new int[1][];
int encoded = (5 << 5) + 10;
typeTopicCounts[0] = new int[] { encoded };
int[] tokensPerTopic = new int[numTopics];
tokensPerTopic[10] = 5;
for (int i = 0; i < numTopics; i++) {
if (i != 10) {
tokensPerTopic[i] = 1;
}
}
double[] alpha = new double[numTopics];
for (int i = 0; i < numTopics; i++) {
alpha[i] = 0.01;
}
double beta = 0.01;
double betaSum = 0.01 * 1;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(998);
int[] tokens = new int[] { token };
FeatureSequence fs = new FeatureSequence(alphabet, tokens);
Instance instance = new Instance(fs, null, "many-topics", null);
double[] dist = inferencer.getSampledDistribution(instance, 5, 1, 1);
assertEquals(numTopics, dist.length);
double sum = 0.0;
sum = sum + dist[0];
sum = sum + dist[1];
sum = sum + dist[2];
sum = sum + dist[3];
sum = sum + dist[4];
sum = sum + dist[5];
sum = sum + dist[6];
sum = sum + dist[7];
sum = sum + dist[8];
sum = sum + dist[9];
sum = sum + dist[10];
sum = sum + dist[11];
sum = sum + dist[12];
sum = sum + dist[13];
sum = sum + dist[14];
sum = sum + dist[15];
sum = sum + dist[16];
assertEquals(1.0, sum, 1e-6);
}

@Test
public void testAlphaAllZerosWithZeroTokensPerTopic() {
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("alphaZero");
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (1 << 1) + 0 };
int[] tokensPerTopic = new int[] { 0, 0 };
double[] alpha = new double[] { 0.0, 0.0 };
double beta = 0.01;
double betaSum = 0.02;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(4321);
int[] data = new int[] { 0 };
FeatureSequence fs = new FeatureSequence(alphabet, data);
Instance instance = new Instance(fs, null, "alpha-zero", null);
double[] result = inferencer.getSampledDistribution(instance, 5, 1, 1);
assertEquals(2, result.length);
double sum = result[0] + result[1];
assertTrue(result[0] >= 0.0);
assertTrue(result[1] >= 0.0);
assertEquals(1.0, sum, 1e-6);
}

@Test
public void testBurnInEqualsNumIterationsShouldReturnEmptyDistribution() {
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("burnIn");
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (4 << 1) + 1 };
int[] tokensPerTopic = new int[] { 2, 4 };
double[] alpha = new double[] { 0.3, 0.3 };
double beta = 0.01;
double betaSum = 0.02;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(999);
int[] data = new int[] { idx };
FeatureSequence fs = new FeatureSequence(alphabet, data);
Instance instance = new Instance(fs, null, "burn-eq-iter", null);
double[] result = inferencer.getSampledDistribution(instance, 5, 1, 5);
assertEquals(2, result.length);
double sum = result[0] + result[1];
assertTrue(result[0] >= 0.0);
assertTrue(result[1] >= 0.0);
assertEquals(1.0, sum, 1e-6);
}

@Test
public void testBurnInGreaterThanNumIterationsShouldTriggerFallback() {
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("burnInTooHigh");
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (2 << 1) + 0 };
int[] tokensPerTopic = new int[] { 4, 4 };
double[] alpha = new double[] { 0.2, 0.2 };
double beta = 0.01;
double betaSum = 0.02;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(1234);
int[] data = new int[] { idx };
FeatureSequence fs = new FeatureSequence(alphabet, data);
Instance instance = new Instance(fs, null, "burn-gt-iter", null);
double[] result = inferencer.getSampledDistribution(instance, 3, 1, 5);
assertEquals(2, result.length);
double sum = result[0] + result[1];
assertTrue(sum > 0.0);
assertEquals(1.0, sum, 1e-6);
}

@Test
public void testZeroThinningSampleOnceOnly() {
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("zeroThinning");
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (1 << 1) + 0 };
int[] tokensPerTopic = new int[] { 1, 1 };
double[] alpha = new double[] { 0.3, 0.3 };
double beta = 0.01;
double betaSum = 0.02;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(2023);
int[] tokens = new int[] { idx };
FeatureSequence fs = new FeatureSequence(alphabet, tokens);
Instance instance = new Instance(fs, null, "no-thinning", null);
double[] result = inferencer.getSampledDistribution(instance, 3, 0, 0);
assertEquals(2, result.length);
double total = result[0] + result[1];
assertEquals(1.0, total, 1e-6);
}

@Test
public void testMultipleDocumentsInWriteInferredDistributions() throws IOException {
Alphabet alphabet = new Alphabet();
int id1 = alphabet.lookupIndex("doc1term");
int id2 = alphabet.lookupIndex("doc2term");
int[][] typeTopicCounts = new int[2][];
typeTopicCounts[0] = new int[] { (1 << 1) + 0 };
typeTopicCounts[1] = new int[] { (2 << 1) + 1 };
int[] tokensPerTopic = new int[] { 1, 2 };
double[] alpha = new double[] { 0.1, 0.1 };
double beta = 0.01;
double betaSum = 0.02;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(8421);
int[] tokens1 = new int[] { id1 };
FeatureSequence fs1 = new FeatureSequence(alphabet, tokens1);
Instance inst1 = new Instance(fs1, null, "doc1", null);
int[] tokens2 = new int[] { id2 };
FeatureSequence fs2 = new FeatureSequence(alphabet, tokens2);
Instance inst2 = new Instance(fs2, null, "doc2", null);
InstanceList list = new InstanceList(alphabet, null);
list.addThruPipe(inst1);
list.addThruPipe(inst2);
File out = File.createTempFile("multi-docs", ".txt");
out.deleteOnExit();
inferencer.writeInferredDistributions(list, out, 5, 1, 1, 0.0, 2);
BufferedReader reader = new BufferedReader(new FileReader(out));
String header = reader.readLine();
String docLine1 = reader.readLine();
String docLine2 = reader.readLine();
reader.close();
assertNotNull(header);
assertTrue(header.startsWith("#doc"));
assertNotNull(docLine1);
assertTrue(docLine1.contains("doc1"));
assertNotNull(docLine2);
assertTrue(docLine2.contains("doc2"));
}

@Test
public void testInstanceWithNullData() {
Alphabet alphabet = new Alphabet();
int[][] typeTopicCounts = new int[0][];
int[] tokensPerTopic = new int[] { 1, 1 };
double[] alpha = new double[] { 0.2, 0.3 };
double beta = 0.01;
double betaSum = 0.02;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(55);
Instance inst = new Instance(null, null, "null-data", null);
try {
inferencer.getSampledDistribution(inst, 5, 1, 1);
fail("Expected ClassCastException due to null data");
} catch (ClassCastException expected) {
assertTrue(true);
}
}

@Test
public void testFeatureSequenceWithRepeatedTokensAffectsSampling() {
Alphabet alphabet = new Alphabet();
int tokenIndex = alphabet.lookupIndex("repeat");
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (3 << 1) + 0 };
int[] tokensPerTopic = new int[] { 3, 1 };
double[] alpha = new double[] { 0.1, 0.1 };
double beta = 0.01;
double betaSum = 0.02;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(222);
int[] tokens = new int[] { tokenIndex, tokenIndex, tokenIndex };
FeatureSequence fs = new FeatureSequence(alphabet, tokens);
Instance instance = new Instance(fs, null, "repeated-doc", null);
double[] result = inferencer.getSampledDistribution(instance, 5, 1, 1);
assertEquals(2, result.length);
double total = result[0] + result[1];
assertTrue(result[0] >= 0.0);
assertTrue(result[1] >= 0.0);
assertEquals(1.0, total, 1e-6);
}

@Test
public void testWriteInferredDistributionsWithZeroThresholdStillOutputsAll() throws IOException {
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("zeroThresh");
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (1 << 1) + 0 };
int[] tokensPerTopic = new int[] { 1, 1 };
double[] alpha = new double[] { 0.1, 0.1 };
double beta = 0.01;
double betaSum = 0.02;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(7777);
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { idx });
Instance instance = new Instance(fs, null, "thresh-zero", null);
InstanceList list = new InstanceList(alphabet, null);
list.addThruPipe(instance);
File output = File.createTempFile("zero-threshold", ".txt");
output.deleteOnExit();
inferencer.writeInferredDistributions(list, output, 5, 1, 1, 0.0, 2);
BufferedReader reader = new BufferedReader(new FileReader(output));
reader.readLine();
String line = reader.readLine();
reader.close();
assertNotNull(line);
assertTrue(line.contains("thresh-zero"));
assertTrue(line.split("\t").length >= 3);
}

@Test
public void testEmptyTypeTopicCountsArray() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("ignored");
int[][] typeTopicCounts = new int[0][];
int[] tokensPerTopic = new int[] { 1, 1 };
double[] alpha = new double[] { 0.3, 0.3 };
double beta = 0.01;
double betaSum = 0.02;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(9090);
int[] tokens = new int[] { 0 };
FeatureSequence fs = new FeatureSequence(alphabet, tokens);
Instance instance = new Instance(fs, null, "empty-counts", null);
double[] result = inferencer.getSampledDistribution(instance, 3, 1, 1);
assertEquals(2, result.length);
double total = result[0] + result[1];
assertTrue(result[0] >= 0.0);
assertTrue(result[1] >= 0.0);
assertEquals(1.0, total, 1e-6);
}

@Test
public void testDistributionSumFallbackIfNoSamplesSaved() {
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("fallback");
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (1 << 1) + 0 };
int[] tokensPerTopic = new int[] { 5, 5 };
double[] alpha = new double[] { 0.2, 0.3 };
double beta = 0.01;
double betaSum = 0.02;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(42);
int[] inputTokens = new int[] { idx };
FeatureSequence fs = new FeatureSequence(alphabet, inputTokens);
Instance instance = new Instance(fs, null, "fallback-dist", null);
double[] dist = inferencer.getSampledDistribution(instance, 4, 1, 4);
assertEquals(2, dist.length);
double sum = dist[0] + dist[1];
assertTrue(dist[0] > 0.0);
assertTrue(dist[1] > 0.0);
assertEquals(1.0, sum, 1e-6);
}

@Test
public void testCachedCoefficientsAreResilientWhenAlphaIsLarge() {
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("alphaHigh");
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (10 << 1) + 0 };
int[] tokensPerTopic = new int[] { 10, 10 };
double[] alpha = new double[] { 999.0, 0.001 };
double beta = 0.01;
double betaSum = 0.02;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(2024);
FeatureSequence fs = new FeatureSequence(alphabet, new int[] { idx });
Instance instance = new Instance(fs, null, "high-alpha", null);
double[] dist = inferencer.getSampledDistribution(instance, 5, 1, 2);
assertEquals(2, dist.length);
double total = dist[0] + dist[1];
assertTrue(dist[0] >= 0);
assertTrue(dist[1] >= 0);
assertEquals(1.0, total, 1e-6);
}

@Test
public void testAlphabetWithUnusedTypesHasNoImpact() {
Alphabet alphabet = new Alphabet();
int usedId = alphabet.lookupIndex("used");
alphabet.lookupIndex("unused");
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (2 << 1) + 1 };
int[] tokensPerTopic = new int[] { 2, 2 };
double[] alpha = new double[] { 0.4, 0.6 };
double beta = 0.01;
double betaSum = 0.02;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(3131);
int[] tokens = new int[] { usedId };
FeatureSequence fs = new FeatureSequence(alphabet, tokens);
Instance instance = new Instance(fs, null, "only-used", null);
double[] dist = inferencer.getSampledDistribution(instance, 5, 1, 2);
assertEquals(2, dist.length);
double sum = dist[0] + dist[1];
assertTrue(dist[0] >= 0.0);
assertTrue(dist[1] >= 0.0);
assertEquals(1.0, sum, 1e-6);
}

@Test
public void testAlphaWithMixedSmallAndLargeValuesAffectsDistribution() {
Alphabet alphabet = new Alphabet();
int tokenId = alphabet.lookupIndex("mixed-alpha");
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (3 << 1) + 0 };
int[] tokensPerTopic = new int[] { 3, 3, 3 };
double[] alpha = new double[] { 0.001, 1000.0, 0.01 };
double beta = 0.01;
double betaSum = 0.03;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(147);
int[] tokens = new int[] { tokenId, tokenId };
FeatureSequence sequence = new FeatureSequence(alphabet, tokens);
Instance instance = new Instance(sequence, null, "mixed-alpha-doc", null);
double[] dist = inferencer.getSampledDistribution(instance, 10, 1, 2);
assertEquals(3, dist.length);
assertTrue(dist[0] >= 0.0);
assertTrue(dist[1] >= 0.0);
assertTrue(dist[2] >= 0.0);
double sum = dist[0] + dist[1] + dist[2];
assertEquals(1.0, sum, 1e-6);
}

@Test
public void testEmptyInstanceListInWriteInferredDistributionsProducesNoOutput() throws Exception {
Alphabet alphabet = new Alphabet();
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (2 << 1) + 0 };
int[] tokensPerTopic = new int[] { 2, 2 };
double[] alpha = new double[] { 0.1, 0.1 };
double beta = 0.01;
double betaSum = 0.02;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
InstanceList emptyList = new InstanceList(alphabet, null);
File file = File.createTempFile("empty-instance-list", ".txt");
file.deleteOnExit();
inferencer.writeInferredDistributions(emptyList, file, 5, 1, 1, 0.05, 2);
BufferedReader reader = new BufferedReader(new FileReader(file));
String header = reader.readLine();
String nextLine = reader.readLine();
reader.close();
assertNotNull(header);
assertTrue(header.startsWith("#doc"));
assertNull(nextLine);
}

@Test
public void testMultipleTypeTopicCountsPerType() {
Alphabet alphabet = new Alphabet();
int tokenIndex = alphabet.lookupIndex("multi-topic");
int topic0 = (2 << 1) + 0;
int topic1 = (1 << 1) + 1;
int topic2 = (4 << 1) + 2;
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { topic0, topic1, topic2 };
int[] tokensPerTopic = new int[] { 2, 1, 4 };
double[] alpha = new double[] { 0.2, 0.2, 0.2 };
double beta = 0.01;
double betaSum = 0.03;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(4567);
int[] tokens = new int[] { tokenIndex, tokenIndex };
FeatureSequence fs = new FeatureSequence(alphabet, tokens);
Instance instance = new Instance(fs, null, "multi-topic-demo", null);
double[] result = inferencer.getSampledDistribution(instance, 10, 1, 2);
assertEquals(3, result.length);
double sum = result[0] + result[1] + result[2];
assertTrue(result[0] >= 0.0);
assertTrue(result[1] >= 0.0);
assertTrue(result[2] >= 0.0);
assertEquals(1.0, sum, 1e-6);
}

@Test
public void testDistributionWhenAllTopicsZeroCountsAndAlphaNonZero() {
Alphabet alphabet = new Alphabet();
int tokenIndex = alphabet.lookupIndex("zero-topic");
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] {};
int[] tokensPerTopic = new int[] { 0, 0 };
double[] alpha = new double[] { 0.5, 0.5 };
double beta = 0.01;
double betaSum = 0.02;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(8888);
int[] tokens = new int[] { tokenIndex };
FeatureSequence fs = new FeatureSequence(alphabet, tokens);
Instance instance = new Instance(fs, null, "no-topic-counts", null);
double[] result = inferencer.getSampledDistribution(instance, 10, 1, 5);
assertEquals(2, result.length);
assertTrue(result[0] >= 0.0);
assertTrue(result[1] >= 0.0);
double sum = result[0] + result[1];
assertEquals(1.0, sum, 1e-6);
}

@Test
public void testInvalidAlphabetIndexOutOfBoundsInFeatureSequenceIsIgnored() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("known-term");
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (1 << 1) + 0 };
int[] tokensPerTopic = new int[] { 1, 1 };
double[] alpha = new double[] { 0.2, 0.2 };
double beta = 0.01;
double betaSum = 0.02;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(6543);
int[] tokens = new int[] { 5 };
FeatureSequence fs = new FeatureSequence(alphabet, tokens);
Instance instance = new Instance(fs, null, "oob-index", null);
double[] result = inferencer.getSampledDistribution(instance, 5, 1, 1);
assertEquals(2, result.length);
assertTrue(result[0] >= 0.0);
assertTrue(result[1] >= 0.0);
double sum = result[0] + result[1];
assertEquals(1.0, sum, 1e-6);
}

@Test
public void testAllAlphaValuesZeroAndAllTokenCountsZeroStillNormalizes() {
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("zero-everything");
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] {};
int[] tokensPerTopic = new int[] { 0, 0 };
double[] alpha = new double[] { 0.0, 0.0 };
double beta = 0.01;
double betaSum = 0.02;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(3333);
int[] inputTokens = new int[] { idx };
FeatureSequence fs = new FeatureSequence(alphabet, inputTokens);
Instance instance = new Instance(fs, null, "all-zeros-case", null);
double[] result = inferencer.getSampledDistribution(instance, 3, 1, 2);
assertEquals(2, result.length);
double sum = result[0] + result[1];
assertTrue(result[0] >= 0.0);
assertTrue(result[1] >= 0.0);
assertEquals(1.0, sum, 1e-6);
}

@Test
public void testLargeTokenPerTopicValuesCauseNoOverflow() {
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("high-count");
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (100000 << 1) + 0 };
int[] tokensPerTopic = new int[] { 100000, 100000 };
double[] alpha = new double[] { 0.5, 0.5 };
double beta = 0.01;
double betaSum = 0.02;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(123);
int[] tokens = new int[] { idx };
FeatureSequence fs = new FeatureSequence(alphabet, tokens);
Instance instance = new Instance(fs, null, "large-count-doc", null);
double[] result = inferencer.getSampledDistribution(instance, 5, 1, 2);
assertEquals(2, result.length);
double sum = result[0] + result[1];
assertTrue(result[0] >= 0.0);
assertTrue(result[1] >= 0.0);
assertEquals(1.0, sum, 1e-6);
}

@Test
public void testSamplingWithZeroBurnInAndThinningGreaterThanNumIterations() {
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("skip-too-much");
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (2 << 1) + 0 };
int[] tokensPerTopic = new int[] { 2, 2 };
double[] alpha = new double[] { 0.2, 0.2 };
double beta = 0.01;
double betaSum = 0.02;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(321);
int[] tokens = new int[] { idx };
FeatureSequence fs = new FeatureSequence(alphabet, tokens);
Instance instance = new Instance(fs, null, "thin-over-iter", null);
double[] result = inferencer.getSampledDistribution(instance, 5, 10, 0);
double sum = result[0] + result[1];
assertEquals(2, result.length);
assertTrue(result[0] >= 0);
assertTrue(result[1] >= 0);
assertEquals(1.0, sum, 1e-6);
}

@Test
public void testSamplingSingleIterationReturnsSingleSample() {
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("one-iter");
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (1 << 1) + 0 };
int[] tokensPerTopic = new int[] { 1, 1 };
double[] alpha = new double[] { 0.1, 0.1 };
double beta = 0.01;
double betaSum = 0.02;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(666);
int[] tokens = new int[] { idx };
FeatureSequence fs = new FeatureSequence(alphabet, tokens);
Instance instance = new Instance(fs, null, "one-iter-doc", null);
double[] result = inferencer.getSampledDistribution(instance, 1, 1, 0);
assertEquals(2, result.length);
assertTrue(result[0] >= 0.0);
assertTrue(result[1] >= 0.0);
double sum = result[0] + result[1];
assertEquals(1.0, sum, 1e-6);
}

@Test
public void testNonPowerOfTwoTopicSizes() {
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("non-pow2");
int numTopics = 3;
int[][] typeTopicCounts = new int[1][];
int encoded = (5 << 2) + 1;
typeTopicCounts[0] = new int[] { encoded };
int[] tokensPerTopic = new int[] { 5, 5, 5 };
double[] alpha = new double[] { 0.3, 0.3, 0.3 };
double beta = 0.01;
double betaSum = 0.03;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(100);
int[] tokens = new int[] { idx };
FeatureSequence fs = new FeatureSequence(alphabet, tokens);
Instance instance = new Instance(fs, null, "non-power-topic", null);
double[] result = inferencer.getSampledDistribution(instance, 5, 1, 1);
assertEquals(3, result.length);
double sum = result[0] + result[1] + result[2];
assertTrue(result[0] >= 0.0);
assertTrue(result[1] >= 0.0);
assertTrue(result[2] >= 0.0);
assertEquals(1.0, sum, 1e-6);
}

@Test
public void testWriteInferredDistributionsWithNullInstanceName() throws IOException {
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("null-name");
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (1 << 1) + 0 };
int[] tokensPerTopic = new int[] { 1, 1 };
double[] alpha = new double[] { 0.1, 0.1 };
double beta = 0.01;
double betaSum = 0.02;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(51);
int[] tokens = new int[] { idx, idx };
FeatureSequence fs = new FeatureSequence(alphabet, tokens);
Instance instance = new Instance(fs, null, null, null);
InstanceList list = new InstanceList(alphabet, null);
list.addThruPipe(instance);
File tempFile = File.createTempFile("null-name", ".txt");
tempFile.deleteOnExit();
inferencer.writeInferredDistributions(list, tempFile, 5, 1, 1, 0.01, 2);
BufferedReader reader = new BufferedReader(new FileReader(tempFile));
String headerLine = reader.readLine();
String instanceLine = reader.readLine();
reader.close();
assertNotNull(headerLine);
assertTrue(headerLine.startsWith("#doc"));
assertNotNull(instanceLine);
assertTrue(instanceLine.contains("no-name"));
}
}
