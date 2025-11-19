public class AugmentableFeatureVector_wosr_2_GPTLLMTest { 

 @Test
    public void testConstructorWithDenseValues() {
        Alphabet dict = new Alphabet();
        dict.lookupIndex("feature1");
        dict.lookupIndex("feature2");

        double[] values = new double[] {0.5, 1.5};
        AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, values);

        assertEquals(2, afv.numLocations());
        assertEquals(0.5, afv.valueAtLocation(0), 0.001);
        assertEquals(1.5, afv.valueAtLocation(1), 0.001);
    }
@Test
    public void testConstructorWithBinaryFlagTrue() {
        Alphabet dict = new Alphabet();
        dict.lookupIndex("f1");
        dict.lookupIndex("f2");

        AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, 2, true);

        assertEquals(0, afv.numLocations());
        assertEquals(0.0, afv.value(0), 0.001);
    }
@Test
    public void testAddIntDoubleDense() {
        Alphabet dict = new Alphabet();
        dict.lookupIndex("feat");

        double[] values = new double[5];
        AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, values);

        afv.add(0, 3.0);
        afv.add(1, 2.0);

        assertEquals(2, afv.numLocations());
        assertEquals(3.0, afv.value(0), 0.001);
        assertEquals(2.0, afv.value(1), 0.001);
    }
@Test
    public void testAddIntDoubleSparse() {
        Alphabet dict = new Alphabet();
        dict.lookupIndex("f1");
        dict.lookupIndex("f2");

        int[] indices = new int[0];
        double[] values = new double[0];

        AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, indices, values, 0);

        afv.add(1, 2.5);
        afv.add(0, 1.5);

        afv.sortIndices();

        assertEquals(2, afv.numLocations());
        assertEquals(1, afv.indexAtLocation(0));
        assertEquals(0, afv.indexAtLocation(1));
    }
@Test(expected = IllegalArgumentException.class)
    public void testAddIllegalValueToBinaryVector() {
        Alphabet dict = new Alphabet();
        dict.lookupIndex("feat");

        AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, 5, true);

        afv.add(0, 0.5);
    }
@Test
    public void testAddFeatureVector() {
        Alphabet dict = new Alphabet();
        dict.lookupIndex("a");
        dict.lookupIndex("b");

        double[] values = new double[] {1.0, 2.0};
        AugmentableFeatureVector base = new AugmentableFeatureVector(dict, values);

        AugmentableFeatureVector toAdd = new AugmentableFeatureVector(dict, 5, true);
        toAdd.add(0);
        toAdd.add(1);

        base.add(toAdd);

        assertEquals(2, base.numLocations());
        assertEquals(2.0, base.value(0), 0.001);
        assertEquals(3.0, base.value(1), 0.001);
    }
@Test
    public void testAddObject() {
        Alphabet dict = new Alphabet();
        dict.lookupIndex("term");

        AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, 5, false);
        afv.add("term", 4.4);

        int index = dict.lookupIndex("term", false);
        assertEquals(4.4, afv.value(index), 0.001);
    }
@Test
    public void testValueAndLocationMethodsSparse() {
        Alphabet dict = new Alphabet();
        dict.lookupIndex("a");
        dict.lookupIndex("b");

        int[] indices = new int[] {1, 0};
        double[] values = new double[] {1.0, 2.0};

        AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, indices, values, 2);

        afv.sortIndices();

        assertEquals(0, afv.location(0));
        assertEquals(1, afv.location(1));
        assertEquals(2.0, afv.value(0), 0.001);
        assertEquals(1.0, afv.value(1), 0.001);
    }
@Test
    public void testDotProductWithDenseVector() {
        Alphabet dict = new Alphabet();
        dict.lookupIndex("x");
        dict.lookupIndex("y");

        double[] values = new double[] {2.0, 3.0};
        AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, values);

        double[] dense = new double[] {4.0, 5.0};
        DenseVector dv = new DenseVector(dense);

        double result = afv.dotProduct(dv);
        assertEquals(2.0 * 4.0 + 3.0 * 5.0, result, 0.001);
    }
@Test
    public void testAddToDenseArray() {
        Alphabet dict = new Alphabet();
        dict.lookupIndex("x");
        dict.lookupIndex("y");

        double[] values = new double[] {1.0, 2.0};
        AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, values);

        double[] accumulator = new double[] {1.0, 1.0};

        afv.addTo(accumulator, 2.0);

        assertEquals(1.0 + 1.0 * 2.0, accumulator[0], 0.001);
        assertEquals(1.0 + 2.0 * 2.0, accumulator[1], 0.001);
    }
@Test
    public void testSetValueMethods() {
        Alphabet dict = new Alphabet();
        dict.lookupIndex("a");

        double[] values = new double[] {3.0};
        AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, values);

        afv.setValue(0, 10.0);
        assertEquals(10.0, afv.value(0), 0.001);

        afv.setValueAtLocation(0, 5.0);
        assertEquals(5.0, afv.value(0), 0.001);
    }
@Test
    public void testCloneMatrix() {
        Alphabet dict = new Alphabet();
        dict.lookupIndex("f1");

        double[] values = new double[] {1.0};
        AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, values);

        ConstantMatrix cloned = afv.cloneMatrix();

        assertTrue(cloned instanceof AugmentableFeatureVector);
        assertEquals(1.0, ((AugmentableFeatureVector) cloned).value(0), 0.001);
    }
@Test
    public void testCloneMatrixZeroed() {
        Alphabet dict = new Alphabet();
        dict.lookupIndex("a");

        double[] values = new double[] {4.2};
        AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, values);

        ConstantMatrix zeroed = afv.cloneMatrixZeroed();

        assertTrue(zeroed instanceof AugmentableFeatureVector);
        assertEquals(0.0, ((AugmentableFeatureVector) zeroed).value(0), 0.001);
    }
@Test
    public void testOneTwoInfinityNorm() {
        Alphabet dict = new Alphabet();
        dict.lookupIndex("a");
        dict.lookupIndex("b");

        double[] values = new double[] {3.0, 4.0};
        AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, values);

        assertEquals(7.0, afv.oneNorm(), 0.001);
        assertEquals(5.0, afv.twoNorm(), 0.001);
        assertEquals(4.0, afv.infinityNorm(), 0.001);
    }
@Test
    public void testToSparseVector() {
        Alphabet dict = new Alphabet();
        dict.lookupIndex("f");

        double[] values = new double[] {1.0};
        AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, values);

        SparseVector sv = afv.toSparseVector();

        assertNotNull(sv);
        assertEquals(1.0, sv.value(0), 0.001);
    }
@Test
    public void testAddFeatureVectorWithPrefix() {
        Alphabet dict1 = new Alphabet();
        dict1.lookupIndex("dog");

        Alphabet dict2 = new Alphabet();
        dict2.lookupIndex("dog");

        int[] indices = new int[] {0};
        double[] values = new double[] {1.0};

        FeatureVector fv = new FeatureVector(dict2, indices, values);
        AugmentableFeatureVector afv = new AugmentableFeatureVector(dict1, true);

        afv.add(fv, "X_");

        int idx = dict1.lookupIndex("X_dog", false);
        assertTrue(idx >= 0);
        assertEquals(1.0, afv.value(idx), 0.001);
    }
@Test
    public void testSetAll() {
        Alphabet dict = new Alphabet();
        dict.lookupIndex("a");
        dict.lookupIndex("b");

        double[] values = new double[] {1.0, 2.0};
        AugmentableFeatureVector afv = new AugmentableFeatureVector(dict, values);

        afv.setAll(5.5);

        assertEquals(5.5, afv.value(0), 0.001);
        assertEquals(5.5, afv.value(1), 0.001);
    } 
}