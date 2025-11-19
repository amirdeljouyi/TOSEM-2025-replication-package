package opennlp.tools.stemmer;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PorterStemmer_3_GPTLLMTest {

@Test
public void testPluralFormCaresses() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("caresses");
assertEquals("caress", result);
}

@Test
public void testPluralFormPonies() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("ponies");
assertEquals("poni", result);
}

@Test
public void testPluralFormCats() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("cats");
assertEquals("cat", result);
}

@Test
public void testIngSuffixMating() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("mating");
assertEquals("mate", result);
}

@Test
public void testEdSuffixDisabled() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("disabled");
assertEquals("disable", result);
}

@Test
public void testYToITransformationHappy() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("happy");
assertEquals("happi", result);
}

@Test
public void testYToITransformationSky() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("sky");
assertEquals("sky", result);
}

@Test
public void testDoubleSuffixRelational() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("relational");
assertEquals("relate", result);
}

@Test
public void testSuffixCritical() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("critical");
assertEquals("critic", result);
}

@Test
public void testSuffixUsefulness() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("usefulness");
assertEquals("use", result);
}

@Test
public void testMentSuffixDevelopment() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("development");
assertEquals("develop", result);
}

@Test
public void testFinalEProbable() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("probable");
assertEquals("probabl", result);
}

@Test
public void testToStringAfterAddAndStem() {
PorterStemmer stemmer = new PorterStemmer();
stemmer.add('r');
stemmer.add('u');
stemmer.add('n');
stemmer.add('n');
stemmer.add('i');
stemmer.add('n');
stemmer.add('g');
boolean changed = stemmer.stem();
String result = stemmer.toString();
assertTrue(changed);
assertEquals("run", result);
}

@Test
public void testStemCharArrayCorrectness() {
PorterStemmer stemmer = new PorterStemmer();
char[] input = new char[] { 'c', 'r', 'i', 'e', 's' };
boolean changed = stemmer.stem(input);
String result = stemmer.toString();
assertTrue(changed);
assertEquals("cri", result);
}

@Test
public void testStemCharArrayWithOffset() {
PorterStemmer stemmer = new PorterStemmer();
char[] buffer = new char[] { 'X', 'X', 'X', 'c', 'r', 'i', 'e', 's', 'X' };
boolean changed = stemmer.stem(buffer, 3, 5);
String result = stemmer.toString();
assertTrue(changed);
assertEquals("cri", result);
}

@Test
public void testStemCharSequence() {
PorterStemmer stemmer = new PorterStemmer();
CharSequence result = stemmer.stem("relational");
assertEquals("relate", result.toString());
}

@Test
public void testResetAndReseed() {
PorterStemmer stemmer = new PorterStemmer();
stemmer.add('r');
stemmer.add('u');
stemmer.add('n');
stemmer.stem();
String first = stemmer.toString();
assertEquals("run", first);
stemmer.reset();
stemmer.add('r');
stemmer.add('u');
stemmer.add('n');
stemmer.add('s');
stemmer.stem();
String second = stemmer.toString();
assertEquals("run", second);
}

@Test
public void testNoStemChange() {
PorterStemmer stemmer = new PorterStemmer();
char[] input = new char[] { 's', 'k', 'y' };
boolean changed = stemmer.stem(input);
assertFalse(changed);
assertEquals("sky", stemmer.toString());
}

@Test
public void testGetResultBufferAfterStem() {
PorterStemmer stemmer = new PorterStemmer();
String input = "happiness";
String result = stemmer.stem(input);
char[] buffer = stemmer.getResultBuffer();
int len = stemmer.getResultLength();
String extracted = new String(buffer, 0, len);
assertEquals("happi", extracted);
assertEquals(result, extracted);
}

@Test
public void testShortWordsAreNotStemmed() {
PorterStemmer stemmer1 = new PorterStemmer();
assertEquals("is", stemmer1.stem("is"));
PorterStemmer stemmer2 = new PorterStemmer();
assertEquals("at", stemmer2.stem("at"));
PorterStemmer stemmer3 = new PorterStemmer();
assertEquals("be", stemmer3.stem("be"));
}

@Test
public void testBug1_AedHandledGracefully() {
PorterStemmer stemmer = new PorterStemmer();
boolean changed = stemmer.stem("aed".toCharArray());
String result = stemmer.toString();
assertTrue(changed);
assertNotNull(result);
}

@Test
public void testBug2_IonHandledGracefully() {
PorterStemmer stemmer = new PorterStemmer();
boolean changed = stemmer.stem("ion".toCharArray());
assertTrue(changed);
assertEquals("ion", stemmer.toString());
}

@Test
public void testDoubleCharacterHandling() {
PorterStemmer stemmer1 = new PorterStemmer();
assertEquals("control", stemmer1.stem("controlled"));
PorterStemmer stemmer2 = new PorterStemmer();
assertEquals("refer", stemmer2.stem("referring"));
PorterStemmer stemmer3 = new PorterStemmer();
assertEquals("prefer", stemmer3.stem("preferred"));
}

@Test
public void testSuffixAliAndIzer() {
PorterStemmer stemmer1 = new PorterStemmer();
assertEquals("music", stemmer1.stem("musical"));
PorterStemmer stemmer2 = new PorterStemmer();
assertEquals("equal", stemmer2.stem("equalizer"));
}

@Test
public void testStep4VariousCases() {
PorterStemmer stemmer1 = new PorterStemmer();
assertEquals("critic", stemmer1.stem("criticism"));
PorterStemmer stemmer2 = new PorterStemmer();
assertEquals("elect", stemmer2.stem("election"));
PorterStemmer stemmer3 = new PorterStemmer();
assertEquals("real", stemmer3.stem("realize"));
}

@Test
public void testEmptyInputReturnsEmpty() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("");
assertEquals("", result);
}

@Test
public void testWhitespaceOnlyString() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem(" ");
assertEquals(" ", result);
}

@Test
public void testUpperCaseInput() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("Running".toLowerCase());
assertEquals("run", result);
}

@Test
public void testStemWithNewInstance() {
PorterStemmer stemmer = new PorterStemmer();
boolean changed = stemmer.stem("running".toCharArray());
String result = stemmer.toString();
assertTrue(changed);
assertEquals("run", result);
}

@Test
public void testMinimumValidWordLength() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("aa");
assertEquals("aa", result);
}

@Test
public void testSingleCharacterInput() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("a");
assertEquals("a", result);
}

@Test
public void testWordEndingInSButNotPlural() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("gas");
assertEquals("gas", result);
}

@Test
public void testStemWordWithIonNotAfterSOrT() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("scion");
assertEquals("scion", result);
}

@Test
public void testStemWordWithIonAfterSOrT() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("transition");
assertEquals("transit", result);
}

@Test
public void testStemWordEndingWithEButNotEnoughMeasure() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("race");
assertEquals("race", result);
}

@Test
public void testStemWordEndingInLeLikeAble() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("capable");
assertEquals("capabl", result);
}

@Test
public void testStemConsonantVowelConsonantPatternEndingInE() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("hoping");
assertEquals("hope", result);
}

@Test
public void testDoubleConsonantEdgeZ() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("fizzing");
assertEquals("fizz", result);
}

@Test
public void testCvcEndingWithW() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("snow");
assertEquals("snow", result);
}

@Test
public void testCvcEndingWithX() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("box");
assertEquals("box", result);
}

@Test
public void testCvcEndingWithY() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("tray");
assertEquals("tray", result);
}

@Test
public void testNoChangesOnValidBaseWord() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("computer");
assertEquals("computer", result);
}

@Test
public void testOnlyConsonants() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("rhythms");
assertEquals("rhythm", result);
}

@Test
public void testStemWordEndsWithEedButNoMeasure() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("feed");
assertEquals("feed", result);
}

@Test
public void testStemWordEndsWithEedWithMeasure() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("agreed");
assertEquals("agre", result);
}

@Test
public void testStemWithPrefixOnly() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("under");
assertEquals("under", result);
}

@Test
public void testAggressivelySuffixedWord() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("institutionalization");
assertEquals("institut", result);
}

@Test
public void testStemWithStep6LRemovalOnLogical() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("logical");
assertEquals("logic", result);
}

@Test
public void testOnlyVowels() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("aeiou");
assertEquals("aeiou", result);
}

@Test
public void testUnusualCharactersInWord() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("co-op");
assertEquals("co-op", result);
}

@Test
public void testHyphenatedWordPart1() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("high-speed");
assertEquals("high-speed", result);
}

@Test
public void testUnicodeCharactersIgnored() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("naïve");
assertEquals("naïv", result);
}

@Test
public void testDigitInWord() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("item123");
assertEquals("item123", result);
}

@Test
public void testStemWordWithStep4EntSuffix() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("entitlement");
assertEquals("entitl", result);
}

@Test
public void testStatementStep3BliCase() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("nobli");
assertEquals("noble", result);
}

@Test
public void testAliSuffixStep3() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("finally");
assertEquals("final", result);
}

@Test
public void testIvitiSuffixStep3() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("creativity");
assertEquals("creativ", result);
}

@Test
public void testOUSnessSuffixStep3() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("graciousness");
assertEquals("gracious", result);
}

@Test
public void testLongChainTransformation() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("transmissionally");
assertEquals("transmiss", result);
}

@Test
public void testEndsWithSsButNotPlural() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("boss");
assertEquals("boss", result);
}

@Test
public void testEndsInEdButNoVowelBefore() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("bzzed");
assertEquals("bzzed", result);
}

@Test
public void testEndsInIngButNoVowelBefore() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("bzzing");
assertEquals("bzzing", result);
}

@Test
public void testConsonantDoubling_RemoveLNotSOrZ() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("hopped");
assertEquals("hop", result);
}

@Test
public void testConsonantDoubling_KeepsZ() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("buzzed");
assertEquals("buzz", result);
}

@Test
public void testSetToSkipsIfMIsZero() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("izer");
assertEquals("izer", result);
}

@Test
public void testStep3_LogiSuffixHandled() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("analogical");
assertEquals("analog", result);
}

@Test
public void testStep3_BilitiSuffixHandled() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("flexibility");
assertEquals("flexibl", result);
}

@Test
public void testStep3_IvitiSuffixHandled() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("sensitivity");
assertEquals("sensitiv", result);
}

@Test
public void testStep3_AlliSuffixHandled() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("logically");
assertEquals("logic", result);
}

@Test
public void testStep4_NessSuffixHandled() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("kindness");
assertEquals("kind", result);
}

@Test
public void testCvcBlockingWithXEnding() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("mixing");
assertEquals("mix", result);
}

@Test
public void testCvcBlockingWithYEnding() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("traying");
assertEquals("tray", result);
}

@Test
public void testCvcBlockingWithWEnding() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("gnawing");
assertEquals("gnaw", result);
}

@Test
public void testStep4_RemoveFullSuffix() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("colorful");
assertEquals("color", result);
}

@Test
public void testStep4_RemoveNessSuffix() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("brightness");
assertEquals("bright", result);
}

@Test
public void testStep5_WordWithEntEndingButLowM() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("bent");
assertEquals("bent", result);
}

@Test
public void testStep5_RemoveIzeSuffix() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("finalize");
assertEquals("final", result);
}

@Test
public void testStep6_RemoveFinalE_MIsOne() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("rate");
assertEquals("rate", result);
}

@Test
public void testStep6_RemoveFinalE_MIsGreaterThanOne() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("defensible");
assertEquals("defens", result);
}

@Test
public void testStep6_DoubleConsonantLLRemoved() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("skilled");
assertEquals("skill", result);
}

@Test
public void testWhitespaceWordHandledWithoutException() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("   ");
assertEquals("   ", result);
}

@Test
public void testNullSafeToStringAfterStem() {
PorterStemmer stemmer = new PorterStemmer();
stemmer.add('f');
stemmer.add('o');
stemmer.add('o');
stemmer.stem();
String result = stemmer.toString();
assertNotNull(result);
assertEquals("foo", result);
}

@Test
public void testCaseSensitivityLowerCaseRequired() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("HOPPING");
assertEquals("HOPPING", result);
}

@Test
public void testStemUnchangedIfShortLength() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("go");
assertEquals("go", result);
}

@Test
public void testStemOnlyVowelCluster() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("aeiouaeiou");
assertEquals("aeiouaeiou", result);
}

@Test
public void testEndsInEdButZeroMeasure() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("bled");
assertEquals("bled", result);
}

@Test
public void testEndsInIngButZeroMeasure() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("sling");
assertEquals("sling", result);
}

@Test
public void testDoubleConsonantFinalSSPreserved() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("miss");
assertEquals("miss", result);
}

@Test
public void testDoubleSufficesStep3_Izer() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("finalizer");
assertEquals("final", result);
}

@Test
public void testSuffixStep3_WithMinimalMeasure() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("izer");
assertEquals("izer", result);
}

@Test
public void testSuffixStep3_WithSufficientMeasure() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("organizer");
assertEquals("organ", result);
}

@Test
public void testSuffixStep4_Alize() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("normalize");
assertEquals("normal", result);
}

@Test
public void testSuffixStep4_AteRemovesWithSufficientMeasure() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("communicate");
assertEquals("communic", result);
}

@Test
public void testSuffixStep5_IcRemovedWithSufficientMeasure() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("music");
assertEquals("music", result);
}

@Test
public void testEndsWithABLEAndRemovesIfMeasureMatches() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("capable");
assertEquals("capabl", result);
}

@Test
public void testEndsWithIBLEAndRemovesIfMeasureMatches() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("sensible");
assertEquals("sensibl", result);
}

@Test
public void testEndsWithOUSAndRemovesIfMeasureMatches() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("previous");
assertEquals("previou", result);
}

@Test
public void testConsonantVowelConsonantPattern_AddsE() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("hop");
assertEquals("hope", result);
}

@Test
public void testNonTargetSuffixShouldRemainIntact() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("paradox");
assertEquals("paradox", result);
}

@Test
public void testBufferExpansionAddBeyondInitialLength() {
PorterStemmer stemmer = new PorterStemmer();
stemmer.add('p');
stemmer.add('n');
stemmer.add('e');
stemmer.add('u');
stemmer.add('m');
stemmer.add('o');
stemmer.add('n');
stemmer.add('o');
stemmer.add('u');
stemmer.add('l');
stemmer.add('t');
stemmer.add('r');
stemmer.add('a');
stemmer.add('m');
stemmer.add('i');
stemmer.add('c');
stemmer.add('r');
stemmer.add('o');
stemmer.add('s');
stemmer.add('c');
stemmer.add('o');
stemmer.add('p');
stemmer.add('i');
stemmer.add('c');
stemmer.add('s');
stemmer.stem();
String result = stemmer.toString();
assertEquals("pneumonoultramicroscopics", result);
}

@Test
public void testStemAgainWithoutReset_AffectsSameInstance() {
PorterStemmer stemmer = new PorterStemmer();
stemmer.add('r');
stemmer.add('u');
stemmer.add('n');
stemmer.add('n');
stemmer.add('i');
stemmer.add('n');
stemmer.add('g');
stemmer.stem();
String first = stemmer.toString();
stemmer.add('s');
stemmer.stem();
String second = stemmer.toString();
assertNotEquals(first, second);
assertEquals("runs", second);
}

@Test
public void testStemReturnsFalseForUnchangedWord() {
PorterStemmer stemmer = new PorterStemmer();
char[] input = new char[] { 's', 'k', 'y' };
boolean changed = stemmer.stem(input);
assertFalse(changed);
}

@Test
public void testStemReturnsTrueIfChangedViaAdd() {
PorterStemmer stemmer = new PorterStemmer();
stemmer.add('w');
stemmer.add('a');
stemmer.add('t');
stemmer.add('c');
stemmer.add('h');
stemmer.add('e');
stemmer.add('s');
boolean changed = stemmer.stem();
assertTrue(changed);
assertEquals("watch", stemmer.toString());
}

@Test
public void testStemHandlesEmptyCharArray() {
PorterStemmer stemmer = new PorterStemmer();
char[] empty = new char[0];
boolean changed = stemmer.stem(empty);
assertFalse(changed);
String result = stemmer.toString();
assertEquals("", result);
}

@Test
public void testStemWithLengthLargerThanInput() {
PorterStemmer stemmer = new PorterStemmer();
char[] input = new char[] { 's', 't', 'e', 'm', 'm', 'i', 'n', 'g' };
boolean changed = stemmer.stem(input, 5);
assertTrue(changed);
assertEquals("stem", stemmer.toString());
}

@Test
public void testEndsWithEntliStep3() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("differently");
assertEquals("differ", result);
}

@Test
public void testEndsWithOusliStep3() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("graciously");
assertEquals("gracious", result);
}

@Test
public void testEndsWithLogiStep3() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("analogical");
assertEquals("analog", result);
}

@Test
public void testEndsWithBliStep3() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("nobly");
assertEquals("noble", result);
}

@Test
public void testEndsWithEmentStep5() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("agreement");
assertEquals("agre", result);
}

@Test
public void testEndsWithAtionStep3() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("automation");
assertEquals("automat", result);
}

@Test
public void testEndsWithTionalStep3() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("constitutional");
assertEquals("constitut", result);
}

@Test
public void testInputAllSameCharacter() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("zzzzz");
assertEquals("zzzzz", result);
}

@Test
public void testStemInputLengthOneChar() {
PorterStemmer stemmer = new PorterStemmer();
stemmer.add('r');
boolean changed = stemmer.stem();
String result = stemmer.toString();
assertFalse(changed);
assertEquals("r", result);
}

@Test
public void testStemShortWordTwoChars() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("og");
assertEquals("og", result);
}

@Test
public void testStemNonAlphaCharacters() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("1234!");
assertEquals("1234!", result);
}

@Test
public void testStemWordWithPrefixThatLooksLikeSuffix() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("izationism");
assertEquals("ization", result);
}

@Test
public void testStemSuffixIVITIWithoutMeasure() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("liviti");
assertEquals("liviti", result);
}

@Test
public void testStemSuffixOUSNESS() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("righteousness");
assertEquals("righteous", result);
}

@Test
public void testStemSuffixFULNESS() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("thankfulness");
assertEquals("thank", result);
}

@Test
public void testStemSuffixALISM() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("nationalism");
assertEquals("nation", result);
}

@Test
public void testStemSuffixENTWithoutMeasure() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("bent");
assertEquals("bent", result);
}

@Test
public void testStemSuffixENCEWithMeasure() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("presence");
assertEquals("presenc", result);
}

@Test
public void testStemSuffixIONNoMatchBecauseJNegative() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("ion");
assertEquals("ion", result);
}

@Test
public void testStemSuffixIONWithSBefore() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("discussion");
assertEquals("discuss", result);
}

@Test
public void testStemRemoveFinalELWhenMGreaterThanOne() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("meddle");
assertEquals("meddl", result);
}

@Test
public void testStemVowelInYHandledCorrectly() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("cry");
assertEquals("cri", result);
}

@Test
public void testStemSameWordTwiceResetsState() {
PorterStemmer stemmer = new PorterStemmer();
String result1 = stemmer.stem("meeting");
String result2 = stemmer.stem("meeting");
assertEquals("meet", result1);
assertEquals("meet", result2);
}

@Test
public void testStemBufferOverResizeWithLongInput() {
PorterStemmer stemmer = new PorterStemmer();
stemmer.add('m');
stemmer.add('i');
stemmer.add('s');
stemmer.add('u');
stemmer.add('n');
stemmer.add('d');
stemmer.add('e');
stemmer.add('r');
stemmer.add('s');
stemmer.add('t');
stemmer.add('a');
stemmer.add('n');
stemmer.add('d');
stemmer.add('i');
stemmer.add('n');
stemmer.add('g');
stemmer.add('l');
stemmer.add('y');
boolean changed = stemmer.stem();
assertTrue(changed);
String result = stemmer.toString();
assertEquals("misunderstandingli", result);
}

@Test
public void testStemHandlesEmptyCharSequenceGracefully() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("");
assertEquals("", result);
}

@Test
public void testEndsWithEntliHandledByStep3() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("differently");
assertEquals("differ", result);
}

@Test
public void testEndsWithOusliHandledByStep3() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("graciously");
assertEquals("gracious", result);
}

@Test
public void testEndsWithEliHandledInStep3() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("gentle");
assertEquals("gentl", result);
}

@Test
public void testEndsWithAlitiHandledInStep3() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("formality");
assertEquals("formal", result);
}

@Test
public void testEndsWithIvitiHandledInStep3() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("sensitivity");
assertEquals("sensitiv", result);
}

@Test
public void testEndsWithBilitiHandledInStep3() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("flexibility");
assertEquals("flexibl", result);
}

@Test
public void testEndsWithLogiHandledInStep3() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("analogical");
assertEquals("analog", result);
}

@Test
public void testEndsWithFulHandledInStep4() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("hopeful");
assertEquals("hope", result);
}

@Test
public void testEndsWithNessHandledInStep4() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("kindness");
assertEquals("kind", result);
}

@Test
public void testEndsWithIcitiHandledInStep4() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("simplicity");
assertEquals("simplic", result);
}

@Test
public void testEndsWithAteHandledInStep5() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("consolidate");
assertEquals("consolid", result);
}

@Test
public void testRemovesFinalEIfMeasureIsGreaterThanOne() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("validate");
assertEquals("validat", result);
}

@Test
public void testDoesNotRemoveFinalEIfMeasureIsZero() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("e");
assertEquals("e", result);
}

@Test
public void testLeavesWordEndingInCvcWithWUnchanged() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("draw");
assertEquals("draw", result);
}

@Test
public void testLeavesWordEndingInCvcWithYUnchanged() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("tray");
assertEquals("tray", result);
}

@Test
public void testLeavesWordEndingInCvcWithXUnchanged() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("box");
assertEquals("box", result);
}

@Test
public void testStep1EedBranchWithZeroMeasure() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("feed");
assertEquals("feed", result);
}

@Test
public void testStep1EedBranchWithPositiveMeasure() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("agreed");
assertEquals("agre", result);
}

@Test
public void testStep4HandlesAlize() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("finalize");
assertEquals("final", result);
}

@Test
public void testStep4HandlesIcAndDoesNotReplaceIfMZero() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("pic");
assertEquals("pic", result);
}

@Test
public void testEmptyInputStringReturnsEmpty() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("");
assertEquals("", result);
}

@Test
public void testWhitespaceStringReturnsUnchanged() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem(" ");
assertEquals(" ", result);
}

@Test
public void testInputAllConsonantsUnmodified() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("rhythm");
assertEquals("rhythm", result);
}

@Test
public void testAddExpandsBufferCorrectlyAndStems() {
PorterStemmer stemmer = new PorterStemmer();
stemmer.add('u');
stemmer.add('n');
stemmer.add('r');
stemmer.add('e');
stemmer.add('l');
stemmer.add('a');
stemmer.add('t');
stemmer.add('e');
stemmer.add('d');
boolean changed = stemmer.stem();
String result = stemmer.toString();
assertTrue(changed);
assertEquals("unrelat", result);
}

@Test
public void testStemMethodReturnsTrueForChangedInput() {
PorterStemmer stemmer = new PorterStemmer();
boolean changed = stemmer.stem("playing".toCharArray());
String result = stemmer.toString();
assertTrue(changed);
assertEquals("play", result);
}

@Test
public void testStemMethodReturnsFalseIfUnchanged() {
PorterStemmer stemmer = new PorterStemmer();
boolean changed = stemmer.stem("the".toCharArray());
assertFalse(changed);
assertEquals("the", stemmer.toString());
}

@Test
public void testStep3AlliSuffixWithZeroMeasure() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("ralli");
assertEquals("ralli", result);
}

@Test
public void testEdgeCaseSuffixOverlap() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("activation");
assertEquals("activ", result);
}

@Test
public void testStemStep3EndsWithEnci() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("emergency");
assertEquals("emergency", result);
}

@Test
public void testStemStep3EndsWithAnci() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("occupancy");
assertEquals("occupancy", result);
}

@Test
public void testStem_Step5EndsWithMent() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("enforcement");
assertEquals("enforc", result);
}

@Test
public void testStem_Step5EndsWithAnt() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("pleasant");
assertEquals("pleasant", result);
}

@Test
public void testStem_Step5EndsWithATEButLowMeasure() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("elate");
assertEquals("elate", result);
}

@Test
public void testStem_Step5EndsWithITI() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("sensitivity");
assertEquals("sensitiv", result);
}

@Test
public void testStem_Step5EndsWithIVE() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("assertive");
assertEquals("assert", result);
}

@Test
public void testStem_Step5EndsWithOUS() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("enormous");
assertEquals("enorm", result);
}

@Test
public void testStemWithSingleCVC_EndsInE() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("hop");
assertEquals("hope", result);
}

@Test
public void testStemWithStep1_DoubleConsonant_L() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("rolling");
assertEquals("roll", result);
}

@Test
public void testStemStep1_DoubleConsonant_ZPreserved() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("buzzing");
assertEquals("buzz", result);
}

@Test
public void testStemStep2_YToI() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("fuzzy");
assertEquals("fuzzi", result);
}

@Test
public void testStemStep2_YNotReplacedDueToNoVowel() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("by");
assertEquals("by", result);
}

@Test
public void testStemBug1ReportedAED() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("aed");
assertEquals("aed", result);
}

@Test
public void testStemBug2EdgeCaseION() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("ion");
assertEquals("ion", result);
}

@Test
public void testStemStep6FinalERemovedWithHighMeasure() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("removable");
assertEquals("remov", result);
}

@Test
public void testStemStep6FinalE_NotRemovedDueToCVC() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("stone");
assertEquals("stone", result);
}

@Test
public void testStemWithCharArrayWithOffsetGreaterThanZero() {
PorterStemmer stemmer = new PorterStemmer();
char[] chars = new char[] { '.', '.', 'f', 'l', 'o', 'w', 'e', 'r', '.', '.' };
boolean changed = stemmer.stem(chars, 2, 6);
assertTrue(changed);
assertEquals("flower", stemmer.toString());
}

@Test
public void testStemWithCharArrayWithSuffixEndingEED_EnoughMeasureToReduce() {
PorterStemmer stemmer = new PorterStemmer();
char[] chars = new char[] { 'a', 'g', 'r', 'e', 'e', 'd' };
boolean changed = stemmer.stem(chars);
assertTrue(changed);
assertEquals("agre", stemmer.toString());
}

@Test
public void testToStringAfterEmptyAddStem() {
PorterStemmer stemmer = new PorterStemmer();
boolean changed = stemmer.stem();
assertFalse(changed);
assertEquals("", stemmer.toString());
}

@Test
public void testStemCharSequenceInput() {
PorterStemmer stemmer = new PorterStemmer();
CharSequence result = stemmer.stem("operational");
assertEquals("oper", result.toString());
}

@Test
public void testStemShortWordInputSingleLetter() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("g");
assertEquals("g", result);
}

@Test
public void testStemMinimumTransformableLength() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("bed");
assertEquals("bed", result);
}

@Test
public void testStemWithWhitespaceOnly() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("   ");
assertEquals("   ", result);
}

@Test
public void testStemUppercaseLettersInputPreservedAsIs() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("LOOKING");
assertEquals("LOOKING", result);
}

@Test
public void testStemFinalLRemovalInStep6() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("drill");
assertEquals("dril", result);
}

@Test
public void testStemInputThatTriggersStep4AndStep5() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("communicativeness");
assertEquals("communic", result);
}

@Test
public void testStemInputThatShouldReturnUnchangedDueToLowMeasure() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("abled");
assertEquals("abled", result);
}

@Test
public void testStemMultipleCallsReuseInstance() {
PorterStemmer stemmer = new PorterStemmer();
String first = stemmer.stem("approving");
String second = stemmer.stem("disapproval");
assertEquals("approv", first);
assertEquals("disapprov", second);
}

@Test
public void testStemAddExceedsInitialBufferCapacity() {
PorterStemmer stemmer = new PorterStemmer();
stemmer.add('s');
stemmer.add('u');
stemmer.add('p');
stemmer.add('e');
stemmer.add('r');
stemmer.add('c');
stemmer.add('a');
stemmer.add('l');
stemmer.add('i');
stemmer.add('f');
stemmer.add('r');
stemmer.add('a');
stemmer.add('g');
stemmer.add('i');
stemmer.add('l');
stemmer.add('i');
stemmer.add('s');
stemmer.add('t');
stemmer.add('i');
stemmer.add('c');
boolean changed = stemmer.stem();
assertTrue(changed);
assertTrue(stemmer.toString().length() > 10);
}
}
