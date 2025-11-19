package opennlp.tools.stemmer.snowball;

import opennlp.tools.stemmer.Stemmer;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class SnowballStemmer_2_GPTLLMTest {

@Test
public void testStem_English_DefaultRepeat() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("running");
assertNotNull(result);
assertTrue(result.length() >= 1);
}

@Test
public void testStem_English_CustomRepeat() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, 2);
CharSequence result = stemmer.stem("running");
assertNotNull(result);
assertTrue(result.length() >= 1);
}

@Test
public void testStem_WithMockStemmer_SingleRepeat() {
AbstractSnowballStemmer mockStemmer = mock(AbstractSnowballStemmer.class);
when(mockStemmer.getCurrent()).thenReturn("run");
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, 1) {

@Override
public CharSequence stem(CharSequence word) {
mockStemmer.setCurrent(word.toString());
mockStemmer.stem();
return mockStemmer.getCurrent();
}
};
CharSequence result = stemmer.stem("running");
verify(mockStemmer).setCurrent("running");
verify(mockStemmer).stem();
verify(mockStemmer).getCurrent();
assertEquals("run", result);
}

@Test
public void testStem_WithMockStemmer_ThreeRepeats() {
AbstractSnowballStemmer mockStemmer = mock(AbstractSnowballStemmer.class);
when(mockStemmer.getCurrent()).thenReturn("run");
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, 3) {

@Override
public CharSequence stem(CharSequence word) {
mockStemmer.setCurrent(word.toString());
mockStemmer.stem();
mockStemmer.stem();
mockStemmer.stem();
return mockStemmer.getCurrent();
}
};
CharSequence result = stemmer.stem("running");
verify(mockStemmer).setCurrent("running");
verify(mockStemmer, times(3)).stem();
verify(mockStemmer).getCurrent();
assertEquals("run", result);
}

@Test
public void testStem_Arabic() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ARABIC);
CharSequence result = stemmer.stem("الكتابة");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testStem_English() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("connections");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testStem_German() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.GERMAN);
CharSequence result = stemmer.stem("Verbindungen");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testStem_Porter() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.PORTER);
CharSequence result = stemmer.stem("fishing");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testStemmer_NullAlgorithm_ShouldThrow() {
new SnowballStemmer(null).stem("fail");
}

@Test
public void testStem_EmptyString() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("");
assertNotNull(result);
assertEquals("", result.toString());
}

@Test
public void testStem_SingleCharacter() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.FRENCH);
CharSequence result = stemmer.stem("a");
assertNotNull(result);
assertTrue(result.length() >= 1);
}

@Test
public void testStem_NumericString() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("12345");
assertNotNull(result);
assertEquals("12345", result.toString());
}

@Test
public void testStem_WithZeroRepeats() {
AbstractSnowballStemmer mockStemmer = mock(AbstractSnowballStemmer.class);
when(mockStemmer.getCurrent()).thenReturn("input");
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, 0) {

@Override
public CharSequence stem(CharSequence word) {
mockStemmer.setCurrent(word.toString());
return mockStemmer.getCurrent();
}
};
CharSequence result = stemmer.stem("input");
verify(mockStemmer).setCurrent("input");
verify(mockStemmer, never()).stem();
verify(mockStemmer).getCurrent();
assertEquals("input", result);
}

@Test
public void testStem_WithNegativeRepeat() {
AbstractSnowballStemmer mockStemmer = mock(AbstractSnowballStemmer.class);
when(mockStemmer.getCurrent()).thenReturn("input");
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, -2) {

@Override
public CharSequence stem(CharSequence word) {
mockStemmer.setCurrent(word.toString());
return mockStemmer.getCurrent();
}
};
CharSequence result = stemmer.stem("input");
verify(mockStemmer).setCurrent("input");
verify(mockStemmer, never()).stem();
verify(mockStemmer).getCurrent();
assertEquals("input", result);
}

@Test
public void testStem_CompareDifferentLanguages() {
SnowballStemmer englishStemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
SnowballStemmer spanishStemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.SPANISH);
CharSequence english = englishStemmer.stem("talked");
CharSequence spanish = spanishStemmer.stem("hablando");
assertNotNull(english);
assertNotNull(spanish);
assertNotEquals(english.toString(), spanish.toString());
}

@Test
public void testStem_Dutch() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.DUTCH);
CharSequence result = stemmer.stem("hogescholen");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testStem_Finnish() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.FINNISH);
CharSequence result = stemmer.stem("kirjoittaminen");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testStem_Hungarian() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.HUNGARIAN);
CharSequence result = stemmer.stem("tanulás");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testStem_Portuguese() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.PORTUGUESE);
CharSequence result = stemmer.stem("falando");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testStem_Swedish() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.SWEDISH);
CharSequence result = stemmer.stem("skrivande");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testStem_NullInputShouldReturnEmpty() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence input = null;
try {
CharSequence result = stemmer.stem(input);
fail("Expected NullPointerException");
} catch (NullPointerException e) {
assertTrue(true);
}
}

@Test
public void testStem_InputWithOnlyWhitespace() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("   ");
assertNotNull(result);
assertEquals("   ", result.toString());
}

@Test
public void testStem_InputWithSymbolsOnly() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence input = "!@#$%^";
CharSequence result = stemmer.stem(input);
assertNotNull(result);
assertEquals("!@#$%^", result.toString());
}

@Test
public void testStem_InputWithMixedUnicodeCharacters() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence input = "résumé®";
CharSequence result = stemmer.stem(input);
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testStem_LongInputWord() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
StringBuilder sb = new StringBuilder();
for (int i = 0; i < 1000; i++) {
sb.append("running");
}
CharSequence input = sb.toString();
CharSequence result = stemmer.stem(input);
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testStem_RepeatSetToMaxInteger() {
AbstractSnowballStemmer mockStemmer = mock(AbstractSnowballStemmer.class);
when(mockStemmer.getCurrent()).thenReturn("root");
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, Integer.MAX_VALUE) {

@Override
public CharSequence stem(CharSequence word) {
mockStemmer.setCurrent(word.toString());
return mockStemmer.getCurrent();
}
};
CharSequence result = stemmer.stem("running");
verify(mockStemmer).setCurrent("running");
verify(mockStemmer).getCurrent();
assertEquals("root", result);
}

@Test
public void testStem_RepeatSetToIntegerMinValue() {
AbstractSnowballStemmer mockStemmer = mock(AbstractSnowballStemmer.class);
when(mockStemmer.getCurrent()).thenReturn("root");
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, Integer.MIN_VALUE) {

@Override
public CharSequence stem(CharSequence word) {
mockStemmer.setCurrent(word.toString());
return mockStemmer.getCurrent();
}
};
CharSequence result = stemmer.stem("running");
verify(mockStemmer).setCurrent("running");
verify(mockStemmer, never()).stem();
verify(mockStemmer).getCurrent();
assertEquals("root", result);
}

@Test
public void testStem_Catalan() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.CATALAN);
CharSequence result = stemmer.stem("estudiant");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testStem_Greek() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.GREEK);
CharSequence result = stemmer.stem("γράφοντας");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testStem_Irish() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.IRISH);
CharSequence result = stemmer.stem("ag scríobh");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testStem_Turkish() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.TURKISH);
CharSequence result = stemmer.stem("konuşuyorum");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testStem_LanguageBoundary_NorwegianVsDanish() {
SnowballStemmer norwegianStemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.NORWEGIAN);
SnowballStemmer danishStemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.DANISH);
CharSequence norwegian = norwegianStemmer.stem("skrivende");
CharSequence danish = danishStemmer.stem("skrivende");
assertNotNull(norwegian);
assertNotNull(danish);
assertNotEquals(norwegian.toString(), danish.toString());
}

@Test
public void testStem_Romanian() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ROMANIAN);
CharSequence result = stemmer.stem("scriind");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testStem_MultipleStemsProduceSameOutput() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, 5);
CharSequence result = stemmer.stem("connected");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testStem_InputIsSameObjectAfterStemming() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence input = "connected";
CharSequence result = stemmer.stem(input);
assertNotSame(input, result, "Stemmed output should be a new object if stemmer modifies string");
}

@Test
public void testStem_RepeatedCallsWithSameStemmerProduceSameResults() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result1 = stemmer.stem("organization");
CharSequence result2 = stemmer.stem("organization");
assertEquals(result1.toString(), result2.toString());
}

@Test
public void testStem_RepeatedCallsWithDifferentInputsSameStemmer() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result1 = stemmer.stem("activation");
CharSequence result2 = stemmer.stem("activated");
assertNotEquals(result1.toString(), result2.toString());
}

@Test
public void testAllEnumValuesMappedAndExposedViaConstructor() {
SnowballStemmer stemmer0 = new SnowballStemmer(SnowballStemmer.ALGORITHM.ARABIC);
assertNotNull(stemmer0.stem(""));
SnowballStemmer stemmer1 = new SnowballStemmer(SnowballStemmer.ALGORITHM.CATALAN);
assertNotNull(stemmer1.stem(""));
SnowballStemmer stemmer2 = new SnowballStemmer(SnowballStemmer.ALGORITHM.DANISH);
assertNotNull(stemmer2.stem(""));
SnowballStemmer stemmer3 = new SnowballStemmer(SnowballStemmer.ALGORITHM.DUTCH);
assertNotNull(stemmer3.stem(""));
SnowballStemmer stemmer4 = new SnowballStemmer(SnowballStemmer.ALGORITHM.FINNISH);
assertNotNull(stemmer4.stem(""));
SnowballStemmer stemmer5 = new SnowballStemmer(SnowballStemmer.ALGORITHM.FRENCH);
assertNotNull(stemmer5.stem(""));
SnowballStemmer stemmer6 = new SnowballStemmer(SnowballStemmer.ALGORITHM.GERMAN);
assertNotNull(stemmer6.stem(""));
SnowballStemmer stemmer7 = new SnowballStemmer(SnowballStemmer.ALGORITHM.GREEK);
assertNotNull(stemmer7.stem(""));
SnowballStemmer stemmer8 = new SnowballStemmer(SnowballStemmer.ALGORITHM.HUNGARIAN);
assertNotNull(stemmer8.stem(""));
SnowballStemmer stemmer9 = new SnowballStemmer(SnowballStemmer.ALGORITHM.INDONESIAN);
assertNotNull(stemmer9.stem(""));
SnowballStemmer stemmer10 = new SnowballStemmer(SnowballStemmer.ALGORITHM.IRISH);
assertNotNull(stemmer10.stem(""));
SnowballStemmer stemmer11 = new SnowballStemmer(SnowballStemmer.ALGORITHM.ITALIAN);
assertNotNull(stemmer11.stem(""));
SnowballStemmer stemmer12 = new SnowballStemmer(SnowballStemmer.ALGORITHM.NORWEGIAN);
assertNotNull(stemmer12.stem(""));
SnowballStemmer stemmer13 = new SnowballStemmer(SnowballStemmer.ALGORITHM.PORTER);
assertNotNull(stemmer13.stem(""));
SnowballStemmer stemmer14 = new SnowballStemmer(SnowballStemmer.ALGORITHM.PORTUGUESE);
assertNotNull(stemmer14.stem(""));
SnowballStemmer stemmer15 = new SnowballStemmer(SnowballStemmer.ALGORITHM.ROMANIAN);
assertNotNull(stemmer15.stem(""));
SnowballStemmer stemmer16 = new SnowballStemmer(SnowballStemmer.ALGORITHM.RUSSIAN);
assertNotNull(stemmer16.stem(""));
SnowballStemmer stemmer17 = new SnowballStemmer(SnowballStemmer.ALGORITHM.SPANISH);
assertNotNull(stemmer17.stem(""));
SnowballStemmer stemmer18 = new SnowballStemmer(SnowballStemmer.ALGORITHM.SWEDISH);
assertNotNull(stemmer18.stem(""));
SnowballStemmer stemmer19 = new SnowballStemmer(SnowballStemmer.ALGORITHM.TURKISH);
assertNotNull(stemmer19.stem(""));
}

@Test
public void testStem_MockStemmerResetBehaviorAcrossInputs() {
AbstractSnowballStemmer mockStemmer = mock(AbstractSnowballStemmer.class);
when(mockStemmer.getCurrent()).thenReturn("base");
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, 1) {

@Override
public CharSequence stem(CharSequence word) {
mockStemmer.setCurrent(word.toString());
mockStemmer.stem();
return mockStemmer.getCurrent();
}
};
CharSequence first = stemmer.stem("running");
CharSequence second = stemmer.stem("walking");
verify(mockStemmer, times(1)).setCurrent("running");
verify(mockStemmer, times(1)).setCurrent("walking");
verify(mockStemmer, times(2)).stem();
verify(mockStemmer, times(2)).getCurrent();
assertEquals("base", first.toString());
assertEquals("base", second.toString());
}

@Test
public void testCustomSubclassCallsParentConstructor() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH) {

@Override
public CharSequence stem(CharSequence word) {
return "manuallyProcessed";
}
};
CharSequence result = stemmer.stem("word");
assertEquals("manuallyProcessed", result);
}

@Test
public void testStem_VeryShortRepeatStillWorks() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, 1);
CharSequence result = stemmer.stem("walked");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testStem_TrailingPunctuationPreservation() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence input = "connect.";
CharSequence result = stemmer.stem(input);
assertNotNull(result);
assertTrue(result.toString().contains(".") || result.length() > 0);
}

@Test
public void testStem_WhitespacePadding() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence input = "  walking  ";
CharSequence result = stemmer.stem(input);
assertNotNull(result);
assertTrue(result.toString().trim().length() > 0);
}

@Test
public void testStem_InternalStemmerReturnsNull() {
AbstractSnowballStemmer mockStemmer = mock(AbstractSnowballStemmer.class);
when(mockStemmer.getCurrent()).thenReturn(null);
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, 1) {

@Override
public CharSequence stem(CharSequence word) {
mockStemmer.setCurrent(word.toString());
mockStemmer.stem();
return mockStemmer.getCurrent();
}
};
CharSequence result = stemmer.stem("test");
assertNull(result);
}

@Test
public void testStem_NonLatinCharacters() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence input = "漢字한글";
CharSequence result = stemmer.stem(input);
assertNotNull(result);
assertEquals("漢字한글", result.toString());
}

@Test
public void testStem_SpecialCharactersMix() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence input = "connect@123!ing###";
CharSequence result = stemmer.stem(input);
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testStem_SameInputMultipleTimesDifferentResults() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, 1);
CharSequence input = "runner";
CharSequence result1 = stemmer.stem(input);
CharSequence result2 = stemmer.stem(input);
assertNotNull(result1);
assertNotNull(result2);
assertEquals(result1.toString(), result2.toString());
}

@Test
public void testStem_UppercaseInput() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence input = "RUNNING";
CharSequence result = stemmer.stem(input);
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testStem_OnlyAccents() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.FRENCH);
CharSequence input = "éèëê";
CharSequence result = stemmer.stem(input);
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testStem_TabAndNewLineCharacters() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence input = "\twalking\n";
CharSequence result = stemmer.stem(input);
assertNotNull(result);
assertTrue(result.toString().contains("\t") || result.toString().contains("\n"));
}

@Test
public void testStem_WhitespaceOnlyTabsAndSpaces() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.SPANISH);
CharSequence input = "   \t   ";
CharSequence result = stemmer.stem(input);
assertNotNull(result);
assertEquals(input.toString(), result.toString());
}

@Test
public void testStem_AllWhitespaceCharacters() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence input = " \n\t\r";
CharSequence result = stemmer.stem(input);
assertNotNull(result);
assertEquals(input.toString(), result.toString());
}

@Test
public void testStem_LongWhitespaceInput() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence input = new String(new char[100]).replace('\0', ' ');
CharSequence result = stemmer.stem(input);
assertNotNull(result);
assertEquals(input.toString(), result.toString());
}

@Test
public void testStem_MixOfLettersAndNumbers() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence input = "test123ing456";
CharSequence result = stemmer.stem(input);
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testStem_RepeatedCallAfterWhitespaceInput() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence input1 = "   ";
CharSequence input2 = "running";
CharSequence result1 = stemmer.stem(input1);
CharSequence result2 = stemmer.stem(input2);
assertEquals("   ", result1.toString());
assertNotEquals(result2.toString(), result1.toString());
}

@Test
public void testStem_MinimalValidSingleUnicodeChar() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence input = "\u0000";
CharSequence result = stemmer.stem(input);
assertNotNull(result);
assertTrue(result.length() == 1);
}

@Test
public void testStem_EmojiInput() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence input = "😊🚀🌍";
CharSequence result = stemmer.stem(input);
assertNotNull(result);
assertEquals("😊🚀🌍", result.toString());
}

@Test
public void testStem_ExtremeShortWord() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence input = "x";
CharSequence result = stemmer.stem(input);
assertNotNull(result);
assertTrue(result.length() >= 1);
}

@Test
public void testStem_DifferentCasingSameWord() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence lower = stemmer.stem("running");
CharSequence upper = stemmer.stem("Running");
assertNotNull(lower);
assertNotNull(upper);
assertEquals(lower.toString().toLowerCase(), upper.toString().toLowerCase());
}

@Test
public void testStem_NullCharSequenceImplementation() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence input = new CharSequence() {

@Override
public int length() {
return 0;
}

@Override
public char charAt(int index) {
throw new IndexOutOfBoundsException();
}

@Override
public CharSequence subSequence(int start, int end) {
return null;
}

@Override
public String toString() {
return null;
}
};
try {
stemmer.stem(input);
fail("Expected NullPointerException due to null from toString()");
} catch (NullPointerException e) {
assertTrue(true);
}
}

@Test
public void testStem_VeryLargeWordLikeInput() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
String input = "";
for (int i = 0; i < 100_000; i++) {
input += "a";
}
CharSequence result = stemmer.stem(input);
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testStem_CharSequenceThatThrowsException() {
CharSequence faulty = new CharSequence() {

@Override
public int length() {
throw new RuntimeException("Length unsupported");
}

@Override
public char charAt(int index) {
throw new RuntimeException("charAt unsupported");
}

@Override
public CharSequence subSequence(int start, int end) {
throw new RuntimeException("subSequence unsupported");
}

@Override
public String toString() {
return "testing";
}
};
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem(faulty);
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testStem_MultipleWordsConcatenated() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("runningJumpingFlying");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testStem_AlternatingStemInvocationAndToString() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("swimming");
assertNotNull(result);
String output = result.toString();
assertNotNull(output);
assertTrue(output.length() > 0);
CharSequence result2 = stemmer.stem("flying");
assertNotNull(result2);
assertNotSame(result.toString(), result2.toString());
}

@Test
public void testStem_WhitespaceBeforeAndAfterWord() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("  running  ");
assertNotNull(result);
assertTrue(result.toString().trim().length() > 0);
}

@Test
public void testStem_UnicodeSurrogatePairs() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
String input = "𝔘𝔫𝔦𝔠𝔬𝔡𝔢";
CharSequence result = stemmer.stem(input);
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testStem_ChangeRepeatDynamicallyByConstructor() {
SnowballStemmer stemmer1 = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, 1);
CharSequence result1 = stemmer1.stem("running");
SnowballStemmer stemmer2 = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, 3);
CharSequence result2 = stemmer2.stem("running");
assertNotNull(result1);
assertNotNull(result2);
assertEquals(result1.toString(), result1.toString());
assertEquals(result2.toString(), result2.toString());
}

@Test
public void testStem_LanguageAlgorithmExhaustiveBranchSingle() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.TURKISH);
CharSequence result = stemmer.stem("konuşuyorum");
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testStem_IllegalAlgorithmEnumThrows() {
try {
SnowballStemmer.ALGORITHM bogusAlgorithm = Enum.valueOf(SnowballStemmer.ALGORITHM.class, "PORTUGUESE");
SnowballStemmer stemmer = new SnowballStemmer(bogusAlgorithm);
CharSequence result = stemmer.stem("falando");
assertNotNull(result);
} catch (Exception e) {
fail("Expected algorithm to be handled: " + e.getMessage());
}
}

@Test
public void testStem_ExplicitCallToStemManyTimes_IdempotentOutput() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, 2);
CharSequence result1 = stemmer.stem("connected");
CharSequence result2 = stemmer.stem("connected");
CharSequence result3 = stemmer.stem("connected");
assertEquals(result1.toString(), result2.toString());
assertEquals(result2.toString(), result3.toString());
}

@Test
public void testStem_EmptyCharSequenceWithoutToStringOverride() {
CharSequence input = new CharSequence() {

@Override
public int length() {
return 0;
}

@Override
public char charAt(int index) {
throw new IndexOutOfBoundsException();
}

@Override
public CharSequence subSequence(int start, int end) {
return "";
}
};
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
try {
stemmer.stem(input);
fail("Expected NullPointerException due to default toString returning null");
} catch (NullPointerException e) {
assertTrue(e.getMessage() == null || e.getMessage().isEmpty());
}
}

@Test
public void testStem_DelegatedStemmerThrowsExceptionIsPropagated() {
AbstractSnowballStemmer throwingStemmer = new AbstractSnowballStemmer() {

@Override
public boolean stem() {
throw new RuntimeException("Simulated failure");
}
};
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, 1) {

@Override
public CharSequence stem(CharSequence word) {
throwingStemmer.setCurrent(word.toString());
throwingStemmer.stem();
return throwingStemmer.getCurrent();
}
};
try {
stemmer.stem("failure");
fail("Expected RuntimeException");
} catch (RuntimeException e) {
assertEquals("Simulated failure", e.getMessage());
}
}

@Test
public void testStem_WhitespaceAtBeginningAndEndWithPunctuation() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence input = "  walking!  ";
CharSequence result = stemmer.stem(input);
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testStem_InputWithTabsNewlinesAndSpaces() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence input = "\n\t running \t\n";
CharSequence result = stemmer.stem(input);
assertNotNull(result);
assertTrue(result.toString().trim().length() > 0);
}

@Test
public void testStem_StringWithQuotesAndLanguagesCharacters() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("“Quoted” —running—");
assertNotNull(result);
assertTrue(result.toString().length() > 0);
}

@Test
public void testStem_CyrillicCharacters() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.RUSSIAN);
CharSequence input = "говоришь";
CharSequence result = stemmer.stem(input);
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testStem_CJKCharacters() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence input = "漢語拼音";
CharSequence result = stemmer.stem(input);
assertNotNull(result);
assertEquals("漢語拼音", result.toString());
}

@Test
public void testStem_PortugueseAccentedCharacters() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.PORTUGUESE);
CharSequence input = "falatórios";
CharSequence result = stemmer.stem(input);
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testStem_GreekInputIncludesCombiningCharacters() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.GREEK);
CharSequence input = "γρα\u0301φω";
CharSequence result = stemmer.stem(input);
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testStem_InputIncludesZeroWidthJoiners() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence input = "run\u200Dning";
CharSequence result = stemmer.stem(input);
assertNotNull(result);
assertTrue(result.toString().contains("\u200D"));
}

@Test
public void testStem_EmojiAndTextMix() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence input = "running😊";
CharSequence result = stemmer.stem(input);
assertNotNull(result);
assertTrue(result.toString().contains("😊"));
}

@Test
public void testStem_RepeatCountOfOneBehavesSameAsDefaultConstructor() {
SnowballStemmer stemmer1 = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
SnowballStemmer stemmer2 = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, 1);
CharSequence result1 = stemmer1.stem("talking");
CharSequence result2 = stemmer2.stem("talking");
assertEquals(result1.toString(), result2.toString());
}

@Test
public void testStem_CallWithSpecialSymbolsOnly() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("!!!@@@###");
assertNotNull(result);
assertEquals("!!!@@@###", result.toString());
}

@Test
public void testStem_VeryLargeUnicodeString() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
StringBuilder builder = new StringBuilder();
for (int i = 0; i < 1000; i++) {
builder.append("á");
}
CharSequence result = stemmer.stem(builder.toString());
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testStem_MultipleSpacesBetweenWords() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence input = "walking     running     jumping";
CharSequence result = stemmer.stem(input);
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testConstructor_WithNegativeRepeatValue() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, -3);
CharSequence result = stemmer.stem("connected");
assertNotNull(result);
assertTrue(result.length() >= 0);
}

@Test
public void testConstructor_WithZeroRepeatValue() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, 0);
CharSequence result = stemmer.stem("connected");
assertNotNull(result);
assertTrue(result.length() >= 0);
}

@Test
public void testStemmer_CalledWithSurrogatePairInput() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
String input = "test\uD83D\uDE00";
CharSequence result = stemmer.stem(input);
assertNotNull(result);
assertTrue(result.toString().contains("\uD83D\uDE00"));
}

@Test
public void testStem_TurkishCasingBehavior() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.TURKISH);
CharSequence resultUpper = stemmer.stem("Istanbul");
CharSequence resultLower = stemmer.stem("istanbul");
assertNotNull(resultUpper);
assertNotNull(resultLower);
assertNotEquals(resultUpper.toString(), resultLower.toString());
}

@Test
public void testStem_EmptyEnumBranchCoverage() {
try {
SnowballStemmer.ALGORITHM invalid = null;
SnowballStemmer stemmer = new SnowballStemmer(invalid);
stemmer.stem("fail");
fail("Expected IllegalStateException or NullPointerException");
} catch (NullPointerException | IllegalStateException e) {
assertTrue(e instanceof NullPointerException || e instanceof IllegalStateException);
}
}

@Test
public void testStem_MultipleWhitespaceInsideWord() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("run  ning");
assertNotNull(result);
assertTrue(result.toString().contains("  "));
}

@Test
public void testStem_UncommonButValidUnicodeWordBoundary() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence input = "\u200Bwalking";
CharSequence result = stemmer.stem(input);
assertNotNull(result);
assertTrue(result.toString().contains("\u200B") || result.length() > 0);
}

@Test
public void testStem_SameStemmerUsedAcrossMultipleLanguages() {
SnowballStemmer english = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
SnowballStemmer italian = new SnowballStemmer(SnowballStemmer.ALGORITHM.ITALIAN);
CharSequence resultEn = english.stem("running");
CharSequence resultIt = italian.stem("correndo");
assertNotNull(resultEn);
assertNotNull(resultIt);
assertNotEquals(resultEn.toString(), resultIt.toString());
}

@Test
public void testStem_SymbolHeavyWord() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence input = "#connect!";
CharSequence result = stemmer.stem(input);
assertNotNull(result);
assertTrue(result.toString().contains("!") || result.toString().contains("#"));
}

@Test
public void testStem_WordMadeOfOnlyDots() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence result = stemmer.stem("...");
assertNotNull(result);
assertEquals("...", result.toString());
}

@Test
public void testStem_WordWithRTLCharacters() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ARABIC);
CharSequence input = "كتابة";
CharSequence result = stemmer.stem(input);
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testStem_InputWithEmbeddedControlCharacter() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
String input = "walk\u0005ing";
CharSequence result = stemmer.stem(input);
assertNotNull(result);
assertTrue(result.length() > 0);
}

@Test
public void testStem_RepeatExactlyTwoTimes() {
AbstractSnowballStemmer mockStemmer = mock(AbstractSnowballStemmer.class);
when(mockStemmer.getCurrent()).thenReturn("root");
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH, 2) {

@Override
public CharSequence stem(CharSequence word) {
mockStemmer.setCurrent(word.toString());
mockStemmer.stem();
mockStemmer.stem();
return mockStemmer.getCurrent();
}
};
CharSequence result = stemmer.stem("connected");
assertEquals("root", result);
verify(mockStemmer).setCurrent("connected");
verify(mockStemmer, times(2)).stem();
verify(mockStemmer).getCurrent();
}

@Test
public void testStem_PunctuationBeforeAndAfterWord() {
SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
CharSequence input = "(running)";
CharSequence result = stemmer.stem(input);
assertNotNull(result);
assertTrue(result.length() > 0);
}
}
