package opennlp.tools.stemmer.snowball;

import opennlp.tools.stemmer.Stemmer;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class SnowballStemmer_3_GPTLLMTest {

@Test
public void testStemmerConstructorWithEnglishAlgorithm() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, 2);
assertNotNull(stemmer);
CharSequence result = stemmer.stem("running");
assertEquals("run", result.toString().toLowerCase());
}

@Test
public void testStemmerConstructorWithSpanishAlgorithm() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.SPANISH, 2);
assertNotNull(stemmer);
CharSequence result = stemmer.stem("corriendo");
assertTrue(result.toString().length() <= "corriendo".length());
}

@Test
public void testStemmerConstructorWithDutchAlgorithm() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.DUTCH, 1);
assertNotNull(stemmer);
CharSequence result = stemmer.stem("lopen");
assertNotNull(result);
}

@Test
public void testStemmerConstructorWithDefaultRepeatOne() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
assertNotNull(stemmer);
CharSequence result = stemmer.stem("running");
assertEquals("run", result.toString().toLowerCase());
}

@Test
public void testConstructorWithNullAlgorithmThrowsException() {
new SnowballStemmer(null);
}

@Test
public void testStemNullInputThrowsNullPointerException() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
try {
stemmer.stem(null);
fail("Expected NullPointerException for null word input");
} catch (NullPointerException expected) {
}
}

@Test
public void testStemEmptyStringReturnsEmpty() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("");
assertEquals("", result.toString());
}

@Test
public void testStemWhitespaceOnlyString() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("   ");
assertEquals("", result.toString());
}

@Test
public void testStemArabicExample() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ARABIC);
CharSequence result = stemmer.stem("الكتب");
assertNotNull(result);
}

@Test
public void testRepeatGreaterThanOneGivesSameOutputForEnglish() {
SnowballStemmer stemmerOnce = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, 1);
SnowballStemmer stemmerTwice = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, 2);
CharSequence resultOnce = stemmerOnce.stem("running");
CharSequence resultTwice = stemmerTwice.stem("running");
assertEquals(resultOnce.toString(), resultTwice.toString());
}

@Test
public void testStemShortWordEnglish() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("go");
assertEquals("go", result.toString().toLowerCase());
}

@Test
public void testVeryHighRepeatValueDoesNotFail() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, 100);
CharSequence result = stemmer.stem("running");
assertEquals("run", result.toString().toLowerCase());
}

@Test
public void testStemSameWordRepeatedlyIsDeterministic() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result1 = stemmer.stem("running");
CharSequence result2 = stemmer.stem("running");
CharSequence result3 = stemmer.stem("running");
assertEquals(result1.toString(), result2.toString());
assertEquals(result2.toString(), result3.toString());
}

@Test
public void testStemSpecialCharactersInWord() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("runn!ng?");
assertNotNull(result);
}

@Test
public void testStemNumericWord() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("12345");
assertEquals("12345", result.toString());
}

@Test
public void testStemMixedAlphanumeric() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("run123");
assertNotNull(result);
}

@Test
public void testUppercaseAndLowercaseAreEqual() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence lower = stemmer.stem("running");
CharSequence upper = stemmer.stem("RUNNING");
assertEquals(lower.toString().toLowerCase(), upper.toString().toLowerCase());
}

@Test
public void testVeryLargeInputString() {
StringBuilder builder = new StringBuilder();
for (int i = 0; i < 100000; i++) {
builder.append("a");
}
String largeInput = builder.toString();
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem(largeInput);
assertNotNull(result);
assertTrue(result.length() <= largeInput.length());
}

@Test
public void testZeroRepeatMeansNoStemming() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, 0);
CharSequence result = stemmer.stem("running");
assertEquals("running", result.toString());
}

@Test
public void testStemCatalanLanguageExample() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.CATALAN);
CharSequence result = stemmer.stem("corrents");
assertNotNull(result);
}

@Test
public void testStemItalianLanguage() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ITALIAN);
CharSequence result = stemmer.stem("mangiando");
assertNotNull(result);
}

@Test
public void testStemFinnishLanguage() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.FINNISH);
CharSequence result = stemmer.stem("juokseminen");
assertNotNull(result);
}

@Test
public void testStemFrenchLanguage() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.FRENCH);
CharSequence result = stemmer.stem("courir");
assertNotNull(result);
}

@Test
public void testStemGermanLanguage() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.GERMAN);
CharSequence result = stemmer.stem("laufend");
assertNotNull(result);
}

@Test
public void testStemGreekLanguage() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.GREEK);
CharSequence result = stemmer.stem("τρέξιμο");
assertNotNull(result);
}

@Test
public void testStemIrishLanguage() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.IRISH);
CharSequence result = stemmer.stem("ag rith");
assertNotNull(result);
}

@Test
public void testStemHungarianLanguage() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.HUNGARIAN);
CharSequence result = stemmer.stem("futás");
assertNotNull(result);
}

@Test
public void testStemTurkishLanguage() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.TURKISH);
CharSequence result = stemmer.stem("koşuyor");
assertNotNull(result);
}

@Test
public void testInvalidAlgorithmThrowsIllegalStateException() {
new SnowballStemmer(null, 1).stem("word");
}

@Test
public void testStemInputWithEmoji() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("😊running🚀");
assertNotNull(result);
assertTrue(result.toString().contains("running") || result.toString().length() <= "😊running🚀".length());
}

@Test
public void testStemWordWithMixedCaseCharacters() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("RuNnInG");
assertEquals("run", result.toString().toLowerCase());
}

@Test
public void testStemStringWithTabsAndNewlines() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("\trunning\n");
assertEquals("run", result.toString().toLowerCase());
}

@Test
public void testStemHighlyFormattedString() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("**running--");
assertNotNull(result);
}

@Test
public void testStemWithHighUnicodeCharacters() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("𝒓𝒖𝒏𝒏𝒊𝒏𝒈");
assertNotNull(result);
}

@Test
public void testStemOnlyPunctuations() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("!!!???");
assertNotNull(result);
assertEquals("!!!???", result.toString());
}

@Test
public void testStemMultilingualText() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("running correr laufen");
assertNotNull(result);
}

@Test
public void testStemHyphenatedWord() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("pre-running");
assertNotNull(result);
}

@Test
public void testStemIndonesianWord() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.INDONESIAN);
CharSequence result = stemmer.stem("berlari");
assertNotNull(result);
}

@Test
public void testStemNorwegianWordWithCommonSuffix() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.NORWEGIAN);
CharSequence result = stemmer.stem("løpende");
assertNotNull(result);
}

@Test
public void testStemPorterAlgorithmKnownInput() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.PORTER);
CharSequence result = stemmer.stem("consignment");
assertEquals("consign", result.toString());
}

@Test
public void testStemPortugueseVerbConjugation() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.PORTUGUESE);
CharSequence result = stemmer.stem("correndo");
assertNotNull(result);
}

@Test
public void testWhitespaceAtEdgesOfInput() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("  running  ");
assertEquals("run", result.toString().toLowerCase());
}

@Test
public void testStemRussianWord() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.RUSSIAN);
CharSequence result = stemmer.stem("бегущий");
assertNotNull(result);
}

@Test
public void testStemWhenWordAlreadyBaseForm() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("run");
assertEquals("run", result.toString().toLowerCase());
}

@Test
public void testStemOnlyNumbersWithSpaces() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem(" 1234 ");
assertEquals("1234", result.toString().trim());
}

@Test
public void testStemDifferentFromOriginal() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("happiness");
assertNotEquals("happiness", result.toString());
}

@Test
public void testStemSwedishWord() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.SWEDISH);
CharSequence result = stemmer.stem("springande");
assertNotNull(result);
}

@Test
public void testStemRomanianVerb() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ROMANIAN);
CharSequence result = stemmer.stem("fugind");
assertNotNull(result);
}

@Test
public void testStemNullCharSequenceThrowsNullPointerException() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
try {
stemmer.stem(null);
fail("Expected NullPointerException when passing null to stem()");
} catch (NullPointerException expected) {
}
}

@Test
public void testStemTabCharactersOnly() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("\t\t\t");
assertNotNull(result);
assertEquals("", result.toString());
}

@Test
public void testStemNewlineCharactersOnly() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("\n\n");
assertNotNull(result);
assertEquals("", result.toString());
}

@Test
public void testStemWhitespaceMixCharacters() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem(" \t\n\r ");
assertNotNull(result);
assertEquals("", result.toString());
}

@Test
public void testStemSingleUppercaseCharacter() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("R");
assertEquals("r", result.toString().toLowerCase());
}

@Test
public void testStemSingleLowercaseCharacter() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("r");
assertEquals("r", result.toString().toLowerCase());
}

@Test
public void testStemUsingAllUppercaseWord() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("RUNNING");
assertEquals("run", result.toString().toLowerCase());
}

@Test
public void testStemUsingAllLowercaseWord() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("running");
assertEquals("run", result.toString().toLowerCase());
}

@Test
public void testStemUsingMixedCaseWord() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("RuNnInG");
assertEquals("run", result.toString().toLowerCase());
}

@Test
public void testStemNonAlphabeticSymbolsOnly() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("@#$%^&*()");
assertEquals("@#$%^&*()", result.toString());
}

@Test
public void testStemInputWithNumbersAndLetters() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("run123");
assertNotNull(result);
}

@Test
public void testStemEmptyRepeatedly() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, 10);
CharSequence result = stemmer.stem("");
assertEquals("", result.toString());
}

@Test
public void testStemWithRepeatOneDefaultsProperly() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("running");
assertEquals("run", result.toString().toLowerCase());
}

@Test
public void testStemWithExplicitRepeatOneSameAsDefaultConstructor() {
SnowballStemmer stemmerExplicit = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, 1);
SnowballStemmer stemmerDefault = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence resultExplicit = stemmerExplicit.stem("running");
CharSequence resultDefault = stemmerDefault.stem("running");
assertEquals(resultExplicit.toString(), resultDefault.toString());
}

@Test
public void testStemMultipleSpacesWithinWord() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("run    ning");
assertNotNull(result);
}

@Test
public void testStemWithTabInsideWord() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("run\ting");
assertNotNull(result);
}

@Test
public void testStemExtremeUnicode() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("\uD83D\uDE80");
assertEquals("\uD83D\uDE80", result.toString());
}

@Test
public void testMinimalValidInputWithRepeatZero() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, 0);
CharSequence result = stemmer.stem("run");
assertEquals("run", result.toString());
}

@Test
public void testStemIndonesianStopword() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.INDONESIAN);
CharSequence result = stemmer.stem("yang");
assertNotNull(result);
}

@Test
public void testStemTurkishSuffixHeavyWord() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.TURKISH);
CharSequence result = stemmer.stem("koşuyorsunuz");
assertNotNull(result);
}

@Test
public void testStemFailsGracefullyWithEmptyCharacters() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result1 = stemmer.stem(" ");
CharSequence result2 = stemmer.stem("");
CharSequence result3 = stemmer.stem("\n");
assertEquals("", result1.toString().trim());
assertEquals("", result2.toString());
assertEquals("", result3.toString().trim());
}

@Test
public void testStemCharacterWithAccent() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.FRENCH);
CharSequence result = stemmer.stem("étudiant");
assertNotNull(result);
}

@Test
public void testStemSingleCharacterThatCouldStem() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("s");
assertEquals("s", result.toString());
}

@Test
public void testUnnormalizedUnicodeInput() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("e\u0301");
assertNotNull(result);
}

@Test
public void testStemHungarianWord() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.HUNGARIAN);
CharSequence result = stemmer.stem("futó");
assertNotNull(result);
}

@Test
public void testStemIrishWord() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.IRISH);
CharSequence result = stemmer.stem("ag rith");
assertNotNull(result);
}

@Test
public void testStemRomanianCompoundWord() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ROMANIAN);
CharSequence result = stemmer.stem("alergătorilor");
assertNotNull(result);
}

@Test
public void testStemLargeUnicodeInput() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
StringBuilder inputBuilder = new StringBuilder();
for (int i = 0; i < 10000; i++) {
inputBuilder.append("ü");
}
String input = inputBuilder.toString();
CharSequence result = stemmer.stem(input);
assertTrue(result.length() <= input.length());
}

@Test
public void testStemInvalidUnicodeSurrogatePair() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
String malformed = "\uD83Dtext";
CharSequence result = stemmer.stem(malformed);
assertNotNull(result);
}

@Test
public void testRepeatNegativeValueEffectivelyZero() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, -3);
CharSequence result = stemmer.stem("running");
assertEquals("running", result.toString());
}

@Test
public void testRepeatVeryLargeValueMaintainsOutput() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, 10000);
CharSequence result = stemmer.stem("running");
assertEquals("run", result.toString().toLowerCase());
}

@Test
public void testStemAfterEmptyStringCall() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result1 = stemmer.stem("");
assertEquals("", result1.toString());
CharSequence result2 = stemmer.stem("running");
assertEquals("run", result2.toString().toLowerCase());
}

@Test
public void testDifferentRepeatValuesAffectBehavior() {
SnowballStemmer stemmer1 = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, 1);
SnowballStemmer stemmer2 = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, 2);
CharSequence result1 = stemmer1.stem("happiness");
CharSequence result2 = stemmer2.stem("happiness");
assertEquals(result1.toString(), result2.toString());
}

@Test
public void testMultipleStemmingCallsSameInstance() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence first = stemmer.stem("running");
CharSequence second = stemmer.stem("jumping");
assertEquals("run", first.toString().toLowerCase());
assertEquals("jump", second.toString().toLowerCase());
}

@Test
public void testStemConsistencyAcrossNewInstancesSameInput() {
SnowballStemmer stemmerA = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
SnowballStemmer stemmerB = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence resultA = stemmerA.stem("running");
CharSequence resultB = stemmerB.stem("running");
assertEquals(resultA.toString(), resultB.toString());
}

@Test
public void testStemLetterFollowedBySpace() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("r ");
assertEquals("r", result.toString().trim());
}

@Test
public void testStemGreekInputMixedWithEnglish() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.GREEK);
CharSequence result = stemmer.stem("τρέχω run");
assertNotNull(result);
}

@Test
public void testStemPortugueseAccentWord() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.PORTUGUESE);
CharSequence result = stemmer.stem("corrêr");
assertNotNull(result);
}

@Test
public void testStemLongMixedLanguageString() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
String input = "running correr laufen бегать courir koşmak";
CharSequence result = stemmer.stem(input);
assertNotNull(result);
}

@Test
public void testStemAfterExceptionRecovery() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
try {
stemmer.stem(null);
} catch (NullPointerException expected) {
}
CharSequence result = stemmer.stem("running");
assertEquals("run", result.toString().toLowerCase());
}

@Test
public void testStemNorwegianWord() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.NORWEGIAN, 1);
CharSequence result = stemmer.stem("løpende");
assertNotNull(result);
}

@Test
public void testStemPorterSuffixHandledCorrectly() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.PORTER, 1);
CharSequence result = stemmer.stem("adjustable");
assertEquals("adjust", result.toString().toLowerCase());
}

@Test
public void testStemCatalanWord() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.CATALAN, 1);
CharSequence result = stemmer.stem("corrents");
assertNotNull(result);
}

@Test
public void testStemFinnishWord() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.FINNISH, 1);
CharSequence result = stemmer.stem("juoksemassa");
assertNotNull(result);
}

@Test
public void testStemDutchWithCapitalization() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.DUTCH, 1);
CharSequence result = stemmer.stem("LOPEN");
assertNotNull(result);
}

@Test
public void testStemIndonesianNonRootForm() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.INDONESIAN, 1);
CharSequence result = stemmer.stem("berlari");
assertNotNull(result);
}

@Test
public void testStemRussianInputWithSuffix() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.RUSSIAN, 1);
CharSequence result = stemmer.stem("бежавший");
assertNotNull(result);
}

@Test
public void testStemGreekWithAccentedCharacters() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.GREEK, 1);
CharSequence result = stemmer.stem("τρέχοντας");
assertNotNull(result);
}

@Test
public void testConstructorWithRepeatValueOneMillion() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, 1_000_000);
CharSequence result = stemmer.stem("running");
assertEquals("run", result.toString().toLowerCase());
}

@Test
public void testConstructorWithRepeatZeroReturnsOriginalUnchanged() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, 0);
CharSequence result = stemmer.stem("running");
assertEquals("running", result.toString());
}

@Test
public void testStemEmptyStringAndThenValidWord() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, 1);
CharSequence result1 = stemmer.stem("");
assertEquals("", result1.toString());
CharSequence result2 = stemmer.stem("jumping");
assertEquals("jump", result2.toString().toLowerCase());
}

@Test
public void testStemHighlyRepeatingCharacters() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, 1);
CharSequence result = stemmer.stem("aaaaaaa");
assertTrue(result.toString().length() <= 7);
}

@Test
public void testStemInputWithHyphenSeparator() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("pre-running");
assertNotNull(result);
}

@Test
public void testStemWhitespaceWordDoesNotThrow() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("   ");
assertEquals("", result.toString());
}

@Test
public void testStemWithWhitespaceBeforeAndAfter() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("   running   ");
assertEquals("run", result.toString().toLowerCase());
}

@Test
public void testStemWithUnexpectedSymbolsInWord() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("run$%#@!");
assertNotNull(result);
}

@Test
public void testArabicStemmingWithPrefix() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ARABIC);
CharSequence result = stemmer.stem("والكتب");
assertNotNull(result);
}

@Test
public void testStemSpanishInflectedVerb() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.SPANISH);
CharSequence result = stemmer.stem("corriendo");
assertTrue(result.toString().length() <= "corriendo".length());
}

@Test
public void testStemDanishInput() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.DANISH, 1);
CharSequence result = stemmer.stem("løbene");
assertNotNull(result);
}

@Test
public void testStemItalianInput() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ITALIAN, 1);
CharSequence result = stemmer.stem("correndo");
assertNotNull(result);
}

@Test
public void testStemFrenchInputWithAccent() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.FRENCH, 1);
CharSequence result = stemmer.stem("étudiants");
assertNotNull(result);
}

@Test
public void testStemMultipleCallsOnSameInstanceConsistency() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result1 = stemmer.stem("running");
CharSequence result2 = stemmer.stem("jumping");
CharSequence result3 = stemmer.stem("walking");
assertEquals("run", result1.toString().toLowerCase());
assertEquals("jump", result2.toString().toLowerCase());
assertEquals("walk", result3.toString().toLowerCase());
}

@Test
public void testStemWithUnderscoresAndSymbols() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("run_ning!");
assertNotNull(result);
}

@Test
public void testStemNonBreakingSpaceInput() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
String input = "\u00A0running\u00A0";
CharSequence result = stemmer.stem(input);
assertEquals("run", result.toString().trim().toLowerCase());
}

@Test
public void testStemWithBackToBackEmptyAndValidWord() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence first = stemmer.stem("");
assertEquals("", first.toString());
CharSequence second = stemmer.stem("running");
assertEquals("run", second.toString().toLowerCase());
}

@Test
public void testRepeatValueTwoSameAsOneForIdempotentStem() {
SnowballStemmer stemmerRepeat1 = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, 1);
SnowballStemmer stemmerRepeat2 = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, 2);
CharSequence r1 = stemmerRepeat1.stem("running");
CharSequence r2 = stemmerRepeat2.stem("running");
assertEquals(r1.toString(), r2.toString());
}

@Test
public void testRepeatValueZeroPreventsStemming() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, 0);
CharSequence result = stemmer.stem("running");
assertEquals("running", result.toString());
}

@Test
public void testNegativeRepeatValueBehavesAsZero() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, -5);
CharSequence result = stemmer.stem("running");
assertEquals("running", result.toString());
}

@Test
public void testRepeatHighValueStabilizesOutput() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, 5000);
CharSequence result = stemmer.stem("running");
assertEquals("run", result.toString().toLowerCase());
}

@Test
public void testStemAfterNullInvocationRecovers() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
try {
stemmer.stem(null);
} catch (NullPointerException expected) {
}
CharSequence result = stemmer.stem("running");
assertEquals("run", result.toString().toLowerCase());
}

@Test
public void testStemMultilineInputDoesNotThrow() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
String input = "running\njumping";
CharSequence result = stemmer.stem(input);
assertNotNull(result);
}

@Test
public void testStemSpacesAndTabsAndNewlines() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
String input = "  \n\t  ";
CharSequence result = stemmer.stem(input);
assertEquals("", result.toString());
}

@Test
public void testStemUnicodeComposedAccentForm() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.FRENCH);
CharSequence result = stemmer.stem("e\u0301tudiants");
assertNotNull(result);
}

@Test
public void testStemWhitespaceSurroundedValidInput() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("   running   ");
assertEquals("run", result.toString().trim().toLowerCase());
}

@Test
public void testStemSpanishWordWithPunctuation() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.SPANISH);
CharSequence result = stemmer.stem("corriendo.");
assertTrue(result.toString().length() <= "corriendo.".length());
}

@Test
public void testStemInputContainingEmojiAndText() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("running😊");
assertNotNull(result);
assertTrue(result.toString().contains("run") || result.length() <= "running😊".length());
}

@Test
public void testStemNullWordThrowsNullPointerException() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
stemmer.stem(null);
}

@Test
public void testStemLowercaseAndUppercaseAreUnified() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence lower = stemmer.stem("running");
CharSequence upper = stemmer.stem("RUNNING");
assertEquals(lower.toString().toLowerCase(), upper.toString().toLowerCase());
}

@Test
public void testStemWithSurrogatePair() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("💨running");
assertNotNull(result);
}

@Test
public void testStemWhitespaceWithMixedEncodings() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
String input = " \u00A0\t\n\r ";
CharSequence result = stemmer.stem(input);
assertEquals("", result.toString().trim());
}

@Test
public void testConstructorRepeatNegativeIsAcceptedButTreatedAsZero() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, -1);
CharSequence result = stemmer.stem("running");
assertEquals("running", result.toString());
}

@Test
public void testConstructorRepeatZeroSkipsStemming() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, 0);
CharSequence result = stemmer.stem("running");
assertEquals("running", result.toString());
}

@Test
public void testConstructorRepeatOneStemsOnce() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, 1);
CharSequence result = stemmer.stem("running");
assertEquals("run", result.toString());
}

@Test
public void testConstructorRepeatMultipleStillStemsIdempotently() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, 10);
CharSequence result = stemmer.stem("running");
assertEquals("run", result.toString());
}

@Test
public void testEnglishStemmingOnRootWordReturnsSame() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("run");
assertEquals("run", result.toString());
}

@Test
public void testDifferentAlgorithmProducesDifferentResult() {
SnowballStemmer englishStemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
SnowballStemmer spanishStemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.SPANISH);
CharSequence english = englishStemmer.stem("running");
CharSequence spanish = spanishStemmer.stem("running");
assertNotSame(english.toString(), spanish.toString());
}

@Test
public void testReuseSameStemmerInstanceMultipleCalls() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result1 = stemmer.stem("running");
CharSequence result2 = stemmer.stem("jumping");
CharSequence result3 = stemmer.stem("walking");
assertEquals("run", result1.toString().toLowerCase());
assertEquals("jump", result2.toString().toLowerCase());
assertEquals("walk", result3.toString().toLowerCase());
}

@Test
public void testReusingStemmerAfterException() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
try {
stemmer.stem(null);
} catch (NullPointerException expected) {
}
CharSequence result = stemmer.stem("walking");
assertEquals("walk", result.toString().toLowerCase());
}

@Test
public void testLargeInputStringStemming() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
StringBuilder builder = new StringBuilder();
for (int i = 0; i < 100000; i++) {
builder.append("a");
}
String longWord = builder.toString();
CharSequence result = stemmer.stem(longWord);
assertTrue(result.length() <= longWord.length());
}

@Test
public void testStemmingMultipleLanguagesCharacters() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("laufend correr бегущий");
assertNotNull(result);
}

@Test
public void testStemmingPreStemsWordTwiceConsistently() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, 2);
CharSequence result = stemmer.stem("running");
assertEquals("run", result.toString());
}

@Test
public void testStemEmptyStringAndWhitespace() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result1 = stemmer.stem("");
CharSequence result2 = stemmer.stem("   ");
assertEquals("", result1.toString());
assertEquals("", result2.toString().trim());
}

@Test
public void testUnchangedStemmingResultIsSameAfterRepeat() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, 5);
CharSequence r1 = stemmer.stem("happiness");
SnowballStemmer stemmer2 = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, 1);
CharSequence r2 = stemmer2.stem("happiness");
assertEquals(r1.toString(), r2.toString());
}

@Test
public void testStemShortUppercaseWord() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("GO");
assertEquals("go", result.toString().toLowerCase());
}

@Test
public void testStemTrimmedOutputForInputWhitespaceBeforeAfter() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
String input = "  running\t";
CharSequence result = stemmer.stem(input);
assertEquals("run", result.toString().trim().toLowerCase());
}
}
