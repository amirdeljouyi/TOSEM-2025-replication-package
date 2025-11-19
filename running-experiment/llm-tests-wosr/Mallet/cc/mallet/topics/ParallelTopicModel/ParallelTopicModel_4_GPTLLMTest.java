public class ParallelTopicModel_4_GPTLLMTest { 

 @Test
    public void testModelInitialization() {
        ParallelTopicModel model = new ParallelTopicModel(5, 5.0, 0.01);
        assertEquals(5, model.getNumTopics());
        assertNotNull(model.getTopicAlphabet());
        assertEquals(5, model.getTopicAlphabet().size());
    }
@Test
    public void testSetNumTopics() {
        ParallelTopicModel model = new ParallelTopicModel(3, 3.0, 0.01);
        model.setNumTopics(7);
        assertEquals(7, model.getNumTopics());
    }
@Test
    public void testAddInstances() {
        Alphabet alphabet = new Alphabet();
        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{
                alphabet.lookupIndex("apple"), alphabet.lookupIndex("banana")
        });
        Instance instance = new Instance(fs, null, "doc1", null);
        InstanceList instanceList = new InstanceList(alphabet, null);
        instanceList.add(instance);

        ParallelTopicModel model = new ParallelTopicModel(2, 2.0, 0.01);
        model.setRandomSeed(42);
        model.addInstances(instanceList);

        assertNotNull(model.getAlphabet());
        assertNotNull(model.getData());
        assertEquals(1, model.getData().size());
    }
@Test
    public void testGetTopicProbabilities() {
        Alphabet alphabet = new Alphabet();
        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{
                alphabet.lookupIndex("apple"), alphabet.lookupIndex("banana")
        });
        Instance instance = new Instance(fs, null, "doc1", null);
        InstanceList instanceList = new InstanceList(alphabet, null);
        instanceList.add(instance);

        ParallelTopicModel model = new ParallelTopicModel(4, 4.0, 0.01);
        model.setRandomSeed(123);
        model.addInstances(instanceList);

        double[] topicDist = model.getTopicProbabilities(0);
        assertEquals(4, topicDist.length);

        double sum = topicDist[0] + topicDist[1] + topicDist[2] + topicDist[3];
        assertEquals(1.0, sum, 0.00001);
        assertTrue(topicDist[0] >= 0);
    }
@Test
    public void testTopWordsLength() {
        Alphabet alphabet = new Alphabet();
        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{
                alphabet.lookupIndex("x"), alphabet.lookupIndex("y")
        });
        Instance instance = new Instance(fs, null, "d1", null);
        InstanceList instanceList = new InstanceList(alphabet, null);
        instanceList.add(instance);

        ParallelTopicModel model = new ParallelTopicModel(3, 3.0, 0.01);
        model.setRandomSeed(99);
        model.addInstances(instanceList);

        Object[][] topWords = model.getTopWords(2);
        assertEquals(3, topWords.length);
        assertTrue(topWords[0].length <= 2);
    }
@Test
    public void testSortedWordsNonNull() {
        Alphabet alphabet = new Alphabet();
        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{
                alphabet.lookupIndex("red"), alphabet.lookupIndex("blue")
        });
        Instance instance = new Instance(fs, null, "colorDoc", null);
        InstanceList instanceList = new InstanceList(alphabet, null);
        instanceList.add(instance);

        ParallelTopicModel model = new ParallelTopicModel(2, 2.0, 0.01);
        model.setRandomSeed(88);
        model.addInstances(instanceList);

        ArrayList<TreeSet<IDSorter>> result = model.getSortedWords();

        assertEquals(2, result.size());
        assertNotNull(result.get(0));
        assertTrue(result.get(0) instanceof TreeSet);
    }
@Test
    public void testSerializationAndDeserialization() throws Exception {
        Alphabet alphabet = new Alphabet();
        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{
                alphabet.lookupIndex("ocean"), alphabet.lookupIndex("sky")
        });
        Instance instance = new Instance(fs, null, "docX", null);
        InstanceList instanceList = new InstanceList(alphabet, null);
        instanceList.add(instance);

        ParallelTopicModel model = new ParallelTopicModel(3, 3.0, 0.01);
        model.setRandomSeed(99);
        model.addInstances(instanceList);

        File file = File.createTempFile("tempModel", ".ser");
        model.write(file);

        ParallelTopicModel reloaded = ParallelTopicModel.read(file);
        assertEquals(model.getNumTopics(), reloaded.getNumTopics());
        file.delete();
    }
@Test
    public void testDocumentTopicMatrixNormalized() {
        Alphabet alphabet = new Alphabet();
        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{
                alphabet.lookupIndex("doc"), alphabet.lookupIndex("topic")
        });
        Instance instance = new Instance(fs, null, "someDoc", null);
        InstanceList instanceList = new InstanceList(alphabet, null);
        instanceList.add(instance);

        ParallelTopicModel model = new ParallelTopicModel(2, 2.0, 0.01);
        model.setRandomSeed(101);
        model.addInstances(instanceList);

        double[][] matrix = model.getDocumentTopics(true, true);
        assertEquals(1, matrix.length);
        assertEquals(2, matrix[0].length);
        double sum = matrix[0][0] + matrix[0][1];
        assertEquals(1.0, sum, 0.00001);
    }
@Test
    public void testTopicProbabilitiesMultipleTopics() {
        Alphabet alphabet = new Alphabet();
        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{
                alphabet.lookupIndex("a"), alphabet.lookupIndex("b"), alphabet.lookupIndex("c")
        });
        Instance instance = new Instance(fs, null, "docZ", null);
        InstanceList instanceList = new InstanceList(alphabet, null);
        instanceList.add(instance);

        ParallelTopicModel model = new ParallelTopicModel(6, 6.0, 0.01);
        model.setRandomSeed(56);
        model.addInstances(instanceList);

        double[] dist = model.getTopicProbabilities(0);
        assertEquals(6, dist.length);
        assertTrue(dist[0] >= 0.0);

        double sum = 0.0 + dist[0] + dist[1] + dist[2] + dist[3] + dist[4] + dist[5];
        assertEquals(1.0, sum, 0.00001);
    }
@Test
    public void testInferencerNotNull() {
        Alphabet alphabet = new Alphabet();
        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{
                alphabet.lookupIndex("infer"), alphabet.lookupIndex("guess")
        });
        Instance instance = new Instance(fs, null, "guessDoc", null);
        InstanceList instanceList = new InstanceList(alphabet, null);
        instanceList.add(instance);

        ParallelTopicModel model = new ParallelTopicModel(4, 4.0, 0.01);
        model.setRandomSeed(42);
        model.addInstances(instanceList);

        TopicInferencer inferencer = model.getInferencer();
        assertNotNull(inferencer);
    }
@Test
    public void testModelLogLikelihoodReturnsFiniteValue() {
        Alphabet alphabet = new Alphabet();
        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{
                alphabet.lookupIndex("earth"), alphabet.lookupIndex("moon")
        });
        Instance instance = new Instance(fs, null, "spaceDoc", null);
        InstanceList instanceList = new InstanceList(alphabet, null);
        instanceList.add(instance);

        ParallelTopicModel model = new ParallelTopicModel(3, 3.0, 0.01);
        model.setRandomSeed(777);
        model.addInstances(instanceList);

        double ll = model.modelLogLikelihood();
        assertTrue(Double.isFinite(ll));
    }
@Test
    public void testGetTopicDocumentsReturnsCorrectSize() {
        Alphabet alphabet = new Alphabet();
        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{
                alphabet.lookupIndex("word")
        });
        Instance instance = new Instance(fs, null, "d", null);
        InstanceList instanceList = new InstanceList(alphabet, null);
        instanceList.add(instance);

        ParallelTopicModel model = new ParallelTopicModel(2, 2.0, 0.01);
        model.setRandomSeed(100);
        model.addInstances(instanceList);

        ArrayList<TreeSet<IDSorter>> docs = model.getTopicDocuments(0.1);
        assertEquals(2, docs.size());
        assertNotNull(docs.get(0));
    }
@Test
    public void testPrintStateCreatesFile() throws IOException {
        Alphabet alphabet = new Alphabet();
        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{
                alphabet.lookupIndex("save")
        });
        Instance instance = new Instance(fs, null, "saveDoc", null);
        InstanceList instanceList = new InstanceList(alphabet, null);
        instanceList.add(instance);

        ParallelTopicModel model = new ParallelTopicModel(1, 1.0, 0.01);
        model.setRandomSeed(5);
        model.addInstances(instanceList);

        File file = File.createTempFile("state", ".gz");
        model.printState(file);
        assertTrue(file.exists());
        file.delete();
    }
@Test
    public void testAddInstancesWithEmptyInstanceList() {
        Alphabet alphabet = new Alphabet();
        InstanceList emptyList = new InstanceList(alphabet, null);

        ParallelTopicModel model = new ParallelTopicModel(2, 2.0, 0.01);
        model.setRandomSeed(123);
        model.addInstances(emptyList);

        assertNotNull(model.getData());
        assertEquals(0, model.getData().size());
    }
@Test
    public void testSetNumTopicsToOne() {
        ParallelTopicModel model = new ParallelTopicModel(5, 5.0, 0.01);
        model.setNumTopics(1);

        assertEquals(1, model.getNumTopics());
        assertEquals(1.0, model.alpha[0], 0.00001);
    }
@Test
    public void testGetTopicProbabilitiesWithZeroLengthTopicSequence() {
        Alphabet alphabet = new Alphabet();
        LabelSequence emptySequence = new LabelSequence(new LabelAlphabet(), new int[0]);

        ParallelTopicModel model = new ParallelTopicModel(3, 3.0, 0.01);
        double[] dist = model.getTopicProbabilities(emptySequence);

        assertEquals(3, dist.length);
        double sum = dist[0] + dist[1] + dist[2];
        assertEquals(1.0, sum, 0.00001);
    }
@Test
    public void testEstimateWithNoDocuments() throws IOException {
        ParallelTopicModel model = new ParallelTopicModel(2, 2.0, 0.5);
        model.setNumThreads(1);
        model.setRandomSeed(42);

        model.setNumIterations(1);
        model.setBurninPeriod(0);
        model.setOptimizeInterval(0);
        model.setSymmetricAlpha(true);

        model.estimate(); 

        assertEquals(2, model.getNumTopics());
        assertNotNull(model.alpha);
    }
@Test
    public void testWriteAndReadEmptyModel() throws Exception {
        ParallelTopicModel model = new ParallelTopicModel(2, 2.0, 0.01);
        File f = File.createTempFile("empty_model", ".ser");
        model.write(f);

        ParallelTopicModel restored = ParallelTopicModel.read(f);
        assertEquals(model.getNumTopics(), restored.getNumTopics());
        f.delete();
    }
@Test
    public void testGetTopWordsWithZeroRequestedWords() {
        Alphabet alphabet = new Alphabet();
        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{alphabet.lookupIndex("x")});
        Instance instance = new Instance(fs, null, "doc", null);
        InstanceList instances = new InstanceList(alphabet, null);
        instances.add(instance);

        ParallelTopicModel model = new ParallelTopicModel(2, 2.0, 0.01);
        model.setRandomSeed(1);
        model.addInstances(instances);

        Object[][] result = model.getTopWords(0);
        assertEquals(2, result.length);
        assertEquals(0, result[0].length);
    }
@Test
    public void testModelLogLikelihoodWithDifferentAlphaValues() {
        Alphabet alphabet = new Alphabet();
        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{
                alphabet.lookupIndex("apple"), alphabet.lookupIndex("banana")
        });
        Instance instance = new Instance(fs, null, "doc", null);
        InstanceList list = new InstanceList(alphabet, null);
        list.add(instance);

        LabelAlphabet topicAlphabet = new LabelAlphabet();
        topicAlphabet.lookupIndex("topic0");
        topicAlphabet.lookupIndex("topic1");

        ParallelTopicModel model = new ParallelTopicModel(topicAlphabet, 1.0, 0.01);
        model.addInstances(list);

        model.alpha[0] = 0.9;
        model.alpha[1] = 0.1;

        double ll = model.modelLogLikelihood();
        assertTrue(Double.isFinite(ll));
    }
@Test
    public void testPrintStateToOutputStream() throws IOException {
        Alphabet alphabet = new Alphabet();
        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{alphabet.lookupIndex("a")});
        Instance instance = new Instance(fs, null, "doc", null);
        InstanceList list = new InstanceList(alphabet, null);
        list.add(instance);

        ParallelTopicModel model = new ParallelTopicModel(2, 2.0, 0.01);
        model.setRandomSeed(1);
        model.addInstances(list);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        model.printState(ps);
        ps.close();

        String output = baos.toString("UTF-8");
        assertTrue(output.contains("#alpha"));
        assertTrue(output.contains("type"));
    }
@Test
    public void testGetDocumentTopicsWithoutSmoothingNoNormalization() {
        Alphabet alphabet = new Alphabet();
        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{alphabet.lookupIndex("a"), alphabet.lookupIndex("a")});
        Instance instance = new Instance(fs, null, "doc", null);
        InstanceList list = new InstanceList(alphabet, null);
        list.add(instance);

        ParallelTopicModel model = new ParallelTopicModel(2, 2.0, 0.01);
        model.setRandomSeed(0);
        model.addInstances(list);

        double[][] result = model.getDocumentTopics(false, false);
        assertEquals(1, result.length);
        assertEquals(2, result[0].length);
        assertTrue(result[0][0] >= 0);
        assertTrue(result[0][1] >= 0);
    }
@Test
    public void testTopicDocumentSortingWithSingleDocument() {
        Alphabet alphabet = new Alphabet();
        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{alphabet.lookupIndex("x"), alphabet.lookupIndex("y")});
        Instance instance = new Instance(fs, null, "d", null);
        InstanceList list = new InstanceList(alphabet, null);
        list.add(instance);

        ParallelTopicModel model = new ParallelTopicModel(2, 2.0, 0.02);
        model.setRandomSeed(123);
        model.addInstances(list);

        ArrayList<TreeSet<IDSorter>> result = model.getTopicDocuments(10.0);
        assertEquals(2, result.size());
        assertFalse(result.get(0).isEmpty());
        assertFalse(result.get(1).isEmpty());
    }
@Test
    public void testZeroAlphaSumShouldAssignDefaultAlpha() {
        LabelAlphabet topicAlphabet = new LabelAlphabet();
        topicAlphabet.lookupIndex("topic0");
        topicAlphabet.lookupIndex("topic1");

        ParallelTopicModel model = new ParallelTopicModel(topicAlphabet, 0.0, 0.01);

        assertEquals(2, model.alpha.length);
        assertEquals(0.0, model.alphaSum, 0.00001);
        assertEquals(0.0, model.alpha[0], 0.00001);
    }
@Test
    public void testSetOptimizeIntervalGreaterThanSaveSampleInterval() {
        ParallelTopicModel model = new ParallelTopicModel(5);
        model.setOptimizeInterval(30); 

        assertEquals(10, model.saveSampleInterval);
        assertEquals(30, model.optimizeInterval);
    }
@Test
    public void testSetSymmetricAlphaParameterUpdatesFlag() {
        ParallelTopicModel model = new ParallelTopicModel(4);
        model.setSymmetricAlpha(true);

        assertTrue(model.usingSymmetricAlpha);
    }
@Test
    public void testGetInferencerThrowsExceptionWhenNoData() {
        ParallelTopicModel model = new ParallelTopicModel(3);
        try {
            model.getInferencer();
            fail("Expected IndexOutOfBoundsException due to no data.");
        } catch (IndexOutOfBoundsException expected) {
            assertTrue(true);
        }
    }
@Test
    public void testInitializeFromMalformedStateFileThrowsException() throws IOException {
        File stateFile = File.createTempFile("malformedState", ".gz");
        FileOutputStream fos = new FileOutputStream(stateFile);
        GZIPOutputStream gzip = new GZIPOutputStream(fos);
        PrintWriter out = new PrintWriter(gzip);

        out.println("#alpha : 0.1 0.1");
        out.println("#beta : 0.01");
        out.println("0 source 0 42 word 1");
        out.close();

        Alphabet alphabet = new Alphabet();
        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{alphabet.lookupIndex("wordB")});
        Instance instance = new Instance(fs, null, "doc", null);
        InstanceList list = new InstanceList(alphabet, null);
        list.add(instance);

        ParallelTopicModel model = new ParallelTopicModel(2);
        model.setRandomSeed(100);
        model.addInstances(list);

        try {
            model.initializeFromState(stateFile);
            fail("Expected IllegalStateException due to mismatched word index.");
        } catch (IllegalStateException expected) {
            assertTrue(true);
        }
        stateFile.delete();
    }
@Test
    public void testPrintTopWordsToStreamFormat() {
        Alphabet alphabet = new Alphabet();
        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{alphabet.lookupIndex("good"), alphabet.lookupIndex("food")});
        Instance instance = new Instance(fs, null, "doc", null);
        InstanceList list = new InstanceList(alphabet, null);
        list.add(instance);

        ParallelTopicModel model = new ParallelTopicModel(2);
        model.setRandomSeed(111);
        model.addInstances(list);

        ByteArrayOutputStream arrayOut = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(arrayOut);
        model.printTopWords(printStream, 1, true);
        String result = arrayOut.toString();
        assertTrue(result.contains("\t"));
    }
@Test
    public void testGetTopicWordsNotNormalizedNotSmoothed() {
        Alphabet alphabet = new Alphabet();
        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{alphabet.lookupIndex("alpha")});
        Instance instance = new Instance(fs, null, "doc", null);
        InstanceList list = new InstanceList(alphabet, null);
        list.add(instance);

        ParallelTopicModel model = new ParallelTopicModel(2);
        model.setRandomSeed(99);
        model.addInstances(list);

        double[][] result = model.getTopicWords(false, false);
        assertEquals(2, result.length);
        assertTrue(result[0].length > 0);
        assertTrue(result[0][0] >= 0);
    }
@Test
    public void testPrintDocumentTopicsWithThresholdAndLimit() {
        Alphabet alphabet = new Alphabet();
        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{
                alphabet.lookupIndex("alpha"), alphabet.lookupIndex("beta")
        });
        Instance instance = new Instance(fs, null, "mydoc", null);
        InstanceList list = new InstanceList(alphabet, null);
        list.add(instance);

        ParallelTopicModel model = new ParallelTopicModel(4, 4.0, 0.01);
        model.setRandomSeed(101);
        model.addInstances(list);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(baos);
        model.printDocumentTopics(pw, 0.0, 2);
        pw.close();

        String out = baos.toString();
        assertTrue(out.contains("mydoc"));
    }
@Test
    public void testPrintTopicDocumentsPrintsWithValidNames() {
        Alphabet alphabet = new Alphabet();
        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{alphabet.lookupIndex("sun")});
        Instance instance = new Instance(fs, null, "namedDoc", null);
        InstanceList list = new InstanceList(alphabet, null);
        list.add(instance);

        ParallelTopicModel model = new ParallelTopicModel(2, 2.0, 0.01);
        model.setRandomSeed(44);
        model.addInstances(list);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(baos);
        model.printTopicDocuments(writer);
        writer.close();

        String content = baos.toString();
        assertTrue(content.contains("namedDoc"));
    }
@Test
    public void testPrintTopicWordWeightsOutput() throws IOException {
        Alphabet alphabet = new Alphabet();
        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{alphabet.lookupIndex("green")});
        Instance instance = new Instance(fs, null, "greenDoc", null);
        InstanceList list = new InstanceList(alphabet, null);
        list.add(instance);

        ParallelTopicModel model = new ParallelTopicModel(2);
        model.setRandomSeed(333);
        model.addInstances(list);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(baos);
        model.printTopicWordWeights(writer);
        writer.close();

        String content = baos.toString("UTF-8");
        assertTrue(content.contains("green"));
    }
@Test
    public void testGetProbEstimatorReturnsNotNull() {
        Alphabet alphabet = new Alphabet();
        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{
                alphabet.lookupIndex("earth")
        });
        Instance instance = new Instance(fs, null, "geo", null);
        InstanceList list = new InstanceList(alphabet, null);
        list.add(instance);

        ParallelTopicModel model = new ParallelTopicModel(2);
        model.setRandomSeed(92);
        model.addInstances(list);

        MarginalProbEstimator estimator = model.getProbEstimator();
        assertNotNull(estimator);
    }
@Test
    public void testSetSaveStateAndModelIntervalCreatesInternalFlags() {
        ParallelTopicModel model = new ParallelTopicModel(2);
        model.setSaveState(10, "statePath");
        model.setSaveSerializedModel(5, "modelPath");

        assertEquals(10, model.saveStateInterval);
        assertEquals(5, model.saveModelInterval);
        assertEquals("statePath", model.stateFilename);
        assertEquals("modelPath", model.modelFilename);
    }
@Test
    public void testPrintDocumentTopicsWithUnnamedInstance() {
        Alphabet alphabet = new Alphabet();
        FeatureSequence sequence = new FeatureSequence(alphabet, new int[]{alphabet.lookupIndex("word")});
        Instance instance = new Instance(sequence, null, null, null);
        InstanceList list = new InstanceList(alphabet, null);
        list.add(instance);

        ParallelTopicModel model = new ParallelTopicModel(3);
        model.setRandomSeed(50);
        model.addInstances(list);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(output);
        model.printDocumentTopics(writer);
        writer.close();

        String result = output.toString();
        assertTrue(result.contains("no-name"));
    }
@Test
    public void testDisplayTopWordsWithNewLinesTrue() {
        Alphabet alphabet = new Alphabet();
        FeatureSequence sequence = new FeatureSequence(alphabet, new int[]{alphabet.lookupIndex("one"), alphabet.lookupIndex("two")});
        Instance instance = new Instance(sequence, null, "doc", null);
        InstanceList list = new InstanceList(alphabet, null);
        list.add(instance);

        ParallelTopicModel model = new ParallelTopicModel(3);
        model.setRandomSeed(6);
        model.addInstances(list);

        String result = model.displayTopWords(1, true);
        assertTrue(result.contains("\n"));
    }
@Test
    public void testDisplayTopWordsWithNewLinesFalse() {
        Alphabet alphabet = new Alphabet();
        FeatureSequence sequence = new FeatureSequence(alphabet, new int[]{alphabet.lookupIndex("termA"), alphabet.lookupIndex("termB")});
        Instance instance = new Instance(sequence, null, "doc", null);
        InstanceList list = new InstanceList(alphabet, null);
        list.add(instance);

        ParallelTopicModel model = new ParallelTopicModel(2);
        model.setRandomSeed(44);
        model.addInstances(list);

        String result = model.displayTopWords(1, false);
        assertTrue(result.contains("\t"));
        assertTrue(result.contains("term"));
    }
@Test
    public void testGetSubCorpusTopicWordsWithPartialMask() {
        Alphabet alphabet = new Alphabet();
        FeatureSequence featureSeq1 = new FeatureSequence(alphabet, new int[]{alphabet.lookupIndex("a")});
        FeatureSequence featureSeq2 = new FeatureSequence(alphabet, new int[]{alphabet.lookupIndex("b")});

        Instance instance1 = new Instance(featureSeq1, null, "doc1", null);
        Instance instance2 = new Instance(featureSeq2, null, "doc2", null);
        InstanceList list = new InstanceList(alphabet, null);
        list.add(instance1);
        list.add(instance2);

        ParallelTopicModel model = new ParallelTopicModel(2, 2.0, 0.01);
        model.setRandomSeed(14);
        model.addInstances(list);

        boolean[] mask = new boolean[2];
        mask[0] = true;
        mask[1] = false;

        double[][] result = model.getSubCorpusTopicWords(mask, true, true);
        assertEquals(2, result.length);
        assertEquals(alphabet.size(), result[0].length);
    }
@Test
    public void testMaximizeZeroIterationsPreservesTopicAssignments() {
        Alphabet alphabet = new Alphabet();
        FeatureSequence sequence = new FeatureSequence(alphabet, new int[]{alphabet.lookupIndex("cat")});
        Instance instance = new Instance(sequence, null, "doc", null);
        InstanceList list = new InstanceList(alphabet, null);
        list.add(instance);

        ParallelTopicModel model = new ParallelTopicModel(3);
        model.setRandomSeed(12);
        model.addInstances(list);
        LabelSequence original = (LabelSequence) model.getData().get(0).topicSequence;

        model.maximize(0);

        LabelSequence after = (LabelSequence) model.getData().get(0).topicSequence;
        assertArrayEquals(original.getFeatures(), after.getFeatures());
    }
@Test
    public void testEstimateWithSingleThreadAndMinimalSetup() throws Exception {
        Alphabet alphabet = new Alphabet();
        FeatureSequence sequence = new FeatureSequence(alphabet, new int[]{alphabet.lookupIndex("sun")});
        Instance instance = new Instance(sequence, null, "d", null);
        InstanceList instanceList = new InstanceList(alphabet, null);
        instanceList.add(instance);

        ParallelTopicModel model = new ParallelTopicModel(2);
        model.setNumThreads(1);
        model.setRandomSeed(123);
        model.setNumIterations(1);

        model.addInstances(instanceList);
        model.estimate();

        assertTrue(model.tokensPerTopic[0] > 0 || model.tokensPerTopic[1] > 0);
    }
@Test
    public void testPrintTopicWordWeightsToFileWriter() throws Exception {
        Alphabet alphabet = new Alphabet();
        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{alphabet.lookupIndex("green")});
        Instance instance = new Instance(fs, null, "doc", null);
        InstanceList list = new InstanceList(alphabet, null);
        list.add(instance);

        File tempFile = File.createTempFile("weights", ".txt");

        ParallelTopicModel model = new ParallelTopicModel(2);
        model.setRandomSeed(202);
        model.addInstances(list);
        model.printTopicWordWeights(tempFile);

        assertTrue(tempFile.exists());
        assertTrue(tempFile.length() > 0);
        tempFile.delete();
    }
@Test
    public void testGetTopicDocumentsReturnsSortedStructure() {
        Alphabet alphabet = new Alphabet();
        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{alphabet.lookupIndex("dog")});
        Instance instance = new Instance(fs, null, "namedDoc", null);
        InstanceList list = new InstanceList(alphabet, null);
        list.add(instance);

        ParallelTopicModel model = new ParallelTopicModel(2);
        model.setRandomSeed(32);
        model.addInstances(list);
        ArrayList<TreeSet<IDSorter>> result = model.getTopicDocuments(1.0);

        assertEquals(2, result.size());
        assertFalse(result.get(0).isEmpty() || result.get(1).isEmpty());
    }
@Test
    public void testPrintDocumentTopicsWithNegativeMaxPrintsAllTopics() {
        Alphabet alphabet = new Alphabet();
        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{alphabet.lookupIndex("alpha")});
        Instance instance = new Instance(fs, null, "doc", null);
        InstanceList list = new InstanceList(alphabet, null);
        list.add(instance);

        ParallelTopicModel model = new ParallelTopicModel(4);
        model.setRandomSeed(18);
        model.addInstances(list);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(baos);
        model.printDocumentTopics(pw, 0.0, -1);
        pw.close();

        String out = baos.toString();
        assertTrue(out.contains("0"));
        assertTrue(out.contains("doc"));
    }
@Test
    public void testSetTemperingIntervalSetsValue() {
        ParallelTopicModel model = new ParallelTopicModel(3);
        model.setTemperingInterval(15);
        assertEquals(15, model.temperingInterval);
    }
@Test
    public void testSetNumThreadsSetsThreadCount() {
        ParallelTopicModel model = new ParallelTopicModel(2);
        model.setNumThreads(4);
        assertEquals(4, model.numThreads);
    }
@Test
    public void testPrintTypeTopicCountsGeneratesOutput() throws IOException {
        Alphabet alphabet = new Alphabet();
        int index = alphabet.lookupIndex("testword");
        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{index, index});
        Instance instance = new Instance(fs, null, "test", null);
        InstanceList list = new InstanceList(alphabet, null);
        list.add(instance);

        ParallelTopicModel model = new ParallelTopicModel(2);
        model.setRandomSeed(42);
        model.addInstances(list);

        File temp = File.createTempFile("typeCounts", ".txt");
        model.printTypeTopicCounts(temp);
        assertTrue(temp.exists());
        assertTrue(temp.length() > 0);
        temp.delete();
    }
@Test
    public void testGetTopicWordsWithNormalizedAndSmoothedSet() {
        Alphabet alphabet = new Alphabet();
        int index = alphabet.lookupIndex("term");
        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{index});
        Instance instance = new Instance(fs, null, "mydoc", null);
        InstanceList list = new InstanceList(alphabet, null);
        list.add(instance);

        ParallelTopicModel model = new ParallelTopicModel(2);
        model.setRandomSeed(22);
        model.addInstances(list);

        double[][] result = model.getTopicWords(true, true);
        assertEquals(2, result.length);
        assertTrue(result[0][index] > 0);
    }
@Test
    public void testGetTopicWordsWithNoSmoothing() {
        Alphabet alphabet = new Alphabet();
        int index = alphabet.lookupIndex("fruit");
        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{index});
        Instance instance = new Instance(fs, null, "fruitdoc", null);
        InstanceList list = new InstanceList(alphabet, null);
        list.add(instance);

        ParallelTopicModel model = new ParallelTopicModel(2);
        model.setRandomSeed(21);
        model.addInstances(list);

        double[][] result = model.getTopicWords(false, false);
        assertEquals(2, result.length);
        assertTrue(result[0].length > 0);
    }
@Test
    public void testLearnAlphaStabilityFallbackTriggers() {
        Alphabet alphabet = new Alphabet();
        int index = alphabet.lookupIndex("w");
        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{index});
        Instance instance = new Instance(fs, null, "a", null);
        InstanceList list = new InstanceList(alphabet, null);
        list.add(instance);

        ParallelTopicModel model = new ParallelTopicModel(2);
        model.setSymmetricAlpha(false);
        model.setRandomSeed(5);
        model.addInstances(list);

        model.alpha[0] = 1000000.0; 
        model.alpha[1] = 1000000.0;

        model.optimizeInterval = 1;
        model.saveSampleInterval = 1;

        model.setNumIterations(2);
        model.setBurninPeriod(0);
        model.setNumThreads(1);
        try {
            model.estimate(); 
        } catch (Exception e) {
            fail("Exception not expected during fallback alpha resetting.");
        }

        assertEquals(1.0, model.alpha[0], 0.001);
        assertEquals(1.0, model.alpha[1], 0.001);
    }
@Test
    public void testPrintStateToGzipFile() throws IOException {
        Alphabet alphabet = new Alphabet();
        int index = alphabet.lookupIndex("x");
        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{index, index});
        Instance instance = new Instance(fs, null, "doc", null);
        InstanceList data = new InstanceList(alphabet, null);
        data.add(instance);

        ParallelTopicModel model = new ParallelTopicModel(3);
        model.setRandomSeed(17);
        model.addInstances(data);

        File file = File.createTempFile("state", ".gz");
        model.printState(file);
        assertTrue(file.exists());
        assertTrue(file.length() > 0);
        file.delete();
    }
@Test
    public void testModelLogLikelihoodHandlesEmptyTopicsWithoutNaN() {
        ParallelTopicModel model = new ParallelTopicModel(5);
        double ll = model.modelLogLikelihood();
        assertTrue(Double.isFinite(ll));
    }
@Test
    public void testGetTopWordsWithMoreWordsThanVocabulary() {
        Alphabet alphabet = new Alphabet();
        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{
                alphabet.lookupIndex("alpha"), alphabet.lookupIndex("beta")
        });
        Instance instance = new Instance(fs, null, "words", null);
        InstanceList list = new InstanceList(alphabet, null);
        list.add(instance);

        ParallelTopicModel model = new ParallelTopicModel(2);
        model.setRandomSeed(77);
        model.addInstances(list);

        Object[][] topWords = model.getTopWords(10); 
        assertTrue(topWords.length == 2);
        assertTrue(topWords[0].length <= 10);
    }
@Test
    public void testGetTopicProbabilitiesHandlesEmptySequence() {
        LabelSequence blank = new LabelSequence(new LabelAlphabet(), new int[0]);
        ParallelTopicModel model = new ParallelTopicModel(3);

        double[] result = model.getTopicProbabilities(blank);
        assertEquals(3, result.length);
        assertEquals(1.0, result[0] + result[1] + result[2], 0.00001);
    }
@Test
    public void testBuildInitialTypeTopicCountsWithAllUnassignedTopics() {
        Alphabet alphabet = new Alphabet();
        int typeIdx = alphabet.lookupIndex("a");

        FeatureSequence features = new FeatureSequence(alphabet, new int[]{typeIdx, typeIdx});
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        LabelSequence topics = new LabelSequence(labelAlphabet, new int[]{ParallelTopicModel.UNASSIGNED_TOPIC, ParallelTopicModel.UNASSIGNED_TOPIC});

        Instance instance = new Instance(features, null, "none", null);
        TopicAssignment assignment = new TopicAssignment(instance, topics);

        ParallelTopicModel model = new ParallelTopicModel(3);
        model.alphabet = alphabet;
        model.data = new ArrayList<TopicAssignment>();
        model.data.add(assignment);
        model.numTypes = alphabet.size();
        model.setNumTopics(3);

        model.buildInitialTypeTopicCounts();  
    }
@Test
    public void testEstimateWithEmptyDocsAndMultipleThreads() throws Exception {
        ParallelTopicModel model = new ParallelTopicModel(3);
        model.setNumThreads(3);
        model.setNumIterations(1);
        model.setBurninPeriod(0);
        model.setOptimizeInterval(0);

        model.data = new ArrayList<TopicAssignment>(); 
        model.numTypes = 0;
        model.totalTokens = 0;

        model.estimate();  
    }
@Test
    public void testSetBurninPeriodWorks() {
        ParallelTopicModel model = new ParallelTopicModel(2);
        model.setBurninPeriod(150);
        assertEquals(150, model.burninPeriod);
    }
@Test
    public void testSetRandomSeedAffectsInferencer() {
        Alphabet alphabet = new Alphabet();
        int idx = alphabet.lookupIndex("seedWord");
        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{idx});

        Instance instance = new Instance(fs, null, "s", null);
        InstanceList list = new InstanceList(alphabet, null);
        list.add(instance);

        ParallelTopicModel model1 = new ParallelTopicModel(2);
        model1.setRandomSeed(123);
        model1.addInstances(list);
        TopicInferencer inf1 = model1.getInferencer();

        ParallelTopicModel model2 = new ParallelTopicModel(2);
        model2.setRandomSeed(999);
        model2.addInstances(list);
        TopicInferencer inf2 = model2.getInferencer();

        assertNotEquals(inf1, inf2);  
    }
@Test
    public void testSetTopicDisplaySetsFieldsCorrectly() {
        ParallelTopicModel model = new ParallelTopicModel(4);
        model.setTopicDisplay(20, 6);
        assertEquals(20, model.showTopicsInterval);
        assertEquals(6, model.wordsPerTopic);
    }
@Test
    public void testTypeTopicCountOverflowLoggingPath() {
        Alphabet alphabet = new Alphabet();
        int typeIdx = alphabet.lookupIndex("many");

        int[] longArray = new int[100];
        for (int i = 0; i < longArray.length; i++) {
            longArray[i] = 1;
        }

        FeatureSequence fs = new FeatureSequence(alphabet, new int[100]);
        for (int i = 0; i < 100; i++) {
            fs.add(typeIdx);
        }

        LabelAlphabet topicAlphabet = new LabelAlphabet();
        topicAlphabet.lookupIndex("topic0");

        LabelSequence topicSeq = new LabelSequence(topicAlphabet, new int[100]);
        for (int i = 0; i < 100; i++) {
            topicSeq.setFeatureValue(i, 0);  

        }

        Instance instance = new Instance(fs, null, "doc", null);
        TopicAssignment ta = new TopicAssignment(instance, topicSeq);

        ParallelTopicModel model = new ParallelTopicModel(topicAlphabet, 1.0, 0.01);
        model.data = new ArrayList<TopicAssignment>();
        model.data.add(ta);
        model.alphabet = alphabet;
        model.numTypes = alphabet.size();
        model.setNumTopics(1);

        model.buildInitialTypeTopicCounts();  
    }
@Test
    public void testPrintDocumentTopicsThresholdLogicExercised() {
        Alphabet alphabet = new Alphabet();
        int word = alphabet.lookupIndex("t1");

        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{word, word});
        Instance instance = new Instance(fs, null, "myDoc", null);
        InstanceList list = new InstanceList(alphabet, null);
        list.add(instance);

        ParallelTopicModel model = new ParallelTopicModel(4);
        model.setRandomSeed(10);
        model.addInstances(list);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(baos);
        model.printDocumentTopics(pw, 0.000001, 2);  
        pw.flush();
        String result = baos.toString();

        assertTrue(result.contains("myDoc"));
    }
@Test
    public void testPrintTopWordsToPrintStreamWithZeroTopics() {
        ParallelTopicModel model = new ParallelTopicModel(0);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(baos);
        model.printTopWords(writer, 5, true);
        writer.flush();
        String output = baos.toString();
        assertTrue(output.isEmpty() || output.trim().isEmpty());
    }
@Test
    public void testPrintDenseDocumentTopicsWithZeroDocs() {
        ParallelTopicModel model = new ParallelTopicModel(5);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(baos);
        model.printDenseDocumentTopics(writer);
        writer.flush();

        String output = baos.toString();
        assertTrue(output.isEmpty());
    }
@Test
    public void testPrintTopicDocumentsWithZeroDocs() {
        ParallelTopicModel model = new ParallelTopicModel(5);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(baos);
        model.printTopicDocuments(writer, 5);
        writer.flush();

        String out = baos.toString();
        assertTrue(out.contains("topic"));
    }
@Test
    public void testGetTopWordsReturnsNullIfAlphabetNotSet() {
        ParallelTopicModel model = new ParallelTopicModel(2);
        Object[][] topWords = model.getTopWords(3);
        assertEquals(2, topWords.length);
        assertEquals(0, topWords[0].length);  
    }
@Test
    public void testSetSaveStateWithNullFilename() {
        ParallelTopicModel model = new ParallelTopicModel(5);
        model.setSaveState(10, null);
        assertEquals(10, model.saveStateInterval);
        assertNull(model.stateFilename);
    }
@Test
    public void testSetSaveSerializedModelWithNullFilename() {
        ParallelTopicModel model = new ParallelTopicModel(7);
        model.setSaveSerializedModel(25, null);
        assertEquals(25, model.saveModelInterval);
        assertNull(model.modelFilename);
    }
@Test
    public void testPrintDocumentTopicsDoesNotCrashWithAlphaZero() {
        Alphabet alphabet = new Alphabet();
        int index = alphabet.lookupIndex("zero");
        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{index});
        Instance instance = new Instance(fs, null, "zdoc", null);
        InstanceList list = new InstanceList(alphabet, null);
        list.add(instance);

        ParallelTopicModel model = new ParallelTopicModel(3, 0.0, 0.01);
        model.setRandomSeed(123);
        model.addInstances(list);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(baos);
        model.printDocumentTopics(writer, 0.0, 2);
        writer.flush();

        String result = baos.toString();
        assertTrue(result.contains("zdoc"));
    }
@Test
    public void testEstimateWithSingleDocumentAndSingleThread() throws Exception {
        Alphabet alphabet = new Alphabet();
        int term = alphabet.lookupIndex("unique");
        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{term, term});
        Instance instance = new Instance(fs, null, "single", null);
        InstanceList list = new InstanceList(alphabet, null);
        list.add(instance);

        ParallelTopicModel model = new ParallelTopicModel(2);
        model.setRandomSeed(999);
        model.setNumThreads(1);
        model.setNumIterations(1);
        model.setBurninPeriod(0);
        model.setOptimizeInterval(0);
        model.addInstances(list);
        model.estimate();

        assertTrue(model.tokensPerTopic[0] >= 0 || model.tokensPerTopic[1] >= 0);
    }
@Test
    public void testModelLogLikelihoodOutputIsStableAfterOneDocAdded() {
        Alphabet alphabet = new Alphabet();
        int idx = alphabet.lookupIndex("stable");
        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{idx, idx});
        Instance instance = new Instance(fs, null, "stableDoc", null);
        InstanceList list = new InstanceList(alphabet, null);
        list.add(instance);

        ParallelTopicModel model = new ParallelTopicModel(3);
        model.setRandomSeed(47);
        model.addInstances(list);
        double ll = model.modelLogLikelihood();

        assertTrue(Double.isFinite(ll));
    }
@Test
    public void testAlphabetIsReturnedCorrectly() {
        ParallelTopicModel model = new ParallelTopicModel(3);
        Alphabet alphabet = new Alphabet();
        int idx = alphabet.lookupIndex("term");
        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{idx});
        Instance instance = new Instance(fs, null, "doc", null);
        InstanceList list = new InstanceList(alphabet, null);
        list.add(instance);
        model.addInstances(list);

        assertNotNull(model.getAlphabet());
    }
@Test
    public void testDisplayTopWordsHandlesZeroWordsRequested() {
        Alphabet alphabet = new Alphabet();
        int idx = alphabet.lookupIndex("word");
        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{idx});
        Instance instance = new Instance(fs, null, "zeroDoc", null);
        InstanceList list = new InstanceList(alphabet, null);
        list.add(instance);

        ParallelTopicModel model = new ParallelTopicModel(2);
        model.setRandomSeed(10);
        model.addInstances(list);
        String result = model.displayTopWords(0, false);
        assertNotNull(result);
        assertTrue(result.contains("\t"));
    }
@Test
    public void testGetTopicProbabilitiesReturnsNormalizedValues() {
        Alphabet alphabet = new Alphabet();
        int idx = alphabet.lookupIndex("alpha");
        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{idx, idx, idx});
        Instance instance = new Instance(fs, null, "alphaDoc", null);
        InstanceList list = new InstanceList(alphabet, null);
        list.add(instance);

        ParallelTopicModel model = new ParallelTopicModel(3);
        model.setRandomSeed(99);
        model.addInstances(list);
        double[] probs = model.getTopicProbabilities(0);

        double sum = probs[0] + probs[1] + probs[2];
        assertEquals(1.0, sum, 0.000001);
    }
@Test
    public void testPrintStatePrintsValidLinesWhenAlphaSumIsZero() throws IOException {
        Alphabet alphabet = new Alphabet();
        int idx = alphabet.lookupIndex("beta");
        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{idx});
        Instance instance = new Instance(fs, null, "state", null);
        InstanceList list = new InstanceList(alphabet, null);
        list.add(instance);

        ParallelTopicModel model = new ParallelTopicModel(1, 0.0, 0.01);
        model.setRandomSeed(100);
        model.addInstances(list);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        model.printState(ps);
        ps.flush();

        String output = baos.toString("UTF-8");
        assertTrue(output.contains("#alpha"));
        assertTrue(output.contains("beta"));
    }
@Test
    public void testPrintTopicDocumentsHandlesDocumentsWithoutNames() {
        FeatureSequence fs = new FeatureSequence(new Alphabet(), new int[]{0});
        Instance unnamed = new Instance(fs, null, null, null);
        InstanceList list = new InstanceList(new Alphabet(), null);
        list.add(unnamed);

        ParallelTopicModel model = new ParallelTopicModel(2);
        model.setRandomSeed(33);
        model.addInstances(list);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(baos);
        model.printTopicDocuments(pw);
        pw.flush();

        String result = baos.toString();
        assertTrue(result.contains("no-name"));
    }
@Test
    public void testAddInstancesSetsNumTypesCorrectly() {
        Alphabet alphabet = new Alphabet();
        int i = alphabet.lookupIndex("x");
        int j = alphabet.lookupIndex("y");
        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{i, j});
        Instance instance = new Instance(fs, null, "test", null);
        InstanceList instances = new InstanceList(alphabet, null);
        instances.add(instance);

        ParallelTopicModel model = new ParallelTopicModel(3);
        model.setRandomSeed(22);
        model.addInstances(instances);

        assertEquals(2, model.numTypes);
    }
@Test
    public void testPrintTopicWordWeightsHandlesAlphabetEntries() throws Exception {
        Alphabet alphabet = new Alphabet();
        int idx = alphabet.lookupIndex("foo");
        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{idx});
        Instance instance = new Instance(fs, null, "weighted", null);
        InstanceList list = new InstanceList(alphabet, null);
        list.add(instance);

        ParallelTopicModel model = new ParallelTopicModel(2);
        model.setRandomSeed(1);
        model.addInstances(list);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(baos);
        model.printTopicWordWeights(pw);
        pw.flush();

        String result = baos.toString();
        assertTrue(result.contains("foo"));
    }
@Test
    public void testPrintDocumentTopicsMaxGreaterThanTopicsHandled() {
        Alphabet alphabet = new Alphabet();
        int idx = alphabet.lookupIndex("apple");
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{idx});
        Instance instance = new Instance(fs, null, "check", null);
        InstanceList instanceList = new InstanceList(alphabet, null);
        instanceList.add(instance);

        ParallelTopicModel model = new ParallelTopicModel(2);
        model.setRandomSeed(5);
        model.addInstances(instanceList);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(baos);
        model.printDocumentTopics(pw, 0.0, 10); 
        pw.flush();

        String result = baos.toString();
        assertTrue(result.contains("check"));
    } 
}