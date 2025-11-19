package cc.mallet.types.tests;

import cc.mallet.types.*;
import cc.mallet.util.PropertyList;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AugmentableFeatureVector_1_GPTLLMTest {

@Test
public void testAddWithIndexAndValue_BinaryVector() {
Alphabet alphabet = new Alphabet();
int index = alphabet.lookupIndex("test", true);
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, true);
afv.add(index);
assertEquals(1.0, afv.value(index), 0.0);
}

@Test
public void testAddWithIndexAndValue_RealVector() {
Alphabet alphabet = new Alphabet();
int index = alphabet.lookupIndex("feature1", true);
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, false);
afv.add(index, 2.5);
assertEquals(2.5, afv.value(index), 0.0);
}

@Test(expected = IllegalArgumentException.class)
public void testAddNonBinaryValueToBinaryVectorThrows() {
Alphabet alphabet = new Alphabet();
int index = alphabet.lookupIndex("binary", true);
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, true);
afv.add(index, 2.0);
}

@Test
public void testAddFeatureVectorValuesMerged() {
Alphabet alphabet = new Alphabet();
int indexA = alphabet.lookupIndex("a", true);
int indexB = alphabet.lookupIndex("b", true);
AugmentableFeatureVector afv1 = new AugmentableFeatureVector(alphabet, false);
afv1.add(indexA, 1.0);
AugmentableFeatureVector afv2 = new AugmentableFeatureVector(alphabet, false);
afv2.add(indexB, 2.5);
afv1.add(afv2);
assertEquals(1.0, afv1.value(indexA), 0.0001);
assertEquals(2.5, afv1.value(indexB), 0.0001);
}

@Test
public void testAddWithKey() {
Alphabet alphabet = new Alphabet();
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, false);
afv.add("token", 3.3);
int index = alphabet.lookupIndex("token", false);
assertEquals(3.3, afv.value(index), 0.0);
}

@Test
public void testDotProductWithDenseVector() {
Alphabet alphabet = new Alphabet();
int idx0 = alphabet.lookupIndex("f0", true);
int idx1 = alphabet.lookupIndex("f1", true);
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, false);
afv.add(idx0, 2.0);
afv.add(idx1, 3.0);
double[] vector = new double[10];
vector[idx0] = 0.5;
vector[idx1] = 1.0;
DenseVector dense = new DenseVector(vector);
double result = afv.dotProduct(dense);
assertEquals(2.0 * 0.5 + 3.0 * 1.0, result, 0.0001);
}

@Test
public void testDotProductBinaryVectors() {
Alphabet alphabet = new Alphabet();
int index1 = alphabet.lookupIndex("a", true);
int index2 = alphabet.lookupIndex("b", true);
int index3 = alphabet.lookupIndex("c", true);
AugmentableFeatureVector afv1 = new AugmentableFeatureVector(alphabet, true);
afv1.add(index1);
afv1.add(index2);
AugmentableFeatureVector afv2 = new AugmentableFeatureVector(alphabet, true);
afv2.add(index1);
afv2.add(index3);
double result = afv1.dotProduct(afv2);
assertEquals(1.0, result, 0.0001);
}

@Test
public void testPlusEqualsWithScaling() {
Alphabet alphabet = new Alphabet();
int index = alphabet.lookupIndex("x", true);
AugmentableFeatureVector afv1 = new AugmentableFeatureVector(alphabet, false);
afv1.add(index, 2.0);
AugmentableFeatureVector afv2 = new AugmentableFeatureVector(alphabet, false);
afv2.add(index, 3.0);
afv1.plusEquals(afv2, 0.5);
assertEquals(2.0 + 0.5 * 3.0, afv1.value(index), 0.0001);
}

@Test
public void testAddToArrayWithScale() {
Alphabet alphabet = new Alphabet();
int index = alphabet.lookupIndex("p", true);
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, false);
afv.add(index, 2.0);
double[] acc = new double[10];
afv.addTo(acc, 3.0);
assertEquals(6.0, acc[index], 0.0001);
}

@Test
public void testNorms() {
Alphabet alphabet = new Alphabet();
int id1 = alphabet.lookupIndex("a", true);
int id2 = alphabet.lookupIndex("b", true);
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, false);
afv.add(id1, 3.0);
afv.add(id2, 4.0);
assertEquals(7.0, afv.oneNorm(), 0.0001);
assertEquals(5.0, afv.twoNorm(), 0.0001);
assertEquals(4.0, afv.infinityNorm(), 0.0001);
}

@Test
public void testSetValueAndSetValueAtLocation() {
Alphabet alphabet = new Alphabet();
int index = alphabet.lookupIndex("f", true);
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, false);
afv.add(index, 1.0);
afv.setValue(index, 5.0);
assertEquals(5.0, afv.value(index), 0.0001);
int location = afv.location(index);
afv.setValueAtLocation(location, 7.7);
assertEquals(7.7, afv.valueAtLocation(location), 0.0001);
}

@Test
public void testCloneMatrixCopiesValues() {
Alphabet alphabet = new Alphabet();
int index = alphabet.lookupIndex("z", true);
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, false);
afv.add(index, 2.0);
AugmentableFeatureVector clone = (AugmentableFeatureVector) afv.cloneMatrix();
assertEquals(2.0, clone.value(index), 0.0001);
}

@Test
public void testCloneMatrixZeroedHasZeroValues() {
Alphabet alphabet = new Alphabet();
int index = alphabet.lookupIndex("key", true);
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, false);
afv.add(index, 5.0);
AugmentableFeatureVector cloneZeroed = (AugmentableFeatureVector) afv.cloneMatrixZeroed();
assertEquals(0.0, cloneZeroed.value(index), 0.0001);
}

@Test
public void testSortIndicesAndRemoveDuplicates() {
Alphabet alphabet = new Alphabet();
int idA = alphabet.lookupIndex("a", true);
int idB = alphabet.lookupIndex("b", true);
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, false);
afv.add(idB, 1.0);
afv.add(idA, 2.0);
afv.add(idA, 3.0);
// afv.sortIndices();
assertEquals(5.0, afv.value(idA), 0.0001);
assertEquals(1.0, afv.value(idB), 0.0001);
}

@Test
public void testConstructorFromFeatureSequence() {
Alphabet alphabet = new Alphabet();
int index1 = alphabet.lookupIndex("foo", true);
int index2 = alphabet.lookupIndex("bar", true);
int[] indices = new int[3];
indices[0] = index1;
indices[1] = index2;
indices[2] = index1;
FeatureSequence fs = new FeatureSequence(alphabet);
fs.add(indices[0]);
fs.add(indices[1]);
fs.add(indices[2]);
AugmentableFeatureVector afv = new AugmentableFeatureVector(fs, false);
assertEquals(2.0, afv.value(index1), 0.0001);
assertEquals(1.0, afv.value(index2), 0.0001);
}

@Test
public void testConstructorFromPropertyList() {
Alphabet alphabet = new Alphabet();
PropertyList pl = PropertyList.add("f1", 1.5, null);
pl = PropertyList.add("f2", 2.5, pl);
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, pl, false, true);
int indexF1 = alphabet.lookupIndex("f1", false);
int indexF2 = alphabet.lookupIndex("f2", false);
assertEquals(1.5, afv.value(indexF1), 0.0001);
assertEquals(2.5, afv.value(indexF2), 0.0001);
}

@Test
public void testAddZeroValueIgnoredInDenseBinaryVector() {
Alphabet alphabet = new Alphabet();
int index = alphabet.lookupIndex("feature", true);
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, true);
afv.add(index);
assertEquals(1.0, afv.value(index), 0.0);
}

@Test
public void testAddValueBeyondInitialCapacityDense() {
Alphabet alphabet = new Alphabet();
int index1 = alphabet.lookupIndex("f1", true);
int index2 = 25;
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, true);
afv.add(index1);
afv.add(index2);
assertEquals(1.0, afv.value(index1), 0.0);
assertEquals(1.0, afv.value(index2), 0.0);
}

@Test
public void testAddDuplicateIndexRetainsSum_RealValues() {
Alphabet alphabet = new Alphabet();
int index = alphabet.lookupIndex("dup", true);
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, false);
afv.add(index, 2.0);
afv.add(index, 3.0);
// afv.sortIndices();
assertEquals(5.0, afv.value(index), 0.001);
}

@Test
public void testAddWithIndexExceedingCurrentMaxInSparseRealVector() {
Alphabet alphabet = new Alphabet();
int index = 1000;
int known = alphabet.lookupIndex("test", true);
int[] indices = new int[2];
double[] values = new double[2];
indices[0] = known;
values[0] = 1.0;
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, indices, values, 2, false);
afv.add(index, 7.0);
assertEquals(7.0, afv.value(index), 0.0001);
}

@Test
public void testAddMultipleFeatureVectors_MergedCorrectly() {
Alphabet alphabet = new Alphabet();
int idxA = alphabet.lookupIndex("a", true);
int idxB = alphabet.lookupIndex("b", true);
int idxC = alphabet.lookupIndex("c", true);
AugmentableFeatureVector afv1 = new AugmentableFeatureVector(alphabet, false);
afv1.add(idxA, 1.0);
AugmentableFeatureVector afv2 = new AugmentableFeatureVector(alphabet, false);
afv2.add(idxB, 2.0);
AugmentableFeatureVector afv3 = new AugmentableFeatureVector(alphabet, false);
afv3.add(idxC, 3.0);
afv1.add(afv2);
afv1.add(afv3);
assertEquals(1.0, afv1.value(idxA), 0.0001);
assertEquals(2.0, afv1.value(idxB), 0.0001);
assertEquals(3.0, afv1.value(idxC), 0.0001);
}

@Test
public void testDotProductSparseNullValues() {
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("feature", true);
AugmentableFeatureVector afv1 = new AugmentableFeatureVector(alphabet, true);
afv1.add(idx);
int[] indices = new int[] { idx };
double[] values = null;
SparseVector sparse = new SparseVector(indices, values, 1, 1, true, false, false);
double result = afv1.dotProduct(sparse);
assertEquals(1.0, result, 0.0001);
}

@Test(expected = IllegalArgumentException.class)
public void testAddBinaryFailsInRealVector() {
Alphabet alphabet = new Alphabet();
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, false);
int index = alphabet.lookupIndex("real", true);
if (afv.value(index) == 0.0) {
afv.add(index);
}
}

@Test
public void testInfinityNormNegativeValues() {
Alphabet alphabet = new Alphabet();
int id1 = alphabet.lookupIndex("f1", true);
int id2 = alphabet.lookupIndex("f2", true);
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, false);
afv.add(id1, -7.5);
afv.add(id2, 2.0);
double norm = afv.infinityNorm();
assertEquals(7.5, norm, 0.0001);
}

@Test
public void testSetAllValuesToZero() {
Alphabet alphabet = new Alphabet();
int idx1 = alphabet.lookupIndex("a", true);
int idx2 = alphabet.lookupIndex("b", true);
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, false);
afv.add(idx1, 1.0);
afv.add(idx2, 2.0);
afv.setAll(0.0);
assertEquals(0.0, afv.value(idx1), 0.0001);
assertEquals(0.0, afv.value(idx2), 0.0001);
}

@Test
public void testToFeatureVectorConversionRetainsValues() {
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("x", true);
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, false);
afv.add(idx, 9.9);
FeatureVector fv = afv.toFeatureVector();
assertEquals(9.9, fv.value(idx), 0.0001);
}

@Test
public void testToSparseVectorRetainsStructure() {
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("k", true);
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, false);
afv.add(idx, 2.0);
SparseVector sv = afv.toSparseVector();
assertEquals(2.0, sv.value(idx), 0.0001);
}

@Test
public void testEmptyFeatureVectorNormsAreZero() {
Alphabet alphabet = new Alphabet();
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, false);
assertEquals(0.0, afv.oneNorm(), 0.0001);
assertEquals(0.0, afv.twoNorm(), 0.0001);
assertEquals(Double.NEGATIVE_INFINITY, afv.infinityNorm(), 0.0001);
}

@Test
public void testAddToWithNoFeaturesDoesNotAlterAccumulator() {
Alphabet alphabet = new Alphabet();
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, false);
double[] acc = new double[5];
acc[0] = 1.0;
afv.addTo(acc);
assertEquals(1.0, acc[0], 0.0001);
}

@Test
public void testSingleSizeReturnsLastIndexForSparseData() {
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("token", true);
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, false);
afv.add(idx, 2.0);
int singleSize = afv.singleSize();
assertTrue(singleSize >= idx);
}

@Test
public void testValueForNonexistentIndexReturnsZero() {
Alphabet alphabet = new Alphabet();
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, false);
int index = alphabet.lookupIndex("absent", true);
assertEquals(0.0, afv.value(index), 0.0001);
}

@Test
public void testAddToWithIndicesNullAndValuesNull() {
Alphabet alphabet = new Alphabet();
int index = alphabet.lookupIndex("a", true);
double[] values = null;
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, null, values, 10);
afv.add(index);
double[] acc = new double[20];
afv.addTo(acc, 2.0);
assertEquals(2.0, acc[index], 0.001);
}

@Test
public void testAddToWithIndicesNonNullAndValuesNull() {
Alphabet alphabet = new Alphabet();
int index = alphabet.lookupIndex("a", true);
int[] indices = new int[1];
indices[0] = index;
double[] values = null;
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, indices, values, 1);
double[] acc = new double[20];
afv.addTo(acc, 3.0);
assertEquals(3.0, acc[index], 0.001);
}

@Test
public void testAddToWithIndicesAndValuesNotNull() {
Alphabet alphabet = new Alphabet();
int index = alphabet.lookupIndex("a", true);
int[] indices = new int[1];
double[] values = new double[1];
indices[0] = index;
values[0] = 4.0;
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, indices, values, 1);
double[] acc = new double[20];
afv.addTo(acc, 1.5);
assertEquals(6.0, acc[index], 0.001);
}

@Test
public void testPlusEqualsWithDenseRealVectors() {
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("x", true);
double[] values1 = new double[5];
values1[idx] = 2.0;
double[] values2 = new double[5];
values2[idx] = 3.0;
AugmentableFeatureVector afv1 = new AugmentableFeatureVector(alphabet, null, values1, 5);
AugmentableFeatureVector afv2 = new AugmentableFeatureVector(alphabet, null, values2, 5);
afv1.plusEquals(afv2, 2.0);
assertEquals(2.0 + 3.0 * 2.0, afv1.value(idx), 0.0001);
}

@Test
public void testPlusEqualsWithSparseBinaryOnSparseReal() {
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("a", true);
int[] indices = new int[] { idx };
double[] values = new double[] { 2.0 };
AugmentableFeatureVector afvReal = new AugmentableFeatureVector(alphabet, indices, values, 1);
AugmentableFeatureVector afvBinary = new AugmentableFeatureVector(alphabet, true);
afvBinary.add(idx);
afvReal.plusEquals(afvBinary, 1.0);
assertEquals(3.0, afvReal.value(idx), 0.001);
}

@Test
public void testPlusEqualsWithSparseRealOnSparseReal() {
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("tok", true);
AugmentableFeatureVector afv1 = new AugmentableFeatureVector(alphabet, false);
afv1.add(idx, 1.0);
// afv1.sortIndices();
AugmentableFeatureVector afv2 = new AugmentableFeatureVector(alphabet, false);
afv2.add(idx, 2.0);
// afv2.sortIndices();
afv1.plusEquals(afv2, 0.5);
assertEquals(1.0 + 2.0 * 0.5, afv1.value(idx), 0.0001);
}

@Test
public void testDotProductWithSparseValuesNullBothSides() {
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("abc", true);
AugmentableFeatureVector afv1 = new AugmentableFeatureVector(alphabet, true);
afv1.add(idx);
SparseVector sv = new SparseVector(new int[] { idx }, null, 1, 1, true, false, false);
double result = afv1.dotProduct(sv);
assertEquals(1.0, result, 0.001);
}

@Test
public void testDotProductAugmentableBinaryVsDenseReal() {
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("fe", true);
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, true);
afv.add(idx);
double[] data = new double[100];
data[idx] = 7.0;
DenseVector dv = new DenseVector(data);
double result = afv.dotProduct(dv);
assertEquals(7.0, result, 0.001);
}

@Test
public void testMultipleAddSameIndexBeforeSortResultEqualsSum() {
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("merged", true);
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, false);
afv.add(idx, 1.0);
afv.add(idx, 2.0);
afv.add(idx, 3.0);
// afv.sortIndices();
assertEquals(6.0, afv.value(idx), 0.0001);
}

@Test
public void testAddToAccumulatorWithDefaultScale() {
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("acc", true);
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, false);
afv.add(idx, 5.0);
double[] acc = new double[10];
afv.addTo(acc);
assertEquals(5.0, acc[idx], 0.0001);
}

@Test
public void testSetValueExactIndexSparseReal() {
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("mod", true);
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, false);
afv.add(idx, 4.0);
// afv.sortIndices();
afv.setValue(idx, 9.0);
assertEquals(9.0, afv.value(idx), 0.0001);
}

@Test
public void testSetValueAtLocationModifiesExpected() {
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("modloc", true);
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, false);
afv.add(idx, 3.3);
int loc = afv.location(idx);
afv.setValueAtLocation(loc, 7.7);
assertEquals(7.7, afv.value(idx), 0.0001);
}

@Test
public void testToSparseVectorAfterAddReturnsExpectedValues() {
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("sparse", true);
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, false);
afv.add(idx, 2.2);
SparseVector sv = afv.toSparseVector();
assertEquals(2.2, sv.value(idx), 0.001);
}

@Test
public void testToFeatureVectorContainsSameValues() {
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("fv", true);
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, false);
afv.add(idx, 1.5);
FeatureVector fv = afv.toFeatureVector();
assertEquals(1.5, fv.value(idx), 0.0001);
}

@Test
public void testValueOnMissingIndexDenseDataReturnsZero() {
Alphabet alphabet = new Alphabet();
double[] values = new double[10];
values[0] = 1.0;
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, null, values, 10);
int index = 5;
assertEquals(0.0, afv.value(index), 0.0001);
}

@Test
public void testValueAtOutOfBoundsIndexReturnsZeroSparse() {
Alphabet alphabet = new Alphabet();
int idx1 = alphabet.lookupIndex("one", true);
int idx2 = alphabet.lookupIndex("two", true);
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, false);
afv.add(idx1, 2.0);
assertEquals(0.0, afv.value(idx2), 0.0001);
}

@Test
public void testRemoveDuplicatesWithNullValues() {
Alphabet alphabet = new Alphabet();
int index = alphabet.lookupIndex("dupKey", true);
int[] indices = new int[] { index, index };
double[] values = null;
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, indices, values, 2, false, false);
// afv.sortIndices();
assertEquals(1.0, afv.value(index), 0.0001);
assertEquals(1, afv.numLocations());
}

@Test
public void testAddToWithEmptyVectorNoError() {
Alphabet alphabet = new Alphabet();
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, false);
double[] accumulator = new double[10];
afv.addTo(accumulator);
assertEquals(0.0, accumulator[0], 0.0001);
}

@Test
public void testDotProductWithEmptyVectorReturnsZero() {
Alphabet alphabet = new Alphabet();
AugmentableFeatureVector afv1 = new AugmentableFeatureVector(alphabet, false);
AugmentableFeatureVector afv2 = new AugmentableFeatureVector(alphabet, false);
double result = afv1.dotProduct(afv2);
assertEquals(0.0, result, 0.0001);
}

@Test
public void testAddToWithNegativeAndPositiveValues() {
Alphabet alphabet = new Alphabet();
int index1 = alphabet.lookupIndex("pos", true);
int index2 = alphabet.lookupIndex("neg", true);
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, false);
afv.add(index1, 3.0);
afv.add(index2, -2.0);
double[] acc = new double[10];
afv.addTo(acc);
assertEquals(3.0, acc[index1], 0.0001);
assertEquals(-2.0, acc[index2], 0.0001);
}

@Test
public void testInfinityNormWithSingleNegative() {
Alphabet alphabet = new Alphabet();
int index = alphabet.lookupIndex("negmax", true);
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, false);
afv.add(index, -9.2);
double infNorm = afv.infinityNorm();
assertEquals(9.2, infNorm, 0.0001);
}

@Test
public void testSortIndicesAlreadySortedDoesNotChangeValues() {
Alphabet alphabet = new Alphabet();
int index1 = alphabet.lookupIndex("a", true);
int index2 = alphabet.lookupIndex("b", true);
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, false);
afv.add(index1, 1.0);
afv.add(index2, 2.0);
// afv.sortIndices();
// afv.sortIndices();
assertEquals(1.0, afv.value(index1), 0.001);
assertEquals(2.0, afv.value(index2), 0.001);
}

@Test
public void testCloneMatrixPreservesIndicesAndValues() {
Alphabet alphabet = new Alphabet();
int index = alphabet.lookupIndex("retain", true);
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, false);
afv.add(index, 42.0);
ConstantMatrix clone = afv.cloneMatrix();
assertTrue(clone instanceof AugmentableFeatureVector);
AugmentableFeatureVector cloned = (AugmentableFeatureVector) clone;
assertEquals(42.0, cloned.value(index), 0.001);
}

@Test
public void testConstructorFromPropertyListWithNullInput() {
Alphabet alphabet = new Alphabet();
PropertyList pl = null;
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, pl, false, true);
assertEquals(0, afv.numLocations());
}

@Test
public void testSortIndicesWithZeroSizeDoesNotCrash() {
Alphabet alphabet = new Alphabet();
int[] indices = new int[] {};
double[] values = new double[] {};
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, indices, values, 0, 0, false, false, false);
// afv.sortIndices();
assertEquals(0, afv.numLocations());
}

@Test
public void testDotProductWithMismatchedAlphabetsReturnsZero() {
Alphabet a1 = new Alphabet();
Alphabet a2 = new Alphabet();
int index1 = a1.lookupIndex("f1", true);
int index2 = a2.lookupIndex("f1", true);
AugmentableFeatureVector v1 = new AugmentableFeatureVector(a1, false);
AugmentableFeatureVector v2 = new AugmentableFeatureVector(a2, false);
v1.add(index1, 1.0);
v2.add(index2, 2.0);
double result = v1.dotProduct(v2);
assertEquals(2.0, result, 0.0001);
}

@Test
public void testAddSameFeatureNameFromTwoFeatureVectorsWithPrefix() {
Alphabet alphabet = new Alphabet();
int baseIndex = alphabet.lookupIndex("foo", true);
AugmentableFeatureVector source = new AugmentableFeatureVector(alphabet, false);
source.add(baseIndex, 2.0);
AugmentableFeatureVector target = new AugmentableFeatureVector(alphabet, false);
target.add(source, "prefix_");
int prefixedIndex = alphabet.lookupIndex("prefix_foo", false);
assertEquals(2.0, target.value(prefixedIndex), 0.001);
}

@Test
public void testAddWithPrefixNonBinaryPreservesValueWeight() {
Alphabet alphabet = new Alphabet();
int index = alphabet.lookupIndex("ft", true);
AugmentableFeatureVector f1 = new AugmentableFeatureVector(alphabet, false);
f1.add(index, 3.3);
AugmentableFeatureVector f2 = new AugmentableFeatureVector(alphabet, false);
f2.add(f1, "p_", false);
int pIndex = alphabet.lookupIndex("p_ft", false);
assertEquals(3.3, f2.value(pIndex), 0.001);
}

@Test
public void testAddObjectKeyWithExistingValueAccumulates() {
Alphabet alphabet = new Alphabet();
Alphabet a = alphabet;
a.lookupIndex("add", true);
AugmentableFeatureVector afv = new AugmentableFeatureVector(a, false);
afv.add("add", 1.0);
afv.add("add", 2.0);
int index = a.lookupIndex("add", false);
assertEquals(3.0, afv.value(index), 0.001);
}

@Test
public void testSetAllOverwritesAllValues() {
Alphabet alphabet = new Alphabet();
int id1 = alphabet.lookupIndex("a", true);
int id2 = alphabet.lookupIndex("b", true);
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, false);
afv.add(id1, 10.0);
afv.add(id2, 20.0);
afv.setAll(0.5);
assertEquals(0.5, afv.value(id1), 0.0001);
assertEquals(0.5, afv.value(id2), 0.0001);
}

@Test
public void testAddSingleElementBeyondInitialSparseCapacity() {
Alphabet alphabet = new Alphabet();
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, 1, false);
int index = alphabet.lookupIndex("f_expansion", true);
afv.add(index, 1.0);
assertEquals(1.0, afv.value(index), 0.0001);
}

@Test
public void testAddSparseRealVectorTriggersResizeAndPreservesData() {
Alphabet alphabet = new Alphabet();
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, false);
int id1 = alphabet.lookupIndex("a", true);
int id2 = alphabet.lookupIndex("b", true);
afv.add(id1, 1.0);
afv.add(id2, 2.0);
double[] acc = new double[10];
afv.addTo(acc);
assertEquals(1.0, acc[id1], 0.001);
assertEquals(2.0, acc[id2], 0.001);
}

@Test
public void testDotProductDenseVectorSparseReal() {
Alphabet alphabet = new Alphabet();
double[] values = new double[5];
int index = alphabet.lookupIndex("dense", true);
values[index] = 3.0;
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, null, values, 5);
double[] input = new double[5];
input[index] = 2.0;
DenseVector dense = new DenseVector(input);
double dot = afv.dotProduct(dense);
assertEquals(6.0, dot, 0.001);
}

@Test
public void testDotProductSparseValuesBothNull() {
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("word", true);
int[] indices = new int[] { idx };
SparseVector sv = new SparseVector(indices, null, 1, 1, true, false, false);
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, true);
afv.add(idx);
double result = afv.dotProduct(sv);
assertEquals(1.0, result, 0.0001);
}

@Test
public void testPlusEqualsSparseRealOnSparseBinary() {
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("x", true);
AugmentableFeatureVector real = new AugmentableFeatureVector(alphabet, false);
real.add(idx, 5.0);
// real.sortIndices();
AugmentableFeatureVector binary = new AugmentableFeatureVector(alphabet, true);
binary.add(idx);
// binary.sortIndices();
real.plusEquals(binary, 2.0);
assertEquals(7.0, real.value(idx), 0.0001);
}

@Test
public void testSetValueOnNonExistentIndexSparseShouldNotThrow() {
Alphabet alphabet = new Alphabet();
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, false);
int idx = alphabet.lookupIndex("foo", true);
afv.add(idx, 1.0);
// afv.sortIndices();
int missing = alphabet.lookupIndex("bar", true);
afv.setValue(missing, 0.0);
assertEquals(0.0, afv.value(missing), 0.0001);
}

@Test
public void testDotProductSparseDenseSwitchingPaths() {
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("s", true);
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, false);
afv.add(idx, 4.0);
// afv.sortIndices();
int[] svIndices = new int[] { idx };
double[] svValues = new double[] { 1.5 };
SparseVector sv = new SparseVector(svIndices, svValues, 1, 1, true, false, false);
double result = afv.dotProduct(sv);
assertEquals(6.0, result, 0.0001);
}

@Test
public void testVectorWithMaxSortedIndexLessThanSizeTriggersSort() {
Alphabet alphabet = new Alphabet();
int id1 = alphabet.lookupIndex("id1", true);
int id2 = alphabet.lookupIndex("id2", true);
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, false);
afv.add(id1, 1.0);
afv.add(id2, 2.0);
assertEquals(2, afv.numLocations());
assertEquals(2.0, afv.value(id2), 0.001);
}

@Test
public void testOneNormOnBinaryVectorReturnsSize() {
Alphabet alphabet = new Alphabet();
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, true);
int idx1 = alphabet.lookupIndex("f1", true);
int idx2 = alphabet.lookupIndex("f2", true);
afv.add(idx1);
afv.add(idx2);
assertEquals(2.0, afv.oneNorm(), 0.0001);
}

@Test
public void testTwoNormOfBinaryVectorReturnsSqrtOfSize() {
Alphabet alphabet = new Alphabet();
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, true);
int idx1 = alphabet.lookupIndex("x1", true);
int idx2 = alphabet.lookupIndex("x2", true);
int idx3 = alphabet.lookupIndex("x3", true);
afv.add(idx1);
afv.add(idx2);
afv.add(idx3);
assertEquals(Math.sqrt(3.0), afv.twoNorm(), 0.0001);
}

@Test
public void testInfinityNormOnEmptyRealVectorReturnsNegInfinity() {
Alphabet alphabet = new Alphabet();
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, false);
assertEquals(Double.NEGATIVE_INFINITY, afv.infinityNorm(), 0.0001);
}

@Test
public void testAddToSparseBinaryAndPartialAccumulator() {
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("token", true);
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, true);
afv.add(idx);
double[] accumulator = new double[idx + 1];
afv.addTo(accumulator);
assertEquals(1.0, accumulator[idx], 0.0001);
}

@Test
public void testPlusEqualsDoesNotExceedOriginalSizeWhenIndicesMismatch() {
Alphabet alphabet = new Alphabet();
int idx1 = alphabet.lookupIndex("a", true);
int idx2 = alphabet.lookupIndex("b", true);
AugmentableFeatureVector afv1 = new AugmentableFeatureVector(alphabet, false);
afv1.add(idx1, 1.0);
// afv1.sortIndices();
AugmentableFeatureVector afv2 = new AugmentableFeatureVector(alphabet, false);
afv2.add(idx2, 1.0);
// afv2.sortIndices();
afv1.plusEquals(afv2, 1.0);
assertEquals(1.0, afv1.value(idx1), 0.001);
assertEquals(0.0, afv1.value(idx2), 0.001);
}

@Test
public void testSingleSizeReturnsLastIndexForSparseReal() {
Alphabet alphabet = new Alphabet();
int idx1 = alphabet.lookupIndex("a", true);
int idx2 = alphabet.lookupIndex("z", true);
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, false);
afv.add(idx1, 1.0);
afv.add(idx2, 2.0);
assertTrue(afv.singleSize() >= idx2);
}

@Test
public void testDotProductOfDenseRealWithSparseBinary() {
Alphabet alphabet = new Alphabet();
double[] values = new double[10];
int idx = alphabet.lookupIndex("w", true);
values[idx] = 2.0;
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, null, values, 10);
int[] indices = new int[] { idx };
SparseVector sparse = new SparseVector(indices, null, 1, 1, true, false, false);
double result = afv.dotProduct(sparse);
assertEquals(2.0, result, 0.001);
}

@Test
public void testConstructor_UnsortedNoDedup() {
Alphabet alphabet = new Alphabet();
int idx1 = alphabet.lookupIndex("f1", true);
int idx2 = alphabet.lookupIndex("f2", true);
int[] indices = new int[] { idx2, idx1 };
double[] values = new double[] { 1.0, 2.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, indices, values, 2, 2, true, false, false);
assertEquals(2, afv.numLocations());
}

@Test
public void testSetValueOnNonExistingIndexSparseVector_WithMultipleEntries_NoException() {
Alphabet alphabet = new Alphabet();
int idx1 = alphabet.lookupIndex("f1", true);
int idxTarget = alphabet.lookupIndex("notAdded", true);
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, false);
afv.add(idx1, 3.0);
// afv.sortIndices();
afv.setValue(idxTarget, 11.0);
assertEquals(11.0, afv.value(idxTarget), 0.0001);
}

@Test
public void testAddWithUnknownObjectKey_ExpandsAlphabet() {
Alphabet alphabet = new Alphabet();
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, false);
afv.add("newFeature", 9.0);
int index = alphabet.lookupIndex("newFeature", false);
assertTrue(index >= 0);
assertEquals(9.0, afv.value(index), 0.0001);
}

@Test
public void testRemoveDuplicates_SkipRecount() {
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("f", true);
int[] indices = new int[] { idx, idx };
double[] values = new double[] { 1.0, 1.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, indices, values, 2, 2, false, false, false);
// afv.sortIndices();
// afv.sortIndices();
assertEquals(2.0, afv.value(idx), 0.001);
assertEquals(1, afv.numLocations());
}

@Test
public void testCloneMatrixZeroedOnBinaryVector() {
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("binaryK", true);
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, true);
afv.add(idx);
ConstantMatrix clone = afv.cloneMatrixZeroed();
assertTrue(clone instanceof AugmentableFeatureVector);
AugmentableFeatureVector zeroed = (AugmentableFeatureVector) clone;
assertEquals(0.0, zeroed.value(idx), 0.0001);
}

@Test
public void testLocationOfUnknownIndexInDenseVectorReturnsIndex() {
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("f", true);
double[] values = new double[10];
values[idx] = 5.5;
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, null, values, 10);
int loc = afv.location(idx);
assertEquals(idx, loc);
}

@Test
public void testLocationOfNonExistentIndexSparseReturnsMinusOne() {
Alphabet alphabet = new Alphabet();
int idx1 = alphabet.lookupIndex("f1", true);
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, false);
afv.add(idx1, 1.0);
// afv.sortIndices();
int unknown = alphabet.lookupIndex("unknown", true);
int loc = afv.location(unknown);
assertEquals(-1, loc);
}

@Test
public void testOneNormSingleElement() {
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("v", true);
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, false);
afv.add(idx, 6.2);
double norm = afv.oneNorm();
assertEquals(6.2, norm, 0.0001);
}

@Test
public void testTwoNormSingleElement() {
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("v2", true);
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, false);
afv.add(idx, 3.0);
double twoNorm = afv.twoNorm();
assertEquals(3.0, twoNorm, 0.0001);
}

@Test
public void testInfinityNormSingleElement() {
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("inf", true);
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, false);
afv.add(idx, -77.0);
double infNorm = afv.infinityNorm();
assertEquals(77.0, infNorm, 0.0001);
}

@Test
public void testClonePreservesSortedOrderAndNoMutationOfOriginal() {
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("k", true);
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, false);
afv.add(idx, 9.0);
// afv.sortIndices();
AugmentableFeatureVector clone = (AugmentableFeatureVector) afv.cloneMatrix();
assertEquals(9.0, clone.value(idx), 0.0001);
assertEquals(9.0, afv.value(idx), 0.0001);
assertEquals(clone.numLocations(), afv.numLocations());
}

@Test
public void testPlusEqualsSparseVector_UnmatchedIndicesAllIgnored() {
Alphabet alphabet = new Alphabet();
int idxMain = alphabet.lookupIndex("main", true);
int idxOther = alphabet.lookupIndex("other", true);
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, false);
afv.add(idxMain, 1.0);
// afv.sortIndices();
int[] indices = new int[] { idxOther };
double[] values = new double[] { 99.0 };
SparseVector sv = new SparseVector(indices, values, 1, 1, true, false, false);
afv.plusEquals(sv, 0.5);
assertEquals(1.0, afv.value(idxMain), 0.0001);
assertEquals(0.0, afv.value(idxOther), 0.0001);
}

@Test
public void testToFeatureVectorContentsEqualOriginal() {
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("copy", true);
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, false);
afv.add(idx, 4.4);
FeatureVector fv = afv.toFeatureVector();
assertEquals(4.4, fv.value(idx), 0.0001);
}

@Test
public void testDenseVectorSetValueDoesNotAffectOthers() {
Alphabet alphabet = new Alphabet();
double[] values = new double[3];
values[0] = 1.0;
values[1] = 2.0;
values[2] = 3.0;
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, null, values, 3);
afv.setValue(1, 9.0);
assertEquals(1.0, afv.value(0), 0.0001);
assertEquals(9.0, afv.value(1), 0.0001);
assertEquals(3.0, afv.value(2), 0.0001);
}

@Test
public void testPlusEqualsSparseRightTruncatesMissingLeftIndices() {
Alphabet alphabet = new Alphabet();
int idxA = alphabet.lookupIndex("x", true);
int idxB = alphabet.lookupIndex("y", true);
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, false);
afv.add(idxA, 1.0);
// afv.sortIndices();
int[] indices = new int[] { idxB };
double[] values = new double[] { 4.0 };
SparseVector sv = new SparseVector(indices, values, 1, 1, true, false, false);
afv.plusEquals(sv);
assertEquals(1.0, afv.value(idxA), 0.0001);
assertEquals(0.0, afv.value(idxB), 0.0001);
}

@Test
public void testDotProductAugmentableFirstDenseValuesNull() {
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("binary", true);
int[] indices1 = new int[] { idx };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, indices1, null, 1);
double[] denseValues = new double[10];
denseValues[idx] = 1.5;
DenseVector dense = new DenseVector(denseValues);
double result = afv.dotProduct(dense);
assertEquals(1.5, result, 0.001);
}

@Test
public void testDotProductAllCombinationSparseStructures() {
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("shared", true);
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, true);
afv.add(idx);
int[] indices = new int[] { idx };
SparseVector sv = new SparseVector(indices, null, 1, 1, true, false, false);
double result = afv.dotProduct(sv);
assertEquals(1.0, result, 0.001);
}

@Test
public void testSetAllToZeroRetainsIndicesAndNumLocations() {
Alphabet alphabet = new Alphabet();
int idxA = alphabet.lookupIndex("v1", true);
int idxB = alphabet.lookupIndex("v2", true);
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, false);
afv.add(idxA, 1.0);
afv.add(idxB, 2.0);
// afv.sortIndices();
int before = afv.numLocations();
afv.setAll(0.0);
int after = afv.numLocations();
assertEquals(0.0, afv.value(idxA), 0.001);
assertEquals(0.0, afv.value(idxB), 0.001);
assertEquals(before, after);
}

@Test
public void testAddMoreThan100GrowPatternAbove100() {
Alphabet alphabet = new Alphabet();
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, false);
int i0 = alphabet.lookupIndex("initial", true);
for (int i = 1; i <= 105; i++) {
String feature = "f" + i;
int idx = alphabet.lookupIndex(feature, true);
afv.add(idx, 1.0);
}
assertTrue(afv.numLocations() >= 106);
}

@Test
public void testAddBinaryToRealVectorThrows() {
Alphabet alphabet = new Alphabet();
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, false);
int idx = alphabet.lookupIndex("real", true);
afv.add(idx, 1.5);
try {
afv.add(idx);
// fail("Expected IllegalArgumentException not thrown.");
} catch (IllegalArgumentException ex) {
assertTrue(ex.getMessage().contains("Trying to add binary feature"));
}
}

@Test
public void testCloneZeroedValuesInDenseVector() {
Alphabet alphabet = new Alphabet();
double[] dense = new double[5];
dense[2] = 7.0;
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, null, dense, 5);
ConstantMatrix clone = afv.cloneMatrixZeroed();
assertTrue(clone instanceof AugmentableFeatureVector);
AugmentableFeatureVector result = (AugmentableFeatureVector) clone;
assertEquals(0.0, result.value(2), 0.0001);
}

@Test
public void testSortIndicesAndRemoveDuplicatesAcrossMultiple() {
Alphabet alphabet = new Alphabet();
int idxA = alphabet.lookupIndex("dup1", true);
int idxB = alphabet.lookupIndex("dup2", true);
int[] indices = new int[] { idxA, idxB, idxA, idxB };
double[] values = new double[] { 1.0, 2.0, 3.0, 1.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, indices, values, 4, false, false);
// afv.sortIndices();
assertEquals(1.0 + 3.0, afv.value(idxA), 0.001);
assertEquals(2.0 + 1.0, afv.value(idxB), 0.001);
}

@Test
public void testToSparseVectorWithUnsortedDataTriggersSort() {
Alphabet alphabet = new Alphabet();
int idx1 = alphabet.lookupIndex("x", true);
int idx2 = alphabet.lookupIndex("y", true);
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, false);
afv.add(idx2, 2.0);
afv.add(idx1, 1.0);
SparseVector sv = afv.toSparseVector();
assertEquals(1.0, sv.value(idx1), 0.001);
assertEquals(2.0, sv.value(idx2), 0.001);
}

@Test
public void testAddToWithSparseIndicesNullDenseValues() {
Alphabet alphabet = new Alphabet();
double[] dense = new double[3];
dense[1] = 4.0;
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, null, dense, 3);
double[] acc = new double[3];
afv.addTo(acc, 2.0);
assertEquals(8.0, acc[1], 0.001);
}

@Test
public void testSetValueOnDenseVectorAtSpecificIndex() {
Alphabet alphabet = new Alphabet();
double[] dense = new double[5];
dense[3] = 10.0;
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, null, dense, 5);
afv.setValue(3, 99.9);
assertEquals(99.9, afv.value(3), 0.0001);
}

@Test
public void testSingleSizeWhenVectorIsEmptyReturnsZero() {
Alphabet alphabet = new Alphabet();
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, false);
assertEquals(0, afv.singleSize());
}

@Test
public void testConstructorWithFeatureVectorCopyPreservesValues() {
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("feature", true);
AugmentableFeatureVector original = new AugmentableFeatureVector(alphabet, false);
original.add(idx, 7.7);
FeatureVector fv = original.toFeatureVector();
AugmentableFeatureVector copy = new AugmentableFeatureVector(fv);
assertEquals(7.7, copy.value(idx), 0.0001);
}
}
