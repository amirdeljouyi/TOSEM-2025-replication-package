public class HMM_wosr_1_GPTLLMTest { 

 @Test
    public void testConstructorWithAlphabets() {
        Alphabet inputAlphabet = new Alphabet();
        Alphabet outputAlphabet = new Alphabet();
        inputAlphabet.lookupIndex("word1");
        outputAlphabet.lookupIndex("label1");

        HMM hmm = new HMM(inputAlphabet, outputAlphabet);

        assertEquals(1, hmm.getInputAlphabet().size());
        assertEquals(1, hmm.getOutputAlphabet().size());
    }
@Test
    public void testAddStateSimple() {
        Alphabet inputAlphabet = new Alphabet();
        Alphabet outputAlphabet = new Alphabet();
        inputAlphabet.lookupIndex("a");
        outputAlphabet.lookupIndex("X");

        HMM hmm = new HMM(inputAlphabet, outputAlphabet);
        String[] destinations = new String[]{"S1"};
        String[] labels = new String[]{"X"};
        hmm.addState("S1", 1.0, 0.0, destinations, labels);

        assertEquals(1, hmm.numStates());
        assertNotNull(hmm.getState("S1"));
    }
@Test(expected = IllegalArgumentException.class)
    public void testAddDuplicateStateThrowsException() {
        Alphabet inputAlphabet = new Alphabet();
        Alphabet outputAlphabet = new Alphabet();
        inputAlphabet.lookupIndex("a");
        outputAlphabet.lookupIndex("Y");

        HMM hmm = new HMM(inputAlphabet, outputAlphabet);
        String[] destinations = new String[]{"S1"};
        String[] labels = new String[]{"Y"};

        hmm.addState("S1", 0.0, 0.0, destinations, labels);
        hmm.addState("S1", 0.0, 0.0, destinations, labels); 
    }
@Test
    public void testAddStateWithMatchDestinationsAndLabels() {
        Alphabet inputAlphabet = new Alphabet();
        Alphabet outputAlphabet = new Alphabet();
        inputAlphabet.lookupIndex("b");
        outputAlphabet.lookupIndex("Z");

        HMM hmm = new HMM(inputAlphabet, outputAlphabet);
        hmm.addState("Z", new String[]{"Z"});

        assertEquals(1, hmm.numStates());
        assertEquals("Z", hmm.getState(0).getName());
    }
@Test
    public void testAddFullyConnectedStates() {
        Alphabet inputAlphabet = new Alphabet();
        Alphabet outputAlphabet = new Alphabet();
        inputAlphabet.lookupIndex("x");
        outputAlphabet.lookupIndex("L1");
        outputAlphabet.lookupIndex("L2");

        HMM hmm = new HMM(inputAlphabet, outputAlphabet);
        String[] labels = new String[]{"L1", "L2"};
        hmm.addFullyConnectedStates(labels);

        assertEquals(2, hmm.numStates());
        assertNotNull(hmm.getState("L1"));
        assertNotNull(hmm.getState("L2"));
    }
@Test
    public void testAddFullyConnectedStatesForLabels() {
        Alphabet inputAlphabet = new Alphabet();
        LabelAlphabet outputAlphabet = new LabelAlphabet();
        inputAlphabet.lookupIndex("x");
        outputAlphabet.lookupLabel("A");
        outputAlphabet.lookupLabel("B");

        HMM hmm = new HMM(inputAlphabet, outputAlphabet);
        hmm.addFullyConnectedStatesForLabels();

        assertEquals(2, hmm.numStates());
        assertNotNull(hmm.getState("A"));
        assertNotNull(hmm.getState("B"));
    }
@Test
    public void testInitTransitionsUniform() {
        Alphabet inputAlphabet = new Alphabet();
        Alphabet outputAlphabet = new Alphabet();
        inputAlphabet.lookupIndex("word");
        outputAlphabet.lookupIndex("label");

        HMM hmm = new HMM(inputAlphabet, outputAlphabet);
        hmm.addState("X", new String[]{"X"});
        hmm.initTransitions(null, 0.0);

        assertNotNull(hmm.getTransitionMultinomial());
        assertEquals(1, hmm.getTransitionMultinomial().length);
    }
@Test
    public void testInitEmissionsWithNoise() {
        Alphabet inputAlphabet = new Alphabet();
        Alphabet outputAlphabet = new Alphabet();
        inputAlphabet.lookupIndex("input1");
        inputAlphabet.lookupIndex("input2");
        outputAlphabet.lookupIndex("label");

        HMM hmm = new HMM(inputAlphabet, outputAlphabet);
        hmm.addState("label", new String[]{"label"});
        hmm.initEmissions(new Random(42), 0.5);

        assertNotNull(hmm.getEmissionMultinomial());
        assertEquals(1, hmm.getEmissionMultinomial().length);
    }
@Test
    public void testTrainWithMockInstanceList() {
        Alphabet inputAlphabet = new Alphabet();
        Alphabet outputAlphabet = new Alphabet();
        inputAlphabet.lookupIndex("w1");
        outputAlphabet.lookupIndex("l1");

        HMM hmm = new HMM(inputAlphabet, outputAlphabet);

        hmm.addState("l1", new String[]{"l1"});

        InstanceList ilist = new InstanceList(null);
        TokenSequence ts = new TokenSequence();
        ts.add(new Token("w1"));

        FeatureSequence inputSeq = new FeatureSequence(inputAlphabet);
        inputSeq.add("w1");

        FeatureSequence targetSeq = new FeatureSequence(outputAlphabet);
        targetSeq.add("l1");

        Instance instance = new Instance(inputSeq, targetSeq, null, null);
        ArrayList<Instance> list = new ArrayList<>();
        list.add(instance);
        ilist = new InstanceList(null);
        ilist.addThruPipe(instance);

        boolean result = hmm.train(ilist);
        assertTrue(result);
    }
@Test
    public void testAddOrderNStatesOrderZero() {
        Alphabet inputAlphabet = new Alphabet();
        LabelAlphabet outputAlphabet = new LabelAlphabet();
        inputAlphabet.lookupIndex("x");
        outputAlphabet.lookupLabel("A");

        HMM hmm = new HMM(inputAlphabet, outputAlphabet);

        InstanceList data = new InstanceList(null);

        String startState = hmm.addOrderNStates(data, null, null, "A", null, null, true);
        assertNotNull(startState);
        assertEquals("A", startState);
        assertEquals(1, hmm.numStates());
    }
@Test
    public void testAddFullyConnectedStatesForTriLabels() {
        Alphabet inputAlphabet = new Alphabet();
        LabelAlphabet outputAlphabet = new LabelAlphabet();
        inputAlphabet.lookupIndex("a");
        outputAlphabet.lookupLabel("X");
        outputAlphabet.lookupLabel("Y");

        HMM hmm = new HMM(inputAlphabet, outputAlphabet);
        hmm.addFullyConnectedStatesForTriLabels();

        
        assertEquals(8, hmm.numStates());
    }
@Test
    public void testWriteAndReadObjectSerialization() throws Exception {
        Alphabet inputAlphabet = new Alphabet();
        Alphabet outputAlphabet = new Alphabet();
        inputAlphabet.lookupIndex("a");
        outputAlphabet.lookupIndex("b");

        HMM hmm = new HMM(inputAlphabet, outputAlphabet);
        hmm.addState("b", new String[]{"b"});
        hmm.initTransitions(null, 0.1);
        hmm.initEmissions(null, 0.1);

        File tempFile = File.createTempFile("hmm_test", ".ser");
        tempFile.deleteOnExit();
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(tempFile));
        oos.writeObject(hmm);
        oos.close();

        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(tempFile));
        HMM readHmm = (HMM) ois.readObject();
        ois.close();

        assertEquals(hmm.numStates(), readHmm.numStates());
        assertEquals(hmm.getInputAlphabet().size(), readHmm.getInputAlphabet().size());
        assertEquals(hmm.getOutputAlphabet().size(), readHmm.getOutputAlphabet().size());
    }
@Test
    public void testWeightedIncrementorAffectsEstimator() {
        Alphabet inputAlphabet = new Alphabet();
        Alphabet outputAlphabet = new Alphabet();
        inputAlphabet.lookupIndex("foo");
        outputAlphabet.lookupIndex("bar");

        HMM hmm = new HMM(inputAlphabet, outputAlphabet);
        hmm.addState("bar", new String[]{"bar"});

        hmm.emissionEstimator = new Multinomial.Estimator[1];
        hmm.transitionEstimator = new Multinomial.Estimator[1];

        hmm.emissionEstimator[0] = new Multinomial.LaplaceEstimator(inputAlphabet);
        hmm.transitionEstimator[0] = new Multinomial.LaplaceEstimator(outputAlphabet);

        HMM.State state = (HMM.State) hmm.getState("bar");
        HMM.TransitionIterator ti = state.transitionIterator(
                new FeatureSequence(inputAlphabet),
                0,
                new FeatureSequence(outputAlphabet),
                0);
        Transducer.TransitionIterator transition = ti;

        HMM.WeightedIncrementor inc = hmm.new WeightedIncrementor(2.0);
        while (transition.hasNext()) {
            transition.nextState();
            inc.incrementTransition(transition, 1.0);
        }

        assertNotNull(hmm.emissionEstimator[0]);
        assertNotNull(hmm.transitionEstimator[0]);
    } 
}