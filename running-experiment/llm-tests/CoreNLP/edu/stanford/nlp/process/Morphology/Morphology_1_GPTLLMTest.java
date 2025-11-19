package edu.stanford.nlp.process;

import edu.stanford.nlp.ling.*;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import static org.junit.Assert.*;

public class Morphology_1_GPTLLMTest {

 @Test
  public void testStem_regularNoun_returnsSingular() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.stem("dogs");
    assertEquals("dog", result);
  }
@Test
  public void testStem_verbGerund_returnsBaseForm() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.stem("running");
    assertEquals("run", result);
  }
@Test
  public void testStem_pastTense_returnsBaseForm() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.stem("walked");
    assertEquals("walk", result);
  }
@Test
  public void testStem_adverb_returnsAdjective() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.stem("quickly");
    assertEquals("quick", result);
  }
@Test
  public void testStem_adjectiveComparative_returnsBaseForm() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.stem("faster");
    assertEquals("fast", result);
  }
@Test
  public void testStem_emptyString_returnsEmpty() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.stem("");
    assertEquals("", result);
  }
@Test
  public void testStem_invalidInput_withSpace_returnsOriginal() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.stem("hard working");
    assertEquals("hard working", result);
  }
@Test
  public void testStem_invalidInput_withUnderscore_returnsOriginal() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.stem("multi_word");
    assertEquals("multi_word", result);
  }
@Test
  public void testStem_invalidInput_withNewline_returnsOriginal() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.stem("newline\nitem");
    assertEquals("newline\nitem", result);
  }
@Test
  public void testLemma_withLowercasing_returnsLowercaseLemma() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.lemma("Dogs", "NNS", true);
    assertEquals("dog", result);
  }
@Test
  public void testLemma_withoutLowercasing_returnsCasedLemma() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.lemma("Dogs", "NNS", false);
    assertEquals("Dog", result);
  }
@Test
  public void testLemma_properNoun_returnsOriginal() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.lemma("Stanford", "NNP");
    assertEquals("Stanford", result);
  }
@Test
  public void testStem_CoreLabel_setsLemmaAnnotation() {
    Morphology morphology = new Morphology(new StringReader(""));
    CoreLabel label = new CoreLabel();
    label.setWord("cats");
    label.setTag("NNS");
    morphology.stem(label);
    assertEquals("cat", label.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testStem_CoreLabel_withCustomAnnotation_setsCorrectLemma() {
    Morphology morphology = new Morphology(new StringReader(""));
    CoreLabel label = new CoreLabel();
    label.setWord("walking");
    label.setTag("VBG");
    morphology.stem(label, CoreAnnotations.LemmaAnnotation.class);
    assertEquals("walk", label.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testStem_Word_returnsStemmedWord() {
    Morphology morphology = new Morphology(new StringReader(""));
    Word input = new Word("dogs");
    Word result = morphology.stem(input);
    assertEquals("dog", result.word());
  }
@Test
  public void testApply_withWordTag_returnsStemmedWordTag() {
    Morphology morphology = new Morphology(new StringReader(""));
    WordTag input = new WordTag("horses", "NNS");
    Object result = morphology.apply(input);
    assertTrue(result instanceof WordTag);
    WordTag actual = (WordTag) result;
    assertEquals("horse", actual.word());
    assertEquals("NNS", actual.tag());
  }
@Test
  public void testApply_withWord_returnsStemmedWord() {
    Morphology morphology = new Morphology(new StringReader(""));
    Word input = new Word("running");
    Object result = morphology.apply(input);
    assertTrue(result instanceof Word);
    Word out = (Word) result;
    assertEquals("run", out.word());
  }
@Test
  public void testApply_withUnknownType_returnsInput() {
    Morphology morphology = new Morphology(new StringReader(""));
    Object result = morphology.apply("someString");
    assertEquals("someString", result);
  }
@Test
  public void testLemmatize_WordTag_returnsWordLemmaTag() {
    Morphology morphology = new Morphology(new StringReader(""));
    WordTag input = new WordTag("cars", "NNS");
    WordLemmaTag result = morphology.lemmatize(input);
    assertEquals("cars", result.word());
    assertEquals("car", result.lemma());
    assertEquals("NNS", result.tag());
  }
@Test
  public void testLemmatizeStatic_returnsCorrectWordLemmaTag() {
    WordTag wordTag = new WordTag("running", "VBG");
    WordLemmaTag result = Morphology.lemmatizeStatic(wordTag);
    assertEquals("running", result.word());
    assertEquals("run", result.lemma());
    assertEquals("VBG", result.tag());
  }
@Test
  public void testStemStatic_withWordAndTag_returnsStemmedWordTag() {
    WordTag result = Morphology.stemStatic("bigger", "JJR");
    assertEquals("big", result.word());
    assertEquals("JJR", result.tag());
  }
@Test
  public void testStemStatic_withWordTag_returnsStemmedTag() {
    WordTag input = new WordTag("swimming", "VBG");
    WordTag result = Morphology.stemStatic(input);
    assertEquals("swim", result.word());
    assertEquals("VBG", result.tag());
  }
@Test
  public void testLemmaStatic_withDefaultLowercase_returnsLowercase() {
    String result = Morphology.lemmaStatic("Houses", "NNS");
    assertEquals("house", result);
  }
@Test
  public void testLemmaStatic_withLowercaseFalse_preservesCase() {
    String result = Morphology.lemmaStatic("Houses", "NNS", false);
    assertEquals("House", result);
  }
@Test
  public void testLemma_forbiddenCharacters_underscorePreserved() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.lemma("some_phrase", "NN");
    assertEquals("some_phrase", result);
  }
@Test
  public void testLemma_IOError_returnsOriginalWord() {
    Morphology failing = new Morphology(new Reader() {
      @Override
      public int read(char[] cbuf, int off, int len) throws IOException {
        throw new IOException("fail");
      }

      @Override
      public void close() throws IOException {}
    });
    String result = failing.lemma("failword", "NN");
    assertEquals("failword", result);
  }
@Test
  public void testStem_IOError_returnsOriginalWord() {
    Morphology failing = new Morphology(new Reader() {
      @Override
      public int read(char[] cbuf, int off, int len) throws IOException {
        throw new IOException("error");
      }

      @Override
      public void close() throws IOException {}
    });
    String stemmed = failing.stem("error");
    assertEquals("error", stemmed);
  }
@Test
  public void testNext_returnsNextValidWord() throws IOException {
    Morphology morph = new Morphology(new StringReader("runner\n"));
    Word word = morph.next();
    assertNotNull(word);
    assertEquals("runner", word.word());
  }
@Test
  public void testNext_returnsNullAtEOF() throws IOException {
    Morphology morph = new Morphology(new StringReader(""));
    Word word = morph.next();
    assertNull(word);
  }
@Test
  public void testStem_wordWithTabs_returnsOriginal() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.stem("tab\tword");
    assertEquals("tab\tword", result);
  }
@Test
  public void testStem_wordWithMixedWhitespace_returnsOriginal() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.stem("hello world\tagain\n");
    assertEquals("hello world\tagain\n", result);
  }
@Test
  public void testLemma_wordStartingWithUnderscore_returnsTransformedProperly() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.lemma("_underscoreStart", "NN");
    assertEquals("_underscoreStart", result); 
  }
@Test
  public void testLemma_wordContainingUnicodeSeparators() {
    Morphology morphology = new Morphology(new StringReader(""));
    String input = "part\u2028word"; 
    String result = morphology.lemma(input, "NN");
    assertEquals("part\u2028word", result); 
  }
@Test
  public void testStem_apostropheInWord_preservesIt() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.stem("doesn't");
    assertEquals("doesn't", result);
  }
@Test
  public void testLemma_spaceBeforeUnderscore_returnsOriginal() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.lemma("foo _bar", "NN");
    assertEquals("foo _bar", result);
  }
@Test
  public void testStem_uppercaseWord_convertedToLowercaseUnlessOptionDisabled() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.lemma("WALKED", "VBD", true);
    assertEquals("walk", result);
  }
@Test
  public void testLemma_properNounWithMixedCase_isPreserved() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.lemma("McDonald's", "NNP");
    assertEquals("McDonald's", result);
  }
@Test
  public void testApply_withNullInput_returnsNull() {
    Morphology morphology = new Morphology(new StringReader(""));
    Object result = morphology.apply(null);
    assertNull(result);
  }
@Test
  public void testStem_CoreLabel_withoutTag_doesNotThrowError() {
    Morphology morphology = new Morphology(new StringReader(""));
    CoreLabel label = new CoreLabel();
    label.setWord("cats");
    morphology.stem(label);
    assertNotNull(label.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testStem_wordIsAlreadyBaseForm_returnsSame() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.stem("run");
    assertEquals("run", result);
  }
@Test
  public void testApply_withWordTagEmptyWord_returnsEmpty() {
    Morphology morphology = new Morphology(new StringReader(""));
    WordTag input = new WordTag("", "NN");
    Object output = morphology.apply(input);
    assertTrue(output instanceof WordTag);
    WordTag result = (WordTag) output;
    assertEquals("", result.word());
    assertEquals("NN", result.tag());
  }
@Test
  public void testLemma_multipleUnderscoresPreservedInOutput() {
    Morphology morphology = new Morphology(new StringReader(""));
    String input = "multi_word_phrase";
    String result = morphology.lemma(input, "NN");
    assertEquals("multi_word_phrase", result);
  }
@Test
  public void testConstructorWithFlags_acceptsDifferentValues() {
    Morphology morph = new Morphology(new StringReader(""), 3);
    String lemma = morph.lemma("WALKING", "VBG", true);
    assertEquals("walk", lemma);
  }
@Test
  public void testStem_withNullString_returnsNull() {
    Morphology morph = new Morphology(new StringReader(""));
    String result = morph.stem((String) null);
    assertNull(result);
  }
@Test
  public void testNext_withWordFollowedByEOF_returnsOnlyOnce() throws IOException {
    Morphology morph = new Morphology(new StringReader("word"));
    assertNotNull(morph.next());
    assertNull(morph.next());
  }
@Test
  public void testStem_numericToken_returnsSameNumber() {
    Morphology morph = new Morphology(new StringReader(""));
    String result = morph.stem("2024");
    assertEquals("2024", result);
  }
@Test
  public void testStem_wordContainingSymbol_returnsUnchanged() {
    Morphology morph = new Morphology(new StringReader(""));
    String result = morph.stem("?!@word");
    assertEquals("?!@word", result);
  }
@Test
  public void testStaticLemma_preservesSpaceInWord() {
    String result = Morphology.lemmaStatic("hello world", "NN", true);
    assertEquals("hello world", result);
  }
@Test
  public void testStem_whitespaceOnly_returnsWhitespace() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.stem("   ");
    assertEquals("   ", result);
  }
@Test
  public void testLemma_forWordWithNewlineOnly_returnsSame() {
    Morphology morphology = new Morphology(new StringReader(""));
    String input = "\n";
    String result = morphology.lemma(input, "NN");
    assertEquals("\n", result);
  }
@Test
  public void testStem_wordWithMultipleSequentialUnderscores_returnsOriginal() {
    Morphology morphology = new Morphology(new StringReader(""));
    String input = "multi__underscore";
    String result = morphology.stem(input);
    assertEquals("multi__underscore", result);
  }
@Test
  public void testStem_nullWordObject_returnsNull() {
    Morphology morphology = new Morphology(new StringReader(""));
    Word input = null;
    Word result = morphology.stem(input);
    assertNull(result);
  }
@Test
  public void testStem_Object_isUnknownType_returnsSame() {
    Morphology morphology = new Morphology(new StringReader(""));
    Integer input = 12345;
    Object result = morphology.apply(input);
    assertEquals(input, result);
  }
@Test
  public void testStaticStem_withImproperTagFormat_stillReturnsWord() {
    WordTag input = new WordTag("laughing", "");
    WordTag result = Morphology.stemStatic(input);
    assertEquals("laughing", result.word()); 
    assertEquals("", result.tag());
  }
@Test
  public void testLemmatizeCoreLabel_withMissingWord_returnsNullAsLemma() {
    Morphology morphology = new Morphology(new StringReader(""));
    CoreLabel label = new CoreLabel();
    label.setTag("NNS");
    morphology.stem(label);
    String lemma = label.get(CoreAnnotations.LemmaAnnotation.class);
    assertNull(lemma);
  }
@Test
  public void testLemmatizeCoreLabel_withMissingTag_returnsOriginalWordAsLemma() {
    Morphology morphology = new Morphology(new StringReader(""));
    CoreLabel label = new CoreLabel();
    label.setWord("horses");
    morphology.stem(label);
    String lemma = label.get(CoreAnnotations.LemmaAnnotation.class);
    assertEquals("horses", lemma); 
  }
@Test
  public void testLemmatize_NumericTagHandledCorrectly() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.lemma("walked", "123", true);
    assertEquals("walked", result); 
  }
@Test
  public void testLemmatize_specialUnicodeChars_survivesTransformation() {
    Morphology morphology = new Morphology(new StringReader(""));
    String input = "unicode\u1CF0test";
    String result = morphology.lemma(input, "NN");
    assertEquals("unicode\u1CF0test", result);
  }
@Test
  public void testApply_wordTagWithSpaceInWord_handlesCorrectly() {
    Morphology morphology = new Morphology(new StringReader(""));
    WordTag input = new WordTag("some value", "NN");
    Object result = morphology.apply(input);
    assertTrue(result instanceof WordTag);
    WordTag actual = (WordTag) result;
    assertEquals("some value", actual.word());
  }
@Test
  public void testApply_wordTagWithSpaceInTag_returnsWordWithSameTag() {
    Morphology morphology = new Morphology(new StringReader(""));
    WordTag input = new WordTag("value", "N N");
    Object result = morphology.apply(input);
    assertTrue(result instanceof WordTag);
    WordTag tag = (WordTag) result;
    assertEquals("value", tag.word());
    assertEquals("N N", tag.tag());
  }
@Test
  public void testNext_deliversMultipleTokensSequentially() throws IOException {
    Morphology morphology = new Morphology(new StringReader("cat\ndog\n"));
    Word first = morphology.next();
    Word second = morphology.next();
    Word third = morphology.next();
    assertNotNull(first);
    assertNotNull(second);
    assertNull(third);
    assertEquals("cat", first.word());
    assertEquals("dog", second.word());
  }
@Test
  public void testDefaultConstructor_stillParsesValidStem() throws IOException {
    Morphology morphology = new Morphology();
    morphology.stem(new Word("ran")); 
  }
@Test
  public void testLemmatizeStatic_withEmptyWord_returnsEmpty() {
    WordTag input = new WordTag("", "NN");
    WordLemmaTag result = Morphology.lemmatizeStatic(input);
    assertEquals("", result.word());
    assertEquals("", result.lemma());
    assertEquals("NN", result.tag());
  }
@Test
  public void testLemmatize_withEmptyTagOnWordTag_returnsWordAsLemma() {
    Morphology morphology = new Morphology(new StringReader(""));
    WordTag input = new WordTag("play", "");
    WordLemmaTag result = morphology.lemmatize(input);
    assertEquals("play", result.lemma());
  }
@Test
  public void testStemStatic_withEmptyWord_returnsUnchanged() {
    WordTag result = Morphology.stemStatic("", "NN");
    assertEquals("", result.word());
  }
@Test
  public void testStaticLemma_nullWordHandledGracefully() {
    String result = Morphology.lemmaStatic(null, "NN", true);
    assertNull(result);
  }
@Test
  public void testStemNullCharInInput_returnsOriginal() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.stem("foo\u0000bar");
    assertEquals("foo\u0000bar", result);
  }
@Test
  public void testStemNewlineOnly_returnsNewline() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.stem("\n");
    assertEquals("\n", result);
  }
@Test
  public void testLemmatize_emptyTagAndWord_returnsWordAsIs() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.lemma("", "");
    assertEquals("", result);
  }
@Test
  public void testStem_customLexerFlags_zeroOption() {
    Morphology morphology = new Morphology(new StringReader(""), 0);
    String result = morphology.lemma("LOOKED", "VBD", true);
    assertEquals("look", result);
  }
@Test
  public void testStem_latinAndNonAsciiWord_transformsProperly() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.stem("façade");
    assertEquals("façade", result); 
  }
@Test
  public void testStem_emojiCharacter_returnsUnchanged() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.stem("😊");
    assertEquals("😊", result);
  }
@Test
  public void testStem_specialDelimiterReplacementAndReversion() {
    Morphology morphology = new Morphology(new StringReader(""));
    String input = "a_b c\nd";
    String result = morphology.lemma(input, "NN");
    assertEquals("a_b c\nd", result); 
  }
@Test
  public void testLemmatize_appliesReplacementAndRestoresCorrectly() {
    Morphology morphology = new Morphology(new StringReader(""));
    String input = "multi_part\nword";
    String result = morphology.lemma(input, "NN");
    assertEquals("multi_part\nword", result);
  }
@Test
  public void testLemmatize_uppercaseNNP_preservesCase() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.lemma("NASA", "NNP");
    assertEquals("NASA", result);
  }
@Test
  public void testStemStatic_multipleCalls_stillReturnsCorrectValues() {
    WordTag result1 = Morphology.stemStatic("jumped", "VBD");
    WordTag result2 = Morphology.stemStatic("houses", "NNS");
    WordTag result3 = Morphology.stemStatic("faster", "JJR");
    assertEquals("jump", result1.word());
    assertEquals("house", result2.word());
    assertEquals("fast", result3.word());
  }
@Test
  public void testStemStatic_concurrentCalls_mustReturnCorrectResultEachTime() {
    WordTag resultA = Morphology.stemStatic("reading", "VBG");
    WordTag resultB = Morphology.stemStatic("bigger", "JJR");
    assertEquals("read", resultA.word());
    assertEquals("big", resultB.word());
  }
@Test
  public void testLemmatize_missingWordAndTag_preservesNullSafety() {
    Morphology morphology = new Morphology(new StringReader(""));
    CoreLabel label = new CoreLabel();
    morphology.stem(label);
    assertNull(label.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testApply_withWordTagHavingNullValues_returnsUnchangedOrValidResult() {
    Morphology morphology = new Morphology(new StringReader(""));
    WordTag tag = new WordTag((String) null, null);
    Object result = morphology.apply(tag);
    assertTrue(result instanceof WordTag);
    WordTag cast = (WordTag) result;
    assertNull(cast.word());
    assertNull(cast.tag());
  }
@Test
  public void testStem_wordContainingReplacementMarker_returnsCorrectly() {
    Morphology morphology = new Morphology(new StringReader(""));
    String input = "test\u1CF0replacement"; 
    String result = morphology.lemma(input, "NN");
    assertEquals("test\u1CF0replacement", result);
  }
@Test
  public void testNext_inputWithWhitespaceTokens_returnsOneThenNull() throws IOException {
    Morphology morphology = new Morphology(new StringReader("   \n"));
    Word word = morphology.next();
    Word second = morphology.next();
    assertNotNull(word);
    assertNull(second);
  }
@Test
  public void testLemmatizeWhitespaceWord_returnsWhitespace() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.lemma("   ", "NN");
    assertEquals("   ", result);
  }
@Test
  public void testLemmatize_withNonAlphabeticCharactersInWord_returnsSame() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.lemma("!@#$%^&*", "NN");
    assertEquals("!@#$%^&*", result);
  }
@Test
  public void testLemmatizeStatic_withNullWordAndNullTag_returnsNullOrNoCrash() {
    String result = Morphology.lemmaStatic(null, null, true);
    assertNull(result);
  }
@Test
  public void testLemmatizeWithFalseLowercase_forcesOriginalCasing() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.lemma("RUNNING", "VBG", false);
    assertEquals("RUN", result); 
  }
@Test
  public void testLemmatize_wordWithTrailingSpace_preservedAfterRestoration() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.lemma("dogs ", "NNS");
    assertEquals("dog ", result);
  }
@Test
  public void testStem_emptyWordTag_stemStaticReturnsEmpty() {
    WordTag tag = new WordTag("", "");
    WordTag result = Morphology.stemStatic(tag);
    assertEquals("", result.word());
    assertEquals("", result.tag());
  }
@Test
  public void testApply_withUninitializedCoreLabel_returnsNullLemma() {
    Morphology morphology = new Morphology(new StringReader(""));
    CoreLabel label = new CoreLabel();
    morphology.stem(label);
    assertNull(label.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testApply_withWordTagMissingTag_returnsOriginalWord() {
    Morphology morphology = new Morphology(new StringReader(""));
    WordTag input = new WordTag("walked", null);
    Object result = morphology.apply(input);
    assertTrue(result instanceof WordTag);
    WordTag wt = (WordTag) result;
    assertEquals("walked", wt.word());
    assertNull(wt.tag());
  }
@Test
  public void testLemma_wordWithPunctuationOnly_returnsSame() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.lemma("!!!", "NN");
    assertEquals("!!!", result);
  }
@Test
  public void testStem_wordWithLeadingAndTrailingSpaces_preservedCorrectly() {
    Morphology morphology = new Morphology(new StringReader(""));
    String input = "  cats  ";
    String result = morphology.stem(input);
    assertEquals("  cats  ", result);
  }
@Test
  public void testLemmatize_wordWithTabCharacter_returnsSame() {
    Morphology morphology = new Morphology(new StringReader(""));
    String input = "tab\tinword";
    String result = morphology.lemma(input, "NN");
    assertEquals("tab\tinword", result);
  }
@Test
  public void testStemStatic_multipleWords_enforcesGlobalMutexBehavior() {
    WordTag first = Morphology.stemStatic("jumps", "VBZ");
    WordTag second = Morphology.stemStatic("flying", "VBG");
    assertEquals("jump", first.word());
    assertEquals("fly", second.word());
  }
@Test
  public void testStemMixedCaseProperNoun_preservesCasingWithNNP() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.lemma("StanFord", "NNP", true);
    assertEquals("StanFord", result);
  }
@Test
  public void testLemma_wordWithMultipleSpaces_returnsResultUnchanged() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.lemma("multi   space", "NN");
    assertEquals("multi   space", result);
  }
@Test
  public void testStem_validNounWithNoChange_returnsSame() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.stem("data");
    assertEquals("data", result);
  }
@Test
  public void testLemmatize_tagWithSpecialCharacter_handledGracefully() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.lemma("coded", "$VB");
    assertEquals("coded", result);  
  }
@Test
  public void testLemma_wordWithNumbers_returnsSameWhenNotInflected() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.lemma("2019s", "NNS");
    assertEquals("2019", result);
  }
@Test
  public void testStaticStem_doesNotThrowOnEmptyWordOrTag() {
    WordTag result = Morphology.stemStatic("", "");
    assertNotNull(result);
    assertEquals("", result.word());
    assertEquals("", result.tag());
  }
@Test
  public void testNext_multipleCallsAfterEOF_returnsNull() throws IOException {
    Morphology morphology = new Morphology(new StringReader("fox\n"));
    morphology.next(); 
    morphology.next(); 
    Word result = morphology.next();
    assertNull(result);
  }
@Test
  public void testStem_numericToken_combinedWithLetters_returnsSameUnlessMatched() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.stem("word123");
    assertEquals("word123", result);
  }
@Test
  public void testApplyWithWhitespaceOnlyWord_returnsSameInWord() {
    Morphology morphology = new Morphology(new StringReader(""));
    Word input = new Word(" ");
    Object result = morphology.apply(input);
    assertTrue(result instanceof Word);
    assertEquals(" ", ((Word) result).word());
  }
@Test
  public void testLemmatizeStatic_lowercaseFalseForPronoun_preservesCase() {
    String result = Morphology.lemmaStatic("He", "PRP", false);
    assertEquals("He", result);
  }
@Test
  public void testLemmatize_wordWithQuotes_returnsSame() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.lemma("\"quoted\"", "NN");
    assertEquals("\"quoted\"", result);
  }
@Test
  public void testStem_wordWithHyphen_returnsSameIfUnmatched() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.stem("well-known");
    assertEquals("well-known", result);
  }
@Test
  public void testLemma_nullWordAndNullTag_returnsNull() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.lemma(null, null);
    assertNull(result);
  }
@Test
  public void testLemma_wordIsCapitalizedCommonNoun_lowercaseFlagTrue_returnsLowercase() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.lemma("Dogs", "NNS", true);
    assertEquals("dog", result);
  }
@Test
  public void testRepeatedLemma_callWithSameInput_returnsSameValue() {
    Morphology morphology = new Morphology(new StringReader(""));
    String first = morphology.lemma("ran", "VBD");
    String second = morphology.lemma("ran", "VBD");
    assertEquals(first, second);
  }
@Test
  public void testLemmatize_withVeryLongWord_returnsTruncatedOrSame() {
    Morphology morphology = new Morphology(new StringReader(""));
    String input = "a";
    String result = morphology.lemma(input, "NN");
    assertEquals(input, result);
  }
@Test
  public void testLemmatize_mixedWordAndNumbers_suffixPreserved() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.lemma("token42", "NN");
    assertEquals("token42", result);
  }
@Test
  public void testStaticStem_multipleInvocations_shareStaticInstanceWithoutError() {
    WordTag one = Morphology.stemStatic("played", "VBD");
    WordTag two = Morphology.stemStatic("jumps", "VBZ");
    assertEquals("play", one.word());
    assertEquals("jump", two.word());
  }
@Test
  public void testStem_includesMultipleForbiddenChars_performsAllReplacements() {
    Morphology morphology = new Morphology(new StringReader(""));
    String input = "multi_line word_with_symbols\n";
    String result = morphology.lemma(input, "NN");
    assertEquals("multi_line word_with_symbols\n", result);
  }
@Test
  public void testStaticStem_emptyTag_returnsSameWordUnchanged() {
    WordTag result = Morphology.stemStatic("dreaming", "");
    assertEquals("dreaming", result.word());
    assertEquals("", result.tag());
  }
@Test
  public void testApply_withEmptyCoreLabel_returnsNoOutput() {
    Morphology morphology = new Morphology(new StringReader(""));
    CoreLabel label = new CoreLabel();
    morphology.stem(label);
    assertNull(label.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testApply_withOnlyWhitespaceWord_returnsSameAsLemma() {
    Morphology morphology = new Morphology(new StringReader(""));
    WordTag tag = new WordTag("   ", "NN");
    Object result = morphology.apply(tag);
    assertTrue(result instanceof WordTag);
    WordTag actual = (WordTag) result;
    assertEquals("   ", actual.word());
    assertEquals("NN", actual.tag());
  }
@Test
  public void testStem_wordWithCamelCase_notLoweredForProperNoun() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.lemma("CamelCase", "NNP", true);
    assertEquals("CamelCase", result);
  }
@Test
  public void testLemmatizeStatic_calledAfterInstanceLemmatize_returnsCorrectly() {
    Morphology morph = new Morphology(new StringReader(""));
    String inst = morph.lemma("houses", "NNS");
    WordLemmaTag result = Morphology.lemmatizeStatic(new WordTag("flies", "NNS"));
    assertEquals("flies", result.word());
    assertEquals("fly", result.lemma());
  }
@Test
  public void testStemObjectCalledWithCoreLabel_usesTagAndWordProperly() {
    Morphology morphology = new Morphology(new StringReader(""));
    CoreLabel label = new CoreLabel();
    label.setWord("running");
    label.setTag("VBG");
    morphology.stem(label);
    String lemma = label.get(CoreAnnotations.LemmaAnnotation.class);
    assertEquals("run", lemma);
  }
@Test
  public void testStaticLemma_malformedPOS_returnsInputUnchanged() {
    String result = Morphology.lemmaStatic("something", "???", true);
    assertEquals("something", result);
  }
@Test
  public void testLemmatize_staticWithLowercasingForAdverb_returnsStemmedWord() {
    String result = Morphology.lemmaStatic("QUICKLY", "RB", true);
    assertEquals("quick", result);
  }
@Test
  public void testStem_invalidPOSCombo_stillReturnsSomething() {
    Morphology morph = new Morphology(new StringReader(""));
    String result = morph.lemma("playing", "@@@");
    assertEquals("playing", result);
  }
@Test
  public void testNext_whenReaderContainsOnlyWhitespace_returnsWordOrNull() throws IOException {
    Morphology morph = new Morphology(new StringReader("   "));
    Word w = morph.next();
    assertNotNull(w);
    Word second = morph.next();
    assertNull(second);
  }
@Test
  public void testStaticStem_withCompoundWord_usesWholeString() {
    WordTag result = Morphology.stemStatic("check-in", "NN");
    assertEquals("check-in", result.word());
  }
@Test
  public void testLemma_withCommaSymbol_returnsComma() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.lemma(",", ",");
    assertEquals(",", result);
  }
@Test
  public void testStem_wordWithUnicodeControlChar_returnsUnchanged() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.stem("test\u0001word");
    assertEquals("test\u0001word", result);
  }
@Test
  public void testInitializeStaticLexer_thenUseStaticMethod() {
    WordTag result = Morphology.stemStatic("runners", "NNS");
    assertNotNull(result);
    assertEquals("runner", result.word());
  }
@Test
  public void testApply_withUnknownObjectType_returnsInput() {
    Morphology morphology = new Morphology(new StringReader(""));
    Object input = new Object();
    Object output = morphology.apply(input);
    assertEquals(input, output);
  }
@Test
  public void testLemmatize_withTagNull_returnsOriginal() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.lemma("apples", null);
    assertEquals("apples", result);
  }
@Test
  public void testLemmatize_withEmptyTag_returnsOriginal() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.lemma("apples", "");
    assertEquals("apples", result);
  }
@Test
  public void testLemmatize_withEmptyWord_returnsEmpty() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.lemma("", "NNS");
    assertEquals("", result);
  }
@Test
  public void testLemma_wordWithNewline_tab_and_space_substitutesAndRestoresCorrectly() {
    Morphology morphology = new Morphology(new StringReader(""));
    String input = "aaa_bbb ccc\nddd";
    String result = morphology.lemma(input, "NN");
    assertEquals("aaa_bbb ccc\nddd", result);
  }
@Test
  public void testStemStatic_calledMultipleTimes_returnsEachCorrectly() {
    WordTag first = Morphology.stemStatic("puppies", "NNS");
    WordTag second = Morphology.stemStatic("jumped", "VBD");
    WordTag third = Morphology.stemStatic("happier", "JJR");
    assertEquals("puppy", first.word());
    assertEquals("jump", second.word());
    assertEquals("happy", third.word());
  }
@Test
  public void testStem_withNullTag_returnsOriginal() {
    Morphology morph = new Morphology(new StringReader(""));
    String result = morph.lemma("words", null);
    assertEquals("words", result);
  }
@Test
  public void testLemmatizeStatic_tagWithWhitespaceAndSymbols_returnsWordUnchanged() {
    WordLemmaTag result = Morphology.lemmatizeStatic(new WordTag("foo", "  @ "));
    assertEquals("foo", result.lemma());
  }
@Test
  public void testStem_staticWithImproperTokenSyntax_returnsFallback() {
    WordTag malformed = WordTag.valueOf("some_input", "*");
    WordTag result = Morphology.stemStatic(malformed);
    assertEquals("some_input", result.word());
  }
@Test
  public void testNext_tokenWithUnderscorePosSeparator_returnsParsedWord() throws IOException {
    Morphology morph = new Morphology(new StringReader("dog_NN\n"));
    Word token = morph.next();
    assertNotNull(token);
    assertEquals("dog", token.word());
  }
@Test
  public void testApply_withNonInflectedWordTag_returnsSameWord() {
    Morphology morphology = new Morphology(new StringReader(""));
    WordTag tag = new WordTag("book", "NN");
    Object result = morphology.apply(tag);
    assertTrue(result instanceof WordTag);
    WordTag wordTag = (WordTag) result;
    assertEquals("book", wordTag.word());
  }
@Test
  public void testLemma_withDigitsInWord_returnsSameOrStemmed() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.lemma("car99s", "NNS");
    assertEquals("car99", result);
  }
@Test
  public void testStaticLemma_withNullWord_returnsNull() {
    String result = Morphology.lemmaStatic(null, "NN");
    assertNull(result);
  }
@Test
  public void testStem_inputExceptionDuringLexerStillReturnsOriginal() {
    Morphology failing = new Morphology(new Reader() {
      @Override
      public int read(char[] cbuf, int off, int len) throws IOException {
        throw new IOException("forced read failure");
      }
      @Override
      public void close() throws IOException {}
    });
    String result = failing.stem("failureWord");
    assertEquals("failureWord", result);
  }
@Test
  public void testStem_CoreLabelWithMissingWord_setsNullLemma() {
    Morphology morphology = new Morphology(new StringReader(""));
    CoreLabel label = new CoreLabel();
    label.setTag("VB");
    morphology.stem(label);
    assertNull(label.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testStaticStem_withProperNounCase_returnsExactCasing() {
    WordTag tag = Morphology.stemStatic("Microsoft", "NNP");
    assertEquals("Microsoft", tag.word());
  }
@Test
  public void testStem_CoreLabelWithMissingTag_setsNullLemma() {
    Morphology morphology = new Morphology(new StringReader(""));
    CoreLabel label = new CoreLabel();
    label.setWord("jumps");
    morphology.stem(label);
    assertEquals("jumps", label.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testApply_withEmptyWordObject_returnsEmptyWord() {
    Morphology morphology = new Morphology(new StringReader(""));
    Word word = new Word("");
    Object result = morphology.apply(word);
    assertTrue(result instanceof Word);
    assertEquals("", ((Word) result).word());
  }
@Test
  public void testApply_withEmptyStringReturnsSame() {
    Morphology morphology = new Morphology(new StringReader(""));
    Object output = morphology.apply("");
    assertEquals("", output);
  } 
}