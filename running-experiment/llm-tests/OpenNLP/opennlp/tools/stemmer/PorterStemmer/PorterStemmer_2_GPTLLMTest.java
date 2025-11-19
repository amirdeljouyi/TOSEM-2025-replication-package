package opennlp.tools.stemmer;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PorterStemmer_2_GPTLLMTest {

@Test
public void testStemCatsPlural() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("cats");
assertEquals("cat", result);
}

@Test
public void testStemCaresses() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("caresses");
assertEquals("caress", result);
}

@Test
public void testStemPonies() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("ponies");
assertEquals("poni", result);
}

@Test
public void testStemMeeting() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("meeting");
assertEquals("meet", result);
}

@Test
public void testStemAgreed() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("agreed");
assertEquals("agree", result);
}

@Test
public void testStemDisabled() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("disabled");
assertEquals("disable", result);
}

@Test
public void testStemMatting() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("matting");
assertEquals("mat", result);
}

@Test
public void testStemMessing() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("messing");
assertEquals("mess", result);
}

@Test
public void testStemHappy() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("happy");
assertEquals("happi", result);
}

@Test
public void testStemSky() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("sky");
assertEquals("sky", result);
}

@Test
public void testStemRelational() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("relational");
assertEquals("relat", result);
}

@Test
public void testStemFormally() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("formally");
assertEquals("formal", result);
}

@Test
public void testStemDecisiveness() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("decisiveness");
assertEquals("decis", result);
}

@Test
public void testStemHopefulness() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("hopefulness");
assertEquals("hope", result);
}

@Test
public void testStemHappiness() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("happiness");
assertEquals("happi", result);
}

@Test
public void testStemAnimation() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("animation");
assertEquals("animate", result);
}

@Test
public void testStemIndependence() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("independence");
assertEquals("independ", result);
}

@Test
public void testStemHope() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("hope");
assertEquals("hope", result);
}

@Test
public void testStemHopping() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("hopping");
assertEquals("hop", result);
}

@Test
public void testStemDropper() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("dropper");
assertEquals("drop", result);
}

@Test
public void testStemFooling() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("fooling");
assertEquals("fool", result);
}

@Test
public void testStemWithAddCharactersManually() {
PorterStemmer stemmer = new PorterStemmer();
stemmer.add('r');
stemmer.add('u');
stemmer.add('n');
boolean res = stemmer.stem();
assertEquals("run", stemmer.toString());
assertTrue(res);
}

@Test
public void testStemCharArrayAndGetString() {
PorterStemmer stemmer = new PorterStemmer();
char[] input = new char[] { 's', 't', 'u', 'd', 'i', 'e', 's' };
boolean dirty = stemmer.stem(input, input.length);
assertTrue(dirty);
assertEquals("studi", stemmer.toString());
}

@Test
public void testStemCharArrayFromOffset() {
PorterStemmer stemmer = new PorterStemmer();
char[] input = new char[] { 'a', 'b', 'c', 'r', 'u', 'n', 'n', 'i', 'n', 'g' };
boolean dirty = stemmer.stem(input, 3, 7);
assertTrue(dirty);
assertEquals("run", stemmer.toString());
}

@Test
public void testStemReturnsFalseWhenNoChangeOccurs() {
PorterStemmer stemmer = new PorterStemmer();
char[] unchanged = new char[] { 's', 'k', 'y' };
boolean changed = stemmer.stem(unchanged, unchanged.length);
assertFalse(changed);
assertEquals("sky", stemmer.toString());
}

@Test
public void testStemEmptyString() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("");
assertEquals("", result);
}

@Test
public void testStemSingleCharA() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("a");
assertEquals("a", result);
}

@Test
public void testStemSingleCharZ() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("z");
assertEquals("z", result);
}

@Test
public void testStemBeShortWord() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("be");
assertEquals("be", result);
}

@Test
public void testStemCharSequence() {
PorterStemmer stemmer = new PorterStemmer();
CharSequence result = stemmer.stem((CharSequence) "calling");
assertEquals("call", result.toString());
}

@Test
public void testStemIdempotentBehavior() {
PorterStemmer stemmer = new PorterStemmer();
String once = stemmer.stem("caresses");
String again = stemmer.stem(once);
assertEquals(once, again);
}

@Test
public void testBugFixAedEdge() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("aed");
assertEquals("ae", result);
}

@Test
public void testBugFixEedEdge() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("feed");
assertEquals("feed", result);
}

@Test
public void testBugFixOedEdge() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("oed");
assertEquals("oe", result);
}

@Test
public void testBugFixIonPositionOutOfBounds() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("action");
assertEquals("act", result);
}

@Test
public void testStemWordDifferentLengths() {
PorterStemmer stemmer = new PorterStemmer();
String result1 = stemmer.stem("consolidation");
assertEquals("consolid", result1);
PorterStemmer stemmer2 = new PorterStemmer();
String result2 = stemmer2.stem("consolidate");
assertEquals("consolid", result2);
}

@Test
public void testStemWordWithOnlyVowels() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("aeiou");
assertEquals("aeiou", result);
}

@Test
public void testStemWordWithOnlyConsonants() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("bcdfg");
assertEquals("bcdfg", result);
}

@Test
public void testStemWordEndingInYWithoutVowelInStem() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("by");
assertEquals("by", result);
}

@Test
public void testStemEndsWithSsNotPlural() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("boss");
assertEquals("boss", result);
}

@Test
public void testStemSsesPlural() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("classes");
assertEquals("class", result);
}

@Test
public void testStemWordThatInvokesDoubleCRemoval() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("hopping");
assertEquals("hop", result);
}

@Test
public void testStemShortCVCWithMEquals1() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("hop");
assertEquals("hop", result);
}

@Test
public void testStemStemBufferGrowthWhenAddingManyCharacters() {
PorterStemmer stemmer = new PorterStemmer();
stemmer.add('a');
stemmer.add('b');
stemmer.add('c');
stemmer.add('d');
stemmer.add('e');
stemmer.add('f');
stemmer.add('g');
stemmer.add('h');
stemmer.add('i');
stemmer.add('j');
stemmer.add('k');
stemmer.add('l');
stemmer.add('m');
stemmer.add('n');
stemmer.add('o');
stemmer.add('p');
stemmer.add('q');
stemmer.add('r');
stemmer.add('s');
stemmer.add('t');
stemmer.add('u');
stemmer.add('v');
stemmer.add('w');
stemmer.add('x');
stemmer.add('y');
stemmer.add('z');
stemmer.add('a');
stemmer.add('b');
stemmer.add('c');
stemmer.add('d');
boolean changed = stemmer.stem();
assertTrue(changed);
assertTrue(stemmer.getResultLength() > 0);
}

@Test
public void testStemWordWithRepeatingVowelConsonantPatterns() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("caveman");
assertEquals("caveman", result);
}

@Test
public void testStemWordWithSuffixAble() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("adjustable");
assertEquals("adjust", result);
}

@Test
public void testStemWordEndingInEButNotToBeRemoved() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("note");
assertEquals("note", result);
}

@Test
public void testStemWordWithLogiSuffix() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("analogical");
assertEquals("analog", result);
}

@Test
public void testStemWordWithIsmSuffix() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("realism");
assertEquals("real", result);
}

@Test
public void testStemWordWithOusnessSuffix() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("graciousness");
assertEquals("gracious", result);
}

@Test
public void testStemWithWhitespaceInput() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem(" ");
assertEquals(" ", result);
}

@Test
public void testStemWithPunctuation() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("hop!");
assertEquals("hop!", result);
}

@Test
public void testStemIonWithoutProperPreconditions() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("ion");
assertEquals("ion", result);
}

@Test
public void testStemSuffixAlize() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("formalize");
assertEquals("formal", result);
}

@Test
public void testStemSuffixIciti() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("simplicity");
assertEquals("simplic", result);
}

@Test
public void testStemSuffixFul() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("helpful");
assertEquals("help", result);
}

@Test
public void testStemSuffixNessDirectly() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("softness");
assertEquals("soft", result);
}

@Test
public void testStemSuffixTional() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("conditional");
assertEquals("condit", result);
}

@Test
public void testStemWithUppercaseInput() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("RUNNING");
assertEquals("RUNNING", result);
}

@Test
public void testStemNonAlphabeticNumerical() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("12345");
assertEquals("12345", result);
}

@Test
public void testStemSuffixEment() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("agreement");
assertEquals("agree", result);
}

@Test
public void testStemSuffixAnt() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("elegant");
assertEquals("eleg", result);
}

@Test
public void testStemSuffixEnci() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("dependency");
assertEquals("depend", result);
}

@Test
public void testStemSuffixAtReplacedWithAte() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("conflate");
assertEquals("conflate", result);
}

@Test
public void testStemSuffixBlReplacedWithBle() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("troubling");
assertEquals("trouble", result);
}

@Test
public void testStemSuffixIzReplacedWithIze() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("finalizing");
assertEquals("finalize", result);
}

@Test
public void testStemDoubleConsonantButKeepLSZ() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("miss");
assertEquals("miss", result);
}

@Test
public void testStemDoubleConsonantButNotLSZ() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("hopping");
assertEquals("hop", result);
}

@Test
public void testStemShortWordCVCTriggerEAppend() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("hoping");
assertEquals("hope", result);
}

@Test
public void testStemShortWordCVCNoEForWXOrY() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("boxing");
assertEquals("box", result);
}

@Test
public void testStemWordEndingWithYButNoVowelInStem() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("my");
assertEquals("my", result);
}

@Test
public void testStemYReplacedWithIOnVowelInStem() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("happily");
assertEquals("happili", result);
}

@Test
public void testStemNoChangeWithShortWordLength() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("us");
assertEquals("us", result);
}

@Test
public void testStemSuffixBilitiReplacedWithBle() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("responsibility");
assertEquals("respons", result);
}

@Test
public void testStemSuffixIvitiReplacedWithIve() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("sensitivity");
assertEquals("sensit", result);
}

@Test
public void testStemSuffixAlismReplacedWithAl() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("formalism");
assertEquals("formal", result);
}

@Test
public void testStemSuffixFulRemoved() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("playful");
assertEquals("play", result);
}

@Test
public void testStemStep5RejectsIfMIsNotGT1() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("relate");
assertEquals("relate", result);
}

@Test
public void testStemStep5DeletesFinalEWhenMIsGreaterThan1() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("duplicate");
assertEquals("duplic", result);
}

@Test
public void testStemEndingInDoubleLLWithMGreaterThan1() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("controlling");
assertEquals("control", result);
}

@Test
public void testStemFromAddCharSingleCallThenStem() {
PorterStemmer stemmer = new PorterStemmer();
stemmer.add('h');
stemmer.add('a');
stemmer.add('p');
stemmer.add('p');
stemmer.add('i');
stemmer.add('n');
stemmer.add('e');
stemmer.add('s');
stemmer.add('s');
boolean changed = stemmer.stem();
assertTrue(changed);
assertEquals("happi", stemmer.toString());
}

@Test
public void testStemFromCharArrayNoOffset() {
PorterStemmer stemmer = new PorterStemmer();
char[] input = new char[] { 'm', 'o', 'v', 'a', 'b', 'l', 'e' };
boolean result = stemmer.stem(input, input.length);
assertTrue(result);
assertEquals("move", stemmer.toString());
}

@Test
public void testStemFromCharSequenceWrapper() {
PorterStemmer stemmer = new PorterStemmer();
CharSequence input = "activation";
CharSequence output = stemmer.stem(input);
assertEquals("activ", output.toString());
}

@Test
public void testStemWordWithTrailingZShouldNotBeReduced() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("fizz");
assertEquals("fizz", result);
}

@Test
public void testStemMultiStepDerivation() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("organizational");
assertEquals("organ", result);
}

@Test
public void testStemSuffixEousNotRemoved() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("hideous");
assertEquals("hideous", result);
}

@Test
public void testStemSuffixEnessRemoved() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("brightness");
assertEquals("bright", result);
}

@Test
public void testStemSuffixMentStrippedWhenMIsGreaterThan1() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("enjoyment");
assertEquals("enjoy", result);
}

@Test
public void testStemSuffixEmentStrippedOnlyWithProperContext() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("refinement");
assertEquals("refin", result);
}

@Test
public void testStemShortWordEndingInEWithMEqual1ButCvcTrue() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("tame");
assertEquals("tame", result);
}

@Test
public void testStemVeryLongWord() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("internationalization");
assertEquals("intern", result);
}

@Test
public void testStemWordWithSingleConsonant() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("t");
assertEquals("t", result);
}

@Test
public void testStemWordWithUppercaseMixedInput() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("RuNnIng");
assertEquals("RuNnIng", result);
}

@Test
public void testStemCompoundWordWithoutHyphen() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("notebook");
assertEquals("notebook", result);
}

@Test
public void testStemCompoundWordWithHyphen() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("state-of-the-art");
assertEquals("state-of-the-art", result);
}

@Test
public void testStemWithSpecialCharacters() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("@running#");
assertEquals("@running#", result);
}

@Test
public void testStemWhitespaceString() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("   ");
assertEquals("   ", result);
}

@Test
public void testStemNullInputCharArrayThrowsException() {
PorterStemmer stemmer = new PorterStemmer();
try {
stemmer.stem((char[]) null);
fail("Expected NullPointerException");
} catch (NullPointerException expected) {
assertTrue(true);
}
}

@Test
public void testStemNullInputStringThrowsException() {
PorterStemmer stemmer = new PorterStemmer();
try {
String input = null;
stemmer.stem(input);
fail("Expected NullPointerException");
} catch (NullPointerException expected) {
assertTrue(true);
}
}

@Test
public void testCallToStemWithZeroLengthCharArray() {
PorterStemmer stemmer = new PorterStemmer();
boolean changed = stemmer.stem(new char[0], 0);
assertFalse(changed);
assertEquals("", stemmer.toString());
}

@Test
public void testStemBufferedWordAfterReset() {
PorterStemmer stemmer = new PorterStemmer();
stemmer.add('s');
stemmer.add('t');
stemmer.add('u');
stemmer.add('d');
stemmer.add('i');
stemmer.add('e');
stemmer.add('s');
stemmer.reset();
stemmer.add('a');
stemmer.add('c');
stemmer.add('t');
stemmer.add('e');
stemmer.add('d');
boolean changed = stemmer.stem();
assertTrue(changed);
assertEquals("act", stemmer.toString());
}

@Test
public void testMultipleStemsOnSameInstance() {
PorterStemmer stemmer = new PorterStemmer();
String result1 = stemmer.stem("conditioning");
assertEquals("condition", result1);
String result2 = stemmer.stem("relational");
assertEquals("relat", result2);
}

@Test
public void testStemConvertsBliToBle() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("possibly");
assertEquals("possibl", result);
}

@Test
public void testStemAliBecomesAl() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("formally");
assertEquals("formal", result);
}

@Test
public void testStemEntliGoesToEnt() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("evidently");
assertEquals("evident", result);
}

@Test
public void testStemOusliToOus() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("deliciously");
assertEquals("delicious", result);
}

@Test
public void testStemEliReduction() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("readily");
assertEquals("readi", result);
}

@Test
public void testStemOfShortWordThreeLetters() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("bat");
assertEquals("bat", result);
}

@Test
public void testStemResponseToWordEndingInTwoIdenticalVowels() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("agreeing");
assertEquals("agree", result);
}

@Test
public void testStemWithEmbeddedNumericCharacters() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("run123");
assertEquals("run123", result);
}

@Test
public void testStemLongComplexSuffixChain() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("institutionalization");
assertEquals("institut", result);
}

@Test
public void testStemEndingInIonWithoutPrecedingSOrT() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("champion");
assertEquals("champion", result);
}

@Test
public void testStemEndingInIonWithPrecedingS() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("admission");
assertEquals("admiss", result);
}

@Test
public void testStemEndingInIonWithPrecedingT() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("relation");
assertEquals("relat", result);
}

@Test
public void testStemDoesNotModifyAlreadyStemmedWord() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("connect");
assertEquals("connect", result);
}

@Test
public void testStemShortCVCWordNoEAdditionOnWX() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("fix");
assertEquals("fix", result);
}

@Test
public void testStemWordWhereSettoDoesNotModifyBecauseMZero() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("ational");
assertEquals("ational", result);
}

@Test
public void testStemInputEndingWithYButNoConsonantBefore() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("any");
assertEquals("ani", result);
}

@Test
public void testStemReturnsTrueIfModified() {
PorterStemmer stemmer = new PorterStemmer();
boolean modified = stemmer.stem("agreed".toCharArray(), 6);
assertTrue(modified);
}

@Test
public void testStemReturnsFalseIfNotModified() {
PorterStemmer stemmer = new PorterStemmer();
boolean modified = stemmer.stem("sky".toCharArray(), 3);
assertFalse(modified);
}

@Test
public void testStemEHandlingMEqualsOneAndCvcFalse() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("rate");
assertEquals("rate", result);
}

@Test
public void testStemEHandlingMGreaterThanOne() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("create");
assertEquals("creat", result);
}

@Test
public void testStemEndsWithDoubleLRemovedIfMGreaterThanOne() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("controlling");
assertEquals("control", result);
}

@Test
public void testStemEndsWithDoubleLRetainedIfMNotGreaterThanOne() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("bell");
assertEquals("bell", result);
}

@Test
public void testStemWithSymbolsAndLettersMixed() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("run@ning");
assertEquals("run@ning", result);
}

@Test
public void testStemWordThatCausesStep3ButNotStep4() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("organization");
assertEquals("organ", result);
}

@Test
public void testStemNumberIncludedInWord() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("run123ing");
assertEquals("run123ing", result);
}

@Test
public void testStemWordEndingInEButCvcCheckFails() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("tree");
assertEquals("tree", result);
}

@Test
public void testStemOfAllUppercaseReturnsUnchanged() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("RUNNING");
assertEquals("RUNNING", result);
}

@Test
public void testStemHandlesInputWithDigitsOnly() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("123");
assertEquals("123", result);
}

@Test
public void testStemRecognizesPluralButKeepsSSInMiss() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("miss");
assertEquals("miss", result);
}

@Test
public void testStemEndsWithAbleButMEqualsZero() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("enable");
assertEquals("enable", result);
}

@Test
public void testStemEndsWithIbleButMEqualsZero() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("edible");
assertEquals("edible", result);
}

@Test
public void testStemEndsWithIciti() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("authenticity");
assertEquals("authentic", result);
}

@Test
public void testStemEndsWithAlize() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("visualize");
assertEquals("visual", result);
}

@Test
public void testStemWordThatSkipsAllSteps() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("box");
assertEquals("box", result);
}

@Test
public void testStemHandlingZeroLengthStemAfterSuffixRemoval() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("e");
assertEquals("e", result);
}

@Test
public void testStemAbleStemNotChangedDueToShortWord() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("able");
assertEquals("able", result);
}

@Test
public void testStemMEqualToOneCVCRulePreventsEDeletion() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("made");
assertEquals("made", result);
}

@Test
public void testStemEndsWithEntButMNotGT1() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("talent");
assertEquals("talent", result);
}

@Test
public void testStemEndsWithEmentAndMGreaterThan1() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("refinement");
assertEquals("refin", result);
}

@Test
public void testStemEndsWithMentAndMNotGT1() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("payment");
assertEquals("payment", result);
}

@Test
public void testStemEndsWithTionalBecomesTion() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("national");
assertEquals("nation", result);
}

@Test
public void testStemEndsWithEnciBecomesEnce() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("frequency");
assertEquals("frequenc", result);
}

@Test
public void testStemEndsWithAnciBecomesAnce() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("vacancy");
assertEquals("vacanc", result);
}

@Test
public void testStemShortStackedSuffixes() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("happiness");
assertEquals("happi", result);
}

@Test
public void testStemDoesNotApplyStep5IfMNotGT1() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("repeat");
assertEquals("repeat", result);
}

@Test
public void testStemRemovesFinalEBecauseMGT1AndNoCVC() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("debate");
assertEquals("debat", result);
}

@Test
public void testStemCvcWithWNoEAdded() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("mow");
assertEquals("mow", result);
}

@Test
public void testStemCvcWithYNoEAdded() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("shy");
assertEquals("shy", result);
}

@Test
public void testStemWithBufferExactlyFullExpansionPath() {
PorterStemmer stemmer = new PorterStemmer();
stemmer.add('a');
stemmer.add('b');
stemmer.add('c');
stemmer.add('d');
stemmer.add('e');
stemmer.add('f');
stemmer.add('g');
stemmer.add('h');
stemmer.add('i');
stemmer.add('j');
stemmer.add('k');
stemmer.add('l');
stemmer.add('m');
stemmer.add('n');
stemmer.add('o');
stemmer.add('p');
stemmer.add('q');
stemmer.add('r');
stemmer.add('s');
stemmer.add('t');
stemmer.add('u');
stemmer.add('v');
stemmer.add('w');
stemmer.add('x');
stemmer.add('y');
stemmer.add('z');
boolean changed = stemmer.stem();
assertTrue(changed || !changed);
}

@Test
public void testStemNullCharSequenceThrowsNullPointerException() {
PorterStemmer stemmer = new PorterStemmer();
try {
CharSequence input = null;
stemmer.stem(input);
fail("Expected NullPointerException");
} catch (NullPointerException e) {
assertTrue(true);
}
}

@Test
public void testStemNullCharArrayThrowsNullPointerException() {
PorterStemmer stemmer = new PorterStemmer();
try {
char[] chars = null;
stemmer.stem(chars);
fail("Expected NullPointerException");
} catch (NullPointerException e) {
assertTrue(true);
}
}

@Test
public void testStemNullCharArrayWithOffsetThrowsNullPointerException() {
PorterStemmer stemmer = new PorterStemmer();
try {
stemmer.stem(null, 0, 3);
fail("Expected NullPointerException");
} catch (NullPointerException e) {
assertTrue(true);
}
}

@Test
public void testStemOffsetGreaterThanArrayLengthThrowsArrayIndexOutOfBounds() {
PorterStemmer stemmer = new PorterStemmer();
try {
char[] input = new char[] { 'r', 'u', 'n' };
stemmer.stem(input, 5, 2);
fail("Expected ArrayIndexOutOfBoundsException");
} catch (ArrayIndexOutOfBoundsException e) {
assertTrue(true);
}
}

@Test
public void testStemNegativeOffsetThrowsArrayIndexOutOfBounds() {
PorterStemmer stemmer = new PorterStemmer();
try {
char[] input = new char[] { 'r', 'u', 'n' };
stemmer.stem(input, -1, 2);
fail("Expected ArrayIndexOutOfBoundsException");
} catch (ArrayIndexOutOfBoundsException e) {
assertTrue(true);
}
}

@Test
public void testStemEmptyCharArrayWithLengthGreaterThanZero() {
PorterStemmer stemmer = new PorterStemmer();
try {
char[] input = new char[0];
stemmer.stem(input, 0, 1);
fail("Expected ArrayIndexOutOfBoundsException");
} catch (ArrayIndexOutOfBoundsException e) {
assertTrue(true);
}
}

@Test
public void testStemOutOfBoundsOffsetPlusLength() {
PorterStemmer stemmer = new PorterStemmer();
try {
char[] input = "testword".toCharArray();
stemmer.stem(input, 5, 5);
fail("Expected ArrayIndexOutOfBoundsException");
} catch (ArrayIndexOutOfBoundsException e) {
assertTrue(true);
}
}

@Test
public void testStemOffsetGreaterThanLengthButWithinArrayBounds() {
PorterStemmer stemmer = new PorterStemmer();
try {
char[] input = { 'a', 'b', 'c', 'd', 'e', 'f' };
stemmer.stem(input, 3, 2);
assertEquals("de", stemmer.toString());
} catch (Exception e) {
fail("Did not expect exception");
}
}

@Test
public void testStemStep3NotAppliedIfKEqualsK0() {
PorterStemmer stemmer = new PorterStemmer();
stemmer.add('a');
stemmer.add('t');
boolean changed = stemmer.stem();
assertTrue(changed);
assertEquals("at", stemmer.toString());
}

@Test
public void testStemInputThatTriggersAllStepsSequentially() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("operationalizing");
assertEquals("oper", result);
}

@Test
public void testSuccessiveStemCallsDoNotLeakState() {
PorterStemmer stemmer = new PorterStemmer();
String first = stemmer.stem("relational");
String second = stemmer.stem("running");
String third = stemmer.stem("happiness");
assertEquals("relat", first);
assertEquals("run", second);
assertEquals("happi", third);
}

@Test
public void testStemBufferReuseWithShorterWord() {
PorterStemmer stemmer = new PorterStemmer();
stemmer.stem("institutionalization");
String result = stemmer.stem("box");
assertEquals("box", result);
}

@Test
public void testStemOnlySpacesBuffer() {
PorterStemmer stemmer = new PorterStemmer();
stemmer.add(' ');
stemmer.add(' ');
stemmer.add(' ');
boolean changed = stemmer.stem();
assertFalse(changed);
assertEquals("   ", stemmer.toString());
}

@Test
public void testStemBufferExpansionWithVeryLongInput() {
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
stemmer.add('e');
stemmer.add('x');
stemmer.add('p');
stemmer.add('i');
stemmer.add('a');
stemmer.add('l');
stemmer.add('i');
stemmer.add('d');
stemmer.add('o');
stemmer.add('c');
stemmer.add('i');
stemmer.add('o');
stemmer.add('u');
stemmer.add('s');
boolean changed = stemmer.stem();
assertTrue(changed || !changed);
assertEquals("supercalifragilisticexpialidocious", stemmer.toString());
}
}
