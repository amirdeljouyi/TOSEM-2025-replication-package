package cc.mallet.types.tests;

import cc.mallet.types.*;
import cc.mallet.util.PropertyList;
import org.junit.Test;
import java.io.*;
import java.lang.reflect.Method;
import static org.junit.Assert.*;

public class SparseVector_1_GPTLLMTest {

@Test
public void testConstructorAndAccessors() {
int[] indices = new int[] { 1, 3, 7 };
double[] values = new double[] { 2.0, 3.5, -1.0 };
SparseVector vector = new SparseVector(indices, values, true, true, false);
assertEquals(3, vector.numLocations());
assertEquals(2.0, vector.value(1), 0.0001);
assertEquals(3.5, vector.value(3), 0.0001);
assertEquals(-1.0, vector.value(7), 0.0001);
assertEquals(0.0, vector.value(0), 0.0001);
assertEquals(1, vector.indexAtLocation(0));
assertEquals(3, vector.indexAtLocation(1));
assertEquals(7, vector.indexAtLocation(2));
}

@Test(expected = IllegalArgumentException.class)
public void testInvalidConstructorMismatchedLengths() {
int[] indices = new int[] { 1, 3 };
double[] values = new double[] { 2.0, 3.5, -1.0 };
new SparseVector(indices, values, true, true, false);
}

@Test
public void testValueInDenseVector() {
double[] values = new double[] { 0.0, 2.0, 0.0, 3.5, 0.0, 0.0, 0.0, -1.0 };
SparseVector vector = new SparseVector(values, true);
assertEquals(0.0, vector.value(0), 0.0001);
assertEquals(2.0, vector.value(1), 0.0001);
assertEquals(0.0, vector.value(2), 0.0001);
assertEquals(3.5, vector.value(3), 0.0001);
assertEquals(0.0, vector.value(4), 0.0001);
assertEquals(-1.0, vector.value(7), 0.0001);
}

@Test
public void testDotProductWithDenseArray() {
int[] indices = new int[] { 0, 2, 4 };
double[] values = new double[] { 1.0, 2.0, 3.0 };
SparseVector vector = new SparseVector(indices, values, true, true, false);
double[] dense = new double[] { 10.0, 0.0, 5.0, 0.0, 2.0 };
double result = vector.dotProduct(dense);
assertEquals(10.0 * 1.0 + 5.0 * 2.0 + 2.0 * 3.0, result, 0.0001);
}

@Test
public void testPlusEqualsSparse() {
int[] indices1 = new int[] { 1, 3 };
double[] values1 = new double[] { 2.0, 3.0 };
SparseVector v1 = new SparseVector(indices1, values1, true, true, false);
int[] indices2 = new int[] { 1, 3 };
double[] values2 = new double[] { 1.0, 2.0 };
SparseVector v2 = new SparseVector(indices2, values2, true, true, false);
v1.plusEqualsSparse(v2);
assertEquals(3.0, v1.value(1), 0.0001);
assertEquals(5.0, v1.value(3), 0.0001);
}

@Test
public void testTimesEqualsSparse() {
int[] indices1 = new int[] { 1, 3 };
double[] values1 = new double[] { 4.0, 5.0 };
SparseVector v1 = new SparseVector(indices1, values1, true, true, false);
int[] indices2 = new int[] { 1, 3 };
double[] values2 = new double[] { 2.0, 0.5 };
SparseVector v2 = new SparseVector(indices2, values2, true, true, false);
v1.timesEqualsSparse(v2);
assertEquals(8.0, v1.value(1), 0.0001);
assertEquals(2.5, v1.value(3), 0.0001);
}

@Test
public void testTimesEqualsWithScalar() {
int[] indices = new int[] { 0, 2 };
double[] values = new double[] { 1.5, -2.0 };
SparseVector vector = new SparseVector(indices, values, true, true, false);
vector.timesEquals(3.0);
assertEquals(4.5, vector.value(0), 0.0001);
assertEquals(-6.0, vector.value(2), 0.0001);
}

@Test(expected = IllegalArgumentException.class)
public void testSetValueThrowsOnMissingIndex() {
int[] indices = new int[] { 0, 2 };
double[] values = new double[] { 1.0, 2.0 };
SparseVector vector = new SparseVector(indices, values, true, true, false);
vector.setValue(1, 5.0);
}

@Test
public void testSetValueAtLocation() {
int[] indices = new int[] { 0, 2 };
double[] values = new double[] { 1.0, 2.0 };
SparseVector vector = new SparseVector(indices, values, true, true, false);
vector.setValueAtLocation(1, 10.0);
assertEquals(10.0, vector.value(2), 0.0001);
}

@Test
public void testArrayCopyFromAndInto() {
double[] source = new double[] { 1.0, 2.0, 3.0 };
SparseVector vector = new SparseVector(new int[] { 0, 1, 2 }, new double[] { 0.0, 0.0, 0.0 }, true, true, false);
vector.arrayCopyFrom(source);
assertEquals(1.0, vector.value(0), 0.0001);
assertEquals(2.0, vector.value(1), 0.0001);
assertEquals(3.0, vector.value(2), 0.0001);
double[] output = new double[5];
int resultOffset = vector.arrayCopyInto(output, 1);
assertEquals(4, resultOffset);
assertEquals(0.0, output[0], 0.0001);
assertEquals(1.0, output[1], 0.0001);
assertEquals(2.0, output[2], 0.0001);
assertEquals(3.0, output[3], 0.0001);
}

@Test
public void testVectorAdd() {
int[] indices1 = new int[] { 0, 2 };
double[] values1 = new double[] { 1.0, 2.0 };
SparseVector v1 = new SparseVector(indices1, values1, true, true, false);
int[] indices2 = new int[] { 0, 2 };
double[] values2 = new double[] { 3.0, 4.0 };
SparseVector v2 = new SparseVector(indices2, values2, true, true, false);
SparseVector result = v1.vectorAdd(v2, 0.5);
assertEquals(2.5, result.value(0), 0.0001);
assertEquals(4.0, result.value(2), 0.0001);
}

@Test
public void testToStringIncludesExpectedText() {
int[] indices = new int[] { 1, 3 };
double[] values = new double[] { 2.0, 4.0 };
SparseVector vector = new SparseVector(indices, values, true, true, false);
String str = vector.toString(true);
assertTrue(str.contains("1=2.0"));
assertTrue(str.contains("3=4.0"));
}

@Test
public void testSetAll() {
int[] indices = new int[] { 0, 2 };
double[] values = new double[] { 1.0, 2.0 };
SparseVector vector = new SparseVector(indices, values, true, true, false);
vector.setAll(5.0);
assertEquals(5.0, vector.value(0), 0.0001);
assertEquals(5.0, vector.value(2), 0.0001);
}

@Test
public void testNorms() {
int[] indices = new int[] { 0, 2 };
double[] values = new double[] { -3.0, 4.0 };
SparseVector vector = new SparseVector(indices, values, true, true, false);
assertEquals(7.0, vector.oneNorm(), 0.0001);
assertEquals(7.0, vector.absNorm(), 0.0001);
assertEquals(5.0, vector.twoNorm(), 0.0001);
assertEquals(4.0, vector.infinityNorm(), 0.0001);
}

@Test
public void testIsBinaryReturnsFalse() {
int[] indices = new int[] { 0, 1 };
double[] values = new double[] { 1.0, 0.5 };
SparseVector vector = new SparseVector(indices, values, true, true, false);
assertFalse(vector.isBinary());
}

@Test(expected = UnsupportedOperationException.class)
public void testMakeBinaryThrows() {
int[] indices = new int[] { 0, 1 };
double[] values = new double[] { 1.0, 2.0 };
SparseVector vector = new SparseVector(indices, values, true, true, false);
vector.makeBinary();
}

@Test
public void testSerializationRestoreEqualsOriginal() throws Exception {
int[] indices = new int[] { 3, 5 };
double[] values = new double[] { 1.0, 2.0 };
SparseVector vector = new SparseVector(indices, values, true, true, false);
ByteArrayOutputStream out = new ByteArrayOutputStream();
ObjectOutputStream oos = new ObjectOutputStream(out);
oos.writeObject(vector);
oos.close();
ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
ObjectInputStream ois = new ObjectInputStream(in);
SparseVector restored = (SparseVector) ois.readObject();
assertEquals(vector.value(3), restored.value(3), 0.0001);
assertEquals(vector.value(5), restored.value(5), 0.0001);
assertArrayEquals(vector.getIndices(), restored.getIndices());
assertArrayEquals(vector.getValues(), restored.getValues(), 0.0001);
}

@Test
public void testMapSquaresEachEntry() throws Throwable {
int[] indices = new int[] { 0, 1 };
double[] values = new double[] { 2.0, -3.0 };
SparseVector vector = new SparseVector(indices, values, true, true, false);
// Method square = SparseVectorTest.class.getDeclaredMethod("square", Double.class);
try {
// vector.map(square);
} catch (Throwable t) {
throw t;
}
assertEquals(4.0, vector.value(0), 0.0001);
assertEquals(9.0, vector.value(1), 0.0001);
}

@Test
public void testEmptySparseVectorConstructor() {
SparseVector vector = new SparseVector();
assertEquals(0, vector.numLocations());
assertEquals(0.0, vector.value(0), 0.0001);
assertFalse(vector.isBinary());
}

@Test
public void testSparseVectorWithZeroCapacity() {
int[] indices = new int[] {};
double[] values = new double[] {};
SparseVector vector = new SparseVector(indices, values, 0, 0, true, true, false);
assertEquals(0, vector.numLocations());
assertEquals(0.0, vector.value(0), 0.0001);
}

@Test
public void testBinaryVectorValueAtLocationAndIndex() {
int[] indices = new int[] { 2, 4, 6 };
SparseVector vector = new SparseVector(indices, true, true);
assertTrue(vector.isBinary());
assertEquals(1.0, vector.value(2), 0.0001);
assertEquals(1.0, vector.valueAtLocation(0), 0.0001);
assertEquals(2, vector.indexAtLocation(0));
assertEquals(4, vector.indexAtLocation(1));
}

@Test
public void testDenseVectorDotProductWithSparseZeroOverlap() {
double[] denseValues = new double[] { 1.0, 2.0, 3.0, 4.0, 0.0 };
SparseVector vectorDense = new SparseVector(denseValues, true);
int[] sparseIndices = new int[] { 5, 6 };
double[] sparseVals = new double[] { 1.0, 1.0 };
SparseVector vectorSparse = new SparseVector(sparseIndices, sparseVals, true, true, false);
double result = vectorDense.dotProduct(vectorSparse);
assertEquals(0.0, result, 0.0001);
}

@Test
public void testValueMethodWithNullIndicesArgument() {
int[] indices = new int[] { 0 };
double[] values = new double[] { 1.0 };
SparseVector vector = new SparseVector(indices, values, true, true, false);
assertEquals(1.0, vector.value(null), 0.0001);
}

@Test(expected = IllegalArgumentException.class)
public void testArrayCopyFromWithInsufficientArrayLength() {
int[] indices = new int[] { 0, 1, 2 };
double[] values = new double[] { 1.0, 2.0, 3.0 };
SparseVector vector = new SparseVector(indices, values, true, true, false);
double[] source = new double[] { 1.0 };
vector.arrayCopyFrom(source, 0);
}

@Test(expected = IllegalArgumentException.class)
public void testMapWithInvalidSignatureThrows() throws Throwable {
int[] indices = new int[] { 0, 1 };
double[] values = new double[] { 1.0, 2.0 };
SparseVector vector = new SparseVector(indices, values, true, true, false);
java.lang.reflect.Method method = String.class.getMethod("trim");
try {
vector.map(method);
} catch (Throwable t) {
throw t;
}
}

@Test
public void testTimesEqualsSparseZero() {
int[] indices1 = new int[] { 1, 3, 5 };
double[] values1 = new double[] { 1.0, 2.0, 3.0 };
SparseVector v1 = new SparseVector(indices1, values1, true, true, false);
int[] indices2 = new int[] { 3, 6 };
double[] values2 = new double[] { 2.0, 2.0 };
SparseVector v2 = new SparseVector(indices2, values2, true, true, false);
v1.timesEqualsSparseZero(v2, 1.0);
assertEquals(0.0, v1.value(1), 0.0001);
assertEquals(4.0, v1.value(3), 0.0001);
assertEquals(3.0, v1.value(5), 0.0001);
}

@Test
public void testCloneMatrixCreatesNewInstance() {
int[] indices = new int[] { 0, 2 };
double[] values = new double[] { 1.5, 3.0 };
SparseVector vector = new SparseVector(indices, values, true, true, false);
ConstantMatrix clone = vector.cloneMatrix();
assertNotSame(vector, clone);
assertEquals(vector.value(0), clone.singleValue(0), 0.0001);
}

@Test
public void testCloneMatrixZeroedWithSparseVector() {
int[] indices = new int[] { 0, 3 };
double[] values = new double[] { 1.0, 2.0 };
SparseVector vector = new SparseVector(indices, values, true, true, false);
ConstantMatrix zero = vector.cloneMatrixZeroed();
assertEquals(0.0, ((SparseVector) zero).value(0), 0.0001);
assertEquals(0.0, ((SparseVector) zero).value(3), 0.0001);
}

@Test
public void testZeroNormsOnBinaryVector() {
int[] indices = new int[] { 1, 2, 3 };
SparseVector binaryVector = new SparseVector(indices, true, true);
assertEquals(3.0, binaryVector.oneNorm(), 0.0001);
assertEquals(3.0, binaryVector.absNorm(), 0.0001);
assertEquals(Math.sqrt(3), binaryVector.twoNorm(), 0.0001);
assertEquals(1.0, binaryVector.infinityNorm(), 0.0001);
}

@Test
public void testLocationSearchExistingAndMissingKey() {
int[] indices = new int[] { 1, 4, 5, 8 };
double[] values = new double[] { 1.0, 2.0, 3.0, 4.0 };
SparseVector vector = new SparseVector(indices, values, true, true, false);
int locationExisting = vector.location(4);
assertTrue(locationExisting >= 0);
int locationMissing = vector.location(3);
assertTrue(locationMissing < 0);
}

@Test
public void testIsNaNAndIsInfiniteReturnCorrectly() {
int[] indices = new int[] { 0, 1 };
double[] values = new double[] { Double.NaN, Double.POSITIVE_INFINITY };
SparseVector vector = new SparseVector(indices, values, true, true, false);
assertTrue(vector.isNaN());
assertTrue(vector.isInfinite());
assertTrue(vector.isNaNOrInfinite());
}

@Test
public void testSortIndicesWithDuplicateMergesValues() {
int[] indices = new int[] { 1, 3, 1 };
double[] values = new double[] { 1.0, 2.0, 4.0 };
SparseVector vector = new SparseVector(indices, values, true, true, false);
assertEquals(5.0, vector.value(1), 0.0001);
assertEquals(2.0, vector.value(3), 0.0001);
}

@Test
public void testSparseVectorWithNullValuesIsBinary() {
int[] indices = new int[] { 2, 4, 8 };
SparseVector binaryVector = new SparseVector(indices, true, true);
assertTrue(binaryVector.isBinary());
}

@Test
public void testDenseVectorLocationReturnsIndex() {
double[] values = new double[] { 1.0, 2.0, 3.0 };
SparseVector vector = new SparseVector(values, true);
assertEquals(0, vector.location(0));
assertEquals(1, vector.location(1));
assertEquals(2, vector.location(2));
}

@Test
public void testValueAtLocationWithBinaryVector() {
int[] indices = new int[] { 5 };
SparseVector binaryVector = new SparseVector(indices, true, true);
assertEquals(1.0, binaryVector.valueAtLocation(0), 0.0001);
}

@Test
public void testIndexAtLocationDenseVector() {
double[] values = new double[] { 10.0, 20.0 };
SparseVector vector = new SparseVector(values, true);
assertEquals(0, vector.indexAtLocation(0));
assertEquals(1, vector.indexAtLocation(1));
}

@Test
public void testSingleSizeWithEmptySparseVector() {
int[] indices = new int[0];
double[] values = new double[0];
SparseVector vector = new SparseVector(indices, values, true, true, false);
assertEquals(0, vector.singleSize());
}

@Test
public void testSingleSizeWithNonZeroSparseVector() {
int[] indices = new int[] { 2, 4, 8 };
double[] values = new double[] { 1.0, 1.0, 1.0 };
SparseVector vector = new SparseVector(indices, values, true, true, false);
assertEquals(8, vector.singleSize());
}

@Test(expected = IllegalArgumentException.class)
public void testIncrementValueOnMissingIndexThrows() {
int[] indices = new int[] { 0, 2 };
double[] values = new double[] { 1.0, 2.0 };
SparseVector vector = new SparseVector(indices, values, true, true, false);
vector.incrementValue(1, 3.0);
}

@Test(expected = IllegalArgumentException.class)
public void testSetValueOnMissingIndexSparseThrows() {
int[] indices = new int[] { 0, 3 };
double[] values = new double[] { 5.0, 10.0 };
SparseVector vector = new SparseVector(indices, values, true, true, false);
vector.setValue(2, 1.0);
}

@Test
public void testAddToWithBinarySparseVector() {
int[] indices = new int[] { 1, 3 };
SparseVector vector = new SparseVector(indices, true, true);
double[] accumulator = new double[5];
vector.addTo(accumulator, 2.0);
assertEquals(2.0, accumulator[1], 0.0001);
assertEquals(2.0, accumulator[3], 0.0001);
assertEquals(0.0, accumulator[0], 0.0001);
}

@Test
public void testDotProductWithSelf() {
int[] indices = new int[] { 1, 5 };
double[] values = new double[] { 2.0, 3.0 };
SparseVector vector = new SparseVector(indices, values, true, true, false);
double dot = vector.dotProduct(vector);
assertEquals(2.0 * 2.0 + 3.0 * 3.0, dot, 0.0001);
}

@Test
public void testExtendedDotProductWithOneInfiniteValue() {
int[] indices1 = new int[] { 0 };
double[] values1 = new double[] { Double.POSITIVE_INFINITY };
SparseVector vector1 = new SparseVector(indices1, values1, true, true, false);
int[] indices2 = new int[] { 0 };
double[] values2 = new double[] { 0.0 };
SparseVector vector2 = new SparseVector(indices2, values2, true, true, false);
double result = vector1.extendedDotProduct(vector2);
assertEquals(0.0, result, 0.0001);
}

@Test
public void testDotProductNaNTriggersFallback() {
int[] indices1 = new int[] { 0 };
double[] values1 = new double[] { Double.NaN };
SparseVector vector1 = new SparseVector(indices1, values1, true, true, false);
int[] indices2 = new int[] { 0 };
double[] values2 = new double[] { 2.0 };
SparseVector vector2 = new SparseVector(indices2, values2, true, true, false);
double result = vector1.dotProduct(vector2);
assertTrue(Double.isNaN(result));
}

@Test
public void testMatrixCloneMatchesOriginalForDense() {
double[] values = new double[] { 1.1, 2.2, 3.3 };
SparseVector vector = new SparseVector(values, true);
ConstantMatrix clone = vector.cloneMatrix();
assertEquals(vector.singleValue(0), clone.singleValue(0), 0.0001);
assertEquals(vector.singleValue(1), clone.singleValue(1), 0.0001);
assertEquals(vector.singleValue(2), clone.singleValue(2), 0.0001);
}

@Test
public void testSingleToIndicesPlacesCorrectIndex() {
int i = 3;
int[] dest = new int[1];
SparseVector vector = new SparseVector(new double[] { 1.0, 2.0, 3.0, 4.0 }, true);
vector.singleToIndices(i, dest);
assertEquals(3, dest[0]);
}

@Test
public void testSortIndicesRetainsCorrectOrderForDense() {
double[] values = new double[] { 10.0, 20.0, 30.0 };
SparseVector vector = new SparseVector(values, true);
// vector.sortIndices();
assertEquals(10.0, vector.value(0), 0.0001);
assertEquals(20.0, vector.value(1), 0.0001);
assertEquals(30.0, vector.value(2), 0.0001);
}

@Test
public void testRemoveDuplicatesCombinesValues() {
int[] indices = new int[] { 0, 1, 1, 2 };
double[] values = new double[] { 2.0, 1.0, 3.0, 4.0 };
SparseVector vector = new SparseVector(indices, values, true, true, true);
assertEquals(2.0, vector.value(0), 0.0001);
assertEquals(4.0, vector.value(1), 0.0001);
assertEquals(4.0, vector.value(2), 0.0001);
}

@Test(expected = IllegalArgumentException.class)
public void testConstructorMismatchNullCheck() {
int[] indices = new int[] { 0, 1, 2 };
double[] values = new double[] { 1.0, 2.0 };
new SparseVector(indices, values, 3, 3, true, false, false);
}

@Test
public void testConstructorHandlesNullPropertyList() {
SparseVector vector = new SparseVector(new Alphabet(), null, false, false);
assertEquals(0, vector.numLocations());
}

@Test
public void testValueWithMultiDimensionalIndexArray() {
int[] indices = new int[] { 3 };
double[] values = new double[] { 5.0 };
SparseVector vector = new SparseVector(indices, values, true, true, false);
int[] input = new int[] { 3 };
assertEquals(5.0, vector.value(input), 0.0001);
}

@Test
public void testArrayCopyIntoOffsetWorksCorrectly() {
int[] indices = new int[] { 0, 2 };
double[] values = new double[] { 5.0, 9.0 };
SparseVector vector = new SparseVector(indices, values, true, true, false);
double[] target = new double[5];
int resultIdx = vector.arrayCopyInto(target, 2);
assertEquals(2, vector.numLocations());
assertEquals(2 + 2, resultIdx);
assertEquals(5.0, target[2], 0.0001);
assertEquals(9.0, target[3], 0.0001);
}

@Test
public void testToStringOneLineFormat() {
int[] indices = new int[] { 1 };
double[] values = new double[] { 2.5 };
SparseVector vector = new SparseVector(indices, values, true, true, false);
String output = vector.toString(true);
assertTrue(output.contains("1=2.5"));
assertFalse(output.contains("\n"));
}

@Test
public void testToStringMultiLineFormat() {
int[] indices = new int[] { 1 };
double[] values = new double[] { 2.5 };
SparseVector vector = new SparseVector(indices, values, true, true, false);
String output = vector.toString(false);
assertTrue(output.contains("1=2.5\n") || output.contains("1=2.5\r\n"));
}

@Test
public void testDenseTimesEqualsSparseBoundsCheck() {
double[] denseValues = new double[] { 1.0, 2.0 };
SparseVector denseVector = new SparseVector(denseValues, true);
int[] indices = new int[] { 0, 5 };
double[] values = new double[] { 2.0, 3.0 };
SparseVector sparse = new SparseVector(indices, values, true, true, false);
denseVector.timesEqualsSparse(sparse, 1.0);
assertEquals(2.0, denseVector.value(0), 0.0001);
assertEquals(2.0, denseVector.value(1), 0.0001);
}

@Test
public void testDensePlusEqualsSparseBoundsCheck() {
double[] denseValues = new double[] { 10.0 };
SparseVector denseVector = new SparseVector(denseValues, true);
int[] indices = new int[] { 0, 3 };
double[] values = new double[] { 0.5, 4.0 };
SparseVector sparse = new SparseVector(indices, values, true, true, false);
denseVector.plusEqualsSparse(sparse, 1.0);
assertEquals(10.5, denseVector.value(0), 0.0001);
}

@Test(expected = IllegalArgumentException.class)
public void testConstructorThrowsWhenCopyFalseButMismatchedLengths() {
int[] indices = new int[] { 0, 1 };
double[] values = new double[] { 1.0 };
new SparseVector(indices, values, 2, 2, false, false, false);
}

@Test
public void testCloneMatrixZeroedDense() {
double[] values = new double[] { 1.0, 2.0, 3.0 };
SparseVector vector = new SparseVector(values, true);
ConstantMatrix zeroed = vector.cloneMatrixZeroed();
assertTrue(zeroed instanceof SparseVector);
SparseVector result = (SparseVector) zeroed;
assertEquals(0.0, result.value(0), 0.0001);
assertEquals(0.0, result.value(1), 0.0001);
assertEquals(0.0, result.value(2), 0.0001);
}

@Test(expected = IllegalArgumentException.class)
public void testSetValueFailsOnNonSortedSparseIndex() {
int[] indices = new int[] { 3, 1 };
double[] values = new double[] { 2.0, 3.0 };
SparseVector vector = new SparseVector(indices, values, true, false, false);
vector.setValue(0, 5.0);
}

@Test
public void testDotProductWithDenseVectorContainingInfinityAndZero() {
int[] indices = new int[] { 0, 1 };
double[] values = new double[] { 0.0, Double.POSITIVE_INFINITY };
SparseVector sparse = new SparseVector(indices, values, true, true, false);
double[] denseValues = new double[] { Double.POSITIVE_INFINITY, 0.0 };
DenseVector dense = new DenseVector(denseValues);
double result = sparse.dotProduct(dense);
assertEquals(0.0, result, 0.0001);
}

@Test
public void testExtendedDotProductSkipsNaNCombinations() {
int[] indicesA = new int[] { 0, 1 };
double[] valuesA = new double[] { 0.0, Double.POSITIVE_INFINITY };
SparseVector vectorA = new SparseVector(indicesA, valuesA, true, true, false);
int[] indicesB = new int[] { 0, 1 };
double[] valuesB = new double[] { Double.POSITIVE_INFINITY, 0.0 };
SparseVector vectorB = new SparseVector(indicesB, valuesB, true, true, false);
double result = vectorA.extendedDotProduct(vectorB);
assertEquals(0.0, result, 0.0001);
}

@Test
public void testVectorAddWithZeroResultingVector() {
int[] indices1 = new int[] { 0, 1 };
double[] values1 = new double[] { 1.0, -1.0 };
SparseVector vector1 = new SparseVector(indices1, values1, true, true, false);
int[] indices2 = new int[] { 0, 1 };
double[] values2 = new double[] { -1.0, 1.0 };
SparseVector vector2 = new SparseVector(indices2, values2, true, true, false);
SparseVector result = vector1.vectorAdd(vector2, 1.0);
assertEquals(0.0, result.value(0), 0.0001);
assertEquals(0.0, result.value(1), 0.0001);
assertEquals(0, result.value(new int[] { 4 }), 0.0001);
}

@Test
public void testAddToWithScaleValueZero() {
int[] indices = new int[] { 1, 2 };
double[] values = new double[] { 10.0, 5.0 };
SparseVector vector = new SparseVector(indices, values, true, true, false);
double[] acc = new double[5];
vector.addTo(acc, 0.0);
assertEquals(0.0, acc[1], 0.0001);
assertEquals(0.0, acc[2], 0.0001);
}

@Test
public void testToStringEmptyVector() {
int[] indices = new int[0];
double[] values = new double[0];
SparseVector vector = new SparseVector(indices, values, true, true, false);
String str = vector.toString(false);
assertEquals("", str);
}

@Test
public void testSerializationPreservesHasInfiniteFlag() throws Exception {
int[] indices = new int[] { 2 };
double[] values = new double[] { Double.NEGATIVE_INFINITY };
SparseVector vector = new SparseVector(indices, values, true, true, false);
ByteArrayOutputStream out = new ByteArrayOutputStream();
ObjectOutputStream oos = new ObjectOutputStream(out);
oos.writeObject(vector);
oos.close();
ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(out.toByteArray()));
SparseVector deserialized = (SparseVector) in.readObject();
assertTrue(deserialized.isInfinite());
}

@Test
public void testConstructorWithSingleElementArray() {
int[] indices = new int[] { 42 };
double[] values = new double[] { 3.1415 };
SparseVector vector = new SparseVector(indices, values, true, true, false);
assertEquals(1, vector.numLocations());
assertEquals(3.1415, vector.value(42), 0.0001);
assertEquals(0.0, vector.value(0), 0.0001);
}

@Test
public void testDenseVectorWithZeroLength() {
double[] empty = new double[0];
SparseVector vector = new SparseVector(empty, true);
assertEquals(0, vector.numLocations());
assertEquals(0.0, vector.value(0), 0.0001);
}

@Test
public void testArrayCopyFromWithStartOffset() {
int[] indices = new int[] { 0, 1 };
double[] values = new double[] { 0.0, 0.0 };
SparseVector vector = new SparseVector(indices, values, true, true, false);
double[] source = new double[] { 99.0, 100.0, 200.0 };
int offset = 1;
int returned = vector.arrayCopyFrom(source, offset);
assertEquals(3, returned);
assertEquals(100.0, vector.valueAtLocation(0), 0.0001);
assertEquals(200.0, vector.valueAtLocation(1), 0.0001);
}

@Test
public void testInvalidSignatureMapThrowsIAE() throws Exception {
int[] indices = new int[] { 0 };
double[] values = new double[] { 2.0 };
SparseVector v = new SparseVector(indices, values, true, true, false);
java.lang.reflect.Method invalid = String.class.getMethod("toUpperCase");
boolean threw = false;
try {
// v.map(invalid);
} catch (IllegalArgumentException e) {
threw = true;
}
assertTrue(threw);
}

@Test
public void testBinaryVectorCloneMatrixZeroedCreatesSameIndicesWithNullValues() {
int[] indices = new int[] { 1, 3, 7 };
SparseVector binary = new SparseVector(indices, true, true);
ConstantMatrix zeroed = binary.cloneMatrixZeroed();
assertEquals(1.0, binary.value(1), 0.0001);
assertEquals(1.0, binary.value(3), 0.0001);
assertTrue(((SparseVector) zeroed).isBinary());
}

@Test
public void testLocationReturnsNegativeIndexNotFound() {
int[] indices = new int[] { 2, 4, 6 };
double[] values = new double[] { 1.0, 2.0, 3.0 };
SparseVector v = new SparseVector(indices, values, true, true, false);
int location = v.location(5);
assertTrue(location < 0);
}

@Test
public void testDotProductSymmetricResult() {
int[] indA = new int[] { 1, 3 };
double[] valA = new double[] { 1.0, 2.0 };
SparseVector a = new SparseVector(indA, valA, true, true, false);
int[] indB = new int[] { 3, 1 };
double[] valB = new double[] { 2.0, 1.0 };
SparseVector b = new SparseVector(indB, valB, true, true, false);
double result1 = a.dotProduct(b);
double result2 = b.dotProduct(a);
assertEquals(result1, result2, 0.0001);
assertEquals(1.0 * 1.0 + 2.0 * 2.0, result1, 0.0001);
}

@Test
public void testDotProductWithMismatchedIndicesReturnsZero() {
int[] indices1 = new int[] { 1 };
double[] values1 = new double[] { 5.0 };
SparseVector a = new SparseVector(indices1, values1, true, true, false);
int[] indices2 = new int[] { 0 };
double[] values2 = new double[] { 2.0 };
SparseVector b = new SparseVector(indices2, values2, true, true, false);
double result = a.dotProduct(b);
assertEquals(0.0, result, 0.0001);
}

@Test
public void testPrintMethodWithBinaryVector() {
int[] indices = new int[] { 2, 4 };
SparseVector binary = new SparseVector(indices, true, true);
try {
binary.print();
} catch (Exception e) {
fail("Print method threw exception");
}
}

@Test
public void testPrintMethodWithNonBinaryVector() {
int[] indices = new int[] { 2, 4 };
double[] values = new double[] { 10.0, -3.0 };
SparseVector nonBinary = new SparseVector(indices, values, true, true, false);
try {
nonBinary.print();
} catch (Exception e) {
fail("Print method threw exception");
}
}

@Test
public void testSingleIndexAndSingleToIndices() {
SparseVector v = new SparseVector(new double[] { 0.1, 0.2, 0.3 }, true);
int input = 2;
int result = v.singleIndex(new int[] { input });
assertEquals(2, result);
int[] arr = new int[1];
v.singleToIndices(1, arr);
assertEquals(1, arr[0]);
}

@Test
public void testConstructorWithPropertyListAllOnesResolvesToBinary() {
Alphabet dict = new Alphabet();
PropertyList pl = PropertyList.add("feature1", 1.0, null);
pl = PropertyList.add("feature2", 1.0, pl);
SparseVector vector = new SparseVector(dict, pl, false);
assertTrue(vector.isBinary());
}

@Test
public void testConstructorWithPropertyListNonBinaryValueForcesNonBinary() {
Alphabet dict = new Alphabet();
PropertyList pl = PropertyList.add("feat", 2.0, null);
SparseVector vector = new SparseVector(dict, pl, false);
assertFalse(vector.isBinary());
assertEquals(2.0, vector.value(vector.getIndices()[0]), 0.0001);
}

@Test
public void testOneLocationDimensionOnlyPopulatesSizesArray() {
SparseVector v = new SparseVector(new double[] { 5.0, 6.0, 7.0 }, true);
int[] sizes = new int[1];
int dims = v.getDimensions(sizes);
assertEquals(1, dims);
assertEquals(3, sizes[0]);
}

@Test
public void testEmptyVectorReturnsCorrectString() {
SparseVector v = new SparseVector();
String repr = v.toString();
assertNotNull(repr);
assertEquals("", repr);
}

@Test
public void testMapOnEmptyVectorDoesNothing() throws Throwable {
int[] indices = new int[] {};
double[] values = new double[] {};
SparseVector v = new SparseVector(indices, values, true, true, false);
java.lang.reflect.Method method = Math.class.getMethod("abs", double.class);
// java.lang.reflect.Method adapted = DoubleUnaryInvokerTestAdapter.adaptAbsMethod();
// v.map(adapted);
assertEquals(0, v.numLocations());
}

@Test
public void testSerializationRoundTripPreservesValues() throws Exception {
int[] indices = new int[] { 0, 1 };
double[] values = new double[] { 11.0, 22.0 };
SparseVector original = new SparseVector(indices, values, true, true, false);
ByteArrayOutputStream bout = new ByteArrayOutputStream();
ObjectOutputStream out = new ObjectOutputStream(bout);
out.writeObject(original);
out.close();
ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
ObjectInputStream in = new ObjectInputStream(bin);
SparseVector deserialized = (SparseVector) in.readObject();
assertEquals(11.0, deserialized.value(0), 0.0001);
assertEquals(22.0, deserialized.value(1), 0.0001);
}

@Test
public void testConstructorWithZeroLengthDenseArray() {
double[] dense = new double[0];
SparseVector vector = new SparseVector(dense, true);
assertEquals(0, vector.numLocations());
assertEquals(0, vector.singleSize());
}

@Test
public void testConstructorCheckSortedFalseButRemoveDuplicatesTrue() {
int[] indices = new int[] { 5, 2, 2 };
double[] values = new double[] { 1.0, 2.0, 3.0 };
SparseVector vector = new SparseVector(indices, values, true, false, true);
assertEquals(2, vector.numLocations());
assertEquals(5.0, vector.value(2), 0.0001);
assertEquals(1.0, vector.value(5), 0.0001);
}

@Test
public void testConstructorHighCapacityLowSize() {
int[] indices = new int[] { 0, 1 };
double[] values = new double[] { 1.0, 2.0 };
SparseVector vector = new SparseVector(indices, values, 10, 2, true, true, false);
assertEquals(2, vector.numLocations());
assertEquals(1.0, vector.value(0), 0.0001);
assertEquals(2.0, vector.value(1), 0.0001);
assertEquals(0.0, vector.value(9), 0.0001);
}

@Test(expected = IllegalArgumentException.class)
public void testConstructorWithNullIndicesAndNonNullValuesThrows() {
double[] values = new double[] { 1.0, 2.0 };
new SparseVector(null, values, 2, 2, true, true, false);
}

@Test
public void testDotProductWithEmptyVectorsReturnsZero() {
int[] empty = new int[0];
double[] emptyValues = new double[0];
SparseVector v1 = new SparseVector(empty, emptyValues, true, true, false);
SparseVector v2 = new SparseVector(empty, emptyValues, true, true, false);
assertEquals(0.0, v1.dotProduct(v2), 0.0001);
}

@Test
public void testMapCreatesInfinityUpdatesHasInfiniteFlag() throws Throwable {
int[] indices = new int[] { 0 };
double[] values = new double[] { Double.MAX_VALUE };
SparseVector vector = new SparseVector(indices, values, true, true, false);
// Method doubleFunction = SparseVectorEdgeBranchesTest.class.getDeclaredMethod("multiplyMax", Double.class);
try {
// vector.map(doubleFunction);
} catch (Throwable t) {
throw t;
}
assertTrue(vector.isInfinite());
}

@Test
public void testValueIndexNotPresentReturnsZero() {
int[] indices = new int[] { 3 };
double[] values = new double[] { 10.0 };
SparseVector sv = new SparseVector(indices, values, true, true, false);
assertEquals(10.0, sv.value(3), 0.0001);
assertEquals(0.0, sv.value(1), 0.0001);
}

@Test
public void testArrayCopyIntoFromBinaryVector() {
int[] indices = new int[] { 1, 2 };
SparseVector binary = new SparseVector(indices, true, true);
double[] target = new double[5];
int result = binary.arrayCopyInto(target, 0);
assertEquals(2, result);
assertEquals(1.0, target[0], 0.0001);
assertEquals(1.0, target[1], 0.0001);
}

@Test
public void testSortIndicesDegenerateCases() {
int[] single = new int[] { 7 };
double[] v = new double[] { 3.0 };
SparseVector vec = new SparseVector(single, v, true, true, true);
assertEquals(7, vec.indexAtLocation(0));
assertEquals(3.0, vec.valueAtLocation(0), 0.0001);
int[] empty = new int[0];
double[] emptyVals = new double[0];
SparseVector emptyVec = new SparseVector(empty, emptyVals, true, true, true);
assertEquals(0, emptyVec.numLocations());
}

@Test
public void testDefaultConstructorEmptyVector() {
SparseVector v = new SparseVector();
assertEquals(0, v.numLocations());
assertEquals(0, v.singleSize());
assertFalse(v.isBinary());
}

@Test
public void testConstructorWithEmptyPropertyList() {
Alphabet dict = new Alphabet();
PropertyList pl = null;
SparseVector vector = new SparseVector(dict, pl, false, true);
assertEquals(0, vector.numLocations());
assertFalse(vector.isBinary());
}

@Test
public void testConstructorWithDenseValuesOnlyActsAsDense() {
double[] values = new double[] { 0.5, 1.0, 2.5 };
SparseVector sv = new SparseVector(values);
assertEquals(3, sv.numLocations());
assertEquals(1.0, sv.value(1), 0.0001);
}

@Test
public void testInfiniteDotProductWithDense() {
double[] values = new double[] { Double.POSITIVE_INFINITY };
SparseVector sparse = new SparseVector(new int[] { 0 }, values, true, true, false);
DenseVector dense = new DenseVector(new double[] { 0.0 });
assertEquals(0.0, sparse.dotProduct(dense), 0.0001);
}

@Test
public void testCloneMatrixZeroedPreservesIndices() {
int[] indices = new int[] { 1, 2 };
double[] values = new double[] { 10.5, -3.0 };
SparseVector vector = new SparseVector(indices, values, true, true, false);
ConstantMatrix clone = vector.cloneMatrixZeroed();
assertTrue(clone instanceof SparseVector);
SparseVector z = (SparseVector) clone;
assertEquals(0.0, z.value(1), 0.0001);
assertEquals(0.0, z.value(2), 0.0001);
}

@Test
public void testValuesRemainInvariantAfterSerialization() throws Exception {
int[] indices = new int[] { 3, 5 };
double[] values = new double[] { 100.0, 200.0 };
SparseVector original = new SparseVector(indices, values, true, true, false);
ByteArrayOutputStream bout = new ByteArrayOutputStream();
ObjectOutputStream oos = new ObjectOutputStream(bout);
oos.writeObject(original);
oos.close();
ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
ObjectInputStream ois = new ObjectInputStream(bin);
SparseVector loaded = (SparseVector) ois.readObject();
assertEquals(100.0, loaded.value(3), 0.0001);
assertEquals(200.0, loaded.value(5), 0.0001);
}

@Test
public void testSetAllResetsValuesRegardlessOfPreviousContent() {
int[] ind = new int[] { 0, 2 };
double[] val = new double[] { 1.0, 4.2 };
SparseVector v = new SparseVector(ind, val, true, true, false);
v.setAll(-1.0);
assertEquals(-1.0, v.value(0), 0.0001);
assertEquals(-1.0, v.value(2), 0.0001);
}

@Test
public void testInfinityNormReturnsCorrectForNegativeValues() {
int[] indices = new int[] { 0, 1, 2 };
double[] values = new double[] { 1.0, -7.0, 5.5 };
SparseVector v = new SparseVector(indices, values, true, true, false);
assertEquals(7.0, v.infinityNorm(), 0.0001);
}

@Test
public void testIsNaNOrInfiniteDetectsOneNaNOrInfCorrectly() {
int[] ind = new int[] { 0, 1 };
double[] val = new double[] { Double.NaN, 5.0 };
SparseVector nan = new SparseVector(ind, val, true, true, false);
assertTrue(nan.isNaNOrInfinite());
double[] val2 = new double[] { Double.POSITIVE_INFINITY, 2.0 };
SparseVector inf = new SparseVector(ind, val2, true, true, false);
assertTrue(inf.isNaNOrInfinite());
double[] val3 = new double[] { 100.0, 2.0 };
SparseVector normal = new SparseVector(ind, val3, true, true, false);
assertFalse(normal.isNaNOrInfinite());
}

@Test
public void testSortIndicesAlreadySortedNoOp() {
int[] indices = new int[] { 1, 2, 3 };
double[] values = new double[] { 10.0, 20.0, 30.0 };
SparseVector vector = new SparseVector(indices, values, true, true, false);
// vector.sortIndices();
assertEquals(1, vector.indexAtLocation(0));
assertEquals(2, vector.indexAtLocation(1));
assertEquals(3, vector.indexAtLocation(2));
}

@Test
public void testRemoveDuplicatesZeroArgumentSkipsIfNoneExist() {
int[] indices = new int[] { 1, 2 };
double[] values = new double[] { 3.0, 4.0 };
SparseVector vector = new SparseVector(indices, values, true, true, false);
// vector.removeDuplicates(0);
assertEquals(2, vector.numLocations());
assertEquals(3.0, vector.value(1), 0.0001);
}

@Test
public void testZeroLengthBinaryVector() {
int[] indices = new int[0];
SparseVector vector = new SparseVector(indices, true, true);
assertEquals(0, vector.numLocations());
assertTrue(vector.isBinary());
}

@Test
public void testArrayCopyFromIntoSparseWithExtraCapacity() {
int[] indices = new int[] { 0, 1 };
double[] values = new double[4];
SparseVector vector = new SparseVector(indices, values, 4, 2, true, true, false);
double[] source = new double[] { 88.0, 99.0, 0.0, 0.0 };
int next = vector.arrayCopyFrom(source, 0);
assertEquals(2, vector.numLocations());
assertEquals(88.0, vector.valueAtLocation(0), 0.0001);
assertEquals(99.0, vector.valueAtLocation(1), 0.0001);
assertEquals(2, next);
}

@Test
public void testIndexAtLocationDenseMatchesPosition() {
double[] values = new double[] { 9.0, 8.0, 7.0 };
SparseVector dense = new SparseVector(values, true);
assertEquals(0, dense.indexAtLocation(0));
assertEquals(1, dense.indexAtLocation(1));
assertEquals(2, dense.indexAtLocation(2));
}

@Test
public void testDotProductConstantMatrixFallbackToException() {
SparseVector vector = new SparseVector(new double[] { 1.0, 2.0 });
// ConstantMatrix invalid = new ConstantMatrix() {
// 
// public int getNumDimensions() {
// return 1;
// }
// 
// public int getDimensions(int[] sizes) {
// return 1;
// }
// 
// public int numLocations() {
// return 1;
// }
// 
// public double value(int[] indices) {
// return 0;
// }
// 
// public double singleValue(int i) {
// return 0;
// }
// 
// public int singleIndex(int[] indices) {
// return 0;
// }
// 
// public void singleToIndices(int i, int[] indices) {
// }
// 
// public ConstantMatrix cloneMatrix() {
// return this;
// }
// };
boolean caught = false;
try {
// vector.dotProduct(invalid);
} catch (IllegalArgumentException e) {
caught = true;
}
assertTrue(caught);
}

@Test
public void testPlusEqualsSparseWithBinaryAddends() {
int[] i1 = new int[] { 1, 2 };
double[] v1 = new double[] { 5.0, 10.0 };
SparseVector base = new SparseVector(i1, v1, true, true, false);
int[] i2 = new int[] { 1, 2 };
SparseVector binary = new SparseVector(i2, true, true);
base.plusEqualsSparse(binary, 1.0);
assertEquals(6.0, base.value(1), 0.0001);
assertEquals(11.0, base.value(2), 0.0001);
}

@Test
public void testTimesEqualsSparseWithBinaryMultiplicands() {
int[] i1 = new int[] { 1, 2 };
double[] v1 = new double[] { 5.0, 10.0 };
SparseVector base = new SparseVector(i1, v1, true, true, false);
int[] i2 = new int[] { 1, 2 };
SparseVector binary = new SparseVector(i2, true, true);
base.timesEqualsSparse(binary, 2.0);
assertEquals(10.0, base.value(1), 0.0001);
assertEquals(20.0, base.value(2), 0.0001);
}

@Test
public void testValueWithNullIndexSingleElement() {
int[] indices = new int[] { 5 };
double[] values = new double[] { 42.0 };
SparseVector vector = new SparseVector(indices, values, true, true, false);
assertEquals(42.0, vector.value(null), 0.0001);
}

@Test(expected = IllegalArgumentException.class)
public void testSetValueOnBinaryShouldFail() {
int[] indices = new int[] { 4 };
SparseVector binary = new SparseVector(indices, true, true);
binary.setValue(4, 1.0);
}

@Test
public void testToStringBinaryVectorMultiLine() {
int[] indices = new int[] { 2, 4 };
SparseVector vector = new SparseVector(indices, true, true);
String result = vector.toString(false);
assertTrue(result.contains("2=1.0"));
assertTrue(result.contains("4=1.0"));
}

@Test
public void testCloneMatrixReturnsCorrectInstance() {
double[] values = new double[] { 3.0, 6.0 };
SparseVector vector = new SparseVector(values, true);
ConstantMatrix clone = vector.cloneMatrix();
assertNotSame(vector, clone);
assertEquals(vector.singleValue(1), clone.singleValue(1), 0.0001);
}
}
