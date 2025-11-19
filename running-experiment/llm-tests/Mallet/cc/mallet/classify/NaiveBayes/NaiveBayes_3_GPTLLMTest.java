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

public class NaiveBayes_3_GPTLLMTest {

@Test
public void testConstructorWithLogged() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
// Multinomial prior = new Multinomial(labelAlphabet, new double[] { 0.7, 0.3 });
// labelAlphabet.lookupIndex("yes");
labelAlphabet.lookupIndex("no");
Multinomial[] conditionals = new Multinomial[2];
double[] probs1 = new double[] { 0.6, 0.4 };
dataAlphabet.lookupIndex("word1");
dataAlphabet.lookupIndex("word2");
// conditionals[0] = new Multinomial(dataAlphabet, probs1);
double[] probs2 = new double[] { 0.3, 0.7 };
// conditionals[1] = new Multinomial(dataAlphabet, probs2);
// Multinomial.Logged loggedPrior = new Multinomial.Logged(prior);
Multinomial.Logged[] loggedConditionals = new Multinomial.Logged[2];
loggedConditionals[0] = new Multinomial.Logged(conditionals[0]);
loggedConditionals[1] = new Multinomial.Logged(conditionals[1]);
Pipe pipe = new SerialPipes(new ArrayList<>());
// NaiveBayes nb = new NaiveBayes(pipe, loggedPrior, loggedConditionals);
// assertNotNull(nb.getPriors());
// assertNotNull(nb.getMultinomials());
// assertEquals(2, nb.getMultinomials().length);
}

@Test
public void testClassifySimpleInstance() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("pos");
labelAlphabet.lookupIndex("neg");
double[] priorProbs = new double[] { 0.5, 0.5 };
// Multinomial prior = new Multinomial(labelAlphabet, priorProbs);
dataAlphabet.lookupIndex("great");
dataAlphabet.lookupIndex("bad");
double[] class0 = new double[] { 0.8, 0.2 };
double[] class1 = new double[] { 0.2, 0.8 };
Multinomial[] conds = new Multinomial[2];
// conds[0] = new Multinomial(dataAlphabet, class0);
// conds[1] = new Multinomial(dataAlphabet, class1);
ArrayList<Pipe> pipeList = new ArrayList<>();
pipeList.add(new cc.mallet.pipe.CharSequence2TokenSequence("\\p{L}+"));
pipeList.add(new TokenSequenceLowercase());
pipeList.add(new TokenSequenceRemoveStopwords(false, false));
pipeList.add(new TokenSequence2FeatureVectorSequence(dataAlphabet));
Pipe pipe = new SerialPipes(pipeList);
// NaiveBayes classifier = new NaiveBayes(pipe, prior, conds);
Instance instance = new Instance("great", null, "test", null);
Instance processed = pipe.instanceFrom(instance);
// Classification classification = classifier.classify(processed);
// assertNotNull(classification);
// assertEquals(2, classification.getLabeling().numLocations());
}

@Test
public void testLabelLogLikelihoodWithKnownLabels() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("fake");
labelAlphabet.lookupIndex("real");
// Multinomial prior = new Multinomial(labelAlphabet, new double[] { 0.6, 0.4 });
// dataAlphabet.lookupIndex("fake");
dataAlphabet.lookupIndex("real");
Multinomial[] cond = new Multinomial[2];
// cond[0] = new Multinomial(dataAlphabet, new double[] { 0.7, 0.3 });
// cond[1] = new Multinomial(dataAlphabet, new double[] { 0.2, 0.8 });
ArrayList<Pipe> pipeList = new ArrayList<>();
pipeList.add(new cc.mallet.pipe.CharSequence2TokenSequence("\\p{L}+"));
pipeList.add(new TokenSequenceLowercase());
pipeList.add(new TokenSequenceRemoveStopwords(false, false));
pipeList.add(new TokenSequence2FeatureVectorSequence(dataAlphabet));
Pipe pipe = new SerialPipes(pipeList);
pipe.setTargetProcessing(true);
// NaiveBayes classifier = new NaiveBayes(pipe, prior, cond);
InstanceList list = new InstanceList(pipe);
list.addThruPipe(new Instance("fake fake", "fake", null, null));
list.addThruPipe(new Instance("real real", "real", null, null));
// double result = classifier.labelLogLikelihood(list);
// assertTrue(Double.isFinite(result));
}

@Test
public void testDataLogLikelihoodWithoutLabels() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("label1");
labelAlphabet.lookupIndex("label2");
// Multinomial prior = new Multinomial(labelAlphabet, new double[] { 0.5, 0.5 });
// dataAlphabet.lookupIndex("hello");
dataAlphabet.lookupIndex("world");
Multinomial[] cond = new Multinomial[2];
// cond[0] = new Multinomial(dataAlphabet, new double[] { 0.9, 0.1 });
// cond[1] = new Multinomial(dataAlphabet, new double[] { 0.1, 0.9 });
ArrayList<Pipe> pipeList = new ArrayList<>();
pipeList.add(new cc.mallet.pipe.CharSequence2TokenSequence("\\p{L}+"));
pipeList.add(new TokenSequenceLowercase());
pipeList.add(new TokenSequenceRemoveStopwords(false, false));
pipeList.add(new TokenSequence2FeatureVectorSequence(dataAlphabet));
Pipe pipe = new SerialPipes(pipeList);
// NaiveBayes classifier = new NaiveBayes(pipe, prior, cond);
Instance unlabeled = new Instance("hello world", null, null, null);
Instance processed = pipe.instanceFrom(unlabeled);
InstanceList instanceList = new InstanceList(pipe);
instanceList.add(processed);
// double likelihood = classifier.dataLogLikelihood(instanceList);
// assertTrue(Double.isFinite(likelihood));
}

@Test
public void testSerializationAndDeserialization() throws IOException, ClassNotFoundException {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("classA");
labelAlphabet.lookupIndex("classB");
// Multinomial prior = new Multinomial(labelAlphabet, new double[] { 0.7, 0.3 });
// dataAlphabet.lookupIndex("feature1");
dataAlphabet.lookupIndex("feature2");
Multinomial[] conditionals = new Multinomial[2];
// conditionals[0] = new Multinomial(dataAlphabet, new double[] { 0.6, 0.4 });
// conditionals[1] = new Multinomial(dataAlphabet, new double[] { 0.5, 0.5 });
Pipe pipe = new SerialPipes(new ArrayList<>());
// NaiveBayes original = new NaiveBayes(pipe, prior, conditionals);
ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
ObjectOutputStream out = new ObjectOutputStream(byteOut);
// out.writeObject(original);
out.close();
ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
ObjectInputStream in = new ObjectInputStream(byteIn);
NaiveBayes deserialized = (NaiveBayes) in.readObject();
in.close();
assertNotNull(deserialized);
// assertEquals(original.getMultinomials().length, deserialized.getMultinomials().length);
}

@Test
public void testPrintWordsNoException() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("label0");
labelAlphabet.lookupIndex("label1");
// Multinomial prior = new Multinomial(labelAlphabet, new double[] { 0.5, 0.5 });
// dataAlphabet.lookupIndex("apple");
dataAlphabet.lookupIndex("banana");
Multinomial[] cond = new Multinomial[2];
// cond[0] = new Multinomial(dataAlphabet, new double[] { 0.6, 0.4 });
// cond[1] = new Multinomial(dataAlphabet, new double[] { 0.3, 0.7 });
ArrayList<Pipe> pipeList = new ArrayList<>();
pipeList.add(new cc.mallet.pipe.CharSequence2TokenSequence("\\p{L}+"));
pipeList.add(new TokenSequenceLowercase());
pipeList.add(new TokenSequenceRemoveStopwords(false, false));
pipeList.add(new TokenSequence2FeatureVectorSequence(dataAlphabet));
Pipe pipe = new SerialPipes(pipeList);
// NaiveBayes classifier = new NaiveBayes(pipe, prior, cond);
try {
// classifier.printWords(2);
} catch (Exception e) {
fail("printWords should not throw exception but got: " + e.getMessage());
}
}

@Test(expected = ClassNotFoundException.class)
public void testReadObjectWithWrongVersionThrows() throws IOException, ClassNotFoundException {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
// Multinomial prior = new Multinomial(labelAlphabet, new double[] { 0.5, 0.5 });
// Multinomial.Logged loggedPrior = new Multinomial.Logged(prior);
Multinomial.Logged[] loggedConds = new Multinomial.Logged[0];
ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
ObjectOutputStream out = new ObjectOutputStream(byteOut);
out.writeInt(999);
out.writeObject(new SerialPipes(new ArrayList<>()));
// out.writeObject(loggedPrior);
out.writeObject(loggedConds);
out.close();
ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
ObjectInputStream in = new ObjectInputStream(byteIn);
// NaiveBayes dummy = new NaiveBayes(null, loggedPrior, loggedConds);
// dummy.readObject(in);
}

@Test
public void testClassifyWithEmptyFeatureVector() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("label1");
labelAlphabet.lookupIndex("label2");
// Multinomial prior = new Multinomial(labelAlphabet, new double[] { 0.9, 0.1 });
// dataAlphabet.lookupIndex("word1");
Multinomial[] cond = new Multinomial[2];
// cond[0] = new Multinomial(dataAlphabet, new double[] { 1.0 });
// cond[1] = new Multinomial(dataAlphabet, new double[] { 1.0 });
Pipe pipe = new SerialPipes(new ArrayList<>());
// NaiveBayes classifier = new NaiveBayes(pipe, prior, cond);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[0], new double[0]);
Instance instance = new Instance(fv, null, "empty", null);
// Classification classification = classifier.classify(instance);
// assertNotNull(classification);
// assertEquals(2, classification.getLabeling().numLocations());
}

@Test
public void testClassifyWithUnknownFeatureIndex() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("positive");
// Multinomial prior = new Multinomial(labelAlphabet, new double[] { 1.0 });
// Multinomial[] cond = new Multinomial[1];
// cond[0] = new Multinomial(dataAlphabet, new double[] { 0.5, 0.5 });
// dataAlphabet.lookupIndex("known");
Pipe pipe = new SerialPipes(new ArrayList<>());
// NaiveBayes classifier = new NaiveBayes(pipe, prior, cond);
int[] indices = new int[] { 9999 };
double[] values = new double[] { 1.0 };
FeatureVector fv = new FeatureVector(dataAlphabet, indices, values);
Instance instance = new Instance(fv, null, "unseen", null);
// Classification classification = classifier.classify(instance);
// assertNotNull(classification);
}

@Test
public void testClassifyWithZeroFeaturesValues() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("a");
labelAlphabet.lookupIndex("b");
// Multinomial prior = new Multinomial(labelAlphabet, new double[] { 0.6, 0.4 });
// dataAlphabet.lookupIndex("f1");
dataAlphabet.lookupIndex("f2");
Multinomial[] cond = new Multinomial[2];
// cond[0] = new Multinomial(dataAlphabet, new double[] { 0.7, 0.3 });
// cond[1] = new Multinomial(dataAlphabet, new double[] { 0.4, 0.6 });
Pipe pipe = new SerialPipes(new ArrayList<>());
// NaiveBayes classifier = new NaiveBayes(pipe, prior, cond);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0, 1 }, new double[] { 0.0, 0.0 });
Instance instance = new Instance(fv, null, "zero_vals", null);
// Classification classification = classifier.classify(instance);
// assertNotNull(classification);
// assertEquals(2, classification.getLabeling().numLocations());
}

@Test
public void testClassifyWithPipeNullDoesNotCheckAlphabet() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("L");
// Multinomial prior = new Multinomial(labelAlphabet, new double[] { 1.0 });
// dataAlphabet.lookupIndex("X");
Multinomial[] cond = new Multinomial[1];
// cond[0] = new Multinomial(dataAlphabet, new double[] { 1.0 });
// NaiveBayes classifier = new NaiveBayes(null, prior, cond);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
Instance instance = new Instance(fv, null, "no_pipe", null);
// Classification classification = classifier.classify(instance);
// assertNotNull(classification);
}

@Test
public void testTargetAlphabetExpandedAfterTraining() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("train_label1");
// Multinomial prior = new Multinomial(labelAlphabet, new double[] { 1.0 });
// dataAlphabet.lookupIndex("feat1");
Multinomial[] cond = new Multinomial[1];
// cond[0] = new Multinomial(dataAlphabet, new double[] { 1.0 });
// Pipe pipe = new SerialPipes(new ArrayList<>());
// NaiveBayes classifier = new NaiveBayes(pipe, prior, cond);
labelAlphabet.lookupIndex("new_label_after_train");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
Instance instance = new Instance(fv, null, "expanded", null);
// Classification classification = classifier.classify(instance);
// assertEquals(2, classification.getLabeling().numLocations());
}

@Test
public void testDataLogLikelihoodWithAllZeroWeights() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("label");
// Multinomial prior = new Multinomial(labelAlphabet, new double[] { 1.0 });
// dataAlphabet.lookupIndex("f");
Multinomial[] cond = new Multinomial[1];
// cond[0] = new Multinomial(dataAlphabet, new double[] { 1.0 });
// Pipe pipe = new SerialPipes(new ArrayList<>());
// NaiveBayes classifier = new NaiveBayes(pipe, prior, cond);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 0.0 });
// Instance instance = new Instance(fv, new Label(labelAlphabet, 0), null, null);
// InstanceList ilist = new InstanceList(pipe);
// ilist.add(instance);
// ilist.setInstanceWeight(0, 0.0);
// double value = classifier.dataLogLikelihood(ilist);
// assertEquals(0.0, value, 0.0001);
}

@Test
public void testLabelLogLikelihoodWithZeroProbabilityScores() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("cat");
labelAlphabet.lookupIndex("dog");
// Multinomial prior = new Multinomial(labelAlphabet, new double[] { 0.5, 0.5 });
// dataAlphabet.lookupIndex("meow");
dataAlphabet.lookupIndex("bark");
Multinomial[] cond = new Multinomial[2];
// cond[0] = new Multinomial(dataAlphabet, new double[] { 1.0, 0.0 });
// cond[1] = new Multinomial(dataAlphabet, new double[] { 0.0, 1.0 });
Pipe pipe = new SerialPipes(new ArrayList<>());
// NaiveBayes classifier = new NaiveBayes(pipe, prior, cond);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
LabelVector incorrectLabel = new LabelVector(labelAlphabet, new double[] { 0.0, 1.0 });
Instance inst = new Instance(fv, incorrectLabel, null, null);
InstanceList ilist = new InstanceList(pipe);
ilist.add(inst);
// double score = classifier.labelLogLikelihood(ilist);
// assertTrue(Double.isInfinite(score) || Double.isNaN(score) || score < 0);
}

@Test
public void testClassifyFailsWhenPipeAlphabetDiffersFromInstance() {
Alphabet alphabet1 = new Alphabet();
Alphabet alphabet2 = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("yes");
// Multinomial prior = new Multinomial(labelAlphabet, new double[] { 1.0 });
// alphabet1.lookupIndex("featureA");
alphabet2.lookupIndex("featureA");
Multinomial[] cond = new Multinomial[1];
// cond[0] = new Multinomial(alphabet2, new double[] { 1.0 });
// ArrayList<Pipe> pipes = new ArrayList<>();
// pipes.add(new CharSequence2TokenSequence("\\p{L}+"));
// pipes.add(new TokenSequenceLowercase());
// pipes.add(new TokenSequenceRemoveStopwords(false, false));
// pipes.add(new TokenSequence2FeatureVectorSequence(alphabet1));
// Pipe pipe = new SerialPipes(pipes);
// NaiveBayes nb = new NaiveBayes(pipe, prior, cond);
FeatureVector fv = new FeatureVector(alphabet2, new int[] { 0 }, new double[] { 1.0 });
Instance inst = new Instance(fv, null, null, null);
try {
// nb.classify(inst);
fail("Expected AssertionError due to alphabet mismatch");
} catch (AssertionError expected) {
}
}

@Test
public void testConstructionWithEmptyConditionalProbabilityVectors() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("labelX");
// Multinomial prior = new Multinomial(labelAlphabet, new double[] { 1.0 });
// Multinomial[] cond = new Multinomial[1];
// cond[0] = new Multinomial(dataAlphabet);
Pipe pipe = new SerialPipes(new ArrayList<>());
// NaiveBayes nb = new NaiveBayes(pipe, prior, cond);
// assertNotNull(nb.getMultinomials());
// assertEquals(1, nb.getMultinomials().length);
}

@Test
public void testClassifyWithLabelAlphabetMismatchGivesZeroProbForExtraLabels() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("original");
// Multinomial prior = new Multinomial(labelAlphabet, new double[] { 1.0 });
// dataAlphabet.lookupIndex("text");
Multinomial[] cond = new Multinomial[1];
// cond[0] = new Multinomial(dataAlphabet, new double[] { 1.0 });
// Pipe pipe = new SerialPipes(new ArrayList<>());
// NaiveBayes nb = new NaiveBayes(pipe, prior, cond);
labelAlphabet.lookupIndex("new_label");
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
Instance inst = new Instance(fv, null, null, null);
// Classification result = nb.classify(inst);
// assertEquals(2, result.getLabeling().numLocations());
}

@Test
public void testInstanceListWithNullInstanceYieldsNoError() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("neutral");
// Multinomial prior = new Multinomial(labelAlphabet, new double[] { 1.0 });
// dataAlphabet.lookupIndex("f1");
Multinomial[] cond = new Multinomial[1];
// cond[0] = new Multinomial(dataAlphabet, new double[] { 1.0 });
// Pipe pipe = new SerialPipes(new ArrayList<>());
// NaiveBayes nb = new NaiveBayes(pipe, prior, cond);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
// Instance inst1 = new Instance(fv, new Label(labelAlphabet, 0), null, null);
// InstanceList ilist = new InstanceList(pipe);
// ilist.add(inst1);
// ilist.add(null);
// double val = nb.dataLogLikelihood(ilist);
// assertTrue(Double.isFinite(val));
}

@Test
public void testClassificationWhenAllClassScoresAreFilteredToZero() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("A");
labelAlphabet.lookupIndex("B");
// Multinomial prior = new Multinomial(labelAlphabet, new double[] { 0.0, 0.0 });
// dataAlphabet.lookupIndex("foo");
Multinomial[] cond = new Multinomial[2];
// cond[0] = new Multinomial(dataAlphabet, new double[] { 0.0 });
// cond[1] = new Multinomial(dataAlphabet, new double[] { 0.0 });
Pipe pipe = new SerialPipes(new ArrayList<>());
// NaiveBayes nb = new NaiveBayes(pipe, prior, cond);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
Instance inst = new Instance(fv, null, null, null);
// Classification result = nb.classify(inst);
// assertNotNull(result.getLabeling());
// assertEquals(2, result.getLabeling().numLocations());
}

@Test
public void testPrintWordsWithZeroNumToPrint() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("C");
// Multinomial prior = new Multinomial(labelAlphabet, new double[] { 1.0 });
// dataAlphabet.lookupIndex("zero");
Multinomial[] cond = new Multinomial[1];
// cond[0] = new Multinomial(dataAlphabet, new double[] { 1.0 });
// ArrayList<Pipe> pipeList = new ArrayList<>();
// pipeList.add(new CharSequence2TokenSequence("\\p{L}+"));
// pipeList.add(new TokenSequenceLowercase());
// pipeList.add(new TokenSequenceRemoveStopwords(false, false));
// pipeList.add(new TokenSequence2FeatureVectorSequence(dataAlphabet));
// Pipe pipe = new SerialPipes(pipeList);
// NaiveBayes nb = new NaiveBayes(pipe, prior, cond);
try {
// nb.printWords(0);
} catch (Exception e) {
fail("printWords(0) threw exception: " + e.getMessage());
}
}

@Test
public void testClassifyWithFeaturesNeverSeenByAnyClassProbability() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("L1");
labelAlphabet.lookupIndex("L2");
// Multinomial prior = new Multinomial(labelAlphabet, new double[] { 0.5, 0.5 });
// Alphabet condAlphabet = new Alphabet();
// condAlphabet.lookupIndex("a");
Multinomial[] cond = new Multinomial[2];
// cond[0] = new Multinomial(condAlphabet, new double[] { 1.0 });
// cond[1] = new Multinomial(condAlphabet, new double[] { 1.0 });
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
Pipe pipe = new SerialPipes(new ArrayList<>());
// NaiveBayes classifier = new NaiveBayes(pipe, prior, cond);
Instance inst = new Instance(fv, null, "unseen", null);
// Classification result = classifier.classify(inst);
// assertNotNull(result.getLabeling());
// assertEquals(2, result.getLabeling().numLocations());
}

@Test
public void testSerializeDeserializeWithNullPipe() throws Exception {
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("classA");
// Multinomial prior = new Multinomial(labelAlphabet, new double[] { 1.0 });
// Multinomial[] cond = new Multinomial[1];
// cond[0] = new Multinomial(new Alphabet(), new double[] { 1.0 });
// NaiveBayes original = new NaiveBayes(null, prior, cond);
ByteArrayOutputStream bout = new ByteArrayOutputStream();
ObjectOutputStream oos = new ObjectOutputStream(bout);
// oos.writeObject(original);
oos.close();
ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
ObjectInputStream ois = new ObjectInputStream(bin);
NaiveBayes restored = (NaiveBayes) ois.readObject();
assertNotNull(restored);
assertEquals(1, restored.getMultinomials().length);
}

@Test
public void testClassificationWithScoresAllZeroBeforeExp() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("A");
labelAlphabet.lookupIndex("B");
// Multinomial prior = new Multinomial(labelAlphabet, new double[] { 0.5, 0.5 });
// dataAlphabet.lookupIndex("x");
Multinomial[] cond = new Multinomial[2];
// cond[0] = new Multinomial(dataAlphabet, new double[] { 0.0 });
// cond[1] = new Multinomial(dataAlphabet, new double[] { 0.0 });
Pipe pipe = new SerialPipes(new ArrayList<>());
// NaiveBayes nb = new NaiveBayes(pipe, prior, cond);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 0.0 });
Instance inst = new Instance(fv, null, null, null);
// Classification c = nb.classify(inst);
// assertNotNull(c);
// assertEquals(2, c.getLabeling().numLocations());
// double sum = c.getLabeling().value(0) + c.getLabeling().value(1);
// assertEquals(1.0, sum, 1e-6);
}

@Test
public void testClassifyWithEmptyLabelAlphabet() {
Alphabet featureAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
// Multinomial prior = new Multinomial(labelAlphabet);
Multinomial[] cond = new Multinomial[0];
Pipe pipe = new SerialPipes(new ArrayList<>());
// NaiveBayes classifier = new NaiveBayes(pipe, prior, cond);
FeatureVector fv = new FeatureVector(featureAlphabet, new int[0], new double[0]);
Instance inst = new Instance(fv, null, null, null);
// Classification result = classifier.classify(inst);
// assertEquals(0, result.getLabeling().numLocations());
}

@Test
public void testDataLogLikelihoodWithMissingClassProbabilities() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("A");
// Multinomial prior = new Multinomial(labelAlphabet, new double[] { 1.0 });
// dataAlphabet.lookupIndex("x");
Multinomial[] cond = new Multinomial[1];
// cond[0] = new Multinomial(dataAlphabet, new double[] { 0.0 });
// Pipe pipe = new SerialPipes(new ArrayList<>());
// NaiveBayes classifier = new NaiveBayes(pipe, prior, cond);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
Instance inst = new Instance(fv, null, null, null);
// InstanceList ilist = new InstanceList(pipe);
// ilist.add(inst);
// double score = classifier.dataLogLikelihood(ilist);
// assertTrue(Double.isFinite(score));
}

@Test
public void testLabelLogLikelihoodWithZeroPredictedProbability() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("pos");
labelAlphabet.lookupIndex("neg");
// Multinomial prior = new Multinomial(labelAlphabet, new double[] { 0.0, 1.0 });
// dataAlphabet.lookupIndex("featA");
Multinomial[] cond = new Multinomial[2];
// cond[0] = new Multinomial(dataAlphabet, new double[] { 0.0 });
// cond[1] = new Multinomial(dataAlphabet, new double[] { 1.0 });
Pipe pipe = new SerialPipes(new ArrayList<>());
// NaiveBayes classifier = new NaiveBayes(pipe, prior, cond);
double[] trueProbs = new double[] { 1.0, 0.0 };
LabelVector gold = new LabelVector(labelAlphabet, trueProbs);
FeatureVector input = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
Instance instance = new Instance(input, gold, null, null);
InstanceList list = new InstanceList(pipe);
list.add(instance);
// double loglik = classifier.labelLogLikelihood(list);
// assertTrue(Double.isInfinite(loglik));
}

@Test
public void testFeatureVectorWithRepeatedIndices() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("X");
// Multinomial prior = new Multinomial(labelAlphabet, new double[] { 1.0 });
// dataAlphabet.lookupIndex("repeat");
Multinomial[] cond = new Multinomial[1];
// cond[0] = new Multinomial(dataAlphabet, new double[] { 1.0 });
// Pipe pipe = new SerialPipes(new ArrayList<>());
// NaiveBayes classifier = new NaiveBayes(pipe, prior, cond);
int[] indices = new int[] { 0, 0 };
double[] values = new double[] { 1.0, 2.0 };
FeatureVector fv = new FeatureVector(dataAlphabet, indices, values);
Instance inst = new Instance(fv, null, null, null);
// Classification result = classifier.classify(inst);
// assertNotNull(result);
// assertEquals(1, result.getLabeling().numLocations());
}

@Test
public void testSerializationWithNullPriors() throws Exception {
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("a");
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("b");
Multinomial.Logged[] cond = new Multinomial.Logged[1];
// Multinomial base = new Multinomial(dataAlphabet, new double[] { 1.0 });
// cond[0] = new Multinomial.Logged(base);
Pipe pipe = new SerialPipes(new ArrayList<>());
NaiveBayes nb = new NaiveBayes(pipe, null, cond);
ByteArrayOutputStream bout = new ByteArrayOutputStream();
ObjectOutputStream oos = new ObjectOutputStream(bout);
oos.writeInt(1);
oos.writeObject(pipe);
oos.writeObject(null);
oos.writeObject(cond);
oos.close();
ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
ObjectInputStream in = new ObjectInputStream(bin);
NaiveBayes restored = new NaiveBayes(pipe, null, cond);
// restored.readObject(in);
assertNull(restored.getPriors());
assertEquals(1, restored.getMultinomials().length);
}

@Test
public void testPrintWordsLargerThanFeatureCount() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("class0");
dataAlphabet.lookupIndex("word1");
// Multinomial prior = new Multinomial(labelAlphabet, new double[] { 1.0 });
// Multinomial[] cond = new Multinomial[1];
// cond[0] = new Multinomial(dataAlphabet, new double[] { 1.0 });
// ArrayList<Pipe> pipes = new ArrayList<>();
// pipes.add(new CharSequence2TokenSequence("\\p{L}+"));
// pipes.add(new TokenSequenceLowercase());
// pipes.add(new TokenSequenceRemoveStopwords(false, false));
// pipes.add(new TokenSequence2FeatureVectorSequence(dataAlphabet));
// Pipe pipe = new SerialPipes(pipes);
// pipe.setTargetProcessing(true);
// pipe.getTargetAlphabet().lookupIndex("class0");
// NaiveBayes nb = new NaiveBayes(pipe, prior, cond);
try {
// nb.printWords(100);
} catch (Exception e) {
fail("printWords should not throw exception when printing more features than exist");
}
}

@Test
public void testLabelLogLikelihoodWithZeroWeightLabels() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("x");
labelAlphabet.lookupIndex("y");
dataAlphabet.lookupIndex("f");
// Multinomial prior = new Multinomial(labelAlphabet, new double[] { 0.5, 0.5 });
// Multinomial[] cond = new Multinomial[2];
// cond[0] = new Multinomial(dataAlphabet, new double[] { 1.0 });
// cond[1] = new Multinomial(dataAlphabet, new double[] { 1.0 });
Pipe pipe = new SerialPipes(new ArrayList<>());
// NaiveBayes classifier = new NaiveBayes(pipe, prior, cond);
double[] weights = new double[] { 0.0, 0.0 };
LabelVector labels = new LabelVector(labelAlphabet, weights);
FeatureVector features = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
Instance inst = new Instance(features, labels, null, null);
InstanceList list = new InstanceList(pipe);
list.add(inst);
// double ll = classifier.labelLogLikelihood(list);
// assertTrue(Double.isNaN(ll) || ll == 0.0);
}

@Test
public void testClassifyWhenAllLogScoresNegativeInfinity() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("a");
labelAlphabet.lookupIndex("b");
// Multinomial prior = new Multinomial(labelAlphabet, new double[] { 0.0, 0.0 });
// dataAlphabet.lookupIndex("f");
Multinomial[] cond = new Multinomial[2];
// cond[0] = new Multinomial(dataAlphabet, new double[] { 0.0 });
// cond[1] = new Multinomial(dataAlphabet, new double[] { 0.0 });
Pipe pipe = new SerialPipes(new ArrayList<>());
// NaiveBayes nb = new NaiveBayes(pipe, prior, cond);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 0.0 });
Instance instance = new Instance(fv, null, null, null);
// Classification result = nb.classify(instance);
// assertEquals(2, result.getLabeling().numLocations());
double total = 0;
// total += result.getLabeling().value(0);
// total += result.getLabeling().value(1);
assertEquals(1.0, total, 1e-6);
}

@Test
public void testDataLogLikelihoodWithNaNFeatureValue() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("spam");
dataAlphabet.lookupIndex("subject");
// Multinomial prior = new Multinomial(labelAlphabet, new double[] { 1.0 });
// Multinomial[] cond = new Multinomial[1];
// cond[0] = new Multinomial(dataAlphabet, new double[] { 1.0 });
// Pipe pipe = new SerialPipes(new ArrayList<>());
// NaiveBayes classifier = new NaiveBayes(pipe, prior, cond);
double[] values = new double[] { Double.NaN };
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, values);
// Label label = new Label(labelAlphabet, 0);
// Instance inst = new Instance(fv, label, "NaNFeature", null);
// InstanceList list = new InstanceList(pipe);
// list.add(inst);
// double ll = classifier.dataLogLikelihood(list);
// assertTrue(Double.isNaN(ll));
}

@Test
public void testLabelLogLikelihoodWithPredictedZeroForTrueLabel() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("good");
labelAlphabet.lookupIndex("bad");
// Multinomial prior = new Multinomial(labelAlphabet, new double[] { 0.0, 1.0 });
// dataAlphabet.lookupIndex("x");
Multinomial[] cond = new Multinomial[2];
// cond[0] = new Multinomial(dataAlphabet, new double[] { 0.0 });
// cond[1] = new Multinomial(dataAlphabet, new double[] { 1.0 });
Pipe pipe = new SerialPipes(new ArrayList<>());
// NaiveBayes classifier = new NaiveBayes(pipe, prior, cond);
double[] labelWeights = new double[] { 1.0, 0.0 };
LabelVector labelVector = new LabelVector(labelAlphabet, labelWeights);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
Instance inst = new Instance(fv, labelVector, null, null);
InstanceList ilist = new InstanceList(pipe);
ilist.add(inst);
// double ll = classifier.labelLogLikelihood(ilist);
// assertTrue(Double.isInfinite(ll));
}

@Test
public void testMultinomialsWithInconsistentFeatureSizeAcrossClasses() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("l1");
labelAlphabet.lookupIndex("l2");
dataAlphabet.lookupIndex("a");
dataAlphabet.lookupIndex("b");
// Multinomial prior = new Multinomial(labelAlphabet, new double[] { 0.5, 0.5 });
// Multinomial[] cond = new Multinomial[2];
// cond[0] = new Multinomial(dataAlphabet, new double[] { 1.0, 0.0 });
// cond[1] = new Multinomial(new Alphabet(), new double[] {});
Pipe pipe = new SerialPipes(new ArrayList<>());
// NaiveBayes nb = new NaiveBayes(pipe, prior, cond);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
Instance inst = new Instance(fv, null, null, null);
// Classification classification = nb.classify(inst);
// assertEquals(2, classification.getLabeling().numLocations());
}

@Test
public void testReadObjectWithNullConditionals() throws Exception {
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("z");
// Multinomial prior = new Multinomial(labelAlphabet, new double[] { 1.0 });
// Pipe pipe = new SerialPipes(new ArrayList<>());
ByteArrayOutputStream bout = new ByteArrayOutputStream();
ObjectOutputStream oos = new ObjectOutputStream(bout);
oos.writeInt(1);
// oos.writeObject(pipe);
// oos.writeObject(new Multinomial.Logged(prior));
oos.writeObject(null);
oos.close();
ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
ObjectInputStream in = new ObjectInputStream(bin);
Multinomial.Logged[] dummyArray = new Multinomial.Logged[0];
// NaiveBayes nb = new NaiveBayes(pipe, new Multinomial.Logged(prior), dummyArray);
// nb.readObject(in);
}

@Test
public void testClassifyWithNegativeFeatureValue() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
dataAlphabet.lookupIndex("feature");
labelAlphabet.lookupIndex("label");
// Multinomial prior = new Multinomial(labelAlphabet, new double[] { 1.0 });
// Multinomial[] cond = new Multinomial[1];
// cond[0] = new Multinomial(dataAlphabet, new double[] { 1.0 });
// Pipe pipe = new SerialPipes(new ArrayList<>());
// NaiveBayes nb = new NaiveBayes(pipe, prior, cond);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { -1.0 });
Instance inst = new Instance(fv, null, "neg_feature", null);
// Classification classification = nb.classify(inst);
// assertNotNull(classification);
// assertEquals(1, classification.getLabeling().numLocations());
// assertTrue(classification.getLabeling().value(0) >= 0.0);
}

@Test
public void testClassifyWithAllZeroPriorProbabilities() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("l1");
labelAlphabet.lookupIndex("l2");
// Multinomial prior = new Multinomial(labelAlphabet, new double[] { 0.0, 0.0 });
// dataAlphabet.lookupIndex("x");
Multinomial[] cond = new Multinomial[2];
// cond[0] = new Multinomial(dataAlphabet, new double[] { 1.0 });
// cond[1] = new Multinomial(dataAlphabet, new double[] { 1.0 });
Pipe pipe = new SerialPipes(new ArrayList<>());
// NaiveBayes nb = new NaiveBayes(pipe, prior, cond);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
Instance inst = new Instance(fv, null, "zero_prior", null);
// Classification classification = nb.classify(inst);
// assertNotNull(classification);
// assertEquals(2, classification.getLabeling().numLocations());
}

@Test
public void testLabelLogLikelihoodWithUniformLabelDistributionAndZeroPrediction() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("p");
labelAlphabet.lookupIndex("n");
// Multinomial prior = new Multinomial(labelAlphabet, new double[] { 1.0, 0.0 });
// dataAlphabet.lookupIndex("w");
Multinomial[] cond = new Multinomial[2];
// cond[0] = new Multinomial(dataAlphabet, new double[] { 1.0 });
// cond[1] = new Multinomial(dataAlphabet, new double[] { 0.0 });
Pipe pipe = new SerialPipes(new ArrayList<>());
// NaiveBayes classifier = new NaiveBayes(pipe, prior, cond);
double[] gold = new double[] { 0.5, 0.5 };
LabelVector labelVec = new LabelVector(labelAlphabet, gold);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
Instance inst = new Instance(fv, labelVec, null, null);
InstanceList ilist = new InstanceList(pipe);
ilist.add(inst);
// double result = classifier.labelLogLikelihood(ilist);
// assertTrue(Double.isFinite(result));
}

@Test
public void testDataLogLikelihoodWithOverlappingFeatures() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
dataAlphabet.lookupIndex("a");
labelAlphabet.lookupIndex("foo");
// Multinomial prior = new Multinomial(labelAlphabet, new double[] { 1.0 });
// Multinomial[] cond = new Multinomial[1];
// cond[0] = new Multinomial(dataAlphabet, new double[] { 1.0 });
// FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0, 0 }, new double[] { 1.5, 2.5 });
// Label label = new Label(labelAlphabet, 0);
// Instance inst = new Instance(fv, label, null, null);
Pipe pipe = new SerialPipes(new ArrayList<>());
// NaiveBayes classifier = new NaiveBayes(pipe, prior, cond);
InstanceList ilist = new InstanceList(pipe);
// ilist.add(inst);
// double ll = classifier.dataLogLikelihood(ilist);
// assertTrue(Double.isFinite(ll));
}

@Test
public void testLabelLogLikelihoodWithNullTargetReturnsZero() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
dataAlphabet.lookupIndex("k");
labelAlphabet.lookupIndex("y");
// Multinomial prior = new Multinomial(labelAlphabet, new double[] { 1.0 });
// Multinomial[] cond = new Multinomial[1];
// cond[0] = new Multinomial(dataAlphabet, new double[] { 1.0 });
// FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
// Instance inst = new Instance(fv, null, "missing_target", null);
Pipe pipe = new SerialPipes(new ArrayList<>());
// NaiveBayes classifier = new NaiveBayes(pipe, prior, cond);
InstanceList ilist = new InstanceList(pipe);
// ilist.add(inst);
// double ll = classifier.labelLogLikelihood(ilist);
// assertEquals(0.0, ll, 0.00001);
}

@Test
public void testClassificationWithMixedZeroAndPositiveConditionals() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
dataAlphabet.lookupIndex("alpha");
labelAlphabet.lookupIndex("one");
labelAlphabet.lookupIndex("two");
// Multinomial prior = new Multinomial(labelAlphabet, new double[] { 0.4, 0.6 });
// Multinomial[] cond = new Multinomial[2];
// cond[0] = new Multinomial(dataAlphabet, new double[] { 0.0 });
// cond[1] = new Multinomial(dataAlphabet, new double[] { 1.0 });
Pipe pipe = new SerialPipes(new ArrayList<>());
// NaiveBayes classifier = new NaiveBayes(pipe, prior, cond);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
Instance inst = new Instance(fv, null, "mix_cond", null);
// Classification result = classifier.classify(inst);
// assertNotNull(result);
// assertEquals(2, result.getLabeling().numLocations());
}

@Test
public void testClassifyWhenAllProbabilitiesAreEqual() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
dataAlphabet.lookupIndex("x");
labelAlphabet.lookupIndex("l1");
labelAlphabet.lookupIndex("l2");
// Multinomial prior = new Multinomial(labelAlphabet, new double[] { 0.5, 0.5 });
// Multinomial[] cond = new Multinomial[2];
// cond[0] = new Multinomial(dataAlphabet, new double[] { 1.0 });
// cond[1] = new Multinomial(dataAlphabet, new double[] { 1.0 });
Pipe pipe = new SerialPipes(new ArrayList<>());
// NaiveBayes nb = new NaiveBayes(pipe, prior, cond);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
Instance inst = new Instance(fv, null, "tie_case", null);
// Classification classification = nb.classify(inst);
// assertNotNull(classification);
// double v0 = classification.getLabeling().value(0);
// double v1 = classification.getLabeling().value(1);
// assertEquals(v0, v1, 1e-6);
}

@Test
public void testClassificationScalesExponentialWithLargeWeights() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
dataAlphabet.lookupIndex("massive");
labelAlphabet.lookupIndex("strong");
labelAlphabet.lookupIndex("weak");
// Multinomial prior = new Multinomial(labelAlphabet, new double[] { 0.3, 0.7 });
// Multinomial[] cond = new Multinomial[2];
// cond[0] = new Multinomial(dataAlphabet, new double[] { 1000.0 });
// cond[1] = new Multinomial(dataAlphabet, new double[] { 0.001 });
Pipe pipe = new SerialPipes(new ArrayList<>());
// NaiveBayes classifier = new NaiveBayes(pipe, prior, cond);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
Instance inst = new Instance(fv, null, null, null);
// Classification result = classifier.classify(inst);
// assertTrue(result.getLabeling().value(0) > 0.99);
}

@Test
public void testClassifyWithEmptyPriorsAndConditionals() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
dataAlphabet.lookupIndex("term");
// Multinomial prior = new Multinomial(labelAlphabet);
Multinomial[] cond = new Multinomial[0];
Pipe pipe = new SerialPipes(new ArrayList<>());
// NaiveBayes nb = new NaiveBayes(pipe, prior, cond);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
Instance instance = new Instance(fv, null, "empty-model", null);
// Classification result = nb.classify(instance);
// assertNotNull(result);
// assertEquals(0, result.getLabeling().numLocations());
}

@Test
public void testClassifyWithAlphabetSizeMismatchAndMissingProbabilities() {
Alphabet modelAlphabet = new Alphabet();
Alphabet instanceAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("c1");
labelAlphabet.lookupIndex("c2");
modelAlphabet.lookupIndex("feature0");
// Multinomial prior = new Multinomial(labelAlphabet, new double[] { 0.5, 0.5 });
// Multinomial[] cond = new Multinomial[2];
// cond[0] = new Multinomial(modelAlphabet, new double[] { 1.0 });
// cond[1] = new Multinomial(modelAlphabet, new double[] { 1.0 });
FeatureVector fv = new FeatureVector(instanceAlphabet, new int[] { 0 }, new double[] { 1.0 });
Instance instance = new Instance(fv, null, "mismatch", null);
Pipe pipe = new SerialPipes(new ArrayList<>());
// NaiveBayes nb = new NaiveBayes(pipe, prior, cond);
try {
// nb.classify(instance);
fail("Expected AssertionError due to feature alphabet mismatch");
} catch (AssertionError expected) {
}
}

@Test
public void testDataLogLikelihoodWithUnlabeledInstanceReturnsWeightedSum() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
dataAlphabet.lookupIndex("x1");
labelAlphabet.lookupIndex("yes");
labelAlphabet.lookupIndex("no");
// Multinomial prior = new Multinomial(labelAlphabet, new double[] { 0.5, 0.5 });
// Multinomial[] cond = new Multinomial[2];
// cond[0] = new Multinomial(dataAlphabet, new double[] { 1.0 });
// cond[1] = new Multinomial(dataAlphabet, new double[] { 0.5 });
Pipe pipe = new SerialPipes(new ArrayList<>());
// NaiveBayes nb = new NaiveBayes(pipe, prior, cond);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.5 });
Instance instance = new Instance(fv, null, null, null);
InstanceList ilist = new InstanceList(pipe);
ilist.add(instance);
ilist.setInstanceWeight(0, 0.5);
// double score = nb.dataLogLikelihood(ilist);
// assertTrue(Double.isFinite(score));
}

@Test
public void testLabelLogLikelihoodWithSingleLabelMultiplePredictedValues() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("one");
labelAlphabet.lookupIndex("two");
dataAlphabet.lookupIndex("f");
// Multinomial prior = new Multinomial(labelAlphabet, new double[] { 0.8, 0.2 });
// Multinomial[] cond = new Multinomial[2];
// cond[0] = new Multinomial(dataAlphabet, new double[] { 0.9 });
// cond[1] = new Multinomial(dataAlphabet, new double[] { 0.1 });
Pipe pipe = new SerialPipes(new ArrayList<>());
// NaiveBayes nb = new NaiveBayes(pipe, prior, cond);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
// Label label = new Label(labelAlphabet, 1);
// Instance inst = new Instance(fv, label, null, null);
InstanceList ilist = new InstanceList(pipe);
// ilist.add(inst);
// double score = nb.labelLogLikelihood(ilist);
// assertTrue(Double.isFinite(score));
}

@Test
public void testSerializationWithLargeModelContent() throws Exception {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
int numFeatures = 1000;
int numClasses = 5;
double[] priors = new double[numClasses];
for (int i = 0; i < numClasses; i++) {
labelAlphabet.lookupIndex("class" + i);
priors[i] = 1.0;
}
// Multinomial prior = new Multinomial(labelAlphabet, priors);
Multinomial[] conditionals = new Multinomial[numClasses];
for (int i = 0; i < numFeatures; i++) {
dataAlphabet.lookupIndex("f" + i);
}
double[] features = new double[numFeatures];
for (int i = 0; i < numFeatures; i++) {
features[i] = 1.0;
}
for (int i = 0; i < numClasses; i++) {
// conditionals[i] = new Multinomial(dataAlphabet, features);
}
Pipe pipe = new SerialPipes(new ArrayList<>());
// NaiveBayes classifier = new NaiveBayes(pipe, prior, conditionals);
ByteArrayOutputStream bout = new ByteArrayOutputStream();
ObjectOutputStream oos = new ObjectOutputStream(bout);
// oos.writeObject(classifier);
oos.close();
ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
ObjectInputStream ois = new ObjectInputStream(bin);
NaiveBayes deserialized = (NaiveBayes) ois.readObject();
ois.close();
assertNotNull(deserialized);
assertEquals(numClasses, deserialized.getMultinomials().length);
assertNotNull(deserialized.getPriors());
}

@Test
public void testLabelLogLikelihoodWithNullMultinomialProbabilityReturnsZero() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("x");
dataAlphabet.lookupIndex("y");
// Multinomial prior = new Multinomial(labelAlphabet, new double[] { 1.0 });
// Multinomial[] cond = new Multinomial[1];
// cond[0] = new Multinomial(dataAlphabet, new double[] { 1.0 });
// Pipe pipe = new SerialPipes(new ArrayList<>());
// NaiveBayes nb = new NaiveBayes(pipe, prior, cond);
LabelVector labelVec = new LabelVector(labelAlphabet, new double[] { 1.0 });
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 0.0 });
Instance inst = new Instance(fv, labelVec, null, null);
// InstanceList instances = new InstanceList(pipe);
// instances.add(inst);
// instances.setInstanceWeight(0, 1.0);
// double score = nb.labelLogLikelihood(instances);
// assertEquals(0.0, score, 1e-9);
}

@Test
public void testClassifyWithOnlyOneClass() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("feat");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("A");
// Multinomial prior = new Multinomial(labelAlphabet, new double[] { 1.0 });
// Multinomial[] condit = new Multinomial[1];
// condit[0] = new Multinomial(dataAlphabet, new double[] { 1.0 });
// Pipe pipe = new SerialPipes(new ArrayList<>());
// NaiveBayes classifier = new NaiveBayes(pipe, prior, condit);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
Instance inst = new Instance(fv, null, null, null);
// Classification result = classifier.classify(inst);
// assertEquals(1, result.getLabeling().numLocations());
// assertEquals(1.0, result.getLabeling().value(0), 0.00001);
}
}
