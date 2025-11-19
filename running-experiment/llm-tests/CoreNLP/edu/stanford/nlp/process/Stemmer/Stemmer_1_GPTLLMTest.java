package edu.stanford.nlp.process;

import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.process.Stemmer;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class Stemmer_1_GPTLLMTest {

 @Test
  public void testSimpleWordWithoutSuffix() {
    Stemmer stemmer = new Stemmer();
    assertEquals("dog", stemmer.stem("dog"));

    stemmer = new Stemmer();
    assertEquals("run", stemmer.stem("run"));
  }
@Test
  public void testPluralSForm() {
    Stemmer stemmer1 = new Stemmer();
    assertEquals("cat", stemmer1.stem("cats"));

    Stemmer stemmer2 = new Stemmer();
    assertEquals("caress", stemmer2.stem("caresses"));

    Stemmer stemmer3 = new Stemmer();
    assertEquals("poni", stemmer3.stem("ponies"));

    Stemmer stemmer4 = new Stemmer();
    assertEquals("ti", stemmer4.stem("ties"));
  }
@Test
  public void testEdEnding() {
    Stemmer stemmer1 = new Stemmer();
    assertEquals("agree", stemmer1.stem("agreed"));

    Stemmer stemmer2 = new Stemmer();
    assertEquals("disable", stemmer2.stem("disabled"));

    Stemmer stemmer3 = new Stemmer();
    assertEquals("feed", stemmer3.stem("feed"));
  }
@Test
  public void testIngEnding() {
    Stemmer stemmer1 = new Stemmer();
    assertEquals("mat", stemmer1.stem("matting"));

    Stemmer stemmer2 = new Stemmer();
    assertEquals("mate", stemmer2.stem("mating"));

    Stemmer stemmer3 = new Stemmer();
    assertEquals("meet", stemmer3.stem("meeting"));

    Stemmer stemmer4 = new Stemmer();
    assertEquals("mill", stemmer4.stem("milling"));

    Stemmer stemmer5 = new Stemmer();
    assertEquals("mess", stemmer5.stem("messing"));

    Stemmer stemmer6 = new Stemmer();
    assertEquals("meet", stemmer6.stem("meetings"));
  }
@Test
  public void testStep2ChangeYToI() {
    Stemmer stemmer1 = new Stemmer();
    assertEquals("happi", stemmer1.stem("happy"));

    Stemmer stemmer2 = new Stemmer();
    assertEquals("sky", stemmer2.stem("sky"));
  }
@Test
  public void testStep3SuffixReductions() {
    Stemmer stemmer1 = new Stemmer();
    assertEquals("formal", stemmer1.stem("formality"));

    Stemmer stemmer2 = new Stemmer();
    assertEquals("sensit", stemmer2.stem("sensitivity"));

    Stemmer stemmer3 = new Stemmer();
    assertEquals("relat", stemmer3.stem("relational"));

    Stemmer stemmer4 = new Stemmer();
    assertEquals("hop", stemmer4.stem("hopelessness"));

    Stemmer stemmer5 = new Stemmer();
    assertEquals("organ", stemmer5.stem("organizational"));
  }
@Test
  public void testStep4AdditionalSuffixes() {
    Stemmer stemmer1 = new Stemmer();
    assertEquals("critic", stemmer1.stem("critically"));

    Stemmer stemmer2 = new Stemmer();
    assertEquals("use", stemmer2.stem("useful"));

    Stemmer stemmer3 = new Stemmer();
    assertEquals("bold", stemmer3.stem("boldness"));
  }
@Test
  public void testStep5CompoundSuffixRemoval() {
    Stemmer stemmer1 = new Stemmer();
    assertEquals("adjust", stemmer1.stem("adjustment"));

    Stemmer stemmer2 = new Stemmer();
    assertEquals("effect", stemmer2.stem("effectiveness"));

    Stemmer stemmer3 = new Stemmer();
    assertEquals("commun", stemmer3.stem("communication"));

    Stemmer stemmer4 = new Stemmer();
    assertEquals("critic", stemmer4.stem("critical"));
  }
@Test
  public void testStep6FinalEAndDoubleL() {
    Stemmer stemmer1 = new Stemmer();
    assertEquals("probabl", stemmer1.stem("probable"));

    Stemmer stemmer2 = new Stemmer();
    assertEquals("control", stemmer2.stem("controlled"));

    Stemmer stemmer3 = new Stemmer();
    assertEquals("dob", stemmer3.stem("dobble"));
  }
@Test
  public void testApplyFunctionInterface() {
    Stemmer stemmer = new Stemmer();
    Word word = new Word("happiness");
    Word result = stemmer.apply(word);
    assertEquals("happi", result.word());
  }
@Test
  public void testApplyConsistency() {
    Stemmer stemmer = new Stemmer();
    Word word = new Word("agreed");
    Word fromApply = stemmer.apply(word);
    Word fromStem = stemmer.stem(word);
    assertEquals(fromStem, fromApply);
  }
@Test
  public void testToStringIsCorrectAfterStem() {
    Stemmer stemmer = new Stemmer();
    stemmer.stem("cats");
    assertEquals("cat", stemmer.toString());
  }
@Test
  public void testShortWordUnderThreeLetters() {
    Stemmer stemmer1 = new Stemmer();
    assertEquals("be", stemmer1.stem("be"));

    Stemmer stemmer2 = new Stemmer();
    assertEquals("do", stemmer2.stem("do"));
  }
@Test
  public void testOnlyConsonants() {
    Stemmer stemmer1 = new Stemmer();
    assertEquals("bbb", stemmer1.stem("bbb"));

    Stemmer stemmer2 = new Stemmer();
    assertEquals("mmmm", stemmer2.stem("mmmm"));
  }
@Test
  public void testOnlyVowels() {
    Stemmer stemmer1 = new Stemmer();
    assertEquals("aeiou", stemmer1.stem("aeiou"));

    Stemmer stemmer2 = new Stemmer();
    assertEquals("iouea", stemmer2.stem("iouea"));
  }
@Test
  public void testEmptyString() {
    Stemmer stemmer = new Stemmer();
    assertEquals("", stemmer.stem(""));
  }
@Test
  public void testSingleLetter() {
    Stemmer stemmer1 = new Stemmer();
    assertEquals("a", stemmer1.stem("a"));

    Stemmer stemmer2 = new Stemmer();
    assertEquals("z", stemmer2.stem("z"));
  }
@Test
  public void testRepeatedCallsWithDifferentWords() {
    Stemmer stemmer1 = new Stemmer();
    assertEquals("running", stemmer1.stem("running"));

    Stemmer stemmer2 = new Stemmer();
    assertEquals("agreement", stemmer2.stem("agreements"));

    Stemmer stemmer3 = new Stemmer();
    assertEquals("meet", stemmer3.stem("meetings"));

    Stemmer stemmer4 = new Stemmer();
    assertEquals("cat", stemmer4.stem("cats"));
  }
@Test
  public void testLongerWordStem() {
    Stemmer stemmer1 = new Stemmer();
    assertEquals("character", stemmer1.stem("characterizing"));

    Stemmer stemmer2 = new Stemmer();
    assertEquals("nation", stemmer2.stem("nationalization"));
  }
@Test
  public void testStemNullStringThrows() {
    try {
      Stemmer stemmer = new Stemmer();
      stemmer.stem((String) null);
      fail("Expected NullPointerException");
    } catch (NullPointerException expected) {
      
    }
  }
@Test
  public void testStemNullWordThrows() {
    try {
      Stemmer stemmer = new Stemmer();
      stemmer.stem((Word) null);
      fail("Expected NullPointerException");
    } catch (NullPointerException expected) {
      
    }
  }
@Test
  public void testMixedUsageStyle() {
    Stemmer stemmer1 = new Stemmer();
    assertEquals("organ", stemmer1.stem(new Word("organizational")).word());

    Stemmer stemmer2 = new Stemmer();
    assertEquals("meet", stemmer2.apply(new Word("meetings")).word());

    Stemmer stemmer3 = new Stemmer();
    assertEquals("probabl", stemmer3.stem(new Word("probable")).word());

    Stemmer stemmer4 = new Stemmer();
    assertEquals("relat", stemmer4.stem("relational"));
  }
@Test
  public void testStep1SkipDueToMEqualsZero() {
    Stemmer stemmer = new Stemmer();
    assertEquals("bleed", stemmer.stem("bleed")); 
  }
@Test
  public void testStep1DoubleConsonantNotRemoved() {
    Stemmer stemmer = new Stemmer();
    assertEquals("roll", stemmer.stem("rolling")); 
  }
@Test
  public void testStep1CvcEAdded() {
    Stemmer stemmer = new Stemmer();
    assertEquals("hop", stemmer.stem("hopping")); 
  }
@Test
  public void testEndsMethodShorterWordFails() {
    Stemmer stemmer = new Stemmer();
    assertEquals("at", stemmer.stem("at")); 
  }
@Test
  public void testDoubleCWithVowelBefore() {
    Stemmer stemmer = new Stemmer();
    assertEquals("tripl", stemmer.stem("tripling")); 
  }
@Test
  public void testStep3MEqualsZeroSkip() {
    Stemmer stemmer = new Stemmer();
    assertEquals("rational", stemmer.stem("rational")); 
  }
@Test
  public void testStep4UnmodifiedShortSyllable() {
    Stemmer stemmer = new Stemmer();
    assertEquals("ness", stemmer.stem("ness")); 
  }
@Test
  public void testStep5MEqualsOneNoChange() {
    Stemmer stemmer = new Stemmer();
    assertEquals("con", stemmer.stem("con")); 
  }
@Test
  public void testStep6RemoveEWhenValid() {
    Stemmer stemmer = new Stemmer();
    assertEquals("rate", stemmer.stem("rates")); 
  }
@Test
  public void testFinalLNotStrippedWhenNotDoubleAndmSmall() {
    Stemmer stemmer = new Stemmer();
    assertEquals("slim", stemmer.stem("slim")); 
  }
@Test
  public void testSequentialStemmingReusesSuccessfully() {
    Stemmer stemmer = new Stemmer();
    assertEquals("connect", stemmer.stem("connected"));
    assertEquals("connect", stemmer.stem("connecting"));
    assertEquals("connect", stemmer.stem("connection"));
    assertEquals("connect", stemmer.stem("connections"));
  }
@Test
  public void testWordAlreadyStemmedForm() {
    Stemmer stemmer = new Stemmer();
    assertEquals("rate", stemmer.stem("rate")); 
  }
@Test
  public void testTrailingWhitespacePreservedIfOutside() {
    Stemmer stemmer = new Stemmer();
    assertEquals("happi", stemmer.stem("happiness")); 
  }
@Test
  public void testUppercaseLettersAreCaseSensitive() {
    Stemmer stemmer1 = new Stemmer();
    assertEquals("Happiness", stemmer1.stem("Happiness"));  

    Stemmer stemmer2 = new Stemmer();
    assertEquals("Organizational", stemmer2.stem("Organizational")); 
  }
@Test
  public void testNumericInputUnchanged() {
    Stemmer stemmer = new Stemmer();
    assertEquals("12345", stemmer.stem("12345")); 
  }
@Test
  public void testHyphenatedWord() {
    Stemmer stemmer = new Stemmer();
    assertEquals("co-oper", stemmer.stem("co-operating")); 
  }
@Test
  public void testSuffixThatLooksLikeAnotherButIsNot() {
    Stemmer stemmer = new Stemmer();
    assertEquals("tiles", stemmer.stem("tiles")); 
  }
@Test
  public void testNonAsciiInput() {
    Stemmer stemmer = new Stemmer();
    assertEquals("naïv", stemmer.stem("naïveté")); 
  }
@Test
  public void testWordEndingWithYNoVowelBefore() {
    Stemmer stemmer = new Stemmer();
    assertEquals("cry", stemmer.stem("cry")); 
  }
@Test
  public void testVeryLongInputWord() {
    Stemmer stemmer = new Stemmer();
    assertEquals("pneumonoultramicroscopicsilicovolcanoconiosis", stemmer.stem("pneumonoultramicroscopicsilicovolcanoconiosis")); 
  }
@Test
  public void testStemWithSymbols() {
    Stemmer stemmer = new Stemmer();
    assertEquals("$percent", stemmer.stem("$percent")); 
  }
@Test
  public void testStemWithSingleY() {
    Stemmer stemmer = new Stemmer();
    assertEquals("y", stemmer.stem("y")); 
  }
@Test
  public void testStemWithOnlyWhitespace() {
    Stemmer stemmer = new Stemmer();
    assertEquals(" ", stemmer.stem(" "));
  }
@Test
  public void testStemMultipleSimilarSuffixes() {
    Stemmer stemmer1 = new Stemmer();
    assertEquals("reviv", stemmer1.stem("revival"));

    Stemmer stemmer2 = new Stemmer();
    assertEquals("reviv", stemmer2.stem("reviving"));

    Stemmer stemmer3 = new Stemmer();
    assertEquals("reviv", stemmer3.stem("revives"));

    Stemmer stemmer4 = new Stemmer();
    assertEquals("reviv", stemmer4.stem("revived"));
  }
@Test
  public void testYAtBeginningOfWordHandledAsConsonant() {
    Stemmer stemmer = new Stemmer();
    assertEquals("yelp", stemmer.stem("yelping")); 
  }
@Test
  public void testYAfterVowelHandledAsConsonantInStem() {
    Stemmer stemmer = new Stemmer();
    assertEquals("play", stemmer.stem("plays")); 
  }
@Test
  public void testStemWordWithConsonantClusterAtEnd() {
    Stemmer stemmer = new Stemmer();
    assertEquals("strong", stemmer.stem("strong")); 
  }
@Test
  public void testStemWordEndingWithCvcPatternEdgeCase() {
    Stemmer stemmer = new Stemmer();
    assertEquals("make", stemmer.stem("making")); 
  }
@Test
  public void testWordEndingWithEProtectedInStep6() {
    Stemmer stemmer = new Stemmer();
    assertEquals("hoe", stemmer.stem("hoe")); 
  }
@Test
  public void testWordEndingInLeNotRemovedIncorrectly() {
    Stemmer stemmer = new Stemmer();
    assertEquals("single", stemmer.stem("single")); 
  }
@Test
  public void testStep3UnknownSuffixLetterFallthrough() {
    Stemmer stemmer = new Stemmer();
    assertEquals("design", stemmer.stem("designize")); 
  }
@Test
  public void testEdgeCaseStep4FallthroughDefault() {
    Stemmer stemmer = new Stemmer();
    assertEquals("fatal", stemmer.stem("fatalize")); 
  }
@Test
  public void testSuffixNotStrippedWhenMEqualsZero() {
    Stemmer stemmer = new Stemmer();
    assertEquals("bee", stemmer.stem("bees")); 
  }
@Test
  public void testSuffixStrippedWhenMEqualsOne() {
    Stemmer stemmer = new Stemmer();
    assertEquals("agree", stemmer.stem("agreed")); 
  }
@Test
  public void testSuffixStrippedWhenMGreaterThanOne() {
    Stemmer stemmer = new Stemmer();
    assertEquals("commun", stemmer.stem("communication")); 
  }
@Test
  public void testCvcEndingPreventsEAddition() {
    Stemmer stemmer = new Stemmer();
    assertEquals("box", stemmer.stem("boxing")); 
  }
@Test
  public void testDoubleConsonantExceptionsNotStripped() {
    Stemmer stemmer = new Stemmer();
    assertEquals("fizz", stemmer.stem("fizzing")); 
  }
@Test
  public void testStemProtectiveStep5IonCondition() {
    Stemmer stemmer = new Stemmer();
    assertEquals("direction", stemmer.stem("direction")); 
  }
@Test
  public void testStep5RejectsIonWhenConditionNotMet() {
    Stemmer stemmer = new Stemmer();
    assertEquals("opinion", stemmer.stem("opinion")); 
  }
@Test
  public void testStemWordEndingWithFullSuffixNotCoveredInSteps() {
    Stemmer stemmer = new Stemmer();
    assertEquals("thank", stemmer.stem("thankful")); 
  }
@Test
  public void testStemUnknownLongSuffixNoStrip() {
    Stemmer stemmer = new Stemmer();
    assertEquals("capitolize", stemmer.stem("capitolize")); 
  }
@Test
  public void testAccidentalPartialSufficesDontMatchEnds() {
    Stemmer stemmer = new Stemmer();
    assertEquals("medic", stemmer.stem("medical")); 
  }
@Test
  public void testInternalYNotModifiedWhenConsistent() {
    Stemmer stemmer = new Stemmer();
    assertEquals("cry", stemmer.stem("crying")); 
  }
@Test
  public void testStemSuffixSensitiveToCasing() {
    Stemmer stemmer = new Stemmer();
    assertEquals("Talking", stemmer.stem("Talking")); 
  }
@Test
  public void testStemShortValidWithSuffixButTooShortForStep() {
    Stemmer stemmer = new Stemmer();
    assertEquals("us", stemmer.stem("us")); 
  }
@Test
  public void testWordEndingWithEDThatIsNotASuffix() {
    Stemmer stemmer = new Stemmer();
    assertEquals("red", stemmer.stem("red")); 
  }
@Test
  public void testStemWordEndingWithNessAndShortRoot() {
    Stemmer stemmer = new Stemmer();
    assertEquals("mad", stemmer.stem("madness")); 
  }
@Test
  public void testDoubleConsonantEndingWithSShouldRetain() {
    Stemmer stemmer = new Stemmer();
    assertEquals("boss", stemmer.stem("bosses")); 
  }
@Test
  public void testStemComplexSuffixChain() {
    Stemmer stemmer = new Stemmer();
    assertEquals("optim", stemmer.stem("optimistically")); 
  }
@Test
  public void testStemMultiStepSuffixes() {
    Stemmer stemmer = new Stemmer();
    assertEquals("educat", stemmer.stem("educationally")); 
  }
@Test
  public void testWordThatEndsInZShouldRetainIfDoubleZ() {
    Stemmer stemmer = new Stemmer();
    assertEquals("buzz", stemmer.stem("buzzing")); 
  }
@Test
  public void testStemWordWithNumbersMixedIn() {
    Stemmer stemmer = new Stemmer();
    assertEquals("run123", stemmer.stem("run123")); 
  }
@Test
  public void testStemControlCharacterIgnored() {
    Stemmer stemmer = new Stemmer();
    assertEquals("hop", stemmer.stem("hop\u0007ping")); 
  }
@Test
  public void testEndsWithIonButNotPrecededBySTShouldNotStrip() {
    Stemmer stemmer = new Stemmer();
    assertEquals("onion", stemmer.stem("onion")); 
  }
@Test
  public void testDoubleCWithLExceptionOffsetInAdjustment() {
    Stemmer stemmer = new Stemmer();
    assertEquals("roll", stemmer.stem("rolling")); 
  }
@Test
  public void testDoubleCWithSExceptionHandledCorrectly() {
    Stemmer stemmer = new Stemmer();
    assertEquals("miss", stemmer.stem("misses")); 
  }
@Test
  public void testDoubleCWithZExceptionHandledCorrectly() {
    Stemmer stemmer = new Stemmer();
    assertEquals("fizz", stemmer.stem("fizzing")); 
  }
@Test
  public void testStemWithSuffixButMEqualsZeroShouldNotChange() {
    Stemmer stemmer = new Stemmer();
    assertEquals("tees", stemmer.stem("tees")); 
  }
@Test
  public void testStemWithSuffixBliShouldBecomeBle() {
    Stemmer stemmer = new Stemmer();
    assertEquals("noble", stemmer.stem("nobli")); 
  }
@Test
  public void testStemWithSuffixEliShouldBecomeE() {
    Stemmer stemmer = new Stemmer();
    assertEquals("responsible", stemmer.stem("responseli")); 
  }
@Test
  public void testStemWithSuffixEntliShouldBecomeEnt() {
    Stemmer stemmer = new Stemmer();
    assertEquals("differ", stemmer.stem("differentli")); 
  }
@Test
  public void testStemWordWithUnknownSwitchCaseSuffixStep3() {
    Stemmer stemmer = new Stemmer();
    assertEquals("xenoxi", stemmer.stem("xenoxification")); 
  }
@Test
  public void testStep4SuffixIcitiShouldBecomeIc() {
    Stemmer stemmer = new Stemmer();
    assertEquals("critic", stemmer.stem("criticity")); 
  }
@Test
  public void testStep4SuffixFullShouldBeRemoved() {
    Stemmer stemmer = new Stemmer();
    assertEquals("grace", stemmer.stem("graceful")); 
  }
@Test
  public void testStep4SuffixNessShouldBeRemoved() {
    Stemmer stemmer = new Stemmer();
    assertEquals("kind", stemmer.stem("kindness")); 
  }
@Test
  public void testStep5SuffixAbleShouldBeRemovedIfMGreaterThan1() {
    Stemmer stemmer = new Stemmer();
    assertEquals("drink", stemmer.stem("drinkable")); 
  }
@Test
  public void testStep5SuffixMentShouldBeRemovedIfMGreaterThan1() {
    Stemmer stemmer = new Stemmer();
    assertEquals("adjust", stemmer.stem("adjustment")); 
  }
@Test
  public void testStep5SuffixEmentWithLowMNotRemoved() {
    Stemmer stemmer = new Stemmer();
    assertEquals("element", stemmer.stem("element")); 
  }
@Test
  public void testStep5SuffixEntShouldBeRemoved() {
    Stemmer stemmer = new Stemmer();
    assertEquals("differ", stemmer.stem("different")); 
  }
@Test
  public void testStep6FinalERemovedIfMGreaterThan1() {
    Stemmer stemmer = new Stemmer();
    assertEquals("decid", stemmer.stem("decide")); 
  }
@Test
  public void testStep6FinalERetainedIfMEquals1AndCVCTrue() {
    Stemmer stemmer = new Stemmer();
    assertEquals("hope", stemmer.stem("hope")); 
  }
@Test
  public void testUnicodeWithValidSuffixProcessing() {
    Stemmer stemmer = new Stemmer();
    assertEquals("naïv", stemmer.stem("naïveté")); 
  }
@Test
  public void testStemWithDotOrPunctuation() {
    Stemmer stemmer = new Stemmer();
    assertEquals("run.", stemmer.stem("running.")); 
  }
@Test
  public void testStemWithCompoundSuffixization() {
    Stemmer stemmer = new Stemmer();
    assertEquals("modern", stemmer.stem("modernization")); 
  }
@Test
  public void testWordWithNoVowelInStemSkipsEdRule() {
    Stemmer stemmer = new Stemmer();
    assertEquals("rhythmed", stemmer.stem("rhythmed")); 
  }
@Test
  public void testWordWithCVCButBlockedByXEnding() {
    Stemmer stemmer = new Stemmer();
    assertEquals("box", stemmer.stem("boxing")); 
  }
@Test
  public void testStemWithPrefixThatLooksLikeSuffix() {
    Stemmer stemmer = new Stemmer();
    assertEquals("izatio", stemmer.stem("izational")); 
  }
@Test
  public void testStemWithSuffixAlitiShouldBecomeAl() {
    Stemmer stemmer = new Stemmer();
    assertEquals("form", stemmer.stem("formality")); 
  }
@Test
  public void testStemWithSuffixBilitiShouldBecomeBle() {
    Stemmer stemmer = new Stemmer();
    assertEquals("prob", stemmer.stem("probability")); 
  }
@Test
  public void testStemWithLIsExceptionHandled() {
    Stemmer stemmer = new Stemmer();
    assertEquals("label", stemmer.stem("labelling")); 
  }
@Test
  public void testStemShortWordExactlyThreeLettersNotChanged() {
    Stemmer stemmer = new Stemmer();
    assertEquals("run", stemmer.stem("run")); 
  }
@Test
  public void testEndsMethodFailsDueToShortWord() {
    Stemmer stemmer = new Stemmer();
    assertEquals("it", stemmer.stem("it")); 
  }
@Test
  public void testEndsMethodBoundaryAtSuffixLength() {
    Stemmer stemmer = new Stemmer();
    assertEquals("hop", stemmer.stem("hoping")); 
  }
@Test
  public void testStep1VowelInStemGateTrue() {
    Stemmer stemmer = new Stemmer();
    assertEquals("agree", stemmer.stem("agreed")); 
  }
@Test
  public void testStep1VowelInStemGateFalse() {
    Stemmer stemmer = new Stemmer();
    assertEquals("dryed", stemmer.stem("dryed")); 
  }
@Test
  public void testStep2YChangedToIOnlyWithVowelBefore() {
    Stemmer stemmer = new Stemmer();
    assertEquals("happi", stemmer.stem("happy")); 
  }
@Test
  public void testStep2YNotChangedIfNoVowelBefore() {
    Stemmer stemmer = new Stemmer();
    assertEquals("shy", stemmer.stem("shy")); 
  }
@Test
  public void testStep3SuffixWithInsufficientM() {
    Stemmer stemmer = new Stemmer();
    assertEquals("use", stemmer.stem("usable")); 
  }
@Test
  public void testStep3HasMatchingSuffixWithMZeroAvoidsChange() {
    Stemmer stemmer = new Stemmer();
    assertEquals("qualiti", stemmer.stem("qualiti")); 
  }
@Test
  public void testStep4RemovesSuffixWithInclusiveMatch() {
    Stemmer stemmer = new Stemmer();
    assertEquals("ic", stemmer.stem("iciti")); 
  }
@Test
  public void testStep4SuffixNotStrippedDueToShortStem() {
    Stemmer stemmer = new Stemmer();
    assertEquals("faceness", stemmer.stem("faceness")); 
  }
@Test
  public void testStep5HandlesEdgeCaseIonPrecededByT() {
    Stemmer stemmer = new Stemmer();
    assertEquals("reduc", stemmer.stem("reduction")); 
  }
@Test
  public void testStep5RejectsIonIfNotPrecededBySOrT() {
    Stemmer stemmer = new Stemmer();
    assertEquals("passion", stemmer.stem("passion")); 
  }
@Test
  public void testStep6RemovesFinalEWhenMIsGreaterThanOne() {
    Stemmer stemmer = new Stemmer();
    assertEquals("creat", stemmer.stem("create")); 
  }
@Test
  public void testStep6PreservesFinalEOnCVCFalse() {
    Stemmer stemmer = new Stemmer();
    assertEquals("phone", stemmer.stem("phone")); 
  }
@Test
  public void testOnlyDigitsInput() {
    Stemmer stemmer = new Stemmer();
    assertEquals("1234", stemmer.stem("1234")); 
  }
@Test
  public void testSpecialCharactersInputOnly() {
    Stemmer stemmer = new Stemmer();
    assertEquals("@#$%", stemmer.stem("@#$%")); 
  }
@Test
  public void testMixedAlphanumericInput() {
    Stemmer stemmer = new Stemmer();
    assertEquals("code123", stemmer.stem("code123")); 
  }
@Test
  public void testUnicodeCharacterInput() {
    Stemmer stemmer = new Stemmer();
    assertEquals("fête", stemmer.stem("fête")); 
  }
@Test
  public void testValidWordRepeatedCallsStateIsolation() {
    Stemmer stemmer = new Stemmer();
    assertEquals("connect", stemmer.stem("connected"));
    assertEquals("connect", stemmer.stem("connecting")); 
  }
@Test
  public void testWordStemSameAsOutputNoSuffixLogicExecutes() {
    Stemmer stemmer = new Stemmer();
    assertEquals("build", stemmer.stem("build")); 
  }
@Test
  public void testStreamingSuffixStepsPreservedInOutput() {
    Stemmer stemmer = new Stemmer();
    assertEquals("activ", stemmer.stem("activation")); 
  }
@Test
  public void testCompoundSuffixSequenceExecution() {
    Stemmer stemmer = new Stemmer();
    assertEquals("critic", stemmer.stem("criticalness")); 
  }
@Test
  public void testVeryShortInputBelowProcessingThreshold() {
    Stemmer stemmer = new Stemmer();
    assertEquals("hi", stemmer.stem("hi")); 
  }
@Test
  public void testWordWithTrailingWhitespaceShouldIncludeWhitespace() {
    Stemmer stemmer = new Stemmer();
    assertEquals("bike ", stemmer.stem("bikes ")); 
  }
@Test
  public void testWordThatLooksStemmedAlready() {
    Stemmer stemmer = new Stemmer();
    assertEquals("relax", stemmer.stem("relax")); 
  }
@Test
  public void testApplyHandlesEmptyWordObject() {
    Stemmer stemmer = new Stemmer();
    Word word = new Word("");
    Word result = stemmer.apply(word);
    assertEquals("", result.word());
  }
@Test
  public void testApplyHandlesSingleCharacter() {
    Stemmer stemmer = new Stemmer();
    Word word = new Word("a");
    Word result = stemmer.apply(word);
    assertEquals("a", result.word());
  }
@Test
  public void testStemPreservesWordsEndingInLWhenNotDoubleConsonant() {
    Stemmer stemmer = new Stemmer();
    assertEquals("seal", stemmer.stem("seal")); 
  }
@Test
  public void testDoubleLRemovedIfConditionMatches() {
    Stemmer stemmer = new Stemmer();
    assertEquals("controll", stemmer.stem("controlling")); 
  }
@Test
  public void testStemResetAfterConsecutiveCallsWithDifferentLengths() {
    Stemmer stemmer = new Stemmer();
    assertEquals("agreement", stemmer.stem("agreements"));
    assertEquals("happi", stemmer.stem("happiness"));
    assertEquals("relat", stemmer.stem("relational"));
  }
@Test
  public void testEndsWithAtThenDoubleConsonantLRestored() {
    Stemmer stemmer = new Stemmer(); 
    assertEquals("battl", stemmer.stem("battling")); 
  }
@Test
  public void testEndsWithIzTranslatesToIze() {
    Stemmer stemmer = new Stemmer(); 
    assertEquals("finalize", stemmer.stem("finalizing")); 
  }
@Test
  public void testCVCWithWMustNotInsertE() {
    Stemmer stemmer = new Stemmer();
    assertEquals("snow", stemmer.stem("snowing")); 
  }
@Test
  public void testCVCWithXMustNotInsertE() {
    Stemmer stemmer = new Stemmer();
    assertEquals("box", stemmer.stem("boxing")); 
  }
@Test
  public void testCVCWithYMustNotInsertE() {
    Stemmer stemmer = new Stemmer();
    assertEquals("tray", stemmer.stem("traying")); 
  }
@Test
  public void testEndsWithAtShouldBecomeAte() {
    Stemmer stemmer = new Stemmer();
    assertEquals("locate", stemmer.stem("locating")); 
  }
@Test
  public void testEndsWithBlShouldBecomeBle() {
    Stemmer stemmer = new Stemmer();
    assertEquals("trouble", stemmer.stem("troubling")); 
  }
@Test
  public void testSuffixRemovalBlockedWhenNoVowelInStem() {
    Stemmer stemmer = new Stemmer();
    assertEquals("brrr", stemmer.stem("brrred")); 
  }
@Test
  public void testStemShortLengthEqualToTwo() {
    Stemmer stemmer = new Stemmer();
    assertEquals("on", stemmer.stem("on")); 
  }
@Test
  public void testStep3EndsWithOusliBecomesOus() {
    Stemmer stemmer = new Stemmer();
    assertEquals("hazardous", stemmer.stem("hazardousli")); 
  }
@Test
  public void testStep3EndsWithAlliBecomesAl() {
    Stemmer stemmer = new Stemmer();
    assertEquals("function", stemmer.stem("functionalli")); 
  }
@Test
  public void testStep3EndsWithEntliBecomesEnt() {
    Stemmer stemmer = new Stemmer();
    assertEquals("different", stemmer.stem("differentli")); 
  }
@Test
  public void testStep3EndsWithIvitiBecomesIve() {
    Stemmer stemmer = new Stemmer();
    assertEquals("assertive", stemmer.stem("assertivity")); 
  }
@Test
  public void testStep4EndsWithAlizeBecomesAl() {
    Stemmer stemmer = new Stemmer();
    assertEquals("formal", stemmer.stem("formalize")); 
  }
@Test
  public void testStep4EndsWithFulRemoved() {
    Stemmer stemmer = new Stemmer();
    assertEquals("delight", stemmer.stem("delightful")); 
  }
@Test
  public void testStep4EndsWithIcalBecomesIc() {
    Stemmer stemmer = new Stemmer();
    assertEquals("technic", stemmer.stem("technical")); 
  }
@Test
  public void testMinimalStemSizeAtMEquals1PreservesE() {
    Stemmer stemmer = new Stemmer();
    assertEquals("like", stemmer.stem("like")); 
  }
@Test
  public void testFinalELRemovedForMGreaterThan1() {
    Stemmer stemmer = new Stemmer();
    assertEquals("duplic", stemmer.stem("duplicate")); 
  }
@Test
  public void testStep5SuffixOusStrippedProperly() {
    Stemmer stemmer = new Stemmer();
    assertEquals("curi", stemmer.stem("curious")); 
  }
@Test
  public void testStep5SuffixIveStrippedProperly() {
    Stemmer stemmer = new Stemmer();
    assertEquals("construct", stemmer.stem("constructive")); 
  }
@Test
  public void testStep5SuffixAteStrippedProperly() {
    Stemmer stemmer = new Stemmer();
    assertEquals("relate", stemmer.stem("relate")); 
  }
@Test
  public void testCompoundInputShouldApplyMultipleSteps() {
    Stemmer stemmer = new Stemmer();
    assertEquals("sensit", stemmer.stem("sensitiveness")); 
  }
@Test
  public void testFinalDoubleLStrippedIfMGreaterThan1() {
    Stemmer stemmer = new Stemmer();
    assertEquals("instal", stemmer.stem("install")); 
  }
@Test
  public void testFinalDoubleLNotStrippedIfMTooSmall() {
    Stemmer stemmer = new Stemmer();
    assertEquals("ill", stemmer.stem("ill")); 
  }
@Test
  public void testSuffixBilitiBecomesBleStep3() {
    Stemmer stemmer = new Stemmer();
    assertEquals("accept", stemmer.stem("acceptability")); 
  }
@Test
  public void testStep3UnknownSwitchCharFallback() {
    Stemmer stemmer = new Stemmer();
    assertEquals("biologic", stemmer.stem("biological")); 
  }
@Test
  public void testStep3UnknownSuffixNoEffect() {
    Stemmer stemmer = new Stemmer();
    assertEquals("programminz", stemmer.stem("programminz")); 
  }
@Test
  public void testConsMethodYIsConsonantAtPositionZero() {
    Stemmer stemmer = new Stemmer();
    assertEquals("y", stemmer.stem("y")); 
  }
@Test
  public void testConsMethodYIsVowelWhenPreviousIsConsonant() {
    Stemmer stemmer = new Stemmer();
    assertEquals("cry", stemmer.stem("cry")); 
  }
@Test
  public void testConsMethodYIsConsonantWhenPreviousIsVowel() {
    Stemmer stemmer = new Stemmer();
    assertEquals("play", stemmer.stem("play")); 
  }
@Test
  public void testVowelInStemWithOnlyVowelsBeforeSuffix() {
    Stemmer stemmer = new Stemmer();
    assertEquals("agreed", stemmer.stem("agreed")); 
  }
@Test
  public void testVowelInStemWithYActingAsVowel() {
    Stemmer stemmer = new Stemmer();
    assertEquals("apply", stemmer.stem("applying")); 
  }
@Test
  public void testCvcMethodFalseWhenFinalCharIsW() {
    Stemmer stemmer = new Stemmer();
    assertEquals("snow", stemmer.stem("snows")); 
  }
@Test
  public void testCvcMethodFalseWhenMiddleIsConsonant() {
    Stemmer stemmer = new Stemmer();
    assertEquals("shrub", stemmer.stem("shrubbing")); 
  }
@Test
  public void testDoublecTrueWhenLastTwoConsonantsAreSame() {
    Stemmer stemmer = new Stemmer();
    assertEquals("roll", stemmer.stem("rolling")); 
  }
@Test
  public void testDoublecFalseWhenOnlyOneConsonant() {
    Stemmer stemmer = new Stemmer();
    assertEquals("stop", stemmer.stem("stopping")); 
  }
@Test
  public void testEndsFailsWhenSuffixOverlong() {
    Stemmer stemmer = new Stemmer();
    assertEquals("dogmatic", stemmer.stem("dogmatic")); 
  }
@Test
  public void testEndsFailsDueToMismatchLastChar() {
    Stemmer stemmer = new Stemmer();
    assertEquals("adopt", stemmer.stem("adoption")); 
  }
@Test
  public void testStep3SuffixEnciBecomesEnce() {
    Stemmer stemmer = new Stemmer();
    assertEquals("independence", stemmer.stem("independency")); 
  }
@Test
  public void testStep3SuffixAnciBecomesAnce() {
    Stemmer stemmer = new Stemmer();
    assertEquals("brillianc", stemmer.stem("brilliancy")); 
  }
@Test
  public void testStep3SuffixIzerBecomesIze() {
    Stemmer stemmer = new Stemmer();
    assertEquals("modernize", stemmer.stem("modernizer")); 
  }
@Test
  public void testStep3SuffixLogiBecomesLog() {
    Stemmer stemmer = new Stemmer();
    assertEquals("analog", stemmer.stem("analogical")); 
  }
@Test
  public void testStep4SuffixAtiveRemovedFully() {
    Stemmer stemmer = new Stemmer();
    assertEquals("inform", stemmer.stem("informative")); 
  }
@Test
  public void testStep4SuffixNessRemoved() {
    Stemmer stemmer = new Stemmer();
    assertEquals("kind", stemmer.stem("kindness")); 
  }
@Test
  public void testStep5SuffixEntRemoved() {
    Stemmer stemmer = new Stemmer();
    assertEquals("diff", stemmer.stem("different")); 
  }
@Test
  public void testStep5SuffixMentRemovedCorrectly() {
    Stemmer stemmer = new Stemmer();
    assertEquals("enjoy", stemmer.stem("enjoyment")); 
  }
@Test
  public void testStep5SuffixOuMatchedAndStripped() {
    Stemmer stemmer = new Stemmer();
    assertEquals("curi", stemmer.stem("curious")); 
  }
@Test
  public void testStep6RemovesFinalLDoubleIfMGreaterThan1() {
    Stemmer stemmer = new Stemmer();
    assertEquals("trave", stemmer.stem("travelled")); 
  }
@Test
  public void testStep6FinalELRemovedIfMGT1() {
    Stemmer stemmer = new Stemmer();
    assertEquals("creat", stemmer.stem("create")); 
  }
@Test
  public void testStemAfterEdWhenMEquals0ShouldNotStrip() {
    Stemmer stemmer = new Stemmer();
    assertEquals("bled", stemmer.stem("bled")); 
  }
@Test
  public void testToStringReturnsEmptyAfterEmptyInput() {
    Stemmer stemmer = new Stemmer();
    assertEquals("", stemmer.stem("")); 
    assertEquals("", stemmer.toString()); 
  }
@Test
  public void testInputThatLooksPluralButEndsInDoubleS() {
    Stemmer stemmer = new Stemmer();
    assertEquals("glass", stemmer.stem("glasses")); 
  }
@Test
  public void testWordEndingInEedWithMZeroShouldSkip() {
    Stemmer stemmer = new Stemmer();
    assertEquals("deed", stemmer.stem("deed")); 
  }
@Test
  public void testEmptyWordObjectApplyShouldReturnEmptyString() {
    Stemmer stemmer = new Stemmer();
    Word word = new Word("");
    assertEquals("", stemmer.apply(word).word());
  }
@Test
  public void testInputWithSpacesShouldNotCrash() {
    Stemmer stemmer = new Stemmer();
    assertEquals("file ", stemmer.stem("files ")); 
  }
@Test
  public void testNonTypicalButValidInputLongLength() {
    Stemmer stemmer = new Stemmer();
    assertEquals("pneumonoultramicroscopicsilicovolcanoconiosi", stemmer.stem("pneumonoultramicroscopicsilicovolcanoconiosis")); 
  } 
}