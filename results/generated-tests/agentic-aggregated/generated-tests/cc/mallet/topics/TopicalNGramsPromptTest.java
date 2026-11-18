package cc.mallet.topics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Test;

import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureSequence;
import cc.mallet.types.FeatureSequenceWithBigrams;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.Token;
import cc.mallet.types.TokenSequence;
import cc.mallet.util.Randoms;

public class TopicalNGramsPromptTest {

    @Test
    public void estimateSingleTopicProducesCompleteState() {
        Alphabet words = new Alphabet();
        Alphabet bigrams = new Alphabet();
        TokenSequence tokens = new TokenSequence(new Object[] {"red", "fox", "runs"});
        FeatureSequenceWithBigrams sequence =
                new FeatureSequenceWithBigrams(words, bigrams, tokens);
        InstanceList documents = new InstanceList(words, null);
        documents.add(new Instance(sequence, null, "doc-one", "source-one"));
        TopicalNGrams model = new TopicalNGrams(1);

        model.estimate(documents, 3, 0, 0, null, new Randoms(11));
        StringWriter sink = new StringWriter();
        PrintWriter writer = new PrintWriter(sink);
        model.printState(writer);
        writer.flush();

        String state = sink.toString();
        assertTrue(state.startsWith("#doc pos typeindex type bigrampossible? topic bigram\n"));
        assertTrue(state.contains("0 0 0 red 0 0 0\n"));
        assertTrue(state.matches("(?s).*0 1 1 fox 1 0 [01]\\n.*"));
        assertTrue(state.matches("(?s).*0 2 2 runs 1 0 [01]\\n.*"));
        assertEquals(4, state.split("\\R").length);
    }

    @Test
    public void estimateMultipleDocumentsHonorsDeletedBigramBoundary() {
        Alphabet words = new Alphabet();
        Alphabet bigrams = new Alphabet();
        Token first = new Token("new");
        first.setProperty(FeatureSequenceWithBigrams.deletionMark, Boolean.TRUE);
        TokenSequence firstTokens = new TokenSequence(new Token[] {
                first, new Token("york"), new Token("city")
        });
        TokenSequence secondTokens = new TokenSequence(new Object[] {"new", "ideas"});
        InstanceList documents = new InstanceList(words, null);
        documents.add(new Instance(new FeatureSequenceWithBigrams(words, bigrams, firstTokens),
                null, "first", "new york city"));
        documents.add(new Instance(new FeatureSequenceWithBigrams(words, bigrams, secondTokens),
                null, "second", "new ideas"));
        TopicalNGrams model = new TopicalNGrams(3, 3.0, 0.2, 0.3, 0.4, 0.5, 0.6);

        model.estimate(documents, 5, 0, 0, null, new Randoms(29));
        StringWriter sink = new StringWriter();
        PrintWriter writer = new PrintWriter(sink);
        model.printState(writer);
        writer.flush();

        String state = sink.toString();
        assertTrue(state.contains("0 0 0 new 0 "));
        assertTrue(state.contains("0 1 1 york 0 "));
        assertTrue(state.contains("0 2 2 city 1 "));
        assertTrue(state.contains("1 0 0 new 0 "));
        assertTrue(state.contains("1 1 3 ideas 1 "));
        assertEquals(6, state.split("\\R").length);
    }

    @Test
    public void printDocumentTopicsReportsSourcesAndCertainSingleTopic() {
        Alphabet words = new Alphabet();
        Alphabet bigrams = new Alphabet();
        InstanceList documents = new InstanceList(words, null);
        documents.add(new Instance(new FeatureSequenceWithBigrams(words, bigrams,
                new TokenSequence(new Object[] {"alpha", "beta"})), null, "a", "source-A"));
        documents.add(new Instance(new FeatureSequenceWithBigrams(words, bigrams,
                new TokenSequence(new Object[] {"gamma"})), null, "b", "source-B"));
        TopicalNGrams model = new TopicalNGrams(1);
        model.estimate(documents, 1, 0, 0, null, new Randoms(3));
        StringWriter sink = new StringWriter();
        PrintWriter writer = new PrintWriter(sink);

        model.printDocumentTopics(writer, 0.0, -1);
        writer.flush();

        assertEquals("#doc source topic proportions\n"
                + "0 source-A 0 1.0  \n"
                + "1 source-B 0 1.0  \n", sink.toString());
    }

    @Test
    public void printDocumentTopicsAppliesThresholdAndMaximum() {
        Alphabet words = new Alphabet();
        Alphabet bigrams = new Alphabet();
        InstanceList documents = new InstanceList(words, null);
        documents.add(new Instance(new FeatureSequenceWithBigrams(words, bigrams,
                new TokenSequence(new Object[] {"one", "two", "three", "four"})),
                null, "doc", "threshold-source"));
        TopicalNGrams model = new TopicalNGrams(2);
        model.estimate(documents, 0, 0, 0, null, new Randoms(7));
        StringWriter noneSink = new StringWriter();
        PrintWriter noneWriter = new PrintWriter(noneSink);
        StringWriter oneSink = new StringWriter();
        PrintWriter oneWriter = new PrintWriter(oneSink);

        model.printDocumentTopics(noneWriter, 1.1, 2);
        model.printDocumentTopics(oneWriter, 0.0, 1);
        noneWriter.flush();
        oneWriter.flush();

        assertEquals("#doc source topic proportions\n0 threshold-source  \n", noneSink.toString());
        assertTrue(oneSink.toString().startsWith(
                "#doc source topic proportions\n0 threshold-source "));
        assertTrue(oneSink.toString().matches(
                "(?s)#doc source topic proportions\\n0 threshold-source [01] (0\\.25|0\\.5|0\\.75|1\\.0)  \\n"));
    }

    @Test
    public void conveniencePrintDocumentTopicsWriterIsNoOp() {
        TopicalNGrams model = new TopicalNGrams(2);
        StringWriter sink = new StringWriter();
        PrintWriter writer = new PrintWriter(sink);

        model.printDocumentTopics(writer);
        writer.flush();

        assertEquals("", sink.toString());
    }

    @Test
    public void printStateFileContainsEveryToken() throws Exception {
        Alphabet words = new Alphabet();
        Alphabet bigrams = new Alphabet();
        InstanceList documents = new InstanceList(words, null);
        documents.add(new Instance(new FeatureSequenceWithBigrams(words, bigrams,
                new TokenSequence(new Object[] {"file", "state"})), null, "doc", "file-source"));
        TopicalNGrams model = new TopicalNGrams(2);
        model.estimate(documents, 2, 0, 0, null, new Randoms(13));
        Path directory = Files.createTempDirectory("tng-state-");
        Path output = directory.resolve("state.txt");

        model.printState(output.toFile());
        String state = Files.readString(output, StandardCharsets.UTF_8);

        assertTrue(state.contains("#doc pos typeindex type bigrampossible? topic bigram"));
        assertTrue(state.contains("0 0 0 file 0 "));
        assertTrue(state.contains("0 1 1 state 1 "));
        assertTrue(Files.deleteIfExists(output));
        assertTrue(Files.deleteIfExists(directory));
    }

    @Test
    public void printTopWordsCompactIncludesVocabularyAndSummary() throws Exception {
        Alphabet words = new Alphabet();
        Alphabet bigrams = new Alphabet();
        InstanceList documents = new InstanceList(words, null);
        documents.add(new Instance(new FeatureSequenceWithBigrams(words, bigrams,
                new TokenSequence(new Object[] {"cat", "sat", "cat"})), null, "doc", "cats"));
        TopicalNGrams model = new TopicalNGrams(1);
        model.estimate(documents, 0, 0, 0, null, new Randoms(5));
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintStream original = System.out;

        try {
            System.setOut(new PrintStream(bytes, true, StandardCharsets.UTF_8.name()));
            model.printTopWords(10, false);
        } finally {
            System.setOut(original);
        }

        String output = bytes.toString(StandardCharsets.UTF_8.name());
        assertTrue(output.startsWith("Topic 0: "));
        assertTrue(output.contains("cat"));
        assertTrue(output.contains("sat"));
        assertTrue(output.contains("(unigrams "));
        assertTrue(output.contains(" bigrams "));
        assertTrue(output.contains(" phrases "));
    }

    @Test
    public void printTopWordsMultilinePrintsTopicStatistics() throws Exception {
        Alphabet words = new Alphabet();
        Alphabet bigrams = new Alphabet();
        InstanceList documents = new InstanceList(words, null);
        documents.add(new Instance(new FeatureSequenceWithBigrams(words, bigrams,
                new TokenSequence(new Object[] {"north", "wind", "north", "star"})),
                null, "doc", "weather"));
        TopicalNGrams model = new TopicalNGrams(2);
        model.estimate(documents, 4, 0, 0, null, new Randoms(41));
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintStream original = System.out;

        try {
            System.setOut(new PrintStream(bytes, true, StandardCharsets.UTF_8.name()));
            model.printTopWords(2, true);
        } finally {
            System.setOut(original);
        }

        String output = bytes.toString(StandardCharsets.UTF_8.name());
        assertTrue(output.contains("Topic 0 unigrams"));
        assertTrue(output.contains("Topic 1 unigrams"));
        assertTrue(output.contains("Topic 0 unigrams "));
        assertTrue(output.contains("Topic 1 unigrams "));
        assertFalse(output.contains("Topic 2"));
    }

    @Test
    public void estimateShowTopicsIntervalInvokesReportingDuringSampling() throws Exception {
        Alphabet words = new Alphabet();
        Alphabet bigrams = new Alphabet();
        InstanceList documents = new InstanceList(words, null);
        documents.add(new Instance(new FeatureSequenceWithBigrams(words, bigrams,
                new TokenSequence(new Object[] {"interval", "report"})),
                null, "doc", "interval-source"));
        TopicalNGrams model = new TopicalNGrams(1);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintStream original = System.out;

        try {
            System.setOut(new PrintStream(bytes, true, StandardCharsets.UTF_8.name()));
            model.estimate(documents, 2, 1, 0, null, new Randoms(17));
        } finally {
            System.setOut(original);
        }

        String output = bytes.toString(StandardCharsets.UTF_8.name());
        assertTrue(output.startsWith("0."));
        assertTrue(output.contains("Topic 0: "));
        assertTrue(output.contains("Total time (sec):"));
    }

    @Test
    public void estimateOutputIntervalCreatesRequestedSnapshot() throws Exception {
        Alphabet words = new Alphabet();
        Alphabet bigrams = new Alphabet();
        InstanceList documents = new InstanceList(words, null);
        documents.add(new Instance(new FeatureSequenceWithBigrams(words, bigrams,
                new TokenSequence(new Object[] {"model", "snapshot"})),
                null, "doc", "snapshot-source"));
        TopicalNGrams model = new TopicalNGrams(1);
        Path directory = Files.createTempDirectory("tng-model-");
        String prefix = directory.resolve("checkpoint").toString();

        model.estimate(documents, 2, 0, 1, prefix, new Randoms(23));
        Path snapshot = directory.resolve("checkpoint.1");

        assertTrue(Files.exists(snapshot));
        assertTrue(Files.size(snapshot) > 0L);
        assertTrue(Files.deleteIfExists(snapshot));
        assertTrue(Files.deleteIfExists(directory));
    }

    @Test
    public void estimateCanReplacePreviouslyEstimatedCorpus() {
        Alphabet firstWords = new Alphabet();
        Alphabet firstBigrams = new Alphabet();
        InstanceList firstDocuments = new InstanceList(firstWords, null);
        firstDocuments.add(new Instance(new FeatureSequenceWithBigrams(firstWords, firstBigrams,
                new TokenSequence(new Object[] {"old", "corpus"})), null, "old", "old-source"));
        Alphabet secondWords = new Alphabet();
        Alphabet secondBigrams = new Alphabet();
        InstanceList secondDocuments = new InstanceList(secondWords, null);
        secondDocuments.add(new Instance(new FeatureSequenceWithBigrams(secondWords, secondBigrams,
                new TokenSequence(new Object[] {"fresh"})), null, "fresh", "fresh-source"));
        TopicalNGrams model = new TopicalNGrams(1);

        model.estimate(firstDocuments, 1, 0, 0, null, new Randoms(2));
        model.estimate(secondDocuments, 1, 0, 0, null, new Randoms(2));
        StringWriter sink = new StringWriter();
        PrintWriter writer = new PrintWriter(sink);
        model.printState(writer);
        writer.flush();

        assertTrue(sink.toString().contains("0 0 0 fresh 0 0 0"));
        assertFalse(sink.toString().contains("old"));
    }

    @Test
    public void emptyDocumentCanBeEstimatedAndHasHeaderOnlyState() {
        Alphabet words = new Alphabet();
        Alphabet bigrams = new Alphabet();
        InstanceList documents = new InstanceList(words, null);
        documents.add(new Instance(new FeatureSequenceWithBigrams(words, bigrams,
                new TokenSequence()), null, "empty", "empty-source"));
        TopicalNGrams model = new TopicalNGrams(2);

        model.estimate(documents, 2, 0, 0, null, new Randoms(31));
        StringWriter sink = new StringWriter();
        PrintWriter writer = new PrintWriter(sink);
        model.printState(writer);
        writer.flush();

        assertEquals("#doc pos typeindex type bigrampossible? topic bigram\n", sink.toString());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void estimateRejectsCorpusWithoutDocuments() {
        Alphabet words = new Alphabet();
        InstanceList documents = new InstanceList(words, null);
        TopicalNGrams model = new TopicalNGrams(2);

        model.estimate(documents, 0, 0, 0, null, new Randoms(1));
    }

    @Test(expected = ClassCastException.class)
    public void estimateRejectsNonBigramFeatureSequenceData() {
        Alphabet words = new Alphabet();
        int ordinaryIndex = words.lookupIndex("ordinary");
        InstanceList documents = new InstanceList(words, null);
        documents.add(new Instance(new FeatureSequence(words, new int[] {ordinaryIndex}),
                null, "bad", "bad-source"));
        TopicalNGrams model = new TopicalNGrams(2);

        model.estimate(documents, 0, 0, 0, null, new Randoms(1));
    }

    @Test
    public void printStateBeforeEstimateFailsClearly() {
        TopicalNGrams model = new TopicalNGrams(2);
        PrintWriter writer = new PrintWriter(new StringWriter());

        try {
            model.printState(writer);
            fail("Expected uninitialized model state to be rejected");
        } catch (NullPointerException expected) {
            assertNotNull(expected);
        }
    }

    @Test
    public void fileConvenienceDocumentTopicsCreatesEmptyFile() throws Exception {
        TopicalNGrams model = new TopicalNGrams(2);
        Path directory = Files.createTempDirectory("tng-doc-topics-");
        File output = directory.resolve("topics.txt").toFile();

        model.printDocumentTopics(output);

        assertTrue(output.isFile());
        assertEquals(0L, output.length());
        assertTrue(Files.deleteIfExists(output.toPath()));
        assertTrue(Files.deleteIfExists(directory));
    }
}
