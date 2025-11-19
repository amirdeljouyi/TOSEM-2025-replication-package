public class CRF_wosr_4_GPTLLMTest { 

 @Test
    public void testCRFConstructorFromAlphabets() {
        Alphabet inputAlphabet = new Alphabet();
        Alphabet outputAlphabet = new Alphabet();

        inputAlphabet.lookupIndex("feature1");
        outputAlphabet.lookupIndex("label1");

        CRF crf = new CRF(inputAlphabet, outputAlphabet);

        assertEquals(1, crf.getInputAlphabet().size());
        assertEquals(1, crf.getOutputAlphabet().size());
    }
@Test
    public void testAddSingleStateSuccessfully() {
        Alphabet inputAlphabet = new Alphabet();
        Alphabet outputAlphabet = new Alphabet();
        inputAlphabet.lookupIndex("f1");
        outputAlphabet.lookupIndex("L1");

        Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
        CRF crf = new CRF(pipe, null);

        String[] dests = new String[]{"L1"};
        crf.addState("L1", 0.0, 0.0, dests, dests);

        Transducer.State state = crf.getState("L1");
        assertNotNull(state);
        assertEquals("L1", state.getName());
        assertEquals(0.0, ((CRF.State) state).getInitialWeight(), 0.01);
    }
@Test(expected = IllegalArgumentException.class)
    public void testAddStateWithDuplicateNameFails() {
        Alphabet inputAlphabet = new Alphabet();
        Alphabet outputAlphabet = new Alphabet();
        inputAlphabet.lookupIndex("f1");
        outputAlphabet.lookupIndex("L1");

        Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
        CRF crf = new CRF(pipe, null);

        String[] dests = new String[]{"L1"};
        crf.addState("L1", 0.0, 0.0, dests, dests);
        crf.addState("L1", 0.0, 0.0, dests, dests); 
    }
@Test
    public void testGetWeightsIndexCreatesNewWeight() {
        Alphabet inputAlphabet = new Alphabet();
        Alphabet outputAlphabet = new Alphabet();
        inputAlphabet.lookupIndex("f1");
        outputAlphabet.lookupIndex("l1");

        Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
        CRF crf = new CRF(pipe, null);

        int index = crf.getWeightsIndex("w1");
        assertEquals(0, index);

        int index2 = crf.getWeightsIndex("w2");
        assertEquals(1, index2);

        assertEquals(2, crf.getWeights().length);
    }
@Test
    public void testSetAndGetDefaultWeight() {
        Alphabet inputAlphabet = new Alphabet();
        Alphabet outputAlphabet = new Alphabet();
        inputAlphabet.lookupIndex("f1");
        outputAlphabet.lookupIndex("l1");

        Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
        CRF crf = new CRF(pipe, null);

        int weightIndex = crf.getWeightsIndex("w1");
        crf.setDefaultWeight(weightIndex, 0.75);
        assertEquals(0.75, crf.getDefaultWeights()[weightIndex], 0.0001);
    }
@Test
    public void testFreezeUnfreezeWeights() {
        Alphabet inputAlphabet = new Alphabet();
        Alphabet outputAlphabet = new Alphabet();
        inputAlphabet.lookupIndex("f1");
        outputAlphabet.lookupIndex("l1");

        Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
        CRF crf = new CRF(pipe, null);

        String weightName = "transition";
        int widx = crf.getWeightsIndex(weightName);

        assertFalse(crf.isWeightsFrozen(widx));

        crf.freezeWeights(weightName);
        assertTrue(crf.isWeightsFrozen(widx));

        crf.unfreezeWeights(weightName);
        assertFalse(crf.isWeightsFrozen(widx));
    }
@Test
    public void testSetFeatureSelection() {
        Alphabet inputAlphabet = new Alphabet();
        Alphabet outputAlphabet = new Alphabet();
        inputAlphabet.lookupIndex("a");
        outputAlphabet.lookupIndex("x");

        Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
        CRF crf = new CRF(pipe, null);

        int widx = crf.getWeightsIndex("w");
        FeatureSelection fs = new FeatureSelection(inputAlphabet);
        fs.select("a");

        crf.setFeatureSelection(widx, fs);
        assertNotNull(fs);
    }
@Test
    public void testSetAndGetParameterByTransition() {
        Alphabet inputAlphabet = new Alphabet();
        Alphabet outputAlphabet = new Alphabet();
        inputAlphabet.lookupIndex("f1");
        outputAlphabet.lookupIndex("L1");

        Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
        CRF crf = new CRF(pipe, null);

        String[] dests = new String[]{"L1"};
        crf.addState("L1", 0.0, 0.0, dests, dests);

        int featureIndex = inputAlphabet.lookupIndex("f1");
        crf.setParameter(0, 0, featureIndex, 0.5);
        double value = crf.getParameter(0, 0, featureIndex);

        assertEquals(0.5, value, 0.001);
    }
@Test(expected = IllegalArgumentException.class)
    public void testSetIllegalParameterTransition() {
        Alphabet inputAlphabet = new Alphabet();
        Alphabet outputAlphabet = new Alphabet();
        inputAlphabet.lookupIndex("f");
        outputAlphabet.lookupIndex("L1");
        outputAlphabet.lookupIndex("L2");

        Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
        CRF crf = new CRF(pipe, null);

        String[] dests = new String[]{"L1"};
        crf.addState("L1", 0.0, 0.0, dests, dests);

        crf.setParameter(0, 1, 0, 0.2); 
    }
@Test
    public void testAddOrder0CRFStateLabels() {
        Alphabet inputAlphabet = new Alphabet();
        Alphabet outputAlphabet = new Alphabet();
        inputAlphabet.lookupIndex("f0");
        outputAlphabet.lookupIndex("A");
        outputAlphabet.lookupIndex("B");

        Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
        CRF crf = new CRF(pipe, null);

        InstanceList instances = new InstanceList(pipe);
        instances.addThruPipe(new Instance(new FeatureVector(inputAlphabet, new int[]{0}), new FeatureSequence(outputAlphabet, new int[]{0, 1}), null, null));

        String startName = crf.addOrderNStates(instances, null, null, null, null, null, true);

        assertNotNull(crf.getState("A"));
        assertNotNull(crf.getState("B"));
        assertEquals(null, startName);
    }
@Test
    public void testAddStateHasTransition() {
        Alphabet inputAlphabet = new Alphabet();
        Alphabet outputAlphabet = new Alphabet();
        inputAlphabet.lookupIndex("f1");
        outputAlphabet.lookupIndex("L1");
        outputAlphabet.lookupIndex("L2");

        Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
        CRF crf = new CRF(pipe, null);

        String[] dests = new String[]{"L2"};
        crf.addState("L1", 0.0, 0.0, dests, dests);

        Transducer.State s = crf.getState("L1");
        CRF.State source = (CRF.State) s;

        Transducer.TransitionIterator it = source.transitionIterator(
                new FeatureVectorSequence(new FeatureVector[]{new FeatureVector(inputAlphabet, new int[]{0})}),
                "L2");

        assertTrue(it.hasNext());
        Transducer.State next = it.nextState();
        assertEquals("L2", next.getName());
    } 
}