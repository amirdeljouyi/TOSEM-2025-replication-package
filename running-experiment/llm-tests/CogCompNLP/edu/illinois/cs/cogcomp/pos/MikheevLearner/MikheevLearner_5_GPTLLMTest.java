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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class MikheevLearner_5_GPTLLMTest {

@Test
public void testConstructorInitializesLearner() {
MikheevLearner learner = new MikheevLearner("classifierName");
assertNotNull(learner);
}

@Test
public void testEmptyCloneReturnsNewInstance() {
MikheevLearner learner = new MikheevLearner("original");
MikheevLearner cloned = (MikheevLearner) learner.emptyClone();
assertNotNull(cloned);
assertNotSame(learner, cloned);
}

@Test
public void testForgetClearsLearnedData() {
MikheevLearner learner = new MikheevLearner("forgetTest");
Word word = new Word("learningword");
word.label = "NN";
learner.learn(word);
learner.forget();
Word newWord = new Word("learningword");
Set<String> tags = learner.allowableTags(newWord);
assertTrue(tags.contains("NN"));
}

@Test
public void testLearnUncapitalizedNoHyphen() {
MikheevLearner learner = new MikheevLearner("basicLearn");
Word word = new Word("walking");
word.label = "VBG";
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("VBG"));
}

@Test
public void testLearnCapitalizedFirstWord() {
MikheevLearner learner = new MikheevLearner("capFirst");
Word word = new Word("Running");
word.label = "NN";
word.capitalized = true;
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
}

@Test
public void testLearnCapitalizedNotFirstWord() {
MikheevLearner learner = new MikheevLearner("capNotFirst");
Word prev = new Word("was");
Word current = new Word("Running");
current.label = "VBG";
current.capitalized = true;
current.previous = prev;
learner.learn(current);
Set<String> tags = learner.allowableTags(current);
assertTrue(tags.contains("VBG"));
}

@Test
public void testLearnShortWordDoesNothing() {
MikheevLearner learner = new MikheevLearner("shortWord");
Word word = new Word("do");
word.label = "VB";
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
}

@Test
public void testLearnHyphenatedWordIsIgnored() {
MikheevLearner learner = new MikheevLearner("hyphenLearn");
Word word = new Word("well-known");
word.label = "JJ";
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
assertTrue(tags.contains("JJ"));
}

@Test
public void testAllowableTagsWhenSuffixIsUnseenAndCapitalized() {
MikheevLearner learner = new MikheevLearner("fallbackNNP");
Word word = new Word("Foobar");
word.capitalized = true;
Set<String> tags = learner.allowableTags(word);
assertEquals(1, tags.size());
assertTrue(tags.contains("NNP"));
}

@Test
public void testAllowableTagsWhenSuffixIsUnseenAndUncapitalized() {
MikheevLearner learner = new MikheevLearner("fallbackNN");
Word word = new Word("foobar");
word.capitalized = false;
Set<String> tags = learner.allowableTags(word);
assertEquals(1, tags.size());
assertTrue(tags.contains("NN"));
}

@Test
public void testAllowableTagsForHyphenatedWord() {
MikheevLearner learner = new MikheevLearner("hyphenTag");
Word word = new Word("self-made");
word.capitalized = false;
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("JJ"));
assertTrue(tags.contains("NN"));
}

@Test
public void testDoneLearningWithLowFrequencyPruning() {
MikheevLearner learner = new MikheevLearner("pruneTest");
Word word1 = new Word("broadcasting");
word1.capitalized = false;
word1.label = "NN";
Word word2 = new Word("broadcasting");
word2.capitalized = false;
word2.label = "VBG";
learner.learn(word1);
learner.learn(word2);
for (int i = 0; i < 9; i++) {
Word extra = new Word("broadcasting");
extra.capitalized = false;
extra.label = "NN";
learner.learn(extra);
}
learner.doneLearning();
Word test = new Word("broadcasting");
Set<String> tags = learner.allowableTags(test);
assertTrue(tags.contains("NN"));
assertFalse(tags.contains("VBG"));
}

@Test
public void testDoneLearningRemovesEntriesWithLowTotalCount() {
MikheevLearner learner = new MikheevLearner("removeLowTotal");
Word word = new Word("scripting");
word.capitalized = false;
word.label = "VBG";
learner.learn(word);
learner.doneLearning();
Word test = new Word("scripting");
Set<String> tags = learner.allowableTags(test);
assertTrue(tags.contains("NN"));
assertFalse(tags.contains("VBG"));
}

@Test
public void testWritePrintStreamOutputsExpectedFormat() {
MikheevLearner learner = new MikheevLearner("writeTest");
Word word1 = new Word("Quiz");
word1.capitalized = true;
word1.label = "NN";
Word word2 = new Word("playing");
word2.capitalized = false;
word2.label = "VBG";
learner.learn(word1);
learner.learn(word2);
ByteArrayOutputStream bos = new ByteArrayOutputStream();
PrintStream ps = new PrintStream(bos);
learner.write(ps);
String output = bos.toString();
assertTrue(output.contains("# if capitalized and first word in sentence:"));
assertTrue(output.contains("# main table:"));
}

@Test
public void testAllowableTagsFallbackToSuffixOfLength4() {
MikheevLearner learner = new MikheevLearner("suffix4");
Word word = new Word("amazingly");
word.label = "RB";
learner.learn(word);
Word query = new Word("amazingly");
Set<String> tags = learner.allowableTags(query);
assertTrue(tags.contains("RB"));
}

@Test
public void testLearnOnly3LetterSuffixWhen4thLastIsNotLetter() {
MikheevLearner learner = new MikheevLearner("nonalpha_suffix");
Word word = new Word("hi!ing");
word.label = "SYM";
learner.learn(word);
Word query = new Word("hi!ing");
Set<String> tags = learner.allowableTags(query);
assertTrue(tags.contains("SYM"));
}

@Test
public void testLearnWhenSuffixRepeatsWithDifferentTags() {
MikheevLearner learner = new MikheevLearner("conflicting_suffix");
Word word1 = new Word("declining");
word1.label = "VBG";
learner.learn(word1);
Word word2 = new Word("defining");
word2.label = "NN";
learner.learn(word2);
Word query = new Word("combining");
Set<String> tags = learner.allowableTags(query);
assertTrue(tags.contains("VBG") || tags.contains("NN"));
}

@Test
public void testLearnDoesNotIncrementWhenNonLetterSuffix() {
MikheevLearner learner = new MikheevLearner("nonletter_suffix");
Word word = new Word("start99");
word.label = "CD";
learner.learn(word);
Word query = new Word("start99");
Set<String> tags = learner.allowableTags(query);
assertTrue(tags.contains("NN"));
}

@Test
public void testDoneLearningPrunesLowFrequencyInCapitalizedMap() {
MikheevLearner learner = new MikheevLearner("cap_prune");
Word word = new Word("Motoring");
word.capitalized = true;
word.label = "NN";
for (int i = 0; i < 5; i++) {
learner.learn(word);
}
learner.doneLearning();
Word query = new Word("Motoring");
query.capitalized = true;
Set<String> tags = learner.allowableTags(query);
assertTrue(tags.contains("NNP"));
}

@Test
public void testAllowableTagsWithExactLengthBoundary() {
MikheevLearner learner = new MikheevLearner("exact_boundary");
Word word = new Word("swims");
word.label = "VBZ";
learner.learn(word);
Word query = new Word("swims");
Set<String> result = learner.allowableTags(query);
assertTrue(result.contains("VBZ"));
}

@Test
public void testAllowableTagsIsCaseInsensitiveInSuffixMatch() {
MikheevLearner learner = new MikheevLearner("case_insensitive");
Word word = new Word("Dancing");
word.capitalized = true;
word.label = "VBG";
learner.learn(word);
Word query = new Word("dancing");
query.capitalized = false;
Set<String> tags = learner.allowableTags(query);
assertTrue(tags.contains("VBG") || tags.contains("NN"));
}

@Test
public void testPruneKeepsFrequentTagOnly() {
MikheevLearner learner = new MikheevLearner("prune_majority");
Word word1 = new Word("repeating");
word1.label = "VBG";
for (int i = 0; i < 20; i++) {
learner.learn(word1);
}
Word word2 = new Word("repeating");
word2.label = "JJ";
learner.learn(word2);
learner.doneLearning();
Word query = new Word("repeating");
Set<String> tags = learner.allowableTags(query);
assertTrue(tags.contains("VBG"));
assertFalse(tags.contains("JJ"));
}

@Test
public void testWriteBinaryAndReadNotCrash() {
MikheevLearner learner = new MikheevLearner("write_binary_test");
Word word = new Word("Walking");
word.capitalized = true;
word.label = "NN";
learner.learn(word);
ByteArrayOutputStream buffer = new ByteArrayOutputStream();
ExceptionlessOutputStream exOut = new ExceptionlessOutputStream(buffer);
learner.write(exOut);
ByteArrayInputStream input = new ByteArrayInputStream(buffer.toByteArray());
ExceptionlessInputStream exIn = new ExceptionlessInputStream(input);
MikheevLearner restored = new MikheevLearner("restored_learner");
restored.read(exIn);
Word query = new Word("Walking");
query.capitalized = true;
Set<String> tags = restored.allowableTags(query);
assertTrue(tags.contains("NN") || tags.contains("NNP"));
}

@Test
public void testPruneEmptyTableDoesNothing() {
MikheevLearner learner = new MikheevLearner("empty_prune");
HashMap<String, TreeMap<String, Integer>> dummy = new HashMap<String, TreeMap<String, Integer>>();
learner.prune(dummy);
assertTrue(dummy.isEmpty());
}

@Test
public void testAllowableTagsFallbackWhenAllSuffixesRemoved() {
MikheevLearner learner = new MikheevLearner("suffix_removed");
Word word = new Word("forming");
word.label = "VBG";
learner.learn(word);
learner.doneLearning();
Word query = new Word("forming");
Set<String> tags = learner.allowableTags(query);
assertTrue(tags.contains("NN"));
}

@Test
public void testConstructorWithParametersObject() {
MikheevLearner.Parameters parameters = new MikheevLearner.Parameters();
MikheevLearner learner = new MikheevLearner(parameters);
assertNotNull(learner);
}

@Test
public void testParametersCopyConstructor() {
MikheevLearner.Parameters original = new MikheevLearner.Parameters();
MikheevLearner.Parameters copy = new MikheevLearner.Parameters(original);
assertNotNull(copy);
assertNotSame(original, copy);
}

@Test
public void testLearnWithNullPreviousCapitalizedWordFallsIntoFirstCapitalizedMap() {
MikheevLearner learner = new MikheevLearner("firstCapTest");
Word word = new Word("Surprising");
word.capitalized = true;
word.previous = null;
word.label = "NN";
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
}

@Test
public void testLearnWithNonCapitalizedWordContainingDigitsIsIgnored() {
MikheevLearner learner = new MikheevLearner("digitSuffix");
Word word = new Word("word9s");
word.capitalized = false;
word.label = "CD";
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
}

@Test
public void testAllowableTagsReturnsOnlyMajorityTagAfterPrune() {
MikheevLearner learner = new MikheevLearner("majorityTag");
Word word1 = new Word("accepting");
word1.label = "VBG";
Word word2 = new Word("accepting");
word2.label = "NN";
for (int i = 0; i < 30; i++) {
learner.learn(word1);
}
for (int i = 0; i < 2; i++) {
learner.learn(word2);
}
learner.doneLearning();
Word query = new Word("accepting");
Set<String> tags = learner.allowableTags(query);
assertTrue(tags.contains("VBG"));
assertFalse(tags.contains("NN"));
}

@Test
public void testAllowableTagsFallbackWhenSuffixLengthIsInvalid() {
MikheevLearner learner = new MikheevLearner("shortInput");
Word word = new Word("cat");
word.capitalized = false;
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
}

@Test
public void testAllowableTagsWithCapitalizedWordUnseenSuffixFallsBackToNNP() {
MikheevLearner learner = new MikheevLearner("unseenCap");
Word word = new Word("Xyzophobe");
word.capitalized = true;
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NNP"));
}

@Test
public void testAllowableTagsWithLowercaseHyphenatedFallbacksToNNAndJJ() {
MikheevLearner learner = new MikheevLearner("hyphenCase");
Word word = new Word("blue-green");
word.capitalized = false;
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("JJ"));
assertTrue(tags.contains("NN"));
}

@Test
public void testLearnDoesNotThrowOnNonLetter4thCharAndStillAdds3CharSuffix() {
MikheevLearner learner = new MikheevLearner("mixedEnding");
Word word = new Word("chess1ng");
word.label = "VBG";
word.capitalized = false;
learner.learn(word);
Word query = new Word("chess1ng");
Set<String> tags = learner.allowableTags(query);
assertTrue(tags.contains("VBG") || tags.contains("NN"));
}

@Test
public void testWriteAndReadBinaryRetainsLearnedTags() {
MikheevLearner learner = new MikheevLearner("binaryIO");
Word w = new Word("Improving");
w.capitalized = true;
w.label = "NN";
learner.learn(w);
ByteArrayOutputStream out = new ByteArrayOutputStream();
ExceptionlessOutputStream eos = new ExceptionlessOutputStream(out);
learner.write(eos);
ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
ExceptionlessInputStream eis = new ExceptionlessInputStream(in);
MikheevLearner loaded = new MikheevLearner("binaryIORead");
loaded.read(eis);
Word query = new Word("Improving");
query.capitalized = true;
Set<String> tags = loaded.allowableTags(query);
assertTrue(tags.contains("NN") || tags.contains("NNP"));
}

@Test
public void testPruneRemovesSuffixMapWhenAllTagsAreBelowThreshold() {
MikheevLearner learner = new MikheevLearner("pruneRemoveAll");
Word word = new Word("arching");
word.label = "VB";
for (int i = 0; i < 5; i++) {
learner.learn(word);
}
learner.doneLearning();
Word query = new Word("arching");
Set<String> tags = learner.allowableTags(query);
assertTrue(tags.contains("NN"));
}

@Test
public void testAllowableTagsWithCapitalizedNotFirstFallback() {
MikheevLearner learner = new MikheevLearner("capNotFirstFallback");
Word prev = new Word("and");
Word word = new Word("Experts");
word.capitalized = true;
word.previous = prev;
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NNP"));
}

@Test
public void testLearnWithSixCharacterWordAddsOnly3CharSuffix() {
MikheevLearner learner = new MikheevLearner("suffixLength6");
Word word = new Word("circle");
word.label = "NN";
word.capitalized = false;
learner.learn(word);
Word query = new Word("circle");
Set<String> tags = learner.allowableTags(query);
assertTrue(tags.contains("NN"));
}

@Test
public void testLearnWithSevenCharacterWordAddsBothSuffixes() {
MikheevLearner learner = new MikheevLearner("suffixLength7");
Word word = new Word("cycling");
word.label = "VBG";
word.capitalized = false;
learner.learn(word);
Word query = new Word("cycling");
Set<String> tags = learner.allowableTags(query);
assertTrue(tags.contains("VBG"));
}

@Test
public void testReadWithEmptyStreamDoesNotCrash() {
MikheevLearner learner = new MikheevLearner("emptyStream");
ByteArrayInputStream in = new ByteArrayInputStream(new byte[0]);
ExceptionlessInputStream eis = new ExceptionlessInputStream(in);
try {
learner.read(eis);
assertTrue(true);
} catch (Exception e) {
fail("Read should not throw exception on empty input.");
}
}

@Test
public void testWriteTextualFormatProducesNonEmptyOutput() {
MikheevLearner learner = new MikheevLearner("textWrite");
Word word = new Word("Running");
word.label = "VBG";
word.capitalized = false;
learner.learn(word);
ByteArrayOutputStream out = new ByteArrayOutputStream();
PrintStream ps = new PrintStream(out);
learner.write(ps);
String output = out.toString();
assertNotNull(output);
assertTrue(output.contains("main table"));
}

@Test
public void testLearnWithNullExampleDoesNotThrow() {
MikheevLearner learner = new MikheevLearner("nullInput");
try {
learner.learn(Optional.ofNullable(null));
assertTrue(true);
} catch (Exception e) {
fail("Learning null should not throw exception.");
}
}

@Test
public void testLearnWithEmptyStringFormDoesNotAddSuffixes() {
MikheevLearner learner = new MikheevLearner("emptyString");
Word word = new Word("");
word.label = "NN";
word.capitalized = false;
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
}

@Test
public void testLearnWithWhitespaceOnlyFormDoesNotAddSuffixes() {
MikheevLearner learner = new MikheevLearner("whitespaceForm");
Word word = new Word("     ");
word.label = "JJ";
word.capitalized = false;
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
}

@Test
public void testAllowableTagsFromCapitalizedAndPreviousNotNullWithNoMatch() {
MikheevLearner learner = new MikheevLearner("noSuffixMatch");
Word prev = new Word("The");
Word word = new Word("Mountain");
word.capitalized = true;
word.previous = prev;
Set<String> tags = learner.allowableTags(word);
assertEquals(1, tags.size());
assertTrue(tags.contains("NNP"));
}

@Test
public void testAllowableTagsFallbackWhenSuffixMapsAreEmpty() {
MikheevLearner learner = new MikheevLearner("suffixEmpty");
Word word = new Word("talking");
word.capitalized = false;
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
}

@Test
public void testPruneRemovesEntryWithAllTagsBelowThreshold() {
MikheevLearner learner = new MikheevLearner("thresholdPrune");
Word word1 = new Word("replaying");
word1.label = "VB";
for (int i = 0; i < 5; i++) {
learner.learn(word1);
}
Word word2 = new Word("replaying");
word2.label = "NN";
for (int i = 0; i < 5; i++) {
learner.learn(word2);
}
learner.doneLearning();
Word query = new Word("replaying");
Set<String> tags = learner.allowableTags(query);
assertTrue(tags.contains("NN"));
}

@Test
public void testWriteHandlesEmptyStructures() {
MikheevLearner learner = new MikheevLearner("emptyWrite");
ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
PrintStream printStream = new PrintStream(byteStream);
learner.write(printStream);
String output = byteStream.toString();
assertTrue(output.contains("# main table:"));
}

@Test
public void testWriteBinaryHandlesEmptyStructures() {
MikheevLearner learner = new MikheevLearner("binaryEmpty");
ByteArrayOutputStream bos = new ByteArrayOutputStream();
ExceptionlessOutputStream eos = new ExceptionlessOutputStream(bos);
learner.write(eos);
assertTrue(bos.size() > 0);
}

@Test
public void testReadBinaryHandlesEmptyInputGracefully() {
MikheevLearner learner = new MikheevLearner("emptyBinaryRead");
byte[] emptyBytes = new byte[0];
ByteArrayInputStream bis = new ByteArrayInputStream(emptyBytes);
ExceptionlessInputStream eis = new ExceptionlessInputStream(bis);
try {
learner.read(eis);
assertTrue(true);
} catch (Exception e) {
fail("Reading empty stream should not throw exception.");
}
}

@Test
public void testLearnWithNonAlphabeticFinalSuffixCharactersIgnored() {
MikheevLearner learner = new MikheevLearner("mixedSuffix");
Word word = new Word("boxin9");
word.label = "NN";
word.capitalized = false;
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
}

@Test
public void testLearnWithCapitalizedAndHyphenatedWordIsIgnored() {
MikheevLearner learner = new MikheevLearner("capitalHyphen");
Word word = new Word("Well-Done");
word.label = "JJ";
word.capitalized = true;
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NNP"));
}

@Test
public void testAllowableTagsForCapitalized4CharSuffixOnlyMatch() {
MikheevLearner learner = new MikheevLearner("suffixMatch4");
Word word = new Word("Finishing");
word.label = "VBG";
word.capitalized = true;
learner.learn(word);
Word query = new Word("Finishing");
query.capitalized = true;
Set<String> tags = learner.allowableTags(query);
assertTrue(tags.contains("VBG"));
}

@Test
public void testLearnAddsSuffixToCorrectMapForCapitalizedFirstWord() {
MikheevLearner learner = new MikheevLearner("mapRouting");
Word word = new Word("Flying");
word.label = "NN";
word.capitalized = true;
word.previous = null;
learner.learn(word);
Word query = new Word("Flying");
query.capitalized = true;
query.previous = null;
Set<String> tags = learner.allowableTags(query);
assertTrue(tags.contains("NN"));
}

@Test
public void testLearnAddsSuffixToCorrectMapForCapitalizedNotFirstWord() {
MikheevLearner learner = new MikheevLearner("mapRouting2");
Word previous = new Word("is");
Word word = new Word("Flying");
word.label = "NN";
word.capitalized = true;
word.previous = previous;
learner.learn(word);
Word query = new Word("Flying");
query.capitalized = true;
query.previous = previous;
Set<String> tags = learner.allowableTags(query);
assertTrue(tags.contains("NN"));
}

@Test
public void testAllowableTagsWithCapitalizedAndShortWordFallsBackToNNP() {
MikheevLearner learner = new MikheevLearner("shortCap");
Word word = new Word("It");
word.capitalized = true;
Set<String> tags = learner.allowableTags(word);
assertEquals(1, tags.size());
assertTrue(tags.contains("NNP"));
}

@Test
public void testLearnAndAllowableTagsWithWordContainingOnlySymbols() {
MikheevLearner learner = new MikheevLearner("symbolWord");
Word word = new Word("!!!@##");
word.label = "SYM";
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
}

@Test
public void testLearnWithExactSixCharWordAddsThreeCharSuffixWhenFourthLastIsNotLetter() {
MikheevLearner learner = new MikheevLearner("nonAlphaFourth");
Word word = new Word("cid9ng");
word.label = "CD";
learner.learn(word);
Word query = new Word("cid9ng");
Set<String> tags = learner.allowableTags(query);
assertTrue(tags.contains("CD") || tags.contains("NN"));
}

@Test
public void testLearnAndAllowableTagsWith7CharWordWhere4thLastIsDigit() {
MikheevLearner learner = new MikheevLearner("digitInSuffix");
Word word = new Word("run9ing");
word.label = "SYM";
learner.learn(word);
Word query = new Word("run9ing");
Set<String> tags = learner.allowableTags(query);
assertTrue(tags.contains("SYM") || tags.contains("NN"));
}

@Test
public void testLearnIgnoresHyphenatedCapitalizedWordFromSuffixLearning() {
MikheevLearner learner = new MikheevLearner("ignoreCapHyphen");
Word word = new Word("Top-Class");
word.capitalized = true;
word.label = "JJ";
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertEquals(1, tags.size());
assertTrue(tags.contains("NNP"));
}

@Test
public void testAllowableTagsWithMultipleValidSuffixesReturnsAllTagsBeforePrune() {
MikheevLearner learner = new MikheevLearner("multiTagSuffix");
Word word1 = new Word("embedding");
word1.label = "VBG";
learner.learn(word1);
Word word2 = new Word("embedding");
word2.label = "NN";
learner.learn(word2);
Word query = new Word("embedding");
Set<String> tags = learner.allowableTags(query);
assertTrue(tags.contains("VBG"));
assertTrue(tags.contains("NN"));
}

@Test
public void testDoneLearningKeepsHighFrequencyAndRemovesLowFrequencyInSameSuffix() {
MikheevLearner learner = new MikheevLearner("selectivePrune");
Word word1 = new Word("running");
word1.label = "VBG";
Word word2 = new Word("running");
word2.label = "NN";
for (int i = 0; i < 20; i++) {
learner.learn(word1);
}
for (int i = 0; i < 1; i++) {
learner.learn(word2);
}
learner.doneLearning();
Word query = new Word("running");
Set<String> tags = learner.allowableTags(query);
assertTrue(tags.contains("VBG"));
assertFalse(tags.contains("NN"));
}

@Test
public void testLearnAndAllowableTagsWithFourCharWordAndAllLettersSuffix() {
MikheevLearner learner = new MikheevLearner("fourCharSuffix");
Word word = new Word("bets");
word.label = "NNS";
learner.learn(word);
Word query = new Word("bets");
Set<String> tags = learner.allowableTags(query);
assertTrue(tags.contains("NNS") || tags.contains("NN"));
}

@Test
public void testWriteAndReadBinaryPreservesTagsAcrossInstances() {
MikheevLearner learner = new MikheevLearner("binaryIOConsistency");
Word word = new Word("Creating");
word.label = "NN";
word.capitalized = true;
learner.learn(word);
ByteArrayOutputStream out = new ByteArrayOutputStream();
ExceptionlessOutputStream eos = new ExceptionlessOutputStream(out);
learner.write(eos);
ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
ExceptionlessInputStream eis = new ExceptionlessInputStream(in);
MikheevLearner restored = new MikheevLearner("restored");
restored.read(eis);
Word query = new Word("Creating");
query.capitalized = true;
Set<String> tags = restored.allowableTags(query);
assertTrue(tags.contains("NN") || tags.contains("NNP"));
}

@Test
public void testLearnDoesNotCrashOnMinimumViableInput() {
MikheevLearner learner = new MikheevLearner("minInput");
Word word = new Word("a");
word.label = "SYM";
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
}

@Test
public void testWriteToPrintStreamIncludesAllSectionHeaders() {
MikheevLearner learner = new MikheevLearner("textWriteHeaders");
Word word = new Word("Running");
word.label = "VBG";
word.capitalized = false;
learner.learn(word);
ByteArrayOutputStream out = new ByteArrayOutputStream();
PrintStream ps = new PrintStream(out);
learner.write(ps);
String text = out.toString();
assertTrue(text.contains("# if capitalized and first word in sentence:"));
assertTrue(text.contains("# if capitalized and not first word in sentence:"));
assertTrue(text.contains("# main table:"));
}

@Test
public void testLearnWithNullLabelReturnsGracefully() {
MikheevLearner learner = new MikheevLearner("nullLabel");
Word word = new Word("processing");
word.label = null;
word.capitalized = false;
try {
learner.learn(word);
assertTrue(true);
} catch (Exception e) {
fail("Learn should not throw an exception on null label.");
}
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
}

@Test
public void testLearnWithNullFormReturnsGracefully() {
MikheevLearner learner = new MikheevLearner("nullForm");
Word word = new Word(null);
word.label = "NN";
word.capitalized = false;
try {
learner.learn(word);
assertTrue(true);
} catch (Exception e) {
fail("Learn should not throw an exception on null form.");
}
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
}

@Test
public void testLearnWithUppercaseSuffixAddsCorrectly() {
MikheevLearner learner = new MikheevLearner("uppercaseSuffix");
Word word = new Word("PLAYING");
word.label = "VBG";
word.capitalized = false;
learner.learn(word);
Word query = new Word("playing");
Set<String> tags = learner.allowableTags(query);
assertTrue(tags.contains("VBG"));
}

@Test
public void testAllowableTagsWithCapitalizedSingleCharReturnsNNP() {
MikheevLearner learner = new MikheevLearner("singleCharCap");
Word word = new Word("A");
word.capitalized = true;
Set<String> tags = learner.allowableTags(word);
assertEquals(1, tags.size());
assertTrue(tags.contains("NNP"));
}

@Test
public void testAllowableTagsWithShortUncapitalizedHyphenatedReturnsNNJJ() {
MikheevLearner learner = new MikheevLearner("shortHyphen");
Word word = new Word("a-b");
word.capitalized = false;
Set<String> tags = learner.allowableTags(word);
assertEquals(2, tags.size());
assertTrue(tags.contains("NN"));
assertTrue(tags.contains("JJ"));
}

@Test
public void testWriteBinaryAndReadPreservesMultipleSuffixes() {
MikheevLearner learner = new MikheevLearner("multiSuffixBinary");
Word word = new Word("processing");
word.capitalized = false;
word.label = "VBG";
learner.learn(word);
Word word2 = new Word("finishing");
word2.capitalized = false;
word2.label = "VBG";
learner.learn(word2);
ByteArrayOutputStream out = new ByteArrayOutputStream();
ExceptionlessOutputStream eos = new ExceptionlessOutputStream(out);
learner.write(eos);
ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
ExceptionlessInputStream eis = new ExceptionlessInputStream(in);
MikheevLearner other = new MikheevLearner("restoredLearner");
other.read(eis);
Word query1 = new Word("processing");
Set<String> tags1 = other.allowableTags(query1);
assertTrue(tags1.contains("VBG"));
Word query2 = new Word("finishing");
Set<String> tags2 = other.allowableTags(query2);
assertTrue(tags2.contains("VBG"));
}

@Test
public void testLearnWithUpperBoundaryLengthWordAddsSuffixCorrectly() {
MikheevLearner learner = new MikheevLearner("maxLengthWord");
Word word = new Word("supercalifragilisticexpialidocious");
word.capitalized = false;
word.label = "NN";
learner.learn(word);
Word query = new Word("supercalifragilisticexpialidocious");
query.capitalized = false;
Set<String> tags = learner.allowableTags(query);
assertTrue(tags.contains("NN"));
}

@Test
public void testAllowableTagsReturnsConsistentResultAcrossCalls() {
MikheevLearner learner = new MikheevLearner("consistencyTest");
Word word = new Word("dancing");
word.label = "VBG";
word.capitalized = false;
learner.learn(word);
Word query1 = new Word("dancing");
Set<String> tags1 = learner.allowableTags(query1);
Word query2 = new Word("dancing");
Set<String> tags2 = learner.allowableTags(query2);
assertEquals(tags1, tags2);
}

@Test
public void testPruneRetainsSuffixWithExactly10Count() {
MikheevLearner learner = new MikheevLearner("edgeThreshold");
Word word = new Word("reforming");
word.label = "VB";
for (int i = 0; i < 10; i++) {
learner.learn(word);
}
learner.doneLearning();
Word query = new Word("reforming");
Set<String> tags = learner.allowableTags(query);
assertTrue(tags.contains("VB"));
}

@Test
public void testAllowableTagsReturnedFromFirstCapitalizedAndNotFirstCapitalized() {
MikheevLearner learner = new MikheevLearner("routeCheck");
Word word1 = new Word("Delivering");
word1.label = "NN";
word1.capitalized = true;
word1.previous = null;
learner.learn(word1);
Word word2 = new Word("Delivering");
word2.label = "VBG";
word2.capitalized = true;
word2.previous = new Word("is");
learner.learn(word2);
Word query1 = new Word("Delivering");
query1.capitalized = true;
query1.previous = null;
Set<String> tags1 = learner.allowableTags(query1);
Word query2 = new Word("Delivering");
query2.capitalized = true;
query2.previous = new Word("is");
Set<String> tags2 = learner.allowableTags(query2);
assertTrue(tags1.contains("NN"));
assertTrue(tags2.contains("VBG"));
}

@Test
public void testLearnWithVeryLongWordAddsSuffixes() {
MikheevLearner learner = new MikheevLearner("veryLongWord");
Word word = new Word("pneumonoultramicroscopicsilicovolcanoconiosis");
word.label = "NN";
word.capitalized = false;
learner.learn(word);
Word query = new Word("pneumonoultramicroscopicsilicovolcanoconiosis");
Set<String> tags = learner.allowableTags(query);
assertTrue(tags.contains("NN"));
}

@Test
public void testLearnDoesNotIncludePartialUnknownSuffixDueToHyphen() {
MikheevLearner learner = new MikheevLearner("hyphenSuffixMiddle");
Word word = new Word("mid-runner");
word.label = "NN";
word.capitalized = false;
learner.learn(word);
Word query = new Word("mid-runner");
Set<String> tags = learner.allowableTags(query);
assertTrue(tags.contains("JJ"));
assertTrue(tags.contains("NN"));
}

@Test
public void testLearnWithLowerCaseCapitalizedPrefixRoutesCorrectly() {
MikheevLearner learner = new MikheevLearner("CapPrefixRouting");
Word word = new Word("Breaking");
word.label = "VBG";
word.capitalized = true;
word.previous = null;
learner.learn(word);
Word query = new Word("Breaking");
query.capitalized = true;
query.previous = null;
Set<String> tags = learner.allowableTags(query);
assertTrue(tags.contains("VBG"));
}

@Test
public void testAllowableTagsFallbackWhenSuffixHasUppercaseAndNotLearned() {
MikheevLearner learner = new MikheevLearner("notLearnedUpper");
Word query = new Word("TESTING");
query.capitalized = false;
Set<String> tags = learner.allowableTags(query);
assertTrue(tags.contains("NN"));
}

@Test
public void testSuffixMatchingIsCaseInsensitiveDuringQuery() {
MikheevLearner learner = new MikheevLearner("suffixCaseInsensitive");
Word word = new Word("Creating");
word.label = "VBG";
word.capitalized = false;
learner.learn(word);
Word query = new Word("CREATING");
query.capitalized = false;
Set<String> tags = learner.allowableTags(query);
assertTrue(tags.contains("VBG"));
}

@Test
public void testWordWithNullPreviousCapitalizedChecksFirstCapitalizedMap() {
MikheevLearner learner = new MikheevLearner("firstCapSwitch");
Word word = new Word("Landing");
word.label = "VBG";
word.capitalized = true;
word.previous = null;
learner.learn(word);
Word query = new Word("Landing");
query.capitalized = true;
query.previous = null;
Set<String> tags = learner.allowableTags(query);
assertTrue(tags.contains("VBG"));
}

@Test
public void testDoneLearningDoesNotRemoveMajorityTagWhenRatioIsHigh() {
MikheevLearner learner = new MikheevLearner("pruneMajorityDominates");
Word word1 = new Word("staking");
word1.label = "VBG";
for (int i = 0; i < 100; i++) learner.learn(word1);
Word word2 = new Word("staking");
word2.label = "NN";
for (int i = 0; i < 1; i++) learner.learn(word2);
learner.doneLearning();
Word query = new Word("staking");
Set<String> tags = learner.allowableTags(query);
assertTrue(tags.contains("VBG"));
assertFalse(tags.contains("NN"));
}

@Test
public void testPrintStreamWritePrintsAllSuffixMapsEvenIfEmpty() {
MikheevLearner learner = new MikheevLearner("writeHeaders");
ByteArrayOutputStream baos = new ByteArrayOutputStream();
PrintStream ps = new PrintStream(baos);
learner.write(ps);
String output = baos.toString();
assertTrue(output.contains("if capitalized and first word in sentence"));
assertTrue(output.contains("if capitalized and not first word in sentence"));
assertTrue(output.contains("main table"));
}

@Test
public void testBinaryWriteAndReadDoesNotLoseSuffixData() {
MikheevLearner learner = new MikheevLearner("testBinaryRW");
Word word = new Word("forming");
word.label = "VBG";
word.capitalized = false;
learner.learn(word);
ByteArrayOutputStream bos = new ByteArrayOutputStream();
ExceptionlessOutputStream eos = new ExceptionlessOutputStream(bos);
learner.write(eos);
MikheevLearner inputLearner = new MikheevLearner("readInstance");
ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
ExceptionlessInputStream eis = new ExceptionlessInputStream(bis);
inputLearner.read(eis);
Word query = new Word("forming");
Set<String> tags = inputLearner.allowableTags(query);
assertTrue(tags.contains("VBG"));
}

@Test
public void testAllowableTagsPrefersSuffixLength4WhenAvailable() {
MikheevLearner learner = new MikheevLearner("longerSuffixFirst");
Word word = new Word("conflicting");
word.label = "NN";
word.capitalized = false;
learner.learn(word);
Word query = new Word("conflicting");
Set<String> tags = learner.allowableTags(query);
assertTrue(tags.contains("NN"));
}

@Test
public void testAllowableTagsFallsBackTo3LetterSuffixIf4LetterNotFound() {
MikheevLearner learner = new MikheevLearner("threeSuffixFallback");
Word word = new Word("blending");
word.label = "VBG";
word.capitalized = false;
learner.learn(word);
learner.doneLearning();
Word query = new Word("blending");
Set<String> tags = learner.allowableTags(query);
assertTrue(tags.contains("VBG") || tags.contains("NN"));
}

@Test
public void testLearnerParametersConstructorWithNullArgument() {
MikheevLearner.Parameters parameters = new MikheevLearner.Parameters(null);
assertNotNull(parameters);
}
}
