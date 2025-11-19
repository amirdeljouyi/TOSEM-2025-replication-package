public class HMM_wosr_4_GPTLLMTest { 

 @Test
    public void testConstructorWithAlphabets() {
        Alphabet inputAlpha = new Alphabet();
        Alphabet outputAlpha = new LabelAlphabet();

        inputAlpha.lookupIndex("feature1");
        outputAlpha.lookupIndex("label1");

        HMM hmm = new HMM(inputAlpha, outputAlpha);

        assertNotNull(hmm.getInputAlphabet());
        assertNotNull(hmm.getOutputAlphabet());
        assertEquals(1, hmm.getInputAlphabet().size());
        assertEquals(1, hmm.getOutputAlphabet().size());
    }
@Test
    public void testAddStateWithBasicParameters() {
        Alphabet inputAlpha = new Alphabet();
        Alphabet outputAlpha = new LabelAlphabet();

        inputAlpha.lookupIndex("x");
        outputAlpha.lookupIndex("A");

        HMM hmm = new HMM(inputAlpha, outputAlpha);
        String[] destNames = new String[]{"S2"};
        String[] labelNames = new String[]{"A"};

        hmm.addState("S1", 0.5, 0.0, destNames, labelNames);

        HMM.State s1 = hmm.getState("S1");
        assertNotNull(s1);
        assertEquals("S1", s1.getName());
        assertEquals(0.5, s1.getInitialWeight(), 1e-6);
        assertEquals(0.0, s1.getFinalWeight(), 1e-6);
    }
@Test(expected = IllegalArgumentException.class)
    public void testAddDuplicateStateThrows() {
        Alphabet inputAlpha = new Alphabet();
        Alphabet outputAlpha = new LabelAlphabet();

        inputAlpha.lookupIndex("x");
        outputAlpha.lookupIndex("A");

        HMM hmm = new HMM(inputAlpha, outputAlpha);
        String[] destNames = new String[]{"S2"};
        String[] labelNames = new String[]{"A"};

        hmm.addState("S1", 0.5, 0.0, destNames, labelNames);
        hmm.addState("S1", 0.5, 0.0, destNames, labelNames);
    }
@Test
    public void testAddFullyConnectedStates() {
        Alphabet inputAlpha = new Alphabet();
        Alphabet outputAlpha = new LabelAlphabet();
        inputAlpha.lookupIndex("x");
        outputAlpha.lookupIndex("A");
        outputAlpha.lookupIndex("B");

        HMM hmm = new HMM(inputAlpha, outputAlpha);
        String[] states = new String[]{"A", "B"};

        hmm.addFullyConnectedStates(states);

        assertEquals(2, hmm.numStates());
        assertNotNull(hmm.getState("A"));
        assertNotNull(hmm.getState("B"));
    }
@Test
    public void testResetInitializesEstimators() {
        Alphabet inputAlpha = new Alphabet();
        Alphabet outputAlpha = new LabelAlphabet();

        inputAlpha.lookupIndex("x");
        outputAlpha.lookupIndex("A");
        outputAlpha.lookupIndex("B");

        HMM hmm = new HMM(inputAlpha, outputAlpha);
        hmm.addFullyConnectedStatesForLabels();
        hmm.reset();

        assertNotNull(hmm.getTransitionMultinomial());
        assertNotNull(hmm.getEmissionMultinomial());
        assertNotNull(hmm.getInitialMultinomial());
        assertEquals(hmm.numStates(), hmm.getTransitionMultinomial().length);
        assertEquals(hmm.numStates(), hmm.getEmissionMultinomial().length);
    }
@Test
    public void testInitTransitionsSetsWeights() {
        Alphabet inputAlpha = new Alphabet();
        Alphabet outputAlpha = new LabelAlphabet();

        inputAlpha.lookupIndex("f1");
        outputAlpha.lookupIndex("L1");
        outputAlpha.lookupIndex("L2");

        HMM hmm = new HMM(inputAlpha, outputAlpha);
        hmm.addFullyConnectedStatesForLabels();
        hmm.initTransitions(new Random(123), 1.0);

        assertNotNull(hmm.getInitialMultinomial());
        assertEquals(hmm.numStates(), hmm.getTransitionMultinomial().length);
    }
@Test
    public void testInitEmissionsInitializesAllStates() {
        Alphabet inputAlpha = new Alphabet();
        Alphabet outputAlpha = new LabelAlphabet();

        inputAlpha.lookupIndex("f1");
        inputAlpha.lookupIndex("f2");
        outputAlpha.lookupIndex("L1");

        HMM hmm = new HMM(inputAlpha, outputAlpha);
        hmm.addFullyConnectedStatesForLabels();
        hmm.initEmissions(new Random(123), 0.5);

        assertNotNull(hmm.getEmissionMultinomial());
        assertEquals(hmm.numStates(), hmm.getEmissionMultinomial().length);
    }
@Test
    public void testAddOrderNStatesOrderZero() {
        Alphabet inputAlpha = new Alphabet();
        LabelAlphabet outputAlpha = new LabelAlphabet();

        inputAlpha.lookupIndex("walk");
        outputAlpha.lookupIndex("verb");

        InstanceList ilist = new InstanceList(new SerialPipes(Arrays.asList(
                new Target2LabelSequence(),
                new TokenSequence2FeatureSequence()
        )));
        ilist.setTargetAlphabet(outputAlpha);
        ilist.setDataAlphabet(inputAlpha);

        TokenSequence ts = new TokenSequence();
        ts.add("walk");

        ilist.addThruPipe(new Instance(ts, "verb", null, null));

        HMM hmm = new HMM(inputAlpha, outputAlpha);
        String start = hmm.addOrderNStates(ilist, null, null, "verb", null, null, true);

        assertEquals("verb", start);
        assertEquals(1, hmm.numStates());
        assertNotNull(hmm.getState("verb"));
    }
@Test
    public void testTrainUpdatesModel() {
        Alphabet inputAlpha = new Alphabet();
        LabelAlphabet outputAlpha = new LabelAlphabet();

        inputAlpha.lookupIndex("walk");
        inputAlpha.lookupIndex("run");
        outputAlpha.lookupIndex("verb");
        outputAlpha.lookupIndex("action");

        InstanceList ilist = new InstanceList(new SerialPipes(Arrays.asList(
                new Target2LabelSequence(),
                new TokenSequence2FeatureSequence()
        )));
        ilist.setDataAlphabet(inputAlpha);
        ilist.setTargetAlphabet(outputAlpha);

        TokenSequence ts1 = new TokenSequence();
        ts1.add("walk");
        ilist.addThruPipe(new Instance(ts1, "verb", null, null));

        TokenSequence ts2 = new TokenSequence();
        ts2.add("run");
        ilist.addThruPipe(new Instance(ts2, "action", null, null));

        HMM hmm = new HMM(inputAlpha, outputAlpha);
        hmm.addFullyConnectedStatesForLabels();
        boolean result = hmm.train(ilist);

        assertTrue(result);
        assertNotNull(hmm.getInitialMultinomial());
        assertEquals(hmm.numStates(), hmm.getTransitionMultinomial().length);
    }
@Test
    public void testAddSelfTransitioningStateForAllLabels() {
        Alphabet inputAlpha = new Alphabet();
        LabelAlphabet outputAlpha = new LabelAlphabet();

        inputAlpha.lookupIndex("word1");
        outputAlpha.lookupIndex("X");
        outputAlpha.lookupIndex("Y");

        HMM hmm = new HMM(inputAlpha, outputAlpha);
        hmm.addSelfTransitioningStateForAllLabels("loopState");

        assertEquals(1, hmm.numStates());
        assertNotNull(hmm.getState("loopState"));
    }
@Test
    public void testOutputAlphabetUsedInFullyConnectedBiLabels() {
        Alphabet inputAlpha = new Alphabet();
        LabelAlphabet outputAlpha = new LabelAlphabet();

        String[] labels = new String[]{"NOUN", "VERB"};
        for (String label : labels) {
            outputAlpha.lookupIndex(label);
        }

        HMM hmm = new HMM(inputAlpha, outputAlpha);
        hmm.addFullyConnectedStatesForBiLabels();

        int expected = labels.length * labels.length;
        assertEquals(expected, hmm.numStates());
    }
@Test
    public void testPrintDoesNotThrow() {
        Alphabet inputAlpha = new Alphabet();
        Alphabet outputAlpha = new LabelAlphabet();

        inputAlpha.lookupIndex("token");
        outputAlpha.lookupIndex("TAG");

        HMM hmm = new HMM(inputAlpha, outputAlpha);
        hmm.addFullyConnectedStatesForLabels();
        hmm.reset();

        try {
            hmm.print();
        } catch (Exception e) {
            fail("Print method threw exception: " + e.getMessage());
        }
    }
@Test
    public void testInitialStateIteratorReturnsCorrectStates() {
        Alphabet inputAlphabet = new Alphabet();
        LabelAlphabet outputAlphabet = new LabelAlphabet();
        inputAlphabet.lookupIndex("token");
        outputAlphabet.lookupIndex("T");

        HMM hmm = new HMM(inputAlphabet, outputAlphabet);
        hmm.addState("start", 1.0, 0.0, new String[]{"start"}, new String[]{"T"});

        Iterator it = hmm.initialStateIterator();
        assertTrue(it.hasNext());

        HMM.State state = (HMM.State) it.next();
        assertEquals("start", state.getName());
    }
@Test
    public void testAllowedTransitionWithPattern() {
        Alphabet inputAlphabet = new Alphabet();
        LabelAlphabet outputAlphabet = new LabelAlphabet();
        outputAlphabet.lookupIndex("X");
        outputAlphabet.lookupIndex("Y");

        HMM hmm = new HMM(inputAlphabet, outputAlphabet);
        Pattern forbidden = Pattern.compile("X,Y");

        boolean result = hmm.addOrderNStates(null,
                new int[]{1}, null, "X", forbidden, null, true).equals("X");

        assertTrue(result);
    } 
}