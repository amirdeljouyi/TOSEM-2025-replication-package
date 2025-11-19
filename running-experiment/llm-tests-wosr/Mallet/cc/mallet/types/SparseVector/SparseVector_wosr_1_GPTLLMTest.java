public class SparseVector_wosr_1_GPTLLMTest { 

 @Test
    public void testDenseConstructor_CorrectValues() {
        double[] values = {1.0, 0.0, 2.0};
        SparseVector v = new SparseVector(values, true);
        assertEquals(3, v.numLocations());
        assertEquals(1.0, v.value(0), 1e-6);
        assertEquals(0.0, v.value(1), 1e-6);
        assertEquals(2.0, v.value(2), 1e-6);
    }
@Test
    public void testSparseConstructor_CorrectValues() {
        int[] indices = {2, 4, 7};
        double[] values = {1.0, 2.0, 3.0};
        SparseVector v = new SparseVector(indices, values, true);
        assertEquals(3, v.numLocations());
        assertEquals(1.0, v.value(2), 1e-6);
        assertEquals(0.0, v.value(3), 1e-6);
        assertEquals(2.0, v.value(4), 1e-6);
        assertEquals(3.0, v.value(7), 1e-6);
        assertEquals(-1, v.location(0));
    }
@Test(expected = IllegalArgumentException.class)
    public void testSparseConstructor_MismatchedLength() {
        int[] indices = {1, 2};
        double[] values = {1.0};
        new SparseVector(indices, values, true);
    }
@Test
    public void testBinaryConstructor() {
        int[] indices = {0, 2, 5};
        SparseVector v = new SparseVector(indices, true);
        assertTrue(v.isBinary());
        assertEquals(1.0, v.value(0), 1e-6);
        assertEquals(0.0, v.value(1), 1e-6);
        assertEquals(1.0, v.value(5), 1e-6);
    }
@Test
    public void testIncrementSparse() {
        int[] indices = {1, 3};
        double[] values = {1.0, 2.0};
        SparseVector v = new SparseVector(indices, values, true);
        v.incrementValue(3, 5.0);
        assertEquals(7.0, v.value(3), 1e-6);
    }
@Test(expected = IllegalArgumentException.class)
    public void testIncrementInvalidIndex() {
        int[] indices = {0, 4};
        double[] values = {1.0, 1.0};
        SparseVector v = new SparseVector(indices, values, true);
        v.incrementValue(2, 1.0);
    }
@Test
    public void testDotProduct_SameIndices() {
        int[] i1 = {1, 4};
        int[] i2 = {1, 4};
        double[] v1 = {2.0, 3.0};
        double[] v2 = {5.0, 7.0};
        SparseVector a = new SparseVector(i1, v1, true);
        SparseVector b = new SparseVector(i2, v2, true);
        double dot = a.dotProduct(b);
        assertEquals(2.0 * 5.0 + 3.0 * 7.0, dot, 1e-6);
    }
@Test
    public void testDotProduct_DenseAndSparse() {
        double[] data = {0.0, 1.0, 2.0, 3.0};
        DenseVector d = new DenseVector(data);
        int[] indices = {1, 2};
        double[] values = {2.0, 4.0};
        SparseVector v = new SparseVector(indices, values, true);
        double dot = v.dotProduct(d);
        assertEquals(2.0 * 1.0 + 4.0 * 2.0, dot, 1e-6);
    }
@Test
    public void testTimesEqualsSparse() {
        int[] indicesA = {1, 2};
        double[] valuesA = {3.0, 4.0};
        int[] indicesB = {1, 2};
        double[] valuesB = {5.0, 10.0};
        SparseVector a = new SparseVector(indicesA, valuesA, true);
        SparseVector b = new SparseVector(indicesB, valuesB, true);
        a.timesEqualsSparse(b, 0.5);
        assertEquals(3.0 * 5.0 * 0.5, a.value(1), 1e-6);
        assertEquals(4.0 * 10.0 * 0.5, a.value(2), 1e-6);
    }
@Test
    public void testSetValueAndGetValue() {
        int[] indices = {0, 3};
        double[] values = {2.0, 6.0};
        SparseVector v = new SparseVector(indices, values, true);
        v.setValue(3, 12.0);
        assertEquals(12.0, v.value(3), 1e-6);
    }
@Test(expected = IllegalArgumentException.class)
    public void testSetValueOnMissingIndex() {
        int[] indices = {1, 2};
        double[] values = {4.0, 5.0};
        SparseVector v = new SparseVector(indices, values, true);
        v.setValue(3, 1.0);
    }
@Test
    public void testCloneMatrixZeroed() {
        int[] indices = {2, 3};
        double[] values = {5.0, 7.0};
        SparseVector v = new SparseVector(indices, values, true);
        SparseVector clone = (SparseVector) v.cloneMatrixZeroed();
        for (int i = 0; i < clone.numLocations(); i++) {
            assertEquals(0.0, clone.valueAtLocation(i), 1e-6);
        }
        assertEquals(2, clone.numLocations());
    }
@Test
    public void testArrayCopyFromAndInto() {
        double[] values = {1.0, 2.0, 3.0};
        SparseVector v = new SparseVector(values, true);
        double[] copy = {10.0, 20.0, 30.0};
        v.arrayCopyFrom(copy);
        assertEquals(10.0, v.value(0), 1e-6);
        
        double[] result = new double[3];
        v.arrayCopyInto(result, 0);
        assertArrayEquals(copy, result, 1e-6);
    }
@Test
    public void testOneNorm_Binary() {
        int[] indices = {1, 2, 3};
        SparseVector v = new SparseVector(indices, true);
        assertEquals(3.0, v.oneNorm(), 1e-6);
    }
@Test
    public void testInfinityNorm() {
        double[] values = {1.0, -5.0, 3.0};
        SparseVector v = new SparseVector(values, true);
        assertEquals(5.0, v.infinityNorm(), 1e-6);
    }
@Test
    public void testToString() {
        int[] indices = {0, 2};
        double[] values = {1.0, 5.0};
        SparseVector v = new SparseVector(indices, values, true);
        String str = v.toString(true);
        assertTrue(str.contains("0=1.0"));
        assertTrue(str.contains("2=5.0"));
    }
@Test
    public void testMap() throws Throwable {
        double[] data = {2.0, 4.0};
        SparseVector v = new SparseVector(data, true);
        Method sqrt = Math.class.getMethod("sqrt", Double.TYPE);
        try {
            v.map(sqrt);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            
        }

        Method cube = SparseVectorTest.class.getMethod("cube", Double.class);
        v.map(cube);
        assertEquals(8.0, v.value(0), 1e-6);
        assertEquals(64.0, v.value(1), 1e-6);
    }
@Test(expected = UnsupportedOperationException.class)
    public void testMapBinaryVectorThrows() throws Throwable {
        int[] indices = {0, 1};
        SparseVector v = new SparseVector(indices, true);
        Method cube = SparseVectorTest.class.getMethod("cube", Double.class);
        v.map(cube);
    }
@Test
    public void testVectorAdd() {
        double[] values1 = {1.0, 2.0};
        double[] values2 = {5.0, 6.0};
        int[] indices1 = {1, 3};
        int[] indices2 = {1, 3};
        SparseVector a = new SparseVector(indices1, values1, true);
        SparseVector b = new SparseVector(indices2, values2, true);
        SparseVector sum = a.vectorAdd(b, 1.0);
        assertEquals(6.0, sum.value(1), 1e-6);
        assertEquals(8.0, sum.value(3), 1e-6);
    }
@Test
    public void testSetAll() {
        double[] values = {1.0, 2.5, -9.8};
        SparseVector v = new SparseVector(values, true);
        v.setAll(3.0);
        assertEquals(3.0, v.value(0), 1e-6);
        assertEquals(3.0, v.value(1), 1e-6);
        assertEquals(3.0, v.value(2), 1e-6);
    }
@Test
    public void testDotProductWithZeroOverlap() {
        int[] i1 = {1, 3};
        int[] i2 = {0, 2};
        double[] v1 = {1.0, 1.0};
        double[] v2 = {1.0, 1.0};
        SparseVector a = new SparseVector(i1, v1);
        SparseVector b = new SparseVector(i2, v2);
        assertEquals(0.0, a.dotProduct(b), 1e-6);
    }
@Test
    public void testIsNaNOrInfinite() {
        double[] values = {Double.POSITIVE_INFINITY, 2.0};
        SparseVector v = new SparseVector(values, true);
        assertTrue(v.isInfinite());
        assertTrue(v.isNaNOrInfinite());
    }
@Test
    public void testExtendedDotProductNaNHandling() {
        int[] indicesA = {0};
        int[] indicesB = {0};
        double[] valuesA = {Double.NEGATIVE_INFINITY};
        double[] valuesB = {0.0};
        SparseVector a = new SparseVector(indicesA, valuesA, true);
        SparseVector b = new SparseVector(indicesB, valuesB, true);
        double result = a.dotProduct(b);
        assertEquals(0.0, result, 1e-6); 
    } 
}