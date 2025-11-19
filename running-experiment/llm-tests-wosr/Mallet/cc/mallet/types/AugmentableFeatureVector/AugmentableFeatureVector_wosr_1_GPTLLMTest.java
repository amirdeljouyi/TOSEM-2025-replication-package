public class AugmentableFeatureVector_wosr_1_GPTLLMTest { 

 @Test
    public void testConstructorWithValuesAndCapacity() {
        Alphabet alphabet = new Alphabet();
        double[] values = new double[] {1.0, 2.0};
        AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, values, 2);
        assertEquals(2, afv.numLocations());
        assertEquals(1.0, afv.valueAtLocation(0), 0.00001);
        assertEquals(2.0, afv.valueAtLocation(1), 0.00001);
    }
@Test
    public void testConstructorWithIndicesAndValues() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("a");
        alphabet.lookupIndex("b");
        int[] indices = new int[] {0, 1};
        double[] values = new double[] {0.5, 1.5};
        AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, indices, values, 2);
        assertEquals(2, afv.numLocations());
        assertEquals(0, afv.indexAtLocation(0));
        assertEquals(1, afv.indexAtLocation(1));
        assertEquals(0.5, afv.valueAtLocation(0), 0.00001);
        assertEquals(1.5, afv.valueAtLocation(1), 0.00001);
    }
@Test
    public void testAddIndexAndValueToSparseVector() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("a");
        alphabet.lookupIndex("b");
        AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, 2, false);
        afv.add(0, 1.0);
        afv.add(1, 2.0);
        assertEquals(2, afv.numLocations());
        assertEquals(1.0, afv.value(0), 0.00001);
        assertEquals(2.0, afv.value(1), 0.00001);
    }
@Test
    public void testAddObjectKey() {
        Alphabet alphabet = new Alphabet();
        int index = alphabet.lookupIndex("x");
        AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, 4, false);
        afv.add("x", 3.5);
        assertEquals(1, afv.numLocations());
        assertEquals(3.5, afv.value(index), 0.00001);
    }
@Test(expected = IllegalArgumentException.class)
    public void testAddBinaryFeatureToRealValuedThrows() {
        Alphabet alphabet = new Alphabet();
        AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, 4, false);
        afv.add(0);
    }
@Test
    public void testAddFeatureVectorWithPrefixBinary() {
        Alphabet dict1 = new Alphabet();
        dict1.lookupIndex("feat1");
        dict1.lookupIndex("feat2");
        int[] indices = new int[] {0, 1};
        AugmentableFeatureVector base = new AugmentableFeatureVector(dict1, indices, null, 2);

        Alphabet dict2 = new Alphabet();
        dict2.lookupIndex("featX");
        double[] values = new double[] {1.0};
        AugmentableFeatureVector target = new AugmentableFeatureVector(dict2, values);

        target.add(base, "pre_");
        assertEquals(2, target.numLocations());
    }
@Test
    public void testCloneMatrixCopiesCorrectly() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("a");
        alphabet.lookupIndex("b");
        AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, 2, false);
        afv.add(0, 1.0);
        afv.add(1, 2.0);

        ConstantMatrix clone = afv.cloneMatrix();
        assertTrue(clone instanceof AugmentableFeatureVector);
        assertEquals(2, ((AugmentableFeatureVector) clone).numLocations());
    }
@Test
    public void testCloneMatrixZeroed() {
        Alphabet alphabet = new Alphabet();
        double[] values = new double[] {4.5, 2.5};
        alphabet.lookupIndex("x");
        alphabet.lookupIndex("y");

        AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, values, 2);
        AugmentableFeatureVector zeroed = (AugmentableFeatureVector) afv.cloneMatrixZeroed();

        assertEquals(0.0, zeroed.value(0), 0.00001);
        assertEquals(0.0, zeroed.value(1), 0.00001);
        assertEquals(2, zeroed.numLocations());
    }
@Test
    public void testDotProductWithDenseVector_Sparse() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("x");
        alphabet.lookupIndex("y");

        AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, 2, false);
        afv.add(0, 2.0);
        afv.add(1, 3.0);

        double[] denseVals = new double[] {2.0, 1.0};
        DenseVector dv = new DenseVector(denseVals);
        double result = afv.dotProduct(dv);
        assertEquals(7.0, result, 0.00001);
    }
@Test
    public void testDotProductWithSparseVector() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("a");
        alphabet.lookupIndex("b");

        double[] afvVals = new double[] {1.0, 2.0};
        AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, afvVals, 2);

        int[] indices = new int[] {0, 1};
        double[] sVals = new double[] {2.0, 3.0};
        SparseVector sv = new SparseVector(indices, sVals, 2, 2, true, false, false);
        double dot = afv.dotProduct(sv);
        assertEquals(8.0, dot, 0.00001);
    }
@Test
    public void testSetValueAndGetValue() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("a");
        double[] values = new double[] {1.0};
        AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, values, 1);

        afv.setValue(0, 5.5);
        assertEquals(5.5, afv.value(0), 0.00001);
    }
@Test
    public void testAddFeatureVectorAndValues() {
        Alphabet alphabet = new Alphabet();
        int a = alphabet.lookupIndex("a");
        int b = alphabet.lookupIndex("b");

        AugmentableFeatureVector afv1 = new AugmentableFeatureVector(alphabet, 2, false);
        afv1.add(a, 1.0);
        afv1.add(b, 1.0);

        AugmentableFeatureVector afv2 = new AugmentableFeatureVector(alphabet, 2, false);
        afv2.add(a, 2.0);
        afv2.add(b, 3.0);

        afv1.plusEquals(afv2, 0.5);
        assertEquals(2.0, afv1.value(a), 0.00001);
        assertEquals(2.5, afv1.value(b), 0.00001);
    }
@Test
    public void testAddToAccumulator() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("a");
        alphabet.lookupIndex("b");

        AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, 2, false);
        afv.add(0, 2.0);
        afv.add(1, 3.0);

        double[] accumulator = new double[2];
        afv.addTo(accumulator, 2.0);

        assertEquals(4.0, accumulator[0], 0.00001);
        assertEquals(6.0, accumulator[1], 0.00001);
    }
@Test
    public void testNormCalculations() {
        Alphabet alphabet = new Alphabet();
        double[] values = new double[] {3.0, 4.0};
        AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, values, 2);

        assertEquals(7.0, afv.oneNorm(), 0.00001);
        assertEquals(5.0, afv.twoNorm(), 0.00001);
        assertEquals(4.0, afv.infinityNorm(), 0.00001);
    }
@Test
    public void testSetAll() {
        Alphabet alphabet = new Alphabet();
        double[] values = new double[] {1.1, 2.2};
        AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, values, 2);

        afv.setAll(0.5);
        assertEquals(0.5, afv.value(0), 0.00001);
        assertEquals(0.5, afv.value(1), 0.00001);
    }
@Test
    public void testToSparseVector() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("a");
        alphabet.lookupIndex("b");
        AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, 2, false);
        afv.add(0, 1.0);
        afv.add(1, 2.0);

        SparseVector sparse = afv.toSparseVector();
        assertEquals(1.0, sparse.value(0), 0.00001);
        assertEquals(2.0, sparse.value(1), 0.00001);
        assertEquals(2, sparse.numLocations());
    }
@Test
    public void testSingleSizeWhenDense() {
        Alphabet alphabet = new Alphabet();
        double[] values = new double[] {1.0, 4.0, 7.0};
        AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, values, 3);
        assertEquals(3, afv.singleSize());
    }
@Test
    public void testSingleSizeWhenSparse() {
        Alphabet alphabet = new Alphabet();
        int[] indices = new int[] {0, 2};
        double[] values = new double[] {3.0, 6.0};
        AugmentableFeatureVector afv = new AugmentableFeatureVector(alphabet, indices, values, 2);
        assertEquals(2, afv.singleSize());
    } 
}