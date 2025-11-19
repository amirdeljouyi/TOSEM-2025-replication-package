public class ParallelTopicModel_2_GPTLLMTest { 

 @Test
    public void testConstructorSetsDefaults() {
        ParallelTopicModel model = new ParallelTopicModel(3, 1.5, 0.01);
        assertEquals(3, model.getNumTopics());

        double[] alpha = model.alpha;
        assertEquals(3, alpha.length);
        assertEquals(0.5, alpha[0], 1e-6);
        assertEquals(0.5, alpha[1], 1e-6);
        assertEquals(0.5, alpha[2], 1e-6);

        assertEquals(1.5, model.alphaSum, 1e-6);
        assertEquals(0.01, model.beta, 1e-6);
    }
@Test
    public void testSetNumTopicsAdjustsBitmask() {
        LabelAlphabet topicAlphabet = new LabelAlphabet();
        topicAlphabet.lookupIndex("topic0");
        topicAlphabet.lookupIndex("topic1");
        topicAlphabet.lookupIndex("topic2");
        topicAlphabet.lookupIndex("topic3");
        topicAlphabet.lookupIndex("topic4");

        ParallelTopicModel model = new ParallelTopicModel(topicAlphabet, 5.0, 0.01);
        model.setNumTopics(5);

        assertEquals(5, model.getNumTopics());
        
        assertEquals(5, model.alpha.length);
    }
@Test
    public void testAddInstancesPopulatesData() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("apple");
        alphabet.lookupIndex("banana");
        LabelAlphabet topicAlphabet = new LabelAlphabet();

        ParallelTopicModel model = new ParallelTopicModel(2, 1.0, 0.01);
        model.setRandomSeed(42);

        InstanceList instances = new InstanceList(alphabet, null);
        FeatureSequence fs = new FeatureSequence(alphabet);
        fs.add("apple");
        fs.add("banana");

        Instance instance = new Instance(fs, null, "doc1", null);
        instances.addThruPipe(instance);

        model.addInstances(instances);

        ArrayList<TopicAssignment> data = model.getData();
        assertEquals(1, data.size());
        LabelSequence sequence = data.get(0).topicSequence;
        Object seqData = data.get(0).instance.getData();
        assertTrue(seqData instanceof FeatureSequence);
        assertEquals(((FeatureSequence) seqData).size(), sequence.size());

        int topicA = sequence.getIndexAtPosition(0);
        int topicB = sequence.getIndexAtPosition(1);
        assertTrue(topicA >= 0 && topicA < 2);
        assertTrue(topicB >= 0 && topicB < 2);
    }
@Test
    public void testGetTopicProbabilitiesSumToOne() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("word1");
        LabelAlphabet topicAlphabet = new LabelAlphabet();

        ParallelTopicModel model = new ParallelTopicModel(2, 1.0, 0.01);
        model.setRandomSeed(10);

        InstanceList list = new InstanceList(alphabet, null);
        FeatureSequence seq = new FeatureSequence(alphabet);
        seq.add("word1");
        seq.add("word1");
        Instance instance = new Instance(seq, null, "doc", null);
        list.addThruPipe(instance);

        model.addInstances(list);
        double[] probs = model.getTopicProbabilities(0);

        assertEquals(2, probs.length);
        double sum = probs[0] + probs[1];
        assertTrue(probs[0] >= 0.0 && probs[0] <= 1.0);
        assertTrue(probs[1] >= 0.0 && probs[1] <= 1.0);
        assertEquals(1.0, sum, 1e-6);
    }
@Test
    public void testDisplayTopWordsReturnsString() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("w1");
        alphabet.lookupIndex("w2");
        alphabet.lookupIndex("w3");

        LabelAlphabet topicAlphabet = new LabelAlphabet();
        ParallelTopicModel model = new ParallelTopicModel(2, 1.0, 0.01);
        model.setRandomSeed(99);

        InstanceList list = new InstanceList(alphabet, null);
        FeatureSequence seq1 = new FeatureSequence(alphabet);
        seq1.add("w1");
        seq1.add("w2");

        FeatureSequence seq2 = new FeatureSequence(alphabet);
        seq2.add("w2");
        seq2.add("w3");

        list.addThruPipe(new Instance(seq1, null, "doc1", null));
        list.addThruPipe(new Instance(seq2, null, "doc2", null));
        model.addInstances(list);

        String output = model.displayTopWords(5, true);
        assertNotNull(output);
        assertTrue(output.length() > 0);
    }
@Test
    public void testGetSortedWordsReturnsExpectedSize() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("x");
        alphabet.lookupIndex("y");

        ParallelTopicModel model = new ParallelTopicModel(2, 1.0, 0.01);
        model.setRandomSeed(7);

        InstanceList list = new InstanceList(alphabet, null);
        FeatureSequence seq = new FeatureSequence(alphabet);
        seq.add("x");
        seq.add("y");
        list.addThruPipe(new Instance(seq, null, "doc", null));

        model.addInstances(list);
        ArrayList<TreeSet<IDSorter>> sortedWords = model.getSortedWords();

        assertEquals(2, sortedWords.size());
        TreeSet<IDSorter> topic0Words = sortedWords.get(0);
        for (IDSorter sorter : topic0Words) {
            assertTrue(sorter.getID() >= 0);
        }
    }
@Test
    public void testSetOptimizeIntervalUpdatesSampleInterval() {
        ParallelTopicModel model = new ParallelTopicModel(3, 1.0, 0.01);
        model.setSaveSerializedModel(20, "model");
        model.setOptimizeInterval(10);

        assertEquals(10, model.optimizeInterval);
        assertTrue(model.saveSampleInterval <= model.optimizeInterval);
    }
@Test
    public void testSetNumThreadsSavesCorrectly() {
        ParallelTopicModel model = new ParallelTopicModel(3);
        model.setNumThreads(4);
        assertEquals(4, model.numThreads);
    }
@Test
    public void testSetTopicDisplaySetsValues() {
        ParallelTopicModel model = new ParallelTopicModel(3);
        model.setTopicDisplay(25, 6);

        assertEquals(25, model.showTopicsInterval);
        assertEquals(6, model.wordsPerTopic);
    }
@Test
    public void testSetSaveStateAndModel() {
        ParallelTopicModel model = new ParallelTopicModel(4);
        model.setSaveState(100, "state.gz");
        model.setSaveSerializedModel(200, "model.ser");

        assertEquals(100, model.saveStateInterval);
        assertEquals("state.gz", model.stateFilename);
        assertEquals(200, model.saveModelInterval);
        assertEquals("model.ser", model.modelFilename);
    }
@Test
    public void testSetNumTopicsWithPowerOfTwo() {
        LabelAlphabet topicAlphabet = new LabelAlphabet();
        topicAlphabet.lookupIndex("topic0");
        topicAlphabet.lookupIndex("topic1");
        topicAlphabet.lookupIndex("topic2");
        topicAlphabet.lookupIndex("topic3"); 

        ParallelTopicModel model = new ParallelTopicModel(topicAlphabet, 4.0, 0.01);
        model.setNumTopics(4);

        assertEquals(4, model.getNumTopics());
        assertEquals(4, model.alpha.length);
    }
@Test
    public void testAddInstancesWithEmptyInstanceList() {
        Alphabet alphabet = new Alphabet();
        LabelAlphabet topicAlphabet = new LabelAlphabet();
        ParallelTopicModel model = new ParallelTopicModel(2, 1.0, 0.01);

        InstanceList emptyInstances = new InstanceList(alphabet, null);
        model.addInstances(emptyInstances);

        assertEquals(0, model.getData().size());
    }
@Test
    public void testGetTopicProbabilitiesWithZeroLengthTopicSequence() {
        LabelAlphabet topicAlphabet = new LabelAlphabet();
        ParallelTopicModel model = new ParallelTopicModel(3, 3.0, 0.01);

        LabelSequence emptySequence = new LabelSequence(topicAlphabet, new int[0]);

        double[] distribution = model.getTopicProbabilities(emptySequence);
        assertEquals(3, distribution.length);
        double sum = distribution[0] + distribution[1] + distribution[2];
        assertEquals(1.0, sum, 0.00001);

        assertTrue(distribution[0] > 0);
        assertTrue(distribution[1] > 0);
        assertTrue(distribution[2] > 0);
    }
@Test
    public void testSetSymmetricAlphaTrue() {
        ParallelTopicModel model = new ParallelTopicModel(3, 1.5, 0.01);
        model.setSymmetricAlpha(true);

        assertTrue(model.usingSymmetricAlpha);
    }
@Test
    public void testPrintTopWordsWithZeroWordsRequested() throws Exception {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("w1");
        LabelAlphabet topicAlphabet = new LabelAlphabet();

        ParallelTopicModel model = new ParallelTopicModel(2, 1.0, 0.01);
        model.setRandomSeed(21);

        InstanceList list = new InstanceList(alphabet, null);
        FeatureSequence fs = new FeatureSequence(alphabet);
        fs.add("w1");
        fs.add("w1");
        list.addThruPipe(new Instance(fs, null, "doc", null));
        model.addInstances(list);

        File file = File.createTempFile("topwords-zero", ".txt");
        try {
            model.printTopWords(file, 0, true);
            assertTrue(file.exists());
        } finally {
            file.delete();
        }
    }
@Test
    public void testGetTopWordsWithFewerThanRequestedWords() {
        Alphabet alphabet = new Alphabet();
        int id = alphabet.lookupIndex("uniqueWord");

        ParallelTopicModel model = new ParallelTopicModel(1, 1.0, 0.01);
        model.alphabet = alphabet;
        model.numTypes = 1;
        model.typeTopicCounts = new int[1][];
        model.typeTopicCounts[0] = new int[] { (1 << 1) + 0 }; 
        model.tokensPerTopic = new int[] { 1 };

        Object[][] result = model.getTopWords(5);
        assertEquals(1, result.length);
        assertEquals("uniqueWord", result[0][0]);
    }
@Test
    public void testSetSaveStateAndPrintState() throws Exception {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("token");

        ParallelTopicModel model = new ParallelTopicModel(1, 1.0, 0.01);
        model.setRandomSeed(1);
        model.setSaveState(1, "test_state.gz");

        InstanceList list = new InstanceList(alphabet, null);
        FeatureSequence fs = new FeatureSequence(alphabet);
        fs.add("token");
        list.addThruPipe(new Instance(fs, null, "doc", null));
        model.addInstances(list);

        File file = File.createTempFile("state", ".gz");
        try {
            model.printState(file);
            assertTrue(file.exists());
        } finally {
            file.delete();
        }
    }
@Test
    public void testModelLogLikelihoodNoDocuments() {
        ParallelTopicModel model = new ParallelTopicModel(2, 1.0, 0.01);
        double ll = model.modelLogLikelihood();
        assertEquals(0.0, ll, 0.0001);
    }
@Test
    public void testTopicXMLReportMinimal() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("w");

        ParallelTopicModel model = new ParallelTopicModel(1, 1.0, 0.01);
        model.setRandomSeed(1);

        InstanceList list = new InstanceList(alphabet, null);
        FeatureSequence fs = new FeatureSequence(alphabet);
        fs.add("w");
        list.addThruPipe(new Instance(fs, null, "doc", null));
        model.addInstances(list);

        StringWriter writer = new StringWriter();
        PrintWriter out = new PrintWriter(writer);

        model.topicXMLReport(out, 1);
        out.flush();
        String result = writer.toString();
        assertTrue(result.contains("<topicModel>"));
    }
@Test
    public void testPrintDocumentTopicsWithThresholdGreaterThanAny() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("x");

        ParallelTopicModel model = new ParallelTopicModel(1, 1.0, 0.01);
        model.setRandomSeed(2);

        InstanceList list = new InstanceList(alphabet, null);
        FeatureSequence fs = new FeatureSequence(alphabet);
        fs.add("x");
        list.addThruPipe(new Instance(fs, null, "doc", null));
        model.addInstances(list);

        StringWriter writer = new StringWriter();
        PrintWriter out = new PrintWriter(writer);

        model.printDocumentTopics(out, 0.999, 10);
        out.flush();

        assertTrue(writer.toString().contains("doc"));
    }
@Test
    public void testPrintStatePrintStream() throws Exception {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("token");

        ParallelTopicModel model = new ParallelTopicModel(1, 1.0, 0.01);
        model.setRandomSeed(3);

        InstanceList list = new InstanceList(alphabet, null);
        FeatureSequence fs = new FeatureSequence(alphabet);
        fs.add("token");
        list.addThruPipe(new Instance(fs, null, "doc", null));
        model.addInstances(list);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outputStream);

        model.printState(out);
        out.flush();

        String output = outputStream.toString("UTF-8");
        assertTrue(output.contains("#alpha : "));
    }
@Test
    public void testWriteAndReadSerializedModel() throws Exception {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("token");

        ParallelTopicModel model = new ParallelTopicModel(1, 1.0, 0.01);
        model.setRandomSeed(4);

        InstanceList list = new InstanceList(alphabet, null);
        FeatureSequence fs = new FeatureSequence(alphabet);
        fs.add("token");
        list.addThruPipe(new Instance(fs, null, "doc", null));
        model.addInstances(list);

        File tempFile = File.createTempFile("model", ".ser");
        try {
            model.write(tempFile);
            assertTrue(tempFile.exists());

            ParallelTopicModel loaded = ParallelTopicModel.read(tempFile);
            assertNotNull(loaded);
            assertEquals(1, loaded.getNumTopics());
        } finally {
            tempFile.delete();
        }
    }
@Test
    public void testInferencerIsNotNull() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("token");

        ParallelTopicModel model = new ParallelTopicModel(1, 1.0, 0.01);
        model.setRandomSeed(8);

        InstanceList list = new InstanceList(alphabet, null);
        FeatureSequence fs = new FeatureSequence(alphabet);
        fs.add("token");
        list.addThruPipe(new Instance(fs, null, "doc", null));
        model.addInstances(list);

        assertNotNull(model.getInferencer());
    }
@Test
    public void testGetDocumentTopicsWithNoNormalizationAndNoSmoothing() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("word");

        ParallelTopicModel model = new ParallelTopicModel(2, 2.0, 0.01);
        model.setRandomSeed(10);

        InstanceList instances = new InstanceList(alphabet, null);
        FeatureSequence fs = new FeatureSequence(alphabet);
        fs.add("word");
        fs.add("word");
        instances.addThruPipe(new Instance(fs, null, "doc", null));

        model.addInstances(instances);

        double[][] docTopics = model.getDocumentTopics(false, false);
        assertEquals(1, docTopics.length);
        assertEquals(2, docTopics[0].length);

        double total = docTopics[0][0] + docTopics[0][1];
        assertTrue(total == 2.0);
    }
@Test
    public void testPrintDocumentTopicsWithNameNull() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("alpha");

        ParallelTopicModel model = new ParallelTopicModel(2, 2.0, 0.01);
        model.setRandomSeed(33);

        Instance instance = new Instance(
                new FeatureSequence(alphabet, new int[]{0}), 
                null,
                null, 
                null
        );
        InstanceList list = new InstanceList(alphabet, null);
        list.addThruPipe(instance);

        model.addInstances(list);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        model.printDocumentTopics(pw);
        pw.flush();
        String output = sw.toString();
        assertTrue(output.contains("no-name"));
    }
@Test
    public void testGetTopicDocumentsReturnsCorrectSize() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("dog");
        alphabet.lookupIndex("cat");

        ParallelTopicModel model = new ParallelTopicModel(2, 1.0, 0.01);
        model.setRandomSeed(8);

        FeatureSequence fs = new FeatureSequence(alphabet);
        fs.add("dog");
        fs.add("cat");

        Instance doc = new Instance(fs, null, "sample", null);
        InstanceList list = new InstanceList(alphabet, null);
        list.addThruPipe(doc);

        model.addInstances(list);

        ArrayList<TreeSet<IDSorter>> result = model.getTopicDocuments(0.01);
        assertEquals(2, result.size());
    }
@Test
    public void testTopicWordWeightsWithDefaultSetup() throws IOException {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("a");

        ParallelTopicModel model = new ParallelTopicModel(2, 1.0, 0.01);
        model.setRandomSeed(3);

        FeatureSequence fs = new FeatureSequence(alphabet);
        fs.add("a");
        InstanceList list = new InstanceList(alphabet, null);
        list.addThruPipe(new Instance(fs, null, "x", null));

        model.addInstances(list);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        model.printTopicWordWeights(pw);
        pw.flush();

        String out = sw.toString();
        assertTrue(out.contains("a"));
    }
@Test
    public void testPrintTopicDocumentsPrintsLimitedDocs() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("word");

        ParallelTopicModel model = new ParallelTopicModel(2, 1.0, 0.01);
        model.setRandomSeed(7);

        InstanceList list = new InstanceList(alphabet, null);
        FeatureSequence fs = new FeatureSequence(alphabet);
        fs.add("word");
        list.addThruPipe(new Instance(fs, null, "docA", null));

        model.addInstances(list);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        model.printTopicDocuments(pw, 1);
        pw.flush();

        String out = sw.toString();
        assertTrue(out.contains("0"));
        assertTrue(out.contains("docA"));
    }
@Test
    public void testInitializeFromCorruptedStateThrowsException() throws Exception {
        ParallelTopicModel model = new ParallelTopicModel(2, 1.0, 0.01);

        
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("token");
        Alphabet dataAlphabet = alphabet;

        FeatureSequence fs = new FeatureSequence(dataAlphabet);
        fs.add("token");

        Instance instance = new Instance(fs, null, "d", null);
        InstanceList instanceList = new InstanceList(alphabet, null);
        instanceList.addThruPipe(instance);

        model.addInstances(instanceList);

        File tempFile = File.createTempFile("badstate", ".gz");
        try (PrintWriter pw = new PrintWriter(
                new OutputStreamWriter(
                        new GZIPOutputStream(
                                new FileOutputStream(tempFile)), "UTF-8"))) {

            pw.println("#alpha : 0.5 0.5");
            pw.println("#beta : 0.01");
            pw.println("0 source 0 9999 foo 1"); 
        }

        boolean threw = false;
        try {
            model.initializeFromState(tempFile);
        } catch (IllegalStateException e) {
            threw = true;
        }
        assertTrue(threw);

        tempFile.delete();
    }
@Test
    public void testCreateWithOneTopicValidAlpha() {
        ParallelTopicModel model = new ParallelTopicModel(1, 1.0, 0.1);
        assertEquals(1, model.alpha.length);
        assertEquals(1.0, model.alphaSum, 0.0001);
        assertEquals(0.1, model.beta, 0.0001);
    }
@Test
    public void testSetAlphaAfterSetNumTopics() {
        ParallelTopicModel model = new ParallelTopicModel(3, 3.0, 0.01);
        model.setNumTopics(6);
        assertEquals(6, model.alpha.length);
        assertEquals(3.0, model.alphaSum, 0.0001); 
    }
@Test
    public void testGetSubCorpusTopicWordsReturnsCorrectShape() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("a");
        alphabet.lookupIndex("b");

        ParallelTopicModel model = new ParallelTopicModel(2, 1.0, 0.01);
        model.setRandomSeed(5);

        FeatureSequence fs = new FeatureSequence(alphabet);
        fs.add("a");
        fs.add("b");

        InstanceList list = new InstanceList(alphabet, null);
        list.addThruPipe(new Instance(fs, null, "doc", null));

        model.addInstances(list);

        boolean[] mask = new boolean[1];
        mask[0] = true;

        double[][] result = model.getSubCorpusTopicWords(mask, true, true);
        assertEquals(2, result.length);
        assertEquals(2, result[0].length);
    }
@Test
    public void testMaximizeStopsEarlyWhenNoChange() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("same");

        ParallelTopicModel model = new ParallelTopicModel(1, 1.0, 0.01);
        model.setRandomSeed(5);

        FeatureSequence fs = new FeatureSequence(alphabet);
        fs.add("same");
        fs.add("same");

        InstanceList list = new InstanceList(alphabet, null);
        list.addThruPipe(new Instance(fs, null, "d", null));
        model.addInstances(list);

        model.maximize(5); 
        assertEquals(1, model.numTopics);
    }
@Test
    public void testPrintTypeTopicCountsSingleToken() throws IOException {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("x");

        ParallelTopicModel model = new ParallelTopicModel(1, 1.0, 0.01);
        model.setRandomSeed(4);

        FeatureSequence fs = new FeatureSequence(alphabet);
        fs.add("x");

        InstanceList list = new InstanceList(alphabet, null);
        list.addThruPipe(new Instance(fs, null, "doc", null));
        model.addInstances(list);

        File file = File.createTempFile("ttc", ".txt");
        model.printTypeTopicCounts(file);
        assertTrue(file.exists());
        assertTrue(file.length() > 0);
        file.delete();
    }
@Test
    public void testPrintDenseDocumentTopics() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("term");

        ParallelTopicModel model = new ParallelTopicModel(2, 2.0, 0.01);
        model.setRandomSeed(20);

        FeatureSequence fs = new FeatureSequence(alphabet);
        fs.add("term");
        fs.add("term");

        InstanceList list = new InstanceList(alphabet, null);
        list.addThruPipe(new Instance(fs, null, "nullname", null));

        model.addInstances(list);

        StringWriter writer = new StringWriter();
        PrintWriter out = new PrintWriter(writer);

        model.printDenseDocumentTopics(out);
        out.flush();

        String output = writer.toString();
        assertTrue(output.contains("0"));
        assertTrue(output.contains("nullname"));
    }
@Test
    public void testGetTopicWordsWithNormalizationOffAndSmoothingOff() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("word");

        ParallelTopicModel model = new ParallelTopicModel(2, 1.0, 0.01);
        model.setRandomSeed(99);

        FeatureSequence fs = new FeatureSequence(alphabet);
        fs.add("word");
        fs.add("word");

        Instance instance = new Instance(fs, null, "docA", null);
        InstanceList list = new InstanceList(alphabet, null);
        list.addThruPipe(instance);

        model.addInstances(list);

        double[][] words = model.getTopicWords(false, false);
        assertEquals(2, words.length);
        assertEquals(1, words[0].length);
    }
@Test
    public void testSetBurninPeriodToZero() {
        ParallelTopicModel model = new ParallelTopicModel(5);
        model.setBurninPeriod(0);
        assertEquals(0, model.burninPeriod);
    }
@Test
    public void testSetSaveSerializedModelOnly() throws Exception {
        ParallelTopicModel model = new ParallelTopicModel(4);
        model.setSaveSerializedModel(20, "model.ser");
        assertEquals(20, model.saveModelInterval);
        assertEquals("model.ser", model.modelFilename);
    }
@Test
    public void testSetSaveStateWithNullFilename() {
        ParallelTopicModel model = new ParallelTopicModel(4);
        model.setSaveState(10, null);
        assertEquals(10, model.saveStateInterval);
        assertNull(model.stateFilename);
    }
@Test
    public void testSetTemperingInterval() {
        ParallelTopicModel model = new ParallelTopicModel(3);
        model.setTemperingInterval(5);
        assertEquals(5, model.temperingInterval);
    }
@Test
    public void testGetTopWordsForTopicWithNoCount() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("rare");

        ParallelTopicModel model = new ParallelTopicModel(1);
        model.alphabet = alphabet;
        model.numTypes = 1;

        model.typeTopicCounts = new int[1][];
        model.typeTopicCounts[0] = new int[0];
        model.tokensPerTopic = new int[1];

        Object[][] topWords = model.getTopWords(10);

        assertEquals(1, topWords.length);
        assertEquals(0, topWords[0].length);
    }
@Test
    public void testDisplayTopWordsWithUsingNewLinesFalse() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("foo");

        ParallelTopicModel model = new ParallelTopicModel(1);
        model.alphabet = alphabet;
        model.numTypes = 1;

        model.typeTopicCounts = new int[1][];
        int topicBits = Integer.bitCount(1) + 1;
        int topicMask = (Integer.highestOneBit(1) << 1) - 1;
        
        model.setNumTopics(1);
        model.typeTopicCounts[0] = new int[] { (2 << topicBits) + 0 };
        model.tokensPerTopic = new int[] { 2 };

        String output = model.displayTopWords(5, false);
        assertTrue(output.contains("foo"));
    }
@Test
    public void testPrintTopicWordWeightsWithoutAnyCountAboveBeta() throws IOException {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("x");

        ParallelTopicModel model = new ParallelTopicModel(2, 2.0, 0.01);
        model.setRandomSeed(123);
        model.alphabet = alphabet;
        model.numTypes = 1;
        model.tokensPerTopic = new int[] {0, 0};
        model.typeTopicCounts = new int[1][];
        model.typeTopicCounts[0] = new int[0];

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        model.printTopicWordWeights(pw);
        pw.flush();
        String out = sw.toString();
        assertTrue(out.contains("x"));
    }
@Test
    public void testGetInferencerReturnsNonNull() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("token");

        ParallelTopicModel model = new ParallelTopicModel(1, 1.0, 0.5);
        model.setRandomSeed(2);

        FeatureSequence fs = new FeatureSequence(alphabet);
        fs.add("token");
        InstanceList instances = new InstanceList(alphabet, null);
        instances.addThruPipe(new Instance(fs, null, "x", null));
        model.addInstances(instances);

        assertNotNull(model.getInferencer());
    }
@Test
    public void testModelLogLikelihoodReturnsNaNSafety() {
        ParallelTopicModel model = new ParallelTopicModel(0); 
        double likelihood = model.modelLogLikelihood();
        assertEquals(0.0, likelihood, 0.0);
    }
@Test
    public void testSetRandomSeedEffect() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("item");

        ParallelTopicModel model1 = new ParallelTopicModel(2, 1.0, 0.01);
        model1.setRandomSeed(1);
        InstanceList list1 = new InstanceList(alphabet, null);
        FeatureSequence fs1 = new FeatureSequence(alphabet);
        fs1.add("item");
        list1.addThruPipe(new Instance(fs1, null, "d1", null));
        model1.addInstances(list1);
        double[] probs1 = model1.getTopicProbabilities(0);

        ParallelTopicModel model2 = new ParallelTopicModel(2, 1.0, 0.01);
        model2.setRandomSeed(1);
        InstanceList list2 = new InstanceList(alphabet, null);
        FeatureSequence fs2 = new FeatureSequence(alphabet);
        fs2.add("item");
        list2.addThruPipe(new Instance(fs2, null, "d1", null));
        model2.addInstances(list2);
        double[] probs2 = model2.getTopicProbabilities(0);

        assertArrayEquals(probs1, probs2, 0.0001);
    }
@Test
    public void testPrintStateSkipsUnknownSource() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("bar");

        ParallelTopicModel model = new ParallelTopicModel(1, 1.0, 0.01);
        model.setRandomSeed(10);

        FeatureSequence fs = new FeatureSequence(alphabet);
        fs.add("bar");
        InstanceList list = new InstanceList(alphabet, null);
        Instance instance = new Instance(fs, null, "z", null);
        list.addThruPipe(instance);

        model.addInstances(list);

        StringWriter writer = new StringWriter();
        PrintStream out = new PrintStream(new OutputStream() {
            @Override public void write(int b) {}
        });

        model.printState(out);
        
        assertTrue(true);
    }
@Test
    public void testLearnParametersFallbackOnException() {
        ParallelTopicModel model = new ParallelTopicModel(5, 0.0, 0.01);
        model.setSymmetricAlpha(false);

        WorkerCallable[] callables = new WorkerCallable[1];
        callables[0] = null; 

        model.docLengthCounts = new int[]{0};
        model.topicDocCounts = new int[5][1];

        model.optimizeAlpha(callables);

        double[] alpha = model.alpha;
        assertEquals(1.0, alpha[0], 0.0001);
    }
@Test
    public void testDisplayTopWordsAfterSetFormatter() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("fruit");
        ParallelTopicModel model = new ParallelTopicModel(1, 1.0, 0.01);
        model.setRandomSeed(3);

        FeatureSequence fs = new FeatureSequence(alphabet);
        fs.add("fruit");
        InstanceList list = new InstanceList(alphabet, null);
        list.addThruPipe(new Instance(fs, null, "doc", null));
        model.addInstances(list);

        model.formatter = java.text.NumberFormat.getInstance(Locale.FRANCE);
        String output = model.displayTopWords(1, true);
        assertTrue(output.contains("fruit"));
    }
@Test
    public void testEstimateWithOneThreadMinimalSetup() throws Exception {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("token");

        ParallelTopicModel model = new ParallelTopicModel(2, 1.0, 0.01);
        model.setNumThreads(1);
        model.setNumIterations(1);
        model.setBurninPeriod(0);
        model.setRandomSeed(42);

        FeatureSequence sequence = new FeatureSequence(alphabet);
        sequence.add("token");
        sequence.add("token");

        InstanceList instances = new InstanceList(alphabet, null);
        instances.addThruPipe(new Instance(sequence, null, "doc", null));

        model.addInstances(instances);
        model.estimate();

        ArrayList<TopicAssignment> data = model.getData();
        assertEquals(1, data.size());
        assertEquals(2, data.get(0).topicSequence.getLength());
    }
@Test
    public void testEstimateWithMultiThreading() throws Exception {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("a");
        alphabet.lookupIndex("b");
        alphabet.lookupIndex("c");

        ParallelTopicModel model = new ParallelTopicModel(3, 1.0, 0.01);
        model.setNumThreads(2);
        model.setNumIterations(2);
        model.setBurninPeriod(0);
        model.setRandomSeed(123);

        FeatureSequence fs1 = new FeatureSequence(alphabet);
        fs1.add("a");
        fs1.add("b");

        FeatureSequence fs2 = new FeatureSequence(alphabet);
        fs2.add("c");
        fs2.add("b");

        InstanceList list = new InstanceList(alphabet, null);
        list.addThruPipe(new Instance(fs1, null, "doc1", null));
        list.addThruPipe(new Instance(fs2, null, "doc2", null));

        model.addInstances(list);
        model.estimate();

        assertEquals(2, model.getData().size());
    }
@Test
    public void testMaximizeZeroIterationNoChangeExpected() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("a");

        ParallelTopicModel model = new ParallelTopicModel(1, 1.0, 0.01);
        model.setRandomSeed(456);

        FeatureSequence fs = new FeatureSequence(alphabet);
        fs.add("a");
        fs.add("a");

        InstanceList list = new InstanceList(alphabet, null);
        list.addThruPipe(new Instance(fs, null, "d0", null));

        model.addInstances(list);
        model.maximize(0); 

        assertEquals(1, model.getNumTopics());
    }
@Test
    public void testTopicSortedWordsCountsWithZeroEntries() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("zero");

        ParallelTopicModel model = new ParallelTopicModel(2, 1.5, 0.01);
        model.alphabet = alphabet;
        model.numTypes = 1;
        model.tokensPerTopic = new int[] {0, 0};
        model.typeTopicCounts = new int[1][];
        model.typeTopicCounts[0] = new int[0];

        ArrayList<TreeSet<IDSorter>> sorted = model.getSortedWords();

        assertEquals(2, sorted.size());
        assertEquals(0, sorted.get(0).size());
        assertEquals(0, sorted.get(1).size());
    }
@Test
    public void testSetZeroTopicsExpectProperSetup() {
        ParallelTopicModel model = new ParallelTopicModel(1, 1.0, 0.01);
        model.setNumTopics(0);
        assertEquals(0, model.getNumTopics());
        assertEquals(0, model.alpha.length);
    }
@Test
    public void testEstimateWithEmptyInstanceListSkipsSampling() throws Exception {
        Alphabet alphabet = new Alphabet();

        ParallelTopicModel model = new ParallelTopicModel(2, 1.0, 0.01);
        model.setNumThreads(1);
        model.setNumIterations(1);
        model.setBurninPeriod(0);
        model.setRandomSeed(11);

        InstanceList emptyList = new InstanceList(alphabet, null);
        model.addInstances(emptyList);

        model.estimate();

        assertEquals(0, model.getData().size());
    }
@Test
    public void testOptimizeBetaHandlesZeroCounts() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("x");

        ParallelTopicModel model = new ParallelTopicModel(2, 2.0, 0.01);
        model.setRandomSeed(314);

        FeatureSequence fs = new FeatureSequence(alphabet);
        fs.add("x");
        fs.add("x");

        Instance instance = new Instance(fs, null, "docA", null);
        InstanceList instances = new InstanceList(alphabet, null);
        instances.addThruPipe(instance);

        model.addInstances(instances);
        model.buildInitialTypeTopicCounts();

        ParallelTopicModel.WorkerCallable[] callables = new ParallelTopicModel.WorkerCallable[1];
        callables[0] = new ParallelTopicModel.WorkerCallable(
                model.getNumTopics(),
                model.alpha,
                model.alphaSum,
                model.beta,
                new cc.mallet.util.Randoms(),
                model.getData(),
                model.getTypeTopicCounts(),
                model.getTokensPerTopic(),
                0,
                model.getData().size()
        );
        callables[0].initializeAlphaStatistics(10);

        
        model.optimizeBeta(callables);
        assertTrue(model.beta > 0);
    }
@Test
    public void testGetTopicProbabilitiesAllTokensInOneTopic() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("tok");

        ParallelTopicModel model = new ParallelTopicModel(3, 3.0, 0.01);
        model.setRandomSeed(77);

        FeatureSequence fs = new FeatureSequence(alphabet);
        fs.add("tok");
        fs.add("tok");
        fs.add("tok");

        Instance doc = new Instance(fs, null, "dname", null);
        InstanceList list = new InstanceList(alphabet, null);
        list.addThruPipe(doc);

        model.addInstances(list);

        
        for (int i = 0; i < model.getData().get(0).topicSequence.getLength(); i++) {
            model.getData().get(0).topicSequence.getFeatures()[i] = 0;
        }

        double[] probs = model.getTopicProbabilities(0);
        assertEquals(3, probs.length);
        assertTrue(probs[0] > probs[1]);
        assertTrue(probs[0] > probs[2]);

        double total = 0.0;
        for (double p : probs) total += p;
        assertEquals(1.0, total, 1e-6);
    }
@Test
    public void testPrintDocumentTopicsWithCustomThresholdAndMax() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("term");

        ParallelTopicModel model = new ParallelTopicModel(4, 2.0, 0.01);
        model.setRandomSeed(87);

        FeatureSequence fs = new FeatureSequence(alphabet);
        fs.add("term");

        InstanceList list = new InstanceList(alphabet, null);
        list.addThruPipe(new Instance(fs, null, "docy", null));

        model.addInstances(list);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);

        model.printDocumentTopics(writer, 0.0001, 2);
        writer.flush();

        assertTrue(stringWriter.toString().contains("docy"));
    }
@Test
    public void testPrintTopicDocumentsPrintsUpToLimit() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("x");

        ParallelTopicModel model = new ParallelTopicModel(3, 3.0, 0.01);
        model.setRandomSeed(99);

        InstanceList list = new InstanceList(alphabet, null);
        FeatureSequence fs = new FeatureSequence(alphabet);
        fs.add("x");

        list.addThruPipe(new Instance(fs, null, "d1", null));
        list.addThruPipe(new Instance(fs, null, "d2", null));
        list.addThruPipe(new Instance(fs, null, "d3", null));

        model.addInstances(list);

        StringWriter sw = new StringWriter();
        PrintWriter out = new PrintWriter(sw);

        model.printTopicDocuments(out, 2);
        out.flush();

        String result = sw.toString();
        assertTrue(result.contains("0"));
        assertTrue(result.contains("d1") || result.contains("d2") || result.contains("d3"));
    }
@Test
    public void testGetDocumentTopicsWithMultipleDocumentsDifferentLengths() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("alpha");
        alphabet.lookupIndex("beta");

        ParallelTopicModel model = new ParallelTopicModel(2, 1.0, 0.01);
        model.setRandomSeed(9);

        FeatureSequence seq1 = new FeatureSequence(alphabet);
        seq1.add("alpha");

        FeatureSequence seq2 = new FeatureSequence(alphabet);
        seq2.add("alpha");
        seq2.add("beta");

        InstanceList instances = new InstanceList(alphabet, null);
        instances.addThruPipe(new Instance(seq1, null, "doc1", null));
        instances.addThruPipe(new Instance(seq2, null, "doc2", null));

        model.addInstances(instances);

        double[][] dist = model.getDocumentTopics(true, true);

        assertEquals(2, dist.length);
        assertEquals(2, dist[0].length);
        assertEquals(2, dist[1].length);

        double sum1 = dist[0][0] + dist[0][1];
        double sum2 = dist[1][0] + dist[1][1];

        assertEquals(1.0, sum1, 1e-6);
        assertEquals(1.0, sum2, 1e-6);
    }
@Test
    public void testGetDocumentTopicsWithoutSmoothingOrNormalization() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("gamma");

        ParallelTopicModel model = new ParallelTopicModel(2, 1.0, 0.01);
        model.setRandomSeed(2);
        FeatureSequence seq = new FeatureSequence(alphabet);
        seq.add("gamma");
        seq.add("gamma");
        seq.add("gamma");

        InstanceList instances = new InstanceList(alphabet, null);
        instances.addThruPipe(new Instance(seq, null, "docX", null));
        model.addInstances(instances);

        double[][] dist = model.getDocumentTopics(false, false);

        assertEquals(1, dist.length);
        assertEquals(2, dist[0].length);
        assertEquals(3.0, dist[0][0] + dist[0][1], 1e-6);
    }
@Test
    public void testPrintTopWordsPrintStreamSingleTopic() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("dog");

        ParallelTopicModel model = new ParallelTopicModel(1, 1.0, 0.01);
        model.setRandomSeed(3);
        FeatureSequence seq = new FeatureSequence(alphabet);
        seq.add("dog");
        seq.add("dog");

        InstanceList instances = new InstanceList(alphabet, null);
        instances.addThruPipe(new Instance(seq, null, "docY", null));

        model.addInstances(instances);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);

        model.printTopWords(writer, 5, true);
        writer.flush();

        String result = stringWriter.toString();
        assertTrue(result.contains("dog"));
    }
@Test
    public void testDisplayTopWordsRespectsWordLimit() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("lion");

        ParallelTopicModel model = new ParallelTopicModel(1, 1.0, 0.01);
        model.setRandomSeed(6);
        FeatureSequence seq = new FeatureSequence(alphabet);
        seq.add("lion");

        InstanceList instances = new InstanceList(alphabet, null);
        instances.addThruPipe(new Instance(seq, null, "docL", null));

        model.addInstances(instances);

        String output = model.displayTopWords(100, true);
        assertTrue(output.contains("lion"));
    }
@Test
    public void testGetSubCorpusTopicWordsWithEmptyMaskResultsZeroMatrix() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("one");
        alphabet.lookupIndex("two");

        ParallelTopicModel model = new ParallelTopicModel(2, 1.0, 0.01);
        model.setRandomSeed(5);

        FeatureSequence fs = new FeatureSequence(alphabet);
        fs.add("one");
        fs.add("two");

        InstanceList list = new InstanceList(alphabet, null);
        list.addThruPipe(new Instance(fs, null, "doc1", null));
        model.addInstances(list);

        boolean[] mask = new boolean[1];
        mask[0] = false;

        double[][] result = model.getSubCorpusTopicWords(mask, true, true);
        assertEquals(2, result.length);
        assertEquals(2, result[0].length);

        assertTrue(result[0][0] > 0); 
        assertTrue(result[1][1] > 0);
    }
@Test
    public void testModelLogLikelihoodReturnsValidValueForSingleDoc() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("zeta");

        ParallelTopicModel model = new ParallelTopicModel(2, 1.0, 0.1);
        model.setRandomSeed(4);

        FeatureSequence fs = new FeatureSequence(alphabet);
        fs.add("zeta");
        fs.add("zeta");

        InstanceList list = new InstanceList(alphabet, null);
        list.addThruPipe(new Instance(fs, null, "D1", null));

        model.addInstances(list);
        double ll = model.modelLogLikelihood();

        assertTrue(Double.isFinite(ll));
    }
@Test
    public void testGetTopicAlphabetAndNumTopicsConsistency() {
        ParallelTopicModel model = new ParallelTopicModel(5, 5.0, 0.01);
        LabelAlphabet topicAlphabet = model.getTopicAlphabet();
        assertEquals(5, model.getNumTopics());
        assertEquals(5, topicAlphabet.size());

        assertEquals("topic0", topicAlphabet.lookupLabel(0).toString());
        assertEquals("topic4", topicAlphabet.lookupLabel(4).toString());
    }
@Test
    public void testSetNumTopicsAfterConstructionUpdatesAlphaProperly() {
        ParallelTopicModel model = new ParallelTopicModel(2, 1.0, 0.01);
        model.setNumTopics(4);
        assertEquals(4, model.alpha.length);

        double sum = 0.0;
        for (double a : model.alpha) sum += a;
        assertEquals(1.0, sum, 0.0001);
    }
@Test
    public void testSetRandomSeedAndSampleConsistency() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("seedtest");

        ParallelTopicModel model1 = new ParallelTopicModel(2, 2.0, 0.01);
        model1.setRandomSeed(123);
        FeatureSequence fs1 = new FeatureSequence(alphabet);
        fs1.add("seedtest");
        fs1.add("seedtest");
        InstanceList list1 = new InstanceList(alphabet, null);
        list1.addThruPipe(new Instance(fs1, null, "docA", null));
        model1.addInstances(list1);

        ArrayList<TopicAssignment> data1 = model1.getData();
        LabelSequence seq1 = data1.get(0).topicSequence;

        ParallelTopicModel model2 = new ParallelTopicModel(2, 2.0, 0.01);
        model2.setRandomSeed(123);
        FeatureSequence fs2 = new FeatureSequence(alphabet);
        fs2.add("seedtest");
        fs2.add("seedtest");
        InstanceList list2 = new InstanceList(alphabet, null);
        list2.addThruPipe(new Instance(fs2, null, "docA", null));
        model2.addInstances(list2);

        ArrayList<TopicAssignment> data2 = model2.getData();
        LabelSequence seq2 = data2.get(0).topicSequence;

        assertArrayEquals(seq1.getFeatures(), seq2.getFeatures());
    }
@Test
    public void testSetOptimizeIntervalGreaterThanSaveSampleIntervalAdjustsCorrectly() {
        ParallelTopicModel model = new ParallelTopicModel(3, 3.0, 0.01);
        model.setOptimizeInterval(5);
        model.setOptimizeInterval(10);
        assertTrue(model.saveSampleInterval <= model.optimizeInterval);
    }
@Test
    public void testEstimateWithNoAlphaOptimization() throws Exception {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("mango");

        ParallelTopicModel model = new ParallelTopicModel(2, 2.0, 0.01);
        model.setRandomSeed(10);
        model.setNumIterations(2);
        model.setBurninPeriod(1);
        model.setOptimizeInterval(0); 
        model.setNumThreads(1);

        FeatureSequence fs = new FeatureSequence(alphabet);
        fs.add("mango");
        fs.add("mango");

        InstanceList list = new InstanceList(alphabet, null);
        list.addThruPipe(new Instance(fs, null, "fruit", null));

        model.addInstances(list);
        model.estimate();

        double ll = model.modelLogLikelihood();
        assertTrue(Double.isFinite(ll));
    }
@Test
    public void testPrintStateOutputsAlphaAndBetaLines() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("grape");

        ParallelTopicModel model = new ParallelTopicModel(1, 1.0, 0.01);
        model.setRandomSeed(15);

        FeatureSequence fs = new FeatureSequence(alphabet);
        fs.add("grape");

        InstanceList list = new InstanceList(alphabet, null);
        list.addThruPipe(new Instance(fs, null, "docx", null));
        model.addInstances(list);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outputStream);
        model.printState(out);
        out.flush();

        String result = outputStream.toString();
        assertTrue(result.contains("#alpha"));
        assertTrue(result.contains("#beta"));
    }
@Test
    public void testTopicXMLReportContainsXMLTags() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("xml");

        ParallelTopicModel model = new ParallelTopicModel(1, 1.0, 0.01);
        model.setRandomSeed(123);

        FeatureSequence fs = new FeatureSequence(alphabet);
        fs.add("xml");

        InstanceList list = new InstanceList(alphabet, null);
        list.addThruPipe(new Instance(fs, null, "doc", null));
        model.addInstances(list);

        StringWriter writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        model.topicXMLReport(printWriter, 1);
        printWriter.flush();

        String xml = writer.toString();
        assertTrue(xml.contains("<?xml"));
        assertTrue(xml.contains("<topicModel>"));
        assertTrue(xml.contains("<topic"));
    }
@Test
    public void testPrintDenseDocumentTopicsIncludesAllTopics() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("x");

        ParallelTopicModel model = new ParallelTopicModel(3, 3.0, 0.01);
        model.setRandomSeed(20);

        FeatureSequence fs = new FeatureSequence(alphabet);
        fs.add("x");
        fs.add("x");

        InstanceList list = new InstanceList(alphabet, null);
        list.addThruPipe(new Instance(fs, null, "docz", null));
        model.addInstances(list);

        StringWriter sw = new StringWriter();
        PrintWriter writer = new PrintWriter(sw);
        model.printDenseDocumentTopics(writer);
        writer.flush();

        String out = sw.toString();
        assertTrue(out.contains("docz"));
        assertTrue(out.split("\t").length >= 4); 
    }
@Test
    public void testPrintDocumentTopicsWithNegativeMaxPrintsAllTopics() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("y");

        ParallelTopicModel model = new ParallelTopicModel(4, 4.0, 0.01);
        model.setRandomSeed(100);

        FeatureSequence fs = new FeatureSequence(alphabet);
        fs.add("y");
        fs.add("y");

        InstanceList list = new InstanceList(alphabet, null);
        list.addThruPipe(new Instance(fs, null, "docn", null));
        model.addInstances(list);

        StringWriter sw = new StringWriter();
        PrintWriter writer = new PrintWriter(sw);

        model.printDocumentTopics(writer, 0.0, -1);
        writer.flush();

        String out = sw.toString();
        assertTrue(out.contains("docn"));
        assertTrue(out.contains("\t0\t") || out.contains("\t1\t")); 
    }
@Test
    public void testDisplayTopWordsUsesLocaleFormatter() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("format");

        ParallelTopicModel model = new ParallelTopicModel(2, 2.0, 0.01);
        model.setRandomSeed(30);

        FeatureSequence fs = new FeatureSequence(alphabet);
        fs.add("format");

        InstanceList list = new InstanceList(alphabet, null);
        list.addThruPipe(new Instance(fs, null, "doc", null));
        model.addInstances(list);

        model.formatter = java.text.NumberFormat.getInstance(Locale.GERMANY);

        String output = model.displayTopWords(5, true);
        assertTrue(output.contains("format"));
    }
@Test
    public void testGetTopicDocumentsReturnsAllDocuments() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("zzz");

        ParallelTopicModel model = new ParallelTopicModel(2, 2.0, 0.01);
        model.setRandomSeed(7);

        FeatureSequence fs = new FeatureSequence(alphabet);
        fs.add("zzz");

        InstanceList list = new InstanceList(alphabet, null);
        list.addThruPipe(new Instance(fs, null, "a", null));
        list.addThruPipe(new Instance(fs, null, "b", null));
        list.addThruPipe(new Instance(fs, null, "c", null));

        model.addInstances(list);
        ArrayList<TreeSet<IDSorter>> topicDocs = model.getTopicDocuments(1.0);
        assertEquals(2, topicDocs.size());

        TreeSet<IDSorter> topic0 = topicDocs.get(0);
        assertTrue(topic0.size() >= 1);
    }
@Test
    public void testSetSymmetricAlphaTrueThenFalse() {
        ParallelTopicModel model = new ParallelTopicModel(3, 3.0, 0.01);
        model.setSymmetricAlpha(true);
        assertTrue(model.usingSymmetricAlpha);
        model.setSymmetricAlpha(false);
        assertFalse(model.usingSymmetricAlpha);
    }
@Test
    public void testWriteToFileAndReadDoesNotThrow() throws Exception {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("save");

        ParallelTopicModel model = new ParallelTopicModel(2, 2.0, 0.01);
        model.setRandomSeed(55);

        FeatureSequence sequence = new FeatureSequence(alphabet);
        sequence.add("save");

        InstanceList instances = new InstanceList(alphabet, null);
        instances.addThruPipe(new Instance(sequence, null, "doc", null));

        model.addInstances(instances);

        File tempFile = File.createTempFile("lda-model", ".ser");
        try {
            model.write(tempFile);
            ParallelTopicModel loaded = ParallelTopicModel.read(tempFile);
            assertNotNull(loaded);
            assertEquals(2, loaded.getNumTopics());
        } finally {
            tempFile.delete();
        }
    } 
}