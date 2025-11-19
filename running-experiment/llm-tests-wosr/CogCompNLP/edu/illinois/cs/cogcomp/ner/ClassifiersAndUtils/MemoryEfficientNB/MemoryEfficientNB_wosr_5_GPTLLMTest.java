public class MemoryEfficientNB_wosr_5_GPTLLMTest { 

 @Test
    public void testAllocateSpaceInitializesFields() {
        FeatureMap map = new FeatureMap("test");
        map.dim = 5;
        int numClasses = 3;

        MemoryEfficientNB nb = new MemoryEfficientNB(map, numClasses);

        assertEquals(map, nb.map);
        assertEquals(numClasses, nb.classesN);
        assertNotNull(nb.classCounts);
        assertEquals(numClasses, nb.classCounts.length);
        assertNotNull(nb.fidCounts);
        assertEquals(numClasses, nb.fidCounts.size());
        assertNotNull(nb.weights);
        assertEquals(numClasses, nb.weights.length);
        assertNotNull(nb.wordCounts);
        assertEquals(map.dim, nb.wordCounts.length);
    }
@Test
    public void testOnlineLearningAccumulatesCounts() {
        FeatureMap map = new FeatureMap("test");
        map.dim = 10;
        map.wordToFid.put("feature1", 1);
        map.wordToFid.put("feature2", 2);
        map.fidToWord.put(1, "feature1");
        map.fidToWord.put(2, "feature2");

        Document doc = new Document();
        doc.words.add("feature1");
        doc.words.add("feature2");
        doc.classID = 1;

        MemoryEfficientNB nb = new MemoryEfficientNB(map, 3);
        nb.onlineLearning(doc);

        assertEquals(1.0, nb.sampleSize, 0.0001);
        assertEquals(1.0, nb.classCounts[1], 0.0001);
        assertEquals(1.0, nb.weights[1], 0.0001);
        assertEquals(1.0, nb.wordCounts[1], 0.0001);
        assertEquals(1.0, nb.wordCounts[2], 0.0001);
        assertTrue(nb.fidCounts.get(1).containsKey(1));
        assertTrue(nb.fidCounts.get(1).containsKey(2));
    }
@Test
    public void testClassifyReturnsCorrectClass() {
        FeatureMap map = new FeatureMap("test");
        map.dim = 5;
        map.wordToFid.put("x", 1);
        map.fidToWord.put(1, "x");

        MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);

        Document doc = new Document();
        doc.words.add("x");
        doc.classID = 0;

        nb.weightedOnlineLearning(doc.getActiveFid(map), 5.0, 0);

        int prediction = nb.classify(doc, -1);
        assertEquals(0, prediction);
    }
@Test
    public void testWeightedOnlineLearningAddsCorrectAmounts() {
        FeatureMap map = new FeatureMap("test");
        map.dim = 3;
        map.wordToFid.put("w1", 0);
        map.wordToFid.put("w2", 1);
        map.fidToWord.put(0, "w1");
        map.fidToWord.put(1, "w2");

        MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
        int[] activeFeatures = new int[]{0, 1};

        nb.weightedOnlineLearning(activeFeatures, 2.0, 0);

        assertEquals(2.0, nb.sampleSize, 0.00001);
        assertEquals(2.0, nb.classCounts[0], 0.00001);
        assertEquals(4.0, nb.fidCount, 0.00001);
        assertEquals(2.0, nb.weights[0], 0.00001);
        assertEquals(2.0, nb.wordCounts[0], 0.00001);
        assertEquals(2.0, nb.wordCounts[1], 0.00001);
        assertTrue(nb.fidCounts.get(0).containsKey(0));
        assertTrue(nb.fidCounts.get(0).containsKey(1));
    }
@Test
    public void testGetFidProbReturnsSmoothedValueIfMissing() {
        FeatureMap map = new FeatureMap("test");
        map.dim = 4;

        MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
        double prob = nb.getFidProb(1, 1);

        assertEquals(MemoryEfficientNB.smooth / 4, prob, 0.0001);
    }
@Test
    public void testGetFidProbReturnsActualProbabilityIfExists() {
        FeatureMap map = new FeatureMap("test");
        map.dim = 4;

        MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
        nb.weights[0] = 2.0;
        nb.fidCounts.get(0).put(1, 1.0);

        double prob = nb.getFidProb(1, 0);
        assertEquals((1.0 - MemoryEfficientNB.smooth) * 1.0 / 2.0, prob, 0.0001);
    }
@Test
    public void testGetPriorZeroSampleSizeReturnsZero() {
        FeatureMap map = new FeatureMap("test");
        map.dim = 2;

        MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
        nb.sampleSize = 0;
        double prior = nb.getPrior(0);
        assertEquals(0.0, prior, 0.0001);
    }
@Test
    public void testGetPriorReturnsCorrectValue() {
        FeatureMap map = new FeatureMap("test");
        map.dim = 2;

        MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
        nb.sampleSize = 5.0;
        nb.classCounts[0] = 2.0;

        double prior = nb.getPrior(0);
        assertEquals(0.4, prior, 0.0001);
    }
@Test
    public void testClassifyReturnsNegativeOneOnLowConfidence() {
        FeatureMap map = new FeatureMap("test");
        map.dim = 1;
        map.wordToFid.put("alpha", 0);
        map.fidToWord.put(0, "alpha");

        MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
        nb.sampleSize = 1.0;
        nb.classCounts[0] = 1.0;
        nb.weights[0] = 1.0;
        nb.fidCounts.get(0).put(0, 1.0);
        nb.wordCounts[0] = 1.0;
        nb.fidCount = 1.0;

        Document doc = new Document();
        doc.words.add("alpha");
        doc.classID = 0;

        int label = nb.classify(doc, 0.95);
        assertEquals(-1, label);
    }
@Test
    public void testClassifyThresholdIsSatisfiedReturnsCorrectClass() {
        FeatureMap map = new FeatureMap("test");
        map.dim = 1;
        map.wordToFid.put("x", 0);
        map.fidToWord.put(0, "x");

        MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
        nb.sampleSize = 2.0;
        nb.classCounts[0] = 2.0;
        nb.weights[0] = 2.0;
        nb.fidCounts.get(0).put(0, 2.0);
        nb.wordCounts[0] = 2.0;
        nb.fidCount = 2.0;

        Document doc = new Document();
        doc.words.add("x");
        doc.classID = 0;

        int label = nb.classify(doc, 0.5);
        assertEquals(0, label);
    }
@Test
    public void testGetTopPmiWordsReturnsValidMap() {
        FeatureMap map = new FeatureMap("test");
        map.dim = 2;
        map.fidToWord.put(0, "foo");
        map.fidToWord.put(1, "bar");

        MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
        nb.weights[0] = 5.0;
        nb.fidCounts.get(0).put(0, 5.0);
        nb.wordCounts[0] = 5.0;
        nb.fidCount = 5.0;

        Hashtable<String, Integer> result =
                nb.getTopPmiWords(1, 0.5, 1);

        assertTrue(result.containsKey("foo"));
    }
@Test
    public void testToBeKeptReturnsTrueWhenRequirementsMet() {
        Vector<String> tokens = new Vector<>();
        tokens.add("a");
        tokens.add("b");
        tokens.add("c");

        Hashtable<String, Integer> coolWords = new Hashtable<>();
        coolWords.put("a", 1);
        coolWords.put("b", 1);

        boolean result = MemoryEfficientNB.toBeKept(tokens, coolWords, 0.5, 3);
        assertTrue(result);
    }
@Test
    public void testToBeKeptReturnsFalseWhenRatioTooLow() {
        Vector<String> tokens = new Vector<>();
        tokens.add("x");
        tokens.add("y");
        tokens.add("z");

        Hashtable<String, Integer> coolWords = new Hashtable<>();
        coolWords.put("x", 1);

        boolean result = MemoryEfficientNB.toBeKept(tokens, coolWords, 0.8, 3);
        assertFalse(result);
    }
@Test
    public void testGetExtendedFeaturesReturnsFormattedString() {
        FeatureMap map = new FeatureMap("test");
        map.dim = 1;
        map.wordToFid.put("q", 0);
        map.fidToWord.put(0, "q");

        MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
        nb.methodName = "METHOD";
        nb.sampleSize = 2.0;
        nb.classCounts[0] = 2.0;
        nb.weights[0] = 2.0;
        nb.fidCounts.get(0).put(0, 2.0);
        nb.wordCounts[0] = 2.0;
        nb.fidCount = 2.0;

        Document doc = new Document();
        doc.words.add("q");
        doc.classID = 0;

        String result = nb.getExtendedFeatures(doc);
        assertTrue(result.contains("METHOD0("));
    } 
}