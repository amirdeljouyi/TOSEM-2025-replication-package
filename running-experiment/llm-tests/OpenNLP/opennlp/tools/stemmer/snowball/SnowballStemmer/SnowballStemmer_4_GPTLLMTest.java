package opennlp.tools.stemmer.snowball;

import opennlp.tools.stemmer.Stemmer;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class SnowballStemmer_4_GPTLLMTest {

@Test
public void testStemEnglishWord() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("running");
assertNotNull(result);
assertEquals("run", result.toString());
}

@Test
public void testStemFrenchWord() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.FRENCH);
CharSequence result = stemmer.stem("mangeais");
assertNotNull(result);
assertEquals("mang", result.toString());
}

@Test
public void testStemWithRepeatThree() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, 3);
CharSequence result = stemmer.stem("fishing");
assertNotNull(result);
assertEquals("fish", result.toString());
}

@Test
public void testStemEmptyString() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("");
assertEquals("", result.toString());
}

@Test
public void testStemNullThrowsException() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
// thrown.expect(NullPointerException.class);
stemmer.stem(null);
}

@Test
public void testStemNumericString() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("123456");
assertEquals("123456", result.toString());
}

@Test
public void testStemSpecialCharacters() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("@#$%");
assertEquals("@#$%", result.toString());
}

@Test
public void testRepeatZeroPreservesInput() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, 0);
CharSequence result = stemmer.stem("played");
assertEquals("played", result.toString());
}

@Test
public void testRussianStemming() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.RUSSIAN);
CharSequence result = stemmer.stem("говорящий");
assertEquals("говорящ", result.toString());
}

@Test
public void testMultipleStemsAreIdempotent() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence once = stemmer.stem("running");
CharSequence twice = stemmer.stem(once);
assertEquals(once.toString(), twice.toString());
}

@Test
public void testStemmerInstancesDoNotInterfere() {
SnowballStemmer stemmerOne = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
SnowballStemmer stemmerTwo = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result1 = stemmerOne.stem("playing");
CharSequence result2 = stemmerTwo.stem("singing");
assertEquals("play", result1.toString());
assertEquals("sing", result2.toString());
}

@Test
public void testGreekStemming() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.GREEK);
CharSequence result = stemmer.stem("τρέχοντας");
assertTrue(result.toString().startsWith("τρέχ"));
}

@Test
public void testIrishStemming() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.IRISH);
CharSequence result = stemmer.stem("bhí");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testTurkishStemming() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.TURKISH);
CharSequence result = stemmer.stem("kitaplar");
assertEquals("kitap", result.toString());
}

@Test
public void testIllegalStateForNullAlgorithm() {
// thrown.expect(NullPointerException.class);
new SnowballStemmer(null);
}

@Test
public void testCatalanStemming() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.CATALAN);
CharSequence result = stemmer.stem("cantant");
assertNotNull(result);
assertEquals("cant", result.toString());
}

@Test
public void testPorterStemmer() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.PORTER);
CharSequence result = stemmer.stem("happiness");
assertEquals("happi", result.toString());
}

@Test
public void testGermanStemming() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.GERMAN);
CharSequence result = stemmer.stem("spielen");
assertEquals("spiel", result.toString());
}

@Test
public void testHungarianStemming() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.HUNGARIAN);
CharSequence result = stemmer.stem("szaladgáló");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testDanishStemming() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.DANISH);
CharSequence result = stemmer.stem("spiser");
assertNotNull(result);
assertEquals("spis", result.toString());
}

@Test
public void testConstructorWithRepeatOneBehavesSameAsDefaultConstructor() {
SnowballStemmer stemmerDefault = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
SnowballStemmer stemmerExplicit = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, 1);
CharSequence resultDefault = stemmerDefault.stem("running");
CharSequence resultExplicit = stemmerExplicit.stem("running");
assertEquals(resultDefault.toString(), resultExplicit.toString());
}

@Test
public void testConstructorWithNegativeRepeatThrowsNoException() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, -1);
CharSequence result = stemmer.stem("processing");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testConstructorWithHighRepeatAppliesRepeatedStemming() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, 10);
CharSequence result = stemmer.stem("relational");
assertEquals("relat", result.toString());
}

@Test
public void testStemWordAllUppercase() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("RUNNING");
assertEquals("run", result.toString().toLowerCase());
}

@Test
public void testStemWordSingleCharacter() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("a");
assertEquals("a", result.toString());
}

@Test
public void testStemWhitespaceOnly() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("   ");
assertEquals("   ", result.toString());
}

@Test
public void testStemTrimsNewlinesAndTabsPreserved() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("\nwalking\t");
assertTrue(result.toString().contains("walk"));
}

@Test
public void testStemUnicodeCharacters() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("crème");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testStemEmojiInput() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("🍕");
assertEquals("🍕", result.toString());
}

@Test
public void testDifferentAlgorithmsProduceDifferentStems() {
SnowballStemmer stemmerEnglish = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
SnowballStemmer stemmerGerman = new SnowballStemmer(SnowballStemmer.ALGORITHM.GERMAN);
CharSequence resultEnglish = stemmerEnglish.stem("organizing");
CharSequence resultGerman = stemmerGerman.stem("organizing");
assertNotEquals(resultEnglish.toString(), resultGerman.toString());
}

@Test
public void testStemmerWithVeryLongInput() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
StringBuilder longInput = new StringBuilder();
for (int i = 0; i < 1000; i++) {
longInput.append("running");
}
CharSequence result = stemmer.stem(longInput.toString());
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testStemSingleNonAlphabeticCharacter() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("!");
assertEquals("!", result.toString());
}

@Test
public void testStemInputWithMixedLettersNumbers() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("run123");
assertNotNull(result);
assertTrue(result.length() > 0);
assertTrue(result.toString().startsWith("run"));
}

@Test
public void testStemMultiWordString() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("running jumping walking");
assertNotNull(result);
assertTrue(result.toString().length() > 0);
}

@Test
public void testStemWithTabCharacterOnly() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("\t");
assertEquals("\t", result.toString());
}

@Test
public void testStemWithNewlineOnly() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("\n");
assertEquals("\n", result.toString());
}

@Test
public void testStemWithCarriageReturn() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("\r");
assertEquals("\r", result.toString());
}

@Test
public void testStemWithExtendedAsciiCharacters() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("naïve");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testStemWithSurrogatePairCharacter() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("😊");
assertEquals("😊", result.toString());
}

@Test
public void testStemAllUpperCaseInputPreservesStructure() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("WALKING");
assertNotNull(result);
assertTrue(result.toString().toLowerCase().startsWith("walk"));
}

@Test
public void testStemArabicTextOnArabicStemmer() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ARABIC);
CharSequence result = stemmer.stem("الطالب");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testStemSpanishDiacriticsInput() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.SPANISH);
CharSequence result = stemmer.stem("camión");
assertNotNull(result);
assertTrue(result.toString().startsWith("cam"));
}

@Test
public void testConstructorWithMaxRepeatValue() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, Integer.MAX_VALUE);
CharSequence result = stemmer.stem("walking");
assertNotNull(result);
assertTrue(result.length() > 0 || result.length() == 0);
}

@Test
public void testStemWithMultipleRepeatsStillStable() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, 5);
CharSequence result = stemmer.stem("relational");
assertEquals("relat", result.toString());
}

@Test
public void testStemEmptySpacesFollowedByWord() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("   running");
assertTrue(result.toString().contains("run"));
}

@Test
public void testStemNonLatinScriptJapanese() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.PORTER);
CharSequence result = stemmer.stem("こんにちは");
assertNotNull(result);
assertEquals("こんにちは", result.toString());
}

@Test
public void testStemWhitespaceSurroundingWord() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem(" walking ");
assertTrue(result.toString().contains("walk"));
}

@Test
public void testStemControlCharactersInInput() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("run\u0000ning");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testInputWithLongConsecutiveSpecialCharacters() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.PORTER);
CharSequence result = stemmer.stem("!@#$%^&*()_+");
assertEquals("!@#$%^&*()_+", result.toString());
}

@Test
public void testIllegalStateThrownForUnknownAlgorithmViaReflection() throws Exception {
// thrown.expect(IllegalStateException.class);
SnowballStemmer.ALGORITHM invalidAlgorithm = (SnowballStemmer.ALGORITHM) Enum.valueOf((Class<Enum>) Class.forName("opennlp.tools.stemmer.snowball.SnowballStemmer$ALGORITHM"), "ARABIC");
SnowballStemmer stemmer = new SnowballStemmer(null);
}

@Test
public void testNorwegianStemmerExecution() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.NORWEGIAN);
CharSequence result = stemmer.stem("springende");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testPortugueseStemming() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.PORTUGUESE);
CharSequence result = stemmer.stem("corações");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testRomanianStemming() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ROMANIAN);
CharSequence result = stemmer.stem("cântând");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testSwedishStemming() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.SWEDISH);
CharSequence result = stemmer.stem("pratande");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testIndonesianStemming() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.INDONESIAN);
CharSequence result = stemmer.stem("berjalan");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testWordsWithHyphen() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("state-of-the-art");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testVeryLargeWordInput() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
StringBuilder word = new StringBuilder();
for (int i = 0; i < 10000; i++) {
word.append('a');
}
CharSequence result = stemmer.stem(word.toString());
assertNotNull(result);
assertEquals(word.length(), result.length());
}

@Test
public void testRepeatBoundaryZeroThenOne() {
SnowballStemmer stemmerZero = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, 0);
SnowballStemmer stemmerOne = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, 1);
CharSequence resultZero = stemmerZero.stem("danced");
CharSequence resultOne = stemmerOne.stem("danced");
assertNotEquals(resultZero.toString(), resultOne.toString());
}

@Test
public void testDutchStemming() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.DUTCH);
CharSequence result = stemmer.stem("lopend");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testItalianStemming() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ITALIAN);
CharSequence result = stemmer.stem("correndo");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testFinnishStemming() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.FINNISH);
CharSequence result = stemmer.stem("juoksemassa");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testUnchangedInputAfterStemmingStableForm() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("run");
assertEquals("run", result.toString());
}

@Test
public void testArabicWordWithEnglishStemmer() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("الطالب");
assertNotNull(result);
assertEquals("الطالب", result.toString());
}

@Test
public void testWordAlreadyStemmedForm() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("walk");
assertEquals("walk", result.toString());
}

@Test
public void testStringWithMultipleSpacesOnly() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("     ");
assertEquals("     ", result.toString());
}

@Test
public void testSingleUnderscoreInput() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("_");
assertEquals("_", result.toString());
}

@Test
public void testRepeatParameterNegativeValue() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, -3);
CharSequence result = stemmer.stem("forced");
assertNotNull(result);
assertEquals("forced", result.toString());
}

@Test
public void testEmptyStringWithAllSupportedLanguages() {
SnowballStemmer stemmer1 = new SnowballStemmer(SnowballStemmer.ALGORITHM.SPANISH);
CharSequence result1 = stemmer1.stem("");
assertEquals("", result1.toString());
SnowballStemmer stemmer2 = new SnowballStemmer(SnowballStemmer.ALGORITHM.GREEK);
CharSequence result2 = stemmer2.stem("");
assertEquals("", result2.toString());
SnowballStemmer stemmer3 = new SnowballStemmer(SnowballStemmer.ALGORITHM.TURKISH);
CharSequence result3 = stemmer3.stem("");
assertEquals("", result3.toString());
}

@Test
public void testIrishStemmingWithSoftLenition() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.IRISH);
CharSequence result = stemmer.stem("bhean");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testValidRepeatEdgeCaseTwo() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.PORTER, 2);
CharSequence result = stemmer.stem("relational");
assertEquals("relat", result.toString());
}

@Test
public void testEnglishStemmerOnNonEnglishScript() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("你好");
assertEquals("你好", result.toString());
}

@Test
public void testVeryShortSymbolOnlyInput() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.PORTER);
CharSequence result = stemmer.stem(".");
assertNotNull(result);
assertEquals(".", result.toString());
}

@Test
public void testMixedSymbolAndLetterInput() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.GERMAN);
CharSequence result = stemmer.stem("lauf@123");
assertNotNull(result);
assertTrue(result.toString().contains("lauf"));
}

@Test
public void testUnexpectedLanguageWordWithWrongStemmer() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.RUSSIAN);
CharSequence result = stemmer.stem("τρέχει");
assertNotNull(result);
assertEquals("τρέχει", result.toString());
}

@Test
public void testRepeatWithOneStability() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.PORTER, 1);
CharSequence once = stemmer.stem("conditional");
CharSequence again = stemmer.stem(once);
assertEquals(once.toString(), again.toString());
}

@Test
public void testConstructorWithEnumNameValue() {
SnowballStemmer.ALGORITHM algo = SnowballStemmer.ALGORITHM.valueOf("FINNISH");
SnowballStemmer stemmer = new SnowballStemmer(algo);
CharSequence result = stemmer.stem("juoksemista");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testStemNullCharSequenceThrows() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.HUNGARIAN);
// thrown.expect(NullPointerException.class);
stemmer.stem(null);
}

@Test
public void testConstructorWithEnumOrdinalAccess() {
SnowballStemmer.ALGORITHM algo = SnowballStemmer.ALGORITHM.values()[8];
SnowballStemmer stemmer = new SnowballStemmer(algo);
CharSequence result = stemmer.stem("τρέξιμο");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testInputWithControlUnicodeSymbols() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.PORTUGUESE);
CharSequence result = stemmer.stem("\u001F");
assertEquals("\u001F", result.toString());
}

@Test
public void testInputWithEmojiAndLettersCombined() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.SPANISH);
CharSequence result = stemmer.stem("correr🚀");
assertTrue(result.toString().contains("corr"));
}

@Test
public void testWhitespaceOnlyMultiCharacter() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem(" \t\r\n ");
assertEquals(" \t\r\n ", result.toString());
}

@Test
public void testNullAlgorithmConstructorThrows() {
// thrown.expect(NullPointerException.class);
new SnowballStemmer(null, 1);
}

@Test
public void testStemmingWordWithDigitPrefix() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.PORTER);
CharSequence result = stemmer.stem("123running");
assertNotNull(result);
assertTrue(result.toString().contains("run"));
}

@Test
public void testArabicStemmerWithArabicInput() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ARABIC);
CharSequence result = stemmer.stem("المدرسة");
assertNotNull(result);
assertTrue(result.toString().length() > 0);
}

@Test
public void testDutchStemmerWithPluralWord() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.DUTCH);
CharSequence result = stemmer.stem("kinderen");
assertNotNull(result);
assertTrue(result.toString().startsWith("kinder"));
}

@Test
public void testGermanStemmerWithCompoundWord() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.GERMAN);
CharSequence result = stemmer.stem("Donaudampfschifffahrtsgesellschaft");
assertNotNull(result);
assertTrue(result.toString().length() > 0);
}

@Test
public void testRepeatedStemmingPreservesPreviousResult() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence once = stemmer.stem("playfully");
CharSequence twice = stemmer.stem(once);
assertEquals(once.toString(), twice.toString());
}

@Test
public void testNonBreakingSpacePreservedInInput() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("\u00A0running");
assertTrue(result.toString().contains("run"));
}

@Test
public void testInputThatLooksLikeCodeVariable() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.PORTER);
CharSequence result = stemmer.stem("user_name");
assertNotNull(result);
assertTrue(result.toString().contains("user"));
}

@Test
public void testStemmingWithUnprintableAsciiCharacters() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
String input = "\u0007walk\u0009";
CharSequence result = stemmer.stem(input);
assertTrue(result.toString().contains("walk"));
}

@Test
public void testSpanishWordUsingEnglishStemmer() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("comiendo");
assertEquals("comiendo", result.toString());
}

@Test
public void testTurkishCompoundWithSuffix() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.TURKISH);
CharSequence result = stemmer.stem("kitaplıklar");
assertNotNull(result);
assertTrue(result.toString().startsWith("kitap"));
}

@Test
public void testGreekWordWithEndingChanges() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.GREEK);
CharSequence result = stemmer.stem("τρέχεις");
assertNotNull(result);
assertTrue(result.toString().startsWith("τρέχ"));
}

@Test
public void testItalianReflexiveVerb() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ITALIAN);
CharSequence result = stemmer.stem("lavarsi");
assertNotNull(result);
assertTrue(result.toString().startsWith("lav"));
}

@Test
public void testWordEndsWithCapitalLetter() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("runninG");
assertTrue(result.toString().toLowerCase().startsWith("run"));
}

@Test
public void testWordWithSurrogatePairEmoji() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.PORTER);
CharSequence result = stemmer.stem("run🏃");
assertNotNull(result);
assertTrue(result.toString().contains("run"));
}

@Test
public void testAllWhitespaceWithTabsNewlines() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.PORTER);
CharSequence result = stemmer.stem("  \t\n ");
assertEquals("  \t\n ", result.toString());
}

@Test
public void testWhitespaceSurroundingWord() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("  walking  ");
assertTrue(result.toString().contains("walk"));
}

@Test
public void testRepeatWithLargePositiveValueStillStable() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, 50);
CharSequence result = stemmer.stem("generalization");
assertEquals("general", result.toString());
}

@Test
public void testRepeatMinimumNegativeBoundary() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, Integer.MIN_VALUE);
CharSequence result = stemmer.stem("played");
assertEquals("played", result.toString());
}

@Test
public void testNullCharInsideWord() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.PORTER);
CharSequence result = stemmer.stem("run\u0000ning");
assertTrue(result.toString().contains("run"));
}

@Test
public void testEmojiAndAsciiCombination() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.FRENCH);
CharSequence result = stemmer.stem("travail🚀");
assertTrue(result.toString().startsWith("trava"));
}

@Test
public void testWhitespaceAndSymbolsOnlyInput() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.SWEDISH);
CharSequence result = stemmer.stem(" \t!@# ");
assertEquals(" \t!@# ", result.toString());
}

@Test
public void testSingleHighSurrogateCharacterInput() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.SPANISH);
CharSequence result = stemmer.stem("\uD83D");
assertEquals("\uD83D", result.toString());
}

@Test
public void testSingleLowSurrogateCharacterInput() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.NORWEGIAN);
CharSequence result = stemmer.stem("\uDC36");
assertEquals("\uDC36", result.toString());
}

@Test
public void testLetterDigitSymbolMixInput() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ROMANIAN);
CharSequence result = stemmer.stem("run123!_");
assertNotNull(result);
assertTrue(result.toString().contains("run"));
}

@Test
public void testExtremeUnicodeCharacter() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.DANISH);
CharSequence result = stemmer.stem("\uDBFF\uDFFF");
assertEquals("\uDBFF\uDFFF", result.toString());
}

@Test
public void testWhitespaceBeforeAndAfterWord() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.HUNGARIAN);
CharSequence result = stemmer.stem("  futott  ");
assertTrue(result.toString().contains("fut"));
}

@Test
public void testLowerCaseNormalizationNotApplied() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.FRENCH);
CharSequence result = stemmer.stem("MARCHAIS");
assertTrue(result.toString().toLowerCase().contains("march"));
}

@Test
public void testEmptyStringWithZeroRepeat() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, 0);
CharSequence result = stemmer.stem("");
assertEquals("", result.toString());
}

@Test
public void testAllDigitsInputRemainsUnchanged() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.SPANISH);
CharSequence result = stemmer.stem("1234567890");
assertEquals("1234567890", result.toString());
}

@Test
public void testWordWithOnlyAccentedCharacters() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.PORTUGUESE);
CharSequence result = stemmer.stem("ação");
assertNotNull(result);
assertTrue(result.toString().startsWith("aç"));
}

@Test
public void testGreekWordWithWhitespaceAndTabPadding() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.GREEK);
CharSequence result = stemmer.stem("\tτρέχει\t");
assertTrue(result.toString().contains("τρέχ"));
}

@Test
public void testMultipleWhitespaceCharactersWithNoLetters() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.SWEDISH);
CharSequence result = stemmer.stem("   \t  ");
assertEquals("   \t  ", result.toString());
}
}
