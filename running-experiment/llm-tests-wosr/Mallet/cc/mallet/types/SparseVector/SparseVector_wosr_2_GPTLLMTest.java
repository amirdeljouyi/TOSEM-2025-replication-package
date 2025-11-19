public class SparseVector_wosr_2_GPTLLMTest { 

 @Test
    public void testConstructSparseVectorWithIndicesAndValues() {
        int[] indices = {1, 3, 5};
        double[] values = {2.0, 4.0, 6.0};
        SparseVector vector = new SparseVector(indices, values);

        assertEquals(3, vector.numLocations());
        assertEquals(2.0, vector.value(1), 0.0001);
        assertEquals(4.0, vector.value(3), 0.0001);
        assertEquals(6.0, vector.value(5), 0.0001);
        assertEquals(0.0, vector.value(2), 0.0001);
    }
@Test
    public void testConstructDenseVector() {
        double[] values = {1.0, 2.0, 3.0};
        SparseVector vector = new SparseVector(values);

        assertEquals(3, vector.numLocations());
        assertEquals(1.0, vector.value(0), 0.0001);
        assertEquals(2.0, vector.value(1), 0.0001);
        assertEquals(3.0, vector.value(2), 0.0001);
    }
@Test
    public void testCreateBinarySparseVector() {
        int[] indices = {0, 2, 4};
        SparseVector vector = new SparseVector(indices);

        assertEquals(3, vector.numLocations());
        assertEquals(1.0, vector.value(0), 0.0001);
        assertEquals(1.0, vector.value(2), 0.0001);
        assertEquals(1.0, vector.value(4), 0.0001);
        assertEquals(0.0, vector.value(1), 0.0001);
    }
@Test
    public void testValueOutOfBoundsDenseVector() {
        double[] values = {1.0, 2.0};
        SparseVector vector = new SparseVector(values);
        assertEquals(0.0, vector.value(5), 0.0001);  
    }
@Test
    public void testSetValueInDenseVector() {
        double[] values = {1.0, 2.0, 3.0};
        SparseVector vector = new SparseVector(values, true);
        vector.setValue(1, 10.0);
        assertEquals(10.0, vector.value(1), 0.0001);
    }
@Test(expected = IllegalArgumentException.class)
    public void testSetValueInSparseVectorShouldThrow() {
        int[] indices = {2, 4};
        double[] values = {1.0, 1.0};
        SparseVector vector = new SparseVector(indices, values);
        vector.setValue(1, 5.0);
    }
@Test
    public void testDotProductWithDoubleArray() {
        int[] indices = {0, 3};
        double[] values = {1.0, 2.0};
        SparseVector vector = new SparseVector(indices, values);
        double[] array = {3.0, 0.0, 0.0, 4.0};
        double result = vector.dotProduct(array);
        assertEquals(11.0, result, 0.0001);
    }
@Test
    public void testDotProductWithDenseVector() {
        double[] vecValues = {1.0, 2.0, 0.0, 4.0};
        DenseVector dense = new DenseVector(vecValues);
        int[] indices = {1, 3};
        double[] values = {2.0, 2.0};
        SparseVector sparse = new SparseVector(indices, values);
        double result = sparse.dotProduct(dense);
        assertEquals(12.0, result, 0.0001);
    }
@Test
    public void testCloneMatrix() {
        int[] indices = {0, 3};
        double[] values = {1.0, 2.0};
        SparseVector original = new SparseVector(indices, values);
        SparseVector clone = (SparseVector) original.cloneMatrix();

        assertEquals(2, clone.numLocations());
        assertEquals(1.0, clone.value(0), 0.0001);
        assertEquals(2.0, clone.value(3), 0.0001);
    }
@Test
    public void testIncrementValue() {
        int[] indices = {1, 2};
        double[] values = {3.0, 4.0};
        SparseVector vector = new SparseVector(indices, values);
        vector.incrementValue(2, 1.0);
        assertEquals(5.0, vector.value(2), 0.0001);
    }
@Test(expected = IllegalArgumentException.class)
    public void testIncrementValueThrowsIfIndexAbsent() {
        int[] indices = {1, 3};
        double[] values = {1.0, 2.0};
        SparseVector vector = new SparseVector(indices, values);
        vector.incrementValue(2, 1.0); 
    }
@Test
    public void testTimesEquals() {
        int[] indices = {1, 2};
        double[] values = {2.0, 3.0};
        SparseVector vector = new SparseVector(indices, values);
        vector.timesEquals(2.0);
        assertEquals(4.0, vector.value(1), 0.0001);
        assertEquals(6.0, vector.value(2), 0.0001);
    }
@Test
    public void testVectorAddSparse() {
        int[] indices1 = {1, 2};
        double[] values1 = {1.0, 2.0};
        SparseVector v1 = new SparseVector(indices1, values1);

        int[] indices2 = {2, 3};
        double[] values2 = {3.0, 4.0};
        SparseVector v2 = new SparseVector(indices2, values2);

        SparseVector result = v1.vectorAdd(v2, 1.0);
        assertEquals(4, result.numLocations());
        assertEquals(1.0, result.value(1), 0.0001);
        assertEquals(5.0, result.value(2), 0.0001);
        assertEquals(4.0, result.value(3), 0.0001);
    }
@Test
    public void testOneNormAndTwoNormAndInfinityNorm() {
        int[] indices = {0, 1, 2};
        double[] values = {1.0, 2.0, 3.0};
        SparseVector vector = new SparseVector(indices, values);
        assertEquals(6.0, vector.oneNorm(), 0.0001);
        assertEquals(Math.sqrt(14.0), vector.twoNorm(), 0.0001);
        assertEquals(3.0, vector.infinityNorm(), 0.0001);
    }
@Test
    public void testMapAppliesMethodToValues() throws Exception {
        int[] indices = {0, 1};
        double[] values = {2.0, 3.0};
        SparseVector vector = new SparseVector(indices, values);

        Method sqrtMethod = Math.class.getMethod("sqrt", double.class);
        Method boxedMethod = DoubleUnaryWrapper.class.getMethod("sqrtDouble", Double.class);
        vector.map(boxedMethod);

        assertEquals(Math.sqrt(2.0), vector.value(0), 0.0001);
        assertEquals(Math.sqrt(3.0), vector.value(1), 0.0001);
    }
@Test(expected = UnsupportedOperationException.class)
    public void testMapOnBinaryVectorThrowsUnsupported() throws Exception {
        int[] indices = {1, 2};
        SparseVector vector = new SparseVector(indices);
        Method boxedMethod = DoubleUnaryWrapper.class.getMethod("sqrtDouble", Double.class);
        vector.map(boxedMethod);
    }
@Test(expected = IllegalArgumentException.class)
    public void testInvalidConstructorThrows() {
        int[] indices = {1, 2, 3};
        double[] values = {1.0, 2.0}; 
        new SparseVector(indices, values);
    }
@Test
    public void testCloneMatrixZeroed() {
        int[] indices = {0, 2};
        double[] values = {5.0, 8.0};
        SparseVector vector = new SparseVector(indices, values);
        SparseVector zeroed = (SparseVector) vector.cloneMatrixZeroed();
        assertEquals(0.0, zeroed.value(0), 0.0001);
        assertEquals(0.0, zeroed.value(2), 0.0001);
    } 
}