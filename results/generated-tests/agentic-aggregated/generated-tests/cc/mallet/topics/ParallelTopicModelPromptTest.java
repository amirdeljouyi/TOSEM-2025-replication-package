package cc.mallet.topics;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.TreeSet;
import java.util.zip.GZIPOutputStream;

import org.junit.Test;

import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureSequence;
import cc.mallet.types.IDSorter;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.LabelAlphabet;
import cc.mallet.types.LabelSequence;

public class ParallelTopicModelPromptTest {

    @Test
    public void constructorCreatesRequestedTopicsAndLabels() {
        ParallelTopicModel model = new ParallelTopicModel(4);

        assertEquals(4, model.getNumTopics());
        assertEquals(4, model.getTopicAlphabet().size());
        assertEquals("topic0", model.getTopicAlphabet().lookupObject(0));
        assertEquals("topic3", model.getTopicAlphabet().lookupObject(3));
        assertEquals(0, model.getData().size());
        assertEquals(4, model.getTokensPerTopic().length);
    }

    @Test
    public void labelAlphabetConstructorRetainsAlphabetAndUsesCustomPrior() {
        LabelAlphabet topics = new LabelAlphabet();
        topics.lookupIndex("science");
        topics.lookupIndex("arts");
        ParallelTopicModel model = new ParallelTopicModel(topics, 6.0, 0.25);
        LabelSequence assignments = new LabelSequence(topics, new int[] {0, 0, 1});

        assertSame(topics, model.getTopicAlphabet());
        assertEquals(2, model.getNumTopics());
        assertArrayEquals(new double[] {5.0 / 9.0, 4.0 / 9.0},
                model.getTopicProbabilities(assignments), 0.0000000001);
    }

    @Test
    public void setNumTopicsResizesProbabilityDistributionBeforeTraining() {
        ParallelTopicModel model = new ParallelTopicModel(2, 8.0, 0.1);
        model.setNumTopics(5);
        LabelAlphabet labels = new LabelAlphabet();
        labels.lookupIndex("a");
        labels.lookupIndex("b");
        labels.lookupIndex("c");
        labels.lookupIndex("d");
        labels.lookupIndex("e");
        LabelSequence empty = new LabelSequence(labels, new int[0]);

        assertEquals(5, model.getNumTopics());
        assertEquals(5, model.getTokensPerTopic().length);
        assertArrayEquals(new double[] {0.2, 0.2, 0.2, 0.2, 0.2},
                model.getTopicProbabilities(empty), 0.0000000001);
    }

    @Test
    public void addInstancesIsReproducibleAndBuildsAllCounts() {
        Alphabet alphabet = new Alphabet();
        int apple = alphabet.lookupIndex("apple");
        int banana = alphabet.lookupIndex("banana");
        int carrot = alphabet.lookupIndex("carrot");
        InstanceList instances = new InstanceList(alphabet, null);
        instances.add(new Instance(new FeatureSequence(alphabet,
                new int[] {apple, banana, apple}), null, "doc-a", "source-a"));
        instances.add(new Instance(new FeatureSequence(alphabet,
                new int[] {banana, carrot}), null, "doc-b", "source-b"));
        ParallelTopicModel first = new ParallelTopicModel(3, 3.0, 0.1);
        first.setRandomSeed(712);
        first.addInstances(instances);
        ParallelTopicModel second = new ParallelTopicModel(3, 3.0, 0.1);
        second.setRandomSeed(712);
        second.addInstances(instances);

        assertSame(alphabet, first.getAlphabet());
        assertEquals(2, first.getData().size());
        assertEquals(5, Arrays.stream(first.getTokensPerTopic()).sum());
        assertTrue(Arrays.deepEquals(first.getTypeTopicCounts(), second.getTypeTopicCounts()));
        assertArrayEquals(first.getTokensPerTopic(), second.getTokensPerTopic());
        assertEquals(3, first.getTypeTopicCounts().length);
        assertEquals(2, first.getTypeTopicCounts()[apple].length);
        assertEquals(2, first.getTypeTopicCounts()[banana].length);
        assertEquals(1, first.getTypeTopicCounts()[carrot].length);
    }

    @Test
    public void initializeFromStateRestoresExactAssignmentsAndHyperparameters() throws Exception {
        Alphabet alphabet = new Alphabet();
        int apple = alphabet.lookupIndex("apple");
        int banana = alphabet.lookupIndex("banana");
        int carrot = alphabet.lookupIndex("carrot");
        InstanceList instances = new InstanceList(alphabet, null);
        instances.add(new Instance(new FeatureSequence(alphabet,
                new int[] {apple, banana, apple}), null, "doc-a", "source-a"));
        instances.add(new Instance(new FeatureSequence(alphabet,
                new int[] {banana, carrot}), null, "doc-b", "source-b"));
        ParallelTopicModel model = new ParallelTopicModel(2, 2.0, 0.01);
        model.setRandomSeed(4);
        model.addInstances(instances);
        File state = File.createTempFile("parallel-topic-state", ".gz");
        state.deleteOnExit();
        PrintWriter writer = new PrintWriter(new GZIPOutputStream(Files.newOutputStream(state.toPath())));
        writer.println("#doc source pos typeindex type topic");
        writer.println("#alpha : 1.0 2.0");
        writer.println("#beta : 0.5");
        writer.println("0 source-a 0 0 apple 0");
        writer.println("0 source-a 1 1 banana 1");
        writer.println("0 source-a 2 0 apple 0");
        writer.println("1 source-b 0 1 banana 1");
        writer.println("1 source-b 1 2 carrot 1");
        writer.close();

        model.initializeFromState(state);

        assertArrayEquals(new int[] {2, 3}, model.getTokensPerTopic());
        assertArrayEquals(new double[] {0.5, 0.5}, model.getTopicProbabilities(0), 0.0000000001);
        assertArrayEquals(new double[] {0.2, 0.8}, model.getTopicProbabilities(1), 0.0000000001);
        assertArrayEquals(new Object[] {"apple"}, model.getTopWords(3)[0]);
        assertArrayEquals(new Object[] {"banana", "carrot"}, model.getTopWords(3)[1]);
        assertTrue(Double.isFinite(model.modelLogLikelihood()));
    }

    @Test
    public void topicWordMatricesSupportRawSmoothedAndNormalizedViews() throws Exception {
        Alphabet alphabet = new Alphabet();
        int apple = alphabet.lookupIndex("apple");
        int banana = alphabet.lookupIndex("banana");
        int carrot = alphabet.lookupIndex("carrot");
        InstanceList instances = new InstanceList(alphabet, null);
        instances.add(new Instance(new FeatureSequence(alphabet,
                new int[] {apple, banana, apple}), null, "doc-a", "source-a"));
        instances.add(new Instance(new FeatureSequence(alphabet,
                new int[] {banana, carrot}), null, "doc-b", "source-b"));
        ParallelTopicModel model = new ParallelTopicModel(2, 2.0, 0.5);
        model.setRandomSeed(8);
        model.addInstances(instances);
        File state = File.createTempFile("parallel-topic-words", ".gz");
        state.deleteOnExit();
        PrintWriter writer = new PrintWriter(new GZIPOutputStream(Files.newOutputStream(state.toPath())));
        writer.println("#alpha : 1.0 2.0");
        writer.println("#beta : 0.5");
        writer.println("0 s 0 0 apple 0");
        writer.println("0 s 1 1 banana 1");
        writer.println("0 s 2 0 apple 0");
        writer.println("1 s 0 1 banana 1");
        writer.println("1 s 1 2 carrot 1");
        writer.close();
        model.initializeFromState(state);

        double[][] raw = model.getTopicWords(false, false);
        double[][] smooth = model.getTopicWords(false, true);
        double[][] normalized = model.getTopicWords(true, true);

        assertArrayEquals(new double[] {2.0, 0.0, 0.0}, raw[0], 0.0);
        assertArrayEquals(new double[] {0.0, 2.0, 1.0}, raw[1], 0.0);
        assertArrayEquals(new double[] {2.5, 0.5, 0.5}, smooth[0], 0.0);
        assertArrayEquals(new double[] {0.5, 2.5, 1.5}, smooth[1], 0.0);
        assertArrayEquals(new double[] {2.5 / 3.5, 0.5 / 3.5, 0.5 / 3.5},
                normalized[0], 0.0000000001);
        assertArrayEquals(new double[] {0.5 / 4.5, 2.5 / 4.5, 1.5 / 4.5},
                normalized[1], 0.0000000001);
    }

    @Test
    public void subCorpusAndDocumentMatricesRespectMasksSmoothingAndNormalization() throws Exception {
        Alphabet alphabet = new Alphabet();
        int apple = alphabet.lookupIndex("apple");
        int banana = alphabet.lookupIndex("banana");
        int carrot = alphabet.lookupIndex("carrot");
        InstanceList instances = new InstanceList(alphabet, null);
        instances.add(new Instance(new FeatureSequence(alphabet,
                new int[] {apple, banana, apple}), null, "doc-a", "source-a"));
        instances.add(new Instance(new FeatureSequence(alphabet,
                new int[] {banana, carrot}), null, "doc-b", "source-b"));
        ParallelTopicModel model = new ParallelTopicModel(2, 3.0, 0.5);
        model.setRandomSeed(9);
        model.addInstances(instances);
        File state = File.createTempFile("parallel-topic-subcorpus", ".gz");
        state.deleteOnExit();
        PrintWriter writer = new PrintWriter(new GZIPOutputStream(Files.newOutputStream(state.toPath())));
        writer.println("#alpha : 1.0 2.0");
        writer.println("#beta : 0.5");
        writer.println("0 s 0 0 apple 0");
        writer.println("0 s 1 1 banana 1");
        writer.println("0 s 2 0 apple 0");
        writer.println("1 s 0 1 banana 1");
        writer.println("1 s 1 2 carrot 1");
        writer.close();
        model.initializeFromState(state);

        double[][] subset = model.getSubCorpusTopicWords(new boolean[] {true, false}, false, false);
        double[][] subsetNormalized = model.getSubCorpusTopicWords(
                new boolean[] {true, false}, true, true);
        double[][] documents = model.getDocumentTopics(false, false);
        double[][] documentProbabilities = model.getDocumentTopics(true, true);

        assertArrayEquals(new double[] {2.0, 0.0, 0.0}, subset[0], 0.0);
        assertArrayEquals(new double[] {0.0, 1.0, 0.0}, subset[1], 0.0);
        assertArrayEquals(new double[] {2.5 / 3.5, 0.5 / 3.5, 0.5 / 3.5},
                subsetNormalized[0], 0.0000000001);
        assertArrayEquals(new double[] {0.5 / 2.5, 1.5 / 2.5, 0.5 / 2.5},
                subsetNormalized[1], 0.0000000001);
        assertArrayEquals(new double[] {2.0, 1.0}, documents[0], 0.0);
        assertArrayEquals(new double[] {0.0, 2.0}, documents[1], 0.0);
        assertArrayEquals(new double[] {0.5, 0.5}, documentProbabilities[0], 0.0000000001);
        assertArrayEquals(new double[] {0.2, 0.8}, documentProbabilities[1], 0.0000000001);
    }

    @Test
    public void sortedWordsAndTopicDocumentsOrderHighestWeightsFirst() throws Exception {
        Alphabet alphabet = new Alphabet();
        int apple = alphabet.lookupIndex("apple");
        int banana = alphabet.lookupIndex("banana");
        int carrot = alphabet.lookupIndex("carrot");
        InstanceList instances = new InstanceList(alphabet, null);
        instances.add(new Instance(new FeatureSequence(alphabet,
                new int[] {apple, banana, apple}), null, "doc-a", "source-a"));
        instances.add(new Instance(new FeatureSequence(alphabet,
                new int[] {banana, carrot}), null, "doc-b", "source-b"));
        ParallelTopicModel model = new ParallelTopicModel(2, 3.0, 0.5);
        model.setRandomSeed(10);
        model.addInstances(instances);
        File state = File.createTempFile("parallel-topic-sorting", ".gz");
        state.deleteOnExit();
        PrintWriter writer = new PrintWriter(new GZIPOutputStream(Files.newOutputStream(state.toPath())));
        writer.println("#alpha : 1.0 2.0");
        writer.println("#beta : 0.5");
        writer.println("0 s 0 0 apple 0");
        writer.println("0 s 1 1 banana 1");
        writer.println("0 s 2 0 apple 0");
        writer.println("1 s 0 1 banana 1");
        writer.println("1 s 1 2 carrot 1");
        writer.close();
        model.initializeFromState(state);

        TreeSet<IDSorter> topicZeroWords = model.getSortedWords().get(0);
        TreeSet<IDSorter> topicOneDocuments = model.getTopicDocuments(1.0).get(1);

        assertEquals(1, topicZeroWords.size());
        assertEquals(apple, topicZeroWords.first().getID());
        assertEquals(2.0, topicZeroWords.first().getWeight(), 0.0);
        assertEquals(1, topicOneDocuments.first().getID());
        assertEquals(3.0 / 4.0, topicOneDocuments.first().getWeight(), 0.0000000001);
        assertEquals(2, model.getTopicDocuments(1.0).get(0).size());
    }

    @Test
    public void inMemoryReportsContainTopicsWordsDocumentsAndState() throws Exception {
        Alphabet alphabet = new Alphabet();
        int apple = alphabet.lookupIndex("apple");
        int banana = alphabet.lookupIndex("banana");
        InstanceList instances = new InstanceList(alphabet, null);
        instances.add(new Instance(new FeatureSequence(alphabet,
                new int[] {apple, banana, apple}), null, "named-doc", "named-source"));
        ParallelTopicModel model = new ParallelTopicModel(2, 2.0, 0.25);
        model.setRandomSeed(11);
        model.addInstances(instances);
        ByteArrayOutputStream topBytes = new ByteArrayOutputStream();
        model.printTopWords(new PrintStream(topBytes, true, StandardCharsets.UTF_8.name()), 2, true);
        ByteArrayOutputStream xmlBytes = new ByteArrayOutputStream();
        PrintWriter xmlWriter = new PrintWriter(xmlBytes, true);
        model.topicXMLReport(xmlWriter, 2);
        ByteArrayOutputStream denseBytes = new ByteArrayOutputStream();
        PrintWriter denseWriter = new PrintWriter(denseBytes, true);
        model.printDenseDocumentTopics(denseWriter);
        ByteArrayOutputStream sparseBytes = new ByteArrayOutputStream();
        PrintWriter sparseWriter = new PrintWriter(sparseBytes, true);
        model.printDocumentTopics(sparseWriter, 0.0, 1);
        ByteArrayOutputStream stateBytes = new ByteArrayOutputStream();
        model.printState(new PrintStream(stateBytes, true, StandardCharsets.UTF_8.name()));

        String top = topBytes.toString(StandardCharsets.UTF_8.name());
        String xml = xmlBytes.toString(StandardCharsets.UTF_8.name());
        String dense = denseBytes.toString(StandardCharsets.UTF_8.name());
        String sparse = sparseBytes.toString(StandardCharsets.UTF_8.name());
        String state = stateBytes.toString(StandardCharsets.UTF_8.name());
        assertTrue(top.contains("0\t"));
        assertTrue(top.contains("apple") || top.contains("banana"));
        assertTrue(xml.startsWith("<?xml version='1.0' ?>"));
        assertTrue(xml.contains("<topic id='0'"));
        assertTrue(xml.contains("</topicModel>"));
        assertTrue(dense.startsWith("0\tnamed-doc\t"));
        assertTrue(sparse.startsWith("#doc name topic proportion"));
        assertTrue(sparse.contains("named-doc"));
        assertTrue(state.contains("#alpha : "));
        assertTrue(state.contains("#beta : 0.25"));
        assertTrue(state.contains("named-source"));
    }

    @Test
    public void fileReportsWriteExpectedRepresentations() throws Exception {
        Alphabet alphabet = new Alphabet();
        int apple = alphabet.lookupIndex("apple");
        int banana = alphabet.lookupIndex("banana");
        InstanceList instances = new InstanceList(alphabet, null);
        instances.add(new Instance(new FeatureSequence(alphabet,
                new int[] {apple, banana, apple}), null, "doc", "source"));
        ParallelTopicModel model = new ParallelTopicModel(2, 2.0, 0.5);
        model.setRandomSeed(13);
        model.addInstances(instances);
        File top = File.createTempFile("parallel-top", ".txt");
        File counts = File.createTempFile("parallel-counts", ".txt");
        File weights = File.createTempFile("parallel-weights", ".txt");
        File documents = File.createTempFile("parallel-documents", ".txt");
        top.deleteOnExit();
        counts.deleteOnExit();
        weights.deleteOnExit();
        documents.deleteOnExit();

        model.printTopWords(top, 2, false);
        model.printTypeTopicCounts(counts);
        model.printTopicWordWeights(weights);
        model.printDocumentTopics(documents);

        String topText = Files.readString(top.toPath());
        String countText = Files.readString(counts.toPath());
        String weightText = Files.readString(weights.toPath());
        String documentText = Files.readString(documents.toPath());
        assertTrue(topText.contains("apple") || topText.contains("banana"));
        assertTrue(countText.contains("0 apple"));
        assertTrue(countText.contains("1 banana"));
        assertEquals(4, weightText.lines().count());
        assertTrue(weightText.contains("\tapple\t"));
        assertTrue(documentText.startsWith("#doc name topic proportion"));
        assertTrue(documentText.contains("doc"));
    }

    @Test
    public void printedStateCanInitializeAnotherModelExactly() throws Exception {
        Alphabet alphabet = new Alphabet();
        int apple = alphabet.lookupIndex("apple");
        int banana = alphabet.lookupIndex("banana");
        int carrot = alphabet.lookupIndex("carrot");
        InstanceList instances = new InstanceList(alphabet, null);
        instances.add(new Instance(new FeatureSequence(alphabet,
                new int[] {apple, banana, apple}), null, "doc-a", "source-a"));
        instances.add(new Instance(new FeatureSequence(alphabet,
                new int[] {banana, carrot}), null, "doc-b", "source-b"));
        ParallelTopicModel original = new ParallelTopicModel(3, 4.5, 0.2);
        original.setRandomSeed(41);
        original.addInstances(instances);
        File state = File.createTempFile("parallel-roundtrip", ".gz");
        state.deleteOnExit();
        original.printState(state);
        ParallelTopicModel restored = new ParallelTopicModel(3, 1.0, 0.01);
        restored.setRandomSeed(99);
        restored.addInstances(instances);

        restored.initializeFromState(state);

        assertArrayEquals(original.getTokensPerTopic(), restored.getTokensPerTopic());
        assertTrue(Arrays.deepEquals(original.getTypeTopicCounts(), restored.getTypeTopicCounts()));
        assertArrayEquals(original.getTopicProbabilities(0), restored.getTopicProbabilities(0), 0.0);
        assertArrayEquals(original.getTopicProbabilities(1), restored.getTopicProbabilities(1), 0.0);
        assertEquals(original.displayTopWords(5, false), restored.displayTopWords(5, false));
    }

    @Test(expected = IllegalStateException.class)
    public void initializeFromStateRejectsMismatchedTokenTypes() throws Exception {
        Alphabet alphabet = new Alphabet();
        int apple = alphabet.lookupIndex("apple");
        alphabet.lookupIndex("banana");
        InstanceList instances = new InstanceList(alphabet, null);
        instances.add(new Instance(new FeatureSequence(alphabet,
                new int[] {apple}), null, "doc", "source"));
        ParallelTopicModel model = new ParallelTopicModel(2);
        model.setRandomSeed(1);
        model.addInstances(instances);
        File state = File.createTempFile("parallel-invalid", ".gz");
        state.deleteOnExit();
        PrintWriter writer = new PrintWriter(new GZIPOutputStream(Files.newOutputStream(state.toPath())));
        writer.println("#alpha : 1.0 1.0");
        writer.println("#beta : 0.01");
        writer.println("0 source 0 1 banana 0");
        writer.close();

        model.initializeFromState(state);
    }

    @Test
    public void serializationRoundTripPreservesLearnedPublicResults() throws Exception {
        Alphabet alphabet = new Alphabet();
        int apple = alphabet.lookupIndex("apple");
        int banana = alphabet.lookupIndex("banana");
        InstanceList instances = new InstanceList(alphabet, null);
        instances.add(new Instance(new FeatureSequence(alphabet,
                new int[] {apple, banana, apple}), null, "doc-a", "source-a"));
        instances.add(new Instance(new FeatureSequence(alphabet,
                new int[] {banana, banana}), null, "doc-b", "source-b"));
        ParallelTopicModel model = new ParallelTopicModel(2, 2.0, 0.1);
        model.setRandomSeed(21);
        model.addInstances(instances);
        File serialized = File.createTempFile("parallel-model", ".ser");
        serialized.deleteOnExit();

        model.write(serialized);
        ParallelTopicModel restored = ParallelTopicModel.read(serialized);

        assertNotSame(model, restored);
        assertEquals(model.getNumTopics(), restored.getNumTopics());
        assertArrayEquals(model.getTokensPerTopic(), restored.getTokensPerTopic());
        assertTrue(Arrays.deepEquals(model.getTypeTopicCounts(), restored.getTypeTopicCounts()));
        assertArrayEquals(model.getTopicProbabilities(0), restored.getTopicProbabilities(0), 0.0);
        assertEquals(model.displayTopWords(4, false), restored.displayTopWords(4, false));
        assertTrue(Double.isFinite(restored.modelLogLikelihood()));
    }

    @Test
    public void inferencerAndProbabilityEstimatorOperateOnModelOutputs() throws Exception {
        Alphabet alphabet = new Alphabet();
        int apple = alphabet.lookupIndex("apple");
        int banana = alphabet.lookupIndex("banana");
        int carrot = alphabet.lookupIndex("carrot");
        InstanceList training = new InstanceList(alphabet, null);
        training.add(new Instance(new FeatureSequence(alphabet,
                new int[] {apple, banana, apple, carrot}), null, "train-a", "source-a"));
        training.add(new Instance(new FeatureSequence(alphabet,
                new int[] {banana, carrot, carrot}), null, "train-b", "source-b"));
        ParallelTopicModel model = new ParallelTopicModel(2, 2.0, 0.1);
        model.setRandomSeed(32);
        model.addInstances(training);
        TopicInferencer inferencer = model.getInferencer();
        inferencer.setRandomSeed(32);
        Instance heldOut = new Instance(new FeatureSequence(alphabet,
                new int[] {apple, carrot}), null, "held-out", "source-held-out");
        double[] inferred = inferencer.getSampledDistribution(heldOut, 20, 1, 5);
        InstanceList evaluation = new InstanceList(alphabet, null);
        evaluation.add(heldOut);
        MarginalProbEstimator estimator = model.getProbEstimator();
        estimator.setRandomSeed(32);
        ByteArrayOutputStream probabilities = new ByteArrayOutputStream();
        double logProbability = estimator.evaluateLeftToRight(
                evaluation, 5, false, new PrintStream(probabilities));

        assertNotNull(inferencer);
        assertNotNull(estimator);
        assertEquals(2, inferred.length);
        assertEquals(1.0, Arrays.stream(inferred).sum(), 0.0000000001);
        assertTrue(inferred[0] >= 0.0);
        assertTrue(inferred[1] >= 0.0);
        assertTrue(Double.isFinite(logProbability));
        assertArrayEquals(model.getTokensPerTopic(), estimator.getTokensPerTopic());
        assertSame(model.getTypeTopicCounts(), estimator.getTypeTopicCounts());
    }

    @Test
    public void singleThreadEstimateAndMaximizeKeepAConsistentModel() throws Exception {
        Alphabet alphabet = new Alphabet();
        int apple = alphabet.lookupIndex("apple");
        int banana = alphabet.lookupIndex("banana");
        int carrot = alphabet.lookupIndex("carrot");
        InstanceList instances = new InstanceList(alphabet, null);
        instances.add(new Instance(new FeatureSequence(alphabet,
                new int[] {apple, banana, apple, carrot}), null, "doc-a", "source-a"));
        instances.add(new Instance(new FeatureSequence(alphabet,
                new int[] {banana, carrot, banana}), null, "doc-b", "source-b"));
        ParallelTopicModel model = new ParallelTopicModel(3, 3.0, 0.1);
        model.setRandomSeed(51);
        model.addInstances(instances);
        model.setNumThreads(1);
        model.setNumIterations(3);
        model.setBurninPeriod(10);
        model.setOptimizeInterval(0);
        model.setTemperingInterval(0);
        model.setTopicDisplay(0, 3);

        model.estimate();
        model.maximize(2);

        assertEquals(7, Arrays.stream(model.getTokensPerTopic()).sum());
        assertEquals(1.0, Arrays.stream(model.getTopicProbabilities(0)).sum(), 0.0000000001);
        assertEquals(1.0, Arrays.stream(model.getTopicProbabilities(1)).sum(), 0.0000000001);
        assertTrue(Double.isFinite(model.modelLogLikelihood()));
        assertEquals(3, model.getSortedWords().size());
    }

    @Test
    public void multiThreadEstimateMergesWorkerCounts() throws Exception {
        Alphabet alphabet = new Alphabet();
        int apple = alphabet.lookupIndex("apple");
        int banana = alphabet.lookupIndex("banana");
        int carrot = alphabet.lookupIndex("carrot");
        InstanceList instances = new InstanceList(alphabet, null);
        instances.add(new Instance(new FeatureSequence(alphabet,
                new int[] {apple, banana, apple}), null, "doc-a", "source-a"));
        instances.add(new Instance(new FeatureSequence(alphabet,
                new int[] {banana, carrot, banana}), null, "doc-b", "source-b"));
        instances.add(new Instance(new FeatureSequence(alphabet,
                new int[] {carrot, apple}), null, "doc-c", "source-c"));
        instances.add(new Instance(new FeatureSequence(alphabet,
                new int[] {apple, carrot}), null, "doc-d", "source-d"));
        ParallelTopicModel model = new ParallelTopicModel(2, 2.0, 0.1);
        model.setRandomSeed(61);
        model.addInstances(instances);
        model.setNumThreads(2);
        model.setNumIterations(2);
        model.setBurninPeriod(10);
        model.setOptimizeInterval(0);
        model.setTopicDisplay(0, 2);

        model.estimate();

        double[][] documents = model.getDocumentTopics(false, false);
        double[][] words = model.getTopicWords(false, false);
        assertEquals(10, Arrays.stream(model.getTokensPerTopic()).sum());
        assertEquals(3.0, Arrays.stream(documents[0]).sum(), 0.0);
        assertEquals(3.0, Arrays.stream(documents[1]).sum(), 0.0);
        assertEquals(2.0, Arrays.stream(documents[2]).sum(), 0.0);
        assertEquals(2.0, Arrays.stream(documents[3]).sum(), 0.0);
        assertEquals(10.0, Arrays.stream(words[0]).sum() + Arrays.stream(words[1]).sum(), 0.0);
        assertTrue(Double.isFinite(model.modelLogLikelihood()));
    }

    @Test
    public void scheduledStateAndModelSavingCreatesReadableArtifacts() throws Exception {
        Alphabet alphabet = new Alphabet();
        int apple = alphabet.lookupIndex("apple");
        int banana = alphabet.lookupIndex("banana");
        InstanceList instances = new InstanceList(alphabet, null);
        instances.add(new Instance(new FeatureSequence(alphabet,
                new int[] {apple, banana}), null, "doc", "source"));
        ParallelTopicModel model = new ParallelTopicModel(2, 2.0, 0.1);
        model.setRandomSeed(71);
        model.addInstances(instances);
        model.setNumIterations(1);
        model.setNumThreads(1);
        model.setOptimizeInterval(0);
        model.setTopicDisplay(0, 2);
        File directory = Files.createTempDirectory("parallel-scheduled").toFile();
        directory.deleteOnExit();
        String statePrefix = new File(directory, "state").getAbsolutePath();
        String modelPrefix = new File(directory, "model").getAbsolutePath();
        model.setSaveState(1, statePrefix);
        model.setSaveSerializedModel(1, modelPrefix);

        model.estimate();

        File state = new File(statePrefix + ".1");
        File serialized = new File(modelPrefix + ".1");
        state.deleteOnExit();
        serialized.deleteOnExit();
        assertTrue(state.isFile());
        assertTrue(serialized.isFile());
        assertTrue(Files.size(state.toPath()) > 0);
        ParallelTopicModel restored = ParallelTopicModel.read(serialized);
        assertEquals(2, Arrays.stream(restored.getTokensPerTopic()).sum());
        assertEquals(1.0, Arrays.stream(restored.getTopicProbabilities(0)).sum(), 0.0000000001);
    }

    @Test
    public void phraseAndTopicDocumentReportsAreWellFormed() {
        Alphabet alphabet = new Alphabet();
        int red = alphabet.lookupIndex("red");
        int apple = alphabet.lookupIndex("apple");
        int green = alphabet.lookupIndex("green");
        InstanceList instances = new InstanceList(alphabet, null);
        instances.add(new Instance(new FeatureSequence(alphabet,
                new int[] {red, apple, green, apple}), null, "fruit-doc", "source"));
        ParallelTopicModel model = new ParallelTopicModel(2, 2.0, 0.1);
        model.setRandomSeed(81);
        model.addInstances(instances);
        ByteArrayOutputStream phraseBytes = new ByteArrayOutputStream();
        PrintWriter phraseWriter = new PrintWriter(phraseBytes, true);
        model.topicPhraseXMLReport(phraseWriter, 3);
        ByteArrayOutputStream documentBytes = new ByteArrayOutputStream();
        PrintWriter documentWriter = new PrintWriter(documentBytes, true);
        model.printTopicDocuments(documentWriter, 1);

        String phraseXml = new String(phraseBytes.toByteArray(), StandardCharsets.UTF_8);
        String documentReport = new String(documentBytes.toByteArray(), StandardCharsets.UTF_8);
        assertTrue(phraseXml.startsWith("<?xml version='1.0' ?>"));
        assertTrue(phraseXml.contains("<topics>"));
        assertTrue(phraseXml.contains("<topic id=\"0\""));
        assertTrue(phraseXml.endsWith("</topics>\n"));
        assertTrue(documentReport.startsWith("#topic doc name proportion"));
        assertTrue(documentReport.contains("fruit-doc"));
        assertEquals(3, documentReport.lines().count());
    }

    @Test
    public void printWriterTopicWeightsIncludesSmoothingForAbsentPairs() throws Exception {
        Alphabet alphabet = new Alphabet();
        int apple = alphabet.lookupIndex("apple");
        int banana = alphabet.lookupIndex("banana");
        InstanceList instances = new InstanceList(alphabet, null);
        instances.add(new Instance(new FeatureSequence(alphabet,
                new int[] {apple, apple, banana}), null, "doc", "source"));
        ParallelTopicModel model = new ParallelTopicModel(2, 2.0, 0.5);
        model.setRandomSeed(91);
        model.addInstances(instances);
        File state = File.createTempFile("parallel-weight-state", ".gz");
        state.deleteOnExit();
        PrintWriter stateWriter = new PrintWriter(new GZIPOutputStream(Files.newOutputStream(state.toPath())));
        stateWriter.println("#alpha : 1.0 1.0");
        stateWriter.println("#beta : 0.5");
        stateWriter.println("0 source 0 0 apple 0");
        stateWriter.println("0 source 1 0 apple 0");
        stateWriter.println("0 source 2 1 banana 1");
        stateWriter.close();
        model.initializeFromState(state);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(bytes, true);

        model.printTopicWordWeights(writer);

        String output = bytes.toString(StandardCharsets.UTF_8.name());
        assertTrue(output.contains("0\tapple\t2.5"));
        assertTrue(output.contains("0\tbanana\t0.5"));
        assertTrue(output.contains("1\tapple\t0.5"));
        assertTrue(output.contains("1\tbanana\t1.5"));
        assertEquals(4, output.lines().count());
        assertFalse(output.contains("NaN"));
    }
}
