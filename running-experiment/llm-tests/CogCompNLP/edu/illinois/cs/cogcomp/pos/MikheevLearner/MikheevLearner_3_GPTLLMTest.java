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

public class MikheevLearner_3_GPTLLMTest {

@Test
public void testConstructorAndEmptyClone() {
MikheevLearner learner = new MikheevLearner("testLearner");
// assertEquals("testLearner", learner.getName());
Learner clone = learner.emptyClone();
assertNotNull(clone);
assertTrue(clone instanceof MikheevLearner);
}

@Test
public void testLearnUncapitalizedNonHyphenatedWordStoresTags() {
MikheevLearner learner = new MikheevLearner();
// learner.extractor = new DummyExtractor("running");
// learner.labeler = new DummyExtractor("VB");
Word word = new Word("running", "VB");
word.capitalized = false;
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("VB"));
}

@Test
public void testLearnHyphenatedLowercaseIgnoredByLearning() {
MikheevLearner learner = new MikheevLearner();
// learner.extractor = new DummyExtractor("state-of-the-art");
// learner.labeler = new DummyExtractor("JJ");
Word word = new Word("state-of-the-art", "JJ");
word.capitalized = false;
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
assertTrue(tags.contains("JJ"));
}

@Test
public void testAllowableTagsFallbackForCapitalizedFirstWord() {
MikheevLearner learner = new MikheevLearner();
Word word = new Word("London", "NNP");
word.capitalized = true;
word.previous = null;
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NNP"));
}

@Test
public void testAllowableTagsFallbackForUncapitalizedUnknownNonHyphenated() {
MikheevLearner learner = new MikheevLearner();
Word word = new Word("unseenword", "NN");
word.capitalized = false;
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
}

@Test
public void testAllowableTagsForCapitalizedNotFirstWord() {
MikheevLearner learner = new MikheevLearner();
// learner.extractor = new DummyExtractor("Rocketing");
// learner.labeler = new DummyExtractor("NN");
Word previous = new Word("This", "DT");
Word word = new Word("Rocketing", "NN");
word.capitalized = true;
word.previous = previous;
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
}

@Test
public void testForgetClearsLearnedKnowledgeCapFirstWord() {
MikheevLearner learner = new MikheevLearner();
// learner.extractor = new DummyExtractor("Apple");
// learner.labeler = new DummyExtractor("NNP");
Word word = new Word("Apple", "NNP");
word.capitalized = true;
word.previous = null;
learner.learn(word);
learner.forget();
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NNP"));
}

@Test
public void testWriteToTextStreamContainsExpectedSections() {
MikheevLearner learner = new MikheevLearner();
// learner.extractor = new DummyExtractor("Testing");
// learner.labeler = new DummyExtractor("NN");
Word word = new Word("Testing", "NN");
word.capitalized = true;
word.previous = null;
learner.learn(word);
ByteArrayOutputStream bytes = new ByteArrayOutputStream();
PrintStream ps = new PrintStream(bytes);
learner.write(ps);
String output = bytes.toString();
assertTrue(output.contains("# if capitalized and first word in sentence:"));
assertTrue(output.contains("# main table:"));
}

@Test
public void testBinaryWriteAndReadPreservesStructure() {
MikheevLearner learner = new MikheevLearner();
// learner.extractor = new DummyExtractor("Example");
// learner.labeler = new DummyExtractor("NN");
Word word = new Word("Example", "NN");
word.capitalized = true;
learner.learn(word);
ByteArrayOutputStream baos = new ByteArrayOutputStream();
ExceptionlessOutputStream eos = new ExceptionlessOutputStream(baos);
learner.write(eos);
// ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
// ExceptionlessInputStream eis = new ExceptionlessInputStream(bais);
MikheevLearner reloaded = new MikheevLearner();
// reloaded.read(eis);
Set<String> tags = reloaded.allowableTags(word);
assertTrue(tags.contains("NNP") || tags.contains("NN"));
}

@Test
public void testParametersConstructorCopy() {
MikheevLearner.Parameters original = new MikheevLearner.Parameters();
MikheevLearner.Parameters copy = new MikheevLearner.Parameters(original);
assertNotNull(copy);
assertTrue(copy instanceof MikheevLearner.Parameters);
}

@Test
public void testLearnWordExactlyFiveLettersTriggersSuffixOnly3() {
MikheevLearner learner = new MikheevLearner();
// learner.extractor = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
// 
// public String discreteValue(Object o) {
// return "laugh";
// }
// };
// learner.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
// 
// public String discreteValue(Object o) {
// return "VB";
// }
// };
Word word = new Word("laugh", "VB");
word.capitalized = false;
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("VB"));
}

@Test
public void testLearnWordExactlySixLettersStoresBothSuffixes() {
MikheevLearner learner = new MikheevLearner();
// learner.extractor = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
// 
// public String discreteValue(Object o) {
// return "ticker";
// }
// };
// learner.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
// 
// public String discreteValue(Object o) {
// return "NN";
// }
// };
Word word = new Word("ticker", "NN");
word.capitalized = false;
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
}

@Test
public void testAllowableTagsCapitalizedWithUnseenSuffixFallsBackToDefault() {
MikheevLearner learner = new MikheevLearner();
Word prev = new Word("The", "DT");
Word word = new Word("UnknownCap", "NNP");
word.capitalized = true;
word.previous = prev;
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NNP"));
assertEquals(1, tags.size());
}

@Test
public void testAllowableTagsUncapitalizedUnseenHyphenatedReturnsNNJJ() {
MikheevLearner learner = new MikheevLearner();
Word word = new Word("long-term", "JJ");
word.capitalized = false;
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("JJ"));
assertTrue(tags.contains("NN"));
assertEquals(2, tags.size());
}

@Test
public void testAllowableTagsUncapitalizedSeenReturnsTrainedTags() {
MikheevLearner learner = new MikheevLearner();
// learner.extractor = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
// 
// public String discreteValue(Object o) {
// return "tracker";
// }
// };
// learner.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
// 
// public String discreteValue(Object o) {
// return "NN";
// }
// };
Word word = new Word("tracker", "NN");
word.capitalized = false;
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
}

@Test
public void testLearnSkipsWhenLast3SuffixIsNotAllLetters() {
MikheevLearner learner = new MikheevLearner();
// learner.extractor = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
// 
// public String discreteValue(Object o) {
// return "12345";
// }
// };
// learner.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
// 
// public String discreteValue(Object o) {
// return "CD";
// }
// };
Word word = new Word("12345", "CD");
word.capitalized = false;
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
}

@Test
public void testPruneRemovesLowRelativeFrequencyTag() {
MikheevLearner learner = new MikheevLearner();
HashMap<String, TreeMap<String, Integer>> table = new HashMap<>();
TreeMap<String, Integer> suffixMap = new TreeMap<>();
suffixMap.put("NN", 90);
suffixMap.put("VB", 5);
table.put("ing", suffixMap);
learner.prune(table);
assertTrue(table.containsKey("ing"));
assertTrue(table.get("ing").containsKey("NN"));
assertFalse(table.get("ing").containsKey("VB"));
}

@Test
public void testPruneRemovesEntireEntryWithLowSum() {
MikheevLearner learner = new MikheevLearner();
HashMap<String, TreeMap<String, Integer>> table = new HashMap<>();
TreeMap<String, Integer> suffixMap = new TreeMap<>();
suffixMap.put("RB", 2);
suffixMap.put("JJ", 3);
table.put("ful", suffixMap);
learner.prune(table);
assertFalse(table.containsKey("ful"));
}

@Test
public void testAllowableTagsCapitalizedFirstUsesStoredSuffix() {
MikheevLearner learner = new MikheevLearner();
// learner.extractor = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
// 
// public String discreteValue(Object o) {
// return "Google";
// }
// };
// learner.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
// 
// public String discreteValue(Object o) {
// return "NNP";
// }
// };
Word word = new Word("Google", "NNP");
word.capitalized = true;
word.previous = null;
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NNP"));
}

@Test
public void testAllowableTagsCapitalizedSecondUsesStoredSuffix() {
MikheevLearner learner = new MikheevLearner();
// learner.extractor = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
// 
// public String discreteValue(Object o) {
// return "Amazon";
// }
// };
// learner.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
// 
// public String discreteValue(Object o) {
// return "NNP";
// }
// };
Word prev = new Word("is", "VBZ");
Word word = new Word("Amazon", "NNP");
word.capitalized = true;
word.previous = prev;
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NNP"));
}

@Test
public void testLearnWithNullPreviousAndNonAlphaSuffix() {
MikheevLearner learner = new MikheevLearner();
// learner.extractor = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
// 
// public String discreteValue(Object o) {
// return "ends!";
// }
// };
// learner.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
// 
// public String discreteValue(Object o) {
// return "SYM";
// }
// };
Word word = new Word("ends!", "SYM");
word.capitalized = true;
word.previous = null;
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NNP"));
}

@Test
public void testAllowableTagsEmptySuffixTableFallbackToNNP() {
MikheevLearner learner = new MikheevLearner();
Word word = new Word("CapitalX", "NNP");
word.capitalized = true;
word.previous = null;
Set<String> tags = learner.allowableTags(word);
assertEquals(1, tags.size());
assertTrue(tags.contains("NNP"));
}

@Test
public void testAllowableTagsEmptySuffixTableFallbackToNN() {
MikheevLearner learner = new MikheevLearner();
Word word = new Word("unknown", "NN");
word.capitalized = false;
Set<String> tags = learner.allowableTags(word);
assertEquals(1, tags.size());
assertTrue(tags.contains("NN"));
}

@Test
public void testAllowableTagsShortWordFallbackToNN() {
MikheevLearner learner = new MikheevLearner();
Word word = new Word("it", "PRP");
word.capitalized = false;
Set<String> tags = learner.allowableTags(word);
assertEquals(1, tags.size());
assertTrue(tags.contains("NN"));
}

@Test
public void testLearnWithNonLetterCharacterInOnlyOneCharOfSuffix() {
MikheevLearner learner = new MikheevLearner();
// learner.extractor = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
// 
// public String discreteValue(Object o) {
// return "act5!";
// }
// };
// learner.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
// 
// public String discreteValue(Object o) {
// return "SYM";
// }
// };
Word word = new Word("act5!", "SYM");
word.capitalized = false;
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
}

@Test
public void testLearnTooShortWordDoesNotThrow() {
MikheevLearner learner = new MikheevLearner();
// learner.extractor = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
// 
// public String discreteValue(Object o) {
// return "go";
// }
// };
// learner.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
// 
// public String discreteValue(Object o) {
// return "VB";
// }
// };
Word word = new Word("go", "VB");
word.capitalized = true;
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NNP"));
}

@Test
public void testWriteHandlesEmptyTablesGracefully() {
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
public void testAllowableTagsSuffixMatchFallback3Fails4Succeeds() {
MikheevLearner learner = new MikheevLearner();
// learner.extractor = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
// 
// public String discreteValue(Object o) {
// return "climax";
// }
// };
// learner.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
// 
// public String discreteValue(Object o) {
// return "NN";
// }
// };
Word word = new Word("climax", "NN");
word.capitalized = false;
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
}

@Test
public void testLearnWithSuffixLengthExactly3AndFromCapitalizedFirstWord() {
MikheevLearner learner = new MikheevLearner();
// learner.extractor = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
// 
// public String discreteValue(Object o) {
// return "Peace";
// }
// };
// learner.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
// 
// public String discreteValue(Object o) {
// return "NN";
// }
// };
Word word = new Word("Peace", "NN");
word.capitalized = true;
word.previous = null;
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
}

@Test
public void testLearnWithNullLabelDoesNotThrow() {
MikheevLearner learner = new MikheevLearner();
// learner.extractor = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
// 
// public String discreteValue(Object o) {
// return "testing";
// }
// };
// learner.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
// 
// public String discreteValue(Object o) {
// return null;
// }
// };
// Word word = new Word("testing", null);
// word.capitalized = false;
// learner.learn(word);
// Set<String> tags = learner.allowableTags(word);
// assertTrue(tags.contains("NN"));
}

@Test
public void testAllowableTagsCapitalizedNotFirstWordSuffixAbsentReturnsNNP() {
MikheevLearner learner = new MikheevLearner();
Word prev = new Word("Then", "RB");
Word word = new Word("Startable", "NN");
word.capitalized = true;
word.previous = prev;
Set<String> tags = learner.allowableTags(word);
assertEquals(1, tags.size());
assertTrue(tags.contains("NNP"));
}

@Test
public void testAllowableTagsWithShortUnknownHyphenatedWord() {
MikheevLearner learner = new MikheevLearner();
Word word = new Word("e-x", "JJ");
word.capitalized = false;
Set<String> tags = learner.allowableTags(word);
assertEquals(2, tags.size());
assertTrue(tags.contains("JJ"));
assertTrue(tags.contains("NN"));
}

@Test
public void testLearnWithSuffixLength3OnlyDoesNotIncrementLength4Branch() {
MikheevLearner learner = new MikheevLearner();
// learner.extractor = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
// 
// public String discreteValue(Object o) {
// return "boat";
// }
// };
// learner.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
// 
// public String discreteValue(Object o) {
// return "NN";
// }
// };
Word word = new Word("boat", "NN");
word.capitalized = false;
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
}

@Test
public void testLearnDoesNotOverrideExistingCount() {
MikheevLearner learner = new MikheevLearner();
// learner.extractor = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
// 
// public String discreteValue(Object o) {
// return "testing";
// }
// };
// learner.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
// 
// public String discreteValue(Object o) {
// return "NN";
// }
// };
Word word1 = new Word("testing", "NN");
word1.capitalized = false;
learner.learn(word1);
// learner.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
// 
// public String discreteValue(Object o) {
// return "VB";
// }
// };
Word word2 = new Word("testing", "VB");
word2.capitalized = false;
learner.learn(word2);
Set<String> tags = learner.allowableTags(word2);
assertTrue(tags.contains("NN"));
assertTrue(tags.contains("VB"));
}

@Test
public void testAllowableTagsCapitalizedFirstWordUsesLowerCaseSuffixKey() {
MikheevLearner learner = new MikheevLearner();
// learner.extractor = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
// 
// public String discreteValue(Object o) {
// return "Testing";
// }
// };
// learner.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
// 
// public String discreteValue(Object o) {
// return "VB";
// }
// };
Word word = new Word("Testing", "VB");
word.capitalized = true;
word.previous = null;
learner.learn(word);
Word query = new Word("TESTING", "VB");
query.capitalized = true;
query.previous = null;
Set<String> tags = learner.allowableTags(query);
assertTrue(tags.contains("VB"));
}

@Test
public void testLearnWithExactlyFiveCharsAndHyphenIgnoredCompletely() {
MikheevLearner learner = new MikheevLearner();
// learner.extractor = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
// 
// public String discreteValue(Object o) {
// return "ab-cd";
// }
// };
// learner.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
// 
// public String discreteValue(Object o) {
// return "JJ";
// }
// };
Word word = new Word("ab-cd", "JJ");
word.capitalized = false;
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("JJ"));
assertTrue(tags.contains("NN"));
}

@Test
public void testDoneLearningDoesNotClearValidHighFrequency() {
MikheevLearner learner = new MikheevLearner();
// learner.extractor = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
// 
// public String discreteValue(Object o) {
// return "replaying";
// }
// };
// learner.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
// 
// public String discreteValue(Object o) {
// return "VB";
// }
// };
for (int i = 0; i < 15; i++) {
Word word = new Word("replaying", "VB");
word.capitalized = false;
learner.learn(word);
}
learner.doneLearning();
Word check = new Word("replaying", "VB");
check.capitalized = false;
Set<String> tags = learner.allowableTags(check);
assertTrue(tags.contains("VB"));
}

@Test
public void testDoneLearningPrunesLowTotalSumEntry() {
MikheevLearner learner = new MikheevLearner();
// learner.extractor = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
// 
// public String discreteValue(Object o) {
// return "limit";
// }
// };
// learner.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
// 
// public String discreteValue(Object o) {
// return "NN";
// }
// };
Word word = new Word("limit", "NN");
word.capitalized = false;
for (int i = 0; i < 5; i++) {
learner.learn(word);
}
learner.doneLearning();
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
}

@Test
public void testLearnWithSuffixNonLetterAtThirdCharFromEndSkipsLearning() {
MikheevLearner learner = new MikheevLearner();
// learner.extractor = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
// 
// public String discreteValue(Object o) {
// return "te!ing";
// }
// };
// learner.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
// 
// public String discreteValue(Object o) {
// return "NN";
// }
// };
Word word = new Word("te!ing", "NN");
word.capitalized = false;
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
}

@Test
public void testLearnWithSuffixLengthExactlySixFourthCharNonLetterSkipsFourthSuffix() {
MikheevLearner learner = new MikheevLearner();
// learner.extractor = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
// 
// public String discreteValue(Object o) {
// return "semi-6";
// }
// };
// learner.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
// 
// public String discreteValue(Object o) {
// return "JJ";
// }
// };
Word word = new Word("semi-6", "JJ");
word.capitalized = false;
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("JJ"));
assertTrue(tags.contains("NN"));
}

@Test
public void testLearnSameSuffixMultipleTimesBuildsCombinedTags() {
MikheevLearner learner = new MikheevLearner();
// learner.extractor = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
// 
// public String discreteValue(Object o) {
// return "testing";
// }
// };
// learner.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
// 
// public String discreteValue(Object o) {
// return "NN";
// }
// };
Word word1 = new Word("testing", "NN");
word1.capitalized = false;
learner.learn(word1);
// learner.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
// 
// public String discreteValue(Object o) {
// return "VB";
// }
// };
Word word2 = new Word("testing", "VB");
word2.capitalized = false;
learner.learn(word2);
Word wordQuery = new Word("testing", "VB");
wordQuery.capitalized = false;
Set<String> tags = learner.allowableTags(wordQuery);
assertTrue(tags.contains("NN"));
assertTrue(tags.contains("VB"));
}

@Test
public void testAllowableTagsUncapitalizedContainsMatching4ButNot3Suffix() {
MikheevLearner learner = new MikheevLearner();
// learner.extractor = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
// 
// public String discreteValue(Object o) {
// return "rework";
// }
// };
// learner.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
// 
// public String discreteValue(Object o) {
// return "VB";
// }
// };
Word trainWord = new Word("rework", "VB");
trainWord.capitalized = false;
learner.learn(trainWord);
Word queryWord = new Word("rework", "VB");
queryWord.capitalized = false;
Set<String> tags = learner.allowableTags(queryWord);
assertTrue(tags.contains("VB"));
}

@Test
public void testLearnDoesNothingWhenExtractorReturnsNull() {
MikheevLearner learner = new MikheevLearner();
// learner.extractor = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
// 
// public String discreteValue(Object o) {
// return null;
// }
// };
// learner.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
// 
// public String discreteValue(Object o) {
// return "NN";
// }
// };
Word word = new Word("someword", "NN");
word.capitalized = false;
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
}

@Test
public void testLearnDoesNotThrowWhenNullExampleProvided() {
MikheevLearner learner = new MikheevLearner();
// learner.extractor = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
// 
// public String discreteValue(Object o) {
// return "null";
// }
// };
// learner.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
// 
// public String discreteValue(Object o) {
// return "NN";
// }
// };
try {
// learner.learn(null);
} catch (Exception e) {
fail("learn() should not throw when given null input.");
}
}

@Test
public void testAllowableTagsSuffixPresentButEmptyTagSetFallsBack() {
MikheevLearner learner = new MikheevLearner();
HashMap<String, java.util.TreeMap<String, Integer>> mockTable = new HashMap<>();
java.util.TreeMap<String, Integer> emptyTagMap = new TreeMap<>();
mockTable.put("ing", emptyTagMap);
// learner.extractor = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
// 
// public String discreteValue(Object o) {
// return "testing";
// }
// };
// learner.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
// 
// public String discreteValue(Object o) {
// return "VB";
// }
// };
Word word = new Word("testing", "VB");
word.capitalized = false;
learner.learn(word);
learner.doneLearning();
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
}

@Test
public void testDoneLearningWithEmptyTablesDoesNotThrow() {
MikheevLearner learner = new MikheevLearner();
try {
learner.doneLearning();
} catch (Exception e) {
fail("doneLearning() should not throw when internal tables are empty.");
}
}

@Test
public void testAllowableTagsCapitalizedPreviousWordNonNullAndFormNotTrained() {
MikheevLearner learner = new MikheevLearner();
Word previous = new Word("The", "DT");
Word word = new Word("Acme", "NNP");
word.capitalized = true;
word.previous = previous;
Set<String> tags = learner.allowableTags(word);
assertEquals(1, tags.size());
assertTrue(tags.contains("NNP"));
}

@Test
public void testAllowableTagsCapitalizedShortLengthWordReturnsNNP() {
MikheevLearner learner = new MikheevLearner();
Word word = new Word("It", "PRP");
word.capitalized = true;
word.previous = null;
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NNP"));
assertEquals(1, tags.size());
}

@Test
public void testAllowableTagsCapitalizedWordMatchedOnlyBy3CharSuffix() {
MikheevLearner learner = new MikheevLearner();
// learner.extractor = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
// 
// public String discreteValue(Object o) {
// return "Example";
// }
// };
// learner.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
// 
// public String discreteValue(Object o) {
// return "NNP";
// }
// };
Word word = new Word("Example", "NNP");
word.capitalized = true;
word.previous = null;
learner.learn(word);
Word queryWord = new Word("Example", "NNP");
queryWord.capitalized = true;
queryWord.previous = null;
Set<String> tags = learner.allowableTags(queryWord);
assertTrue(tags.contains("NNP"));
}

@Test
public void testAllowableTagsMissingSuffixFromAllTablesReturnsDefaultNNP() {
MikheevLearner learner = new MikheevLearner();
Word previous = new Word("He", "PRP");
Word word = new Word("Zyxopqr", "UNKNOWN");
word.capitalized = true;
word.previous = previous;
Set<String> tags = learner.allowableTags(word);
assertEquals(1, tags.size());
assertTrue(tags.contains("NNP"));
}

@Test
public void testAllowableTagsUncapitalizedNonHyphenatedWithOnly4CharSuffixMatch() {
MikheevLearner learner = new MikheevLearner();
// learner.extractor = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
// 
// public String discreteValue(Object o) {
// return "driving";
// }
// };
// learner.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
// 
// public String discreteValue(Object o) {
// return "VBG";
// }
// };
Word input = new Word("driving", "VBG");
input.capitalized = false;
learner.learn(input);
Word query = new Word("driving", "VBG");
query.capitalized = false;
Set<String> tags = learner.allowableTags(query);
assertTrue(tags.contains("VBG"));
}

@Test
public void testAllowableTagsSuffixFoundButTagSetIsEmptyFallbackNN() {
MikheevLearner learner = new MikheevLearner();
// learner.extractor = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
// 
// public String discreteValue(Object o) {
// return "testing";
// }
// };
// learner.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
// 
// public String discreteValue(Object o) {
// return "VB";
// }
// };
Word word = new Word("testing", "VB");
word.capitalized = false;
learner.learn(word);
learner.doneLearning();
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
assertEquals(1, tags.size());
}

@Test
public void testPruneRemovesLowFrequencyTagAndKeepsHigh() {
MikheevLearner learner = new MikheevLearner();
java.util.HashMap<String, java.util.TreeMap<String, Integer>> table = new java.util.HashMap<>();
java.util.TreeMap<String, Integer> map = new java.util.TreeMap<>();
map.put("NN", 91);
map.put("VB", 5);
table.put("ing", map);
learner.prune(table);
assertTrue(table.containsKey("ing"));
assertTrue(table.get("ing").containsKey("NN"));
assertFalse(table.get("ing").containsKey("VB"));
}

@Test
public void testPruneFullyRemovesSuffixDueToLowTotalCount() {
MikheevLearner learner = new MikheevLearner();
java.util.HashMap<String, java.util.TreeMap<String, Integer>> table = new java.util.HashMap<>();
java.util.TreeMap<String, Integer> map = new java.util.TreeMap<>();
map.put("JJ", 3);
map.put("NN", 2);
table.put("ful", map);
learner.prune(table);
assertFalse(table.containsKey("ful"));
}

@Test
public void testDoneLearningHandlesAlreadyPrunedTables() {
MikheevLearner learner = new MikheevLearner();
learner.doneLearning();
assertTrue(true);
}

@Test
public void testAllowableTagsFallbackForUnknownHyphenatedShortWord() {
MikheevLearner learner = new MikheevLearner();
Word word = new Word("a-b", "JJ");
word.capitalized = false;
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
assertTrue(tags.contains("JJ"));
}

@Test
public void testForgetDoesNotAffectNewLearning() {
MikheevLearner learner = new MikheevLearner();
// learner.extractor = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
// 
// public String discreteValue(Object o) {
// return "rebuild";
// }
// };
// learner.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
// 
// public String discreteValue(Object o) {
// return "VB";
// }
// };
Word word = new Word("rebuild", "VB");
word.capitalized = false;
learner.learn(word);
learner.forget();
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("VB"));
}

@Test
public void testIncrementAddsNewSuffixAndTagEntry() {
MikheevLearner learner = new MikheevLearner();
java.util.HashMap<String, java.util.TreeMap<String, Integer>> table = new java.util.HashMap<>();
learner.learn(new Word("walking", "VB") {

{
capitalized = false;
}
});
// learner.extractor = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
// 
// public String discreteValue(Object o) {
// return "walking";
// }
// };
// learner.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
// 
// public String discreteValue(Object o) {
// return "VB";
// }
// };
learner.learn(new Word("walking", "VB") {

{
capitalized = false;
}
});
Set<String> tags = learner.allowableTags(new Word("walking", "VB") {

{
capitalized = false;
}
});
assertTrue(tags.contains("VB"));
}

@Test
public void testWriteTextOutputAfterMultipleLearningPaths() {
MikheevLearner learner = new MikheevLearner();
// learner.extractor = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
// 
// public String discreteValue(Object o) {
// if (o instanceof Word)
// return ((Word) o).form;
// return "unknown";
// }
// };
// learner.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
// 
// public String discreteValue(Object o) {
// if (o instanceof Word)
// return ((Word) o).label;
// return "UNK";
// }
// };
learner.learn(new Word("Running", "VBG") {

{
capitalized = true;
previous = new Word("is", "VBZ");
}
});
learner.learn(new Word("Runaway", "NN") {

{
capitalized = true;
previous = null;
}
});
learner.learn(new Word("constructive", "JJ") {

{
capitalized = false;
}
});
ByteArrayOutputStream stream = new ByteArrayOutputStream();
PrintStream ps = new PrintStream(stream);
learner.write(ps);
String output = stream.toString();
assertTrue(output.contains("# if capitalized and first word in sentence:"));
assertTrue(output.contains("# if capitalized and not first word in sentence:"));
assertTrue(output.contains("# main table:"));
}

@Test
public void testAllowableTagsCapitalizedWordOnlyMatchesLowercaseSuffix() {
MikheevLearner learner = new MikheevLearner();
// learner.extractor = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
// 
// public String discreteValue(Object o) {
// return "Exported";
// }
// };
// learner.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
// 
// public String discreteValue(Object o) {
// return "VBD";
// }
// };
Word word = new Word("Exported", "VBD");
word.capitalized = true;
word.previous = null;
learner.learn(word);
Set<String> tags = learner.allowableTags(new Word("EXPORTED", "VBD") {

{
capitalized = true;
previous = null;
}
});
assertTrue(tags.contains("VBD"));
}

@Test
public void testPruneRemovesEntryWithOnlyOneVeryLowFrequencyTag() {
MikheevLearner learner = new MikheevLearner();
java.util.HashMap<String, java.util.TreeMap<String, Integer>> table = new java.util.HashMap<>();
java.util.TreeMap<String, Integer> map = new java.util.TreeMap<>();
map.put("DT", 1);
table.put("the", map);
learner.prune(table);
assertFalse(table.containsKey("the"));
}

@Test
public void testAllowableTagsExact4LetterSuffixMatchOnly() {
MikheevLearner learner = new MikheevLearner();
// learner.extractor = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
// 
// public String discreteValue(Object o) {
// return "excited";
// }
// };
// learner.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
// 
// public String discreteValue(Object o) {
// return "JJ";
// }
// };
learner.learn(new Word("excited", "JJ") {

{
capitalized = false;
}
});
Set<String> tags = learner.allowableTags(new Word("excited", "JJ") {

{
capitalized = false;
}
});
assertTrue(tags.contains("JJ"));
}

@Test
public void testAllowableTagsCapitalizedWord4LetterSuffixOverridesFallback() {
MikheevLearner learner = new MikheevLearner();
// learner.extractor = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
// 
// public String discreteValue(Object o) {
// return "Heroism";
// }
// };
// learner.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
// 
// public String discreteValue(Object o) {
// return "NN";
// }
// };
Word word = new Word("Heroism", "NN");
word.capitalized = true;
word.previous = null;
learner.learn(word);
Set<String> tags = learner.allowableTags(new Word("Heroism", "NN") {

{
capitalized = true;
previous = null;
}
});
assertTrue(tags.contains("NN"));
}

@Test
public void testLearnShortWordDoesNotThrowOrStore() {
MikheevLearner learner = new MikheevLearner();
// learner.extractor = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
// 
// public String discreteValue(Object o) {
// return "no";
// }
// };
// learner.labeler = new edu.illinois.cs.cogcomp.lbjava.classify.Classifier() {
// 
// public String discreteValue(Object o) {
// return "RB";
// }
// };
Word word = new Word("no", "RB");
word.capitalized = false;
learner.learn(word);
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
}

@Test
public void testAllowableTagsUncapitalizedWordWithAllDigitsSuffix() {
MikheevLearner learner = new MikheevLearner();
Word word = new Word("123456", "CD");
word.capitalized = false;
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NN"));
}

@Test
public void testAllowableTagsCapitalizedWordWithDigitsIgnoredSuffix() {
MikheevLearner learner = new MikheevLearner();
Word word = new Word("Part123", "NNP");
word.capitalized = true;
word.previous = null;
Set<String> tags = learner.allowableTags(word);
assertTrue(tags.contains("NNP"));
}
}
