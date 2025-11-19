package opennlp.tools.stemmer;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PorterStemmer_1_GPTLLMTest {

@Test
public void testStemPluralCats() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("cats");
assertEquals("cat", result);
}

@Test
public void testStemPluralPonies() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("ponies");
assertEquals("poni", result);
}

@Test
public void testStemEdFeed() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("feed");
assertEquals("feed", result);
}

@Test
public void testStemEdAgreed() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("agreed");
assertEquals("agree", result);
}

@Test
public void testStemEdDisabled() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("disabled");
assertEquals("disable", result);
}

@Test
public void testStemIngMatting() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("matting");
assertEquals("mat", result);
}

@Test
public void testStemIngMating() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("mating");
assertEquals("mate", result);
}

@Test
public void testStemIngMeeting() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("meeting");
assertEquals("meet", result);
}

@Test
public void testStemIngMilling() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("milling");
assertEquals("mill", result);
}

@Test
public void testStemIngMessing() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("messing");
assertEquals("mess", result);
}

@Test
public void testStemPluralMeetings() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("meetings");
assertEquals("meet", result);
}

@Test
public void testStep2_YtoITransformation() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("happy");
assertEquals("happi", result);
}

@Test
public void testStep2_YNoTransformation() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("sky");
assertEquals("sky", result);
}

@Test
public void testStep3_SuffixReductionRelational() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("relational");
assertEquals("relate", result);
}

@Test
public void testStep3_SuffixReductionConditional() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("conditional");
assertEquals("condition", result);
}

@Test
public void testStep4_FulSuffix() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("hopeful");
assertEquals("hope", result);
}

@Test
public void testStep4_NessSuffix() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("darkness");
assertEquals("dark", result);
}

@Test
public void testStep5_MentSuffix() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("adjustment");
assertEquals("adjust", result);
}

@Test
public void testStep5_IonEnding() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("adoption");
assertEquals("adopt", result);
}

@Test
public void testStep6_FinalE_Removed() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("rate");
assertEquals("rate", result);
}

@Test
public void testAddCharAndToString() {
PorterStemmer stemmer = new PorterStemmer();
stemmer.add('s');
stemmer.add('t');
stemmer.add('o');
stemmer.add('p');
String result = stemmer.toString();
assertEquals("stop", result);
}

@Test
public void testResetClearsState() {
PorterStemmer stemmer = new PorterStemmer();
stemmer.add('t');
stemmer.add('e');
stemmer.add('s');
stemmer.add('t');
stemmer.reset();
assertEquals("", stemmer.toString());
}

@Test
public void testStemCharArray() {
PorterStemmer stemmer = new PorterStemmer();
char[] word = { 'h', 'o', 'p', 'p', 'i', 'n', 'g' };
boolean changed = stemmer.stem(word);
String result = stemmer.toString();
assertTrue(changed);
assertEquals("hop", result);
}

@Test
public void testStemCharArrayWithOffsetAndLength() {
PorterStemmer stemmer = new PorterStemmer();
char[] word = { 'x', 'x', 'x', 'h', 'o', 'p', 'p', 'i', 'n', 'g', 'z', 'z' };
boolean changed = stemmer.stem(word, 3, 7);
String result = stemmer.toString();
assertTrue(changed);
assertEquals("hop", result);
}

@Test
public void testStemCharSequenceInput() {
PorterStemmer stemmer = new PorterStemmer();
CharSequence input = "adjustment";
CharSequence output = stemmer.stem(input);
assertEquals("adjust", output.toString());
}

@Test
public void testStemEmptyString() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("");
assertEquals("", result);
}

@Test
public void testStemSingleCharacter() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("a");
assertEquals("a", result);
}

@Test
public void testStemOnlyVowels() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("aeiou");
assertEquals("aeiou", result);
}

@Test
public void testStemOnlyConsonants() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("bcdfg");
assertEquals("bcdfg", result);
}

@Test
public void testBug1_AED() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("aed");
assertEquals("a", result);
}

@Test
public void testBug2_ION() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("direction");
assertEquals("direct", result);
}

@Test
public void testToStringReflectsStemmedValue() {
PorterStemmer stemmer = new PorterStemmer();
stemmer.stem("running".toCharArray(), 7);
String result = stemmer.toString();
assertEquals("run", result);
}

@Test
public void testGetResultBufferAndLength() {
PorterStemmer stemmer = new PorterStemmer();
String word = "relational";
stemmer.stem(word);
char[] buffer = stemmer.getResultBuffer();
int len = stemmer.getResultLength();
String result = new String(buffer, 0, len);
assertEquals("relate", result);
}

@Test
public void testEndsWithSButNotPlural() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("gas");
assertEquals("gas", result);
}

@Test
public void testConsonantYAtStartHandledCorrectly() {
PorterStemmer stemmer = new PorterStemmer();
stemmer.add('y');
stemmer.add('e');
stemmer.add('l');
stemmer.add('l');
stemmer.stem();
String result = stemmer.toString();
assertEquals("yell", result);
}

@Test
public void testStep3LogiSuffixMappedToLog() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("analogic");
assertEquals("analog", result);
}

@Test
public void testStep3DoesNotTransformWhenMZero() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("ization");
assertEquals("ize", result);
}

@Test
public void testStep4RemovesAlizeSuffix() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("finalize");
assertEquals("final", result);
}

@Test
public void testStep5DoesNotRemoveWhenMEqualsOneCvcTrue() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("hop");
assertEquals("hop", result);
}

@Test
public void testDoubleCElseConditionKeepsLsz() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("hopping");
assertEquals("hop", result);
}

@Test
public void testStemWordEndingWithDoubleL() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("falling");
assertEquals("fall", result);
}

@Test
public void testStemWordWithNoApplicableRule() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("zebra");
assertEquals("zebra", result);
}

@Test
public void testSingularFormNotEndingWithS() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("pass");
assertEquals("pass", result);
}

@Test
public void testShortWordTwoLetters() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("it");
assertEquals("it", result);
}

@Test
public void testStemWordWithoutVowels() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("rhythm");
assertEquals("rhythm", result);
}

@Test
public void testStemWordEndingWithAble() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("readable");
assertEquals("read", result);
}

@Test
public void testStemWordEndingWithOus() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("nervous");
assertEquals("nervous", result);
}

@Test
public void testStemWordEndingWithBLE() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("enable");
assertEquals("enabl", result);
}

@Test
public void testStemStopsAtMinimumWordSize() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("ox");
assertEquals("ox", result);
}

@Test
public void testStemWordWithStep3SuffixButFailsMCheck() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("ic");
assertEquals("ic", result);
}

@Test
public void testStemWordWithBlSuffixToBle() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("foible");
assertEquals("foibl", result);
}

@Test
public void testWordWithDoubleConsonantButNotRemoved() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("blessing");
assertEquals("bless", result);
}

@Test
public void testStemWithLAtEndNotDoublec() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("total");
assertEquals("total", result);
}

@Test
public void testStemWithDoubleZ() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("buzzing");
assertEquals("buzz", result);
}

@Test
public void testStemRetainsUsSuffix() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("bonus");
assertEquals("bonus", result);
}

@Test
public void testStemShortSingleConsonantVowel() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("ab");
assertEquals("ab", result);
}

@Test
public void testStem_CvcEndsWithW_ShouldNotRemoveE() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("snowing");
assertEquals("snow", result);
}

@Test
public void testStem_EndsWithX_ShouldNotAddE() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("box");
assertEquals("box", result);
}

@Test
public void testStem_YAsConsonantInStart() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("yell");
assertEquals("yell", result);
}

@Test
public void testStem_YAsVowelAfterConsonantCluster() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("crying");
assertEquals("cri", result);
}

@Test
public void testStemEndsWithEntButShouldNotStripBecauseM0() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("bent");
assertEquals("bent", result);
}

@Test
public void testStemEndsWithEntMGe1ShouldStrip() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("independent");
assertEquals("independ", result);
}

@Test
public void testStemStep3EndsWithAtion() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("automation");
assertEquals("automat", result);
}

@Test
public void testStemStep3EndsWithAlism() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("formalism");
assertEquals("formal", result);
}

@Test
public void testStemStep3EndsWithFulness() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("hopefulness");
assertEquals("hope", result);
}

@Test
public void testStemStep4EndsWithIciti() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("electricity");
assertEquals("electric", result);
}

@Test
public void testStemStep4EndsWithIcal() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("logical");
assertEquals("logic", result);
}

@Test
public void testStemStep5EndsWithEment() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("endowment");
assertEquals("endow", result);
}

@Test
public void testStemStep5EndsWithAnt() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("resistant");
assertEquals("resist", result);
}

@Test
public void testStemStep5HandlesValidIonCondition() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("precaution");
assertEquals("precaut", result);
}

@Test
public void testStemStep5SkipsInvalidIonCondition() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("pigeon");
assertEquals("pigeon", result);
}

@Test
public void testStemStep6RemovesE_WhenMGreaterThan1() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("delete");
assertEquals("delet", result);
}

@Test
public void testStemStep6KeepsE_WhenCvcTrue() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("cave");
assertEquals("cave", result);
}

@Test
public void testStemStep6DoubleLRemovedIfMGT1() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("fall");
assertEquals("fal", result);
}

@Test
public void testStemStep6DoubleLLNotRemovedIfMEqual1() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("bill");
assertEquals("bill", result);
}

@Test
public void testStemNothingChangesReturnsFalseOnStemCharArray() {
PorterStemmer stemmer = new PorterStemmer();
char[] word = { 'r', 'u', 'n' };
boolean changed = stemmer.stem(word);
assertFalse(changed);
assertEquals("run", stemmer.toString());
}

@Test
public void testStemNothingChangesReturnsFalseOnAddCharStem() {
PorterStemmer stemmer = new PorterStemmer();
stemmer.add('r');
stemmer.add('u');
stemmer.add('n');
boolean changed = stemmer.stem();
assertFalse(changed);
assertEquals("run", stemmer.toString());
}

@Test
public void testStemWithExactBufferLengthReallocation() {
PorterStemmer stemmer = new PorterStemmer();
stemmer.add('p');
stemmer.add('a');
stemmer.add('i');
stemmer.add('n');
stemmer.add('f');
stemmer.add('u');
stemmer.add('l');
boolean changed = stemmer.stem();
assertTrue(changed);
assertEquals("pain", stemmer.toString());
}

@Test
public void testStemWithWhitespaceIncluded() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem(" running ");
assertEquals(" running ", result);
}

@Test
public void testStemAllUppercaseInputPreservedCase() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("RUNNING");
assertEquals("RUNNING", result);
}

@Test
public void testStemMixedCaseInput() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("Running");
assertEquals("Running", result);
}

@Test
public void testStemWordWithCapitalLetters() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("RUNNING");
assertEquals("RUNNING", result);
}

@Test
public void testStemEmptyCharArray() {
PorterStemmer stemmer = new PorterStemmer();
char[] word = {};
boolean changed = stemmer.stem(word);
assertFalse(changed);
assertEquals("", stemmer.toString());
}

@Test
public void testStemOverridesInternalBufferAfterReallocation() {
PorterStemmer stemmer = new PorterStemmer();
char[] longWord = new char[100];
longWord[0] = 'r';
longWord[1] = 'e';
longWord[2] = 'l';
longWord[3] = 'a';
longWord[4] = 't';
longWord[5] = 'i';
longWord[6] = 'o';
longWord[7] = 'n';
longWord[8] = 'a';
longWord[9] = 'l';
longWord[10] = 'i';
longWord[11] = 'z';
longWord[12] = 'a';
longWord[13] = 't';
longWord[14] = 'i';
longWord[15] = 'o';
longWord[16] = 'n';
for (int i = 17; i < 100; i++) {
longWord[i] = 'x';
}
boolean changed = stemmer.stem(longWord, 17);
assertTrue(changed);
assertEquals("relate", stemmer.toString());
}

@Test
public void testStemVeryShortWordLengthOne() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("a");
assertEquals("a", result);
}

@Test
public void testStemWordEndingWithIviti() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("sensitivity");
assertEquals("sensit", result);
}

@Test
public void testStemWordEndingWithBiliti() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("responsibility");
assertEquals("respons", result);
}

@Test
public void testStemWordThatTriggersMultipleSteps() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("communicativeness");
assertEquals("communic", result);
}

@Test
public void testStemShortMZeroSkipR() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("tional");
assertEquals("tional", result);
}

@Test
public void testStemSkipStep3DueToKEqualsK0() {
PorterStemmer stemmer = new PorterStemmer();
stemmer.add('a');
stemmer.add('b');
stemmer.add('l');
boolean changed = stemmer.stem();
assertFalse(changed);
assertEquals("abl", stemmer.toString());
}

@Test
public void testStemBufferedInputMatchesStringInput() {
PorterStemmer stemmer = new PorterStemmer();
stemmer.add('r');
stemmer.add('u');
stemmer.add('n');
boolean changed = stemmer.stem();
assertFalse(changed);
String result = stemmer.toString();
assertEquals("run", result);
}

@Test
public void testStemUnrecognizedSuffixShouldNotChange() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("abcdefzzz");
assertEquals("abcdefzzz", result);
}

@Test
public void testStemWordEndingWithEEndsInCvcFalse() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("plate");
assertEquals("plate", result);
}

@Test
public void testStemWithDoubleCButExceptionCharacterZ() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("fizzing");
assertEquals("fizz", result);
}

@Test
public void testStemLongSuffixChainizationalBility() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("organizationally");
assertEquals("organ", result);
}

@Test
public void testStemBufferSmallerThanWord_ReallocationOccurs() {
PorterStemmer stemmer = new PorterStemmer();
char[] input = new char[] { 'c', 'o', 'n', 'n', 'e', 'c', 't', 'i', 'o', 'n', 'a', 'l' };
boolean changed = stemmer.stem(input, 12);
assertTrue(changed);
assertEquals("connect", stemmer.toString());
}

@Test
public void testStemSingularEndsWithSButShouldNotBeStripped() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("as");
assertEquals("as", result);
}

@Test
public void testStemEndsWithSNotFollowedByAnotherS() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("boss");
assertEquals("boss", result);
}

@Test
public void testStemEndsWithIES() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("cries");
assertEquals("cri", result);
}

@Test
public void testStemEndsWithEDWithNoVowelInStem() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("bzzed");
assertEquals("bzzed", result);
}

@Test
public void testStemOnlyConsonantsAndStepShouldBeSkipped() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("bzz");
assertEquals("bzz", result);
}

@Test
public void testStemEndsWithATShouldBecomeATE() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("plated");
assertEquals("plate", result);
}

@Test
public void testStemEndsWithBLShouldBecomeBLE() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("bubbling");
assertEquals("bubble", result);
}

@Test
public void testStemEndsWithIZShouldBecomeIZE() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("sizing");
assertEquals("size", result);
}

@Test
public void testStemEndsWithDoubleConsonantExceptionsLSZShouldRetainSecondChar() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("falling");
assertEquals("fall", result);
}

@Test
public void testStemShortWordWithDoubleConsonantButNotLSZ() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("dropping");
assertEquals("drop", result);
}

@Test
public void testStemDoubleCFollowedByConsonantCluster() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("stuffing");
assertEquals("stuff", result);
}

@Test
public void testStemEndsWithCvcPatternShouldAddE() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("hoping");
assertEquals("hope", result);
}

@Test
public void testStemEndsWithYFollowingVowel() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("annoy");
assertEquals("annoy", result);
}

@Test
public void testStemEndsWithYFollowingConsonantAndVowelInStem() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("happy");
assertEquals("happi", result);
}

@Test
public void testStemEndsWithYAndNoVowelInStem() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("shy");
assertEquals("shy", result);
}

@Test
public void testStemStep5EndsWithOUS() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("previous");
assertEquals("previ", result);
}

@Test
public void testStemStep5EndsWithIVE() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("effective");
assertEquals("effect", result);
}

@Test
public void testStemCallsResetAndNewStemmedInput() {
PorterStemmer stemmer = new PorterStemmer();
stemmer.add('f');
stemmer.add('i');
stemmer.add('s');
stemmer.add('h');
boolean firstChanged = stemmer.stem();
assertFalse(firstChanged);
stemmer.reset();
stemmer.add('m');
stemmer.add('e');
stemmer.add('e');
stemmer.add('t');
stemmer.add('i');
stemmer.add('n');
stemmer.add('g');
boolean secondChanged = stemmer.stem();
String result = stemmer.toString();
assertTrue(secondChanged);
assertEquals("meet", result);
}

@Test
public void testStemExactStep5IonConditionWithPrecedingT() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("legislation");
assertEquals("legislat", result);
}

@Test
public void testStemIonNotStrippedDueToWrongPrecedingLetter() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("champion");
assertEquals("champion", result);
}

@Test
public void testStemAWordWithSuffixStripThenFinalE() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("debating");
assertEquals("debat", result);
}

@Test
public void testStemMZero_PreservesOriginal() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("elate");
assertEquals("elate", result);
}

@Test
public void testStemWithWhitespaceStringShouldRemainUnchanged() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem(" ");
assertEquals(" ", result);
}

@Test
public void testStemWithLeadingTrailingWhitespaceShouldBeTreatedAsDifferentWord() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem(" running ");
assertEquals(" running ", result);
}

@Test
public void testStemWithSpecialCharactersOnly() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("!!!");
assertEquals("!!!", result);
}

@Test
public void testStemEndsWithERStep5() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("runner");
assertEquals("runner", result);
}

@Test
public void testToStringReturnsPartialWhenAddUsedWithoutStem() {
PorterStemmer stemmer = new PorterStemmer();
stemmer.add('t');
stemmer.add('e');
stemmer.add('s');
stemmer.add('t');
String result = stemmer.toString();
assertEquals("test", result);
}

@Test
public void testStemEndsWithICRemovesSuffix() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("specific");
assertEquals("specif", result);
}

@Test
public void testStemEndsWithENCE_MGreaterThanZero() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("insistence");
assertEquals("insist", result);
}

@Test
public void testStemEndsWithENCE_MEqualZero() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("ence");
assertEquals("ence", result);
}

@Test
public void testStemStep4_IcitiSuffixReplacedWithIc() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("authenticity");
assertEquals("authentic", result);
}

@Test
public void testStemStep4_IcalSuffixReplacedWithIc() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("logical");
assertEquals("logic", result);
}

@Test
public void testStemStep4_FulSuffixRemoved() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("helpful");
assertEquals("help", result);
}

@Test
public void testStemDoubleLAtEndRemovedOnlyWhenMGreaterThanOne() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("parallel");
assertEquals("paral", result);
}

@Test
public void testStemDoubleLNotRemovedWhenMEqualOne() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("ill");
assertEquals("ill", result);
}

@Test
public void testStemEndsWithERButMEqualZero() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("er");
assertEquals("er", result);
}

@Test
public void testStemEndsWithERAndMGreaterThanZero() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("runner");
assertEquals("runner", result);
}

@Test
public void testStemFinalE_RemovedWithMGreaterThanOne() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("remove");
assertEquals("remov", result);
}

@Test
public void testStemFinalE_KeptWhenMEqualOneAndCvcTrue() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("cave");
assertEquals("cave", result);
}

@Test
public void testStemFinalE_RemovedWhenMEqualOneAndCvcFalse() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("brace");
assertEquals("brac", result);
}

@Test
public void testStemEndsWithAbleSuffix_Removed() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("adaptable");
assertEquals("adapt", result);
}

@Test
public void testStemWithOnlyVowelYConsonantBehavior() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("by");
assertEquals("by", result);
}

@Test
public void testStemVowelOnlySequence() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("aeio");
assertEquals("aeio", result);
}

@Test
public void testStemOnlyY_ShouldCountAsConsonantAtStart() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("y");
assertEquals("y", result);
}

@Test
public void testStemEndsWithLogi_MappedToLog() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("geologic");
assertEquals("geolog", result);
}

@Test
public void testStemEndsWithOUSNESS_MappedTo_OUS() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("anxiousness");
assertEquals("anxious", result);
}

@Test
public void testStemConsecutiveStemCallsWithoutReset() {
PorterStemmer stemmer = new PorterStemmer();
stemmer.add('s');
stemmer.add('t');
stemmer.add('u');
stemmer.add('d');
stemmer.add('i');
stemmer.add('e');
stemmer.add('s');
boolean changedFirst = stemmer.stem();
assertTrue(changedFirst);
String resultFirst = stemmer.toString();
assertEquals("studi", resultFirst);
stemmer.reset();
stemmer.add('l');
stemmer.add('o');
stemmer.add('v');
stemmer.add('i');
stemmer.add('n');
stemmer.add('g');
boolean changedSecond = stemmer.stem();
assertTrue(changedSecond);
String resultSecond = stemmer.toString();
assertEquals("love", resultSecond);
}

@Test
public void testStemEndsWithATION_MappedTo_ATE() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("organization");
assertEquals("organ", result);
}

@Test
public void testStemEndsWithATOR_MappedTo_ATE() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("generator");
assertEquals("gener", result);
}

@Test
public void testStemEndsWithIVNESS_MappedTo_IVE() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("assertiveness");
assertEquals("assert", result);
}

@Test
public void testStemEndsWithENTLI_MappedTo_ENT() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("indifferently");
assertEquals("indiffer", result);
}

@Test
public void testStemWordWithMultipleSuffixesRemovedRecursively() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("nationalization");
assertEquals("nation", result);
}

@Test
public void testStemWordEndsWithOUSLI_to_OUS() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("humorously");
assertEquals("humorous", result);
}

@Test
public void testStemEndsWithENT_ShouldRemoveEnt_WhenMCheckPasses() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("anxient");
assertEquals("anxi", result);
}

@Test
public void testStemEndsWithOUS_ShouldRemoveOUS_WhenMCheckPasses() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("generous");
assertEquals("gener", result);
}

@Test
public void testStemWordEndingWithATE_MCheckBoundary() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("relate");
assertEquals("relat", result);
}

@Test
public void testStemEdgeCaseOnlyConsonantsYAsVowel() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("ly");
assertEquals("li", result);
}

@Test
public void testStemWordEndingInMENT() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("endorsement");
assertEquals("endorse", result);
}

@Test
public void testStemWordEndingInEMENT() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("improvement");
assertEquals("improv", result);
}

@Test
public void testStep5HandlesIONPrecededByS() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("collision");
assertEquals("collis", result);
}

@Test
public void testStemEndingWithENCEAndMEqualsOne() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("fence");
assertEquals("fenc", result);
}

@Test
public void testStemWithSingleChar_WordLengthOne() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("x");
assertEquals("x", result);
}

@Test
public void testStemEndingWithNESS_ShouldRemoveNess() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("sadness");
assertEquals("sad", result);
}

@Test
public void testStemWordEndingWithENT_WithJustMEqualsOne() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("tent");
assertEquals("tent", result);
}

@Test
public void testStemWordEndingWithALITI_ShouldMapToAL() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("formality");
assertEquals("formal", result);
}

@Test
public void testStemEndsWithIVITI_ShouldMapTo_IVE() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("activity");
assertEquals("activ", result);
}

@Test
public void testStemEndsWithBILITI_ShouldMapTo_BLE() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("compatibility");
assertEquals("compat", result);
}

@Test
public void testUnchangedShortStemInput() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("am");
assertEquals("am", result);
}

@Test
public void testStemWithNullCharactersInBuffer() {
PorterStemmer stemmer = new PorterStemmer();
stemmer.add('\0');
stemmer.add('\0');
stemmer.add('e');
stemmer.add('d');
boolean changed = stemmer.stem();
String result = stemmer.toString();
assertTrue(changed);
assertEquals("\0\0", result);
}

@Test
public void testStemEndsWithALLY_ShouldRemoveToAL() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("logically");
assertEquals("logic", result);
}

@Test
public void testStemEndsWithELY_ShouldRemoveELIToE() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("nicely");
assertEquals("nice", result);
}

@Test
public void testStemEndsWithOUSLY_ShouldMapTo_OUS() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("generously");
assertEquals("generous", result);
}

@Test
public void testStemEndsWithBLI_ShouldMapTo_BLE() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("nobly");
assertEquals("noble", result);
}

@Test
public void testStemEndsWithENTLI_ShouldMapTo_ENT() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("differently");
assertEquals("differ", result);
}

@Test
public void testStemEndsWithATION_AND_CheckMZero() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("ation");
assertEquals("ation", result);
}

@Test
public void testStemDoubleZSuffix_ShouldRetainZ() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("buzzing");
assertEquals("buzz", result);
}

@Test
public void testStemWordEndingInOUS_ShouldNotBeStrippedIfMZero() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("ous");
assertEquals("ous", result);
}

@Test
public void testStemReturnsOriginalIfNoChangeHappened() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("data");
assertEquals("data", result);
}
}
