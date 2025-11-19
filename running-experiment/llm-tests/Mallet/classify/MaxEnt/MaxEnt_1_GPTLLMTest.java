package cc.mallet.classify.tests;

import cc.mallet.classify.Classification;
import cc.mallet.classify.MaxEnt;
import cc.mallet.pipe.Pipe;
import cc.mallet.types.*;
import org.junit.Test;
import java.io.*;
import java.util.Arrays;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MaxEnt_1_GPTLLMTest {

@Test
public void testConstructorAndGetters() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("f1");
dataAlphabet.lookupIndex("f2");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("C1");
labelAlphabet.lookupIndex("C2");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
int numFeatures = dataAlphabet.size();
int numClasses = labelAlphabet.size();
double[] params = new double[(numFeatures + 1) * numClasses];
Arrays.fill(params, 1.0);
MaxEnt classifier = new MaxEnt(pipe, params);
assertNotNull(classifier);
assertEquals((numFeatures + 1) * numClasses, classifier.getParameters().length);
assertEquals(dataAlphabet.size(), classifier.getDefaultFeatureIndex());
}

@Test
public void testSetParameters() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("f1");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("C1");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
MaxEnt classifier = new MaxEnt(pipe, (double[]) null);
double[] newParams = new double[2];
newParams[0] = 0.5;
newParams[1] = 1.5;
classifier.setParameters(newParams);
assertEquals(0.5, classifier.getParameters()[0], 0.001);
assertEquals(1.5, classifier.getParameters()[1], 0.001);
}

@Test
public void testSetParameter() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("f1");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("C1");
labelAlphabet.lookupIndex("C2");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
double[] params = new double[4];
MaxEnt classifier = new MaxEnt(pipe, params);
classifier.setParameter(1, 0, 3.1415);
assertEquals(3.1415, classifier.getParameters()[2], 0.0001);
}

@Test
public void testGetClassificationScores() {
Alphabet dataAlphabet = new Alphabet();
int indexA = dataAlphabet.lookupIndex("A");
int indexB = dataAlphabet.lookupIndex("B");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("spam");
labelAlphabet.lookupIndex("ham");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
double[] parameters = new double[6];
Arrays.fill(parameters, 1.0);
MaxEnt classifier = new MaxEnt(pipe, parameters);
int[] indices = { indexA, indexB };
double[] values = { 1.0, 2.0 };
FeatureVector fv = new FeatureVector(dataAlphabet, indices, values);
Instance instance = new Instance(fv, "ham", null, null);
double[] scores = new double[2];
classifier.getClassificationScores(instance, scores);
double total = scores[0] + scores[1];
assertTrue(scores[0] > 0);
assertTrue(scores[1] > 0);
assertEquals(1.0, total, 0.00001);
}

@Test
public void testGetClassificationScoresWithTemperature() {
Alphabet dataAlphabet = new Alphabet();
int indexA = dataAlphabet.lookupIndex("X");
int indexB = dataAlphabet.lookupIndex("Y");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("p");
labelAlphabet.lookupIndex("q");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
double[] parameters = new double[6];
Arrays.fill(parameters, 1.0);
MaxEnt classifier = new MaxEnt(pipe, parameters);
int[] indices = { indexA, indexB };
double[] values = { 0.5, 1.5 };
FeatureVector fv = new FeatureVector(dataAlphabet, indices, values);
Instance instance = new Instance(fv, "p", null, null);
double[] scores = new double[2];
classifier.getClassificationScoresWithTemperature(instance, 2.0, scores);
double total = scores[0] + scores[1];
assertTrue(scores[0] > 0);
assertTrue(scores[1] > 0);
assertEquals(1.0, total, 0.00001);
}

@Test
public void testSetFeatureSelection() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("x");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("label");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
double[] parameters = new double[2];
FeatureSelection fs = mock(FeatureSelection.class);
MaxEnt classifier = new MaxEnt(pipe, parameters);
classifier.setFeatureSelection(fs);
assertEquals(fs, classifier.getFeatureSelection());
}

@Test
public void testSetPerClassFeatureSelection() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("word");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("pos");
labelAlphabet.lookupIndex("neg");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
double[] parameters = new double[4];
FeatureSelection fs0 = mock(FeatureSelection.class);
FeatureSelection fs1 = mock(FeatureSelection.class);
FeatureSelection[] perClassFS = new FeatureSelection[] { fs0, fs1 };
MaxEnt classifier = new MaxEnt(pipe, parameters);
classifier.setPerClassFeatureSelection(perClassFS);
assertEquals(perClassFS, classifier.getPerClassFeatureSelection());
}

@Test
public void testClassifyReturnsLabelVector() {
Alphabet dataAlphabet = new Alphabet();
int idx = dataAlphabet.lookupIndex("feat");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("yes");
labelAlphabet.lookupIndex("no");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
double[] parameters = new double[6];
Arrays.fill(parameters, 1.0);
MaxEnt classifier = new MaxEnt(pipe, parameters);
int[] indices = { idx };
double[] values = { 2.0 };
FeatureVector fv = new FeatureVector(dataAlphabet, indices, values);
Instance instance = new Instance(fv, "yes", "name", null);
Classification classification = classifier.classify(instance);
assertNotNull(classification);
assertNotNull(classification.getLabelVector());
// assertEquals(2, classification.getLabelVector().size());
}

@Test
public void testSerializationAndDeserialization() throws Exception {
Alphabet dataAlphabet = new Alphabet();
int idx = dataAlphabet.lookupIndex("token");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("positive");
labelAlphabet.lookupIndex("negative");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
double[] parameters = new double[6];
Arrays.fill(parameters, 2.0);
MaxEnt original = new MaxEnt(pipe, parameters);
original.setDefaultFeatureIndex(dataAlphabet.size());
ByteArrayOutputStream baos = new ByteArrayOutputStream();
ObjectOutputStream oos = new ObjectOutputStream(baos);
oos.writeObject(original);
oos.flush();
byte[] serializedBytes = baos.toByteArray();
ByteArrayInputStream bais = new ByteArrayInputStream(serializedBytes);
ObjectInputStream ois = new ObjectInputStream(bais);
MaxEnt deserialized = (MaxEnt) ois.readObject();
assertNotNull(deserialized);
assertEquals(parameters.length, deserialized.getParameters().length);
assertEquals(dataAlphabet.size(), deserialized.getDefaultFeatureIndex());
}

@Test(expected = ClassNotFoundException.class)
public void testDeserializeThrowsVersionMismatch() throws Exception {
ByteArrayOutputStream baos = new ByteArrayOutputStream();
DataOutputStream dos = new DataOutputStream(baos);
dos.writeInt(-999);
ObjectOutputStream oos = new ObjectOutputStream(baos);
Pipe dummyPipe = mock(Pipe.class);
oos.writeObject(dummyPipe);
oos.writeInt(2);
oos.writeDouble(0.1);
oos.writeDouble(0.2);
oos.writeInt(0);
oos.writeInt(-1);
oos.writeInt(-1);
oos.flush();
byte[] bytes = baos.toByteArray();
ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
ObjectInputStream ois = new ObjectInputStream(bais);
ois.readObject();
}

@Test
public void testGetNumParametersStaticMethod() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("a");
dataAlphabet.lookupIndex("b");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("X");
labelAlphabet.lookupIndex("Y");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
int result = MaxEnt.getNumParameters(pipe);
assertEquals((dataAlphabet.size() + 1) * labelAlphabet.size(), result);
}

@Test(expected = AssertionError.class)
public void testGetUnnormalizedClassificationScores_invalidScoresLength() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("f");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("L1");
labelAlphabet.lookupIndex("L2");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
double[] parameters = new double[6];
MaxEnt classifier = new MaxEnt(pipe, parameters);
int[] indices = { 0 };
double[] values = { 1.0 };
FeatureVector fv = new FeatureVector(dataAlphabet, indices, values);
Instance instance = new Instance(fv, "L1", null, null);
double[] scores = new double[1];
classifier.getUnnormalizedClassificationScores(instance, scores);
}

@Test(expected = AssertionError.class)
public void testGetUnnormalizedClassificationScores_mismatchedAlphabet() {
Alphabet trainAlphabet = new Alphabet();
trainAlphabet.lookupIndex("good");
Alphabet testAlphabet = new Alphabet();
testAlphabet.lookupIndex("bad");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("Positive");
labelAlphabet.lookupIndex("Negative");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(trainAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
double[] parameters = new double[6];
MaxEnt classifier = new MaxEnt(pipe, parameters);
int[] indices = { 0 };
double[] values = { 1.0 };
FeatureVector fv = new FeatureVector(testAlphabet, indices, values);
Instance instance = new Instance(fv, "Positive", null, null);
double[] scores = new double[2];
classifier.getUnnormalizedClassificationScores(instance, scores);
}

@Test
public void testClassificationProducesNonNaNProbabilities() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("x");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("A");
labelAlphabet.lookupIndex("B");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
double[] parameters = new double[6];
parameters[0] = 1000.0;
parameters[1] = 0.0;
parameters[2] = 0.0;
parameters[3] = 1000.0;
parameters[4] = 0.0;
parameters[5] = 0.0;
MaxEnt classifier = new MaxEnt(pipe, parameters);
int[] indices = { 0 };
double[] values = { 1.0 };
FeatureVector fv = new FeatureVector(dataAlphabet, indices, values);
Instance instance = new Instance(fv, "A", null, null);
double[] scores = new double[2];
classifier.getClassificationScores(instance, scores);
assertFalse(Double.isNaN(scores[0]));
assertFalse(Double.isNaN(scores[1]));
assertEquals(1.0, scores[0] + scores[1], 1e-6);
}

@Test
public void testPrintHandlesEmptyParameters() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
double[] parameters = new double[0];
MaxEnt classifier = new MaxEnt(pipe, parameters);
PrintWriter writer = new PrintWriter(System.out);
classifier.print(writer);
writer.flush();
}

@Test
public void testPrintExtremeFeaturesHandlesEmpty() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
MaxEnt classifier = new MaxEnt(pipe, new double[0]);
PrintWriter pw = new PrintWriter(System.out);
classifier.printExtremeFeatures(pw, 5);
pw.flush();
}

@Test
public void testSetDefaultFeatureIndexUpdatesIndex() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("f1");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("L1");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
double[] parameters = new double[4];
MaxEnt classifier = new MaxEnt(pipe, parameters);
assertEquals(1, classifier.getDefaultFeatureIndex());
classifier.setDefaultFeatureIndex(10);
assertEquals(10, classifier.getDefaultFeatureIndex());
}

@Test
public void testClassificationWithZeroWeights() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("feature");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("class1");
labelAlphabet.lookupIndex("class2");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
double[] parameters = new double[6];
MaxEnt classifier = new MaxEnt(pipe, parameters);
int[] indices = { 0 };
double[] values = { 1.0 };
FeatureVector fv = new FeatureVector(dataAlphabet, indices, values);
Instance instance = new Instance(fv, "class1", null, null);
Classification result = classifier.classify(instance);
assertNotNull(result);
LabelVector lv = result.getLabelVector();
double sum = lv.value(0) + lv.value(1);
assertEquals(1.0, sum, 1e-6);
}

@Test
public void testSerializationWithNullFeatureSelections() throws Exception {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("a");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("Z");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
double[] parameters = new double[2];
parameters[0] = 1.0;
parameters[1] = 2.0;
MaxEnt original = new MaxEnt(pipe, parameters);
ByteArrayOutputStream baos = new ByteArrayOutputStream();
ObjectOutputStream oos = new ObjectOutputStream(baos);
oos.writeObject(original);
oos.flush();
byte[] serialized = baos.toByteArray();
ByteArrayInputStream bais = new ByteArrayInputStream(serialized);
ObjectInputStream ois = new ObjectInputStream(bais);
MaxEnt restored = (MaxEnt) ois.readObject();
assertNotNull(restored);
assertEquals(2, restored.getParameters().length);
assertNull(restored.getFeatureSelection());
assertNull(restored.getPerClassFeatureSelection());
}

@Test
public void testEmptyAlphabetsConstructor() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
double[] parameters = new double[0];
MaxEnt classifier = new MaxEnt(pipe, parameters);
assertNotNull(classifier);
assertEquals(0, classifier.getParameters().length);
}

@Test
public void testFeatureSelectionMutualExclusionAssertion() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("f");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("L");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
double[] parameters = new double[2];
FeatureSelection sharedFS = mock(FeatureSelection.class);
FeatureSelection[] perClassFS = new FeatureSelection[1];
perClassFS[0] = sharedFS;
boolean caught = false;
try {
new MaxEnt(pipe, parameters, sharedFS, perClassFS);
} catch (AssertionError e) {
caught = true;
}
assertTrue("Mutual exclusion assertion not enforced", caught);
}

@Test
public void testSetNullPerClassFeatureSelection() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("f");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("L");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
double[] parameters = new double[2];
MaxEnt classifier = new MaxEnt(pipe, parameters);
classifier.setPerClassFeatureSelection(null);
assertNull(classifier.getPerClassFeatureSelection());
}

@Test
public void testGetNumParametersWithOneFeatureAndOneLabel() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("x");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("c");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
int result = MaxEnt.getNumParameters(pipe);
assertEquals(2, result);
}

@Test
public void testClassificationScores_withIdenticalProbabilities() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("token");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("alpha");
labelAlphabet.lookupIndex("beta");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
double[] parameters = new double[6];
parameters[0] = 0.0;
parameters[3] = 0.0;
MaxEnt classifier = new MaxEnt(pipe, parameters);
int[] indices = { 0 };
double[] values = { 0.0 };
FeatureVector fv = new FeatureVector(dataAlphabet, indices, values);
Instance instance = new Instance(fv, null, null, null);
double[] scores = new double[2];
classifier.getClassificationScores(instance, scores);
assertEquals(0.5, scores[0], 0.0001);
assertEquals(0.5, scores[1], 0.0001);
}

@Test
public void testPrintWithMultipleFeaturesAndLabels() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("a");
dataAlphabet.lookupIndex("b");
dataAlphabet.lookupIndex("c");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("class1");
labelAlphabet.lookupIndex("class2");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
double[] parameters = new double[8];
parameters[0] = 0.1;
parameters[1] = 0.2;
parameters[2] = 0.3;
parameters[3] = 0.4;
parameters[4] = 0.5;
parameters[5] = 0.6;
parameters[6] = 0.7;
parameters[7] = 0.8;
MaxEnt classifier = new MaxEnt(pipe, parameters);
StringWriter sw = new StringWriter();
PrintWriter pw = new PrintWriter(sw);
classifier.print(pw);
pw.flush();
String output = sw.toString();
assertTrue(output.contains("FEATURES FOR CLASS class1"));
assertTrue(output.contains("<default> 0.4"));
assertTrue(output.contains("a 0.1"));
assertTrue(output.contains("b 0.2"));
assertTrue(output.contains("c 0.3"));
assertTrue(output.contains("FEATURES FOR CLASS class2"));
assertTrue(output.contains("<default> 0.8"));
}

@Test
public void testSetParameterBeyondExpectedIndexHasNoCrash() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("x");
dataAlphabet.lookupIndex("y");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("A");
labelAlphabet.lookupIndex("B");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
int numFeatures = dataAlphabet.size();
int numLabels = labelAlphabet.size();
double[] parameters = new double[(numFeatures + 1) * numLabels];
MaxEnt classifier = new MaxEnt(pipe, parameters);
classifier.setParameter(0, 0, 42.0);
classifier.setParameter(0, 1, -3.0);
classifier.setParameter(1, 2, 12.5);
assertEquals(42.0, classifier.getParameters()[0], 0.001);
assertEquals(-3.0, classifier.getParameters()[1], 0.001);
assertEquals(12.5, classifier.getParameters()[5], 0.001);
}

@Test
public void testDeserializationIncompletePerClassSelection() throws Exception {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("w");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("Yes");
labelAlphabet.lookupIndex("No");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
double[] parameters = new double[6];
MaxEnt classifier = new MaxEnt(pipe, parameters);
ByteArrayOutputStream baos = new ByteArrayOutputStream();
ObjectOutputStream oos = new ObjectOutputStream(baos);
oos.writeInt(1);
oos.writeObject(pipe);
oos.writeInt(6);
oos.writeDouble(1);
oos.writeDouble(1);
oos.writeDouble(1);
oos.writeDouble(1);
oos.writeDouble(1);
oos.writeDouble(1);
oos.writeInt(1);
oos.writeInt(-1);
oos.writeInt(2);
oos.writeInt(-1);
oos.writeInt(-1);
oos.flush();
byte[] bytes = baos.toByteArray();
ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
ObjectInputStream ois = new ObjectInputStream(bais);
MaxEnt restored = (MaxEnt) ois.readObject();
assertNotNull(restored);
assertEquals(6, restored.getParameters().length);
assertNull(restored.getFeatureSelection());
assertEquals(2, restored.getPerClassFeatureSelection().length);
assertNull(restored.getPerClassFeatureSelection()[0]);
assertNull(restored.getPerClassFeatureSelection()[1]);
}

@Test
public void testZeroLabelsZeroFeatures() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
MaxEnt classifier = new MaxEnt(pipe, (double[]) null);
assertEquals(0, classifier.getParameters().length);
assertEquals(0, classifier.getAlphabet().size());
assertEquals(0, classifier.getLabelAlphabet().size());
}

@Test(expected = ArrayIndexOutOfBoundsException.class)
public void testGetClassificationScores_emptyParamsThrowsArrayIndexException() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("feat");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("class");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
MaxEnt classifier = new MaxEnt(pipe, new double[0]);
int[] indices = { 0 };
double[] values = { 1.0 };
FeatureVector fv = new FeatureVector(dataAlphabet, indices, values);
Instance instance = new Instance(fv, labelAlphabet.lookupLabel("class"), null, null);
double[] scores = new double[1];
classifier.getClassificationScores(instance, scores);
}

@Test
public void testSetNullFeatureSelectionExplicitly() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("x");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("L");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
double[] parameters = new double[2];
MaxEnt classifier = new MaxEnt(pipe, parameters);
classifier.setFeatureSelection(null);
assertNull(classifier.getFeatureSelection());
}

@Test
public void testClassifyWithNullTargetReturnsNonNull() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("x");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("label1");
labelAlphabet.lookupIndex("label2");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
double[] parameters = new double[6];
Arrays.fill(parameters, 0.5);
MaxEnt classifier = new MaxEnt(pipe, parameters);
int[] indices = { 0 };
double[] values = { 1.0 };
FeatureVector fv = new FeatureVector(dataAlphabet, indices, values);
Instance instance = new Instance(fv, null, "nullLabelInstance", "source");
Classification classification = classifier.classify(instance);
assertNotNull(classification);
assertNotNull(classification.getLabelVector());
// assertEquals(2, classification.getLabelVector().size());
}

@Test
public void testExtremeLargeInputVectorValues() {
Alphabet dataAlphabet = new Alphabet();
int index = dataAlphabet.lookupIndex("huge");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("positive");
labelAlphabet.lookupIndex("negative");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
double[] parameters = new double[6];
parameters[0] = 1e10;
parameters[3] = -1e10;
MaxEnt classifier = new MaxEnt(pipe, parameters);
int[] indices = { index };
double[] values = { 1e5 };
FeatureVector fv = new FeatureVector(dataAlphabet, indices, values);
Instance instance = new Instance(fv, null, "test", null);
double[] scores = new double[2];
classifier.getClassificationScores(instance, scores);
assertEquals(1.0, scores[0] + scores[1], 0.0001);
assertFalse(Double.isNaN(scores[0]));
assertFalse(Double.isNaN(scores[1]));
}

@Test
public void testDefaultFeatureIndexRespectedInScoring() {
Alphabet dataAlphabet = new Alphabet();
int index = dataAlphabet.lookupIndex("default");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("cat");
labelAlphabet.lookupIndex("dog");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
double[] parameters = new double[6];
parameters[2] = 2.0;
parameters[5] = -1.0;
MaxEnt classifier = new MaxEnt(pipe, parameters);
int[] indices = { index };
double[] values = { 0.0 };
FeatureVector fv = new FeatureVector(dataAlphabet, indices, values);
Instance instance = new Instance(fv, "cat", null, null);
double[] scores = new double[2];
classifier.getClassificationScores(instance, scores);
assertEquals(1.0, scores[0] + scores[1], 0.0001);
assertTrue(scores[0] > scores[1]);
}

@Test
public void testRankedFeatureVectorFromPrintRank() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("f1");
dataAlphabet.lookupIndex("f2");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("label");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
double[] parameters = new double[3];
parameters[0] = 2.5;
parameters[1] = -1.2;
parameters[2] = 0.0;
MaxEnt classifier = new MaxEnt(pipe, parameters);
StringWriter sw = new StringWriter();
PrintWriter pw = new PrintWriter(sw);
classifier.printRank(pw);
pw.flush();
String output = sw.toString();
assertTrue(output.contains("FEATURES FOR CLASS"));
assertTrue(output.contains("f1"));
assertTrue(output.contains("f2"));
assertTrue(output.contains("<default> 0.0"));
}

@Test
public void testGetClassificationScoresWithTemperatureZeroTemperature() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("feat");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("yes");
labelAlphabet.lookupIndex("no");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
double[] parameters = new double[6];
MaxEnt classifier = new MaxEnt(pipe, parameters);
int[] indices = { 0 };
double[] values = { 5.0 };
FeatureVector fv = new FeatureVector(dataAlphabet, indices, values);
Instance instance = new Instance(fv, null, null, null);
double[] scores = new double[2];
boolean caught = false;
try {
classifier.getClassificationScoresWithTemperature(instance, 0.0, scores);
} catch (ArithmeticException e) {
caught = true;
}
assertTrue(caught);
}

@Test
public void testSetAndGetParameterZeroFeatureZeroClass() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("a");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("Z");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
MaxEnt classifier = new MaxEnt(pipe, new double[2]);
classifier.setParameter(0, 0, 123.45);
double[] params = classifier.getParameters();
assertEquals(123.45, params[0], 0.0001);
}

@Test
public void testGetNumParametersMatchesStaticComputation() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("one");
dataAlphabet.lookupIndex("two");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("labelA");
labelAlphabet.lookupIndex("labelB");
labelAlphabet.lookupIndex("labelC");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
MaxEnt classifier = new MaxEnt(pipe, (double[]) null);
int expected = MaxEnt.getNumParameters(pipe);
int actual = classifier.getNumParameters();
assertEquals(expected, actual);
}

@Test
public void testSetAndGetDefaultFeatureIndex_CustomValue() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("abc");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("cls");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
MaxEnt model = new MaxEnt(pipe, new double[4]);
model.setDefaultFeatureIndex(99);
assertEquals(99, model.getDefaultFeatureIndex());
}

@Test
public void testGetPerClassFeatureSelectionAfterConstructionWithSharedNull() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("abc");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("yes");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
FeatureSelection fs = mock(FeatureSelection.class);
MaxEnt classifier = new MaxEnt(pipe, new double[2], fs, null);
assertNull(classifier.getPerClassFeatureSelection());
assertEquals(fs, classifier.getFeatureSelection());
}

@Test
public void testGetFeatureSelectionAfterConstructionWithPerClassOnly() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("f1");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("C1");
labelAlphabet.lookupIndex("C2");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
FeatureSelection[] perClassFS = new FeatureSelection[2];
perClassFS[0] = mock(FeatureSelection.class);
perClassFS[1] = mock(FeatureSelection.class);
MaxEnt classifier = new MaxEnt(pipe, new double[6], null, perClassFS);
assertNotNull(classifier.getPerClassFeatureSelection());
assertNull(classifier.getFeatureSelection());
}

@Test
public void testSetFeatureSelectionOverwritesPrevious() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("delta");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("yes");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
MaxEnt classifier = new MaxEnt(pipe, new double[2]);
FeatureSelection fs1 = mock(FeatureSelection.class);
FeatureSelection fs2 = mock(FeatureSelection.class);
classifier.setFeatureSelection(fs1);
assertEquals(fs1, classifier.getFeatureSelection());
classifier.setFeatureSelection(fs2);
assertEquals(fs2, classifier.getFeatureSelection());
}

@Test
public void testSetPerClassFeatureSelectionOverwrites() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("z");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("a1");
labelAlphabet.lookupIndex("a2");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
double[] parameters = new double[6];
MaxEnt classifier = new MaxEnt(pipe, parameters);
FeatureSelection[] fs1 = new FeatureSelection[2];
fs1[0] = mock(FeatureSelection.class);
fs1[1] = null;
FeatureSelection[] fs2 = new FeatureSelection[2];
fs2[0] = null;
fs2[1] = mock(FeatureSelection.class);
classifier.setPerClassFeatureSelection(fs1);
FeatureSelection[] set1 = classifier.getPerClassFeatureSelection();
assertEquals(fs1[0], set1[0]);
assertNull(set1[1]);
classifier.setPerClassFeatureSelection(fs2);
FeatureSelection[] set2 = classifier.getPerClassFeatureSelection();
assertNull(set2[0]);
assertEquals(fs2[1], set2[1]);
}

@Test
public void testDeserializeWithPartiallyNullFeatureSelections() throws Exception {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("f");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("YES");
labelAlphabet.lookupIndex("NO");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
double[] parameters = new double[6];
for (int i = 0; i < 6; i++) parameters[i] = i;
MaxEnt instance = new MaxEnt(pipe, parameters);
instance.setDefaultFeatureIndex(1);
FeatureSelection fs = mock(FeatureSelection.class);
FeatureSelection[] perClass = new FeatureSelection[2];
perClass[0] = null;
perClass[1] = fs;
instance.setPerClassFeatureSelection(perClass);
ByteArrayOutputStream baos = new ByteArrayOutputStream();
ObjectOutputStream oos = new ObjectOutputStream(baos);
oos.writeObject(instance);
oos.flush();
byte[] bytes = baos.toByteArray();
ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
ObjectInputStream ois = new ObjectInputStream(bais);
MaxEnt deserialized = (MaxEnt) ois.readObject();
assertNotNull(deserialized);
assertEquals(6, deserialized.getParameters().length);
assertEquals(1, deserialized.getDefaultFeatureIndex());
assertNull(deserialized.getFeatureSelection());
assertNotNull(deserialized.getPerClassFeatureSelection());
assertNull(deserialized.getPerClassFeatureSelection()[0]);
assertNotNull(deserialized.getPerClassFeatureSelection()[1]);
}

@Test
public void testClassificationScoresAllZeroFeatureInputs() {
Alphabet dataAlphabet = new Alphabet();
int idx = dataAlphabet.lookupIndex("a");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("first");
labelAlphabet.lookupIndex("second");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
double[] parameters = new double[6];
for (int i = 0; i < parameters.length; i++) parameters[i] = 0.0;
MaxEnt classifier = new MaxEnt(pipe, parameters);
int[] indices = { idx };
double[] values = { 0.0 };
FeatureVector fv = new FeatureVector(dataAlphabet, indices, values);
Instance instance = new Instance(fv, null, null, null);
double[] scores = new double[2];
classifier.getClassificationScores(instance, scores);
assertEquals(1.0, scores[0] + scores[1], 1e-6);
assertTrue(scores[0] >= 0);
assertTrue(scores[1] >= 0);
}

@Test
public void testGetUnnormalizedClassificationScoresAllZeroWeights() {
Alphabet dataAlphabet = new Alphabet();
int fidx = dataAlphabet.lookupIndex("f");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("a");
labelAlphabet.lookupIndex("b");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
double[] parameters = new double[6];
Arrays.fill(parameters, 0.0);
MaxEnt classifier = new MaxEnt(pipe, parameters);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { fidx }, new double[] { 1.0 });
Instance instance = new Instance(fv, null, null, null);
double[] scores = new double[2];
classifier.getUnnormalizedClassificationScores(instance, scores);
assertEquals(0.0, scores[0], 0.001);
assertEquals(0.0, scores[1], 0.001);
}

@Test
public void testFeatureVectorWithUnseenFeatureIndex() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("knownFeature");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("label");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
double[] parameters = new double[2];
MaxEnt classifier = new MaxEnt(pipe, parameters);
Alphabet testAlphabet = new Alphabet();
testAlphabet.lookupIndex("unknownFeature");
int[] indices = { 0 };
double[] values = { 1.0 };
FeatureVector fv = new FeatureVector(testAlphabet, indices, values);
Instance instance = new Instance(fv, "label", "name", null);
boolean failed = false;
try {
double[] scores = new double[1];
classifier.getClassificationScores(instance, scores);
} catch (AssertionError e) {
failed = true;
}
assertTrue(failed);
}

@Test
public void testClassificationWithAllNegativeWeights() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("dark");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("pos");
labelAlphabet.lookupIndex("neg");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
double[] parameters = new double[6];
Arrays.fill(parameters, -100.0);
MaxEnt classifier = new MaxEnt(pipe, parameters);
int[] indices = { 0 };
double[] values = { 1.0 };
FeatureVector fv = new FeatureVector(dataAlphabet, indices, values);
Instance instance = new Instance(fv, "pos", null, null);
double[] scores = new double[2];
classifier.getClassificationScores(instance, scores);
assertEquals(1.0, scores[0] + scores[1], 1e-6);
assertTrue(scores[0] >= 0);
assertTrue(scores[1] >= 0);
}

@Test
public void testClassificationScoresWithTemperatureSetToOne() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("temp");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("low");
labelAlphabet.lookupIndex("high");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
double[] params = new double[6];
params[0] = 1.0;
params[3] = 2.0;
MaxEnt classifier = new MaxEnt(pipe, params);
int[] indices = { 0 };
double[] values = { 3.0 };
FeatureVector fv = new FeatureVector(dataAlphabet, indices, values);
Instance instance = new Instance(fv, "low", "test", null);
double[] scores = new double[2];
classifier.getClassificationScoresWithTemperature(instance, 1.0, scores);
assertEquals(1.0, scores[0] + scores[1], 1e-6);
}

@Test
public void testPrintExtremeFeaturesHandlesZeroWeights() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("zero1");
dataAlphabet.lookupIndex("zero2");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("none");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
double[] parameters = new double[3];
parameters[0] = 0.0;
parameters[1] = 0.0;
parameters[2] = 0.0;
MaxEnt classifier = new MaxEnt(pipe, parameters);
StringWriter writer = new StringWriter();
PrintWriter pw = new PrintWriter(writer);
classifier.printExtremeFeatures(pw, 1);
pw.flush();
String output = writer.toString();
assertTrue(output.contains("FEATURES FOR CLASS"));
assertTrue(output.contains("<default>"));
}

@Test
public void testClassificationScoresExtremeTemperature() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("scale");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("one");
labelAlphabet.lookupIndex("two");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
double[] params = new double[6];
params[0] = 10.0;
params[3] = -10.0;
MaxEnt classifier = new MaxEnt(pipe, params);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
Instance instance = new Instance(fv, null, null, null);
double[] scores = new double[2];
classifier.getClassificationScoresWithTemperature(instance, Double.MAX_VALUE, scores);
assertEquals(1.0, scores[0] + scores[1], 1e-6);
assertTrue(scores[0] >= 0.0 && scores[0] <= 1.0);
assertTrue(scores[1] >= 0.0 && scores[1] <= 1.0);
}

@Test
public void testClassificationScoresOverflowProtection() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("boost");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("x");
labelAlphabet.lookupIndex("y");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
double[] params = new double[6];
params[0] = 1e10;
params[3] = -1e10;
MaxEnt classifier = new MaxEnt(pipe, params);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
Instance instance = new Instance(fv, null, null, null);
double[] scores = new double[2];
classifier.getClassificationScores(instance, scores);
assertEquals(1.0, scores[0] + scores[1], 1e-6);
assertFalse(Double.isNaN(scores[0]));
assertFalse(Double.isNaN(scores[1]));
}

@Test
public void testZeroFeatureVectorScoresFallbackToBiasOnly() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("a");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("c1");
labelAlphabet.lookupIndex("c2");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
double[] params = new double[6];
params[2] = 3.0;
params[5] = 0.0;
MaxEnt classifier = new MaxEnt(pipe, params);
int[] indices = {};
double[] values = {};
FeatureVector fv = new FeatureVector(dataAlphabet, indices, values);
Instance instance = new Instance(fv, null, "test", null);
double[] scores = new double[2];
classifier.getClassificationScores(instance, scores);
assertEquals(1.0, scores[0] + scores[1], 1e-6);
assertTrue(scores[0] > scores[1]);
}

@Test
public void testPrintRankHandlesOneLabelOnly() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("rank1");
dataAlphabet.lookupIndex("rank2");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("solo");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
double[] parameters = new double[3];
parameters[0] = 10.0;
parameters[1] = -10.0;
parameters[2] = 5.0;
MaxEnt classifier = new MaxEnt(pipe, parameters);
StringWriter sw = new StringWriter();
PrintWriter pw = new PrintWriter(sw);
classifier.printRank(pw);
pw.flush();
String output = sw.toString();
assertTrue(output.contains("rank1"));
assertTrue(output.contains("rank2"));
assertTrue(output.contains("<default> 5.0"));
}

@Test
public void testClassificationWithLargeNegativeDefaultWeights() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("input");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("happy");
labelAlphabet.lookupIndex("sad");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
double[] parameters = new double[6];
parameters[2] = -1e9;
parameters[5] = -1e9;
MaxEnt classifier = new MaxEnt(pipe, parameters);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 0.0 });
Instance instance = new Instance(fv, null, null, null);
double[] scores = new double[2];
classifier.getClassificationScores(instance, scores);
assertEquals(1.0, scores[0] + scores[1], 1e-6);
assertFalse(Double.isNaN(scores[0]));
assertFalse(Double.isNaN(scores[1]));
}

@Test(expected = IllegalArgumentException.class)
public void testFeatureVectorWithNullValuesCausesFailure() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("f1");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("class");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
MaxEnt classifier = new MaxEnt(pipe, new double[2]);
// new FeatureVector(dataAlphabet, null, null);
}

@Test
public void testFeatureSelectionAndPerClassCannotCoexistEnforced() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("a");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("A");
labelAlphabet.lookupIndex("B");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
double[] parameters = new double[6];
FeatureSelection shared = mock(FeatureSelection.class);
FeatureSelection[] perClass = new FeatureSelection[2];
perClass[0] = mock(FeatureSelection.class);
perClass[1] = mock(FeatureSelection.class);
boolean thrown = false;
try {
new MaxEnt(pipe, parameters, shared, perClass);
} catch (AssertionError e) {
thrown = true;
}
assertTrue(thrown);
}

@Test
public void testMultipleLabelsSingleFeatureClassification() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("x");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("l1");
labelAlphabet.lookupIndex("l2");
labelAlphabet.lookupIndex("l3");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
double[] parameters = new double[9];
parameters[2] = 10.0;
parameters[5] = 5.0;
parameters[8] = 0.0;
MaxEnt classifier = new MaxEnt(pipe, parameters);
int[] indices = {};
double[] values = {};
FeatureVector fv = new FeatureVector(dataAlphabet, indices, values);
Instance instance = new Instance(fv, null, null, null);
double[] scores = new double[3];
classifier.getClassificationScores(instance, scores);
assertEquals(1.0, scores[0] + scores[1] + scores[2], 1e-6);
assertTrue(scores[0] > scores[1]);
assertTrue(scores[1] > scores[2]);
}

@Test
public void testPrintPrintsAlphabetNames() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("spam");
dataAlphabet.lookupIndex("eggs");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("ham");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
double[] parameters = new double[3];
parameters[0] = 1.0;
parameters[1] = -2.0;
parameters[2] = 0.5;
MaxEnt classifier = new MaxEnt(pipe, parameters);
StringWriter sw = new StringWriter();
PrintWriter pw = new PrintWriter(sw);
classifier.print(pw);
pw.flush();
String result = sw.toString();
assertTrue(result.contains("spam"));
assertTrue(result.contains("eggs"));
assertTrue(result.contains("<default> 0.5"));
}

@Test
public void testSerializationWithNullFeatureSelectionFields() throws Exception {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("s1");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("zzz");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
MaxEnt original = new MaxEnt(pipe, new double[2]);
original.setDefaultFeatureIndex(1);
ByteArrayOutputStream bos = new ByteArrayOutputStream();
ObjectOutputStream oos = new ObjectOutputStream(bos);
oos.writeObject(original);
oos.flush();
byte[] data = bos.toByteArray();
ByteArrayInputStream bis = new ByteArrayInputStream(data);
ObjectInputStream ois = new ObjectInputStream(bis);
MaxEnt restored = (MaxEnt) ois.readObject();
assertNotNull(restored);
assertEquals(2, restored.getParameters().length);
assertEquals(1, restored.getDefaultFeatureIndex());
}

@Test
public void testDeserializeWithSinglePerClassFeatureSelection() throws Exception {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("x");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("yes");
labelAlphabet.lookupIndex("no");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
MaxEnt original = new MaxEnt(pipe, new double[6]);
original.setDefaultFeatureIndex(1);
FeatureSelection[] perClass = new FeatureSelection[2];
perClass[0] = null;
perClass[1] = mock(FeatureSelection.class);
original.setPerClassFeatureSelection(perClass);
ByteArrayOutputStream baos = new ByteArrayOutputStream();
ObjectOutputStream oos = new ObjectOutputStream(baos);
oos.writeObject(original);
oos.flush();
ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
ObjectInputStream ois = new ObjectInputStream(bais);
MaxEnt deserialized = (MaxEnt) ois.readObject();
assertNotNull(deserialized);
assertEquals(1, deserialized.getDefaultFeatureIndex());
assertEquals(2, deserialized.getPerClassFeatureSelection().length);
assertNull(deserialized.getPerClassFeatureSelection()[0]);
}

@Test
public void testSetParametersNullAndOverwrite() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("y");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("L");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
MaxEnt classifier = new MaxEnt(pipe, null);
assertTrue(classifier.getParameters().length > 0);
double[] newParams = new double[classifier.getParameters().length];
newParams[0] = 3.14;
classifier.setParameters(newParams);
assertEquals(3.14, classifier.getParameters()[0], 0.001);
}

@Test
public void testUnnormalizedScoreMatchesDotProductComputation() {
Alphabet dataAlphabet = new Alphabet();
int idx = dataAlphabet.lookupIndex("token");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("l1");
labelAlphabet.lookupIndex("l2");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
double[] parameters = new double[6];
parameters[0] = 1.0;
parameters[1] = 0.0;
parameters[2] = 0.0;
parameters[3] = 2.0;
parameters[4] = 0.0;
parameters[5] = 0.0;
MaxEnt classifier = new MaxEnt(pipe, parameters);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { idx }, new double[] { 5.0 });
Instance instance = new Instance(fv, null, null, null);
double[] scores = new double[2];
classifier.getUnnormalizedClassificationScores(instance, scores);
assertEquals(1.0 * 5.0 + 0.0, scores[0], 0.001);
assertEquals(2.0 * 5.0 + 0.0, scores[1], 0.001);
}
}
