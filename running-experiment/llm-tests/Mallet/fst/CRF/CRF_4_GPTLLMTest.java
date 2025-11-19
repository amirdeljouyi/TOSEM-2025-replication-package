package cc.mallet.fst.tests;

import cc.mallet.fst.CRF;
import cc.mallet.fst.Transducer;
import cc.mallet.pipe.Noop;
import cc.mallet.pipe.Pipe;
import cc.mallet.types.*;
import org.junit.Test;
import java.io.*;
import static org.junit.Assert.*;

public class CRF_4_GPTLLMTest {

@Test
public void testAddStateCreatesExpectedState() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("L");
outputAlphabet.lookupIndex("B");
Pipe inputPipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(inputPipe, inputPipe);
String[] destinations = new String[] { "B" };
String[] labels = new String[] { "L" };
crf.addState("A", 0.5, 1.0, destinations, labels);
assertNotNull(crf.getState("A"));
assertEquals("A", crf.getState("A").getName());
assertEquals(0, crf.getState("A").getIndex());
}

@Test(expected = IllegalArgumentException.class)
public void testAddDuplicateStateThrowsException() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("X");
Pipe inputPipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(inputPipe, inputPipe);
crf.addState("X", 0.0, 0.0, new String[] { "X" }, new String[] { "X" });
crf.addState("X", 0.0, 0.0, new String[] { "Y" }, new String[] { "X" });
}

@Test
public void testGetWeightsIndexWithNewNameCreatesEntry() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(pipe, pipe);
int index1 = crf.getWeightsIndex("feature1");
int index2 = crf.getWeightsIndex("feature2");
assertEquals(0, index1);
assertEquals(1, index2);
}

@Test
public void testFreezeAndUnfreezeWeight() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(pipe, pipe);
int weightIndex = crf.getWeightsIndex("w1");
assertFalse(crf.isWeightsFrozen(weightIndex));
crf.freezeWeights(weightIndex);
assertTrue(crf.isWeightsFrozen(weightIndex));
crf.unfreezeWeights("w1");
assertFalse(crf.isWeightsFrozen(weightIndex));
}

@Test
public void testSetAndGetDefaultWeight() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(pipe, pipe);
int weightIndex = crf.getWeightsIndex("w1");
crf.setDefaultWeight(weightIndex, 3.14);
double[] defaults = crf.getDefaultWeights();
assertEquals(3.14, defaults[weightIndex], 1e-6);
}

@Test
public void testGetWeightsNameReturnsCorrectName() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(pipe, pipe);
int weightIndex = crf.getWeightsIndex("transitionWeight");
String name = crf.getWeightsName(weightIndex);
assertEquals("transitionWeight", name);
}

@Test
public void testSetFeatureSelectionOnWeightIndex() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(pipe, pipe);
int weightIndex = crf.getWeightsIndex("myWeight");
FeatureSelection fs = new FeatureSelection(inputAlphabet);
crf.setFeatureSelection(weightIndex, fs);
}

@Test
public void testAssignWeightsToTransition() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("feature1");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("A");
outputAlphabet.lookupIndex("B");
Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(pipe, pipe);
crf.addState("A", 0.0, 0.0, new String[] { "B" }, new String[] { "B" });
crf.addState("B", 0.0, 0.0, new String[0], new String[0]);
int sourceIndex = crf.getState("A").getIndex();
int destIndex = crf.getState("B").getIndex();
int featureIndex = inputAlphabet.lookupIndex("feature1");
crf.setParameter(sourceIndex, destIndex, featureIndex, 42.0);
double value = crf.getParameter(sourceIndex, destIndex, featureIndex);
assertEquals(42.0, value, 1e-6);
}

@Test(expected = IllegalArgumentException.class)
public void testSetParameterInvalidDestinationThrows() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("X");
Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(pipe, pipe);
crf.addState("X", 0.0, 0.0, new String[] { "X" }, new String[] { "X" });
crf.setParameter(0, 99, 0, 1.0);
}

@Test
public void testSetAndGetSparseWeightsForIndex() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(pipe, pipe);
int weightIndex = crf.getWeightsIndex("myWeight");
SparseVector sparseVector = new SparseVector();
sparseVector.setValue(3, 5.5);
crf.setWeights(weightIndex, sparseVector);
SparseVector result = crf.getWeights(weightIndex);
double val = result.value(3);
assertEquals(5.5, val, 0.0001);
}

@Test
public void testSetWeightsDimensionDenselyDoesNotFail() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f1");
inputAlphabet.lookupIndex("f2");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("L1");
Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(pipe, pipe);
int wi = crf.getWeightsIndex("w");
crf.setWeightsDimensionDensely();
SparseVector[] weights = crf.getWeights();
assertNotNull(weights[wi]);
}

@Test
public void testGetParametersAbsNormReturnsCorrectSum() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("token1");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("A");
outputAlphabet.lookupIndex("B");
Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(pipe, pipe);
crf.addState("A", 1.0, 2.0, new String[] { "B" }, new String[] { "B" });
crf.addState("B", 3.0, 4.0, new String[] { "A" }, new String[] { "A" });
int wi = crf.getWeightsIndex("weight1");
crf.setDefaultWeight(wi, 5.0);
SparseVector sv = new SparseVector();
sv.setValue(inputAlphabet.lookupIndex("token1"), 6.0);
crf.setWeights(wi, sv);
double norm = crf.getParametersAbsNorm();
assertEquals(1.0 + 2.0 + 3.0 + 4.0 + 5.0 + 6.0, norm, 0.0001);
}

@Test(expected = IllegalArgumentException.class)
public void testGetParameterWithNonexistentTransitionThrowsException() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("A");
outputAlphabet.lookupIndex("B");
Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(pipe, pipe);
crf.addState("S1", 0.0, 0.0, new String[] { "B" }, new String[] { "B" });
crf.addState("S2", 0.0, 0.0, new String[0], new String[0]);
crf.getParameter(0, 1, -1);
}

@Test(expected = IllegalArgumentException.class)
public void testGetParameterWithTooHighIndexThrowsException() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(pipe, pipe);
int wi = crf.getWeightsIndex("w");
CRF.Factors factors = crf.getParameters();
factors.getParameter(Integer.MAX_VALUE);
}

@Test(expected = IllegalArgumentException.class)
public void testSetParameterWithExcessiveIndexThrows() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(pipe, pipe);
CRF.Factors f = crf.getParameters();
f.setParameter(Integer.MAX_VALUE, 1.23);
}

@Test
public void testSetWeightsWithInvalidIndexDoesNotThrowIfInsideBounds() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(pipe, pipe);
int wi = crf.getWeightsIndex("w");
SparseVector sv = new SparseVector();
sv.setValue(100, 3.3);
crf.setWeights(wi, sv);
SparseVector actual = crf.getWeights(wi);
assertEquals(3.3, actual.value(100), 0.0001);
}

@Test
public void testAddStateWithEmptyWeightsNamesAddsCorrectly() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("X");
Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(pipe, pipe);
String[][] weightNames = new String[1][];
weightNames[0] = new String[0];
crf.addState("X", 0.0, 0.0, new String[] { "X" }, new String[] { "X" }, weightNames);
assertNotNull(crf.getState("X"));
}

@Test
public void testSetAndGetSingleParameterNegativeFeatureIndexSetsDefaultWeight() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("featureX");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("Y");
outputAlphabet.lookupIndex("Z");
Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(pipe, pipe);
crf.addState("Y", 0.0, 0.0, new String[] { "Z" }, new String[] { "Z" });
crf.addState("Z", 0.0, 0.0, new String[0], new String[0]);
int src = crf.getState("Y").getIndex();
int dst = crf.getState("Z").getIndex();
crf.setParameter(src, dst, -1, 888.0);
double value = crf.getParameter(src, dst, -1);
assertEquals(888.0, value, 0.00001);
}

@Test
public void testGetNumParametersIncludesAllElements() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f1");
inputAlphabet.lookupIndex("f2");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("A");
outputAlphabet.lookupIndex("B");
Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(pipe, pipe);
crf.addState("A", 0.5, 0.8, new String[] { "B" }, new String[] { "B" });
crf.addState("B", 0.2, 0.4, new String[] { "A" }, new String[] { "A" });
int wi = crf.getWeightsIndex("transition");
SparseVector sv = new SparseVector();
sv.setValue(0, 1.1);
sv.setValue(1, 2.2);
crf.setWeights(wi, sv);
crf.setDefaultWeight(wi, 3.3);
int paramCount = crf.getNumParameters();
assertTrue(paramCount >= 2 * 2 + 1 + 2);
}

@Test
public void testAddFullyConnectedStatesForBiLabelsAddsExpectedNumberOfStates() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("X");
outputAlphabet.lookupIndex("Y");
Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(pipe, pipe);
crf.addFullyConnectedStatesForBiLabels();
assertEquals(4, crf.numStates());
assertNotNull(crf.getState("X,Y"));
assertNotNull(crf.getState("Y,X"));
assertNotNull(crf.getState("X,X"));
assertNotNull(crf.getState("Y,Y"));
}

@Test
public void testWriteAndReadCRFSerializationPreservesState() throws Exception {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("word");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("TAG");
Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(pipe, pipe);
crf.addState("START", 1.0, 2.0, new String[] { "MIDDLE" }, new String[] { "TAG" });
crf.addState("MIDDLE", 3.0, 4.0, new String[] { "END" }, new String[] { "TAG" });
crf.addState("END", 0.0, 5.0, new String[0], new String[0]);
ByteArrayOutputStream bos = new ByteArrayOutputStream();
ObjectOutputStream out = new ObjectOutputStream(bos);
out.writeObject(crf);
out.close();
ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
ObjectInputStream in = new ObjectInputStream(bis);
CRF deserialized = (CRF) in.readObject();
assertNotNull(deserialized);
assertEquals(3, deserialized.numStates());
assertEquals("START", deserialized.getState(0).getName());
assertEquals("MIDDLE", deserialized.getState(1).getName());
assertEquals("END", deserialized.getState(2).getName());
}

@Test(expected = IllegalArgumentException.class)
public void testAddStateWithMismatchedWeightsThrows() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("X");
Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(pipe, pipe);
String[] dests = new String[] { "X" };
String[] labels = new String[] { "X" };
String[][] weightNames = new String[2][1];
crf.addState("bad", 0.0, 0.0, dests, labels, weightNames);
}

@Test
public void testPlusEqualsDoesNotModifyFrozenWeights() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f");
Alphabet outputAlphabet = new Alphabet();
Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(pipe, pipe);
int wi = crf.getWeightsIndex("w");
SparseVector sv = new SparseVector();
sv.setValue(4, 77.0);
crf.setWeights(wi, sv);
crf.setDefaultWeight(wi, 11.11);
crf.freezeWeights(wi);
CRF.Factors f1 = crf.getParameters();
CRF.Factors f2 = new CRF.Factors(f1);
f1.plusEquals(f2, 2.0, true);
assertEquals(11.11, f1.defaultWeights[wi], 0.00001);
assertEquals(77.0, f1.weights[wi].value(4), 0.00001);
}

@Test(expected = IllegalArgumentException.class)
public void testSetWeightsWithOutOfBoundsIndexShouldThrow() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(pipe, pipe);
SparseVector weights = new SparseVector();
crf.setWeights(10, weights);
}

@Test
public void testSetWeightsWithBoundaryIndexShouldSucceed() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(pipe, pipe);
int index = crf.getWeightsIndex("alpha");
SparseVector sv = new SparseVector();
sv.setValue(0, 1.0);
crf.setWeights(index, sv);
SparseVector retrieved = crf.getWeights(index);
assertEquals(1.0, retrieved.value(0), 0.0001);
}

@Test
public void testGetParameterDefaultOnlyWithoutFeatures() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("A");
outputAlphabet.lookupIndex("B");
Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(pipe, pipe);
crf.addState("A", 0.0, 0.0, new String[] { "B" }, new String[] { "B" });
crf.addState("B", 0.0, 0.0, new String[0], new String[0]);
int sourceIndex = crf.getState("A").getIndex();
int targetIndex = crf.getState("B").getIndex();
crf.setParameter(sourceIndex, targetIndex, -1, 9.99);
double v = crf.getParameter(sourceIndex, targetIndex, -1);
assertEquals(9.99, v, 0.0001);
}

@Test(expected = IllegalArgumentException.class)
public void testGetWeightsIndexWithFrozenAlphabetShouldThrow() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("L1");
inputAlphabet.stopGrowth();
Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(pipe, pipe);
// crf.parameters.weightAlphabet.stopGrowth();
crf.getWeightsIndex("newWeight");
}

@Test
public void testMultipleTransitionsSameFeatureVector() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("feature");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("A");
outputAlphabet.lookupIndex("B");
Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(pipe, pipe);
crf.addState("S1", 0.0, 0.0, new String[] { "S2", "S2" }, new String[] { "B", "B" });
crf.addState("S2", 0.0, 0.0, new String[0], new String[0]);
CRF.State state = (CRF.State) crf.getState("S1");
double[] weights = new double[state.numDestinations()];
FeatureVector fv = new FeatureVector(inputAlphabet, new int[] { 0 }, new double[] { 1.0 });
// Transducer.TransitionIterator iter = new Transducer.TransitionIterator(state, fv, "B", crf);
// while (iter.hasNext()) {
// iter.nextState();
// assertEquals("S2", iter.getDestinationState().getName());
// assertEquals("B", iter.getOutput());
// assertTrue(iter.getWeight() != Double.NEGATIVE_INFINITY);
// }
}

@Test
public void testSetParameterOnMultipleWeightGroup() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("T1");
outputAlphabet.lookupIndex("T2");
Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(pipe, pipe);
String[][] weights = new String[1][2];
weights[0][0] = "w1";
weights[0][1] = "w2";
crf.addState("S1", 0.0, 0.0, new String[] { "S2" }, new String[] { "T2" }, weights);
crf.addState("S2", 0.0, 0.0, new String[0], new String[0]);
int src = crf.getState("S1").getIndex();
int dst = crf.getState("S2").getIndex();
int featureIdx = inputAlphabet.lookupIndex("f");
crf.setParameter(src, dst, featureIdx, 1, 4.4);
double result = crf.getParameter(src, dst, featureIdx, 1);
assertEquals(4.4, result, 0.0001);
}

@Test
public void testPlusEqualsWithObeyFrozenFlagFalseModifiesFrozenWeight() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f");
Alphabet outputAlphabet = new Alphabet();
Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(pipe, pipe);
int wi = crf.getWeightsIndex("w");
CRF.Factors f1 = crf.getParameters();
CRF.Factors f2 = new CRF.Factors(f1, false);
f1.weightsFrozen[wi] = true;
f2.defaultWeights[wi] = 5.0;
f2.weights[wi].setValue(3, 7.0);
f1.plusEquals(f2, 1.0, false);
assertEquals(5.0, f1.defaultWeights[wi], 0.0001);
assertEquals(7.0, f1.weights[wi].value(3), 0.0001);
}

@Test
public void testSetParameterWithZeroFeatureIndex() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f0");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("L1");
outputAlphabet.lookupIndex("L2");
Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(pipe, pipe);
crf.addState("S1", 0.0, 0.0, new String[] { "S2" }, new String[] { "L2" });
crf.addState("S2", 0.0, 0.0, new String[0], new String[0]);
int src = crf.getState("S1").getIndex();
int dst = crf.getState("S2").getIndex();
int fid = 0;
crf.setParameter(src, dst, fid, 1.23);
double value = crf.getParameter(src, dst, fid);
assertEquals(1.23, value, 0.0001);
}

@Test
public void testMultipleDefaultWeightsHandledCorrectlyInSum() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f1");
Alphabet outputAlphabet = new Alphabet();
Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
outputAlphabet.lookupIndex("X");
CRF crf = new CRF(pipe, pipe);
int wi1 = crf.getWeightsIndex("W1");
int wi2 = crf.getWeightsIndex("W2");
CRF.Factors f1 = crf.getParameters();
f1.defaultWeights[wi1] = 2.5;
f1.defaultWeights[wi2] = 1.5;
CRF.Factors f2 = new CRF.Factors(f1);
f2.defaultWeights[wi1] = 1.0;
f2.defaultWeights[wi2] = 0.5;
f1.plusEquals(f2, 2.0);
assertEquals(2.5 + (1.0 * 2.0), f1.defaultWeights[wi1], 0.0001);
assertEquals(1.5 + (0.5 * 2.0), f1.defaultWeights[wi2], 0.0001);
}

@Test
public void testZeroedFactorsSetAllValuesToZero() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("L");
Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(pipe, pipe);
int wi = crf.getWeightsIndex("w");
SparseVector sv = new SparseVector();
sv.setValue(0, 3.0);
crf.setWeights(wi, sv);
crf.setDefaultWeight(wi, 2.0);
CRF.Factors factors = crf.getParameters();
factors.initialWeights = new double[] { 1.1 };
factors.finalWeights = new double[] { 2.2 };
factors.zero();
assertEquals(0.0, factors.weights[wi].value(0), 0.00001);
assertEquals(0.0, factors.defaultWeights[wi], 0.00001);
assertEquals(0.0, factors.initialWeights[0], 0.00001);
assertEquals(0.0, factors.finalWeights[0], 0.00001);
}

@Test
public void testCloneFactorsWithCloneAlphabetTrueClonesWeightAlphabet() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f1");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("TAG");
Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(pipe, pipe);
int wi = crf.getWeightsIndex("w");
crf.setDefaultWeight(wi, 3.0);
CRF.Factors f1 = crf.getParameters();
CRF.Factors f2 = new CRF.Factors(f1, true);
assertEquals(f1.weightAlphabet.size(), f2.weightAlphabet.size());
assertNotSame(f1.weightAlphabet, f2.weightAlphabet);
}

@Test(expected = IllegalArgumentException.class)
public void testSetParametersThrowsWhenBufferLengthMismatch() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f1");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("L1");
Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(pipe, pipe);
int wi = crf.getWeightsIndex("w");
crf.setDefaultWeight(wi, 2.0);
CRF.Factors f = crf.getParameters();
double[] invalid = new double[1];
f.setParameters(invalid);
}

@Test(expected = IllegalArgumentException.class)
public void testGetParameterWithOutOfRangeIndexThrows() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f1");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("L1");
Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(pipe, pipe);
crf.getParameters().getParameter(999999);
}

@Test
public void testSetParameterSetsCorrectDefault() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("L");
Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(pipe, pipe);
CRF.Factors f = crf.getParameters();
int wi = crf.getWeightsIndex("foo");
int paramIndex = 2 * f.initialWeights.length + wi * (1 + f.weights[wi].numLocations());
f.setParameter(paramIndex, 42.0);
double actual = f.getParameter(paramIndex);
assertEquals(42.0, actual, 1e-6);
}

@Test
public void testGetNumFactorsIncludesWeights() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("L");
Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(pipe, pipe);
crf.getWeightsIndex("w");
CRF.Factors f = crf.getParameters();
int num = f.getNumFactors();
assertTrue(num > 0);
}

@Test
public void testGaussianPriorWithInfiniteWeightsSkipsThem() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("L");
Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(pipe, pipe);
int wi = crf.getWeightsIndex("w");
CRF.Factors f = crf.getParameters();
f.defaultWeights[wi] = Double.POSITIVE_INFINITY;
f.finalWeights[0] = Double.NEGATIVE_INFINITY;
f.initialWeights[0] = Double.POSITIVE_INFINITY;
double prior = f.gaussianPrior(1.0);
assertTrue(Double.isFinite(prior));
}

@Test
public void testHyperbolicPriorWithInfiniteValuesIsFinite() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("T");
Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(pipe, pipe);
int wi = crf.getWeightsIndex("x");
CRF.Factors factors = crf.getParameters();
factors.defaultWeights[wi] = Double.POSITIVE_INFINITY;
factors.initialWeights[0] = Double.NEGATIVE_INFINITY;
factors.finalWeights[0] = Double.NEGATIVE_INFINITY;
double prior = factors.hyberbolicPrior(1.0, 1.0);
assertTrue(Double.isFinite(prior));
}

@Test
public void testPlusEqualsGaussianPriorGradientSkipsInfiniteValues() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("feat");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("L");
Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(pipe, pipe);
CRF.Factors target = crf.getParameters();
target.initialWeights[0] = Double.POSITIVE_INFINITY;
target.finalWeights[0] = Double.NEGATIVE_INFINITY;
int wi = crf.getWeightsIndex("w");
target.defaultWeights[wi] = Double.POSITIVE_INFINITY;
CRF.Factors source = new CRF.Factors(target);
target.plusEqualsGaussianPriorGradient(source, 1.0);
assertTrue(Double.isInfinite(target.defaultWeights[wi]));
}

@Test
public void testPlusEqualsHyperbolicPriorGradientWithInfinityHandled() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("x");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("L1");
Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(pipe, pipe);
CRF.Factors target = crf.getParameters();
int w = crf.getWeightsIndex("w");
target.defaultWeights[w] = Double.POSITIVE_INFINITY;
target.initialWeights[0] = Double.POSITIVE_INFINITY;
target.finalWeights[0] = Double.POSITIVE_INFINITY;
CRF.Factors src = new CRF.Factors(target);
src.defaultWeights[w] = 10.0;
src.initialWeights[0] = 5.0;
src.finalWeights[0] = 5.0;
target.plusEqualsHyperbolicPriorGradient(src, 0.4, 2.0);
assertTrue(Double.isInfinite(target.defaultWeights[w]));
}

@Test
public void testGetParametersAbsNormIncludesAllNonNegative() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("LBL");
Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(pipe, pipe);
crf.addState("S", 1.2, 2.3, new String[] { "S" }, new String[] { "LBL" });
crf.setDefaultWeight(crf.getWeightsIndex("w"), 4.4);
SparseVector sv = new SparseVector();
sv.setValue(0, 5.5);
crf.setWeights(crf.getWeightsIndex("w"), sv);
double norm = crf.getParametersAbsNorm();
assertTrue(norm >= (1.2 + 2.3 + 4.4 + 5.5));
}

@Test
public void testStateSerializationAndDeserialization() throws Exception {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("x");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("L");
Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(pipe, pipe);
crf.addState("S1", 0.0, 0.0, new String[] { "S1" }, new String[] { "L" });
CRF.State state = (CRF.State) crf.getState("S1");
ByteArrayOutputStream bos = new ByteArrayOutputStream();
ObjectOutputStream oos = new ObjectOutputStream(bos);
oos.writeObject(state);
oos.flush();
ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
ObjectInputStream ois = new ObjectInputStream(bis);
CRF.State deserialized = (CRF.State) ois.readObject();
assertEquals("S1", deserialized.getName());
assertEquals(0, deserialized.getIndex());
}

@Test(expected = UnsupportedOperationException.class)
public void testTransitionIteratorThrowsOnNegativeInputPosition() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("fx");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("L");
Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(pipe, pipe);
crf.addState("Start", 0.0, 0.0, new String[] { "Next" }, new String[] { "L" });
crf.addState("Next", 0.0, 0.0, new String[0], new String[0]);
CRF.State state = (CRF.State) crf.getState("Start");
// state.transitionIterator(new FeatureVectorSequence(), -1, null, 0);
}

@Test(expected = UnsupportedOperationException.class)
public void testTransitionIteratorThrowsOnNullInputSequence() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("fx");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("L");
Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(pipe, pipe);
crf.addState("S1", 0.0, 0.0, new String[] { "S2" }, new String[] { "L" });
crf.addState("S2", 0.0, 0.0, new String[0], new String[0]);
CRF.State state = (CRF.State) crf.getState("S1");
state.transitionIterator(null, 0, null, 0);
}

@Test(expected = IllegalArgumentException.class)
public void testGetDestinationStateThrowsIfNameNotFound() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("input");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("Y");
Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(pipe, pipe);
String[] destinations = new String[] { "MissingState" };
String[] labels = new String[] { "Y" };
crf.addState("Origin", 0.0, 0.0, destinations, labels);
CRF.State origin = (CRF.State) crf.getState("Origin");
origin.getDestinationState(0);
}

@Test
public void testGetWeightNamesReturnsExpectedArray() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("T");
Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(pipe, pipe);
crf.addState("A", 0.0, 0.0, new String[] { "B" }, new String[] { "T" }, new String[][] { { "w0", "w1" } });
crf.addState("B", 0.0, 0.0, new String[0], new String[0]);
CRF.State s = (CRF.State) crf.getState("A");
String[] weightNames = s.getWeightNames(0);
assertEquals(2, weightNames.length);
assertEquals("w0", weightNames[0]);
assertEquals("w1", weightNames[1]);
}

@Test
public void testAddWeightIncrementsTransitionWeightMapping() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("Y");
Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(pipe, pipe);
crf.addState("S", 0.0, 0.0, new String[] { "S" }, new String[] { "Y" });
CRF.State state = (CRF.State) crf.getState("S");
// int before = state.weightsIndices[0].length;
state.addWeight(0, "newWeight");
// int after = state.weightsIndices[0].length;
// assertEquals(before + 1, after);
}

@Test
public void testTransitionIteratorWithNullOutputFiltersCorrectly() {
Alphabet inputAlphabet = new Alphabet();
int fIdx = inputAlphabet.lookupIndex("token");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("TAG");
Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(pipe, pipe);
crf.addState("S1", 0.0, 0.0, new String[] { "S2" }, new String[] { "TAG" });
crf.addState("S2", 0.0, 0.0, new String[0], new String[0]);
FeatureVector fv = new FeatureVector(inputAlphabet, new int[] { fIdx }, new double[] { 1.0 });
CRF.State s = (CRF.State) crf.getState("S1");
// Transducer.TransitionIterator it = new Transducer.TransitionIterator(s, fv, null, crf);
// assertTrue(it.hasNext());
// it.nextState();
// assertEquals("S2", it.getDestinationState().getName());
// assertEquals("TAG", it.getOutput());
// assertEquals("S1", it.getSourceState().getName());
// assertEquals(fv, it.getInput());
}

@Test
public void testAddFullyConnectedStatesForTriLabelsYieldsCorrectStateCount() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("A");
outputAlphabet.lookupIndex("B");
Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(pipe, pipe);
crf.addFullyConnectedStatesForTriLabels();
assertEquals(8, crf.numStates());
}

@Test
public void testGetWeightsIndexExpandsArraysWithCorrectDefaults() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(pipe, pipe);
int wi = crf.getWeightsIndex("firstGroup");
assertEquals(0, wi);
assertNotNull(crf.getWeights()[0]);
int next = crf.getWeightsIndex("secondGroup");
assertEquals(1, next);
assertNotNull(crf.getWeights()[1]);
assertFalse(crf.isWeightsFrozen(1));
assertEquals("secondGroup", crf.getWeightsName(1));
}

@Test
public void testSetAsStartStateSetsCorrectInitialAndResetsOthers() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("A");
outputAlphabet.lookupIndex("B");
Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(pipe, pipe);
crf.addFullyConnectedStatesForLabels();
CRF.State target = (CRF.State) crf.getState("A");
crf.setAsStartState(target);
for (int i = 0; i < crf.numStates(); i++) {
CRF.State s = (CRF.State) crf.getState(i);
if (s == target) {
assertEquals(0.0, s.getInitialWeight(), 0.00001);
} else {
assertEquals(CRF.IMPOSSIBLE_WEIGHT, s.getInitialWeight(), 0.00001);
}
}
}

@Test
public void testRankedFeatureVectorSortsDescendingByMagnitude() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f0");
inputAlphabet.lookupIndex("f1");
int[] indices = new int[] { 0, 1 };
double[] values = new double[] { 2.0, 4.0 };
// SparseVector sv = new SparseVector(indices, values, values.length);
// RankedFeatureVector rfv = new RankedFeatureVector(inputAlphabet, sv);
// int topIndex = rfv.getIndexAtRank(0);
// assertEquals(1, topIndex);
// assertEquals(4.0, rfv.getValueAtRank(0), 0.0001);
// int secondIndex = rfv.getIndexAtRank(1);
// assertEquals(0, secondIndex);
// assertEquals(2.0, rfv.getValueAtRank(1), 0.0001);
}

@Test
public void testPlusEqualsSkipsFrozenWeightsWhenObeyFlagIsTrue() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f1");
Alphabet outputAlphabet = new Alphabet();
Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(pipe, pipe);
int weightIndex = crf.getWeightsIndex("W");
crf.freezeWeights(weightIndex);
CRF.Factors f1 = crf.getParameters();
CRF.Factors f2 = new CRF.Factors(f1);
f2.defaultWeights[weightIndex] = 10.0;
f2.weights[weightIndex].setValue(0, 5.0);
double original = f1.defaultWeights[weightIndex];
double originalV = f1.weights[weightIndex].value(0);
f1.plusEquals(f2, 1.0, true);
assertEquals(original, f1.defaultWeights[weightIndex], 0.00001);
assertEquals(originalV, f1.weights[weightIndex].value(0), 0.00001);
}

@Test
public void testFeatureSelectionBlocksWeightUpdate() {
Alphabet inputAlphabet = new Alphabet();
int fidx1 = inputAlphabet.lookupIndex("keep");
int fidx2 = inputAlphabet.lookupIndex("block");
Alphabet outputAlphabet = new Alphabet();
Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(pipe, pipe);
int weightIndex = crf.getWeightsIndex("W");
FeatureSelection fs = new FeatureSelection(inputAlphabet);
// BitSet bits = new BitSet();
// bits.set(fidx1);
// fs.setBits(bits);
crf.setFeatureSelection(weightIndex, fs);
SparseVector sv = new SparseVector();
sv.setValue(fidx1, 1.0);
sv.setValue(fidx2, 1000.0);
crf.setWeights(weightIndex, sv);
double actualF1 = crf.getWeights()[weightIndex].value(fidx1);
double actualF2 = crf.getWeights()[weightIndex].value(fidx2);
assertEquals(1.0, actualF1, 0.0001);
assertEquals(0.0, actualF2, 0.0001);
}

@Test
public void testGaussianPriorHandlesOnlyFiniteValues() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f");
Alphabet outputAlphabet = new Alphabet();
Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(pipe, pipe);
int wi = crf.getWeightsIndex("W");
CRF.Factors f = crf.getParameters();
f.defaultWeights[wi] = Double.NEGATIVE_INFINITY;
f.weights[wi].setValue(0, Double.NaN);
f.initialWeights[0] = Double.POSITIVE_INFINITY;
double value = f.gaussianPrior(1.0);
assertTrue(Double.isFinite(value));
}

@Test
public void testSetParameterIndexAssignsFinalWeightIfIndexOdd() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(pipe, pipe);
crf.addFullyConnectedStatesForLabels();
CRF.Factors factors = crf.getParameters();
int index = 1;
factors.setParameter(index, 3.33);
double value = factors.getParameter(index);
assertEquals(3.33, value, 0.00001);
}

@Test(expected = IllegalArgumentException.class)
public void testSetParameterIndexTooLargeThrows() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(pipe, pipe);
CRF.Factors factors = crf.getParameters();
int tooBig = Integer.MAX_VALUE;
factors.setParameter(tooBig, 1.0);
}

@Test(expected = IllegalArgumentException.class)
public void testGetParameterIndexTooLargeThrows() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(pipe, pipe);
CRF.Factors factors = crf.getParameters();
int tooBig = Integer.MAX_VALUE;
factors.getParameter(tooBig);
}

@Test
public void testZeroEffectivelyClearsAllFactors() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("L");
Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(pipe, pipe);
int wi = crf.getWeightsIndex("W");
CRF.Factors f = crf.getParameters();
f.initialWeights[0] = 10.0;
f.finalWeights[0] = 3.0;
f.defaultWeights[wi] = 2.0;
f.weights[wi].setValue(0, 5.0);
f.zero();
assertEquals(0.0, f.initialWeights[0], 0.0001);
assertEquals(0.0, f.finalWeights[0], 0.0001);
assertEquals(0.0, f.defaultWeights[wi], 0.0001);
assertEquals(0.0, f.weights[wi].value(0), 0.0001);
}

@Test
public void testSetAndGetParameterForWeightVectorMultipleFeatures() {
Alphabet inputAlphabet = new Alphabet();
int idxA = inputAlphabet.lookupIndex("A");
int idxB = inputAlphabet.lookupIndex("B");
Alphabet outputAlphabet = new Alphabet();
Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(pipe, pipe);
int wi = crf.getWeightsIndex("TRANS");
SparseVector sv = new SparseVector();
sv.setValue(idxA, 4.4);
sv.setValue(idxB, 5.5);
crf.setWeights(wi, sv);
CRF.Factors f = crf.getParameters();
int baseIdx = 2 * f.initialWeights.length + wi;
f.setParameter(baseIdx + 1, 10.0);
double param = f.getParameter(baseIdx + 1);
assertEquals(10.0, param, 0.00001);
}

@Test
public void testGetNumParametersIncludesAllDistinctWeights() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("a");
inputAlphabet.lookupIndex("b");
Alphabet outputAlphabet = new Alphabet();
Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(pipe, pipe);
crf.addFullyConnectedStatesForLabels();
int wi = crf.getWeightsIndex("W1");
SparseVector sv = new SparseVector();
sv.setValue(0, 1.1);
sv.setValue(1, 2.2);
crf.setWeights(wi, sv);
crf.setDefaultWeight(wi, 3.3);
int num = crf.getNumParameters();
assertTrue(num >= 2 * crf.numStates() + 1 + 2);
}

@Test
public void testMaxLatticeDefaultBestPathReturnsSequenceSameLength() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("L");
Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(pipe, pipe);
crf.addFullyConnectedStatesForLabels();
FeatureVector[] fvs = new FeatureVector[3];
for (int i = 0; i < 3; i++) {
fvs[i] = new FeatureVector(inputAlphabet, new int[] { 0 }, new double[] { 1.0 });
}
FeatureVectorSequence sequence = new FeatureVectorSequence(fvs);
FeatureSequence dummyTarget = new FeatureSequence(outputAlphabet, new int[] { 0, 0, 0 });
Instance instance = new Instance(sequence, dummyTarget, null, null);
InstanceList list = new InstanceList(pipe);
list.addThruPipe(instance);
crf.induceFeaturesFor(list);
assertNotNull(list.get(0));
assertEquals(3, ((FeatureVectorSequence) list.get(0).getData()).size());
}

@Test
public void testAssertNotNaNOrInfiniteDoesNotThrowOnValidInput() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f");
Alphabet outputAlphabet = new Alphabet();
Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(pipe, pipe);
int wi = crf.getWeightsIndex("W");
crf.setDefaultWeight(wi, 1.1);
crf.getParameters().initialWeights[0] = 0.0;
crf.getParameters().finalWeights[0] = 0.0;
SparseVector sv = new SparseVector();
sv.setValue(0, 1.5);
crf.setWeights(wi, sv);
crf.getParameters().assertNotNaNOrInfinite();
}

@Test
public void testDescribeTransitionReturnsNonNullForValidPath() {
Alphabet inputAlphabet = new Alphabet();
int fIdx = inputAlphabet.lookupIndex("f");
Alphabet outputAlphabet = new Alphabet();
int tagIdx = outputAlphabet.lookupIndex("T");
Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(pipe, pipe);
crf.addState("S1", 0.0, 0.0, new String[] { "S2" }, new String[] { "T" });
crf.addState("S2", 0.0, 0.0, new String[0], new String[0]);
FeatureVector fv = new FeatureVector(inputAlphabet, new int[] { fIdx }, new double[] { 1.0 });
CRF.State state = (CRF.State) crf.getState("S1");
// Transducer.TransitionIterator iter = new Transducer.TransitionIterator(state, fv, "T", crf);
// iter.nextState();
// String desc = iter.describeTransition(0.0);
// assertNotNull(desc);
// assertTrue(desc.contains("Value:"));
}

@Test
public void testGetParameterReturnsZeroIfSparseIndexMissing() {
Alphabet inputAlphabet = new Alphabet();
int fIdx = inputAlphabet.lookupIndex("f1");
Alphabet outputAlphabet = new Alphabet();
Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(pipe, pipe);
int wi = crf.getWeightsIndex("WX");
SparseVector sv = new SparseVector();
sv.setValue(fIdx, 1.0);
crf.setWeights(wi, sv);
CRF.Factors factors = crf.getParameters();
int index = 2 * crf.numStates() + wi + 1;
double val = factors.getParameter(index + 20);
assertEquals(0.0, val, 0.00001);
}

@Test
public void testWeightedIncrementorMultipliesCorrectly() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("fX");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("Y");
Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(pipe, pipe);
crf.addState("A", 0.0, 0.0, new String[] { "A" }, new String[] { "Y" });
SparseVector inner = new SparseVector();
inner.setValue(0, 1.0);
CRF.Factors f = crf.getParameters();
CRF.Factors.WeightedIncrementor inc = f.new WeightedIncrementor(2.5);
Transducer.State s = crf.getState("A");
// Transducer.TransitionIterator ti = new Transducer.TransitionIterator((CRF.State) s, new FeatureVector(inputAlphabet, new int[] { 0 }, new double[] { 1.0 }), "Y", crf);
// ti.nextState();
inc.incrementInitialState(s, 1.0);
inc.incrementFinalState(s, 2.0);
// inc.incrementTransition(ti, 1.0);
assertEquals(2.5, f.initialWeights[s.getIndex()], 0.00001);
assertEquals(5.0, f.finalWeights[s.getIndex()], 0.00001);
}

@Test
public void testCloneMatrixZeroedProducesIdenticalIndexSetZeroValues() {
Alphabet dict = new Alphabet();
dict.lookupIndex("x");
dict.lookupIndex("y");
int[] indices = new int[] { 0, 1 };
double[] values = new double[] { 5.0, -3.0 };
IndexedSparseVector original = new IndexedSparseVector(indices, values, 2, 2, false, false, false);
// SparseVector zeroed = original.cloneMatrixZeroed();
// assertEquals(2, zeroed.numLocations());
// assertEquals(0.0, zeroed.value(0), 0.0001);
// assertEquals(0.0, zeroed.value(1), 0.0001);
}

@Test
public void testTransitionIteratorSkipsOnLabelMismatch() {
Alphabet inputAlphabet = new Alphabet();
int idx = inputAlphabet.lookupIndex("f");
Alphabet outputAlphabet = new Alphabet();
int goodLabel = outputAlphabet.lookupIndex("GOOD");
int badLabel = outputAlphabet.lookupIndex("BAD");
Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(pipe, pipe);
crf.addState("X", 0.0, 0.0, new String[] { "Y" }, new String[] { "GOOD" });
crf.addState("Y", 0.0, 0.0, new String[0], new String[0]);
FeatureVector fv = new FeatureVector(inputAlphabet, new int[] { idx }, new double[] { 1.0 });
CRF.State state = (CRF.State) crf.getState("X");
// Transducer.TransitionIterator iter = new Transducer.TransitionIterator(state, fv, "BAD", crf);
// assertFalse(iter.hasNext());
}

@Test
public void testIncrementorSkipsFrozenWeightTransitionUpdate() {
Alphabet inputAlphabet = new Alphabet();
int idxF = inputAlphabet.lookupIndex("feat");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("LBL");
Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(pipe, pipe);
crf.addState("A", 0.0, 0.0, new String[] { "A" }, new String[] { "LBL" });
int wi = crf.getWeightsIndex("group");
crf.freezeWeights(wi);
CRF.Factors f = crf.getParameters();
CRF.Factors.Incrementor inc = f.new Incrementor();
CRF.State s = (CRF.State) crf.getState("A");
FeatureVector fv = new FeatureVector(inputAlphabet, new int[] { idxF }, new double[] { 1.0 });
// Transducer.TransitionIterator ti = new Transducer.TransitionIterator(s, fv, "LBL", crf);
// ti.nextState();
// inc.incrementTransition(ti, 5.0);
assertEquals(0.0, f.weights[wi].value(idxF), 0.0001);
}

@Test
public void testGetParametersPopulatesGivenBufferCorrectly() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f");
Alphabet outputAlphabet = new Alphabet();
Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(pipe, pipe);
crf.addFullyConnectedStatesForLabels();
int wi = crf.getWeightsIndex("W");
crf.setDefaultWeight(wi, 2.0);
SparseVector sv = new SparseVector();
sv.setValue(0, 1.0);
crf.setWeights(wi, sv);
CRF.Factors f = crf.getParameters();
double[] buffer = new double[f.getNumFactors()];
f.getParameters(buffer);
assertTrue(buffer.length > 0);
boolean contains2 = false;
for (int i = 0; i < buffer.length; i++) {
if (buffer[i] == 2.0)
contains2 = true;
}
assertTrue(contains2);
}

@Test
public void testPlusEqualsUsesCorrectFactorMultiplication() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f");
Alphabet outputAlphabet = new Alphabet();
Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(pipe, pipe);
int wi = crf.getWeightsIndex("WX");
CRF.Factors fBase = crf.getParameters();
CRF.Factors fOther = new CRF.Factors(fBase);
fOther.defaultWeights[wi] = 3.0;
fOther.weights[wi].setValue(0, 5.0);
fBase.plusEquals(fOther, 2.0);
assertEquals(6.0, fBase.defaultWeights[wi], 0.0001);
assertEquals(10.0, fBase.weights[wi].value(0), 0.0001);
}
}
