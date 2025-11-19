public class AugmentableFeatureVector_wosr_3_GPTLLMTest { 

 @Test
    public void testConstructorWithIndicesAndValues_CopyTrue() {
        Alphabet alphabet = new Alphabet();
        int indexA = alphabet.lookupIndex("featureA");
        int indexB = alphabet.lookupIndex("featureB");

        int[] indices = new int[] { indexA, indexB };
        double[] values = new double[] { 1.0, 2.0 };

        AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, indices, values, 2, true, false);

        assertEquals(2, afv.numLocations());
        assertEquals(1.0, afv.value(indexA), 0.0001);
        assertEquals(2.0, afv.value(indexB), 0.0001);
    }
@Test
    public void testAddByIndexToRealValuedVector() {
        Alphabet alphabet = new Alphabet();
        int index = alphabet.lookupIndex("featureX");

        int[] indices = new int[] { index };
        double[] values = new double[] { 1.0 };

        AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, indices, values, 2, true, false);

        assertEquals(1.0, afv.value(index), 0.0001);

        afv.add(index, 3.0);

        assertEquals(4.0, afv.value(index), 0.0001);
    }
@Test
    public void testAddIndexedWithAutoResize() {
        Alphabet alphabet = new Alphabet();
        int index = alphabet.lookupIndex("featureY");

        int[] indices = new int[0];
        double[] values = new double[0];

        AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, indices, values, 0, true, false);

        afv.add(index, 2.0);

        assertEquals(2.0, afv.value(index), 0.0001);
    }
@Test(expected = IllegalArgumentException.class)
    public void testAddNonBinaryToBinaryVectorThrowsException() {
        Alphabet alphabet = new Alphabet();
        int index = alphabet.lookupIndex("featureZ");

        AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, 10, true);

        afv.add(index, 0.5); 
    }
@Test(expected = IllegalArgumentException.class)
    public void testAddBinaryToNonBinaryVectorThrowsException() {
        Alphabet alphabet = new Alphabet();
        int index = alphabet.lookupIndex("featureA");
        double[] values = new double[10];

        AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, values, 10);

        afv.add(index); 
    }
@Test
    public void testAddByKey() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("feature1");

        AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, 10, false);
        afv.add("feature1", 1.0);

        int idx = alphabet.lookupIndex("feature1");
        assertEquals(1.0, afv.value(idx), 0.0001);
    }
@Test
    public void testSetAll() {
        Alphabet alphabet = new Alphabet();
        double[] values = new double[] { 2.0, 3.0, 4.0 };

        AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, values, 3);

        afv.setAll(1.5);

        assertEquals(1.5, afv.valueAtLocation(0), 0.0001);
        assertEquals(1.5, afv.valueAtLocation(1), 0.0001);
        assertEquals(1.5, afv.valueAtLocation(2), 0.0001);
    }
@Test
    public void testAddFeatureVectorBinary() {
        Alphabet alphabet = new Alphabet();
        int indexA = alphabet.lookupIndex("featA");
        int indexB = alphabet.lookupIndex("featB");

        int[] indices1 = new int[] { indexA };
        double[] values1 = new double[] { 1.0 };

        int[] indices2 = new int[] { indexB };
        double[] values2 = new double[] { 2.0 };

        FeatureVector fv = new FeatureVector(alphabet, indices2, values2);
        AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, indices1, values1, 2);

        afv.add(fv);

        assertEquals(1.0, afv.value(indexA), 0.0001);
        assertEquals(2.0, afv.value(indexB), 0.0001);
    }
@Test
    public void testAddWithPrefix() {
        Alphabet alphabet1 = new Alphabet();
        Alphabet alphabet2 = new Alphabet();
        int index1 = alphabet2.lookupIndex("word");

        int[] indices = new int[] { index1 };
        double[] values = new double[] { 5.0 };

        FeatureVector fv = new FeatureVector(alphabet2, indices, values);
        AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet1, 10, false);

        afv.add(fv, "prefix_");

        int newIndex = alphabet1.lookupIndex("prefix_word");
        assertTrue(newIndex >= 0);
        assertEquals(1.0, afv.value(newIndex), 0.0001);
    }
@Test
    public void testCloneMatrix() {
        Alphabet alphabet = new Alphabet();
        int index = alphabet.lookupIndex("test");
        int[] indices = new int[] { index };
        double[] values = new double[] { 3.5 };

        AugmentableFeatureVector original = new AugmentableFeatureVector(alphabet, indices, values, 1);
        ConstantMatrix clone = original.cloneMatrix();

        assertTrue(clone instanceof AugmentableFeatureVector);
        assertEquals(3.5, ((AugmentableFeatureVector) clone).value(index), 0.0001);
    }
@Test
    public void testCloneMatrixZeroed() {
        Alphabet alphabet = new Alphabet();
        int index = alphabet.lookupIndex("test");
        int[] indices = new int[] { index };
        double[] values = new double[] { 3.5 };

        AugmentableFeatureVector original = new AugmentableFeatureVector(alphabet, indices, values, 1);
        ConstantMatrix clone = original.cloneMatrixZeroed();

        assertTrue(clone instanceof AugmentableFeatureVector);
        assertEquals(0.0, ((AugmentableFeatureVector) clone).value(index), 0.0001);
    }
@Test
    public void testDotProductWithDenseVector() {
        Alphabet alphabet = new Alphabet();
        int indexX = alphabet.lookupIndex("x");
        int indexY = alphabet.lookupIndex("y");

        int[] indices = new int[] { indexX, indexY };
        double[] values = new double[] { 1.0, 2.0 };

        AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, indices, values, 2);

        double[] dv = new double[10];
        dv[indexX] = 1.0;
        dv[indexY] = 3.0;

        DenseVector dense = new DenseVector(dv);

        double dot = afv.dotProduct(dense);
        assertEquals(1.0 * 1.0 + 2.0 * 3.0, dot, 0.0001);
    }
@Test
    public void testDotProductWithSparseVector() {
        Alphabet alphabet = new Alphabet();
        int indexA = alphabet.lookupIndex("A");
        int indexB = alphabet.lookupIndex("B");

        int[] indices = new int[] { indexA, indexB };
        double[] values = new double[] { 2.0, 4.0 };

        AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, indices, values, 2);

        int[] sIndices = new int[] { indexA, indexB };
        double[] sValues = new double[] { 3.0, 0.5 };

        SparseVector sv = new SparseVector(sIndices, sValues, 2, 2, true, true, true);

        double dot = afv.dotProduct(sv);

        assertEquals((2.0 * 3.0) + (4.0 * 0.5), dot, 0.0001);
    }
@Test
    public void testPlusEqualsWithSameIndices() {
        Alphabet alphabet = new Alphabet();
        int index = alphabet.lookupIndex("feature");

        int[] indices = new int[] { index };
        double[] values1 = new double[] { 2.0 };
        double[] values2 = new double[] { 3.0 };

        AugmentableFeatureVector v1 = new AugmentableFeatureVector(alphabet, indices, values1, 1);
        AugmentableFeatureVector v2 = new AugmentableFeatureVector(alphabet, indices, values2, 1);

        v1.plusEquals(v2, 1.0);

        assertEquals(5.0, v1.value(index), 0.0001);
    }
@Test
    public void testOneNormAndTwoNorm() {
        Alphabet alphabet = new Alphabet();
        int index1 = alphabet.lookupIndex("f1");
        int index2 = alphabet.lookupIndex("f2");

        int[] indices = new int[] { index1, index2 };
        double[] values = new double[] { 3.0, 4.0 };

        AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, indices, values, 2);

        assertEquals(7.0, afv.oneNorm(), 0.0001);
        assertEquals(5.0, afv.twoNorm(), 0.0001);
    }
@Test
    public void testInfinityNorm() {
        Alphabet alphabet = new Alphabet();
        int index1 = alphabet.lookupIndex("f1");
        int index2 = alphabet.lookupIndex("f2");

        int[] indices = new int[] { index1, index2 };
        double[] values = new double[] { -2.0, 5.0 };

        AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, indices, values, 2);
        assertEquals(5.0, afv.infinityNorm(), 0.0001);
    }
@Test
    public void testToSparseVectorProperties() {
        Alphabet alphabet = new Alphabet();
        int idx1 = alphabet.lookupIndex("x");
        int[] indices = new int[] { idx1 };
        double[] values = new double[] { 10.0 };

        AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, indices, values, 1);

        SparseVector sv = afv.toSparseVector();

        assertEquals(1, sv.numLocations());
        assertEquals(idx1, sv.indexAtLocation(0));
        assertEquals(10.0, sv.valueAtLocation(0), 0.0001);
    } 
}