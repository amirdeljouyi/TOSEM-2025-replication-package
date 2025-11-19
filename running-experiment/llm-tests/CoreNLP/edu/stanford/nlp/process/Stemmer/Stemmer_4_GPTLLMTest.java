package edu.stanford.nlp.process;

import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.process.Stemmer;
import org.junit.Test;

import static org.junit.Assert.*;

public class Stemmer_4_GPTLLMTest {

 @Test
  public void testPluralForms1() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("caresses");
    assertEquals("caress", result);
  }
@Test
  public void testPluralForms2() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("ponies");
    assertEquals("poni", result);
  }
@Test
  public void testPluralForms3() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("ties");
    assertEquals("ti", result);
  }
@Test
  public void testPluralForms4() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("cats");
    assertEquals("cat", result);
  }
@Test
  public void testAlreadyStemmed() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("caress");
    assertEquals("caress", result);
  }
@Test
  public void testEdEnding1() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("agreed");
    assertEquals("agree", result);
  }
@Test
  public void testEdEnding2() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("disabled");
    assertEquals("disable", result);
  }
@Test
  public void testEdEnding3() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("feed");
    assertEquals("feed", result);
  }
@Test
  public void testIngEnding1() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("matting");
    assertEquals("mat", result);
  }
@Test
  public void testIngEnding2() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("mating");
    assertEquals("mate", result);
  }
@Test
  public void testIngEnding3() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("meeting");
    assertEquals("meet", result);
  }
@Test
  public void testIngEnding4() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("messing");
    assertEquals("mess", result);
  }
@Test
  public void testIngEnding5() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("milling");
    assertEquals("mill", result);
  }
@Test
  public void testIngPluralCompound() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("meetings");
    assertEquals("meet", result);
  }
@Test
  public void testYtoIConversion() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("happy");
    assertEquals("happi", result);
  }
@Test
  public void testNoYReplacement() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("sky");
    assertEquals("sky", result);
  }
@Test
  public void testStep3Suffix1() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("relational");
    assertEquals("relate", result);
  }
@Test
  public void testStep3Suffix2() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("conditional");
    assertEquals("condition", result);
  }
@Test
  public void testStep3Suffix3() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("valenci");
    assertEquals("valence", result);
  }
@Test
  public void testStep4Suffix1() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("electrical");
    assertEquals("electric", result);
  }
@Test
  public void testStep4Suffix2() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("hopeful");
    assertEquals("hope", result);
  }
@Test
  public void testStep4Suffix3() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("goodness");
    assertEquals("good", result);
  }
@Test
  public void testStep5Suffix() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("formally");
    assertEquals("formal", result);
  }
@Test
  public void testFinalERemoval() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("cease");
    assertEquals("ceas", result);
  }
@Test
  public void testDoubleLReduction() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("controll");
    assertEquals("control", result);
  }
@Test
  public void testStemWordObject() {
    Stemmer stemmer = new Stemmer();
    Word word = new Word("caresses");
    Word result = stemmer.stem(word);
    assertEquals("caress", result.word());
  }
@Test
  public void testApplyWord() {
    Stemmer stemmer = new Stemmer();
    Word word = new Word("mating");
    Word result = stemmer.apply(word);
    assertEquals("mate", result.word());
  }
@Test
  public void testEmptyString() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("");
    assertEquals("", result);
  }
@Test
  public void testSingleLetters1() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("a");
    assertEquals("a", result);
  }
@Test
  public void testSingleLetters2() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("x");
    assertEquals("x", result);
  }
@Test
  public void testSingleLetters3() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("y");
    assertEquals("y", result);
  }
@Test
  public void testTwoLetterWord() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("at");
    assertEquals("at", result);
  }
@Test
  public void testNumericWord() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("123");
    assertEquals("123", result);
  }
@Test
  public void testPunctuationInput() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("-?!");
    assertEquals("-?!", result);
  }
@Test
  public void testAlphaNumericWord() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("file123");
    assertEquals("file123", result);
  }
@Test
  public void testAnotherAlphaNumeric() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("web2");
    assertEquals("web2", result);
  }
@Test
  public void testConsecutiveCalls1() {
    Stemmer stemmer = new Stemmer();
    String result1 = stemmer.stem("running");
    assertEquals("run", result1);
    String result2 = stemmer.stem("agreed");
    assertEquals("agree", result2);
  }
@Test
  public void testConsecutiveCalls2() {
    Stemmer stemmer = new Stemmer();
    String result1 = stemmer.stem("meetings");
    assertEquals("meet", result1);
    String result2 = stemmer.stem("hopping");
    assertEquals("hop", result2);
    String result3 = stemmer.stem("hoped");
    assertEquals("hope", result3);
  }
@Test
  public void testLargeInput() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
    assertNotNull(result);
    assertTrue(result.length() <= 100);
  }
@Test
  public void testBugFixIonEnding() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("revision");
    assertEquals("revision", result);
  }
@Test
  public void testBugFixShortInput() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("a");
    assertEquals("a", result);
  }
@Test
  public void testShortWordLengthTwo() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("be");
    assertEquals("be", result);
  }
@Test
  public void testShortWordLengthThree() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("bat");
    assertEquals("bat", result);
  }
@Test
  public void testWordEndingWithSNotPlural() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("gas");
    assertEquals("gas", result);
  }
@Test
  public void testDoubleConsonantEndsWithL() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("controlled");
    assertEquals("control", result);
  }
@Test
  public void testDoubleConsonantEndsWithS() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("missed");
    assertEquals("miss", result);
  }
@Test
  public void testDoubleConsonantEndsWithZ() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("buzzed");
    assertEquals("buzz", result);
  }
@Test
  public void testCvcEndingWordsShouldAddE() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("hoping");
    assertEquals("hope", result);
  }
@Test
  public void testCvcFalseDueToFinalY() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("tray");
    assertEquals("tray", result);
  }
@Test
  public void testCvcFalseDueToFinalW() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("snow");
    assertEquals("snow", result);
  }
@Test
  public void testStep3EndsWithLogi() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("analogical");
    assertEquals("analog", result);
  }
@Test
  public void testStep3WithNoMatchingSuffix() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("birthday");
    assertEquals("birthday", result);
  }
@Test
  public void testStep5EndsWithIonAndPrecededByST() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("prevention");
    assertEquals("prevent", result);
  }
@Test
  public void testStep5EndsWithIonAndNotPrecededByST() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("champion");
    assertEquals("champion", result);
  }
@Test
  public void testFinalStep6DropFinalE() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("make");
    assertEquals("make", result); 
  }
@Test
  public void testStep2YToIAtEnd() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("party");
    assertEquals("parti", result);
  }
@Test
  public void testStep2YToINoVowelBefore() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("cry");
    assertEquals("cry", result);
  }
@Test
  public void testApplyNullWordThrowsException() {
    Stemmer stemmer = new Stemmer();
    try {
      stemmer.apply(null);
      fail("NullPointerException expected but not thrown");
    } catch (NullPointerException | ClassCastException e) {
      
      assertTrue(e instanceof NullPointerException || e instanceof ClassCastException);
    }
  }
@Test
  public void testWordContainingSingleVowel() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("ate");
    assertEquals("ate", result);
  }
@Test
  public void testWordThatTriggersSetToAndReduce() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("singing");
    assertEquals("sing", result);
  }
@Test
  public void testWordEndingWithAt() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("combat");
    assertEquals("comate", result); 
  }
@Test
  public void testWordEndingWithBl() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("nobbl");
    assertEquals("nobble", result);
  }
@Test
  public void testWordEndingWithIz() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("accessoriz");
    assertEquals("accessorize", result);
  }
@Test
  public void testStemIdempotencyWithSameWordTwiceIdentical() {
    Stemmer stemmer = new Stemmer();
    String result1 = stemmer.stem("running");
    assertEquals("run", result1);
    String result2 = stemmer.stem("running");
    assertEquals("run", result2);
  }
@Test
  public void testStemSafeOnWhitespaceOnly() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem(" ");
    assertEquals(" ", result);
  }
@Test
  public void testStemSafeOnTabOnly() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("\t");
    assertEquals("\t", result);
  }
@Test
  public void testStemWordWithCapitalLettersPreserved() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("Walking");
    assertEquals("Walk", result); 
  }
@Test
  public void testStemNullInputStringThrowsException() {
    try {
      Stemmer stemmer = new Stemmer();
      stemmer.stem((String) null);
      fail("Expected NullPointerException");
    } catch (NullPointerException e) {
      
    }
  }
@Test
  public void testEndsWithAlismSuffix() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("vandalism");
    assertEquals("vandal", result);
  }
@Test
  public void testEndsWithFulnessSuffix() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("hopefulness");
    assertEquals("hope", result);
  }
@Test
  public void testEndsWithOusnessSuffix() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("joyousness");
    assertEquals("joyous", result);
  }
@Test
  public void testEndsWithAlitiSuffix() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("formality");
    assertEquals("formal", result);
  }
@Test
  public void testEndsWithIvitiSuffix() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("sensitivity");
    assertEquals("sensitiv", result);
  }
@Test
  public void testEndsWithBilitiSuffix() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("flexibility");
    assertEquals("flexibl", result);
  }
@Test
  public void testEndsWithLogiSuffix() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("theology");
    assertEquals("theolog", result);
  }
@Test
  public void testEndsWithIcalSuffix() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("logical");
    assertEquals("logic", result);
  }
@Test
  public void testEndsWithIcitiSuffix() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("electricity");
    assertEquals("electric", result);
  }
@Test
  public void testEndsWithNessSuffixAfterShortWord() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("kindness");
    assertEquals("kind", result);
  }
@Test
  public void testEndsWithAbleInStep5() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("readable");
    assertEquals("read", result);
  }
@Test
  public void testEndsWithEmentSuffix() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("agreement");
    assertEquals("agree", result);
  }
@Test
  public void testEndsWithEntSuffix() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("dependent");
    assertEquals("depend", result);
  }
@Test
  public void testEndsWithOusSuffix() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("curious");
    assertEquals("curi", result);
  }
@Test
  public void testEndsWithAteInStep5() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("dictate");
    assertEquals("dict", result);
  }
@Test
  public void testEndsWithItiInStep5() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("sincerity");
    assertEquals("sincer", result);
  }
@Test
  public void testEndsWithIveSuffixStep5() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("productive");
    assertEquals("product", result);
  }
@Test
  public void testEndsWithIzeSuffixStep5() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("finalize");
    assertEquals("final", result);
  }
@Test
  public void testEndsWithTionalSuffix() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("operational");
    assertEquals("operation", result);
  }
@Test
  public void testEndsWithAtButNotReducedBecauseNoVowelInStem() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("brat");
    assertEquals("brat", result);
  }
@Test
  public void testStep1IngNotRemovedDueToNoVowelBefore() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("brrring");
    assertEquals("brrring", result);
  }
@Test
  public void testStep1EdNotRemovedDueToNoVowel() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("sked");
    assertEquals("sked", result);
  }
@Test
  public void testStemWordContainingNoVowels() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("rhythms");
    assertEquals("rhythm", result);
  }
@Test
  public void testStemAllConsonantsEdgeCase() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("brrr");
    assertEquals("brrr", result);
  }
@Test
  public void testSuffixEndsBliMappedToBle() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("nobli");
    assertEquals("noble", result);
  }
@Test
  public void testSuffixEndsEliMappedToE() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("genteli");
    assertEquals("gente", result);
  }
@Test
  public void testSuffixEndsEntliToEnt() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("consistently");
    assertEquals("consistent", result);
  }
@Test
  public void testStep2SuffixRemovalYReplacementBoundary() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("apply");
    assertEquals("appli", result);
  }
@Test
  public void testWhitespacePaddedWordsTrimmedIncorrectly() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem(" testing ");
    assertEquals("test", result);
  }
@Test
  public void testEndsWithAtAndTrimsDoubleConsonantL() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("batt");
    assertEquals("bat", result);
  }
@Test
  public void testEndsWithAtAndCvcMatchShortWord() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("hop");
    assertEquals("hop", result);
  }
@Test
  public void testEndDoubleConsonantAtLimitZShouldNotTrimZ() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("buzzing");
    assertEquals("buzz", result);
  }
@Test
  public void testWordMatchOnlyStep1CvcWithNoMatchInLaterSteps() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("hugging");
    assertEquals("hug", result);
  }
@Test
  public void testWordWithRepeatedConsonantThatShouldNotBeTrimmed() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("pressing");
    assertEquals("press", result);
  }
@Test
  public void testVeryShortUnstemmedWordWithNumbers() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("2b");
    assertEquals("2b", result);
  }
@Test
  public void testInputEndingInSWithoutPluralIntent() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("his");
    assertEquals("hi", result);
  }
@Test
  public void testEdgeCaseEndsWithSButDoubleSSNotPresent() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("gas");
    assertEquals("gas", result);
  }
@Test
  public void testApplyWordWithEmptyContent() {
    Stemmer stemmer = new Stemmer();
    Word word = new Word("");
    Word result = stemmer.apply(word);
    assertEquals("", result.word());
  }
@Test
  public void testInputThatTriggersSetToFromEndsWithBl() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("troubl");
    assertEquals("trouble", result);
  }
@Test
  public void testEndsWithAzNoSpecialSuffixShouldRemainSame() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("jazz");
    assertEquals("jazz", result);
  }
@Test
  public void testIgnoresIrregularCapitalizationAllUpper() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("RUNNING");
    assertEquals("RUNN", result); 
  }
@Test
  public void testSuffixEndsWithOtionShouldBeConvertedProperly() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("demotion");
    assertEquals("demote", result); 
  }
@Test
  public void testStep3WithSuffixizationTriggersCompoundRule() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("actualization");
    assertEquals("actualize", result);
  }
@Test
  public void testSuffixizationWithShorterMatch() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("saturation");
    assertEquals("saturate", result);
  }
@Test
  public void testWordThatOnlyMatchesStep3Suffix() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("finalizer");
    assertEquals("finalize", result);
  }
@Test
  public void testWordWithStep4SuffixButNoMgtZero() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("logic");
    assertEquals("logic", result);
  }
@Test
  public void testWordWithShortSuffixThatShouldNotBeTrimmed() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("ice");
    assertEquals("ice", result);
  }
@Test
  public void testConsonantYAtStart() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("yell");
    assertEquals("yell", result);
  }
@Test
  public void testVowelYMidword() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("playing");
    assertEquals("play", result);
  }
@Test
  public void testSingleUpperCaseLetter() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("A");
    assertEquals("A", result);
  }
@Test
  public void testSingleDigit() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("9");
    assertEquals("9", result);
  }
@Test
  public void testEndsWithLyFollowedByStep3LiSuffix() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("brutally");
    assertEquals("brutal", result);
  }
@Test
  public void testInputThatTriggersMultipleStepSuffixes() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("privatization");
    assertEquals("privatize", result); 
  }
@Test
  public void testEndsWithAntForStep5Cond() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("tenant");
    assertEquals("ten", result);
  }
@Test
  public void testEndsWithEntInStep5Triggered() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("independent");
    assertEquals("independ", result);
  }
@Test
  public void testOnlyStep6TriggeredViaFinalE() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("code");
    assertEquals("cod", result);
  }
@Test
  public void testEndsWithLDoubleConsonantFinalReduction() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("controlled");
    assertEquals("control", result);
  }
@Test
  public void testEndsWithLButNotDoublec() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("total");
    assertEquals("total", result);
  }
@Test
  public void testLongCompoundSuffixMultipleSteps() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("nationalization");
    assertEquals("nationalize", result);
  }
@Test
  public void testEndsWithOUSLIToOUS() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("graciously");
    assertEquals("gracious", result);
  }
@Test
  public void testEndsWithENTLIToENT() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("recently");
    assertEquals("recent", result);
  }
@Test
  public void testEndsWithALLIToAL() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("formally");
    assertEquals("formal", result);
  }
@Test
  public void testEndsWithELIToE() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("intensely");
    assertEquals("intense", result);
  }
@Test
  public void testEndsWithTIONALToTION() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("educational");
    assertEquals("education", result);
  }
@Test
  public void testEndsWithIZATIONToIZE() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("legalization");
    assertEquals("legalize", result);
  }
@Test
  public void testEndsWithATORToATE() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("creator");
    assertEquals("create", result);
  }
@Test
  public void testEndsWithALISMToAL() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("regionalism");
    assertEquals("regional", result);
  }
@Test
  public void testMEquals1CvcEndsWithE() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("hoping");
    assertEquals("hope", result);
  }
@Test
  public void testStep1DoubleConsonantTrimmingS() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("missing");
    assertEquals("miss", result);
  }
@Test
  public void testStep1DoubleConsonantTrimmingL() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("milling");
    assertEquals("mill", result);
  }
@Test
  public void testStep1DoubleConsonantRetainsZ() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("buzzing");
    assertEquals("buzz", result);
  }
@Test
  public void testStep5MEqualsOneAndEndsIonWithS() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("admission");
    assertEquals("admiss", result);
  }
@Test
  public void testStep5MEqualsOneAndEndsIonWithT() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("position");
    assertEquals("posit", result);
  }
@Test
  public void testStep5MEqualsOneEndsIonWithoutST() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("fashion");
    assertEquals("fashion", result);
  }
@Test
  public void testStep6DropFinalE_MEquals1_CVCFalse() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("hate");
    assertEquals("hat", result);
  }
@Test
  public void testStep6KeepsFinalEDueToCVCTrue() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("cave");
    assertEquals("cave", result);
  }
@Test
  public void testInputWithRepeatingSuffixAfterStemTransform() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("nationalationalism");
    assertEquals("nation", result); 
  }
@Test
  public void testVariousTransformationMZeroNoAction() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("ied");
    assertEquals("i", result); 
  }
@Test
  public void testMEquals0PreventsStep3Change() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("bli");
    assertEquals("bli", result);
  }
@Test
  public void testPreventFinalLConsonantDropWhenMIs1Only() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("pull");
    assertEquals("pull", result); 
  }
@Test
  public void testPreventFinalLConsonantDropWhenMIs2() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("rolled");
    assertEquals("roll", result); 
  }
@Test
  public void testDoubleConsonantKIsNotTrimmed() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("trekking");
    assertEquals("trek", result); 
  }
@Test
  public void testCvcReturnsFalseWhenConsonantFollowedByTwoVowels() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("cooing");
    assertEquals("coo", result);
  }
@Test
  public void testCvcReturnsFalseWhenFinalLetterIsW() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("glow");
    assertEquals("glow", result);
  }
@Test
  public void testCvcReturnsFalseWhenFinalLetterIsX() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("box");
    assertEquals("box", result);
  }
@Test
  public void testCvcReturnsFalseWhenFinalLetterIsY() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("tray");
    assertEquals("tray", result);
  }
@Test
  public void testEdSuffixNotRemovedDueToAbsenceOfVowel() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("tcked");
    assertEquals("tcked", result); 
  }
@Test
  public void testEndsWithANCE() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("performance");
    assertEquals("perform", result);
  }
@Test
  public void testEndsWithENCE() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("preference");
    assertEquals("prefer", result);
  }
@Test
  public void testEndsWithER() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("baker");
    assertEquals("bake", result);
  }
@Test
  public void testEndsWithMENT() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("enjoyment");
    assertEquals("enjoy", result);
  }
@Test
  public void testMEqualsZeroSkipsSettoInR() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("bli");
    assertEquals("bli", result); 
  }
@Test
  public void testCvcMatchTriggersEAddition() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("mad");
    assertEquals("mad", result); 
  }
@Test
  public void testStemDoesNotChangeWordWithoutMatchingSuffix() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("banana");
    assertEquals("banana", result);
  }
@Test
  public void testWordEndingWithNESSAsOnlyApplicableStep4Suffix() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("kindness");
    assertEquals("kind", result);
  }
@Test
  public void testStemmingWordEndingWithICITI() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("authenticity");
    assertEquals("authentic", result);
  }
@Test
  public void testStemmingWordEndingWithFUL() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("thankful");
    assertEquals("thank", result);
  }
@Test
  public void testSuffixENTButMEqualsZeroSoNoChange() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("tent");
    assertEquals("tent", result);
  }
@Test
  public void testInputWithConsecutiveVowels() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("agreeing");
    assertEquals("agree", result);
  }
@Test
  public void testApplyWithWordEndingInIZED() {
    Stemmer stemmer = new Stemmer();
    Word word = new Word("finalized");
    Word result = stemmer.apply(word);
    assertEquals("final", result.word());
  }
@Test
  public void testStemEndingInOUSNESS() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("graciousness");
    assertEquals("gracious", result);
  }
@Test
  public void testStemEndingATIVENoChangeIfMZero() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("native");
    assertEquals("native", result);
  }
@Test
  public void testStep4EndsWithALIZE() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("finalize");
    assertEquals("final", result);
  }
@Test
  public void testTokenEndsWithICATE() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("duplicate");
    assertEquals("duplic", result);
  }
@Test
  public void testEdgeCaseCVCWithShortWord() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("mat");
    assertEquals("mat", result); 
  }
@Test
  public void testInputConsistingOfJustY() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("y");
    assertEquals("y", result); 
  }
@Test
  public void testWordEndingWithYButNoVowelBeforeIt() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("shy");
    assertEquals("shy", result); 
  }
@Test
  public void testWordEndingWithYWithVowelBeforeIt() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("funny");
    assertEquals("funni", result); 
  }
@Test
  public void testToStringReturnsStemmedFormAfterStem() {
    Stemmer stemmer = new Stemmer();
    stemmer.stem("argued");
    String result = stemmer.toString();
    assertEquals("argu", result);
  }
@Test
  public void testApplyReturnsStemmedWordWithDifferentLength() {
    Stemmer stemmer = new Stemmer();
    Word word = new Word("meetings");
    Word result = stemmer.apply(word);
    assertEquals("meet", result.word());
  }
@Test
  public void testStep6DropFinalEWhenMEqualsOneAndNotCVC() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("rate");
    assertEquals("rat", result);
  }
@Test
  public void testStep6KeepsFinalEWhenCVCTrue() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("love");
    assertEquals("love", result);
  }
@Test
  public void testEndsWithIVEInStep5() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("explosive");
    assertEquals("explos", result);
  }
@Test
  public void testSuffixOU() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("famous");
    assertEquals("famous", result); 
  }
@Test
  public void testWithSingleNonAlphaCharacter() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem(".");
    assertEquals(".", result);
  }
@Test
  public void testNoMatchingSuffixEndsWithLY() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("ugly");
    assertEquals("ugli", result); 
  }
@Test
  public void testWordEndsWithIVE() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("massive");
    assertEquals("mass", result);
  }
@Test
  public void testStep5EndsWithOUSNoParentMatch() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("dangerous");
    assertEquals("danger", result);
  }
@Test
  public void testStep5EndsWithALNoMatch() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("journal");
    assertEquals("journal", result);
  }
@Test
  public void testStep5EndsWithENCEButMZeroNoChange() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("fence");
    assertEquals("fenc", result); 
  }
@Test
  public void testStep4RemovesICATECorrectly() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("communicate");
    assertEquals("communic", result);
  }
@Test
  public void testWordEndingWithIZER() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("minimizer");
    assertEquals("minimize", result);
  }
@Test
  public void testEdgeCaseShortizingWord() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("sizing");
    assertEquals("size", result); 
  }
@Test
  public void testFinalLWithLongStemMGreaterThanOne() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("controll");
    assertEquals("control", result); 
  }
@Test
  public void testEndsWithABLE() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("enjoyable");
    assertEquals("enjoy", result);
  }
@Test
  public void testEndsWithIBLE() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("legible");
    assertEquals("leg", result);
  }
@Test
  public void testEndingWithALWithoutTransformation() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("goal");
    assertEquals("goal", result);
  }
@Test
  public void testSuffixENTInStep5() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("absorbent");
    assertEquals("absorb", result);
  }
@Test
  public void testEndsWithISMAndStep5Triggers() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("dualism");
    assertEquals("dual", result);
  }
@Test
  public void testEndsWithMENTTruncationAndStep5() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("investment");
    assertEquals("invest", result);
  }
@Test
  public void testStep1PluralFormCaressNoChange() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("caress");
    assertEquals("caress", result);
  }
@Test
  public void testStemIdempotencyMultipleCalls() {
    Stemmer stemmer = new Stemmer();
    String first = stemmer.stem("agreeing");
    assertEquals("agree", first);
    String second = stemmer.stem("agreeing");
    assertEquals("agree", second); 
  }
@Test
  public void testConsonantAtStartAndFinalY() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("try");
    assertEquals("tri", result);
  }
@Test
  public void testStemStep1DoubleLWithMEqualsOne() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("billing");
    assertEquals("bill", result);
  }
@Test
  public void testStemShortInputSkippedByMainStemmerLogic() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("on");
    assertEquals("on", result);
  }
@Test
  public void testTrailingWhiteSpaceInput() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("meeting ");
    assertEquals("meetin", result); 
  }
@Test
  public void testUppercaseInputReturnsPartiallyTransformed() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("CONNECTING");
    assertEquals("CONNECT", result); 
  }
@Test
  public void testBufferedInputCausesExpansion() {
    Stemmer stemmer = new Stemmer();
    String longInput = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaing";
    String result = stemmer.stem(longInput);
    assertTrue(result.length() <= longInput.length());
    assertFalse(result.isEmpty());
  }
@Test
  public void testBufferedInputWithSuffixAtCapacity() {
    Stemmer stemmer = new Stemmer();
    String longInput = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaabling";
    String result = stemmer.stem(longInput);
    assertFalse(result.isEmpty());
  }
@Test
  public void testUnderscoreSymbolPreserved() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("abc_def");
    assertEquals("abc_def", result);
  }
@Test
  public void testHyphenSymbolPreserved() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("pre-process");
    assertEquals("pre-process", result);
  }
@Test
  public void testQuotedWordNoEffect() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("\"agreeing\"");
    assertEquals("\"agree", result);
  } 
}