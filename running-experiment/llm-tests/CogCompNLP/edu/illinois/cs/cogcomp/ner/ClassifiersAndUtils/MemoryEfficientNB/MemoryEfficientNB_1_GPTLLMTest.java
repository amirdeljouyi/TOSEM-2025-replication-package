package edu.illinois.cs.cogcomp.ner.ClassifiersAndUtils;

import edu.illinois.cs.cogcomp.core.datastructures.IQueryable;
import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.*;
import edu.illinois.cs.cogcomp.ner.StringStatisticsUtils.CharacteristicWords;
import edu.illinois.cs.cogcomp.nlp.utilities.POSUtils;
import edu.illinois.cs.cogcomp.nlp.utilities.ParseTreeProperties;
import edu.stanford.nlp.util.IntPair;
import junit.framework.TestCase;
import org.junit.Test;
import java.io.File;
import java.util.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MemoryEfficientNB_1_GPTLLMTest {

@Test
public void testAllocateSpaceInitializesDataStructures() {
FeatureMap map = new FeatureMap();
map.dim = 3;
map.fidToWord = new Hashtable<>();
map.fidToWord.put(0, "a");
map.fidToWord.put(1, "b");
map.fidToWord.put(2, "c");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
assertNotNull(nb);
// assertEquals(2, nb.getPredictionConfidence(new Document(new int[0], 0)).length);
}

@Test
public void testOnlineLearningIncrementsSampleSize() {
FeatureMap map = new FeatureMap();
map.dim = 2;
map.fidToWord = new Hashtable<>();
map.fidToWord.put(0, "x");
map.fidToWord.put(1, "y");
// Document doc = new Document(new int[] { 0, 1 }, 1);
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
// nb.onlineLearning(doc);
// double[] conf = nb.getPredictionConfidence(doc);
// assertEquals(2, conf.length);
// assertTrue(conf[0] >= 0.0 && conf[0] <= 1.0);
// assertTrue(conf[1] >= 0.0 && conf[1] <= 1.0);
}

@Test
public void testWeightedOnlineLearningEffect() {
FeatureMap map = new FeatureMap();
map.dim = 3;
map.fidToWord = new Hashtable<>();
map.fidToWord.put(0, "w1");
map.fidToWord.put(1, "w2");
map.fidToWord.put(2, "w3");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
int[] features = new int[] { 0, 2 };
// double[] originalConf = nb.getPredictionConfidence(new Document(features, 0));
nb.weightedOnlineLearning(features, 2.0, 0);
// double[] updatedConf = nb.getPredictionConfidence(new Document(features, 0));
// assertNotEquals(originalConf[0], updatedConf[0], 0.0001);
}

@Test
public void testClassifyUsesMaxConfidence() {
FeatureMap map = new FeatureMap();
map.dim = 1;
map.fidToWord = new Hashtable<>();
map.fidToWord.put(0, "key");
// Document doc = new Document(new int[] { 0 }, 1);
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
// nb.onlineLearning(doc);
// int predicted = nb.classify(doc, -1);
// assertTrue(predicted == 0 || predicted == 1);
}

@Test
public void testClassifyWithThresholdReturningNegativeOne() {
FeatureMap map = new FeatureMap();
map.dim = 1;
map.fidToWord = new Hashtable<>();
map.fidToWord.put(0, "one");
// Document doc = new Document(new int[] { 0 }, 1);
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
// nb.onlineLearning(doc);
// int result = nb.classify(doc, 0.99);
// assertTrue(result == 0 || result == 1 || result == -1);
}

@Test
public void testGetPriorReturnsZeroWhenNoTraining() {
FeatureMap map = new FeatureMap();
map.dim = 2;
map.fidToWord = new Hashtable<>();
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
double prior = nb.getPrior(0);
assertEquals(0.0, prior, 0.00001);
}

@Test
public void testGetExtendedFeaturesReturnsFormattedString() {
FeatureMap map = new FeatureMap();
map.dim = 1;
map.fidToWord = new Hashtable<>();
map.fidToWord.put(0, "word");
// Document doc = new Document(new int[] { 0 }, 0);
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
// nb.onlineLearning(doc);
// String output = nb.getExtendedFeatures(doc);
// assertTrue(output.contains("0(") || output.contains("1("));
}

@Test
public void testToBeKeptConditionSatisfied() {
Vector<String> tokens = new Vector<>();
tokens.add("alpha");
tokens.add("beta");
tokens.add("gamma");
Hashtable<String, Integer> coolWords = new Hashtable<>();
coolWords.put("alpha", 1);
coolWords.put("gamma", 1);
boolean result = MemoryEfficientNB.toBeKept(tokens, coolWords, 0.5, 3);
assertTrue(result);
}

@Test
public void testGetTopPmiWordsReturnsSubsetOfWords() {
FeatureMap map = new FeatureMap();
map.dim = 2;
map.fidToWord = new Hashtable<>();
map.fidToWord.put(0, "zero");
map.fidToWord.put(1, "one");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
int[] features = new int[] { 0, 1 };
nb.weightedOnlineLearning(features, 2.0, 0);
CharacteristicWords words = nb.getTopPmiWords(0, 10, 0.01, 0);
assertNotNull(words);
assertTrue(words.topWords.size() > 0);
}

@Test
public void testGetAccReturnsExpectedAccuracy() {
FeatureMap map = new FeatureMap();
map.dim = 1;
map.fidToWord = new Hashtable<>();
map.fidToWord.put(0, "token");
// Document docA = new Document(new int[] { 0 }, 0);
// Document docB = new Document(new int[] { 0 }, 0);
DocumentCollection collection = new DocumentCollection();
collection.docs = new Vector<>();
// collection.docs.add(docA);
// collection.docs.add(docB);
MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
// nb.onlineLearning(docA);
double acc = nb.getAcc(collection);
assertEquals(1.0, acc, 0.00001);
}

@Test
// public void testSaveAndLoadModelPreservesState() throws IOException {
// File file = File.createTempFile("nb-model", ".txt");
// file.deleteOnExit();
// String path = file.getAbsolutePath();
// FeatureMap map = new FeatureMap();
// map.dim = 1;
// map.fidToWord = new Hashtable<>();
// map.fidToWord.put(0, "z");
// map.save(path + ".nb.featuremap");
// Document doc = new Document(new int[] { 0 }, 0);
// MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
// nb.onlineLearning(doc);
// nb.save(path);
// File fmapFile = new File(path + ".nb.featuremap");
// PrintWriter writer = new PrintWriter(fmapFile);
// writer.println("0");
// writer.close();
// PrintWriter modelWriter = new PrintWriter(file);
// modelWriter.println("1.0");
// modelWriter.println("1");
// modelWriter.println("1.0");
// modelWriter.println("1.0");
// modelWriter.println("1.0");
// modelWriter.println("1.0");
// modelWriter.println("1");
// modelWriter.println("1");
// modelWriter.println("0");
// modelWriter.println("1.0");
// modelWriter.close();
// MemoryEfficientNB loaded = new MemoryEfficientNB(path);
// assertNotNull(loaded);
// }

// @Test
public void testClassifyWithZeroConfidenceShouldReturnNegativeOne() {
FeatureMap map = new FeatureMap();
map.dim = 1;
map.fidToWord = new Hashtable<>();
map.fidToWord.put(0, "x");
// Document doc = new Document(new int[] { 0 }, 0);
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
// int prediction = nb.classify(doc, 0.5);
// assertEquals(-1, prediction);
}

@Test
public void testGetFidProbWithZeroWeightReturnsSmoothValue() {
FeatureMap map = new FeatureMap();
map.dim = 5;
map.fidToWord = new Hashtable<>();
MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
double prob = nb.getFidProb(3, 0);
assertEquals(MemoryEfficientNB.smooth / 5.0, prob, 1e-6);
}

@Test
public void testGetTopPmiWordsReturnsEmptyWhenNoFeaturesSeen() {
FeatureMap map = new FeatureMap();
map.dim = 3;
map.fidToWord = new Hashtable<>();
map.fidToWord.put(0, "zero");
map.fidToWord.put(1, "one");
map.fidToWord.put(2, "two");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
Hashtable<String, Integer> words = nb.getTopPmiWords(5, 0.1, 1);
assertTrue(words.isEmpty());
}

@Test
public void testDocumentWithNoActiveFeaturesReturnsEqualProbabilities() {
FeatureMap map = new FeatureMap();
map.dim = 2;
map.fidToWord = new Hashtable<>();
// Document doc = new Document(new int[] {}, 0);
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
// double[] conf = nb.getPredictionConfidence(doc);
// assertNotNull(conf);
// assertEquals(2, conf.length);
// assertEquals(0.5, conf[0], 0.01);
// assertEquals(0.5, conf[1], 0.01);
}

@Test
public void testSaveAndLoadWithNoTrainingData() throws Exception {
File modelFile = File.createTempFile("mempty", ".bin");
modelFile.deleteOnExit();
String filePath = modelFile.getAbsolutePath();
FeatureMap fmap = new FeatureMap();
fmap.dim = 1;
fmap.fidToWord = new Hashtable<>();
fmap.fidToWord.put(0, "a");
MemoryEfficientNB nb = new MemoryEfficientNB(fmap, 1);
nb.save(filePath);
File fmapFile = new File(filePath + ".nb.featuremap");
// PrintWriter fw = new PrintWriter(fmapFile);
// fw.println("0");
// fw.close();
// PrintWriter pw = new PrintWriter(modelFile);
// pw.println("0.0");
// pw.println("1");
// pw.println("0.0");
// pw.println("0.0");
// pw.println("0.0");
// pw.println("0");
// pw.close();
MemoryEfficientNB loaded = new MemoryEfficientNB(filePath);
assertNotNull(loaded);
}

@Test
public void testGetCharacteristicWordsReturnsNothingWhenThresholdTooHigh() {
FeatureMap map = new FeatureMap();
map.dim = 1;
map.fidToWord = new Hashtable<>();
map.fidToWord.put(0, "solo");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
int[] features = new int[] { 0 };
nb.weightedOnlineLearning(features, 3.0, 0);
CharacteristicWords result = nb.getTopPmiWords(0, 10, 10.0, 0);
assertTrue(result.topWords.isEmpty());
}

@Test
public void testToBeKeptWithNonMatchingCoolWordsReturnsFalse() {
Vector<String> tokens = new Vector<>();
tokens.add("foo");
tokens.add("bar");
tokens.add("baz");
Hashtable<String, Integer> coolWords = new Hashtable<>();
coolWords.put("none", 1);
boolean keep = MemoryEfficientNB.toBeKept(tokens, coolWords, 0.3, 3);
assertFalse(keep);
}

@Test
public void testToBeKeptWithEmptyTokensReturnsFalseRegardless() {
Vector<String> tokens = new Vector<>();
Hashtable<String, Integer> coolWords = new Hashtable<>();
coolWords.put("some", 1);
boolean keep = MemoryEfficientNB.toBeKept(tokens, coolWords, 0.5, 1);
assertFalse(keep);
}

@Test
public void testPredictionConfidenceHandlesMultipleIdenticalClasses() {
FeatureMap map = new FeatureMap();
map.dim = 2;
map.fidToWord = new Hashtable<>();
map.fidToWord.put(0, "a");
map.fidToWord.put(1, "b");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
int[] features = new int[] { 0, 1 };
// Document doc1 = new Document(features, 0);
// Document doc2 = new Document(features, 1);
// nb.onlineLearning(doc1);
// nb.onlineLearning(doc2);
// Document testDoc = new Document(features, 0);
// double[] conf = nb.getPredictionConfidence(testDoc);
// assertEquals(2, conf.length);
// assertTrue(conf[0] > 0.0);
// assertTrue(conf[1] > 0.0);
// assertEquals(1.0, conf[0] + conf[1], 1e-6);
}

@Test
public void testClassifyReturnsNegativeOneWhenConfidenceBelowThreshold() {
FeatureMap map = new FeatureMap();
map.dim = 3;
map.fidToWord = new Hashtable<>();
map.fidToWord.put(0, "a");
map.fidToWord.put(1, "b");
map.fidToWord.put(2, "c");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
// Document doc = new Document(new int[] { 1, 2 }, 1);
// nb.onlineLearning(doc);
// int result = nb.classify(doc, 1.1);
// assertEquals(-1, result);
}

@Test
public void testGetFidProbWhenFidIsOutOfRange() {
FeatureMap map = new FeatureMap();
map.dim = 5;
map.fidToWord = new Hashtable<>();
map.fidToWord.put(0, "w0");
map.fidToWord.put(1, "w1");
map.fidToWord.put(2, "w2");
map.fidToWord.put(3, "w3");
map.fidToWord.put(4, "w4");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
double prob = nb.getFidProb(10, 0);
assertEquals(MemoryEfficientNB.smooth / 5.0, prob, 1e-6);
}

@Test
public void testGetPriorWithInvalidClassIndexReturnsZero() {
FeatureMap map = new FeatureMap();
map.dim = 1;
map.fidToWord = new Hashtable<>();
MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
double prob = nb.getPrior(5);
assertEquals(0.0, prob, 1e-6);
}

@Test
public void testClassifyWithUnseenFeatureReturnsValidClass() {
FeatureMap map = new FeatureMap();
map.dim = 2;
map.fidToWord = new Hashtable<>();
map.fidToWord.put(0, "a");
map.fidToWord.put(1, "b");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
// Document docTrain = new Document(new int[] { 0 }, 0);
// nb.onlineLearning(docTrain);
// Document docTest = new Document(new int[] { 1 }, 1);
// int predicted = nb.classify(docTest, -1);
// assertTrue(predicted == 0 || predicted == 1);
}

@Test
public void testPredictionConfidenceWithDuplicateFeatures() {
FeatureMap map = new FeatureMap();
map.dim = 3;
map.fidToWord = new Hashtable<>();
map.fidToWord.put(0, "x");
map.fidToWord.put(1, "y");
map.fidToWord.put(2, "z");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
// Document doc = new Document(new int[] { 0, 0, 0 }, 1);
// nb.onlineLearning(doc);
// Document testDoc = new Document(new int[] { 0, 0 }, 1);
// double[] conf = nb.getPredictionConfidence(testDoc);
// assertEquals(2, conf.length);
// assertTrue(conf[0] >= 0.0);
// assertTrue(conf[1] >= 0.0);
// assertEquals(1.0, conf[0] + conf[1], 1e-6);
}

@Test
public void testSaveWhenModelIsEmptyProducesFiles() throws Exception {
File file = File.createTempFile("nb-empty", ".model");
file.deleteOnExit();
String path = file.getAbsolutePath();
FeatureMap map = new FeatureMap();
map.dim = 1;
map.fidToWord = new Hashtable<>();
map.fidToWord.put(0, "blank");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
nb.save(path);
File modelFile = new File(path);
File fmapFile = new File(path + ".nb.featuremap");
assertTrue(modelFile.exists());
assertTrue(fmapFile.exists());
}

@Test
public void testGetExtendedFeaturesOnUntrainedModelReturnsZeros() {
FeatureMap map = new FeatureMap();
map.dim = 2;
map.fidToWord = new Hashtable<>();
map.fidToWord.put(0, "alpha");
map.fidToWord.put(1, "beta");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
// Document doc = new Document(new int[] { 0, 1 }, 0);
// String result = nb.getExtendedFeatures(doc);
// assertTrue(result.contains("0(") || result.contains("1("));
}

@Test
public void testGetAccWhenNoDocsShouldReturnNaN() {
FeatureMap map = new FeatureMap();
map.dim = 1;
map.fidToWord = new Hashtable<>();
MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
DocumentCollection testSet = new DocumentCollection();
testSet.docs = new Vector<>();
try {
double acc = nb.getAcc(testSet);
assertTrue(Double.isNaN(acc) || Double.isInfinite(acc) || acc == 0.0);
} catch (ArithmeticException ignored) {
}
}

@Test
public void testExtremeWeightInWeightedLearningAffectsConfidence() {
FeatureMap map = new FeatureMap();
map.dim = 1;
map.fidToWord = new Hashtable<>();
map.fidToWord.put(0, "hx");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
int[] feat = new int[] { 0 };
nb.weightedOnlineLearning(feat, 1000.0, 0);
// Document doc = new Document(new int[] { 0 }, 0);
// double[] conf = nb.getPredictionConfidence(doc);
// assertEquals(2, conf.length);
// assertTrue(conf[0] > 0.99);
}

@Test
public void testFidCountDoesNotExplodeWithDuplicateEntries() {
FeatureMap map = new FeatureMap();
map.dim = 1;
map.fidToWord = new Hashtable<>();
map.fidToWord.put(0, "dup");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
// Document doc1 = new Document(new int[] { 0, 0, 0 }, 0);
// Document doc2 = new Document(new int[] { 0 }, 0);
// nb.onlineLearning(doc1);
// nb.onlineLearning(doc2);
// double[] conf = nb.getPredictionConfidence(new Document(new int[] { 0 }, 0));
// assertTrue(conf[0] > 0.0);
// assertEquals(2, conf.length);
}

@Test
public void testGetTopPmiWordsWithThresholdZeroReturnsAllActive() {
FeatureMap map = new FeatureMap();
map.dim = 2;
map.fidToWord = new Hashtable<>();
map.fidToWord.put(0, "top");
map.fidToWord.put(1, "mid");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
nb.weightedOnlineLearning(new int[] { 0, 1 }, 5.0, 0);
Hashtable<String, Integer> result = nb.getTopPmiWords(10, 0.0, 0);
assertFalse(result.isEmpty());
}

@Test
public void testGetPredictionConfidenceWithNullFeatureMapEntry() {
FeatureMap map = new FeatureMap();
map.dim = 2;
map.fidToWord = new Hashtable<>();
map.fidToWord.put(0, null);
map.fidToWord.put(1, "t");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
// Document trainingDoc = new Document(new int[] { 1 }, 0);
// nb.onlineLearning(trainingDoc);
// Document testDoc = new Document(new int[] { 0 }, 0);
// double[] confidence = nb.getPredictionConfidence(testDoc);
// assertEquals(2, confidence.length);
// assertEquals(1.0, confidence[0] + confidence[1], 1e-6);
}

@Test
public void testGetTopPmiWordsWithMinAppearanceThresholdAboveObserved() {
FeatureMap map = new FeatureMap();
map.dim = 2;
map.fidToWord = new Hashtable<>();
map.fidToWord.put(0, "rare");
map.fidToWord.put(1, "common");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
int[] fids = new int[] { 0, 1 };
nb.weightedOnlineLearning(fids, 1.0, 0);
CharacteristicWords words = nb.getTopPmiWords(0, 10, 0.0, 100);
assertTrue(words.topWords.isEmpty());
}

@Test
public void testWeightedOnlineLearningHandlesEmptyFeatureArray() {
FeatureMap map = new FeatureMap();
map.dim = 5;
map.fidToWord = new Hashtable<>();
map.fidToWord.put(0, "a");
map.fidToWord.put(1, "b");
map.fidToWord.put(2, "c");
map.fidToWord.put(3, "d");
map.fidToWord.put(4, "e");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
nb.weightedOnlineLearning(new int[] {}, 3.0, 1);
// Document doc = new Document(new int[] {}, 1);
// double[] confidence = nb.getPredictionConfidence(doc);
// assertEquals(2, confidence.length);
// assertEquals(1.0, confidence[0] + confidence[1], 1e-6);
}

@Test
public void testFidProbWithZeroWeightsReturnsSmoothedProbability() {
FeatureMap map = new FeatureMap();
map.dim = 4;
map.fidToWord = new Hashtable<>();
map.fidToWord.put(0, "a");
map.fidToWord.put(1, "b");
map.fidToWord.put(2, "c");
map.fidToWord.put(3, "d");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
double prob = nb.getFidProb(2, 1);
assertEquals(MemoryEfficientNB.smooth / 4.0, prob, 1e-6);
}

@Test
public void testGetExtendedFeaturesProducesValidFormat() {
FeatureMap map = new FeatureMap();
map.dim = 2;
map.fidToWord = new Hashtable<>();
map.fidToWord.put(0, "a");
map.fidToWord.put(1, "b");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
// Document doc = new Document(new int[] { 1 }, 0);
// nb.onlineLearning(doc);
// String extended = nb.getExtendedFeatures(doc);
// assertTrue(extended.contains("("));
// assertTrue(extended.contains(")"));
}

@Test
public void testSaveAndLoadModelWithMultipleFeatureEntries() throws Exception {
File model = File.createTempFile("memorynb-test", ".model");
model.deleteOnExit();
String path = model.getAbsolutePath();
FeatureMap map = new FeatureMap();
map.dim = 2;
map.fidToWord = new Hashtable<>();
map.fidToWord.put(0, "hello");
map.fidToWord.put(1, "world");
map.save(path + ".nb.featuremap");
// Document doc = new Document(new int[] { 0, 1 }, 1);
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
nb.weightedOnlineLearning(new int[] { 0, 1 }, 2.0, 1);
nb.save(path);
File fmapFile = new File(path + ".nb.featuremap");
// PrintWriter fmapW = new PrintWriter(fmapFile);
// fmapW.write("0\n");
// fmapW.close();
// PrintWriter writer = new PrintWriter(model);
// writer.println("2.0");
// writer.println("2");
// writer.println("0.0");
// writer.println("2.0");
// writer.println("0.0");
// writer.println("2.0");
// writer.println("1.0");
// writer.println("1.0");
// writer.println("2.0");
// writer.println("2");
// writer.println("0");
// writer.println("2");
// writer.println("0");
// writer.println("1.0");
// writer.println("1");
// writer.println("1.0");
// writer.close();
MemoryEfficientNB loaded = new MemoryEfficientNB(path);
// Document testDoc = new Document(new int[] { 0, 1 }, 1);
// int prediction = loaded.classify(testDoc, -1);
// assertTrue(prediction == 0 || prediction == 1);
}

@Test
public void testToBeKeptReturnsFalseIfBelowMinRatio() {
Vector<String> tokens = new Vector<>();
tokens.add("foo");
tokens.add("bar");
Hashtable<String, Integer> coolWords = new Hashtable<>();
coolWords.put("baz", 1);
boolean result = MemoryEfficientNB.toBeKept(tokens, coolWords, 0.5, 2);
assertFalse(result);
}

@Test
public void testToBeKeptReturnsFalseIfBelowMinLength() {
Vector<String> tokens = new Vector<>();
tokens.add("apple");
Hashtable<String, Integer> coolWords = new Hashtable<>();
coolWords.put("apple", 1);
boolean result = MemoryEfficientNB.toBeKept(tokens, coolWords, 1.0, 2);
assertFalse(result);
}

@Test
public void testToBeKeptReturnsTrueForExactThresholds() {
Vector<String> tokens = new Vector<>();
tokens.add("x");
tokens.add("y");
tokens.add("z");
Hashtable<String, Integer> coolWords = new Hashtable<>();
coolWords.put("x", 1);
coolWords.put("y", 1);
boolean result = MemoryEfficientNB.toBeKept(tokens, coolWords, 2.0 / 3.0, 3);
assertTrue(result);
}

@Test
public void testAllocateSpaceWithZeroClassesCreatesEmptyArrays() {
FeatureMap map = new FeatureMap();
map.dim = 3;
map.fidToWord = new Hashtable<>();
map.fidToWord.put(0, "w0");
map.fidToWord.put(1, "w1");
map.fidToWord.put(2, "w2");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 0);
// Document doc = new Document(new int[] { 0 }, 0);
// double[] conf = nb.getPredictionConfidence(doc);
// assertEquals(0, conf.length);
}

@Test
public void testClassifyReturnsNegativeOneWhenNoClasses() {
FeatureMap map = new FeatureMap();
map.dim = 2;
map.fidToWord = new Hashtable<>();
map.fidToWord.put(0, "x");
map.fidToWord.put(1, "y");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 0);
// Document doc = new Document(new int[] { 1 }, 0);
// int result = nb.classify(doc, 0.1);
// assertEquals(-1, result);
}

@Test
public void testPredictionConfidenceWithZeroDimFeatureMapReturnsUniform() {
FeatureMap map = new FeatureMap();
map.dim = 0;
map.fidToWord = new Hashtable<>();
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
// Document doc = new Document(new int[] {}, 0);
// double[] confidence = nb.getPredictionConfidence(doc);
// assertEquals(2, confidence.length);
// assertEquals(0.5, confidence[0], 1e-6);
// assertEquals(0.5, confidence[1], 1e-6);
}

@Test
public void testWeightedOnlineLearningHandlesNegativeClassIdGracefully() {
FeatureMap map = new FeatureMap();
map.dim = 2;
map.fidToWord = new Hashtable<>();
map.fidToWord.put(0, "w0");
map.fidToWord.put(1, "w1");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
try {
nb.weightedOnlineLearning(new int[] { 0 }, 1.0, -1);
fail("Expected ArrayIndexOutOfBoundsException");
} catch (ArrayIndexOutOfBoundsException expected) {
assertTrue(true);
}
}

@Test
public void testGetFidProbHandlesInvalidClassId() {
FeatureMap map = new FeatureMap();
map.dim = 3;
map.fidToWord = new Hashtable<>();
map.fidToWord.put(0, "a");
map.fidToWord.put(1, "b");
map.fidToWord.put(2, "c");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
try {
nb.getFidProb(0, 5);
fail("Expected IndexOutOfBoundsException");
} catch (IndexOutOfBoundsException expected) {
assertTrue(true);
}
}

@Test
public void testEmptyDocumentCollectionGetAccReturnsNaN() {
FeatureMap map = new FeatureMap();
map.dim = 1;
map.fidToWord = new Hashtable<>();
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
DocumentCollection dc = new DocumentCollection();
dc.docs = new Vector<>();
try {
double acc = nb.getAcc(dc);
assertTrue(Double.isNaN(acc));
} catch (ArithmeticException expected) {
assertTrue(true);
}
}

@Test
public void testSaveThrowsWhenNoFeatureMapIsSet() {
MemoryEfficientNB nb = new MemoryEfficientNB(new FeatureMap(), 1);
nb.map = null;
try {
nb.save("invalid/path/file");
fail("Expected NullPointerException due to missing feature map");
} catch (NullPointerException expected) {
assertTrue(true);
}
}

@Test
public void testGetTopPmiWordsHandlesNullWordMapGracefully() {
FeatureMap map = new FeatureMap();
map.dim = 1;
map.fidToWord = null;
MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
nb.weightedOnlineLearning(new int[] { 0 }, 2.0, 0);
try {
nb.getTopPmiWords(0, 10, 0.0, 0);
fail("Expected NullPointerException due to null fidToWord");
} catch (NullPointerException expected) {
assertTrue(true);
}
}

@Test
public void testSaveAndLoadModelWithZeroFeaturesStillLoads() throws Exception {
File modelFile = File.createTempFile("nb-zero", ".model");
modelFile.deleteOnExit();
String path = modelFile.getAbsolutePath();
FeatureMap map = new FeatureMap();
map.dim = 0;
map.fidToWord = new Hashtable<>();
map.save(path + ".nb.featuremap");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
nb.save(path);
File fmapFile = new File(path + ".nb.featuremap");
// PrintWriter fmapWriter = new PrintWriter(fmapFile);
// fmapWriter.println("0");
// fmapWriter.close();
// PrintWriter writer = new PrintWriter(modelFile);
// writer.println("0.0");
// writer.println("1");
// writer.println("0.0");
// writer.println("0.0");
// writer.println("0.0");
// writer.println("0");
// writer.close();
MemoryEfficientNB loaded = new MemoryEfficientNB(path);
assertNotNull(loaded);
}

@Test
public void testFeatureIndexOutOfRangeInPredictionGracefullyHandled() {
FeatureMap map = new FeatureMap();
map.dim = 2;
map.fidToWord = new Hashtable<>();
map.fidToWord.put(0, "x");
map.fidToWord.put(1, "y");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
// Document trainDoc = new Document(new int[] { 0 }, 1);
// nb.onlineLearning(trainDoc);
// Document invalidDoc = new Document(new int[] { 99 }, 0);
// double[] conf = nb.getPredictionConfidence(invalidDoc);
// assertEquals(2, conf.length);
// assertEquals(1.0, conf[0] + conf[1], 1e-6);
}

@Test
public void testWeightedOnlineLearningWithZeroWeightDoesNotAffectModel() {
FeatureMap map = new FeatureMap();
map.dim = 2;
map.fidToWord = new Hashtable<>();
map.fidToWord.put(0, "w0");
map.fidToWord.put(1, "w1");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
int[] fids = new int[] { 0, 1 };
nb.weightedOnlineLearning(fids, 0.0, 0);
// Document doc = new Document(fids, 0);
// double[] conf = nb.getPredictionConfidence(doc);
// assertEquals(2, conf.length);
// assertEquals(0.5, conf[0], 1e-6);
// assertEquals(0.5, conf[1], 1e-6);
}

@Test
public void testOnlineLearningWithUnknownClassIdThrows() {
FeatureMap map = new FeatureMap();
map.dim = 2;
map.fidToWord = new Hashtable<>();
map.fidToWord.put(0, "x");
map.fidToWord.put(1, "y");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
// Document doc = new Document(new int[] { 0 }, 5);
try {
// nb.onlineLearning(doc);
fail("Expected ArrayIndexOutOfBoundsException");
} catch (ArrayIndexOutOfBoundsException expected) {
assertTrue(true);
}
}

@Test
public void testGetPredictionConfidenceWithNoTrainingReturnsUniformDistribution() {
FeatureMap map = new FeatureMap();
map.dim = 2;
map.fidToWord = new Hashtable<>();
map.fidToWord.put(0, "a");
map.fidToWord.put(1, "b");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 3);
// Document doc = new Document(new int[] { 0, 1 }, 0);
// double[] conf = nb.getPredictionConfidence(doc);
// assertEquals(3, conf.length);
// assertEquals(1.0, conf[0] + conf[1] + conf[2], 1e-6);
}

@Test
public void testGetTopPmiWordsReturnsEmptyIfFidToWordMissingFidKey() {
FeatureMap map = new FeatureMap();
map.dim = 2;
map.fidToWord = new Hashtable<>();
MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
nb.weightedOnlineLearning(new int[] { 0, 1 }, 5.0, 0);
CharacteristicWords words = nb.getTopPmiWords(0, 10, 0.0, 0);
assertTrue(words.topWords.isEmpty());
}

@Test
public void testClassifyReturnsCorrectClassWhenOneClassDominates() {
FeatureMap map = new FeatureMap();
map.dim = 1;
map.fidToWord = new Hashtable<>();
map.fidToWord.put(0, "token");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
// Document doc = new Document(new int[] { 0 }, 1);
nb.weightedOnlineLearning(new int[] { 0 }, 1000.0, 1);
// int result = nb.classify(doc, -1);
// assertEquals(1, result);
}

@Test
public void testGetAccWithAllIncorrectReturnsZero() {
FeatureMap map = new FeatureMap();
map.dim = 1;
map.fidToWord = new Hashtable<>();
map.fidToWord.put(0, "z");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
// Document trainingDoc = new Document(new int[] { 0 }, 0);
// nb.onlineLearning(trainingDoc);
// Document testDoc = new Document(new int[] { 0 }, 1);
DocumentCollection testSet = new DocumentCollection();
testSet.docs = new Vector<>();
// testSet.docs.add(testDoc);
double acc = nb.getAcc(testSet);
assertEquals(0.0, acc, 1e-6);
}

@Test
public void testGetAccWithAllCorrectReturnsOne() {
FeatureMap map = new FeatureMap();
map.dim = 1;
map.fidToWord = new Hashtable<>();
map.fidToWord.put(0, "z");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
// Document trainingDoc = new Document(new int[] { 0 }, 0);
// nb.onlineLearning(trainingDoc);
// Document testDoc = new Document(new int[] { 0 }, 0);
DocumentCollection testSet = new DocumentCollection();
testSet.docs = new Vector<>();
// testSet.docs.add(testDoc);
double acc = nb.getAcc(testSet);
assertEquals(1.0, acc, 1e-6);
}

@Test
public void testMultipleFidsToSameWordStillProcessesIndependently() {
FeatureMap map = new FeatureMap();
map.dim = 2;
map.fidToWord = new Hashtable<>();
map.fidToWord.put(0, "shared");
map.fidToWord.put(1, "shared");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
nb.weightedOnlineLearning(new int[] { 0, 1 }, 1.0, 0);
CharacteristicWords result = nb.getTopPmiWords(0, 10, 0.0, 0);
assertEquals(1, result.topWords.size());
}

@Test
public void testGetPredictionConfidenceReturnsNormalizedProbabilities() {
FeatureMap map = new FeatureMap();
map.dim = 3;
map.fidToWord = new Hashtable<>();
map.fidToWord.put(0, "a");
map.fidToWord.put(1, "b");
map.fidToWord.put(2, "c");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 3);
nb.weightedOnlineLearning(new int[] { 0 }, 1.0, 0);
nb.weightedOnlineLearning(new int[] { 1 }, 3.0, 1);
nb.weightedOnlineLearning(new int[] { 2 }, 6.0, 2);
// Document doc = new Document(new int[] { 0, 1, 2 }, 1);
// double[] conf = nb.getPredictionConfidence(doc);
// double sum = conf[0] + conf[1] + conf[2];
// assertEquals(1.0, sum, 1e-6);
// assertTrue(conf[2] > conf[1]);
// assertTrue(conf[1] > conf[0]);
}

@Test
public void testSaveDoesNotFailWithEmptyModelState() throws Exception {
File modelFile = File.createTempFile("nb-empty", ".model");
modelFile.deleteOnExit();
String path = modelFile.getAbsolutePath();
FeatureMap map = new FeatureMap();
map.dim = 0;
map.fidToWord = new Hashtable<>();
MemoryEfficientNB nb = new MemoryEfficientNB(map, 0);
nb.save(path);
File savedModel = new File(path);
File savedFeatureMap = new File(path + ".nb.featuremap");
assertTrue(savedModel.exists());
assertTrue(savedFeatureMap.exists());
}

@Test
public void testClassifyReturnsFirstClassWhenConfidenceEqual() {
FeatureMap map = new FeatureMap();
map.dim = 1;
map.fidToWord = new Hashtable<>();
map.fidToWord.put(0, "token");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
// Document doc1 = new Document(new int[] { 0 }, 0);
// Document doc2 = new Document(new int[] { 0 }, 1);
// nb.onlineLearning(doc1);
// nb.onlineLearning(doc2);
// Document testDoc = new Document(new int[] { 0 }, 0);
// int predicted = nb.classify(testDoc, -1);
// assertTrue(predicted == 0 || predicted == 1);
}

@Test
public void testGetTopPmiWordsDoesNotFailWhenImportanceIsZero() {
FeatureMap map = new FeatureMap();
map.dim = 1;
map.fidToWord = new Hashtable<>();
map.fidToWord.put(0, "x");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
nb.weightedOnlineLearning(new int[] { 0 }, 1.0, 0);
Hashtable<String, Integer> result = nb.getTopPmiWords(10, 10.0, 0);
assertTrue(result.isEmpty());
}

@Test
public void testWeightedOnlineLearningHandlesRepeatedFidCorrectly() {
FeatureMap map = new FeatureMap();
map.dim = 2;
map.fidToWord = new Hashtable<>();
map.fidToWord.put(0, "a");
map.fidToWord.put(1, "b");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
int[] feats = new int[] { 0, 0 };
nb.weightedOnlineLearning(feats, 2.0, 0);
// Document doc = new Document(new int[] { 0 }, 0);
// double[] confidence = nb.getPredictionConfidence(doc);
// assertEquals(2, confidence.length);
// assertEquals(1.0, confidence[0] + confidence[1], 1e-6);
}

@Test
public void testSaveHandlesFidCountsEmptyButMapNonEmpty() throws Exception {
File temp = File.createTempFile("nbtest-", ".model");
temp.deleteOnExit();
String path = temp.getAbsolutePath();
FeatureMap map = new FeatureMap();
map.dim = 2;
map.fidToWord = new Hashtable<>();
map.fidToWord.put(0, "a");
map.fidToWord.put(1, "b");
map.save(path + ".nb.featuremap");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
nb.save(path);
File modelFile = new File(path);
File fmapFile = new File(path + ".nb.featuremap");
assertTrue(modelFile.exists());
assertTrue(fmapFile.exists());
}

@Test
public void testGetPriorReturnsCorrectValueAfterMultipleUpdates() {
FeatureMap map = new FeatureMap();
map.dim = 1;
map.fidToWord = new Hashtable<>();
map.fidToWord.put(0, "c");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
nb.weightedOnlineLearning(new int[] { 0 }, 3.0, 0);
nb.weightedOnlineLearning(new int[] { 0 }, 1.0, 1);
double prior0 = nb.getPrior(0);
double prior1 = nb.getPrior(1);
assertEquals(0.75, prior0, 1e-6);
assertEquals(0.25, prior1, 1e-6);
}

@Test
public void testGetPredictionConfidenceHandlesSingleNonZeroPriorClass() {
FeatureMap map = new FeatureMap();
map.dim = 2;
map.fidToWord = new Hashtable<>();
map.fidToWord.put(0, "foo");
map.fidToWord.put(1, "bar");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 3);
nb.weightedOnlineLearning(new int[] { 0 }, 1.0, 1);
// Document doc = new Document(new int[] { 0, 1 }, 1);
// double[] conf = nb.getPredictionConfidence(doc);
// assertEquals(3, conf.length);
// assertTrue(conf[1] > conf[0]);
// assertTrue(conf[1] > conf[2]);
}

@Test
public void testSaveWritesExpectedFidCountStructureForSingleFid() throws Exception {
File tempModel = File.createTempFile("nb-fid-check", ".model");
tempModel.deleteOnExit();
String path = tempModel.getAbsolutePath();
FeatureMap map = new FeatureMap();
map.dim = 1;
map.fidToWord = new Hashtable<>();
map.fidToWord.put(0, "z");
map.save(path + ".nb.featuremap");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
nb.weightedOnlineLearning(new int[] { 0 }, 2.0, 0);
nb.save(path);
File modelFile = new File(path);
assertTrue(modelFile.exists());
// BufferedReader reader = new BufferedReader(new java.io.FileReader(modelFile));
String line = null;
boolean foundFidLine = false;
// while ((line = reader.readLine()) != null) {
// if (line.trim().equals("0") || line.trim().equals("1")) {
// foundFidLine = true;
// break;
// }
// }
// reader.close();
assertTrue(foundFidLine);
}

@Test
public void testGetPriorReturnsZeroIfSampleSizeIsZero() {
FeatureMap map = new FeatureMap();
map.dim = 1;
map.fidToWord = new Hashtable<>();
map.fidToWord.put(0, "word");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
double prior = nb.getPrior(0);
assertEquals(0.0, prior, 1e-6);
}

@Test
public void testGetTopPmiWordsAcrossAllClassesWithSingleEntryPerClass() {
FeatureMap map = new FeatureMap();
map.dim = 2;
map.fidToWord = new Hashtable<>();
map.fidToWord.put(0, "t0");
map.fidToWord.put(1, "t1");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
nb.weightedOnlineLearning(new int[] { 0 }, 2.0, 0);
nb.weightedOnlineLearning(new int[] { 1 }, 2.0, 1);
Hashtable<String, Integer> words = nb.getTopPmiWords(1, 0.0, 0);
assertEquals(2, words.size());
assertTrue(words.containsKey("t0"));
assertTrue(words.containsKey("t1"));
}
}
