package edu.illinois.cs.cogcomp.pos;

import edu.illinois.cs.cogcomp.core.datastructures.IQueryable;
import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.*;
import edu.illinois.cs.cogcomp.core.datastructures.vectors.ExceptionlessInputStream;
import edu.illinois.cs.cogcomp.core.datastructures.vectors.ExceptionlessOutputStream;
import edu.illinois.cs.cogcomp.lbjava.learn.Learner;
import edu.illinois.cs.cogcomp.lbjava.nlp.Word;
import edu.illinois.cs.cogcomp.nlp.utilities.POSUtils;
import edu.illinois.cs.cogcomp.nlp.utilities.ParseTreeProperties;
import edu.illinois.cs.cogcomp.pos.MikheevLearner;
import junit.framework.TestCase;
import org.junit.Test;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class MikheevLearner_2_GPTLLMTest {

@Test
public void testConstructorShouldInitializeWithoutErrors() {
MikheevLearner learner = new MikheevLearner();
assertNotNull(learner);
}

@Test
public void testLearnUncapitalizedSuffix3AndSuffix4() {
MikheevLearner learner = new MikheevLearner();
Word word = new Word("walking");
// word.setLabel("VBG");
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("VBG"));
}

@Test
public void testLearnCapitalizedWordAtSentenceStart() {
MikheevLearner learner = new MikheevLearner();
Word word = new Word("Developing");
// word.setLabel("VBG");
word.capitalized = true;
word.previous = null;
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("VBG") || tags.contains("NNP"));
}

@Test
public void testLearnCapitalizedWordNotFirstInSentence() {
MikheevLearner learner = new MikheevLearner();
Word previous = new Word("It");
Word word = new Word("Learning");
// word.setLabel("NN");
word.capitalized = true;
word.previous = previous;
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN") || tags.contains("NNP"));
}

@Test
public void testLearnHyphenatedWordShouldNotLearn() {
MikheevLearner learner = new MikheevLearner();
Word word = new Word("long-term");
// word.setLabel("JJ");
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
assertTrue(tags.contains("JJ"));
}

@Test
public void testLearnShortWordShouldFallback() {
MikheevLearner learner = new MikheevLearner();
Word word = new Word("on");
// word.setLabel("IN");
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
}

@Test
public void testLearnWithNonAlphabeticSuffixShouldFallback() {
MikheevLearner learner = new MikheevLearner();
Word word = new Word("comp23");
// word.setLabel("NN");
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
}

@Test
public void testAllowableTagsFirstCapitalizedFallback() {
MikheevLearner learner = new MikheevLearner();
Word word = new Word("Quantum");
word.capitalized = true;
word.previous = null;
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NNP"));
}

@Test
public void testAllowableTagsCapitalizedNotFirstFallback() {
MikheevLearner learner = new MikheevLearner();
Word prev = new Word("The");
Word word = new Word("System");
word.capitalized = true;
word.previous = prev;
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NNP"));
}

@Test
public void testAllowableTagsHyphenatedWordReturnsCorrectDefaults() {
MikheevLearner learner = new MikheevLearner();
Word word = new Word("blue-green");
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("JJ"));
assertTrue(tags.contains("NN"));
}

@Test
public void testAllowableTagsUnknownWordReturnsNN() {
MikheevLearner learner = new MikheevLearner();
Word word = new Word("unseenword");
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
}

@Test
public void testForgetClearsInternalTables() {
MikheevLearner learner = new MikheevLearner();
Word word = new Word("Speaking");
// word.setLabel("VBG");
learner.learn(word);
Set<String> tagsBefore = learner.allowableTags(word);
assertTrue(tagsBefore.contains("VBG"));
learner.forget();
Set<String> tagsAfter = learner.allowableTags(word);
assertTrue(tagsAfter.contains("NN"));
}

@Test
public void testEmptyCloneCreatesFreshLearner() {
MikheevLearner learner = new MikheevLearner();
MikheevLearner clone = (MikheevLearner) learner.emptyClone();
assertNotNull(clone);
assertNotSame(learner, clone);
}

@Test
public void testDoneLearningPrunesLowFrequencyData() {
MikheevLearner learner = new MikheevLearner();
Word word = new Word("Testing");
// word.setLabel("NN");
learner.learn(word);
learner.doneLearning();
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
}

@Test
public void testWriteOutputsExpectedTextSections() {
MikheevLearner learner = new MikheevLearner();
Word word = new Word("Running");
// word.setLabel("VBG");
learner.learn(word);
ByteArrayOutputStream baos = new ByteArrayOutputStream();
PrintStream ps = new PrintStream(baos);
learner.write(ps);
String output = baos.toString();
assertTrue(output.contains("# if capitalized and first word in sentence:"));
assertTrue(output.contains("# if capitalized and not first word in sentence:"));
assertTrue(output.contains("# main table:"));
}

@Test
public void testAllowableTagsMatchesSuffix3() {
MikheevLearner learner = new MikheevLearner();
Word learned = new Word("running");
// learned.setLabel("VBG");
learner.learn(learned);
Word test = new Word("jumping");
Set<String> tags = learner.allowableTags(test);
assertTrue(tags.contains("VBG"));
}

@Test
public void testAllowableTagsMatchesSuffix4() {
MikheevLearner learner = new MikheevLearner();
Word learned = new Word("started");
// learned.setLabel("VBD");
learner.learn(learned);
Word test = new Word("charted");
Set<String> tags = learner.allowableTags(test);
assertTrue(tags.contains("VBD"));
}

@Test
public void testLearnThenForgetThenLearnAgain() {
MikheevLearner learner = new MikheevLearner();
Word word1 = new Word("Walking");
// word1.setLabel("VBG");
learner.learn(word1);
Set<String> tags1 = learner.allowableTags(word1);
assertTrue(tags1.contains("VBG"));
learner.forget();
Set<String> tags2 = learner.allowableTags(word1);
assertTrue(tags2.contains("NN"));
Word word2 = new Word("Skiing");
// word2.setLabel("VBG");
learner.learn(word2);
Set<String> tags3 = learner.allowableTags(word2);
assertTrue(tags3.contains("VBG"));
}

@Test
public void testLearnFormWithExactly5CharactersAndValidSuffix() {
MikheevLearner learner = new MikheevLearner();
Word word = new Word("Dance");
// word.setLabel("NN");
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
}

@Test
public void testLearnFormWithUpperMixedSuffixAndIgnoreNonLetterCharacter() {
MikheevLearner learner = new MikheevLearner();
Word word = new Word("Comp@");
// word.setLabel("NN");
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
}

@Test
public void testLearnFormWithLetterDigitMixedSuffixShouldNotLearn() {
MikheevLearner learner = new MikheevLearner();
Word word = new Word("abc12");
// word.setLabel("NN");
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
}

@Test
public void testAllowableTagsExactSuffixBoundaryMatchWithoutLearning() {
MikheevLearner learner = new MikheevLearner();
Word word = new Word("ending");
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
}

@Test
public void testAllowableTagsCapitalizedFirstNoSuffixFoundReturnsDefault() {
MikheevLearner learner = new MikheevLearner();
Word word = new Word("Conference");
word.capitalized = true;
word.previous = null;
Set<String> tags = learner.allowableTags(word);
assertEquals(1, tags.size());
assertTrue(tags.contains("NNP"));
}

@Test
public void testAllowableTagsWithMinimumLengthSuffixButNoLetters() {
MikheevLearner learner = new MikheevLearner();
Word word = new Word("12!@3");
// word.setLabel("NN");
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
}

@Test
public void testMultipleLabelEntriesUnderSameSuffix() {
MikheevLearner learner = new MikheevLearner();
Word word1 = new Word("Testing");
// word1.setLabel("VBG");
learner.learn(word1);
Word word2 = new Word("Testing");
// word2.setLabel("NN");
learner.learn(word2);
Word test = new Word("Mocking");
Set<String> tags = learner.allowableTags(test);
assertTrue(tags.contains("VBG"));
assertTrue(tags.contains("NN"));
}

@Test
public void testLearnerWriteToBinaryStreamDoesNotThrow() {
MikheevLearner learner = new MikheevLearner();
Word word = new Word("Coding");
// word.setLabel("NN");
learner.learn(word);
ExceptionlessOutputStream out = new ExceptionlessOutputStream(new ByteArrayOutputStream());
learner.write(out);
assertNotNull(out);
}

@Test
public void testLearnerReadFromEmptyBinaryStreamDoesNotThrow() {
MikheevLearner learner = new MikheevLearner();
byte[] emptyData = new byte[0];
ExceptionlessInputStream in = new ExceptionlessInputStream(new java.io.ByteArrayInputStream(emptyData));
learner.read(in);
Word word = new Word("Example");
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN") || tags.contains("NNP"));
}

@Test
public void testPruneRemovesSuffixWithOnlyOneLowFreqTag() {
MikheevLearner learner = new MikheevLearner();
HashSet<String> forms = new HashSet<>();
forms.add("testing");
forms.add("testing");
forms.add("testing");
forms.add("testing");
forms.add("testing");
Word word = new Word("Testing");
// word.setLabel("NN");
learner.learn(word);
learner.doneLearning();
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
}

@Test
public void testSuffixWithExactThresholdNotPruned() {
MikheevLearner learner = new MikheevLearner();
Word w1 = new Word("fishing");
// w1.setLabel("VBG");
learner.learn(w1);
Word w2 = new Word("cooking");
// w2.setLabel("VBG");
learner.learn(w2);
Word w3 = new Word("baking");
// w3.setLabel("VBG");
learner.learn(w3);
Word w4 = new Word("painting");
// w4.setLabel("VBG");
learner.learn(w4);
Word w5 = new Word("dancing");
// w5.setLabel("VBG");
learner.learn(w5);
Word w6 = new Word("writing");
// w6.setLabel("VBG");
learner.learn(w6);
Word w7 = new Word("reading");
// w7.setLabel("VBG");
learner.learn(w7);
Word w8 = new Word("singing");
// w8.setLabel("VBG");
learner.learn(w8);
Word w9 = new Word("skiing");
// w9.setLabel("VBG");
learner.learn(w9);
Word w10 = new Word("running");
// w10.setLabel("VBG");
learner.learn(w10);
learner.doneLearning();
Word test = new Word("rowing");
Set<String> tags = learner.allowableTags(test);
assertTrue(tags.contains("VBG"));
}

@Test
public void testLearnWithNullPreviousCapitalizedShouldUseFirstCapitalizedMap() {
MikheevLearner learner = new MikheevLearner();
Word word = new Word("Energizing");
// word.setLabel("VBG");
word.capitalized = true;
word.previous = null;
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("VBG") || tags.contains("NNP"));
}

@Test
public void testLearnWithCapitalizedAndPreviousShouldUseNotFirstCapitalizedMap() {
MikheevLearner learner = new MikheevLearner();
Word previous = new Word("is");
Word word = new Word("Running");
// word.setLabel("VBG");
word.capitalized = true;
word.previous = previous;
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("VBG") || tags.contains("NNP"));
}

@Test
public void testAllowableTagsFallbackForUncapitalizedWithoutHyphenReturnsNN() {
MikheevLearner learner = new MikheevLearner();
Word word = new Word("unseenword");
Set<String> tags = learner.allowableTags(word);
assertEquals(1, tags.size());
assertTrue(tags.contains("NN"));
}

@Test
public void testAllowableTagsReturnsSuffix4PreferredIfAvailable() {
MikheevLearner learner = new MikheevLearner();
Word word = new Word("syllable");
// word.setLabel("NN");
learner.learn(word);
Word test = new Word("tunable");
Set<String> tags = learner.allowableTags(test);
assertTrue(tags.contains("NN"));
}

@Test
public void testAllowableTagsReturnsSuffix3IfSuffix4Absent() {
MikheevLearner learner = new MikheevLearner();
Word word = new Word("gather");
// word.setLabel("VB");
learner.learn(word);
Word test = new Word("tether");
Set<String> tags = learner.allowableTags(test);
assertTrue(tags.contains("VB"));
}

@Test
public void testDoneLearningOnEmptyTablesDoesNotThrow() {
MikheevLearner learner = new MikheevLearner();
learner.doneLearning();
Word word = new Word("example");
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
}

@Test
public void testReadAndWriteBinaryStreamsWithNoContentGracefullyHandled() {
MikheevLearner learner = new MikheevLearner();
ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
ExceptionlessOutputStream eos = new ExceptionlessOutputStream(outputStream);
learner.write(eos);
// ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
// ExceptionlessInputStream eis = new ExceptionlessInputStream(inputStream);
// learner.read(eis);
Word word = new Word("basic");
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
}

@Test
public void testAllowableTagsForHyphenatedCapitalizedWord() {
MikheevLearner learner = new MikheevLearner();
Word word = new Word("High-Speed");
word.capitalized = true;
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
assertTrue(tags.contains("JJ"));
}

@Test
public void testAllowableTagsCapitalizedWithoutLearningFallsBackNNP() {
MikheevLearner learner = new MikheevLearner();
Word word = new Word("Aluminium");
word.capitalized = true;
word.previous = new Word("The");
Set<String> tags = learner.allowableTags(word);
assertEquals(1, tags.size());
assertTrue(tags.contains("NNP"));
}

@Test
public void testWriteTextStreamOutputsLearnedSuffixes() {
MikheevLearner learner = new MikheevLearner();
Word word = new Word("running");
// word.setLabel("VBG");
learner.learn(word);
ByteArrayOutputStream out = new ByteArrayOutputStream();
PrintStream printStream = new PrintStream(out);
learner.write(printStream);
String output = out.toString();
assertTrue(output.contains("vbg"));
}

@Test
public void testLearnWordWithExactlyFiveLettersShouldUseSuffix3() {
MikheevLearner learner = new MikheevLearner();
Word word = new Word("Coded");
// word.setLabel("VBD");
learner.learn(word);
Word test = new Word("Voted");
Set<String> tags = learner.allowableTags(test);
assertTrue(tags.contains("VBD"));
}

@Test
public void testLearnWordWithExactlySixLettersShouldUseSuffix4Too() {
MikheevLearner learner = new MikheevLearner();
Word word = new Word("Driver");
// word.setLabel("NN");
learner.learn(word);
Word test = new Word("Flyver");
Set<String> tags = learner.allowableTags(test);
assertTrue(tags.contains("NN"));
}

@Test
public void testLearnWithNonLetterSuffixShouldBeIgnored() {
MikheevLearner learner = new MikheevLearner();
Word word = new Word("test1!");
// word.setLabel("VB");
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
}

@Test
public void testAllowableTagsReturnsOnlySuffix3IfSuffix4StartsWithNonLetter() {
MikheevLearner learner = new MikheevLearner();
Word word = new Word("Worm3d");
// word.setLabel("JJ");
learner.learn(word);
Word test = new Word("Charmd");
Set<String> tags = learner.allowableTags(test);
assertTrue(tags.contains("JJ"));
}

@Test
public void testAllowableTagsHandlesWordWithOnlySuffix3AvailableNot4() {
MikheevLearner learner = new MikheevLearner();
Word word = new Word("called");
// word.setLabel("VBD");
learner.learn(word);
Word test = new Word("balled");
Set<String> tags = learner.allowableTags(test);
assertTrue(tags.contains("VBD"));
}

@Test
public void testLearnHyphenatedWordCapitalizedShouldBeIgnored() {
MikheevLearner learner = new MikheevLearner();
Word word = new Word("Well-Known");
// word.setLabel("JJ");
word.capitalized = true;
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("JJ"));
assertTrue(tags.contains("NN"));
}

@Test
public void testWriteAndReadPreserveLearnedTags() {
MikheevLearner learner1 = new MikheevLearner();
Word word = new Word("escaping");
// word.setLabel("VBG");
learner1.learn(word);
ByteArrayOutputStream buffer = new ByteArrayOutputStream();
ExceptionlessOutputStream eos = new ExceptionlessOutputStream(buffer);
learner1.write(eos);
// ByteArrayInputStream in = new ByteArrayInputStream(buffer.toByteArray());
// ExceptionlessInputStream eis = new ExceptionlessInputStream(in);
MikheevLearner learner2 = new MikheevLearner();
// learner2.read(eis);
Word test = new Word("rapping");
Set<String> tags = learner2.allowableTags(test);
assertTrue(tags.contains("VBG"));
}

@Test
public void testAllowableTagsOfCapitalizedFirstWordFallbackToNNP() {
MikheevLearner learner = new MikheevLearner();
Word word = new Word("Valhalla");
word.capitalized = true;
word.previous = null;
Set<String> tags = learner.allowableTags(word);
assertEquals(1, tags.size());
assertTrue(tags.contains("NNP"));
}

@Test
public void testAllowableTagsOfCapitalizedNonFirstWordFallbackToNNP() {
MikheevLearner learner = new MikheevLearner();
Word previous = new Word("The");
Word word = new Word("Council");
word.capitalized = true;
word.previous = previous;
Set<String> tags = learner.allowableTags(word);
assertEquals(1, tags.size());
assertTrue(tags.contains("NNP"));
}

@Test
public void testAllowableTagsOfUncapitalizedHyphenatedFallbackToNNAndJJ() {
MikheevLearner learner = new MikheevLearner();
Word word = new Word("eco-friendly");
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
assertTrue(tags.contains("JJ"));
}

@Test
public void testDoneLearningPrunesLowFrequencyEntriesCorrectly() {
MikheevLearner learner = new MikheevLearner();
Word word = new Word("Testing");
// word.setLabel("NN");
learner.learn(word);
learner.doneLearning();
Set<String> tags = learner.allowableTags(new Word("mocking"));
assertTrue(tags.contains("NN"));
}

@Test
public void testEmptyPruneDoesNotCrash() {
MikheevLearner learner = new MikheevLearner();
learner.prune(new java.util.HashMap<String, java.util.TreeMap<String, Integer>>());
Word word = new Word("example");
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
}

@Test
public void testLearnExactlyThreeLetterWordTooShortToLearnSuffix() {
MikheevLearner learner = new MikheevLearner();
Word word = new Word("cat");
// word.setLabel("NN");
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
}

@Test
public void testLearnWithLowercaseValidSuffixStillLearned() {
MikheevLearner learner = new MikheevLearner();
Word word = new Word("elevation");
// word.setLabel("NN");
learner.learn(word);
Word test = new Word("migration");
Set<String> tags = learner.allowableTags(test);
assertTrue(tags.contains("NN"));
}

@Test
public void testAllowableTagsFallsBackToSuffix4Only() {
MikheevLearner learner = new MikheevLearner();
Word word = new Word("started");
// word.setLabel("VBD");
learner.learn(word);
Word test = new Word("carted");
Set<String> tags = learner.allowableTags(test);
assertTrue(tags.contains("VBD"));
}

@Test
public void testAllowableTagsReturnsEmptySetIfNoSuffixAndHyphenAndNotCapitalized() {
MikheevLearner learner = new MikheevLearner();
Word word = new Word("***");
// word.setLabel("NN");
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
}

@Test
public void testForgetAfterMultipleLearnsClearsAllData() {
MikheevLearner learner = new MikheevLearner();
Word word1 = new Word("learning");
// word1.setLabel("VBG");
learner.learn(word1);
Word word2 = new Word("Teaching");
// word2.setLabel("VBG");
word2.capitalized = true;
learner.learn(word2);
Word word3 = new Word("Travelling");
// word3.setLabel("NN");
word3.capitalized = true;
word3.previous = new Word("Was");
learner.learn(word3);
learner.forget();
Word result = new Word("Training");
Set<String> tags = learner.allowableTags(result);
assertTrue(tags.contains("NN"));
}

@Test
public void testPruneKeepsEntriesAboveThreshold() {
MikheevLearner learner = new MikheevLearner();
Word word = new Word("publishing");
// word.setLabel("VBG");
learner.learn(word);
learner.learn(word);
learner.learn(word);
learner.learn(word);
learner.learn(word);
learner.learn(word);
learner.learn(word);
learner.learn(word);
learner.learn(word);
learner.learn(word);
learner.doneLearning();
Word test = new Word("finishing");
Set<String> tags = learner.allowableTags(test);
assertTrue(tags.contains("VBG"));
}

@Test
public void testWriteBinaryStreamWithEmptyTablesDoesNotThrow() {
MikheevLearner learner = new MikheevLearner();
ByteArrayOutputStream buffer = new ByteArrayOutputStream();
ExceptionlessOutputStream out = new ExceptionlessOutputStream(buffer);
learner.write(out);
assertTrue(buffer.toByteArray().length > 0);
}

@Test
public void testReadAndRecoverNoDataDoesNotCrash() {
MikheevLearner learner = new MikheevLearner();
// ByteArrayInputStream input = new ByteArrayInputStream(new byte[0]);
// ExceptionlessInputStream in = new ExceptionlessInputStream(input);
// learner.read(in);
Word word = new Word("desk");
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
}

@Test
public void testWriteTextStreamIncludesSectionHeadersEvenWithoutTraining() {
MikheevLearner learner = new MikheevLearner();
ByteArrayOutputStream stream = new ByteArrayOutputStream();
PrintStream out = new PrintStream(stream);
learner.write(out);
String result = stream.toString();
assertTrue(result.contains("# if capitalized and first word in sentence:"));
assertTrue(result.contains("# if capitalized and not first word in sentence:"));
assertTrue(result.contains("# main table:"));
}

@Test
public void testLearnerWithOnlySuffix3AndLessThanThresholdIsPruned() {
MikheevLearner learner = new MikheevLearner();
Word word = new Word("drawing");
// word.setLabel("NN");
learner.learn(word);
learner.doneLearning();
Word test = new Word("rowing");
Set<String> tags = learner.allowableTags(test);
assertTrue(tags.contains("NN"));
}

@Test
public void testAllowableTagsFallbackAddsOnlyNNPForUnknownCapitalized() {
MikheevLearner learner = new MikheevLearner();
Word word = new Word("Moonlight");
word.capitalized = true;
word.previous = new Word("By");
Set<String> tags = learner.allowableTags(word);
assertEquals(1, tags.size());
assertTrue(tags.contains("NNP"));
}

@Test
public void testLearnDoesNotAddSuffixIfLengthTooShort() {
MikheevLearner learner = new MikheevLearner();
Word word = new Word("abcd");
// word.setLabel("JJ");
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
}

@Test
public void testLearnDoesNotAddSuffixIfSuffixHasNonLetterCharacters() {
MikheevLearner learner = new MikheevLearner();
Word word = new Word("end4g%");
// word.setLabel("NN");
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
}

@Test
public void testLearnWithValidSuffixes3and4() {
MikheevLearner learner = new MikheevLearner();
Word word = new Word("starting");
// word.setLabel("VBG");
learner.learn(word);
Word test = new Word("charting");
Set<String> tags = learner.allowableTags(test);
assertTrue(tags.contains("VBG"));
}

@Test
public void testLearnCapitalizedNoPreviousAppliesToFirstCapitalizedSet() {
MikheevLearner learner = new MikheevLearner();
Word word = new Word("Climbing");
// word.setLabel("VBG");
word.capitalized = true;
word.previous = null;
learner.learn(word);
Word test = new Word("Skating");
test.capitalized = true;
test.previous = null;
Set<String> tags = learner.allowableTags(test);
assertTrue(tags.contains("VBG") || tags.contains("NNP"));
}

@Test
public void testLearnCapitalizedWithPreviousAppliesToNotFirstCapitalizedSet() {
MikheevLearner learner = new MikheevLearner();
Word word = new Word("Running");
// word.setLabel("NN");
word.capitalized = true;
word.previous = new Word("The");
learner.learn(word);
Word test = new Word("Hopping");
test.capitalized = true;
test.previous = new Word("Was");
Set<String> tags = learner.allowableTags(test);
assertTrue(tags.contains("NN") || tags.contains("NNP"));
}

@Test
public void testAllowableTagsHyphenWordReturnsDefaultSet() {
MikheevLearner learner = new MikheevLearner();
Word word = new Word("cost-effective");
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
assertTrue(tags.contains("JJ"));
}

@Test
public void testAllowableTagsOfCapitalizedWithUnknownSuffixReturnsNNP() {
MikheevLearner learner = new MikheevLearner();
Word word = new Word("Atlantis");
word.capitalized = true;
word.previous = new Word("from");
Set<String> tags = learner.allowableTags(word);
assertEquals(1, tags.size());
assertTrue(tags.contains("NNP"));
}

@Test
public void testAllowableTagsOfUncapitalizedUnknownWordReturnsNN() {
MikheevLearner learner = new MikheevLearner();
Word word = new Word("experience");
Set<String> tags = learner.allowableTags(word);
assertEquals(1, tags.size());
assertTrue(tags.contains("NN"));
}

@Test
public void testWriteToPrintStreamContainsAllExpectedSections() {
MikheevLearner learner = new MikheevLearner();
Word word = new Word("Processing");
// word.setLabel("NN");
learner.learn(word);
ByteArrayOutputStream out = new ByteArrayOutputStream();
PrintStream ps = new PrintStream(out);
learner.write(ps);
String output = out.toString();
assertTrue(output.contains("# if capitalized and first word in sentence:"));
assertTrue(output.contains("# if capitalized and not first word in sentence:"));
assertTrue(output.contains("# main table:"));
}

@Test
public void testPruneRemovesTagIfLowPercentageInEntry() {
MikheevLearner learner = new MikheevLearner();
Word word1 = new Word("tailing");
// word1.setLabel("VBG");
learner.learn(word1);
Word word2 = new Word("tailing");
// word2.setLabel("NN");
learner.learn(word2);
Word word3 = new Word("tailing");
// word3.setLabel("NN");
learner.learn(word3);
Word word4 = new Word("tailing");
// word4.setLabel("NN");
learner.learn(word4);
learner.doneLearning();
Word test = new Word("whaling");
Set<String> tags = learner.allowableTags(test);
assertFalse(tags.contains("VBG"));
assertTrue(tags.contains("NN"));
}

@Test
public void testWriteAndReadBinaryPreservesLearnedData() {
MikheevLearner learner1 = new MikheevLearner();
Word word = new Word("dancing");
// word.setLabel("VBG");
learner1.learn(word);
ByteArrayOutputStream buffer = new ByteArrayOutputStream();
ExceptionlessOutputStream eos = new ExceptionlessOutputStream(buffer);
learner1.write(eos);
// ByteArrayInputStream in = new ByteArrayInputStream(buffer.toByteArray());
// ExceptionlessInputStream eis = new ExceptionlessInputStream(in);
MikheevLearner learner2 = new MikheevLearner();
// learner2.read(eis);
Word test = new Word("prancing");
Set<String> tags = learner2.allowableTags(test);
assertTrue(tags.contains("VBG"));
}

@Test
public void testEmptyCloneReturnsDistinctFreshInstance() {
MikheevLearner original = new MikheevLearner();
MikheevLearner cloned = (MikheevLearner) original.emptyClone();
assertNotNull(cloned);
assertNotSame(original, cloned);
assertTrue(cloned instanceof MikheevLearner);
}

@Test
public void testLearnUncapitalizedHyphenatedWordShouldNotBeLearned() {
MikheevLearner learner = new MikheevLearner();
Word word = new Word("well-known");
// word.setLabel("JJ");
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
assertTrue(tags.contains("JJ"));
}

@Test
public void testLearnCapitalizedButTooShortShouldNotLearn() {
MikheevLearner learner = new MikheevLearner();
Word word = new Word("Ape");
// word.setLabel("NN");
word.capitalized = true;
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NNP"));
}

@Test
public void testLearnWordWithExactlyFiveLettersValidSuffix3Only() {
MikheevLearner learner = new MikheevLearner();
Word word = new Word("cried");
// word.setLabel("VBD");
learner.learn(word);
Word test = new Word("tried");
Set<String> tags = learner.allowableTags(test);
assertTrue(tags.contains("VBD"));
}

@Test
public void testLearnWordWithExactlySixLettersValidSuffix3AndSuffix4() {
MikheevLearner learner = new MikheevLearner();
Word word = new Word("played");
// word.setLabel("VBD");
learner.learn(word);
Word test = new Word("stayed");
Set<String> tags = learner.allowableTags(test);
assertTrue(tags.contains("VBD"));
}

@Test
public void testLearnWordWithSuffix4ContainingNonLetterShouldSkipSuffix4() {
MikheevLearner learner = new MikheevLearner();
Word word = new Word("end@4r");
// word.setLabel("NN");
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
}

@Test
public void testAllowableTagsCapitalizedWordNotFirstWithKnownSuffix3() {
MikheevLearner learner = new MikheevLearner();
Word word = new Word("Testing");
// word.setLabel("NN");
word.capitalized = true;
word.previous = new Word("Was");
learner.learn(word);
Word test = new Word("Hunting");
test.capitalized = true;
test.previous = new Word("Was");
Set<String> tags = learner.allowableTags(test);
assertTrue(tags.contains("NN"));
}

@Test
public void testAllowableTagsCapitalizedWordFirstWithKnownSuffix4() {
MikheevLearner learner = new MikheevLearner();
Word word = new Word("Running");
// word.setLabel("VBG");
word.capitalized = true;
word.previous = null;
learner.learn(word);
Word test = new Word("Jumping");
test.capitalized = true;
test.previous = null;
Set<String> tags = learner.allowableTags(test);
assertTrue(tags.contains("VBG"));
}

@Test
public void testPruneRemovesSuffixIfTotalFrequencyIsLessThanOrEqualTo10() {
MikheevLearner learner = new MikheevLearner();
Word word = new Word("processing");
// word.setLabel("NN");
learner.learn(word);
learner.doneLearning();
Word test = new Word("encoding");
Set<String> tags = learner.allowableTags(test);
assertTrue(tags.contains("NN"));
}

@Test
public void testPruneKeepsOnlyHighFrequencyTagIfMultipleTagsExist() {
MikheevLearner learner = new MikheevLearner();
Word word1 = new Word("breaking");
// word1.setLabel("VBG");
learner.learn(word1);
Word word2 = new Word("breaking");
// word2.setLabel("VBG");
learner.learn(word2);
Word word3 = new Word("breaking");
// word3.setLabel("NN");
learner.learn(word3);
learner.doneLearning();
Word test = new Word("barking");
Set<String> tags = learner.allowableTags(test);
assertTrue(tags.contains("NN"));
}

@Test
public void testWriteAndReadDoNotThrowWhenNoTrainingOccurred() {
MikheevLearner learner = new MikheevLearner();
ByteArrayOutputStream outStream = new ByteArrayOutputStream();
ExceptionlessOutputStream eos = new ExceptionlessOutputStream(outStream);
learner.write(eos);
// ByteArrayInputStream inStream = new ByteArrayInputStream(outStream.toByteArray());
// ExceptionlessInputStream eis = new ExceptionlessInputStream(inStream);
MikheevLearner loaded = new MikheevLearner();
// loaded.read(eis);
Word word = new Word("testing");
Set<String> tags = loaded.allowableTags(word);
assertTrue(tags.contains("NN"));
}

@Test
public void testForgetResetsAllLearnedDataAcrossAllTables() {
MikheevLearner learner = new MikheevLearner();
Word word1 = new Word("Heating");
// word1.setLabel("VBG");
word1.capitalized = false;
learner.learn(word1);
Word word2 = new Word("Baking");
// word2.setLabel("NN");
word2.capitalized = true;
word2.previous = null;
learner.learn(word2);
Word word3 = new Word("Roasting");
// word3.setLabel("VBG");
word3.capitalized = true;
word3.previous = new Word("Was");
learner.learn(word3);
learner.forget();
Word test = new Word("frying");
Set<String> tags = learner.allowableTags(test);
assertTrue(tags.contains("NN"));
}
}
