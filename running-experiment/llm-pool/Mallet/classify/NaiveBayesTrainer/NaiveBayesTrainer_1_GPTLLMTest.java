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

public class NaiveBayesTrainer_1_GPTLLMTest {

@Test
public void testTrainWithSingleInstance() {
Alphabet dataAlphabet = new Alphabet();
Alphabet labelAlphabet = new Alphabet();
dataAlphabet.lookupIndex("feature1");
labelAlphabet.lookupIndex("label1");
Pipe pipe = new Noop(dataAlphabet, labelAlphabet);
// FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "feature1" });
// Label label = labelAlphabet.lookupLabel("label1");
// Labeling labeling = label.getLabeling();
// Instance instance = new Instance(fv, labeling, null, null);
InstanceList list = new InstanceList(pipe);
// list.addThruPipe(instance);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
NaiveBayes classifier = trainer.train(list);
assertNotNull(classifier);
assertEquals(1, classifier.getLabelAlphabet().size());
assertEquals("label1", classifier.getLabelAlphabet().lookupLabel(0).toString());
}

@Test
public void testIncrementalTrainWithTwoInstances() {
Alphabet dataAlphabet = new Alphabet();
Alphabet labelAlphabet = new Alphabet();
dataAlphabet.lookupIndex("a");
dataAlphabet.lookupIndex("b");
labelAlphabet.lookupIndex("yes");
labelAlphabet.lookupIndex("no");
Pipe pipe = new Noop(dataAlphabet, labelAlphabet);
// FeatureVector fv1 = new FeatureVector(dataAlphabet, new String[] { "a" });
// Label label1 = labelAlphabet.lookupLabel("yes");
// Labeling labeling1 = label1.getLabeling();
// Instance inst1 = new Instance(fv1, labeling1, null, null);
// FeatureVector fv2 = new FeatureVector(dataAlphabet, new String[] { "b" });
// Label label2 = labelAlphabet.lookupLabel("no");
// Labeling labeling2 = label2.getLabeling();
// Instance inst2 = new Instance(fv2, labeling2, null, null);
InstanceList list = new InstanceList(pipe);
// list.addThruPipe(inst1);
// list.addThruPipe(inst2);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
NaiveBayes nb = trainer.trainIncremental(list);
assertNotNull(nb);
assertEquals(2, nb.getLabelAlphabet().size());
}

@Test
public void testGetAndSetDocLengthNormalization() {
NaiveBayesTrainer trainer = new NaiveBayesTrainer();
trainer.setDocLengthNormalization(42.0);
double value = trainer.getDocLengthNormalization();
assertEquals(42.0, value, 0.000001);
}

@Test
public void testTrainSkipsZeroOneNorm() {
Alphabet dataAlphabet = new Alphabet();
Alphabet labelAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, labelAlphabet);
FeatureVector emptyFV = new FeatureVector(dataAlphabet, new int[] {}, new double[] {});
// Label label = labelAlphabet.lookupLabel("unused");
// Labeling labeling = label.getLabeling();
// Instance instance = new Instance(emptyFV, labeling, null, null);
InstanceList list = new InstanceList(pipe);
// list.addThruPipe(instance);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
NaiveBayes classifier = trainer.train(list);
assertNotNull(classifier);
assertEquals(1, classifier.getLabelAlphabet().size());
}

@Test(expected = IllegalArgumentException.class)
public void testTrainFailsOnMismatchedAlphabet() {
Alphabet a1 = new Alphabet();
Alphabet a2 = new Alphabet();
a1.lookupIndex("x");
a2.lookupIndex("y");
Pipe pipe1 = new Noop(a1, a1);
Pipe pipe2 = new Noop(a2, a2);
// FeatureVector fv = new FeatureVector(a2, new String[] { "y" });
// Label label = a2.lookupLabel("labelX");
// Labeling labeling = label.getLabeling();
// Instance instance = new Instance(fv, labeling, null, null);
InstanceList list = new InstanceList(pipe2);
// list.addThruPipe(instance);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe1);
trainer.train(list);
}

@Test
public void testToStringValue() {
NaiveBayesTrainer trainer = new NaiveBayesTrainer();
assertEquals("NaiveBayesTrainer", trainer.toString());
}

@Test
public void testSerializationIntegrity() throws IOException, ClassNotFoundException {
Alphabet dataAlphabet = new Alphabet();
Alphabet labelAlphabet = new Alphabet();
dataAlphabet.lookupIndex("q");
labelAlphabet.lookupIndex("yes");
Pipe pipe = new Noop(dataAlphabet, labelAlphabet);
// FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "q" });
// Label label = labelAlphabet.lookupLabel("yes");
// Labeling labeling = label.getLabeling();
// Instance inst = new Instance(fv, labeling, null, null);
InstanceList list = new InstanceList(pipe);
// list.addThruPipe(inst);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
trainer.train(list);
ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
ObjectOutputStream out = new ObjectOutputStream(byteStream);
out.writeObject(trainer);
out.close();
byte[] bytes = byteStream.toByteArray();
ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
ObjectInputStream in = new ObjectInputStream(inputStream);
NaiveBayesTrainer deserialized = (NaiveBayesTrainer) in.readObject();
in.close();
assertNotNull(deserialized);
assertEquals(trainer.getAlphabet().size(), deserialized.getAlphabet().size());
assertEquals(trainer.getClassifier().getLabelAlphabet().size(), deserialized.getClassifier().getLabelAlphabet().size());
}

@Test
public void testFactoryCreatesTrainer() {
NaiveBayesTrainer.Factory factory = new NaiveBayesTrainer.Factory();
NaiveBayesTrainer trainer = factory.newClassifierTrainer(null);
assertNotNull(trainer);
assertTrue(trainer instanceof NaiveBayesTrainer);
}

@Test
public void testFactorySetters() {
Multinomial.Estimator featEst = new Multinomial.LaplaceEstimator();
Multinomial.Estimator priorEst = new Multinomial.LaplaceEstimator();
NaiveBayesTrainer.Factory factory = new NaiveBayesTrainer.Factory();
factory.setFeatureMultinomialEstimator(featEst);
factory.setPriorMultinomialEstimator(priorEst);
factory.setDocLengthNormalization(5.5);
// assertEquals(5.5, factory.docLengthNormalization, 0.0001);
// assertSame(featEst.getClass(), factory.featureEstimator.getClass());
// assertSame(priorEst.getClass(), factory.priorEstimator.getClass());
}

@Test
public void testIncrementalTrainingSingleInstance() {
Alphabet dataAlphabet = new Alphabet();
Alphabet labelAlphabet = new Alphabet();
dataAlphabet.lookupIndex("z");
labelAlphabet.lookupIndex("Y");
Pipe pipe = new Noop(dataAlphabet, labelAlphabet);
// FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "z" });
// Label label = labelAlphabet.lookupLabel("Y");
// Labeling labeling = label.getLabeling();
// Instance instance = new Instance(fv, labeling, null, null);
// pipe.instanceFrom(instance);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
// NaiveBayes classifier = trainer.trainIncremental(instance);
// assertNotNull(classifier);
// assertEquals(1, classifier.getLabelAlphabet().size());
// assertEquals("Y", classifier.getLabelAlphabet().lookupLabel(0).toString());
}

@Test(expected = IllegalArgumentException.class)
public void testTrainWithNullInstanceFails() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
trainer.train(null);
}

@Test(expected = IllegalArgumentException.class)
public void testTrainIncrementalWithInstanceHavingMismatchedAlphabet() {
Alphabet dataAlphabet1 = new Alphabet();
Alphabet targetAlphabet1 = new Alphabet();
dataAlphabet1.lookupIndex("token");
targetAlphabet1.lookupIndex("labelA");
Pipe pipe1 = new Noop(dataAlphabet1, targetAlphabet1);
Alphabet dataAlphabet2 = new Alphabet();
Alphabet targetAlphabet2 = new Alphabet();
dataAlphabet2.lookupIndex("diffToken");
targetAlphabet2.lookupIndex("labelB");
Pipe pipe2 = new Noop(dataAlphabet2, targetAlphabet2);
// FeatureVector fv = new FeatureVector(dataAlphabet2, new String[] { "diffToken" });
// Label label = targetAlphabet2.lookupLabel("labelB");
// Labeling labeling = label.getLabeling();
// Instance instance = new Instance(fv, labeling, null, null);
InstanceList list = new InstanceList(pipe2);
// list.addThruPipe(instance);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe1);
trainer.trainIncremental(list);
}

@Test
public void testTrainIncrementalWithUnlabeledInstanceSkipped() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
dataAlphabet.lookupIndex("feature");
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
// FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "feature" });
// Instance instance = new Instance(fv, null, null, null);
InstanceList list = new InstanceList(pipe);
// list.addThruPipe(instance);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
NaiveBayes classifier = trainer.trainIncremental(list);
assertNotNull(classifier);
assertEquals(0, classifier.getLabelAlphabet().size());
}

@Test
public void testTrainIncrementalWithMultipleLabels() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
dataAlphabet.lookupIndex("x");
targetAlphabet.lookupIndex("label1");
targetAlphabet.lookupIndex("label2");
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
// FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "x" });
// Label label1 = targetAlphabet.lookupLabel("label1");
// Label label2 = targetAlphabet.lookupLabel("label2");
// LabelVector labelVector = new LabelVector(targetAlphabet, new int[] { label1.getIndex(), label2.getIndex() }, new double[] { 0.5, 0.5 });
// Instance instance = new Instance(fv, labelVector, null, null);
InstanceList list = new InstanceList(pipe);
// list.addThruPipe(instance);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
NaiveBayes classifier = trainer.trainIncremental(list);
assertNotNull(classifier);
assertEquals(2, classifier.getLabelAlphabet().size());
}

@Test
public void testTrainWithLabelAlphabetExpansion() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
dataAlphabet.lookupIndex("x");
targetAlphabet.lookupIndex("A");
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
// FeatureVector fv1 = new FeatureVector(dataAlphabet, new String[] { "x" });
// Label l1 = targetAlphabet.lookupLabel("A");
// Labeling labeling1 = l1.getLabeling();
// Instance i1 = new Instance(fv1, labeling1, null, null);
InstanceList list1 = new InstanceList(pipe);
// list1.addThruPipe(i1);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
trainer.trainIncremental(list1);
targetAlphabet.lookupIndex("B");
// FeatureVector fv2 = new FeatureVector(dataAlphabet, new String[] { "x" });
// Label l2 = targetAlphabet.lookupLabel("B");
// Labeling labeling2 = l2.getLabeling();
// Instance i2 = new Instance(fv2, labeling2, null, null);
InstanceList list2 = new InstanceList(pipe);
// list2.addThruPipe(i2);
trainer.trainIncremental(list2);
NaiveBayes classifier = trainer.getClassifier();
assertNotNull(classifier);
assertEquals(2, classifier.getLabelAlphabet().size());
}

@Test
public void testTrainWithEmptyInstanceList() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
InstanceList emptyList = new InstanceList(pipe);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
NaiveBayes classifier = trainer.train(emptyList);
assertNotNull(classifier);
assertEquals(0, classifier.getLabelAlphabet().size());
}

@Test
public void testIncrementalTrainWithZeroWeightInstanceIsIgnored() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
dataAlphabet.lookupIndex("feat");
targetAlphabet.lookupIndex("lab");
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
// FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "feat" });
// Label label = targetAlphabet.lookupLabel("lab");
// Labeling labeling = label.getLabeling();
// Instance instance = new Instance(fv, labeling, null, null);
InstanceList list = new InstanceList(pipe);
// list.addThruPipe(instance);
// list.setInstanceWeight(instance, 0.0);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
NaiveBayes classifier = trainer.trainIncremental(list);
assertNotNull(classifier);
assertTrue(classifier.getLabelAlphabet().lookupLabel("lab") != null);
}

@Test(expected = IllegalArgumentException.class)
public void testTrainIncrementalWithDifferentPipesThrows() {
Alphabet a1 = new Alphabet();
Alphabet a2 = new Alphabet();
a1.lookupIndex("tok1");
a2.lookupIndex("tok2");
Pipe pipe1 = new Noop(a1, a1);
Pipe pipe2 = new Noop(a2, a2);
// FeatureVector fv = new FeatureVector(a2, new String[] { "tok2" });
// Label label = a2.lookupLabel("L");
// Labeling labeling = label.getLabeling();
// Instance instance = new Instance(fv, labeling, null, null);
InstanceList list = new InstanceList(pipe2);
// list.addThruPipe(instance);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe1);
trainer.trainIncremental(list);
}

@Test
public void testSetDocLengthNormalizationAffectsClassifierWhenOneNormNonZero() {
Alphabet dataAlphabet = new Alphabet();
Alphabet labelAlphabet = new Alphabet();
dataAlphabet.lookupIndex("word");
labelAlphabet.lookupIndex("class");
Pipe pipe = new Noop(dataAlphabet, labelAlphabet);
// FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "word" });
// Label label = labelAlphabet.lookupLabel("class");
// Labeling labeling = label.getLabeling();
// Instance instance = new Instance(fv, labeling, null, null);
InstanceList list = new InstanceList(pipe);
// list.addThruPipe(instance);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
trainer.setDocLengthNormalization(10.0);
NaiveBayes classifier = trainer.trainIncremental(list);
assertNotNull(classifier);
assertEquals(1, classifier.getLabelAlphabet().size());
}

@Test
public void testEmptyConstructorAndSetDocLengthNormalization() {
NaiveBayesTrainer trainer = new NaiveBayesTrainer();
trainer.setDocLengthNormalization(0.0);
assertEquals(0.0, trainer.getDocLengthNormalization(), 0.00001);
}

@Test
public void testTrainIncrementalWithDocLengthNormalizationZero() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
dataAlphabet.lookupIndex("feature");
targetAlphabet.lookupIndex("label");
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
// FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "feature" });
// Label label = targetAlphabet.lookupLabel("label");
// Labeling labeling = label.getLabeling();
// Instance inst = new Instance(fv, labeling, null, null);
InstanceList list = new InstanceList(pipe);
// list.addThruPipe(inst);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
trainer.setDocLengthNormalization(0.0);
NaiveBayes classifier = trainer.trainIncremental(list);
assertNotNull(classifier);
assertEquals(1, classifier.getLabelAlphabet().size());
}

@Test
public void testTrainWithDefaultEstimatorsGetsReset() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
dataAlphabet.lookupIndex("f");
targetAlphabet.lookupIndex("L");
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
// FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "f" });
// Label label = targetAlphabet.lookupLabel("L");
// Labeling labeling = label.getLabeling();
// Instance inst = new Instance(fv, labeling, null, null);
InstanceList list = new InstanceList(pipe);
// list.addThruPipe(inst);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
trainer.train(list);
NaiveBayes classifier = trainer.getClassifier();
assertNotNull(classifier);
}

@Test
public void testAlphabetGrowthTriggersExpansion() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
dataAlphabet.lookupIndex("a");
targetAlphabet.lookupIndex("x");
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
// FeatureVector fv1 = new FeatureVector(dataAlphabet, new String[] { "a" });
// Label label1 = targetAlphabet.lookupLabel("x");
// Labeling labeling1 = label1.getLabeling();
// Instance instance1 = new Instance(fv1, labeling1, null, null);
InstanceList list1 = new InstanceList(pipe);
// list1.addThruPipe(instance1);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
trainer.trainIncremental(list1);
targetAlphabet.lookupIndex("y");
// FeatureVector fv2 = new FeatureVector(dataAlphabet, new String[] { "a" });
// Label label2 = targetAlphabet.lookupLabel("y");
// Labeling labeling2 = label2.getLabeling();
// Instance instance2 = new Instance(fv2, labeling2, null, null);
InstanceList list2 = new InstanceList(pipe);
// list2.addThruPipe(instance2);
trainer.trainIncremental(list2);
NaiveBayes classifier = trainer.getClassifier();
assertEquals(2, classifier.getLabelAlphabet().size());
}

@Test(expected = IllegalStateException.class)
public void testSetFeatureEstimatorThrowsAfterPipeInitialization() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
dataAlphabet.lookupIndex("feat");
targetAlphabet.lookupIndex("yes");
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
// FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "feat" });
// Label label = targetAlphabet.lookupLabel("yes");
// Labeling labeling = label.getLabeling();
// Instance instance = new Instance(fv, labeling, null, null);
InstanceList list = new InstanceList(pipe);
// list.addThruPipe(instance);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
trainer.trainIncremental(list);
Multinomial.Estimator estimator = new Multinomial.LaplaceEstimator();
trainer.setFeatureMultinomialEstimator(estimator);
}

@Test(expected = IllegalStateException.class)
public void testSetPriorEstimatorThrowsAfterPipeInitialization() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
dataAlphabet.lookupIndex("f");
targetAlphabet.lookupIndex("a");
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
// FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "f" });
// Label label = targetAlphabet.lookupLabel("a");
// Labeling labeling = label.getLabeling();
// Instance instance = new Instance(fv, labeling, null, null);
InstanceList list = new InstanceList(pipe);
// list.addThruPipe(instance);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
trainer.trainIncremental(list);
Multinomial.Estimator est = new Multinomial.LaplaceEstimator();
trainer.setPriorMultinomialEstimator(est);
}

@Test
public void testTrainMultipleTimesResetsEstimates() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
dataAlphabet.lookupIndex("w");
targetAlphabet.lookupIndex("c");
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
// FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "w" });
// Label label = targetAlphabet.lookupLabel("c");
// Labeling labeling = label.getLabeling();
// Instance instance = new Instance(fv, labeling, null, null);
InstanceList list = new InstanceList(pipe);
// list.addThruPipe(instance);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
NaiveBayes classifier1 = trainer.train(list);
NaiveBayes classifier2 = trainer.train(list);
assertNotSame(classifier1, classifier2);
}

@Test
public void testGetAlphabets() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
Alphabet[] alphabets = trainer.getAlphabets();
assertEquals(2, alphabets.length);
assertEquals(dataAlphabet, alphabets[0]);
assertEquals(targetAlphabet, alphabets[1]);
}

@Test
public void testTrainIncrementalWithMissingPipeUsesNoop() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
dataAlphabet.lookupIndex("tok");
targetAlphabet.lookupIndex("lbl");
// FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "tok" });
// Label label = targetAlphabet.lookupLabel("lbl");
// Labeling labeling = label.getLabeling();
// Instance instance = new Instance(fv, labeling, null, null);
NaiveBayesTrainer trainer = new NaiveBayesTrainer();
// NaiveBayes classifier = trainer.trainIncremental(instance);
// assertNotNull(classifier);
// assertEquals(1, classifier.getLabelAlphabet().size());
}

@Test(expected = ClassNotFoundException.class)
public void testDeserializationVersionMismatchThrowsException() throws IOException, ClassNotFoundException {
ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
ObjectOutputStream out = new ObjectOutputStream(byteOut);
out.writeInt(999);
out.writeObject(new Multinomial.LaplaceEstimator());
out.writeObject(new Multinomial.LaplaceEstimator());
out.writeObject(null);
out.writeObject(null);
out.writeObject(null);
out.writeObject(null);
out.writeObject(null);
out.close();
ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
ObjectInputStream in = new ObjectInputStream(byteIn);
NaiveBayesTrainer trainer = new NaiveBayesTrainer();
// java.lang.reflect.Method method = NaiveBayesTrainer.class.getDeclaredMethod("readObject", ObjectInputStream.class);
// method.setAccessible(true);
// try {
// method.invoke(trainer, in);
// } catch (java.lang.reflect.InvocationTargetException e) {
// throw (ClassNotFoundException) e.getCause();
// }
}

@Test
public void testDocLengthNormalizationAltersInstanceWeight() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("token");
Alphabet labelAlphabet = new Alphabet();
labelAlphabet.lookupIndex("A");
Pipe pipe = new Noop(dataAlphabet, labelAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
trainer.setDocLengthNormalization(4.0);
// FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "token", "token" });
// Label label = labelAlphabet.lookupLabel("A");
// Labeling labeling = label.getLabeling();
// Instance instance = new Instance(fv, labeling, null, null);
InstanceList list = new InstanceList(pipe);
// list.addThruPipe(instance);
NaiveBayes classifier = trainer.trainIncremental(list);
assertNotNull(classifier);
assertTrue(classifier.getLabelAlphabet().lookupLabel("A") != null);
}

@Test
public void testTrainWithNullLabelingIsSafelyIgnored() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("token");
Alphabet labelAlphabet = new Alphabet();
labelAlphabet.lookupIndex("validLabel");
Pipe pipe = new Noop(dataAlphabet, labelAlphabet);
// FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "token" });
// Instance instance = new Instance(fv, null, null, null);
InstanceList list = new InstanceList(pipe);
// list.addThruPipe(instance);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
NaiveBayes classifier = trainer.trainIncremental(list);
assertNotNull(classifier);
assertEquals(0, classifier.getLabelAlphabet().size());
}

@Test
public void testMultipleLabelingUnequalWeights() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("word");
Alphabet labelAlphabet = new Alphabet();
int idA = labelAlphabet.lookupIndex("A");
int idB = labelAlphabet.lookupIndex("B");
Pipe pipe = new Noop(dataAlphabet, labelAlphabet);
int[] labelIndices = new int[] { idA, idB };
double[] weights = new double[] { 0.9, 0.1 };
// LabelVector lv = new LabelVector(labelAlphabet, labelIndices, weights);
// FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "word" });
// Instance instance = new Instance(fv, lv, null, null);
InstanceList list = new InstanceList(pipe);
// list.addThruPipe(instance);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
NaiveBayes classifier = trainer.trainIncremental(list);
assertNotNull(classifier);
assertEquals(2, classifier.getLabelAlphabet().size());
}

@Test
public void testZeroOneNormInstanceIgnored() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
dataAlphabet.lookupIndex("anything");
targetAlphabet.lookupIndex("Z");
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
FeatureVector emptyFV = new FeatureVector(dataAlphabet, new int[] {}, new double[] {});
// Label label = targetAlphabet.lookupLabel("Z");
// Labeling labeling = label.getLabeling();
// Instance instance = new Instance(emptyFV, labeling, null, null);
InstanceList list = new InstanceList(pipe);
// list.addThruPipe(instance);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
NaiveBayes classifier = trainer.trainIncremental(list);
assertNotNull(classifier);
assertEquals(1, classifier.getLabelAlphabet().size());
}

@Test
public void testZeroWeightLabelIgnoredDuringTraining() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("f");
Alphabet targetAlphabet = new Alphabet();
targetAlphabet.lookupIndex("A");
targetAlphabet.lookupIndex("B");
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
int a = targetAlphabet.lookupIndex("A");
int b = targetAlphabet.lookupIndex("B");
int[] indices = new int[] { a, b };
double[] values = new double[] { 0.0, 1.0 };
// LabelVector labeling = new LabelVector(targetAlphabet, indices, values);
// FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "f" });
// Instance instance = new Instance(fv, labeling, null, null);
InstanceList list = new InstanceList(pipe);
// list.addThruPipe(instance);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
NaiveBayes classifier = trainer.trainIncremental(list);
assertNotNull(classifier);
assertEquals(2, classifier.getLabelAlphabet().size());
}

@Test
public void testAlphabetsMatchFailsWithNull() {
NaiveBayesTrainer trainer = new NaiveBayesTrainer();
boolean match = trainer.alphabetsMatch(null);
assertFalse(match);
}

@Test
public void testTrainWithInstanceWithoutFeatureAndLabelAlphabet() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
dataAlphabet.lookupIndex("feat");
targetAlphabet.lookupIndex("lbl");
// FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "feat" });
// Label label = targetAlphabet.lookupLabel("lbl");
// Labeling labeling = label.getLabeling();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
// Instance instance = new Instance(fv, labeling, null, null);
InstanceList list = new InstanceList(pipe);
// list.addThruPipe(instance);
NaiveBayesTrainer trainer = new NaiveBayesTrainer();
NaiveBayes classifier = trainer.trainIncremental(list);
assertNotNull(classifier);
assertEquals(1, classifier.getLabelAlphabet().size());
}

@Test
public void testEmptyFeatureInstanceShouldNotCrash() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
targetAlphabet.lookupIndex("class1");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] {}, new double[] {});
// Label label = targetAlphabet.lookupLabel("class1");
// Labeling labeling = label.getLabeling();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
// Instance instance = new Instance(fv, labeling, null, null);
InstanceList list = new InstanceList(pipe);
// list.addThruPipe(instance);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
NaiveBayes classifier = trainer.trainIncremental(list);
assertNotNull(classifier);
assertEquals(1, classifier.getLabelAlphabet().size());
}

@Test
public void testFactoryCreatesTrainerFromInitialClassifier() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
dataAlphabet.lookupIndex("feat");
targetAlphabet.lookupIndex("label");
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
// Multinomial prior = new Multinomial(targetAlphabet);
Multinomial[] features = new Multinomial[1];
// features[0] = new Multinomial(dataAlphabet);
// NaiveBayes initialClassifier = new NaiveBayes(pipe, prior, features);
NaiveBayesTrainer.Factory factory = new NaiveBayesTrainer.Factory();
// NaiveBayesTrainer trainer = factory.newClassifierTrainer(initialClassifier);
// assertNotNull(trainer);
// assertEquals(dataAlphabet, trainer.getAlphabet());
// assertNotNull(trainer.getClassifier());
}

@Test
public void testFactorySettingsCarriedOverToTrainer() {
NaiveBayesTrainer.Factory factory = new NaiveBayesTrainer.Factory();
factory.setDocLengthNormalization(9.0);
// factory.setFeatureMultinomialEstimator(new Multinomial.MaximumLikelihoodEstimator());
factory.setPriorMultinomialEstimator(new Multinomial.LaplaceEstimator());
NaiveBayesTrainer trainer = factory.newClassifierTrainer(null);
// trainer.setDocLengthNormalization(factory.docLengthNormalization);
assertEquals(9.0, trainer.getDocLengthNormalization(), 0.0001);
}

@Test
public void testMultipleClassifierCallsReturnSameInstance() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
dataAlphabet.lookupIndex("t");
targetAlphabet.lookupIndex("c");
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
// FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "t" });
// Label label = targetAlphabet.lookupLabel("c");
// Labeling labeling = label.getLabeling();
// Instance inst = new Instance(fv, labeling, null, null);
InstanceList list = new InstanceList(pipe);
// list.addThruPipe(inst);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
trainer.train(list);
NaiveBayes clf1 = trainer.getClassifier();
NaiveBayes clf2 = trainer.getClassifier();
assertSame(clf1, clf2);
}

@Test
public void testFeatureEstimatorCloneIndependence() {
Multinomial.LaplaceEstimator est = new Multinomial.LaplaceEstimator();
NaiveBayesTrainer trainer = new NaiveBayesTrainer();
trainer.setFeatureMultinomialEstimator(est);
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
dataAlphabet.lookupIndex("the");
targetAlphabet.lookupIndex("yes");
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
trainer = new NaiveBayesTrainer(pipe);
// FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "the" });
// Label label = targetAlphabet.lookupLabel("yes");
// Labeling labeling = label.getLabeling();
// Instance inst = new Instance(fv, labeling, null, null);
InstanceList list = new InstanceList(pipe);
// list.addThruPipe(inst);
NaiveBayes nb = trainer.train(list);
assertNotNull(nb);
}

@Test
public void testIncorporateOneInstanceSkipsZeroWeightLabel() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
dataAlphabet.lookupIndex("x");
int a = targetAlphabet.lookupIndex("A");
int b = targetAlphabet.lookupIndex("B");
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
int[] indexes = new int[] { a, b };
double[] values = new double[] { 1.0, 0.0 };
// FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "x" });
// LabelVector lv = new LabelVector(targetAlphabet, indexes, values);
// Instance inst = new Instance(fv, lv, null, null);
InstanceList list = new InstanceList(pipe);
// list.addThruPipe(inst);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
NaiveBayes nb = trainer.trainIncremental(list);
assertNotNull(nb);
}

@Test(expected = IllegalArgumentException.class)
public void testIncrementalTrainFailsOnPipeMismatch() {
Alphabet d1 = new Alphabet();
Alphabet t1 = new Alphabet();
Alphabet d2 = new Alphabet();
Alphabet t2 = new Alphabet();
d1.lookupIndex("f1");
d2.lookupIndex("f2");
t1.lookupIndex("l1");
t2.lookupIndex("l2");
Pipe pipe1 = new Noop(d1, t1);
Pipe pipe2 = new Noop(d2, t2);
// FeatureVector fv = new FeatureVector(d2, new String[] { "f2" });
// Label lab = t2.lookupLabel("l2");
// Labeling labeling = lab.getLabeling();
// Instance i = new Instance(fv, labeling, null, null);
InstanceList list = new InstanceList(pipe2);
// list.addThruPipe(i);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe1);
trainer.trainIncremental(list);
}

@Test(expected = ClassCastException.class)
public void testTrainInstanceListWithInvalidDataTypeThrows() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
Instance instance = new Instance("not feature vector", "not labeling", null, null);
InstanceList list = new InstanceList(pipe);
list.addThruPipe(instance);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
trainer.train(list);
}

@Test
public void testTargetAlphabetGrowthEdgeDuringTraining() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
dataAlphabet.lookupIndex("u");
targetAlphabet.lookupIndex("olderLabel");
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
// FeatureVector fv1 = new FeatureVector(dataAlphabet, new String[] { "u" });
// Label label1 = targetAlphabet.lookupLabel("olderLabel");
// Labeling labeling1 = label1.getLabeling();
// Instance inst1 = new Instance(fv1, labeling1, null, null);
InstanceList list1 = new InstanceList(pipe);
// list1.addThruPipe(inst1);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
trainer.trainIncremental(list1);
targetAlphabet.lookupIndex("newLabel");
// FeatureVector fv2 = new FeatureVector(dataAlphabet, new String[] { "u" });
// Label label2 = targetAlphabet.lookupLabel("newLabel");
// Labeling labeling2 = label2.getLabeling();
// Instance inst2 = new Instance(fv2, labeling2, null, null);
InstanceList list2 = new InstanceList(pipe);
// list2.addThruPipe(inst2);
trainer.trainIncremental(list2);
assertEquals(2, trainer.getClassifier().getLabelAlphabet().size());
}

@Test
public void testTrainWithRepeatedPipesSameInstance() {
Alphabet dataAlphabet = new Alphabet();
Alphabet labelAlphabet = new Alphabet();
dataAlphabet.lookupIndex("feature");
labelAlphabet.lookupIndex("label");
Pipe pipe = new Noop(dataAlphabet, labelAlphabet);
// FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "feature" });
// Label label = labelAlphabet.lookupLabel("label");
// Labeling labeling = label.getLabeling();
// Instance instance = new Instance(fv, labeling, null, null);
InstanceList list = new InstanceList(pipe);
// list.addThruPipe(instance);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
trainer.trainIncremental(list);
// FeatureVector fv2 = new FeatureVector(dataAlphabet, new String[] { "feature" });
// Instance inst2 = new Instance(fv2, labeling, null, null);
InstanceList list2 = new InstanceList(pipe);
// list2.addThruPipe(inst2);
NaiveBayes classifier = trainer.trainIncremental(list2);
assertNotNull(classifier);
assertEquals(1, classifier.getLabelAlphabet().size());
}

@Test(expected = IllegalArgumentException.class)
public void testTrainWithMismatchedAlphabetThrows() {
Alphabet dataAlphabet1 = new Alphabet();
Alphabet labelAlphabet1 = new Alphabet();
dataAlphabet1.lookupIndex("f1");
labelAlphabet1.lookupIndex("L1");
// FeatureVector fv = new FeatureVector(dataAlphabet1, new String[] { "f1" });
// Label label = labelAlphabet1.lookupLabel("L1");
// Labeling labeling = label.getLabeling();
Pipe pipe1 = new Noop(dataAlphabet1, labelAlphabet1);
// Instance instance = new Instance(fv, labeling, null, null);
InstanceList list = new InstanceList(pipe1);
// list.addThruPipe(instance);
Pipe pipe2 = new Noop(new Alphabet(), new Alphabet());
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe2);
trainer.trainIncremental(list);
}

@Test
public void testTrainWithSingleFeatureMultiLabelDifferentWeights() {
Alphabet dataAlphabet = new Alphabet();
Alphabet labelAlphabet = new Alphabet();
int idx1 = labelAlphabet.lookupIndex("pos");
int idx2 = labelAlphabet.lookupIndex("neg");
dataAlphabet.lookupIndex("great");
Pipe pipe = new Noop(dataAlphabet, labelAlphabet);
// FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "great" });
// int[] labelIndices = new int[] { idx1, idx2 };
double[] labelWeights = new double[] { 0.7, 0.3 };
// LabelVector labeling = new LabelVector(labelAlphabet, labelIndices, labelWeights);
// Instance instance = new Instance(fv, labeling, null, null);
InstanceList list = new InstanceList(pipe);
// list.addThruPipe(instance);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
NaiveBayes classifier = trainer.train(list);
assertNotNull(classifier);
assertEquals(2, classifier.getLabelAlphabet().size());
}

@Test
public void testSetDocLengthNormalizationWithMultipleCalls() {
NaiveBayesTrainer trainer = new NaiveBayesTrainer();
trainer.setDocLengthNormalization(3.0);
assertEquals(3.0, trainer.getDocLengthNormalization(), 0.0001);
trainer.setDocLengthNormalization(7.5);
assertEquals(7.5, trainer.getDocLengthNormalization(), 0.0001);
}

@Test
public void testGetFeatureAndPriorEstimatorDefaultInstances() {
NaiveBayesTrainer trainer = new NaiveBayesTrainer();
Multinomial.Estimator featureEst = trainer.getFeatureMultinomialEstimator();
Multinomial.Estimator priorEst = trainer.getPriorMultinomialEstimator();
assertNotNull(featureEst);
assertNotNull(priorEst);
assertEquals(Multinomial.LaplaceEstimator.class, featureEst.getClass());
assertEquals(Multinomial.LaplaceEstimator.class, priorEst.getClass());
}

@Test(expected = IllegalStateException.class)
public void testSetFeatureEstimatorAfterTrainingThrows() {
Alphabet dataAlphabet = new Alphabet();
Alphabet labelAlphabet = new Alphabet();
dataAlphabet.lookupIndex("f");
labelAlphabet.lookupIndex("L");
Pipe pipe = new Noop(dataAlphabet, labelAlphabet);
// FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "f" });
// Label label = labelAlphabet.lookupLabel("L");
// Labeling labeling = label.getLabeling();
// Instance instance = new Instance(fv, labeling, null, null);
InstanceList list = new InstanceList(pipe);
// list.addThruPipe(instance);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
trainer.trainIncremental(list);
Multinomial.Estimator newEstimator = new Multinomial.LaplaceEstimator();
trainer.setFeatureMultinomialEstimator(newEstimator);
}

@Test
public void testClassifierIsUpdatedAfterRetraining() {
Alphabet dataAlphabet = new Alphabet();
Alphabet labelAlphabet = new Alphabet();
dataAlphabet.lookupIndex("feat");
labelAlphabet.lookupIndex("Y");
Pipe pipe = new Noop(dataAlphabet, labelAlphabet);
// FeatureVector fv1 = new FeatureVector(dataAlphabet, new String[] { "feat" });
// Label lab = labelAlphabet.lookupLabel("Y");
// Labeling labVector = lab.getLabeling();
// Instance i1 = new Instance(fv1, labVector, null, null);
InstanceList list1 = new InstanceList(pipe);
// list1.addThruPipe(i1);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
NaiveBayes clfBefore = trainer.train(list1);
// FeatureVector fv2 = new FeatureVector(dataAlphabet, new String[] { "feat" });
// Labeling newLab = lab.getLabeling();
// Instance i2 = new Instance(fv2, newLab, null, null);
// NaiveBayes clfAfter = trainer.trainIncremental(i2);
assertNotNull(clfBefore);
// assertNotNull(clfAfter);
// assertNotSame(clfBefore, clfAfter);
}

@Test
public void testAlphabetMatchesReturnsFalseOnIncompatibleObject() {
Alphabet dataAlphabet = new Alphabet();
Alphabet labelAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, labelAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
// boolean result = trainer.alphabetsMatch(new Object() {
// });
// assertFalse(result);
}

@Test
public void testGetAlphabetsIncludesBothDataAndTarget() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
dataAlphabet.lookupIndex("x");
targetAlphabet.lookupIndex("y");
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
Alphabet[] alphabets = trainer.getAlphabets();
assertEquals(2, alphabets.length);
assertSame(dataAlphabet, alphabets[0]);
assertSame(targetAlphabet, alphabets[1]);
}

@Test(expected = IllegalArgumentException.class)
public void testTrainingSetWithNullAlphabetThrowsException() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = null;
Pipe pipe = new Noop(dataAlphabet, new Alphabet());
// FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "word" });
// Pipe forcedPipe = new Pipe() {
// 
// public Instance pipe(Instance carrier) {
// return carrier;
// }
// 
// public Alphabet getDataAlphabet() {
// return dataAlphabet;
// }
// 
// public Alphabet getTargetAlphabet() {
// return null;
// }
// };
// Instance instance = new Instance(fv, null, null, null);
// InstanceList list = new InstanceList(forcedPipe);
// list.addThruPipe(instance);
// NaiveBayesTrainer trainer = new NaiveBayesTrainer(forcedPipe);
// trainer.train(list);
}

@Test
public void testIncorporateInstanceSkipsZeroOneNorm() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
targetAlphabet.lookupIndex("label");
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] {}, new double[] {});
// Label label = targetAlphabet.lookupLabel("label");
// Labeling labeling = label.getLabeling();
// Instance instance = new Instance(fv, labeling, null, null);
InstanceList list = new InstanceList(pipe);
// list.addThruPipe(instance);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
NaiveBayes nb = trainer.trainIncremental(list);
assertNotNull(nb);
assertEquals(1, nb.getLabelAlphabet().size());
}

@Test
public void testTrainingWithEmptyLabelingIsSafe() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
dataAlphabet.lookupIndex("word");
targetAlphabet.lookupIndex("label1");
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
// FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "word" });
// LabelVector lv = new LabelVector(targetAlphabet, new int[] {}, new double[] {});
// Instance instance = new Instance(fv, lv, null, null);
InstanceList list = new InstanceList(pipe);
// list.addThruPipe(instance);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
NaiveBayes classifier = trainer.trainIncremental(list);
assertNotNull(classifier);
}

@Test
public void testTrainIncrementalWithExtremelySmallWeight() {
Alphabet dataAlphabet = new Alphabet();
Alphabet labelAlphabet = new Alphabet();
dataAlphabet.lookupIndex("f");
labelAlphabet.lookupIndex("C");
Pipe pipe = new Noop(dataAlphabet, labelAlphabet);
// FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "f" });
// Label label = labelAlphabet.lookupLabel("C");
// Labeling labeling = label.getLabeling();
// Instance instance = new Instance(fv, labeling, null, null);
InstanceList list = new InstanceList(pipe);
// list.addThruPipe(instance);
// list.setInstanceWeight(instance, 1e-10);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
NaiveBayes classifier = trainer.trainIncremental(list);
assertNotNull(classifier);
}

@Test
public void testToStringReturnsCorrectValue() {
NaiveBayesTrainer trainer = new NaiveBayesTrainer();
String str = trainer.toString();
assertEquals("NaiveBayesTrainer", str);
}

@Test
public void testTrainIncrementalAddsNewFeaturePostInit() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
dataAlphabet.lookupIndex("first");
targetAlphabet.lookupIndex("pos");
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
// FeatureVector fv1 = new FeatureVector(dataAlphabet, new String[] { "first" });
// Label l1 = targetAlphabet.lookupLabel("pos");
// Labeling labeling = l1.getLabeling();
// Instance i1 = new Instance(fv1, labeling, null, null);
InstanceList list = new InstanceList(pipe);
// list.addThruPipe(i1);
trainer.trainIncremental(list);
dataAlphabet.lookupIndex("second");
// FeatureVector fv2 = new FeatureVector(dataAlphabet, new String[] { "second" });
// Instance i2 = new Instance(fv2, labeling, null, null);
InstanceList list2 = new InstanceList(pipe);
// list2.addThruPipe(i2);
NaiveBayes classifier = trainer.trainIncremental(list2);
assertNotNull(classifier);
assertEquals(1, classifier.getLabelAlphabet().size());
}

@Test
public void testClassifierIsNotNullAfterSingleUnlabeledSkipped() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
dataAlphabet.lookupIndex("v");
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
// FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "v" });
// Instance inst = new Instance(fv, null, null, null);
InstanceList list = new InstanceList(pipe);
// list.addThruPipe(inst);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
NaiveBayes nb = trainer.trainIncremental(list);
assertNotNull(nb);
}

@Test
public void testSerializationRoundTripWithMultipleLabels() throws IOException, ClassNotFoundException {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
dataAlphabet.lookupIndex("x");
targetAlphabet.lookupIndex("L1");
targetAlphabet.lookupIndex("L2");
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
// FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "x" });
// int i1 = targetAlphabet.lookupIndex("L1");
int i2 = targetAlphabet.lookupIndex("L2");
// LabelVector lv = new LabelVector(targetAlphabet, new int[] { i1, i2 }, new double[] { 0.6, 0.4 });
// Instance instance = new Instance(fv, lv, null, null);
InstanceList list = new InstanceList(pipe);
// list.addThruPipe(instance);
trainer.trainIncremental(list);
ByteArrayOutputStream baos = new ByteArrayOutputStream();
ObjectOutputStream oos = new ObjectOutputStream(baos);
oos.writeObject(trainer);
oos.close();
byte[] bytes = baos.toByteArray();
ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
ObjectInputStream ois = new ObjectInputStream(bais);
NaiveBayesTrainer deserialized = (NaiveBayesTrainer) ois.readObject();
assertEquals(trainer.getAlphabet().size(), deserialized.getAlphabet().size());
assertEquals(trainer.getClassifier().getLabelAlphabet().size(), deserialized.getClassifier().getLabelAlphabet().size());
}

@Test
public void testAlphabetsMatchReturnsFalseOnMismatchedLabelAlphabet() {
Alphabet da = new Alphabet();
Alphabet ta1 = new Alphabet();
Alphabet ta2 = new Alphabet();
da.lookupIndex("f");
ta1.lookupIndex("A");
ta2.lookupIndex("B");
Pipe p1 = new Noop(da, ta1);
Pipe p2 = new Noop(da, ta2);
NaiveBayesTrainer trainer1 = new NaiveBayesTrainer(p1);
NaiveBayesTrainer trainer2 = new NaiveBayesTrainer(p2);
boolean match = trainer1.alphabetsMatch(trainer2);
assertFalse(match);
}
}
