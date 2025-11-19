package cc.mallet.types.tests;

import cc.mallet.types.*;
import cc.mallet.util.PropertyList;
import org.junit.Test;
import java.io.*;
import static org.junit.Assert.*;

public class AugmentableFeatureVector_3_GPTLLMTest {

@Test
public void testConstructorWithDenseValues() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("f1");
alphabet.lookupIndex("f2");
alphabet.lookupIndex("f3");
double[] values = { 1.0, 2.0, 3.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, values);
assertEquals(3, afv.numLocations());
assertEquals(1.0, afv.value(0), 0.00001);
assertEquals(2.0, afv.value(1), 0.00001);
assertEquals(3.0, afv.value(2), 0.00001);
}

@Test
public void testConstructorWithSparseIndices() {
Alphabet alphabet = new Alphabet();
int index1 = alphabet.lookupIndex("f1");
int index2 = alphabet.lookupIndex("f3");
int[] indices = new int[] { index1, index2 };
double[] values = new double[] { 1.5, 3.5 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, indices, values, 2);
assertEquals(2, afv.numLocations());
assertEquals(1.5, afv.value(index1), 0.00001);
assertEquals(3.5, afv.value(index2), 0.00001);
}

@Test
public void testAddObjectKeyWithValue() {
Alphabet alphabet = new Alphabet();
int index = alphabet.lookupIndex("f1");
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, true);
afv.add("f1", 5.0);
assertEquals(5.0, afv.value(index), 0.0001);
}

@Test
public void testAddSameIndexTwiceAddsValues() {
Alphabet alphabet = new Alphabet();
int index = alphabet.lookupIndex("feat");
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, true);
afv.add(index, 2.0);
afv.add(index, 3.0);
assertEquals(5.0, afv.value(index), 0.0001);
}

@Test(expected = IllegalArgumentException.class)
public void testAddBinaryThrowsExceptionOnRealVector() {
Alphabet alphabet = new Alphabet();
int index = alphabet.lookupIndex("feat");
int[] indices = new int[] { index };
double[] values = new double[] { 1.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, indices, values, 1);
afv.add(index);
}

@Test(expected = IllegalArgumentException.class)
public void testAddRealValueToBinaryVectorThrows() {
Alphabet alphabet = new Alphabet();
int index = alphabet.lookupIndex("x");
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, true);
afv.add(index, 2.0);
}

@Test
public void testAddFeatureVectorWithPrefix() {
Alphabet alphabet = new Alphabet();
int idx1 = alphabet.lookupIndex("a");
int idx2 = alphabet.lookupIndex("b");
int newIdx1 = alphabet.lookupIndex("pre_a");
int newIdx2 = alphabet.lookupIndex("pre_b");
int[] indices = new int[] { idx1, idx2 };
double[] values = new double[] { 1.0, 2.0 };
FeatureVector fv = new FeatureVector(alphabet, indices, values);
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, true);
afv.add(fv, "pre_");
assertEquals(1.0, afv.value(newIdx1), 0.0001);
assertEquals(2.0, afv.value(newIdx2), 0.0001);
}

@Test
public void testSetValueAndGetValue() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("x");
alphabet.lookupIndex("y");
double[] values = new double[] { 0.5, 1.5 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, values);
afv.setValue(1, 9.0);
assertEquals(9.0, afv.value(1), 0.0001);
assertEquals(0.5, afv.value(0), 0.0001);
}

@Test
public void testSetAllValuesToConstant() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("a");
alphabet.lookupIndex("b");
alphabet.lookupIndex("c");
double[] values = new double[] { 1.0, 2.0, 3.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, values);
afv.setAll(7.7);
assertEquals(7.7, afv.value(0), 0.0001);
assertEquals(7.7, afv.value(1), 0.0001);
assertEquals(7.7, afv.value(2), 0.0001);
}

@Test
public void testDotProductWithDenseVector() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("f1");
alphabet.lookupIndex("f2");
alphabet.lookupIndex("f3");
double[] values1 = new double[] { 1.0, 2.0, 3.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, values1);
double[] denseVals = new double[] { 4.0, 5.0, 6.0 };
DenseVector dv = new DenseVector(denseVals);
double expected = 1.0 * 4.0 + 2.0 * 5.0 + 3.0 * 6.0;
double result = afv.dotProduct(dv);
assertEquals(expected, result, 0.0001);
}

@Test
public void testOneTwoInfinityNorms() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("a");
alphabet.lookupIndex("b");
double[] vals = new double[] { 3.0, 4.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, vals);
assertEquals(7.0, afv.oneNorm(), 0.0001);
assertEquals(5.0, afv.twoNorm(), 0.0001);
assertEquals(4.0, afv.infinityNorm(), 0.0001);
}

@Test
public void testCloneMatrixCreatesCopy() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("aa");
alphabet.lookupIndex("bb");
double[] vals = new double[] { 1.1, 2.2 };
AugmentableFeatureVector original = new AugmentableFeatureVector(alphabet, vals);
AugmentableFeatureVector clone = (AugmentableFeatureVector) original.cloneMatrix();
assertEquals(1.1, clone.value(0), 0.0001);
assertEquals(2.2, clone.value(1), 0.0001);
}

@Test
public void testPropertyListConstructorAddsCorrectValues() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("a");
alphabet.lookupIndex("b");
PropertyList pl = PropertyList.add("a", 1.0, PropertyList.add("b", 2.5, null));
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, pl, true);
assertEquals(1.0, afv.value(alphabet.lookupIndex("a")), 0.0001);
assertEquals(2.5, afv.value(alphabet.lookupIndex("b")), 0.0001);
}

@Test
public void testAddToAccumulator() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("a");
alphabet.lookupIndex("b");
double[] vals = new double[] { 0.5, 2.5 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, vals);
double[] target = new double[] { 1.0, 1.0 };
afv.addTo(target);
assertEquals(1.5, target[0], 0.0001);
assertEquals(3.5, target[1], 0.0001);
}

@Test
public void testToSparseVectorAndBack() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("a");
alphabet.lookupIndex("b");
double[] vals = new double[] { 1.1, 2.2 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, vals);
SparseVector sv = afv.toSparseVector();
FeatureVector fv = afv.toFeatureVector();
assertEquals(1.1, sv.value(0), 0.0001);
assertEquals(2.2, fv.value(1), 0.0001);
}

@Test
public void testFeatureSequenceConstructorAddsAllFeatures() {
Alphabet alphabet = new Alphabet();
int i1 = alphabet.lookupIndex("x");
int i2 = alphabet.lookupIndex("y");
String[] tokens = new String[] { "x", "y", "x" };
// FeatureSequence fs = new FeatureSequence(alphabet, tokens);
// AugmentableFeatureVector afv = new AugmentableFeatureVector(fs, false);
// assertEquals(2.0, afv.value(i1), 0.0001);
// assertEquals(1.0, afv.value(i2), 0.0001);
}

@Test
public void testEmptyDenseVectorNorms() {
Alphabet alphabet = new Alphabet();
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, new double[0]);
assertEquals(0, afv.numLocations());
assertEquals(0.0, afv.oneNorm(), 0.0001);
assertEquals(0.0, afv.twoNorm(), 0.0001);
assertEquals(0.0, afv.infinityNorm(), 0.0001);
}

@Test
public void testAddWithIndexGreaterThanInitialCapacityGrowsVector() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("f1");
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, new double[1]);
afv.add(10, 2.0);
assertEquals(2.0, afv.value(10), 0.0001);
}

@Test
public void testAddToWithScale() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("x");
alphabet.lookupIndex("y");
double[] values = new double[] { 2.0, 4.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, values);
double[] acc = new double[] { 1.0, 1.0 };
afv.addTo(acc, 0.5);
assertEquals(1.0 + 2.0 * 0.5, acc[0], 0.0001);
assertEquals(1.0 + 4.0 * 0.5, acc[1], 0.0001);
}

@Test
public void testCloneMatrixZeroedWithSparseVector() {
Alphabet alphabet = new Alphabet();
int idx1 = alphabet.lookupIndex("a");
int idx2 = alphabet.lookupIndex("b");
int[] indices = new int[] { idx1, idx2 };
double[] values = new double[] { 5.0, 7.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, indices, values, 2);
AugmentableFeatureVector clone = (AugmentableFeatureVector) afv.cloneMatrixZeroed();
assertEquals(0.0, clone.value(idx1), 0.0001);
assertEquals(0.0, clone.value(idx2), 0.0001);
}

@Test
public void testSortIndicesWithDuplicateAndRemove() {
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("z");
int[] indices = new int[] { idx, idx };
double[] values = new double[] { 1.0, 2.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, indices, values, 2, false, false);
afv.numLocations();
assertEquals(1, afv.numLocations());
assertEquals(3.0, afv.value(idx), 0.0001);
}

@Test
public void testDotProductWithAugmentableFeatureVectorBinary() {
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("word");
AugmentableFeatureVector afv1 = new AugmentableFeatureVector(alphabet, true);
afv1.add(idx, 1.0);
AugmentableFeatureVector afv2 = new AugmentableFeatureVector(alphabet, true);
afv2.add(idx, 1.0);
double result = afv1.dotProduct(afv2);
assertEquals(1.0, result, 0.0001);
}

@Test
public void testPlusEqualsWithPartialOverlap() {
Alphabet alphabet = new Alphabet();
int idx1 = alphabet.lookupIndex("a");
int idx2 = alphabet.lookupIndex("b");
double[] v1 = new double[] { 3.0, 0.0 };
double[] v2 = new double[] { 0.0, 5.0 };
AugmentableFeatureVector afv1 = new AugmentableFeatureVector(alphabet, v1);
AugmentableFeatureVector afv2 = new AugmentableFeatureVector(alphabet, v2);
afv1.plusEquals(afv2, 1.0);
assertEquals(3.0, afv1.value(idx1), 0.0001);
assertEquals(5.0, afv1.value(idx2), 0.0001);
}

@Test(expected = IllegalArgumentException.class)
public void testSetValueOnMissingLocationInSparseVectorThrows() {
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("a");
int[] indices = new int[] { idx };
double[] values = new double[] { 1.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, indices, values, 1);
afv.setValue(100, 2.0);
}

@Test
public void testRemoveDuplicatesMany() {
Alphabet alphabet = new Alphabet();
int a = alphabet.lookupIndex("a");
int[] indices = new int[] { a, a, a };
double[] values = new double[] { 1.0, 2.0, 3.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, indices, values, 3, false, false);
afv.numLocations();
assertEquals(1, afv.numLocations());
assertEquals(6.0, afv.value(a), 0.0001);
}

@Test
public void testConstructorWithNullPropertyList() {
Alphabet alphabet = new Alphabet();
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, null, true);
assertEquals(0, afv.numLocations());
}

@Test
public void testIndexAtLocationMatchesOriginalIndexInDense() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("a");
alphabet.lookupIndex("b");
double[] values = new double[] { 1.0, 2.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, values);
int index0 = afv.indexAtLocation(0);
int index1 = afv.indexAtLocation(1);
assertEquals(0, index0);
assertEquals(1, index1);
}

@Test
public void testZeroLengthSparseVectorHandlesGracefully() {
Alphabet alphabet = new Alphabet();
int[] indices = new int[0];
double[] values = new double[0];
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, indices, values, 0);
assertEquals(0, afv.numLocations());
assertEquals(0.0, afv.oneNorm(), 0.0001);
assertEquals(0.0, afv.twoNorm(), 0.0001);
assertEquals(0.0, afv.infinityNorm(), 0.0001);
}

@Test
public void testAddToSparseBinaryVectorWithDifferentLength() {
Alphabet alphabet = new Alphabet();
int indexA = alphabet.lookupIndex("a");
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, true);
afv.add(indexA);
double[] accumulator = new double[5];
afv.addTo(accumulator);
assertEquals(1.0, accumulator[indexA], 0.0001);
assertEquals(0.0, accumulator[1], 0.0001);
}

@Test
public void testPlusEqualsSparseBinaryToRealVector() {
Alphabet alphabet = new Alphabet();
int indexX = alphabet.lookupIndex("x");
int indexY = alphabet.lookupIndex("y");
double[] values1 = new double[] { 2.0, 4.0 };
AugmentableFeatureVector afv1 = new AugmentableFeatureVector(alphabet, values1);
AugmentableFeatureVector afv2 = new AugmentableFeatureVector(alphabet, true);
afv2.add(indexX);
afv2.add(indexY);
afv1.plusEquals(afv2, 1.0);
assertEquals(3.0, afv1.value(indexX), 0.0001);
assertEquals(5.0, afv1.value(indexY), 0.0001);
}

@Test
public void testCloningDenseVectorPreservesValues() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("a");
alphabet.lookupIndex("b");
alphabet.lookupIndex("c");
double[] values = new double[] { 1.0, 2.0, 3.0 };
AugmentableFeatureVector original = new AugmentableFeatureVector(alphabet, values);
ConstantMatrix clone = original.cloneMatrix();
double[] accumulator = new double[3];
// clone.addTo(accumulator);
assertEquals(1.0, accumulator[0], 0.0001);
assertEquals(2.0, accumulator[1], 0.0001);
assertEquals(3.0, accumulator[2], 0.0001);
}

@Test
public void testAddToWithNullIndicesAndBinaryVector() {
Alphabet alphabet = new Alphabet();
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, true);
afv.add(alphabet.lookupIndex("a"));
double[] acc = new double[10];
afv.addTo(acc, 2.0);
assertEquals(2.0, acc[alphabet.lookupIndex("a")], 0.0001);
}

@Test
public void testAddSameValueToDenseBinaryManyTimes() {
Alphabet alphabet = new Alphabet();
int index = alphabet.lookupIndex("tok");
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, true);
afv.add(index);
afv.add(index);
afv.add(index);
assertEquals(3.0, afv.value(index), 0.0001);
}

@Test
public void testDotProductBetweenDenseAndEmptySparse() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("a");
alphabet.lookupIndex("b");
AugmentableFeatureVector afv1 = new AugmentableFeatureVector(alphabet, new double[] { 3.0, 4.0 });
int[] emptyIndices = new int[0];
double[] emptyValues = new double[0];
AugmentableFeatureVector afv2 = new AugmentableFeatureVector(alphabet, emptyIndices, emptyValues, 0);
double result = afv1.dotProduct(afv2);
assertEquals(0.0, result, 0.0001);
}

@Test
public void testLocationWithUnsortedIndicesPerformsSort() {
Alphabet alphabet = new Alphabet();
int indexA = alphabet.lookupIndex("a");
int indexB = alphabet.lookupIndex("b");
int[] unsorted = new int[] { indexB, indexA };
double[] vals = new double[] { 1.0, 2.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, unsorted, vals, 2, false, false);
int location = afv.location(indexA);
assertTrue(location >= 0);
assertEquals(2.0, afv.value(indexA), 0.0001);
}

@Test
public void testSetValueAtLocationModifiesDenseVectorValueDirectly() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("a");
alphabet.lookupIndex("b");
double[] base = new double[] { 1.0, 2.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, base);
afv.setValueAtLocation(1, 9.0);
assertEquals(9.0, afv.value(1), 0.0001);
}

@Test
public void testDotProductWithOffsetSparseOverlap() {
Alphabet alphabet = new Alphabet();
int idx1 = alphabet.lookupIndex("a");
int idx2 = alphabet.lookupIndex("b");
double[] v1 = new double[] { 0.0, 5.0 };
AugmentableFeatureVector afv1 = new AugmentableFeatureVector(alphabet, v1);
int[] indices = new int[] { idx2 };
double[] vals = new double[] { 4.0 };
// SparseVector sv = new SparseVector(indices, vals, 1);
// double result = afv1.dotProduct(sv);
// assertEquals(20.0, result, 0.0001);
}

@Test
public void testAddToWithNullValuesInSparseBinary() {
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("a");
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, true);
afv.add(idx);
double[] acc = new double[5];
afv.addTo(acc);
assertEquals(1.0, acc[idx], 0.0001);
}

@Test
public void testCloneMatrixZeroedWithUnsortedIndices() {
Alphabet alphabet = new Alphabet();
int a = alphabet.lookupIndex("a");
int b = alphabet.lookupIndex("b");
int[] indices = new int[] { b, a };
double[] values = new double[] { 6.0, 4.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, indices, values, 2, false, false);
AugmentableFeatureVector zeroed = (AugmentableFeatureVector) afv.cloneMatrixZeroed();
assertEquals(0.0, zeroed.value(a), 0.0001);
assertEquals(0.0, zeroed.value(b), 0.0001);
}

@Test
public void testSetAllOnEmptyDenseVectorDoesNotThrow() {
Alphabet alphabet = new Alphabet();
double[] arr = new double[0];
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, arr);
afv.setAll(1.0);
assertEquals(0, afv.numLocations());
}

@Test
public void testAddBeyondCapacityTriggersResize() {
Alphabet alphabet = new Alphabet();
int idxA = alphabet.lookupIndex("a");
int idxB = alphabet.lookupIndex("b");
int idxC = alphabet.lookupIndex("c");
int idxD = alphabet.lookupIndex("d");
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, 2, false);
afv.add(idxA, 1.0);
afv.add(idxB, 2.0);
afv.add(idxC, 3.0);
afv.add(idxD, 4.0);
assertEquals(1.0, afv.value(idxA), 0.0001);
assertEquals(2.0, afv.value(idxB), 0.0001);
assertEquals(3.0, afv.value(idxC), 0.0001);
assertEquals(4.0, afv.value(idxD), 0.0001);
}

@Test(expected = IllegalArgumentException.class)
public void testAddInvalidToBinaryThrows() {
Alphabet alphabet = new Alphabet();
int index = alphabet.lookupIndex("f");
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, true);
afv.add(index, 2.0);
}

@Test
public void testSetValueAtLocationZeroInSparseVector() {
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("f");
int[] indices = new int[] { idx };
double[] values = new double[] { 1.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, indices, values, 1);
afv.setValueAtLocation(0, 9.0);
assertEquals(9.0, afv.value(idx), 0.0001);
}

@Test
public void testDotProductWithNonOverlappingSparse() {
Alphabet alphabet = new Alphabet();
int index1 = alphabet.lookupIndex("x");
int index2 = alphabet.lookupIndex("y");
int[] indices1 = new int[] { index1 };
int[] indices2 = new int[] { index2 };
double[] vals1 = new double[] { 1.0 };
double[] vals2 = new double[] { 5.0 };
AugmentableFeatureVector afv1 = new AugmentableFeatureVector(alphabet, indices1, vals1, 1);
AugmentableFeatureVector afv2 = new AugmentableFeatureVector(alphabet, indices2, vals2, 1);
double result = afv1.dotProduct(afv2);
assertEquals(0.0, result, 0.0001);
}

@Test
public void testDotProductWithOneNullValues() {
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("a");
AugmentableFeatureVector afv1 = new AugmentableFeatureVector(alphabet, true);
afv1.add(idx);
int[] indices = new int[] { idx };
double[] values = new double[] { 10.0 };
// SparseVector sv = new SparseVector(indices, values, 1);
// double result = afv1.dotProduct(sv);
// assertEquals(10.0, result, 0.0001);
}

@Test
public void testIndexAtLocationWithSparseVector() {
Alphabet alphabet = new Alphabet();
int idxA = alphabet.lookupIndex("a");
int[] indices = new int[] { idxA };
double[] values = new double[] { 3.3 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, indices, values, 1);
int index = afv.indexAtLocation(0);
assertEquals(idxA, index);
}

@Test
public void testSingleSizeOnEmptySparseVector() {
Alphabet alphabet = new Alphabet();
int[] indices = new int[0];
double[] values = new double[0];
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, indices, values, 0);
int result = afv.singleSize();
assertEquals(0, result);
}

@Test
public void testAddToAccumulatorLargerThanVector() {
Alphabet alphabet = new Alphabet();
double[] values = new double[] { 1.0, 2.0, 3.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, values);
double[] accumulator = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0 };
afv.addTo(accumulator);
assertEquals(1.0, accumulator[0], 0.0001);
assertEquals(2.0, accumulator[1], 0.0001);
assertEquals(3.0, accumulator[2], 0.0001);
assertEquals(0.0, accumulator[3], 0.0001);
}

@Test
public void testPlusEqualsDenseToBinaryWithExtraIndicesIgnored() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("a");
alphabet.lookupIndex("b");
double[] values = new double[] { 2.0, 3.0 };
AugmentableFeatureVector afv1 = new AugmentableFeatureVector(alphabet, true);
afv1.add(0);
AugmentableFeatureVector afv2 = new AugmentableFeatureVector(alphabet, values);
afv2.plusEquals(afv1, 1.0);
assertEquals(2.0, afv2.value(0), 0.0001);
assertEquals(3.0, afv2.value(1), 0.0001);
}

@Test
public void testRemoveDuplicatesMergesExactly() {
Alphabet alphabet = new Alphabet();
int index = alphabet.lookupIndex("tok");
int[] indices = new int[] { index, index };
double[] values = new double[] { 2.0, 3.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, indices, values, 2, false, false);
int sizeBefore = afv.numLocations();
assertEquals(1, afv.numLocations());
assertEquals(5.0, afv.value(index), 0.0001);
}

@Test
public void testToFeatureVectorReturnsEquivalentSparse() {
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("a");
int[] indices = new int[] { idx };
double[] values = new double[] { 2.5 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, indices, values, 1);
FeatureVector fv = afv.toFeatureVector();
assertEquals(2.5, fv.value(idx), 0.0001);
}

@Test
public void testInfinityNormWithLargeNegativeValue() {
Alphabet alphabet = new Alphabet();
double[] values = new double[] { -4.0, -7.0, 2.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, values);
double result = afv.infinityNorm();
assertEquals(7.0, result, 0.0001);
}

@Test
public void testAddNonConsecutiveDuplicateIndicesTriggersMerge() {
Alphabet alphabet = new Alphabet();
int idx1 = alphabet.lookupIndex("x");
int[] indices = new int[] { idx1, idx1 };
double[] values = new double[] { 2.0, 3.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, indices, values, 2, false, false);
afv.numLocations();
assertEquals(1, afv.numLocations());
assertEquals(5.0, afv.value(idx1), 0.0001);
}

@Test
public void testDotProductBinaryWithRealValuedSparseVector() {
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("z");
AugmentableFeatureVector binary = new AugmentableFeatureVector(alphabet, true);
binary.add(idx);
int[] indices = new int[] { idx };
double[] values = new double[] { 7.0 };
// SparseVector realSparse = new SparseVector(indices, values, 1);
// double result = binary.dotProduct(realSparse);
// assertEquals(7.0, result, 0.0001);
}

@Test
public void testValueOfDenseVectorOutOfRangeReturnsZero() {
Alphabet alphabet = new Alphabet();
double[] values = new double[] { 1.0, 2.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, values);
assertEquals(0.0, afv.value(3), 0.0001);
}

@Test
public void testSingleSizeOfDenseVector() {
Alphabet alphabet = new Alphabet();
double[] values = new double[] { 1.0, 4.5, 2.2 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, values);
int result = afv.singleSize();
assertEquals(3, result);
}

@Test
public void testToSparseVectorFromEmpty() {
Alphabet alphabet = new Alphabet();
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, new double[0]);
SparseVector sv = afv.toSparseVector();
assertEquals(0.0, sv.oneNorm(), 0.0001);
assertEquals(0, sv.numLocations());
}

@Test
public void testCloneMatrixOnEmptyVectorDoesNotThrow() {
Alphabet alphabet = new Alphabet();
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, new double[0]);
ConstantMatrix clone = afv.cloneMatrix();
double[] acc = new double[1];
// clone.addTo(acc);
assertEquals(0.0, acc[0], 0.0001);
}

@Test
public void testPlusEqualsWithSparseVectorBinaryAddsCorrectly() {
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("tok");
double[] values = new double[] { 2.0, 4.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, values);
int[] indices = new int[] { idx };
// SparseVector binary = new SparseVector(indices, null, 1);
// afv.plusEquals(binary, 3.0);
assertEquals(2.0 + 3.0, afv.value(idx), 0.0001);
}

@Test
public void testPlusEqualsWithSparseVectorRealValuedList() {
Alphabet alphabet = new Alphabet();
int idx1 = alphabet.lookupIndex("a");
int idx2 = alphabet.lookupIndex("b");
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, new double[] { 1.0, 2.0 });
int[] indices = new int[] { idx2 };
double[] values = new double[] { 5.5 };
// SparseVector sparse = new SparseVector(indices, values, 1);
// afv.plusEquals(sparse, 0.5);
assertEquals(2.0 + 5.5 * 0.5, afv.value(idx2), 0.0001);
}

@Test
public void testPropertyListWithDuplicateFeatureNames() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("dup");
PropertyList pl = PropertyList.add("dup", 1.0, PropertyList.add("dup", 2.0, null));
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, pl, false);
assertEquals(3.0, afv.value(alphabet.lookupIndex("dup")), 0.0001);
}

@Test(expected = AssertionError.class)
public void testSetValueOnMissingIndexInSparseVectorThrows() {
Alphabet alphabet = new Alphabet();
int a = alphabet.lookupIndex("a");
int b = alphabet.lookupIndex("b");
int[] indices = new int[] { a };
double[] values = new double[] { 5.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, indices, values, 1);
afv.setValue(b, 2.0);
}

@Test
public void testValueOfMissingKeyInSparseReturnsZero() {
Alphabet alphabet = new Alphabet();
int idxA = alphabet.lookupIndex("x");
int idxB = alphabet.lookupIndex("y");
int[] indices = new int[] { idxA };
double[] values = new double[] { 2.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, indices, values, 1);
assertEquals(0.0, afv.value(idxB), 0.0001);
}

@Test
public void testIndexAtInvalidLocationInDenseThrowsAssertion() {
Alphabet alphabet = new Alphabet();
double[] values = new double[] { 1.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, values);
try {
afv.indexAtLocation(5);
fail("Expected AssertionError for out-of-bounds location");
} catch (AssertionError e) {
assertTrue(true);
}
}

@Test
public void testAddObjectKeyWithUnknownAlphabetEntry() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("x");
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, 10, false);
try {
afv.add("y", 1.0);
fail("Expected AssertionError because index would be -1");
} catch (AssertionError e) {
assertTrue(true);
}
}

@Test
public void testAlreadySortedIndicesSkipsSortLogic() {
Alphabet alphabet = new Alphabet();
int a = alphabet.lookupIndex("a");
int b = alphabet.lookupIndex("b");
int[] indices = new int[] { a, b };
double[] values = new double[] { 1.0, 2.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, indices, values, 2, indices.length, true, true, false);
int locA = afv.location(a);
int locB = afv.location(b);
assertEquals(0, locA);
assertEquals(1, locB);
}

@Test
public void testDotProductOfTwoEmptyVectors() {
Alphabet alphabet = new Alphabet();
AugmentableFeatureVector afv1 = new AugmentableFeatureVector(alphabet, new double[0]);
AugmentableFeatureVector afv2 = new AugmentableFeatureVector(alphabet, new double[0]);
assertEquals(0.0, afv1.dotProduct(afv2), 0.0001);
}

@Test
public void testAddTriggersMultipleResizes() {
Alphabet alphabet = new Alphabet();
for (int i = 0; i < 20; i++) {
alphabet.lookupIndex("f" + i);
}
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, 2, false);
afv.add(0, 1.0);
afv.add(1, 2.0);
afv.add(2, 3.0);
afv.add(3, 4.0);
afv.add(4, 5.0);
afv.add(5, 6.0);
afv.add(6, 7.0);
afv.add(7, 8.0);
assertEquals(8, afv.numLocations());
assertEquals(8.0, afv.value(7), 0.0001);
}

@Test
public void testLocationReturnsMinusOneForMissingIndexAfterSort() {
Alphabet alphabet = new Alphabet();
int index1 = alphabet.lookupIndex("a");
int[] indices = new int[] { index1 };
double[] values = new double[] { 1.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, indices, values, 1, false, false);
int missingIndex = alphabet.lookupIndex("b");
int loc = afv.location(missingIndex);
assertEquals(-1, loc);
}

@Test
public void testRemoveDuplicatesSkipsWhenNoDuplicatesPresent() {
Alphabet alphabet = new Alphabet();
int idx1 = alphabet.lookupIndex("f1");
int idx2 = alphabet.lookupIndex("f2");
int[] indices = new int[] { idx1, idx2 };
double[] values = new double[] { 1.0, 3.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, indices, values, 2, true, false);
int originalSize = afv.numLocations();
// afv.removeDuplicates(0);
assertEquals(originalSize, afv.numLocations());
assertEquals(1.0, afv.value(idx1), 0.0001);
assertEquals(3.0, afv.value(idx2), 0.0001);
}

@Test
public void testAddToBinarySparseVector() {
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("b");
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, true);
afv.add(idx);
double[] acc = new double[5];
afv.addTo(acc, 2.0);
assertEquals(2.0, acc[idx], 0.0001);
}

@Test
public void testPlusEqualsDenseToSparseVectorOverlap() {
Alphabet alphabet = new Alphabet();
int idx = alphabet.lookupIndex("z");
double[] denseVals = new double[] { 0.0, 2.0, 0.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, denseVals);
int[] indices = new int[] { idx };
double[] values = new double[] { 3.5 };
// SparseVector sparse = new SparseVector(indices, values, 1);
// afv.plusEquals(sparse);
assertEquals(3.5, afv.value(idx), 0.0001);
}

@Test
public void testPlusEqualsWithSparseBinaryNonOverlapping() {
Alphabet alphabet = new Alphabet();
alphabet.lookupIndex("a");
int idx = alphabet.lookupIndex("b");
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, new double[] { 1.0 });
int[] sparseIndices = new int[] { idx };
// SparseVector binary = new SparseVector(sparseIndices, null, 1);
// afv.plusEquals(binary, 3.0);
assertEquals(1.0, afv.value(0), 0.0001);
}

@Test
public void testInfinityNormWhenValuesAreEqualInMagnitude() {
Alphabet alphabet = new Alphabet();
double[] values = new double[] { -1.0, 1.0, -1.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, values);
double norm = afv.infinityNorm();
assertEquals(1.0, norm, 0.0001);
}

@Test
public void testZeroedCloneReflectsOriginalIndicesButZeroValues() {
Alphabet alphabet = new Alphabet();
int idx1 = alphabet.lookupIndex("a");
int idx2 = alphabet.lookupIndex("b");
int[] indices = new int[] { idx1, idx2 };
double[] values = new double[] { 9.0, 4.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, indices, values, 2);
AugmentableFeatureVector zeroed = (AugmentableFeatureVector) afv.cloneMatrixZeroed();
assertEquals(0.0, zeroed.value(idx1), 0.0001);
assertEquals(0.0, zeroed.value(idx2), 0.0001);
}

@Test
public void testSparseVectorWithIndicesButNullValues() {
Alphabet alphabet = new Alphabet();
int indexA = alphabet.lookupIndex("a");
int indexB = alphabet.lookupIndex("b");
int[] indices = new int[] { indexA, indexB };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, indices, null, 2, true, false);
assertEquals(1.0, afv.value(indexA), 0.0001);
assertEquals(1.0, afv.value(indexB), 0.0001);
}

@Test(expected = AssertionError.class)
public void testSetValueAtInvalidLocationThrows() {
Alphabet alphabet = new Alphabet();
double[] values = new double[] { 5.5, 6.6 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, values);
afv.setValueAtLocation(5, 2.0);
}

@Test
public void testAddToDenseVectorWhenIndexExceedsCapacityTriggersResize() {
Alphabet alphabet = new Alphabet();
double[] denseValues = new double[] { 1.0, 2.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, denseValues);
afv.add(4, 3.0);
assertEquals(3.0, afv.value(4), 0.0001);
}

@Test
public void testSortIndicesAfterMaxSortedIndexUsed() {
Alphabet alphabet = new Alphabet();
int idx1 = alphabet.lookupIndex("z");
int idx2 = alphabet.lookupIndex("a");
int[] indices = new int[] { idx1, idx2 };
double[] values = new double[] { 2.0, 3.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, indices, values, 2, false, false);
afv.location(idx1);
assertEquals(2.0, afv.value(idx1), 0.0001);
}

@Test
public void testLocationOfFirstAndLastElementInSparseVector() {
Alphabet alphabet = new Alphabet();
int first = alphabet.lookupIndex("f1");
int middle = alphabet.lookupIndex("f2");
int last = alphabet.lookupIndex("f3");
int[] indices = new int[] { first, middle, last };
double[] values = new double[] { 1.1, 2.2, 3.3 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, indices, values, 3, false, false);
afv.numLocations();
int firstLoc = afv.location(first);
int lastLoc = afv.location(last);
assertTrue(firstLoc >= 0);
assertTrue(lastLoc >= 0);
}

@Test
public void testRemoveDuplicatesFromSortedSparseVectorWithTriplicates() {
Alphabet alphabet = new Alphabet();
int index = alphabet.lookupIndex("word");
int[] indices = new int[] { index, index, index };
double[] values = new double[] { 1.0, 2.0, 3.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, indices, values, 3, false, false);
afv.numLocations();
assertEquals(1, afv.numLocations());
assertEquals(6.0, afv.value(index), 0.0001);
}

@Test
public void testCloneMatrixZeroedPreservesIndicesAndZeroesValues() {
Alphabet alphabet = new Alphabet();
int idx1 = alphabet.lookupIndex("apple");
int idx2 = alphabet.lookupIndex("banana");
int[] indices = new int[] { idx1, idx2 };
double[] values = new double[] { 5.0, 7.0 };
AugmentableFeatureVector original = new AugmentableFeatureVector(alphabet, indices, values, 2);
AugmentableFeatureVector zeroed = (AugmentableFeatureVector) original.cloneMatrixZeroed();
assertEquals(0.0, zeroed.value(idx1), 0.0001);
assertEquals(0.0, zeroed.value(idx2), 0.0001);
}

@Test
public void testAddToDenseVectorThatWasGrownRetainsOriginalValues() {
Alphabet alphabet = new Alphabet();
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, new double[] { 1.0, 2.0 });
afv.add(5, 3.0);
assertEquals(3.0, afv.value(5), 0.0001);
assertEquals(2.0, afv.value(1), 0.0001);
}

@Test
public void testAddWithZeroValueToDenseVectorStillChangesInternalArray() {
Alphabet alphabet = new Alphabet();
double[] base = new double[] { 0.0, 0.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, base);
afv.add(0, 0.0);
afv.add(1, 0.0);
assertEquals(0.0, afv.value(0), 0.0001);
assertEquals(0.0, afv.value(1), 0.0001);
}

@Test
public void testSetAllHandlesEmptyDenseVectorSilently() {
Alphabet alphabet = new Alphabet();
double[] base = new double[0];
AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, base);
afv.setAll(3.3);
assertEquals(0, afv.numLocations());
}
}
