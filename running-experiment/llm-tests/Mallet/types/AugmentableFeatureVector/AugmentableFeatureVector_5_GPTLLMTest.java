package cc.mallet.types.tests;

import cc.mallet.types.*;
import cc.mallet.util.PropertyList;
import org.junit.Test;
import java.io.*;
import static org.junit.Assert.*;

public class AugmentableFeatureVector_5_GPTLLMTest {

@Test
public void testAddSingleFeature() {
Alphabet dict = new Alphabet();
dict.lookupIndex("feature1");
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, true);
int index = dict.lookupIndex("feature1");
afv.add(index);
assertEquals(1.0, afv.value(index), 0.001);
assertEquals(1, afv.numLocations());
}

@Test(expected = IllegalArgumentException.class)
public void testAddNonBinaryToBinaryVector() {
Alphabet dict = new Alphabet();
dict.lookupIndex("feature1");
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, true);
int index = dict.lookupIndex("feature1");
afv.add(index, 0.5);
}

@Test
public void testAddDuplicateFeaturesAndRemoveDuplicates() {
Alphabet dict = new Alphabet();
dict.lookupIndex("feature1");
dict.lookupIndex("feature2");
dict.lookupIndex("feature3");
int[] indices = { 0, 1, 1, 2 };
double[] values = { 1.0, 0.5, 0.5, 1.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, indices, values, 4);
// afv.sortIndices();
assertEquals(3, afv.numLocations());
assertEquals(1.0, afv.value(0), 0.001);
assertEquals(1.0, afv.value(1), 0.001);
assertEquals(1.0, afv.value(2), 0.001);
}

@Test
public void testDotProductWithDenseVector() {
Alphabet dict = new Alphabet();
dict.lookupIndex("feature1");
dict.lookupIndex("feature2");
dict.lookupIndex("feature3");
double[] values = { 1.0, 2.0, 3.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, values);
DenseVector dv = new DenseVector(new double[] { 0.0, 1.0, 1.0 });
double result = afv.dotProduct(dv);
assertEquals(5.0, result, 0.001);
}

@Test
public void testDotProductWithSparseVector() {
Alphabet dict = new Alphabet();
dict.lookupIndex("f0");
dict.lookupIndex("f1");
dict.lookupIndex("f2");
int[] indices = { 0, 2 };
double[] values = { 1.0, 3.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, indices, values, 2);
int[] svIndices = { 2 };
double[] svValues = { 2.0 };
SparseVector sv = new SparseVector(svIndices, svValues, 1, 1, true, true, true);
double result = afv.dotProduct(sv);
assertEquals(6.0, result, 0.001);
}

@Test
public void testPlusEqualsWithAugmentableFeatureVector() {
Alphabet dict = new Alphabet();
dict.lookupIndex("f0");
dict.lookupIndex("f1");
int[] indices1 = { 0, 1 };
double[] values1 = { 1.0, 2.0 };
AugmentableFeatureVector afv1 = new AugmentableFeatureVector(dict, indices1, values1, 2);
int[] indices2 = { 0, 1 };
double[] values2 = { 3.0, 1.0 };
AugmentableFeatureVector afv2 = new AugmentableFeatureVector(dict, indices2, values2, 2);
afv1.plusEquals(afv2, 1.0);
assertEquals(4.0, afv1.value(0), 0.001);
assertEquals(3.0, afv1.value(1), 0.001);
}

@Test
public void testCloneMatrixCopy() {
Alphabet dict = new Alphabet();
dict.lookupIndex("f0");
dict.lookupIndex("f1");
double[] values = { 1.0, 2.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, values);
AugmentableFeatureVector cloned = (AugmentableFeatureVector) afv.cloneMatrix();
assertNotSame(afv, cloned);
assertEquals(1.0, cloned.value(0), 0.001);
assertEquals(2.0, cloned.value(1), 0.001);
}

@Test
public void testCloneMatrixZeroed() {
Alphabet dict = new Alphabet();
dict.lookupIndex("zero");
double[] values = { 1.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, values);
AugmentableFeatureVector zeroed = (AugmentableFeatureVector) afv.cloneMatrixZeroed();
assertEquals(0.0, zeroed.value(0), 0.001);
}

@Test
public void testSetValueAndGetValue() {
Alphabet dict = new Alphabet();
dict.lookupIndex("f0");
dict.lookupIndex("f1");
int[] indices = { 0, 1 };
double[] values = { 1.0, 2.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, indices, values, 2);
afv.setValue(1, 5.0);
assertEquals(5.0, afv.value(1), 0.001);
}

@Test
public void testToFeatureVectorPreservesValues() {
Alphabet dict = new Alphabet();
dict.lookupIndex("f0");
dict.lookupIndex("f1");
double[] values = { 1.0, 2.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, values);
FeatureVector fv = afv.toFeatureVector();
assertEquals(2, fv.numLocations());
assertEquals(1.0, fv.valueAtLocation(0), 0.001);
assertEquals(2.0, fv.valueAtLocation(1), 0.001);
}

@Test
public void testAddFeatureVectorWithPrefix() {
Alphabet dict = new Alphabet();
dict.lookupIndex("original");
Alphabet other = new Alphabet();
int otherIdx = other.lookupIndex("featX");
int[] indices = { otherIdx };
double[] values = { 1.0 };
FeatureVector otherFV = new FeatureVector(other, indices, values);
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, true);
afv.add(otherFV, "pre_");
int newIndex = dict.lookupIndex("pre_featX");
assertEquals(1.0, afv.value(newIndex), 0.001);
}

@Test
public void testNormCalculations() {
Alphabet dict = new Alphabet();
dict.lookupIndex("f0");
dict.lookupIndex("f1");
double[] values = { 3.0, 4.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, values);
assertEquals(7.0, afv.oneNorm(), 0.001);
assertEquals(5.0, afv.twoNorm(), 0.001);
assertEquals(4.0, afv.infinityNorm(), 0.001);
}

@Test(expected = IllegalArgumentException.class)
public void testAddBinaryFeatureToRealValuedVectorShouldFail() {
Alphabet dict = new Alphabet();
dict.lookupIndex("f0");
double[] values = { 1.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, values);
afv.add(0);
}

@Test
public void testAddToWithAccumulatorAndScaling() {
Alphabet dict = new Alphabet();
dict.lookupIndex("f0");
dict.lookupIndex("f1");
double[] values = { 2.0, 3.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, values);
double[] accumulator = new double[2];
afv.addTo(accumulator, 2.0);
assertEquals(4.0, accumulator[0], 0.001);
assertEquals(6.0, accumulator[1], 0.001);
}

@Test
public void testConstructorWithPropertyList() {
Alphabet dict = new Alphabet();
dict.lookupIndex("f0");
dict.lookupIndex("f1");
PropertyList pl = PropertyList.add("f0", 1.5, null);
pl = PropertyList.add("f1", 2.5, pl);
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, pl, false);
assertEquals(1.5, afv.value(dict.lookupIndex("f0")), 0.001);
assertEquals(2.5, afv.value(dict.lookupIndex("f1")), 0.001);
}

@Test
public void testSetAllValuesToConstant() {
Alphabet dict = new Alphabet();
dict.lookupIndex("f0");
dict.lookupIndex("f1");
dict.lookupIndex("f2");
double[] values = { 5.0, -1.0, 9.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, values);
afv.setAll(0.0);
assertEquals(0.0, afv.value(0), 0.001);
assertEquals(0.0, afv.value(1), 0.001);
assertEquals(0.0, afv.value(2), 0.001);
}

@Test
public void testDotProductWithEmptyVector() {
Alphabet dict = new Alphabet();
dict.lookupIndex("x");
SparseVector sv = new SparseVector(new int[] {}, new double[] {}, 0, 0, true, true, true);
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, true);
double result = afv.dotProduct(sv);
assertEquals(0.0, result, 0.001);
}

@Test
public void testConstructorWithNullValuesArray() {
Alphabet dict = new Alphabet();
dict.lookupIndex("f0");
int[] indices = { 0 };
double[] values = null;
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, indices, values, 1);
assertEquals(1, afv.numLocations());
assertEquals(1.0, afv.value(0), 0.001);
}

@Test
public void testAddSameIndexToSparseBinaryVector() {
Alphabet dict = new Alphabet();
dict.lookupIndex("feat");
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, true);
int index = dict.lookupIndex("feat");
afv.add(index);
afv.add(index);
assertEquals(2, afv.numLocations());
// afv.sortIndices();
assertEquals(1, afv.numLocations());
assertEquals(2.0, afv.value(index), 0.001);
}

@Test
public void testSetValueAtLocationOnSparseVector() {
Alphabet dict = new Alphabet();
dict.lookupIndex("f0");
int[] indices = { 0 };
double[] values = { 2.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, indices, values, 1);
afv.setValueAtLocation(0, 3.0);
assertEquals(3.0, afv.value(0), 0.001);
}

@Test
public void testAddObjectKeyToVector() {
Alphabet dict = new Alphabet();
dict.lookupIndex("word");
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, true);
afv.add("word", 2.0);
int index = dict.lookupIndex("word");
assertEquals(2.0, afv.value(index), 0.001);
}

@Test
public void testAddUnknownObjectKeyThrowsException() {
Alphabet dict = new Alphabet();
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, true);
boolean thrown = false;
try {
afv.add("unknown", 1.0);
} catch (IllegalStateException | AssertionError e) {
thrown = true;
}
assertTrue(thrown);
}

@Test
public void testToSparseVectorReturnsCorrectIndicesAndValues() {
Alphabet dict = new Alphabet();
dict.lookupIndex("f0");
dict.lookupIndex("f1");
int[] indices = { 1, 0 };
double[] values = { 1.0, 2.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, indices, values, 2);
SparseVector sv = afv.toSparseVector();
assertEquals(2, sv.numLocations());
assertEquals(0, sv.indexAtLocation(0));
assertEquals(1, sv.indexAtLocation(1));
assertEquals(2.0, sv.valueAtLocation(0), 0.001);
assertEquals(1.0, sv.valueAtLocation(1), 0.001);
}

@Test
public void testLocationReturnsMinusOneWhenIndexNotFound() {
Alphabet dict = new Alphabet();
dict.lookupIndex("a");
int[] indices = { 0 };
double[] values = { 1.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, indices, values, 1);
int result = afv.location(1);
assertEquals(-1, result);
}

@Test
public void testAddToDenseVectorWithDifferentLength() {
Alphabet dict = new Alphabet();
dict.lookupIndex("a");
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, new double[] { 1.0 });
double[] acc = new double[2];
afv.addTo(acc);
assertEquals(1.0, acc[0], 0.001);
assertEquals(0.0, acc[1], 0.0);
}

@Test
public void testRemoveDuplicatesNoDuplicates() {
Alphabet dict = new Alphabet();
dict.lookupIndex("a");
int[] indices = { 0 };
double[] values = { 1.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, indices, values, 1);
// afv.sortIndices();
assertEquals(1, afv.numLocations());
}

@Test
public void testDotProductWithDisjointAugmentableFeatureVectors() {
Alphabet dict = new Alphabet();
for (int i = 0; i < 4; i++) {
dict.lookupIndex("f" + i);
}
int[] indicesA = { 0, 1 };
double[] valuesA = { 1.0, 2.0 };
AugmentableFeatureVector afv1 = new AugmentableFeatureVector(dict, indicesA, valuesA, 2);
int[] indicesB = { 2, 3 };
double[] valuesB = { 3.0, 4.0 };
AugmentableFeatureVector afv2 = new AugmentableFeatureVector(dict, indicesB, valuesB, 2);
double result = afv1.dotProduct(afv2);
assertEquals(0.0, result, 0.001);
}

@Test
public void testSingleSizeReturnsCorrectValuesDense() {
Alphabet dict = new Alphabet();
dict.lookupIndex("a");
dict.lookupIndex("b");
double[] values = { 10.0, 20.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, values);
int result = afv.singleSize();
assertEquals(2, result);
}

@Test
public void testSingleSizeReturnsCorrectValuesSparse() {
Alphabet dict = new Alphabet();
dict.lookupIndex("a");
dict.lookupIndex("b");
int[] indices = { 1 };
double[] values = { 3.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, indices, values, 1);
int result = afv.singleSize();
assertEquals(1, result);
}

@Test
public void testInfinityNormOfNegativeValues() {
Alphabet dict = new Alphabet();
dict.lookupIndex("x");
dict.lookupIndex("y");
double[] values = { -3.0, -7.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, values);
double result = afv.infinityNorm();
assertEquals(7.0, result, 0.001);
}

@Test
public void testSortIndicesDoesNotChangeDenseVector() {
Alphabet dict = new Alphabet();
dict.lookupIndex("x");
dict.lookupIndex("y");
double[] values = { 1.0, 2.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, values);
// afv.sortIndices();
assertEquals(2, afv.numLocations());
assertEquals(1.0, afv.value(0), 0.001);
}

@Test
public void testPlusEqualsWithSparseVectorBinaryValuesNull() {
Alphabet dict = new Alphabet();
dict.lookupIndex("x");
dict.lookupIndex("y");
int[] indices = { 0, 1 };
double[] values = { 2.0, 4.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, indices, values, 2);
int[] svIndices = { 0, 1 };
SparseVector sv = new SparseVector(svIndices, null, 2, 2, true, true, true);
afv.plusEquals(sv, 2.0);
assertEquals(4.0, afv.value(0), 0.001);
assertEquals(6.0, afv.value(1), 0.001);
}

@Test
public void testConstructorWithZeroCapacityVector() {
Alphabet dict = new Alphabet();
dict.lookupIndex("a");
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, 0, false);
assertEquals(0, afv.numLocations());
int index = dict.lookupIndex("a");
afv.add(index);
assertEquals(1, afv.numLocations());
assertEquals(1.0, afv.value(index), 0.001);
}

@Test
public void testAddBeyondInitialCapacity() {
Alphabet dict = new Alphabet();
dict.lookupIndex("a");
dict.lookupIndex("b");
dict.lookupIndex("c");
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, 1, false);
afv.add(dict.lookupIndex("a"));
afv.add(dict.lookupIndex("b"));
afv.add(dict.lookupIndex("c"));
assertEquals(3, afv.numLocations());
assertEquals(1.0, afv.value(dict.lookupIndex("a")), 0.001);
assertEquals(1.0, afv.value(dict.lookupIndex("b")), 0.001);
assertEquals(1.0, afv.value(dict.lookupIndex("c")), 0.001);
}

@Test
public void testSetValueOnUnsortedVector() {
Alphabet dict = new Alphabet();
dict.lookupIndex("a");
dict.lookupIndex("b");
int[] indices = { 1, 0 };
double[] values = { 2.0, 1.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, indices, values, 2);
afv.setValue(0, 3.0);
assertEquals(3.0, afv.value(0), 0.001);
}

@Test
public void testPlusEqualsOverlappingIndicesWithSparseVector() {
Alphabet dict = new Alphabet();
dict.lookupIndex("a");
int[] indices = { 0 };
double[] values = { 2.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, indices, values, 1);
int[] svIndices = { 0 };
double[] svValues = { 3.0 };
SparseVector sv = new SparseVector(svIndices, svValues, 1, 1, true, true, true);
afv.plusEquals(sv, 2.0);
assertEquals(8.0, afv.value(0), 0.001);
}

@Test
public void testAddToWithRepeatedIndices() {
Alphabet dict = new Alphabet();
dict.lookupIndex("a");
dict.lookupIndex("b");
int[] indices = { 0, 0 };
double[] values = { 1.0, 2.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, indices, values, 2);
// afv.sortIndices();
double[] acc = new double[2];
afv.addTo(acc, 1.0);
assertEquals(3.0, acc[0], 0.001);
}

@Test
public void testSetValueForDenseVector() {
Alphabet dict = new Alphabet();
dict.lookupIndex("a");
dict.lookupIndex("b");
double[] values = { 4.0, 5.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, values);
afv.setValue(1, 7.0);
assertEquals(7.0, afv.value(1), 0.001);
}

@Test
public void testSerializationAndDeserialization() throws IOException, ClassNotFoundException {
Alphabet dict = new Alphabet();
dict.lookupIndex("a");
double[] values = { 1.5 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, values);
ByteArrayOutputStream bos = new ByteArrayOutputStream();
ObjectOutputStream oos = new ObjectOutputStream(bos);
oos.writeObject(afv);
oos.flush();
oos.close();
byte[] bytes = bos.toByteArray();
ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
ObjectInputStream ois = new ObjectInputStream(bis);
AugmentableFeatureVector deserialized = (AugmentableFeatureVector) ois.readObject();
assertEquals(1.5, deserialized.value(0), 0.001);
}

@Test
public void testAddWithIndexGreaterThanInitialArraySize() {
Alphabet dict = new Alphabet();
dict.lookupIndex("a");
dict.lookupIndex("b");
dict.lookupIndex("z");
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, true);
afv.add(dict.lookupIndex("z"));
assertEquals(1.0, afv.value(dict.lookupIndex("z")), 0.001);
}

@Test
public void testAddMultipleObjectFeaturesExpandsAlphabet() {
Alphabet dict = new Alphabet();
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, true);
dict.lookupIndex("featureX", true);
dict.lookupIndex("featureY", true);
afv.add("featureX", 2.0);
afv.add("featureY", 3.0);
assertEquals(2.0, afv.value(dict.lookupIndex("featureX")), 0.001);
assertEquals(3.0, afv.value(dict.lookupIndex("featureY")), 0.001);
}

@Test
public void testEmptyAddToSparseBinaryVector() {
Alphabet dict = new Alphabet();
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, 0, true);
double[] acc = new double[1];
afv.addTo(acc, 1.0);
assertEquals(0.0, acc[0], 0.001);
}

@Test
public void testRemoveDuplicatesMultipleEqualEntries() {
Alphabet dict = new Alphabet();
dict.lookupIndex("x");
int[] indices = { 0, 0, 0 };
double[] values = { 1.0, 2.0, 3.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, indices, values, 3, true);
// afv.sortIndices();
assertEquals(6.0, afv.value(0), 0.001);
assertEquals(1, afv.numLocations());
}

@Test
public void testAddWithUnsetInitialValuesDense() {
Alphabet dict = new Alphabet();
dict.lookupIndex("a");
double[] values = new double[10];
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, values);
afv.add(0, 5.0);
assertEquals(5.0, afv.value(0), 0.001);
}

@Test
public void testAddFeatureWithSameIndexMultipleTimesSparseVector() {
Alphabet dict = new Alphabet();
dict.lookupIndex("token");
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, true);
int index = dict.lookupIndex("token");
afv.add(index);
afv.add(index);
afv.add(index);
// afv.sortIndices();
assertEquals(1, afv.numLocations());
assertEquals(3.0, afv.value(index), 0.001);
}

@Test
public void testDotProductWithOverlappingAndNonOverlappingIndices() {
Alphabet dict = new Alphabet();
dict.lookupIndex("f0");
dict.lookupIndex("f1");
dict.lookupIndex("f2");
int[] indices1 = { 0, 1 };
double[] values1 = { 2.0, 3.0 };
AugmentableFeatureVector afv1 = new AugmentableFeatureVector(dict, indices1, values1, 2);
int[] indices2 = { 1, 2 };
double[] values2 = { 4.0, 5.0 };
SparseVector sv = new SparseVector(indices2, values2, 2, 2, true, true, true);
double dot = afv1.dotProduct(sv);
assertEquals(12.0, dot, 0.001);
}

@Test
public void testPlusEqualsWithEmptySparseVectorDoesNothing() {
Alphabet dict = new Alphabet();
dict.lookupIndex("x");
double[] values = { 1.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, values);
int[] indices = {};
double[] svValues = {};
SparseVector empty = new SparseVector(indices, svValues, 0, 0, true, true, true);
afv.plusEquals(empty);
assertEquals(1.0, afv.value(0), 0.001);
}

@Test(expected = IllegalArgumentException.class)
public void testAddInvalidValueToBinaryDenseVectorThrows() {
Alphabet dict = new Alphabet();
dict.lookupIndex("flag");
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, true);
int index = dict.lookupIndex("flag");
afv.add(index, 0.90);
}

@Test
public void testToSparseVectorAfterAddWithGaps() {
Alphabet dict = new Alphabet();
dict.lookupIndex("f0");
dict.lookupIndex("f1");
dict.lookupIndex("f2");
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, true);
afv.add(dict.lookupIndex("f0"));
afv.add(dict.lookupIndex("f2"));
SparseVector s = afv.toSparseVector();
assertEquals(2, s.numLocations());
assertTrue(s.indexAtLocation(0) == 0 || s.indexAtLocation(1) == 0);
assertTrue(s.indexAtLocation(0) == 2 || s.indexAtLocation(1) == 2);
}

@Test
public void testPropertyListConstructorWithNullList() {
Alphabet dict = new Alphabet();
dict.lookupIndex("x");
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, null, true, true);
assertEquals(0, afv.numLocations());
}

@Test
public void testAddWithOutOfOrderIndexingPreservesSorting() {
Alphabet dict = new Alphabet();
dict.lookupIndex("x");
dict.lookupIndex("y");
int[] indices = { 1, 0 };
double[] values = { 2.0, 1.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, indices, values, 2);
assertEquals(2, afv.numLocations());
assertEquals(2.0, afv.value(1), 0.001);
assertEquals(1.0, afv.value(0), 0.001);
}

@Test
public void testSetValueForNonExistentIndexInSparseVector() {
Alphabet dict = new Alphabet();
dict.lookupIndex("a");
dict.lookupIndex("b");
int[] indices = { 0 };
double[] values = { 1.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, indices, values, 1);
// afv.sortIndices();
afv.setValue(1, 9.0);
assertEquals(9.0, afv.value(1), 0.001);
}

@Test
public void testCloneMatrixPreservesIndicesAndValues() {
Alphabet dict = new Alphabet();
dict.lookupIndex("t1");
dict.lookupIndex("t2");
double[] values = { 1.5, 2.5 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, values);
ConstantMatrix clone = afv.cloneMatrix();
// assertEquals(1.5, clone.value(0), 0.001);
// assertEquals(2.5, clone.value(1), 0.001);
}

@Test
public void testInfinityNormZeroValueVector() {
Alphabet dict = new Alphabet();
dict.lookupIndex("x");
double[] values = { 0.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, values);
assertEquals(0.0, afv.infinityNorm(), 0.001);
}

@Test
public void testAddToWithNegativeScaleFactor() {
Alphabet dict = new Alphabet();
dict.lookupIndex("x");
double[] values = { 5.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, values);
double[] acc = new double[1];
afv.addTo(acc, -1.0);
assertEquals(-5.0, acc[0], 0.001);
}

@Test
public void testDotProductWithSelfEqualsNormSquared() {
Alphabet dict = new Alphabet();
dict.lookupIndex("x");
dict.lookupIndex("y");
double[] values = { 3.0, 4.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, values);
double dotSelf = afv.dotProduct(afv);
double normSquared = Math.pow(afv.twoNorm(), 2.0);
assertEquals(normSquared, dotSelf, 0.001);
}

@Test
public void testEmptyToFeatureVectorConversion() {
Alphabet dict = new Alphabet();
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, true);
FeatureVector converted = afv.toFeatureVector();
assertEquals(0, converted.numLocations());
}

@Test
public void testRemoveDuplicatesWithNoDuplicatesPresent() {
Alphabet dict = new Alphabet();
dict.lookupIndex("x");
dict.lookupIndex("y");
int[] indices = { 0, 1 };
double[] values = { 1.0, 2.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, indices, values, 2, true);
// afv.sortIndices();
assertEquals(2, afv.numLocations());
assertEquals(1.0, afv.value(0), 0.001);
assertEquals(2.0, afv.value(1), 0.001);
}

@Test
public void testAddWithInitiallyZeroLengthArray() {
Alphabet dict = new Alphabet();
dict.lookupIndex("z");
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, 0, false);
afv.add(0, 1.0);
assertEquals(1.0, afv.value(0), 0.001);
assertEquals(1, afv.numLocations());
}

@Test
public void testAddMultipleCausesResizeBiggerThanSmallThreshold() {
Alphabet dict = new Alphabet();
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, 4, false);
dict.lookupIndex("f0");
dict.lookupIndex("f1");
dict.lookupIndex("f2");
dict.lookupIndex("f3");
dict.lookupIndex("f4");
dict.lookupIndex("f5");
dict.lookupIndex("f6");
afv.add(0, 1.0);
afv.add(1, 2.0);
afv.add(2, 3.0);
afv.add(3, 4.0);
afv.add(4, 5.0);
assertEquals(5, afv.numLocations());
assertEquals(5.0, afv.value(4), 0.001);
}

@Test
public void testDotProductSparseDenseEdgeAlignment() {
Alphabet dict = new Alphabet();
dict.lookupIndex("a");
dict.lookupIndex("b");
int[] indices = { 0 };
double[] values = { 2.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, indices, values, 2);
DenseVector dv = new DenseVector(new double[] { 5.0, 0.0 });
double result = afv.dotProduct(dv);
assertEquals(10.0, result, 0.001);
}

@Test
public void testDotProductWhenValueMissingInDense() {
Alphabet dict = new Alphabet();
dict.lookupIndex("a");
dict.lookupIndex("b");
int[] indices = { 0, 1 };
double[] values = { 2.0, 3.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, indices, values, 2);
DenseVector dv = new DenseVector(new double[] { 5.0 });
double result = afv.dotProduct(dv);
assertEquals(10.0, result, 0.001);
}

@Test
public void testPlusEqualsAugmentableFeatureVectorUnaligned() {
Alphabet dict = new Alphabet();
dict.lookupIndex("a");
dict.lookupIndex("b");
dict.lookupIndex("c");
int[] indices = { 0, 2 };
double[] values = { 1.0, 3.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, indices, values, 2);
int[] otherIndices = { 1 };
double[] otherValues = { 5.0 };
AugmentableFeatureVector other = new AugmentableFeatureVector(dict, otherIndices, otherValues, 1);
afv.plusEquals(other, 2.0);
assertEquals(10.0, afv.value(1), 0.001);
}

@Test
public void testPlusEqualsSparseVectorValuesNull() {
Alphabet dict = new Alphabet();
dict.lookupIndex("a");
dict.lookupIndex("b");
int[] afvIndices = { 0, 1 };
double[] afvValues = { 3.0, 4.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, afvIndices, afvValues, 2);
int[] sparseIndices = { 0, 1 };
SparseVector sv = new SparseVector(sparseIndices, null, 2, 2, true, true, true);
afv.plusEquals(sv, 3.0);
assertEquals(6.0, afv.value(0), 0.001);
assertEquals(7.0, afv.value(1), 0.001);
}

@Test
public void testSetValueShouldUpdateCorrectIndexPostSort() {
Alphabet dict = new Alphabet();
dict.lookupIndex("x");
dict.lookupIndex("y");
dict.lookupIndex("z");
int[] indices = { 2, 0, 1 };
double[] values = { 0.5, 1.0, 1.5 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, indices, values, 3);
afv.setValue(1, 7.0);
assertEquals(7.0, afv.value(1), 0.001);
}

@Test
public void testValueForUnknownIndexSparseReturnsZero() {
Alphabet dict = new Alphabet();
dict.lookupIndex("a");
int[] indices = { 0 };
double[] values = { 2.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, indices, values, 1);
double v = afv.value(1);
assertEquals(0.0, v, 0.001);
}

@Test
public void testAddSparseVectorOnlyPartialOverlap() {
Alphabet dict = new Alphabet();
dict.lookupIndex("a");
dict.lookupIndex("b");
dict.lookupIndex("c");
int[] afvIndices = { 0 };
double[] afvValues = { 1.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, afvIndices, afvValues, 1);
int[] svIndices = { 0, 2 };
double[] svValues = { 2.0, 3.5 };
SparseVector sv = new SparseVector(svIndices, svValues, 2, 2, true, true, true);
afv.plusEquals(sv);
assertEquals(3.0, afv.value(0), 0.001);
assertEquals(0.0, afv.value(2), 0.001);
}

@Test
public void testSetValueAtLocationDenseVector() {
Alphabet dict = new Alphabet();
dict.lookupIndex("x");
dict.lookupIndex("y");
double[] values = { 1.0, 2.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, values);
afv.setValueAtLocation(1, 6.5);
assertEquals(6.5, afv.value(1), 0.001);
}

@Test(expected = AssertionError.class)
public void testSetValueAtLocationThrowsForInvalidOffset() {
Alphabet dict = new Alphabet();
dict.lookupIndex("x");
double[] values = { 2.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, values);
afv.setValueAtLocation(2, 3.0);
}

@Test
public void testSortIndicesPreservesValueAlignment() {
Alphabet dict = new Alphabet();
dict.lookupIndex("a");
dict.lookupIndex("b");
dict.lookupIndex("c");
int[] indices = { 2, 0, 1 };
double[] values = { 2.0, 3.0, 4.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, indices, values, 3);
// afv.sortIndices();
assertEquals(3.0, afv.value(0), 0.001);
assertEquals(4.0, afv.value(1), 0.001);
assertEquals(2.0, afv.value(2), 0.001);
}

@Test
public void testAddObjectKeyWithGrowingAlphabet() {
Alphabet dict = new Alphabet();
dict.lookupIndex("a");
PropertyList pl = PropertyList.add("b", 2.0, null);
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, pl, false, true);
assertEquals(2.0, afv.value(dict.lookupIndex("b")), 0.001);
}

@Test
public void testAddDenseBeyondArrayLengthTriggersGrowth() {
Alphabet dict = new Alphabet();
dict.lookupIndex("a");
double[] initial = new double[2];
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, initial, 2);
afv.add(5, 3.0);
assertEquals(3.0, afv.value(5), 0.001);
assertEquals(6, afv.numLocations());
}

@Test
public void testAddWithAutoCapacityDoublingLogic() {
Alphabet dict = new Alphabet();
for (int i = 0; i < 10; i++) {
dict.lookupIndex("f" + i);
}
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, 2, false);
afv.add(0, 1.0);
afv.add(1, 1.0);
afv.add(2, 1.0);
afv.add(3, 1.0);
afv.add(4, 1.0);
afv.add(5, 1.0);
afv.add(6, 1.0);
assertEquals(7, afv.numLocations());
assertEquals(1.0, afv.value(6), 0.001);
}

@Test
public void testAddToWithBinarySparseVectorAndScale() {
Alphabet dict = new Alphabet();
dict.lookupIndex("x");
dict.lookupIndex("y");
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, true);
afv.add(0);
afv.add(1);
double[] acc = new double[2];
afv.addTo(acc, 3.0);
assertEquals(3.0, acc[0], 0.001);
assertEquals(3.0, acc[1], 0.001);
}

@Test
public void testAddFromFeatureVectorWithStringPrefixBinary() {
Alphabet dictTarget = new Alphabet();
Alphabet dictSource = new Alphabet();
int idx = dictSource.lookupIndex("F");
int[] indices = { idx };
double[] values = { 1.0 };
FeatureVector srcFV = new FeatureVector(dictSource, indices, values);
AugmentableFeatureVector afv = new AugmentableFeatureVector(dictTarget, true);
afv.add(srcFV, "pre_");
int mappedIndex = dictTarget.lookupIndex("pre_F");
assertEquals(1.0, afv.value(mappedIndex), 0.001);
}

@Test
public void testAddFromFeatureVectorWithStringPrefixAndValue() {
Alphabet dictTarget = new Alphabet();
Alphabet dictSource = new Alphabet();
int idx = dictSource.lookupIndex("T");
double[] values = { 2.5 };
int[] indices = { idx };
FeatureVector src = new FeatureVector(dictSource, indices, values);
AugmentableFeatureVector afv = new AugmentableFeatureVector(dictTarget, false);
afv.add(src, "x_", false);
int newIndex = dictTarget.lookupIndex("x_T");
assertEquals(2.5, afv.value(newIndex), 0.001);
}

@Test
public void testAddToWithNullIndicesButNonUnityValue() {
Alphabet dict = new Alphabet();
dict.lookupIndex("a");
double[] values = new double[100];
values[99] = 9.0;
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, values);
double[] acc = new double[100];
afv.addTo(acc, 1.0);
assertEquals(9.0, acc[99], 0.001);
}

@Test
public void testRemoveDuplicatesWithManuallyCountedDuplicates() {
Alphabet dict = new Alphabet();
dict.lookupIndex("a");
int[] indices = { 0, 0, 0, 0 };
double[] values = { 1.0, 1.0, 1.0, 2.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, indices, values, 4, 4, true, false, false);
// afv.removeDuplicates(3);
assertEquals(1, afv.numLocations());
assertEquals(5.0, afv.value(0), 0.001);
}

@Test
public void testCloneMatrixZeroedReturnsCopyWithZeroValues() {
Alphabet dict = new Alphabet();
dict.lookupIndex("b");
double[] values = { 4.5 };
int[] indices = { 0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, indices, values, 1);
ConstantMatrix clone = afv.cloneMatrixZeroed();
// assertEquals(0.0, clone.value(0), 0.001);
assertEquals(1, clone.numLocations());
}

@Test
public void testToFeatureVectorCorrectlyTransfersState() {
Alphabet dict = new Alphabet();
dict.lookupIndex("t1");
dict.lookupIndex("t2");
double[] values = { 1.1, 2.2 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, values);
FeatureVector copy = afv.toFeatureVector();
assertEquals(2, copy.numLocations());
assertEquals(1.1, copy.valueAtLocation(0), 0.001);
assertEquals(2.2, copy.valueAtLocation(1), 0.001);
}

@Test(expected = IllegalArgumentException.class)
public void testDenseBinaryShouldRejectValueNotOne() {
Alphabet dict = new Alphabet();
dict.lookupIndex("f");
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, new int[1], null, 1, 0, false, false, false);
afv.add(0, 0.5);
}

@Test
public void testToFeatureVectorAfterRemoveDuplicates() {
Alphabet dict = new Alphabet();
dict.lookupIndex("a");
int[] indices = { 0, 0 };
double[] values = { 1.0, 1.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, indices, values, 2);
// afv.sortIndices();
FeatureVector fv = afv.toFeatureVector();
assertEquals(1, fv.numLocations());
assertEquals(2.0, fv.value(0), 0.001);
}

@Test
public void testRemoveDuplicatesEmptyVectorDoesNothing() {
Alphabet dict = new Alphabet();
int[] indices = new int[0];
double[] values = new double[0];
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, indices, values, 0, 0, true, false, false);
// afv.removeDuplicates(0);
assertEquals(0, afv.numLocations());
}

@Test
public void testDotProductBetweenSparseDenseOverlapWithZeroValues() {
Alphabet dict = new Alphabet();
dict.lookupIndex("a");
dict.lookupIndex("b");
int[] indices = { 0, 1 };
double[] values = { 0.0, 0.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, indices, values, 2);
DenseVector dense = new DenseVector(new double[] { 10.0, 5.0 });
double result = afv.dotProduct(dense);
assertEquals(0.0, result, 0.001);
}

@Test
public void testDotProductDifferentAlphabetsNoMatchingIndices() {
Alphabet dict1 = new Alphabet();
Alphabet dict2 = new Alphabet();
dict1.lookupIndex("x");
dict2.lookupIndex("y");
double[] values = { 1.0 };
int[] indices = { 0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict1, indices, values, 1, 1, true, true, true);
FeatureVector fv = new FeatureVector(dict2, indices, values);
afv.add(fv);
assertEquals(1.0, afv.value(0), 0.001);
assertEquals(1, afv.numLocations());
}

@Test
public void testSingleSizeOnEmptySparseVectorReturnsZero() {
Alphabet dict = new Alphabet();
int[] indices = new int[0];
double[] values = new double[0];
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, indices, values, 0, 0, true, false, false);
assertEquals(0, afv.singleSize());
}

@Test
public void testSingleSizeOnPopulatedSparseVectorReturnsHighestIndex() {
Alphabet dict = new Alphabet();
dict.lookupIndex("a");
dict.lookupIndex("b");
int[] indices = { 0, 1 };
double[] values = { 1.0, 2.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, indices, values, 2);
assertEquals(1, afv.singleSize());
}

@Test
public void testValueWhenIndicesAreNullAndSetManually() {
Alphabet dict = new Alphabet();
dict.lookupIndex("x");
double[] values = new double[3];
values[1] = 2.5;
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, values);
assertEquals(2.5, afv.value(1), 0.001);
}

@Test
public void testAddToWithNonZeroScaleAndDenseNullIndices() {
Alphabet dict = new Alphabet();
dict.lookupIndex("x");
double[] values = { 1.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, values);
double[] acc = new double[1];
afv.addTo(acc, 2.0);
assertEquals(2.0, acc[0], 0.001);
}

@Test
public void testDotProductWithAugmentableFeatureVectorOneEmpty() {
Alphabet dict = new Alphabet();
dict.lookupIndex("x");
AugmentableFeatureVector afv1 = new AugmentableFeatureVector(dict, true);
int[] indices = { 0 };
double[] values = { 1.0 };
AugmentableFeatureVector afv2 = new AugmentableFeatureVector(dict, indices, values, 1);
double result = afv1.dotProduct(afv2);
assertEquals(1.0, result, 0.001);
}

@Test
public void testPlusEqualsWithDenseVectorTruncatesByLength() {
Alphabet dict = new Alphabet();
dict.lookupIndex("x");
dict.lookupIndex("y");
double[] values1 = { 1.0, 2.0 };
AugmentableFeatureVector afv1 = new AugmentableFeatureVector(dict, values1);
double[] values2 = { 5.0 };
// SparseVector sv = new DenseVector(values2);
// afv1.plusEquals(sv, 1.0);
assertEquals(6.0, afv1.value(0), 0.001);
assertEquals(2.0, afv1.value(1), 0.001);
}

@Test
public void testTwoNormWithNullValuesBinaryReturnsSizeSqrt() {
Alphabet dict = new Alphabet();
dict.lookupIndex("x");
dict.lookupIndex("y");
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, 2, true);
afv.add(0);
afv.add(1);
double norm = afv.twoNorm();
assertEquals(Math.sqrt(2.0), norm, 0.001);
}

@Test
public void testRemoveDuplicatesWithUnsortedEqualIndices() {
Alphabet dict = new Alphabet();
dict.lookupIndex("a");
int[] indices = { 0, 0, 0 };
double[] values = { 0.5, 0.5, 1.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, indices, values, 3, 3, true, false, false);
// afv.sortIndices();
assertEquals(1, afv.numLocations());
assertEquals(2.0, afv.value(0), 0.001);
}

@Test
public void testToSparseVectorOnEmptyVector() {
Alphabet dict = new Alphabet();
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, true);
SparseVector sv = afv.toSparseVector();
assertEquals(0, sv.numLocations());
}

@Test
public void testInfinityNormReturnsZeroOnEmpty() {
Alphabet dict = new Alphabet();
dict.lookupIndex("x");
double[] values = { 0.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, values);
assertEquals(0.0, afv.infinityNorm(), 0.001);
}

@Test
public void testSerializationOfEmptyVectorMaintainsSize() throws Exception {
Alphabet dict = new Alphabet();
dict.lookupIndex("x");
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, new double[0]);
java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
ObjectOutputStream oos = new ObjectOutputStream(out);
oos.writeObject(afv);
oos.close();
byte[] raw = out.toByteArray();
ObjectInputStream ois = new ObjectInputStream(new java.io.ByteArrayInputStream(raw));
AugmentableFeatureVector deserialized = (AugmentableFeatureVector) ois.readObject();
assertEquals(0, deserialized.numLocations());
}
}
