public class MemoryEfficientNB_wosr_2_GPTLLMTest { 

 @Test
    public void testAllocateSpaceInitializesStructuresCorrectly() {
        FeatureMap map = new FeatureMap();
        map.fidToWord.put(0, "alpha");
        map.fidToWord.put(1, "beta");
        map.dim = 2;
        MemoryEfficientNB nb = new MemoryEfficientNB(map, 3);

        assertNotNull(nb.map);
        assertEquals(3, nb.classCounts.length);
        assertEquals(3, nb.weights.length);
        assertEquals(2, nb.wordCounts.length);
        assertEquals(3, nb.fidCounts.size());
    }
@Test
    public void testWeightedOnlineLearningIncrementsCounts() {
        FeatureMap map = new FeatureMap();
        map.fidToWord.put(0, "alpha");
        map.fidToWord.put(1, "beta");
        map.dim = 2;
        MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);

        int[] features = new int[]{0, 1};
        double weight = 2.0;
        int classID = 1;

        nb.weightedOnlineLearning(features, weight, classID);

        assertEquals(2.0, nb.sampleSize, 0.0001);
        assertEquals(2.0, nb.classCounts[1], 0.0001);
        assertEquals(4.0, nb.fidCount, 0.0001);
        assertEquals(2.0, nb.weights[1], 0.0001);
        assertEquals(2.0, nb.wordCounts[0], 0.0001);
        assertEquals(2.0, nb.wordCounts[1], 0.0001);
        assertEquals(2.0, nb.fidCounts.elementAt(1).get(0), 0.0001);
        assertEquals(2.0, nb.fidCounts.elementAt(1).get(1), 0.0001);
    }
@Test
    public void testGetPriorReturnsZeroOnEmptySample() {
        FeatureMap map = new FeatureMap();
        map.dim = 1;
        MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
        double prior = nb.getPrior(0);
        assertEquals(0.0, prior, 0.0001);
    }
@Test
    public void testGetPriorWithNonZeroSample() {
        FeatureMap map = new FeatureMap();
        map.dim = 1;
        MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
        nb.classCounts[0] = 5.0;
        nb.sampleSize = 10.0;
        double prior = nb.getPrior(0);
        assertEquals(0.5, prior, 0.0001);
    }
@Test
    public void testClassifyReturnsCorrectClass() {
        FeatureMap map = new FeatureMap();
        map.fidToWord.put(0, "foo");
        map.fidToWord.put(1, "bar");
        map.dim = 2;

        MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
        nb.weightedOnlineLearning(new int[]{0}, 2.0, 0);
        nb.weightedOnlineLearning(new int[]{1}, 1.0, 1);

        Document doc = new Document("test");
        doc.classID = 0;
        doc.featureSetFids.add(0);

        int result = nb.classify(doc, -1);
        assertEquals(0, result);
    }
@Test
    public void testClassifyBelowThresholdReturnsNegativeOne() {
        FeatureMap map = new FeatureMap();
        map.fidToWord.put(0, "aaa");
        map.fidToWord.put(1, "bbb");
        map.dim = 2;

        MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
        nb.weightedOnlineLearning(new int[]{1}, 1.0, 1);

        Document doc = new Document("doc");
        doc.classID = 0;
        doc.featureSetFids.add(0);

        int result = nb.classify(doc, 0.9);
        assertEquals(-1, result);
    }
@Test
    public void testGetPredictionConfidenceDistribution() {
        FeatureMap map = new FeatureMap();
        map.fidToWord.put(0, "alpha");
        map.fidToWord.put(1, "beta");
        map.dim = 2;

        MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
        nb.weightedOnlineLearning(new int[]{0}, 4.0, 0);
        nb.weightedOnlineLearning(new int[]{1}, 1.0, 1);

        Document doc = new Document("testDoc");
        doc.featureSetFids.add(0);

        double[] confidences = nb.getPredictionConfidence(doc);

        assertEquals(2, confidences.length);
        assertTrue(confidences[0] > confidences[1]);
        assertTrue(confidences[0] + confidences[1] <= 1.05); 
    }
@Test
    public void testGetTopPmiWordsIncludesExpectedWord() {
        FeatureMap map = new FeatureMap();
        map.fidToWord.put(0, "dog");
        map.dim = 1;

        MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
        nb.wordCounts[0] = 5.0;
        nb.weights[0] = 5.0;
        nb.fidCount = 5.0;
        Hashtable<Integer, Double> table = new Hashtable<>();
        table.put(0, 5.0);
        nb.fidCounts.set(0, table);

        CharacteristicWords words = nb.getTopPmiWords(0, 10, 0.9, 3);
        assertEquals(1, words.topWords.size());
        assertEquals("dog", words.topWords.get(0));
    }
@Test
    public void testToBeKeptPassesWhenRatioAndLengthSatisfied() {
        Vector<String> tokens = new Vector<>();
        tokens.add("apple");
        tokens.add("banana");
        tokens.add("apple");

        Hashtable<String, Integer> coolWords = new Hashtable<>();
        coolWords.put("apple", 1);
        boolean kept = MemoryEfficientNB.toBeKept(tokens, coolWords, 0.5, 2);
        assertTrue(kept);
    }
@Test
    public void testToBeKeptFailsWhenTokenCountTooLow() {
        Vector<String> tokens = new Vector<>();
        tokens.add("single");

        Hashtable<String, Integer> coolWords = new Hashtable<>();
        coolWords.put("single", 1);
        boolean kept = MemoryEfficientNB.toBeKept(tokens, coolWords, 0.5, 2);
        assertFalse(kept);
    }
@Test
    public void testToBeKeptFailsWhenCoolWordRatioTooLow() {
        Vector<String> tokens = new Vector<>();
        tokens.add("apple");
        tokens.add("banana");
        tokens.add("cherry");

        Hashtable<String, Integer> coolWords = new Hashtable<>();
        coolWords.put("banana", 1);

        boolean kept = MemoryEfficientNB.toBeKept(tokens, coolWords, 0.9, 2);
        assertFalse(kept);
    }
@Test
    public void testGetExtendedFeaturesContainsConfidence() {
        FeatureMap map = new FeatureMap();
        map.fidToWord.put(0, "science");
        map.dim = 1;

        MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
        nb.methodName = "naiveBayes";

        nb.weightedOnlineLearning(new int[]{0}, 3.0, 1);

        Document doc = new Document("text");
        doc.featureSetFids.add(0);

        String result = nb.getExtendedFeatures(doc);
        assertTrue(result.contains("naiveBayes1("));
    }
@Test
    public void testGetFidProbReturnsSmoothedValueIfAbsent() {
        FeatureMap map = new FeatureMap();
        map.dim = 5;

        MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
        double val = nb.getFidProb(2, 1);
        assertEquals(MemoryEfficientNB.smooth / 5, val, 0.0001);
    }
@Test
    public void testGetFidProbReturnsProperConditionalValueIfPresent() {
        FeatureMap map = new FeatureMap();
        map.dim = 3;
        MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
        Hashtable<Integer, Double> h = new Hashtable<>();
        h.put(2, 4.0);
        nb.fidCounts.set(1, h);
        nb.weights[1] = 10.0;

        double val = nb.getFidProb(2, 1);
        assertEquals((1 - MemoryEfficientNB.smooth) * 4.0 / 10.0, val, 0.0001);
    } 
}