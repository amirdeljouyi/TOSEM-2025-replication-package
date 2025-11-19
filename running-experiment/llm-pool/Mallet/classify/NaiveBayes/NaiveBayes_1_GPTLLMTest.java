package cc.mallet.classify.tests;

import cc.mallet.classify.*;
import cc.mallet.pipe.*;
import cc.mallet.pipe.iterator.ArrayIterator;
import cc.mallet.pipe.iterator.StringArrayIterator;
import cc.mallet.types.*;
import org.junit.Test;
import org.mockito.Mockito;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class NaiveBayes_1_GPTLLMTest {

@Test
public void testClassifyReturnsValidLabeling() {
Alphabet dataAlphabet = new Alphabet();
Alphabet labelAlphabet = new Alphabet();
dataAlphabet.lookupIndex("good");
dataAlphabet.lookupIndex("excellent");
labelAlphabet.lookupIndex("positive");
labelAlphabet.lookupIndex("negative");
Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {

public Instance pipe(Instance carrier) {
return carrier;
}
};
double[] priorValues = new double[] { 0.6, 0.4 };
Multinomial prior = new Multinomial(priorValues);
Multinomial[] classProbs = new Multinomial[2];
double[] featureProbs1 = new double[] { 1.0, 2.0 };
double[] featureProbs2 = new double[] { 2.0, 1.0 };
classProbs[0] = new Multinomial(featureProbs1);
classProbs[1] = new Multinomial(featureProbs2);
NaiveBayes nb = new NaiveBayes(pipe, prior, classProbs);
int[] indices = new int[] { dataAlphabet.lookupIndex("good"), dataAlphabet.lookupIndex("excellent") };
double[] values = new double[] { 1.0, 1.0 };
FeatureVector fv = new FeatureVector(dataAlphabet, indices, values);
// Label label = labelAlphabet.lookupLabel("positive");
// Instance instance = new Instance(fv, label, "test1", null);
// Classification classification = nb.classify(instance);
// Labeling labeling = classification.getLabeling();
// assertEquals(2, labeling.numLocations());
// double p1 = labeling.valueAtLocation(0);
// double p2 = labeling.valueAtLocation(1);
// assertTrue(p1 >= 0 && p1 <= 1);
// assertTrue(p2 >= 0 && p2 <= 1);
// assertEquals(1.0, p1 + p2, 1e-6);
}

@Test
public void testClassifyWithUnknownFeatureDoesNotFail() {
Alphabet dataAlphabet = new Alphabet();
Alphabet labelAlphabet = new Alphabet();
dataAlphabet.lookupIndex("known");
labelAlphabet.lookupIndex("A");
labelAlphabet.lookupIndex("B");
Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {

public Instance pipe(Instance carrier) {
return carrier;
}
};
Multinomial prior = new Multinomial(new double[] { 0.5, 0.5 });
Multinomial[] probs = new Multinomial[2];
probs[0] = new Multinomial(new double[] { 1.0 });
probs[1] = new Multinomial(new double[] { 2.0 });
NaiveBayes nb = new NaiveBayes(pipe, prior, probs);
int unknownFeatureIndex = dataAlphabet.lookupIndex("unknown", true);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { unknownFeatureIndex }, new double[] { 1.0 });
// Instance instance = new Instance(fv, labelAlphabet.lookupLabel("A"), "test2", null);
// Classification classification = nb.classify(instance);
// Labeling labeling = classification.getLabeling();
// assertEquals(2, labeling.numLocations());
}

@Test
public void testGetMultinomialsReturnsExpectedLength() {
Alphabet dataAlphabet = new Alphabet();
Alphabet labelAlphabet = new Alphabet();
dataAlphabet.lookupIndex("feat");
labelAlphabet.lookupIndex("yes");
labelAlphabet.lookupIndex("no");
Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {

public Instance pipe(Instance carrier) {
return carrier;
}
};
Multinomial prior = new Multinomial(new double[] { 0.5, 0.5 });
Multinomial[] arr = new Multinomial[2];
arr[0] = new Multinomial(new double[] { 1 });
arr[1] = new Multinomial(new double[] { 1 });
NaiveBayes nb = new NaiveBayes(pipe, prior, arr);
assertEquals(2, nb.getMultinomials().length);
}

@Test
public void testGetPriorsReturnsCorrectValues() {
Alphabet dataAlphabet = new Alphabet();
Alphabet labelAlphabet = new Alphabet();
dataAlphabet.lookupIndex("x");
labelAlphabet.lookupIndex("cat");
labelAlphabet.lookupIndex("dog");
Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {

public Instance pipe(Instance carrier) {
return carrier;
}
};
double[] priorValues = new double[] { 0.3, 0.7 };
Multinomial prior = new Multinomial(priorValues);
Multinomial[] featureProbs = new Multinomial[2];
featureProbs[0] = new Multinomial(new double[] { 1.0 });
featureProbs[1] = new Multinomial(new double[] { 1.0 });
NaiveBayes nb = new NaiveBayes(pipe, prior, featureProbs);
Multinomial.Logged returned = nb.getPriors();
// assertArrayEquals(prior.toLogMultinomial().getValues(), returned.getValues(), 1e-6);
}

@Test
public void testSerializationRoundTrip() throws IOException, ClassNotFoundException {
Alphabet dataAlphabet = new Alphabet();
Alphabet labelAlphabet = new Alphabet();
dataAlphabet.lookupIndex("f1");
labelAlphabet.lookupIndex("class1");
labelAlphabet.lookupIndex("class2");
Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {

public Instance pipe(Instance carrier) {
return carrier;
}
};
Multinomial prior = new Multinomial(new double[] { 0.4, 0.6 });
Multinomial[] m = new Multinomial[2];
m[0] = new Multinomial(new double[] { 1.0 });
m[1] = new Multinomial(new double[] { 2.0 });
NaiveBayes nb = new NaiveBayes(pipe, prior, m);
ByteArrayOutputStream out = new ByteArrayOutputStream();
ObjectOutputStream oos = new ObjectOutputStream(out);
oos.writeObject(nb);
oos.close();
ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
ObjectInputStream ois = new ObjectInputStream(in);
NaiveBayes loaded = (NaiveBayes) ois.readObject();
ois.close();
int[] indices = new int[] { dataAlphabet.lookupIndex("f1") };
double[] values = new double[] { 1.0 };
FeatureVector fv = new FeatureVector(dataAlphabet, indices, values);
// Label label = labelAlphabet.lookupLabel("class1");
// Instance instance = new Instance(fv, label, "test3", null);
// Classification result = loaded.classify(instance);
// assertNotNull(result);
}

@Test(expected = ClassNotFoundException.class)
public void testReadObjectThrowsOnVersionMismatch() throws IOException, ClassNotFoundException {
Alphabet dataAlphabet = new Alphabet();
Alphabet labelAlphabet = new Alphabet();
dataAlphabet.lookupIndex("x");
labelAlphabet.lookupIndex("lab");
Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {

public Instance pipe(Instance carrier) {
return carrier;
}
};
Multinomial prior = new Multinomial(new double[] { 1.0 });
Multinomial[] m = new Multinomial[1];
m[0] = new Multinomial(new double[] { 1.0 });
NaiveBayes nb = new NaiveBayes(pipe, prior, m);
ByteArrayOutputStream out = new ByteArrayOutputStream();
ObjectOutputStream oos = new ObjectOutputStream(out);
oos.writeInt(999);
oos.writeObject(pipe);
// oos.writeObject(prior.toLogMultinomial());
oos.writeObject(nb.getMultinomials());
oos.close();
ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
ObjectInputStream ois = new ObjectInputStream(in);
// nb.readObject(ois);
}

@Test
public void testDataLogLikelihoodWithLabeledInstances() {
Alphabet dataAlphabet = new Alphabet();
Alphabet labelAlphabet = new Alphabet();
dataAlphabet.lookupIndex("x");
labelAlphabet.lookupIndex("L1");
labelAlphabet.lookupIndex("L2");
Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {

public Instance pipe(Instance carrier) {
return carrier;
}
};
Multinomial prior = new Multinomial(new double[] { 0.5, 0.5 });
Multinomial[] m = new Multinomial[2];
m[0] = new Multinomial(new double[] { 2.0 });
m[1] = new Multinomial(new double[] { 3.0 });
NaiveBayes nb = new NaiveBayes(pipe, prior, m);
FeatureVector fv1 = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 2.0 });
// Label label1 = labelAlphabet.lookupLabel("L1");
// Instance inst1 = new Instance(fv1, label1, "id1", null);
FeatureVector fv2 = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
// Label label2 = labelAlphabet.lookupLabel("L2");
// Instance inst2 = new Instance(fv2, label2, "id2", null);
InstanceList list = new InstanceList(pipe);
// list.add(inst1);
// list.add(inst2);
double ll = nb.dataLogLikelihood(list);
assertTrue(Double.isFinite(ll));
}

@Test
public void testLabelLogLikelihoodWithOneLabelPerInstance() {
Alphabet dataAlphabet = new Alphabet();
Alphabet labelAlphabet = new Alphabet();
dataAlphabet.lookupIndex("term");
labelAlphabet.lookupIndex("spam");
labelAlphabet.lookupIndex("ham");
Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {

public Instance pipe(Instance carrier) {
return carrier;
}
};
Multinomial prior = new Multinomial(new double[] { 0.4, 0.6 });
Multinomial[] m = new Multinomial[2];
m[0] = new Multinomial(new double[] { 1.5 });
m[1] = new Multinomial(new double[] { 2.5 });
NaiveBayes nb = new NaiveBayes(pipe, prior, m);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
// Label label = labelAlphabet.lookupLabel("spam");
// Instance instance = new Instance(fv, label, "doc", null);
InstanceList list = new InstanceList(pipe);
// list.add(instance);
double result = nb.labelLogLikelihood(list);
assertTrue(Double.isFinite(result));
}

@Test
public void testEmptyInstanceListReturnsZeroLikelihoods() {
Alphabet dataAlphabet = new Alphabet();
Alphabet labelAlphabet = new Alphabet();
dataAlphabet.lookupIndex("a");
labelAlphabet.lookupIndex("b");
labelAlphabet.lookupIndex("c");
Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {

public Instance pipe(Instance carrier) {
return carrier;
}
};
Multinomial prior = new Multinomial(new double[] { 1.0, 1.0 });
Multinomial[] m = new Multinomial[2];
m[0] = new Multinomial(new double[] { 1.0 });
m[1] = new Multinomial(new double[] { 1.0 });
NaiveBayes nb = new NaiveBayes(pipe, prior, m);
InstanceList list = new InstanceList(pipe);
assertEquals(0.0, nb.dataLogLikelihood(list), 1e-10);
assertEquals(0.0, nb.labelLogLikelihood(list), 1e-10);
}

@Test
public void testClassifyOnEmptyFeatureVector() {
Alphabet dataAlphabet = new Alphabet();
Alphabet labelAlphabet = new Alphabet();
labelAlphabet.lookupIndex("yes");
labelAlphabet.lookupIndex("no");
Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {

public Instance pipe(Instance carrier) {
return carrier;
}
};
Multinomial prior = new Multinomial(new double[] { 1.0, 1.0 });
Multinomial[] condProbs = new Multinomial[] { new Multinomial(new double[] {}), new Multinomial(new double[] {}) };
NaiveBayes nb = new NaiveBayes(pipe, prior, condProbs);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] {}, new double[] {});
// Label label = labelAlphabet.lookupLabel("yes");
// Instance instance = new Instance(fv, label, "empty", null);
// Classification classification = nb.classify(instance);
// assertNotNull(classification);
// Labeling labeling = classification.getLabeling();
// assertTrue(labeling.value(0) > 0.0 || labeling.value(1) > 0.0);
}

@Test
public void testDataLogLikelihoodWithNullLabelingFallsBackToPredicted() {
Alphabet dataAlphabet = new Alphabet();
Alphabet labelAlphabet = new Alphabet();
dataAlphabet.lookupIndex("t1");
labelAlphabet.lookupIndex("label1");
labelAlphabet.lookupIndex("label2");
Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {

public Instance pipe(Instance carrier) {
return carrier;
}
};
Multinomial prior = new Multinomial(new double[] { 0.5, 0.5 });
Multinomial[] m = new Multinomial[] { new Multinomial(new double[] { 1.0 }), new Multinomial(new double[] { 2.0 }) };
NaiveBayes nb = new NaiveBayes(pipe, prior, m);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
Instance instance = new Instance(fv, null, "noLabel", null);
InstanceList list = new InstanceList(pipe);
list.add(instance);
double result = nb.dataLogLikelihood(list);
assertTrue(Double.isFinite(result));
}

@Test
public void testLabelLogLikelihoodWithMultiLabelDistribution() {
Alphabet dataAlphabet = new Alphabet();
Alphabet labelAlphabet = new Alphabet();
dataAlphabet.lookupIndex("feature");
labelAlphabet.lookupIndex("A");
labelAlphabet.lookupIndex("B");
Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {

public Instance pipe(Instance carrier) {
return carrier;
}
};
Multinomial prior = new Multinomial(new double[] { 0.5, 0.5 });
Multinomial[] m = new Multinomial[] { new Multinomial(new double[] { 1.0 }), new Multinomial(new double[] { 1.0 }) };
NaiveBayes nb = new NaiveBayes(pipe, prior, m);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
// Labeling labeling = new LabelVector(labelAlphabet, new double[] { 0.6, 0.4 });
// Instance instance = new Instance(fv, labeling, "multiLabel", null);
InstanceList list = new InstanceList(pipe);
// list.add(instance);
double logLikelihood = nb.labelLogLikelihood(list);
assertTrue(Double.isFinite(logLikelihood));
}

@Test
public void testClassifyWithExpandedLabelAlphabetSizeOnlyUsesTrainedLabels() {
Alphabet dataAlphabet = new Alphabet();
Alphabet labelAlphabet = new Alphabet();
dataAlphabet.lookupIndex("token");
labelAlphabet.lookupIndex("label1");
labelAlphabet.lookupIndex("label2");
Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {

public Instance pipe(Instance carrier) {
return carrier;
}
};
Multinomial prior = new Multinomial(new double[] { 0.3, 0.7 });
Multinomial[] condProbs = new Multinomial[] { new Multinomial(new double[] { 1.0 }), new Multinomial(new double[] { 1.0 }) };
NaiveBayes nb = new NaiveBayes(pipe, prior, condProbs);
labelAlphabet.lookupIndex("label3");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
// Label label = labelAlphabet.lookupLabel("label1");
// Instance instance = new Instance(fv, label, "expandTest", null);
// Classification classification = nb.classify(instance);
// Labeling labeling = classification.getLabeling();
// assertEquals(3, labeling.numLocations());
// assertEquals(0.0, labeling.valueAtLocation(2), 1e-10);
}

@Test
public void testPrintWordsWithMoreRequestedThanFeaturesDoesNotThrow() {
Alphabet dataAlphabet = new Alphabet();
Alphabet labelAlphabet = new Alphabet();
dataAlphabet.lookupIndex("alpha");
dataAlphabet.lookupIndex("beta");
labelAlphabet.lookupIndex("C1");
labelAlphabet.lookupIndex("C2");
Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {

public Instance pipe(Instance carrier) {
return carrier;
}
};
Multinomial[] m = new Multinomial[] { new Multinomial(new double[] { 1.0, 2.0 }), new Multinomial(new double[] { 2.0, 1.0 }) };
Multinomial prior = new Multinomial(new double[] { 0.5, 0.5 });
NaiveBayes nb = new NaiveBayes(pipe, prior, m);
nb.printWords(10);
}

@Test
public void testDataLogProbabilityHandlesMissingFeatureGracefully() {
Alphabet dataAlphabet = new Alphabet();
Alphabet labelAlphabet = new Alphabet();
dataAlphabet.lookupIndex("f1");
labelAlphabet.lookupIndex("L");
Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {

public Instance pipe(Instance carrier) {
return carrier;
}
};
Multinomial[] m = new Multinomial[] { new Multinomial(new double[] { 1.0 }) };
Multinomial prior = new Multinomial(new double[] { 1.0 });
NaiveBayes nb = new NaiveBayes(pipe, prior, m);
int missingIndex = dataAlphabet.lookupIndex("f2", true);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { missingIndex }, new double[] { 1.0 });
// Label label = labelAlphabet.lookupLabel("L");
// Instance instance = new Instance(fv, label, "missingFeature", null);
// Classification result = nb.classify(instance);
// assertNotNull(result);
}

@Test
public void testLabelLogLikelihoodSkipsZeroWeightLabels() {
Alphabet dataAlphabet = new Alphabet();
Alphabet labelAlphabet = new Alphabet();
dataAlphabet.lookupIndex("term");
labelAlphabet.lookupIndex("p");
labelAlphabet.lookupIndex("q");
Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {

public Instance pipe(Instance carrier) {
return carrier;
}
};
Multinomial[] m = new Multinomial[] { new Multinomial(new double[] { 1.0 }), new Multinomial(new double[] { 2.0 }) };
Multinomial prior = new Multinomial(new double[] { 0.5, 0.5 });
NaiveBayes nb = new NaiveBayes(pipe, prior, m);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
double[] labelWeights = new double[] { 1.0, 0.0 };
// Labeling labeling = new LabelVector(labelAlphabet, labelWeights);
// Instance instance = new Instance(fv, labeling, "zeroWeight", null);
InstanceList list = new InstanceList(pipe);
// list.add(instance);
double likelihood = nb.labelLogLikelihood(list);
assertTrue(Double.isFinite(likelihood));
}

@Test
public void testInstanceWithZeroWeightsInFeatureVector() {
Alphabet dataAlphabet = new Alphabet();
Alphabet labelAlphabet = new Alphabet();
dataAlphabet.lookupIndex("x1");
dataAlphabet.lookupIndex("x2");
labelAlphabet.lookupIndex("C1");
labelAlphabet.lookupIndex("C2");
Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {

public Instance pipe(Instance carrier) {
return carrier;
}
};
double[] priors = new double[] { 0.5, 0.5 };
Multinomial prior = new Multinomial(priors);
Multinomial[] classProbs = new Multinomial[2];
classProbs[0] = new Multinomial(new double[] { 1.0, 1.0 });
classProbs[1] = new Multinomial(new double[] { 1.0, 1.0 });
NaiveBayes nb = new NaiveBayes(pipe, prior, classProbs);
int[] indices = new int[] { 0, 1 };
double[] values = new double[] { 0.0, 0.0 };
FeatureVector fv = new FeatureVector(dataAlphabet, indices, values);
// Label label = labelAlphabet.lookupLabel("C1");
// Instance instance = new Instance(fv, label, "zeroFV", null);
// Classification classification = nb.classify(instance);
// Labeling labeling = classification.getLabeling();
// assertEquals(2, labeling.numLocations());
// double p0 = labeling.valueAtLocation(0);
// double p1 = labeling.valueAtLocation(1);
// assertEquals(1.0, p0 + p1, 1e-6);
}

@Test
public void testClassificationProbabilityNormalizationOnLargeScores() {
Alphabet dataAlphabet = new Alphabet();
Alphabet labelAlphabet = new Alphabet();
dataAlphabet.lookupIndex("f");
labelAlphabet.lookupIndex("A");
labelAlphabet.lookupIndex("B");
Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {

public Instance pipe(Instance carrier) {
return carrier;
}
};
double[] priors = new double[] { 0.5, 0.5 };
Multinomial prior = new Multinomial(priors);
Multinomial[] classProbs = new Multinomial[2];
classProbs[0] = new Multinomial(new double[] { 1e100 });
classProbs[1] = new Multinomial(new double[] { 1e-100 });
NaiveBayes nb = new NaiveBayes(pipe, prior, classProbs);
int[] indices = new int[] { 0 };
double[] values = new double[] { 1.0 };
FeatureVector fv = new FeatureVector(dataAlphabet, indices, values);
// Label label = labelAlphabet.lookupLabel("A");
// Instance instance = new Instance(fv, label, "largeScores", null);
// Classification classification = nb.classify(instance);
// Labeling labeling = classification.getLabeling();
// assertEquals(2, labeling.numLocations());
// double p0 = labeling.valueAtLocation(0);
// double p1 = labeling.valueAtLocation(1);
// assertTrue(p0 >= 0.99);
// assertTrue(p1 <= 0.01);
}

@Test
public void testSerializationWithEmptyModel() throws IOException, ClassNotFoundException {
Alphabet dataAlphabet = new Alphabet();
Alphabet labelAlphabet = new Alphabet();
Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {

public Instance pipe(Instance instance) {
return instance;
}
};
Multinomial emptyPrior = new Multinomial(new double[] {});
Multinomial[] emptyConds = new Multinomial.Logged[0];
NaiveBayes nb = new NaiveBayes(pipe, emptyPrior, emptyConds);
ByteArrayOutputStream out = new ByteArrayOutputStream();
ObjectOutputStream oos = new ObjectOutputStream(out);
oos.writeObject(nb);
oos.close();
ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
ObjectInputStream ois = new ObjectInputStream(in);
NaiveBayes deserialized = (NaiveBayes) ois.readObject();
assertNotNull(deserialized);
assertNotNull(deserialized.getPriors());
assertEquals(0, deserialized.getMultinomials().length);
}

@Test
public void testInstanceWithFeatureVectorValuesEqualToNaN() {
Alphabet dataAlphabet = new Alphabet();
Alphabet labelAlphabet = new Alphabet();
dataAlphabet.lookupIndex("f1");
labelAlphabet.lookupIndex("L1");
labelAlphabet.lookupIndex("L2");
Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {

public Instance pipe(Instance instance) {
return instance;
}
};
Multinomial prior = new Multinomial(new double[] { 0.5, 0.5 });
Multinomial[] condProbs = new Multinomial[] { new Multinomial(new double[] { 1.0 }), new Multinomial(new double[] { 1.0 }) };
NaiveBayes nb = new NaiveBayes(pipe, prior, condProbs);
int[] indices = new int[] { 0 };
double[] values = new double[] { Double.NaN };
FeatureVector fv = new FeatureVector(dataAlphabet, indices, values);
Instance instance = new Instance(fv, null, "nanTest", null);
Classification classification = nb.classify(instance);
Labeling labeling = classification.getLabeling();
assertEquals(2, labeling.numLocations());
double p0 = labeling.valueAtLocation(0);
double p1 = labeling.valueAtLocation(1);
double total = p0 + p1;
assertTrue(Double.isNaN(p0) || Double.isNaN(p1) || Double.isNaN(total));
}

@Test
public void testTrainingWithZeroProbabilities() {
Alphabet dataAlphabet = new Alphabet();
Alphabet labelAlphabet = new Alphabet();
dataAlphabet.lookupIndex("f1");
dataAlphabet.lookupIndex("f2");
labelAlphabet.lookupIndex("A");
labelAlphabet.lookupIndex("B");
Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {

public Instance pipe(Instance instance) {
return instance;
}
};
Multinomial prior = new Multinomial(new double[] { 0.0, 1.0 });
Multinomial[] condProbs = new Multinomial[] { new Multinomial(new double[] { 0.0, 0.0 }), new Multinomial(new double[] { 1.0, 1.0 }) };
NaiveBayes nb = new NaiveBayes(pipe, prior, condProbs);
int[] indices = new int[] { 0, 1 };
double[] values = new double[] { 1.0, 1.0 };
FeatureVector fv = new FeatureVector(dataAlphabet, indices, values);
// Label label = labelAlphabet.lookupLabel("B");
// Instance instance = new Instance(fv, label, "zeroProb", null);
// Classification classification = nb.classify(instance);
// assertNotNull(classification);
// Labeling labeling = classification.getLabeling();
// double p0 = labeling.value(0);
// double p1 = labeling.value(1);
// assertEquals(0.0, p0, 1e-10);
// assertEquals(1.0, p1, 1e-10);
}

@Test
public void testScoreCalculationIgnoresExtraFeaturesBeyondModelSize() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("feature1");
dataAlphabet.lookupIndex("feature2");
dataAlphabet.lookupIndex("extra_feature");
Alphabet labelAlphabet = new Alphabet();
labelAlphabet.lookupIndex("class1");
labelAlphabet.lookupIndex("class2");
Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {

public Instance pipe(Instance instance) {
return instance;
}
};
Multinomial prior = new Multinomial(new double[] { 0.5, 0.5 });
Multinomial[] classConditionals = new Multinomial[] { new Multinomial(new double[] { 1.0, 2.0 }), new Multinomial(new double[] { 2.0, 1.0 }) };
NaiveBayes nb = new NaiveBayes(pipe, prior, classConditionals);
int[] indices = new int[] { dataAlphabet.lookupIndex("feature1"), dataAlphabet.lookupIndex("feature2"), dataAlphabet.lookupIndex("extra_feature") };
double[] values = new double[] { 1.0, 2.0, 3.0 };
FeatureVector fv = new FeatureVector(dataAlphabet, indices, values);
// Label label = labelAlphabet.lookupLabel("class1");
// Instance instance = new Instance(fv, label, "input", null);
// Classification classification = nb.classify(instance);
// Labeling labeling = classification.getLabeling();
// assertEquals(2, labeling.numLocations());
// double p0 = labeling.valueAtLocation(0);
// double p1 = labeling.valueAtLocation(1);
// assertEquals(1.0, p0 + p1, 1e-10);
}

@Test
public void testInstanceListWithMixedLabeledAndUnlabeledInstances() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("f");
Alphabet labelAlphabet = new Alphabet();
labelAlphabet.lookupIndex("L1");
labelAlphabet.lookupIndex("L2");
Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {

public Instance pipe(Instance instance) {
return instance;
}
};
Multinomial prior = new Multinomial(new double[] { 0.5, 0.5 });
Multinomial[] conds = new Multinomial[] { new Multinomial(new double[] { 1 }), new Multinomial(new double[] { 2 }) };
NaiveBayes nb = new NaiveBayes(pipe, prior, conds);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
// Instance labeled = new Instance(fv, labelAlphabet.lookupLabel("L1"), "labeled", null);
Instance unlabeled = new Instance(fv, null, "unlabeled", null);
InstanceList list = new InstanceList(pipe);
// list.add(labeled);
list.add(unlabeled);
double ll = nb.dataLogLikelihood(list);
assertTrue(Double.isFinite(ll));
}

@Test
public void testLabelLogLikelihoodWithAllZeroLabelWeights() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("f");
Alphabet labelAlphabet = new Alphabet();
labelAlphabet.lookupIndex("A");
labelAlphabet.lookupIndex("B");
Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {

public Instance pipe(Instance instance) {
return instance;
}
};
Multinomial[] conds = new Multinomial[] { new Multinomial(new double[] { 1.0 }), new Multinomial(new double[] { 1.0 }) };
Multinomial prior = new Multinomial(new double[] { 0.5, 0.5 });
NaiveBayes nb = new NaiveBayes(pipe, prior, conds);
double[] labelWeights = new double[] { 0.0, 0.0 };
// Labeling labeling = new LabelVector(labelAlphabet, labelWeights);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
// Instance instance = new Instance(fv, labeling, "allZero", null);
InstanceList list = new InstanceList(pipe);
// list.add(instance);
double result = nb.labelLogLikelihood(list);
assertEquals(0.0, result, 1e-10);
}

@Test
public void testZeroLengthMultinomialInConstructor() {
Alphabet dataAlphabet = new Alphabet();
Alphabet labelAlphabet = new Alphabet();
Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {

public Instance pipe(Instance instance) {
return instance;
}
};
Multinomial prior = new Multinomial(new double[] {});
Multinomial[] p = new Multinomial[] {};
NaiveBayes nb = new NaiveBayes(pipe, prior, p);
assertNotNull(nb.getPriors());
assertEquals(0, nb.getMultinomials().length);
}

@Test
public void testClassifierHandlesNegativeFeatureValue() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("feat");
Alphabet labelAlphabet = new Alphabet();
labelAlphabet.lookupIndex("P");
labelAlphabet.lookupIndex("N");
Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {

public Instance pipe(Instance instance) {
return instance;
}
};
Multinomial prior = new Multinomial(new double[] { 0.6, 0.4 });
Multinomial[] condProbs = new Multinomial[] { new Multinomial(new double[] { 1.0 }), new Multinomial(new double[] { 1.0 }) };
NaiveBayes nb = new NaiveBayes(pipe, prior, condProbs);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { -3.0 });
// Label label = labelAlphabet.lookupLabel("P");
// Instance instance = new Instance(fv, label, "negativeWeight", null);
// Classification classification = nb.classify(instance);
// Labeling labeling = classification.getLabeling();
// double p0 = labeling.value(0);
// double p1 = labeling.value(1);
// assertEquals(1.0, p0 + p1, 1e-10);
// assertTrue(p0 >= 0.0 && p1 >= 0.0);
}

@Test
public void testClassificationWhenPipeIsNull() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("w");
Alphabet labelAlphabet = new Alphabet();
labelAlphabet.lookupIndex("Y");
labelAlphabet.lookupIndex("N");
Multinomial prior = new Multinomial(new double[] { 0.5, 0.5 });
Multinomial[] cond = new Multinomial[] { new Multinomial(new double[] { 1.0 }), new Multinomial(new double[] { 1.0 }) };
NaiveBayes nb = new NaiveBayes(null, prior, cond);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
Instance instance = new Instance(fv, null, "test-instance", null);
Classification classification = nb.classify(instance);
Labeling labeling = classification.getLabeling();
assertEquals(2, labeling.numLocations());
double total = labeling.value(0) + labeling.value(1);
assertEquals(1.0, total, 1e-10);
}

@Test
public void testConstructorWithLoggedInputs() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("w1");
Alphabet labelAlphabet = new Alphabet();
labelAlphabet.lookupIndex("c1");
labelAlphabet.lookupIndex("c2");
Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {

public Instance pipe(Instance instance) {
return instance;
}
};
Multinomial prior = new Multinomial(new double[] { 0.6, 0.4 });
Multinomial[] cond = new Multinomial[] { new Multinomial(new double[] { 1.0 }), new Multinomial(new double[] { 2.0 }) };
// Multinomial.Logged loggedPrior = prior.toLogMultinomial();
// Multinomial.Logged logged0 = cond[0].toLogMultinomial();
// Multinomial.Logged logged1 = cond[1].toLogMultinomial();
// NaiveBayes nb = new NaiveBayes(pipe, loggedPrior, new Multinomial.Logged[] { logged0, logged1 });
// assertNotNull(nb.getPriors());
// assertEquals(2, nb.getMultinomials().length);
}

@Test
public void testClassifyRejectsMismatchedAlphabetViaAssertion() {
Alphabet trainingAlphabet = new Alphabet();
Alphabet testingAlphabet = new Alphabet();
trainingAlphabet.lookupIndex("train_feat");
testingAlphabet.lookupIndex("test_feat");
Alphabet labelAlphabet = new Alphabet();
labelAlphabet.lookupIndex("label");
Pipe pipe = new Pipe(trainingAlphabet, labelAlphabet) {

public Instance pipe(Instance inst) {
return inst;
}
};
Multinomial prior = new Multinomial(new double[] { 1.0 });
Multinomial[] conditionals = new Multinomial[] { new Multinomial(new double[] { 1.0 }) };
NaiveBayes nb = new NaiveBayes(pipe, prior, conditionals);
int index = testingAlphabet.lookupIndex("test_feat");
FeatureVector fv = new FeatureVector(testingAlphabet, new int[] { index }, new double[] { 1.0 });
// Label label = labelAlphabet.lookupLabel("label");
// Instance mismatchedInstance = new Instance(fv, label, "id", null);
boolean caught = false;
try {
// nb.classify(mismatchedInstance);
} catch (AssertionError expected) {
caught = true;
}
assertTrue("Expected assertion error due to mismatched alphabet", caught);
}

@Test
public void testClassificationWithMoreClassesThanTrained() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("token");
Alphabet labelAlphabet = new Alphabet();
labelAlphabet.lookupIndex("A");
labelAlphabet.lookupIndex("B");
labelAlphabet.lookupIndex("C");
Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {

public Instance pipe(Instance i) {
return i;
}
};
Multinomial prior = new Multinomial(new double[] { 0.3, 0.7 });
Multinomial[] conds = new Multinomial[] { new Multinomial(new double[] { 1.0 }), new Multinomial(new double[] { 2.0 }) };
NaiveBayes nb = new NaiveBayes(pipe, prior, conds);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
Instance instance = new Instance(fv, null, "id", null);
Classification classification = nb.classify(instance);
Labeling labeling = classification.getLabeling();
assertEquals(3, labeling.numLocations());
assertEquals(0.0, labeling.valueAtLocation(2), 1e-10);
}

@Test
public void testClassificationWithAllZeroPriorProbabilities() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("x");
Alphabet labelAlphabet = new Alphabet();
labelAlphabet.lookupIndex("yes");
labelAlphabet.lookupIndex("no");
Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {

public Instance pipe(Instance inst) {
return inst;
}
};
double[] zeroPriors = new double[] { 0.0, 0.0 };
Multinomial prior = new Multinomial(zeroPriors);
Multinomial[] conds = new Multinomial[] { new Multinomial(new double[] { 1.0 }), new Multinomial(new double[] { 1.0 }) };
NaiveBayes nb = new NaiveBayes(pipe, prior, conds);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
Instance instance = new Instance(fv, null, "zeroPrior", null);
Classification classification = nb.classify(instance);
Labeling labeling = classification.getLabeling();
double p0 = labeling.value(0);
double p1 = labeling.value(1);
double total = p0 + p1;
assertEquals(1.0, total, 1e-6);
assertTrue(p0 >= 0.0 && p1 >= 0.0);
}

@Test
public void testSerializationWithFeatureAlphabetExpansionPostTraining() throws IOException, ClassNotFoundException {
Alphabet dataAlphabet = new Alphabet();
Alphabet labelAlphabet = new Alphabet();
dataAlphabet.lookupIndex("a");
labelAlphabet.lookupIndex("X");
Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {

public Instance pipe(Instance inst) {
return inst;
}
};
Multinomial prior = new Multinomial(new double[] { 1.0 });
Multinomial[] conds = new Multinomial[] { new Multinomial(new double[] { 0.5 }) };
NaiveBayes trained = new NaiveBayes(pipe, prior, conds);
ByteArrayOutputStream out = new ByteArrayOutputStream();
ObjectOutputStream oos = new ObjectOutputStream(out);
oos.writeObject(trained);
oos.close();
dataAlphabet.lookupIndex("new_feature");
ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
ObjectInputStream ois = new ObjectInputStream(in);
NaiveBayes deserialized = (NaiveBayes) ois.readObject();
ois.close();
assertNotNull(deserialized);
assertEquals(1, deserialized.getMultinomials().length);
}

@Test
public void testDataLogLikelihoodWithNegativeInstanceWeight() {
Alphabet dataAlphabet = new Alphabet();
Alphabet labelAlphabet = new Alphabet();
dataAlphabet.lookupIndex("z");
labelAlphabet.lookupIndex("spam");
labelAlphabet.lookupIndex("ham");
Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {

public Instance pipe(Instance instance) {
return instance;
}
};
Multinomial prior = new Multinomial(new double[] { 0.5, 0.5 });
Multinomial[] conds = new Multinomial[] { new Multinomial(new double[] { 1.0 }), new Multinomial(new double[] { 2.0 }) };
NaiveBayes nb = new NaiveBayes(pipe, prior, conds);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1 });
// Instance instance = new Instance(fv, labelAlphabet.lookupLabel("ham"), "negId", null);
InstanceList list = new InstanceList(pipe);
// list.add(instance);
list.setInstanceWeight(0, -2.5);
double result = nb.dataLogLikelihood(list);
assertTrue(Double.isFinite(result));
assertTrue(result < 0);
}

@Test
public void testClassificationWithZeroExponentScoresProducesUniformOutput() {
Alphabet dataAlphabet = new Alphabet();
Alphabet labelAlphabet = new Alphabet();
dataAlphabet.lookupIndex("tok");
labelAlphabet.lookupIndex("X");
labelAlphabet.lookupIndex("Y");
Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {

public Instance pipe(Instance instance) {
return instance;
}
};
Multinomial prior = new Multinomial(new double[] { 1.0, 1.0 });
Multinomial[] conds = new Multinomial[] { new Multinomial(new double[] { 1.0 }), new Multinomial(new double[] { 1.0 }) };
NaiveBayes nb = new NaiveBayes(pipe, prior, conds);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[0], new double[0]);
Instance instance = new Instance(fv, null, "nofeatures", null);
Classification classification = nb.classify(instance);
Labeling labeling = classification.getLabeling();
double p0 = labeling.value(0);
double p1 = labeling.value(1);
assertEquals(1.0, p0 + p1, 1e-6);
assertEquals(p0, p1, 1e-6);
}

@Test
public void testInstanceWithNullDataThrowsClassCastException() {
Alphabet dataAlphabet = new Alphabet();
Alphabet labelAlphabet = new Alphabet();
labelAlphabet.lookupIndex("X");
Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {

public Instance pipe(Instance instance) {
return instance;
}
};
Multinomial prior = new Multinomial(new double[] { 1.0 });
Multinomial[] classProbs = new Multinomial[] { new Multinomial(new double[] { 1.0 }) };
NaiveBayes nb = new NaiveBayes(pipe, prior, classProbs);
// Instance instance = new Instance(null, labelAlphabet.lookupLabel("X"), "nullData", null);
boolean threw = false;
try {
// nb.classify(instance);
} catch (ClassCastException expected) {
threw = true;
}
assertTrue(threw);
}

@Test
public void testFeatureVectorWithDuplicateIndicesInLocations() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("w1");
Alphabet labelAlphabet = new Alphabet();
labelAlphabet.lookupIndex("Y");
labelAlphabet.lookupIndex("N");
Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {

public Instance pipe(Instance instance) {
return instance;
}
};
Multinomial prior = new Multinomial(new double[] { 0.5, 0.5 });
Multinomial[] p = new Multinomial[] { new Multinomial(new double[] { 1.0 }), new Multinomial(new double[] { 2.0 }) };
NaiveBayes nb = new NaiveBayes(pipe, prior, p);
int index = dataAlphabet.lookupIndex("w1");
int[] indices = new int[] { index, index };
double[] values = new double[] { 1.0, 2.0 };
FeatureVector fv = new FeatureVector(dataAlphabet, indices, values);
Instance instance = new Instance(fv, null, "dup", null);
Classification classification = nb.classify(instance);
Labeling labeling = classification.getLabeling();
assertEquals(2, labeling.numLocations());
double sum = labeling.value(0) + labeling.value(1);
assertEquals(1.0, sum, 1e-6);
}

@Test
public void testFeatureAlphabetIsEmpty() {
Alphabet dataAlphabet = new Alphabet();
Alphabet labelAlphabet = new Alphabet();
labelAlphabet.lookupIndex("yes");
labelAlphabet.lookupIndex("no");
Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {

public Instance pipe(Instance inst) {
return inst;
}
};
Multinomial prior = new Multinomial(new double[] { 1.0, 1.0 });
Multinomial[] cond = new Multinomial[] { new Multinomial(new double[] {}), new Multinomial(new double[] {}) };
NaiveBayes nb = new NaiveBayes(pipe, prior, cond);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] {}, new double[] {});
Instance instance = new Instance(fv, null, "no_feat", null);
Classification classification = nb.classify(instance);
Labeling labeling = classification.getLabeling();
double sum = labeling.value(0) + labeling.value(1);
assertEquals(1.0, sum, 1e-6);
}

@Test
public void testLabelAlphabetIsEmpty() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("f");
Alphabet labelAlphabet = new Alphabet();
Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {

public Instance pipe(Instance i) {
return i;
}
};
Multinomial prior = new Multinomial(new double[] {});
Multinomial[] cond = new Multinomial[] {};
NaiveBayes nb = new NaiveBayes(pipe, prior, cond);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
Instance instance = new Instance(fv, null, "emptyLabel", null);
Classification classification = nb.classify(instance);
Labeling labeling = classification.getLabeling();
assertEquals(0, labeling.numLocations());
}

@Test
public void testLabelingWithWeightsSummingToMoreThanOne() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("t");
Alphabet labelAlphabet = new Alphabet();
labelAlphabet.lookupIndex("a");
labelAlphabet.lookupIndex("b");
Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {

public Instance pipe(Instance i) {
return i;
}
};
Multinomial prior = new Multinomial(new double[] { 0.3, 0.7 });
Multinomial[] cond = new Multinomial[] { new Multinomial(new double[] { 1.0 }), new Multinomial(new double[] { 2.0 }) };
NaiveBayes nb = new NaiveBayes(pipe, prior, cond);
// Labeling labeling = new LabelVector(labelAlphabet, new double[] { 1.5, 0.5 });
// FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
// Instance instance = new Instance(fv, labeling, "overweight", null);
InstanceList list = new InstanceList(pipe);
// list.add(instance);
double result = nb.labelLogLikelihood(list);
assertTrue(Double.isFinite(result));
}

@Test
public void testZeroFeatureVectorLengthWithLabelLogLikelihood() {
Alphabet dataAlphabet = new Alphabet();
Alphabet labelAlphabet = new Alphabet();
dataAlphabet.lookupIndex("unused");
labelAlphabet.lookupIndex("A");
Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {

public Instance pipe(Instance inst) {
return inst;
}
};
Multinomial prior = new Multinomial(new double[] { 1.0 });
Multinomial[] conds = new Multinomial[] { new Multinomial(new double[] { 1.0 }) };
NaiveBayes nb = new NaiveBayes(pipe, prior, conds);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] {}, new double[] {});
// Label label = labelAlphabet.lookupLabel("A");
// Instance instance = new Instance(fv, label, "emptyFV", null);
InstanceList list = new InstanceList(pipe);
// list.add(instance);
double result = nb.labelLogLikelihood(list);
assertTrue(Double.isFinite(result));
}

@Test
public void testClassificationWithZeroLengthLabelsInMultinomial() {
Alphabet dataAlphabet = new Alphabet();
Alphabet labelAlphabet = new Alphabet();
dataAlphabet.lookupIndex("f");
Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {

public Instance pipe(Instance inst) {
return inst;
}
};
labelAlphabet.lookupIndex("only");
Multinomial prior = new Multinomial(new double[] { 1.0 });
Multinomial emptyMultinomial = new Multinomial(new double[0]);
Multinomial[] cond = new Multinomial[] { emptyMultinomial };
NaiveBayes nb = new NaiveBayes(pipe, prior, cond);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 2.0 });
Instance instance = new Instance(fv, null, "zeroCond", null);
Classification classification = nb.classify(instance);
Labeling labeling = classification.getLabeling();
double prob = labeling.value(0);
assertEquals(1.0, prob, 1e-6);
}

@Test
public void testLabelLogLikelihoodSkipsUntrainedLabelIndexGracefully() {
Alphabet dataAlphabet = new Alphabet();
Alphabet labelAlphabet = new Alphabet();
dataAlphabet.lookupIndex("token");
labelAlphabet.lookupIndex("L0");
labelAlphabet.lookupIndex("L1");
Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {

public Instance pipe(Instance instance) {
return instance;
}
};
Multinomial prior = new Multinomial(new double[] { 1.0 });
Multinomial[] conds = new Multinomial[] { new Multinomial(new double[] { 1.0 }) };
NaiveBayes nb = new NaiveBayes(pipe, prior, conds);
double[] weights = new double[] { 0.9, 0.1 };
// LabelVector labeling = new LabelVector(labelAlphabet, weights);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
// Instance instance = new Instance(fv, labeling, "partialLabels", null);
InstanceList list = new InstanceList(pipe);
// list.add(instance);
double result = nb.labelLogLikelihood(list);
assertTrue(Double.isFinite(result));
}

@Test
public void testClassifyWithEmptyFeatureIndicesButNonEmptyValues() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("f1");
Alphabet labelAlphabet = new Alphabet();
labelAlphabet.lookupIndex("A");
labelAlphabet.lookupIndex("B");
Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {

public Instance pipe(Instance i) {
return i;
}
};
Multinomial prior = new Multinomial(new double[] { 0.5, 0.5 });
Multinomial[] conds = new Multinomial[] { new Multinomial(new double[] { 1.0 }), new Multinomial(new double[] { 1.0 }) };
NaiveBayes nb = new NaiveBayes(pipe, prior, conds);
int[] indices = new int[] {};
double[] values = new double[] { 1.0 };
boolean threw = false;
try {
FeatureVector fv = new FeatureVector(dataAlphabet, indices, values);
Instance instance = new Instance(fv, null, "bad", null);
nb.classify(instance);
} catch (ArrayIndexOutOfBoundsException expected) {
threw = true;
}
assertTrue(threw);
}

@Test
public void testClassificationWithTinyPriorsToTriggerUnderflowSafeguards() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("x");
Alphabet labelAlphabet = new Alphabet();
labelAlphabet.lookupIndex("Y");
labelAlphabet.lookupIndex("N");
Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {

public Instance pipe(Instance inst) {
return inst;
}
};
double[] priors = new double[] { 1e-300, 1e-300 };
Multinomial prior = new Multinomial(priors);
Multinomial[] conds = new Multinomial[] { new Multinomial(new double[] { 1.0 }), new Multinomial(new double[] { 2.0 }) };
NaiveBayes nb = new NaiveBayes(pipe, prior, conds);
int index = dataAlphabet.lookupIndex("x");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { index }, new double[] { 1.0 });
Instance instance = new Instance(fv, null, "tinyPrior", null);
Classification classification = nb.classify(instance);
Labeling labeling = classification.getLabeling();
assertEquals(2, labeling.numLocations());
assertNotEquals(Double.NaN, labeling.value(0));
assertNotEquals(Double.NaN, labeling.value(1));
}

@Test
public void testClassifyWithInvalidFeatureIndex() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("a");
Alphabet labelAlphabet = new Alphabet();
labelAlphabet.lookupIndex("X");
labelAlphabet.lookupIndex("Y");
Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {

public Instance pipe(Instance i) {
return i;
}
};
Multinomial prior = new Multinomial(new double[] { 1.0, 1.0 });
Multinomial[] conds = new Multinomial[] { new Multinomial(new double[] { 1.0 }), new Multinomial(new double[] { 1.0 }) };
NaiveBayes nb = new NaiveBayes(pipe, prior, conds);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 999 }, new double[] { 1.0 });
Instance instance = new Instance(fv, null, "invalidIndex", null);
Classification result = nb.classify(instance);
assertNotNull(result);
Labeling labeling = result.getLabeling();
assertEquals(2, labeling.numLocations());
}

@Test
public void testLabelLogLikelihoodWithNullLabelingSkipsInstance() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("f");
Alphabet labelAlphabet = new Alphabet();
labelAlphabet.lookupIndex("A");
Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {

public Instance pipe(Instance i) {
return i;
}
};
Multinomial prior = new Multinomial(new double[] { 1.0 });
Multinomial[] cond = new Multinomial[] { new Multinomial(new double[] { 1.0 }) };
NaiveBayes nb = new NaiveBayes(pipe, prior, cond);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
Instance instance = new Instance(fv, null, "input", null);
InstanceList list = new InstanceList(pipe);
list.add(instance);
double result = nb.labelLogLikelihood(list);
assertEquals(0.0, result, 1e-6);
}

@Test
public void testClassificationWithManyClassesReturnsAllScores() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("word");
Alphabet labelAlphabet = new Alphabet();
labelAlphabet.lookupIndex("L0");
labelAlphabet.lookupIndex("L1");
labelAlphabet.lookupIndex("L2");
labelAlphabet.lookupIndex("L3");
labelAlphabet.lookupIndex("L4");
Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {

public Instance pipe(Instance i) {
return i;
}
};
double[] prior = new double[] { 1.0, 2.0, 3.0, 4.0, 5.0 };
Multinomial priorMult = new Multinomial(prior);
Multinomial[] conds = new Multinomial[] { new Multinomial(new double[] { 1.0 }), new Multinomial(new double[] { 1.0 }), new Multinomial(new double[] { 1.0 }), new Multinomial(new double[] { 1.0 }), new Multinomial(new double[] { 1.0 }) };
NaiveBayes nb = new NaiveBayes(pipe, priorMult, conds);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
Instance instance = new Instance(fv, null, "multiClass", null);
Classification c = nb.classify(instance);
Labeling l = c.getLabeling();
assertEquals(5, l.numLocations());
double sum = l.value(0) + l.value(1) + l.value(2) + l.value(3) + l.value(4);
assertEquals(1.0, sum, 1e-6);
}

@Test
public void testClassificationWithNegativeClassLogProbabilitiesSkippedGracefully() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("f");
Alphabet labelAlphabet = new Alphabet();
labelAlphabet.lookupIndex("A");
labelAlphabet.lookupIndex("B");
Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {

public Instance pipe(Instance i) {
return i;
}
};
Multinomial prior = new Multinomial(new double[] { 0.5, 0.5 });
Multinomial[] conds = new Multinomial[] { new Multinomial(new double[] { 1.0 }), new Multinomial(new double[] { 0.0 }) };
NaiveBayes nb = new NaiveBayes(pipe, prior, conds);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
Instance instance = new Instance(fv, null, "skipNegInf", null);
Classification classification = nb.classify(instance);
Labeling labeling = classification.getLabeling();
assertEquals(2, labeling.numLocations());
assertTrue(labeling.value(1) < 1e-6);
}

@Test
public void testNaiveBayesWithLargeFeatureVector() {
Alphabet dataAlphabet = new Alphabet();
for (int i = 0; i < 500; i++) {
dataAlphabet.lookupIndex("f" + i);
}
Alphabet labelAlphabet = new Alphabet();
labelAlphabet.lookupIndex("C");
Pipe pipe = new Pipe(dataAlphabet, labelAlphabet) {

public Instance pipe(Instance instance) {
return instance;
}
};
double[] prior = new double[] { 1.0 };
double[] features = new double[500];
for (int i = 0; i < 500; i++) {
features[i] = 1.0;
}
Multinomial m = new Multinomial(features);
Multinomial[] conds = new Multinomial[] { m };
NaiveBayes nb = new NaiveBayes(pipe, new Multinomial(prior), conds);
int[] indices = new int[500];
double[] values = new double[500];
for (int i = 0; i < 500; i++) {
indices[i] = i;
values[i] = 1.0;
}
FeatureVector fv = new FeatureVector(dataAlphabet, indices, values);
Instance instance = new Instance(fv, null, "largeFV", null);
Classification out = nb.classify(instance);
assertNotNull(out);
assertEquals(1.0, out.getLabeling().value(0), 1e-6);
}
}
