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

public class TopicInferencer_llmsuite_1_GPTLLMTest {

@Test
public void testSampledDistributionProducesValidProbabilities() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("apple");
alphabet.lookupIndex("banana");
alphabet.lookupIndex("cherry");
int numTopics = 2;
double[] alpha = new double[2];
alpha[0] = 0.1;
alpha[1] = 0.1;
double beta = 0.01;
double betaSum = 0.1;
int[] tokensPerTopic = new int[2];
tokensPerTopic[0] = 10;
tokensPerTopic[1] = 15;
int[][] typeTopicCounts = new int[3][];
typeTopicCounts[0] = new int[] { (5 << 1) | 0 };
typeTopicCounts[1] = new int[] { (7 << 1) | 1 };
typeTopicCounts[2] = new int[] { (3 << 1) | 0 };
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(42);
ArrayList pipeList = new ArrayList();
pipeList.add(new CharSequence2TokenSequence());
pipeList.add(new TokenSequence2FeatureSequence(alphabet));
SerialPipes pipeline = new SerialPipes(pipeList);
Instance instance = new Instance("apple banana cherry", null, "test-doc", null);
instance = pipeline.instanceFrom(instance);
double[] distribution = inferencer.getSampledDistribution(instance, 10, 1, 2);
assertNotNull(distribution);
assertEquals(2, distribution.length);
assertTrue(distribution[0] >= 0.0);
assertTrue(distribution[0] <= 1.0);
assertTrue(distribution[1] >= 0.0);
assertTrue(distribution[1] <= 1.0);
double sum = distribution[0] + distribution[1];
assertEquals(1.0, sum, 1e-6);
}

@Test
public void testSampledDistributionHandlesOutOfVocabulary() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("apple");
int numTopics = 2;
double[] alpha = new double[2];
alpha[0] = 0.1;
alpha[1] = 0.1;
double beta = 0.01;
double betaSum = 0.1;
int[] tokensPerTopic = new int[2];
tokensPerTopic[0] = 5;
tokensPerTopic[1] = 6;
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (4 << 1) | 0 };
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(123);
ArrayList pipeList = new ArrayList();
pipeList.add(new CharSequence2TokenSequence());
pipeList.add(new TokenSequence2FeatureSequence(alphabet));
SerialPipes pipeline = new SerialPipes(pipeList);
Instance instance = new Instance("apple unknown unknown", null, "doc-OOV", null);
instance = pipeline.instanceFrom(instance);
double[] distribution = inferencer.getSampledDistribution(instance, 10, 1, 2);
assertNotNull(distribution);
assertEquals(2, distribution.length);
assertTrue(distribution[0] >= 0.0);
assertTrue(distribution[1] >= 0.0);
double sum = distribution[0] + distribution[1];
assertEquals(1.0, sum, 1e-6);
}

@Test
public void testGetSampledDistributionZeroIterations() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("a");
alphabet.lookupIndex("b");
int[][] typeTopicCounts = new int[2][];
typeTopicCounts[0] = new int[] { (3 << 1) | 0 };
typeTopicCounts[1] = new int[] { (2 << 1) | 1 };
int[] tokensPerTopic = new int[2];
tokensPerTopic[0] = 3;
tokensPerTopic[1] = 2;
double[] alpha = new double[2];
alpha[0] = 0.5;
alpha[1] = 0.5;
double beta = 0.01;
double betaSum = 0.1;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(999);
ArrayList pipeList = new ArrayList();
pipeList.add(new CharSequence2TokenSequence());
pipeList.add(new TokenSequence2FeatureSequence(alphabet));
SerialPipes pipeline = new SerialPipes(pipeList);
Instance instance = new Instance("a b", null, "zero-iter", null);
instance = pipeline.instanceFrom(instance);
double[] distribution = inferencer.getSampledDistribution(instance, 0, 1, 0);
assertNotNull(distribution);
assertEquals(2, distribution.length);
assertTrue(distribution[0] > 0.0);
assertTrue(distribution[1] > 0.0);
double sum = distribution[0] + distribution[1];
assertEquals(1.0, sum, 1e-6);
}

@Test
public void testWriteInferredDistributionsToFile() throws IOException {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("apple");
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (2 << 1) | 0 };
int[] tokensPerTopic = new int[1];
tokensPerTopic[0] = 2;
double[] alpha = new double[1];
alpha[0] = 0.1;
double beta = 0.01;
double betaSum = 0.01;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(55);
ArrayList pipeList = new ArrayList();
pipeList.add(new CharSequence2TokenSequence());
pipeList.add(new TokenSequence2FeatureSequence(alphabet));
SerialPipes pipeline = new SerialPipes(pipeList);
Instance instance = new Instance("apple apple apple", null, "docName", null);
instance = pipeline.instanceFrom(instance);
InstanceList instanceList = new InstanceList(pipeline);
instanceList.addThruPipe(instance);
File output = File.createTempFile("inferred", ".txt");
output.deleteOnExit();
inferencer.writeInferredDistributions(instanceList, output, 5, 1, 1, 0.0, -1);
BufferedReader reader = new BufferedReader(new FileReader(output));
String headerLine = reader.readLine();
assertNotNull(headerLine);
assertTrue(headerLine.contains("#doc"));
String dataLine = reader.readLine();
assertNotNull(dataLine);
String[] parts = dataLine.split("\\t");
assertEquals("0", parts[0]);
assertEquals("docName", parts[1]);
reader.close();
}

@Test
public void testSerializationRoundTrip() throws Exception {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("token");
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (1 << 1) | 0 };
int[] tokensPerTopic = new int[1];
tokensPerTopic[0] = 1;
double[] alpha = new double[1];
alpha[0] = 0.5;
double beta = 0.01;
double betaSum = 0.01;
TopicInferencer original = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
original.setRandomSeed(456);
ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
ObjectOutputStream out = new ObjectOutputStream(byteOut);
out.writeObject(original);
out.close();
byte[] data = byteOut.toByteArray();
ByteArrayInputStream byteIn = new ByteArrayInputStream(data);
ObjectInputStream in = new ObjectInputStream(byteIn);
TopicInferencer loaded = (TopicInferencer) in.readObject();
in.close();
assertNotNull(loaded);
ArrayList pipeList = new ArrayList();
pipeList.add(new CharSequence2TokenSequence());
pipeList.add(new TokenSequence2FeatureSequence(alphabet));
SerialPipes pipeline = new SerialPipes(pipeList);
Instance instance = new Instance("token token", null, "serial-test", null);
instance = pipeline.instanceFrom(instance);
double[] dist = loaded.getSampledDistribution(instance, 5, 1, 1);
assertNotNull(dist);
assertEquals(1, dist.length);
assertTrue(dist[0] > 0.0);
assertEquals(1.0, dist[0], 1e-6);
}

@Test(expected = FileNotFoundException.class)
public void testReadThrowsForMissingFile() throws Exception {
File nonExistent = new File("does_not_exist_9831173.ser");
TopicInferencer.read(nonExistent);
}

@Test
public void testEmptyInstanceReturnsSmoothedDistribution() {
Alphabet alphabet = new Alphabet();
int[][] typeTopicCounts = new int[0][];
int[] tokensPerTopic = new int[1];
double[] alpha = new double[] { 0.5 };
double beta = 0.01;
double betaSum = 0.01;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(42);
ArrayList pipes = new ArrayList();
pipes.add(new CharSequence2TokenSequence());
pipes.add(new TokenSequence2FeatureSequence(alphabet));
SerialPipes pipeline = new SerialPipes(pipes);
Instance instance = new Instance("", null, "empty", null);
instance = pipeline.instanceFrom(instance);
double[] result = inferencer.getSampledDistribution(instance, 10, 1, 2);
assertNotNull(result);
assertEquals(1, result.length);
assertEquals(1.0, result[0], 1e-6);
}

@Test
public void testAllOOVTokensGivesSmoothedDistribution() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("known");
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (5 << 1) | 0 };
int[] tokensPerTopic = new int[] { 5 };
double[] alpha = new double[] { 0.5 };
double beta = 0.01;
double betaSum = 0.01;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(7);
ArrayList pipes = new ArrayList();
pipes.add(new CharSequence2TokenSequence());
pipes.add(new TokenSequence2FeatureSequence(alphabet));
SerialPipes pipeline = new SerialPipes(pipes);
Instance instance = new Instance("unknown unknown", null, "doc-oov", null);
instance = pipeline.instanceFrom(instance);
double[] result = inferencer.getSampledDistribution(instance, 10, 1, 2);
assertNotNull(result);
assertEquals(1, result.length);
assertEquals(1.0, result[0], 1e-6);
}

@Test
public void testInferenceWithBurnInGreaterThanIterations() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("token");
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (3 << 1) | 0 };
int[] tokensPerTopic = new int[] { 3 };
double[] alpha = new double[] { 0.5 };
double beta = 0.01;
double betaSum = 0.05;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(10);
ArrayList pipes = new ArrayList();
pipes.add(new CharSequence2TokenSequence());
pipes.add(new TokenSequence2FeatureSequence(alphabet));
SerialPipes pipeline = new SerialPipes(pipes);
Instance instance = new Instance("token token", null, "burnin-case", null);
instance = pipeline.instanceFrom(instance);
double[] result = inferencer.getSampledDistribution(instance, 3, 1, 5);
assertNotNull(result);
assertEquals(1, result.length);
assertEquals(1.0, result[0], 1e-6);
}

@Test
public void testThinningGreaterThanNumIterations() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("token");
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (2 << 1) | 0 };
int[] tokensPerTopic = new int[] { 2 };
double[] alpha = new double[] { 0.5 };
double beta = 0.01;
double betaSum = 0.05;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(77);
ArrayList pipes = new ArrayList();
pipes.add(new CharSequence2TokenSequence());
pipes.add(new TokenSequence2FeatureSequence(alphabet));
SerialPipes pipeline = new SerialPipes(pipes);
Instance instance = new Instance("token token token", null, "thin-doc", null);
instance = pipeline.instanceFrom(instance);
double[] result = inferencer.getSampledDistribution(instance, 3, 10, 2);
assertNotNull(result);
assertEquals(1, result.length);
assertEquals(1.0, result[0], 1e-6);
}

@Test
public void testWriteInferredDistributionsWithEmptyInstanceList() throws IOException {
Alphabet alphabet = new Alphabet();
int[][] typeTopicCounts = new int[0][];
int[] tokensPerTopic = new int[1];
double[] alpha = new double[] { 0.5 };
double beta = 0.01;
double betaSum = 0.01;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(89);
ArrayList pipes = new ArrayList();
pipes.add(new CharSequence2TokenSequence());
pipes.add(new TokenSequence2FeatureSequence(alphabet));
SerialPipes pipeline = new SerialPipes(pipes);
InstanceList emptyList = new InstanceList(pipeline);
File output = File.createTempFile("empty-output", ".txt");
output.deleteOnExit();
inferencer.writeInferredDistributions(emptyList, output, 5, 1, 1, 0.0, -1);
BufferedReader reader = new BufferedReader(new FileReader(output));
String firstLine = reader.readLine();
assertNotNull(firstLine);
assertTrue(firstLine.startsWith("#doc"));
assertNull(reader.readLine());
reader.close();
}

@Test
public void testExtremeThresholdFiltersAllTopics() throws IOException {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("word");
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (3 << 1) | 0 };
int[] tokensPerTopic = new int[] { 3 };
double[] alpha = new double[] { 0.5 };
double beta = 0.01;
double betaSum = 0.1;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(101);
ArrayList pipes = new ArrayList();
pipes.add(new CharSequence2TokenSequence());
pipes.add(new TokenSequence2FeatureSequence(alphabet));
SerialPipes pipeline = new SerialPipes(pipes);
Instance instance = new Instance("word word", null, "filter-doc", null);
instance = pipeline.instanceFrom(instance);
InstanceList list = new InstanceList(pipeline);
list.addThruPipe(instance);
File file = File.createTempFile("high-thresh", ".out");
file.deleteOnExit();
inferencer.writeInferredDistributions(list, file, 5, 1, 1, 1.1, -1);
BufferedReader reader = new BufferedReader(new FileReader(file));
String line = reader.readLine();
assertTrue(line.startsWith("#doc"));
line = reader.readLine();
assertTrue(line.contains("filter-doc"));
assertTrue(line.split("\t").length <= 2);
reader.close();
}

@Test
public void testZeroTopicCountsHandledGracefully() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("x");
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[0];
int[] tokensPerTopic = new int[] { 5 };
double[] alpha = new double[] { 0.2 };
double beta = 0.01;
double betaSum = 0.01;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(12);
ArrayList pipes = new ArrayList();
pipes.add(new CharSequence2TokenSequence());
pipes.add(new TokenSequence2FeatureSequence(alphabet));
SerialPipes pipeline = new SerialPipes(pipes);
Instance instance = new Instance("x", null, "zero-count", null);
instance = pipeline.instanceFrom(instance);
double[] result = inferencer.getSampledDistribution(instance, 5, 1, 1);
assertNotNull(result);
assertEquals(1, result.length);
assertEquals(1.0, result[0], 1e-6);
}

@Test
public void testVeryLargeAlphaValue() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("a");
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (1 << 1) | 0 };
int[] tokensPerTopic = new int[] { 1 };
double[] alpha = new double[] { 1000.0 };
double beta = 0.01;
double betaSum = 0.1;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(333);
ArrayList pipes = new ArrayList();
pipes.add(new CharSequence2TokenSequence());
pipes.add(new TokenSequence2FeatureSequence(alphabet));
SerialPipes pipeline = new SerialPipes(pipes);
Instance instance = new Instance("a a a a a", null, "high-alpha", null);
instance = pipeline.instanceFrom(instance);
double[] result = inferencer.getSampledDistribution(instance, 10, 1, 2);
assertNotNull(result);
assertEquals(1, result.length);
assertEquals(1.0, result[0], 1e-6);
}

@Test
public void testMultipleTopicsSameHighestCountInitialization() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("token");
int[][] typeTopicCounts = new int[1][];
int topicBits = 2;
int encoded1 = (5 << topicBits) + 0;
int encoded2 = (5 << topicBits) + 1;
typeTopicCounts[0] = new int[] { encoded1, encoded2 };
double[] alpha = new double[] { 0.1, 0.1 };
int[] tokensPerTopic = new int[] { 5, 5 };
double beta = 0.01;
double betaSum = 0.02;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(50);
ArrayList pipes = new ArrayList();
pipes.add(new CharSequence2TokenSequence());
pipes.add(new TokenSequence2FeatureSequence(alphabet));
SerialPipes pipeline = new SerialPipes(pipes);
Instance instance = new Instance("token token", null, "ambiguous", null);
instance = pipeline.instanceFrom(instance);
double[] result = inferencer.getSampledDistribution(instance, 5, 1, 0);
assertEquals(2, result.length);
assertTrue(result[0] >= 0.0);
assertTrue(result[1] >= 0.0);
assertEquals(1.0, result[0] + result[1], 1e-6);
}

@Test
public void testMixedOOVAndInVocabTokens() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("valid");
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (3 << 1) | 0 };
double[] alpha = new double[] { 0.1 };
int[] tokensPerTopic = new int[] { 3 };
double beta = 0.01;
double betaSum = 0.05;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(222);
ArrayList pipes = new ArrayList();
pipes.add(new CharSequence2TokenSequence());
pipes.add(new TokenSequence2FeatureSequence(alphabet));
SerialPipes pipeline = new SerialPipes(pipes);
Instance instance = new Instance("valid unknown unknown valid", null, "mixed-tokens", null);
instance = pipeline.instanceFrom(instance);
double[] result = inferencer.getSampledDistribution(instance, 5, 1, 1);
assertEquals(1, result.length);
assertEquals(1.0, result[0], 1e-6);
}

@Test
public void testSingleTokenDocument() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("only");
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (1 << 1) | 0 };
double[] alpha = new double[] { 1.0 };
int[] tokensPerTopic = new int[] { 1 };
double beta = 0.01;
double betaSum = 0.05;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(580);
ArrayList pipes = new ArrayList();
pipes.add(new CharSequence2TokenSequence());
pipes.add(new TokenSequence2FeatureSequence(alphabet));
SerialPipes pipeline = new SerialPipes(pipes);
Instance instance = new Instance("only", null, "single-token", null);
instance = pipeline.instanceFrom(instance);
double[] result = inferencer.getSampledDistribution(instance, 1, 1, 0);
assertEquals(1, result.length);
assertEquals(1.0, result[0], 1e-6);
}

@Test
public void testAlphaZeroProducesValidDistribution() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("z");
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (4 << 1) | 0 };
double[] alpha = new double[] { 0.0 };
int[] tokensPerTopic = new int[] { 4 };
double beta = 0.01;
double betaSum = 0.2;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(313);
ArrayList pipes = new ArrayList();
pipes.add(new CharSequence2TokenSequence());
pipes.add(new TokenSequence2FeatureSequence(alphabet));
SerialPipes pipeline = new SerialPipes(pipes);
Instance instance = new Instance("z", null, "alpha-zero", null);
instance = pipeline.instanceFrom(instance);
double[] result = inferencer.getSampledDistribution(instance, 2, 1, 1);
assertEquals(1, result.length);
assertEquals(1.0, result[0], 1e-6);
}

@Test
public void testTokensPerTopicZeroHandledGracefully() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("x");
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (2 << 1) | 0 };
double[] alpha = new double[] { 0.1 };
int[] tokensPerTopic = new int[] { 0 };
double beta = 0.01;
double betaSum = 0.1;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(444);
ArrayList pipes = new ArrayList();
pipes.add(new CharSequence2TokenSequence());
pipes.add(new TokenSequence2FeatureSequence(alphabet));
SerialPipes pipeline = new SerialPipes(pipes);
Instance instance = new Instance("x x", null, "tpt-zero", null);
instance = pipeline.instanceFrom(instance);
double[] result = inferencer.getSampledDistribution(instance, 5, 1, 1);
assertEquals(1, result.length);
assertEquals(1.0, result[0], 1e-6);
}

@Test
public void testTopicValueExceedsTopicMaskIgnoredGracefully() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("a");
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (3 << 3) | 7 };
double[] alpha = new double[] { 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2 };
int[] tokensPerTopic = new int[] { 3, 3, 3, 3, 3, 3, 3, 3 };
double beta = 0.01;
double betaSum = 0.08;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(8);
ArrayList pipes = new ArrayList();
pipes.add(new CharSequence2TokenSequence());
pipes.add(new TokenSequence2FeatureSequence(alphabet));
SerialPipes pipeline = new SerialPipes(pipes);
Instance instance = new Instance("a", null, "mask-over", null);
instance = pipeline.instanceFrom(instance);
double[] result = inferencer.getSampledDistribution(instance, 2, 1, 0);
assertEquals(8, result.length);
double sum = 0.0;
sum = sum + result[0];
sum = sum + result[1];
sum = sum + result[2];
sum = sum + result[3];
sum = sum + result[4];
sum = sum + result[5];
sum = sum + result[6];
sum = sum + result[7];
assertEquals(1.0, sum, 1e-6);
}

@Test
public void testThresholdFilteringCapsToMaxTopics() throws IOException {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("a");
alphabet.lookupIndex("b");
int[][] typeTopicCounts = new int[2][];
typeTopicCounts[0] = new int[] { (5 << 1) | 0 };
typeTopicCounts[1] = new int[] { (5 << 1) | 1 };
double[] alpha = new double[] { 0.5, 0.5 };
int[] tokensPerTopic = new int[] { 5, 5 };
double beta = 0.01;
double betaSum = 0.1;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(9);
ArrayList pipes = new ArrayList();
pipes.add(new CharSequence2TokenSequence());
pipes.add(new TokenSequence2FeatureSequence(alphabet));
SerialPipes pipeline = new SerialPipes(pipes);
Instance instance = new Instance("a b", null, "filtermax", null);
instance = pipeline.instanceFrom(instance);
InstanceList list = new InstanceList(pipeline);
list.addThruPipe(instance);
File out = File.createTempFile("maxout", ".txt");
out.deleteOnExit();
inferencer.writeInferredDistributions(list, out, 5, 1, 1, 0.01, 1);
BufferedReader reader = new BufferedReader(new FileReader(out));
String header = reader.readLine();
String body = reader.readLine();
int tabCount = body.split("\t").length;
assertTrue(tabCount <= 4);
reader.close();
}

@Test
public void testNegativeThinningDoesNotCrash() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("a");
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (2 << 1) | 0 };
int[] tokensPerTopic = new int[] { 2 };
double[] alpha = new double[] { 0.2 };
double beta = 0.01;
double betaSum = 0.1;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(75);
ArrayList pipes = new ArrayList();
pipes.add(new CharSequence2TokenSequence());
pipes.add(new TokenSequence2FeatureSequence(alphabet));
SerialPipes pipeline = new SerialPipes(pipes);
Instance instance = new Instance("a a", null, "neg-thin", null);
instance = pipeline.instanceFrom(instance);
double[] result = inferencer.getSampledDistribution(instance, 5, -1, 0);
assertNotNull(result);
assertEquals(1, result.length);
}

@Test
public void testNegativeBurnInDoesNotCrash() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("a");
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (1 << 1) | 0 };
int[] tokensPerTopic = new int[] { 1 };
double[] alpha = new double[] { 0.5 };
double beta = 0.01;
double betaSum = 0.1;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(202);
ArrayList pipes = new ArrayList();
pipes.add(new CharSequence2TokenSequence());
pipes.add(new TokenSequence2FeatureSequence(alphabet));
SerialPipes pipeline = new SerialPipes(pipes);
Instance instance = new Instance("a", null, "neg-burn", null);
instance = pipeline.instanceFrom(instance);
double[] result = inferencer.getSampledDistribution(instance, 5, 1, -4);
assertNotNull(result);
assertEquals(1, result.length);
}

@Test
public void testTypeWithEmptyTypeTopicCountsSkippedDuringInitialization() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("a");
alphabet.lookupIndex("b");
int[][] typeTopicCounts = new int[2][];
typeTopicCounts[0] = new int[] {};
typeTopicCounts[1] = new int[] { (2 << 1) | 0 };
int[] tokensPerTopic = new int[] { 2 };
double[] alpha = new double[] { 0.1 };
double beta = 0.01;
double betaSum = 0.05;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(1);
ArrayList pipeList = new ArrayList();
pipeList.add(new CharSequence2TokenSequence());
pipeList.add(new TokenSequence2FeatureSequence(alphabet));
SerialPipes pipes = new SerialPipes(pipeList);
Instance inst = new Instance("a b", null, "null-topic-count", null);
inst = pipes.instanceFrom(inst);
double[] result = inferencer.getSampledDistribution(inst, 5, 1, 1);
assertNotNull(result);
assertEquals(1, result.length);
assertEquals(1.0, result[0], 1e-6);
}

@Test
public void testInstanceWithNullNameAppearsAsNoNameInWriteOutput() throws IOException {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("doc");
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (3 << 1) | 0 };
int[] tokensPerTopic = new int[] { 3 };
double[] alpha = new double[] { 0.5 };
double beta = 0.01;
double betaSum = 0.05;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(5);
ArrayList pipeList = new ArrayList();
pipeList.add(new CharSequence2TokenSequence());
pipeList.add(new TokenSequence2FeatureSequence(alphabet));
SerialPipes pipes = new SerialPipes(pipeList);
Instance instance = new Instance("doc doc", null, null, null);
instance = pipes.instanceFrom(instance);
InstanceList instanceList = new InstanceList(pipes);
instanceList.addThruPipe(instance);
File tempFile = File.createTempFile("topic-infer", ".txt");
tempFile.deleteOnExit();
inferencer.writeInferredDistributions(instanceList, tempFile, 10, 1, 1, 0.0, -1);
BufferedReader reader = new BufferedReader(new FileReader(tempFile));
reader.readLine();
String outputLine = reader.readLine();
assertTrue(outputLine.contains("no-name"));
reader.close();
}

@Test
public void testWriteInferredDistributionsWithNegativeMaxTreatsItAsUnlimited() throws IOException {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("cat");
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (4 << 1) | 0, (2 << 1) | 1 };
int[] tokensPerTopic = new int[] { 4, 2 };
double[] alpha = new double[] { 0.2, 0.2 };
double beta = 0.01;
double betaSum = 0.1;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(42);
ArrayList pipeList = new ArrayList();
pipeList.add(new CharSequence2TokenSequence());
pipeList.add(new TokenSequence2FeatureSequence(alphabet));
SerialPipes pipes = new SerialPipes(pipeList);
Instance instance = new Instance("cat cat", null, "neg-max", null);
instance = pipes.instanceFrom(instance);
InstanceList instanceList = new InstanceList(pipes);
instanceList.addThruPipe(instance);
File output = File.createTempFile("negmax", ".txt");
output.deleteOnExit();
inferencer.writeInferredDistributions(instanceList, output, 5, 1, 1, 0.01, -5);
BufferedReader reader = new BufferedReader(new FileReader(output));
reader.readLine();
String line = reader.readLine();
assertNotNull(line);
assertTrue(line.contains("neg-max"));
reader.close();
}

@Test
public void testHighNumberOfTopicsPowerOfTwoInitialization() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("t");
int numTopics = 8;
int[] tokensPerTopic = new int[numTopics];
double[] alpha = new double[numTopics];
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (2 << 3) | 0, (2 << 3) | 1, (2 << 3) | 2, (2 << 3) | 3, (2 << 3) | 4, (2 << 3) | 5, (2 << 3) | 6, (2 << 3) | 7 };
tokensPerTopic[0] = 2;
tokensPerTopic[1] = 2;
tokensPerTopic[2] = 2;
tokensPerTopic[3] = 2;
tokensPerTopic[4] = 2;
tokensPerTopic[5] = 2;
tokensPerTopic[6] = 2;
tokensPerTopic[7] = 2;
alpha[0] = 0.1;
alpha[1] = 0.1;
alpha[2] = 0.1;
alpha[3] = 0.1;
alpha[4] = 0.1;
alpha[5] = 0.1;
alpha[6] = 0.1;
alpha[7] = 0.1;
double beta = 0.01;
double betaSum = 0.08;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(100);
ArrayList pipeList = new ArrayList();
pipeList.add(new CharSequence2TokenSequence());
pipeList.add(new TokenSequence2FeatureSequence(alphabet));
SerialPipes pipes = new SerialPipes(pipeList);
Instance instance = new Instance("t t", null, "8topics", null);
instance = pipes.instanceFrom(instance);
double[] result = inferencer.getSampledDistribution(instance, 5, 1, 1);
assertEquals(8, result.length);
double sum = result[0] + result[1] + result[2] + result[3] + result[4] + result[5] + result[6] + result[7];
assertEquals(1.0, sum, 1e-6);
}

@Test
public void testHighTopicIndexInsertionInDenseTopicIndex() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("w");
int numTopics = 4;
int[] tokensPerTopic = new int[] { 3, 3, 3, 3 };
double[] alpha = new double[] { 0.1, 0.1, 0.1, 0.1 };
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (3 << 2) | 0, (3 << 2) | 3 };
double beta = 0.01;
double betaSum = 0.2;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(451);
ArrayList pipeList = new ArrayList();
pipeList.add(new CharSequence2TokenSequence());
pipeList.add(new TokenSequence2FeatureSequence(alphabet));
SerialPipes pipes = new SerialPipes(pipeList);
Instance instance = new Instance("w w w", null, "insert-dense", null);
instance = pipes.instanceFrom(instance);
double[] result = inferencer.getSampledDistribution(instance, 10, 1, 2);
assertEquals(4, result.length);
double sum = result[0] + result[1] + result[2] + result[3];
assertEquals(1.0, sum, 1e-6);
}

@Test
public void testDeserializedFromFileMaintainsCorrectBehavior() throws Exception {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("apple");
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (2 << 1) | 0 };
int[] tokensPerTopic = new int[] { 2 };
double[] alpha = new double[] { 0.1 };
double beta = 0.01;
double betaSum = 0.05;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(2024);
File tempFile = File.createTempFile("inferencer", ".ser");
tempFile.deleteOnExit();
ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(tempFile));
out.writeObject(inferencer);
out.close();
TopicInferencer loaded = TopicInferencer.read(tempFile);
ArrayList pipes = new ArrayList();
pipes.add(new CharSequence2TokenSequence());
pipes.add(new TokenSequence2FeatureSequence(alphabet));
SerialPipes serialPipes = new SerialPipes(pipes);
Instance instance = new Instance("apple", null, "doc", null);
instance = serialPipes.instanceFrom(instance);
double[] result = loaded.getSampledDistribution(instance, 5, 1, 1);
assertNotNull(result);
assertEquals(1, result.length);
assertEquals(1.0, result[0], 1e-6);
}

@Test
public void testTypeWithTwoTopicsEnsuresCorrectSampling() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("poly");
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (2 << 2) | 0, (4 << 2) | 1 };
int[] tokensPerTopic = new int[] { 2, 4 };
double[] alpha = new double[] { 0.1, 0.1 };
double beta = 0.01;
double betaSum = 0.05;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(77);
ArrayList pipes = new ArrayList();
pipes.add(new CharSequence2TokenSequence());
pipes.add(new TokenSequence2FeatureSequence(alphabet));
SerialPipes serialPipes = new SerialPipes(pipes);
Instance instance = new Instance("poly poly poly", null, "multi-topic", null);
instance = serialPipes.instanceFrom(instance);
double[] result = inferencer.getSampledDistribution(instance, 10, 1, 2);
assertNotNull(result);
assertEquals(2, result.length);
double total = result[0] + result[1];
assertEquals(1.0, total, 1e-6);
}

@Test
public void testEmptyAlphaDoesNotCrashGetSampledDistribution() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("solo");
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (3 << 1) | 0 };
int[] tokensPerTopic = new int[] { 3 };
double[] alpha = new double[] { 0.0 };
double beta = 0.01;
double betaSum = 0.02;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(1234);
ArrayList pipes = new ArrayList();
pipes.add(new CharSequence2TokenSequence());
pipes.add(new TokenSequence2FeatureSequence(alphabet));
SerialPipes serial = new SerialPipes(pipes);
Instance instance = new Instance("solo solo", null, "zero-alpha", null);
instance = serial.instanceFrom(instance);
double[] result = inferencer.getSampledDistribution(instance, 5, 1, 1);
assertEquals(1, result.length);
assertTrue(result[0] > 0.0);
assertEquals(1.0, result[0], 1e-6);
}

@Test
public void testSingleTermWithSingleTopicResultsInStableSampling() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("term");
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (1 << 2) | 2 };
int[] tokensPerTopic = new int[] { 0, 0, 1, 0 };
double[] alpha = new double[] { 0.1, 0.1, 0.1, 0.1 };
double beta = 0.01;
double betaSum = 0.4;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(99);
ArrayList pipeList = new ArrayList();
pipeList.add(new CharSequence2TokenSequence());
pipeList.add(new TokenSequence2FeatureSequence(alphabet));
SerialPipes pipes = new SerialPipes(pipeList);
Instance instance = new Instance("term term term", null, "single-topic-triple", null);
instance = pipes.instanceFrom(instance);
double[] result = inferencer.getSampledDistribution(instance, 6, 1, 2);
assertEquals(4, result.length);
double sum = result[0] + result[1] + result[2] + result[3];
assertEquals(1.0, sum, 1e-6);
}

@Test
public void testNoSavedSamplesFallbackToFinalAlphaPlusCounts() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("late");
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (2 << 1) | 0 };
int[] tokensPerTopic = new int[] { 2 };
double[] alpha = new double[] { 0.5 };
double beta = 0.01;
double betaSum = 0.05;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(19);
ArrayList pipes = new ArrayList();
pipes.add(new CharSequence2TokenSequence());
pipes.add(new TokenSequence2FeatureSequence(alphabet));
SerialPipes serial = new SerialPipes(pipes);
Instance instance = new Instance("late", null, "no-saved", null);
instance = serial.instanceFrom(instance);
double[] result = inferencer.getSampledDistribution(instance, 1, 10, 1);
assertEquals(1, result.length);
assertEquals(1.0, result[0], 1e-6);
}

@Test
public void testEmptyTokenSequenceLeadsToSmoothedAlphaDistribution() {
Alphabet alphabet = new Alphabet();
int[][] typeTopicCounts = new int[0][];
int[] tokensPerTopic = new int[] { 0 };
double[] alpha = new double[] { 0.3 };
double beta = 0.01;
double betaSum = 0.01;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(11);
ArrayList pipeList = new ArrayList();
pipeList.add(new CharSequence2TokenSequence());
pipeList.add(new TokenSequence2FeatureSequence(alphabet));
SerialPipes pipeline = new SerialPipes(pipeList);
Instance instance = new Instance("", null, "empty-input", null);
instance = pipeline.instanceFrom(instance);
double[] result = inferencer.getSampledDistribution(instance, 3, 1, 1);
assertNotNull(result);
assertEquals(1, result.length);
assertEquals(1.0, result[0], 1e-6);
}

@Test
public void testNonZeroTopicBecomesZeroThenReinsertedIntoDenseIndex() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("x");
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (3 << 2) | 0, (1 << 2) | 2 };
int[] tokensPerTopic = new int[] { 3, 0, 1, 0 };
double[] alpha = new double[] { 0.1, 0.1, 0.1, 0.1 };
double beta = 0.01;
double betaSum = 0.5;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(88);
ArrayList pipes = new ArrayList();
pipes.add(new CharSequence2TokenSequence());
pipes.add(new TokenSequence2FeatureSequence(alphabet));
SerialPipes pipeline = new SerialPipes(pipes);
Instance instance = new Instance("x x x x", null, "dense-test", null);
instance = pipeline.instanceFrom(instance);
double[] result = inferencer.getSampledDistribution(instance, 15, 1, 3);
assertEquals(4, result.length);
double sum = result[0] + result[1] + result[2] + result[3];
assertEquals(1.0, sum, 1e-6);
}

@Test
public void testInstanceWithRepeatedSameTermStillProducesValidDistribution() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("repeat");
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (6 << 1) | 0 };
int[] tokensPerTopic = new int[] { 6 };
double[] alpha = new double[] { 0.5 };
double beta = 0.01;
double betaSum = 0.01;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(5001);
ArrayList pipeList = new ArrayList();
pipeList.add(new CharSequence2TokenSequence());
pipeList.add(new TokenSequence2FeatureSequence(alphabet));
SerialPipes pipeline = new SerialPipes(pipeList);
Instance instance = new Instance("repeat repeat repeat repeat repeat", null, "repeats", null);
instance = pipeline.instanceFrom(instance);
double[] result = inferencer.getSampledDistribution(instance, 10, 1, 2);
assertNotNull(result);
assertEquals(1, result.length);
assertEquals(1.0, result[0], 1e-6);
}

@Test
public void testConstructorWithNoTopicsDoesNotCrash() {
Alphabet alphabet = new Alphabet();
int[][] typeTopicCounts = new int[0][];
int[] tokensPerTopic = new int[0];
double[] alpha = new double[0];
double beta = 0.01;
double betaSum = 0.01;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
assertNotNull(inferencer);
}

@Test
public void testConstructorWithZeroLengthAlphaArrayHandlesGracefully() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("w");
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[0];
int[] tokensPerTopic = new int[0];
double[] alpha = new double[0];
double beta = 0.01;
double betaSum = 0.01;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
assertNotNull(inferencer);
}

@Test
public void testTopicIdEqualsMaskMaximumHandled() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("k");
int topicBits = 3;
int topicId = 7;
int value = 4;
int encoded = (value << topicBits) | topicId;
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { encoded };
int[] tokensPerTopic = new int[8];
tokensPerTopic[7] = value;
double[] alpha = new double[] { 0.05, 0.05, 0.05, 0.05, 0.05, 0.05, 0.05, 0.05 };
double beta = 0.01;
double betaSum = 0.2;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(99);
ArrayList pipeList = new ArrayList();
pipeList.add(new CharSequence2TokenSequence());
pipeList.add(new TokenSequence2FeatureSequence(alphabet));
SerialPipes pipeline = new SerialPipes(pipeList);
Instance instance = new Instance("k k k", null, "topic-mask", null);
instance = pipeline.instanceFrom(instance);
double[] result = inferencer.getSampledDistribution(instance, 3, 1, 1);
assertEquals(8, result.length);
double sum = result[0] + result[1] + result[2] + result[3] + result[4] + result[5] + result[6] + result[7];
assertEquals(1.0, sum, 1e-6);
}

@Test
public void testGetSampledDistributionWithZeroThinningStillCompletes() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("zero");
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (2 << 1) | 0 };
double[] alpha = new double[] { 0.1 };
int[] tokensPerTopic = new int[] { 2 };
double beta = 0.01;
double betaSum = 0.05;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(1000);
ArrayList pipeList = new ArrayList();
pipeList.add(new CharSequence2TokenSequence());
pipeList.add(new TokenSequence2FeatureSequence(alphabet));
SerialPipes pipeline = new SerialPipes(pipeList);
Instance instance = new Instance("zero zero", null, "thinning-zero", null);
instance = pipeline.instanceFrom(instance);
double[] result = inferencer.getSampledDistribution(instance, 5, 0, 1);
assertEquals(1, result.length);
assertEquals(1.0, result[0], 1e-6);
}

@Test
public void testNegativeTokensPerTopicHandled() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("t");
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (1 << 1) | 0 };
int[] tokensPerTopic = new int[] { -3 };
double[] alpha = new double[] { 0.1 };
double beta = 0.01;
double betaSum = 0.05;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(777);
ArrayList pipeList = new ArrayList();
pipeList.add(new CharSequence2TokenSequence());
pipeList.add(new TokenSequence2FeatureSequence(alphabet));
SerialPipes pipes = new SerialPipes(pipeList);
Instance instance = new Instance("t t t", null, "negative-tpt", null);
instance = pipes.instanceFrom(instance);
double[] result = inferencer.getSampledDistribution(instance, 5, 1, 1);
assertNotNull(result);
assertEquals(1, result.length);
assertTrue(result[0] >= 0.0);
assertEquals(1.0, result[0], 1e-6);
}

@Test
public void testEmptyStringInputYieldsSmoothedAlphaOnly() {
Alphabet alphabet = new Alphabet();
int[][] typeTopicCounts = new int[0][];
int[] tokensPerTopic = new int[] { 0, 0 };
double[] alpha = new double[] { 0.1, 0.1 };
double beta = 0.01;
double betaSum = 0.02;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(500);
ArrayList pipeList = new ArrayList();
pipeList.add(new CharSequence2TokenSequence());
pipeList.add(new TokenSequence2FeatureSequence(alphabet));
SerialPipes pipeline = new SerialPipes(pipeList);
Instance instance = new Instance("", null, "empty-input", null);
instance = pipeline.instanceFrom(instance);
double[] result = inferencer.getSampledDistribution(instance, 5, 1, 1);
assertNotNull(result);
assertEquals(2, result.length);
double total = result[0] + result[1];
assertEquals(1.0, total, 1e-6);
}

@Test
public void testGetSampledDistributionHandlesNullInstanceData() {
Alphabet alphabet = new Alphabet();
int[][] typeTopicCounts = new int[0][];
int[] tokensPerTopic = new int[1];
double[] alpha = new double[] { 0.1 };
double beta = 0.01;
double betaSum = 0.01;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(10);
Instance instance = new Instance(null, null, "null", null);
double[] distribution = inferencer.getSampledDistribution(instance, 5, 1, 1);
assertNotNull(distribution);
assertEquals(1, distribution.length);
assertEquals(1.0, distribution[0], 1e-6);
}

@Test
public void testBurnInGreaterThanIterationsYieldsFallbackDistribution() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("word");
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (2 << 1) | 0 };
int[] tokensPerTopic = new int[] { 2 };
double[] alpha = new double[] { 0.5 };
double beta = 0.01;
double betaSum = 0.01;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(123);
ArrayList pipeList = new ArrayList();
pipeList.add(new CharSequence2TokenSequence());
pipeList.add(new TokenSequence2FeatureSequence(alphabet));
SerialPipes pipeline = new SerialPipes(pipeList);
Instance instance = new Instance("word", null, "burn-in", null);
instance = pipeline.instanceFrom(instance);
double[] result = inferencer.getSampledDistribution(instance, 2, 1, 10);
assertEquals(1, result.length);
assertEquals(1.0, result[0], 1e-6);
}

@Test
public void testThinningGreaterThanIterationsStillReturnsValidDistribution() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("term");
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (3 << 1) | 0 };
int[] tokensPerTopic = new int[] { 3 };
double[] alpha = new double[] { 1.0 };
double beta = 0.01;
double betaSum = 0.01;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(99);
ArrayList pipes = new ArrayList();
pipes.add(new CharSequence2TokenSequence());
pipes.add(new TokenSequence2FeatureSequence(alphabet));
SerialPipes serialPipes = new SerialPipes(pipes);
Instance instance = new Instance("term term", null, "thin-large", null);
instance = serialPipes.instanceFrom(instance);
double[] result = inferencer.getSampledDistribution(instance, 2, 5, 0);
assertNotNull(result);
assertEquals(1, result.length);
assertEquals(1.0, result[0], 1e-6);
}

@Test
public void testUnbalancedAlphaPriorYieldsDifferentDistribution() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("dog");
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (2 << 1) | 0 };
int[] tokensPerTopic = new int[] { 2, 2 };
double[] alpha = new double[] { 100.0, 0.1 };
double beta = 0.01;
double betaSum = 0.02;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(456);
ArrayList pipes = new ArrayList();
pipes.add(new CharSequence2TokenSequence());
pipes.add(new TokenSequence2FeatureSequence(alphabet));
SerialPipes pipeline = new SerialPipes(pipes);
Instance instance = new Instance("dog", null, "bias-test", null);
instance = pipeline.instanceFrom(instance);
double[] result = inferencer.getSampledDistribution(instance, 5, 1, 1);
assertEquals(2, result.length);
assertTrue(result[0] > result[1]);
assertEquals(1.0, result[0] + result[1], 1e-6);
}

@Test
public void testAlternatingOOVAndInVocabTokens() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("real");
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (2 << 1) | 0 };
int[] tokensPerTopic = new int[] { 2 };
double[] alpha = new double[] { 0.1 };
double beta = 0.01;
double betaSum = 0.01;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(700);
ArrayList pipes = new ArrayList();
pipes.add(new CharSequence2TokenSequence());
pipes.add(new TokenSequence2FeatureSequence(alphabet));
SerialPipes pipeline = new SerialPipes(pipes);
Instance instance = new Instance("real fake real fake real", null, "zebra", null);
instance = pipeline.instanceFrom(instance);
double[] result = inferencer.getSampledDistribution(instance, 5, 1, 1);
assertEquals(1, result.length);
assertEquals(1.0, result[0], 1e-6);
}

@Test
public void testWriteDistributionRespectsThresholdAndMax() throws IOException {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("fruit");
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (8 << 2) | 0, (3 << 2) | 1, (2 << 2) | 2 };
int[] tokensPerTopic = new int[] { 8, 3, 2 };
double[] alpha = new double[] { 0.1, 0.1, 0.1 };
double beta = 0.01;
double betaSum = 0.3;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(82);
ArrayList pipes = new ArrayList();
pipes.add(new CharSequence2TokenSequence());
pipes.add(new TokenSequence2FeatureSequence(alphabet));
SerialPipes pipeline = new SerialPipes(pipes);
Instance instance = new Instance("fruit fruit", null, "docMax", null);
instance = pipeline.instanceFrom(instance);
InstanceList instList = new InstanceList(pipeline);
instList.addThruPipe(instance);
File file = File.createTempFile("thresholdmax", ".txt");
file.deleteOnExit();
inferencer.writeInferredDistributions(instList, file, 5, 1, 1, 0.1, 2);
BufferedReader reader = new BufferedReader(new FileReader(file));
reader.readLine();
String line = reader.readLine();
assertTrue(line.split("\t").length <= 6);
reader.close();
}

@Test
public void testExplicitNonPowerOfTwoTopicCountStillInitializes() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("banana");
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (3 << 3) | 0, (3 << 3) | 1, (3 << 3) | 2, (3 << 3) | 3, (3 << 3) | 4 };
int[] tokensPerTopic = new int[] { 3, 3, 3, 3, 3 };
double[] alpha = new double[] { 0.1, 0.1, 0.1, 0.1, 0.1 };
double beta = 0.01;
double betaSum = 0.25;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
assertEquals(5, inferencer.getSampledDistribution(new Instance("banana", null, null, null), 1, 1, 0).length);
}

@Test
public void testUnseenTypeWithIndexLargerThanNumTypesIgnored() {
Alphabet alphabet = new Alphabet();
int id1 = alphabet.lookupIndex("foo");
int id2 = alphabet.lookupIndex("bar");
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { (1 << 1) | 0 };
int[] tokensPerTopic = new int[] { 1 };
double[] alpha = new double[] { 0.1 };
double beta = 0.01;
double betaSum = 0.1;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(903);
ArrayList pipes = new ArrayList();
pipes.add(new CharSequence2TokenSequence());
pipes.add(new TokenSequence2FeatureSequence(alphabet));
SerialPipes serialPipes = new SerialPipes(pipes);
Instance instance = new Instance("foo bar bar", null, "unseen-bar", null);
instance = serialPipes.instanceFrom(instance);
double[] result = inferencer.getSampledDistribution(instance, 5, 1, 1);
assertEquals(1, result.length);
assertEquals(1.0, result[0], 1e-6);
}

@Test
public void testNegativeEncodedTopicValueHandlesGracefully() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("neg");
int[][] typeTopicCounts = new int[1][];
typeTopicCounts[0] = new int[] { -1 };
int[] tokensPerTopic = new int[] { 0 };
double[] alpha = new double[] { 0.2 };
double beta = 0.01;
double betaSum = 0.01;
TopicInferencer inferencer = new TopicInferencer(typeTopicCounts, tokensPerTopic, alphabet, alpha, beta, betaSum);
inferencer.setRandomSeed(999);
ArrayList pipeList = new ArrayList();
pipeList.add(new CharSequence2TokenSequence());
pipeList.add(new TokenSequence2FeatureSequence(alphabet));
SerialPipes serialPipes = new SerialPipes(pipeList);
Instance instance = new Instance("neg", null, "negenc", null);
instance = serialPipes.instanceFrom(instance);
double[] result = inferencer.getSampledDistribution(instance, 3, 1, 1);
assertEquals(1, result.length);
assertEquals(1.0, result[0], 1e-6);
}
}
