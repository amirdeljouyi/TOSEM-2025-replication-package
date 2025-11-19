public class CRF_wosr_2_GPTLLMTest { 

 @Test
    public void testDefaultConstructorInitializesFields() {
        CRF.Factors factors = new CRF.Factors();

        assertNotNull(factors.weightAlphabet);
        assertEquals(0, factors.initialWeights.length);
        assertEquals(0, factors.finalWeights.length);
        assertNull(factors.weights);
        assertNull(factors.defaultWeights);
        assertNull(factors.weightsFrozen);
    }
@Test
    public void testCloneStructureConstructorZeroedWeights() {
        Alphabet alpha = new Alphabet();
        alpha.lookupIndex("featureX");

        SparseVector[] weights = new SparseVector[1];
        weights[0] = new IndexedSparseVector(new int[]{0}, new double[]{5.0}, 1, 1, false, false, false);
        double[] defaultWeights = new double[]{3.14};
        boolean[] weightsFrozen = new boolean[]{false};
        double[] initialWeights = new double[]{1.2};
        double[] finalWeights = new double[]{2.4};

        CRF.Factors original = new CRF.Factors();
        original.weightAlphabet = alpha;
        original.weights = weights;
        original.defaultWeights = defaultWeights;
        original.weightsFrozen = weightsFrozen;
        original.initialWeights = initialWeights;
        original.finalWeights = finalWeights;

        CRF.Factors clone = new CRF.Factors(original);

        assertEquals(original.weightAlphabet, clone.weightAlphabet);
        assertEquals(1, clone.weights.length);
        assertEquals(0.0, clone.weights[0].value(0), 0.001);
        assertEquals(0.0, clone.defaultWeights[0], 0.001);
        assertEquals(0.0, clone.initialWeights[0], 0.001);
        assertEquals(0.0, clone.finalWeights[0], 0.001);
        assertSame(original.weightsFrozen, clone.weightsFrozen);
    }
@Test
    public void testCloneWithValuesConstructorCopiesValues() {
        Alphabet alpha = new Alphabet();
        alpha.lookupIndex("f");

        SparseVector vector = new IndexedSparseVector(new int[]{0}, new double[]{1.0}, 1, 1, false, false, false);
        SparseVector[] weights = new SparseVector[]{vector};
        double[] defaultWeights = new double[]{2.5};
        boolean[] frozen = new boolean[]{true};
        double[] initialWeights = new double[]{0.3};
        double[] finalWeights = new double[]{0.7};

        CRF.Factors original = new CRF.Factors();
        original.weightAlphabet = alpha;
        original.weights = weights;
        original.defaultWeights = defaultWeights;
        original.weightsFrozen = frozen;
        original.initialWeights = initialWeights;
        original.finalWeights = finalWeights;

        CRF.Factors copy = new CRF.Factors(original, true);

        assertNotSame(original.weights[0], copy.weights[0]);
        assertEquals(1.0, copy.weights[0].value(0), 0.001);
        assertEquals(2.5, copy.defaultWeights[0], 0.001);
        assertEquals(0.3, copy.initialWeights[0], 0.001);
        assertEquals(0.7, copy.finalWeights[0], 0.001);
        assertSame(original.weightsFrozen, copy.weightsFrozen);
        assertNotSame(original.weightAlphabet, copy.weightAlphabet);
        assertEquals(original.weightAlphabet.size(), copy.weightAlphabet.size());
    }
@Test
    public void testGetNumFactorsIncludesAllWeights() {
        Alphabet alpha = new Alphabet();
        alpha.lookupIndex("a");

        SparseVector weight = new IndexedSparseVector(new int[]{0}, new double[]{2.0}, 1, 1, false, false, false);
        CRF.Factors factors = new CRF.Factors();
        factors.weightAlphabet = alpha;
        factors.weights = new SparseVector[]{weight};
        factors.defaultWeights = new double[]{1.0};
        factors.initialWeights = new double[]{0.1};
        factors.finalWeights = new double[]{0.2};

        int expected = 1 + 1 + 1 + 1; 
        assertEquals(expected, factors.getNumFactors());
    }
@Test
    public void testPlusEqualsAddsCorrectly() {
        Alphabet alpha = new Alphabet();
        alpha.lookupIndex("f");

        SparseVector baseVec = new IndexedSparseVector(new int[]{0}, new double[]{3.0}, 1, 1, false, false, false);
        SparseVector addVec = new IndexedSparseVector(new int[]{0}, new double[]{1.0}, 1, 1, false, false, false);

        CRF.Factors base = new CRF.Factors();
        base.weightAlphabet = alpha;
        base.weights = new SparseVector[]{baseVec};
        base.defaultWeights = new double[]{2.0};
        base.initialWeights = new double[]{0.5};
        base.finalWeights = new double[]{0.5};
        base.weightsFrozen = new boolean[]{false};

        CRF.Factors add = new CRF.Factors();
        add.weightAlphabet = alpha;
        add.weights = new SparseVector[]{addVec};
        add.defaultWeights = new double[]{1.0};
        add.initialWeights = new double[]{0.3};
        add.finalWeights = new double[]{0.3};
        add.weightsFrozen = new boolean[]{false};

        base.plusEquals(add, 2.0);

        assertEquals(5.0, base.weights[0].value(0), 0.001);  
        assertEquals(4.0, base.defaultWeights[0], 0.001);    
        assertEquals(1.1, base.initialWeights[0], 0.001);    
        assertEquals(1.1, base.finalWeights[0], 0.001);
    }
@Test
    public void testPlusEqualsWithObeyFrozenSkipsFrozenWeight() {
        Alphabet alpha = new Alphabet();
        alpha.lookupIndex("f");

        SparseVector frozenVec = new IndexedSparseVector(new int[]{0}, new double[]{10.0}, 1, 1, false, false, false);
        SparseVector addVec = new IndexedSparseVector(new int[]{0}, new double[]{1.0}, 1, 1, false, false, false);

        CRF.Factors base = new CRF.Factors();
        base.weightAlphabet = alpha;
        base.weights = new SparseVector[]{frozenVec};
        base.defaultWeights = new double[]{5.0};
        base.initialWeights = new double[]{0.2};
        base.finalWeights = new double[]{0.2};
        base.weightsFrozen = new boolean[]{true};

        CRF.Factors add = new CRF.Factors();
        add.weightAlphabet = alpha;
        add.weights = new SparseVector[]{addVec};
        add.defaultWeights = new double[]{1.0};
        add.initialWeights = new double[]{0.3};
        add.finalWeights = new double[]{0.3};
        add.weightsFrozen = new boolean[]{true};

        base.plusEquals(add, 1.0, true);

        assertEquals(10.0, base.weights[0].value(0), 0.001);
        assertEquals(5.0, base.defaultWeights[0], 0.001);
        assertEquals(0.5, base.initialWeights[0], 0.001);
        assertEquals(0.5, base.finalWeights[0], 0.001);
    }
@Test
    public void testZeroClearsAllWeights() {
        Alphabet alpha = new Alphabet();
        alpha.lookupIndex("feat");

        SparseVector vec = new IndexedSparseVector(new int[]{0}, new double[]{2.0}, 1, 1, false, false, false);

        CRF.Factors factors = new CRF.Factors();
        factors.weightAlphabet = alpha;
        factors.weights = new SparseVector[]{vec};
        factors.defaultWeights = new double[]{1.0};
        factors.initialWeights = new double[]{3.0};
        factors.finalWeights = new double[]{4.0};

        factors.zero();

        assertEquals(0.0, factors.weights[0].value(0), 0.001);
        assertEquals(0.0, factors.defaultWeights[0], 0.001);
        assertEquals(0.0, factors.initialWeights[0], 0.001);
        assertEquals(0.0, factors.finalWeights[0], 0.001);
    }
@Test
    public void testSetAndGetParameterFromFlatIndex() {
        Alphabet alpha = new Alphabet();
        alpha.lookupIndex("xyz");

        SparseVector vec = new IndexedSparseVector(new int[]{0}, new double[]{5.0}, 1, 1, false, false, false);

        CRF.Factors factors = new CRF.Factors();
        factors.weightAlphabet = alpha;
        factors.weights = new SparseVector[]{vec};
        factors.defaultWeights = new double[]{2.0};
        factors.initialWeights = new double[]{1.0};
        factors.finalWeights = new double[]{1.5};

        int totalParams = factors.getNumFactors();

        for (int i = 0; i < totalParams; i++) {
            double newVal = 100.0 + i;
            factors.setParameter(i, newVal);
            double result = factors.getParameter(i);
            assertEquals(newVal, result, 0.001);
        }
    }
@Test(expected = IllegalArgumentException.class)
    public void testGetParameterOutOfBoundsThrowsException() {
        CRF.Factors factors = new CRF.Factors();
        factors.initialWeights = new double[]{1.1};
        factors.finalWeights = new double[]{1.2};
        factors.weights = new SparseVector[]{new IndexedSparseVector(new int[]{0}, new double[]{1.0}, 1, 1, false, false, false)};
        factors.defaultWeights = new double[]{1.0};
        factors.getParameter(100);  
    }
@Test(expected = IllegalArgumentException.class)
    public void testSetParametersInvalidLengthThrows() {
        CRF.Factors factors = new CRF.Factors();
        factors.initialWeights = new double[]{1.0};
        factors.finalWeights = new double[]{1.0};
        factors.defaultWeights = new double[]{1.0};
        factors.weights = new SparseVector[]{new IndexedSparseVector(new int[]{0}, new double[]{1.0}, 1, 1, false, false, false)};
        factors.setParameters(new double[]{1.0, 2.0}); 
    } 
}