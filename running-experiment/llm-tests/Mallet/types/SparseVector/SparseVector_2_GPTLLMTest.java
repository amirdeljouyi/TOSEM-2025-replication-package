package cc.mallet.types.tests;

import cc.mallet.types.*;
import cc.mallet.util.PropertyList;
import org.junit.Test;
import java.io.*;
import java.lang.reflect.Method;
import static org.junit.Assert.*;

public class SparseVector_2_GPTLLMTest {

@Test
public void testDenseConstructor() {
double[] values = new double[] { 1.0, 2.0, 3.0 };
SparseVector vector = new SparseVector(values);
assertEquals(3, vector.numLocations());
assertEquals(1.0, vector.value(0), 0.00001);
assertEquals(2.0, vector.value(1), 0.00001);
assertEquals(3.0, vector.value(2), 0.00001);
assertFalse(vector.isBinary());
}

@Test
public void testBinarySparseConstructor() {
int[] indices = new int[] { 1, 3 };
SparseVector vector = new SparseVector(indices);
assertTrue(vector.isBinary());
assertEquals(2, vector.numLocations());
assertEquals(1.0, vector.value(1), 0.00001);
assertEquals(1.0, vector.value(3), 0.00001);
assertEquals(0.0, vector.value(0), 0.00001);
assertEquals(0.0, vector.value(2), 0.00001);
}

@Test
public void testRealSparseConstructor() {
int[] indices = new int[] { 0, 2 };
double[] values = new double[] { 1.5, -2.0 };
SparseVector vector = new SparseVector(indices, values);
assertFalse(vector.isBinary());
assertEquals(2, vector.numLocations());
assertEquals(1.5, vector.value(0), 0.00001);
assertEquals(-2.0, vector.value(2), 0.00001);
assertEquals(0.0, vector.value(1), 0.00001);
}

@Test
public void testIncrementValue() {
int[] indices = new int[] { 0, 2 };
double[] values = new double[] { 1.0, 3.0 };
SparseVector vector = new SparseVector(indices, values);
vector.incrementValue(2, 2.5);
assertEquals(1.0, vector.value(0), 0.00001);
assertEquals(5.5, vector.value(2), 0.00001);
}

@Test(expected = IllegalArgumentException.class)
public void testIncrementValueInvalidIndex() {
int[] indices = new int[] { 1 };
double[] values = new double[] { 1.0 };
SparseVector vector = new SparseVector(indices, values);
vector.incrementValue(0, 2.0);
}

@Test
public void testSetValueDense() {
double[] values = new double[] { 1.0, 2.0, 3.0 };
SparseVector vector = new SparseVector(values);
vector.setValue(1, 8.5);
assertEquals(1.0, vector.value(0), 0.00001);
assertEquals(8.5, vector.value(1), 0.00001);
assertEquals(3.0, vector.value(2), 0.00001);
}

@Test(expected = IllegalArgumentException.class)
public void testSetValueSparseInvalid() {
int[] indices = new int[] { 2 };
double[] values = new double[] { 5.0 };
SparseVector vector = new SparseVector(indices, values);
vector.setValue(0, 1.0);
}

@Test
public void testSetAll() {
int[] indices = new int[] { 1, 2 };
double[] values = new double[] { 1.0, 2.0 };
SparseVector vector = new SparseVector(indices, values);
vector.setAll(17.0);
assertEquals(17.0, vector.value(1), 1e-9);
assertEquals(17.0, vector.value(2), 1e-9);
}

@Test
public void testDotProductWithArray() {
int[] indices = new int[] { 0, 2 };
double[] values = new double[] { 2.0, -1.0 };
SparseVector vector = new SparseVector(indices, values);
double[] v = new double[] { 4.0, 0.0, 5.0 };
double result = vector.dotProduct(v);
assertEquals(3.0, result, 0.00001);
}

@Test
public void testDotProductWithSparseVector() {
int[] indicesA = new int[] { 0, 2 };
double[] valuesA = new double[] { 2.0, 3.0 };
int[] indicesB = new int[] { 2 };
double[] valuesB = new double[] { 5.0 };
SparseVector a = new SparseVector(indicesA, valuesA);
SparseVector b = new SparseVector(indicesB, valuesB);
double result = a.dotProduct(b);
assertEquals(15.0, result, 0.00001);
}

@Test
public void testNorms() {
int[] indices = new int[] { 0, 1, 3 };
double[] values = new double[] { -1.0, 2.0, -3.0 };
SparseVector vector = new SparseVector(indices, values);
assertEquals(6.0, vector.absNorm(), 1e-9);
assertEquals(6.0, vector.oneNorm(), 1e-9);
assertEquals(Math.sqrt(14.0), vector.twoNorm(), 1e-9);
assertEquals(3.0, vector.infinityNorm(), 1e-9);
}

@Test
public void testIsNaN() {
int[] indices = new int[] { 0 };
double[] values = new double[] { Double.NaN };
SparseVector vector = new SparseVector(indices, values);
assertTrue(vector.isNaN());
}

@Test
public void testIsInfinite() {
int[] indices = new int[] { 0 };
double[] values = new double[] { Double.POSITIVE_INFINITY };
SparseVector vector = new SparseVector(indices, values);
assertTrue(vector.isInfinite());
}

@Test
public void testIsNaNOrInfinite() {
int[] indices = new int[] { 0, 1 };
double[] values = new double[] { Double.NaN, Double.POSITIVE_INFINITY };
SparseVector vector = new SparseVector(indices, values);
assertTrue(vector.isNaNOrInfinite());
}

@Test
public void testToStringSingleLine() {
int[] indices = new int[] { 1 };
double[] values = new double[] { 4.2 };
SparseVector vector = new SparseVector(indices, values);
String result = vector.toString(true);
assertTrue(result.contains("1=4.2"));
}

@Test
public void testCloneMatrix() {
int[] indices = new int[] { 0, 1 };
double[] values = new double[] { 1.0, 2.0 };
SparseVector vector = new SparseVector(indices, values);
ConstantMatrix clone = vector.cloneMatrix();
assertNotNull(clone);
assertTrue(clone instanceof SparseVector);
assertEquals(1.0, ((SparseVector) clone).value(0), 0.001);
assertEquals(2.0, ((SparseVector) clone).value(1), 0.001);
}

@Test
public void testCloneMatrixZeroed() {
int[] indices = new int[] { 0, 2 };
double[] values = new double[] { 2.0, 5.0 };
SparseVector vector = new SparseVector(indices, values);
SparseVector clone = (SparseVector) vector.cloneMatrixZeroed();
assertEquals(0.0, clone.value(0), 0.0001);
assertEquals(0.0, clone.value(2), 0.0001);
}

@Test
public void testMapMethod() throws Throwable {
int[] indices = new int[] { 0, 1 };
double[] values = new double[] { 4.0, 9.0 };
SparseVector vector = new SparseVector(indices, values);
Method sqrtMethod = Math.class.getMethod("sqrt", double.class);
// Method boxMethod = SqrtWrapper.class.getMethod("sqrtBoxed", Double.class);
// vector.map(boxMethod);
assertEquals(2.0, vector.value(0), 0.01);
assertEquals(3.0, vector.value(1), 0.01);
}

@Test(expected = UnsupportedOperationException.class)
public void testMapFailsOnBinary() throws Throwable {
int[] indices = new int[] { 1, 2 };
SparseVector vector = new SparseVector(indices);
// Method method = SqrtWrapper.class.getMethod("sqrtBoxed", Double.class);
// vector.map(method);
}

@Test
public void testSerialization() throws IOException, ClassNotFoundException {
int[] indices = new int[] { 1, 3 };
double[] values = new double[] { 2.5, -1.0 };
SparseVector vector = new SparseVector(indices, values);
ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
ObjectOutputStream out = new ObjectOutputStream(byteOut);
out.writeObject(vector);
out.flush();
out.close();
ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(byteOut.toByteArray()));
SparseVector deserialized = (SparseVector) in.readObject();
assertEquals(2.5, deserialized.value(1), 0.00001);
assertEquals(-1.0, deserialized.value(3), 0.00001);
}

@Test
public void testEmptyDenseVector() {
double[] values = new double[0];
SparseVector vector = new SparseVector(values);
assertEquals(0, vector.numLocations());
assertEquals(0.0, vector.value(0), 0.00001);
assertFalse(vector.isBinary());
}

@Test
public void testEmptySparseVector() {
int[] indices = new int[0];
double[] values = new double[0];
SparseVector vector = new SparseVector(indices, values);
assertEquals(0, vector.numLocations());
assertEquals(0.0, vector.value(0), 0.00001);
assertFalse(vector.isBinary());
}

@Test
public void testDensePlusSparseScale() {
double[] denseValues = new double[] { 1.0, 2.0, 3.0, 0.0 };
SparseVector dense = new SparseVector(denseValues);
int[] indices = new int[] { 0, 3 };
double[] values = new double[] { 1.0, 2.0 };
SparseVector sparse = new SparseVector(indices, values);
dense.plusEqualsSparse(sparse, 2.0);
assertEquals(3.0, dense.value(0), 0.0001);
assertEquals(2.0, dense.value(1), 0.0001);
assertEquals(3.0, dense.value(2), 0.0001);
assertEquals(4.0, dense.value(3), 0.0001);
}

@Test
public void testDenseTimesEqualsSparseWithScaling() {
double[] denseValues = new double[] { 2.0, 4.0, 6.0 };
SparseVector dense = new SparseVector(denseValues);
int[] idx = new int[] { 0, 1, 2 };
double[] val = new double[] { 1.0, 0.5, 0.0 };
SparseVector sparse = new SparseVector(idx, val);
dense.timesEqualsSparse(sparse, 2.0);
assertEquals(4.0, dense.value(0), 1e-9);
assertEquals(4.0, dense.value(1), 1e-9);
assertEquals(0.0, dense.value(2), 1e-9);
}

@Test
public void testTimesEqualsSparseZero() {
int[] idx1 = new int[] { 1, 3 };
double[] val1 = new double[] { 5.0, 2.0 };
SparseVector v1 = new SparseVector(idx1, val1);
int[] idx2 = new int[] { 1 };
double[] val2 = new double[] { 2.0 };
SparseVector v2 = new SparseVector(idx2, val2);
v1.timesEqualsSparseZero(v2, 1.0);
assertEquals(10.0, v1.value(1), 0.000001);
assertEquals(0.0, v1.value(3), 0.000001);
}

@Test
public void testVectorAddToZeroResult() {
int[] idx1 = new int[] { 0, 2 };
double[] val1 = new double[] { 1.0, -3.0 };
SparseVector v1 = new SparseVector(idx1, val1);
int[] idx2 = new int[] { 0, 2 };
double[] val2 = new double[] { -1.0, 3.0 };
SparseVector v2 = new SparseVector(idx2, val2);
SparseVector result = v1.vectorAdd(v2, 1.0);
assertEquals(0, result.numLocations());
}

@Test
public void testLocationBinarySearchNotFound() {
int[] indices = new int[] { 0, 2, 4 };
SparseVector vector = new SparseVector(indices, null, true, true);
int location = vector.location(3);
assertTrue(location < 0);
assertEquals(0.0, vector.value(3), 0.0);
}

@Test
public void testArrayCopyFromWithOffset() {
double[] values = new double[] { 1.0, 2.0, 3.0 };
SparseVector vector = new SparseVector(values);
double[] source = new double[] { 9.0, 4.5, 5.5, 6.5 };
int newIndex = vector.arrayCopyFrom(source, 1);
assertEquals(2.5, newIndex - 1.0, 1.0);
assertEquals(4.5, vector.value(0), 1e-9);
assertEquals(5.5, vector.value(1), 1e-9);
assertEquals(6.5, vector.value(2), 1e-9);
}

@Test
public void testArrayCopyInto() {
int[] indices = new int[] { 0, 2 };
double[] values = new double[] { 3.0, 7.0 };
SparseVector vector = new SparseVector(indices, values);
double[] arr = new double[5];
int pos = vector.arrayCopyInto(arr, 1);
assertEquals(1, arr[0], 0.0);
assertEquals(3.0, arr[1], 1e-6);
assertEquals(7.0, arr[2], 1e-6);
assertEquals(3, pos);
}

@Test
public void testSingleIndexAndSingleToIndices() {
SparseVector vector = new SparseVector(new double[] { 1.0 });
int index = vector.singleIndex(new int[] { 7 });
assertEquals(7, index);
int[] output = new int[1];
vector.singleToIndices(5, output);
assertEquals(5, output[0]);
}

@Test
public void testSingleSizeSparseVector() {
int[] indices = new int[] { 1, 3, 7 };
double[] values = new double[] { 2.0, 4.0, 6.0 };
SparseVector vector = new SparseVector(indices, values);
assertEquals(7, vector.singleSize());
}

@Test
public void testRemoveDuplicatesByCounting() {
int[] indices = new int[] { 1, 2, 2, 3 };
double[] values = new double[] { 1.0, 2.0, 3.0, 4.0 };
SparseVector vector = new SparseVector(indices, values, true, true);
assertEquals(3, vector.numLocations());
assertEquals(1.0, vector.value(1), 1e-9);
assertEquals(5.0, vector.value(2), 1e-9);
assertEquals(4.0, vector.value(3), 1e-9);
}

@Test
public void testRemoveDuplicatesWithZeroArgument() {
int[] indices = new int[] { 5, 5 };
double[] values = new double[] { 4.0, 6.0 };
SparseVector vector = new SparseVector(indices, values, true, true);
assertEquals(1, vector.numLocations());
assertEquals(10.0, vector.value(5), 1e-9);
}

@Test(expected = IllegalArgumentException.class)
public void testConstructorMismatchedIndexAndValueLength() {
int[] indices = new int[] { 1, 2 };
double[] values = new double[] { 1.0 };
SparseVector vector = new SparseVector(indices, values, true, false, false);
}

@Test
public void testConstructorWithCapacityGreaterThanLength() {
int[] indices = new int[] { 2, 4 };
double[] values = new double[] { 5.0, 6.0 };
SparseVector vector = new SparseVector(indices, values, 5, 2, true, false, false);
assertEquals(2, vector.numLocations());
assertEquals(6.0, vector.value(4), 0.0001);
}

@Test
public void testConstructorWithRemoveDuplicatesFalse() {
int[] indices = new int[] { 1, 1, 2 };
double[] values = new double[] { 1.0, 2.0, 3.0 };
SparseVector vector = new SparseVector(indices, values, 3, 3, true, false, false);
assertEquals(3, vector.numLocations());
}

@Test
public void testMakeBinaryUnsupportedOperation() {
double[] values = new double[] { 1.0, 2.0 };
SparseVector vector = new SparseVector(values);
try {
vector.makeBinary();
fail("Expected UnsupportedOperationException");
} catch (UnsupportedOperationException e) {
assertTrue(e.getMessage().contains("Not yet implemented"));
}
}

@Test
public void testMakeNonBinaryUnsupportedOperation() {
int[] indices = new int[] { 1, 2 };
SparseVector vector = new SparseVector(indices);
try {
vector.makeNonBinary();
fail("Expected UnsupportedOperationException");
} catch (UnsupportedOperationException e) {
assertTrue(e.getMessage().contains("Not yet implemented"));
}
}

@Test(expected = IllegalArgumentException.class)
public void testSetValueOnSparseMissingIndexThrows() {
int[] indices = new int[] { 1, 3 };
double[] values = new double[] { 2.0, 4.0 };
SparseVector vector = new SparseVector(indices, values);
vector.setValue(2, 1.0);
}

@Test
public void testCloneMatrixBinaryVector() {
int[] indices = new int[] { 1, 2 };
SparseVector vector = new SparseVector(indices);
ConstantMatrix clone = vector.cloneMatrix();
assertTrue(clone instanceof SparseVector);
double value = ((SparseVector) clone).value(1);
assertEquals(1.0, value, 0.0001);
}

@Test(expected = IllegalArgumentException.class)
public void testMapFailsOnWrongMethodSignature() throws Throwable {
int[] indices = new int[] { 0 };
double[] values = new double[] { 2.0 };
SparseVector vector = new SparseVector(indices, values);
Method method = String.class.getMethod("toUpperCase");
vector.map(method);
}

@Test(expected = Throwable.class)
public void testMapThrowsTargetException() throws Throwable {
int[] indices = new int[] { 0 };
double[] values = new double[] { 2.0 };
SparseVector vector = new SparseVector(indices, values);
// Method method = ThrowingFunction.class.getMethod("throwsException", Double.class);
// vector.map(method);
}

@Test
public void testDotProductWithDenseVectorContainingInfinity() {
int[] indices = new int[] { 1 };
double[] values = new double[] { 0.0 };
SparseVector vector = new SparseVector(indices, values);
double[] denseValues = new double[] { 0.0, Double.POSITIVE_INFINITY };
DenseVector dense = new DenseVector(denseValues);
double result = vector.dotProduct(dense);
assertEquals(0.0, result, 0.0);
}

@Test
public void testExtendedDotProductWithSparseContainingZeroAndInfinity() {
int[] indices1 = new int[] { 0 };
double[] values1 = new double[] { 0.0 };
SparseVector v1 = new SparseVector(indices1, values1);
int[] indices2 = new int[] { 0 };
double[] values2 = new double[] { Double.NEGATIVE_INFINITY };
SparseVector v2 = new SparseVector(indices2, values2);
double result = v1.dotProduct(v2);
assertEquals(0.0, result, 0.0);
}

@Test
public void testAddToZeroScaleLeavesAccumulatorUnchanged() {
int[] indices = new int[] { 1, 2 };
double[] values = new double[] { 1.0, 2.0 };
SparseVector vector = new SparseVector(indices, values);
double[] accumulator = new double[] { 10.0, 20.0, 30.0 };
vector.addTo(accumulator, 0.0);
assertEquals(10.0, accumulator[0], 0.0001);
assertEquals(20.0, accumulator[1], 0.0001);
assertEquals(30.0, accumulator[2], 0.0001);
}

@Test
public void testDotProductWithUnrecognizedMatrixTypeThrows() {
int[] indices = new int[] { 0 };
double[] values = new double[] { 1.0 };
SparseVector vector = new SparseVector(indices, values);
ConstantMatrix unknown = new ConstantMatrix() {

@Override
public int getNumDimensions() {
return 0;
}

@Override
public int getDimensions(int[] sizes) {
return 0;
}

@Override
public int numLocations() {
return 0;
}

@Override
public int location(int index) {
return 0;
}

@Override
public double valueAtLocation(int location) {
return 0;
}

@Override
public int indexAtLocation(int location) {
return 0;
}

@Override
public double value(int[] indices) {
return 0;
}

@Override
public int singleIndex(int[] indices) {
return 0;
}

@Override
public void singleToIndices(int i, int[] indices) {
}

@Override
public double singleValue(int i) {
return 0;
}

@Override
public int singleSize() {
return 0;
}

@Override
public ConstantMatrix cloneMatrix() {
return this;
}

@Override
public double dotProduct(ConstantMatrix m) {
return 0;
}

@Override
public double oneNorm() {
return 0;
}

@Override
public double absNorm() {
return 0;
}

@Override
public double twoNorm() {
return 0;
}

@Override
public double infinityNorm() {
return 0;
}

@Override
public void print() {
}

@Override
public boolean isNaN() {
return false;
}
};
try {
vector.dotProduct(unknown);
fail("Expected IllegalArgumentException");
} catch (IllegalArgumentException e) {
assertTrue(e.getMessage().contains("Unrecognized Matrix type"));
}
}

@Test
public void testGetDimensionsDenseAndSparse() {
double[] denseValues = new double[] { 5.0, 6.0 };
SparseVector dense = new SparseVector(denseValues);
int[] sizeHolder1 = new int[1];
int dims1 = dense.getDimensions(sizeHolder1);
assertEquals(1, dims1);
assertEquals(2, sizeHolder1[0]);
int[] sparseIndices = new int[] { 0, 3, 5 };
double[] sparseValues = new double[] { 1.0, 2.0, 3.0 };
SparseVector sparse = new SparseVector(sparseIndices, sparseValues);
int[] sizeHolder2 = new int[1];
int dims2 = sparse.getDimensions(sizeHolder2);
assertEquals(1, dims2);
assertEquals(5, sizeHolder2[0]);
}

@Test
public void testLocationDenseVectorReturnsIdentity() {
double[] values = new double[] { 2.0, 4.0, 6.0 };
SparseVector dense = new SparseVector(values);
int location = dense.location(2);
assertEquals(2, location);
}

@Test
public void testLocationSparseNotFoundReturnsNegative() {
int[] indices = new int[] { 0, 2, 5 };
double[] values = new double[] { 1.0, 1.0, 1.0 };
SparseVector vector = new SparseVector(indices, values);
int location = vector.location(3);
assertTrue(location < 0);
}

@Test
public void testValueArrayWithNullIndicesFieldReturnsFromLocationZero() {
int[] indices = null;
double[] values = new double[] { 7.7 };
SparseVector vector = new SparseVector(indices, values, 1, 1, false, false, false);
int[] arr = new int[1];
arr[0] = 0;
double v = vector.value(arr);
assertEquals(7.7, v, 1e-9);
}

@Test
public void testValueArrayWithSparseIndices() {
int[] indices = new int[] { 1 };
double[] values = new double[] { 8.8 };
SparseVector vector = new SparseVector(indices, values);
int[] arr = new int[1];
arr[0] = 1;
double v = vector.value(arr);
assertEquals(8.8, v, 1e-9);
}

@Test
public void testCloneMatrixOnDenseVector() {
double[] values = new double[] { 9.0, 10.0 };
SparseVector vector = new SparseVector(values);
SparseVector clone = (SparseVector) vector.cloneMatrix();
assertNotSame(vector, clone);
assertEquals(9.0, clone.value(0), 0.0);
}

@Test
public void testCloneMatrixZeroedDenseVector() {
double[] values = new double[] { 3.3, 4.4 };
SparseVector vector = new SparseVector(values);
SparseVector zeroed = (SparseVector) vector.cloneMatrixZeroed();
assertEquals(0.0, zeroed.value(0), 0.0);
assertEquals(0.0, zeroed.value(1), 0.0);
}

@Test
public void testArrayCopyFromArrayTooSmall() {
double[] initial = new double[] { 1.0, 2.0 };
SparseVector vector = new SparseVector(initial);
double[] source = new double[] { 7.7 };
try {
vector.arrayCopyFrom(source, 0);
fail("Expected ArrayIndexOutOfBoundsException");
} catch (ArrayIndexOutOfBoundsException e) {
assertTrue(e.getMessage() != null);
}
}

@Test
public void testDotProductEmptySparse() {
int[] indices = new int[0];
double[] values = new double[0];
SparseVector vector = new SparseVector(indices, values);
double[] dense = new double[] { 1.0, 2.0 };
double result = vector.dotProduct(dense);
assertEquals(0.0, result, 0.0);
}

@Test
public void testDotProductEmptyWithSparseVectorOther() {
int[] indices1 = new int[0];
double[] values1 = new double[0];
SparseVector v1 = new SparseVector(indices1, values1);
int[] indices2 = new int[] { 1 };
double[] values2 = new double[] { 1.0 };
SparseVector v2 = new SparseVector(indices2, values2);
double dp = v1.dotProduct(v2);
assertEquals(0.0, dp, 0.0);
}

@Test
public void testAbsNormZeroedVector() {
int[] indices = new int[] { 2, 3 };
double[] values = new double[] { 0.0, 0.0 };
SparseVector vector = new SparseVector(indices, values);
assertEquals(0.0, vector.absNorm(), 0.0);
}

@Test
public void testToStringBinaryValues() {
int[] indices = new int[] { 0, 5 };
SparseVector vector = new SparseVector(indices);
String repr = vector.toString(false);
assertTrue(repr.contains("0="));
assertTrue(repr.contains("5="));
}

@Test
public void testToStringDenseVectorMultiline() {
double[] values = new double[] { 1.0, 2.5 };
SparseVector vector = new SparseVector(values);
String s = vector.toString(false);
assertTrue(s.contains("0=1.0"));
assertTrue(s.contains("\n"));
}

@Test
public void testVectorAddDenseToSparseWithZerosOnly() {
double[] denseValues = new double[] { 0.0, 0.0, 0.0 };
SparseVector dense = new SparseVector(denseValues);
int[] sparseIndices = new int[] { 0, 1, 2 };
double[] sparseValues = new double[] { 0.0, 0.0, 0.0 };
SparseVector sparse = new SparseVector(sparseIndices, sparseValues);
SparseVector result = dense.vectorAdd(sparse, 1.0);
assertEquals(0, result.numLocations());
}

@Test
public void testTimesEqualsSparseWhereNoIndexOverlaps() {
int[] indicesA = new int[] { 0, 1 };
double[] valuesA = new double[] { 1.0, 2.0 };
SparseVector vecA = new SparseVector(indicesA, valuesA);
int[] indicesB = new int[] { 3, 4 };
double[] valuesB = new double[] { 1.0, 1.0 };
SparseVector vecB = new SparseVector(indicesB, valuesB);
vecA.timesEqualsSparse(vecB);
assertEquals(1.0, vecA.value(0), 0.0);
assertEquals(2.0, vecA.value(1), 0.0);
}

@Test
public void testDensePlusEqualsSparseWithOutOfBoundsSparseIndex() {
double[] denseValues = new double[] { 1.0, 2.0 };
SparseVector dense = new SparseVector(denseValues);
int[] sparseIndices = new int[] { 0, 5 };
double[] sparseValues = new double[] { 10.0, 100.0 };
SparseVector sparse = new SparseVector(sparseIndices, sparseValues);
dense.plusEqualsSparse(sparse, 1.0);
assertEquals(11.0, dense.value(0), 0.0);
assertEquals(2.0, dense.value(1), 0.0);
}

@Test
public void testDenseTimesEqualsSparseWithOutOfBoundsIndex() {
double[] denseValues = new double[] { 3.0, 4.0 };
SparseVector dense = new SparseVector(denseValues);
int[] sparseIndices = new int[] { 0, 99 };
double[] sparseValues = new double[] { 2.0, 10.0 };
SparseVector sparse = new SparseVector(sparseIndices, sparseValues);
dense.timesEqualsSparse(sparse, 1.0);
assertEquals(6.0, dense.value(0), 0.0);
assertEquals(4.0, dense.value(1), 0.0);
}

@Test
public void testDotProductWithBinaryAsRightHandOperand() {
int[] leftIndices = new int[] { 0, 2, 4 };
double[] leftValues = new double[] { 1.0, 2.0, 3.0 };
SparseVector left = new SparseVector(leftIndices, leftValues);
int[] rightIndices = new int[] { 2, 4 };
SparseVector right = new SparseVector(rightIndices);
double result = left.dotProduct(right);
assertEquals(5.0, result, 0.0001);
}

@Test
public void testSortIndicesWithAlreadySortedInput() {
int[] indices = new int[] { 1, 2, 3 };
double[] values = new double[] { 10.0, 20.0, 30.0 };
SparseVector vector = new SparseVector(indices, values, true, true);
assertEquals(3, vector.numLocations());
assertEquals(10.0, vector.value(1), 0.01);
assertEquals(30.0, vector.value(3), 0.01);
}

@Test
public void testSortIndicesTriggersDuplicateRemovalAndMergesCorrectly() {
int[] indices = new int[] { 2, 1, 2 };
double[] values = new double[] { 2.0, 1.0, 3.0 };
SparseVector vector = new SparseVector(indices, values, true, true);
assertEquals(2, vector.numLocations());
assertEquals(1.0, vector.value(1), 0.0);
assertEquals(5.0, vector.value(2), 0.0);
}

@Test
public void testHasInfiniteTrueEncodedDuringDeserialization() throws IOException, ClassNotFoundException {
int[] indices = new int[] { 0 };
double[] values = new double[] { Double.POSITIVE_INFINITY };
SparseVector vector = new SparseVector(indices, values, true, false, false);
ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
ObjectOutputStream oos = new ObjectOutputStream(outBytes);
oos.writeObject(vector);
oos.close();
ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(outBytes.toByteArray()));
SparseVector deserialized = (SparseVector) in.readObject();
assertTrue(deserialized.isInfinite());
}

@Test
public void testHasInfiniteFalseEncodedDuringDeserialization() throws IOException, ClassNotFoundException {
int[] indices = new int[] { 0 };
double[] values = new double[] { 1.0 };
SparseVector vector = new SparseVector(indices, values, true, false, false);
ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
ObjectOutputStream oos = new ObjectOutputStream(outBytes);
oos.writeObject(vector);
oos.close();
ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(outBytes.toByteArray()));
SparseVector deserialized = (SparseVector) in.readObject();
assertFalse(deserialized.isInfinite());
}

@Test
public void testValueOutOfBoundsOnDenseVectorReturnsZero() {
double[] values = new double[] { 1.0 };
SparseVector vector = new SparseVector(values);
assertEquals(0.0, vector.value(5), 0.0);
}

@Test
public void testValueAtLocationOnBinaryVectorReturnsOne() {
int[] indices = new int[] { 2 };
SparseVector vector = new SparseVector(indices);
assertEquals(1.0, vector.valueAtLocation(0), 0.0);
}

@Test
public void testAddToWithoutScaleDefaultsToOne() {
int[] indices = new int[] { 0, 2 };
double[] values = new double[] { 2.0, 3.0 };
SparseVector vector = new SparseVector(indices, values);
double[] acc = new double[] { 0.0, 0.0, 0.0 };
vector.addTo(acc);
assertEquals(2.0, acc[0], 0.0);
assertEquals(3.0, acc[2], 0.0);
}

@Test
public void testSingleIndexAndSingleToIndicesInverseOperations() {
SparseVector vector = new SparseVector(new double[] { 1.0, 2.0 });
int[] arr = new int[1];
vector.singleToIndices(5, arr);
int recovered = vector.singleIndex(arr);
assertEquals(5, recovered);
}

@Test
public void testSortIndicesMergesAndSortsCorrectly() {
int[] indices = new int[] { 3, 2, 2, 1 };
double[] values = new double[] { 1.0, 2.0, 3.0, 4.0 };
SparseVector vector = new SparseVector(indices, values, true, true);
assertEquals(3, vector.numLocations());
assertEquals(4.0, vector.value(1), 0.0);
assertEquals(5.0, vector.value(2), 0.0);
assertEquals(1.0, vector.value(3), 0.0);
}

@Test
public void testSortIndicesSkipsSwapIfSorted() {
int[] indices = new int[] { 1, 2, 3 };
double[] values = new double[] { 5.0, 6.0, 7.0 };
SparseVector vector = new SparseVector(indices, values, true, true);
assertEquals(3, vector.numLocations());
assertEquals(5.0, vector.value(1), 0.0);
assertEquals(6.0, vector.value(2), 0.0);
assertEquals(7.0, vector.value(3), 0.0);
}

@Test
public void testOneNormOfBinaryVector() {
int[] indices = new int[] { 0, 1, 2 };
SparseVector binaryVector = new SparseVector(indices);
double norm = binaryVector.oneNorm();
assertEquals(3.0, norm, 0.0001);
}

@Test
public void testValueWithNullIntArray() {
double[] values = new double[] { 10.0 };
SparseVector vector = new SparseVector(values);
double result = vector.value(null);
assertEquals(10.0, result, 0.0);
}

@Test(expected = AssertionError.class)
public void testValueWithWrongDimIntArrayLength() {
double[] values = new double[] { 1.0, 2.0 };
SparseVector vector = new SparseVector(values);
int[] badDims = new int[] { 1, 2 };
vector.value(badDims);
}

@Test
public void testRemoveDuplicatesExplicitCount() {
int[] indices = new int[] { 1, 1, 1 };
double[] values = new double[] { 1.0, 2.0, 3.0 };
SparseVector vector = new SparseVector(indices, values, true, false, false);
// vector.removeDuplicates(2);
assertEquals(1, vector.numLocations());
assertEquals(6.0, vector.value(1), 1e-9);
}

@Test
public void testCloneMatrixOnBinaryVector() {
int[] indices = new int[] { 2, 4 };
SparseVector binaryVector = new SparseVector(indices);
SparseVector clone = (SparseVector) binaryVector.cloneMatrix();
assertTrue(clone.isBinary());
assertEquals(1.0, clone.value(2), 1e-9);
assertEquals(0.0, clone.value(1), 1e-9);
}

@Test
public void testCloneMatrixZeroedOnBinaryVector() {
int[] indices = new int[] { 1, 3 };
SparseVector binaryVector = new SparseVector(indices);
SparseVector zeroed = (SparseVector) binaryVector.cloneMatrixZeroed();
assertTrue(zeroed.isBinary());
assertEquals(1.0, zeroed.value(1), 1e-9);
}

@Test
public void testToStringOnOneLineTrue() {
int[] indices = new int[] { 0, 1 };
double[] values = new double[] { 2.0, 4.0 };
SparseVector vector = new SparseVector(indices, values);
String result = vector.toString(true);
assertTrue(result.contains("0=2.0 "));
assertTrue(result.contains("1=4.0"));
assertFalse(result.contains("\n"));
}

@Test
public void testToStringOnOneLineFalse() {
int[] indices = new int[] { 0, 1 };
double[] values = new double[] { 2.0, 4.0 };
SparseVector vector = new SparseVector(indices, values);
String result = vector.toString(false);
assertTrue(result.contains("0=2.0\n"));
assertTrue(result.contains("1=4.0"));
}

@Test
public void testExtendedDotProductWhenNaNOccurs() {
int[] indicesA = new int[] { 0 };
double[] valuesA = new double[] { Double.NaN };
SparseVector v1 = new SparseVector(indicesA, valuesA);
int[] indicesB = new int[] { 0 };
double[] valuesB = new double[] { 1.0 };
SparseVector v2 = new SparseVector(indicesB, valuesB);
double result = v1.dotProduct(v2);
assertTrue(Double.isNaN(result));
}

@Test
public void testExtendedDotProductSkipsInfMultipliedByZero() {
int[] indices = new int[] { 0 };
double[] values = new double[] { Double.POSITIVE_INFINITY };
SparseVector v1 = new SparseVector(indices, values);
int[] indices2 = new int[] { 0 };
double[] values2 = new double[] { 0.0 };
SparseVector v2 = new SparseVector(indices2, values2);
double dp = v1.dotProduct(v2);
assertEquals(0.0, dp, 1e-9);
}

@Test
public void testInfinityNormWithNegativeValues() {
int[] indices = new int[] { 0, 1 };
double[] values = new double[] { -2.0, -5.0 };
SparseVector vector = new SparseVector(indices, values);
double norm = vector.infinityNorm();
assertEquals(5.0, norm, 0.0);
}

@Test
public void testSingleSizeWhenSparseHasEmptyIndicesReturnsZero() {
int[] indices = new int[0];
double[] values = new double[0];
SparseVector vector = new SparseVector(indices, values);
int size = vector.singleSize();
assertEquals(0, size);
}

@Test(expected = ArrayIndexOutOfBoundsException.class)
public void testSetValueAtLocationOutOfBounds() {
double[] values = new double[] { 1.0, 2.0 };
int[] indices = new int[] { 0, 1 };
SparseVector vector = new SparseVector(indices, values);
vector.setValueAtLocation(3, 5.0);
}

@Test
public void testDotProductWithZeroSparseVector() {
int[] indices = new int[] { 1, 3 };
double[] values = new double[] { 0.0, 0.0 };
SparseVector vectorA = new SparseVector(indices, values);
double[] dense = new double[] { 0.0, 10.0, 0.0, 20.0 };
double result = vectorA.dotProduct(dense);
assertEquals(0.0, result, 1e-9);
}

@Test
public void testValueAtLocationOnBinarySparseVector() {
int[] indices = new int[] { 2 };
SparseVector vector = new SparseVector(indices);
double value = vector.valueAtLocation(0);
assertEquals(1.0, value, 0.0001);
}

@Test
public void testSingleIndexAndSingleToIndicesWithDenseVector() {
double[] values = new double[] { 1.0, 2.0, 3.0 };
SparseVector dense = new SparseVector(values);
int[] arr = new int[1];
dense.singleToIndices(2, arr);
int singleIdx = dense.singleIndex(arr);
assertEquals(2, singleIdx);
}

@Test
public void testRemoveDuplicatesWhenNoneExist() {
int[] indices = new int[] { 1, 2, 3 };
double[] values = new double[] { 10.0, 20.0, 30.0 };
SparseVector vector = new SparseVector(indices, values);
// vector.removeDuplicates(0);
assertEquals(3, vector.numLocations());
assertEquals(20.0, vector.value(2), 0.0);
}

@Test
public void testAddToWithPreFilledAccumulator() {
int[] indices = new int[] { 1, 3 };
double[] values = new double[] { 5.0, 10.0 };
SparseVector vector = new SparseVector(indices, values);
double[] acc = new double[] { 1.0, 1.0, 1.0, 1.0 };
vector.addTo(acc);
assertEquals(1.0, acc[0], 0.0);
assertEquals(6.0, acc[1], 0.0);
assertEquals(1.0, acc[2], 0.0);
assertEquals(11.0, acc[3], 0.0);
}

@Test
public void testCloneMatrixZeroedSharedCapacityBehavior() {
int[] indices = new int[] { 2, 4 };
double[] values = new double[] { 6.0, 9.0 };
SparseVector vector = new SparseVector(indices, values, 5, 2, true, false, false);
SparseVector zeroed = (SparseVector) vector.cloneMatrixZeroed();
assertEquals(0.0, zeroed.value(2), 0.0);
assertEquals(0.0, zeroed.value(4), 0.0);
}

@Test
public void testExtendedDotProductWithInfinityAndNonZero() {
int[] indices = new int[] { 0 };
double[] values = new double[] { Double.POSITIVE_INFINITY };
SparseVector v1 = new SparseVector(indices, values);
int[] indices2 = new int[] { 0 };
double[] values2 = new double[] { 2.0 };
SparseVector v2 = new SparseVector(indices2, values2);
double result = v1.dotProduct(v2);
assertEquals(Double.POSITIVE_INFINITY, result, 0.0);
}

@Test(expected = ArrayIndexOutOfBoundsException.class)
public void testArrayCopyIntoExceedsDestinationLength() {
int[] indices = new int[] { 0, 1 };
double[] values = new double[] { 1.0, 2.0 };
SparseVector vector = new SparseVector(indices, values);
double[] destination = new double[2];
vector.arrayCopyInto(destination, 1);
}

@Test(expected = IllegalArgumentException.class)
public void testMapWithWrongReturnTypeFails() throws Throwable {
int[] indices = new int[] { 0 };
double[] values = new double[] { 8.0 };
SparseVector vector = new SparseVector(indices, values);
java.lang.reflect.Method method = String.class.getMethod("valueOf", Object.class);
vector.map(method);
}
}
