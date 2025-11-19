package cc.mallet.fst.tests;

import cc.mallet.fst.CRF;
import cc.mallet.fst.Transducer;
import cc.mallet.types.*;
import org.junit.Test;
import java.io.*;
import static org.junit.Assert.*;

public class CRF_3_GPTLLMTest {

@Test
public void testCRFConstructorWithAlphabets() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("feature1");
outputAlphabet.lookupIndex("labelA");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
assertSame(inputAlphabet, crf.getInputAlphabet());
assertSame(outputAlphabet, crf.getOutputAlphabet());
}

@Test
public void testAddStateAndGetState() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f");
outputAlphabet.lookupIndex("L1");
outputAlphabet.lookupIndex("L2");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
String[] dest = new String[] { "L2" };
String[] labels = new String[] { "labelX" };
crf.addState("L1", 0.1, 0.2, dest, labels);
CRF.State state = crf.getState("L1");
assertNotNull(state);
assertEquals("L1", state.getName());
assertEquals(0.1, state.getInitialWeight(), 0.00001);
assertEquals(0.2, state.getFinalWeight(), 0.00001);
}

@Test(expected = IllegalArgumentException.class)
public void testAddDuplicateStateThrowsException() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f");
outputAlphabet.lookupIndex("L");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
String[] dest = new String[] { "L" };
String[] labels = new String[] { "labelX" };
crf.addState("state1", 0.0, 0.0, dest, labels);
crf.addState("state1", 0.0, 0.0, dest, labels);
}

@Test
public void testSetAndGetParameter() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("feat");
outputAlphabet.lookupIndex("A");
outputAlphabet.lookupIndex("B");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("A", 0.0, 0.0, new String[] { "B" }, new String[] { "X" });
crf.addState("B", 0.0, 0.0, new String[] { "A" }, new String[] { "Y" });
int featureIndex = inputAlphabet.lookupIndex("feat");
crf.setParameter(0, 1, featureIndex, 1.23);
double paramValue = crf.getParameter(0, 1, featureIndex);
assertEquals(1.23, paramValue, 0.0001);
}

@Test
public void testSetAndGetDefaultParameter() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("feat");
outputAlphabet.lookupIndex("S1");
outputAlphabet.lookupIndex("S2");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("S1", 0.0, 0.0, new String[] { "S2" }, new String[] { "tag1" });
crf.addState("S2", 0.0, 0.0, new String[] { "S1" }, new String[] { "tag2" });
crf.setParameter(0, 1, -1, 3.5);
double defaultWeight = crf.getParameter(0, 1, -1);
assertEquals(3.5, defaultWeight, 0.0001);
}

@Test
public void testMultipleWeightIndicesAndSetDefaultWeight() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f1");
outputAlphabet.lookupIndex("A");
outputAlphabet.lookupIndex("B");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("A", 0.0, 0.0, new String[] { "B" }, new String[] { "lab" });
crf.addState("B", 0.0, 0.0, new String[] { "A" }, new String[] { "lab" });
String weightName = "A->B:lab";
int index = crf.getWeightsIndex(weightName);
crf.setDefaultWeight(index, 2.5);
double[] defaults = crf.getDefaultWeights();
assertEquals(2.5, defaults[index], 0.0001);
}

@Test
public void testFreezeAndUnfreezeWeight() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f1");
outputAlphabet.lookupIndex("Tag");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("Tag", 0.0, 0.0, new String[] { "Tag" }, new String[] { "Tag" });
String weightName = "Tag->Tag:Tag";
int index = crf.getWeightsIndex(weightName);
crf.freezeWeights(index);
boolean frozen = crf.isWeightsFrozen(index);
assertTrue(frozen);
crf.unfreezeWeights(weightName);
boolean unfrozen = crf.isWeightsFrozen(index);
assertFalse(unfrozen);
}

@Test
public void testFactorsBasicInitialization() {
CRF.Factors factors = new CRF.Factors();
assertEquals(0, factors.initialWeights.length);
assertEquals(0, factors.finalWeights.length);
assertNull(factors.weights);
assertNull(factors.defaultWeights);
}

@Test
public void testFactorsGetAndSetParameterByIndex() {
CRF.Factors factors = new CRF.Factors();
factors.weightAlphabet.lookupIndex("w0");
SparseVector vector = new IndexedSparseVector(new int[] { 0 }, new double[] { 1.0 });
factors.weights = new SparseVector[] { vector };
factors.defaultWeights = new double[] { 2.0 };
factors.weightsFrozen = new boolean[] { false };
factors.initialWeights = new double[] { 0.5 };
factors.finalWeights = new double[] { 0.6 };
int totalFactors = factors.getNumFactors();
double[] buffer = new double[totalFactors];
factors.getParameters(buffer);
CRF.Factors clone = new CRF.Factors(factors, false);
clone.setParameters(buffer);
assertEquals(0.5, clone.initialWeights[0], 0.0001);
assertEquals(0.6, clone.finalWeights[0], 0.0001);
assertEquals(2.0, clone.defaultWeights[0], 0.0001);
assertEquals(1.0, clone.weights[0].value(0), 0.0001);
}

@Test
public void testFactorsPlusEqualsWithScalingFactor() {
CRF.Factors factors = new CRF.Factors();
factors.weightAlphabet.lookupIndex("w");
factors.weights = new SparseVector[] { new IndexedSparseVector(new int[] { 0 }, new double[] { 1.0 }) };
factors.defaultWeights = new double[] { 2.0 };
factors.weightsFrozen = new boolean[] { false };
factors.initialWeights = new double[] { 0.1 };
factors.finalWeights = new double[] { 0.2 };
CRF.Factors other = new CRF.Factors(factors, false);
factors.plusEquals(other, 2.0);
assertEquals(3.0, factors.weights[0].value(0), 0.0001);
assertEquals(6.0, factors.defaultWeights[0], 0.0001);
assertEquals(0.3, factors.initialWeights[0], 0.0001);
assertEquals(0.6, factors.finalWeights[0], 0.0001);
}

@Test
public void testFactorsSerializationRoundtrip() throws IOException, ClassNotFoundException {
CRF.Factors factors = new CRF.Factors();
factors.weightAlphabet.lookupIndex("w");
factors.weights = new SparseVector[] { new IndexedSparseVector(new int[] { 0 }, new double[] { 1.5 }) };
factors.defaultWeights = new double[] { 2.5 };
factors.weightsFrozen = new boolean[] { false };
factors.initialWeights = new double[] { 0.8 };
factors.finalWeights = new double[] { 0.9 };
ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
ObjectOutputStream out = new ObjectOutputStream(byteStream);
out.writeObject(factors);
out.close();
ByteArrayInputStream inputStream = new ByteArrayInputStream(byteStream.toByteArray());
ObjectInputStream in = new ObjectInputStream(inputStream);
CRF.Factors restored = (CRF.Factors) in.readObject();
in.close();
assertEquals(1.5, restored.weights[0].value(0), 0.0001);
assertEquals(2.5, restored.defaultWeights[0], 0.0001);
assertEquals(0.8, restored.initialWeights[0], 0.0001);
assertEquals(0.9, restored.finalWeights[0], 0.0001);
}

@Test
public void testWeightsStructureChangeStampIncrements() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f1");
outputAlphabet.lookupIndex("l1");
CRF crfTest = new CRF(inputAlphabet, outputAlphabet);
int prev = crfTest.getWeightsStructureChangeStamp();
crfTest.weightsStructureChanged();
int after = crfTest.getWeightsStructureChangeStamp();
assertTrue(after > prev);
}

@Test
public void testWeightsValueChangeStampIncrements() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f1");
outputAlphabet.lookupIndex("l1");
CRF crfTest = new CRF(inputAlphabet, outputAlphabet);
int prev = crfTest.getWeightsValueChangeStamp();
crfTest.weightsValueChanged();
int after = crfTest.getWeightsValueChangeStamp();
assertTrue(after > prev);
}

@Test(expected = IllegalArgumentException.class)
public void testGetParameterOutOfBoundsThrowsException() {
CRF.Factors factors = new CRF.Factors();
factors.initialWeights = new double[] { 0.1 };
factors.finalWeights = new double[] { 0.2 };
factors.weights = new SparseVector[] {};
factors.defaultWeights = new double[] {};
factors.getParameter(10);
}

@Test(expected = IllegalArgumentException.class)
public void testSetParameterOutOfBoundsThrowsException() {
CRF.Factors factors = new CRF.Factors();
factors.initialWeights = new double[] { 0.1 };
factors.finalWeights = new double[] { 0.2 };
factors.weights = new SparseVector[] { new IndexedSparseVector(new int[] { 0 }, new double[] { 1.0 }) };
factors.defaultWeights = new double[] { 2.0 };
factors.setParameter(100, 5.0);
}

@Test
public void testStructureMatchesReturnsFalseOnMismatchLength() {
CRF.Factors f1 = new CRF.Factors();
f1.initialWeights = new double[] { 0.1 };
f1.finalWeights = new double[] { 0.2 };
f1.weightsFrozen = new boolean[] { false };
f1.defaultWeights = new double[] { 1.0 };
f1.weights = new SparseVector[] { new IndexedSparseVector(new int[] { 1 }, new double[] { 2.0 }) };
CRF.Factors f2 = new CRF.Factors();
f2.initialWeights = new double[] { 0.1, 0.2 };
f2.finalWeights = new double[] { 0.2, 0.3 };
f2.weightsFrozen = new boolean[] { false, false };
f2.defaultWeights = new double[] { 1.0, 2.0 };
f2.weights = new SparseVector[] { new IndexedSparseVector(new int[] { 1 }, new double[] { 1.0 }), new IndexedSparseVector(new int[] { 2 }, new double[] { 1.0 }) };
boolean match = f1.structureMatches(f2);
assertFalse(match);
}

@Test
public void testPlusEqualsRespectsWeightsFrozenFlag() {
CRF.Factors f1 = new CRF.Factors();
f1.weights = new SparseVector[] { new IndexedSparseVector(new int[] { 0 }, new double[] { 3.0 }) };
f1.defaultWeights = new double[] { 10.0 };
f1.initialWeights = new double[] { 1.0 };
f1.finalWeights = new double[] { 2.0 };
f1.weightsFrozen = new boolean[] { true };
CRF.Factors f2 = new CRF.Factors(f1, false);
f1.plusEquals(f2, 2.0, true);
assertEquals(3.0, f1.weights[0].value(0), 0.0001);
assertEquals(10.0, f1.defaultWeights[0], 0.0001);
}

@Test
public void testSetParameterVectorEntryByOverloadedMethod() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f1");
outputAlphabet.lookupIndex("A");
outputAlphabet.lookupIndex("B");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("A", 0.0, 0.0, new String[] { "B" }, new String[] { "tag" });
crf.addState("B", 0.0, 0.0, new String[] { "A" }, new String[] { "tag" });
int featureIndex = inputAlphabet.lookupIndex("f1");
crf.setParameter(0, 1, featureIndex, 0, 2.22);
assertEquals(2.22, crf.getParameter(0, 1, featureIndex), 0.0001);
}

@Test(expected = IllegalArgumentException.class)
public void testAddStateWithMismatchedArraysThrowsException() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("x");
outputAlphabet.lookupIndex("y");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
String[] destinations = new String[] { "y" };
String[] labels = new String[] { "a", "b" };
crf.addState("badState", 0.0, 0.0, destinations, labels);
}

@Test(expected = IllegalArgumentException.class)
public void testGetWeightsIndexThrowsIfAlphabetFrozenAndUnknownName() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f");
outputAlphabet.lookupIndex("l");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
inputAlphabet.stopGrowth();
crf.getWeightsIndex("unknown-weight");
}

@Test(expected = IllegalArgumentException.class)
public void testSetWeightsIndexOutOfBoundsThrows() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f");
outputAlphabet.lookupIndex("l");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
SparseVector bad = new IndexedSparseVector(new int[] { 0 }, new double[] { 1.0 });
crf.setWeights(999, bad);
}

@Test
public void testZeroOnEmptyFactorsDoesNotThrow() {
CRF.Factors f = new CRF.Factors();
f.weights = new SparseVector[] {};
f.defaultWeights = new double[] {};
f.initialWeights = new double[] {};
f.finalWeights = new double[] {};
f.zero();
assertTrue(true);
}

@Test
public void testAssertNotNaNOrInfinitePassesWithNormalValues() {
CRF.Factors f = new CRF.Factors();
f.initialWeights = new double[] { 0.1 };
f.finalWeights = new double[] { 0.2 };
f.defaultWeights = new double[] { 0.3 };
f.weights = new SparseVector[] { new IndexedSparseVector(new int[] { 0 }, new double[] { 0.4 }) };
f.assertNotNaNOrInfinite();
assertTrue(true);
}

@Test(expected = AssertionError.class)
public void testAssertNotNaNOrInfiniteFailsOnInfinity() {
CRF.Factors f = new CRF.Factors();
f.initialWeights = new double[] { Double.POSITIVE_INFINITY };
f.finalWeights = new double[] { 0.2 };
f.defaultWeights = new double[] { 0.3 };
f.weights = new SparseVector[] { new IndexedSparseVector(new int[] { 0 }, new double[] { 0.4 }) };
f.assertNotNaNOrInfinite();
}

@Test(expected = AssertionError.class)
public void testAssertNotNaNFailsOnNaN() {
CRF.Factors f = new CRF.Factors();
f.initialWeights = new double[] { Double.NaN };
f.finalWeights = new double[] { 0.2 };
f.defaultWeights = new double[] { 0.3 };
f.weights = new SparseVector[] { new IndexedSparseVector(new int[] { 0 }, new double[] { 0.4 }) };
f.assertNotNaN();
}

@Test
public void testCloneZeroedWeightsMatchStructure() {
Alphabet alpha = new Alphabet();
alpha.lookupIndex("feature");
SparseVector[] originalWeights = new SparseVector[] { new IndexedSparseVector(new int[] { 0 }, new double[] { 2.0 }) };
CRF.Factors f1 = new CRF.Factors();
f1.weightAlphabet = alpha;
f1.weights = originalWeights;
f1.defaultWeights = new double[] { 1.0 };
f1.initialWeights = new double[] { 1.0 };
f1.finalWeights = new double[] { 1.0 };
f1.weightsFrozen = new boolean[] { false };
CRF.Factors zeroedClone = new CRF.Factors(f1);
double w = zeroedClone.weights[0].value(0);
assertEquals(0.0, w, 0.0001);
assertEquals(0.0, zeroedClone.defaultWeights[0], 0.0001);
assertEquals(0.0, zeroedClone.initialWeights[0], 0.0001);
assertEquals(0.0, zeroedClone.finalWeights[0], 0.0001);
}

@Test
public void testAddStateWithMultipleWeightNames() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f0");
outputAlphabet.lookupIndex("L1");
outputAlphabet.lookupIndex("L2");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
String[] destinations = new String[] { "L2" };
String[] labels = new String[] { "lab" };
String[][] weightNames = new String[][] { { "w0", "w1" } };
crf.addState("L1", 0.0, 0.0, destinations, labels, weightNames);
CRF.State state = crf.getState("L1");
String[] weightNamesRetrieved = state.getWeightNames(0);
assertEquals("w0", weightNamesRetrieved[0]);
assertEquals("w1", weightNamesRetrieved[1]);
}

@Test
public void testGetNumParametersRecomputesAfterStructureChange() {
Alphabet input = new Alphabet();
Alphabet output = new Alphabet();
input.lookupIndex("feat");
output.lookupIndex("TAG");
CRF crf = new CRF(input, output);
crf.addState("TAG", 0.0, 0.0, new String[] { "TAG" }, new String[] { "TAG" });
int initial = crf.getNumParameters();
crf.setDefaultWeight(0, 2.0);
int same = crf.getNumParameters();
crf.setWeights(0, new IndexedSparseVector(new int[] { 0 }, new double[] { 3.0 }));
int changed = crf.getNumParameters();
assertEquals(initial, same);
assertTrue(changed > initial);
}

@Test
public void testCopyConstructorCreatesIndependentInstance() {
Alphabet input = new Alphabet();
Alphabet output = new Alphabet();
input.lookupIndex("f");
output.lookupIndex("L1");
output.lookupIndex("L2");
CRF crf1 = new CRF(input, output);
crf1.addState("L1", 0.0, 0.0, new String[] { "L2" }, new String[] { "lab" });
crf1.addState("L2", 0.0, 0.0, new String[] { "L1" }, new String[] { "lab" });
CRF crf2 = new CRF(crf1);
assertNotNull(crf2.getState("L1"));
assertNotSame(crf1.getState("L1"), crf2.getState("L1"));
}

@Test
public void testAddWeightToStateAddsCorrectWeightIndex() {
Alphabet input = new Alphabet();
Alphabet output = new Alphabet();
input.lookupIndex("f");
output.lookupIndex("Y");
CRF crf = new CRF(input, output);
crf.addState("Y", 0.0, 0.0, new String[] { "Y" }, new String[] { "tag" });
CRF.State state = crf.getState("Y");
state.addWeight(0, "xform");
String[] weights = state.getWeightNames(0);
boolean found = weights.length > 1;
assertTrue(found);
}

@Test
public void testTransitionIteratorUnsupportedNegativePosition() {
Alphabet input = new Alphabet();
Alphabet output = new Alphabet();
input.lookupIndex("f0");
output.lookupIndex("OUT");
CRF crf = new CRF(input, output);
crf.addState("OUT", 0.0, 0.0, new String[] { "OUT" }, new String[] { "tag" });
// FeatureVectorSequence inputSeq = new FeatureVectorSequence(input);
// inputSeq.add(new FeatureVector(input, new int[] { 0 }));
// CRF.State state = crf.getState("OUT");
try {
// state.transitionIterator(inputSeq, -1, null, 0);
fail();
} catch (UnsupportedOperationException expected) {
assertTrue(true);
}
}

@Test
public void testTransitionIteratorWithNullInputThrows() {
Alphabet input = new Alphabet();
Alphabet output = new Alphabet();
input.lookupIndex("f");
output.lookupIndex("A");
CRF crf = new CRF(input, output);
crf.addState("A", 0.0, 0.0, new String[] { "A" }, new String[] { "lab" });
CRF.State state = crf.getState("A");
try {
state.transitionIterator(null, 0, null, 0);
fail();
} catch (UnsupportedOperationException e) {
assertTrue(true);
}
}

@Test
public void testGetWeightsReturnsCorrectSingleVector() {
Alphabet input = new Alphabet();
Alphabet output = new Alphabet();
input.lookupIndex("f");
output.lookupIndex("O");
CRF crf = new CRF(input, output);
crf.addState("O", 0.0, 0.0, new String[] { "O" }, new String[] { "x" });
String weightName = "O->O:x";
SparseVector vector = new IndexedSparseVector(new int[] { 0 }, new double[] { 0.5 });
crf.setWeights(weightName, vector);
SparseVector result = crf.getWeights(weightName);
assertEquals(0.5, result.value(0), 1e-6);
}

@Test
public void testFeatureAlphabetIsClonedInCopy() {
Alphabet input = new Alphabet();
Alphabet output = new Alphabet();
input.lookupIndex("f");
output.lookupIndex("L1");
output.lookupIndex("L2");
CRF crf = new CRF(input, output);
crf.addState("L1", 0.0, 0.0, new String[] { "L2" }, new String[] { "lab" });
crf.addState("L2", 0.0, 0.0, new String[] { "L1" }, new String[] { "lab" });
CRF copied = new CRF(crf);
String weightName = copied.getWeightsName(0);
assertNotNull(weightName);
}

@Test
public void testPrintDoesNotThrowWithPopulatedCRF() {
Alphabet input = new Alphabet();
Alphabet output = new Alphabet();
input.lookupIndex("f1");
output.lookupIndex("label");
CRF crf = new CRF(input, output);
crf.addFullyConnectedStatesForLabels();
crf.print();
assertTrue(true);
}

@Test
public void testSetAndRetrieveFinalAndInitialStateWeights() {
Alphabet input = new Alphabet();
Alphabet output = new Alphabet();
input.lookupIndex("f");
output.lookupIndex("A");
CRF crf = new CRF(input, output);
crf.addState("A", 0.7, 0.9, new String[] { "A" }, new String[] { "lab" });
CRF.State state = crf.getState("A");
state.setInitialWeight(0.3);
state.setFinalWeight(0.6);
assertEquals(0.3, state.getInitialWeight(), 1e-5);
assertEquals(0.6, state.getFinalWeight(), 1e-5);
}

@Test
public void testGetParameterSingleStateNoFeatures() {
Alphabet input = new Alphabet();
Alphabet output = new Alphabet();
input.lookupIndex("w");
output.lookupIndex("Y");
CRF crf = new CRF(input, output);
crf.addState("Y", 0.1, 0.5, new String[] { "Y" }, new String[] { "Y" });
double param = crf.getParameter(0, 0, -1);
assertEquals(0.0, param, 0.0001);
}

@Test
public void testSetParameterDoesNotFailForZeroSparseVector() {
Alphabet input = new Alphabet();
Alphabet output = new Alphabet();
int featIdx = input.lookupIndex("f1");
output.lookupIndex("T1");
output.lookupIndex("T2");
CRF crf = new CRF(input, output);
crf.addState("T1", 0.0, 0.0, new String[] { "T2" }, new String[] { "L" });
crf.addState("T2", 0.0, 0.0, new String[] { "T1" }, new String[] { "L" });
crf.setParameter(0, 1, featIdx, 0, 0.0);
double val = crf.getParameter(0, 1, featIdx);
assertEquals(0.0, val, 0.00001);
}

@Test
public void testTransitionIteratorWithLabelConstraint() {
Alphabet input = new Alphabet();
Alphabet output = new Alphabet();
int fIndex = input.lookupIndex("f");
output.lookupIndex("B");
CRF crf = new CRF(input, output);
crf.addState("B", 1.0, 2.0, new String[] { "B" }, new String[] { "label" });
FeatureVector inputVec = new FeatureVector(input, new int[] { fIndex }, new double[] { 1.0 });
CRF.State state = crf.getState("B");
Transducer.TransitionIterator iterator = state.transitionIterator(inputVec, "label");
assertTrue(iterator.hasNext());
Transducer.State dest = iterator.nextState();
assertNotNull(dest);
assertEquals("B", dest.getName());
}

@Test
public void testTransitionIteratorSkipsWrongLabel() {
Alphabet input = new Alphabet();
Alphabet output = new Alphabet();
int fIndex = input.lookupIndex("f");
output.lookupIndex("B");
CRF crf = new CRF(input, output);
crf.addState("B", 1.0, 2.0, new String[] { "B" }, new String[] { "label" });
FeatureVector inputVec = new FeatureVector(input, new int[] { fIndex }, new double[] { 1.0 });
CRF.State state = crf.getState("B");
Transducer.TransitionIterator iterator = state.transitionIterator(inputVec, "unmatched");
assertFalse(iterator.hasNext());
}

@Test
public void testFactorsGaussianPriorReturnsFinite() {
CRF.Factors f = new CRF.Factors();
Alphabet alpha = new Alphabet();
alpha.lookupIndex("w");
f.weightAlphabet = alpha;
f.weights = new SparseVector[] { new IndexedSparseVector(new int[] { 0 }, new double[] { 2.0 }) };
f.defaultWeights = new double[] { 1.0 };
f.initialWeights = new double[] { 1.0 };
f.finalWeights = new double[] { 1.0 };
f.weightsFrozen = new boolean[] { false };
double result = f.gaussianPrior(1.0);
assertTrue(Double.isFinite(result));
}

@Test
public void testFactorsHyperbolicPriorReturnsFinite() {
CRF.Factors f = new CRF.Factors();
Alphabet alpha = new Alphabet();
alpha.lookupIndex("p");
f.weightAlphabet = alpha;
f.weights = new SparseVector[] { new IndexedSparseVector(new int[] { 0 }, new double[] { 0.5 }) };
f.defaultWeights = new double[] { 1.0 };
f.initialWeights = new double[] { 0.0 };
f.finalWeights = new double[] { 0.0 };
f.weightsFrozen = new boolean[] { false };
double result = f.hyberbolicPrior(0.01, 1.0);
assertTrue(Double.isFinite(result));
}

@Test
public void testFactorsGetParameterAtExactLastValidIndex() {
Alphabet alpha = new Alphabet();
alpha.lookupIndex("z");
SparseVector sv = new IndexedSparseVector(new int[] { 0 }, new double[] { 42.0 });
CRF.Factors f = new CRF.Factors();
f.weightAlphabet = alpha;
f.weights = new SparseVector[] { sv };
f.defaultWeights = new double[] { 10.0 };
f.initialWeights = new double[] { 1.0 };
f.finalWeights = new double[] { 2.0 };
f.weightsFrozen = new boolean[] { false };
int len = f.getNumFactors();
double last = f.getParameter(len - 1);
assertEquals(42.0, last, 0.0001);
}

@Test
public void testFactorsSerializationPreservesWeights() throws Exception {
Alphabet a = new Alphabet();
a.lookupIndex("feat");
CRF.Factors original = new CRF.Factors();
original.weightAlphabet = a;
original.weights = new SparseVector[] { new IndexedSparseVector(new int[] { 0 }, new double[] { 5.5 }) };
original.defaultWeights = new double[] { 1.1 };
original.initialWeights = new double[] { 2.2 };
original.finalWeights = new double[] { 3.3 };
original.weightsFrozen = new boolean[] { false };
ByteArrayOutputStream bos = new ByteArrayOutputStream();
ObjectOutputStream oos = new ObjectOutputStream(bos);
oos.writeObject(original);
oos.close();
ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
ObjectInputStream ois = new ObjectInputStream(bis);
CRF.Factors restored = (CRF.Factors) ois.readObject();
assertEquals(5.5, restored.weights[0].value(0), 0.0001);
assertEquals(1.1, restored.defaultWeights[0], 0.0001);
assertEquals(2.2, restored.initialWeights[0], 0.0001);
assertEquals(3.3, restored.finalWeights[0], 0.0001);
}

@Test(expected = IllegalArgumentException.class)
public void testGetParameterThrowsIllegalIndexTooHigh() {
CRF.Factors f = new CRF.Factors();
Alphabet alpha = new Alphabet();
alpha.lookupIndex("w");
SparseVector w = new IndexedSparseVector(new int[] { 0 }, new double[] { 1.0 });
f.weightAlphabet = alpha;
f.weights = new SparseVector[] { w };
f.defaultWeights = new double[] { 1.0 };
f.initialWeights = new double[] { 1.0 };
f.finalWeights = new double[] { 1.0 };
f.weightsFrozen = new boolean[] { false };
int tooHigh = f.getNumFactors() + 1;
f.getParameter(tooHigh);
}

@Test
public void testEmptyStateTransitionIteratorHasNextIsFalse() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
int fidx = inputAlphabet.lookupIndex("x");
outputAlphabet.lookupIndex("Y");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("Y", 0.0, 0.0, new String[] { "Y" }, new String[] { "Y" });
CRF.State state = crf.getState("Y");
FeatureVector fv = new FeatureVector(inputAlphabet, new int[] { fidx }, new double[] { 1.0 });
Transducer.TransitionIterator ti = state.transitionIterator(fv, "NOPE");
assertFalse(ti.hasNext());
}

@Test
public void testStateGetDestinationStateReturnsConsistentInstance() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("foo");
outputAlphabet.lookupIndex("Q");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("Q", 0.0, 0.0, new String[] { "Q" }, new String[] { "Q" });
CRF.State state = crf.getState("Q");
Transducer.State one = state.getDestinationState(0);
Transducer.State two = state.getDestinationState(0);
assertSame(one, two);
}

@Test
public void testSetInvalidTransitionThrowsException() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f");
outputAlphabet.lookupIndex("L1");
outputAlphabet.lookupIndex("L2");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("L1", 0.0, 0.0, new String[] { "L2" }, new String[] { "X" });
crf.addState("L2", 0.0, 0.0, new String[] { "L1" }, new String[] { "Y" });
int featureIndex = inputAlphabet.lookupIndex("f");
try {
crf.setParameter(0, 1, featureIndex + 50, 0.0);
assertTrue(true);
} catch (Exception e) {
fail("Exception should not be thrown for invalid feature index");
}
try {
crf.setParameter(1, 0, featureIndex, 0.0);
} catch (Exception e) {
fail("Valid transition and index should not throw");
}
try {
crf.setParameter(0, 0, featureIndex, 0.0);
fail("Expected exception");
} catch (IllegalArgumentException e) {
assertTrue(true);
}
}

@Test
public void testGetWeightsNameAndAccessThrowsPastLimit() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f0");
outputAlphabet.lookupIndex("A");
outputAlphabet.lookupIndex("B");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("A", 0.0, 0.0, new String[] { "B" }, new String[] { "label" });
crf.addState("B", 0.0, 0.0, new String[] { "A" }, new String[] { "label" });
String weightName = "A->B:label";
int validIndex = crf.getWeightsIndex(weightName);
String name = crf.getWeightsName(validIndex);
assertEquals(weightName, name);
try {
crf.getWeightsName(validIndex + 5);
fail("Expected IllegalArgumentException due to out-of-bound index");
} catch (ArrayIndexOutOfBoundsException e) {
assertTrue(true);
} catch (Exception e) {
fail("Expected ArrayIndexOutOfBoundsException but got " + e);
}
}

@Test
public void testGetWeightsDimensionDenselyNoSelection() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
int idx1 = inputAlphabet.lookupIndex("f0");
outputAlphabet.lookupIndex("A");
outputAlphabet.lookupIndex("B");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("A", 0.0, 0.0, new String[] { "B" }, new String[] { "label" });
crf.addState("B", 0.0, 0.0, new String[] { "A" }, new String[] { "label" });
crf.setWeightsDimensionDensely();
SparseVector[] weights = crf.getWeights();
assertNotNull(weights);
assertTrue(weights.length >= 1);
}

@Test
public void testEmptyFeatureVectorInTransitionDotProductIsZero() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
int featIdx = inputAlphabet.lookupIndex("x");
outputAlphabet.lookupIndex("Y");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("Y", 0.0, 0.0, new String[] { "Y" }, new String[] { "label" });
FeatureVector empty = new FeatureVector(inputAlphabet, new int[] {}, new double[] {});
CRF.State state = crf.getState("Y");
Transducer.TransitionIterator iterator = state.transitionIterator(empty, "label");
while (iterator.hasNext()) {
double weight = iterator.getWeight();
assertEquals(0.0, weight, 0.0001);
iterator.nextState();
}
}

@Test
public void testParametersAbsNormIncludesAllWeights() {
Alphabet inputAlpha = new Alphabet();
Alphabet outputAlpha = new Alphabet();
int featIdx = inputAlpha.lookupIndex("x");
outputAlpha.lookupIndex("A");
outputAlpha.lookupIndex("B");
CRF crf = new CRF(inputAlpha, outputAlpha);
crf.addState("A", 1.0, 2.0, new String[] { "B" }, new String[] { "label" });
crf.addState("B", 3.0, 4.0, new String[] { "A" }, new String[] { "label" });
String weightName = "A->B:label";
int widx = crf.getWeightsIndex(weightName);
SparseVector vec = new IndexedSparseVector(new int[] { featIdx }, new double[] { 5.0 });
crf.setWeights(widx, vec);
crf.setDefaultWeight(widx, 6.0);
double norm = crf.getParametersAbsNorm();
assertTrue(norm > 20.0);
}

@Test
public void testGetParameterViaFlatIndexStateOnly() {
CRF.Factors factors = new CRF.Factors();
Alphabet alpha = new Alphabet();
alpha.lookupIndex("w");
SparseVector sv = new IndexedSparseVector(new int[] { 1 }, new double[] { 2.0 });
factors.weightAlphabet = alpha;
factors.weights = new SparseVector[] { sv };
factors.defaultWeights = new double[] { 1.0 };
factors.initialWeights = new double[] { 10.0 };
factors.finalWeights = new double[] { 20.0 };
factors.weightsFrozen = new boolean[] { false };
double init = factors.getParameter(0);
double fin = factors.getParameter(1);
assertEquals(10.0, init, 0.0001);
assertEquals(20.0, fin, 0.0001);
}

@Test(expected = IllegalArgumentException.class)
public void testSetParametersMismatchedBufferLengthThrows() {
Alphabet alpha = new Alphabet();
alpha.lookupIndex("z");
CRF.Factors f = new CRF.Factors();
SparseVector vec = new IndexedSparseVector(new int[] { 0 }, new double[] { 0.1 });
f.weightAlphabet = alpha;
f.weights = new SparseVector[] { vec };
f.defaultWeights = new double[] { 0.2 };
f.initialWeights = new double[] { 0.3 };
f.finalWeights = new double[] { 0.4 };
f.weightsFrozen = new boolean[] { false };
double[] wrong = new double[1];
f.setParameters(wrong);
}

@Test
public void testAddStartStateForcesInitialWeights() {
Alphabet inputAlpha = new Alphabet();
Alphabet outputAlpha = new Alphabet();
inputAlpha.lookupIndex("f1");
outputAlpha.lookupIndex("L");
CRF crf = new CRF(inputAlpha, outputAlpha);
crf.addState("L", 100.0, 200.0, new String[] { "L" }, new String[] { "L" });
crf.addStartState("START");
CRF.State start = crf.getState("START");
assertEquals(0.0, start.getInitialWeight(), 0.00001);
}

@Test
public void testFeatureAlphabetIsUsedInWeights() {
Alphabet input = new Alphabet();
Alphabet output = new Alphabet();
int fvid = input.lookupIndex("featureX");
output.lookupIndex("L");
CRF crf = new CRF(input, output);
crf.addState("L", 0.0, 0.0, new String[] { "L" }, new String[] { "lbl" });
String wname = "L->L:lbl";
int widx = crf.getWeightsIndex(wname);
SparseVector vec = new IndexedSparseVector(new int[] { fvid }, new double[] { 0.9 });
crf.setWeights(widx, vec);
SparseVector restored = crf.getWeights(wname);
assertEquals(0.9, restored.value(fvid), 1e-6);
}

@Test
public void testEqualWeightAlphabetDifferentStructureReturnsFalseInStructureMatch() {
Alphabet alpha = new Alphabet();
int fid = alpha.lookupIndex("f");
CRF.Factors f1 = new CRF.Factors();
CRF.Factors f2 = new CRF.Factors();
SparseVector sv = new IndexedSparseVector(new int[] { fid }, new double[] { 1.0 });
f1.weightAlphabet = alpha;
f1.weights = new SparseVector[] { sv };
f1.defaultWeights = new double[] { 1.0 };
f1.initialWeights = new double[] { 1.0 };
f1.finalWeights = new double[] { 1.0 };
f1.weightsFrozen = new boolean[] { false };
f2.weightAlphabet = alpha;
f2.weights = new SparseVector[] { new IndexedSparseVector(new int[] { fid }, new double[] { 1.0 }), new IndexedSparseVector(new int[] { fid }, new double[] { 2.0 }) };
f2.defaultWeights = new double[] { 1.0, 2.0 };
f2.initialWeights = new double[] { 1.0 };
f2.finalWeights = new double[] { 1.0 };
f2.weightsFrozen = new boolean[] { false, false };
boolean match = f1.structureMatches(f2);
assertFalse(match);
}

@Test
public void testTransitionIteratorGetMethods() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
int feat = inputAlphabet.lookupIndex("f");
outputAlphabet.lookupIndex("A");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("A", 0.0, 0.0, new String[] { "A" }, new String[] { "A" });
FeatureVector fv = new FeatureVector(inputAlphabet, new int[] { feat }, new double[] { 1.0 });
CRF.State state = crf.getState("A");
Transducer.TransitionIterator iter = state.transitionIterator(fv, "A");
assertTrue(iter.hasNext());
Transducer.State dest = iter.nextState();
assertNotNull(dest);
assertEquals("A", iter.getSourceState().getName());
assertEquals("A", iter.getOutput());
assertEquals(fv, iter.getInput());
assertEquals(dest, iter.getDestinationState());
double weight = iter.getWeight();
assertEquals(0.0, weight, 0.0001);
}

@Test
public void testCopyWeightsPreservesValuesAfterClone() {
Alphabet inputAlph = new Alphabet();
inputAlph.lookupIndex("f");
SparseVector original = new IndexedSparseVector(new int[] { 0 }, new double[] { 1.25 });
SparseVector[] weights = new SparseVector[] { original };
CRF.Factors source = new CRF.Factors();
source.weightAlphabet = inputAlph;
source.weights = weights;
source.defaultWeights = new double[] { 0.5 };
source.initialWeights = new double[] { 0.1 };
source.finalWeights = new double[] { 0.9 };
source.weightsFrozen = new boolean[] { false };
CRF.Factors clone = new CRF.Factors(source, false);
assertEquals(1.25, clone.weights[0].valueAtLocation(0), 0.0001);
assertEquals(0.5, clone.defaultWeights[0], 0.0001);
assertEquals(0.1, clone.initialWeights[0], 0.0001);
assertEquals(0.9, clone.finalWeights[0], 0.0001);
}

@Test
public void testSerializationOfStateMaintainsDestinationLinks() throws Exception {
Alphabet input = new Alphabet();
Alphabet output = new Alphabet();
input.lookupIndex("f");
output.lookupIndex("OUT");
CRF crf = new CRF(input, output);
crf.addState("OUT", 0.0, 0.0, new String[] { "OUT" }, new String[] { "OUT" });
CRF.State state = crf.getState("OUT");
ByteArrayOutputStream baos = new ByteArrayOutputStream();
ObjectOutputStream oos = new ObjectOutputStream(baos);
oos.writeObject(state);
oos.writeObject(crf);
oos.close();
ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
ObjectInputStream ois = new ObjectInputStream(bais);
CRF.State deserialized = (CRF.State) ois.readObject();
CRF loadedCRF = (CRF) ois.readObject();
assertNotNull(deserialized);
assertEquals("OUT", deserialized.getName());
}

@Test
public void testWeightsAlphabetMissingIndexThrowsException() {
Alphabet inputAlph = new Alphabet();
Alphabet outputAlph = new Alphabet();
inputAlph.lookupIndex("x");
outputAlph.lookupIndex("L");
CRF crf = new CRF(inputAlph, outputAlph);
crf.addState("L", 0.0, 0.0, new String[] { "L" }, new String[] { "label" });
int badIndex = crf.getWeightsIndex("L->L:label") + 100;
try {
String name = crf.getWeightsName(badIndex);
fail("Expected exception from invalid weight index: got " + name);
} catch (ArrayIndexOutOfBoundsException e) {
assertTrue(true);
}
}

@Test
public void testAppendInitialAndFinalWeightsExpandsCorrectly() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("g");
outputAlphabet.lookupIndex("Z");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
int before = crf.getParameters().initialWeights.length;
crf.addState("Z", 0.7, 0.8, new String[] { "Z" }, new String[] { "tag" });
int after = crf.getParameters().initialWeights.length;
assertEquals(before + 1, after);
}

@Test
public void testTransitionIteratorDescribeTransitionNoException() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
int feat = inputAlphabet.lookupIndex("q");
outputAlphabet.lookupIndex("Q");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("Q", 0.0, 0.0, new String[] { "Q" }, new String[] { "Q" });
FeatureVector fv = new FeatureVector(inputAlphabet, new int[] { feat }, new double[] { 1.0 });
CRF.State state = crf.getState("Q");
Transducer.TransitionIterator iterator = state.transitionIterator(fv, "Q");
String html = null;
if (iterator.hasNext()) {
iterator.nextState();
// html = ((CRF.TransitionIterator) iterator).describeTransition(0.0001);
}
assertNotNull(html);
assertTrue(html.contains("Value:"));
}

@Test
public void testPlusEqualsGaussianPriorGradientWithoutInfinityThrowsNothing() {
Alphabet alpha = new Alphabet();
int fid = alpha.lookupIndex("f");
CRF.Factors f1 = new CRF.Factors();
f1.weightAlphabet = alpha;
f1.weights = new SparseVector[] { new IndexedSparseVector(new int[] { fid }, new double[] { 0.0 }) };
f1.defaultWeights = new double[] { 0.0 };
f1.initialWeights = new double[] { 0.0 };
f1.finalWeights = new double[] { 0.0 };
f1.weightsFrozen = new boolean[] { false };
CRF.Factors grad = new CRF.Factors(f1, false);
f1.plusEqualsGaussianPriorGradient(grad, 1.0);
assertEquals(0.0, f1.initialWeights[0], 0.0001);
}

@Test
public void testCloneZeroedStructureHasSameIndexLayout() {
Alphabet alpha = new Alphabet();
alpha.lookupIndex("k");
SparseVector base = new IndexedSparseVector(new int[] { 0 }, new double[] { 3.14 });
SparseVector[] arr = new SparseVector[] { base };
CRF.Factors original = new CRF.Factors();
original.weightAlphabet = alpha;
original.weights = arr;
original.defaultWeights = new double[] { 1.0 };
original.initialWeights = new double[] { 0.2 };
original.finalWeights = new double[] { 0.3 };
original.weightsFrozen = new boolean[] { false };
CRF.Factors zeroed = new CRF.Factors(original);
assertEquals(base.numLocations(), zeroed.weights[0].numLocations());
assertEquals(0.0, zeroed.weights[0].value(0), 0.00001);
}

@Test(expected = IllegalArgumentException.class)
public void testGetParameterThrowsIfNoMatchingTransitionExists() {
Alphabet input = new Alphabet();
Alphabet output = new Alphabet();
input.lookupIndex("feat");
output.lookupIndex("A");
output.lookupIndex("B");
CRF crf = new CRF(input, output);
crf.addState("A", 0.0, 0.0, new String[] { "B" }, new String[] { "lab1" });
crf.addState("B", 0.0, 0.0, new String[] { "A" }, new String[] { "lab2" });
crf.getParameter(0, 0, -1);
}

@Test(expected = IllegalArgumentException.class)
public void testSetParameterThrowsIfNoMatchingTransitionExists() {
Alphabet input = new Alphabet();
Alphabet output = new Alphabet();
int idx = input.lookupIndex("f1");
output.lookupIndex("X");
output.lookupIndex("Y");
CRF crf = new CRF(input, output);
crf.addState("X", 0.0, 0.0, new String[] { "Y" }, new String[] { "t1" });
crf.addState("Y", 0.0, 0.0, new String[] { "X" }, new String[] { "t2" });
crf.setParameter(0, 0, idx, 1.0);
}

@Test
public void testWeightsValueChangeStampIncrementsOnlyOnValueChange() {
Alphabet input = new Alphabet();
Alphabet output = new Alphabet();
input.lookupIndex("f0");
output.lookupIndex("A");
CRF crf = new CRF(input, output);
crf.addState("A", 0.0, 0.0, new String[] { "A" }, new String[] { "X" });
int before = crf.getWeightsValueChangeStamp();
crf.setDefaultWeight(0, 1.0);
int after = crf.getWeightsValueChangeStamp();
assertTrue(after > before);
}

@Test
public void testFactorsPlusEqualsSingleWeightLocation() {
Alphabet alpha = new Alphabet();
int featIndex = alpha.lookupIndex("f");
CRF.Factors f1 = new CRF.Factors();
f1.weightAlphabet = alpha;
f1.weights = new SparseVector[] { new IndexedSparseVector(new int[] { featIndex }, new double[] { 1.0 }) };
f1.defaultWeights = new double[] { 1.0 };
f1.initialWeights = new double[] { 1.0 };
f1.finalWeights = new double[] { 1.0 };
f1.weightsFrozen = new boolean[] { false };
CRF.Factors f2 = new CRF.Factors(f1, false);
f1.plusEquals(f2, 2.0);
assertEquals(3.0, f1.weights[0].value(featIndex), 0.001);
assertEquals(3.0, f1.defaultWeights[0], 0.001);
assertEquals(3.0, f1.initialWeights[0], 0.001);
assertEquals(3.0, f1.finalWeights[0], 0.001);
}

@Test(expected = IllegalArgumentException.class)
public void testEmptyBufferInGetParametersThrows() {
CRF.Factors f = new CRF.Factors();
Alphabet a = new Alphabet();
a.lookupIndex("w");
f.weightAlphabet = a;
SparseVector sv = new IndexedSparseVector(new int[] { 0 }, new double[] { 1.0 });
f.weights = new SparseVector[] { sv };
f.defaultWeights = new double[] { 0.0 };
f.initialWeights = new double[] { 0.0 };
f.finalWeights = new double[] { 0.0 };
f.weightsFrozen = new boolean[] { false };
double[] buff = new double[2];
f.getParameters(buff);
}

@Test
public void testStateDestinationCachingAvoidsDoubleLookup() {
Alphabet input = new Alphabet();
Alphabet output = new Alphabet();
input.lookupIndex("f");
output.lookupIndex("A");
CRF crf = new CRF(input, output);
crf.addState("A", 0.0, 0.0, new String[] { "A" }, new String[] { "x" });
CRF.State state = crf.getState("A");
Transducer.State first = state.getDestinationState(0);
Transducer.State second = state.getDestinationState(0);
assertSame(first, second);
}

@Test
public void testSettingInitialFinalWeightDirectlyWorks() {
Alphabet in = new Alphabet();
Alphabet out = new Alphabet();
in.lookupIndex("f");
out.lookupIndex("TAG");
CRF crf = new CRF(in, out);
crf.addState("TAG", 0.0, 0.0, new String[] { "TAG" }, new String[] { "lbl" });
CRF.State state = crf.getState("TAG");
state.setInitialWeight(4.5);
state.setFinalWeight(7.3);
assertEquals(4.5, state.getInitialWeight(), 0.0001);
assertEquals(7.3, state.getFinalWeight(), 0.0001);
}

@Test
public void testTransitionIteratorSkipsImpossibleWeights() {
Alphabet in = new Alphabet();
Alphabet out = new Alphabet();
int fid = in.lookupIndex("x");
out.lookupIndex("L");
CRF crf = new CRF(in, out);
crf.addState("L", 0.0, 0.0, new String[] { "L" }, new String[] { "L" });
FeatureVector fv = new FeatureVector(in, new int[] { fid }, new double[] { 1.0 });
CRF.State state = crf.getState("L");
Transducer.TransitionIterator iterator = state.transitionIterator(fv, "unavailable");
assertFalse(iterator.hasNext());
}

@Test
public void testGetOutputAlphabetReturnsExpected() {
Alphabet in = new Alphabet();
Alphabet out = new Alphabet();
in.lookupIndex("f");
out.lookupIndex("X");
CRF crf = new CRF(in, out);
Alphabet result = crf.getOutputAlphabet();
assertSame(out, result);
}

@Test
public void testFreezeWeightsPreventsPlusEqualsUpdate() {
Alphabet input = new Alphabet();
input.lookupIndex("f");
CRF.Factors f1 = new CRF.Factors();
f1.weightAlphabet = input;
f1.weights = new SparseVector[] { new IndexedSparseVector(new int[] { 0 }, new double[] { 5.0 }) };
f1.defaultWeights = new double[] { 10.0 };
f1.initialWeights = new double[] { 1.0 };
f1.finalWeights = new double[] { 1.0 };
f1.weightsFrozen = new boolean[] { true };
CRF.Factors f2 = new CRF.Factors(f1, false);
f1.plusEquals(f2, 1.0, true);
assertEquals(5.0, f1.weights[0].value(0), 0.0001);
assertEquals(10.0, f1.defaultWeights[0], 0.0001);
}

@Test
public void testWeightsWithEmptyLocationsHandled() {
Alphabet input = new Alphabet();
input.lookupIndex("a");
SparseVector sv = new IndexedSparseVector(new int[0], new double[0]);
CRF.Factors f1 = new CRF.Factors();
f1.weights = new SparseVector[] { sv };
f1.defaultWeights = new double[] { 0.0 };
f1.initialWeights = new double[] { 0.0 };
f1.finalWeights = new double[] { 0.0 };
f1.weightsFrozen = new boolean[] { false };
f1.weightAlphabet = input;
assertEquals(2 + 1, f1.getNumFactors());
}

@Test
public void testDefaultFeatureWeightSumInTransitionDotProduct() {
Alphabet in = new Alphabet();
Alphabet out = new Alphabet();
int fid = in.lookupIndex("zz");
out.lookupIndex("OO");
CRF crf = new CRF(in, out);
crf.addState("OO", 0.0, 0.0, new String[] { "OO" }, new String[] { "lbl" });
String wname = "OO->OO:lbl";
int widx = crf.getWeightsIndex(wname);
crf.setDefaultWeight(widx, 0.8);
FeatureVector fv = new FeatureVector(in, new int[] { fid }, new double[] { 1.0 });
CRF.State state = crf.getState("OO");
Transducer.TransitionIterator iterator = state.transitionIterator(fv, "lbl");
assertTrue(iterator.hasNext());
iterator.nextState();
double weight = iterator.getWeight();
assertEquals(0.8, weight, 0.0001);
}
}
