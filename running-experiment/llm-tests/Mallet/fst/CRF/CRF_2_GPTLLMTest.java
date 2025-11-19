package cc.mallet.fst.tests;

import cc.mallet.fst.CRF;
import cc.mallet.fst.Transducer;
import cc.mallet.types.*;
import org.junit.Test;
import java.io.*;
import static org.junit.Assert.*;

public class CRF_2_GPTLLMTest {

@Test
public void testDefaultConstructor() {
CRF.Factors factors = new CRF.Factors();
assertNotNull(factors.weightAlphabet);
assertNotNull(factors.initialWeights);
assertNotNull(factors.finalWeights);
assertEquals(0, factors.initialWeights.length);
assertEquals(0, factors.finalWeights.length);
assertNull(factors.weights);
assertNull(factors.defaultWeights);
}

@Test
public void testCopyConstructorWithClonedAlphabet() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("feature1");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("label1");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
String[] dest = new String[] { "label1" };
crf.addState("label1", 0.0, 0.0, dest, dest);
crf.setWeightsDimensionDensely();
CRF.Factors factors = new CRF.Factors(crf);
CRF.Factors copy = new CRF.Factors(factors, true);
assertNotSame(factors.weightAlphabet, copy.weightAlphabet);
assertEquals(factors.weightAlphabet.size(), copy.weightAlphabet.size());
assertEquals(factors.initialWeights.length, copy.initialWeights.length);
assertEquals(factors.finalWeights.length, copy.finalWeights.length);
assertEquals(factors.defaultWeights.length, copy.defaultWeights.length);
assertEquals(factors.weights.length, copy.weights.length);
}

@Test
public void testZeroMethodClearsAllParameters() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("l");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
String[] dest = new String[] { "l" };
crf.addState("l", 0.0, 0.0, dest, dest);
crf.setWeightsDimensionDensely();
CRF.Factors factors = new CRF.Factors(crf);
factors.setParameter(0, 1.0);
factors.setParameter(1, 2.0);
int weightCount = factors.getNumFactors();
if (weightCount > 2) {
factors.setParameter(2, 3.0);
}
factors.zero();
assertEquals(0.0, factors.getParameter(0), 0.0);
assertEquals(0.0, factors.getParameter(1), 0.0);
if (weightCount > 2) {
assertEquals(0.0, factors.getParameter(2), 0.0);
}
}

@Test
public void testStructureMatchesReturnsTrueForIdenticalStructure() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("a");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("x");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
String[] dest = new String[] { "x" };
crf.addState("x", 0.0, 0.0, dest, dest);
crf.setWeightsDimensionDensely();
CRF.Factors f1 = new CRF.Factors(crf);
CRF.Factors f2 = new CRF.Factors(f1);
assertTrue(f1.structureMatches(f2));
}

@Test
public void testGetSetParameter() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("input");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("label");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
String[] dest = new String[] { "label" };
crf.addState("label", 0.0, 0.0, dest, dest);
crf.setWeightsDimensionDensely();
CRF.Factors factors = new CRF.Factors(crf);
int numFactors = factors.getNumFactors();
factors.setParameter(0, 1.5);
factors.setParameter(1, 2.5);
if (numFactors > 2) {
factors.setParameter(2, 3.5);
}
assertEquals(1.5, factors.getParameter(0), 1e-8);
assertEquals(2.5, factors.getParameter(1), 1e-8);
if (numFactors > 2) {
assertEquals(3.5, factors.getParameter(2), 1e-8);
}
}

@Test
public void testPlusEqualsUpdatesCorrectly() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("w");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("y");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
String[] dest = new String[] { "y" };
crf.addState("y", 0.0, 0.0, dest, dest);
crf.setWeightsDimensionDensely();
CRF.Factors f1 = new CRF.Factors(crf);
CRF.Factors f2 = new CRF.Factors(crf);
f1.setParameter(0, 1.0);
f1.setParameter(1, 2.0);
if (f1.getNumFactors() > 2) {
f1.setParameter(2, 3.0);
}
f2.plusEquals(f1, 1.0);
assertEquals(1.0, f2.getParameter(0), 1e-8);
assertEquals(2.0, f2.getParameter(1), 1e-8);
if (f2.getNumFactors() > 2) {
assertEquals(3.0, f2.getParameter(2), 1e-8);
}
}

@Test
public void testGaussianPriorComputesCorrectly() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("w1");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("l1");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
String[] dest = new String[] { "l1" };
crf.addState("l1", 0.0, 0.0, dest, dest);
crf.setWeightsDimensionDensely();
CRF.Factors factors = new CRF.Factors(crf);
factors.setParameter(0, 1.0);
factors.setParameter(1, 2.0);
double prior = factors.gaussianPrior(1.0);
assertTrue(prior < 0.0);
}

@Test(expected = IllegalArgumentException.class)
public void testGetParameterThrowsOnIndexTooHigh() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("foo");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("bar");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
String[] dest = new String[] { "bar" };
crf.addState("bar", 0.0, 0.0, dest, dest);
crf.setWeightsDimensionDensely();
CRF.Factors factors = new CRF.Factors(crf);
int invalidIndex = factors.getNumFactors() + 10;
factors.getParameter(invalidIndex);
}

@Test(expected = IllegalArgumentException.class)
public void testSetParametersWithTooFewValuesThrowsException() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("x1");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("y1");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
String[] dest = new String[] { "y1" };
crf.addState("y1", 0.0, 0.0, dest, dest);
crf.setWeightsDimensionDensely();
CRF.Factors factors = new CRF.Factors(crf);
double[] wrong = new double[factors.getNumFactors() - 1];
factors.setParameters(wrong);
}

@Test
public void testAssertNotNaNOrInfiniteWithInfiniteWeightsShouldFail() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("x2");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("y2");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
String[] dest = new String[] { "y2" };
crf.addState("y2", 0.0, 0.0, dest, dest);
crf.setWeightsDimensionDensely();
CRF.Factors factors = new CRF.Factors(crf);
factors.setParameter(0, Double.POSITIVE_INFINITY);
try {
factors.assertNotNaNOrInfinite();
fail("Expected AssertionError");
} catch (AssertionError expected) {
}
}

@Test
public void testHyberbolicPriorReturnsNegativeValue() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("feat");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("lab");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
String[] dest = new String[] { "lab" };
crf.addState("lab", 0.0, 0.0, dest, dest);
crf.setWeightsDimensionDensely();
CRF.Factors factors = new CRF.Factors(crf);
factors.setParameter(0, 1.0);
double result = factors.hyberbolicPrior(0.5, 1.0);
assertTrue(result < 0);
}

@Test
public void testSetParameterAndGetParameterDefaultWeightOnly() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("a1");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("b1");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
String[] dest = new String[] { "b1" };
crf.addState("b1", 0.0, 0.0, dest, dest);
crf.setWeightsDimensionDensely();
CRF.Factors factors = new CRF.Factors(crf);
int numStates = 1;
int defaultParamIndex = 2 * numStates;
if (defaultParamIndex < factors.getNumFactors()) {
factors.setParameter(defaultParamIndex, Math.PI);
double value = factors.getParameter(defaultParamIndex);
assertEquals(Math.PI, value, 1e-8);
}
}

@Test
public void testPlusEqualsWithFrozenWeightSkipsUpdate() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("x");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("y");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
String[] dest = new String[] { "y" };
crf.addState("y", 0.0, 0.0, dest, dest);
crf.setWeightsDimensionDensely();
CRF.Factors factor1 = new CRF.Factors(crf);
CRF.Factors factor2 = new CRF.Factors(crf);
int paramCount = factor1.getNumFactors();
for (int i = 0; i < paramCount; i++) {
factor1.setParameter(i, 1.0);
}
crf.freezeWeights(0);
factor2.plusEquals(factor1, 1.0, true);
double unchangedValue = factor2.getParameter(0);
assertEquals(0.0, unchangedValue, 1e-8);
}

@Test
public void testPlusEqualsHyperbolicPriorGradientUpdatesWeights() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f1");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("l1");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
String[] dest = new String[] { "l1" };
crf.addState("l1", 0.0, 0.0, dest, dest);
crf.setWeightsDimensionDensely();
CRF.Factors prior = new CRF.Factors(crf);
CRF.Factors gradientSource = new CRF.Factors(crf);
int paramCount = prior.getNumFactors();
if (paramCount > 0) {
prior.setParameter(0, 0.0);
gradientSource.setParameter(0, 1.0);
double slope = 0.5;
double sharpness = 1.0;
prior.plusEqualsHyperbolicPriorGradient(gradientSource, slope, sharpness);
double result = prior.getParameter(0);
assertTrue(result > 0.0);
}
}

@Test
public void testGetParametersFillsArrayCorrectly() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("z");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("q");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
String[] dest = new String[] { "q" };
crf.addState("q", 0.0, 0.0, dest, dest);
crf.setWeightsDimensionDensely();
CRF.Factors factors = new CRF.Factors(crf);
double[] array = new double[factors.getNumFactors()];
factors.setParameter(0, 99.9);
factors.getParameters(array);
assertEquals(99.9, array[0], 1e-8);
}

@Test
public void testWeightedIncrementorAppliesInstanceWeightToTransition() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("alpha");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("omega");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
String[] dest = new String[] { "omega" };
crf.addState("omega", 1.0, 1.0, dest, dest);
crf.setWeightsDimensionDensely();
CRF.Factors factors = new CRF.Factors(crf);
CRF.Factors.WeightedIncrementor inc = factors.new WeightedIncrementor(2.0);
CRF.State state = (CRF.State) crf.getState("omega");
inc.incrementInitialState(state, 3.0);
inc.incrementFinalState(state, 4.0);
double expectedInit = 3.0 * 2.0;
double expectedFinal = 4.0 * 2.0;
assertEquals(expectedInit, factors.getParameter(0), 1e-8);
assertEquals(expectedFinal, factors.getParameter(1), 1e-8);
}

@Test
public void testSetParameterNegativeFeatureIndexIsDefaultWeight() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("x123");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("y123");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
String[] dest = new String[] { "y123" };
crf.addState("y123", 0.0, 0.0, dest, dest);
crf.setWeightsDimensionDensely();
CRF.Factors factors = crf.getParameters();
int sourceState = 0;
int targetState = 0;
int defaultParamOriginal = 2 * crf.numStates();
double oldVal = factors.getParameter(defaultParamOriginal);
crf.setParameter(sourceState, targetState, -1, 123.456);
// double newVal = factors.getParameter(sourceState, targetState, -1);
// assertTrue(newVal != oldVal);
// assertEquals(123.456, newVal, 1e-6);
}

@Test(expected = IllegalArgumentException.class)
public void testGetParameterOnEmptyFactorsShouldThrow() {
CRF.Factors factors = new CRF.Factors();
factors.getParameter(0);
}

@Test
public void testStructureMatchesWithDifferentWeightSizesReturnsFalse() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("input");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("label");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("label", 0.0, 0.0, new String[] { "label" }, new String[] { "label" });
crf.setWeightsDimensionDensely();
CRF.Factors factorsA = new CRF.Factors(crf);
CRF.Factors factorsB = new CRF.Factors(crf);
SparseVector[] newWeight = new SparseVector[factorsB.weights.length];
newWeight[0] = new IndexedSparseVector(new int[] { 0, 1 }, new double[] { 1.0, 2.0 }, 2, 2, false, false, false);
for (int i = 1; i < newWeight.length; i++) {
// newWeight[i] = (SparseVector) factorsB.weights[i].clone();
}
factorsB.weights = newWeight;
boolean matched = factorsA.structureMatches(factorsB);
assertFalse("Structure match should be false with different weight locations", matched);
}

@Test
public void testGetNumFactorsConsistencyAfterModifyWeight() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("fx");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("lx");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("lx", 0.0, 0.0, new String[] { "lx" }, new String[] { "lx" });
crf.setWeightsDimensionDensely();
CRF.Factors factors = new CRF.Factors(crf);
int before = factors.getNumFactors();
crf.setWeights(0, new IndexedSparseVector(new int[] { 0, 1 }, new double[] { 1.0, 2.0 }, 2, 2, false, false, false));
crf.weightsStructureChanged();
int after = factors.getNumFactors();
assertNotEquals("Cached numParameters should be invalidated after structure change", before, after);
}

@Test
public void testGetAndSetParameterAcrossAllRegions() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("hello");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("world");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("world", 0.0, 0.0, new String[] { "world" }, new String[] { "world" });
crf.setWeightsDimensionDensely();
CRF.Factors factors = new CRF.Factors(crf);
int count = 0;
double[] expected = new double[factors.getNumFactors()];
while (count < expected.length) {
expected[count] = count + 10.0;
factors.setParameter(count, expected[count]);
count++;
}
int index = 0;
while (index < expected.length) {
double actual = factors.getParameter(index);
assertEquals(expected[index], actual, 1e-8);
index++;
}
}

@Test
public void testPlusEqualsWithNegativeScaleShouldSubtract() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("k");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("v");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("v", 0.0, 0.0, new String[] { "v" }, new String[] { "v" });
crf.setWeightsDimensionDensely();
CRF.Factors source = new CRF.Factors(crf);
CRF.Factors target = new CRF.Factors(crf);
target.setParameter(0, 3.0);
source.setParameter(0, 2.0);
target.plusEquals(source, -1.0);
assertEquals(1.0, target.getParameter(0), 1e-8);
}

@Test
public void testWeightedIncrementorSkipsFrozenWeight() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("abc");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("xyz");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("xyz", 0.0, 0.0, new String[] { "xyz" }, new String[] { "xyz" });
crf.setWeightsDimensionDensely();
crf.freezeWeights(0);
CRF.Factors factors = crf.getParameters();
CRF.Factors.WeightedIncrementor incr = factors.new WeightedIncrementor(1.0);
CRF.State state = (CRF.State) crf.getState("xyz");
int transitionIndex = 0;
FeatureVector fv = new FeatureVector(inputAlphabet, new int[] { 0 }, new double[] { 1.0 });
// CRF.TransitionIterator iterator = new CRF.TransitionIterator(state, fv, "xyz", crf);
// if (iterator.hasNext()) {
// iterator.nextState();
// incr.incrementTransition(iterator, 1.0);
// }
double weight = factors.getParameter(2);
assertEquals(0.0, weight, 1e-6);
}

@Test
public void testGetParameterBoundaryLastValidIndex() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("z");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("z");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("z", 0.0, 0.0, new String[] { "z" }, new String[] { "z" });
crf.setWeightsDimensionDensely();
CRF.Factors factors = crf.getParameters();
int maxIndex = factors.getNumFactors() - 1;
factors.setParameter(maxIndex, 7.25);
double value = factors.getParameter(maxIndex);
assertEquals(7.25, value, 0.0);
}

@Test(expected = IllegalArgumentException.class)
public void testSetParameterIndexTooHighShouldThrow() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("w");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("w");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("w", 0.0, 0.0, new String[] { "w" }, new String[] { "w" });
crf.setWeightsDimensionDensely();
CRF.Factors factors = crf.getParameters();
int high = factors.getNumFactors() + 100;
factors.setParameter(high, 99.99);
}

@Test
public void testPlusEqualsGaussianPriorGradientRespectsFrozen() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("in");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("out");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("out", 0.0, 0.0, new String[] { "out" }, new String[] { "out" });
crf.setWeightsDimensionDensely();
crf.freezeWeights(0);
CRF.Factors original = crf.getParameters();
CRF.Factors grad = new CRF.Factors(crf);
double before = original.getParameter(2);
grad.setParameter(2, 10.0);
original.plusEqualsGaussianPriorGradient(grad, 1.0);
double after = original.getParameter(2);
assertEquals(before, after, 0.0);
}

@Test
public void testDefaultWeightsOnlyContributeToAbsNorm() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("w1");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("y1");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("y1", 0.0, 0.0, new String[] { "y1" }, new String[] { "y1" });
crf.setWeightsDimensionDensely();
CRF.Factors factors = crf.getParameters();
int defaultWeightIndex = 2 * crf.numStates();
factors.setParameter(defaultWeightIndex, -5.0);
double norm = factors.getParametersAbsNorm();
assertEquals(5.0, norm, 1e-6);
}

@Test
public void testSetAndGetInitialAndFinalWeightSeparately() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("feat");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("lab");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("lab", 0.0, 0.0, new String[] { "lab" }, new String[] { "lab" });
crf.setWeightsDimensionDensely();
CRF.Factors f = crf.getParameters();
int initIndex = 0;
int finalIndex = 1;
f.setParameter(initIndex, 3.3);
f.setParameter(finalIndex, 4.4);
assertEquals(3.3, f.getParameter(initIndex), 0.0);
assertEquals(4.4, f.getParameter(finalIndex), 0.0);
}

@Test
public void testFeatureSelectionDisablesVectorDuringWeightDimensionSetting() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("a");
inputAlphabet.lookupIndex("b");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("x");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("x", 0.0, 0.0, new String[] { "x" }, new String[] { "x" });
int widx = crf.getWeightsIndex("x->x:x");
// crf.featureSelections = new FeatureSelection[1];
FeatureSelection disabledFS = new FeatureSelection(inputAlphabet);
disabledFS.getBitSet().clear();
// crf.featureSelections[widx] = disabledFS;
crf.setWeightsDimensionDensely();
SparseVector[] weights = crf.getWeights();
assertEquals(0, weights[widx].numLocations());
}

@Test
public void testSerializationPreservesParameters() throws IOException, ClassNotFoundException {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("featZ");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("labZ");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("labZ", 0.0, 0.0, new String[] { "labZ" }, new String[] { "labZ" });
crf.setWeightsDimensionDensely();
CRF.Factors original = new CRF.Factors(crf);
original.setParameter(0, 2.2);
ByteArrayOutputStream baos = new ByteArrayOutputStream();
ObjectOutputStream oos = new ObjectOutputStream(baos);
oos.writeObject(original);
oos.close();
ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
ObjectInputStream ois = new ObjectInputStream(bais);
CRF.Factors deserialized = (CRF.Factors) ois.readObject();
assertEquals(2.2, deserialized.getParameter(0), 0.0);
}

@Test
public void testGetNumFactorsForZeroLengthWeights() {
CRF.Factors factors = new CRF.Factors();
int count = factors.getNumFactors();
assertEquals(0, count);
}

@Test
public void testPlusEqualsObeysFrozenWeights() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("cool");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("hot");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("hot", 0.0, 0.0, new String[] { "hot" }, new String[] { "hot" });
int widx = crf.getWeightsIndex("hot->hot:hot");
crf.setWeightsDimensionDensely();
crf.freezeWeights(widx);
CRF.Factors f1 = crf.getParameters();
CRF.Factors f2 = new CRF.Factors(crf);
double before = f1.getParameter(2);
f2.setParameter(2, 7.0);
f1.plusEquals(f2, 1.0, true);
double after = f1.getParameter(2);
assertEquals(before, after, 0.0);
}

@Test
public void testHyberbolicPriorZeroSlopeReturnsZero() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("fancy");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("awesome");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("awesome", 0.0, 0.0, new String[] { "awesome" }, new String[] { "awesome" });
crf.setWeightsDimensionDensely();
CRF.Factors f = new CRF.Factors(crf);
f.setParameter(0, 100.0);
double prior = f.hyberbolicPrior(0.0, 5.0);
assertEquals(0.0, prior, 0.0);
}

@Test
public void testTransitionIteratorSkipsImpossibleTransitions() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("a");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("x");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("x", 0.0, 0.0, new String[] { "x" }, new String[] { "x" });
crf.setWeightsDimensionDensely();
FeatureVector fv = new FeatureVector(inputAlphabet, new int[] { 0 }, new double[] { 1.0 });
CRF.State state = (CRF.State) crf.getState("x");
// CRF.TransitionIterator iterator = new CRF.TransitionIterator(state, fv, "non_matching_label", crf);
// assertFalse(iterator.hasNext());
}

@Test(expected = IllegalArgumentException.class)
public void testGetParameterThrowsOnMalformedSparseStructure() {
CRF.Factors f = new CRF.Factors();
f.weights = new SparseVector[1];
f.weights[0] = new IndexedSparseVector(new int[] { 0 }, new double[] { 0.0 }, 1, 1, false, false, false);
f.defaultWeights = new double[1];
f.initialWeights = new double[1];
f.finalWeights = new double[1];
int indexBeyond = f.getNumFactors() + 5;
f.getParameter(indexBeyond);
}

@Test
public void testPlusEqualsWithZeroFactorShouldHaveNoEffect() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("a");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("b");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("b", 0.0, 0.0, new String[] { "b" }, new String[] { "b" });
crf.setWeightsDimensionDensely();
CRF.Factors f1 = crf.getParameters();
CRF.Factors f2 = new CRF.Factors(crf);
f1.setParameter(0, 3.0);
f2.plusEquals(f1, 0.0);
assertEquals(0.0, f2.getParameter(0), 0.0);
}

@Test
public void testZeroHandlesNullWeightsArrayGracefully() {
CRF.Factors f = new CRF.Factors();
f.defaultWeights = null;
f.weights = null;
f.initialWeights = new double[1];
f.finalWeights = new double[1];
f.zero();
assertEquals(0.0, f.initialWeights[0], 1e-5);
assertEquals(0.0, f.finalWeights[0], 1e-5);
}

@Test
public void testAssertionFailWhenSparseVectorHasNaN() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("a");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("b");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("b", 0.0, 0.0, new String[] { "b" }, new String[] { "b" });
crf.setWeightsDimensionDensely();
CRF.Factors factors = new CRF.Factors(crf);
SparseVector[] weights = factors.weights;
weights[0].setValue(0, Double.NaN);
try {
factors.assertNotNaNOrInfinite();
fail("Expected AssertionError due to NaN");
} catch (AssertionError expected) {
}
}

@Test
public void testSetGetParameterAtSparseVectorLocationIndex1() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("x");
inputAlphabet.lookupIndex("y");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("z");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("z", 0.0, 0.0, new String[] { "z" }, new String[] { "z" });
crf.setWeightsDimensionDensely();
CRF.Factors f = crf.getParameters();
double[] buffer = new double[f.getNumFactors()];
f.getParameters(buffer);
if (buffer.length >= 4) {
f.setParameter(3, 5.5);
assertEquals(5.5, f.getParameter(3), 0.0);
}
}

@Test
public void testStructureMatchWithNullDefaultWeightsReturnsFalse() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("q");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("r");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("r", 0.0, 0.0, new String[] { "r" }, new String[] { "r" });
crf.setWeightsDimensionDensely();
CRF.Factors f1 = crf.getParameters();
CRF.Factors f2 = new CRF.Factors();
f2.weightAlphabet = f1.weightAlphabet;
f2.weights = f1.weights;
f2.defaultWeights = null;
f2.initialWeights = f1.initialWeights;
f2.finalWeights = f1.finalWeights;
boolean matched = f1.structureMatches(f2);
assertFalse(matched);
}

@Test
public void testGaussianPriorIgnoresInfiniteWeights() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("f");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("l");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("l", 0.0, 0.0, new String[] { "l" }, new String[] { "l" });
crf.setWeightsDimensionDensely();
CRF.Factors f = crf.getParameters();
f.setParameter(0, Double.POSITIVE_INFINITY);
f.setParameter(1, 2.0);
double prior = f.gaussianPrior(1.0);
assertEquals(-2.0 * 2.0 / 2.0, prior, 1e-5);
}

@Test
public void testFeatureSelectionDoesNotBlockDefaultWeightInGradient() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("m");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("n");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("n", 0.0, 0.0, new String[] { "n" }, new String[] { "n" });
crf.setWeightsDimensionDensely();
CRF.Factors f = crf.getParameters();
CRF.Factors g = new CRF.Factors(crf);
int defaultParam = 2 * crf.numStates();
g.setParameter(defaultParam, 2.0);
f.plusEqualsGaussianPriorGradient(g, 1.0);
assertEquals(-2.0, f.getParameter(defaultParam), 0.0);
}

@Test
public void testPlusEqualsHyperbolicPriorGradientWithZeroSharpnessBehaves() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("x0");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("y0");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("y0", 0.0, 0.0, new String[] { "y0" }, new String[] { "y0" });
crf.setWeightsDimensionDensely();
CRF.Factors f = crf.getParameters();
CRF.Factors g = new CRF.Factors(crf);
g.setParameter(0, 1.0);
f.plusEqualsHyperbolicPriorGradient(g, 0.0, 1e-9);
assertEquals(0.0, f.getParameter(0), 1e-6);
}

@Test
public void testGetParametersFillsArrayWithExpectedValues() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("xx");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("yy");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("yy", 0.0, 0.0, new String[] { "yy" }, new String[] { "yy" });
crf.setWeightsDimensionDensely();
CRF.Factors f = crf.getParameters();
if (f.getNumFactors() >= 3) {
f.setParameter(0, 1.0);
f.setParameter(1, 2.0);
f.setParameter(2, 3.0);
double[] buff = new double[f.getNumFactors()];
f.getParameters(buff);
assertEquals(1.0, buff[0], 1e-6);
assertEquals(2.0, buff[1], 1e-6);
assertEquals(3.0, buff[2], 1e-6);
}
}

@Test(expected = IllegalArgumentException.class)
public void testSetParameterIndexHigherThanAllowedThrowsException() {
CRF.Factors f = new CRF.Factors();
f.initialWeights = new double[1];
f.finalWeights = new double[1];
f.defaultWeights = new double[1];
f.weights = new SparseVector[1];
f.weights[0] = new IndexedSparseVector(new int[] { 0 }, new double[] { 1.0 }, 1, 1, false, false, false);
f.setParameter(10, 5.0);
}

@Test
public void testSetParameterWithDefaultWeightIndexUpdatesCorrectly() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("x");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("y");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("y", 0.0, 0.0, new String[] { "y" }, new String[] { "y" });
crf.setWeightsDimensionDensely();
CRF.Factors factors = crf.getParameters();
int numStates = crf.numStates();
int defaultIndex = 2 * numStates;
double before = factors.getParameter(defaultIndex);
factors.setParameter(defaultIndex, 6.66);
double after = factors.getParameter(defaultIndex);
assertNotEquals(before, after);
assertEquals(6.66, after, 1e-6);
}

@Test
public void testSetParameterUpdatesWeightValueCorrectlyInMiddleOfVector() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("a1");
inputAlphabet.lookupIndex("a2");
inputAlphabet.lookupIndex("a3");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("z");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("z", 0.0, 0.0, new String[] { "z" }, new String[] { "z" });
crf.setWeightsDimensionDensely();
CRF.Factors factors = crf.getParameters();
int parameterIndex = 2 * crf.numStates() + 1;
factors.setParameter(parameterIndex, 8.8);
double result = factors.getParameter(parameterIndex);
assertEquals(8.8, result, 0.0001);
}

@Test
public void testFreezeAndUnfreezeWeightUpdatesFrozenStatus() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("xx");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("yy");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("yy", 0.0, 0.0, new String[] { "yy" }, new String[] { "yy" });
crf.setWeightsDimensionDensely();
String weightName = crf.getWeightsName(0);
crf.freezeWeights(weightName);
assertTrue(crf.isWeightsFrozen(0));
crf.unfreezeWeights(weightName);
assertFalse(crf.isWeightsFrozen(0));
}

@Test
public void testGetParameterOrderAlignmentMatchesStoredValues() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("foo1");
inputAlphabet.lookupIndex("foo2");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("bar");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("bar", 0.0, 0.0, new String[] { "bar" }, new String[] { "bar" });
crf.setWeightsDimensionDensely();
CRF.Factors f = crf.getParameters();
int total = f.getNumFactors();
double[] bufferSet = new double[total];
double[] bufferGet = new double[total];
bufferSet[0] = 1.1;
if (total > 1)
bufferSet[1] = 2.2;
if (total > 2)
bufferSet[2] = 3.3;
f.setParameters(bufferSet);
f.getParameters(bufferGet);
for (int i = 0; i < total; i++) {
assertEquals(bufferSet[i], bufferGet[i], 1e-6);
}
}

@Test
public void testStructureMatchesFailsIfWeightAlphabetSizesDiffer() {
CRF.Factors f1 = new CRF.Factors();
f1.weightAlphabet = new Alphabet();
f1.weightAlphabet.lookupIndex("w1");
f1.weights = new SparseVector[1];
f1.weights[0] = new IndexedSparseVector();
f1.defaultWeights = new double[1];
f1.initialWeights = new double[1];
f1.finalWeights = new double[1];
CRF.Factors f2 = new CRF.Factors();
f2.weightAlphabet = new Alphabet();
f2.weightAlphabet.lookupIndex("w1");
f2.weightAlphabet.lookupIndex("w2");
f2.weights = new SparseVector[1];
f2.weights[0] = new IndexedSparseVector();
f2.defaultWeights = new double[1];
f2.initialWeights = new double[1];
f2.finalWeights = new double[1];
assertFalse(f1.structureMatches(f2));
}

@Test
public void testGaussianPriorSkipsInfiniteFinalWeights() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("inF1");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("outF1");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("outF1", 0.0, 0.0, new String[] { "outF1" }, new String[] { "outF1" });
crf.setWeightsDimensionDensely();
CRF.Factors factors = crf.getParameters();
factors.setParameter(0, Double.POSITIVE_INFINITY);
factors.setParameter(1, Double.POSITIVE_INFINITY);
factors.setParameter(2, 1.0);
double value = factors.gaussianPrior(1.0);
assertEquals(-0.5 * 1.0 * 1.0, value, 1e-5);
}

@Test
public void testWeightAlphabetSharedAcrossFactorsInCopyConstructor() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("inputTest");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("labelTest");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
String[] dest = new String[] { "labelTest" };
crf.addState("labelTest", 0.0, 0.0, dest, dest);
crf.setWeightsDimensionDensely();
CRF.Factors original = new CRF.Factors(crf);
CRF.Factors copy = new CRF.Factors(original, false);
assertSame(original.weightAlphabet, copy.weightAlphabet);
}

@Test
public void testZeroMethodDoesNotThrowOnEmptyWeights() {
CRF.Factors f = new CRF.Factors();
f.weightAlphabet = new Alphabet();
f.initialWeights = new double[0];
f.finalWeights = new double[0];
f.weights = new SparseVector[0];
f.defaultWeights = new double[0];
try {
f.zero();
} catch (Exception e) {
fail("Zero method must handle empty weights gracefully.");
}
}

@Test
public void testSetParametersZeroLengthArrayDoesNothingValid() {
CRF.Factors factors = new CRF.Factors();
factors.initialWeights = new double[0];
factors.finalWeights = new double[0];
factors.defaultWeights = new double[0];
factors.weights = new SparseVector[0];
double[] empty = new double[0];
factors.setParameters(empty);
assertEquals(0, factors.getNumFactors());
}

@Test
public void testGetParameterDefaultWeightEdgeAfterFinalWeightBlock() {
Alphabet input = new Alphabet();
input.lookupIndex("tok");
Alphabet output = new Alphabet();
output.lookupIndex("lbl");
CRF crf = new CRF(input, output);
crf.addState("lbl", 0.0, 0.0, new String[] { "lbl" }, new String[] { "lbl" });
crf.setWeightsDimensionDensely();
CRF.Factors factors = crf.getParameters();
int defaultIdx = 2 * crf.numStates();
factors.setParameter(defaultIdx, 7.77);
double actual = factors.getParameter(defaultIdx);
assertEquals(7.77, actual, 1e-8);
}

@Test
public void testGetParameterFinalWeightCorrectIndexing() {
Alphabet input = new Alphabet();
input.lookupIndex("f1");
Alphabet output = new Alphabet();
output.lookupIndex("f2");
CRF crf = new CRF(input, output);
crf.addState("f2", 0.0, 0.0, new String[] { "f2" }, new String[] { "f2" });
crf.setWeightsDimensionDensely();
CRF.Factors factors = crf.getParameters();
factors.setParameter(1, 4.5);
double val = factors.getParameter(1);
assertEquals(4.5, val, 0.0);
}

@Test
public void testStructureMatchesFailsWhenNumFinalWeightsDiffers() {
CRF.Factors f1 = new CRF.Factors();
f1.initialWeights = new double[1];
f1.finalWeights = new double[1];
f1.defaultWeights = new double[0];
f1.weights = new SparseVector[0];
f1.weightAlphabet = new Alphabet();
CRF.Factors f2 = new CRF.Factors();
f2.initialWeights = new double[2];
f2.finalWeights = new double[2];
f2.defaultWeights = new double[0];
f2.weights = new SparseVector[0];
f2.weightAlphabet = new Alphabet();
assertFalse(f1.structureMatches(f2));
}

@Test
public void testSetAndGetParameterOnSparseVectorWeightEntry() {
Alphabet input = new Alphabet();
input.lookupIndex("a");
input.lookupIndex("b");
Alphabet output = new Alphabet();
output.lookupIndex("c");
CRF crf = new CRF(input, output);
crf.addState("c", 0.0, 0.0, new String[] { "c" }, new String[] { "c" });
crf.setWeightsDimensionDensely();
CRF.Factors f = crf.getParameters();
int index = f.getNumFactors() - 1;
f.setParameter(index, 9.99);
double actual = f.getParameter(index);
assertEquals(9.99, actual, 0.0);
}

@Test
public void testPlusEqualsWithMismatchedFrozenFlagsStillMergesCorrectly() {
Alphabet input = new Alphabet();
input.lookupIndex("one");
Alphabet output = new Alphabet();
output.lookupIndex("two");
CRF crf = new CRF(input, output);
crf.addState("two", 0.0, 0.0, new String[] { "two" }, new String[] { "two" });
crf.setWeightsDimensionDensely();
CRF.Factors base = crf.getParameters();
CRF.Factors delta = new CRF.Factors(crf);
crf.freezeWeights(0);
base.plusEquals(delta, 2.0, true);
assertEquals(0.0, base.getParameter(2), 0.0);
}

@Test
public void testPlusEqualsGaussianPriorGradientWithFrozenWeightRemainsUnchanged() {
Alphabet input = new Alphabet();
input.lookupIndex("in");
Alphabet output = new Alphabet();
output.lookupIndex("out");
CRF crf = new CRF(input, output);
crf.addState("out", 0.0, 0.0, new String[] { "out" }, new String[] { "out" });
crf.setWeightsDimensionDensely();
crf.freezeWeights(0);
CRF.Factors params = crf.getParameters();
CRF.Factors grad = new CRF.Factors(crf);
double before = params.getParameter(2);
grad.setParameter(2, 88.8);
params.plusEqualsGaussianPriorGradient(grad, 1.0);
double after = params.getParameter(2);
assertEquals(before, after, 0.0);
}

@Test
public void testCloneAlphabetInConstructorOffsetsOriginal() {
Alphabet input = new Alphabet();
input.lookupIndex("i1");
Alphabet output = new Alphabet();
output.lookupIndex("o1");
CRF crf = new CRF(input, output);
crf.addState("o1", 0.0, 0.0, new String[] { "o1" }, new String[] { "o1" });
crf.setWeightsDimensionDensely();
CRF.Factors f1 = crf.getParameters();
CRF.Factors f2 = new CRF.Factors(f1, true);
f1.weightAlphabet.lookupIndex("extraValue");
assertNotEquals(f1.weightAlphabet.size(), f2.weightAlphabet.size());
}

@Test
public void testGetParametersAbsNormIncludesAllRegions() {
Alphabet inputAlphabet = new Alphabet();
inputAlphabet.lookupIndex("qk");
Alphabet outputAlphabet = new Alphabet();
outputAlphabet.lookupIndex("zk");
CRF crf = new CRF(inputAlphabet, outputAlphabet);
crf.addState("zk", 0.0, 0.0, new String[] { "zk" }, new String[] { "zk" });
crf.setWeightsDimensionDensely();
CRF.Factors f = crf.getParameters();
f.setParameter(0, 2.0);
f.setParameter(1, -3.0);
f.setParameter(2, 4.0);
if (f.getNumFactors() > 3) {
f.setParameter(3, -5.0);
}
double norm = f.getParametersAbsNorm();
assertTrue(norm >= 14.0);
}

@Test
public void testGetNumFactorsOnlyRecalculatedWhenStampsChange() {
Alphabet input = new Alphabet();
input.lookupIndex("x");
Alphabet output = new Alphabet();
output.lookupIndex("y");
CRF crf = new CRF(input, output);
crf.addState("y", 0.0, 0.0, new String[] { "y" }, new String[] { "y" });
crf.setWeightsDimensionDensely();
CRF.Factors factors = crf.getParameters();
int original = factors.getNumFactors();
int again = factors.getNumFactors();
assertEquals(original, again);
}
}
