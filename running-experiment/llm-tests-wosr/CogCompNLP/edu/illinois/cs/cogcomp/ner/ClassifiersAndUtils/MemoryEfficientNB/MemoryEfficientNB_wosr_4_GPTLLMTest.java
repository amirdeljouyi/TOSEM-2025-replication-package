public class MemoryEfficientNB_wosr_4_GPTLLMTest { 

 @Test
    public void testConstructorWithFeatureMapAndClasses() {
        FeatureMap featureMap = new FeatureMap("testMap");
        featureMap.dim = 5;
        MemoryEfficientNB nb = new MemoryEfficientNB(featureMap, 3);

        assertEquals(3, nb.classCounts.length);
        assertEquals(3, nb.fidCounts.size());
        assertEquals(3, nb.weights.length);
        assertEquals(5, nb.wordCounts.length);
        assertSame(featureMap, nb.map);
    }
@Test
    public void testWeightedOnlineLearningSingleFeature() {
        FeatureMap featureMap = new FeatureMap("testMap");
        featureMap.dim = 10;
        MemoryEfficientNB nb = new MemoryEfficientNB(featureMap, 2);

        int[] features = new int[]{2};
        nb.weightedOnlineLearning(features, 1.0, 0);

        assertEquals(1.0, nb.sampleSize, 0.0001);
        assertEquals(1.0, nb.classCounts[0], 0.0001);
        assertEquals(1.0, nb.fidCount, 0.0001);
        assertEquals(1.0, nb.weights[0], 0.0001);
        assertEquals(1.0, nb.wordCounts[2], 0.0001);
        assertEquals(1.0, nb.fidCounts.elementAt(0).get(2), 0.0001);
    }
@Test
    public void testGetPriorNormalCase() {
        FeatureMap featureMap = new FeatureMap("testMap");
        featureMap.dim = 4;
        MemoryEfficientNB nb = new MemoryEfficientNB(featureMap, 2);

        nb.sampleSize = 5.0;
        nb.classCounts[0] = 2.0;
        nb.classCounts[1] = 3.0;

        assertEquals(0.4, nb.getPrior(0), 0.0001);
        assertEquals(0.6, nb.getPrior(1), 0.0001);
    }
@Test
    public void testGetPriorZeroSampleSize() {
        FeatureMap featureMap = new FeatureMap("testMap");
        featureMap.dim = 2;
        MemoryEfficientNB nb = new MemoryEfficientNB(featureMap, 2);

        assertEquals(0.0, nb.getPrior(0), 0.0001);
        assertEquals(0.0, nb.getPrior(1), 0.0001);
    }
@Test
    public void testGetFidProbSmoothed() {
        FeatureMap featureMap = new FeatureMap("testMap");
        featureMap.dim = 10;
        MemoryEfficientNB nb = new MemoryEfficientNB(featureMap, 2);

        double prob = nb.getFidProb(5, 1);
        assertEquals(MemoryEfficientNB.smooth / 10, prob, 0.000001);
    }
@Test
    public void testGetFidProbObserved() {
        FeatureMap featureMap = new FeatureMap("testMap");
        featureMap.dim = 10;
        MemoryEfficientNB nb = new MemoryEfficientNB(featureMap, 1);
        nb.weights[0] = 2.0;
        nb.fidCounts.elementAt(0).put(1, 1.0);

        double prob = nb.getFidProb(1, 0);
        assertEquals((1 - MemoryEfficientNB.smooth) * 1.0 / 2.0, prob, 0.000001);
    }
@Test
    public void testGetPredictionConfidence() {
        FeatureMap featureMap = new FeatureMap("testMap");
        featureMap.dim = 5;
        MemoryEfficientNB nb = new MemoryEfficientNB(featureMap, 2);

        Document doc = new Document();
        doc.classID = 0;
        nb.weightedOnlineLearning(new int[]{0, 1}, 1.0, 0);
        nb.weightedOnlineLearning(new int[]{2}, 1.0, 1);

        double[] conf = nb.getPredictionConfidence(doc);
        assertEquals(2, conf.length);
        assertTrue(conf[0] >= 0);
        assertTrue(conf[1] >= 0);
        assertEquals(1.0, conf[0] + conf[1], 0.0001);
    }
@Test
    public void testClassifyThresholdSatisfied() {
        FeatureMap featureMap = new FeatureMap("testMap");
        featureMap.dim = 3;
        MemoryEfficientNB nb = new MemoryEfficientNB(featureMap, 2);

        Document doc = new Document();
        doc.classID = 1;
        nb.weightedOnlineLearning(new int[]{0, 2}, 1.0, 1);

        int result = nb.classify(doc, 0.0);
        assertTrue(result == 0 || result == 1);
    }
@Test
    public void testClassifyThresholdTooHigh() {
        FeatureMap featureMap = new FeatureMap("testMap");
        featureMap.dim = 3;
        MemoryEfficientNB nb = new MemoryEfficientNB(featureMap, 2);

        Document doc = new Document();
        doc.classID = 1;
        nb.weightedOnlineLearning(new int[]{0, 2}, 1.0, 1);

        int result = nb.classify(doc, 1.1);
        assertEquals(-1, result);
    }
@Test
    public void testGetExtendedFeaturesOutputFormat() {
        FeatureMap featureMap = new FeatureMap("testMap");
        featureMap.dim = 3;
        MemoryEfficientNB nb = new MemoryEfficientNB(featureMap, 2);
        nb.methodName = "nb";
        Document doc = new Document();
        doc.classID = 1;
        nb.weightedOnlineLearning(new int[]{1}, 1.0, 1);

        String features = nb.getExtendedFeatures(doc);
        assertTrue(features.contains("nb1("));
    }
@Test
    public void testToBeKeptTrue() {
        Vector<String> tokens = new Vector<>();
        tokens.add("apple");
        tokens.add("banana");
        tokens.add("cherry");

        Hashtable<String, Integer> coolWords = new Hashtable<>();
        coolWords.put("apple", 1);
        coolWords.put("banana", 1);

        boolean result = MemoryEfficientNB.toBeKept(tokens, coolWords, 0.5, 3);
        assertTrue(result);
    }
@Test
    public void testToBeKeptFalseDueToLowRatio() {
        Vector<String> tokens = new Vector<>();
        tokens.add("apple");
        tokens.add("banana");
        tokens.add("cherry");

        Hashtable<String, Integer> coolWords = new Hashtable<>();
        coolWords.put("kiwi", 1);

        boolean result = MemoryEfficientNB.toBeKept(tokens, coolWords, 0.5, 3);
        assertFalse(result);
    }
@Test
    public void testToBeKeptFalseDueToShortLength() {
        Vector<String> tokens = new Vector<>();
        tokens.add("apple");

        Hashtable<String, Integer> coolWords = new Hashtable<>();
        coolWords.put("apple", 1);

        boolean result = MemoryEfficientNB.toBeKept(tokens, coolWords, 0.5, 2);
        assertFalse(result);
    }
@Test
    public void testGetTopPmiWordsBasic() {
        FeatureMap featureMap = new FeatureMap("testMap");
        featureMap.dim = 2;
        featureMap.fidToWord.put(0, "java");
        featureMap.fidToWord.put(1, "python");

        MemoryEfficientNB nb = new MemoryEfficientNB(featureMap, 1);
        nb.wordCounts[0] = 10.0;
        nb.wordCounts[1] = 8.0;
        nb.fidCount = 18.0;
        nb.weights[0] = 10.0;
        nb.fidCounts.elementAt(0).put(0, 9.0);
        nb.fidCounts.elementAt(0).put(1, 1.0);

        CharacteristicWords cw = nb.getTopPmiWords(0, 2, 0.5, 1);
        assertTrue(cw.topWords.contains("java"));
    }
@Test
    public void testSaveAndReload() throws Exception {
        String baseName = "tempModel";
        FeatureMap featureMap = new FeatureMap(baseName + ".nb.featuremap");
        featureMap.dim = 2;
        featureMap.fidToWord.put(0, "word1");
        featureMap.fidToWord.put(1, "word2");

        featureMap.save(baseName + ".nb.featuremap");

        MemoryEfficientNB nb1 = new MemoryEfficientNB(featureMap, 1);
        nb1.weightedOnlineLearning(new int[]{0, 1}, 1.0, 0);
        nb1.save(baseName);

        MemoryEfficientNB nb2 = new MemoryEfficientNB(baseName);
        assertEquals(nb1.sampleSize, nb2.sampleSize, 0.0001);
        assertEquals(nb1.weights[0], nb2.weights[0], 0.0001);
        assertEquals(nb1.wordCounts[0], nb2.wordCounts[0], 0.0001);

        new File(baseName).delete();
        new File(baseName + ".nb.featuremap").delete();
    } 
}