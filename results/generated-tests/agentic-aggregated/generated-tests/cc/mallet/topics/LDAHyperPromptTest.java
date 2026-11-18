package cc.mallet.topics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.zip.GZIPInputStream;

import org.junit.Test;

import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureSequence;
import cc.mallet.types.IDSorter;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.LabelAlphabet;
import cc.mallet.types.LabelSequence;
import cc.mallet.util.Randoms;

public class LDAHyperPromptTest {

    @Test
    public void constructorCreatesRequestedTopicAlphabet() {
        LDAHyper model = new LDAHyper(3);

        assertEquals(3, model.getNumTopics());
        assertEquals(3, model.getTopicAlphabet().size());
        assertEquals("topic0", model.getTopicAlphabet().lookupObject(0));
        assertEquals("topic1", model.getTopicAlphabet().lookupObject(1));
        assertEquals("topic2", model.getTopicAlphabet().lookupObject(2));
        assertTrue(model.getData().isEmpty());
        assertNull(model.getAlphabet());
    }

    @Test
    public void constructorRetainsCallerSuppliedTopicAlphabet() {
        LabelAlphabet topics = new LabelAlphabet();
        topics.lookupIndex("news");
        topics.lookupIndex("science");

        LDAHyper model = new LDAHyper(topics, 4.0, 0.25, new Randoms(9));

        assertSame(topics, model.getTopicAlphabet());
        assertEquals(2, model.getNumTopics());
        assertEquals("news", model.getTopicAlphabet().lookupObject(0));
        assertEquals("science", model.getTopicAlphabet().lookupObject(1));
    }

    @Test
    public void explicitAssignmentsPopulateAllPublicCounts() {
        Alphabet words = new Alphabet(new Object[] {"apple", "banana", "carrot"});
        InstanceList training = new InstanceList(words, null);
        training.add(new Instance(new FeatureSequence(words, new int[] {0, 1, 0}), null, "d0", "source-a"));
        training.add(new Instance(new FeatureSequence(words, new int[] {2, 1}), null, "d1", "source-b"));
        LDAHyper model = new LDAHyper(2, 2.0, 0.1, new Randoms(3));
        LabelSequence first = new LabelSequence(model.getTopicAlphabet(), new int[] {0, 1, 0});
        LabelSequence second = new LabelSequence(model.getTopicAlphabet(), new int[] {1, 1});

        model.addInstances(training, Arrays.asList(first, second));

        assertSame(words, model.getAlphabet());
        assertEquals(2, model.getData().size());
        assertEquals(2, model.getCountFeatureTopic(0, 0));
        assertEquals(0, model.getCountFeatureTopic(0, 1));
        assertEquals(0, model.getCountFeatureTopic(1, 0));
        assertEquals(2, model.getCountFeatureTopic(1, 1));
        assertEquals(1, model.getCountFeatureTopic(2, 1));
        assertEquals(2, model.getCountTokensPerTopic(0));
        assertEquals(3, model.getCountTokensPerTopic(1));
    }

    @Test
    public void repeatedAddInstancesAccumulatesStatistics() {
        Alphabet words = new Alphabet(new Object[] {"red", "blue"});
        LDAHyper model = new LDAHyper(2, 1.0, 0.1, new Randoms(4));
        InstanceList firstBatch = new InstanceList(words, null);
        firstBatch.add(new Instance(new FeatureSequence(words, new int[] {0, 1}), null, "one", null));
        InstanceList secondBatch = new InstanceList(words, null);
        secondBatch.add(new Instance(new FeatureSequence(words, new int[] {1, 1}), null, "two", null));

        model.addInstances(firstBatch, Collections.singletonList(
                new LabelSequence(model.getTopicAlphabet(), new int[] {0, 1})));
        model.addInstances(secondBatch, Collections.singletonList(
                new LabelSequence(model.getTopicAlphabet(), new int[] {1, 0})));

        assertEquals(2, model.getData().size());
        assertEquals(1, model.getCountFeatureTopic(0, 0));
        assertEquals(1, model.getCountFeatureTopic(1, 0));
        assertEquals(2, model.getCountFeatureTopic(1, 1));
        assertEquals(2, model.getCountTokensPerTopic(0));
        assertEquals(2, model.getCountTokensPerTopic(1));
    }

    @Test
    public void automaticAssignmentsAccountForEveryToken() {
        Alphabet words = new Alphabet(new Object[] {"x", "y"});
        InstanceList training = new InstanceList(words, null);
        training.add(new Instance(new FeatureSequence(words, new int[] {0, 0, 1, 0}), null, null, null));
        LDAHyper model = new LDAHyper(2);

        model.addInstances(training);

        assertEquals(1, model.getData().size());
        assertEquals(3, model.getCountFeatureTopic(0, 0) + model.getCountFeatureTopic(0, 1));
        assertEquals(1, model.getCountFeatureTopic(1, 0) + model.getCountFeatureTopic(1, 1));
        assertEquals(4, model.getCountTokensPerTopic(0) + model.getCountTokensPerTopic(1));
    }

    @Test
    public void addingInstancesFromAnotherAlphabetIsRejected() {
        Alphabet firstWords = new Alphabet(new Object[] {"same"});
        Alphabet secondWords = new Alphabet(new Object[] {"same"});
        InstanceList first = new InstanceList(firstWords, null);
        first.add(new Instance(new FeatureSequence(firstWords, new int[] {0}), null, null, null));
        InstanceList second = new InstanceList(secondWords, null);
        second.add(new Instance(new FeatureSequence(secondWords, new int[] {0}), null, null, null));
        LDAHyper model = new LDAHyper(1);
        model.addInstances(first, Collections.singletonList(
                new LabelSequence(model.getTopicAlphabet(), new int[] {0})));

        try {
            model.addInstances(second, Collections.singletonList(
                    new LabelSequence(model.getTopicAlphabet(), new int[] {0})));
            fail("Expected a different Alphabet instance to be rejected");
        } catch (IllegalArgumentException expected) {
            assertEquals("Cannot change Alphabet.", expected.getMessage());
        }
    }

    @Test
    public void invalidTopicAssignmentIsRejectedDuringCounting() {
        Alphabet words = new Alphabet(new Object[] {"token"});
        InstanceList training = new InstanceList(words, null);
        training.add(new Instance(new FeatureSequence(words, new int[] {0}), null, null, null));
        LDAHyper model = new LDAHyper(2);

        try {
            model.addInstances(training, Collections.singletonList(
                    new LabelSequence(model.getTopicAlphabet(), new int[] {2})));
            fail("Expected an out-of-range topic index to fail");
        } catch (ArrayIndexOutOfBoundsException expected) {
            assertNotNull(expected);
        }
    }

    @Test
    public void sortedTopicWordsOrdersByDescendingCount() {
        Alphabet words = new Alphabet(new Object[] {"low", "high", "zero"});
        InstanceList training = new InstanceList(words, null);
        training.add(new Instance(new FeatureSequence(words, new int[] {0, 1, 1, 1}), null, null, null));
        LDAHyper model = new LDAHyper(2);
        model.addInstances(training, Collections.singletonList(
                new LabelSequence(model.getTopicAlphabet(), new int[] {0, 0, 0, 1})));

        IDSorter[] sorted = model.getSortedTopicWords(0);

        assertEquals(1, sorted[0].getID());
        assertEquals(2.0, sorted[0].getWeight(), 0.0);
        assertEquals(0, sorted[1].getID());
        assertEquals(1.0, sorted[1].getWeight(), 0.0);
        assertEquals(2, sorted[2].getID());
        assertEquals(0.0, sorted[2].getWeight(), 0.0);
    }

    @Test
    public void printTopWordsSupportsCompactAndMultilineFormats() throws Exception {
        Alphabet words = new Alphabet(new Object[] {"alpha", "beta"});
        InstanceList training = new InstanceList(words, null);
        training.add(new Instance(new FeatureSequence(words, new int[] {0, 0, 1}), null, null, null));
        LDAHyper model = new LDAHyper(1, 1.0, 0.1, new Randoms(1));
        model.addInstances(training, Collections.singletonList(
                new LabelSequence(model.getTopicAlphabet(), new int[] {0, 0, 0})));
        ByteArrayOutputStream compactBytes = new ByteArrayOutputStream();
        ByteArrayOutputStream multilineBytes = new ByteArrayOutputStream();

        model.printTopWords(new PrintStream(compactBytes, true, "UTF-8"), 3, false);
        model.printTopWords(new PrintStream(multilineBytes, true, "UTF-8"), 3, true);

        String compact = compactBytes.toString("UTF-8");
        String multiline = multilineBytes.toString("UTF-8");
        assertTrue(compact.startsWith("0\t1\t3\t"));
        assertTrue(compact.contains("alpha beta"));
        assertTrue(multiline.startsWith("Topic 0" + System.lineSeparator()));
        assertTrue(multiline.contains("alpha\t2"));
        assertTrue(multiline.contains("beta\t1"));
    }

    @Test
    public void printTopWordsToFileProducesReadableOutput() throws Exception {
        Alphabet words = new Alphabet(new Object[] {"only"});
        InstanceList training = new InstanceList(words, null);
        training.add(new Instance(new FeatureSequence(words, new int[] {0}), null, null, null));
        LDAHyper model = new LDAHyper(1);
        model.addInstances(training, Collections.singletonList(
                new LabelSequence(model.getTopicAlphabet(), new int[] {0})));
        Path directory = Files.createTempDirectory("lda-top-words");
        File output = directory.resolve("topics.txt").toFile();

        model.printTopWords(output, 2, true);

        String text = new String(Files.readAllBytes(output.toPath()), StandardCharsets.UTF_8);
        assertTrue(text.contains("Topic 0"));
        assertTrue(text.contains("only\t1"));
    }

    @Test
    public void topicXmlReportContainsTopicMetadataAndRankedTerms() {
        Alphabet words = new Alphabet(new Object[] {"first", "second"});
        InstanceList training = new InstanceList(words, null);
        training.add(new Instance(new FeatureSequence(words, new int[] {0, 0, 1}), null, null, null));
        LDAHyper model = new LDAHyper(1, 1.0, 0.1, new Randoms(2));
        model.addInstances(training, Collections.singletonList(
                new LabelSequence(model.getTopicAlphabet(), new int[] {0, 0, 0})));
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(bytes);

        model.topicXMLReport(writer, 3);
        writer.flush();

        String xml = new String(bytes.toByteArray(), StandardCharsets.UTF_8);
        assertTrue(xml.startsWith("<?xml version='1.0' ?>"));
        assertTrue(xml.contains("<topicModel>"));
        assertTrue(xml.contains("<topic id='0' alpha='1.0' totalTokens='3'>"));
        assertTrue(xml.contains("<word rank='1'>first</word>"));
        assertTrue(xml.contains("<word rank='2'>second</word>"));
        assertTrue(xml.trim().endsWith("</topicModel>"));
    }

    @Test
    public void phraseXmlReportCombinesModelCountsAndDocumentSequences() throws Exception {
        Alphabet words = new Alphabet(new Object[] {"new", "york", "city"});
        InstanceList training = new InstanceList(words, null);
        training.add(new Instance(new FeatureSequence(words, new int[] {0, 1, 2, 0}), null, null, null));
        LDAHyper model = new LDAHyper(1, 1.0, 0.1, new Randoms(2));
        model.addInstances(training, Collections.singletonList(
                new LabelSequence(model.getTopicAlphabet(), new int[] {0, 0, 0, 0})));
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        model.topicXMLReportPhrases(new PrintStream(bytes, true, "UTF-8"), 2);

        String xml = bytes.toString("UTF-8");
        assertTrue(xml.startsWith("<?xml version='1.0' ?>"));
        assertTrue(xml.contains("<topics>"));
        assertTrue(xml.contains("<topic id=\"0\""));
        assertTrue(xml.contains("totalTokens=\"4\""));
        assertTrue(xml.contains("titles=\""));
        assertTrue(xml.trim().endsWith("</topics>"));
    }

    @Test
    public void documentTopicReportHonorsThresholdMaximumAndSources() {
        Alphabet words = new Alphabet(new Object[] {"a", "b"});
        InstanceList training = new InstanceList(words, null);
        training.add(new Instance(new FeatureSequence(words, new int[] {0, 0, 1, 1}), null, "named", "doc-source"));
        training.add(new Instance(new FeatureSequence(words, new int[] {1, 1}), null, "unnamed", null));
        LDAHyper model = new LDAHyper(2);
        model.addInstances(training, Arrays.asList(
                new LabelSequence(model.getTopicAlphabet(), new int[] {0, 0, 0, 1}),
                new LabelSequence(model.getTopicAlphabet(), new int[] {1, 1})));
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(bytes);

        model.printDocumentTopics(writer, 0.6, 1);
        writer.flush();

        String report = new String(bytes.toByteArray(), StandardCharsets.UTF_8);
        assertTrue(report.startsWith("#doc source topic proportion"));
        assertTrue(report.contains("0 doc-source 0 0.75"));
        assertTrue(report.contains("1 null-source 1 1.0"));
        assertFalse(report.contains("0.25"));
    }

    @Test
    public void printStateIncludesEveryTokenAndItsAssignedTopic() throws Exception {
        Alphabet words = new Alphabet(new Object[] {"cat", "dog"});
        InstanceList training = new InstanceList(words, null);
        training.add(new Instance(new FeatureSequence(words, new int[] {0, 1}), null, null, "origin"));
        LDAHyper model = new LDAHyper(2);
        model.addInstances(training, Collections.singletonList(
                new LabelSequence(model.getTopicAlphabet(), new int[] {1, 0})));
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        model.printState(new PrintStream(bytes, true, "UTF-8"));

        String state = bytes.toString("UTF-8");
        assertTrue(state.startsWith("#doc source pos typeindex type topic"));
        assertTrue(state.contains("0 origin 0 0 cat 1"));
        assertTrue(state.contains("0 origin 1 1 dog 0"));
    }

    @Test
    public void printStateFileIsGzipCompressedAndReadable() throws Exception {
        Alphabet words = new Alphabet(new Object[] {"z"});
        InstanceList training = new InstanceList(words, null);
        training.add(new Instance(new FeatureSequence(words, new int[] {0}), null, null, null));
        LDAHyper model = new LDAHyper(1);
        model.addInstances(training, Collections.singletonList(
                new LabelSequence(model.getTopicAlphabet(), new int[] {0})));
        Path directory = Files.createTempDirectory("lda-state");
        File output = directory.resolve("state.gz").toFile();

        model.printState(output);

        byte[] compressed = Files.readAllBytes(output.toPath());
        GZIPInputStream gzip = new GZIPInputStream(new ByteArrayInputStream(compressed));
        ByteArrayOutputStream uncompressed = new ByteArrayOutputStream();
        byte[] buffer = new byte[512];
        int count = gzip.read(buffer);
        uncompressed.write(buffer, 0, count);
        gzip.close();
        String state = uncompressed.toString("UTF-8");
        assertTrue(state.contains("#doc source pos typeindex type topic"));
        assertTrue(state.contains("0 NA 0 0 z 0"));
    }

    @Test
    public void writeSuppressesIoFailureForDirectoryTarget() throws Exception {
        Alphabet words = new Alphabet(new Object[] {"left", "right"});
        InstanceList training = new InstanceList(words, null);
        training.add(new Instance(new FeatureSequence(words, new int[] {0, 1, 1}), null, "d", "s"));
        LDAHyper original = new LDAHyper(2, 3.0, 0.2, new Randoms(7));
        original.addInstances(training, Collections.singletonList(
                new LabelSequence(original.getTopicAlphabet(), new int[] {0, 1, 1})));
        Path directory = Files.createTempDirectory("lda-model");
        File invalidOutput = directory.toFile();

        original.write(invalidOutput);

        assertTrue(invalidOutput.isDirectory());
        assertEquals(1, original.getData().size());
        assertEquals(2, original.getCountFeatureTopic(1, 1));
    }

    @Test
    public void readReturnsNullForMissingOrMalformedModel() throws Exception {
        Path directory = Files.createTempDirectory("lda-bad-model");
        File missing = directory.resolve("missing.bin").toFile();
        Path malformed = directory.resolve("malformed.bin");
        Files.write(malformed, new byte[] {1, 2, 3, 4});

        assertNull(LDAHyper.read(missing));
        assertNull(LDAHyper.read(malformed.toFile()));
    }

    @Test
    public void estimationLifecycleSupportsAnEmptyCorpus() throws Exception {
        Alphabet words = new Alphabet(new Object[] {"a", "b"});
        InstanceList training = new InstanceList(words, null);
        LDAHyper model = new LDAHyper(1, 1.0, 0.1, new Randoms(11));
        model.addInstances(training, Collections.emptyList());
        model.setBurninPeriod(100);
        model.setOptimizeInterval(0);
        model.setTopicDisplay(0, 2);
        model.setRandomSeed(11);
        model.setNumIterations(1);

        model.estimate();

        assertEquals(0, model.getData().size());
        assertEquals(0, model.getCountFeatureTopic(0, 0));
        assertEquals(0, model.getCountFeatureTopic(1, 0));
        assertEquals(0, model.getCountTokensPerTopic(0));
        assertTrue(Double.isFinite(model.modelLogLikelihood()));
    }

    @Test
    public void estimateUsesConfiguredStateCheckpointAtIterationZero() throws Exception {
        Alphabet words = new Alphabet(new Object[] {"checkpoint"});
        InstanceList training = new InstanceList(words, null);
        training.add(new Instance(new FeatureSequence(words, new int[] {0}), null, null, "src"));
        LDAHyper model = new LDAHyper(1, 1.0, 0.1, new Randoms(5));
        model.addInstances(training, Collections.singletonList(
                new LabelSequence(model.getTopicAlphabet(), new int[] {0})));
        Path directory = Files.createTempDirectory("lda-checkpoint");
        String prefix = directory.resolve("state").toString();
        model.setSaveState(1, prefix);
        model.setTopicDisplay(0, 1);
        model.setOptimizeInterval(0);

        model.estimate(0);

        File checkpoint = new File(prefix + ".0");
        assertTrue(checkpoint.isFile());
        assertTrue(checkpoint.length() > 0L);
    }

    @Test
    public void modelLogLikelihoodIsFiniteForMultipleDocumentsAndTopics() {
        Alphabet words = new Alphabet(new Object[] {"a", "b", "c"});
        InstanceList training = new InstanceList(words, null);
        training.add(new Instance(new FeatureSequence(words, new int[] {0, 1}), null, null, null));
        training.add(new Instance(new FeatureSequence(words, new int[] {1, 2, 2}), null, null, null));
        LDAHyper model = new LDAHyper(2, 2.0, 0.5, new Randoms(8));
        model.addInstances(training, Arrays.asList(
                new LabelSequence(model.getTopicAlphabet(), new int[] {0, 1}),
                new LabelSequence(model.getTopicAlphabet(), new int[] {1, 0, 0})));

        double likelihood = model.modelLogLikelihood();

        assertTrue(Double.isFinite(likelihood));
        assertTrue(likelihood < 0.0);
    }

    @Test
    public void empiricalLikelihoodMatchesSingleTopicPredictiveProbabilities() {
        Alphabet words = new Alphabet(new Object[] {"apple", "banana"});
        InstanceList training = new InstanceList(words, null);
        training.add(new Instance(new FeatureSequence(words, new int[] {0, 0, 1}), null, null, null));
        LDAHyper model = new LDAHyper(1, 1.0, 0.5, new Randoms(6));
        model.addInstances(training, Collections.singletonList(
                new LabelSequence(model.getTopicAlphabet(), new int[] {0, 0, 0})));
        InstanceList testing = new InstanceList(words, null);
        testing.add(new Instance(new FeatureSequence(words, new int[] {0, 1}), null, null, null));

        double likelihood = model.empiricalLikelihood(5, testing);

        assertEquals(Math.log(2.5 / 4.0) + Math.log(1.5 / 4.0), likelihood, 1.0e-10);
    }

    @Test
    public void empiricalLikelihoodIgnoresTestingTypesUnknownDuringTraining() {
        Alphabet words = new Alphabet(new Object[] {"known"});
        InstanceList training = new InstanceList(words, null);
        training.add(new Instance(new FeatureSequence(words, new int[] {0, 0}), null, null, null));
        LDAHyper model = new LDAHyper(1, 1.0, 0.5, new Randoms(6));
        model.addInstances(training, Collections.singletonList(
                new LabelSequence(model.getTopicAlphabet(), new int[] {0, 0})));
        int unseen = words.lookupIndex("unseen");
        InstanceList testing = new InstanceList(words, null);
        testing.add(new Instance(new FeatureSequence(words, new int[] {unseen}), null, null, null));

        assertEquals(0.0, model.empiricalLikelihood(3, testing), 0.0);
    }

    @Test
    public void topicLabelMutualInformationIsOneForPerfectAssociation() {
        Alphabet words = new Alphabet(new Object[] {"w"});
        LabelAlphabet labels = new LabelAlphabet();
        InstanceList training = new InstanceList(words, labels);
        training.add(new Instance(new FeatureSequence(words, new int[] {0, 0}), labels.lookupLabel("negative"), "n", null));
        training.add(new Instance(new FeatureSequence(words, new int[] {0, 0}), labels.lookupLabel("positive"), "p", null));
        LDAHyper model = new LDAHyper(2);
        model.addInstances(training, Arrays.asList(
                new LabelSequence(model.getTopicAlphabet(), new int[] {0, 0}),
                new LabelSequence(model.getTopicAlphabet(), new int[] {1, 1})));

        assertEquals(1.0, model.topicLabelMutualInformation(), 1.0e-12);
    }

    @Test
    public void topicLabelMutualInformationIsZeroWithoutTargets() {
        Alphabet words = new Alphabet(new Object[] {"w"});
        InstanceList training = new InstanceList(words, null);
        training.add(new Instance(new FeatureSequence(words, new int[] {0}), null, null, null));
        LDAHyper model = new LDAHyper(1);
        model.addInstances(training, Collections.singletonList(
                new LabelSequence(model.getTopicAlphabet(), new int[] {0})));

        assertEquals(0.0, model.topicLabelMutualInformation(), 0.0);
    }

    @Test
    public void shorterTopicSequenceCountsOnlyItsRepresentedPositions() {
        Alphabet words = new Alphabet(new Object[] {"first", "second"});
        InstanceList training = new InstanceList(words, null);
        training.add(new Instance(new FeatureSequence(words, new int[] {0, 1}), null, null, null));
        LDAHyper model = new LDAHyper(1);

        model.addInstances(training, Collections.singletonList(
                new LabelSequence(model.getTopicAlphabet(), new int[] {0})));

        assertEquals(1, model.getCountFeatureTopic(0, 0));
        assertEquals(0, model.getCountFeatureTopic(1, 0));
        assertEquals(1, model.getCountTokensPerTopic(0));
    }
}
