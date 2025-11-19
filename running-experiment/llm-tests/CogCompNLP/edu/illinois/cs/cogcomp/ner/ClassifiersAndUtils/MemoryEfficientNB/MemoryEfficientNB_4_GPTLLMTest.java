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

public class MemoryEfficientNB_4_GPTLLMTest {

@Test
public void testOnlineLearningAndClassification() {
FeatureMap map = new FeatureMap();
map.dim = 2;
// map.add("apple");
// map.add("banana");
MemoryEfficientNB classifier = new MemoryEfficientNB(map, 2);
String[] wordsDoc1 = new String[] { "apple" };
Document doc1 = new Document(wordsDoc1, 0);
String[] wordsDoc2 = new String[] { "banana" };
Document doc2 = new Document(wordsDoc2, 1);
classifier.onlineLearning(doc1);
classifier.onlineLearning(doc2);
int predicted1 = classifier.classify(doc1, -1);
int predicted2 = classifier.classify(doc2, -1);
assertEquals(0, predicted1);
assertEquals(1, predicted2);
}

@Test
public void testWeightedOnlineLearning() {
FeatureMap map = new FeatureMap();
map.dim = 1;
// map.add("orange");
MemoryEfficientNB classifier = new MemoryEfficientNB(map, 1);
int fid = map.wordToFid.get("orange");
int[] activeFeatures = new int[] { fid };
classifier.weightedOnlineLearning(activeFeatures, 2.0, 0);
Document doc = new Document(new String[] { "orange" }, 0);
int prediction = classifier.classify(doc, -1);
assertEquals(0, prediction);
}

@Test
public void testGetFidProbReturnsSmoothWhenMissing() {
FeatureMap map = new FeatureMap();
map.dim = 5;
// map.add("a");
// map.add("b");
// map.add("c");
// map.add("d");
// map.add("e");
MemoryEfficientNB classifier = new MemoryEfficientNB(map, 1);
double prob = classifier.getFidProb(999, 0);
double expected = MemoryEfficientNB.smooth / map.dim;
assertEquals(expected, prob, 1e-6);
}

@Test
public void testGetPriorReturnsZeroWhenNoSamples() {
FeatureMap map = new FeatureMap();
map.dim = 1;
// map.add("word");
MemoryEfficientNB classifier = new MemoryEfficientNB(map, 1);
double prior = classifier.getPrior(0);
assertEquals(0.0, prior, 1e-9);
}

@Test
public void testGetPredictionConfidenceSumsToOne() {
FeatureMap map = new FeatureMap();
map.dim = 2;
// map.add("good");
// map.add("bad");
MemoryEfficientNB classifier = new MemoryEfficientNB(map, 2);
Document doc = new Document(new String[] { "good" }, 0);
classifier.onlineLearning(doc);
double[] confidence = classifier.getPredictionConfidence(doc);
double sum = confidence[0] + confidence[1];
assertEquals(1.0, sum, 1e-6);
}

@Test
public void testGetAccReturnsOneWhenAllCorrect() {
FeatureMap map = new FeatureMap();
map.dim = 1;
// map.add("yes");
MemoryEfficientNB classifier = new MemoryEfficientNB(map, 1);
Document doc = new Document(new String[] { "yes" }, 0);
classifier.onlineLearning(doc);
DocumentCollection collection = new DocumentCollection();
collection.docs.add(doc);
double acc = classifier.getAcc(collection);
assertEquals(1.0, acc, 1e-6);
}

@Test
public void testGetTopPmiWordsReturnsExpectedMap() {
FeatureMap map = new FeatureMap();
map.dim = 1;
// map.add("wow");
MemoryEfficientNB classifier = new MemoryEfficientNB(map, 1);
String[] words = new String[] { "wow" };
Document doc = new Document(words, 0);
classifier.onlineLearning(doc);
Hashtable<String, Integer> result = classifier.getTopPmiWords(1, 0.0, 0);
boolean hasWow = result.containsKey("wow");
assertTrue(hasWow);
}

@Test
public void testToBeKeptReturnsTrueWhenRequirementsMet() {
Vector<String> tokens = new Vector<>();
tokens.add("a");
tokens.add("b");
tokens.add("a");
Hashtable<String, Integer> coolWords = new Hashtable<>();
coolWords.put("a", 1);
boolean keep = MemoryEfficientNB.toBeKept(tokens, coolWords, 0.5, 2);
assertTrue(keep);
}

@Test
public void testGetExtendedFeaturesReturnsExpectedFormat() {
FeatureMap map = new FeatureMap();
map.dim = 1;
// map.add("zebra");
MemoryEfficientNB classifier = new MemoryEfficientNB(map, 1);
Document doc = new Document(new String[] { "zebra" }, 0);
classifier.onlineLearning(doc);
String features = classifier.getExtendedFeatures(doc);
assertTrue(features.contains("0("));
}

@Test
// public void testSaveAndReloadPreservesState() throws IOException {
// FeatureMap map = new FeatureMap();
// map.dim = 1;
// map.add("foo");
// MemoryEfficientNB classifier = new MemoryEfficientNB(map, 1);
// Document doc = new Document(new String[] { "foo" }, 0);
// classifier.onlineLearning(doc);
// File tmp = File.createTempFile("nb_model", ".nb");
// String path = tmp.getAbsolutePath();
// classifier.save(path);
// MemoryEfficientNB loaded = new MemoryEfficientNB(path);
// String[] testWords = new String[] { "foo" };
// Document testDoc = new Document(testWords, 0);
// int predicted = loaded.classify(testDoc, -1);
// assertEquals(0, predicted);
// tmp.delete();
// new File(path + ".nb.featuremap").delete();
// }

// @Test
public void testConstructorWithDocumentCollectionInitializesModel() {
FeatureMap map = new FeatureMap();
map.dim = 2;
// map.add("cat");
// map.add("dog");
Document doc1 = new Document(new String[] { "cat" }, 0);
Document doc2 = new Document(new String[] { "dog" }, 1);
DocumentCollection collection = new DocumentCollection();
collection.docs.add(doc1);
collection.docs.add(doc2);
MemoryEfficientNB classifier = new MemoryEfficientNB(collection, map, 2);
int prediction1 = classifier.classify(doc1, -1);
int prediction2 = classifier.classify(doc2, -1);
assertEquals(0, prediction1);
assertEquals(1, prediction2);
}

@Test
public void testClassifyReturnsNegativeOneWhenThresholdIsNotMet() {
FeatureMap map = new FeatureMap();
map.dim = 1;
// map.add("term");
MemoryEfficientNB classifier = new MemoryEfficientNB(map, 1);
Document testDoc = new Document(new String[] { "term" }, 0);
int prediction = classifier.classify(testDoc, 0.99);
assertEquals(-1, prediction);
}

@Test
public void testGetPredictionConfidenceNoFeaturesReturnsUniform() {
FeatureMap map = new FeatureMap();
map.dim = 3;
// map.add("a");
// map.add("b");
// map.add("c");
MemoryEfficientNB classifier = new MemoryEfficientNB(map, 2);
double[] conf = classifier.getPredictionConfidence(new Document(new String[] {}, 0));
double total = conf[0] + conf[1];
assertEquals(1.0, total, 1e-6);
}

@Test
public void testWeightedOnlineLearningWithZeroWeight() {
FeatureMap map = new FeatureMap();
map.dim = 1;
// map.add("x");
int fid = map.wordToFid.get("x");
int[] activeFeatures = new int[] { fid };
MemoryEfficientNB classifier = new MemoryEfficientNB(map, 1);
classifier.weightedOnlineLearning(activeFeatures, 0.0, 0);
Document testDoc = new Document(new String[] { "x" }, 0);
double[] conf = classifier.getPredictionConfidence(testDoc);
double totalConf = conf[0];
assertEquals(1.0, totalConf, 1e-6);
}

@Test
public void testGetTopPmiWordsReturnsEmptyWhenNothingLearned() {
FeatureMap map = new FeatureMap();
map.dim = 2;
// map.add("a");
// map.add("b");
MemoryEfficientNB classifier = new MemoryEfficientNB(map, 2);
CharacteristicWords words = classifier.getTopPmiWords(0, 5, 0.1, 1);
assertEquals(0, words.topWords.size());
}

@Test
public void testToBeKeptReturnsFalseWhenBelowRatio() {
Vector<String> tokens = new Vector<>();
tokens.add("a");
tokens.add("b");
tokens.add("c");
Hashtable<String, Integer> coolWords = new Hashtable<>();
coolWords.put("a", 1);
boolean keep = MemoryEfficientNB.toBeKept(tokens, coolWords, 0.75, 1);
assertFalse(keep);
}

@Test
public void testToBeKeptReturnsFalseWhenBelowMinLength() {
Vector<String> tokens = new Vector<>();
tokens.add("x");
Hashtable<String, Integer> coolWords = new Hashtable<>();
coolWords.put("x", 1);
boolean keep = MemoryEfficientNB.toBeKept(tokens, coolWords, 0.5, 2);
assertFalse(keep);
}

@Test
public void testClassifyWhenUniformConfidence() {
FeatureMap map = new FeatureMap();
map.dim = 1;
// map.add("nothing");
MemoryEfficientNB classifier = new MemoryEfficientNB(map, 2);
Document emptyDoc = new Document(new String[] {}, 1);
int prediction = classifier.classify(emptyDoc, -1);
assertEquals(0, prediction);
}

@Test
public void testGetExtendedFeaturesWhenConfidenceIsZero() {
FeatureMap map = new FeatureMap();
map.dim = 1;
// map.add("zero");
MemoryEfficientNB classifier = new MemoryEfficientNB(map, 3);
Document d = new Document(new String[] {}, 0);
String result = classifier.getExtendedFeatures(d);
assertTrue(result.contains("0("));
}

@Test
public void testClassifyWithNonexistentFeatures() {
FeatureMap map = new FeatureMap();
map.dim = 1;
// map.add("foo");
MemoryEfficientNB classifier = new MemoryEfficientNB(map, 1);
Document dummyDoc = new Document(new String[] { "bar" }, 0);
int prediction = classifier.classify(dummyDoc, -1);
assertEquals(0, prediction);
}

@Test
// public void testSaveAndReloadWithEmptyModel() throws IOException {
// FeatureMap map = new FeatureMap();
// map.dim = 1;
// map.add("abc");
// MemoryEfficientNB classifier = new MemoryEfficientNB(map, 1);
// File tmp = File.createTempFile("emptyModel", ".nb");
// String basePath = tmp.getAbsolutePath();
// classifier.save(basePath);
// MemoryEfficientNB loaded = new MemoryEfficientNB(basePath);
// Document doc = new Document(new String[] {}, 0);
// int prediction = loaded.classify(doc, -1);
// assertEquals(0, prediction);
// tmp.delete();
// new File(basePath + ".nb.featuremap").delete();
// }

// @Test
public void testGetFidProbWithExactMatch() {
FeatureMap map = new FeatureMap();
map.dim = 1;
// map.add("match");
int fid = map.wordToFid.get("match");
MemoryEfficientNB classifier = new MemoryEfficientNB(map, 1);
int[] feats = new int[] { fid };
classifier.weightedOnlineLearning(feats, 3.0, 0);
double prob = classifier.getFidProb(fid, 0);
double expected = (1 - MemoryEfficientNB.smooth) * 3.0 / 3.0;
assertEquals(expected, prob, 1e-6);
}

@Test
public void testGetPriorWithNonZeroSampleSizeButZeroClassCount() {
FeatureMap map = new FeatureMap();
map.dim = 1;
// map.add("classless");
MemoryEfficientNB model = new MemoryEfficientNB(map, 2);
int fid = map.wordToFid.get("classless");
int[] fidArray = new int[] { fid };
model.weightedOnlineLearning(fidArray, 2.0, 1);
double prior0 = model.getPrior(0);
assertEquals(0.0, prior0, 1e-6);
double prior1 = model.getPrior(1);
assertEquals(1.0, prior1, 1e-6);
}

@Test
public void testWeightedOnlineLearningMultipleCallsSameFeatureDifferentClasses() {
FeatureMap map = new FeatureMap();
map.dim = 1;
// map.add("f");
int fid = map.wordToFid.get("f");
MemoryEfficientNB model = new MemoryEfficientNB(map, 2);
model.weightedOnlineLearning(new int[] { fid }, 1.0, 0);
model.weightedOnlineLearning(new int[] { fid }, 1.0, 1);
Document doc0 = new Document(new String[] { "f" }, 0);
double[] confidence = model.getPredictionConfidence(doc0);
assertEquals(1.0, confidence[0] + confidence[1], 1e-6);
assertTrue(confidence[0] > 0);
assertTrue(confidence[1] > 0);
}

@Test
public void testGetTopPmiWordsSkipsFeatureWhenProbabilityZero() {
FeatureMap map = new FeatureMap();
map.dim = 2;
// map.add("zero1");
// map.add("zero2");
MemoryEfficientNB model = new MemoryEfficientNB(map, 2);
int fidZero = map.wordToFid.get("zero1");
int fidLow = map.wordToFid.get("zero2");
model.weightedOnlineLearning(new int[] { fidLow }, 1.0, 1);
CharacteristicWords result = model.getTopPmiWords(0, 5, 0.1, 0);
assertEquals(0, result.topWords.size());
}

@Test
public void testGetTopPmiWordsIgnoresBelowAppThreshold() {
FeatureMap map = new FeatureMap();
map.dim = 1;
// map.add("lowFreq");
MemoryEfficientNB model = new MemoryEfficientNB(map, 1);
int fid = map.wordToFid.get("lowFreq");
model.weightedOnlineLearning(new int[] { fid }, 1.0, 0);
CharacteristicWords words = model.getTopPmiWords(0, 5, 0.0, 5);
assertEquals(0, words.topWords.size());
}

@Test
public void testGetExtendedFeaturesIncludesAllClassesWithConfidence() {
FeatureMap map = new FeatureMap();
map.dim = 2;
// map.add("apple");
// map.add("banana");
MemoryEfficientNB model = new MemoryEfficientNB(map, 2);
model.onlineLearning(new Document(new String[] { "apple" }, 0));
model.onlineLearning(new Document(new String[] { "banana" }, 1));
Document testDoc = new Document(new String[] { "apple", "banana" }, 0);
String result = model.getExtendedFeatures(testDoc);
assertTrue(result.contains("0("));
assertTrue(result.contains("1("));
}

@Test
public void testClassifyTiesDefaultToFirstMaxClass() {
FeatureMap map = new FeatureMap();
map.dim = 1;
// map.add("tie");
int fid = map.wordToFid.get("tie");
MemoryEfficientNB model = new MemoryEfficientNB(map, 2);
model.weightedOnlineLearning(new int[] { fid }, 1.0, 0);
model.weightedOnlineLearning(new int[] { fid }, 1.0, 1);
Document d = new Document(new String[] { "tie" }, 0);
int result = model.classify(d, -1);
assertEquals(0, result);
}

@Test
public void testGetPredictionConfidenceHandlesZeroLikelihoodGracefully() {
FeatureMap map = new FeatureMap();
map.dim = 2;
// map.add("zero1");
// map.add("zero2");
MemoryEfficientNB model = new MemoryEfficientNB(map, 1);
Document d = new Document(new String[] { "zero1", "zero2" }, 0);
double[] conf = model.getPredictionConfidence(d);
assertTrue(conf[0] >= 0);
assertEquals(1.0, conf[0], 1e-6);
}

@Test
// public void testLoadFromSavedModelDoesNotAlterPredictions() throws IOException {
// FeatureMap map = new FeatureMap();
// map.dim = 1;
// map.add("persist");
// MemoryEfficientNB model = new MemoryEfficientNB(map, 1);
// Document doc = new Document(new String[] { "persist" }, 0);
// model.onlineLearning(doc);
// File tmpFile = File.createTempFile("persistModel", ".nb");
// String basePath = tmpFile.getAbsolutePath();
// model.save(basePath);
// MemoryEfficientNB loaded = new MemoryEfficientNB(basePath);
// Document testDoc = new Document(new String[] { "persist" }, 0);
// int prediction = loaded.classify(testDoc, -1);
// assertEquals(0, prediction);
// tmpFile.delete();
// new File(basePath + ".nb.featuremap").delete();
// }

// @Test
public void testUpdateFidCountsAddsNewEntryCorrectly() {
FeatureMap map = new FeatureMap();
map.dim = 1;
// map.add("x");
MemoryEfficientNB classifier = new MemoryEfficientNB(map, 1);
int fid = map.wordToFid.get("x");
int[] fids = new int[] { fid };
classifier.weightedOnlineLearning(fids, 1.0, 0);
Document doc = new Document(new String[] { "x" }, 0);
int prediction = classifier.classify(doc, -1);
assertEquals(0, prediction);
}

@Test
public void testUpdateFidCountsUpdatesExistingEntry() {
FeatureMap map = new FeatureMap();
map.dim = 1;
// map.add("token");
MemoryEfficientNB classifier = new MemoryEfficientNB(map, 1);
int fid = map.wordToFid.get("token");
int[] fids = new int[] { fid };
classifier.weightedOnlineLearning(fids, 1.0, 0);
classifier.weightedOnlineLearning(fids, 2.0, 0);
Document doc = new Document(new String[] { "token" }, 0);
int prediction = classifier.classify(doc, -1);
assertEquals(0, prediction);
}

@Test
public void testClassifyWithThresholdExactlyMet() {
FeatureMap map = new FeatureMap();
map.dim = 1;
// map.add("foo");
MemoryEfficientNB classifier = new MemoryEfficientNB(map, 1);
Document doc = new Document(new String[] { "foo" }, 0);
classifier.onlineLearning(doc);
double[] conf = classifier.getPredictionConfidence(doc);
double threshold = conf[0];
int prediction = classifier.classify(doc, threshold);
assertEquals(0, prediction);
}

@Test
public void testGetPriorHandlesMultipleSamplesCorrectly() {
FeatureMap map = new FeatureMap();
map.dim = 1;
// map.add("f");
MemoryEfficientNB classifier = new MemoryEfficientNB(map, 2);
int fid = map.wordToFid.get("f");
classifier.weightedOnlineLearning(new int[] { fid }, 3.0, 0);
classifier.weightedOnlineLearning(new int[] { fid }, 1.0, 1);
double prior0 = classifier.getPrior(0);
double prior1 = classifier.getPrior(1);
assertEquals(0.75, prior0, 1e-6);
assertEquals(0.25, prior1, 1e-6);
}

@Test
public void testSaveAndLoadWithMultipleFeaturesAndClasses() throws Exception {
FeatureMap map = new FeatureMap();
map.dim = 2;
// map.add("left");
// map.add("right");
MemoryEfficientNB classifier = new MemoryEfficientNB(map, 2);
classifier.onlineLearning(new Document(new String[] { "left" }, 0));
classifier.onlineLearning(new Document(new String[] { "right" }, 1));
File saveFile = File.createTempFile("complex", ".nb");
String basePath = saveFile.getAbsolutePath();
classifier.save(basePath);
MemoryEfficientNB loaded = new MemoryEfficientNB(basePath);
Document doc0 = new Document(new String[] { "left" }, 0);
Document doc1 = new Document(new String[] { "right" }, 1);
assertEquals(0, loaded.classify(doc0, -1));
assertEquals(1, loaded.classify(doc1, -1));
saveFile.delete();
new File(basePath + ".nb.featuremap").delete();
}

@Test
public void testGetTopPmiWordsWithExactProbabilityMatchOnThreshold() {
FeatureMap map = new FeatureMap();
map.dim = 1;
// map.add("perfect");
MemoryEfficientNB classifier = new MemoryEfficientNB(map, 1);
Document doc = new Document(new String[] { "perfect" }, 0);
classifier.weightedOnlineLearning(doc.getActiveFid(classifier.map), 1.0, 0);
CharacteristicWords words = classifier.getTopPmiWords(0, 5, 1.0, 0);
assertEquals(1, words.topWords.size());
assertEquals("perfect", words.topWords.get(0));
}

@Test
public void testClassifyReturnsNegativeOneWhenThresholdTooHigh() {
FeatureMap map = new FeatureMap();
map.dim = 1;
// map.add("bar");
MemoryEfficientNB classifier = new MemoryEfficientNB(map, 1);
classifier.onlineLearning(new Document(new String[] { "bar" }, 0));
int result = classifier.classify(new Document(new String[] { "bar" }, 0), 0.9999);
assertEquals(-1, result);
}

@Test
public void testToBeKeptAllTokensAreCoolButBelowMinLength() {
Vector<String> tokens = new Vector<>();
tokens.add("good");
tokens.add("good");
Hashtable<String, Integer> coolWords = new Hashtable<>();
coolWords.put("good", 1);
boolean keep = MemoryEfficientNB.toBeKept(tokens, coolWords, 0.5, 3);
assertFalse(keep);
}

@Test
public void testToBeKeptAllTokensAreCoolAndMeetRatioAndLength() {
Vector<String> tokens = new Vector<>();
tokens.add("great");
tokens.add("great");
tokens.add("awesome");
Hashtable<String, Integer> coolWords = new Hashtable<>();
coolWords.put("great", 1);
coolWords.put("awesome", 1);
boolean keep = MemoryEfficientNB.toBeKept(tokens, coolWords, 0.5, 2);
assertTrue(keep);
}

@Test
public void testAllocateSpaceInitializesCountsCorrectly() {
FeatureMap map = new FeatureMap();
map.dim = 3;
// map.add("a");
// map.add("b");
// map.add("c");
MemoryEfficientNB classifier = new MemoryEfficientNB(map, 2);
assertEquals(3, classifier.wordCounts.length);
assertEquals(2, classifier.weights.length);
assertEquals(2, classifier.classCounts.length);
assertEquals(2, classifier.fidCounts.size());
}

@Test
public void testGetFidProbWhenWeightIsZero() {
FeatureMap map = new FeatureMap();
map.dim = 1;
// map.add("w0");
MemoryEfficientNB classifier = new MemoryEfficientNB(map, 1);
int fid = map.wordToFid.get("w0");
classifier.fidCounts.get(0).put(fid, 3.0);
classifier.weights[0] = 0.0;
double prob = classifier.getFidProb(fid, 0);
assertEquals(MemoryEfficientNB.smooth / map.dim, prob, 1e-5);
}

@Test
public void testGetPredictionConfidenceWhenAllLogLikelihoodsEqual() {
FeatureMap map = new FeatureMap();
map.dim = 1;
// map.add("token");
MemoryEfficientNB classifier = new MemoryEfficientNB(map, 2);
int fid = map.wordToFid.get("token");
classifier.weightedOnlineLearning(new int[] { fid }, 1.0, 0);
classifier.weightedOnlineLearning(new int[] { fid }, 1.0, 1);
Document doc = new Document(new String[] { "token" }, 0);
double[] conf = classifier.getPredictionConfidence(doc);
assertNotNull(conf);
assertEquals(2, conf.length);
assertEquals(1.0, conf[0] + conf[1], 1e-6);
assertTrue(conf[0] > 0.0);
assertTrue(conf[1] > 0.0);
}

@Test
public void testClassifyReturnsMaxClassWhenThresholdIsNegative() {
FeatureMap map = new FeatureMap();
map.dim = 1;
// map.add("foo");
MemoryEfficientNB classifier = new MemoryEfficientNB(map, 2);
classifier.onlineLearning(new Document(new String[] { "foo" }, 0));
Document doc = new Document(new String[] { "foo" }, 0);
int prediction = classifier.classify(doc, -1.0);
assertEquals(0, prediction);
}

@Test
public void testClassifyReturnsNegativeOneWhenConfidenceBelowThreshold() {
FeatureMap map = new FeatureMap();
map.dim = 1;
// map.add("t");
MemoryEfficientNB classifier = new MemoryEfficientNB(map, 2);
classifier.weightedOnlineLearning(new int[] { map.wordToFid.get("t") }, 1.0, 0);
classifier.weightedOnlineLearning(new int[] { map.wordToFid.get("t") }, 1.0, 1);
Document doc = new Document(new String[] { "t" }, 0);
int prediction = classifier.classify(doc, 0.99);
assertEquals(-1, prediction);
}

@Test
public void testFidProbReturnsSmoothForUnseenFeature() {
FeatureMap map = new FeatureMap();
map.dim = 1;
// map.add("z");
MemoryEfficientNB classifier = new MemoryEfficientNB(map, 1);
int unseenFid = 999;
double prob = classifier.getFidProb(unseenFid, 0);
assertEquals(MemoryEfficientNB.smooth / map.dim, prob, 1e-6);
}

@Test
public void testGetTopPmiWordsSkipsImportantWordWhenImportanceBelowThreshold() {
FeatureMap map = new FeatureMap();
map.dim = 1;
// map.add("low");
MemoryEfficientNB classifier = new MemoryEfficientNB(map, 1);
int fid = map.wordToFid.get("low");
classifier.weightedOnlineLearning(new int[] { fid }, 0.01, 0);
CharacteristicWords topWords = classifier.getTopPmiWords(0, 5, 2.0, 0);
assertTrue(topWords.topWords.isEmpty());
}

@Test
public void testGetExtendedFeaturesReturnsEmptyWhenConfidenceIsZero() {
FeatureMap map = new FeatureMap();
map.dim = 1;
// map.add("x");
MemoryEfficientNB classifier = new MemoryEfficientNB(map, 2);
Document doc = new Document(new String[] { "unseen" }, 0);
String extended = classifier.getExtendedFeatures(doc);
assertTrue(extended.contains("0(") || extended.contains("1("));
}

@Test
public void testAllocateSpaceInitializesWordCountsCorrectly() {
FeatureMap map = new FeatureMap();
map.dim = 3;
// map.add("a");
// map.add("b");
// map.add("c");
MemoryEfficientNB classifier = new MemoryEfficientNB(map, 2);
assertEquals(3, classifier.wordCounts.length);
assertEquals(2, classifier.fidCounts.size());
assertEquals(2, classifier.weights.length);
}

@Test
public void testGetPredictionConfidenceReturnsUniformForEmptyInput() {
FeatureMap map = new FeatureMap();
map.dim = 2;
// map.add("x");
// map.add("y");
MemoryEfficientNB classifier = new MemoryEfficientNB(map, 2);
Document doc = new Document(new String[] {}, 0);
double[] confidence = classifier.getPredictionConfidence(doc);
assertEquals(2, confidence.length);
assertEquals(1.0, confidence[0] + confidence[1], 1e-6);
}

@Test
public void testSaveAndReloadWithZeroSamples() throws Exception {
FeatureMap map = new FeatureMap();
map.dim = 1;
// map.add("zero");
MemoryEfficientNB classifier = new MemoryEfficientNB(map, 2);
File tmp = File.createTempFile("zeroTest", ".nb");
String filePath = tmp.getAbsolutePath();
classifier.save(filePath);
MemoryEfficientNB loaded = new MemoryEfficientNB(filePath);
Document doc = new Document(new String[] { "zero" }, 0);
double[] conf = loaded.getPredictionConfidence(doc);
assertEquals(1.0, conf[0] + conf[1], 1e-6);
tmp.delete();
new File(filePath + ".nb.featuremap").delete();
}

@Test
public void testGetTopPmiWordsHandlesZeroFidAndZeroWordCounts() {
FeatureMap map = new FeatureMap();
map.dim = 2;
// map.add("alpha");
// map.add("beta");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
CharacteristicWords result = nb.getTopPmiWords(0, 10, 0.1, 0);
assertNotNull(result);
assertTrue(result.topWords.isEmpty());
}

@Test
public void testGetFidProbHandleClassWithoutFidCountEntry() {
FeatureMap map = new FeatureMap();
map.dim = 2;
// map.add("term1");
// map.add("term2");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
int fid = map.wordToFid.get("term1");
nb.weightedOnlineLearning(new int[] { fid }, 1.0, 0);
double fidProb = nb.getFidProb(fid, 1);
assertEquals(MemoryEfficientNB.smooth / map.dim, fidProb, 1e-6);
}

@Test
public void testGetPredictionConfidenceWithZeroWeightFeature() {
FeatureMap map = new FeatureMap();
map.dim = 1;
// map.add("rare");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
int fid = map.wordToFid.get("rare");
nb.fidCounts.get(0).put(fid, 3.0);
nb.weights[0] = 0.0;
double[] conf = nb.getPredictionConfidence(new Document(new String[] { "rare" }, 0));
assertEquals(1.0, conf[0], 1e-6);
}

@Test
public void testToBeKeptReturnsFalseWhenTokenSetIsEmpty() {
Vector<String> tokens = new Vector<>();
Hashtable<String, Integer> coolWords = new Hashtable<>();
boolean result = MemoryEfficientNB.toBeKept(tokens, coolWords, 0.5, 1);
assertFalse(result);
}

@Test
public void testToBeKeptReturnsFalseWhenNoCoolWordsPresent() {
Vector<String> tokens = new Vector<>();
tokens.add("a");
tokens.add("b");
tokens.add("c");
Hashtable<String, Integer> coolWords = new Hashtable<>();
coolWords.put("x", 1);
boolean result = MemoryEfficientNB.toBeKept(tokens, coolWords, 0.5, 1);
assertFalse(result);
}

@Test
public void testToBeKeptPassesExactlyOnMinRatioBoundary() {
Vector<String> tokens = new Vector<>();
tokens.add("apple");
tokens.add("banana");
Hashtable<String, Integer> coolWords = new Hashtable<>();
coolWords.put("apple", 1);
boolean result = MemoryEfficientNB.toBeKept(tokens, coolWords, 0.5, 2);
assertTrue(result);
}

@Test
public void testClassifyWithUnknownFeatureFallsBackToSmoothedProbabilities() {
FeatureMap map = new FeatureMap();
map.dim = 1;
// map.add("known");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
nb.onlineLearning(new Document(new String[] { "known" }, 0));
Document testDoc = new Document(new String[] { "unknown" }, 0);
int prediction = nb.classify(testDoc, -1);
assertTrue(prediction == 0 || prediction == 1);
}

@Test
public void testGetExtendedFeaturesWithUniformProbability() {
FeatureMap map = new FeatureMap();
map.dim = 1;
// map.add("word");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
Document testDoc = new Document(new String[] { "word" }, 0);
String result = nb.getExtendedFeatures(testDoc);
assertNotNull(result);
assertTrue(result.contains("0(") || result.contains("1("));
}

@Test
public void testClassifyReturnsNegativeOneOnInvalidHighThreshold() {
FeatureMap map = new FeatureMap();
map.dim = 1;
// map.add("feature");
MemoryEfficientNB classifier = new MemoryEfficientNB(map, 2);
Document doc = new Document(new String[] { "feature" }, 1);
classifier.onlineLearning(doc);
int result = classifier.classify(doc, 1.1);
assertEquals(-1, result);
}

@Test
public void testClassifyReturnsZeroWithUniformConfidenceAndNoTraining() {
FeatureMap map = new FeatureMap();
map.dim = 1;
// map.add("blank");
MemoryEfficientNB classifier = new MemoryEfficientNB(map, 2);
Document doc = new Document(new String[] { "blank" }, 1);
int result = classifier.classify(doc, -1);
assertEquals(0, result);
}

@Test
public void testGetFidProbOnUninitializedFidCounts() {
FeatureMap map = new FeatureMap();
map.dim = 5;
// map.add("hello");
// map.add("world");
// map.add("nlp");
// map.add("java");
// map.add("unit");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
int fid = map.wordToFid.get("java");
double prob = nb.getFidProb(fid, 1);
assertEquals(MemoryEfficientNB.smooth / map.dim, prob, 1e-6);
}

@Test
public void testGetAccReturnsZeroWhenNoPredictionMatches() {
FeatureMap map = new FeatureMap();
map.dim = 1;
// map.add("text");
MemoryEfficientNB model = new MemoryEfficientNB(map, 1);
DocumentCollection test = new DocumentCollection();
Document testDoc = new Document(new String[] { "text" }, 0);
test.docs.add(testDoc);
double acc = model.getAcc(test);
assertEquals(0.0, acc, 1e-6);
}

@Test
public void testGetPredictionConfidenceNormalizeZeroLogLikelihoods() {
FeatureMap map = new FeatureMap();
map.dim = 2;
// map.add("a");
// map.add("b");
MemoryEfficientNB model = new MemoryEfficientNB(map, 2);
Document d = new Document(new String[] { "c", "d" }, 0);
double[] result = model.getPredictionConfidence(d);
assertEquals(2, result.length);
assertEquals(1.0, result[0] + result[1], 1e-6);
}

@Test
public void testSaveLoadRoundTripPreservesFidCount() throws Exception {
FeatureMap map = new FeatureMap();
map.dim = 1;
// map.add("persist");
MemoryEfficientNB original = new MemoryEfficientNB(map, 1);
original.onlineLearning(new Document(new String[] { "persist" }, 0));
File tmp = File.createTempFile("nb_roundtrip", ".nb");
String path = tmp.getAbsolutePath();
original.save(path);
MemoryEfficientNB loaded = new MemoryEfficientNB(path);
Document testDoc = new Document(new String[] { "persist" }, 0);
int predicted = loaded.classify(testDoc, -1);
assertEquals(0, predicted);
tmp.delete();
new File(path + ".nb.featuremap").delete();
}

@Test
public void testWeightedOnlineLearningWithEmptyFeatureArray() {
FeatureMap map = new FeatureMap();
map.dim = 1;
// map.add("skip");
MemoryEfficientNB model = new MemoryEfficientNB(map, 2);
int[] emptyFidArray = new int[] {};
model.weightedOnlineLearning(emptyFidArray, 1.0, 0);
Document d = new Document(new String[] {}, 0);
double[] conf = model.getPredictionConfidence(d);
assertEquals(1.0, conf[0] + conf[1], 1e-6);
}

@Test
public void testGetTopPmiWordsWithoutFeatureOccurrenceMeetingMinThreshold() {
FeatureMap map = new FeatureMap();
map.dim = 1;
// map.add("nope");
MemoryEfficientNB model = new MemoryEfficientNB(map, 1);
model.weightedOnlineLearning(new int[] { map.wordToFid.get("nope") }, 1.0, 0);
CharacteristicWords words = model.getTopPmiWords(0, 5, 0.0, 10);
assertTrue(words.topWords.isEmpty());
}

@Test
public void testGetTopPmiWordsImportanceExactlyOnThreshold() {
FeatureMap map = new FeatureMap();
map.dim = 1;
// map.add("perfect");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
int fid = map.wordToFid.get("perfect");
nb.weightedOnlineLearning(new int[] { fid }, 1.0, 0);
CharacteristicWords words = nb.getTopPmiWords(0, 5, 1.0, 0);
assertEquals(1, words.topWords.size());
assertEquals("perfect", words.topWords.get(0));
}
}
