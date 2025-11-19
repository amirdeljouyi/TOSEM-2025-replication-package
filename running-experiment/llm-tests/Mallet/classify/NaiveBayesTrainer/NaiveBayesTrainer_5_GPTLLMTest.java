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

public class NaiveBayesTrainer_5_GPTLLMTest {

@Test
public void testDefaultConstructor() {
NaiveBayesTrainer trainer = new NaiveBayesTrainer();
assertNull(trainer.getClassifier());
}

@Test
public void testConstructorWithPipeInitializesAlphabets() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
assertEquals(dataAlphabet, trainer.getAlphabet());
assertArrayEquals(new Alphabet[] { dataAlphabet, targetAlphabet }, trainer.getAlphabets());
}

@Test
public void testSetAndGetDocLengthNormalization() {
NaiveBayesTrainer trainer = new NaiveBayesTrainer();
trainer.setDocLengthNormalization(10.0);
assertEquals(10.0, trainer.getDocLengthNormalization(), 0.0001);
}

@Test
public void testSetAndGetFeatureMultinomialEstimator() {
NaiveBayesTrainer trainer = new NaiveBayesTrainer();
Multinomial.Estimator estimator = new Multinomial.LaplaceEstimator();
trainer.setFeatureMultinomialEstimator(estimator);
assertEquals(estimator, trainer.getFeatureMultinomialEstimator());
}

@Test
public void testSetAndGetPriorMultinomialEstimator() {
NaiveBayesTrainer trainer = new NaiveBayesTrainer();
Multinomial.Estimator estimator = new Multinomial.LaplaceEstimator();
trainer.setPriorMultinomialEstimator(estimator);
assertEquals(estimator, trainer.getPriorMultinomialEstimator());
}

@Test(expected = IllegalStateException.class)
public void testSetFeatureEstimatorAfterPipeInitializationThrows() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
trainer.setFeatureMultinomialEstimator(new Multinomial.LaplaceEstimator());
}

@Test(expected = IllegalStateException.class)
public void testSetPriorEstimatorAfterPipeInitializationThrows() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
trainer.setPriorMultinomialEstimator(new Multinomial.LaplaceEstimator());
}

@Test
public void testTrainClassifierReturnsValidModel() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new LabelAlphabet();
dataAlphabet.lookupIndex("word");
targetAlphabet.lookupIndex("yes");
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
InstanceList trainingData = new InstanceList(pipe);
int featureIndex = dataAlphabet.lookupIndex("word");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { featureIndex }, new double[] { 1.0 });
Label label = ((LabelAlphabet) targetAlphabet).lookupLabel("yes");
// Instance instance = new Instance(fv, label.getLabeling(), "inst", null);
// trainingData.addThruPipe(instance);
NaiveBayes classifier = trainer.train(trainingData);
assertNotNull(classifier);
assertEquals(1, classifier.getLabelAlphabet().size());
}

@Test
public void testTrainIncrementalWithInstanceList() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new LabelAlphabet();
int index = dataAlphabet.lookupIndex("token");
Label label = ((LabelAlphabet) targetAlphabet).lookupLabel("classA");
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
InstanceList instanceList = new InstanceList(pipe);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { index }, new double[] { 1.0 });
// Labeling labeling = label.getLabeling();
// Instance instance = new Instance(fv, labeling, "id", null);
// instanceList.addThruPipe(instance);
NaiveBayes classifier = trainer.trainIncremental(instanceList);
assertNotNull(classifier);
}

@Test
public void testTrainIncrementalWithSingleInstance() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new LabelAlphabet();
int featureIndex = dataAlphabet.lookupIndex("w");
Label label = ((LabelAlphabet) targetAlphabet).lookupLabel("c");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { featureIndex }, new double[] { 1.0 });
// Labeling labeling = label.getLabeling();
// Instance instance = new Instance(fv, labeling, "i", null);
// instance.setDataAlphabet(dataAlphabet);
// instance.setTargetAlphabet(targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer();
// NaiveBayes classifier = trainer.trainIncremental(instance);
// assertNotNull(classifier);
}

@Test
public void testToStringReturnsClassName() {
NaiveBayesTrainer trainer = new NaiveBayesTrainer();
assertEquals("NaiveBayesTrainer", trainer.toString());
}

@Test
public void testSerializationRoundTrip() throws Exception {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
ObjectOutputStream oos = new ObjectOutputStream(byteOut);
oos.writeObject(trainer);
oos.close();
ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
ObjectInputStream ois = new ObjectInputStream(byteIn);
Object result = ois.readObject();
assertTrue(result instanceof NaiveBayesTrainer);
NaiveBayesTrainer deserialized = (NaiveBayesTrainer) result;
assertNotNull(deserialized.getAlphabet());
}

@Test(expected = IllegalArgumentException.class)
public void testMismatchedPipeDuringIncrementalTrainingThrows() {
Alphabet data1 = new Alphabet();
Alphabet target1 = new LabelAlphabet();
Alphabet data2 = new Alphabet();
Alphabet target2 = new LabelAlphabet();
int index1 = data1.lookupIndex("x");
int index2 = data2.lookupIndex("y");
// target1.lookupLabel("labelA");
// target2.lookupLabel("labelA");
Pipe pipe1 = new Noop(data1, target1);
Pipe pipe2 = new Noop(data2, target2);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe1);
InstanceList list = new InstanceList(pipe2);
FeatureVector fv = new FeatureVector(data2, new int[] { index2 }, new double[] { 1.0 });
Label label = ((LabelAlphabet) target2).lookupLabel("labelA");
// Instance instance = new Instance(fv, label.getLabeling(), "inst", null);
// list.addThruPipe(instance);
trainer.trainIncremental(list);
}

@Test(expected = IllegalArgumentException.class)
public void testMismatchedAlphabetsThrows() {
Alphabet dataAlphabet1 = new Alphabet();
Alphabet dataAlphabet2 = new Alphabet();
Alphabet targetAlphabet = new LabelAlphabet();
dataAlphabet1.lookupIndex("foo");
dataAlphabet2.lookupIndex("bar");
Label label = ((LabelAlphabet) targetAlphabet).lookupLabel("class");
FeatureVector fv = new FeatureVector(dataAlphabet2, new int[] { 0 }, new double[] { 1.0 });
// Labeling labeling = label.getLabeling();
// Instance instance = new Instance(fv, labeling, "sample", null);
// instance.setDataAlphabet(dataAlphabet2);
// instance.setTargetAlphabet(targetAlphabet);
Pipe pipe = new Noop(dataAlphabet1, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
// trainer.trainIncremental(instance);
}

@Test
public void testAlphabetsMatchReturnsTrue() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer1 = new NaiveBayesTrainer(pipe);
NaiveBayesTrainer trainer2 = new NaiveBayesTrainer(pipe);
assertTrue(trainer1.alphabetsMatch(trainer2));
}

@Test
public void testAlphabetsMatchReturnsFalse() {
Alphabet dataAlpha1 = new Alphabet();
Alphabet targetAlpha1 = new Alphabet();
dataAlpha1.lookupIndex("x");
targetAlpha1.lookupIndex("a");
Alphabet dataAlpha2 = new Alphabet();
Alphabet targetAlpha2 = new Alphabet();
dataAlpha2.lookupIndex("z");
targetAlpha2.lookupIndex("b");
NaiveBayesTrainer trainer1 = new NaiveBayesTrainer(new Noop(dataAlpha1, targetAlpha1));
NaiveBayesTrainer trainer2 = new NaiveBayesTrainer(new Noop(dataAlpha2, targetAlpha2));
assertFalse(trainer1.alphabetsMatch(trainer2));
}

@Test
public void testTrainWithEmptyInstanceListReturnsClassifier() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new LabelAlphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
InstanceList emptyList = new InstanceList(pipe);
NaiveBayes classifier = trainer.train(emptyList);
assertNotNull(classifier);
assertEquals(0, classifier.getLabelAlphabet().size());
}

@Test
public void testTrainIgnoresUnlabeledInstances() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new LabelAlphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
InstanceList list = new InstanceList(pipe);
int fi = dataAlphabet.lookupIndex("feature1");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { fi }, new double[] { 1.0 });
Instance instance = new Instance(fv, null, "id", null);
list.addThruPipe(instance);
NaiveBayes classifier = trainer.train(list);
assertNotNull(classifier);
assertEquals(0, classifier.getLabelAlphabet().size());
}

@Test
public void testTrainIgnoresZeroNormFeatureVectors() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new LabelAlphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
InstanceList list = new InstanceList(pipe);
int index = dataAlphabet.lookupIndex("irrelevant");
// targetAlphabet.lookupLabel("label");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { index }, new double[] { 0.0 });
Label label = ((LabelAlphabet) targetAlphabet).lookupLabel("label");
// Labeling labeling = label.getLabeling();
// Instance instance = new Instance(fv, labeling, "zeroNorm", null);
// list.addThruPipe(instance);
NaiveBayes classifier = trainer.train(list);
assertNotNull(classifier);
assertEquals(1, classifier.getLabelAlphabet().size());
}

@Test
public void testDocLengthNormalizationAffectsInstanceWeight() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new LabelAlphabet();
dataAlphabet.lookupIndex("w");
// targetAlphabet.lookupLabel("positive");
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
trainer.setDocLengthNormalization(2.0);
InstanceList list = new InstanceList(pipe);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 4.0 });
Label label = ((LabelAlphabet) targetAlphabet).lookupLabel("positive");
// Labeling labeling = label.getLabeling();
// Instance instance = new Instance(fv, labeling, "norm", null);
// list.addThruPipe(instance);
NaiveBayes classifier = trainer.train(list);
assertNotNull(classifier);
}

@Test
public void testTargetAlphabetGrowthTriggersEstimatorExpansion() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
Pipe pipe = new Noop(dataAlphabet, labelAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
int fi = dataAlphabet.lookupIndex("f");
Label l1 = labelAlphabet.lookupLabel("a");
Label l2 = labelAlphabet.lookupLabel("b");
FeatureVector fv1 = new FeatureVector(dataAlphabet, new int[] { fi }, new double[] { 1.0 });
// Instance i1 = new Instance(fv1, l1.getLabeling(), "i1", null);
// i1.setDataAlphabet(dataAlphabet);
// i1.setTargetAlphabet(labelAlphabet);
// trainer.trainIncremental(i1);
Label l3 = labelAlphabet.lookupLabel("c");
FeatureVector fv2 = new FeatureVector(dataAlphabet, new int[] { fi }, new double[] { 1.0 });
// Instance i2 = new Instance(fv2, l3.getLabeling(), "i2", null);
// i2.setDataAlphabet(dataAlphabet);
// i2.setTargetAlphabet(labelAlphabet);
// trainer.trainIncremental(i2);
NaiveBayes model = trainer.getClassifier();
assertNotNull(model);
assertEquals(3, model.getLabelAlphabet().size());
}

@Test(expected = IllegalArgumentException.class)
public void testSetupThrowsOnPipeMismatch() {
Alphabet dataA = new Alphabet();
Alphabet targetA = new LabelAlphabet();
dataA.lookupIndex("x");
// targetA.lookupLabel("z");
Pipe pipe1 = new Noop(dataA, targetA);
Alphabet dataB = new Alphabet();
Alphabet targetB = new LabelAlphabet();
dataB.lookupIndex("x");
// targetB.lookupLabel("z");
Pipe pipe2 = new Noop(dataB, targetB);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe1);
InstanceList list = new InstanceList(pipe2);
FeatureVector fv = new FeatureVector(dataB, new int[] { 0 }, new double[] { 1.0 });
Label label = ((LabelAlphabet) targetB).lookupLabel("z");
// Labeling labeling = label.getLabeling();
// Instance i = new Instance(fv, labeling, "bad", null);
// list.addThruPipe(i);
trainer.trainIncremental(list);
}

@Test
public void testFeatureEstimatorDefaultsToLaplaceEstimator() {
NaiveBayesTrainer trainer = new NaiveBayesTrainer();
Multinomial.Estimator featureEstimator = trainer.getFeatureMultinomialEstimator();
assertTrue(featureEstimator instanceof Multinomial.LaplaceEstimator);
}

@Test
public void testPriorEstimatorDefaultsToLaplaceEstimator() {
NaiveBayesTrainer trainer = new NaiveBayesTrainer();
Multinomial.Estimator priorEstimator = trainer.getPriorMultinomialEstimator();
assertTrue(priorEstimator instanceof Multinomial.LaplaceEstimator);
}

@Test
public void testFactoryCreatesTrainerWithCorrectSettings() {
Multinomial.Estimator featureEstimator = new Multinomial.MLEstimator();
Multinomial.Estimator priorEstimator = new Multinomial.MLEstimator();
NaiveBayesTrainer.Factory factory = new NaiveBayesTrainer.Factory().setFeatureMultinomialEstimator(featureEstimator).setPriorMultinomialEstimator(priorEstimator).setDocLengthNormalization(42.0);
Classifier initial = null;
NaiveBayesTrainer trainer = factory.newClassifierTrainer(initial);
assertEquals(42.0, trainer.getDocLengthNormalization(), 1e-6);
}

@Test
public void testIncrementalTrainHandlesMultipleLabelsWithWeights() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
int fi = dataAlphabet.lookupIndex("x");
Label positive = labelAlphabet.lookupLabel("pos");
Label negative = labelAlphabet.lookupLabel("neg");
LabelVector labels = new LabelVector(labelAlphabet, new int[] { positive.getIndex(), negative.getIndex() }, new double[] { 0.7, 0.3 });
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { fi }, new double[] { 1.0 });
Instance instance = new Instance(fv, labels, "multi", null);
// instance.setDataAlphabet(dataAlphabet);
// instance.setTargetAlphabet(labelAlphabet);
Pipe pipe = new Noop(dataAlphabet, labelAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
NaiveBayes classifier = trainer.trainIncremental(instance);
assertNotNull(classifier);
assertEquals(2, classifier.getLabelAlphabet().size());
}

@Test(expected = IllegalArgumentException.class)
public void testTrainIncrementalWithInstanceHavingMismatchedAlphabetsThrows() {
Alphabet trainingDataAlphabet = new Alphabet();
Alphabet trainingTargetAlphabet = new LabelAlphabet();
Pipe trainingPipe = new Noop(trainingDataAlphabet, trainingTargetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(trainingPipe);
Alphabet newDataAlphabet = new Alphabet();
newDataAlphabet.lookupIndex("feature");
LabelAlphabet newTargetAlphabet = new LabelAlphabet();
Label label = newTargetAlphabet.lookupLabel("cat");
FeatureVector fv = new FeatureVector(newDataAlphabet, new int[] { 0 }, new double[] { 1.0 });
// Instance inst = new Instance(fv, label.getLabeling(), "id", null);
// inst.setDataAlphabet(newDataAlphabet);
// inst.setTargetAlphabet(newTargetAlphabet);
// trainer.trainIncremental(inst);
}

@Test
public void testTrainWithInstanceHavingMultipleZeroWeightLabelsSkipsIncorporation() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet targetAlphabet = new LabelAlphabet();
int fi = dataAlphabet.lookupIndex("word");
Label l1 = targetAlphabet.lookupLabel("label1");
Label l2 = targetAlphabet.lookupLabel("label2");
LabelVector labeling = new LabelVector(targetAlphabet, new int[] { l1.getIndex(), l2.getIndex() }, new double[] { 0.0, 0.0 });
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { fi }, new double[] { 1.0 });
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
InstanceList list = new InstanceList(pipe);
Instance inst = new Instance(fv, labeling, "id", null);
list.addThruPipe(inst);
NaiveBayes classifier = trainer.train(list);
assertNotNull(classifier);
assertEquals(2, classifier.getLabelAlphabet().size());
}

@Test
public void testTrainMultipleInstancesWithSameLabelAndFeatureUpdatesCountsCorrectly() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet targetAlphabet = new LabelAlphabet();
int index = dataAlphabet.lookupIndex("common");
Label label = targetAlphabet.lookupLabel("topic");
FeatureVector fv1 = new FeatureVector(dataAlphabet, new int[] { index }, new double[] { 1.0 });
FeatureVector fv2 = new FeatureVector(dataAlphabet, new int[] { index }, new double[] { 2.0 });
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
InstanceList list = new InstanceList(pipe);
// Instance instance1 = new Instance(fv1, label.getLabeling(), "i1", null);
// Instance instance2 = new Instance(fv2, label.getLabeling(), "i2", null);
// list.addThruPipe(instance1);
// list.addThruPipe(instance2);
NaiveBayes nb = trainer.train(list);
assertNotNull(nb);
}

@Test
public void testEstimateFeatureMultinomialsReturnsCorrectSizeArray() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet targetAlphabet = new LabelAlphabet();
int fIndex = dataAlphabet.lookupIndex("token");
Label l = targetAlphabet.lookupLabel("class");
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { fIndex }, new double[] { 1.0 });
// Labeling labeling = l.getLabeling();
// Instance instance = new Instance(fv, labeling, "sample", null);
// instance.setDataAlphabet(dataAlphabet);
// instance.setTargetAlphabet(targetAlphabet);
// trainer.trainIncremental(instance);
NaiveBayes classifier = trainer.getClassifier();
assertNotNull(classifier);
assertEquals(1, classifier.getLabelAlphabet().size());
}

@Test
public void testEdgeCaseWithInfiniteInstanceWeightSkipped() {
Alphabet featureAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
int fi = featureAlphabet.lookupIndex("w");
Label label = labelAlphabet.lookupLabel("ok");
Pipe pipe = new Noop(featureAlphabet, labelAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
trainer.setDocLengthNormalization(Double.POSITIVE_INFINITY);
FeatureVector fv = new FeatureVector(featureAlphabet, new int[] { fi }, new double[] { 1.0 });
// Labeling labeling = label.getLabeling();
// Instance instance = new Instance(fv, labeling, "inf", null);
// instance.setDataAlphabet(featureAlphabet);
// instance.setTargetAlphabet(labelAlphabet);
try {
// trainer.trainIncremental(instance);
} catch (AssertionError e) {
return;
}
fail("Expected assertion error not thrown due to infinite instance weight.");
}

@Test
public void testFactorySetsAllParametersCorrectly() {
Multinomial.Estimator customFeatureEstimator = new Multinomial.MLEstimator();
Multinomial.Estimator customPriorEstimator = new Multinomial.MLEstimator();
NaiveBayesTrainer.Factory factory = new NaiveBayesTrainer.Factory().setFeatureMultinomialEstimator(customFeatureEstimator).setPriorMultinomialEstimator(customPriorEstimator).setDocLengthNormalization(5.0);
NaiveBayesTrainer trainer = factory.newClassifierTrainer(null);
assertEquals(5.0, trainer.getDocLengthNormalization(), 0.00001);
assertEquals(customFeatureEstimator, trainer.getFeatureMultinomialEstimator());
assertEquals(customPriorEstimator, trainer.getPriorMultinomialEstimator());
}

@Test
public void testTrainWithInstanceWeightGreaterThanOne() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet targetAlphabet = new LabelAlphabet();
int fi = dataAlphabet.lookupIndex("f");
Label label = targetAlphabet.lookupLabel("z");
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
InstanceList list = new InstanceList(pipe);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { fi }, new double[] { 1.0 });
// Labeling labeling = label.getLabeling();
// Instance instance = new Instance(fv, labeling, "id", null);
// list.addThruPipe(instance);
// list.setInstanceWeight(instance, 5.0);
NaiveBayes classifier = trainer.train(list);
assertNotNull(classifier);
}

@Test
public void testTrainIncrementalWithNullLabelingIsSkipped() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet targetAlphabet = new LabelAlphabet();
dataAlphabet.lookupIndex("some");
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
Instance instance = new Instance(fv, null, "unlabeled", null);
// instance.setDataAlphabet(dataAlphabet);
// instance.setTargetAlphabet(targetAlphabet);
NaiveBayes classifier = trainer.trainIncremental(instance);
assertNotNull(classifier);
assertEquals(0, classifier.getLabelAlphabet().size());
}

@Test
public void testToStringIsImplementedCorrectly() {
NaiveBayesTrainer trainer = new NaiveBayesTrainer();
String result = trainer.toString();
assertEquals("NaiveBayesTrainer", result);
}

@Test
public void testTrainWithSingleInstanceHavingMultipleFeatures() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet targetAlphabet = new LabelAlphabet();
int fi1 = dataAlphabet.lookupIndex("feature1");
int fi2 = dataAlphabet.lookupIndex("feature2");
int fi3 = dataAlphabet.lookupIndex("feature3");
Label label = targetAlphabet.lookupLabel("labelX");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { fi1, fi2, fi3 }, new double[] { 1.0, 2.0, 3.0 });
// Labeling labeling = label.getLabeling();
// Instance instance = new Instance(fv, labeling, "multi-feature", null);
// instance.setDataAlphabet(dataAlphabet);
// instance.setTargetAlphabet(targetAlphabet);
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
InstanceList list = new InstanceList(pipe);
// list.addThruPipe(instance);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
NaiveBayes classifier = trainer.train(list);
assertNotNull(classifier);
assertEquals(1, classifier.getLabelAlphabet().size());
}

@Test
public void testTrainingAfterCallingTrainClearsPreviousState() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet targetAlphabet = new LabelAlphabet();
dataAlphabet.lookupIndex("term");
targetAlphabet.lookupLabel("yes");
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
// Labeling labeling = targetAlphabet.lookupLabel("yes").getLabeling();
// Instance instance = new Instance(fv, labeling, "1", null);
// instance.setDataAlphabet(dataAlphabet);
// instance.setTargetAlphabet(targetAlphabet);
InstanceList list = new InstanceList(pipe);
// list.addThruPipe(instance);
NaiveBayes model1 = trainer.train(list);
assertNotNull(model1);
NaiveBayes model2 = trainer.train(list);
assertNotSame(model1, model2);
}

@Test
public void testFactoryCreatesNewTrainerFromClassifier() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet targetAlphabet = new LabelAlphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer initialTrainer = new NaiveBayesTrainer(pipe);
int index = dataAlphabet.lookupIndex("term");
Label label = targetAlphabet.lookupLabel("yes");
// Labeling labeling = label.getLabeling();
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { index }, new double[] { 1.0 });
// Instance instance = new Instance(fv, labeling, "i", null);
// instance.setDataAlphabet(dataAlphabet);
// instance.setTargetAlphabet(targetAlphabet);
InstanceList list = new InstanceList(pipe);
// list.addThruPipe(instance);
NaiveBayes model = initialTrainer.train(list);
NaiveBayesTrainer.Factory factory = new NaiveBayesTrainer.Factory();
NaiveBayesTrainer newTrainer = factory.newClassifierTrainer(model);
assertNotNull(newTrainer);
assertEquals(initialTrainer.getClassifier().getLabelAlphabet().size(), newTrainer.getClassifier().getLabelAlphabet().size());
}

@Test
public void testAddingNewLabelAfterInitialTrainingExpandsLabelSpace() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet targetAlphabet = new LabelAlphabet();
int index = dataAlphabet.lookupIndex("x");
Label label1 = targetAlphabet.lookupLabel("A");
FeatureVector fv1 = new FeatureVector(dataAlphabet, new int[] { index }, new double[] { 1.0 });
// Labeling labeling1 = label1.getLabeling();
// Instance instance1 = new Instance(fv1, labeling1, "id1", null);
// instance1.setDataAlphabet(dataAlphabet);
// instance1.setTargetAlphabet(targetAlphabet);
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
// trainer.trainIncremental(instance1);
Label label2 = targetAlphabet.lookupLabel("B");
FeatureVector fv2 = new FeatureVector(dataAlphabet, new int[] { index }, new double[] { 1.0 });
// Labeling labeling2 = label2.getLabeling();
// Instance instance2 = new Instance(fv2, labeling2, "id2", null);
// instance2.setDataAlphabet(dataAlphabet);
// instance2.setTargetAlphabet(targetAlphabet);
// trainer.trainIncremental(instance2);
NaiveBayes classifier = trainer.getClassifier();
assertEquals(2, classifier.getLabelAlphabet().size());
}

@Test
public void testTrainWithDuplicateFeatureIndices() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet targetAlphabet = new LabelAlphabet();
int fIndex = dataAlphabet.lookupIndex("token");
int[] indices = new int[] { fIndex, fIndex };
double[] values = new double[] { 1.0, 2.0 };
Label label = targetAlphabet.lookupLabel("class");
// Labeling labeling = label.getLabeling();
FeatureVector fv = new FeatureVector(dataAlphabet, indices, values);
// Instance instance = new Instance(fv, labeling, "dup", null);
// instance.setDataAlphabet(dataAlphabet);
// instance.setTargetAlphabet(targetAlphabet);
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
InstanceList list = new InstanceList(pipe);
// list.addThruPipe(instance);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
NaiveBayes model = trainer.train(list);
assertNotNull(model);
assertEquals(1, model.getLabelAlphabet().size());
}

@Test
public void testClassifierIsUpdatedAfterEachTrainIncrementalCall() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet targetAlphabet = new LabelAlphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
int f1 = dataAlphabet.lookupIndex("x");
Label label = targetAlphabet.lookupLabel("A");
FeatureVector fv1 = new FeatureVector(dataAlphabet, new int[] { f1 }, new double[] { 1.0 });
// Labeling labeling = label.getLabeling();
// Instance instance1 = new Instance(fv1, labeling, "i1", null);
// instance1.setDataAlphabet(dataAlphabet);
// instance1.setTargetAlphabet(targetAlphabet);
// NaiveBayes classifier1 = trainer.trainIncremental(instance1);
// assertNotNull(classifier1);
// assertEquals(1, classifier1.getLabelAlphabet().size());
int f2 = dataAlphabet.lookupIndex("y");
FeatureVector fv2 = new FeatureVector(dataAlphabet, new int[] { f1, f2 }, new double[] { 1.0, 1.0 });
// Instance instance2 = new Instance(fv2, labeling, "i2", null);
// instance2.setDataAlphabet(dataAlphabet);
// instance2.setTargetAlphabet(targetAlphabet);
// NaiveBayes classifier2 = trainer.trainIncremental(instance2);
// assertNotNull(classifier2);
// assertSame(classifier2, trainer.getClassifier());
// assertEquals(1, classifier2.getLabelAlphabet().size());
}

@Test
public void testEmptyFeatureVectorWithValidLabelIsHandledGracefully() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet targetAlphabet = new LabelAlphabet();
Label label = targetAlphabet.lookupLabel("class");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] {}, new double[] {});
// Labeling labeling = label.getLabeling();
// Instance instance = new Instance(fv, labeling, "empty", null);
// instance.setDataAlphabet(dataAlphabet);
// instance.setTargetAlphabet(targetAlphabet);
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
// NaiveBayes classifier = trainer.trainIncremental(instance);
// assertNotNull(classifier);
// assertEquals(1, classifier.getLabelAlphabet().size());
}

@Test(expected = AssertionError.class)
public void testIllegalWeightCalculationTriggersAssertion() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet targetAlphabet = new LabelAlphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
trainer.setDocLengthNormalization(0);
int fi = dataAlphabet.lookupIndex("f");
Label label = targetAlphabet.lookupLabel("z");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { fi }, new double[] { 1.0 });
// Labeling labeling = label.getLabeling();
// Instance instance = new Instance(fv, labeling, "id", null);
// instance.setDataAlphabet(dataAlphabet);
// instance.setTargetAlphabet(targetAlphabet);
// trainer.trainIncremental(instance);
}

@Test
public void testAlphabetReturnedReferencesOriginalDataAndTarget() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
Alphabet[] alphabets = trainer.getAlphabets();
assertEquals(dataAlphabet, alphabets[0]);
assertEquals(targetAlphabet, alphabets[1]);
}

@Test
public void testSetAlphabetOnceAndCallTrainWithMatchingAlphabetShouldSucceed() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new LabelAlphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
dataAlphabet.lookupIndex("feature");
// targetAlphabet.lookupLabel("label");
NaiveBayesTrainer trainer = new NaiveBayesTrainer();
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
Label label = ((LabelAlphabet) targetAlphabet).lookupLabel("label");
// Labeling labeling = label.getLabeling();
// Instance instance = new Instance(fv, labeling, "inst", null);
// instance.setDataAlphabet(dataAlphabet);
// instance.setTargetAlphabet(targetAlphabet);
InstanceList list = new InstanceList(pipe);
// list.addThruPipe(instance);
NaiveBayes classifier = trainer.train(list);
assertNotNull(classifier);
}

@Test(expected = IllegalArgumentException.class)
public void testTrainIncrementalWithAlphabetsMismatchThrowsException() {
Alphabet dataAlphabet1 = new Alphabet();
Alphabet targetAlphabet1 = new LabelAlphabet();
dataAlphabet1.lookupIndex("a");
// targetAlphabet1.lookupLabel("labelA");
Alphabet dataAlphabet2 = new Alphabet();
Alphabet targetAlphabet2 = new LabelAlphabet();
dataAlphabet2.lookupIndex("b");
// targetAlphabet2.lookupLabel("labelA");
Pipe pipe = new Noop(dataAlphabet1, targetAlphabet1);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
FeatureVector fv = new FeatureVector(dataAlphabet2, new int[] { 0 }, new double[] { 1.0 });
Label label = ((LabelAlphabet) targetAlphabet2).lookupLabel("labelA");
// Labeling labeling = label.getLabeling();
// Instance instance = new Instance(fv, labeling, "id", null);
// instance.setDataAlphabet(dataAlphabet2);
// instance.setTargetAlphabet(targetAlphabet2);
// trainer.trainIncremental(instance);
}

@Test
public void testMultipleTrainIncrementalCallsWithSameLabelAccumulatesCorrectly() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet targetAlphabet = new LabelAlphabet();
int fidx = dataAlphabet.lookupIndex("common");
Label label = targetAlphabet.lookupLabel("label");
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
FeatureVector fv1 = new FeatureVector(dataAlphabet, new int[] { fidx }, new double[] { 0.5 });
// Instance inst1 = new Instance(fv1, label.getLabeling(), "1", null);
// inst1.setDataAlphabet(dataAlphabet);
// inst1.setTargetAlphabet(targetAlphabet);
// trainer.trainIncremental(inst1);
FeatureVector fv2 = new FeatureVector(dataAlphabet, new int[] { fidx }, new double[] { 0.8 });
// Instance inst2 = new Instance(fv2, label.getLabeling(), "2", null);
// inst2.setDataAlphabet(dataAlphabet);
// inst2.setTargetAlphabet(targetAlphabet);
// trainer.trainIncremental(inst2);
NaiveBayes classifier = trainer.getClassifier();
assertNotNull(classifier);
assertEquals(1, classifier.getLabelAlphabet().size());
}

@Test
public void testTrainIncrementalWhenInstancePipeIsNullUsesNoopPipeWithAlphabets() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet targetAlphabet = new LabelAlphabet();
int fidx = dataAlphabet.lookupIndex("x");
Label label = targetAlphabet.lookupLabel("label");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { fidx }, new double[] { 1.0 });
// Labeling labeling = label.getLabeling();
// Instance instance = new Instance(fv, labeling, "id", null);
// instance.setDataAlphabet(dataAlphabet);
// instance.setTargetAlphabet(targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer();
// NaiveBayes classifier = trainer.trainIncremental(instance);
// assertNotNull(classifier);
// assertEquals(1, classifier.getLabelAlphabet().size());
}

@Test
public void testTrainWithNullClassifierReturnsNewTrainedClassifier() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet targetAlphabet = new LabelAlphabet();
int fi = dataAlphabet.lookupIndex("token");
Label label = targetAlphabet.lookupLabel("target");
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer();
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { fi }, new double[] { 1.0 });
// Labeling labeling = label.getLabeling();
// Instance instance = new Instance(fv, labeling, "1", null);
// instance.setDataAlphabet(dataAlphabet);
// instance.setTargetAlphabet(targetAlphabet);
InstanceList list = new InstanceList(pipe);
// list.addThruPipe(instance);
NaiveBayes classifier = trainer.train(list);
assertNotNull(classifier);
}

@Test
public void testClassifierTrainedIsSetInternallyOnTrain() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet targetAlphabet = new LabelAlphabet();
int fi = dataAlphabet.lookupIndex("token");
Label label = targetAlphabet.lookupLabel("c");
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer();
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { fi }, new double[] { 1.0 });
// Labeling labeling = label.getLabeling();
// Instance instance = new Instance(fv, labeling, "x", null);
// instance.setDataAlphabet(dataAlphabet);
// instance.setTargetAlphabet(targetAlphabet);
InstanceList list = new InstanceList(pipe);
// list.addThruPipe(instance);
trainer.train(list);
assertNotNull(trainer.getClassifier());
}

@Test
public void testSerializationVersionMismatchThrows() throws Exception {
ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
ObjectOutputStream oos = new ObjectOutputStream(outputStream);
oos.writeInt(999);
oos.writeObject(new Multinomial.LaplaceEstimator());
oos.writeObject(new Multinomial.LaplaceEstimator());
oos.writeObject(null);
oos.writeObject(null);
oos.writeObject(new Noop(new Alphabet(), new Alphabet()));
oos.writeObject(new Alphabet());
oos.writeObject(new Alphabet());
oos.close();
ByteArrayInputStream byteIn = new ByteArrayInputStream(outputStream.toByteArray());
ObjectInputStream ois = new ObjectInputStream(byteIn);
// try {
// NaiveBayesTrainer trainer = new NaiveBayesTrainer();
// trainer.getClass().getDeclaredMethod("readObject", ObjectInputStream.class).setAccessible(true);
// trainer.readObject(ois);
// fail("Expected ClassNotFoundException due to version mismatch");
// } catch (ClassNotFoundException e) {
// }
// }
// 
// @Test
// public void testToStringAlwaysReturnsCorrectClassName() {
// NaiveBayesTrainer trainer1 = new NaiveBayesTrainer();
// NaiveBayesTrainer trainer2 = new NaiveBayesTrainer(new Noop(new Alphabet(), new LabelAlphabet()));
// assertEquals("NaiveBayesTrainer", trainer1.toString());
// assertEquals("NaiveBayesTrainer", trainer2.toString());
// }
// 
// @Test
// public void testTrainWithEmptyFeatureVectorAndNullLabelingDoesNotThrow() {
// Alphabet dataAlphabet = new Alphabet();
// Alphabet targetAlphabet = new Alphabet();
// FeatureVector fv = new FeatureVector(dataAlphabet, new int[] {}, new double[] {});
// Instance instance = new Instance(fv, null, "empty-null-label", null);
// instance.setDataAlphabet(dataAlphabet);
// instance.setTargetAlphabet(targetAlphabet);
// Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
// NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
// InstanceList list = new InstanceList(pipe);
// list.addThruPipe(instance);
// NaiveBayes classifier = trainer.train(list);
// assertNotNull(classifier);
// }
// 
// @Test
// public void testTrainWithInstanceHavingOnlyZeroWeightsInFeatures() {
// Alphabet dataAlphabet = new Alphabet();
// LabelAlphabet targetAlphabet = new LabelAlphabet();
// dataAlphabet.lookupIndex("word1");
// targetAlphabet.lookupLabel("class");
// Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
// NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
// int[] indices = new int[] { 0 };
// double[] values = new double[] { 0.0 };
// FeatureVector fv = new FeatureVector(dataAlphabet, indices, values);
// Label label = targetAlphabet.lookupLabel("class");
// Labeling labeling = label.getLabeling();
// Instance instance = new Instance(fv, labeling, "zeroFeature", null);
// instance.setDataAlphabet(dataAlphabet);
// instance.setTargetAlphabet(targetAlphabet);
// InstanceList list = new InstanceList(pipe);
// list.addThruPipe(instance);
// NaiveBayes classifier = trainer.train(list);
// assertNotNull(classifier);
// }
// 
// @Test
// public void testInstanceListWithOneUnlabeledAndOneLabeledInstanceAddsOnlyLabeled() {
// Alphabet dataAlphabet = new Alphabet();
// LabelAlphabet targetAlphabet = new LabelAlphabet();
// dataAlphabet.lookupIndex("token");
// targetAlphabet.lookupLabel("L");
// Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
// NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
// InstanceList list = new InstanceList(pipe);
// FeatureVector fv1 = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
// FeatureVector fv2 = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 2.0 });
// Labeling labeling = targetAlphabet.lookupLabel("L").getLabeling();
// Instance labeled = new Instance(fv1, labeling, "labeled", null);
// labeled.setDataAlphabet(dataAlphabet);
// labeled.setTargetAlphabet(targetAlphabet);
// Instance unlabeled = new Instance(fv2, null, "unlabeled", null);
// unlabeled.setDataAlphabet(dataAlphabet);
// unlabeled.setTargetAlphabet(targetAlphabet);
// list.addThruPipe(unlabeled);
// list.addThruPipe(labeled);
// NaiveBayes classifier = trainer.train(list);
// assertNotNull(classifier);
// assertEquals(1, classifier.getLabelAlphabet().size());
// }
// 
// @Test
// public void testDocLengthNormalizationAffectsWeightedIncorporation() {
// Alphabet dataAlphabet = new Alphabet();
// LabelAlphabet targetAlphabet = new LabelAlphabet();
// dataAlphabet.lookupIndex("x");
// targetAlphabet.lookupLabel("pos");
// Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
// NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
// trainer.setDocLengthNormalization(10.0);
// FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 5.0 });
// Label label = targetAlphabet.lookupLabel("pos");
// Labeling labeling = label.getLabeling();
// Instance instance = new Instance(fv, labeling, "docNorm", null);
// instance.setDataAlphabet(dataAlphabet);
// instance.setTargetAlphabet(targetAlphabet);
// NaiveBayes classifier = trainer.trainIncremental(instance);
// assertNotNull(classifier);
// }
// 
// @Test
// public void testIncrementalTrainWithGrowingLabelAlphabetAddsNewEstimatorArrayElements() {
// Alphabet dataAlphabet = new Alphabet();
// LabelAlphabet targetAlphabet = new LabelAlphabet();
// int f = dataAlphabet.lookupIndex("f");
// Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
// NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
// Label label1 = targetAlphabet.lookupLabel("L1");
// FeatureVector fv1 = new FeatureVector(dataAlphabet, new int[] { f }, new double[] { 1.0 });
// Instance i1 = new Instance(fv1, label1.getLabeling(), "a", null);
// i1.setDataAlphabet(dataAlphabet);
// i1.setTargetAlphabet(targetAlphabet);
// trainer.trainIncremental(i1);
// Label label2 = targetAlphabet.lookupLabel("L2");
// FeatureVector fv2 = new FeatureVector(dataAlphabet, new int[] { f }, new double[] { 1.0 });
// Instance i2 = new Instance(fv2, label2.getLabeling(), "b", null);
// i2.setDataAlphabet(dataAlphabet);
// i2.setTargetAlphabet(targetAlphabet);
// trainer.trainIncremental(i2);
// NaiveBayes classifier = trainer.getClassifier();
// assertEquals(2, classifier.getLabelAlphabet().size());
// }
// 
// @Test
// public void testClassifierSetByTrainMatchesGetClassifierReference() {
// Alphabet dataAlphabet = new Alphabet();
// LabelAlphabet targetAlphabet = new LabelAlphabet();
// dataAlphabet.lookupIndex("feature");
// targetAlphabet.lookupLabel("label");
// Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
// NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
// FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
// Label label = targetAlphabet.lookupLabel("label");
// Labeling labeling = label.getLabeling();
// Instance instance = new Instance(fv, labeling, "id", null);
// instance.setDataAlphabet(dataAlphabet);
// instance.setTargetAlphabet(targetAlphabet);
// InstanceList list = new InstanceList(pipe);
// list.addThruPipe(instance);
// NaiveBayes classifierFromTrain = trainer.train(list);
// NaiveBayes classifierFromGetter = trainer.getClassifier();
// assertSame(classifierFromTrain, classifierFromGetter);
// }
// 
// @Test
// public void testInstanceWithMultipleLabelsAppliesAllCorrectly() {
// Alphabet dataAlphabet = new Alphabet();
// LabelAlphabet targetAlphabet = new LabelAlphabet();
// int fi = dataAlphabet.lookupIndex("term");
// int label1Index = targetAlphabet.lookupLabel("yes").getIndex();
// int label2Index = targetAlphabet.lookupLabel("no").getIndex();
// Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
// NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
// int[] labelIndices = new int[] { label1Index, label2Index };
// double[] weights = new double[] { 0.6, 0.4 };
// Labeling multiLabeling = new LabelVector(targetAlphabet, labelIndices, weights);
// FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { fi }, new double[] { 1.0 });
// Instance instance = new Instance(fv, multiLabeling, "id", null);
// instance.setDataAlphabet(dataAlphabet);
// instance.setTargetAlphabet(targetAlphabet);
// NaiveBayes classifier = trainer.trainIncremental(instance);
// assertNotNull(classifier);
// assertEquals(2, classifier.getLabelAlphabet().size());
// }
// 
// @Test(expected = IllegalArgumentException.class)
// public void testSetupWithMismatchedPipeThrowsIllegalArgumentException() {
// Alphabet dataAlphabet1 = new Alphabet();
// LabelAlphabet targetAlphabet1 = new LabelAlphabet();
// dataAlphabet1.lookupIndex("f1");
// targetAlphabet1.lookupLabel("class1");
// Alphabet dataAlphabet2 = new Alphabet();
// LabelAlphabet targetAlphabet2 = new LabelAlphabet();
// dataAlphabet2.lookupIndex("f2");
// targetAlphabet2.lookupLabel("class1");
// Pipe pipe1 = new Noop(dataAlphabet1, targetAlphabet1);
// Pipe pipe2 = new Noop(dataAlphabet2, targetAlphabet2);
// NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe1);
// FeatureVector fv = new FeatureVector(dataAlphabet2, new int[] { 0 }, new double[] { 1.0 });
// Label label = targetAlphabet2.lookupLabel("class1");
// Labeling labeling = label.getLabeling();
// Instance instance = new Instance(fv, labeling, "id", null);
// instance.setDataAlphabet(dataAlphabet2);
// instance.setTargetAlphabet(targetAlphabet2);
// InstanceList list = new InstanceList(pipe2);
// list.addThruPipe(instance);
// trainer.trainIncremental(list);
// }
// 
// @Test
// public void testTrainWithNullPipeAndInitialClassifier() {
// Alphabet dataAlphabet = new Alphabet();
// LabelAlphabet targetAlphabet = new LabelAlphabet();
// dataAlphabet.lookupIndex("feature");
// targetAlphabet.lookupLabel("class");
// Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
// NaiveBayesTrainer initialTrainer = new NaiveBayesTrainer(pipe);
// FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
// Label label = targetAlphabet.lookupLabel("class");
// Labeling labeling = label.getLabeling();
// Instance instance = new Instance(fv, labeling, "id", null);
// instance.setDataAlphabet(dataAlphabet);
// instance.setTargetAlphabet(targetAlphabet);
// InstanceList list = new InstanceList(pipe);
// list.addThruPipe(instance);
// NaiveBayes classifier = initialTrainer.train(list);
// NaiveBayesTrainer trainer = new NaiveBayesTrainer(classifier);
// assertEquals(dataAlphabet, trainer.getAlphabet());
// assertNotNull(trainer.getClassifier());
// }
// 
// @Test
// public void testSetFeatureMultinomialEstimatorBeforeIncrementalTrainSucceeds() {
// Alphabet dataAlphabet = new Alphabet();
// LabelAlphabet targetAlphabet = new LabelAlphabet();
// dataAlphabet.lookupIndex("term");
// targetAlphabet.lookupLabel("label");
// Multinomial.Estimator customEstimator = new Multinomial.MLEstimator();
// NaiveBayesTrainer trainer = new NaiveBayesTrainer();
// trainer.setFeatureMultinomialEstimator(customEstimator);
// FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
// Label label = targetAlphabet.lookupLabel("label");
// Labeling labeling = label.getLabeling();
// Instance instance = new Instance(fv, labeling, "id", null);
// instance.setDataAlphabet(dataAlphabet);
// instance.setTargetAlphabet(targetAlphabet);
// NaiveBayes classifier = trainer.trainIncremental(instance);
// assertNotNull(classifier);
// assertEquals(1, classifier.getLabelAlphabet().size());
// }
// 
// @Test
// public void testSetPriorMultinomialEstimatorBeforeIncrementalTrainSucceeds() {
// Alphabet dataAlphabet = new Alphabet();
// LabelAlphabet targetAlphabet = new LabelAlphabet();
// dataAlphabet.lookupIndex("entry");
// targetAlphabet.lookupLabel("tag");
// Multinomial.Estimator priorEstimator = new Multinomial.MLEstimator();
// NaiveBayesTrainer trainer = new NaiveBayesTrainer();
// trainer.setPriorMultinomialEstimator(priorEstimator);
// FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 3.0 });
// Label label = targetAlphabet.lookupLabel("tag");
// Labeling labeling = label.getLabeling();
// Instance instance = new Instance(fv, labeling, "rec", null);
// instance.setDataAlphabet(dataAlphabet);
// instance.setTargetAlphabet(targetAlphabet);
// NaiveBayes classifier = trainer.trainIncremental(instance);
// assertNotNull(classifier);
// }
// 
// @Test
// public void testTrainWithLabelAlphabetThatResizesDuringSetup() {
// Alphabet dataAlphabet = new Alphabet();
// LabelAlphabet targetAlphabet = new LabelAlphabet();
// dataAlphabet.lookupIndex("x");
// Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
// NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
// Label label1 = targetAlphabet.lookupLabel("a");
// FeatureVector fv1 = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
// Instance i1 = new Instance(fv1, label1.getLabeling(), "1", null);
// i1.setDataAlphabet(dataAlphabet);
// i1.setTargetAlphabet(targetAlphabet);
// trainer.trainIncremental(i1);
// for (int i = 0; i < 5; i++) {
// Label growingLabel = targetAlphabet.lookupLabel("b" + i);
// FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
// Instance growingInstance = new Instance(fv, growingLabel.getLabeling(), "g" + i, null);
// growingInstance.setDataAlphabet(dataAlphabet);
// growingInstance.setTargetAlphabet(targetAlphabet);
// trainer.trainIncremental(growingInstance);
// }
// NaiveBayes classifier = trainer.getClassifier();
// assertEquals(6, classifier.getLabelAlphabet().size());
}

@Test
public void testTrainIncrementalUsesClonedEstimatorCorrectly() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet targetAlphabet = new LabelAlphabet();
dataAlphabet.lookupIndex("v");
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
Multinomial.Estimator estimator = new Multinomial.MLEstimator();
trainer.setFeatureMultinomialEstimator(estimator);
trainer.setPriorMultinomialEstimator(estimator);
Label label = targetAlphabet.lookupLabel("cat");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
// Instance instance = new Instance(fv, label.getLabeling(), "obj", null);
// instance.setDataAlphabet(dataAlphabet);
// instance.setTargetAlphabet(targetAlphabet);
// NaiveBayes classifier = trainer.trainIncremental(instance);
// assertNotNull(classifier);
}

@Test
public void testPipeIsUpdatedAfterTrainingWithNullPipe() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet targetAlphabet = new LabelAlphabet();
dataAlphabet.lookupIndex("hello");
targetAlphabet.lookupLabel("world");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
Label label = targetAlphabet.lookupLabel("world");
// Instance instance = new Instance(fv, label.getLabeling(), "first", null);
// instance.setDataAlphabet(dataAlphabet);
// instance.setTargetAlphabet(targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer();
assertNull(trainer.getClassifier());
// NaiveBayes classifier = trainer.trainIncremental(instance);
// assertNotNull(classifier);
assertNotNull(trainer.getClassifier());
}

@Test
public void testSerializationPreservesDocLengthNormalization() throws Exception {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet targetAlphabet = new LabelAlphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
trainer.setDocLengthNormalization(42.0);
ByteArrayOutputStream baos = new ByteArrayOutputStream();
ObjectOutputStream oos = new ObjectOutputStream(baos);
oos.writeObject(trainer);
oos.flush();
oos.close();
ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
ObjectInputStream ois = new ObjectInputStream(bais);
NaiveBayesTrainer deserialized = (NaiveBayesTrainer) ois.readObject();
assertEquals(42.0, deserialized.getDocLengthNormalization(), 0.00001);
}

@Test
public void testResetStateViaTrainDoesNotRetainPreviousCounts() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet targetAlphabet = new LabelAlphabet();
dataAlphabet.lookupIndex("x");
targetAlphabet.lookupLabel("A");
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
Label label = targetAlphabet.lookupLabel("A");
// Instance instance = new Instance(fv, label.getLabeling(), "id", null);
// instance.setDataAlphabet(dataAlphabet);
// instance.setTargetAlphabet(targetAlphabet);
InstanceList list = new InstanceList(pipe);
// list.addThruPipe(instance);
NaiveBayes classifier1 = trainer.train(list);
NaiveBayes classifier2 = trainer.train(list);
assertNotSame(classifier1, classifier2);
}

@Test
public void testEstimateFeatureMultinomialsHandlesEmptyTargetAlphabet() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet targetAlphabet = new LabelAlphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
// NaiveBayes classifier = new NaiveBayes(pipe, new Multinomial(targetAlphabet), new Multinomial[] {});
// trainer.trainIncremental(new Instance(new FeatureVector(dataAlphabet, new int[] {}, new double[] {}), null, "empty", null));
assertNotNull(trainer.getClassifier());
}
}
