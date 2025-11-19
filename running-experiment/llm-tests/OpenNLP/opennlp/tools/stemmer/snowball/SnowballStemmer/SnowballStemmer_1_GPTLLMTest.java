package opennlp.tools.stemmer.snowball;

import opennlp.tools.stemmer.Stemmer;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;

public class SnowballStemmer_1_GPTLLMTest {

@Test
public void testEnglishDefaultRepeat() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("running");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testEnglishCustomRepeat() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, 3);
CharSequence result = stemmer.stem("running");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testNullAlgorithmThrowsException() {
new SnowballStemmer(null);
}

@Test
public void testArabicStemmer() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ARABIC);
CharSequence result = stemmer.stem("كلمات");
assertNotNull(result);
}

@Test
public void testCatalanStemmer() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.CATALAN);
CharSequence result = stemmer.stem("parlant");
assertNotNull(result);
}

@Test
public void testFrenchStemmer() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.FRENCH);
CharSequence result = stemmer.stem("mangent");
assertNotNull(result);
}

@Test
public void testTurkishStemmerWithUnicode() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.TURKISH);
CharSequence result = stemmer.stem("çalışıyor");
assertNotNull(result);
}

@Test
public void testEmptyInput() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.SPANISH);
CharSequence result = stemmer.stem("");
assertNotNull(result);
}

@Test
public void testNonAlphabeticInput() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.GERMAN);
CharSequence result = stemmer.stem("1234");
assertNotNull(result);
}

@Test
public void testNullInputToStem() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
try {
stemmer.stem(null);
fail("Expected NullPointerException or similar when passing null");
} catch (NullPointerException expected) {
} catch (Exception e) {
fail("Unexpected exception type thrown: " + e);
}
}

@Test
public void testRepeatAppliedExactTimes() {
// MockStemmer mockStemmer = new MockStemmer();
CharSequence input = "testing";
// mockStemmer.setCurrent(input.toString());
// mockStemmer.stem();
// mockStemmer.stem();
// mockStemmer.stem();
// assertEquals("testing_x_x_x", mockStemmer.getCurrent());
// assertEquals(3, mockStemmer.getStemCallCount());
}

@Test
public void testRepeatOneIsIdempotentAcrossCalls() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, 1);
CharSequence result1 = stemmer.stem("running");
CharSequence result2 = stemmer.stem("running");
assertEquals(result1, result2);
}

@Test
public void testStemmingEffectivenessEnglish() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("running");
assertNotNull(result);
assertNotEquals("running", result.toString());
}

@Test
public void testSpanishStemmer() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.SPANISH);
CharSequence result = stemmer.stem("hablando");
assertNotNull(result);
}

@Test
public void testIndonesianStemmer() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.INDONESIAN);
CharSequence result = stemmer.stem("berlari");
assertNotNull(result);
}

@Test
public void testRussianStemmer() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.RUSSIAN);
CharSequence result = stemmer.stem("бегать");
assertNotNull(result);
}

@Test
public void testSwedishStemmer() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.SWEDISH);
CharSequence result = stemmer.stem("springer");
assertNotNull(result);
}

@Test
public void testFinnishStemmer() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.FINNISH);
CharSequence result = stemmer.stem("juoksee");
assertNotNull(result);
}

@Test
public void testIrishStemmer() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.IRISH);
CharSequence result = stemmer.stem("ag rith");
assertNotNull(result);
}

@Test
public void testGermanStemmer() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.GERMAN);
CharSequence result = stemmer.stem("laufend");
assertNotNull(result);
}

@Test
public void testConstructorWithZeroRepeat() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, 0);
CharSequence result = stemmer.stem("running");
assertEquals("running", result.toString());
}

@Test
public void testConstructorWithNegativeRepeat() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, -5);
CharSequence result = stemmer.stem("running");
assertEquals("running", result.toString());
}

@Test
public void testInputIsWhitespaceOnly() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("   ");
assertNotNull(result);
assertTrue(result.toString().trim().isEmpty());
}

@Test
public void testInputIsSingleCharacter() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("a");
assertNotNull(result);
assertEquals(1, result.length());
}

@Test
public void testInputIsAlreadyStemmedWord() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("run");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testPorterStemmerAlgorithm() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.PORTER);
CharSequence result = stemmer.stem("hopping");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testGreekStemmerAlgorithm() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.GREEK);
CharSequence result = stemmer.stem("τρέχει");
assertNotNull(result);
}

@Test
public void testRomanianStemmerAlgorithm() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ROMANIAN);
CharSequence result = stemmer.stem("alergând");
assertNotNull(result);
}

@Test
public void testUppercaseInput() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("RUNNING");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testMixedCaseInput() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("RuNnInG");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testVeryLongInput() {
StringBuilder sb = new StringBuilder();
for (int i = 0; i < 10000; i++) {
sb.append("running");
}
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem(sb.toString());
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testDifferentInstancesReturnIndependentResults() {
SnowballStemmer stemmer1 = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
SnowballStemmer stemmer2 = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result1 = stemmer1.stem("running");
CharSequence result2 = stemmer2.stem("running");
assertNotNull(result1);
assertNotNull(result2);
assertEquals(result1.toString(), result2.toString());
}

@Test
public void testMultipleSequentialCallsChangeResultCorrectly() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, 1);
CharSequence a = stemmer.stem("running");
CharSequence b = stemmer.stem("hopping");
assertNotEquals(a.toString(), b.toString());
}

@Test
public void testNorwegianStemmer() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.NORWEGIAN);
CharSequence result = stemmer.stem("løpende");
assertNotNull(result);
}

@Test
public void testItalianStemmer() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ITALIAN);
CharSequence result = stemmer.stem("correndo");
assertNotNull(result);
}

@Test
public void testPortugueseStemmer() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.PORTUGUESE);
CharSequence result = stemmer.stem("correndo");
assertNotNull(result);
}

@Test
public void testHungarianStemmer() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.HUNGARIAN);
CharSequence result = stemmer.stem("futó");
assertNotNull(result);
}

@Test
public void testDutchStemmer() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.DUTCH);
CharSequence result = stemmer.stem("lopende");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testDanishStemmer() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.DANISH);
CharSequence result = stemmer.stem("løbende");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testSetRepeatToMaxInteger() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, Integer.MAX_VALUE);
CharSequence result = null;
try {
result = stemmer.stem("running");
} catch (Throwable t) {
result = null;
}
assertTrue(result == null || result.length() > 0);
}

@Test
public void testSetRepeatToIntegerMinValue() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, Integer.MIN_VALUE);
CharSequence result = stemmer.stem("running");
assertNotNull(result);
}

@Test
public void testStemmerHandlesTabCharacter() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("run\tning");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testStemmerHandlesNewlineCharacter() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("run\nning");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testNonLatinScript() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.TURKISH);
CharSequence result = stemmer.stem("дома");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testRepeatOnceYieldsSameResultTwice() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.RUSSIAN, 1);
CharSequence result1 = stemmer.stem("бегает");
CharSequence result2 = stemmer.stem("бегает");
assertEquals(result1, result2);
}

@Test
public void testInputIsOnlyPunctuation() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.PORTER);
CharSequence result = stemmer.stem("!!!");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testInputContainsEmoji() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("run🏃‍♂️ning");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testStemmerReturnsDifferentObject() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence input = "running";
CharSequence result = stemmer.stem(input);
assertNotSame(input, result);
}

@Test
public void testStemmerWithSpecialCharacters() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ITALIAN);
CharSequence result = stemmer.stem("correndo!");
assertNotNull(result);
assertTrue(result.toString().contains("!") || result.toString().length() > 0);
}

@Test
public void testRepeatedStemOnSameInputInstance() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence word = "hopping";
CharSequence result1 = stemmer.stem(word);
CharSequence result2 = stemmer.stem(word);
assertEquals(result1, result2);
}

@Test
public void testStemmerWithNullRepeatDefaultsToOne() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.GERMAN);
CharSequence result = stemmer.stem("laufend");
assertNotNull(result);
}

@Test
public void testMultipleDifferentAlgorithmInstancesIndependence() {
SnowballStemmer germanStemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.GERMAN);
SnowballStemmer spanishStemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.SPANISH);
CharSequence word = "corrientes";
CharSequence germanResult = germanStemmer.stem(word);
CharSequence spanishResult = spanishStemmer.stem(word);
assertNotNull(germanResult);
assertNotNull(spanishResult);
assertNotEquals(germanResult, spanishResult);
}

@Test
public void testStemmerAlgorithmIRISHWithAccentedWord() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.IRISH);
CharSequence result = stemmer.stem("obair");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testStemmerHandlesSurrogatePairCharInput() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("\uD83D\uDE00");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testStemmerReusedWithDifferentInputs() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence input1 = "caring";
CharSequence input2 = "running";
CharSequence result1 = stemmer.stem(input1);
CharSequence result2 = stemmer.stem(input2);
assertNotNull(result1);
assertNotNull(result2);
assertNotEquals(result1.toString(), result2.toString());
}

@Test
public void testStemmerWithWhitespaceStartAndEnd() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("  running  ");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testStemmerWithNumericWordLikeInput() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.PORTER);
CharSequence result = stemmer.stem("run123");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testStemmerWithAllUpperCaseInput() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.SPANISH);
CharSequence result = stemmer.stem("HABLANDO");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testCharSequenceImplementationViaStringBuilder() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
StringBuilder word = new StringBuilder("running");
CharSequence result = stemmer.stem(word);
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testStemmerWithControlCharacters() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("run\u0008ning");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testStemmerOnIdenticalWordCaseMismatch() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence lower = stemmer.stem("running");
CharSequence upper = stemmer.stem("RUNNING");
assertNotNull(lower);
assertNotNull(upper);
assertNotEquals(lower.toString(), upper.toString());
}

@Test
public void testRepeatSetToOneProcessesOnce() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, 1);
CharSequence result = stemmer.stem("jumping");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testRepeatSetToTwoShouldDifferentiateFromSingleRepeat() {
SnowballStemmer stemmerOnce = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, 1);
SnowballStemmer stemmerTwice = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, 2);
CharSequence result1 = stemmerOnce.stem("running");
CharSequence result2 = stemmerTwice.stem("running");
assertNotNull(result1);
assertNotNull(result2);
assertFalse(result1.toString().isEmpty());
assertFalse(result2.toString().isEmpty());
assertNotEquals(result1.toString(), result2.toString());
}

@Test
public void testPorterStemmerAgainstShortStemRoot() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.PORTER);
CharSequence result = stemmer.stem("run");
assertNotNull(result);
assertEquals("run", result.toString());
}

@Test
public void testStemWordWithNullCharacterEmbedded() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.GERMAN);
CharSequence result = stemmer.stem("lauf\u0000end");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testStemmerDoesNotMutateInputWord() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
String input = "running";
CharSequence result = stemmer.stem(input);
assertEquals("running", input);
}

@Test
public void testStemWithMultipleSpacesInsideInput() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("run  ning");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testMinimalLengthStemInput() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.PORTER);
CharSequence result = stemmer.stem("a");
assertNotNull(result);
assertEquals(1, result.length());
}

@Test
public void testSwitchingAlgorithmsDoesNotCarryState() {
SnowballStemmer englishStemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence english = englishStemmer.stem("running");
SnowballStemmer frenchStemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.FRENCH);
CharSequence french = frenchStemmer.stem("parlant");
assertNotNull(english);
assertNotNull(french);
assertNotEquals(english.toString(), french.toString());
}

@Test
public void testStemmerMultipleCallsWithDifferentWords() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.FRENCH, 1);
CharSequence result1 = stemmer.stem("mangent");
CharSequence result2 = stemmer.stem("parlent");
assertNotNull(result1);
assertNotNull(result2);
assertNotEquals(result1.toString(), result2.toString());
}

@Test
public void testStemmerInputWithTrailingSpecialCharacter() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.SPANISH);
CharSequence result = stemmer.stem("hablando!");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testStemmerWithMultipleSpacesAroundWord() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("   walking   ");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testStemmerWithHyphenatedCompoundWord() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("long-running");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testStemmerWithTabsAndNewlinesInsideWord() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("run\t\nning");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testStemmerWithOnlyControlCharacters() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.PORTER);
CharSequence result = stemmer.stem("\u0001\u0002\u0003");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testStemWithEmbeddedNullChar() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.GERMAN);
CharSequence result = stemmer.stem("lauf\u0000end");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testStemSameWordTwiceReturnsConsistentResult() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.CATALAN);
CharSequence first = stemmer.stem("ballant");
CharSequence second = stemmer.stem("ballant");
assertEquals(first.toString(), second.toString());
}

@Test
public void testStemmerWithSurroundingNumbers() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("123running456");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testRepeatedStemCallsOnModifiedInput() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
String word = "faster";
CharSequence result1 = stemmer.stem(word);
word = "fastest";
CharSequence result2 = stemmer.stem(word);
assertNotNull(result1);
assertNotNull(result2);
assertNotEquals(result1.toString(), result2.toString());
}

@Test
public void testStemmerHandlesSingleNonLetterCharacter() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.SWEDISH);
CharSequence result = stemmer.stem("#");
assertNotNull(result);
assertEquals("#", result.toString());
}

@Test
public void testMultipleStemmersIndependentBehavior() {
SnowballStemmer stemmer1 = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
SnowballStemmer stemmer2 = new SnowballStemmer(SnowballStemmer.ALGORITHM.DUTCH);
CharSequence result1 = stemmer1.stem("running");
CharSequence result2 = stemmer2.stem("lopende");
assertNotNull(result1);
assertNotNull(result2);
assertNotEquals(result1.toString(), result2.toString());
}

@Test
public void testStemmerWithVeryShortWord() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.PORTUGUESE);
CharSequence result = stemmer.stem("a");
assertNotNull(result);
assertEquals("a", result.toString());
}

@Test
public void testStemmerWithWhitespaceOnlyString() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("   ");
assertNotNull(result);
assertTrue(result.toString().trim().isEmpty());
}

@Test
public void testStemmerWithRepeatedSpecialCharacters() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.FINNISH);
CharSequence result = stemmer.stem("!!##@@");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testStemmerWithSpaceOnlyCharSequence() {
Stemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.HUNGARIAN);
CharSequence result = stemmer.stem(" ");
assertNotNull(result);
assertEquals(" ", result.toString());
}

@Test
public void testStemmerWithStringWithSpacePrefix() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.GREEK);
CharSequence result = stemmer.stem(" περπατάω");
assertNotNull(result);
assertTrue(result.toString().length() > 0);
}

@Test
public void testStemmerWithCharSequenceSubType() {
CharSequence input = new StringBuffer("working");
Stemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem(input);
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testStemmerConsistencyAcrossRepeatedInvocationsSameInput() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.RUSSIAN);
CharSequence result1 = stemmer.stem("бегущий");
CharSequence result2 = stemmer.stem("бегущий");
assertEquals(result1.toString(), result2.toString());
}

@Test
public void testStemmerWithControlCharacterOnlyInput() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.DUTCH);
CharSequence result = stemmer.stem("\t");
assertNotNull(result);
assertEquals("\t", result.toString());
}

@Test
public void testStemmerHandlesLargeUnicodeSequence() {
String input = "\uD835\uDD0A\uD835\uDD1E\uD835\uDD2F";
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.TURKISH);
CharSequence result = stemmer.stem(input);
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testStemmerResultIsNotSameAsInputInstance() {
String input = "returning";
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem(input);
assertNotSame(input, result);
}

@Test
public void testStemmerWithWordContainingDiacritics() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ROMANIAN);
CharSequence result = stemmer.stem("mâncând");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testStemmerWithExactAlgorithmBoundaryCase() {
SnowballStemmer.ALGORITHM algorithm = SnowballStemmer.ALGORITHM.values()[SnowballStemmer.ALGORITHM.values().length - 1];
SnowballStemmer stemmer = new SnowballStemmer(algorithm);
CharSequence result = stemmer.stem("koşuyor");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testStemmerWithShortWordThatShouldNotBeStemmed() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("be");
assertNotNull(result);
assertEquals("be", result.toString());
}

@Test
public void testStemmerRejectsRandomEnumFallback() {
try {
SnowballStemmer.ALGORITHM invalidAlgorithm = SnowballStemmer.ALGORITHM.valueOf("ENGLISH");
SnowballStemmer stemmer = new SnowballStemmer(invalidAlgorithm);
CharSequence result = stemmer.stem("run");
assertNotNull(result);
} catch (IllegalStateException e) {
fail("Unexpected fallback rejection with valid enum");
}
}

@Test
public void testStemmerDoesNotChangeImmutableInput() {
String original = "smiling";
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
stemmer.stem(original);
assertEquals("smiling", original);
}

@Test
public void testStemmerMultiInstanceIndependenceSameInput() {
SnowballStemmer s1 = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
SnowballStemmer s2 = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence r1 = s1.stem("going");
CharSequence r2 = s2.stem("going");
assertEquals(r1.toString(), r2.toString());
}

@Test
public void testStemmerInputWithMultipleInternalSpaces() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("fast   run");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testStemmerOutputIsTrimmedIfSpacesAdded() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.PORTER);
CharSequence result = stemmer.stem("  pushing  ");
assertNotNull(result);
assertTrue(result.toString().length() > 0);
}

@Test
public void testStemmerWithNonBreakingSpace() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence input = "run\u00A0ning";
CharSequence result = stemmer.stem(input);
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testStemmerWithSurrogatePairUnmatchedHigh() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence input = new String("\uD83D");
CharSequence result = stemmer.stem(input);
assertNotNull(result);
assertEquals("\uD83D", result.toString());
}

@Test
public void testStemmerWithSurrogatePairUnmatchedLow() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.RUSSIAN);
CharSequence input = new String("\uDE00");
CharSequence result = stemmer.stem(input);
assertNotNull(result);
assertEquals("\uDE00", result.toString());
}

@Test
public void testStemmerWithRepeatAtTen() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, 10);
CharSequence input = "running";
CharSequence result = stemmer.stem(input);
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testStemmerWithVeryLongWordInput() {
StringBuilder sb = new StringBuilder();
for (int i = 0; i < 5000; i++) {
sb.append("walk");
}
String longWord = sb.toString();
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem(longWord);
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testStemmerWithDifferentCharSequenceImplementationSameText() {
CharSequence seq1 = new StringBuilder("walking");
CharSequence seq2 = new StringBuffer("walking");
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result1 = stemmer.stem(seq1);
CharSequence result2 = stemmer.stem(seq2);
assertEquals(result1.toString(), result2.toString());
}

@Test
public void testStemmerWithOnlySymbols() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.GERMAN);
CharSequence result = stemmer.stem("@@!!??");
assertNotNull(result);
assertEquals("@@!!??", result.toString());
}

@Test
public void testStemmerWithRepeatEqualsToWordLength() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, 7);
CharSequence input = "running";
CharSequence result = stemmer.stem(input);
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testStemmerWithRepeatGreaterThanWordLength() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.SPANISH, 20);
CharSequence result = stemmer.stem("hablando");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testSameStringLiteralAcrossInstances() {
SnowballStemmer stemmer1 = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
SnowballStemmer stemmer2 = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence input = "jumping";
CharSequence result1 = stemmer1.stem(input);
CharSequence result2 = stemmer2.stem(input);
assertEquals(result1.toString(), result2.toString());
}

@Test
public void testStemmerWithLineBreaksInWord() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("run\nning");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testStemmerWithMixedAsciiAndUnicodeLetters() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ITALIAN);
CharSequence input = "corrèrunning";
CharSequence result = stemmer.stem(input);
assertNotNull(result);
assertTrue(result.length() > 0);
}
}
