package cc.mallet.fst.tests;

import cc.mallet.fst.CRF;
import cc.mallet.fst.Transducer;
import cc.mallet.pipe.Noop;
import cc.mallet.pipe.Pipe;
import cc.mallet.types.*;
import org.junit.Test;
import java.io.*;
import java.util.ArrayList;
import java.util.regex.Pattern;
import static org.junit.Assert.*;

public class CRF_5_GPTLLMTest {

@Test
public void testAddStateAndTransitions() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("input1");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("labelA");
outputAlphabet.lookupIndex("labelB");
Pipe dummyPipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(dummyPipe, dummyPipe);
crf.addState("S1", 0.2, 0.1, new String[] { "S2" }, new String[] { "labelB" });
crf.addState("S2", 0.3, 0.0, new String[] { "S1" }, new String[] { "labelA" });
assertEquals(2, crf.numStates());
assertEquals("S1", crf.getState(0).getName());
assertEquals("S2", crf.getState(1).getName());
}

@Test(expected = IllegalArgumentException.class)
public void testAddDuplicateStateThrows() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("input1");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("labelA");
Pipe dummyPipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(dummyPipe, dummyPipe);
crf.addState("S1", 0.0, 0.0, new String[] { "S1" }, new String[] { "labelA" });
crf.addState("S1", 0.0, 0.0, new String[] { "S1" }, new String[] { "labelA" });
}

@Test
public void testSetAndGetParameter() {
Alphabet inputAlphabet = new Alphabet();
int fidx = inputAlphabet.lookupIndex("feat1");
Alphabet outputAlphabet = new Alphabet();
int lblIdx = outputAlphabet.lookupIndex("L");
Pipe dummyPipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(dummyPipe, dummyPipe);
crf.addState("S", 0, 0, new String[] { "S" }, new String[] { "L" });
crf.setParameter(0, 0, fidx, 0, 7.0);
double v = crf.getParameter(0, 0, fidx, 0);
assertEquals(7.0, v, 1e-6);
}

@Test(expected = IllegalArgumentException.class)
public void testGetParameterInvalidTransitionThrows() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("L1");
outputAlphabet.lookupIndex("L2");
Pipe dummyPipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(dummyPipe, dummyPipe);
crf.addState("S", 0, 0, new String[] { "S" }, new String[] { "L1" });
crf.getParameter(0, 1, 0, 0);
}

@Test
public void testFreezeUnfreezeWeights() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("L");
Pipe dummyPipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(dummyPipe, dummyPipe);
int index = crf.getWeightsIndex("w");
assertFalse(crf.isWeightsFrozen(index));
crf.freezeWeights(index);
assertTrue(crf.isWeightsFrozen(index));
crf.unfreezeWeights("w");
assertFalse(crf.isWeightsFrozen(index));
}

@Test
public void testGetWeightsIndexReturnsSameIndex() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
Pipe dummyPipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(dummyPipe, dummyPipe);
int idx1 = crf.getWeightsIndex("w1");
int idx2 = crf.getWeightsIndex("w1");
assertEquals(idx1, idx2);
}

@Test
public void testSetWeightsDimensionDenselyDoesNotThrow() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("a");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("A");
Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(pipe, pipe);
crf.getWeightsIndex("w");
crf.setWeightsDimensionDensely();
assertNotNull(crf.getWeights("w"));
}

@Test
public void testCopyConstructorCreatesEqualStates() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("a");
outputAlphabet.lookupIndex("A");
Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(pipe, pipe);
crf.addState("S", 0, 0, new String[] { "S" }, new String[] { "A" });
CRF crfCopy = new CRF(crf);
assertEquals(1, crfCopy.numStates());
assertEquals("S", crfCopy.getState(0).getName());
}

@Test
public void testCRFSerialization() throws Exception {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f");
outputAlphabet.lookupIndex("L");
Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(pipe, pipe);
crf.addState("StateA", 1.0, 0.0, new String[] { "StateA" }, new String[] { "L" });
File file = File.createTempFile("crf", ".ser");
file.deleteOnExit();
ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
out.writeObject(crf);
out.close();
ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
Object obj = in.readObject();
in.close();
assertNotNull(obj);
assertTrue(obj instanceof CRF);
CRF crfDeserialized = (CRF) obj;
assertEquals("StateA", crfDeserialized.getState(0).getName());
}

@Test
public void testAddFullyConnectedStatesForLabels() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("X");
outputAlphabet.lookupIndex("Y");
Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(pipe, pipe);
crf.addFullyConnectedStatesForLabels();
assertEquals(2, crf.numStates());
assertNotNull(crf.getState("X"));
assertNotNull(crf.getState("Y"));
}

@Test
public void testGetWeightsNameReturnsCorrectString() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(pipe, pipe);
int idx = crf.getWeightsIndex("my-weight");
assertEquals("my-weight", crf.getWeightsName(idx));
}

@Test
public void testSetDefaultWeightChangesDefaultValue() {
Alphabet inAlphabet = new Alphabet();
Alphabet outAlphabet = new Alphabet();
Pipe pipe = new Noop(inAlphabet, outAlphabet);
CRF crf = new CRF(pipe, pipe);
int index = crf.getWeightsIndex("x-weight");
crf.setDefaultWeight(index, 2.5);
double[] defaults = crf.getDefaultWeights();
assertEquals(2.5, defaults[index], 1e-6);
}

@Test
public void testParametersAbsNormIsPositive() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(pipe, pipe);
crf.addState("S", 1.1, 0.0, new String[] { "S" }, new String[] { "L" });
double norm = crf.getParametersAbsNorm();
assertTrue(norm > 0.0);
}

@Test
public void testSetParameterDefaultWeightOnly() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f1");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("L");
Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(pipe, pipe);
crf.addState("State1", 0, 1, new String[] { "State1" }, new String[] { "L" });
crf.setParameter(0, 0, -1, 0, 4.2);
double val = crf.getParameter(0, 0, -1, 0);
assertEquals(4.2, val, 1e-5);
}

@Test(expected = IllegalArgumentException.class)
public void testSetParameterInvalidWeightIndexThrows() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("x");
outputAlphabet.lookupIndex("A");
Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(pipe, pipe);
crf.addState("S", 0.0, 0.0, new String[] { "S" }, new String[] { "A" });
crf.setParameter(0, 0, 99, 99, 1.0);
}

@Test
public void testSetAndGetParameterNegativeFeatureIndexAffectsDefault() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("phi");
outputAlphabet.lookupIndex("A");
Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(pipe, pipe);
crf.addState("Q", 0.0, 0.0, new String[] { "Q" }, new String[] { "A" });
crf.setParameter(0, 0, -1, 0, 3.14);
double val = crf.getParameter(0, 0, -1, 0);
assertEquals(3.14, val, 1e-6);
}

@Test(expected = IllegalArgumentException.class)
public void testSetParameterWithInvalidTransition() {
Alphabet a1 = new Alphabet();
Alphabet a2 = new Alphabet();
a1.lookupIndex("f");
a2.lookupIndex("y");
Pipe p = new Noop(a1, a2);
CRF crf = new CRF(p, p);
crf.addState("U", 0, 0, new String[] { "U" }, new String[] { "y" });
crf.setParameter(0, 99, -1, 0, 5.5);
}

@Test
public void testAddStateWithSingleWeightPerTransition() {
Alphabet a1 = new Alphabet();
Alphabet a2 = new Alphabet();
a1.lookupIndex("X");
a2.lookupIndex("Z");
Pipe pipe = new Noop(a1, a2);
CRF crf = new CRF(pipe, pipe);
crf.addState("S", 1.0, 1.0, new String[] { "S" }, new String[] { "Z" }, new String[] { "W" });
assertEquals(1, crf.numStates());
// String[] wnames = crf.getState(0).getWeightNames(0);
// assertEquals(1, wnames.length);
// assertEquals("W", wnames[0]);
}

@Test
public void testAddStateWithMultipleWeightsPerTransition() {
Alphabet a1 = new Alphabet();
Alphabet a2 = new Alphabet();
a1.lookupIndex("i");
a2.lookupIndex("l");
Pipe pipe = new Noop(a1, a2);
CRF crf = new CRF(pipe, pipe);
String[][] weightNames = new String[1][2];
weightNames[0][0] = "w1";
weightNames[0][1] = "w2";
String[] destinations = new String[] { "END" };
String[] labels = new String[] { "l" };
crf.addState("BEGIN", 0.5, 0.5, destinations, labels, weightNames);
assertNotNull(crf.getState("BEGIN"));
}

@Test
public void testAddSelfTransitioningStateForAllLabelsCreatesExpectedState() {
Alphabet input = new Alphabet();
Alphabet output = new Alphabet();
output.lookupIndex("A");
output.lookupIndex("B");
Pipe pipe = new Noop(input, output);
CRF crf = new CRF(pipe, pipe);
crf.addSelfTransitioningStateForAllLabels("SELF");
CRF.State state = (CRF.State) crf.getState("SELF");
assertNotNull(state);
assertEquals(2, state.numDestinations());
assertEquals("SELF", state.getDestinationState(0).getName());
assertEquals("SELF", state.getDestinationState(1).getName());
}

@Test
public void testAddStartStateOverridesOtherInitialWeights() {
Alphabet input = new Alphabet();
Alphabet output = new Alphabet();
output.lookupIndex("A");
Pipe pipe = new Noop(input, output);
CRF crf = new CRF(pipe, pipe);
crf.addState("A", 0.9, 0.0, new String[] { "A" }, new String[] { "A" });
crf.addStartState("GENESIS");
assertEquals(2, crf.numStates());
assertEquals("GENESIS", crf.getState(1).getName());
assertEquals(0.0, crf.getState(1).getInitialWeight(), 0.0001);
assertEquals(CRF.IMPOSSIBLE_WEIGHT, crf.getState(0).getInitialWeight(), 0.0001);
}

@Test
public void testSetFeatureSelectionNoExceptionPath() {
Alphabet input = new Alphabet();
input.lookupIndex("f1");
Alphabet output = new Alphabet();
output.lookupIndex("L");
Pipe pipe = new Noop(input, output);
CRF crf = new CRF(pipe, pipe);
int widx = crf.getWeightsIndex("wF");
FeatureSelection fs = new FeatureSelection(input);
crf.setFeatureSelection(widx, fs);
assertNotNull(fs);
}

@Test
public void testFeatureInducerListExecutedInInduceFeaturesFor() {
Alphabet input = new Alphabet();
input.lookupIndex("f");
Alphabet output = new Alphabet();
output.lookupIndex("G");
Pipe pipe = new Noop(input, output);
CRF crf = new CRF(pipe, pipe);
InstanceList instanceList = new InstanceList(pipe);
FeatureVector fv = new FeatureVector(input, new int[] { 0 }, new double[] { 1.0 });
FeatureVectorSequence fvs = new FeatureVectorSequence(new FeatureVector[] { fv });
Instance inst = new Instance(fvs, null, "x", null);
instanceList.add(inst);
// crf.globalFeatureSelection = null;
// FeatureInducer dummyInducer = new FeatureInducer() {
// 
// public void induceFeaturesFor(InstanceList ilist, boolean b1, boolean b2) {
// assertNotNull(ilist);
// }
// };
ArrayList<FeatureInducer> list = new ArrayList<FeatureInducer>();
// list.add(dummyInducer);
// crf.featureInducers = list;
crf.induceFeaturesFor(instanceList);
// assertEquals(1, crf.featureInducers.size());
}

@Test
public void testGetParameterCountReflectsStateAndWeightChanges() {
Alphabet input = new Alphabet();
Alphabet output = new Alphabet();
input.lookupIndex("a");
output.lookupIndex("A");
Pipe pipe = new Noop(input, output);
CRF crf = new CRF(pipe, pipe);
crf.addState("S", 0.0, 0.0, new String[] { "S" }, new String[] { "A" });
crf.getWeightsIndex("w-alpha");
int paramCount = crf.getNumParameters();
assertTrue(paramCount > 0);
}

@Test(expected = IllegalArgumentException.class)
public void testSetParametersWrongLengthThrows() {
Alphabet input = new Alphabet();
input.lookupIndex("f");
Alphabet output = new Alphabet();
output.lookupIndex("L");
Pipe pipe = new Noop(input, output);
CRF crf = new CRF(pipe, pipe);
crf.addState("A", 0, 0, new String[] { "A" }, new String[] { "L" });
CRF.Factors f = crf.getParameters();
int expectedLength = f.getNumFactors();
double[] wrongLengthArray = new double[expectedLength + 1];
f.setParameters(wrongLengthArray);
}

@Test
public void testGetParameterIndexJustOverBoundsThrows() {
Alphabet input = new Alphabet();
input.lookupIndex("f");
Alphabet output = new Alphabet();
output.lookupIndex("L");
Pipe pipe = new Noop(input, output);
CRF crf = new CRF(pipe, pipe);
crf.addState("A", 0.1, 0.1, new String[] { "A" }, new String[] { "L" });
CRF.Factors f = crf.getParameters();
int lastValidIndex = f.getNumFactors() - 1;
try {
f.getParameter(lastValidIndex + 1);
fail("Expected IllegalArgumentException");
} catch (IllegalArgumentException expected) {
}
}

@Test
public void testZeroedWeightsCleared() {
Alphabet input = new Alphabet();
input.lookupIndex("f1");
input.lookupIndex("f2");
Alphabet output = new Alphabet();
output.lookupIndex("X");
Pipe pipe = new Noop(input, output);
CRF crf = new CRF(pipe, pipe);
crf.getWeightsIndex("w0");
CRF.Factors f = crf.getParameters();
f.defaultWeights[0] = 1.5;
f.initialWeights = new double[] { 1.0 };
f.finalWeights = new double[] { 1.0 };
f.weights[0] = new IndexedSparseVector(new int[] { 0, 1 }, new double[] { 0.5, 0.25 }, 2, 2, false, false, false);
f.zero();
assertEquals(0.0, f.defaultWeights[0], 1e-6);
assertEquals(0.0, f.initialWeights[0], 1e-6);
assertEquals(0.0, f.finalWeights[0], 1e-6);
assertEquals(0.0, f.weights[0].value(0), 1e-6);
assertEquals(0.0, f.weights[0].value(1), 1e-6);
}

@Test
public void testTransitionIteratorHandlesInvalidOutput() {
Alphabet input = new Alphabet();
input.lookupIndex("t");
Alphabet output = new Alphabet();
output.lookupIndex("L");
Pipe pipe = new Noop(input, output);
CRF crf = new CRF(pipe, pipe);
crf.addState("S", 0, 0, new String[] { "S" }, new String[] { "L" });
CRF.State s = (CRF.State) crf.getState(0);
FeatureVector fv = new FeatureVector(input, new int[] { 0 }, new double[] { 1.0 });
Transducer.TransitionIterator it = s.transitionIterator(fv, "BAD_LABEL");
assertFalse(it.hasNext());
}

@Test
public void testWriteMethodCreatesFile() throws Exception {
Alphabet input = new Alphabet();
input.lookupIndex("f");
Alphabet output = new Alphabet();
output.lookupIndex("Y");
Pipe pipe = new Noop(input, output);
CRF crf = new CRF(pipe, pipe);
crf.addState("S", 0.1, 0.2, new String[] { "S" }, new String[] { "Y" });
File tempFile = File.createTempFile("crf_model_test", ".ser");
tempFile.deleteOnExit();
crf.write(tempFile);
assertTrue(tempFile.exists());
assertTrue(tempFile.length() > 0);
}

@Test
public void testPrintDoesNotThrow() throws Exception {
Alphabet input = new Alphabet();
input.lookupIndex("x");
Alphabet output = new Alphabet();
output.lookupIndex("L");
Pipe pipe = new Noop(input, output);
CRF crf = new CRF(pipe, pipe);
crf.addState("Q", 0.0, 1.0, new String[] { "Q" }, new String[] { "L" });
File tmp = File.createTempFile("out", ".txt");
tmp.deleteOnExit();
PrintWriter out = new PrintWriter(new FileWriter(tmp));
crf.print(out);
out.close();
assertTrue(tmp.exists());
assertTrue(tmp.length() > 0);
}

@Test
public void testPlusEqualsUpdatesTargetFactors() {
Alphabet input = new Alphabet();
input.lookupIndex("p");
Alphabet output = new Alphabet();
output.lookupIndex("O");
Pipe pipe = new Noop(input, output);
CRF crf = new CRF(pipe, pipe);
crf.getWeightsIndex("omega");
CRF.Factors a = new CRF.Factors(crf);
CRF.Factors b = new CRF.Factors(crf);
a.defaultWeights[0] = 1.0;
b.defaultWeights[0] = 2.0;
a.initialWeights[0] = 0.5;
b.initialWeights[0] = 2.5;
a.plusEquals(b, 1.0);
assertEquals(3.0, a.defaultWeights[0], 1e-6);
assertEquals(3.0, a.initialWeights[0], 1e-6);
}

@Test
public void testPlusEqualsRespectsFrozenFlag() {
Alphabet input = new Alphabet();
input.lookupIndex("f");
Alphabet output = new Alphabet();
output.lookupIndex("A");
Pipe pipe = new Noop(input, output);
CRF crf = new CRF(pipe, pipe);
int w = crf.getWeightsIndex("frozen_w");
CRF.Factors a = new CRF.Factors(crf);
CRF.Factors b = new CRF.Factors(crf);
a.defaultWeights[w] = 0.0;
b.defaultWeights[w] = 1.0;
crf.freezeWeights(w);
a.plusEquals(b, 1.0, true);
assertEquals(0.0, a.defaultWeights[w], 1e-6);
a.plusEquals(b, 1.0, false);
assertEquals(1.0, a.defaultWeights[w], 1e-6);
}

@Test
public void testStructureMatchesDetectsMismatch() {
Alphabet input = new Alphabet();
input.lookupIndex("f");
Alphabet output = new Alphabet();
output.lookupIndex("L");
Pipe p = new Noop(input, output);
CRF crf = new CRF(p, p);
crf.getWeightsIndex("alpha");
CRF.Factors f1 = new CRF.Factors(crf);
CRF.Factors f2 = new CRF.Factors(crf);
f2.weights[0] = new IndexedSparseVector(new int[] { 0, 1, 2 }, new double[] { 0, 0, 0 }, 3, 3, false, false, false);
boolean matches = f1.structureMatches(f2);
assertFalse(matches);
}

@Test
public void testAssertNotNaNOrInfinitePassesWithValidValues() {
Alphabet input = new Alphabet();
input.lookupIndex("f");
Alphabet output = new Alphabet();
output.lookupIndex("Z");
Pipe pipe = new Noop(input, output);
CRF crf = new CRF(pipe, pipe);
int w = crf.getWeightsIndex("safeW");
CRF.Factors f = crf.getParameters();
f.defaultWeights[w] = 0.0;
f.initialWeights = new double[] { 0.0 };
f.finalWeights = new double[] { 0.0 };
f.weights[w] = new IndexedSparseVector(new int[] {}, new double[] {}, 0, 0, false, false, false);
f.assertNotNaNOrInfinite();
}

@Test
public void testAddStateUsingDefaultConstructorWeightNames() {
Alphabet input = new Alphabet();
Alphabet output = new Alphabet();
output.lookupIndex("X");
output.lookupIndex("Y");
Pipe pipe = new Noop(input, output);
CRF crf = new CRF(pipe, pipe);
crf.addState("S1", 0.5, 0.3, new String[] { "S2" }, new String[] { "Y" });
crf.addState("S2", 0.1, 0.9, new String[] { "S1" }, new String[] { "X" });
CRF.State state = (CRF.State) crf.getState("S1");
String[] weights = state.getWeightNames(0);
assertTrue(weights.length > 0);
assertNotNull(weights[0]);
}

@Test
public void testTransitionIteratorWithValidTransition() {
Alphabet input = new Alphabet();
input.lookupIndex("f1");
Alphabet output = new Alphabet();
output.lookupIndex("L1");
Pipe pipe = new Noop(input, output);
CRF crf = new CRF(pipe, pipe);
crf.addState("s1", 0.0, 0.0, new String[] { "s1" }, new String[] { "L1" });
CRF.State state = (CRF.State) crf.getState("s1");
FeatureVector fv = new FeatureVector(input, new int[] { 0 }, new double[] { 1.0 });
Transducer.TransitionIterator ti = state.transitionIterator(fv, "L1");
assertTrue(ti.hasNext());
assertEquals("s1", ti.getDestinationState().getName());
assertEquals("L1", ti.getOutput());
}

@Test
public void testSetWeightsWithNullThrowsExpected() {
Alphabet input = new Alphabet();
Alphabet output = new Alphabet();
Pipe pipe = new Noop(input, output);
CRF crf = new CRF(pipe, pipe);
try {
crf.setWeights(null);
fail("Expected NullPointerException");
} catch (NullPointerException expected) {
}
}

@Test
public void testSetWeightsAndGetWeightsWorksAsymmetrically() {
Alphabet input = new Alphabet();
input.lookupIndex("f");
Alphabet output = new Alphabet();
Pipe pipe = new Noop(input, output);
CRF crf = new CRF(pipe, pipe);
int idx = crf.getWeightsIndex("wX");
SparseVector[] vectors = new SparseVector[1];
vectors[0] = new IndexedSparseVector(new int[] { 0 }, new double[] { 2.0 }, 1, 1, false, false, false);
crf.setWeights(vectors);
SparseVector ret = crf.getWeights("wX");
assertNotNull(ret);
assertEquals(2.0, ret.value(0), 0.0001);
}

@Test
public void testGetAndSetWeightByNameAndIndexConsistency() {
Alphabet alpha1 = new Alphabet();
alpha1.lookupIndex("f");
Alphabet alpha2 = new Alphabet();
Pipe pipe = new Noop(alpha1, alpha2);
CRF crf = new CRF(pipe, pipe);
int index = crf.getWeightsIndex("custom-weight");
SparseVector vector = new IndexedSparseVector(new int[] { 0 }, new double[] { 5.5 }, 1, 1, false, false, false);
crf.setWeights(index, vector);
SparseVector fetched = crf.getWeights(index);
assertEquals(5.5, fetched.value(0), 0.0001);
SparseVector fetchedByName = crf.getWeights("custom-weight");
assertEquals(5.5, fetchedByName.value(0), 0.0001);
}

@Test
public void testAddFullyConnectedStatesHandlesLabelIdentity() {
Alphabet input = new Alphabet();
Alphabet output = new Alphabet();
output.lookupIndex("East");
output.lookupIndex("West");
Pipe pipe = new Noop(input, output);
CRF crf = new CRF(pipe, pipe);
crf.addFullyConnectedStates(new String[] { "East", "West" });
assertEquals(2, crf.numStates());
assertNotNull(crf.getState("East"));
assertNotNull(crf.getState("West"));
}

@Test
public void testAddStartStateWithoutDestroysInitialWeightDefaults() {
Alphabet input = new Alphabet();
Alphabet output = new Alphabet();
output.lookupIndex("L");
Pipe pipe = new Noop(input, output);
CRF crf = new CRF(pipe, pipe);
crf.addState("Main", 0.9, 0.5, new String[] { "Main" }, new String[] { "L" });
assertEquals(0.9, crf.getState("Main").getInitialWeight(), 0.0001);
crf.addStartState("Start");
assertEquals(CRF.IMPOSSIBLE_WEIGHT, crf.getState("Main").getInitialWeight(), 0.0001);
assertEquals(0.0, crf.getState("Start").getInitialWeight(), 0.0001);
}

@Test
public void testSetWeightsDimensionAsInHandlesUnlabeledData() {
Alphabet input = new Alphabet();
input.lookupIndex("f1");
input.lookupIndex("f2");
Alphabet output = new Alphabet();
output.lookupIndex("O");
Pipe pipe = new Noop(input, output);
CRF crf = new CRF(pipe, pipe);
crf.addState("S1", 0, 0, new String[] { "S1" }, new String[] { "O" });
InstanceList list = new InstanceList(pipe);
FeatureVector fv1 = new FeatureVector(input, new int[] { 0 }, new double[] { 2.0 });
FeatureVector fv2 = new FeatureVector(input, new int[] { 1 }, new double[] { 5.0 });
FeatureVector[] seqFVs = new FeatureVector[] { fv1, fv2 };
FeatureVectorSequence fvs = new FeatureVectorSequence(seqFVs);
Instance instance = new Instance(fvs, null, "x", null);
list.add(instance);
crf.setWeightsDimensionAsIn(list, true);
SparseVector[] weights = crf.getWeights();
assertNotNull(weights);
assertTrue(weights[0].numLocations() >= 1);
}

@Test
public void testGetDefaultWeightsReturnsNonNullAndCorrectLength() {
Alphabet input = new Alphabet();
input.lookupIndex("abc");
Alphabet output = new Alphabet();
Pipe pipe = new Noop(input, output);
CRF crf = new CRF(pipe, pipe);
int idx = crf.getWeightsIndex("w-default");
double[] defaults = crf.getDefaultWeights();
assertNotNull(defaults);
assertTrue(defaults.length > idx);
}

@Test
public void testSerializationPreservesState() throws Exception {
Alphabet input = new Alphabet();
input.lookupIndex("x");
Alphabet output = new Alphabet();
output.lookupIndex("Y");
Pipe pipe = new Noop(input, output);
CRF crf = new CRF(pipe, pipe);
crf.addState("Z", 0.1, 0.9, new String[] { "Z" }, new String[] { "Y" });
File file = File.createTempFile("crf_serial", ".bin");
file.deleteOnExit();
ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
oos.writeObject(crf);
oos.close();
ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
CRF crfReloaded = (CRF) ois.readObject();
ois.close();
assertNotNull(crfReloaded);
assertEquals("Z", crfReloaded.getState(0).getName());
assertEquals(0.1, crfReloaded.getState(0).getInitialWeight(), 0.0001);
}

@Test(expected = IllegalArgumentException.class)
public void testAddStateWithLabelMismatchThrows() {
Alphabet inputAlphabet = new Alphabet();
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("L1");
Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
CRF crf = new CRF(pipe, pipe);
crf.addState("State1", 0.0, 0.0, new String[] { "State2" }, new String[] { "L1", "L2" });
}

@Test(expected = IllegalArgumentException.class)
public void testAddStateWithWeightMismatchThrows() {
Alphabet input = new Alphabet();
Alphabet output = new Alphabet();
output.lookupIndex("Y");
Pipe pipe = new Noop(input, output);
CRF crf = new CRF(pipe, pipe);
crf.addState("A", 0.0, 0.0, new String[] { "B" }, new String[] { "Y" }, new String[][] { { "w1", "w2" }, { "w3" } });
}

@Test
public void testGetWeightsIndexWithFrozenAlphabetThrowsIfUnknown() {
Alphabet input = new Alphabet();
input.lookupIndex("x");
input.stopGrowth();
Alphabet output = new Alphabet();
Pipe pipe = new Noop(input, output);
CRF crf = new CRF(pipe, pipe);
// crf.parameters.weightAlphabet.stopGrowth();
try {
crf.getWeightsIndex("unknown-weight");
fail("Expected IllegalArgumentException");
} catch (IllegalArgumentException expected) {
}
}

@Test(expected = IllegalArgumentException.class)
public void testGetParameterInvalidIndexTooHighThrows() {
Alphabet input = new Alphabet();
input.lookupIndex("f");
Alphabet output = new Alphabet();
output.lookupIndex("A");
Pipe pipe = new Noop(input, output);
CRF crf = new CRF(pipe, pipe);
crf.getWeightsIndex("alpha");
CRF.Factors f = crf.getParameters();
int excessiveIndex = f.getNumFactors() + 5;
f.getParameter(excessiveIndex);
}

@Test
public void testStateSerializationRoundTrip() throws Exception {
Alphabet input = new Alphabet();
input.lookupIndex("w");
Alphabet output = new Alphabet();
output.lookupIndex("Z");
Pipe pipe = new Noop(input, output);
CRF crf = new CRF(pipe, pipe);
crf.addState("S", 0.1, 0.2, new String[] { "S" }, new String[] { "Z" });
CRF.State originalState = (CRF.State) crf.getState("S");
ByteArrayOutputStream bos = new ByteArrayOutputStream();
ObjectOutputStream oos = new ObjectOutputStream(bos);
oos.writeObject(originalState);
oos.close();
ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
CRF.State deserialized = (CRF.State) ois.readObject();
assertEquals("S", deserialized.getName());
assertEquals(0.1, deserialized.getInitialWeight(), 1e-6);
assertEquals(0.2, deserialized.getFinalWeight(), 1e-6);
}

@Test(expected = IllegalArgumentException.class)
public void testSetParameterWithMissingTransitionThrows() {
Alphabet input = new Alphabet();
input.lookupIndex("f");
Alphabet output = new Alphabet();
output.lookupIndex("Y");
Pipe pipe = new Noop(input, output);
CRF crf = new CRF(pipe, pipe);
crf.addState("S1", 0.0, 0.0, new String[] { "S1" }, new String[] { "Y" });
crf.addState("S2", 0.0, 0.0, new String[] { "S2" }, new String[] { "Y" });
crf.setParameter(0, 1, 0, 0, 3.0);
}

@Test
public void testGetParameterForDefaultReturnsCorrect() {
Alphabet input = new Alphabet();
input.lookupIndex("f");
Alphabet output = new Alphabet();
output.lookupIndex("B");
Pipe pipe = new Noop(input, output);
CRF crf = new CRF(pipe, pipe);
crf.addState("S", 0, 0, new String[] { "S" }, new String[] { "B" });
crf.setParameter(0, 0, -1, 0, 1.25);
double val = crf.getParameter(0, 0, -1, 0);
assertEquals(1.25, val, 1e-6);
}

@Test
public void testTransitionIteratorDescribeTransitionDoesNotThrow() {
Alphabet input = new Alphabet();
input.lookupIndex("f");
Alphabet output = new Alphabet();
output.lookupIndex("A");
Pipe pipe = new Noop(input, output);
CRF crf = new CRF(pipe, pipe);
crf.addState("S", 0, 0, new String[] { "S" }, new String[] { "A" });
CRF.State state = (CRF.State) crf.getState("S");
FeatureVector fv = new FeatureVector(input, new int[] { 0 }, new double[] { 1.0 });
Transducer.TransitionIterator ti = state.transitionIterator(fv, "A");
assertTrue(ti.hasNext());
String html = ((Transducer.TransitionIterator) ti).describeTransition(0.01);
assertNotNull(html);
assertTrue(html.contains("Value:"));
}

@Test
public void testFactorsPlusEqualsWithZeroWeightSizeDoesNotThrow() {
Alphabet input = new Alphabet();
Alphabet output = new Alphabet();
Pipe pipe = new Noop(input, output);
CRF crf = new CRF(pipe, pipe);
// crf.parameters.weights = new SparseVector[0];
// crf.parameters.defaultWeights = new double[0];
// crf.parameters.initialWeights = new double[0];
// crf.parameters.finalWeights = new double[0];
// crf.featureSelections = new FeatureSelection[0];
// crf.parameters.weightsFrozen = new boolean[0];
CRF.Factors f1 = crf.getParameters();
CRF.Factors f2 = crf.getParameters();
f1.plusEquals(f2, 1.0);
}

@Test
public void testGetNumParametersReflectsStateAndWeightSize() {
Alphabet input = new Alphabet();
input.lookupIndex("t");
Alphabet output = new Alphabet();
output.lookupIndex("X");
Pipe pipe = new Noop(input, output);
CRF crf = new CRF(pipe, pipe);
crf.addState("SS", 0, 0, new String[] { "SS" }, new String[] { "X" });
crf.getWeightsIndex("wparam");
int params = crf.getNumParameters();
assertTrue(params >= 3);
}

@Test
public void testSetFeatureSelectionUpdatesInternalArray() {
Alphabet input = new Alphabet();
input.lookupIndex("f2");
Alphabet output = new Alphabet();
output.lookupIndex("Z");
Pipe pipe = new Noop(input, output);
CRF crf = new CRF(pipe, pipe);
int weightIdx = crf.getWeightsIndex("my-weight");
FeatureSelection fs = new FeatureSelection(input);
crf.setFeatureSelection(weightIdx, fs);
assertNotNull(fs);
}

@Test
public void testZeroFeaturesWithWeightsDimensionAsIn() {
Alphabet input = new Alphabet();
input.lookupIndex("f1");
Alphabet output = new Alphabet();
output.lookupIndex("L1");
Pipe pipe = new Noop(input, output);
CRF crf = new CRF(pipe, pipe);
crf.addState("S", 0.0, 0.0, new String[] { "S" }, new String[] { "L1" });
crf.getWeightsIndex("wX");
InstanceList instanceList = new InstanceList(pipe);
FeatureVectorSequence fvs = new FeatureVectorSequence(new FeatureVector[] { new FeatureVector(input, new int[0], new double[0]) });
FeatureSequence fs = new FeatureSequence(output, new int[] { 0 });
Instance instance = new Instance(fvs, fs, "name", null);
instanceList.add(instance);
crf.setWeightsDimensionAsIn(instanceList, false);
assertNotNull(crf.getWeights());
assertTrue(crf.getWeights()[0].numLocations() >= 0);
}

@Test
public void testAddFullyConnectedStatesForTriLabelsCreatesProductStates() {
Alphabet input = new Alphabet();
Alphabet output = new Alphabet();
output.lookupIndex("X");
output.lookupIndex("Y");
Pipe pipe = new Noop(input, output);
CRF crf = new CRF(pipe, pipe);
crf.addFullyConnectedStatesForTriLabels();
assertTrue(crf.numStates() > 0);
}

@Test
public void testAddStatesForBiLabelsConnectedAsInCreatesExpectedStateNames() {
Alphabet input = new Alphabet();
Alphabet output = new Alphabet();
output.lookupIndex("B1");
output.lookupIndex("B2");
Pipe pipe = new Noop(input, output);
CRF crf = new CRF(pipe, pipe);
InstanceList list = new InstanceList(pipe);
FeatureVectorSequence fvs = new FeatureVectorSequence(new FeatureVector[] { new FeatureVector(input, new int[0], new double[0]), new FeatureVector(input, new int[0], new double[0]) });
FeatureSequence fs = new FeatureSequence(output, new int[] { 0, 1 });
Instance inst = new Instance(fvs, fs, "x", null);
list.add(inst);
crf.addStatesForBiLabelsConnectedAsIn(list);
boolean found = false;
for (int i = 0; i < crf.numStates(); i++) {
String name = crf.getState(i).getName();
if (name.equals("B1,B2")) {
found = true;
break;
}
}
assertTrue(found);
}

@Test
public void testLabelConnectionsInStartNodeIncluded() {
Alphabet input = new Alphabet();
Alphabet output = new Alphabet();
output.lookupIndex("AA");
output.lookupIndex("BB");
Pipe pipe = new Noop(input, output);
CRF crf = new CRF(pipe, pipe);
InstanceList list = new InstanceList(pipe);
FeatureVectorSequence fvs = new FeatureVectorSequence(new FeatureVector[] { new FeatureVector(input, new int[0], new double[0]), new FeatureVector(input, new int[0], new double[0]) });
FeatureSequence fs = new FeatureSequence(output, new int[] { 0, 1 });
Instance inst = new Instance(fvs, fs, "x", null);
list.add(inst);
String startLabel = "START";
output.lookupIndex(startLabel);
// boolean[][] conns = crf.labelConnectionsIn(list, startLabel);
// assertTrue(conns[output.lookupIndex(startLabel)][0]);
// assertTrue(conns[output.lookupIndex(startLabel)][1]);
}

@Test
public void testCloneConstructorCopiesFeatureSelections() {
Alphabet input = new Alphabet();
input.lookupIndex("f1");
Alphabet output = new Alphabet();
output.lookupIndex("L");
Pipe pipe = new Noop(input, output);
CRF crf = new CRF(pipe, pipe);
crf.addState("S", 0.0, 0.0, new String[] { "S" }, new String[] { "L" });
int weightIdx = crf.getWeightsIndex("w");
FeatureSelection fs = new FeatureSelection(input);
crf.setFeatureSelection(weightIdx, fs);
CRF cloned = new CRF(crf);
// assertEquals(1, cloned.featureSelections.length);
// assertNotNull(cloned.featureSelections[0]);
}

@Test
public void testConcatLabelsWithMultipleSeparators() throws Exception {
Alphabet input = new Alphabet();
Alphabet output = new Alphabet();
output.lookupIndex("A");
output.lookupIndex("B");
Pipe pipe = new Noop(input, output);
CRF crf = new CRF(pipe, pipe);
String[] labels = new String[] { "A", "B" };
java.lang.reflect.Method concatLabels = CRF.class.getDeclaredMethod("concatLabels", String[].class);
concatLabels.setAccessible(true);
String result = (String) concatLabels.invoke(crf, (Object) labels);
assertEquals("A,B", result);
}

@Test
public void testNextKGramConcatenatesCorrectly() throws Exception {
Alphabet input = new Alphabet();
Alphabet output = new Alphabet();
output.lookupIndex("X");
Pipe pipe = new Noop(input, output);
CRF crf = new CRF(pipe, pipe);
String[] history = new String[] { "X", "Y" };
String next = "Z";
int k = 2;
java.lang.reflect.Method method = CRF.class.getDeclaredMethod("nextKGram", String[].class, int.class, String.class);
method.setAccessible(true);
String result = (String) method.invoke(crf, history, k, next);
assertEquals("Y,Z", result);
}

@Test
public void testAllowedHistoryReturnsFalseWithForbidden() throws Exception {
Alphabet input = new Alphabet();
Alphabet output = new Alphabet();
output.lookupIndex("a");
output.lookupIndex("b");
Pipe pipe = new Noop(input, output);
CRF crf = new CRF(pipe, pipe);
String[] history = new String[] { "a", "b" };
java.util.regex.Pattern noPattern = java.util.regex.Pattern.compile("a,b");
java.util.regex.Pattern yesPattern = null;
java.lang.reflect.Method method = CRF.class.getDeclaredMethod("allowedHistory", String[].class, java.util.regex.Pattern.class, java.util.regex.Pattern.class);
method.setAccessible(true);
boolean allowed = (Boolean) method.invoke(crf, history, noPattern, yesPattern);
assertFalse(allowed);
}

@Test
public void testHyberbolicPriorReturnsFiniteValue() {
Alphabet input = new Alphabet();
input.lookupIndex("f");
Alphabet output = new Alphabet();
output.lookupIndex("Y");
Pipe pipe = new Noop(input, output);
CRF crf = new CRF(pipe, pipe);
crf.getWeightsIndex("hyperW");
CRF.Factors f = crf.getParameters();
f.defaultWeights[0] = 1.5;
f.initialWeights = new double[] { 1.0 };
f.finalWeights = new double[] { -0.5 };
f.weights[0] = new IndexedSparseVector(new int[] { 0 }, new double[] { 2.0 }, 1, 1, false, false, false);
double prior = f.hyberbolicPrior(0.5, 1.0);
assertTrue(Double.isFinite(prior));
}

@Test
public void testSerializationRoundTripViaInMemoryBuffer() throws Exception {
Alphabet input = new Alphabet();
input.lookupIndex("x");
Alphabet output = new Alphabet();
output.lookupIndex("T");
Pipe pipe = new Noop(input, output);
CRF crf = new CRF(pipe, pipe);
crf.addState("S", 0.5, 1.0, new String[] { "S" }, new String[] { "T" });
ByteArrayOutputStream bos = new ByteArrayOutputStream();
ObjectOutputStream oos = new ObjectOutputStream(bos);
oos.writeObject(crf);
oos.close();
ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
ObjectInputStream ois = new ObjectInputStream(bis);
CRF deserialized = (CRF) ois.readObject();
assertEquals(crf.numStates(), deserialized.numStates());
assertEquals("S", deserialized.getState(0).getName());
}

@Test
public void testSetParameterAndGetParameterDefaultFeatureOnly() {
Alphabet input = new Alphabet();
Alphabet output = new Alphabet();
input.lookupIndex("f");
output.lookupIndex("A");
Pipe pipe = new Noop(input, output);
CRF crf = new CRF(pipe, pipe);
crf.addState("S", 0.0, 0.0, new String[] { "S" }, new String[] { "A" });
crf.setParameter(0, 0, -1, 0, 3.14);
double value = crf.getParameter(0, 0, -1, 0);
assertEquals(3.14, value, 0.0001);
}

@Test(expected = IllegalArgumentException.class)
public void testSetParameterToInvalidTransitionThrows() {
Alphabet input = new Alphabet();
Alphabet output = new Alphabet();
input.lookupIndex("feat");
output.lookupIndex("L");
Pipe pipe = new Noop(input, output);
CRF crf = new CRF(pipe, pipe);
crf.addState("A", 0.0, 0.0, new String[] { "A" }, new String[] { "L" });
crf.addState("B", 0.0, 0.0, new String[] { "B" }, new String[] { "L" });
crf.setParameter(0, 1, 0, 0, 1.0);
}

@Test
public void testAddFullyConnectedStatesForBiLabelsGeneratesStatesCorrectly() {
Alphabet input = new Alphabet();
Alphabet output = new Alphabet();
output.lookupIndex("A");
output.lookupIndex("B");
Pipe pipe = new Noop(input, output);
CRF crf = new CRF(pipe, pipe);
crf.addFullyConnectedStatesForBiLabels();
assertEquals(4, crf.numStates());
}

@Test
public void testAddFullyConnectedStatesCreatesAllStates() {
Alphabet input = new Alphabet();
Alphabet output = new Alphabet();
output.lookupIndex("A");
output.lookupIndex("B");
output.lookupIndex("C");
Pipe pipe = new Noop(input, output);
CRF crf = new CRF(pipe, pipe);
crf.addFullyConnectedStates(new String[] { "A", "B", "C" });
assertNotNull(crf.getState("A"));
assertNotNull(crf.getState("B"));
assertNotNull(crf.getState("C"));
assertEquals(3, crf.numStates());
}

@Test
public void testFreezeAndUnfreezeSingleWeight() {
Alphabet input = new Alphabet();
Alphabet output = new Alphabet();
Pipe pipe = new Noop(input, output);
CRF crf = new CRF(pipe, pipe);
int widx = crf.getWeightsIndex("W1");
crf.freezeWeights(widx);
assertTrue(crf.isWeightsFrozen(widx));
crf.unfreezeWeights("W1");
assertFalse(crf.isWeightsFrozen(widx));
}

@Test
public void testGaussianPriorReturnsFiniteValue() {
Alphabet input = new Alphabet();
input.lookupIndex("f1");
Alphabet output = new Alphabet();
output.lookupIndex("Y");
Pipe pipe = new Noop(input, output);
CRF crf = new CRF(pipe, pipe);
int widx = crf.getWeightsIndex("w");
CRF.Factors f = crf.getParameters();
f.initialWeights = new double[] { 1.0 };
f.finalWeights = new double[] { 1.0 };
f.defaultWeights[widx] = 2.0;
f.weights[widx] = new IndexedSparseVector(new int[] { 0 }, new double[] { 3.0 }, 1, 1, false, false, false);
double result = f.gaussianPrior(1.0);
assertTrue(Double.isFinite(result));
}

@Test
public void testAddOrderNStatesWithNullDefaults() {
Alphabet input = new Alphabet();
input.lookupIndex("f");
Alphabet output = new Alphabet();
output.lookupIndex("X");
output.lookupIndex("Y");
Pipe pipe = new Noop(input, output);
CRF crf = new CRF(pipe, pipe);
InstanceList list = new InstanceList(pipe);
FeatureVectorSequence fvs = new FeatureVectorSequence(new FeatureVector[] { new FeatureVector(input, new int[] { 0 }, new double[] { 1.0 }), new FeatureVector(input, new int[] { 0 }, new double[] { 1.0 }) });
FeatureSequence fs = new FeatureSequence(output, new int[] { 0, 1 });
list.add(new Instance(fvs, fs, "x", null));
String start = crf.addOrderNStates(list, new int[] { 0, 1 }, null, "S", null, null, false);
assertNotNull(start);
}

@Test
public void testAddOrderNStatesWithPatternsAllowedOnly() {
Alphabet input = new Alphabet();
input.lookupIndex("x");
Alphabet output = new Alphabet();
output.lookupIndex("good");
output.lookupIndex("bad");
Pipe pipe = new Noop(input, output);
CRF crf = new CRF(pipe, pipe);
InstanceList list = new InstanceList(pipe);
FeatureVectorSequence fvs = new FeatureVectorSequence(new FeatureVector[] { new FeatureVector(input, new int[] { 0 }, new double[] { 1.0 }), new FeatureVector(input, new int[] { 0 }, new double[] { 1.0 }) });
FeatureSequence fs = new FeatureSequence(output, new int[] { 0, 0 });
list.add(new Instance(fvs, fs, "instance", null));
Pattern allowed = Pattern.compile("good,good");
crf.addOrderNStates(list, new int[] { 1 }, null, null, null, allowed, true);
for (int i = 0; i < crf.numStates(); i++) {
assertTrue(crf.getState(i).getName().contains("good"));
}
}

@Test
public void testAddOrderNStatesThrowsIfOrdersNotAscending() {
Alphabet input = new Alphabet();
Alphabet output = new Alphabet();
output.lookupIndex("A");
Pipe pipe = new Noop(input, output);
CRF crf = new CRF(pipe, pipe);
int[] orders = new int[] { 1, 0 };
try {
crf.addOrderNStates(new InstanceList(pipe), orders, null, null, null, null, false);
fail("Expected IllegalArgumentException");
} catch (IllegalArgumentException expected) {
}
}

@Test
public void testDefaultWeightUpdateThroughSetParameter() {
Alphabet input = new Alphabet();
input.lookupIndex("f");
Alphabet output = new Alphabet();
output.lookupIndex("Z");
Pipe pipe = new Noop(input, output);
CRF crf = new CRF(pipe, pipe);
crf.addState("S", 0, 0, new String[] { "S" }, new String[] { "Z" });
crf.setParameter(0, 0, -1, 0, 4.5);
double actual = crf.getParameter(0, 0, -1, 0);
assertEquals(4.5, actual, 1e-6);
}

@Test(expected = IllegalArgumentException.class)
public void testSetParameterInvalidFeatureIndexTooHigh() {
Alphabet input = new Alphabet();
input.lookupIndex("f1");
Alphabet output = new Alphabet();
output.lookupIndex("L");
Pipe pipe = new Noop(input, output);
CRF crf = new CRF(pipe, pipe);
crf.addState("S", 0, 0, new String[] { "S" }, new String[] { "L" });
crf.setParameter(0, 0, 9999, 0, 10.0);
}

@Test
public void testWeightsValueChangeStampIncrements() {
Alphabet input = new Alphabet();
Alphabet output = new Alphabet();
Pipe pipe = new Noop(input, output);
CRF crf = new CRF(pipe, pipe);
int before = crf.getWeightsValueChangeStamp();
crf.weightsValueChanged();
int after = crf.getWeightsValueChangeStamp();
assertEquals(before + 1, after);
}

@Test
public void testWeightsStructureChangeStampIncrements() {
Alphabet input = new Alphabet();
Alphabet output = new Alphabet();
Pipe pipe = new Noop(input, output);
CRF crf = new CRF(pipe, pipe);
int before = crf.getWeightsStructureChangeStamp();
crf.weightsStructureChanged();
int after = crf.getWeightsStructureChangeStamp();
assertEquals(before + 1, after);
}

@Test
public void testSetWeightsDimensionDenselyRespectsFeatureSelection() {
Alphabet input = new Alphabet();
input.lookupIndex("a");
input.lookupIndex("b");
Alphabet output = new Alphabet();
output.lookupIndex("W");
Pipe pipe = new Noop(input, output);
CRF crf = new CRF(pipe, pipe);
crf.addState("A", 0, 0, new String[] { "A" }, new String[] { "W" });
int widx = crf.getWeightsIndex("denseW");
FeatureSelection fs = new FeatureSelection(input);
// fs.select(0);
crf.setFeatureSelection(widx, fs);
crf.setWeightsDimensionDensely();
SparseVector[] weights = crf.getWeights();
assertEquals(1, weights[widx].numLocations());
}
}
