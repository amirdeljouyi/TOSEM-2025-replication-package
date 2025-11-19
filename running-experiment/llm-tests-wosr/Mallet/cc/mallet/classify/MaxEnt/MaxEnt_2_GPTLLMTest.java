public class MaxEnt_2_GPTLLMTest { 

 @Test
    public void testConstructorWithFeatureSelection() {
        List<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        pipes.add(new Target2Label());

        Pipe pipe = new SerialPipes(pipes);
        pipe.setTargetProcessing(true);

        InstanceList instances = new InstanceList(pipe);
        instances.addThruPipe(new StringArrayIterator(new String[]{"a b c"}, new String[]{"X"}));

        Alphabet dataAlphabet = pipe.getDataAlphabet();
        LabelAlphabet labelAlphabet = (LabelAlphabet) pipe.getTargetAlphabet();

        int numParams = MaxEnt.getNumParameters(pipe);
        double[] parameters = new double[numParams];
        parameters[0] = 0.5;

        FeatureSelection fs = new FeatureSelection(dataAlphabet);
        MaxEnt classifier = new MaxEnt(pipe, parameters, fs);

        assertSame(fs, classifier.getFeatureSelection());
        assertNull(classifier.getPerClassFeatureSelection());
    }
@Test
    public void testConstructorWithPerClassFeatureSelection() {
        List<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        pipes.add(new Target2Label());

        Pipe pipe = new SerialPipes(pipes);
        pipe.setTargetProcessing(true);

        InstanceList instances = new InstanceList(pipe);
        instances.addThruPipe(new StringArrayIterator(
                new String[]{"x y z", "a b c"},
                new String[]{"Y", "X"}
        ));

        Alphabet dataAlphabet = pipe.getDataAlphabet();
        LabelAlphabet labelAlphabet = (LabelAlphabet) pipe.getTargetAlphabet();

        int numParams = MaxEnt.getNumParameters(pipe);
        double[] parameters = new double[numParams];
        parameters[1] = 0.9;

        FeatureSelection fs0 = new FeatureSelection(dataAlphabet);
        FeatureSelection fs1 = new FeatureSelection(dataAlphabet);
        FeatureSelection[] perClassFS = new FeatureSelection[]{fs0, fs1};

        MaxEnt classifier = new MaxEnt(pipe, parameters, perClassFS);
        assertSame(perClassFS, classifier.getPerClassFeatureSelection());
        assertNull(classifier.getFeatureSelection());
    }
@Test
    public void testGetNumParametersStatic() {
        List<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        pipes.add(new Target2Label());

        Pipe pipe = new SerialPipes(pipes);
        pipe.setTargetProcessing(true);

        InstanceList instances = new InstanceList(pipe);
        instances.addThruPipe(new StringArrayIterator(
                new String[]{"a b", "c d"},
                new String[]{"pos", "neg"}
        ));

        int expected = (pipe.getDataAlphabet().size() + 1) * pipe.getTargetAlphabet().size();
        int actual = MaxEnt.getNumParameters(pipe);

        assertEquals(expected, actual);
    }
@Test
    public void testClassificationScoresSumToOne() {
        List<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        pipes.add(new Target2Label());

        Pipe pipe = new SerialPipes(pipes);
        pipe.setTargetProcessing(true);

        InstanceList instances = new InstanceList(pipe);
        instances.addThruPipe(new StringArrayIterator(
                new String[]{"foo bar", "bar baz"},
                new String[]{"A", "B"}
        ));

        int numParams = MaxEnt.getNumParameters(pipe);
        double[] parameters = new double[numParams];
        parameters[0] = 0.1;
        parameters[1] = 0.2;
        parameters[2] = 0.3;

        MaxEnt classifier = new MaxEnt(pipe, parameters);
        Instance instance = instances.get(0);

        int numLabels = pipe.getTargetAlphabet().size();
        double[] scores = new double[numLabels];
        classifier.getClassificationScores(instance, scores);

        double total = scores[0] + scores[1];

        assertTrue(scores[0] >= 0);
        assertTrue(scores[1] >= 0);
        assertEquals(1.0, total, 1e-6);
    }
@Test
    public void testSetAndGetFeatureSelection() {
        List<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        pipes.add(new Target2Label());

        Pipe pipe = new SerialPipes(pipes);
        pipe.setTargetProcessing(true);

        InstanceList instances = new InstanceList(pipe);
        instances.addThruPipe(new StringArrayIterator(
                new String[]{"one word"},
                new String[]{"Label"}
        ));

        int numParams = MaxEnt.getNumParameters(pipe);
        double[] parameters = new double[numParams];
        parameters[0] = 0.1;

        MaxEnt classifier = new MaxEnt(pipe, parameters);
        FeatureSelection fs = new FeatureSelection(pipe.getDataAlphabet());
        classifier.setFeatureSelection(fs);

        assertSame(fs, classifier.getFeatureSelection());
    }
@Test
    public void testClassifyReturnsValidClassification() {
        List<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        pipes.add(new Target2Label());

        Pipe pipe = new SerialPipes(pipes);
        pipe.setTargetProcessing(true);

        InstanceList instances = new InstanceList(pipe);
        instances.addThruPipe(new StringArrayIterator(
                new String[]{"word1 word2"},
                new String[]{"ClassA"}
        ));

        int numParams = MaxEnt.getNumParameters(pipe);
        double[] parameters = new double[numParams];
        parameters[0] = 0;

        MaxEnt classifier = new MaxEnt(pipe, parameters);
        Instance instance = instances.get(0);

        Classification result = classifier.classify(instance);

        assertNotNull(result);
        assertNotNull(result.getLabelVector());
        assertEquals(1, result.getLabelVector().numLocations());
    }
@Test
    public void testSerializationAndDeserializationMaxEnt() throws Exception {
        List<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        pipes.add(new Target2Label());

        Pipe pipe = new SerialPipes(pipes);
        pipe.setTargetProcessing(true);

        InstanceList instances = new InstanceList(pipe);
        instances.addThruPipe(new StringArrayIterator(new String[]{"text"}, new String[]{"tag"}));

        int numParams = MaxEnt.getNumParameters(pipe);
        double[] parameters = new double[numParams];
        parameters[0] = 0.42;

        MaxEnt original = new MaxEnt(pipe, parameters);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject(original);
        out.close();

        byte[] serialized = bos.toByteArray();
        ByteArrayInputStream bis = new ByteArrayInputStream(serialized);
        ObjectInputStream in = new ObjectInputStream(bis);
        MaxEnt restored = (MaxEnt) in.readObject();

        assertNotNull(restored);
        assertEquals(original.getDefaultFeatureIndex(), restored.getDefaultFeatureIndex());
    }
@Test
    public void testSetPerClassFeatureSelectionWorks() {
        List<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        pipes.add(new Target2Label());

        Pipe pipe = new SerialPipes(pipes);
        pipe.setTargetProcessing(true);

        InstanceList instances = new InstanceList(pipe);
        instances.addThruPipe(new StringArrayIterator(
                new String[]{"x y", "z q"}, 
                new String[]{"A", "B"}
        ));

        int numParams = MaxEnt.getNumParameters(pipe);
        double[] parameters = new double[numParams];

        MaxEnt model = new MaxEnt(pipe, parameters);

        Alphabet dataAlphabet = pipe.getDataAlphabet();
        FeatureSelection[] fss = new FeatureSelection[2];
        fss[0] = new FeatureSelection(dataAlphabet);
        fss[1] = new FeatureSelection(dataAlphabet);

        model.setPerClassFeatureSelection(fss);

        assertSame(fss, model.getPerClassFeatureSelection());
    }
@Test
    public void testEmptyAlphabetZeroParameters() {
        Alphabet dataAlphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        dataAlphabet.lookupIndex("feature"); 
        labelAlphabet.lookupLabel("label"); 

        List<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new Noop());
        Pipe pipe = new SerialPipes(pipes);
        pipe.setDataAlphabet(dataAlphabet);
        pipe.setTargetAlphabet(labelAlphabet);

        double[] parameters = new double[MaxEnt.getNumParameters(pipe)];

        MaxEnt classifier = new MaxEnt(pipe, parameters);
        assertNotNull(classifier);
        assertEquals(2, parameters.length); 
    }
@Test
    public void testClassificationScoresWithNaNParameter() {
        List<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        pipes.add(new Target2Label());

        Pipe pipe = new SerialPipes(pipes);
        pipe.setTargetProcessing(true);

        String[] inputs = {"a b"};
        String[] targets = {"y"};
        InstanceList instanceList = new InstanceList(pipe);
        instanceList.addThruPipe(new StringArrayIterator(inputs, targets));

        int numParams = MaxEnt.getNumParameters(pipe);
        double[] parameters = new double[numParams];
        parameters[0] = Double.NaN;

        MaxEnt classifier = new MaxEnt(pipe, parameters);
        double[] scores = new double[pipe.getTargetAlphabet().size()];

        classifier.getClassificationScores(instanceList.get(0), scores);

        assertFalse(Double.isNaN(scores[0]));
    }
@Test
    public void testGetUnnormalizedClassificationScoresWithNullFeatureSelectionAndPerClass() {
        List<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        pipes.add(new Target2Label());

        Pipe pipe = new SerialPipes(pipes);
        pipe.setTargetProcessing(true);

        String[] data = {"x y"};
        String[] target = {"Z"};
        InstanceList instanceList = new InstanceList(pipe);
        instanceList.addThruPipe(new StringArrayIterator(data, target));

        int numParams = MaxEnt.getNumParameters(pipe);
        double[] params = new double[numParams];
        params[0] = 0.7;

        MaxEnt classifier = new MaxEnt(pipe, params);
        double[] scores = new double[pipe.getTargetAlphabet().size()];

        classifier.getUnnormalizedClassificationScores(instanceList.get(0), scores);

        assertTrue(scores[0] != 0);
    }
@Test(expected = AssertionError.class)
    public void testGetClassificationScoresWithMismatchedFeatureAlphabet() {
        Alphabet originalAlphabet = new Alphabet();
        originalAlphabet.lookupIndex("f1");

        FeatureVector fv = new FeatureVector(new Alphabet(), new int[]{0}, new double[]{1.0});
        Instance instance = new Instance(fv, "label", "name", null);

        List<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new Noop());
        Pipe pipe = new SerialPipes(pipes);
        pipe.setDataAlphabet(originalAlphabet);
        pipe.setTargetAlphabet(new LabelAlphabet());
        pipe.getTargetAlphabet().lookupLabel("label");

        double[] params = new double[MaxEnt.getNumParameters(pipe)];
        MaxEnt classifier = new MaxEnt(pipe, params);
        double[] scores = new double[1]; 

        classifier.getUnnormalizedClassificationScores(instance, scores);
    }
@Test
    public void testClassificationScoresWithTemperatureZeroShouldProduceInfinityAndHandleIt() {
        List<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        pipes.add(new Target2Label());

        Pipe pipe = new SerialPipes(pipes);
        pipe.setTargetProcessing(true);

        String[] text = {"x"};
        String[] label = {"A"};
        InstanceList insts = new InstanceList(pipe);
        insts.addThruPipe(new StringArrayIterator(text, label));

        MaxEnt classifier = new MaxEnt(pipe, new double[MaxEnt.getNumParameters(pipe)]);
        double[] scores = new double[pipe.getTargetAlphabet().size()];

        classifier.getClassificationScoresWithTemperature(insts.get(0), 0.0000001, scores);

        assertEquals(1.0, scores[0], 1e-5); 
    }
@Test
    public void testPrintHandlesNonZeroWeights() throws UnsupportedEncodingException {
        List<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        pipes.add(new Target2Label());

        Pipe pipe = new SerialPipes(pipes);
        pipe.setTargetProcessing(true);

        String[] data = {"x y"};
        String[] targets = {"Z"};
        InstanceList list = new InstanceList(pipe);
        list.addThruPipe(new StringArrayIterator(data, targets));

        int numParams = MaxEnt.getNumParameters(pipe);
        double[] params = new double[numParams];
        params[1] = 9.99;

        MaxEnt classifier = new MaxEnt(pipe, params);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(bos, true, "UTF-8");
        classifier.print(ps);
        String result = bos.toString("UTF-8");

        assertTrue(result.contains("FEATURES FOR CLASS"));
        assertTrue(result.contains("9.99"));
    }
@Test
    public void testSerializationWithNullFeatureSelections() throws IOException, ClassNotFoundException {
        List<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        pipes.add(new Target2Label());

        Pipe pipe = new SerialPipes(pipes);
        pipe.setTargetProcessing(true);

        InstanceList instances = new InstanceList(pipe);
        instances.addThruPipe(new StringArrayIterator(new String[]{"abc"}, new String[]{"xyz"}));

        int numParams = MaxEnt.getNumParameters(pipe);
        double[] params = new double[numParams];
        params[0] = 1.1;

        MaxEnt classifier = new MaxEnt(pipe, params);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject(classifier);
        out.close();

        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        ObjectInputStream in = new ObjectInputStream(bis);
        MaxEnt deserialized = (MaxEnt) in.readObject();

        assertNotNull(deserialized);
        assertNull(deserialized.getFeatureSelection());
        assertNull(deserialized.getPerClassFeatureSelection());
    }
@Test
    public void testPrintExtremeFeaturesPrintsBothTopAndBottomWeights() throws UnsupportedEncodingException {
        List<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        pipes.add(new Target2Label());

        Pipe pipe = new SerialPipes(pipes);
        pipe.setTargetProcessing(true);
        String[] text = {"a b c"};
        String[] labels = {"L"};
        InstanceList instances = new InstanceList(pipe);
        instances.addThruPipe(new StringArrayIterator(text, labels));

        int size = MaxEnt.getNumParameters(pipe);
        double[] parameters = new double[size];
        parameters[0] = -3.3;
        parameters[1] = 8.8;

        MaxEnt classifier = new MaxEnt(pipe, parameters);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(bos, "UTF-8"));
        classifier.printExtremeFeatures(writer, 1);
        writer.flush();
        String output = bos.toString("UTF-8");

        assertTrue(output.contains("<default>"));
        assertTrue(output.contains("FEATURES FOR CLASS"));
    }
@Test
    public void testSetParameterAtDefaultFeatureIndex() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("feat1");
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupLabel("label1");

        SerialPipes pipes = new SerialPipes(new ArrayList<Pipe>());
        pipes.setDataAlphabet(dataAlphabet);
        pipes.setTargetAlphabet(labelAlphabet);
        Pipe pipe = pipes;

        int numParams = MaxEnt.getNumParameters(pipe);
        double[] parameters = new double[numParams];
        MaxEnt classifier = new MaxEnt(pipe, parameters);

        int classIndex = 0;
        int defaultFeatureIndex = classifier.getDefaultFeatureIndex();

        double value = 3.1415;
        classifier.setParameter(classIndex, defaultFeatureIndex, value);

        double[] resultParams = classifier.getParameters();
        double actual = resultParams[classIndex * (dataAlphabet.size() + 1) + defaultFeatureIndex];
        assertEquals(value, actual, 1e-6);
    }
@Test
    public void testGetClassificationScoresWithZeroGradientInputs() {
        ArrayList<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        pipes.add(new Target2Label());

        Pipe pipe = new SerialPipes(pipes);
        pipe.setTargetProcessing(true);

        InstanceList list = new InstanceList(pipe);
        list.addThruPipe(new StringArrayIterator(new String[]{""}, new String[]{"label"}));

        int paramSize = MaxEnt.getNumParameters(pipe);
        double[] parameters = new double[paramSize]; 

        MaxEnt classifier = new MaxEnt(pipe, parameters);
        Instance instance = list.get(0);

        int numClasses = pipe.getTargetAlphabet().size();
        double[] scores = new double[numClasses];
        classifier.getClassificationScores(instance, scores);

        
        assertEquals(1.0, scores[0], 1e-9);
    }
@Test
    public void testSetFeatureSelectionReturnsSameInstance() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("f1");
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupLabel("L");

        SerialPipes pipes = new SerialPipes(new ArrayList<Pipe>());
        pipes.setDataAlphabet(dataAlphabet);
        pipes.setTargetAlphabet(labelAlphabet);

        Pipe pipe = pipes;
        double[] parameters = new double[MaxEnt.getNumParameters(pipe)];
        MaxEnt classifier = new MaxEnt(pipe, parameters);

        FeatureSelection fs = new FeatureSelection(dataAlphabet);
        MaxEnt result = classifier.setFeatureSelection(fs);

        assertSame(classifier, result);
        assertSame(fs, result.getFeatureSelection());
    }
@Test
    public void testSetPerClassFeatureSelectionReturnsSelfAndStoresReference() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("x");
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupLabel("A");
        labelAlphabet.lookupLabel("B");

        SerialPipes pipes = new SerialPipes(new ArrayList<Pipe>());
        pipes.setDataAlphabet(dataAlphabet);
        pipes.setTargetAlphabet(labelAlphabet);

        Pipe pipe = pipes;

        double[] parameters = new double[MaxEnt.getNumParameters(pipe)];
        MaxEnt classifier = new MaxEnt(pipe, parameters);

        FeatureSelection[] fss = new FeatureSelection[2];
        fss[0] = new FeatureSelection(dataAlphabet);
        fss[1] = new FeatureSelection(dataAlphabet);

        MaxEnt returned = classifier.setPerClassFeatureSelection(fss);

        assertSame(classifier, returned);
        assertSame(fss, returned.getPerClassFeatureSelection());
    }
@Test
    public void testGetNumParametersWithMultipleLabelsAndFeatures() {
        Alphabet data = new Alphabet();
        data.lookupIndex("f1");
        data.lookupIndex("f2");
        data.lookupIndex("f3");

        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupLabel("L1");
        labels.lookupLabel("L2");

        SerialPipes pipes = new SerialPipes(new ArrayList<Pipe>());
        pipes.setDataAlphabet(data);
        pipes.setTargetAlphabet(labels);

        int expected = (data.size() + 1) * labels.size(); 
        int actual = MaxEnt.getNumParameters(pipes);
        assertEquals(8, actual);
        assertEquals(expected, actual);
    }
@Test
    public void testPrintPrintStreamDelegatesToPrintWriter() throws UnsupportedEncodingException {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("f1");
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupLabel("label");

        SerialPipes pipes = new SerialPipes(new ArrayList<Pipe>());
        pipes.setDataAlphabet(dataAlphabet);
        pipes.setTargetAlphabet(labelAlphabet);

        int numParams = MaxEnt.getNumParameters(pipes);
        double[] params = new double[numParams];
        params[0] = 42.0;

        MaxEnt classifier = new MaxEnt(pipes, params);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(bos, true, "UTF-8");

        classifier.print(ps);
        ps.flush();
        String content = bos.toString("UTF-8");

        assertTrue(content.contains("FEATURES FOR CLASS"));
    }
@Test
    public void testReadWriteObjectRoundTripWithFeatureSelections() throws IOException, ClassNotFoundException {
        Alphabet data = new Alphabet();
        data.lookupIndex("one");
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupLabel("X");
        labels.lookupLabel("Y");

        SerialPipes pipes = new SerialPipes(new ArrayList<Pipe>());
        pipes.setDataAlphabet(data);
        pipes.setTargetAlphabet(labels);

        int paramCount = MaxEnt.getNumParameters(pipes);
        double[] parameters = new double[paramCount];
        parameters[0] = 0.11;
        parameters[1] = 0.22;

        MaxEnt classifier = new MaxEnt(pipes, parameters);
        classifier.setFeatureSelection(new FeatureSelection(data));

        FeatureSelection[] perClass = new FeatureSelection[2];
        perClass[0] = new FeatureSelection(data);
        perClass[1] = null;
        classifier.setPerClassFeatureSelection(perClass);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(classifier);
        oos.close();

        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bis);
        MaxEnt restored = (MaxEnt) ois.readObject();

        assertNotNull(restored);
        assertNotNull(restored.getFeatureSelection());
        assertNotNull(restored.getPerClassFeatureSelection());
        assertNull(restored.getPerClassFeatureSelection()[1]);
    }
@Test
    public void testClassificationScoresWithNegativeWeights() {
        ArrayList<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        pipes.add(new Target2Label());

        Pipe pipe = new SerialPipes(pipes);
        pipe.setTargetProcessing(true);

        String[] docs = {"x y"};
        String[] labels = {"label"};
        InstanceList list = new InstanceList(pipe);
        list.addThruPipe(new StringArrayIterator(docs, labels));

        int numParams = MaxEnt.getNumParameters(pipe);
        double[] parameters = new double[numParams];
        parameters[0] = -5.0;

        MaxEnt classifier = new MaxEnt(pipe, parameters);
        double[] scores = new double[1];
        classifier.getClassificationScores(list.get(0), scores);

        assertEquals(1.0, scores[0], 1e-8); 
    }
@Test
    public void testRankedFeatureVectorPrintingDoesNotCrash() throws UnsupportedEncodingException {
        ArrayList<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        pipes.add(new Target2Label());

        Pipe pipe = new SerialPipes(pipes);
        pipe.setTargetProcessing(true);

        String[] x = {"x y z q"};
        String[] y = {"LABEL"};
        InstanceList list = new InstanceList(pipe);
        list.addThruPipe(new StringArrayIterator(x, y));

        int paramSize = MaxEnt.getNumParameters(pipe);
        double[] weights = new double[paramSize];
        weights[0] = 1.3;
        weights[1] = -0.9;

        MaxEnt model = new MaxEnt(pipe, weights);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(bos, "UTF-8"));
        model.printRank(pw);
        pw.flush();
        String result = bos.toString("UTF-8");

        assertTrue(result.contains("FEATURES FOR CLASS"));
        assertTrue(result.contains("<default>"));
    }
@Test
    public void testClassificationScoresWithLargeTemperature() {
        ArrayList<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        pipes.add(new Target2Label());

        Pipe pipe = new SerialPipes(pipes);
        pipe.setTargetProcessing(true);

        String[] texts = {"foo bar"};
        String[] labels = {"L"};
        InstanceList instances = new InstanceList(pipe);
        instances.addThruPipe(new StringArrayIterator(texts, labels));

        int numParams = MaxEnt.getNumParameters(pipe);
        double[] parameters = new double[numParams];
        parameters[0] = 4.0;

        MaxEnt classifier = new MaxEnt(pipe, parameters);
        Instance instance = instances.get(0);

        double[] scores = new double[pipe.getTargetAlphabet().size()];
        classifier.getClassificationScoresWithTemperature(instance, 1e6, scores);

        assertEquals(1.0, scores[0], 1e-6);
    }
@Test
    public void testClassificationWithMultipleLabelsProducesCorrectVectorLength() {
        ArrayList<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        pipes.add(new Target2Label());

        Pipe pipe = new SerialPipes(pipes);
        pipe.setTargetProcessing(true);

        String[] doc = {"word1 word2"};
        String[] label = {"A"};
        LabelAlphabet la = (LabelAlphabet) pipe.getTargetAlphabet();
        la.lookupLabel("B");
        la.lookupLabel("C");

        InstanceList list = new InstanceList(pipe);
        list.addThruPipe(new StringArrayIterator(doc, label));

        int numParams = MaxEnt.getNumParameters(pipe);
        double[] params = new double[numParams];

        MaxEnt classifier = new MaxEnt(pipe, params);
        Classification result = classifier.classify(list.get(0));
        LabelVector lv = result.getLabelVector();

        int expected = pipe.getTargetAlphabet().size();
        assertEquals(expected, lv.numLocations());
    }
@Test
    public void testPrintExtremeFeaturesForZeroWeights() throws UnsupportedEncodingException {
        Alphabet data = new Alphabet();
        data.lookupIndex("f1");
        data.lookupIndex("f2");
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupIndex("class");

        SerialPipes pipes = new SerialPipes(new ArrayList<Pipe>());
        pipes.setDataAlphabet(data);
        pipes.setTargetAlphabet(labels);

        int paramCount = MaxEnt.getNumParameters(pipes);
        double[] parameters = new double[paramCount]; 

        MaxEnt classifier = new MaxEnt(pipes, parameters);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(bos, "UTF-8"));
        classifier.printExtremeFeatures(pw, 1);
        pw.flush();
        String output = bos.toString("UTF-8");

        assertTrue(output.contains("FEATURES FOR CLASS"));
        assertTrue(output.contains("<default>"));
    }
@Test
    public void testClassificationScoresHandlesExpOverflowGracefully() {
        ArrayList<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        pipes.add(new Target2Label());

        Pipe pipe = new SerialPipes(pipes);
        pipe.setTargetProcessing(true);

        String[] data = {"a b"};
        String[] targets = {"A", "B"};
        InstanceList instanceList = new InstanceList(pipe);
        instanceList.addThruPipe(new StringArrayIterator(new String[]{"sample"}, new String[]{"B"}));

        pipe.getTargetAlphabet().lookupLabel("A");
        pipe.getTargetAlphabet().lookupLabel("B");

        int featureCount = pipe.getDataAlphabet().size();
        int labelCount = pipe.getTargetAlphabet().size();
        double[] parameters = new double[(featureCount + 1) * labelCount];

        for (int i = 0; i < parameters.length; i++) {
            parameters[i] = 700.0; 
        }

        MaxEnt classifier = new MaxEnt(pipe, parameters);
        double[] scores = new double[labelCount];
        classifier.getClassificationScores(instanceList.get(0), scores);

        double sum = scores[0] + scores[1];
        assertEquals(1.0, sum, 1e-6);
    }
@Test
    public void testDeserializationWithNullFieldsStillProvidesValidInstance() throws IOException, ClassNotFoundException {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("x");
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("Y");

        SerialPipes pipes = new SerialPipes(Collections.emptyList());
        pipes.setDataAlphabet(dataAlphabet);
        pipes.setTargetAlphabet(labelAlphabet);

        double[] parameters = new double[(dataAlphabet.size() + 1) * labelAlphabet.size()];
        MaxEnt model = new MaxEnt(pipes, parameters);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(model);
        oos.close();

        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bis);
        MaxEnt deserialized = (MaxEnt) ois.readObject();

        assertNotNull(deserialized);
        assertNotNull(deserialized.getParameters());
        assertEquals(parameters.length, deserialized.getParameters().length);
    }
@Test
    public void testGetDefaultFeatureIndexEqualsAlphabetSize() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("f1");
        dataAlphabet.lookupIndex("f2");
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("C");

        SerialPipes pipes = new SerialPipes(Collections.emptyList());
        pipes.setDataAlphabet(dataAlphabet);
        pipes.setTargetAlphabet(labelAlphabet);

        double[] parameters = new double[(dataAlphabet.size() + 1) * labelAlphabet.size()];
        MaxEnt classifier = new MaxEnt(pipes, parameters);

        assertEquals(dataAlphabet.size(), classifier.getDefaultFeatureIndex());
    }
@Test
    public void testGetFeatureSelectionAndPerClassFeatureSelectionInitiallyNull() {
        Alphabet data = new Alphabet();
        data.lookupIndex("w");
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupLabel("X");

        SerialPipes pipes = new SerialPipes(new ArrayList<Pipe>());
        pipes.setDataAlphabet(data);
        pipes.setTargetAlphabet(labels);

        double[] parameters = new double[MaxEnt.getNumParameters(pipes)];
        MaxEnt classifier = new MaxEnt(pipes, parameters);

        assertNull(classifier.getFeatureSelection());
        assertNull(classifier.getPerClassFeatureSelection());
    }
@Test
    public void testDefaultConstructorWithNullSelectionsInitializesCorrectly() {
        Alphabet data = new Alphabet();
        data.lookupIndex("a");
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupLabel("B");

        SerialPipes pipes = new SerialPipes(new ArrayList<Pipe>());
        pipes.setDataAlphabet(data);
        pipes.setTargetAlphabet(labels);

        double[] parameters = new double[MaxEnt.getNumParameters(pipes)];
        MaxEnt classifier = new MaxEnt(pipes, parameters, null, null);

        assertNotNull(classifier);
    }
@Test
    public void testSetAndGetFeatureSelectionNull() {
        Alphabet data = new Alphabet();
        data.lookupIndex("feat");
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupLabel("X");

        SerialPipes pipe = new SerialPipes(new ArrayList<Pipe>());
        pipe.setDataAlphabet(data);
        pipe.setTargetAlphabet(labels);

        int size = MaxEnt.getNumParameters(pipe);
        double[] vals = new double[size];

        MaxEnt classifier = new MaxEnt(pipe, vals);
        classifier.setFeatureSelection(null);
        assertNull(classifier.getFeatureSelection());
    }
@Test
    public void testSetAndGetPerClassFeatureSelectionNullArray() {
        Alphabet data = new Alphabet();
        data.lookupIndex("f1");
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupLabel("pos");

        SerialPipes pipe = new SerialPipes(new ArrayList<Pipe>());
        pipe.setDataAlphabet(data);
        pipe.setTargetAlphabet(labels);

        double[] parameters = new double[MaxEnt.getNumParameters(pipe)];
        MaxEnt classifier = new MaxEnt(pipe, parameters);

        classifier.setPerClassFeatureSelection(null);
        assertNull(classifier.getPerClassFeatureSelection());
    }
@Test
    public void testSerializeWithSomeNullPerClassFeatureSelections() throws IOException, ClassNotFoundException {
        Alphabet data = new Alphabet();
        data.lookupIndex("x1");
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupLabel("a");
        labels.lookupLabel("b");

        SerialPipes pipe = new SerialPipes(new ArrayList<Pipe>());
        pipe.setDataAlphabet(data);
        pipe.setTargetAlphabet(labels);

        double[] parameters = new double[MaxEnt.getNumParameters(pipe)];
        MaxEnt classifier = new MaxEnt(pipe, parameters);

        FeatureSelection[] perClass = new FeatureSelection[2];
        perClass[0] = new FeatureSelection(data);
        perClass[1] = null;
        classifier.setPerClassFeatureSelection(perClass);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject(classifier);
        out.close();

        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        ObjectInputStream in = new ObjectInputStream(bis);
        MaxEnt restored = (MaxEnt) in.readObject();

        assertNotNull(restored.getPerClassFeatureSelection());
        assertNotNull(restored.getPerClassFeatureSelection()[0]);
        assertNull(restored.getPerClassFeatureSelection()[1]);
    }
@Test(expected = ClassNotFoundException.class)
    public void testDeserializationWithMismatchedVersionThrowsException() throws Exception {
        Alphabet data = new Alphabet();
        data.lookupIndex("xx");
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupLabel("Y");

        SerialPipes pipe = new SerialPipes(Collections.emptyList());
        pipe.setDataAlphabet(data);
        pipe.setTargetAlphabet(labels);

        MaxEnt classifier = new MaxEnt(pipe, new double[MaxEnt.getNumParameters(pipe)]);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);

        oos.writeInt(999); 
        oos.close();

        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        ObjectInputStream in = new ObjectInputStream(bis);

        MaxEnt dummy = new MaxEnt(pipe, new double[0]);
        java.lang.reflect.Method readObject = MaxEnt.class.getDeclaredMethod("readObject", ObjectInputStream.class);
        readObject.setAccessible(true);
        readObject.invoke(dummy, in); 
    }
@Test
    public void testGetUnnormalizedClassificationScoresDefaultWeightsOnly() {
        ArrayList<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        pipes.add(new Target2Label());

        Pipe pipe = new SerialPipes(pipes);
        pipe.setTargetProcessing(true);
        InstanceList list = new InstanceList(pipe);
        list.addThruPipe(new StringArrayIterator(
                new String[]{"hello world"},
                new String[]{"CATEGORY"}
        ));

        int numParams = MaxEnt.getNumParameters(pipe);
        double[] parameters = new double[numParams];
        int defaultFeatureIndex = pipe.getDataAlphabet().size();
        parameters[defaultFeatureIndex] = 1.0;

        MaxEnt classifier = new MaxEnt(pipe, parameters);
        double[] scores = new double[pipe.getTargetAlphabet().size()];
        classifier.getUnnormalizedClassificationScores(list.get(0), scores);

        assertTrue(scores[0] > 0);
    }
@Test
    public void testGetClassificationScoresProducesNaNSafelyFromExtremeNegativeParameters() {
        ArrayList<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        pipes.add(new Target2Label());

        Pipe pipe = new SerialPipes(pipes);
        pipe.setTargetProcessing(true);

        String[] input = {"x y"};
        String[] label = {"A"};
        InstanceList list = new InstanceList(pipe);
        list.addThruPipe(new StringArrayIterator(input, label));

        int numParams = MaxEnt.getNumParameters(pipe);
        double[] parameters = new double[numParams];
        for (int i = 0; i < parameters.length; i++) {
            parameters[i] = -10000; 
        }

        MaxEnt classifier = new MaxEnt(pipe, parameters);
        double[] scores = new double[1];
        classifier.getClassificationScores(list.get(0), scores);

        assertEquals(1.0, scores[0], 1e-9);
    }
@Test(expected = AssertionError.class)
    public void testGetNumParametersThrowsOnUninitializedTargetAlphabet() {
        Pipe pipe = new SerialPipes(new ArrayList<Pipe>());
        pipe.setDataAlphabet(new Alphabet());

        MaxEnt.getNumParameters(pipe); 
    }
@Test(expected = AssertionError.class)
    public void testGetNumParametersThrowsOnUninitializedDataAlphabet() {
        Pipe pipe = new SerialPipes(new ArrayList<Pipe>());
        pipe.setTargetAlphabet(new LabelAlphabet());

        MaxEnt.getNumParameters(pipe); 
    }
@Test
    public void testsetDefaultFeatureIndexAndValidate() {
        Alphabet data = new Alphabet();
        data.lookupIndex("word");
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupLabel("L");

        SerialPipes pipe = new SerialPipes(new ArrayList<Pipe>());
        pipe.setDataAlphabet(data);
        pipe.setTargetAlphabet(labelAlphabet);

        double[] parameters = new double[MaxEnt.getNumParameters(pipe)];
        MaxEnt classifier = new MaxEnt(pipe, parameters);

        classifier.setDefaultFeatureIndex(99);
        assertEquals(99, classifier.getDefaultFeatureIndex());
    }
@Test
    public void testConstructionWithNullAllInputFeatureSelectionArgs() {
        Alphabet data = new Alphabet();
        data.lookupIndex("f");
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupLabel("CLASS");

        SerialPipes pipe = new SerialPipes(new ArrayList<Pipe>());
        pipe.setDataAlphabet(data);
        pipe.setTargetAlphabet(labels);

        double[] parameters = new double[MaxEnt.getNumParameters(pipe)];
        MaxEnt classifier = new MaxEnt(pipe, parameters, null, null);

        assertNotNull(classifier);
    }
@Test
    public void testGetClassificationScoresWithEmptyScoresArray() {
        ArrayList<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        pipes.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipes);
        pipe.setTargetProcessing(true);

        InstanceList instList = new InstanceList(pipe);
        instList.addThruPipe(new StringArrayIterator(new String[]{"sample text"}, new String[]{"label"}));

        int paramSize = MaxEnt.getNumParameters(pipe);
        double[] parameters = new double[paramSize];

        MaxEnt classifier = new MaxEnt(pipe, parameters);
        Instance instance = instList.get(0);

        double[] scores = new double[0];

        try {
            classifier.getClassificationScores(instance, scores);
            fail("Expected ArrayIndexOutOfBoundsException not thrown");
        } catch (ArrayIndexOutOfBoundsException expected) {
            
        }
    }
@Test
    public void testClassifyWithSingleHighValueScore() {
        ArrayList<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        pipes.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipes);
        pipe.setTargetProcessing(true);

        InstanceList list = new InstanceList(pipe);
        list.addThruPipe(new StringArrayIterator(new String[]{"text"}, new String[]{"L1"}));
        ((LabelAlphabet) pipe.getTargetAlphabet()).lookupLabel("L2");

        int numParams = MaxEnt.getNumParameters(pipe);
        double[] parameters = new double[numParams];
        parameters[0] = 1000; 

        MaxEnt classifier = new MaxEnt(pipe, parameters);
        Classification result = classifier.classify(list.get(0));

        assertNotNull(result);
        double confidence = result.getLabelVector().value(0);
        assertTrue("Expected score near 1.0", confidence > 0.999);
    }
@Test
    public void testClassificationScoresWithZeroTemperature() {
        ArrayList<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        pipes.add(new Target2Label());

        Pipe pipe = new SerialPipes(pipes);
        pipe.setTargetProcessing(true);

        InstanceList instList = new InstanceList(pipe);
        instList.addThruPipe(new StringArrayIterator(new String[]{"doc"}, new String[]{"labelA"}));

        int numParams = MaxEnt.getNumParameters(pipe);
        double[] parameters = new double[numParams];
        parameters[0] = 5.5;

        MaxEnt classifier = new MaxEnt(pipe, parameters);

        double[] scores = new double[pipe.getTargetAlphabet().size()];
        try {
            classifier.getClassificationScoresWithTemperature(instList.get(0), 0.0, scores);
            fail("Expected ArithmeticException (divide by zero) not thrown");
        } catch (ArithmeticException expected) {
            
        }
    }
@Test
    public void testPrintWriterOutputWithOneLabelOneFeature() throws Exception {
        Alphabet data = new Alphabet();
        data.lookupIndex("word");
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupLabel("classX");

        SerialPipes pipe = new SerialPipes(new ArrayList<Pipe>());
        pipe.setDataAlphabet(data);
        pipe.setTargetAlphabet(labels);

        int numParams = MaxEnt.getNumParameters(pipe);
        double[] params = new double[numParams];
        params[0] = 1.5;
        params[1] = 2.7;

        MaxEnt classifier = new MaxEnt(pipe, params);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(bos, "UTF-8"));
        classifier.print(pw);
        pw.flush();
        String result = bos.toString("UTF-8");

        assertTrue(result.contains("FEATURES FOR CLASS"));
        assertTrue(result.contains("<default>"));
    }
@Test
    public void testPrintRankWithZeroFeaturesOutputsHeaderOnly() throws Exception {
        Alphabet data = new Alphabet();
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupLabel("Z");

        SerialPipes pipe = new SerialPipes(new ArrayList<Pipe>());
        pipe.setDataAlphabet(data);
        pipe.setTargetAlphabet(labels);

        MaxEnt classifier = new MaxEnt(pipe, new double[MaxEnt.getNumParameters(pipe)]);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(bos, "UTF-8"));
        classifier.printRank(pw);
        pw.flush();
        String result = bos.toString("UTF-8");

        assertTrue(result.contains("FEATURES FOR CLASS"));
        assertTrue(result.contains("<default>"));
    }
@Test
    public void testPrintExtremeFeaturesWithKGreaterThanNumFeatures() throws Exception {
        Alphabet data = new Alphabet();
        data.lookupIndex("abc");
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupLabel("classZ");

        SerialPipes pipe = new SerialPipes(new ArrayList<Pipe>());
        pipe.setDataAlphabet(data);
        pipe.setTargetAlphabet(labels);

        double[] params = new double[MaxEnt.getNumParameters(pipe)];
        MaxEnt classifier = new MaxEnt(pipe, params);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(bos, "UTF-8"));
        classifier.printExtremeFeatures(pw, 5);
        pw.flush();
        String result = bos.toString("UTF-8");

        assertTrue(result.contains("<default>"));
        assertTrue(result.contains("FEATURES FOR CLASS"));
    }
@Test
    public void testSerializeDeserializeWithEmptyPerClassFeatureSelectionArray() throws Exception {
        Alphabet data = new Alphabet();
        data.lookupIndex("term");
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupLabel("CLS");

        SerialPipes pipe = new SerialPipes(new ArrayList<Pipe>());
        pipe.setDataAlphabet(data);
        pipe.setTargetAlphabet(labels);

        MaxEnt classifier = new MaxEnt(pipe, new double[MaxEnt.getNumParameters(pipe)]);
        classifier.setPerClassFeatureSelection(new FeatureSelection[0]);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(classifier);
        oos.close();

        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bis);
        MaxEnt restored = (MaxEnt) ois.readObject();

        assertNotNull(restored.getPerClassFeatureSelection());
        assertEquals(0, restored.getPerClassFeatureSelection().length);
    }
@Test
    public void testClassificationScoresHandlesEmptyInstanceGracefully() {
        Alphabet a = new Alphabet();
        a.lookupIndex("token1");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupLabel("LBL");

        SerialPipes pipe = new SerialPipes(new ArrayList<Pipe>());
        pipe.setDataAlphabet(a);
        pipe.setTargetAlphabet(labelAlphabet);

        FeatureVector emptyVec = new FeatureVector(a, new int[0], new double[0]);
        Instance instance = new Instance(emptyVec, "LBL", null, null);

        MaxEnt classifier = new MaxEnt(pipe, new double[MaxEnt.getNumParameters(pipe)]);
        double[] scores = new double[1];

        classifier.getClassificationScores(instance, scores);

        assertEquals(1.0, scores[0], 1e-5); 
    }
@Test
    public void testRankedFeatureVectorToleranceRankingEqualValues() throws Exception {
        Alphabet data = new Alphabet();
        data.lookupIndex("x");
        data.lookupIndex("y");
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupLabel("tag");

        SerialPipes pipe = new SerialPipes(new ArrayList<Pipe>());
        pipe.setDataAlphabet(data);
        pipe.setTargetAlphabet(labelAlphabet);

        double[] parameters = new double[MaxEnt.getNumParameters(pipe)];
        
        parameters[0] = 0.9;
        parameters[1] = 0.9;

        MaxEnt classifier = new MaxEnt(pipe, parameters);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(bos, "UTF-8"));
        classifier.printRank(pw);
        pw.flush();
        String str = bos.toString("UTF-8");

        assertTrue(str.contains("FEATURES FOR CLASS"));
    }
@Test
    public void testGetUnnormalizedClassificationScoresWithOnlyDefaultBias() {
        ArrayList<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        pipes.add(new Target2Label());

        Pipe pipe = new SerialPipes(pipes);
        pipe.setTargetProcessing(true);

        InstanceList list = new InstanceList(pipe);
        list.addThruPipe(new StringArrayIterator(
                new String[]{"sample input"}, new String[]{"label1"}
        ));

        int defaultFeatureIdx = pipe.getDataAlphabet().size();
        int numParams = MaxEnt.getNumParameters(pipe);
        double[] parameters = new double[numParams];
        parameters[defaultFeatureIdx] = 2.0;

        MaxEnt model = new MaxEnt(pipe, parameters);
        double[] scores = new double[pipe.getTargetAlphabet().size()];
        model.getUnnormalizedClassificationScores(list.get(0), scores);

        assertTrue(scores[0] > 0);
    }
@Test
    public void testSetParameterOutOfRangeIndexDoesNotThrowWhenArrayResizedBefore() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("one");
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupLabel("class");

        SerialPipes pipes = new SerialPipes(Collections.emptyList());
        pipes.setDataAlphabet(dataAlphabet);
        pipes.setTargetAlphabet(labelAlphabet);

        int size = MaxEnt.getNumParameters(pipes);
        double[] parameters = new double[size];

        MaxEnt model = new MaxEnt(pipes, parameters);
        model.setParameter(0, 0, 3.14);

        assertEquals(3.14, model.getParameters()[0], 1e-6);
    }
@Test
    public void testClassificationScoresWithAllZeroWeightsProducesUniformDistribution() {
        ArrayList<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        pipes.add(new Target2Label());

        Pipe pipe = new SerialPipes(pipes);
        pipe.setTargetProcessing(true);

        InstanceList list = new InstanceList(pipe);
        list.addThruPipe(new StringArrayIterator(
                new String[]{"a b"}, new String[]{"X"}
        ));
        ((LabelAlphabet) pipe.getTargetAlphabet()).lookupLabel("Y");

        int numParams = MaxEnt.getNumParameters(pipe);
        double[] parameters = new double[numParams];

        MaxEnt classifier = new MaxEnt(pipe, parameters);
        double[] scores = new double[2];
        classifier.getClassificationScores(list.get(0), scores);

        assertEquals(0.5, scores[0], 1e-6);
        assertEquals(0.5, scores[1], 1e-6);
    }
@Test
    public void testClassificationScoresHandlesInfiniteWeights() {
        ArrayList<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        pipes.add(new Target2Label());
        Pipe pipe = new SerialPipes(pipes);
        pipe.setTargetProcessing(true);

        InstanceList instances = new InstanceList(pipe);
        instances.addThruPipe(new StringArrayIterator(new String[]{"text text"}, new String[]{"cat"}));
        ((LabelAlphabet) pipe.getTargetAlphabet()).lookupLabel("dog");

        int paramCount = MaxEnt.getNumParameters(pipe);
        double[] params = new double[paramCount];
        params[0] = Double.POSITIVE_INFINITY;

        MaxEnt classifier = new MaxEnt(pipe, params);
        double[] scores = new double[pipe.getTargetAlphabet().size()];
        classifier.getClassificationScores(instances.get(0), scores);

        assertEquals(1.0, scores[0], 1e-6);
        assertEquals(0.0, scores[1], 1e-6);
    }
@Test
    public void testDeserializeHandlesNullClassFeatureSelectionEntries() throws Exception {
        Alphabet data = new Alphabet();
        data.lookupIndex("x");
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupLabel("L1");
        labels.lookupLabel("L2");

        SerialPipes pipe = new SerialPipes(Collections.emptyList());
        pipe.setDataAlphabet(data);
        pipe.setTargetAlphabet(labels);

        double[] parameters = new double[MaxEnt.getNumParameters(pipe)];
        MaxEnt model = new MaxEnt(pipe, parameters);
        FeatureSelection[] fsa = new FeatureSelection[2];
        fsa[0] = new FeatureSelection(data);
        fsa[1] = null;
        model.setPerClassFeatureSelection(fsa);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject(model);
        out.close();

        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        ObjectInputStream in = new ObjectInputStream(bis);
        MaxEnt restored = (MaxEnt) in.readObject();

        assertNotNull(restored.getPerClassFeatureSelection());
        assertNull(restored.getPerClassFeatureSelection()[1]);
        assertNotNull(restored.getPerClassFeatureSelection()[0]);
    }
@Test
    public void testClassificationScoresDoesNotReturnNaNWhenInputIsZeroVector() {
        Alphabet data = new Alphabet();
        data.lookupIndex("tok1");
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupLabel("tag");

        SerialPipes pipe = new SerialPipes(Collections.emptyList());
        pipe.setDataAlphabet(data);
        pipe.setTargetAlphabet(labels);

        FeatureVector fv = new FeatureVector(data, new int[0]);
        Instance instance = new Instance(fv, "tag", null, null);

        MaxEnt classifier = new MaxEnt(pipe, new double[MaxEnt.getNumParameters(pipe)]);
        double[] scores = new double[1];
        classifier.getClassificationScores(instance, scores);

        assertEquals(1.0, scores[0], 1e-6);
        assertFalse(Double.isNaN(scores[0]));
    }
@Test
    public void testPrintWithMultipleFeaturesAndClasses() throws Exception {
        Alphabet data = new Alphabet();
        data.lookupIndex("f1");
        data.lookupIndex("f2");

        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupLabel("c1");
        labels.lookupLabel("c2");

        SerialPipes pipe = new SerialPipes(Collections.emptyList());
        pipe.setDataAlphabet(data);
        pipe.setTargetAlphabet(labels);

        double[] parameters = new double[MaxEnt.getNumParameters(pipe)];
        for (int i = 0; i < parameters.length; i++) {
            parameters[i] = i * 1.0;
        }

        MaxEnt classifier = new MaxEnt(pipe, parameters);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(bos, "UTF-8"));
        classifier.print(writer);
        writer.flush();
        String output = bos.toString("UTF-8");

        assertTrue(output.contains("FEATURES FOR CLASS"));
        assertTrue(output.contains("f1"));
        assertTrue(output.contains("f2"));
        assertTrue(output.contains("<default>"));
    }
@Test
    public void testConstructionWithNullPipeTargetAlphabetThrowsException() {
        Pipe pipe = new SerialPipes(Collections.emptyList());
        pipe.setDataAlphabet(new Alphabet());
        

        try {
            MaxEnt.getNumParameters(pipe);
            fail("Expected AssertionError because target alphabet was not set");
        } catch (AssertionError expected) {
            
        }
    } 
}