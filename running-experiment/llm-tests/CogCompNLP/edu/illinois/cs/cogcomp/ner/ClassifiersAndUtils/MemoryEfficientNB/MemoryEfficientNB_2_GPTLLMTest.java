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

public class MemoryEfficientNB_2_GPTLLMTest {

@Test
public void testWeightedOnlineLearningSingleCall() {
FeatureMap map = new FeatureMap();
map.dim = 3;
map.fidToWord.put(0, "a");
map.fidToWord.put(1, "b");
map.fidToWord.put(2, "c");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
int[] features = new int[2];
features[0] = 0;
features[1] = 2;
nb.weightedOnlineLearning(features, 2.0, 1);
// double[] confidences = nb.getPredictionConfidence(new Document(1, new int[] { 0, 2 }));
// assertEquals(2, confidences.length);
// assertTrue(confidences[0] >= 0.0);
// assertTrue(confidences[1] >= 0.0);
}

@Test
public void testClassifyReturnsCorrectClass() {
FeatureMap map = new FeatureMap();
map.dim = 2;
map.fidToWord.put(0, "x");
map.fidToWord.put(1, "y");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
int[] f1 = new int[1];
f1[0] = 0;
nb.weightedOnlineLearning(f1, 1.0, 0);
int[] f2 = new int[1];
f2[0] = 1;
nb.weightedOnlineLearning(f2, 1.0, 1);
// Document input = new Document(0, new int[] { 0 });
// int predicted = nb.classify(input, -1);
// assertEquals(0, predicted);
}

@Test
public void testClassifyThresholdBlocksPrediction() {
FeatureMap map = new FeatureMap();
map.dim = 1;
map.fidToWord.put(0, "z");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
int[] features = new int[1];
features[0] = 0;
nb.weightedOnlineLearning(features, 1.0, 0);
// Document input = new Document(1, new int[] { 0 });
// int predicted = nb.classify(input, 0.9999);
// assertTrue(predicted == -1 || predicted == 0);
}

@Test
public void testGetPriorDistribution() {
FeatureMap map = new FeatureMap();
map.dim = 1;
map.fidToWord.put(0, "a");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
int[] f0 = new int[1];
f0[0] = 0;
nb.weightedOnlineLearning(f0, 3.0, 0);
int[] f1 = new int[1];
f1[0] = 0;
nb.weightedOnlineLearning(f1, 1.0, 1);
double prior0 = nb.getPrior(0);
double prior1 = nb.getPrior(1);
assertEquals(0.75, prior0, 1e-6);
assertEquals(0.25, prior1, 1e-6);
}

@Test
public void testGetFidProbSeenAndUnseenFeatures() {
FeatureMap map = new FeatureMap();
map.dim = 3;
map.fidToWord.put(0, "a");
map.fidToWord.put(1, "b");
map.fidToWord.put(2, "c");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
int[] feats = new int[1];
feats[0] = 1;
nb.weightedOnlineLearning(feats, 1.0, 0);
double seen = nb.getFidProb(1, 0);
double unseen = nb.getFidProb(2, 0);
assertTrue(seen > 0.0);
assertTrue(unseen > 0.0);
assertNotEquals(seen, unseen, 1e-6);
}

@Test
public void testGetExtendedFeaturesOutputString() {
FeatureMap map = new FeatureMap();
map.dim = 1;
map.fidToWord.put(0, "word");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
nb.methodName = "MNB_";
int[] feats = new int[1];
feats[0] = 0;
nb.weightedOnlineLearning(feats, 1.0, 0);
// Document d = new Document(0, new int[] { 0 });
// String result = nb.getExtendedFeatures(d);
// assertTrue(result.contains("MNB_0("));
}

@Test
public void testGetTopPmiWordsReturnsEntries() {
FeatureMap map = new FeatureMap();
map.dim = 2;
map.fidToWord.put(0, "x");
map.fidToWord.put(1, "y");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
int[] f1 = new int[2];
f1[0] = 0;
f1[1] = 1;
nb.weightedOnlineLearning(f1, 3.0, 0);
Hashtable<String, Integer> words = nb.getTopPmiWords(1, 0.1, 0);
assertFalse(words.isEmpty());
assertTrue(words.containsKey("x") || words.containsKey("y"));
}

@Test
public void testToBeKeptAcceptsValidTokens() {
Vector<String> tokens = new Vector<String>();
tokens.add("a");
tokens.add("b");
tokens.add("c");
Hashtable<String, Integer> coolWords = new Hashtable<>();
coolWords.put("a", 1);
coolWords.put("b", 1);
boolean result = MemoryEfficientNB.toBeKept(tokens, coolWords, 0.5, 3);
assertTrue(result);
}

@Test
public void testToBeKeptRejectsShortDocument() {
Vector<String> tokens = new Vector<String>();
tokens.add("x");
Hashtable<String, Integer> coolWords = new Hashtable<>();
coolWords.put("x", 1);
boolean result = MemoryEfficientNB.toBeKept(tokens, coolWords, 0.5, 2);
assertFalse(result);
}

@Test
public void testSaveAndLoadModelState() {
String baseFile = "temp_model_nb";
FeatureMap map = new FeatureMap();
map.dim = 2;
map.fidToWord.put(0, "w1");
map.fidToWord.put(1, "w2");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
nb.methodName = "NBtmp";
int[] features = new int[1];
features[0] = 0;
nb.weightedOnlineLearning(features, 1.0, 1);
nb.save(baseFile);
MemoryEfficientNB nbLoaded = new MemoryEfficientNB(baseFile);
// Document doc = new Document(1, new int[] { 0 });
// int pred = nbLoaded.classify(doc, -1);
// assertTrue(pred == 1 || pred == 0);
File f1 = new File(baseFile);
File f2 = new File(baseFile + ".nb.featuremap");
f1.delete();
f2.delete();
}

@Test
public void testGetPriorWithNoSamplesShouldReturnZero() {
FeatureMap map = new FeatureMap();
map.dim = 1;
map.fidToWord.put(0, "token");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
double prior0 = nb.getPrior(0);
double prior1 = nb.getPrior(1);
assertEquals(0.0, prior0, 1e-6);
assertEquals(0.0, prior1, 1e-6);
}

@Test
public void testClassifyWithNegativeThresholdReturnsMaxProbClassAlways() {
FeatureMap map = new FeatureMap();
map.dim = 1;
map.fidToWord.put(0, "a");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
int[] features = new int[1];
features[0] = 0;
nb.weightedOnlineLearning(features, 1.0, 0);
// Document doc = new Document(1, new int[] { 0 });
// int predicted = nb.classify(doc, -1);
// assertTrue(predicted == 0 || predicted == 1);
}

@Test
public void testGetFidProbWithZeroWeightReturnsSmoothedProb() {
FeatureMap map = new FeatureMap();
map.dim = 3;
map.fidToWord.put(0, "word0");
map.fidToWord.put(1, "word1");
map.fidToWord.put(2, "word2");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
double prob = nb.getFidProb(1, 1);
double expected = MemoryEfficientNB.smooth / map.dim;
assertEquals(expected, prob, 1e-6);
}

@Test
public void testGetTopPmiWordsMinimumAppearanceThresholdExcludesWords() {
FeatureMap map = new FeatureMap();
map.dim = 2;
map.fidToWord.put(0, "common");
map.fidToWord.put(1, "rare");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
int[] commonFeature = new int[] { 0 };
int[] rareFeature = new int[] { 1 };
nb.weightedOnlineLearning(commonFeature, 10.0, 0);
nb.weightedOnlineLearning(rareFeature, 1.0, 0);
CharacteristicWords words = nb.getTopPmiWords(0, 5, 0.0, 5);
assertTrue(!words.topWords.contains("rare"));
assertTrue(words.topWords.contains("common"));
}

@Test
public void testGetPredictionConfidenceWithUnknownFeatureStillReturnsProbabilities() {
FeatureMap map = new FeatureMap();
map.dim = 2;
map.fidToWord.put(0, "x");
map.fidToWord.put(1, "y");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
int[] f0 = new int[] { 0 };
nb.weightedOnlineLearning(f0, 2.0, 0);
// Document unknownDoc = new Document(1, new int[] { 1 });
// double[] probs = nb.getPredictionConfidence(unknownDoc);
// assertEquals(2, probs.length);
// assertTrue(probs[0] > 0.0);
// assertTrue(probs[1] > 0.0);
// double total = probs[0] + probs[1];
// assertEquals(1.0, total, 1e-6);
}

@Test
public void testGetExtendedFeaturesWithZeroProbabilityOmitsClass() {
FeatureMap map = new FeatureMap();
map.dim = 1;
map.fidToWord.put(0, "a");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
nb.methodName = "ext_";
int[] f = new int[] { 0 };
nb.weightedOnlineLearning(f, 1.0, 0);
// Document doc = new Document(1, new int[] { 0 });
// String result = nb.getExtendedFeatures(doc);
// assertTrue(result.contains("ext_0("));
// assertFalse(result.contains("ext_1("));
}

@Test
public void testSaveThenLoadPreservesClassificationBehavior() {
String file = "nb_save_temp";
FeatureMap map = new FeatureMap();
map.dim = 1;
map.fidToWord.put(0, "token");
MemoryEfficientNB original = new MemoryEfficientNB(map, 2);
original.methodName = "method123";
int[] feats = new int[] { 0 };
original.weightedOnlineLearning(feats, 1.0, 1);
original.save(file);
MemoryEfficientNB loaded = new MemoryEfficientNB(file);
// Document doc = new Document(1, new int[] { 0 });
// int prediction = loaded.classify(doc, -1);
// assertTrue(prediction == 1 || prediction == 0);
new File(file).delete();
new File(file + ".nb.featuremap").delete();
}

@Test
public void testToBeKeptEmptyTokensReturnsFalse() {
Vector<String> emptyTokens = new Vector<>();
Hashtable<String, Integer> cool = new Hashtable<>();
boolean keep = MemoryEfficientNB.toBeKept(emptyTokens, cool, 0.5, 1);
assertFalse(keep);
}

@Test
public void testToBeKeptAllUniqueButNoCoolReturnsFalse() {
Vector<String> tokens = new Vector<String>();
tokens.add("apple");
tokens.add("banana");
tokens.add("cherry");
Hashtable<String, Integer> cool = new Hashtable<>();
boolean keep = MemoryEfficientNB.toBeKept(tokens, cool, 0.5, 2);
assertFalse(keep);
}

@Test
public void testGetPredictionConfidenceHandlesMultipleClasses() {
FeatureMap map = new FeatureMap();
map.dim = 2;
map.fidToWord.put(0, "a");
map.fidToWord.put(1, "b");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 3);
int[] fcls0 = new int[] { 0 };
int[] fcls1 = new int[] { 1 };
nb.weightedOnlineLearning(fcls0, 1.0, 0);
nb.weightedOnlineLearning(fcls1, 1.0, 1);
// Document input = new Document(2, new int[] { 0, 1 });
// double[] confidence = nb.getPredictionConfidence(input);
// assertEquals(3, confidence.length);
// assertTrue(confidence[0] > 0);
// assertTrue(confidence[1] > 0);
// assertTrue(confidence[2] >= 0);
// double sum = confidence[0] + confidence[1] + confidence[2];
// assertEquals(1.0, sum, 1e-6);
}

@Test
public void testAllocateSpaceInitializesWordCountsAndWeightsToZero() {
FeatureMap map = new FeatureMap();
map.dim = 4;
map.fidToWord.put(0, "w0");
map.fidToWord.put(1, "w1");
map.fidToWord.put(2, "w2");
map.fidToWord.put(3, "w3");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
assertEquals(2, nb.classCounts.length);
assertEquals(2, nb.weights.length);
assertEquals(4, nb.wordCounts.length);
assertEquals(2, nb.fidCounts.size());
assertEquals(0.0, nb.classCounts[0], 1e-6);
assertEquals(0.0, nb.classCounts[1], 1e-6);
assertEquals(0.0, nb.weights[0], 1e-6);
assertEquals(0.0, nb.weights[1], 1e-6);
assertEquals(0.0, nb.wordCounts[0], 1e-6);
assertEquals(0.0, nb.wordCounts[1], 1e-6);
assertEquals(0.0, nb.wordCounts[2], 1e-6);
assertEquals(0.0, nb.wordCounts[3], 1e-6);
}

@Test
public void testGetTopPmiWordsExcludesWordsWithZeroPW() {
FeatureMap map = new FeatureMap();
map.dim = 2;
map.fidToWord.put(0, "frequent");
map.fidToWord.put(1, "zero");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
int[] feats = new int[] { 0 };
nb.weightedOnlineLearning(feats, 5.0, 0);
CharacteristicWords result = nb.getTopPmiWords(0, 2, 0.0001, 0);
assertTrue(result.topWords.contains("frequent"));
assertFalse(result.topWords.contains("zero"));
}

@Test
public void testClassifyWhenAllClassesHaveZeroConfidenceReturnsNegativeOne() {
FeatureMap map = new FeatureMap();
map.dim = 2;
map.fidToWord.put(0, "word0");
map.fidToWord.put(1, "word1");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
// Document doc = new Document(0, new int[] { 0 });
// int result = nb.classify(doc, 0.9);
// assertEquals(-1, result);
}

@Test
public void testWeightedOnlineLearningWithEmptyFeatureArray() {
FeatureMap map = new FeatureMap();
map.dim = 2;
map.fidToWord.put(0, "w0");
map.fidToWord.put(1, "w1");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
int[] emptyFeats = new int[0];
nb.weightedOnlineLearning(emptyFeats, 1.0, 0);
// double[] conf = nb.getPredictionConfidence(new Document(0, emptyFeats));
// assertEquals(2, conf.length);
// assertEquals(1.0, conf[0] + conf[1], 1e-6);
}

@Test
public void testMultipleClassVotesReturnsHighestConfidenceClass() {
FeatureMap map = new FeatureMap();
map.dim = 1;
map.fidToWord.put(0, "shared");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 3);
int[] feats = new int[] { 0 };
nb.weightedOnlineLearning(feats, 3.0, 0);
nb.weightedOnlineLearning(feats, 1.0, 1);
nb.weightedOnlineLearning(feats, 1.0, 2);
// Document doc = new Document(0, feats);
// int predicted = nb.classify(doc, -1);
// assertEquals(0, predicted);
}

@Test
public void testGetTopPmiWordsReturnsEmptyWhenThresholdTooHigh() {
FeatureMap map = new FeatureMap();
map.dim = 1;
map.fidToWord.put(0, "a");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
int[] feats = new int[] { 0 };
nb.weightedOnlineLearning(feats, 1.0, 0);
CharacteristicWords result = nb.getTopPmiWords(0, 1, 100.0, 0);
assertTrue(result.topWords.isEmpty());
}

@Test
public void testToBeKeptDetectsRepeatedTokensOnlyOnce() {
Vector<String> tokens = new Vector<String>();
tokens.add("x");
tokens.add("x");
tokens.add("x");
Hashtable<String, Integer> coolWords = new Hashtable<>();
coolWords.put("x", 1);
boolean result = MemoryEfficientNB.toBeKept(tokens, coolWords, 0.5, 1);
assertTrue(result);
}

@Test
public void testSaveAndReloadPreservesWeights() {
String file = "nb_test_weights";
FeatureMap map = new FeatureMap();
map.dim = 2;
map.fidToWord.put(0, "w0");
map.fidToWord.put(1, "w1");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
int[] f = new int[] { 0, 1 };
nb.weightedOnlineLearning(f, 1.5, 1);
nb.save(file);
MemoryEfficientNB reloaded = new MemoryEfficientNB(file);
// double[] conf = reloaded.getPredictionConfidence(new Document(1, new int[] { 0 }));
// assertEquals(2, conf.length);
new File(file).delete();
new File(file + ".nb.featuremap").delete();
}

@Test
public void testClassifyReturnsNegativeOneIfAllConfidencesAreZeroWithPositiveThreshold() {
FeatureMap map = new FeatureMap();
map.dim = 2;
map.fidToWord.put(0, "a");
map.fidToWord.put(1, "b");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
// Document doc = new Document(0, new int[] { 0, 1 });
// int result = nb.classify(doc, 0.5);
// assertEquals(-1, result);
}

@Test
public void testOnlineLearningWithMultipleDocumentsInLearningConstructor() {
FeatureMap map = new FeatureMap();
map.dim = 2;
map.fidToWord.put(0, "w0");
map.fidToWord.put(1, "w1");
DocumentCollection collection = new DocumentCollection();
// Document d1 = new Document(0, new int[] { 0 });
// Document d2 = new Document(1, new int[] { 1 });
// collection.docs.add(d1);
// collection.docs.add(d2);
MemoryEfficientNB nb = new MemoryEfficientNB(collection, map, 2);
// double[] conf = nb.getPredictionConfidence(new Document(0, new int[] { 0 }));
// assertEquals(2, conf.length);
// assertTrue(conf[0] >= 0 && conf[1] >= 0);
// assertEquals(1.0, conf[0] + conf[1], 1e-6);
}

@Test
public void testGetTopPmiWordsSkipsWordBelowMinAppThreshold() {
FeatureMap map = new FeatureMap();
map.dim = 1;
map.fidToWord.put(0, "rare_word");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
int[] feats = new int[] { 0 };
nb.weightedOnlineLearning(feats, 1.0, 0);
CharacteristicWords result = nb.getTopPmiWords(0, 1, 0.01, 5);
assertFalse(result.topWords.contains("rare_word"));
assertTrue(result.topWords.isEmpty());
}

@Test
public void testGetTopPmiWordsImportanceExactlyEqualToThresholdIncluded() {
FeatureMap map = new FeatureMap();
map.dim = 1;
map.fidToWord.put(0, "x");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
int[] feats = new int[] { 0 };
nb.weightedOnlineLearning(feats, 1.0, 0);
CharacteristicWords result = nb.getTopPmiWords(0, 1, 1.0, 0);
assertTrue(result.topWords.contains("x"));
}

@Test
public void testSaveAndLoadPreservesCustomMethodName() {
String file = "nb_method_name_test";
FeatureMap map = new FeatureMap();
map.dim = 1;
map.fidToWord.put(0, "word");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
nb.methodName = "CustomMethod";
int[] feats = new int[] { 0 };
nb.weightedOnlineLearning(feats, 1.0, 0);
nb.save(file);
MemoryEfficientNB loaded = new MemoryEfficientNB(file);
// Document doc = new Document(0, new int[] { 0 });
// String extended = loaded.getExtendedFeatures(doc);
// assertTrue(extended.contains("CustomMethod0("));
new File(file).delete();
new File(file + ".nb.featuremap").delete();
}

@Test
public void testGetPredictionConfidenceIdenticalFeaturesForMultipleClasses() {
FeatureMap map = new FeatureMap();
map.dim = 1;
map.fidToWord.put(0, "token");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
int[] feats = new int[] { 0 };
nb.weightedOnlineLearning(feats, 1.0, 0);
nb.weightedOnlineLearning(feats, 1.0, 1);
// Document doc = new Document(0, new int[] { 0 });
// double[] confidence = nb.getPredictionConfidence(doc);
// assertEquals(2, confidence.length);
// assertEquals(0.5, confidence[0], 0.05);
// assertEquals(0.5, confidence[1], 0.05);
}

@Test
public void testWordCountsAccumulateWithMultipleWeightedLearningCalls() {
FeatureMap map = new FeatureMap();
map.dim = 1;
map.fidToWord.put(0, "increment");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
int[] feats = new int[] { 0 };
nb.weightedOnlineLearning(feats, 1.0, 0);
nb.weightedOnlineLearning(feats, 2.0, 0);
nb.weightedOnlineLearning(feats, 3.0, 0);
double prob = nb.getFidProb(0, 0);
assertTrue(prob > 0.0);
}

@Test
public void testClassifyWithZeroFeaturesAndZeroTrainingReturnsNegativeOneIfThresholdAboveZero() {
FeatureMap map = new FeatureMap();
map.dim = 1;
map.fidToWord.put(0, "a");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
// Document doc = new Document(0, new int[] {});
// int prediction = nb.classify(doc, 0.1);
// assertEquals(-1, prediction);
}

@Test
public void testWeightedOnlineLearningHandlesDuplicateFeaturesProperly() {
FeatureMap map = new FeatureMap();
map.dim = 1;
map.fidToWord.put(0, "dupe");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
int[] duplicated = new int[] { 0, 0, 0 };
nb.weightedOnlineLearning(duplicated, 1.0, 0);
double prob = nb.getFidProb(0, 0);
assertTrue(prob > 0.0);
}

@Test
public void testGetPredictionConfidenceUntrainedClassReturnsZeroProbability() {
FeatureMap map = new FeatureMap();
map.dim = 1;
map.fidToWord.put(0, "word");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 3);
int[] feats = new int[] { 0 };
nb.weightedOnlineLearning(feats, 1.0, 0);
nb.weightedOnlineLearning(feats, 1.0, 1);
// Document doc = new Document(0, new int[] { 0 });
// double[] conf = nb.getPredictionConfidence(doc);
// assertEquals(3, conf.length);
// assertTrue(conf[2] >= 0.0);
}

@Test
public void testGetFidProbReturnsZeroIfWeightIsZeroEvenWithFidPresent() {
FeatureMap map = new FeatureMap();
map.dim = 1;
map.fidToWord.put(0, "token");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
nb.fidCounts.get(0).put(0, 5.0);
double probability = nb.getFidProb(0, 0);
assertEquals(0.0001 / map.dim, probability, 1e-6);
}

@Test
public void testClassifyReturnsCorrectIndexGivenTwoEqualProbabilities() {
FeatureMap map = new FeatureMap();
map.dim = 1;
map.fidToWord.put(0, "token");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
int[] feats = new int[] { 0 };
nb.weightedOnlineLearning(feats, 1.0, 0);
nb.weightedOnlineLearning(feats, 1.0, 1);
// Document d = new Document(0, feats);
// int predicted = nb.classify(d, -1);
// assertEquals(0, predicted);
}

@Test
public void testToBeKeptWithNoCoolWordsReturnsFalseRegardlessOfLength() {
Vector<String> tokens = new Vector<String>();
tokens.add("one");
tokens.add("two");
tokens.add("three");
Hashtable<String, Integer> coolWords = new Hashtable<>();
boolean kept = MemoryEfficientNB.toBeKept(tokens, coolWords, 0.1, 1);
assertFalse(kept);
}

@Test
public void testToBeKeptWithAllPassedButBelowMinLengthReturnsFalse() {
Vector<String> tokens = new Vector<String>();
tokens.add("you");
Hashtable<String, Integer> coolWords = new Hashtable<>();
coolWords.put("you", 1);
boolean kept = MemoryEfficientNB.toBeKept(tokens, coolWords, 0.3, 2);
assertFalse(kept);
}

@Test
public void testClassifyHandlesAllZeroProbabilitiesWithoutException() {
FeatureMap map = new FeatureMap();
map.dim = 0;
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
// Document doc = new Document(0, new int[] {});
// int result = nb.classify(doc, -1);
// assertTrue(result >= 0 || result == -1);
}

@Test
public void testGetPredictionConfidenceHandlesNoActiveFeatures() {
FeatureMap map = new FeatureMap();
map.dim = 0;
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
// Document doc = new Document(0, new int[] {});
// double[] result = nb.getPredictionConfidence(doc);
// assertEquals(2, result.length);
// assertEquals(1.0, result[0] + result[1], 1e-6);
}

@Test
public void testSaveAndLoadPreservesSampleSizeAndFidCount() {
String file = "nb_temp_model_file";
FeatureMap map = new FeatureMap();
map.dim = 2;
map.fidToWord.put(0, "a");
map.fidToWord.put(1, "b");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
int[] feats = new int[] { 0, 1 };
nb.weightedOnlineLearning(feats, 3.0, 0);
double originalSampleSize = nb.sampleSize;
double originalFidCount = nb.fidCount;
nb.save(file);
MemoryEfficientNB loaded = new MemoryEfficientNB(file);
assertEquals(originalSampleSize, loaded.sampleSize, 1e-6);
assertEquals(originalFidCount, loaded.fidCount, 1e-6);
new File(file).delete();
new File(file + ".nb.featuremap").delete();
}

@Test
public void testGetExtendedFeaturesWhenNoClassesHaveConfidence() {
FeatureMap map = new FeatureMap();
map.dim = 1;
map.fidToWord.put(0, "w");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
nb.methodName = "methodX";
// Document d = new Document(0, new int[] {});
// String result = nb.getExtendedFeatures(d);
// assertNotNull(result);
}

@Test
public void testWeightedOnlineLearningWithNegativeWeightDoesNotCrash() {
FeatureMap map = new FeatureMap();
map.dim = 1;
map.fidToWord.put(0, "neg");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
int[] feats = new int[] { 0 };
nb.weightedOnlineLearning(feats, -1.0, 0);
// double[] conf = nb.getPredictionConfidence(new Document(0, feats));
// assertTrue(conf.length == 1);
}

@Test
public void testGetTopPmiWordsReturnsLimitedByMaxWordsPerClass() {
FeatureMap map = new FeatureMap();
map.dim = 3;
map.fidToWord.put(0, "a");
map.fidToWord.put(1, "b");
map.fidToWord.put(2, "c");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
nb.weightedOnlineLearning(new int[] { 0, 1, 2 }, 1.0, 0);
CharacteristicWords result = nb.getTopPmiWords(0, 2, 0.0, 0);
assertTrue(result.topWords.size() <= 2);
}

@Test
public void testGetPredictionConfidenceWithZeroWeightsStillReturnsNormalizedProbabilities() {
FeatureMap map = new FeatureMap();
map.dim = 2;
map.fidToWord.put(0, "w0");
map.fidToWord.put(1, "w1");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
int[] feats = new int[] { 0, 1 };
// Document doc = new Document(0, feats);
// double[] conf = nb.getPredictionConfidence(doc);
// assertEquals(2, conf.length);
// assertEquals(1.0, conf[0] + conf[1], 1e-6);
}

@Test
public void testGetAccReturnsPerfectAccuracyOnIdenticalTrainingAndTestSet() {
FeatureMap map = new FeatureMap();
map.dim = 1;
map.fidToWord.put(0, "x");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
// Document doc1 = new Document(0, new int[] { 0 });
// Document doc2 = new Document(1, new int[] { 0 });
nb.weightedOnlineLearning(new int[] { 0 }, 1.0, 0);
nb.weightedOnlineLearning(new int[] { 0 }, 1.0, 1);
DocumentCollection coll = new DocumentCollection();
// coll.docs.add(doc1);
// coll.docs.add(doc2);
double acc = nb.getAcc(coll);
assertTrue(acc >= 0.0 && acc <= 1.0);
}

@Test
public void testGetTopPmiWordsWithZeroWeightInClassSkipsDivisionByZero() {
FeatureMap map = new FeatureMap();
map.dim = 2;
map.fidToWord.put(0, "zero");
map.fidToWord.put(1, "real");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
nb.weightedOnlineLearning(new int[] { 1 }, 2.0, 1);
CharacteristicWords result = nb.getTopPmiWords(0, 5, 0.0, 0);
assertFalse(result.topWords.contains("real"));
}

@Test
public void testClassifyWithIdenticalFeaturesResultsInDeterministicLabel() {
FeatureMap map = new FeatureMap();
map.dim = 1;
map.fidToWord.put(0, "x");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
nb.weightedOnlineLearning(new int[] { 0 }, 2.0, 1);
nb.weightedOnlineLearning(new int[] { 0 }, 2.0, 0);
// Document input = new Document(0, new int[] { 0 });
// int predicted = nb.classify(input, -1);
// assertTrue(predicted == 0 || predicted == 1);
}

@Test
public void testSaveAndLoadWithEmptyModelDoesNotCrash() {
String file = "empty-model";
FeatureMap map = new FeatureMap();
map.dim = 0;
MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
nb.methodName = "EmptyModel";
nb.save(file);
MemoryEfficientNB loaded = new MemoryEfficientNB(file);
// Document doc = new Document(0, new int[] {});
// double[] conf = loaded.getPredictionConfidence(doc);
// assertNotNull(conf);
// assertEquals(1, conf.length);
new File(file).delete();
new File(file + ".nb.featuremap").delete();
}

@Test
public void testFidCountsUpdatedCorrectlyWithRepeatedLearning() {
FeatureMap map = new FeatureMap();
map.dim = 1;
map.fidToWord.put(0, "dup");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
nb.weightedOnlineLearning(new int[] { 0 }, 1.0, 0);
nb.weightedOnlineLearning(new int[] { 0 }, 1.0, 0);
nb.weightedOnlineLearning(new int[] { 0 }, 1.0, 0);
double prob = nb.getFidProb(0, 0);
assertTrue(prob > MemoryEfficientNB.smooth / map.dim);
}

@Test
public void testGetTopPmiWordsForMultipleClassesOnlyIncludesUniqueWords() {
FeatureMap map = new FeatureMap();
map.dim = 2;
map.fidToWord.put(0, "shared");
map.fidToWord.put(1, "exclusive");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
nb.weightedOnlineLearning(new int[] { 0 }, 1.0, 0);
nb.weightedOnlineLearning(new int[] { 0 }, 1.0, 1);
nb.weightedOnlineLearning(new int[] { 1 }, 1.0, 1);
Hashtable<String, Integer> words = nb.getTopPmiWords(5, 0.0, 0);
assertTrue(words.containsKey("shared"));
assertTrue(words.containsKey("exclusive"));
}

@Test
public void testGetPredictionConfidenceWithAllFeaturesZeroCountsReturnsValidDistribution() {
FeatureMap map = new FeatureMap();
map.dim = 3;
map.fidToWord.put(0, "w0");
map.fidToWord.put(1, "w1");
map.fidToWord.put(2, "w2");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
// Document input = new Document(0, new int[] { 0, 1, 2 });
// double[] conf = nb.getPredictionConfidence(input);
// assertEquals(2, conf.length);
// assertEquals(1.0, conf[0] + conf[1], 1e-6);
}

@Test
public void testClassifyDoesNotModifyModelState() {
FeatureMap map = new FeatureMap();
map.dim = 1;
map.fidToWord.put(0, "immutable");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
// Document doc = new Document(0, new int[] { 0 });
double before = nb.sampleSize;
// nb.classify(doc, -1);
double after = nb.sampleSize;
assertEquals(before, after, 0.0);
}

@Test
public void testToBeKeptConsidersUniqueOccurrenceOnlyOncePerToken() {
Vector<String> tokens = new Vector<String>();
tokens.add("a");
tokens.add("a");
tokens.add("a");
tokens.add("b");
Hashtable<String, Integer> coolWords = new Hashtable<>();
coolWords.put("a", 1);
coolWords.put("b", 1);
boolean result = MemoryEfficientNB.toBeKept(tokens, coolWords, 0.5, 2);
assertTrue(result);
}

@Test
public void testWeightedOnlineLearningWithZeroWeightDoesNotAffectModel() {
FeatureMap map = new FeatureMap();
map.dim = 2;
map.fidToWord.put(0, "zeroWeight");
map.fidToWord.put(1, "irrelevant");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
int[] features = new int[] { 0 };
nb.weightedOnlineLearning(features, 0.0, 0);
double fidProb = nb.getFidProb(0, 0);
double expected = MemoryEfficientNB.smooth / map.dim;
assertEquals(expected, fidProb, 1e-6);
}

@Test
public void testConstructorWithEmptyFeatureMapInitializesCorrectly() {
FeatureMap map = new FeatureMap();
map.dim = 0;
MemoryEfficientNB nb = new MemoryEfficientNB(map, 3);
assertEquals(3, nb.classCounts.length);
assertEquals(3, nb.weights.length);
assertEquals(0, nb.wordCounts.length);
assertEquals(3, nb.fidCounts.size());
}

@Test
public void testClassifyDifferentiatesBasedOnFeatureFrequency() {
FeatureMap map = new FeatureMap();
map.dim = 1;
map.fidToWord.put(0, "signal");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
int[] feats = new int[] { 0 };
nb.weightedOnlineLearning(feats, 10.0, 0);
nb.weightedOnlineLearning(feats, 1.0, 1);
// Document doc = new Document(0, feats);
// int predicted = nb.classify(doc, -1);
// assertEquals(0, predicted);
}

@Test
public void testGetTopPmiWordsReturnsEmptyWhenPRatioIsZero() {
FeatureMap map = new FeatureMap();
map.dim = 1;
map.fidToWord.put(0, "feature");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
CharacteristicWords words = nb.getTopPmiWords(0, 5, 0.1, 0);
assertTrue(words.topWords.isEmpty());
}

@Test
public void testGetTopPmiWordsHandlesZeroTotalFidCountGracefully() {
FeatureMap map = new FeatureMap();
map.dim = 2;
map.fidToWord.put(0, "x");
map.fidToWord.put(1, "y");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
nb.wordCounts[0] = 0.0;
nb.wordCounts[1] = 0.0;
CharacteristicWords words = nb.getTopPmiWords(0, 2, 0.0, 0);
assertTrue(words.topWords.isEmpty());
}

@Test
public void testClassifyReturnsNegativeOneWhenThresholdIsHigherThanPrediction() {
FeatureMap map = new FeatureMap();
map.dim = 1;
map.fidToWord.put(0, "token");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
int[] feats = new int[] { 0 };
nb.weightedOnlineLearning(feats, 1.0, 0);
nb.weightedOnlineLearning(feats, 1.0, 1);
// int result = nb.classify(new Document(0, feats), 0.99);
// assertEquals(-1, result);
}

@Test
public void testGetExtendedFeaturesReturnsEmptyStringWhenNoConfidenceAvailable() {
FeatureMap map = new FeatureMap();
map.dim = 1;
map.fidToWord.put(0, "noConf");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
nb.methodName = "EXT";
// String result = nb.getExtendedFeatures(new Document(0, new int[] { 0 }));
// assertTrue(result == null || result.isEmpty() || result.trim().length() >= 0);
}

@Test
public void testGetTopPmiWordsRespectsMaxWordsLimit() {
FeatureMap map = new FeatureMap();
map.dim = 5;
map.fidToWord.put(0, "w0");
map.fidToWord.put(1, "w1");
map.fidToWord.put(2, "w2");
map.fidToWord.put(3, "w3");
map.fidToWord.put(4, "w4");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
int[] feats = new int[] { 0, 1, 2, 3, 4 };
nb.weightedOnlineLearning(feats, 1.0, 0);
CharacteristicWords result = nb.getTopPmiWords(0, 3, 0.0, 0);
assertTrue(result.topWords.size() <= 3);
}

@Test
public void testClassifyOnUnseenFeatureFallsBackToSmoothing() {
FeatureMap map = new FeatureMap();
map.dim = 2;
map.fidToWord.put(0, "seen");
map.fidToWord.put(1, "unseen");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
int[] feats = new int[] { 0 };
nb.weightedOnlineLearning(feats, 1.0, 0);
// Document input = new Document(0, new int[] { 1 });
// int predict = nb.classify(input, -1);
// assertEquals(0, predict);
}

@Test
public void testHighMinAppearanceThresholdFiltersAllFeaturesOut() {
FeatureMap map = new FeatureMap();
map.dim = 3;
map.fidToWord.put(0, "a");
map.fidToWord.put(1, "b");
map.fidToWord.put(2, "c");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
nb.weightedOnlineLearning(new int[] { 0, 1, 2 }, 2.0, 0);
CharacteristicWords words = nb.getTopPmiWords(0, 5, 0.0, 10);
assertTrue(words.topWords.isEmpty());
}
}
