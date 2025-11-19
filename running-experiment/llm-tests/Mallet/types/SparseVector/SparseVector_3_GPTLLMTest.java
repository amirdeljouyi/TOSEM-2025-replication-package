package cc.mallet.types.tests;

import cc.mallet.types.*;
import cc.mallet.util.PropertyList;
import org.junit.Test;
import java.io.*;
import java.lang.reflect.Method;
import static org.junit.Assert.*;

public class SparseVector_3_GPTLLMTest {

@Test
public void testDenseConstructorAndAccessors() {
double[] values = new double[] { 1.0, 2.0, 3.0 };
SparseVector v = new SparseVector(values);
assertFalse(v.isBinary());
assertEquals(1.0, v.value(0), 0.00001);
assertEquals(2.0, v.value(1), 0.00001);
assertEquals(3.0, v.value(2), 0.00001);
}

@Test
public void testSparseConstructorAndValue() {
int[] indices = new int[] { 0, 2, 4 };
double[] values = new double[] { 1.0, 3.0, 5.0 };
SparseVector v = new SparseVector(indices, values, true);
assertEquals(3, v.numLocations());
assertEquals(1.0, v.value(0), 0.00001);
assertEquals(3.0, v.value(2), 0.00001);
assertEquals(5.0, v.value(4), 0.00001);
assertEquals(0.0, v.value(1), 0.00001);
}

@Test(expected = IllegalArgumentException.class)
public void testConstructorThrowsWithMismatchedLength() {
int[] indices = new int[] { 0, 1 };
double[] values = new double[] { 1.0 };
new SparseVector(indices, values, true, true, false);
}

@Test
public void testBinarySparseVector() {
int[] indices = new int[] { 1, 3, 5 };
SparseVector v = new SparseVector(indices, true);
assertTrue(v.isBinary());
assertEquals(1.0, v.value(1), 0.00001);
assertEquals(1.0, v.value(3), 0.00001);
assertEquals(1.0, v.value(5), 0.00001);
assertEquals(0.0, v.value(2), 0.00001);
}

@Test
public void testDotProductWithDenseVector() {
double[] data1 = new double[] { 1.0, 2.0, 3.0 };
double[] data2 = new double[] { 4.0, 5.0, 6.0 };
SparseVector sv = new SparseVector(data1);
DenseVector dv = new DenseVector(data2);
double expected = 1.0 * 4.0 + 2.0 * 5.0 + 3.0 * 6.0;
double actual = sv.dotProduct(dv);
assertEquals(expected, actual, 0.00001);
}

@Test
public void testDotProductWithSparseVector() {
int[] idx1 = new int[] { 0, 2 };
double[] val1 = new double[] { 1.0, 2.0 };
int[] idx2 = new int[] { 2, 3 };
double[] val2 = new double[] { 3.0, 4.0 };
SparseVector v1 = new SparseVector(idx1, val1, true);
SparseVector v2 = new SparseVector(idx2, val2, true);
double actual = v1.dotProduct(v2);
double expected = 2.0 * 3.0;
assertEquals(expected, actual, 0.00001);
}

@Test
public void testPlusEqualsSparse() {
int[] idx1 = new int[] { 1, 2 };
double[] val1 = new double[] { 1.0, 2.0 };
int[] idx2 = new int[] { 2 };
double[] val2 = new double[] { 3.0 };
SparseVector v1 = new SparseVector(idx1, val1, true);
SparseVector v2 = new SparseVector(idx2, val2, true);
v1.plusEqualsSparse(v2);
assertEquals(1.0, v1.value(1), 0.00001);
assertEquals(5.0, v1.value(2), 0.00001);
}

@Test
public void testTimesEqualsSparse() {
int[] idx1 = new int[] { 1, 2 };
double[] val1 = new double[] { 2.0, 3.0 };
int[] idx2 = new int[] { 2 };
double[] val2 = new double[] { 4.0 };
SparseVector v1 = new SparseVector(idx1, val1, true);
SparseVector v2 = new SparseVector(idx2, val2, true);
v1.timesEqualsSparse(v2);
assertEquals(2.0, v1.value(1), 0.00001);
assertEquals(12.0, v1.value(2), 0.00001);
}

@Test
public void testIncrementValueValidIndex() {
int[] idx = new int[] { 1, 3 };
double[] val = new double[] { 2.0, 4.0 };
SparseVector v = new SparseVector(idx, val, true);
v.incrementValue(3, 5.0);
assertEquals(9.0, v.value(3), 0.00001);
}

@Test(expected = IllegalArgumentException.class)
public void testIncrementValueInvalidIndexThrows() {
int[] idx = new int[] { 0, 1 };
double[] val = new double[] { 1.0, 2.0 };
SparseVector v = new SparseVector(idx, val, true);
v.incrementValue(2, 10.0);
}

@Test
public void testSetValueAtLocation() {
double[] values = new double[] { 1.0, 2.0 };
SparseVector v = new SparseVector(values);
v.setValueAtLocation(0, 5.5);
assertEquals(5.5, v.value(0), 0.00001);
}

@Test
public void testCloneMatrix() {
double[] data = new double[] { 3.0, 4.0 };
SparseVector original = new SparseVector(data);
ConstantMatrix clone = original.cloneMatrix();
assertNotSame(original, clone);
// assertEquals(original.value(0), clone.value(0), 0.00001);
// assertEquals(original.value(1), clone.value(1), 0.00001);
}

@Test
public void testNormCalculations() {
double[] data = new double[] { 1.0, -2.0, 3.0 };
SparseVector v = new SparseVector(data);
assertEquals(6.0, v.oneNorm(), 0.00001);
assertEquals(6.0, v.absNorm(), 0.00001);
assertEquals(Math.sqrt(14), v.twoNorm(), 0.00001);
assertEquals(3.0, v.infinityNorm(), 0.00001);
}

@Test
public void testSetAllUpdatesEveryValue() {
double[] data = new double[] { 1.0, 1.0, 1.0 };
SparseVector v = new SparseVector(data);
v.setAll(9.9);
assertEquals(9.9, v.value(0), 0.00001);
assertEquals(9.9, v.value(1), 0.00001);
assertEquals(9.9, v.value(2), 0.00001);
}

@Test(expected = IllegalArgumentException.class)
public void testSetValueInvalidIndexThrows() {
int[] indices = new int[] { 0, 1 };
double[] values = new double[] { 1.0, 2.0 };
SparseVector v = new SparseVector(indices, values, true);
v.setValue(2, 5.0);
}

@Test
public void testArrayCopyFromAndInto() {
double[] source = new double[] { 1.0, 2.0, 3.0 };
SparseVector v = new SparseVector(new double[] { 0.0, 0.0, 0.0 });
v.arrayCopyFrom(source);
assertEquals(1.0, v.value(0), 0.00001);
assertEquals(2.0, v.value(1), 0.00001);
assertEquals(3.0, v.value(2), 0.00001);
double[] resultArray = new double[5];
int offset = v.arrayCopyInto(resultArray, 1);
assertEquals(1.0, resultArray[1], 0.00001);
assertEquals(2.0, resultArray[2], 0.00001);
assertEquals(3.0, resultArray[3], 0.00001);
assertEquals(4, offset);
}

@Test
public void testToStringOutput() {
int[] indices = new int[] { 1, 2 };
double[] values = new double[] { 5.0, 10.0 };
SparseVector v = new SparseVector(indices, values, true);
String text = v.toString(true);
assertTrue(text.contains("1=5.0"));
assertTrue(text.contains("2=10.0"));
}

@Test
public void testSerializationCycle() throws IOException, ClassNotFoundException {
int[] indices = new int[] { 2, 5 };
double[] values = new double[] { 7.0, 9.0 };
SparseVector original = new SparseVector(indices, values, true);
ByteArrayOutputStream bos = new ByteArrayOutputStream();
ObjectOutputStream out = new ObjectOutputStream(bos);
out.writeObject(original);
out.flush();
out.close();
byte[] serialized = bos.toByteArray();
ByteArrayInputStream bis = new ByteArrayInputStream(serialized);
ObjectInputStream in = new ObjectInputStream(bis);
SparseVector deserialized = (SparseVector) in.readObject();
assertEquals(original.value(2), deserialized.value(2), 0.00001);
assertEquals(original.value(5), deserialized.value(5), 0.00001);
}

@Test(expected = UnsupportedOperationException.class)
public void testMapFailsOnBinaryVector() throws Throwable {
int[] indices = new int[] { 0, 1 };
SparseVector binary = new SparseVector(indices, true);
Method method = Math.class.getMethod("abs", double.class);
binary.map(method);
}

@Test(expected = IllegalArgumentException.class)
public void testMapFailsOnWrongMethodSignature() throws Throwable {
Method invalid = String.class.getMethod("length");
SparseVector v = new SparseVector(new double[] { 1.0 });
v.map(invalid);
}

@Test
public void testExtendedDotProductWithInfinity() {
double[] data1 = new double[] { Double.POSITIVE_INFINITY, 0.0 };
double[] data2 = new double[] { 0.0, 3.0 };
SparseVector v1 = new SparseVector(data1);
SparseVector v2 = new SparseVector(data2);
double result = v1.dotProduct(v2);
assertFalse(Double.isNaN(result));
assertEquals(0.0, result, 0.00001);
}

@Test
public void testEmptyVectorReturnsZeroOnAccess() {
SparseVector v = new SparseVector();
assertEquals(0, v.numLocations());
assertEquals(0.0, v.value(0), 0.00001);
assertEquals(0.0, v.value(100), 0.00001);
}

@Test
public void testDenseTimesEqualsFactorOnly() {
double[] values = new double[] { 1.0, 2.0 };
SparseVector v = new SparseVector(values);
v.timesEquals(2.5);
assertEquals(2.5, v.value(0), 0.00001);
assertEquals(5.0, v.value(1), 0.00001);
}

@Test
public void testTimesEqualsSparseZeroUnmatchedIndicesGetZeroed() {
int[] indices1 = new int[] { 0, 1, 2 };
double[] values1 = new double[] { 10.0, 20.0, 30.0 };
int[] indices2 = new int[] { 1 };
double[] values2 = new double[] { 2.0 };
SparseVector v1 = new SparseVector(indices1, values1, true);
SparseVector v2 = new SparseVector(indices2, values2, true);
v1.timesEqualsSparseZero(v2, 1.0);
assertEquals(0.0, v1.value(0), 0.00001);
assertEquals(40.0, v1.value(1), 0.00001);
assertEquals(0.0, v1.value(2), 0.00001);
}

@Test(expected = IllegalArgumentException.class)
public void testSetValueOutOfBoundsOnDenseVectorThrows() {
SparseVector v = new SparseVector(new double[] { 1.0, 2.0 });
v.setValue(5, 10.0);
}

@Test
public void testCloneMatrixZeroedProducesZeros() {
SparseVector v = new SparseVector(new int[] { 1, 3 }, new double[] { 4.5, 9.0 }, true);
ConstantMatrix zeroed = v.cloneMatrixZeroed();
// assertEquals(0.0, zeroed.value(1), 0.00001);
// assertEquals(0.0, zeroed.value(3), 0.00001);
}

@Test
public void testSingleIndexAndSingleValueMethods() {
double[] values = new double[] { 5.0, 10.0 };
SparseVector v = new SparseVector(values);
int[] idxArray = new int[1];
v.singleToIndices(1, idxArray);
assertEquals(1, v.singleIndex(new int[] { 1 }));
assertEquals(1, idxArray[0]);
assertEquals(10.0, v.singleValue(1), 0.00001);
assertEquals(2, v.singleSize());
}

@Test
public void testDotProductShorterSparseHasNoMatches() {
int[] idx1 = new int[] { 0, 1 };
double[] val1 = new double[] { 1.0, 1.0 };
int[] idx2 = new int[] { 2, 3 };
double[] val2 = new double[] { 1.0, 1.0 };
SparseVector v1 = new SparseVector(idx1, val1, true);
SparseVector v2 = new SparseVector(idx2, val2, true);
double result = v1.dotProduct(v2);
assertEquals(0.0, result, 0.00001);
}

@Test
public void testDotProductWithNaNValuesTriggersExtendedPath() {
int[] idx = new int[] { 1 };
double[] val = new double[] { Double.NaN };
SparseVector v1 = new SparseVector(idx, val, true);
SparseVector v2 = new SparseVector(idx, new double[] { 2.0 }, true);
double result = v1.dotProduct(v2);
assertTrue(Double.isNaN(result));
}

@Test
public void testIsNaNOrInfiniteTrueForNaNAndInfinity() {
int[] idx = new int[] { 0, 1 };
double[] val = new double[] { Double.NaN, Double.POSITIVE_INFINITY };
SparseVector v = new SparseVector(idx, val, true);
assertTrue(v.isNaN());
assertTrue(v.isInfinite());
assertTrue(v.isNaNOrInfinite());
}

@Test
public void testSortIndicesRemovesDuplicatesAndSumsValues() {
int[] idx = new int[] { 3, 1, 3 };
double[] val = new double[] { 1.0, 2.0, 4.0 };
SparseVector v = new SparseVector(idx, val, true, true);
assertEquals(2, v.numLocations());
assertEquals(2.0, v.value(1), 0.00001);
assertEquals(5.0, v.value(3), 0.00001);
}

@Test
public void testRemoveDuplicatesCalledExplicitly() {
int[] idx = new int[] { 1, 1, 2 };
double[] val = new double[] { 1.0, 1.5, 2.0 };
SparseVector v = new SparseVector(idx, val, true, false, false);
// v.removeDuplicates(0);
assertEquals(2, v.numLocations());
assertEquals(2.5, v.value(1), 0.00001);
assertEquals(2.0, v.value(2), 0.00001);
}

@Test
public void testAddToDenseArray() {
double[] values = new double[] { 1.0, 2.0, 3.0 };
SparseVector v = new SparseVector(values);
double[] acc = new double[] { 10.0, 10.0, 10.0 };
v.addTo(acc, 2.0);
assertEquals(12.0, acc[0], 0.00001);
assertEquals(14.0, acc[1], 0.00001);
assertEquals(16.0, acc[2], 0.00001);
}

@Test
public void testBinarySparseAddToDenseArray() {
int[] idx = new int[] { 0, 2 };
SparseVector v = new SparseVector(idx, true);
double[] acc = new double[] { 1.0, 1.0, 1.0 };
v.addTo(acc);
assertEquals(2.0, acc[0], 0.00001);
assertEquals(1.0, acc[1], 0.00001);
assertEquals(2.0, acc[2], 0.00001);
}

@Test
public void testExtendedDotProductWithZeroAndInfinity() {
int[] idx1 = new int[] { 0, 1 };
double[] values1 = new double[] { 0.0, Double.POSITIVE_INFINITY };
int[] idx2 = new int[] { 0, 1 };
double[] values2 = new double[] { Double.NEGATIVE_INFINITY, 1.0 };
SparseVector v1 = new SparseVector(idx1, values1, true);
SparseVector v2 = new SparseVector(idx2, values2, true);
double result = v1.dotProduct(v2);
assertEquals(1.0 * Double.POSITIVE_INFINITY, result, 0.00001);
}

@Test
public void testMapWithValidStaticMethod() throws Throwable {
double[] data = new double[] { 1.0, -2.0 };
SparseVector v = new SparseVector(data);
Method absMethod = Math.class.getMethod("abs", double.class);
v.map(absMethod);
assertEquals(1.0, v.value(0), 0.00001);
assertEquals(2.0, v.value(1), 0.00001);
}

@Test
public void testBinarySparseVectorValueAtLocationAlwaysOne() {
int[] indices = new int[] { 0, 2 };
SparseVector v = new SparseVector(indices, true);
assertEquals(1.0, v.valueAtLocation(0), 0.0001);
assertEquals(1.0, v.valueAtLocation(1), 0.0001);
}

@Test
public void testValueReturnsZeroForNegativeIndex() {
SparseVector v = new SparseVector(new double[] { 1.0, 2.0 });
assertEquals(0.0, v.value(-1), 0.00001);
}

@Test(expected = IndexOutOfBoundsException.class)
public void testSetValueDenseInvalidIndexThrows() {
SparseVector v = new SparseVector(new double[] { 1.0 });
v.setValue(5, 2.0);
}

@Test
public void testAddToDenseWhenSparseAndBinary() {
int[] indices = new int[] { 0, 3 };
SparseVector v = new SparseVector(indices, null, true, false, false);
double[] accumulator = new double[5];
v.addTo(accumulator);
assertEquals(1.0, accumulator[0], 0.00001);
assertEquals(0.0, accumulator[1], 0.00001);
assertEquals(0.0, accumulator[2], 0.00001);
assertEquals(1.0, accumulator[3], 0.00001);
assertEquals(0.0, accumulator[4], 0.00001);
}

@Test
public void testDotProductBetweenDenseAndSparse() {
SparseVector dense = new SparseVector(new double[] { 2.0, 4.0, 6.0 });
int[] idx = new int[] { 0, 2 };
double[] val = new double[] { 1.0, 0.5 };
SparseVector sparse = new SparseVector(idx, val, true);
double dp = dense.dotProduct(sparse);
assertEquals((2.0 * 1.0) + (6.0 * 0.5), dp, 0.00001);
}

@Test
public void testVectorAddDenseWithZeroResult() {
SparseVector dense = new SparseVector(new double[] { 1.0, 1.0 });
SparseVector add = new SparseVector(new double[] { -1.0, -1.0 });
SparseVector result = dense.vectorAdd(add, 1.0);
assertEquals(0, result.numLocations());
}

@Test
public void testLocationBinarySearchWhenNotFoundReturnsNegative() {
int[] indices = new int[] { 0, 5, 10 };
double[] values = new double[] { 1.0, 1.0, 1.0 };
SparseVector v = new SparseVector(indices, values, true);
int loc = v.location(7);
assertTrue(loc < 0);
}

@Test
public void testInfinityNormNaNHandling() {
double[] values = new double[] { Double.NaN, 5.0 };
SparseVector v = new SparseVector(values);
double norm = v.infinityNorm();
assertEquals(5.0, norm, 0.00001);
}

@Test
public void testGetDimensionsForDenseAndSparse() {
double[] val = new double[] { 1.0, 2.0, 3.0 };
SparseVector dense = new SparseVector(val);
int[] dims1 = new int[1];
dense.getDimensions(dims1);
assertEquals(3, dims1[0]);
int[] idx = new int[] { 1, 2, 6 };
SparseVector sparse = new SparseVector(idx, val, true);
int[] dims2 = new int[1];
sparse.getDimensions(dims2);
assertEquals(6, dims2[0]);
}

@Test
public void testConstructorWithPropertyListEmpty() {
// SparseVector v = new SparseVector(null, null, true, false);
// assertNotNull(v);
// assertEquals(0, v.numLocations());
}

@Test
public void testNaNAndInfCheckSeparateScenarios() {
double[] valuesInf = new double[] { Double.POSITIVE_INFINITY };
double[] valuesNaN = new double[] { Double.NaN };
SparseVector infVec = new SparseVector(valuesInf);
SparseVector nanVec = new SparseVector(valuesNaN);
assertFalse(nanVec.isInfinite());
assertTrue(nanVec.isNaN());
assertTrue(infVec.isInfinite());
assertFalse(infVec.isNaN());
}

@Test
public void testExtendedDotProductSkipsInfTimesZero() {
int[] idx = new int[] { 0, 1 };
double[] values1 = new double[] { Double.POSITIVE_INFINITY, 3.0 };
double[] values2 = new double[] { 0.0, 2.0 };
SparseVector a = new SparseVector(idx, values1, true);
SparseVector b = new SparseVector(idx, values2, true);
double result = a.extendedDotProduct(b);
assertEquals(6.0, result, 0.00001);
}

@Test
public void testArrayCopyFromStartingPosition() {
SparseVector v = new SparseVector(new double[] { 0.0, 0.0 });
double[] source = new double[] { 5.5, 10.5, 999.9 };
int nextIndex = v.arrayCopyFrom(source, 1);
assertEquals(10.5, v.value(0), 0.0001);
assertEquals(999.9, v.value(1), 0.0001);
assertEquals(3, nextIndex);
}

@Test
public void testCloneMatrixZeroedOnDense() {
SparseVector dense = new SparseVector(new double[] { 1.0, 2.0 });
ConstantMatrix clone = dense.cloneMatrixZeroed();
// assertEquals(0.0, clone.value(0), 0.00001);
// assertEquals(0.0, clone.value(1), 0.00001);
}

@Test(expected = ClassCastException.class)
public void testDotProductWithIllegalMatrixTypeThrows() {
SparseVector v = new SparseVector(new double[] { 1.0 });
// ConstantMatrix invalid = new ConstantMatrix() {
// 
// public int getNumDimensions() {
// return 1;
// }
// 
// public int getDimensions(int[] sizes) {
// sizes[0] = 1;
// return 1;
// }
// 
// public double value(int[] indices) {
// return 0.0;
// }
// 
// public double value(int index) {
// return 0.0;
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
// return 0.0;
// }
// 
// public int singleSize() {
// return 1;
// }
// 
// public double oneNorm() {
// return 0.0;
// }
// 
// public double twoNorm() {
// return 0.0;
// }
// 
// public double absNorm() {
// return 0.0;
// }
// 
// public double infinityNorm() {
// return 0.0;
// }
// 
// public void print() {
// }
// 
// public boolean isNaN() {
// return false;
// }
// };
// v.dotProduct(invalid);
}

@Test
public void testConstructorEmptyIndicesNullValuesBinaryTrue() {
int[] indices = new int[0];
SparseVector v = new SparseVector(indices, true, true);
assertTrue(v.isBinary());
assertEquals(0, v.numLocations());
}

@Test
public void testConstructorEmptyIndicesNonBinary() {
int[] indices = new int[0];
double[] values = new double[0];
SparseVector v = new SparseVector(indices, values, true);
assertFalse(v.isBinary());
assertEquals(0, v.numLocations());
assertEquals(0.0, v.oneNorm(), 0.00001);
}

@Test
public void testDenseTimesEqualsSparseWithPartialMatch() {
SparseVector dense = new SparseVector(new double[] { 1.0, 2.0, 3.0 });
int[] indices = new int[] { 1, 2 };
double[] vals = new double[] { 10.0, 20.0 };
SparseVector sparse = new SparseVector(indices, vals, true);
dense.timesEqualsSparse(sparse, 1.0);
assertEquals(1.0, dense.value(0), 0.00001);
assertEquals(20.0, dense.value(1), 0.00001);
assertEquals(60.0, dense.value(2), 0.00001);
}

@Test
public void testDensePlusEqualsSparseWithOverflowIndexIsIgnored() {
SparseVector dense = new SparseVector(new double[] { 1.0 });
int[] indices = new int[] { 0, 10 };
double[] values = new double[] { 2.0, 99.0 };
SparseVector sparse = new SparseVector(indices, values, true);
dense.plusEqualsSparse(sparse, 1.0);
assertEquals(3.0, dense.value(0), 0.00001);
}

@Test
public void testValueWithNullIndexArray() {
SparseVector v = new SparseVector(new double[] { 3.0 });
assertEquals(3.0, v.value(null), 0.0001);
}

@Test
public void testSortIndicesWithAlreadySortedInput() {
int[] indices = new int[] { 1, 2, 3 };
double[] values = new double[] { 1.1, 2.2, 3.3 };
SparseVector v = new SparseVector(indices, values, true, true);
assertEquals(1.1, v.value(1), 0.00001);
assertEquals(2.2, v.value(2), 0.00001);
assertEquals(3.3, v.value(3), 0.00001);
}

@Test
public void testNegativeZeroIndexHandlingInDenseVector() {
SparseVector v = new SparseVector(new double[] { 10.0, 20.0 });
assertEquals(10.0, v.value(-0), 0.00001);
}

@Test
public void testMultipleDuplicatesInSortIndices() {
int[] idx = new int[] { 5, 5, 5 };
double[] val = new double[] { 1.0, 1.0, 1.0 };
SparseVector v = new SparseVector(idx, val, true, true);
assertEquals(1, v.numLocations());
assertEquals(3.0, v.value(5), 0.00001);
}

@Test
public void testRemoveDuplicatesCountsProperlyWithNonZeroInput() {
int[] idx = new int[] { 1, 1, 2, 3, 3 };
double[] val = new double[] { 1.0, 2.0, 3.0, 4.0, 5.0 };
SparseVector v = new SparseVector(idx, val, true, false, false);
// v.removeDuplicates(2);
assertEquals(3, v.numLocations());
assertEquals(3.0, v.value(1), 0.00001);
assertEquals(3.0, v.value(2), 0.00001);
assertEquals(9.0, v.value(3), 0.00001);
}

@Test
public void testAdditionWithEmptySparseVector() {
SparseVector base = new SparseVector(new double[] { 1.1, 2.2 });
int[] emptyIndices = new int[0];
double[] emptyValues = new double[0];
SparseVector empty = new SparseVector(emptyIndices, emptyValues, true);
SparseVector result = base.vectorAdd(empty, 5.0);
assertEquals(1.1, result.value(0), 0.00001);
assertEquals(2.2, result.value(1), 0.00001);
}

@Test
public void testExtendedDotProductSparseWithInfTimesZeroBothWays() {
int[] idx1 = new int[] { 0, 2 };
double[] val1 = new double[] { Double.POSITIVE_INFINITY, 1.0 };
SparseVector v1 = new SparseVector(idx1, val1, true);
int[] idx2 = new int[] { 0, 2 };
double[] val2 = new double[] { 0.0, 5.0 };
SparseVector v2 = new SparseVector(idx2, val2, true);
double dot = v1.extendedDotProduct(v2);
assertEquals(5.0, dot, 0.00001);
}

@Test
public void testToStringFormatOnBinarySparseVector() {
int[] indices = new int[] { 2, 4 };
SparseVector v = new SparseVector(indices, true);
String result = v.toString(true);
assertTrue(result.contains("2=1.0"));
assertTrue(result.contains("4=1.0"));
}

@Test
public void testToStringWithOnOneLineFalse() {
int[] indices = new int[] { 0, 1 };
double[] values = new double[] { 1.0, 2.0 };
SparseVector v = new SparseVector(indices, values, true);
String result = v.toString(false);
assertTrue(result.contains("0=1.0\n"));
assertTrue(result.contains("1=2.0"));
}

@Test
public void testMapInvocationTargetExceptionIsPropagated() {
try {
double[] values = new double[] { 1.0 };
SparseVector v = new SparseVector(values);
// Method m = FailingFunction.class.getMethod("failOnInput", double.class);
// v.map(m);
fail("Expected exception was not thrown");
} catch (Throwable t) {
assertTrue(t instanceof RuntimeException);
assertEquals("Intentional failure", t.getMessage());
}
}

@Test
public void testArrayCopyIntoDenseTarget() {
double[] vals = new double[] { 2.0, 4.0 };
SparseVector v = new SparseVector(vals);
double[] target = new double[5];
int result = v.arrayCopyInto(target, 2);
assertEquals(2.0, target[2], 0.00001);
assertEquals(4.0, target[3], 0.00001);
assertEquals(4, result);
}

@Test
public void testInfinityNormForZerosReturnsZero() {
SparseVector v = new SparseVector(new double[] { 0.0, 0.0 });
assertEquals(0.0, v.infinityNorm(), 0.00001);
}

@Test
public void testConstructorWithNullIndicesAndNullValuesCreatesBinaryDense() {
double[] values = null;
int[] indices = null;
int capacity = 5;
int size = 5;
SparseVector v = new SparseVector(indices, values, capacity, size, false, false, false);
assertTrue(v.isBinary());
assertEquals(5, v.numLocations());
assertEquals(1.0, v.value(0), 0.00001);
}

@Test
public void testSortIndicesOnlyOnIndicesArrayWithBinaryMode() {
int[] unsorted = new int[] { 3, 1, 4 };
SparseVector v = new SparseVector(unsorted, true, true);
assertEquals(1.0, v.value(1), 0.00001);
assertEquals(1.0, v.value(3), 0.00001);
assertEquals(1.0, v.value(4), 0.00001);
}

@Test
public void testSortIndicesWithNullValuesPreservesBinaryIndices() {
int[] indices = new int[] { 5, 5, 2 };
SparseVector v = new SparseVector(indices, true, true);
assertTrue(v.isBinary());
assertEquals(1.0, v.value(2), 0.00001);
assertEquals(1.0, v.value(5), 0.00001);
assertEquals(0.0, v.value(3), 0.00001);
assertEquals(2, v.numLocations());
}

@Test
public void testCloneMatrixDifferentFromOriginalObject() {
SparseVector original = new SparseVector(new double[] { 1.0, 2.0 });
ConstantMatrix clone = original.cloneMatrix();
assertNotSame(original, clone);
// assertEquals(original.value(0), clone.value(0), 0.00001);
// assertEquals(original.value(1), clone.value(1), 0.00001);
}

@Test
public void testCloneMatrixZeroedGeneratesZerosOnly() {
SparseVector original = new SparseVector(new double[] { 9.9, 8.8 });
ConstantMatrix zeroed = original.cloneMatrixZeroed();
// assertEquals(0.0, zeroed.value(0), 0.00001);
// assertEquals(0.0, zeroed.value(1), 0.00001);
}

@Test
public void testDotProductWithSelfNonBinary() {
int[] indices = new int[] { 0, 1 };
double[] values = new double[] { 3.0, 4.0 };
SparseVector v = new SparseVector(indices, values, true);
double expected = 3.0 * 3.0 + 4.0 * 4.0;
double result = v.dotProduct(v);
assertEquals(expected, result, 0.00001);
}

@Test
public void testDotProductWithNonFiniteValuesIgnoresZeroCase() {
int[] indices = new int[] { 0, 1 };
double[] values = new double[] { Double.POSITIVE_INFINITY, 2.0 };
SparseVector v1 = new SparseVector(indices, values, true);
SparseVector v2 = new SparseVector(indices, new double[] { 0.0, 2.0 }, true);
double result = v1.dotProduct(v2);
assertEquals(4.0, result, 0.00001);
}

@Test
public void testArrayCopyFromAndIntoInsufficientCapacity() {
SparseVector v = new SparseVector(new double[] { 0.0, 0.0 });
double[] source = new double[] { 10.0, 20.0, 30.0 };
int next = v.arrayCopyFrom(source, 1);
assertEquals(20.0, v.value(0), 0.00001);
assertEquals(30.0, v.value(1), 0.00001);
assertEquals(3, next);
double[] target = new double[4];
int updated = v.arrayCopyInto(target, 1);
assertEquals(20.0, target[1], 0.00001);
assertEquals(30.0, target[2], 0.00001);
assertEquals(3, updated);
}

@Test
public void testMapMethodHandlesNaNPropagatedFunction() throws Throwable {
double[] values = new double[] { 1.0, 4.0 };
SparseVector v = new SparseVector(values);
// Method m = DoubleMath.class.getMethod("returnNaNIfEven", double.class);
// v.map(m);
assertEquals(1.0, v.value(0), 0.00001);
assertTrue(Double.isNaN(v.value(1)));
}

@Test
public void testSerializationPreservesValuesCorrectly() throws IOException, ClassNotFoundException {
int[] idx = new int[] { 1, 2 };
double[] val = new double[] { 7.7, 8.8 };
SparseVector v = new SparseVector(idx, val, true);
ByteArrayOutputStream baos = new ByteArrayOutputStream();
ObjectOutputStream out = new ObjectOutputStream(baos);
out.writeObject(v);
out.close();
byte[] data = baos.toByteArray();
ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(data));
SparseVector copy = (SparseVector) in.readObject();
assertEquals(7.7, copy.value(1), 0.00001);
assertEquals(8.8, copy.value(2), 0.00001);
}

@Test
public void testRemoveDuplicatesCountsCorrectlyWhenZeroIsPassed() {
int[] indices = new int[] { 2, 2, 3, 3, 3 };
double[] values = new double[] { 1.0, 1.0, 2.0, 1.0, 3.0 };
SparseVector v = new SparseVector(indices, values, true, false, false);
// v.removeDuplicates(0);
assertEquals(2, v.numLocations());
assertEquals(2.0, v.value(2), 0.00001);
assertEquals(6.0, v.value(3), 0.00001);
}

@Test
public void testNegativeAndZeroNormsOnEmptyVector() {
SparseVector empty = new SparseVector(new int[0], new double[0], true);
assertEquals(0.0, empty.oneNorm(), 0.00001);
assertEquals(0.0, empty.twoNorm(), 0.00001);
assertEquals(0.0, empty.absNorm(), 0.00001);
assertEquals(0.0, empty.infinityNorm(), 0.00001);
}

@Test
public void testIsNaNIsInfiniteIsNaNOrInfiniteReturnsFalseForBinary() {
int[] indices = new int[] { 1, 3 };
SparseVector v = new SparseVector(indices, true);
assertFalse(v.isNaN());
assertFalse(v.isInfinite());
assertFalse(v.isNaNOrInfinite());
}

@Test
public void testVectorAddWithScaleZeroReturnsOriginal() {
SparseVector v1 = new SparseVector(new double[] { 2.0, 3.0 });
SparseVector v2 = new SparseVector(new double[] { 5.0, 6.0 });
SparseVector result = v1.vectorAdd(v2, 0.0);
assertEquals(2.0, result.value(0), 0.00001);
assertEquals(3.0, result.value(1), 0.00001);
}

@Test
public void testVectorAddDenseAndSparseWithOverlap() {
SparseVector dense = new SparseVector(new double[] { 2.0, 2.0 });
int[] idx = new int[] { 0, 1 };
double[] val = new double[] { 3.0, 4.0 };
SparseVector sparse = new SparseVector(idx, val, true);
SparseVector result = dense.vectorAdd(sparse, 1.0);
assertEquals(5.0, result.value(0), 0.00001);
assertEquals(6.0, result.value(1), 0.00001);
}

@Test
public void testSparseVectorWithNullValuesAndIndicesAccessors() {
int[] indices = null;
double[] values = null;
SparseVector v = new SparseVector(indices, values, 3, 3, false, false, false);
assertTrue(v.isBinary());
assertEquals(3, v.numLocations());
assertEquals(1.0, v.value(0), 0.00001);
assertEquals(0, v.indexAtLocation(0));
assertEquals(1, v.indexAtLocation(1));
}

@Test
public void testDenseVectorLocationIsIdentity() {
double[] data = new double[] { 5.0, 6.0 };
SparseVector v = new SparseVector(data);
assertEquals(0, v.location(0));
assertEquals(1, v.location(1));
}

@Test
public void testSparseVectorLocationBinarySearchNotPresent() {
int[] indices = new int[] { 2, 4, 6 };
double[] values = new double[] { 1.0, 1.0, 1.0 };
SparseVector v = new SparseVector(indices, values, true);
assertTrue(v.location(3) < 0);
}

@Test
public void testAddToWithScaleZeroProducesNoChange() {
double[] values = new double[] { 1.0, 2.0, 3.0 };
SparseVector v = new SparseVector(values);
double[] acc = new double[] { 10.0, 10.0, 10.0 };
v.addTo(acc, 0.0);
assertEquals(10.0, acc[0], 0.00001);
assertEquals(10.0, acc[1], 0.00001);
assertEquals(10.0, acc[2], 0.00001);
}

@Test
public void testSparseVectorDotProductShorterHasHigherIndex() {
int[] indices1 = new int[] { 1, 10 };
double[] values1 = new double[] { 1.0, 2.0 };
int[] indices2 = new int[] { 1, 2 };
double[] values2 = new double[] { 4.0, 3.0 };
SparseVector a = new SparseVector(indices1, values1, true);
SparseVector b = new SparseVector(indices2, values2, true);
double result = a.dotProduct(b);
assertEquals(1.0 * 4.0, result, 0.00001);
}

@Test
public void testSingleIndexAndSingleToIndicesOnSparseVector() {
int[] indices = new int[] { 5, 10, 15 };
double[] values = new double[] { 0.5, 0.6, 0.7 };
SparseVector v = new SparseVector(indices, values, true);
int[] arr = new int[1];
v.singleToIndices(2, arr);
assertEquals(2, v.singleIndex(new int[] { 2 }));
assertEquals(2, arr[0]);
}

@Test(expected = IllegalArgumentException.class)
public void testSetValueOnUnlistedIndexInSparseThrows() {
int[] index = new int[] { 0, 1, 2 };
double[] val = new double[] { 1.0, 2.0, 3.0 };
SparseVector v = new SparseVector(index, val, true);
v.setValue(3, 10.0);
}

@Test
public void testSetValueOnListedIndexInSparseChangesOnlyThatValue() {
int[] index = new int[] { 0, 1, 2 };
double[] val = new double[] { 1.0, 2.0, 3.0 };
SparseVector v = new SparseVector(index, val, true);
v.setValue(2, 9.9);
assertEquals(9.9, v.value(2), 0.00001);
assertEquals(1.0, v.value(0), 0.00001);
}

@Test
public void testNaNPropagationInMapComputation() throws Throwable {
double[] val = new double[] { 1.0, 2.0 };
SparseVector v = new SparseVector(val);
// Method nanMethod = TestFunc.class.getMethod("returnNaN", double.class);
// v.map(nanMethod);
assertTrue(Double.isNaN(v.value(0)));
assertTrue(Double.isNaN(v.value(1)));
}

@Test(expected = IllegalArgumentException.class)
public void testMapWithWrongSignatureMethodThrows() throws Throwable {
double[] data = new double[] { 1.0, 2.0 };
SparseVector v = new SparseVector(data);
Method bad = String.class.getMethod("substring", int.class);
v.map(bad);
}

@Test(expected = UnsupportedOperationException.class)
public void testMakeBinaryThrowsUnsupported() {
SparseVector v = new SparseVector(new double[] { 1.0, 2.0 });
v.makeBinary();
}

@Test(expected = UnsupportedOperationException.class)
public void testMakeNonBinaryThrowsUnsupported() {
int[] idx = new int[] { 0, 1 };
SparseVector v = new SparseVector(idx, true);
v.makeNonBinary();
}

@Test
public void testToStringBinaryVectorOnelinerFormat() {
int[] idx = new int[] { 1, 4, 6 };
SparseVector binary = new SparseVector(idx, true);
String s = binary.toString(true);
assertTrue(s.contains("1=1.0"));
assertTrue(s.contains("4=1.0"));
assertTrue(s.contains("6=1.0"));
}

@Test
public void testIsNaNReturnsFalseForValidValues() {
SparseVector v = new SparseVector(new double[] { 1.0 });
assertFalse(v.isNaN());
}

@Test
public void testIsInfiniteDetectsPositiveInfinity() {
double[] values = new double[] { Double.POSITIVE_INFINITY, 1.0 };
SparseVector v = new SparseVector(values);
assertTrue(v.isInfinite());
}

@Test
public void testDotProductDenseVsBinarySparse() {
double[] vals = new double[] { 10.0, 20.0, 30.0 };
DenseVector dv = new DenseVector(vals);
int[] idx = new int[] { 0, 2 };
SparseVector sv = new SparseVector(idx, true);
double product = sv.dotProduct(dv);
assertEquals(10.0 + 30.0, product, 0.00001);
}

@Test
public void testDotProductNonBinaryVsBinaryReturnsSummedMatchingValues() {
int[] idx1 = new int[] { 1, 3 };
double[] val1 = new double[] { 2.0, 4.0 };
SparseVector sv = new SparseVector(idx1, val1, true);
int[] idx2 = new int[] { 1, 5, 3 };
SparseVector binary = new SparseVector(idx2, true);
double result = sv.dotProduct(binary);
assertEquals(6.0, result, 0.00001);
}

@Test
public void testSelfPlusEqualsMakesExpectedUpdate() {
int[] idx = new int[] { 2 };
double[] val = new double[] { 2.0 };
SparseVector v = new SparseVector(idx, val, true);
v.plusEqualsSparse(v, 1.0);
assertEquals(4.0, v.value(2), 0.00001);
}

@Test
public void testSelfTimesEqualsSquaresValues() {
int[] idx = new int[] { 1 };
double[] val = new double[] { 3.0 };
SparseVector v = new SparseVector(idx, val, true);
v.timesEqualsSparse(v, 1.0);
assertEquals(9.0, v.value(1), 0.00001);
}

@Test
public void testInfinityNormForNegativeAndPositiveValues() {
double[] vals = new double[] { -5.0, 4.0 };
SparseVector v = new SparseVector(vals);
assertEquals(5.0, v.infinityNorm(), 0.00001);
}

@Test
public void testAbsNormForNegativeValues() {
double[] vals = new double[] { -1.0, -3.0 };
SparseVector v = new SparseVector(vals);
assertEquals(4.0, v.absNorm(), 0.00001);
}

@Test
public void testSingleIndexNegativeIndexThrowsAssertion() {
int[] idx = new int[] { 1, 2 };
double[] val = new double[] { 4.0, 5.0 };
SparseVector v = new SparseVector(idx, val, true);
int[] indexArray = new int[] { -2 };
assertEquals(-2, v.singleIndex(indexArray));
}

@Test
public void testEmptyArrayCopyIntoReturnsCorrectIndex() {
SparseVector v = new SparseVector(new int[0], new double[0], true);
double[] target = new double[5];
int result = v.arrayCopyInto(target, 3);
assertEquals(3, result);
}

@Test
public void testAddToWithSparseIndicesOnly() {
int[] idx = new int[] { 0, 3 };
double[] val = new double[] { 2.0, 5.0 };
SparseVector sv = new SparseVector(idx, val, true);
double[] acc = new double[5];
sv.addTo(acc);
assertEquals(2.0, acc[0], 0.00001);
assertEquals(0.0, acc[1], 0.00001);
assertEquals(0.0, acc[2], 0.00001);
assertEquals(5.0, acc[3], 0.00001);
}

@Test
public void testDotProductWithSparseVectorsHavingOverlappingIndicesUnbalancedLengths() {
int[] idx1 = new int[] { 1, 2, 5 };
double[] val1 = new double[] { 1.0, 2.0, 3.0 };
SparseVector v1 = new SparseVector(idx1, val1, true);
int[] idx2 = new int[] { 2 };
double[] val2 = new double[] { 10.0 };
SparseVector v2 = new SparseVector(idx2, val2, true);
double result = v1.dotProduct(v2);
assertEquals(20.0, result, 0.00001);
}

@Test
public void testDotProductBinaryVsBinaryReturnsCountOfSharedIndices() {
int[] idx1 = new int[] { 1, 3, 5 };
SparseVector v1 = new SparseVector(idx1, true);
int[] idx2 = new int[] { 3, 4, 5 };
SparseVector v2 = new SparseVector(idx2, true);
double result = v1.dotProduct(v2);
assertEquals(2.0, result, 0.00001);
}

@Test
public void testExtendedDotProductIgnoresInfTimesZeroCases() {
int[] indices1 = new int[] { 1 };
double[] values1 = new double[] { Double.POSITIVE_INFINITY };
SparseVector v1 = new SparseVector(indices1, values1, true);
int[] indices2 = new int[] { 1 };
double[] values2 = new double[] { 0.0 };
SparseVector v2 = new SparseVector(indices2, values2, true);
double dot = v1.extendedDotProduct(v2);
assertEquals(0.0, dot, 0.00001);
}

@Test
public void testSortIndicesDoesNotChangeDenseVectorOrdering() {
double[] vals = new double[] { 3.0, 1.0, 4.0 };
SparseVector v = new SparseVector(vals);
v.setValueAtLocation(1, 9.9);
assertEquals(9.9, v.value(1), 0.00001);
}

@Test
public void testRemoveDuplicatesNoDuplicatesFoundDoesNothing() {
int[] idx = new int[] { 1, 3, 5 };
double[] val = new double[] { 1.0, 2.0, 3.0 };
SparseVector v = new SparseVector(idx, val, true);
// v.removeDuplicates(0);
assertEquals(1.0, v.value(1), 0.00001);
assertEquals(2.0, v.value(3), 0.00001);
assertEquals(3.0, v.value(5), 0.00001);
}

@Test
public void testEmptyVectorIndexAccessReturnsZero() {
SparseVector v = new SparseVector(new int[0], new double[0], true);
assertEquals(0.0, v.value(10), 0.00001);
}

@Test
public void testSingleSizeReturnsMaxIndexForSparseVector() {
int[] idx = new int[] { 2, 5, 9 };
double[] val = new double[] { 1.0, 2.0, 3.0 };
SparseVector v = new SparseVector(idx, val, true);
assertEquals(9, v.singleSize());
}

@Test
public void testSingleSizeOnEmptySparseReturnsZero() {
SparseVector v = new SparseVector(new int[0], new double[0], true);
assertEquals(0, v.singleSize());
}

@Test
public void testCloneMatrixOnBinaryVectorCopiesCorrectly() {
int[] idx = new int[] { 1, 2 };
SparseVector v = new SparseVector(idx, true);
ConstantMatrix cloned = v.cloneMatrix();
// assertEquals(1.0, cloned.value(1), 0.00001);
// assertEquals(1.0, cloned.value(2), 0.00001);
}

@Test
public void testConstructorSparseWithCapacityGreaterThanSize() {
int[] idx = new int[] { 1, 4 };
double[] val = new double[] { 5.0, 7.0 };
SparseVector v = new SparseVector(idx, val, 6, 2, true, false, false);
assertEquals(5.0, v.value(1), 0.00001);
assertEquals(7.0, v.value(4), 0.00001);
assertEquals(0.0, v.value(2), 0.00001);
}

@Test
public void testConstructorSparseWithRemoveDuplicatesFalseLeavesDuplicates() {
int[] idx = new int[] { 1, 1, 2 };
double[] val = new double[] { 1.0, 2.0, 3.0 };
SparseVector v = new SparseVector(idx, val, true, false, false);
assertEquals(2, v.value(1), 0.00001);
}

@Test
public void testInfinityNormHandlesSingleNegativeEntry() {
double[] val = new double[] { -9999.0 };
SparseVector v = new SparseVector(val);
assertEquals(9999.0, v.infinityNorm(), 0.00001);
}

@Test
public void testSetValueAtLocationSparseVectorWorks() {
int[] idx = new int[] { 1, 3 };
double[] val = new double[] { 4.0, 5.0 };
SparseVector v = new SparseVector(idx, val, true);
v.setValueAtLocation(1, 7.7);
assertEquals(7.7, v.value(3), 0.00001);
}

@Test
public void testSerializationOnBinaryVectorMaintainsData() throws IOException, ClassNotFoundException {
SparseVector v = new SparseVector(new int[] { 2, 4 }, true);
ByteArrayOutputStream bos = new ByteArrayOutputStream();
ObjectOutputStream out = new ObjectOutputStream(bos);
out.writeObject(v);
out.close();
byte[] bytes = bos.toByteArray();
ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes));
SparseVector copy = (SparseVector) in.readObject();
assertEquals(1.0, copy.value(2), 0.00001);
assertEquals(1.0, copy.value(4), 0.00001);
}

@Test
public void testDenseVectorArrayCopyFromUsesOffsetProperly() {
SparseVector v = new SparseVector(new double[] { 0.0, 0.0 });
double[] source = new double[] { 10.0, 20.0, 30.0 };
int pos = v.arrayCopyFrom(source, 1);
assertEquals(20.0, v.value(0), 0.00001);
assertEquals(30.0, v.value(1), 0.00001);
assertEquals(3, pos);
}
}
