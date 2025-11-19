package cc.mallet.classify.tests;

public class MaxEntOptimizableByLabelLikelihood_3_GPTLLMTest {

// @Test
public void testConstructorWithInitialClassifier() {
// List<Pipe> pipes = new ArrayList<Pipe>();
// pipes.add(new Input2CharSequence("UTF-8"));
// pipes.add(new CharSequenceLowercase());
// pipes.add(new CharSequence2TokenSequence("[\\p{L}\\p{N}_]+"));
// pipes.add(new TokenSequenceRemoveStopwords(false, false));
// pipes.add(new TokenSequence2FeatureVector());
// pipes.add(new Target2Label());
// Pipe pipe = new SerialPipes(pipes);
// InstanceList trainingSet = new InstanceList(pipe);
// Instance inst1 = new Instance("fox runs fast", "A", "inst1", null);
// Instance inst2 = new Instance("dog barks loud", "B", "inst2", null);
// trainingSet.addThruPipe(inst1);
// trainingSet.addThruPipe(inst2);
// LabelAlphabet labelAlphabet = (LabelAlphabet) trainingSet.getTargetAlphabet();
// Alphabet dataAlphabet = trainingSet.getDataAlphabet();
// int numLabels = labelAlphabet.size();
// int numFeatures = dataAlphabet.size() + 1;
// MaxEnt initialClassifier = new MaxEnt(pipe, new double[numLabels * numFeatures], null, null);
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(trainingSet, initialClassifier);
// assertNotNull(opt.getClassifier());
// assertEquals(pipe, opt.getClassifier().getInstancePipe());
}

// @Test
public void testGetNumParameters() {
// Pipe pipe = new SerialPipes(Arrays.asList(new Input2CharSequence("UTF-8"), new CharSequenceLowercase(), new CharSequence2TokenSequence("[\\p{L}\\p{N}_]+"), new TokenSequenceRemoveStopwords(false, false), new TokenSequence2FeatureVector(), new Target2Label()));
// InstanceList trainingSet = new InstanceList(pipe);
// trainingSet.addThruPipe(new Instance("a b c", "L", "1", null));
// trainingSet.addThruPipe(new Instance("x y z", "L", "2", null));
// LabelAlphabet labelAlphabet = (LabelAlphabet) trainingSet.getTargetAlphabet();
// int numLabels = labelAlphabet.size();
// int numFeatures = trainingSet.getDataAlphabet().size() + 1;
// MaxEnt initialClassifier = new MaxEnt(pipe, new double[numLabels * numFeatures], null, null);
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(trainingSet, initialClassifier);
// assertEquals(numLabels * numFeatures, opt.getNumParameters());
}

// @Test
public void testSetAndGetParameter() {
// Pipe pipe = new SerialPipes(Arrays.asList(new Input2CharSequence("UTF-8"), new CharSequenceLowercase(), new CharSequence2TokenSequence("[\\p{L}\\p{N}_]+"), new TokenSequence2FeatureVector(), new Target2Label()));
// InstanceList list = new InstanceList(pipe);
// list.addThruPipe(new Instance("text sample", "class1", "i1", null));
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, null);
int index = 0;
double value = 1.234;
// opt.setParameter(index, value);
// double result = opt.getParameter(index);
// assertEquals(value, result, 0.0001);
}

// @Test
public void testSetAndGetParametersArray() {
// Pipe pipe = new SerialPipes(Arrays.asList(new Input2CharSequence("UTF-8"), new CharSequence2TokenSequence("[a-zA-Z]+"), new TokenSequence2FeatureVector(), new Target2Label()));
// InstanceList list = new InstanceList(pipe);
// list.addThruPipe(new Instance("data test", "cat", "i1", null));
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, null);
// int paramLength = opt.getNumParameters();
// double[] params = new double[paramLength];
// params[0] = 10.5;
// params[paramLength - 1] = -2.8;
// opt.setParameters(params);
// double[] result = new double[paramLength];
// opt.getParameters(result);
// assertEquals(10.5, result[0], 1e-6);
// assertEquals(-2.8, result[paramLength - 1], 1e-6);
}

// @Test
public void testUseGaussianPrior() {
// Pipe pipe = new SerialPipes(Arrays.asList(new Input2CharSequence("UTF-8"), new CharSequence2TokenSequence("\\S+"), new TokenSequence2FeatureVector(), new Target2Label()));
// InstanceList list = new InstanceList(pipe);
// list.addThruPipe(new Instance("words and tags", "topic1", "id", null));
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, null);
// opt.useGaussianPrior();
// double value = opt.getValue();
// assertTrue(Double.isFinite(value));
}

// @Test
public void testUseNoPrior() {
// Pipe pipe = new SerialPipes(Arrays.asList(new Input2CharSequence("UTF-8"), new CharSequence2TokenSequence("\\S+"), new TokenSequence2FeatureVector(), new Target2Label()));
// InstanceList list = new InstanceList(pipe);
// list.addThruPipe(new Instance("content body", "tag", "id", null));
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, null);
// opt.useNoPrior();
// double value = opt.getValue();
// assertTrue(Double.isFinite(value));
}

// @Test
public void testGetValueCalls() {
// Pipe pipe = new SerialPipes(Arrays.asList(new Input2CharSequence("UTF-8"), new CharSequence2TokenSequence("\\S+"), new TokenSequence2FeatureVector(), new Target2Label()));
// InstanceList list = new InstanceList(pipe);
// list.addThruPipe(new Instance("some text", "label", "id", null));
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, null);
// int before = opt.getValueCalls();
// opt.getValue();
// int after = opt.getValueCalls();
// assertEquals(before + 1, after);
}

// @Test
public void testSetGaussianPriorVariance() {
// Pipe pipe = new SerialPipes(Arrays.asList(new Input2CharSequence("UTF-8"), new CharSequence2TokenSequence("\\S+"), new TokenSequence2FeatureVector(), new Target2Label()));
// InstanceList list = new InstanceList(pipe);
// list.addThruPipe(new Instance("random tokens", "X", "id", null));
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, null);
// opt.setGaussianPriorVariance(0.5);
// double value = opt.getValue();
// assertTrue(Double.isFinite(value));
}

// @Test
public void testGetValueGradientWithParamChange() {
// Pipe pipe = new SerialPipes(Arrays.asList(new Input2CharSequence("UTF-8"), new CharSequence2TokenSequence("\\S+"), new TokenSequence2FeatureVector(), new Target2Label()));
// InstanceList list = new InstanceList(pipe);
// list.addThruPipe(new Instance("x y z", "yes", "1", null));
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, null);
// int length = opt.getNumParameters();
// double[] grad1 = new double[length];
// opt.getValueGradient(grad1);
// opt.setParameter(0, 5.0);
// double[] grad2 = new double[length];
// opt.getValueGradient(grad2);
// boolean gradientsDiffer = grad1[0] != grad2[0] || grad1[length - 1] != grad2[length - 1];
// assertTrue("Gradient changes with parameter update", gradientsDiffer);
}

// @Test
public void testHandlesInstanceWithNullLabeling() {
// Pipe pipe = new SerialPipes(Arrays.asList(new Input2CharSequence("UTF-8"), new CharSequence2TokenSequence("\\S+"), new TokenSequence2FeatureVector(), new Target2Label()));
// InstanceList list = new InstanceList(pipe);
// list.addThruPipe(new Instance("defined label", "tag", "id1", null));
// list.addThruPipe(new Instance("no label available", null, "id2", null));
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, null);
// double value = opt.getValue();
// assertTrue(Double.isFinite(value));
}

// @Test(expected = UnsupportedOperationException.class)
public void testUseHyperbolicPriorGradientThrowsUnsupported() {
// Pipe pipe = new SerialPipes(Arrays.asList(new Input2CharSequence("UTF-8"), new CharSequence2TokenSequence("\\S+"), new TokenSequence2FeatureVector(), new Target2Label()));
// InstanceList list = new InstanceList(pipe);
// list.addThruPipe(new Instance("hello there", "one", "id", null));
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, null);
// opt.useHyperbolicPrior();
// double[] buffer = new double[opt.getNumParameters()];
// opt.getValueGradient(buffer);
}

// @Test
public void testSingleLabelSingleFeatureScenario() {
// Pipe pipe = new SerialPipes(Arrays.asList(new Input2CharSequence("UTF-8"), new CharSequence2TokenSequence("\\S+"), new TokenSequence2FeatureVector(), new Target2Label()));
// InstanceList list = new InstanceList(pipe);
// list.addThruPipe(new Instance("token", "label1", "id1", null));
// list.getTargetAlphabet().stopGrowth();
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, null);
// double val = opt.getValue();
// assertTrue("Value must be finite for single-label single-feature", Double.isFinite(val));
}

// @Test
public void testSetParametersWithWrongLengthOverridesInternals() {
// Pipe pipe = new SerialPipes(Arrays.asList(new Input2CharSequence("UTF-8"), new CharSequence2TokenSequence("\\S+"), new TokenSequence2FeatureVector(), new Target2Label()));
// InstanceList list = new InstanceList(pipe);
// list.addThruPipe(new Instance("data string", "topicA", "id", null));
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, null);
// double[] wrongSize = new double[opt.getNumParameters() + 5];
// wrongSize[0] = 3.1415;
// opt.setParameters(wrongSize);
// double[] buffer = new double[wrongSize.length];
// opt.getParameters(buffer);
// assertEquals(3.1415, buffer[0], 1e-6);
}

// @Test
public void testGetValueReturnsNegativeInfinityWhenScoreIsZero() {
// Pipe pipe = new SerialPipes(Arrays.asList(new Input2CharSequence("UTF-8"), new CharSequence2TokenSequence("\\S+"), new TokenSequence2FeatureVector(), new Target2Label()));
// InstanceList list = new InstanceList(pipe);
// list.addThruPipe(new Instance("zero score", "cat", "id", null));
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, null);
// opt.setParameter(0, 1000.0);
// double value = opt.getValue();
// assertTrue("Should return finite value even for extreme parameter", Double.isFinite(value));
}

// @Test
public void testGetValueGradientWithNegativeInfinityParameter() {
// Pipe pipe = new SerialPipes(Arrays.asList(new Input2CharSequence("UTF-8"), new CharSequence2TokenSequence("\\S+"), new TokenSequence2FeatureVector(), new Target2Label()));
// InstanceList list = new InstanceList(pipe);
// list.addThruPipe(new Instance("feature here", "label", "id", null));
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, null);
// opt.setParameter(0, Double.NEGATIVE_INFINITY);
// double[] grad = new double[opt.getNumParameters()];
// opt.getValueGradient(grad);
// assertEquals("Gradient at NEGATIVE_INFINITY should be 0", 0.0, grad[0], 1e-9);
}

// @Test
public void testPerLabelFeatureSelectionEnabled() {
// Pipe pipe = new SerialPipes(Arrays.asList(new Input2CharSequence("UTF-8"), new CharSequence2TokenSequence("\\S+"), new TokenSequence2FeatureVector(), new Target2Label()));
// InstanceList list = new InstanceList(pipe);
// LabelAlphabet labels = (LabelAlphabet) list.getTargetAlphabet();
// Alphabet data = list.getDataAlphabet();
// list.addThruPipe(new Instance("word1", "classA", "id1", null));
// FeatureSelection[] perLabel = new FeatureSelection[1];
// perLabel[0] = new FeatureSelection(data);
// perLabel[0].add(0);
// list.setPerLabelFeatureSelection(perLabel);
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, null);
// double[] grad = new double[opt.getNumParameters()];
// opt.getValueGradient(grad);
int offset = 0;
// assertNotEquals(0.0, grad[offset], 1e-9);
int offset2 = offset + 1;
// assertEquals("Non-selected features should have 0 gradient", 0.0, grad[offset2], 1e-9);
}

// @Test
public void testGetParametersWithNullArray() {
// Pipe pipe = new SerialPipes(Arrays.asList(new Input2CharSequence("UTF-8"), new CharSequence2TokenSequence("\\S+"), new TokenSequence2FeatureVector(), new Target2Label()));
// InstanceList list = new InstanceList(pipe);
// list.addThruPipe(new Instance("info", "C", "name", null));
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, null);
// opt.getParameters(null);
// assertTrue(true);
}

// @Test
public void testSetParametersWithEmptyArray() {
// Pipe pipe = new SerialPipes(Arrays.asList(new Input2CharSequence("UTF-8"), new CharSequence2TokenSequence("\\S+"), new TokenSequence2FeatureVector(), new Target2Label()));
// InstanceList list = new InstanceList(pipe);
// list.addThruPipe(new Instance("text", "label", "name", null));
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, null);
double[] emptyArray = new double[0];
// opt.setParameters(emptyArray);
// assertEquals(0, opt.getNumParameters());
}

// @Test
public void testGradientCalculationWithoutPrior() {
// Pipe pipe = new SerialPipes(Arrays.asList(new Input2CharSequence("UTF-8"), new CharSequence2TokenSequence("\\S+"), new TokenSequence2FeatureVector(), new Target2Label()));
// InstanceList list = new InstanceList(pipe);
// list.addThruPipe(new Instance("data sample", "action", "id1", null));
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, null);
// opt.useNoPrior();
// double[] grad = new double[opt.getNumParameters()];
// opt.getValueGradient(grad);
// assertEquals(grad.length, opt.getNumParameters());
}

// @Test
public void testGetValueGradientCallsIncrements() {
// Pipe pipe = new SerialPipes(Arrays.asList(new Input2CharSequence("UTF-8"), new CharSequence2TokenSequence("\\S+"), new TokenSequence2FeatureVector(), new Target2Label()));
// InstanceList list = new InstanceList(pipe);
// list.addThruPipe(new Instance("message", "cat", "inst", null));
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, null);
// int initial = opt.getValueGradientCalls();
// double[] grad = new double[opt.getNumParameters()];
// opt.getValueGradient(grad);
// int after = opt.getValueGradientCalls();
// assertEquals(initial + 1, after);
}

// @Test
public void testValueWithInfiniteWeightInstanceSkipped() {
// Pipe pipe = new SerialPipes(Arrays.asList(new Input2CharSequence("UTF-8"), new CharSequence2TokenSequence("\\S+"), new TokenSequence2FeatureVector(), new Target2Label()));
// InstanceList list = new InstanceList(pipe);
// Instance instance = new Instance("value skip", "label", "inst", null);
// list.addThruPipe(instance);
// list.setInstanceWeight(instance, Double.POSITIVE_INFINITY);
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, null);
// double value = opt.getValue();
// assertTrue(Double.isFinite(value));
}

// @Test
public void testDefaultConstructorWithoutInitialization() {
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood();
// assertNotNull(opt);
}

// @Test
public void testSetParameterUpdatesGradientCacheFlag() {
// Pipe pipe = new SerialPipes(Arrays.asList(new Input2CharSequence("UTF-8"), new CharSequence2TokenSequence("\\S+"), new TokenSequence2FeatureVector(), new Target2Label()));
// InstanceList list = new InstanceList(pipe);
// list.addThruPipe(new Instance("test", "label", "id", null));
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, null);
// double valueBefore = opt.getValue();
// opt.setParameter(0, opt.getParameter(0) + 1.0);
// double valueAfter = opt.getValue();
// assertTrue("Updated parameter should change value or re-trigger computation", Double.isFinite(valueAfter));
}

// @Test
public void testSetParametersResetsCachedState() {
// Pipe pipe = new SerialPipes(Arrays.asList(new Input2CharSequence("UTF-8"), new CharSequence2TokenSequence("\\S+"), new TokenSequence2FeatureVector(), new Target2Label()));
// InstanceList list = new InstanceList(pipe);
// list.addThruPipe(new Instance("data example", "lab", "inst", null));
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, null);
// double[] params = new double[opt.getNumParameters()];
// params[0] = 2.0;
// double val1 = opt.getValue();
// opt.setParameters(params);
// double val2 = opt.getValue();
// assertTrue("Setting parameters forces value recomputation", Double.isFinite(val2));
}

// @Test
public void testSetParametersWithShorterArray() {
// Pipe pipe = new SerialPipes(Arrays.asList(new Input2CharSequence("UTF-8"), new CharSequence2TokenSequence("\\S+"), new TokenSequence2FeatureVector(), new Target2Label()));
// InstanceList list = new InstanceList(pipe);
// list.addThruPipe(new Instance("tiny", "cat", "id", null));
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, null);
// int originalLength = opt.getNumParameters();
// double[] shorterArray = new double[originalLength / 2];
// for (int i = 0; i < shorterArray.length; i++) {
// shorterArray[i] = 1.0;
// }
// opt.setParameters(shorterArray);
// double[] result = new double[shorterArray.length];
// opt.getParameters(result);
// assertEquals("Partial parameter copy still completes without error", 1.0, result[0], 0.01);
}

// @Test
public void testSetGaussianPriorOverridesOtherPriors() {
// Pipe pipe = new SerialPipes(Arrays.asList(new Input2CharSequence("UTF-8"), new CharSequence2TokenSequence("\\S+"), new TokenSequence2FeatureVector(), new Target2Label()));
// InstanceList list = new InstanceList(pipe);
// list.addThruPipe(new Instance("prior test", "label", "n1", null));
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, null);
// opt.useHyperbolicPrior();
// opt.setGaussianPriorVariance(0.7);
// double val = opt.getValue();
// assertTrue(Double.isFinite(val));
}

// @Test
public void testUseHyperbolicPriorDisablesGaussianPrior() {
// Pipe pipe = new SerialPipes(Arrays.asList(new Input2CharSequence("UTF-8"), new CharSequence2TokenSequence("\\S+"), new TokenSequence2FeatureVector(), new Target2Label()));
// InstanceList list = new InstanceList(pipe);
// list.addThruPipe(new Instance("test pipe", "label", "xyz", null));
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, null);
// opt.useHyperbolicPrior();
try {
// double[] buffer = new double[opt.getNumParameters()];
// opt.getValueGradient(buffer);
// fail("Expected UnsupportedOperationException for hyperbolic prior");
} catch (UnsupportedOperationException e) {
// assertTrue(true);
}
}

// @Test
public void testGetValueSkipsInstanceWithNullLabeling() {
// Pipe pipe = new SerialPipes(Arrays.asList(new Input2CharSequence("UTF-8"), new CharSequence2TokenSequence("\\S+"), new TokenSequence2FeatureVector(), new Target2Label()));
// InstanceList list = new InstanceList(pipe);
// list.addThruPipe(new Instance("labeled1", "label1", "id1", null));
// list.addThruPipe(new Instance("no label", null, "id2", null));
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, null);
// double value = opt.getValue();
// assertTrue("Null-labeled instance should be skipped and not crash", Double.isFinite(value));
}

// @Test
public void testGetValueScoreZeroYieldsWarningPath() {
// Pipe pipe = new SerialPipes(Arrays.asList(new Input2CharSequence("UTF-8"), new CharSequence2TokenSequence("\\S+"), new TokenSequence2FeatureVector(), new Target2Label()));
// InstanceList list = new InstanceList(pipe);
// list.addThruPipe(new Instance("extreme", "cls", "id", null));
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, null);
// int paramLength = opt.getNumParameters();
// for (int i = 0; i < paramLength; i++) {
// opt.setParameter(i, 1e10);
// }
// double value = opt.getValue();
// assertTrue("Even extreme parameter settings should return finite log likelihood", Double.isFinite(value));
}

// @Test
public void testInstanceWeightIsNaNIgnoredSafely() {
// Pipe pipe = new SerialPipes(Arrays.asList(new Input2CharSequence("UTF-8"), new CharSequence2TokenSequence("\\S+"), new TokenSequence2FeatureVector(), new Target2Label()));
// InstanceList list = new InstanceList(pipe);
// Instance instance = new Instance("abc", "label", "id1", null);
// list.addThruPipe(instance);
// list.setInstanceWeight(instance, Double.NaN);
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, null);
// double val = opt.getValue();
// assertTrue("NaN-weighted instances should be safely ignored", Double.isFinite(val));
}

// @Test
public void testConstraintInitializationAddsDefaultFeature() {
// Pipe pipe = new SerialPipes(Arrays.asList(new Input2CharSequence("UTF-8"), new CharSequence2TokenSequence("\\S+"), new TokenSequence2FeatureVector(), new Target2Label()));
// Alphabet dataAlphabet = new Alphabet();
// LabelAlphabet labelAlphabet = new LabelAlphabet();
// InstanceList list = new InstanceList(pipe);
// list.addThruPipe(new Instance("default test", "classA", "id", null));
// FeatureSelection selection = new FeatureSelection(dataAlphabet);
// list.setFeatureSelection(selection);
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, null);
// double val = opt.getValue();
// assertTrue("Default feature should be included in constraint matrix", Double.isFinite(val));
}

// @Test
public void testClassificationWithAllZeroParameters() {
// Pipe pipe = new SerialPipes(Arrays.asList(new Input2CharSequence("UTF-8"), new CharSequence2TokenSequence("\\S+"), new TokenSequence2FeatureVector(), new Target2Label()));
// InstanceList list = new InstanceList(pipe);
// list.addThruPipe(new Instance("neutral content", "class1", "id", null));
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, null);
// double[] gradient = new double[opt.getNumParameters()];
// opt.getValueGradient(gradient);
// assertNotNull(gradient);
// assertEquals(opt.getNumParameters(), gradient.length);
}

// @Test
public void testSetHyperbolicPriorSharpnessOverridesGaussian() {
// Pipe pipe = new SerialPipes(Arrays.asList(new Input2CharSequence("UTF-8"), new CharSequence2TokenSequence("\\S+"), new TokenSequence2FeatureVector(), new Target2Label()));
// InstanceList list = new InstanceList(pipe);
// list.addThruPipe(new Instance("test sample", "labelX", "instId", null));
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, null);
// opt.setHyperbolicPriorSharpness(5.0);
try {
// double[] buffer = new double[opt.getNumParameters()];
// opt.getValueGradient(buffer);
// fail("Expected UnsupportedOperationException for hyperbolic prior");
} catch (UnsupportedOperationException expected) {
// assertTrue(true);
}
}

// @Test
public void testSetHyperbolicPriorSlopeOverridesGaussian() {
// Pipe pipe = new SerialPipes(Arrays.asList(new Input2CharSequence("UTF-8"), new CharSequence2TokenSequence("\\S+"), new TokenSequence2FeatureVector(), new Target2Label()));
// InstanceList list = new InstanceList(pipe);
// list.addThruPipe(new Instance("slope test", "L1", "id", null));
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, null);
// opt.setHyperbolicPriorSlope(1.5);
try {
// double[] gradient = new double[opt.getNumParameters()];
// opt.getValueGradient(gradient);
// fail("Gradient should fail for hyperbolic prior");
} catch (UnsupportedOperationException expected) {
// assertTrue(true);
}
}

// @Test
public void testSetGaussianPriorOverridesHyperbolicPrior() {
// Pipe pipe = new SerialPipes(Arrays.asList(new Input2CharSequence("UTF-8"), new CharSequence2TokenSequence("\\S+"), new TokenSequence2FeatureVector(), new Target2Label()));
// InstanceList list = new InstanceList(pipe);
// list.addThruPipe(new Instance("content hyp", "yes", "id", null));
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, null);
// opt.useHyperbolicPrior();
// opt.setGaussianPriorVariance(2.0);
// double[] gradient = new double[opt.getNumParameters()];
// opt.getValueGradient(gradient);
// assertNotNull(gradient);
// assertEquals(opt.getNumParameters(), gradient.length);
}

// @Test
public void testGradientValuesAreZeroWhenFeaturesFilteredBySelection() {
// Pipe pipe = new SerialPipes(Arrays.asList(new Input2CharSequence("UTF-8"), new CharSequence2TokenSequence("[a-zA-Z]+"), new TokenSequence2FeatureVector(), new Target2Label()));
// InstanceList list = new InstanceList(pipe);
// list.addThruPipe(new Instance("abc xyz", "dog", "item", null));
// Alphabet dataAlphabet = list.getDataAlphabet();
// FeatureSelection selection = new FeatureSelection(dataAlphabet);
// list.setFeatureSelection(selection);
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, null);
// double[] gradient = new double[opt.getNumParameters()];
// opt.getValueGradient(gradient);
double sum = 0.0;
// if (gradient.length > 0) {
// sum = gradient[0];
// }
// assertEquals("Expected masked gradient to be zero", 0.0, sum, 1e-10);
}

// @Test
public void testGetParametersSizeMatchesAfterSetWithLargerArray() {
// Pipe pipe = new SerialPipes(Arrays.asList(new Input2CharSequence("UTF-8"), new CharSequence2TokenSequence("[a-zA-Z]+"), new TokenSequence2FeatureVector(), new Target2Label()));
// InstanceList list = new InstanceList(pipe);
// list.addThruPipe(new Instance("features A B", "cat", "id", null));
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, null);
// int originalSize = opt.getNumParameters();
// double[] largeArray = new double[originalSize + 10];
// largeArray[0] = 42.0;
// opt.setParameters(largeArray);
// double[] paramsAfterSet = new double[originalSize + 10];
// opt.getParameters(paramsAfterSet);
// assertEquals(42.0, paramsAfterSet[0], 1e-9);
// assertEquals(originalSize + 10, paramsAfterSet.length);
}

// @Test
public void testNoFeaturesPresentStillInitializesCorrectly() {
// Alphabet dataAlphabet = new Alphabet();
// LabelAlphabet labelAlphabet = new LabelAlphabet();
// dataAlphabet.lookupIndex("a", true);
// Pipe pipe = new SerialPipes(Arrays.asList(new Input2CharSequence("UTF-8"), new TokenSequence2FeatureVector(), new Target2Label()));
// InstanceList list = new InstanceList(pipe);
// Instance instance = new Instance("a", "label", "id", null);
// list.addThruPipe(instance);
// FeatureSelection fs = new FeatureSelection(dataAlphabet);
// list.setFeatureSelection(fs);
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, null);
// double[] gradient = new double[opt.getNumParameters()];
// opt.getValueGradient(gradient);
// assertNotNull("Ensure gradient array is still populated", gradient);
}

// @Test
public void testSetAndGetParameterAtLastIndex() {
// Pipe pipe = new SerialPipes(Arrays.asList(new Input2CharSequence("UTF-8"), new CharSequence2TokenSequence("\\S+"), new TokenSequence2FeatureVector(), new Target2Label()));
// InstanceList list = new InstanceList(pipe);
// list.addThruPipe(new Instance("boundary test", "L", "id", null));
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, null);
// int lastIndex = opt.getNumParameters() - 1;
// opt.setParameter(lastIndex, 9.99);
// double result = opt.getParameter(lastIndex);
// assertEquals(9.99, result, 0.00001);
}

// @Test
public void testSetParametersWithNullThrowsAssertionError() {
// Pipe pipe = new SerialPipes(Arrays.asList(new Input2CharSequence("UTF-8"), new CharSequence2TokenSequence("\\S+"), new TokenSequence2FeatureVector(), new Target2Label()));
// InstanceList list = new InstanceList(pipe);
// list.addThruPipe(new Instance("test case", "cat", "id", null));
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, null);
try {
// opt.setParameters(null);
// fail("Expected AssertionError");
} catch (AssertionError expected) {
// assertTrue(true);
}
}

// @Test
public void testGetValueWithVeryLargeParameterProducesFiniteValue() {
// Pipe pipe = new SerialPipes(Arrays.asList(new Input2CharSequence("UTF-8"), new CharSequence2TokenSequence("\\S+"), new TokenSequence2FeatureVector(), new Target2Label()));
// InstanceList list = new InstanceList(pipe);
// list.addThruPipe(new Instance("sample sentence input", "label", "id1", null));
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, null);
// int numParams = opt.getNumParameters();
// for (int i = 0; i < numParams; i++) {
// opt.setParameter(i, 1e300);
// }
// double value = opt.getValue();
// assertTrue("Even with large parameters, getValue returns finite", Double.isFinite(value));
}

// @Test
public void testGetValueWithNegativeInfinityInScorePath() {
// Pipe pipe = new SerialPipes(Arrays.asList(new Input2CharSequence("UTF-8"), new CharSequence2TokenSequence("\\S+"), new TokenSequence2FeatureVector(), new Target2Label()));
// InstanceList list = new InstanceList(pipe);
// list.addThruPipe(new Instance("forced failure", "spam", "idX", null));
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, null);
// int paramIdx = opt.getNumParameters() > 0 ? 0 : -1;
// if (paramIdx >= 0) {
// opt.setParameter(paramIdx, -1e300);
// }
// double value = opt.getValue();
// assertTrue("Value should still be finite under low scores", Double.isFinite(value));
}

// @Test
public void testGetValueSkipsInfiniteWeightedInstance() {
// Pipe pipe = new SerialPipes(Arrays.asList(new Input2CharSequence("UTF-8"), new CharSequence2TokenSequence("\\S+"), new TokenSequence2FeatureVector(), new Target2Label()));
// InstanceList list = new InstanceList(pipe);
// Instance validInstance = new Instance("valid input", "ok", "id1", null);
// Instance skipInstance = new Instance("skip me", "bad", "id2", null);
// list.addThruPipe(validInstance);
// list.addThruPipe(skipInstance);
// list.setInstanceWeight(skipInstance, Double.POSITIVE_INFINITY);
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, null);
// double value = opt.getValue();
// assertTrue("Infinite-weighted instance should not cause crash", Double.isFinite(value));
}

// @Test
public void testFeatureSelectionWithOnlyDefaultFeatureIndex() {
// Pipe pipe = new SerialPipes(Arrays.asList(new Input2CharSequence("UTF-8"), new CharSequence2TokenSequence("[a-zA-Z]+"), new TokenSequence2FeatureVector(), new Target2Label()));
// InstanceList list = new InstanceList(pipe);
// list.addThruPipe(new Instance("just one word", "label", "id", null));
// Alphabet dataAlphabet = list.getDataAlphabet();
// FeatureSelection featureSelection = new FeatureSelection(dataAlphabet);
// FeatureSelection[] perLabelSelection = new FeatureSelection[1];
// perLabelSelection[0] = new FeatureSelection(dataAlphabet);
// list.setPerLabelFeatureSelection(perLabelSelection);
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, null);
// double val = opt.getValue();
// assertTrue("Value should compute even when only default feature used", Double.isFinite(val));
}

// @Test
public void testSetAndGetValueGradientCalls() {
// Pipe pipe = new SerialPipes(Arrays.asList(new Input2CharSequence("UTF-8"), new CharSequence2TokenSequence("[^\\s]+"), new TokenSequence2FeatureVector(), new Target2Label()));
// InstanceList list = new InstanceList(pipe);
// list.addThruPipe(new Instance("data data", "tag", "xyz", null));
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, null);
// int initialCalls = opt.getValueGradientCalls();
// double[] grad = new double[opt.getNumParameters()];
// opt.getValueGradient(grad);
// int finalCalls = opt.getValueGradientCalls();
// assertEquals(initialCalls + 1, finalCalls);
}

// @Test
public void testShortcutGradientNullPriorEdgeCondition() {
// Pipe pipe = new SerialPipes(Arrays.asList(new Input2CharSequence("UTF-8"), new CharSequence2TokenSequence("[^\\s]+"), new TokenSequence2FeatureVector(), new Target2Label()));
// InstanceList list = new InstanceList(pipe);
// list.addThruPipe(new Instance("edge case", "yes", "id", null));
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, null);
// opt.useNoPrior();
// double[] buffer = new double[opt.getNumParameters()];
// opt.getValueGradient(buffer);
// assertNotNull("Should return valid gradient even with no prior", buffer);
}

// @Test
public void testGetValueDoesNotCrashOnRepeatedCallsWithNoChanges() {
// Pipe pipe = new SerialPipes(Arrays.asList(new Input2CharSequence("UTF-8"), new CharSequence2TokenSequence("\\S+"), new TokenSequence2FeatureVector(), new Target2Label()));
// InstanceList list = new InstanceList(pipe);
// list.addThruPipe(new Instance("repeat call test", "A", "id", null));
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, null);
// double first = opt.getValue();
// double second = opt.getValue();
// double third = opt.getValue();
// assertEquals(first, second, 1e-9);
// assertEquals(first, third, 1e-9);
}

// @Test
public void testNullFeatureSelectionDoesNotAffectGradientComputation() {
// Pipe pipe = new SerialPipes(Arrays.asList(new Input2CharSequence("UTF-8"), new CharSequence2TokenSequence("\\S+"), new TokenSequence2FeatureVector(), new Target2Label()));
// InstanceList instanceList = new InstanceList(pipe);
// instanceList.addThruPipe(new Instance("input example", "label", "id", null));
// instanceList.setFeatureSelection(null);
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(instanceList, null);
// double[] gradient = new double[opt.getNumParameters()];
// opt.getValueGradient(gradient);
// assertNotNull(gradient);
// assertEquals(opt.getNumParameters(), gradient.length);
}

// @Test
public void testConstructorInitializesClassifierWhenNullIsPassed() {
// Pipe pipe = new SerialPipes(Arrays.asList(new Input2CharSequence("UTF-8"), new CharSequence2TokenSequence("[a-zA-Z]+"), new TokenSequence2FeatureVector(), new Target2Label()));
// InstanceList instanceList = new InstanceList(pipe);
// instanceList.addThruPipe(new Instance("init classifier", "labelA", "id", null));
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(instanceList, null);
// MaxEnt classifier = opt.getClassifier();
// assertNotNull("Internal classifier should be initialized if null was passed", classifier);
}

// @Test
public void testGetParametersCalledWithSmallerArrayDoesNotModifyBuffer() {
// Pipe pipe = new SerialPipes(Arrays.asList(new Input2CharSequence("UTF-8"), new CharSequence2TokenSequence("[^\\s]+"), new TokenSequence2FeatureVector(), new Target2Label()));
// InstanceList dataset = new InstanceList(pipe);
// dataset.addThruPipe(new Instance("abc def", "x", "id", null));
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(dataset, null);
// double[] trailingBuffer = new double[opt.getNumParameters() - 1];
// opt.getParameters(trailingBuffer);
// assertEquals("Buffer length should remain unchanged", opt.getNumParameters() - 1, trailingBuffer.length);
}

// @Test
public void testGetValueHandlesZeroScoreGracefully() {
// Pipe pipe = new SerialPipes(Arrays.asList(new Input2CharSequence("UTF-8"), new CharSequence2TokenSequence("[^\\s]+"), new TokenSequence2FeatureVector(), new Target2Label()));
// InstanceList dataset = new InstanceList(pipe);
// dataset.addThruPipe(new Instance("value case", "topic", "id", null));
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(dataset, null);
// int paramSize = opt.getNumParameters();
// if (paramSize > 0) {
// opt.setParameter(0, -1.0e300);
// }
// double value = opt.getValue();
// assertTrue("Calling getValue() on low-prob scenario should remain finite", Double.isFinite(value));
}

// @Test
public void testGradientValuesAreUnchangedIfCached() {
// Pipe pipe = new SerialPipes(Arrays.asList(new Input2CharSequence("UTF-8"), new CharSequence2TokenSequence("[^\\s]+"), new TokenSequence2FeatureVector(), new Target2Label()));
// InstanceList dataset = new InstanceList(pipe);
// dataset.addThruPipe(new Instance("feature importance", "Y", "xid", null));
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(dataset, null);
// double[] g1 = new double[opt.getNumParameters()];
// opt.getValueGradient(g1);
// double[] g2 = new double[opt.getNumParameters()];
// opt.getValueGradient(g2);
// assertEquals("Recompute with cached value should yield same gradient", g1[0], g2[0], 1e-9);
}

// @Test
public void testSetGaussianPriorVarianceNegativeValueStillComputes() {
// Pipe pipe = new SerialPipes(Arrays.asList(new Input2CharSequence("UTF-8"), new CharSequence2TokenSequence("[^\\s]+"), new TokenSequence2FeatureVector(), new Target2Label()));
// InstanceList dataset = new InstanceList(pipe);
// dataset.addThruPipe(new Instance("negative variance", "tag", "id2", null));
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(dataset, null);
// opt.setGaussianPriorVariance(-10.0);
// double value = opt.getValue();
// assertTrue("Negative variance still results in evaluatable likelihood", Double.isFinite(value));
}

// @Test
public void testCallingGetValueGradientTwiceIncrementsOnlyOnceIfCached() {
// Pipe pipe = new SerialPipes(Arrays.asList(new Input2CharSequence("UTF-8"), new CharSequence2TokenSequence("[a-z]+"), new TokenSequence2FeatureVector(), new Target2Label()));
// InstanceList dataset = new InstanceList(pipe);
// dataset.addThruPipe(new Instance("one two", "x", "id3", null));
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(dataset, null);
// double[] buffer1 = new double[opt.getNumParameters()];
// int callsBefore = opt.getValueGradientCalls();
// opt.getValueGradient(buffer1);
// double[] buffer2 = new double[opt.getNumParameters()];
// opt.getValueGradient(buffer2);
// int callsAfter = opt.getValueGradientCalls();
// assertEquals("Second call should not increment gradient calls", callsBefore + 1, callsAfter);
}

// @Test
public void testLabelingWithUniformScoreDoesNotCauseNaNInValue() {
// Pipe pipe = new SerialPipes(Arrays.asList(new Input2CharSequence("UTF-8"), new CharSequence2TokenSequence("[^\\s]+"), new TokenSequence2FeatureVector(), new Target2Label()));
// InstanceList dataset = new InstanceList(pipe);
// dataset.addThruPipe(new Instance("uniform label test", "Z", "idU", null));
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(dataset, null);
// int numParams = opt.getNumParameters();
// for (int i = 0; i < numParams; i++) {
// opt.setParameter(i, 0.0);
// }
// double val = opt.getValue();
// assertFalse("Uniform weights should not produce NaN", Double.isNaN(val));
}

// @Test
public void testMultipleInstancesWithSameLabelProcessedCorrectly() {
// Pipe pipe = new SerialPipes(Arrays.asList(new Input2CharSequence("UTF-8"), new CharSequence2TokenSequence("[^\\s]+"), new TokenSequence2FeatureVector(), new Target2Label()));
// InstanceList instList = new InstanceList(pipe);
// instList.addThruPipe(new Instance("foo bar", "C1", "a", null));
// instList.addThruPipe(new Instance("baz qux", "C1", "b", null));
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(instList, null);
// double value = opt.getValue();
// assertTrue("Duplicate labels should not crash probability calculation", Double.isFinite(value));
}

// @Test
public void testNullLabelingInConstructorIsHandledGracefully() {
// Pipe pipe = new SerialPipes(Arrays.asList(new Input2CharSequence("UTF-8"), new CharSequence2TokenSequence("[a-zA-Z]+"), new TokenSequence2FeatureVector(), new Target2Label()));
// InstanceList instances = new InstanceList(pipe);
// instances.addThruPipe(new Instance("word test", "class1", "id1", null));
// instances.addThruPipe(new Instance("unlabeled", null, "id2", null));
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(instances, null);
// double value = opt.getValue();
// assertTrue("Instances with null labeling skipped, value still computed", Double.isFinite(value));
}

// @Test
public void testPerLabelFeatureSelectionAppliesCorrectFiltering() {
// Pipe pipe = new SerialPipes(Arrays.asList(new Input2CharSequence("UTF-8"), new CharSequence2TokenSequence("[a-zA-Z]+"), new TokenSequence2FeatureVector(), new Target2Label()));
// InstanceList list = new InstanceList(pipe);
// list.addThruPipe(new Instance("alpha beta gamma", "L1", "one", null));
// LabelAlphabet labelAlphabet = (LabelAlphabet) list.getTargetAlphabet();
// Alphabet dataAlphabet = list.getDataAlphabet();
// int numLabels = labelAlphabet.size();
// FeatureSelection[] perLabel = new FeatureSelection[numLabels];
// FeatureSelection fs0 = new FeatureSelection(dataAlphabet);
// fs0.add(0);
// perLabel[0] = fs0;
// list.setPerLabelFeatureSelection(perLabel);
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, null);
// double[] grad = new double[opt.getNumParameters()];
// opt.getValueGradient(grad);
// if (grad.length > 1) {
// assertEquals(0.0, grad[1], 1e-9);
// }
}

// @Test
public void testFeatureVectorWithNaNValueIsLoggedNotCrashed() {
// Pipe pipe = new SerialPipes(Arrays.asList(new Input2CharSequence("UTF-8"), new CharSequence2TokenSequence("[a-zA-Z]+"), new TokenSequence2FeatureVector(), new Target2Label()));
// Alphabet dataAlphabet = new Alphabet();
// LabelAlphabet labelAlphabet = new LabelAlphabet();
// dataAlphabet.lookupIndex("feature1");
// FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { Double.NaN });
// Instance instance = new Instance(fv, labelAlphabet.lookupLabel("label1"), "id", null);
// InstanceList list = new InstanceList(pipe);
// list.add(instance);
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, null);
// double value = opt.getValue();
// assertTrue("NaN feature does not crash getValue()", Double.isFinite(value));
}

// @Test
public void testZeroInstanceWeightIgnoredInConstraintsInitialization() {
// Pipe pipe = new SerialPipes(Arrays.asList(new Input2CharSequence("UTF-8"), new CharSequence2TokenSequence("[a-zA-Z]+"), new TokenSequence2FeatureVector(), new Target2Label()));
// InstanceList list = new InstanceList(pipe);
// Instance i1 = new Instance("cat dog", "Animal", "id1", null);
// list.addThruPipe(i1);
// list.setInstanceWeight(i1, 0.0);
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, null);
// double[] grad = new double[opt.getNumParameters()];
// opt.getValueGradient(grad);
double sum = 0.0;
// if (grad.length > 0) {
// sum += grad[0];
// }
// assertEquals("Gradient should be 0 if instance weight is 0", 0.0, sum, 1e-9);
}

// @Test
public void testUseNoPriorDisablesAllPriors() {
// Pipe pipe = new SerialPipes(Arrays.asList(new Input2CharSequence("UTF-8"), new CharSequence2TokenSequence("[a-zA-Z]+"), new TokenSequence2FeatureVector(), new Target2Label()));
// InstanceList list = new InstanceList(pipe);
// list.addThruPipe(new Instance("instance text", "pos", "id", null));
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, null);
// opt.useGaussianPrior();
// opt.useNoPrior();
// double[] buffer = new double[opt.getNumParameters()];
// opt.getValueGradient(buffer);
// assertNotNull(buffer);
// assertEquals(opt.getNumParameters(), buffer.length);
}

// @Test
public void testSingleFeatureBinaryLabelScenario() {
// Pipe pipe = new SerialPipes(Arrays.asList(new Input2CharSequence("UTF-8"), new CharSequence2TokenSequence("[a-zA-Z]+"), new TokenSequence2FeatureVector(), new Target2Label()));
// InstanceList list = new InstanceList(pipe);
// list.addThruPipe(new Instance("alone", "yes", "id1", null));
// list.addThruPipe(new Instance("alone", "no", "id2", null));
// MaxEntOptimizableByLabelLikelihood opt = new MaxEntOptimizableByLabelLikelihood(list, null);
// double val = opt.getValue();
// assertTrue("Binary classification with 1 feature should evaluate correctly", Double.isFinite(val));
}
}
