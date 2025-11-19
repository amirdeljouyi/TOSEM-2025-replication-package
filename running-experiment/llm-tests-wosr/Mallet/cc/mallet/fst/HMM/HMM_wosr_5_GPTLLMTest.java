public class HMM_wosr_5_GPTLLMTest { 

 @Test
    public void testConstructorWithAlphabets() {
        Alphabet inputAlphabet = new Alphabet();
        Alphabet outputAlphabet = new Alphabet();
        inputAlphabet.lookupIndex("a");
        outputAlphabet.lookupIndex("x");
        HMM hmm = new HMM(inputAlphabet, outputAlphabet);

        Assert.assertEquals(inputAlphabet, hmm.getInputAlphabet());
        Assert.assertEquals(outputAlphabet, hmm.getOutputAlphabet());
    }
@Test
    public void testConstructorWithPipe() {
        Alphabet inputAlphabet = new Alphabet();
        Alphabet outputAlphabet = new Alphabet();
        Pipe inputPipe = new Noop(inputAlphabet, outputAlphabet);
        Pipe outputPipe = new Noop(inputAlphabet, outputAlphabet);

        HMM hmm = new HMM(inputPipe, outputPipe);
        Assert.assertEquals(inputAlphabet, hmm.getInputAlphabet());
        Assert.assertEquals(outputAlphabet, hmm.getOutputAlphabet());
    }
@Test
    public void testAddStateAndGet() {
        Alphabet inputAlphabet = new Alphabet();
        Alphabet outputAlphabet = new Alphabet();
        inputAlphabet.lookupIndex("a");
        outputAlphabet.lookupIndex("x");

        HMM hmm = new HMM(inputAlphabet, outputAlphabet);

        String[] destinations = new String[] { "S1" };
        String[] labels = new String[] { "x" };
        hmm.addState("S0", 1.0, 0.5, destinations, labels);
        hmm.addState("S1", new String[] { "S1" });

        HMM.State state = hmm.getState("S0");
        Assert.assertEquals("S0", state.getName());
        Assert.assertEquals(1.0, state.getInitialWeight(), 0.0001);
        Assert.assertEquals(0.5, state.getFinalWeight(), 0.0001);
        Assert.assertEquals("S1", state.getDestinationState(0).getName());
    }
@Test(expected = IllegalArgumentException.class)
    public void testAddStateDuplicateNameThrows() {
        Alphabet inputAlphabet = new Alphabet();
        Alphabet outputAlphabet = new Alphabet();
        inputAlphabet.lookupIndex("a");
        outputAlphabet.lookupIndex("x");

        HMM hmm = new HMM(inputAlphabet, outputAlphabet);
        String[] dest = new String[] { "S1" };
        String[] label = new String[] { "x" };

        hmm.addState("S0", 0.0, 0.0, dest, label);
        hmm.addState("S0", 0.0, 0.0, dest, label); 
    }
@Test
    public void testAddFullyConnectedStates() {
        Alphabet inputAlphabet = new Alphabet();
        Alphabet outputAlphabet = new Alphabet();
        inputAlphabet.lookupIndex("w1");
        outputAlphabet.lookupIndex("L1");
        outputAlphabet.lookupIndex("L2");

        HMM hmm = new HMM(inputAlphabet, outputAlphabet);

        hmm.addFullyConnectedStates(new String[] { "S1", "S2" });

        Assert.assertEquals(2, hmm.numStates());
        Assert.assertNotNull(hmm.getState("S1"));
        Assert.assertNotNull(hmm.getState("S2"));
    }
@Test
    public void testFullyConnectedStatesForLabels() {
        Alphabet inputAlphabet = new Alphabet();
        Alphabet outputAlphabet = new Alphabet();
        inputAlphabet.lookupIndex("w1");
        outputAlphabet.lookupIndex("X");
        outputAlphabet.lookupIndex("Y");

        HMM hmm = new HMM(inputAlphabet, outputAlphabet);
        hmm.addFullyConnectedStatesForLabels();

        Assert.assertEquals(2, hmm.numStates());
        Assert.assertNotNull(hmm.getState("X"));
        Assert.assertNotNull(hmm.getState("Y"));
    }
@Test
    public void testAddStatesForLabelsConnectedAsIn() {
        Alphabet inputAlphabet = new Alphabet();
        Alphabet outputAlphabet = new Alphabet();
        inputAlphabet.lookupIndex("a");
        int x = outputAlphabet.lookupIndex("X");
        int y = outputAlphabet.lookupIndex("Y");

        InstanceList ilist = new InstanceList(new Noop(inputAlphabet, outputAlphabet));
        FeatureSequence data = new FeatureSequence(inputAlphabet, new int[] { 0 });
        FeatureSequence target = new FeatureSequence(outputAlphabet, new int[] { x, y });
        ilist.addThruPipe(new Instance(data, target, null, null));

        HMM hmm = new HMM(inputAlphabet, outputAlphabet);
        hmm.addStatesForLabelsConnectedAsIn(ilist);

        Assert.assertEquals(2, hmm.numStates());
        Assert.assertNotNull(hmm.getState("X"));
        Assert.assertNotNull(hmm.getState("Y"));
    }
@Test
    public void testAddOrderNStatesOrder0() {
        Alphabet inputAlphabet = new Alphabet();
        Alphabet outputAlphabet = new Alphabet();
        inputAlphabet.lookupIndex("tokenA");
        outputAlphabet.lookupIndex("L1");
        outputAlphabet.lookupIndex("L2");

        InstanceList ilist = new InstanceList(new Noop(inputAlphabet, outputAlphabet));
        HMM hmm = new HMM(inputAlphabet, outputAlphabet);
        String start = hmm.addOrderNStates(ilist, null, null, "L1", null, null, true);

        Assert.assertEquals("L1", start);
        Assert.assertEquals(2, hmm.numStates());
        Assert.assertNotNull(hmm.getState("L1"));
        Assert.assertNotNull(hmm.getState("L2"));
    }
@Test
    public void testInitTransitionsAndEmissions() {
        Alphabet inputAlphabet = new Alphabet();
        Alphabet outputAlphabet = new Alphabet();

        inputAlphabet.lookupIndex("i");
        outputAlphabet.lookupIndex("o");

        HMM hmm = new HMM(inputAlphabet, outputAlphabet);
        hmm.addFullyConnectedStates(new String[] { "A" });

        hmm.initTransitions(new Random(42), 0.5);
        hmm.initEmissions(new Random(13), 0.9);

        Assert.assertNotNull(hmm.getInitialMultinomial());
        Assert.assertEquals(1, hmm.getTransitionMultinomial().length);
        Assert.assertEquals(1, hmm.getEmissionMultinomial().length);
    }
@Test
    public void testEstimateAfterInit() {
        Alphabet inputAlphabet = new Alphabet();
        Alphabet outputAlphabet = new Alphabet();

        inputAlphabet.lookupIndex("alpha");
        outputAlphabet.lookupIndex("label");

        HMM hmm = new HMM(inputAlphabet, outputAlphabet);

        hmm.addFullyConnectedStates(new String[] { "Q" });

        hmm.initTransitions(new Random(), 0.2);
        hmm.initEmissions(new Random(), 0.3);
        hmm.estimate();

        Assert.assertNotNull(hmm.getInitialMultinomial());
    }
@Test
    public void testTrainBasic() {
        Alphabet inputAlphabet = new Alphabet();
        Alphabet outputAlphabet = new Alphabet();
        int i = inputAlphabet.lookupIndex("feat");
        int l = outputAlphabet.lookupIndex("TAG");

        InstanceList ilist = new InstanceList(new Noop(inputAlphabet, outputAlphabet));
        FeatureSequence input = new FeatureSequence(inputAlphabet, new int[] { i });
        FeatureSequence target = new FeatureSequence(outputAlphabet, new int[] { l });
        ilist.addThruPipe(new Instance(input, target, null, null));

        HMM hmm = new HMM(inputAlphabet, outputAlphabet);
        hmm.addFullyConnectedStates(new String[] { "TAG" });

        boolean trained = hmm.train(ilist);
        Assert.assertTrue(trained);
        Assert.assertNotNull(hmm.getInitialMultinomial());
    }
@Test
    public void testWriteAndReadObject() throws Exception {
        Alphabet inputAlphabet = new Alphabet();
        Alphabet outputAlphabet = new Alphabet();
        inputAlphabet.lookupIndex("a");
        outputAlphabet.lookupIndex("b");

        HMM hmm = new HMM(inputAlphabet, outputAlphabet);
        hmm.addFullyConnectedStates(new String[] { "Z" });
        hmm.initTransitions(new Random(), 0.5);
        hmm.initEmissions(new Random(), 0.5);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject(hmm);
        out.flush();

        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        ObjectInputStream in = new ObjectInputStream(bis);

        Object obj = in.readObject();
        Assert.assertTrue(obj instanceof HMM);

        HMM deserialized = (HMM) obj;
        Assert.assertEquals(hmm.numStates(), deserialized.numStates());
        Assert.assertEquals(hmm.getState(0).getName(), deserialized.getState(0).getName());
    }
@Test
    public void testAddSelfTransitioningStateForAllLabels() {
        Alphabet inputAlphabet = new Alphabet();
        Alphabet outputAlphabet = new Alphabet();
        inputAlphabet.lookupIndex("input");
        outputAlphabet.lookupIndex("X");
        outputAlphabet.lookupIndex("Y");

        HMM hmm = new HMM(inputAlphabet, outputAlphabet);
        hmm.addSelfTransitioningStateForAllLabels("SELF");

        Assert.assertNotNull(hmm.getState("SELF"));
        Assert.assertEquals(2, hmm.getState("SELF").destinations.length);
    }
@Test
    public void testTransitionIterator() {
        Alphabet inputAlphabet = new Alphabet();
        Alphabet outputAlphabet = new Alphabet();
        int a = inputAlphabet.lookupIndex("f");
        int b = outputAlphabet.lookupIndex("l");

        HMM hmm = new HMM(inputAlphabet, outputAlphabet);
        hmm.addState("X", 1.0, 0.0, new String[] { "X" }, new String[] { "l" });

        hmm.initTransitions(new Random(), 0.1);
        hmm.initEmissions(new Random(), 0.1);

        FeatureSequence inputSeq = new FeatureSequence(inputAlphabet, new int[] { a });
        FeatureSequence targetSeq = new FeatureSequence(outputAlphabet, new int[] { b });

        HMM.State state = hmm.getState("X");

        Transducer.TransitionIterator iterator = state.transitionIterator(inputSeq, 0, targetSeq, 0);

        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals("X", ((HMM.State) iterator.nextState()).getName());
    } 
}