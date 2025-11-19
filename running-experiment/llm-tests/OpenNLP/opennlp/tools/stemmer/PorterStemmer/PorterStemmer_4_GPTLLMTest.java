package opennlp.tools.stemmer;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PorterStemmer_4_GPTLLMTest {

@Test
public void testStemPluralsAndEdIngSuffixes() {
PorterStemmer stemmer1 = new PorterStemmer();
assertEquals("caress", stemmer1.stem("caresses"));
PorterStemmer stemmer2 = new PorterStemmer();
assertEquals("poni", stemmer2.stem("ponies"));
PorterStemmer stemmer3 = new PorterStemmer();
assertEquals("ti", stemmer3.stem("ties"));
PorterStemmer stemmer4 = new PorterStemmer();
assertEquals("cat", stemmer4.stem("cats"));
PorterStemmer stemmer5 = new PorterStemmer();
assertEquals("agree", stemmer5.stem("agreed"));
PorterStemmer stemmer6 = new PorterStemmer();
assertEquals("disable", stemmer6.stem("disabled"));
PorterStemmer stemmer7 = new PorterStemmer();
assertEquals("mat", stemmer7.stem("matting"));
PorterStemmer stemmer8 = new PorterStemmer();
assertEquals("meet", stemmer8.stem("meeting"));
PorterStemmer stemmer9 = new PorterStemmer();
assertEquals("mill", stemmer9.stem("milling"));
PorterStemmer stemmer10 = new PorterStemmer();
assertEquals("mess", stemmer10.stem("messing"));
PorterStemmer stemmer11 = new PorterStemmer();
assertEquals("meet", stemmer11.stem("meetings"));
}

@Test
public void testStep2YtoITransformation() {
PorterStemmer stemmer1 = new PorterStemmer();
assertEquals("happi", stemmer1.stem("happy"));
PorterStemmer stemmer2 = new PorterStemmer();
assertEquals("readi", stemmer2.stem("ready"));
}

@Test
public void testStep3SuffixReduction() {
PorterStemmer stemmer1 = new PorterStemmer();
assertEquals("relate", stemmer1.stem("relational"));
PorterStemmer stemmer2 = new PorterStemmer();
assertEquals("condition", stemmer2.stem("conditional"));
PorterStemmer stemmer3 = new PorterStemmer();
assertEquals("valenc", stemmer3.stem("valenci"));
PorterStemmer stemmer4 = new PorterStemmer();
assertEquals("digit", stemmer4.stem("digitizer"));
PorterStemmer stemmer5 = new PorterStemmer();
assertEquals("activate", stemmer5.stem("activation"));
}

@Test
public void testStep4SuffixicFullNessRemoval() {
PorterStemmer stemmer1 = new PorterStemmer();
assertEquals("electric", stemmer1.stem("electrical"));
PorterStemmer stemmer2 = new PorterStemmer();
assertEquals("use", stemmer2.stem("usefulness"));
PorterStemmer stemmer3 = new PorterStemmer();
assertEquals("hope", stemmer3.stem("hopeful"));
PorterStemmer stemmer4 = new PorterStemmer();
assertEquals("critic", stemmer4.stem("critical"));
}

@Test
public void testStep5ContextualSuffixStripping() {
PorterStemmer stemmer1 = new PorterStemmer();
assertEquals("fail", stemmer1.stem("failing"));
PorterStemmer stemmer2 = new PorterStemmer();
assertEquals("test", stemmer2.stem("testament"));
PorterStemmer stemmer3 = new PorterStemmer();
assertEquals("statement", stemmer3.stem("statement"));
PorterStemmer stemmer4 = new PorterStemmer();
assertEquals("argu", stemmer4.stem("arguable"));
PorterStemmer stemmer5 = new PorterStemmer();
assertEquals("effect", stemmer5.stem("effective"));
}

@Test
public void testStep6FinalERemoval() {
PorterStemmer stemmer1 = new PorterStemmer();
assertEquals("probabl", stemmer1.stem("probably"));
PorterStemmer stemmer2 = new PorterStemmer();
assertEquals("rational", stemmer2.stem("rationale"));
}

@Test
public void testStemShortWordsNoChange() {
PorterStemmer stemmer1 = new PorterStemmer();
assertEquals("a", stemmer1.stem("a"));
PorterStemmer stemmer2 = new PorterStemmer();
assertEquals("it", stemmer2.stem("it"));
}

@Test
public void testStemAlreadyStemmedWordReturnsSame() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("run", stemmer.stem("run"));
}

@Test
public void testStemWithAddCharIndividually() {
PorterStemmer stemmer = new PorterStemmer();
stemmer.add('a');
stemmer.add('g');
stemmer.add('r');
stemmer.add('e');
stemmer.add('e');
stemmer.add('d');
boolean changed = stemmer.stem();
assertTrue(changed);
assertEquals("agree", stemmer.toString());
}

@Test
public void testStemCharArray() {
PorterStemmer stemmer = new PorterStemmer();
char[] word = new char[] { 'd', 'i', 's', 'a', 'b', 'l', 'e', 'd' };
boolean changed = stemmer.stem(word);
assertTrue(changed);
assertEquals("disable", stemmer.toString());
}

@Test
public void testStemCharArrayWithOffset() {
PorterStemmer stemmer = new PorterStemmer();
char[] word = new char[] { 'x', 'x', 'x', 'd', 'i', 's', 'a', 'b', 'l', 'e', 'd' };
boolean changed = stemmer.stem(word, 3, 8);
assertTrue(changed);
assertEquals("disable", stemmer.toString());
}

@Test
public void testStemCharSequenceInput() {
PorterStemmer stemmer = new PorterStemmer();
CharSequence input = "caresses";
CharSequence result = stemmer.stem(input);
assertTrue(result instanceof String);
assertEquals("caress", result.toString());
}

@Test
public void testResetBufferAndStemAnotherWord() {
PorterStemmer stemmer = new PorterStemmer();
stemmer.add('t');
stemmer.add('e');
stemmer.add('s');
stemmer.add('t');
stemmer.stem();
stemmer.reset();
stemmer.add('m');
stemmer.add('a');
stemmer.add('t');
stemmer.add('t');
stemmer.add('i');
stemmer.add('n');
stemmer.add('g');
boolean changed = stemmer.stem();
assertTrue(changed);
assertEquals("mat", stemmer.toString());
}

@Test
public void testStemEmptyString() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("", stemmer.stem(""));
}

@Test
public void testStemNonAlphabeticInput() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("123", stemmer.stem("123"));
PorterStemmer stemmer2 = new PorterStemmer();
assertEquals("#$%", stemmer2.stem("#$%"));
}

@Test
public void testStemAllVowelsNoChange() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("aeiou", stemmer.stem("aeiou"));
}

@Test
public void testStemAllConsonantsNoChange() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("bcdfgh", stemmer.stem("bcdfgh"));
}

@Test
public void testStemFinalEHandling() {
PorterStemmer stemmer1 = new PorterStemmer();
assertEquals("creat", stemmer1.stem("create"));
PorterStemmer stemmer2 = new PorterStemmer();
assertEquals("creativ", stemmer2.stem("creative"));
}

@Test
public void testStemCVCandDoubleConsonantUnderStep1Rules() {
PorterStemmer stemmer1 = new PorterStemmer();
assertEquals("hop", stemmer1.stem("hopping"));
PorterStemmer stemmer2 = new PorterStemmer();
assertEquals("hope", stemmer2.stem("hoping"));
PorterStemmer stemmer3 = new PorterStemmer();
assertEquals("fat", stemmer3.stem("fattening"));
}

@Test
public void testStemWithLargeInputBuffer() {
PorterStemmer stemmer = new PorterStemmer();
stemmer.add('a');
stemmer.add('a');
stemmer.add('a');
stemmer.add('a');
stemmer.add('a');
stemmer.add('a');
stemmer.add('a');
stemmer.add('a');
stemmer.add('a');
stemmer.add('a');
stemmer.add('a');
stemmer.add('a');
stemmer.add('a');
stemmer.add('a');
stemmer.add('a');
stemmer.add('a');
stemmer.add('a');
stemmer.add('a');
stemmer.add('a');
stemmer.add('a');
stemmer.add('a');
stemmer.add('a');
stemmer.add('a');
stemmer.add('a');
stemmer.add('a');
boolean changed = stemmer.stem();
assertFalse(changed);
}

@Test
public void testStemOnlyS() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("s", stemmer.stem("s"));
}

@Test
public void testStemSuffixDoesNotMatchEndsCheck() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("guards", stemmer.stem("guards"));
}

@Test
public void testStep1EedSuffixButNoMGT0() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("feed", stemmer.stem("feed"));
}

@Test
public void testStemWordEndingWithIESButDoubleS() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("miss", stemmer.stem("missies"));
}

@Test
public void testDoubleConsonantAtEndHandledProperly() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("control", stemmer.stem("controlling"));
}

@Test
public void testCVCRejectWordsEndingInWXY() {
PorterStemmer stemmer1 = new PorterStemmer();
assertEquals("hopp", stemmer1.stem("hopped"));
PorterStemmer stemmer2 = new PorterStemmer();
assertEquals("snow", stemmer2.stem("snowed"));
PorterStemmer stemmer3 = new PorterStemmer();
assertEquals("tray", stemmer3.stem("trayed"));
}

@Test
public void testStemWordsEndingWithYWithoutOtherVowels() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("sky", stemmer.stem("sky"));
}

@Test
public void testStemWordsEndingWithYWithVowelInStem() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("cri", stemmer.stem("cry"));
}

@Test
public void testStem_ReturnsSameIfNoChangeAfterStem() {
PorterStemmer stemmer = new PorterStemmer();
char[] word = new char[] { 'r', 'u', 'n' };
boolean changed = stemmer.stem(word);
assertFalse(changed);
assertEquals("run", stemmer.toString());
}

@Test
public void testStemEmptyCharArray() {
PorterStemmer stemmer = new PorterStemmer();
char[] empty = new char[0];
boolean changed = stemmer.stem(empty);
assertFalse(changed);
assertEquals("", stemmer.toString());
}

@Test
public void testStemCharArraySmallerThanInternalBuffer() {
PorterStemmer stemmer = new PorterStemmer();
char[] word = new char[] { 'h', 'o', 'p', 'e' };
boolean changed = stemmer.stem(word, 4);
assertTrue(changed);
assertEquals("hope", stemmer.toString());
}

@Test
public void testStemCharArrayWithZerosAndOffset() {
PorterStemmer stemmer = new PorterStemmer();
char[] padded = new char[10];
padded[3] = 'z';
padded[4] = 'o';
padded[5] = 'n';
padded[6] = 'e';
boolean changed = stemmer.stem(padded, 3, 4);
assertTrue(changed);
assertEquals("zone", stemmer.toString());
}

@Test
public void testStemUnchangedBufferLengthMatchesStringOutput() {
PorterStemmer stemmer = new PorterStemmer();
String result = stemmer.stem("run");
assertEquals(stemmer.getResultLength(), result.length());
}

@Test
public void testStemWordEndingInIonButNoSTPrefix() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("opinion", stemmer.stem("opinion"));
}

@Test
public void testStemBug1AndBug2DefensiveLogicWithAedIon() {
PorterStemmer stemmer1 = new PorterStemmer();
assertEquals("aed", stemmer1.stem("aed"));
PorterStemmer stemmer2 = new PorterStemmer();
assertEquals("ion", stemmer2.stem("ion"));
}

@Test
public void testStep4ActiveSuffixNotMatchingCriteria() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("executive", stemmer.stem("executive"));
}

@Test
public void testStemRemovesFinalELongWordConditionally() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("reviv", stemmer.stem("revive"));
}

@Test
public void testStemRemoveELLInDoubleLCondition() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("control", stemmer.stem("controlled"));
}

@Test
public void testStemLiteralEndsWithSSES() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("caress", stemmer.stem("caresses"));
}

@Test
public void testStemLiteralEndsWithEEDAndMZero() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("eed", stemmer.stem("eed"));
}

@Test
public void testStemWithOnlyY() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("y", stemmer.stem("y"));
}

@Test
public void testStemWithBufferMutationAcrossCalls() {
PorterStemmer first = new PorterStemmer();
assertEquals("agree", first.stem("agreed"));
PorterStemmer second = new PorterStemmer();
second.add('c');
second.add('a');
second.add('t');
second.stem();
assertEquals("cat", second.toString());
}

@Test
public void testStemEndsWithSButNotPlural() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("gas", stemmer.stem("gas"));
}

@Test
public void testStemEndsWithIESNoStemWhenSingular() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("dies", stemmer.stem("dies"));
}

@Test
public void testStemEndsWithATMapsToATE() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("conflate", stemmer.stem("conflated"));
}

@Test
public void testStemEndsWithBLMapsToBLE() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("trouble", stemmer.stem("troubled"));
}

@Test
public void testStemEndsWithIZMapsToIZE() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("analyz", stemmer.stem("analyzing"));
}

@Test
public void testStemDoubleCWithLZExcluded() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("fizz", stemmer.stem("fizzed"));
}

@Test
public void testStep3WithZeroLengthStemReturnsEarly() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("oed", stemmer.stem("oed"));
}

@Test
public void testStemILogicalSuffixRemoveIC() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("critic", stemmer.stem("criticism"));
}

@Test
public void testStemAlitiBecomesAl() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("formal", stemmer.stem("formality"));
}

@Test
public void testStemIvitiBecomesIve() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("active", stemmer.stem("activity"));
}

@Test
public void testStemBilitiBecomesBle() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("responsible", stemmer.stem("responsibility"));
}

@Test
public void testStemOUSNESSBecomesOUS() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("curious", stemmer.stem("curiousness"));
}

@Test
public void testStemLOGIBecomesLOG() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("catalog", stemmer.stem("cataloging"));
}

@Test
public void testStep4EdgeSuffixNessRemoval() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("dark", stemmer.stem("darkness"));
}

@Test
public void testStemEndsIONWithoutSTPrefix() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("motion", stemmer.stem("motion"));
}

@Test
public void testStemEndsIONWithSPrefix() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("discuss", stemmer.stem("discussion"));
}

@Test
public void testStemEndsIONWithTPrefix() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("act", stemmer.stem("action"));
}

@Test
public void testStemEndsWithEInShortWord() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("kite", stemmer.stem("kite"));
}

@Test
public void testStemEndsWithLWithDoubleLCheckFailsMCondition() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("skill", stemmer.stem("skill"));
}

@Test
public void testStemEndsWithLWithDoubleLRemoved() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("control", stemmer.stem("controlled"));
}

@Test
public void testStemDoubleCIsFalseIfFirstNotConsonant() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("seeing", stemmer.stem("seeing"));
}

@Test
public void testStemAppliesAllStepsSequentially() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("gener", stemmer.stem("generalization"));
}

@Test
public void testStemCharArrayLargerThanInputImmediateBufferExpansion() {
PorterStemmer stemmer = new PorterStemmer();
char[] word = new char[] { 's', 'e', 'n', 's', 'a', 't', 'i', 'o', 'n' };
boolean changed = stemmer.stem(word, 0, 9);
assertTrue(changed);
assertEquals("sensat", stemmer.toString());
}

@Test
public void testWordEndingWithSSNotDoubleC() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("kiss", stemmer.stem("kisses"));
}

@Test
public void testWordEndingInEdNoVowelInStem() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("bled", stemmer.stem("bled"));
}

@Test
public void testWordEndingInIngNoVowelInStem() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("thing", stemmer.stem("thing"));
}

@Test
public void testWordEndingInEdWithCVCBecomesE() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("hop", stemmer.stem("hopped"));
}

@Test
public void testStemEndsWithYNoVowelInStemYNotConverted() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("sky", stemmer.stem("sky"));
}

@Test
public void testStemYBecomesIWithVowelInStem() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("happi", stemmer.stem("happy"));
}

@Test
public void testStemNoChangeForShortConsonantOnlyWord() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("b", stemmer.stem("b"));
}

@Test
public void testStemSuffixRemovalFailsBecauseMNotGreaterThanZero() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("izer", stemmer.stem("izer"));
}

@Test
public void testStemStep3EndsWithTionalBecomesTion() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("option", stemmer.stem("optional"));
}

@Test
public void testStep4EndsWithFulRemovesSuffix() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("use", stemmer.stem("useful"));
}

@Test
public void testStep4EndsWithNessRemovesSuffix() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("dark", stemmer.stem("darkness"));
}

@Test
public void testStep5RemovesEnt() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("differ", stemmer.stem("different"));
}

@Test
public void testStep5ConditionIonWithNoSPrefix() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("onion", stemmer.stem("onion"));
}

@Test
public void testStep5ConditionIonWithSPrefix() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("discuss", stemmer.stem("discussion"));
}

@Test
public void testStep6MEqualsOneAndCVCPatternDoesNotRemoveE() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("code", stemmer.stem("code"));
}

@Test
public void testStep6RemovesTrailingEWhenMGreaterThanOne() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("make", stemmer.stem("make"));
}

@Test
public void testRepeatedStemCallsWithReset() {
PorterStemmer stemmer = new PorterStemmer();
stemmer.add('s');
stemmer.add('k');
stemmer.add('i');
stemmer.add('e');
stemmer.add('s');
stemmer.stem();
assertEquals("ski", stemmer.toString());
stemmer.reset();
stemmer.add('b');
stemmer.add('o');
stemmer.add('x');
stemmer.add('e');
stemmer.add('s');
stemmer.stem();
assertEquals("box", stemmer.toString());
}

@Test
public void testEmptyCharSequence() {
PorterStemmer stemmer = new PorterStemmer();
CharSequence input = new StringBuilder("");
CharSequence result = stemmer.stem(input);
assertEquals("", result.toString());
}

@Test
public void testStemKnownNoChangeButStillDirtyBuffer() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("orbit", stemmer.stem("orbit"));
}

@Test
public void testStemAlreadyReducedRootForm() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("decide", stemmer.stem("decide"));
}

@Test
public void testStemShortWordLessThan3Chars() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("go", stemmer.stem("go"));
}

@Test
public void testGetResultBufferIsCorrectAfterStemming() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("create", stemmer.stem("created"));
char[] buffer = stemmer.getResultBuffer();
assertEquals('c', buffer[0]);
assertEquals('r', buffer[1]);
assertEquals('e', buffer[2]);
assertEquals('a', buffer[3]);
assertEquals('t', buffer[4]);
assertEquals('e', buffer[5]);
}

@Test
public void testStemInputWithOnlyWhitespaceCharacters() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("   ", stemmer.stem("   "));
}

@Test
public void testStemWordWithNonLetterCharacters() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("test123", stemmer.stem("test123"));
}

@Test
public void testStemWordWithLeadingAndTrailingSpecialChars() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("-develop-", stemmer.stem("-develop-"));
}

@Test
public void testStemLongConstantWordNoVowels() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("rhythms", stemmer.stem("rhythms"));
}

@Test
public void testStemWordEndsWithSButDoesNotMatchPluralRules() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("bus", stemmer.stem("bus"));
}

@Test
public void testStemEndsWithIESButMEqualsZero() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("tries", stemmer.stem("tries"));
}

@Test
public void testEndsFailsDueToShorterStemThanSuffix() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("at", stemmer.stem("at"));
}

@Test
public void testStemWordEndsEedWithMEqualsZero() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("feed", stemmer.stem("feed"));
}

@Test
public void testStemWordEndsWithEdButNoVowelInStem() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("bled", stemmer.stem("bled"));
}

@Test
public void testStemCVCFailsDueToFinalLetterBeingWXY() {
PorterStemmer stemmerX = new PorterStemmer();
assertEquals("tray", stemmerX.stem("traying"));
PorterStemmer stemmerW = new PorterStemmer();
assertEquals("snow", stemmerW.stem("snowing"));
PorterStemmer stemmerZ = new PorterStemmer();
assertEquals("buzz", stemmerZ.stem("buzzing"));
}

@Test
public void testStep3DoesNotApplyIfKEqualsK0() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("a", stemmer.stem("a"));
}

@Test
public void testStep5EndsWithIONButNoSOrTPrefix() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("champion", stemmer.stem("champion"));
}

@Test
public void testStep5EndsWithIONWithSPrefix() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("confess", stemmer.stem("confession"));
}

@Test
public void testStep5EndsWithIONWithTPrefix() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("protect", stemmer.stem("protection"));
}

@Test
public void testStemBufferExpansionOnAddBeyondDefault() {
PorterStemmer stemmer = new PorterStemmer();
stemmer.add('p');
stemmer.add('r');
stemmer.add('e');
stemmer.add('d');
stemmer.add('i');
stemmer.add('s');
stemmer.add('p');
stemmer.add('o');
stemmer.add('s');
stemmer.add('i');
stemmer.add('t');
stemmer.add('i');
stemmer.add('o');
stemmer.add('n');
stemmer.add('s');
boolean changed = stemmer.stem();
assertTrue(changed);
assertEquals("predisposit", stemmer.toString());
}

@Test
public void testStemCharArrayWithOffsetOutsideInternalBufferLength() {
PorterStemmer stemmer = new PorterStemmer();
char[] padded = new char[] { '_', '_', '_', 'c', 'r', 'e', 'a', 't', 'e', 'd' };
boolean changed = stemmer.stem(padded, 3, 7);
assertTrue(changed);
assertEquals("create", stemmer.toString());
}

@Test
public void testStemSuffixRuleWithShortInputThatLooksLikeSuffix() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("ed", stemmer.stem("ed"));
}

@Test
public void testStemWordWithNonAlphabeticCharacterSuffix() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("pass123", stemmer.stem("pass123"));
}

@Test
public void testStemReplacesYWithIInLargerWord() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("qualifi", stemmer.stem("qualify"));
}

@Test
public void testStemWordAlreadyInRootFormReturnsSame() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("run", stemmer.stem("run"));
}

@Test
public void testStemResultLengthMatchesReducedString() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("agree", stemmer.stem("agreed"));
int resultLength = stemmer.getResultLength();
String result = stemmer.toString();
assertEquals(result.length(), resultLength);
}

@Test
public void testStemWithLargeRandomWordWithNoRulesApplied() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("abcdefghijklmnpqrstuvwxyz", stemmer.stem("abcdefghijklmnpqrstuvwxyz"));
}

@Test
public void testStemCVCReturnsFalseForShortStemCVCFalse() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("mad", stemmer.stem("mad"));
}

@Test
public void testStep6FinalEConditionWithCVCTruePreventsEDeletion() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("hale", stemmer.stem("hale"));
}

@Test
public void testStep6FinalEConditionWithMOneCVCFalseRemovesE() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("rate", stemmer.stem("rate"));
}

@Test
public void testStemEndsWithEntSuffixButMIsZero() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("tent", stemmer.stem("tent"));
}

@Test
public void testStemEndsWithEntSuffixWithMEqualsTwo() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("differ", stemmer.stem("different"));
}

@Test
public void testStemWithPluralFormButNoChangeBecauseSIsPartOfStem() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("glass", stemmer.stem("glass"));
}

@Test
public void testStemEndsWithEdAndVowelInStemAppliesTransformation() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("aggravat", stemmer.stem("aggravated"));
}

@Test
public void testStemEndsWithIngAndNoVowelInStemSkipsTransformation() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("bring", stemmer.stem("bring"));
}

@Test
public void testStemEndsWithEEDAndMIsZeroNoChangeOccurs() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("reed", stemmer.stem("reed"));
}

@Test
public void testStemEndsWithEEDWithMGreaterThanZeroRemovesD() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("proceed", stemmer.stem("proceeded"));
}

@Test
public void testStemCvcRuleTrueAddsE() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("hop", stemmer.stem("hopping"));
}

@Test
public void testStemCvcRuleFalseDueToEndingInW() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("snow", stemmer.stem("snowing"));
}

@Test
public void testStemrWithMZeroPreventsReplacementInStep3() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("izer", stemmer.stem("izer"));
}

@Test
public void testStemDoesNotReplaceYWhenNoVowelInStem() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("fry", stemmer.stem("fry"));
}

@Test
public void testStemReplacesYWithIWhenVowelInStem() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("cried", stemmer.stem("crying"));
}

@Test
public void testStemEndsWithLogiIsTransformedCorrectly() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("analog", stemmer.stem("analogizing"));
}

@Test
public void testStemMEqualsOneFinalERemovalFailsOnCvc() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("love", stemmer.stem("love"));
}

@Test
public void testDoubleCConditionFalseWhenCharsDoNotMatch() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("commit", stemmer.stem("commits"));
}

@Test
public void testEndsWithSSESRemovesSES() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("dress", stemmer.stem("dresses"));
}

@Test
public void testEndsWithIESReplacedWithI() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("cry", stemmer.stem("cries"));
}

@Test
public void testStep4EndsWithATEReplacedCorrectly() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("relate", stemmer.stem("relatively"));
}

@Test
public void testStep3EndsWithATIONToATE() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("activate", stemmer.stem("activation"));
}

@Test
public void testResetClearsPreviousStateForNextStem() {
PorterStemmer stemmer = new PorterStemmer();
stemmer.add('p');
stemmer.add('l');
stemmer.add('a');
stemmer.add('y');
stemmer.add('e');
stemmer.add('d');
stemmer.stem();
assertEquals("play", stemmer.toString());
stemmer.reset();
stemmer.add('w');
stemmer.add('a');
stemmer.add('l');
stemmer.add('k');
stemmer.add('i');
stemmer.add('n');
stemmer.add('g');
stemmer.stem();
assertEquals("walk", stemmer.toString());
}

@Test
public void testStemSingleCharacterArray() {
PorterStemmer stemmer = new PorterStemmer();
char[] chars = new char[] { 'a' };
boolean changed = stemmer.stem(chars);
assertFalse(changed);
assertEquals("a", stemmer.toString());
}

@Test
public void testStemAppliesAllStepsOnComplexWord() {
PorterStemmer stemmer = new PorterStemmer();
char[] chars = "institutionalization".toCharArray();
boolean changed = stemmer.stem(chars);
assertTrue(changed);
assertEquals("institut", stemmer.toString());
}

@Test
public void testStemDuplicatesConsRemovedExceptLZS() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("fizz", stemmer.stem("fizzing"));
assertEquals("pass", stemmer.stem("passing"));
assertEquals("bell", stemmer.stem("belling"));
}

@Test
public void testStemTooShortToMatchAnyRule() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("a", stemmer.stem("a"));
}

@Test
public void testStemShortWordNotMatchingAnySuffix() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("it", stemmer.stem("it"));
}

@Test
public void testStemWithWhitespaceOnly() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("   ", stemmer.stem("   "));
}

@Test
public void testStemWithSymbolsAndDigits() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("run123", stemmer.stem("run123"));
}

@Test
public void testStemBufferUnderResizeThreshold() {
PorterStemmer stemmer = new PorterStemmer();
stemmer.add('p');
stemmer.add('e');
stemmer.add('e');
stemmer.add('d');
boolean changed = stemmer.stem();
assertTrue(changed);
assertEquals("pee", stemmer.toString());
}

@Test
public void testStemAddingCharactersThenReset() {
PorterStemmer stemmer = new PorterStemmer();
stemmer.add('h');
stemmer.add('o');
stemmer.add('p');
stemmer.add('p');
stemmer.add('e');
stemmer.stem();
assertEquals("hope", stemmer.toString());
stemmer.reset();
stemmer.add('s');
stemmer.add('u');
stemmer.add('r');
stemmer.add('e');
boolean changed = stemmer.stem();
assertFalse(changed);
assertEquals("sure", stemmer.toString());
}

@Test
public void testStemEEndingShortWordMEquals1CvcFalseKeepsE() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("code", stemmer.stem("code"));
}

@Test
public void testStemEEndingShortWordMEquals1CvcTrueRemovesE() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("hate", stemmer.stem("hate"));
}

@Test
public void testStemDoubleLRemovedWhenMAboveOne() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("control", stemmer.stem("controlled"));
}

@Test
public void testStemEndsWithEButMZeroKeepsE() {
PorterStemmer stemmer = new PorterStemmer();
stemmer.add('b');
stemmer.add('e');
boolean changed = stemmer.stem();
assertFalse(changed);
assertEquals("be", stemmer.toString());
}

@Test
public void testStemInputAlreadyStemmedValueReturnsFalse() {
PorterStemmer stemmer = new PorterStemmer();
char[] word = new char[] { 's', 'k', 'i' };
boolean changed = stemmer.stem(word);
assertFalse(changed);
assertEquals("ski", stemmer.toString());
}

@Test
public void testStemCharArrayWithOffsetCoversSubset() {
PorterStemmer stemmer = new PorterStemmer();
char[] buffer = new char[] { 'x', 'x', 'r', 'u', 'n' };
boolean changed = stemmer.stem(buffer, 2, 3);
assertFalse(changed);
assertEquals("run", stemmer.toString());
}

@Test
public void testStemCharArrayOffsetLargerThanInternalBufferForCopyResize() {
PorterStemmer stemmer = new PorterStemmer();
char[] input = new char[20];
input[5] = 'l';
input[6] = 'o';
input[7] = 'v';
input[8] = 'e';
input[9] = 'd';
boolean changed = stemmer.stem(input, 5, 5);
assertTrue(changed);
assertEquals("love", stemmer.toString());
}

@Test
public void testStep3EarlyReturnIfKEqualsK0() {
PorterStemmer stemmer = new PorterStemmer();
char[] input = "eed".toCharArray();
boolean changed = stemmer.stem(input, 0, input.length);
assertFalse(changed);
assertEquals("eed", stemmer.toString());
}

@Test
public void testStep5Bug2JLessThanZeroHandled() {
PorterStemmer stemmer = new PorterStemmer();
char[] input = "ion".toCharArray();
boolean changed = stemmer.stem(input, 0, 3);
assertFalse(changed);
assertEquals("ion", stemmer.toString());
}

@Test
public void testEndsMethodFailsDueToShortStem() {
PorterStemmer stemmer = new PorterStemmer();
char[] word = new char[] { 'a', 't' };
boolean changed = stemmer.stem(word);
assertFalse(changed);
assertEquals("at", stemmer.toString());
}

@Test
public void testStemMultipleSequentialSuffixTransformations() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("sensat", stemmer.stem("sensational"));
}

@Test
public void testStemToStringLengthMatchesResultLength() {
PorterStemmer stemmer = new PorterStemmer();
assertEquals("generate", stemmer.stem("generated"));
assertEquals(stemmer.toString().length(), stemmer.getResultLength());
}

@Test
public void testStemResultBufferReflectsStemmingOutput() {
PorterStemmer stemmer = new PorterStemmer();
stemmer.stem("university");
char[] result = stemmer.getResultBuffer();
assertEquals('u', result[0]);
assertEquals('n', result[1]);
assertEquals('i', result[2]);
}

@Test
public void testStemMultipleInputWithReuseWithoutResetYieldsWrongResult() {
PorterStemmer stemmer = new PorterStemmer();
stemmer.stem("creating");
stemmer.add('b');
stemmer.add('u');
stemmer.add('s');
boolean changed = stemmer.stem();
assertTrue(changed);
assertNotEquals("bus", stemmer.toString());
}
}
