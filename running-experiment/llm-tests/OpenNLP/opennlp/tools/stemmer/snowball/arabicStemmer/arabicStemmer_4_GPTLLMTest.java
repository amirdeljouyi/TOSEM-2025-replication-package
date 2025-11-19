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

public class arabicStemmer_4_GPTLLMTest {

@Test
public void testNormalizePreRemovesTatweel() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("ســـــلام");
stemmer.stem();
assertEquals("سلام", stemmer.getCurrent());
}

@Test
public void testNormalizePreRemovesHarakat() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("سَلَام");
stemmer.stem();
assertEquals("سلام", stemmer.getCurrent());
}

@Test
public void testNormalizeArabicIndicDigits() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("١٢٣");
stemmer.stem();
assertEquals("123", stemmer.getCurrent());
}

@Test
public void testNormalizeLigatureLamAlef() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("ﻻ");
stemmer.stem();
assertEquals("لا", stemmer.getCurrent());
}

@Test
public void testNormalizePostAlefVariantsToHamza() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("سآلام");
stemmer.stem();
assertEquals("سلام", stemmer.getCurrent());
}

@Test
public void testPrefixStepAlRemovesAlIfDefinedAndLargeEnough() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("المدرسة");
stemmer.stem();
assertEquals("مدرس", stemmer.getCurrent());
}

@Test
public void testPrefixShortWordsUnaffected() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("كال");
stemmer.stem();
assertEquals("كال", stemmer.getCurrent());
}

@Test
public void testPrefixMorphologicalFormsNormalizeVariants() {
arabicStemmer stemmer1 = new arabicStemmer();
stemmer1.setCurrent("إمل");
stemmer1.stem();
assertEquals("امل", stemmer1.getCurrent());
arabicStemmer stemmer2 = new arabicStemmer();
stemmer2.setCurrent("آمل");
stemmer2.stem();
assertEquals("امل", stemmer2.getCurrent());
}

@Test
public void testPrefixSimpleVerbFaRemovedIfLongEnough() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("فكتب");
stemmer.stem();
assertEquals("كتب", stemmer.getCurrent());
}

@Test
public void testPrefixStep3aNounRemovesCommonPrefixes() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("بالباب");
stemmer.stem();
assertEquals("باب", stemmer.getCurrent());
}

@Test
public void testPrefixStep3bNounDoubleLetterPrefixNormalized() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("ببباب");
stemmer.stem();
assertEquals("باب", stemmer.getCurrent());
}

@Test
public void testPrefixStep4VerbMatchesAndReplaces() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("يستعمل");
stemmer.stem();
assertEquals("استعمل", stemmer.getCurrent());
}

@Test
public void testSuffixNounStep1aPossessive() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("كتابه");
stemmer.stem();
assertEquals("كتاب", stemmer.getCurrent());
}

@Test
public void testSuffixNounStep2c1TaaRemoved() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("مدرست");
stemmer.stem();
assertEquals("مدرس", stemmer.getCurrent());
}

@Test
public void testSuffixNounStep2bPluralRemoved() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("مدرسات");
stemmer.stem();
assertEquals("مدرس", stemmer.getCurrent());
}

@Test
public void testSuffixVerbStep1And2aRemoveSuffixes() {
arabicStemmer stemmer1 = new arabicStemmer();
stemmer1.setCurrent("كتبنا");
stemmer1.stem();
assertEquals("كتب", stemmer1.getCurrent());
arabicStemmer stemmer2 = new arabicStemmer();
stemmer2.setCurrent("كتبتما");
stemmer2.stem();
assertEquals("كتب", stemmer2.getCurrent());
}

@Test
public void testSuffixVerbStep2cRemovesSuffix() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("كتتمو");
stemmer.stem();
assertEquals("كت", stemmer.getCurrent());
}

@Test
public void testAlifMaqsuraConvertsToYe() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("هدى");
stemmer.stem();
assertEquals("هدي", stemmer.getCurrent());
}

@Test
public void testShortWordsRemainUnchanged() {
arabicStemmer stemmer1 = new arabicStemmer();
stemmer1.setCurrent("هو");
stemmer1.stem();
assertEquals("هو", stemmer1.getCurrent());
arabicStemmer stemmer2 = new arabicStemmer();
stemmer2.setCurrent("في");
stemmer2.stem();
assertEquals("في", stemmer2.getCurrent());
arabicStemmer stemmer3 = new arabicStemmer();
stemmer3.setCurrent("لا");
stemmer3.stem();
assertEquals("لا", stemmer3.getCurrent());
}

@Test
public void testEmptyStringReturnsEmpty() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("");
stemmer.stem();
assertEquals("", stemmer.getCurrent());
}

@Test
public void testSingleLetterInputRemainsUnchanged() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("س");
stemmer.stem();
assertEquals("س", stemmer.getCurrent());
}

@Test
public void testEqualsWithSameType() {
arabicStemmer stemmer1 = new arabicStemmer();
arabicStemmer stemmer2 = new arabicStemmer();
assertTrue(stemmer1.equals(stemmer2));
}

@Test
public void testEqualsWithSameInstance() {
arabicStemmer stemmer = new arabicStemmer();
assertTrue(stemmer.equals(stemmer));
}

@Test
public void testEqualsWithDifferentType() {
arabicStemmer stemmer = new arabicStemmer();
assertFalse(stemmer.equals("not_astemmer"));
}

@Test
public void testHashCodeConsistency() {
arabicStemmer stemmer1 = new arabicStemmer();
arabicStemmer stemmer2 = new arabicStemmer();
assertEquals(stemmer1.hashCode(), stemmer2.hashCode());
}

@Test
public void testWordUnaffectedIfAlreadyStemmed() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("كتب");
stemmer.stem();
assertEquals("كتب", stemmer.getCurrent());
}

@Test
public void testWordUnaffectedIfNoSuffixMatch() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("قلم");
stemmer.stem();
assertEquals("قلم", stemmer.getCurrent());
}

@Test
public void testPrefixStep1WithShortWordShouldNotStem() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("أأ");
stemmer.stem();
assertEquals("أأ", stemmer.getCurrent());
}

@Test
public void testPrefixStep2FailsWhenLengthTooShort() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("وف");
stemmer.stem();
assertEquals("وف", stemmer.getCurrent());
}

@Test
public void testPrefixStep2FailsWhenNextCharIsAlef() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("وفا");
stemmer.stem();
assertEquals("وفا", stemmer.getCurrent());
}

@Test
public void testPrefixStep3bNounFailureWhenTooShort() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("بب");
stemmer.stem();
assertEquals("بب", stemmer.getCurrent());
}

@Test
public void testPrefixStep3VerbFailsWhenLengthTooShort() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("سيه");
stemmer.stem();
assertEquals("سيه", stemmer.getCurrent());
}

@Test
public void testSuffixNounStep1aFailureWhenLengthIsShort() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("بك");
stemmer.stem();
assertEquals("بك", stemmer.getCurrent());
}

@Test
public void testSuffixNounStep2c2RejectionForShortWord() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("هة");
stemmer.stem();
assertEquals("هة", stemmer.getCurrent());
}

@Test
public void testSuffixVerbStep1FailsWhenLengthIsShort() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("هكم");
stemmer.stem();
assertEquals("هكم", stemmer.getCurrent());
}

@Test
public void testSuffixVerbStep2bFailsWhenLengthTooShort() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("تمو");
stemmer.stem();
assertEquals("تمو", stemmer.getCurrent());
}

@Test
public void testSuffixVerbStep2cFailsWhenLengthTooShort() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("و");
stemmer.stem();
assertEquals("و", stemmer.getCurrent());
}

@Test
public void testSuffixNounStep2aFallbackToC2WhenDefinedFlagFalse() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("سيات");
stemmer.stem();
assertEquals("س", stemmer.getCurrent());
}

@Test
public void testSuffixNounStep3RejectsShortInput() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("ي");
stemmer.stem();
assertEquals("ي", stemmer.getCurrent());
}

@Test
public void testStemAfterPrefixAndSuffixRemoval() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("فبالمدرساتهمو");
stemmer.stem();
assertEquals("مدرس", stemmer.getCurrent());
}

@Test
public void testPrefixAndSuffixInteractionOnVerb() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("سيكتبون");
stemmer.stem();
assertEquals("كتب", stemmer.getCurrent());
}

@Test
public void testMultipleSuffixesForVerbStripping() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("كتبتمو");
stemmer.stem();
assertEquals("كتب", stemmer.getCurrent());
}

@Test
public void testUnmatchedUnicodeCharactersRemainUnaffected() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("𞸁𞸊");
stemmer.stem();
assertEquals("𞸁𞸊", stemmer.getCurrent());
}

@Test
public void testPrefixWithNoMatchingAmongEntryIsIgnored() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("زكالمدرسة");
stemmer.stem();
assertEquals("مدرس", stemmer.getCurrent());
}

@Test
public void testWordWithNoStemChange() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("ذهب");
stemmer.stem();
assertEquals("ذهب", stemmer.getCurrent());
}

@Test
public void testNormalizedTaaMaqsuraToYaAtEnd() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("دنيا");
stemmer.stem();
assertEquals("دني", stemmer.getCurrent());
}

@Test
public void testSuffixNounStep2aRemovesWaw() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("كتبوا");
stemmer.stem();
assertEquals("كتب", stemmer.getCurrent());
}

@Test
public void testSuffixNounStep2aRemovesAlef() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("عصا");
stemmer.stem();
assertEquals("عص", stemmer.getCurrent());
}

@Test
public void testSuffixNounStep2aRemovesYa() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("نبي");
stemmer.stem();
assertEquals("نب", stemmer.getCurrent());
}

@Test
public void testSuffixNounStep2bHandlesDualForm() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("مدرستان");
stemmer.stem();
assertEquals("مدرس", stemmer.getCurrent());
}

@Test
public void testSuffixVerbStep2aYakthubaniConjugation() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("يكتباني");
stemmer.stem();
assertEquals("كتب", stemmer.getCurrent());
}

@Test
public void testSuffixVerbStep2aTaktubnaConjugation() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("تكتبنا");
stemmer.stem();
assertEquals("كتب", stemmer.getCurrent());
}

@Test
public void testSuffixVerbStep2aYaktuboonConjugation() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("يكتبون");
stemmer.stem();
assertEquals("كتب", stemmer.getCurrent());
}

@Test
public void testNormalizeMultipleFinaFormsConverted() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("ﻛﺘﺐ");
stemmer.stem();
assertEquals("كتب", stemmer.getCurrent());
}

@Test
public void testHandlesMixedPresentationAndStandardForms() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("ﻟﻠﻤﺪﺭﺳﺔ");
stemmer.stem();
assertEquals("مدرس", stemmer.getCurrent());
}

@Test
public void testRejectsPrefixStep3bNounWhenLengthTooShort() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("بب");
stemmer.stem();
assertEquals("بب", stemmer.getCurrent());
}

@Test
public void testPrefixStep4VerbDoesNotSetVerbFlagWhenTooShort() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("يست");
stemmer.stem();
assertEquals("يست", stemmer.getCurrent());
}

@Test
public void testSuffixNounStep1bOnlyWhenLengthGreaterThanFive() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("كتبن");
stemmer.stem();
assertEquals("كتبن", stemmer.getCurrent());
}

@Test
public void testSuffixAllAlefMaqsuraWithLongerWord() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("هدى");
stemmer.stem();
assertEquals("هدي", stemmer.getCurrent());
}

@Test
public void testStemAllRulesApplyInOrder() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("وسأتكتبونها");
stemmer.stem();
assertEquals("كتب", stemmer.getCurrent());
}

@Test
public void testUnicodeShortFormAlef() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("سٰم");
stemmer.stem();
assertEquals("سم", stemmer.getCurrent());
}

@Test
public void testPreservesNumeralsEmbedded() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("١٢٣كلمات");
stemmer.stem();
assertEquals("123كلم", stemmer.getCurrent());
}

@Test
public void testHandlesTaaMarbutaAndAlefSuffix() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("مدرسةا");
stemmer.stem();
assertEquals("مدرس", stemmer.getCurrent());
}

@Test
public void testHamzaRemovalThroughNormalizePostCombos() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("جاء");
stemmer.stem();
assertEquals("جا", stemmer.getCurrent());
}

@Test
public void testNestedPrefixSuffixWithDualForms() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("وسيراها");
stemmer.stem();
assertEquals("ر", stemmer.getCurrent());
}

@Test
public void testStemWithTatweelAndUnicodeAlefPresentationForm() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("ســـــﻻم");
stemmer.stem();
assertEquals("سلام", stemmer.getCurrent());
}

@Test
public void testStemWithUndefinedPrefixGroup() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("زكالمدرسة");
stemmer.stem();
assertEquals("مدرس", stemmer.getCurrent());
}

@Test
public void testSuffixVerbStep2aWithShortestValidWordLength() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("كتبنا");
stemmer.stem();
assertEquals("كتب", stemmer.getCurrent());
}

@Test
public void testSuffixVerbStep2aFallbackToStep2b() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("كتبتتم");
stemmer.stem();
assertEquals("كتب", stemmer.getCurrent());
}

@Test
public void testMultipleConsecutiveSuffixMatchesInVerbMode() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("كتبتماها");
stemmer.stem();
assertEquals("كتب", stemmer.getCurrent());
}

@Test
public void testSuffixNounStep2c1AndStep3Sequentially() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("مدرستي");
stemmer.stem();
assertEquals("مدرس", stemmer.getCurrent());
}

@Test
public void testInputWithOnlySuffixNoPrefix() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("درسها");
stemmer.stem();
assertEquals("درس", stemmer.getCurrent());
}

@Test
public void testInputWithOnlyPrefixNoSuffix() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("فالكتاب");
stemmer.stem();
assertEquals("كتب", stemmer.getCurrent());
}

@Test
public void testStemUnaffectedWhenPrefixIsSingleCharacterAndInvalid() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("زكتاب");
stemmer.stem();
assertEquals("كتب", stemmer.getCurrent());
}

@Test
public void testSuffixNounWithMultipleFallbackConditionsFailsAll() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("كتابسى");
stemmer.stem();
assertEquals("كتابسى", stemmer.getCurrent());
}

@Test
public void testPrefixAndSuffixWithAmbiguousBoundaries() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("بكالكتبنا");
stemmer.stem();
assertEquals("كتب", stemmer.getCurrent());
}

@Test
public void testStemWithShortCombinationThatShouldNotTriggerAnyRule() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("ال");
stemmer.stem();
assertEquals("ال", stemmer.getCurrent());
}

@Test
public void testStemWithOnlySpecialCharacterShouldBeDeleted() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("ـ");
stemmer.stem();
assertEquals("", stemmer.getCurrent());
}

@Test
public void testStemWithOnlyHarakatShouldBeDeleted() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("ٌَّ");
stemmer.stem();
assertEquals("", stemmer.getCurrent());
}

@Test
public void testStemWithCombinationOfArabicDigitsAndTashkeel() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("١٢٣ٌَّ");
stemmer.stem();
assertEquals("123", stemmer.getCurrent());
}

@Test
public void testStemWithValidPrefixButShortStemShouldNotStrip() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("بالأ");
stemmer.stem();
assertEquals("بال", stemmer.getCurrent());
}

@Test
public void testStemComplexVerbWithAllSuffixesAndFuturePrefix() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("فسنستعملونها");
stemmer.stem();
assertEquals("استعمل", stemmer.getCurrent());
}

@Test
public void testStemWithPrefixStep3bFallbackReplacement() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("ببكتاب");
stemmer.stem();
assertEquals("كتاب", stemmer.getCurrent());
}

@Test
public void testStemWithPrefixStep3bFallbackKReplacement() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("كككلام");
stemmer.stem();
assertEquals("كلام", stemmer.getCurrent());
}

@Test
public void testStemInputThatMatchesNothingStaysUnchanged() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("غيمة");
stemmer.stem();
assertEquals("غيم", stemmer.getCurrent());
}

@Test
public void testStemWithStandaloneArabicLetterVariants() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("ﺍﺏﺝ");
stemmer.stem();
assertEquals("ابج", stemmer.getCurrent());
}

@Test
public void testStemEmojiPreservation() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("حب💖");
stemmer.stem();
assertEquals("حب💖", stemmer.getCurrent());
}

@Test
public void testOnlyHamzaLetterShouldRemainUnchangedAfterNormalize() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("ء");
stemmer.stem();
assertEquals("ء", stemmer.getCurrent());
}

@Test
public void testCombinedAlefHamzaFormNormalizedToAlef() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("إبراهيم");
stemmer.stem();
assertEquals("براهيم", stemmer.getCurrent());
}

@Test
public void testSuffixNounStep2FailsWhenDefinedIsFalseAndSuffixTooShort() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("سلمو");
stemmer.stem();
assertEquals("سلمو", stemmer.getCurrent());
}

@Test
public void testPrefixStep3aNounNotAppliedIfNotNoun() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("كالكلام");
stemmer.stem();
assertEquals("كلام", stemmer.getCurrent());
}

@Test
public void testPrefixStep3bNounFailsWhenNoMatchInAmong() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("زكلكتب");
stemmer.stem();
assertEquals("كتب", stemmer.getCurrent());
}

@Test
public void testSuffixVerbStep2bNotAppliedIfBelowMinLength() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("أتم");
stemmer.stem();
assertEquals("أتم", stemmer.getCurrent());
}

@Test
public void testSuffixVerbStep2cOnlyAppliesFirstValidSuffix() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("كتبتتمو");
stemmer.stem();
assertEquals("كتبت", stemmer.getCurrent());
}

@Test
public void testAllFlagsResetProperlyEachCall() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("كتبنا");
stemmer.stem();
String firstResult = stemmer.getCurrent();
stemmer.setCurrent("بابه");
stemmer.stem();
String secondResult = stemmer.getCurrent();
assertEquals("كتب", firstResult);
assertEquals("باب", secondResult);
}

@Test
public void testStemHandlesOnlyDiacriticsAndNormalLetters() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("سَلَامٌ");
stemmer.stem();
assertEquals("سلام", stemmer.getCurrent());
}

@Test
public void testPrefixAndSuffixResultInEmptyStem() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("كالها");
stemmer.stem();
assertEquals("", stemmer.getCurrent());
}

@Test
public void testInputWithRepeatingPrefixFormsReducesProperly() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("بالبالباب");
stemmer.stem();
assertEquals("باب", stemmer.getCurrent());
}

@Test
public void testMultipleValidSuffixesInNounFlowApplyCorrectly() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("كتاباتكم");
stemmer.stem();
assertEquals("كتب", stemmer.getCurrent());
}

@Test
public void testLongInputWithCombinedHarakatAndPrefixSuffix() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("فَسَيَكْتُبُونَهَا");
stemmer.stem();
assertEquals("كتب", stemmer.getCurrent());
}

@Test
public void testHamzaNormalizationAtEndOfWord() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("شيء");
stemmer.stem();
assertEquals("شيء", stemmer.getCurrent());
}

@Test
public void testStemWithMultipleAlefVariantsInWord() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("إستئناف");
stemmer.stem();
assertEquals("استنف", stemmer.getCurrent());
}

@Test
public void testNormalizePreHandlesMultipleFormsInSinglePass() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("ﷲ١٢٣ًٌّْٓٓ");
stemmer.stem();
assertEquals("الله123", stemmer.getCurrent());
}

@Test
public void testStemWithCombinationOfLamAlefFormsAndHarakat() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("ﻟَﻼَﻡ");
stemmer.stem();
assertEquals("لام", stemmer.getCurrent());
}

@Test
public void testPrefixStep3bNounMatchesBiBaReplacesCorrectly() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("بببكتاب");
stemmer.stem();
assertEquals("كتاب", stemmer.getCurrent());
}

@Test
public void testPrefixStep2SliceDelRemovesSingleLetterPrefix() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("وفعل");
stemmer.stem();
assertEquals("فعل", stemmer.getCurrent());
}

@Test
public void testSuffixNounStep2c2DetectsTaaMarbutaAndDeletes() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("مدرسة");
stemmer.stem();
assertEquals("مدرس", stemmer.getCurrent());
}

@Test
public void testSuffixNounStep3DoesNotDeleteBelowLengthThreshold() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("فيي");
stemmer.stem();
assertEquals("في", stemmer.getCurrent());
}

@Test
public void testSuffixVerbStep1RemovesLongestValidSuffixFirst() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("كتبكما");
stemmer.stem();
assertEquals("كتب", stemmer.getCurrent());
}

@Test
public void testSuffixVerbStep2aAppliesDualSuffixFirstThenPlural() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("فعلتن");
stemmer.stem();
assertEquals("فعل", stemmer.getCurrent());
}

@Test
public void testSuffixVerbStep2aFailsDueToMinimumLengthConstraint() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("بنا");
stemmer.stem();
assertEquals("بنا", stemmer.getCurrent());
}

@Test
public void testSuffixNounStep1aLongestMatchApplicable() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("كتابكما");
stemmer.stem();
assertEquals("كتاب", stemmer.getCurrent());
}

@Test
public void testNormalizePostAppliesMultipleHamzaReplacements() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("سإآأل");
stemmer.stem();
assertEquals("سال", stemmer.getCurrent());
}

@Test
public void testPrefixStep1FallbackWhenFindAmongFails() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("أبجد");
stemmer.stem();
assertEquals("ابجد", stemmer.getCurrent());
}

@Test
public void testGeneratedWordWithNonsensicalAffixesStillStems() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("فكالكتاباتكمو");
stemmer.stem();
assertEquals("كتب", stemmer.getCurrent());
}

@Test
public void testWordWithMultipleSequentialSuffixesInVerbMode() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("تكتبونهما");
stemmer.stem();
assertEquals("كتب", stemmer.getCurrent());
}

@Test
public void testSuffixVerbStep2cMatchesMultipleChoices() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("فعلتمو");
stemmer.stem();
assertEquals("فعل", stemmer.getCurrent());
}

@Test
public void testStemWithOnlyShortPrefixAndShortStemFails() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("فا");
stemmer.stem();
assertEquals("فا", stemmer.getCurrent());
}

@Test
public void testNormalizationReplacesUnicodeDigitsAndForms() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("١٢٣٤٥٦٧٨٩٠");
stemmer.stem();
assertEquals("1234567890", stemmer.getCurrent());
}

@Test
public void testMultipleRoundNormalizationAndPrefixSuffixChain() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("فلسيستفعلونها");
stemmer.stem();
assertEquals("فعل", stemmer.getCurrent());
}

@Test
public void testWordWithOnlyArabicDiacriticsDeletedCompletely() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("ًٌٍَُِ");
stemmer.stem();
assertEquals("", stemmer.getCurrent());
}

@Test
public void testPrefixStep4VerbSetsVerbFlagForValidWord() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("يستغفرون");
stemmer.stem();
assertEquals("غفر", stemmer.getCurrent());
}

@Test
public void testPrefixStep3VerbSkipsNonVerbWordWhenNounFlagIsExclusive() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("سيكلمة");
stemmer.stem();
assertEquals("كلم", stemmer.getCurrent());
}

@Test
public void testSuffixNounStep2MatchOnlyOneSuffixNotBoth() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("كتابهما");
stemmer.stem();
assertEquals("كتاب", stemmer.getCurrent());
}

@Test
public void testStemWithPrefixAndSuffixWithOverlap() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("بالكلماتها");
stemmer.stem();
assertEquals("كلم", stemmer.getCurrent());
}

@Test
public void testNormalizeShortFormAlefAndAlefWithHamza() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("إلٰه");
stemmer.stem();
assertEquals("اله", stemmer.getCurrent());
}

@Test
public void testWordWithoutAnyRemovableAffixRemainsUnchanged() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("نور");
stemmer.stem();
assertEquals("نور", stemmer.getCurrent());
}

@Test
public void testPrefixAndSuffixThatTogetherMakeOriginalStem() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("المدرساتها");
stemmer.stem();
assertEquals("مدرس", stemmer.getCurrent());
}

@Test
public void testStemWordWithIdenticalPrefixAndSuffix() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("هاكلامها");
stemmer.stem();
assertEquals("كلم", stemmer.getCurrent());
}

@Test
public void testPrefixStep3aNounRejectedIfLengthConstraintFails() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("كال");
stemmer.stem();
assertEquals("كال", stemmer.getCurrent());
}

@Test
public void testSuffixNounStep1aDoesNotApplyIfLengthIsTooShort() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("بكم");
stemmer.stem();
assertEquals("بكم", stemmer.getCurrent());
}

@Test
public void testSuffixVerbStep2cFailsWhenSuffixPartialAndMismatch() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("كتبتموه");
stemmer.stem();
assertEquals("كتبتموه", stemmer.getCurrent());
}

@Test
public void testWordEndingWithAlefMaqsuraReplacesItWithYeh() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("هدى");
stemmer.stem();
assertEquals("هدي", stemmer.getCurrent());
}

@Test
public void testPrefixNotInAmongListDoesNothing() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("زدفعل");
stemmer.stem();
assertEquals("فعل", stemmer.getCurrent());
}

@Test
public void testSuffixInAmongButTooShortPreventsDeletion() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("كه");
stemmer.stem();
assertEquals("كه", stemmer.getCurrent());
}

@Test
public void testStemPreservesPrimaryRootWhenMixedAffixesPresent() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("فبالكتاباتهمو");
stemmer.stem();
assertEquals("كتب", stemmer.getCurrent());
}

@Test
public void testStemMultipleSequentialPrefixesRemovesFirstValid() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("كالكاللغة");
stemmer.stem();
assertEquals("لغ", stemmer.getCurrent());
}

@Test
public void testStemWithUnicodeLetterVariantsMappedCorrectly() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("ﺍﻟﻜﺘﺎﺏ");
stemmer.stem();
assertEquals("كتب", stemmer.getCurrent());
}

@Test
public void testStemWithStandaloneTatweelOnly() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("ـ");
stemmer.stem();
assertEquals("", stemmer.getCurrent());
}

@Test
public void testStemWithLatinCharactersDoesNotAlterText() {
arabicStemmer stemmer = new arabicStemmer();
stemmer.setCurrent("hello");
stemmer.stem();
assertEquals("hello", stemmer.getCurrent());
}
}
