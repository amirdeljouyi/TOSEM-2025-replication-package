public class CRF_wosr_5_GPTLLMTest { 

 @Test
    public void testCRFConstructorUsingAlphabets() {
        Alphabet inputAlphabet = new Alphabet();
        Alphabet outputAlphabet = new LabelAlphabet();
        inputAlphabet.lookupIndex("token1");
        outputAlphabet.lookupIndex("label1");

        CRF crf = new CRF(inputAlphabet, outputAlphabet);

        assertEquals(inputAlphabet, crf.getInputAlphabet());
        assertEquals(outputAlphabet, crf.getOutputAlphabet());
        assertNotNull(crf.getParameters());
        assertTrue(crf.getParameters().weights == null);
    }
@Test
    public void testAddStateMinimal() {
        Alphabet inputAlphabet = new Alphabet();
        Alphabet outputAlphabet = new LabelAlphabet();
        inputAlphabet.lookupIndex("token");
        outputAlphabet.lookupIndex("label");

        CRF crf = new CRF(inputAlphabet, outputAlphabet);
        crf.addState("state1", new String[] { "state1" });

        assertEquals(1, crf.numStates());
        assertNotNull(crf.getState("state1"));
        assertEquals("state1", crf.getState("state1").getName());
    }
@Test
    public void testAddStateWithFullParams() {
        Alphabet inputAlphabet = new Alphabet();
        Alphabet outputAlphabet = new LabelAlphabet();
        inputAlphabet.lookupIndex("tok");
        outputAlphabet.lookupIndex("A");
        outputAlphabet.lookupIndex("B");

        CRF crf = new CRF(inputAlphabet, outputAlphabet);

        String[] destinations = new String[] { "s2", "s3" };
        String[] labels = new String[] { "A", "B" };
        String[][] weightNames = new String[][] {
                {"w1"}, {"w2"}
        };

        crf.addState("s2", 0.0, 0.0, new String[] {}, new String[] {});
        crf.addState("s3", 0.0, 0.0, new String[] {}, new String[] {});
        crf.addState("s1", 0.0, 0.0, destinations, labels, weightNames);

        assertEquals(3, crf.numStates());
        assertEquals("s1", crf.getState("s1").getName());
    }
@Test
    public void testSetAndGetWeights() {
        Alphabet inputAlphabet = new Alphabet();
        Alphabet outputAlphabet = new LabelAlphabet();
        inputAlphabet.lookupIndex("feature");
        outputAlphabet.lookupIndex("label");

        CRF crf = new CRF(inputAlphabet, outputAlphabet);
        crf.addState("s1", new String[] { "s1" });

        int widx = crf.getWeightsIndex("s1->s1:label");
        SparseVector vec = new SparseVector(new int[] {0}, new double[] {1.5}, 1, 1);
        crf.setWeights(widx, vec);

        assertEquals(1.5, crf.getWeights(widx).value(0), 0.0001);
    }
@Test
    public void testFreezeAndUnfreezeWeights() {
        Alphabet inputAlphabet = new Alphabet();
        Alphabet outputAlphabet = new LabelAlphabet();
        inputAlphabet.lookupIndex("token");
        outputAlphabet.lookupIndex("lab");

        CRF crf = new CRF(inputAlphabet, outputAlphabet);
        crf.addState("s1", new String[] { "s1" });

        int widx = crf.getWeightsIndex("s1->s1:lab");

        crf.freezeWeights(widx);
        assertTrue(crf.isWeightsFrozen(widx));

        crf.unfreezeWeights("s1->s1:lab");
        assertFalse(crf.isWeightsFrozen(widx));
    }
@Test
    public void testAddStatesForLabelsConnectedAsIn() {
        Alphabet inputAlphabet = new Alphabet();
        Alphabet outputAlphabet = new LabelAlphabet();
        inputAlphabet.lookupIndex("tok1");
        outputAlphabet.lookupIndex("X");
        outputAlphabet.lookupIndex("Y");

        Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
        InstanceList instanceList = new InstanceList(pipe);

        FeatureVector fv1 = new FeatureVector(inputAlphabet, new int[] {0});
        FeatureVector fv2 = new FeatureVector(inputAlphabet, new int[] {0});
        FeatureVectorSequence fvs = new FeatureVectorSequence(new FeatureVector[] {fv1, fv2});
        FeatureVectorSequence labelSeq = new FeatureVectorSequence(new Object[] {"X", "Y"});

        Instance inst = new Instance(fvs, labelSeq, null, null);
        instanceList.add(inst);

        CRF crf = new CRF(inputAlphabet, outputAlphabet);
        crf.addStatesForLabelsConnectedAsIn(instanceList);

        assertEquals(2, crf.numStates());
        assertNotNull(crf.getState("X"));
        assertNotNull(crf.getState("Y"));
    }
@Test
    public void testAddOrderNStatesZero() {
        Alphabet inputAlphabet = new Alphabet();
        Alphabet outputAlphabet = new LabelAlphabet();
        inputAlphabet.lookupIndex("foo");
        outputAlphabet.lookupIndex("A");
        outputAlphabet.lookupIndex("B");

        Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
        InstanceList ilist = new InstanceList(pipe);

        CRF crf = new CRF(inputAlphabet, outputAlphabet);
        String startStateName = crf.addOrderNStates(ilist, null, null, null, null, null, true);

        assertEquals(2, crf.numStates());
        assertNull(startStateName);
    }
@Test
    public void testWeightsParameterSettingAndRetrieval() {
        Alphabet inputAlphabet = new Alphabet();
        Alphabet outputAlphabet = new LabelAlphabet();
        inputAlphabet.lookupIndex("f");
        outputAlphabet.lookupIndex("L1");
        outputAlphabet.lookupIndex("L2");

        CRF crf = new CRF(inputAlphabet, outputAlphabet);
        crf.addState("state1", 0.0, 0.0, new String[] {"state2"}, new String[] {"L1"});
        crf.addState("state2", 0.0, 0.0, new String[] {}, new String[] {});
        int featureIndex = inputAlphabet.lookupIndex("f");

        crf.setParameter(0, 1, featureIndex, 2.5);
        assertEquals(2.5, crf.getParameter(0, 1, featureIndex), 0.00001);
    }
@Test
    public void testGetNumParametersIncrementsWhenStructureChanges() {
        Alphabet inputAlphabet = new Alphabet();
        Alphabet outputAlphabet = new LabelAlphabet();
        inputAlphabet.lookupIndex("f");
        outputAlphabet.lookupIndex("L");

        CRF crf = new CRF(inputAlphabet, outputAlphabet);
        int original = crf.getNumParameters();

        crf.addState("s1", new String[]{"s1"});
        int after = crf.getNumParameters();

        assertTrue(after > original);
    }
@Test
    public void testSerializationCycle() throws Exception {
        Alphabet inputAlphabet = new Alphabet();
        Alphabet outputAlphabet = new LabelAlphabet();
        inputAlphabet.lookupIndex("foo");
        outputAlphabet.lookupIndex("BAR");

        CRF crf = new CRF(inputAlphabet, outputAlphabet);
        crf.addState("start", new String[] { "start" });

        java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();
        java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(bos);
        oos.writeObject(crf);
        oos.flush();
        byte[] serialized = bos.toByteArray();

        java.io.ByteArrayInputStream bis = new java.io.ByteArrayInputStream(serialized);
        java.io.ObjectInputStream ois = new java.io.ObjectInputStream(bis);
        CRF deserialized = (CRF) ois.readObject();

        assertEquals(crf.numStates(), deserialized.numStates());
        assertEquals("start", deserialized.getState("start").getName());
    }
@Test
    public void testAddOrderNStatesForbidden() {
        Alphabet inputAlphabet = new Alphabet();
        Alphabet outputAlphabet = new LabelAlphabet();
        inputAlphabet.lookupIndex("feat");
        outputAlphabet.lookupIndex("A");
        outputAlphabet.lookupIndex("B");

        Pipe pipe = new Noop(inputAlphabet, outputAlphabet);
        InstanceList list = new InstanceList(pipe);
        CRF crf = new CRF(inputAlphabet, outputAlphabet);

        int[] orders = new int[] { 1 };
        boolean[] defaults = new boolean[] { false };
        Pattern forbidden = Pattern.compile("A,B");

        crf.addOrderNStates(list, orders, defaults, null, forbidden, null, true);
        assertTrue(crf.numStates() > 0);

        for (int i = 0; i < crf.numStates(); i++) {
            CRF.State s = (CRF.State) crf.getState(i);
            if (s.getName().equals("A")) {
                for (int j = 0; j < s.numDestinations(); j++) {
                    assertNotEquals("B", s.getDestinationState(j).getName());
                }
            }
        }
    } 
}