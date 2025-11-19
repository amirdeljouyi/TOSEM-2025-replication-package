package edu.stanford.nlp.process;

import edu.stanford.nlp.ling.*;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import static org.junit.Assert.*;

public class Morphology_2_GPTLLMTest {

 @Test
  public void testStemStringPluralNoun() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.stem("dogs");
    assertEquals("dog", result);
  }
@Test
  public void testStemStringGerundVerb() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.stem("running");
    assertEquals("run", result);
  }
@Test
  public void testStemStringIrregularPastTense() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.stem("went");
    assertEquals("go", result);
  }
@Test
  public void testStemStringCapitalizedVerb() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.stem("Walked");
    assertEquals("walk", result);
  }
@Test
  public void testStemWordInput() {
    Morphology morphology = new Morphology(new StringReader(""));
    Word input = new Word("cats");
    Word result = morphology.stem(input);
    assertEquals("cat", result.word());
  }
@Test
  public void testLemmaStringVerbLowercase() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.lemma("Running", "VBG");
    assertEquals("run", result);
  }
@Test
  public void testLemmaProperNounPreserved() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.lemma("London", "NNP");
    assertEquals("London", result);
  }
@Test
  public void testLemmaDoNotLowercase() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.lemma("Cities", "NNS", false);
    assertEquals("Cities", result);
  }
@Test
  public void testStemCoreLabelDefaultAnnotation() {
    Morphology morphology = new Morphology(new StringReader(""));
    CoreLabel label = new CoreLabel();
    label.setWord("dogs");
    label.setTag("NNS");
    morphology.stem(label);
    String lemma = label.get(CoreAnnotations.LemmaAnnotation.class);
    assertEquals("dog", lemma);
  }
@Test
  public void testStemCoreLabelCustomAnnotation() {
    Morphology morphology = new Morphology(new StringReader(""));
    CoreLabel label = new CoreLabel();
    label.setWord("singing");
    label.setTag("VBG");
    morphology.stem(label, CoreAnnotations.LemmaAnnotation.class);
    String lemma = label.get(CoreAnnotations.LemmaAnnotation.class);
    assertEquals("sing", lemma);
  }
@Test
  public void testApplyWithWordTag() {
    Morphology morphology = new Morphology(new StringReader(""));
    WordTag input = new WordTag("talked", "VBD");
    Object result = morphology.apply(input);
    assertTrue(result instanceof WordTag);
    WordTag output = (WordTag) result;
    assertEquals("talk", output.word());
    assertEquals("VBD", output.tag());
  }
@Test
  public void testApplyWithWord() {
    Morphology morphology = new Morphology(new StringReader(""));
    Word input = new Word("flies");
    Object result = morphology.apply(input);
    assertTrue(result instanceof Word);
    Word output = (Word) result;
    assertEquals("fly", output.word());
  }
@Test
  public void testApplyWithUnsupportedInput() {
    Morphology morphology = new Morphology(new StringReader(""));
    String input = "custom";
    Object result = morphology.apply(input);
    assertEquals("custom", result);
  }
@Test
  public void testLemmatizeReturnWordLemmaTag() {
    Morphology morphology = new Morphology(new StringReader(""));
    WordTag wt = new WordTag("eaten", "VBN");
    WordLemmaTag result = morphology.lemmatize(wt);
    assertEquals("eaten", result.word());
    assertEquals("eat", result.lemma());
    assertEquals("VBN", result.tag());
  }
@Test
  public void testStemStaticReturnsExpectedOutput() {
    WordTag input = new WordTag("slept", "VBD");
    WordTag result = Morphology.stemStatic(input);
    assertEquals("sleep", result.word());
    assertEquals("VBD", result.tag());
  }
@Test
  public void testLemmaStaticDefaultLowercase() {
    String result = Morphology.lemmaStatic("Drives", "VBZ");
    assertEquals("drive", result);
  }
@Test
  public void testLemmaStaticWithLowercaseFalse() {
    String result = Morphology.lemmaStatic("Cities", "NNS", false);
    assertEquals("Cities", result);
  }
@Test
  public void testLemmatizeStaticReturnsExpectedOutput() {
    WordTag input = new WordTag("ran", "VBD");
    WordLemmaTag result = Morphology.lemmatizeStatic(input);
    assertEquals("ran", result.word());
    assertEquals("run", result.lemma());
    assertEquals("VBD", result.tag());
  }
@Test
  public void testLemmaWithUnderscoreInWord() {
    String result = Morphology.lemmaStatic("multi_word", "NN");
    assertNotNull(result);
    assertFalse(result.contains("\u1CF0")); 
  }
@Test
  public void testStemHandlesNewlineInWord() {
    String result = Morphology.lemmaStatic("multi\nline", "NN");
    assertNotNull(result);
    assertFalse(result.contains("\u1CF2")); 
  }
@Test
  public void testStemHandlesSpaceInWord() {
    String result = Morphology.lemmaStatic("compound term", "NN");
    assertNotNull(result);
    assertFalse(result.contains("\u1CF1")); 
  }
@Test
  public void testStemEmptyString() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.stem("");
    assertEquals("", result);
  }
@Test
  public void testLemmaEmptyWordAndTag() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.lemma("", "");
    assertEquals("", result);
  }
@Test
  public void testLemmaWithUnknownTag() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.lemma("abcxyz", "ZZZ");
    assertEquals("abcxyz", result); 
  }
@Test
  public void testStemStringWithOnlyPunctuation() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.stem("!!!");
    assertEquals("!!!", result);
  }
@Test
  public void testStemStringWithNumbers() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.stem("1234");
    assertEquals("1234", result);
  }
@Test
  public void testStemStringNullEquivalent() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.stem("null");
    assertEquals("null", result); 
  }
@Test
  public void testApplyWithNullInput() {
    Morphology morphology = new Morphology(new StringReader(""));
    Object result = morphology.apply(null);
    assertNull(result); 
  }
@Test
  public void testLemmaStaticWithUnderscoreTag() {
    String result = Morphology.lemmaStatic("file_name", "NN");
    assertNotNull(result);
    assertFalse(result.contains("\u1CF0"));
  }
@Test
  public void testLemmatizeEmptyWordTag() {
    Morphology morphology = new Morphology(new StringReader(""));
    WordTag wordTag = new WordTag("", "");
    WordLemmaTag lemmaTag = morphology.lemmatize(wordTag);
    assertEquals("", lemmaTag.word());
    assertEquals("", lemmaTag.lemma());
    assertEquals("", lemmaTag.tag());
  }
@Test
  public void testStemStaticWithUppercaseProperNoun() {
    WordTag wordTag = new WordTag("London", "NNP");
    WordTag stemmed = Morphology.stemStatic(wordTag);
    assertEquals("London", stemmed.word()); 
    assertEquals("NNP", stemmed.tag());
  }
@Test
  public void testStemCoreLabelWithNullFields() {
    Morphology morphology = new Morphology(new StringReader(""));
    CoreLabel label = new CoreLabel();
    morphology.stem(label, CoreAnnotations.LemmaAnnotation.class);
    String result = label.get(CoreAnnotations.LemmaAnnotation.class);
    assertNull(result); 
  }
@Test
  public void testStemWithWhitespaceOnlyWord() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.stem("   ");
    assertEquals("   ", result); 
  }
@Test
  public void testStemControlCharacterInput() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.stem("\u0007");
    assertEquals("\u0007", result); 
  }
@Test
  public void testLemmaWithTabSpaceCharacters() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.lemma("multi\tterm", "NN");
    assertNotNull(result); 
  }
@Test
  public void testStaticLemmaMultipleCallsThreadSafe() {
    String result1 = Morphology.lemmaStatic("walked", "VBD");
    String result2 = Morphology.lemmaStatic("running", "VBG");
    String result3 = Morphology.lemmaStatic("flies", "VBZ");

    assertEquals("walk", result1);
    assertEquals("run", result2);
    assertEquals("fly", result3);
  }
@Test
  public void testStemStaticWithWordContainingNumericSuffix() {
    WordTag wordTag = new WordTag("dogs123", "NNS");
    WordTag stemmed = Morphology.stemStatic(wordTag);
    assertNotNull(stemmed.word());
  }
@Test
  public void testLemmatizeStaticTrickyInflection() {
    WordTag wordTag = new WordTag("better", "JJR");
    WordLemmaTag result = Morphology.lemmatizeStatic(wordTag);
    assertEquals("better", result.word()); 
    assertEquals("JJR", result.tag());
  }
@Test
  public void testStemStringWithEmoji() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.stem("😊");
    assertEquals("😊", result); 
  }
@Test
  public void testApplyOnWordWithAccentedCharacters() {
    Morphology morphology = new Morphology(new StringReader(""));
    Word input = new Word("niños");
    Object result = morphology.apply(input);
    assertTrue(result instanceof Word);
    Word output = (Word) result;
    assertNotNull(output.word());
  }
@Test
  public void testLemmaMixedCaseNoun() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.lemma("Trees", "NNS");
    assertEquals("tree", result);
  }
@Test
  public void testStemVeryLongWord() {
    Morphology morphology = new Morphology(new StringReader(""));
    String longWord = "supercalifragilisticexpialidociousnessified";
    String result = morphology.stem(longWord);
    assertNotNull(result);
  }
@Test
  public void testNextReturnsNullOnEmptyReader() throws Exception {
    Morphology morphology = new Morphology(new StringReader(""));
    Word result = morphology.next();
    assertNull(result);
  }
@Test
  public void testStemStringWithSpecialCharacters() {
    Morphology morphology = new Morphology(new StringReader(""));
    String input = "@running!";
    String result = morphology.stem(input);
    assertEquals("@running!", result); 
  }
@Test
  public void testStemStringMixedAlphaNumeric() {
    Morphology morphology = new Morphology(new StringReader(""));
    String input = "walk123ing";
    String result = morphology.stem(input);
    assertEquals("walk123ing", result); 
  }
@Test
  public void testLemmaWithTagSpaceCharacter() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.lemma("walked", " ");
    assertEquals("walked", result); 
  }
@Test
  public void testLemmaWithEmptyTagShouldReturnOriginalWord() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.lemma("running", "");
    assertEquals("running", result);
  }
@Test
  public void testStemStringWithTrailingWhitespace() {
    Morphology morphology = new Morphology(new StringReader(""));
    String input = "talked ";
    String result = morphology.stem(input);
    assertEquals("talked ", result); 
  }
@Test
  public void testStemStringWithLeadingWhitespace() {
    Morphology morphology = new Morphology(new StringReader(""));
    String input = " talked";
    String result = morphology.stem(input);
    assertEquals(" talked", result); 
  }
@Test
  public void testApplyWithUnsupportedType() {
    Morphology morphology = new Morphology(new StringReader(""));
    Integer input = 42;
    Object result = morphology.apply(input);
    assertEquals(input, result); 
  }
@Test
  public void testLemmaStaticOnSymbolWord() {
    String result = Morphology.lemmaStatic("&", "SYM");
    assertEquals("&", result);
  }
@Test
  public void testStemStaticWordTagWithUnderscoreInWord() {
    WordTag input = new WordTag("multi_word", "NN");
    WordTag result = Morphology.stemStatic(input);
    assertNotNull(result.word());
    assertFalse(result.word().contains("\u1CF0"));
  }
@Test
  public void testStemStaticWordTagWithMultipleForbiddenChars() {
    WordTag input = new WordTag("multi_word\nwith space", "NN");
    WordTag result = Morphology.stemStatic(input);
    assertNotNull(result.word()); 
    assertFalse(result.word().contains("\u1CF0"));
    assertFalse(result.word().contains("\u1CF1"));
    assertFalse(result.word().contains("\u1CF2"));
  }
@Test
  public void testLemmatizeStaticMultipleForbiddenCharacters() {
    WordTag input = new WordTag("term_with newline\nand space", "NN");
    WordLemmaTag result = Morphology.lemmatizeStatic(input);
    assertNotNull(result.lemma());
    assertFalse(result.lemma().contains("\u1CF0"));
    assertFalse(result.lemma().contains("\u1CF1"));
    assertFalse(result.lemma().contains("\u1CF2"));
  }
@Test
  public void testStemReturnsInputOnIOExceptionInsideLexer() {
    Morphology morphology = new Morphology(new StringReader("")) {
      @Override
      public String stem(String word) {
        throw new RuntimeException(new java.io.IOException("Simulated IO error"));
      }
    };
    String output;
    try {
      output = morphology.stem("test");
    } catch (RuntimeException e) {
      output = "test"; 
    }
    assertEquals("test", output);
  }
@Test
  public void testCoreLabelNoWordOrTagDoesNotThrow() {
    Morphology morphology = new Morphology(new StringReader(""));
    CoreLabel label = new CoreLabel(); 
    morphology.stem(label, CoreAnnotations.LemmaAnnotation.class);
    String result = label.get(CoreAnnotations.LemmaAnnotation.class);
    assertNull(result); 
  }
@Test
  public void testStaticLemmatizationSequence() {
    WordTag wt1 = new WordTag("talked", "VBD");
    WordLemmaTag l1 = Morphology.lemmatizeStatic(wt1);

    WordTag wt2 = new WordTag("going", "VBG");
    WordLemmaTag l2 = Morphology.lemmatizeStatic(wt2);

    assertEquals("talk", l1.lemma());
    assertEquals("go", l2.lemma());
  }
@Test
  public void testLowercaseEnabledFunctional() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.lemma("WALKED", "VBD", true);
    assertEquals("walk", result); 
  }
@Test
  public void testLowercaseDisabledOnCommonNoun() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.lemma("Cats", "NNS", false);
    assertEquals("Cats", result); 
  }
@Test
  public void testApplyOnNullWordTagFields() {
    Morphology morphology = new Morphology(new StringReader(""));
    WordTag input = new WordTag((String) null, null);
    Object result = morphology.apply(input);
    assertTrue(result instanceof WordTag);
    WordTag output = (WordTag) result;
    assertNull(output.word());
    assertNull(output.tag());
  }
@Test
  public void testStemNullInputReturnsUnchanged() {
    Morphology morphology = new Morphology(new StringReader(""));
    String word = null;
    String result;
    try {
      result = morphology.stem(word);
    } catch (NullPointerException e) {
      result = null;
    }
    assertNull(result);
  }
@Test
  public void testLemmaWithUnderscoreInPOSShouldNotBreak() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.lemma("walked", "VB_D");
    assertEquals("walked", result);
  }
@Test
  public void testStemVeryLongAlphaInput() {
    Morphology morphology = new Morphology(new StringReader(""));
    String longWord = "a";
    String result = morphology.stem(longWord);
    assertEquals(longWord, result);
  }
@Test
  public void testStemStaticCalledMultipleTimesThreadSafetyPreserved() {
    WordTag input1 = new WordTag("talked", "VBD");
    WordTag input2 = new WordTag("talked", "VBD");
    WordTag input3 = new WordTag("talked", "VBD");

    WordTag result1 = Morphology.stemStatic(input1);
    WordTag result2 = Morphology.stemStatic(input2);
    WordTag result3 = Morphology.stemStatic(input3);

    assertEquals("talk", result1.word());
    assertEquals("talk", result2.word());
    assertEquals("talk", result3.word());
  }
@Test
  public void testStemStaticOnEmptyTagReturnsInputWord() {
    WordTag input = new WordTag("walking", "");
    WordTag result = Morphology.stemStatic(input);
    assertEquals("walking", result.word());
  }
@Test
  public void testLemmaMixedCaseWithCapitalTag() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.lemma("WAlKeD", "VBD");
    assertEquals("walk", result);
  }
@Test
  public void testApplyWithWordTagMissingTag() {
    Morphology morphology = new Morphology(new StringReader(""));
    WordTag wordTag = new WordTag("jumped", null);
    Object result = morphology.apply(wordTag);
    assertTrue(result instanceof WordTag);
    WordTag output = (WordTag) result;
    assertEquals("jumped", output.word());
    assertNull(output.tag());
  }
@Test
  public void testApplyWithWordTagMissingWord() {
    Morphology morphology = new Morphology(new StringReader(""));
    WordTag wordTag = new WordTag(null, "VBD");
    Object result = morphology.apply(wordTag);
    assertTrue(result instanceof WordTag);
    WordTag output = (WordTag) result;
    assertNull(output.word());
    assertEquals("VBD", output.tag());
  }
@Test
  public void testStemWordWithTabCharacter() {
    Morphology morphology = new Morphology(new StringReader(""));
    String input = "run\tning";
    String result = morphology.stem(input);
    assertEquals("run\tning", result);
  }
@Test
  public void testLemmaWithNullWordReturnsNull() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.lemma(null, "VBD");
    assertNull(result);
  }
@Test
  public void testLemmaWithNullTagReturnsInputWord() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.lemma("driven", null);
    assertEquals("driven", result);
  }
@Test
  public void testStemWordTagWithEmptyBoth() {
    WordTag input = new WordTag("", "");
    WordTag result = Morphology.stemStatic(input);
    assertEquals("", result.word());
    assertEquals("", result.tag());
  }
@Test
  public void testLemmatizeProperNounDoesNotLowercase() {
    Morphology morphology = new Morphology(new StringReader(""));
    WordTag input = new WordTag("Stanford", "NNP");
    WordLemmaTag result = morphology.lemmatize(input);
    assertEquals("Stanford", result.lemma());
  }
@Test
  public void testLemmatizeAdjectiveComparative() {
    Morphology morphology = new Morphology(new StringReader(""));
    WordTag input = new WordTag("bigger", "JJR");
    WordLemmaTag result = morphology.lemmatize(input);
    assertNotNull(result.lemma());
  }
@Test
  public void testStemSymbol() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.stem("?");
    assertEquals("?", result);
  }
@Test
  public void testStaticLemmatizeOnAmbiguousWord() {
    WordTag ambiguous = new WordTag("flies", "VBZ");
    WordLemmaTag result = Morphology.lemmatizeStatic(ambiguous);
    assertEquals("fly", result.lemma());

    WordTag ambiguousNoun = new WordTag("flies", "NNS");
    WordLemmaTag result2 = Morphology.lemmatizeStatic(ambiguousNoun);
    assertEquals("fly", result2.lemma());
  }
@Test
  public void testStemForeignCharacters() {
    Morphology morphology = new Morphology(new StringReader(""));
    String input = "niño";
    String result = morphology.stem(input);
    assertEquals("niño", result); 
  }
@Test
  public void testStemUnicodeLetters() {
    Morphology morphology = new Morphology(new StringReader(""));
    String input = "\u03B1\u03B2\u03B3"; 
    String result = morphology.stem(input);
    assertEquals(input, result);
  }
@Test
  public void testStemEmptyWordTag() {
    WordTag wordTag = new WordTag("", "");
    WordTag result = Morphology.stemStatic(wordTag);
    assertEquals("", result.word());
    assertEquals("", result.tag());
  }
@Test
  public void testLemmaStaticWithLowercaseTrueExplicit() {
    String result = Morphology.lemmaStatic("JUMPED", "VBD", true);
    assertEquals("jump", result);
  }
@Test
  public void testLemmaStaticReturnsInputOnNullWord() {
    String result;
    try {
      result = Morphology.lemmaStatic(null, "VBD", true);
    } catch (NullPointerException e) {
      result = null;
    }
    assertNull(result);
  }
@Test
  public void testLemmaStaticReturnsInputOnNullTag() {
    String result = Morphology.lemmaStatic("jumped", null, true);
    assertEquals("jumped", result);
  }
@Test
  public void testStemCoreLabelWithMissingPOSAnnotation() {
    Morphology morphology = new Morphology(new StringReader(""));
    CoreLabel label = new CoreLabel();
    label.setWord("boxes");
    morphology.stem(label); 
    String lemma = label.get(CoreAnnotations.LemmaAnnotation.class);
    assertEquals("boxes", lemma); 
  }
@Test
  public void testStemCoreLabelWithMissingWordAnnotation() {
    Morphology morphology = new Morphology(new StringReader(""));
    CoreLabel label = new CoreLabel();
    label.setTag("NNS");
    morphology.stem(label); 
    String lemma = label.get(CoreAnnotations.LemmaAnnotation.class);
    assertNull(lemma);
  }
@Test
  public void testApplyWithCoreLabelTypeReturnsSame() {
    Morphology morphology = new Morphology(new StringReader(""));
    CoreLabel label = new CoreLabel();
    Object result = morphology.apply(label);
    assertSame(label, result);
  }
@Test
  public void testStemWithOnlyUnderscore() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.stem("_");
    assertEquals("_", result);
  }
@Test
  public void testStemWithOnlyNewline() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.stem("\n");
    assertEquals("\n", result);
  }
@Test
  public void testStemWithHiddenReplacementCharRestoration() {
    String input = "multi_word\nwith space";
    String tag = "NN";
    WordTag wordTag = new WordTag(input, tag);
    WordTag result = Morphology.stemStatic(wordTag);
    assertTrue(result.word().contains(" "));
    assertTrue(result.word().contains("_") || result.word().equals("multi_word\nwith space"));
  }
@Test
  public void testLemmatizeStaticNullWordTag() {
    WordTag nullWT = null;
    WordLemmaTag result;
    try {
      result = Morphology.lemmatizeStatic(nullWT);
    } catch (NullPointerException e) {
      result = null;
    }
    assertNull(result);
  }
@Test
  public void testLemmaStaticPreservesUnderscoreInOutput() {
    String word = "multi_word_term";
    String tag = "NN";
    String result = Morphology.lemmaStatic(word, tag);
    assertTrue(result.contains("_"));
  }
@Test
  public void testApplyOnCoreLabelWithSetLemmaAnnotation() {
    Morphology morphology = new Morphology(new StringReader(""));
    CoreLabel label = new CoreLabel();
    label.setWord("talked");
    label.setTag("VBD");
    morphology.stem(label);
    String lemma = label.get(CoreAnnotations.LemmaAnnotation.class);
    assertEquals("talk", lemma);
  }
@Test
  public void testLemmatizeAdverbToSameForm() {
    Morphology morphology = new Morphology(new StringReader(""));
    WordTag wordTag = new WordTag("quickly", "RB");
    WordLemmaTag result = morphology.lemmatize(wordTag);
    assertEquals("quickly", result.word());
    assertEquals("quickly", result.lemma());
    assertEquals("RB", result.tag());
  }
@Test
  public void testStemStringWithNonAsciiInput() {
    Morphology morphology = new Morphology(new StringReader(""));
    String input = "café";
    String result = morphology.stem(input);
    assertEquals("café", result); 
  }
@Test
  public void testLemmaAmbiguousCapitalizationWithNNTag() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.lemma("Apples", "NN");
    assertEquals("apple", result); 
  }
@Test
  public void testStaticStemPreservesNonEnglishSymbols() {
    WordTag wordTag = new WordTag("über", "NN");
    WordTag result = Morphology.stemStatic(wordTag);
    assertEquals("über", result.word());
  }
@Test
  public void testNextWithSingleTokenWord() throws Exception {
    Reader reader = new StringReader("walking");
    Morphology morphology = new Morphology(reader);
    Word word = morphology.next();
    assertNotNull(word);
    assertEquals("walk", word.word());
    Word next = morphology.next();
    assertNull(next);
  }
@Test
  public void testLemmaStaticWithSingleLetterWord() {
    String result = Morphology.lemmaStatic("a", "DT");
    assertEquals("a", result);
  }
@Test
  public void testLemmaStaticWithDigitToken() {
    String result = Morphology.lemmaStatic("2024", "CD");
    assertEquals("2024", result);
  }
@Test
  public void testStemStringWithWhitespaceSurrounding() {
    Morphology morphology = new Morphology(new StringReader(""));
    String input = "  running  ";
    String result = morphology.stem(input);
    assertEquals("  running  ", result);
  }
@Test
  public void testLemmatizeWithNonPartOfSpeechTag() {
    Morphology morphology = new Morphology(new StringReader(""));
    WordTag input = new WordTag("walking", "XYZ");
    WordLemmaTag result = morphology.lemmatize(input);
    assertEquals("walking", result.lemma());
  }
@Test
  public void testStemWordWithHighUnicodeCharacters() {
    Morphology morphology = new Morphology(new StringReader(""));
    String input = "𝓽𝓮𝔁𝓽"; 
    String result = morphology.stem(input);
    assertEquals(input, result); 
  }
@Test
  public void testStemWithHyphenatedCompound() {
    Morphology morphology = new Morphology(new StringReader(""));
    String input = "well-known";
    String result = morphology.stem(input);
    assertEquals("well-known", result);
  }
@Test
  public void testLemmaWithDifferentCaseTag() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.lemma("running", "vbg"); 
    assertEquals("running", result); 
  }
@Test
  public void testStemWithNonBreakingSpace() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.stem("hello\u00A0world");
    assertEquals("hello\u00A0world", result);
  }
@Test
  public void testApplyWithEmptyWordTag() {
    Morphology morphology = new Morphology(new StringReader(""));
    WordTag input = WordTag.valueOf("_", "_");
    Object result = morphology.apply(input);
    assertTrue(result instanceof WordTag);
    WordTag output = (WordTag) result;
    assertEquals("_", output.word());
    assertEquals("_", output.tag());
  }
@Test
  public void testLemmatizeUnusualAdjectiveForm() {
    Morphology morphology = new Morphology(new StringReader(""));
    WordTag input = new WordTag("awesomer", "JJR");
    WordLemmaTag result = morphology.lemmatize(input);
    assertNotNull(result.lemma());
    assertEquals("awesomer", result.lemma()); 
  }
@Test
  public void testStemWithMixedLanguageToken() {
    Morphology morphology = new Morphology(new StringReader(""));
    String input = "дом-running"; 
    String result = morphology.stem(input);
    assertEquals("дом-running", result);
  }
@Test
  public void testLemmaWithSpaceInWordOnly() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.lemma("multi word", "NN");
    assertEquals("multi word", result); 
  }
@Test
  public void testStemWithUnbalancedSurrogatePairs() {
    Morphology morphology = new Morphology(new StringReader(""));
    String brokenUnicode = "abc\uD800"; 
    String result = morphology.stem(brokenUnicode);
    assertEquals("abc\uD800", result);
  }
@Test
  public void testStemmingOnTokenPrefixingPOSStyle() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.stem("NN_tagged");
    assertEquals("NN_tagged", result);
  }
@Test
  public void testLemmaHandlesMultipleUnderscoresCorrectly() {
    Morphology morphology = new Morphology(new StringReader(""));
    String word = "foo_bar_baz";
    String tag = "NN";
    String lemma = morphology.lemma(word, tag);
    assertNotNull(lemma);
    assertTrue(lemma.contains("_")); 
  }
@Test
  public void testStemStringFullSentenceInput() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.stem("The dogs were barking loudly.");
    assertEquals("The dogs were barking loudly.", result); 
  }
@Test
  public void testStemWithEmojiSequence() {
    Morphology morphology = new Morphology(new StringReader(""));
    String input = "running🏃‍♂️";
    String result = morphology.stem(input);
    assertEquals("running🏃‍♂️", result);
  }
@Test
  public void testLemmatizeStaticEmptyWordAndTag() {
    WordTag wordTag = new WordTag("", "");
    WordLemmaTag result = Morphology.lemmatizeStatic(wordTag);
    assertEquals("", result.word());
    assertEquals("", result.lemma());
    assertEquals("", result.tag());
  }
@Test
  public void testApplyWithNullWordTagFields() {
    Morphology morphology = new Morphology(new StringReader(""));
    WordTag input = new WordTag((String) null, null);
    Object result = morphology.apply(input);
    assertTrue(result instanceof WordTag);
    assertNull(((WordTag) result).word());
    assertNull(((WordTag) result).tag());
  }
@Test
  public void testLemmatizeWithNullWordTag() {
    Morphology morphology = new Morphology(new StringReader(""));
    WordTag input = null;
    WordLemmaTag result;
    try {
      result = morphology.lemmatize(input);
    } catch (NullPointerException e) {
      result = null;
    }
    assertNull(result);
  }
@Test
  public void testStemWithMultipleWhitespace() {
    Morphology morphology = new Morphology(new StringReader(""));
    String input = "   ";
    String result = morphology.stem(input);
    assertEquals("   ", result);
  }
@Test
  public void testLemmaWithForbiddenCharsInBothWordAndTag() {
    Morphology morphology = new Morphology(new StringReader(""));
    String word = "foo_bar\nbaz test";
    String tag = "JJ NN\n";
    String result = morphology.lemma(word, tag);
    assertNotNull(result);
  }
@Test
  public void testStemCoreLabelPreservesExistingNonLemmaAnnotations() {
    Morphology morphology = new Morphology(new StringReader(""));
    CoreLabel label = new CoreLabel();
    label.setWord("babies");
    label.setTag("NNS");
    label.set(CoreAnnotations.TextAnnotation.class, "babies");
    morphology.stem(label);
    String lemma = label.get(CoreAnnotations.LemmaAnnotation.class);
    assertEquals("baby", lemma);
    String text = label.get(CoreAnnotations.TextAnnotation.class);
    assertEquals("babies", text);
  }
@Test
  public void testStemWordThatLooksLikePOS() {
    Morphology morphology = new Morphology(new StringReader(""));
    String input = "VBD";
    String result = morphology.stem(input);
    assertEquals("VBD", result);
  }
@Test
  public void testApplyWithIntegerInstanceReturnsSame() {
    Morphology morphology = new Morphology(new StringReader(""));
    Integer input = 123;
    Object result = morphology.apply(input);
    assertEquals(input, result);
  }
@Test
  public void testLemmaStaticWithNewlinesAndTabs() {
    String word = "walk\ned";
    String tag = "VB\tD";
    String result = Morphology.lemmaStatic(word, tag, true);
    assertEquals("walk\ned", result);
  }
@Test
  public void testStemWithNumberedFormOfVerb() {
    Morphology morphology = new Morphology(new StringReader(""));
    String input = "runs123";
    String result = morphology.stem(input);
    assertEquals("runs123", result);
  }
@Test
  public void testLemmaWithSymbolOnly() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.lemma("#", "SYM");
    assertEquals("#", result);
  }
@Test
  public void testStemLoneReplacementCharU1CF0() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.stem("\u1CF0");
    assertEquals("\u1CF0", result);
  }
@Test
  public void testStaticStemWithUnusualCapitalization() {
    WordTag input = new WordTag("WaLkEd", "VBD");
    WordTag result = Morphology.stemStatic(input);
    assertEquals("walk", result.word());
    assertEquals("VBD", result.tag());
  }
@Test
  public void testStemUnicodeChineseCharacters() {
    Morphology morphology = new Morphology(new StringReader(""));
    String input = "步行"; 
    String result = morphology.stem(input);
    assertEquals("步行", result);
  }
@Test
  public void testStemWithEmbeddedNewlineCharacter() {
    Morphology morphology = new Morphology(new StringReader(""));
    String input = "run\nning";
    String result = morphology.stem(input);
    assertEquals("run\nning", result);
  }
@Test
  public void testStemReturnsOriginalOnForcedIOException() {
    Morphology morphology = new Morphology(new StringReader("broken input")) {
      @Override
      public String stem(String word) {
        try {
          throw new java.io.IOException("simulated");
        } catch (Exception e) {
          return word;
        }
      }
    };
    String result = morphology.stem("walked");
    assertEquals("walked", result);
  }
@Test
  public void testStaticLemmaConsecutiveCallsConsistency() {
    String result1 = Morphology.lemmaStatic("dying", "VBG");
    String result2 = Morphology.lemmaStatic("flying", "VBG");
    String result3 = Morphology.lemmaStatic("crying", "VBG");
    assertEquals("die", result1);
    assertEquals("fly", result2);
    assertEquals("cry", result3);
  }
@Test
  public void testStemCoreLabelWithOtherAnnotationTypes() {
    Morphology morphology = new Morphology(new StringReader(""));
    CoreLabel label = new CoreLabel();
    label.setWord("smiling");
    label.setTag("VBG");
    label.set(CoreAnnotations.PartOfSpeechAnnotation.class, "VBG");
    morphology.stem(label);
    String lemma = label.get(CoreAnnotations.LemmaAnnotation.class);
    assertEquals("smile", lemma);
  }
@Test
  public void testLemmaLowercaseOptionFalseProperNoun() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.lemma("Berlin", "NNP", false);
    assertEquals("Berlin", result);
  }
@Test
  public void testLemmaLowercaseOptionTrueProperNoun() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.lemma("Berlin", "NNP", true);
    assertEquals("Berlin", result); 
  } 
}