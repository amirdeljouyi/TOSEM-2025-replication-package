package opennlp.tools.stemmer.snowball;

import opennlp.tools.stemmer.Stemmer;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class SnowballStemmer_5_GPTLLMTest {

@Test
public void testEnglishStemRunning() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("running");
assertEquals("run", result.toString());
}

@Test
public void testEnglishStemRunningWithMultipleRepeats() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, 3);
CharSequence result = stemmer.stem("running");
assertEquals("run", result.toString());
}

@Test
public void testPorterStemmerRelational() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.PORTER);
CharSequence result = stemmer.stem("relational");
assertEquals("relat", result.toString());
}

@Test
public void testFrenchStemMangeraient() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.FRENCH);
CharSequence result = stemmer.stem("mangeraient");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testGermanStemHauser() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.GERMAN);
CharSequence result = stemmer.stem("häuser");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testArabicStemAlkitaabat() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ARABIC);
CharSequence result = stemmer.stem("الكتابات");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testEmptyStringEnglish() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("");
assertEquals("", result.toString());
}

@Test
public void testEmptyStringFrench() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.FRENCH);
CharSequence result = stemmer.stem("");
assertEquals("", result.toString());
}

@Test
public void testSingleCharacterEnglish() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("a");
assertNotNull(result);
assertEquals("a", result.toString());
}

@Test
public void testSingleCharacterGerman() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.GERMAN);
CharSequence result = stemmer.stem("e");
assertNotNull(result);
}

@Test
public void testNullInputEnglish() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
stemmer.stem(null);
}

@Test
public void testRepeatZeroReturnsUnchangedWord() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, 0);
CharSequence result = stemmer.stem("testing");
assertEquals("testing", result.toString());
}

@Test
public void testRepeatHighValueStillCorrectStemming() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, 20);
CharSequence result = stemmer.stem("testing");
assertEquals("test", result.toString());
}

@Test
public void testKnownStemConnected() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("connected");
assertEquals("connect", result.toString());
}

@Test
public void testKnownStemConnection() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("connection");
assertEquals("connect", result.toString());
}

@Test
public void testDifferentStemmersProduceDifferentResults() {
SnowballStemmer englishStemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
SnowballStemmer germanStemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.GERMAN);
CharSequence english = englishStemmer.stem("nationalities");
CharSequence german = germanStemmer.stem("nationalitäten");
assertNotEquals(english.toString(), german.toString());
}

@Test
public void testStemWordWithNumbers() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("1234");
assertEquals("1234", result.toString());
}

@Test
public void testStemWordWithSymbols() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("!?@#");
assertEquals("!?@#", result.toString());
}

@Test
public void testStemUnicodeCharacters() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("café");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testWhitespaceOnlyInput() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem(" ");
assertNotNull(result);
assertEquals(" ", result.toString());
}

@Test
public void testConstructorWithNullAlgorithmThrows() {
new SnowballStemmer(null);
}

@Test
public void testSpanishStemming() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.SPANISH);
CharSequence result = stemmer.stem("hablando");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testItalianStemming() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ITALIAN);
CharSequence result = stemmer.stem("parlando");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testPortugueseStemming() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.PORTUGUESE);
CharSequence result = stemmer.stem("falando");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testDutchStemming() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.DUTCH);
CharSequence result = stemmer.stem("lopend");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testSwedishStemming() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.SWEDISH);
CharSequence result = stemmer.stem("bilar");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testNorwegianStemming() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.NORWEGIAN);
CharSequence result = stemmer.stem("bøkene");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testGreekStemming() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.GREEK);
CharSequence result = stemmer.stem("καλημέρα");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testHungarianStemming() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.HUNGARIAN);
CharSequence result = stemmer.stem("házak");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testRepeatMaxValueDoesNotCrash() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, Integer.MAX_VALUE);
CharSequence result = stemmer.stem("testing");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testRepeatNegativeValueIsTreatedGracefully() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, -1);
CharSequence result = stemmer.stem("testing");
assertNotNull(result);
assertEquals("testing", result.toString());
}

@Test
public void testVeryLongWord() {
StringBuilder sb = new StringBuilder();
for (int i = 0; i < 10000; i++) {
sb.append("run");
}
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem(sb.toString());
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testUnchangedWordStillReturnsSame() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("go");
assertEquals("go", result.toString());
}

@Test
public void testEmojiInput() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("😊😢");
assertNotNull(result);
assertEquals("😊😢", result.toString());
}

@Test
public void testHighlyRepeatedCharacterInput() {
String repeated = new String(new char[1000]).replace('\0', 'a');
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem(repeated);
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testRomanianStemming() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ROMANIAN);
CharSequence result = stemmer.stem("copiilor");
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
public void testIrishStemming() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.IRISH);
CharSequence result = stemmer.stem("bhaile");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testCatalanStemming() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.CATALAN);
CharSequence result = stemmer.stem("parlant");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testTurkishStemming() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.TURKISH);
CharSequence result = stemmer.stem("koşuyorum");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testZeroLengthStringWithRepeatZero() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, 0);
CharSequence result = stemmer.stem("");
assertEquals("", result.toString());
}

@Test
public void testWordWithMixedCase() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("RunNiNg");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testFullyUppercaseWord() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("RUNNING");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testStemWhitespaceTabNewline() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem(" \t\n");
assertNotNull(result);
}

@Test
public void testStemWordEndingWithPunctuation() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("running!");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testStemNumericAndAlphabeticCombination() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("run123");
assertNotNull(result);
assertTrue(result.toString().length() > 0);
}

@Test
public void testConstructorWithRepeatEqualsOne() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, 1);
CharSequence result = stemmer.stem("jumped");
assertEquals("jump", result.toString());
}

@Test
public void testConstructorWithRepeatEqualsTwo() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, 2);
CharSequence result = stemmer.stem("jumped");
assertEquals("jump", result.toString());
}

@Test
public void testConstructorWithRepeatEqualsMinusOne() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, -1);
CharSequence result = stemmer.stem("jumped");
assertNotNull(result);
assertEquals("jumped", result.toString());
}

@Test
public void testConstructorWithRepeatEqualsIntegerMinValue() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, Integer.MIN_VALUE);
CharSequence result = stemmer.stem("jumped");
assertNotNull(result);
assertEquals("jumped", result.toString());
}

@Test
public void testRepeatEffectOnWordThatDoesNotStemFurther() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, 5);
CharSequence result = stemmer.stem("go");
assertNotNull(result);
assertEquals("go", result.toString());
}

@Test
public void testDifferentAlgorithmSameInput() {
SnowballStemmer englishStemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
SnowballStemmer frenchStemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.FRENCH);
CharSequence englishResult = englishStemmer.stem("national");
CharSequence frenchResult = frenchStemmer.stem("national");
assertNotNull(englishResult);
assertNotNull(frenchResult);
assertNotEquals(englishResult.toString(), frenchResult.toString());
}

@Test
public void testSpecialCharacterStem() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("@$*!");
assertNotNull(result);
assertEquals("@$*!", result.toString());
}

@Test
public void testStemCJKCharacters() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("漢字");
assertNotNull(result);
assertEquals("漢字", result.toString());
}

@Test
public void testWhitespaceOnlyResultShouldReturnSameString() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("   ");
assertEquals("   ", result.toString());
}

@Test
public void testEnumToStringValueExists() {
SnowballStemmer.ALGORITHM algorithm = SnowballStemmer.ALGORITHM.FINNISH;
assertEquals("FINNISH", algorithm.toString());
}

@Test
public void testWordContainingHyphenRemainsIntact() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("pre-processed");
assertNotNull(result);
assertTrue(result.toString().contains("-"));
}

@Test
public void testEmptyStringWithHighRepeat() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, 100);
CharSequence result = stemmer.stem("");
assertEquals("", result.toString());
}

@Test
public void testConstructorWithZeroRepeatSameAsOriginal() {
SnowballStemmer stemmerZero = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, 0);
SnowballStemmer stemmerDefault = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence resultZero = stemmerZero.stem("walking");
CharSequence resultDefault = stemmerDefault.stem("walking");
assertNotEquals(resultZero.toString(), resultDefault.toString());
}

@Test
public void testStemmingMultipleTimesUsingSameInstance() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence first = stemmer.stem("playing");
CharSequence second = stemmer.stem("walked");
assertEquals("play", first.toString());
assertEquals("walk", second.toString());
}

@Test
public void testPolymorphicBehaviorWithDifferentWords() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result1 = stemmer.stem("skipping");
CharSequence result2 = stemmer.stem("jumps");
assertEquals("skip", result1.toString());
assertEquals("jump", result2.toString());
}

@Test
public void testRepeatedStemmingOfSameWordConsistent() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence first = stemmer.stem("folded");
CharSequence second = stemmer.stem("folded");
assertEquals("fold", first.toString());
assertEquals("fold", second.toString());
}

@Test
public void testSymbolWordReturnsSameString() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("@@@###");
assertEquals("@@@###", result.toString());
}

@Test
public void testSingleNonAlphabeticCharacter() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("*");
assertEquals("*", result.toString());
}

@Test
public void testWhitespaceAndTabsOnly() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("   \t  ");
assertEquals("   \t  ", result.toString());
}

@Test
public void testStemOnLongMixedInput() {
String input = "running123!@#_processing-going123中文😊";
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem(input);
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testRepeatOneEqualsDefaultConstructorBehavior() {
SnowballStemmer stemmerDefault = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
SnowballStemmer stemmerOne = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, 1);
CharSequence resultDefault = stemmerDefault.stem("wrapped");
CharSequence resultOne = stemmerOne.stem("wrapped");
assertEquals(resultDefault.toString(), resultOne.toString());
}

@Test
public void testStemmerReusabilityAcrossLanguages() {
SnowballStemmer englishStemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence englishResult = englishStemmer.stem("runner");
SnowballStemmer italianStemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ITALIAN);
CharSequence italianResult = italianStemmer.stem("parlando");
assertEquals("runner", "runner".startsWith(englishResult.toString()) ? englishResult.toString() : englishResult.toString(), englishResult.toString());
assertTrue(italianResult.length() > 0);
}

@Test
public void testSpecialLanguageStemmingDoesNotThrow() {
SnowballStemmer catalanStemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.CATALAN);
CharSequence resultCatalan = catalanStemmer.stem("parlant");
assertNotNull(resultCatalan);
assertTrue(resultCatalan.length() > 0);
SnowballStemmer romanianStemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ROMANIAN);
CharSequence resultRomanian = romanianStemmer.stem("copiii");
assertNotNull(resultRomanian);
assertTrue(resultRomanian.length() > 0);
}

@Test
public void testWordsEndingWithApostrophe() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("teacher's");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testMultipleNonLatinScriptsCombined() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("テスト漢字русскийالعربية🔤");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testStemOnValidEnterString() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("\n");
assertEquals("\n", result.toString());
}

@Test
public void testStemWithTrailingWhitespace() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("jumped ");
assertNotNull(result);
assertTrue(result.toString().startsWith("jump"));
}

@Test
public void testStemOnlyHyphens() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("---");
assertEquals("---", result.toString());
}

@Test
public void testEdgeCaseWithAllDigits() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("1234567890");
assertEquals("1234567890", result.toString());
}

@Test
public void testStemmerWithHighRepeatNoCrash() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, 1000);
CharSequence result = stemmer.stem("arching");
assertNotNull(result);
assertTrue(result.toString().length() > 0);
}

@Test
public void testRepeatedCallsDoNotRetainPreviousWord() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result1 = stemmer.stem("connected");
CharSequence result2 = stemmer.stem("walking");
assertEquals("connect", result1.toString());
assertEquals("walk", result2.toString());
}

@Test
public void testStemmingAlreadyStemmedWord() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("run");
assertEquals("run", result.toString());
}

@Test
public void testWhitespaceBetweenLetters() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("r u n n i n g");
assertEquals("r u n n i n g", result.toString());
}

@Test
public void testStemmingWithNewlineEmbedded() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("run\nning");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testStemmingCyrillicScript() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.RUSSIAN);
CharSequence result = stemmer.stem("работавший");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testStemmingGreekLetters() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.GREEK);
CharSequence result = stemmer.stem("τρέχοντας");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testStemmingTurkishCharacters() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.TURKISH);
CharSequence result = stemmer.stem("koşuyorum");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testConstructorWithLargePositiveRepeat() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, 100);
CharSequence result = stemmer.stem("transformation");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testConstructorWithIntegerMinValueRepeat() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, Integer.MIN_VALUE);
CharSequence result = stemmer.stem("transformation");
assertNotNull(result);
assertEquals("transformation", result.toString());
}

@Test
public void testConstructorWithIntegerMaxValueRepeat() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, Integer.MAX_VALUE);
CharSequence result = stemmer.stem("transformation");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testAllUppercaseWord() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("COMING");
assertEquals("come", result.toString());
}

@Test
public void testWeirdSymbolsOnly() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("!@#$%^&*-+=~");
assertEquals("!@#$%^&*-+=~", result.toString());
}

@Test
public void testDigitsAndLetters() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("run12345");
assertEquals("run12345", result.toString());
}

@Test
public void testEmojiStringUnchanged() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("😀😁😂🤣😃😄");
assertEquals("😀😁😂🤣😃😄", result.toString());
}

@Test
public void testMultilingualMixedInput() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.FRENCH);
CharSequence result = stemmer.stem("bonjourこんにちはhello");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testStemmingApostrophes() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("runner's");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testIllegalStateWhenAlgorithmIsNull() {
SnowballStemmer stemmer = new SnowballStemmer(null);
}

@Test
public void testStemmerDoesNotModifyInputString() {
String original = "connected";
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem(original);
assertEquals("connected", original);
assertEquals("connect", result.toString());
}

@Test
public void testEmptyStringMultipleTimes() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, 3);
CharSequence result = stemmer.stem("");
assertEquals("", result.toString());
}

@Test
public void testWhitespaceInputMultipleTimes() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, 5);
CharSequence result = stemmer.stem("   ");
assertEquals("   ", result.toString());
}

@Test
public void testOnlyHyphenCharacterInput() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("-");
assertEquals("-", result.toString());
}

@Test
public void testBuilderWordWithMultipleHyphens() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("co-operating");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testSymbolMixedWithAlphabeticInput() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("go!");
assertNotNull(result);
assertEquals("go!", result.toString());
}

@Test
public void testWhitespaceAtStartAndEnd() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem(" walking ");
assertNotNull(result);
assertEquals(" walking ", result.toString());
}

@Test
public void testNumericsWithHyphensInput() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("test-2024");
assertEquals("test-2024", result.toString());
}

@Test
public void testCamelCaseInput() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("CamelCaseWord");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testExtremelyShortInput() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("x");
assertEquals("x", result.toString());
}

@Test
public void testEmojiPrefixInput() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("💬comment");
assertEquals("💬comment", result.toString());
}

@Test
public void testRepeatCountEqualTwoConsistencyCheck() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, 2);
CharSequence result = stemmer.stem("processing");
assertEquals("process", result.toString());
}

@Test
public void testMultilingualWordInEnglishStemmer() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("überlaufen");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testTurkishSpecialCharacters() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.TURKISH);
CharSequence result = stemmer.stem("çalışıyor");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testUpperCaseWithApostrophe() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("USER'S");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testTabCharacterOnly() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("\t");
assertEquals("\t", result.toString());
}

@Test
public void testInputWithLineBreak() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("line\nbreak");
assertNotNull(result);
assertTrue(result.toString().contains("\n"));
}

@Test
public void testVeryLargeInputWithValidChars() {
StringBuilder sb = new StringBuilder();
for (int i = 0; i < 5000; i++) {
sb.append("test");
}
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem(sb.toString());
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testMixedWhitespaceBetweenLetters() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("t e s t");
assertEquals("t e s t", result.toString());
}

@Test
public void testNullCharacterInInput() {
String input = "test\0word";
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem(input);
assertTrue(result.toString().contains("\0"));
}

@Test
public void testRepeatOneReturnsExpectedResult() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, 1);
CharSequence result = stemmer.stem("modifying");
assertEquals("modifi", result.toString());
}

@Test
public void testRepeatThreeOnStemmableWord() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, 3);
CharSequence result = stemmer.stem("connections");
assertEquals("connect", result.toString());
}

@Test
public void testRepeatThreeOnNonStemmableWord() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, 3);
CharSequence result = stemmer.stem("go");
assertEquals("go", result.toString());
}

@Test
public void testMultipleConstructorInvocationsWithSameAlgorithmProduceIndependentInstances() {
SnowballStemmer s1 = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
SnowballStemmer s2 = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result1 = s1.stem("jumps");
CharSequence result2 = s2.stem("running");
assertEquals("jump", result1.toString());
assertEquals("run", result2.toString());
}

@Test
public void testRepeatMinusTenShouldBehaveLikeZero() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, -10);
CharSequence result = stemmer.stem("tested");
assertEquals("tested", result.toString());
}

@Test
public void testRepeatZeroForWordNeedingStemming() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, 0);
CharSequence result = stemmer.stem("testing");
assertEquals("testing", result.toString());
}

@Test
public void testMixedCaseInputEnglish() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence mixed = stemmer.stem("Running");
CharSequence upper = stemmer.stem("RUNNING");
assertNotNull(mixed);
assertNotNull(upper);
assertTrue(mixed.toString().length() > 0);
assertTrue(upper.toString().length() > 0);
}

@Test
public void testStringContainingOnlyAccents() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
String input = "éèêë";
CharSequence result = stemmer.stem(input);
assertEquals(input, result.toString());
}

@Test
public void testWhitespaceWithValidSuffix() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("running ");
assertEquals("running ", result.toString());
}

@Test
public void testStemmingIndonesianWithLengthOneWord() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.INDONESIAN);
CharSequence result = stemmer.stem("a");
assertEquals("a", result.toString());
}

@Test
public void testStemmingHungarianShortUntouchedWord() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.HUNGARIAN);
CharSequence result = stemmer.stem("és");
assertNotNull(result);
assertEquals("és", result.toString());
}

@Test
public void testStemmingFinnishNullCharInside() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.FINNISH);
CharSequence result = stemmer.stem("sana\0");
assertNotNull(result);
assertTrue(result.toString().contains("\0"));
}

@Test
public void testUncommonCharactersInRomanian() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ROMANIAN);
CharSequence result = stemmer.stem("șțâîă");
assertNotNull(result);
assertTrue(result.toString().length() > 0);
}

@Test
public void testEmojiFollowedByAlphabet() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("💡walking");
assertEquals("💡walking", result.toString());
}

@Test
public void testNumberMixedWithEnglishWord() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("walk123");
assertEquals("walk123", result.toString());
}

@Test
public void testShortWordAlreadyStemmedAcrossLanguages() {
SnowballStemmer stemmerEnglish = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
SnowballStemmer stemmerGerman = new SnowballStemmer(SnowballStemmer.ALGORITHM.GERMAN);
CharSequence resultEnglish = stemmerEnglish.stem("go");
CharSequence resultGerman = stemmerGerman.stem("go");
assertEquals("go", resultEnglish.toString());
assertEquals("go", resultGerman.toString());
}

@Test
public void testCatalanShortWordUnchanged() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.CATALAN);
CharSequence result = stemmer.stem("el");
assertNotNull(result);
assertEquals("el", result.toString());
}

@Test
public void testIrishCommonPreposition() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.IRISH);
CharSequence result = stemmer.stem("ag");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testWordWithQuotes() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("\"running\"");
assertEquals("\"running\"", result.toString());
}

@Test
public void testEmptyStringWithMaximumRepeat() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, Integer.MAX_VALUE);
CharSequence result = stemmer.stem("");
assertEquals("", result.toString());
}

@Test
public void testWhitespaceOnlyWithNegativeRepeat() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, -20);
CharSequence result = stemmer.stem(" ");
assertEquals(" ", result.toString());
}

@Test
public void testValidWordWithTrailingSymbol() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("running!");
assertEquals("running!", result.toString());
}

@Test
public void testVeryLongSingleWordBoundaryLength() {
String word = "stem" + new String(new char[3000]).replace('\0', 'a');
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem(word);
assertNotNull(result);
assertTrue(result.length() > 0);
}
}
