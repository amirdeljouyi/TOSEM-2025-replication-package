package edu.illinois.cs.cogcomp.pos;

import edu.illinois.cs.cogcomp.core.datastructures.vectors.ExceptionlessOutputStream;
import edu.illinois.cs.cogcomp.lbjava.learn.Learner;
import edu.illinois.cs.cogcomp.lbjava.nlp.Word;
import edu.illinois.cs.cogcomp.lbjava.nlp.seg.Token;
import edu.illinois.cs.cogcomp.pos.lbjava.POSLabel;
import edu.illinois.cs.cogcomp.pos.lbjava.WordForm;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;

import static org.junit.Assert.*;

public class MikheevLearnerPromptTest {

    @Test
    public void constructorsProduceUsableEmptyLearners() {
        MikheevLearner defaultLearner = new MikheevLearner();
        MikheevLearner namedLearner = new MikheevLearner("suffixes");
        MikheevLearner parameterLearner = new MikheevLearner(new MikheevLearner.Parameters());

        assertEquals(new HashSet<>(Arrays.asList("NN")),
                defaultLearner.allowableTags(new Word("ordinary")));
        assertEquals(new HashSet<>(Arrays.asList("NNP")),
                namedLearner.allowableTags(new Word("London")));
        assertEquals(new HashSet<>(Arrays.asList("NN", "JJ")),
                parameterLearner.allowableTags(new Word("well-known")));
    }

    @Test
    public void learnsBothThreeAndFourLetterSuffixesForLowercaseWords() {
        MikheevLearner learner = new MikheevLearner();
        learner.setExtractor(new WordForm());
        learner.setLabeler(new POSLabel());
        learner.learn(new Token(new Word("walking"), null, "VBG"));

        assertEquals(new HashSet<>(Arrays.asList("VBG")),
                learner.allowableTags(new Word("talking")));
        assertEquals(new HashSet<>(Arrays.asList("VBG")),
                learner.allowableTags(new Word("king")));
        assertEquals(new HashSet<>(Arrays.asList("NN")),
                learner.allowableTags(new Word("talkers")));
    }

    @Test
    public void lengthFiveWordLearnsOnlyThreeLetterSuffix() {
        MikheevLearner learner = new MikheevLearner();
        learner.setExtractor(new WordForm());
        learner.setLabeler(new POSLabel());
        learner.learn(new Token(new Word("boxes"), null, "NNS"));

        assertEquals(new HashSet<>(Arrays.asList("NNS")),
                learner.allowableTags(new Word("taxes")));
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        learner.write(new PrintStream(bytes));
        String text = new String(bytes.toByteArray(), StandardCharsets.UTF_8);
        assertTrue(text.contains("xes: NNS(1)"));
        assertFalse(text.contains("oxes: NNS(1)"));
    }

    @Test
    public void wordsShorterThanFiveAndNonLetterEndingsAreIgnored() {
        MikheevLearner learner = new MikheevLearner();
        learner.setExtractor(new WordForm());
        learner.setLabeler(new POSLabel());
        learner.learn(new Token(new Word("tiny"), null, "JJ"));
        learner.learn(new Token(new Word("abc12"), null, "CD"));
        learner.learn(new Token(new Word("word!"), null, "SYM"));

        assertEquals(new HashSet<>(Arrays.asList("NN")),
                learner.allowableTags(new Word("shiny")));
        assertEquals(new HashSet<>(Arrays.asList("NN")),
                learner.allowableTags(new Word("zbc12")));
        assertEquals(new HashSet<>(Arrays.asList("NN")),
                learner.allowableTags(new Word("sword!")));
    }

    @Test
    public void lowercaseHyphenatedWordsAreNotLearnedAndHaveHardCodedTags() {
        MikheevLearner learner = new MikheevLearner();
        learner.setExtractor(new WordForm());
        learner.setLabeler(new POSLabel());
        learner.learn(new Token(new Word("fast-moving"), null, "VBG"));

        assertEquals(new HashSet<>(Arrays.asList("NN", "JJ")),
                learner.allowableTags(new Word("slow-moving")));
        assertEquals(new HashSet<>(Arrays.asList("NN")),
                learner.allowableTags(new Word("removing")));
    }

    @Test
    public void capitalizedFirstWordLearningIsCaseInsensitiveAndPositionSpecific() {
        MikheevLearner learner = new MikheevLearner();
        learner.setExtractor(new WordForm());
        learner.setLabeler(new POSLabel());
        learner.learn(new Token(new Word("Running"), null, "VBG"));

        assertEquals(new HashSet<>(Arrays.asList("VBG")),
                learner.allowableTags(new Word("SINGING")));
        Token previous = new Token(new Word("The"), null, "DT");
        assertEquals(new HashSet<>(Arrays.asList("NNP")),
                learner.allowableTags(new Token(new Word("Singing"), previous, "NNP")));
        assertEquals(new HashSet<>(Arrays.asList("NN")),
                learner.allowableTags(new Word("singing")));
    }

    @Test
    public void capitalizedNonFirstWordLearningStaysInItsOwnBucket() {
        MikheevLearner learner = new MikheevLearner();
        learner.setExtractor(new WordForm());
        learner.setLabeler(new POSLabel());
        Token previous = new Token(new Word("A"), null, "DT");
        learner.learn(new Token(new Word("Darwin"), previous, "NNP"));

        assertEquals(new HashSet<>(Arrays.asList("NNP")),
                learner.allowableTags(new Token(new Word("Marwin"), previous, "NNP")));
        assertEquals(new HashSet<>(Arrays.asList("NNP")),
                learner.allowableTags(new Word("Marwin")));
        assertEquals(new HashSet<>(Arrays.asList("NN")),
                learner.allowableTags(new Word("marwin")));
    }

    @Test
    public void multipleLabelsForOneSuffixAreAllReturned() {
        MikheevLearner learner = new MikheevLearner();
        learner.setExtractor(new WordForm());
        learner.setLabeler(new POSLabel());
        learner.learn(new Token(new Word("creation"), null, "NN"));
        learner.learn(new Token(new Word("relation"), null, "VB"));
        learner.learn(new Token(new Word("vacation"), null, "JJ"));

        assertEquals(new HashSet<>(Arrays.asList("JJ", "NN", "VB")),
                learner.allowableTags(new Word("notation")));
    }

    @Test
    public void exactSuperclassDefaultsTakePriorityOverSuffixDefaults() {
        MikheevLearner learner = new MikheevLearner();

        assertEquals(new HashSet<>(Arrays.asList(":")),
                learner.allowableTags(new Word(";")));
        assertEquals(new HashSet<>(Arrays.asList("CD")),
                learner.allowableTags(new Word("12,345.67")));
        assertEquals(new HashSet<>(Arrays.asList("NN")),
                learner.allowableTags(new Word("12a")));
    }

    @Test
    public void pruneRemovesEntriesAtTenButRetainsEntriesAtEleven() {
        MikheevLearner learner = new MikheevLearner();
        HashMap<String, TreeMap<String, Integer>> map = new HashMap<>();
        TreeMap<String, Integer> ten = new TreeMap<>();
        ten.put("NN", 10);
        TreeMap<String, Integer> eleven = new TreeMap<>();
        eleven.put("VB", 11);
        map.put("ten", ten);
        map.put("eleven", eleven);

        learner.prune(map);

        assertFalse(map.containsKey("ten"));
        assertEquals(Integer.valueOf(11), map.get("eleven").get("VB"));
    }

    @Test
    public void pruneDropsTagsBelowOnePercentAndRecomputesRemainingTotal() {
        MikheevLearner learner = new MikheevLearner();
        HashMap<String, TreeMap<String, Integer>> map = new HashMap<>();
        TreeMap<String, Integer> counts = new TreeMap<>();
        counts.put("AA", 1);
        counts.put("BB", 100);
        map.put("suffix", counts);

        learner.prune(map);

        assertEquals(1, map.get("suffix").size());
        assertEquals(Integer.valueOf(100), map.get("suffix").get("BB"));
        assertFalse(map.get("suffix").containsKey("AA"));
    }

    @Test
    public void doneLearningPrunesSparseLearnedSuffixes() {
        MikheevLearner learner = new MikheevLearner();
        learner.setExtractor(new WordForm());
        learner.setLabeler(new POSLabel());
        Token token = new Token(new Word("walking"), null, "VBG");
        learner.learn(token);
        learner.learn(token);
        learner.learn(token);
        learner.learn(token);
        learner.learn(token);
        learner.learn(token);
        learner.learn(token);
        learner.learn(token);
        learner.learn(token);
        learner.learn(token);
        assertEquals(new HashSet<>(Arrays.asList("VBG")),
                learner.allowableTags(new Word("talking")));

        learner.doneLearning();

        assertEquals(new HashSet<>(Arrays.asList("NN")),
                learner.allowableTags(new Word("talking")));
    }

    @Test
    public void doneLearningRetainsSuffixesSeenElevenTimes() {
        MikheevLearner learner = new MikheevLearner();
        learner.setExtractor(new WordForm());
        learner.setLabeler(new POSLabel());
        Token token = new Token(new Word("jumping"), null, "VBG");
        learner.learn(token);
        learner.learn(token);
        learner.learn(token);
        learner.learn(token);
        learner.learn(token);
        learner.learn(token);
        learner.learn(token);
        learner.learn(token);
        learner.learn(token);
        learner.learn(token);
        learner.learn(token);

        learner.doneLearning();

        assertEquals(new HashSet<>(Arrays.asList("VBG")),
                learner.allowableTags(new Word("camping")));
    }

    @Test
    public void forgetClearsAllThreeLearnedCategories() {
        MikheevLearner learner = new MikheevLearner();
        learner.setExtractor(new WordForm());
        learner.setLabeler(new POSLabel());
        Token previous = new Token(new Word("The"), null, "DT");
        learner.learn(new Token(new Word("walking"), null, "VBG"));
        learner.learn(new Token(new Word("Running"), null, "VBG"));
        learner.learn(new Token(new Word("Darwin"), previous, "NNP"));

        learner.forget();

        assertEquals(new HashSet<>(Arrays.asList("NN")),
                learner.allowableTags(new Word("talking")));
        assertEquals(new HashSet<>(Arrays.asList("NNP")),
                learner.allowableTags(new Word("Singing")));
        assertEquals(new HashSet<>(Arrays.asList("NNP")),
                learner.allowableTags(new Token(new Word("Marwin"), previous, "NNP")));
    }

    @Test
    public void emptyCloneHasSameRuntimeTypeButNoLearnedRules() {
        MikheevLearner learner = new MikheevLearner("trained");
        learner.setExtractor(new WordForm());
        learner.setLabeler(new POSLabel());
        learner.learn(new Token(new Word("walking"), null, "VBG"));

        Learner clone = learner.emptyClone();

        assertTrue(clone instanceof MikheevLearner);
        assertNotSame(learner, clone);
        assertEquals(new HashSet<>(Arrays.asList("NN")),
                ((MikheevLearner) clone).allowableTags(new Word("talking")));
        assertEquals(new HashSet<>(Arrays.asList("VBG")),
                learner.allowableTags(new Word("talking")));
    }

    @Test
    public void textWriteSeparatesAndLabelsEveryCategory() throws Exception {
        MikheevLearner learner = new MikheevLearner();
        learner.setExtractor(new WordForm());
        learner.setLabeler(new POSLabel());
        Token previous = new Token(new Word("The"), null, "DT");
        learner.learn(new Token(new Word("walking"), null, "VBG"));
        learner.learn(new Token(new Word("Running"), null, "VBG"));
        learner.learn(new Token(new Word("Darwin"), previous, "NNP"));
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintStream output = new PrintStream(bytes, true, StandardCharsets.UTF_8.name());

        learner.write(output);
        String text = bytes.toString(StandardCharsets.UTF_8.name());

        assertTrue(text.contains("# if capitalized and first word in sentence:"));
        assertTrue(text.contains("# if capitalized and not first word in sentence:"));
        assertTrue(text.contains("# main table:"));
        assertTrue(text.contains("ning: VBG(1)"));
        assertTrue(text.contains("rwin: NNP(1)"));
    }

    @Test
    public void binaryWriteEmitsARepresentationOfAllLearnedCategories() {
        MikheevLearner original = new MikheevLearner("serialized");
        original.setExtractor(new WordForm());
        original.setLabeler(new POSLabel());
        Token previous = new Token(new Word("The"), null, "DT");
        original.learn(new Token(new Word("walking"), null, "VBG"));
        original.learn(new Token(new Word("Running"), null, "VB"));
        original.learn(new Token(new Word("Darwin"), previous, "NNP"));
        original.setExtractor(null);
        original.setLabeler(null);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ExceptionlessOutputStream output = new ExceptionlessOutputStream(bytes);
        original.write(output);
        output.close();

        assertTrue(bytes.size() > 0);
    }

    @Test(expected = NullPointerException.class)
    public void learnWithoutConfiguredExtractorFailsFast() {
        MikheevLearner learner = new MikheevLearner();
        learner.learn(new Token(new Word("walking"), null, "VBG"));
    }

    @Test(expected = NullPointerException.class)
    public void allowableTagsRejectsNullWord() {
        MikheevLearner learner = new MikheevLearner();
        learner.allowableTags((Word) null);
    }
}
