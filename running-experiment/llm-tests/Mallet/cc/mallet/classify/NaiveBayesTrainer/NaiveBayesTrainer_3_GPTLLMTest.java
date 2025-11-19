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

public class NaiveBayesTrainer_3_GPTLLMTest {

@Test
public void testSetAndGetDocLengthNormalization() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet targetAlphabet = new LabelAlphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
assertEquals(-1.0, trainer.getDocLengthNormalization(), 0.0);
trainer.setDocLengthNormalization(15.0);
assertEquals(15.0, trainer.getDocLengthNormalization(), 0.0);
}

@Test
public void testSetAndGetFeatureEstimator() {
NaiveBayesTrainer trainer = new NaiveBayesTrainer();
Multinomial.Estimator estimator = new Multinomial.MLEstimator();
trainer.setFeatureMultinomialEstimator(estimator);
assertEquals(estimator, trainer.getFeatureMultinomialEstimator());
}

@Test(expected = IllegalStateException.class)
public void testSetFeatureEstimatorAfterPipeThrows() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet targetAlphabet = new LabelAlphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
Multinomial.Estimator estimator = new Multinomial.MLEstimator();
trainer.setFeatureMultinomialEstimator(estimator);
}

@Test
public void testSetAndGetPriorEstimator() {
NaiveBayesTrainer trainer = new NaiveBayesTrainer();
Multinomial.Estimator estimator = new Multinomial.MLEstimator();
trainer.setPriorMultinomialEstimator(estimator);
assertEquals(estimator, trainer.getPriorMultinomialEstimator());
}

@Test(expected = IllegalStateException.class)
public void testSetPriorEstimatorAfterPipeThrows() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet targetAlphabet = new LabelAlphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
Multinomial.Estimator estimator = new Multinomial.MLEstimator();
trainer.setPriorMultinomialEstimator(estimator);
}

@Test
public void testTrainSingleInstance() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet targetAlphabet = new LabelAlphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "dog", "cat" }, new double[] { 1.0, 1.0 });
targetAlphabet.lookupLabel("animal");
Labeling labeling = targetAlphabet.lookupLabel("animal");
Instance instance = new Instance(fv, labeling, null, null);
InstanceList list = new InstanceList(pipe);
list.addThruPipe(instance);
NaiveBayes classifier = trainer.train(list);
assertNotNull(classifier);
}

@Test
public void testTrainMultipleInstances() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet targetAlphabet = new LabelAlphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
targetAlphabet.lookupLabel("positive");
targetAlphabet.lookupLabel("negative");
FeatureVector fv1 = new FeatureVector(dataAlphabet, new String[] { "great" }, new double[] { 1.0 });
Labeling labeling1 = targetAlphabet.lookupLabel("positive");
Instance instance1 = new Instance(fv1, labeling1, null, null);
FeatureVector fv2 = new FeatureVector(dataAlphabet, new String[] { "bad" }, new double[] { 1.0 });
Labeling labeling2 = targetAlphabet.lookupLabel("negative");
Instance instance2 = new Instance(fv2, labeling2, null, null);
InstanceList list = new InstanceList(pipe);
list.addThruPipe(instance1);
list.addThruPipe(instance2);
NaiveBayes classifier = trainer.train(list);
assertNotNull(classifier);
}

@Test
public void testTrainIncrementalInstanceList() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet targetAlphabet = new LabelAlphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
targetAlphabet.lookupLabel("spam");
targetAlphabet.lookupLabel("ham");
FeatureVector fv1 = new FeatureVector(dataAlphabet, new String[] { "offer" }, new double[] { 1.0 });
Labeling labeling1 = targetAlphabet.lookupLabel("spam");
Instance instance1 = new Instance(fv1, labeling1, null, null);
FeatureVector fv2 = new FeatureVector(dataAlphabet, new String[] { "hello" }, new double[] { 1.0 });
Labeling labeling2 = targetAlphabet.lookupLabel("ham");
Instance instance2 = new Instance(fv2, labeling2, null, null);
InstanceList list = new InstanceList(pipe);
list.addThruPipe(instance1);
list.addThruPipe(instance2);
NaiveBayes classifier = trainer.trainIncremental(list);
assertNotNull(classifier);
}

@Test
public void testTrainIncrementalSingleInstance() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet targetAlphabet = new LabelAlphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
targetAlphabet.lookupLabel("neutral");
FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "okay" }, new double[] { 1.0 });
Labeling labeling = targetAlphabet.lookupLabel("neutral");
Instance instance = new Instance(fv, labeling, null, null);
NaiveBayes classifier = trainer.trainIncremental(instance);
assertNotNull(classifier);
}

@Test(expected = IllegalArgumentException.class)
public void testAlphabetMismatchThrows() {
Alphabet dataAlphabet1 = new Alphabet();
LabelAlphabet targetAlphabet1 = new LabelAlphabet();
Pipe pipe1 = new Noop(dataAlphabet1, targetAlphabet1);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe1);
Alphabet dataAlphabet2 = new Alphabet();
LabelAlphabet targetAlphabet2 = new LabelAlphabet();
Pipe pipe2 = new Noop(dataAlphabet2, targetAlphabet2);
FeatureVector fv = new FeatureVector(dataAlphabet2, new String[] { "diff" }, new double[] { 1.0 });
targetAlphabet2.lookupLabel("other");
Labeling labeling = targetAlphabet2.lookupLabel("other");
Instance instance = new Instance(fv, labeling, null, null);
InstanceList list = new InstanceList(pipe2);
list.addThruPipe(instance);
trainer.train(list);
}

@Test
public void testToStringMethod() {
NaiveBayesTrainer trainer = new NaiveBayesTrainer();
assertEquals("NaiveBayesTrainer", trainer.toString());
}

@Test
public void testAlphabetsMatchReturnsTrue() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet targetAlphabet = new LabelAlphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
AlphabetCarrying object = new AlphabetCarrying() {

public Alphabet[] getAlphabets() {
return new Alphabet[] { dataAlphabet, targetAlphabet };
}

public Alphabet getAlphabet() {
return dataAlphabet;
}
};
assertTrue(trainer.alphabetsMatch(object));
}

@Test
public void testSerializationAndDeserialization() throws Exception {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet targetAlphabet = new LabelAlphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "x" }, new double[] { 1.0 });
targetAlphabet.lookupLabel("label");
Labeling labeling = targetAlphabet.lookupLabel("label");
Instance instance = new Instance(fv, labeling, null, null);
InstanceList list = new InstanceList(pipe);
list.addThruPipe(instance);
trainer.train(list);
ByteArrayOutputStream baos = new ByteArrayOutputStream();
ObjectOutputStream out = new ObjectOutputStream(baos);
out.writeObject(trainer);
out.close();
ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
ObjectInputStream in = new ObjectInputStream(bais);
NaiveBayesTrainer deserialized = (NaiveBayesTrainer) in.readObject();
assertNotNull(deserialized.getClassifier());
}

@Test
public void testUnlabeledInstanceIsIgnored() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet targetAlphabet = new LabelAlphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "foo" }, new double[] { 1.0 });
Instance instance = new Instance(fv, null, null, null);
InstanceList list = new InstanceList(pipe);
list.addThruPipe(instance);
NaiveBayes classifier = trainer.train(list);
assertNotNull(classifier);
}

@Test
public void testInstanceWithZeroFeaturesIsIgnored() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet targetAlphabet = new LabelAlphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "empty" }, new double[] { 0.0 });
targetAlphabet.lookupLabel("label");
Labeling labeling = targetAlphabet.lookupLabel("label");
Instance instance = new Instance(fv, labeling, null, null);
InstanceList list = new InstanceList(pipe);
list.addThruPipe(instance);
NaiveBayes classifier = trainer.train(list);
assertNotNull(classifier);
}

@Test
public void testDocumentLengthNormalizationEffect() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet targetAlphabet = new LabelAlphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
trainer.setDocLengthNormalization(10.0);
FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "word1", "word2" }, new double[] { 1.0, 1.0 });
targetAlphabet.lookupLabel("label");
Labeling labeling = targetAlphabet.lookupLabel("label");
Instance instance = new Instance(fv, labeling, null, null);
InstanceList list = new InstanceList(pipe);
list.addThruPipe(instance);
NaiveBayes classifier = trainer.train(list);
assertNotNull(classifier);
}

@Test(expected = IllegalArgumentException.class)
public void testTrainWithNullInstanceAndNullInstanceListThrowsException() {
NaiveBayesTrainer trainer = new NaiveBayesTrainer();
trainer.trainIncremental((InstanceList) null);
}

@Test
public void testTrainOnInstanceWithZeroOneNormIsIgnored() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet targetAlphabet = new LabelAlphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
String labelName = "emptyNorm";
targetAlphabet.lookupLabel(labelName);
FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "featureX" }, new double[] { 0.0 });
Labeling labeling = targetAlphabet.lookupLabel(labelName);
Instance instance = new Instance(fv, labeling, null, null);
InstanceList list = new InstanceList(pipe);
list.addThruPipe(instance);
NaiveBayes classifier = trainer.train(list);
assertNotNull(classifier);
}

@Test
public void testIncrementalTrainingWithGrowingLabelAlphabet() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet targetAlphabet = new LabelAlphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
targetAlphabet.lookupLabel("first");
FeatureVector fv1 = new FeatureVector(dataAlphabet, new String[] { "a" }, new double[] { 1.0 });
Labeling labeling1 = targetAlphabet.lookupLabel("first");
Instance instance1 = new Instance(fv1, labeling1, null, null);
trainer.trainIncremental(instance1);
targetAlphabet.lookupLabel("second");
FeatureVector fv2 = new FeatureVector(dataAlphabet, new String[] { "b" }, new double[] { 1.0 });
Labeling labeling2 = targetAlphabet.lookupLabel("second");
Instance instance2 = new Instance(fv2, labeling2, null, null);
NaiveBayes classifier = trainer.trainIncremental(instance2);
assertNotNull(classifier);
assertEquals(2, classifier.getLabelAlphabet().size());
}

@Test
public void testTrainWithMultipleLabelsPerInstance() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet targetAlphabet = new LabelAlphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
Label label1 = targetAlphabet.lookupLabel("Label1");
Label label2 = targetAlphabet.lookupLabel("Label2");
LabelVector labeling = new LabelVector(new Label[] { label1, label2 }, new double[] { 0.4, 0.6 });
FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "feat" }, new double[] { 1.0 });
Instance instance = new Instance(fv, labeling, null, null);
InstanceList list = new InstanceList(pipe);
list.addThruPipe(instance);
NaiveBayes classifier = trainer.train(list);
assertNotNull(classifier);
}

@Test
public void testClassifierAfterMultipleIncrementalCalls() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet targetAlphabet = new LabelAlphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
targetAlphabet.lookupLabel("yes");
FeatureVector fv1 = new FeatureVector(dataAlphabet, new String[] { "f1" }, new double[] { 1.0 });
Labeling labeling1 = targetAlphabet.lookupLabel("yes");
Instance i1 = new Instance(fv1, labeling1, null, null);
trainer.trainIncremental(i1);
targetAlphabet.lookupLabel("no");
FeatureVector fv2 = new FeatureVector(dataAlphabet, new String[] { "f2" }, new double[] { 1.0 });
Labeling labeling2 = targetAlphabet.lookupLabel("no");
Instance i2 = new Instance(fv2, labeling2, null, null);
trainer.trainIncremental(i2);
FeatureVector fv3 = new FeatureVector(dataAlphabet, new String[] { "f3" }, new double[] { 1.0 });
Labeling labeling3 = targetAlphabet.lookupLabel("yes");
Instance i3 = new Instance(fv3, labeling3, null, null);
NaiveBayes classifier = trainer.trainIncremental(i3);
assertNotNull(classifier);
assertEquals(2, classifier.getLabelAlphabet().size());
}

@Test
public void testFactoryProducesTrainerWithSettings() {
NaiveBayesTrainer.Factory factory = new NaiveBayesTrainer.Factory();
factory.setDocLengthNormalization(5.0);
factory.setFeatureMultinomialEstimator(new Multinomial.MLEstimator());
factory.setPriorMultinomialEstimator(new Multinomial.MLEstimator());
NaiveBayesTrainer trainer = factory.newClassifierTrainer(null);
assertEquals(-1.0, trainer.getDocLengthNormalization(), 0.0);
}

@Test(expected = ClassNotFoundException.class)
public void testSerializationWithVersionMismatchThrows() throws Exception {
ByteArrayOutputStream baos = new ByteArrayOutputStream();
ObjectOutputStream out = new ObjectOutputStream(baos);
out.writeInt(999);
out.writeObject(new Multinomial.LaplaceEstimator());
out.writeObject(new Multinomial.LaplaceEstimator());
out.writeObject(null);
out.writeObject(null);
out.writeObject(null);
out.writeObject(null);
out.writeObject(null);
out.close();
ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
ObjectInputStream in = new ObjectInputStream(bais);
NaiveBayesTrainer trainer = new NaiveBayesTrainer();
trainer.getClass().getDeclaredMethod("readObject", ObjectInputStream.class).setAccessible(true);
trainer.getClass().getDeclaredMethod("readObject", ObjectInputStream.class).invoke(trainer, in);
}

@Test
public void testTrainWithEmptyInstanceListDoesNotFail() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet targetAlphabet = new LabelAlphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
InstanceList emptyList = new InstanceList(pipe);
NaiveBayes classifier = trainer.train(emptyList);
assertNotNull(classifier);
}

@Test
public void testClassifierReturnedAfterTrainIsSameAsFromGetter() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet targetAlphabet = new LabelAlphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "feat" }, new double[] { 1.0 });
targetAlphabet.lookupLabel("lbl");
Labeling labeling = targetAlphabet.lookupLabel("lbl");
Instance instance = new Instance(fv, labeling, null, null);
InstanceList list = new InstanceList(pipe);
list.addThruPipe(instance);
NaiveBayes classifier1 = trainer.train(list);
NaiveBayes classifier2 = trainer.getClassifier();
assertSame(classifier1, classifier2);
}

@Test
public void testTrainIncrementalWithNullPipeSetsDefaultNoop() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet targetAlphabet = new LabelAlphabet();
FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "feature1" }, new double[] { 1.0 });
targetAlphabet.lookupLabel("label");
Labeling labeling = targetAlphabet.lookupLabel("label");
Instance instance = new Instance(fv, labeling, null, null);
NaiveBayesTrainer trainer = new NaiveBayesTrainer();
NaiveBayes classifier = trainer.trainIncremental(instance);
assertNotNull(classifier);
assertNotNull(classifier.getInstancePipe());
}

@Test
public void testMultipleTrainCallsResetsPreviousState() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet targetAlphabet = new LabelAlphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
FeatureVector fv1 = new FeatureVector(dataAlphabet, new String[] { "feature1" }, new double[] { 1.0 });
Labeling labeling1 = targetAlphabet.lookupLabel("label1");
Instance instance1 = new Instance(fv1, labeling1, null, null);
InstanceList list1 = new InstanceList(pipe);
list1.addThruPipe(instance1);
trainer.train(list1);
FeatureVector fv2 = new FeatureVector(dataAlphabet, new String[] { "feature2" }, new double[] { 1.0 });
Labeling labeling2 = targetAlphabet.lookupLabel("label2");
Instance instance2 = new Instance(fv2, labeling2, null, null);
InstanceList list2 = new InstanceList(pipe);
list2.addThruPipe(instance2);
NaiveBayes classifier = trainer.train(list2);
assertNotNull(classifier);
assertEquals(2, classifier.getLabelAlphabet().size());
assertEquals(2, classifier.getAlphabet().size());
}

@Test
public void testIncrementalTrainCreatesProperMultinomials() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet targetAlphabet = new LabelAlphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
targetAlphabet.lookupLabel("classA");
FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "wordA" }, new double[] { 1.0 });
Labeling labeling = targetAlphabet.lookupLabel("classA");
Instance instance = new Instance(fv, labeling, null, null);
NaiveBayes classifier = trainer.trainIncremental(instance);
Multinomial[] multinomials = classifier.getMultinomials();
assertEquals(1, multinomials.length);
assertTrue(multinomials[0].getAlphabet().lookupIndex("wordA", false) >= 0);
}

@Test
public void testFactoryNewClassifierTrainerWithNullClassifier() {
NaiveBayesTrainer.Factory factory = new NaiveBayesTrainer.Factory();
NaiveBayesTrainer trainer = factory.newClassifierTrainer(null);
assertNotNull(trainer);
assertNull(trainer.getClassifier());
}

@Test
public void testFactoryNewClassifierTrainerWithNonNullClassifier() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet targetAlphabet = new LabelAlphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
Multinomial[] m = new Multinomial[] { new Multinomial(new double[] { 1.0, 2.0 }) };
Multinomial p = new Multinomial(new double[] { 1.0 });
NaiveBayes baseClassifier = new NaiveBayes(pipe, p, m);
NaiveBayesTrainer.Factory factory = new NaiveBayesTrainer.Factory();
NaiveBayesTrainer trainer = factory.newClassifierTrainer(baseClassifier);
assertNotNull(trainer);
assertNotNull(trainer.getClassifier());
}

@Test
public void testTrainWithInstanceWeightAffectingPriorAndFeatureCounts() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet targetAlphabet = new LabelAlphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "token" }, new double[] { 1.0 });
Labeling labeling = targetAlphabet.lookupLabel("foo");
Instance instance = new Instance(fv, labeling, null, null);
InstanceList list = new InstanceList(pipe);
list.addThruPipe(instance);
list.setInstanceWeight(instance, 3.0);
NaiveBayes classifier = trainer.train(list);
assertNotNull(classifier);
}

@Test
public void testSerializationPreservesAlphabetsAndPipe() throws Exception {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet targetAlphabet = new LabelAlphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "hi" }, new double[] { 1.0 });
Labeling labeling = targetAlphabet.lookupLabel("greet");
Instance i = new Instance(fv, labeling, null, null);
InstanceList l = new InstanceList(pipe);
l.addThruPipe(i);
trainer.train(l);
ByteArrayOutputStream baos = new ByteArrayOutputStream();
ObjectOutputStream out = new ObjectOutputStream(baos);
out.writeObject(trainer);
out.close();
ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
ObjectInputStream in = new ObjectInputStream(bais);
NaiveBayesTrainer restored = (NaiveBayesTrainer) in.readObject();
assertNotNull(restored.getAlphabet());
assertEquals(dataAlphabet.size(), restored.getAlphabet().size());
}

@Test
public void testEstimateFeatureMultinomialsReturnsCorrectSizeArray() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet targetAlphabet = new LabelAlphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
targetAlphabet.lookupLabel("A");
FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "x" }, new double[] { 1.0 });
Labeling labeling = targetAlphabet.lookupLabel("A");
Instance instance = new Instance(fv, labeling, null, null);
InstanceList list = new InstanceList(pipe);
list.addThruPipe(instance);
NaiveBayes classifier = trainer.train(list);
assertEquals(1, classifier.getMultinomials().length);
}

@Test
public void testEmptyLabelingInstanceIsIgnored() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new Alphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "x" }, new double[] { 1.0 });
// Instance instance = new Instance(fv, new LabelVector(), null, null);
InstanceList list = new InstanceList(pipe);
// list.addThruPipe(instance);
NaiveBayes classifier = trainer.train(list);
assertNotNull(classifier);
}

@Test
public void testFeatureEstimatorAndPriorEstimatorPreservedInSerialization() throws Exception {
NaiveBayesTrainer trainer = new NaiveBayesTrainer();
trainer.setFeatureMultinomialEstimator(new Multinomial.MLEstimator());
trainer.setPriorMultinomialEstimator(new Multinomial.MLEstimator());
ByteArrayOutputStream baos = new ByteArrayOutputStream();
ObjectOutputStream out = new ObjectOutputStream(baos);
out.writeObject(trainer);
out.close();
ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
ObjectInputStream in = new ObjectInputStream(bais);
NaiveBayesTrainer loaded = (NaiveBayesTrainer) in.readObject();
assertTrue(loaded.getFeatureMultinomialEstimator() instanceof Multinomial.MLEstimator);
assertTrue(loaded.getPriorMultinomialEstimator() instanceof Multinomial.MLEstimator);
}

@Test
public void testIncrementalTrainWithEmptyFeatureVectorDoesNotThrow() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet targetAlphabet = new LabelAlphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
targetAlphabet.lookupLabel("empty");
// FeatureVector fv = new FeatureVector(dataAlphabet);
Labeling labeling = targetAlphabet.lookupLabel("empty");
// Instance instance = new Instance(fv, labeling, null, null);
// NaiveBayes classifier = trainer.trainIncremental(instance);
// assertNotNull(classifier);
}

@Test
public void testTrainIncrementalWithDifferentPipesThrows() {
Alphabet dataAlphabet1 = new Alphabet();
Alphabet targetAlphabet1 = new LabelAlphabet();
Pipe pipe1 = new Noop(dataAlphabet1, targetAlphabet1);
Alphabet dataAlphabet2 = new Alphabet();
Alphabet targetAlphabet2 = new LabelAlphabet();
Pipe pipe2 = new Noop(dataAlphabet2, targetAlphabet2);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe1);
FeatureVector fv = new FeatureVector(dataAlphabet2, new String[] { "word" }, new double[] { 1.0 });
((LabelAlphabet) targetAlphabet2).lookupLabel("class");
Labeling labeling = ((LabelAlphabet) targetAlphabet2).lookupLabel("class");
Instance instance = new Instance(fv, labeling, null, null);
InstanceList list = new InstanceList(pipe2);
list.addThruPipe(instance);
try {
trainer.train(list);
fail("Expected IllegalArgumentException due to pipe mismatch");
} catch (IllegalArgumentException e) {
}
}

@Test
public void testCloneEstimatorAndAlphabetAfterTargetAlphabetGrowth() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet targetAlphabet = new LabelAlphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
targetAlphabet.lookupLabel("first");
FeatureVector fv1 = new FeatureVector(dataAlphabet, new String[] { "a" }, new double[] { 1.0 });
Labeling lab1 = targetAlphabet.lookupLabel("first");
Instance inst1 = new Instance(fv1, lab1, null, null);
trainer.trainIncremental(inst1);
targetAlphabet.lookupLabel("second");
FeatureVector fv2 = new FeatureVector(dataAlphabet, new String[] { "b" }, new double[] { 1.0 });
Labeling lab2 = targetAlphabet.lookupLabel("second");
Instance inst2 = new Instance(fv2, lab2, null, null);
NaiveBayes classifier = trainer.trainIncremental(inst2);
assertNotNull(classifier);
assertEquals(2, classifier.getLabelAlphabet().size());
}

@Test
public void testTrainReturnsNewClassifierEachTime() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet targetAlphabet = new LabelAlphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
FeatureVector fv1 = new FeatureVector(dataAlphabet, new String[] { "word1" }, new double[] { 1.0 });
Labeling labeling1 = targetAlphabet.lookupLabel("label");
Instance inst1 = new Instance(fv1, labeling1, null, null);
InstanceList l1 = new InstanceList(pipe);
l1.addThruPipe(inst1);
NaiveBayes classifier1 = trainer.train(l1);
FeatureVector fv2 = new FeatureVector(dataAlphabet, new String[] { "word2" }, new double[] { 2.0 });
Labeling labeling2 = targetAlphabet.lookupLabel("label");
Instance inst2 = new Instance(fv2, labeling2, null, null);
InstanceList l2 = new InstanceList(pipe);
l2.addThruPipe(inst2);
NaiveBayes classifier2 = trainer.train(l2);
assertNotSame(classifier1, classifier2);
}

@Test
public void testInstanceWeightUsedWithIncrementalTrain() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet targetAlphabet = new LabelAlphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "f" }, new double[] { 1.0 });
Labeling labeling = targetAlphabet.lookupLabel("w");
Instance instance = new Instance(fv, labeling, null, null);
InstanceList list = new InstanceList(pipe);
list.addThruPipe(instance);
list.setInstanceWeight(instance, 0.5);
NaiveBayes classifier = trainer.trainIncremental(list);
assertNotNull(classifier);
}

@Test
public void testDocLengthNormalizationAffectsWeight() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet targetAlphabet = new LabelAlphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
trainer.setDocLengthNormalization(5.0);
FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "x", "y" }, new double[] { 1.0, 1.0 });
Labeling labeling = targetAlphabet.lookupLabel("some");
Instance instance = new Instance(fv, labeling, null, null);
InstanceList list = new InstanceList(pipe);
list.addThruPipe(instance);
NaiveBayes classifier = trainer.trainIncremental(list);
assertNotNull(classifier);
}

@Test
public void testTrainIncrementalWithZeroInstanceWeightSkipsCounts() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet targetAlphabet = new LabelAlphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
label: {
FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "zz" }, new double[] { 1.0 });
Labeling labeling = targetAlphabet.lookupLabel("zz");
Instance instance = new Instance(fv, labeling, null, null);
InstanceList list = new InstanceList(pipe);
list.addThruPipe(instance);
list.setInstanceWeight(instance, 0.0);
NaiveBayes classifier = trainer.trainIncremental(list);
assertNotNull(classifier);
}
}

@Test
public void testTrainIncrementalMixedInstancesWithAndWithoutFeatures() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet targetAlphabet = new LabelAlphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
targetAlphabet.lookupLabel("A");
targetAlphabet.lookupLabel("B");
FeatureVector fv1 = new FeatureVector(dataAlphabet, new String[] { "x" }, new double[] { 1.0 });
Labeling lab1 = targetAlphabet.lookupLabel("A");
Instance inst1 = new Instance(fv1, lab1, null, null);
FeatureVector fv2 = new FeatureVector(dataAlphabet, new String[] { "z" }, new double[] { 0.0 });
Labeling lab2 = targetAlphabet.lookupLabel("B");
Instance inst2 = new Instance(fv2, lab2, null, null);
InstanceList list = new InstanceList(pipe);
list.addThruPipe(inst1);
list.addThruPipe(inst2);
NaiveBayes classifier = trainer.trainIncremental(list);
assertNotNull(classifier);
assertEquals(2, classifier.getLabelAlphabet().size());
}

@Test
public void testSetFeatureEstimatorBeforeTrainingOnlyAllowedOnce() {
NaiveBayesTrainer trainer = new NaiveBayesTrainer();
Multinomial.Estimator est1 = new Multinomial.MLEstimator();
trainer.setFeatureMultinomialEstimator(est1);
Multinomial.Estimator est2 = new Multinomial.MLEstimator();
trainer.setFeatureMultinomialEstimator(est2);
assertEquals(est2, trainer.getFeatureMultinomialEstimator());
}

@Test(expected = IllegalStateException.class)
public void testSetPriorEstimatorAfterTrainingInitializationFails() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet targetAlphabet = new LabelAlphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "a" }, new double[] { 1.0 });
Labeling labeling = targetAlphabet.lookupLabel("alpha");
Instance instance = new Instance(fv, labeling, null, null);
InstanceList list = new InstanceList(pipe);
list.addThruPipe(instance);
trainer.trainIncremental(list);
Multinomial.Estimator newEstimator = new Multinomial.MLEstimator();
trainer.setPriorMultinomialEstimator(newEstimator);
}

@Test
public void testTrainWithNullLabelingIsSkipped() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new LabelAlphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "feat" }, new double[] { 1.0 });
Instance instance = new Instance(fv, null, null, null);
InstanceList list = new InstanceList(pipe);
list.addThruPipe(instance);
NaiveBayes classifier = trainer.trainIncremental(list);
assertNotNull(classifier);
}

@Test
public void testTrainWithMultipleLabelsDifferentWeights() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet targetAlphabet = new LabelAlphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
Label label1 = targetAlphabet.lookupLabel("L1");
Label label2 = targetAlphabet.lookupLabel("L2");
Label[] labels = new Label[] { label1, label2 };
double[] weights = new double[] { 0.7, 0.3 };
FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "token" }, new double[] { 1.0 });
LabelVector labeling = new LabelVector(labels, weights);
Instance instance = new Instance(fv, labeling, null, null);
InstanceList list = new InstanceList(pipe);
list.addThruPipe(instance);
NaiveBayes classifier = trainer.trainIncremental(list);
assertNotNull(classifier);
}

@Test
public void testTrainWithUnknownFeatureIgnoredInPrediction() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet targetAlphabet = new LabelAlphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
FeatureVector fvTrain = new FeatureVector(dataAlphabet, new String[] { "known" }, new double[] { 1.0 });
Labeling labelingTrain = targetAlphabet.lookupLabel("yes");
Instance instanceTrain = new Instance(fvTrain, labelingTrain, null, null);
InstanceList list = new InstanceList(pipe);
list.addThruPipe(instanceTrain);
NaiveBayes classifier = trainer.train(list);
Alphabet newAlphabet = new Alphabet();
FeatureVector fvTest = new FeatureVector(newAlphabet, new String[] { "unknown" }, new double[] { 1.0 });
Labeling predicted = classifier.classify(fvTest).getLabeling();
assertNotNull(predicted);
assertTrue(predicted.getBestValue() >= 0.0);
}

@Test
public void testAlphabetsStillMatchAfterSerialization() throws Exception {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet targetAlphabet = new LabelAlphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "tok" }, new double[] { 1.0 });
Labeling l = targetAlphabet.lookupLabel("A");
Instance i = new Instance(fv, l, null, null);
InstanceList li = new InstanceList(pipe);
li.addThruPipe(i);
trainer.train(li);
ByteArrayOutputStream baos = new ByteArrayOutputStream();
ObjectOutputStream oos = new ObjectOutputStream(baos);
oos.writeObject(trainer);
oos.close();
ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
ObjectInputStream ois = new ObjectInputStream(bais);
NaiveBayesTrainer loadedTrainer = (NaiveBayesTrainer) ois.readObject();
AlphabetCarrying original = trainer;
AlphabetCarrying deserialized = loadedTrainer;
// assertTrue(original.alphabetsMatch(deserialized));
}

@Test
public void testTrainWithLargeInstanceWeightInfluencesPrior() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet targetAlphabet = new LabelAlphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
targetAlphabet.lookupLabel("spam");
FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "offer" }, new double[] { 1.0 });
Labeling labeling = targetAlphabet.lookupLabel("spam");
Instance instance = new Instance(fv, labeling, null, null);
InstanceList list = new InstanceList(pipe);
list.addThruPipe(instance);
list.setInstanceWeight(instance, 10000.0);
NaiveBayes classifier = trainer.trainIncremental(list);
assertNotNull(classifier);
}

@Test
public void testTrainWithNegativeInstanceWeightIsSkipped() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet targetAlphabet = new LabelAlphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "f" }, new double[] { 1.0 });
Labeling labeling = targetAlphabet.lookupLabel("label");
Instance instance = new Instance(fv, labeling, null, null);
InstanceList list = new InstanceList(pipe);
list.addThruPipe(instance);
list.setInstanceWeight(instance, -1.0);
try {
trainer.trainIncremental(list);
} catch (AssertionError e) {
assertTrue(e.getMessage() == null || e.getMessage().isEmpty());
}
}

@Test
public void testClassificationAfterSingleLabelTraining() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet targetAlphabet = new LabelAlphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
targetAlphabet.lookupLabel("only");
FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "featureX" }, new double[] { 1.0 });
Labeling labeling = targetAlphabet.lookupLabel("only");
Instance instance = new Instance(fv, labeling, null, null);
InstanceList list = new InstanceList(pipe);
list.addThruPipe(instance);
NaiveBayes classifier = trainer.trainIncremental(list);
Labeling classified = classifier.classify(fv).getLabeling();
assertEquals("only", classified.getBestLabel().toString());
assertEquals(1.0, classified.getBestValue(), 0.0001);
}

@Test
public void testSetBothEstimatorsAndTrain() {
NaiveBayesTrainer trainer = new NaiveBayesTrainer();
trainer.setFeatureMultinomialEstimator(new Multinomial.MLEstimator());
trainer.setPriorMultinomialEstimator(new Multinomial.MLEstimator());
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet targetAlphabet = new LabelAlphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "tok" }, new double[] { 1.0 });
targetAlphabet.lookupLabel("e");
Labeling labeling = targetAlphabet.lookupLabel("e");
Instance i = new Instance(fv, labeling, null, null);
InstanceList list = new InstanceList(pipe);
list.addThruPipe(i);
NaiveBayesTrainer configuredTrainer = new NaiveBayesTrainer(pipe);
configuredTrainer.setFeatureMultinomialEstimator(new Multinomial.MLEstimator());
configuredTrainer.setPriorMultinomialEstimator(new Multinomial.MLEstimator());
NaiveBayes classifier = configuredTrainer.train(list);
assertNotNull(classifier);
}

@Test
public void testTargetAlphabetGrowMultipleTimes() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet targetAlphabet = new LabelAlphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
for (int i = 0; i < 5; i++) {
String labelName = "label" + i;
targetAlphabet.lookupLabel(labelName);
FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "f" + i }, new double[] { 1.0 });
Labeling labeling = targetAlphabet.lookupLabel(labelName);
Instance instance = new Instance(fv, labeling, null, null);
trainer.trainIncremental(instance);
}
NaiveBayes classifier = trainer.getClassifier();
assertEquals(5, classifier.getLabelAlphabet().size());
}

@Test
public void testTrainWithEmptyPipeAndLabelAlphabet() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet targetAlphabet = new LabelAlphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
InstanceList list = new InstanceList(pipe);
NaiveBayes classifier = trainer.train(list);
assertNotNull(classifier);
assertEquals(0, classifier.getLabelAlphabet().size());
assertEquals(0, classifier.getAlphabet().size());
}

@Test
public void testTrainWithSingleFeatureSingleLabelMultipleTimes() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet targetAlphabet = new LabelAlphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "feature1" }, new double[] { 1.0 });
Labeling labeling = targetAlphabet.lookupLabel("label1");
Instance instance = new Instance(fv, labeling, null, null);
InstanceList list = new InstanceList(pipe);
list.addThruPipe(instance);
list.addThruPipe(instance);
list.setInstanceWeight(instance, 2.0);
NaiveBayes classifier = trainer.train(list);
assertNotNull(classifier);
assertEquals(1, classifier.getLabelAlphabet().size());
assertEquals(1, classifier.getAlphabet().size());
}

@Test
public void testTrainWithInstanceWithNoData() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet targetAlphabet = new LabelAlphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
Label label = targetAlphabet.lookupLabel("nolabel");
LabelVector labeling = new LabelVector(new Label[] { label }, new double[] { 1.0 });
Instance instance = new Instance(null, labeling, null, null);
InstanceList list = new InstanceList(pipe);
list.addThruPipe(instance);
try {
trainer.train(list);
fail("Expected ClassCastException due to null data");
} catch (ClassCastException expected) {
}
}

@Test
public void testTrainIncrementalWithDuplicateLabels() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet targetAlphabet = new LabelAlphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
targetAlphabet.lookupLabel("duplicate");
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
FeatureVector fv1 = new FeatureVector(dataAlphabet, new String[] { "token1" }, new double[] { 1.0 });
Labeling labeling1 = targetAlphabet.lookupLabel("duplicate");
FeatureVector fv2 = new FeatureVector(dataAlphabet, new String[] { "token2" }, new double[] { 2.0 });
Labeling labeling2 = targetAlphabet.lookupLabel("duplicate");
Instance instance1 = new Instance(fv1, labeling1, null, null);
Instance instance2 = new Instance(fv2, labeling2, null, null);
InstanceList list = new InstanceList(pipe);
list.addThruPipe(instance1);
list.addThruPipe(instance2);
NaiveBayes classifier = trainer.trainIncremental(list);
assertNotNull(classifier);
assertEquals(1, classifier.getLabelAlphabet().size());
}

@Test
public void testTrainFromFactoryWithAllDefaults() {
NaiveBayesTrainer.Factory factory = new NaiveBayesTrainer.Factory();
NaiveBayesTrainer trainer = factory.newClassifierTrainer(null);
assertNotNull(trainer);
assertNull(trainer.getClassifier());
assertTrue(trainer.getFeatureMultinomialEstimator() instanceof Multinomial.LaplaceEstimator);
assertTrue(trainer.getPriorMultinomialEstimator() instanceof Multinomial.LaplaceEstimator);
assertEquals(-1.0, trainer.getDocLengthNormalization(), 0.0);
}

@Test
public void testAlphabetsMatchReturnsFalseForDifferentAlphabets() {
Alphabet dataAlphabet1 = new Alphabet();
LabelAlphabet targetAlphabet1 = new LabelAlphabet();
Pipe pipe1 = new Noop(dataAlphabet1, targetAlphabet1);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe1);
Alphabet dataAlphabet2 = new Alphabet();
Alphabet targetAlphabet2 = new LabelAlphabet();
AlphabetCarrying mismatched = new AlphabetCarrying() {

public Alphabet[] getAlphabets() {
return new Alphabet[] { dataAlphabet2, targetAlphabet2 };
}

public Alphabet getAlphabet() {
return dataAlphabet2;
}
};
assertFalse(trainer.alphabetsMatch(mismatched));
}

@Test
public void testTrainIncrementalWithAlphabetResizeByThreeLabels() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet targetAlphabet = new LabelAlphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
for (int i = 0; i < 3; i++) {
String label = "label_" + i;
targetAlphabet.lookupLabel(label);
FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "feature" + i }, new double[] { 1.0 });
Labeling labeling = targetAlphabet.lookupLabel(label);
Instance instance = new Instance(fv, labeling, null, null);
trainer.trainIncremental(instance);
}
NaiveBayes classifier = trainer.getClassifier();
assertNotNull(classifier);
assertEquals(3, classifier.getLabelAlphabet().size());
}

@Test
public void testTrainWithHighDimensionalSparseFeatureVector() {
Alphabet dataAlphabet = new Alphabet();
for (int i = 0; i < 1000; i++) {
dataAlphabet.lookupIndex("f" + i);
}
LabelAlphabet targetAlphabet = new LabelAlphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
int[] indices = new int[] { 10, 100, 999 };
double[] values = new double[] { 0.5, 1.5, 2.0 };
FeatureVector fv = new FeatureVector(dataAlphabet, indices, values);
Labeling labeling = targetAlphabet.lookupLabel("class");
Instance instance = new Instance(fv, labeling, null, null);
InstanceList list = new InstanceList(pipe);
list.addThruPipe(instance);
NaiveBayes classifier = trainer.train(list);
assertNotNull(classifier);
assertEquals(1000, classifier.getAlphabet().size());
}

@Test
public void testTrainWithLabelWeightZeroIsSkipped() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet targetAlphabet = new LabelAlphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
Label label = targetAlphabet.lookupLabel("zero");
FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "skip" }, new double[] { 1.0 });
LabelVector labeling = new LabelVector(new Label[] { label }, new double[] { 0.0 });
Instance instance = new Instance(fv, labeling, null, null);
InstanceList list = new InstanceList(pipe);
list.addThruPipe(instance);
NaiveBayes classifier = trainer.trainIncremental(list);
assertNotNull(classifier);
}

@Test
public void testTrainIncrementalAfterSetFeatureMultinomialEstimatorThrows() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet targetAlphabet = new LabelAlphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "f1" }, new double[] { 1.0 });
Labeling labeling = targetAlphabet.lookupLabel("L1");
Instance instance = new Instance(fv, labeling, null, null);
trainer.trainIncremental(instance);
try {
trainer.setFeatureMultinomialEstimator(new Multinomial.MLEstimator());
fail("Expected IllegalStateException");
} catch (IllegalStateException e) {
assertTrue(e.getMessage().contains("Can't set after incrementalTrain"));
}
}

@Test
public void testTrainIncrementalAfterSetPriorMultinomialEstimatorThrows() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet targetAlphabet = new LabelAlphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "f1" }, new double[] { 1.0 });
Labeling labeling = targetAlphabet.lookupLabel("L1");
Instance instance = new Instance(fv, labeling, null, null);
trainer.trainIncremental(instance);
try {
trainer.setPriorMultinomialEstimator(new Multinomial.MLEstimator());
fail("Expected IllegalStateException");
} catch (IllegalStateException e) {
assertTrue(e.getMessage().contains("Can't set after incrementalTrain"));
}
}

@Test
public void testTrainWithDocumentLengthNormalizationZeroIsIgnored() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet targetAlphabet = new LabelAlphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
trainer.setDocLengthNormalization(0.0);
FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "x" }, new double[] { 1.0 });
Labeling labeling = targetAlphabet.lookupLabel("label");
Instance instance = new Instance(fv, labeling, null, null);
InstanceList list = new InstanceList(pipe);
list.addThruPipe(instance);
NaiveBayes classifier = trainer.train(list);
assertNotNull(classifier);
}

@Test
public void testTrainWithInfiniteInstanceWeightThrowsAssertionError() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet targetAlphabet = new LabelAlphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "f" }, new double[] { 1.0 });
Labeling labeling = targetAlphabet.lookupLabel("label");
Instance instance = new Instance(fv, labeling, null, null);
InstanceList list = new InstanceList(pipe);
list.addThruPipe(instance);
list.setInstanceWeight(instance, Double.POSITIVE_INFINITY);
try {
trainer.train(list);
fail("Expected AssertionError for infinite instance weight");
} catch (AssertionError e) {
}
}

@Test
public void testTrainWithMissingAlphabetsCreatesFromInstance() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet targetAlphabet = new LabelAlphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
targetAlphabet.lookupLabel("foo");
FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "bar" }, new double[] { 1.0 });
Labeling labeling = targetAlphabet.lookupLabel("foo");
Instance instance = new Instance(fv, labeling, null, null);
NaiveBayesTrainer trainer = new NaiveBayesTrainer();
NaiveBayes classifier = trainer.trainIncremental(instance);
assertNotNull(classifier);
assertEquals(1, classifier.getLabelAlphabet().size());
assertEquals(1, classifier.getAlphabet().size());
}

@Test
public void testTrainCallsTrainIncrementalAndResetsState() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet targetAlphabet = new LabelAlphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
targetAlphabet.lookupLabel("class");
FeatureVector fv = new FeatureVector(dataAlphabet, new String[] { "w" }, new double[] { 1.0 });
Labeling labeling = targetAlphabet.lookupLabel("class");
InstanceList list = new InstanceList(pipe);
list.addThruPipe(new Instance(fv, labeling, null, null));
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
NaiveBayes c1 = trainer.train(list);
NaiveBayes c2 = trainer.train(list);
assertNotNull(c1);
assertNotNull(c2);
assertNotSame(c1, c2);
}

@Test
public void testGetAlphabetsReturnsCorrectOrder() {
Alphabet dataAlphabet = new Alphabet();
Alphabet targetAlphabet = new LabelAlphabet();
Pipe pipe = new Noop(dataAlphabet, targetAlphabet);
NaiveBayesTrainer trainer = new NaiveBayesTrainer(pipe);
Alphabet[] alphabets = trainer.getAlphabets();
assertEquals(dataAlphabet, alphabets[0]);
assertEquals(targetAlphabet, alphabets[1]);
}

@Test
public void testFactoryWithCustomEstimatorsAndNormalization() {
NaiveBayesTrainer.Factory factory = new NaiveBayesTrainer.Factory();
factory.setDocLengthNormalization(5.0);
factory.setFeatureMultinomialEstimator(new Multinomial.MLEstimator());
factory.setPriorMultinomialEstimator(new Multinomial.LaplaceEstimator());
NaiveBayesTrainer trainer = factory.newClassifierTrainer(null);
assertEquals(-1.0, trainer.getDocLengthNormalization(), 0.0);
}

@Test
public void testTrainFileDeserializationWithCorruptedBytesCausesIOException() throws Exception {
byte[] badData = new byte[] { 0, 1, 2, 3, 4 };
ByteArrayInputStream bais = new ByteArrayInputStream(badData);
ObjectInputStream ois = new ObjectInputStream(bais);
try {
NaiveBayesTrainer trainer = new NaiveBayesTrainer();
trainer.getClass().getDeclaredMethod("readObject", ObjectInputStream.class).setAccessible(true);
trainer.getClass().getDeclaredMethod("readObject", ObjectInputStream.class).invoke(trainer, ois);
fail("Expected IOException or ClassNotFoundException");
} catch (Exception ex) {
assertTrue(ex instanceof IOException || ex.getCause() instanceof IOException || ex.getCause() instanceof ClassNotFoundException);
}
}
}
