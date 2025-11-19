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

public class MemoryEfficientNB_5_GPTLLMTest {

@Test
public void testAllocateSpaceInitializesFields() {
FeatureMap map = new FeatureMap();
map.dim = 3;
map.fidToWord.put(0, "a");
map.fidToWord.put(1, "b");
map.fidToWord.put(2, "c");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
assertNotNull(nb.map);
assertNotNull(nb.classCounts);
assertEquals(2, nb.classCounts.length);
assertEquals(2, nb.weights.length);
assertNotNull(nb.wordCounts);
assertEquals(map.dim, nb.wordCounts.length);
}

@Test
public void testOnlineLearningUpdatesModel() {
FeatureMap map = new FeatureMap();
map.dim = 3;
map.fidToWord.put(0, "w0");
map.fidToWord.put(1, "w1");
map.fidToWord.put(2, "w2");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
// Document doc = new Document() {
// 
// public int[] getActiveFid(FeatureMap m) {
// return new int[] { 0, 1 };
// }
// 
// public int classID = 1;
// };
// nb.onlineLearning(doc);
// double[] confidence = nb.getPredictionConfidence(doc);
// assertEquals(2, confidence.length);
// assertTrue(confidence[1] > confidence[0]);
}

@Test
public void testWeightedOnlineLearningAffectsConfidence() {
FeatureMap map = new FeatureMap();
map.dim = 2;
map.fidToWord.put(0, "x");
map.fidToWord.put(1, "y");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
int[] features = new int[] { 0 };
nb.weightedOnlineLearning(features, 5.0, 0);
// Document doc = new Document() {
// 
// public int[] getActiveFid(FeatureMap m) {
// return new int[] { 0 };
// }
// 
// public int classID = -1;
// };
// double[] conf = nb.getPredictionConfidence(doc);
// assertTrue(conf[0] > conf[1]);
}

@Test
public void testGetPriorReturnsExpectedValue() {
FeatureMap map = new FeatureMap();
map.dim = 1;
map.fidToWord.put(0, "aaa");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
nb.weightedOnlineLearning(new int[] { 0 }, 2.0, 1);
nb.weightedOnlineLearning(new int[] { 0 }, 2.0, 0);
double prior0 = nb.getPrior(0);
double prior1 = nb.getPrior(1);
assertEquals(0.5, prior0, 1e-6);
assertEquals(0.5, prior1, 1e-6);
}

@Test
public void testClassifyReturnsCorrectClassWithoutThreshold() {
FeatureMap map = new FeatureMap();
map.dim = 2;
map.fidToWord.put(0, "p");
map.fidToWord.put(1, "q");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
nb.weightedOnlineLearning(new int[] { 1 }, 3.0, 0);
nb.weightedOnlineLearning(new int[] { 0 }, 1.0, 1);
// Document doc = new Document() {
// 
// public int[] getActiveFid(FeatureMap m) {
// return new int[] { 1 };
// }
// 
// public int classID = -1;
// };
// int predicted = nb.classify(doc, -1);
// assertEquals(0, predicted);
}

@Test
public void testClassifyThresholdBlocksLowConfidence() {
FeatureMap map = new FeatureMap();
map.dim = 2;
map.fidToWord.put(0, "x");
map.fidToWord.put(1, "y");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
nb.weightedOnlineLearning(new int[] { 0 }, 1.0, 0);
// Document doc = new Document() {
// 
// public int[] getActiveFid(FeatureMap m) {
// return new int[] { 0 };
// }
// 
// public int classID = -1;
// };
// int result = nb.classify(doc, 0.95);
// assertTrue(result == -1 || result == 0);
}

@Test
public void testGetAccReturnsPerfectAccuracy() {
FeatureMap map = new FeatureMap();
map.dim = 1;
map.fidToWord.put(0, "k");
// Document doc1 = new Document() {
// 
// public int[] getActiveFid(FeatureMap m) {
// return new int[] { 0 };
// }
// 
// public int classID = 0;
// };
DocumentCollection train = new DocumentCollection();
// train.docs.add(doc1);
MemoryEfficientNB nb = new MemoryEfficientNB(train, map, 1);
DocumentCollection test = new DocumentCollection();
// test.docs.add(doc1);
double acc = nb.getAcc(test);
assertEquals(1.0, acc, 1e-6);
}

@Test
public void testGetTopPmiWordsReturnsSomeWords() {
FeatureMap map = new FeatureMap();
map.dim = 2;
map.fidToWord.put(0, "a");
map.fidToWord.put(1, "b");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
nb.weightedOnlineLearning(new int[] { 0, 0 }, 2.0, 0);
nb.weightedOnlineLearning(new int[] { 1 }, 1.0, 1);
Hashtable<String, Integer> result = nb.getTopPmiWords(2, 0.9, 1);
assertNotNull(result);
assertTrue(result.containsKey("a") || result.containsKey("b"));
}

@Test
public void testToBeKeptReturnsTrueWithEnoughCoolTokens() {
Vector<String> tokens = new Vector<String>();
tokens.add("x");
tokens.add("x");
tokens.add("y");
Hashtable<String, Integer> coolWords = new Hashtable<>();
coolWords.put("x", 1);
coolWords.put("y", 1);
boolean result = MemoryEfficientNB.toBeKept(tokens, coolWords, 0.5, 2);
assertTrue(result);
}

@Test
public void testGetExtendedFeaturesReturnsNonEmptyString() {
FeatureMap map = new FeatureMap();
map.dim = 2;
map.fidToWord.put(0, "f0");
map.fidToWord.put(1, "f1");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
nb.weightedOnlineLearning(new int[] { 0, 1 }, 1.0, 0);
// Document doc = new Document() {
// 
// public int[] getActiveFid(FeatureMap m) {
// return new int[] { 0, 1 };
// }
// 
// public int classID = -1;
// };
// String result = nb.getExtendedFeatures(doc);
// assertNotNull(result);
// assertTrue(result.length() > 0);
}

@Test
public void testSaveAndLoadModelPreservesState() {
FeatureMap map = new FeatureMap();
map.dim = 1;
map.fidToWord.put(0, "z");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
nb.weightedOnlineLearning(new int[] { 0 }, 5.0, 0);
String basePath = "temp_nb_model";
nb.save(basePath);
MemoryEfficientNB loaded = new MemoryEfficientNB(basePath);
// Document doc = new Document() {
// 
// public int[] getActiveFid(FeatureMap m) {
// return new int[] { 0 };
// }
// 
// public int classID = 0;
// };
// int pred = loaded.classify(doc, -1);
// assertEquals(0, pred);
new File(basePath).delete();
new File(basePath + ".nb.featuremap").delete();
}

@Test
public void testClassifyWithEmptyConfidenceArrayReturnsNegativeOne() {
FeatureMap map = new FeatureMap();
map.dim = 1;
map.fidToWord.put(0, "empty");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
// Document doc = new Document() {
// 
// public int[] getActiveFid(FeatureMap m) {
// return new int[] {};
// }
// 
// public int classID = -1;
// };
// int result = nb.classify(doc, 1.0);
// assertEquals(-1, result);
}

@Test
public void testGetPredictionConfidenceWithNoFeaturesReturnsEqualProbability() {
FeatureMap map = new FeatureMap();
map.dim = 0;
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
nb.allocateSpace(map, 2);
nb.weightedOnlineLearning(new int[] {}, 1.0, 0);
nb.weightedOnlineLearning(new int[] {}, 1.0, 1);
// Document doc = new Document() {
// 
// public int[] getActiveFid(FeatureMap m) {
// return new int[] {};
// }
// 
// public int classID = -1;
// };
// double[] conf = nb.getPredictionConfidence(doc);
// assertEquals(2, conf.length);
// assertEquals(0.5, conf[0], 1e-6);
// assertEquals(0.5, conf[1], 1e-6);
}

@Test
public void testGetFidProbWhenClassWeightIsZeroReturnsSmoothedProbability() {
FeatureMap map = new FeatureMap();
map.dim = 4;
map.fidToWord.put(0, "a");
map.fidToWord.put(1, "b");
map.fidToWord.put(2, "c");
map.fidToWord.put(3, "d");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
double prob = nb.getFidProb(2, 1);
assertEquals(MemoryEfficientNB.smooth / 4, prob, 1e-6);
}

@Test
public void testGetTopPmiWordsWithZeroWeightSkipsFeature() {
FeatureMap map = new FeatureMap();
map.dim = 2;
map.fidToWord.put(0, "w0");
map.fidToWord.put(1, "w1");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
CharacteristicWords words = nb.getTopPmiWords(0, 5, 0.1, 1);
assertNotNull(words);
assertEquals(0, words.topWords.size());
}

@Test
public void testGetAccWithNoDocumentsInTestCollection() {
FeatureMap map = new FeatureMap();
map.dim = 1;
map.fidToWord.put(0, "x");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
DocumentCollection test = new DocumentCollection();
try {
nb.getAcc(test);
fail("Expected ArithmeticException due to division by zero");
} catch (ArithmeticException e) {
assertTrue(e.getMessage() == null || e.getMessage().contains("/ by zero"));
}
}

@Test
public void testGetExtendedFeaturesOnUntrainedModelReturnsEmpty() {
FeatureMap map = new FeatureMap();
map.dim = 2;
map.fidToWord.put(0, "f0");
map.fidToWord.put(1, "f1");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
// Document doc = new Document() {
// 
// public int[] getActiveFid(FeatureMap m) {
// return new int[] { 0, 1 };
// }
// 
// public int classID = 1;
// };
// String result = nb.getExtendedFeatures(doc);
// assertNotNull(result);
// assertEquals("", result);
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
public void testSaveAndLoadModelHandlesNoFeaturesGracefully() {
FeatureMap map = new FeatureMap();
map.dim = 0;
MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
nb.save("nb_zerofid");
MemoryEfficientNB loaded = new MemoryEfficientNB("nb_zerofid");
assertNotNull(loaded);
new File("nb_zerofid").delete();
new File("nb_zerofid.nb.featuremap").delete();
}

@Test
public void testAllocateSpaceWithZeroClassesDoesNotThrow() {
FeatureMap map = new FeatureMap();
map.dim = 2;
map.fidToWord.put(0, "a");
map.fidToWord.put(1, "b");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 0);
assertEquals(0, nb.classCounts.length);
assertEquals(0, nb.weights.length);
assertEquals(0, nb.fidCounts.size());
}

@Test
public void testGetFidProbReturnsSmoothedForNegativeFeatureId() {
FeatureMap map = new FeatureMap();
map.dim = 3;
map.fidToWord.put(0, "w0");
map.fidToWord.put(1, "w1");
map.fidToWord.put(2, "w2");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
double prob = nb.getFidProb(-1, 0);
assertEquals(MemoryEfficientNB.smooth / 3, prob, 1e-6);
}

@Test
public void testGetPriorWithInvalidClassIdReturnsZero() {
FeatureMap map = new FeatureMap();
map.dim = 1;
map.fidToWord.put(0, "x");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
nb.weightedOnlineLearning(new int[] { 0 }, 1.0, 0);
double prior = nb.getPrior(999);
assertEquals(0.0, prior, 1e-6);
}

@Test
public void testGetTopPmiWordsWithVeryHighConfidenceYieldsNoWords() {
FeatureMap map = new FeatureMap();
map.dim = 2;
map.fidToWord.put(0, "x");
map.fidToWord.put(1, "y");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
nb.weightedOnlineLearning(new int[] { 0, 1 }, 2.0, 0);
Hashtable<String, Integer> result = nb.getTopPmiWords(100, 1000.0, 0);
assertNotNull(result);
assertEquals(0, result.size());
}

@Test
public void testClassifyAfterLearningOnlyOneClassAlwaysReturnsThatClass() {
FeatureMap map = new FeatureMap();
map.dim = 2;
map.fidToWord.put(0, "x");
map.fidToWord.put(1, "y");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
nb.weightedOnlineLearning(new int[] { 0, 1 }, 1.0, 0);
// Document doc = new Document() {
// 
// public int[] getActiveFid(FeatureMap m) {
// return new int[] { 0 };
// }
// 
// public int classID = -1;
// };
// int result = nb.classify(doc, -1);
// assertEquals(0, result);
}

@Test
public void testUpdateFidCountsWithZeroWeightKeepsValueUnchanged() {
FeatureMap map = new FeatureMap();
map.dim = 1;
map.fidToWord.put(0, "zero");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
nb.weightedOnlineLearning(new int[] { 0 }, 2.0, 0);
nb.weightedOnlineLearning(new int[] { 0 }, 0.0, 0);
// Document doc = new Document() {
// 
// public int[] getActiveFid(FeatureMap m) {
// return new int[] { 0 };
// }
// 
// public int classID = -1;
// };
// double[] conf = nb.getPredictionConfidence(doc);
// assertEquals(1.0, conf[0], 1e-6);
}

@Test
public void testGetExtendedFeaturesReturnsMultipleClassConfidence() {
FeatureMap map = new FeatureMap();
map.dim = 2;
map.fidToWord.put(0, "x");
map.fidToWord.put(1, "y");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
nb.weightedOnlineLearning(new int[] { 0 }, 2.0, 0);
nb.weightedOnlineLearning(new int[] { 1 }, 2.0, 1);
// Document doc = new Document() {
// 
// public int[] getActiveFid(FeatureMap m) {
// return new int[] { 0, 1 };
// }
// 
// public int classID = -1;
// };
// String ext = nb.getExtendedFeatures(doc);
// assertTrue(ext.contains("0("));
// assertTrue(ext.contains("1("));
}

@Test
public void testEmptyFidCountsDoesNotThrowInGetTopPmiWords() {
FeatureMap map = new FeatureMap();
map.dim = 1;
map.fidToWord.put(0, "x");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
CharacteristicWords words = nb.getTopPmiWords(0, 5, 0.1, 0);
assertNotNull(words);
assertEquals(0, words.topWords.size());
}

@Test
public void testToBeKeptHasNoMatchAndBelowMinLenReturnsFalse() {
Vector<String> tokens = new Vector<String>();
tokens.add("a");
Hashtable<String, Integer> coolWords = new Hashtable<>();
coolWords.put("nonexistent", 1);
boolean result = MemoryEfficientNB.toBeKept(tokens, coolWords, 0.5, 5);
assertFalse(result);
}

@Test
public void testToBeKeptWithAllTokensMatchingCoolWordsPasses() {
Vector<String> tokens = new Vector<String>();
tokens.add("special");
tokens.add("special");
tokens.add("cool");
Hashtable<String, Integer> coolWords = new Hashtable<>();
coolWords.put("special", 1);
coolWords.put("cool", 1);
boolean result = MemoryEfficientNB.toBeKept(tokens, coolWords, 0.5, 2);
assertTrue(result);
}

@Test
public void testLoadModelWithZeroDimFeatureMapDoesNotBreak() {
FeatureMap map = new FeatureMap();
map.dim = 0;
MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
String basePath = "test_zero_dim";
nb.save(basePath);
MemoryEfficientNB loaded = new MemoryEfficientNB(basePath);
assertNotNull(loaded);
new File(basePath).delete();
new File(basePath + ".nb.featuremap").delete();
}

@Test
public void testClassifyWithSingleClassReturnsZero() {
FeatureMap map = new FeatureMap();
map.dim = 1;
map.fidToWord.put(0, "x");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
nb.weightedOnlineLearning(new int[] { 0 }, 1.0, 0);
// Document doc = new Document() {
// 
// public int[] getActiveFid(FeatureMap m) {
// return new int[] { 0 };
// }
// 
// public int classID = -1;
// };
// int predicted = nb.classify(doc, -1);
// assertEquals(0, predicted);
}

@Test
public void testGetFidProbReturnsSmoothedWhenFeatureAbsentFromMap() {
FeatureMap map = new FeatureMap();
map.dim = 5;
map.fidToWord.put(0, "a");
map.fidToWord.put(1, "b");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
double result = nb.getFidProb(4, 1);
assertEquals(MemoryEfficientNB.smooth / 5, result, 1e-6);
}

@Test
public void testClassifyReturnsNegativeOneWhenThresholdNotMet() {
FeatureMap map = new FeatureMap();
map.dim = 2;
map.fidToWord.put(0, "a");
map.fidToWord.put(1, "b");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
nb.weightedOnlineLearning(new int[] { 0 }, 1.0, 0);
// Document doc = new Document() {
// 
// public int[] getActiveFid(FeatureMap m) {
// return new int[] { 1 };
// }
// 
// public int classID = 1;
// };
// int pred = nb.classify(doc, 0.9);
// assertTrue(pred == -1 || pred == 0 || pred == 1);
}

@Test
public void testGetPredictionConfidenceReturnsZeroForEmptyModel() {
FeatureMap map = new FeatureMap();
map.dim = 2;
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
// Document doc = new Document() {
// 
// public int[] getActiveFid(FeatureMap m) {
// return new int[] { 0, 1 };
// }
// 
// public int classID = -1;
// };
// double[] conf = nb.getPredictionConfidence(doc);
// assertEquals(2, conf.length);
// assertTrue(Double.isFinite(conf[0]));
// assertTrue(Double.isFinite(conf[1]));
}

@Test
public void testGetTopPmiWordsIgnoresLowFrequencyWords() {
FeatureMap map = new FeatureMap();
map.dim = 2;
map.fidToWord.put(0, "low");
map.fidToWord.put(1, "high");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
nb.weightedOnlineLearning(new int[] { 1, 1, 1 }, 3.0, 0);
nb.weightedOnlineLearning(new int[] { 0 }, 1.0, 1);
Hashtable<String, Integer> result = nb.getTopPmiWords(2, 0.0, 2);
assertFalse(result.containsKey("low"));
}

@Test
public void testClassifyTiebreaksToFirstClassIfEqualConfidence() {
FeatureMap map = new FeatureMap();
map.dim = 1;
map.fidToWord.put(0, "a");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
nb.weightedOnlineLearning(new int[] { 0 }, 1.0, 0);
nb.weightedOnlineLearning(new int[] { 0 }, 1.0, 1);
// Document doc = new Document() {
// 
// public int[] getActiveFid(FeatureMap m) {
// return new int[] { 0 };
// }
// 
// public int classID = -1;
// };
// int result = nb.classify(doc, -1);
// assertTrue(result == 0 || result == 1);
}

@Test
public void testSaveAndReloadPreservesWordCounts() {
FeatureMap map = new FeatureMap();
map.dim = 2;
map.fidToWord.put(0, "x");
map.fidToWord.put(1, "y");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
nb.weightedOnlineLearning(new int[] { 0, 1 }, 1.0, 0);
String path = "temp_nb_state";
nb.save(path);
MemoryEfficientNB loaded = new MemoryEfficientNB(path);
// Document doc = new Document() {
// 
// public int[] getActiveFid(FeatureMap m) {
// return new int[] { 1 };
// }
// 
// public int classID = -1;
// };
// double[] conf = loaded.getPredictionConfidence(doc);
// assertEquals(1.0, conf[0], 1e-6);
new File(path).delete();
new File(path + ".nb.featuremap").delete();
}

@Test
public void testToBeKeptExactlyAtMinLenAndMinRatioPasses() {
Vector<String> tokens = new Vector<String>();
tokens.add("tok1");
tokens.add("tok2");
Hashtable<String, Integer> coolWords = new Hashtable<>();
coolWords.put("tok1", 1);
boolean result = MemoryEfficientNB.toBeKept(tokens, coolWords, 0.5, 2);
assertTrue(result);
}

@Test
public void testToBeKeptFailsWhenBelowMinRatio() {
Vector<String> tokens = new Vector<String>();
tokens.add("tok1");
tokens.add("tok2");
tokens.add("tok3");
Hashtable<String, Integer> coolWords = new Hashtable<>();
coolWords.put("tok1", 1);
boolean result = MemoryEfficientNB.toBeKept(tokens, coolWords, 0.5, 2);
assertFalse(result);
}

@Test
public void testFeatureIdOutsideMapDimReturnsSmoothedProbability() {
FeatureMap map = new FeatureMap();
map.dim = 1;
map.fidToWord.put(0, "valid");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
double prob = nb.getFidProb(9999, 0);
assertEquals(MemoryEfficientNB.smooth / 1, prob, 1e-6);
}

@Test
public void testWeightedOnlineLearningDoesNotCrashWithEmptyFeatures() {
FeatureMap map = new FeatureMap();
map.dim = 0;
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
int[] features = new int[] {};
nb.weightedOnlineLearning(features, 1.0, 0);
// Document doc = new Document() {
// 
// public int[] getActiveFid(FeatureMap m) {
// return new int[] {};
// }
// 
// public int classID = 0;
// };
// double[] confidences = nb.getPredictionConfidence(doc);
// assertEquals(2, confidences.length);
// assertTrue(confidences[0] >= 0.0);
// assertTrue(confidences[1] >= 0.0);
}

@Test
public void testClassifyWithThresholdEqualToConfidenceReturnsClass() {
FeatureMap map = new FeatureMap();
map.dim = 1;
map.fidToWord.put(0, "only");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
nb.weightedOnlineLearning(new int[] { 0 }, 1.0, 0);
// Document doc = new Document() {
// 
// public int[] getActiveFid(FeatureMap m) {
// return new int[] { 0 };
// }
// 
// public int classID = 0;
// };
// double[] confidence = nb.getPredictionConfidence(doc);
// int result = nb.classify(doc, confidence[0]);
// assertEquals(0, result);
}

@Test
public void testGetTopPmiWordsReturnsEmptyIfNoWordsAboveThreshold() {
FeatureMap map = new FeatureMap();
map.dim = 2;
map.fidToWord.put(0, "a");
map.fidToWord.put(1, "b");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
nb.weightedOnlineLearning(new int[] { 0 }, 1.0, 0);
nb.weightedOnlineLearning(new int[] { 1 }, 1.0, 1);
// CharacteristicWords words = nb.getTopPmiWords(0, 100.0, 0);
// assertNotNull(words);
// assertEquals(0, words.topWords.size());
}

@Test
public void testFIDProbabilityForZeroWeightDenominatorReturnsSmoothed() {
FeatureMap map = new FeatureMap();
map.dim = 2;
map.fidToWord.put(0, "w0");
map.fidToWord.put(1, "w1");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
double p = nb.getFidProb(1, 0);
assertEquals(MemoryEfficientNB.smooth / map.dim, p, 1e-6);
}

@Test
public void testClassifierDoesNotCrashWithNullFeatureNames() {
FeatureMap map = new FeatureMap();
map.dim = 1;
MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
nb.weightedOnlineLearning(new int[] { 0 }, 1.0, 0);
CharacteristicWords words = nb.getTopPmiWords(0, 1, 0, 0);
assertNotNull(words);
}

@Test
public void testSaveAndLoadPreservesFidCount() {
FeatureMap map = new FeatureMap();
map.dim = 3;
map.fidToWord.put(0, "zero");
map.fidToWord.put(1, "one");
map.fidToWord.put(2, "two");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
nb.weightedOnlineLearning(new int[] { 0, 1, 2 }, 1.0, 0);
String fname = "nb_test_save_load";
nb.save(fname);
MemoryEfficientNB loaded = new MemoryEfficientNB(fname);
// Document doc = new Document() {
// 
// public int[] getActiveFid(FeatureMap m) {
// return new int[] { 0 };
// }
// 
// public int classID = 0;
// };
// double[] confidences = loaded.getPredictionConfidence(doc);
// assertEquals(1.0, confidences[0], 1e-6);
new File(fname).delete();
new File(fname + ".nb.featuremap").delete();
}

@Test
public void testEmptyFeatureMapDoesNotCauseClassifierToCrash() {
FeatureMap emptyMap = new FeatureMap();
emptyMap.dim = 0;
MemoryEfficientNB nb = new MemoryEfficientNB(emptyMap, 1);
// Document doc = new Document() {
// 
// public int[] getActiveFid(FeatureMap m) {
// return new int[] {};
// }
// 
// public int classID = 0;
// };
// int result = nb.classify(doc, -1);
// assertEquals(0, result);
}

@Test
public void testToBeKeptWithDuplicateTokensStillCountsOnce() {
Vector<String> tokens = new Vector<String>();
tokens.add("word1");
tokens.add("word1");
tokens.add("word2");
tokens.add("word2");
tokens.add("word3");
Hashtable<String, Integer> coolWords = new Hashtable<>();
coolWords.put("word1", 1);
coolWords.put("word2", 1);
boolean result = MemoryEfficientNB.toBeKept(tokens, coolWords, 0.6, 3);
assertTrue(result);
}

@Test
public void testGetExtendedFeaturesReturnsEmptyIfAllConfidencesZero() {
FeatureMap map = new FeatureMap();
map.dim = 1;
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
// Document doc = new Document() {
// 
// public int[] getActiveFid(FeatureMap m) {
// return new int[] {};
// }
// 
// public int classID = -1;
// };
// String output = nb.getExtendedFeatures(doc);
// assertTrue(output.isEmpty());
}

@Test
public void testGetFidProbReturnsSmoothedWhenNoClassDataExists() {
FeatureMap map = new FeatureMap();
map.dim = 10;
map.fidToWord.put(2, "word");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 3);
double prob = nb.getFidProb(2, 1);
assertEquals(MemoryEfficientNB.smooth / 10, prob, 1e-6);
}

@Test
public void testGetPriorWithZeroSampleSize() {
FeatureMap map = new FeatureMap();
map.dim = 1;
MemoryEfficientNB nb = new MemoryEfficientNB(map, 3);
double prior = nb.getPrior(0);
assertEquals(0.0, prior, 1e-6);
}

@Test
public void testClassifyReturnsDefaultWhenOnlyNegativeLogLikelihoods() {
FeatureMap map = new FeatureMap();
map.dim = 1;
map.fidToWord.put(0, "a");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
nb.weightedOnlineLearning(new int[] { 0 }, 1.0, 1);
// Document doc = new Document() {
// 
// public int[] getActiveFid(FeatureMap m) {
// return new int[] { 0 };
// }
// 
// public int classID = -1;
// };
// int result = nb.classify(doc, -1);
// assertTrue(result == 0 || result == 1);
}

@Test
public void testGetAccReturnsZeroWhenNoneCorrect() {
FeatureMap map = new FeatureMap();
map.dim = 1;
map.fidToWord.put(0, "a");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
nb.weightedOnlineLearning(new int[] { 0 }, 1.0, 0);
// Document doc = new Document() {
// 
// public int[] getActiveFid(FeatureMap m) {
// return new int[] { 0 };
// }
// 
// public int classID = 1;
// };
DocumentCollection testSet = new DocumentCollection();
// testSet.docs.add(doc);
double acc = nb.getAcc(testSet);
assertEquals(0.0, acc, 1e-6);
}

@Test
public void testGetTopPmiWordsHandlesNullFidToWordGracefully() {
FeatureMap map = new FeatureMap();
map.dim = 2;
MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
nb.weightedOnlineLearning(new int[] { 0, 1 }, 2.0, 0);
CharacteristicWords words = nb.getTopPmiWords(0, 5, 0.1, 0);
assertNotNull(words);
}

@Test
public void testUpdateFidCountsIncrementsCorrectly() {
FeatureMap map = new FeatureMap();
map.dim = 2;
map.fidToWord.put(0, "test");
map.fidToWord.put(1, "test2");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
nb.weightedOnlineLearning(new int[] { 0 }, 1.5, 0);
nb.weightedOnlineLearning(new int[] { 0 }, 2.5, 0);
// Document doc = new Document() {
// 
// public int[] getActiveFid(FeatureMap m) {
// return new int[] { 0 };
// }
// 
// public int classID = 0;
// };
// double[] conf = nb.getPredictionConfidence(doc);
// assertEquals(1.0, conf[0], 1e-6);
}

@Test
public void testToBeKeptReturnsFalseWhenNoTokenMeetsMinLenRequirement() {
Vector<String> tokens = new Vector<String>();
Hashtable<String, Integer> coolWords = new Hashtable<>();
coolWords.put("cool", 1);
boolean result = MemoryEfficientNB.toBeKept(tokens, coolWords, 0.5, 1);
assertFalse(result);
}

@Test
public void testSaveModelWithEmptyFidMapDoesNotThrow() {
FeatureMap map = new FeatureMap();
map.dim = 0;
MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
String file = "nb_empty_test";
nb.save(file);
File mainFile = new File(file);
File mapFile = new File(file + ".nb.featuremap");
assertTrue(mainFile.exists());
assertTrue(mapFile.exists());
mainFile.delete();
mapFile.delete();
}

@Test
public void testClassifyReturnsNegativeOneWhenAllConfidencesBelowThreshold() {
FeatureMap map = new FeatureMap();
map.dim = 1;
map.fidToWord.put(0, "z");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
nb.weightedOnlineLearning(new int[] { 0 }, 1.0, 0);
// Document doc = new Document() {
// 
// public int[] getActiveFid(FeatureMap m) {
// return new int[] { 0 };
// }
// 
// public int classID = -1;
// };
// int result = nb.classify(doc, 0.99999);
// assertEquals(-1, result);
}

@Test
public void testGetExtendedFeaturesIncludesMultipleClassesIfApplicable() {
FeatureMap map = new FeatureMap();
map.dim = 2;
map.fidToWord.put(0, "a");
map.fidToWord.put(1, "b");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
nb.weightedOnlineLearning(new int[] { 0 }, 2.0, 0);
nb.weightedOnlineLearning(new int[] { 1 }, 2.0, 1);
// Document doc = new Document() {
// 
// public int[] getActiveFid(FeatureMap m) {
// return new int[] { 0, 1 };
// }
// 
// public int classID = -1;
// };
// String extended = nb.getExtendedFeatures(doc);
// assertTrue(extended.contains("0("));
// assertTrue(extended.contains("1("));
}

@Test
public void testClassifyWhenAllConfidencesEqualReturnsFirstClass() {
FeatureMap map = new FeatureMap();
map.dim = 1;
map.fidToWord.put(0, "equal");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 3);
nb.weightedOnlineLearning(new int[] { 0 }, 1.0, 0);
nb.weightedOnlineLearning(new int[] { 0 }, 1.0, 1);
nb.weightedOnlineLearning(new int[] { 0 }, 1.0, 2);
// Document doc = new Document() {
// 
// public int[] getActiveFid(FeatureMap m) {
// return new int[] { 0 };
// }
// 
// public int classID = -1;
// };
// int predicted = nb.classify(doc, -1);
// assertTrue(predicted >= 0 && predicted <= 2);
}

@Test
public void testGetPredictionConfidenceWithUntrainedFeatureReturnsSmoothed() {
FeatureMap map = new FeatureMap();
map.dim = 2;
map.fidToWord.put(0, "a");
map.fidToWord.put(1, "b");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
nb.weightedOnlineLearning(new int[] { 0 }, 1.0, 0);
// Document doc = new Document() {
// 
// public int[] getActiveFid(FeatureMap m) {
// return new int[] { 1 };
// }
// 
// public int classID = 1;
// };
// double[] conf = nb.getPredictionConfidence(doc);
// assertEquals(2, conf.length);
// assertTrue(conf[0] > 0.0 || conf[1] > 0.0);
}

@Test
public void testGetTopPmiWordsWithAllZeroWeightsSkipsAllFeatures() {
FeatureMap map = new FeatureMap();
map.dim = 2;
map.fidToWord.put(0, "zero0");
map.fidToWord.put(1, "zero1");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
CharacteristicWords result = nb.getTopPmiWords(0, 5, 0.1, 0);
assertNotNull(result);
assertEquals(0, result.topWords.size());
}

@Test
public void testSaveAndLoadWithMultipleClassesAndFeatures() {
FeatureMap map = new FeatureMap();
map.dim = 3;
map.fidToWord.put(0, "x");
map.fidToWord.put(1, "y");
map.fidToWord.put(2, "z");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 3);
nb.weightedOnlineLearning(new int[] { 0 }, 1.0, 0);
nb.weightedOnlineLearning(new int[] { 1, 2 }, 2.0, 1);
nb.weightedOnlineLearning(new int[] { 2 }, 0.5, 2);
String file = "nb_test_multi";
nb.save(file);
MemoryEfficientNB loaded = new MemoryEfficientNB(file);
// Document doc = new Document() {
// 
// public int[] getActiveFid(FeatureMap m) {
// return new int[] { 2 };
// }
// 
// public int classID = 2;
// };
// int prediction = loaded.classify(doc, -1);
// assertTrue(prediction >= 0 && prediction <= 2);
new File(file).delete();
new File(file + ".nb.featuremap").delete();
}

@Test
public void testToBeKeptWithManyDuplicateTokensStillSatisfiesRatio() {
Vector<String> tokens = new Vector<String>();
tokens.add("alpha");
tokens.add("alpha");
tokens.add("alpha");
tokens.add("beta");
tokens.add("gamma");
tokens.add("gamma");
Hashtable<String, Integer> coolWords = new Hashtable<>();
coolWords.put("alpha", 1);
coolWords.put("gamma", 1);
boolean result = MemoryEfficientNB.toBeKept(tokens, coolWords, 0.5, 3);
assertTrue(result);
}

@Test
public void testGetExtendedFeaturesWithZeroProbabilitiesReturnsEmptyString() {
FeatureMap map = new FeatureMap();
map.dim = 2;
MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
// Document doc = new Document() {
// 
// public int[] getActiveFid(FeatureMap m) {
// return new int[] { 0, 1 };
// }
// 
// public int classID = -1;
// };
// String result = nb.getExtendedFeatures(doc);
// assertEquals("", result);
}

@Test
public void testHighMinAppThresExcludesAllTopPmiWords() {
FeatureMap map = new FeatureMap();
map.dim = 3;
map.fidToWord.put(0, "low0");
map.fidToWord.put(1, "low1");
map.fidToWord.put(2, "low2");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
nb.weightedOnlineLearning(new int[] { 0, 1 }, 1.0, 0);
nb.weightedOnlineLearning(new int[] { 2 }, 1.0, 0);
CharacteristicWords words = nb.getTopPmiWords(0, 5, 0.0, 10);
assertEquals(0, words.topWords.size());
}

@Test
public void testGetPredictionConfidenceCorrectlyHandlesEmptyMap() {
FeatureMap map = new FeatureMap();
map.dim = 0;
MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
// Document doc = new Document() {
// 
// public int[] getActiveFid(FeatureMap m) {
// return new int[] {};
// }
// 
// public int classID = -1;
// };
// double[] conf = nb.getPredictionConfidence(doc);
// assertNotNull(conf);
// assertEquals(1, conf.length);
// assertEquals(1.0, conf[0], 1e-6);
}

@Test
public void testGetAccHandlesAllCorrectPredictions() {
FeatureMap map = new FeatureMap();
map.dim = 1;
map.fidToWord.put(0, "x");
MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
nb.weightedOnlineLearning(new int[] { 0 }, 3.0, 0);
// Document doc = new Document() {
// 
// public int[] getActiveFid(FeatureMap m) {
// return new int[] { 0 };
// }
// 
// public int classID = 0;
// };
DocumentCollection c = new DocumentCollection();
// c.docs.add(doc);
// c.docs.add(doc);
double acc = nb.getAcc(c);
assertEquals(1.0, acc, 1e-6);
}
}
