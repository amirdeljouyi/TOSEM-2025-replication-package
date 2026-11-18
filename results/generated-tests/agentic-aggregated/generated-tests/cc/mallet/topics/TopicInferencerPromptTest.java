package cc.mallet.topics;

import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureSequence;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

import static org.junit.Assert.*;

public class TopicInferencerPromptTest {

    @Test
    public void zeroIterationsUsesMostFrequentTopicForPowerOfTwoTopicCount() {
        Alphabet alphabet = new Alphabet(new Object[] {"apple", "banana"});
        int[][] counts = new int[][] {{6, 3}, {5, 2}};
        TopicInferencer inferencer = new TopicInferencer(
                counts, new int[] {10, 10}, alphabet, new double[] {1.0, 1.0}, 0.1, 0.2);
        FeatureSequence sequence = new FeatureSequence(alphabet, new int[] {0, 0, 1});
        Instance instance = new Instance(sequence, null, "document", null);

        double[] distribution = inferencer.getSampledDistribution(instance, 0, 1, 0);

        assertArrayEquals(new double[] {0.6, 0.4}, distribution, 1.0e-12);
    }

    @Test
    public void zeroIterationsDecodesNonPowerOfTwoTopicCount() {
        Alphabet alphabet = new Alphabet(new Object[] {"term"});
        int[][] counts = new int[][] {{9, 6}};
        TopicInferencer inferencer = new TopicInferencer(
                counts, new int[] {5, 6, 7}, alphabet,
                new double[] {0.5, 0.5, 0.5}, 0.1, 0.3);
        Instance instance = new Instance(
                new FeatureSequence(alphabet, new int[] {0, 0}), null, null, null);

        double[] distribution = inferencer.getSampledDistribution(instance, 0, 2, 0);

        assertArrayEquals(new double[] {1.0 / 7.0, 5.0 / 7.0, 1.0 / 7.0},
                distribution, 1.0e-12);
    }

    @Test
    public void emptyDocumentReturnsNormalizedPrior() {
        Alphabet alphabet = new Alphabet(new Object[] {"known"});
        TopicInferencer inferencer = new TopicInferencer(
                new int[][] {{4, 3}}, new int[] {12, 8}, alphabet,
                new double[] {2.0, 1.0}, 0.25, 0.5);
        Instance instance = new Instance(new FeatureSequence(alphabet, new int[0]), null, null, null);

        double[] distribution = inferencer.getSampledDistribution(instance, 25, 5, 5);

        assertArrayEquals(new double[] {2.0 / 3.0, 1.0 / 3.0}, distribution, 1.0e-12);
    }

    @Test
    public void outOfVocabularyTokensDoNotContributeToDocumentCounts() {
        Alphabet alphabet = new Alphabet(new Object[] {"known", "unused", "new"});
        TopicInferencer inferencer = new TopicInferencer(
                new int[][] {{5, 2}, {4, 3}}, new int[] {9, 11}, alphabet,
                new double[] {1.0, 3.0}, 0.1, 0.3);
        Instance instance = new Instance(
                new FeatureSequence(alphabet, new int[] {2, 2, 2}), null, null, null);

        double[] distribution = inferencer.getSampledDistribution(instance, 10, 2, 2);

        assertArrayEquals(new double[] {0.25, 0.75}, distribution, 1.0e-12);
    }

    @Test
    public void typeWithNoTrainingCountsIsIgnored() {
        Alphabet alphabet = new Alphabet(new Object[] {"seen", "unseen"});
        TopicInferencer inferencer = new TopicInferencer(
                new int[][] {{4}, {}}, new int[] {4, 4}, alphabet,
                new double[] {3.0, 1.0}, 0.1, 0.2);
        Instance instance = new Instance(
                new FeatureSequence(alphabet, new int[] {1, 1}), null, null, null);

        double[] distribution = inferencer.getSampledDistribution(instance, 6, 2, 1);

        assertArrayEquals(new double[] {0.75, 0.25}, distribution, 1.0e-12);
    }

    @Test
    public void burnInBeyondIterationsStillReturnsOneNormalizedSample() {
        Alphabet alphabet = new Alphabet(new Object[] {"a", "b"});
        TopicInferencer inferencer = new TopicInferencer(
                new int[][] {{8, 3}, {7, 2}}, new int[] {20, 20}, alphabet,
                new double[] {0.5, 0.5}, 0.01, 0.02);
        inferencer.setRandomSeed(91);
        Instance instance = new Instance(
                new FeatureSequence(alphabet, new int[] {0, 1, 0}), null, null, null);

        double[] distribution = inferencer.getSampledDistribution(instance, 4, 2, 10);

        assertEquals(1.0, distribution[0] + distribution[1], 1.0e-12);
        assertTrue(distribution[0] > 0.0);
        assertTrue(distribution[1] > 0.0);
    }

    @Test
    public void resettingSeedReproducesSamplingAcrossCalls() {
        Alphabet alphabet = new Alphabet(new Object[] {"a", "b", "c"});
        TopicInferencer inferencer = new TopicInferencer(
                new int[][] {{20, 5}, {17, 8}, {12, 13}}, new int[] {40, 40}, alphabet,
                new double[] {0.75, 0.75}, 0.2, 0.6);
        Instance instance = new Instance(
                new FeatureSequence(alphabet, new int[] {0, 1, 2, 0, 2}), null, null, null);
        inferencer.setRandomSeed(12345);
        double[] first = inferencer.getSampledDistribution(instance, 30, 3, 6);
        inferencer.setRandomSeed(12345);

        double[] second = inferencer.getSampledDistribution(instance, 30, 3, 6);

        assertArrayEquals(first, second, 0.0);
        assertEquals(1.0, second[0] + second[1], 1.0e-12);
    }

    @Test(expected = ArithmeticException.class)
    public void zeroThinningFailsWhenAnIterationWouldBeSaved() {
        Alphabet alphabet = new Alphabet(new Object[] {"word"});
        TopicInferencer inferencer = new TopicInferencer(
                new int[][] {{4, 3}}, new int[] {3, 3}, alphabet,
                new double[] {1.0, 1.0}, 0.1, 0.2);
        Instance instance = new Instance(new FeatureSequence(alphabet, new int[] {0}), null, null, null);

        inferencer.getSampledDistribution(instance, 1, 0, 0);
    }

    @Test(expected = ClassCastException.class)
    public void nonFeatureSequenceInstanceDataIsRejected() {
        Alphabet alphabet = new Alphabet(new Object[] {"word"});
        TopicInferencer inferencer = new TopicInferencer(
                new int[][] {{4}}, new int[] {3}, alphabet,
                new double[] {1.0}, 0.1, 0.1);

        inferencer.getSampledDistribution(new Instance("word", null, null, null), 0, 1, 0);
    }

    @Test
    public void noTopicsAndEmptyDocumentReturnsEmptyDistribution() {
        Alphabet alphabet = new Alphabet();
        TopicInferencer inferencer = new TopicInferencer(
                new int[0][], new int[0], alphabet, new double[0], 0.1, 0.0);
        Instance instance = new Instance(new FeatureSequence(alphabet, new int[0]), null, null, null);

        double[] distribution = inferencer.getSampledDistribution(instance, 0, 1, 0);

        assertArrayEquals(new double[0], distribution, 0.0);
    }

    @Test
    public void writeDenseDistributionsIncludesHeaderNamesAndNoNameFallback() throws Exception {
        Alphabet alphabet = new Alphabet(new Object[] {"left", "right"});
        TopicInferencer inferencer = new TopicInferencer(
                new int[][] {{6, 3}, {7, 2}}, new int[] {10, 10}, alphabet,
                new double[] {1.0, 1.0}, 0.1, 0.2);
        InstanceList instances = new InstanceList(alphabet, null);
        instances.add(new Instance(new FeatureSequence(alphabet, new int[] {0}), null, "named", null));
        instances.add(new Instance(new FeatureSequence(alphabet, new int[] {1}), null, null, null));
        File output = Files.createTempFile("inferencer-dense", ".txt").toFile();

        inferencer.writeInferredDistributions(instances, output, 0, 1, 0, 0.0, -1);
        List<String> lines = Files.readAllLines(output.toPath(), StandardCharsets.UTF_8);

        assertEquals(3, lines.size());
        assertEquals("#doc name topic proportion ...", lines.get(0));
        assertEquals("0\tnamed\t0.6666666666666666\t0.3333333333333333", lines.get(1));
        assertEquals("1\tno-name\t0.3333333333333333\t0.6666666666666666", lines.get(2));
        assertTrue(output.delete());
    }

    @Test
    public void writeSparseDistributionSortsByWeightAndHonorsMaximum() throws Exception {
        Alphabet alphabet = new Alphabet(new Object[] {"word"});
        TopicInferencer inferencer = new TopicInferencer(
                new int[][] {{13, 6, 3}}, new int[] {10, 10, 10}, alphabet,
                new double[] {1.0, 1.0, 1.0}, 0.1, 0.3);
        InstanceList instances = new InstanceList(alphabet, null);
        instances.add(new Instance(
                new FeatureSequence(alphabet, new int[] {0, 0, 0}), null, "ranked", null));
        File output = Files.createTempFile("inferencer-sparse", ".txt").toFile();

        inferencer.writeInferredDistributions(instances, output, 0, 1, 0, 0.1, 1);
        List<String> lines = Files.readAllLines(output.toPath(), StandardCharsets.UTF_8);

        assertEquals(2, lines.size());
        assertEquals("0\tranked\t1\t0.6666666666666666", lines.get(1));
        assertTrue(output.delete());
    }

    @Test
    public void sparseThresholdCanSuppressEveryTopic() throws Exception {
        Alphabet alphabet = new Alphabet(new Object[] {"word"});
        TopicInferencer inferencer = new TopicInferencer(
                new int[][] {{4, 3}}, new int[] {5, 5}, alphabet,
                new double[] {1.0, 1.0}, 0.1, 0.2);
        InstanceList instances = new InstanceList(alphabet, null);
        instances.add(new Instance(new FeatureSequence(alphabet, new int[] {0}), null, "doc", null));
        File output = Files.createTempFile("inferencer-threshold", ".txt").toFile();

        inferencer.writeInferredDistributions(instances, output, 0, 1, 0, 1.1, 2);
        List<String> lines = Files.readAllLines(output.toPath(), StandardCharsets.UTF_8);

        assertEquals("0\tdoc", lines.get(1));
        assertTrue(output.delete());
    }

    @Test
    public void maximumAboveTopicCountIsClamped() throws Exception {
        Alphabet alphabet = new Alphabet(new Object[] {"word"});
        TopicInferencer inferencer = new TopicInferencer(
                new int[][] {{4, 3}}, new int[] {5, 5}, alphabet,
                new double[] {1.0, 1.0}, 0.1, 0.2);
        InstanceList instances = new InstanceList(alphabet, null);
        instances.add(new Instance(new FeatureSequence(alphabet, new int[0]), null, "doc", null));
        File output = Files.createTempFile("inferencer-max", ".txt").toFile();

        inferencer.writeInferredDistributions(instances, output, 0, 1, 0, 0.01, 99);
        String line = Files.readAllLines(output.toPath(), StandardCharsets.UTF_8).get(1);

        assertEquals(6, line.split("\\t").length);
        assertTrue(line.contains("\t0\t0.5"));
        assertTrue(line.contains("\t1\t0.5"));
        assertTrue(output.delete());
    }

    @Test
    public void serializedInferencerCanBeReadAndRetainsSeededState() throws Exception {
        Alphabet alphabet = new Alphabet(new Object[] {"a", "b"});
        TopicInferencer inferencer = new TopicInferencer(
                new int[][] {{10, 5}, {9, 6}}, new int[] {15, 15}, alphabet,
                new double[] {0.5, 0.5}, 0.1, 0.2);
        inferencer.setRandomSeed(808);
        File serialized = Files.createTempFile("inferencer", ".ser").toFile();
        ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(serialized));
        stream.writeObject(inferencer);
        stream.close();
        TopicInferencer restored = TopicInferencer.read(serialized);
        Instance instance = new Instance(
                new FeatureSequence(alphabet, new int[] {0, 1, 0, 1}), null, "doc", null);

        double[] originalResult = inferencer.getSampledDistribution(instance, 12, 2, 2);
        double[] restoredResult = restored.getSampledDistribution(instance, 12, 2, 2);

        assertNotSame(inferencer, restored);
        assertArrayEquals(originalResult, restoredResult, 0.0);
        assertTrue(serialized.delete());
    }

    @Test(expected = java.io.StreamCorruptedException.class)
    public void readRejectsFileThatIsNotASerializedInferencer() throws Exception {
        File invalid = Files.createTempFile("not-inferencer", ".ser").toFile();
        Files.write(invalid.toPath(), new byte[] {1, 2, 3, 4});

        TopicInferencer.read(invalid);
    }

    @Test(expected = java.io.FileNotFoundException.class)
    public void writingToDirectoryPropagatesFileOpenFailure() throws Exception {
        Alphabet alphabet = new Alphabet(new Object[] {"word"});
        TopicInferencer inferencer = new TopicInferencer(
                new int[][] {{4}}, new int[] {3}, alphabet,
                new double[] {1.0}, 0.1, 0.1);
        InstanceList instances = new InstanceList(alphabet, null);
        File directory = Files.createTempDirectory("inferencer-output-dir").toFile();

        inferencer.writeInferredDistributions(instances, directory, 0, 1, 0, 0.0, 1);
    }
}
