package opennlp.tools.stemmer.snowball;

import opennlp.tools.dictionary.Dictionary;
import opennlp.tools.dictionary.serializer.Attributes;
import opennlp.tools.dictionary.serializer.DictionaryEntryPersistor;
import opennlp.tools.dictionary.serializer.Entry;
import opennlp.tools.ml.model.*;
import opennlp.tools.stemmer.snowball.arabicStemmer;
import opennlp.tools.util.*;
import org.junit.jupiter.api.Test;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class arabicStemmer_3_GPTLLMTest {

@Test
public void testNormalizePre_RemovesTatweel() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("\u0640\u0643\u062A\u0627\u0628");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("كتاب", stemmer.getCurrent());
}

@Test
public void testNormalizePre_ReplacesArabicNumbers() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("\u0661\u0662\u0663");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("123", stemmer.getCurrent());
}

@Test
public void testNormalizePre_ReplacesPresentationForms() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("\uFE8D\uFE8E\uFE8F");
boolean result = stemmer.stem();
assertTrue(result);
String output = stemmer.getCurrent();
assertFalse(output.contains("\uFE8D"));
assertFalse(output.contains("\uFE8E"));
assertFalse(output.contains("\uFE8F"));
}

@Test
public void testNormalizePost_ReplacesHamzaForms() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("\u0622\u0623\u0625\u0624\u0626");
boolean result = stemmer.stem();
assertTrue(result);
String output = stemmer.getCurrent();
assertTrue(output.contains("\u0621"));
}

@Test
public void testPrefixStep1_ReplacesCompositeAlefs() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("\u0623\u0622\u0643\u062A\u0627\u0628");
boolean result = stemmer.stem();
assertTrue(result);
String stemmed = stemmer.getCurrent();
assertFalse(stemmed.startsWith("\u0623\u0622"));
}

@Test
public void testPrefixStep2_RemovesSingleLetterPrefix() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("\u0641\u0627\u0643\u0644");
boolean result = stemmer.stem();
assertTrue(result);
String stemmed = stemmer.getCurrent();
assertFalse(stemmed.startsWith("\u0641"));
}

@Test
public void testPrefixStep3aNoun_RemovesBaAlPrefix() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("\u0628\u0627\u0644\u0643\u062A\u0627\u0628");
boolean result = stemmer.stem();
assertTrue(result);
String stemmed = stemmer.getCurrent();
assertFalse(stemmed.startsWith("\u0628\u0627\u0644"));
}

@Test
public void testPrefixStep3bNoun_ReplacesDoubleBaWithBa() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("\u0628\u0628\u0645\u062F\u0631\u0633\u0629");
boolean result = stemmer.stem();
assertTrue(result);
String stemmed = stemmer.getCurrent();
assertFalse(stemmed.startsWith("\u0628\u0628"));
assertTrue(stemmed.startsWith("\u0628"));
}

@Test
public void testPrefixStep4Verb_ReplacesIstPrefix() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("\u064A\u0633\u062A\u0643\u062A\u0628");
boolean result = stemmer.stem();
assertTrue(result);
String stemmed = stemmer.getCurrent();
assertFalse(stemmed.startsWith("\u064A\u0633\u062A"));
}

@Test
public void testSuffixNounStep1a_RemovesPossessiveSuffix() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("كتابكما");
boolean result = stemmer.stem();
assertTrue(result);
assertFalse(stemmer.getCurrent().endsWith("كما"));
}

@Test
public void testSuffixNounStep1b_RemovesFinalNoon() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("مكاتبن");
boolean result = stemmer.stem();
assertTrue(result);
assertFalse(stemmer.getCurrent().endsWith("ن"));
}

@Test
public void testSuffixNounStep2a_RemovesAlefSuffix() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("عصا");
boolean result = stemmer.stem();
assertTrue(result);
assertFalse(stemmer.getCurrent().endsWith("\u0627"));
}

@Test
public void testSuffixNounStep2c2_RemovesTaaMarbuta() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("مدرسة");
boolean result = stemmer.stem();
assertTrue(result);
assertFalse(stemmer.getCurrent().endsWith("\u0629"));
}

@Test
public void testSuffixNounStep2b_RemovesAtSuffix() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("مدرسات");
boolean result = stemmer.stem();
assertTrue(result);
assertFalse(stemmer.getCurrent().endsWith("ات"));
}

@Test
public void testSuffixVerbStep2b_RemovesTamSuffix() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("كتبتم");
boolean result = stemmer.stem();
assertTrue(result);
assertFalse(stemmer.getCurrent().endsWith("تم"));
}

@Test
public void testSuffixAllAlefMaqsura_ReplacesToYa() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("فتى");
boolean result = stemmer.stem();
assertTrue(result);
assertTrue(stemmer.getCurrent().endsWith("\u064A"));
}

@Test
public void testStemmerWithEmptyInput() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("", stemmer.getCurrent());
}

@Test
public void testStemmerWithNeutralWord_NoChangeOccurs() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("سلام");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("سلام", stemmer.getCurrent());
}

@Test
public void testStemmerWithNonArabicWord_UnchangedOutput() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("test");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("test", stemmer.getCurrent());
}

@Test
public void testStemmerEqualsHashCodeContract() {
arabicStemmer stemmer1 = new arabicStemmer();
arabicStemmer stemmer2 = new arabicStemmer();
assertTrue(stemmer1.equals(stemmer2));
assertEquals(stemmer1.hashCode(), stemmer2.hashCode());
}

@Test
public void testStemmingDoesNotModifyInputReference() {
arabicStemmer stemmer = new arabicStemmer();
String input = "معلمين";
stemmer.setCurrent(input);
boolean result = stemmer.stem();
assertTrue(result);
String output = stemmer.getCurrent();
assertFalse(input == output);
assertEquals("معلمين", input);
}

@Test
public void testSingleLetterInput_NoStemmingOccurs() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("ك");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("ك", stemmer.getCurrent());
}

@Test
public void testShortVerbPrefixWithoutStem_NotRemoved() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("س");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("س", stemmer.getCurrent());
}

@Test
public void testWordWithOnlyPresentationForms_AllConverted() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("\uFE8B\uFE8C\uFE8D\uFE8E");
boolean result = stemmer.stem();
assertTrue(result);
String stemmed = stemmer.getCurrent();
assertFalse(stemmed.contains("\uFE8B"));
assertFalse(stemmed.contains("\uFE8C"));
assertFalse(stemmed.contains("\uFE8D"));
assertFalse(stemmed.contains("\uFE8E"));
}

@Test
public void testAlefMaqsuraInMiddle_NotReplaced() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("رَقىَة");
boolean result = stemmer.stem();
assertTrue(result);
String stemmed = stemmer.getCurrent();
assertTrue(stemmed.contains("ى"));
}

@Test
public void testMixedArabicAndLatin_NoExceptionThrown() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("كِتابbook");
boolean result = stemmer.stem();
assertTrue(result);
String stemmed = stemmer.getCurrent();
assertTrue(stemmed.contains("book"));
assertTrue(stemmed.contains("ك"));
}

@Test
public void testWordOnlyWithDiacritics_Removed() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("\u064B\u064C\u064D\u064E\u064F\u0650\u0651\u0652");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("", stemmer.getCurrent());
}

@Test
public void testStemmedOutputShorterThanInput() {
arabicStemmer stemmer = new arabicStemmer();
String original = "بالمدرسة";
stemmer.setCurrent(original);
boolean result = stemmer.stem();
assertTrue(result);
String stemmed = stemmer.getCurrent();
assertTrue(stemmed.length() < original.length());
}

@Test
public void testPrefixStep3VerbFailure_NoMatchPrefix() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("زكتب");
boolean result = stemmer.stem();
assertTrue(result);
String stemmed = stemmer.getCurrent();
assertEquals("زكتب", stemmed);
}

@Test
public void testSuffixVerbStep2a_FailureCase_ShortWord() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("تن");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("تن", stemmer.getCurrent());
}

@Test
public void testSuffixVerbStep2c_RemovesTamooCorrectly() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("كتبتتمو");
boolean result = stemmer.stem();
assertTrue(result);
String stemmed = stemmer.getCurrent();
assertFalse(stemmed.endsWith("تمو"));
}

@Test
public void testSuffixVerbStep1_MultipleRemovals() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("كتبكمهما");
boolean result = stemmer.stem();
assertTrue(result);
String stemmed = stemmer.getCurrent();
assertFalse(stemmed.contains("كم"));
assertFalse(stemmed.contains("هما"));
}

@Test
public void testEdgeCaseLengthExactlyThree_PrefixNotRemoved() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("كال");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("كال", stemmer.getCurrent());
}

@Test
public void testRecursiveSuffixTrimming_ApplyMoreThanOnce() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("معلمينه");
boolean result = stemmer.stem();
assertTrue(result);
assertFalse(stemmer.getCurrent().endsWith("ين"));
assertFalse(stemmer.getCurrent().endsWith("ه"));
}

@Test
public void testSuffixNounStep2aBoundaryCase_Length4() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("رساي");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("رسا", stemmer.getCurrent());
}

@Test
public void testDisplayArabicAlefLaminaLigatures() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("\uFEFB");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("\u0644\u0627", stemmer.getCurrent());
}

@Test
public void testNonMatchingSuffixPreserved() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("كتابز");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("كتابز", stemmer.getCurrent());
}

@Test
public void testOnlyAlefMaqsuraAsInput_IsMappedToYa() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("ى");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("ي", stemmer.getCurrent());
}

@Test
public void testStemmerWithWhitespaceOnly_ReturnsEmpty() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("   ");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("   ", stemmer.getCurrent());
}

@Test
public void testLigatureMappings_LAM_ALEF_WithHamzaUnder() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("\uFEFA");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("\u0644\u0625", stemmer.getCurrent());
}

@Test
public void testLongVerbWithMultipleSuffixes_RemovesCorrectly() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("يكتبونكما");
boolean result = stemmer.stem();
assertTrue(result);
String output = stemmer.getCurrent();
assertFalse(output.endsWith("كما"));
assertFalse(output.endsWith("ون"));
}

@Test
public void testPrefixStep1_FailureForShortInput() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("\u0623\u0622");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("\u0623\u0622", stemmer.getCurrent());
}

@Test
public void testPrefixStep3bNoun_LengthExactlyThree_NoChange() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("\u0643\u0643\u0644");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("\u0643\u0643\u0644", stemmer.getCurrent());
}

@Test
public void testSuffixNounStep1a_InvalidLength3SuffixIgnored() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("بيتهم");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("بيت", stemmer.getCurrent());
}

@Test
public void testSuffixNounStep2a_Length4Exactly_RemovesSuffix() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("سعيي");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("سعي", stemmer.getCurrent());
}

@Test
public void testSuffixNounStep2c1_InvalidLengthLessThan4() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("كتت");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("كتت", stemmer.getCurrent());
}

@Test
public void testSuffixNounStep2c2_RemovedIfLengthEquals4() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("حبة");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("حب", stemmer.getCurrent());
}

@Test
public void testSuffixVerbStep2a_ExactLengthCutoff4_RemovesSuffix() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("مشيي");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("مشي", stemmer.getCurrent());
}

@Test
public void testSuffixVerbStep2a_LessThanRequiredLength_DoNotRemove() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("فيت");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("فيت", stemmer.getCurrent());
}

@Test
public void testStemmingArabicDigitSequenceAndWord() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("\u0661\u0662\u0663كلمة");
boolean result = stemmer.stem();
assertTrue(result);
assertTrue(stemmer.getCurrent().startsWith("123"));
}

@Test
public void testLaLigatureWithHamzaAbove() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("\uFEF9");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("\u0644\u0623", stemmer.getCurrent());
}

@Test
public void testSuffixVerbStep2c_InvalidLength_NoChange() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("تمو");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("تمو", stemmer.getCurrent());
}

@Test
public void testSuffixVerbStep2c_ValidSuffixTamoo_RemovedFully() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("عربتمو");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("عرب", stemmer.getCurrent());
}

@Test
public void testNoChangesWhenStemmingFixedWord() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("شمس");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("شمس", stemmer.getCurrent());
}

@Test
public void testWordWithMultiplePrefixesAndSuffixes_CleansAllLayers() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("وبالكتابكما");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("كتب", stemmer.getCurrent());
}

@Test
public void testRareLigature_FEFB_RemovedProperly() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("\uFEFB");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("\u0644\u0627", stemmer.getCurrent());
}

@Test
public void testLigatureFEF7_ReplacedCorrectly() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("\uFEF7");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("\u0644\u0623", stemmer.getCurrent());
}

@Test
public void testSimpleNonVerbNounFailsVerbPathExecutesSuffixReplacement() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("كتبكم");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("كتب", stemmer.getCurrent());
}

@Test
public void testPrefixStep2_NotDeletedWhenLengthIsThree() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("فاك");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("فاك", stemmer.getCurrent());
}

@Test
public void testPrefixStep2_EndsWithAlef_Remains() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("فاخا");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("فاخا", stemmer.getCurrent());
}

@Test
public void testPrefixStep3_VerbBranchSkippedWhenNotVerb() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("سكتاب");
boolean result = stemmer.stem();
assertTrue(result);
assertTrue(stemmer.getCurrent().contains("س"));
}

@Test
public void testPrefixStep4Verb_ShortWord_FailsDueToLength() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("يست");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("يست", stemmer.getCurrent());
}

@Test
public void testSuffixNounStep1a_InvalidSuffixDoesNothing() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("كتابزز");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("كتابزز", stemmer.getCurrent());
}

@Test
public void testSuffixNounStep3_FailsOnShortLength() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("في");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("في", stemmer.getCurrent());
}

@Test
public void testSuffixVerbStep1_TooShortToRemove() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("هم");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("هم", stemmer.getCurrent());
}

@Test
public void testSuffixVerbStep2b_FailsWhenLengthTooSmall() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("تم");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("تم", stemmer.getCurrent());
}

@Test
public void testSuffixVerbStep2c_FailureOnNoMatch() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("عملتو");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("عملتو", stemmer.getCurrent());
}

@Test
public void testMultipleDiacriticsPreservedInMiddle() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("مُكْتَب");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("مكتب", stemmer.getCurrent());
}

@Test
public void testEmojiAndArabic_ReturnsArabicStemmed() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("📘 الكتاب");
boolean result = stemmer.stem();
assertTrue(result);
assertTrue(stemmer.getCurrent().contains("كتب") || stemmer.getCurrent().contains("ال"));
}

@Test
public void testIsolatedDiacritic_ReturnsEmptyAfterStem() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("\u064F");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("", stemmer.getCurrent());
}

@Test
public void testSingleLigatureOnly_IsSplitCorrectly() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("\uFEFB");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("\u0644\u0627", stemmer.getCurrent());
}

@Test
public void testOnlyHarakatInput_ReturnsEmpty() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("\u064B\u064E\u0650");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("", stemmer.getCurrent());
}

@Test
public void testStemWithEndCursorMidway_NormalizesAndStems() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("كَتَبُوا");
boolean result = stemmer.stem();
assertTrue(result);
String output = stemmer.getCurrent();
assertTrue(output.length() < 6);
assertTrue(output.contains("كتب"));
}

@Test
public void testWhitespaceBeforeAndAfterIsPreserved() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("  كتابة  ");
boolean result = stemmer.stem();
assertTrue(result);
assertTrue(stemmer.getCurrent().trim().contains("كتب"));
}

@Test
public void testPrefixStep1_Failure_NoMatchAmong() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("زيزي");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("زيزي", stemmer.getCurrent());
}

@Test
public void testMultiStepSuffixRemovalInVerb() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("يكتبونهما");
boolean result = stemmer.stem();
assertTrue(result);
assertTrue(stemmer.getCurrent().length() < 8);
}

@Test
public void testMultiStepPrefixAndSuffix_CompletelyStripped() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("بالكتابتين");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("كتب", stemmer.getCurrent());
}

@Test
public void testStem_OnlyDiacritics_NoOutput() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("\u064E\u064F\u0650\u0651");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("", stemmer.getCurrent());
}

@Test
public void testStem_OnlyPresentationAlefMappedCorrectly() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("\uFE81");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("\u0622", stemmer.getCurrent());
}

@Test
public void testStem_StemmedToEmptyString() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("\u0646\u0627");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("", stemmer.getCurrent());
}

@Test
public void testStem_SuffixMatchesButLengthTooShort_NotRemoved() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("كم");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("كم", stemmer.getCurrent());
}

@Test
public void testStem_WithPrefixAndSuffix_BothApplied() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("بالكتابكما");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("كتب", stemmer.getCurrent());
}

@Test
public void testStem_StemFailOnPrefixStep3_Verb_NotSetAsVerb() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("صكتب");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("صكتب", stemmer.getCurrent());
}

@Test
public void testStem_OnlySuffixRemoved_PrefixIntact() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("والكتبكما");
boolean result = stemmer.stem();
assertTrue(result);
assertTrue(stemmer.getCurrent().startsWith("وال"));
assertFalse(stemmer.getCurrent().endsWith("كما"));
}

@Test
public void testStem_LigatureMappedAndSuffixRemoved() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("\uFEFBكما");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("\u0644\u0627", stemmer.getCurrent());
}

@Test
public void testStem_IncorrectPrefixStep2SkippedBecauseSuffixIsAlef() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("فاا");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("فاا", stemmer.getCurrent());
}

@Test
public void testStem_Checks1PositiveThenPrefixSuffixApplied() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("كالكتابهما");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("كتب", stemmer.getCurrent());
}

@Test
public void testStem_OnlyVirtualTranslation_ReplacingNumbers() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("\u0661\u0662\u0663");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("123", stemmer.getCurrent());
}

@Test
public void testStem_PrefixStep3bWithReplacement() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("ببمدرسة");
boolean result = stemmer.stem();
assertTrue(result);
assertTrue(stemmer.getCurrent().startsWith("ب"));
assertFalse(stemmer.getCurrent().startsWith("بب"));
}

@Test
public void testStem_SuffixStep2aRemovesMultipleMatches() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("بيتيا");
boolean result = stemmer.stem();
assertTrue(result);
assertTrue(stemmer.getCurrent().startsWith("بيت"));
assertFalse(stemmer.getCurrent().endsWith("ي"));
assertFalse(stemmer.getCurrent().endsWith("ا"));
}

@Test
public void testStem_SuffixStep2bMatchesAndRemovesCorrectly() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("مدرسات");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("مدرس", stemmer.getCurrent());
}

@Test
public void testStem_SuffixStep2c1MatchesTaaRemoved() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("كتبت");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("كتب", stemmer.getCurrent());
}

@Test
public void testStem_SuffixStep2c2MatchesTaaMarbutaRemoved() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("مدرسة");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("مدرس", stemmer.getCurrent());
}

@Test
public void testStem_Suffix_all_alef_maqsuraConversion() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("فتى");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("فتي", stemmer.getCurrent());
}

@Test
public void testStem_PrefixStep3aRemoved_PrefixStep2SkippedDueToAlef() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("بالمدارسا");
boolean result = stemmer.stem();
assertTrue(result);
assertFalse(stemmer.getCurrent().startsWith("بال"));
}

@Test
public void testStem_OnlyPrefixStep4VerbApplied_FinalPrefixCheck() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("تستكتب");
boolean result = stemmer.stem();
assertTrue(result);
assertTrue(stemmer.getCurrent().startsWith("است"));
}

@Test
public void testInputWithOnlySpecialChar_NoStemChange() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("؟");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("؟", stemmer.getCurrent());
}

@Test
public void testUnmatchedPrefix_NoReplacementOrDeletion() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("ززكتاب");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("ززكتاب", stemmer.getCurrent());
}

@Test
public void testSuffixNounStep1a_SuffixOfLength3_NotRemoved() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("بيتكم");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("بيتكم", stemmer.getCurrent());
}

@Test
public void testSuffixNounStep2a_AllMatchFormsRemoved() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("حيوي");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("حيو", stemmer.getCurrent());
}

@Test
public void testSuffixNounStep2b_NotRemovedDueToLength() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("هات");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("هات", stemmer.getCurrent());
}

@Test
public void testPrefixAndSuffixCombinationRemoval() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("بالمدرستين");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("مدرس", stemmer.getCurrent());
}

@Test
public void testUnrecognizedPresentationForm_NoOpForAmong0() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("\uFB50");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("\uFB50", stemmer.getCurrent());
}

@Test
public void testSuffixVerbStep1_AppliedWhenLengthGreaterThanLimit() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("شاهدوهما");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("شاهد", stemmer.getCurrent());
}

@Test
public void testSuffixVerbStep2b_IgnoredDueToLengthLessThan5() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("أتم");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("أتم", stemmer.getCurrent());
}

@Test
public void testSuffixVerbStep2c_Type2_MatchedAndRemoved() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("كتبتمو");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("كتب", stemmer.getCurrent());
}

@Test
public void testSuffixVerbStep2a_CompoundSuffix_RemovedFully() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("كتبتما");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("كتب", stemmer.getCurrent());
}

@Test
public void testNormalizePost_HamzaFormsAreUnconditionallyMapped() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("أإؤئ");
boolean result = stemmer.stem();
assertTrue(result);
assertTrue(stemmer.getCurrent().contains("ء"));
}

@Test
public void testStem_WithOnlyLigatureFEF6_IsMappedCorrectly() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("\uFEF6");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("\u0644\u0622", stemmer.getCurrent());
}

@Test
public void testStem_OnlyYaSuffixIsRemoved() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("مصري");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("مصر", stemmer.getCurrent());
}

@Test
public void testStem_NounChecks1Match_LlfPrefixDetectedProperly() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("للبيت");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("بيت", stemmer.getCurrent());
}

@Test
public void testStem_CompoundVerbWithIstPrefixTrimmed() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("يستفهم");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("استفهم", stemmer.getCurrent());
}

@Test
public void testShortWordWithAlefMaqsura_ReplacedToYa() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("فى");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("في", stemmer.getCurrent());
}

@Test
public void testStem_PrefixStep1MapsCompositeAlefToSimple_AlefMadda() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("أآدم");
boolean result = stemmer.stem();
assertTrue(result);
assertTrue(stemmer.getCurrent().startsWith("آ") || stemmer.getCurrent().startsWith("أ"));
}

@Test
public void testStem_EmptyString_NoExceptionThrown() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("", stemmer.getCurrent());
}

@Test
public void testStem_LatinPrefixInferenceIsIgnored() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("ebook");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("ebook", stemmer.getCurrent());
}

@Test
public void testStem_PrefixStep3b_Case3_ReplaceKaf() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("ككلمة");
boolean result = stemmer.stem();
assertTrue(result);
assertTrue(stemmer.getCurrent().startsWith("ك"));
assertFalse(stemmer.getCurrent().startsWith("كك"));
}

@Test
public void testStem_PrefixStep3b_Case2_ReplaceBabbaWithBa() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("ببلد");
boolean result = stemmer.stem();
assertTrue(result);
assertTrue(stemmer.getCurrent().startsWith("ب"));
assertFalse(stemmer.getCurrent().startsWith("بب"));
}

@Test
public void testStem_PrefixStep4Verb_MatchAndSlice() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("يستخرج");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("استخرج", stemmer.getCurrent());
}

@Test
public void testStem_PrefixStep3a_Case1_TooShort_NoRemoval() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("بالك");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("بالك", stemmer.getCurrent());
}

@Test
public void testStem_PrefixStep3a_Case2_ExactLimit_Removes() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("للبيت");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("بيت", stemmer.getCurrent());
}

@Test
public void testStem_SuffixNounStep1a_Case3_Removal() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("كتابكما");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("كتب", stemmer.getCurrent());
}

@Test
public void testStem_SuffixVerbStep1_Case2_LengthLimitOneBelow() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("كتبك");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("كتب", stemmer.getCurrent());
}

@Test
public void testStem_SuffixVerbStep2c_Case1_RemovesWaw() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("كتبو");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("كتب", stemmer.getCurrent());
}

@Test
public void testStem_SuffixVerbStep2a_Alternative_HasTaa() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("كتبتن");
boolean result = stemmer.stem();
assertTrue(result);
assertTrue(stemmer.getCurrent().contains("كتب"));
assertFalse(stemmer.getCurrent().endsWith("تن"));
}

@Test
public void testStem_SuffixVerbStep2b_CaseMatchedRemoved() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("خرجتم");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("خرج", stemmer.getCurrent());
}

@Test
public void testStem_SuffixNounStep1b_MatchAndRemoveNoon() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("عاملين");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("عامل", stemmer.getCurrent());
}

@Test
public void testStem_SuffixNounStep2a_MultipleForms_LastYaRemoved() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("قوي");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("قو", stemmer.getCurrent());
}

@Test
public void testStem_SuffixNounStep2c1_MinLength4_AllowRemoval() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("فوت");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("فو", stemmer.getCurrent());
}

@Test
public void testStem_SuffixNounStep2c2_MinLength4_RemoveTaaMarbuta() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("قصة");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("قص", stemmer.getCurrent());
}

@Test
public void testStem_SuffixNounStep3_DeleteYaIfLengthAtLeast3() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("ذي");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("ذ", stemmer.getCurrent());
}

@Test
public void testStem_SuffixAllAlefMaqsura_IsMappedToYa() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("هدى");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("هدي", stemmer.getCurrent());
}

@Test
public void testStem_PrefixStep2_MatchesButBlockedDueToAlefEnding() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("فاا");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("فاا", stemmer.getCurrent());
}

@Test
public void testStem_CombinedNormalizationAndPrefixSuffixProcessing() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("\uFE8E\u0644\u0643\u062A\u0628\u0643\u0645\u0627");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("كتب", stemmer.getCurrent());
}

@Test
public void testStem_NormalizationOfHamzaInMiddlePosition() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("سأل");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("سأل", stemmer.getCurrent());
}

@Test
public void testStem_OnlyNormalizationDone_NoSuffixNoPrefixChange() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("\uFE8D");
boolean result = stemmer.stem();
assertTrue(result);
assertEquals("\u0627", stemmer.getCurrent());
}
}
