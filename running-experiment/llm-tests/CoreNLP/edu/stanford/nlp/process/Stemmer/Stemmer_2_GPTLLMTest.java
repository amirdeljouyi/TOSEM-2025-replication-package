package edu.stanford.nlp.process;

import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.process.Stemmer;
import org.junit.Test;

import static org.junit.Assert.*;

public class Stemmer_2_GPTLLMTest {

 @Test
  public void testPluralCats() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("cats");
    assertEquals("cat", result);
  }
@Test
  public void testPluralCaresses() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("caresses");
    assertEquals("caress", result);
  }
@Test
  public void testPluralPonies() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("ponies");
    assertEquals("poni", result);
  }
@Test
  public void testPluralTies() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("ties");
    assertEquals("ti", result);
  }
@Test
  public void testPastTenseAgreed() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("agreed");
    assertEquals("agree", result);
  }
@Test
  public void testPastTenseDisabled() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("disabled");
    assertEquals("disable", result);
  }
@Test
  public void testPastTenseFeed() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("feed");
    assertEquals("feed", result);
  }
@Test
  public void testGerundMating() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("mating");
    assertEquals("mate", result);
  }
@Test
  public void testGerundHopping() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("hopping");
    assertEquals("hop", result);
  }
@Test
  public void testGerundMeeting() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("meeting");
    assertEquals("meet", result);
  }
@Test
  public void testGerundMessing() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("messing");
    assertEquals("mess", result);
  }
@Test
  public void testYToIHappy() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("happy");
    assertEquals("happi", result);
  }
@Test
  public void testYPreservedCry() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("cry");
    assertEquals("cry", result);
  }
@Test
  public void testSuffixRelational() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("relational");
    assertEquals("relate", result);
  }
@Test
  public void testSuffixValenci() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("valenci");
    assertEquals("valence", result);
  }
@Test
  public void testSuffixElectrical() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("electrical");
    assertEquals("electric", result);
  }
@Test
  public void testSuffixUsefulness() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("usefulness");
    assertEquals("use", result);
  }
@Test
  public void testSuffixCreation() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("creation");
    assertEquals("create", result);
  }
@Test
  public void testFinalECreate() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("creating");
    assertEquals("create", result);
  }
@Test
  public void testConnective() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("connective");
    assertEquals("connect", result);
  }
@Test
  public void testFinalERate() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("rater");
    assertEquals("rate", result);
  }
@Test
  public void testEmptyString() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("");
    assertEquals("", result);
  }
@Test
  public void testSingleCharacterA() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("a");
    assertEquals("a", result);
  }
@Test
  public void testSingleCharacterB() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("b");
    assertEquals("b", result);
  }
@Test
  public void testShortWordIs() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("is");
    assertEquals("is", result);
  }
@Test
  public void testShortWordBy() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("by");
    assertEquals("by", result);
  }
@Test
  public void testIrregularPluralMouse() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("mouse");
    assertEquals("mouse", result);
  }
@Test
  public void testRootBook() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("book");
    assertEquals("book", result);
  }
@Test
  public void testDoubleConsonantTripping() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("tripping");
    assertEquals("trip", result);
  }
@Test
  public void testDoubleConsonantPlanning() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("planning");
    assertEquals("plan", result);
  }
@Test
  public void testUpperCaseNotLowered() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("Running");
    assertEquals("Running", result);
  }
@Test
  public void testLowerCaseRun() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("run");
    assertEquals("run", result);
  }
@Test
  public void testApplyReturnsStemmedWord() {
    Stemmer stemmer = new Stemmer();
    Word input = new Word("running");
    Word result = stemmer.apply(input);
    assertEquals(new Word("run"), result);
  }
@Test
  public void testStemWordInstance() {
    Stemmer stemmer = new Stemmer();
    Word word = new Word("running");
    Word result = stemmer.stem(word);
    assertEquals("run", result.word());
  }
@Test
  public void testToStringReflectsStemmedWord() {
    Stemmer stemmer = new Stemmer();
    stemmer.stem("dancing");
    String result = stemmer.toString();
    assertEquals("danc", result);
  }
@Test
  public void testCachedBufferCleared() {
    Stemmer stemmer = new Stemmer();
    String result1 = stemmer.stem("performing");
    assertEquals("perform", result1);
    String result2 = stemmer.stem("performed");
    assertEquals("perform", result2);
  }
@Test(expected = NullPointerException.class)
  public void testNullInputStringThrowsException() {
    Stemmer stemmer = new Stemmer();
    stemmer.stem((String) null);
  }
@Test(expected = NullPointerException.class)
  public void testNullInputWordThrowsException() {
    Stemmer stemmer = new Stemmer();
    stemmer.stem((Word) null);
  }
@Test(expected = ClassCastException.class)
  public void testApplyWithInvalidObjectTypeThrowsException() {
    Stemmer stemmer = new Stemmer();
    @SuppressWarnings("unchecked")
    java.util.function.Function<Object, Word> function = o -> stemmer.apply((Word) o);
    function.apply("not a Word object");
  }
@Test
public void testSuffixEndsWithIonViaST() {
  Stemmer stemmer = new Stemmer();
  String result = stemmer.stem("attention");
  assertEquals("attent", result);
}
@Test
public void testSuffixEndsWithIonInvalidSContext() {
  Stemmer stemmer = new Stemmer();
  String result = stemmer.stem("million");
  assertEquals("million", result); 
}
@Test
public void testStep3EndsWithLogi() {
  Stemmer stemmer = new Stemmer();
  String result = stemmer.stem("analogical");
  assertEquals("analog", result);
}
@Test
public void testStep4EndsWithNess() {
  Stemmer stemmer = new Stemmer();
  String result = stemmer.stem("kindness");
  assertEquals("kind", result);
}
@Test
public void testStep4EndsWithFull() {
  Stemmer stemmer = new Stemmer();
  String result = stemmer.stem("hopeful");
  assertEquals("hope", result);
}
@Test
public void testCvcEConditionTrue() {
  Stemmer stemmer = new Stemmer();
  String result = stemmer.stem("hop");
  assertEquals("hop", result); 
}
@Test
public void testCvcEConditionTriggers() {
  Stemmer stemmer = new Stemmer();
  String result = stemmer.stem("hug");
  assertEquals("hug", result); 
}
@Test
public void testRepeatedConsonantElsPreserved() {
  Stemmer stemmer = new Stemmer();
  String result = stemmer.stem("milling");
  assertEquals("mill", result);
}
@Test
public void testRepeatedConsonantZPreserved() {
  Stemmer stemmer = new Stemmer();
  String result = stemmer.stem("buzzing");
  assertEquals("buzz", result);
}
@Test
public void testFinalEWithShortWord() {
  Stemmer stemmer = new Stemmer();
  String result = stemmer.stem("cave");
    assertEquals("cave", result);
}
@Test
public void testBufferResizeExpansion() {
  Stemmer stemmer = new Stemmer();
  
  String input = "supercalifragilisticexpialidociousabcdefghijklmno";
  String result = stemmer.stem(input);
  assertNotNull(result); 
}
@Test
public void testIdempotentAfterStemming() {
  Stemmer stemmer = new Stemmer();
  String first = stemmer.stem("running");
  assertEquals("run", first);
  String second = stemmer.stem(first);
  assertEquals("run", second);
}
@Test
public void testNonAlphabeticInputSymbols() {
  Stemmer stemmer = new Stemmer();
  String result = stemmer.stem("!@#");
  assertEquals("!@#", result);
}
@Test
public void testNonAlphabeticInputDigits() {
  Stemmer stemmer = new Stemmer();
  String result = stemmer.stem("123456");
  assertEquals("123456", result);
}
@Test
public void testEdgeCaseAt() {
  Stemmer stemmer = new Stemmer();
  String result = stemmer.stem("at");
  assertEquals("at", result); 
}
@Test
public void testEndsAliReturnsAl() {
  Stemmer stemmer = new Stemmer();
  String result = stemmer.stem("optionally");
  assertEquals("option", result); 
}
@Test
public void testEndsEliReturnsE() {
  Stemmer stemmer = new Stemmer();
  String result = stemmer.stem("nicely");
  assertEquals("nice", result);
}
@Test
public void testEndsLogiBranchTaken() {
  Stemmer stemmer = new Stemmer();
  String result = stemmer.stem("logical");
  assertEquals("logic", result); 
}
@Test
public void testVeryShortWordTooSmallToStem() {
  Stemmer stemmer = new Stemmer();
  String result = stemmer.stem("be");
  assertEquals("be", result); 
}
@Test
public void testInputContainingYAtBeginning() {
  Stemmer stemmer = new Stemmer();
  String result = stemmer.stem("yell");
  assertEquals("yell", result); 
}
@Test
public void testInputWithEndingYConsonantCheck() {
  Stemmer stemmer = new Stemmer();
  String result = stemmer.stem("clumsy");
  assertEquals("clumsi", result); 
}
@Test
  public void testWordEndingWithAbleSuffix() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("adjustable");
    assertEquals("adjust", result);
  }
@Test
  public void testWordEndingWithAntSuffix() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("reluctant");
    assertEquals("reluct", result);
  }
@Test
  public void testWordEndingWithEntSuffix() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("different");
    assertEquals("differ", result);
  }
@Test
  public void testWordEndingWithMentSuffix() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("development");
    assertEquals("develop", result);
  }
@Test
  public void testWordEndingWithOuSuffix() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("famous");
    assertEquals("famou", result);
  }
@Test
  public void testWordEndingWithOusSuffix() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("gracious");
    assertEquals("graci", result);
  }
@Test
  public void testWordEndingWithItiSuffix() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("sensitivity");
    assertEquals("sensit", result);
  }
@Test
  public void testWordEndingWithBilitiSuffix() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("capability");
    assertEquals("capabl", result);
  }
@Test
  public void testWordWithOnlyVowels() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("aeiou");
    assertEquals("aeiou", result);
  }
@Test
  public void testWordWithOnlyConsonants() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("bcdfg");
    assertEquals("bcdfg", result);
  }
@Test
  public void testWordWithYFirstCharActsLikeConsonant() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("yes");
    assertEquals("ye", result);
  }
@Test
  public void testStemTwiceProducesSameResult() {
    Stemmer stemmer = new Stemmer();
    String once = stemmer.stem("dancing");
    String twice = stemmer.stem(once);
    assertEquals("danc", once);
    assertEquals("danc", twice);
  }
@Test
  public void testAddTriggersBufferResize() {
    Stemmer stemmer = new Stemmer();
    StringBuilder input = new StringBuilder();
    for (int i = 0; i < 55; i++) {
      input.append('a');
    }
    String result = stemmer.stem(input.toString());
    assertEquals(input.toString(), result);
  }
@Test
  public void testEndsFunctionFailsGracefullyOnShortWord() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("ae");
    assertEquals("ae", result);  
  }
@Test
  public void testWordEndingWithIciti() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("simplicity");
    assertEquals("simplic", result);
  }
@Test
  public void testWordWithYAndNoVowelsBefore() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("myth");
    assertEquals("myth", result);  
  }
@Test
  public void testRepeatedYHandling() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("syzygy");
    assertEquals("syzygi", result);  
  }
@Test
  public void testStemEthical() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("ethical");
    assertEquals("ethic", result);  
  }
@Test
  public void testStemCreatingTriggersEEvaluation() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("creating");
    assertEquals("create", result);
  }
@Test
  public void testStemWithInternalDoubleConsonants() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("hopping");
    assertEquals("hop", result);  
  }
@Test
  public void testWordEndingInBli() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("incredibly");
    assertEquals("incred", result);  
  }
@Test
  public void testStemReturnsEmptyForWhitespace() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem(" ");
    assertEquals(" ", result);  
  }
@Test
  public void testStemWordEndingWithEntli() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("evidently");
    assertEquals("evident", result); 
  }
@Test
  public void testWordThatTriggersSettoAfterAtAndM1Cvc() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("hop");  
    assertEquals("hope", result);
  }
@Test
  public void testEndsFunctionWithSuffixNotMatching() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("drawn");
    assertEquals("drawn", result); 
  }
@Test
  public void testStep4RemovesAlize() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("realize");
    assertEquals("real", result);
  }
@Test
  public void testStep4RemovesIcati() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("simplicity");
    assertEquals("simplic", result); 
  }
@Test
  public void testStep3EndsWithEnci() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("independence");
    assertEquals("independ", result);
  }
@Test
  public void testStep3EndsWithAnci() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("compliancy");
    assertEquals("complianc", result);  
  }
@Test
  public void testStep3EndsWithEntli() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("confidently");
    assertEquals("confident", result);
  }
@Test
  public void testStep3EndsWithOusli() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("graciously");
    assertEquals("gracious", result);
  }
@Test
  public void testStep3EndsWithFulness() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("hopefulness");
    assertEquals("hope", result);
  }
@Test
  public void testCvcNotTrueForWXYSuffixes() {
    Stemmer stemmer = new Stemmer();
    String result1 = stemmer.stem("snowing");
    String result2 = stemmer.stem("boxing");
    String result3 = stemmer.stem("traying");
    assertEquals("snow", result1);  
    assertEquals("box", result2);   
    assertEquals("tray", result3);  
  }
@Test
  public void testDoubleConsonantOnlyWhenConsonants() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("falling");
    assertEquals("fall", result);  
  }
@Test
  public void testDoubleConsonantOnlyOneLetter() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("rolling");
    assertEquals("roll", result);
  }
@Test
  public void testSuffixEndsWithEement() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("achievement");
    assertEquals("achiev", result);  
  }
@Test
  public void testSuffixEndsWithOusness() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("seriousness");
    assertEquals("serious", result);
  }
@Test
  public void testSuffixEndsWithAtion() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("information");
    assertEquals("inform", result);
  }
@Test
  public void testconsAndVowels() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("yes");
    assertEquals("ye", result);  
  }
@Test
  public void testShortWordThatShouldBeStemmedWithE() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("hugging"); 
    assertEquals("hug", result);             
  }
@Test
  public void testVowelInstemFalse() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("tss");
    assertEquals("tss", result);  
  }
@Test
  public void testYAtBeginningTreatedConsonant() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("yodel");
    assertEquals("yodel", result); 
  }
@Test
  public void testGetToStringReflectsLastStemmedResult() {
    Stemmer stemmer = new Stemmer();
    stemmer.stem("happiness");
    String result = stemmer.toString();
    assertEquals("happi", result);
  }
@Test
  public void testStemEmptyStringDoesNotThrow() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("");
    assertEquals("", result); 
  }
@Test
  public void testStemStopsEarlyForShortLength() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("an");
    assertEquals("an", result);  
  }
@Test
  public void testStemWithNumbersOnly() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("123");
    assertEquals("123", result);  
  }
@Test
  public void testStemWithMixedAlphaNumeric() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("node123");
    assertEquals("node123", result); 
  }
@Test
  public void testConsonantYTransformsCorrectly() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("apply");
    assertEquals("appli", result); 
  }
@Test
  public void testPluralSReductionSingleSCondition() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("cats");
    assertEquals("cat", result); 
  }
@Test
  public void testPluralSReductionNoDoubleS() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("books");
    assertEquals("book", result); 
  }
@Test
  public void testPluralSWithDoubleSRemovesExtraS() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("chesses");
    assertEquals("chess", result); 
  }
@Test
  public void testEDWithNoVowelIsIgnored() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("brrred");
    assertEquals("brrred", result); 
  }
@Test
  public void testINGWithNoVowelIsIgnored() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("brrrring");
    assertEquals("brrrring", result); 
  }
@Test
  public void testEDAndATBecomesATE() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("mated");
    assertEquals("mate", result); 
  }
@Test
  public void testEDAndIZBecomesIZE() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("finalizing");
    assertEquals("finalize", result); 
  }
@Test
  public void testDoubleConsonantAtEndRemovesOneUnlessLSZ() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("hopping");
    assertEquals("hop", result); 
  }
@Test
  public void testDoubleConsonantKeepForL() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("milling");
    assertEquals("mill", result); 
  }
@Test
  public void testDoubleConsonantKeepForS() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("dressing");
    assertEquals("dress", result); 
  }
@Test
  public void testDoubleConsonantKeepForZ() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("buzzing");
    assertEquals("buzz", result); 
  }
@Test
  public void testCvcTriggersEWhenMEquals1() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("hoping");
    assertEquals("hope", result); 
  }
@Test
  public void testStep6RemovesFinalENotCVC() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("rate");
    assertEquals("rate", result); 
  }
@Test
  public void testStep6RemovesFinalEWithMGreaterThan1() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("rateable");
    assertEquals("rateabl", result); 
  }
@Test
  public void testStep6RemovesFinalLAfterDoubleLAndMGreaterThan1() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("compelled");
    assertEquals("compel", result); 
  }
@Test
  public void testEndsFailsQuicklyWithShortWord() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("it");
    assertEquals("it", result); 
  }
@Test
  public void testEndsWithIncorrectSuffixReturnsFalsePath() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("hopelessness"); 
    assertEquals("hopeless", result); 
  }
@Test
  public void testNoChangeWhenSuffixMatchesButMEqualsZero() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("bled"); 
    assertEquals("bled", result); 
  }
@Test
  public void testYBecomesIIfVowelExistsBefore() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("enemy");
    assertEquals("enemi", result); 
  }
@Test
  public void testYRemainsIfNoVowelExistsBefore() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("cry");
    assertEquals("cry", result); 
  }
@Test
  public void testConsonantYFirstLetterHandledCorrectly() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("yell");
    assertEquals("yell", result); 
  }
@Test
  public void testEndsWithBliConvertsToBle() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("incredibly");
    assertEquals("incred", result); 
  }
@Test
  public void testFinalZDoubleConsonantIsKept() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("fizzing");
    assertEquals("fizz", result);
  }
@Test
  public void testSuffixLogiChangesToLog() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("analogical");
    assertEquals("analog", result);
  }
@Test
  public void testSuffixAlizeChangesToAl() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("formalize");
    assertEquals("formal", result);
  }
@Test
  public void testSuffixIcitiChangesToIc() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("authenticity");
    assertEquals("authentic", result);
  }
@Test
  public void testSuffixnessRemoval() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("darkness");
    assertEquals("dark", result); 
  }
@Test
  public void testMultipleSuffixTransformationsCascade() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("rationalization");
    assertEquals("rational", result); 
  }
@Test
  public void testShortWordSkipsAllSteps() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("go");
    assertEquals("go", result); 
  }
@Test
  public void testEndsWithAlismBecomesAl() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("formalism");
    assertEquals("formal", result);
  }
@Test
  public void testEndsWithOusliBecomesOus() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("deliciously");
    assertEquals("delicious", result);
  }
@Test
  public void testEndsWithTionalBecomesTion() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("rational");
    assertEquals("ration", result); 
  }
@Test
  public void testEndsWithAtFinalStep1AppliesATE() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("conjugated");
    assertEquals("conjugate", result); 
  }
@Test
  public void testEndsWithIvitiBecomesIve() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("positivity");
    assertEquals("positiv", result);
  }
@Test
  public void testEndsWithBilitiBecomesBle() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("flexibility");
    assertEquals("flexibl", result);
  }
@Test
  public void testMEquals1AndFinalEConditionMet() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("hug");
    assertEquals("hug", result); 
  }
@Test
  public void testMEquals1FinalERemovedBecauseCvcFalse() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("rate");
    assertEquals("rate", result); 
  }
@Test
  public void testUpperCaseWordPreservedIfNotLowercased() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("RUNNING");
    assertEquals("RUNNING", result); 
  }
@Test
  public void testEmbeddedUpperCaseDoesNotAffectMatching() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("InFORMATion");
    assertEquals("InFORMAT", result); 
  }
@Test
  public void testWhitespacePaddingPreserved() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem(" jumping ");
    assertEquals(" jump", result); 
  }
@Test
  public void testSpecialCharactersIgnoredInSuffixCheck() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("walk-ing");
    assertEquals("walk-ing", result); 
  }
@Test
  public void testWordOnlyConsonantsNoChangeVowelinstemFalse() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("trrrng");
    assertEquals("trrrng", result); 
  }
@Test
  public void testApplyMethodMultipleTimesWithSameWord() {
    Stemmer stemmer = new Stemmer();
    String result1 = stemmer.stem("nodding");
    assertEquals("nod", result1);
    String result2 = stemmer.stem("nodding");
    assertEquals("nod", result2);
  }
@Test
  public void testPartialSuffixMatchFails() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("confident");
    assertEquals("confident", result); 
  }
@Test
  public void testSuffixOverlappingButLongestMatchApplies() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("operational");
    assertEquals("operate", result); 
  }
@Test
  public void testEndsWithAliBecomesAl() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("optionally");
    assertEquals("option", result); 
  }
@Test
  public void testEndsWithEliBecomesE() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("nicely");
    assertEquals("nice", result); 
  }
@Test
  public void testNestedSuffixEndsWithICATE() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("communicate");
    assertEquals("communic", result); 
  }
@Test
  public void testEndsWithATIVE() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("demonstrative");
    assertEquals("demonstr", result); 
  }
@Test
  public void testStep6FinalEWithMEquals1AndCvcTrueKeepsE() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("cave");
    assertEquals("cave", result); 
  }
@Test
  public void testSuffixMENTStripsCorrectly() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("movement");
    assertEquals("move", result); 
  }
@Test
  public void testEndsWithIzerBecomesIze() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("computerizer");
    assertEquals("computerize", result);
  }
@Test
  public void testEndsWithAtorBecomesAte() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("generator");
    assertEquals("generate", result);
  }
@Test
  public void testEndsWithEntliBecomesEnt() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("recently");
    assertEquals("recent", result);
  }
@Test
  public void testEndsWithAtionBecomesAte() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("exaggeration");
    assertEquals("exaggerate", result);
  }
@Test
  public void testEndsWithOUSNESSDropsSuffix() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("anxiousness");
    assertEquals("anxious", result);
  }
@Test
  public void testEndsWithEMENTDropsSuffix() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("entrapment");
    assertEquals("entrap", result);
  }
@Test
  public void testEndsWithEMENTNotDroppedDueToLowM() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("cement");
    assertEquals("cement", result); 
  }
@Test
  public void testVCVCVCWordHighMValue() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("abandonment");
    assertEquals("abandon", result); 
  }
@Test
  public void testShortWordVowelYNoStem() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("by");
    assertEquals("by", result);
  }
@Test
  public void testWordWithFinalYConvertsToIWithVowelInStem() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("silly");
    assertEquals("silli", result); 
  }
@Test
  public void testWordEndsInCVCWithWBlocksFinalE() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("snowing");
    assertEquals("snow", result); 
  }
@Test
  public void testWordEndsInCVCWithXBlocksFinalE() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("boxing");
    assertEquals("box", result);
  }
@Test
  public void testWordEndsInCVCWithYBlocksFinalE() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("playing");
    assertEquals("play", result); 
  }
@Test
  public void testEarlyReturnOnShortInputLength() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("ox");
    assertEquals("ox", result); 
  }
@Test
  public void testNullOnlyNumbersStayUntouched() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("12345");
    assertEquals("12345", result);
  }
@Test
  public void testAlphanumericStaysUntouched() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("run123");
    assertEquals("run123", result);
  }
@Test
  public void testDotsAndPunctuationStayUntouched() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("run.");
    assertEquals("run.", result);
  }
@Test
  public void testDoubleInvocationResetBufferProperly() {
    Stemmer stemmer = new Stemmer();
    String first = stemmer.stem("running");
    assertEquals("run", first);
    String second = stemmer.stem("agreement");
    assertEquals("agree", second); 
  }
@Test
  public void testSuffixLogiBecomesLog() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("badlylogical");
    assertEquals("badlylogic", result); 
  }
@Test
  public void testFinalLStrippedWhenDoubleAndMGreaterThanOne() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("controlled");
    assertEquals("control", result); 
  }
@Test
  public void testMEquals1CvcConditionFalseFinalERetained() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("code");
    assertEquals("code", result); 
  }
@Test
  public void testEndsWithIcRemainsValidInStep5() {
    Stemmer stemmer = new Stemmer();
    String result = stemmer.stem("specific");
    assertEquals("specif", result); 
  }
@Test
  public void testSameWordStemmedTwiceGivesSameResult() {
    Stemmer stemmer = new Stemmer();
    String a = stemmer.stem("meeting");
    assertEquals("meet", a);
    String b = stemmer.stem("meeting");
    assertEquals("meet", b);
  }
@Test
  public void testToStringReturnsStemmedForm() {
    Stemmer stemmer = new Stemmer();
    stemmer.stem("training");
    String result = stemmer.toString();
    assertEquals("train", result); 
  } 
}