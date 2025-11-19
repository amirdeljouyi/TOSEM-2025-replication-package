public class SparseVector_wosr_3_GPTLLMTest { 

 @Test
    public void testDenseConstructorAndValueRetrieval() {
        double[] values = new double[] {1.0, 2.0, 3.0};
        SparseVector vector = new SparseVector(values);
        assertEquals(1.0, vector.value(0), 1e-10);
        assertEquals(2.0, vector.value(1), 1e-10);
        assertEquals(3.0, vector.value(2), 1e-10);
        assertEquals(0.0, vector.value(3), 1e-10); 
    }
@Test
    public void testSparseConstructorAndValueRetrieval() {
        int[] indices = new int[] {2, 4};
        double[] values = new double[] {1.5, 2.5};
        SparseVector vector = new SparseVector(indices, values);
        assertEquals(0.0, vector.value(0), 1e-10);
        assertEquals(1.5, vector.value(2), 1e-10);
        assertEquals(2.5, vector.value(4), 1e-10);
    }
@Test(expected = IllegalArgumentException.class)
    public void testMismatchedConstructorThrowsException() {
        int[] indices = new int[] {0, 1};
        double[] values = new double[] {1.0}; 
        new SparseVector(indices, values, true, true, true);
    }
@Test
    public void testBinaryConstructor() {
        int[] indices = new int[] {1, 3, 5};
        SparseVector vector = new SparseVector(indices, true, true, true);
        assertTrue(vector.isBinary());
        assertEquals(1.0, vector.value(1), 1e-10);
        assertEquals(0.0, vector.value(0), 1e-10);
    }
@Test
    public void testCloneMatrix() {
        double[] values = new double[] {1.0, 2.0};
        SparseVector original = new SparseVector(values);
        ConstantMatrix clone = original.cloneMatrix();
        assertTrue(clone instanceof SparseVector);
        SparseVector clonedVector = (SparseVector)clone;
        assertArrayEquals(values, clonedVector.getValues(), 1e-10);
    }
@Test
    public void testCloneMatrixZeroed() {
        double[] values = new double[] {1.5, 3.5};
        SparseVector original = new SparseVector(values);
        ConstantMatrix zeroed = original.cloneMatrixZeroed();
        assertTrue(zeroed instanceof SparseVector);
        SparseVector zeroVec = (SparseVector) zeroed;
        assertArrayEquals(new double[] {0.0, 0.0}, zeroVec.getValues(), 1e-10);
    }
@Test
    public void testAddToDense() {
        double[] vectorValues = new double[] {1.0, 2.0};
        double[] accumulator = new double[] {0.5, 0.5, 0.5};
        SparseVector vector = new SparseVector(vectorValues);
        vector.addTo(accumulator);
        assertArrayEquals(new double[] {1.5, 2.5, 0.5}, accumulator, 1e-10);
    }
@Test
    public void testAddToSparseBinary() {
        int[] indices = new int[] {1, 3};
        SparseVector sv = new SparseVector(indices);
        double[] accumulator = new double[] {0.0, 1.0, 0.0, 1.0};
        sv.addTo(accumulator, 2.0);
        assertArrayEquals(new double[] {0.0, 3.0, 0.0, 3.0}, accumulator, 1e-10);
    }
@Test
    public void testIncrementValueValid() {
        int[] indices = new int[] {1, 3};
        double[] values = new double[] {2.0, 4.0};
        SparseVector sv = new SparseVector(indices, values);
        sv.incrementValue(3, 1.0);
        assertEquals(5.0, sv.value(3), 1e-10);
    }
@Test(expected = IllegalArgumentException.class)
    public void testIncrementValueInvalidIndex() {
        int[] indices = new int[] {0, 2};
        double[] values = new double[] {1.0, 2.0};
        SparseVector sv = new SparseVector(indices, values);
        sv.incrementValue(1, 1.0);
    }
@Test
    public void testSetAll() {
        double[] values = new double[] {1.5, -2.0, 0.8};
        SparseVector vector = new SparseVector(values);
        vector.setAll(5.0);
        assertArrayEquals(new double[] {5.0, 5.0, 5.0}, vector.getValues(), 1e-10);
    }
@Test
    public void testDotProductWithDenseVector() {
        double[] vecAValues = new double[] {1.0, 2.0, 3.0};
        SparseVector vecA = new SparseVector(vecAValues);
        double[] vecBValues = new double[] {0.5, 1.0, 1.5};
        DenseVector vecB = new DenseVector(vecBValues);
        double result = vecA.dotProduct(vecB);
        assertEquals(8.0, result, 1e-10);
    }
@Test
    public void testDotProductWithSparseVector() {
        int[] aIndices = new int[] {0, 2};
        double[] aValues = new double[] {1.0, 3.0};
        SparseVector a = new SparseVector(aIndices, aValues);

        int[] bIndices = new int[] {0, 1, 2};
        double[] bValues = new double[] {2.0, 4.0, 5.0};
        SparseVector b = new SparseVector(bIndices, bValues);

        double result = a.dotProduct(b);
        assertEquals(1.0 * 2.0 + 3.0 * 5.0, result, 1e-10);
    }
@Test
    public void testInfinityNorm() {
        double[] values = new double[] {1.0, -4.0, 3.0};
        SparseVector vector = new SparseVector(values);
        double norm = vector.infinityNorm();
        assertEquals(4.0, norm, 1e-10);
    }
@Test
    public void testTwoNorm() {
        double[] values = new double[] {3.0, 4.0};
        SparseVector vector = new SparseVector(values);
        assertEquals(5.0, vector.twoNorm(), 1e-10);
    }
@Test
    public void testAbsNorm() {
        double[] values = new double[] {-2.0, 3.0, -4.0};
        SparseVector vector = new SparseVector(values);
        assertEquals(9.0, vector.absNorm(), 1e-10);
    }
@Test
    public void testTimesEquals() {
        double[] values = new double[] {2.0, 3.0};
        SparseVector vector = new SparseVector(values);
        vector.timesEquals(2.0);
        assertArrayEquals(new double[] {4.0, 6.0}, vector.getValues(), 1e-10);
    }
@Test
    public void testValueMethodWithNullIndices() {
        double[] values = new double[] {2.5, 7.5};
        SparseVector vector = new SparseVector(values);
        int[] indexArray = new int[] {1};
        double value = vector.value(indexArray);
        assertEquals(7.5, value, 1e-10);
    }
@Test
    public void testArrayCopyInto() {
        double[] values = new double[] {1.0, 2.0};
        SparseVector vector = new SparseVector(values);
        double[] target = new double[5];
        int result = vector.arrayCopyInto(target, 2);
        assertEquals(4, result);
        assertArrayEquals(new double[] {0.0, 0.0, 1.0, 2.0, 0.0}, target, 1e-10);
    }
@Test
    public void testArrayCopyFrom() {
        double[] values = new double[] {1.0, 1.0};
        SparseVector vector = new SparseVector(values);
        double[] source = new double[] {4.0, 5.0, 6.0};
        int result = vector.arrayCopyFrom(source, 1);
        assertEquals(3, result);
        assertArrayEquals(new double[] {5.0, 6.0}, vector.getValues(), 1e-10);
    }
@Test(expected = UnsupportedOperationException.class)
    public void testMapUnsupportedForBinary() throws Throwable {
        int[] indices = new int[] {1, 3};
        SparseVector vector = new SparseVector(indices);
        Method method = Math.class.getMethod("abs", double.class); 
        vector.map(method);
    }
@Test(expected = IllegalArgumentException.class)
    public void testSetValueInvalidIndexThrows() {
        int[] indices = new int[] {2, 4};
        double[] values = new double[] {1.0, 2.0};
        SparseVector vector = new SparseVector(indices, values);
        vector.setValue(3, 5.0);
    }
@Test
    public void testSortIndicesRemovesDuplicates() {
        int[] indices = new int[] {3, 1, 3};
        double[] values = new double[] {1.0, 2.0, 3.0};
        SparseVector vector = new SparseVector(indices, values, true, true, true);
        assertArrayEquals(new int[] {1, 3}, vector.getIndices());
        assertArrayEquals(new double[] {2.0, 4.0}, vector.getValues(), 1e-10);
    }
@Test
    public void testIsNaNReturnsTrue() {
        double[] values = new double[] {Double.NaN, 1.0};
        SparseVector vector = new SparseVector(values);
        assertTrue(vector.isNaN());
    }
@Test
    public void testIsInfiniteReturnsTrue() {
        double[] values = new double[] {1.0, Double.POSITIVE_INFINITY};
        SparseVector vector = new SparseVector(values);
        assertTrue(vector.isInfinite());
    }
@Test
    public void testToStringNonBinary() {
        double[] values = new double[] {1.0, 2.0};
        SparseVector vector = new SparseVector(values);
        String str = vector.toString(true);
        assertTrue(str.contains("0=1.0"));
        assertTrue(str.contains("1=2.0"));
    } 
}