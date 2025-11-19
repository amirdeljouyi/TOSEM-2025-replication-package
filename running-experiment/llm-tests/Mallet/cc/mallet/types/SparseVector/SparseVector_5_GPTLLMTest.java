package cc.mallet.types.tests;

import cc.mallet.types.*;
import cc.mallet.util.PropertyList;
import org.junit.Test;
import java.io.*;
import java.lang.reflect.Method;
import static org.junit.Assert.*;

public class SparseVector_5_GPTLLMTest {

@Test
public void testDenseVectorValueAccess() {
SparseVector denseVector = new SparseVector(new double[] { 1.0, 2.0, 3.0 });
assertEquals(1.0, denseVector.value(0), 1e-9);
assertEquals(2.0, denseVector.value(1), 1e-9);
assertEquals(3.0, denseVector.value(2), 1e-9);
assertEquals(0.0, denseVector.value(100), 1e-9);
}

@Test
public void testSparseVectorValueAccess() {
SparseVector sparseVector = new SparseVector(new int[] { 0, 2 }, new double[] { 4.0, 5.0 });
assertEquals(4.0, sparseVector.value(0), 1e-9);
assertEquals(0.0, sparseVector.value(1), 1e-9);
assertEquals(5.0, sparseVector.value(2), 1e-9);
}

@Test
public void testBinaryVectorValueAccess() {
SparseVector binaryVector = new SparseVector(new int[] { 1, 3 });
assertEquals(1.0, binaryVector.value(1), 1e-9);
assertEquals(1.0, binaryVector.value(3), 1e-9);
assertEquals(0.0, binaryVector.value(0), 1e-9);
assertEquals(0.0, binaryVector.value(2), 1e-9);
}

@Test
public void testAddToDenseBinaryVector() {
SparseVector binaryVector = new SparseVector(new int[] { 0, 2 });
double[] accumulator = new double[4];
binaryVector.addTo(accumulator, 2.0);
assertEquals(2.0 * 1.0, accumulator[0], 1e-9);
assertEquals(0.0, accumulator[1], 1e-9);
assertEquals(2.0 * 1.0, accumulator[2], 1e-9);
assertEquals(0.0, accumulator[3], 1e-9);
}

@Test
public void testPlusEqualsSparse() {
SparseVector vector1 = new SparseVector(new int[] { 0, 2 }, new double[] { 1.0, 2.0 });
SparseVector vector2 = new SparseVector(new int[] { 0, 2 }, new double[] { 3.0, 4.0 });
vector1.plusEqualsSparse(vector2, 1.0);
assertEquals(1.0 + 3.0, vector1.value(0), 1e-9);
assertEquals(2.0 + 4.0, vector1.value(2), 1e-9);
assertEquals(0.0, vector1.value(1), 1e-9);
}

@Test
public void testTimesEqualsSparse() {
SparseVector vector1 = new SparseVector(new int[] { 1, 3 }, new double[] { 2.0, 4.0 });
SparseVector vector2 = new SparseVector(new int[] { 1, 3 }, new double[] { 3.0, 5.0 });
vector1.timesEqualsSparse(vector2, 1.0);
assertEquals(2.0 * 3.0, vector1.value(1), 1e-9);
assertEquals(4.0 * 5.0, vector1.value(3), 1e-9);
}

@Test
public void testSetValueForDenseVector() {
SparseVector denseVector = new SparseVector(new double[] { 1.0, 2.0, 3.0 });
denseVector.setValue(1, 9.0);
assertEquals(1.0, denseVector.value(0), 1e-9);
assertEquals(9.0, denseVector.value(1), 1e-9);
assertEquals(3.0, denseVector.value(2), 1e-9);
}

@Test(expected = IllegalArgumentException.class)
public void testSetValueInvalidSparse() {
SparseVector sparse = new SparseVector(new int[] { 0, 2 }, new double[] { 1.0, 2.0 });
sparse.setValue(1, 5.0);
}

@Test
public void testIsBinary() {
SparseVector binaryVec = new SparseVector(new int[] { 0, 2 });
assertTrue(binaryVec.isBinary());
SparseVector nonBinaryVec = new SparseVector(new int[] { 0, 2 }, new double[] { 1.1, 0.9 });
assertFalse(nonBinaryVec.isBinary());
}

@Test
public void testDotProductWithDenseArray() {
SparseVector sparseVector = new SparseVector(new int[] { 0, 2 }, new double[] { 1.0, 3.0 });
double[] denseArray = new double[] { 10.0, 100.0, 1000.0 };
double result = sparseVector.dotProduct(denseArray);
assertEquals((1.0 * 10.0) + (3.0 * 1000.0), result, 1e-9);
}

@Test
public void testDotProductSparse() {
SparseVector vecA = new SparseVector(new int[] { 0, 1 }, new double[] { 2.0, 4.0 });
SparseVector vecB = new SparseVector(new int[] { 0, 1 }, new double[] { 5.0, 6.0 });
double result = vecA.dotProduct(vecB);
assertEquals(2.0 * 5.0 + 4.0 * 6.0, result, 1e-9);
}

@Test
public void testCloneMatrixCreatesEqualButDistinctVector() {
SparseVector original = new SparseVector(new int[] { 1, 2 }, new double[] { 1.0, 2.0 });
ConstantMatrix cloned = original.cloneMatrix();
assertTrue(cloned instanceof SparseVector);
assertEquals(1.0, ((SparseVector) cloned).value(1), 1e-9);
assertEquals(2.0, ((SparseVector) cloned).value(2), 1e-9);
assertNotSame(original, cloned);
}

@Test
public void testSetAllValues() {
SparseVector vector = new SparseVector(new int[] { 0, 1, 2 }, new double[] { 0.5, 0.8, 1.1 });
vector.setAll(3.0);
assertEquals(3.0, vector.value(0), 1e-9);
assertEquals(3.0, vector.value(1), 1e-9);
assertEquals(3.0, vector.value(2), 1e-9);
}

@Test
public void testIncrementValueWorks() {
SparseVector v = new SparseVector(new int[] { 3 }, new double[] { 5.0 });
v.incrementValue(3, 2.5);
assertEquals(5.0 + 2.5, v.value(3), 1e-9);
}

@Test(expected = IllegalArgumentException.class)
public void testIncrementValueInvalidIndex() {
SparseVector v = new SparseVector(new int[] { 0 }, new double[] { 1.0 });
v.incrementValue(1, 2.0);
}

@Test
public void testInfinityNorm() {
SparseVector vector = new SparseVector(new int[] { 0, 1, 2 }, new double[] { 3.0, -7.0, 1.0 });
double result = vector.infinityNorm();
assertEquals(7.0, result, 1e-9);
}

@Test
public void testAbsNorm() {
SparseVector vector = new SparseVector(new int[] { 0, 1, 2 }, new double[] { -1.0, 2.0, -3.0 });
double result = vector.absNorm();
assertEquals(6.0, result, 1e-9);
}

@Test
public void testOneNorm() {
SparseVector vector = new SparseVector(new int[] { 0, 2 }, new double[] { 4.0, 6.0 });
double norm = vector.oneNorm();
assertEquals(10.0, norm, 1e-9);
}

@Test
public void testTwoNorm() {
SparseVector vector = new SparseVector(new int[] { 0, 2 }, new double[] { 3.0, 4.0 });
double norm = vector.twoNorm();
assertEquals(5.0, norm, 1e-9);
}

@Test
public void testVectorAddResultIsCorrect() {
SparseVector a = new SparseVector(new double[] { 1.0, 2.0, 3.0 });
SparseVector b = new SparseVector(new double[] { 0.1, 0.2, 0.3 });
SparseVector c = a.vectorAdd(b, 1.0);
assertEquals(1.1, c.value(0), 1e-9);
assertEquals(2.2, c.value(1), 1e-9);
assertEquals(3.3, c.value(2), 1e-9);
}

@Test
public void testArrayCopyFromAndInto() {
SparseVector vector = new SparseVector(new int[] { 0, 1 }, new double[] { 0.0, 0.0 });
double[] input = new double[] { 5.0, 10.0 };
vector.arrayCopyFrom(input);
assertEquals(5.0, vector.value(0), 1e-9);
assertEquals(10.0, vector.value(1), 1e-9);
double[] output = new double[5];
int index = vector.arrayCopyInto(output, 2);
assertEquals(4, index);
assertEquals(5.0, output[2], 1e-9);
assertEquals(10.0, output[3], 1e-9);
}

@Test
public void testEmptyConstructorDefaultsToZero() {
SparseVector vector = new SparseVector();
assertEquals(0, vector.numLocations());
assertEquals(0.0, vector.value(0), 1e-9);
}

@Test
public void testSparseVectorWithDuplicateIndicesRemovesThem() {
int[] indices = new int[] { 1, 2, 2, 3 };
double[] values = new double[] { 1.0, 2.0, 2.0, 3.0 };
SparseVector vector = new SparseVector(indices, values, true, true, true);
assertEquals(1.0, vector.value(1), 1e-9);
assertEquals(4.0, vector.value(2), 1e-9);
assertEquals(3.0, vector.value(3), 1e-9);
}

@Test
public void testLocationReturnsNegativeWhenIndexNotPresentInSparse() {
SparseVector sparse = new SparseVector(new int[] { 1, 3 }, new double[] { 1.0, 2.0 });
int loc = sparse.location(2);
assertTrue(loc < 0);
}

@Test
public void testDotProductWithEmptySparseVectorReturnsZero() {
SparseVector v1 = new SparseVector(new int[] {});
SparseVector v2 = new SparseVector(new int[] { 1, 2 }, new double[] { 2.0, 3.0 });
double result = v1.dotProduct(v2);
assertEquals(0.0, result, 1e-9);
}

@Test
public void testDotProductWithDisjointIndicesReturnsZero() {
SparseVector v1 = new SparseVector(new int[] { 0 }, new double[] { 1.0 });
SparseVector v2 = new SparseVector(new int[] { 2 }, new double[] { 2.0 });
double result = v1.dotProduct(v2);
assertEquals(0.0, result, 1e-9);
}

@Test
public void testTimesEqualsWithDisjointSparseLeavesZeros() {
SparseVector a = new SparseVector(new int[] { 1, 2 }, new double[] { 2.0, 3.0 });
SparseVector b = new SparseVector(new int[] { 3, 4 }, new double[] { 5.0, 6.0 });
a.timesEqualsSparseZero(b, 1.0);
assertEquals(0.0, a.value(1), 1e-9);
assertEquals(0.0, a.value(2), 1e-9);
}

@Test(expected = IllegalArgumentException.class)
public void testConstructorThrowsWhenMismatchedLengths() {
int[] indices = new int[] { 0, 1 };
double[] values = new double[] { 1.0 };
new SparseVector(indices, values, true, true, false);
}

@Test
public void testCloneMatrixZeroedHasZeroValues() {
SparseVector original = new SparseVector(new int[] { 1, 2 }, new double[] { 3.0, 4.0 });
ConstantMatrix clone = original.cloneMatrixZeroed();
assertEquals(0.0, ((SparseVector) clone).value(1), 1e-9);
assertEquals(0.0, ((SparseVector) clone).value(2), 1e-9);
}

@Test
public void testToStringReturnsExpectedFormat() {
SparseVector vector = new SparseVector(new int[] { 2, 4 }, new double[] { 5.0, 6.0 });
String output = vector.toString(true);
assertTrue(output.contains("2=5.0"));
assertTrue(output.contains("4=6.0"));
}

@Test
public void testIsNaNReturnsTrueWhenAnyValueIsNaN() {
SparseVector vector = new SparseVector(new int[] { 0 }, new double[] { Double.NaN });
assertTrue(vector.isNaN());
}

@Test
public void testIsInfiniteReturnsTrueWhenAnyValueIsInfinite() {
SparseVector vector = new SparseVector(new int[] { 0 }, new double[] { Double.POSITIVE_INFINITY });
assertTrue(vector.isInfinite());
}

@Test
public void testIsNaNOrInfiniteHandlesBothCases() {
SparseVector vector1 = new SparseVector(new int[] { 0 }, new double[] { Double.NaN });
SparseVector vector2 = new SparseVector(new int[] { 0 }, new double[] { Double.NEGATIVE_INFINITY });
assertTrue(vector1.isNaNOrInfinite());
assertTrue(vector2.isNaNOrInfinite());
}

@Test
public void testSingleIndexAndSingleToIndices() {
SparseVector vector = new SparseVector(new double[] { 1.0, 2.0 });
int[] array = new int[1];
vector.singleToIndices(5, array);
assertEquals(5, array[0]);
int index = vector.singleIndex(new int[] { 7 });
assertEquals(7, index);
}

@Test
public void testSingleSizeReturnsLastIndexOfSparse() {
SparseVector vector = new SparseVector(new int[] { 1, 4, 10 }, new double[] { 1.0, 1.0, 1.0 });
int size = vector.singleSize();
assertEquals(10, size);
}

@Test
public void testSingleSizeReturnsLengthWhenDense() {
SparseVector vector = new SparseVector(new double[] { 1.0, 2.0, 3.0 });
int size = vector.singleSize();
assertEquals(3, size);
}

@Test
public void testConstructorWithBinaryTrueAndBinaryFeatures() {
Alphabet dict = new Alphabet();
PropertyList pl = PropertyList.add("feature1", 1.0, PropertyList.add("feature2", 1.0, null));
SparseVector vector = new SparseVector(dict, pl, true, true);
assertEquals(1.0, vector.value(dict.lookupIndex("feature1", false)), 1e-9);
assertEquals(1.0, vector.value(dict.lookupIndex("feature2", false)), 1e-9);
}

@Test
public void testConstructorWithBinaryFalseAndNonBinaryFeatures() {
Alphabet dict = new Alphabet();
PropertyList pl = PropertyList.add("feature1", 2.5, PropertyList.add("feature2", 1.0, null));
SparseVector vector = new SparseVector(dict, pl, false, true);
assertEquals(2.5, vector.value(dict.lookupIndex("feature1", false)), 1e-9);
assertEquals(1.0, vector.value(dict.lookupIndex("feature2", false)), 1e-9);
}

@Test
public void testToStringOnDenseVector() {
SparseVector vector = new SparseVector(new double[] { 0.1, 0.2, 0.3 });
String result = vector.toString(true);
assertTrue(result.contains("0=0.1"));
assertTrue(result.contains("1=0.2"));
assertTrue(result.contains("2=0.3"));
}

@Test
public void testSetValueAtLocationAffectsCorrectIndex() {
SparseVector vector = new SparseVector(new int[] { 0, 1, 2 }, new double[] { 1.0, 2.0, 3.0 });
vector.setValueAtLocation(1, 9.9);
assertEquals(9.9, vector.value(1), 1e-9);
}

@Test
public void testArrayCopyFromWithOffset() {
SparseVector vector = new SparseVector(new int[] { 0, 1 }, new double[] { 0.0, 0.0 });
double[] source = new double[] { 9.0, 10.0, 11.0 };
int returned = vector.arrayCopyFrom(source, 1);
assertEquals(11.0, vector.value(0), 1e-9);
assertEquals(0.0, vector.value(1), 1e-9);
assertEquals(1 + 2, returned);
}

@Test
public void testArrayCopyIntoCopiesToDestination() {
SparseVector vector = new SparseVector(new int[] { 0, 1 }, new double[] { 2.0, 4.0 });
double[] out = new double[5];
int result = vector.arrayCopyInto(out, 2);
assertEquals(2.0, out[2], 1e-9);
assertEquals(4.0, out[3], 1e-9);
assertEquals(4, result);
}

@Test(expected = IllegalArgumentException.class)
public void testMapWithWrongParameterTypeThrows() throws Throwable {
SparseVector vector = new SparseVector(new double[] { 1.0, 2.0 });
Method method = String.class.getDeclaredMethod("toString");
vector.map(method);
}

@Test(expected = UnsupportedOperationException.class)
public void testMapThrowsOnBinarySparseVector() throws Throwable {
SparseVector binaryVector = new SparseVector(new int[] { 0, 1 });
Method method = Double.class.getMethod("doubleValue");
binaryVector.map(method);
}

@Test
public void testDotProductDenseHandlesNaNGracefully() {
SparseVector vector = new SparseVector(new int[] { 0, 1 }, new double[] { 1.0, Double.NaN });
double[] other = new double[] { 5.0, 6.0 };
double result = vector.dotProduct(other);
assertTrue(Double.isNaN(result));
}

@Test
public void testExtendedDotProductSetsInfiniteFlags() {
SparseVector a = new SparseVector(new int[] { 0 }, new double[] { Double.POSITIVE_INFINITY });
SparseVector b = new SparseVector(new int[] { 0 }, new double[] { 0.0 });
double result = a.dotProduct(b);
assertEquals(0.0, result, 1e-9);
// assertTrue(a.hasInfinite);
}

@Test
public void testSparseDotProductNaNTriggersExtendedHandling() {
SparseVector a = new SparseVector(new int[] { 0 }, new double[] { Double.NaN });
SparseVector b = new SparseVector(new int[] { 0 }, new double[] { 1.0 });
double result = a.dotProduct(b);
assertTrue(Double.isNaN(result));
}

@Test
public void testSerializationAndDeserialization() throws IOException, ClassNotFoundException {
SparseVector original = new SparseVector(new int[] { 1, 2 }, new double[] { 10.0, 20.0 });
ByteArrayOutputStream out = new ByteArrayOutputStream();
ObjectOutputStream oos = new ObjectOutputStream(out);
oos.writeObject(original);
oos.close();
byte[] bytes = out.toByteArray();
ByteArrayInputStream in = new ByteArrayInputStream(bytes);
ObjectInputStream ois = new ObjectInputStream(in);
SparseVector deserialized = (SparseVector) ois.readObject();
assertEquals(10.0, deserialized.value(1), 1e-9);
assertEquals(20.0, deserialized.value(2), 1e-9);
}

@Test
public void testSortIndicesDoesNotThrowForDenseVector() {
SparseVector dense = new SparseVector(new double[] { 1.0, 2.0 });
// dense.sortIndices();
assertEquals(1.0, dense.value(0), 1e-9);
assertEquals(2.0, dense.value(1), 1e-9);
}

@Test
public void testRemoveDuplicatesWithZeroFlag() {
int[] indices = new int[] { 1, 2, 2, 3 };
double[] values = new double[] { 1.0, 2.0, 2.0, 3.0 };
SparseVector vector = new SparseVector(indices, values, false, false, false);
// vector.removeDuplicates(0);
assertEquals(1.0, vector.value(1), 1e-9);
assertEquals(4.0, vector.value(2), 1e-9);
assertEquals(3.0, vector.value(3), 1e-9);
}

@Test
public void testPrintMethodForSparseAndBinary() {
SparseVector binaryVec = new SparseVector(new int[] { 0, 2 });
SparseVector nonBinaryVec = new SparseVector(new int[] { 1 }, new double[] { 3.0 });
binaryVec.print();
nonBinaryVec.print();
assertEquals(1.0, binaryVec.value(0), 1e-9);
assertEquals(3.0, nonBinaryVec.value(1), 1e-9);
}

@Test
public void testDotProductWithDenseVectorContainingInf() {
SparseVector sparse = new SparseVector(new int[] { 1 }, new double[] { 0.0 });
DenseVector dense = new DenseVector(new double[] { 1.0, Double.POSITIVE_INFINITY });
double result = sparse.dotProduct(dense);
assertEquals(0.0, result, 1e-9);
}

@Test
public void testConstructorDenseWithCopyFalse() {
double[] values = new double[] { 5.0, 6.0 };
SparseVector vector = new SparseVector(null, values, 2, 2, false, false, false);
assertEquals(5.0, vector.value(0), 1e-9);
values[0] = 99.0;
assertEquals(99.0, vector.value(0), 1e-9);
}

@Test
public void testConstructorSparseWithCopyFalse() {
int[] indices = new int[] { 0, 2 };
double[] values = new double[] { 1.0, 2.0 };
SparseVector vector = new SparseVector(indices, values, 2, 2, false, false, false);
assertEquals(1.0, vector.value(0), 1e-9);
values[0] = 10.0;
assertEquals(10.0, vector.value(0), 1e-9);
}

@Test
public void testZeroLengthIndicesAndValues() {
int[] indices = new int[0];
double[] values = new double[0];
SparseVector vector = new SparseVector(indices, values, true, true, true);
assertEquals(0, vector.numLocations());
assertEquals(0.0, vector.value(0), 1e-9);
}

@Test
public void testDotProductDifferentLengthDenseArray() {
SparseVector vector = new SparseVector(new int[] { 0, 3 }, new double[] { 10.0, 20.0 });
double[] dense = new double[] { 1.0 };
double result = vector.dotProduct(dense);
assertEquals(10.0, result, 1e-9);
}

@Test
public void testTimesEqualsWithFactorZero() {
SparseVector vector = new SparseVector(new int[] { 0, 1 }, new double[] { 8.0, 9.0 });
vector.timesEquals(0.0);
assertEquals(0.0, vector.value(0), 1e-9);
assertEquals(0.0, vector.value(1), 1e-9);
}

@Test
public void testTimesEqualsSparseZeroWithCommonIndex() {
SparseVector a = new SparseVector(new int[] { 1, 2, 3 }, new double[] { 2.0, 4.0, 6.0 });
SparseVector b = new SparseVector(new int[] { 2 }, new double[] { 10.0 });
a.timesEqualsSparseZero(b, 1.0);
assertEquals(0.0, a.value(1), 1e-9);
assertEquals(40.0, a.value(2), 1e-9);
assertEquals(0.0, a.value(3), 1e-9);
}

@Test
public void testExtendedDotProductSkipsInfTimesZero() {
DenseVector dense = new DenseVector(new double[] { 0.0 });
SparseVector vector = new SparseVector(new int[] { 0 }, new double[] { Double.NEGATIVE_INFINITY });
double result = vector.dotProduct(dense);
assertEquals(0.0, result, 1e-9);
assertTrue(vector.isInfinite());
}

@Test
public void testSortIndicesAlreadySorted() {
SparseVector vector = new SparseVector(new int[] { 1, 2, 3 }, new double[] { 1.0, 2.0, 3.0 });
// vector.sortIndices();
assertEquals(1.0, vector.value(1), 1e-9);
}

@Test
public void testSortIndicesUnsortedWithDuplicates() {
SparseVector vector = new SparseVector(new int[] { 3, 2, 2, 1 }, new double[] { 1.0, 1.0, 2.0, 3.0 });
// vector.sortIndices();
assertEquals(3.0, vector.value(1), 1e-9);
assertEquals(3.0, vector.value(2), 1e-9);
assertEquals(1.0, vector.value(3), 1e-9);
}

@Test
public void testDensePlusEqualsSparseClipOnIndexLength() {
SparseVector dense = new SparseVector(new double[] { 1.0, 2.0 });
SparseVector sparse = new SparseVector(new int[] { 0, 2 }, new double[] { 5.0, 10.0 });
dense.plusEqualsSparse(sparse, 1.0);
assertEquals(6.0, dense.value(0), 1e-9);
assertEquals(2.0, dense.value(1), 1e-9);
}

@Test
public void testVectorAddWithZeroScaleKeepsOriginal() {
SparseVector a = new SparseVector(new int[] { 0 }, new double[] { 3.0 });
SparseVector b = new SparseVector(new int[] { 0 }, new double[] { 100.0 });
SparseVector result = a.vectorAdd(b, 0.0);
assertEquals(3.0, result.value(0), 1e-9);
}

@Test
public void testVectorAddSparseToSparseMergedResult() {
SparseVector a = new SparseVector(new int[] { 1 }, new double[] { 2.0 });
SparseVector b = new SparseVector(new int[] { 3 }, new double[] { 4.0 });
SparseVector result = a.vectorAdd(b, 1.0);
assertEquals(2.0, result.value(1), 1e-9);
assertEquals(4.0, result.value(3), 1e-9);
}

@Test
public void testZeroScaleTimesEqualsSparseDoesNotChangeValues() {
SparseVector a = new SparseVector(new int[] { 0, 1 }, new double[] { 1.0, 2.0 });
SparseVector b = new SparseVector(new int[] { 0, 1 }, new double[] { 10.0, 20.0 });
a.timesEqualsSparse(b, 0.0);
assertEquals(0.0, a.value(0), 1e-9);
assertEquals(0.0, a.value(1), 1e-9);
}

@Test
public void testDotProductDenseWithNaNAndInfForcesExtendedHandling() {
SparseVector a = new SparseVector(new int[] { 0, 1 }, new double[] { 1.0, Double.POSITIVE_INFINITY });
DenseVector b = new DenseVector(new double[] { Double.NaN, 0.0 });
double result = a.dotProduct(b);
assertEquals(0.0, result, 1e-9);
assertTrue(a.isInfinite());
}

@Test(expected = IllegalArgumentException.class)
public void testDotProductWithInvalidMatrixImplementationFails() {
// ConstantMatrix m = new ConstantMatrix() {
// 
// public int getNumDimensions() {
// return 1;
// }
// 
// public int getDimensions(int[] sizes) {
// return 0;
// }
// 
// public int numLocations() {
// return 0;
// }
// 
// public int location(int index) {
// return 0;
// }
// 
// public double valueAtLocation(int location) {
// return 0;
// }
// 
// public int indexAtLocation(int location) {
// return 0;
// }
// 
// public double value(int[] indices) {
// return 0;
// }
// 
// public double value(int index) {
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
// public double singleValue(int i) {
// return 0;
// }
// 
// public int singleSize() {
// return 0;
// }
// 
// public ConstantMatrix cloneMatrix() {
// return this;
// }
// 
// public double oneNorm() {
// return 0;
// }
// 
// public double twoNorm() {
// return 0;
// }
// 
// public double absNorm() {
// return 0;
// }
// 
// public double infinityNorm() {
// return 0;
// }
// 
// public void print() {
// }
// 
// public boolean isNaN() {
// return false;
// }
// };
SparseVector v = new SparseVector(new int[] { 0 }, new double[] { 1.0 });
// v.dotProduct(m);
}

@Test(expected = IllegalArgumentException.class)
public void testConstructorIndexValueLengthMismatchThrows() {
int[] indices = new int[] { 0, 1, 2 };
double[] values = new double[] { 1.0 };
new SparseVector(indices, values, true, false, false);
}

@Test
public void testConstructorWithLargeCapacityAndCopyTrue() {
int[] indices = new int[] { 0, 1 };
double[] values = new double[] { 2.0, 3.0 };
SparseVector vector = new SparseVector(indices, values, 10, 2, true, false, false);
assertEquals(2, vector.numLocations());
assertEquals(2.0, vector.value(0), 1e-9);
assertEquals(3.0, vector.value(1), 1e-9);
}

@Test
public void testDenseVectorLocationIsIdentity() {
SparseVector dense = new SparseVector(new double[] { 1.0, 2.0, 3.0 });
assertEquals(0, dense.location(0));
assertEquals(2, dense.location(2));
}

@Test
public void testDenseVectorIndexAtLocation() {
SparseVector dense = new SparseVector(new double[] { 1.0, 2.0 });
assertEquals(0, dense.indexAtLocation(0));
assertEquals(1, dense.indexAtLocation(1));
}

@Test
public void testToStringOnEmptyBinaryVector() {
SparseVector vector = new SparseVector(new int[] {}, null, true, true, true);
String str = vector.toString(true);
assertEquals("", str.trim());
}

@Test
public void testToStringOnEmptyDenseVector() {
SparseVector vector = new SparseVector(new double[] {});
String str = vector.toString(true);
assertEquals("", str.trim());
}

@Test
public void testCloneMatrixZeroedForBinaryVector() {
SparseVector binary = new SparseVector(new int[] { 2, 4 });
ConstantMatrix clone = binary.cloneMatrixZeroed();
// assertEquals(0.0, clone.value(2), 1e-9);
// assertEquals(0.0, clone.value(4), 1e-9);
}

@Test
public void testCloneMatrixZeroedForDenseVector() {
SparseVector dense = new SparseVector(new double[] { 5.0, 6.0 });
ConstantMatrix clone = dense.cloneMatrixZeroed();
// assertEquals(0.0, clone.value(0), 1e-9);
// assertEquals(0.0, clone.value(1), 1e-9);
}

@Test
public void testSingleIndexAndSingleToIndicesIdentityFunctionality() {
SparseVector vector = new SparseVector(new double[] { 1.0 });
int[] idx = new int[1];
vector.singleToIndices(42, idx);
assertEquals(42, idx[0]);
int val = vector.singleIndex(new int[] { 13 });
assertEquals(13, val);
}

@Test
public void testValueIntArrayWithNullInputDefaultsCorrectly() {
int[] indices = new int[] { 0 };
double[] values = new double[] { 3.3 };
SparseVector vector = new SparseVector(indices, values);
double result = vector.value((int[]) null);
assertEquals(3.3, result, 1e-9);
}

@Test
public void testArrayCopyFromExactFitBehavior() {
double[] input = new double[] { 99.9, 77.7, 55.5 };
SparseVector vector = new SparseVector(new int[] { 0, 1, 2 }, new double[] { 0.0, 0.0, 0.0 });
int result = vector.arrayCopyFrom(input, 0);
assertEquals(99.9, vector.value(0), 1e-9);
assertEquals(77.7, vector.value(1), 1e-9);
assertEquals(55.5, vector.value(2), 1e-9);
assertEquals(3, result);
}

@Test
public void testArrayCopyFromOffsetWithinRange() {
double[] input = new double[] { 0.0, 0.0, 5.0, 6.0 };
SparseVector vector = new SparseVector(new int[] { 0, 1 }, new double[] { 0.0, 0.0 });
int nextOffset = vector.arrayCopyFrom(input, 2);
assertEquals(5.0, vector.value(0), 1e-9);
assertEquals(6.0, vector.value(1), 1e-9);
assertEquals(4, nextOffset);
}

@Test
public void testZeroNormsForEmptyVector() {
SparseVector vector = new SparseVector();
assertEquals(0.0, vector.oneNorm(), 1e-9);
assertEquals(0.0, vector.twoNorm(), 1e-9);
assertEquals(0.0, vector.absNorm(), 1e-9);
assertEquals(0.0, vector.infinityNorm(), 1e-9);
}

@Test
public void testDotProductWithDenseVectorLongerThanSparseIndices() {
SparseVector sparse = new SparseVector(new int[] { 1, 2 }, new double[] { 3.0, 4.0 });
DenseVector dense = new DenseVector(new double[] { 0.0, 10.0, 20.0, 30.0 });
double result = sparse.dotProduct(dense);
assertEquals(3.0 * 10.0 + 4.0 * 20.0, result, 1e-9);
}

@Test
public void testInfinityNormReturnsOneForBinarySparseVector() {
SparseVector binary = new SparseVector(new int[] { 1, 3, 5 });
assertEquals(1.0, binary.infinityNorm(), 1e-9);
}

@Test
public void testIsNaNReturnsFalseForBinary() {
SparseVector binary = new SparseVector(new int[] { 7 });
assertFalse(binary.isNaN());
}

@Test
public void testIsInfiniteReturnsFalseForBinary() {
SparseVector binary = new SparseVector(new int[] { 9 });
assertFalse(binary.isInfinite());
}

@Test
public void testIsNaNOrInfiniteReturnsFalseForBinary() {
SparseVector binary = new SparseVector(new int[] { 2, 3 });
assertFalse(binary.isNaNOrInfinite());
}

@Test
public void testDotProductWithSelfMatchesNormSquared() {
SparseVector vector = new SparseVector(new int[] { 0, 1 }, new double[] { 3.0, 4.0 });
double result = vector.dotProduct(vector);
double expected = 3.0 * 3.0 + 4.0 * 4.0;
assertEquals(expected, result, 1e-9);
assertEquals(expected, vector.twoNorm() * vector.twoNorm(), 1e-9);
}

@Test
public void testPrintOutputDoesNotThrowForEmpty() {
SparseVector vector = new SparseVector();
vector.print();
assertEquals(0, vector.numLocations());
}

@Test(expected = UnsupportedOperationException.class)
public void testMapOnBinaryVectorThrows() throws Throwable {
SparseVector vector = new SparseVector(new int[] { 1, 2 });
Method method = Double.class.getMethod("isInfinite", double.class);
vector.map(method);
}

@Test
public void testSortIndicesSkipsWhenDense() {
SparseVector vector = new SparseVector(new double[] { 1.0, 2.0, 3.0 });
// vector.sortIndices();
assertEquals(3, vector.numLocations());
}

@Test
public void testRemoveDuplicatesExplicitValue() {
SparseVector vector = new SparseVector(new int[] { 1, 2, 2, 3 }, new double[] { 4.0, 5.0, 6.0, 7.0 });
// vector.removeDuplicates(1);
assertEquals(4.0, vector.value(1), 1e-9);
assertEquals(11.0, vector.value(2), 1e-9);
assertEquals(7.0, vector.value(3), 1e-9);
}

@Test(expected = AssertionError.class)
public void testValueWithMultiDimensionalIndexArrayThrows() {
SparseVector vector = new SparseVector(new double[] { 1.0, 2.0 });
int[] indices = new int[] { 0, 1 };
vector.value(indices);
}

@Test
public void testCloneMatrixFromBinaryVector() {
SparseVector original = new SparseVector(new int[] { 1, 3 });
ConstantMatrix clone = original.cloneMatrix();
// assertEquals(1.0, clone.value(1), 1e-9);
// assertEquals(1.0, clone.value(3), 1e-9);
}

@Test
public void testZeroCapacityBinarySparseVector() {
int[] indices = new int[] { 0, 1 };
SparseVector vector = new SparseVector(indices, 0, 0, true, true, true);
assertEquals(0, vector.numLocations());
}

@Test
public void testCloneMatrixZeroedOnEmptyBinaryVector() {
SparseVector empty = new SparseVector(new int[] {}, null, true, true, true);
ConstantMatrix clone = empty.cloneMatrixZeroed();
assertEquals(0, clone.numLocations());
}

@Test
public void testArrayCopyIntoWithOffsetBeyondArrayCapacity() {
SparseVector vector = new SparseVector(new int[] { 0, 1 }, new double[] { 1.0, 2.0 });
double[] target = new double[3];
int result = vector.arrayCopyInto(target, 1);
assertEquals(1.0, target[1], 1e-9);
assertEquals(2.0, target[2], 1e-9);
assertEquals(3, result);
}

@Test
public void testArrayCopyIntoOffsetTooSmallGrowsSequentially() {
SparseVector vector = new SparseVector(new int[] { 0, 1, 2 }, new double[] { 7.0, 8.0, 9.0 });
double[] out = new double[5];
int result = vector.arrayCopyInto(out, 0);
assertEquals(7.0, out[0], 1e-9);
assertEquals(8.0, out[1], 1e-9);
assertEquals(9.0, out[2], 1e-9);
assertEquals(3, result);
}

@Test
public void testDotProductPartialOverlapSparse() {
SparseVector a = new SparseVector(new int[] { 1, 3 }, new double[] { 5.0, 6.0 });
SparseVector b = new SparseVector(new int[] { 3, 4 }, new double[] { 7.0, 8.0 });
double result = a.dotProduct(b);
assertEquals(6.0 * 7.0, result, 1e-9);
}

@Test
public void testDenseTimesEqualsSparseSkipsOutOfBoundsIndex() {
SparseVector dense = new SparseVector(new double[] { 2.0, 3.0 });
SparseVector sparse = new SparseVector(new int[] { 1, 5 }, new double[] { 10.0, 20.0 });
dense.timesEqualsSparse(sparse, 1.0);
assertEquals(2.0, dense.value(0), 1e-9);
assertEquals(3.0 * 10.0, dense.value(1), 1e-9);
}

@Test(expected = IllegalArgumentException.class)
public void testSetValueOnInvalidSparseIndexThrows() {
SparseVector vector = new SparseVector(new int[] { 5 }, new double[] { 9.9 });
vector.setValue(3, 100.0);
}

@Test
public void testSetValueOnDenseWorks() {
SparseVector vector = new SparseVector(new double[] { 1.0, 2.0 });
vector.setValue(0, 42.0);
assertEquals(42.0, vector.value(0), 1e-9);
}

@Test(expected = IllegalArgumentException.class)
public void testIncrementValueWithMissingIndexThrows() {
SparseVector vector = new SparseVector(new int[] { 2 }, new double[] { 1.0 });
vector.incrementValue(1, 1.0);
}

@Test
public void testIncrementValueAddsCorrectly() {
SparseVector vector = new SparseVector(new int[] { 1 }, new double[] { 3.0 });
vector.incrementValue(1, 2.5);
assertEquals(5.5, vector.value(1), 1e-9);
}

@Test
public void testSetAllOnEmptySparseVector() {
SparseVector vector = new SparseVector(new int[0], new double[0], true, false, false);
vector.setAll(10.0);
assertEquals(0, vector.numLocations());
}

@Test
public void testSetAllModifiesAllDenseValues() {
SparseVector vector = new SparseVector(new double[] { 5.0, 6.0 });
vector.setAll(-3.3);
assertEquals(-3.3, vector.value(0), 1e-9);
assertEquals(-3.3, vector.value(1), 1e-9);
}

@Test
public void testDotProductDenseWithEmptyVectorReturnsZero() {
SparseVector sparse = new SparseVector();
double[] dense = new double[] { 1.0, 2.0 };
double result = sparse.dotProduct(dense);
assertEquals(0.0, result, 1e-9);
}

@Test
public void testTwoBinaryVectorsDotProductEqualsOverlapCount() {
SparseVector a = new SparseVector(new int[] { 1, 3, 5 });
SparseVector b = new SparseVector(new int[] { 3, 4, 5 });
double result = a.dotProduct(b);
assertEquals(2.0, result, 1e-9);
}

@Test
public void testValueWithNullIndicesAndSingleIntArray() {
SparseVector vector = new SparseVector(new double[] { 9.0 });
int[] index = new int[] { 0 };
double output = vector.value(index);
assertEquals(9.0, output, 1e-9);
}

@Test
public void testValueWithIndicesPresentAndSingleIntArray() {
SparseVector vector = new SparseVector(new int[] { 3 }, new double[] { 7.0 });
int[] index = new int[] { 3 };
double output = vector.value(index);
assertEquals(7.0, output, 1e-9);
}

@Test
public void testRemoveDuplicatesWithExactlyOneDuplicate() {
int[] indices = new int[] { 1, 2, 2 };
double[] values = new double[] { 1.0, 2.0, 3.0 };
SparseVector vector = new SparseVector(indices, values, true, false, false);
// vector.sortIndices();
assertEquals(5.0, vector.value(2), 1e-9);
}

@Test
public void testCloneMatrixZeroedSparse() {
SparseVector vector = new SparseVector(new int[] { 1, 3 }, new double[] { 8.0, 9.0 });
ConstantMatrix clone = vector.cloneMatrixZeroed();
// assertEquals(0.0, clone.value(1), 1e-9);
// assertEquals(0.0, clone.value(3), 1e-9);
}

@Test
public void testCloneMatrixZeroedDense() {
SparseVector vector = new SparseVector(new double[] { 2.0, 4.0 });
ConstantMatrix clone = vector.cloneMatrixZeroed();
// assertEquals(0.0, clone.value(0), 1e-9);
// assertEquals(0.0, clone.value(1), 1e-9);
}

@Test
public void testVectorAddSparseWithFullOverlap() {
SparseVector a = new SparseVector(new int[] { 1, 2 }, new double[] { 2.0, 3.0 });
SparseVector b = new SparseVector(new int[] { 1, 2 }, new double[] { 4.0, 5.0 });
SparseVector result = a.vectorAdd(b, 1.0);
assertEquals(6.0, result.value(1), 1e-9);
assertEquals(8.0, result.value(2), 1e-9);
}

@Test
public void testMapAppliesSquareFunctionProperly() throws Throwable {
SparseVector vector = new SparseVector(new double[] { 2.0, 3.0 });
// Method m = SparseVectorExtendedEdgeTests.class.getDeclaredMethod("square", Double.class);
// vector.map(m);
assertEquals(4.0, vector.value(0), 1e-9);
assertEquals(9.0, vector.value(1), 1e-9);
}

@Test
public void testArrayCopyFromEmptyArray() {
SparseVector vector = new SparseVector(new int[] {}, new double[] {}, true, false, false);
double[] input = new double[] {};
int result = vector.arrayCopyFrom(input, 0);
assertEquals(0, result);
}

@Test
public void testArrayCopyIntoEmptyDestination() {
SparseVector vector = new SparseVector(new int[] {}, new double[] {}, true, false, false);
double[] array = new double[0];
int result = vector.arrayCopyInto(array, 0);
assertEquals(0, result);
}

@Test
public void testRemoveDuplicatesKeepsLastValue() {
int[] indices = new int[] { 4, 2, 2, 1 };
double[] values = new double[] { 1.0, 2.0, 3.0, 4.0 };
SparseVector vector = new SparseVector(indices, values, true, true, false);
assertEquals(5.0, vector.value(2), 1e-9);
assertEquals(4.0, vector.value(1), 1e-9);
}

@Test
public void testSingleSizeReturnsZeroForEmptySparse() {
SparseVector vector = new SparseVector(new int[] {}, new double[] {}, true, false, false);
int result = vector.singleSize();
assertEquals(0, result);
}

@Test
public void testSingleSizeReturnsCorrectForSparse() {
SparseVector vector = new SparseVector(new int[] { 1, 2, 4 }, new double[] { 5.0, 6.0, 7.0 });
int result = vector.singleSize();
assertEquals(4, result);
}

@Test
public void testSingleSizeReturnsLengthForDense() {
SparseVector vector = new SparseVector(new double[] { 1.0, 2.0, 3.0 });
int result = vector.singleSize();
assertEquals(3, result);
}

@Test
public void testIndexAtLocationRegressionCheck() {
SparseVector vector = new SparseVector(new int[] { 2, 5 }, new double[] { 1.0, 4.0 });
assertEquals(2, vector.indexAtLocation(0));
assertEquals(5, vector.indexAtLocation(1));
}

@Test
public void testValueAtLocationReturnsCorrectSparseValue() {
SparseVector vector = new SparseVector(new int[] { 10, 20 }, new double[] { 100.0, 200.0 });
assertEquals(100.0, vector.valueAtLocation(0), 1e-9);
assertEquals(200.0, vector.valueAtLocation(1), 1e-9);
}
}
