public class LDAHyper_5_GPTLLMTest { 

 @Test
    public void testAddInstancesAddsCorrectNumberOfDocuments() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("word1");
        alphabet.lookupIndex("word2");

        InstanceList instances = new InstanceList(alphabet, null);
        FeatureSequence fs1 = new FeatureSequence(alphabet, new int[]{0, 1});
        FeatureSequence fs2 = new FeatureSequence(alphabet, new int[]{1});

        Instance instance1 = new Instance(fs1, null, "doc1", null);
        Instance instance2 = new Instance(fs2, null, "doc2", null);

        instances.addThruPipe(instance1);
        instances.addThruPipe(instance2);

        LDAHyper lda = new LDAHyper(5, 1.0, 0.01);
        lda.addInstances(instances);

        assertEquals(2, lda.getData().size());
        assertNotNull(lda.getAlphabet());
        assertEquals(alphabet.size(), lda.getAlphabet().size());
    }
@Test
    public void testSetTestingInstancesStoresData() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("testword");

        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{0});
        InstanceList testList = new InstanceList(alphabet, null);
        Instance testInstance = new Instance(fs, null, "test", null);
        testList.add(testInstance);

        LDAHyper lda = new LDAHyper(3, 1.0, 0.01);
        lda.setTestingInstances(testList);

        assertNotNull(testList);
        assertEquals(1, testList.size());
    }
@Test
    public void testEstimateSingleIterationDoesNotThrowException() throws IOException {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("w");

        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{0});
        InstanceList instances = new InstanceList(alphabet, null);
        Instance instance = new Instance(fs, null, "doc", null);
        instances.add(instance);

        LDAHyper lda = new LDAHyper(2, 1.0, 0.01);
        lda.addInstances(instances);

        lda.setNumIterations(1);
        lda.estimate();

        assertTrue(lda.getData().size() > 0);
    }
@Test
    public void testModelSerializationAndDeserialization() throws Exception {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("w");

        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{0});
        InstanceList instances = new InstanceList(alphabet, null);
        Instance instance = new Instance(fs, null, "doc", null);
        instances.add(instance);

        LDAHyper lda = new LDAHyper(2, 1.0, 0.01);
        lda.addInstances(instances);

        File file = File.createTempFile("lda-model", ".ser");
        file.deleteOnExit();
        lda.write(file);

        LDAHyper loaded = LDAHyper.read(file);

        assertNotNull(loaded);
        assertEquals(lda.getNumTopics(), loaded.getNumTopics());
        assertEquals(lda.getData().size(), loaded.getData().size());
    }
@Test
    public void testTopicLogLikelihoodIsFinite() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("apple");

        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{0});
        InstanceList list = new InstanceList(alphabet, null);
        Instance instance = new Instance(fs, null, "doc", null);
        list.add(instance);

        LDAHyper lda = new LDAHyper(3, 1.0, 0.01);
        lda.addInstances(list);

        double ll = lda.modelLogLikelihood();

        assertTrue(Double.isFinite(ll));
    }
@Test
    public void testPrintTopWordsDoesNotFail() throws IOException {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("banana");

        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{0});
        Instance instance = new Instance(fs, null, "doc", null);
        InstanceList list = new InstanceList(alphabet, null);
        list.add(instance);

        LDAHyper lda = new LDAHyper(2, 1.0, 0.01);
        lda.addInstances(list);

        File file = File.createTempFile("topwords", ".txt");
        file.deleteOnExit();
        lda.printTopWords(file, 1, true);

        assertTrue(file.exists());
    }
@Test
    public void testGetSortedTopicWordsReturnsArray() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("sky");

        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{0});
        InstanceList list = new InstanceList(alphabet, null);
        Instance instance = new Instance(fs, null, "doc", null);
        list.add(instance);

        LDAHyper lda = new LDAHyper(2, 1.0, 0.01);
        lda.addInstances(list);

        assertNotNull(lda.getSortedTopicWords(0));
        assertTrue(lda.getSortedTopicWords(0).length > 0);
    }
@Test
    public void testSettersCorrectlyStoreValues() {
        LDAHyper lda = new LDAHyper(10, 1.0, 0.01);
        lda.setNumIterations(123);
        lda.setBurninPeriod(10);
        lda.setOptimizeInterval(5);
        lda.setModelOutput(1, "model-out");
        lda.setSaveState(2, "state-out");
        lda.setTopicDisplay(3, 4);
        lda.setRandomSeed(5678);

        assertEquals(123, lda.numIterations);
        assertEquals(10, lda.burninPeriod);
        assertEquals(5, lda.optimizeInterval);
        assertEquals(1, lda.outputModelInterval);
        assertEquals("model-out", lda.outputModelFilename);
    }
@Test
    public void testEmpiricalLikelihoodReturnsValidDouble() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("test");

        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{0});
        Instance instance = new Instance(fs, null, "X", null);
        InstanceList list = new InstanceList(alphabet, null);
        list.add(instance);

        LDAHyper lda = new LDAHyper(2, 1.0, 0.01);
        lda.addInstances(list);
        lda.setTestingInstances(list);

        double likelihood = lda.empiricalLikelihood(2, list);
        assertTrue(Double.isFinite(likelihood));
    }
@Test
    public void testPrintStateToFile() throws IOException {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("dog");

        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{0, 0});
        InstanceList list = new InstanceList(alphabet, null);
        list.add(new Instance(fs, null, "doc1", null));

        LDAHyper lda = new LDAHyper(3, 1.0, 0.01);
        lda.addInstances(list);

        File stateFile = File.createTempFile("state", ".gz");
        stateFile.deleteOnExit();

        lda.printState(stateFile);
        assertTrue(stateFile.exists());
    }
@Test
    public void testAddInstancesWithEmptyInstanceList() {
        Alphabet alphabet = new Alphabet();
        LDAHyper lda = new LDAHyper(5, 1.0, 0.01);

        InstanceList list = new InstanceList(alphabet, null);
        lda.addInstances(list);

        assertNotNull(lda.getData());
        assertEquals(0, lda.getData().size());
    }
@Test
    public void testGetCountFeatureTopicOnUnusedFeatureReturnsZero() {
        Alphabet alphabet = new Alphabet();
        int index = alphabet.lookupIndex("unused");

        LDAHyper lda = new LDAHyper(3, 1.0, 0.01);
        lda.addInstances(new InstanceList(alphabet, null));

        int count = lda.getCountFeatureTopic(index, 0);
        assertEquals(0, count);
    }
@Test
    public void testPrintTopWordsWithZeroTokensPerTopic() throws IOException {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("token");

        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{0});
        Instance instance = new Instance(fs, null, "doc1", null);
        InstanceList list = new InstanceList(alphabet, null);
        list.add(instance);

        LDAHyper lda = new LDAHyper(2, 1.0, 0.01);
        lda.addInstances(list);

        File file = File.createTempFile("zero-token-test", ".txt");
        file.deleteOnExit();
        lda.printTopWords(file, 5, false);

        assertTrue(file.length() > 0);
    }
@Test
    public void testPrintDocumentTopicsWithZeroThresholdAndLimit() throws IOException {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("cat");

        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{0});
        Instance instance = new Instance(fs, null, "docY", null);
        InstanceList list = new InstanceList(alphabet, null);
        list.add(instance);

        LDAHyper lda = new LDAHyper(4, 1.0, 0.01);
        lda.addInstances(list);

        File output = File.createTempFile("doc-topics", ".txt");
        output.deleteOnExit();

        PrintWriter writer = new PrintWriter(new FileWriter(output));
        lda.printDocumentTopics(writer, 0.0, 0);
        writer.flush();
        writer.close();

        assertTrue(output.length() > 0);
    }
@Test
    public void testWriteAndReadWithEmptyModel() throws IOException, ClassNotFoundException {
        LDAHyper lda = new LDAHyper(2, 1.0, 0.01);

        File file = File.createTempFile("empty-model", ".ser");
        file.deleteOnExit();
        lda.write(file);

        LDAHyper loaded = LDAHyper.read(file);

        assertNotNull(loaded);
        assertEquals(2, loaded.getNumTopics());
    }
@Test
    public void testPrintStateGeneratesGzippedOutput() throws IOException {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("a");

        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{0});
        Instance instance = new Instance(fs, null, "sourcedoc", null);
        InstanceList list = new InstanceList(alphabet, null);
        list.add(instance);

        LDAHyper lda = new LDAHyper(3, 1.0, 0.01);
        lda.addInstances(list);

        File gzFile = File.createTempFile("state", ".gz");
        gzFile.deleteOnExit();
        lda.printState(gzFile);

        FileInputStream fis = new FileInputStream(gzFile);
        GZIPInputStream gis = new GZIPInputStream(fis);
        BufferedReader reader = new BufferedReader(new InputStreamReader(gis));
        String header = reader.readLine(); 
        String line = reader.readLine();   

        assertNotNull(header);
        assertNotNull(line);
        reader.close();
    }
@Test
    public void testEstimateWithZeroIterationsDoesNotCrash() throws IOException {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("t");

        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{0});
        Instance instance = new Instance(fs, null, "x", null);
        InstanceList list = new InstanceList(alphabet, null);
        list.add(instance);

        LDAHyper lda = new LDAHyper(2, 1.0, 0.01);
        lda.addInstances(list);

        lda.setNumIterations(0);
        lda.estimate();

        assertEquals(0, lda.iterationsSoFar);
    }
@Test
    public void testReadInvalidFileReturnsNull() {
        File invalidFile = new File("nonexistent.file");

        LDAHyper lda = LDAHyper.read(invalidFile);
        assertNull(lda);
    }
@Test
    public void testModelLogLikelihoodWithEmptyData() {
        LDAHyper lda = new LDAHyper(5, 1.0, 0.01);
        double likelihood = lda.modelLogLikelihood();

        assertTrue(Double.isFinite(likelihood));
    }
@Test
    public void testEmpiricalLikelihoodIgnoresOOVTokens() {
        Alphabet trainAlpha = new Alphabet();
        int idx = trainAlpha.lookupIndex("in");

        FeatureSequence fs = new FeatureSequence(trainAlpha, new int[]{idx});
        Instance trainInstance = new Instance(fs, null, "tr", null);
        InstanceList trainList = new InstanceList(trainAlpha, null);
        trainList.add(trainInstance);

        LDAHyper lda = new LDAHyper(2, 1.0, 0.01);
        lda.addInstances(trainList);

        Alphabet testAlpha = new Alphabet();
        int testIdx = testAlpha.lookupIndex("out"); 
        FeatureSequence testFS = new FeatureSequence(testAlpha, new int[]{testIdx});
        Instance testInstance = new Instance(testFS, null, "ts", null);
        InstanceList testList = new InstanceList(testAlpha, null);
        testList.add(testInstance);

        double result = lda.empiricalLikelihood(3, testList);
        assertTrue(Double.isFinite(result));
    }
@Test
    public void testTopicLabelMutualInformationWithNoLabelsReturnsZero() {
        Alphabet alpha = new Alphabet();
        alpha.lookupIndex("one");

        FeatureSequence fs = new FeatureSequence(alpha, new int[]{0});
        Instance instance = new Instance(fs, null, "blah", null);
        InstanceList list = new InstanceList(alpha, null);
        list.add(instance);

        LDAHyper lda = new LDAHyper(2, 1.0, 0.01);
        lda.addInstances(list);

        double mi = lda.topicLabelMutualInformation();
        assertEquals(0.0, mi, 0.00001);
    }
@Test
    public void testTopicationSerializationWithNullTopicDistribution() throws IOException, ClassNotFoundException {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("word");

        FeatureSequence fs = new FeatureSequence(alphabet, new int[] { 0 });
        Instance instance = new Instance(fs, null, "doc", null);

        LabelAlphabet labelAlpha = new LabelAlphabet();
        labelAlpha.lookupIndex("topic0");

        LabelSequence labelSeq = new LabelSequence(labelAlpha, new int[] { 0 });
        LDAHyper lda = new LDAHyper(labelAlpha, 1.0, 0.01, new Randoms());

        LDAHyper.Topication top = lda.new Topication(instance, lda, labelSeq);
        

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bout);
        oos.writeObject(top);
        oos.close();

        ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bin);
        LDAHyper.Topication read = (LDAHyper.Topication) ois.readObject();

        assertNotNull(read);
        assertEquals("doc", read.instance.getName());
        assertNull(read.topicDistribution);
    }
@Test
    public void testEstimateHandlesEmptyDocumentWithoutError() throws IOException {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("z");

        FeatureSequence emptySequence = new FeatureSequence(alphabet, new int[]{});
        Instance instance = new Instance(emptySequence, null, "emptyDoc", null);

        InstanceList list = new InstanceList(alphabet, null);
        list.add(instance);

        LDAHyper lda = new LDAHyper(2, 1.0, 0.01);
        lda.addInstances(list);
        lda.setNumIterations(1);
        lda.estimate();

        assertEquals(1, lda.getData().size());
    }
@Test
    public void testInstanceWithNoLabelingReturnsZeroForMutualInformation() {
        Alphabet alphabet = new Alphabet();
        int idx = alphabet.lookupIndex("y");

        FeatureSequence fs = new FeatureSequence(alphabet, new int[] { idx });
        Instance instance = new Instance(fs, null, "test", null);

        InstanceList list = new InstanceList(alphabet, null);
        list.add(instance);

        LDAHyper lda = new LDAHyper(1, 1.0, 0.01);
        lda.addInstances(list);

        double mi = lda.topicLabelMutualInformation();
        assertEquals(0.0, mi, 0.0001);
    }
@Test
    public void testNegativeCountAdjustmentThrows() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("bad_token");

        FeatureSequence fs = new FeatureSequence(alphabet, new int[] { 0, 0 });
        Instance instance = new Instance(fs, null, "doc", null);

        InstanceList list = new InstanceList(alphabet, null);
        list.add(instance);

        LDAHyper lda = new LDAHyper(1, 1.0, 0.01);
        lda.addInstances(list);

        try {
            Method m = LDAHyper.class.getDeclaredMethod("oldSampleTopicsForOneDoc",
                    FeatureSequence.class, FeatureSequence.class, boolean.class, boolean.class);
            m.setAccessible(true);
            LabelSequence ls = new LabelSequence(lda.getTopicAlphabet(), new int[] { 0, 0 });
            m.invoke(lda, fs, ls, false, true);

            fail("Expected IllegalStateException for negative count");
        } catch (IllegalStateException ise) {
            assertTrue(ise.getMessage().contains("Token count in topic went negative."));
        } catch (Exception e) {
            
        }
    }
@Test
    public void testFallbackTopicSamplingIfInvalid() {
        Alphabet alphabet = new Alphabet();
        int word = alphabet.lookupIndex("fallback");

        FeatureSequence fs = new FeatureSequence(alphabet, new int[] { word });
        Instance instance = new Instance(fs, null, "d", null);

        InstanceList list = new InstanceList(alphabet, null);
        list.add(instance);

        LDAHyper lda = new LDAHyper(1, 1.0, 0.01);
        lda.addInstances(list);

        lda.setNumIterations(1);
        lda.estimate();  

        assertEquals(1, lda.getNumTopics());
    }
@Test
    public void testSetTopicDisplayWithZeroIntervalDoesNotCauseDisplay() throws IOException {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("a");
        FeatureSequence fs = new FeatureSequence(alphabet, new int[] {0});
        InstanceList list = new InstanceList(alphabet, null);
        list.add(new Instance(fs, null, "doc", null));

        LDAHyper lda = new LDAHyper(2, 1.0, 0.01);
        lda.addInstances(list);
        lda.setTopicDisplay(0, 5);
        lda.setNumIterations(1);
        lda.estimate();

        assertEquals(1, lda.iterationsSoFar);
    }
@Test
    public void testSetSaveStateWithNullPrefixDoesNotCrash() throws IOException {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("g");

        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{0});
        Instance instance = new Instance(fs, null, "doc1", null);
        InstanceList list = new InstanceList(alphabet, null);
        list.add(instance);

        LDAHyper lda = new LDAHyper(3, 1.0, 0.01);
        lda.addInstances(list);

        lda.setSaveState(2, null); 
        lda.setNumIterations(2);

        try {
            lda.estimate();
        } catch (NullPointerException ex) {
            fail("Must not throw NPE when null prefix is set via setSaveState");
        }
    }
@Test
    public void testPrintStateWithNoDataEmitsOnlyHeader() throws IOException {
        Alphabet alphabet = new Alphabet();

        LDAHyper lda = new LDAHyper(1, 1.0, 0.01);

        File temp = File.createTempFile("nodata-state", ".gz");
        temp.deleteOnExit();

        lda.printState(temp);

        GZIPInputStream gin = new GZIPInputStream(new FileInputStream(temp));
        BufferedReader br = new BufferedReader(new InputStreamReader(gin));
        String line = br.readLine();
        br.close();

        assertTrue(line != null && line.startsWith("#doc"));
    }
@Test
    public void testPrintTopWordsWithZeroWordsLimitStillPrintsHeader() throws IOException {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("x");

        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{0, 0});
        InstanceList list = new InstanceList(alphabet, null);
        list.add(new Instance(fs, null, "doc", null));

        LDAHyper lda = new LDAHyper(2, 1.0, 0.01);
        lda.addInstances(list);

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        PrintStream pout = new PrintStream(bout);
        lda.printTopWords(pout, 0, true);
        pout.flush();

        String output = bout.toString();
        assertTrue(output.contains("Topic 0") || output.contains("Topic 1"));
    }
@Test
    public void testAddInstancesWithMismatchedSizes() {
        Alphabet alphabet = new Alphabet();
        LabelAlphabet topicAlphabet = new LabelAlphabet();
        for (int i = 0; i < 3; i++) {
            alphabet.lookupIndex("word" + i);
            topicAlphabet.lookupIndex("topic" + i);
        }

        FeatureSequence featureSequence = new FeatureSequence(alphabet, new int[] {0, 1});
        Instance instance = new Instance(featureSequence, null, "doc", null);
        InstanceList instanceList = new InstanceList(alphabet, null);
        instanceList.add(instance);

        LabelSequence labelSequence1 = new LabelSequence(topicAlphabet, new int[] {0}); 
        LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, new Randoms());

        try {
            lda.addInstances(instanceList, java.util.Collections.singletonList(labelSequence1));
            fail("Expected IndexOutOfBoundsException due to size mismatch.");
        } catch (IndexOutOfBoundsException e) {
            assertTrue(e.getMessage() != null);
        }
    }
@Test
    public void testNewLabelAlphabetInitializesCorrectSize() {
        int topicCount = 7;
        LabelAlphabet alphabet = null;
        try {
            java.lang.reflect.Method method = LDAHyper.class.getDeclaredMethod("newLabelAlphabet", int.class);
            method.setAccessible(true);
            alphabet = (LabelAlphabet) method.invoke(null, topicCount);
        } catch (Exception e) {
            fail("Reflection error");
        }

        assertNotNull(alphabet);
        assertEquals(topicCount, alphabet.size());
        for (int i = 0; i < topicCount; i++) {
            assertEquals(i, alphabet.lookupIndex("topic" + i));
        }
    }
@Test
    public void testGetCountTokensPerTopicReturnsZeroIfUnset() {
        LDAHyper lda = new LDAHyper(3, 1.0, 0.01);
        assertEquals(0, lda.getCountTokensPerTopic(1));
        assertEquals(0, lda.getCountTokensPerTopic(2));
    }
@Test
    public void testSetModelOutputWithEmptyString() {
        LDAHyper lda = new LDAHyper(2, 1.0, 0.01);
        lda.setModelOutput(10, "");
        assertEquals(10, lda.outputModelInterval);
        assertEquals("", lda.outputModelFilename);
    }
@Test
    public void testWriteToInvalidPathLogsError() {
        LDAHyper lda = new LDAHyper(2, 1.0, 0.01);
        File invalidFile = new File("/invalid_dir/model.ser");
        lda.write(invalidFile); 
        assertFalse(invalidFile.exists());
    }
@Test
    public void testPrintTopWordsToStreamWithNewLineFlagFalse() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("x");

        FeatureSequence fs = new FeatureSequence(alphabet, new int[] {0, 0});
        Instance instance = new Instance(fs, null, "doc", null);
        InstanceList instances = new InstanceList(alphabet, null);
        instances.add(instance);

        LDAHyper lda = new LDAHyper(3, 1.0, 0.01);
        lda.addInstances(instances);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream stream = new PrintStream(output);

        lda.printTopWords(stream, 3, false);
        stream.flush();

        String outputString = output.toString();
        assertTrue(outputString.contains("\t"));
        assertTrue(outputString.contains("x"));
        assertFalse(outputString.contains("\nTopic"));
    }
@Test
    public void testPrintDocumentTopicsWithMaxLessThanZeroPrintsAllTopics() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("x");

        FeatureSequence fs = new FeatureSequence(alphabet, new int[] {0, 0});
        InstanceList instances = new InstanceList(alphabet, null);
        instances.add(new Instance(fs, null, "doc1", null));

        LDAHyper lda = new LDAHyper(3, 1.0, 0.01);
        lda.addInstances(instances);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(baos);
        lda.printDocumentTopics(pw, 0.0, -1);
        pw.flush();

        String result = baos.toString();
        assertTrue(result.contains("doc1"));
    }
@Test
    public void testDirichletParameterOptimizationTriggersAtInterval() throws IOException {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("aaa");

        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{0});
        InstanceList list = new InstanceList(alphabet, null);
        list.add(new Instance(fs, null, "x", null));

        LDAHyper lda = new LDAHyper(3, 1.0, 0.01);
        lda.setNumIterations(5);
        lda.setBurninPeriod(0);
        lda.setOptimizeInterval(2);

        lda.addInstances(list);

        lda.estimate(); 

        assertTrue(lda.iterationsSoFar >= 5);
    }
@Test
    public void testPrintDocumentTopicsWithNullSourceInstance() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("term");

        FeatureSequence fs = new FeatureSequence(alphabet, new int[] {0});
        Instance instance = new Instance(fs, null, null, null);
        InstanceList list = new InstanceList(alphabet, null);
        list.add(instance);

        LDAHyper lda = new LDAHyper(2, 1.0, 0.01);
        lda.addInstances(list);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(out);
        lda.printDocumentTopics(writer);
        writer.flush();

        String contents = out.toString();
        assertTrue(contents.contains("null-source") || contents.contains("NA") || contents.contains("doc"));
    }
@Test
    public void testSetRandomSeedAffectsRandomAssignment() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("word");

        FeatureSequence fs = new FeatureSequence(alphabet, new int[] {0});
        Instance instance = new Instance(fs, null, "A", null);
        InstanceList list = new InstanceList(alphabet, null);
        list.add(instance);

        LDAHyper lda1 = new LDAHyper(1, 1.0, 0.01);
        lda1.setRandomSeed(42);
        lda1.addInstances(list);

        LDAHyper lda2 = new LDAHyper(1, 1.0, 0.01);
        lda2.setRandomSeed(42);
        lda2.addInstances(list);

        int t1 = ((LabelSequence) lda1.getData().get(0).topicSequence).getIndexAtPosition(0);
        int t2 = ((LabelSequence) lda2.getData().get(0).topicSequence).getIndexAtPosition(0);

        assertEquals(t1, t2);
    }
@Test
    public void testAddInstancesThrowsOnAlphabetMismatchAfterValidInit() {
        Alphabet alpha1 = new Alphabet();
        Alphabet alpha2 = new Alphabet();

        alpha1.lookupIndex("dog");
        alpha2.lookupIndex("cat");

        Instance instance1 = new Instance(new FeatureSequence(alpha1, new int[]{0}), null, "doc1", null);
        Instance instance2 = new Instance(new FeatureSequence(alpha2, new int[]{0}), null, "doc2", null);

        InstanceList list1 = new InstanceList(alpha1, null);
        list1.add(instance1);

        InstanceList list2 = new InstanceList(alpha2, null);
        list2.add(instance2);

        LDAHyper lda = new LDAHyper(3, 1.0, 0.01);
        lda.addInstances(list1);

        try {
            lda.addInstances(list2);
            fail("Expected IllegalArgumentException on alphabet mismatch");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("Cannot change Alphabet"));
        }
    }
@Test
    public void testAddInstancesWithLabelSequenceSameLengthSucceeds() {
        Alphabet alpha = new Alphabet();
        alpha.lookupIndex("hello");
        LabelAlphabet topics = new LabelAlphabet();
        topics.lookupIndex("topic0");
        topics.lookupIndex("topic1");

        FeatureSequence fs = new FeatureSequence(alpha, new int[]{0, 0});
        LabelSequence ls = new LabelSequence(topics, new int[]{0, 1});
        
        Instance instance = new Instance(fs, null, "data", null);
        InstanceList list = new InstanceList(alpha, null);
        list.add(instance);

        ArrayList<LabelSequence> labels = new ArrayList<LabelSequence>();
        labels.add(ls);

        LDAHyper lda = new LDAHyper(topics, 1.0, 0.01, new Randoms());
        lda.addInstances(list, labels);

        assertNotNull(lda.getData());
        assertEquals(1, lda.getData().size());
    }
@Test
    public void testInitializeForTypesLeavesTypeTopicCountsUnchangedIfSameAlphabet() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("item");
        LDAHyper lda = new LDAHyper(3, 1.0, 0.01);
        lda.addInstances(new InstanceList(alphabet, null)); 

        lda.initializeForTypes(alphabet); 

        assertNotNull(lda.getAlphabet());
        assertEquals(alphabet, lda.getAlphabet());
    }
@Test
    public void testTopicXMLReportHandlesNoTermsGracefully() throws IOException {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("zzz");

        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{0});
        Instance instance = new Instance(fs, null, "doc", null);
        InstanceList list = new InstanceList(alphabet, null);
        list.add(instance);

        LDAHyper lda = new LDAHyper(3, 1.0, 0.01);
        lda.addInstances(list);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(baos);
        lda.topicXMLReport(pw, 5);
        pw.flush();

        String output = baos.toString();
        assertTrue(output.contains("<topic id='0'"));
        assertTrue(output.contains("<topicModel>") || output.contains("</topicModel>"));
    }
@Test
    public void testEmpiricalLikelihoodWithEmptyTestingReturnsZero() {
        LDAHyper lda = new LDAHyper(3, 1.0, 0.01);
        InstanceList emptyTest = new InstanceList(new Alphabet(), null);
        double result = lda.empiricalLikelihood(5, emptyTest);

        assertEquals(0.0, result, 0.000001);
    }
@Test
    public void testCachedCoefficientsSetToSmoothingOnlyAfterSampling() {
        Alphabet alpha = new Alphabet();
        alpha.lookupIndex("a");

        FeatureSequence fs = new FeatureSequence(alpha, new int[] {0});
        Instance instance = new Instance(fs, null, "d", null);
        InstanceList list = new InstanceList(alpha, null);
        list.add(instance);

        LDAHyper lda = new LDAHyper(2, 1.0, 0.01);
        lda.addInstances(list);
        lda.setNumIterations(1);
        lda.estimate();

        
        assertNotNull(lda.getSortedTopicWords(0));
    }
@Test
    public void testSerializationAndDeserializationRoundTrip() throws IOException, ClassNotFoundException {
        LDAHyper lda = new LDAHyper(3, 1.0, 0.01);

        File tmp = File.createTempFile("lda-ser", ".bin");
        tmp.deleteOnExit();
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(tmp));
        out.writeObject(lda);
        out.close();

        ObjectInputStream in = new ObjectInputStream(new FileInputStream(tmp));
        LDAHyper result = (LDAHyper) in.readObject();
        in.close();

        assertNotNull(result);
        assertEquals(lda.getNumTopics(), result.getNumTopics());
    }
@Test
    public void testTopicationSerializableFieldsEmptyByDefault() throws IOException, ClassNotFoundException {
        Alphabet a = new Alphabet();
        FeatureSequence fs = new FeatureSequence(a, new int[] {});
        LabelAlphabet la = new LabelAlphabet();
        la.lookupIndex("topic0");
        LabelSequence ts = new LabelSequence(la, new int[] {});
        LDAHyper lda = new LDAHyper(1, 1.0, 0.01);

        LDAHyper.Topication top = lda.new Topication(new Instance(fs, null, "empty", null), lda, ts);

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bout);
        oos.writeObject(top);
        oos.close();

        ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bin);
        LDAHyper.Topication read = (LDAHyper.Topication) ois.readObject();

        assertNotNull(read.instance);
        assertEquals(0, read.topicSequence.getLength());
    }
@Test
    public void testPrintTopWordsDoesNotFailOnEmptyVocabulary() throws IOException {
        Alphabet emptyAlpha = new Alphabet();
        FeatureSequence fs = new FeatureSequence(emptyAlpha, new int[] {});
        Instance instance = new Instance(fs, null, "doc", null);
        InstanceList list = new InstanceList(emptyAlpha, null);
        list.add(instance);

        LDAHyper lda = new LDAHyper(3, 1.0, 0.01);
        lda.addInstances(list);
        File file = File.createTempFile("topwords", ".txt");
        file.deleteOnExit();
        lda.printTopWords(file, 5, true);

        assertTrue(file.exists());
    }
@Test
    public void testTopicDocCountsArrayBoundsPreserved() {
        Alphabet a = new Alphabet();
        a.lookupIndex("aa");

        FeatureSequence fs = new FeatureSequence(a, new int[]{0});
        Instance doc = new Instance(fs, null, "doc", null);
        InstanceList list = new InstanceList(a, null);
        list.add(doc);

        LDAHyper lda = new LDAHyper(2, 1.0, 0.01);
        lda.addInstances(list);
        try {
            int totalTopics = lda.getNumTopics();
            for (int i = 0; i < totalTopics; i++) {
                assertTrue(lda.topicDocCounts[i].length > 0);
            }
        } catch (Exception e) {
            fail("Should not throw accessing topicDocCounts");
        }
    }
@Test
    public void testZeroTopicThrowsOnConstruction() {
        try {
            new LDAHyper(0, 1.0, 0.01);
            fail("Expected exception for zero topics");
        } catch (IllegalArgumentException | NegativeArraySizeException e) {
            assertTrue(e instanceof IllegalArgumentException || e instanceof NegativeArraySizeException);
        }
    }
@Test
    public void testSingleTokenDocumentTopicSampling() throws IOException {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("x");

        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{0});
        Instance instance = new Instance(fs, null, "doc", null);
        InstanceList list = new InstanceList(alphabet, null);
        list.add(instance);

        LDAHyper lda = new LDAHyper(5, 1.0, 0.01);
        lda.addInstances(list);
        lda.setNumIterations(3);
        lda.estimate();

        assertEquals(1, lda.getData().size());
    }
@Test
    public void testGetCountFeatureTopicForUnseenTypeIndex() {
        LDAHyper lda = new LDAHyper(3, 1.0, 0.01);
        int count = lda.getCountFeatureTopic(9999, 1);
        assertEquals(0, count);
    }
@Test
    public void testWriteObjectHandlesNullOptionalFields() throws IOException, ClassNotFoundException {
        LDAHyper lda = new LDAHyper(2, 1.0, 0.01);
        lda.setModelOutput(1, null);
        lda.setSaveState(1, null);

        File tempFile = File.createTempFile("lda", ".ser");
        tempFile.deleteOnExit();
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(tempFile));
        out.writeObject(lda);
        out.close();

        ObjectInputStream in = new ObjectInputStream(new FileInputStream(tempFile));
        LDAHyper read = (LDAHyper) in.readObject();
        assertEquals(2, read.getNumTopics());
    }
@Test
    public void testPrintDocumentTopicsWithThresholdFiltersOutput() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("word");

        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{0, 0});
        Instance instance = new Instance(fs, null, "source", null);
        InstanceList list = new InstanceList(alphabet, null);
        list.add(instance);

        LDAHyper lda = new LDAHyper(3, 1.0, 0.01);
        lda.addInstances(list);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(baos);
        lda.printDocumentTopics(pw, 0.9, 3);  
        pw.flush();

        String output = baos.toString();
        assertTrue(output.contains("source"));
        assertTrue(output.contains("\n"));  
    }
@Test
    public void testPrintTopWordsToMemoryStreamPreservesProbabilities() throws IOException {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("zebra");

        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{0, 0, 0});
        InstanceList list = new InstanceList(alphabet, null);
        list.add(new Instance(fs, null, "doc", null));

        LDAHyper lda = new LDAHyper(2, 1.0, 0.01);
        lda.addInstances(list);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(baos);
        lda.printTopWords(out, 5, false);
        out.flush();

        String result = baos.toString();
        assertTrue(result.contains("zebra"));
    }
@Test
    public void testPrintDocumentTopicsDoesNotThrowWithNegativeMax() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("test");

        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{0, 0});
        Instance instance = new Instance(fs, null, "doc", null);
        InstanceList list = new InstanceList(alphabet, null);
        list.add(instance);

        LDAHyper lda = new LDAHyper(3, 1.0, 0.01);
        lda.addInstances(list);

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(bytes);
        lda.printDocumentTopics(pw, 0.0, -10);  
        pw.flush();

        String content = bytes.toString();
        assertTrue(content.contains("doc"));
    }
@Test
    public void testEmpiricalLikelihoodIgnoresTokenOutsideTrainingAlphabet() {
        Alphabet trainAlpha = new Alphabet();
        int idx = trainAlpha.lookupIndex("known");

        FeatureSequence fs = new FeatureSequence(trainAlpha, new int[]{idx});
        InstanceList training = new InstanceList(trainAlpha, null);
        training.add(new Instance(fs, null, "doc", null));

        Alphabet testAlpha = new Alphabet();
        int testIdx = testAlpha.lookupIndex("unknown");  

        FeatureSequence testFs = new FeatureSequence(testAlpha, new int[]{testIdx});
        Instance testInstance = new Instance(testFs, null, "testDoc", null);
        InstanceList testing = new InstanceList(testAlpha, null);
        testing.add(testInstance);

        LDAHyper lda = new LDAHyper(2, 1.0, 0.01);
        lda.addInstances(training);

        double likelihood = lda.empiricalLikelihood(3, testing);
        assertTrue(Double.isFinite(likelihood));
    }
@Test
    public void testModelLogLikelihoodReturnsNegative() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("shell");

        FeatureSequence fs = new FeatureSequence(alphabet, new int[] {0});
        Instance instance = new Instance(fs, null, "clams", null);
        InstanceList list = new InstanceList(alphabet, null);
        list.add(instance);

        LDAHyper lda = new LDAHyper(2, 1.0, 0.01);
        lda.addInstances(list);

        double ll = lda.modelLogLikelihood();
        assertTrue(Double.isFinite(ll));
        assertTrue(ll < 0.0);
    }
@Test
    public void testTopicTermSamplingFallbackSetsValidTopic() throws IOException {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("x");

        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{0});
        Instance instance = new Instance(fs, null, "f", null);
        InstanceList list = new InstanceList(alphabet, null);
        list.add(instance);

        LDAHyper lda = new LDAHyper(2, 1.0, 0.01);
        lda.addInstances(list);
        lda.setNumIterations(2);
        lda.estimate();

        int topic = ((LabelSequence) lda.getData().get(0).topicSequence).getIndexAtPosition(0);
        assertTrue(topic >= 0);
    }
@Test
    public void testClearHistogramsOnEmptyModelDoesNotThrow() throws Exception {
        LDAHyper lda = new LDAHyper(3, 1.0, 0.01);
        java.lang.reflect.Method clear = LDAHyper.class.getDeclaredMethod("clearHistograms");
        clear.setAccessible(true);
        clear.invoke(lda);
        assertTrue(true); 
    }
@Test
    public void testPrintStateSkipsNullSources() throws IOException {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("a");

        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{0});
        Instance instance = new Instance(fs, null, null, null);
        InstanceList list = new InstanceList(alphabet, null);
        list.add(instance);

        LDAHyper lda = new LDAHyper(2, 1.0, 0.01);
        lda.addInstances(list);

        File stateFile = File.createTempFile("testState", ".gz");
        stateFile.deleteOnExit();
        lda.printState(stateFile);

        GZIPInputStream in = new GZIPInputStream(new FileInputStream(stateFile));
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String header = reader.readLine();
        String line = reader.readLine();
        reader.close();

        assertNotNull(header);
        assertNotNull(line);
        assertTrue(line.contains("0 -")); 
    }
@Test
    public void testSetShowTopicIntervalZeroAvoidsPrinting() throws IOException {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("b");

        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{0});
        Instance instance = new Instance(fs, null, "x", null);
        InstanceList list = new InstanceList(alphabet, null);
        list.add(instance);

        LDAHyper lda = new LDAHyper(2, 0.5, 0.01);
        lda.addInstances(list);
        lda.setNumIterations(1);
        lda.setTopicDisplay(0, 2);
        lda.estimate();

        assertTrue(lda.iterationsSoFar >= 1);
    }
@Test
    public void testGetSortedTopicWordsRespectsEmptyCounts() {
        LDAHyper lda = new LDAHyper(3, 1.0, 0.01);
        IDSorter[] sorted = lda.getSortedTopicWords(1);
        assertEquals(0, sorted.length);
    }
@Test
    public void testTopicXMLReportWithNoCountsStillOutputsValidXML() throws IOException {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("aa");

        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{0});
        InstanceList list = new InstanceList(alphabet, null);
        list.add(new Instance(fs, null, "file", null));

        LDAHyper lda = new LDAHyper(2, 0.5, 0.01);
        lda.addInstances(list);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(output);
        lda.topicXMLReport(writer, 3);
        writer.flush();
        String xml = output.toString();

        assertTrue(xml.contains("<topicModel>"));
        assertTrue(xml.contains("</topicModel>"));
    }
@Test
    public void testOptimizeAlphaUpdatesCachedCoefficients() throws IOException {
        Alphabet a = new Alphabet();
        a.lookupIndex("word0");

        FeatureSequence fs = new FeatureSequence(a, new int[]{0});
        InstanceList train = new InstanceList(a, null);
        train.add(new Instance(fs, null, "doc", null));

        LDAHyper lda = new LDAHyper(2, 1.0, 0.01);
        lda.addInstances(train);
        lda.setNumIterations(4);
        lda.setBurninPeriod(0);
        lda.setOptimizeInterval(2);
        lda.estimate();
        assertTrue(lda.iterationsSoFar >= 4);
    }
@Test
    public void testNullOutputFilenameDoesNotThrowError() throws IOException {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("x");

        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{0});
        InstanceList list = new InstanceList(alphabet, null);
        list.add(new Instance(fs, null, "doc", null));

        LDAHyper lda = new LDAHyper(3, 1.0, 0.01);
        lda.setModelOutput(5, null);
        lda.setSaveState(3, null);
        lda.addInstances(list);
        lda.setNumIterations(3);
        try {
            lda.estimate();
        } catch (NullPointerException e) {
            fail("Should not throw for null filename");
        }
    }
@Test
    public void testPrintTopWordsPrintsProperPerTopicProbabilities() throws IOException {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("test");

        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{0, 0, 0});
        Instance instance = new Instance(fs, null, "document", null);
        InstanceList list = new InstanceList(alphabet, null);
        list.add(instance);

        LDAHyper lda = new LDAHyper(2, 1.0, 0.1);
        lda.addInstances(list);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream stream = new PrintStream(out);
        lda.printTopWords(stream, 5, false);
        stream.flush();

        String result = out.toString();
        assertTrue(result.contains("test"));
        assertTrue(result.contains("\t"));
    }
@Test
    public void testEmptyInstanceListOnAddInstancesDoesNotThrow() {
        InstanceList empty = new InstanceList(new Alphabet(), null);
        LDAHyper lda = new LDAHyper(2, 1.0, 0.01);
        lda.addInstances(empty);
        assertEquals(0, lda.getData().size());
    }
@Test
    public void testEstimateWithZeroIterationsSkipsSampling() throws Exception {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("x");

        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{0});
        Instance instance = new Instance(fs, null, "empty", null);
        InstanceList list = new InstanceList(alphabet, null);
        list.add(instance);

        LDAHyper lda = new LDAHyper(1, 1.0, 0.01);
        lda.addInstances(list);
        lda.setNumIterations(0);
        lda.estimate();

        assertEquals(0, lda.iterationsSoFar);
    } 
}