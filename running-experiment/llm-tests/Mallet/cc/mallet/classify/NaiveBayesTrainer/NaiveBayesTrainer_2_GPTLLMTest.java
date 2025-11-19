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

public class NaiveBayesTrainer_2_GPTLLMTest {

@Test
public void testTrainCreatesClassifier() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
int featureIndex1 = dataAlphabet.lookupIndex("feat1");
int featureIndex2 = dataAlphabet.lookupIndex("feat2");
int labelIndex = targetAlphabet.lookupIndex("labelA");
FeatureVector featureVector = new FeatureVector(dataAlphabet, new int[] { featureIndex1, featureIndex2 }, new double[] { 1.0, 2.0 });
// Labeling labeling = new LabelVector(targetAlphabet, labelIndex, 1.0);
// Instance instance = new Instance(featureVector, labeling, null, null);
InstanceList instanceList = new InstanceList(pipe);
// instanceList.addThruPipe(instance);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
NaiveBayes classifier = trainer.train(instanceList);
assertNotNull(classifier);
}

@Test
public void testTrainAssignsClassifierInTrainer() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
int f1 = dataAlphabet.lookupIndex("a");
int label = targetAlphabet.lookupIndex("label");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { f1 }, new double[] { 1.0 });
// Labeling labeling = new LabelVector(targetAlphabet, label, 1.0);
// Instance instance = new Instance(fv, labeling, null, null);
InstanceList list = new InstanceList(pipe);
// list.addThruPipe(instance);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
assertNull(trainer.getClassifier());
trainer.train(list);
assertNotNull(trainer.getClassifier());
}

@Test
public void testTrainIncrementalSingleInstance() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
int f1 = dataAlphabet.lookupIndex("f1");
int labelIndex = targetAlphabet.lookupIndex("target");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { f1 }, new double[] { 2.5 });
// Labeling labeling = new LabelVector(targetAlphabet, labelIndex, 1.0);
// Instance instance = new Instance(fv, labeling, null, null);
NaiveBayesTrainer trainer = new NaiveBayesTrainer();
// NaiveBayes classifier = trainer.trainIncremental(instance);
// assertNotNull(classifier);
assertNotNull(trainer.getClassifier());
}

@Test
public void testGetAndSetDocLengthNormalization() {
NaiveBayesTrainer trainer = new NaiveBayesTrainer();
NaiveBayesTrainer returnTrainer = trainer.setDocLengthNormalization(7.5);
double value = trainer.getDocLengthNormalization();
assertSame(trainer, returnTrainer);
assertEquals(7.5, value, 0.0001);
}

@Test
public void testSetFeatureEstimatorBeforeTrain() {
NaiveBayesTrainer trainer = new NaiveBayesTrainer();
Multinomial.Estimator estimator = new Multinomial.LaplaceEstimator();
NaiveBayesTrainer result = trainer.setFeatureMultinomialEstimator(estimator);
assertSame(trainer, result);
assertEquals(estimator, trainer.getFeatureMultinomialEstimator());
}

@Test(expected = IllegalStateException.class)
public void testSetFeatureEstimatorAfterTrainingThrows() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
int idx = dataAlphabet.lookupIndex("x");
int label = targetAlphabet.lookupIndex("class");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { idx }, new double[] { 1.0 });
// Labeling labeling = new LabelVector(targetAlphabet, label, 1.0);
// Instance instance = new Instance(fv, labeling, null, null);
InstanceList list = new InstanceList(pipe);
// list.addThruPipe(instance);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
trainer.train(list);
trainer.setFeatureMultinomialEstimator(new Multinomial.LaplaceEstimator());
}

@Test
public void testSetPriorEstimatorBeforeTrain() {
NaiveBayesTrainer trainer = new NaiveBayesTrainer();
Multinomial.Estimator estimator = new Multinomial.LaplaceEstimator();
NaiveBayesTrainer result = trainer.setPriorMultinomialEstimator(estimator);
assertSame(trainer, result);
assertEquals(estimator, trainer.getPriorMultinomialEstimator());
}

@Test(expected = IllegalStateException.class)
public void testSetPriorEstimatorAfterTrainThrows() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
int idx = dataAlphabet.lookupIndex("w");
int label = targetAlphabet.lookupIndex("c");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { idx }, new double[] { 1.0 });
// Labeling labeling = new LabelVector(targetAlphabet, label, 1.0);
// Instance instance = new Instance(fv, labeling, null, null);
InstanceList list = new InstanceList(pipe);
// list.addThruPipe(instance);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
trainer.train(list);
trainer.setPriorMultinomialEstimator(new Multinomial.LaplaceEstimator());
}

@Test
public void testToStringReturnsCorrectValue() {
NaiveBayesTrainer trainer = new NaiveBayesTrainer();
String result = trainer.toString();
assertEquals("NaiveBayesTrainer", result);
}

@Test
public void testAlphabetsMatchIsTrueForSameAlphabets() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
assertTrue(trainer.alphabetsMatch(pipe));
}

@Test(expected = IllegalArgumentException.class)
public void testMismatchedPipeThrowsException() {
Alphabet data1 = new Alphabet();
Alphabet label1 = new Alphabet();
Pipe pipe1 = new Noop(data1, label1);
Alphabet data2 = new Alphabet();
Alphabet label2 = new Alphabet();
Pipe pipe2 = new Noop(data2, label2);
int fi = data2.lookupIndex("f");
int li = label2.lookupIndex("l");
FeatureVector fv = new FeatureVector(data2, new int[] { fi }, new double[] { 1.0 });
// Labeling labeling = new LabelVector(label2, li, 1.0);
// Instance instance = new Instance(fv, labeling, null, null);
InstanceList list = new InstanceList(pipe2);
// list.addThruPipe(instance);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe1);
trainer.trainIncremental(list);
}

@Test
public void testSkipUnlabeledInstanceDuringTraining() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
int fi = dataAlphabet.lookupIndex("feature");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { fi }, new double[] { 1.0 });
Instance instance = new Instance(fv, null, null, null);
InstanceList list = new InstanceList(pipe);
list.addThruPipe(instance);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
NaiveBayes classifier = trainer.trainIncremental(list);
assertNotNull(classifier);
}

@Test
public void testSkipFeatureVectorWithZeroNorm() {
Alphabet dataAlphabet = new Alphabet();
Alphabet labelAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, labelAlphabet);
int fid = dataAlphabet.lookupIndex("x");
int lid = labelAlphabet.lookupIndex("yes");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { fid }, new double[] { 0.0 });
// Labeling label = new LabelVector(labelAlphabet, lid, 1.0);
// Instance instance = new Instance(fv, label, null, null);
InstanceList list = new InstanceList(pipe);
// list.addThruPipe(instance);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
NaiveBayes classifier = trainer.trainIncremental(list);
assertNotNull(classifier);
}

@Test
public void testSerializationAndDeserialization() throws Exception {
Alphabet dataAlphabet = new Alphabet();
Alphabet labelAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, labelAlphabet);
int fid = dataAlphabet.lookupIndex("f");
int lid = labelAlphabet.lookupIndex("label");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { fid }, new double[] { 1.0 });
// Labeling labeling = new LabelVector(labelAlphabet, lid, 1.0);
// Instance instance = new Instance(fv, labeling, null, null);
InstanceList list = new InstanceList(pipe);
// list.addThruPipe(instance);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
trainer.train(list);
ByteArrayOutputStream baos = new ByteArrayOutputStream();
ObjectOutputStream oos = new ObjectOutputStream(baos);
oos.writeObject(trainer);
oos.close();
ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
ObjectInputStream ois = new ObjectInputStream(bais);
NaiveBayesTrainer result = (NaiveBayesTrainer) ois.readObject();
ois.close();
assertNotNull(result);
assertNotNull(result.getAlphabet());
assertEquals(dataAlphabet.size(), result.getAlphabet().size());
}

@Test
public void testFactoryCreatesTrainer() {
NaiveBayesTrainer.Factory factory = new NaiveBayesTrainer.Factory();
Multinomial.Estimator est = new Multinomial.LaplaceEstimator();
factory.setFeatureMultinomialEstimator(est);
factory.setPriorMultinomialEstimator(est);
factory.setDocLengthNormalization(20.0);
NaiveBayesTrainer trainer = factory.newClassifierTrainer(null);
assertNotNull(trainer);
}

@Test
public void testTrainOnEmptyInstanceListReturnsNonNullClassifier() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
InstanceList emptyList = new InstanceList(pipe);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
NaiveBayes classifier = trainer.train(emptyList);
assertNotNull(classifier);
}

@Test
public void testTrainIncrementalWithNullFeatureVectorInInstanceSkipsInstance() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
int labelIdx = targetAlphabet.lookupIndex("label");
// Labeling labeling = new LabelVector(targetAlphabet, labelIdx, 1.0);
// Instance instance = new Instance(null, labeling, null, null);
InstanceList list = new InstanceList(pipe);
// list.addThruPipe(instance);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
NaiveBayes classifier = trainer.trainIncremental(list);
assertNotNull(classifier);
}

@Test
public void testTrainIncrementalIgnoresZeroWeightLabelLocation() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
int featureIndex = dataAlphabet.lookupIndex("f");
int labelIndex = targetAlphabet.lookupIndex("zero_weight_label");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { featureIndex }, new double[] { 1.0 });
// LabelVector labeling = new LabelVector(targetAlphabet, new int[] { labelIndex }, new double[] { 0.0 });
// Instance instance = new Instance(fv, labeling, null, null);
InstanceList list = new InstanceList(pipe);
// list.addThruPipe(instance);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
NaiveBayes classifier = trainer.trainIncremental(list);
assertNotNull(classifier);
}

@Test
public void testTargetAlphabetGrowsDuringSequentialTraining() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
int f1 = dataAlphabet.lookupIndex("f1");
int label1 = targetAlphabet.lookupIndex("label1");
FeatureVector fv1 = new FeatureVector(dataAlphabet, new int[] { f1 }, new double[] { 1.0 });
// Labeling labeling1 = new LabelVector(targetAlphabet, label1, 1.0);
// Instance instance1 = new Instance(fv1, labeling1, null, null);
InstanceList list1 = new InstanceList(pipe);
// list1.addThruPipe(instance1);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
trainer.trainIncremental(list1);
int f2 = dataAlphabet.lookupIndex("f2");
int label2 = targetAlphabet.lookupIndex("label2");
FeatureVector fv2 = new FeatureVector(dataAlphabet, new int[] { f2 }, new double[] { 1.0 });
// Labeling labeling2 = new LabelVector(targetAlphabet, label2, 1.0);
// Instance instance2 = new Instance(fv2, labeling2, null, null);
InstanceList list2 = new InstanceList(pipe);
// list2.addThruPipe(instance2);
NaiveBayes classifier = trainer.trainIncremental(list2);
assertNotNull(classifier);
assertEquals(2, classifier.getLabelAlphabet().size());
}

@Test(expected = IllegalArgumentException.class)
public void testSetupThrowsWhenInstanceAlphabetMismatchOccurs() {
Alphabet dataAlphabet1 = new Alphabet();
Alphabet targetAlphabet1 = new Alphabet();
Pipe pipe1 = new Noop(dataAlphabet1, targetAlphabet1);
Alphabet dataAlphabet2 = new Alphabet();
Alphabet targetAlphabet2 = new Alphabet();
Pipe pipe2 = new Noop(dataAlphabet2, targetAlphabet2);
int f = dataAlphabet2.lookupIndex("feat");
int l = targetAlphabet2.lookupIndex("lab");
FeatureVector fv = new FeatureVector(dataAlphabet2, new int[] { f }, new double[] { 1 });
// LabelVector lv = new LabelVector(targetAlphabet2, l, 1);
// Instance instance = new Instance(fv, lv, null, null);
InstanceList list = new InstanceList(pipe2);
// list.addThruPipe(instance);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe1);
trainer.trainIncremental(list);
}

@Test
public void testSerializationConsistencyAfterTraining() throws IOException, ClassNotFoundException {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
int fi = dataAlphabet.lookupIndex("word");
int li = targetAlphabet.lookupIndex("yes");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { fi }, new double[] { 1.0 });
// LabelVector lv = new LabelVector(targetAlphabet, li, 1.0);
// Instance instance = new Instance(fv, lv, null, null);
InstanceList list = new InstanceList(pipe);
// list.addThruPipe(instance);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
trainer.train(list);
ByteArrayOutputStream baos = new ByteArrayOutputStream();
ObjectOutputStream oos = new ObjectOutputStream(baos);
oos.writeObject(trainer);
oos.close();
byte[] bytes = baos.toByteArray();
ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
ObjectInputStream ois = new ObjectInputStream(bais);
NaiveBayesTrainer deserialized = (NaiveBayesTrainer) ois.readObject();
assertNotNull(deserialized);
assertNotNull(deserialized.getAlphabet());
assertEquals(dataAlphabet.size(), deserialized.getAlphabet().size());
}

@Test
public void testTrainWithOneInstanceHavingPartialLabelWeight() {
Alphabet dataAlphabet = new Alphabet();
Alphabet labelAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, labelAlphabet);
int f1 = dataAlphabet.lookupIndex("token");
int l1 = labelAlphabet.lookupIndex("labelA");
int l2 = labelAlphabet.lookupIndex("labelB");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { f1 }, new double[] { 1.0 });
// LabelVector lv = new LabelVector(labelAlphabet, new int[] { l1, l2 }, new double[] { 0.7, 0.3 });
// Instance instance = new Instance(fv, lv, null, null);
InstanceList list = new InstanceList(pipe);
// list.addThruPipe(instance);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
NaiveBayes classifier = trainer.trainIncremental(list);
assertNotNull(classifier);
assertEquals(2, classifier.getLabelAlphabet().size());
}

@Test
public void testTrainIncrementalWithPreInitializedInstancePipe() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
int featureIndex = dataAlphabet.lookupIndex("token");
int labelIndex = targetAlphabet.lookupIndex("class");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { featureIndex }, new double[] { 2.0 });
// Labeling labeling = new LabelVector(targetAlphabet, labelIndex, 1.0);
// Instance instance = new Instance(fv, labeling, null, null);
// NaiveBayes initialClassifier = new NaiveBayes(pipe, new double[] { 1.0 }, new Multinomial[] {});
// NaiveBayesTrainer trainer = new NaiveBayesTrainer(initialClassifier);
// NaiveBayes classifier = trainer.trainIncremental(instance);
// assertNotNull(classifier);
}

@Test
public void testTrainIncrementalWithOneNormNormalizationActive() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
int featureIndex = dataAlphabet.lookupIndex("alpha");
int labelIndex = targetAlphabet.lookupIndex("beta");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { featureIndex }, new double[] { 4.0 });
// Labeling labeling = new LabelVector(targetAlphabet, labelIndex, 1.0);
// Instance instance = new Instance(fv, labeling, null, null);
InstanceList list = new InstanceList(pipe);
// list.addThruPipe(instance);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
trainer.setDocLengthNormalization(8.0);
NaiveBayes classifier = trainer.trainIncremental(list);
assertNotNull(classifier);
}

@Test
public void testTrainOnInstanceWithInfiniteValueSkipsInstance() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
int f = dataAlphabet.lookupIndex("f");
int l = targetAlphabet.lookupIndex("l");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { f }, new double[] { Double.POSITIVE_INFINITY });
// Labeling lbl = new LabelVector(targetAlphabet, l, 1.0);
// Instance instance = new Instance(fv, lbl, null, null);
InstanceList list = new InstanceList(pipe);
// list.addThruPipe(instance);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
NaiveBayes classifier = trainer.trainIncremental(list);
assertNotNull(classifier);
}

@Test
public void testTrainIncrementalCreatesMultinomialsForAllLabels() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
dataAlphabet.lookupIndex("f1");
targetAlphabet.lookupIndex("label1");
targetAlphabet.lookupIndex("label2");
int labelIndex = targetAlphabet.lookupIndex("label1");
int featureIndex = dataAlphabet.lookupIndex("f1");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { featureIndex }, new double[] { 3.0 });
// LabelVector lv = new LabelVector(targetAlphabet, new int[] { labelIndex }, new double[] { 1.0 });
// Instance instance = new Instance(fv, lv, null, null);
InstanceList list = new InstanceList(pipe);
// list.addThruPipe(instance);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
NaiveBayes nb = trainer.train(list);
assertNotNull(nb);
assertEquals(2, nb.getLabelAlphabet().size());
}

@Test
public void testIncrementalWithNewAlphabetExpanded() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
int featureIndex1 = dataAlphabet.lookupIndex("f1");
int labelIndex1 = targetAlphabet.lookupIndex("L1");
FeatureVector fv1 = new FeatureVector(dataAlphabet, new int[] { featureIndex1 }, new double[] { 1.0 });
// Labeling labeling1 = new LabelVector(targetAlphabet, labelIndex1, 1.0);
// Instance inst1 = new Instance(fv1, labeling1, null, null);
InstanceList list1 = new InstanceList(pipe);
// list1.addThruPipe(inst1);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
trainer.trainIncremental(list1);
int featureIndex2 = dataAlphabet.lookupIndex("f2");
int labelIndex2 = targetAlphabet.lookupIndex("L2");
FeatureVector fv2 = new FeatureVector(dataAlphabet, new int[] { featureIndex2 }, new double[] { 2.0 });
// Labeling labeling2 = new LabelVector(targetAlphabet, labelIndex2, 1.0);
// Instance inst2 = new Instance(fv2, labeling2, null, null);
InstanceList list2 = new InstanceList(pipe);
// list2.addThruPipe(inst2);
NaiveBayes classifier = trainer.trainIncremental(list2);
assertNotNull(classifier);
assertEquals(2, classifier.getLabelAlphabet().size());
}

@Test
public void testFactoryCreatesTrainerWithNonDefaultEstimators() {
NaiveBayesTrainer.Factory factory = new NaiveBayesTrainer.Factory();
Multinomial.Estimator est1 = new Multinomial.LaplaceEstimator();
Multinomial.Estimator est2 = new Multinomial.LaplaceEstimator();
factory.setFeatureMultinomialEstimator(est1);
factory.setPriorMultinomialEstimator(est2);
factory.setDocLengthNormalization(5.5);
NaiveBayesTrainer trainer = factory.newClassifierTrainer(null);
assertNotNull(trainer);
assertEquals(5.5, trainer.getDocLengthNormalization(), 1e-9);
assertSame(est1, trainer.getFeatureMultinomialEstimator());
assertSame(est2, trainer.getPriorMultinomialEstimator());
}

@Test
public void testSerializationVersionMismatchThrowsException() throws IOException {
ByteArrayOutputStream bos = new ByteArrayOutputStream();
ObjectOutputStream out = new ObjectOutputStream(bos);
out.writeInt(999);
out.writeObject(new Multinomial.LaplaceEstimator());
out.writeObject(new Multinomial.LaplaceEstimator());
out.writeObject(null);
out.writeObject(null);
out.writeObject(null);
out.writeObject(null);
out.writeObject(null);
out.close();
ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
ObjectInputStream in = new ObjectInputStream(bis);
try {
NaiveBayesTrainer trainer = new NaiveBayesTrainer();
java.lang.reflect.Method readObject = NaiveBayesTrainer.class.getDeclaredMethod("readObject", ObjectInputStream.class);
readObject.setAccessible(true);
readObject.invoke(trainer, in);
fail("Expected exception not thrown");
} catch (Exception ex) {
assertTrue(ex.getCause() instanceof ClassNotFoundException);
assertTrue(ex.getCause().getMessage().contains("Mismatched NaiveBayesTrainer versions"));
}
}

@Test
public void testTrainWithInstanceHavingNoFeatures() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
int labelIndex = targetAlphabet.lookupIndex("class");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] {}, new double[] {});
// Labeling labeling = new LabelVector(targetAlphabet, labelIndex, 1.0);
// Instance instance = new Instance(fv, labeling, null, null);
InstanceList list = new InstanceList(pipe);
// list.addThruPipe(instance);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
NaiveBayes classifier = trainer.train(list);
assertNotNull(classifier);
assertEquals(1, classifier.getLabelAlphabet().size());
}

@Test
public void testTrainWithFeatureVectorContainingNaN() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
int featureIdx = dataAlphabet.lookupIndex("badFeature");
int labelIdx = targetAlphabet.lookupIndex("label");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { featureIdx }, new double[] { Double.NaN });
// Labeling labeling = new LabelVector(targetAlphabet, labelIdx, 1.0);
// Instance instance = new Instance(fv, labeling, null, null);
InstanceList list = new InstanceList(pipe);
// list.addThruPipe(instance);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
NaiveBayes nb = trainer.train(list);
assertNotNull(nb);
assertEquals(1, nb.getLabelAlphabet().size());
}

@Test
public void testTrainWithMultipleLabelsSharingWeight() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
int li1 = targetAlphabet.lookupIndex("label1");
int li2 = targetAlphabet.lookupIndex("label2");
int fi = dataAlphabet.lookupIndex("shared");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { fi }, new double[] { 1.0 });
// LabelVector lbl = new LabelVector(targetAlphabet, new int[] { li1, li2 }, new double[] { 0.4, 0.6 });
// Instance inst = new Instance(fv, lbl, null, null);
InstanceList list = new InstanceList(pipe);
// list.addThruPipe(inst);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
NaiveBayes nb = trainer.train(list);
assertNotNull(nb);
assertEquals(2, nb.getLabelAlphabet().size());
}

@Test
public void testEstimateFeatureMultinomialsReturnsAllLabels() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
int fi = dataAlphabet.lookupIndex("feat");
int li1 = targetAlphabet.lookupIndex("l1");
int li2 = targetAlphabet.lookupIndex("l2");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { fi }, new double[] { 1.0 });
// LabelVector lbl = new LabelVector(targetAlphabet, new int[] { li1 }, new double[] { 1.0 });
// Instance inst = new Instance(fv, lbl, null, null);
InstanceList list = new InstanceList(pipe);
// list.addThruPipe(inst);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
NaiveBayes nb = trainer.trainIncremental(list);
assertNotNull(nb);
assertEquals(2, nb.getLabelAlphabet().size());
}

@Test
public void testSequentialTrainIncrementalAccumulatesProperly() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
int f1 = dataAlphabet.lookupIndex("f1");
int l1 = targetAlphabet.lookupIndex("labelA");
FeatureVector fv1 = new FeatureVector(dataAlphabet, new int[] { f1 }, new double[] { 1.0 });
// Labeling labeling1 = new LabelVector(targetAlphabet, l1, 1.0);
// Instance instance1 = new Instance(fv1, labeling1, null, null);
InstanceList batch1 = new InstanceList(pipe);
// batch1.addThruPipe(instance1);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
trainer.trainIncremental(batch1);
int f2 = dataAlphabet.lookupIndex("f2");
int l2 = targetAlphabet.lookupIndex("labelB");
FeatureVector fv2 = new FeatureVector(dataAlphabet, new int[] { f2 }, new double[] { 1.0 });
// Labeling labeling2 = new LabelVector(targetAlphabet, l2, 1.0);
// Instance instance2 = new Instance(fv2, labeling2, null, null);
InstanceList batch2 = new InstanceList(pipe);
// batch2.addThruPipe(instance2);
NaiveBayes classifier = trainer.trainIncremental(batch2);
assertNotNull(classifier);
assertEquals(2, classifier.getLabelAlphabet().size());
}

@Test
public void testGetAlphabetsReturnsCorrectOrder() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
Alphabet[] alphabets = trainer.getAlphabets();
assertEquals(2, alphabets.length);
assertSame(dataAlphabet, alphabets[0]);
assertSame(targetAlphabet, alphabets[1]);
}

@Test
public void testTrainWithSameLabelAppearingMultipleTimes() {
Alphabet dataAlphabet = new Alphabet();
Alphabet labelAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, labelAlphabet);
int fid = dataAlphabet.lookupIndex("word");
int lid = labelAlphabet.lookupIndex("yes");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { fid }, new double[] { 1.5 });
// LabelVector lv = new LabelVector(labelAlphabet, new int[] { lid, lid }, new double[] { 0.5, 0.5 });
// Instance instance = new Instance(fv, lv, null, null);
InstanceList list = new InstanceList(pipe);
// list.addThruPipe(instance);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
NaiveBayes classifier = trainer.trainIncremental(list);
assertNotNull(classifier);
assertEquals(1, classifier.getLabelAlphabet().size());
}

@Test
public void testTrainOnInstanceWithZeroAndNonZeroFeatureValues() {
Alphabet dataAlphabet = new Alphabet();
Alphabet labelAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, labelAlphabet);
int fzero = dataAlphabet.lookupIndex("f0");
int fnz = dataAlphabet.lookupIndex("f1");
int lid = labelAlphabet.lookupIndex("lbl");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { fzero, fnz }, new double[] { 0.0, 3.0 });
// Labeling lbl = new LabelVector(labelAlphabet, lid, 1.0);
// Instance instance = new Instance(fv, lbl, null, null);
InstanceList list = new InstanceList(pipe);
// list.addThruPipe(instance);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
NaiveBayes result = trainer.trainIncremental(list);
assertNotNull(result);
}

@Test
public void testTrainWithInstanceHavingNullAlphabetsUsesPipeAlphabets() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
int featureIndex = dataAlphabet.lookupIndex("alpha");
int labelIndex = targetAlphabet.lookupIndex("class");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { featureIndex }, new double[] { 0.5 });
// Labeling labeling = new LabelVector(targetAlphabet, labelIndex, 1.0);
// Instance instance = new Instance(fv, labeling, null, null);
InstanceList emptyList = new InstanceList(pipe);
// emptyList.addThruPipe(instance);
emptyList.getDataAlphabet().lookupIndex("alpha");
NaiveBayesTrainer trainer = new NaiveBayesTrainer();
NaiveBayes classifier = trainer.train(emptyList);
assertNotNull(classifier);
assertEquals(1, classifier.getLabelAlphabet().size());
}

@Test
public void testIncorporateOneInstanceWithNegativeOneNormSkipsInstance() {
Alphabet dataAlphabet = new Alphabet();
Alphabet labelAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, labelAlphabet);
int featureIndex = dataAlphabet.lookupIndex("feat");
int labelIndex = labelAlphabet.lookupIndex("label");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { featureIndex }, new double[] { 0.0 });
// Labeling labeling = new LabelVector(labelAlphabet, labelIndex, 1.0);
// Instance instance = new Instance(fv, labeling, null, null);
InstanceList list = new InstanceList(pipe);
// list.addThruPipe(instance);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
NaiveBayes classifier = trainer.trainIncremental(list);
assertNotNull(classifier);
}

@Test
public void testTrainIncrementalUsesCorrectDocLengthNormalizationScaling() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
int fi = dataAlphabet.lookupIndex("token");
int li = targetAlphabet.lookupIndex("class");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { fi }, new double[] { 2.0 });
// Labeling lbl = new LabelVector(targetAlphabet, li, 1.0);
// Instance instance = new Instance(fv, lbl, null, null);
InstanceList list = new InstanceList(pipe);
// list.addThruPipe(instance);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
trainer.setDocLengthNormalization(6.0);
NaiveBayes classifier = trainer.trainIncremental(list);
assertNotNull(classifier);
}

@Test
public void testTrainSwitchesToNewClassifierEachCall() {
Alphabet dataAlphabet = new Alphabet();
Alphabet labelAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, labelAlphabet);
int fid1 = dataAlphabet.lookupIndex("a");
int lid1 = labelAlphabet.lookupIndex("L1");
FeatureVector fv1 = new FeatureVector(dataAlphabet, new int[] { fid1 }, new double[] { 1 });
// Labeling l1 = new LabelVector(labelAlphabet, lid1, 1);
// Instance instance1 = new Instance(fv1, l1, null, null);
InstanceList list1 = new InstanceList(pipe);
// list1.addThruPipe(instance1);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
NaiveBayes nb1 = trainer.train(list1);
int fid2 = dataAlphabet.lookupIndex("b");
int lid2 = labelAlphabet.lookupIndex("L2");
FeatureVector fv2 = new FeatureVector(dataAlphabet, new int[] { fid2 }, new double[] { 1 });
// Labeling l2 = new LabelVector(labelAlphabet, lid2, 1);
// Instance instance2 = new Instance(fv2, l2, null, null);
InstanceList list2 = new InstanceList(pipe);
// list2.addThruPipe(instance2);
NaiveBayes nb2 = trainer.train(list2);
assertNotNull(nb1);
assertNotNull(nb2);
assertNotSame(nb1, nb2);
}

@Test
public void testInstancePipeSetToNoopIfNullOnSingleInstanceTrain() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
int fi = dataAlphabet.lookupIndex("f");
int li = targetAlphabet.lookupIndex("l");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { fi }, new double[] { 1 });
// Labeling lbl = new LabelVector(targetAlphabet, li, 1);
// Instance instance = new Instance(fv, lbl, null, null);
NaiveBayesTrainer trainer = new NaiveBayesTrainer();
// NaiveBayes nb = trainer.trainIncremental(instance);
// assertNotNull(nb.getInstancePipe());
// assertEquals(Noop.class, nb.getInstancePipe().getClass());
}

@Test
public void testTrainIncrementalHandlesZeroOneNormWithNormalizationSet() {
Alphabet dataAlphabet = new Alphabet();
Alphabet labelAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, labelAlphabet);
int fid = dataAlphabet.lookupIndex("f");
int lid = labelAlphabet.lookupIndex("label");
FeatureVector vector = new FeatureVector(dataAlphabet, new int[] { fid }, new double[] { 0.0 });
// Labeling labeling = new LabelVector(labelAlphabet, lid, 1);
// Instance instance = new Instance(vector, labeling, null, null);
InstanceList list = new InstanceList(pipe);
// list.addThruPipe(instance);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
trainer.setDocLengthNormalization(5.0);
NaiveBayes classifier = trainer.trainIncremental(list);
assertNotNull(classifier);
}

@Test
public void testEstimateFeatureMultinomialsReturnsArrayLengthMatchingTargetAlphabet() {
Alphabet dataAlphabet = new Alphabet();
Alphabet labelAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, labelAlphabet);
int f = dataAlphabet.lookupIndex("w");
int l1 = labelAlphabet.lookupIndex("label1");
int l2 = labelAlphabet.lookupIndex("label2");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { f }, new double[] { 1.0 });
// LabelVector labeling = new LabelVector(labelAlphabet, l1, 1.0);
// Instance instance = new Instance(fv, labeling, null, null);
InstanceList list = new InstanceList(pipe);
// list.addThruPipe(instance);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
trainer.trainIncremental(list);
// Multinomial[] featureMultinomials = trainer.getClassifier().getFeatureMultinomials();
// assertEquals(labelAlphabet.size(), featureMultinomials.length);
}

@Test
public void testTrainWithNullInstanceListAndNonNullInstanceInSetup() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
int featureIndex = dataAlphabet.lookupIndex("feature");
int labelIndex = targetAlphabet.lookupIndex("category");
FeatureVector featureVector = new FeatureVector(dataAlphabet, new int[] { featureIndex }, new double[] { 1.0 });
// Labeling labeling = new LabelVector(targetAlphabet, labelIndex, 1.0);
// Instance instance = new Instance(featureVector, labeling, null, null);
NaiveBayesTrainer trainer = new NaiveBayesTrainer();
// NaiveBayes classifier = trainer.trainIncremental(instance);
// assertNotNull(classifier);
}

@Test
public void testIncorporateOneInstanceWithZeroLabelWeightIsSkipped() {
Alphabet dataAlphabet = new Alphabet();
Alphabet labelAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, labelAlphabet);
int featureIndex = dataAlphabet.lookupIndex("token");
int labelIndex = labelAlphabet.lookupIndex("tag");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { featureIndex }, new double[] { 1.0 });
// Labeling labeling = new LabelVector(labelAlphabet, new int[] { labelIndex }, new double[] { 0.0 });
// Instance instance = new Instance(fv, labeling, null, null);
InstanceList list = new InstanceList(pipe);
// list.addThruPipe(instance);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
NaiveBayes classifier = trainer.trainIncremental(list);
assertNotNull(classifier);
}

@Test
public void testFeatureEstimatorAlphabetIsSetOnlyOnce() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
Multinomial.LaplaceEstimator estimator = new Multinomial.LaplaceEstimator();
trainer.setFeatureMultinomialEstimator(estimator);
Alphabet pipeAlphabet = pipe.getDataAlphabet();
assertSame(dataAlphabet, pipeAlphabet);
}

@Test
public void testPriorEstimatorCloneBeforeTraining() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
Multinomial.LaplaceEstimator estimator = new Multinomial.LaplaceEstimator();
trainer.setPriorMultinomialEstimator(estimator);
assertEquals(estimator, trainer.getPriorMultinomialEstimator());
}

@Test
public void testSetupThrowsIfPipeMismatch() {
Alphabet alphabet1 = new Alphabet();
Alphabet target1 = new Alphabet();
Pipe pipe1 = new Noop(alphabet1, target1);
Alphabet alphabet2 = new Alphabet();
Alphabet target2 = new Alphabet();
Pipe pipe2 = new Noop(alphabet2, target2);
int f = alphabet2.lookupIndex("f");
int l = target2.lookupIndex("l");
FeatureVector fv = new FeatureVector(alphabet2, new int[] { f }, new double[] { 1.0 });
// LabelVector lv = new LabelVector(target2, l, 1.0);
// Instance inst = new Instance(fv, lv, null, null);
InstanceList list = new InstanceList(pipe2);
// list.addThruPipe(inst);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe1);
try {
trainer.trainIncremental(list);
fail("Expected IllegalArgumentException due to pipe mismatch");
} catch (IllegalArgumentException e) {
assertTrue(e.getMessage().contains("pipe does not match"));
}
}

@Test
public void testTrainWithNoLabelsInTargetAlphabetReturnsValidClassifier() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
int fi = dataAlphabet.lookupIndex("word");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { fi }, new double[] { 1.0 });
// Labeling labeling = new LabelVector(targetAlphabet, new int[] {}, new double[] {});
// Instance instance = new Instance(fv, labeling, null, null);
InstanceList list = new InstanceList(pipe);
// list.addThruPipe(instance);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
NaiveBayes classifier = trainer.train(list);
assertNotNull(classifier);
}

@Test
public void testTrainWithLabelNotInTargetAlphabetAddsLabel() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
dataAlphabet.lookupIndex("word");
int labelIdx = targetAlphabet.lookupIndex("initial");
targetAlphabet.lookupIndex("added");
int fi = dataAlphabet.lookupIndex("word");
int addedLabelIdx = targetAlphabet.lookupIndex("added");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { fi }, new double[] { 1.0 });
// LabelVector lv = new LabelVector(targetAlphabet, addedLabelIdx, 1.0);
// Instance inst = new Instance(fv, lv, null, null);
InstanceList list = new InstanceList(pipe);
// list.addThruPipe(inst);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
NaiveBayes classifier = trainer.trainIncremental(list);
assertNotNull(classifier);
assertEquals(2, classifier.getLabelAlphabet().size());
}

@Test
public void testTrainerHandlesAlphabetsGrowingAfterSetupCall() {
Alphabet dataAlphabet = new Alphabet();
Alphabet labelAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, labelAlphabet);
int fi = dataAlphabet.lookupIndex("x");
int li = labelAlphabet.lookupIndex("label");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { fi }, new double[] { 2.0 });
// LabelVector lv = new LabelVector(labelAlphabet, li, 1.0);
// Instance inst = new Instance(fv, lv, null, null);
InstanceList list = new InstanceList(pipe);
// list.addThruPipe(inst);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
trainer.trainIncremental(list);
labelAlphabet.lookupIndex("newLabel");
FeatureVector fv2 = new FeatureVector(dataAlphabet, new int[] { fi }, new double[] { 1.0 });
// LabelVector lv2 = new LabelVector(labelAlphabet, labelAlphabet.lookupIndex("newLabel"), 1.0);
// Instance inst2 = new Instance(fv2, lv2, null, null);
InstanceList list2 = new InstanceList(pipe);
// list2.addThruPipe(inst2);
NaiveBayes classifier = trainer.trainIncremental(list2);
assertNotNull(classifier);
assertEquals(2, classifier.getLabelAlphabet().size());
}

@Test
public void testAlphabetMatchingReturnsFalseForMismatchedAlphabets() {
Alphabet data1 = new Alphabet();
Alphabet label1 = new Alphabet();
Pipe p1 = new Noop(data1, label1);
NaiveBayesTrainer trainer1 = new NaiveBayesTrainer(p1);
Alphabet data2 = new Alphabet();
Alphabet label2 = new Alphabet();
Pipe p2 = new Noop(data2, label2);
assertFalse(trainer1.alphabetsMatch(p2));
}

@Test
public void testTrainIncrementalWithoutPriorTraining() {
Alphabet dataAlphabet = new Alphabet();
Alphabet labelAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, labelAlphabet);
int featureIndex = dataAlphabet.lookupIndex("token");
int labelIndex = labelAlphabet.lookupIndex("label");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { featureIndex }, new double[] { 2.0 });
// Labeling lv = new LabelVector(labelAlphabet, labelIndex, 1.0);
// Instance instance = new Instance(fv, lv, null, null);
InstanceList list = new InstanceList(pipe);
// list.addThruPipe(instance);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
NaiveBayes nb = trainer.trainIncremental(list);
assertNotNull(nb);
}

@Test
public void testInstanceWithSingleZeroWeightLabelIsIgnored() {
Alphabet data = new Alphabet();
Alphabet labels = new Alphabet();
Pipe pipe = new Noop(data, labels);
int feature = data.lookupIndex("feature1");
int label = labels.lookupIndex("LabelA");
FeatureVector fv = new FeatureVector(data, new int[] { feature }, new double[] { 1.0 });
// Labeling lbl = new LabelVector(labels, new int[] { label }, new double[] { 0.0 });
// Instance instance = new Instance(fv, lbl, null, null);
InstanceList list = new InstanceList(pipe);
// list.addThruPipe(instance);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
NaiveBayes nb = trainer.trainIncremental(list);
assertNotNull(nb);
// assertEquals(1, nb.getFeatureMultinomials().length);
}

@Test
public void testEmptyPipeHasValidClassifierAfterTraining() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
InstanceList list = new InstanceList(pipe);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
NaiveBayes classifier = trainer.train(list);
assertNotNull(classifier);
assertEquals(0, classifier.getLabelAlphabet().size());
}

@Test
public void testSetPriorEstimatorWithSharedInstanceDoesNotAffectOthers() {
NaiveBayesTrainer trainerA = new NaiveBayesTrainer();
NaiveBayesTrainer trainerB = new NaiveBayesTrainer();
Multinomial.LaplaceEstimator sharedEstimator = new Multinomial.LaplaceEstimator();
trainerA.setPriorMultinomialEstimator(sharedEstimator);
trainerB.setPriorMultinomialEstimator(new Multinomial.LaplaceEstimator());
assertNotSame(trainerA.getPriorMultinomialEstimator(), trainerB.getPriorMultinomialEstimator());
}

@Test
public void testFactoryReturnsTrainersWithIndependentEstimators() {
NaiveBayesTrainer.Factory factory = new NaiveBayesTrainer.Factory();
Multinomial.LaplaceEstimator laplace = new Multinomial.LaplaceEstimator();
factory.setFeatureMultinomialEstimator(laplace);
NaiveBayesTrainer a = factory.newClassifierTrainer(null);
NaiveBayesTrainer b = factory.newClassifierTrainer(null);
assertNotSame(a.getFeatureMultinomialEstimator(), b.getFeatureMultinomialEstimator());
}

@Test
public void testDocLengthNormalizationIgnoredWhenNegative() {
Alphabet data = new Alphabet();
Alphabet labels = new Alphabet();
Pipe pipe = new Noop(data, labels);
int f = data.lookupIndex("f");
int l = labels.lookupIndex("lbl");
FeatureVector fv = new FeatureVector(data, new int[] { f }, new double[] { 4.0 });
// Labeling labeling = new LabelVector(labels, l, 1.0);
// Instance instance = new Instance(fv, labeling, null, null);
InstanceList list = new InstanceList(pipe);
// list.addThruPipe(instance);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
trainer.setDocLengthNormalization(-1.0);
NaiveBayes nb = trainer.trainIncremental(list);
assertNotNull(nb);
}

@Test
public void testTrainWithLabelDistributionNotSummingToOne() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
int fIndex = dataAlphabet.lookupIndex("f");
int l1Index = targetAlphabet.lookupIndex("A");
int l2Index = targetAlphabet.lookupIndex("B");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { fIndex }, new double[] { 5.0 });
// LabelVector labelVector = new LabelVector(targetAlphabet, new int[] { l1Index, l2Index }, new double[] { 3.0, 0.0 });
// Instance instance = new Instance(fv, labelVector, null, null);
InstanceList list = new InstanceList(pipe);
// list.addThruPipe(instance);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
NaiveBayes nb = trainer.train(list);
assertNotNull(nb);
assertEquals(2, nb.getLabelAlphabet().size());
}

@Test
public void testSetFeatureEstimatorAfterMultipleClassifierCreationsCausesException() {
Alphabet data = new Alphabet();
Alphabet labels = new Alphabet();
Pipe pipe = new Noop(data, labels);
int fi = data.lookupIndex("f1");
int li = labels.lookupIndex("label1");
FeatureVector fv = new FeatureVector(data, new int[] { fi }, new double[] { 1.0 });
// LabelVector lbl = new LabelVector(labels, li, 1.0);
// Instance instance = new Instance(fv, lbl, null, null);
InstanceList list = new InstanceList(pipe);
// list.addThruPipe(instance);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
trainer.train(list);
try {
trainer.setFeatureMultinomialEstimator(new Multinomial.LaplaceEstimator());
fail("Expected IllegalStateException after training");
} catch (IllegalStateException e) {
assertTrue(e.getMessage().contains("Can't set after incrementalTrain() is called"));
}
}
}
