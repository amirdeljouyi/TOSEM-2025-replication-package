public class TopicalNGrams_3_GPTLLMTest { 

 @Test
    public void testConstructorSetsParameters() {
        TopicalNGrams model = new TopicalNGrams(4, 8.0, 0.01, 0.02, 0.03, 0.1, 0.2);

        assertEquals(4, model.numTopics);
        assertTrue(model.alpha > 0.0);
        assertEquals(0.01, model.beta, 0.00001);
        assertEquals(0.02, model.gamma, 0.00001);
        assertEquals(0.03, model.delta, 0.00001);
    }
@Test
    public void testEstimateWithSingleShortDocument() {
        TopicalNGrams model = new TopicalNGrams(3);

        FeatureSequenceWithBigrams fs = mock(FeatureSequenceWithBigrams.class);
        when(fs.getLength()).thenReturn(2);
        when(fs.getIndexAtPosition(0)).thenReturn(0);
        when(fs.getIndexAtPosition(1)).thenReturn(1);
        when(fs.getBiIndexAtPosition(0)).thenReturn(-1);
        when(fs.getBiIndexAtPosition(1)).thenReturn(-1);
        Alphabet uniAlphabet = new Alphabet();
        uniAlphabet.lookupIndex("word0");
        uniAlphabet.lookupIndex("word1");
        when(fs.getBiAlphabet()).thenReturn(new Alphabet());

        Instance instance = new Instance(fs, null, "doc1", null);
        InstanceList instanceList = mock(InstanceList.class);
        when(instanceList.size()).thenReturn(1);
        when(instanceList.get(0)).thenReturn(instance);
        when(instanceList.getDataAlphabet()).thenReturn(uniAlphabet);

        Randoms randoms = mock(Randoms.class);
        when(randoms.nextInt(3)).thenReturn(0);

        model.estimate(instanceList, 1, 0, 0, null, randoms);

        assertNotNull(instanceList);
    }
@Test
    public void testPrintTopWordsDoesNotThrowException() {
        TopicalNGrams model = new TopicalNGrams(2);

        FeatureSequenceWithBigrams fs = mock(FeatureSequenceWithBigrams.class);
        when(fs.getLength()).thenReturn(1);
        when(fs.getIndexAtPosition(0)).thenReturn(0);
        when(fs.getBiIndexAtPosition(0)).thenReturn(-1);
        Alphabet uniAlphabet = new Alphabet();
        uniAlphabet.lookupIndex("wordX");
        when(fs.getBiAlphabet()).thenReturn(new Alphabet());

        Instance instance = new Instance(fs, null, "doc1", null);
        InstanceList instanceList = mock(InstanceList.class);
        when(instanceList.size()).thenReturn(1);
        when(instanceList.get(0)).thenReturn(instance);
        when(instanceList.getDataAlphabet()).thenReturn(uniAlphabet);

        Randoms randoms = mock(Randoms.class);
        when(randoms.nextInt(2)).thenReturn(0);

        model.estimate(instanceList, 5, 0, 0, null, randoms);

        try {
            model.printTopWords(3, true);
        } catch (Exception e) {
            fail("Should not throw exception: " + e.getMessage());
        }
    }
@Test
    public void testPrintDocumentTopicsPrintsExpectedFormat() {
        TopicalNGrams model = new TopicalNGrams(2);

        FeatureSequenceWithBigrams fs = mock(FeatureSequenceWithBigrams.class);
        when(fs.getLength()).thenReturn(3);
        when(fs.getIndexAtPosition(0)).thenReturn(0);
        when(fs.getIndexAtPosition(1)).thenReturn(1);
        when(fs.getIndexAtPosition(2)).thenReturn(2);
        when(fs.getBiIndexAtPosition(0)).thenReturn(-1);
        when(fs.getBiIndexAtPosition(1)).thenReturn(-1);
        when(fs.getBiIndexAtPosition(2)).thenReturn(-1);
        Alphabet uniAlphabet = new Alphabet();
        uniAlphabet.lookupIndex("foo");
        uniAlphabet.lookupIndex("bar");
        uniAlphabet.lookupIndex("baz");
        when(fs.getBiAlphabet()).thenReturn(new Alphabet());

        Instance instance = new Instance(fs, null, "source1", null);
        InstanceList instanceList = mock(InstanceList.class);
        when(instanceList.size()).thenReturn(1);
        when(instanceList.get(0)).thenReturn(instance);
        when(instanceList.getDataAlphabet()).thenReturn(uniAlphabet);

        Randoms r = mock(Randoms.class);
        when(r.nextInt(2)).thenReturn(0);

        model.estimate(instanceList, 2, 0, 0, null, r);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        model.printDocumentTopics(pw, 0.0, 2);
        pw.flush();

        String output = sw.toString();
        assertTrue(output.contains("0 source1"));
    }
@Test
    public void testPrintStateOutputsData() {
        TopicalNGrams model = new TopicalNGrams(2);

        FeatureSequenceWithBigrams fs = mock(FeatureSequenceWithBigrams.class);
        when(fs.getLength()).thenReturn(2);
        when(fs.getIndexAtPosition(0)).thenReturn(0);
        when(fs.getIndexAtPosition(1)).thenReturn(1);
        when(fs.getBiIndexAtPosition(0)).thenReturn(-1);
        when(fs.getBiIndexAtPosition(1)).thenReturn(-1);
        Alphabet uniAlphabet = new Alphabet();
        uniAlphabet.lookupIndex("a");
        uniAlphabet.lookupIndex("b");
        when(fs.getBiAlphabet()).thenReturn(new Alphabet());

        Instance instance = new Instance(fs, null, "docX", null);
        InstanceList instanceList = mock(InstanceList.class);
        when(instanceList.size()).thenReturn(1);
        when(instanceList.get(0)).thenReturn(instance);
        when(instanceList.getDataAlphabet()).thenReturn(uniAlphabet);

        Randoms r = mock(Randoms.class);
        when(r.nextInt(2)).thenReturn(0);

        model.estimate(instanceList, 1, 0, 0, null, r);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        model.printState(pw);
        pw.flush();
        String output = sw.toString();
        assertTrue(output.contains("#doc pos typeindex type"));
    }
@Test
    public void testEstimateHandlesEmptyInstanceListGracefully() {
        TopicalNGrams model = new TopicalNGrams(3);

        InstanceList instanceList = mock(InstanceList.class);
        when(instanceList.size()).thenReturn(0);

        Randoms r = mock(Randoms.class);

        try {
            model.estimate(instanceList, 1, 0, 0, null, r);
        } catch (Exception e) {
            fail("Should not throw exception on empty input: " + e.getMessage());
        }
    }
@Test
    public void testModelSerializationAndDeserialization() throws IOException, ClassNotFoundException {
        TopicalNGrams model = new TopicalNGrams(3);

        FeatureSequenceWithBigrams fs = mock(FeatureSequenceWithBigrams.class);
        when(fs.getLength()).thenReturn(3);
        when(fs.getIndexAtPosition(0)).thenReturn(0);
        when(fs.getIndexAtPosition(1)).thenReturn(1);
        when(fs.getIndexAtPosition(2)).thenReturn(2);
        when(fs.getBiIndexAtPosition(0)).thenReturn(-1);
        when(fs.getBiIndexAtPosition(1)).thenReturn(-1);
        when(fs.getBiIndexAtPosition(2)).thenReturn(-1);
        Alphabet uniAlphabet = new Alphabet();
        uniAlphabet.lookupIndex("a");
        uniAlphabet.lookupIndex("b");
        uniAlphabet.lookupIndex("c");
        Alphabet biAlphabet = new Alphabet();
        biAlphabet.lookupIndex("a_b");
        when(fs.getBiAlphabet()).thenReturn(biAlphabet);

        Instance instance = new Instance(fs, null, "docY", null);
        InstanceList instanceList = mock(InstanceList.class);
        when(instanceList.size()).thenReturn(1);
        when(instanceList.get(0)).thenReturn(instance);
        when(instanceList.getDataAlphabet()).thenReturn(uniAlphabet);

        Randoms r = mock(Randoms.class);
        when(r.nextInt(3)).thenReturn(1);

        model.estimate(instanceList, 2, 0, 0, null, r);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(model);
        oos.close();

        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bis);
        TopicalNGrams deserialized = (TopicalNGrams) ois.readObject();

        assertNotNull(deserialized);
    }
@Test
    public void testEstimateWithDocumentHavingNoBigramPossible() {
        TopicalNGrams model = new TopicalNGrams(2);

        FeatureSequenceWithBigrams fs = mock(FeatureSequenceWithBigrams.class);
        when(fs.getLength()).thenReturn(2);
        when(fs.getIndexAtPosition(0)).thenReturn(0);
        when(fs.getIndexAtPosition(1)).thenReturn(1);
        when(fs.getBiIndexAtPosition(0)).thenReturn(-1);
        when(fs.getBiIndexAtPosition(1)).thenReturn(-1);

        Alphabet uniAlphabet = new Alphabet();
        uniAlphabet.lookupIndex("one");
        uniAlphabet.lookupIndex("two");
        when(fs.getBiAlphabet()).thenReturn(new Alphabet());

        Instance instance = new Instance(fs, null, "noBigramDoc", null);
        InstanceList instanceList = mock(InstanceList.class);
        when(instanceList.size()).thenReturn(1);
        when(instanceList.get(0)).thenReturn(instance);
        when(instanceList.getDataAlphabet()).thenReturn(uniAlphabet);

        Randoms randoms = mock(Randoms.class);
        when(randoms.nextInt(2)).thenReturn(0);

        model.estimate(instanceList, 1, 0, 0, null, randoms);

        assertNotNull(instanceList);
    }
@Test
    public void testEstimateWithDocumentHavingAllBigramsPossible() {
        TopicalNGrams model = new TopicalNGrams(2);

        FeatureSequenceWithBigrams fs = mock(FeatureSequenceWithBigrams.class);
        when(fs.getLength()).thenReturn(2);
        when(fs.getIndexAtPosition(0)).thenReturn(0);
        when(fs.getIndexAtPosition(1)).thenReturn(1);
        when(fs.getBiIndexAtPosition(0)).thenReturn(0);
        when(fs.getBiIndexAtPosition(1)).thenReturn(0);

        Alphabet uniAlphabet = new Alphabet();
        uniAlphabet.lookupIndex("alpha");
        uniAlphabet.lookupIndex("beta");

        Alphabet biAlphabet = new Alphabet();
        biAlphabet.lookupIndex("alpha_beta");
        when(fs.getBiAlphabet()).thenReturn(biAlphabet);

        Instance ins = new Instance(fs, null, "allBigram", null);
        InstanceList list = mock(InstanceList.class);
        when(list.size()).thenReturn(1);
        when(list.get(0)).thenReturn(ins);
        when(list.getDataAlphabet()).thenReturn(uniAlphabet);

        Randoms randoms = mock(Randoms.class);
        when(randoms.nextInt(2)).thenReturn(1);

        model.estimate(list, 2, 0, 0, null, randoms);

        assertNotNull(list);
    }
@Test
    public void testPrintDocumentTopicsWithThresholdGreaterThanMaxTopicProbability() {
        TopicalNGrams model = new TopicalNGrams(2);

        FeatureSequenceWithBigrams fs = mock(FeatureSequenceWithBigrams.class);
        when(fs.getLength()).thenReturn(2);
        when(fs.getIndexAtPosition(0)).thenReturn(0);
        when(fs.getIndexAtPosition(1)).thenReturn(1);
        when(fs.getBiIndexAtPosition(0)).thenReturn(-1);
        when(fs.getBiIndexAtPosition(1)).thenReturn(-1);

        Alphabet uniAlphabet = new Alphabet();
        uniAlphabet.lookupIndex("a");
        uniAlphabet.lookupIndex("b");
        when(fs.getBiAlphabet()).thenReturn(new Alphabet());

        Instance instance = new Instance(fs, null, "docZ", null);
        InstanceList ilist = mock(InstanceList.class);
        when(ilist.size()).thenReturn(1);
        when(ilist.get(0)).thenReturn(instance);
        when(ilist.getDataAlphabet()).thenReturn(uniAlphabet);

        Randoms randoms = mock(Randoms.class);
        when(randoms.nextInt(2)).thenReturn(0);

        model.estimate(ilist, 2, 0, 0, null, randoms);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        model.printDocumentTopics(pw, 1.0, 2); 
        pw.flush();

        String output = sw.toString();
        assertTrue(output.contains("0 docZ")); 
    }
@Test
    public void testPrintTopWordsWithZeroTopics() {
        TopicalNGrams model = new TopicalNGrams(1);

        FeatureSequenceWithBigrams fs = mock(FeatureSequenceWithBigrams.class);
        when(fs.getLength()).thenReturn(1);
        when(fs.getIndexAtPosition(0)).thenReturn(0);
        when(fs.getBiIndexAtPosition(0)).thenReturn(-1);

        Alphabet uniAlphabet = new Alphabet();
        uniAlphabet.lookupIndex("zz");
        when(fs.getBiAlphabet()).thenReturn(new Alphabet());

        Instance instance = new Instance(fs, null, "docSmall", null);
        InstanceList ilist = mock(InstanceList.class);
        when(ilist.size()).thenReturn(1);
        when(ilist.get(0)).thenReturn(instance);
        when(ilist.getDataAlphabet()).thenReturn(uniAlphabet);
        Randoms randoms = mock(Randoms.class);
        when(randoms.nextInt(1)).thenReturn(0);

        model.estimate(ilist, 1, 0, 0, null, randoms);

        try {
            model.printTopWords(0, true); 
        } catch (Exception e) {
            fail("Top words print should not throw on zero numWords");
        }
    }
@Test
    public void testPrintDocumentTopicsWithNegativeMaxReturnsAllTopics() {
        TopicalNGrams model = new TopicalNGrams(3);

        FeatureSequenceWithBigrams fs = mock(FeatureSequenceWithBigrams.class);
        when(fs.getLength()).thenReturn(3);
        when(fs.getIndexAtPosition(0)).thenReturn(0);
        when(fs.getIndexAtPosition(1)).thenReturn(1);
        when(fs.getIndexAtPosition(2)).thenReturn(2);
        when(fs.getBiIndexAtPosition(0)).thenReturn(-1);
        when(fs.getBiIndexAtPosition(1)).thenReturn(-1);
        when(fs.getBiIndexAtPosition(2)).thenReturn(-1);

        Alphabet uniAlphabet = new Alphabet();
        uniAlphabet.lookupIndex("aaa");
        uniAlphabet.lookupIndex("bbb");
        uniAlphabet.lookupIndex("ccc");
        when(fs.getBiAlphabet()).thenReturn(new Alphabet());

        Instance instance = new Instance(fs, null, "fullDoc", null);
        InstanceList ilist = mock(InstanceList.class);
        when(ilist.size()).thenReturn(1);
        when(ilist.get(0)).thenReturn(instance);
        when(ilist.getDataAlphabet()).thenReturn(uniAlphabet);

        Randoms randoms = mock(Randoms.class);
        when(randoms.nextInt(3)).thenReturn(2);

        model.estimate(ilist, 2, 0, 0, null, randoms);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        model.printDocumentTopics(pw, 0.0, -1); 
        pw.flush();

        String out = sw.toString();
        assertTrue(out.contains("0 fullDoc"));
    }
@Test
    public void testWriteMethodHandlesIOException() {
        TopicalNGrams model = new TopicalNGrams(2);

        File file = mock(File.class);
        try {
            FileOutputStream fos = mock(FileOutputStream.class);
            whenNew(FileOutputStream.class).withArguments(file).thenThrow(new IOException("Simulated IO failure"));
            model.write(file);
        } catch (Exception e) {
            
        }
    }
@Test
    public void testPrintStateOutputIncludesAllKeywords() {
        TopicalNGrams model = new TopicalNGrams(1);

        FeatureSequenceWithBigrams fs = mock(FeatureSequenceWithBigrams.class);
        when(fs.getLength()).thenReturn(1);
        when(fs.getIndexAtPosition(0)).thenReturn(0);
        when(fs.getBiIndexAtPosition(0)).thenReturn(-1);

        Alphabet uniAlphabet = new Alphabet();
        uniAlphabet.lookupIndex("keyword");
        when(fs.getBiAlphabet()).thenReturn(new Alphabet());

        Instance instance = new Instance(fs, null, "anyDoc", null);
        InstanceList ilist = mock(InstanceList.class);
        when(ilist.size()).thenReturn(1);
        when(ilist.get(0)).thenReturn(instance);
        when(ilist.getDataAlphabet()).thenReturn(uniAlphabet);
        Randoms randoms = mock(Randoms.class);
        when(randoms.nextInt(1)).thenReturn(0);

        model.estimate(ilist, 1, 0, 0, null, randoms);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        model.printState(pw);
        pw.flush();

        String output = sw.toString();
        assertTrue(output.contains("#doc pos typeindex type"));
        assertTrue(output.contains("keyword"));
    }
@Test
    public void testEstimateWithSingleTokenDocumentAndBigramAllowed() {
        TopicalNGrams model = new TopicalNGrams(2);

        FeatureSequenceWithBigrams fs = mock(FeatureSequenceWithBigrams.class);
        when(fs.getLength()).thenReturn(1);
        when(fs.getIndexAtPosition(0)).thenReturn(0);
        when(fs.getBiIndexAtPosition(0)).thenReturn(0); 

        Alphabet uniAlphabet = new Alphabet();
        uniAlphabet.lookupIndex("word0");

        Alphabet biAlphabet = new Alphabet();
        biAlphabet.lookupIndex("dummy");

        when(fs.getBiAlphabet()).thenReturn(biAlphabet);

        Instance instance = new Instance(fs, null, "doc1", null);
        InstanceList instanceList = mock(InstanceList.class);
        when(instanceList.size()).thenReturn(1);
        when(instanceList.get(0)).thenReturn(instance);
        when(instanceList.getDataAlphabet()).thenReturn(uniAlphabet);

        Randoms r = mock(Randoms.class);
        when(r.nextInt(2)).thenReturn(1); 

        model.estimate(instanceList, 1, 0, 0, null, r);

        assertNotNull(instanceList.get(0));
    }
@Test
    public void testEstimateWithSingleDocumentAndAlternatingBigramPossibilities() {
        TopicalNGrams model = new TopicalNGrams(2);

        FeatureSequenceWithBigrams fs = mock(FeatureSequenceWithBigrams.class);
        when(fs.getLength()).thenReturn(4);
        when(fs.getIndexAtPosition(0)).thenReturn(0);
        when(fs.getIndexAtPosition(1)).thenReturn(1);
        when(fs.getIndexAtPosition(2)).thenReturn(2);
        when(fs.getIndexAtPosition(3)).thenReturn(3);
        when(fs.getBiIndexAtPosition(0)).thenReturn(-1);
        when(fs.getBiIndexAtPosition(1)).thenReturn(0);
        when(fs.getBiIndexAtPosition(2)).thenReturn(-1);
        when(fs.getBiIndexAtPosition(3)).thenReturn(0);

        Alphabet uniAlphabet = new Alphabet();
        uniAlphabet.lookupIndex("w0");
        uniAlphabet.lookupIndex("w1");
        uniAlphabet.lookupIndex("w2");
        uniAlphabet.lookupIndex("w3");

        Alphabet biAlphabet = new Alphabet();
        biAlphabet.lookupIndex("w1_w2");
        biAlphabet.lookupIndex("w3");

        when(fs.getBiAlphabet()).thenReturn(biAlphabet);

        Instance instance = new Instance(fs, null, "testDoc", null);
        InstanceList instanceList = mock(InstanceList.class);
        when(instanceList.size()).thenReturn(1);
        when(instanceList.get(0)).thenReturn(instance);
        when(instanceList.getDataAlphabet()).thenReturn(uniAlphabet);

        Randoms r = mock(Randoms.class);
        when(r.nextInt(2)).thenReturn(0).thenReturn(1).thenReturn(0).thenReturn(1);

        model.estimate(instanceList, 1, 0, 0, null, r);

        assertNotNull(instanceList);
    }
@Test
    public void testPrintStateToFileAndReadBack() throws IOException {
        TopicalNGrams model = new TopicalNGrams(2);

        FeatureSequenceWithBigrams fs = mock(FeatureSequenceWithBigrams.class);
        when(fs.getLength()).thenReturn(2);
        when(fs.getIndexAtPosition(0)).thenReturn(0);
        when(fs.getIndexAtPosition(1)).thenReturn(1);
        when(fs.getBiIndexAtPosition(0)).thenReturn(-1);
        when(fs.getBiIndexAtPosition(1)).thenReturn(-1);

        Alphabet uniAlphabet = new Alphabet();
        uniAlphabet.lookupIndex("w0");
        uniAlphabet.lookupIndex("w1");
        when(fs.getBiAlphabet()).thenReturn(new Alphabet());

        Instance instance = new Instance(fs, null, "stateDoc", null);
        InstanceList ilist = mock(InstanceList.class);
        when(ilist.size()).thenReturn(1);
        when(ilist.get(0)).thenReturn(instance);
        when(ilist.getDataAlphabet()).thenReturn(uniAlphabet);

        Randoms r = mock(Randoms.class);
        when(r.nextInt(2)).thenReturn(0);

        model.estimate(ilist, 1, 0, 0, null, r);

        File tempFile = File.createTempFile("test-state", ".txt");
        tempFile.deleteOnExit();

        model.printState(tempFile);

        BufferedReader reader = new BufferedReader(new FileReader(tempFile));
        String header = reader.readLine();
        assertTrue(header.contains("#doc pos typeindex type"));
        reader.close();
    }
@Test
    public void testPrintDocumentTopicsWithMaxZero() {
        TopicalNGrams model = new TopicalNGrams(3);

        FeatureSequenceWithBigrams fs = mock(FeatureSequenceWithBigrams.class);
        when(fs.getLength()).thenReturn(3);
        when(fs.getIndexAtPosition(0)).thenReturn(0);
        when(fs.getIndexAtPosition(1)).thenReturn(1);
        when(fs.getIndexAtPosition(2)).thenReturn(2);
        when(fs.getBiIndexAtPosition(0)).thenReturn(-1);
        when(fs.getBiIndexAtPosition(1)).thenReturn(-1);
        when(fs.getBiIndexAtPosition(2)).thenReturn(-1);

        Alphabet uniAlphabet = new Alphabet();
        uniAlphabet.lookupIndex("x");
        uniAlphabet.lookupIndex("y");
        uniAlphabet.lookupIndex("z");
        when(fs.getBiAlphabet()).thenReturn(new Alphabet());

        Instance instance = new Instance(fs, null, "maxZeroDoc", null);
        InstanceList ilist = mock(InstanceList.class);
        when(ilist.size()).thenReturn(1);
        when(ilist.get(0)).thenReturn(instance);
        when(ilist.getDataAlphabet()).thenReturn(uniAlphabet);

        Randoms r = mock(Randoms.class);
        when(r.nextInt(3)).thenReturn(1);

        model.estimate(ilist, 1, 0, 0, null, r);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        model.printDocumentTopics(pw, 0.0, 0); 
        pw.flush();

        String out = sw.toString();
        assertTrue(out.contains("0 maxZeroDoc"));
    }
@Test
    public void testEstimateWithNullOutputModelFilenameAndSpecifiedInterval() {
        TopicalNGrams model = new TopicalNGrams(2);

        FeatureSequenceWithBigrams fs = mock(FeatureSequenceWithBigrams.class);
        when(fs.getLength()).thenReturn(3);
        when(fs.getIndexAtPosition(0)).thenReturn(0);
        when(fs.getIndexAtPosition(1)).thenReturn(1);
        when(fs.getIndexAtPosition(2)).thenReturn(2);
        when(fs.getBiIndexAtPosition(0)).thenReturn(-1);
        when(fs.getBiIndexAtPosition(1)).thenReturn(-1);
        when(fs.getBiIndexAtPosition(2)).thenReturn(-1);

        Alphabet uniAlphabet = new Alphabet();
        uniAlphabet.lookupIndex("a");
        uniAlphabet.lookupIndex("b");
        uniAlphabet.lookupIndex("c");
        when(fs.getBiAlphabet()).thenReturn(new Alphabet());

        Instance instance = new Instance(fs, null, "nullModelDoc", null);
        InstanceList ilist = mock(InstanceList.class);
        when(ilist.size()).thenReturn(1);
        when(ilist.get(0)).thenReturn(instance);
        when(ilist.getDataAlphabet()).thenReturn(uniAlphabet);

        Randoms r = mock(Randoms.class);
        when(r.nextInt(2)).thenReturn(1);

        model.estimate(ilist, 3, 0, 1, null, r);  

        assertNotNull(ilist);
    }
@Test
    public void testEstimateWithNullBiAlphabetInFeatureSequenceThrowsException() {
        TopicalNGrams model = new TopicalNGrams(2);

        FeatureSequenceWithBigrams fs = mock(FeatureSequenceWithBigrams.class);
        when(fs.getLength()).thenReturn(2);
        when(fs.getIndexAtPosition(0)).thenReturn(0);
        when(fs.getIndexAtPosition(1)).thenReturn(1);
        when(fs.getBiIndexAtPosition(0)).thenReturn(-1);
        when(fs.getBiIndexAtPosition(1)).thenReturn(-1);
        when(fs.getBiAlphabet()).thenReturn(null); 

        Alphabet uniAlphabet = new Alphabet();
        uniAlphabet.lookupIndex("w0");
        uniAlphabet.lookupIndex("w1");

        Instance instance = new Instance(fs, null, "doc-null-bi", null);
        InstanceList ilist = mock(InstanceList.class);
        when(ilist.size()).thenReturn(1);
        when(ilist.get(0)).thenReturn(instance);
        when(ilist.getDataAlphabet()).thenReturn(uniAlphabet);

        Randoms randoms = mock(Randoms.class);
        when(randoms.nextInt(2)).thenReturn(0);

        try {
            model.estimate(ilist, 1, 0, 0, null, randoms);
            fail("Expected NullPointerException due to null biAlphabet");
        } catch (NullPointerException expected) {
            assertTrue(true);
        }
    }
@Test
    public void testPrintTopWordsWithNegativeNumWords() {
        TopicalNGrams model = new TopicalNGrams(1);

        FeatureSequenceWithBigrams fs = mock(FeatureSequenceWithBigrams.class);
        when(fs.getLength()).thenReturn(2);
        when(fs.getIndexAtPosition(0)).thenReturn(0);
        when(fs.getIndexAtPosition(1)).thenReturn(1);
        when(fs.getBiIndexAtPosition(0)).thenReturn(-1);
        when(fs.getBiIndexAtPosition(1)).thenReturn(-1);

        Alphabet uniAlphabet = new Alphabet();
        uniAlphabet.lookupIndex("neg");
        uniAlphabet.lookupIndex("word");
        when(fs.getBiAlphabet()).thenReturn(new Alphabet());

        Instance instance = new Instance(fs, null, "doc-negative", null);
        InstanceList ilist = mock(InstanceList.class);
        when(ilist.size()).thenReturn(1);
        when(ilist.get(0)).thenReturn(instance);
        when(ilist.getDataAlphabet()).thenReturn(uniAlphabet);

        Randoms randoms = mock(Randoms.class);
        when(randoms.nextInt(1)).thenReturn(0);

        model.estimate(ilist, 1, 0, 0, null, randoms);

        try {
            model.printTopWords(-3, false); 
        } catch (Exception e) {
            fail("printTopWords with negative numWords should not throw: " + e.getMessage());
        }
    }
@Test
    public void testEstimateWithNullInstanceListDoesNotThrow() {
        TopicalNGrams model = new TopicalNGrams(2);
        Randoms r = mock(Randoms.class);

        try {
            model.estimate(null, 1, 0, 0, null, r);
            fail("Expected NullPointerException for null input list");
        } catch (NullPointerException expected) {
            assertTrue(true);
        }
    }
@Test
    public void testEstimateWithNullRandomsThrowsException() {
        TopicalNGrams model = new TopicalNGrams(2);

        FeatureSequenceWithBigrams fs = mock(FeatureSequenceWithBigrams.class);
        when(fs.getLength()).thenReturn(2);
        when(fs.getIndexAtPosition(0)).thenReturn(0);
        when(fs.getIndexAtPosition(1)).thenReturn(1);
        when(fs.getBiIndexAtPosition(0)).thenReturn(-1);
        when(fs.getBiIndexAtPosition(1)).thenReturn(-1);
        when(fs.getBiAlphabet()).thenReturn(new Alphabet());

        Alphabet alpha = new Alphabet();
        alpha.lookupIndex("foo");
        alpha.lookupIndex("bar");

        Instance i = new Instance(fs, null, "docX", null);
        InstanceList ilist = mock(InstanceList.class);
        when(ilist.size()).thenReturn(1);
        when(ilist.get(0)).thenReturn(i);
        when(ilist.getDataAlphabet()).thenReturn(alpha);

        try {
            model.estimate(ilist, 1, 0, 0, null, null);
            fail("Expected NullPointerException for null Randoms");
        } catch (NullPointerException expected) {
            assertTrue(true);
        }
    }
@Test
    public void testPrintDocumentTopicsWithNonStringSource() {
        TopicalNGrams model = new TopicalNGrams(1);

        FeatureSequenceWithBigrams fs = mock(FeatureSequenceWithBigrams.class);
        when(fs.getLength()).thenReturn(1);
        when(fs.getIndexAtPosition(0)).thenReturn(0);
        when(fs.getBiIndexAtPosition(0)).thenReturn(-1);

        Alphabet a = new Alphabet();
        a.lookupIndex("non-str");

        when(fs.getBiAlphabet()).thenReturn(new Alphabet());

        Object nonStringSource = new Object() {
            @Override
            public String toString() {
                return "nonStringSourceObject";
            }
        };

        Instance i = new Instance(fs, null, nonStringSource, null);
        InstanceList ilist = mock(InstanceList.class);
        when(ilist.size()).thenReturn(1);
        when(ilist.get(0)).thenReturn(i);
        when(ilist.getDataAlphabet()).thenReturn(a);

        Randoms rand = mock(Randoms.class);
        when(rand.nextInt(1)).thenReturn(0);

        model.estimate(ilist, 1, 0, 0, null, rand);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        model.printDocumentTopics(pw, 0.0, 1);
        pw.flush();

        String output = sw.toString();
        assertTrue(output.contains("nonStringSourceObject"));
    }
@Test
    public void testPrintDocumentTopicsWithEmptyTopicsArray() throws Exception {
        TopicalNGrams model = new TopicalNGrams(2);

        FeatureSequenceWithBigrams fs = mock(FeatureSequenceWithBigrams.class);
        when(fs.getLength()).thenReturn(0);

        Alphabet alphabet = new Alphabet();

        when(fs.getBiAlphabet()).thenReturn(new Alphabet());

        Instance i = new Instance(fs, null, "emptyDoc", null);
        InstanceList ilist = mock(InstanceList.class);
        when(ilist.size()).thenReturn(1);
        when(ilist.get(0)).thenReturn(i);
        when(ilist.getDataAlphabet()).thenReturn(alphabet);

        Randoms rand = mock(Randoms.class);

        model.estimate(ilist, 1, 0, 0, null, rand);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        model.printDocumentTopics(pw, 0.0, -1);
        pw.flush();

        String output = sw.toString();
        assertTrue(output.contains("emptyDoc"));
    }
@Test
    public void testWriteMethodWithUnwritableFile() {
        TopicalNGrams model = new TopicalNGrams(1);

        File unwritableFile = new File("/root/unwritable-model.ser");
        
        try {
            model.write(unwritableFile);
        } catch (Exception e) {
            
            fail("write() should not propagate exception");
        }
    }
@Test
    public void testEstimateWithZeroLengthFeatureSequence() {
        TopicalNGrams model = new TopicalNGrams(2);

        FeatureSequenceWithBigrams fs = mock(FeatureSequenceWithBigrams.class);
        when(fs.getLength()).thenReturn(0);
        when(fs.getBiAlphabet()).thenReturn(new Alphabet());

        Alphabet uniAlphabet = new Alphabet();

        Instance instance = new Instance(fs, null, "emptyDoc", null);
        InstanceList instanceList = mock(InstanceList.class);
        when(instanceList.size()).thenReturn(1);
        when(instanceList.get(0)).thenReturn(instance);
        when(instanceList.getDataAlphabet()).thenReturn(uniAlphabet);

        Randoms randoms = mock(Randoms.class);

        model.estimate(instanceList, 1, 0, 0, null, randoms);

        assertNotNull(instanceList.get(0));
    }
@Test
    public void testPrintDocumentTopicsWhenDocHasOnlyOneTopic() {
        TopicalNGrams model = new TopicalNGrams(1);

        FeatureSequenceWithBigrams fs = mock(FeatureSequenceWithBigrams.class);
        when(fs.getLength()).thenReturn(1);
        when(fs.getIndexAtPosition(0)).thenReturn(0);
        when(fs.getBiIndexAtPosition(0)).thenReturn(-1);
        when(fs.getBiAlphabet()).thenReturn(new Alphabet());

        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("singleTopicWord");

        Instance instance = new Instance(fs, null, "singleTopicDoc", null);
        InstanceList ilist = mock(InstanceList.class);
        when(ilist.size()).thenReturn(1);
        when(ilist.get(0)).thenReturn(instance);
        when(ilist.getDataAlphabet()).thenReturn(alphabet);

        Randoms rand = mock(Randoms.class);
        when(rand.nextInt(1)).thenReturn(0);

        model.estimate(ilist, 1, 0, 0, null, rand);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        model.printDocumentTopics(pw, 0.0, 1);
        pw.flush();

        String output = sw.toString();
        assertTrue(output.contains("singleTopicDoc"));
    }
@Test
    public void testPrintStateWithSingleTokenAndBigram() throws IOException {
        TopicalNGrams model = new TopicalNGrams(1);

        FeatureSequenceWithBigrams fs = mock(FeatureSequenceWithBigrams.class);
        when(fs.getLength()).thenReturn(1);
        when(fs.getIndexAtPosition(0)).thenReturn(0);
        when(fs.getBiIndexAtPosition(0)).thenReturn(0); 
        Alphabet uniAlphabet = new Alphabet();
        uniAlphabet.lookupIndex("bigramToken");

        Alphabet biAlphabet = new Alphabet();
        biAlphabet.lookupIndex("bigramToken");

        when(fs.getBiAlphabet()).thenReturn(biAlphabet);

        Instance inst = new Instance(fs, null, "gramTest", null);
        InstanceList ilist = mock(InstanceList.class);
        when(ilist.size()).thenReturn(1);
        when(ilist.get(0)).thenReturn(inst);
        when(ilist.getDataAlphabet()).thenReturn(uniAlphabet);

        Randoms r = mock(Randoms.class);
        when(r.nextInt(1)).thenReturn(0);

        model.estimate(ilist, 1, 0, 0, null, r);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        model.printState(pw);
        pw.flush();

        String output = sw.toString();
        assertTrue(output.contains("gramTest"));
        assertTrue(output.contains("bigramToken"));
    }
@Test
    public void testPrintDocumentTopicsWithThresholdOne() {
        TopicalNGrams model = new TopicalNGrams(2);

        FeatureSequenceWithBigrams fs = mock(FeatureSequenceWithBigrams.class);
        when(fs.getLength()).thenReturn(2);
        when(fs.getIndexAtPosition(0)).thenReturn(0);
        when(fs.getIndexAtPosition(1)).thenReturn(1);
        when(fs.getBiIndexAtPosition(0)).thenReturn(-1);
        when(fs.getBiIndexAtPosition(1)).thenReturn(-1);
        when(fs.getBiAlphabet()).thenReturn(new Alphabet());

        Alphabet a = new Alphabet();
        a.lookupIndex("one");
        a.lookupIndex("two");

        Instance inst = new Instance(fs, null, "exactThresholdDoc", null);
        InstanceList list = mock(InstanceList.class);
        when(list.size()).thenReturn(1);
        when(list.get(0)).thenReturn(inst);
        when(list.getDataAlphabet()).thenReturn(a);

        Randoms rand = mock(Randoms.class);
        when(rand.nextInt(2)).thenReturn(0).thenReturn(0);

        model.estimate(list, 1, 0, 0, null, rand);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        model.printDocumentTopics(pw, 1.0, 1); 
        pw.flush();

        String output = sw.toString();
        assertTrue(output.contains("exactThresholdDoc"));
    }
@Test
    public void testEstimateWithVocabSizeZero() {
        TopicalNGrams model = new TopicalNGrams(2);

        FeatureSequenceWithBigrams fs = mock(FeatureSequenceWithBigrams.class);
        when(fs.getLength()).thenReturn(1);
        when(fs.getIndexAtPosition(0)).thenReturn(0);
        when(fs.getBiIndexAtPosition(0)).thenReturn(-1);

        Alphabet emptyAlphabet = new Alphabet(); 

        when(fs.getBiAlphabet()).thenReturn(new Alphabet());

        Instance i = new Instance(fs, null, "zeroVocabDoc", null);
        InstanceList ilist = mock(InstanceList.class);
        when(ilist.size()).thenReturn(1);
        when(ilist.get(0)).thenReturn(i);
        when(ilist.getDataAlphabet()).thenReturn(emptyAlphabet);

        Randoms rand = mock(Randoms.class);
        when(rand.nextInt(2)).thenReturn(0);

        model.estimate(ilist, 1, 0, 0, null, rand);

        assertNotNull(ilist);
    }
@Test
    public void testPrintTopWordsWithUseNewLinesTrueAndZeroCounts() {
        TopicalNGrams model = new TopicalNGrams(1);

        FeatureSequenceWithBigrams fs = mock(FeatureSequenceWithBigrams.class);
        when(fs.getLength()).thenReturn(1);
        when(fs.getIndexAtPosition(0)).thenReturn(0);
        when(fs.getBiIndexAtPosition(0)).thenReturn(-1);

        Alphabet uniAlphabet = new Alphabet();
        uniAlphabet.lookupIndex("empty");

        Alphabet biAlphabet = new Alphabet();

        when(fs.getBiAlphabet()).thenReturn(biAlphabet);

        Instance inst = new Instance(fs, null, "doc", null);
        InstanceList ilist = mock(InstanceList.class);
        when(ilist.size()).thenReturn(1);
        when(ilist.get(0)).thenReturn(inst);
        when(ilist.getDataAlphabet()).thenReturn(uniAlphabet);

        Randoms rand = mock(Randoms.class);
        when(rand.nextInt(1)).thenReturn(0);

        model.estimate(ilist, 1, 0, 0, null, rand);

        try {
            model.printTopWords(5, true);
        } catch (Exception e) {
            fail("printTopWords(true) should not throw when counts are zero");
        }
    }
@Test
    public void testPrintStateWithOnlyBigramTokensInDocument() {
        TopicalNGrams model = new TopicalNGrams(1);

        FeatureSequenceWithBigrams fs = mock(FeatureSequenceWithBigrams.class);
        when(fs.getLength()).thenReturn(2);
        when(fs.getIndexAtPosition(0)).thenReturn(0);
        when(fs.getIndexAtPosition(1)).thenReturn(1);
        when(fs.getBiIndexAtPosition(0)).thenReturn(0);
        when(fs.getBiIndexAtPosition(1)).thenReturn(0);

        Alphabet uniAlphabet = new Alphabet();
        uniAlphabet.lookupIndex("first");
        uniAlphabet.lookupIndex("second");

        Alphabet biAlphabet = new Alphabet();
        biAlphabet.lookupIndex("first_second");

        when(fs.getBiAlphabet()).thenReturn(biAlphabet);

        Instance i = new Instance(fs, null, "onlyBigrams", null);
        InstanceList ilist = mock(InstanceList.class);
        when(ilist.size()).thenReturn(1);
        when(ilist.get(0)).thenReturn(i);
        when(ilist.getDataAlphabet()).thenReturn(uniAlphabet);

        Randoms r = mock(Randoms.class);
        when(r.nextInt(1)).thenReturn(0);

        model.estimate(ilist, 1, 0, 0, null, r);

        StringWriter writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);
        model.printState(pw);
        pw.flush();

        String output = writer.toString();
        assertTrue(output.contains("first"));
        assertTrue(output.contains("second"));
    }
@Test
    public void testPrintDocumentTopicsWithNoTokensInDocument() {
        TopicalNGrams model = new TopicalNGrams(2);

        FeatureSequenceWithBigrams fs = mock(FeatureSequenceWithBigrams.class);
        when(fs.getLength()).thenReturn(0);
        when(fs.getBiAlphabet()).thenReturn(new Alphabet());

        Alphabet alphabet = new Alphabet();

        Instance instance = new Instance(fs, null, "emptyTokensDoc", null);
        InstanceList list = mock(InstanceList.class);
        when(list.size()).thenReturn(1);
        when(list.get(0)).thenReturn(instance);
        when(list.getDataAlphabet()).thenReturn(alphabet);

        Randoms randoms = mock(Randoms.class);

        model.estimate(list, 1, 0, 0, null, randoms);

        StringWriter buffer = new StringWriter();
        PrintWriter pw = new PrintWriter(buffer);
        model.printDocumentTopics(pw, 0.0, 3);
        pw.flush();

        String output = buffer.toString();
        assertTrue(output.contains("emptyTokensDoc"));
    }
@Test
    public void testEstimateWithAllTokensMappedToSameIndex() {
        TopicalNGrams model = new TopicalNGrams(2);

        FeatureSequenceWithBigrams fs = mock(FeatureSequenceWithBigrams.class);
        when(fs.getLength()).thenReturn(3);
        when(fs.getIndexAtPosition(0)).thenReturn(0);
        when(fs.getIndexAtPosition(1)).thenReturn(0);
        when(fs.getIndexAtPosition(2)).thenReturn(0);
        when(fs.getBiIndexAtPosition(0)).thenReturn(-1);
        when(fs.getBiIndexAtPosition(1)).thenReturn(-1);
        when(fs.getBiIndexAtPosition(2)).thenReturn(-1);

        Alphabet uniAlphabet = new Alphabet();
        uniAlphabet.lookupIndex("repeat");

        when(fs.getBiAlphabet()).thenReturn(new Alphabet());

        Instance instance = new Instance(fs, null, "identicalTokens", null);
        InstanceList ilist = mock(InstanceList.class);
        when(ilist.size()).thenReturn(1);
        when(ilist.get(0)).thenReturn(instance);
        when(ilist.getDataAlphabet()).thenReturn(uniAlphabet);

        Randoms rand = mock(Randoms.class);
        when(rand.nextInt(2)).thenReturn(0).thenReturn(1).thenReturn(1);

        model.estimate(ilist, 3, 0, 0, null, rand);
        assertNotNull(instance);
    }
@Test
    public void testEstimateWithTopicChangeAcrossIterations() {
        TopicalNGrams model = new TopicalNGrams(2);

        FeatureSequenceWithBigrams fs = mock(FeatureSequenceWithBigrams.class);
        when(fs.getLength()).thenReturn(2);
        when(fs.getIndexAtPosition(0)).thenReturn(0);
        when(fs.getIndexAtPosition(1)).thenReturn(1);
        when(fs.getBiIndexAtPosition(0)).thenReturn(-1);
        when(fs.getBiIndexAtPosition(1)).thenReturn(-1);

        Alphabet uniAlphabet = new Alphabet();
        uniAlphabet.lookupIndex("foo");
        uniAlphabet.lookupIndex("bar");

        when(fs.getBiAlphabet()).thenReturn(new Alphabet());

        Instance instance = new Instance(fs, null, "changetopicsDoc", null);
        InstanceList ilist = mock(InstanceList.class);
        when(ilist.size()).thenReturn(1);
        when(ilist.get(0)).thenReturn(instance);
        when(ilist.getDataAlphabet()).thenReturn(uniAlphabet);

        Randoms rand = mock(Randoms.class);
        when(rand.nextInt(2)).thenReturn(0).thenReturn(1).thenReturn(0).thenReturn(1);

        model.estimate(ilist, 4, 0, 0, null, rand);
        assertNotNull(instance);
    }
@Test
    public void testEstimateWithBiTokenCountEdgeIncrementAndDecrement() {
        TopicalNGrams model = new TopicalNGrams(1);

        FeatureSequenceWithBigrams fs = mock(FeatureSequenceWithBigrams.class);
        when(fs.getLength()).thenReturn(2);
        when(fs.getIndexAtPosition(0)).thenReturn(0);
        when(fs.getIndexAtPosition(1)).thenReturn(1);
        when(fs.getBiIndexAtPosition(0)).thenReturn(0);
        when(fs.getBiIndexAtPosition(1)).thenReturn(1);

        Alphabet uniAlphabet = new Alphabet();
        uniAlphabet.lookupIndex("a");
        uniAlphabet.lookupIndex("b");

        Alphabet biAlphabet = new Alphabet();
        biAlphabet.lookupIndex("a_b");
        biAlphabet.lookupIndex("b_c");
        when(fs.getBiAlphabet()).thenReturn(biAlphabet);

        Instance instance = new Instance(fs, null, "biTokenChangeDoc", null);
        InstanceList ilist = mock(InstanceList.class);
        when(ilist.size()).thenReturn(1);
        when(ilist.get(0)).thenReturn(instance);
        when(ilist.getDataAlphabet()).thenReturn(uniAlphabet);

        Randoms rand = mock(Randoms.class);
        when(rand.nextInt(1)).thenReturn(0);

        model.estimate(ilist, 1, 0, 0, null, rand);
        assertNotNull(ilist);
    }
@Test
    public void testWriteObjectToTemporaryFileAndReadBack() throws Exception {
        TopicalNGrams model = new TopicalNGrams(2, 2.0, 0.01, 0.02, 0.03, 0.1, 0.2);

        FeatureSequenceWithBigrams fs = mock(FeatureSequenceWithBigrams.class);
        when(fs.getLength()).thenReturn(1);
        when(fs.getIndexAtPosition(0)).thenReturn(0);
        when(fs.getBiIndexAtPosition(0)).thenReturn(-1);

        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("persist");

        when(fs.getBiAlphabet()).thenReturn(new Alphabet());

        Instance instance = new Instance(fs, null, "persistDoc", null);
        InstanceList list = mock(InstanceList.class);
        when(list.size()).thenReturn(1);
        when(list.get(0)).thenReturn(instance);
        when(list.getDataAlphabet()).thenReturn(alphabet);

        Randoms rand = mock(Randoms.class);
        when(rand.nextInt(2)).thenReturn(0);

        model.estimate(list, 1, 0, 0, null, rand);

        File temp = File.createTempFile("tng-model", ".bin");
        temp.deleteOnExit();
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(temp));
        out.writeObject(model);
        out.close();

        ObjectInputStream in = new ObjectInputStream(new FileInputStream(temp));
        TopicalNGrams readBack = (TopicalNGrams) in.readObject();
        in.close();

        assertNotNull(readBack);
    }
@Test
    public void testPrintTopWordsHandlesLongPhraseConstruction() {
        TopicalNGrams model = new TopicalNGrams(1);

        FeatureSequenceWithBigrams fs = mock(FeatureSequenceWithBigrams.class);
        when(fs.getLength()).thenReturn(4);
        when(fs.getIndexAtPosition(0)).thenReturn(0);
        when(fs.getIndexAtPosition(1)).thenReturn(1);
        when(fs.getIndexAtPosition(2)).thenReturn(2);
        when(fs.getIndexAtPosition(3)).thenReturn(3);
        when(fs.getBiIndexAtPosition(0)).thenReturn(0);
        when(fs.getBiIndexAtPosition(1)).thenReturn(1);
        when(fs.getBiIndexAtPosition(2)).thenReturn(2);
        when(fs.getBiIndexAtPosition(3)).thenReturn(-1);

        Alphabet uni = new Alphabet();
        uni.lookupIndex("a");
        uni.lookupIndex("b");
        uni.lookupIndex("c");
        uni.lookupIndex("d");

        Alphabet bi = new Alphabet();
        bi.lookupIndex("a_b");
        bi.lookupIndex("b_c");
        bi.lookupIndex("c_d");

        when(fs.getBiAlphabet()).thenReturn(bi);

        Instance inst = new Instance(fs, null, "longPhraseDoc", null);
        InstanceList ilist = mock(InstanceList.class);
        when(ilist.size()).thenReturn(1);
        when(ilist.get(0)).thenReturn(inst);
        when(ilist.getDataAlphabet()).thenReturn(uni);

        Randoms r = mock(Randoms.class);
        when(r.nextInt(1)).thenReturn(0);

        model.estimate(ilist, 1, 0, 0, null, r);

        try {
            model.printTopWords(10, true);
        } catch (Exception e) {
            fail("printTopWords should handle long phrase construction: " + e.getMessage());
        }
    }
@Test
    public void testPrintTopWordsWithNoUnigramsInTopic() {
        TopicalNGrams model = new TopicalNGrams(1);

        FeatureSequenceWithBigrams fs = mock(FeatureSequenceWithBigrams.class);
        when(fs.getLength()).thenReturn(1);
        when(fs.getIndexAtPosition(0)).thenReturn(0);
        when(fs.getBiIndexAtPosition(0)).thenReturn(0); 

        Alphabet a = new Alphabet();
        a.lookupIndex("onlyBigram");

        Alphabet bi = new Alphabet();
        bi.lookupIndex("onlyBigram");

        when(fs.getBiAlphabet()).thenReturn(bi);

        Instance instance = new Instance(fs, null, "noUnigrams", null);
        InstanceList list = mock(InstanceList.class);
        when(list.size()).thenReturn(1);
        when(list.get(0)).thenReturn(instance);
        when(list.getDataAlphabet()).thenReturn(a);

        Randoms rand = mock(Randoms.class);
        when(rand.nextInt(1)).thenReturn(0);

        model.estimate(list, 1, 0, 0, null, rand);

        model.printTopWords(5, true);
    }
@Test
    public void testEstimateWithMinBoundaryValuesForTopicsAndAlphas() {
        TopicalNGrams model = new TopicalNGrams(1, 0.01, 0.01, 0.01, 0.01, 0.01, 0.01);

        FeatureSequenceWithBigrams fs = mock(FeatureSequenceWithBigrams.class);
        when(fs.getLength()).thenReturn(1);
        when(fs.getIndexAtPosition(0)).thenReturn(0);
        when(fs.getBiIndexAtPosition(0)).thenReturn(-1);

        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("a");

        when(fs.getBiAlphabet()).thenReturn(new Alphabet());

        Instance instance = new Instance(fs, null, "minTestDoc", null);
        InstanceList instanceList = mock(InstanceList.class);
        when(instanceList.size()).thenReturn(1);
        when(instanceList.get(0)).thenReturn(instance);
        when(instanceList.getDataAlphabet()).thenReturn(alphabet);

        Randoms randoms = mock(Randoms.class);
        when(randoms.nextInt(1)).thenReturn(0);

        model.estimate(instanceList, 1, 0, 0, null, randoms);
        assertNotNull(instance);
    }
@Test
    public void testEstimateWithMaxAlphabetSize() {
        TopicalNGrams model = new TopicalNGrams(2);

        FeatureSequenceWithBigrams fs = mock(FeatureSequenceWithBigrams.class);
        when(fs.getLength()).thenReturn(10);
        for (int i = 0; i < 10; i++) {
            when(fs.getIndexAtPosition(i)).thenReturn(i);
            when(fs.getBiIndexAtPosition(i)).thenReturn(-1);
        }

        Alphabet alphabet = new Alphabet();
        for (int i = 0; i < 1000; i++) {
            alphabet.lookupIndex("word" + i);
        }

        Alphabet biAlphabet = new Alphabet();
        for (int i = 0; i < 500; i++) {
            biAlphabet.lookupIndex("bigram" + i);
        }

        when(fs.getBiAlphabet()).thenReturn(biAlphabet);

        Instance instance = new Instance(fs, null, "largeAlphabetDoc", null);
        InstanceList dataset = mock(InstanceList.class);
        when(dataset.size()).thenReturn(1);
        when(dataset.get(0)).thenReturn(instance);
        when(dataset.getDataAlphabet()).thenReturn(alphabet);

        Randoms rng = mock(Randoms.class);
        when(rng.nextInt(2)).thenReturn(1);

        model.estimate(dataset, 1, 0, 0, null, rng);
        assertNotNull(dataset.get(0));
    }
@Test
    public void testPrintDocumentTopicsWithNegativeThreshold() {
        TopicalNGrams model = new TopicalNGrams(2);

        FeatureSequenceWithBigrams fs = mock(FeatureSequenceWithBigrams.class);
        when(fs.getLength()).thenReturn(2);
        when(fs.getIndexAtPosition(0)).thenReturn(0);
        when(fs.getIndexAtPosition(1)).thenReturn(1);
        when(fs.getBiIndexAtPosition(0)).thenReturn(-1);
        when(fs.getBiIndexAtPosition(1)).thenReturn(-1);
        when(fs.getBiAlphabet()).thenReturn(new Alphabet());

        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("w0");
        alphabet.lookupIndex("w1");

        Instance instance = new Instance(fs, null, "negThresholdDoc", null);
        InstanceList list = mock(InstanceList.class);
        when(list.size()).thenReturn(1);
        when(list.get(0)).thenReturn(instance);
        when(list.getDataAlphabet()).thenReturn(alphabet);

        Randoms r = mock(Randoms.class);
        when(r.nextInt(2)).thenReturn(0);

        model.estimate(list, 1, 0, 0, null, r);

        StringWriter writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);
        model.printDocumentTopics(pw, -1.0, 2);
        pw.flush();

        String output = writer.toString();
        assertTrue(output.contains("negThresholdDoc"));
    }
@Test
    public void testPrintStateWithUnicodeTokens() {
        TopicalNGrams model = new TopicalNGrams(1);

        FeatureSequenceWithBigrams fs = mock(FeatureSequenceWithBigrams.class);
        when(fs.getLength()).thenReturn(2);
        when(fs.getIndexAtPosition(0)).thenReturn(0);
        when(fs.getIndexAtPosition(1)).thenReturn(1);
        when(fs.getBiIndexAtPosition(0)).thenReturn(-1);
        when(fs.getBiIndexAtPosition(1)).thenReturn(-1);

        Alphabet alphabet = new Alphabet();
        alphabet.lookupIndex("你好");
        alphabet.lookupIndex("世界");

        Alphabet biAlphabet = new Alphabet();
        biAlphabet.lookupIndex("你好_世界");

        when(fs.getBiAlphabet()).thenReturn(biAlphabet);

        Instance instance = new Instance(fs, null, "unicodeDoc", null);
        InstanceList ilist = mock(InstanceList.class);
        when(ilist.size()).thenReturn(1);
        when(ilist.get(0)).thenReturn(instance);
        when(ilist.getDataAlphabet()).thenReturn(alphabet);

        Randoms random = mock(Randoms.class);
        when(random.nextInt(1)).thenReturn(0);

        model.estimate(ilist, 1, 0, 0, null, random);

        StringWriter buffer = new StringWriter();
        PrintWriter pw = new PrintWriter(buffer);
        model.printState(pw);
        pw.flush();

        String output = buffer.toString();
        assertTrue(output.contains("你好"));
        assertTrue(output.contains("世界"));
    }
@Test
    public void testEstimateSingleTopicAndProbabilityDominance() {
        TopicalNGrams model = new TopicalNGrams(1);

        FeatureSequenceWithBigrams fs = mock(FeatureSequenceWithBigrams.class);
        when(fs.getLength()).thenReturn(5);
        for (int i = 0; i < 5; i++) {
            when(fs.getIndexAtPosition(i)).thenReturn(i);
            when(fs.getBiIndexAtPosition(i)).thenReturn(-1);
        }

        Alphabet alphabet = new Alphabet();
        for (int i = 0; i < 5; i++) {
            alphabet.lookupIndex("token" + i);
        }

        when(fs.getBiAlphabet()).thenReturn(new Alphabet());

        Instance instance = new Instance(fs, null, "singleTopicDominance", null);

        InstanceList set = mock(InstanceList.class);
        when(set.size()).thenReturn(1);
        when(set.get(0)).thenReturn(instance);
        when(set.getDataAlphabet()).thenReturn(alphabet);

        Randoms random = mock(Randoms.class);
        when(random.nextInt(1)).thenReturn(0);

        model.estimate(set, 3, 0, 0, null, random);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        model.printDocumentTopics(pw, 0.5, 2);
        pw.flush();

        String output = sw.toString();
        assertTrue(output.contains("singleTopicDominance"));
    }
@Test
    public void testBigramFlagResetOnResample() {
        TopicalNGrams model = new TopicalNGrams(1);

        FeatureSequenceWithBigrams fs = mock(FeatureSequenceWithBigrams.class);
        when(fs.getLength()).thenReturn(3);
        when(fs.getIndexAtPosition(0)).thenReturn(0);
        when(fs.getIndexAtPosition(1)).thenReturn(1);
        when(fs.getIndexAtPosition(2)).thenReturn(2);
        when(fs.getBiIndexAtPosition(0)).thenReturn(0);
        when(fs.getBiIndexAtPosition(1)).thenReturn(1);
        when(fs.getBiIndexAtPosition(2)).thenReturn(2);

        Alphabet alpha = new Alphabet();
        alpha.lookupIndex("x0");
        alpha.lookupIndex("x1");
        alpha.lookupIndex("x2");

        Alphabet biAlpha = new Alphabet();
        biAlpha.lookupIndex("x0_x1");
        biAlpha.lookupIndex("x1_x2");
        biAlpha.lookupIndex("x2_x3");

        when(fs.getBiAlphabet()).thenReturn(biAlpha);

        Instance inst = new Instance(fs, null, "resampleDoc", null);
        InstanceList ilist = mock(InstanceList.class);
        when(ilist.size()).thenReturn(1);
        when(ilist.get(0)).thenReturn(inst);
        when(ilist.getDataAlphabet()).thenReturn(alpha);

        Randoms r = mock(Randoms.class);
        when(r.nextInt(1)).thenReturn(0);

        model.estimate(ilist, 2, 0, 0, null, r);

        assertNotNull(inst);
    } 
}