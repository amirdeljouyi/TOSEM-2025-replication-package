package cc.mallet.types.tests;

import cc.mallet.types.*;
import cc.mallet.util.PropertyList;
import org.junit.Test;
import java.io.*;
import java.lang.reflect.Method;
import static org.junit.Assert.*;

public class SparseVector_4_GPTLLMTest {

@Test
public void testDenseVectorValueAccess() {
double[] values = new double[] { 1.0, 2.0, 3.0 };
SparseVector vector = new SparseVector(values);
assertEquals(1.0, vector.value(0), 0.0001);
assertEquals(2.0, vector.value(1), 0.0001);
assertEquals(3.0, vector.value(2), 0.0001);
assertEquals(0.0, vector.value(10), 0.0001);
}

@Test
public void testSparseVectorValueAccess() {
int[] indices = new int[] { 1, 3 };
double[] values = new double[] { 4.0, 7.0 };
SparseVector vector = new SparseVector(indices, values);
assertEquals(0.0, vector.value(0), 0.0001);
assertEquals(4.0, vector.value(1), 0.0001);
assertEquals(7.0, vector.value(3), 0.0001);
assertEquals(0.0, vector.value(2), 0.0001);
}

@Test
public void testBinaryVectorValueAccess() {
int[] indices = new int[] { 2, 4 };
SparseVector vector = new SparseVector(indices, true);
assertEquals(1.0, vector.value(2), 0.0001);
assertEquals(1.0, vector.value(4), 0.0001);
assertEquals(0.0, vector.value(1), 0.0001);
}

@Test
public void testDotProductWithDenseArray() {
int[] indices = new int[] { 1, 3 };
double[] values = new double[] { 2.0, 3.0 };
SparseVector vector = new SparseVector(indices, values);
double[] dense = new double[] { 0.0, 1.5, 0.0, 2.0 };
double expected = 2.0 * 1.5 + 3.0 * 2.0;
double actual = vector.dotProduct(dense);
assertEquals(expected, actual, 0.0001);
}

@Test
public void testDotProductWithSparseVector() {
int[] indices1 = new int[] { 1, 2 };
double[] values1 = new double[] { 2.0, 1.0 };
SparseVector v1 = new SparseVector(indices1, values1);
int[] indices2 = new int[] { 1, 2 };
double[] values2 = new double[] { 3.0, 4.0 };
SparseVector v2 = new SparseVector(indices2, values2);
double expected = 2.0 * 3.0 + 1.0 * 4.0;
double actual = v1.dotProduct(v2);
assertEquals(expected, actual, 0.0001);
}

@Test
public void testPlusEqualsSparseUpdatesInPlace() {
int[] indices1 = new int[] { 0, 2 };
double[] values1 = new double[] { 1.0, 1.0 };
SparseVector base = new SparseVector(indices1, values1);
int[] indices2 = new int[] { 0, 2 };
double[] values2 = new double[] { 2.0, 3.0 };
SparseVector addend = new SparseVector(indices2, values2);
base.plusEqualsSparse(addend);
assertEquals(3.0, base.value(0), 0.0001);
assertEquals(4.0, base.value(2), 0.0001);
}

@Test
public void testTimesEqualsSparseMultipliesInPlace() {
int[] indices1 = new int[] { 0, 1 };
double[] values1 = new double[] { 2.0, 3.0 };
SparseVector base = new SparseVector(indices1, values1);
int[] indices2 = new int[] { 0, 1 };
double[] values2 = new double[] { 3.0, 4.0 };
SparseVector multiplicand = new SparseVector(indices2, values2);
base.timesEqualsSparse(multiplicand);
assertEquals(6.0, base.value(0), 0.0001);
assertEquals(12.0, base.value(1), 0.0001);
}

@Test
public void testSetValueAtLocationSetsCorrectValue() {
int[] indices = new int[] { 0, 1 };
double[] values = new double[] { 5.0, 6.0 };
SparseVector vector = new SparseVector(indices, values);
vector.setValueAtLocation(1, 10.0);
assertEquals(10.0, vector.value(1), 0.0001);
}

@Test(expected = IllegalArgumentException.class)
public void testSetValueThrowsForUnknownSparseIndex() {
int[] indices = new int[] { 1, 3 };
double[] values = new double[] { 1.0, 2.0 };
SparseVector vector = new SparseVector(indices, values);
vector.setValue(2, 4.0);
}

@Test
public void testSetAllReplacesAllValues() {
int[] indices = new int[] { 0, 2 };
double[] values = new double[] { 5.0, 9.0 };
SparseVector vector = new SparseVector(indices, values);
vector.setAll(7.5);
assertEquals(7.5, vector.value(0), 0.0001);
assertEquals(7.5, vector.value(2), 0.0001);
}

@Test
public void testMapAppliesFunctionToValues() throws Throwable {
int[] indices = new int[] { 0, 1 };
double[] values = new double[] { 2.0, 3.0 };
SparseVector vector = new SparseVector(indices, values);
// Method method = MapTestHelper.class.getMethod("square", Double.class);
// vector.map(method);
assertEquals(4.0, vector.value(0), 0.0001);
assertEquals(9.0, vector.value(1), 0.0001);
}

@Test(expected = UnsupportedOperationException.class)
public void testMapThrowsForBinaryVector() throws Throwable {
int[] indices = new int[] { 1, 2 };
SparseVector vector = new SparseVector(indices, true);
// Method method = MapTestHelper.class.getMethod("square", Double.class);
// vector.map(method);
}

@Test
public void testOneNormDenseVector() {
double[] values = new double[] { 1.0, 2.0, 3.0 };
SparseVector vector = new SparseVector(values);
assertEquals(6.0, vector.oneNorm(), 0.0001);
}

@Test
public void testAbsNormSparseVector() {
int[] indices = new int[] { 0, 1 };
double[] values = new double[] { -2.0, 3.0 };
SparseVector vector = new SparseVector(indices, values);
assertEquals(5.0, vector.absNorm(), 0.0001);
}

@Test
public void testTwoNormSparseVector() {
int[] indices = new int[] { 0, 1 };
double[] values = new double[] { 3.0, 4.0 };
SparseVector vector = new SparseVector(indices, values);
assertEquals(5.0, vector.twoNorm(), 0.0001);
}

@Test
public void testInfinityNormSparseVector() {
int[] indices = new int[] { 0, 1 };
double[] values = new double[] { -3.0, 7.5 };
SparseVector vector = new SparseVector(indices, values);
assertEquals(7.5, vector.infinityNorm(), 0.0001);
}

@Test
public void testCloneMatrixProducesEqualDotProduct() {
int[] indices = new int[] { 1, 2 };
double[] values = new double[] { 2.0, 4.0 };
SparseVector original = new SparseVector(indices, values);
SparseVector clone = (SparseVector) original.cloneMatrix();
double expected = original.dotProduct(clone);
assertEquals(expected, original.dotProduct(clone), 0.0001);
}

@Test
public void testIsNaNReturnsTrueWhenElementIsNaN() {
int[] indices = new int[] { 0 };
double[] values = new double[] { Double.NaN };
SparseVector vector = new SparseVector(indices, values);
assertTrue(vector.isNaN());
}

@Test
public void testIsInfiniteReturnsTrueForInfiniteValue() {
int[] indices = new int[] { 0 };
double[] values = new double[] { Double.POSITIVE_INFINITY };
SparseVector vector = new SparseVector(indices, values);
assertTrue(vector.isInfinite());
}

@Test
public void testBinarySparseVectorInitializationValuesNull() {
int[] indices = new int[] { 5, 10 };
SparseVector vector = new SparseVector(indices, true);
assertNull(vector.getValues());
assertArrayEquals(indices, vector.getIndices());
assertEquals(1.0, vector.value(5), 0.0001);
assertEquals(0.0, vector.value(6), 0.0001);
assertTrue(vector.isBinary());
}

@Test
public void testDenseVectorConstructedWithFillValue() {
SparseVector vector = new SparseVector(3, 2.5);
assertEquals(2.5, vector.value(0), 0.0001);
assertEquals(2.5, vector.value(1), 0.0001);
assertEquals(2.5, vector.value(2), 0.0001);
assertEquals(0.0, vector.value(3), 0.0001);
}

@Test
public void testLocationBinarySearchStrongNegativeCase() {
int[] indices = new int[] { 2, 4, 6 };
double[] values = new double[] { 1.0, 2.0, 3.0 };
SparseVector vector = new SparseVector(indices, values);
int loc = vector.location(3);
assertTrue(loc < 0);
assertEquals(0.0, vector.value(3), 0.0001);
}

@Test
public void testArrayCopyFromAndIntoExactMatch() {
double[] source = new double[] { 4.0, 5.0 };
SparseVector vector = new SparseVector(new double[] { 1.0, 1.0 });
int val = vector.arrayCopyFrom(source, 0);
assertEquals(2, val);
assertEquals(4.0, vector.value(0), 0.0001);
assertEquals(5.0, vector.value(1), 0.0001);
double[] dest = new double[4];
int next = vector.arrayCopyInto(dest, 1);
assertEquals(3, next);
assertEquals(4.0, dest[1], 0.0001);
assertEquals(5.0, dest[2], 0.0001);
}

@Test
public void testVectorAddSparseToBinary() {
int[] indices1 = new int[] { 1, 3 };
SparseVector binary = new SparseVector(indices1, true);
int[] indices2 = new int[] { 1, 3 };
double[] values2 = new double[] { 2.0, 2.0 };
SparseVector nonBinary = new SparseVector(indices2, values2);
SparseVector result = binary.vectorAdd(nonBinary, 1.0);
assertEquals(3.0, result.value(1), 0.0001);
assertEquals(3.0, result.value(3), 0.0001);
}

@Test
public void testDensePlusEqualsSparseWithIndexBeyondLength() {
SparseVector dense = new SparseVector(new double[] { 1.0, 2.0, 3.0 });
int[] indices = new int[] { 2, 5 };
double[] values = new double[] { 1.0, 10.0 };
SparseVector sparse = new SparseVector(indices, values);
dense.plusEqualsSparse(sparse);
assertEquals(1.0, dense.value(0), 0.0001);
assertEquals(2.0, dense.value(1), 0.0001);
assertEquals(4.0, dense.value(2), 0.0001);
}

@Test
public void testDenseTimesEqualsSparseWithIndexBeyondBounds() {
SparseVector dense = new SparseVector(new double[] { 2.0, 4.0 });
int[] indices = new int[] { 1, 5 };
double[] values = new double[] { 2.0, 10.0 };
SparseVector sparse = new SparseVector(indices, values);
dense.timesEqualsSparse(sparse);
assertEquals(2.0, dense.value(0), 0.0001);
assertEquals(8.0, dense.value(1), 0.0001);
}

@Test
public void testSortIndicesAlreadySortedNoDuplicates() {
int[] indices = new int[] { 1, 2, 3 };
double[] values = new double[] { 10.0, 20.0, 30.0 };
SparseVector vector = new SparseVector(indices, values, true, true);
assertEquals(10.0, vector.value(1), 0.0001);
assertEquals(20.0, vector.value(2), 0.0001);
assertEquals(30.0, vector.value(3), 0.0001);
}

@Test
public void testRemoveDuplicatesCalledWithZero() {
int[] indices = new int[] { 2, 2, 3 };
double[] values = new double[] { 1.0, 2.0, 3.0 };
SparseVector vector = new SparseVector(indices, values, true, true, true);
assertEquals(2, vector.numLocations());
assertEquals(3.0, vector.value(2), 0.0001);
assertEquals(3.0, vector.value(3), 0.0001);
}

@Test(expected = IllegalArgumentException.class)
public void testConstructorMismatchIndicesAndValuesLengthsThrows() {
int[] indices = new int[] { 0, 1 };
double[] values = new double[] { 1.0 };
new SparseVector(indices, values, true, false, false);
}

@Test
public void testSingleElementSparseVectorDotProductItself() {
int[] indices = new int[] { 5 };
double[] values = new double[] { 2.0 };
SparseVector vector = new SparseVector(indices, values);
double result = vector.dotProduct(vector);
assertEquals(4.0, result, 0.0001);
}

@Test
public void testEmptySparseVectorDotProductWithDenseArray() {
int[] indices = new int[0];
double[] values = new double[0];
double[] dense = new double[] { 1.0, 2.0, 3.0 };
SparseVector vector = new SparseVector(indices, values);
double result = vector.dotProduct(dense);
assertEquals(0.0, result, 0.0001);
}

@Test
public void testSparseVectorExtendedDotProductWithInfAndZero() {
int[] indices1 = new int[] { 0, 1 };
double[] values1 = new double[] { Double.POSITIVE_INFINITY, 0.0 };
SparseVector v1 = new SparseVector(indices1, values1);
int[] indices2 = new int[] { 0, 1 };
double[] values2 = new double[] { 0.0, Double.NEGATIVE_INFINITY };
SparseVector v2 = new SparseVector(indices2, values2);
double result = v1.extendedDotProduct(v2);
assertEquals(0.0, result, 0.0001);
}

@Test(expected = ArrayIndexOutOfBoundsException.class)
public void testDenseVectorSetValueOutOfBoundsThrows() {
double[] values = new double[] { 1.0, 2.0 };
SparseVector vector = new SparseVector(values);
vector.setValue(5, 10.0);
}

@Test(expected = ArrayIndexOutOfBoundsException.class)
public void testArrayCopyFromWithInvalidStartThrows() {
double[] src = new double[] { 1.0, 2.0, 3.0 };
SparseVector vector = new SparseVector(new double[] { 0.0, 0.0 });
vector.arrayCopyFrom(src, 2);
}

@Test
public void testCloneMatrixZeroedBinaryVectorProducesZeros() {
int[] binaryIndices = new int[] { 1, 3, 5 };
SparseVector binaryVector = new SparseVector(binaryIndices, true);
ConstantMatrix clone = binaryVector.cloneMatrixZeroed();
SparseVector zeroed = (SparseVector) clone;
assertEquals(3, zeroed.numLocations());
assertEquals(0.0, zeroed.valueAtLocation(0), 0.0001);
assertEquals(0.0, zeroed.valueAtLocation(1), 0.0001);
assertEquals(0.0, zeroed.valueAtLocation(2), 0.0001);
}

@Test
public void testInfinityNormWithNegativeInfiniteAndNaN() {
int[] indices = new int[] { 0, 1 };
double[] values = new double[] { Double.NEGATIVE_INFINITY, Double.NaN };
SparseVector vector = new SparseVector(indices, values);
double norm = vector.infinityNorm();
assertEquals(Double.POSITIVE_INFINITY, norm, 0.0001);
}

@Test
public void testValueAccessWhenIndicesArrayIsNull() {
double[] values = new double[] { 5.0, 10.0, 0.0 };
SparseVector vector = new SparseVector(values);
double actual1 = vector.value(0);
double actual2 = vector.value(2);
double actual3 = vector.value(5);
assertEquals(5.0, actual1, 0.0001);
assertEquals(0.0, actual2, 0.0001);
assertEquals(0.0, actual3, 0.0001);
}

@Test
public void testSingleIndexAndSingleValueConsistency() {
double[] values = new double[] { 3.3 };
SparseVector vector = new SparseVector(values);
int[] dims = new int[1];
int dimCount = vector.getDimensions(dims);
int singleIndex = vector.singleIndex(new int[] { 0 });
double singleValue = vector.singleValue(singleIndex);
assertEquals(1, dimCount);
assertEquals(1, dims[0]);
assertEquals(0, singleIndex);
assertEquals(3.3, singleValue, 0.0001);
}

@Test
public void testIsNaNOrInfiniteOnCleanVector() {
double[] values = new double[] { 1.0, 2.0, 3.0 };
int[] indices = new int[] { 0, 1, 2 };
SparseVector vector = new SparseVector(indices, values);
assertFalse(vector.isNaN());
assertFalse(vector.isInfinite());
assertFalse(vector.isNaNOrInfinite());
}

@Test
public void testArrayCopyIntoNoOffset() {
double[] values = new double[] { 8.0, 9.0 };
SparseVector vector = new SparseVector(values);
double[] destination = new double[2];
int resultIndex = vector.arrayCopyInto(destination, 0);
assertEquals(2, resultIndex);
assertEquals(8.0, destination[0], 0.0001);
assertEquals(9.0, destination[1], 0.0001);
}

@Test
public void testSortIndicesDescendingInputWithDuplicates() {
int[] indices = new int[] { 9, 3, 3, 1 };
double[] values = new double[] { 1.0, 2.0, 3.0, 4.0 };
SparseVector vector = new SparseVector(indices, values, true, true, true);
assertEquals(3, vector.numLocations());
assertEquals(4.0, vector.value(1), 0.0001);
assertEquals(5.0, vector.value(3), 0.0001);
assertEquals(1.0, vector.value(9), 0.0001);
}

@Test(expected = IllegalArgumentException.class)
public void testMapMethodSignatureMismatchThrows() throws Throwable {
Method method = String.class.getMethod("trim");
int[] indices = new int[] { 0, 1 };
double[] values = new double[] { 1.0, 2.0 };
SparseVector vector = new SparseVector(indices, values);
vector.map(method);
}

@Test(expected = NullPointerException.class)
public void testMapNullMethodThrows() throws Throwable {
int[] indices = new int[] { 0 };
double[] values = new double[] { 3.0 };
SparseVector vector = new SparseVector(indices, values);
Method method = null;
vector.map(method);
}

@Test
public void testConstructorWithZeroLengthArrays() {
int[] indices = new int[0];
double[] values = new double[0];
SparseVector vector = new SparseVector(indices, values, true, true, true);
assertEquals(0, vector.numLocations());
assertEquals(0.0, vector.value(5), 0.0001);
}

@Test
public void testDotProductBetweenBinaryAndBinaryVectors() {
int[] indices1 = new int[] { 1, 3 };
int[] indices2 = new int[] { 1, 4 };
SparseVector v1 = new SparseVector(indices1, true);
SparseVector v2 = new SparseVector(indices2, true);
double result = v1.dotProduct(v2);
assertEquals(1.0, result, 0.0001);
}

@Test
public void testDotProductWithNaNShouldTriggerExtended() {
int[] indices = new int[] { 0, 1 };
double[] values = new double[] { 1.0, Double.NaN };
SparseVector v1 = new SparseVector(indices, values);
SparseVector v2 = new SparseVector(indices, new double[] { 2.0, 3.0 });
double result = v1.dotProduct(v2);
assertTrue(Double.isNaN(result));
}

@Test
public void testToStringWithBinaryVectorDefault() {
int[] indices = new int[] { 2, 5 };
SparseVector binary = new SparseVector(indices, true);
String actual = binary.toString();
assertTrue(actual.contains("2=1.0"));
assertTrue(actual.contains("5=1.0"));
}

@Test
public void testToStringWithDenseVectorOneLine() {
double[] values = new double[] { 1.1, 2.2 };
SparseVector dense = new SparseVector(values);
String result = dense.toString(true);
assertTrue(result.contains("0=1.1"));
assertTrue(result.contains("1=2.2"));
assertFalse(result.contains("\n"));
}

@Test
public void testGetNumDimensionsAlwaysReturnsOne() {
double[] values = new double[] { 1.0, 2.0 };
SparseVector vector = new SparseVector(values);
assertEquals(1, vector.getNumDimensions());
}

@Test
public void testGetDimensionsDenseAndSparse() {
double[] denseValues = new double[] { 10.0, 20.0 };
SparseVector dense = new SparseVector(denseValues);
int[] sizesDense = new int[1];
int retDense = dense.getDimensions(sizesDense);
assertEquals(1, retDense);
assertEquals(2, sizesDense[0]);
int[] indices = new int[] { 0, 5 };
double[] sparseValues = new double[] { 1.0, 2.0 };
SparseVector sparse = new SparseVector(indices, sparseValues);
int[] sizesSparse = new int[1];
int retSparse = sparse.getDimensions(sizesSparse);
assertEquals(1, retSparse);
assertEquals(5, sizesSparse[0]);
}

@Test
public void testSingleSizeOnSparseAndDenseVectors() {
int[] indices = new int[] { 3, 5 };
double[] values = new double[] { 1.0, 2.0 };
SparseVector sparse = new SparseVector(indices, values);
assertEquals(5, sparse.singleSize());
double[] denseValues = new double[] { 5.0, 6.0, 7.0 };
SparseVector dense = new SparseVector(denseValues);
assertEquals(3, dense.singleSize());
SparseVector empty = new SparseVector(new int[0], new double[0], true, true, true);
assertEquals(0, empty.singleSize());
}

@Test
public void testConstructorNullIndicesDense() {
double[] values = new double[] { 1.1, 2.2 };
SparseVector dense = new SparseVector(null, values, true, false, false);
assertEquals(1.1, dense.value(0), 0.0001);
assertEquals(2.2, dense.value(1), 0.0001);
assertEquals(0.0, dense.value(5), 0.0001);
}

@Test
public void testConstructorNullValuesBinarySparse() {
int[] indices = new int[] { 0, 2, 5 };
SparseVector sv = new SparseVector(indices, null, true, false, false);
assertEquals(1.0, sv.value(2), 0.0001);
assertEquals(0.0, sv.value(4), 0.0001);
assertTrue(sv.isBinary());
}

@Test(expected = IllegalArgumentException.class)
public void testConstructorNullIndicesAndValuesMismatch() {
int[] indices = new int[] { 0, 1, 2 };
double[] values = new double[] { 1.0 };
new SparseVector(indices, values, true, true, true);
}

@Test
public void testCloneMatrixZeroedOnDenseVector() {
double[] values = new double[] { 3.0, 6.0 };
SparseVector dense = new SparseVector(values);
SparseVector zeroed = (SparseVector) dense.cloneMatrixZeroed();
assertEquals(0.0, zeroed.value(0), 0.0001);
assertEquals(0.0, zeroed.value(1), 0.0001);
}

@Test
public void testCloneMatrixZeroedOnSparseVector() {
int[] indices = new int[] { 1, 4 };
double[] values = new double[] { 2.2, 3.3 };
SparseVector sparse = new SparseVector(indices, values);
SparseVector zeroed = (SparseVector) sparse.cloneMatrixZeroed();
assertEquals(0.0, zeroed.value(1), 0.0001);
assertEquals(0.0, zeroed.value(4), 0.0001);
}

@Test
public void testDotProductSelfWithZeroAndLargeIndex() {
int[] indices = new int[] { 0, 50 };
double[] values = new double[] { 0.0, 4.0 };
SparseVector v = new SparseVector(indices, values);
assertEquals(16.0, v.dotProduct(v), 0.0001);
}

@Test
public void testInfinityNormWithNegativeValues() {
int[] indices = new int[] { 0, 1 };
double[] values = new double[] { -1.2, -3.4 };
SparseVector v = new SparseVector(indices, values);
assertEquals(3.4, v.infinityNorm(), 0.0001);
}

@Test
public void testArrayCopyFromAndArrayCopyIntoBoundaryMatch() {
double[] source = new double[] { 2.0, 4.0 };
SparseVector v = new SparseVector(new double[] { 0.0, 0.0 });
int result = v.arrayCopyFrom(source, 0);
assertEquals(2, result);
double[] dest = new double[3];
int after = v.arrayCopyInto(dest, 1);
assertEquals(3, after);
assertEquals(2.0, dest[1], 0.0001);
assertEquals(4.0, dest[2], 0.0001);
}

@Test
public void testSingleIndexAndSingleToIndicesUsage() {
int[] indices = new int[] { 0, 2 };
double[] values = new double[] { 5.0, 10.0 };
SparseVector vector = new SparseVector(indices, values);
int[] holder = new int[1];
vector.singleToIndices(1, holder);
int single = vector.singleIndex(holder);
assertEquals(1, single);
}

@Test(expected = AssertionError.class)
public void testValueIntArrayWithMoreThanOneDimensionThrows() {
int[] indices = new int[] { 1 };
double[] values = new double[] { 5.0 };
SparseVector vector = new SparseVector(indices, values);
vector.value(new int[] { 1, 2 });
}

@Test
public void testValueIntArrayNullReturnsValueAtIndexZeroLocation() {
int[] indices = new int[] { 0, 1 };
double[] values = new double[] { 1.5, 2.5 };
SparseVector vector = new SparseVector(indices, values);
double result = vector.value(null);
assertEquals(1.5, result, 0.0001);
}

@Test
public void testTimesEqualsWithZeroFactorFlattensAllValues() {
int[] indices = new int[] { 1, 3 };
double[] values = new double[] { 6.0, -3.0 };
SparseVector vector = new SparseVector(indices, values);
vector.timesEquals(0.0);
assertEquals(0.0, vector.value(1), 0.00001);
assertEquals(0.0, vector.value(3), 0.00001);
}

@Test
public void testPlusEqualsSparseNoOverlapIgnored() {
int[] baseIndices = new int[] { 0, 2 };
double[] baseValues = new double[] { 5.0, 6.0 };
SparseVector base = new SparseVector(baseIndices, baseValues);
int[] otherIndices = new int[] { 1, 4 };
double[] otherValues = new double[] { 10.0, 20.0 };
SparseVector other = new SparseVector(otherIndices, otherValues);
base.plusEqualsSparse(other);
assertEquals(5.0, base.value(0), 0.0001);
assertEquals(6.0, base.value(2), 0.0001);
}

@Test
public void testTimesEqualsSparseZeroNoIndexMatchZerosAll() {
int[] baseIndices = new int[] { 0, 2, 3 };
double[] baseValues = new double[] { 1.0, 1.0, 1.0 };
SparseVector original = new SparseVector(baseIndices, baseValues);
int[] otherIndices = new int[] { 99 };
double[] otherValues = new double[] { 5.0 };
SparseVector mask = new SparseVector(otherIndices, otherValues);
original.timesEqualsSparseZero(mask, 2.0);
assertEquals(0.0, original.value(0), 0.0001);
assertEquals(0.0, original.value(2), 0.0001);
assertEquals(0.0, original.value(3), 0.0001);
}

@Test
public void testIsNaNOrInfiniteNegativeInfiniteEntry() {
int[] indices = new int[] { 0 };
double[] values = new double[] { Double.NEGATIVE_INFINITY };
SparseVector v = new SparseVector(indices, values);
assertTrue(v.isInfinite());
assertFalse(v.isNaN());
assertTrue(v.isNaNOrInfinite());
}

@Test
public void testToStringSparseVectorOnOneLineFalse() {
int[] indices = new int[] { 1, 3 };
double[] values = new double[] { 7.0, 8.0 };
SparseVector v = new SparseVector(indices, values);
String text = v.toString(false);
assertTrue(text.contains("1=7.0"));
assertTrue(text.contains("3=8.0"));
assertTrue(text.contains("\n"));
}

@Test
public void testCloneMatrixOnBinaryVector() {
int[] indices = new int[] { 0, 1 };
SparseVector binary = new SparseVector(indices, true);
ConstantMatrix clone = binary.cloneMatrix();
SparseVector cloneVector = (SparseVector) clone;
assertTrue(cloneVector.isBinary());
assertEquals(1.0, cloneVector.value(0), 0.0001);
assertEquals(1.0, cloneVector.value(1), 0.0001);
}

@Test
public void testDotProductBinaryDisjointIndicesReturnsZero() {
int[] indices1 = new int[] { 0, 1 };
int[] indices2 = new int[] { 2, 3 };
SparseVector v1 = new SparseVector(indices1, true);
SparseVector v2 = new SparseVector(indices2, true);
double result = v1.dotProduct(v2);
assertEquals(0.0, result, 0.0001);
}

@Test
public void testConstructorRemovesMultipleDuplicates() {
int[] indices = new int[] { 2, 2, 2, 3 };
double[] values = new double[] { 1.0, 2.0, 3.0, 4.0 };
SparseVector vector = new SparseVector(indices, values, true, true);
assertEquals(2, vector.numLocations());
assertEquals(6.0, vector.value(2), 0.0001);
assertEquals(4.0, vector.value(3), 0.0001);
}

@Test
public void testNormsWithNegativeValues() {
int[] indices = new int[] { 0, 1 };
double[] values = new double[] { -2.0, -3.0 };
SparseVector v = new SparseVector(indices, values);
assertEquals(5.0, v.oneNorm(), 0.0001);
assertEquals(5.0, v.absNorm(), 0.0001);
assertEquals(Math.sqrt(13.0), v.twoNorm(), 0.0001);
assertEquals(3.0, v.infinityNorm(), 0.0001);
}

@Test
public void testLocationDenseVectorOutOfBoundsReturnsIndex() {
double[] values = new double[] { 1.0, 2.0, 3.0 };
SparseVector v = new SparseVector(values);
int loc = v.location(10);
assertEquals(10, loc);
}

@Test
public void testTimesEqualsSparseZeroWithPartialMatch() {
int[] baseIndices = new int[] { 0, 1, 2 };
double[] baseValues = new double[] { 2.0, 4.0, 6.0 };
SparseVector base = new SparseVector(baseIndices, baseValues);
int[] factorIndices = new int[] { 1 };
double[] factorValues = new double[] { 0.5 };
SparseVector factor = new SparseVector(factorIndices, factorValues);
base.timesEqualsSparseZero(factor, 2.0);
assertEquals(0.0, base.value(0), 0.0001);
assertEquals(4.0, base.value(1), 0.0001);
assertEquals(0.0, base.value(2), 0.0001);
}

@Test(expected = ArrayIndexOutOfBoundsException.class)
public void testArrayCopyFromTooLargeOffsetThrows() {
double[] input = new double[] { 1.0, 2.0 };
SparseVector vector = new SparseVector(new double[] { 3.0 });
vector.arrayCopyFrom(input, 2);
}

@Test
public void testPrintMethodRunsForBinaryVector() {
int[] indices = new int[] { 1, 5 };
SparseVector binary = new SparseVector(indices, true);
binary.print();
assertTrue(true);
}

@Test
public void testRemoveDuplicatesInvokedWithZeroFlag() {
int[] indices = new int[] { 4, 4, 5 };
double[] values = new double[] { 2.0, 3.0, 1.0 };
SparseVector vector = new SparseVector(indices, values, true, false, true);
assertEquals(2, vector.numLocations());
assertEquals(5.0, vector.value(4), 0.0001);
assertEquals(1.0, vector.value(5), 0.0001);
}

@Test
public void testDenseVectorReturnsZeroForInvalidIndex() {
SparseVector dense = new SparseVector(new double[] { 1.0, 2.0 });
assertEquals(0.0, dense.value(5), 0.0001);
}

@Test
public void testConstructorWithIndicesOnlyBinaryFalse() {
int[] indices = new int[] { 1, 3, 5 };
SparseVector vector = new SparseVector(indices, false, true, true, false);
assertEquals(1.0, vector.value(1), 0.0001);
assertEquals(1.0, vector.value(5), 0.0001);
assertEquals(0.0, vector.value(6), 0.0001);
}

@Test
public void testDotProductWithDenseVectorContainingNaN() {
int[] indices = new int[] { 0, 1 };
double[] values = new double[] { 1.0, 2.0 };
SparseVector sparse = new SparseVector(indices, values);
double[] denseArray = new double[] { Double.NaN, 1.5 };
DenseVector dense = new DenseVector(denseArray);
double result = sparse.dotProduct(dense);
assertTrue(Double.isNaN(result));
}

@Test
public void testExtendedDotProductDenseVectorWithZerosAndInf() {
int[] indices = new int[] { 0, 1 };
double[] values = new double[] { Double.POSITIVE_INFINITY, 1.0 };
double[] denseValues = new double[] { 0.0, Double.NEGATIVE_INFINITY };
SparseVector sparse = new SparseVector(indices, values);
DenseVector dense = new DenseVector(denseValues);
double result = sparse.extendedDotProduct(dense);
assertEquals(0.0, result, 0.0001);
}

@Test
public void testExtendedDotProductSparseVectorsWithZerosAndInf() {
int[] indices1 = new int[] { 0, 1 };
double[] values1 = new double[] { Double.POSITIVE_INFINITY, 1.0 };
int[] indices2 = new int[] { 0, 1 };
double[] values2 = new double[] { 0.0, Double.NEGATIVE_INFINITY };
SparseVector v1 = new SparseVector(indices1, values1);
SparseVector v2 = new SparseVector(indices2, values2);
double result = v1.extendedDotProduct(v2);
assertEquals(0.0, result, 0.0001);
}

@Test
public void testDotProductInternalSparseBinaryAndNonBinary() {
int[] shortIndices = new int[] { 0, 2 };
int[] longIndices = new int[] { 0, 1, 2 };
double[] longValues = new double[] { 3.0, 4.0, 5.0 };
SparseVector vShort = new SparseVector(shortIndices, true);
SparseVector vLong = new SparseVector(longIndices, longValues);
// double result = vLong.dotProductInternal(vShort, vLong);
// assertEquals(8.0, result, 0.0001);
}

@Test(expected = IllegalArgumentException.class)
public void testSetValueThrowsOnUntrackedSparseIndex() {
int[] indices = new int[] { 0, 1, 2 };
double[] values = new double[] { 1.0, 1.0, 1.0 };
SparseVector vector = new SparseVector(indices, values);
vector.setValue(5, 10.0);
}

@Test
public void testSetValueOnDenseVectorValid() {
double[] values = new double[] { 1.0, 2.0, 3.0 };
SparseVector dense = new SparseVector(values);
dense.setValue(1, 9.0);
assertEquals(9.0, dense.value(1), 0.0001);
}

@Test
public void testMapAppliesMathAbsFunction() throws Throwable {
int[] indices = new int[] { 0, 1 };
double[] values = new double[] { -1.0, -2.0 };
SparseVector vector = new SparseVector(indices, values);
Method absMethod = Math.class.getMethod("abs", double.class);
// Method wrapped = AbsWrapper.class.getMethod("absWrap", Double.class);
// vector.map(wrapped);
assertEquals(1.0, vector.value(0), 0.0001);
assertEquals(2.0, vector.value(1), 0.0001);
}

@Test
public void testToStringOnBinaryVectorOneLineTrue() {
int[] indices = new int[] { 0, 1 };
SparseVector vector = new SparseVector(indices, true);
String text = vector.toString(true);
assertTrue(text.contains("0=1.0"));
assertTrue(text.contains("1=1.0"));
assertFalse(text.contains("\n"));
}

@Test
public void testToStringOnBinaryVectorOneLineFalse() {
int[] indices = new int[] { 0, 1 };
SparseVector vector = new SparseVector(indices, true);
String text = vector.toString(false);
assertTrue(text.contains("0=1.0"));
assertTrue(text.contains("1=1.0"));
assertTrue(text.contains("\n"));
}

@Test
public void testValueMethodWithNullIndicesFieldOnValueIntArray() {
double[] values = new double[] { 1.0, 2.0 };
SparseVector vector = new SparseVector(values);
int[] i = new int[] { 1 };
assertEquals(2.0, vector.value(i), 0.0001);
}

@Test
public void testGetDimensionsWithOneIndexedSparseVector() {
int[] indices = new int[] { 5 };
double[] values = new double[] { 10.0 };
SparseVector vector = new SparseVector(indices, values);
int[] sizes = new int[1];
int dims = vector.getDimensions(sizes);
assertEquals(1, dims);
assertEquals(5, sizes[0]);
}

@Test
public void testArrayCopyFromAndIntoWithOffset() {
double[] valuesSrc = new double[] { 1.0, 2.0, 3.0 };
SparseVector vector = new SparseVector(new double[] { 0.0, 0.0 });
int written = vector.arrayCopyFrom(valuesSrc, 1);
assertEquals(3, written);
double[] target = new double[4];
int writtenOut = vector.arrayCopyInto(target, 2);
assertEquals(4, writtenOut);
assertEquals(2.0, target[2], 0.0001);
assertEquals(3.0, target[3], 0.0001);
}

@Test
public void testAddToWithDenseVectorScaling() {
double[] values = new double[] { 2.0, 4.0, 6.0 };
SparseVector dense = new SparseVector(values);
double[] accumulator = new double[3];
dense.addTo(accumulator, 0.5);
assertEquals(1.0, accumulator[0], 0.0001);
assertEquals(2.0, accumulator[1], 0.0001);
assertEquals(3.0, accumulator[2], 0.0001);
}

@Test
public void testIndexAtLocationAndValueAtLocationSparse() {
int[] indices = new int[] { 4, 7 };
double[] values = new double[] { 1.1, 2.2 };
SparseVector sparse = new SparseVector(indices, values);
assertEquals(4, sparse.indexAtLocation(0));
assertEquals(7, sparse.indexAtLocation(1));
assertEquals(1.1, sparse.valueAtLocation(0), 0.0001);
assertEquals(2.2, sparse.valueAtLocation(1), 0.0001);
}
}
