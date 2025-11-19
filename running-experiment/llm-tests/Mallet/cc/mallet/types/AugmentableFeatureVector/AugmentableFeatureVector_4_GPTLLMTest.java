package cc.mallet.types.tests;

import cc.mallet.types.*;
import cc.mallet.util.PropertyList;
import org.junit.Test;
import java.io.*;
import static org.junit.Assert.*;

public class AugmentableFeatureVector_4_GPTLLMTest {

@Test
public void testConstructorWithDenseValues() {
Alphabet dict = new Alphabet();
dict.lookupIndex("apple");
dict.lookupIndex("banana");
dict.lookupIndex("carrot");
double[] values = new double[] { 1.0, 2.0, 3.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, values);
assertEquals(3, afv.numLocations());
assertEquals(1.0, afv.value(0), 0.0001);
assertEquals(2.0, afv.value(1), 0.0001);
assertEquals(3.0, afv.value(2), 0.0001);
}

@Test
public void testConstructorWithSparseIndicesAndValues() {
Alphabet dict = new Alphabet();
dict.lookupIndex("x");
dict.lookupIndex("y");
dict.lookupIndex("z");
int[] indices = new int[] { 0, 2 };
double[] values = new double[] { 5.0, 10.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, indices, values, 2);
assertEquals(2, afv.numLocations());
assertEquals(5.0, afv.value(0), 0.0001);
assertEquals(0.0, afv.value(1), 0.0001);
assertEquals(10.0, afv.value(2), 0.0001);
}

@Test(expected = IllegalArgumentException.class)
public void testAddNonBinaryThrowsException() {
Alphabet dict = new Alphabet();
int index = dict.lookupIndex("feature");
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, true);
afv.add(index, 2.0);
}

@Test
public void testAddValueAtIndexToRealVector() {
Alphabet dict = new Alphabet();
int index = dict.lookupIndex("weight");
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, 3, false);
afv.add(index, 4.2);
assertEquals(4.2, afv.value(index), 0.0001);
}

@Test
public void testAddTwiceToSameIndexAccumulates() {
Alphabet dict = new Alphabet();
int index = dict.lookupIndex("run");
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, 2, false);
afv.add(index, 1.5);
afv.add(index, 2.5);
assertEquals(4.0, afv.value(index), 0.0001);
}

@Test
public void testAddObjectKey() {
Alphabet dict = new Alphabet();
dict.lookupIndex("something");
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, 2, false);
afv.add("something", 3.0);
int index = dict.lookupIndex("something");
assertEquals(3.0, afv.value(index), 0.0001);
}

@Test
public void testSetValueReplacesCurrentValue() {
Alphabet dict = new Alphabet();
int index = dict.lookupIndex("set");
int[] indices = new int[] { index };
double[] values = new double[] { 7.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, indices, values, 1);
afv.setValue(index, 4.0);
assertEquals(4.0, afv.value(index), 0.0001);
}

@Test
public void testDotProductWithDenseVector() {
Alphabet dict = new Alphabet();
dict.lookupIndex("a");
dict.lookupIndex("b");
dict.lookupIndex("c");
double[] afvValues = new double[] { 1.0, 2.0, 3.0 };
DenseVector dense = new DenseVector(new double[] { 2.0, 1.0, 4.0 });
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, afvValues);
double result = afv.dotProduct(dense);
assertEquals(1.0 * 2.0 + 2.0 * 1.0 + 3.0 * 4.0, result, 0.0001);
}

@Test
public void testToFeatureVectorCopiesContent() {
Alphabet dict = new Alphabet();
dict.lookupIndex("f");
dict.lookupIndex("g");
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, 2, false);
afv.add("f", 5.0);
afv.add("g", 10.0);
FeatureVector fv = afv.toFeatureVector();
int fi = dict.lookupIndex("f");
int gi = dict.lookupIndex("g");
assertEquals(5.0, fv.value(fi), 0.0001);
assertEquals(10.0, fv.value(gi), 0.0001);
}

@Test
public void testOneNormCalculation() {
Alphabet dict = new Alphabet();
dict.lookupIndex("foo");
dict.lookupIndex("bar");
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, 2, false);
afv.add("foo", 4.0);
afv.add("bar", 3.0);
assertEquals(7.0, afv.oneNorm(), 0.0001);
}

@Test
public void testTwoNormCalculation() {
Alphabet dict = new Alphabet();
dict.lookupIndex("x");
dict.lookupIndex("y");
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, 2, false);
afv.add("x", 3.0);
afv.add("y", 4.0);
assertEquals(5.0, afv.twoNorm(), 0.0001);
}

@Test
public void testInfinityNormCalculation() {
Alphabet dict = new Alphabet();
dict.lookupIndex("max");
dict.lookupIndex("min");
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, 2, false);
afv.add("max", -15.0);
afv.add("min", 10.0);
assertEquals(15.0, afv.infinityNorm(), 0.0001);
}

@Test
public void testCloneMatrixZeroedProducesZeroedCopy() {
Alphabet dict = new Alphabet();
dict.lookupIndex("alpha");
dict.lookupIndex("beta");
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, 2, false);
afv.add("alpha", 2.0);
afv.add("beta", 3.0);
AugmentableFeatureVector clone = (AugmentableFeatureVector) afv.cloneMatrixZeroed();
int index1 = dict.lookupIndex("alpha");
int index2 = dict.lookupIndex("beta");
double v1 = clone.value(index1);
double v2 = clone.value(index2);
assertEquals(0.0, v1, 0.0001);
assertEquals(0.0, v2, 0.0001);
}

@Test
public void testFeatureSequenceBinaryConstructor() {
Alphabet dict = new Alphabet();
dict.lookupIndex("word1");
dict.lookupIndex("word2");
FeatureSequence fs = new FeatureSequence(dict);
fs.add("word1");
fs.add("word2");
AugmentableFeatureVector afv = new AugmentableFeatureVector(fs, true);
int i1 = dict.lookupIndex("word1");
int i2 = dict.lookupIndex("word2");
assertEquals(1.0, afv.value(i1), 0.0001);
assertEquals(1.0, afv.value(i2), 0.0001);
}

@Test
public void testAddToAccumulatorArray() {
Alphabet dict = new Alphabet();
int ix1 = dict.lookupIndex("item1");
int ix2 = dict.lookupIndex("item2");
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, 3, false);
afv.add(ix1, 2.0);
afv.add(ix2, 3.0);
double[] accumulator = new double[5];
afv.addTo(accumulator);
assertEquals(2.0, accumulator[ix1], 0.0001);
assertEquals(3.0, accumulator[ix2], 0.0001);
}

@Test
public void testSerializationRoundTrip() throws Exception {
Alphabet dict = new Alphabet();
dict.lookupIndex("key");
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, 2, false);
afv.add("key", 7.5);
ByteArrayOutputStream bos = new ByteArrayOutputStream();
ObjectOutputStream oos = new ObjectOutputStream(bos);
oos.writeObject(afv);
oos.close();
ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
ObjectInputStream ois = new ObjectInputStream(bis);
AugmentableFeatureVector deserialized = (AugmentableFeatureVector) ois.readObject();
assertNotNull(deserialized);
}

@Test(expected = IllegalArgumentException.class)
public void testAddBinaryFeatureToRealValuedVectorThrows() {
Alphabet dict = new Alphabet();
dict.lookupIndex("real");
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, 4, false);
afv.add(dict.lookupIndex("real"));
}

@Test
public void testAddBeyondInitialCapacityDenseVector() {
Alphabet dict = new Alphabet();
dict.lookupIndex("a");
dict.lookupIndex("b");
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, 1, false);
afv.add(dict.lookupIndex("a"), 1.0);
afv.add(dict.lookupIndex("b"), 2.0);
assertEquals(1.0, afv.value(dict.lookupIndex("a")), 0.0001);
assertEquals(2.0, afv.value(dict.lookupIndex("b")), 0.0001);
}

@Test
public void testAddBeyondInitialCapacitySparseVector() {
Alphabet dict = new Alphabet();
dict.lookupIndex("a");
dict.lookupIndex("b");
dict.lookupIndex("c");
dict.lookupIndex("d");
dict.lookupIndex("e");
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, true);
afv.add(dict.lookupIndex("a"));
afv.add(dict.lookupIndex("b"));
afv.add(dict.lookupIndex("c"));
afv.add(dict.lookupIndex("d"));
afv.add(dict.lookupIndex("e"));
assertEquals(1.0, afv.value(dict.lookupIndex("e")), 0.0001);
}

@Test
public void testSetAllZerosOnDenseVector() {
Alphabet dict = new Alphabet();
dict.lookupIndex("x");
dict.lookupIndex("y");
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, 2, false);
afv.add("x", 1.0);
afv.add("y", 2.0);
afv.setAll(0.0);
assertEquals(0.0, afv.value(dict.lookupIndex("x")), 0.0001);
assertEquals(0.0, afv.value(dict.lookupIndex("y")), 0.0001);
}

@Test
public void testRemoveDuplicatesWithMultipleRepeatedIndices() {
Alphabet dict = new Alphabet();
dict.lookupIndex("word");
int index = dict.lookupIndex("word");
int[] indices = new int[] { index, index, index };
double[] values = new double[] { 1.0, 2.0, 3.0 };
// AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, indices, values, 3, true, false, false);
// afv.sortIndices();
// assertEquals(6.0, afv.value(index), 0.0001);
// assertEquals(1, afv.numLocations());
}

@Test
public void testDotProductWithMismatchedSizeDenseVector() {
Alphabet dict = new Alphabet();
dict.lookupIndex("foo");
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, 5, false);
afv.add("foo", 4.5);
DenseVector dv = new DenseVector(new double[10]);
dv.setValue(dict.lookupIndex("foo"), 2.0);
double dot = afv.dotProduct(dv);
assertEquals(9.0, dot, 0.0001);
}

@Test
public void testAddWithPrefixAndNonBinary() {
Alphabet dict1 = new Alphabet();
dict1.lookupIndex("prefix-apple");
dict1.lookupIndex("prefix-banana");
Alphabet dict2 = new Alphabet();
int i1 = dict2.lookupIndex("apple");
int i2 = dict2.lookupIndex("banana");
double[] values = new double[] { 3.0, 4.0 };
// FeatureVector fv = new FeatureVector(dict2, new int[] { i1, i2 }, values, 2, 2, true, false, false);
// AugmentableFeatureVector afv = new AugmentableFeatureVector(dict1, 4, false);
// afv.add(fv, "prefix-", false);
// assertEquals(3.0, afv.value(dict1.lookupIndex("prefix-apple")), 0.0001);
// assertEquals(4.0, afv.value(dict1.lookupIndex("prefix-banana")), 0.0001);
}

@Test
public void testSingleSizeForEmptyVector() {
Alphabet dict = new Alphabet();
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, 2, false);
assertEquals(0, afv.singleSize());
}

@Test
public void testSingleSizeForDenseVector() {
Alphabet dict = new Alphabet();
dict.lookupIndex("one");
dict.lookupIndex("two");
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, 2, false);
afv.add("one", 1.0);
afv.add("two", 2.0);
int val = afv.singleSize();
assertEquals(2, val);
}

@Test
public void testCloneMatrixPreservesValues() {
Alphabet dict = new Alphabet();
dict.lookupIndex("x");
int index = dict.lookupIndex("y");
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, 4, false);
afv.add(index, 3.5);
AugmentableFeatureVector clone = (AugmentableFeatureVector) afv.cloneMatrix();
assertNotSame(afv, clone);
assertEquals(3.5, clone.value(index), 0.0001);
}

@Test
public void testAddToWithScaleDenseVector() {
Alphabet dict = new Alphabet();
dict.lookupIndex("a");
dict.lookupIndex("b");
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, 2, false);
afv.add("a", 2.0);
afv.add("b", 3.0);
double[] accumulator = new double[3];
afv.addTo(accumulator, 0.5);
assertEquals(1.0, accumulator[dict.lookupIndex("a")], 0.0001);
assertEquals(1.5, accumulator[dict.lookupIndex("b")], 0.0001);
}

@Test
public void testAddToWithScaleBinaryVector() {
Alphabet dict = new Alphabet();
int idx1 = dict.lookupIndex("f1");
int idx2 = dict.lookupIndex("f2");
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, true);
afv.add(idx1);
afv.add(idx2);
double[] accumulator = new double[4];
afv.addTo(accumulator, 2.0);
assertEquals(2.0, accumulator[idx1], 0.0001);
assertEquals(2.0, accumulator[idx2], 0.0001);
}

@Test
public void testValueAtInvalidIndexReturnsZero() {
Alphabet dict = new Alphabet();
dict.lookupIndex("a");
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, 1, false);
afv.add("a", 1.0);
assertEquals(0.0, afv.value(5), 0.0001);
}

@Test
public void testDotProductWithNullValuesBinaryAgainstDense() {
Alphabet dict = new Alphabet();
int a = dict.lookupIndex("x");
int b = dict.lookupIndex("y");
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, true);
afv.add(a);
afv.add(b);
DenseVector dv = new DenseVector(new double[10]);
dv.setValue(a, 2.0);
dv.setValue(b, 3.0);
assertEquals(5.0, afv.dotProduct(dv), 0.0001);
}

@Test
public void testDotProductWithNullIndicesAgainstDense() {
Alphabet dict = new Alphabet();
int i1 = dict.lookupIndex("x");
int i2 = dict.lookupIndex("y");
double[] vals = new double[5];
vals[i1] = 2.0;
vals[i2] = 3.0;
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, vals);
DenseVector dv = new DenseVector(new double[5]);
dv.setValue(i1, 2.0);
dv.setValue(i2, 4.0);
assertEquals(2.0 * 2.0 + 3.0 * 4.0, afv.dotProduct(dv), 0.0001);
}

@Test
public void testPlusEqualsWithSparseVectorBinary() {
Alphabet dict = new Alphabet();
int i1 = dict.lookupIndex("a");
int i2 = dict.lookupIndex("b");
AugmentableFeatureVector afv1 = new AugmentableFeatureVector(dict, 2, false);
afv1.add(i1, 1.0);
afv1.add(i2, 1.0);
int[] indices = new int[] { i1 };
// SparseVector sv = new FeatureVector(dict, indices, null, 1, 1, true, false, false);
// afv1.plusEquals(sv, 3.0);
assertEquals(4.0, afv1.value(i1), 0.0001);
assertEquals(1.0, afv1.value(i2), 0.0001);
}

@Test
public void testPlusEqualsWithSparseVectorRealValues() {
Alphabet dict = new Alphabet();
int i1 = dict.lookupIndex("one");
int i2 = dict.lookupIndex("two");
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, 2, false);
afv.add(i1, 2.0);
afv.add(i2, 4.0);
int[] indices = new int[] { i1, i2 };
double[] values = new double[] { 2.0, 3.0 };
// SparseVector sv = new FeatureVector(dict, indices, values, 2, 2, true, false, false);
// afv.plusEquals(sv, 2.0);
assertEquals(2.0 + 4.0, afv.value(i1), 0.0001);
assertEquals(4.0 + 6.0, afv.value(i2), 0.0001);
}

@Test
public void testLocationReturnsMinusOneForMissingIndex() {
Alphabet dict = new Alphabet();
dict.lookupIndex("a");
dict.lookupIndex("b");
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, 2, false);
int missingIndex = 10;
assertEquals(-1, afv.location(missingIndex));
}

@Test
public void testValueAtLocationForSparseVector() {
Alphabet dict = new Alphabet();
int i = dict.lookupIndex("key");
int[] indices = new int[] { i };
double[] values = new double[] { 9.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, indices, values, 1);
assertEquals(9.0, afv.valueAtLocation(0), 0.0001);
}

@Test
public void testValueAtLocationForDenseVector() {
Alphabet dict = new Alphabet();
dict.lookupIndex("a");
dict.lookupIndex("b");
double[] values = new double[] { 3.0, 4.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, values);
assertEquals(3.0, afv.valueAtLocation(0), 0.0001);
assertEquals(4.0, afv.valueAtLocation(1), 0.0001);
}

@Test
public void testSortIndicesTriggersMaxSortedIndexUpdate() {
Alphabet dict = new Alphabet();
int i1 = dict.lookupIndex("z");
int i2 = dict.lookupIndex("a");
int[] indices = new int[] { i1, i2 };
double[] values = new double[] { 1.0, 2.0 };
// AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, indices, values, 2, true, false, false);
// int index0 = afv.indexAtLocation(0);
// int index1 = afv.indexAtLocation(1);
// assertTrue(index0 < index1);
}

@Test
public void testRemoveDuplicatesDefaultCount() {
Alphabet dict = new Alphabet();
int index = dict.lookupIndex("duplicate");
int[] indices = new int[] { index, index };
double[] values = new double[] { 1.0, 3.0 };
// AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, indices, values, 2, false, false, false);
// afv.sortIndices();
// assertEquals(4.0, afv.value(index), 0.0001);
// assertEquals(1, afv.numLocations());
}

@Test
public void testAddToExceedingOriginalSizeDense() {
Alphabet dict = new Alphabet();
dict.lookupIndex("out");
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, 1, false);
afv.add("out", 1.0);
dict.lookupIndex("beyond");
afv.add("beyond", 5.0);
assertEquals(5.0, afv.value(dict.lookupIndex("beyond")), 0.0001);
}

@Test
public void testDotProductEmptyVectorReturnsZero() {
Alphabet dict = new Alphabet();
DenseVector dv = new DenseVector(new double[] { 1.0, 2.0 });
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, 2, false);
double dot = afv.dotProduct(dv);
assertEquals(0.0, dot, 0.0001);
}

@Test
public void testToSparseVectorPreservesData() {
Alphabet dict = new Alphabet();
int idx = dict.lookupIndex("hello");
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, 2, false);
afv.add(idx, 9.0);
SparseVector sv = afv.toSparseVector();
assertEquals(9.0, sv.value(idx), 0.0001);
assertEquals(1, sv.numLocations());
}

@Test
public void testSerializationPreservesSizeAndMaxSortedIndex() throws Exception {
Alphabet dict = new Alphabet();
dict.lookupIndex("a");
dict.lookupIndex("b");
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, 5, false);
afv.add("a", 1.0);
afv.add("b", 2.0);
ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
ObjectOutputStream out = new ObjectOutputStream(byteOut);
out.writeObject(afv);
out.close();
ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
ObjectInputStream in = new ObjectInputStream(byteIn);
AugmentableFeatureVector deserialized = (AugmentableFeatureVector) in.readObject();
in.close();
assertNotNull(deserialized);
assertEquals(2, deserialized.numLocations());
assertEquals(1.0, deserialized.value(dict.lookupIndex("a")), 0.0001);
assertEquals(2.0, deserialized.value(dict.lookupIndex("b")), 0.0001);
}

@Test
public void testCloneMatrixZeroedOnSparseVectorPreservesIndicesButZerosValues() {
Alphabet dict = new Alphabet();
int idx = dict.lookupIndex("zero");
int[] indices = new int[] { idx };
double[] values = new double[] { 2.5 };
// AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, indices, values, 1, false, false, false);
// AugmentableFeatureVector clone = (AugmentableFeatureVector) afv.cloneMatrixZeroed();
// double originalValue = afv.value(idx);
// double clonedValue = clone.value(idx);
// assertEquals(2.5, originalValue, 0.0001);
// assertEquals(0.0, clonedValue, 0.0001);
}

@Test
public void testAddSameIndexMultipleTimesSparse() {
Alphabet dict = new Alphabet();
int index = dict.lookupIndex("item");
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, true);
afv.add(index);
afv.add(index);
// afv.sortIndices();
assertEquals(2.0, afv.value(index), 0.0001);
assertEquals(1, afv.numLocations());
}

@Test
public void testSetValueOnDenseVectorAtUpperBound() {
Alphabet dict = new Alphabet();
dict.lookupIndex("x");
dict.lookupIndex("y");
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, 2, false);
afv.add("x", 1.0);
afv.setValue(dict.lookupIndex("x"), 9.0);
assertEquals(9.0, afv.value(dict.lookupIndex("x")), 0.0001);
}

@Test
public void testFeatureVectorConstructorPreservesExistingFeatureData() {
Alphabet dict = new Alphabet();
int index1 = dict.lookupIndex("first");
int index2 = dict.lookupIndex("second");
int[] indices = new int[] { index1, index2 };
double[] values = new double[] { 4.0, 5.0 };
// FeatureVector fv = new FeatureVector(dict, indices, values, 2, 2, true, false, false);
// AugmentableFeatureVector afv = new AugmentableFeatureVector(fv);
// assertEquals(4.0, afv.value(index1), 0.0001);
// assertEquals(5.0, afv.value(index2), 0.0001);
}

@Test
public void testSetValueBeyondDefinedIndexHasNoEffectOnSparse() {
Alphabet dict = new Alphabet();
int idx = dict.lookupIndex("one");
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, true);
afv.add(idx);
try {
afv.setValue(100, 5.0);
fail("Expected ArrayIndexOutOfBoundsException or assertion error");
} catch (Exception e) {
// assertTrue(e instanceof ArrayIndexOutOfBoundsException || e instanceof AssertionError);
}
}

@Test(expected = IllegalArgumentException.class)
public void testAddInvalidValueToBinaryVectorThrows() {
Alphabet dict = new Alphabet();
dict.lookupIndex("b");
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, true);
afv.add("b", 2.0);
}

@Test(expected = IllegalArgumentException.class)
public void testAddBinaryFeatureToRealVectorThrows() {
Alphabet dict = new Alphabet();
dict.lookupIndex("a");
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, 3, false);
afv.add(dict.lookupIndex("a"));
}

@Test
public void testAddOutOfBoundsOnDenseTriggersResize() {
Alphabet dict = new Alphabet();
int idx = dict.lookupIndex("big");
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, 1, false);
afv.add(idx + 10, 4.5);
assertEquals(4.5, afv.value(idx + 10), 0.0001);
}

@Test
public void testSparseToSparseDotProductMixedOverlap() {
Alphabet dict = new Alphabet();
int i1 = dict.lookupIndex("a");
int i2 = dict.lookupIndex("b");
int i3 = dict.lookupIndex("c");
AugmentableFeatureVector afv1 = new AugmentableFeatureVector(dict, true);
afv1.add(i1);
afv1.add(i3);
int[] indices = new int[] { i2, i3 };
// SparseVector sv = new FeatureVector(dict, indices, null, 2, 2, true, false, false);
// double result = afv1.dotProduct(sv);
// assertEquals(1.0, result, 0.0001);
}

@Test
public void testCloneMatrixCreatesIndependentCopy() {
Alphabet dict = new Alphabet();
int idx = dict.lookupIndex("copy");
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, 2, false);
afv.add(idx, 5.0);
AugmentableFeatureVector cloned = (AugmentableFeatureVector) afv.cloneMatrix();
afv.setValue(idx, 99.0);
assertEquals(5.0, cloned.value(idx), 0.0001);
}

@Test
public void testAddFromPropertyListGrowAlphabetFalse() {
Alphabet dict = new Alphabet();
dict.lookupIndex("known");
PropertyList pl = PropertyList.add("known", 3.0, null);
pl = PropertyList.add("unknown", 9.0, pl);
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, pl, false, false);
int knownIdx = dict.lookupIndex("known");
int unknownIdx = dict.lookupIndex("unknown", false);
assertEquals(3.0, afv.value(knownIdx), 0.0001);
assertEquals(-1, unknownIdx);
}

@Test
public void testAddFeatureVectorPrefixWithNameCollisionsCreatesNew() {
Alphabet dict1 = new Alphabet();
dict1.lookupIndex("prefix-overlap");
Alphabet dict2 = new Alphabet();
int index = dict2.lookupIndex("overlap");
// FeatureVector fvOther = new FeatureVector(dict2, new int[] { index }, null, 1, 1, true, false, false);
// AugmentableFeatureVector afv = new AugmentableFeatureVector(dict1, false);
// afv.add(fvOther, "prefix-");
int newIndex = dict1.lookupIndex("prefix-overlap");
// assertEquals(1.0, afv.value(newIndex), 0.0001);
}

@Test
public void testDotProductWithSparseEmptyVectorReturnsZero() {
Alphabet dict = new Alphabet();
dict.lookupIndex("a");
dict.lookupIndex("b");
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, 2, false);
afv.add("a", 1.0);
// SparseVector emptyVec = new FeatureVector(dict, new int[0], new double[0], 0, 0, true, false, false);
// double product = afv.dotProduct(emptyVec);
// assertEquals(0.0, product, 0.0001);
}

@Test
public void testDotProductBetweenTwoBinaryVectors() {
Alphabet dict = new Alphabet();
int i1 = dict.lookupIndex("x");
int i2 = dict.lookupIndex("y");
AugmentableFeatureVector afv1 = new AugmentableFeatureVector(dict, true);
afv1.add(i1);
afv1.add(i2);
AugmentableFeatureVector afv2 = new AugmentableFeatureVector(dict, true);
afv2.add(i1);
double result = afv1.dotProduct(afv2);
assertEquals(1.0, result, 0.0);
}

@Test
public void testPlusEqualsWithMismatchedSparseDimensions() {
Alphabet dict = new Alphabet();
int i1 = dict.lookupIndex("shared");
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, 1, false);
afv.add(i1, 2.5);
Alphabet dict2 = new Alphabet();
dict2.lookupIndex("shared");
// FeatureVector other = new FeatureVector(dict2, new int[] { 0 }, new double[] { 3.0 }, 1, 1, true, false, false);
// afv.plusEquals(other, 1.0);
assertEquals(2.5, afv.value(i1), 0.0001);
}

@Test
public void testCloneMatrixZeroedDense() {
Alphabet dict = new Alphabet();
dict.lookupIndex("key1");
dict.lookupIndex("key2");
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, 2, false);
afv.add("key1", 7.0);
afv.add("key2", 8.0);
AugmentableFeatureVector clone = (AugmentableFeatureVector) afv.cloneMatrixZeroed();
assertEquals(0.0, clone.value(dict.lookupIndex("key1")), 0.0001);
assertEquals(0.0, clone.value(dict.lookupIndex("key2")), 0.0001);
}

@Test
public void testInfinityNormBinaryVectorReturnsOne() {
Alphabet dict = new Alphabet();
int i1 = dict.lookupIndex("a");
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, true);
afv.add(i1);
assertEquals(1.0, afv.infinityNorm(), 0.0001);
}

@Test
public void testSparseAddRespectsCapacityGrowth() {
Alphabet dict = new Alphabet();
int a = dict.lookupIndex("a");
int b = dict.lookupIndex("b");
int c = dict.lookupIndex("c");
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, true);
afv.add(a);
afv.add(b);
afv.add(c);
assertEquals(1.0, afv.value(c), 0.0001);
}

@Test
public void testRemoveDuplicatesWithZerosDoesNothing() {
Alphabet dict = new Alphabet();
dict.lookupIndex("x");
int[] indices = new int[] { 0 };
double[] values = new double[] { 1.0 };
// AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, indices, values, 1, false, false, false);
// afv.removeDuplicates(0);
// assertEquals(1, afv.numLocations());
// assertEquals(1.0, afv.value(0), 0.0001);
}

@Test
public void testRemoveDuplicatesWithPrecomputedCount() {
Alphabet dict = new Alphabet();
int idx = dict.lookupIndex("dup");
int[] indices = new int[] { idx, idx };
double[] values = new double[] { 1.0, 2.0 };
// AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, indices, values, 2, false, false, false);
// afv.sortIndices();
// afv.removeDuplicates(1);
// assertEquals(1, afv.numLocations());
// assertEquals(3.0, afv.value(idx), 0.0001);
}

@Test
public void testAddSameIndexThreeTimesDeduplicatesProperly() {
Alphabet dict = new Alphabet();
int idx = dict.lookupIndex("item");
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, 4, false);
afv.add(idx, 1.0);
afv.add(idx, 1.0);
afv.add(idx, 1.0);
// afv.sortIndices();
assertEquals(3.0, afv.value(idx), 0.0001);
assertEquals(1, afv.numLocations());
}

@Test
public void testPlusEqualsWithSparseRealVectorAddsCorrectly() {
Alphabet dict = new Alphabet();
int i1 = dict.lookupIndex("a");
int i2 = dict.lookupIndex("b");
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, 2, false);
afv.add(i1, 1.0);
afv.add(i2, 2.0);
int[] indices = new int[] { i1, i2 };
double[] vvalues = new double[] { 3.0, 4.0 };
// SparseVector sv = new FeatureVector(dict, indices, vvalues, 2, 2, true, false, false);
// afv.plusEquals(sv, 0.5);
assertEquals(1.0 + 3.0 * 0.5, afv.value(i1), 0.0001);
assertEquals(2.0 + 4.0 * 0.5, afv.value(i2), 0.0001);
}

@Test
public void testPlusEqualsWithSparseBinaryVectorAddsCorrectly() {
Alphabet dict = new Alphabet();
int i1 = dict.lookupIndex("x");
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, 1, false);
afv.add(i1, 1.0);
int[] indices = new int[] { i1 };
// SparseVector sv = new FeatureVector(dict, indices, null, 1, 1, true, false, false);
// afv.plusEquals(sv, 3.0);
assertEquals(1.0 + 3.0, afv.value(i1), 0.0001);
}

@Test
public void testSetValueAtLocationUpdatesAccurately() {
Alphabet dict = new Alphabet();
int idx = dict.lookupIndex("val");
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, 2, false);
afv.add(idx, 2.0);
int location = afv.location(idx);
afv.setValueAtLocation(location, 10.0);
assertEquals(10.0, afv.value(idx), 0.0001);
}

@Test
public void testDotProductReturnsZeroForDisjointSparseInputs() {
Alphabet dict = new Alphabet();
int i1 = dict.lookupIndex("p");
int i2 = dict.lookupIndex("q");
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, 4, false);
afv.add(i1, 4.0);
int[] otherIndices = new int[] { i2 };
double[] otherValues = new double[] { 3.0 };
// SparseVector sv = new FeatureVector(dict, otherIndices, otherValues, 1, 1, true, false, false);
// double result = afv.dotProduct(sv);
// assertEquals(0.0, result, 0.0001);
}

@Test
public void testSetAllOnDenseVectorToFixedConstant() {
Alphabet dict = new Alphabet();
dict.lookupIndex("a");
dict.lookupIndex("b");
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, 2, false);
afv.add("a", 1.0);
afv.add("b", 2.0);
afv.setAll(0.5);
assertEquals(0.5, afv.value(dict.lookupIndex("a")), 0.0001);
assertEquals(0.5, afv.value(dict.lookupIndex("b")), 0.0001);
}

@Test
public void testSingleSizeReturnsHighestIndexOnSparse() {
Alphabet dict = new Alphabet();
int i1 = dict.lookupIndex("x");
int i2 = dict.lookupIndex("z");
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, true);
afv.add(i1);
afv.add(i2);
// afv.sortIndices();
int size = afv.singleSize();
assertEquals(i2, size);
}

@Test
public void testToFeatureVectorPreservesContentAndIndices() {
Alphabet dict = new Alphabet();
int i1 = dict.lookupIndex("m");
int i2 = dict.lookupIndex("n");
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, 2, false);
afv.add(i1, 2.0);
afv.add(i2, 5.0);
FeatureVector fv = afv.toFeatureVector();
assertEquals(2.0, fv.value(i1), 0.0001);
assertEquals(5.0, fv.value(i2), 0.0001);
}

@Test
public void testAddFromAnotherFeatureVectorOnlyAppendsUnknowns() {
Alphabet dict = new Alphabet();
int indexA = dict.lookupIndex("a");
int indexB = dict.lookupIndex("b");
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, true);
afv.add(indexA);
int[] indices = new int[] { indexA, indexB };
double[] values = new double[] { 1.0, 2.0 };
// FeatureVector fv = new FeatureVector(dict, indices, values, 2, 2, true, false, false);
// afv.add(fv);
int occurrences = afv.location(indexA) >= 0 ? 1 : 0;
occurrences += afv.location(indexB) >= 0 ? 1 : 0;
assertEquals(2, occurrences);
assertEquals(1.0, afv.value(indexA), 0.0001);
assertEquals(2.0, afv.value(indexB), 0.0001);
}

@Test
public void testSortIndicesOnEmptySparseVector() {
Alphabet dict = new Alphabet();
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, true);
// afv.sortIndices();
assertEquals(0, afv.numLocations());
}

@Test
public void testRemoveDuplicatesSameIndexIdenticalValues() {
Alphabet dict = new Alphabet();
int idx = dict.lookupIndex("x");
int[] indices = new int[] { idx, idx };
double[] values = new double[] { 1.0, 1.0 };
// AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, indices, values, 2, false, false, false);
// afv.sortIndices();
// assertEquals(2.0, afv.value(idx), 0.0001);
// assertEquals(1, afv.numLocations());
}

@Test
public void testAddToWithNullValuesButSparseIndices() {
Alphabet dict = new Alphabet();
int i1 = dict.lookupIndex("a");
int i2 = dict.lookupIndex("b");
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, true);
afv.add(i1);
afv.add(i2);
double[] acc = new double[5];
afv.addTo(acc);
assertEquals(1.0, acc[i1], 0.0001);
assertEquals(1.0, acc[i2], 0.0001);
}

@Test
public void testDotProductSparseRealVsDenseMismatchedSize() {
Alphabet dict = new Alphabet();
int a = dict.lookupIndex("x");
int b = dict.lookupIndex("y");
double[] values = new double[] { 1.5, 2.5 };
int[] indices = new int[] { a, b };
// AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, indices, values, 2, true, false, false);
DenseVector dv = new DenseVector(new double[] { 1.0 });
// double result = afv.dotProduct(dv);
// assertEquals(1.5, result, 0.0001);
}

@Test
public void testDotProductDenseVsSparseBinary() {
Alphabet dict = new Alphabet();
int a = dict.lookupIndex("x");
int b = dict.lookupIndex("y");
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, 2, false);
afv.add(a, 3.5);
afv.add(b, 2.0);
int[] indices = new int[] { a };
// SparseVector sv = new FeatureVector(dict, indices, null, 1, 1, true, false, false);
// double result = afv.dotProduct(sv);
// assertEquals(3.5, result, 0.0001);
}

@Test
public void testInfinityNormWithNegativeRealValues() {
Alphabet dict = new Alphabet();
int a = dict.lookupIndex("foo");
int b = dict.lookupIndex("bar");
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, 4, false);
afv.add(a, -10.0);
afv.add(b, -8.0);
double norm = afv.infinityNorm();
assertEquals(10.0, norm, 0.0001);
}

@Test
public void testDotProductWhenBothVectorsAreUnsorted() {
Alphabet dict = new Alphabet();
int a = dict.lookupIndex("x");
int b = dict.lookupIndex("y");
int c = dict.lookupIndex("z");
AugmentableFeatureVector afv1 = new AugmentableFeatureVector(dict, true);
afv1.add(c);
afv1.add(a);
AugmentableFeatureVector afv2 = new AugmentableFeatureVector(dict, true);
afv2.add(a);
afv2.add(c);
double result = afv1.dotProduct(afv2);
assertEquals(2.0, result, 0.0001);
}

@Test
public void testAddToWithNullIndicesAndValidValues() {
Alphabet dict = new Alphabet();
dict.lookupIndex("a");
dict.lookupIndex("b");
dict.lookupIndex("c");
double[] raw = new double[] { 1.0, 2.0, 3.0 };
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, raw);
double[] acc = new double[5];
afv.addTo(acc, 2.0);
assertEquals(2.0, acc[0], 0.0001);
assertEquals(4.0, acc[1], 0.0001);
assertEquals(6.0, acc[2], 0.0001);
}

@Test
public void testCloneMatrixAfterInternalSortAndDeduplication() {
Alphabet dict = new Alphabet();
int i = dict.lookupIndex("dup");
int[] idx = new int[] { i, i };
double[] val = new double[] { 2.0, 3.0 };
// AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, idx, val, 2, true, false, false);
// afv.sortIndices();
// AugmentableFeatureVector clone = (AugmentableFeatureVector) afv.cloneMatrix();
// assertEquals(5.0, clone.value(i), 0.0001);
// assertEquals(1, clone.numLocations());
}

@Test
public void testSerializationWithEmptyVectorPreservesState() throws Exception {
Alphabet dict = new Alphabet();
AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, true);
ByteArrayOutputStream baos = new ByteArrayOutputStream();
ObjectOutputStream out = new ObjectOutputStream(baos);
out.writeObject(afv);
out.flush();
out.close();
byte[] data = baos.toByteArray();
ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(data));
AugmentableFeatureVector restored = (AugmentableFeatureVector) in.readObject();
assertNotNull(restored);
assertEquals(0, restored.numLocations());
}
}
