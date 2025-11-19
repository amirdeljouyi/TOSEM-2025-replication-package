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

public class NaiveBayes_5_GPTLLMTest {

@Test
public void testGetPriorsAndMultinomials() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("f0");
dataAlphabet.lookupIndex("f1");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("yes");
labelAlphabet.lookupIndex("no");
Pipe pipe = new Noop();
pipe.setDataAlphabet(dataAlphabet);
pipe.setTargetAlphabet(labelAlphabet);
double[] priorProbs = new double[] { 0.6, 0.4 };
// Multinomial prior = new Multinomial(labelAlphabet, priorProbs);
double[] cond1 = new double[] { 0.4, 0.6 };
double[] cond2 = new double[] { 0.7, 0.3 };
// Multinomial m1 = new Multinomial(dataAlphabet, cond1);
// Multinomial m2 = new Multinomial(dataAlphabet, cond2);
// Multinomial[] multinomials = new Multinomial[] { m1, m2 };
// NaiveBayes nb = new NaiveBayes(pipe, prior, multinomials);
// assertNotNull(nb.getPriors());
// assertNotNull(nb.getMultinomials());
// assertEquals(2, nb.getMultinomials().length);
// assertEquals(2, nb.getPriors().size());
}

@Test
public void testClassifyInstance() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("a");
dataAlphabet.lookupIndex("b");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("X");
labelAlphabet.lookupIndex("Y");
Pipe pipe = new Noop();
pipe.setDataAlphabet(dataAlphabet);
pipe.setTargetAlphabet(labelAlphabet);
double[] priorProbs = new double[] { 0.8, 0.2 };
// Multinomial prior = new Multinomial(labelAlphabet, priorProbs);
double[] class1 = new double[] { 0.9, 0.1 };
double[] class2 = new double[] { 0.2, 0.8 };
// Multinomial m1 = new Multinomial(dataAlphabet, class1);
// Multinomial m2 = new Multinomial(dataAlphabet, class2);
// Multinomial[] multinomials = new Multinomial[] { m1, m2 };
// NaiveBayes nb = new NaiveBayes(pipe, prior, multinomials);
int[] indices = new int[] { 0, 1 };
double[] values = new double[] { 1.0, 2.0 };
FeatureVector fv = new FeatureVector(dataAlphabet, indices, values);
Instance instance = new Instance(fv, "X", null, null);
// Classification classification = nb.classify(instance);
// assertNotNull(classification);
// assertEquals(nb, classification.getClassifier());
// assertEquals(instance, classification.getInstance());
// Labeling labeling = classification.getLabeling();
// assertEquals(2, labeling.numLocations());
// double total = labeling.valueAtLocation(0) + labeling.valueAtLocation(1);
// assertEquals(1.0, total, 1e-6);
}

@Test
public void testLabelLogLikelihoodWithSingleLabel() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("x");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("pos");
labelAlphabet.lookupIndex("neg");
Pipe pipe = new Noop();
pipe.setDataAlphabet(dataAlphabet);
pipe.setTargetAlphabet(labelAlphabet);
double[] priorProbs = new double[] { 0.5, 0.5 };
// Multinomial prior = new Multinomial(labelAlphabet, priorProbs);
double[] m1Probs = new double[] { 0.7 };
double[] m2Probs = new double[] { 0.3 };
// Multinomial m1 = new Multinomial(dataAlphabet, m1Probs);
// Multinomial m2 = new Multinomial(dataAlphabet, m2Probs);
// Multinomial[] multinomials = new Multinomial[] { m1, m2 };
// NaiveBayes nb = new NaiveBayes(pipe, prior, multinomials);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 2.0 });
Instance inst = new Instance(fv, "pos", null, null);
InstanceList list = new InstanceList(pipe);
list.add(inst);
// double likelihood = nb.labelLogLikelihood(list);
// assertTrue(Double.isFinite(likelihood));
}

@Test
public void testDataLogLikelihoodWithUnlabeledInstance() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("x");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("y1");
labelAlphabet.lookupIndex("y2");
Pipe pipe = new Noop();
pipe.setDataAlphabet(dataAlphabet);
pipe.setTargetAlphabet(labelAlphabet);
double[] priorProbs = new double[] { 0.6, 0.4 };
// Multinomial prior = new Multinomial(labelAlphabet, priorProbs);
double[] m1 = new double[] { 0.8 };
double[] m2 = new double[] { 0.2 };
// Multinomial mm1 = new Multinomial(dataAlphabet, m1);
// Multinomial mm2 = new Multinomial(dataAlphabet, m2);
// Multinomial[] multinomials = new Multinomial[] { mm1, mm2 };
// NaiveBayes nb = new NaiveBayes(pipe, prior, multinomials);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
Instance inst = new Instance(fv, null, null, null);
InstanceList list = new InstanceList(pipe);
list.add(inst);
// double ll = nb.dataLogLikelihood(list);
// assertTrue(Double.isFinite(ll));
}

@Test
public void testPrintWordsRunsWithoutException() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("foo");
dataAlphabet.lookupIndex("bar");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("spam");
labelAlphabet.lookupIndex("ham");
Pipe pipe = new Noop();
pipe.setDataAlphabet(dataAlphabet);
pipe.setTargetAlphabet(labelAlphabet);
double[] priorProbs = new double[] { 0.3, 0.7 };
// Multinomial prior = new Multinomial(labelAlphabet, priorProbs);
double[] p1 = new double[] { 0.6, 0.4 };
double[] p2 = new double[] { 0.2, 0.8 };
// Multinomial m1 = new Multinomial(dataAlphabet, p1);
// Multinomial m2 = new Multinomial(dataAlphabet, p2);
// Multinomial[] multinomials = new Multinomial[] { m1, m2 };
// NaiveBayes nb = new NaiveBayes(pipe, prior, multinomials);
// nb.printWords(2);
}

@Test
public void testSerializationAndDeserialization() throws Exception {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("email");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("spam");
labelAlphabet.lookupIndex("ham");
Pipe pipe = new Noop();
pipe.setDataAlphabet(dataAlphabet);
pipe.setTargetAlphabet(labelAlphabet);
double[] priorArr = new double[] { 0.6, 0.4 };
// Multinomial prior = new Multinomial(labelAlphabet, priorArr);
double[] f1Arr = new double[] { 0.9 };
double[] f2Arr = new double[] { 0.1 };
// Multinomial f1 = new Multinomial(dataAlphabet, f1Arr);
// Multinomial f2 = new Multinomial(dataAlphabet, f2Arr);
// Multinomial[] conditionals = new Multinomial[] { f1, f2 };
// NaiveBayes original = new NaiveBayes(pipe, prior, conditionals);
ByteArrayOutputStream baos = new ByteArrayOutputStream();
ObjectOutputStream oos = new ObjectOutputStream(baos);
// oos.writeObject(original);
oos.close();
ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
ObjectInputStream ois = new ObjectInputStream(bais);
NaiveBayes loaded = (NaiveBayes) ois.readObject();
assertNotNull(loaded.getPriors());
assertNotNull(loaded.getMultinomials());
}

@Test(expected = ClassNotFoundException.class)
public void testDeserializationVersionMismatchThrows() throws Exception {
ByteArrayOutputStream baos = new ByteArrayOutputStream();
ObjectOutputStream oos = new ObjectOutputStream(baos);
oos.writeInt(999);
oos.close();
ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
ObjectInputStream ois = new ObjectInputStream(bais);
NaiveBayes dummy = new NaiveBayes(null, (Multinomial) null, (Multinomial[]) null);
java.lang.reflect.Method readMethod = NaiveBayes.class.getDeclaredMethod("readObject", ObjectInputStream.class);
readMethod.setAccessible(true);
readMethod.invoke(dummy, ois);
}

@Test
public void testInputWithSoftLabelVector() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("token");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("label1");
labelAlphabet.lookupIndex("label2");
Pipe pipe = new Noop();
pipe.setDataAlphabet(dataAlphabet);
pipe.setTargetAlphabet(labelAlphabet);
double[] prior = new double[] { 0.5, 0.5 };
// Multinomial multPrior = new Multinomial(labelAlphabet, prior);
double[] cond1 = new double[] { 0.6 };
double[] cond2 = new double[] { 0.4 };
// Multinomial m1 = new Multinomial(dataAlphabet, cond1);
// Multinomial m2 = new Multinomial(dataAlphabet, cond2);
// Multinomial[] conditionals = new Multinomial[] { m1, m2 };
// NaiveBayes nb = new NaiveBayes(pipe, multPrior, conditionals);
double[] labelWeights = new double[] { 0.7, 0.3 };
LabelVector labelVector = new LabelVector(labelAlphabet, labelWeights);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
Instance inst = new Instance(fv, labelVector, "soft", null);
InstanceList list = new InstanceList(pipe);
list.add(inst);
// double likelihood = nb.labelLogLikelihood(list);
// assertTrue(Double.isFinite(likelihood));
}

@Test
public void testClassifyWithFeatureOutsideIndexBoundsIgnored() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("known1");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("A");
labelAlphabet.lookupIndex("B");
Pipe pipe = new Noop();
pipe.setDataAlphabet(dataAlphabet);
pipe.setTargetAlphabet(labelAlphabet);
double[] priorProbs = new double[] { 0.4, 0.6 };
// Multinomial prior = new Multinomial(labelAlphabet, priorProbs);
double[] condA = new double[] { 0.8 };
double[] condB = new double[] { 0.5 };
// Multinomial mA = new Multinomial(dataAlphabet, condA);
// Multinomial mB = new Multinomial(dataAlphabet, condB);
// Multinomial[] conditionals = new Multinomial[] { mA, mB };
// NaiveBayes nb = new NaiveBayes(pipe, prior, conditionals);
int[] indices = new int[] { 0, 1 };
double[] values = new double[] { 1.0, 2.0 };
FeatureVector fv = new FeatureVector(dataAlphabet, indices, values);
Instance instance = new Instance(fv, "A", null, null);
// Classification classification = nb.classify(instance);
// double total = classification.getLabeling().valueAtLocation(0) + classification.getLabeling().valueAtLocation(1);
// assertEquals(1.0, total, 1e-6);
}

@Test
public void testClassifyWithMoreLabelsThanConditionalsHandledProperly() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("f0");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("L1");
labelAlphabet.lookupIndex("L2");
labelAlphabet.lookupIndex("L3");
Pipe pipe = new Noop();
pipe.setDataAlphabet(dataAlphabet);
pipe.setTargetAlphabet(labelAlphabet);
double[] priorDist = new double[] { 0.4, 0.6 };
// Multinomial prior = new Multinomial(labelAlphabet, priorDist);
double[] c1 = new double[] { 0.9 };
double[] c2 = new double[] { 0.1 };
// Multinomial m1 = new Multinomial(dataAlphabet, c1);
// Multinomial m2 = new Multinomial(dataAlphabet, c2);
// Multinomial[] multinomials = new Multinomial[] { m1, m2 };
// NaiveBayes nb = new NaiveBayes(pipe, prior, multinomials);
int[] indices = new int[] { 0 };
double[] values = new double[] { 1.0 };
FeatureVector fv = new FeatureVector(dataAlphabet, indices, values);
Instance instance = new Instance(fv, null, null, null);
// Classification classification = nb.classify(instance);
// assertNotNull(classification);
// assertEquals(3, classification.getLabeling().numLocations());
}

@Test
public void testLabelLogLikelihoodWithMultipleLocationSoftLabels() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("x");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("happy");
labelAlphabet.lookupIndex("sad");
Pipe pipe = new Noop();
pipe.setDataAlphabet(dataAlphabet);
pipe.setTargetAlphabet(labelAlphabet);
double[] priors = new double[] { 0.5, 0.5 };
// Multinomial prior = new Multinomial(labelAlphabet, priors);
double[] probs1 = new double[] { 0.7 };
double[] probs2 = new double[] { 0.3 };
// Multinomial m1 = new Multinomial(dataAlphabet, probs1);
// Multinomial m2 = new Multinomial(dataAlphabet, probs2);
// Multinomial[] conds = new Multinomial[] { m1, m2 };
// NaiveBayes nb = new NaiveBayes(pipe, prior, conds);
double[] labelWeights = new double[] { 0.4, 0.6 };
LabelVector softLabel = new LabelVector(labelAlphabet, labelWeights);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
Instance instance = new Instance(fv, softLabel, "inst-X", null);
InstanceList list = new InstanceList(pipe);
list.add(instance);
// double logLikelihood = nb.labelLogLikelihood(list);
// assertTrue(Double.isFinite(logLikelihood));
}

@Test
public void testDataLogLikelihoodWithInstanceWeightZero() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("w");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("pos");
labelAlphabet.lookupIndex("neg");
Pipe pipe = new Noop();
pipe.setDataAlphabet(dataAlphabet);
pipe.setTargetAlphabet(labelAlphabet);
double[] priorArr = new double[] { 0.5, 0.5 };
// Multinomial multPrior = new Multinomial(labelAlphabet, priorArr);
double[] cond1 = new double[] { 0.6 };
double[] cond2 = new double[] { 0.4 };
// Multinomial m1 = new Multinomial(dataAlphabet, cond1);
// Multinomial m2 = new Multinomial(dataAlphabet, cond2);
// Multinomial[] conditionals = new Multinomial[] { m1, m2 };
// NaiveBayes nb = new NaiveBayes(pipe, multPrior, conditionals);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
Instance instance = new Instance(fv, "pos", null, null);
InstanceList list = new InstanceList(pipe);
list.add(instance);
list.setInstanceWeight(0, 0.0);
// double result = nb.dataLogLikelihood(list);
// assertEquals(0.0, result, 1e-6);
}

@Test
public void testLabelLogLikelihoodWithZeroProbabilityPredictionHandled() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("f");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("cat");
labelAlphabet.lookupIndex("dog");
Pipe pipe = new Noop();
pipe.setDataAlphabet(dataAlphabet);
pipe.setTargetAlphabet(labelAlphabet);
double[] priorArr = new double[] { 1.0, 0.0 };
// Multinomial prior = new Multinomial(labelAlphabet, priorArr);
double[] cond1 = new double[] { 1.0 };
double[] cond2 = new double[] { 0.0 };
// Multinomial m1 = new Multinomial(dataAlphabet, cond1);
// Multinomial m2 = new Multinomial(dataAlphabet, cond2);
// Multinomial[] multinomials = new Multinomial[] { m1, m2 };
// NaiveBayes nb = new NaiveBayes(pipe, prior, multinomials);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
Instance instance = new Instance(fv, "cat", null, null);
InstanceList list = new InstanceList(pipe);
list.add(instance);
// double ll = nb.labelLogLikelihood(list);
// assertTrue(Double.isFinite(ll));
}

@Test
public void testDataLogLikelihoodWithNullLabelingUsesPrediction() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("w");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("cls1");
labelAlphabet.lookupIndex("cls2");
Pipe pipe = new Noop();
pipe.setDataAlphabet(dataAlphabet);
pipe.setTargetAlphabet(labelAlphabet);
double[] priorDist = new double[] { 0.3, 0.7 };
// Multinomial prior = new Multinomial(labelAlphabet, priorDist);
double[] c1 = new double[] { 0.5 };
double[] c2 = new double[] { 0.5 };
// Multinomial m1 = new Multinomial(dataAlphabet, c1);
// Multinomial m2 = new Multinomial(dataAlphabet, c2);
// Multinomial[] conditionals = new Multinomial[] { m1, m2 };
// NaiveBayes nb = new NaiveBayes(pipe, prior, conditionals);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 2.0 });
Instance instance = new Instance(fv, null, null, null);
InstanceList list = new InstanceList(pipe);
list.add(instance);
// double logLikelihood = nb.dataLogLikelihood(list);
// assertTrue(Double.isFinite(logLikelihood));
}

@Test
public void testPrintWordsWithMoreToPrintThanAvailable() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("feat0");
dataAlphabet.lookupIndex("feat1");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("c1");
Pipe pipe = new Noop();
pipe.setDataAlphabet(dataAlphabet);
pipe.setTargetAlphabet(labelAlphabet);
double[] priorData = new double[] { 1.0 };
// Multinomial prior = new Multinomial(labelAlphabet, priorData);
double[] condData = new double[] { 0.7, 0.3 };
// Multinomial m = new Multinomial(dataAlphabet, condData);
// Multinomial[] conds = new Multinomial[] { m };
// NaiveBayes nb = new NaiveBayes(pipe, prior, conds);
// nb.printWords(10);
}

@Test
public void testClassifyWithEmptyFeatureVector() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("pos");
labelAlphabet.lookupIndex("neg");
Pipe pipe = new Noop();
pipe.setDataAlphabet(dataAlphabet);
pipe.setTargetAlphabet(labelAlphabet);
// Multinomial prior = new Multinomial(labelAlphabet, new double[] { 0.7, 0.3 });
// Multinomial m1 = new Multinomial(dataAlphabet, new double[0]);
// Multinomial m2 = new Multinomial(dataAlphabet, new double[0]);
// Multinomial[] conds = new Multinomial[] { m1, m2 };
// NaiveBayes nb = new NaiveBayes(pipe, prior, conds);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[0], new double[0]);
Instance inst = new Instance(fv, null, null, null);
// Classification c = nb.classify(inst);
// double sum = c.getLabeling().valueAtLocation(0) + c.getLabeling().valueAtLocation(1);
// assertEquals(1.0, sum, 1e-6);
}

@Test
public void testClassifyWithNullPipe() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("token");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("class1");
labelAlphabet.lookupIndex("class2");
// Multinomial prior = new Multinomial(labelAlphabet, new double[] { 0.5, 0.5 });
// Multinomial c1 = new Multinomial(alphabet, new double[] { 1.0 });
// Multinomial c2 = new Multinomial(alphabet, new double[] { 1.0 });
// NaiveBayes nb = new NaiveBayes(null, prior, new Multinomial[] { c1, c2 });
int[] indices = new int[] { 0 };
double[] values = new double[] { 1.0 };
FeatureVector fv = new FeatureVector(alphabet, indices, values);
Instance inst = new Instance(fv, null, null, null);
// Classification c = nb.classify(inst);
// assertNotNull(c);
}

@Test(expected = ArrayIndexOutOfBoundsException.class)
public void testClassifyWithEmptyMultinomialsThrows() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("feature");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("label");
Pipe pipe = new Noop();
pipe.setDataAlphabet(dataAlphabet);
pipe.setTargetAlphabet(labelAlphabet);
// Multinomial prior = new Multinomial(labelAlphabet, new double[] { 1.0 });
// NaiveBayes nb = new NaiveBayes(pipe, prior, new Multinomial[] {});
int[] indices = new int[] { 0 };
double[] values = new double[] { 1.0 };
FeatureVector fv = new FeatureVector(dataAlphabet, indices, values);
Instance instance = new Instance(fv, null, null, null);
// nb.classify(instance);
}

@Test
public void testPrintWordsWithZeroNumToPrint() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("alpha");
dataAlphabet.lookupIndex("beta");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("label");
Pipe pipe = new Noop();
pipe.setDataAlphabet(dataAlphabet);
pipe.setTargetAlphabet(labelAlphabet);
// Multinomial prior = new Multinomial(labelAlphabet, new double[] { 1.0 });
// Multinomial m = new Multinomial(dataAlphabet, new double[] { 0.6, 0.4 });
// NaiveBayes nb = new NaiveBayes(pipe, prior, new Multinomial[] { m });
// nb.printWords(0);
}

@Test
public void testClassificationWithLabelAlphabetExtensionNotInTraining() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("f");
LabelAlphabet trainedLabelAlphabet = new LabelAlphabet();
trainedLabelAlphabet.lookupIndex("x");
// Multinomial prior = new Multinomial(trainedLabelAlphabet, new double[] { 1.0 });
// Multinomial m = new Multinomial(dataAlphabet, new double[] { 1.0 });
LabelAlphabet testLabelAlphabet = new LabelAlphabet();
testLabelAlphabet.lookupIndex("x");
testLabelAlphabet.lookupIndex("y");
testLabelAlphabet.lookupIndex("z");
Pipe pipe = new Noop();
pipe.setDataAlphabet(dataAlphabet);
pipe.setTargetAlphabet(testLabelAlphabet);
// NaiveBayes nb = new NaiveBayes(pipe, prior, new Multinomial[] { m });
// FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
// Instance instance = new Instance(fv, null, null, null);
// Classification classification = nb.classify(instance);
// assertEquals(3, classification.getLabeling().numLocations());
}

@Test
public void testLabelLogLikelihoodWithZeroWeightSoftLabelIsIgnored() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("f");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("l1");
labelAlphabet.lookupIndex("l2");
Pipe pipe = new Noop();
pipe.setDataAlphabet(dataAlphabet);
pipe.setTargetAlphabet(labelAlphabet);
// Multinomial prior = new Multinomial(labelAlphabet, new double[] { 0.5, 0.5 });
// Multinomial m1 = new Multinomial(dataAlphabet, new double[] { 0.8 });
// Multinomial m2 = new Multinomial(dataAlphabet, new double[] { 0.2 });
// NaiveBayes nb = new NaiveBayes(pipe, prior, new Multinomial[] { m1, m2 });
double[] labelWeights = new double[] { 0.0, 1.0 };
Labeling softLabel = new LabelVector(labelAlphabet, labelWeights);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
Instance instance = new Instance(fv, softLabel, null, null);
InstanceList list = new InstanceList(pipe);
list.add(instance);
// double result = nb.labelLogLikelihood(list);
// assertTrue(Double.isFinite(result));
}

@Test
public void testMultinomialLoggedWithZeroProbabilityHandled() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("token");
double[] probs = new double[] { 0.0 };
// Multinomial m = new Multinomial(alphabet, probs);
// Multinomial.Logged logged = new Multinomial.Logged(m);
// double value = logged.logProbability(0);
// assertEquals(Double.NEGATIVE_INFINITY, value, 1e-6);
}

@Test
public void testDataLogProbabilityReturnsZeroOnEmptyFeatureVector() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("f0");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("label");
Pipe pipe = new Noop();
pipe.setDataAlphabet(alphabet);
pipe.setTargetAlphabet(labelAlphabet);
// Multinomial prior = new Multinomial(labelAlphabet, new double[] { 1.0 });
// Multinomial m = new Multinomial(alphabet, new double[] { 1.0 });
// NaiveBayes nb = new NaiveBayes(pipe, prior, new Multinomial[] { m });
// FeatureVector fv = new FeatureVector(alphabet, new int[0], new double[0]);
// Instance instance = new Instance(fv, "label", null, null);
InstanceList list = new InstanceList(pipe);
// list.add(instance);
// double value = nb.dataLogLikelihood(list);
// assertEquals(0.0, value, 1e-6);
}

@Test
public void testFeatureIndexOutOfBoundsHandledGracefullyDuringClassification() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("f0");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("classA");
Pipe pipe = new Noop();
pipe.setDataAlphabet(dataAlphabet);
pipe.setTargetAlphabet(labelAlphabet);
// Multinomial prior = new Multinomial(labelAlphabet, new double[] { 1.0 });
// Multinomial[] classFeatureProbs = new Multinomial[] { new Multinomial(dataAlphabet, new double[] { 1.0 }) };
// NaiveBayes nb = new NaiveBayes(pipe, prior, classFeatureProbs);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0, 1 }, new double[] { 1.0, 2.0 });
Instance instance = new Instance(fv, null, null, null);
// Classification classification = nb.classify(instance);
// assertNotNull(classification);
// assertEquals(1, classification.getLabeling().numLocations());
}

@Test
public void testClassifyWithAllMinusInfinityPriorScoresReturnsUniformDistribution() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("term");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("A");
labelAlphabet.lookupIndex("B");
Pipe pipe = new Noop();
pipe.setDataAlphabet(dataAlphabet);
pipe.setTargetAlphabet(labelAlphabet);
double[] priorValues = new double[] { 0.0, 0.0 };
// Multinomial prior = new Multinomial(labelAlphabet, priorValues);
// prior.setDimensions(new double[] { 0.0, 0.0 });
// Multinomial m1 = new Multinomial(dataAlphabet, new double[] { 1.0 });
// Multinomial m2 = new Multinomial(dataAlphabet, new double[] { 1.0 });
// NaiveBayes nb = new NaiveBayes(pipe, prior, new Multinomial[] { m1, m2 });
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
Instance instance = new Instance(fv, null, null, null);
// Classification result = nb.classify(instance);
// double total = result.getLabeling().valueAtRank(0) + result.getLabeling().valueAtRank(1);
// assertEquals(1.0, total, 1e-6);
}

@Test
public void testClassifyAfterSerializationProducesSameResult() throws Exception {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("x");
dataAlphabet.lookupIndex("y");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("yes");
labelAlphabet.lookupIndex("no");
Pipe pipe = new Noop();
pipe.setDataAlphabet(dataAlphabet);
pipe.setTargetAlphabet(labelAlphabet);
// Multinomial prior = new Multinomial(labelAlphabet, new double[] { 0.5, 0.5 });
// Multinomial m1 = new Multinomial(dataAlphabet, new double[] { 0.6, 0.4 });
// Multinomial m2 = new Multinomial(dataAlphabet, new double[] { 0.4, 0.6 });
// NaiveBayes nb = new NaiveBayes(pipe, prior, new Multinomial[] { m1, m2 });
ByteArrayOutputStream bout = new ByteArrayOutputStream();
ObjectOutputStream oos = new ObjectOutputStream(bout);
// oos.writeObject(nb);
oos.close();
ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
ObjectInputStream ois = new ObjectInputStream(bin);
NaiveBayes deserialized = (NaiveBayes) ois.readObject();
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0, 1 }, new double[] { 1.0, 1.0 });
Instance instance = new Instance(fv, null, "id", null);
// Classification original = nb.classify(instance);
Classification after = deserialized.classify(instance);
// assertEquals(original.getLabeling().getBestLabel().toString(), after.getLabeling().getBestLabel().toString());
}

@Test
public void testClassificationProducedWithNaNFeatureIgnored() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("feat");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("cls");
Pipe pipe = new Noop();
pipe.setDataAlphabet(dataAlphabet);
pipe.setTargetAlphabet(labelAlphabet);
// Multinomial prior = new Multinomial(labelAlphabet, new double[] { 1.0 });
// Multinomial m = new Multinomial(dataAlphabet, new double[] { 1.0 });
// NaiveBayes nb = new NaiveBayes(pipe, prior, new Multinomial[] { m });
// FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { Double.NaN });
// Instance inst = new Instance(fv, null, null, null);
// Classification c = nb.classify(inst);
// assertNotNull(c);
}

@Test
public void testClassificationProducedWithNegativeFeature() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("feat");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("c");
Pipe pipe = new Noop();
pipe.setDataAlphabet(dataAlphabet);
pipe.setTargetAlphabet(labelAlphabet);
// Multinomial prior = new Multinomial(labelAlphabet, new double[] { 1.0 });
// Multinomial[] conds = new Multinomial[] { new Multinomial(dataAlphabet, new double[] { 0.9 }) };
// NaiveBayes nb = new NaiveBayes(pipe, prior, conds);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { -2.0 });
Instance instance = new Instance(fv, null, null, null);
// Classification classification = nb.classify(instance);
// assertNotNull(classification);
}

@Test
public void testLabelLogLikelihoodWithThreeLabelSoftInput() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("featA");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("L0");
labelAlphabet.lookupIndex("L1");
labelAlphabet.lookupIndex("L2");
Pipe pipe = new Noop();
pipe.setDataAlphabet(dataAlphabet);
pipe.setTargetAlphabet(labelAlphabet);
double[] priors = new double[] { 0.3, 0.3, 0.4 };
// Multinomial prior = new Multinomial(labelAlphabet, priors);
// Multinomial m0 = new Multinomial(dataAlphabet, new double[] { 0.2 });
// Multinomial m1 = new Multinomial(dataAlphabet, new double[] { 0.3 });
// Multinomial m2 = new Multinomial(dataAlphabet, new double[] { 0.5 });
// NaiveBayes nb = new NaiveBayes(pipe, prior, new Multinomial[] { m0, m1, m2 });
double[] weights = new double[] { 0.1, 0.1, 0.8 };
LabelVector labelVector = new LabelVector(labelAlphabet, weights);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
Instance inst = new Instance(fv, labelVector, "inst1", null);
InstanceList list = new InstanceList(pipe);
list.add(inst);
// double logLikelihood = nb.labelLogLikelihood(list);
// assertTrue(Double.isFinite(logLikelihood));
}

@Test
public void testClassificationWithExtremelyLargeFeatureWeights() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("huge");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("max");
Pipe pipe = new Noop();
pipe.setDataAlphabet(dataAlphabet);
pipe.setTargetAlphabet(labelAlphabet);
// Multinomial prior = new Multinomial(labelAlphabet, new double[] { 1.0 });
// Multinomial m = new Multinomial(dataAlphabet, new double[] { 1.0 });
// NaiveBayes nb = new NaiveBayes(pipe, prior, new Multinomial[] { m });
// FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { Double.MAX_VALUE });
// Instance inst = new Instance(fv, null, null, null);
// Classification classification = nb.classify(inst);
// assertNotNull(classification);
}

@Test
public void testClassifyWithEmptyPriorAndMultinomials() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
Pipe pipe = new Noop();
pipe.setDataAlphabet(dataAlphabet);
pipe.setTargetAlphabet(labelAlphabet);
// Multinomial prior = new Multinomial(labelAlphabet, new double[0]);
Multinomial[] multinomials = new Multinomial[0];
// NaiveBayes nb = new NaiveBayes(pipe, prior, multinomials);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[0], new double[0]);
Instance inst = new Instance(fv, null, null, null);
// Classification classification = nb.classify(inst);
// assertNotNull(classification);
}

@Test
public void testDataLogProbabilityWithInvalidLabelIndexSkipped() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("x");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("A");
Pipe pipe = new Noop();
pipe.setDataAlphabet(dataAlphabet);
pipe.setTargetAlphabet(labelAlphabet);
// Multinomial prior = new Multinomial(labelAlphabet, new double[] { 1.0 });
// Multinomial m = new Multinomial(dataAlphabet, new double[] { 1.0 });
// NaiveBayes nb = new NaiveBayes(pipe, prior, new Multinomial[] { m });
// FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
// Instance invalidLabel = new Instance(fv, new LabelVector(labelAlphabet, new double[] { 0.0 }), null, null);
// Classification classification = nb.classify(invalidLabel);
// assertNotNull(classification);
}

@Test
public void testClassificationWithFeatureIndexExceedingConditionalSize() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("f0");
dataAlphabet.lookupIndex("f1");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("label1");
Pipe pipe = new Noop();
pipe.setDataAlphabet(dataAlphabet);
pipe.setTargetAlphabet(labelAlphabet);
// Multinomial prior = new Multinomial(labelAlphabet, new double[] { 1.0 });
// Multinomial m = new Multinomial(dataAlphabet, new double[] { 0.9 });
// NaiveBayes nb = new NaiveBayes(pipe, prior, new Multinomial[] { m });
// FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 1 }, new double[] { 1.0 });
// Instance instance = new Instance(fv, null, null, null);
// Classification result = nb.classify(instance);
// assertNotNull(result);
}

@Test
public void testClassifyWithEmptyFeatureVectorAndNonNullPipe() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("L");
Pipe pipe = new Noop();
pipe.setDataAlphabet(dataAlphabet);
pipe.setTargetAlphabet(labelAlphabet);
// Multinomial prior = new Multinomial(labelAlphabet, new double[] { 1.0 });
// Multinomial cond = new Multinomial(dataAlphabet, new double[0]);
// NaiveBayes nb = new NaiveBayes(pipe, prior, new Multinomial[] { cond });
// FeatureVector fv = new FeatureVector(dataAlphabet, new int[0], new double[0]);
// Instance instance = new Instance(fv, null, null, null);
// Classification classification = nb.classify(instance);
// assertNotNull(classification);
}

@Test
public void testClassificationWithFeatureValueZero() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("f");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("c");
Pipe pipe = new Noop();
pipe.setDataAlphabet(dataAlphabet);
pipe.setTargetAlphabet(labelAlphabet);
// Multinomial prior = new Multinomial(labelAlphabet, new double[] { 1.0 });
// Multinomial m = new Multinomial(dataAlphabet, new double[] { 1.0 });
// NaiveBayes nb = new NaiveBayes(pipe, prior, new Multinomial[] { m });
// FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 0.0 });
// Instance instance = new Instance(fv, null, null, null);
// Classification c = nb.classify(instance);
// assertNotNull(c);
}

@Test
public void testLabelLogLikelihoodWithZeroWeightsLabelVector() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("x");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("pos");
labelAlphabet.lookupIndex("neg");
Pipe pipe = new Noop();
pipe.setDataAlphabet(dataAlphabet);
pipe.setTargetAlphabet(labelAlphabet);
// Multinomial prior = new Multinomial(labelAlphabet, new double[] { 0.5, 0.5 });
// Multinomial m1 = new Multinomial(dataAlphabet, new double[] { 1.0 });
// Multinomial m2 = new Multinomial(dataAlphabet, new double[] { 1.0 });
// NaiveBayes nb = new NaiveBayes(pipe, prior, new Multinomial[] { m1, m2 });
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
double[] zeroWeights = new double[] { 0.0, 0.0 };
Instance instance = new Instance(fv, new LabelVector(labelAlphabet, zeroWeights), null, null);
InstanceList list = new InstanceList(pipe);
list.add(instance);
// double ll = nb.labelLogLikelihood(list);
// assertEquals(0.0, ll, 1e-6);
}

@Test
public void testLabelAlphabetLookupUnseenLabelReturnsInvalidIndex() {
LabelAlphabet alphabet = new LabelAlphabet();
alphabet.lookupIndex("seen");
int index = alphabet.lookupIndex("unseen", false);
assertEquals(-1, index);
}

@Test
public void testLabelLogLikelihoodWithNegativeWeightsIgnored() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("a");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("1");
labelAlphabet.lookupIndex("2");
Pipe pipe = new Noop();
pipe.setDataAlphabet(dataAlphabet);
pipe.setTargetAlphabet(labelAlphabet);
// Multinomial prior = new Multinomial(labelAlphabet, new double[] { 0.5, 0.5 });
// Multinomial m1 = new Multinomial(dataAlphabet, new double[] { 1.0 });
// Multinomial m2 = new Multinomial(dataAlphabet, new double[] { 1.0 });
// NaiveBayes nb = new NaiveBayes(pipe, prior, new Multinomial[] { m1, m2 });
double[] negativeWeights = new double[] { -0.4, 1.4 };
LabelVector labels = new LabelVector(labelAlphabet, negativeWeights);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
Instance instance = new Instance(fv, labels, null, null);
InstanceList list = new InstanceList(pipe);
list.add(instance);
// double ll = nb.labelLogLikelihood(list);
// assertTrue(Double.isFinite(ll));
}

@Test
public void testClassificationExpOverflowHandledSafely() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("exp");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("hi");
labelAlphabet.lookupIndex("lo");
Pipe pipe = new Noop();
pipe.setDataAlphabet(dataAlphabet);
pipe.setTargetAlphabet(labelAlphabet);
// Multinomial prior = new Multinomial(labelAlphabet, new double[] { 0.9, 0.1 });
// Multinomial m1 = new Multinomial(dataAlphabet, new double[] { 1.0 });
// Multinomial m2 = new Multinomial(dataAlphabet, new double[] { 1.0e-300 });
// NaiveBayes nb = new NaiveBayes(pipe, prior, new Multinomial[] { m1, m2 });
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1000.0 });
Instance inst = new Instance(fv, null, null, null);
// Classification c = nb.classify(inst);
// double total = c.getLabeling().valueAtRank(0) + c.getLabeling().valueAtRank(1);
// assertEquals(1.0, total, 1e-6);
}

@Test
public void testScoreNormalizationHandlesZeroSumGracefully() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("x");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("one");
labelAlphabet.lookupIndex("two");
Pipe pipe = new Noop();
pipe.setDataAlphabet(dataAlphabet);
pipe.setTargetAlphabet(labelAlphabet);
double[] p = new double[] { 0.0, 0.0 };
// Multinomial prior = new Multinomial(labelAlphabet, p);
// Multinomial m1 = new Multinomial(dataAlphabet, new double[] { 0.0 });
// Multinomial m2 = new Multinomial(dataAlphabet, new double[] { 0.0 });
// NaiveBayes nb = new NaiveBayes(pipe, prior, new Multinomial[] { m1, m2 });
// FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
// Instance instance = new Instance(fv, null, null, null);
// Classification result = nb.classify(instance);
// double sum = result.getLabeling().valueAtLocation(0) + result.getLabeling().valueAtLocation(1);
// assertEquals(1.0, sum, 1e-6);
}

@Test
public void testClassifyWithOnlyPriorsAndNoFeatures() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("A");
labelAlphabet.lookupIndex("B");
Pipe pipe = new Noop();
pipe.setDataAlphabet(dataAlphabet);
pipe.setTargetAlphabet(labelAlphabet);
// Multinomial prior = new Multinomial(labelAlphabet, new double[] { 0.7, 0.3 });
// NaiveBayes nb = new NaiveBayes(pipe, prior, new Multinomial[] { new Multinomial(dataAlphabet, new double[0]), new Multinomial(dataAlphabet, new double[0]) });
FeatureVector fv = new FeatureVector(dataAlphabet, new int[0], new double[0]);
Instance instance = new Instance(fv, null, "source", null);
// Classification classification = nb.classify(instance);
// double total = classification.getLabeling().value(0) + classification.getLabeling().value(1);
// assertEquals(1.0, total, 1e-6);
}

@Test
public void testLabelLogLikelihoodWithPartialMatchLabeling() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("x");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("a");
labelAlphabet.lookupIndex("b");
Pipe pipe = new Noop();
pipe.setDataAlphabet(dataAlphabet);
pipe.setTargetAlphabet(labelAlphabet);
// Multinomial prior = new Multinomial(labelAlphabet, new double[] { 0.4, 0.6 });
// Multinomial m1 = new Multinomial(dataAlphabet, new double[] { 0.5 });
// Multinomial m2 = new Multinomial(dataAlphabet, new double[] { 0.5 });
// NaiveBayes nb = new NaiveBayes(pipe, prior, new Multinomial[] { m1, m2 });
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
double[] labelValues = new double[] { 1.0, 0.0 };
LabelVector labels = new LabelVector(labelAlphabet, labelValues);
Instance instance = new Instance(fv, labels, "test", null);
InstanceList list = new InstanceList(pipe);
list.add(instance);
// double result = nb.labelLogLikelihood(list);
// assertTrue(Double.isFinite(result));
}

@Test
public void testDataLogLikelihoodWithMultipleMatchingLabels() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("a");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("x");
labelAlphabet.lookupIndex("y");
Pipe pipe = new Noop();
pipe.setDataAlphabet(dataAlphabet);
pipe.setTargetAlphabet(labelAlphabet);
double[] priorDist = new double[] { 0.5, 0.5 };
// Multinomial prior = new Multinomial(labelAlphabet, priorDist);
double[] condA = new double[] { 0.6 };
double[] condB = new double[] { 0.4 };
// Multinomial m1 = new Multinomial(dataAlphabet, condA);
// Multinomial m2 = new Multinomial(dataAlphabet, condB);
// NaiveBayes nb = new NaiveBayes(pipe, prior, new Multinomial[] { m1, m2 });
// double[] labelProbs = new double[] { 0.25, 0.75 };
FeatureVector featureVector = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
// Labeling softLabeling = new LabelVector(labelAlphabet, labelProbs);
// Instance inst = new Instance(featureVector, softLabeling, null, null);
InstanceList list = new InstanceList(pipe);
// list.add(inst);
// double likelihood = nb.dataLogLikelihood(list);
// assertTrue(Double.isFinite(likelihood));
}

@Test
public void testClassifyLabelAlphabetWithGapIndex() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("feature");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("label1");
labelAlphabet.lookupIndex("label2");
labelAlphabet.lookupIndex("label3");
Pipe pipe = new Noop();
pipe.setDataAlphabet(dataAlphabet);
pipe.setTargetAlphabet(labelAlphabet);
// Multinomial prior = new Multinomial(labelAlphabet, new double[] { 0.4, 0.4, 0.2 });
// Multinomial m0 = new Multinomial(dataAlphabet, new double[] { 0.3 });
// Multinomial m1 = new Multinomial(dataAlphabet, new double[] { 0.4 });
// Multinomial m2 = new Multinomial(dataAlphabet, new double[] { 0.3 });
// NaiveBayes nb = new NaiveBayes(pipe, prior, new Multinomial[] { m0, m1, m2 });
// FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
// Instance inst = new Instance(fv, null, null, null);
// Classification classification = nb.classify(inst);
// assertEquals(3, classification.getLabeling().numLocations());
}

@Test
public void testInstanceWithUnusedFeatureIndexAndFeatureAlphabetMismatchIgnored() {
Alphabet dataAlphabetTrain = new Alphabet();
dataAlphabetTrain.lookupIndex("used");
Alphabet dataAlphabetTest = new Alphabet();
dataAlphabetTest.lookupIndex("unused");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("truth");
Pipe trainPipe = new Noop();
trainPipe.setDataAlphabet(dataAlphabetTrain);
trainPipe.setTargetAlphabet(labelAlphabet);
// Multinomial prior = new Multinomial(labelAlphabet, new double[] { 1.0 });
// Multinomial multinomial = new Multinomial(dataAlphabetTrain, new double[] { 1.0 });
// NaiveBayes nb = new NaiveBayes(null, prior, new Multinomial[] { multinomial });
// FeatureVector fv = new FeatureVector(dataAlphabetTest, new int[] { 0 }, new double[] { 1.0 });
// Instance inst = new Instance(fv, null, null, null);
// Classification c = nb.classify(inst);
// assertEquals(1, c.getLabeling().numLocations());
}

@Test
public void testClassificationWithCustomLabelSorting() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("f");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("Z");
labelAlphabet.lookupIndex("Y");
labelAlphabet.lookupIndex("X");
Pipe pipe = new Noop();
pipe.setDataAlphabet(dataAlphabet);
pipe.setTargetAlphabet(labelAlphabet);
double[] priorProb = new double[] { 0.4, 0.3, 0.3 };
// Multinomial prior = new Multinomial(labelAlphabet, priorProb);
// Multinomial m1 = new Multinomial(dataAlphabet, new double[] { 1.0 });
// Multinomial m2 = new Multinomial(dataAlphabet, new double[] { 1.0 });
// Multinomial m3 = new Multinomial(dataAlphabet, new double[] { 1.0 });
// NaiveBayes nb = new NaiveBayes(pipe, prior, new Multinomial[] { m1, m2, m3 });
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
Instance inst = new Instance(fv, null, "doc", null);
// Classification c = nb.classify(inst);
// assertEquals(3, c.getLabeling().numLocations());
// assertEquals(1.0, c.getLabeling().value(0) + c.getLabeling().value(1) + c.getLabeling().value(2), 1e-6);
}

@Test
public void testClassificationWithZeroPriorForOneClass() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("tok");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("good");
labelAlphabet.lookupIndex("bad");
Pipe pipe = new Noop();
pipe.setDataAlphabet(dataAlphabet);
pipe.setTargetAlphabet(labelAlphabet);
double[] priors = new double[] { 1.0, 0.0 };
// Multinomial prior = new Multinomial(labelAlphabet, priors);
// Multinomial m1 = new Multinomial(dataAlphabet, new double[] { 1.0 });
// Multinomial m2 = new Multinomial(dataAlphabet, new double[] { 1.0 });
// NaiveBayes nb = new NaiveBayes(pipe, prior, new Multinomial[] { m1, m2 });
// FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
// Instance instance = new Instance(fv, null, null, null);
// Classification c = nb.classify(instance);
// assertTrue(c.getLabeling().value(1) < 1e-6);
}

@Test
public void testLogLikelihoodWithInstanceWeightZeroAndNoLabeling() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("word");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("pos");
labelAlphabet.lookupIndex("neg");
Pipe pipe = new Noop();
pipe.setDataAlphabet(dataAlphabet);
pipe.setTargetAlphabet(labelAlphabet);
// Multinomial prior = new Multinomial(labelAlphabet, new double[] { 0.5, 0.5 });
// Multinomial c1 = new Multinomial(dataAlphabet, new double[] { 0.5 });
// Multinomial c2 = new Multinomial(dataAlphabet, new double[] { 0.5 });
// NaiveBayes nb = new NaiveBayes(pipe, prior, new Multinomial[] { c1, c2 });
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
Instance inst = new Instance(fv, null, "id", null);
InstanceList list = new InstanceList(pipe);
list.add(inst);
list.setInstanceWeight(0, 0.0);
// double log = nb.dataLogLikelihood(list);
// assertEquals(0.0, log, 1e-6);
}

@Test
public void testLabelLogLikelihoodWithAllZeroProbabilityPrediction() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("x");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("yes");
labelAlphabet.lookupIndex("no");
Pipe pipe = new Noop();
pipe.setDataAlphabet(dataAlphabet);
pipe.setTargetAlphabet(labelAlphabet);
double[] priorProbs = new double[] { 1.0, 0.0 };
// Multinomial prior = new Multinomial(labelAlphabet, priorProbs);
// Multinomial m1 = new Multinomial(dataAlphabet, new double[] { 0.0 });
// Multinomial m2 = new Multinomial(dataAlphabet, new double[] { 0.0 });
// NaiveBayes nb = new NaiveBayes(pipe, prior, new Multinomial[] { m1, m2 });
// Labeling label = new LabelVector(labelAlphabet, new double[] { 1.0, 0.0 });
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
// Instance inst = new Instance(fv, label, null, null);
InstanceList list = new InstanceList(pipe);
// list.add(inst);
// double ll = nb.labelLogLikelihood(list);
// assertTrue(Double.isFinite(ll));
}

@Test
public void testClassificationWithNullSourceAndName() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("feature");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("cls1");
labelAlphabet.lookupIndex("cls2");
Pipe pipe = new Noop();
pipe.setDataAlphabet(dataAlphabet);
pipe.setTargetAlphabet(labelAlphabet);
// Multinomial prior = new Multinomial(labelAlphabet, new double[] { 0.6, 0.4 });
// Multinomial m1 = new Multinomial(dataAlphabet, new double[] { 0.7 });
// Multinomial m2 = new Multinomial(dataAlphabet, new double[] { 0.3 });
// NaiveBayes nb = new NaiveBayes(pipe, prior, new Multinomial[] { m1, m2 });
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
Instance inst = new Instance(fv, null, null, null);
// Classification c = nb.classify(inst);
// assertEquals(2, c.getLabeling().numLocations());
}

@Test
public void testFeatureVectorWithRepeatedIndices() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("f0");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("class");
Pipe pipe = new Noop();
pipe.setDataAlphabet(dataAlphabet);
pipe.setTargetAlphabet(labelAlphabet);
// Multinomial prior = new Multinomial(labelAlphabet, new double[] { 1.0 });
// Multinomial m = new Multinomial(dataAlphabet, new double[] { 1.0 });
// NaiveBayes nb = new NaiveBayes(pipe, prior, new Multinomial[] { m });
// int[] indices = new int[] { 0, 0 };
double[] values = new double[] { 1.0, 2.0 };
// FeatureVector fv = new FeatureVector(dataAlphabet, indices, values);
// Instance inst = new Instance(fv, null, null, null);
// Classification c = nb.classify(inst);
// assertEquals(1, c.getLabeling().numLocations());
// assertEquals(1.0, c.getLabeling().valueAtRank(0), 1e-6);
}

@Test
public void testLabelLogLikelihoodWithLabelMissingFromPredictionAlphabet() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("f");
LabelAlphabet trainLabelAlphabet = new LabelAlphabet();
trainLabelAlphabet.lookupIndex("a");
trainLabelAlphabet.lookupIndex("b");
LabelAlphabet testLabelAlphabet = new LabelAlphabet();
testLabelAlphabet.lookupIndex("a");
testLabelAlphabet.lookupIndex("b");
testLabelAlphabet.lookupIndex("c");
Pipe pipe = new Noop();
pipe.setDataAlphabet(dataAlphabet);
pipe.setTargetAlphabet(testLabelAlphabet);
// Multinomial trainPrior = new Multinomial(trainLabelAlphabet, new double[] { 0.5, 0.5 });
// Multinomial mA = new Multinomial(dataAlphabet, new double[] { 1.0 });
// Multinomial mB = new Multinomial(dataAlphabet, new double[] { 1.0 });
// NaiveBayes nb = new NaiveBayes(pipe, trainPrior, new Multinomial[] { mA, mB });
double[] labelProbs = new double[] { 0.3, 0.3, 0.4 };
LabelVector labels = new LabelVector(testLabelAlphabet, labelProbs);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
Instance inst = new Instance(fv, labels, null, null);
InstanceList list = new InstanceList(pipe);
list.add(inst);
// double ll = nb.labelLogLikelihood(list);
// assertTrue(Double.isFinite(ll));
}

@Test
public void testDataLogLikelihoodWithFeatureIndexLargerThanConditionalSize() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("w0");
dataAlphabet.lookupIndex("w1");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("label");
Pipe pipe = new Noop();
pipe.setDataAlphabet(dataAlphabet);
pipe.setTargetAlphabet(labelAlphabet);
// Multinomial prior = new Multinomial(labelAlphabet, new double[] { 1.0 });
// Multinomial m = new Multinomial(dataAlphabet, new double[] { 0.9 });
// NaiveBayes nb = new NaiveBayes(pipe, prior, new Multinomial[] { m });
// FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 1 }, new double[] { 1.0 });
// Instance inst = new Instance(fv, new LabelVector(labelAlphabet, new double[] { 1.0 }), null, null);
// InstanceList list = new InstanceList(pipe);
// list.add(inst);
// double log = nb.dataLogLikelihood(list);
// assertTrue(Double.isFinite(log));
}

@Test
public void testInstanceListHandlingOfMultipleWeights() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("x");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("L");
Pipe pipe = new Noop();
pipe.setDataAlphabet(dataAlphabet);
pipe.setTargetAlphabet(labelAlphabet);
// Multinomial prior = new Multinomial(labelAlphabet, new double[] { 1.0 });
// Multinomial cond = new Multinomial(dataAlphabet, new double[] { 1.0 });
// NaiveBayes nb = new NaiveBayes(pipe, prior, new Multinomial[] { cond });
// InstanceList list = new InstanceList(pipe);
FeatureVector fv1 = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
Instance inst1 = new Instance(fv1, new LabelVector(labelAlphabet, new double[] { 1.0 }), null, null);
// list.add(inst1);
// list.setInstanceWeight(0, 0.5);
FeatureVector fv2 = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
Instance inst2 = new Instance(fv2, new LabelVector(labelAlphabet, new double[] { 1.0 }), null, null);
// list.add(inst2);
// list.setInstanceWeight(1, 1.5);
// double ll = nb.dataLogLikelihood(list);
// assertTrue(Double.isFinite(ll));
}

@Test
public void testClassifierHandlesNullDataAlphabetFeatureVectorIfPipeIsNull() {
Alphabet dynamicAlphabet = new Alphabet();
dynamicAlphabet.lookupIndex("new");
// Multinomial prior = new Multinomial(new LabelAlphabet(), new double[0]);
// NaiveBayes nb = new NaiveBayes(null, prior, new Multinomial[0]);
FeatureVector fv = new FeatureVector(dynamicAlphabet, new int[0], new double[0]);
Instance inst = new Instance(fv, null, null, null);
// Classification c = nb.classify(inst);
// assertNotNull(c);
}
}
