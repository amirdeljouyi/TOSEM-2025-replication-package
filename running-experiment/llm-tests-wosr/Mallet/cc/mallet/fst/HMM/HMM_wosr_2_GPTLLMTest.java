public class HMM_wosr_2_GPTLLMTest { 

 @Test
    public void testConstructorWithAlphabets() {
        Alphabet inputAlphabet = new Alphabet();
        inputAlphabet.lookupIndex("a");
        Alphabet outputAlphabet = new Alphabet();
        outputAlphabet.lookupIndex("X");

        HMM hmm = new HMM(inputAlphabet, outputAlphabet);

        assertEquals(inputAlphabet, hmm.getInputAlphabet());
        assertEquals(outputAlphabet, hmm.getOutputAlphabet());
        assertNotNull(hmm.getTransitionMultinomial());
        assertNull(hmm.getInitialMultinomial());
    }
@Test
    public void testAddStateWithAllParameters() {
        Alphabet inputAlphabet = new Alphabet();
        inputAlphabet.lookupIndex("foo");
        Alphabet outputAlphabet = new Alphabet();
        outputAlphabet.lookupIndex("label1");
        outputAlphabet.lookupIndex("label2");
        HMM hmm = new HMM(inputAlphabet, outputAlphabet);

        String[] labels = new String[]{"label1", "label2"};
        String[] destinations = new String[]{"label1", "label2"};

        hmm.addState("s1", 0.5, 0.3, destinations, labels);

        HMM.State state = hmm.getState("s1");
        assertNotNull(state);
        assertEquals("s1", state.getName());
        assertEquals(0.5, state.getInitialWeight(), 1e-6);
        assertEquals(0.3, state.getFinalWeight(), 1e-6);
    }
@Test(expected = IllegalArgumentException.class)
    public void testAddDuplicateStateThrowsException() {
        Alphabet inputAlphabet = new Alphabet();
        inputAlphabet.lookupIndex("x");
        Alphabet outputAlphabet = new Alphabet();
        outputAlphabet.lookupIndex("A");

        HMM hmm = new HMM(inputAlphabet, outputAlphabet);
        
        String[] labels = new String[]{"A"};
        String[] dests = new String[]{"A"};

        hmm.addState("SAME", 0.0, 0.0, dests, labels);
        hmm.addState("SAME", 0.0, 0.0, dests, labels);
    }
@Test
    public void testAddFullyConnectedStates() {
        Alphabet inputAlphabet = new Alphabet();
        Alphabet outputAlphabet = new Alphabet();
        outputAlphabet.lookupIndex("L1");
        outputAlphabet.lookupIndex("L2");

        HMM hmm = new HMM(inputAlphabet, outputAlphabet);

        hmm.addFullyConnectedStatesForLabels();

        assertEquals(2, hmm.numStates());
        assertNotNull(hmm.getState("L1"));
        assertNotNull(hmm.getState("L2"));
    }
@Test
    public void testInitTransitionsAndEmissions() {
        Alphabet inputAlphabet = new Alphabet();
        inputAlphabet.lookupIndex("X");
        Alphabet outputAlphabet = new Alphabet();
        outputAlphabet.lookupIndex("Y");

        HMM hmm = new HMM(inputAlphabet, outputAlphabet);
        String[] labels = new String[]{"Y"};
        String[] dests = new String[]{"Y"};
        hmm.addState("Y", 0.6, 0.0, dests, labels);

        Random rand = new Random(1234L);

        hmm.initTransitions(rand, 0.5);
        hmm.initEmissions(rand, 0.7);

        assertNotNull(hmm.getInitialMultinomial());
        assertNotNull(hmm.getTransitionMultinomial());
        assertNotNull(hmm.getEmissionMultinomial());
        assertTrue(hmm.getTransitionMultinomial().length > 0);
    }
@Test
    public void testAddStatesForOrderZero() {
        Alphabet inputAlphabet = new Alphabet();
        Alphabet outputAlphabet = new Alphabet();
        outputAlphabet.lookupIndex("Tag1");
        outputAlphabet.lookupIndex("Tag2");

        InstanceList instanceList = new InstanceList(new Pipe() {
            public Instance pipe(Instance carrier) {
                return carrier;
            }
        });

        HMM hmm = new HMM(inputAlphabet, outputAlphabet);
        String startState = hmm.addOrderNStates(instanceList, null, null, "S", null, null, true);

        assertEquals("S", startState);
        assertNotNull(hmm.getState("Tag1"));
        assertNotNull(hmm.getState("Tag2"));
    }
@Test
    public void testLabelConnectionsInCreatesExpected() {
        Alphabet inputAlphabet = new Alphabet();
        inputAlphabet.lookupIndex("foo");
        Alphabet outputAlphabet = new Alphabet();
        outputAlphabet.lookupIndex("A");
        outputAlphabet.lookupIndex("B");

        Pipe pipe = new Pipe() {
            public Instance pipe(Instance carrier) {
                return carrier;
            }
        };
        pipe.setTargetAlphabet(outputAlphabet);

        InstanceList trainingData = new InstanceList(pipe);
        trainingData.addThruPipe(new Instance("foo", new FeatureSequence(outputAlphabet, new String[]{"A", "B"}), null, null));
        trainingData.addThruPipe(new Instance("foo", new FeatureSequence(outputAlphabet, new String[]{"B", "A"}), null, null));

        HMM hmm = new HMM(inputAlphabet, outputAlphabet);
        hmm.addStatesForLabelsConnectedAsIn(trainingData);

        assertEquals(2, hmm.numStates());
        assertNotNull(hmm.getState("A"));
        assertNotNull(hmm.getState("B"));
    }
@Test
    public void testResetInitializesMultinomials() {
        Alphabet inputAlphabet = new Alphabet();
        inputAlphabet.lookupIndex("x");
        Alphabet outputAlphabet = new Alphabet();
        outputAlphabet.lookupIndex("y");

        HMM hmm = new HMM(inputAlphabet, outputAlphabet);
        String[] labels = new String[]{"y"};
        String[] dests = new String[]{"y"};
        hmm.addState("s", 0.0, 0.0, dests, labels);

        hmm.reset();

        assertNotNull(hmm.getTransitionMultinomial());
        assertNotNull(hmm.getInitialMultinomial());
        assertTrue(hmm.getTransitionMultinomial().length > 0);
        assertNotNull(hmm.getEmissionMultinomial()[0]);
    }
@Test
    public void testTrainSetsEstimatorsAndMultinomials() {
        Alphabet inputAlphabet = new Alphabet();
        inputAlphabet.lookupIndex("x");
        Alphabet outputAlphabet = new Alphabet();
        outputAlphabet.lookupIndex("A");

        HMM hmm = new HMM(inputAlphabet, outputAlphabet);
        String[] labels = new String[]{"A"};
        String[] states = new String[]{"A"};
        hmm.addState("A", 0.0, 0.0, states, labels);

        Pipe dummyPipe = new Pipe() {
            public Instance pipe(Instance inst) {
                return inst;
            }
        };
        dummyPipe.setDataAlphabet(inputAlphabet);
        dummyPipe.setTargetAlphabet(outputAlphabet);

        InstanceList trainList = new InstanceList(dummyPipe);
        FeatureSequence dataSeq = new FeatureSequence(inputAlphabet, new int[]{0});
        FeatureSequence labelSeq = new FeatureSequence(outputAlphabet, new int[]{0});
        trainList.addThruPipe(new Instance(dataSeq, labelSeq, null, null));

        boolean result = hmm.train(trainList);

        assertTrue(result);
        assertNotNull(hmm.getInitialMultinomial());
        assertNotNull(hmm.getEmissionMultinomial()[0]);
        assertNotNull(hmm.getTransitionMultinomial()[0]);
    }
@Test
    public void testGetStateByNameAndIndex() {
        Alphabet inputAlphabet = new Alphabet();
        inputAlphabet.lookupIndex("feature");
        Alphabet outputAlphabet = new Alphabet();
        outputAlphabet.lookupIndex("L");

        HMM hmm = new HMM(inputAlphabet, outputAlphabet);
        hmm.addState("L", new String[]{"L"});

        Transducer.State stateByName = hmm.getState("L");
        Transducer.State stateByIndex = hmm.getState(0);

        assertEquals(stateByName, stateByIndex);
    }
@Test
    public void testAddStatesForBiLabelsConnectedAsIn() {
        Alphabet inputAlphabet = new Alphabet();
        Alphabet outputAlphabet = new Alphabet();
        outputAlphabet.lookupIndex("X");
        outputAlphabet.lookupIndex("Y");

        Pipe dummyPipe = new Pipe() {
            public Instance pipe(Instance inst) {
                return inst;
            }
        };
        dummyPipe.setTargetAlphabet(outputAlphabet);
        
        FeatureSequence target = new FeatureSequence(outputAlphabet, new String[]{"X", "Y", "X"});
        InstanceList trainList = new InstanceList(dummyPipe);
        trainList.addThruPipe(new Instance("input", target, null, null));

        HMM hmm = new HMM(inputAlphabet, outputAlphabet);
        hmm.addStatesForBiLabelsConnectedAsIn(trainList);

        assertTrue(hmm.numStates() > 0);
        assertNotNull(hmm.getState("X,Y"));
        assertNotNull(hmm.getState("Y,X"));
    } 
}