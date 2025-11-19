public class TopicalNGrams_5_GPTLLMTest { 

 @Test
    public void testConstructorWithDefaults() {
        TopicalNGrams model = new TopicalNGrams(5);

        assertNotNull(model);
    }
@Test
    public void testEstimateInitializesStructures() {
        Alphabet uniAlphabet = new Alphabet();
        Alphabet biAlphabet = new Alphabet();

        FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(uniAlphabet, biAlphabet);
        fs.add("apple");
        fs.add("banana");
        fs.add("carrot");

        int bi1 = biAlphabet.lookupIndex("apple_banana");
        fs.setBiIndexAtPosition(1, bi1);

        Instance instance = new Instance(fs, null, "doc1", null);
        InstanceList ilist = new InstanceList(uniAlphabet);
        ilist.add(instance);

        TopicalNGrams model = new TopicalNGrams(3, 1.0, 0.01, 0.01, 0.01, 0.2, 1000);

        Randoms randoms = new Randoms(1234);
        model.estimate(ilist, 5, 0, 0, null, randoms);

        assertNotNull(model);
    }
@Test
    public void testEstimateWithOutputModelFile() throws IOException {
        Alphabet uniAlphabet = new Alphabet();
        Alphabet biAlphabet = new Alphabet();
        FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(uniAlphabet, biAlphabet);
        fs.add("dog");
        fs.add("cat");
        int bi = biAlphabet.lookupIndex("dog_cat");
        fs.setBiIndexAtPosition(1, bi);

        Instance instance = new Instance(fs, null, "doc2", null);
        InstanceList ilist = new InstanceList(uniAlphabet);
        ilist.add(instance);

        TopicalNGrams model = new TopicalNGrams(2);

        File tempFile = File.createTempFile("model", "");
        String outputPrefix = tempFile.getAbsolutePath();
        tempFile.delete();

        Randoms randoms = new Randoms(42);
        model.estimate(ilist, 1, 0, 1, outputPrefix, randoms);

        File expected = new File(outputPrefix + ".1");
        assertTrue(expected.exists());
        expected.delete();
    }
@Test
    public void testPrintTopWordsStandardOutput() {
        Alphabet uniAlphabet = new Alphabet();
        Alphabet biAlphabet = new Alphabet();
        FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(uniAlphabet, biAlphabet);
        fs.add("red");
        fs.add("blue");
        fs.setBiIndexAtPosition(1, biAlphabet.lookupIndex("red_blue"));

        Instance instance = new Instance(fs, null, "doc3", null);
        InstanceList ilist = new InstanceList(uniAlphabet);
        ilist.add(instance);

        TopicalNGrams model = new TopicalNGrams(3);

        Randoms r = new Randoms(789);
        model.estimate(ilist, 1, 0, 0, null, r);

        model.printTopWords(2, false);
        model.printTopWords(2, true);
    }
@Test
    public void testPrintDocumentTopicsToFileWithThreshold() throws IOException {
        Alphabet uniAlphabet = new Alphabet();
        Alphabet biAlphabet = new Alphabet();
        FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(uniAlphabet, biAlphabet);
        fs.add("sun");
        fs.add("moon");
        fs.setBiIndexAtPosition(1, biAlphabet.lookupIndex("sun_moon"));

        Instance instance = new Instance(fs, null, "doc4", null);
        InstanceList ilist = new InstanceList(uniAlphabet);
        ilist.add(instance);

        TopicalNGrams model = new TopicalNGrams(2);
        model.estimate(ilist, 1, 0, 0, null, new Randoms(987));

        File output = File.createTempFile("doctopics", ".txt");
        PrintWriter pw = new PrintWriter(new FileWriter(output));
        model.printDocumentTopics(pw, 0.02, 1);
        pw.close();

        FileReader fr = new FileReader(output);
        BufferedReader br = new BufferedReader(fr);
        String line = br.readLine();
        assertTrue(line.contains("doc"));
        output.delete();
    }
@Test
    public void testPrintStateToFile() throws IOException {
        Alphabet uniAlphabet = new Alphabet();
        Alphabet biAlphabet = new Alphabet();
        FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(uniAlphabet, biAlphabet);
        fs.add("yes");
        fs.add("no");
        fs.setBiIndexAtPosition(1, biAlphabet.lookupIndex("yes_no"));

        Instance instance = new Instance(fs, null, "doc5", null);
        InstanceList ilist = new InstanceList(uniAlphabet);
        ilist.add(instance);

        TopicalNGrams model = new TopicalNGrams(2);
        model.estimate(ilist, 1, 0, 0, null, new Randoms(321));

        File file = File.createTempFile("state", ".txt");
        model.printState(file);

        assertTrue(file.exists());

        FileReader reader = new FileReader(file);
        BufferedReader br = new BufferedReader(reader);
        String header = br.readLine();
        assertTrue(header.contains("#doc"));
        file.delete();
    }
@Test
    public void testSerializationRoundTrip() throws IOException, ClassNotFoundException {
        Alphabet uniAlphabet = new Alphabet();
        Alphabet biAlphabet = new Alphabet();
        FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(uniAlphabet, biAlphabet);
        fs.add("green");
        fs.add("yellow");
        fs.setBiIndexAtPosition(1, biAlphabet.lookupIndex("green_yellow"));

        Instance instance = new Instance(fs, null, "doc6", null);
        InstanceList ilist = new InstanceList(uniAlphabet);
        ilist.add(instance);

        TopicalNGrams model = new TopicalNGrams(2);
        model.estimate(ilist, 1, 0, 0, null, new Randoms(456));

        File out = File.createTempFile("model", ".ser");
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(out));
        oos.writeObject(model);
        oos.close();

        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(out));
        TopicalNGrams restored = (TopicalNGrams) ois.readObject();
        ois.close();
        out.delete();

        assertNotNull(restored);
    }
@Test
    public void testEstimateWithZeroIterationsDoesNotCrash() {
        Alphabet uniAlphabet = new Alphabet();
        Alphabet biAlphabet = new Alphabet();
        FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(uniAlphabet, biAlphabet);
        fs.add("oneword");

        Instance instance = new Instance(fs, null, "docA", null);
        InstanceList ilist = new InstanceList(uniAlphabet);
        ilist.add(instance);

        TopicalNGrams model = new TopicalNGrams(2);
        model.estimate(ilist, 0, 0, 0, null, new Randoms(321));

        assertNotNull(model);
    }
@Test
    public void testEstimateWithEmptyDocumentListDoesNothing() {
        Alphabet alpha = new Alphabet();
        InstanceList ilist = new InstanceList(alpha);

        TopicalNGrams model = new TopicalNGrams(3);

        model.estimate(ilist, 1, 0, 0, null, new Randoms());

        assertNotNull(model);
    }
@Test
    public void testPrintDocumentTopicsWithNegativeMax() throws Exception {
        Alphabet uni = new Alphabet();
        Alphabet bi = new Alphabet();

        FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(uni, bi);
        fs.add("a");
        fs.add("b");
        fs.setBiIndexAtPosition(1, bi.lookupIndex("a_b"));

        InstanceList ilist = new InstanceList(uni);
        ilist.add(new Instance(fs, null, "source", "target"));

        TopicalNGrams model = new TopicalNGrams(2);
        model.estimate(ilist, 1, 0, 0, null, new Randoms());

        File file = File.createTempFile("negMax", ".txt");
        PrintWriter writer = new PrintWriter(new FileWriter(file));
        model.printDocumentTopics(writer, 0.0, -1);
        writer.close();

        BufferedReader reader = new BufferedReader(new FileReader(file));
        String firstLine = reader.readLine();
        assertTrue(firstLine.contains("doc"));
        file.delete();
    }
@Test
    public void testPrintStateWithOneTokenDocument() throws Exception {
        Alphabet a1 = new Alphabet();
        Alphabet b1 = new Alphabet();
        FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(a1, b1);
        fs.add("single");

        Instance instance = new Instance(fs, null, "doc", null);
        InstanceList il = new InstanceList(a1);
        il.add(instance);

        TopicalNGrams model = new TopicalNGrams(2);
        model.estimate(il, 1, 0, 0, null, new Randoms());

        File out = File.createTempFile("single", ".state");
        model.printState(out);

        assertTrue(out.exists());
        BufferedReader r = new BufferedReader(new FileReader(out));
        r.readLine(); 
        String docLine = r.readLine();
        assertTrue(docLine.contains("0"));
        out.delete();
    }
@Test
    public void testBigramDisallowedPosition() {
        Alphabet uni = new Alphabet();
        Alphabet bi = new Alphabet();

        FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(uni, bi);
        fs.add("one");
        fs.add("two");
        

        InstanceList il = new InstanceList(uni);
        il.add(new Instance(fs, null, "noskip", null));

        TopicalNGrams model = new TopicalNGrams(3);
        model.estimate(il, 3, 0, 0, null, new Randoms(99));

        assertNotNull(model);
    }
@Test
    public void testPrintDocumentTopicsWithThresholdGreaterThanAllTopics() throws Exception {
        Alphabet a = new Alphabet();
        Alphabet b = new Alphabet();
        FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(a, b);
        fs.add("word1");
        fs.add("word2");
        fs.setBiIndexAtPosition(1, b.lookupIndex("word1_word2"));

        Instance instance = new Instance(fs, null, "doc", null);
        InstanceList ilist = new InstanceList(a);
        ilist.add(instance);

        TopicalNGrams model = new TopicalNGrams(2);
        model.estimate(ilist, 1, 0, 0, null, new Randoms());

        File f = File.createTempFile("highThreshold", ".txt");
        PrintWriter pw = new PrintWriter(new FileWriter(f));
        model.printDocumentTopics(pw, 99.0, 5);
        pw.close();

        BufferedReader br = new BufferedReader(new FileReader(f));
        String line = br.readLine();
        assertTrue(line.contains("#doc"));
        line = br.readLine();
        assertTrue(line.contains("doc"));
        f.delete();
    }
@Test
    public void testIOExceptionDuringWriteHandledGracefully() {
        Alphabet a = new Alphabet();
        Alphabet b = new Alphabet();
        FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(a, b);
        fs.add("word");
        InstanceList ilist = new InstanceList(a);
        ilist.add(new Instance(fs, null, "source", null));

        TopicalNGrams model = new TopicalNGrams(2);
        model.estimate(ilist, 1, 0, 0, null, new Randoms());

        File fakeDir = new File("nonexistent_dir/failure.ser");
        model.write(fakeDir); 
        assertTrue(true); 
    }
@Test
    public void testNullInstanceListThrowsNPE() {
        TopicalNGrams model = new TopicalNGrams(2);
        try {
            model.estimate(null, 1, 0, 0, null, new Randoms());
            fail("Expected NullPointerException");
        } catch (NullPointerException expected) {
            assertTrue(true);
        }
    }
@Test
    public void testEstimateWithSingleTokenWithBigramUnset() {
        Alphabet a = new Alphabet();
        Alphabet b = new Alphabet();
        FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(a, b);
        fs.add("zebra");

        Instance instance = new Instance(fs, null, "solo", null);
        InstanceList il = new InstanceList(a);
        il.add(instance);

        TopicalNGrams model = new TopicalNGrams(2);
        model.estimate(il, 1, 0, 0, null, new Randoms(124));

        assertNotNull(model);
    }
@Test
    public void testEstimateWithEmptyFeatureSequence() {
        Alphabet uni = new Alphabet();
        Alphabet bi = new Alphabet();
        FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(uni, bi);
        InstanceList ilist = new InstanceList(uni);
        ilist.add(new Instance(fs, null, "emptyDoc", null));

        TopicalNGrams model = new TopicalNGrams(2);
        model.estimate(ilist, 1, 0, 0, null, new Randoms(1111));

        assertNotNull(model);
    }
@Test
    public void testWriteToUnwritableFilePath() {
        Alphabet a = new Alphabet();
        Alphabet b = new Alphabet();
        FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(a, b);
        fs.add("fail");
        InstanceList ilist = new InstanceList(a);
        ilist.add(new Instance(fs, null, "cause", null));
        TopicalNGrams model = new TopicalNGrams(2);
        model.estimate(ilist, 1, 0, 0, null, new Randoms());

        File restrictedFolder = new File("/root/invalid_dir.model");
        model.write(restrictedFolder);
        assertTrue(true); 
    }
@Test
    public void testPrintTopWordsWithZeroCounts() {
        Alphabet uni = new Alphabet();
        Alphabet bi = new Alphabet();
        FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(uni, bi);
        fs.add("a");
        Instance instance = new Instance(fs, null, "topWord", null);
        InstanceList ilist = new InstanceList(uni);
        ilist.add(instance);
        TopicalNGrams model = new TopicalNGrams(2);
        model.estimate(ilist, 1, 0, 0, null, new Randoms(123));

        model.printTopWords(0, false); 
        model.printTopWords(0, true);
    }
@Test
    public void testPrintStateWithMisalignedBiIndexes() throws IOException {
        Alphabet uni = new Alphabet();
        Alphabet bi = new Alphabet();
        FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(uni, bi);
        fs.add("x");
        fs.add("y"); 
        InstanceList ilist = new InstanceList(uni);
        ilist.add(new Instance(fs, null, "doc", null));

        TopicalNGrams model = new TopicalNGrams(2);
        model.estimate(ilist, 1, 0, 0, null, new Randoms(321));
        File file = File.createTempFile("misalign", ".txt");

        model.printState(file);

        assertTrue(file.exists());
        file.delete();
    }
@Test
    public void testEstimateMultipleDocsDifferentLengths() {
        Alphabet uni = new Alphabet();
        Alphabet bi = new Alphabet();

        FeatureSequenceWithBigrams fs1 = new FeatureSequenceWithBigrams(uni, bi);
        fs1.add("short");

        FeatureSequenceWithBigrams fs2 = new FeatureSequenceWithBigrams(uni, bi);
        fs2.add("this");
        fs2.add("is");
        fs2.add("long");

        InstanceList ilist = new InstanceList(uni);
        ilist.add(new Instance(fs1, null, "s1", null));
        ilist.add(new Instance(fs2, null, "s2", null));

        TopicalNGrams model = new TopicalNGrams(2);
        model.estimate(ilist, 2, 0, 0, null, new Randoms(9999));

        assertNotNull(model);
    }
@Test
    public void testEstimateWithNullRandomsShouldThrow() {
        Alphabet alphabet = new Alphabet();
        FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(alphabet, new Alphabet());
        fs.add("word");
        InstanceList ilist = new InstanceList(alphabet);
        ilist.add(new Instance(fs, null, "doc", null));

        TopicalNGrams model = new TopicalNGrams(2);
        try {
            model.estimate(ilist, 1, 0, 0, null, null);
            fail("Expected NullPointerException due to null Randoms");
        } catch (NullPointerException expected) {
            assertTrue(true);
        }
    }
@Test
    public void testPrintDocumentTopicsWithNullWriterDoesNotThrowImmediately() throws Exception {
        Alphabet alphabet = new Alphabet();
        Alphabet biAlphabet = new Alphabet();
        FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(alphabet, biAlphabet);
        fs.add("alpha");
        InstanceList ilist = new InstanceList(alphabet);
        ilist.add(new Instance(fs, null, "doc", null));

        TopicalNGrams model = new TopicalNGrams(2);
        model.estimate(ilist, 1, 0, 0, null, new Randoms());
        try {
            model.printDocumentTopics((PrintWriter) null, 0.0, 2);
            fail("Expected NullPointerException");
        } catch (NullPointerException expected) {
            assertTrue(true);
        }
    }
@Test
    public void testPrintTopWordsWithNoTopics() {
        TopicalNGrams model = new TopicalNGrams(0, 0.1, 0.01, 0.01, 0.01, 0.1, 0.1);
        Alphabet uni = new Alphabet();
        Alphabet bi = new Alphabet();
        FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(uni, bi);
        fs.add("solo");

        InstanceList ilist = new InstanceList(uni);
        ilist.add(new Instance(fs, null, "none", null));

        model.estimate(ilist, 1, 0, 0, null, new Randoms());

        model.printTopWords(5, false); 
    }
@Test
    public void testSerializationWithEmptyModel() throws Exception {
        TopicalNGrams model = new TopicalNGrams(2);
        File temp = File.createTempFile("empty_model", ".ser");

        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(temp));
        oos.writeObject(model);
        oos.close();

        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(temp));
        TopicalNGrams result = (TopicalNGrams) ois.readObject();
        ois.close();

        assertNotNull(result);
        temp.delete();
    }
@Test
    public void testPrintDocumentTopicsWithZeroThresholdAndZeroMax() throws Exception {
        Alphabet uni = new Alphabet();
        Alphabet bi = new Alphabet();
        FeatureSequenceWithBigrams fseq = new FeatureSequenceWithBigrams(uni, bi);
        fseq.add("x");
        fseq.add("y");

        InstanceList ilist = new InstanceList(uni);
        ilist.add(new Instance(fseq, null, "src", null));

        TopicalNGrams model = new TopicalNGrams(3);
        model.estimate(ilist, 1, 0, 0, null, new Randoms());

        File tempFile = File.createTempFile("doc_topics", ".txt");
        PrintWriter writer = new PrintWriter(new FileWriter(tempFile));
        model.printDocumentTopics(writer, 0.0, 0);
        writer.close();

        BufferedReader reader = new BufferedReader(new FileReader(tempFile));
        String header = reader.readLine();
        reader.close();
        assertTrue(header.startsWith("#doc"));
        tempFile.delete();
    }
@Test
    public void testPrintStatePrintsCorrectNumberOfLines() throws IOException {
        Alphabet alphabet = new Alphabet();
        Alphabet biAlphabet = new Alphabet();
        FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(alphabet, biAlphabet);
        fs.add("one");
        fs.add("two");
        fs.setBiIndexAtPosition(1, biAlphabet.lookupIndex("one_two"));

        InstanceList ilist = new InstanceList(alphabet);
        ilist.add(new Instance(fs, null, "docA", null));

        TopicalNGrams model = new TopicalNGrams(2);
        model.estimate(ilist, 1, 0, 0, null, new Randoms());

        File f = File.createTempFile("printState", ".txt");
        model.printState(f);

        BufferedReader br = new BufferedReader(new FileReader(f));
        String line1 = br.readLine(); 
        String line2 = br.readLine(); 
        String line3 = br.readLine(); 
        String line4 = br.readLine(); 
        br.close();
        f.delete();

        assertNotNull(line1);
        assertNotNull(line2);
        assertNotNull(line3);
        assertNull(line4); 
    }
@Test
    public void testPrintDocumentTopicsMultipleDocuments() throws IOException {
        Alphabet uni = new Alphabet();
        Alphabet bi = new Alphabet();
        FeatureSequenceWithBigrams fs1 = new FeatureSequenceWithBigrams(uni, bi);
        fs1.add("a");
        fs1.add("b");

        FeatureSequenceWithBigrams fs2 = new FeatureSequenceWithBigrams(uni, bi);
        fs2.add("c");
        fs2.add("d");

        InstanceList ilist = new InstanceList(uni);
        ilist.add(new Instance(fs1, null, "docA", null));
        ilist.add(new Instance(fs2, null, "docB", null));

        TopicalNGrams model = new TopicalNGrams(3);
        model.estimate(ilist, 1, 0, 0, null, new Randoms(123));

        File output = File.createTempFile("multi_doc_topics", ".txt");
        PrintWriter pw = new PrintWriter(new FileWriter(output));
        model.printDocumentTopics(pw, 0.0, 3);
        pw.close();

        BufferedReader br = new BufferedReader(new FileReader(output));
        String header = br.readLine();
        String line1 = br.readLine();
        String line2 = br.readLine();
        br.close();
        assertNotNull(header);
        assertTrue(line1.contains("docA") || line1.contains("docB"));
        assertTrue(line2.contains("docA") || line2.contains("docB"));
        output.delete();
    }
@Test
    public void testEstimateWithAllTokensAsBigrams() {
        Alphabet uni = new Alphabet();
        Alphabet bi = new Alphabet();
        int biIndex1 = bi.lookupIndex("hello_world");
        int biIndex2 = bi.lookupIndex("world_foo");

        FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(uni, bi);
        fs.add("hello");
        fs.setBiIndexAtPosition(0, biIndex1);
        fs.add("world");
        fs.setBiIndexAtPosition(1, biIndex2);
        fs.add("foo");

        InstanceList ilist = new InstanceList(uni);
        ilist.add(new Instance(fs, null, "docBigram", null));

        TopicalNGrams model = new TopicalNGrams(2);
        model.estimate(ilist, 1, 0, 0, null, new Randoms(741));

        assertNotNull(model);
    }
@Test
    public void testEstimateWithNullOutputModelFilename() {
        Alphabet alpha = new Alphabet();
        Alphabet bi = new Alphabet();
        FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(alpha, bi);
        fs.add("w1");
        fs.add("w2");

        InstanceList ilist = new InstanceList(alpha);
        ilist.add(new Instance(fs, null, "doc", null));

        TopicalNGrams model = new TopicalNGrams(3);

        
        model.estimate(ilist, 2, 0, 1, null, new Randoms(222));
        assertTrue(true); 
    }
@Test
    public void testPrintTopWordsWithLargeNumRequest() {
        Alphabet uni = new Alphabet();
        Alphabet bi = new Alphabet();
        FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(uni, bi);
        fs.add("apple");
        fs.add("banana");
        fs.add("carrot");
        fs.add("date");
        fs.add("eggplant");

        InstanceList ilist = new InstanceList(uni);
        ilist.add(new Instance(fs, null, "fruits", null));

        TopicalNGrams model = new TopicalNGrams(2);
        model.estimate(ilist, 2, 0, 0, null, new Randoms(333));

        model.printTopWords(100, true);  
        assertTrue(true);  
    }
@Test
    public void testSerializationWithNullFieldsThrowsNoException() throws Exception {
        TopicalNGrams model = new TopicalNGrams(1);
        File out = File.createTempFile("basic_serial", ".ser");

        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(out));
        oos.writeObject(model);
        oos.close();

        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(out));
        TopicalNGrams deserialized = (TopicalNGrams) ois.readObject();
        ois.close();

        assertNotNull(deserialized);
        out.delete();
    }
@Test
    public void testEstimateWithOneTopicOnly() {
        Alphabet uni = new Alphabet();
        Alphabet bi = new Alphabet();
        FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(uni, bi);
        fs.add("single");
        fs.add("token");

        InstanceList ilist = new InstanceList(uni);
        ilist.add(new Instance(fs, null, "oneTopic", null));

        TopicalNGrams model = new TopicalNGrams(1); 
        model.estimate(ilist, 2, 0, 0, null, new Randoms(656));
        model.printTopWords(5, true);
        assertTrue(true);
    }
@Test
    public void testEstimateWithLongTokenSequenceAndBigrams() {
        Alphabet uni = new Alphabet();
        Alphabet bi = new Alphabet();

        FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(uni, bi);
        fs.add("the");
        fs.setBiIndexAtPosition(0, bi.lookupIndex("the_quick"));
        fs.add("quick");
        fs.setBiIndexAtPosition(1, bi.lookupIndex("quick_brown"));
        fs.add("brown");
        fs.setBiIndexAtPosition(2, bi.lookupIndex("brown_fox"));
        fs.add("fox");

        InstanceList ilist = new InstanceList(uni);
        ilist.add(new Instance(fs, null, "longSeq", null));

        TopicalNGrams model = new TopicalNGrams(4);
        model.estimate(ilist, 5, 0, 0, null, new Randoms(989));
        assertTrue(true);
    }
@Test
    public void testEstimateWithNullBiAlphabetInFeatureSequence() {
        Alphabet uni = new Alphabet();
        FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(uni, null); 
        fs.add("alpha");
        fs.add("beta");

        InstanceList ilist = new InstanceList(uni);
        ilist.add(new Instance(fs, null, "docNullBi", null));

        TopicalNGrams model = new TopicalNGrams(2);
        try {
            model.estimate(ilist, 1, 0, 0, null, new Randoms());
            fail("Expected NullPointerException due to null biAlphabet");
        } catch (NullPointerException expected) {
            assertTrue(true);
        }
    }
@Test
    public void testPrintDocumentTopicsWithAllZeroProportions() throws IOException {
        Alphabet alpha = new Alphabet();
        Alphabet bi = new Alphabet();

        FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(alpha, bi);
        fs.add("x");

        InstanceList ilist = new InstanceList(alpha);
        ilist.add(new Instance(fs, null, "docZ", null));

        TopicalNGrams model = new TopicalNGrams(4);
        model.estimate(ilist, 1, 0, 0, null, new Randoms());

        File file = File.createTempFile("doczero", ".txt");
        PrintWriter writer = new PrintWriter(new FileWriter(file));
        model.printDocumentTopics(writer, 1.0, 10); 
        writer.close();

        BufferedReader br = new BufferedReader(new FileReader(file));
        String header = br.readLine();
        String line = br.readLine();
        br.close();

        assertTrue(header.contains("#doc"));
        assertTrue(line.contains("docZ"));
        assertFalse(line.contains("0.0")); 

        file.delete();
    }
@Test
    public void testEstimateWithAllIdenticalTokens() {
        Alphabet alpha = new Alphabet();
        Alphabet bi = new Alphabet();

        FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(alpha, bi);
        fs.add("repeat");
        fs.add("repeat");
        fs.setBiIndexAtPosition(1, bi.lookupIndex("repeat_repeat"));

        InstanceList ilist = new InstanceList(alpha);
        ilist.add(new Instance(fs, null, "sameTokens", null));

        TopicalNGrams model = new TopicalNGrams(3);
        model.estimate(ilist, 2, 0, 0, null, new Randoms());

        assertNotNull(model);
    }
@Test
    public void testEstimateWithAlternatingBigramAvailability() {
        Alphabet alpha = new Alphabet();
        Alphabet bi = new Alphabet();

        FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(alpha, bi);
        fs.add("a");
        fs.add("b");
        fs.setBiIndexAtPosition(1, bi.lookupIndex("a_b"));
        fs.add("c");
        fs.add("d");
        fs.setBiIndexAtPosition(3, bi.lookupIndex("c_d"));

        InstanceList ilist = new InstanceList(alpha);
        ilist.add(new Instance(fs, null, "alternateBi", null));

        TopicalNGrams model = new TopicalNGrams(2);
        model.estimate(ilist, 3, 0, 0, null, new Randoms(555));

        assertNotNull(model);
    }
@Test
    public void testPrintTopWordsWithNoTokensForTopic() {
        Alphabet alpha = new Alphabet();
        Alphabet bi = new Alphabet();

        FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(alpha, bi);
        fs.add("unseen");

        InstanceList ilist = new InstanceList(alpha);
        ilist.add(new Instance(fs, null, "noTokens", null));

        TopicalNGrams model = new TopicalNGrams(2);
        model.estimate(ilist, 1, 0, 0, null, new Randoms(999));

        model.printTopWords(5, false); 
        model.printTopWords(5, true);
    }
@Test
    public void testEstimatingVeryLargeNumberOfTopics() {
        Alphabet alpha = new Alphabet();
        Alphabet bi = new Alphabet();

        FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(alpha, bi);
        fs.add("t1");
        fs.add("t2");

        InstanceList ilist = new InstanceList(alpha);
        ilist.add(new Instance(fs, null, "manyTopics", null));

        TopicalNGrams model = new TopicalNGrams(100); 
        model.estimate(ilist, 2, 0, 0, null, new Randoms(123));

        assertNotNull(model);
    }
@Test
    public void testEstimateHandlesOneTokenNoBigramPossible() {
        Alphabet a = new Alphabet();
        Alphabet b = new Alphabet();
        FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(a, b);
        fs.add("only"); 

        InstanceList ilist = new InstanceList(a);
        ilist.add(new Instance(fs, null, "oneTokenNoBi", null));

        TopicalNGrams model = new TopicalNGrams(2);
        model.estimate(ilist, 2, 0, 0, null, new Randoms(321));

        assertNotNull(model);
    }
@Test
    public void testSerializationWithMultipleTopicsAndTokens() throws Exception {
        Alphabet a = new Alphabet();
        Alphabet b = new Alphabet();
        FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(a, b);
        fs.add("red");
        fs.add("blue");
        fs.setBiIndexAtPosition(1, b.lookupIndex("red_blue"));

        InstanceList ilist = new InstanceList(a);
        ilist.add(new Instance(fs, null, "colors", null));

        TopicalNGrams model = new TopicalNGrams(5);
        model.estimate(ilist, 1, 0, 0, null, new Randoms(991));

        File f = File.createTempFile("tng_save", ".ser");
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
        oos.writeObject(model);
        oos.close();

        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
        TopicalNGrams loaded = (TopicalNGrams) ois.readObject();
        ois.close();
        f.delete();

        assertNotNull(loaded);
    }
@Test
    public void testEstimateWithZeroTopicsShouldAvoidDivisionByZero() {
        try {
            TopicalNGrams model = new TopicalNGrams(0); 
            fail("Expected ArithmeticException or IllegalArgumentException");
        } catch (ArithmeticException | IllegalArgumentException expected) {
            assertTrue(true);
        }
    }
@Test
    public void testPrintStateHandlesEmptyDocument() throws IOException {
        Alphabet alphabet = new Alphabet();
        Alphabet biAlphabet = new Alphabet();
        FeatureSequenceWithBigrams emptySequence = new FeatureSequenceWithBigrams(alphabet, biAlphabet);

        Instance emptyInstance = new Instance(emptySequence, null, "emptyDoc", null);
        InstanceList instanceList = new InstanceList(alphabet);
        instanceList.add(emptyInstance);

        TopicalNGrams model = new TopicalNGrams(2);
        model.estimate(instanceList, 1, 0, 0, null, new Randoms());

        File outFile = File.createTempFile("emptydoc", ".state");
        model.printState(outFile);

        BufferedReader reader = new BufferedReader(new FileReader(outFile));
        String header = reader.readLine();
        String next = reader.readLine();
        reader.close();
        outFile.delete();

        assertNotNull(header);
        assertNull(next); 
    }
@Test
    public void testEstimateWithOneDocumentAndOneTokenAndBigramSetToNegativeOne() {
        Alphabet alpha = new Alphabet();
        Alphabet bi = new Alphabet();
        FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(alpha, bi);
        fs.add("word");
        fs.setBiIndexAtPosition(0, -1); 

        Instance instance = new Instance(fs, null, "singleton", null);
        InstanceList ilist = new InstanceList(alpha);
        ilist.add(instance);

        TopicalNGrams model = new TopicalNGrams(2);
        model.estimate(ilist, 1, 0, 0, null, new Randoms(123));

        assertNotNull(model);
    }
@Test
    public void testPrintDocumentTopicsDoesNotPrintAnyTopicsDueToHighThreshold() throws IOException {
        Alphabet alpha = new Alphabet();
        Alphabet bi = new Alphabet();

        FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(alpha, bi);
        fs.add("a");
        fs.add("b");

        InstanceList ilist = new InstanceList(alpha);
        ilist.add(new Instance(fs, null, "highThreshDoc", null));

        TopicalNGrams model = new TopicalNGrams(3);
        model.estimate(ilist, 1, 0, 0, null, new Randoms());

        File file = File.createTempFile("ht", ".txt");
        PrintWriter writer = new PrintWriter(new FileWriter(file));
        model.printDocumentTopics(writer, 1.1, 3); 
        writer.close();

        BufferedReader reader = new BufferedReader(new FileReader(file));
        String header = reader.readLine();
        String line = reader.readLine();
        reader.close();
        file.delete();

        assertTrue(header.contains("doc"));
        assertTrue(line.startsWith("0"));
        assertFalse(line.contains("0.")); 
    }
@Test
    public void testSerializationOfModelWithoutCallingEstimate() throws Exception {
        TopicalNGrams model = new TopicalNGrams(2); 

        File tempFile = File.createTempFile("no_estimate_write", ".ser");
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(tempFile));
        oos.writeObject(model);
        oos.close();

        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(tempFile));
        TopicalNGrams deserialized = (TopicalNGrams) ois.readObject();
        ois.close();
        tempFile.delete();

        assertNotNull(deserialized);
    }
@Test
    public void testMultipleDocumentsWithDifferingLengths() {
        Alphabet alphabet = new Alphabet();
        Alphabet biAlphabet = new Alphabet();

        FeatureSequenceWithBigrams fs1 = new FeatureSequenceWithBigrams(alphabet, biAlphabet);
        fs1.add("w1");

        FeatureSequenceWithBigrams fs2 = new FeatureSequenceWithBigrams(alphabet, biAlphabet);
        fs2.add("x");
        fs2.add("y");
        fs2.setBiIndexAtPosition(1, biAlphabet.lookupIndex("x_y"));

        FeatureSequenceWithBigrams fs3 = new FeatureSequenceWithBigrams(alphabet, biAlphabet);
        fs3.add("a");
        fs3.add("b");
        fs3.add("c");
        fs3.setBiIndexAtPosition(2, biAlphabet.lookupIndex("b_c"));

        InstanceList ilist = new InstanceList(alphabet);
        ilist.add(new Instance(fs1, null, "d1", null));
        ilist.add(new Instance(fs2, null, "d2", null));
        ilist.add(new Instance(fs3, null, "d3", null));

        TopicalNGrams model = new TopicalNGrams(3);
        model.estimate(ilist, 5, 0, 0, null, new Randoms());

        assertNotNull(model);
    }
@Test
    public void testPrintTopWordsWithZeroTopicsDefined() {
        try {
            TopicalNGrams model = new TopicalNGrams(0); 
            fail("Expected exception when creating model with zero topics.");
        } catch (ArithmeticException | IllegalArgumentException expected) {
            assertTrue(true);
        }
    }
@Test
    public void testEstimateWithNullAlphabetInInstanceList() {
        try {
            InstanceList ilist = new InstanceList(null); 
            TopicalNGrams model = new TopicalNGrams(2);
            model.estimate(ilist, 1, 0, 0, null, new Randoms());
            fail("Expected NullPointerException due to null Alphabet");
        } catch (NullPointerException expected) {
            assertTrue(true);
        }
    }
@Test
    public void testEstimateWithNegativeOutputModelInterval() {
        Alphabet alpha = new Alphabet();
        Alphabet bi = new Alphabet();
        FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(alpha, bi);
        fs.add("w");

        InstanceList ilist = new InstanceList(alpha);
        ilist.add(new Instance(fs, null, "output", null));

        TopicalNGrams model = new TopicalNGrams(2);
        model.estimate(ilist, 5, 0, -1, null, new Randoms()); 

        assertTrue(true); 
    }
@Test
    public void testPrintDocumentTopicsWithZeroDocuments() throws IOException {
        Alphabet alpha = new Alphabet();
        InstanceList ilist = new InstanceList(alpha); 

        TopicalNGrams model = new TopicalNGrams(3);
        model.estimate(ilist, 1, 0, 0, null, new Randoms());

        File tempFile = File.createTempFile("zt", ".txt");
        PrintWriter writer = new PrintWriter(new FileWriter(tempFile));
        model.printDocumentTopics(writer, 0.0, 3);
        writer.close();

        BufferedReader reader = new BufferedReader(new FileReader(tempFile));
        String header = reader.readLine();
        String line = reader.readLine();
        reader.close();
        tempFile.delete();

        assertNotNull(header);
        assertNull(line); 
    }
@Test
    public void testEstimateWithAllBigramIndicesSetToMinusOne() {
        Alphabet alpha = new Alphabet();
        Alphabet bi = new Alphabet();
        FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(alpha, bi);

        fs.add("x");
        fs.setBiIndexAtPosition(0, -1);
        fs.add("y");
        fs.setBiIndexAtPosition(1, -1);
        fs.add("z");

        InstanceList ilist = new InstanceList(alpha);
        ilist.add(new Instance(fs, null, "noBigrams", null));

        TopicalNGrams model = new TopicalNGrams(2);
        model.estimate(ilist, 2, 0, 0, null, new Randoms(456));

        assertNotNull(model);
    }
@Test
    public void testPrintStateWithAllBigrams() throws IOException {
        Alphabet uni = new Alphabet();
        Alphabet bi = new Alphabet();
        int bi0 = bi.lookupIndex("a_b");
        int bi1 = bi.lookupIndex("b_c");

        FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(uni, bi);
        fs.add("a");
        fs.setBiIndexAtPosition(0, bi0);
        fs.add("b");
        fs.setBiIndexAtPosition(1, bi1);
        fs.add("c");

        InstanceList ilist = new InstanceList(uni);
        ilist.add(new Instance(fs, null, "doc", null));

        TopicalNGrams model = new TopicalNGrams(3);
        model.estimate(ilist, 3, 0, 0, null, new Randoms(101));

        File f = File.createTempFile("stateBigramOnly", ".txt");
        model.printState(f);

        BufferedReader reader = new BufferedReader(new FileReader(f));
        String header = reader.readLine();
        String row1 = reader.readLine();
        String row2 = reader.readLine();
        String row3 = reader.readLine();
        reader.close();
        f.delete();

        assertNotNull(header);
        assertNotNull(row1);
        assertNotNull(row2);
        assertNotNull(row3);
    }
@Test
    public void testPrintTopWordsHandlesZeroCountsInAllTopics() {
        Alphabet uni = new Alphabet();
        Alphabet bi = new Alphabet();

        FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(uni, bi);
        fs.add("x");

        InstanceList ilist = new InstanceList(uni);
        ilist.add(new Instance(fs, null, "zeroCounts", null));

        TopicalNGrams model = new TopicalNGrams(2);
        model.estimate(ilist, 1, 0, 0, null, new Randoms(651));

        model.printTopWords(5, false);
        model.printTopWords(5, true);
    }
@Test
    public void testEstimateWithDocumentEndingOnBigramToken() {
        Alphabet uni = new Alphabet();
        Alphabet bi = new Alphabet();

        int idx = bi.lookupIndex("foo_bar");

        FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(uni, bi);
        fs.add("foo");
        fs.setBiIndexAtPosition(0, idx);
        fs.add("bar");

        InstanceList ilist = new InstanceList(uni);
        ilist.add(new Instance(fs, null, "endWithBi", null));

        TopicalNGrams model = new TopicalNGrams(2);
        model.estimate(ilist, 5, 0, 0, null, new Randoms(999));

        assertNotNull(model);
    }
@Test
    public void testOneTokenDocumentWithBiIndexZero() {
        Alphabet uni = new Alphabet();
        Alphabet bi = new Alphabet();

        int biIndex = bi.lookupIndex("a_b");

        FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(uni, bi);
        fs.add("a");
        fs.setBiIndexAtPosition(0, biIndex);

        InstanceList ilist = new InstanceList(uni);
        ilist.add(new Instance(fs, null, "singleBi", null));

        TopicalNGrams model = new TopicalNGrams(1);
        model.estimate(ilist, 3, 0, 0, null, new Randoms());

        assertNotNull(model);
    }
@Test
    public void testEstimateWithMultipleDocumentsSameTokenSameBigram() {
        Alphabet alpha = new Alphabet();
        Alphabet bi = new Alphabet();

        int biIdx = bi.lookupIndex("x_y");

        FeatureSequenceWithBigrams fs1 = new FeatureSequenceWithBigrams(alpha, bi);
        fs1.add("x");
        fs1.setBiIndexAtPosition(0, biIdx);
        fs1.add("y");

        FeatureSequenceWithBigrams fs2 = new FeatureSequenceWithBigrams(alpha, bi);
        fs2.add("x");
        fs2.setBiIndexAtPosition(0, biIdx);
        fs2.add("y");

        InstanceList ilist = new InstanceList(alpha);
        ilist.add(new Instance(fs1, null, "a", null));
        ilist.add(new Instance(fs2, null, "b", null));

        TopicalNGrams model = new TopicalNGrams(2);
        model.estimate(ilist, 5, 0, 0, null, new Randoms(888));

        assertNotNull(model);
    }
@Test
    public void testEmptyBiAlphabetStillAllowsEstimate() {
        Alphabet uni = new Alphabet();
        FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(uni, new Alphabet());
        fs.add("word1");
        fs.add("word2");

        InstanceList ilist = new InstanceList(uni);
        ilist.add(new Instance(fs, null, "nolinks", null));

        TopicalNGrams model = new TopicalNGrams(2);
        model.estimate(ilist, 1, 0, 0, null, new Randoms());

        assertNotNull(model);
    }
@Test
    public void testDocumentWithMultipleUnassignedBigramGaps() {
        Alphabet uni = new Alphabet();
        Alphabet bi = new Alphabet();

        FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(uni, bi);
        fs.add("a");
        fs.add("b");
        fs.add("c");
        fs.add("d"); 

        InstanceList ilist = new InstanceList(uni);
        ilist.add(new Instance(fs, null, "gaps", null));

        TopicalNGrams model = new TopicalNGrams(2);
        model.estimate(ilist, 3, 0, 0, null, new Randoms());

        assertNotNull(model);
    }
@Test
    public void testPrintDocumentTopicsWithNegativeMaxTreatsAsUnlimited() throws IOException {
        Alphabet alpha = new Alphabet();
        Alphabet bi = new Alphabet();

        FeatureSequenceWithBigrams f = new FeatureSequenceWithBigrams(alpha, bi);
        f.add("tok1");
        f.add("tok2");

        InstanceList ilist = new InstanceList(alpha);
        ilist.add(new Instance(f, null, "unlim", null));

        TopicalNGrams model = new TopicalNGrams(4);
        model.estimate(ilist, 2, 0, 0, null, new Randoms());

        File file = File.createTempFile("negMax", ".txt");
        PrintWriter writer = new PrintWriter(new FileWriter(file));
        model.printDocumentTopics(writer, 0.0, -1);
        writer.close();

        BufferedReader reader = new BufferedReader(new FileReader(file));
        String header = reader.readLine();
        String body = reader.readLine();
        file.delete();

        assertNotNull(header);
        assertTrue(body.length() > 0);
    }
@Test
    public void testEstimateWithTokenRepeatsAcrossPositions() {
        Alphabet a = new Alphabet();
        Alphabet b = new Alphabet();

        FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(a, b);
        fs.add("repeat");
        fs.add("repeat");
        fs.add("repeat");

        InstanceList ilist = new InstanceList(a);
        ilist.add(new Instance(fs, null, "sameTokens", null));

        TopicalNGrams model = new TopicalNGrams(3);
        model.estimate(ilist, 4, 0, 0, null, new Randoms(912));

        assertNotNull(model);
    } 
}