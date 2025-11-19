public class TopicalNGrams_4_GPTLLMTest { 

 @Test
    public void testConstructorDefault_paramsInitialized() {
        TopicalNGrams model = new TopicalNGrams(5);

        assertNotNull(model);
    }
@Test
    public void testEstimate_initializesDataStructures() {
        Alphabet uniAlphabet = new Alphabet();
        uniAlphabet.lookupIndex("word0");
        uniAlphabet.lookupIndex("word1");
        biAlphabet = new Alphabet();
        biAlphabet.lookupIndex("word0_word1");

        FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(uniAlphabet, biAlphabet,
                new int[]{0, 1}, new int[]{-1, 0});
        Instance instance = new Instance(fs, null, "doc1", null);
        InstanceList instanceList = new InstanceList(uniAlphabet);
        instanceList.addThruPipe(instance);

        TopicalNGrams model = new TopicalNGrams(2);
        model.estimate(instanceList, 1, 0, 0, null, new Randoms(101));

        assertNotNull(model);
    }
@Test
    public void testPrintTopWords_doesNotThrow() {
        Alphabet uniAlphabet = new Alphabet();
        uniAlphabet.lookupIndex("w0");
        uniAlphabet.lookupIndex("w1");

        Alphabet biAlphabet = new Alphabet();
        biAlphabet.lookupIndex("w0_w1");

        FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(uniAlphabet, biAlphabet,
                new int[]{0, 1}, new int[]{-1, 0});
        Instance instance = new Instance(fs, null, "doc", null);

        InstanceList instanceList = new InstanceList(uniAlphabet);
        instanceList.addThruPipe(instance);

        TopicalNGrams model = new TopicalNGrams(2);
        model.estimate(instanceList, 5, 0, 0, null, new Randoms(101));

        model.printTopWords(3, true);
        model.printTopWords(3, false);
    }
@Test
    public void testPrintState_writesOutputFile() throws IOException {
        Alphabet uniAlphabet = new Alphabet();
        uniAlphabet.lookupIndex("w0");
        uniAlphabet.lookupIndex("w1");

        Alphabet biAlphabet = new Alphabet();
        biAlphabet.lookupIndex("w0_w1");

        FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(uniAlphabet, biAlphabet,
                new int[]{0, 1}, new int[]{-1, 0});
        Instance instance = new Instance(fs, null, "doc", null);

        InstanceList instanceList = new InstanceList(uniAlphabet);
        instanceList.addThruPipe(instance);

        TopicalNGrams model = new TopicalNGrams(2);
        model.estimate(instanceList, 2, 0, 0, null, new Randoms(12345));

        File outputFile = folder.newFile("state.txt");
        model.printState(outputFile);

        FileReader reader = new FileReader(outputFile);
        BufferedReader br = new BufferedReader(reader);
        String firstLine = br.readLine();
        br.close();

        assertNotNull(firstLine);
        assertTrue(firstLine.contains("#doc pos typeindex type"));
    }
@Test
    public void testWriteAndReadObject_serializationWorks() throws Exception {
        Alphabet uniAlphabet = new Alphabet();
        uniAlphabet.lookupIndex("w0");
        uniAlphabet.lookupIndex("w1");

        Alphabet biAlphabet = new Alphabet();
        biAlphabet.lookupIndex("w0_w1");

        FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(uniAlphabet, biAlphabet,
                new int[]{0, 1}, new int[]{-1, 0});
        Instance instance = new Instance(fs, null, "doc", null);

        InstanceList instanceList = new InstanceList(uniAlphabet);
        instanceList.addThruPipe(instance);

        TopicalNGrams originalModel = new TopicalNGrams(2);
        originalModel.estimate(instanceList, 5, 0, 0, null, new Randoms(1234));

        File file = folder.newFile("model.ser");
        originalModel.write(file);

        FileInputStream fis = new FileInputStream(file);
        ObjectInputStream ois = new ObjectInputStream(fis);
        TopicalNGrams deserializedModel = (TopicalNGrams) ois.readObject();
        ois.close();

        assertNotNull(deserializedModel);
    }
@Test
    public void testPrintDocumentTopicsFile_generationSucceeds() throws Exception {
        Alphabet uniAlphabet = new Alphabet();
        uniAlphabet.lookupIndex("w0");
        uniAlphabet.lookupIndex("w1");

        Alphabet biAlphabet = new Alphabet();
        biAlphabet.lookupIndex("w0_w1");

        FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(uniAlphabet, biAlphabet,
                new int[]{0, 1}, new int[]{-1, 0});
        Instance instance = new Instance(fs, null, "doc-file", null);

        InstanceList instanceList = new InstanceList(uniAlphabet);
        instanceList.addThruPipe(instance);

        TopicalNGrams model = new TopicalNGrams(2);
        model.estimate(instanceList, 3, 0, 0, null, new Randoms(88));

        File file = folder.newFile("docTopics.txt");
        model.printDocumentTopics(file);

        assertTrue(file.exists());
        assertTrue(file.length() > 0);
    }
@Test
    public void testPrintDocumentTopicsWriter_thresholdAndMax() throws Exception {
        Alphabet uniAlphabet = new Alphabet();
        uniAlphabet.lookupIndex("w0");
        uniAlphabet.lookupIndex("w1");

        Alphabet biAlphabet = new Alphabet();
        biAlphabet.lookupIndex("w0_w1");

        FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(uniAlphabet, biAlphabet,
                new int[]{0, 1}, new int[]{-1, 0});
        Instance instance = new Instance(fs, null, "docX", null);

        InstanceList instanceList = new InstanceList(uniAlphabet);
        instanceList.addThruPipe(instance);

        TopicalNGrams model = new TopicalNGrams(3);
        model.estimate(instanceList, 3, 0, 0, null, new Randoms(555));

        File file = folder.newFile("pdoc.txt");
        PrintWriter pw = new PrintWriter(new FileWriter(file));
        model.printDocumentTopics(pw, 0.0, 2);
        pw.flush();
        pw.close();

        assertTrue(file.exists());
        assertTrue(file.length() > 0);
    }
@Test(expected = NullPointerException.class)
    public void testEstimate_nullInstanceList_throwsException() {
        TopicalNGrams model = new TopicalNGrams(2);
        model.estimate(null, 10, 0, 0, null, new Randoms(1));
    }
@Test
public void testEstimate_withSingleTokenDocument() {
    Alphabet uniAlphabet = new Alphabet();
    uniAlphabet.lookupIndex("w0");

    Alphabet biAlphabet = new Alphabet();
    biAlphabet.lookupIndex("w0_w1");

    FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(uniAlphabet, biAlphabet,
            new int[]{0}, new int[]{-1});
    Instance instance = new Instance(fs, null, "single-token", null);

    InstanceList list = new InstanceList(uniAlphabet);
    list.addThruPipe(instance);

    TopicalNGrams model = new TopicalNGrams(2);
    model.estimate(list, 2, 0, 0, null, new Randoms(654321));

    assertNotNull(model);
}
@Test
public void testEstimate_withEmptyInstanceList() {
    Alphabet uniAlphabet = new Alphabet();

    InstanceList list = new InstanceList(uniAlphabet);

    TopicalNGrams model = new TopicalNGrams(2);
    model.estimate(list, 1, 0, 0, null, new Randoms(44));

    
    assertNotNull(model);
}
@Test
public void testPrintTopWords_zeroRequest() {
    Alphabet uniAlphabet = new Alphabet();
    uniAlphabet.lookupIndex("a");

    Alphabet biAlphabet = new Alphabet();
    biAlphabet.lookupIndex("a_b");

    FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(uniAlphabet, biAlphabet,
            new int[]{0, 0}, new int[]{-1, 0});
    Instance instance = new Instance(fs, null, "doc-top", null);

    InstanceList list = new InstanceList(uniAlphabet);
    list.addThruPipe(instance);

    TopicalNGrams model = new TopicalNGrams(2);
    model.estimate(list, 3, 0, 0, null, new Randoms(123));

    
    model.printTopWords(0, true);
    model.printTopWords(0, false);
}
@Test
public void testEstimate_withIdenticalWordsAndBigrams() {
    Alphabet uniAlphabet = new Alphabet();
    int wi0 = uniAlphabet.lookupIndex("repeat");
    int wi1 = uniAlphabet.lookupIndex("repeat");

    Alphabet biAlphabet = new Alphabet();
    biAlphabet.lookupIndex("repeat_repeat");

    FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(uniAlphabet, biAlphabet,
            new int[]{wi0, wi1}, new int[]{-1, 0});
    Instance instance = new Instance(fs, null, "repeated", null);

    InstanceList list = new InstanceList(uniAlphabet);
    list.addThruPipe(instance);

    TopicalNGrams model = new TopicalNGrams(3);
    model.estimate(list, 5, 0, 0, null, new Randoms(234));

    assertNotNull(model);
}
@Test
public void testPrintDocumentTopics_thresholdGreaterThanOne() throws IOException {
    Alphabet uniAlphabet = new Alphabet();
    uniAlphabet.lookupIndex("alpha");
    Alphabet biAlphabet = new Alphabet();
    biAlphabet.lookupIndex("alpha_alpha");

    FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(uniAlphabet, biAlphabet,
            new int[]{0}, new int[]{-1});
    Instance instance = new Instance(fs, null, "doc", null);

    InstanceList list = new InstanceList(uniAlphabet);
    list.addThruPipe(instance);
    TopicalNGrams model = new TopicalNGrams(2);
    model.estimate(list, 2, 0, 0, null, new Randoms(789));

    File file = folder.newFile("overthr.txt");
    PrintWriter pw = new PrintWriter(new FileWriter(file));
    model.printDocumentTopics(pw, 2.5, -1); 
    pw.flush();
    pw.close();

    assertTrue(file.exists());
    assertTrue(file.length() > 0);
}
@Test
public void testPrintTopWords_withNonexistentTypes() {
    Alphabet uniAlphabet = new Alphabet();
    uniAlphabet.lookupIndex("z");  

    Alphabet biAlphabet = new Alphabet();
    biAlphabet.lookupIndex("z_z");

    FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(uniAlphabet, biAlphabet,
            new int[]{0}, new int[]{-1});
    Instance instance = new Instance(fs, null, "underflow", null);

    InstanceList list = new InstanceList(uniAlphabet);
    list.addThruPipe(instance);

    TopicalNGrams model = new TopicalNGrams(1);
    model.estimate(list, 1, 0, 0, null, new Randoms(3333));

    model.printTopWords(10, true);  
    model.printTopWords(10, false);
}
@Test
public void testReadObject_withInvalidStreamData_failsGracefully() {
    File empty = null;
    try {
        empty = folder.newFile("broken.ser");
        FileOutputStream fos = new FileOutputStream(empty);
        fos.write("Invalid serialized content".getBytes());
        fos.close();

        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(empty));
        try {
            Object obj = ois.readObject();
            fail("Expected exception not thrown");
        } catch (IOException | ClassNotFoundException e) {
            
        }
    } catch (IOException ex) {
        fail("Setup failed: " + ex.getMessage());
    }
}
@Test
public void testEstimate_withHighTopicCountComparedToTokens() {
    Alphabet uniAlphabet = new Alphabet();
    uniAlphabet.lookupIndex("one");

    Alphabet biAlphabet = new Alphabet();
    biAlphabet.lookupIndex("one_one");

    FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(uniAlphabet, biAlphabet,
            new int[]{0}, new int[]{-1});
    Instance instance = new Instance(fs, null, "tiny", null);
    InstanceList list = new InstanceList(uniAlphabet);
    list.addThruPipe(instance);

    TopicalNGrams model = new TopicalNGrams(50); 
    model.estimate(list, 2, 0, 0, null, new Randoms(802));

    assertNotNull(model);
}
@Test
public void testEstimate_withZeroIterations() {
    Alphabet uniAlphabet = new Alphabet();
    uniAlphabet.lookupIndex("word0");

    Alphabet biAlphabet = new Alphabet();
    biAlphabet.lookupIndex("word0_word1");

    FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(
        uniAlphabet,
        biAlphabet,
        new int[]{0, 0},
        new int[]{-1, -1}
    );

    Instance instance = new Instance(fs, null, "doc", null);
    InstanceList list = new InstanceList(uniAlphabet);
    list.addThruPipe(instance);

    TopicalNGrams model = new TopicalNGrams(2);
    model.estimate(list, 0, 0, 0, null, new Randoms(1));

    assertNotNull(model);
}
@Test
public void testEstimate_withOutputModelIntervalButNoFilename() {
    Alphabet uniAlphabet = new Alphabet();
    uniAlphabet.lookupIndex("x");
    Alphabet biAlphabet = new Alphabet();
    biAlphabet.lookupIndex("x_y");

    FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(
        uniAlphabet,
        biAlphabet,
        new int[]{0, 0},
        new int[]{-1, 0}
    );

    Instance instance = new Instance(fs, null, "doc", null);
    InstanceList list = new InstanceList(uniAlphabet);
    list.addThruPipe(instance);

    TopicalNGrams model = new TopicalNGrams(2);
    model.estimate(list, 3, 1, 2, null, new Randoms(1));

    assertNotNull(model);
}
@Test
public void testPrintTopWords_withEmptyAlphabets() {
    Alphabet uniAlphabet = new Alphabet();
    Alphabet biAlphabet = new Alphabet();

    FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(
        uniAlphabet,
        biAlphabet,
        new int[]{},
        new int[]{}
    );

    Instance instance = new Instance(fs, null, "empty", null);
    InstanceList list = new InstanceList(uniAlphabet);
    list.addThruPipe(instance);

    TopicalNGrams model = new TopicalNGrams(1);
    model.estimate(list, 1, 0, 0, null, new Randoms(42));

    model.printTopWords(5, true);
    model.printTopWords(5, false);
}
@Test
public void testPrintState_withNoBigramPossibleFlag() throws IOException {
    Alphabet uniAlphabet = new Alphabet();
    uniAlphabet.lookupIndex("w0");
    Alphabet biAlphabet = new Alphabet();
    biAlphabet.lookupIndex("w1_w2");

    FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(
        uniAlphabet, 
        biAlphabet, 
        new int[]{0, 0, 0}, 
        new int[]{-1, -1, -1}
    );

    Instance instance = new Instance(fs, null, "doc", null);
    InstanceList list = new InstanceList(uniAlphabet);
    list.addThruPipe(instance);

    TopicalNGrams model = new TopicalNGrams(2);
    model.estimate(list, 1, 0, 0, null, new Randoms(123));

    File file = folder.newFile("no_bigram.txt");
    model.printState(file);

    BufferedReader reader = new BufferedReader(new FileReader(file));
    String line1 = reader.readLine();
    String line2 = reader.readLine();
    reader.close();

    assertTrue(line1.contains("#doc"));
    assertNotNull(line2);
}
@Test
public void testPrintDocumentTopics_withMaxZero() throws IOException {
    Alphabet uniAlphabet = new Alphabet();
    uniAlphabet.lookupIndex("one");

    Alphabet biAlphabet = new Alphabet();
    biAlphabet.lookupIndex("one_one");

    FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(
        uniAlphabet,
        biAlphabet,
        new int[]{0, 0},
        new int[]{-1, 0}
    );

    Instance instance = new Instance(fs, null, "test", null);
    InstanceList list = new InstanceList(uniAlphabet);
    list.addThruPipe(instance);

    TopicalNGrams model = new TopicalNGrams(2);
    model.estimate(list, 3, 0, 0, null, new Randoms(987));

    File output = folder.newFile("max0.txt");
    PrintWriter pw = new PrintWriter(new FileWriter(output));
    model.printDocumentTopics(pw, 0.0, 0);
    pw.close();

    assertTrue(output.exists());
    assertTrue(output.length() > 0);
}
@Test
public void testSerializationWithEmptyModel() throws Exception {
    Alphabet uniAlphabet = new Alphabet();
    Alphabet biAlphabet = new Alphabet();

    FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(
        uniAlphabet,
        biAlphabet,
        new int[]{},
        new int[]{}
    );

    Instance instance = new Instance(fs, null, "emptydoc", null);
    InstanceList list = new InstanceList(uniAlphabet);
    list.addThruPipe(instance);

    TopicalNGrams model = new TopicalNGrams(1);
    model.estimate(list, 1, 0, 0, null, new Randoms(55));

    File file = folder.newFile("model_empty.ser");
    model.write(file);

    ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
    TopicalNGrams deserialized = (TopicalNGrams) in.readObject();
    in.close();

    assertNotNull(deserialized);
}
@Test
public void testEstimate_bigramNotAllowedAnywhere() {
    Alphabet uniAlphabet = new Alphabet();
    uniAlphabet.lookupIndex("foo");
    Alphabet biAlphabet = new Alphabet();

    FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(
        uniAlphabet,
        biAlphabet,
        new int[]{0, 0, 0},
        new int[]{-1, -1, -1}
    );

    Instance instance = new Instance(fs, null, "no_bigrams", null);
    InstanceList list = new InstanceList(uniAlphabet);
    list.addThruPipe(instance);

    TopicalNGrams model = new TopicalNGrams(2);
    model.estimate(list, 5, 0, 0, null, new Randoms(82));

    assertNotNull(model);
}
@Test
public void testEstimate_documentWithOnlyBigramTransitions() {
    Alphabet uniAlphabet = new Alphabet();
    int indexA = uniAlphabet.lookupIndex("a");
    int indexB = uniAlphabet.lookupIndex("b");
    Alphabet biAlphabet = new Alphabet();
    biAlphabet.lookupIndex("a_b");

    FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(
        uniAlphabet,
        biAlphabet,
        new int[]{indexA, indexB},
        new int[]{-1, 0}
    );

    Instance instance = new Instance(fs, null, "only-bigrams", null);
    InstanceList list = new InstanceList(uniAlphabet);
    list.addThruPipe(instance);

    TopicalNGrams model = new TopicalNGrams(2);
    model.estimate(list, 2, 0, 0, null, new Randoms(999));

    assertNotNull(model);
}
@Test
public void testEstimate_documentWithAllBigramsDisabled() {
    Alphabet uniAlphabet = new Alphabet();
    int w1 = uniAlphabet.lookupIndex("x");
    int w2 = uniAlphabet.lookupIndex("y");
    Alphabet biAlphabet = new Alphabet();

    FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(
        uniAlphabet,
        biAlphabet,
        new int[]{w1, w2},
        new int[]{-1, -1}
    );

    Instance instance = new Instance(fs, null, "no-bigrams", null);
    InstanceList list = new InstanceList(uniAlphabet);
    list.addThruPipe(instance);

    TopicalNGrams model = new TopicalNGrams(2);
    model.estimate(list, 2, 0, 0, null, new Randoms(486));

    assertNotNull(model);
}
@Test
public void testEstimate_withHighAlphaLowBetaValues() {
    Alphabet uniAlphabet = new Alphabet();
    uniAlphabet.lookupIndex("a");
    uniAlphabet.lookupIndex("b");

    Alphabet biAlphabet = new Alphabet();
    biAlphabet.lookupIndex("a_b");

    FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(
        uniAlphabet,
        biAlphabet,
        new int[]{0, 1},
        new int[]{-1, 0}
    );

    Instance instance = new Instance(fs, null, "edge-distrib", null);
    InstanceList list = new InstanceList(uniAlphabet);
    list.addThruPipe(instance);

    TopicalNGrams model = new TopicalNGrams(5, 500.0, 1e-7, 1e-7, 0.1, 0.1, 0.1);
    model.estimate(list, 3, 0, 0, null, new Randoms(1001));

    assertNotNull(model);
}
@Test
public void testPrintTopWords_largeNumRequestedWithSmallCorpus() {
    Alphabet uniAlphabet = new Alphabet();
    uniAlphabet.lookupIndex("onlyword");

    Alphabet biAlphabet = new Alphabet();
    biAlphabet.lookupIndex("onlyword_onlyword");

    FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(
        uniAlphabet,
        biAlphabet,
        new int[]{0, 0},
        new int[]{-1, 0}
    );

    Instance instance = new Instance(fs, null, "scarce", null);
    InstanceList list = new InstanceList(uniAlphabet);
    list.addThruPipe(instance);

    TopicalNGrams model = new TopicalNGrams(1);
    model.estimate(list, 2, 0, 0, null, new Randoms(7));

    model.printTopWords(50, true);  
    model.printTopWords(50, false);
}
@Test
public void testPrintDocumentTopics_thresholdExactlyOne() throws IOException {
    Alphabet uniAlphabet = new Alphabet();
    uniAlphabet.lookupIndex("a");

    Alphabet biAlphabet = new Alphabet();
    biAlphabet.lookupIndex("a_a");

    FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(
        uniAlphabet,
        biAlphabet,
        new int[]{0},
        new int[]{-1}
    );

    Instance instance = new Instance(fs, null, "threshold-one", null);
    InstanceList list = new InstanceList(uniAlphabet);
    list.addThruPipe(instance);

    TopicalNGrams model = new TopicalNGrams(2);
    model.estimate(list, 5, 0, 0, null, new Randoms(4000));

    File file = folder.newFile("one-thresh.txt");
    PrintWriter pw = new PrintWriter(new FileWriter(file));
    model.printDocumentTopics(pw, 1.0, 2);
    pw.flush();
    pw.close();

    assertTrue(file.exists());
}
@Test
public void testPrintState_printsCorrectRowCount() throws IOException {
    Alphabet uniAlphabet = new Alphabet();
    uniAlphabet.lookupIndex("t1");
    Alphabet biAlphabet = new Alphabet();
    biAlphabet.lookupIndex("t1_t1");

    FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(
        uniAlphabet,
        biAlphabet,
        new int[]{0, 0, 0},
        new int[]{-1, 0, 0}
    );

    Instance instance = new Instance(fs, null, "3token", null);
    InstanceList list = new InstanceList(uniAlphabet);
    list.addThruPipe(instance);

    TopicalNGrams model = new TopicalNGrams(2);
    model.estimate(list, 1, 0, 0, null, new Randoms(537));

    File f = folder.newFile("state-check.txt");
    model.printState(f);

    BufferedReader reader = new BufferedReader(new FileReader(f));
    reader.readLine(); 
    String l1 = reader.readLine();
    String l2 = reader.readLine();
    String l3 = reader.readLine();
    String l4 = reader.readLine();
    reader.close();

    assertNotNull(l1);
    assertNotNull(l2);
    assertNotNull(l3);
    assertNull(l4);  
}
@Test
public void testPrintDocumentTopicsWithNegativeMaxLimit() throws IOException {
    Alphabet uniAlphabet = new Alphabet();
    uniAlphabet.lookupIndex("neg");

    Alphabet biAlphabet = new Alphabet();
    biAlphabet.lookupIndex("neg_neg");

    FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(
        uniAlphabet,
        biAlphabet,
        new int[]{0, 0},
        new int[]{-1, 0}
    );

    Instance inst = new Instance(fs, null, "neg-max", null);
    InstanceList list = new InstanceList(uniAlphabet);
    list.addThruPipe(inst);

    TopicalNGrams model = new TopicalNGrams(3);
    model.estimate(list, 5, 0, 0, null, new Randoms(600));

    File output = folder.newFile("neg-max.txt");
    PrintWriter pw = new PrintWriter(new FileWriter(output));
    model.printDocumentTopics(pw, 0.0, -1);
    pw.flush();
    pw.close();

    assertTrue(output.exists());
    assertTrue(output.length() > 0);
}
@Test
public void testEstimate_withEmptyFeatureSequence() {
    Alphabet uniAlphabet = new Alphabet();
    Alphabet biAlphabet = new Alphabet();

    FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(
        uniAlphabet,
        biAlphabet,
        new int[]{},
        new int[]{}
    );

    Instance instance = new Instance(fs, null, "empty-seq", null);
    InstanceList list = new InstanceList(uniAlphabet);
    list.addThruPipe(instance);

    TopicalNGrams model = new TopicalNGrams(3);
    model.estimate(list, 2, 0, 0, null, new Randoms(777));

    assertNotNull(model);
}
@Test
public void testEstimate_singleTokenPerDocument_multipleDocuments() {
    Alphabet uniAlphabet = new Alphabet();
    int idx = uniAlphabet.lookupIndex("single");

    Alphabet biAlphabet = new Alphabet();

    FeatureSequenceWithBigrams fs1 = new FeatureSequenceWithBigrams(
        uniAlphabet, biAlphabet,
        new int[]{idx},
        new int[]{-1}
    );
    FeatureSequenceWithBigrams fs2 = new FeatureSequenceWithBigrams(
        uniAlphabet, biAlphabet,
        new int[]{idx},
        new int[]{-1}
    );

    InstanceList list = new InstanceList(uniAlphabet);
    list.addThruPipe(new Instance(fs1, null, "doc1", null));
    list.addThruPipe(new Instance(fs2, null, "doc2", null));

    TopicalNGrams model = new TopicalNGrams(2);
    model.estimate(list, 3, 0, 0, null, new Randoms(42));

    assertNotNull(model);
}
@Test
public void testPrintDocumentTopics_withZeroLengthDocument() throws IOException {
    Alphabet uniAlphabet = new Alphabet();
    Alphabet biAlphabet = new Alphabet();

    FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(
        uniAlphabet,
        biAlphabet,
        new int[]{},
        new int[]{}
    );
    Instance instance = new Instance(fs, null, "zerolen", null);

    InstanceList list = new InstanceList(uniAlphabet);
    list.addThruPipe(instance);

    TopicalNGrams model = new TopicalNGrams(4);
    model.estimate(list, 3, 0, 0, null, new Randoms(12));

    File file = folder.newFile("zerolen.txt");
    PrintWriter pw = new PrintWriter(new FileWriter(file));
    model.printDocumentTopics(pw, 0.0, 2);
    pw.flush();
    pw.close();
    assertTrue(file.exists());
}
@Test
public void testEstimate_documentWithSpecialCharactersInAlphabet() {
    Alphabet uniAlphabet = new Alphabet();
    Alphabet biAlphabet = new Alphabet();

    int idx1 = uniAlphabet.lookupIndex("@start");
    int idx2 = uniAlphabet.lookupIndex("mid#dle");
    int idx3 = uniAlphabet.lookupIndex("end!");

    biAlphabet.lookupIndex("@start_mid#dle");

    FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(
        uniAlphabet, biAlphabet,
        new int[]{idx1, idx2, idx3},
        new int[]{-1, 0, 0}
    );

    Instance instance = new Instance(fs, null, "specialchars", null);
    InstanceList list = new InstanceList(uniAlphabet);
    list.addThruPipe(instance);

    TopicalNGrams model = new TopicalNGrams(3);
    model.estimate(list, 5, 0, 0, null, new Randoms(2023));

    assertNotNull(model);
}
@Test
public void testWrite_outputIOExceptionHandledGracefully() {
    Alphabet uniAlphabet = new Alphabet();
    uniAlphabet.lookupIndex("x");
    Alphabet biAlphabet = new Alphabet();
    biAlphabet.lookupIndex("x_y");

    FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(
        uniAlphabet, biAlphabet,
        new int[]{0},
        new int[]{-1}
    );

    InstanceList list = new InstanceList(uniAlphabet);
    list.addThruPipe(new Instance(fs, null, "savefail", null));

    TopicalNGrams model = new TopicalNGrams(2);
    model.estimate(list, 2, 0, 0, null, new Randoms(101));

    File readOnlyFile = new File("/dev/full"); 

    if (readOnlyFile.exists()) {
        model.write(readOnlyFile); 
    }
}
@Test
public void testPrintTopWords_zeroTokenTopic() {
    Alphabet uniAlphabet = new Alphabet();
    uniAlphabet.lookupIndex("zero");

    Alphabet biAlphabet = new Alphabet();

    FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(
        uniAlphabet, biAlphabet,
        new int[]{0},
        new int[]{-1}
    );

    Instance instance = new Instance(fs, null, "zerotopic", null);
    InstanceList list = new InstanceList(uniAlphabet);
    list.addThruPipe(instance);

    TopicalNGrams model = new TopicalNGrams(3);
    model.estimate(list, 1, 0, 0, null, new Randoms(75));

    model.printTopWords(3, true);
    model.printTopWords(3, false);
}
@Test
public void testEstimate_topicSamplingWithSingleTopic() {
    Alphabet uniAlphabet = new Alphabet();
    uniAlphabet.lookupIndex("uniq");

    Alphabet biAlphabet = new Alphabet();
    biAlphabet.lookupIndex("uniq_uniq");

    FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(
        uniAlphabet, biAlphabet,
        new int[]{0, 0},
        new int[]{-1, 0}
    );

    Instance instance = new Instance(fs, null, "single-topic", null);
    InstanceList list = new InstanceList(uniAlphabet);
    list.addThruPipe(instance);

    TopicalNGrams model = new TopicalNGrams(1);
    model.estimate(list, 5, 0, 0, null, new Randoms(300));

    assertNotNull(model);
}
@Test
public void testEstimate_withNullSourceObject() {
    Alphabet uniAlphabet = new Alphabet();
    uniAlphabet.lookupIndex("token");

    Alphabet biAlphabet = new Alphabet();
    biAlphabet.lookupIndex("token_token");

    FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(
        uniAlphabet, biAlphabet,
        new int[]{0, 0}, new int[]{-1, 0}
    );

    Instance instance = new Instance(fs, null, null, null);
    InstanceList instanceList = new InstanceList(uniAlphabet);
    instanceList.addThruPipe(instance);

    TopicalNGrams model = new TopicalNGrams(3);
    model.estimate(instanceList, 2, 0, 0, null, new Randoms(12345));

    assertNotNull(model);
}
@Test
public void testPrintDocumentTopics_withNullPrintWriter() {
    Alphabet uniAlphabet = new Alphabet();
    uniAlphabet.lookupIndex("word");

    Alphabet biAlphabet = new Alphabet();
    biAlphabet.lookupIndex("word_word");

    FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(
        uniAlphabet, biAlphabet,
        new int[]{0, 0}, new int[]{-1, 0}
    );

    Instance instance = new Instance(fs, null, "doc", null);
    InstanceList list = new InstanceList(uniAlphabet);
    list.addThruPipe(instance);

    TopicalNGrams model = new TopicalNGrams(2);
    model.estimate(list, 3, 0, 0, null, new Randoms(42));

    try {
        model.printDocumentTopics((PrintWriter) null);
        fail("Expected NullPointerException");
    } catch (NullPointerException expected) {
        
    }
}
@Test
public void testPrintTopWords_withTopicHavingNoUnigrams() {
    Alphabet uniAlphabet = new Alphabet();
    uniAlphabet.lookupIndex("a");

    Alphabet biAlphabet = new Alphabet();
    biAlphabet.lookupIndex("a_a");

    FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(
        uniAlphabet, biAlphabet,
        new int[]{0, 0}, new int[]{-1, 1}
    );

    Instance instance = new Instance(fs, null, "doc", null);
    InstanceList list = new InstanceList(uniAlphabet);
    list.addThruPipe(instance);

    TopicalNGrams model = new TopicalNGrams(1);
    model.estimate(list, 2, 0, 0, null, new Randoms(77));

    model.printTopWords(10, true);
    model.printTopWords(10, false);
}
@Test
public void testEstimate_withZeroAlphaGammaAndBeta() {
    Alphabet uniAlphabet = new Alphabet();
    uniAlphabet.lookupIndex("zero");

    Alphabet biAlphabet = new Alphabet();
    biAlphabet.lookupIndex("zero_zero");

    FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(
        uniAlphabet, biAlphabet,
        new int[]{0, 0}, new int[]{-1, 0}
    );

    InstanceList list = new InstanceList(uniAlphabet);
    list.addThruPipe(new Instance(fs, null, "doc", null));

    try {
        TopicalNGrams model = new TopicalNGrams(2, 0.0, 0.0, 0.0, 0.1, 0.1, 0.1);
        model.estimate(list, 3, 0, 0, null, new Randoms(99));
        assertNotNull(model);
    } catch (Exception e) {
        fail("Should handle zero smoothing gracefully: " + e.getMessage());
    }
}
@Test
public void testSampleTopics_boundaryPositionAtEndOfSequence() {
    Alphabet uniAlphabet = new Alphabet();
    uniAlphabet.lookupIndex("last");

    Alphabet biAlphabet = new Alphabet();
    biAlphabet.lookupIndex("last_last");

    FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(
        uniAlphabet, biAlphabet,
        new int[]{0, 0, 0},
        new int[]{-1, 0, 1}
    );

    Instance instance = new Instance(fs, null, "endseq", null);
    InstanceList list = new InstanceList(uniAlphabet);
    list.addThruPipe(instance);

    TopicalNGrams model = new TopicalNGrams(3);
    model.estimate(list, 5, 0, 0, null, new Randoms(1010));

    assertNotNull(model);
}
@Test
public void testPrintState_withCustomTokenAlphabetMapping() throws IOException {
    Alphabet uniAlphabet = new Alphabet();
    int idx = uniAlphabet.lookupIndex("X_Token");

    Alphabet biAlphabet = new Alphabet();
    biAlphabet.lookupIndex("X_Token:X_Token");

    FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(
        uniAlphabet, biAlphabet,
        new int[]{idx, idx, idx},
        new int[]{-1, 0, 0}
    );

    Instance inst = new Instance(fs, null, "tokenmap", null);
    InstanceList list = new InstanceList(uniAlphabet);
    list.addThruPipe(inst);

    TopicalNGrams model = new TopicalNGrams(2);
    model.estimate(list, 5, 0, 0, null, new Randoms(1212));

    File file = folder.newFile("mapped_state.txt");
    model.printState(file);
    assertTrue(file.length() > 0);
}
@Test
public void testEstimate_withUnequalTokenAndGramArrays() {
    Alphabet uniAlphabet = new Alphabet();
    uniAlphabet.lookupIndex("ab");

    Alphabet biAlphabet = new Alphabet();
    biAlphabet.lookupIndex("ab_ab");

    FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(
        uniAlphabet,
        biAlphabet,
        new int[]{0},
        new int[]{-1, 0} 
    );

    Instance instance = new Instance(fs, null, "mismatch", null);
    InstanceList list = new InstanceList(uniAlphabet);
    list.addThruPipe(instance);

    TopicalNGrams model = new TopicalNGrams(2);

    try {
        model.estimate(list, 1, 0, 0, null, new Randoms(333));
        fail("Expected ArrayIndexOutOfBounds or AssertionError");
    } catch (Exception expected) {
        
    }
}
@Test
public void testEstimate_withSingleWordBigramChain() {
    Alphabet uniAlphabet = new Alphabet();
    int index = uniAlphabet.lookupIndex("w");
    Alphabet biAlphabet = new Alphabet();
    biAlphabet.lookupIndex("w_w");

    FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(
        uniAlphabet,
        biAlphabet,
        new int[]{index, index, index},
        new int[]{-1, 0, 1}
    );

    Instance instance = new Instance(fs, null, "chain", null);
    InstanceList list = new InstanceList(uniAlphabet);
    list.addThruPipe(instance);

    TopicalNGrams model = new TopicalNGrams(2);
    model.estimate(list, 10, 0, 0, null, new Randoms(111));

    assertNotNull(model);
}
@Test
public void testEstimate_withBigramOnlyAfterFirstPosition() {
    Alphabet uniAlphabet = new Alphabet();
    int a = uniAlphabet.lookupIndex("a");
    int b = uniAlphabet.lookupIndex("b");

    Alphabet biAlphabet = new Alphabet();
    biAlphabet.lookupIndex("a_b");

    FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(
        uniAlphabet,
        biAlphabet,
        new int[]{a, b},
        new int[]{-1, 1}
    );

    Instance instance = new Instance(fs, null, "leading-unigram", null);
    InstanceList list = new InstanceList(uniAlphabet);
    list.addThruPipe(instance);

    TopicalNGrams model = new TopicalNGrams(1);
    model.estimate(list, 5, 0, 0, null, new Randoms(222));

    assertNotNull(model);
}
@Test
public void testPhraseExtractionLogic_multipleNestedBigrams() {
    Alphabet uniAlphabet = new Alphabet();
    Alphabet biAlphabet = new Alphabet();

    int idxA = uniAlphabet.lookupIndex("a");
    int idxB = uniAlphabet.lookupIndex("b");
    int idxC = uniAlphabet.lookupIndex("c");

    biAlphabet.lookupIndex("a_b");
    biAlphabet.lookupIndex("b_c");

    FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(
        uniAlphabet,
        biAlphabet,
        new int[]{idxA, idxB, idxC},
        new int[]{-1, 1, 1}
    );

    Instance instance = new Instance(fs, null, "ngram-compound", null);
    InstanceList list = new InstanceList(uniAlphabet);
    list.addThruPipe(instance);

    TopicalNGrams model = new TopicalNGrams(1);
    model.estimate(list, 10, 0, 0, null, new Randoms(333));
    model.printTopWords(5, true);
    model.printTopWords(5, false);
}
@Test
public void testEstimate_withMaxSmoothingValues() {
    Alphabet uniAlphabet = new Alphabet();
    uniAlphabet.lookupIndex("t");

    Alphabet biAlphabet = new Alphabet();
    biAlphabet.lookupIndex("t_t");

    FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(
        uniAlphabet,
        biAlphabet,
        new int[]{0, 0},
        new int[]{-1, 0}
    );

    Instance inst = new Instance(fs, null, "max-smooth", null);
    InstanceList list = new InstanceList(uniAlphabet);
    list.addThruPipe(inst);

    TopicalNGrams model = new TopicalNGrams(2, 1000.0, 1000.0, 1000.0, 1000.0, 1000.0, 1000.0);
    model.estimate(list, 2, 0, 0, null, new Randoms(919));
    assertNotNull(model);
}
@Test
public void testSingleTokenPhraseHandling() {
    Alphabet uniAlphabet = new Alphabet();
    int idx = uniAlphabet.lookupIndex("solo");

    Alphabet biAlphabet = new Alphabet();

    FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(
        uniAlphabet, biAlphabet,
        new int[]{idx},
        new int[]{-1}
    );

    Instance inst = new Instance(fs, null, "single-token", null);
    InstanceList list = new InstanceList(uniAlphabet);
    list.addThruPipe(inst);

    TopicalNGrams model = new TopicalNGrams(1);
    model.estimate(list, 3, 0, 0, null, new Randoms(101));

    model.printTopWords(5, true);
}
@Test
public void testEstimateWithTwoTopicsAndConflictingCounts() {
    Alphabet uniAlphabet = new Alphabet();
    int a = uniAlphabet.lookupIndex("a");
    int b = uniAlphabet.lookupIndex("b");

    Alphabet biAlphabet = new Alphabet();
    biAlphabet.lookupIndex("a_b");

    FeatureSequenceWithBigrams fs = new FeatureSequenceWithBigrams(
        uniAlphabet,
        biAlphabet,
        new int[]{a, b},
        new int[]{-1, 0}
    );

    Instance instance = new Instance(fs, null, "conflict-topic", null);
    InstanceList list = new InstanceList(uniAlphabet);
    list.addThruPipe(instance);

    TopicalNGrams model = new TopicalNGrams(2);
    model.estimate(list, 5, 0, 0, null, new Randoms(9090));

    assertNotNull(model);
}
@Test
public void testPrintStateHandlesMultipleDocsDistinctLengths() throws IOException {
    Alphabet uniAlphabet = new Alphabet();
    int w = uniAlphabet.lookupIndex("w");
    Alphabet biAlphabet = new Alphabet();
    biAlphabet.lookupIndex("w_w");

    FeatureSequenceWithBigrams fs1 = new FeatureSequenceWithBigrams(
        uniAlphabet,
        biAlphabet,
        new int[]{w, w},
        new int[]{-1, 0}
    );
    FeatureSequenceWithBigrams fs2 = new FeatureSequenceWithBigrams(
        uniAlphabet,
        biAlphabet,
        new int[]{w, w, w},
        new int[]{-1, 0, 1}
    );

    InstanceList list = new InstanceList(uniAlphabet);
    list.addThruPipe(new Instance(fs1, null, "doc1", null));
    list.addThruPipe(new Instance(fs2, null, "doc2", null));

    TopicalNGrams model = new TopicalNGrams(2);
    model.estimate(list, 2, 0, 0, null, new Randoms(123));
    File f = folder.newFile("multi_state.txt");
    model.printState(f);
    assertTrue(f.exists());
} 
}