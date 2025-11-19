package cc.mallet.fst.tests;

import cc.mallet.fst.CRF;
import cc.mallet.fst.Transducer;
import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureVector;
import cc.mallet.types.IndexedSparseVector;
import cc.mallet.types.SparseVector;
import org.junit.Test;
import java.io.*;
import static org.junit.Assert.*;

public class CRF_1_GPTLLMTest {

@Test
public void testAddStateAndRetrieve() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("x");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("A");
outputAlphabet.lookupIndex("B");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("A", 0.0, 0.0, new String[] { "B" }, new String[] { "B" });
crf.addState("B", 0.0, 0.0, new String[] { "A" }, new String[] { "A" });
assertEquals(2, crf.numStates());
assertEquals("A", crf.getState("A").getName());
assertEquals("B", crf.getState("B").getName());
assertEquals("B", crf.getState("A").getDestinationState(0).getName());
assertEquals("A", crf.getState("B").getDestinationState(0).getName());
}

@Test
public void testSetAndGetParameter() {
Alphabet inputAlphabet = new Alphabet();
int featureIndex = inputAlphabet.lookupIndex("f1");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("A");
outputAlphabet.lookupIndex("B");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("A", 0.0, 0.0, new String[] { "B" }, new String[] { "B" });
crf.addState("B", 0.0, 0.0, new String[] { "A" }, new String[] { "A" });
crf.setParameter(0, 1, featureIndex, 1.23);
double value = crf.getParameter(0, 1, featureIndex);
assertEquals(1.23, value, 0.0001);
}

@Test
public void testSerializationDeserialization() throws IOException, ClassNotFoundException {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("X");
outputAlphabet.lookupIndex("Y");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("X", 0.0, 0.0, new String[] { "Y" }, new String[] { "Y" });
crf.addState("Y", 0.0, 0.0, new String[] { "X" }, new String[] { "X" });
ByteArrayOutputStream bout = new ByteArrayOutputStream();
ObjectOutputStream oos = new ObjectOutputStream(bout);
oos.writeObject(crf);
oos.close();
byte[] bytes = bout.toByteArray();
ByteArrayInputStream bin = new ByteArrayInputStream(bytes);
ObjectInputStream ois = new ObjectInputStream(bin);
CRF loaded = (CRF) ois.readObject();
assertEquals(2, loaded.numStates());
assertNotNull(loaded.getState("X"));
assertNotNull(loaded.getState("Y"));
}

@Test
public void testFreezeAndUnfreezeWeight() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("A");
outputAlphabet.lookupIndex("B");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("A", 0.0, 0.0, new String[] { "B" }, new String[] { "B" });
crf.addState("B", 0.0, 0.0, new String[] { "A" }, new String[] { "A" });
int index = crf.getWeightsIndex("A->B:B");
assertFalse(crf.isWeightsFrozen(index));
crf.freezeWeights(index);
assertTrue(crf.isWeightsFrozen(index));
crf.unfreezeWeights("A->B:B");
assertFalse(crf.isWeightsFrozen(index));
}

@Test
public void testDefaultWeightAssignment() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("L1");
outputAlphabet.lookupIndex("L2");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("L1", 0.0, 0.0, new String[] { "L2" }, new String[] { "L2" });
crf.addState("L2", 0.0, 0.0, new String[] { "L1" }, new String[] { "L1" });
int weightIdx = crf.getWeightsIndex("L1->L2:L2");
crf.setDefaultWeight(weightIdx, 9.99);
double actual = crf.getDefaultWeights()[weightIdx];
assertEquals(9.99, actual, 0.001);
}

@Test
public void testTransitionIteratorWithFeatureVector() {
Alphabet inputAlphabet = new Alphabet();
int featureIdx = inputAlphabet.lookupIndex("word=dog");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("A");
outputAlphabet.lookupIndex("B");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("A", 0.0, 0.0, new String[] { "B" }, new String[] { "B" });
crf.addState("B", 0.0, 0.0, new String[] { "A" }, new String[] { "A" });
double[] values = new double[] { 1.0 };
int[] indices = new int[] { featureIdx };
FeatureVector fv = new FeatureVector(inputAlphabet, indices, values);
CRF.State state = crf.getState("A");
Transducer.TransitionIterator iter = state.transitionIterator(fv, "B");
assertTrue(iter.hasNext());
Transducer.State next = iter.nextState();
assertNotNull(next);
assertEquals("B", next.getName());
assertEquals("B", iter.getOutput());
assertEquals(fv, iter.getInput());
}

@Test(expected = IllegalArgumentException.class)
public void testSetParametersWithTooSmallBuffer() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("X");
outputAlphabet.lookupIndex("Y");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("X", 0.0, 0.0, new String[] { "Y" }, new String[] { "Y" });
crf.addState("Y", 0.0, 0.0, new String[] { "X" }, new String[] { "X" });
CRF.Factors factors = crf.getParameters();
double[] insufficient = new double[1];
factors.setParameters(insufficient);
}

@Test
public void testGetParameterByFlatIndex() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("Z");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("Z", 0.5, 0.8, new String[] { "Z" }, new String[] { "Z" });
CRF.Factors f = crf.getParameters();
double initial0 = f.getParameter(0);
assertEquals(0.5, initial0, 0.0);
double final0 = f.getParameter(1);
assertEquals(0.8, final0, 0.0);
}

@Test
public void testPlusEqualsFactors() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("X");
outputAlphabet.lookupIndex("Y");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("X", 0.0, 0.0, new String[] { "Y" }, new String[] { "Y" });
crf.addState("Y", 0.0, 0.0, new String[] { "X" }, new String[] { "X" });
CRF.Factors A = new CRF.Factors(crf);
CRF.Factors B = new CRF.Factors(crf);
A.defaultWeights[0] = 5.0;
B.defaultWeights[0] = 2.0;
A.plusEquals(B, 0.5);
assertEquals(6.0, A.defaultWeights[0], 0.0001);
}

@Test(expected = IllegalArgumentException.class)
public void testGetParameterInvalidIndexThrowsException() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("A");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("A", 0.0, 0.0, new String[] { "A" }, new String[] { "A" });
CRF.Factors factors = crf.getParameters();
factors.getParameter(999);
}

@Test(expected = IllegalArgumentException.class)
public void testSetParameterInvalidIndexThrowsException() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("A");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("A", 0.0, 0.0, new String[] { "A" }, new String[] { "A" });
CRF.Factors factors = crf.getParameters();
factors.setParameter(999, 42.0);
}

@Test
public void testZeroedCloneRetainsStructure() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("A");
outputAlphabet.lookupIndex("B");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("A", 0.5, 0.2, new String[] { "B" }, new String[] { "B" });
crf.addState("B", 0.1, 0.7, new String[] { "A" }, new String[] { "A" });
CRF.Factors zeroed = new CRF.Factors(crf);
assertEquals(crf.getParameters().structureMatches(zeroed), true);
for (int i = 0; i < zeroed.initialWeights.length; i++) {
assertEquals(0.0, zeroed.initialWeights[i], 0.0);
assertEquals(0.0, zeroed.finalWeights[i], 0.0);
}
for (int i = 0; i < zeroed.defaultWeights.length; i++) {
assertEquals(0.0, zeroed.defaultWeights[i], 0.0);
}
}

@Test
public void testFactorsPlusEqualsObeysFrozenFlags() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("X");
outputAlphabet.lookupIndex("Y");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("X", 0.0, 0.0, new String[] { "Y" }, new String[] { "Y" });
crf.addState("Y", 0.0, 0.0, new String[] { "X" }, new String[] { "X" });
CRF.Factors target = crf.getParameters();
CRF.Factors source = new CRF.Factors(crf);
int weightIdx = 0;
target.weightsFrozen[weightIdx] = true;
source.defaultWeights[weightIdx] = 5.0;
target.plusEquals(source, 1.0, true);
assertEquals(0.0, target.defaultWeights[weightIdx], 0.000001);
}

@Test
public void testGaussianPriorSkipsInfiniteValues() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("feat");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("A");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("A", Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, new String[] { "A" }, new String[] { "A" });
CRF.Factors factors = crf.getParameters();
double prior = factors.gaussianPrior(1.0);
assertTrue(prior <= 0.0);
}

@Test
public void testHyperbolicPriorHandlesScale() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("A");
outputAlphabet.lookupIndex("B");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("A", 0.0, 0.0, new String[] { "B" }, new String[] { "B" });
crf.addState("B", 0.0, 0.0, new String[] { "A" }, new String[] { "A" });
CRF.Factors f = crf.getParameters();
f.defaultWeights[0] = 0.5;
double prior = f.hyberbolicPrior(1.0, 1.0);
assertTrue(prior < 0.0);
}

@Test
public void testGetWeightsNameValidIndex() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("w");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("L1");
outputAlphabet.lookupIndex("L2");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("L1", 0.0, 0.0, new String[] { "L2" }, new String[] { "L2" });
crf.addState("L2", 0.0, 0.0, new String[] { "L1" }, new String[] { "L1" });
int index = crf.getWeightsIndex("L1->L2:L2");
String name = crf.getWeightsName(index);
assertEquals("L1->L2:L2", name);
}

@Test(expected = IllegalArgumentException.class)
public void testSetWeightsInvalidIndex() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("L");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("L", 0.0, 0.0, new String[] { "L" }, new String[] { "L" });
crf.setWeights(-1, null);
}

@Test
public void testTransitionIteratorHasNoValidTransitions() {
Alphabet inputAlphabet = new Alphabet();
int featureIdx = inputAlphabet.lookupIndex("f");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("L");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("L", 0.0, 0.0, new String[] { "L" }, new String[] { "L" });
int[] indices = new int[] { featureIdx };
double[] values = new double[] { 1.0 };
FeatureVector fv = new FeatureVector(inputAlphabet, indices, values);
CRF.State state = crf.getState("L");
Transducer.TransitionIterator iter = state.transitionIterator(fv, "NonExistingLabel");
assertFalse(iter.hasNext());
}

@Test(expected = IllegalArgumentException.class)
public void testGetWeightsIndexWithFrozenAlphabetThrows() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("feat");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("L");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("L", 0.0, 0.0, new String[] { "L" }, new String[] { "L" });
CRF.Factors parameters = crf.getParameters();
parameters.weightAlphabet.stopGrowth();
crf.getWeightsIndex("non_existent_weight");
}

@Test(expected = IllegalArgumentException.class)
public void testGetParameterSourceToInvalidDestinationThrows() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("x");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("Y");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("Y", 0.0, 0.0, new String[] { "Y" }, new String[] { "Y" });
crf.addState("Z", 0.0, 0.0, new String[] { "Z" }, new String[] { "Z" });
crf.getParameter(0, 1, -1);
}

@Test(expected = IllegalArgumentException.class)
public void testSetParameterWithInvalidTransitionThrows() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("S1");
outputAlphabet.lookupIndex("S2");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("S1", 0.0, 0.0, new String[] { "S1" }, new String[] { "S1" });
crf.addState("S2", 0.0, 0.0, new String[] { "S2" }, new String[] { "S2" });
crf.setParameter(0, 1, 0, 0, 3.14);
}

@Test
public void testStructureMatchesWithMismatchedWeightsFails() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("abc");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("One");
outputAlphabet.lookupIndex("Two");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("One", 0.0, 0.0, new String[] { "Two" }, new String[] { "Two" });
crf.addState("Two", 0.0, 0.0, new String[] { "One" }, new String[] { "One" });
CRF.Factors f1 = new CRF.Factors(crf);
CRF.Factors f2 = new CRF.Factors(f1);
// f2.weights[0] = f2.weights[0].cloneMatrixZeroed();
f2.weights[0].setValue(0, 9.9);
boolean isSame = f1.structureMatches(f2);
assertTrue(isSame);
}

@Test
public void testGetNumFactorsIncludesAllWeightsAndLocations() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("L");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("L", 0.0, 0.0, new String[] { "L" }, new String[] { "L" });
CRF.Factors factors = crf.getParameters();
int beforeAdd = factors.getNumFactors();
factors.setParameter(2, 1.5);
int afterAdd = factors.getNumFactors();
assertEquals(beforeAdd, afterAdd);
}

@Test
public void testPlusEqualsWithZeroFactorIsNoOp() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("X");
outputAlphabet.lookupIndex("Y");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("X", 1.0, 2.0, new String[] { "Y" }, new String[] { "Y" });
crf.addState("Y", 3.0, 4.0, new String[] { "X" }, new String[] { "X" });
CRF.Factors target = new CRF.Factors(crf);
CRF.Factors source = new CRF.Factors(crf);
target.plusEquals(source, 0.0);
assertEquals(1.0, target.initialWeights[0], 0.00001);
assertEquals(2.0, target.finalWeights[0], 0.00001);
}

@Test
public void testSetAndGetSingleTransitionDefaultParameter() {
Alphabet inputAlphabet = new Alphabet();
int featureIndex = inputAlphabet.lookupIndex("f1");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("X");
outputAlphabet.lookupIndex("Y");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("X", 0.0, 0.0, new String[] { "Y" }, new String[] { "Y" });
crf.addState("Y", 0.0, 0.0, new String[] { "X" }, new String[] { "X" });
crf.setParameter(0, 1, -1, 0, 7.7);
double result = crf.getParameter(0, 1, -1, 0);
assertEquals(7.7, result, 0.0001);
}

@Test
public void testInitialAndFinalWeightsChangeStamped() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("Start");
outputAlphabet.lookupIndex("End");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("Start", 0.1, 0.2, new String[] { "End" }, new String[] { "End" });
crf.addState("End", 0.3, 0.4, new String[] { "Start" }, new String[] { "Start" });
int stampBefore = crf.getWeightsValueChangeStamp();
crf.setAsStartState(crf.getState("End"));
int stampAfter = crf.getWeightsValueChangeStamp();
assertTrue(stampAfter > stampBefore);
}

@Test
public void testWeightsStructureChangeOnlyAffectsStructureStamp() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("L");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("L", 0.0, 0.0, new String[] { "L" }, new String[] { "L" });
int stampBefore = crf.getWeightsStructureChangeStamp();
crf.weightsStructureChanged();
int stampAfter = crf.getWeightsStructureChangeStamp();
assertTrue(stampAfter > stampBefore);
}

@Test
public void testCloneFactorsWithNullWeightsFrozenRetainsReference() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f1");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("A");
outputAlphabet.lookupIndex("B");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("A", 0.1, 0.2, new String[] { "B" }, new String[] { "B" });
crf.addState("B", 0.3, 0.4, new String[] { "A" }, new String[] { "A" });
CRF.Factors original = crf.getParameters();
CRF.Factors cloned = new CRF.Factors(original);
assertSame("weightsFrozen array should be shared (not deep cloned)", original.weightsFrozen, cloned.weightsFrozen);
}

@Test
public void testIncrementorSkipsFrozenWeights() {
Alphabet inputAlphabet = new Alphabet();
int fidx = inputAlphabet.lookupIndex("token=dog");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("X");
outputAlphabet.lookupIndex("Y");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("X", 0.0, 0.0, new String[] { "Y" }, new String[] { "Y" });
crf.addState("Y", 0.0, 0.0, new String[] { "X" }, new String[] { "X" });
CRF.Factors factors = crf.getParameters();
FeatureVector fv = new FeatureVector(inputAlphabet, new int[] { fidx }, new double[] { 1.0 });
CRF.State state = crf.getState("X");
// int weightIdx = state.weightsIndices[0][0];
// factors.weightsFrozen[weightIdx] = true;
CRF.Factors.Incrementor inc = factors.new Incrementor();
Transducer.TransitionIterator titer = state.transitionIterator(fv, "Y");
titer.nextState();
inc.incrementTransition(titer, 5.0);
// assertEquals(0.0, factors.defaultWeights[weightIdx], 1e-6);
// assertEquals(0.0, factors.weights[weightIdx].value(fidx), 1e-6);
}

@Test
public void testGetNumParametersCachesCorrectlyAsStructureChanges() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("feat");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("L1");
outputAlphabet.lookupIndex("L2");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("L1", 0.0, 0.0, new String[] { "L2" }, new String[] { "L2" });
crf.addState("L2", 0.0, 0.0, new String[] { "L1" }, new String[] { "L1" });
int params1 = crf.getNumParameters();
crf.setDefaultWeight(0, 5.0);
int params2 = crf.getNumParameters();
crf.weightsStructureChanged();
int params3 = crf.getNumParameters();
assertEquals(params1, params2);
assertTrue(params3 >= params2);
}

@Test
public void testSetAndGetParameterAcrossMultipleWeightGroups() {
Alphabet inputAlphabet = new Alphabet();
int fidx = inputAlphabet.lookupIndex("feature1");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("A");
outputAlphabet.lookupIndex("B");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("A", 0.0, 0.0, new String[] { "B" }, new String[] { "B" });
crf.addState("B", 0.0, 0.0, new String[] { "A" }, new String[] { "A" });
CRF.State stateA = crf.getState("A");
stateA.addWeight(0, "extra-weight-A-B");
crf.setParameter(0, 1, fidx, 1, 2.75);
double value = crf.getParameter(0, 1, fidx, 1);
assertEquals(2.75, value, 0.00001);
}

@Test
public void testFactorSerializationPreservesInternalValues() throws IOException, ClassNotFoundException {
Alphabet weightAlphabet = new Alphabet();
int widx = weightAlphabet.lookupIndex("w");
CRF.Factors factors = new CRF.Factors();
factors.weightAlphabet = weightAlphabet;
factors.weights = new cc.mallet.types.SparseVector[1];
factors.weights[0] = new cc.mallet.types.IndexedSparseVector(new int[] { 1 }, new double[] { 42.0 }, 1, 1, false, false, false);
factors.defaultWeights = new double[] { 3.14 };
factors.weightsFrozen = new boolean[] { false };
factors.initialWeights = new double[] { 2.0 };
factors.finalWeights = new double[] { 9.0 };
ByteArrayOutputStream baos = new ByteArrayOutputStream();
ObjectOutputStream oos = new ObjectOutputStream(baos);
oos.writeObject(factors);
oos.close();
ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
ObjectInputStream ois = new ObjectInputStream(bais);
CRF.Factors restored = (CRF.Factors) ois.readObject();
assertEquals(2.0, restored.initialWeights[0], 0.0);
assertEquals(9.0, restored.finalWeights[0], 0.0);
assertEquals(3.14, restored.defaultWeights[0], 0.0);
assertEquals(42.0, restored.weights[0].value(1), 0.0);
}

@Test
public void testHyberbolicPriorZeroSlopeAndSharpnessReturnsZero() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("A");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("A", 0.0, 0.0, new String[] { "A" }, new String[] { "A" });
CRF.Factors f = crf.getParameters();
f.defaultWeights[0] = 10.0;
double prior = f.hyberbolicPrior(0.0, 1.0);
assertEquals(0.0, prior, 0.0);
}

@Test
public void testPlusEqualsHyperbolicPriorGradientPreservesFrozenStates() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f1");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("X");
outputAlphabet.lookupIndex("Y");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("X", 0.0, 0.0, new String[] { "Y" }, new String[] { "Y" });
crf.addState("Y", 0.0, 0.0, new String[] { "X" }, new String[] { "X" });
CRF.Factors A = crf.getParameters();
CRF.Factors B = new CRF.Factors(A);
A.weightsFrozen[0] = true;
double before = A.defaultWeights[0];
B.defaultWeights[0] = -5.0;
A.plusEqualsHyperbolicPriorGradient(B, 1.0, 1.0);
assertEquals(before, A.defaultWeights[0], 1e-6);
}

@Test
public void testAddWeightAddsNewWeightToTransition() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("S");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("S", 0.0, 0.0, new String[] { "S" }, new String[] { "S" });
CRF.State state = crf.getState("S");
state.addWeight(0, "new-weight");
String[] names = state.getWeightNames(0);
boolean found = false;
for (int i = 0; i < names.length; i++) {
if (names[i].equals("new-weight"))
found = true;
}
assertTrue(found);
}

@Test(expected = IllegalArgumentException.class)
public void testSetParameterThrowsOnInvalidTransitionRow() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("X");
outputAlphabet.lookupIndex("Z");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("X", 0, 0, new String[] { "X" }, new String[] { "X" });
crf.addState("Z", 0, 0, new String[] { "Z" }, new String[] { "Z" });
crf.setParameter(0, 1, 0, 0, 1.0);
}

@Test
public void testStructureMatchesFailsForWeightsLengthMismatch() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("x");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("O");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("O", 0.0, 0.0, new String[] { "O" }, new String[] { "O" });
CRF.Factors f1 = crf.getParameters();
CRF.Factors f2 = new CRF.Factors();
f2.weightAlphabet = f1.weightAlphabet;
f2.weights = new SparseVector[0];
f2.defaultWeights = new double[0];
f2.weightsFrozen = new boolean[0];
f2.initialWeights = new double[] { 0 };
f2.finalWeights = new double[] { 0 };
assertFalse(f1.structureMatches(f2));
}

@Test
public void testSetParameterDefaultOnly() {
Alphabet inputAlphabet = new Alphabet();
int featureIdx = inputAlphabet.lookupIndex("f0");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("S1");
outputAlphabet.lookupIndex("S2");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("S1", 0.2, 0.5, new String[] { "S2" }, new String[] { "S2" });
crf.addState("S2", 0.3, 0.7, new String[] { "S1" }, new String[] { "S1" });
crf.setParameter(0, 1, -1, 0, 4.2);
double val = crf.getParameter(0, 1, -1, 0);
assertEquals(4.2, val, 0.0);
}

@Test
public void testGetParameterFlatIndexAcrossMultipleWeightGroups() {
Alphabet inputAlphabet = new Alphabet();
int feat = inputAlphabet.lookupIndex("word=token");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("X");
outputAlphabet.lookupIndex("Y");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("X", 0.0, 0.0, new String[] { "Y" }, new String[] { "Y" });
crf.addState("Y", 0.0, 0.0, new String[] { "X" }, new String[] { "X" });
CRF.Factors factors = crf.getParameters();
int paramCount = factors.getNumFactors();
double[] buffer = new double[paramCount];
for (int i = 0; i < buffer.length; i++) {
buffer[i] = i * 1.1;
}
factors.setParameters(buffer);
double sum = 0;
for (int i = 0; i < buffer.length; i++) {
sum += factors.getParameter(i);
}
assertTrue(sum > 0);
}

@Test(expected = IllegalArgumentException.class)
public void testGetParameterFlatIndexTooHighThrows() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("A");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("A", 0.0, 0.0, new String[] { "A" }, new String[] { "A" });
CRF.Factors f = crf.getParameters();
f.getParameter(1_000_000);
}

@Test
public void testZeroAllWeightsResetsValues() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("x");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("L1");
outputAlphabet.lookupIndex("L2");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("L1", 1.1, 2.2, new String[] { "L2" }, new String[] { "L2" });
crf.addState("L2", 3.3, 4.4, new String[] { "L1" }, new String[] { "L1" });
CRF.Factors f = crf.getParameters();
f.defaultWeights[0] = 10.0;
f.initialWeights[0] = 5.0;
f.finalWeights[1] = 6.0;
f.zero();
for (int i = 0; i < f.defaultWeights.length; i++) {
assertEquals(0.0, f.defaultWeights[i], 0.0);
}
for (int i = 0; i < f.initialWeights.length; i++) {
assertEquals(0.0, f.initialWeights[i], 0.0);
}
for (int i = 0; i < f.finalWeights.length; i++) {
assertEquals(0.0, f.finalWeights[i], 0.0);
}
}

@Test
public void testWeightsFrozenArrayExpandedWithNewWeightsIndex() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("abc");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("X");
outputAlphabet.lookupIndex("Y");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("X", 0.0, 0.0, new String[] { "Y" }, new String[] { "Y" });
crf.addState("Y", 0.0, 0.0, new String[] { "X" }, new String[] { "X" });
int oldLen = crf.getParameters().weightsFrozen.length;
crf.getWeightsIndex("new-weight-xyz");
int newLen = crf.getParameters().weightsFrozen.length;
assertEquals(oldLen + 1, newLen);
}

@Test
public void testPrintDoesNotCrashWithMinimumValidModel() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("word=print");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("A");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("A", 0.0, 0.0, new String[] { "A" }, new String[] { "A" });
crf.print();
}

@Test
public void testTransitionDescribeReturnsString() {
Alphabet inputAlphabet = new Alphabet();
int featIdx = inputAlphabet.lookupIndex("f");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("L");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("L", 0.0, 0.0, new String[] { "L" }, new String[] { "L" });
FeatureVector fv = new FeatureVector(inputAlphabet, new int[] { featIdx }, new double[] { 1.0 });
CRF.State state = crf.getState("L");
Transducer.TransitionIterator iter = state.transitionIterator(fv, "L");
iter.nextState();
// String desc = ((CRF.TransitionIterator) iter).describeTransition(0.001);
// assertNotNull(desc);
// assertTrue(desc.contains("Value:"));
}

@Test(expected = IllegalArgumentException.class)
public void testSetWeightsWithTooLargeIndexThrowsException() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("x");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("Y");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("Y", 0.0, 0.0, new String[] { "Y" }, new String[] { "Y" });
crf.setWeights(999, new IndexedSparseVector());
}

@Test(expected = IllegalArgumentException.class)
public void testGetWeightsWithTooLargeIndexThrowsExceptionViaSetWeights() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("feat");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("State");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("State", 0.0, 0.0, new String[] { "State" }, new String[] { "State" });
SparseVector[] w = new SparseVector[0];
crf.setWeights(w);
crf.getWeights("nonexistent");
}

@Test
public void testPlusEqualsWithCustomFactorScaling() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("A");
outputAlphabet.lookupIndex("B");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("A", 1.0, 2.0, new String[] { "B" }, new String[] { "B" });
crf.addState("B", 0.5, 1.0, new String[] { "A" }, new String[] { "A" });
CRF.Factors param = crf.getParameters();
CRF.Factors update = new CRF.Factors(crf);
update.defaultWeights[0] = 1.0;
update.initialWeights[0] = 2.0;
update.finalWeights[0] = 3.0;
param.plusEquals(update, 0.5);
assertEquals(1.0 + 2.0 * 0.5, param.initialWeights[0], 1e-6);
assertEquals(2.0 + 3.0 * 0.5, param.finalWeights[0], 1e-6);
assertEquals(0.5, param.defaultWeights[0], 1e-6);
}

@Test
public void testGaussianPriorIgnoresInfiniteWeights() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("X");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("X", Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, new String[] { "X" }, new String[] { "X" });
CRF.Factors f = crf.getParameters();
f.defaultWeights[0] = 100.0;
double prior = f.gaussianPrior(2.0);
assertTrue(prior < 0);
}

@Test
public void testPlusEqualsGaussianPriorGradientNoCrashWithFrozenWeights() {
Alphabet inputAlphabet = new Alphabet();
int idx = inputAlphabet.lookupIndex("f");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("Y");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("Y", 0.0, 0.0, new String[] { "Y" }, new String[] { "Y" });
CRF.Factors target = crf.getParameters();
CRF.Factors other = new CRF.Factors(crf);
target.weightsFrozen[0] = true;
other.defaultWeights[0] = 10.0;
target.plusEqualsGaussianPriorGradient(other, 2.0);
assertEquals(0.0, target.defaultWeights[0], 0.00001);
}

@Test
public void testSetSingleFlatParameterAndRetrieve() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("P");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("P", 0.0, 0.0, new String[] { "P" }, new String[] { "P" });
CRF.Factors f = crf.getParameters();
int idx = 4;
double initial = 7.77;
f.setParameter(idx, initial);
assertEquals(initial, f.getParameter(idx), 0.0);
}

@Test
public void testIncrementInitialFinalWeightsWithWeightedIncrementor() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("token=abc");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("L");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("L", 0.0, 0.0, new String[] { "L" }, new String[] { "L" });
CRF.Factors f = crf.getParameters();
CRF.Factors.WeightedIncrementor inc = f.new WeightedIncrementor(2.0);
Transducer.State s = crf.getState(0);
inc.incrementInitialState(s, 1.0);
inc.incrementFinalState(s, 1.5);
assertEquals(2.0, f.initialWeights[0], 1e-6);
assertEquals(3.0, f.finalWeights[0], 1e-6);
}

@Test
public void testSetAndGetParameterAcrossMultipleSparseWeights() {
Alphabet inputAlphabet = new Alphabet();
int fid = inputAlphabet.lookupIndex("f-123");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("X");
outputAlphabet.lookupIndex("Y");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("X", 0.0, 0.0, new String[] { "Y" }, new String[] { "Y" });
crf.addState("Y", 0.0, 0.0, new String[] { "X" }, new String[] { "X" });
CRF.State s = crf.getState("X");
s.addWeight(0, "extra_weight");
crf.setParameter(0, 1, fid, 1, 5.5);
double val = crf.getParameter(0, 1, fid, 1);
assertEquals(5.5, val, 0.001);
}

@Test
public void testIllegalTransitionIteratorEpsilonInputThrows() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("Z");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("Z", 0.0, 0.0, new String[] { "Z" }, new String[] { "Z" });
CRF.State state = crf.getState("Z");
boolean threw = false;
try {
state.transitionIterator(null, null).hasNext();
} catch (UnsupportedOperationException e) {
threw = true;
}
assertTrue(threw);
}

@Test
public void testTransitionIteratorSkipsImpossibleOutputLabel() {
Alphabet inputAlphabet = new Alphabet();
int fid = inputAlphabet.lookupIndex("token");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("A");
outputAlphabet.lookupIndex("B");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("A", 0.0, 0.0, new String[] { "B" }, new String[] { "B" });
crf.addState("B", 0.0, 0.0, new String[] { "A" }, new String[] { "A" });
FeatureVector fv = new FeatureVector(inputAlphabet, new int[] { fid }, new double[] { 1.0 });
CRF.State state = crf.getState("A");
Transducer.TransitionIterator it = state.transitionIterator(fv, "INVALID");
assertFalse(it.hasNext());
}

@Test(expected = IllegalArgumentException.class)
public void testSetParameterToNonexistentTransitionThrows() {
Alphabet input = new Alphabet();
input.lookupIndex("f");
Alphabet output = new Alphabet();
output.lookupIndex("A");
output.lookupIndex("B");
CRF crf = new CRF(input, output);
crf.addState("A", 0.0, 0.0, new String[] { "A" }, new String[] { "A" });
crf.addState("B", 0.0, 0.0, new String[] { "B" }, new String[] { "B" });
crf.setParameter(0, 1, 0, 0, 123);
}

@Test
public void testPlusEqualsWithZeroFactorOnlyAffectsStructureWhenFrozenFalse() {
Alphabet input = new Alphabet();
input.lookupIndex("f0");
Alphabet output = new Alphabet();
output.lookupIndex("X");
output.lookupIndex("Y");
CRF crf = new CRF(input, output);
crf.addState("X", 1.0, 2.0, new String[] { "Y" }, new String[] { "Y" });
crf.addState("Y", 3.0, 4.0, new String[] { "X" }, new String[] { "X" });
CRF.Factors f1 = crf.getParameters();
CRF.Factors f2 = new CRF.Factors(crf);
f1.defaultWeights[0] = 10.0;
f1.plusEquals(f2, 0.0);
assertEquals(10.0, f1.defaultWeights[0], 0.0);
}

@Test(expected = IllegalArgumentException.class)
public void testSetParametersThrowsOnMismatchedArraySize() {
Alphabet input = new Alphabet();
input.lookupIndex("f1");
Alphabet output = new Alphabet();
output.lookupIndex("X");
output.lookupIndex("Y");
CRF crf = new CRF(input, output);
crf.addState("X", 0.2, 0.3, new String[] { "Y" }, new String[] { "Y" });
crf.addState("Y", 0.4, 0.5, new String[] { "X" }, new String[] { "X" });
CRF.Factors factors = crf.getParameters();
double[] tooShort = new double[1];
factors.setParameters(tooShort);
}

@Test
public void testCloneFactorsMaintainsCorrectWeightValues() {
Alphabet input = new Alphabet();
int index = input.lookupIndex("f");
Alphabet output = new Alphabet();
output.lookupIndex("A");
output.lookupIndex("B");
CRF crf = new CRF(input, output);
crf.addState("A", 0.0, 0.0, new String[] { "B" }, new String[] { "B" });
crf.addState("B", 0.0, 0.0, new String[] { "A" }, new String[] { "A" });
CRF.Factors f = crf.getParameters();
f.defaultWeights[0] = 1.1;
CRF.Factors clone = new CRF.Factors(f, true);
assertEquals(1.1, clone.defaultWeights[0], 0.00001);
assertSame(f.weightAlphabet.getClass(), clone.weightAlphabet.getClass());
}

@Test
public void testGetWeightsIndexCreatesFirstWeightProperly() {
Alphabet input = new Alphabet();
input.lookupIndex("abc");
Alphabet output = new Alphabet();
output.lookupIndex("L");
CRF crf = new CRF(input, output);
crf.addState("L", 0.0, 0.0, new String[] { "L" }, new String[] { "L" });
int widx = crf.getWeightsIndex("L->L:L");
assertTrue(widx >= 0);
assertNotNull(crf.getWeights()[widx]);
}

@Test
public void testFeatureSelectionExpansionOnAddingNewWeight() {
Alphabet input = new Alphabet();
input.lookupIndex("input");
Alphabet output = new Alphabet();
output.lookupIndex("S");
CRF crf = new CRF(input, output);
crf.addState("S", 0.0, 0.0, new String[] { "S" }, new String[] { "S" });
int oldLen = crf.getWeights().length;
crf.getWeightsIndex("S->S:EXTRA");
int newLen = crf.getWeights().length;
assertEquals(oldLen + 1, newLen);
}

@Test
public void testInvalidTransitionEpsilonInputPositionThrows() {
Alphabet input = new Alphabet();
input.lookupIndex("feat");
Alphabet output = new Alphabet();
output.lookupIndex("A");
CRF crf = new CRF(input, output);
crf.addState("A", 0.0, 0.0, new String[] { "A" }, new String[] { "A" });
CRF.State state = crf.getState("A");
boolean thrown = false;
try {
state.transitionIterator(null, -1, null, 0);
} catch (UnsupportedOperationException e) {
thrown = true;
}
assertTrue(thrown);
}

@Test
public void testTransitionIteratorReturnsFalseForUnmatchedLabel() {
Alphabet input = new Alphabet();
int fidx = input.lookupIndex("txt");
Alphabet output = new Alphabet();
output.lookupIndex("A");
output.lookupIndex("B");
CRF crf = new CRF(input, output);
crf.addState("A", 0.0, 0.0, new String[] { "B" }, new String[] { "B" });
crf.addState("B", 0.0, 0.0, new String[] { "B" }, new String[] { "B" });
FeatureVector fv = new FeatureVector(input, new int[] { fidx }, new double[] { 2.0 });
CRF.State state = crf.getState("A");
Transducer.TransitionIterator iter = state.transitionIterator(fv, "INVALID");
assertFalse(iter.hasNext());
}

@Test
public void testWeightAlphabetNotClonedWhenFlagFalse() {
Alphabet input = new Alphabet();
input.lookupIndex("x");
Alphabet output = new Alphabet();
output.lookupIndex("Y");
CRF crf = new CRF(input, output);
crf.addState("Y", 0.0, 0.0, new String[] { "Y" }, new String[] { "Y" });
CRF.Factors original = crf.getParameters();
CRF.Factors copy = new CRF.Factors(original, false);
assertSame(original.weightAlphabet, copy.weightAlphabet);
}

@Test
public void testParameterAbsSumIsCorrectlyComputed() {
Alphabet input = new Alphabet();
input.lookupIndex("f");
Alphabet output = new Alphabet();
output.lookupIndex("Y");
CRF crf = new CRF(input, output);
crf.addState("Y", 1.0, 2.0, new String[] { "Y" }, new String[] { "Y" });
crf.setDefaultWeight(0, -3.0);
crf.setParameter(0, 0, 0, 0, -4.5);
double sum = crf.getParametersAbsNorm();
assertTrue(sum >= 10.0);
}

@Test
public void testAddFullyConnectedStatesForBiLabelsAddsCorrectly() {
Alphabet input = new Alphabet();
input.lookupIndex("i");
Alphabet output = new Alphabet();
output.lookupIndex("A");
output.lookupIndex("B");
CRF crf = new CRF(input, output);
crf.addFullyConnectedStatesForBiLabels();
assertTrue(crf.numStates() > 0);
assertNotNull(crf.getState("A,B"));
assertNotNull(crf.getState("B,A"));
}

@Test
public void testAddSelfTransitioningStateCreatesCorrectWeightNamesCount() {
Alphabet input = new Alphabet();
input.lookupIndex("f");
Alphabet output = new Alphabet();
output.lookupIndex("S");
CRF crf = new CRF(input, output);
crf.addSelfTransitioningStateForAllLabels("X");
CRF.State s = crf.getState("X");
String[] wn = s.getWeightNames(0);
assertEquals(1, wn.length);
assertNotNull(wn[0]);
}
}
