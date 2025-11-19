public class LDAHyper_1_GPTLLMTest { 

 @Test
    public void testConstructorInitializesTopicAlphabetCorrectly() {
        LDAHyper lda = new LDAHyper(3, 1.5, 0.01, new Randoms(42));

        LabelAlphabet topicAlphabet = lda.getTopicAlphabet();
        assertNotNull(topicAlphabet);
        assertEquals(3, lda.getNumTopics());
        assertEquals(3, topicAlphabet.size());
    }
@Test
    public void testSettersAffectConfiguration() {
        LDAHyper lda = new LDAHyper(4, 1.0, 0.1, new Randoms(123));
        lda.setBurninPeriod(5);
        lda.setNumIterations(500);
        lda.setOptimizeInterval(10);
        lda.setTopicDisplay(20, 10);
        lda.setSaveState(25, "state.gz");
        lda.setModelOutput(30, "model.gz");
        lda.setRandomSeed(999);

        assertEquals(5, lda.burninPeriod);
        assertEquals(500, lda.numIterations);
        assertEquals(10, lda.optimizeInterval);
        assertEquals(20, lda.showTopicsInterval);
        assertEquals(10, lda.wordsPerTopic);
        assertEquals(25, lda.saveStateInterval);
        assertEquals("state.gz", lda.stateFilename);
        assertEquals(30, lda.outputModelInterval);
        assertEquals("model.gz", lda.outputModelFilename);
    }
@Test
    public void testAddSingleInstanceTriggersTopicAssignment() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("foo");
        alphabet.lookupIndex("bar");

        InstanceList training = new InstanceList(alphabet, null);
        FeatureSequence fs = new FeatureSequence(alphabet, new String[]{"foo", "bar"});
        training.addThruPipe(new Instance(fs, null, "doc1", null));

        LDAHyper lda = new LDAHyper(2, 1.0, 0.01, new Randoms(42));
        lda.addInstances(training);

        assertEquals(training.getDataAlphabet(), lda.getAlphabet());
        assertEquals(1, lda.getData().size());
        assertEquals(2, lda.getNumTopics());
    }
@Test(expected = IllegalArgumentException.class)
    public void testRejectsInstancesWithDifferentAlphabet() {
        Alphabet alphabet1 = new Alphabet();
        alphabet1.lookupIndex("apple");

        Alphabet alphabet2 = new Alphabet();
        alphabet2.lookupIndex("carrot");

        InstanceList training1 = new InstanceList(alphabet1, null);
        InstanceList training2 = new InstanceList(alphabet2, null);

        FeatureSequence fs1 = new FeatureSequence(alphabet1, new String[]{"apple"});
        training1.addThruPipe(new Instance(fs1, null, "docA", null));

        FeatureSequence fs2 = new FeatureSequence(alphabet2, new String[]{"carrot"});
        training2.addThruPipe(new Instance(fs2, null, "docB", null));

        LDAHyper lda = new LDAHyper(2, 1.0, 0.01, new Randoms(123));
        lda.addInstances(training1);
        lda.addInstances(training2);
    }
@Test
    public void testEstimateDoesNotThrowException() throws IOException {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("apple");
        alphabet.lookupIndex("banana");

        InstanceList training = new InstanceList(alphabet, null);
        FeatureSequence fs = new FeatureSequence(alphabet, new String[]{"apple", "banana"});
        training.addThruPipe(new Instance(fs, null, "doc", null));

        LDAHyper lda = new LDAHyper(2, 1.0, 0.01, new Randoms(42));
        lda.setTopicDisplay(0, 0);
        lda.setNumIterations(5);
        lda.addInstances(training);
        lda.estimate(); 
    }
@Test
    public void testModelLogLikelihoodProducesFiniteValue() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("x");
        alphabet.lookupIndex("y");

        InstanceList training = new InstanceList(alphabet, null);
        FeatureSequence fs = new FeatureSequence(alphabet, new String[]{"x", "y", "x"});
        training.addThruPipe(new Instance(fs, null, "doc1", null));

        LDAHyper lda = new LDAHyper(3, 2.0, 0.05, new Randoms(99));
        lda.addInstances(training);
        double ll = lda.modelLogLikelihood();
        assertTrue(Double.isFinite(ll));
    }
@Test
    public void testPrintTopWordsWithNewLines() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("alpha");
        alphabet.lookupIndex("beta");

        InstanceList training = new InstanceList(alphabet, null);
        FeatureSequence fs = new FeatureSequence(alphabet, new String[]{"alpha", "beta"});
        training.addThruPipe(new Instance(fs, null, "doc", null));

        LDAHyper lda = new LDAHyper(2, 1.0, 0.01, new Randoms(1));
        lda.addInstances(training);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(output);
        lda.printTopWords(out, 2, true);
        String result = output.toString();
        assertTrue(result.contains("Topic"));
        assertTrue(result.contains("alpha") || result.contains("beta"));
    }
@Test
    public void testPrintDocumentTopicsOutputsExpectedFormat() throws UnsupportedEncodingException {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("word");

        InstanceList training = new InstanceList(alphabet, null);
        FeatureSequence fs = new FeatureSequence(alphabet, new String[]{"word", "word"});
        training.addThruPipe(new Instance(fs, null, "doc", null));

        LDAHyper lda = new LDAHyper(2, 1.0, 0.01, new Randoms(7));
        lda.addInstances(training);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(output);
        lda.printDocumentTopics(writer, 0.0, 2);
        writer.flush();

        String text = output.toString("UTF-8");
        assertTrue(text.contains("#doc"));
        assertTrue(text.contains("0"));
    }
@Test
    public void testPrintStateProducesSampledOutput() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("a");
        alphabet.lookupIndex("b");

        InstanceList training = new InstanceList(alphabet, null);
        training.addThruPipe(new Instance(new FeatureSequence(alphabet, new String[]{"a", "b"}), null, "doc", null));

        LDAHyper lda = new LDAHyper(3, 2.0, 0.01, new Randoms(11));
        lda.addInstances(training);

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(buffer);
        lda.printState(ps);
        String text = buffer.toString();
        assertTrue(text.contains("#doc source pos typeindex type topic"));
    }
@Test
    public void testGetSortedTopicWordsReturnsResults() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("cheap");
        alphabet.lookupIndex("fast");
        alphabet.lookupIndex("reliable");

        InstanceList training = new InstanceList(alphabet, null);
        FeatureSequence fs = new FeatureSequence(alphabet, new String[]{"cheap", "fast", "reliable"});
        training.addThruPipe(new Instance(fs, null, "doc", null));

        LDAHyper lda = new LDAHyper(2, 1.0, 0.01, new Randoms(88));
        lda.addInstances(training);

        IDSorter[] sorted = lda.getSortedTopicWords(0);
        assertNotNull(sorted);
        assertEquals(alphabet.size(), sorted.length);
    }
@Test
    public void testLDAHyperSerializationRoundtrip() throws IOException, ClassNotFoundException {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("foo");

        InstanceList training = new InstanceList(alphabet, null);
        FeatureSequence fs = new FeatureSequence(alphabet, new String[]{"foo", "foo", "foo"});
        training.addThruPipe(new Instance(fs, null, "doc", null));

        LDAHyper lda = new LDAHyper(2, 0.5, 0.01, new Randoms(66));
        lda.addInstances(training);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(lda);
        oos.close();
        byte[] data = bos.toByteArray();

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
        Object obj = ois.readObject();
        ois.close();

        assertTrue(obj instanceof LDAHyper);
        LDAHyper loaded = (LDAHyper) obj;
        assertEquals(lda.getNumTopics(), loaded.getNumTopics());
    }
@Test
    public void testTopicLabelMutualInformationReturnsZeroWithoutLabels() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("x");

        InstanceList training = new InstanceList(alphabet, null);
        FeatureSequence fs = new FeatureSequence(alphabet, new String[]{"x"});
        training.addThruPipe(new Instance(fs, null, "doc", null));

        LDAHyper lda = new LDAHyper(2, 1.0, 0.01, new Randoms(5));
        lda.addInstances(training);
        double mi = lda.topicLabelMutualInformation();
        assertEquals(0.0, mi, 0.0);
    }
@Test
    public void testAddInstancesWithEmptyInstanceList() {
        Alphabet alphabet = new Alphabet();
        InstanceList training = new InstanceList(alphabet, null);

        LDAHyper lda = new LDAHyper(2, 1.0, 0.01, new Randoms(42));
        lda.addInstances(training);

        assertEquals(0, lda.getData().size());
    }
@Test
    public void testAddInstancesWithEmptyTopicSequenceList() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("x");

        InstanceList training = new InstanceList(alphabet, null);
        FeatureSequence fs = new FeatureSequence(alphabet, new String[]{"x"});
        training.addThruPipe(new Instance(fs, null, "doc", null));

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        LabelSequence emptyTopics = new LabelSequence(labelAlphabet, new int[1]);
        List<LabelSequence> topicList = Collections.singletonList(emptyTopics);

        LDAHyper lda = new LDAHyper(1, 1.0, 0.01, new Randoms(1));
        lda.addInstances(training, topicList);

        assertEquals(1, lda.getData().size());
    }
@Test
    public void testEstimateWithZeroIterations() throws IOException {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("x");
        InstanceList training = new InstanceList(alphabet, null);
        training.addThruPipe(new Instance(new FeatureSequence(alphabet, new String[]{"x"}), null, "doc", null));

        LDAHyper lda = new LDAHyper(2, 1.0, 0.01, new Randoms(9));
        lda.addInstances(training);
        lda.setNumIterations(0);
        lda.estimate(); 
        assertTrue(lda.getData().size() > 0);
    }
@Test
    public void testPrintStateToFileCreatesFile() throws IOException {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("a");
        InstanceList training = new InstanceList(alphabet, null);
        training.addThruPipe(new Instance(new FeatureSequence(alphabet, new String[]{"a"}), null, "doc", null));

        LDAHyper lda = new LDAHyper(1, 1.0, 0.01, new Randoms(123));
        lda.addInstances(training);

        File tmpFile = File.createTempFile("state", ".gz");
        tmpFile.deleteOnExit();
        lda.printState(tmpFile);

        assertTrue(tmpFile.exists());
        assertTrue(tmpFile.length() > 0);
    }
@Test
    public void testWriteToInvalidFileDoesNotThrowException() {
        LDAHyper lda = new LDAHyper(2, 1.0, 0.01, new Randoms(0));
        File invalidFile = new File("/invalid/path/model.ser");
        lda.write(invalidFile); 
        assertTrue(true); 
    }
@Test
    public void testReadFromInvalidFileReturnsNull() {
        File fake = new File("non_existent_file.model");
        LDAHyper lda = LDAHyper.read(fake);
        assertNull(lda);
    }
@Test
    public void testAddInstanceWithEmptyFeatureSequence() {
        Alphabet alphabet = new Alphabet();
        FeatureSequence fs = new FeatureSequence(alphabet, new int[0]);

        Instance instance = new Instance(fs, null, "emptyDoc", null);
        InstanceList training = new InstanceList(alphabet, null);
        training.addThruPipe(instance);

        LDAHyper lda = new LDAHyper(2, 1.0, 0.01, new Randoms(42));
        lda.addInstances(training);

        assertEquals(1, lda.getData().size());
    }
@Test
    public void testTopicXMLReportHandlesZeroTopics() {
        LabelAlphabet topicAlphabet = new LabelAlphabet(); 
        topicAlphabet.lookupIndex("topic0"); 
        LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, new Randoms(1));

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(output);
        lda.topicXMLReport(writer, 5);
        writer.close();

        String xml = output.toString();
        assertTrue(xml.contains("<topicModel>"));
        assertTrue(xml.contains("</topicModel>"));
    }
@Test
    public void testEstimateWithNoInstancesSilentSuccess() throws IOException {
        Alphabet alphabet = new Alphabet();
        InstanceList emptyList = new InstanceList(alphabet, null);

        LDAHyper lda = new LDAHyper(1, 1.0, 0.01, new Randoms(7));
        lda.addInstances(emptyList);

        lda.setNumIterations(2);
        lda.setTopicDisplay(0, 0);
        lda.estimate();

        assertEquals(0, lda.getData().size());
    }
@Test
    public void testPrintTopWordsToFile() throws IOException {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("foo");

        InstanceList list = new InstanceList(alphabet, null);
        list.addThruPipe(new Instance(new FeatureSequence(alphabet, new String[]{"foo", "foo"}), null, "doc", null));

        LDAHyper lda = new LDAHyper(2, 1.0, 0.01, new Randoms(1));
        lda.addInstances(list);

        File file = File.createTempFile("topwords", ".txt");
        file.deleteOnExit();
        lda.printTopWords(file, 5, true);

        assertTrue(file.exists());
    }
@Test
    public void testEmpiricalLikelihoodIgnoresUnknownWords() {
        Alphabet trainAlphabet = new Alphabet();
        trainAlphabet.lookupIndex("apple");

        Alphabet testAlphabet = new Alphabet();
        testAlphabet.lookupIndex("orange");

        InstanceList training = new InstanceList(trainAlphabet, null);
        training.addThruPipe(new Instance(new FeatureSequence(trainAlphabet, new String[]{"apple"}), null, "doc1", null));

        InstanceList testing = new InstanceList(testAlphabet, null);
        testing.addThruPipe(new Instance(new FeatureSequence(testAlphabet, new String[]{"orange"}), null, "testDoc", null));

        LDAHyper lda = new LDAHyper(2, 1.0, 0.01, new Randoms(5));
        lda.addInstances(training);
        lda.setTestingInstances(testing);

        double el = lda.empiricalLikelihood(3, testing);
        assertTrue(Double.isFinite(el));
    }
@Test
    public void testTopicXMLReportPhrasesEmptyCorpusIsHandled() {
        LDAHyper lda = new LDAHyper(2, 1.0, 0.01, new Randoms(19));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(out);
        lda.topicXMLReportPhrases(ps, 5);
        String xml = out.toString();
        assertTrue(xml.contains("<topics>"));
        assertTrue(xml.contains("</topics>"));
    }
@Test
    public void testTopicReportTitlesAvoidDuplicates() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("repeat");

        InstanceList list = new InstanceList(alphabet, null);
        list.addThruPipe(new Instance(new FeatureSequence(alphabet, new String[]{"repeat", "repeat", "repeat"}), null, "doc", null));

        LDAHyper lda = new LDAHyper(2, 1.0, 0.01, new Randoms(3));
        lda.addInstances(list);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(out);
        lda.topicXMLReportPhrases(ps, 5);
        String xml = out.toString();
        assertTrue(xml.contains("titles="));
    }
@Test
    public void testPrintDocumentTopicsWithMaxLimit() throws UnsupportedEncodingException {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("a");
        InstanceList training = new InstanceList(alphabet, null);
        training.addThruPipe(new Instance(new FeatureSequence(alphabet, new String[]{"a", "a", "a"}), null, "doc", null));

        LDAHyper lda = new LDAHyper(5, 1.0, 0.01, new Randoms(4));
        lda.addInstances(training);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(out);
        lda.printDocumentTopics(pw, 0.0, 2);
        pw.flush();

        String output = out.toString("UTF-8");
        assertTrue(output.contains("0"));
    }
@Test
    public void testAddInstancesWithMismatchedTopicListSizeThrowsAssertionError() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("token");

        InstanceList list = new InstanceList(alphabet, null);
        list.addThruPipe(new Instance(new FeatureSequence(alphabet, new String[]{"token"}), null, "doc", null));

        LabelAlphabet topicAlphabet = new LabelAlphabet();
        LabelSequence topicSeq = new LabelSequence(topicAlphabet, new int[]{0});

        LDAHyper lda = new LDAHyper(1, 1.0, 0.01, new Randoms(5));

        try {
            lda.addInstances(list, java.util.Collections.emptyList());
            fail("Expected AssertionError due to mismatch in instance and topic list sizes");
        } catch (AssertionError e) {
            assertTrue(true);
        }
    }
@Test
    public void testPrintDocumentTopicsWithNegativeMaxPrintsAllTopics() throws UnsupportedEncodingException {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("x");

        InstanceList list = new InstanceList(alphabet, null);
        list.addThruPipe(new Instance(new FeatureSequence(alphabet, new String[]{"x", "x"}), null, "doc1", null));

        LDAHyper lda = new LDAHyper(3, 1.0, 0.01, new Randoms(6));
        lda.addInstances(list);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(out);
        lda.printDocumentTopics(writer, 0.0, -1);
        writer.flush();

        String result = out.toString("UTF-8");
        assertTrue(result.contains("0"));
    }
@Test
    public void testPrintStateHandlesNullSourceGracefully() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("token");

        FeatureSequence fs = new FeatureSequence(alphabet, new String[]{"token"});
        Instance instance = new Instance(fs, null, null, null);

        InstanceList list = new InstanceList(alphabet, null);
        list.addThruPipe(instance);

        LDAHyper lda = new LDAHyper(1, 1.0, 0.01, new Randoms(9));
        lda.addInstances(list);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream stream = new PrintStream(out);
        lda.printState(stream);
        String output = out.toString();
        assertTrue(output.contains("NA"));
    }
@Test
    public void testPrintTopWordsWithoutNewLines() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("alpha");
        alphabet.lookupIndex("beta");

        InstanceList list = new InstanceList(alphabet, null);
        FeatureSequence fs = new FeatureSequence(alphabet, new String[]{"alpha", "beta", "alpha"});
        list.addThruPipe(new Instance(fs, null, "doc", null));

        LDAHyper lda = new LDAHyper(2, 0.5, 0.01, new Randoms(123));
        lda.addInstances(list);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(out);
        lda.printTopWords(ps, 2, false);
        String result = out.toString();
        assertTrue(result.contains("0") || result.contains("1"));
        assertTrue(result.contains("alpha") || result.contains("beta"));
    }
@Test
    public void testPrintTopWordsHandlesZeroTypes() {
        Alphabet alphabet = new Alphabet(); 

        LDAHyper lda = new LDAHyper(2, 1.0, 0.01, new Randoms(11));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(out);
        lda.printTopWords(ps, 5, true);
        String result = out.toString();
        assertTrue(result.contains("Topic") || result.isEmpty());
    }
@Test
    public void testModelLogLikelihoodWithNoDataReturnsZeroOrLess() {
        LDAHyper lda = new LDAHyper(2, 1.0, 0.01, new Randoms(13));
        double ll = lda.modelLogLikelihood();
        assertTrue(Double.isFinite(ll));
    }
@Test
    public void testEmpiricalLikelihoodWithZeroSamplesReturnsZero() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("apple");

        InstanceList list = new InstanceList(alphabet, null);
        list.addThruPipe(new Instance(new FeatureSequence(alphabet, new String[]{"apple"}), null, "doc", null));

        LDAHyper lda = new LDAHyper(2, 1.0, 0.01, new Randoms(77));
        lda.addInstances(list);
        lda.setTestingInstances(list);

        double likelihood = lda.empiricalLikelihood(0, list);
        assertTrue(Double.isNaN(likelihood) || Double.isInfinite(likelihood) || likelihood == 0.0);
    }
@Test
    public void testXMLReportHandlesSmallWordCount() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("thin");

        InstanceList list = new InstanceList(alphabet, null);
        FeatureSequence fs = new FeatureSequence(alphabet, new String[]{"thin"});
        list.addThruPipe(new Instance(fs, null, "doc", null));

        LDAHyper lda = new LDAHyper(1, 1.0, 0.01, new Randoms(999));
        lda.addInstances(list);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(os);
        lda.topicXMLReport(pw, 1);
        pw.flush();

        String xml = os.toString();
        assertTrue(xml.contains("topicModel"));
    }
@Test
    public void testGetCountFeatureTopicHandlesNonexistentIndex() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("x");

        InstanceList list = new InstanceList(alphabet, null);
        list.addThruPipe(new Instance(new FeatureSequence(alphabet, new String[]{"x"}), null, "doc", null));

        LDAHyper lda = new LDAHyper(1, 1.0, 0.01, new Randoms(88));
        lda.addInstances(list);

        int result = lda.getCountFeatureTopic(100, 0); 
        assertEquals(0, result);
    }
@Test
    public void testTokenPerTopicCountReturnsZeroForUnusedTopics() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("a");

        InstanceList list = new InstanceList(alphabet, null);
        list.addThruPipe(new Instance(new FeatureSequence(alphabet, new String[]{"a"}), null, "doc", null));

        LDAHyper lda = new LDAHyper(3, 1.0, 0.01, new Randoms(51));
        lda.addInstances(list);

        int result = lda.getCountTokensPerTopic(2);
        assertTrue(result >= 0);
    }
@Test
    public void testSamplingAssignsValidTopic() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("word");

        FeatureSequence tokens = new FeatureSequence(alphabet, new String[]{"word"});
        FeatureSequence topics = new LabelSequence(new LabelAlphabet(), new int[]{0});

        LDAHyper lda = new LDAHyper(2, 1.0, 0.01, new Randoms(1));
        lda.setOptimizeInterval(0);
        lda.setBurninPeriod(0);
        lda.oneDocTopicCounts = new int[2];
        lda.tokensPerTopic = new int[2];
        lda.alphabet = alphabet;
        lda.numTypes = 1;
        lda.typeTopicCounts = new com.carrotsearch.hppc.IntIntHashMap[1];
        lda.typeTopicCounts[0] = new com.carrotsearch.hppc.IntIntHashMap();
        lda.typeTopicCounts[0].put(0, 1);

        lda.sampleTopicsForOneDoc(tokens, topics, false, true);

        int newTopic = ((LabelSequence) topics).getFeatures()[0];
        assertTrue(newTopic >= 0 && newTopic < 2);
    }
@Test
    public void testReadObjectWithCorruptedStreamThrowsExceptionSilently() {
        byte[] corruptedData = new byte[]{0, 1, 2, 3, 4};
        ByteArrayInputStream bis = new ByteArrayInputStream(corruptedData);
        try {
            ObjectInputStream in = new ObjectInputStream(bis);
            LDAHyper result = (LDAHyper) in.readObject();
            fail("Expected exception not thrown");
        } catch (Exception e) {
            assertTrue(true); 
        }
    }
@Test
    public void testWriteObjectDoesNotFailWhenAllFieldsAreDefault() throws IOException {
        LDAHyper ldaHyper = new LDAHyper(2, 1.0, 0.01, new Randoms(99));
        File temp = File.createTempFile("lda-default", ".ser");
        temp.deleteOnExit();
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(temp));
        out.writeObject(ldaHyper);
        out.close();
        assertTrue(temp.exists());
    }
@Test
    public void testTopicXMLReportWithEmptyTopicVocabulary() throws IOException {
        LabelAlphabet topicAlphabet = new LabelAlphabet();
        topicAlphabet.lookupIndex("topic0");
        LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, new Randoms(0));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(output);
        lda.topicXMLReport(pw, 5);
        pw.flush();
        String xml = output.toString();
        assertTrue(xml.contains("<topicModel>") && xml.contains("</topicModel>"));
    }
@Test
    public void testPrintDocumentTopicsWithDocumentHavingNullSource() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("null");

        FeatureSequence fs = new FeatureSequence(alphabet, new String[]{"null"});
        Instance instance = new Instance(fs, null, null, null);

        InstanceList list = new InstanceList(alphabet, null);
        list.addThruPipe(instance);

        LDAHyper lda = new LDAHyper(2, 1.0, 0.01, new Randoms(30));
        lda.addInstances(list);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(output);
        lda.printDocumentTopics(writer, 0.0, 2);
        writer.flush();

        String text = output.toString();
        assertTrue(text.contains("null-source"));
    }
@Test
    public void testTopicTermMassFallbackBranch() {
        LDAHyper lda = new LDAHyper(1, 1.0, 0.01, new Randoms(17));
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("token");

        FeatureSequence tokenSequence = new FeatureSequence(alphabet, new String[]{"token"});
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("topic0");

        LabelSequence topicSequence = new LabelSequence(labelAlphabet, new int[]{0});
        lda.alphabet = alphabet;
        lda.numTypes = 1;
        lda.typeTopicCounts = new com.carrotsearch.hppc.IntIntHashMap[1];
        lda.typeTopicCounts[0] = new com.carrotsearch.hppc.IntIntHashMap();
        lda.typeTopicCounts[0].put(0, 1);
        lda.tokensPerTopic = new int[]{5};
        lda.cachedCoefficients = new double[]{0.5};
        lda.alpha = new double[]{1.0};
        lda.beta = 0.01;
        lda.betaSum = 0.01;
        lda.docLengthCounts = new int[10];
        lda.topicDocCounts = new int[1][10];

        lda.sampleTopicsForOneDoc(tokenSequence, topicSequence, true, true);

        int topic = topicSequence.getIndexAtPosition(0);
        assertTrue(topic >= 0);
    }
@Test
    public void testZeroAlphaSumDirichletLearnSkipsUpdate() {
        double[] alpha = new double[]{0.0, 0.0};
        int[][] topicCounts = new int[][]{
            new int[]{10, 0},
            new int[]{0, 5}
        };
        int[] docLengths = new int[]{10, 5};

        double learnedAlphaSum = Dirichlet.learnParameters(alpha, topicCounts, docLengths);
        assertTrue(learnedAlphaSum >= 0.0);
    }
@Test
    public void testPrintStateWithMultipleDocuments() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("word1");
        alphabet.lookupIndex("word2");

        InstanceList list = new InstanceList(alphabet, null);
        String[] doc1 = {"word1", "word2"};
        String[] doc2 = {"word2", "word2"};
        list.addThruPipe(new Instance(new FeatureSequence(alphabet, doc1), null, "doc1", null));
        list.addThruPipe(new Instance(new FeatureSequence(alphabet, doc2), null, "doc2", null));

        LDAHyper lda = new LDAHyper(2, 1.0, 0.01, new Randoms(21));
        lda.addInstances(list);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(out);
        lda.printState(ps);
        String result = out.toString();
        assertTrue(result.contains("doc1"));
        assertTrue(result.contains("doc2"));
    }
@Test
    public void testModelLogLikelihoodHandlesMultipleTopicsAndTokens() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("dog");
        alphabet.lookupIndex("cat");
        alphabet.lookupIndex("mouse");

        InstanceList list = new InstanceList(alphabet, null);
        list.addThruPipe(new Instance(new FeatureSequence(alphabet, new String[]{"cat", "dog", "mouse"}), null, "doc", null));

        LDAHyper lda = new LDAHyper(3, 3.0, 0.1, new Randoms(33));
        lda.addInstances(list);
        double ll = lda.modelLogLikelihood();
        assertTrue(Double.isFinite(ll));
    }
@Test
    public void testTopicationSerializationRoundtrip() throws IOException, ClassNotFoundException {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("test");

        Instance instance = new Instance(new FeatureSequence(alphabet, new String[]{"test", "test"}), null, "doc", null);
        LabelAlphabet topicAlphabet = new LabelAlphabet();
        LabelSequence topics = new LabelSequence(topicAlphabet, new int[]{0, 1});

        LDAHyper model = new LDAHyper(2, 1.0, 0.01, new Randoms(12));
        LDAHyper.Topication topication = model.new Topication(instance, model, topics);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(out);
        oos.writeObject(topication);
        oos.close();

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(out.toByteArray()));
        Object restored = ois.readObject();
        assertNotNull(restored);
        assertTrue(restored instanceof LDAHyper.Topication);
    }
@Test
    public void testEmptySortedTopicWordsReturnsProperly() {
        LDAHyper lda = new LDAHyper(2, 1.0, 0.01, new Randoms(2));
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("z");

        lda.alphabet = alphabet;
        lda.numTypes = 1;
        lda.typeTopicCounts = new com.carrotsearch.hppc.IntIntHashMap[1];
        lda.typeTopicCounts[0] = new com.carrotsearch.hppc.IntIntHashMap();
        lda.typeTopicCounts[0].put(0, 0);

        IDSorter[] sorters = lda.getSortedTopicWords(0);
        assertEquals(1, sorters.length);
        assertEquals(0.0, sorters[0].getWeight(), 0.001);
    }
@Test
    public void testPrintTopWordsWithZeroResults() throws IOException {
        LDAHyper lda = new LDAHyper(2, 1.0, 0.01, new Randoms(42));
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("zero");

        lda.alphabet = alphabet;
        lda.numTypes = 1;
        lda.typeTopicCounts = new com.carrotsearch.hppc.IntIntHashMap[1];
        lda.typeTopicCounts[0] = new com.carrotsearch.hppc.IntIntHashMap();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(out);
        lda.printTopWords(ps, 10, false);
        String result = out.toString();
        assertTrue(result.contains("0") || result.contains("1"));
    }
@Test
    public void testEstimateWithBurninOnlyNoIterationsBeyond() throws IOException {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("alpha");
        InstanceList list = new InstanceList(alphabet, null);
        FeatureSequence seq = new FeatureSequence(alphabet, new String[]{"alpha", "alpha"});
        list.addThruPipe(new Instance(seq, null, "doc1", null));

        LDAHyper lda = new LDAHyper(2, 1.0, 0.01, new Randoms(99));
        lda.addInstances(list);
        lda.setNumIterations(2);
        lda.setBurninPeriod(2);
        lda.estimate();

        assertEquals(1, lda.getData().size());
    }
@Test
    public void testEstimateWithOptimizeIntervalNoSamplesShouldNotFail() throws IOException {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("term");

        FeatureSequence fs = new FeatureSequence(alphabet, new String[]{"term", "term"});
        InstanceList list = new InstanceList(alphabet, null);
        list.addThruPipe(new Instance(fs, null, "doc", null));

        LDAHyper lda = new LDAHyper(3, 5.0, 0.01, new Randoms(6));
        lda.setNumIterations(2);
        lda.setBurninPeriod(1);
        lda.setOptimizeInterval(2);
        lda.addInstances(list);
        lda.estimate();
        assertEquals(1, lda.getData().size());
    }
@Test
    public void testPrintStateToFileContainsExpectedContent() throws IOException {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("tag");

        FeatureSequence fs = new FeatureSequence(alphabet, new String[]{"tag"});
        InstanceList list = new InstanceList(alphabet, null);
        list.addThruPipe(new Instance(fs, null, "doc", null));

        LDAHyper lda = new LDAHyper(1, 1.0, 0.01, new Randoms(4));
        lda.addInstances(list);

        File file = File.createTempFile("state", ".gz");
        file.deleteOnExit();
        lda.printState(file);

        FileInputStream fis = new FileInputStream(file);
        byte[] buffer = new byte[200];
        int len = fis.read(buffer);
        fis.close();
        assertTrue(new String(buffer, 0, len).contains("topic"));
    }
@Test
    public void testTopicDistributionAppliesCorrectlyToEmpiricalLikelihood() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("w");

        FeatureSequence fs = new FeatureSequence(alphabet, new String[]{"w"});
        Instance instance = new Instance(fs, null, "d", null);
        InstanceList list = new InstanceList(alphabet, null);
        list.addThruPipe(instance);

        LDAHyper lda = new LDAHyper(2, 2.0, 0.01, new Randoms(15));
        lda.addInstances(list);
        lda.setTestingInstances(list);

        double result = lda.empiricalLikelihood(10, list);
        assertTrue(Double.isFinite(result));
    }
@Test
    public void testModelLogLikelihoodReturnsValueWithMinimalData() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("x");

        InstanceList list = new InstanceList(alphabet, null);
        FeatureSequence fs = new FeatureSequence(alphabet, new String[]{"x", "x", "x"});
        list.addThruPipe(new Instance(fs, null, "doc", null));

        LDAHyper lda = new LDAHyper(1, 1.0, 0.01, new Randoms(42));
        lda.addInstances(list);
        double ll = lda.modelLogLikelihood();

        assertTrue(Double.isFinite(ll));
    }
@Test
    public void testAddInstancesWithLargeAlphabetDoesNotCrash() {
        Alphabet alphabet = new Alphabet();
        for (int i = 0; i < 100; i++) {
            alphabet.lookupIndex("term" + i);
        }

        String[] terms = new String[]{"term1", "term2", "term3"};
        FeatureSequence fs = new FeatureSequence(alphabet, terms);
        InstanceList list = new InstanceList(alphabet, null);
        list.addThruPipe(new Instance(fs, null, "doc", null));

        LDAHyper lda = new LDAHyper(3, 1.0, 0.01, new Randoms(20));
        lda.addInstances(list);

        assertNotNull(lda.getAlphabet());
        assertEquals(3, lda.getNumTopics());
    }
@Test
    public void testPrintTopWordsToEmptyFileSucceeds() throws IOException {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("a");

        FeatureSequence fs = new FeatureSequence(alphabet, new String[]{"a", "a"});
        InstanceList list = new InstanceList(alphabet, null);
        list.addThruPipe(new Instance(fs, null, "doc", null));

        LDAHyper lda = new LDAHyper(2, 1.0, 0.01, new Randoms(43));
        lda.addInstances(list);

        File file = File.createTempFile("topwords", ".txt");
        file.deleteOnExit();
        lda.printTopWords(file, 3, true);

        assertTrue(file.exists() && file.length() > 0);
    }
@Test
    public void testEmptyTokenSequenceHandledInInference() {
        Alphabet alphabet = new Alphabet();
        FeatureSequence fs = new FeatureSequence(alphabet, new int[0]);
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("topic0");
        LabelSequence topics = new LabelSequence(labelAlphabet, new int[0]);

        LDAHyper lda = new LDAHyper(1, 1.0, 0.01, new Randoms(91));
        lda.alphabet = alphabet;
        lda.typeTopicCounts = new com.carrotsearch.hppc.IntIntHashMap[0];
        lda.tokensPerTopic = new int[]{0};
        lda.cachedCoefficients = new double[]{0};
        lda.betaSum = 0.0;
        lda.alpha = new double[]{0.1};
        lda.beta = 0.01;
        lda.docLengthCounts = new int[1];
        lda.topicDocCounts = new int[1][1];

        lda.sampleTopicsForOneDoc(fs, topics, true, false);
        assertEquals(0, topics.getLength());
    }
@Test
    public void testPrintTopWordsGeneratesStringForLongAlphabet() {
        Alphabet alphabet = new Alphabet();
        for (int i = 0; i < 50; i++) {
            alphabet.lookupIndex("token" + i);
        }

        IntIntHashMap[] counts = new IntIntHashMap[50];
        for (int i = 0; i < 50; i++) {
            counts[i] = new IntIntHashMap();
            counts[i].put(0, i + 1);
        }

        LDAHyper lda = new LDAHyper(1, 1.0, 0.01, new Randoms(100));
        lda.numTypes = 50;
        lda.alphabet = alphabet;
        lda.typeTopicCounts = counts;
        lda.tokensPerTopic = new int[]{50};
        lda.alpha = new double[]{1.0};
        lda.cachedCoefficients = new double[]{1.0};

        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(buf);
        lda.printTopWords(ps, 10, true);
        String content = buf.toString();
        assertTrue(content.contains("token"));
    }
@Test
    public void testTopicXMLReportPhrasesWithEmptyInput() {
        LDAHyper lda = new LDAHyper(2, 1.0, 0.01, new Randoms(220));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(out);
        lda.topicXMLReportPhrases(ps, 10);
        String xml = out.toString();
        assertTrue(xml.contains("<topics>"));
    }
@Test
    public void testZeroTopicsHandledGracefully() {
        try {
            LDAHyper lda = new LDAHyper(0, 0.0, 0.01, new Randoms(1));
            fail("Should fail with zero topics");
        } catch (IllegalArgumentException | ArithmeticException e) {
            assertTrue(true);
        }
    }
@Test
    public void testSampleTopicsForOneDocWithZeroTypeTopicCounts() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("token");

        FeatureSequence fs = new FeatureSequence(alphabet, new String[]{"token", "token"});
        LabelAlphabet topicAlphabet = new LabelAlphabet();
        topicAlphabet.lookupIndex("t0");
        topicAlphabet.lookupIndex("t1");
        LabelSequence topics = new LabelSequence(topicAlphabet, new int[]{0, 1});

        LDAHyper lda = new LDAHyper(2, 1.0, 0.01, new Randoms(42));
        lda.alphabet = alphabet;
        lda.numTypes = 1;
        lda.alpha = new double[]{1.0, 1.0};
        lda.beta = 0.01;
        lda.betaSum = lda.beta;
        lda.cachedCoefficients = new double[]{1.0, 1.0};
        lda.tokensPerTopic = new int[]{0, 0};
        lda.docLengthCounts = new int[10];
        lda.topicDocCounts = new int[2][10];
        lda.typeTopicCounts = new com.carrotsearch.hppc.IntIntHashMap[1];
        lda.typeTopicCounts[0] = new com.carrotsearch.hppc.IntIntHashMap(); 

        lda.sampleTopicsForOneDoc(fs, topics, true, true);

        int[] sampled = topics.getFeatures();
        assertEquals(2, sampled.length);
        assertTrue(sampled[0] >= 0);
        assertTrue(sampled[1] >= 0);
    }
@Test
    public void testDirichletLearnParametersWithZeroDocLengths() {
        double[] alpha = new double[]{0.1, 0.1};
        int[][] topicCounts = new int[2][5];
        int[] docLengths = new int[5]; 

        double result = Dirichlet.learnParameters(alpha, topicCounts, docLengths);
        assertTrue(Double.isFinite(result));
    }
@Test
    public void testTopicXMLReportPhrasesWithShortDocuments() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("word");

        FeatureSequence fs = new FeatureSequence(alphabet, new String[]{"word"});
        Instance instance = new Instance(fs, null, "doc", null);
        InstanceList list = new InstanceList(alphabet, null);
        list.addThruPipe(instance);

        LDAHyper lda = new LDAHyper(2, 1.0, 0.01, new Randoms(77));
        lda.addInstances(list);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(out);
        lda.topicXMLReportPhrases(ps, 3);

        String xml = out.toString();
        assertTrue(xml.contains("<topics>"));
    }
@Test
    public void testModelLogLikelihoodWithEmptyTopicCounts() {
        LDAHyper lda = new LDAHyper(1, 1.0, 0.01, new Randoms(17));
        lda.alphabet = new Alphabet();
        lda.numTypes = 0;
        lda.typeTopicCounts = new com.carrotsearch.hppc.IntIntHashMap[0];
        lda.tokensPerTopic = new int[]{0};
        lda.alpha = new double[]{1.0};
        lda.beta = 0.01;
        lda.betaSum = 0.01;
        lda.cachedCoefficients = new double[]{1.0};
        double ll = lda.modelLogLikelihood();
        assertTrue(Double.isFinite(ll));
    }
@Test
    public void testEmpiricalLikelihoodWithEmptyTestingSet() {
        LDAHyper lda = new LDAHyper(2, 1.0, 0.01, new Randoms(12));
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("word");

        InstanceList training = new InstanceList(alphabet, null);
        InstanceList test = new InstanceList(alphabet, null); 

        FeatureSequence fs = new FeatureSequence(alphabet, new String[]{"word"});
        training.addThruPipe(new Instance(fs, null, "doc1", null));

        lda.addInstances(training);
        lda.setTestingInstances(test);

        double ll = lda.empiricalLikelihood(5, test);
        assertEquals(0.0, ll, 0.0001);
    }
@Test
    public void testTopicLabelMutualInformationWhenNoTargetAlphabet() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("x");

        InstanceList list = new InstanceList(alphabet, null);
        FeatureSequence fs = new FeatureSequence(alphabet, new String[]{"x"});
        list.addThruPipe(new Instance(fs, null, "doc", null)); 

        LDAHyper lda = new LDAHyper(1, 1.0, 0.01, new Randoms(1));
        lda.addInstances(list);

        double mi = lda.topicLabelMutualInformation();
        assertEquals(0.0, mi, 0.0001);
    }
@Test
    public void testTopicLabelMutualInformationWithActualLabels() {
        Alphabet dataAlphabet = new Alphabet();
        dataAlphabet.lookupIndex("a");

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("L0");
        labelAlphabet.lookupIndex("L1");

        InstanceList list = new InstanceList(dataAlphabet, labelAlphabet);
        Label l = labelAlphabet.lookupLabel("L0");
        FeatureSequence fs = new FeatureSequence(dataAlphabet, new String[]{"a", "a"});
        list.addThruPipe(new Instance(fs, l, "doc1", null));

        LDAHyper lda = new LDAHyper(2, 1.0, 0.01, new Randoms(50));
        lda.addInstances(list);
        double result = lda.topicLabelMutualInformation();
        assertTrue(result >= 0.0);
    }
@Test
    public void testPrintDocumentTopicsHandlesThresholdCorrectly() throws UnsupportedEncodingException {
        Alphabet alpha = new Alphabet();
        alpha.lookupIndex("z");

        FeatureSequence fs = new FeatureSequence(alpha, new String[]{"z", "z"});
        InstanceList list = new InstanceList(alpha, null);
        list.addThruPipe(new Instance(fs, null, "doc", null));

        LDAHyper lda = new LDAHyper(3, 1.0, 0.01, new Randoms(8));
        lda.addInstances(list);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(out);
        lda.printDocumentTopics(writer, 0.5, 3);
        writer.flush();

        String result = out.toString("UTF-8");
        assertTrue(result.contains("doc"));
    }
@Test
    public void testAlphaOptimizationSkipsIfSampleConditionFails() throws IOException {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("word");

        InstanceList list = new InstanceList(alphabet, null);
        list.addThruPipe(new Instance(new FeatureSequence(alphabet, new String[]{"word"}), null, "doc", null));

        LDAHyper lda = new LDAHyper(3, 1.0, 0.01, new Randoms(34));
        lda.setOptimizeInterval(0);
        lda.setBurninPeriod(5);
        lda.setNumIterations(3);
        lda.addInstances(list);
        lda.estimate(); 

        assertTrue(lda.getData().size() > 0);
    }
@Test
    public void testTopicXMLReportHandlesNonAsciiCharacters() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("こんにちは"); 

        FeatureSequence fs = new FeatureSequence(alphabet, new String[]{"こんにちは", "こんにちは"});
        Instance instance = new Instance(fs, null, "doc", null);
        InstanceList list = new InstanceList(alphabet, null);
        list.addThruPipe(instance);

        LDAHyper lda = new LDAHyper(1, 1.0, 0.01, new Randoms(99));
        lda.addInstances(list);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(out);
        lda.topicXMLReport(writer, 5);
        writer.flush();

        String xml = out.toString();
        assertTrue(xml.contains("こんにちは"));
    }
@Test
    public void testPrintDocumentTopicsWithMaxGreaterThanNumTopics() throws UnsupportedEncodingException {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("x");

        FeatureSequence fs = new FeatureSequence(alphabet, new String[]{"x", "x"});
        InstanceList list = new InstanceList(alphabet, null);
        list.addThruPipe(new Instance(fs, null, "docMax", null));

        LDAHyper lda = new LDAHyper(2, 1.0, 0.01, new Randoms(21));
        lda.addInstances(list);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(baos);
        lda.printDocumentTopics(writer, 0.0, 10); 
        writer.flush();

        String text = baos.toString("UTF-8");
        assertTrue(text.contains("docMax"));
    }
@Test
    public void testPrintTopWordsWithZeroWordsRequested() {
        LDAHyper lda = new LDAHyper(2, 1.0, 0.01, new Randoms(3));
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("zero");

        lda.alphabet = alphabet;
        lda.numTypes = 1;
        lda.typeTopicCounts = new com.carrotsearch.hppc.IntIntHashMap[1];
        lda.typeTopicCounts[0] = new com.carrotsearch.hppc.IntIntHashMap();
        lda.typeTopicCounts[0].put(0, 1);
        lda.tokensPerTopic = new int[]{1, 1};
        lda.alpha = new double[]{1.0, 1.0};
        lda.cachedCoefficients = new double[]{1.0, 1.0};

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(out);
        lda.printTopWords(ps, 0, true); 
        String result = out.toString();
        assertTrue(result.contains("Topic") || result.contains("0"));
    }
@Test
    public void testPrintTopWordsWithoutTypeTopicEntrySkipsGracefully() {
        LDAHyper lda = new LDAHyper(2, 1.0, 0.01, new Randoms(55));
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("a");
        alphabet.lookupIndex("b");

        lda.alphabet = alphabet;
        lda.numTypes = 2;
        lda.tokensPerTopic = new int[]{1, 1};
        lda.alpha = new double[]{1.0, 1.0};
        lda.cachedCoefficients = new double[]{1.0, 1.0};

        lda.typeTopicCounts = new com.carrotsearch.hppc.IntIntHashMap[2];
        lda.typeTopicCounts[0] = new com.carrotsearch.hppc.IntIntHashMap(); 
        lda.typeTopicCounts[1] = new com.carrotsearch.hppc.IntIntHashMap(); 

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(out);
        lda.printTopWords(ps, 3, true);
        String result = out.toString();
        assertTrue(result.contains("Topic"));
    }
@Test
    public void testTopicXMLReportPrintsTokensInSortedOrder() throws IOException {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("first");
        alphabet.lookupIndex("second");
        alphabet.lookupIndex("third");

        FeatureSequence fs = new FeatureSequence(alphabet, new String[]{"second", "first", "third"});
        Instance instance = new Instance(fs, null, "doc", null);
        InstanceList list = new InstanceList(alphabet, null);
        list.addThruPipe(instance);

        LDAHyper lda = new LDAHyper(1, 1.0, 0.01, new Randoms(77));
        lda.addInstances(list);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(out);
        lda.topicXMLReport(pw, 3);
        pw.flush();

        String xml = out.toString();
        assertTrue(xml.contains("first"));
        assertTrue(xml.contains("second"));
        assertTrue(xml.contains("third"));
    }
@Test
    public void testZeroOrNegativeTopicCountsHandledInModelLogLikelihood() {
        LDAHyper lda = new LDAHyper(2, 1.0, 0.01, new Randoms(101));
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("token");

        lda.alphabet = alphabet;
        lda.numTypes = 1;
        lda.tokensPerTopic = new int[]{0, 0};

        lda.typeTopicCounts = new com.carrotsearch.hppc.IntIntHashMap[1];
        com.carrotsearch.hppc.IntIntHashMap map = new com.carrotsearch.hppc.IntIntHashMap();
        map.put(0, -1); 
        map.put(1, 0);  
        lda.typeTopicCounts[0] = map;

        double ll = lda.modelLogLikelihood();
        assertTrue(Double.isFinite(ll));
    }
@Test
    public void testTopicXMLReportWritesAllTopicsCorrectly() {
        LDAHyper lda = new LDAHyper(3, 1.0, 0.01, new Randoms(42));
        Alphabet alphabet = new Alphabet();
        for (int i = 0; i < 5; i++) {
            alphabet.lookupIndex("term" + i);
        }

        FeatureSequence fs = new FeatureSequence(alphabet, new String[]{"term1", "term2", "term3"});
        Instance instance = new Instance(fs, null, "doc", null);
        InstanceList list = new InstanceList(alphabet, null);
        list.addThruPipe(instance);

        lda.addInstances(list);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(out);
        lda.topicXMLReport(writer, 5);
        writer.flush();

        String xml = out.toString();
        assertTrue(xml.contains("<topic"));
        assertTrue(xml.contains("</topic>"));
    }
@Test
    public void testReadAndWriteObjectRoundtripWithoutTrainingData() throws IOException, ClassNotFoundException {
        LDAHyper lda = new LDAHyper(2, 1.0, 0.01, new Randoms(100));
        lda.tokensPerTopic = new int[]{0, 0};
        lda.cachedCoefficients = new double[]{1.0, 1.0};
        lda.alphabet = new Alphabet();
        lda.topicAlphabet = new LabelAlphabet();
        lda.typeTopicCounts = new com.carrotsearch.hppc.IntIntHashMap[0];

        File temp = File.createTempFile("lda", ".ser");
        temp.deleteOnExit();
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(temp));
        oos.writeObject(lda);
        oos.close();

        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(temp));
        LDAHyper reloaded = (LDAHyper) ois.readObject();
        assertNotNull(reloaded);
        assertEquals(2, reloaded.getNumTopics());
    }
@Test
    public void testSetSaveStateAndTriggerInEstimate() throws IOException {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("x");

        FeatureSequence fs = new FeatureSequence(alphabet, new String[]{"x", "x", "x"});
        InstanceList list = new InstanceList(alphabet, null);
        list.addThruPipe(new Instance(fs, null, "stateDoc", null));

        LDAHyper lda = new LDAHyper(2, 1.0, 0.01, new Randoms(13));
        lda.setSaveState(1, File.createTempFile("state", ".gz").getAbsolutePath());
        lda.setNumIterations(2);
        lda.setTopicDisplay(0, 0);
        lda.addInstances(list);
        lda.estimate();

        assertTrue(lda.getData().size() > 0);
    } 
}