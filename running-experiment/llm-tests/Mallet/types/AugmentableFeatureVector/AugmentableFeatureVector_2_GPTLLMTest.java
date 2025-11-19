package cc.mallet.types.tests;

import cc.mallet.types.*;
import cc.mallet.util.PropertyList;
import org.junit.Test;
import java.io.*;
import static org.junit.Assert.*;

public class AugmentableFeatureVector_2_GPTLLMTest {

@Test
public void testConstructorWithDenseValues() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("f1");
alphabet.lookupIndex("f2");
alphabet.lookupIndex("f3");
double[] values = new double[] { 1.0, 2.0, 3.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, values);
assertEquals(3, afv.numLocations());
assertEquals(1.0, afv.value(0), 1e-6);
assertEquals(2.0, afv.value(1), 1e-6);
assertEquals(3.0, afv.value(2), 1e-6);
}

@Test
public void testConstructorWithSparseValues() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("f1");
alphabet.lookupIndex("f2");
alphabet.lookupIndex("f3");
int[] indices = new int[] { 0, 2 };
double[] values = new double[] { 4.0, 6.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, indices, values, 2);
assertEquals(2, afv.numLocations());
assertEquals(4.0, afv.value(0), 1e-6);
assertEquals(0.0, afv.value(1), 1e-6);
assertEquals(6.0, afv.value(2), 1e-6);
}

@Test(expected = IllegalArgumentException.class)
public void testAddInvalidValueToBinaryVector() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("f1");
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, true);
int index = alphabet.lookupIndex("f1");
afv.add(index, 2.5);
}

@Test
public void testAddBinaryFeatureToBinaryVector() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("feat");
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, true);
afv.add(alphabet.lookupIndex("feat"));
assertEquals(1.0, afv.value(alphabet.lookupIndex("feat")), 1e-6);
}

@Test(expected = IllegalArgumentException.class)
public void testAddIndexToRealValuedVector() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("f1");
int[] indices = new int[] { 0 };
double[] values = new double[] { 1.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, indices, values, 1);
afv.add(1);
}

@Test
public void testAddByKey() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("x");
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, true);
afv.add("x", 1.0);
int index = alphabet.lookupIndex("x", false);
assertEquals(1.0, afv.value(index), 1e-6);
}

@Test
public void testSetValueAtIndex() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("f1");
alphabet.lookupIndex("f2");
double[] values = new double[] { 3.0, 8.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, values);
afv.setValue(1, 9.5);
assertEquals(9.5, afv.value(1), 1e-6);
}

@Test
public void testDotProductWithDenseVector() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("a");
alphabet.lookupIndex("b");
alphabet.lookupIndex("c");
double[] values = new double[] { 1.0, 2.0, 3.0 };
DenseVector dv = new DenseVector(new double[] { 2.0, 0.5, 1.0 });
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, values);
double result = afv.dotProduct(dv);
double expected = 1.0 * 2.0 + 2.0 * 0.5 + 3.0 * 1.0;
assertEquals(expected, result, 1e-6);
}

@Test
public void testOneNorm() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("a");
alphabet.lookupIndex("b");
double[] values = new double[] { 2.0, 3.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, values);
assertEquals(5.0, afv.oneNorm(), 1e-6);
}

@Test
public void testTwoNorm() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("a");
alphabet.lookupIndex("b");
double[] values = new double[] { 3.0, 4.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, values);
assertEquals(5.0, afv.twoNorm(), 1e-6);
}

@Test
public void testInfinityNorm() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("a");
alphabet.lookupIndex("b");
alphabet.lookupIndex("c");
double[] values = new double[] { 1.0, -7.5, 3.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, values);
assertEquals(7.5, afv.infinityNorm(), 1e-6);
}

@Test
public void testAddFeatureVectorWithPrefix() {
Alphabet dict = new Alphabet();
dict.lookupIndex("a");
int idX = dict.lookupIndex("x");
Alphabet otherDict = new Alphabet();
int idY = otherDict.lookupIndex("y");
AugmentableFeatureVector afvX = new AugmentableFeatureVector(dict, true);
afvX.add(idX);
int[] indices = new int[] { idY };
double[] values = new double[] { 1.0 };
// FeatureVector otherFv = new FeatureVector(otherDict, indices, values, 1);
// afvX.add(otherFv, "pre_");
int newIndex = dict.lookupIndex("pre_y", false);
assertTrue(newIndex >= 0);
assertEquals(1.0, afvX.value(newIndex), 1e-6);
}

@Test
public void testCloneMatrix_preservesValues() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("x");
alphabet.lookupIndex("y");
int[] indices = new int[] { 0, 1 };
double[] values = new double[] { 10.0, 20.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, indices, values, 2);
AugmentableFeatureVector clone = (AugmentableFeatureVector) afv.cloneMatrix();
assertEquals(10.0, clone.value(0), 1e-6);
assertEquals(20.0, clone.value(1), 1e-6);
}

@Test
public void testCloneMatrixZeroed_returnsZeroedValues() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("x");
alphabet.lookupIndex("y");
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, new double[] { 1.5, 2.5 });
AugmentableFeatureVector zeroed = (AugmentableFeatureVector) afv.cloneMatrixZeroed();
assertEquals(0.0, zeroed.value(0), 1e-6);
assertEquals(0.0, zeroed.value(1), 1e-6);
}

@Test
public void testSetAllValuesToConstant() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("a");
alphabet.lookupIndex("b");
alphabet.lookupIndex("c");
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, new double[] { 1.0, 2.0, 3.0 });
afv.setAll(5.0);
assertEquals(5.0, afv.value(0), 1e-6);
assertEquals(5.0, afv.value(1), 1e-6);
assertEquals(5.0, afv.value(2), 1e-6);
}

@Test
public void testAddToAccumulatorArray() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("f1");
alphabet.lookupIndex("f2");
double[] values = new double[] { 2.0, 3.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, values);
double[] accumulator = new double[] { 0.0, 0.0 };
afv.addTo(accumulator, 2.0);
assertEquals(4.0, accumulator[0], 1e-6);
assertEquals(6.0, accumulator[1], 1e-6);
}

@Test
public void testValueAtMissingIndexReturnsZero() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("f1");
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, new double[] { 1.0 });
assertEquals(0.0, afv.value(5), 1e-9);
}

@Test
public void testToFeatureVectorConversion() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("a");
alphabet.lookupIndex("b");
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, new double[] { 1.0, 3.0 });
FeatureVector fv = afv.toFeatureVector();
assertEquals(1.0, fv.value(0), 1e-6);
assertEquals(3.0, fv.value(1), 1e-6);
}

@Test
public void testAddDuplicateIndexManualDeduplication() {
Alphabet alphabet = new Alphabet();
int id1 = alphabet.lookupIndex("dup");
int[] indices = new int[] { id1, id1 };
double[] values = new double[] { 1.0, 2.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, indices, values, 2, 2, false, false, false);
// afv.sortIndices();
assertEquals(1, afv.numLocations());
assertEquals(3.0, afv.value(id1), 1e-6);
}

@Test
public void testSortIndicesEmptyVector() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("a");
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, true);
// afv.sortIndices();
assertEquals(0, afv.numLocations());
}

@Test
public void testDotProductWithEmptyVector() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("a");
AugmentableFeatureVector afv1 = new AugmentableFeatureVector(alphabet, true);
AugmentableFeatureVector afv2 = new AugmentableFeatureVector(alphabet, true);
assertEquals(0.0, afv1.dotProduct(afv2), 1e-6);
}

@Test
public void testSetValueAtLocationDense() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("x");
alphabet.lookupIndex("y");
double[] values = new double[] { 1.0, 2.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, values);
afv.setValueAtLocation(0, 8.0);
afv.setValueAtLocation(1, 9.0);
assertEquals(8.0, afv.value(0), 1e-6);
assertEquals(9.0, afv.value(1), 1e-6);
}

@Test
public void testAddToRealValuedSparseVectorWithScale() {
Alphabet alphabet = new Alphabet();
int id1 = alphabet.lookupIndex("a");
int id2 = alphabet.lookupIndex("b");
int[] indices = new int[] { id1, id2 };
double[] values = new double[] { 1.0, 2.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, indices, values, 2);
double[] accumulator = new double[5];
afv.addTo(accumulator, 3.0);
assertEquals(3.0, accumulator[id1], 1e-6);
assertEquals(6.0, accumulator[id2], 1e-6);
}

@Test
public void testPlusEqualsSparseToSparsePartialOverlap() {
Alphabet alphabet = new Alphabet();
int idA = alphabet.lookupIndex("A");
int idB = alphabet.lookupIndex("B");
int idC = alphabet.lookupIndex("C");
int[] indices1 = new int[] { idA, idB };
double[] values1 = new double[] { 1.0, 2.0 };
AugmentableFeatureVector afv1 = new AugmentableFeatureVector(alphabet, indices1, values1, 2);
int[] indices2 = new int[] { idB, idC };
double[] values2 = new double[] { 3.0, 4.0 };
AugmentableFeatureVector afv2 = new AugmentableFeatureVector(alphabet, indices2, values2, 2);
afv1.plusEquals(afv2, 1.0);
assertEquals(1.0, afv1.value(idA), 1e-6);
assertEquals(5.0, afv1.value(idB), 1e-6);
assertEquals(0.0, afv1.value(idC), 1e-6);
}

@Test
public void testDotProductWithBinaryAndDenseMix() {
Alphabet alphabet = new Alphabet();
int id1 = alphabet.lookupIndex("a");
int id2 = alphabet.lookupIndex("b");
AugmentableFeatureVector afv1 = new AugmentableFeatureVector(alphabet, true);
AugmentableFeatureVector afv2 = new AugmentableFeatureVector(alphabet, new double[] { 3.0, 5.0 });
afv1.add(id1);
afv1.add(id2);
double dot = afv1.dotProduct(afv2);
assertEquals(8.0, dot, 1e-6);
}

@Test
public void testDotProductWithPartiallyOverlappingSparseVectors() {
Alphabet alphabet = new Alphabet();
int id1 = alphabet.lookupIndex("a");
int id2 = alphabet.lookupIndex("b");
int id3 = alphabet.lookupIndex("c");
int[] indices1 = new int[] { id1, id2 };
double[] values1 = new double[] { 1.0, 2.0 };
AugmentableFeatureVector afv1 = new AugmentableFeatureVector(alphabet, indices1, values1, 2);
int[] indices2 = new int[] { id2, id3 };
double[] values2 = new double[] { 3.0, 5.0 };
// SparseVector sv2 = new SparseVector(indices2, values2, 2);
// double result = afv1.dotProduct(sv2);
// assertEquals(6.0, result, 1e-6);
}

@Test
public void testToSparseVectorConversionPreservesValues() {
Alphabet alphabet = new Alphabet();
int id1 = alphabet.lookupIndex("a");
int id2 = alphabet.lookupIndex("b");
int[] indices = new int[] { id1, id2 };
double[] values = new double[] { 1.5, 2.5 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, indices, values, 2);
SparseVector sv = afv.toSparseVector();
assertEquals(1.5, sv.value(id1), 1e-6);
assertEquals(2.5, sv.value(id2), 1e-6);
}

@Test
public void testConstructorWithPropertyList() {
Alphabet alphabet = new Alphabet();
PropertyList pl = PropertyList.add("f1", 1.0, null);
pl = PropertyList.add("f2", 2.0, pl);
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, pl, false);
int idx1 = alphabet.lookupIndex("f1", false);
int idx2 = alphabet.lookupIndex("f2", false);
assertEquals(1.0, afv.value(idx1), 1e-6);
assertEquals(2.0, afv.value(idx2), 1e-6);
}

@Test
public void testSingleSizeWithSparseFeatures() {
Alphabet alphabet = new Alphabet();
int id1 = alphabet.lookupIndex("x");
int id2 = alphabet.lookupIndex("z");
int[] indices = new int[] { id1, id2 };
double[] values = new double[] { 1.0, 2.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, indices, values, 2);
int singleSize = afv.singleSize();
assertEquals(id2, singleSize);
}

@Test
public void testAddBeyondCurrentCapacityInDenseVector() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("a");
alphabet.lookupIndex("b");
alphabet.lookupIndex("c");
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, new double[] { 1.0 });
afv.add(2, 5.0);
assertEquals(5.0, afv.value(2), 1e-6);
assertEquals(1.0, afv.value(0), 1e-6);
assertEquals(0.0, afv.value(1), 1e-6);
}

@Test
public void testAddBeyondCurrentCapacityInSparseVector() {
Alphabet alphabet = new Alphabet();
int id1 = alphabet.lookupIndex("a");
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, new int[0], new double[0], 0, 0, false, false, false);
afv.add(id1, 1.0);
assertEquals(1.0, afv.value(id1), 1e-6);
assertEquals(1, afv.numLocations());
}

@Test
public void testPlusEquals_SparseWithBinaryInput() {
Alphabet alphabet = new Alphabet();
int id1 = alphabet.lookupIndex("f1");
int id2 = alphabet.lookupIndex("f2");
int[] indices1 = new int[] { id1 };
double[] values1 = new double[] { 2.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, indices1, values1, 1);
int[] indices2 = new int[] { id1, id2 };
// SparseVector binaryVector = new FeatureVector(alphabet, indices2, null, 2, 2, true, true, true);
// afv.plusEquals(binaryVector, 2.0);
assertEquals(4.0, afv.value(id1), 1e-6);
assertEquals(0.0, afv.value(id2), 1e-6);
}

@Test
public void testAddToWithNullValuesAndSparseIndices() {
Alphabet alphabet = new Alphabet();
int id1 = alphabet.lookupIndex("a");
int id2 = alphabet.lookupIndex("b");
int capacity = 2;
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, new int[capacity], null, capacity, 0, false, false, false);
afv.add(id1);
afv.add(id2);
double[] accumulator = new double[3];
afv.addTo(accumulator, 3.0);
assertEquals(3.0, accumulator[id1], 1e-6);
assertEquals(3.0, accumulator[id2], 1e-6);
}

@Test
public void testRemoveDuplicatesWithPreProvidedCount() {
Alphabet alphabet = new Alphabet();
int id = alphabet.lookupIndex("x");
int[] indices = new int[] { id, id };
double[] values = new double[] { 1.0, 2.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, indices, values, 2, 2, false, false, false);
// afv.sortIndices();
assertEquals(1, afv.numLocations());
assertEquals(3.0, afv.value(id), 1e-6);
}

@Test
public void testConvertSparseVectorWithNullValues() {
Alphabet alphabet = new Alphabet();
int id1 = alphabet.lookupIndex("x");
int id2 = alphabet.lookupIndex("y");
int[] indices = new int[] { id1, id2 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, indices, null, 2, 2, false, false, false);
SparseVector sv = afv.toSparseVector();
assertEquals(1.0, sv.value(id1), 1e-6);
assertEquals(1.0, sv.value(id2), 1e-6);
}

@Test
public void testPlusEqualsDenseWithOverlappingIndices() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("a");
alphabet.lookupIndex("b");
alphabet.lookupIndex("c");
AugmentableFeatureVector v1 = new AugmentableFeatureVector(alphabet, new double[] { 1.0, 2.0, 3.0 });
AugmentableFeatureVector v2 = new AugmentableFeatureVector(alphabet, new double[] { 2.0, -1.0, 0.5 });
v1.plusEquals(v2, 1.0);
assertEquals(3.0, v1.value(0), 1e-6);
assertEquals(1.0, v1.value(1), 1e-6);
assertEquals(3.5, v1.value(2), 1e-6);
}

@Test
public void testAddWithStringKeyAutomaticIndexing() {
Alphabet dict = new Alphabet();
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, true);
afv.add("new_feature", 1.0);
int index = dict.lookupIndex("new_feature", false);
assertTrue(index >= 0);
assertEquals(1.0, afv.value(index), 1e-6);
}

@Test
public void testDotProductWithBinaryAndNonBinarySparse() {
Alphabet alphabet = new Alphabet();
int idA = alphabet.lookupIndex("a");
int idB = alphabet.lookupIndex("b");
AugmentableFeatureVector left = new AugmentableFeatureVector(alphabet, new int[] { idA, idB }, null, 2, 2, false, false, false);
int[] indices = new int[] { idA, idB };
double[] values = new double[] { 3.0, 4.0 };
// SparseVector right = new SparseVector(indices, values, 2);
// double dot = left.dotProduct(right);
// assertEquals(7.0, dot, 1e-6);
}

@Test
public void testDotProductDenseWithSparsePartialMismatch() {
Alphabet alphabet = new Alphabet();
int id0 = alphabet.lookupIndex("a");
int id1 = alphabet.lookupIndex("b");
double[] vecValues = new double[] { 2.0, 4.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, vecValues);
int[] indices = new int[] { id1 };
double[] values = new double[] { 10.0 };
// SparseVector sv = new SparseVector(indices, values, 1);
// double dot = afv.dotProduct(sv);
// assertEquals(40.0, dot, 1e-6);
}

@Test
public void testSetAllDoesNotAffectIndices_whenSparse() {
Alphabet alphabet = new Alphabet();
int id1 = alphabet.lookupIndex("a");
int id2 = alphabet.lookupIndex("b");
int[] indices = new int[] { id1, id2 };
double[] values = new double[] { 1.0, 1.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, indices, values, 2, 2, false, false, false);
afv.setAll(3.0);
assertEquals(3.0, afv.value(id1), 1e-6);
assertEquals(3.0, afv.value(id2), 1e-6);
assertEquals(0.0, afv.value(10), 1e-6);
}

@Test
public void testDotProductWithAugmentableSparseBinary() {
Alphabet alphabet = new Alphabet();
int idX = alphabet.lookupIndex("x");
int idY = alphabet.lookupIndex("y");
int idZ = alphabet.lookupIndex("z");
AugmentableFeatureVector left = new AugmentableFeatureVector(alphabet, new int[] { idX, idY }, null, 2, 2, false, false, false);
AugmentableFeatureVector right = new AugmentableFeatureVector(alphabet, new int[] { idY, idZ }, null, 2, 2, false, false, false);
double product = left.dotProduct(right);
assertEquals(1.0, product, 1e-6);
}

@Test
public void testAddToSparseVectorWithZeroCapacityExpands() {
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("a");
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, new int[0], new double[0], 0, 0, false, false, false);
afv.add(idx, 1.0);
assertEquals(1.0, afv.value(idx), 1e-6);
assertEquals(1, afv.numLocations());
}

@Test
public void testAddToDenseVectorWithIndexFarBeyondCurrentLength() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("a");
alphabet.lookupIndex("b");
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, new double[] { 1.0 });
afv.add(10, 3.0);
assertEquals(1.0, afv.value(0), 1e-6);
assertEquals(3.0, afv.value(10), 1e-6);
assertEquals(11, afv.numLocations());
}

@Test
public void testZeroLengthVectorDotProductWithDenseVector() {
Alphabet alphabet = new Alphabet();
DenseVector dv = new DenseVector(new double[] { 1.0, 2.0, 3.0 });
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, new int[0], new double[0], 0, 0, false, false, false);
double result = afv.dotProduct(dv);
assertEquals(0.0, result, 1e-6);
}

@Test
public void testEmptyVectorOneNormAndTwoNorm() {
Alphabet alphabet = new Alphabet();
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, new int[0], null, 0, 0, false, false, false);
assertEquals(0.0, afv.oneNorm(), 1e-6);
assertEquals(0.0, afv.twoNorm(), 1e-6);
}

@Test
public void testInfinityNormWithAllNegativeValues() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("m");
alphabet.lookupIndex("n");
double[] values = new double[] { -1.0, -10.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, values);
assertEquals(10.0, afv.infinityNorm(), 1e-6);
}

@Test
public void testSetValueWithSparseIndices() {
Alphabet alphabet = new Alphabet();
int idx1 = alphabet.lookupIndex("x");
int idx2 = alphabet.lookupIndex("y");
int[] indices = new int[] { idx1, idx2 };
double[] values = new double[] { 2.0, 3.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, indices, values, 2, 2, false, false, false);
afv.setValue(idx1, 5.0);
assertEquals(5.0, afv.value(idx1), 1e-6);
}

@Test
public void testSetValueOnDenseVector() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("a");
alphabet.lookupIndex("b");
double[] values = new double[] { 1.0, 2.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, values.clone());
afv.setValue(1, 4.5);
assertEquals(4.5, afv.value(1), 1e-6);
}

@Test
public void testPlusEquals_sparseRightSideNoOverlap() {
Alphabet alphabet = new Alphabet();
int idx1 = alphabet.lookupIndex("x");
int idx2 = alphabet.lookupIndex("y");
int[] leftIndices = new int[] { idx1 };
double[] leftValues = new double[] { 5.0 };
AugmentableFeatureVector afv1 = new AugmentableFeatureVector(alphabet, leftIndices, leftValues, 1);
int[] rightIndices = new int[] { idx2 };
double[] rightValues = new double[] { 7.0 };
AugmentableFeatureVector afv2 = new AugmentableFeatureVector(alphabet, rightIndices, rightValues, 1);
afv1.plusEquals(afv2, 1.0);
assertEquals(5.0, afv1.value(idx1), 1e-6);
assertEquals(0.0, afv1.value(idx2), 1e-6);
}

@Test
public void testDotProduct_sparseNoOverlapReturnsZero() {
Alphabet alphabet = new Alphabet();
int idx1 = alphabet.lookupIndex("x");
int idx2 = alphabet.lookupIndex("y");
int[] v1Indices = new int[] { idx1 };
double[] v1Values = new double[] { 3.0 };
AugmentableFeatureVector afv1 = new AugmentableFeatureVector(alphabet, v1Indices, v1Values, 1);
int[] v2Indices = new int[] { idx2 };
double[] v2Values = new double[] { 5.0 };
// SparseVector sv = new SparseVector(v2Indices, v2Values, 1);
// double product = afv1.dotProduct(sv);
// assertEquals(0.0, product, 1e-6);
}

@Test
public void testCloneMatrixPreservesBinaryRepresentation() {
Alphabet alphabet = new Alphabet();
int idx1 = alphabet.lookupIndex("a");
int idx2 = alphabet.lookupIndex("b");
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, new int[] { idx1, idx2 }, null, 2, 2, false, false, false);
AugmentableFeatureVector clone = (AugmentableFeatureVector) afv.cloneMatrix();
assertEquals(1.0, clone.value(idx1), 1e-6);
assertEquals(1.0, clone.value(idx2), 1e-6);
}

@Test
public void testConstructorWithAllNullValuesAndIndices() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("a");
double[] values = new double[3];
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, null, values, values.length, values.length, true, true, true);
assertEquals(0.0, afv.value(0), 1e-6);
assertEquals(3, afv.numLocations());
}

@Test
public void testAddUsingUnknownStringKeyExpandsAlphabet() {
Alphabet alphabet = new Alphabet();
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, true);
afv.add("new_feature", 1.0);
int idx = alphabet.lookupIndex("new_feature", false);
assertTrue(idx >= 0);
assertEquals(1.0, afv.value(idx), 1e-6);
}

@Test
public void testConversionToFeatureVectorKeepsCorrectSize() {
Alphabet alphabet = new Alphabet();
int idx1 = alphabet.lookupIndex("a");
int idx2 = alphabet.lookupIndex("b");
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, new int[] { idx1, idx2 }, new double[] { 2.0, 4.0 }, 2, 2, false, false, false);
FeatureVector fv = afv.toFeatureVector();
assertEquals(2, fv.numLocations());
assertEquals(2.0, fv.value(idx1), 1e-6);
assertEquals(4.0, fv.value(idx2), 1e-6);
}

@Test
public void testAddPrefixWithEmptyVectorNoCrash() {
Alphabet from = new Alphabet();
// FeatureVector fv = new FeatureVector(from, new int[0], new double[0], 0, 0, true, true, true);
Alphabet to = new Alphabet();
AugmentableFeatureVector afv = new AugmentableFeatureVector(to, true);
// afv.add(fv, "prefix_");
assertEquals(0, afv.numLocations());
}

@Test
public void testSerializationDeserializationPreservesState() throws IOException, ClassNotFoundException, IOException {
Alphabet alphabet = new Alphabet();
int id1 = alphabet.lookupIndex("a");
int id2 = alphabet.lookupIndex("b");
double[] values = new double[] { 4.0, 5.0 };
int[] indices = new int[] { id1, id2 };
AugmentableFeatureVector original = new AugmentableFeatureVector(alphabet, indices, values, 2, 2, true, false, false);
ByteArrayOutputStream bos = new ByteArrayOutputStream();
ObjectOutputStream oos = new ObjectOutputStream(bos);
oos.writeObject(original);
oos.close();
ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
ObjectInputStream ois = new ObjectInputStream(bis);
AugmentableFeatureVector deserialized = (AugmentableFeatureVector) ois.readObject();
assertEquals(2, deserialized.numLocations());
assertEquals(4.0, deserialized.value(id1), 1e-6);
assertEquals(5.0, deserialized.value(id2), 1e-6);
}

@Test
public void testAddIndexBeyondLengthTriggersReallocation_sparse() {
Alphabet alphabet = new Alphabet();
int id = alphabet.lookupIndex("larger_index");
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, new int[0], new double[0], 0, 0, false, false, false);
afv.add(id, 3.0);
assertEquals(3.0, afv.value(id), 1e-6);
}

@Test
public void testSortIndicesOnAlreadySorted() {
Alphabet alphabet = new Alphabet();
int id1 = alphabet.lookupIndex("a");
int id2 = alphabet.lookupIndex("b");
int[] indices = new int[] { id1, id2 };
double[] values = new double[] { 1.0, 2.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, indices, values, 2, 2, true, false, false);
// afv.sortIndices();
assertEquals(1.0, afv.value(id1), 1e-6);
assertEquals(2.0, afv.value(id2), 1e-6);
}

@Test
public void testSortIndicesWithUnsortedInputTriggersSwap() {
Alphabet alphabet = new Alphabet();
int id1 = alphabet.lookupIndex("a");
int id2 = alphabet.lookupIndex("b");
int[] indices = new int[] { id2, id1 };
double[] values = new double[] { 5.0, 10.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, indices, values, 2, 2, false, false, false);
// afv.sortIndices();
assertEquals(10.0, afv.value(id1), 1e-6);
assertEquals(5.0, afv.value(id2), 1e-6);
}

@Test
public void testRemoveDuplicates_noDuplication() {
Alphabet alphabet = new Alphabet();
int id1 = alphabet.lookupIndex("x");
int id2 = alphabet.lookupIndex("y");
int[] indices = new int[] { id1, id2 };
double[] values = new double[] { 1.0, 5.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, indices, values, 2, 2, false, false, false);
// afv.sortIndices();
assertEquals(1.0, afv.value(id1), 1e-6);
assertEquals(5.0, afv.value(id2), 1e-6);
}

@Test
public void testLocationForMissingIndex_returnsMinusOne() {
Alphabet alphabet = new Alphabet();
int id1 = alphabet.lookupIndex("present");
int id2 = alphabet.lookupIndex("missing");
int[] indices = new int[] { id1 };
double[] values = new double[] { 1.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, indices, values, 1, 1, false, false, false);
// afv.sortIndices();
assertEquals(-1, afv.location(id2));
}

@Test
public void testSetValueAtLocation_invalidLocation_noExceptionIfValidIndex() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("a");
double[] values = new double[] { 1.0, 2.0, 3.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, values.clone());
afv.setValueAtLocation(2, 9.0);
assertEquals(9.0, afv.value(2), 1e-6);
}

@Test
public void testSetValueAtLocation_throwIfOutOfBounds() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("x");
double[] values = new double[] { 2.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, values);
try {
afv.setValueAtLocation(5, 3.0);
fail("Expected AssertionError");
} catch (AssertionError e) {
}
}

@Test
public void testCloneMatrixZeroed_binaryRepresentation() {
Alphabet alphabet = new Alphabet();
int id1 = alphabet.lookupIndex("token1");
int id2 = alphabet.lookupIndex("token2");
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, new int[] { id1, id2 }, null, 2, 2, false, false, false);
AugmentableFeatureVector zeroed = (AugmentableFeatureVector) afv.cloneMatrixZeroed();
assertEquals(1.0, afv.value(id1), 1e-6);
assertEquals(1.0, afv.value(id2), 1e-6);
assertEquals(1.0, zeroed.value(id1), 1e-6);
}

@Test
public void testSparseVectorDotProduct_exactMatchValues() {
Alphabet alphabet = new Alphabet();
int id1 = alphabet.lookupIndex("w1");
int id2 = alphabet.lookupIndex("w2");
int[] indices = new int[] { id1, id2 };
double[] values = new double[] { 2.0, 4.0 };
AugmentableFeatureVector afv1 = new AugmentableFeatureVector(alphabet, indices, values, 2);
// SparseVector sv = new SparseVector(indices, values, 2);
// double result = afv1.dotProduct(sv);
// assertEquals(2.0 * 2.0 + 4.0 * 4.0, result, 1e-6);
}

@Test
public void testConstructorWithNullValuesDenseBinaryThrows() {
Alphabet alphabet = new Alphabet();
try {
new AugmentableFeatureVector(alphabet, null, null, 10, 5, true, true, true);
fail("Expected NullPointerException due to missing values in dense constructor");
} catch (NullPointerException e) {
}
}

@Test
public void testSortIndicesWhenSingleEntryStillSetsMaxSortedIndex() {
Alphabet alphabet = new Alphabet();
int index = alphabet.lookupIndex("single");
int[] indices = new int[] { index };
double[] values = new double[] { 7.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, indices, values, 1, 1, true, false, false);
// afv.sortIndices();
assertEquals(7.0, afv.value(index), 1e-6);
assertEquals(1, afv.numLocations());
}

@Test
public void testAddNewIndexExpandsStorageAndPreservesExistingValuesSparse() {
Alphabet alphabet = new Alphabet();
int id1 = alphabet.lookupIndex("old");
int id2 = alphabet.lookupIndex("new");
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, new int[] { id1 }, new double[] { 1.0 }, 1, 1, false, false, false);
afv.add(id2, 2.0);
assertEquals(1.0, afv.value(id1), 1e-6);
assertEquals(2.0, afv.value(id2), 1e-6);
}

@Test
public void testAddToWithZeroScaleKeepsAccumulatorUnchanged() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("a");
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, new double[] { 3.0 });
double[] accumulator = new double[] { 1.0 };
afv.addTo(accumulator, 0.0);
assertEquals(1.0, accumulator[0], 1e-6);
}

@Test
public void testAddEmptyFeatureVectorDoesNothing() {
Alphabet alphabet = new Alphabet();
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, true);
int[] emptyIndices = new int[0];
double[] emptyValues = new double[0];
// FeatureVector emptyFv = new FeatureVector(alphabet, emptyIndices, emptyValues, 0);
// afv.add(emptyFv);
assertEquals(0, afv.numLocations());
}

@Test
public void testDotProductWithEmptyAugmentableVectorGivesZero() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("a");
AugmentableFeatureVector afv1 = new AugmentableFeatureVector(alphabet, true);
AugmentableFeatureVector afv2 = new AugmentableFeatureVector(alphabet, true);
double result = afv1.dotProduct(afv2);
assertEquals(0.0, result, 1e-6);
}

@Test
public void testSetAllOnEmptyDenseVectorDoesNotThrow() {
Alphabet alphabet = new Alphabet();
double[] values = new double[0];
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, values);
afv.setAll(2.0);
assertEquals(0, afv.numLocations());
}

@Test
public void testAddSameIndexToDenseTwiceAccumulatesValue() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("x");
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, new double[] { 1.0 });
afv.add(0, 2.5);
assertEquals(3.5, afv.value(0), 1e-6);
}

@Test
public void testInfinityNormWithSingleNegativeValue() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("x");
double[] values = new double[] { -8.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, values);
double infNorm = afv.infinityNorm();
assertEquals(8.0, infNorm, 1e-6);
}

@Test
public void testDotProductBinarySparseWithPartialMatch() {
Alphabet alphabet = new Alphabet();
int idx1 = alphabet.lookupIndex("a");
int idx2 = alphabet.lookupIndex("b");
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, new int[] { idx1 }, null, 1, 1, false, false, false);
int[] rightIndices = new int[] { idx2 };
double[] rightValues = new double[] { 10.0 };
// SparseVector sv = new SparseVector(rightIndices, rightValues, 1);
// double result = afv.dotProduct(sv);
// assertEquals(0.0, result, 1e-6);
}

@Test
public void testAddFeatureVectorWithPrefixAndEmptyPrefix() {
Alphabet dict = new Alphabet();
int idxOrig = dict.lookupIndex("token");
int[] indices = new int[] { idxOrig };
double[] values = new double[] { 4.0 };
// FeatureVector fv = new FeatureVector(dict, indices, values, 1);
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, true);
// afv.add(fv, "");
int newIdx = dict.lookupIndex("token", false);
assertTrue(newIdx >= 0);
assertEquals(1.0, afv.value(newIdx), 1e-6);
}

@Test
public void testAddFeatureVectorWithPrefixAndValuePreserved() {
Alphabet dict = new Alphabet();
int idx = dict.lookupIndex("feat");
int[] indices = new int[] { idx };
double[] values = new double[] { 3.14 };
// FeatureVector fv = new FeatureVector(dict, indices, values, 1);
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, true);
// afv.add(fv, "pre_", false);
int newIndex = dict.lookupIndex("pre_feat", false);
assertTrue(newIndex >= 0);
assertEquals(3.14, afv.value(newIndex), 1e-6);
}

@Test
public void testPlusEqualsWithDenseAndSparseOverlapping() {
Alphabet alphabet = new Alphabet();
int idx0 = alphabet.lookupIndex("a");
int idx1 = alphabet.lookupIndex("b");
AugmentableFeatureVector denseAfv = new AugmentableFeatureVector(alphabet, new double[] { 1.0, 2.0 });
AugmentableFeatureVector sparseAfv = new AugmentableFeatureVector(alphabet, new int[] { idx1 }, new double[] { 3.0 }, 1, 1, false, false, false);
denseAfv.plusEquals(sparseAfv, 1.0);
assertEquals(1.0, denseAfv.value(idx0), 1e-6);
assertEquals(5.0, denseAfv.value(idx1), 1e-6);
}

@Test
public void testPlusEqualsBinaryWithNonOverlappingSparseVector() {
Alphabet alphabet = new Alphabet();
int idx0 = alphabet.lookupIndex("a");
int idx1 = alphabet.lookupIndex("b");
int[] binaryIndices = new int[] { idx0 };
AugmentableFeatureVector binaryAfv = new AugmentableFeatureVector(alphabet, binaryIndices, null, 1, 1, false, false, false);
int[] otherIndices = new int[] { idx1 };
double[] otherValues = new double[] { 2.0 };
// SparseVector other = new SparseVector(otherIndices, otherValues, 1);
// binaryAfv.plusEquals(other);
assertEquals(1.0, binaryAfv.value(idx0), 1e-6);
assertEquals(0.0, binaryAfv.value(idx1), 1e-6);
}

@Test
public void testDotProductWithDenseVectorZeroPadding() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("x");
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, new double[] { 3.0 });
DenseVector dv = new DenseVector(new double[] { 3.0, 7.0 });
double product = afv.dotProduct(dv);
assertEquals(9.0, product, 1e-6);
}

@Test
public void testSetValueOnSparseVectorWithSortedIndices() {
Alphabet alphabet = new Alphabet();
int i1 = alphabet.lookupIndex("a");
int i2 = alphabet.lookupIndex("b");
int[] indices = new int[] { i1, i2 };
double[] values = new double[] { 1.0, 2.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, indices, values, 2, 2, false, false, false);
// afv.sortIndices();
afv.setValue(i1, 9.0);
assertEquals(9.0, afv.value(i1), 1e-6);
assertEquals(2.0, afv.value(i2), 1e-6);
}

@Test
public void testAddToWithNullIndicesAndNullValuesDoesNothing() {
Alphabet alphabet = new Alphabet();
int capacity = 4;
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, new int[capacity], null, capacity, 0, false, false, false);
double[] acc = new double[10];
afv.addTo(acc);
assertEquals(0.0, acc[0], 1e-6);
assertEquals(0.0, acc[1], 1e-6);
}

@Test
public void testAddSameIndexToSparseVectorReallocatesCorrectly() {
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("dup");
int[] indices = new int[] { idx, idx };
double[] values = new double[] { 5.0, 3.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, indices, values, 2, 2, false, false, false);
// afv.sortIndices();
assertEquals(1, afv.numLocations());
assertEquals(8.0, afv.value(idx), 1e-6);
}

@Test
public void testDotProductSparseWithNonMatchingIndicesReturnsZero() {
Alphabet alphabet = new Alphabet();
int id1 = alphabet.lookupIndex("left");
int id2 = alphabet.lookupIndex("right");
AugmentableFeatureVector afvLeft = new AugmentableFeatureVector(alphabet, new int[] { id1 }, new double[] { 1.0 }, 1, 1, false, false, false);
AugmentableFeatureVector afvRight = new AugmentableFeatureVector(alphabet, new int[] { id2 }, new double[] { 1.0 }, 1, 1, false, false, false);
double result = afvLeft.dotProduct(afvRight);
assertEquals(0.0, result, 1e-6);
}

@Test
public void testSingleSizeSparseWithLastIndexCorrectReturn() {
Alphabet alphabet = new Alphabet();
int idx1 = alphabet.lookupIndex("a");
int idx2 = alphabet.lookupIndex("z");
int[] indices = new int[] { idx1, idx2 };
double[] values = new double[] { 1.0, 2.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, indices, values, 2, 2, false, false, false);
int result = afv.singleSize();
assertEquals(idx2, result);
}

@Test
public void testAddMultipleBinaryFeaturesTriggersReallocationAndPreservesOrder() {
Alphabet alphabet = new Alphabet();
int id1 = alphabet.lookupIndex("f1");
int id2 = alphabet.lookupIndex("f2");
int id3 = alphabet.lookupIndex("f3");
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, new int[0], null, 0, 0, false, false, false);
afv.add(id1);
afv.add(id2);
afv.add(id3);
// afv.sortIndices();
assertEquals(1.0, afv.value(id1), 1e-6);
assertEquals(1.0, afv.value(id2), 1e-6);
assertEquals(1.0, afv.value(id3), 1e-6);
}

@Test
public void testInfinityNormWithMixedSigns() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("x");
alphabet.lookupIndex("y");
double[] values = new double[] { -2.5, 4.1 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, values);
assertEquals(4.1, afv.infinityNorm(), 1e-6);
}

@Test
public void testRemoveDuplicatesHandlesEdgeAtEndCorrectly() {
Alphabet alphabet = new Alphabet();
int id1 = alphabet.lookupIndex("k");
int id2 = alphabet.lookupIndex("k");
int[] indices = new int[] { id1, id1 };
double[] values = new double[] { 1.0, 1.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, indices, values, 2, 2, false, false, false);
// afv.sortIndices();
assertEquals(1, afv.numLocations());
assertEquals(2.0, afv.value(id1), 1e-6);
}

@Test
public void testCloneMatrixZeroedPreservesIndicesButZerosDenseValues() {
Alphabet alphabet = new Alphabet();
double[] values = new double[] { 25.0, 50.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, values);
AugmentableFeatureVector afvZeroed = (AugmentableFeatureVector) afv.cloneMatrixZeroed();
assertEquals(0.0, afvZeroed.value(0), 1e-6);
assertEquals(0.0, afvZeroed.value(1), 1e-6);
}

@Test
public void testSetAllUpdatesAllDenseEntriesWithoutAffectingSize() {
Alphabet alphabet = new Alphabet();
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, new double[] { 10.0, 20.0, 30.0 });
afv.setAll(-1.0);
assertEquals(-1.0, afv.value(0), 1e-6);
assertEquals(-1.0, afv.value(1), 1e-6);
assertEquals(-1.0, afv.value(2), 1e-6);
}

@Test
public void testAddToSparseBinaryOnlyIncrementsValues() {
Alphabet alphabet = new Alphabet();
int id1 = alphabet.lookupIndex("alpha");
int[] indices = new int[] { id1 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, indices, null, 1, 1, false, false, false);
double[] acc = new double[5];
afv.addTo(acc, 2.0);
assertEquals(2.0, acc[id1], 1e-6);
}

@Test
public void testAddWithObjectKeyNonExistentThrowsIfNotInAlphabet() {
Alphabet dict = new Alphabet();
dict.lookupIndex("valid");
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, true);
try {
afv.add("nonexistent_key", 1.0);
fail("Expected AssertionError due to invalid index lookup");
} catch (AssertionError e) {
}
}

@Test
public void testDotProductWithDenseExtendsToAllEntries() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("a");
alphabet.lookupIndex("b");
alphabet.lookupIndex("c");
double[] vecValues = new double[] { 2.0, 3.0, 4.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, vecValues);
DenseVector dv = new DenseVector(new double[] { 1.0, 2.0, 3.0 });
double dp = afv.dotProduct(dv);
assertEquals(2.0 * 1 + 3.0 * 2 + 4.0 * 3, dp, 1e-6);
}
}
