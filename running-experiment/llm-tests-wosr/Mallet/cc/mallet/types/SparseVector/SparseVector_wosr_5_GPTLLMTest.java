public class SparseVector_wosr_5_GPTLLMTest { 

 @Test
    public void testSparseVectorConstructionWithIndicesAndValues() {
        int[] indices = {1, 3, 5};
        double[] values = {2.5, 3.5, 4.5};

        SparseVector vector = new SparseVector(indices, values);

        assertEquals(3, vector.numLocations());
        assertEquals(2.5, vector.value(1), 0.0000001);
        assertEquals(3.5, vector.value(3), 0.0000001);
        assertEquals(4.5, vector.value(5), 0.0000001);
    }
@Test
    public void testSparseBinaryVectorConstruction() {
        int[] indices = {0, 2, 4};

        SparseVector vector = new SparseVector(indices);

        assertTrue(vector.isBinary());
        assertEquals(1.0, vector.value(0), 0.0);
        assertEquals(1.0, vector.value(2), 0.0);
        assertEquals(1.0, vector.value(4), 0.0);
        assertEquals(0.0, vector.value(1), 0.0);
    }
@Test
    public void testDenseVectorConstruction() {
        double[] values = {1.0, 2.0, 3.0};

        SparseVector vector = new SparseVector(values);

        assertFalse(vector.isBinary());
        assertEquals(3, vector.numLocations());
        assertEquals(1.0, vector.value(0), 0.0);
        assertEquals(2.0, vector.value(1), 0.0);
        assertEquals(3.0, vector.value(2), 0.0);
    }
@Test(expected = IllegalArgumentException.class)
    public void testMismatchedIndicesAndValuesConstructorThrowsException() {
        int[] indices = {1, 2};
        double[] values = {3.0};
        new SparseVector(indices, values);
    }
@Test
    public void testDotProductSparse() {
        int[] indicesA = {0, 2};
        double[] valuesA = {1.0, 3.0};
        SparseVector a = new SparseVector(indicesA, valuesA);

        int[] indicesB = {2, 3};
        double[] valuesB = {2.0, 4.0};
        SparseVector b = new SparseVector(indicesB, valuesB);

        double dot = a.dotProduct(b);

        assertEquals(6.0, dot, 0.0001);
    }
@Test
    public void testDotProductDense() {
        double[] dense = {1.0, 0.0, 2.0};
        SparseVector a = new SparseVector(dense);

        int[] indices = {0, 1, 2};
        double[] values = {2.0, 3.0, 4.0};
        SparseVector b = new SparseVector(indices, values);

        double result = a.dotProduct(b);
        assertEquals(1.0 * 2.0 + 0.0 * 3.0 + 2.0 * 4.0, result, 0.0001);
    }
@Test
    public void testTimesEquals() {
        double[] values = {2.0, 4.0};
        SparseVector vector = new SparseVector(values.clone());

        vector.timesEquals(2.5);

        assertEquals(5.0, vector.value(0), 0.0001);
        assertEquals(10.0, vector.value(1), 0.0001);
    }
@Test
    public void testPlusEqualsSparse() {
        int[] ind1 = {0, 1};
        double[] val1 = {1.0, 2.0};
        SparseVector v1 = new SparseVector(ind1, val1);

        int[] ind2 = {1, 2};
        double[] val2 = {3.0, 4.0};
        SparseVector v2 = new SparseVector(ind2, val2);

        v1.plusEqualsSparse(v2);

        assertEquals(1.0, v1.value(0), 0.0001);
        assertEquals(5.0, v1.value(1), 0.0001);
        assertEquals(0.0, v1.value(2), 0.0001);
    }
@Test
    public void testTimesEqualsSparse() {
        int[] ind1 = {0, 2};
        double[] val1 = {2.0, 3.0};
        SparseVector v1 = new SparseVector(ind1, val1);

        int[] ind2 = {2};
        double[] val2 = {4.0};
        SparseVector v2 = new SparseVector(ind2, val2);

        v1.timesEqualsSparse(v2);

        assertEquals(2.0, v1.value(0), 0.0001);
        assertEquals(12.0, v1.value(2), 0.0001);
    }
@Test
    public void testValueFromArrayIndex() {
        int[] ind = {1, 4};
        double[] val = {10.0, 20.0};
        SparseVector vector = new SparseVector(ind, val);

        assertEquals(10.0, vector.value(1), 0.0001);
        assertEquals(0.0, vector.value(2), 0.0001);
    }
@Test
    public void testCloneMatrix() {
        int[] ind = {2, 5};
        double[] val = {3.0, 7.0};
        SparseVector original = new SparseVector(ind, val);

        SparseVector clone = (SparseVector) original.cloneMatrix();

        assertNotSame(original, clone);
        assertArrayEquals(original.getIndices(), clone.getIndices());
        assertArrayEquals(original.getValues(), clone.getValues(), 0.0001);
    }
@Test
    public void testCloneMatrixZeroed() {
        int[] ind = {2, 5};
        double[] val = {3.0, 7.0};
        SparseVector vector = new SparseVector(ind, val);

        SparseVector zeroed = (SparseVector) vector.cloneMatrixZeroed();

        assertEquals(0.0, zeroed.value(2), 0.0001);
        assertEquals(0.0, zeroed.value(5), 0.0001);
    }
@Test
    public void testSetAllValues() {
        int[] ind = {0, 1};
        double[] val = {1.0, 2.0};
        SparseVector vector = new SparseVector(ind, val);

        vector.setAll(5.0);

        assertEquals(5.0, vector.value(0), 0.0001);
        assertEquals(5.0, vector.value(1), 0.0001);
    }
@Test(expected = IllegalArgumentException.class)
    public void testIncrementValueInvalidIndexThrowsException() {
        int[] ind = {1, 3};
        double[] val = {1.0, 2.0};
        SparseVector vector = new SparseVector(ind, val);
        vector.incrementValue(2, 5.0);
    }
@Test
    public void testIncrementValueValid() {
        int[] ind = {1, 3};
        double[] val = {1.0, 2.0};
        SparseVector vector = new SparseVector(ind, val);

        vector.incrementValue(3, 3.0);

        assertEquals(5.0, vector.value(3), 0.0001);
    }
@Test(expected = UnsupportedOperationException.class)
    public void testMapOnBinaryVectorThrowsException() throws Throwable {
        int[] ind = {0, 1};
        SparseVector vector = new SparseVector(ind);

        Method method = DummyMath.class.getMethod("square", Double.class);
        vector.map(method);
    }
@Test
    public void testMapOnNonBinaryVector() throws Throwable {
        int[] ind = {0, 1};
        double[] val = {2.0, 3.0};
        SparseVector vector = new SparseVector(ind, val);

        Method method = DummyMath.class.getMethod("square", Double.class);
        vector.map(method);

        assertEquals(4.0, vector.value(0), 0.0001);
        assertEquals(9.0, vector.value(1), 0.0001);
    } 
}