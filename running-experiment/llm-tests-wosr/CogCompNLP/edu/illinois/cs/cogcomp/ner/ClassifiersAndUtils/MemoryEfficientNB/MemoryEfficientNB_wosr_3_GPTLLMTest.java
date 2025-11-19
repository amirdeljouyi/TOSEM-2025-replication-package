public class MemoryEfficientNB_wosr_3_GPTLLMTest { 

 @Test
    public void testAllocateSpaceInitializesCorrectFields() {
        FeatureMap map = new FeatureMap();
        map.dim = 5;
        MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
        assertEquals(2, nb.fidCounts.size());
        assertEquals(2, nb.weights.length);
        assertEquals(5, nb.wordCounts.length);
        assertSame(map, nb.map);
    }
@Test
    public void testOnlineLearningUpdatesStatisticsCorrectly() {
        FeatureMap map = new FeatureMap();
        map.dim = 3;
        map.wordToFid.put("a", 0);
        map.wordToFid.put("b", 1);
        map.wordToFid.put("c", 2);
        map.fidToWord.put(0, "a");
        map.fidToWord.put(1, "b");
        map.fidToWord.put(2, "c");

        Document doc = new Document();
        doc.classID = 1;
        doc.fidList = new int[]{0, 2};

        MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
        nb.onlineLearning(doc);

        assertEquals(1.0, nb.sampleSize, 0.01);
        assertEquals(1.0, nb.classCounts[1], 0.01);
        assertEquals(2.0, nb.fidCount, 0.01);
        assertEquals(2.0, nb.weights[1], 0.01);
        assertEquals(1.0, nb.wordCounts[0], 0.01);
        assertEquals(1.0, nb.wordCounts[2], 0.01);
        assertTrue(nb.fidCounts.get(1).containsKey(0));
        assertTrue(nb.fidCounts.get(1).containsKey(2));
    }
@Test
    public void testWeightedOnlineLearningAccumulation() {
        FeatureMap map = new FeatureMap();
        map.dim = 2;
        map.wordToFid.put("x", 0);
        map.wordToFid.put("y", 1);
        map.fidToWord.put(0, "x");
        map.fidToWord.put(1, "y");

        MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
        nb.weightedOnlineLearning(new int[]{0, 1}, 2.5, 0);

        assertEquals(2.5, nb.sampleSize, 0.001);
        assertEquals(2.5, nb.classCounts[0], 0.001);
        assertEquals(5.0, nb.fidCount, 0.001);
        assertEquals(2.5, nb.weights[0], 0.001);
        assertEquals(2.5, nb.wordCounts[0], 0.001);
        assertEquals(2.5, nb.wordCounts[1], 0.001);
        assertEquals(2.5, nb.fidCounts.get(0).get(0), 0.001);
        assertEquals(2.5, nb.fidCounts.get(0).get(1), 0.001);
    }
@Test
    public void testGetPriorReturnsZeroWhenSampleSizeZero() {
        FeatureMap map = new FeatureMap();
        map.dim = 2;
        MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
        assertEquals(0.0, nb.getPrior(1), 0.00001);
    }
@Test
    public void testGetPriorReturnsProperValue() {
        FeatureMap map = new FeatureMap();
        map.dim = 2;
        MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
        nb.sampleSize = 10.0;
        nb.classCounts[0] = 4.0;
        nb.classCounts[1] = 6.0;
        assertEquals(0.6, nb.getPrior(1), 0.00001);
        assertEquals(0.4, nb.getPrior(0), 0.00001);
    }
@Test
    public void testGetFidProbKnownFeature() {
        FeatureMap map = new FeatureMap();
        map.dim = 3;
        MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
        nb.weights[0] = 5.0;
        nb.fidCounts.get(0).put(2, 2.5);
        assertEquals((1 - MemoryEfficientNB.smooth) * (2.5 / 5.0), nb.getFidProb(2, 0), 0.00001);
    }
@Test
    public void testGetFidProbUnknownFeatureReturnsSmoothProbability() {
        FeatureMap map = new FeatureMap();
        map.dim = 10;
        MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
        double expected = MemoryEfficientNB.smooth / 10.0;
        assertEquals(expected, nb.getFidProb(4, 0), 0.000001);
    }
@Test
    public void testClassifyAboveThreshold() {
        FeatureMap map = new FeatureMap();
        map.dim = 2;
        map.wordToFid.put("x", 0);
        map.wordToFid.put("y", 1);
        map.fidToWord.put(0, "x");
        map.fidToWord.put(1, "y");

        Document doc = new Document();
        doc.classID = 0;
        doc.fidList = new int[]{0};

        MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
        nb.weightedOnlineLearning(new int[]{0}, 1.0, 0);
        int predicted = nb.classify(doc, 0.0);
        assertEquals(0, predicted);
    }
@Test
    public void testClassifyBelowThresholdReturnsNegativeOne() {
        FeatureMap map = new FeatureMap();
        map.dim = 2;
        map.wordToFid.put("xx", 0);
        map.fidToWord.put(0, "xx");

        Document doc = new Document();
        doc.classID = 0;
        doc.fidList = new int[]{0};

        MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
        nb.weightedOnlineLearning(new int[]{0}, 1.0, 0);
        int predicted = nb.classify(doc, 1.0); 
        assertEquals(-1, predicted);
    }
@Test
    public void testGetPredictionConfidenceReturnsNormalizedDistribution() {
        FeatureMap map = new FeatureMap();
        map.dim = 1;
        map.wordToFid.put("word", 0);
        map.fidToWord.put(0, "word");
        Document doc = new Document();
        doc.classID = 1;
        doc.fidList = new int[]{0};

        MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
        nb.weightedOnlineLearning(new int[]{0}, 10.0, 1);
        double[] confidence = nb.getPredictionConfidence(doc);
        assertEquals(2, confidence.length);
        double sum = confidence[0] + confidence[1];
        assertEquals(1.0, sum, 0.0001);
    }
@Test
    public void testGetExtendedFeaturesIncludesClassProbabilities() {
        FeatureMap map = new FeatureMap();
        map.dim = 1;
        map.wordToFid.put("x", 0);
        map.fidToWord.put(0, "x");

        Document doc = new Document();
        doc.classID = 0;
        doc.fidList = new int[]{0};

        MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
        nb.methodName = "nb";
        nb.weightedOnlineLearning(new int[]{0}, 1.0, 0);
        String result = nb.getExtendedFeatures(doc);
        assertTrue(result.contains("nb0("));
    }
@Test
    public void testToBeKeptReturnsTrueWhenCriteriaMet() {
        Vector<String> tokens = new Vector<>();
        tokens.add("word1");
        tokens.add("word2");
        tokens.add("word1");

        Hashtable<String, Integer> coolWords = new Hashtable<>();
        coolWords.put("word1", 1);

        boolean kept = MemoryEfficientNB.toBeKept(tokens, coolWords, 0.5, 2);
        assertTrue(kept);
    }
@Test
    public void testToBeKeptReturnsFalseWhenBelowMinLen() {
        Vector<String> tokens = new Vector<>();
        tokens.add("word1");

        Hashtable<String, Integer> coolWords = new Hashtable<>();
        coolWords.put("word1", 1);

        boolean kept = MemoryEfficientNB.toBeKept(tokens, coolWords, 0.5, 2);
        assertFalse(kept);
    }
@Test
    public void testGetTopPmiWordsReturnsWordsWithImportanceAboveThreshold() {
        FeatureMap map = new FeatureMap();
        map.dim = 1;
        map.fidToWord.put(0, "important");
        MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
        nb.wordCounts[0] = 5.0;
        nb.fidCount = 5.0;
        nb.weights[0] = 5.0;
        nb.fidCounts.get(0).put(0, 5.0);

        CharacteristicWords words = nb.getTopPmiWords(0, 1, 0.9, 1);
        assertEquals(1, words.topWords.size());
        assertEquals("important", words.topWords.get(0));
    }
@Test
    public void testGetAccReturnsOneForPerfectPrediction() {
        FeatureMap map = new FeatureMap();
        map.dim = 1;
        map.wordToFid.put("foo", 0);
        map.fidToWord.put(0, "foo");

        Document doc = new Document();
        doc.classID = 0;
        doc.fidList = new int[]{0};

        DocumentCollection coll = new DocumentCollection();
        coll.docs = new Vector<>();
        coll.docs.add(doc);

        MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
        nb.weightedOnlineLearning(new int[]{0}, 1.0, 0);

        double acc = nb.getAcc(coll);
        assertEquals(1.0, acc, 0.0001);
    } 
}