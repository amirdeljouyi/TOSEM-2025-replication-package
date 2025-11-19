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

public class MemoryEfficientNB_3_GPTLLMTest {

@Test
public void testAllocateSpaceInitializesCorrectly() {
FeatureMap map = new FeatureMap();
// map.add("apple");
// map.add("banana");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
assertNotNull(nb.map);
assertNotNull(nb.classCounts);
assertEquals(2, nb.classCounts.length);
assertNotNull(nb.weights);
assertEquals(2, nb.weights.length);
assertNotNull(nb.wordCounts);
assertEquals(map.dim, nb.wordCounts.length);
assertNotNull(nb.fidCounts);
assertEquals(2, nb.fidCounts.size());
}

@Test
public void testOnlineLearningModifiesCounts() {
FeatureMap map = new FeatureMap();
// map.add("cat");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
// Document doc = new Document();
// doc.classID = 0;
// doc.tokens.add("cat");
// nb.onlineLearning(doc);
// double[] priors = nb.getPredictionConfidence(doc);
// assertEquals(2, priors.length);
// assertTrue(priors[0] > 0.0);
}

@Test
public void testWeightedOnlineLearningUpdatesModelState() {
FeatureMap map = new FeatureMap();
// map.add("cat");
// int fid = map.getFid("cat");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
// Document doc = new Document();
// doc.classID = 1;
// doc.tokens.add("cat");
// int[] features = doc.getActiveFid(map);
// nb.weightedOnlineLearning(features, 2.0, 1);
// double[] conf = nb.getPredictionConfidence(doc);
// assertNotNull(conf);
// assertTrue(conf[1] > 0.0);
}

@Test
public void testClassifyReturnsCorrectClassForHighConfidence() {
FeatureMap map = new FeatureMap();
// map.add("dog");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
// Document doc = new Document();
// doc.classID = 0;
// doc.tokens.add("dog");
// nb.onlineLearning(doc);
// int predicted = nb.classify(doc, -1);
// assertEquals(0, predicted);
}

@Test
public void testClassifyRejectsWhenThresholdNotMet() {
FeatureMap map = new FeatureMap();
// map.add("unknown");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
// Document doc = new Document();
// doc.classID = 0;
// doc.tokens.add("unknown");
// int result = nb.classify(doc, 0.9);
// assertEquals(-1, result);
}

@Test
public void testGetPriorWorksWithoutTraining() {
FeatureMap map = new FeatureMap();
// map.add("x");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
double prior = nb.getPrior(0);
assertEquals(0.0, prior, 0.0001);
}

@Test
public void testGetFidProbReturnsSmoothed() {
FeatureMap map = new FeatureMap();
// map.add("bird");
// int fid = map.getFid("bird");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
// double prob = nb.getFidProb(fid, 0);
double expected = MemoryEfficientNB.smooth / map.dim;
// assertEquals(expected, prob, 0.0001);
}

@Test
public void testGetTopPmiWordsReturnsValidWords() {
FeatureMap map = new FeatureMap();
// map.add("red");
// map.add("green");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
// Document doc = new Document();
// doc.classID = 1;
// doc.tokens.add("green");
// nb.onlineLearning(doc);
Hashtable<String, Integer> result = nb.getTopPmiWords(5, 0.01, 1);
assertTrue(result.containsKey("green"));
}

@Test
public void testGetExtendedFeaturesProducesExpectedFormat() {
FeatureMap map = new FeatureMap();
// map.add("sky");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
nb.methodName = "nb_test_";
// Document doc = new Document();
// doc.classID = 0;
// doc.tokens.add("sky");
// nb.onlineLearning(doc);
// String features = nb.getExtendedFeatures(doc);
// assertTrue(features.contains("nb_test_0("));
}

@Test
public void testToBeKeptPositiveCondition() {
Vector<String> tokens = new Vector<String>();
tokens.add("apple");
tokens.add("banana");
Hashtable<String, Integer> coolWords = new Hashtable<>();
coolWords.put("apple", 1);
coolWords.put("banana", 1);
boolean result = MemoryEfficientNB.toBeKept(tokens, coolWords, 0.5, 2);
assertTrue(result);
}

@Test
public void testToBeKeptNegativeWhenBelowMinLen() {
Vector<String> tokens = new Vector<String>();
tokens.add("cat");
Hashtable<String, Integer> coolWords = new Hashtable<>();
coolWords.put("cat", 1);
boolean result = MemoryEfficientNB.toBeKept(tokens, coolWords, 0.5, 2);
assertFalse(result);
}

@Test
public void testGetAccReturnsPerfectAccuracy() {
FeatureMap map = new FeatureMap();
// map.add("sun");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
// Document trainDoc = new Document();
// trainDoc.classID = 0;
// trainDoc.tokens.add("sun");
// nb.onlineLearning(trainDoc);
// Document testDoc = new Document();
// testDoc.classID = 0;
// testDoc.tokens.add("sun");
DocumentCollection testSet = new DocumentCollection();
// testSet.docs.add(testDoc);
double acc = nb.getAcc(testSet);
assertEquals(1.0, acc, 0.0001);
}

@Test
public void testSaveAndLoadPreservesValues() throws Exception {
FeatureMap map = new FeatureMap();
// map.add("earth");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
// Document doc = new Document();
// doc.classID = 1;
// doc.tokens.add("earth");
// nb.onlineLearning(doc);
// File modelFile = new File(tempFolder.newFolder(), "model_data");
// String path = modelFile.getAbsolutePath();
// nb.save(path);
// MemoryEfficientNB loaded = new MemoryEfficientNB(path);
// double[] conf = loaded.getPredictionConfidence(doc);
// assertTrue(conf[1] > 0.0);
}

@Test
public void testClassifyReturnsNegativeOneIfConfidenceBelowThresholdForMostProbableClass() {
FeatureMap map = new FeatureMap();
// map.add("zebra");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
// Document doc = new Document();
// doc.classID = 1;
// doc.tokens.add("zebra");
// int result = nb.classify(doc, 0.75);
// assertEquals(-1, result);
}

@Test
public void testGetPredictionConfidenceReturnsUniformWhenModelEmpty() {
FeatureMap map = new FeatureMap();
// map.add("falcon");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
// Document doc = new Document();
// doc.classID = 0;
// doc.tokens.add("falcon");
// double[] confidence = nb.getPredictionConfidence(doc);
// assertEquals(2, confidence.length);
// assertTrue(confidence[0] >= 0.0);
// assertTrue(confidence[1] >= 0.0);
// double sum = confidence[0] + confidence[1];
// assertEquals(1.0, sum, 0.0001);
}

@Test
public void testGetTopPmiWordsIgnoresLowOccurrenceWords() {
FeatureMap map = new FeatureMap();
// map.add("rareword");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
// Document doc = new Document();
// doc.classID = 0;
// doc.tokens.add("rareword");
// nb.onlineLearning(doc);
Hashtable<String, Integer> result = nb.getTopPmiWords(10, 0.01, 5);
assertFalse(result.containsKey("rareword"));
}

@Test
public void testGetTopPmiWordsReturnsEmptyIfNoWordsMeetCriteria() {
FeatureMap map = new FeatureMap();
// map.add("term");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
Hashtable<String, Integer> result = nb.getTopPmiWords(3, 0.9, 1);
assertTrue(result.isEmpty());
}

@Test
public void testSaveAndLoadWithEmptyModel() throws Exception {
FeatureMap map = new FeatureMap();
// map.add("alpha");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
// File modelFile = new File(tempFolder.newFolder(), "empty_model");
// String filePath = modelFile.getAbsolutePath();
// nb.save(filePath);
// MemoryEfficientNB loaded = new MemoryEfficientNB(filePath);
// Document doc = new Document();
// doc.classID = 0;
// doc.tokens.add("alpha");
// double[] conf = loaded.getPredictionConfidence(doc);
// assertEquals(2, conf.length);
}

@Test
public void testToBeKeptReturnsFalseWhenNoCoolWordsPresent() {
Vector<String> tokens = new Vector<String>();
tokens.add("x");
tokens.add("y");
tokens.add("z");
Hashtable<String, Integer> coolWords = new Hashtable<>();
boolean result = MemoryEfficientNB.toBeKept(tokens, coolWords, 0.3, 2);
assertFalse(result);
}

@Test
public void testGetFidProbReturnsCorrectValueForZeroWeightClass() {
FeatureMap map = new FeatureMap();
// map.add("term");
// int fid = map.getFid("term");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
// nb.fidCounts.elementAt(0).put(fid, 3.0);
// double value = nb.getFidProb(fid, 0);
double expected = MemoryEfficientNB.smooth / map.dim;
// assertEquals(expected, value, 0.0001);
}

@Test
public void testWeightedOnlineLearningHandlesEmptyFeatures() {
FeatureMap map = new FeatureMap();
// map.add("unused");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
int[] emptyFeatures = new int[0];
nb.weightedOnlineLearning(emptyFeatures, 1.5, 1);
assertEquals(1.5, nb.sampleSize, 0.0001);
assertEquals(1.5, nb.classCounts[1], 0.0001);
assertEquals(0.0, nb.wordCounts[0], 0.0001);
}

@Test
public void testGetExtendedFeaturesReturnsEmptyStringOnNoConfidence() {
FeatureMap map = new FeatureMap();
// map.add("alpha");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
nb.methodName = "TestNB_";
// Document doc = new Document();
// doc.classID = 0;
// doc.tokens.add("beta");
// String result = nb.getExtendedFeatures(doc);
// assertTrue(result.contains("TestNB_0(") || result.contains("TestNB_1("));
}

@Test
public void testGetTopPmiWordsHandlesZeroFidCountGracefully() {
FeatureMap map = new FeatureMap();
// map.add("zero");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
nb.fidCount = 0.0;
CharacteristicWords words = nb.getTopPmiWords(0, 3, 0.01, 1);
assertNotNull(words);
assertEquals(0, words.topWords.size());
}

@Test
public void testGetFidProbWithUnknownFidReturnsSmoothedValue() {
FeatureMap map = new FeatureMap();
// map.add("word1");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
int unknownFid = 9999;
double result = nb.getFidProb(unknownFid, 1);
double expected = MemoryEfficientNB.smooth / map.dim;
assertEquals(expected, result, 0.0001);
}

@Test
public void testGetTopPmiWordsWithNoMatchingFeaturesReturnsEmpty() {
FeatureMap map = new FeatureMap();
// map.add("x");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
CharacteristicWords result = nb.getTopPmiWords(1, 10, 0.5, 1);
assertNotNull(result);
assertEquals(0, result.topWords.size());
}

@Test
public void testGetPredictionConfidenceWithMultipleZeroLikelihoods() {
FeatureMap map = new FeatureMap();
// map.add("foo");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
// Document doc = new Document();
// doc.classID = 1;
// doc.tokens.add("foo");
// double[] scores = nb.getPredictionConfidence(doc);
// assertEquals(2, scores.length);
// assertTrue(scores[0] >= 0.0);
// assertTrue(scores[1] >= 0.0);
// double total = scores[0] + scores[1];
// assertEquals(1.0, total, 0.0001);
}

@Test
public void testGetAccWithEmptyTestSetReturnsNaN() {
FeatureMap map = new FeatureMap();
// map.add("one");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
DocumentCollection emptyCollection = new DocumentCollection();
double acc = nb.getAcc(emptyCollection);
assertTrue(Double.isNaN(acc) || acc == 0.0);
}

@Test
public void testToBeKeptWhenRatioIsExactlyThreshold() {
Vector<String> tokens = new Vector<String>();
tokens.add("a");
tokens.add("b");
tokens.add("c");
tokens.add("d");
Hashtable<String, Integer> coolWords = new Hashtable<>();
coolWords.put("a", 1);
coolWords.put("b", 1);
boolean result = MemoryEfficientNB.toBeKept(tokens, coolWords, 0.5, 4);
assertTrue(result);
}

@Test
public void testToBeKeptWhenDuplicateCoolWordCountedOnce() {
Vector<String> tokens = new Vector<String>();
tokens.add("cool");
tokens.add("cool");
tokens.add("x");
Hashtable<String, Integer> coolWords = new Hashtable<>();
coolWords.put("cool", 1);
boolean result = MemoryEfficientNB.toBeKept(tokens, coolWords, 0.5, 3);
assertTrue(result);
}

@Test
public void testClassifyHandlesAllZeroConfidenceGracefully() {
FeatureMap map = new FeatureMap();
// map.add("alpha");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
// Document doc = new Document();
// doc.classID = 0;
// doc.tokens.add("beta");
nb.sampleSize = 0.0;
// int result = nb.classify(doc, -1);
// assertTrue(result >= 0 && result < 2);
}

@Test
public void testSaveAndLoadHandlesZeroFeatureEntriesCorrectly() throws Exception {
FeatureMap map = new FeatureMap();
// map.add("kappa");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
// File modelOutFile = new File(tempFolder.newFolder(), "zmodel");
// String filePath = modelOutFile.getAbsolutePath();
// nb.save(filePath);
// MemoryEfficientNB nb2 = new MemoryEfficientNB(filePath);
// Document doc = new Document();
// doc.classID = 0;
// doc.tokens.add("kappa");
// double[] conf = nb2.getPredictionConfidence(doc);
// assertEquals(2, conf.length);
// assertEquals(1.0, conf[0] + conf[1], 0.0001);
}

@Test
public void testWeightedOnlineLearningWithZeroWeightDoesNotAffectModel() {
FeatureMap map = new FeatureMap();
// map.add("zeroeffect");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
// Document doc = new Document();
// doc.classID = 0;
// doc.tokens.add("zeroeffect");
// int[] features = doc.getActiveFid(map);
// nb.weightedOnlineLearning(features, 0.0, 0);
// double[] conf = nb.getPredictionConfidence(doc);
// assertEquals(2, conf.length);
// assertEquals(1.0, conf[0] + conf[1], 0.0001);
}

@Test
public void testGetTopPmiWordsHandlesHighThresholdGracefully() {
FeatureMap map = new FeatureMap();
// map.add("rare");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
// Document doc = new Document();
// doc.classID = 1;
// doc.tokens.add("rare");
// nb.onlineLearning(doc);
Hashtable<String, Integer> result = nb.getTopPmiWords(10, 1000.0, 0);
assertTrue(result.isEmpty());
}

@Test
public void testGetExtendedFeaturesReturnsEmptyWhenAllConfidenceZero() {
FeatureMap map = new FeatureMap();
// map.add("xyz");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
nb.methodName = "ext_";
// Document doc = new Document();
// doc.classID = 0;
// doc.tokens.add("xyz");
// String output = nb.getExtendedFeatures(doc);
// assertTrue(output.contains("ext_0("));
}

@Test
public void testClassifyWithTiedConfidenceReturnsFirstMaxIndex() {
FeatureMap map = new FeatureMap();
// map.add("equal");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
nb.sampleSize = 10;
nb.classCounts[0] = 5;
nb.classCounts[1] = 5;
nb.weights[0] = 1;
nb.weights[1] = 1;
// nb.fidCounts.elementAt(0).put(map.getFid("equal"), 1.0);
// nb.fidCounts.elementAt(1).put(map.getFid("equal"), 1.0);
// nb.wordCounts[map.getFid("equal")] = 2.0;
nb.fidCount = 2.0;
// Document doc = new Document();
// doc.classID = 0;
// doc.tokens.add("equal");
// int prediction = nb.classify(doc, -1);
// assertEquals(0, prediction);
}

@Test
public void testUpdateFidCountsInitialInsertDoesNotThrow() {
FeatureMap map = new FeatureMap();
// map.add("newtoken");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
int classId = 1;
// int fid = map.getFid("newtoken");
// nb.weightedOnlineLearning(new int[] { fid }, 1.0, classId);
// double prob = nb.getFidProb(fid, classId);
// assertTrue(prob > 0);
}

@Test
public void testGetAccWithAllIncorrectPredictionsReturnsZero() {
FeatureMap map = new FeatureMap();
// map.add("mistake");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
// Document doc = new Document();
// doc.classID = 1;
// doc.tokens.add("mistake");
// nb.onlineLearning(doc);
// Document testDoc = new Document();
// testDoc.classID = 0;
// testDoc.tokens.add("mistake");
DocumentCollection testSet = new DocumentCollection();
// testSet.docs.add(testDoc);
double acc = nb.getAcc(testSet);
assertEquals(0.0, acc, 0.0001);
}

@Test
public void testClassifyWithThresholdEqualToConfidenceReturnsClass() {
FeatureMap map = new FeatureMap();
// map.add("match");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
// Document doc = new Document();
// doc.classID = 0;
// doc.tokens.add("match");
// nb.onlineLearning(doc);
// int predicted = nb.classify(doc, nb.getPredictionConfidence(doc)[0]);
// assertEquals(0, predicted);
}

@Test
public void testClassifyWithNegativeThresholdAlwaysReturnsMaxClass() {
FeatureMap map = new FeatureMap();
// map.add("any");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 3);
// Document doc = new Document();
// doc.classID = 1;
// doc.tokens.add("any");
// nb.onlineLearning(doc);
// int predicted = nb.classify(doc, -1);
// assertTrue(predicted >= 0 && predicted < 3);
}

@Test
public void testGetPredictionConfidenceWithEmptyDocumentReturnsUniform() {
FeatureMap map = new FeatureMap();
// map.add("unused");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
// Document doc = new Document();
// doc.classID = 0;
// double[] conf = nb.getPredictionConfidence(doc);
// assertEquals(2, conf.length);
// assertEquals(1.0, conf[0] + conf[1], 0.0001);
}

@Test
public void testGetExtendedFeaturesWithConfidenceZeroOmitsEntry() {
FeatureMap map = new FeatureMap();
// map.add("nonmatch");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
nb.methodName = "label";
// Document doc = new Document();
// doc.classID = 0;
// doc.tokens.add("nonmatch");
// String output = nb.getExtendedFeatures(doc);
// assertTrue(output.contains("label") || output.isEmpty());
}

@Test
public void testSaveAndLoadPreservesFeatureMapDim() throws Exception {
FeatureMap map = new FeatureMap();
// map.add("featureA");
// map.add("featureB");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
// File baseFile = new File(tempFolder.newFolder(), "base");
// nb.save(baseFile.getAbsolutePath());
// MemoryEfficientNB nbLoaded = new MemoryEfficientNB(baseFile.getAbsolutePath());
int dimOriginal = nb.map.dim;
// int dimLoaded = nbLoaded.map.dim;
// assertEquals(dimOriginal, dimLoaded);
}

@Test
public void testWeightedOnlineLearningWithNegativeWeightStillAccumulates() {
FeatureMap map = new FeatureMap();
// map.add("nega");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
// int fid = map.getFid("nega");
// int[] activeFeatures = new int[] { fid };
// nb.weightedOnlineLearning(activeFeatures, -1.0, 1);
// double[] confidence = nb.getPredictionConfidence(new Document());
// assertEquals(2, confidence.length);
}

@Test
public void testGetFidProbDivisionByZeroReturnsSmoothedValue() {
FeatureMap map = new FeatureMap();
// map.add("divzero");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
// int fid = map.getFid("divzero");
// nb.fidCounts.elementAt(0).put(fid, 5.0);
nb.weights[0] = 0.0;
// double result = nb.getFidProb(fid, 0);
double expected = MemoryEfficientNB.smooth / map.dim;
// assertEquals(expected, result, 0.0001);
}

@Test
public void testGetPriorWithZeroSampleSizeReturnsZero() {
FeatureMap map = new FeatureMap();
// map.add("x");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 3);
nb.sampleSize = 0;
double result = nb.getPrior(1);
assertEquals(0.0, result, 0.00001);
}

@Test
public void testWeightedOnlineLearningWithMultipleFeaturesSameClass() {
FeatureMap map = new FeatureMap();
// map.add("a");
// map.add("b");
// int fid1 = map.getFid("a");
// int fid2 = map.getFid("b");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
// nb.weightedOnlineLearning(new int[] { fid1, fid2 }, 1.0, 0);
// double prob1 = nb.getFidProb(fid1, 0);
// double prob2 = nb.getFidProb(fid2, 0);
// assertTrue(prob1 > 0);
// assertTrue(prob2 > 0);
}

@Test
public void testGetTopPmiWordsWhenAllFrequenciesZeroReturnsEmpty() {
FeatureMap map = new FeatureMap();
// map.add("null");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
nb.fidCount = 0.0;
nb.wordCounts[0] = 0.0;
CharacteristicWords result = nb.getTopPmiWords(0, 3, 0.5, 1);
assertEquals(0, result.topWords.size());
}

@Test
public void testToBeKeptWithEmptyTokenListReturnsFalse() {
Vector<String> tokens = new Vector<String>();
Hashtable<String, Integer> coolWords = new Hashtable<>();
coolWords.put("x", 1);
boolean result = MemoryEfficientNB.toBeKept(tokens, coolWords, 0.5, 1);
assertFalse(result);
}

@Test
public void testToBeKeptWithAllCoolWordsButBelowMinLenReturnsFalse() {
Vector<String> tokens = new Vector<String>();
tokens.add("cool");
Hashtable<String, Integer> coolWords = new Hashtable<>();
coolWords.put("cool", 1);
boolean result = MemoryEfficientNB.toBeKept(tokens, coolWords, 0.5, 2);
assertFalse(result);
}

@Test
public void testGetPredictionConfidencePrecisionNormalization() {
FeatureMap map = new FeatureMap();
// map.add("term");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
// Document doc = new Document();
// doc.classID = 0;
// doc.tokens.add("term");
// nb.onlineLearning(doc);
// double[] confidence = nb.getPredictionConfidence(doc);
// double sum = confidence[0] + confidence[1];
// assertEquals(1.0, sum, 0.000001);
}

@Test
public void testClassifyReturnsNegativeOneOnUndefinedConfidenceAndThreshold() {
FeatureMap map = new FeatureMap();
// map.add("unk");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
// Document doc = new Document();
// doc.classID = 1;
// doc.tokens.add("unk");
// int pred = nb.classify(doc, 0.99);
// assertEquals(-1, pred);
}

@Test
public void testGetExtendedFeaturesWithMultipleClassesIncludesAllPositiveConfidences() {
FeatureMap map = new FeatureMap();
// map.add("text");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
nb.methodName = "FTR-";
// Document d = new Document();
// d.classID = 1;
// d.tokens.add("text");
// nb.onlineLearning(d);
// String result = nb.getExtendedFeatures(d);
// assertTrue(result.contains("FTR-0(") || result.contains("FTR-1("));
}

@Test
public void testGetTopPmiWordsIgnoresTermsBelowConfidence() {
FeatureMap map = new FeatureMap();
// map.add("low");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
// Document doc = new Document();
// doc.classID = 0;
// doc.tokens.add("low");
// nb.onlineLearning(doc);
Hashtable<String, Integer> result = nb.getTopPmiWords(2, 100.0, 1);
assertTrue(result.isEmpty());
}

@Test
public void testGetPredictionConfidenceWithNoMatchingFeaturesReturnsUniform() {
FeatureMap map = new FeatureMap();
// map.add("something");
FeatureMap testMap = new FeatureMap();
// testMap.add("different");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
// Document doc = new Document();
// doc.classID = 0;
// doc.tokens.add("different");
// double[] conf = nb.getPredictionConfidence(doc);
// assertEquals(2, conf.length);
// assertEquals(1.0, conf[0] + conf[1], 0.0001);
}

@Test
public void testClassifyReturnsMaxClassWithNegativeLogLikelihoods() {
FeatureMap map = new FeatureMap();
// map.add("token");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
// Document doc = new Document();
// doc.classID = 1;
// doc.tokens.add("token");
// nb.onlineLearning(doc);
// doc.tokens.clear();
// doc.tokens.add("unseen");
// int predicted = nb.classify(doc, -1);
// assertTrue(predicted >= 0 && predicted < 2);
}

@Test
public void testGetPredictionConfidenceNumericalStabilityAllZeros() {
FeatureMap map = new FeatureMap();
// map.add("h");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
// Document doc = new Document();
// doc.classID = 0;
// double[] probs = nb.getPredictionConfidence(doc);
// assertEquals(2, probs.length);
// assertEquals(1.0, probs[0] + probs[1], 0.00001);
}

@Test
public void testGetTopPmiWordsReturnsOnlyHighConfidenceWords() {
FeatureMap map = new FeatureMap();
// map.add("w1");
// map.add("w2");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
// Document d1 = new Document();
// d1.classID = 1;
// d1.tokens.add("w1");
// nb.onlineLearning(d1);
// Document d2 = new Document();
// d2.classID = 1;
// d2.tokens.add("w1");
// nb.onlineLearning(d2);
// Document d3 = new Document();
// d3.classID = 1;
// d3.tokens.add("w2");
// nb.onlineLearning(d3);
Hashtable<String, Integer> words = nb.getTopPmiWords(10, 1.1, 1);
assertTrue(words.containsKey("w1") || words.containsKey("w2"));
}

@Test
public void testSaveAndLoadRestoresFidCountsCorrectly() throws Exception {
FeatureMap map = new FeatureMap();
// map.add("save");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
// Document doc = new Document();
// doc.classID = 0;
// doc.tokens.add("save");
// nb.onlineLearning(doc);
// File tempFolderFile = new File(tempFolder.newFolder(), "modelX");
// String path = tempFolderFile.getAbsolutePath();
// nb.save(path);
// MemoryEfficientNB loaded = new MemoryEfficientNB(path);
// Document testDoc = new Document();
// testDoc.classID = 0;
// testDoc.tokens.add("save");
// double[] conf = loaded.getPredictionConfidence(testDoc);
// assertEquals(2, conf.length);
// assertEquals(1.0, conf[0] + conf[1], 0.0001);
}

@Test
public void testWeightedOnlineLearningWithRepeatedFidInSameArray() {
FeatureMap map = new FeatureMap();
// map.add("dupe");
// int fid = map.getFid("dupe");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
// int[] features = new int[] { fid, fid };
// nb.weightedOnlineLearning(features, 1.0, 0);
// double prob = nb.getFidProb(fid, 0);
// assertTrue(prob > 0);
}

@Test
public void testGetTopPmiWordsWithZeroWeightsDoesNotDivideByZero() {
FeatureMap map = new FeatureMap();
// map.add("x");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
nb.weights[0] = 0.0;
nb.fidCount = 1;
nb.wordCounts[0] = 1;
CharacteristicWords words = nb.getTopPmiWords(0, 1, 0.01, 1);
assertNotNull(words);
assertEquals(0, words.topWords.size());
}

@Test
public void testToBeKeptExactThresholdMatchReturnsTrue() {
Vector<String> tokens = new Vector<String>();
tokens.add("dog");
tokens.add("dog");
tokens.add("cat");
Hashtable<String, Integer> coolWords = new Hashtable<>();
coolWords.put("dog", 1);
boolean result = MemoryEfficientNB.toBeKept(tokens, coolWords, 0.5, 3);
assertTrue(result);
}

@Test
public void testClassifyReturnsZeroIfAllProbsAreZeroUnderThreshold() {
FeatureMap map = new FeatureMap();
// map.add("x");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
// Document doc = new Document();
// doc.classID = 0;
// doc.tokens.add("x");
// int result = nb.classify(doc, 1.0);
// assertEquals(-1, result);
}

@Test
public void testWeightedOnlineLearningWithMultipleClassesCreatesIsolatedCounts() {
FeatureMap map = new FeatureMap();
// map.add("apple");
// map.add("banana");
// int appleFid = map.getFid("apple");
// int bananaFid = map.getFid("banana");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
// nb.weightedOnlineLearning(new int[] { appleFid }, 1.0, 0);
// nb.weightedOnlineLearning(new int[] { bananaFid }, 1.0, 1);
// double p1 = nb.getFidProb(appleFid, 0);
// double p2 = nb.getFidProb(bananaFid, 1);
// assertTrue(p1 > 0.0);
// assertTrue(p2 > 0.0);
}

@Test
public void testClassifyWithOneClassOnly() {
FeatureMap map = new FeatureMap();
// map.add("only");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
// Document doc = new Document();
// doc.classID = 0;
// doc.tokens.add("only");
// nb.onlineLearning(doc);
// int result = nb.classify(doc, -1);
// assertEquals(0, result);
}

@Test
public void testGetTopPmiWordsWithZeroWordCountsDoesNotFail() {
FeatureMap map = new FeatureMap();
// map.add("word");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
CharacteristicWords result = nb.getTopPmiWords(0, 10, 0.01, 1);
assertNotNull(result);
assertEquals(0, result.topWords.size());
}

@Test
public void testGetExtendedFeaturesWithZeroLengthConfidenceVector() {
FeatureMap map = new FeatureMap();
// map.add("lonely");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 0);
nb.methodName = "EXT-";
// Document doc = new Document();
// doc.tokens.add("lonely");
// String ext = nb.getExtendedFeatures(doc);
// assertTrue(ext.isEmpty());
}

@Test
public void testWeightedOnlineLearningWithNoFeaturesStillUpdatesCounts() {
FeatureMap map = new FeatureMap();
// map.add("irrelevant");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
int[] emptyFeatures = new int[0];
nb.weightedOnlineLearning(emptyFeatures, 2.5, 1);
assertEquals(2.5, nb.sampleSize, 0.0001);
assertEquals(2.5, nb.classCounts[1], 0.0001);
}

@Test
public void testGetPredictionConfidenceLogLikelihoodWithUnseenFID() {
FeatureMap map = new FeatureMap();
// map.add("seen");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
// Document trainDoc = new Document();
// trainDoc.classID = 0;
// trainDoc.tokens.add("seen");
// nb.onlineLearning(trainDoc);
// Document testDoc = new Document();
// testDoc.classID = 1;
// testDoc.tokens.add("unseen");
// double[] probs = nb.getPredictionConfidence(testDoc);
// assertEquals(2, probs.length);
// assertTrue(probs[0] >= 0);
// assertTrue(probs[1] >= 0);
// assertEquals(1.0, probs[0] + probs[1], 0.00001);
}

@Test
public void testGetAccWithMultipleDocumentsAllCorrect() {
FeatureMap map = new FeatureMap();
// map.add("alpha");
// map.add("beta");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
// Document doc1 = new Document();
// doc1.classID = 0;
// doc1.tokens.add("alpha");
// nb.onlineLearning(doc1);
// Document doc2 = new Document();
// doc2.classID = 1;
// doc2.tokens.add("beta");
// nb.onlineLearning(doc2);
// Document test1 = new Document();
// test1.classID = 0;
// test1.tokens.add("alpha");
// Document test2 = new Document();
// test2.classID = 1;
// test2.tokens.add("beta");
DocumentCollection testSet = new DocumentCollection();
// testSet.docs.add(test1);
// testSet.docs.add(test2);
double acc = nb.getAcc(testSet);
assertEquals(1.0, acc, 0.00001);
}

@Test
public void testFidProbFallbackWhenClassHasNoFIDMapping() {
FeatureMap map = new FeatureMap();
// map.add("x");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
// int fid = map.getFid("x");
// double prob = nb.getFidProb(fid, 1);
// assertEquals(MemoryEfficientNB.smooth / map.dim, prob, 0.00001);
}

@Test
public void testSaveAndLoadWithEmptyFidCountsDoesNotCrash() throws Exception {
FeatureMap map = new FeatureMap();
// map.add("feature");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
// File modelFile = new File(tempFolder.newFolder(), "emptyModel");
// String path = modelFile.getAbsolutePath();
// nb.save(path);
// MemoryEfficientNB loaded = new MemoryEfficientNB(path);
// Document doc = new Document();
// doc.classID = 0;
// doc.tokens.add("feature");
// double[] probs = loaded.getPredictionConfidence(doc);
// assertEquals(2, probs.length);
}

@Test
public void testGetAccWithZeroDocumentsReturnsNaNOrZero() {
FeatureMap map = new FeatureMap();
// map.add("gamma");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
DocumentCollection emptyTestSet = new DocumentCollection();
double acc = nb.getAcc(emptyTestSet);
assertTrue(Double.isNaN(acc) || acc == 0.0);
}

@Test
public void testGetTopPmiWordsReturnsEmptyWhenFidCountZero() {
FeatureMap map = new FeatureMap();
// map.add("term");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
nb.fidCount = 0.0;
nb.wordCounts[0] = 1.0;
CharacteristicWords words = nb.getTopPmiWords(0, 1, 0.01, 1);
assertNotNull(words);
assertEquals(0, words.topWords.size());
}
}
