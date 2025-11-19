public class CRF_wosr_1_GPTLLMTest { 

 @Test
    public void testConstructorWithAlphabetsInitializesCorrectly() {
        Alphabet inputAlphabet = new Alphabet();
        Alphabet outputAlphabet = new Alphabet();
        inputAlphabet.lookupIndex("word");
        outputAlphabet.lookupIndex("label");

        CRF crf = new CRF(inputAlphabet, outputAlphabet);

        assertEquals(inputAlphabet, crf.getInputAlphabet());
        assertEquals(outputAlphabet, crf.getOutputAlphabet());
    }
@Test
    public void testAddStateAndRetrieve() {
        Alphabet inputAlphabet = new Alphabet();
        Alphabet outputAlphabet = new Alphabet();
        inputAlphabet.lookupIndex("feat");
        outputAlphabet.lookupIndex("lab");

        CRF crf = new CRF(inputAlphabet, outputAlphabet);

        String[] dests = {"lab"};
        crf.addState("A", 0.0, 0.0, dests, dests);

        assertNotNull(crf.getState("A"));
        assertEquals("A", crf.getState("A").getName());
    }
@Test
    public void testAddStateWithWeightNamesCreatesExpectedWeights() {
        Alphabet inputAlphabet = new Alphabet();
        Alphabet outputAlphabet = new Alphabet();
        inputAlphabet.lookupIndex("feature");
        outputAlphabet.lookupIndex("state");

        CRF crf = new CRF(inputAlphabet, outputAlphabet);

        String[] destinations = {"state"};
        String[] labels = {"state"};
        String[] weightNames = {"state->state:state"};
        crf.addState("state", 0.0, 0.0, destinations, labels, weightNames);

        String expectedWeightsName = "state->state:state";
        SparseVector weights = crf.getWeights(expectedWeightsName);
        assertNotNull(weights);
    }
@Test
    public void testGetWeightsIndexCreatesNewWeights() {
        Alphabet inputAlphabet = new Alphabet();
        Alphabet outputAlphabet = new Alphabet();
        CRF crf = new CRF(inputAlphabet, outputAlphabet);

        String weightName = "test-weight";
        int index = crf.getWeightsIndex(weightName);

        assertTrue(index >= 0);
        assertEquals(weightName, crf.getWeightsName(index));
    }
@Test
    public void testSetAndGetWeightByIndex() {
        Alphabet inputAlphabet = new Alphabet();
        Alphabet outputAlphabet = new Alphabet();
        CRF crf = new CRF(inputAlphabet, outputAlphabet);

        int weightIndex = crf.getWeightsIndex("w0");
        double[] values = {1.2, 3.4};
        int[] indices = {0, 1};
        SparseVector vector = new SparseVector(indices, values, 2);

        crf.setWeights(weightIndex, vector);

        SparseVector retrieved = crf.getWeights(weightIndex);
        assertEquals(1.2, retrieved.value(0), 0.0001);
        assertEquals(3.4, retrieved.value(1), 0.0001);
    }
@Test
    public void testFreezeUnfreezeWeights() {
        Alphabet inputAlphabet = new Alphabet();
        Alphabet outputAlphabet = new Alphabet();
        CRF crf = new CRF(inputAlphabet, outputAlphabet);

        int weightIndex = crf.getWeightsIndex("w1");

        crf.freezeWeights("w1");
        assertTrue(crf.isWeightsFrozen(weightIndex));

        crf.unfreezeWeights("w1");
        assertFalse(crf.isWeightsFrozen(weightIndex));
    }
@Test
    public void testSetGetParameterMethods() {
        Alphabet inputAlphabet = new Alphabet();
        Alphabet outputAlphabet = new Alphabet();
        inputAlphabet.lookupIndex("f");
        outputAlphabet.lookupIndex("L");

        CRF crf = new CRF(inputAlphabet, outputAlphabet);

        String[] destinations = {"L"};
        crf.addState("S", 0.0, 0.0, destinations, destinations);
        crf.addState("L", 0.0, 0.0, destinations, destinations);

        int featIdx = 0;
        int defaultWeightIndex = 0;
        crf.setParameter(0, 1, featIdx, defaultWeightIndex, 5.0);

        double weight = crf.getParameter(0, 1, featIdx, defaultWeightIndex);
        assertEquals(5.0, weight, 0.0001);
    }
@Test
    public void testSerializationDeserialization() throws Exception {
        Alphabet inputAlphabet = new Alphabet();
        Alphabet outputAlphabet = new Alphabet();
        inputAlphabet.lookupIndex("inp");
        outputAlphabet.lookupIndex("out");

        CRF original = new CRF(inputAlphabet, outputAlphabet);
        String[] dest = {"out"};
        original.addState("X", 0.5, -0.2, dest, dest);

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bout);
        out.writeObject(original);
        out.close();

        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bout.toByteArray()));
        CRF deserialized = (CRF) in.readObject();

        assertEquals("X", deserialized.getState("X").getName());
        assertEquals(0.5, deserialized.getState("X").getInitialWeight(), 0.0001);
        assertEquals(-0.2, deserialized.getState("X").getFinalWeight(), 0.0001);
    }
@Test
    public void testWeightsStructureChangeStampIncrement() {
        Alphabet inA = new Alphabet();
        Alphabet outA = new Alphabet();
        CRF crf = new CRF(inA, outA);

        int before = crf.getWeightsStructureChangeStamp();
        crf.weightsStructureChanged();
        int after = crf.getWeightsStructureChangeStamp();
        assertTrue(after > before);
    }
@Test
    public void testWeightsValueChangeStampIncrement() {
        Alphabet inA = new Alphabet();
        Alphabet outA = new Alphabet();
        CRF crf = new CRF(inA, outA);

        int before = crf.getWeightsValueChangeStamp();
        crf.weightsValueChanged();
        int after = crf.getWeightsValueChangeStamp();
        assertTrue(after > before);
    }
@Test
    public void testSetDefaultWeightAndRetrieve() {
        Alphabet inA = new Alphabet();
        Alphabet outA = new Alphabet();
        CRF crf = new CRF(inA, outA);

        int idx = crf.getWeightsIndex("transWeight");
        crf.setDefaultWeight(idx, 1.23);

        double[] defaults = crf.getDefaultWeights();
        assertEquals(1.23, defaults[idx], 0.0001);
    }
@Test
    public void testAddStateThatAlreadyExistsThrowsException() {
        Alphabet inA = new Alphabet();
        Alphabet outA = new Alphabet();
        inA.lookupIndex("xx");
        outA.lookupIndex("yy");

        CRF crf = new CRF(inA, outA);
        String[] dests = {"yy"};

        crf.addState("duplicate", 0.1, 0.2, dests, dests);

        try {
            crf.addState("duplicate", 0.1, 0.2, dests, dests);
            fail("Expected exception not thrown.");
        } catch (IllegalArgumentException e) {
            
        }
    }
@Test
    public void testSetAndGetWeightsDimensionDensely() {
        Alphabet inputAlphabet = new Alphabet();
        Alphabet outputAlphabet = new Alphabet();
        inputAlphabet.lookupIndex("f1");
        outputAlphabet.lookupIndex("L1");

        CRF crf = new CRF(inputAlphabet, outputAlphabet);

        String[] dest = {"L1"};
        crf.addState("S1", 0.0, 0.0, dest, dest);
        crf.setWeightsDimensionDensely();
        SparseVector[] weights = crf.getWeights();

        assertTrue(weights.length > 0);
        assertNotNull(weights[0]);
    }
@Test
    public void testGetNumParametersUpdatesCorrectly() {
        Alphabet inputAlphabet = new Alphabet();
        Alphabet outputAlphabet = new Alphabet();
        inputAlphabet.lookupIndex("feat");
        outputAlphabet.lookupIndex("lab");

        CRF crf = new CRF(inputAlphabet, outputAlphabet);

        String[] dests = {"lab"};
        crf.addState("source", 0.2, 0.1, dests, dests);

        int nParams = crf.getNumParameters();
        assertTrue(nParams > 0);
    }
@Test
    public void testStateGetWeightNamesReturnsExpected() {
        Alphabet inputAlphabet = new Alphabet();
        Alphabet outputAlphabet = new Alphabet();
        inputAlphabet.lookupIndex("feat");
        outputAlphabet.lookupIndex("label");

        CRF crf = new CRF(inputAlphabet, outputAlphabet);

        String[] dest = {"label"};
        String[] labels = {"label"};
        String[] weights = {"wX"};
        crf.addState("L1", 0.0, 0.0, dest, labels, weights);

        CRF.State s = crf.getState("L1");
        String[] names = s.getWeightNames(0);
        assertEquals(1, names.length);
        assertEquals("wX", names[0]);
    }
@Test
    public void testStateAddWeightSuccessfullyAdds() {
        Alphabet inputAlphabet = new Alphabet();
        Alphabet outputAlphabet = new Alphabet();
        inputAlphabet.lookupIndex("f0");
        outputAlphabet.lookupIndex("L0");

        CRF crf = new CRF(inputAlphabet, outputAlphabet);
        String[] dests = {"L0"};
        crf.addState("S", 0.0, 0.0, dests, dests);

        CRF.State st = crf.getState("S");
        st.addWeight(0, "added_weight");

        assertTrue(st.getWeightNames(0).length >= 2);
    }
@Test
    public void testFactorsPlusEqualsOperatesAsExpected() {
        Alphabet inputAlphabet = new Alphabet();
        Alphabet outputAlphabet = new Alphabet();

        CRF crf = new CRF(inputAlphabet, outputAlphabet);
        crf.getWeightsIndex("w1");

        CRF.Factors f1 = new CRF.Factors(crf);
        CRF.Factors f2 = new CRF.Factors(crf);

        f1.setParameter(0, 1.0);
        f2.setParameter(0, 2.0);

        f1.plusEquals(f2, 1.0);

        double val = f1.getParameter(0);
        assertEquals(3.0, val, 0.0001);
    }
@Test
    public void testFactorsZeroClearsAllWeights() {
        Alphabet inA = new Alphabet();
        Alphabet outA = new Alphabet();
        inA.lookupIndex("feat");
        outA.lookupIndex("lab");

        CRF crf = new CRF(inA, outA);
        crf.addState("Z", 1.2, -1.1, new String[]{"lab"}, new String[]{"lab"});

        CRF.Factors f = new CRF.Factors(crf);
        f.setParameter(0, 5.0);
        f.zero();
        assertEquals(0.0, f.getParameter(0), 0.0001);
    } 
}