public class MaxEnt_4_GPTLLMTest { 

 @Test
    public void testConstructorWithNullParametersAndValidPipe() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("f1");
        dataAlphabet.lookupIndex("f2");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("label1");
        labelAlphabet.lookupIndex("label2");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        MaxEnt model = new MaxEnt(pipe, null);
        double[] parameters = model.getParameters();

        int expectedParams = (dataAlphabet.size() + 1) * labelAlphabet.size();
        assertNotNull(parameters);
        assertEquals(expectedParams, parameters.length);
    }
@Test
    public void testSetAndGetParameterConsistency() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("a");
        dataAlphabet.lookupIndex("b");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("positive");
        labelAlphabet.lookupIndex("negative");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        int numFeatures = dataAlphabet.size();
        int numClasses = labelAlphabet.size();
        int paramLength = (numFeatures + 1) * numClasses;

        double[] parameters = new double[paramLength];
        MaxEnt model = new MaxEnt(pipe, parameters);

        model.setParameter(1, 1, 3.14);

        double[] updatedParams = model.getParameters();
        int index = 1 * (numFeatures + 1) + 1;
        assertEquals(3.14, updatedParams[index], 0.000001);
    }
@Test
    public void testGetClassificationScoresSumsToOne() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("f1");
        dataAlphabet.lookupIndex("f2");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("A");
        labelAlphabet.lookupIndex("B");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        int paramLength = (dataAlphabet.size() + 1) * labelAlphabet.size();
        double[] params = new double[paramLength];
        Arrays.fill(params, 0.2);

        FeatureVector fv = mock(FeatureVector.class);
        when(fv.getAlphabet()).thenReturn(dataAlphabet);
        when(fv.getIndices()).thenReturn(new int[]{0, 1});
        when(fv.getValues()).thenReturn(new double[]{1.0, 2.0});

        Instance instance = mock(Instance.class);
        when(instance.getData()).thenReturn(fv);

        MaxEnt model = new MaxEnt(pipe, params);
        double[] scores = new double[labelAlphabet.size()];
        model.getClassificationScores(instance, scores);

        double sum = scores[0] + scores[1];
        assertEquals(1.0, sum, 0.00001);
        assertTrue(scores[0] >= 0.0 && scores[0] <= 1.0);
        assertTrue(scores[1] >= 0.0 && scores[1] <= 1.0);
    }
@Test
    public void testClassifyReturnsValidClassification() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("feature");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("cat");
        labelAlphabet.lookupIndex("dog");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        double[] parameters = new double[(dataAlphabet.size() + 1) * labelAlphabet.size()];
        Arrays.fill(parameters, 0.1);

        FeatureVector fv = mock(FeatureVector.class);
        when(fv.getAlphabet()).thenReturn(dataAlphabet);
        when(fv.getIndices()).thenReturn(new int[]{0});
        when(fv.getValues()).thenReturn(new double[]{1.0});

        Instance instance = mock(Instance.class);
        when(instance.getData()).thenReturn(fv);

        MaxEnt model = new MaxEnt(pipe, parameters);
        Classification result = model.classify(instance);

        assertNotNull(result);
        assertEquals(instance, result.getInstance());
        assertEquals(model, result.getClassifier());
        assertEquals(labelAlphabet.size(), result.getLabelVector().size());
    }
@Test
    public void testSetFeatureSelectionAndRetrieveIt() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("foo");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("yes");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        double[] parameters = new double[(dataAlphabet.size() + 1) * labelAlphabet.size()];

        FeatureSelection fs = mock(FeatureSelection.class);
        MaxEnt model = new MaxEnt(pipe, parameters);
        model.setFeatureSelection(fs);

        assertSame(fs, model.getFeatureSelection());
    }
@Test
    public void testSetPerClassFeatureSelectionAndRetrieveIt() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("foo");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("class1");
        labelAlphabet.lookupIndex("class2");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        double[] parameters = new double[(dataAlphabet.size() + 1) * labelAlphabet.size()];

        FeatureSelection fs1 = mock(FeatureSelection.class);
        FeatureSelection fs2 = mock(FeatureSelection.class);
        FeatureSelection[] fsArray = new FeatureSelection[]{fs1, fs2};

        MaxEnt model = new MaxEnt(pipe, parameters);
        model.setPerClassFeatureSelection(fsArray);

        FeatureSelection[] retrieved = model.getPerClassFeatureSelection();
        assertEquals(2, retrieved.length);
        assertSame(fs1, retrieved[0]);
        assertSame(fs2, retrieved[1]);
    }
@Test
    public void testSerializationAndDeserializationRoundTrip() throws Exception {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("f1");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("L1");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        double[] originalParams = new double[(dataAlphabet.size() + 1) * labelAlphabet.size()];
        originalParams[0] = 1.23;

        MaxEnt originalModel = new MaxEnt(pipe, originalParams.clone());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(outputStream);
        oos.writeObject(originalModel);
        oos.close();

        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(inputStream);
        Object object = ois.readObject();
        ois.close();

        assertTrue(object instanceof MaxEnt);
        MaxEnt clonedModel = (MaxEnt) object;
        double[] clonedParams = clonedModel.getParameters();
        assertEquals(originalParams.length, clonedParams.length);
        assertEquals(originalParams[0], clonedParams[0], 0.000001);
    }
@Test(expected = AssertionError.class)
    public void testUnnormalizedScoresMismatchedAlphabetsThrowsAssertionError() {
        Alphabet correctAlphabet = new Alphabet();
        correctAlphabet.lookupIndex("token");

        Alphabet wrongAlphabet = new Alphabet();
        wrongAlphabet.lookupIndex("wrong");

        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupIndex("C1");
        labels.lookupIndex("C2");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(correctAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labels);

        double[] params = new double[(correctAlphabet.size() + 1) * labels.size()];

        FeatureVector fv = mock(FeatureVector.class);
        when(fv.getAlphabet()).thenReturn(wrongAlphabet);
        when(fv.getIndices()).thenReturn(new int[]{0});
        when(fv.getValues()).thenReturn(new double[]{1.0});

        Instance inst = mock(Instance.class);
        when(inst.getData()).thenReturn(fv);

        MaxEnt model = new MaxEnt(pipe, params);
        double[] scores = new double[labels.size()];
        model.getUnnormalizedClassificationScores(inst, scores);
    }
@Test
    public void testEmptyAlphabetsCreatesZeroLengthParameters() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        MaxEnt model = new MaxEnt(pipe, null);
        assertNotNull(model.getParameters());
        assertEquals(0, model.getParameters().length);
    }
@Test
    public void testOneLabelOneFeatureParameterMapping() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("x");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("C");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        int paramLength = (dataAlphabet.size() + 1) * labelAlphabet.size();
        double[] parameters = new double[paramLength];
        parameters[0] = 0.0;
        parameters[1] = 0.1;

        FeatureVector fv = mock(FeatureVector.class);
        when(fv.getAlphabet()).thenReturn(dataAlphabet);
        when(fv.getIndices()).thenReturn(new int[]{0});
        when(fv.getValues()).thenReturn(new double[]{1.0});

        Instance instance = mock(Instance.class);
        when(instance.getData()).thenReturn(fv);

        MaxEnt model = new MaxEnt(pipe, parameters);
        double[] scores = new double[1];
        model.getClassificationScores(instance, scores);

        assertEquals(1.0, scores[0], 0.00001);
    }
@Test
    public void testZeroLengthFeatureVectorDoesNotCrash() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("feature");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("label1");
        labelAlphabet.lookupIndex("label2");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        double[] parameters = new double[(dataAlphabet.size() + 1) * labelAlphabet.size()];
        Arrays.fill(parameters, 0.1);

        FeatureVector fv = mock(FeatureVector.class);
        when(fv.getAlphabet()).thenReturn(dataAlphabet);
        when(fv.getIndices()).thenReturn(new int[0]);
        when(fv.getValues()).thenReturn(new double[0]);

        Instance instance = mock(Instance.class);
        when(instance.getData()).thenReturn(fv);

        MaxEnt model = new MaxEnt(pipe, parameters);
        double[] scores = new double[labelAlphabet.size()];

        model.getClassificationScores(instance, scores);
        double sum = scores[0] + scores[1];
        assertEquals(1.0, sum, 0.00001);
    }
@Test
    public void testNullGlobalFeatureSelectionHandledGracefully() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("a");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("yes");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        double[] parameters = new double[(dataAlphabet.size() + 1) * labelAlphabet.size()];
        Arrays.fill(parameters, 0.5);

        FeatureVector fv = mock(FeatureVector.class);
        when(fv.getAlphabet()).thenReturn(dataAlphabet);
        when(fv.getIndices()).thenReturn(new int[]{0});
        when(fv.getValues()).thenReturn(new double[]{1.0});

        Instance instance = mock(Instance.class);
        when(instance.getData()).thenReturn(fv);

        MaxEnt model = new MaxEnt(pipe, parameters, null, null);

        double[] scores = new double[1];
        model.getUnnormalizedClassificationScores(instance, scores);
        assertNotSame(0.0, scores[0]);
    }
@Test
    public void testClassificationScoresWithZeroTemperatureResultsInInfinity() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("feat");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("l");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        double[] parameters = new double[(dataAlphabet.size() + 1) * labelAlphabet.size()];
        Arrays.fill(parameters, 0.2);

        FeatureVector fv = mock(FeatureVector.class);
        when(fv.getAlphabet()).thenReturn(dataAlphabet);
        when(fv.getIndices()).thenReturn(new int[]{0});
        when(fv.getValues()).thenReturn(new double[]{1.0});

        Instance instance = mock(Instance.class);
        when(instance.getData()).thenReturn(fv);

        MaxEnt model = new MaxEnt(pipe, parameters);

        double[] scores = new double[1];
        try {
            model.getClassificationScoresWithTemperature(instance, 0.0, scores);
            fail("Should have thrown ArithmeticException due to division by zero");
        } catch (ArithmeticException expected) {
            assertTrue(expected.getMessage().contains("/ by zero"));
        }
    }
@Test
    public void testClassificationScoresWithNegativeTemperatureResultsInNaN() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("feat");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("label");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        double[] parameters = new double[(dataAlphabet.size() + 1) * labelAlphabet.size()];
        Arrays.fill(parameters, 1.0);

        FeatureVector fv = mock(FeatureVector.class);
        when(fv.getAlphabet()).thenReturn(dataAlphabet);
        when(fv.getIndices()).thenReturn(new int[]{0});
        when(fv.getValues()).thenReturn(new double[]{1.0});

        Instance instance = mock(Instance.class);
        when(instance.getData()).thenReturn(fv);

        MaxEnt model = new MaxEnt(pipe, parameters);
        double[] scores = new double[1];
        model.getClassificationScoresWithTemperature(instance, -1.0, scores);
        assertFalse(Double.isNaN(scores[0]));
    }
@Test
    public void testSetPerClassFeatureSelectionWithNullArray() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("a");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("y");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        double[] parameters = new double[(dataAlphabet.size() + 1) * labelAlphabet.size()];

        MaxEnt model = new MaxEnt(pipe, parameters);
        model.setPerClassFeatureSelection(null);

        assertNull(model.getPerClassFeatureSelection());
    }
@Test
    public void testClassificationScoresWithVeryLargeFeatureValue() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("F");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("labelA");
        labelAlphabet.lookupIndex("labelB");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        double[] parameters = new double[(dataAlphabet.size() + 1) * labelAlphabet.size()];
        Arrays.fill(parameters, 1.0);

        FeatureVector fv = mock(FeatureVector.class);
        when(fv.getAlphabet()).thenReturn(dataAlphabet);
        when(fv.getIndices()).thenReturn(new int[]{0});
        when(fv.getValues()).thenReturn(new double[]{1e6}); 

        Instance instance = mock(Instance.class);
        when(instance.getData()).thenReturn(fv);

        MaxEnt model = new MaxEnt(pipe, parameters);
        double[] scores = new double[2];
        model.getClassificationScores(instance, scores);

        double sum = scores[0] + scores[1];
        assertEquals(1.0, sum, 0.000001);
        assertFalse(Double.isNaN(scores[0]));
        assertFalse(Double.isInfinite(scores[0]));
    }
@Test
    public void testConstructorWithBothFeatureSelectionsNull() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("x");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("L1");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        double[] parameters = new double[(dataAlphabet.size() + 1) * labelAlphabet.size()];

        MaxEnt model = new MaxEnt(pipe, parameters, null, null);
        assertNotNull(model.getParameters());
        assertNull(model.getFeatureSelection());
        assertNull(model.getPerClassFeatureSelection());
    }
@Test
    public void testConstructorWithFeatureSelectionNotNullButPerClassNull() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("f");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("C");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        FeatureSelection fs = mock(FeatureSelection.class);
        MaxEnt model = new MaxEnt(pipe, new double[(dataAlphabet.size() + 1)], fs, null);

        assertSame(fs, model.getFeatureSelection());
        assertNull(model.getPerClassFeatureSelection());
    }
@Test
    public void testConstructorWithPerClassFeatureSelectionNotNullButFeatureSelectionNull() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("f");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("L1");
        labelAlphabet.lookupIndex("L2");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        FeatureSelection fs1 = mock(FeatureSelection.class);
        FeatureSelection fs2 = mock(FeatureSelection.class);
        FeatureSelection[] fsArray = new FeatureSelection[]{fs1, fs2};

        MaxEnt model = new MaxEnt(pipe, new double[(dataAlphabet.size() + 1) * labelAlphabet.size()], null, fsArray);

        assertNull(model.getFeatureSelection());
        assertArrayEquals(fsArray, model.getPerClassFeatureSelection());
    }
@Test
    public void testSetDefaultFeatureIndexAffectsSerialization() throws Exception {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("f");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("y");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        double[] parameters = new double[(dataAlphabet.size() + 1) * labelAlphabet.size()];

        MaxEnt model = new MaxEnt(pipe, parameters);
        model.setDefaultFeatureIndex(99);

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bout);
        out.writeObject(model);
        out.close();

        ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
        ObjectInputStream in = new ObjectInputStream(bin);

        MaxEnt deserialized = (MaxEnt) in.readObject();
        assertNotNull(deserialized);
        assertEquals(99, deserialized.getDefaultFeatureIndex());
    }
@Test
    public void testSerializationWithNullFeatureSelections() throws Exception {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("f");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("label");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        double[] parameters = new double[(dataAlphabet.size() + 1)];

        MaxEnt model = new MaxEnt(pipe, parameters);
        model.setFeatureSelection(null);
        model.setPerClassFeatureSelection(null);

        ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
        ObjectOutputStream objOut = new ObjectOutputStream(outBytes);
        objOut.writeObject(model);
        objOut.close();

        ByteArrayInputStream inBytes = new ByteArrayInputStream(outBytes.toByteArray());
        ObjectInputStream objIn = new ObjectInputStream(inBytes);
        MaxEnt deserialized = (MaxEnt) objIn.readObject();

        assertNotNull(deserialized);
        assertNull(deserialized.getFeatureSelection());
        assertNull(deserialized.getPerClassFeatureSelection());
    }
@Test
    public void testSerializationWithPartiallyNullPerClassFeatureSelection() throws Exception {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("f");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("label1");
        labelAlphabet.lookupIndex("label2");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        double[] parameters = new double[(dataAlphabet.size() + 1) * 2];

        FeatureSelection fs = mock(FeatureSelection.class);
        FeatureSelection[] perClassFS = new FeatureSelection[]{fs, null};

        MaxEnt model = new MaxEnt(pipe, parameters, null, perClassFS);

        ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
        ObjectOutputStream objOut = new ObjectOutputStream(outBytes);
        objOut.writeObject(model);
        objOut.close();

        ByteArrayInputStream inBytes = new ByteArrayInputStream(outBytes.toByteArray());
        ObjectInputStream objIn = new ObjectInputStream(inBytes);
        MaxEnt deserialized = (MaxEnt) objIn.readObject();

        assertNotNull(deserialized.getPerClassFeatureSelection());
        assertNotNull(deserialized.getPerClassFeatureSelection()[0]);
        assertNull(deserialized.getPerClassFeatureSelection()[1]);
    }
@Test
    public void testPrintExtremeFeaturesWithZeroWeights() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("w");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("label1");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        double[] parameters = new double[(dataAlphabet.size() + 1)];
        parameters[0] = 0.0; 
        parameters[1] = 0.0; 

        MaxEnt model = new MaxEnt(pipe, parameters);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(baos);
        model.printExtremeFeatures(writer, 1);
        writer.flush();

        String output = baos.toString();
        assertTrue(output.contains("FEATURES FOR CLASS"));
        assertTrue(output.contains(" <default> 0.0 "));
    }
@Test
    public void testPrintRankWithOnlyDefaultFeatureZero() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("f");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("label");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        double[] parameters = new double[(dataAlphabet.size() + 1)];

        MaxEnt model = new MaxEnt(pipe, parameters);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(out);
        model.printRank(writer);
        writer.flush();

        String output = out.toString();
        assertTrue(output.contains("FEATURES FOR CLASS"));
        assertTrue(output.contains("<default> 0.0 "));
    }
@Test
    public void testSetFeatureSelectionReturnsSameModelInstance() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("f");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("L");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        MaxEnt model = new MaxEnt(pipe, new double[(dataAlphabet.size() + 1)]);
        FeatureSelection fs = mock(FeatureSelection.class);
        MaxEnt result = model.setFeatureSelection(fs);

        assertSame(model, result);
        assertSame(fs, model.getFeatureSelection());
    }
@Test
    public void testSetPerClassFeatureSelectionReturnsSameModelInstance() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("word");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("label1");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        MaxEnt model = new MaxEnt(pipe, new double[(dataAlphabet.size() + 1)]);
        FeatureSelection[] fss = new FeatureSelection[]{mock(FeatureSelection.class)};
        MaxEnt result = model.setPerClassFeatureSelection(fss);

        assertSame(model, result);
        assertArrayEquals(fss, model.getPerClassFeatureSelection());
    }
@Test
    public void testSetParameterWithZeroIndexes() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("f");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("L");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        double[] parameters = new double[(dataAlphabet.size() + 1) * labelAlphabet.size()];
        MaxEnt model = new MaxEnt(pipe, parameters);

        model.setParameter(0, 0, 42.0);
        double[] result = model.getParameters();
        assertEquals(42.0, result[0], 0.00001);
    }
@Test
    public void testSetDefaultFeatureIndexThenUseInPrintExtremeFeatures() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("foo");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("LBL");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        double[] parameters = new double[(dataAlphabet.size() + 1) * labelAlphabet.size()];
        parameters[0] = 3.0;
        parameters[1] = -2.0;

        MaxEnt model = new MaxEnt(pipe, parameters);
        model.setDefaultFeatureIndex(1); 

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(out);
        model.printExtremeFeatures(pw, 1);
        pw.flush();

        String output = out.toString();
        assertTrue(output.contains("FEATURES FOR CLASS"));
        assertTrue(output.contains("<default> -2.0"));
    }
@Test
    public void testSerializationWithFeatureSelectionOnly() throws Exception {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("word");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("class");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        FeatureSelection fs = mock(FeatureSelection.class);
        MaxEnt model = new MaxEnt(pipe, new double[(dataAlphabet.size() + 1)], fs);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream objOut = new ObjectOutputStream(out);
        objOut.writeObject(model);
        objOut.flush();

        ObjectInputStream objIn = new ObjectInputStream(
                new java.io.ByteArrayInputStream(out.toByteArray()));
        MaxEnt deserialized = (MaxEnt) objIn.readObject();

        assertNotNull(deserialized);
        assertNotNull(deserialized.getFeatureSelection());
        assertNull(deserialized.getPerClassFeatureSelection());
    }
@Test
    public void testPrintMethodWithNonEmptyModel() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("f1");
        dataAlphabet.lookupIndex("f2");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("L1");
        labelAlphabet.lookupIndex("L2");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        double[] parameters = new double[(dataAlphabet.size() + 1) * labelAlphabet.size()];
        for (int i = 0; i < parameters.length; i++) {
            parameters[i] = i * 0.1;
        }

        MaxEnt model = new MaxEnt(pipe, parameters);

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(bout);
        model.print(pw);
        pw.flush();
        String output = bout.toString();

        assertTrue(output.contains("FEATURES FOR CLASS L1"));
        assertTrue(output.contains(" <default> "));
    }
@Test
    public void testClassificationWithConstantParametersReturnsUniformProbs() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("f");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("A");
        labelAlphabet.lookupIndex("B");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        double[] parameters = new double[(dataAlphabet.size() + 1) * labelAlphabet.size()];
        for (int i = 0; i < parameters.length; i++) {
            parameters[i] = 1.0; 

        }

        FeatureVector fv = mock(FeatureVector.class);
        when(fv.getAlphabet()).thenReturn(dataAlphabet);
        when(fv.getIndices()).thenReturn(new int[]{0});
        when(fv.getValues()).thenReturn(new double[]{1.0});

        Instance instance = mock(Instance.class);
        when(instance.getData()).thenReturn(fv);

        MaxEnt model = new MaxEnt(pipe, parameters);
        double[] scores = new double[2];
        model.getClassificationScores(instance, scores);

        assertEquals(1.0, scores[0] + scores[1], 0.00001);
        assertTrue(scores[0] > 0);
        assertTrue(scores[1] > 0);
    }
@Test
    public void testGetNumParametersRespectsPipeState() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("a");
        dataAlphabet.lookupIndex("b");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("x");
        labelAlphabet.lookupIndex("y");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        int expected = (dataAlphabet.size() + 1) * labelAlphabet.size();
        int actual = MaxEnt.getNumParameters(pipe);

        assertEquals(expected, actual);
    }
@Test
    public void testSetParametersReflectsNewArray() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("f");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("c");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        double[] original = new double[(dataAlphabet.size() + 1)];
        MaxEnt model = new MaxEnt(pipe, original);

        double[] newParams = new double[(dataAlphabet.size() + 1)];
        newParams[0] = 123;
        model.setParameters(newParams);

        assertEquals(123.0, model.getParameters()[0], 0.0001);
    }
@Test(expected = ClassNotFoundException.class)
    public void testReadObjectThrowsOnUnsupportedVersion() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(out);
        oos.writeInt(97); 
        oos.flush();

        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        ObjectInputStream objIn = new ObjectInputStream(in);

        MaxEnt dummy = new MaxEnt(mock(Pipe.class), new double[0]);
        java.lang.reflect.Method m = MaxEnt.class.getDeclaredMethod("readObject", ObjectInputStream.class);
        m.setAccessible(true);
        try {
            m.invoke(dummy, objIn);
        } catch (java.lang.reflect.InvocationTargetException e) {
            throw (ClassNotFoundException) e.getCause();
        }
    }
@Test
    public void testGetUnnormalizedClassificationScoresWithNullPerClassFeatureSelectionEntry() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("f1");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("class0");
        labelAlphabet.lookupIndex("class1");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        double[] parameters = new double[(dataAlphabet.size() + 1) * labelAlphabet.size()];
        Arrays.fill(parameters, 0.1);

        FeatureVector fv = mock(FeatureVector.class);
        when(fv.getAlphabet()).thenReturn(dataAlphabet);
        when(fv.getIndices()).thenReturn(new int[]{0});
        when(fv.getValues()).thenReturn(new double[]{1.0});

        Instance instance = mock(Instance.class);
        when(instance.getData()).thenReturn(fv);

        FeatureSelection[] perClass = new FeatureSelection[2];
        perClass[0] = null;
        perClass[1] = mock(FeatureSelection.class);

        MaxEnt model = new MaxEnt(pipe, parameters, null, perClass);
        double[] scores = new double[2];
        model.getUnnormalizedClassificationScores(instance, scores);

        assertEquals(2, scores.length);
    }
@Test
    public void testClassificationScoresWithSingleLabelReturnsOne() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("x");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("onlyLabel");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        FeatureVector fv = mock(FeatureVector.class);
        when(fv.getAlphabet()).thenReturn(dataAlphabet);
        when(fv.getIndices()).thenReturn(new int[]{0});
        when(fv.getValues()).thenReturn(new double[]{1.0});

        Instance instance = mock(Instance.class);
        when(instance.getData()).thenReturn(fv);

        double[] parameters = new double[(dataAlphabet.size() + 1)];
        parameters[0] = 0.5;
        parameters[1] = 0.5;

        MaxEnt model = new MaxEnt(pipe, parameters);
        double[] scores = new double[1];
        model.getClassificationScores(instance, scores);
        assertEquals(1.0, scores[0], 0.000001);
    }
@Test
    public void testGetClassificationScoresHandlesLargeLogits() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("h");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("label1");
        labelAlphabet.lookupIndex("label2");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        FeatureVector fv = mock(FeatureVector.class);
        when(fv.getAlphabet()).thenReturn(dataAlphabet);
        when(fv.getIndices()).thenReturn(new int[]{0});
        when(fv.getValues()).thenReturn(new double[]{1000.0});

        Instance instance = mock(Instance.class);
        when(instance.getData()).thenReturn(fv);

        double[] parameters = new double[(dataAlphabet.size() + 1) * 2];
        Arrays.fill(parameters, 1.0);

        MaxEnt model = new MaxEnt(pipe, parameters);
        double[] scores = new double[2];
        model.getClassificationScores(instance, scores);

        assertEquals(1.0, scores[0] + scores[1], 0.0001);
        assertFalse(Double.isNaN(scores[0]));
        assertFalse(Double.isInfinite(scores[0]));
    }
@Test
    public void testClassificationScoresWithTemperatureExtremelyHighMovesToUniform() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("v");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("l1");
        labelAlphabet.lookupIndex("l2");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        FeatureVector fv = mock(FeatureVector.class);
        when(fv.getAlphabet()).thenReturn(dataAlphabet);
        when(fv.getIndices()).thenReturn(new int[]{0});
        when(fv.getValues()).thenReturn(new double[]{1.0});

        Instance instance = mock(Instance.class);
        when(instance.getData()).thenReturn(fv);

        double[] parameters = new double[(dataAlphabet.size() + 1) * 2];
        parameters[0] = 10.0;
        parameters[1] = 20.0;
        parameters[2] = 5.0;
        parameters[3] = 1.5;

        MaxEnt model = new MaxEnt(pipe, parameters);
        double[] scores = new double[2];
        model.getClassificationScoresWithTemperature(instance, 1000.0, scores);

        assertEquals(1.0, scores[0] + scores[1], 0.0001);
        assertTrue(scores[0] > 0.45);
        assertTrue(scores[1] < 0.55);
    }
@Test
    public void testZeroParametersScoresAreEqual() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("q");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("a");
        labelAlphabet.lookupIndex("b");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        FeatureVector fv = mock(FeatureVector.class);
        when(fv.getAlphabet()).thenReturn(dataAlphabet);
        when(fv.getIndices()).thenReturn(new int[]{0});
        when(fv.getValues()).thenReturn(new double[]{0.0});

        Instance instance = mock(Instance.class);
        when(instance.getData()).thenReturn(fv);

        double[] parameters = new double[(dataAlphabet.size() + 1) * 2];
        Arrays.fill(parameters, 0.0);

        MaxEnt model = new MaxEnt(pipe, parameters);
        double[] scores = new double[2];
        model.getClassificationScores(instance, scores);

        assertEquals(1.0, scores[0] + scores[1], 0.00001);
        assertEquals(scores[0], scores[1], 0.00001);
    }
@Test
    public void testFeatureVectorWithOnlyDefaultUsed() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("x");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("pos");
        labelAlphabet.lookupIndex("neg");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        int numFeatures = dataAlphabet.size();
        int numLabels = labelAlphabet.size();
        int numParams = (numFeatures + 1) * numLabels;

        double[] parameters = new double[numParams];
        Arrays.fill(parameters, 0);
        parameters[numFeatures] = 1.0;
        parameters[numFeatures * 2 - 1] = 2.0;

        FeatureVector fv = mock(FeatureVector.class);
        when(fv.getAlphabet()).thenReturn(dataAlphabet);
        
        when(fv.getIndices()).thenReturn(new int[0]);
        when(fv.getValues()).thenReturn(new double[0]);

        Instance instance = mock(Instance.class);
        when(instance.getData()).thenReturn(fv);

        MaxEnt model = new MaxEnt(pipe, parameters);
        double[] scores = new double[numLabels];
        model.getUnnormalizedClassificationScores(instance, scores);
        assertEquals(1.0, scores[0], 0.0001);
        assertEquals(2.0, scores[1], 0.0001);
    }
@Test
    public void testClassificationPreservesInstanceInOutput() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("s");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("p");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        double[] parameters = new double[(dataAlphabet.size() + 1)];
        parameters[0] = 0;
        parameters[1] = 1;

        FeatureVector fv = mock(FeatureVector.class);
        when(fv.getAlphabet()).thenReturn(dataAlphabet);
        when(fv.getIndices()).thenReturn(new int[]{});
        when(fv.getValues()).thenReturn(new double[]{});

        Instance instance = mock(Instance.class);
        when(instance.getData()).thenReturn(fv);

        MaxEnt model = new MaxEnt(pipe, parameters);
        Classification result = model.classify(instance);

        assertNotNull(result);
        assertEquals(instance, result.getInstance());
        assertEquals(model, result.getClassifier());
    }
@Test
    public void testEmptyFeatureAlphabetAndSingleLabel() {
        Alphabet dataAlphabet = new Alphabet(); 
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("L");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet); 
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet); 

        MaxEnt model = new MaxEnt(pipe, null);

        FeatureVector fv = mock(FeatureVector.class);
        when(fv.getAlphabet()).thenReturn(dataAlphabet);
        when(fv.getIndices()).thenReturn(new int[]{});
        when(fv.getValues()).thenReturn(new double[]{});
        
        Instance instance = mock(Instance.class);
        when(instance.getData()).thenReturn(fv);

        double[] scores = new double[1];
        model.getClassificationScores(instance, scores);
        assertEquals(1.0, scores[0], 0.00001);
    }
@Test
    public void testFeatureVectorWithUnknownFeatureIndexIgnoredIfNotInAlphabet() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("known");  

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("CLASS");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        double[] parameters = new double[(dataAlphabet.size() + 1)];
        parameters[0] = 1.0; 

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0, 100}, new double[]{1.0, 1.0}, false, true);

        Instance instance = mock(Instance.class);
        when(instance.getData()).thenReturn(fv);

        MaxEnt model = new MaxEnt(pipe, parameters);
        double[] scores = new double[1];
        model.getClassificationScores(instance, scores);

        assertFalse(Double.isNaN(scores[0]));
    }
@Test
    public void testPrintStreamDelegationToPrintWriter() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("feature");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("A");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        double[] params = new double[(dataAlphabet.size() + 1) * labelAlphabet.size()];
        Arrays.fill(params, 1.0);

        MaxEnt model = new MaxEnt(pipe, params);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(out);
        model.print(ps);

        assertTrue(out.toString().contains("FEATURES FOR CLASS"));
    }
@Test
    public void testRankedFeatureVectorSortingWithinPrintRank() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("alpha");
        dataAlphabet.lookupIndex("beta");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("C1");
        labelAlphabet.lookupIndex("C2");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        double[] params = new double[(dataAlphabet.size() + 1) * labelAlphabet.size()];
        params[0] = 0.3;
        params[1] = 0.7;
        params[3] = -0.2;
        params[4] = 0.5;

        MaxEnt model = new MaxEnt(pipe, params);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(out);
        model.printRank(writer);
        writer.flush();

        String result = out.toString();
        assertTrue(result.contains("FEATURES FOR CLASS"));
        assertTrue(result.contains("beta"));
        assertTrue(result.contains("alpha"));
    }
@Test
    public void testPrintExtremeFeaturesTopAndBottomSorted() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("f1");
        dataAlphabet.lookupIndex("f2");
        dataAlphabet.lookupIndex("f3");
        dataAlphabet.lookupIndex("f4");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("Z");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        int numFeatures = dataAlphabet.size() + 1;
        double[] params = new double[numFeatures];

        params[0] = -0.5;
        params[1] = 0.1;
        params[2] = 0.8;
        params[3] = -0.7;
        params[4] = 0.2; 

        MaxEnt model = new MaxEnt(pipe, params);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(out);
        model.printExtremeFeatures(writer, 2);
        writer.flush();

        assertTrue(out.toString().contains("FEATURES FOR CLASS"));
    }
@Test
    public void testClassificationScoreWithNegativeTemperatureStillNormalizes() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("f");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("a");
        labelAlphabet.lookupIndex("b");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        double[] params = new double[(dataAlphabet.size() + 1) * labelAlphabet.size()];
        Arrays.fill(params, 1);

        FeatureVector fv = mock(FeatureVector.class);
        when(fv.getAlphabet()).thenReturn(dataAlphabet);
        when(fv.getIndices()).thenReturn(new int[]{0});
        when(fv.getValues()).thenReturn(new double[]{0.5});

        Instance instance = mock(Instance.class);
        when(instance.getData()).thenReturn(fv);

        MaxEnt model = new MaxEnt(pipe, params);
        double[] scores = new double[2];
        model.getClassificationScoresWithTemperature(instance, -1.0, scores);

        assertEquals(1.0, scores[0] + scores[1], 0.00001);
    }
@Test
    public void testGetClassificationScoresPreventsNaNWithExtremeValues() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("f");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("A");
        labelAlphabet.lookupIndex("B");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        double[] params = new double[(dataAlphabet.size() + 1) * labelAlphabet.size()];
        params[0] = 1e6;
        params[1] = 1e5;
        params[2] = -1e6;
        params[3] = -1e5;

        FeatureVector fv = mock(FeatureVector.class);
        when(fv.getAlphabet()).thenReturn(dataAlphabet);
        when(fv.getIndices()).thenReturn(new int[]{0});
        when(fv.getValues()).thenReturn(new double[]{1.0});

        Instance instance = mock(Instance.class);
        when(instance.getData()).thenReturn(fv);

        MaxEnt model = new MaxEnt(pipe, params);
        double[] scores = new double[2];
        model.getClassificationScores(instance, scores);

        assertFalse(Double.isNaN(scores[0]));
        assertFalse(Double.isNaN(scores[1]));
        assertEquals(1.0, scores[0] + scores[1], 0.00001);
    }
@Test
    public void testClassificationSoftmaxSafeWithLargeNegativeLogits() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("t");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("yes");
        labelAlphabet.lookupIndex("no");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        double[] params = new double[4];
        params[0] = -1e10;
        params[1] = -2e10;
        params[2] = -1e10;
        params[3] = -2e10;

        FeatureVector fv = mock(FeatureVector.class);
        when(fv.getAlphabet()).thenReturn(dataAlphabet);
        when(fv.getIndices()).thenReturn(new int[]{0});
        when(fv.getValues()).thenReturn(new double[]{1.0});

        Instance instance = mock(Instance.class);
        when(instance.getData()).thenReturn(fv);

        MaxEnt model = new MaxEnt(pipe, params);
        double[] scores = new double[2];
        model.getClassificationScores(instance, scores);

        assertFalse(Double.isNaN(scores[0]));
        assertFalse(Double.isNaN(scores[1]));
        assertEquals(1.0, scores[0] + scores[1], 0.00001);
    }
@Test
    public void testPrintHandlesEmptyAlphabetsAndZeroParameters() {
        Alphabet dataAlphabet = new Alphabet(); 
        LabelAlphabet labelAlphabet = new LabelAlphabet(); 

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        double[] params = new double[0];
        MaxEnt model = new MaxEnt(pipe, params);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(out);
        model.print(writer);
        writer.flush();

        String output = out.toString();
        assertEquals("", output); 
    }
@Test
    public void testGetUnnormalizedScoresWithNullInstanceDataThrowsClassCastException() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("f");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("lbl");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        double[] params = new double[(dataAlphabet.size() + 1) * labelAlphabet.size()];
        MaxEnt model = new MaxEnt(pipe, params);

        Instance instance = new Instance(null, null, null, null);

        double[] scores = new double[1];
        try {
            model.getUnnormalizedClassificationScores(instance, scores);
            fail("Expected ClassCastException");
        } catch (ClassCastException expected) {
            assertTrue(expected.getMessage().contains("FeatureVector"));
        }
    }
@Test
    public void testRankedPrintHandlesNegativeAndZeroWeights() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("a");
        dataAlphabet.lookupIndex("b");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("z");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        int numParams = (dataAlphabet.size() + 1) * labelAlphabet.size();
        double[] parameters = new double[numParams];
        parameters[0] = -5.0;
        parameters[1] = 0.0;
        parameters[2] = -1.0;

        MaxEnt model = new MaxEnt(pipe, parameters);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(out);
        model.printRank(writer);
        writer.flush();

        String output = out.toString();
        assertTrue(output.contains("FEATURES FOR CLASS"));
        assertTrue(output.contains("<default> -1.0"));
    }
@Test
    public void testPrintExtremeFeaturesWithRequestedTopGreaterThanTotal() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("x");
        dataAlphabet.lookupIndex("y");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("Cat");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        double[] params = new double[(dataAlphabet.size() + 1)];
        params[0] = 8.0;
        params[1] = 3.0;
        params[2] = -2.0;

        MaxEnt model = new MaxEnt(pipe, params);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(out);
        model.printExtremeFeatures(writer, 10); 
        writer.flush();

        String output = out.toString();
        assertTrue(output.contains("FEATURES FOR CLASS"));
    }
@Test
    public void testSetParameterUpdatesCorrectIndexWithMultipleLabels() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("f1");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("A");
        labelAlphabet.lookupIndex("B");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        int numParams = (dataAlphabet.size() + 1) * labelAlphabet.size();
        double[] parameters = new double[numParams];

        MaxEnt model = new MaxEnt(pipe, parameters);
        model.setParameter(1, 0, 7.5);

        assertEquals(7.5, model.getParameters()[(dataAlphabet.size() + 1)], 0.0001);
    }
@Test
    public void testSingleFeatureMultipleLabelsUnnormalizedScores() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("token");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("yes");
        labelAlphabet.lookupIndex("no");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        double[] parameters = new double[(dataAlphabet.size() + 1) * labelAlphabet.size()];
        parameters[0] = 0.5;
        parameters[1] = 0.2;
        parameters[2] = 0.6;
        parameters[3] = 0.4;

        FeatureVector fv = new FeatureVector(dataAlphabet, new int[]{0}, new double[]{1.0});
        Instance instance = new Instance(fv, null, null, null);

        MaxEnt model = new MaxEnt(pipe, parameters);
        double[] scores = new double[2];
        model.getUnnormalizedClassificationScores(instance, scores);

        assertEquals(0.7, scores[0], 0.0001);
        assertEquals(1.0, scores[1], 0.0001);
    }
@Test
    public void testPrintToSystemOutDoesNotThrowException() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("f");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("x");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        MaxEnt model = new MaxEnt(pipe, new double[2]);
        
        model.print();
    }
@Test
    public void testSetParametersWithShorterArrayThanExpectedModifiesReference() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("short");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("L");

        Pipe pipe = mock(Pipe.class);
        when(pipe.getDataAlphabet()).thenReturn(dataAlphabet);
        when(pipe.getTargetAlphabet()).thenReturn(labelAlphabet);

        double[] original = new double[(dataAlphabet.size() + 1)];
        MaxEnt model = new MaxEnt(pipe, original);

        double[] shortParams = new double[1]; 
        shortParams[0] = 99;
        model.setParameters(shortParams);

        assertEquals(99.0, model.getParameters()[0], 0.0001);
    }
@Test(expected = AssertionError.class)
    public void testGetNumParametersInstanceMethodFailsIfPipeNotSet() {
        MaxEnt model = new MaxEnt(null, new double[0]);
        model.getNumParameters(); 
    } 
}