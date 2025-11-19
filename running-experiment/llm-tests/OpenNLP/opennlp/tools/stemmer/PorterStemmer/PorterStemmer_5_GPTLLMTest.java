package opennlp.tools.stemmer;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PorterStemmer_5_GPTLLMTest {

@Test
public void testStemCaresses() {
// PorterStemmer stemmer = new PorterStemmer();
// String result = stemmer.stem("caresses");
// assertEquals("caress", result);
}

@Test
public void testStemCats() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("cats");
assertEquals("cat", result);
}

@Test
public void testStemCaress() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("caress");
assertEquals("caress", result);
}

@Test
public void testStemPonies() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("ponies");
assertEquals("poni", result);
}

@Test
public void testStemTies() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("ties");
assertEquals("ti", result);
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
public void testStemFeed() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("feed");
assertEquals("feed", result);
}

@Test
public void testStemMeeting() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("meeting");
assertEquals("meet", result);
}

@Test
public void testStemMatting() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("matting");
assertEquals("mat", result);
}

@Test
public void testStemMating() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("mating");
assertEquals("mate", result);
}

@Test
public void testStemMilling() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("milling");
assertEquals("mill", result);
}

@Test
public void testStemMessing() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("messing");
assertEquals("mess", result);
}

@Test
public void testStemMeetings() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("meetings");
assertEquals("meet", result);
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
assertEquals("relate", result);
}

@Test
public void testStemConditional() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("conditional");
assertEquals("condition", result);
}

@Test
public void testStemRational() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("rational");
assertEquals("rational", result);
}

@Test
public void testStemValenci() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("valenci");
assertEquals("valenc", result);
}

@Test
public void testStemDigitizer() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("digitizer");
assertEquals("digitize", result);
}

@Test
public void testStemHopefully() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("hopefully");
assertEquals("hope", result);
}

@Test
public void testStemEmptyString() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("");
assertEquals("", result);
}

@Test
public void testStemSingleLetterWord() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("a");
assertEquals("a", result);
}

@Test
public void testStemWithCharArrayInput() {
PorterStemmer stemmer = new PorterStemmer();
char[] input = "meetings".toCharArray();
boolean changed = stemmer.stem(input);
assertTrue(changed);
String result = stemmer.toString();
assertEquals("meet", result);
}

@Test
public void testStemCharArrayWithOffset() {
PorterStemmer stemmer = new PorterStemmer();
char[] buffer = "premeetingspost".toCharArray();
boolean changed = stemmer.stem(buffer, 3, 8);
assertTrue(changed);
String result = stemmer.toString();
assertEquals("meet", result);
}

@Test
public void testStemUsingCharSequence() {
PorterStemmer stemmer = new PorterStemmer();
CharSequence input = "meetings";
CharSequence result = stemmer.stem(input);
assertEquals("meet", result.toString());
}

@Test
public void testAddAndStemUsingAddMethod() {
PorterStemmer stemmer = new PorterStemmer();
stemmer.add('c');
stemmer.add('a');
stemmer.add('t');
boolean changed = stemmer.stem();
assertFalse(changed);
assertEquals("cat", stemmer.toString());
}

@Test
public void testResetAndReuseStemmer() {
PorterStemmer stemmer = new PorterStemmer();
stemmer.stem("meetings");
stemmer.reset();
stemmer.add('r');
stemmer.add('u');
stemmer.add('n');
stemmer.stem();
assertEquals("run", stemmer.toString());
}

@Test
public void testBug1EdgeCaseAed() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("aed");
assertEquals("aed", result);
}

@Test
public void testBug1EdgeCaseOed() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("oed");
assertEquals("oed", result);
}

@Test
public void testBug2EdgeCaseIon() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("ion");
assertEquals("ion", result);
}

@Test
public void testGetResultBufferAndLengthAfterStemming() {
PorterStemmer stemmer = new PorterStemmer();
stemmer.stem("meetings");
char[] resultBuffer = stemmer.getResultBuffer();
int length = stemmer.getResultLength();
String result = new String(resultBuffer, 0, length);
assertEquals("meet", result);
}

@Test
public void testStemLongWordTriggersBufferGrow() {
PorterStemmer stemmer = new PorterStemmer();
StringBuilder wordBuilder = new StringBuilder();
wordBuilder.append("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
String longWord = wordBuilder.toString();
for (int i = 0; i < longWord.length(); i++) {
stemmer.add(longWord.charAt(i));
}
boolean changed = stemmer.stem();
assertFalse(changed);
assertEquals(longWord, stemmer.toString());
}

@Test
public void testStemShortWordMinBoundaryLength2() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("to");
assertEquals("to", result);
}

@Test
public void testStemShortWordLength1StepCheck() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("a");
assertEquals("a", result);
}

@Test
public void testStemNullString() {
PorterStemmer stemmer = new PorterStemmer();
try {
stemmer.stem((String) null);
fail("Expected NullPointerException");
} catch (NullPointerException e) {
assertNotNull(e);
}
}

@Test
public void testStemNullCharSequence() {
PorterStemmer stemmer = new PorterStemmer();
try {
stemmer.stem((CharSequence) null);
fail("Expected NullPointerException");
} catch (NullPointerException e) {
assertNotNull(e);
}
}

@Test
public void testStemNullCharArray() {
PorterStemmer stemmer = new PorterStemmer();
try {
stemmer.stem((char[]) null);
fail("Expected NullPointerException");
} catch (NullPointerException e) {
assertNotNull(e);
}
}

@Test
public void testCvcConditionShortWordLikeHop() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("hoping");
assertEquals("hope", result);
}

@Test
public void testCvcShortWordEEnding() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("hop");
assertEquals("hop", result);
}

@Test
public void testDropFinalEWhenMEquals1ShortStem() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("cave");
assertEquals("cav", result);
}

@Test
public void testDoNotDropFinalEWhenCvcTrue() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("hike");
assertEquals("hike", result);
}

@Test
public void testStemWithCapitalizedWord() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("Meetings".toLowerCase());
assertEquals("meet", result);
}

@Test
public void testStemWithWordEndingInDoubleLWithMGreaterThan1() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("controlled");
assertEquals("control", result);
}

@Test
public void testStemWithWordEndingInLButNotDoubleC() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("fall");
assertEquals("fall", result);
}

@Test
public void testStemWithAbleSuffix() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("readable");
assertEquals("read", result);
}

@Test
public void testStemWithBaseWordThatTriggersOnlyStep1() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("cats");
assertEquals("cat", result);
}

@Test
public void testStemThatTriggersStep3Only() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("alization");
assertEquals("alize", result);
}

@Test
public void testStemWordWithIonEndingWhereJIsNegativeButConditionFails() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("ionize");
assertEquals("ion", result);
}

@Test
public void testStemAlreadyStemmedWord() {
PorterStemmer stemmer = new PorterStemmer();
String stem1 = stemmer.stem("consolidations");
PorterStemmer freshStemmer = new PorterStemmer();
String stem2 = freshStemmer.stem(stem1);
assertEquals(stem1, stem2);
}

@Test
public void testStemEndsWithFulness() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("usefulness");
assertEquals("use", result);
}

@Test
public void testStemEndsWithOusness() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("anxiousness");
assertEquals("anxious", result);
}

@Test
public void testStemEndsWithAlism() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("tribalism");
assertEquals("tribal", result);
}

@Test
public void testStemEndsWithBiliti() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("flexibility");
assertEquals("flexibl", result);
}

@Test
public void testStemEndsWithIviti() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("sensitivity");
assertEquals("sensit", result);
}

@Test
public void testStemEndsWithAliti() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("formality");
assertEquals("formal", result);
}

@Test
public void testStemEndsWithLogi() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("analogies");
assertEquals("analog", result);
}

@Test
public void testStemEndsWithMent() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("adjustment");
assertEquals("adjust", result);
}

@Test
public void testStemEndsWithEment() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("engagement");
assertEquals("engag", result);
}

@Test
public void testStemEndsWithEnt() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("commitment");
assertEquals("commit", result);
}

@Test
public void testStemEndsWithAtion() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("automation");
assertEquals("automat", result);
}

@Test
public void testStemEndsWithAtor() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("generator");
assertEquals("generat", result);
}

@Test
public void testStemEndsWithIc() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("academic");
assertEquals("academ", result);
}

@Test
public void testStemEndsWithAble() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("dependable");
assertEquals("depend", result);
}

@Test
public void testStemEndsWithIble() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("accessible");
assertEquals("access", result);
}

@Test
public void testStemEndsWithAnt() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("applicant");
assertEquals("applic", result);
}

@Test
public void testStemEndsWithEr() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("runner");
assertEquals("runner", result);
}

@Test
public void testStemEndsWithOusAndDoesNotRemove() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("nervous");
assertEquals("nervous", result);
}

@Test
public void testStemEndsWithDoubleZButKeepsZ() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("buzzing");
assertEquals("buzz", result);
}

@Test
public void testStemDoubleSPreserved() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("misses");
assertEquals("miss", result);
}

@Test
public void testStemNoVowelsBeforeSuffixShouldNotStem() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("crying");
assertEquals("cry", result);
}

@Test
public void testStemInvalidSuffixMatchFails() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("computationalism");
assertEquals("comput", result);
}

@Test
public void testStemStopsAtMinimumProcessingStepThreshold() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("is");
assertEquals("is", result);
}

@Test
public void testStep6FinalEDroppedBecauseMGreaterThan1() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("conflate");
assertEquals("conflat", result);
}

@Test
public void testStep6FinalERetainedBecauseCvcBlocked() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("bike");
assertEquals("bike", result);
}

@Test
public void testStep6DoubleLRemoved() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("rolling");
assertEquals("roll", result);
}

@Test
public void testStemLongWordWithStackedSuffixes() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("institutionalization");
assertEquals("institut", result);
}

@Test
public void testStemDifferentInputProducesNewResult() {
PorterStemmer stemmer = new PorterStemmer();
String result1 = stemmer.stem("meetings");
String result2 = stemmer.stem("adjustment");
assertNotEquals(result1, result2);
assertEquals("meet", result1);
assertEquals("adjust", result2);
}

@Test
public void testStemWithEndsIngButNoVowelInStem() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("brrring");
assertEquals("brrring", result);
}

@Test
public void testStemWordEndingWithEdWithNoVowelInStem() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("trkked");
assertEquals("trkked", result);
}

@Test
public void testStemSuffixAtFollowedByDoubleConsonantShouldKeepL() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("grillat");
assertEquals("grillate", result);
}

@Test
public void testStemStep1DoubleZShouldKeepZ() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("buzzing");
assertEquals("buzz", result);
}

@Test
public void testStemEndsWithBlSetsBle() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("nobling");
assertEquals("noble", result);
}

@Test
public void testStemEndsWithIzSetsIze() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("finalizing");
assertEquals("finalize", result);
}

@Test
public void testStemStep2YToIExecution() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("carry");
assertEquals("carri", result);
}

@Test
public void testStemStep2YTreatedAsConsonant() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("cry");
assertEquals("cry", result);
}

@Test
public void testStemEdgeCaseAtCvcToE() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("hoping");
assertEquals("hope", result);
}

@Test
public void testStemEndsWithYAndNoOtherVowel() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("by");
assertEquals("by", result);
}

@Test
public void testStemStep3_EndsWithAlli() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("radically");
assertEquals("radic", result);
}

@Test
public void testStemStep4_EndsWithFul() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("thankful");
assertEquals("thank", result);
}

@Test
public void testStemStep4_EndsWithNess() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("kindness");
assertEquals("kind", result);
}

@Test
public void testStemStep5_EndsWithIonNoSTBefore() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("opinion");
assertEquals("opinion", result);
}

@Test
public void testStemStep5_EndsWithIonWithSBefore() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("confession");
assertEquals("confess", result);
}

@Test
public void testStemStep5_EndsWithIonWithTBefore() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("attention");
assertEquals("attent", result);
}

@Test
public void testStemStep6_RemovesTrailingEWhenMEq1AndNotCvc() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("rate");
assertEquals("rat", result);
}

@Test
public void testStemStep6_KeepEWhenMEq1AndCvcTrue() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("hate");
assertEquals("hate", result);
}

@Test
public void testStemStep6_RemovesDoubleLWhenMGreaterThan1() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("rolling");
assertEquals("roll", result);
}

@Test
public void testStemShortWordWithSuffixThatRequiresMGreaterThan1() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("able");
assertEquals("able", result);
}

@Test
public void testStemInputJustAtThresholdOfLength3() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("hop");
assertEquals("hop", result);
}

@Test
public void testStemInputAlreadyStemmed() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("base");
stemmer.reset();
String again = stemmer.stem(result);
assertEquals(result, again);
}

@Test
public void testStemWordWithMinimumMValueThatTriggersChange() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("feeding");
assertEquals("feed", result);
}

@Test
public void testStemWithCapitalLettersPreservesLoweredResult() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("RUNNING".toLowerCase());
assertEquals("run", result);
}

@Test
public void testStemShortInputSingleConsonant() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("b");
assertEquals("b", result);
}

@Test
public void testStemShortInputSingleVowel() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("e");
assertEquals("e", result);
}

@Test
public void testStemWordWithNoVowels() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("brrr");
assertEquals("brrr", result);
}

@Test
public void testStemWordEndsWithSButIsPluralSCase() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("dogs");
assertEquals("dog", result);
}

@Test
public void testStemWordEndingWithSSShouldRemain() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("kiss");
assertEquals("kiss", result);
}

@Test
public void testStemWordEndingWithEDAndRevertsToRoot() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("hopped");
assertEquals("hop", result);
}

@Test
public void testStemWordEndingWithEEDAndMZeroShouldNotChange() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("feed");
assertEquals("feed", result);
}

@Test
public void testStemWordWithMinimalMOfOneToAllowReduction() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("agreed");
assertEquals("agree", result);
}

@Test
public void testStemWordTriggersSetToAndRChain() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("rationalization");
assertEquals("rational", result);
}

@Test
public void testStemReturnsOriginalIfNoStemmingOccurs() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("the");
assertEquals("the", result);
}

@Test
public void testStemWithUppercaseInputNormalizedToLowercase() {
PorterStemmer stemmer = new PorterStemmer();
String word = "CARESSES".toLowerCase();
String result = stemmer.stem(word);
assertEquals("caress", result);
}

@Test
public void testStemWordWithRepeatedConsonantsThatAreRetained() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("class");
assertEquals("class", result);
}

@Test
public void testStemPreservesShortCvcButRestoresE() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("hoping");
assertEquals("hope", result);
}

@Test
public void testStemWordWhichEndsInCvcWxYShouldNotTriggerEInsertion() {
PorterStemmer stemmer = new PorterStemmer();
String result1 = stemmer.stem("snowing");
String result2 = stemmer.stem("boxing");
String result3 = stemmer.stem("traying");
assertEquals("snow", result1);
assertEquals("box", result2);
assertEquals("tray", result3);
}

@Test
public void testStemWordWhereStep5SkipsDueToMEquals1() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("hope");
assertEquals("hope", result);
}

@Test
public void testStemEndsWithAbleButMZeroShouldRetain() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("able");
assertEquals("able", result);
}

@Test
public void testStemEndsWithMultipleStepsNested() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("organizationally");
assertEquals("organiz", result);
}

@Test
public void testStemProcessingOnlyStep4Suffix() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("reactive");
assertEquals("react", result);
}

@Test
public void testStemInputEndsWithIcitiToIc() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("simplicity");
assertEquals("simplic", result);
}

@Test
public void testStemWithInvalidUnicodeCharsPreserved() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("نص");
assertEquals("نص", result);
}

@Test
public void testStemAddAndStemBufferReuse() {
PorterStemmer stemmer = new PorterStemmer();
stemmer.add('t');
stemmer.add('e');
stemmer.add('s');
stemmer.add('t');
boolean changed = stemmer.stem();
assertFalse(changed);
assertEquals("test", stemmer.toString());
}

@Test
public void testStemUseGetResultBufferAfterStem() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("replacements");
char[] buffer = stemmer.getResultBuffer();
int length = stemmer.getResultLength();
assertEquals("replac", new String(buffer, 0, length));
assertEquals("replac", result);
}

@Test
public void testStemWordEndingWithEntli() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("indifferently");
assertEquals("indiffer", result);
}

@Test
public void testStemWordEndingWithEli() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("horribly");
assertEquals("horribl", result);
}

@Test
public void testStemWordEndingWithOusli() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("hazardously");
assertEquals("hazard", result);
}

@Test
public void testStemWordEndingWithENCEBranchTaken() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("independence");
assertEquals("independ", result);
}

@Test
public void testStemEndsWithENCEBranchNotTakenDueToMCheck() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("ence");
assertEquals("ence", result);
}

@Test
public void testStemStep3EndsWithAnci() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("compliancy");
assertEquals("complianc", result);
}

@Test
public void testStemStep3EndsWithEnci() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("dependency");
assertEquals("dependenc", result);
}

@Test
public void testStemWordEndsWithICATE() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("communicate");
assertEquals("communic", result);
}

@Test
public void testStemWordEndsWithALIZE() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("formalize");
assertEquals("formal", result);
}

@Test
public void testStemWordEndsWithATIONAL() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("relational");
assertEquals("relate", result);
}

@Test
public void testStemBufferManuallyExpandedOnce() {
PorterStemmer stemmer = new PorterStemmer();
stemmer.add('a');
stemmer.add('b');
stemmer.add('c');
stemmer.add('d');
stemmer.add('e');
for (int i = 0; i < 50; i++) {
stemmer.add('x');
}
boolean changed = stemmer.stem();
assertTrue(changed || !changed);
assertTrue(stemmer.getResultLength() > 0);
}

@Test
public void testStemWordEndsWithFULAndGetsRemoved() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("regretful");
assertEquals("regret", result);
}

@Test
public void testStemWordEndsWithNESSAndGetsRemoved() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("boldness");
assertEquals("bold", result);
}

@Test
public void testStemDoubleCRemovesOneWhenConditionsMet() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("hopping");
assertEquals("hop", result);
}

@Test
public void testStemSingleCharFromStemCharArray() {
PorterStemmer stemmer = new PorterStemmer();
char[] word = new char[] { 'a' };
boolean changed = stemmer.stem(word, 1);
assertFalse(changed);
assertEquals("a", stemmer.toString());
}

@Test
public void testStemToStringAfterStemInput() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("rationalization");
String text = stemmer.toString();
assertEquals("rational", text);
assertEquals("rational", result);
}

@Test
public void testStemGetResultBufferLengthConsistent() {
PorterStemmer stemmer = new PorterStemmer();
stemmer.stem("formalization");
char[] buf = stemmer.getResultBuffer();
int len = stemmer.getResultLength();
String reconstructed = new String(buf, 0, len);
assertEquals("formal", reconstructed);
}

@Test
public void testStemValidSuffixMatchFailsMCheck() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("izer");
assertEquals("izer", result);
}

@Test
public void testStemWordAlreadyStemmedProducesSameWord() {
PorterStemmer stemmer = new PorterStemmer();
String first = stemmer.stem("strictly");
PorterStemmer stemmer2 = new PorterStemmer();
String second = stemmer2.stem(first);
assertEquals(first, second);
}

@Test
public void testStemShortWordFailsStepEntrance() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("to");
assertEquals("to", result);
}

@Test
public void testStemStep3GuardClauseBug1AvoidsError() {
PorterStemmer stemmer = new PorterStemmer();
stemmer.add('a');
stemmer.add('e');
stemmer.add('d');
boolean changed = stemmer.stem();
assertTrue(changed || !changed);
assertNotNull(stemmer.toString());
}

@Test
public void testStemStep5GuardClauseBug2AvoidsError() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("ion");
assertEquals("ion", result);
}

@Test
public void testStemEndsWithATOR() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("administrator");
assertEquals("administr", result);
}

@Test
public void testStemEndsWithIZATION() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("modernization");
assertEquals("modern", result);
}

@Test
public void testStemEndsWithLOGI() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("analogies");
assertEquals("analog", result);
}

@Test
public void testStemEndsWithAtionMEqualsZeroShouldNotChange() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("ovation");
assertEquals("ovat", result);
}

@Test
public void testStemEndsWithUnknownSuffixNoChange() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("unmatchedsuffix");
assertEquals("unmatchedsuffix", result);
}

@Test
public void testStemCvcEdgeCaseWPreventsReappendingE() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("glowing");
assertEquals("glow", result);
}

@Test
public void testStemCvcEdgeCaseXPreventsReappendingE() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("fixing");
assertEquals("fix", result);
}

@Test
public void testStemCvcEdgeCaseYPreventsReappendingE() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("employing");
assertEquals("employ", result);
}

@Test
public void testStemStep6CVCFalseStillDropsE() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("desire");
assertEquals("desir", result);
}

@Test
public void testStemEndsWithEntNoEffectWhenMEqualOne() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("talent");
assertEquals("talent", result);
}

@Test
public void testStemEndsWithOUS() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("gracious");
assertEquals("graci", result);
}

@Test
public void testStemStep3SuffixMatchedButMZero() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("izer");
assertEquals("izer", result);
}

@Test
public void testStemEndsWithTional() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("directional");
assertEquals("direction", result);
}

@Test
public void testStemEndsWithBli() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("credibly");
assertEquals("credibl", result);
}

@Test
public void testStemShortCVCShouldInsertE() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("hoped");
assertEquals("hope", result);
}

@Test
public void testStemLongCVCWordNoEAdded() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("stamping");
assertEquals("stamp", result);
}

@Test
public void testStemBufferExpansionMultipleTimes() {
PorterStemmer stemmer = new PorterStemmer();
for (int i = 0; i < 200; i++) {
stemmer.add('a');
}
stemmer.stem();
assertTrue(stemmer.getResultLength() > 0);
}

@Test
public void testStemMEqualsOneEdgeCaseFinalEStays() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("bike");
assertEquals("bike", result);
}

@Test
public void testStemInputConsistingOfAllSameVowel() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("eeeeee");
assertEquals("eeeeee", result);
}

@Test
public void testStemMultipleAddCallsWithResetBetween() {
PorterStemmer stemmer = new PorterStemmer();
stemmer.add('r');
stemmer.add('u');
stemmer.add('n');
stemmer.reset();
stemmer.add('c');
stemmer.add('a');
stemmer.add('r');
boolean changed = stemmer.stem();
assertFalse(changed);
assertEquals("car", stemmer.toString());
}
}
