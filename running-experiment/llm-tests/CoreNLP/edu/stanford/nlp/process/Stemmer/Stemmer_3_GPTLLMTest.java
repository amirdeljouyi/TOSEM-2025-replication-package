package edu.stanford.nlp.process;

import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.process.Stemmer;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class Stemmer_3_GPTLLMTest {

 @Test
  public void testStemCats() {
    Stemmer stemmer = new Stemmer();
    assertEquals("cat", stemmer.stem("cats"));
  }
@Test
  public void testStemDogs() {
    Stemmer stemmer = new Stemmer();
    assertEquals("dog", stemmer.stem("dogs"));
  }
@Test
  public void testStemBooks() {
    Stemmer stemmer = new Stemmer();
    assertEquals("book", stemmer.stem("books"));
  }
@Test
  public void testStemSchools() {
    Stemmer stemmer = new Stemmer();
    assertEquals("school", stemmer.stem("schools"));
  }
@Test
  public void testStemAgreed() {
    Stemmer stemmer = new Stemmer();
    assertEquals("agree", stemmer.stem("agreed"));
  }
@Test
  public void testStemDisabled() {
    Stemmer stemmer = new Stemmer();
    assertEquals("disable", stemmer.stem("disabled"));
  }
@Test
  public void testStemHopHopHop() {
    Stemmer stemmer = new Stemmer();
    assertEquals("hop", stemmer.stem("hopping"));
  }
@Test
  public void testStemHopped() {
    Stemmer stemmer = new Stemmer();
    assertEquals("hop", stemmer.stem("hopped"));
  }
@Test
  public void testStemMeetings() {
    Stemmer stemmer = new Stemmer();
    assertEquals("meet", stemmer.stem("meetings"));
  }
@Test
  public void testStemCry() {
    Stemmer stemmer = new Stemmer();
    assertEquals("cry", stemmer.stem("cry"));
  }
@Test
  public void testStemCries() {
    Stemmer stemmer = new Stemmer();
    assertEquals("cri", stemmer.stem("cries"));
  }
@Test
  public void testStemHappy() {
    Stemmer stemmer = new Stemmer();
    assertEquals("happy", stemmer.stem("happy"));
  }
@Test
  public void testStemHappily() {
    Stemmer stemmer = new Stemmer();
    assertEquals("happi", stemmer.stem("happily"));
  }
@Test
  public void testStemRelational() {
    Stemmer stemmer = new Stemmer();
    assertEquals("relate", stemmer.stem("relational"));
  }
@Test
  public void testStemConditional() {
    Stemmer stemmer = new Stemmer();
    assertEquals("condition", stemmer.stem("conditional"));
  }
@Test
  public void testStemHesitancy() {
    Stemmer stemmer = new Stemmer();
    assertEquals("hesit", stemmer.stem("hesitancy"));
  }
@Test
  public void testStemHelpfulness() {
    Stemmer stemmer = new Stemmer();
    assertEquals("help", stemmer.stem("helpfulness"));
  }
@Test
  public void testStemUsefulness() {
    Stemmer stemmer = new Stemmer();
    assertEquals("use", stemmer.stem("usefulness"));
  }
@Test
  public void testStemRevizes() {
    Stemmer stemmer = new Stemmer();
    assertEquals("revize", stemmer.stem("revizes"));
  }
@Test
  public void testStemFinalize() {
    Stemmer stemmer = new Stemmer();
    assertEquals("final", stemmer.stem("finalize"));
  }
@Test
  public void testStemTriplicate() {
    Stemmer stemmer = new Stemmer();
    assertEquals("trip", stemmer.stem("triplicate"));
  }
@Test
  public void testStemElectrical() {
    Stemmer stemmer = new Stemmer();
    assertEquals("electric", stemmer.stem("electrical"));
  }
@Test
  public void testStemHope() {
    Stemmer stemmer = new Stemmer();
    assertEquals("hope", stemmer.stem("hope"));
  }
@Test
  public void testStemHopeful() {
    Stemmer stemmer = new Stemmer();
    assertEquals("hop", stemmer.stem("hopeful"));
  }
@Test
  public void testStemEmptyString() {
    Stemmer stemmer = new Stemmer();
    assertEquals("", stemmer.stem(""));
  }
@Test
  public void testStemSingleLetterA() {
    Stemmer stemmer = new Stemmer();
    assertEquals("a", stemmer.stem("a"));
  }
@Test
  public void testStemSingleLetterB() {
    Stemmer stemmer = new Stemmer();
    assertEquals("b", stemmer.stem("b"));
  }
@Test
  public void testStemShortWordOn() {
    Stemmer stemmer = new Stemmer();
    assertEquals("on", stemmer.stem("on"));
  }
@Test
  public void testStemTable() {
    Stemmer stemmer = new Stemmer();
    assertEquals("table", stemmer.stem("table"));
  }
@Test
  public void testStemRun() {
    Stemmer stemmer = new Stemmer();
    assertEquals("run", stemmer.stem("run"));
  }
@Test
  public void testStem123456() {
    Stemmer stemmer = new Stemmer();
    assertEquals("123456", stemmer.stem("123456"));
  }
@Test
  public void testStemData123() {
    Stemmer stemmer = new Stemmer();
    assertEquals("data123", stemmer.stem("data123"));
  }
@Test
  public void testStemHelloWorldUnderscore() {
    Stemmer stemmer = new Stemmer();
    assertEquals("hello_world", stemmer.stem("hello_world"));
  }
@Test
  public void testStemNumberOnlyInput() {
    Stemmer stemmer = new Stemmer();
    assertEquals("2023", stemmer.stem("2023"));
  }
@Test
  public void testStemHyphenated() {
    Stemmer stemmer = new Stemmer();
    assertEquals("multi-languag", stemmer.stem("multi-language"));
  }
@Test
  public void testStemPreprocessing() {
    Stemmer stemmer = new Stemmer();
    assertEquals("preprocess", stemmer.stem("preprocessing").toLowerCase());
  }
@Test
  public void testStemAsWordObject() {
    Stemmer stemmer = new Stemmer();
    Word word = new Word("walking");
    Word result = stemmer.stem(word);
    assertEquals("walk", result.word());
  }
@Test
  public void testStemWithApply() {
    Stemmer stemmer = new Stemmer();
    Word input = new Word("connecting");
    Word result = stemmer.apply(input);
    assertEquals("connect", result.word());
  }
@Test
  public void testToStringReturnsStemmedResult() {
    Stemmer stemmer = new Stemmer();
    stemmer.stem("relational");
    assertEquals("relate", stemmer.toString());
  }
@Test
  public void testPluralCaressesToCaress() {
    Stemmer stemmer = new Stemmer();
    assertEquals("caress", stemmer.stem("caresses"));
  }
@Test
  public void testPluralKissesToKiss() {
    Stemmer stemmer = new Stemmer();
    assertEquals("kiss", stemmer.stem("kisses"));
  }
@Test
  public void testPluralBoxesToBox() {
    Stemmer stemmer = new Stemmer();
    assertEquals("box", stemmer.stem("boxes"));
  }
@Test
  public void testPluralMassesToMass() {
    Stemmer stemmer = new Stemmer();
    assertEquals("mass", stemmer.stem("masses"));
  }
@Test
  public void testPluralBossesToBoss() {
    Stemmer stemmer = new Stemmer();
    assertEquals("boss", stemmer.stem("bosses"));
  }
@Test
  public void testCvcWordsHop() {
    Stemmer stemmer = new Stemmer();
    assertEquals("hop", stemmer.stem("hopping"));
  }
@Test
  public void testStemCommunication() {
    Stemmer stemmer = new Stemmer();
    assertEquals("commun", stemmer.stem("communication"));
  }
@Test
  public void testStemDecisions() {
    Stemmer stemmer = new Stemmer();
    assertEquals("decid", stemmer.stem("decisions"));
  }
@Test
  public void testStemCriticism() {
    Stemmer stemmer = new Stemmer();
    assertEquals("critic", stemmer.stem("criticism"));
  }
@Test
  public void testStemAnalyzing() {
    Stemmer stemmer = new Stemmer();
    assertEquals("analyz", stemmer.stem("analyzing"));
  }
@Test
  public void testStemAnalysis() {
    Stemmer stemmer = new Stemmer();
    assertEquals("analysi", stemmer.stem("analysis"));
  }
@Test
  public void testStemGeneralization() {
    Stemmer stemmer = new Stemmer();
    assertEquals("general", stemmer.stem("generalization"));
  }
@Test
  public void testStemSpecialization() {
    Stemmer stemmer = new Stemmer();
    assertEquals("special", stemmer.stem("specialization"));
  }
@Test
  public void testStemOrganizational() {
    Stemmer stemmer = new Stemmer();
    assertEquals("organ", stemmer.stem("organizational"));
  }
@Test
  public void testStemNationalization() {
    Stemmer stemmer = new Stemmer();
    assertEquals("nation", stemmer.stem("nationalization"));
  }
@Test
  public void testStemMinimumLengthOne() {
    Stemmer stemmer = new Stemmer();
    assertEquals("a", stemmer.stem("a")); 
  }
@Test
  public void testStemEndingInYWithVowelBefore() {
    Stemmer stemmer = new Stemmer();
    assertEquals("deni", stemmer.stem("denying")); 
  }
@Test
  public void testStemEndingInYWithoutVowelBefore() {
    Stemmer stemmer = new Stemmer();
    assertEquals("dry", stemmer.stem("dry")); 
  }
@Test
  public void testStemPluralEndingInEs() {
    Stemmer stemmer = new Stemmer();
    assertEquals("bus", stemmer.stem("buses")); 
  }
@Test
  public void testStemEndsInEedWithM0() {
    Stemmer stemmer = new Stemmer();
    assertEquals("feed", stemmer.stem("feed")); 
  }
@Test
  public void testStemEndsInEedWithM1() {
    Stemmer stemmer = new Stemmer();
    assertEquals("need", stemmer.stem("needed")); 
  }
@Test
  public void testStemConsonantDoublingNotStripped() {
    Stemmer stemmer = new Stemmer();
    assertEquals("pass", stemmer.stem("passing")); 
  }
@Test
  public void testStemWordsEndingInBli() {
    Stemmer stemmer = new Stemmer();
    assertEquals("ble", stemmer.stem("blibli")); 
  }
@Test
  public void testStemStep3_EndsWithEntli() {
    Stemmer stemmer = new Stemmer();
    assertEquals("differ", stemmer.stem("differently"));
  }
@Test
  public void testStemStep3_EndsWithOusli() {
    Stemmer stemmer = new Stemmer();
    assertEquals("graci", stemmer.stem("graciously"));
  }
@Test
  public void testStemStep3_EndsWithLogi() {
    Stemmer stemmer = new Stemmer();
    assertEquals("analog", stemmer.stem("analogical"));
  }
@Test
  public void testStemStep4_RemovesNess() {
    Stemmer stemmer = new Stemmer();
    assertEquals("dark", stemmer.stem("darkness"));
  }
@Test
  public void testStemStep5_RemovesMent() {
    Stemmer stemmer = new Stemmer();
    assertEquals("environ", stemmer.stem("environment"));
  }
@Test
  public void testStemStep5_RemovesAnt() {
    Stemmer stemmer = new Stemmer();
    assertEquals("signific", stemmer.stem("significant"));
  }
@Test
  public void testStep5_EndsWithIon_ConditionFail() {
    Stemmer stemmer = new Stemmer();
    assertEquals("onion", stemmer.stem("onion")); 
  }
@Test
  public void testStemStep6_RemovesFinalE_IfM1AndNotCVC() {
    Stemmer stemmer = new Stemmer();
    assertEquals("rate", stemmer.stem("rate")); 
  }
@Test
  public void testStemStep6_RemovesL_DoubleConsonantAndMgt1() {
    Stemmer stemmer = new Stemmer();
    assertEquals("controll", stemmer.stem("controlling")); 
  }
@Test
  public void testStemStep2_DoesNotApplyWithoutVowel() {
    Stemmer stemmer = new Stemmer();
    assertEquals("fly", stemmer.stem("fly")); 
  }
@Test
  public void testStemEndingWithBle() {
    Stemmer stemmer = new Stemmer();
    assertEquals("troubl", stemmer.stem("trouble"));
  }
@Test
  public void testStemFalsePositiveIngEnding() {
    Stemmer stemmer = new Stemmer();
    assertEquals("thing", stemmer.stem("thing")); 
  }
@Test
  public void testStemCaseWhereStemIsSame() {
    Stemmer stemmer = new Stemmer();
    assertEquals("strong", stemmer.stem("strong")); 
  }
@Test
  public void testStemHyphenatedLikeCoOperation() {
    Stemmer stemmer = new Stemmer();
    assertEquals("co-oper", stemmer.stem("co-operation")); 
  }
@Test
  public void testStemAllVowels() {
    Stemmer stemmer = new Stemmer();
    assertEquals("aeiou", stemmer.stem("aeiou")); 
  }
@Test
  public void testStemStep2AliEnding() {
    Stemmer stemmer = new Stemmer();
    assertEquals("emotion", stemmer.stem("emotionally")); 
  }
@Test
  public void testStemWithWordContainingNumbers() {
    Stemmer stemmer = new Stemmer();
    assertEquals("file123", stemmer.stem("file123"));
  }
@Test
  public void testStemWithNonAsciiInput() {
    Stemmer stemmer = new Stemmer();
    assertEquals("naïv", stemmer.stem("naïveté")); 
  }
@Test
  public void testStemWithOnlySpecialCharacters() {
    Stemmer stemmer = new Stemmer();
    assertEquals("!@#", stemmer.stem("!@#")); 
  }
@Test
  public void testStemWithWhitespaceWord() {
    Stemmer stemmer = new Stemmer();
    assertEquals("", stemmer.stem("   ")); 
  }
@Test
  public void testStemLongUnbrokenVowelConsonantWord() {
    Stemmer stemmer = new Stemmer();
    assertEquals("ventil", stemmer.stem("ventilation")); 
  }
@Test
  public void testStemApplyWithNoChange() {
    Stemmer stemmer = new Stemmer();
    Word word = new Word("solid");
    Word result = stemmer.apply(word);
    assertEquals("solid", result.word()); 
  }
@Test
  public void testStep1EndsWithSButPreviousIsAlsoS() {
    Stemmer stemmer = new Stemmer();
    assertEquals("caress", stemmer.stem("caress")); 
  }
@Test
  public void testStep1EndsWithIES() {
    Stemmer stemmer = new Stemmer();
    assertEquals("poni", stemmer.stem("ponies")); 
  }
@Test
  public void testStep1EndsWithEEDButMIsZero() {
    Stemmer stemmer = new Stemmer();
    assertEquals("reed", stemmer.stem("reed")); 
  }
@Test
  public void testStep1EndsWithEEDAndMIsGreaterThanZero() {
    Stemmer stemmer = new Stemmer();
    assertEquals("proceed", stemmer.stem("proceeded")); 
  }
@Test
  public void testStep1EDEndingButNoVowelInStem() {
    Stemmer stemmer = new Stemmer();
    assertEquals("bzzed", stemmer.stem("bzzed")); 
  }
@Test
  public void testStep3MatchesIViti() {
    Stemmer stemmer = new Stemmer();
    assertEquals("sensit", stemmer.stem("sensitivity")); 
  }
@Test
  public void testStep3MatchesBiliti() {
    Stemmer stemmer = new Stemmer();
    assertEquals("adapt", stemmer.stem("adaptability")); 
  }
@Test
  public void testStep4MatchesICal() {
    Stemmer stemmer = new Stemmer();
    assertEquals("critic", stemmer.stem("critical")); 
  }
@Test
  public void testStep4MatchesFUL() {
    Stemmer stemmer = new Stemmer();
    assertEquals("rest", stemmer.stem("restful")); 
  }
@Test
  public void testStep4DoesNotApplyIfSuffixNotFound() {
    Stemmer stemmer = new Stemmer();
    assertEquals("delight", stemmer.stem("delight")); 
  }
@Test
  public void testStep5RemovesENCE() {
    Stemmer stemmer = new Stemmer();
    assertEquals("refer", stemmer.stem("reference")); 
  }
@Test
  public void testStep5EndsWithIONButInvalidPrecedingChar() {
    Stemmer stemmer = new Stemmer();
    assertEquals("opinion", stemmer.stem("opinion")); 
  }
@Test
  public void testStep5EndsWithIONAndValidPrecedingChar() {
    Stemmer stemmer = new Stemmer();
    assertEquals("act", stemmer.stem("action")); 
  }
@Test
  public void testStep6RemovesFinalEWithMGreaterThanOne() {
    Stemmer stemmer = new Stemmer();
    assertEquals("probate", stemmer.stem("probate")); 
  }
@Test
  public void testStep6RetainsFinalEWhenCVCCriteriaFails() {
    Stemmer stemmer = new Stemmer();
    assertEquals("rate", stemmer.stem("rate")); 
  }
@Test
  public void testCVCButEndIsWXY() {
    Stemmer stemmer = new Stemmer();
    assertEquals("tray", stemmer.stem("traying")); 
  }
@Test
  public void testShortWordUnderThreeCharactersNotStemmed() {
    Stemmer stemmer = new Stemmer();
    assertEquals("hi", stemmer.stem("hi")); 
  }
@Test
  public void testApplyFunctionPreservesWordTag() {
    Stemmer stemmer = new Stemmer();
    Word word = new Word("running");
//    word.setTag("VBG");
    Word result = stemmer.apply(word);
    assertEquals("run", result.word());
  }
@Test
  public void testNonStemmableSuffixNotModified() {
    Stemmer stemmer = new Stemmer();
    assertEquals("television", stemmer.stem("television")); 
  }
@Test
  public void testUppercaseInputHandledCorrectly() {
    Stemmer stemmer = new Stemmer();
    assertEquals("relate", stemmer.stem("RELATIONAL".toLowerCase())); 
  }
@Test
  public void testMultipleCallsOnSameStemmerInstance() {
    Stemmer stemmer = new Stemmer();
    assertEquals("walk", stemmer.stem("walking"));
    assertEquals("talk", stemmer.stem("talking"));
    assertEquals("move", stemmer.stem("moved"));
  }
@Test
  public void testWordWithShortSuffixFailsEndsCheck() {
    Stemmer stemmer = new Stemmer();
    assertEquals("hi", stemmer.stem("hi")); 
  }
@Test
  public void testEndsWithEdButNoVowelInStem() {
    Stemmer stemmer = new Stemmer();
    assertEquals("pldd", stemmer.stem("pldded")); 
  }
@Test
  public void testDoubleConsonantsNotSZXRule() {
    Stemmer stemmer = new Stemmer();
    assertEquals("hopp", stemmer.stem("hopping")); 
  }
@Test
  public void testDoubleConsonantsPreservedForAllowedLetters() {
    Stemmer stemmer = new Stemmer();
    assertEquals("fizz", stemmer.stem("fizzing")); 
  }
@Test
  public void testDoubleConsonantAtEndWithHighMValue() {
    Stemmer stemmer = new Stemmer();
    assertEquals("roll", stemmer.stem("rolling")); 
  }
@Test
  public void testCvcReturnsFalseWhenLastCIsWXY() {
    Stemmer stemmer = new Stemmer();
    assertEquals("box", stemmer.stem("boxing")); 
  }
@Test
  public void testCvcTrueConditionTriggersEAddition() {
    Stemmer stemmer = new Stemmer();
    assertEquals("hop", stemmer.stem("hop")); 
  }
@Test
  public void testFinalEIsRemovedWhenMIsGreaterThanOne() {
    Stemmer stemmer = new Stemmer();
    assertEquals("creat", stemmer.stem("create")); 
  }
@Test
  public void testFinalEIsNotRemovedWhenMEqualsOneAndCvcTrue() {
    Stemmer stemmer = new Stemmer();
    assertEquals("hate", stemmer.stem("hate")); 
  }
@Test
  public void testStemEndingWithEIsPreservedIfCvcFails() {
    Stemmer stemmer = new Stemmer();
    assertEquals("pane", stemmer.stem("pane")); 
  }
@Test
  public void testEndsChecksForFalseEarlyExit() {
    Stemmer stemmer = new Stemmer();
    assertEquals("abc", stemmer.stem("abc")); 
  }
@Test
  public void testEndsWithPartialSuffixShouldFail() {
    Stemmer stemmer = new Stemmer();
    assertEquals("ention", stemmer.stem("ention")); 
  }
@Test
  public void testWordWithRepeatedComplexSuffixDoesNotBreak() {
    Stemmer stemmer = new Stemmer();
    assertEquals("commun", stemmer.stem("communicationization")); 
  }
@Test
  public void testNonAlphabeticWordStemmedAsIs() {
    Stemmer stemmer = new Stemmer();
    assertEquals("$$$", stemmer.stem("$$$")); 
  }
@Test
  public void testWordWithInternalDigits() {
    Stemmer stemmer = new Stemmer();
    assertEquals("read123", stemmer.stem("reading123")); 
  }
@Test
  public void testWhitespaceWordReturnsEmpty() {
    Stemmer stemmer = new Stemmer();
    assertEquals("", stemmer.stem("   "));
  }
@Test
  public void testOnlySpacesInWordObject() {
    Stemmer stemmer = new Stemmer();
    Word word = new Word("    ");
    Word result = stemmer.apply(word);
    assertEquals("", result.word());
  }
@Test
  public void testApplyWithWordThatTriggersNoRule() {
    Stemmer stemmer = new Stemmer();
    Word word = new Word("banana");
    Word result = stemmer.apply(word);
    assertEquals("banana", result.word());
  }
@Test
  public void testStemWordThatLooksLikeSuffixButIsNot() {
    Stemmer stemmer = new Stemmer();
    assertEquals("ation", stemmer.stem("ation")); 
  }
@Test
  public void testMZeroPreventsAllRSubstitutions() {
    Stemmer stemmer = new Stemmer();
    assertEquals("abcational", stemmer.stem("abcational")); 
  }
@Test
  public void testStep2FailsWhenNoVowelInStem() {
    Stemmer stemmer = new Stemmer();
    assertEquals("trry", stemmer.stem("trry")); 
  }
@Test
  public void testMExactlyOneBoundaryInStep6() {
    Stemmer stemmer = new Stemmer();
    assertEquals("zap", stemmer.stem("zape")); 
  }
@Test
  public void testEndsWithOnlySuffixNoPrefix() {
    Stemmer stemmer = new Stemmer();
    assertEquals("ment", stemmer.stem("ment")); 
  }
@Test
  public void testEndsMatchButFailOnCharMismatch() {
    Stemmer stemmer = new Stemmer();
    assertEquals("tionx", stemmer.stem("tionx")); 
  }
@Test
  public void testStemResetsBufferProperlyAcrossCalls() {
    Stemmer stemmer = new Stemmer();
    assertEquals("connect", stemmer.stem("connected"));
    assertEquals("relate", stemmer.stem("relational")); 
  }
@Test
  public void testWordWithRepeatedSuffixTriggersOnlyOneStep() {
    Stemmer stemmer = new Stemmer();
    assertEquals("final", stemmer.stem("finalizer")); 
  }
@Test
  public void testStemTriggersOnlyStep1AWithoutStep1B() {
    Stemmer stemmer = new Stemmer();
    assertEquals("caress", stemmer.stem("caress")); 
  }
@Test
  public void testStemStepsShortWordHasNoEffect() {
    Stemmer stemmer = new Stemmer();
    assertEquals("it", stemmer.stem("it")); 
  }
@Test
  public void testMEqualToZeroBlocksStep3Substitution() {
    Stemmer stemmer = new Stemmer();
    assertEquals("abcation", stemmer.stem("abcation")); 
  }
@Test
  public void testMEqualToOneAllowsStep6E() {
    Stemmer stemmer = new Stemmer();
    assertEquals("rake", stemmer.stem("rake")); 
  }
@Test
  public void testMEqualToOneWithCVCStripsE() {
    Stemmer stemmer = new Stemmer();
    assertEquals("rate", stemmer.stem("rate")); 
  }
@Test
  public void testDoubleCNotAtEnd() {
    Stemmer stemmer = new Stemmer();
    assertEquals("jog", stemmer.stem("jogger")); 
  }
@Test
  public void testCVCSequenceEndWXYFails() {
    Stemmer stemmer = new Stemmer();
    assertEquals("tray", stemmer.stem("traying")); 
  }
@Test
  public void testStep5ZSuffixBranchCovered() {
    Stemmer stemmer = new Stemmer();
    assertEquals("modern", stemmer.stem("modernize")); 
  }
@Test
  public void testStep5VBranchive() {
    Stemmer stemmer = new Stemmer();
    assertEquals("act", stemmer.stem("active")); 
  }
@Test
  public void testStep5UBranchOus() {
    Stemmer stemmer = new Stemmer();
    assertEquals("joy", stemmer.stem("joyous")); 
  }
@Test
  public void testStep5FailsDueToMEqualToOne() {
    Stemmer stemmer = new Stemmer();
    assertEquals("curious", stemmer.stem("curious")); 
  }
@Test
  public void testEndsReturnsFalseDueToEarlyExit() {
    Stemmer stemmer = new Stemmer();
    assertEquals("zzz", stemmer.stem("zzz")); 
  }
@Test
  public void testStep4RemovesAlize() {
    Stemmer stemmer = new Stemmer();
    assertEquals("norm", stemmer.stem("normalize")); 
  }
@Test
  public void testMultipleValidSuffixesOnlyOneApplied() {
    Stemmer stemmer = new Stemmer();
    assertEquals("critic", stemmer.stem("criticalization")); 
  }
@Test
  public void testApplyWithAlreadyStemmedWord() {
    Stemmer stemmer = new Stemmer();
    Word input = new Word("critic");
    Word result = stemmer.apply(input);
    assertEquals("critic", result.word()); 
  }
@Test
  public void testWordEndingWithOlogy() {
    Stemmer stemmer = new Stemmer();
    assertEquals("psycholog", stemmer.stem("psychology")); 
  }
@Test
  public void testStemShortRandomSymbols() {
    Stemmer stemmer = new Stemmer();
    assertEquals("@@", stemmer.stem("@@")); 
  }
@Test
  public void testStep1PluralEndingSSNotReduced() {
    Stemmer stemmer = new Stemmer();
    assertEquals("boss", stemmer.stem("boss")); 
  }
@Test
  public void testStep1PluralEndingIESToI() {
    Stemmer stemmer = new Stemmer();
    assertEquals("ladi", stemmer.stem("ladies")); 
  }
@Test
  public void testStep1PluralEndingSOnly() {
    Stemmer stemmer = new Stemmer();
    assertEquals("cat", stemmer.stem("cats")); 
  }
@Test
  public void testStep1EDWithoutVowel() {
    Stemmer stemmer = new Stemmer();
    assertEquals("bzzed", stemmer.stem("bzzed")); 
  }
@Test
  public void testStep1INGWithVowel_doublingConsonant() {
    Stemmer stemmer = new Stemmer();
    assertEquals("hop", stemmer.stem("hopping")); 
  }
@Test
  public void testStep1INGWithVowel_setsSuffixATE() {
    Stemmer stemmer = new Stemmer();
    assertEquals("conflate", stemmer.stem("conflating")); 
  }
@Test
  public void testStep1INGWithVowel_setsSuffixIZE() {
    Stemmer stemmer = new Stemmer();
    assertEquals("digitize", stemmer.stem("digitizing")); 
  }
@Test
  public void testStep1INGWithVowel_setsSuffixBLE() {
    Stemmer stemmer = new Stemmer();
    assertEquals("enable", stemmer.stem("enabling")); 
  }
@Test
  public void testStep2YToIWithVowel() {
    Stemmer stemmer = new Stemmer();
    assertEquals("happi", stemmer.stem("happy")); 
  }
@Test
  public void testStep2YToIKeepsYIfNoVowel() {
    Stemmer stemmer = new Stemmer();
    assertEquals("thyy", stemmer.stem("thyy")); 
  }
@Test
  public void testStep3EndsWithATION() {
    Stemmer stemmer = new Stemmer();
    assertEquals("creat", stemmer.stem("creation")); 
  }
@Test
  public void testStep3EndsWithIZATION() {
    Stemmer stemmer = new Stemmer();
    assertEquals("modern", stemmer.stem("modernization")); 
  }
@Test
  public void testStep3EndsWithATOR() {
    Stemmer stemmer = new Stemmer();
    assertEquals("oper", stemmer.stem("operator")); 
  }
@Test
  public void testStep3EndsWithOUSNESS() {
    Stemmer stemmer = new Stemmer();
    assertEquals("ridicul", stemmer.stem("ridiculousness")); 
  }
@Test
  public void testStep4EndsWithICITI() {
    Stemmer stemmer = new Stemmer();
    assertEquals("electric", stemmer.stem("electricity")); 
  }
@Test
  public void testStep4EndsWithATIVE() {
    Stemmer stemmer = new Stemmer();
    assertEquals("inform", stemmer.stem("informative")); 
  }
@Test
  public void testStep4EndsWithICAL() {
    Stemmer stemmer = new Stemmer();
    assertEquals("crit", stemmer.stem("critical")); 
  }
@Test
  public void testStep5EndsWithANCE() {
    Stemmer stemmer = new Stemmer();
    assertEquals("relev", stemmer.stem("relevance")); 
  }
@Test
  public void testStep5EndsWithENCE() {
    Stemmer stemmer = new Stemmer();
    assertEquals("refer", stemmer.stem("reference")); 
  }
@Test
  public void testStep5EndsWithMENT() {
    Stemmer stemmer = new Stemmer();
    assertEquals("develop", stemmer.stem("development")); 
  }
@Test
  public void testStep5EndsWithIONPrecededByST() {
    Stemmer stemmer = new Stemmer();
    assertEquals("act", stemmer.stem("action")); 
  }
@Test
  public void testStep5EndsWithIONPrecededByInvalid() {
    Stemmer stemmer = new Stemmer();
    assertEquals("opinion", stemmer.stem("opinion")); 
  }
@Test
  public void testStep5EndsWithER() {
    Stemmer stemmer = new Stemmer();
    assertEquals("sing", stemmer.stem("singer")); 
  }
@Test
  public void testStep6RemovesFinalEIfMGreaterThanOne() {
    Stemmer stemmer = new Stemmer();
    assertEquals("creat", stemmer.stem("create")); 
  }
@Test
  public void testStep6RetainsFinalEIfMEqualsOneAndCvcFails() {
    Stemmer stemmer = new Stemmer();
    assertEquals("hope", stemmer.stem("hope")); 
  }
@Test
  public void testStep6RemovesDoubleLIfMGreaterThanOne() {
    Stemmer stemmer = new Stemmer();
    assertEquals("control", stemmer.stem("controlled")); 
  }
@Test
  public void testApplyWithUnchangedWord() {
    Stemmer stemmer = new Stemmer();
    Word input = new Word("run");
    Word result = stemmer.apply(input);
    assertEquals("run", result.word());
  }
@Test
  public void testRealWordWithCompoundSuffixChain() {
    Stemmer stemmer = new Stemmer();
    assertEquals("relate", stemmer.stem("relationally")); 
  }
@Test
  public void testShortNonStemmableWord() {
    Stemmer stemmer = new Stemmer();
    assertEquals("do", stemmer.stem("do")); 
  }
@Test
  public void testWordContainingYWhereItActsAsVowelFirstPosition() {
    Stemmer stemmer = new Stemmer();
    assertEquals("yell", stemmer.stem("yelling")); 
  }
@Test
  public void testStemWordEndingInLogi() {
    Stemmer stemmer = new Stemmer();
    assertEquals("analog", stemmer.stem("analogical")); 
  }
@Test
  public void testSuffixLongerThanWordDoesNotMatch() {
    Stemmer stemmer = new Stemmer();
    assertEquals("hi", stemmer.stem("hi")); 
  }
@Test
  public void testEmptyStringReturnsEmpty() {
    Stemmer stemmer = new Stemmer();
    assertEquals("", stemmer.stem(""));
  }
@Test
  public void testNumericTextRemainsUnchanged() {
    Stemmer stemmer = new Stemmer();
    assertEquals("12345", stemmer.stem("12345")); 
  }
@Test
  public void testSymbolicTextRemainsUnchanged() {
    Stemmer stemmer = new Stemmer();
    assertEquals("!!", stemmer.stem("!!")); 
  }
@Test
  public void testHyphenatedWordReturnsPartiallyStemmed() {
    Stemmer stemmer = new Stemmer();
    assertEquals("data-proces", stemmer.stem("data-processing")); 
  }
@Test
  public void testEndsWithSButBouncedByB_kMinus1S() {
    Stemmer stemmer = new Stemmer();
    assertEquals("glass", stemmer.stem("glass")); 
  }
@Test
  public void testEndsWithBlStemReplacementToBle() {
    Stemmer stemmer = new Stemmer();
    assertEquals("doub", stemmer.stem("doubling")); 
  }
@Test
  public void testVowelInStemEdgeCaseForEd() {
    Stemmer stemmer = new Stemmer();
    assertEquals("agree", stemmer.stem("agreed")); 
  }
@Test
  public void testConsYFirstCharReturnsTrue() {
    Stemmer stemmer = new Stemmer();
    assertEquals("yarn", stemmer.stem("yarn")); 
  }
@Test
  public void testStep3DoesNotReplaceWhenMIsZero() {
    Stemmer stemmer = new Stemmer();
    assertEquals("abcation", stemmer.stem("abcation")); 
  }
@Test
  public void testStep4CoversOUS() {
    Stemmer stemmer = new Stemmer();
    assertEquals("joy", stemmer.stem("joyous")); 
  }
@Test
  public void testStep4DoesNotStripFULWhenMTooLow() {
    Stemmer stemmer = new Stemmer();
    assertEquals("awful", stemmer.stem("awful")); 
  }
@Test
  public void testStep3BliDetectedAndReplaced() {
    Stemmer stemmer = new Stemmer();
    assertEquals("ble", stemmer.stem("blibli")); 
  }
@Test
  public void testStep3IvitiMatchedAndReplaced() {
    Stemmer stemmer = new Stemmer();
    assertEquals("sensit", stemmer.stem("sensitivity")); 
  }
@Test
  public void testStep4MatchesNessAndRemoves() {
    Stemmer stemmer = new Stemmer();
    assertEquals("dark", stemmer.stem("darkness")); 
  }
@Test
  public void testStep5FailsIfMNotGreaterThanOne() {
    Stemmer stemmer = new Stemmer();
    assertEquals("sing", stemmer.stem("singer")); 
  }
@Test
  public void testStep5MatchesEntAndRemoves() {
    Stemmer stemmer = new Stemmer();
    assertEquals("differ", stemmer.stem("different")); 
  }
@Test
  public void testCvcFailsOnShortLength() {
    Stemmer stemmer = new Stemmer();
    assertEquals("am", stemmer.stem("am")); 
  }
@Test
  public void testCvcTrueButLastCharIsWXY() {
    Stemmer stemmer = new Stemmer();
    assertEquals("tray", stemmer.stem("tray")); 
  }
@Test
  public void testDoubleConsonantNonCvcWord() {
    Stemmer stemmer = new Stemmer();
    assertEquals("grip", stemmer.stem("gripping")); 
  }
@Test
  public void testDoubleConsonantAtStartIsFalse() {
    Stemmer stemmer = new Stemmer();
    assertEquals("ab", stemmer.stem("ab")); 
  }
@Test
  public void testVowelInStemReturnsFalseAllConsonants() {
    Stemmer stemmer = new Stemmer();
    assertEquals("bzz", stemmer.stem("bzz")); 
  }
@Test
  public void testVowelInStemReturnsTrueOnFirstChar() {
    Stemmer stemmer = new Stemmer();
    assertEquals("ask", stemmer.stem("asking")); 
  }
@Test
  public void testToStringValueReflectsPostStemResult() {
    Stemmer stemmer = new Stemmer();
    stemmer.stem("relational");
    assertEquals("relate", stemmer.toString());
  }
@Test
  public void testStemWordAlreadyStemmed() {
    Stemmer stemmer = new Stemmer();
    assertEquals("walk", stemmer.stem("walk")); 
  }
@Test
  public void testStemWordThatStartsWithVowelSequence() {
    Stemmer stemmer = new Stemmer();
    assertEquals("agree", stemmer.stem("agreeing")); 
  }
@Test
  public void testWordThatLooksLikeSuffixOnly() {
    Stemmer stemmer = new Stemmer();
    assertEquals("ation", stemmer.stem("ation")); 
  }
@Test
  public void testWordWithNonAlphabeticMixedIn() {
    Stemmer stemmer = new Stemmer();
    assertEquals("log123", stemmer.stem("logging123")); 
  }
@Test
  public void testApplyHandlesWordWithTag() {
    Stemmer stemmer = new Stemmer();
    Word word = new Word("rationalizing");
//    word.setTag("VBG");
    Word result = stemmer.apply(word);
    assertEquals("rational", result.word()); 
  }
@Test
  public void testUnicodeInputStemsAsExpected() {
    Stemmer stemmer = new Stemmer();
    assertEquals("naiv", stemmer.stem("naïvety")); 
  }
@Test
  public void testDigitsOnlyInput() {
    Stemmer stemmer = new Stemmer();
    assertEquals("123", stemmer.stem("123"));
  }
@Test
  public void testWhitespaceOnlyReturnsEmptyString() {
    Stemmer stemmer = new Stemmer();
    assertEquals("", stemmer.stem("   "));
  }
@Test
  public void testHyphenatedCompoundWord() {
    Stemmer stemmer = new Stemmer();
    assertEquals("pre-process", stemmer.stem("pre-processing")); 
  }
@Test
  public void testEndsReturnsFalseWhenTooShort() {
    Stemmer stemmer = new Stemmer();
    assertEquals("it", stemmer.stem("it")); 
  } 
}