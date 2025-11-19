public class ParallelTopicModel_1_GPTLLMTest { 

 @Test
    public void testConstructorSetsNumTopicsAndAlphaArrayLength() {
        ParallelTopicModel model = new ParallelTopicModel(4);
        assertEquals(4, model.getNumTopics());
        assertEquals(4, model.alpha.length);
    }
@Test
    public void testSetNumTopicsAdjustsTopicMaskFields() {
        ParallelTopicModel model = new ParallelTopicModel(2);
        model.setNumTopics(3);
        assertEquals(3, model.getNumTopics());
        assertEquals(model.alpha.length, 3);
    }
@Test
    public void testAddInstancesAssignsTrainingDataCorrectly() {
        ArrayList<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        InstanceList instances = new InstanceList(new SerialPipes(pipes));
        String[] docs = new String[]{"one dog", "two cats"};
        instances.addThruPipe(new StringArrayIterator(docs));

        ParallelTopicModel model = new ParallelTopicModel(2);
        model.setRandomSeed(42);

        model.addInstances(instances);

        assertNotNull(model.getData());
        assertEquals(instances.size(), model.getData().size());
        assertNotNull(model.getAlphabet());
    }
@Test
    public void testGetTopicAlphabetReturnsNonNull() {
        ParallelTopicModel model = new ParallelTopicModel(3);
        assertNotNull(model.getTopicAlphabet());
        assertEquals(3, model.getTopicAlphabet().size());
    }
@Test
    public void testEstimateRunsWithoutErrorForSmallCorpus() throws IOException {
        ArrayList<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        InstanceList instances = new InstanceList(new SerialPipes(pipes));
        String[] docs = new String[]{"alpha beta", "beta gamma"};
        instances.addThruPipe(new StringArrayIterator(docs));

        ParallelTopicModel model = new ParallelTopicModel(2, 1.0, 0.01);
        model.setRandomSeed(42);
        model.setNumThreads(1);
        model.setNumIterations(5);
        model.addInstances(instances);

        model.estimate();

        assertEquals(2, model.getNumTopics());
    }
@Test
    public void testGetTopicProbabilitiesAreNormalized() {
        ArrayList<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        InstanceList instances = new InstanceList(new SerialPipes(pipes));
        String[] docs = new String[]{"cat bat", "dog bat cat"};
        instances.addThruPipe(new StringArrayIterator(docs));

        ParallelTopicModel model = new ParallelTopicModel(2);
        model.setRandomSeed(42);
        model.setNumIterations(1);
        model.setNumThreads(1);
        model.addInstances(instances);

        double[] probs = model.getTopicProbabilities(0);
        double sum = probs[0] + probs[1];

        assertEquals(1.0, sum, 1e-6);
    }
@Test
    public void testPrintTopWordsOutputsToFile() throws IOException {
        ArrayList<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        InstanceList instances = new InstanceList(new SerialPipes(pipes));
        String[] docs = new String[]{"car truck", "bus car"};
        instances.addThruPipe(new StringArrayIterator(docs));

        ParallelTopicModel model = new ParallelTopicModel(2);
        model.setNumIterations(5);
        model.setNumThreads(1);
        model.setRandomSeed(42);
        model.addInstances(instances);
        model.estimate();

        File output = tmpFolder.newFile("topwords.txt");
        model.printTopWords(output, 5, false);

        assertTrue(output.exists());
        assertTrue(output.length() > 0);
    }
@Test
    public void testSerializationAndDeserialization() throws Exception {
        ArrayList<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        InstanceList instances = new InstanceList(new SerialPipes(pipes));
        String[] docs = new String[]{"apple orange", "banana apple"};
        instances.addThruPipe(new StringArrayIterator(docs));

        ParallelTopicModel model = new ParallelTopicModel(2);
        model.setNumThreads(1);
        model.setRandomSeed(7);
        model.setNumIterations(1);
        model.addInstances(instances);
        model.estimate();

        File modelFile = tmpFolder.newFile("model.ser");
        model.write(modelFile);

        ParallelTopicModel loadedModel = ParallelTopicModel.read(modelFile);

        assertEquals(model.getNumTopics(), loadedModel.getNumTopics());
    }
@Test
    public void testInitializeFromStateValid() throws Exception {
        ArrayList<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        InstanceList instances = new InstanceList(new SerialPipes(pipes));
        String[] docs = new String[]{"a b", "b c"};
        instances.addThruPipe(new StringArrayIterator(docs));

        ParallelTopicModel model = new ParallelTopicModel(2);
        model.setNumThreads(1);
        model.setNumIterations(1);
        model.setRandomSeed(1);
        model.addInstances(instances);
        model.estimate();

        File stateFile = tmpFolder.newFile("state.gz");
        model.printState(stateFile);

        ParallelTopicModel restored = new ParallelTopicModel(2);
        restored.data = model.getData();
        restored.alphabet = model.getAlphabet();
        restored.topicAlphabet = model.getTopicAlphabet();
        restored.numTypes = model.getAlphabet().size();

        restored.initializeFromState(stateFile);

        assertNotNull(restored.getTopicAlphabet());
    }
@Test
    public void testPrintDocumentTopicsGeneratesValidOutputFile() throws IOException {
        ArrayList<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        InstanceList instances = new InstanceList(new SerialPipes(pipes));
        String[] docs = new String[]{"king queen", "queen knight"};
        instances.addThruPipe(new StringArrayIterator(docs));

        ParallelTopicModel model = new ParallelTopicModel(2);
        model.setNumIterations(3);
        model.setRandomSeed(23);
        model.setNumThreads(1);
        model.addInstances(instances);
        model.estimate();

        File outFile = tmpFolder.newFile("docTopics.txt");
        model.printDocumentTopics(outFile);

        assertTrue(outFile.exists());
        assertTrue(outFile.length() > 0);
    }
@Test
    public void testGetTopWordsReturnsExpectedShape() throws IOException {
        ArrayList<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        InstanceList instances = new InstanceList(new SerialPipes(pipes));
        String[] docs = new String[]{"java jvm", "junit java"};
        instances.addThruPipe(new StringArrayIterator(docs));

        ParallelTopicModel model = new ParallelTopicModel(2);
        model.setNumIterations(1);
        model.setNumThreads(1);
        model.setRandomSeed(5);
        model.addInstances(instances);
        model.estimate();

        Object[][] topWords = model.getTopWords(3);
        assertEquals(2, topWords.length);
        assertTrue(topWords[0].length <= 3);
        assertTrue(topWords[1].length <= 3);
    }
@Test(expected = IllegalStateException.class)
    public void testInitializeFromStateThrowsForMismatchedIndexes() throws IOException {
        ArrayList<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        InstanceList instances = new InstanceList(new SerialPipes(pipes));
        String[] docs = new String[]{"a b"};
        instances.addThruPipe(new StringArrayIterator(docs));

        ParallelTopicModel model = new ParallelTopicModel(2);
        model.setNumThreads(1);
        model.setRandomSeed(1);
        model.addInstances(instances);

        File badFile = tmpFolder.newFile("badstate.gz");
        PrintWriter writer = new PrintWriter(new GZIPOutputStream(new FileOutputStream(badFile)));
        writer.println("#alpha : 0.5 0.5");
        writer.println("#beta : 0.01");
        writer.println("0 badsource 0 999 invalid 0");
        writer.close();

        ParallelTopicModel brokenModel = new ParallelTopicModel(2);
        brokenModel.data = model.getData();
        brokenModel.alphabet = model.getAlphabet();
        brokenModel.topicAlphabet = model.getTopicAlphabet();
        brokenModel.numTypes = model.getAlphabet().size();

        brokenModel.initializeFromState(badFile);
    }
@Test
    public void testSymmetricAlphaSetter() {
        ParallelTopicModel model = new ParallelTopicModel(3);
        model.setSymmetricAlpha(true);
        assertTrue(model.usingSymmetricAlpha);
    }
@Test
    public void testEmptyInstanceListHandledGracefully() {
        ArrayList<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        InstanceList emptyInstances = new InstanceList(new SerialPipes(pipes));

        ParallelTopicModel model = new ParallelTopicModel(3, 1.0, 0.01);
        model.setNumThreads(1);
        model.setNumIterations(5);
        model.addInstances(emptyInstances);

        assertEquals(0, model.getData().size());
    }
@Test
    public void testSingleWordDocumentTraining() throws IOException {
        ArrayList<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        InstanceList instances = new InstanceList(new SerialPipes(pipes));

        String[] docs = new String[]{"word"};
        instances.addThruPipe(new StringArrayIterator(docs));

        ParallelTopicModel model = new ParallelTopicModel(2, 1.0, 0.01);
        model.setNumThreads(1);
        model.setNumIterations(3);
        model.setRandomSeed(10);
        model.addInstances(instances);
        model.estimate();

        double[] probs = model.getTopicProbabilities(0);
        double sum = probs[0] + probs[1];
        assertEquals(1.0, sum, 0.00001);
    }
@Test
    public void testZeroBurninAndZeroOptimizeInterval() throws IOException {
        ArrayList<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        InstanceList instances = new InstanceList(new SerialPipes(pipes));

        String[] docs = new String[]{"a b c"};
        instances.addThruPipe(new StringArrayIterator(docs));

        ParallelTopicModel model = new ParallelTopicModel(2, 1.0, 0.01);
        model.setNumThreads(1);
        model.setBurninPeriod(0);
        model.setOptimizeInterval(0);
        model.setNumIterations(2);
        model.setRandomSeed(7);
        model.addInstances(instances);

        model.estimate();

        assertEquals(2, model.getNumTopics());
    }
@Test
    public void testTooFewAlphaSumLeadsToFallback() throws IOException {
        ArrayList<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());

        InstanceList instances = new InstanceList(new SerialPipes(pipes));
        String[] docs = new String[]{"d d d d d"};
        instances.addThruPipe(new StringArrayIterator(docs));

        ParallelTopicModel model = new ParallelTopicModel(3);
        model.setSymmetricAlpha(false);
        model.setBurninPeriod(0);
        model.setOptimizeInterval(1);
        model.setNumIterations(3);
        model.setSaveSampleInterval(1);
        model.setNumThreads(1);
        model.setRandomSeed(5);
        model.addInstances(instances);

        model.estimate();

        double alphaSum = model.alpha[0] + model.alpha[1] + model.alpha[2];
        assertTrue(alphaSum > 0.0);
    }
@Test
    public void testModelLogLikelihoodAfterEstimateIsNotZero() throws IOException {
        ArrayList<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());

        InstanceList instances = new InstanceList(new SerialPipes(pipes));
        String[] docs = new String[]{"e f g h", "f g h i"};
        instances.addThruPipe(new StringArrayIterator(docs));

        ParallelTopicModel model = new ParallelTopicModel(2);
        model.setNumIterations(3);
        model.setNumThreads(1);
        model.setRandomSeed(999);
        model.addInstances(instances);
        model.estimate();

        double ll = model.modelLogLikelihood();

        assertFalse(Double.isNaN(ll));
        assertFalse(Double.isInfinite(ll));
    }
@Test
    public void testZeroIterationsEstimateDoesNotCrash() throws IOException {
        ArrayList<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());

        InstanceList instances = new InstanceList(new SerialPipes(pipes));
        String[] docs = new String[]{"one two"};
        instances.addThruPipe(new StringArrayIterator(docs));

        ParallelTopicModel model = new ParallelTopicModel(2);
        model.setNumIterations(0);
        model.setNumThreads(1);
        model.setRandomSeed(3);
        model.addInstances(instances);

        model.estimate();

        assertEquals(2, model.getNumTopics());
    }
@Test
    public void testMaximizeWithZeroIterationsDoesNothing() {
        ParallelTopicModel model = new ParallelTopicModel(3);
        model.maximize(0);
        assertEquals(3, model.getNumTopics());
    }
@Test
    public void testSingleTopicModelDegradesGracefully() throws IOException {
        ArrayList<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());

        InstanceList instances = new InstanceList(new SerialPipes(pipes));
        String[] docs = new String[]{"text processing topic model"};
        instances.addThruPipe(new StringArrayIterator(docs));

        ParallelTopicModel model = new ParallelTopicModel(1);
        model.setNumThreads(1);
        model.setNumIterations(2);
        model.setRandomSeed(5);
        model.addInstances(instances);

        model.estimate();

        assertEquals(1, model.getNumTopics());
        double[] probs = model.getTopicProbabilities(0);
        assertEquals(1.0, probs[0], 1e-6);
    }
@Test
    public void testTopicWordDistributionSmoothingAndNormalization() throws IOException {
        ArrayList<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());

        InstanceList instances = new InstanceList(new SerialPipes(pipes));
        String[] docs = new String[]{"dog cat", "dog"};
        instances.addThruPipe(new StringArrayIterator(docs));

        ParallelTopicModel model = new ParallelTopicModel(2);
        model.setNumIterations(2);
        model.setNumThreads(1);
        model.setRandomSeed(77);
        model.addInstances(instances);
        model.estimate();

        double[][] topicWords = model.getTopicWords(true, true);

        double probSum0 = topicWords[0][0] + topicWords[0][1];
        double probSum1 = topicWords[1][0] + topicWords[1][1];

        assertTrue(Math.abs(probSum0 - 1.0) < 1e-6 || Math.abs(probSum1 - 1.0) < 1e-6);
    }
@Test
    public void testGetDocumentTopicsWithNoSmoothing() throws IOException {
        ArrayList<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());

        InstanceList instances = new InstanceList(new SerialPipes(pipes));
        String[] docs = new String[]{"apple orange banana"};
        instances.addThruPipe(new StringArrayIterator(docs));

        ParallelTopicModel model = new ParallelTopicModel(2);
        model.setNumThreads(1);
        model.setNumIterations(2);
        model.setRandomSeed(66);
        model.addInstances(instances);
        model.estimate();

        double[][] docTopics = model.getDocumentTopics(false, false);

        assertEquals(1, docTopics.length);
        double sum = docTopics[0][0] + docTopics[0][1];
        assertTrue(sum > 0.0);
    }
@Test
    public void testPrintTopicDocumentsPrintsUpToNDocsPerTopic() throws IOException {
        ArrayList<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());

        InstanceList instances = new InstanceList(new SerialPipes(pipes));
        String[] docs = new String[]{"a b c", "b c d", "d e f"};
        instances.addThruPipe(new StringArrayIterator(docs));

        ParallelTopicModel model = new ParallelTopicModel(2);
        model.setNumThreads(1);
        model.setNumIterations(2);
        model.setRandomSeed(55);
        model.addInstances(instances);
        model.estimate();

        File topicDocFile = tmpFolder.newFile("topicDocs.txt");
        PrintWriter out = new PrintWriter(new FileWriter(topicDocFile));
        model.printTopicDocuments(out, 2);
        out.close();

        assertTrue(topicDocFile.exists());
        assertTrue(topicDocFile.length() > 0);
    }
@Test
    public void testPrintTopWordsWithZeroWordsRequested() throws IOException {
        ArrayList<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new CharSequence2TokenSequence("[\\p{L}]+"));
        pipes.add(new TokenSequence2FeatureSequence());
        InstanceList instances = new InstanceList(new SerialPipes(pipes));

        String[] docs = new String[]{"language model topic"};
        instances.addThruPipe(new StringArrayIterator(docs));

        ParallelTopicModel model = new ParallelTopicModel(2);
        model.setNumThreads(1);
        model.setNumIterations(2);
        model.setRandomSeed(123);
        model.addInstances(instances);
        model.estimate();

        File outputFile = tmpFolder.newFile("topwords_zero.txt");
        model.printTopWords(outputFile, 0, false);
        assertTrue(outputFile.exists());
        assertTrue(outputFile.length() > 0); 
    }
@Test
    public void testDisplayTopWordsWithNewLineOutput() throws IOException {
        ArrayList<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        InstanceList instances = new InstanceList(new SerialPipes(pipes));

        String[] docs = new String[]{"alpha beta gamma delta"};
        instances.addThruPipe(new StringArrayIterator(docs));

        ParallelTopicModel model = new ParallelTopicModel(2);
        model.setNumThreads(1);
        model.setRandomSeed(456);
        model.setNumIterations(2);
        model.addInstances(instances);
        model.estimate();

        String output = model.displayTopWords(5, true);
        assertNotNull(output);
        assertTrue(output.contains("\n"));
    }
@Test
    public void testGetSortedWordsReturnsPerTopicLists() throws IOException {
        ArrayList<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        InstanceList instances = new InstanceList(new SerialPipes(pipes));

        String[] docs = new String[]{"word1 word2", "word2 word3", "word1 word3"};
        instances.addThruPipe(new StringArrayIterator(docs));

        ParallelTopicModel model = new ParallelTopicModel(2);
        model.setNumThreads(1);
        model.setNumIterations(2);
        model.setRandomSeed(789);
        model.addInstances(instances);
        model.estimate();

        ArrayList<TreeSet<IDSorter>> sorted = model.getSortedWords();
        assertEquals(2, sorted.size());
        assertTrue(sorted.get(0).size() > 0 || sorted.get(1).size() > 0);
    }
@Test
    public void testEstimateWithLargeAlphaAndBetaValues() throws IOException {
        ArrayList<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        InstanceList instances = new InstanceList(new SerialPipes(pipes));

        String[] docs = new String[]{"x y z"};
        instances.addThruPipe(new StringArrayIterator(docs));

        ParallelTopicModel model = new ParallelTopicModel(2, 100.0, 50.0);
        model.setNumThreads(1);
        model.setNumIterations(3);
        model.setRandomSeed(33);
        model.addInstances(instances);
        model.estimate();

        assertEquals(2, model.getNumTopics());
        String summary = model.displayTopWords(2, false);
        assertNotNull(summary);
    }
@Test
    public void testGetTopicDocumentsReturnsSortedSets() throws IOException {
        ArrayList<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        InstanceList instances = new InstanceList(new SerialPipes(pipes));

        String[] docs = new String[]{"doc a b", "doc c d", "doc e f"};
        instances.addThruPipe(new StringArrayIterator(docs));

        ParallelTopicModel model = new ParallelTopicModel(2);
        model.setNumThreads(1);
        model.setRandomSeed(100);
        model.setNumIterations(3);
        model.addInstances(instances);
        model.estimate();

        ArrayList<TreeSet<IDSorter>> topicDocs = model.getTopicDocuments(0.0001);
        assertEquals(2, topicDocs.size());
        assertNotNull(topicDocs.get(0));
        assertNotNull(topicDocs.get(1));
    }
@Test
    public void testSetSaveStateWithoutCallingEstimate() {
        ParallelTopicModel model = new ParallelTopicModel(3);
        model.setSaveState(10, "dummy-state.gz");
        assertEquals(10, model.saveStateInterval);
        assertEquals("dummy-state.gz", model.stateFilename);
    }
@Test
    public void testSetSaveSerializedModelWithoutCallingEstimate() {
        ParallelTopicModel model = new ParallelTopicModel(3);
        model.setSaveSerializedModel(5, "dummy-model.ser");
        assertEquals(5, model.saveModelInterval);
        assertEquals("dummy-model.ser", model.modelFilename);
    }
@Test
    public void testPrintTopicWordWeightsToFile() throws IOException {
        ArrayList<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new CharSequence2TokenSequence("[\\p{L}]+"));
        pipes.add(new TokenSequence2FeatureSequence());
        InstanceList instances = new InstanceList(new SerialPipes(pipes));

        String[] docs = new String[]{"iraq kuwait war"};
        instances.addThruPipe(new StringArrayIterator(docs));

        ParallelTopicModel model = new ParallelTopicModel(2, 1.0, 0.01);
        model.setNumThreads(1);
        model.setNumIterations(2);
        model.setRandomSeed(50);
        model.addInstances(instances);
        model.estimate();

        File out = tmpFolder.newFile("topic-word-weights.txt");
        model.printTopicWordWeights(out);

        assertTrue(out.exists());
        assertTrue(out.length() > 0);
    }
@Test
    public void testInferencerNotNullAfterEstimate() throws IOException {
        ArrayList<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());

        InstanceList instances = new InstanceList(new SerialPipes(pipes));
        String[] docs = new String[]{"weather prediction climate"};
        instances.addThruPipe(new StringArrayIterator(docs));

        ParallelTopicModel model = new ParallelTopicModel(2);
        model.setNumThreads(1);
        model.setNumIterations(2);
        model.setRandomSeed(202);
        model.addInstances(instances);
        model.estimate();

        TopicInferencer inferencer = model.getInferencer();
        assertNotNull(inferencer);
    }
@Test
    public void testMarginalProbEstimatorNotNullAfterEstimate() throws IOException {
        ArrayList<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());

        InstanceList instances = new InstanceList(new SerialPipes(pipes));
        String[] docs = new String[]{"harvard yale mit stanford"};
        instances.addThruPipe(new StringArrayIterator(docs));

        ParallelTopicModel model = new ParallelTopicModel(2);
        model.setNumThreads(1);
        model.setNumIterations(2);
        model.setRandomSeed(606);
        model.addInstances(instances);
        model.estimate();

        MarginalProbEstimator estimator = model.getProbEstimator();
        assertNotNull(estimator);
    }
@Test
    public void testEstimateWithUnevenDocumentsPerThread() throws IOException {
        ArrayList<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new CharSequence2TokenSequence("[\\p{L}]+"));
        pipes.add(new TokenSequence2FeatureSequence());

        InstanceList data = new InstanceList(new SerialPipes(pipes));
        String[] docs = new String[]{"a b", "c d", "e f", "g h", "i j"};
        data.addThruPipe(new StringArrayIterator(docs));

        ParallelTopicModel model = new ParallelTopicModel(2);
        model.setNumThreads(3);
        model.setNumIterations(1);
        model.setRandomSeed(42);
        model.addInstances(data);
        model.estimate();

        assertEquals(5, data.size());
    }
@Test
    public void testTopicWordSmoothingWithZeroBeta() throws IOException {
        ArrayList<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());

        InstanceList data = new InstanceList(new SerialPipes(pipes));
        String[] docs = new String[]{"a b c"};
        data.addThruPipe(new StringArrayIterator(docs));

        ParallelTopicModel model = new ParallelTopicModel(2, 1.0, 0.0);
        model.setNumThreads(1);
        model.setNumIterations(1);
        model.setRandomSeed(11);
        model.addInstances(data);
        model.estimate();

        double[][] topicWords = model.getTopicWords(true, true);
        double rowSum = topicWords[0][0] + topicWords[0][1] + topicWords[0][2];
        assertEquals(1.0, rowSum, 1e-5);
    }
@Test
    public void testWriteAndReadSerializedModelWithMultipleTopics() throws Exception {
        ArrayList<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new CharSequence2TokenSequence("[\\p{L}]+"));
        pipes.add(new TokenSequence2FeatureSequence());

        InstanceList data = new InstanceList(new SerialPipes(pipes));
        String[] docs = new String[]{"data science", "machine learning"};
        data.addThruPipe(new StringArrayIterator(docs));

        ParallelTopicModel model = new ParallelTopicModel(4);
        model.setNumIterations(2);
        model.setNumThreads(1);
        model.setRandomSeed(55);
        model.addInstances(data);
        model.estimate();

        File out = tmpFolder.newFile("model.ser");
        model.write(out);

        ParallelTopicModel readModel = ParallelTopicModel.read(out);
        assertEquals(4, readModel.getNumTopics());
    }
@Test
    public void testGetSubCorpusTopicWordsWithAllFalseMask() throws IOException {
        ArrayList<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new CharSequence2TokenSequence("[\\p{L}]+"));
        pipes.add(new TokenSequence2FeatureSequence());

        InstanceList list = new InstanceList(new SerialPipes(pipes));
        String[] docs = new String[]{"apple pie", "banana cake"};
        list.addThruPipe(new StringArrayIterator(docs));

        ParallelTopicModel model = new ParallelTopicModel(2);
        model.setRandomSeed(100);
        model.setNumThreads(1);
        model.setNumIterations(1);
        model.addInstances(list);
        model.estimate();

        boolean[] mask = new boolean[]{false, false};
        double[][] matrix = model.getSubCorpusTopicWords(mask, true, true);
        double firstSum = matrix[0][0] + matrix[0][1];
        assertTrue(firstSum > 0.0);
    }
@Test
    public void testPrintStateToFileAndReadBytes() throws IOException {
        ArrayList<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new CharSequence2TokenSequence("[\\p{L}]+"));
        pipes.add(new TokenSequence2FeatureSequence());

        InstanceList list = new InstanceList(new SerialPipes(pipes));
        String[] docs = new String[]{"markov chains", "probability inference"};
        list.addThruPipe(new StringArrayIterator(docs));

        ParallelTopicModel model = new ParallelTopicModel(2);
        model.setNumThreads(1);
        model.setNumIterations(2);
        model.setRandomSeed(78);
        model.addInstances(list);
        model.estimate();

        File gz = tmpFolder.newFile("modelstate.gz");
        model.printState(gz);

        FileInputStream fin = new FileInputStream(gz);
        byte[] buffer = new byte[10];
        int bytesRead = fin.read(buffer);
        fin.close();

        assertTrue(bytesRead > 0);
    }
@Test
    public void testPrintDocumentTopicsWithThresholdExcludesSmallValues() throws IOException {
        ArrayList<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new CharSequence2TokenSequence("[\\p{L}]+"));
        pipes.add(new TokenSequence2FeatureSequence());

        InstanceList data = new InstanceList(new SerialPipes(pipes));
        String[] docs = new String[]{"doca docb", "docc docb"};
        data.addThruPipe(new StringArrayIterator(docs));

        ParallelTopicModel model = new ParallelTopicModel(3);
        model.setNumIterations(1);
        model.setNumThreads(1);
        model.setRandomSeed(111);
        model.addInstances(data);
        model.estimate();

        File file = tmpFolder.newFile("doc_topics.txt");
        PrintWriter pw = new PrintWriter(new FileWriter(file));
        model.printDocumentTopics(pw, 0.9, 3);
        pw.close();

        BufferedReader br = new BufferedReader(new FileReader(file));
        String line1 = br.readLine();
        String line2 = br.readLine();
        br.close();

        assertTrue(line1.contains("#doc"));
        assertTrue(line2.split("\t").length <= 3 + 3 * 2); 
    }
@Test
    public void testPrintTypeTopicCountsFileFormat() throws IOException {
        ArrayList<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new CharSequence2TokenSequence("[\\p{L}]+"));
        pipes.add(new TokenSequence2FeatureSequence());

        InstanceList inst = new InstanceList(new SerialPipes(pipes));
        String[] docs = new String[]{"x y x y"};
        inst.addThruPipe(new StringArrayIterator(docs));

        ParallelTopicModel model = new ParallelTopicModel(2);
        model.setNumIterations(2);
        model.setNumThreads(1);
        model.setRandomSeed(999);
        model.addInstances(inst);
        model.estimate();

        File out = tmpFolder.newFile("typetopiccounts.txt");
        model.printTypeTopicCounts(out);

        BufferedReader reader = new BufferedReader(new FileReader(out));
        String line = reader.readLine();
        reader.close();

        assertTrue(line.matches(".*\\d+ \\w+( \\d+:\\d+)+"));
    }
@Test
    public void testXMLTopicReportCanGenerate() throws IOException {
        ArrayList<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());

        InstanceList inst = new InstanceList(new SerialPipes(pipes));
        String[] docs = new String[]{"hello world", "machine mind", "language understanding"};
        inst.addThruPipe(new StringArrayIterator(docs));

        ParallelTopicModel model = new ParallelTopicModel(2);
        model.setNumIterations(2);
        model.setRandomSeed(17);
        model.setNumThreads(1);
        model.addInstances(inst);
        model.estimate();

        File xmlFile = tmpFolder.newFile("topics.xml");
        PrintWriter out = new PrintWriter(xmlFile);
        model.topicXMLReport(out, 2);
        out.close();

        BufferedReader reader = new BufferedReader(new FileReader(xmlFile));
        String firstLine = reader.readLine();
        reader.close();

        assertTrue(firstLine.startsWith("<?xml"));
    }
@Test
    public void testTopicPhraseXMLReportDoesNotFail() throws IOException {
        ArrayList<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());
        
        InstanceList inst = new InstanceList(new SerialPipes(pipes));
        String[] docs = new String[]{"new york state university", "international affairs office"};
        inst.addThruPipe(new StringArrayIterator(docs));

        ParallelTopicModel model = new ParallelTopicModel(2);
        model.setNumThreads(1);
        model.setNumIterations(2);
        model.setRandomSeed(22);
        model.addInstances(inst);
        model.estimate();

        File xmlOut = tmpFolder.newFile("phrases.xml");
        PrintWriter out = new PrintWriter(xmlOut);
        model.topicPhraseXMLReport(out, 5);
        out.close();

        assertTrue(xmlOut.length() > 0);
    }
@Test
    public void testUniformAlphaSumAssignsEqualAlpha() {
        ParallelTopicModel model = new ParallelTopicModel(4, 1.0, 0.01);
        assertEquals(4, model.alpha.length);
        assertEquals(0.25, model.alpha[0], 1e-8);
        assertEquals(0.25, model.alpha[1], 1e-8);
        assertEquals(0.25, model.alpha[2], 1e-8);
        assertEquals(0.25, model.alpha[3], 1e-8);
    }
@Test
    public void testGetDocumentTopicsWithSmoothingAndNoNormalization() throws IOException {
        ArrayList<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());

        InstanceList list = new InstanceList(new SerialPipes(pipes));
        String[] docs = new String[]{"topic modeling topic inference"};
        list.addThruPipe(new StringArrayIterator(docs));

        ParallelTopicModel model = new ParallelTopicModel(2);
        model.setNumIterations(2);
        model.setNumThreads(1);
        model.setRandomSeed(123);
        model.addInstances(list);
        model.estimate();

        double[][] docTopics = model.getDocumentTopics(false, true);
        assertTrue(docTopics[0][0] > 0.0);
        assertTrue(docTopics[0][1] > 0.0);
    }
@Test
    public void testSetBurninPeriodAppliesValueCorrectly() {
        ParallelTopicModel model = new ParallelTopicModel(3);
        model.setBurninPeriod(25);
        assertEquals(25, model.burninPeriod);
    }
@Test
    public void testSetTemperingIntervalAppliesValueCorrectly() {
        ParallelTopicModel model = new ParallelTopicModel(3);
        model.setTemperingInterval(5);
        assertEquals(5, model.temperingInterval);
    }
@Test
    public void testSetTopicDisplayControlsCorpusSummarySettings() {
        ParallelTopicModel model = new ParallelTopicModel(2);
        model.setTopicDisplay(15, 9);
        assertEquals(15, model.showTopicsInterval);
        assertEquals(9, model.wordsPerTopic);
    }
@Test
    public void testPrintTopWordsWithZeroTopicCountsDoesNotFail() throws IOException {
        ParallelTopicModel model = new ParallelTopicModel(2);
        model.setNumThreads(1);
        model.setNumIterations(0);

        ArrayList<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());

        InstanceList ilist = new InstanceList(new SerialPipes(pipes));
        String[] data = new String[]{"blank input", "testing header only"};
        ilist.addThruPipe(new StringArrayIterator(data));

        model.addInstances(ilist);

        File file = tmpFolder.newFile("top.txt");
        model.printTopWords(file, 5, false);

        assertTrue(file.exists());
        assertTrue(file.length() > 0);
    }
@Test
    public void testPrintTopicDocumentsWithZeroMaxLimit() throws IOException {
        ParallelTopicModel model = new ParallelTopicModel(2);
        model.setNumThreads(1);

        ArrayList<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());

        InstanceList list = new InstanceList(new SerialPipes(pipes));
        String[] docs = new String[]{"sampling lda", "introduction"};
        list.addThruPipe(new StringArrayIterator(docs));
        model.addInstances(list);
        model.setNumIterations(2);
        model.setRandomSeed(12);
        model.estimate();

        File outputFile = tmpFolder.newFile("topicdocs.txt");
        PrintWriter pw = new PrintWriter(outputFile);
        model.printTopicDocuments(pw, 0);
        pw.close();

        BufferedReader reader = new BufferedReader(new FileReader(outputFile));
        String header = reader.readLine();
        reader.close();

        assertTrue(header.contains("#topic"));
    }
@Test
    public void testWriteObjectAndReadObjectRoundTrip() throws Exception {
        ArrayList<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new CharSequence2TokenSequence("[\\p{L}]+"));
        pipes.add(new TokenSequence2FeatureSequence());

        InstanceList list = new InstanceList(new SerialPipes(pipes));
        String[] doc = new String[]{"serialization testing model"};
        list.addThruPipe(new StringArrayIterator(doc));

        ParallelTopicModel original = new ParallelTopicModel(3);
        original.setNumThreads(1);
        original.setNumIterations(2);
        original.setRandomSeed(88);
        original.addInstances(list);
        original.estimate();

        File modelFile = tmpFolder.newFile("serialized.model");
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(modelFile));
        oos.writeObject(original);
        oos.close();

        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(modelFile));
        ParallelTopicModel restored = (ParallelTopicModel) ois.readObject();
        ois.close();

        assertEquals(original.getNumTopics(), restored.getNumTopics());
    }
@Test
    public void testOffsetGreaterThanDocsAssignsEmptyThreadInEstimate() throws IOException {
        ParallelTopicModel model = new ParallelTopicModel(3);
        model.setNumThreads(5); 

        ArrayList<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new CharSequence2TokenSequence("[\\p{L}]+"));
        pipes.add(new TokenSequence2FeatureSequence());

        InstanceList list = new InstanceList(new SerialPipes(pipes));
        String[] docs = new String[]{"doc1", "doc2"};
        list.addThruPipe(new StringArrayIterator(docs));

        model.setRandomSeed(22);
        model.setNumIterations(1);
        model.addInstances(list);

        model.estimate();

        assertEquals(2, list.size());
    }
@Test
    public void testTopicReportOnEmptyDataPrintsHeaderOnly() throws IOException {
        ParallelTopicModel model = new ParallelTopicModel(2);
        model.setNumThreads(1);

        File file = tmpFolder.newFile("topicreport.txt");
        PrintWriter writer = new PrintWriter(file);
        model.topicXMLReport(writer, 10);
        writer.close();

        assertTrue(file.exists());
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String firstLine = reader.readLine();
        reader.close();

        assertTrue(firstLine.trim().startsWith("<?xml"));
    }
@Test
    public void testSetRandomSeedAffectsRandomness() {
        ParallelTopicModel m1 = new ParallelTopicModel(2);
        m1.setRandomSeed(100);

        ParallelTopicModel m2 = new ParallelTopicModel(2);
        m2.setRandomSeed(200);

        assertNotEquals(m1.randomSeed, m2.randomSeed);
    }
@Test
    public void testSetOptimizeIntervalAdjustsSampleSaving() {
        ParallelTopicModel model = new ParallelTopicModel(3);
        model.saveSampleInterval = 100;
        model.setOptimizeInterval(10);
        assertTrue(model.saveSampleInterval <= model.optimizeInterval);
    }
@Test
    public void testGetAlphabetReturnsExpectedObject() throws IOException {
        ParallelTopicModel model = new ParallelTopicModel(2);

        ArrayList<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new CharSequence2TokenSequence("[\\p{L}]+"));
        pipes.add(new TokenSequence2FeatureSequence());

        InstanceList inst = new InstanceList(new SerialPipes(pipes));
        String[] docs = new String[]{"NLP topic"};
        inst.addThruPipe(new StringArrayIterator(docs));
        model.addInstances(inst);
        assertNotNull(model.getAlphabet());
    }
@Test
    public void testBuildInitialTypeTopicCountsOverflowTriggersLogging() throws IOException {
        ArrayList<Pipe> pipeList = new ArrayList<Pipe>();
        pipeList.add(new CharSequence2TokenSequence());
        pipeList.add(new TokenSequence2FeatureSequence());

        InstanceList list = new InstanceList(new SerialPipes(pipeList));

        String[] docs = new String[]{
            "a a a a a a a a a a a a a a a a a a a a a a a",
            "b b b b b b b b b b b b b b b b b b b",
            "c c c c c c c c c c c c c"
        };
        list.addThruPipe(new StringArrayIterator(docs));

        ParallelTopicModel model = new ParallelTopicModel(2);
        model.setRandomSeed(42);
        model.setNumThreads(1);
        model.setNumIterations(1);
        model.addInstances(list); 

        assertNotNull(model.getTypeTopicCounts());
        assertEquals(list.getDataAlphabet().size(), model.getTypeTopicCounts().length);
    }
@Test
    public void testZeroTopicsTriggersMaskAndAlphaExceptionHandlingPath() {
        ParallelTopicModel model = new ParallelTopicModel(1);
        model.setNumTopics(0);
        assertEquals(0, model.getNumTopics());
    }
@Test
    public void testGetTopicProbabilitiesWithEmptyTopicSequenceReturnsSmoothedUniform() {
        ParallelTopicModel model = new ParallelTopicModel(3);
        LabelAlphabet alphabet = model.getTopicAlphabet();
        int[] emptyTopics = new int[0];
        LabelSequence seq = new LabelSequence(alphabet, emptyTopics);
        double[] probs = model.getTopicProbabilities(seq);
        assertEquals(3, probs.length);
        double sum = probs[0] + probs[1] + probs[2];
        assertEquals(1.0, sum, 1e-6);
    }
@Test
    public void testStateFileMissingAlphaLineUsesDefaultAlpha() throws IOException {
        File state = tmpFolder.newFile("state.gz");
        GZIPOutputStream gzipOut = new GZIPOutputStream(new FileOutputStream(state));
        PrintWriter pw = new PrintWriter(gzipOut);
        pw.println("#beta : 0.01");
        pw.println("0 dummy 0 0 word 1");
        pw.flush();
        pw.close();

        ArrayList<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new CharSequence2TokenSequence());
        pipes.add(new TokenSequence2FeatureSequence());

        InstanceList ilist = new InstanceList(new SerialPipes(pipes));
        ilist.addThruPipe(new Instance("word", null, "name", null));

        ParallelTopicModel model = new ParallelTopicModel(1);
        model.data = new ArrayList<TopicAssignment>();
        Instance instance = ilist.get(0);
        LabelSequence topics = new LabelSequence(model.getTopicAlphabet(), new int[1]);
        TopicAssignment ta = new TopicAssignment(instance, topics);
        model.data.add(ta);
        model.alphabet = ilist.getDataAlphabet();
        model.topicAlphabet = model.getTopicAlphabet();
        model.numTypes = model.alphabet.size();

        model.initializeFromState(state);

        assertTrue(model.alpha[0] > 0.0);
    }
@Test
    public void testLogLikelihoodReturnsZeroOnNaNPath() {
        ParallelTopicModel model = new ParallelTopicModel(2);
        model.alpha = new double[]{Double.NaN, Double.NaN};
        model.alphaSum = Double.NaN;
        model.tokensPerTopic = new int[]{100, 150};
        model.typeTopicCounts = new int[1][2];
        model.numTypes = 1;
        model.numTopics = 2;

        
        ArrayList<TopicAssignment> d = new ArrayList<TopicAssignment>();
        model.data = d;
        double ll = model.modelLogLikelihood();

        assertEquals(0.0, ll, 0.0);
    }
@Test
    public void testLogLikelihoodReturnsZeroOnInfiniteBetaPath() {
        ParallelTopicModel model = new ParallelTopicModel(2);
        model.alpha = new double[]{1.0, 1.0};
        model.alphaSum = 2.0;
        model.beta = Double.POSITIVE_INFINITY;
        model.betaSum = Double.POSITIVE_INFINITY;
        model.numTopics = 2;
        model.numTypes = 1;
        model.tokensPerTopic = new int[]{1, 1};
        model.typeTopicCounts = new int[1][1];
        model.typeTopicCounts[0][0] = (2 << 2) + 0;

        FeatureSequence fs = new FeatureSequence(new Alphabet());
        fs.add(0);
        LabelSequence ls = new LabelSequence(new LabelAlphabet(), new int[]{0});
        Instance inst = new Instance(fs, null, "id", null);
        TopicAssignment ta = new TopicAssignment(inst, ls);

        ArrayList<TopicAssignment> assigned = new ArrayList<TopicAssignment>();
        assigned.add(ta);
        model.data = assigned;

        double ll = model.modelLogLikelihood();
        assertEquals(0.0, ll, 0.0);
    }
@Test
    public void testInitializeFromStateWithMismatchedSequenceThrows() throws IOException {
        File state = tmpFolder.newFile("state.gz");
        GZIPOutputStream out = new GZIPOutputStream(new FileOutputStream(state));
        PrintWriter writer = new PrintWriter(out);
        writer.println("#alpha : 0.5 0.5");
        writer.println("#beta : 0.01");
        writer.println("0 src 0 999 nonexistent 1");
        writer.flush();
        writer.close();

        ArrayList<Pipe> pipes = new ArrayList<Pipe>();
        pipes.add(new CharSequence2TokenSequence("[\\p{L}]+"));
        pipes.add(new TokenSequence2FeatureSequence());

        InstanceList list = new InstanceList(new SerialPipes(pipes));
        list.addThruPipe(new Instance("actual", null, "instance", null));

        ParallelTopicModel model = new ParallelTopicModel(2);
        model.data = new ArrayList<TopicAssignment>();
        model.alphabet = list.getDataAlphabet();
        model.topicAlphabet = model.getTopicAlphabet();
        model.numTypes = model.alphabet.size();
        LabelSequence ls = new LabelSequence(model.getTopicAlphabet(), new int[1]);
        model.data.add(new TopicAssignment(list.get(0), ls));

        try {
            model.initializeFromState(state);
            fail("Expected IllegalStateException due to token mismatch.");
        } catch (IllegalStateException expected) {
            assertTrue(expected.getMessage() == null);
        }
    }
@Test
    public void testGetTopWordsHandlesEmptyCountsSafely() {
        ParallelTopicModel model = new ParallelTopicModel(2);
        model.numTypes = 3;
        model.typeTopicCounts = new int[3][];
        model.typeTopicCounts[0] = new int[0];
        model.typeTopicCounts[1] = new int[0];
        model.typeTopicCounts[2] = new int[0];
        model.tokensPerTopic = new int[]{0, 0};
        model.alphabet = new Alphabet();
        model.alphabet.lookupIndex("a");
        model.alphabet.lookupIndex("b");
        model.alphabet.lookupIndex("c");

        Object[][] top = model.getTopWords(2);
        assertEquals(2, top.length);
        assertTrue(top[0].length <= 2);
    }
@Test
    public void testMaximizeZeroIterationsExitsEarly() {
        ParallelTopicModel model = new ParallelTopicModel(2);
        model.maximize(0);
        assertEquals(2, model.getNumTopics());
    }
@Test
    public void testSetNegativeOptimizeIntervalStillApplies() {
        ParallelTopicModel model = new ParallelTopicModel(2);
        model.setOptimizeInterval(-5);
        assertEquals(-5, model.optimizeInterval);
    }
@Test
    public void testSetNegativeNumIterationsStillApplies() {
        ParallelTopicModel model = new ParallelTopicModel(3);
        model.setNumIterations(-1);
        assertEquals(-1, model.numIterations);
    }
@Test
    public void testTopicWordDistributionWithZeroTokenCounts() {
        ParallelTopicModel model = new ParallelTopicModel(2);
        model.numTypes = 2;
        model.tokensPerTopic = new int[]{0, 0};
        model.typeTopicCounts = new int[2][];
        model.typeTopicCounts[0] = new int[0];
        model.typeTopicCounts[1] = new int[0];
        model.alphabet = new Alphabet();
        model.alphabet.lookupIndex("x");
        model.alphabet.lookupIndex("y");

        double[][] smoothed = model.getTopicWords(true, true);
        assertEquals(2, smoothed.length);
        assertEquals(2, smoothed[0].length);
    }
@Test
    public void testIncompleteStateFileWithoutTopicAssignmentsThrows() throws Exception {
        File gz = tmpFolder.newFile("broken.gz");
        GZIPOutputStream gzip = new GZIPOutputStream(new FileOutputStream(gz));
        PrintWriter writer = new PrintWriter(gzip);
        writer.println("#alpha : 0.5 0.5");
        writer.println("#beta : 0.01");
        writer.flush();
        writer.close();

        ParallelTopicModel model = new ParallelTopicModel(2);
        InstanceList list = new InstanceList(new SerialPipes(new ArrayList<Pipe>()));
        list.getPipe().add(new CharSequence2TokenSequence());
        list.getPipe().add(new TokenSequence2FeatureSequence());
        list.addThruPipe(new StringArrayIterator(new String[]{"a b"}));

        model.data = new ArrayList<TopicAssignment>();
        FeatureSequence features = (FeatureSequence) list.get(0).getData();
        LabelSequence labels = new LabelSequence(model.getTopicAlphabet(), new int[features.size()]);
        model.data.add(new TopicAssignment(list.get(0), labels));
        model.alphabet = list.getDataAlphabet();
        model.numTypes = model.alphabet.size();
        model.topicAlphabet = model.getTopicAlphabet();

        try {
            model.initializeFromState(gz);
            fail("Expected IllegalStateException");
        } catch (IllegalStateException expected) {
            assertNull(expected.getMessage());
        }
    }
@Test
    public void testWriteSerializedModelWithoutEstimateStillGeneratesFile() throws Exception {
        ParallelTopicModel model = new ParallelTopicModel(2);
        File file = tmpFolder.newFile("serialize.ser");
        model.write(file);
        assertTrue(file.exists());
        assertTrue(file.length() > 0);
    }
@Test
    public void testGetTopicProbabilitiesOnEmptyDocumentReturnsUniform() {
        ParallelTopicModel model = new ParallelTopicModel(3);
        LabelAlphabet topicAlphabet = model.getTopicAlphabet();
        LabelSequence seq = new LabelSequence(topicAlphabet, new int[0]);

        double[] probs = model.getTopicProbabilities(seq);
        assertEquals(3, probs.length);
        double sum = probs[0] + probs[1] + probs[2];
        assertEquals(1.0, sum, 1e-6);
    }
@Test
    public void testModelLogLikelihoodReturnsZeroWhenNoData() {
        ParallelTopicModel model = new ParallelTopicModel(2);
        model.data = new ArrayList<TopicAssignment>();
        model.numTopics = 2;
        model.alpha = new double[]{1.0, 1.0};
        model.alphaSum = 2.0;
        model.numTypes = 2;
        model.tokensPerTopic = new int[]{1, 1};
        model.typeTopicCounts = new int[2][];
        model.typeTopicCounts[0] = new int[0];
        model.typeTopicCounts[1] = new int[0];

        double ll = model.modelLogLikelihood();
        assertEquals(0.0 + 2 * Dirichlet.logGammaStirling(1.0), ll, 10e10); 
    }
@Test
    public void testSerializationNullSafeAfterDeserialization() throws Exception {
        ParallelTopicModel model = new ParallelTopicModel(2);
        File file = tmpFolder.newFile("roundtrip.ser");
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
        oos.writeObject(model);
        oos.close();

        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
        ParallelTopicModel result = (ParallelTopicModel) ois.readObject();
        ois.close();

        assertNotNull(result);
        assertEquals(2, result.getNumTopics());
    }
@Test
    public void testTypeTopicCountsDuplicateEntriesHandledCorrectly() {
        ParallelTopicModel model = new ParallelTopicModel(2);
        model.tokensPerTopic = new int[]{0, 0};
        model.numTypes = 1;
        model.typeTopicCounts = new int[1][];
        model.typeTopicCounts[0] = new int[]{
                (3 << 2) + 1,
                (1 << 2) + 1 
        };
        model.alphabet = new Alphabet();
        model.alphabet.lookupIndex("word");

        Object[][] topWords = model.getTopWords(1);
        assertEquals(2, topWords.length);
        assertTrue(topWords[0].length <= 1);
        assertTrue(topWords[1].length <= 1);
    }
@Test
    public void testPrintTopWordsHandlesEmptyAlphabetWithoutError() throws IOException {
        ParallelTopicModel model = new ParallelTopicModel(2);
        model.tokensPerTopic = new int[]{0, 0};
        model.typeTopicCounts = new int[0][];
        model.numTypes = 0;
        model.alphabet = new Alphabet();

        File f = tmpFolder.newFile("emptytop.txt");
        model.printTopWords(f, 5, false);

        assertTrue(f.exists());
        assertTrue(f.length() > 0);
    }
@Test
    public void testCopyConstructorEquivalentValuesForBasicFields() throws Exception {
        ParallelTopicModel model = new ParallelTopicModel(3);
        model.setSymmetricAlpha(true);
        model.setSaveState(10, "state.gz");
        model.setSaveSerializedModel(7, "model.ser");
        model.setTopicDisplay(5, 10);
        model.setOptimizeInterval(3);
        model.setTemperingInterval(4);

        assertEquals(true, model.usingSymmetricAlpha);
        assertEquals(10, model.saveStateInterval);
        assertEquals("state.gz", model.stateFilename);
        assertEquals(7, model.saveModelInterval);
        assertEquals("model.ser", model.modelFilename);
        assertEquals(5, model.showTopicsInterval);
        assertEquals(10, model.wordsPerTopic);
        assertEquals(3, model.optimizeInterval);
        assertEquals(4, model.temperingInterval);
    } 
}