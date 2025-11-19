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

public class arabicStemmer_2_GPTLLMTest {

@Test
public void testSimpleNormalization() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("كِتَابٌ");
stemmer.stem();
assertEquals("كتاب", stemmer.getCurrent());
}

@Test
public void testNormalizeIndianDigits() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("\u0661\u0662\u0663\u0664");
stemmer.stem();
assertEquals("1234", stemmer.getCurrent());
}

@Test
public void testLamAlefLigature() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("\uFEFB");
stemmer.stem();
assertEquals("لا", stemmer.getCurrent());
}

@Test
public void testAllLamAlefVariantNormalization() {
arabicStemmer stemmer1 = new arabicStemmer();
stemmer1.setCurrent("\uFEF5");
stemmer1.stem();
assertEquals("لآ", stemmer1.getCurrent());
arabicStemmer stemmer2 = new arabicStemmer();
stemmer2.setCurrent("\uFEF7");
stemmer2.stem();
assertEquals("لأ", stemmer2.getCurrent());
arabicStemmer stemmer3 = new arabicStemmer();
stemmer3.setCurrent("\uFEF9");
stemmer3.stem();
assertEquals("لإ", stemmer3.getCurrent());
}

@Test
public void testHamzaNormalizationAlefVariants() {
arabicStemmer stem1 = new arabicStemmer();
stem1.setCurrent("أ");
stem1.stem();
assertEquals("ا", stem1.getCurrent());
arabicStemmer stem2 = new arabicStemmer();
stem2.setCurrent("إ");
stem2.stem();
assertEquals("ا", stem2.getCurrent());
arabicStemmer stem3 = new arabicStemmer();
stem3.setCurrent("آ");
stem3.stem();
assertEquals("ا", stem3.getCurrent());
}

@Test
public void testNormalizationHamzaInMiddle() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("ألف");
stemmer.stem();
assertEquals("ءلف", stemmer.getCurrent());
}

@Test
public void testSuffixRemovalVerb() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("سيفعلون");
stemmer.stem();
String result = stemmer.getCurrent();
assertTrue(result.length() <= "سيفعلون".length());
assertFalse(result.contains("ون"));
}

@Test
public void testPrefixAndSuffixNoun() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("فالكتابونا");
stemmer.stem();
String result = stemmer.getCurrent();
assertFalse(result.contains("فال"));
assertFalse(result.contains("ونا"));
}

@Test
public void testComplexVerbPrefixSuffix() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("وسنستخدمها");
stemmer.stem();
String result = stemmer.getCurrent();
assertNotEquals("وسنستخدمها", result);
assertTrue(result.length() < "وسنستخدمها".length());
}

@Test
public void testShortWordsUnchanged() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("في");
stemmer.stem();
assertEquals("في", stemmer.getCurrent());
}

@Test
public void testDerivedWordFromRoot() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("المكتبة");
stemmer.stem();
assertTrue(stemmer.getCurrent().contains("كتب"));
}

@Test
public void testEqualsWithSameClass() {
arabicStemmer s1 = new arabicStemmer();
arabicStemmer s2 = new arabicStemmer();
assertTrue(s1.equals(s2));
}

@Test
public void testEqualsWithDifferentType() {
arabicStemmer s1 = new arabicStemmer();
assertFalse(s1.equals("notAnArabicStemmer"));
}

@Test
public void testHashCodeMatchesForSameClasses() {
arabicStemmer s1 = new arabicStemmer();
arabicStemmer s2 = new arabicStemmer();
assertEquals(s1.hashCode(), s2.hashCode());
}

@Test
public void testEmptyString() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("");
stemmer.stem();
assertEquals("", stemmer.getCurrent());
}

@Test
public void testAlefMaqsuraConversion() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("\u0649");
stemmer.stem();
assertEquals("ي", stemmer.getCurrent());
}

@Test
public void testMixedCharactersNormalization() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("\uFEF7السلامُ\u064C\u0664\u0623\u0644");
stemmer.stem();
String result = stemmer.getCurrent();
assertFalse(result.contains("\uFEF7"));
assertFalse(result.contains("\u0664"));
}

@Test
public void testSuffixVerbForm() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("كتبوا");
stemmer.stem();
String result = stemmer.getCurrent();
assertFalse(result.contains("وا"));
assertTrue(result.contains("كتب"));
}

@Test
public void testNounTaaMarbuta() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("مدرسة");
stemmer.stem();
assertTrue(stemmer.getCurrent().contains("درس"));
}

@Test
public void testYaSuffixRemoval() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("رسمي");
stemmer.stem();
assertEquals("رسم", stemmer.getCurrent());
}

@Test
public void testHaSuffixRemoval() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("كتابها");
stemmer.stem();
String result = stemmer.getCurrent();
assertTrue(result.contains("كتب") || result.contains("كتاب"));
}

@Test
public void testNonArabicInputPreserved() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("OpenNLP");
stemmer.stem();
assertEquals("OpenNLP", stemmer.getCurrent());
}

@Test
public void testHarakatRemoval() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("\u0643\u0651\u064F\u062A\u064E\u0628");
stemmer.stem();
assertEquals("كتب", stemmer.getCurrent());
}

@Test
public void testSingleCharacterUnchanged() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("أ");
stemmer.stem();
assertEquals("ا", stemmer.getCurrent());
}

@Test
public void testNumberHandling() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("١٢٣");
stemmer.stem();
assertEquals("123", stemmer.getCurrent());
}

@Test
public void testPrefixRemovalVerb() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("سيكتبون");
stemmer.stem();
assertTrue(stemmer.getCurrent().contains("كتب"));
}

@Test
public void testAggressivePrefixSuffixRemoval() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("فسندخلها");
stemmer.stem();
String result = stemmer.getCurrent();
assertTrue(result.contains("دخل") || result.contains("دخ"));
assertFalse(result.contains("فس"));
}

@Test
public void testHarakatRemovedForm() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("\u0643\u0650\u062A\u064E\u0627\u064E\u0628\u064C");
stemmer.stem();
assertEquals("كتاب", stemmer.getCurrent());
}

@Test
public void testPrefixAndSuffixMultipleLevels() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("وسنشاهدكما");
stemmer.stem();
String result = stemmer.getCurrent();
assertTrue(result.contains("شاهد"));
assertFalse(result.contains("كما"));
}

@Test
public void testDifferentFormsFromSameRoot() {
arabicStemmer s1 = new arabicStemmer();
s1.setCurrent("كاتب");
s1.stem();
assertTrue(s1.getCurrent().contains("كتب"));
arabicStemmer s2 = new arabicStemmer();
s2.setCurrent("كتبت");
s2.stem();
assertTrue(s2.getCurrent().contains("كتب"));
arabicStemmer s3 = new arabicStemmer();
s3.setCurrent("مكتوب");
s3.stem();
assertTrue(s3.getCurrent().contains("كتب"));
arabicStemmer s4 = new arabicStemmer();
s4.setCurrent("مكتبة");
s4.stem();
assertTrue(s4.getCurrent().contains("كتب"));
arabicStemmer s5 = new arabicStemmer();
s5.setCurrent("كتابة");
s5.stem();
assertTrue(s5.getCurrent().contains("كتب"));
}

@Test
public void testStemWordWithPrefixJustBelowThreshold() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("كال");
stemmer.stem();
assertEquals("كال", stemmer.getCurrent());
}

@Test
public void testStemWordWithPrefixAtThreshold() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("كالم");
stemmer.stem();
assertEquals("م", stemmer.getCurrent());
}

@Test
public void testStemWithUnrecognizedUnicodeCharactersUnchanged() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("كتاب🙂");
stemmer.stem();
String result = stemmer.getCurrent();
assertTrue(result.contains("🙂"));
}

@Test
public void testSuffixWithLengthJustBelowThresholdShouldNotDelete() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("هم");
stemmer.stem();
assertEquals("هم", stemmer.getCurrent());
}

@Test
public void testPrefixDelAndSubstitutionConflictPath() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("ببا");
stemmer.stem();
assertEquals("با", stemmer.getCurrent());
}

@Test
public void testStemWordWithNoMatchingPrefixOrSuffix() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("صاروخ");
stemmer.stem();
assertEquals("صاروخ", stemmer.getCurrent());
}

@Test
public void testOnlyPrefixRemovalWithoutSuffix() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("بالكتاب");
stemmer.stem();
String stemmed = stemmer.getCurrent();
assertFalse(stemmed.contains("بال"));
}

@Test
public void testOnlySuffixRemovalWithoutPrefix() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("كتابه");
stemmer.stem();
String stemmed = stemmer.getCurrent();
assertTrue(stemmed.contains("كتب") || stemmed.contains("كتاب"));
}

@Test
public void testChainedSuffixRemovalsApplySequentially() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("كتابكم");
stemmer.stem();
String result = stemmer.getCurrent();
assertTrue(result.contains("كتب"));
}

@Test
public void testStemWordWithMultipleHarakat() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("يُكْتَبُ");
stemmer.stem();
String result = stemmer.getCurrent();
assertTrue(result.contains("كتب"));
}

@Test
public void testNormalizerPreservesCorrectAlefPostForm() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("جاء");
stemmer.stem();
String result = stemmer.getCurrent();
assertTrue(result.contains("جا"));
}

@Test
public void testLigatureMidWordNormalization() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("سلام\uFEFBعليكم");
stemmer.stem();
assertTrue(stemmer.getCurrent().contains("سلاملا"));
}

@Test
public void testInputWithOnlyHarakatShouldBeCleanedToEmpty() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("\u064E\u064F\u0650");
stemmer.stem();
assertEquals("", stemmer.getCurrent());
}

@Test
public void testStemWithRepeatedCharactersPrefixSuffix() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("كالكتابكم");
stemmer.stem();
String result = stemmer.getCurrent();
assertTrue(result.contains("كتب"));
}

@Test
public void testStemSingleArabicLetterUnmatched() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("ف");
stemmer.stem();
assertEquals("ف", stemmer.getCurrent());
}

@Test
public void testDiacriticsAndSuffixRemovalTogether() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("كُتِبُوا");
stemmer.stem();
String r = stemmer.getCurrent();
assertTrue(r.contains("كتب"));
}

@Test
public void testSuffixRemovalMinLengthNotMetShouldSkip() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("هما");
stemmer.stem();
assertEquals("هما", stemmer.getCurrent());
}

@Test
public void testSuffixYaaAndTaaCombinedRemoval() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("كاتبتي");
stemmer.stem();
String r = stemmer.getCurrent();
assertTrue(r.contains("كتب"));
}

@Test
public void testMixedPrefixSuffixVerbForm() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("فسيأكلونها");
stemmer.stem();
String stemmed = stemmer.getCurrent();
assertTrue(stemmed.contains("أكل"));
assertFalse(stemmed.contains("ون"));
}

@Test
public void testRareFormSuffixMatchesButTooShortToRemove() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("نا");
stemmer.stem();
assertEquals("نا", stemmer.getCurrent());
}

@Test
public void testSuffixAlefMaqsuraSubstitutionOnly() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("هدى");
stemmer.stem();
assertEquals("هدي", stemmer.getCurrent());
}

@Test
public void testSingleLetterSuffixVerbNoStemPossible() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("ن");
stemmer.stem();
assertEquals("ن", stemmer.getCurrent());
}

@Test
public void testWordExactlyAtLengthBoundaryForPrefix1() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("بكال");
stemmer.stem();
assertEquals("بكال", stemmer.getCurrent());
}

@Test
public void testWordExactlyAtLengthBoundaryForPrefix2() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("بكالخ");
stemmer.stem();
assertEquals("الخ", stemmer.getCurrent());
}

@Test
public void testSuffixChainAllStepsToBaseRoot() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("كتاباتها");
stemmer.stem();
String result = stemmer.getCurrent();
assertTrue(result.contains("كتب") || result.contains("كتاب"));
}

@Test
public void testPrefixDoesNotApplyDueToLengthLimit() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("كال");
stemmer.stem();
assertEquals("كال", stemmer.getCurrent());
}

@Test
public void testSuffixAlefMaqsuraWithoutTriggeringSubstitution() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("هدى");
stemmer.stem();
assertEquals("هدي", stemmer.getCurrent());
}

@Test
public void testPrefixSubstitutionNoLengthAfterChange() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("أأ");
stemmer.stem();
assertEquals("أأ", stemmer.getCurrent());
}

@Test
public void testUnicodeEdgeCharacterIgnored() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("كت❉ب");
stemmer.stem();
assertTrue(stemmer.getCurrent().contains("كت") || stemmer.getCurrent().contains("كتب"));
}

@Test
public void testNumberStartingWordShouldNormalizeDigits() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("١٢٣كتب");
stemmer.stem();
assertTrue(stemmer.getCurrent().startsWith("123"));
assertTrue(stemmer.getCurrent().contains("كتب"));
}

@Test
public void testIsolatedSuffixShouldNotAffectOtherWords() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("كما");
stemmer.stem();
assertEquals("كما", stemmer.getCurrent());
}

@Test
public void testVerbPrefixStep3FollowedByPrefixStep4() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("سيستخدمون");
stemmer.stem();
String result = stemmer.getCurrent();
assertTrue(result.contains("خدم") || result.contains("استخدم") || result.contains("ستخدم"));
assertFalse(result.contains("ون"));
}

@Test
public void testSuffixNounStepsWithSequentialApplicationAndRollbackFallback() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("مدرستهما");
stemmer.stem();
String result = stemmer.getCurrent();
assertTrue(result.contains("درس"));
assertFalse(result.contains("هما"));
}

@Test
public void testPrefixAndSuffixCombinationMinimumThresholds() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("كالكتبكم");
stemmer.stem();
String result = stemmer.getCurrent();
assertTrue(result.contains("كتب"));
}

@Test
public void testWordWithOnlyUndiacriticAlefShouldPassWithoutChange() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("ا");
stemmer.stem();
assertEquals("ا", stemmer.getCurrent());
}

@Test
public void testSuffixVerb2cTriggersCase2WithMaxLengthCheck() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("كتموا");
stemmer.stem();
String result = stemmer.getCurrent();
assertTrue(result.contains("كتم"));
}

@Test
public void testSuffixNounTaaMarbutaVariantRemoved() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("مدرسه");
stemmer.stem();
String result = stemmer.getCurrent();
assertTrue(result.contains("درس"));
}

@Test
public void testPrefixChainBlockedDueToVerbFalse() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("ستكتبونها");
stemmer.stem();
String result = stemmer.getCurrent();
assertFalse(result.contains("ست"));
assertFalse(result.contains("ون"));
}

@Test
public void testHaltingSuffixChainWhenNoMatchFound() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("كتابسون");
stemmer.stem();
assertEquals("كتابسون", stemmer.getCurrent());
}

@Test
public void testPrefixExactLengthMatchPassed() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("بالكتب");
stemmer.stem();
String result = stemmer.getCurrent();
assertTrue(result.contains("كتب"));
}

@Test
public void testPrefixStep1MatchButTooShortToReplace() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("أأ");
stemmer.stem();
assertEquals("أأ", stemmer.getCurrent());
}

@Test
public void testPrefixStep2MatchButRejectedDueToAlefPresence() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("فا");
stemmer.stem();
assertEquals("فا", stemmer.getCurrent());
}

@Test
public void testPrefixStep3bRejectDueToLength() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("بب");
stemmer.stem();
assertEquals("بب", stemmer.getCurrent());
}

@Test
public void testPrefixStep3aMatchDeletePath() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("كالكتب");
stemmer.stem();
String result = stemmer.getCurrent();
assertTrue(result.contains("كتب"));
}

@Test
public void testInvalidUnicodePrefixIgnored() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("𞸀كتاب");
stemmer.stem();
String result = stemmer.getCurrent();
assertTrue(result.contains("كتب") || result.contains("كتاب"));
}

@Test
public void testMatchingPrefixFollowedByUnremovableSuffix() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("بالقلموو");
stemmer.stem();
String result = stemmer.getCurrent();
assertTrue(result.contains("قلم"));
}

@Test
public void testSuffixStep2bMatchButLengthCheckFails() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("ات");
stemmer.stem();
assertEquals("ات", stemmer.getCurrent());
}

@Test
public void testSuffixNounStep1bTooShortToRemoveSuffix() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("من");
stemmer.stem();
assertEquals("من", stemmer.getCurrent());
}

@Test
public void testSuffixAllAlefMaqsuraApplies() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("يحيى");
stemmer.stem();
assertEquals("يحيي", stemmer.getCurrent());
}

@Test
public void testSuffixVerb2aExactLengthMatchAccepted() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("كتبتا");
stemmer.stem();
assertTrue(stemmer.getCurrent().contains("كتب"));
}

@Test
public void testSuffixVerb2bMatchButTooShort() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("تم");
stemmer.stem();
assertEquals("تم", stemmer.getCurrent());
}

@Test
public void testSuffixVerb2cCase2Applied() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("تتموا");
stemmer.stem();
String result = stemmer.getCurrent();
assertFalse(result.contains("موا"));
}

@Test
public void testMultipleSpecialAlefNormalizationInSingleWord() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("\u0622\u0623\u0625\u0624\u0626");
stemmer.stem();
assertEquals("ااواي", stemmer.getCurrent());
}

@Test
public void testNormalizeLigatureWithDiacritic() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("\uFEFB\u064F");
stemmer.stem();
assertEquals("لا", stemmer.getCurrent());
}

@Test
public void testStemUnknownCombosReturnsUnchanged() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("زقزوق");
stemmer.stem();
assertEquals("زقزوق", stemmer.getCurrent());
}

@Test
public void testLongVerbFormTriggersMultipleRemovals() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("وسنستخدمهمو");
stemmer.stem();
assertTrue(stemmer.getCurrent().contains("خدم"));
}

@Test
public void testBoundaryCasePrefixStep4AcceptsLength4() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("تست");
stemmer.stem();
assertEquals("تست", stemmer.getCurrent());
}

@Test
public void testPrefixStep4AppliesOnLength5() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("تستك");
stemmer.stem();
assertEquals("استك", stemmer.getCurrent());
}

@Test
public void testSuffixNounStep2c1TriggeringRemovalExactLength() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("نبتت");
stemmer.stem();
assertTrue(stemmer.getCurrent().contains("نب"));
}

@Test
public void testStemNonArabicLatinContent() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("translate");
stemmer.stem();
assertEquals("translate", stemmer.getCurrent());
}

@Test
public void testStemArabicWithLatinInputMix() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("كتاب123");
stemmer.stem();
assertTrue(stemmer.getCurrent().contains("كتاب") || stemmer.getCurrent().contains("كتب"));
assertTrue(stemmer.getCurrent().contains("123"));
}

@Test
public void testRepeatedPrefixPatternHandledOnce() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("بالبالبيت");
stemmer.stem();
String result = stemmer.getCurrent();
assertTrue(result.contains("بيت"));
assertFalse(result.startsWith("بالبال"));
}

@Test
public void testDiacriticsAloneRemovedToEmpty() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("\u064E\u0650\u064F\u0652\u0651");
stemmer.stem();
assertEquals("", stemmer.getCurrent());
}

@Test
public void testStemPreservesMiddleYaAlefForm() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("بيان");
stemmer.stem();
assertTrue(stemmer.getCurrent().contains("بي") || stemmer.getCurrent().contains("بان"));
}

@Test
public void testStemStopsAtCursorBoundaryInSuffixWalk() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("ك");
stemmer.stem();
assertEquals("ك", stemmer.getCurrent());
}

@Test
public void testNonRemovablePrefixWithInternalAlefIsPreserved() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("فاكتب");
stemmer.stem();
assertTrue(stemmer.getCurrent().contains("كتب"));
}

@Test
public void testPrefixStep3VerbRejectsInvalidCaseByLength() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("سي");
stemmer.stem();
assertEquals("سي", stemmer.getCurrent());
}

@Test
public void testSuffixStep2bTriggersButIsTooShort() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("تم");
stemmer.stem();
assertEquals("تم", stemmer.getCurrent());
}

@Test
public void testSuffixStep2aMatchCase3EdgeLength5Rejected() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("كتبون");
stemmer.stem();
assertEquals("كتبون", stemmer.getCurrent());
}

@Test
public void testSuffixStep2aMatchCase3Accepted() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("استخدمون");
stemmer.stem();
assertTrue(stemmer.getCurrent().contains("خدم") || stemmer.getCurrent().contains("استخدم"));
}

@Test
public void testSuffixStep1aRejectsCase1WhenLengthIs3() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("كه");
stemmer.stem();
assertEquals("كه", stemmer.getCurrent());
}

@Test
public void testSuffixNounStep2c2TaaMarbutaAtExactLengthBoundary() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("مدرسة");
stemmer.stem();
assertTrue(stemmer.getCurrent().contains("درس"));
}

@Test
public void testNormalizeMultipleTatweelCharacters() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("مــــدارس");
stemmer.stem();
assertTrue(stemmer.getCurrent().contains("مدارس"));
}

@Test
public void testMultipleArabicDigitsNormalizedInSequence() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("رقم\u0661\u0662\u0663");
stemmer.stem();
assertEquals("رقم123", stemmer.getCurrent());
}

@Test
public void testNormalizationWithLigaturesAndArabicLetterSubstitutions() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("\uFEF9\uFE94\uFEF2");
stemmer.stem();
assertEquals("لإذي", stemmer.getCurrent());
}

@Test
public void testSuffixNounRejectedBecauseIsDefinedFalse() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("كتاب");
stemmer.stem();
assertEquals("كتاب", stemmer.getCurrent());
}

@Test
public void testSuffixNounPrefixNounVerbBothTrueWithNonMatch() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("نعناع");
stemmer.stem();
assertEquals("نعناع", stemmer.getCurrent());
}

@Test
public void testStemMixOfArabicAndEnglishCharacters() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("Schoolكتاب");
stemmer.stem();
String current = stemmer.getCurrent();
assertTrue(current.contains("School"));
assertTrue(current.contains("كتب") || current.contains("كتاب"));
}

@Test
public void testSuffixStep1aTriggersCase3ExactMatchLength6() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("كلماتكما");
stemmer.stem();
assertTrue(stemmer.getCurrent().contains("كلم"));
}

@Test
public void testRejectPrefixChainDueToVerbFalse() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("ستفعل");
stemmer.stem();
assertTrue(stemmer.getCurrent().contains("فعل") || stemmer.getCurrent().contains("تفعل"));
}

@Test
public void testSingleNonMatchingArabicCharacterUnchanged() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("ز");
stemmer.stem();
assertEquals("ز", stemmer.getCurrent());
}

@Test
public void testStemPartialVerbThatAvoidsStep4BecauseTooShort() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("تست");
stemmer.stem();
assertEquals("تست", stemmer.getCurrent());
}

@Test
public void testPrefixStep3b_KafDoubleRemoved() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("كككتاب");
stemmer.stem();
String result = stemmer.getCurrent();
assertTrue(result.contains("كتاب"));
}

@Test
public void testHamzaReplacementInPrefixNormalization() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("أأستاذ");
stemmer.stem();
String result = stemmer.getCurrent();
assertTrue(result.contains("استاذ") || result.contains("ستاذ"));
}

@Test
public void testSuffixStep2a_YaMatchExactBoundary() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("عادي");
stemmer.stem();
assertEquals("عاد", stemmer.getCurrent());
}

@Test
public void testSuffixStep2a_AlefMatchExactBoundary() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("دعا");
stemmer.stem();
assertEquals("دع", stemmer.getCurrent());
}

@Test
public void testSuffixStep2a_WawMatchExactBoundary() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("عدو");
stemmer.stem();
assertEquals("عد", stemmer.getCurrent());
}

@Test
public void testPrefixStep3a_WithDefinedArticleAndAcceptableLength() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("كالمعلم");
stemmer.stem();
String result = stemmer.getCurrent();
assertTrue(result.contains("علم"));
}

@Test
public void testPrefixStep2DeletedFOnlyWhenLengthValid() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("فاكتب");
stemmer.stem();
assertEquals("فاكتب", stemmer.getCurrent());
}

@Test
public void testPrefixStep2DeletedWawWhenValid() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("وكتب");
stemmer.stem();
assertEquals("كتب", stemmer.getCurrent());
}

@Test
public void testSuffixNounStep3_YaRemovedIfLengthOkay() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("ساعي");
stemmer.stem();
assertEquals("ساع", stemmer.getCurrent());
}

@Test
public void testSuffixNounStep2c2_TaaMarbutaMatch() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("مدرسة");
stemmer.stem();
String result = stemmer.getCurrent();
assertTrue(result.contains("درس"));
}

@Test
public void testSuffixVerbStep1_SubjectSuffixRemoval_shortWordSkipped() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("هما");
stemmer.stem();
assertEquals("هما", stemmer.getCurrent());
}

@Test
public void testPrefixSuffixChain_BothAppliedFully() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("بالكتابكما");
stemmer.stem();
String result = stemmer.getCurrent();
assertTrue(result.contains("كتب"));
}

@Test
public void testSuffixStep1a_Case1_KafSuffix() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("كتابك");
stemmer.stem();
assertTrue(stemmer.getCurrent().contains("كتب"));
}

@Test
public void testSuffixStep1a_Case2_KumSuffix() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("كتابكم");
stemmer.stem();
String result = stemmer.getCurrent();
assertTrue(result.contains("كتب") || result.contains("كتاب"));
}

@Test
public void testSuffixStep1a_Case3_KumaSuffixLength6() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("كتابكما");
stemmer.stem();
assertTrue(stemmer.getCurrent().contains("كتب"));
}

@Test
public void testSuffixStep2b_TatSuffixRejectedBelowLength5() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("هات");
stemmer.stem();
assertEquals("هات", stemmer.getCurrent());
}

@Test
public void testSuffixStep2c1_TaaSuffixRemovedBoundary() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("قلمت");
stemmer.stem();
assertEquals("قلم", stemmer.getCurrent());
}

@Test
public void testSuffixVerbStep2c_Case1_Length4Accepted() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("كتبوا");
stemmer.stem();
String result = stemmer.getCurrent();
assertTrue(result.contains("كتب"));
}

@Test
public void testSuffixVerbStep2c_Case2_Length6Accepted() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("استخدمتمو");
stemmer.stem();
String result = stemmer.getCurrent();
assertTrue(result.contains("خدم"));
}

@Test
public void testSuffixNounTaaMarbutaConvertedToTaa() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("حسنة");
stemmer.stem();
assertTrue(stemmer.getCurrent().contains("حسن"));
}

@Test
public void testEndingYaOnlyRemovedWhenLengthAllowing() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("هدي");
stemmer.stem();
assertEquals("هد", stemmer.getCurrent());
}

@Test
public void testPrefixNormalizationOnlyWithoutSuffixChange() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("\uFEAEكتاب");
stemmer.stem();
assertTrue(stemmer.getCurrent().contains("كتاب"));
}

@Test
public void testSuffixRemovalChainStopsWhenLengthTooShort() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("كما");
stemmer.stem();
assertEquals("كما", stemmer.getCurrent());
}

@Test
public void testStemmingRejectsVerbSuffixWhenVerbFlagFalse() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("تستخدمونها");
stemmer.stem();
assertTrue(stemmer.getCurrent().contains("خدم") || stemmer.getCurrent().contains("ستخدم"));
}

@Test
public void testReversalOfVerbAndNounFlagSuppressSuffixRemoval() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("دخلتما");
stemmer.stem();
assertTrue(stemmer.getCurrent().contains("دخل"));
}

@Test
public void testSingleShaddaCharacterRemoved() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("\u0651");
stemmer.stem();
assertEquals("", stemmer.getCurrent());
}

@Test
public void testStandaloneTatweelCharacterRemoved() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("\u0640");
stemmer.stem();
assertEquals("", stemmer.getCurrent());
}

@Test
public void testSuffixChainStopsAfterOneMatchOnly() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("كتاباتكم");
stemmer.stem();
assertTrue(stemmer.getCurrent().contains("كتب"));
}

@Test
public void testStemLongFormWithMultipleSuffixMatchesSequentially() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("مكتوباتكما");
stemmer.stem();
String result = stemmer.getCurrent();
assertTrue(result.contains("كتب"));
}

@Test
public void testPrefixAndSuffixChainYieldsShortOutput() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("وبالمدارسكما");
stemmer.stem();
String output = stemmer.getCurrent();
assertTrue(output.length() < "وبالمدارسكما".length());
assertTrue(output.contains("درس") || output.contains("مدرس"));
}

@Test
public void testEdgePrefixStep3bCase3_kafkafChangeToKaf() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("ككمدرسة");
stemmer.stem();
String result = stemmer.getCurrent();
assertTrue(result.contains("درس"));
}

@Test
public void testPrefixStep3bCase2_baWithReplacement() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("ببمدرسة");
stemmer.stem();
String result = stemmer.getCurrent();
assertTrue(result.contains("درس"));
}

@Test
public void testPrefixNonMatchBacktracksCleanly() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("غكشمدرسة");
stemmer.stem();
assertTrue(stemmer.getCurrent().contains("مدرس"));
}

@Test
public void testSuffixNounStep2aRejectedBelowLength() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("هي");
stemmer.stem();
assertEquals("هي", stemmer.getCurrent());
}

@Test
public void testPrefixStep4AppliedForKnownPattern() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("تستفيدون");
stemmer.stem();
assertTrue(stemmer.getCurrent().contains("فيد"));
assertFalse(stemmer.getCurrent().startsWith("تست"));
}

@Test
public void testSuffixNounStep2bEtPluralSuffixExactLengthEdge() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("كلمات");
stemmer.stem();
assertTrue(stemmer.getCurrent().contains("كلم"));
}

@Test
public void testSuffixNounStep2c1_TaMarbutaTaaConflictHandled() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("جميلةت");
stemmer.stem();
assertTrue(stemmer.getCurrent().contains("جمل"));
}

@Test
public void testSuffixNounStep2c2WithoutMatchSkips() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("جامع");
stemmer.stem();
assertEquals("جامع", stemmer.getCurrent());
}

@Test
public void testNormalizationAndPrefixMatchCombined() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("ﺍﻟﻜﺘﺎﺏ");
stemmer.stem();
String result = stemmer.getCurrent();
assertTrue(result.contains("كتب"));
}

@Test
public void testPrefixStep1SubstitutionToAlef() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("أآ");
stemmer.stem();
assertEquals("أآ", stemmer.getCurrent());
}

@Test
public void testPrefixStep1Case4AppliedReplacement() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("أإخراج");
stemmer.stem();
assertTrue(stemmer.getCurrent().contains("خراج"));
}
}
