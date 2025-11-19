public class MaxEnt_5_GPTLLMTest { 

 @Test
    public void testConstructorWithFeatureSelectionOnly() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("f0");
        dataAlphabet.lookupIndex("f1");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("l0");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        FeatureSelection featureSelection = mock(FeatureSelection.class);

        MaxEnt me = new MaxEnt(pipe, null, featureSelection);

        assertEquals(featureSelection, me.getFeatureSelection());
        assertNull(me.getPerClassFeatureSelection());
    }
@Test
    public void testConstructorWithPerClassFeatureSelectionOnly() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("f0");
        dataAlphabet.lookupIndex("f1");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("l0");
        labelAlphabet.lookupIndex("l1");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        FeatureSelection fs0 = mock(FeatureSelection.class);
        FeatureSelection fs1 = null;

        FeatureSelection[] perClass = new FeatureSelection[] { fs0, fs1 };

        MaxEnt me = new MaxEnt(pipe, null, perClass);

        assertArrayEquals(perClass, me.getPerClassFeatureSelection());
        assertNull(me.getFeatureSelection());
    }
@Test
    public void testSetAndGetParameters() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("f0");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("l0");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        double[] initialParams = new double[2];
        initialParams[0] = 0.5;
        initialParams[1] = -0.2;

        MaxEnt me = new MaxEnt(pipe, initialParams);

        double[] newParams = new double[2];
        newParams[0] = 1.9;
        newParams[1] = -1.1;

        me.setParameters(newParams);

        assertArrayEquals(newParams, me.getParameters(), 1e-6);
    }
@Test
    public void testSetParameterModifiesCorrectIndex() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("f0");
        dataAlphabet.lookupIndex("f1");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("l0");
        labelAlphabet.lookupIndex("l1");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        double[] params = new double[(2 + 1) * 2];
        Arrays.fill(params, 0.0);

        MaxEnt me = new MaxEnt(pipe, params);

        me.setParameter(1, 1, 3.14);

        double[] result = me.getParameters();

        assertEquals(3.14, result[1 * 3 + 1], 1e-6);
    }
@Test
    public void testGetUnnormalizedClassificationScores() {
        Alphabet dataAlphabet = new Alphabet();
        int f0 = dataAlphabet.lookupIndex("f0");
        int f1 = dataAlphabet.lookupIndex("f1");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        int l0 = labelAlphabet.lookupIndex("l0");
        int l1 = labelAlphabet.lookupIndex("l1");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        double[] params = new double[(2 + 1) * 2];
        params[0] = 0.1;
        params[1] = 0.2;
        params[2] = 0.3;
        params[3] = 0.4;
        params[4] = 0.5;
        params[5] = 0.6;

        MaxEnt me = new MaxEnt(pipe, params);

        int[] indices = new int[] { f0, f1 };
        double[] values = new double[] { 1.0, 2.0 };
        FeatureVector fv = new FeatureVector(dataAlphabet, indices, values);
        Instance instance = new Instance(fv, null, null, null);

        double[] scores = new double[2];
        me.getUnnormalizedClassificationScores(instance, scores);

        assertEquals(0.1 + (1.0 * 0.1 + 2.0 * 0.2), scores[0], 1e-6);
        assertEquals(0.4 + (1.0 * 0.4 + 2.0 * 0.5), scores[1], 1e-6);
    }
@Test
    public void testGetClassificationScores() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("f0");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("l0");
        labelAlphabet.lookupIndex("l1");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        double[] params = new double[6];
        params[0] = 0.0;
        params[1] = 1.0;
        params[2] = 0.0;
        params[3] = -1.0;
        params[4] = 0.0;
        params[5] = -1.0;

        MaxEnt me = new MaxEnt(pipe, params);

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
        Instance instance = new Instance(fv, null, null, null);

        double[] scores = new double[2];
        me.getClassificationScores(instance, scores);

        double sum = scores[0] + scores[1];

        assertEquals(1.0, sum, 1e-6);
        assertTrue(scores[0] > 0.0);
        assertTrue(scores[1] > 0.0);
    }
@Test
    public void testClassificationOutput() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("f0");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("spam");
        labelAlphabet.lookupIndex("ham");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        double[] params = new double[6];
        params[0] = 0.0;
        params[1] = 0.5;
        params[2] = 0.0;
        params[3] = -0.5;
        params[4] = 0.0;
        params[5] = 0.0;

        MaxEnt me = new MaxEnt(pipe, params);

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
        Instance instance = new Instance(fv, null, null, null);

        Classification classification = me.classify(instance);
        Labeling labeling = classification.getLabeling();

        double value0 = labeling.value(0);
        double value1 = labeling.value(1);

        assertEquals(1.0, value0 + value1, 1e-6);
    }
@Test
    public void testSerializationAndDeserialization() throws Exception {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("f0");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("l0");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        double[] parameters = new double[2];
        parameters[0] = 5.55;
        parameters[1] = 6.66;

        MaxEnt original = new MaxEnt(pipe, parameters);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(baos);
        out.writeObject(original);
        out.flush();

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream in = new ObjectInputStream(bais);
        MaxEnt restored = (MaxEnt) in.readObject();

        assertNotNull(restored);
        assertEquals(5.55, restored.getParameters()[0], 1e-6);
        assertEquals(6.66, restored.getParameters()[1], 1e-6);
    }
@Test
    public void testPrintDoesNotThrow() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("f0");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("c0");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        double[] params = new double[2];
        MaxEnt me = new MaxEnt(pipe, params);

        PrintWriter writer = new PrintWriter(System.out, true);
        me.print(writer);
    }
@Test
    public void testPrintRankDoesNotThrow() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("feature1");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("label1");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        double[] params = new double[2];
        MaxEnt model = new MaxEnt(pipe, params);

        PrintWriter writer = new PrintWriter(System.out, true);
        model.printRank(writer);
    }
@Test
    public void testPrintExtremeFeaturesDoesNotThrow() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("feature1");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("label1");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        double[] params = new double[2];
        MaxEnt model = new MaxEnt(pipe, params);

        PrintWriter writer = new PrintWriter(System.out, true);
        model.printExtremeFeatures(writer, 1);
    }
@Test
    public void testGetNumParametersWhenPipeHasZeroFeaturesAndZeroLabels() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        int expected = 0;
        int actual = MaxEnt.getNumParameters(pipe);
        assertEquals(expected, actual);
    }
@Test
    public void testClassificationScoresWithAllZeroWeights() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("f0");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("c0");
        labelAlphabet.lookupIndex("c1");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        double[] parameters = new double[6];
        for (int i = 0; i < 6; i++) {
            parameters[i] = 0.0;
        }

        MaxEnt model = new MaxEnt(pipe, parameters);

        int[] indices = new int[] { 0 };
        double[] values = new double[] { 1.0 };
        FeatureVector fv = new FeatureVector(dataAlphabet, indices, values);
        Instance instance = new Instance(fv, null, null, null);

        double[] scores = new double[2];
        model.getClassificationScores(instance, scores);

        assertEquals(0.5, scores[0], 1e-6);
        assertEquals(0.5, scores[1], 1e-6);
    }
@Test
    public void testClassificationWithZeroTemperatureShouldThrowArithmeticException() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("f0");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("l0");
        labelAlphabet.lookupIndex("l1");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        double[] parameters = new double[6];
        MaxEnt model = new MaxEnt(pipe, parameters);

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1.0 });
        Instance instance = new Instance(fv, null, null, null);

        double[] scores = new double[2];

        try {
            model.getClassificationScoresWithTemperature(instance, 0.0, scores);
            fail("Expected ArithmeticException due to division by zero temperature");
        } catch (ArithmeticException e) {
            assertTrue(e.getMessage().contains("/ by zero"));
        }
    }
@Test
    public void testSetAndGetDefaultFeatureIndex() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        dataAlphabet.lookupIndex("f0");
        dataAlphabet.lookupIndex("f1");

        labelAlphabet.lookupIndex("c0");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        MaxEnt model = new MaxEnt(pipe, new double[3]);

        model.setDefaultFeatureIndex(7);
        int index = model.getDefaultFeatureIndex();

        assertEquals(7, index);
    }
@Test
    public void testPrintWithMultipleFeaturesAndClasses() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("f0");
        dataAlphabet.lookupIndex("f1");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("label1");
        labelAlphabet.lookupIndex("label2");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        double[] parameters = new double[(2 + 1) * 2];
        for (int i = 0; i < parameters.length; i++) {
            parameters[i] = i * 0.1;
        }

        MaxEnt model = new MaxEnt(pipe, parameters);

        PrintWriter writer = new PrintWriter(System.out, true);
        model.print(writer);
    }
@Test
    public void testSetFeatureSelectionReturnsSameInstance() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("f0");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("label");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        MaxEnt model = new MaxEnt(pipe, new double[2]);

        FeatureSelection fs = mock(FeatureSelection.class);
        MaxEnt returned = model.setFeatureSelection(fs);

        assertSame(model, returned);
        assertEquals(fs, model.getFeatureSelection());
    }
@Test
    public void testSetPerClassFeatureSelectionReturnsSameInstance() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("f0");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("c0");
        labelAlphabet.lookupIndex("c1");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        MaxEnt model = new MaxEnt(pipe, new double[6]);

        FeatureSelection fs0 = mock(FeatureSelection.class);
        FeatureSelection fs1 = null;

        FeatureSelection[] array = new FeatureSelection[] { fs0, fs1 };

        MaxEnt out = model.setPerClassFeatureSelection(array);

        assertSame(model, out);
        assertArrayEquals(array, model.getPerClassFeatureSelection());
    }
@Test
    public void testDeserializationWithNullFeatureSelections() throws Exception {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("f1");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("cat");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        MaxEnt model = new MaxEnt(pipe, new double[2]);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject(model);
        out.close();

        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        ObjectInputStream in = new ObjectInputStream(bis);
        MaxEnt restored = (MaxEnt) in.readObject();

        assertNotNull(restored.getParameters());
        assertNull(restored.getFeatureSelection());
        assertNull(restored.getPerClassFeatureSelection());
    }
@Test
    public void testClassificationWithMismatchedAlphabetThrowsAssertionError() {
        Alphabet dataAlphabetTrain = new Alphabet();
        dataAlphabetTrain.lookupIndex("f0");
        dataAlphabetTrain.lookupIndex("f1");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("class1");
        labelAlphabet.lookupIndex("class2");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabetTrain);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        double[] parameters = new double[(2 + 1) * 2];
        MaxEnt model = new MaxEnt(pipe, parameters);

        Alphabet dataAlphabetTest = new Alphabet();
        dataAlphabetTest.lookupIndex("fX");

        int[] indices = new int[] { 0 };
        double[] values = new double[] { 1.0 };
        FeatureVector fv = new FeatureVector(dataAlphabetTest, indices, values);
        Instance instance = new Instance(fv, null, null, null);

        double[] scores = new double[2];

        try {
            model.getUnnormalizedClassificationScores(instance, scores);
            fail("Expected AssertionError because of mismatched alphabets");
        } catch (AssertionError expected) {
            assertTrue(true);
        }
    }
@Test
    public void testZeroLabelsShouldHaveZeroScores() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("f0");

        LabelAlphabet labelAlphabet = new LabelAlphabet();

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        FeatureVector featureVector = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{1.0});
        Instance instance = new Instance(featureVector, null, null, null);

        MaxEnt model = new MaxEnt(pipe, new double[0]);

        double[] scores = new double[0];

        model.getClassificationScores(instance, scores);

        
        assertEquals(0, scores.length);
    }
@Test
    public void testSingleFeatureSingleLabel() {
        Alphabet dataAlphabet = new Alphabet();
        int f0 = dataAlphabet.lookupIndex("feature0");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        int l0 = labelAlphabet.lookupIndex("label0");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        double[] parameters = new double[2];
        parameters[f0] = 0.7;
        parameters[1] = 0.5; 

        MaxEnt model = new MaxEnt(pipe, parameters);

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{f0}, new double[]{1.0});
        Instance instance = new Instance(fv, null, null, null);

        double[] scores = new double[1];
        model.getClassificationScores(instance, scores);

        assertEquals(1.0, scores[0], 1e-6);
    }
@Test
    public void testExtremeFeatureIndexHandlingWithUnsetDataAlphabet() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("f0");

        LabelAlphabet targetAlphabet = new LabelAlphabet();
        targetAlphabet.lookupIndex("c");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(targetAlphabet);

        MaxEnt model = new MaxEnt(pipe, new double[2]);

        model.setDefaultFeatureIndex(15);
        assertEquals(15, model.getDefaultFeatureIndex());
    }
@Test
    public void testClassificationWithEmptyFeatureVector() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("f0");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("a");
        labelAlphabet.lookupIndex("b");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        double[] parameters = new double[6]; 

        MaxEnt model = new MaxEnt(pipe, parameters);

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{}, new double[]{});
        Instance instance = new Instance(fv, null, null, null);

        double[] scores = new double[2];
        model.getClassificationScores(instance, scores);

        assertEquals(1.0, scores[0] + scores[1], 1e-6);
    }
@Test
    public void testGetNumParametersWithLargerFeatureSet() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("a");
        dataAlphabet.lookupIndex("b");
        dataAlphabet.lookupIndex("c");
        dataAlphabet.lookupIndex("d");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("A");
        labelAlphabet.lookupIndex("B");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        int numParameters = MaxEnt.getNumParameters(pipe);
        assertEquals((4 + 1) * 2, numParameters);
    }
@Test
    public void testPrintWithEmptyLabelAlphabet() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("f0");

        LabelAlphabet labelAlphabet = new LabelAlphabet();

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        double[] parameters = new double[0];
        MaxEnt model = new MaxEnt(pipe, parameters);

        PrintWriter pw = new PrintWriter(System.out);
        model.print(pw);

        
        assertTrue(true);
    }
@Test
    public void testSetNullPerClassFeatureSelection() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("f0");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("L1");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        double[] params = new double[2];

        MaxEnt model = new MaxEnt(pipe, params);

        model.setPerClassFeatureSelection(null);

        assertNull(model.getPerClassFeatureSelection());
    }
@Test
    public void testSetNullFeatureSelection() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("token");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("label");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        MaxEnt me = new MaxEnt(pipe, new double[2]);

        MaxEnt returned = me.setFeatureSelection(null);
        assertSame(me, returned);
        assertNull(returned.getFeatureSelection());
    }
@Test
    public void testEmptyFeatureSelectionAndEmptyPerClassArraySerialization() throws Exception {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("f0");

        LabelAlphabet targetAlphabet = new LabelAlphabet();
        targetAlphabet.lookupIndex("y");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(targetAlphabet);

        double[] params = new double[2];

        MaxEnt me = new MaxEnt(pipe, params);
        me.setFeatureSelection(null);
        me.setPerClassFeatureSelection(new FeatureSelection[] { null });

        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
        objOut.writeObject(me);
        objOut.close();

        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        ObjectInputStream objIn = new ObjectInputStream(byteIn);
        MaxEnt deserialized = (MaxEnt) objIn.readObject();

        assertNotNull(deserialized);
        assertEquals(2, deserialized.getParameters().length);
    }
@Test
    public void testClassificationScoresWithSmallTemperature() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("x");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("A");
        labelAlphabet.lookupIndex("B");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        MaxEnt model = new MaxEnt(pipe, new double[6]);

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1 });
        Instance instance = new Instance(fv, null, null, null);

        double[] scores = new double[2];
        model.getClassificationScoresWithTemperature(instance, 0.01, scores);

        assertEquals(1.0, scores[0] + scores[1], 1e-6);
    }
@Test
    public void testClassificationScoresWithHighTemperature() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("x");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("A");
        labelAlphabet.lookupIndex("B");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        MaxEnt model = new MaxEnt(pipe, new double[6]);

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[] { 0 }, new double[] { 1 });
        Instance instance = new Instance(fv, null, null, null);

        double[] scores = new double[2];
        model.getClassificationScoresWithTemperature(instance, 1000.0, scores);

        assertEquals(1.0, scores[0] + scores[1], 1e-6);
        assertTrue(scores[0] > 0);
        assertTrue(scores[1] > 0);
    }
@Test
    public void testParametersNotInitializedInConstructorDefaultsToComputedSize() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("f0");
        dataAlphabet.lookupIndex("f1");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("l0");
        labelAlphabet.lookupIndex("l1");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        MaxEnt model = new MaxEnt(pipe, null);

        int expectedParamSize = (2 + 1) * 2;
        double[] parameters = model.getParameters();

        assertEquals(expectedParamSize, parameters.length);
    }
@Test
    public void testUnnormalizedClassificationWithNullFeatureSelection() {
        Alphabet dataAlphabet = new Alphabet();
        int idx0 = dataAlphabet.lookupIndex("x");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("a");
        labelAlphabet.lookupIndex("b");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        double[] parameters = new double[6];
        parameters[0] = 1.0; 
        parameters[1] = 0.0; 
        parameters[3] = -1.0; 
        parameters[4] = 0.0;  

        MaxEnt model = new MaxEnt(pipe, parameters, (FeatureSelection) null);

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{idx0}, new double[]{2.0});
        Instance instance = new Instance(fv, null, null, null);
        double[] scores = new double[2];

        model.getUnnormalizedClassificationScores(instance, scores);

        assertEquals(2.0, scores[0], 1e-6);
        assertEquals(-2.0, scores[1], 1e-6);
    }
@Test
    public void testUnnormalizedClassificationWithPerClassFeatureSelection() {
        Alphabet dataAlphabet = new Alphabet();
        int f0 = dataAlphabet.lookupIndex("x");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("yes");
        labelAlphabet.lookupIndex("no");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        double[] parameters = new double[6];
        parameters[0] = 1.0; 
        parameters[1] = 0.0; 
        parameters[3] = 1.0; 
        parameters[4] = 0.0; 

        FeatureSelection fs0 = mock(FeatureSelection.class);
        when(fs0.contains(0)).thenReturn(true);

        FeatureSelection fs1 = mock(FeatureSelection.class);
        when(fs1.contains(0)).thenReturn(false); 

        FeatureSelection[] perClass = new FeatureSelection[]{fs0, fs1};

        MaxEnt model = new MaxEnt(pipe, parameters, perClass);

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{f0}, new double[]{2.0});
        Instance instance = new Instance(fv, null, null, null);
        double[] scores = new double[2];

        model.getUnnormalizedClassificationScores(instance, scores);

        assertEquals(2.0, scores[0], 1e-6);
        assertEquals(0.0, scores[1], 1e-6);
    }
@Test
    public void testZeroLengthFeatureVectorWithDefaultFeatureWeightOnly() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("f0");
        Alphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("A");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        double[] parameters = new double[2];
        parameters[1] = 2.5; 

        MaxEnt model = new MaxEnt(pipe, parameters);

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{}, new double[]{});
        Instance instance = new Instance(fv, null, null, null);
        double[] scores = new double[1];

        model.getUnnormalizedClassificationScores(instance, scores);

        assertEquals(2.5, scores[0], 1e-6);
    }
@Test
    public void testMatrixOpsRowDotProductWithFeatureSelectionExcludingAll() {
        Alphabet dataAlphabet = new Alphabet();
        int i0 = dataAlphabet.lookupIndex("w1");
        int i1 = dataAlphabet.lookupIndex("w2");

        Alphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("Y");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        double[] parameters = new double[3]; 
        parameters[0] = 1.0;
        parameters[1] = 2.0;
        parameters[2] = 0.5;

        FeatureSelection fs = mock(FeatureSelection.class);
        when(fs.contains(0)).thenReturn(false);
        when(fs.contains(1)).thenReturn(false);

        MaxEnt model = new MaxEnt(pipe, parameters, fs);

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0, 1}, new double[]{3.0, 4.0});
        Instance instance = new Instance(fv, null, null, null);
        double[] scores = new double[1];

        model.getUnnormalizedClassificationScores(instance, scores);

        assertEquals(0.5, scores[0], 1e-6); 
    }
@Test
    public void testGetClassificationScoresWithNaNScoresDoesNotThrow() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("x");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("A");
        labelAlphabet.lookupIndex("B");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        double[] params = new double[6];
        params[1] = Double.POSITIVE_INFINITY;
        params[4] = Double.NEGATIVE_INFINITY;

        MaxEnt model = new MaxEnt(pipe, params);

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{1.0});
        Instance instance = new Instance(fv, null, null, null);
        double[] scores = new double[2];

        model.getClassificationScores(instance, scores);

        double sum = scores[0] + scores[1];
        assertEquals(1.0, sum, 1e-6);
    }
@Test
    public void testPrintRankWithZeroWeights() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("zero1");
        dataAlphabet.lookupIndex("zero2");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("neutral");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        double[] params = new double[3];
        params[0] = 0.0;
        params[1] = 0.0;
        params[2] = 0.0;

        MaxEnt model = new MaxEnt(pipe, params);

        PrintWriter out = new PrintWriter(System.out);
        model.printRank(out);

        assertTrue(true); 
    }
@Test
    public void testPrintExtremeFeaturesWithTop1() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("pos");
        dataAlphabet.lookupIndex("neg");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("SENT");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        double[] params = new double[3];
        params[0] = 5.0;   
        params[1] = -3.0;  
        params[2] = 0.0;

        MaxEnt model = new MaxEnt(pipe, params);

        PrintWriter out = new PrintWriter(System.out);
        model.printExtremeFeatures(out, 1);

        assertTrue(true); 
    }
@Test
    public void testSerializationWithNonNullFeatureSelection() throws Exception {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("fA");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("labelA");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        FeatureSelection fs = mock(FeatureSelection.class);

        double[] params = new double[2];
        MaxEnt model = new MaxEnt(pipe, params, fs);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject(model);
        out.close();

        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        ObjectInputStream in = new ObjectInputStream(bis);
        MaxEnt restored = (MaxEnt) in.readObject();

        assertNotNull(restored.getFeatureSelection());
    }
@Test
    public void testGetNumParametersWithOneFeatureOneLabel() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("f0");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("l0");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        int result = MaxEnt.getNumParameters(pipe);
        assertEquals(2, result); 
    }
@Test
    public void testClassificationWithNegativeWeightsAndPositiveFeatures() {
        Alphabet dataAlphabet = new Alphabet();
        int f0 = dataAlphabet.lookupIndex("f0");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("A");
        labelAlphabet.lookupIndex("B");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        double[] parameters = new double[6];
        parameters[0] = -1.0;
        parameters[1] = -2.0;
        parameters[3] = -0.5;
        parameters[4] = -1.0;

        MaxEnt model = new MaxEnt(pipe, parameters);

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{f0}, new double[]{2.0});
        Instance instance = new Instance(fv, null, null, null);

        double[] scores = new double[2];
        model.getClassificationScores(instance, scores);

        assertEquals(1.0, scores[0] + scores[1], 1e-6);
        assertTrue(scores[0] >= 0);
        assertTrue(scores[1] >= 0);
    }
@Test
    public void testClassificationWithNanScoreAvoided() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("f");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("one");
        labelAlphabet.lookupIndex("two");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        double[] parameters = new double[6];
        parameters[0] = Double.MAX_VALUE;
        parameters[1] = Double.MAX_VALUE;
        parameters[3] = Double.MIN_VALUE;
        parameters[4] = Double.MIN_VALUE;

        MaxEnt model = new MaxEnt(pipe, parameters);

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{1.0});
        Instance instance = new Instance(fv, null, null, null);

        double[] scores = new double[2];
        model.getClassificationScores(instance, scores);

        assertFalse(Double.isNaN(scores[0]));
        assertFalse(Double.isNaN(scores[1]));
    }
@Test
    public void testClassificationScoresWithTemperatureOneShouldMatchDefault() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("x");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("left");
        labelAlphabet.lookupIndex("right");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        double[] params = new double[6];
        params[0] = 1.0;
        params[1] = 2.0;
        params[3] = 1.5;
        params[4] = 2.5;

        MaxEnt model = new MaxEnt(pipe, params);

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{1.0});
        Instance instance = new Instance(fv, null, null, null);

        double[] defaultScores = new double[2];
        model.getClassificationScores(instance, defaultScores);

        double[] tempScores = new double[2];
        model.getClassificationScoresWithTemperature(instance, 1.0, tempScores);

        assertEquals(defaultScores[0], tempScores[0], 1e-6);
        assertEquals(defaultScores[1], tempScores[1], 1e-6);
    }
@Test
    public void testSetPerClassFeatureSelectionWithEmptyArray() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("term");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("target");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        MaxEnt model = new MaxEnt(pipe, new double[2]);
        FeatureSelection[] emptyArray = new FeatureSelection[0];

        MaxEnt returned = model.setPerClassFeatureSelection(emptyArray);

        assertSame(model, returned);
        assertEquals(0, model.getPerClassFeatureSelection().length);
    }
@Test
    public void testWriteThenReadObjectPreservesFeatureSelectionCount() throws Exception {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("foo");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("label1");
        labelAlphabet.lookupIndex("label2");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        FeatureSelection fs0 = mock(FeatureSelection.class);
        FeatureSelection fs1 = mock(FeatureSelection.class);
        FeatureSelection[] selection = new FeatureSelection[]{fs0, fs1};

        MaxEnt model = new MaxEnt(pipe, new double[6], null, selection);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(model);
        oos.close();

        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bis);
        MaxEnt restored = (MaxEnt) ois.readObject();

        assertEquals(2, restored.getPerClassFeatureSelection().length);
    }
@Test
    public void testPrintWithUnicodeFeatureNames() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("☕");
        dataAlphabet.lookupIndex("💻");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("java");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        double[] params = new double[3];
        params[0] = 1.1;
        params[1] = -2.2;
        params[2] = 0.9;

        MaxEnt model = new MaxEnt(pipe, params);

        PrintWriter out = new PrintWriter(System.out);
        model.print(out);

        assertTrue(true); 
    }
@Test
    public void testSerializationWithNullFeatureSelectionIndexInArray() throws Exception {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("f");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("c0");
        labelAlphabet.lookupIndex("c1");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        FeatureSelection[] perClass = new FeatureSelection[] { null, mock(FeatureSelection.class) };
        MaxEnt model = new MaxEnt(pipe, new double[6], null, perClass);

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bout);
        oos.writeObject(model);
        oos.close();

        ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bin);
        MaxEnt restored = (MaxEnt) ois.readObject();

        FeatureSelection[] restoredFS = restored.getPerClassFeatureSelection();
        assertNull(restoredFS[0]);
        assertNotNull(restoredFS[1]);
    }
@Test
    public void testFeatureVectorWithRepeatedFeatureIndices() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("dup");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("D");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        double[] parameters = new double[2];
        parameters[0] = 2.0;
        parameters[1] = 0.5;

        MaxEnt model = new MaxEnt(pipe, parameters);

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0, 0}, new double[]{1.0, 1.0});
        Instance instance = new Instance(fv, null, null, null);
        double[] scores = new double[1];

        model.getClassificationScores(instance, scores);

        assertEquals(1.0, scores[0], 1e-6);
    }
@Test
    public void testGetUnnormalizedClassificationScoresWithNoFeaturesUsesDefaultOnly() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        dataAlphabet.lookupIndex("f0");

        labelAlphabet.lookupIndex("a");
        labelAlphabet.lookupIndex("b");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        double[] parameters = new double[6];
        parameters[2] = 5.0; 
        parameters[5] = -3.0; 

        MaxEnt model = new MaxEnt(pipe, parameters);

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{}, new double[]{});
        Instance instance = new Instance(fv, null, null, null);
        double[] scores = new double[2];

        model.getUnnormalizedClassificationScores(instance, scores);

        assertEquals(5.0, scores[0], 1e-6);
        assertEquals(-3.0, scores[1], 1e-6);
    }
@Test
    public void testClassificationScoresWithLargeFeatureWeightCausingOverflow() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("big");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("alpha");
        labelAlphabet.lookupIndex("beta");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        double[] parameters = new double[6];
        parameters[0] = 1e308; 
        parameters[3] = -1e308; 

        MaxEnt model = new MaxEnt(pipe, parameters);

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{1.0});
        Instance instance = new Instance(fv, null, null, null);

        double[] scores = new double[2];
        model.getClassificationScores(instance, scores);

        assertFalse(Double.isInfinite(scores[0]));
        assertFalse(Double.isInfinite(scores[1]));
        assertEquals(1.0, scores[0] + scores[1], 1e-6);
    }
@Test
    public void testPrintPrintStreamMethodWrapsCorrectly() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("token");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("yes");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        double[] parameters = new double[2];
        parameters[0] = 0.42;
        parameters[1] = -1.1;

        MaxEnt model = new MaxEnt(pipe, parameters);

        PrintStream ps = System.out;
        model.print(ps);

        assertTrue(true); 
    }
@Test
    public void testClassificationScoresWithNullFeatureWeightsFallsBackToZero() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("z");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("cat");
        labelAlphabet.lookupIndex("dog");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        double[] parameters = new double[6];
        

        MaxEnt model = new MaxEnt(pipe, parameters);

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{1.0});
        Instance instance = new Instance(fv, null, null, null);
        double[] scores = new double[2];

        model.getClassificationScores(instance, scores);

        assertEquals(0.5, scores[0], 1e-6);
        assertEquals(0.5, scores[1], 1e-6);
    }
@Test
    public void testRankedFeatureVectorPrintOrderForSameWeights() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("a");
        dataAlphabet.lookupIndex("b");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("c");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        double[] params = new double[3];
        params[0] = 0.5;
        params[1] = 0.5;
        params[2] = 0.0;

        MaxEnt maxEnt = new MaxEnt(pipe, params);

        PrintWriter writer = new PrintWriter(System.out);
        maxEnt.printRank(writer);

        assertTrue(true); 
    }
@Test
    public void testGetParametersReturnsIndependentArrayReference() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("x");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("class");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        MaxEnt model = new MaxEnt(pipe, new double[]{1.0, 2.0});

        double[] params1 = model.getParameters();
        double[] params2 = model.getParameters();

        assertSame(params1, params2); 
    }
@Test
    public void testClassificationWithExplicitTargetLabel() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("f");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("pos");
        labelAlphabet.lookupIndex("neg");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        double[] parameters = new double[6];
        parameters[0] = 1.0;
        parameters[3] = -1.0;

        MaxEnt model = new MaxEnt(pipe, parameters);

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{1.0});
        Label label = labelAlphabet.lookupLabel("pos");
        Instance instance = new Instance(fv, label, "src", "data");

        Classification classification = model.classify(instance);
        double scoreSum = 0.0;
        for (int i = 0; i < classification.getLabeling().numLocations(); i++) {
            scoreSum += classification.getLabeling().value(i);
        }
        assertEquals(1.0, scoreSum, 1e-6);
    }
@Test
    public void testClassificationWithSingleClassAlwaysReturnsOneScore() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("feature");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("classOnlyOne");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        MaxEnt model = new MaxEnt(pipe, new double[2]);

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{1});
        Instance instance = new Instance(fv, null, null, null);

        Classification c = model.classify(instance);
        assertEquals(1.0, c.getLabeling().value(0), 1e-6);
        assertEquals(1, c.getLabeling().numLocations());
    }
@Test
    public void testDeserializationWithCorruptedVersionNumberThrowsException() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(out);
        oos.writeInt(999); 
        oos.close();

        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(in);

        try {
            MaxEnt dummy = new MaxEnt(mock(Pipe.class), new double[0]);
            java.lang.reflect.Method readObj = MaxEnt.class.getDeclaredMethod("readObject", ObjectInputStream.class);
            readObj.setAccessible(true);
            readObj.invoke(dummy, ois);
            fail("Expected ClassNotFoundException due to version mismatch");
        } catch (Exception e) {
            assertTrue(e.getCause() instanceof ClassNotFoundException);
        }
    }
@Test
    public void testGetUnnormalizedClassificationScores_withNullPerClassFeatureSelectionAllNullEntries() {
        Alphabet dataAlphabet = new Alphabet();
        int f0 = dataAlphabet.lookupIndex("f0");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("L0");
        labelAlphabet.lookupIndex("L1");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        double[] parameters = new double[6];
        parameters[0] = 0.5;
        parameters[3] = 1.0;

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{f0}, new double[]{1.0});
        Instance instance = new Instance(fv, null, null, null);

        FeatureSelection[] perClass = new FeatureSelection[2];
        perClass[0] = null;
        perClass[1] = null;

        MaxEnt model = new MaxEnt(pipe, parameters, null, perClass);

        double[] scores = new double[2];
        model.getUnnormalizedClassificationScores(instance, scores);

        assertEquals(0.5, scores[0], 1e-6);
        assertEquals(1.0, scores[1], 1e-6);
    }
@Test
    public void testFeatureVectorWithZeroValueShouldStillComputeCorrectly() {
        Alphabet dataAlphabet = new Alphabet();
        int f0 = dataAlphabet.lookupIndex("a");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("X");
        labelAlphabet.lookupIndex("Y");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        double[] parameters = new double[6];
        parameters[0] = 10.0;
        parameters[3] = -5.0;

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{f0}, new double[]{0.0});
        Instance instance = new Instance(fv, null, null, null);

        MaxEnt model = new MaxEnt(pipe, parameters);

        double[] scores = new double[2];
        model.getUnnormalizedClassificationScores(instance, scores);

        assertEquals(0.0, scores[0], 1e-6);
        assertEquals(0.0, scores[1], 1e-6);
    }
@Test
    public void testPrintExtremeFeaturesHandlesSingleClassSingleFeature() {
        Alphabet dataAlphabet = new Alphabet();
        int idx = dataAlphabet.lookupIndex("word");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("spam");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        double[] parameters = new double[2];
        parameters[0] = 4.4;
        parameters[1] = 0.0;

        MaxEnt model = new MaxEnt(pipe, parameters);
        PrintWriter writer = new PrintWriter(System.out);
        model.printExtremeFeatures(writer, 1);

        assertTrue(true); 
    }
@Test
    public void testClassificationWithMultipleActiveFeaturesAndWeights() {
        Alphabet dataAlphabet = new Alphabet();
        int a = dataAlphabet.lookupIndex("a");
        int b = dataAlphabet.lookupIndex("b");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("pos");
        labelAlphabet.lookupIndex("neg");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        int numFeatures = 2;
        double[] param = new double[(numFeatures + 1) * 2];
        param[0] = 1.0;  
        param[1] = -1.0; 
        param[2] = 0.0;  
        param[3] = -2.0; 
        param[4] = 2.0;  
        param[5] = 0.0;  

        MaxEnt model = new MaxEnt(pipe, param);

        int[] indices = new int[]{a, b};
        double[] values = new double[]{1.0, 1.0};

        FeatureVector fv = new FeatureVector(dataAlphabet, indices, values);
        Instance instance = new Instance(fv, null, null, null);

        Classification classification = model.classify(instance);
        double prob0 = classification.getLabeling().value(0);
        double prob1 = classification.getLabeling().value(1);

        assertEquals(1.0, prob0 + prob1, 1e-6);
        assertTrue(prob0 >= 0);
        assertTrue(prob1 >= 0);
    }
@Test
    public void testPerClassFeatureSelectionWithMixedNullAndNonNull() {
        Alphabet dataAlphabet = new Alphabet();
        int f0 = dataAlphabet.lookupIndex("x");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("classA");
        labelAlphabet.lookupIndex("classB");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        FeatureSelection fs0 = mock(FeatureSelection.class);
        when(fs0.contains(0)).thenReturn(false);

        FeatureSelection[] perClassSelection = new FeatureSelection[]{fs0, null};

        double[] parameters = new double[6];
        parameters[0] = 5.0;
        parameters[3] = -5.0;

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{1.0});
        Instance instance = new Instance(fv, null, null, null);

        MaxEnt model = new MaxEnt(pipe, parameters, null, perClassSelection);

        double[] scores = new double[2];
        model.getUnnormalizedClassificationScores(instance, scores);

        assertEquals(0.0, scores[0], 1e-6); 
        assertEquals(-5.0, scores[1], 1e-6); 
    }
@Test
    public void testPrintExtremeFeaturesWithZeroRequestedFeatures() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("x");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("class");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        double[] params = new double[2];
        params[0] = 3.3;
        params[1] = 1.0;

        MaxEnt model = new MaxEnt(pipe, params);
        PrintWriter out = new PrintWriter(System.out);
        model.printExtremeFeatures(out, 0); 

        assertTrue(true); 
    }
@Test
    public void testZeroFeatureZeroLabelPipeReturnZeroParameters() {
        Alphabet dataAlphabet = new Alphabet(); 
        LabelAlphabet labelAlphabet = new LabelAlphabet(); 

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        assertEquals(0, MaxEnt.getNumParameters(pipe));
    }
@Test
    public void testGetClassificationScoresWithAllZeroExp() {
        Alphabet dataAlphabet = new Alphabet();
        int f = dataAlphabet.lookupIndex("f");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("A");
        labelAlphabet.lookupIndex("B");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        double[] params = new double[6]; 

        MaxEnt model = new MaxEnt(pipe, params);

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{f}, new double[]{0.0});
        Instance instance = new Instance(fv, null, null, null);
        double[] scores = new double[2];

        model.getClassificationScores(instance, scores);

        assertEquals(0.5, scores[0], 1e-6);
        assertEquals(0.5, scores[1], 1e-6);
    } 
}