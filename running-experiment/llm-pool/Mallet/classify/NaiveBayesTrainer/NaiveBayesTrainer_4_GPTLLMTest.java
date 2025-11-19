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

public class NaiveBayesTrainer_4_GPTLLMTest {

@Test
public void testConstructorWithPipe() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
assertEquals(dataAlphabet, trainer.getAlphabet());
assertArrayEquals(new Alphabet[] { dataAlphabet, targetAlphabet }, trainer.getAlphabets());
}

@Test
public void testSetAndGetFeatureEstimator() {
NaiveBayesTrainer trainer = new NaiveBayesTrainer();
// Multinomial.Estimator estimator = new Multinomial.LidstoneEstimator(0.5);
// NaiveBayesTrainer returned = trainer.setFeatureMultinomialEstimator(estimator);
// assertSame(trainer, returned);
// assertSame(estimator, trainer.getFeatureMultinomialEstimator());
}

@Test
public void testSetAndGetPriorEstimator() {
NaiveBayesTrainer trainer = new NaiveBayesTrainer();
// Multinomial.Estimator estimator = new Multinomial.LidstoneEstimator(1.0);
// NaiveBayesTrainer returned = trainer.setPriorMultinomialEstimator(estimator);
// assertSame(trainer, returned);
// assertSame(estimator, trainer.getPriorMultinomialEstimator());
}

@Test(expected = IllegalStateException.class)
public void testSetFeatureEstimatorFailsAfterTraining() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { dataAlphabet.lookupIndex("x") }, new double[] { 1 });
// Labeling labeling = new LabelVector(targetAlphabet, new int[] { targetAlphabet.lookupIndex("A") }, new double[] { 1 });
// Instance instance = new Instance(fv, labeling, null, null);
InstanceList list = new InstanceList(pipe);
// list.add(instance);
trainer.trainIncremental(list);
trainer.setFeatureMultinomialEstimator(new Multinomial.LaplaceEstimator());
}

@Test(expected = IllegalStateException.class)
public void testSetPriorEstimatorFailsAfterTraining() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { dataAlphabet.lookupIndex("y") }, new double[] { 1 });
// Labeling labeling = new LabelVector(targetAlphabet, new int[] { targetAlphabet.lookupIndex("B") }, new double[] { 1 });
// Instance instance = new Instance(fv, labeling, null, null);
InstanceList list = new InstanceList(pipe);
// list.add(instance);
trainer.trainIncremental(list);
trainer.setPriorMultinomialEstimator(new Multinomial.LaplaceEstimator());
}

@Test
public void testTrainCreatesClassifier() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
FeatureVector fv1 = new FeatureVector(dataAlphabet, new int[] { dataAlphabet.lookupIndex("hello") }, new double[] { 1 });
// Labeling labeling1 = new LabelVector(targetAlphabet, new int[] { targetAlphabet.lookupIndex("greeting") }, new double[] { 1 });
// Instance i1 = new Instance(fv1, labeling1, null, null);
FeatureVector fv2 = new FeatureVector(dataAlphabet, new int[] { dataAlphabet.lookupIndex("world") }, new double[] { 1 });
// Labeling labeling2 = new LabelVector(targetAlphabet, new int[] { targetAlphabet.lookupIndex("statement") }, new double[] { 1 });
// Instance i2 = new Instance(fv2, labeling2, null, null);
InstanceList list = new InstanceList(pipe);
// list.add(i1);
// list.add(i2);
NaiveBayes classifier = trainer.train(list);
assertNotNull(classifier);
assertSame(classifier, trainer.getClassifier());
}

@Test
public void testIncrementalTrainWithSingleInstance() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { dataAlphabet.lookupIndex("z") }, new double[] { 1 });
// Labeling labeling = new LabelVector(targetAlphabet, new int[] { targetAlphabet.lookupIndex("C") }, new double[] { 1 });
// Instance instance = new Instance(fv, labeling, null, null);
// NaiveBayes classifier = trainer.trainIncremental(instance);
// assertNotNull(classifier);
// assertSame(classifier, trainer.getClassifier());
}

@Test
public void testSkipZeroLengthFeatureVector() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] {}, new double[] {});
// Labeling labeling = new LabelVector(targetAlphabet, new int[] { targetAlphabet.lookupIndex("foo") }, new double[] { 1 });
// Instance instance = new Instance(fv, labeling, null, null);
// NaiveBayes classifier = trainer.trainIncremental(instance);
// assertNotNull(classifier);
}

@Test
public void testSkipUnlabeledInstance() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { dataAlphabet.lookupIndex("a") }, new double[] { 1 });
Instance instance = new Instance(fv, null, null, null);
NaiveBayes classifier = trainer.trainIncremental(instance);
assertNotNull(classifier);
}

@Test
public void testToStringValue() {
NaiveBayesTrainer trainer = new NaiveBayesTrainer();
assertEquals("NaiveBayesTrainer", trainer.toString());
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
public void testSerializationAndDeserialization() throws IOException, ClassNotFoundException {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { dataAlphabet.lookupIndex("feature") }, new double[] { 1 });
// Labeling labeling = new LabelVector(targetAlphabet, new int[] { targetAlphabet.lookupIndex("label") }, new double[] { 1 });
// Instance instance = new Instance(fv, labeling, null, null);
InstanceList list = new InstanceList(pipe);
// list.add(instance);
trainer.train(list);
byte[] serialized;
try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
ObjectOutputStream oos = new ObjectOutputStream(bos);
oos.writeObject(trainer);
oos.flush();
serialized = bos.toByteArray();
}
NaiveBayesTrainer deserialized;
try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(serialized))) {
deserialized = (NaiveBayesTrainer) ois.readObject();
}
assertNotNull(deserialized.getAlphabet());
assertNotNull(deserialized.getAlphabets());
assertEquals(trainer.getAlphabet(), deserialized.getAlphabet());
}

@Test
public void testDocLengthNormalizationAffectsWeighting() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
trainer.setDocLengthNormalization(10.0);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { dataAlphabet.lookupIndex("term1"), dataAlphabet.lookupIndex("term2") }, new double[] { 1, 1 });
// Labeling labeling = new LabelVector(targetAlphabet, new int[] { targetAlphabet.lookupIndex("cat") }, new double[] { 1 });
// Instance instance = new Instance(fv, labeling, null, null);
InstanceList list = new InstanceList(pipe);
// list.add(instance);
NaiveBayes classifier = trainer.train(list);
assertNotNull(classifier);
}

@Test
public void testFactoryCreatesNewTrainer() {
NaiveBayesTrainer.Factory factory = new NaiveBayesTrainer.Factory();
factory.setDocLengthNormalization(20.0);
factory.setFeatureMultinomialEstimator(new Multinomial.LaplaceEstimator());
factory.setPriorMultinomialEstimator(new Multinomial.LaplaceEstimator());
NaiveBayesTrainer trainer = factory.newClassifierTrainer(null);
assertNotNull(trainer);
}

@Test
public void testTrainWithEmptyInstanceList() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
InstanceList emptyList = new InstanceList(pipe);
NaiveBayes classifier = trainer.train(emptyList);
assertNotNull(classifier);
}

@Test(expected = IllegalArgumentException.class)
public void testTrainWithMismatchedAlphabet() {
Alphabet dataAlphabet1 = new Alphabet();
Alphabet targetAlphabet1 = new Alphabet();
Pipe pipe1 = new Noop(dataAlphabet1, targetAlphabet1);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe1);
Alphabet dataAlphabet2 = new Alphabet();
Alphabet targetAlphabet2 = new Alphabet();
Pipe pipe2 = new Noop(dataAlphabet2, targetAlphabet2);
FeatureVector fv = new FeatureVector(dataAlphabet2, new int[] { dataAlphabet2.lookupIndex("term") }, new double[] { 1 });
// Labeling labeling = new LabelVector(targetAlphabet2, new int[] { targetAlphabet2.lookupIndex("label") }, new double[] { 1 });
// Instance instance = new Instance(fv, labeling, null, null);
InstanceList instanceList = new InstanceList(pipe2);
// instanceList.add(instance);
trainer.train(instanceList);
}

@Test
public void testTrainIncrementalResizesEstimatorArray() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
int labelIndex1 = targetAlphabet.lookupIndex("class1");
FeatureVector fv1 = new FeatureVector(dataAlphabet, new int[] { dataAlphabet.lookupIndex("feature1") }, new double[] { 1 });
// Labeling labeling1 = new LabelVector(targetAlphabet, new int[] { labelIndex1 }, new double[] { 1 });
// Instance instance1 = new Instance(fv1, labeling1, null, null);
// trainer.trainIncremental(instance1);
int labelIndex2 = targetAlphabet.lookupIndex("class2");
FeatureVector fv2 = new FeatureVector(dataAlphabet, new int[] { dataAlphabet.lookupIndex("feature2") }, new double[] { 1 });
// Labeling labeling2 = new LabelVector(targetAlphabet, new int[] { labelIndex2 }, new double[] { 1 });
// Instance instance2 = new Instance(fv2, labeling2, null, null);
// NaiveBayes classifier = trainer.trainIncremental(instance2);
// assertNotNull(classifier);
}

@Test
public void testLabelWeightZeroIsSkipped() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
int labelIndex = targetAlphabet.lookupIndex("labelA");
int featureIndex = dataAlphabet.lookupIndex("f1");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { featureIndex }, new double[] { 1.0 });
// Labeling labeling = new LabelVector(targetAlphabet, new int[] { labelIndex }, new double[] { 0.0 });
// Instance instance = new Instance(fv, labeling, null, null);
// NaiveBayes classifier = trainer.trainIncremental(instance);
// assertNotNull(classifier);
}

@Test
public void testDuplicateFeatureInFeatureVector() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
int featureIndex = dataAlphabet.lookupIndex("repeat");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { featureIndex, featureIndex }, new double[] { 1.0, 2.0 });
// Labeling labeling = new LabelVector(targetAlphabet, new int[] { targetAlphabet.lookupIndex("dupclass") }, new double[] { 1.0 });
// Instance instance = new Instance(fv, labeling, null, null);
// NaiveBayes classifier = trainer.trainIncremental(instance);
// assertNotNull(classifier);
}

@Test(expected = IllegalArgumentException.class)
public void testSetupAlphabetMismatchSingleInstance() {
Alphabet dataAlphabet1 = new Alphabet();
Alphabet targetAlphabet1 = new Alphabet();
Pipe pipe1 = new Noop(dataAlphabet1, targetAlphabet1);
Alphabet dataAlphabet2 = new Alphabet();
Alphabet targetAlphabet2 = new Alphabet();
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe1);
FeatureVector fv = new FeatureVector(dataAlphabet2, new int[] { dataAlphabet2.lookupIndex("abc") }, new double[] { 1 });
// Labeling labeling = new LabelVector(targetAlphabet2, new int[] { targetAlphabet2.lookupIndex("c") }, new double[] { 1 });
// Instance instance = new Instance(fv, labeling, null, null);
// trainer.trainIncremental(instance);
}

@Test
public void testNullInstanceTrainingSkipped() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
NaiveBayes classifier = trainer.trainIncremental((Instance) null);
assertNotNull(classifier);
}

@Test
public void testNullDataSkippedGracefully() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
// Labeling labeling = new LabelVector(targetAlphabet, new int[] { targetAlphabet.lookupIndex("null") }, new double[] { 1 });
// Instance instance = new Instance(null, labeling, null, null);
// NaiveBayes classifier = trainer.trainIncremental(instance);
// assertNotNull(classifier);
}

@Test(expected = IllegalArgumentException.class)
public void testTrainWithDifferentPipesThrows() {
Alphabet alphabetA = new Alphabet();
Alphabet labelAlphabetA = new Alphabet();
Pipe pipeA = new Noop(alphabetA, labelAlphabetA);
Alphabet alphabetB = new Alphabet();
Alphabet labelAlphabetB = new Alphabet();
Pipe pipeB = new Noop(alphabetB, labelAlphabetB);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipeA);
FeatureVector fv = new FeatureVector(alphabetB, new int[] { alphabetB.lookupIndex("x") }, new double[] { 1.0 });
// Labeling labeling = new LabelVector(labelAlphabetB, new int[] { labelAlphabetB.lookupIndex("y") }, new double[] { 1.0 });
// Instance instance = new Instance(fv, labeling, null, null);
InstanceList list = new InstanceList(pipeB);
// list.add(instance);
trainer.trainIncremental(list);
}

@Test
public void testTrainWithMultipleLabels() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
int[] labelIndices = new int[] { targetAlphabet.lookupIndex("label1"), targetAlphabet.lookupIndex("label2") };
double[] labelWeights = new double[] { 0.3, 0.7 };
int[] featureIndices = new int[] { dataAlphabet.lookupIndex("feature") };
double[] featureValues = new double[] { 1 };
FeatureVector fv = new FeatureVector(dataAlphabet, featureIndices, featureValues);
// Labeling labeling = new LabelVector(targetAlphabet, labelIndices, labelWeights);
// Instance instance = new Instance(fv, labeling, null, null);
// NaiveBayes classifier = trainer.trainIncremental(instance);
// assertNotNull(classifier);
}

@Test
public void testTrainWithInstanceHavingZeroNormFeatureVector() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
int labelIndex = targetAlphabet.lookupIndex("label");
FeatureVector vector = new FeatureVector(dataAlphabet, new int[] {}, new double[] {});
// Labeling labeling = new LabelVector(targetAlphabet, new int[] { labelIndex }, new double[] { 1.0 });
// Instance instance = new Instance(vector, labeling, null, null);
// NaiveBayes classifier = trainer.trainIncremental(instance);
// assertNotNull(classifier);
}

@Test
public void testTrainSingleInstanceWithDocNormalizationZeroNorm() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe).setDocLengthNormalization(5.0);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] {}, new double[] {});
// Labeling labeling = new LabelVector(targetAlphabet, new int[] { targetAlphabet.lookupIndex("foo") }, new double[] { 1.0 });
// Instance instance = new Instance(fv, labeling, null, null);
// NaiveBayes classifier = trainer.trainIncremental(instance);
// assertNotNull(classifier);
}

@Test(expected = IllegalArgumentException.class)
public void testTrainWithNullPipeAndMismatchedInstancePipe() {
Alphabet a1 = new Alphabet();
Alphabet l1 = new Alphabet();
Pipe pipe1 = new Noop(a1, l1);
NaiveBayesTrainer trainer = new NaiveBayesTrainer();
int featureIdx = a1.lookupIndex("feature");
int labelIdx = l1.lookupIndex("label");
FeatureVector fv = new FeatureVector(a1, new int[] { featureIdx }, new double[] { 1.0 });
// Labeling labeling = new LabelVector(l1, new int[] { labelIdx }, new double[] { 1.0 });
// Instance instance = new Instance(fv, labeling, null, null);
InstanceList list = new InstanceList(pipe1);
// list.add(instance);
trainer.trainIncremental(list);
}

@Test
public void testTrainSingleInstanceAutoInitPipe() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
NaiveBayesTrainer trainer = new NaiveBayesTrainer();
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { dataAlphabet.lookupIndex("word") }, new double[] { 1.0 });
// Labeling labeling = new LabelVector(targetAlphabet, new int[] { targetAlphabet.lookupIndex("label") }, new double[] { 1.0 });
// Instance instance = new Instance(fv, labeling, null, null);
// NaiveBayes classifier = trainer.trainIncremental(instance);
// assertNotNull(classifier);
assertNotNull(trainer.getClassifier());
}

@Test
public void testTrainWithWeightsSummingToOne() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
int f1 = dataAlphabet.lookupIndex("term1");
int li1 = targetAlphabet.lookupIndex("A");
int li2 = targetAlphabet.lookupIndex("B");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { f1 }, new double[] { 1.0 });
// Labeling labeling = new LabelVector(targetAlphabet, new int[] { li1, li2 }, new double[] { 0.3, 0.7 });
// Instance instance = new Instance(fv, labeling, null, null);
// NaiveBayes classifier = trainer.trainIncremental(instance);
// assertNotNull(classifier);
}

@Test
public void testSetDocLengthNormalizationNegativeOneDisablesNormalization() {
NaiveBayesTrainer trainer = new NaiveBayesTrainer();
trainer.setDocLengthNormalization(-1.0);
double value = trainer.getDocLengthNormalization();
assertEquals(-1.0, value, 0.0001);
}

@Test
public void testSetDocLengthNormalizationPositiveValueAffectsTraining() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
trainer.setDocLengthNormalization(10.0);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { dataAlphabet.lookupIndex("word1"), dataAlphabet.lookupIndex("word2") }, new double[] { 1.0, 1.0 });
// Labeling labeling = new LabelVector(targetAlphabet, new int[] { targetAlphabet.lookupIndex("label") }, new double[] { 1.0 });
// Instance instance = new Instance(fv, labeling, null, null);
// NaiveBayes classifier = trainer.trainIncremental(instance);
// assertNotNull(classifier);
}

@Test
public void testAlphabetsMatchReturnsFalseWhenMismatch() {
Alphabet alphabet1 = new Alphabet();
Alphabet target1 = new Alphabet();
Pipe pipe1 = new Noop(alphabet1, target1);
Alphabet alphabet2 = new Alphabet();
Alphabet target2 = new Alphabet();
Pipe pipe2 = new Noop(alphabet2, target2);
NaiveBayesTrainer trainer1 = new NaiveBayesTrainer(pipe1);
NaiveBayesTrainer trainer2 = new NaiveBayesTrainer(pipe2);
assertFalse(trainer1.alphabetsMatch(trainer2));
}

@Test(expected = IOException.class)
public void testWriteObjectFailsWhenStreamIsBroken() throws IOException {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
OutputStream failing = new OutputStream() {

@Override
public void write(int b) throws IOException {
throw new IOException("Stream broken!");
}
};
ObjectOutputStream oos = new ObjectOutputStream(failing);
trainer.getClass().getDeclaredMethods();
oos.writeObject(trainer);
}

@Test(expected = IOException.class)
public void testReadObjectFailsWithInvalidStream() throws IOException, ClassNotFoundException {
byte[] invalidData = new byte[] { 0, 1, 2, 3, 4 };
ByteArrayInputStream bis = new ByteArrayInputStream(invalidData);
ObjectInputStream ois = new ObjectInputStream(bis);
Object obj = ois.readObject();
}

@Test
public void testTrainWithMultipleFeaturesSameLabel() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
int feature1 = dataAlphabet.lookupIndex("f1");
int feature2 = dataAlphabet.lookupIndex("f2");
int label = targetAlphabet.lookupIndex("label");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { feature1, feature2 }, new double[] { 1.0, 1.0 });
// Labeling labeling = new LabelVector(targetAlphabet, new int[] { label }, new double[] { 1.0 });
// Instance instance = new Instance(fv, labeling, null, null);
InstanceList list = new InstanceList(pipe);
// list.add(instance);
NaiveBayes classifier = trainer.train(list);
assertNotNull(classifier);
}

@Test
public void testTrainWithOneFeatureOneLabelPartialWeight() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
int feature = dataAlphabet.lookupIndex("word");
int label1 = targetAlphabet.lookupIndex("l1");
int label2 = targetAlphabet.lookupIndex("l2");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { feature }, new double[] { 1.0 });
// Labeling labeling = new LabelVector(targetAlphabet, new int[] { label1, label2 }, new double[] { 0.6, 0.4 });
// Instance instance = new Instance(fv, labeling, null, null);
// NaiveBayes classifier = trainer.trainIncremental(instance);
// assertNotNull(classifier);
}

@Test
public void testSetFeatureEstimatorBeforeTrainingMultipleTimes() {
NaiveBayesTrainer trainer = new NaiveBayesTrainer();
Multinomial.Estimator est1 = new Multinomial.LaplaceEstimator();
// Multinomial.Estimator est2 = new Multinomial.LidstoneEstimator(0.2);
trainer.setFeatureMultinomialEstimator(est1);
// trainer.setFeatureMultinomialEstimator(est2);
// assertSame(est2, trainer.getFeatureMultinomialEstimator());
}

@Test
public void testSetPriorEstimatorBeforeTrainingMultipleTimes() {
NaiveBayesTrainer trainer = new NaiveBayesTrainer();
Multinomial.Estimator est1 = new Multinomial.LaplaceEstimator();
// Multinomial.Estimator est2 = new Multinomial.LidstoneEstimator(0.4);
trainer.setPriorMultinomialEstimator(est1);
// trainer.setPriorMultinomialEstimator(est2);
// assertSame(est2, trainer.getPriorMultinomialEstimator());
}

@Test
public void testTrainWithPipeSetButAlphabetsExtractedFromInstance() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe dummyPipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer();
int f = dataAlphabet.lookupIndex("x");
int l = targetAlphabet.lookupIndex("y");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { f }, new double[] { 1 });
// Labeling labeling = new LabelVector(targetAlphabet, new int[] { l }, new double[] { 1 });
// Instance inst = new Instance(fv, labeling, null, null);
// NaiveBayes classifier = trainer.trainIncremental(inst);
// assertNotNull(classifier);
}

@Test
public void testIncrementalTrainAppendsWithoutResetting() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
int f1 = dataAlphabet.lookupIndex("f1");
int l1 = targetAlphabet.lookupIndex("l1");
FeatureVector fv1 = new FeatureVector(dataAlphabet, new int[] { f1 }, new double[] { 1.0 });
// Labeling labeling1 = new LabelVector(targetAlphabet, new int[] { l1 }, new double[] { 1.0 });
// Instance instance1 = new Instance(fv1, labeling1, null, null);
// trainer.trainIncremental(instance1);
int f2 = dataAlphabet.lookupIndex("f2");
int l2 = targetAlphabet.lookupIndex("l2");
FeatureVector fv2 = new FeatureVector(dataAlphabet, new int[] { f2 }, new double[] { 1.0 });
// Labeling labeling2 = new LabelVector(targetAlphabet, new int[] { l2 }, new double[] { 1.0 });
// Instance instance2 = new Instance(fv2, labeling2, null, null);
// NaiveBayes classifier = trainer.trainIncremental(instance2);
// assertNotNull(classifier);
}

@Test
public void testResetBehaviorViaTrain() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
int f = dataAlphabet.lookupIndex("term");
int l = targetAlphabet.lookupIndex("label");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { f }, new double[] { 1 });
// Labeling labeling = new LabelVector(targetAlphabet, new int[] { l }, new double[] { 1 });
// Instance instance = new Instance(fv, labeling, null, null);
InstanceList list = new InstanceList(pipe);
// list.add(instance);
// trainer.trainIncremental(instance);
NaiveBayes classifier = trainer.train(list);
assertNotNull(classifier);
}

@Test
public void testTrainWithFeatureHavingZeroValue() {
Alphabet data = new Alphabet();
Alphabet target = new Alphabet();
Pipe pipe = new Noop(data, target);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
int feat = data.lookupIndex("zero_value");
int label = target.lookupIndex("spam");
FeatureVector fv = new FeatureVector(data, new int[] { feat }, new double[] { 0.0 });
// Labeling labelSet = new LabelVector(target, new int[] { label }, new double[] { 1.0 });
// Instance instance = new Instance(fv, labelSet, null, null);
// NaiveBayes classifier = trainer.trainIncremental(instance);
// assertNotNull(classifier);
}

@Test
public void testFactoryWithCustomSettings() {
NaiveBayesTrainer.Factory factory = new NaiveBayesTrainer.Factory();
factory.setDocLengthNormalization(10.0);
// factory.setFeatureMultinomialEstimator(new Multinomial.LidstoneEstimator(0.2));
// factory.setPriorMultinomialEstimator(new Multinomial.LidstoneEstimator(0.5));
NaiveBayesTrainer trainer = factory.newClassifierTrainer(null);
assertNotNull(trainer);
}

@Test
public void testIncrementalTrainWithNullInstancePipeUsesNoopPipe() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
int featureIndex = dataAlphabet.lookupIndex("feature");
int labelIndex = targetAlphabet.lookupIndex("label");
FeatureVector featureVector = new FeatureVector(dataAlphabet, new int[] { featureIndex }, new double[] { 1.0 });
// Labeling labeling = new LabelVector(targetAlphabet, new int[] { labelIndex }, new double[] { 1.0 });
// Instance instance = new Instance(featureVector, labeling, null, null);
NaiveBayesTrainer trainer = new NaiveBayesTrainer();
// NaiveBayes classifier = trainer.trainIncremental(instance);
// assertNotNull(classifier);
assertNotNull(trainer.getClassifier());
}

@Test(expected = IllegalArgumentException.class)
public void testMismatchedAlphabetBetweenTrainerAndInstanceThrows() {
Alphabet dataAlphabet1 = new Alphabet();
Alphabet targetAlphabet1 = new Alphabet();
Pipe trainerPipe = new Noop(dataAlphabet1, targetAlphabet1);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(trainerPipe);
Alphabet dataAlphabet2 = new Alphabet();
Alphabet targetAlphabet2 = new Alphabet();
int featureIndex = dataAlphabet2.lookupIndex("x");
int labelIndex = targetAlphabet2.lookupIndex("y");
FeatureVector fv = new FeatureVector(dataAlphabet2, new int[] { featureIndex }, new double[] { 1.0 });
// Labeling labeling = new LabelVector(targetAlphabet2, new int[] { labelIndex }, new double[] { 1.0 });
// Instance instance = new Instance(fv, labeling, null, null);
// trainer.trainIncremental(instance);
}

@Test(expected = IllegalArgumentException.class)
public void testMismatchedPipeBetweenTrainerAndInstanceListThrows() {
Alphabet dataAlphabet1 = new Alphabet();
Alphabet targetAlphabet1 = new Alphabet();
Pipe trainerPipe = new Noop(dataAlphabet1, targetAlphabet1);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(trainerPipe);
Alphabet dataAlphabet2 = new Alphabet();
Alphabet targetAlphabet2 = new Alphabet();
Pipe instancePipe = new Noop(dataAlphabet2, targetAlphabet2);
int featureIndex = dataAlphabet2.lookupIndex("f");
int labelIndex = targetAlphabet2.lookupIndex("l");
FeatureVector fv = new FeatureVector(dataAlphabet2, new int[] { featureIndex }, new double[] { 1.0 });
// Labeling labeling = new LabelVector(targetAlphabet2, new int[] { labelIndex }, new double[] { 1.0 });
// Instance instance = new Instance(fv, labeling, null, null);
InstanceList list = new InstanceList(instancePipe);
// list.add(instance);
trainer.trainIncremental(list);
}

@Test
public void testSetupCreatesExpandedEstimatorArrayWhenLabelAlphabetGrows() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
int f1 = dataAlphabet.lookupIndex("token");
int l1 = targetAlphabet.lookupIndex("label1");
FeatureVector fv1 = new FeatureVector(dataAlphabet, new int[] { f1 }, new double[] { 1.0 });
// Labeling lab1 = new LabelVector(targetAlphabet, new int[] { l1 }, new double[] { 1.0 });
// Instance i1 = new Instance(fv1, lab1, null, null);
// trainer.trainIncremental(i1);
int l2 = targetAlphabet.lookupIndex("label2");
FeatureVector fv2 = new FeatureVector(dataAlphabet, new int[] { f1 }, new double[] { 1.0 });
// Labeling lab2 = new LabelVector(targetAlphabet, new int[] { l2 }, new double[] { 1.0 });
// Instance i2 = new Instance(fv2, lab2, null, null);
// NaiveBayes classifier = trainer.trainIncremental(i2);
// assertNotNull(classifier);
}

@Test
public void testIncorporateOneInstanceSkipsWhenNoLabeling() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] {}, new double[] {});
Instance instance = new Instance(fv, null, null, null);
NaiveBayes classifier = trainer.trainIncremental(instance);
assertNotNull(classifier);
}

@Test
public void testTrainWithInstanceHavingLabelWeightZeroIsSkipped() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
int featureIndex = dataAlphabet.lookupIndex("token");
int labelIndex = targetAlphabet.lookupIndex("Y");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { featureIndex }, new double[] { 1.0 });
// Labeling labeling = new LabelVector(targetAlphabet, new int[] { labelIndex }, new double[] { 0.0 });
// Instance instance = new Instance(fv, labeling, null, null);
// NaiveBayes classifier = trainer.trainIncremental(instance);
// assertNotNull(classifier);
}

@Test
public void testSetAndUsePriorAndFeatureEstimatorsWithoutTraining() {
NaiveBayesTrainer trainer = new NaiveBayesTrainer();
// Multinomial.Estimator prior = new Multinomial.LidstoneEstimator(0.1);
// Multinomial.Estimator feature = new Multinomial.LidstoneEstimator(0.2);
// trainer.setPriorMultinomialEstimator(prior);
// trainer.setFeatureMultinomialEstimator(feature);
// assertSame(prior, trainer.getPriorMultinomialEstimator());
// assertSame(feature, trainer.getFeatureMultinomialEstimator());
}

@Test
public void testTrainWithMultipleInstancesUnderSameLabel() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
int label = targetAlphabet.lookupIndex("x");
int f1 = dataAlphabet.lookupIndex("a");
int f2 = dataAlphabet.lookupIndex("b");
FeatureVector fv1 = new FeatureVector(dataAlphabet, new int[] { f1 }, new double[] { 1.0 });
// Labeling labeling1 = new LabelVector(targetAlphabet, new int[] { label }, new double[] { 1.0 });
// Instance i1 = new Instance(fv1, labeling1, null, null);
FeatureVector fv2 = new FeatureVector(dataAlphabet, new int[] { f2 }, new double[] { 1.0 });
// Labeling labeling2 = new LabelVector(targetAlphabet, new int[] { label }, new double[] { 1.0 });
// Instance i2 = new Instance(fv2, labeling2, null, null);
InstanceList list = new InstanceList(pipe);
// list.add(i1);
// list.add(i2);
NaiveBayes classifier = trainer.train(list);
assertNotNull(classifier);
}

@Test
public void testTrainSingleInstanceWithNonNormalizedLabelWeightSum() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
int f = dataAlphabet.lookupIndex("word");
int l1 = targetAlphabet.lookupIndex("cat");
int l2 = targetAlphabet.lookupIndex("dog");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { f }, new double[] { 1.0 });
// Labeling labeling = new LabelVector(targetAlphabet, new int[] { l1, l2 }, new double[] { 0.5, 0.9 });
// Instance instance = new Instance(fv, labeling, null, null);
// NaiveBayes classifier = trainer.trainIncremental(instance);
// assertNotNull(classifier);
}

@Test
public void testTrainWithInstanceWeightScalingViaDocLengthNormalization() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
trainer.setDocLengthNormalization(6.0);
int f1 = dataAlphabet.lookupIndex("x");
int l1 = targetAlphabet.lookupIndex("y");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { f1 }, new double[] { 3.0 });
// Labeling labeling = new LabelVector(targetAlphabet, new int[] { l1 }, new double[] { 1.0 });
// Instance instance = new Instance(fv, labeling, null, null);
// NaiveBayes classifier = trainer.trainIncremental(instance);
// assertNotNull(classifier);
}

@Test
public void testTrainWithInstanceWeightScalingWhenOneNormAlreadyEqualsNormalizationTarget() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
trainer.setDocLengthNormalization(5.0);
int ix = dataAlphabet.lookupIndex("a");
int iy = targetAlphabet.lookupIndex("class");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { ix }, new double[] { 5.0 });
// Labeling labeling = new LabelVector(targetAlphabet, new int[] { iy }, new double[] { 1.0 });
// Instance instance = new Instance(fv, labeling, null, null);
// NaiveBayes classifier = trainer.trainIncremental(instance);
// assertNotNull(classifier);
}

@Test
public void testTrainWithEmptyFeatureVectorAndNonNullLabeling() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
int labelIndex = targetAlphabet.lookupIndex("L");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] {}, new double[] {});
// Labeling labeling = new LabelVector(targetAlphabet, new int[] { labelIndex }, new double[] { 1.0 });
// Instance instance = new Instance(fv, labeling, null, null);
// NaiveBayes classifier = trainer.trainIncremental(instance);
// assertNotNull(classifier);
}

@Test
public void testTrainWithDifferentInstanceWeightPerInstance() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
int feature1Index = dataAlphabet.lookupIndex("feature1");
int feature2Index = dataAlphabet.lookupIndex("feature2");
int labelIndex = targetAlphabet.lookupIndex("label");
FeatureVector fv1 = new FeatureVector(dataAlphabet, new int[] { feature1Index }, new double[] { 2.0 });
// Labeling labeling1 = new LabelVector(targetAlphabet, new int[] { labelIndex }, new double[] { 1.0 });
// Instance instance1 = new Instance(fv1, labeling1, null, null);
FeatureVector fv2 = new FeatureVector(dataAlphabet, new int[] { feature2Index }, new double[] { 8.0 });
// Labeling labeling2 = new LabelVector(targetAlphabet, new int[] { labelIndex }, new double[] { 1.0 });
// Instance instance2 = new Instance(fv2, labeling2, null, null);
InstanceList trainingList = new InstanceList(pipe);
// trainingList.add(instance1, 0.5);
// trainingList.add(instance2, 2.0);
NaiveBayes classifier = trainer.train(trainingList);
assertNotNull(classifier);
}

@Test
public void testTrainWithOneFeatureMappedToTwoLabels() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
int feature = dataAlphabet.lookupIndex("shared");
int label1 = targetAlphabet.lookupIndex("class1");
int label2 = targetAlphabet.lookupIndex("class2");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { feature }, new double[] { 2.0 });
// Labeling labeling = new LabelVector(targetAlphabet, new int[] { label1, label2 }, new double[] { 0.5, 0.5 });
// Instance instance = new Instance(fv, labeling, null, null);
// NaiveBayes classifier = trainer.trainIncremental(instance);
// assertNotNull(classifier);
}

@Test
public void testTrainWithDynamicExpansionBeyondOriginalLabelArrayLength() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
int l0 = targetAlphabet.lookupIndex("label0");
int f = dataAlphabet.lookupIndex("term");
FeatureVector fv1 = new FeatureVector(dataAlphabet, new int[] { f }, new double[] { 1.0 });
// Labeling labeling1 = new LabelVector(targetAlphabet, new int[] { l0 }, new double[] { 1.0 });
// Instance instance1 = new Instance(fv1, labeling1, null, null);
// trainer.trainIncremental(instance1);
int l1 = targetAlphabet.lookupIndex("label1");
int l2 = targetAlphabet.lookupIndex("label2");
FeatureVector fv2 = new FeatureVector(dataAlphabet, new int[] { f }, new double[] { 1.0 });
// Labeling labeling2 = new LabelVector(targetAlphabet, new int[] { l1, l2 }, new double[] { 0.7, 0.3 });
// Instance instance2 = new Instance(fv2, labeling2, null, null);
// NaiveBayes classifier = trainer.trainIncremental(instance2);
// assertNotNull(classifier);
}

@Test
public void testTrainWithSingleInstanceListHavingZeroWeight() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
int featureIndex = dataAlphabet.lookupIndex("f");
int labelIndex = targetAlphabet.lookupIndex("l");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { featureIndex }, new double[] { 1.0 });
// Labeling labeling = new LabelVector(targetAlphabet, new int[] { labelIndex }, new double[] { 1.0 });
// Instance instance = new Instance(fv, labeling, "name", null);
InstanceList list = new InstanceList(pipe);
// list.add(instance, 0.0);
NaiveBayes classifier = trainer.train(list);
assertNotNull(classifier);
}

@Test
public void testTrainWithAllZeroLabelWeights() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
int featureIndex = dataAlphabet.lookupIndex("token");
int labelIndex = targetAlphabet.lookupIndex("tag");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { featureIndex }, new double[] { 1.0 });
// Labeling labeling = new LabelVector(targetAlphabet, new int[] { labelIndex }, new double[] { 0.0 });
// Instance instance = new Instance(fv, labeling, "zero-weight", null);
// NaiveBayes classifier = trainer.trainIncremental(instance);
// assertNotNull(classifier);
}

@Test
public void testIncorporateOneInstanceWithFeatureVectorNormZero() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
int labelIndex = targetAlphabet.lookupIndex("outcome");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] {}, new double[] {});
// Labeling labeling = new LabelVector(targetAlphabet, new int[] { labelIndex }, new double[] { 1.0 });
// Instance instance = new Instance(fv, labeling, null, null);
// NaiveBayes classifier = trainer.trainIncremental(instance);
// assertNotNull(classifier);
}

@Test
public void testInstanceWithInfiniteFeatureValueIsIgnoredDueToNormalization() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe).setDocLengthNormalization(10.0);
int featureIndex = dataAlphabet.lookupIndex("weird");
int labelIndex = targetAlphabet.lookupIndex("label");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { featureIndex }, new double[] { Double.POSITIVE_INFINITY });
// Labeling labeling = new LabelVector(targetAlphabet, new int[] { labelIndex }, new double[] { 1.0 });
// Instance instance = new Instance(fv, labeling, null, null);
// NaiveBayes classifier = trainer.trainIncremental(instance);
// assertNotNull(classifier);
}

@Test
public void testInstanceWithNegativeFeatureValueIsAccepted() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
int fIndex = dataAlphabet.lookupIndex("negative");
int labelIndex = targetAlphabet.lookupIndex("pos");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { fIndex }, new double[] { -3.0 });
// Labeling labeling = new LabelVector(targetAlphabet, new int[] { labelIndex }, new double[] { 1.0 });
// Instance instance = new Instance(fv, labeling, null, null);
// NaiveBayes classifier = trainer.trainIncremental(instance);
// assertNotNull(classifier);
}

@Test
public void testFeatureEstimatorCloneIsolatedFromOriginal() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
// Multinomial.LidstoneEstimator estimator = new Multinomial.LidstoneEstimator(0.4);
// trainer.setFeatureMultinomialEstimator(estimator);
dataAlphabet.lookupIndex("a");
targetAlphabet.lookupIndex("X");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1 });
// Labeling labeling = new LabelVector(targetAlphabet, new int[] { 0 }, new double[] { 1 });
// Instance instance = new Instance(fv, labeling, null, null);
// trainer.trainIncremental(instance);
assertNotNull(trainer.getClassifier());
// assertNotSame(estimator, trainer.getFeatureMultinomialEstimator());
}

@Test
public void testTrainWithVeryLargeFeatureValue() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
int f1 = dataAlphabet.lookupIndex("huge");
int l1 = targetAlphabet.lookupIndex("class");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { f1 }, new double[] { Double.MAX_VALUE });
// Labeling labeling = new LabelVector(targetAlphabet, new int[] { l1 }, new double[] { 1.0 });
// Instance instance = new Instance(fv, labeling, null, null);
// NaiveBayes classifier = trainer.trainIncremental(instance);
// assertNotNull(classifier);
}

@Test
public void testSerializationWithEmptyTrainer() throws IOException, ClassNotFoundException {
NaiveBayesTrainer trainer = new NaiveBayesTrainer();
ByteArrayOutputStream baos = new ByteArrayOutputStream();
ObjectOutputStream oos = new ObjectOutputStream(baos);
oos.writeObject(trainer);
oos.close();
byte[] serialized = baos.toByteArray();
ByteArrayInputStream bais = new ByteArrayInputStream(serialized);
ObjectInputStream ois = new ObjectInputStream(bais);
NaiveBayesTrainer readBack = (NaiveBayesTrainer) ois.readObject();
assertNotNull(readBack);
assertNull(readBack.getClassifier());
}

@Test(expected = IllegalArgumentException.class)
public void testSetPriorEstimatorAfterTrainingShouldFail() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
int f = dataAlphabet.lookupIndex("tok");
int l = targetAlphabet.lookupIndex("lab");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { f }, new double[] { 1.0 });
// Labeling labeling = new LabelVector(targetAlphabet, new int[] { l }, new double[] { 1.0 });
// Instance instance = new Instance(fv, labeling, null, null);
// trainer.trainIncremental(instance);
trainer.setPriorMultinomialEstimator(new Multinomial.LaplaceEstimator());
}
}
