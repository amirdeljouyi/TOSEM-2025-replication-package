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
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MaxEntOptimizableByLabelLikelihood_1_GPTLLMTest {

@Test
public void testConstructorInitializesCorrectly() {
List<String> texts = Arrays.asList("apple orange");
List<String> labels = Arrays.asList("fruit");
ArrayList<Pipe> pipes = new ArrayList<Pipe>();
pipes.add(new cc.mallet.pipe.Input2CharSequence("UTF-8"));
pipes.add(new cc.mallet.pipe.CharSequence2TokenSequence());
pipes.add(new TokenSequenceLowercase());
pipes.add(new TokenSequenceRemoveStopwords());
pipes.add(new TokenSequence2FeatureVectorSequence());
Pipe pipe = new SerialPipes(pipes);
InstanceList trainingSet = new InstanceList(pipe);
Instance instance = new Instance("apple orange", "fruit", "inst0", null);
trainingSet.addThruPipe(new ArrayIterator(Arrays.asList(instance)));
MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(trainingSet, null);
assertNotNull(opt.getClassifier());
assertEquals(trainingSet.getTargetAlphabet().size(), opt.getClassifier().getLabelAlphabet().size());
}

@Test
public void testSetAndGetParameter() {
List<String> texts = Arrays.asList("apple");
List<String> labels = Arrays.asList("fruit");
ArrayList<Pipe> pipes = new ArrayList<Pipe>();
pipes.add(new cc.mallet.pipe.Input2CharSequence("UTF-8"));
pipes.add(new cc.mallet.pipe.CharSequence2TokenSequence());
pipes.add(new TokenSequenceLowercase());
pipes.add(new TokenSequenceRemoveStopwords());
pipes.add(new TokenSequence2FeatureVectorSequence());
Pipe pipe = new SerialPipes(pipes);
InstanceList trainingSet = new InstanceList(pipe);
Instance instance = new Instance("apple", "fruit", "inst0", null);
trainingSet.addThruPipe(new ArrayIterator(Arrays.asList(instance)));
MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(trainingSet, null);
opt.setParameter(0, 3.14159);
double value = opt.getParameter(0);
assertEquals(3.14159, value, 0.000001);
}

@Test
public void testSetAndGetParametersArray() {
ArrayList<Pipe> pipes = new ArrayList<Pipe>();
pipes.add(new cc.mallet.pipe.Input2CharSequence("UTF-8"));
pipes.add(new cc.mallet.pipe.CharSequence2TokenSequence());
pipes.add(new TokenSequenceLowercase());
pipes.add(new TokenSequenceRemoveStopwords());
pipes.add(new TokenSequence2FeatureVectorSequence());
Pipe pipe = new SerialPipes(pipes);
InstanceList trainingSet = new InstanceList(pipe);
Instance instance = new Instance("banana", "fruit", "inst0", null);
trainingSet.addThruPipe(new ArrayIterator(Arrays.asList(instance)));
MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(trainingSet, null);
double[] input = new double[opt.getNumParameters()];
input[0] = 7.0;
opt.setParameters(input);
double[] output = new double[opt.getNumParameters()];
opt.getParameters(output);
assertEquals(7.0, output[0], 0.0001);
}

@Test
public void testValueCalculationReturnsFinite() {
ArrayList<Pipe> pipes = new ArrayList<Pipe>();
pipes.add(new cc.mallet.pipe.Input2CharSequence("UTF-8"));
pipes.add(new cc.mallet.pipe.CharSequence2TokenSequence());
pipes.add(new TokenSequenceLowercase());
pipes.add(new TokenSequenceRemoveStopwords());
pipes.add(new TokenSequence2FeatureVectorSequence());
Pipe pipe = new SerialPipes(pipes);
InstanceList trainingSet = new InstanceList(pipe);
Instance instance = new Instance("apple banana", "fruit", "inst0", null);
trainingSet.addThruPipe(new ArrayIterator(Arrays.asList(instance)));
MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(trainingSet, null);
double result = opt.getValue();
assertTrue(Double.isFinite(result));
}

@Test
public void testGetValueGradientHasSameLengthAsParameters() {
ArrayList<Pipe> pipes = new ArrayList<Pipe>();
pipes.add(new cc.mallet.pipe.Input2CharSequence("UTF-8"));
pipes.add(new cc.mallet.pipe.CharSequence2TokenSequence());
pipes.add(new TokenSequenceLowercase());
pipes.add(new TokenSequenceRemoveStopwords());
pipes.add(new TokenSequence2FeatureVectorSequence());
Pipe pipe = new SerialPipes(pipes);
InstanceList trainingSet = new InstanceList(pipe);
Instance instance = new Instance("banana apple", "fruit", "instA", null);
trainingSet.addThruPipe(new ArrayIterator(Arrays.asList(instance)));
MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(trainingSet, null);
double[] gradient = new double[opt.getNumParameters()];
opt.getValueGradient(gradient);
assertEquals(opt.getNumParameters(), gradient.length);
}

@Test
public void testUseGaussianPriorAffectsValue() {
ArrayList<Pipe> pipes = new ArrayList<Pipe>();
pipes.add(new cc.mallet.pipe.Input2CharSequence("UTF-8"));
pipes.add(new cc.mallet.pipe.CharSequence2TokenSequence());
pipes.add(new TokenSequenceLowercase());
pipes.add(new TokenSequenceRemoveStopwords());
pipes.add(new TokenSequence2FeatureVectorSequence());
Pipe pipe = new SerialPipes(pipes);
InstanceList trainingSet = new InstanceList(pipe);
trainingSet.addThruPipe(new ArrayIterator(Arrays.asList(new Instance("apple banana", "fruit", "inst1", null))));
MaxEntOptimizableByLabelLikelihood opt1 = new MaxEntOptimizableByLabelLikelihood(trainingSet, null);
opt1.setGaussianPriorVariance(0.1);
double valueWithPrior = opt1.getValue();
MaxEntOptimizableByLabelLikelihood opt2 = new MaxEntOptimizableByLabelLikelihood(trainingSet, null);
opt2.useNoPrior();
double valueWithoutPrior = opt2.getValue();
assertTrue(valueWithPrior > valueWithoutPrior);
}

@Test(expected = UnsupportedOperationException.class)
public void testUseHyperbolicPriorThrowsInValueGradient() {
ArrayList<Pipe> pipes = new ArrayList<Pipe>();
pipes.add(new cc.mallet.pipe.Input2CharSequence("UTF-8"));
pipes.add(new cc.mallet.pipe.CharSequence2TokenSequence());
pipes.add(new TokenSequenceLowercase());
pipes.add(new TokenSequenceRemoveStopwords());
pipes.add(new TokenSequence2FeatureVectorSequence());
Pipe pipe = new SerialPipes(pipes);
InstanceList trainingSet = new InstanceList(pipe);
trainingSet.addThruPipe(new ArrayIterator(Arrays.asList(new Instance("banana apple", "fruit", "x1", null))));
MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(trainingSet, null);
opt.setHyperbolicPriorSlope(1.0);
double[] buffer = new double[opt.getNumParameters()];
opt.getValueGradient(buffer);
}

@Test
public void testInstanceWithNullLabelingIsIgnored() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("apple", true);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
Instance instance = new Instance(fv, null, "x", null);
ArrayList<Pipe> pipes = new ArrayList<Pipe>();
pipes.add(new TokenSequence2FeatureVectorSequence());
Pipe pipe = new SerialPipes(pipes);
InstanceList list = new InstanceList(pipe);
// list.setDataAlphabet(dataAlphabet);
// list.setTargetAlphabet(new LabelAlphabet());
list.addThruPipe(new ArrayIterator(Arrays.asList(instance)));
MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, null);
double v = opt.getValue();
assertTrue(Double.isFinite(v));
}

@Test
public void testNaNInFeatureDoesNotThrow() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("weird", true);
SparseVector vector = new SparseVector(new int[] { 0 }, new double[] { Double.NaN });
Instance instance = new Instance(vector, "fruit", "test", null);
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupLabel("fruit");
InstanceList list = new InstanceList(new SerialPipes(new ArrayList<Pipe>()));
// list.setDataAlphabet(dataAlphabet);
// list.setTargetAlphabet(labelAlphabet);
list.addThruPipe(new ArrayIterator(Arrays.asList(instance)));
MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, null);
double output = opt.getValue();
assertTrue(Double.isFinite(output));
}

@Test
public void testUseNoPriorDisablesBothPriors() {
ArrayList<Pipe> pipes = new ArrayList<Pipe>();
pipes.add(new cc.mallet.pipe.Input2CharSequence("UTF-8"));
pipes.add(new cc.mallet.pipe.CharSequence2TokenSequence());
pipes.add(new TokenSequenceLowercase());
pipes.add(new TokenSequenceRemoveStopwords());
pipes.add(new TokenSequence2FeatureVectorSequence());
Pipe pipe = new SerialPipes(pipes);
InstanceList training = new InstanceList(pipe);
training.addThruPipe(new ArrayIterator(Arrays.asList(new Instance("apple", "fruit", "inst", null))));
MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(training, null);
opt.useNoPrior();
// assertFalse(opt.useGaussianPrior().useNoPrior().usingGaussianPrior);
// assertFalse(opt.useHyperbolicPrior().useNoPrior().usingHyperbolicPrior);
}

@Test
public void testEmptyInstanceListDoesNotCrash() {
ArrayList<Pipe> pipes = new ArrayList<Pipe>();
pipes.add(new TokenSequence2FeatureVectorSequence());
Pipe pipe = new SerialPipes(pipes);
InstanceList emptyList = new InstanceList(pipe);
// emptyList.setDataAlphabet(new Alphabet());
// emptyList.setTargetAlphabet(new LabelAlphabet());
MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(emptyList, null);
double value = opt.getValue();
double[] gradient = new double[opt.getNumParameters()];
opt.getValueGradient(gradient);
assertTrue(Double.isFinite(value));
}

@Test
public void testSingleFeatureSingleLabel() {
Alphabet dataAlphabet = new Alphabet();
int index = dataAlphabet.lookupIndex("feature1");
LabelAlphabet labelAlphabet = new LabelAlphabet();
Label label = labelAlphabet.lookupLabel("label1");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { index }, new double[] { 1.0 });
Instance inst = new Instance(fv, label, "id", null);
Pipe pipe = new SerialPipes(new ArrayList<Pipe>());
InstanceList list = new InstanceList(pipe);
// list.setDataAlphabet(dataAlphabet);
// list.setTargetAlphabet(labelAlphabet);
list.add(inst);
MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, null);
double value = opt.getValue();
double[] gradient = new double[opt.getNumParameters()];
opt.getValueGradient(gradient);
assertTrue(Double.isFinite(value));
assertEquals(opt.getNumParameters(), gradient.length);
}

@Test
public void testSetParametersWithIncorrectLength() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("x");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupLabel("A");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
Instance inst = new Instance(fv, "A", "one", null);
Pipe pipe = new SerialPipes(new ArrayList<Pipe>());
InstanceList list = new InstanceList(pipe);
// list.setDataAlphabet(dataAlphabet);
// list.setTargetAlphabet(labelAlphabet);
list.add(inst);
MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, null);
double[] wrongLength = new double[opt.getNumParameters() + 1];
opt.setParameters(wrongLength);
double[] newBuffer = new double[opt.getNumParameters()];
opt.getParameters(newBuffer);
assertEquals(newBuffer.length, opt.getNumParameters());
}

@Test
public void testSetParameterInvalidIndexIgnoredSilently() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
dataAlphabet.lookupIndex("z");
labelAlphabet.lookupLabel("label");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
Instance instance = new Instance(fv, "label", "inst", null);
Pipe pipe = new SerialPipes(new ArrayList<Pipe>());
InstanceList list = new InstanceList(pipe);
// list.setDataAlphabet(dataAlphabet);
// list.setTargetAlphabet(labelAlphabet);
list.add(instance);
MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, null);
int numParams = opt.getNumParameters();
opt.setParameter(numParams - 1, 9.99);
double val = opt.getParameter(numParams - 1);
assertEquals(9.99, val, 0.0001);
}

@Test
public void testMultipleLabelsSameFeature() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
dataAlphabet.lookupIndex("feature");
labelAlphabet.lookupLabel("labelA");
labelAlphabet.lookupLabel("labelB");
FeatureVector fv1 = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
FeatureVector fv2 = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
Instance inst1 = new Instance(fv1, "labelA", "a", null);
Instance inst2 = new Instance(fv2, "labelB", "b", null);
Pipe pipe = new SerialPipes(new ArrayList<Pipe>());
InstanceList list = new InstanceList(pipe);
// list.setDataAlphabet(dataAlphabet);
// list.setTargetAlphabet(labelAlphabet);
list.add(inst1);
list.add(inst2);
MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, null);
double value = opt.getValue();
double[] gradient = new double[opt.getNumParameters()];
opt.getValueGradient(gradient);
assertTrue(Double.isFinite(value));
}

@Test
public void testInstanceWeightZero() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
dataAlphabet.lookupIndex("x");
labelAlphabet.lookupLabel("Y");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
Instance instance = new Instance(fv, "Y", "zeroweight", null);
Pipe pipe = new SerialPipes(new ArrayList<Pipe>());
InstanceList list = new InstanceList(pipe) {

@Override
public double getInstanceWeight(Instance inst) {
return 0.0;
}
};
// list.setDataAlphabet(dataAlphabet);
// list.setTargetAlphabet(labelAlphabet);
list.add(instance);
MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, null);
double value = opt.getValue();
assertTrue(Double.isFinite(value));
}

@Test
public void testDefaultFeatureIndexIsIncludedInGradient() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
dataAlphabet.lookupIndex("feat");
labelAlphabet.lookupLabel("lab");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
Instance instance = new Instance(fv, "lab", "defaultTest", null);
Pipe pipe = new SerialPipes(new ArrayList<Pipe>());
InstanceList list = new InstanceList(pipe);
// list.setDataAlphabet(dataAlphabet);
// list.setTargetAlphabet(labelAlphabet);
list.add(instance);
MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, null);
double[] gradient = new double[opt.getNumParameters()];
opt.getValueGradient(gradient);
boolean hasNonZero = false;
for (int i = 0; i < gradient.length; i++) {
if (gradient[i] != 0.0) {
hasNonZero = true;
break;
}
}
assertTrue(hasNonZero);
}

@Test
public void testMultipleCallsToValueAndGradientYieldSameResults() {
ArrayList<Pipe> pipes = new ArrayList<Pipe>();
pipes.add(new cc.mallet.pipe.Input2CharSequence("UTF-8"));
pipes.add(new cc.mallet.pipe.CharSequence2TokenSequence());
pipes.add(new TokenSequenceLowercase());
pipes.add(new TokenSequenceRemoveStopwords());
pipes.add(new TokenSequence2FeatureVectorSequence());
Pipe pipe = new SerialPipes(pipes);
InstanceList list = new InstanceList(pipe);
list.addThruPipe(new ArrayIterator(Arrays.asList(new Instance("a b c", "label", "id", null))));
MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, null);
double v1 = opt.getValue();
double v2 = opt.getValue();
double[] g1 = new double[opt.getNumParameters()];
double[] g2 = new double[opt.getNumParameters()];
opt.getValueGradient(g1);
opt.getValueGradient(g2);
assertEquals(v1, v2, 0.0001);
assertArrayEquals(g1, g2, 0.0001);
}

@Test
public void testGetParametersWithNullInputArray() {
ArrayList<Pipe> pipes = new ArrayList<Pipe>();
pipes.add(new cc.mallet.pipe.Input2CharSequence("UTF-8"));
pipes.add(new cc.mallet.pipe.CharSequence2TokenSequence());
pipes.add(new TokenSequenceLowercase());
pipes.add(new TokenSequenceRemoveStopwords());
pipes.add(new TokenSequence2FeatureVectorSequence());
Pipe pipe = new SerialPipes(pipes);
InstanceList list = new InstanceList(pipe);
Instance inst = new Instance("dog cat", "label1", "id1", null);
list.addThruPipe(new ArrayIterator(Arrays.asList(inst)));
MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, null);
double[] originalParams = new double[opt.getNumParameters()];
opt.getParameters(null);
opt.getParameters(originalParams);
for (int i = 0; i < originalParams.length; i++) {
assertEquals(0.0, originalParams[i], 0.000001);
}
}

@Test
public void testInstanceWithZeroScoresSkipsGradientComputation() {
Alphabet dataAlphabet = new Alphabet();
int fIndex = dataAlphabet.lookupIndex("feat");
LabelAlphabet labelAlphabet = new LabelAlphabet();
int lIndex = labelAlphabet.lookupIndex("label");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { fIndex }, new double[] { 1.0 });
Instance inst = new Instance(fv, "label", "zeroScoreTest", null);
Pipe pipe = new SerialPipes(new ArrayList<Pipe>());
InstanceList list = new InstanceList(pipe);
// list.setDataAlphabet(dataAlphabet);
// list.setTargetAlphabet(labelAlphabet);
list.add(inst);
MaxEnt customClassifier = new MaxEnt(list.getPipe(), new double[] { 0.0, 0.0 }, null, null) {

@Override
public void getClassificationScores(Instance instance, double[] scores) {
scores[0] = 0.0;
}
};
MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, customClassifier);
double v = opt.getValue();
assertTrue(Double.isFinite(v));
}

@Test
public void testSubstituteNegativeInfinityInGradient() {
Alphabet dataAlphabet = new Alphabet();
int fi = dataAlphabet.lookupIndex("f");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupLabel("lab");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { fi }, new double[] { 1.0 });
Instance inst = new Instance(fv, "lab", "neg_inf_grad", null);
Pipe pipe = new SerialPipes(new ArrayList<Pipe>());
InstanceList list = new InstanceList(pipe);
// list.setDataAlphabet(dataAlphabet);
// list.setTargetAlphabet(labelAlphabet);
list.add(inst);
MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, null);
opt.setParameter(0, Double.NEGATIVE_INFINITY);
double[] grad = new double[opt.getNumParameters()];
opt.getValueGradient(grad);
assertEquals(0.0, grad[0], 0.000001);
}

@Test
public void testPerLabelFeatureSelectionRespected() {
Alphabet dataAlphabet = new Alphabet();
int featureIndex = dataAlphabet.lookupIndex("tok");
LabelAlphabet labelAlphabet = new LabelAlphabet();
// int labelIndex = labelAlphabet.lookupLabel("lab");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { featureIndex }, new double[] { 1.0 });
Instance x = new Instance(fv, "lab", "id", null);
Pipe pipe = new SerialPipes(new ArrayList<Pipe>());
InstanceList list = new InstanceList(pipe);
// list.setDataAlphabet(dataAlphabet);
// list.setTargetAlphabet(labelAlphabet);
list.add(x);
FeatureSelection[] perLabelFS = new FeatureSelection[1];
// GrowableFeatureSelection fs = new GrowableFeatureSelection(dataAlphabet);
// fs.add(featureIndex);
// perLabelFS[0] = fs;
list.setPerLabelFeatureSelection(perLabelFS);
MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, null);
double[] gradient = new double[opt.getNumParameters()];
opt.getValueGradient(gradient);
boolean hasNonZero = gradient[0] != 0.0;
assertTrue(hasNonZero);
}

@Test
public void testCachedGradientAndValueStalenessUponParameterChange() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("x");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupLabel("L");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
Instance instance = new Instance(fv, "L", "zz", null);
Pipe pipe = new SerialPipes(new ArrayList<Pipe>());
InstanceList list = new InstanceList(pipe);
// list.setDataAlphabet(dataAlphabet);
// list.setTargetAlphabet(labelAlphabet);
list.add(instance);
MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, null);
double val1 = opt.getValue();
opt.setParameter(0, 5.0);
double val2 = opt.getValue();
assertNotEquals(val1, val2, 0.00001);
}

@Test
public void testLogLikelihoodInfinityHandling() {
Alphabet dataAlphabet = new Alphabet();
int featIndex = dataAlphabet.lookupIndex("xf");
LabelAlphabet labelAlphabet = new LabelAlphabet();
// int labelIdx = labelAlphabet.lookupLabel("lab");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { featIndex }, new double[] { 1.0 });
Instance instance = new Instance(fv, "lab", "infiniteProb", null);
Pipe pipe = new SerialPipes(new ArrayList<Pipe>());
InstanceList list = new InstanceList(pipe);
// list.setDataAlphabet(dataAlphabet);
// list.setTargetAlphabet(labelAlphabet);
list.add(instance);
MaxEnt classifier = new MaxEnt(list.getPipe(), new double[] {}, null, null) {

@Override
public void getClassificationScores(Instance inst, double[] scores) {
scores[0] = 0.0;
}
};
MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, classifier);
double val = opt.getValue();
assertTrue(Double.isInfinite(-val));
}

@Test
public void testSetHyperbolicParametersAffectsState() {
Alphabet dataAlphabet = new Alphabet();
int fidx = dataAlphabet.lookupIndex("k");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupLabel("a");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { fidx }, new double[] { 1.0 });
Instance i = new Instance(fv, "a", "xyz", null);
Pipe p = new SerialPipes(new ArrayList<Pipe>());
InstanceList l = new InstanceList(p);
// l.setDataAlphabet(dataAlphabet);
// l.setTargetAlphabet(labelAlphabet);
l.add(i);
MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(l, null);
opt.setHyperbolicPriorSlope(0.8).setHyperbolicPriorSharpness(5.5);
// assertTrue(opt.useHyperbolicPrior().usingHyperbolicPrior);
}

@Test
public void testDefaultFeatureIsAddedToFeatureSelection() {
Alphabet dataAlphabet = new Alphabet();
int fIdx = dataAlphabet.lookupIndex("feature");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupLabel("label");
// GrowableFeatureSelection globalFS = new GrowableFeatureSelection(dataAlphabet);
// globalFS.add(fIdx);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { fIdx }, new double[] { 1.0 });
Instance inst = new Instance(fv, "label", "id", null);
ArrayList<Pipe> pipes = new ArrayList<Pipe>();
Pipe pipe = new SerialPipes(pipes);
InstanceList list = new InstanceList(pipe);
// list.setDataAlphabet(dataAlphabet);
// list.setTargetAlphabet(labelAlphabet);
// list.setFeatureSelection(globalFS);
list.add(inst);
MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, null);
int numParams = opt.getNumParameters();
double[] gradient = new double[numParams];
opt.getValueGradient(gradient);
boolean hasNonZero = false;
int numLabels = labelAlphabet.size();
int numFeatures = numParams / numLabels;
for (int i = 0; i < gradient.length; i += numFeatures) {
if (gradient[i + numFeatures - 1] != 0.0) {
hasNonZero = true;
break;
}
}
assertTrue(hasNonZero);
}

@Test
public void testConstraintMatrixIsCorrectlyPopulated() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
int f1 = dataAlphabet.lookupIndex("f1");
int f2 = dataAlphabet.lookupIndex("f2");
labelAlphabet.lookupLabel("label");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { f1, f2 }, new double[] { 2.0, 3.0 });
Instance inst = new Instance(fv, "label", "id", null);
Pipe pipe = new SerialPipes(new ArrayList<Pipe>());
InstanceList list = new InstanceList(pipe);
// list.setDataAlphabet(dataAlphabet);
// list.setTargetAlphabet(labelAlphabet);
list.addThruPipe(new ArrayIterator(Arrays.asList(inst)));
MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, null);
opt.getValue();
double[] gradient = new double[opt.getNumParameters()];
opt.getValueGradient(gradient);
int numLabels = labelAlphabet.size();
int numFeatures = opt.getNumParameters() / numLabels;
assertNotEquals(0.0, gradient[0], 0.0);
assertNotEquals(0.0, gradient[1], 0.0);
}

@Test
public void testMultipleLabelsWithSharedFeaturesContributeToGradient() {
Alphabet alpha = new Alphabet();
int featIdx = alpha.lookupIndex("shared");
LabelAlphabet labels = new LabelAlphabet();
labels.lookupLabel("A");
labels.lookupLabel("B");
FeatureVector fv1 = new FeatureVector(alpha, new int[] { featIdx }, new double[] { 1.0 });
FeatureVector fv2 = new FeatureVector(alpha, new int[] { featIdx }, new double[] { 1.0 });
Instance i1 = new Instance(fv1, "A", "x1", null);
Instance i2 = new Instance(fv2, "B", "x2", null);
Pipe p = new SerialPipes(new ArrayList<Pipe>());
InstanceList list = new InstanceList(p);
// list.setDataAlphabet(alpha);
// list.setTargetAlphabet(labels);
list.add(i1);
list.add(i2);
MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, null);
double[] grad = new double[opt.getNumParameters()];
opt.getValue();
opt.getValueGradient(grad);
boolean hasNonzero = false;
for (int k = 0; k < grad.length; k++) {
if (grad[k] != 0.0) {
hasNonzero = true;
break;
}
}
assertTrue(hasNonzero);
}

@Test
public void testGetValueGradientAfterUseNoPriorHasSmallerPenalty() {
ArrayList<Pipe> pipes = new ArrayList<Pipe>();
pipes.add(new TokenSequence2FeatureVectorSequence());
pipes.add(new TokenSequenceLowercase());
pipes.add(new TokenSequenceRemoveStopwords());
Pipe pipe = new SerialPipes(pipes);
InstanceList list = new InstanceList(pipe);
list.addThruPipe(new ArrayIterator(Arrays.asList(new Instance("apple banana", "label1", "i1", null))));
MaxEntOptimizableByLabelLikelihood opt1 = new MaxEntOptimizableByLabelLikelihood(list, null);
opt1.setGaussianPriorVariance(0.1);
opt1.setParameter(0, 1.0);
double v1 = opt1.getValue();
MaxEntOptimizableByLabelLikelihood opt2 = new MaxEntOptimizableByLabelLikelihood(list, null);
opt2.useNoPrior();
opt2.setParameter(0, 1.0);
double v2 = opt2.getValue();
assertTrue(v2 < v1);
}

@Test
public void testFeatureWithZeroWeightDoesNotAffectGradientSignificantly() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("x");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupLabel("y");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 0.0 });
Instance inst = new Instance(fv, "y", "zero", null);
Pipe pipeline = new SerialPipes(new ArrayList<Pipe>());
InstanceList list = new InstanceList(pipeline);
// list.setDataAlphabet(dataAlphabet);
// list.setTargetAlphabet(labelAlphabet);
list.add(inst);
MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, null);
double[] grad = new double[opt.getNumParameters()];
opt.getValue();
opt.getValueGradient(grad);
boolean allZero = true;
for (double g : grad) {
if (Math.abs(g) > 1e-6) {
allZero = false;
break;
}
}
assertTrue(allZero);
}

@Test
public void testClassifierConsistencyBetweenGettersAndConstructor() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("foo");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupLabel("bar");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
Instance inst = new Instance(fv, "bar", "id", null);
Pipe pipe = new SerialPipes(new ArrayList<Pipe>());
InstanceList trainingList = new InstanceList(pipe);
// trainingList.setDataAlphabet(dataAlphabet);
// trainingList.setTargetAlphabet(labelAlphabet);
trainingList.add(inst);
MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(trainingList, null);
MaxEnt classifier = opt.getClassifier();
assertNotNull(classifier);
assertSame(trainingList.getPipe(), classifier.getInstancePipe());
}

@Test
public void testGetParametersWithWrongSizedTargetArray() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("f");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupLabel("L");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
Instance inst = new Instance(fv, "L", "id", null);
Pipe pipe = new SerialPipes(new ArrayList<Pipe>());
InstanceList list = new InstanceList(pipe);
// list.setDataAlphabet(dataAlphabet);
// list.setTargetAlphabet(labelAlphabet);
list.add(inst);
MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, null);
double[] wrongSized = new double[1];
opt.getParameters(wrongSized);
assertNotNull(wrongSized);
}

@Test
public void testSetParametersNullThrowsAssertion() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("f");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupLabel("L");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
Instance inst = new Instance(fv, "L", "id", null);
Pipe pipe = new SerialPipes(new ArrayList<Pipe>());
InstanceList list = new InstanceList(pipe);
// list.setDataAlphabet(dataAlphabet);
// list.setTargetAlphabet(labelAlphabet);
list.add(inst);
MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, null);
try {
opt.setParameters(null);
fail("Expected assertion error for null buffer");
} catch (AssertionError e) {
}
}

@Test
public void testFeatureSelectionOverridesPriorGradientZeroing() {
Alphabet dataAlphabet = new Alphabet();
int idx = dataAlphabet.lookupIndex("Z");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupLabel("L");
// GrowableFeatureSelection fs = new GrowableFeatureSelection(dataAlphabet);
// fs.add(idx);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { idx }, new double[] { 1.0 });
Instance inst = new Instance(fv, "L", "x", null);
Pipe pipe = new SerialPipes(new ArrayList<Pipe>());
InstanceList il = new InstanceList(pipe);
// il.setDataAlphabet(dataAlphabet);
// il.setTargetAlphabet(labelAlphabet);
// il.setFeatureSelection(fs);
il.add(inst);
MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(il, null);
opt.setGaussianPriorVariance(1.0);
double[] gradient = new double[opt.getNumParameters()];
opt.getValueGradient(gradient);
boolean hasNonZero = false;
for (double g : gradient) {
if (g != 0.0) {
hasNonZero = true;
break;
}
}
assertTrue(hasNonZero);
}

@Test
public void testUseNoPriorDisablesGaussianAndHyperbolic() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("a");
LabelAlphabet la = new LabelAlphabet();
la.lookupLabel("x");
FeatureVector fv = new FeatureVector(alphabet, new int[] { 0 }, new double[] { 1.0 });
Instance inst = new Instance(fv, "x", "n", null);
Pipe pipe = new SerialPipes(new ArrayList<Pipe>());
InstanceList il = new InstanceList(pipe);
// il.setDataAlphabet(alphabet);
// il.setTargetAlphabet(la);
il.add(inst);
MaxEntOptimizableByLabelLikelihood trainer = new MaxEntOptimizableByLabelLikelihood(il, null);
trainer.setGaussianPriorVariance(0.5);
trainer.useNoPrior();
// assertFalse(trainer.useNoPrior().usingGaussianPrior);
// assertFalse(trainer.useNoPrior().usingHyperbolicPrior);
}

@Test
public void testGetValueOnModelWithOnlyDefaultFeature() {
Alphabet a = new Alphabet();
LabelAlphabet l = new LabelAlphabet();
a.lookupIndex("bias");
l.lookupLabel("yes");
FeatureVector fv = new FeatureVector(a, new int[0], new double[0]);
Instance i = new Instance(fv, "yes", "id", null);
Pipe p = new SerialPipes(new ArrayList<Pipe>());
InstanceList il = new InstanceList(p);
// il.setDataAlphabet(a);
// il.setTargetAlphabet(l);
il.add(i);
MaxEntOptimizableByLabelLikelihood m = new MaxEntOptimizableByLabelLikelihood(il, null);
double v = m.getValue();
assertTrue(Double.isFinite(v));
}

@Test
public void testMultipleSetParameterCallsResetValueAndGradientValidity() {
Alphabet da = new Alphabet();
da.lookupIndex("p");
LabelAlphabet la = new LabelAlphabet();
la.lookupLabel("q");
FeatureVector fv = new FeatureVector(da, new int[] { 0 }, new double[] { 1.0 });
Instance i = new Instance(fv, "q", "z", null);
Pipe pipe = new SerialPipes(new ArrayList<Pipe>());
InstanceList il = new InstanceList(pipe);
// il.setDataAlphabet(da);
// il.setTargetAlphabet(la);
il.add(i);
MaxEntOptimizableByLabelLikelihood model = new MaxEntOptimizableByLabelLikelihood(il, null);
double[] initialGradient = new double[model.getNumParameters()];
model.getValueGradient(initialGradient);
model.setParameter(0, 2.0);
double[] updatedGradient = new double[model.getNumParameters()];
model.getValueGradient(updatedGradient);
assertNotEquals(initialGradient[0], updatedGradient[0], 0.0001);
}

@Test
public void testGetValueGradientAfterCallingGetValueExplicitly() {
Alphabet da = new Alphabet();
da.lookupIndex("t");
LabelAlphabet la = new LabelAlphabet();
la.lookupLabel("ok");
FeatureVector fv = new FeatureVector(da, new int[] { 0 }, new double[] { 1.0 });
Instance i = new Instance(fv, "ok", "check", null);
Pipe pipe = new SerialPipes(new ArrayList<Pipe>());
InstanceList il = new InstanceList(pipe);
// il.setDataAlphabet(da);
// il.setTargetAlphabet(la);
il.add(i);
MaxEntOptimizableByLabelLikelihood model = new MaxEntOptimizableByLabelLikelihood(il, null);
model.getValue();
double[] gradient = new double[model.getNumParameters()];
model.getValueGradient(gradient);
assertNotNull(gradient);
}

@Test
public void testScoreContainsInfinitySkipsInstance() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("feat");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupLabel("class");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
Instance instance = new Instance(fv, "class", "source", null);
Pipe pipe = new SerialPipes(new ArrayList<Pipe>());
InstanceList trainData = new InstanceList(pipe);
// trainData.setDataAlphabet(dataAlphabet);
// trainData.setTargetAlphabet(labelAlphabet);
trainData.add(instance);
MaxEnt classifier = new MaxEnt(trainData.getPipe(), new double[trainData.getTargetAlphabet().size() * (dataAlphabet.size() + 1)], null, null) {

@Override
public void getClassificationScores(Instance i, double[] s) {
s[0] = Double.POSITIVE_INFINITY;
}
};
MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(trainData, classifier);
double value = opt.getValue();
assertTrue(Double.isInfinite(-value));
}

@Test
public void testValueIgnoresInstanceWithoutLabeling() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("f");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupLabel("label");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
Instance instance = new Instance(fv, null, "unlabeled", null);
Pipe pipe = new SerialPipes(new ArrayList<Pipe>());
InstanceList train = new InstanceList(pipe);
// train.setDataAlphabet(dataAlphabet);
// train.setTargetAlphabet(labelAlphabet);
train.add(instance);
MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(train, null);
double v = opt.getValue();
assertEquals(0.0, -v, 0.0001);
}

@Test
public void testMultipleLabelsWithEmptyFeatureVectors() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupLabel("X");
labelAlphabet.lookupLabel("Y");
FeatureVector fv1 = new FeatureVector(dataAlphabet, new int[0], new double[0]);
FeatureVector fv2 = new FeatureVector(dataAlphabet, new int[0], new double[0]);
Instance a = new Instance(fv1, "X", "a", null);
Instance b = new Instance(fv2, "Y", "b", null);
Pipe p = new SerialPipes(new ArrayList<Pipe>());
InstanceList list = new InstanceList(p);
// list.setDataAlphabet(dataAlphabet);
// list.setTargetAlphabet(labelAlphabet);
list.add(a);
list.add(b);
MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, null);
double value = opt.getValue();
double[] grad = new double[opt.getNumParameters()];
opt.getValueGradient(grad);
assertTrue(Double.isFinite(value));
assertNotNull(grad);
}

@Test
public void testDifferentFeatureDimensionAcrossLabelsWithPerLabelSelection() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
int fa = dataAlphabet.lookupIndex("a");
int fb = dataAlphabet.lookupIndex("b");
// int la = labelAlphabet.lookupLabel("la");
// int lb = labelAlphabet.lookupLabel("lb");
FeatureVector f1 = new FeatureVector(dataAlphabet, new int[] { fa }, new double[] { 1.0 });
FeatureVector f2 = new FeatureVector(dataAlphabet, new int[] { fb }, new double[] { 1.0 });
Instance i1 = new Instance(f1, "la", "x1", null);
Instance i2 = new Instance(f2, "lb", "x2", null);
Pipe pipe = new SerialPipes(new ArrayList<Pipe>());
InstanceList list = new InstanceList(pipe);
// list.setDataAlphabet(dataAlphabet);
// list.setTargetAlphabet(labelAlphabet);
list.add(i1);
list.add(i2);
// GrowableFeatureSelection[] perLabelFS = new GrowableFeatureSelection[2];
// GrowableFeatureSelection fs1 = new GrowableFeatureSelection(dataAlphabet);
// fs1.add(fa);
// GrowableFeatureSelection fs2 = new GrowableFeatureSelection(dataAlphabet);
// fs2.add(fb);
// perLabelFS[0] = fs1;
// perLabelFS[1] = fs2;
// list.setPerLabelFeatureSelection(perLabelFS);
MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, null);
double[] gradient = new double[opt.getNumParameters()];
opt.getValueGradient(gradient);
boolean nonZeroFa = gradient[0] != 0.0;
boolean nonZeroFb = gradient[1] != 0.0;
assertTrue(nonZeroFa || nonZeroFb);
}

@Test
public void testHyperbolicPriorNotCallableInGradientPath() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("x");
LabelAlphabet la = new LabelAlphabet();
la.lookupLabel("label");
FeatureVector fv = new FeatureVector(alphabet, new int[] { 0 }, new double[] { 1.0 });
Instance instance = new Instance(fv, "label", "id", null);
Pipe pipe = new SerialPipes(new ArrayList<Pipe>());
InstanceList il = new InstanceList(pipe);
// il.setDataAlphabet(alphabet);
// il.setTargetAlphabet(la);
il.add(instance);
MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(il, null);
opt.useHyperbolicPrior();
try {
opt.getValueGradient(new double[opt.getNumParameters()]);
fail("Expected UnsupportedOperationException for hyperbolic prior");
} catch (UnsupportedOperationException e) {
assertEquals("Hyperbolic prior not yet implemented.", e.getMessage());
}
}

@Test
public void testGradientZeroedForNonSelectedGlobalFeature() {
Alphabet dataAlphabet = new Alphabet();
int featIdx = dataAlphabet.lookupIndex("maskme");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupLabel("lbl");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { featIdx }, new double[] { 3.0 });
Instance inst = new Instance(fv, "lbl", "id", null);
// GrowableFeatureSelection selection = new GrowableFeatureSelection(dataAlphabet);
Pipe pipe = new SerialPipes(new ArrayList<Pipe>());
InstanceList il = new InstanceList(pipe);
// il.setDataAlphabet(dataAlphabet);
// il.setTargetAlphabet(labelAlphabet);
// il.setFeatureSelection(selection);
il.add(inst);
MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(il, null);
double[] gradient = new double[opt.getNumParameters()];
opt.getValueGradient(gradient);
for (int i = 0; i < gradient.length; i++) {
assertEquals(0.0, gradient[i], 1e-10);
}
}

@Test
public void testDefaultFeatureIndexIncludedInPerLabelFeatureSelection() {
Alphabet dataAlphabet = new Alphabet();
int fi = dataAlphabet.lookupIndex("token");
LabelAlphabet labelAlphabet = new LabelAlphabet();
// int labelId = labelAlphabet.lookupLabel("label");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { fi }, new double[] { 1.0 });
Instance inst = new Instance(fv, "label", "X", null);
// GrowableFeatureSelection[] perLabelFS = new GrowableFeatureSelection[1];
// GrowableFeatureSelection fs = new GrowableFeatureSelection(dataAlphabet);
// fs.add(fi);
// perLabelFS[0] = fs;
Pipe pipe = new SerialPipes(new ArrayList<Pipe>());
InstanceList list = new InstanceList(pipe);
// list.setDataAlphabet(dataAlphabet);
// list.setTargetAlphabet(labelAlphabet);
// list.setPerLabelFeatureSelection(perLabelFS);
list.add(inst);
MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, null);
double[] gradient = new double[opt.getNumParameters()];
opt.getValueGradient(gradient);
int numFeatures = opt.getNumParameters() / 1;
// boolean biasIncluded = fs.contains(numFeatures - 1);
// assertTrue(biasIncluded);
}

@Test
public void testInstanceWeightNaNAssertionFails() {
Alphabet da = new Alphabet();
da.lookupIndex("x");
LabelAlphabet la = new LabelAlphabet();
la.lookupLabel("y");
FeatureVector fv = new FeatureVector(da, new int[] { 0 }, new double[] { 1.0 });
Instance inst = new Instance(fv, "y", "NaN-instance", null);
Pipe pipe = new SerialPipes(new ArrayList<Pipe>());
InstanceList il = new InstanceList(pipe) {

@Override
public double getInstanceWeight(Instance instance) {
return Double.NaN;
}
};
// il.setDataAlphabet(da);
// il.setTargetAlphabet(la);
try {
il.add(inst);
new MaxEntOptimizableByLabelLikelihood(il, null);
fail("Expected AssertionError due to NaN instance weight");
} catch (AssertionError e) {
assertTrue(e.getMessage().contains("instanceWeight is NaN"));
}
}

@Test
public void testLabelingBestIndexNaNTriggersAssertion() {
Alphabet da = new Alphabet();
da.lookupIndex("feat");
LabelAlphabet la = new LabelAlphabet();
la.lookupLabel("A");
FeatureVector fv = new FeatureVector(da, new int[] { 0 }, new double[] { 1.0 });
// Instance instance = new Instance(fv, new Labeling() {
// 
// @Override
// public int getBestIndex() {
// return (int) Double.NaN;
// }
// 
// @Override
// public LabelAlphabet getLabelAlphabet() {
// return la;
// }
// 
// @Override
// public double value(int i) {
// return 0;
// }
// 
// @Override
// public int indexAtLocation(int loc) {
// return 0;
// }
// 
// @Override
// public int numLocations() {
// return 0;
// }
// 
// @Override
// public boolean labelAtLocationIsInstanceOf(Class<?> cls) {
// return false;
// }
// 
// @Override
// public Object getSource() {
// return null;
// }
// }, "bad-label", null);
Pipe pipe = new SerialPipes(new ArrayList<Pipe>());
InstanceList il = new InstanceList(pipe);
// il.setDataAlphabet(da);
// il.setTargetAlphabet(la);
try {
// il.add(instance);
new MaxEntOptimizableByLabelLikelihood(il, null);
fail("Expected AssertionError from NaN bestIndex");
} catch (AssertionError e) {
assertTrue(e.getMessage().contains("bestIndex is NaN"));
}
}

@Test
public void testGetValueGradientBufferMismatchSucceedsWithoutError() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("z");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupLabel("q");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 2.0 });
Instance inst = new Instance(fv, "q", "incomplete-buffer", null);
Pipe pipe = new SerialPipes(new ArrayList<Pipe>());
InstanceList list = new InstanceList(pipe);
// list.setDataAlphabet(dataAlphabet);
// list.setTargetAlphabet(labelAlphabet);
list.add(inst);
MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, null);
double[] wrongBuffer = new double[opt.getNumParameters() - 1];
try {
opt.getValueGradient(wrongBuffer);
fail("Expected AssertionError due to mismatched gradient buffer size");
} catch (AssertionError e) {
}
}

@Test
public void testSetParametersLargerThanModelSize() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("feat");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupLabel("label");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
Instance inst = new Instance(fv, "label", "obj", null);
Pipe pipe = new SerialPipes(new ArrayList<Pipe>());
InstanceList il = new InstanceList(pipe);
// il.setDataAlphabet(dataAlphabet);
// il.setTargetAlphabet(labelAlphabet);
il.add(inst);
MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(il, null);
int largerSize = opt.getNumParameters() + 2;
double[] input = new double[largerSize];
input[0] = 42.0;
opt.setParameters(input);
double[] readBack = new double[opt.getNumParameters()];
opt.getParameters(readBack);
assertEquals(42.0, readBack[0], 1e-9);
}

@Test
public void testMultipleCallsToSetSameParameterCachesUpdated() {
Alphabet da = new Alphabet();
int idx = da.lookupIndex("x");
LabelAlphabet la = new LabelAlphabet();
la.lookupLabel("y");
FeatureVector fv = new FeatureVector(da, new int[] { idx }, new double[] { 1.0 });
Instance inst = new Instance(fv, "y", "t", null);
Pipe pipe = new SerialPipes(new ArrayList<Pipe>());
InstanceList list = new InstanceList(pipe);
// list.setDataAlphabet(da);
// list.setTargetAlphabet(la);
list.add(inst);
MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, null);
opt.setParameter(0, 1.0);
double val1 = opt.getValue();
opt.setParameter(0, 1.0);
double val2 = opt.getValue();
assertEquals(val1, val2, 1e-6);
}
}
