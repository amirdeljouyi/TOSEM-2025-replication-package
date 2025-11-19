public class SparseVector_wosr_4_GPTLLMTest { 

 @Test
    public void testConstructorWithDenseValues() {
        double[] values = new double[]{1.0, 2.0, 3.0};
        SparseVector vector = new SparseVector(values);
        assertEquals(3, vector.numLocations());
        assertEquals(1.0, vector.value(0), 0.0001);
        assertEquals(2.0, vector.value(1), 0.0001);
        assertEquals(3.0, vector.value(2), 0.0001);
    }
@Test
    public void testConstructorWithIndicesAndValuesSparse() {
        int[] indices = new int[]{1, 3, 5};
        double[] values = new double[]{2.0, 4.0, 6.0};
        SparseVector vector = new SparseVector(indices, values);
        assertEquals(3, vector.numLocations());
        assertEquals(2.0, vector.value(1), 0.0001);
        assertEquals(4.0, vector.value(3), 0.0001);
        assertEquals(6.0, vector.value(5), 0.0001);
        assertEquals(0.0, vector.value(0), 0.0001);
    }
@Test
    public void testBinarySparseVector() {
        int[] indices = new int[]{0, 2, 4};
        SparseVector vector = new SparseVector(indices, true);
        assertTrue(vector.isBinary());
        assertEquals(1.0, vector.value(0), 0.0001);
        assertEquals(1.0, vector.value(2), 0.0001);
        assertEquals(0.0, vector.value(1), 0.0001);
    }
@Test
    public void testDotProductSparseWithDenseArray() {
        int[] indices = new int[]{0, 2};
        double[] values = new double[]{3.0, 4.0};
        double[] dense = new double[]{1.0, 0.0, 2.0};
        SparseVector vector = new SparseVector(indices, values);
        double result = vector.dotProduct(dense);
        assertEquals(3.0 * 1.0 + 4.0 * 2.0, result, 0.0001);
    }
@Test
    public void testCloneMatrix() {
        double[] values = new double[]{7.0, 8.0};
        SparseVector vector = new SparseVector(values);
        ConstantMatrix cloned = vector.cloneMatrix();
        assertTrue(cloned instanceof SparseVector);
        assertEquals(7.0, ((SparseVector) cloned).value(0), 0.0001);
    }
@Test
    public void testCloneMatrixZeroed() {
        double[] values = new double[]{1.0, 2.0, 3.0};
        SparseVector vector = new SparseVector(values);
        ConstantMatrix zeroed = vector.cloneMatrixZeroed();
        assertEquals(0.0, ((SparseVector) zeroed).value(0), 0.0001);
        assertEquals(0.0, ((SparseVector) zeroed).value(1), 0.0001);
        assertEquals(0.0, ((SparseVector) zeroed).value(2), 0.0001);
    }
@Test
    public void testPlusEqualsSparseSameIndices() {
        int[] indices = new int[]{1, 2};
        double[] values1 = new double[]{4.0, 5.0};
        double[] values2 = new double[]{1.0, 2.0};
        SparseVector a = new SparseVector(indices, values1.clone());
        SparseVector b = new SparseVector(indices, values2.clone());
        a.plusEqualsSparse(b);
        assertEquals(5.0, a.value(1), 0.0001);
        assertEquals(7.0, a.value(2), 0.0001);
    }
@Test
    public void testPlusEqualsSparseWithFactor() {
        int[] indices = new int[]{1};
        double[] values1 = new double[]{2.0};
        double[] values2 = new double[]{3.0};
        SparseVector a = new SparseVector(indices, values1.clone());
        SparseVector b = new SparseVector(indices, values2.clone());
        a.plusEqualsSparse(b, 2.0);
        assertEquals(2.0 + 3.0 * 2.0, a.value(1), 0.0001);
    }
@Test
    public void testTimesEqualsSparseOverlap() {
        int[] indices = new int[]{1};
        double[] aValues = new double[]{4.0};
        double[] bValues = new double[]{2.0};
        SparseVector a = new SparseVector(indices, aValues.clone());
        SparseVector b = new SparseVector(indices, bValues.clone());
        a.timesEqualsSparse(b);
        assertEquals(8.0, a.value(1), 0.0001);
    }
@Test
    public void testTimesEqualsSparseWithFactorAndNonOverlap() {
        int[] indicesA = new int[]{1};
        int[] indicesB = new int[]{2};
        double[] valuesA = new double[]{3.0};
        double[] valuesB = new double[]{5.0};
        SparseVector a = new SparseVector(indicesA, valuesA.clone());
        SparseVector b = new SparseVector(indicesB, valuesB.clone());
        a.timesEqualsSparse(b, 10.0);
        assertEquals(3.0, a.value(1), 0.0001); 
    }
@Test(expected = IllegalArgumentException.class)
    public void testSetValueExceptionOnMissingIndex() {
        int[] indices = new int[]{1, 3};
        double[] values = new double[]{1.0, 2.0};
        SparseVector vector = new SparseVector(indices, values);
        vector.setValue(2, 5.0); 
    }
@Test
    public void testSetValueAtLocation() {
        int[] indices = new int[]{1};
        double[] values = new double[]{9.0};
        SparseVector vector = new SparseVector(indices, values);
        vector.setValueAtLocation(0, 5.0);
        assertEquals(5.0, vector.value(1), 0.0001);
    }
@Test
    public void testArrayCopyIntoAndFrom() {
        double[] values = new double[]{1.0, 2.0, 3.0};
        SparseVector vector = new SparseVector(values);
        double[] target = new double[5];
        int next = vector.arrayCopyInto(target, 1);
        assertEquals(1.0, target[1], 0.0001);
        assertEquals(2.0, target[2], 0.0001);
        assertEquals(3.0, target[3], 0.0001);
        assertEquals(4, next);
        SparseVector newVector = new SparseVector(values.length, 0.0);
        newVector.arrayCopyFrom(target,1);
        assertEquals(1.0, newVector.value(0), 0.0001);
        assertEquals(2.0, newVector.value(1), 0.0001);
        assertEquals(3.0, newVector.value(2), 0.0001);
    }
@Test
    public void testMapSquareFunction() throws Throwable {
        double[] values = new double[]{2.0};
        SparseVector vector = new SparseVector(values);
        Method square = TestFunctions.class.getMethod("square", Double.class);
        vector.map(square);
        assertEquals(4.0, vector.value(0), 0.0001);
    }
@Test(expected = UnsupportedOperationException.class)
    public void testMapOnBinaryVectorThrows() throws Throwable {
        int[] indices = new int[]{0};
        SparseVector vector = new SparseVector(indices, true);
        Method square = TestFunctions.class.getMethod("square", Double.class);
        vector.map(square);
    }
@Test
    public void testNorms() {
        double[] values = new double[]{-3.0, 4.0};
        SparseVector vector = new SparseVector(values);
        assertEquals(1.0, vector.infinityNorm(), 4.0 - 3.0); 
        assertEquals(7.0, vector.oneNorm(), 0.0001);
        assertEquals(5.0, vector.twoNorm(), 0.0001);
        assertEquals(7.0, vector.absNorm(), 0.0001);
    }
@Test
    public void testIncrementValue() {
        int[] indices = new int[]{2};
        double[] values = new double[]{1.0};
        SparseVector vector = new SparseVector(indices, values.clone());
        vector.incrementValue(2, 3.0);
        assertEquals(4.0, vector.value(2), 0.0001);
    }
@Test(expected = IllegalArgumentException.class)
    public void testIncrementValueInvalidIndexThrows() {
        int[] indices = new int[]{1};
        double[] values = new double[]{2.0};
        SparseVector vector = new SparseVector(indices, values.clone());
        vector.incrementValue(0, 1.0);
    } 
}