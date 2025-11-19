package cc.mallet.classify.tests;

import cc.mallet.classify.Classification;
import cc.mallet.classify.MaxEnt;
import cc.mallet.pipe.*;
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

public class MaxEnt_3_GPTLLMTest {

@Test
public void testConstructorWithFeatureSelection() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("feature0");
dataAlphabet.lookupIndex("feature1");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("labelA");
labelAlphabet.lookupIndex("labelB");
Pipe mockPipe = mock(Pipe.class);
when(mockPipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(mockPipe.getTargetAlphabet()).thenReturn(labelAlphabet);
int numFeatures = dataAlphabet.size();
int numLabels = labelAlphabet.size();
double[] parameters = new double[(numFeatures + 1) * numLabels];
FeatureSelection featureSelection = mock(FeatureSelection.class);
MaxEnt maxEnt = new MaxEnt(mockPipe, parameters, featureSelection);
assertArrayEquals(parameters, maxEnt.getParameters(), 0.000001);
assertEquals(numFeatures, maxEnt.getDefaultFeatureIndex());
assertSame(featureSelection, maxEnt.getFeatureSelection());
assertNull(maxEnt.getPerClassFeatureSelection());
}

@Test
public void testSetAndGetParameters() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("f1");
dataAlphabet.lookupIndex("f2");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("L0");
labelAlphabet.lookupIndex("L1");
Pipe mockPipe = mock(Pipe.class);
when(mockPipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(mockPipe.getTargetAlphabet()).thenReturn(labelAlphabet);
double[] initialParams = new double[(dataAlphabet.size() + 1) * labelAlphabet.size()];
MaxEnt maxEnt = new MaxEnt(mockPipe, initialParams);
double[] newParams = new double[initialParams.length];
Arrays.fill(newParams, 0.5);
maxEnt.setParameters(newParams);
assertArrayEquals(newParams, maxEnt.getParameters(), 0.00001);
}

@Test
public void testSetSingleParameterUpdatesParameterArray() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("f0");
dataAlphabet.lookupIndex("f1");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("class1");
labelAlphabet.lookupIndex("class2");
Pipe mockPipe = mock(Pipe.class);
when(mockPipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(mockPipe.getTargetAlphabet()).thenReturn(labelAlphabet);
double[] parameters = new double[(dataAlphabet.size() + 1) * labelAlphabet.size()];
MaxEnt maxEnt = new MaxEnt(mockPipe, parameters);
maxEnt.setParameter(1, 1, 42.0);
double[] actual = maxEnt.getParameters();
assertEquals(42.0, actual[(dataAlphabet.size() + 1) * 1 + 1], 0.00001);
}

@Test
public void testGetNumParametersFromInstancePipe() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("featA");
dataAlphabet.lookupIndex("featB");
int featureSize = dataAlphabet.size();
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("yes");
labelAlphabet.lookupIndex("no");
int labelSize = labelAlphabet.size();
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
MaxEnt maxEnt = new MaxEnt(pipe, null);
int expected = (featureSize + 1) * labelSize;
assertEquals(expected, maxEnt.getNumParameters());
}

@Test
public void testGetUnnormalizedClassificationScoresUsesOnlyBias() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("word1");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("cat");
labelAlphabet.lookupIndex("dog");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
int numFeatures = dataAlphabet.size();
int numLabels = labelAlphabet.size();
double[] params = new double[(numFeatures + 1) * numLabels];
params[numFeatures + 0] = 1.0;
params[(numFeatures + 1) + numFeatures] = 2.0;
MaxEnt maxEnt = new MaxEnt(pipe, params);
FeatureVector fv = mock(FeatureVector.class);
when(fv.getAlphabet()).thenReturn(dataAlphabet);
Instance inst = mock(Instance.class);
when(inst.getData()).thenReturn(fv);
double[] scores = new double[numLabels];
maxEnt.getUnnormalizedClassificationScores(inst, scores);
assertEquals(1.0, scores[0], 0.00001);
assertEquals(2.0, scores[1], 0.00001);
}

@Test
public void testGetClassificationScoresWithEqualBiasReturnsUniformProbabilities() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("f");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("l0");
labelAlphabet.lookupIndex("l1");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
double[] params = new double[(dataAlphabet.size() + 1) * labelAlphabet.size()];
MaxEnt maxEnt = new MaxEnt(pipe, params);
FeatureVector fv = mock(FeatureVector.class);
when(fv.getAlphabet()).thenReturn(dataAlphabet);
Instance inst = mock(Instance.class);
when(inst.getData()).thenReturn(fv);
double[] scores = new double[labelAlphabet.size()];
maxEnt.getClassificationScores(inst, scores);
assertEquals(0.5, scores[0], 0.0001);
assertEquals(0.5, scores[1], 0.0001);
}

@Test
public void testGetClassificationScoresWithTemperatureInfluence() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("x");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("a");
labelAlphabet.lookupIndex("b");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
double[] params = new double[(dataAlphabet.size() + 1) * labelAlphabet.size()];
params[dataAlphabet.size()] = 0.0;
params[(dataAlphabet.size() + 1) + dataAlphabet.size()] = 2.0;
MaxEnt maxEnt = new MaxEnt(pipe, params);
FeatureVector fv = mock(FeatureVector.class);
when(fv.getAlphabet()).thenReturn(dataAlphabet);
Instance inst = mock(Instance.class);
when(inst.getData()).thenReturn(fv);
double[] scores = new double[labelAlphabet.size()];
maxEnt.getClassificationScoresWithTemperature(inst, 1.5, scores);
assertTrue(scores[1] > scores[0]);
}

@Test
public void testClassifyReturnsCorrectLabelVector() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("t");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("s1");
labelAlphabet.lookupIndex("s2");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
double[] params = new double[(dataAlphabet.size() + 1) * labelAlphabet.size()];
params[(dataAlphabet.size() + 1)] = 10.0;
MaxEnt maxEnt = new MaxEnt(pipe, params);
FeatureVector fv = mock(FeatureVector.class);
when(fv.getAlphabet()).thenReturn(dataAlphabet);
Instance inst = mock(Instance.class);
when(inst.getData()).thenReturn(fv);
Classification c = maxEnt.classify(inst);
LabelVector lv = (LabelVector) c.getLabelVector();
assertTrue(lv.value(1) > lv.value(0));
}

@Test
public void testSerializationAndDeserializationRetainsParameters() throws Exception {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("k");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("good");
labelAlphabet.lookupIndex("bad");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
double[] params = new double[(dataAlphabet.size() + 1) * labelAlphabet.size()];
Arrays.fill(params, 1.2);
MaxEnt original = new MaxEnt(pipe, params);
ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
ObjectOutputStream oos = new ObjectOutputStream(byteOut);
oos.writeObject(original);
oos.close();
ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
ObjectInputStream ois = new ObjectInputStream(byteIn);
MaxEnt restored = (MaxEnt) ois.readObject();
assertNotNull(restored.getParameters());
double[] restoredParams = restored.getParameters();
assertEquals(params.length, restoredParams.length);
assertEquals(params[0], restoredParams[0], 0.00001);
}

@Test
public void testPrintDoesNotThrowError() {
Alphabet da = new Alphabet();
da.lookupIndex("a");
da.lookupIndex("b");
LabelAlphabet la = new LabelAlphabet();
la.lookupIndex("cat");
la.lookupIndex("dog");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(da);
when(pipe.getTargetAlphabet()).thenReturn(la);
int num = (da.size() + 1) * la.size();
double[] param = new double[num];
Arrays.fill(param, 1.0);
MaxEnt maxEnt = new MaxEnt(pipe, param);
StringWriter sw = new StringWriter();
PrintWriter pw = new PrintWriter(sw);
maxEnt.print(pw);
pw.flush();
String output = sw.toString();
assertTrue(output.contains("FEATURES FOR CLASS"));
}

@Test
public void testConstructorWithNullParametersDefaultsToZeroInitialized() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("f1");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("label");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
MaxEnt maxEnt = new MaxEnt(pipe, null);
double[] params = maxEnt.getParameters();
assertNotNull(params);
assertEquals((dataAlphabet.size() + 1) * labelAlphabet.size(), params.length);
assertEquals(0.0, params[0], 1e-9);
}

@Test
public void testConstructorWithFeatureSelectionAndPerClassFeatureSelectionThrowsAssertionError() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("f");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("l");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
double[] params = new double[(dataAlphabet.size() + 1)];
FeatureSelection fs = mock(FeatureSelection.class);
FeatureSelection[] fss = new FeatureSelection[] { fs };
boolean assertionThrown = false;
try {
new MaxEnt(pipe, params, fs, fss);
} catch (AssertionError e) {
assertionThrown = true;
}
assertTrue(assertionThrown);
}

@Test
public void testClassificationReturnsNaNProbabilityIfTemperatureIsZero() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("x");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("A");
labelAlphabet.lookupIndex("B");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
double[] params = new double[(dataAlphabet.size() + 1) * labelAlphabet.size()];
Arrays.fill(params, 1.0);
MaxEnt maxEnt = new MaxEnt(pipe, params);
FeatureVector fv = mock(FeatureVector.class);
when(fv.getAlphabet()).thenReturn(dataAlphabet);
Instance instance = mock(Instance.class);
when(instance.getData()).thenReturn(fv);
double[] scores = new double[labelAlphabet.size()];
try {
maxEnt.getClassificationScoresWithTemperature(instance, 0.0, scores);
} catch (ArithmeticException e) {
assertTrue(e.getMessage().contains("/ by zero"));
}
}

@Test
public void testPrintExtremeFeaturesWithZeroFeatures() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("y");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
double[] params = new double[(dataAlphabet.size() + 1) * labelAlphabet.size()];
MaxEnt maxEnt = new MaxEnt(pipe, params);
StringWriter sw = new StringWriter();
PrintWriter out = new PrintWriter(sw);
maxEnt.printExtremeFeatures(out, 0);
out.flush();
String output = sw.toString();
assertTrue(output.contains("<default>"));
}

@Test
public void testPrintRankHandlesEmptyFeatureAlphabet() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("class");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
MaxEnt maxEnt = new MaxEnt(pipe, new double[1]);
StringWriter writer = new StringWriter();
PrintWriter pw = new PrintWriter(writer);
maxEnt.printRank(pw);
pw.flush();
String result = writer.toString();
assertTrue(result.contains("FEATURES FOR CLASS"));
}

@Test
public void testSetPerClassFeatureSelectionToNullOverridesPrevious() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("x");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("y");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
double[] params = new double[(dataAlphabet.size() + 1)];
MaxEnt maxEnt = new MaxEnt(pipe, params);
FeatureSelection[] fss = new FeatureSelection[] { mock(FeatureSelection.class) };
maxEnt.setPerClassFeatureSelection(fss);
FeatureSelection[] currentFSS = maxEnt.getPerClassFeatureSelection();
assertEquals(fss[0], currentFSS[0]);
maxEnt.setPerClassFeatureSelection(null);
assertNull(maxEnt.getPerClassFeatureSelection());
}

@Test
public void testDeserializeWithoutFeatureSelectionFields() throws Exception {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("x");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("yes");
labelAlphabet.lookupIndex("no");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
int size = (dataAlphabet.size() + 1) * labelAlphabet.size();
double[] params = new double[size];
Arrays.fill(params, 0.3);
MaxEnt before = new MaxEnt(pipe, params);
ByteArrayOutputStream baos = new ByteArrayOutputStream();
ObjectOutputStream oos = new ObjectOutputStream(baos);
oos.writeObject(before);
oos.close();
ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
ObjectInputStream ois = new ObjectInputStream(bais);
MaxEnt after = (MaxEnt) ois.readObject();
assertNotNull(after.getParameters());
assertNull(after.getFeatureSelection());
assertNull(after.getPerClassFeatureSelection());
}

@Test
public void testGetClassificationScoresWithNaNScoresProducesNoNaNOutput() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("f1");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("c1");
labelAlphabet.lookupIndex("c2");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
int numParams = (dataAlphabet.size() + 1) * labelAlphabet.size();
double[] param = new double[numParams];
Arrays.fill(param, Double.POSITIVE_INFINITY);
MaxEnt maxEnt = new MaxEnt(pipe, param);
FeatureVector fv = mock(FeatureVector.class);
when(fv.getAlphabet()).thenReturn(dataAlphabet);
Instance instance = mock(Instance.class);
when(instance.getData()).thenReturn(fv);
double[] scores = new double[labelAlphabet.size()];
try {
maxEnt.getClassificationScores(instance, scores);
} catch (Exception e) {
fail("Should not throw: " + e.getMessage());
}
assertFalse(Double.isNaN(scores[0]));
assertFalse(Double.isNaN(scores[1]));
}

@Test
public void testSetFeatureSelectionNullifiesPrevious() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("f");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("l");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
double[] param = new double[(dataAlphabet.size() + 1) * labelAlphabet.size()];
MaxEnt maxEnt = new MaxEnt(pipe, param);
FeatureSelection fs = mock(FeatureSelection.class);
maxEnt.setFeatureSelection(fs);
assertNotNull(maxEnt.getFeatureSelection());
maxEnt.setFeatureSelection(null);
assertNull(maxEnt.getFeatureSelection());
}

@Test
public void testGetUnnormalizedClassificationScoresWithNullFeatureSelection() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("f1");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("L1");
labelAlphabet.lookupIndex("L2");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
int featureSize = dataAlphabet.size();
int labelSize = labelAlphabet.size();
int paramSize = (featureSize + 1) * labelSize;
double[] parameters = new double[paramSize];
parameters[featureSize] = 2.0;
parameters[(featureSize + 1) + featureSize] = 3.0;
MaxEnt maxEnt = new MaxEnt(pipe, parameters, null, null);
FeatureVector fv = mock(FeatureVector.class);
when(fv.getAlphabet()).thenReturn(dataAlphabet);
when(fv.numLocations()).thenReturn(0);
Instance inst = mock(Instance.class);
when(inst.getData()).thenReturn(fv);
double[] scores = new double[labelSize];
maxEnt.getUnnormalizedClassificationScores(inst, scores);
assertEquals(2.0, scores[0], 0.000001);
assertEquals(3.0, scores[1], 0.000001);
}

@Test
public void testGetUnnormalizedClassificationScoresWithNullPerClassSelection() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("f");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("A");
labelAlphabet.lookupIndex("B");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
double[] parameters = new double[(dataAlphabet.size() + 1) * labelAlphabet.size()];
Arrays.fill(parameters, 0.5);
MaxEnt maxEnt = new MaxEnt(pipe, parameters);
FeatureVector fv = mock(FeatureVector.class);
when(fv.getAlphabet()).thenReturn(dataAlphabet);
when(fv.numLocations()).thenReturn(0);
Instance inst = mock(Instance.class);
when(inst.getData()).thenReturn(fv);
double[] scores = new double[labelAlphabet.size()];
maxEnt.getUnnormalizedClassificationScores(inst, scores);
assertEquals(0.5, scores[0], 0.00001);
assertEquals(0.5, scores[1], 0.00001);
}

@Test
public void testClassificationScoresOutputNormalized() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("a");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("X");
labelAlphabet.lookupIndex("Y");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
double[] parameters = new double[(dataAlphabet.size() + 1) * labelAlphabet.size()];
parameters[dataAlphabet.size()] = 0.0;
parameters[(dataAlphabet.size() + 1) + dataAlphabet.size()] = 1.0;
MaxEnt maxEnt = new MaxEnt(pipe, parameters);
FeatureVector fv = mock(FeatureVector.class);
when(fv.getAlphabet()).thenReturn(dataAlphabet);
when(fv.numLocations()).thenReturn(0);
Instance inst = mock(Instance.class);
when(inst.getData()).thenReturn(fv);
double[] scores = new double[labelAlphabet.size()];
maxEnt.getClassificationScores(inst, scores);
double sum = scores[0] + scores[1];
assertEquals(1.0, sum, 0.00001);
}

@Test
public void testPrintHandlesMultipleClassesAndFeatures() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("f1");
dataAlphabet.lookupIndex("f2");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("spam");
labelAlphabet.lookupIndex("ham");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
int numParams = (dataAlphabet.size() + 1) * labelAlphabet.size();
double[] param = new double[numParams];
for (int i = 0; i < numParams; i++) {
param[i] = i * 0.1;
}
MaxEnt maxEnt = new MaxEnt(pipe, param);
StringWriter sw = new StringWriter();
PrintWriter pw = new PrintWriter(sw);
maxEnt.print(pw);
pw.flush();
String output = sw.toString();
assertTrue(output.contains("FEATURES FOR CLASS spam"));
assertTrue(output.contains("FEATURES FOR CLASS ham"));
assertTrue(output.contains("f1"));
assertTrue(output.contains("f2"));
}

@Test
public void testPrintExtremeFeaturesPrintsTopAndBottomWeights() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("a");
dataAlphabet.lookupIndex("b");
dataAlphabet.lookupIndex("c");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("pos");
labelAlphabet.lookupIndex("neg");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
int numFeatures = dataAlphabet.size();
int numLabels = labelAlphabet.size();
int numParams = (numFeatures + 1) * numLabels;
double[] param = new double[numParams];
param[0] = 0.5;
param[1] = 0.9;
param[2] = -0.4;
MaxEnt maxEnt = new MaxEnt(pipe, param);
StringWriter sw = new StringWriter();
PrintWriter out = new PrintWriter(sw);
maxEnt.printExtremeFeatures(out, 1);
out.flush();
String result = sw.toString();
assertTrue(result.contains("<default>"));
assertTrue(result.contains("FEATURES FOR CLASS"));
}

@Test
public void testSetDefaultFeatureIndexUpdatesCorrectly() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("x0");
dataAlphabet.lookupIndex("x1");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("y");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
MaxEnt maxEnt = new MaxEnt(pipe, new double[(dataAlphabet.size() + 1) * labelAlphabet.size()]);
maxEnt.setDefaultFeatureIndex(99);
assertEquals(99, maxEnt.getDefaultFeatureIndex());
}

@Test
public void testPrintToPrintStreamUsesCorrectAPI() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("xx");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("yy");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
double[] parameters = new double[(dataAlphabet.size() + 1) * labelAlphabet.size()];
parameters[0] = 0.6;
MaxEnt maxEnt = new MaxEnt(pipe, parameters);
ByteArrayOutputStream baos = new ByteArrayOutputStream();
PrintStream ps = new PrintStream(baos);
maxEnt.print(ps);
ps.flush();
String printed = baos.toString();
assertTrue(printed.contains("FEATURES FOR CLASS yy"));
}

@Test
public void testPrintRankPrintsInExpectedFormatEvenForIdenticalWeights() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("a");
dataAlphabet.lookupIndex("b");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("m");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
double[] parameters = new double[(dataAlphabet.size() + 1) * labelAlphabet.size()];
parameters[0] = 1.0;
parameters[1] = 1.0;
MaxEnt maxEnt = new MaxEnt(pipe, parameters);
StringWriter output = new StringWriter();
PrintWriter writer = new PrintWriter(output);
maxEnt.printRank(writer);
writer.flush();
String result = output.toString();
assertTrue(result.contains("FEATURES FOR CLASS"));
}

@Test
public void testSerializeWithNullFeatureSelectionAndNullPerClassFeatureSelection() throws IOException, ClassNotFoundException {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("alpha");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("class");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
MaxEnt maxEnt = new MaxEnt(pipe, new double[(dataAlphabet.size() + 1) * labelAlphabet.size()], null, null);
ByteArrayOutputStream baos = new ByteArrayOutputStream();
ObjectOutputStream oos = new ObjectOutputStream(baos);
oos.writeObject(maxEnt);
oos.close();
ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
ObjectInputStream ois = new ObjectInputStream(bais);
MaxEnt deserialized = (MaxEnt) ois.readObject();
assertNotNull(deserialized.getParameters());
assertNull(deserialized.getFeatureSelection());
assertNull(deserialized.getPerClassFeatureSelection());
}

@Test
public void testSerializeWithPerClassFeatureSelectionContainingNulls() throws IOException, ClassNotFoundException {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("f");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("c1");
labelAlphabet.lookupIndex("c2");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
double[] params = new double[(dataAlphabet.size() + 1) * labelAlphabet.size()];
FeatureSelection[] perClassFS = new FeatureSelection[] { null, mock(FeatureSelection.class) };
MaxEnt maxEnt = new MaxEnt(pipe, params, null, perClassFS);
ByteArrayOutputStream stream = new ByteArrayOutputStream();
ObjectOutputStream out = new ObjectOutputStream(stream);
out.writeObject(maxEnt);
out.close();
ByteArrayInputStream input = new ByteArrayInputStream(stream.toByteArray());
ObjectInputStream objIn = new ObjectInputStream(input);
MaxEnt restored = (MaxEnt) objIn.readObject();
assertNotNull(restored.getParameters());
FeatureSelection[] restoredFSS = restored.getPerClassFeatureSelection();
assertEquals(2, restoredFSS.length);
assertNull(restoredFSS[0]);
}

@Test
public void testClassificationFailsIfFeatureAlphabetMismatched() {
Alphabet trainingAlphabet = new Alphabet();
trainingAlphabet.lookupIndex("train");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("label");
Alphabet fvAlphabet = new Alphabet();
fvAlphabet.lookupIndex("feature");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(trainingAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
double[] params = new double[(trainingAlphabet.size() + 1) * labelAlphabet.size()];
MaxEnt maxEnt = new MaxEnt(pipe, params);
Alphabet testAlphabet = new Alphabet();
testAlphabet.lookupIndex("different");
FeatureVector fv = new FeatureVector(testAlphabet, new int[] { 0 }, new double[] { 1.0 });
Instance instance = new Instance(fv, null, null, null);
double[] scores = new double[labelAlphabet.size()];
boolean assertionFailed = false;
try {
maxEnt.getUnnormalizedClassificationScores(instance, scores);
} catch (AssertionError e) {
assertionFailed = true;
}
assertTrue(assertionFailed);
}

@Test
public void testGetNumParametersHandlesUninitializedLabelAlphabet() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("f1");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(null);
boolean assertionThrown = false;
try {
MaxEnt.getNumParameters(pipe);
} catch (NullPointerException e) {
assertionThrown = true;
}
assertTrue(assertionThrown);
}

@Test
public void testClassificationWithOnlyOneLabel() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("x");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("only");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
double[] params = new double[(dataAlphabet.size() + 1) * labelAlphabet.size()];
params[params.length - 1] = 1000.0;
MaxEnt maxEnt = new MaxEnt(pipe, params);
FeatureVector fv = mock(FeatureVector.class);
when(fv.getAlphabet()).thenReturn(dataAlphabet);
when(fv.numLocations()).thenReturn(0);
Instance inst = mock(Instance.class);
when(inst.getData()).thenReturn(fv);
Classification c = maxEnt.classify(inst);
assertNotNull(c);
assertEquals(1, c.getLabelVector().numLocations());
assertEquals(1.0, c.getLabelVector().value(0), 0.000001);
}

@Test
public void testClassificationScoresWithLargeDifferencesNormalizedCorrectly() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("a");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("one");
labelAlphabet.lookupIndex("two");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
int fsize = dataAlphabet.size();
int lsize = labelAlphabet.size();
double[] params = new double[(fsize + 1) * lsize];
params[fsize] = -1000.0;
params[(fsize + 1) + fsize] = 1000.0;
MaxEnt maxEnt = new MaxEnt(pipe, params);
FeatureVector fv = mock(FeatureVector.class);
when(fv.getAlphabet()).thenReturn(dataAlphabet);
when(fv.numLocations()).thenReturn(0);
Instance inst = mock(Instance.class);
when(inst.getData()).thenReturn(fv);
double[] scores = new double[lsize];
maxEnt.getClassificationScores(inst, scores);
assertEquals(0.0, scores[0], 1e-10);
assertEquals(1.0, scores[1], 1e-10);
}

@Test
public void testClassificationScoresWithNegativeInfinityBias() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("z");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("low");
labelAlphabet.lookupIndex("high");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
double[] params = new double[(dataAlphabet.size() + 1) * labelAlphabet.size()];
params[dataAlphabet.size()] = Double.NEGATIVE_INFINITY;
params[(dataAlphabet.size() + 1) + dataAlphabet.size()] = 0.0;
MaxEnt maxEnt = new MaxEnt(pipe, params);
FeatureVector fv = mock(FeatureVector.class);
when(fv.getAlphabet()).thenReturn(dataAlphabet);
Instance inst = mock(Instance.class);
when(inst.getData()).thenReturn(fv);
double[] scores = new double[labelAlphabet.size()];
maxEnt.getClassificationScores(inst, scores);
assertEquals(0.0, scores[0], 1e-9);
assertEquals(1.0, scores[1], 1e-9);
}

@Test
public void testPrintExtremeFeaturesWithDuplicateWeights() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("d1");
dataAlphabet.lookupIndex("d2");
dataAlphabet.lookupIndex("d3");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("pos");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
double[] params = new double[(dataAlphabet.size() + 1)];
params[0] = 0.5;
params[1] = 0.5;
params[2] = 0.5;
MaxEnt maxEnt = new MaxEnt(pipe, params);
StringWriter output = new StringWriter();
PrintWriter writer = new PrintWriter(output);
maxEnt.printExtremeFeatures(writer, 2);
writer.flush();
String out = output.toString();
assertTrue(out.contains("<default>"));
assertTrue(out.contains("FEATURES FOR CLASS"));
}

@Test
public void testGetClassificationScoresWithZeroSumDoesNotCrash() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("feat");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("labelA");
labelAlphabet.lookupIndex("labelB");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
double[] params = new double[(dataAlphabet.size() + 1) * labelAlphabet.size()];
params[dataAlphabet.size()] = Double.NEGATIVE_INFINITY;
params[(dataAlphabet.size() + 1) + dataAlphabet.size()] = Double.NEGATIVE_INFINITY;
MaxEnt maxEnt = new MaxEnt(pipe, params);
FeatureVector fv = mock(FeatureVector.class);
when(fv.getAlphabet()).thenReturn(dataAlphabet);
Instance inst = mock(Instance.class);
when(inst.getData()).thenReturn(fv);
double[] scores = new double[labelAlphabet.size()];
maxEnt.getClassificationScores(inst, scores);
assertFalse(Double.isNaN(scores[0]));
assertFalse(Double.isNaN(scores[1]));
assertEquals(0.5, scores[0], 1e-6);
assertEquals(0.5, scores[1], 1e-6);
}

@Test
public void testConstructorWithNullPipeThrowsExceptionOnGetNumParameters() {
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(null);
when(pipe.getTargetAlphabet()).thenReturn(null);
boolean threw = false;
try {
MaxEnt.getNumParameters(pipe);
} catch (NullPointerException e) {
threw = true;
}
assertTrue(threw);
}

@Test
public void testClassificationWithAllZeroParametersReturnsUniformDistribution() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("x");
dataAlphabet.lookupIndex("y");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("label1");
labelAlphabet.lookupIndex("label2");
labelAlphabet.lookupIndex("label3");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
int size = (dataAlphabet.size() + 1) * labelAlphabet.size();
double[] params = new double[size];
MaxEnt maxEnt = new MaxEnt(pipe, params);
FeatureVector fv = mock(FeatureVector.class);
when(fv.getAlphabet()).thenReturn(dataAlphabet);
Instance inst = mock(Instance.class);
when(inst.getData()).thenReturn(fv);
Classification classification = maxEnt.classify(inst);
LabelVector scoreVector = (LabelVector) classification.getLabelVector();
double sum = 0.0;
sum += scoreVector.value(0);
sum += scoreVector.value(1);
sum += scoreVector.value(2);
assertEquals(1.0, sum, 1e-6);
}

@Test
public void testReadObjectWithInvalidVersionThrowsClassNotFoundException() throws Exception {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("abc");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("def");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
double[] params = new double[(dataAlphabet.size() + 1) * labelAlphabet.size()];
MaxEnt maxEnt = new MaxEnt(pipe, params);
ByteArrayOutputStream baos = new ByteArrayOutputStream();
ObjectOutputStream oos = new ObjectOutputStream(baos);
oos.writeInt(999);
oos.close();
ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
ObjectInputStream ois = new ObjectInputStream(bais);
MaxEnt maxEntInstance = MaxEnt.class.getDeclaredConstructor().newInstance();
boolean exceptionThrown = false;
try {
java.lang.reflect.Method readObj = MaxEnt.class.getDeclaredMethod("readObject", ObjectInputStream.class);
readObj.setAccessible(true);
readObj.invoke(maxEntInstance, ois);
} catch (Exception e) {
Throwable cause = e.getCause();
if (cause instanceof ClassNotFoundException) {
exceptionThrown = true;
}
}
assertTrue(exceptionThrown);
}

@Test
public void testSetFeatureSelectionToSameValueTwiceDoesNotCauseError() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("q");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("w");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
FeatureSelection fs = mock(FeatureSelection.class);
double[] params = new double[(dataAlphabet.size() + 1) * labelAlphabet.size()];
MaxEnt maxEnt = new MaxEnt(pipe, params);
maxEnt.setFeatureSelection(fs);
maxEnt.setFeatureSelection(fs);
assertSame(fs, maxEnt.getFeatureSelection());
}

@Test
public void testSetPerClassFeatureSelectionWithMixedNullAndNonNullEntries() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("m");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("x");
labelAlphabet.lookupIndex("y");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
FeatureSelection[] selectors = new FeatureSelection[2];
selectors[0] = mock(FeatureSelection.class);
selectors[1] = null;
MaxEnt maxEnt = new MaxEnt(pipe, new double[(dataAlphabet.size() + 1) * labelAlphabet.size()]);
maxEnt.setPerClassFeatureSelection(selectors);
FeatureSelection[] retrieved = maxEnt.getPerClassFeatureSelection();
assertSame(selectors[0], retrieved[0]);
assertNull(retrieved[1]);
}

@Test
public void testPrintHandlesEmptyAlphabetAndLabels() {
Alphabet dataAlphabet = new Alphabet();
LabelAlphabet labelAlphabet = new LabelAlphabet();
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
MaxEnt maxEnt = new MaxEnt(pipe, new double[1]);
StringWriter sw = new StringWriter();
PrintWriter pw = new PrintWriter(sw);
maxEnt.print(pw);
pw.flush();
String result = sw.toString();
assertTrue(result.isEmpty());
}

@Test
public void testPrintExtremeFeaturesWithKGreaterThanFeatureSize() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("f0");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("c0");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
double[] params = new double[(dataAlphabet.size() + 1) * labelAlphabet.size()];
params[0] = 0.1;
MaxEnt maxEnt = new MaxEnt(pipe, params);
StringWriter writer = new StringWriter();
PrintWriter pw = new PrintWriter(writer);
maxEnt.printExtremeFeatures(pw, 10);
pw.flush();
String result = writer.toString();
assertTrue(result.contains("FEATURES FOR CLASS"));
assertTrue(result.contains("<default>"));
}

@Test
public void testSerializationWithInterleavedPerClassFeatureSelection() throws Exception {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("f0");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("c0");
labelAlphabet.lookupIndex("c1");
labelAlphabet.lookupIndex("c2");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
int paramSize = (dataAlphabet.size() + 1) * labelAlphabet.size();
double[] params = new double[paramSize];
FeatureSelection[] selections = new FeatureSelection[3];
selections[0] = mock(FeatureSelection.class);
selections[1] = null;
selections[2] = mock(FeatureSelection.class);
MaxEnt maxEnt = new MaxEnt(pipe, params, null, selections);
ByteArrayOutputStream baos = new ByteArrayOutputStream();
ObjectOutputStream oos = new ObjectOutputStream(baos);
oos.writeObject(maxEnt);
oos.close();
ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
ObjectInputStream ois = new ObjectInputStream(bais);
MaxEnt deserialized = (MaxEnt) ois.readObject();
assertNotNull(deserialized.getPerClassFeatureSelection());
assertEquals(3, deserialized.getPerClassFeatureSelection().length);
}

@Test
public void testPrintRankWithEqualWeights() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("a");
dataAlphabet.lookupIndex("b");
dataAlphabet.lookupIndex("c");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("L");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
double[] parameters = new double[dataAlphabet.size() + 1];
parameters[0] = 0.5;
parameters[1] = 0.5;
parameters[2] = 0.5;
MaxEnt maxEnt = new MaxEnt(pipe, parameters);
StringWriter writer = new StringWriter();
PrintWriter out = new PrintWriter(writer);
maxEnt.printRank(out);
out.flush();
String output = writer.toString();
assertTrue(output.contains("FEATURES FOR CLASS"));
assertTrue(output.contains("<default>"));
}

@Test
public void testGetUnnormalizedClassificationScoresWithActiveFeatureVector() {
Alphabet dataAlphabet = new Alphabet();
int index0 = dataAlphabet.lookupIndex("f0");
int index1 = dataAlphabet.lookupIndex("f1");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("class0");
labelAlphabet.lookupIndex("class1");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
int numLabels = labelAlphabet.size();
int featureSize = dataAlphabet.size();
double[] parameters = new double[(featureSize + 1) * numLabels];
parameters[0] = 2.0;
parameters[1] = 3.0;
parameters[(featureSize + 1) + 0] = 1.0;
parameters[(featureSize + 1) + 1] = 1.0;
MaxEnt maxEnt = new MaxEnt(pipe, parameters);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { index0, index1 }, new double[] { 1.0, 1.0 });
Instance instance = new Instance(fv, null, null, null);
double[] scores = new double[numLabels];
maxEnt.getUnnormalizedClassificationScores(instance, scores);
assertTrue(scores[1] > scores[0]);
}

@Test
public void testClassificationScoresWithMaximumDoubleProduces1ProbCorrectly() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("f");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("hi");
labelAlphabet.lookupIndex("lo");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
int featureSize = dataAlphabet.size();
int numLabels = labelAlphabet.size();
double[] parameters = new double[(featureSize + 1) * numLabels];
parameters[featureSize] = Double.MAX_VALUE;
parameters[(featureSize + 1) + featureSize] = 0.0;
MaxEnt maxEnt = new MaxEnt(pipe, parameters);
FeatureVector fv = mock(FeatureVector.class);
when(fv.getAlphabet()).thenReturn(dataAlphabet);
Instance instance = mock(Instance.class);
when(instance.getData()).thenReturn(fv);
double[] scores = new double[numLabels];
maxEnt.getClassificationScores(instance, scores);
assertEquals(1.0, scores[0], 1e-10);
assertEquals(0.0, scores[1], 1e-10);
}

@Test
public void testSetFeatureSelectionOverridesPerClassFeatureSelection() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("xx");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("yy");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
double[] param = new double[(dataAlphabet.size() + 1) * labelAlphabet.size()];
FeatureSelection fs = mock(FeatureSelection.class);
FeatureSelection[] fss = new FeatureSelection[] { mock(FeatureSelection.class) };
MaxEnt maxEnt = new MaxEnt(pipe, param, null, fss);
assertNotNull(maxEnt.getPerClassFeatureSelection());
maxEnt.setFeatureSelection(fs);
assertSame(fs, maxEnt.getFeatureSelection());
assertNull(maxEnt.getPerClassFeatureSelection());
}

@Test
public void testDefaultFeatureIndexAssignedProperlyInConstructor() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("f0");
dataAlphabet.lookupIndex("f1");
dataAlphabet.lookupIndex("f2");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("L");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
MaxEnt maxEnt = new MaxEnt(pipe, new double[(dataAlphabet.size() + 1) * labelAlphabet.size()]);
assertEquals(dataAlphabet.size(), maxEnt.getDefaultFeatureIndex());
}

@Test
public void testParametersPreservedAfterSerialization() throws Exception {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("aa");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("bb");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
double[] parameters = new double[(dataAlphabet.size() + 1) * labelAlphabet.size()];
parameters[0] = 0.123;
MaxEnt maxEnt = new MaxEnt(pipe, parameters);
ByteArrayOutputStream baos = new ByteArrayOutputStream();
ObjectOutputStream out = new ObjectOutputStream(baos);
out.writeObject(maxEnt);
out.close();
ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
ObjectInputStream in = new ObjectInputStream(bais);
MaxEnt loaded = (MaxEnt) in.readObject();
double[] restoredParams = loaded.getParameters();
assertEquals(0.123, restoredParams[0], 0.00001);
}

@Test
public void testClassificationWithZeroFeatureVectorAndZeroParamsOutputsUniformProbabilities() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("f1");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("A");
labelAlphabet.lookupIndex("B");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
double[] params = new double[(dataAlphabet.size() + 1) * labelAlphabet.size()];
MaxEnt maxEnt = new MaxEnt(pipe, params);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[0], new double[0]);
Instance instance = new Instance(fv, null, null, null);
double[] scores = new double[labelAlphabet.size()];
maxEnt.getClassificationScores(instance, scores);
assertEquals(0.5, scores[0], 1e-6);
assertEquals(0.5, scores[1], 1e-6);
}

@Test
public void testClassificationWithSingleLabelAlwaysReturnsProbability1() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("f1");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("Only");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
double[] params = new double[(dataAlphabet.size() + 1)];
MaxEnt maxEnt = new MaxEnt(pipe, params);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
Instance inst = new Instance(fv, null, null, null);
Classification result = maxEnt.classify(inst);
assertEquals(1.0, result.getLabelVector().value(0), 1e-6);
}

@Test
public void testSetAndOverwriteDefaultFeatureIndex() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("x1");
dataAlphabet.lookupIndex("x2");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("Y");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
MaxEnt maxEnt = new MaxEnt(pipe, new double[(dataAlphabet.size() + 1)]);
int original = maxEnt.getDefaultFeatureIndex();
maxEnt.setDefaultFeatureIndex(42);
assertEquals(42, maxEnt.getDefaultFeatureIndex());
assertNotEquals(original, maxEnt.getDefaultFeatureIndex());
}

@Test
public void testGetDefaultFeatureIndexAffectsBiasScore() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("f0");
dataAlphabet.lookupIndex("f1");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("Label1");
labelAlphabet.lookupIndex("Label2");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
double[] params = new double[(dataAlphabet.size() + 1) * labelAlphabet.size()];
params[dataAlphabet.size()] = 1.5;
params[(dataAlphabet.size() + 1) + dataAlphabet.size()] = -1.5;
MaxEnt maxEnt = new MaxEnt(pipe, params);
maxEnt.setDefaultFeatureIndex(dataAlphabet.size());
FeatureVector fv = new FeatureVector(dataAlphabet, new int[0], new double[0]);
Instance inst = new Instance(fv, null, null, null);
double[] scores = new double[labelAlphabet.size()];
maxEnt.getUnnormalizedClassificationScores(inst, scores);
assertEquals(1.5, scores[0], 0.0001);
assertEquals(-1.5, scores[1], 0.0001);
}

@Test
public void testClassificationWithNaNAsLogitDoesNotThrow() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("f");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("A");
labelAlphabet.lookupIndex("B");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
int fsize = dataAlphabet.size();
int lsize = labelAlphabet.size();
double[] params = new double[(fsize + 1) * lsize];
params[fsize] = Double.NaN;
params[(fsize + 1) + fsize] = 0.0;
MaxEnt maxEnt = new MaxEnt(pipe, params);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[0], new double[0]);
Instance instance = new Instance(fv, null, null, null);
double[] scores = new double[lsize];
maxEnt.getClassificationScores(instance, scores);
assertFalse(Double.isNaN(scores[0]));
assertFalse(Double.isNaN(scores[1]));
double sum = scores[0] + scores[1];
assertEquals(1.0, sum, 1e-5);
}

@Test
public void testPrintExtremeFeaturesWhenKIsZero() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("f1");
dataAlphabet.lookupIndex("f2");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("positive");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
MaxEnt maxEnt = new MaxEnt(pipe, new double[dataAlphabet.size() + 1]);
StringWriter sw = new StringWriter();
PrintWriter out = new PrintWriter(sw);
maxEnt.printExtremeFeatures(out, 0);
out.flush();
String output = sw.toString();
assertTrue(output.contains("<default>"));
}

@Test
public void testNullFeatureAlphabetCausesAssertionErrorOnClassification() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("existing");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("label");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
double[] params = new double[(dataAlphabet.size() + 1)];
MaxEnt maxEnt = new MaxEnt(pipe, params);
Alphabet foreignAlphabet = new Alphabet();
foreignAlphabet.lookupIndex("bad");
int[] indices = new int[] { 0 };
double[] values = new double[] { 1.0 };
FeatureVector conflictingFV = new FeatureVector(foreignAlphabet, indices, values);
Instance inst = new Instance(conflictingFV, null, null, null);
boolean caught = false;
try {
maxEnt.classify(inst);
} catch (AssertionError e) {
caught = true;
}
assertTrue(caught);
}

@Test
public void testSetPerClassFeatureSelectionClearsSharedFeatureSelection() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("f");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("c0");
labelAlphabet.lookupIndex("c1");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
FeatureSelection[] classSelectors = new FeatureSelection[2];
classSelectors[0] = mock(FeatureSelection.class);
classSelectors[1] = null;
MaxEnt maxEnt = new MaxEnt(pipe, new double[(dataAlphabet.size() + 1) * labelAlphabet.size()]);
FeatureSelection sharedFS = mock(FeatureSelection.class);
maxEnt.setFeatureSelection(sharedFS);
assertNotNull(maxEnt.getFeatureSelection());
maxEnt.setPerClassFeatureSelection(classSelectors);
assertNull(maxEnt.getFeatureSelection());
assertNotNull(maxEnt.getPerClassFeatureSelection());
assertEquals(2, maxEnt.getPerClassFeatureSelection().length);
}

@Test
public void testGetClassificationScoresWithTemperatureOneReturnsSameAsStandard() {
Alphabet dataAlphabet = new Alphabet();
dataAlphabet.lookupIndex("foo");
LabelAlphabet labelAlphabet = new LabelAlphabet();
labelAlphabet.lookupIndex("c0");
labelAlphabet.lookupIndex("c1");
Pipe pipe = mock(Pipe.class);
when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);
double[] params = new double[(dataAlphabet.size() + 1) * labelAlphabet.size()];
params[dataAlphabet.size()] = 2.0;
params[(dataAlphabet.size() + 1) + dataAlphabet.size()] = 1.0;
MaxEnt maxEnt = new MaxEnt(pipe, params);
FeatureVector fv = new FeatureVector(dataAlphabet, new int[0], new double[0]);
Instance inst = new Instance(fv, null, null, null);
double[] baseline = new double[2];
double[] tempScores = new double[2];
maxEnt.getClassificationScores(inst, baseline);
maxEnt.getClassificationScoresWithTemperature(inst, 1.0, tempScores);
assertArrayEquals(baseline, tempScores, 1e-6);
}
}
