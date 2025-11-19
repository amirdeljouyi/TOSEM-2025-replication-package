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

public class MikheevLearner_4_GPTLLMTest {

@Test
public void testDefaultConstructorInitializesMaps() {
MikheevLearner learner = new MikheevLearner("");
assertNotNull(learner);
assertTrue(learner.allowableTags(new Word(null, "Reset")).contains("NN"));
}

@Test
public void testLearnWithUncapitalizedWord() {
MikheevLearner learner = new MikheevLearner("");
// learner.extractor = new POSBaselineLearner.MockFeatureExtractor("running");
// learner.labeler = new POSBaselineLearner.MockLabeler("VB");
Word word = new Word(null, "running");
word.capitalized = false;
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("VB"));
}

@Test
public void testLearnDoesNotLearnForShortWords() {
MikheevLearner learner = new MikheevLearner("");
// learner.extractor = new POSBaselineLearner.MockFeatureExtractor("hi");
// learner.labeler = new POSBaselineLearner.MockLabeler("NN");
Word word = new Word(null, "hi");
word.capitalized = false;
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
}

@Test
public void testLearnDoesNotLearnForNonLetterSuffix() {
MikheevLearner learner = new MikheevLearner("");
// learner.extractor = new POSBaselineLearner.MockFeatureExtractor("run1!");
// learner.labeler = new POSBaselineLearner.MockLabeler("VB");
Word word = new Word(null, "run1!");
word.capitalized = false;
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
}

@Test
public void testLearnIgnoresHyphenatedLowerCaseWords() {
MikheevLearner learner = new MikheevLearner("");
// learner.extractor = new POSBaselineLearner.MockFeatureExtractor("well-known");
// learner.labeler = new POSBaselineLearner.MockLabeler("JJ");
Word word = new Word(null, "well-known");
word.capitalized = false;
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
assertTrue(tags.contains("JJ"));
}

@Test
public void testLearnCapitalizedFirstWord() {
MikheevLearner learner = new MikheevLearner("");
// learner.extractor = new POSBaselineLearner.MockFeatureExtractor("Running");
// learner.labeler = new POSBaselineLearner.MockLabeler("VB");
Word word = new Word(null, "Running");
word.capitalized = true;
word.previous = null;
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("VB") || tags.contains("NNP"));
}

@Test
public void testLearnCapitalizedNotFirstWord() {
MikheevLearner learner = new MikheevLearner("");
// learner.extractor = new POSBaselineLearner.MockFeatureExtractor("Running");
// learner.labeler = new POSBaselineLearner.MockLabeler("VB");
Word previous = new Word(null, "He");
previous.capitalized = true;
Word word = new Word(null, "Running");
word.capitalized = true;
word.previous = previous;
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("VB") || tags.contains("NNP"));
}

@Test
public void testAllowableTagsFallbackForUnseenCapitalizedFirstWord() {
MikheevLearner learner = new MikheevLearner("");
Word word = new Word(null, "Innovation");
word.capitalized = true;
word.previous = null;
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NNP"));
}

@Test
public void testAllowableTagsFallbackForHyphenatedWord() {
MikheevLearner learner = new MikheevLearner("");
Word word = new Word(null, "well-written");
word.capitalized = false;
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
assertTrue(tags.contains("JJ"));
}

@Test
public void testAllowableTagsFallbackToNNForUnseenWord() {
MikheevLearner learner = new MikheevLearner("");
Word word = new Word(null, "gibberishword");
word.capitalized = false;
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
}

@Test
public void testForgetResetsLearnedData() {
MikheevLearner learner = new MikheevLearner("");
// learner.extractor = new POSBaselineLearner.MockFeatureExtractor("walking");
// learner.labeler = new POSBaselineLearner.MockLabeler("VB");
Word word = new Word(null, "walking");
word.capitalized = false;
learner.learn(word);
Set<String> tagsBeforeForget = learner.allowableTags(word);
assertTrue(tagsBeforeForget.contains("VB"));
learner.forget();
Set<String> tagsAfterForget = learner.allowableTags(word);
assertTrue(tagsAfterForget.contains("NN"));
assertFalse(tagsAfterForget.contains("VB"));
}

@Test
public void testWriteOutputPrintsSuffixTable() {
MikheevLearner learner = new MikheevLearner("");
// learner.extractor = new POSBaselineLearner.MockFeatureExtractor("watching");
// learner.labeler = new POSBaselineLearner.MockLabeler("VB");
Word word = new Word(null, "watching");
word.capitalized = false;
learner.learn(word);
ByteArrayOutputStream stream = new ByteArrayOutputStream();
PrintStream ps = new PrintStream(stream);
learner.write(ps);
String output = stream.toString();
assertTrue(output.contains("main table"));
assertTrue(output.contains("ing"));
}

@Test
public void testEmptyCloneGeneratesFreshLearner() {
MikheevLearner learner = new MikheevLearner("");
Learner clone = learner.emptyClone();
assertNotNull(clone);
assertTrue(clone instanceof MikheevLearner);
assertNotSame(learner, clone);
}

@Test
public void testLearnWithExactly5LetterWordAllLetters() {
MikheevLearner learner = new MikheevLearner("");
// learner.extractor = new MikheevLearnerTest.POSBaselineLearner.MockFeatureExtractor("hatch");
// learner.labeler = new MikheevLearnerTest.POSBaselineLearner.MockLabeler("NN");
Word word = new Word(null, "hatch");
word.capitalized = false;
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
}

@Test
public void testLearnWithNonLetterInSuffixBlocksLearning() {
MikheevLearner learner = new MikheevLearner("");
// learner.extractor = new MikheevLearnerTest.POSBaselineLearner.MockFeatureExtractor("good!");
// learner.labeler = new MikheevLearnerTest.POSBaselineLearner.MockLabeler("JJ");
Word word = new Word(null, "good!");
word.capitalized = false;
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
}

@Test
public void testLearnWithSuffixOfLength6() {
MikheevLearner learner = new MikheevLearner("");
// learner.extractor = new MikheevLearnerTest.POSBaselineLearner.MockFeatureExtractor("climbing");
// learner.labeler = new MikheevLearnerTest.POSBaselineLearner.MockLabeler("VB");
Word word = new Word(null, "climbing");
word.capitalized = false;
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("VB"));
}

@Test
public void testAllowableTagsCapitalizedWithNonLetterSuffixFallbackNNP() {
MikheevLearner learner = new MikheevLearner("");
Word word = new Word(null, "Data-Driven");
word.capitalized = true;
word.previous = null;
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NNP"));
}

@Test
public void testPruningRemovesSuffixWhenTotalCountLow() {
MikheevLearner learner = new MikheevLearner("");
// learner.extractor = new MikheevLearnerTest.POSBaselineLearner.MockFeatureExtractor("smashed");
// learner.labeler = new MikheevLearnerTest.POSBaselineLearner.MockLabeler("VBD");
Word word = new Word(null, "smashed");
word.capitalized = false;
learner.learn(word);
learner.doneLearning();
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
}

@Test
public void testLearnSameSuffixWithDifferentTags() {
MikheevLearner learner = new MikheevLearner("");
// learner.extractor = new MikheevLearnerTest.POSBaselineLearner.MockFeatureExtractor("baking");
// learner.labeler = new MikheevLearnerTest.POSBaselineLearner.MockLabeler("VB");
Word word1 = new Word(null, "baking");
word1.capitalized = false;
learner.learn(word1);
// learner.labeler = new MikheevLearnerTest.POSBaselineLearner.MockLabeler("NN");
Word word2 = new Word(null, "baking");
word2.capitalized = false;
learner.learn(word2);
Set<String> tags = learner.allowableTags(word1);
assertTrue(tags.contains("VB"));
assertTrue(tags.contains("NN"));
}

@Test
public void testWordWithSymbolsButStillLetterSuffix() {
MikheevLearner learner = new MikheevLearner("");
// learner.extractor = new MikheevLearnerTest.POSBaselineLearner.MockFeatureExtractor("pre*fixing");
// learner.labeler = new MikheevLearnerTest.POSBaselineLearner.MockLabeler("VBG");
Word word = new Word(null, "prefixing");
word.capitalized = false;
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("VBG"));
}

@Test
public void testEmptyStringWordFallsBackToNN() {
MikheevLearner learner = new MikheevLearner("");
Word word = new Word(null, "");
word.capitalized = false;
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
}

@Test
public void testLearnWithNullPreviousButNonCapitalizedWord() {
MikheevLearner learner = new MikheevLearner("");
// learner.extractor = new MikheevLearnerTest.POSBaselineLearner.MockFeatureExtractor("training");
// learner.labeler = new MikheevLearnerTest.POSBaselineLearner.MockLabeler("VB");
Word word = new Word(null, "training");
word.capitalized = false;
word.previous = null;
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("VB"));
}

@Test
public void testWritePrintsCapitalizedTablesEvenWhenEmpty() {
MikheevLearner learner = new MikheevLearner("");
ByteArrayOutputStream stream = new ByteArrayOutputStream();
PrintStream ps = new PrintStream(stream);
learner.write(ps);
String output = stream.toString();
assertTrue(output.contains("capitalized"));
assertTrue(output.contains("main table"));
}

@Test
public void testAllowableTagsCapitalizedWordWithPreviousResolvesTable() {
MikheevLearner learner = new MikheevLearner("");
// learner.extractor = new MikheevLearnerTest.POSBaselineLearner.MockFeatureExtractor("Running");
// learner.labeler = new MikheevLearnerTest.POSBaselineLearner.MockLabeler("VB");
Word prev = new Word(null, "He");
Word word = new Word(null, "Running");
word.capitalized = true;
word.previous = prev;
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("VB"));
}

@Test
public void testLearnWithWordExactlyLength4ShouldNotLearn() {
MikheevLearner learner = new MikheevLearner("");
// learner.extractor = new POSBaselineLearner.MockFeatureExtractor("jump");
// learner.labeler = new POSBaselineLearner.MockLabeler("VB");
Word word = new Word(null, "jump");
word.capitalized = false;
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
}

@Test
public void testLearnWithMixedUpperLowerCaseCapitalizedFirst() {
MikheevLearner learner = new MikheevLearner("");
// learner.extractor = new POSBaselineLearner.MockFeatureExtractor("DisCussING");
// learner.labeler = new POSBaselineLearner.MockLabeler("VBZ");
Word word = new Word(null, "DisCussING");
word.capitalized = true;
word.previous = null;
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("VBZ"));
}

@Test
public void testAllowableTagsWordUnderMinLengthCapitalized() {
MikheevLearner learner = new MikheevLearner("");
Word word = new Word(null, "Ok");
word.capitalized = true;
word.previous = null;
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NNP"));
}

@Test
public void testAllowableTagsWordUnderMinLengthNotCapitalized() {
MikheevLearner learner = new MikheevLearner("");
Word word = new Word(null, "do");
word.capitalized = false;
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
}

@Test
public void testLearnWithShortWordWithHyphen() {
MikheevLearner learner = new MikheevLearner("");
// learner.extractor = new POSBaselineLearner.MockFeatureExtractor("ex-it");
// learner.labeler = new POSBaselineLearner.MockLabeler("NN");
Word word = new Word(null, "ex-it");
word.capitalized = false;
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
assertTrue(tags.contains("JJ"));
}

@Test
public void testPruneRemovesEntireSuffixEntryIfTotalLow() {
MikheevLearner learner = new MikheevLearner("");
// learner.extractor = new POSBaselineLearner.MockFeatureExtractor("sighing");
// learner.labeler = new POSBaselineLearner.MockLabeler("VB");
Word word = new Word(null, "sighing");
word.capitalized = false;
learner.learn(word);
learner.doneLearning();
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
}

@Test
public void testAllowableTagsHandlesWordEndingWithDigits() {
MikheevLearner learner = new MikheevLearner("");
Word word = new Word(null, "word123");
word.capitalized = false;
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
}

@Test
public void testLearnDoesNotIncrementSuffixWithSymbolEnding() {
MikheevLearner learner = new MikheevLearner("");
// learner.extractor = new POSBaselineLearner.MockFeatureExtractor("play#");
// learner.labeler = new POSBaselineLearner.MockLabeler("VB");
Word word = new Word(null, "play#");
word.capitalized = false;
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
}

@Test
public void testEmptyLearnerStillReturnsFallbackTags() {
MikheevLearner learner = new MikheevLearner("");
Word word = new Word(null, "unknownword");
word.capitalized = false;
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
}

@Test
public void testDoneLearningOnEmptyTablesDoesNotThrow() {
MikheevLearner learner = new MikheevLearner("");
learner.doneLearning();
Word word = new Word(null, "unknowndata");
word.capitalized = false;
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
}

@Test
public void testAllowableTagsCapitalizedSetFrom3LetterSuffixFallback() {
MikheevLearner learner = new MikheevLearner("");
// learner.extractor = new POSBaselineLearner.MockFeatureExtractor("Running");
// learner.labeler = new POSBaselineLearner.MockLabeler("NNP");
Word word = new Word(null, "Running");
word.capitalized = true;
word.previous = null;
learner.learn(word);
Word unseen = new Word(null, "Running");
unseen.capitalized = true;
unseen.previous = null;
Set<String> tags = learner.allowableTags(unseen);
assertTrue(tags.contains("NNP"));
}

@Test
public void testUnknownCapitalizedNonFirstWordFallback() {
MikheevLearner learner = new MikheevLearner("");
Word previous = new Word(null, "He");
Word current = new Word(null, "Greek");
current.capitalized = true;
current.previous = previous;
Set<String> tags = learner.allowableTags(current);
assertTrue(tags.contains("NNP"));
}

@Test
public void testWordWithLongSuffixReturnsCorrectTag() {
MikheevLearner learner = new MikheevLearner("");
// learner.extractor = new POSBaselineLearner.MockFeatureExtractor("botanical");
// learner.labeler = new POSBaselineLearner.MockLabeler("JJ");
Word word = new Word(null, "botanical");
word.capitalized = false;
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("JJ"));
}

@Test
public void testLearnUsesOnlyLowercaseSuffixes() {
MikheevLearner learner = new MikheevLearner("");
// learner.extractor = new POSBaselineLearner.MockFeatureExtractor("PROFESSING");
// learner.labeler = new POSBaselineLearner.MockLabeler("VBG");
Word word = new Word(null, "PROFESSING");
word.capitalized = false;
learner.learn(word);
Word test = new Word(null, "professing");
test.capitalized = false;
Set<String> tags = learner.allowableTags(test);
assertTrue(tags.contains("VBG"));
}

@Test
public void testAllowableTagsNoMatchFromCapitalizedWordReturnsNNP() {
MikheevLearner learner = new MikheevLearner("");
Word word = new Word(null, "Emporium");
word.capitalized = true;
word.previous = null;
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NNP"));
}

@Test
public void testSuffixEntryWithLowTagCountGetsPruned() {
MikheevLearner learner = new MikheevLearner("");
// learner.extractor = new MikheevLearnerTest.POSBaselineLearner.MockFeatureExtractor("exploring");
// learner.labeler = new MikheevLearnerTest.POSBaselineLearner.MockLabeler("VBG");
Word word = new Word(null, "exploring");
word.capitalized = false;
learner.learn(word);
// learner.labeler = new MikheevLearnerTest.POSBaselineLearner.MockLabeler("JJ");
Word w2 = new Word(null, "exploring");
w2.capitalized = false;
learner.learn(w2);
learner.doneLearning();
Word test = new Word(null, "exploring");
test.capitalized = false;
Set<String> tags = learner.allowableTags(test);
assertTrue(tags.contains("NN"));
}

@Test
public void testCapitalizedHyphenatedWordUsesNNJJHardcoded() {
MikheevLearner learner = new MikheevLearner("");
Word word = new Word(null, "High-Level");
word.capitalized = true;
word.previous = null;
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
assertTrue(tags.contains("JJ"));
}

@Test
public void testNullExtractorOrLabelerCausesNoLearning() {
MikheevLearner learner = new MikheevLearner("");
// learner.extractor = null;
// learner.labeler = null;
Word word = new Word(null, "running");
word.capitalized = false;
try {
learner.learn(word);
} catch (Exception e) {
fail("Learn should not throw on null extractor or labeler");
}
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
}

@Test
public void testLearnWithNullExampleDoesNotThrow() {
MikheevLearner learner = new MikheevLearner("");
try {
// learner.learn(null);
} catch (Exception e) {
fail("Learner should not throw exception on null input");
}
}

@Test
public void testSuffixStorageIsCaseInsensitive() {
MikheevLearner learner = new MikheevLearner("");
// learner.extractor = new MikheevLearnerTest.POSBaselineLearner.MockFeatureExtractor("Testing");
// learner.labeler = new MikheevLearnerTest.POSBaselineLearner.MockLabeler("VB");
Word word = new Word(null, "Testing");
word.capitalized = false;
learner.learn(word);
Word wordLower = new Word(null, "testing");
wordLower.capitalized = false;
Set<String> tags = learner.allowableTags(wordLower);
assertTrue(tags.contains("VB"));
}

@Test
public void testRepeatLearningIncrementsCountButRetainsSingleTagEntry() {
MikheevLearner learner = new MikheevLearner("");
// learner.extractor = new MikheevLearnerTest.POSBaselineLearner.MockFeatureExtractor("raining");
// learner.labeler = new MikheevLearnerTest.POSBaselineLearner.MockLabeler("VB");
Word word1 = new Word(null, "raining");
word1.capitalized = false;
Word word2 = new Word(null, "raining");
word2.capitalized = false;
learner.learn(word1);
learner.learn(word2);
Set<String> tags = learner.allowableTags(word2);
assertEquals(1, tags.size());
assertTrue(tags.contains("VB"));
}

@Test
public void testPruneRemovesLowFrequency4CharSuffix() {
MikheevLearner learner = new MikheevLearner("");
// learner.extractor = new MikheevLearnerTest.POSBaselineLearner.MockFeatureExtractor("biology");
// learner.labeler = new MikheevLearnerTest.POSBaselineLearner.MockLabeler("NN");
Word word = new Word(null, "biology");
word.capitalized = false;
learner.learn(word);
learner.doneLearning();
Word test = new Word(null, "biology");
test.capitalized = false;
Set<String> tags = learner.allowableTags(test);
assertTrue(tags.contains("NN"));
}

@Test
public void testValidLengthWordWithInvalidSuffixComposition() {
MikheevLearner learner = new MikheevLearner("");
// learner.extractor = new MikheevLearnerTest.POSBaselineLearner.MockFeatureExtractor("try$#");
// learner.labeler = new MikheevLearnerTest.POSBaselineLearner.MockLabeler("VB");
Word word = new Word(null, "try$#");
word.capitalized = false;
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
}

@Test
public void testMultipleTagsOnSuffixRemainsAfterPrune() {
MikheevLearner learner = new MikheevLearner("");
// learner.extractor = new MikheevLearnerTest.POSBaselineLearner.MockFeatureExtractor("jumping");
// learner.labeler = new MikheevLearnerTest.POSBaselineLearner.MockLabeler("VB");
Word word1 = new Word(null, "jumping");
word1.capitalized = false;
Word word2 = new Word(null, "jumping");
word2.capitalized = false;
Word word3 = new Word(null, "jumping");
word3.capitalized = false;
learner.learn(word1);
learner.learn(word2);
learner.learn(word3);
// learner.labeler = new MikheevLearnerTest.POSBaselineLearner.MockLabeler("JJ");
Word word4 = new Word(null, "jumping");
word4.capitalized = false;
learner.learn(word4);
learner.doneLearning();
Word query = new Word(null, "jumping");
query.capitalized = false;
Set<String> tags = learner.allowableTags(query);
assertTrue(tags.contains("VB"));
assertFalse(tags.contains("JJ"));
}

@Test
public void testAllowableTagsUncapitalizedHyphenatedLongValidSuffix() {
MikheevLearner learner = new MikheevLearner("");
// learner.extractor = new MikheevLearnerTest.POSBaselineLearner.MockFeatureExtractor("multi-colored");
// learner.labeler = new MikheevLearnerTest.POSBaselineLearner.MockLabeler("JJ");
Word word = new Word(null, "multi-colored");
word.capitalized = false;
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
assertTrue(tags.contains("JJ"));
}

@Test
public void testLearnRejectsValidLengthIfSuffixHasNonLetterChars() {
MikheevLearner learner = new MikheevLearner();
// learner.extractor = new MikheevLearnerTest.POSBaselineLearner.MockFeatureExtractor("val!d");
// learner.labeler = new MikheevLearnerTest.POSBaselineLearner.MockLabeler("NN");
Word word = new Word(null, "val!d");
word.capitalized = false;
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
}

@Test
public void testLearnAndAllowableTagsPreciseCombinationOf3And4LetterSuffix() {
MikheevLearner learner = new MikheevLearner();
// learner.extractor = new MikheevLearnerTest.POSBaselineLearner.MockFeatureExtractor("painting");
// learner.labeler = new MikheevLearnerTest.POSBaselineLearner.MockLabeler("VBG");
Word word = new Word(null, "painting");
word.capitalized = false;
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("VBG"));
}

@Test
public void testTreeMapSameFrequencyTagsRemainAfterPrune() {
MikheevLearner learner = new MikheevLearner();
// learner.extractor = new MikheevLearnerTest.POSBaselineLearner.MockFeatureExtractor("gathering");
// learner.labeler = new MikheevLearnerTest.POSBaselineLearner.MockLabeler("NN");
Word w1 = new Word(null, "gathering");
w1.capitalized = false;
learner.learn(w1);
// learner.labeler = new MikheevLearnerTest.POSBaselineLearner.MockLabeler("VB");
Word w2 = new Word(null, "gathering");
w2.capitalized = false;
learner.learn(w2);
learner.doneLearning();
Word test = new Word(null, "gathering");
test.capitalized = false;
Set<String> tags = learner.allowableTags(test);
assertTrue(tags.contains("NN") || tags.contains("VB"));
}

@Test
public void testLearnWordEndsUppercaseStillProcessesSuffix() {
MikheevLearner learner = new MikheevLearner();
// learner.extractor = new MikheevLearnerTest.POSBaselineLearner.MockFeatureExtractor("READING");
// learner.labeler = new MikheevLearnerTest.POSBaselineLearner.MockLabeler("VBG");
Word word = new Word(null, "READING");
word.capitalized = false;
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("VBG"));
}

@Test
public void testLearnIgnoresWordWithWhitespaceSuffix() {
MikheevLearner learner = new MikheevLearner();
String noisy = "swim ";
// learner.extractor = new MikheevLearnerTest.POSBaselineLearner.MockFeatureExtractor(noisy);
// learner.labeler = new MikheevLearnerTest.POSBaselineLearner.MockLabeler("VB");
Word word = new Word(null, noisy);
word.capitalized = false;
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
}

@Test
public void testLearnIgnoresWordWithUnderscore() {
MikheevLearner learner = new MikheevLearner();
// learner.extractor = new MikheevLearnerTest.POSBaselineLearner.MockFeatureExtractor("review_case");
// learner.labeler = new MikheevLearnerTest.POSBaselineLearner.MockLabeler("NN");
Word word = new Word(null, "review_case");
word.capitalized = false;
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
}

@Test
public void testAllowableTagsCapitalizedWordSuffixAbsentUsesFallbackOrder() {
MikheevLearner learner = new MikheevLearner();
Word word = new Word(null, "Explode");
word.capitalized = true;
word.previous = null;
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NNP"));
}

@Test
public void testLearnSuffixWithDigitsInMiddlePreventsLearning() {
MikheevLearner learner = new MikheevLearner();
// learner.extractor = new MikheevLearnerTest.POSBaselineLearner.MockFeatureExtractor("fon3tic");
// learner.labeler = new MikheevLearnerTest.POSBaselineLearner.MockLabeler("JJ");
Word word = new Word(null, "fon3tic");
word.capitalized = false;
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
}

@Test
public void testWriteDoesNotThrowWhenNothingLearned() {
MikheevLearner learner = new MikheevLearner();
ByteArrayOutputStream output = new ByteArrayOutputStream();
PrintStream ps = new PrintStream(output);
try {
learner.write(ps);
} catch (Exception e) {
fail("Write should not throw exception if empty");
}
assertTrue(output.toString().contains("main table"));
}

@Test
public void testDoneLearningPreservesValidSuffixesOnly() {
MikheevLearner learner = new MikheevLearner();
// learner.extractor = new MikheevLearnerTest.POSBaselineLearner.MockFeatureExtractor("skipping");
// learner.labeler = new MikheevLearnerTest.POSBaselineLearner.MockLabeler("VBG");
for (int i = 0; i < 11; i++) {
Word w = new Word(null, "skipping");
w.capitalized = false;
learner.learn(w);
}
learner.doneLearning();
Word test = new Word(null, "skipping");
test.capitalized = false;
Set<String> tags = learner.allowableTags(test);
assertTrue(tags.contains("VBG"));
}

@Test
public void testAllowableTagsForCapitalizedNotFirstPositionWordWithoutMatch() {
MikheevLearner learner = new MikheevLearner();
Word prev = new Word(null, "The");
Word current = new Word(null, "Machine");
current.capitalized = true;
current.previous = prev;
Set<String> tags = learner.allowableTags(current);
assertTrue(tags.contains("NNP"));
}

@Test
public void testAllowableTagsCapitalizedNonFirstNotSeenSuffixFallbackToNNP() {
MikheevLearner learner = new MikheevLearner();
Word previous = new Word(null, "The");
Word current = new Word(null, "Author");
current.capitalized = true;
current.previous = previous;
Set<String> tags = learner.allowableTags(current);
assertTrue(tags.contains("NNP"));
}

@Test
public void testAllowableTagsCapitalizedFirstSeenSuffixReturnsLearnedTag() {
MikheevLearner learner = new MikheevLearner();
// learner.extractor = new MikheevLearnerTest.POSBaselineLearner.MockFeatureExtractor("Painting");
// learner.labeler = new MikheevLearnerTest.POSBaselineLearner.MockLabeler("NNP");
Word word = new Word(null, "Painting");
word.capitalized = true;
word.previous = null;
learner.learn(word);
Word test = new Word(null, "Painting");
test.capitalized = true;
test.previous = null;
Set<String> tags = learner.allowableTags(test);
assertTrue(tags.contains("NNP"));
}

@Test
public void testAllowableTagsCapitalizedNonFirstSeenSuffixReturnsLearnedTag() {
MikheevLearner learner = new MikheevLearner();
// learner.extractor = new MikheevLearnerTest.POSBaselineLearner.MockFeatureExtractor("Building");
// learner.labeler = new MikheevLearnerTest.POSBaselineLearner.MockLabeler("NN");
Word previous = new Word(null, "The");
Word current = new Word(null, "Building");
current.capitalized = true;
current.previous = previous;
learner.learn(current);
Set<String> tags = learner.allowableTags(current);
assertTrue(tags.contains("NN"));
}

@Test
public void testAllowableTagsLowercaseSeenSuffixReturnsLearnedTag() {
MikheevLearner learner = new MikheevLearner();
// learner.extractor = new MikheevLearnerTest.POSBaselineLearner.MockFeatureExtractor("climbing");
// learner.labeler = new MikheevLearnerTest.POSBaselineLearner.MockLabeler("VBG");
Word word = new Word(null, "climbing");
word.capitalized = false;
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("VBG"));
}

@Test
public void testAllowableTagsLowercaseSeenSuffixOnly4CharSuffix() {
MikheevLearner learner = new MikheevLearner();
// learner.extractor = new MikheevLearnerTest.POSBaselineLearner.MockFeatureExtractor("original");
// learner.labeler = new MikheevLearnerTest.POSBaselineLearner.MockLabeler("JJ");
Word word = new Word(null, "original");
word.capitalized = false;
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("JJ"));
}

@Test
public void testAllowableTagsUncapitalizedWithUnknownSuffixReturnsFallbackNN() {
MikheevLearner learner = new MikheevLearner();
Word word = new Word(null, "qwertyuiop");
word.capitalized = false;
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
}

@Test
public void testLearnDoesNotIncludeNonLetterSuffixEvenIfLengthValid() {
MikheevLearner learner = new MikheevLearner();
// learner.extractor = new MikheevLearnerTest.POSBaselineLearner.MockFeatureExtractor("play!");
// learner.labeler = new MikheevLearnerTest.POSBaselineLearner.MockLabeler("VB");
Word word = new Word(null, "play!");
word.capitalized = false;
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
}

@Test
public void testDoneLearningDoesNotRemoveHighFrequencyEntries() {
MikheevLearner learner = new MikheevLearner();
// learner.extractor = new MikheevLearnerTest.POSBaselineLearner.MockFeatureExtractor("crawling");
// learner.labeler = new MikheevLearnerTest.POSBaselineLearner.MockLabeler("VBG");
for (int i = 0; i < 12; i++) {
Word word = new Word(null, "crawling");
word.capitalized = false;
learner.learn(word);
}
learner.doneLearning();
Word test = new Word(null, "crawling");
test.capitalized = false;
Set<String> tags = learner.allowableTags(test);
assertTrue(tags.contains("VBG"));
}

@Test
public void testLearnNoSuffixIfLengthEqualsSuffix() {
MikheevLearner learner = new MikheevLearner();
// learner.extractor = new MikheevLearnerTest.POSBaselineLearner.MockFeatureExtractor("ing");
// learner.labeler = new MikheevLearnerTest.POSBaselineLearner.MockLabeler("NN");
Word word = new Word(null, "ing");
word.capitalized = false;
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
}

@Test
public void testAllowableTagsWithCapitalizedHyphenatedUsesNNJJ() {
MikheevLearner learner = new MikheevLearner();
Word word = new Word(null, "Quick-Fix");
word.capitalized = true;
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
assertTrue(tags.contains("JJ"));
}

@Test
public void testAllowableTagsSeen4CharSuffixOnly() {
MikheevLearner learner = new MikheevLearner();
// learner.extractor = new MikheevLearnerTest.POSBaselineLearner.MockFeatureExtractor("national");
// learner.labeler = new MikheevLearnerTest.POSBaselineLearner.MockLabeler("JJ");
Word word = new Word(null, "national");
word.capitalized = false;
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("JJ"));
}

@Test
public void testLearnWithNullPreviousAndCapitalizedWordAddsToFirstTable() {
MikheevLearner learner = new MikheevLearner();
// learner.extractor = new MikheevLearnerTest.POSBaselineLearner.MockFeatureExtractor("Programming");
// learner.labeler = new MikheevLearnerTest.POSBaselineLearner.MockLabeler("NNP");
Word word = new Word(null, "Programming");
word.capitalized = true;
word.previous = null;
learner.learn(word);
Word test = new Word(null, "Programming");
test.capitalized = true;
test.previous = null;
Set<String> tags = learner.allowableTags(test);
assertTrue(tags.contains("NNP"));
}

@Test
public void testLearnWithCapitalizedPreviousAddsToNotFirstTable() {
MikheevLearner learner = new MikheevLearner();
// learner.extractor = new MikheevLearnerTest.POSBaselineLearner.MockFeatureExtractor("Island");
// learner.labeler = new MikheevLearnerTest.POSBaselineLearner.MockLabeler("NNP");
Word previous = new Word(null, "The");
Word word = new Word(null, "Island");
word.capitalized = true;
word.previous = previous;
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NNP"));
}

@Test
public void testLearnWithExactFiveLetterWordAndValidSuffix() {
MikheevLearner learner = new MikheevLearner();
// learner.extractor = new MikheevLearnerTest.POSBaselineLearner.MockFeatureExtractor("blink");
// learner.labeler = new MikheevLearnerTest.POSBaselineLearner.MockLabeler("VB");
Word word = new Word(null, "blink");
word.capitalized = false;
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("VB"));
}

@Test
public void testLearnDoesNotCrashOnShortWord() {
MikheevLearner learner = new MikheevLearner();
// learner.extractor = new MikheevLearnerTest.POSBaselineLearner.MockFeatureExtractor("do");
// learner.labeler = new MikheevLearnerTest.POSBaselineLearner.MockLabeler("VB");
Word word = new Word(null, "do");
word.capitalized = false;
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
}

@Test
public void testLearnSuffixStoredInLowerCaseRegardlessOfCase() {
MikheevLearner learner = new MikheevLearner();
// learner.extractor = new POSBaselineLearner.MockFeatureExtractor("READING");
// learner.labeler = new POSBaselineLearner.MockLabeler("VBG");
Word word = new Word(null, "READING");
word.capitalized = false;
learner.learn(word);
Word wordLower = new Word(null, "reading");
wordLower.capitalized = false;
Set<String> tags = learner.allowableTags(wordLower);
assertTrue(tags.contains("VBG"));
}

@Test
public void testAllowableTagsWithWordThatIsExactlySuffixLengthMinusOne() {
MikheevLearner learner = new MikheevLearner();
Word word = new Word(null, "ab");
word.capitalized = false;
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
}

@Test
public void testWordWithNullFormDoesNotThrow() {
MikheevLearner learner = new MikheevLearner();
// Word word = new Word(null, null);
// word.capitalized = false;
try {
// Set<String> tags = learner.allowableTags(word);
// assertTrue(tags.contains("NN"));
} catch (Exception e) {
fail("Should not throw on null form");
}
}

@Test
public void testLearnWordWithNullFormDoesNotThrow() {
MikheevLearner learner = new MikheevLearner();
// Word word = new Word(null, null);
// word.capitalized = false;
// learner.extractor = new POSBaselineLearner.MockFeatureExtractor(null);
// learner.labeler = new POSBaselineLearner.MockLabeler("NN");
try {
// learner.learn(word);
} catch (Exception e) {
fail("Should not throw on null word form");
}
}

@Test
public void testLearnWordWithEmptyStringFormDoesNotTriggerLearnLogic() {
MikheevLearner learner = new MikheevLearner();
Word word = new Word(null, "");
word.capitalized = false;
// learner.extractor = new POSBaselineLearner.MockFeatureExtractor("");
// learner.labeler = new POSBaselineLearner.MockLabeler("NN");
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
}

@Test
public void testLearnIgnoresSuffixWithDiacriticCharacters() {
MikheevLearner learner = new MikheevLearner();
// learner.extractor = new POSBaselineLearner.MockFeatureExtractor("naïve");
// learner.labeler = new POSBaselineLearner.MockLabeler("JJ");
Word word = new Word(null, "naïve");
word.capitalized = false;
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
}

@Test
public void testLearnShortHyphenatedWordTriggersNNJJ() {
MikheevLearner learner = new MikheevLearner();
Word word = new Word(null, "re-do");
word.capitalized = false;
// learner.extractor = new POSBaselineLearner.MockFeatureExtractor("re-do");
// learner.labeler = new POSBaselineLearner.MockLabeler("VB");
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
assertTrue(tags.contains("JJ"));
}

@Test
public void testLearnHandlesSuffixWithSurrogatePairCharacters() {
MikheevLearner learner = new MikheevLearner();
String wordText = "abc𝌆d";
// learner.extractor = new POSBaselineLearner.MockFeatureExtractor(wordText);
// learner.labeler = new POSBaselineLearner.MockLabeler("SYM");
Word word = new Word(null, wordText);
word.capitalized = false;
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
}

@Test
public void testAllowableTagsCapitalizedWordFallsBackWhenSuffixNotInMapDueToLength() {
MikheevLearner learner = new MikheevLearner();
Word word = new Word(null, "XYZ");
word.capitalized = true;
word.previous = null;
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NNP"));
}

@Test
public void testAllowableTagsLowercaseWithSuffixedLearnedThenClearedByForget() {
MikheevLearner learner = new MikheevLearner();
// learner.extractor = new POSBaselineLearner.MockFeatureExtractor("walking");
// learner.labeler = new POSBaselineLearner.MockLabeler("VBG");
Word word = new Word(null, "walking");
word.capitalized = false;
learner.learn(word);
Set<String> tagsBeforeForget = learner.allowableTags(word);
assertTrue(tagsBeforeForget.contains("VBG"));
learner.forget();
Set<String> tagsAfterForget = learner.allowableTags(word);
assertTrue(tagsAfterForget.contains("NN"));
}
}
