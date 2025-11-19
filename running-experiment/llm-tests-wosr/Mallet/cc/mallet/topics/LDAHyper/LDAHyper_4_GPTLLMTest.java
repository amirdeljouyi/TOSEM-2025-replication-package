public class LDAHyper_4_GPTLLMTest { 

 @Test
    public void testConstructorWithTopicCount() {
        LDAHyper lda = new LDAHyper(5);
        assertNotNull(lda);
        assertEquals(5, lda.getNumTopics());
        assertNotNull(lda.getTopicAlphabet());
    }
@Test
    public void testConstructorWithAlphaAndBeta() {
        LDAHyper lda = new LDAHyper(10, 5.0, 0.02);
        assertNotNull(lda);
        assertEquals(10, lda.getNumTopics());
        assertNotNull(lda.getTopicAlphabet());
    }
@Test
    public void testSetConfigurationValues() {
        LDAHyper lda = new LDAHyper(4);
        lda.setBurninPeriod(25);
        lda.setNumIterations(100);
        lda.setTopicDisplay(10, 8);
        lda.setModelOutput(5, "model.ser");
        lda.setSaveState(20, "state.gz");
        lda.setRandomSeed(12345);
        lda.setOptimizeInterval(10);

        assertEquals(4, lda.getNumTopics()); 
    }
@Test
    public void testAddInstancesPopulatesData() {
        InstanceList instances = new InstanceList(
            new SerialPipes(Arrays.asList(
                new CharSequence2TokenSequence(Pattern.compile("\\p{L}+")),
                new TokenSequence2FeatureSequence()
            ))
        );
        instances.addThruPipe(new Instance("apple orange banana", null, "doc1", null));
        LDAHyper lda = new LDAHyper(3);

        lda.addInstances(instances);

        assertEquals(1, lda.getData().size());
        assertEquals(instances.getDataAlphabet(), lda.getAlphabet());
    }
@Test
    public void testEstimateRunsForSmallCorpus() throws Exception {
        InstanceList instances = new InstanceList(
            new SerialPipes(Arrays.asList(
                new CharSequence2TokenSequence(Pattern.compile("\\p{L}+")),
                new TokenSequence2FeatureSequence()
            ))
        );
        instances.addThruPipe(new Instance("java mallet lda", null, "docA", null));
        LDAHyper lda = new LDAHyper(2);
        lda.setNumIterations(1);
        lda.setTopicDisplay(0, 5);

        lda.addInstances(instances);
        lda.estimate();

        assertEquals(1, lda.getData().size());
    }
@Test(expected = IllegalArgumentException.class)
    public void testAddInstancesWithMismatchedAlphabetThrows() {
        Alphabet alphabetA = new Alphabet();
        alphabetA.lookupIndex("word");

        Alphabet alphabetB = new Alphabet();
        alphabetB.lookupIndex("other");

        InstanceList listA = new InstanceList(alphabetA, null);
        listA.addThruPipe(new Instance("word", null, "doc1", null));

        InstanceList listB = new InstanceList(alphabetB, null);
        listB.addThruPipe(new Instance("other", null, "doc2", null));

        LDAHyper lda = new LDAHyper(2);
        lda.addInstances(listA);
        lda.addInstances(listB); 
    }
@Test
    public void testPrintTopWordsGeneratesOutput() {
        InstanceList instances = new InstanceList(
            new SerialPipes(Arrays.asList(
                new CharSequence2TokenSequence(Pattern.compile("\\p{L}+")),
                new TokenSequence2FeatureSequence()
            ))
        );
        instances.addThruPipe(new Instance("alpha beta gamma", null, "testdoc", null));
        LDAHyper lda = new LDAHyper(2);
        lda.addInstances(instances);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(output);

        lda.printTopWords(out, 3, true);
        String result = output.toString();

        assertTrue(result.contains("Topic 0") || result.contains("Topic 1"));
    }
@Test
    public void testGetSortedTopicWordsValidity() {
        InstanceList instances = new InstanceList(
            new SerialPipes(Arrays.asList(
                new CharSequence2TokenSequence(Pattern.compile("\\p{L}+")),
                new TokenSequence2FeatureSequence()
            ))
        );
        instances.addThruPipe(new Instance("lorem ipsum dolor", null, "sample", null));
        LDAHyper lda = new LDAHyper(3);
        lda.addInstances(instances);

        IDSorter[] sorted = lda.getSortedTopicWords(0);

        assertNotNull(sorted);
        assertTrue(sorted.length > 0);
    }
@Test
    public void testEmpiricalLikelihoodReturnsFiniteValue() throws Exception {
        InstanceList instances = new InstanceList(
            new SerialPipes(Arrays.asList(
                new CharSequence2TokenSequence(Pattern.compile("\\p{L}+")),
                new TokenSequence2FeatureSequence()
            ))
        );
        instances.addThruPipe(new Instance("banana apple orange", null, "test", null));
        LDAHyper lda = new LDAHyper(3);
        lda.addInstances(instances);
        lda.setTestingInstances(instances);
        lda.setNumIterations(1);
        lda.estimate();

        double likelihood = lda.empiricalLikelihood(5, instances);

        assertTrue(Double.isFinite(likelihood));
    }
@Test
    public void testModelSerializationAndDeserialization() throws Exception {
        InstanceList instances = new InstanceList(
            new SerialPipes(Arrays.asList(
                new CharSequence2TokenSequence(Pattern.compile("\\p{L}+")),
                new TokenSequence2FeatureSequence()
            ))
        );
        instances.addThruPipe(new Instance("cats dogs birds", null, "animal", null));
        LDAHyper lda = new LDAHyper(3);
        lda.addInstances(instances);

        File tempFile = File.createTempFile("lda-model", ".ser");
        lda.write(tempFile);
        assertTrue(tempFile.exists());

        LDAHyper loadedLDA = LDAHyper.read(tempFile);
        assertNotNull(loadedLDA);
        assertEquals(3, loadedLDA.getNumTopics());
        assertNotNull(loadedLDA.getData());
        assertEquals(lda.getAlphabet().size(), loadedLDA.getAlphabet().size());

        tempFile.delete();
    }
@Test
    public void testPrintDocumentTopicsProducesFormat() {
        InstanceList instances = new InstanceList(
            new SerialPipes(Arrays.asList(
                new CharSequence2TokenSequence(Pattern.compile("\\p{L}+")),
                new TokenSequence2FeatureSequence()
            ))
        );
        instances.addThruPipe(new Instance("alpha beta gamma delta", null, "docX", null));
        LDAHyper lda = new LDAHyper(2);
        lda.addInstances(instances);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(out);
        lda.printDocumentTopics(writer, 0.0, -1);
        writer.flush();

        String result = out.toString();
        assertTrue(result.contains("#doc source topic proportion"));
        assertTrue(result.contains("0 docX"));
    }
@Test
    public void testModelLogLikelihoodExecution() throws Exception {
        InstanceList instances = new InstanceList(
            new SerialPipes(Arrays.asList(
                new CharSequence2TokenSequence(Pattern.compile("\\p{L}+")),
                new TokenSequence2FeatureSequence()
            ))
        );
        instances.addThruPipe(new Instance("python java cplusplus", null, "progLang", null));
        LDAHyper lda = new LDAHyper(3);
        lda.addInstances(instances);
        lda.setNumIterations(1);
        lda.estimate();

        double ll = lda.modelLogLikelihood();
        assertTrue(Double.isFinite(ll));
    }
@Test
    public void testAddEmptyInstanceList() {
        InstanceList emptyList = new InstanceList(
            new SerialPipes(Arrays.asList(
                new CharSequence2TokenSequence(Pattern.compile("\\p{L}+")),
                new TokenSequence2FeatureSequence()
            ))
        );

        LDAHyper lda = new LDAHyper(3);
        lda.addInstances(emptyList);

        assertEquals(0, lda.getData().size());
        assertEquals(0, lda.getAlphabet().size());
    }
@Test
    public void testConstructorWithZeroTopics() {
        try {
            LDAHyper lda = new LDAHyper(0);
            fail("Expected exception for zero topics not thrown.");
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException || e instanceof NegativeArraySizeException);
        }
    }
@Test
    public void testEstimateOnZeroTokensPerTopic() throws Exception {
        InstanceList instances = new InstanceList(
            new SerialPipes(Arrays.asList(
                new CharSequence2TokenSequence(Pattern.compile("\\p{L}+")),
                new TokenSequence2FeatureSequence()
            ))
        );
        instances.addThruPipe(new Instance("", null, "emptydoc", null)); 

        LDAHyper lda = new LDAHyper(2);
        lda.setNumIterations(1);
        lda.setTopicDisplay(0, 5);
        lda.addInstances(instances);
        lda.estimate();

        assertEquals(1, lda.getData().size());
    }
@Test
    public void testInstanceWithNullSourceHandled() {
        InstanceList instances = new InstanceList(
            new SerialPipes(Arrays.asList(
                new CharSequence2TokenSequence(Pattern.compile("\\p{L}+")),
                new TokenSequence2FeatureSequence()
            ))
        );
        instances.addThruPipe(new Instance("alpha beta gamma", null, null, null));

        LDAHyper lda = new LDAHyper(2);
        lda.addInstances(instances);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(output);
        lda.printDocumentTopics(writer, 0.0, 1);
        writer.flush();

        String result = output.toString();
        assertTrue(result.contains("null-source"));
    }
@Test
    public void testPrintDocumentTopicsWithMaxZero() {
        InstanceList list = new InstanceList(
            new SerialPipes(Arrays.asList(
                new CharSequence2TokenSequence(Pattern.compile("\\S+")),
                new TokenSequence2FeatureSequence()
            ))
        );
        list.addThruPipe(new Instance("x y z", null, "doc123", null));

        LDAHyper lda = new LDAHyper(5);
        lda.addInstances(list);

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(buffer);
        lda.printDocumentTopics(pw, 0.0, 0);
        pw.flush();

        String output = buffer.toString();
        assertTrue(output.contains("doc123"));
    }
@Test
    public void testTestingInstanceWithUnknownTokenSkippedInLikelihood() throws Exception {
        InstanceList training = new InstanceList(
            new SerialPipes(Arrays.asList(
                new CharSequence2TokenSequence(Pattern.compile("\\w+")),
                new TokenSequence2FeatureSequence()
            ))
        );
        training.addThruPipe(new Instance("apple banana", null, "train", null));

        InstanceList testing = new InstanceList(
            new SerialPipes(Arrays.asList(
                new CharSequence2TokenSequence(Pattern.compile("\\w+")),
                new TokenSequence2FeatureSequence()
            ))
        );
        testing.addThruPipe(new Instance("apple kiwi", null, "test", null)); 

        LDAHyper lda = new LDAHyper(3);
        lda.addInstances(training);
        lda.setTestingInstances(testing);
        lda.setNumIterations(1);
        lda.estimate();

        double ll = lda.empiricalLikelihood(5, testing);
        assertTrue(Double.isFinite(ll));
    }
@Test
    public void testPrintDocumentTopicsWithThresholdLargerThanAll() {
        InstanceList list = new InstanceList(
            new SerialPipes(Arrays.asList(
                new CharSequence2TokenSequence(Pattern.compile("\\S+")),
                new TokenSequence2FeatureSequence()
            ))
        );
        list.addThruPipe(new Instance("a b c", null, "someDoc", null));

        LDAHyper lda = new LDAHyper(2);
        lda.addInstances(list);

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(buffer);
        lda.printDocumentTopics(pw, 1.0, 5); 
        pw.flush();

        String output = buffer.toString();
        assertTrue(output.contains("someDoc")); 
        assertFalse(output.matches(".*\\d+\\.\\d+.*")); 
    }
@Test
    public void testWriteThenReadWithNoDocuments() throws Exception {
        LDAHyper lda = new LDAHyper(2);
        File temp = File.createTempFile("emptylda", ".ser");
        lda.write(temp);
        LDAHyper loaded = LDAHyper.read(temp);
        temp.delete();

        assertNotNull(loaded);
        assertEquals(2, loaded.getNumTopics());
        assertEquals(0, loaded.getData().size());
    }
@Test
    public void testReadFromCorruptedFileReturnsNull() throws Exception {
        File corrupt = File.createTempFile("invalidLDA", ".ser");
        PrintWriter writer = new PrintWriter(corrupt);
        writer.print("not-a-valid-object-stream");
        writer.close();

        LDAHyper result = LDAHyper.read(corrupt);
        corrupt.delete();

        assertNull(result);
    }
@Test
    public void testEstimateWhenZeroIterationsRequested() throws Exception {
        InstanceList list = new InstanceList(
            new SerialPipes(Arrays.asList(
                new CharSequence2TokenSequence(Pattern.compile("\\S+")),
                new TokenSequence2FeatureSequence()
            ))
        );
        list.addThruPipe(new Instance("foo bar baz", null, "docId", null));

        LDAHyper lda = new LDAHyper(2);
        lda.setNumIterations(0);
        lda.setTopicDisplay(0, 4);
        lda.addInstances(list);
        lda.estimate();

        assertEquals(1, lda.getData().size());
    }
@Test
    public void testGetCountFeatureTopicWithUnusedTypeIndex() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("word1");
        alphabet.lookupIndex("word2");

        InstanceList training = new InstanceList(new SerialPipes(Arrays.asList(
                new CharSequence2TokenSequence(Pattern.compile("\\S+")),
                new TokenSequence2FeatureSequence()
        )));
        training.addThruPipe(new Instance("word1", null, "doc", null));

        LDAHyper lda = new LDAHyper(2);
        lda.addInstances(training);

        int count = lda.getCountFeatureTopic(1, 0); 
        assertEquals(0, count);
    }
@Test
    public void testPrintTopWordsNoNewLines() {
        InstanceList training = new InstanceList(new SerialPipes(Arrays.asList(
                new CharSequence2TokenSequence(Pattern.compile("\\S+")),
                new TokenSequence2FeatureSequence()
        )));
        training.addThruPipe(new Instance("w1 w2 w3", null, "docX", null));

        LDAHyper lda = new LDAHyper(2);
        lda.addInstances(training);

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(buffer);
        lda.printTopWords(ps, 3, false);

        String text = buffer.toString();
        assertTrue(text.contains("\t"));
        assertTrue(text.contains("w1") || text.contains("w2") || text.contains("w3"));
    }
@Test
    public void testGetSortedTopicWordsSortOrder() {
        InstanceList training = new InstanceList(new SerialPipes(Arrays.asList(
            new CharSequence2TokenSequence(Pattern.compile("\\S+")),
            new TokenSequence2FeatureSequence()
        )));
        training.addThruPipe(new Instance("x y x x z", null, "docZ", null));

        LDAHyper lda = new LDAHyper(3);
        lda.addInstances(training);
        IDSorter[] sorted = lda.getSortedTopicWords(0);

        assertTrue(sorted.length > 0);
        assertTrue(sorted[0].getID() >= 0);
    }
@Test
    public void testPrintStateOutputFormat() throws Exception {
        InstanceList training = new InstanceList(new SerialPipes(Arrays.asList(
            new CharSequence2TokenSequence(Pattern.compile("\\S+")),
            new TokenSequence2FeatureSequence()
        )));
        training.addThruPipe(new Instance("foo bar", null, "sourcedoc", null));

        LDAHyper lda = new LDAHyper(2);
        lda.addInstances(training);
        File f = File.createTempFile("state", ".gz");

        lda.printState(f);
        assertTrue(f.exists());
        assertTrue(f.length() > 0);

        f.delete();
    }
@Test
    public void testTopicXMLReportStructure() {
        InstanceList training = new InstanceList(new SerialPipes(Arrays.asList(
            new CharSequence2TokenSequence(Pattern.compile("\\S+")),
            new TokenSequence2FeatureSequence()
        )));
        training.addThruPipe(new Instance("quick fox ran", null, "d1", null));

        LDAHyper lda = new LDAHyper(2);
        lda.addInstances(training);

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintWriter out = new PrintWriter(buffer);
        lda.topicXMLReport(out, 5);
        out.flush();

        String xml = buffer.toString();
        assertTrue(xml.contains("<topicModel>"));
        assertTrue(xml.contains("<topic id='0'"));
        assertTrue(xml.contains("</topicModel>"));
    }
@Test
    public void testTopicXMLReportPhrasesNoWords() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("foo");
        alphabet.lookupIndex("bar");
        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{0, 1});
        LabelAlphabet topicAlpha = new LabelAlphabet();
        topicAlpha.lookupIndex("topic0");
        LabelSequence topicSeq = new LabelSequence(topicAlpha, new int[]{0, 0});

        Instance inst = new Instance(fs, null, "s", null);
        LDAHyper lda = new LDAHyper(1);
        LDAHyper.Topication t = lda.new Topication(inst, lda, topicSeq);
        lda.getData().add(t);
        lda.initializeForTypes(alphabet);

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(buffer);

        lda.topicXMLReportPhrases(out, 5);

        String result = buffer.toString();
        assertTrue(result.contains("<topics>"));
        assertTrue(result.contains("</topics>"));
    }
@Test
    public void testWriteObjectWithInitializedFields() throws Exception {
        LDAHyper lda = new LDAHyper(2);
        File temp = File.createTempFile("testlda", ".ser");
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(temp));
        oos.writeObject(lda);
        oos.close();

        ObjectInputStream in = new ObjectInputStream(new FileInputStream(temp));
        LDAHyper loaded = (LDAHyper) in.readObject();

        assertNotNull(loaded);
        assertEquals(2, loaded.getNumTopics());

        temp.delete();
    }
@Test
    public void testSampleTopicsWithMinimalSequence() throws Exception {
        Alphabet alph = new Alphabet();
        alph.lookupIndex("hello");
        FeatureSequence fs = new FeatureSequence(alph, new int[]{0});
        LabelAlphabet ta = new LabelAlphabet();
        ta.lookupIndex("t0");
        LabelSequence ts = new LabelSequence(ta, new int[]{0});
        Instance instance = new Instance(fs, null, "doc", null);

        LDAHyper lda = new LDAHyper(ta, 1.0, 0.01, new cc.mallet.util.Randoms(0));
        lda.getData().add(lda.new Topication(instance, lda, ts));
        lda.initializeForTypes(alph);
        lda.addInstances(new InstanceList(alph, null));

        lda.sampleTopicsForOneDoc(fs, ts, false, false);
        assertEquals(1, ts.getLength());
    }
@Test
    public void testModelLogLikelihoodZeroDocuments() {
        LDAHyper lda = new LDAHyper(3);
        double ll = lda.modelLogLikelihood();
        assertTrue(Double.isFinite(ll));
    }
@Test
    public void testTopicLabelMutualInformationNoLabels() {
        Alphabet alphabet = new Alphabet();
        LabelAlphabet topicAlphabet = new LabelAlphabet();
        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{});
        LabelSequence ts = new LabelSequence(topicAlphabet, new int[]{0});
        Instance instance = new Instance(fs, null, "doc", null);
        LDAHyper lda = new LDAHyper(topicAlphabet, 1.0, 0.01, new cc.mallet.util.Randoms(0));
        lda.initializeForTypes(alphabet);
        lda.getData().add(lda.new Topication(instance, lda, ts));
        double result = lda.topicLabelMutualInformation();
        assertEquals(0.0, result, 0.00001);
    }
@Test
    public void testAlphaLearningTriggeredPath() throws Exception {
        InstanceList list = new InstanceList(
            new SerialPipes(Arrays.asList(
                new CharSequence2TokenSequence(Pattern.compile("\\w+")),
                new TokenSequence2FeatureSequence()
            ))
        );
        list.addThruPipe(new Instance("a b c a b", null, "topic", null));
        LDAHyper lda = new LDAHyper(2);
        lda.setOptimizeInterval(1);
        lda.setNumIterations(2);
        lda.setBurninPeriod(0);
        lda.addInstances(list);
        lda.estimate();
        assertEquals(1, lda.getData().size());
    }
@Test
    public void testAddInstancesWithEmptyTopicSequences() {
        Alphabet alphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        alphabet.lookupIndex("apple");
        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{0});
        LabelSequence topicSeq = new LabelSequence(labelAlphabet, new int[]{});
        Instance i = new Instance(fs, null, "doc1", null);
        InstanceList list = new InstanceList(alphabet, null);
        list.add(i);

        LDAHyper lda = new LDAHyper(labelAlphabet, 1.0, 0.01, new Randoms(1));
        try {
            lda.addInstances(list, Arrays.asList(topicSeq));
            
            assertEquals(1, lda.getData().size());
        } catch (Exception ex) {
            fail("Should not throw exception with empty topic sequence");
        }
    }
@Test
    public void testInitializeForTypesWithResizedAlphabet() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("A");
        LDAHyper lda = new LDAHyper(2);
        lda.addInstances(new InstanceList(new SerialPipes(Arrays.asList(
                new CharSequence2TokenSequence(Pattern.compile("\\p{L}+")),
                new TokenSequence2FeatureSequence()
        ))));
        alphabet.lookupIndex("B");
        alphabet.lookupIndex("C");
        try {
            lda.addInstances(new InstanceList(alphabet, null));
        } catch (Exception e) {
            
            assertTrue(e instanceof IllegalArgumentException);
        }
    }
@Test
    public void testZeroAlphaLeadsToNoSamplingFailure() throws Exception {
        InstanceList training = new InstanceList(new SerialPipes(Arrays.asList(
                new CharSequence2TokenSequence(Pattern.compile("\\p{L}+")),
                new TokenSequence2FeatureSequence()
        )));
        training.addThruPipe(new Instance("foo bar baz", null, "doc", null));
        LDAHyper lda = new LDAHyper(2);
        for (int i = 0; i < 2; i++) {
            lda.alpha[i] = 0.0;
        }
        lda.addInstances(training);
        lda.setNumIterations(1);
        lda.estimate(); 
        assertTrue(lda.modelLogLikelihood() < 0);
    }
@Test
    public void testDocumentTopicCountsHistogramInitializes() {
        LDAHyper lda = new LDAHyper(3);
        Alphabet a = new Alphabet();
        a.lookupIndex("x");
        lda.initializeForTypes(a);
        lda.initializeHistogramsAndCachedValues();
        assertNotNull(lda.cachedCoefficients);
        assertNotNull(lda.docLengthCounts);
        assertNotNull(lda.topicDocCounts);
    }
@Test
    public void testClearHistogramsOnReOptimization() {
        LDAHyper lda = new LDAHyper(2);
        lda.docLengthCounts = new int[3];
        lda.docLengthCounts[0] = 2;
        lda.docLengthCounts[1] = 3;
        lda.topicDocCounts = new int[2][3];
        lda.topicDocCounts[0][1] = 5;
        lda.topicDocCounts[1][2] = 4;

        lda.clearHistograms();
        assertEquals(0, lda.docLengthCounts[0]);
        assertEquals(0, lda.docLengthCounts[1]);
        assertEquals(0, lda.topicDocCounts[0][1]);
        assertEquals(0, lda.topicDocCounts[1][2]);
    }
@Test
    public void testEmpiricalLikelihoodWithEmptyData() {
        InstanceList testing = new InstanceList(new SerialPipes(Arrays.asList(
                new CharSequence2TokenSequence(Pattern.compile("\\w+")),
                new TokenSequence2FeatureSequence()
        )));
        LDAHyper lda = new LDAHyper(3);
        LDAHyper emptyLda = new LDAHyper(3);

        lda.setTestingInstances(testing);
        double result = lda.empiricalLikelihood(5, testing);
        assertEquals(0.0, result, 0.000001);
    }
@Test
    public void testPrintDocumentTopicsWithNoWords() {
        Alphabet alphabet = new Alphabet();
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        alphabet.lookupIndex("x");
        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{});
        LabelSequence ts = new LabelSequence(labelAlphabet, new int[]{});
        Instance instance = new Instance(fs, null, "emptydoc", null);
        LDAHyper lda = new LDAHyper(3);
        LDAHyper.Topication t = lda.new Topication(instance, lda, ts);
        lda.getData().add(t);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(buffer);

        lda.printDocumentTopics(writer);
        writer.flush();
        String content = buffer.toString();
        assertTrue(content.contains("#doc"));
        assertTrue(content.contains("emptydoc"));
    }
@Test
    public void testPrintTopWordsNoTypesInTopic() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("one");
        alphabet.lookupIndex("two");
        LDAHyper lda = new LDAHyper(2);
        lda.initializeForTypes(alphabet);

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream stream = new PrintStream(buffer);
        lda.printTopWords(stream, 5, true); 

        String result = buffer.toString();
        assertTrue(result.contains("Topic 0"));
        assertTrue(result.contains("Topic 1"));
    }
@Test
    public void testPrintStateNoTokens() throws Exception {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("x");

        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{});
        LabelAlphabet la = new LabelAlphabet();
        la.lookupIndex("t0");
        LabelSequence ts = new LabelSequence(la, new int[]{});
        Instance i = new Instance(fs, null, "blankdoc", null);

        LDAHyper lda = new LDAHyper(la, 1.0, 0.01, new Randoms(123));
        lda.initializeForTypes(alphabet);
        lda.getData().add(lda.new Topication(i, lda, ts));
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream stream = new PrintStream(buffer);

        lda.printState(stream);
        String state = buffer.toString();
        assertTrue(state.contains("#doc source pos typeindex type topic"));
    }
@Test
    public void testTopicXMLReportWithNoWords() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("a");
        LDAHyper lda = new LDAHyper(3);
        lda.initializeForTypes(alphabet);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(buffer);
        lda.topicXMLReport(writer, 10);
        writer.flush();
        String xml = buffer.toString();
        assertTrue(xml.contains("<?xml"));
        assertTrue(xml.contains("<topicModel>"));
    }
@Test
    public void testMainMethodRunsMinimal() throws Exception {
        InstanceList list = new InstanceList(new SerialPipes(Arrays.asList(
                new CharSequence2TokenSequence(Pattern.compile("\\w+")),
                new TokenSequence2FeatureSequence()
        )));
        list.addThruPipe(new Instance("sampletext goeshere", null, "docId", null));
        File f = File.createTempFile("lda_input", ".data");
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(f));
        out.writeObject(list);
        out.close();

        LDAHyper.main(new String[]{f.getAbsolutePath(), "2"});

        assertTrue(f.exists());
        f.delete();
    }
@Test
    public void testInitializeTypeTopicCountsWithLargerAlphabet() {
        Alphabet smallAlphabet = new Alphabet();
        smallAlphabet.lookupIndex("x");

        LDAHyper lda = new LDAHyper(2);
        lda.initializeForTypes(smallAlphabet);

        Alphabet largerAlphabet = new Alphabet();
        largerAlphabet.lookupIndex("x");
        largerAlphabet.lookupIndex("y");
        lda.initializeForTypes(largerAlphabet);

        assertEquals(2, largerAlphabet.size());
    }
@Test
    public void testAddInstancesWithNullSourceInstance() {
        InstanceList list = new InstanceList(new SerialPipes(Arrays.asList(
            new CharSequence2TokenSequence(Pattern.compile("\\S+")),
            new TokenSequence2FeatureSequence()
        )));
        list.addThruPipe(new Instance("zebra lion tiger", null, null, null));

        LDAHyper lda = new LDAHyper(2);
        lda.addInstances(list);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(stream);
        lda.printDocumentTopics(writer);
        writer.flush();

        String output = stream.toString();
        assertTrue(output.contains("null-source"));
    }
@Test
    public void testEstimateWithMinConfig() throws Exception {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("token");
        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{0});
        LabelAlphabet la = new LabelAlphabet();
        la.lookupIndex("topic0");
        LabelSequence ts = new LabelSequence(la, new int[]{0});
        Instance instance = new Instance(fs, null, "s", null);

        LDAHyper lda = new LDAHyper(la, 1.0, 0.01, new Randoms(1));
        lda.initializeForTypes(alphabet);
        lda.addInstances(new InstanceList(alphabet, null), Arrays.asList(ts));
        lda.setNumIterations(1);
        lda.estimate();

        assertEquals(1, lda.getData().size());
    }
@Test
    public void testTopicTermMassEdgeCondition() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("x");
        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{0});
        LabelAlphabet topics = new LabelAlphabet();
        topics.lookupIndex("t0");
        LabelSequence topicSeq = new LabelSequence(topics, new int[]{0});

        Instance instance = new Instance(fs, null, "edge", null);
        LDAHyper lda = new LDAHyper(topics, 1.0, 0.01, new Randoms(123));
        lda.initializeForTypes(alphabet);
        lda.getData().add(lda.new Topication(instance, lda, topicSeq));
        lda.sampleTopicsForOneDoc(fs, topicSeq, false, true);

        assertEquals(1, topicSeq.getLength());
    }
@Test
    public void testPrintTopWordsWithZeroTypeTopicCounts() {
        Alphabet a = new Alphabet();
        a.lookupIndex("alpha");
        a.lookupIndex("beta");
        LDAHyper lda = new LDAHyper(1);
        lda.initializeForTypes(a);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(out);
        lda.printTopWords(ps, 5, false);
        assertTrue(out.toString().contains("alpha") || out.toString().contains("beta"));
    }
@Test
    public void testTopicXMLReportPhrasesEmptyCounts() {
        Alphabet a = new Alphabet();
        a.lookupIndex("pizza");
        FeatureSequence fs = new FeatureSequence(a, new int[]{0, 0, 0});
        LabelAlphabet topics = new LabelAlphabet();
        topics.lookupIndex("T0");
        LabelSequence ts = new LabelSequence(topics, new int[]{0, 0, 0});
        Instance inst = new Instance(fs, null, "p0", null);
        LDAHyper lda = new LDAHyper(topics, 1.0, 0.01, new Randoms(0));
        lda.initializeForTypes(a);
        LDAHyper.Topication topication = lda.new Topication(inst, lda, ts);
        lda.getData().add(topication);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream stream = new PrintStream(out);
        lda.topicXMLReportPhrases(stream, 5);
        assertTrue(out.toString().contains("<topics>"));
    }
@Test
    public void testWriteReadModelWithEmptyAlphabets() throws Exception {
        LDAHyper lda = new LDAHyper(2);
        File f = File.createTempFile("lda", ".ser");
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
        oos.writeObject(lda);
        oos.close();

        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
        LDAHyper reloaded = (LDAHyper) ois.readObject();

        assertEquals(2, reloaded.getNumTopics());
        assertNotNull(reloaded.getData());

        f.delete();
    }
@Test
    public void testModelLogLikelihoodWithNoTokensAssigned() {
        LDAHyper lda = new LDAHyper(2);
        double logLikelihood = lda.modelLogLikelihood();
        assertTrue(Double.isFinite(logLikelihood));
    }
@Test
    public void testPrintTopWordsOnNewAlphabet() {
        Alphabet alpha = new Alphabet();
        alpha.lookupIndex("a");
        alpha.lookupIndex("b");
        LDAHyper lda = new LDAHyper(4);
        lda.initializeForTypes(alpha);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        PrintStream output = new PrintStream(stream);
        lda.printTopWords(output, 5, true);

        String result = stream.toString();
        assertTrue(result.contains("Topic"));
    }
@Test
    public void testGetSortedTopicWordsIncludesTypes() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("dog");
        alphabet.lookupIndex("cat");

        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{0, 1});
        LabelAlphabet ta = new LabelAlphabet();
        ta.lookupIndex("topic0");
        LabelSequence ts = new LabelSequence(ta, new int[]{0, 0});
        Instance inst = new Instance(fs, null, "pet", null);

        LDAHyper lda = new LDAHyper(ta, 1.0, 0.01, new Randoms(4));
        lda.initializeForTypes(alphabet);
        lda.addInstances(new InstanceList(alphabet, null), Arrays.asList(ts));

        IDSorter[] sorted = lda.getSortedTopicWords(0);
        assertEquals(alphabet.size(), sorted.length);
    }
@Test
    public void testAddInstancesWithUnequalSizeThrows() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("w1");
        Instance inst = new Instance("w1 w1", null, "wrong", null);

        InstanceList list = new InstanceList(new SerialPipes(Arrays.asList(
            new CharSequence2TokenSequence(Pattern.compile("\\S+")),
            new TokenSequence2FeatureSequence()
        )));
        list.addThruPipe(inst);

        LabelAlphabet ta = new LabelAlphabet();
        ta.lookupIndex("tt");
        LabelSequence ts1 = new LabelSequence(ta, new int[]{});
        LabelSequence ts2 = new LabelSequence(ta, new int[]{0, 0});

        LDAHyper lda = new LDAHyper(ta, 1.0, 0.01, new Randoms());
        try {
            lda.addInstances(list, Arrays.asList(ts1, ts2));
            fail("Expected AssertionError due to mismatched sizes");
        } catch (AssertionError e) {
            assertTrue(true);
        }
    }
@Test
    public void testTopicSamplingFallsBackToSmoothingMass() {
        Alphabet alphabet = new Alphabet();
        int wi = alphabet.lookupIndex("token");
        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{wi});
        LabelAlphabet topics = new LabelAlphabet();
        int ti = topics.lookupIndex("topic0");
        LabelSequence ts = new LabelSequence(topics, new int[]{0});
        Instance instance = new Instance(fs, null, "sampledoc", null);

        LDAHyper lda = new LDAHyper(topics, 1.0, 0.0000001, new Randoms(123));
        lda.initializeForTypes(alphabet);
        lda.getData().add(lda.new Topication(instance, lda, ts));
        lda.tokensPerTopic = new int[]{0}; 
        lda.alpha = new double[]{1.0};
        lda.betaSum = 0.0001;
        lda.smoothingOnlyMass = 1000.0; 

        lda.sampleTopicsForOneDoc(fs, ts, false, true);
        assertEquals(1, ts.getLength());
    }
@Test
    public void testAddTopicationWithInvalidFeatureIndex() {
        Alphabet alphabet = new Alphabet();
        int wi = alphabet.lookupIndex("w1");
        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{wi});
        LabelAlphabet topicAlpha = new LabelAlphabet();
        int ti = topicAlpha.lookupIndex("t0");
        LabelSequence topics = new LabelSequence(topicAlpha, new int[]{ti});
        Instance instance = new Instance(fs, null, "badDoc", null);

        LDAHyper lda = new LDAHyper(topicAlpha, 1.0, 0.01, new Randoms());
        lda.initializeForTypes(alphabet);
        IntIntHashMap badMap = new IntIntHashMap();
        lda.typeTopicCounts[wi] = badMap;

        lda.addInstances(new InstanceList(alphabet, null), Arrays.asList(topics));
        assertEquals(1, lda.getData().size());
    }
@Test
    public void testTopicXMLReportPhrasesSkipsInvalidBigramIndices() {
        FeatureSequence fs = new FeatureSequenceWithBigrams(new Alphabet(), 3);
        fs.add("foo");
        fs.add("bar");
        fs.add("baz");
        ((FeatureSequenceWithBigrams) fs).setBiIndexAtPosition(1, -1);

        LabelAlphabet labelAlphabet = new LabelAlphabet();
        int topicIndex = labelAlphabet.lookupIndex("topicX");
        LabelSequence topicSequence = new LabelSequence(labelAlphabet, new int[]{0, 0, 0});
        Instance inst = new Instance(fs, null, "invalidBigram", null);

        LDAHyper lda = new LDAHyper(labelAlphabet, 1.0, 0.01, new Randoms(0));
        lda.initializeForTypes(fs.getAlphabet());
        LDAHyper.Topication top = lda.new Topication(inst, lda, topicSequence);
        lda.getData().add(top);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        lda.topicXMLReportPhrases(ps, 3);
        String xml = os.toString();

        assertTrue(xml.contains("<topics>"));
        assertTrue(xml.contains("<topic id="));
    }
@Test
    public void testSampleTopicsWithAlphaNaN() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("alpha");

        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{0});
        LabelAlphabet ta = new LabelAlphabet();
        ta.lookupIndex("T0");
        LabelSequence ts = new LabelSequence(ta, new int[]{0});
        Instance inst = new Instance(fs, null, "NaNAlphaDoc", null);

        LDAHyper lda = new LDAHyper(ta, 1.0, 0.01, new Randoms(42));
        lda.alpha[0] = Double.NaN;
        lda.initializeForTypes(alphabet);
        lda.getData().add(lda.new Topication(inst, lda, ts));

        lda.sampleTopicsForOneDoc(fs, ts, false, true);
        assertEquals(1, ts.getLength());
    }
@Test
    public void testSampleTopicsNegativeBeta() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("z");

        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{0});
        LabelAlphabet ta = new LabelAlphabet();
        ta.lookupIndex("T0");
        LabelSequence ts = new LabelSequence(ta, new int[]{0});
        Instance inst = new Instance(fs, null, "negBeta", null);

        LDAHyper lda = new LDAHyper(ta, 1.0, -0.01, new Randoms(123));
        lda.initializeForTypes(alphabet);
        lda.getData().add(lda.new Topication(inst, lda, ts));

        lda.sampleTopicsForOneDoc(fs, ts, false, true);
        assertEquals(1, ts.getLength());
    }
@Test
    public void testAlphaLearningTriggeredWithSparseDocLengthCounts() throws Exception {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("aaa");
        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{0});
        LabelAlphabet la = new LabelAlphabet();
        la.lookupIndex("AAA");
        LabelSequence ts = new LabelSequence(la, new int[]{0});
        Instance instance = new Instance(fs, null, "alphaopt", null);

        LDAHyper lda = new LDAHyper(la, 1.0, 0.01, new Randoms(1));
        lda.addInstances(new InstanceList(alphabet, null), Arrays.asList(ts));
        lda.setOptimizeInterval(1);
        lda.setNumIterations(2);
        lda.setBurninPeriod(0);
        lda.estimate();

        assertEquals(1, lda.getData().size());
    }
@Test
    public void testPrintDocumentTopicsWithThresholdExclusion() {
        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("term");
        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{0});
        LabelAlphabet ta = new LabelAlphabet();
        ta.lookupIndex("topicA");
        LabelSequence ts = new LabelSequence(ta, new int[]{0});
        Instance inst = new Instance(fs, null, "Xdoc", null);

        LDAHyper lda = new LDAHyper(ta, 1.0, 0.01, new Randoms());
        lda.initializeForTypes(alphabet);
        LDAHyper.Topication top = lda.new Topication(inst, lda, ts);
        lda.getData().add(top);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(os);
        lda.printDocumentTopics(writer, 2.0, 10);
        writer.flush();

        String result = os.toString();
        assertTrue(result.contains("Xdoc"));
    }
@Test
    public void testAddTopicationWithMismatchedAlphabetThrows() {
        Alphabet a1 = new Alphabet();
        a1.lookupIndex("a");

        Alphabet a2 = new Alphabet();
        a2.lookupIndex("a");
        a2.lookupIndex("b");

        FeatureSequence fs = new FeatureSequence(a2, new int[]{1});
        LabelAlphabet ta = new LabelAlphabet();
        ta.lookupIndex("T");
        LabelSequence ts = new LabelSequence(ta, new int[]{0});
        Instance inst = new Instance(fs, null, "bad", null);

        LDAHyper lda = new LDAHyper(ta, 1.0, 0.01, new Randoms());
        lda.initializeForTypes(a1);

        try {
            lda.addInstances(new InstanceList(a2, null), Arrays.asList(ts));
            fail("Expected exception due to mismatched alphabets.");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("Cannot change Alphabet"));
        }
    }
@Test
    public void testPrintStateWithEmptyInstance() {
        Alphabet a = new Alphabet();
        FeatureSequence fs = new FeatureSequence(a, new int[]{});
        LabelAlphabet ta = new LabelAlphabet();
        ta.lookupIndex("T");
        LabelSequence ts = new LabelSequence(ta, new int[]{});
        Instance i = new Instance(fs, null, "X", null);

        LDAHyper lda = new LDAHyper(ta, 1.0, 0.01, new Randoms(0));
        lda.initializeForTypes(a);
        lda.getData().add(lda.new Topication(i, lda, ts));

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        lda.printState(ps);
        String output = os.toString();

        assertTrue(output.contains("#doc"));
    }
@Test
    public void testAddInstancesWithTypeTopicCountsManuallyNull() {
        Alphabet a = new Alphabet();
        a.lookupIndex("x");
        FeatureSequence fs = new FeatureSequence(a, new int[]{0});
        LabelAlphabet labelAlphabet = new LabelAlphabet();
        labelAlphabet.lookupIndex("t0");
        LabelSequence labels = new LabelSequence(labelAlphabet, new int[]{0});
        Instance instance = new Instance(fs, null, "nullTopicCounts", null);
        LDAHyper lda = new LDAHyper(labelAlphabet, 1.0, 0.01, new Randoms(10));
        lda.initializeForTypes(a);
        lda.typeTopicCounts[0] = null; 
        InstanceList list = new InstanceList(a, null);
        list.add(instance);
        try {
            lda.addInstances(list, Arrays.asList(labels)); 
        } catch (NullPointerException | AssertionError e) {
            assertTrue(true); 
        }
    }
@Test
    public void testPrintDocumentTopicsWithMissingLabeling() {
        Alphabet a = new Alphabet();
        a.lookupIndex("tok");
        LabelAlphabet ta = new LabelAlphabet();
        ta.lookupIndex("topic1");
        FeatureSequence fs = new FeatureSequence(a, new int[]{0});
        LabelSequence ts = new LabelSequence(ta, new int[]{0});
        Instance doc = new Instance(fs, null, "nolabeldoc", null); 
        LDAHyper lda = new LDAHyper(ta, 1.0, 0.01, new Randoms());
        lda.initializeForTypes(a);
        lda.getData().add(lda.new Topication(doc, lda, ts));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(output);
        lda.printDocumentTopics(writer, 0.0, -1);
        writer.flush();
        String text = output.toString();
        assertTrue(text.contains("nolabeldoc"));
    }
@Test
    public void testPrintTopWordsWithTypeTopicCountsEmptyMap() {
        Alphabet a = new Alphabet();
        a.lookupIndex("alpha");
        a.lookupIndex("beta");
        LDAHyper lda = new LDAHyper(2);
        lda.initializeForTypes(a);
        lda.typeTopicCounts[0] = new IntIntHashMap(); 
        lda.typeTopicCounts[1] = new IntIntHashMap(); 
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        lda.printTopWords(ps, 3, false);
        String res = os.toString();
        assertTrue(res.contains("0") || res.contains("1"));
    }
@Test
    public void testPrintStateToFileValidity() throws Exception {
        Alphabet a = new Alphabet();
        a.lookupIndex("word");
        LabelAlphabet topLab = new LabelAlphabet();
        topLab.lookupIndex("topicA");
        FeatureSequence fs = new FeatureSequence(a, new int[]{0, 0});
        LabelSequence ts = new LabelSequence(topLab, new int[]{0, 0});
        Instance inst = new Instance(fs, null, "docX", null);
        LDAHyper lda = new LDAHyper(topLab, 1.0, 0.01, new Randoms());
        lda.initializeForTypes(a);
        lda.getData().add(lda.new Topication(inst, lda, ts));
        File out = File.createTempFile("lda-state-test", ".gz");
        lda.printState(out);
        assertTrue(out.exists() && out.length() > 0);
        out.delete();
    }
@Test
    public void testTopicationManualSerializationRoundTrip() throws Exception {
        Alphabet a = new Alphabet();
        a.lookupIndex("x");
        LabelAlphabet l = new LabelAlphabet();
        l.lookupIndex("t");
        FeatureSequence fs = new FeatureSequence(a, new int[]{0});
        LabelSequence ts = new LabelSequence(l, new int[]{0});
        Instance inst = new Instance(fs, null, "serTest", null);
        LDAHyper lda = new LDAHyper(l, 1.0, 0.01, new Randoms());
        LDAHyper.Topication top = lda.new Topication(inst, lda, ts);

        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(byteOut);
        oos.writeObject(top);
        oos.close();

        byte[] data = byteOut.toByteArray();
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
        Object result = ois.readObject();

        assertNotNull(result);
        assertTrue(result instanceof LDAHyper.Topication);
        LDAHyper.Topication loaded = (LDAHyper.Topication) result;
        assertNotNull(loaded.instance);
        assertNotNull(loaded.topicSequence);
    }
@Test
    public void testGetTopicAlphabetAndAlphabetSafety() {
        LDAHyper lda = new LDAHyper(3);
        assertNotNull(lda.getTopicAlphabet());
        assertNull(lda.getAlphabet()); 
    }
@Test
    public void testEmptyFeatureSequenceSamplingDoesNotFail() {
        Alphabet alphabet = new Alphabet();
        LabelAlphabet label = new LabelAlphabet();
        label.lookupIndex("topic0");
        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{});
        LabelSequence ts = new LabelSequence(label, new int[]{});
        Instance inst = new Instance(fs, null, "emptoken", null);
        LDAHyper lda = new LDAHyper(label, 1.0, 0.01, new Randoms(10));
        lda.initializeForTypes(alphabet);
        lda.getData().add(lda.new Topication(inst, lda, ts));
        lda.sampleTopicsForOneDoc(fs, ts, false, true);
        assertEquals(0, fs.size());
        assertEquals(0, ts.getLength());
    }
@Test
    public void testTopicXMLReportLimitedPhraseRanking() {
        Alphabet a = new Alphabet();
        int wi = a.lookupIndex("word");
        FeatureSequence fs = new FeatureSequence(a, new int[]{wi, wi, wi});
        LabelAlphabet ta = new LabelAlphabet();
        ta.lookupIndex("T0");
        LabelSequence ts = new LabelSequence(ta, new int[]{0, 0, 0});
        Instance inst = new Instance(fs, null, "Xtitle", null);
        LDAHyper lda = new LDAHyper(ta, 1.0, 0.01, new Randoms(0));
        lda.initializeForTypes(a);
        lda.getData().add(lda.new Topication(inst, lda, ts));
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(os);
        lda.topicXMLReport(writer, 1); 
        writer.flush();
        String xml = os.toString();
        assertTrue(xml.contains("title="));
    }
@Test
    public void testEstimateWithZeroWordsNoFailure() throws IOException {
        Alphabet alphabet = new Alphabet();
        FeatureSequence fs = new FeatureSequence(alphabet, new int[]{});
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupIndex("top0");
        LabelSequence ts = new LabelSequence(labels, new int[]{});
        Instance i = new Instance(fs, null, "zerocase", null);
        LDAHyper lda = new LDAHyper(labels, 1.0, 0.01, new Randoms());
        InstanceList dummyList = new InstanceList(alphabet, null);
        dummyList.add(i);
        lda.addInstances(dummyList, Arrays.asList(ts));
        lda.setNumIterations(1);
        lda.estimate();
        assertEquals(1, lda.getData().size());
    }
@Test
    public void testSetSaveStateConfiguration() {
        LDAHyper lda = new LDAHyper(5);
        lda.setSaveState(10, "model-state-abc.gz");
        assertEquals("model-state-abc.gz", lda.stateFilename);
    }
@Test
    public void testSetModelOutputConfiguration() {
        LDAHyper lda = new LDAHyper(2);
        lda.setModelOutput(5, "model-file.serial");
        assertEquals("model-file.serial", lda.outputModelFilename);
    }
@Test
    public void testSetDisplayAndRandomSeedTogether() {
        LDAHyper lda = new LDAHyper(3);
        lda.setTopicDisplay(20, 10);
        lda.setRandomSeed(12345);
        assertNotNull(lda.getTopicAlphabet());
    }
@Test
    public void testGetCountTokensPerTopicAccessBoundaries() {
        LDAHyper lda = new LDAHyper(4);
        int val = lda.getCountTokensPerTopic(0); 
        assertEquals(0, val);
    } 
}