public class HMM_wosr_3_GPTLLMTest { 

 @Test
    public void testConstructorWithAlphabets() {
        Alphabet inputAlphabet = new Alphabet();
        inputAlphabet.lookupIndex("word1");
        Alphabet outputAlphabet = new Alphabet();
        outputAlphabet.lookupIndex("label1");

        HMM hmm = new HMM(inputAlphabet, outputAlphabet);

        Assert.assertNotNull(hmm.getInputAlphabet());
        Assert.assertNotNull(hmm.getOutputAlphabet());
        Assert.assertEquals(1, hmm.getInputAlphabet().size());
        Assert.assertEquals(1, hmm.getOutputAlphabet().size());
    }
@Test
    public void testAddSimpleStateAndRetrieve() {
        Alphabet inputAlphabet = new Alphabet();
        inputAlphabet.lookupIndex("x");
        Alphabet outputAlphabet = new Alphabet();
        outputAlphabet.lookupIndex("POS");

        HMM hmm = new HMM(inputAlphabet, outputAlphabet);
        hmm.addState("POS", new String[]{"POS"});
        HMM.State state = hmm.getState("POS");

        Assert.assertNotNull(state);
        Assert.assertEquals("POS", state.getName());
        Assert.assertEquals(1, hmm.numStates());
    }
@Test
    public void testAddFullyConnectedStatesCreatesCorrectNumber() {
        Alphabet inputAlphabet = new Alphabet();
        inputAlphabet.lookupIndex("data");
        Alphabet outputAlphabet = new Alphabet();
        outputAlphabet.lookupIndex("A");
        outputAlphabet.lookupIndex("B");

        HMM hmm = new HMM(inputAlphabet, outputAlphabet);
        hmm.addFullyConnectedStates(new String[]{"A", "B"});

        Assert.assertEquals(2, hmm.numStates());
        Assert.assertNotNull(hmm.getState("A"));
        Assert.assertNotNull(hmm.getState("B"));
    }
@Test
    public void testResetInitializesMultinomials() {
        Alphabet inputAlphabet = new Alphabet();
        inputAlphabet.lookupIndex("obs1");
        Alphabet outputAlphabet = new Alphabet();
        outputAlphabet.lookupIndex("S");

        HMM hmm = new HMM(inputAlphabet, outputAlphabet);
        hmm.addState("S", new String[]{"S"});
        hmm.reset();

        Assert.assertNotNull(hmm.getEmissionMultinomial());
        Assert.assertNotNull(hmm.getTransitionMultinomial());
        Assert.assertNotNull(hmm.getInitialMultinomial());
        Assert.assertEquals(1, hmm.getEmissionMultinomial().length);
        Assert.assertEquals(1, hmm.getTransitionMultinomial().length);
    }
@Test
    public void testInitTransitionsSetsInitialWeight() {
        Alphabet inputAlphabet = new Alphabet();
        inputAlphabet.lookupIndex("a");
        Alphabet outputAlphabet = new Alphabet();
        outputAlphabet.lookupIndex("L");

        HMM hmm = new HMM(inputAlphabet, outputAlphabet);
        hmm.addState("L", new String[]{"L"});
        hmm.reset();

        hmm.initTransitions(new Random(1234), 1.0);

        HMM.State state = hmm.getState("L");
        double weight = state.getInitialWeight();
        Assert.assertFalse(Double.isNaN(weight));
    }
@Test
    public void testInitEmissionsPopulatesEmissionMultinomials() {
        Alphabet inputAlphabet = new Alphabet();
        inputAlphabet.lookupIndex("a");
        inputAlphabet.lookupIndex("b");
        Alphabet outputAlphabet = new Alphabet();
        outputAlphabet.lookupIndex("S");

        HMM hmm = new HMM(inputAlphabet, outputAlphabet);
        hmm.addState("S", new String[]{"S"});
        hmm.reset();

        hmm.initEmissions(new Random(5678), 1.0);

        Multinomial[] emissions = hmm.getEmissionMultinomial();
        Assert.assertEquals(1, emissions.length);
        Assert.assertNotNull(emissions[0]);
    }
@Test
    public void testAddOrder0StateReturnsStartCorrectly() {
        Alphabet inputAlphabet = new Alphabet();
        inputAlphabet.lookupIndex("x");
        Alphabet outputAlphabet = new Alphabet();
        outputAlphabet.lookupIndex("B");

        HMM hmm = new HMM(inputAlphabet, outputAlphabet);
        InstanceList ilist = new InstanceList(null);

        String startStateName = hmm.addOrderNStates(ilist, null, null, "B", null, null, true);
        Assert.assertEquals("B", startStateName);
        Assert.assertNotNull(hmm.getState("B"));
    }
@Test
    public void testAddOrder1StatesWithFullyConnected() {
        Alphabet inputAlphabet = new Alphabet();
        inputAlphabet.lookupIndex("a");
        Alphabet outputAlphabet = new Alphabet();
        outputAlphabet.lookupIndex("X");
        outputAlphabet.lookupIndex("Y");

        HMM hmm = new HMM(inputAlphabet, outputAlphabet);
        InstanceList ilist = new InstanceList(null);

        String name = hmm.addOrderNStates(ilist, new int[]{1}, new boolean[]{false}, "START", null, null, true);

        Assert.assertNotNull(name);
        Assert.assertEquals(4, hmm.numStates()); 
    }
@Test
    public void testAddStateThrowsForDuplicateName() {
        Alphabet inputAlphabet = new Alphabet();
        inputAlphabet.lookupIndex("token");
        Alphabet outputAlphabet = new Alphabet();
        outputAlphabet.lookupIndex("TAG");

        HMM hmm = new HMM(inputAlphabet, outputAlphabet);
        hmm.addState("TAG", new String[]{"TAG"});

        try {
            hmm.addState("TAG", new String[]{"TAG"});
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("already exists"));
        }
    }
@Test
    public void testTransitionIteratorCorrectInitialization() {
        Alphabet inputAlphabet = new Alphabet();
        int index = inputAlphabet.lookupIndex("word");
        Alphabet outputAlphabet = new Alphabet();
        outputAlphabet.lookupIndex("POS");

        HMM hmm = new HMM(inputAlphabet, outputAlphabet);
        hmm.addState("POS", new String[]{"POS"});
        hmm.reset();
        hmm.initEmissions(new Random(), 0.0);
        hmm.initTransitions(new Random(), 0.0);

        FeatureSequence inputSeq = new FeatureSequence(inputAlphabet);
        inputSeq.add("word");
        FeatureSequence outputSeq = new FeatureSequence(outputAlphabet);
        outputSeq.add("POS");

        HMM.State state = hmm.getState("POS");
        Transducer.TransitionIterator ti = state.transitionIterator(inputSeq, 0, outputSeq, 0);

        Assert.assertTrue(ti.hasNext());
        Transducer.State next = ti.nextState();
        Assert.assertNotNull(next);
        Assert.assertEquals("POS", next.getName());
        Assert.assertEquals("POS", ti.getOutput());
        Assert.assertEquals(index, ((Integer) ti.getInput()).intValue());
    }
@Test
    public void testTrainReturnsTrueForSimpleList() {
        Alphabet inputAlphabet = new Alphabet();
        Alphabet outputAlphabet = new LabelAlphabet();
        inputAlphabet.lookupIndex("hello");
        outputAlphabet.lookupIndex("GREETING");

        Pipe pipe = new Pipe(inputAlphabet, outputAlphabet) {
            public Instance pipe(Instance carrier) { return carrier; }
        };

        HMM hmm = new HMM(pipe, pipe);
        hmm.addState("GREETING", new String[]{"GREETING"});

        InstanceList train = new InstanceList(pipe);

        FeatureSequence input = new FeatureSequence(inputAlphabet, new String[]{"hello", "hello"});
        FeatureSequence output = new FeatureSequence(outputAlphabet, new String[]{"GREETING", "GREETING"});

        train.addThruPipe(new Instance(input, output, null, null));

        hmm.reset();

        boolean result = hmm.train(train);

        Assert.assertTrue(result);
        Assert.assertNotNull(hmm.getEmissionMultinomial()[0]);
        Assert.assertNotNull(hmm.getTransitionMultinomial()[0]);
    }
@Test
    public void testAddStatesForBiLabelsConnectedAsIn() {
        Alphabet inputAlphabet = new Alphabet();
        inputAlphabet.lookupIndex("feature");

        Alphabet outputAlphabet = new LabelAlphabet();
        outputAlphabet.lookupIndex("X");
        outputAlphabet.lookupIndex("Y");

        Pipe pipe = new Pipe(inputAlphabet, outputAlphabet) {
            public Instance pipe(Instance carrier) { return carrier; }
        };

        HMM hmm = new HMM(pipe, pipe);

        InstanceList data = new InstanceList(pipe);
        FeatureSequence input = new FeatureSequence(inputAlphabet, new String[]{"feature", "feature"});
        FeatureSequence output = new FeatureSequence(outputAlphabet, new String[]{"X", "Y"});
        data.addThruPipe(new Instance(input, output, null, null));

        hmm.addStatesForBiLabelsConnectedAsIn(data);

        Assert.assertTrue(hmm.numStates() > 0);
        Assert.assertNotNull(hmm.getState("X,Y"));
    }
@Test
    public void testAddSelfTransitioningStateForAllLabels() {
        Alphabet inputAlphabet = new Alphabet();
        inputAlphabet.lookupIndex("sample");

        Alphabet outputAlphabet = new Alphabet();
        outputAlphabet.lookupIndex("L1");
        outputAlphabet.lookupIndex("L2");

        HMM hmm = new HMM(inputAlphabet, outputAlphabet);
        hmm.addSelfTransitioningStateForAllLabels("SharedState");

        HMM.State state = hmm.getState("SharedState");

        Assert.assertNotNull(state);
        Assert.assertEquals("SharedState", state.getName());
        Transducer.State dest = state.getDestinationState(0);
        Assert.assertNotNull(dest);
        Assert.assertEquals("SharedState", dest.getName());
    }
@Test
    public void testAddStatesForHalfLabelsConnectedAsIn() {
        Alphabet inputAlphabet = new Alphabet();
        inputAlphabet.lookupIndex("obs");

        LabelAlphabet outputAlphabet = new LabelAlphabet();
        outputAlphabet.lookupIndex("A");
        outputAlphabet.lookupIndex("B");

        Pipe pipe = new Pipe(inputAlphabet, outputAlphabet) {
            public Instance pipe(Instance carrier) { return carrier; }
        };

        HMM hmm = new HMM(pipe, pipe);
        InstanceList train = new InstanceList(pipe);
        FeatureSequence fsInput = new FeatureSequence(inputAlphabet, new String[]{"obs", "obs"});
        FeatureSequence fsTarget = new FeatureSequence(outputAlphabet, new String[]{"A", "B"});
        train.addThruPipe(new Instance(fsInput, fsTarget, null, null));

        hmm.addStatesForHalfLabelsConnectedAsIn(train);
        Assert.assertTrue(hmm.numStates() > 0);
        HMM.State state = hmm.getState("A");
        Assert.assertNotNull(state);
        Assert.assertNotNull(state.getDestinationState(0));
    }
@Test
    public void testAllowedTransitionWithPattern() {
        Alphabet inputAlphabet = new Alphabet();
        Alphabet outputAlphabet = new Alphabet();
        outputAlphabet.lookupIndex("a");
        outputAlphabet.lookupIndex("b");

        HMM hmm = new HMM(inputAlphabet, outputAlphabet);

        Pattern forbidden = Pattern.compile("a,b");

        boolean allowed = hmm.allowedTransition("a", "b", forbidden, null);
        Assert.assertFalse(allowed);

        allowed = hmm.allowedTransition("b", "a", forbidden, null);
        Assert.assertTrue(allowed);
    } 
}