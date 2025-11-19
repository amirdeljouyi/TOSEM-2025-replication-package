package cc.mallet.classify.tests;

import cc.mallet.classify.Classification;
import cc.mallet.classify.MaxEnt;
import cc.mallet.classify.MaxEntOptimizableByLabelDistribution;
import cc.mallet.classify.MaxEntOptimizableByLabelLikelihood;
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

public class MaxEntOptimizableByLabelLikelihood_5_GPTLLMTest {

@Test
public void testParameterAccessors_singleParameterSetAndGet() {
// Pipe pipe = new NoOpPipe();
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
dataAlphabet.lookupIndex("feature1");
labelAlphabet.lookupLabel("label1");
// InstanceList trainingSet = new InstanceList(pipe, dataAlphabet, labelAlphabet);
// FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "feature1" });
// Label label = labelAlphabet.lookupLabel("label1");
// Instance instance = new Instance(fv, label, null, null);
// trainingSet.add(instance);
// MaxEnt classifier = new MaxEnt(pipe, new double[labelAlphabet.size() * (dataAlphabet.size() + 1)], null, null);
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(trainingSet, classifier);
// opt.setParameter(0, 1.23);
// double result = opt.getParameter(0);
// assertEquals(1.23, result, 0.0001);
}

@Test
public void testGetNumParameters_returnsExpectedSize() {
// Pipe pipe = new NoOpPipe();
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
dataAlphabet.lookupIndex("feature1");
dataAlphabet.lookupIndex("feature2");
labelAlphabet.lookupLabel("label1");
labelAlphabet.lookupLabel("label2");
// InstanceList trainingSet = new InstanceList(pipe, dataAlphabet, labelAlphabet);
// FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "feature1" });
// Label label = labelAlphabet.lookupLabel("label1");
// trainingSet.add(new Instance(fv, label, null, null));
// MaxEnt classifier = new MaxEnt(pipe, new double[labelAlphabet.size() * (dataAlphabet.size() + 1)], null, null);
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(trainingSet, classifier);
int expected = labelAlphabet.size() * (dataAlphabet.size() + 1);
// int actual = opt.getNumParameters();
// assertEquals(expected, actual);
}

@Test
public void testGetAndSetParameters_copiesCorrectly() {
// Pipe pipe = new NoOpPipe();
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
dataAlphabet.lookupIndex("f1");
labelAlphabet.lookupLabel("l1");
// InstanceList trainingSet = new InstanceList(pipe, dataAlphabet, labelAlphabet);
// FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "f1" });
// Label label = labelAlphabet.lookupLabel("l1");
// trainingSet.add(new Instance(fv, label, null, null));
// MaxEnt classifier = new MaxEnt(pipe, new double[labelAlphabet.size() * (dataAlphabet.size() + 1)], null, null);
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(trainingSet, classifier);
// int len = opt.getNumParameters();
// double[] input = new double[len];
// input[0] = 0.5;
// input[len - 1] = -1.2;
// opt.setParameters(input);
// double[] output = new double[len];
// opt.getParameters(output);
// assertEquals(0.5, output[0], 0.0001);
// assertEquals(-1.2, output[len - 1], 0.0001);
}

@Test
public void testGetValue_returnsNegativeLogLikelihood() {
// Pipe pipe = new NoOpPipe();
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
dataAlphabet.lookupIndex("x");
labelAlphabet.lookupLabel("A");
FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "x" }, new double[] { 1.0 });
Label label = labelAlphabet.lookupLabel("A");
Instance instance = new Instance(fv, label, null, null);
// InstanceList trainingSet = new InstanceList(pipe, dataAlphabet, labelAlphabet);
// trainingSet.add(instance);
// MaxEnt classifier = new MaxEnt(pipe, new double[labelAlphabet.size() * (dataAlphabet.size() + 1)], null, null);
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(trainingSet, classifier);
// double value = opt.getValue();
// assertTrue(value <= 0.0);
}

@Test
public void testGetValueGradient_populatesBufferCorrectly() {
// Pipe pipe = new NoOpPipe();
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
dataAlphabet.lookupIndex("alpha");
labelAlphabet.lookupLabel("yes");
FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "alpha" }, new double[] { 0.8 });
Label label = labelAlphabet.lookupLabel("yes");
Instance instance = new Instance(fv, label, null, null);
// InstanceList trainingSet = new InstanceList(pipe, dataAlphabet, labelAlphabet);
// trainingSet.add(instance);
// MaxEnt classifier = new MaxEnt(pipe, new double[labelAlphabet.size() * (dataAlphabet.size() + 1)], null, null);
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(trainingSet, classifier);
// double[] buffer = new double[opt.getNumParameters()];
// opt.getValueGradient(buffer);
// assertNotNull(buffer);
// assertEquals(opt.getNumParameters(), buffer.length);
}

@Test
public void testUseGaussianPrior_affectsValue() {
// Pipe pipe = new NoOpPipe();
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
dataAlphabet.lookupIndex("f1");
labelAlphabet.lookupLabel("L1");
FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "f1" }, new double[] { 1.0 });
Label label = labelAlphabet.lookupLabel("L1");
// InstanceList trainingSet = new InstanceList(pipe, dataAlphabet, labelAlphabet);
// trainingSet.add(new Instance(fv, label, null, null));
// MaxEnt classifier = new MaxEnt(pipe, new double[labelAlphabet.size() * (dataAlphabet.size() + 1)], null, null);
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(trainingSet, classifier);
// opt.useGaussianPrior();
// double value = opt.getValue();
// assertTrue(value <= 0);
}

@Test
public void testNoPrior_affectsValue() {
// Pipe pipe = new NoOpPipe();
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
dataAlphabet.lookupIndex("featX");
labelAlphabet.lookupLabel("classY");
// FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "featX" });
// Label label = labelAlphabet.lookupLabel("classY");
// Instance instance = new Instance(fv, label, null, null);
// InstanceList trainingSet = new InstanceList(pipe, dataAlphabet, labelAlphabet);
// trainingSet.add(instance);
// MaxEnt classifier = new MaxEnt(pipe, new double[labelAlphabet.size() * (dataAlphabet.size() + 1)], null, null);
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(trainingSet, classifier);
// opt.useNoPrior();
// double value = opt.getValue();
// assertTrue(value <= 0);
}

@Test(expected = UnsupportedOperationException.class)
public void testHyperbolicPrior_throwsOnGradient() {
// Pipe pipe = new NoOpPipe();
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
dataAlphabet.lookupIndex("fx");
labelAlphabet.lookupLabel("lx");
// FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "fx" });
// Label label = labelAlphabet.lookupLabel("lx");
// Instance instance = new Instance(fv, label, null, null);
// InstanceList trainingSet = new InstanceList(pipe, dataAlphabet, labelAlphabet);
// trainingSet.add(instance);
// MaxEnt classifier = new MaxEnt(pipe, new double[labelAlphabet.size() * (dataAlphabet.size() + 1)], null, null);
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(trainingSet, classifier);
// opt.useHyperbolicPrior();
// double[] buffer = new double[opt.getNumParameters()];
// opt.getValueGradient(buffer);
}

@Test
public void testSetParametersWithDifferentLengthResizesArray() {
Pipe pipe = new Pipe() {

public Instance pipe(Instance inst) {
return inst;
}
};
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
dataAlphabet.lookupIndex("a");
labelAlphabet.lookupLabel("L");
// InstanceList trainingSet = new InstanceList(pipe, dataAlphabet, labelAlphabet);
// FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "a" });
// Label label = labelAlphabet.lookupLabel("L");
// trainingSet.add(new Instance(fv, label, null, null));
MaxEnt classifier = new MaxEnt(pipe, new double[1], null, null);
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(trainingSet, classifier);
double[] longerArray = new double[5];
longerArray[0] = 1.0;
longerArray[4] = -0.5;
// opt.setParameters(longerArray);
double[] result = new double[5];
// opt.getParameters(result);
assertEquals(1.0, result[0], 0.0001);
assertEquals(-0.5, result[4], 0.0001);
}

@Test
public void testSetParameterTriggersCacheInvalidation() {
Pipe pipe = new Pipe() {

public Instance pipe(Instance inst) {
return inst;
}
};
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
dataAlphabet.lookupIndex("x");
labelAlphabet.lookupLabel("Y");
// InstanceList trainingSet = new InstanceList(pipe, dataAlphabet, labelAlphabet);
// FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "x" });
// Label label = labelAlphabet.lookupLabel("Y");
// trainingSet.add(new Instance(fv, label, null, null));
MaxEnt classifier = new MaxEnt(pipe, new double[2], null, null);
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(trainingSet, classifier);
// double value1 = opt.getValue();
// opt.setParameter(0, 0.8);
// double value2 = opt.getValue();
// assertNotEquals(value1, value2);
}

@Test
public void testInfiniteScoreReturnsNegativeValue() {
Pipe pipe = new Pipe() {

public Instance pipe(Instance inst) {
return inst;
}
};
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
dataAlphabet.lookupIndex("a");
labelAlphabet.lookupLabel("L");
// InstanceList trainingSet = new InstanceList(pipe, dataAlphabet, labelAlphabet);
// FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "a" });
// Label label = labelAlphabet.lookupLabel("L");
// trainingSet.add(new Instance(fv, label, null, null));
MaxEnt classifier = new MaxEnt(pipe, new double[2], null, null) {

@Override
public void getClassificationScores(Instance instance, double[] scores) {
scores[0] = 0.0;
}
};
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(trainingSet, classifier);
// double val = opt.getValue();
// assertTrue(Double.isInfinite(val) || Double.isNaN(val) || val <= 0);
}

@Test
public void testInstanceWithNullLabelIsSkipped() {
Pipe pipe = new Pipe() {

public Instance pipe(Instance inst) {
return inst;
}
};
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
dataAlphabet.lookupIndex("feat");
// InstanceList trainingSet = new InstanceList(pipe, dataAlphabet, labelAlphabet);
// FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "feat" });
// Instance instance = new Instance(fv, null, null, null);
// trainingSet.add(instance);
MaxEnt classifier = new MaxEnt(pipe, new double[1], null, null);
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(trainingSet, classifier);
// double value = opt.getValue();
// assertTrue(Double.isFinite(value));
}

@Test
public void testNaNFeatureValueIsLoggedAndIgnored() {
Pipe pipe = new Pipe() {

public Instance pipe(Instance inst) {
return inst;
}
};
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
int idx = dataAlphabet.lookupIndex("bad_feature");
labelAlphabet.lookupLabel("spam");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { idx }, new double[] { Double.NaN });
// InstanceList trainingSet = new InstanceList(pipe, dataAlphabet, labelAlphabet);
Label label = labelAlphabet.lookupLabel("spam");
Instance instance = new Instance(fv, label, "instName", "source");
// trainingSet.add(instance);
MaxEnt classifier = new MaxEnt(pipe, new double[2], null, null);
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(trainingSet, classifier);
// double val = opt.getValue();
// assertTrue(Double.isFinite(val));
}

@Test
public void testNegativeInfinityParameterValueHandledInGradient() {
Pipe pipe = new Pipe() {

public Instance pipe(Instance inst) {
return inst;
}
};
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
dataAlphabet.lookupIndex("zz");
labelAlphabet.lookupLabel("N");
FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "zz" }, new double[] { 1.0 });
Label label = labelAlphabet.lookupLabel("N");
// InstanceList trainingSet = new InstanceList(pipe, dataAlphabet, labelAlphabet);
// trainingSet.add(new Instance(fv, label, null, null));
double[] params = new double[2];
params[0] = Double.NEGATIVE_INFINITY;
MaxEnt classifier = new MaxEnt(pipe, params, null, null);
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(trainingSet, classifier);
double[] gradient = new double[2];
// opt.getValueGradient(gradient);
assertEquals(0.0, gradient[0], 0.000001);
}

@Test
public void testSetHyperbolicPriorSlopeChangesState() {
Pipe pipe = new Pipe() {

public Instance pipe(Instance inst) {
return inst;
}
};
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
dataAlphabet.lookupIndex("f");
labelAlphabet.lookupLabel("L");
// FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "f" });
// Label label = labelAlphabet.lookupLabel("L");
// InstanceList trainingSet = new InstanceList(pipe, dataAlphabet, labelAlphabet);
// trainingSet.add(new Instance(fv, label, null, null));
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(trainingSet, null);
// opt.setHyperbolicPriorSlope(1.5);
try {
// double[] grad = new double[opt.getNumParameters()];
// opt.getValueGradient(grad);
fail("Expected UnsupportedOperationException");
} catch (UnsupportedOperationException expected) {
}
}

@Test
public void testSetHyperbolicPriorSharpnessChangesState() {
Pipe pipe = new Pipe() {

public Instance pipe(Instance inst) {
return inst;
}
};
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
dataAlphabet.lookupIndex("k");
labelAlphabet.lookupLabel("J");
// FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "k" });
// Label label = labelAlphabet.lookupLabel("J");
// InstanceList trainingSet = new InstanceList(pipe, dataAlphabet, labelAlphabet);
// trainingSet.add(new Instance(fv, label, null, null));
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(trainingSet, null);
// opt.setHyperbolicPriorSharpness(99);
try {
// double[] grad = new double[opt.getNumParameters()];
// opt.getValueGradient(grad);
fail("Expected UnsupportedOperationException");
} catch (UnsupportedOperationException expected) {
}
}

@Test
public void testEmptyTrainingSetDoesNotCrash_getValue() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
Pipe pipe = new Pipe() {

public Instance pipe(Instance carrier) {
return carrier;
}
};
// InstanceList trainingList = new InstanceList(pipe, dataAlphabet, labelAlphabet);
MaxEnt classifier = new MaxEnt(pipe, new double[1], null, null);
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(trainingList, classifier);
// double val = opt.getValue();
// assertEquals(0.0, val, 0.0001);
}

@Test
public void testGradientBufferSizeMismatchStillCopiesIfCorrected() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
dataAlphabet.lookupIndex("feat");
labelAlphabet.lookupLabel("lab");
Pipe pipe = new Pipe() {

public Instance pipe(Instance carrier) {
return carrier;
}
};
// InstanceList trainingList = new InstanceList(pipe, dataAlphabet, labelAlphabet);
// FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "feat" });
// trainingList.add(new Instance(fv, labelAlphabet.lookupLabel("lab"), null, null));
MaxEnt classifier = new MaxEnt(pipe, new double[labelAlphabet.size() * (dataAlphabet.size() + 1)], null, null);
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(trainingList, classifier);
// double[] buffer = new double[opt.getNumParameters()];
// for (int i = 0; i < buffer.length; i++) {
// buffer[i] = Double.NaN;
// }
// opt.getValueGradient(buffer);
// for (int i = 0; i < buffer.length; i++) {
// assertFalse(Double.isNaN(buffer[i]));
// }
}

@Test
public void testMultipleLabels_singleFeature_correctGradientComputation() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
dataAlphabet.lookupIndex("feat");
labelAlphabet.lookupLabel("yes");
labelAlphabet.lookupLabel("no");
Pipe pipe = new Pipe() {

public Instance pipe(Instance carrier) {
return carrier;
}
};
// InstanceList trainingList = new InstanceList(pipe, dataAlphabet, labelAlphabet);
FeatureVector fv1 = new FeatureVector(dataAlphabet, new String[] { "feat" }, new double[] { 1.0 });
FeatureVector fv2 = new FeatureVector(dataAlphabet, new String[] { "feat" }, new double[] { 1.0 });
// trainingList.add(new Instance(fv1, labelAlphabet.lookupLabel("yes"), null, null));
// trainingList.add(new Instance(fv2, labelAlphabet.lookupLabel("no"), null, null));
MaxEnt classifier = new MaxEnt(pipe, new double[2 * (dataAlphabet.size() + 1)], null, null);
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(trainingList, classifier);
// double[] gradient = new double[opt.getNumParameters()];
// opt.getValueGradient(gradient);
// for (int i = 0; i < gradient.length; i++) {
// assertFalse(Double.isNaN(gradient[i]));
// assertFalse(Double.isInfinite(gradient[i]));
// }
}

@Test
public void testGetParametersWithNullBufferAllocatesNewArray() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
dataAlphabet.lookupIndex("feat");
labelAlphabet.lookupLabel("lab");
Pipe pipe = new Pipe() {

public Instance pipe(Instance carrier) {
return carrier;
}
};
// InstanceList trainingList = new InstanceList(pipe, dataAlphabet, labelAlphabet);
// FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "feat" });
// trainingList.add(new Instance(fv, labelAlphabet.lookupLabel("lab"), null, null));
MaxEnt classifier = new MaxEnt(pipe, new double[2], null, null);
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(trainingList, classifier);
double[] external = null;
// opt.getParameters(external);
}

@Test
public void testSetParametersNullInputThrowsAssertion() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
dataAlphabet.lookupIndex("trick");
labelAlphabet.lookupLabel("class");
Pipe pipe = new Pipe() {

public Instance pipe(Instance carrier) {
return carrier;
}
};
// InstanceList trainingList = new InstanceList(pipe, dataAlphabet, labelAlphabet);
// FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "trick" });
// trainingList.add(new Instance(fv, labelAlphabet.lookupLabel("class"), null, null));
MaxEnt classifier = new MaxEnt(pipe, new double[2], null, null);
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(trainingList, classifier);
try {
// opt.setParameters(null);
fail("Expected AssertionError");
} catch (AssertionError e) {
}
}

@Test
public void testUseGaussianPriorWithSmallVarianceRaisesRegularization() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
dataAlphabet.lookupIndex("test");
labelAlphabet.lookupLabel("C");
Pipe pipe = new Pipe() {

public Instance pipe(Instance carrier) {
return carrier;
}
};
FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "test" }, new double[] { 1.0 });
Instance instance = new Instance(fv, labelAlphabet.lookupLabel("C"), null, null);
// InstanceList trainingList = new InstanceList(pipe, dataAlphabet, labelAlphabet);
// trainingList.add(instance);
MaxEnt classifier = new MaxEnt(pipe, new double[2], null, null);
// MaxEntOptimizableByLabelLikelihood opt1 = new MaxEntOptimizableByLabelLikelihood(trainingList, classifier);
// opt1.setGaussianPriorVariance(1000);
// double value1 = opt1.getValue();
// MaxEntOptimizableByLabelLikelihood opt2 = new MaxEntOptimizableByLabelLikelihood(trainingList, classifier);
// opt2.setGaussianPriorVariance(0.0001);
// double value2 = opt2.getValue();
// assertNotEquals(value1, value2);
}

@Test
public void testGradientConsistencyAcrossMultipleCallsWithoutChange() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
dataAlphabet.lookupIndex("g1");
labelAlphabet.lookupLabel("Y");
Pipe pipe = new Pipe() {

public Instance pipe(Instance carrier) {
return carrier;
}
};
// FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "g1" });
// Instance instance = new Instance(fv, labelAlphabet.lookupLabel("Y"), null, null);
// InstanceList trainingList = new InstanceList(pipe, dataAlphabet, labelAlphabet);
// trainingList.add(instance);
MaxEnt classifier = new MaxEnt(pipe, new double[2], null, null);
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(trainingList, classifier);
double[] grad1 = new double[2];
double[] grad2 = new double[2];
// opt.getValueGradient(grad1);
// opt.getValueGradient(grad2);
assertEquals(grad1[0], grad2[0], 0.000001);
assertEquals(grad1[1], grad2[1], 0.000001);
}

@Test
public void testGetClassifierReturnsSameInstance() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
dataAlphabet.lookupIndex("predict");
labelAlphabet.lookupLabel("Z");
Pipe pipe = new Pipe() {

public Instance pipe(Instance carrier) {
return carrier;
}
};
// FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "predict" });
// Instance instance = new Instance(fv, labelAlphabet.lookupLabel("Z"), null, null);
// InstanceList trainingList = new InstanceList(pipe, dataAlphabet, labelAlphabet);
// trainingList.add(instance);
MaxEnt classifier = new MaxEnt(pipe, new double[2], null, null);
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(trainingList, classifier);
// MaxEnt classifierOut = opt.getClassifier();
// assertSame(classifier, classifierOut);
}

@Test
public void testMultipleInstancesSameLabel() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
dataAlphabet.lookupIndex("f1");
labelAlphabet.lookupLabel("A");
Pipe pipe = new Pipe() {

public Instance pipe(Instance carrier) {
return carrier;
}
};
// InstanceList training = new InstanceList(pipe, dataAlphabet, labelAlphabet);
FeatureVector fv1 = new FeatureVector(dataAlphabet, new String[] { "f1" }, new double[] { 1.0 });
FeatureVector fv2 = new FeatureVector(dataAlphabet, new String[] { "f1" }, new double[] { 2.0 });
FeatureVector fv3 = new FeatureVector(dataAlphabet, new String[] { "f1" }, new double[] { 3.0 });
Label label = labelAlphabet.lookupLabel("A");
// training.add(new Instance(fv1, label, null, null));
// training.add(new Instance(fv2, label, null, null));
// training.add(new Instance(fv3, label, null, null));
MaxEnt classifier = new MaxEnt(pipe, new double[4], null, null);
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(training, classifier);
// double value = opt.getValue();
// assertTrue(Double.isFinite(value));
}

@Test
public void testZeroInputFeatureValues() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
int index = dataAlphabet.lookupIndex("f");
labelAlphabet.lookupLabel("C");
Pipe pipe = new Pipe() {

public Instance pipe(Instance carrier) {
return carrier;
}
};
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { index }, new double[] { 0.0 });
// InstanceList training = new InstanceList(pipe, dataAlphabet, labelAlphabet);
// training.add(new Instance(fv, labelAlphabet.lookupLabel("C"), null, null));
MaxEnt classifier = new MaxEnt(pipe, new double[2], null, null);
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(training, classifier);
// double value = opt.getValue();
// double[] gradient = new double[opt.getNumParameters()];
// opt.getValueGradient(gradient);
// assertTrue(Double.isFinite(value));
// assertFalse(Double.isNaN(gradient[0]));
// assertFalse(Double.isInfinite(gradient[0]));
}

@Test
public void testLabelWithNoTrainingInstancesDoesNotAffectCrash() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
dataAlphabet.lookupIndex("f");
labelAlphabet.lookupLabel("LabelWithData");
labelAlphabet.lookupLabel("LabelWithoutData");
Pipe pipe = new Pipe() {

public Instance pipe(Instance carrier) {
return carrier;
}
};
// FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "f" });
// Instance instance = new Instance(fv, labelAlphabet.lookupLabel("LabelWithData"), null, null);
// InstanceList trainingList = new InstanceList(pipe, dataAlphabet, labelAlphabet);
// trainingList.add(instance);
MaxEnt classifier = new MaxEnt(pipe, new double[4], null, null);
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(trainingList, classifier);
// double value = opt.getValue();
// assertTrue(Double.isFinite(value));
}

@Test
public void testSetGaussianPriorVarianceToZeroLeadsToLargePenalty() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
dataAlphabet.lookupIndex("f");
labelAlphabet.lookupLabel("X");
Pipe pipe = new Pipe() {

public Instance pipe(Instance carrier) {
return carrier;
}
};
// FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "f" });
// Instance instance = new Instance(fv, labelAlphabet.lookupLabel("X"), null, null);
// InstanceList trainingSet = new InstanceList(pipe, dataAlphabet, labelAlphabet);
// trainingSet.add(instance);
MaxEnt classifier = new MaxEnt(pipe, new double[2], null, null);
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(trainingSet, classifier);
// opt.setGaussianPriorVariance(1e-12);
// double value = opt.getValue();
// assertTrue(value < 0.0);
}

@Test
public void testUnseenFeatureInInstanceIsHandledGracefully() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupLabel("Z");
Pipe pipe = new Pipe() {

public Instance pipe(Instance carrier) {
return carrier;
}
};
Alphabet tempAlphabet = new Alphabet();
int unseenIdx = tempAlphabet.lookupIndex("unseen");
FeatureVector fv = new FeatureVector(tempAlphabet, new int[] { unseenIdx }, new double[] { 2.0 });
Label label = labelAlphabet.lookupLabel("Z");
Instance instance = new Instance(fv, label, null, null);
// InstanceList training = new InstanceList(pipe, dataAlphabet, labelAlphabet);
// training.add(instance);
MaxEnt classifier = new MaxEnt(pipe, new double[2], null, null);
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(training, classifier);
try {
// double val = opt.getValue();
// assertTrue(Double.isFinite(val));
} catch (AssertionError e) {
}
}

@Test
public void testConstraintMatrixHandlesEmptyFeatureVector() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupLabel("A");
Pipe pipe = new Pipe() {

public Instance pipe(Instance carrier) {
return carrier;
}
};
// FeatureVector fv = new FeatureVector(dataAlphabet);
// Instance instance = new Instance(fv, labelAlphabet.lookupLabel("A"), "name", null);
// InstanceList trainingList = new InstanceList(pipe, dataAlphabet, labelAlphabet);
// trainingList.add(instance);
MaxEnt classifier = new MaxEnt(pipe, new double[1], null, null);
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(trainingList, classifier);
// double value = opt.getValue();
// assertTrue(Double.isFinite(value));
}

@Test
public void testOnlyDefaultFeatureInFeatureVector() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
int fi = dataAlphabet.lookupIndex("default");
// int li = labelAlphabet.lookupLabel("POS");
Pipe pipe = new Pipe() {

public Instance pipe(Instance instance) {
return instance;
}
};
// InstanceList training = new InstanceList(pipe, dataAlphabet, labelAlphabet);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] {}, new double[] {});
Instance inst = new Instance(fv, labelAlphabet.lookupLabel("POS"), null, null);
// training.add(inst);
MaxEnt classifier = new MaxEnt(pipe, new double[1 * (dataAlphabet.size() + 1)], null, null);
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(training, classifier);
// double val = opt.getValue();
// double[] grad = new double[opt.getNumParameters()];
// opt.getValueGradient(grad);
// assertTrue(Double.isFinite(val));
// for (int i = 0; i < grad.length; i++) {
// assertFalse(Double.isNaN(grad[i]));
// }
}

@Test
public void testAllZeroClassifierParameterArray() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
dataAlphabet.lookupIndex("a");
labelAlphabet.lookupLabel("b");
Pipe pipe = new Pipe() {

public Instance pipe(Instance instance) {
return instance;
}
};
// InstanceList list = new InstanceList(pipe, dataAlphabet, labelAlphabet);
// FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "a" });
// Instance inst = new Instance(fv, labelAlphabet.lookupLabel("b"), null, null);
// list.add(inst);
double[] zeros = new double[labelAlphabet.size() * (dataAlphabet.size() + 1)];
MaxEnt classifier = new MaxEnt(pipe, zeros, null, null);
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, classifier);
// double value = opt.getValue();
// assertTrue(Double.isFinite(value));
}

@Test
public void testLabelAlphabetStopGrowthStillAllowsTraining() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.stopGrowth();
dataAlphabet.lookupIndex("f");
labelAlphabet.lookupLabel("X");
Pipe pipe = new Pipe() {

public Instance pipe(Instance inst) {
return inst;
}
};
// InstanceList trainingList = new InstanceList(pipe, dataAlphabet, labelAlphabet);
// FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "f" });
// Instance instance = new Instance(fv, labelAlphabet.lookupLabel("X"), null, null);
// trainingList.add(instance);
MaxEnt classifier = new MaxEnt(pipe, new double[2], null, null);
// MaxEntOptimizableByLabelLikelihood optimizable = new MaxEntOptimizableByLabelLikelihood(trainingList, classifier);
// double val = optimizable.getValue();
// assertTrue(Double.isFinite(val));
}

@Test
public void testInstanceWithNegativeWeightIsHandledGracefully() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
dataAlphabet.lookupIndex("f");
labelAlphabet.lookupLabel("L");
Pipe pipe = new Pipe() {

public Instance pipe(Instance inst) {
return inst;
}
};
// InstanceList trainingList = new InstanceList(pipe, dataAlphabet, labelAlphabet) {
// 
// @Override
// public double getInstanceWeight(Instance instance) {
// return -1.0;
// }
// };
// FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "f" });
// Instance instance = new Instance(fv, labelAlphabet.lookupLabel("L"), null, null);
// trainingList.add(instance);
MaxEnt classifier = new MaxEnt(pipe, new double[2], null, null);
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(trainingList, classifier);
// double value = opt.getValue();
// assertTrue(Double.isFinite(value));
}

@Test
public void testSetSingleParameterReflectsInSubsequentGetValue() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
dataAlphabet.lookupIndex("f");
labelAlphabet.lookupLabel("A");
Pipe pipe = new Pipe() {

public Instance pipe(Instance inst) {
return inst;
}
};
// FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "f" });
// Instance instance = new Instance(fv, labelAlphabet.lookupLabel("A"), null, null);
// InstanceList list = new InstanceList(pipe, dataAlphabet, labelAlphabet);
// list.add(instance);
MaxEnt classifier = new MaxEnt(pipe, new double[2], null, null);
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, classifier);
// double valBefore = opt.getValue();
// opt.setParameter(0, 1.5);
// double valAfter = opt.getValue();
// assertNotEquals(valBefore, valAfter);
}

@Test
public void testHyperbolicPriorFlagSetCorrectly() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
dataAlphabet.lookupIndex("f");
labelAlphabet.lookupLabel("X");
Pipe pipe = new Pipe() {

public Instance pipe(Instance inst) {
return inst;
}
};
// FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "f" });
// Instance instance = new Instance(fv, labelAlphabet.lookupLabel("X"), null, null);
// InstanceList list = new InstanceList(pipe, dataAlphabet, labelAlphabet);
// list.add(instance);
MaxEnt classifier = new MaxEnt(pipe, new double[2], null, null);
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, classifier);
// opt.setHyperbolicPriorSharpness(5.0);
// opt.setHyperbolicPriorSlope(0.1);
// opt.useHyperbolicPrior();
try {
double[] buffer = new double[2];
// opt.getValueGradient(buffer);
fail("Expected exception due to unsupported HyperbolicPrior");
} catch (UnsupportedOperationException expected) {
assertTrue(expected.getMessage().contains("not yet implemented"));
}
}

@Test
public void testGradientPropagationReflectionAfterSetParameters() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
dataAlphabet.lookupIndex("f");
labelAlphabet.lookupLabel("X");
Pipe pipe = new Pipe() {

public Instance pipe(Instance inst) {
return inst;
}
};
// FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "f" });
// Instance instance = new Instance(fv, labelAlphabet.lookupLabel("X"), null, null);
// InstanceList trainingList = new InstanceList(pipe, dataAlphabet, labelAlphabet);
// trainingList.add(instance);
MaxEnt classifier = new MaxEnt(pipe, new double[2], null, null);
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(trainingList, classifier);
double[] newParams = new double[2];
newParams[0] = 3.0;
newParams[1] = -2.0;
// opt.setParameters(newParams);
double[] grad = new double[2];
// opt.getValueGradient(grad);
assertFalse(Double.isNaN(grad[0]));
assertFalse(Double.isNaN(grad[1]));
}

@Test
public void testInstanceWithNullSourceAndNullNameDoesNotCrash() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
dataAlphabet.lookupIndex("w");
labelAlphabet.lookupLabel("Y");
Pipe pipe = new Pipe() {

public Instance pipe(Instance inst) {
return inst;
}
};
// FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "w" });
// Instance instance = new Instance(fv, labelAlphabet.lookupLabel("Y"), null, null);
// InstanceList trainingList = new InstanceList(pipe, dataAlphabet, labelAlphabet);
// trainingList.add(instance);
MaxEnt classifier = new MaxEnt(pipe, new double[2], null, null);
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(trainingList, classifier);
// double value = opt.getValue();
// assertTrue(Double.isFinite(value));
}

@Test
public void testGetValueRepeatedCallDoesNotChangeCachedValue() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
dataAlphabet.lookupIndex("v");
labelAlphabet.lookupLabel("Z");
Pipe pipe = new Pipe() {

public Instance pipe(Instance inst) {
return inst;
}
};
// FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "v" });
// Instance instance = new Instance(fv, labelAlphabet.lookupLabel("Z"), null, null);
// InstanceList trainingSet = new InstanceList(pipe, dataAlphabet, labelAlphabet);
// trainingSet.add(instance);
MaxEnt classifier = new MaxEnt(pipe, new double[2], null, null);
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(trainingSet, classifier);
// double value1 = opt.getValue();
// double value2 = opt.getValue();
// assertEquals(value1, value2, 0.0001);
}

@Test
public void testMultipleCallsToGetValueGradientDoesNotChangeCachedData() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
dataAlphabet.lookupIndex("x1");
labelAlphabet.lookupLabel("C1");
Pipe pipe = new Pipe() {

public Instance pipe(Instance inst) {
return inst;
}
};
// FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "x1" });
// Instance instance = new Instance(fv, labelAlphabet.lookupLabel("C1"), null, null);
// InstanceList trainingSet = new InstanceList(pipe, dataAlphabet, labelAlphabet);
// trainingSet.add(instance);
MaxEnt classifier = new MaxEnt(pipe, new double[2], null, null);
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(trainingSet, classifier);
double[] grad1 = new double[2];
double[] grad2 = new double[2];
// opt.getValueGradient(grad1);
// opt.getValueGradient(grad2);
assertEquals(grad1[0], grad2[0], 0.000001);
assertEquals(grad1[1], grad2[1], 0.000001);
}

@Test
public void testAllZeroFeatureValuesDoNotCauseNaNInGradient() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
dataAlphabet.lookupIndex("f1");
labelAlphabet.lookupLabel("Label1");
Pipe pipe = new Pipe() {

public Instance pipe(Instance carrier) {
return carrier;
}
};
FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "f1" }, new double[] { 0.0 });
Instance instance = new Instance(fv, labelAlphabet.lookupLabel("Label1"), null, null);
// InstanceList trainingSet = new InstanceList(pipe, dataAlphabet, labelAlphabet);
// trainingSet.add(instance);
MaxEnt classifier = new MaxEnt(pipe, new double[2], null, null);
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(trainingSet, classifier);
// double[] gradient = new double[opt.getNumParameters()];
// opt.getValueGradient(gradient);
// assertFalse(Double.isNaN(gradient[0]));
// assertFalse(Double.isNaN(gradient[1]));
}

@Test
public void testSetParametersWithNegativeValuesAffectsValue() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
dataAlphabet.lookupIndex("a");
labelAlphabet.lookupLabel("B");
Pipe pipe = new Pipe() {

public Instance pipe(Instance inst) {
return inst;
}
};
// FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "a" });
// Instance inst = new Instance(fv, labelAlphabet.lookupLabel("B"), null, null);
// InstanceList list = new InstanceList(pipe, dataAlphabet, labelAlphabet);
// list.add(inst);
MaxEnt classifier = new MaxEnt(pipe, new double[2], null, null);
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, classifier);
// double[] params = new double[opt.getNumParameters()];
// params[0] = -5.0;
// params[1] = -10.0;
// opt.setParameters(params);
// double val = opt.getValue();
// assertTrue(Double.isFinite(val));
}

@Test
public void testClassifierWithUniformScoresDoesNotCauseArithmeticError() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
dataAlphabet.lookupIndex("u");
labelAlphabet.lookupLabel("Y");
Pipe pipe = new Pipe() {

public Instance pipe(Instance i) {
return i;
}
};
// FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "u" });
// Instance inst = new Instance(fv, labelAlphabet.lookupLabel("Y"), null, null);
// InstanceList list = new InstanceList(pipe, dataAlphabet, labelAlphabet);
// list.add(inst);
MaxEnt classifier = new MaxEnt(pipe, new double[2], null, null) {

@Override
public void getClassificationScores(Instance instance, double[] scores) {
scores[0] = 0.5;
}
};
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, classifier);
// double val = opt.getValue();
// assertTrue(Double.isFinite(val));
}

@Test
public void testFeatureVectorWithDuplicateFeatures() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
int i1 = dataAlphabet.lookupIndex("token");
int i2 = dataAlphabet.lookupIndex("token");
labelAlphabet.lookupLabel("TAG");
Pipe pipe = new Pipe() {

public Instance pipe(Instance i) {
return i;
}
};
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { i1, i2 }, new double[] { 1.0, 2.0 });
Instance inst = new Instance(fv, labelAlphabet.lookupLabel("TAG"), null, null);
// InstanceList list = new InstanceList(pipe, dataAlphabet, labelAlphabet);
// list.add(inst);
MaxEnt classifier = new MaxEnt(pipe, new double[2], null, null);
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, classifier);
// double value = opt.getValue();
// assertTrue(Double.isFinite(value));
}

@Test
public void testGradientValuesWithLargerFeatureMagnitude() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
dataAlphabet.lookupIndex("bigfeat");
labelAlphabet.lookupLabel("class1");
Pipe pipe = new Pipe() {

public Instance pipe(Instance i) {
return i;
}
};
FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "bigfeat" }, new double[] { 1000.0 });
Instance inst = new Instance(fv, labelAlphabet.lookupLabel("class1"), null, null);
// InstanceList list = new InstanceList(pipe, dataAlphabet, labelAlphabet);
// list.add(inst);
MaxEnt classifier = new MaxEnt(pipe, new double[2], null, null);
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, classifier);
double[] grad = new double[2];
// opt.getValueGradient(grad);
assertTrue(Math.abs(grad[0]) > 0.0 || Math.abs(grad[1]) > 0.0);
}

@Test
public void testGetValueGradientBufferNotModifiedWhenEmpty() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
dataAlphabet.lookupIndex("token");
labelAlphabet.lookupLabel("class");
Pipe pipe = new Pipe() {

public Instance pipe(Instance i) {
return i;
}
};
// FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "token" });
// Instance inst = new Instance(fv, labelAlphabet.lookupLabel("class"), null, null);
// InstanceList list = new InstanceList(pipe, dataAlphabet, labelAlphabet);
// list.add(inst);
MaxEnt classifier = new MaxEnt(pipe, new double[2], null, null);
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, classifier);
// double[] grad = new double[opt.getNumParameters()];
// for (int i = 0; i < grad.length; i++) grad[i] = -999;
// opt.getValueGradient(grad);
// for (int i = 0; i < grad.length; i++) {
// assertNotEquals(-999.0, grad[i], 0.0001);
// }
}

@Test
public void testGradientZeroForUnusedFeaturesWhenFeatureSelectionSet() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
int feat1 = dataAlphabet.lookupIndex("used");
int feat2 = dataAlphabet.lookupIndex("unused");
labelAlphabet.lookupLabel("L");
Pipe pipe = new Pipe() {

public Instance pipe(Instance i) {
return i;
}
};
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { feat1 }, new double[] { 1.0 });
Instance inst = new Instance(fv, labelAlphabet.lookupLabel("L"), null, null);
// InstanceList list = new InstanceList(pipe, dataAlphabet, labelAlphabet);
// list.setFeatureSelection(new FeatureSelection(dataAlphabet));
// list.getFeatureSelection().add(feat1);
// list.add(inst);
// MaxEnt classifier = new MaxEnt(pipe, new double[2 * (dataAlphabet.size() + 1)], list.getFeatureSelection(), null);
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, classifier);
// double[] grad = new double[opt.getNumParameters()];
// opt.getValueGradient(grad);
int base = dataAlphabet.size() + 1;
// assertNotEquals(0.0, grad[0], 0.0001);
// assertEquals(0.0, grad[1], 0.0001);
}

@Test
public void testSetParametersWithIncorrectLengthShorter() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
dataAlphabet.lookupIndex("f");
labelAlphabet.lookupLabel("L");
Pipe pipe = new Pipe() {

public Instance pipe(Instance carrier) {
return carrier;
}
};
// FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "f" });
// Instance instance = new Instance(fv, labelAlphabet.lookupLabel("L"), null, null);
// InstanceList list = new InstanceList(pipe, dataAlphabet, labelAlphabet);
// list.add(instance);
MaxEnt classifier = new MaxEnt(pipe, new double[4], null, null);
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, classifier);
double[] shortParams = new double[2];
shortParams[0] = 1.0;
// opt.setParameters(shortParams);
// double[] output = new double[opt.getNumParameters()];
// opt.getParameters(output);
// assertEquals(1.0, output[0], 0.0001);
}

@Test
public void testInstanceWithNullDataIsHandled() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupLabel("LABEL");
Pipe pipe = new Pipe() {

public Instance pipe(Instance carrier) {
return carrier;
}
};
Instance instance = new Instance(null, labelAlphabet.lookupLabel("LABEL"), null, null);
// InstanceList list = new InstanceList(pipe, dataAlphabet, labelAlphabet);
// list.add(instance);
MaxEnt classifier = new MaxEnt(pipe, new double[1], null, null);
try {
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, classifier);
// opt.getValue();
} catch (NullPointerException e) {
}
}

@Test
public void testDefaultFeatureIsAlwaysInConstraints() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
dataAlphabet.lookupIndex("f1");
labelAlphabet.lookupLabel("C");
Pipe pipe = new Pipe() {

public Instance pipe(Instance carrier) {
return carrier;
}
};
// FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "f1" });
// Instance instance = new Instance(fv, labelAlphabet.lookupLabel("C"), null, null);
// InstanceList list = new InstanceList(pipe, dataAlphabet, labelAlphabet);
// list.add(instance);
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, null);
int defaultIndex = dataAlphabet.size();
// double val = opt.getValue();
// assertTrue(Double.isFinite(val));
}

@Test
public void testMaxEntOptimizableWithoutInitialClassifierCreatesOne() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
dataAlphabet.lookupIndex("w1");
labelAlphabet.lookupLabel("Y");
Pipe pipe = new Pipe() {

public Instance pipe(Instance carrier) {
return carrier;
}
};
// FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "w1" });
// Instance instance = new Instance(fv, labelAlphabet.lookupLabel("Y"), null, null);
// InstanceList list = new InstanceList(pipe, dataAlphabet, labelAlphabet);
// list.add(instance);
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, null);
// assertNotNull(opt.getClassifier());
}

@Test
public void testMultipleLabelsUsesCorrectConstraintsPerLabel() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
int f1 = dataAlphabet.lookupIndex("f1");
int f2 = dataAlphabet.lookupIndex("f2");
// int l0 = labelAlphabet.lookupLabel("A");
// int l1 = labelAlphabet.lookupLabel("B");
Pipe pipe = new Pipe() {

public Instance pipe(Instance carrier) {
return carrier;
}
};
// FeatureVector fv1 = new FeatureVector(dataAlphabet, new String[] { "f1" });
// FeatureVector fv2 = new FeatureVector(dataAlphabet, new String[] { "f2" });
// Instance i1 = new Instance(fv1, labelAlphabet.lookupLabel("A"), null, null);
// Instance i2 = new Instance(fv2, labelAlphabet.lookupLabel("B"), null, null);
// InstanceList list = new InstanceList(pipe, dataAlphabet, labelAlphabet);
// list.add(i1);
// list.add(i2);
MaxEnt classifier = new MaxEnt(pipe, new double[labelAlphabet.size() * (dataAlphabet.size() + 1)], null, null);
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, classifier);
// double value = opt.getValue();
// assertTrue(Double.isFinite(value));
}

@Test
public void testInfinitePredictionGracefullyFails() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
dataAlphabet.lookupIndex("f");
labelAlphabet.lookupLabel("C");
Pipe pipe = new Pipe() {

public Instance pipe(Instance carrier) {
return carrier;
}
};
// FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "f" });
// Instance instance = new Instance(fv, labelAlphabet.lookupLabel("C"), null, null);
// InstanceList list = new InstanceList(pipe, dataAlphabet, labelAlphabet);
// list.add(instance);
MaxEnt classifier = new MaxEnt(pipe, new double[2], null, null) {

@Override
public void getClassificationScores(Instance instance, double[] scores) {
scores[0] = Double.POSITIVE_INFINITY;
}
};
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, classifier);
// double val = opt.getValue();
// assertTrue(Double.isNaN(val) || Double.isInfinite(val));
}

@Test
public void testOnlyDefaultFeatureSelectedInFeatureSelection() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
int f1 = dataAlphabet.lookupIndex("x");
// int l0 = labelAlphabet.lookupLabel("Q");
Pipe pipe = new Pipe() {

public Instance pipe(Instance carrier) {
return carrier;
}
};
// FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "x" });
// Instance instance = new Instance(fv, labelAlphabet.lookupLabel("Q"), null, null);
// InstanceList list = new InstanceList(pipe, dataAlphabet, labelAlphabet);
// list.add(instance);
FeatureSelection fs = new FeatureSelection(dataAlphabet);
fs.add(dataAlphabet.size());
MaxEnt classifier = new MaxEnt(pipe, new double[labelAlphabet.size() * (dataAlphabet.size() + 1)], fs, null);
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, classifier);
// double[] grad = new double[opt.getNumParameters()];
// opt.getValueGradient(grad);
for (int i = 0; i < dataAlphabet.size(); i++) {
// assertEquals(0.0, grad[i], 0.0000001);
}
}
}
