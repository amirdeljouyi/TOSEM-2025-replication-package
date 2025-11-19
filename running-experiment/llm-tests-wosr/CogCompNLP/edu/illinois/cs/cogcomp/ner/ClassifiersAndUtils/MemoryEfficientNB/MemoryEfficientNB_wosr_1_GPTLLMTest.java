public class MemoryEfficientNB_wosr_1_GPTLLMTest { 

 @Test
    public void testAllocateSpaceInitializesFieldsCorrectly() {
        FeatureMap map = new FeatureMap();
        map.dim = 10;
        MemoryEfficientNB nb = new MemoryEfficientNB(map, 3);

        assertEquals(map, nb.map);
        assertEquals(3, nb.classesN);
        assertNotNull(nb.classCounts);
        assertEquals(3, nb.classCounts.length);
        assertNotNull(nb.fidCounts);
        assertEquals(3, nb.fidCounts.size());
        assertNotNull(nb.weights);
        assertEquals(3, nb.weights.length);
        assertNotNull(nb.wordCounts);
        assertEquals(10, nb.wordCounts.length);
    }
@Test
    public void testWeightedOnlineLearningUpdatesCountsCorrectly() {
        FeatureMap map = new FeatureMap();
        map.dim = 5;
        MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
        int[] activeFeatures = new int[]{0, 2};
        nb.weightedOnlineLearning(activeFeatures, 1.5, 1);

        assertEquals(1.5, nb.sampleSize, 0.001);
        assertEquals(1.5, nb.classCounts[1], 0.001);
        assertEquals(3.0, nb.fidCount, 0.001);
        assertEquals(1.5, nb.weights[1], 0.001);
        assertEquals(1.5, nb.wordCounts[0], 0.001);
        assertEquals(1.5, nb.wordCounts[2], 0.001);
        assertEquals(1.5, nb.fidCounts.elementAt(1).get(0), 0.001);
        assertEquals(1.5, nb.fidCounts.elementAt(1).get(2), 0.001);
    }
@Test
    public void testGetFidProbReturnsCorrectSmoothedValue() {
        FeatureMap map = new FeatureMap();
        map.dim = 5;
        MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
        double prob = nb.getFidProb(3, 0);
        assertEquals(MemoryEfficientNB.smooth / 5, prob, 0.000001);
    }
@Test
    public void testGetFidProbReturnsCorrectProbabilityWhenExists() {
        FeatureMap map = new FeatureMap();
        map.dim = 5;
        MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
        nb.weights[0] = 4.0;
        nb.fidCounts.elementAt(0).put(2, 2.0);
        double prob = nb.getFidProb(2, 0);
        assertEquals((1 - MemoryEfficientNB.smooth) * (2.0 / 4.0), prob, 0.00001);
    }
@Test
    public void testGetPriorWithNoSampleSizeReturnsZero() {
        FeatureMap map = new FeatureMap();
        MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
        nb.sampleSize = 0.0;
        nb.classCounts[0] = 10.0;
        assertEquals(0.0, nb.getPrior(0), 0.00001);
    }
@Test
    public void testGetPriorReturnsCorrectValue() {
        FeatureMap map = new FeatureMap();
        MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
        nb.sampleSize = 10;
        nb.classCounts[0] = 2;
        assertEquals(0.2, nb.getPrior(0), 0.00001);
    }
@Test
    public void testToBeKeptReturnsTrueWhenConditionsMet() {
        Vector<String> tokens = new Vector<>();
        tokens.add("dog");
        tokens.add("cat");
        tokens.add("dog");

        Hashtable<String, Integer> coolWords = new Hashtable<>();
        coolWords.put("dog", 1);

        boolean result = MemoryEfficientNB.toBeKept(tokens, coolWords, 0.5, 2);
        assertTrue(result);
    }
@Test
    public void testToBeKeptReturnsFalseWhenTooFewTokens() {
        Vector<String> tokens = new Vector<>();
        tokens.add("bird");

        Hashtable<String, Integer> coolWords = new Hashtable<>();
        coolWords.put("bird", 1);

        boolean result = MemoryEfficientNB.toBeKept(tokens, coolWords, 0.5, 2);
        assertFalse(result);
    }
@Test
    public void testOnlineLearningCallsWeightedLearning() {
        FeatureMap map = new FeatureMap();
        map.dim = 10;
        MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);

        int[] feat = new int[]{5, 6};
        Document doc = new Document() {
            public int[] getActiveFid(FeatureMap m) {
                return feat;
            }
        };
        doc.classID = 1;
        nb.onlineLearning(doc);

        assertEquals(2.0, nb.fidCount, 0.00001);
        assertEquals(2.0, nb.weights[1], 0.00001);
        assertEquals(2.0, nb.wordCounts[5] + nb.wordCounts[6], 0.00001);
        assertEquals(1.0, nb.fidCounts.elementAt(1).get(5), 0.00001);
        assertEquals(1.0, nb.fidCounts.elementAt(1).get(6), 0.00001);
    }
@Test
    public void testGetPredictionConfidenceReturnsProperDistribution() {
        FeatureMap map = new FeatureMap();
        map.dim = 2;
        map.fidToWord.put(0, "a");
        map.fidToWord.put(1, "b");

        MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
        nb.weightedOnlineLearning(new int[]{0}, 5.0, 0);
        nb.weightedOnlineLearning(new int[]{1}, 1.0, 1);

        Document doc = new Document() {
            public int[] getActiveFid(FeatureMap m) {
                return new int[]{0};
            }
        };

        double[] conf = nb.getPredictionConfidence(doc);
        assertEquals(2, conf.length);
        assertTrue(conf[0] > conf[1]);
        assertEquals(1.0, conf[0] + conf[1], 0.00001);
    }
@Test
    public void testClassifyReturnsCorrectClassOrNegativeOneWithThres() {
        FeatureMap map = new FeatureMap();
        map.dim = 2;
        MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
        nb.weightedOnlineLearning(new int[]{0}, 5.0, 0);
        nb.weightedOnlineLearning(new int[]{1}, 1.0, 1);

        Document doc = new Document() {
            public int[] getActiveFid(FeatureMap m) {
                return new int[]{0};
            }
        };

        int predicted = nb.classify(doc, 0.1);
        assertEquals(0, predicted);
        int predictedTooStrict = nb.classify(doc, 1.1);
        assertEquals(-1, predictedTooStrict);
    }
@Test
    public void testGetAccComputesCorrectAccuracy() {
        FeatureMap map = new FeatureMap();
        map.dim = 2;

        MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
        nb.weightedOnlineLearning(new int[]{0}, 5.0, 0);
        nb.weightedOnlineLearning(new int[]{1}, 1.0, 1);

        Document doc1 = new Document() {
            public int[] getActiveFid(FeatureMap m) {
                return new int[]{0};
            }
        };
        doc1.classID = 0;

        Document doc2 = new Document() {
            public int[] getActiveFid(FeatureMap m) {
                return new int[]{1};
            }
        };
        doc2.classID = 1;

        DocumentCollection dc = new DocumentCollection();
        dc.docs = new Vector<>();
        dc.docs.add(doc1);
        dc.docs.add(doc2);

        double acc = nb.getAcc(dc);
        assertEquals(1.0, acc, 0.00001);
    }
@Test
    public void testGetExtendedFeaturesReturnsFeatureString() {
        FeatureMap map = new FeatureMap();
        map.dim = 2;
        MemoryEfficientNB nb = new MemoryEfficientNB(map, 2);
        nb.methodName = "nb-";

        nb.weightedOnlineLearning(new int[]{0}, 2.0, 0);
        nb.weightedOnlineLearning(new int[]{1}, 1.0, 1);

        Document doc = new Document() {
            public int[] getActiveFid(FeatureMap m) {
                return new int[]{0};
            }
        };

        String features = nb.getExtendedFeatures(doc);
        assertTrue(features.contains("nb-0("));
    }
@Test
    public void testGetTopPmiWordsReturnsCharacteristicWords() {
        FeatureMap map = new FeatureMap();
        map.dim = 2;
        map.fidToWord.put(0, "apple");
        map.fidToWord.put(1, "banana");

        MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
        nb.weightedOnlineLearning(new int[]{0, 1}, 3.0, 0);

        CharacteristicWords words = nb.getTopPmiWords(0, 2, 0.1, 1);
        assertNotNull(words);
        assertFalse(words.topWords.isEmpty());
        assertTrue(words.topWords.contains("apple") || words.topWords.contains("banana"));
    }
@Test
    public void testSaveAndConstructorWithFile() throws Exception {
        FeatureMap map = new FeatureMap();
        map.dim = 1;
        map.fidToWord.put(0, "dog");
        MemoryEfficientNB nb = new MemoryEfficientNB(map, 1);
        nb.weightedOnlineLearning(new int[]{0}, 1.0, 0);

        String tempPath = "temp_nb_model";
        nb.save(tempPath);

        MemoryEfficientNB loaded = new MemoryEfficientNB(tempPath);
        assertEquals(1.0, loaded.sampleSize, 0.0001);
        assertEquals(1, loaded.weights.length);
        assertEquals(1.0, loaded.weights[0], 0.0001);
        new File(tempPath).delete();
        new File(tempPath + ".nb.featuremap").delete();
    } 
}