package cc.mallet.topics;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.zip.GZIPOutputStream;

import org.junit.Test;

public class MultinomialHMMPromptTest {

    @Test
    public void constructorAndInitializeAggregateTopicCountsInOneState() throws Exception {
        Path topics = Files.createTempFile("hmm-topics", ".txt");
        Files.write(topics, Arrays.asList(
                "#doc token type position topic",
                "0 0 10 x 0",
                "0 1 11 x 1",
                "0 2 12 x 1",
                "1 0 13 x 2",
                "1 1 14 x 3"), StandardCharsets.UTF_8);

        MultinomialHMM hmm = new MultinomialHMM(4, topics.toString(), 1);
        hmm.setGamma(1.0);
        hmm.setRandomSeed(123);
        hmm.initialize();

        assertEquals("1\t2\t1\t1\t\n", hmm.stateTopics());
        assertEquals("1\t\n", hmm.stateTransitionMatrix());
    }

    @Test
    public void commentsAndBlankDocumentIdsProduceNoCountsForMissingDocuments() throws Exception {
        Path topics = Files.createTempFile("hmm-sparse", ".txt");
        Files.write(topics, Arrays.asList(
                "# ignored",
                "2 0 99 x 0",
                "2 1 98 x 3"), StandardCharsets.UTF_8);

        MultinomialHMM hmm = new MultinomialHMM(4, topics.toString(), 1);
        hmm.setGamma(0.5);
        hmm.setRandomSeed(7);
        hmm.initialize();

        assertEquals("1\t0\t0\t1\t\n", hmm.stateTopics());
        assertEquals("1\t\n", hmm.stateTransitionMatrix());
    }

    @Test
    public void gzipTopicStateFileIsAccepted() throws Exception {
        Path topics = Files.createTempFile("hmm-topics", ".gz");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(bytes);
        gzip.write(("# header\n0 0 1 x 0\n0 1 2 x 2\n").getBytes(StandardCharsets.UTF_8));
        gzip.close();
        Files.write(topics, bytes.toByteArray());

        MultinomialHMM hmm = new MultinomialHMM(4, topics.toString(), 1);
        hmm.setGamma(1.0);
        hmm.initialize();

        assertEquals("1\t0\t1\t0\t\n", hmm.stateTopics());
        assertEquals("0\t\n", hmm.stateTransitionMatrix());
    }

    @Test
    public void alphaKeysAreShownInDescendingTopicFrequencyOrder() throws Exception {
        Path topics = Files.createTempFile("hmm-topics", ".txt");
        Files.write(topics, Arrays.asList(
                "0 0 1 x 0", "0 1 1 x 0", "0 2 1 x 0", "0 3 1 x 0",
                "0 4 1 x 1", "0 5 1 x 1", "0 6 1 x 1",
                "0 7 1 x 2", "0 8 1 x 2", "0 9 1 x 3"), StandardCharsets.UTF_8);
        Path alpha = Files.createTempFile("hmm-alpha", ".txt");
        Files.write(alpha, Arrays.asList(
                "0 0.01 zero key", "1 25.0 one key", "2 3.0 two key", "3 7.0 three key"),
                StandardCharsets.UTF_8);

        MultinomialHMM hmm = new MultinomialHMM(4, topics.toString(), 1);
        hmm.loadAlphaFromFile(alpha.toString());
        hmm.setGamma(1.0);
        hmm.setRandomSeed(1);
        hmm.initialize();

        String report = hmm.printStateTransitions();
        assertTrue(report.contains("4\tzero key \n3\tone key \n2\ttwo key \n1\tthree key \n"));
        assertTrue(report.contains("[1/0] [0]\t[0]"));
    }

    @Test
    public void sequenceBoundariesControlInitialAndTransitionCounts() throws Exception {
        Path topics = Files.createTempFile("hmm-topics", ".txt");
        Files.write(topics, Arrays.asList(
                "0 0 1 x 0", "1 0 1 x 1", "2 0 1 x 2", "3 0 1 x 3", "4 0 1 x 0"),
                StandardCharsets.UTF_8);
        Path sequences = Files.createTempFile("hmm-sequences", ".txt");
        Files.write(sequences, Arrays.asList("10\ta", "10\tb", "20\tc", "30\td", "30\te"),
                StandardCharsets.UTF_8);
        Path alpha = Files.createTempFile("hmm-alpha", ".txt");
        Files.write(alpha, Arrays.asList("0 1 z", "1 1 o", "2 1 t", "3 1 h"), StandardCharsets.UTF_8);

        MultinomialHMM hmm = new MultinomialHMM(4, topics.toString(), 1);
        hmm.loadAlphaFromFile(alpha.toString());
        hmm.loadSequenceIDsFromFile(sequences.toString());
        hmm.setGamma(1.0);
        hmm.initialize();

        assertEquals("2\t\n", hmm.stateTransitionMatrix());
        assertTrue(hmm.printStateTransitions().contains("[3/0] [2]\t[2]"));
    }

    @Test
    public void oneStateSamplingPreservesAllSufficientStatistics() throws Exception {
        Path topics = Files.createTempFile("hmm-topics", ".txt");
        Files.write(topics, Arrays.asList("0 0 1 x 0", "0 1 1 x 1", "1 0 1 x 2", "2 0 1 x 3"),
                StandardCharsets.UTF_8);
        Path sequences = Files.createTempFile("hmm-sequences", ".txt");
        Files.write(sequences, Arrays.asList("8", "8", "9"), StandardCharsets.UTF_8);

        MultinomialHMM hmm = new MultinomialHMM(4, topics.toString(), 1);
        hmm.loadSequenceIDsFromFile(sequences.toString());
        hmm.setGamma(0.25);
        hmm.setRandomSeed(99);
        hmm.initialize();
        String topicsBefore = hmm.stateTopics();
        String transitionsBefore = hmm.stateTransitionMatrix();
        hmm.setNumIterations(3);
        hmm.sample();

        assertEquals(topicsBefore, hmm.stateTopics());
        assertEquals(transitionsBefore, hmm.stateTransitionMatrix());
        assertEquals("1\t1\t1\t1\t\n", hmm.stateTopics());
    }

    @Test
    public void zeroIterationsIsAStableNoOpAfterInitialization() throws Exception {
        Path topics = Files.createTempFile("hmm-topics", ".txt");
        Files.write(topics, Arrays.asList("0 0 1 x 0", "1 0 1 x 1"), StandardCharsets.UTF_8);

        MultinomialHMM hmm = new MultinomialHMM(4, topics.toString(), 2);
        hmm.setGamma(1.0);
        hmm.setRandomSeed(42);
        hmm.initialize();
        String beforeTopics = hmm.stateTopics();
        String beforeTransitions = hmm.stateTransitionMatrix();
        hmm.setBurninPeriod(5);
        hmm.setOptimizeInterval(2);
        hmm.setTopicDisplayInterval(1);
        hmm.setNumIterations(0);
        hmm.sample();

        assertEquals(beforeTopics, hmm.stateTopics());
        assertEquals(beforeTransitions, hmm.stateTransitionMatrix());
    }

    @Test
    public void emptyCorpusInitializesToZeroReports() throws Exception {
        Path topics = Files.createTempFile("hmm-empty", ".txt");
        Files.write(topics, Collections.singletonList("# no tokens"), StandardCharsets.UTF_8);

        MultinomialHMM hmm = new MultinomialHMM(4, topics.toString(), 2);
        hmm.setGamma(1.0);
        hmm.initialize();

        assertEquals("0\t0\t0\t0\t\n0\t0\t0\t0\t\n", hmm.stateTopics());
        assertEquals("0\t0\t\n0\t0\t\n", hmm.stateTransitionMatrix());
    }

    @Test
    public void multipleStatesStillPartitionEveryObservedToken() throws Exception {
        Path topics = Files.createTempFile("hmm-topics", ".txt");
        Files.write(topics, Arrays.asList(
                "0 0 1 x 0", "0 1 1 x 0", "1 0 1 x 1", "2 0 1 x 2", "2 1 1 x 3"),
                StandardCharsets.UTF_8);

        MultinomialHMM hmm = new MultinomialHMM(4, topics.toString(), 2);
        hmm.setGamma(1.0);
        hmm.setRandomSeed(12);
        hmm.initialize();

        String[] rows = hmm.stateTopics().split("\\n");
        String[] first = rows[0].split("\\t");
        String[] second = rows[1].split("\\t");
        assertEquals(2, rows.length);
        assertEquals(2, Integer.parseInt(first[0]) + Integer.parseInt(second[0]));
        assertEquals(1, Integer.parseInt(first[1]) + Integer.parseInt(second[1]));
        assertEquals(1, Integer.parseInt(first[2]) + Integer.parseInt(second[2]));
        assertEquals(1, Integer.parseInt(first[3]) + Integer.parseInt(second[3]));
    }

    @Test(expected = FileNotFoundException.class)
    public void constructorRejectsMissingTopicFile() throws Exception {
        Path directory = Files.createTempDirectory("missing-hmm");
        new MultinomialHMM(4, directory.resolve("absent.txt").toString(), 1);
    }

    @Test(expected = NumberFormatException.class)
    public void constructorRejectsNonNumericDocumentId() throws Exception {
        Path topics = Files.createTempFile("hmm-bad-doc", ".txt");
        Files.write(topics, Collections.singletonList("doc 0 1 x 0"), StandardCharsets.UTF_8);
        new MultinomialHMM(4, topics.toString(), 1);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void constructorRejectsTopicOutsideConfiguredRange() throws Exception {
        Path topics = Files.createTempFile("hmm-bad-topic", ".txt");
        Files.write(topics, Collections.singletonList("0 0 1 x 4"), StandardCharsets.UTF_8);
        new MultinomialHMM(4, topics.toString(), 1);
    }

    @Test(expected = NegativeArraySizeException.class)
    public void constructorRejectsNegativeStateCount() throws Exception {
        Path topics = Files.createTempFile("hmm-topics", ".txt");
        Files.write(topics, Collections.singletonList("0 0 1 x 0"), StandardCharsets.UTF_8);
        new MultinomialHMM(4, topics.toString(), -1);
    }

    @Test(expected = FileNotFoundException.class)
    public void loadAlphaRejectsMissingFile() throws Exception {
        Path topics = Files.createTempFile("hmm-topics", ".txt");
        Files.write(topics, Collections.singletonList("0 0 1 x 0"), StandardCharsets.UTF_8);
        Path directory = Files.createTempDirectory("missing-alpha");
        MultinomialHMM hmm = new MultinomialHMM(4, topics.toString(), 1);
        hmm.loadAlphaFromFile(directory.resolve("none.keys").toString());
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void loadAlphaRejectsTopicOutsideConfiguredRange() throws Exception {
        Path topics = Files.createTempFile("hmm-topics", ".txt");
        Files.write(topics, Collections.singletonList("0 0 1 x 0"), StandardCharsets.UTF_8);
        Path alpha = Files.createTempFile("hmm-alpha", ".txt");
        Files.write(alpha, Collections.singletonList("4 1 impossible"), StandardCharsets.UTF_8);
        MultinomialHMM hmm = new MultinomialHMM(4, topics.toString(), 1);
        hmm.loadAlphaFromFile(alpha.toString());
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void tooManySequenceRowsAreRejected() throws Exception {
        Path topics = Files.createTempFile("hmm-topics", ".txt");
        Files.write(topics, Collections.singletonList("0 0 1 x 0"), StandardCharsets.UTF_8);
        Path sequences = Files.createTempFile("hmm-sequences", ".txt");
        Files.write(sequences, Arrays.asList("1", "2"), StandardCharsets.UTF_8);
        MultinomialHMM hmm = new MultinomialHMM(4, topics.toString(), 1);
        hmm.loadSequenceIDsFromFile(sequences.toString());
    }

    @Test(expected = NullPointerException.class)
    public void reportsRequireInitialization() throws Exception {
        Path topics = Files.createTempFile("hmm-topics", ".txt");
        Files.write(topics, Collections.singletonList("0 0 1 x 0"), StandardCharsets.UTF_8);
        MultinomialHMM hmm = new MultinomialHMM(4, topics.toString(), 1);
        hmm.stateTopics();
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void reloadingAdditionalCopiesCanExceedConstructorCacheCapacity() throws Exception {
        Path topics = Files.createTempFile("hmm-topics", ".txt");
        Files.write(topics, Collections.singletonList("0 0 1 x 0"), StandardCharsets.UTF_8);
        MultinomialHMM hmm = new MultinomialHMM(4, topics.toString(), 1);
        hmm.loadTopicsFromFile(topics.toString());
        hmm.setGamma(1.0);
        hmm.initialize();
    }
}
