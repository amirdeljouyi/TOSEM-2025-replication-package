package edu.illinois.cs.cogcomp.ner.ClassifiersAndUtils;

import edu.illinois.cs.cogcomp.ner.StringStatisticsUtils.CharacteristicWords;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Hashtable;
import java.util.Vector;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MemoryEfficientNBPromptTest {

    @Test
    public void newClassifierHasZeroPriorsAndSmoothedUnknownFeatureProbability() {
        FeatureMap map = new FeatureMap();
        map.addDimension("alpha");
        map.addDimension("beta");
        MemoryEfficientNB classifier = new MemoryEfficientNB(map, 2);

        assertEquals(0.0, classifier.getPrior(0), 0.0);
        assertEquals(0.0, classifier.getPrior(1), 0.0);
        assertEquals(0.00005, classifier.getFidProb(0, 0), 0.0);
        assertEquals(0.00005, classifier.getFidProb(1, 1), 0.0);
    }

    @Test
    public void weightedLearningAccumulatesFractionalClassPriors() {
        FeatureMap map = new FeatureMap();
        map.addDimension("alpha");
        MemoryEfficientNB classifier = new MemoryEfficientNB(map, 2);

        classifier.weightedOnlineLearning(new int[] {0}, 0.5, 0);
        classifier.weightedOnlineLearning(new int[] {0}, 1.5, 1);

        assertEquals(0.25, classifier.getPrior(0), 1.0e-12);
        assertEquals(0.75, classifier.getPrior(1), 1.0e-12);
        assertEquals(0.9999, classifier.getFidProb(0, 0), 1.0e-12);
        assertEquals(0.9999, classifier.getFidProb(0, 1), 1.0e-12);
    }

    @Test
    public void repeatedWeightedUpdatesAccumulateFeatureCounts() {
        FeatureMap map = new FeatureMap();
        map.addDimension("alpha");
        map.addDimension("beta");
        MemoryEfficientNB classifier = new MemoryEfficientNB(map, 1);

        classifier.weightedOnlineLearning(new int[] {0}, 2.0, 0);
        classifier.weightedOnlineLearning(new int[] {0, 1}, 1.0, 0);

        assertEquals(0.9999 * 3.0 / 4.0, classifier.getFidProb(0, 0), 1.0e-12);
        assertEquals(0.9999 / 4.0, classifier.getFidProb(1, 0), 1.0e-12);
        assertEquals(1.0, classifier.getPrior(0), 0.0);
    }

    @Test
    public void onlineLearningIgnoresUnknownAndDuplicateDocumentWords() {
        FeatureMap map = new FeatureMap();
        map.addDimension("known");
        map.addDimension("other");
        MemoryEfficientNB classifier = new MemoryEfficientNB(map, 1);
        Document document = new Document(new String[] {"known", "known", "missing"}, 0);

        classifier.onlineLearning(document);

        assertEquals(0.9999, classifier.getFidProb(0, 0), 1.0e-12);
        assertEquals(0.00005, classifier.getFidProb(1, 0), 0.0);
        assertEquals(1.0, classifier.getPrior(0), 0.0);
    }

    @Test
    public void collectionConstructorTrainsAcrossMultipleClasses() {
        FeatureMap map = new FeatureMap();
        map.addDimension("red");
        map.addDimension("blue");
        DocumentCollection documents = new DocumentCollection();
        documents.addDoc(new Document(new String[] {"red"}, 0));
        documents.addDoc(new Document(new String[] {"blue"}, 1));
        documents.addDoc(new Document(new String[] {"blue", "unknown"}, 1));

        MemoryEfficientNB classifier = new MemoryEfficientNB(documents, map, 2);

        assertEquals(1.0 / 3.0, classifier.getPrior(0), 1.0e-12);
        assertEquals(2.0 / 3.0, classifier.getPrior(1), 1.0e-12);
        assertEquals(0.9999, classifier.getFidProb(0, 0), 1.0e-12);
        assertEquals(0.9999, classifier.getFidProb(1, 1), 1.0e-12);
    }

    @Test
    public void confidenceIsNormalizedAndStronglyFavorsObservedClass() {
        FeatureMap map = new FeatureMap();
        map.addDimension("red");
        map.addDimension("blue");
        MemoryEfficientNB classifier = new MemoryEfficientNB(map, 2);
        classifier.weightedOnlineLearning(new int[] {0}, 1.0, 0);
        classifier.weightedOnlineLearning(new int[] {1}, 1.0, 1);

        double[] confidence = classifier.getPredictionConfidence(
                new Document(new String[] {"red"}, -1));

        assertEquals(2, confidence.length);
        assertEquals(1.0, confidence[0] + confidence[1], 1.0e-12);
        assertTrue(confidence[0] > 0.999);
        assertTrue(confidence[0] > confidence[1]);
    }

    @Test
    public void unknownDocumentUsesOnlyLearnedPriors() {
        FeatureMap map = new FeatureMap();
        map.addDimension("known");
        MemoryEfficientNB classifier = new MemoryEfficientNB(map, 2);
        classifier.weightedOnlineLearning(new int[] {0}, 3.0, 0);
        classifier.weightedOnlineLearning(new int[] {0}, 1.0, 1);

        double[] confidence = classifier.getPredictionConfidence(
                new Document(new String[] {"not-in-map"}, -1));

        assertArrayEquals(new double[] {0.75, 0.25}, confidence, 1.0e-12);
    }

    @Test
    public void classifyAlwaysModeBreaksEqualScoresTowardFirstClass() {
        FeatureMap map = new FeatureMap();
        map.addDimension("known");
        MemoryEfficientNB classifier = new MemoryEfficientNB(map, 2);
        classifier.weightedOnlineLearning(new int[0], 1.0, 0);
        classifier.weightedOnlineLearning(new int[0], 1.0, 1);

        int prediction = classifier.classify(new Document(new String[] {"unknown"}, -1), -1.0);

        assertEquals(0, prediction);
    }

    @Test
    public void classifyHonorsAcceptingAndRejectingThresholds() {
        FeatureMap map = new FeatureMap();
        map.addDimension("red");
        map.addDimension("blue");
        MemoryEfficientNB classifier = new MemoryEfficientNB(map, 2);
        classifier.onlineLearning(new Document(new String[] {"red"}, 0));
        classifier.onlineLearning(new Document(new String[] {"blue"}, 1));
        Document query = new Document(new String[] {"red"}, -1);

        assertEquals(0, classifier.classify(query, 0.9));
        assertEquals(-1, classifier.classify(query, 1.0));
        assertEquals(0, classifier.classify(query, 0.0));
    }

    @Test
    public void accuracyReflectsCorrectAndIncorrectPredictions() {
        FeatureMap map = new FeatureMap();
        map.addDimension("red");
        map.addDimension("blue");
        MemoryEfficientNB classifier = new MemoryEfficientNB(map, 2);
        classifier.onlineLearning(new Document(new String[] {"red"}, 0));
        classifier.onlineLearning(new Document(new String[] {"blue"}, 1));
        DocumentCollection testDocuments = new DocumentCollection();
        testDocuments.addDoc(new Document(new String[] {"red"}, 0));
        testDocuments.addDoc(new Document(new String[] {"blue"}, 1));
        testDocuments.addDoc(new Document(new String[] {"red"}, 1));

        assertEquals(2.0 / 3.0, classifier.getAcc(testDocuments), 1.0e-12);
    }

    @Test
    public void classSpecificPmiWordsAreRankedAndCapacityLimited() {
        FeatureMap map = new FeatureMap();
        map.addDimension("red");
        map.addDimension("shared");
        map.addDimension("blue");
        MemoryEfficientNB classifier = new MemoryEfficientNB(map, 2);
        classifier.onlineLearning(new Document(new String[] {"red", "shared"}, 0));
        classifier.onlineLearning(new Document(new String[] {"red"}, 0));
        classifier.onlineLearning(new Document(new String[] {"blue", "shared"}, 1));

        CharacteristicWords words = classifier.getTopPmiWords(0, 1, 1.0, 0);

        assertEquals("red\t-\t1.6666666666666665\n", words.toString());
    }

    @Test
    public void pmiMinimumAppearanceThresholdIsStrict() {
        FeatureMap map = new FeatureMap();
        map.addDimension("once");
        MemoryEfficientNB classifier = new MemoryEfficientNB(map, 1);
        classifier.onlineLearning(new Document(new String[] {"once"}, 0));

        CharacteristicWords words = classifier.getTopPmiWords(0, 5, 0.0, 1);

        assertEquals("once\t-\t0.0\n", words.toString());
        assertEquals("", classifier.getTopPmiWords(0, 5, 0.1, 1).toString());
    }

    @Test
    public void aggregatePmiWordsDeduplicatesWordsAcrossClasses() {
        FeatureMap map = new FeatureMap();
        map.addDimension("shared");
        map.addDimension("red");
        map.addDimension("blue");
        MemoryEfficientNB classifier = new MemoryEfficientNB(map, 2);
        classifier.onlineLearning(new Document(new String[] {"shared", "red"}, 0));
        classifier.onlineLearning(new Document(new String[] {"shared", "blue"}, 1));

        Hashtable<String, Integer> words = classifier.getTopPmiWords(3, 0.5, 0);

        assertEquals(3, words.size());
        assertTrue(words.containsKey("shared"));
        assertTrue(words.containsKey("red"));
        assertTrue(words.containsKey("blue"));
    }

    @Test
    public void tokenFilterCountsUniqueTokensAndRequiresMinimumLength() {
        Vector<String> tokens = new Vector<>();
        tokens.add("red");
        tokens.add("red");
        tokens.add("blue");
        Hashtable<String, Integer> selected = new Hashtable<>();
        selected.put("red", 1);

        assertTrue(MemoryEfficientNB.toBeKept(tokens, selected, 0.5, 3));
        assertFalse(MemoryEfficientNB.toBeKept(tokens, selected, 0.51, 3));
        assertFalse(MemoryEfficientNB.toBeKept(tokens, selected, 0.5, 4));
    }

    @Test
    public void tokenFilterRejectsEmptyInputEvenWhenMinimumLengthIsZero() {
        Vector<String> tokens = new Vector<>();
        Hashtable<String, Integer> selected = new Hashtable<>();

        assertFalse(MemoryEfficientNB.toBeKept(tokens, selected, 0.0, 0));
    }

    @Test
    public void allocateSpaceResetsPreviouslyLearnedState() {
        FeatureMap firstMap = new FeatureMap();
        firstMap.addDimension("old");
        MemoryEfficientNB classifier = new MemoryEfficientNB(firstMap, 1);
        classifier.onlineLearning(new Document(new String[] {"old"}, 0));
        FeatureMap replacementMap = new FeatureMap();
        replacementMap.addDimension("new-a");
        replacementMap.addDimension("new-b");

        classifier.allocateSpace(replacementMap, 2);

        assertEquals(0.0, classifier.getPrior(0), 0.0);
        assertEquals(0.0, classifier.getPrior(1), 0.0);
        assertEquals(0.00005, classifier.getFidProb(1, 1), 0.0);
    }

    @Test
    public void saveAndLoadRoundTripPreservesPredictionsAndModelStatistics() throws Exception {
        FeatureMap map = new FeatureMap();
        map.addDimension("red");
        map.addDimension("blue");
        MemoryEfficientNB original = new MemoryEfficientNB(map, 2);
        original.weightedOnlineLearning(new int[] {0}, 2.5, 0);
        original.weightedOnlineLearning(new int[] {1}, 1.5, 1);
        Path directory = Files.createTempDirectory("memory-efficient-nb-");
        Path modelPath = directory.resolve("roundtrip-model");
        modelPath.toFile().deleteOnExit();
        directory.resolve("roundtrip-model.nb.featuremap").toFile().deleteOnExit();
        directory.toFile().deleteOnExit();

        original.save(modelPath.toString());
        MemoryEfficientNB restored = new MemoryEfficientNB(modelPath.toString());
        Document query = new Document(new String[] {"red"}, -1);

        assertEquals(original.getPrior(0), restored.getPrior(0), 0.0);
        assertEquals(original.getPrior(1), restored.getPrior(1), 0.0);
        assertEquals(original.getFidProb(0, 0), restored.getFidProb(0, 0), 0.0);
        assertArrayEquals(original.getPredictionConfidence(query),
                restored.getPredictionConfidence(query), 0.0);
        assertEquals(original.classify(query, -1), restored.classify(query, -1));
    }

    @Test
    public void extendedFeaturesFromLoadedModelUseFilenameAsPrefix() throws Exception {
        FeatureMap map = new FeatureMap();
        map.addDimension("word");
        MemoryEfficientNB original = new MemoryEfficientNB(map, 1);
        original.onlineLearning(new Document(new String[] {"word"}, 0));
        Path directory = Files.createTempDirectory("memory-efficient-nb-features-");
        Path modelPath = directory.resolve("named-model");
        modelPath.toFile().deleteOnExit();
        directory.resolve("named-model.nb.featuremap").toFile().deleteOnExit();
        directory.toFile().deleteOnExit();
        original.save(modelPath.toString());
        MemoryEfficientNB restored = new MemoryEfficientNB(modelPath.toString());

        String features = restored.getExtendedFeatures(new Document(new String[] {"word"}, -1));

        assertEquals("named-model0(1.0) ", features);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void weightedLearningRejectsOutOfRangeClassId() {
        FeatureMap map = new FeatureMap();
        map.addDimension("word");
        MemoryEfficientNB classifier = new MemoryEfficientNB(map, 1);

        classifier.weightedOnlineLearning(new int[] {0}, 1.0, 1);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void weightedLearningRejectsOutOfRangeFeatureId() {
        FeatureMap map = new FeatureMap();
        map.addDimension("word");
        MemoryEfficientNB classifier = new MemoryEfficientNB(map, 1);

        classifier.weightedOnlineLearning(new int[] {1}, 1.0, 0);
    }

    @Test
    public void emptyFeatureMapReturnsInfiniteSmoothingProbability() {
        MemoryEfficientNB classifier = new MemoryEfficientNB(new FeatureMap(), 1);

        assertTrue(Double.isInfinite(classifier.getFidProb(0, 0)));
        assertTrue(classifier.getFidProb(0, 0) > 0.0);
    }
}
