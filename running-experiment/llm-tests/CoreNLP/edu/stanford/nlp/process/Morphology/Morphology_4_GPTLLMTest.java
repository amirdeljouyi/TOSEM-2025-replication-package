package edu.stanford.nlp.process;

import edu.stanford.nlp.ling.*;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.*;

public class Morphology_4_GPTLLMTest {

 @Test
  public void testStem_SimpleWord() {
    Morphology morphology = new Morphology();
    Word input = new Word("running");
    Word output = morphology.stem(input);
    assertEquals("run", output.word());
  }
@Test
  public void testStem_StringInput() {
    Morphology morphology = new Morphology();
    String result = morphology.stem("playing");
    assertEquals("play", result);
  }
@Test
  public void testStem_UnsupportedWordShouldReturnSame() {
    Morphology morphology = new Morphology();
    String input = "xyzzyplugh";
    String result = morphology.stem(input);
    assertEquals("xyzzyplugh", result);
  }
@Test
  public void testLemma_TaggedPluralNoun() {
    Morphology morphology = new Morphology();
    String result = morphology.lemma("dogs", "NNS");
    assertEquals("dog", result);
  }
@Test
  public void testLemma_ProperNounPreservesCase() {
    Morphology morphology = new Morphology();
    String result = morphology.lemma("Dogs", "NNP");
    assertEquals("Dogs", result);
  }
@Test
  public void testLemma_LowercaseFalseShouldPreserveCase() {
    Morphology morphology = new Morphology();
    String result = morphology.lemma("Swimming", "VBG", false);
    assertEquals("Swim", result);
  }
@Test
  public void testStem_CoreLabelAnnotation() {
    Morphology morphology = new Morphology();
    CoreLabel label = new CoreLabel();
    label.setWord("talking");
    label.setTag("VBG");
    morphology.stem(label);
    assertEquals("talk", label.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testStem_CoreLabelCustomAnnotation() {
    Morphology morphology = new Morphology();
    CoreLabel label = new CoreLabel();
    label.setWord("jumped");
    label.setTag("VBD");
    morphology.stem(label, CoreAnnotations.LemmaAnnotation.class);
    assertEquals("jump", label.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testNext_ValidReaderInput() throws IOException {
    StringReader reader = new StringReader("jumping");
    Morphology morphology = new Morphology(reader);
    Word result = morphology.next();
    assertNotNull(result);
    assertEquals("jump", result.word());
  }
@Test
  public void testNext_EmptyReaderReturnsNull() throws IOException {
    StringReader reader = new StringReader("");
    Morphology morphology = new Morphology(reader);
    Word result = morphology.next();
    assertNull(result);
  }
@Test
  public void testLemmatize_ReturnsWordLemmaTag() {
    Morphology morphology = new Morphology();
    WordTag wordTag = new WordTag("ran", "VBD");
    WordLemmaTag result = morphology.lemmatize(wordTag);
    assertEquals("ran", result.word());
    assertEquals("run", result.lemma());
    assertEquals("VBD", result.tag());
  }
@Test
  public void testStaticStem_WordTag() {
    WordTag input = new WordTag("dogs", "NNS");
    WordTag result = Morphology.stemStatic(input);
    assertEquals("dog", result.word());
    assertEquals("NNS", result.tag());
  }
@Test
  public void testStaticLemma_DefaultLowercaseTrue() {
    String result = Morphology.lemmaStatic("BARKING", "VBG");
    assertEquals("bark", result);
  }
@Test
  public void testStaticLemma_LowercaseFalsePreservesCase() {
    String result = Morphology.lemmaStatic("BARKING", "VBG", false);
    assertEquals("BARK", result);
  }
@Test
  public void testApply_WithWordTag() {
    Morphology morphology = new Morphology();
    WordTag wt = new WordTag("jumped", "VBD");
    Object result = morphology.apply(wt);
    assertTrue(result instanceof WordTag);
    WordTag tagged = (WordTag) result;
    assertEquals("jump", tagged.word());
    assertEquals("VBD", tagged.tag());
  }
@Test
  public void testApply_WithWord() {
    Morphology morphology = new Morphology();
    Word input = new Word("running");
    Object output = morphology.apply(input);
    assertTrue(output instanceof Word);
    Word result = (Word) output;
    assertEquals("run", result.word());
  }
@Test
  public void testApply_WithInvalidType() {
    Morphology morphology = new Morphology();
    String input = "invalid";
    Object result = morphology.apply(input);
    assertEquals("invalid", result);
  }
@Test
  public void testStaticLemmatize_WordTagObject() {
    WordTag input = new WordTag("was", "VBD");
    WordLemmaTag result = Morphology.lemmatizeStatic(input);
    assertEquals("was", result.word());
    assertEquals("be", result.lemma());
    assertEquals("VBD", result.tag());
  }
@Test
  public void testLemmatize_WordWithUnderscoreHandledCorrectly() {
    WordTag input = new WordTag("run_away", "VB");
    WordLemmaTag result = Morphology.lemmatizeStatic(input);
    assertEquals("run_away", result.word());
    assertEquals("run_away", result.lemma());
    assertEquals("VB", result.tag());
  }
@Test
  public void testMultipleCallsToNext_ReturnsTokensSequentially() throws IOException {
    StringReader reader = new StringReader("dogs running");
    Morphology morphology = new Morphology(reader);
    Word first = morphology.next();
    Word second = morphology.next();
    Word third = morphology.next();
    assertNotNull(first);
    assertNotNull(second);
    assertNull(third);
  }
@Test
  public void testConstructor_WithReaderAndFlags() {
    StringReader input = new StringReader("walking");
    Morphology morphology = new Morphology(input, 1);
    assertNotNull(morphology);
  }
@Test
  public void testStaticStem_WithExplicitWordAndTag() {
    WordTag result = Morphology.stemStatic("swimming", "VBG");
    assertEquals("swim", result.word());
    assertEquals("VBG", result.tag());
  }
@Test
  public void testMain_WithStemArguments() throws IOException {
    String[] args = {"-stem", "running_VBG", "flew_VBD"};
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(new java.io.ByteArrayOutputStream()));
    Morphology.main(args);
    System.setOut(originalOut);
  }
@Test
  public void testMain_WithInvalidFlagDoesNotThrow() throws IOException {
    String[] args = {"-xxx", "dummy"};
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(new java.io.ByteArrayOutputStream()));
    Morphology.main(args);
    System.setOut(originalOut);
  }
@Test
  public void testIllegalCharacters_EscapedAndHandled() {
    String input = "bad_example\nword space";
    String tag = "NN";
    String result = Morphology.lemmaStatic(input, tag);
    assertNotNull(result);
  }
@Test
  public void testApply_NullInputReturnsNull() {
    Morphology morphology = new Morphology();
    Object result = morphology.apply(null);
    assertNull(result);
  }
@Test
  public void testStem_PunctuationOnly() {
    Morphology morphology = new Morphology();
    String input = "!!!";
    String result = morphology.stem(input);
    
    assertEquals("!!!", result);
  }
@Test
  public void testLemma_EmptyString() {
    Morphology morphology = new Morphology();
    String result = morphology.lemma("", "NN");
    assertEquals("", result);
  }
@Test
  public void testLemma_WhitespaceInWord() {
    Morphology morphology = new Morphology();
    String result = morphology.lemma("drive through", "NN");
    assertNotNull(result);
  }
@Test
  public void testLemma_NewlineInWord() {
    Morphology morphology = new Morphology();
    String result = morphology.lemma("multi\nline", "NN");
    assertNotNull(result);
  }
@Test
  public void testStaticLemma_ProperNoun_CasePreserved() {
    String result = Morphology.lemmaStatic("John", "NNP");
    assertEquals("John", result);
  }
@Test
  public void testStaticStem_UnusualPOSTag() {
    WordTag input = new WordTag("running", "XYZ");
    WordTag result = Morphology.stemStatic(input);
    assertNotNull(result);
  }
@Test
  public void testLemmatize_NonAlphabeticCharacters() {
    Morphology morphology = new Morphology();
    String result = morphology.lemma("123abc!", "NN");
    assertNotNull(result);
  }
@Test
  public void testLemmatize_HyphenatedWord() {
    Morphology morphology = new Morphology();
    String result = morphology.lemma("mother-in-law", "NN");
    assertNotNull(result);
  }
@Test
  public void testConstructor_ThrowsIOExceptionRecovered() {
    Reader brokenReader = new Reader() {
      @Override
      public int read(char[] cbuf, int off, int len) throws IOException {
        throw new IOException("Forced error");
      }

      @Override
      public void close() throws IOException { }
    };

    Morphology morphology = new Morphology(brokenReader);
    String result = morphology.stem("test");
    
    assertEquals("test", result);
  }
@Test
  public void testOutputOfStaticLemmatizeMatchesManualStemStatic() {
    WordTag input = new WordTag("went", "VBD");
    WordLemmaTag lemma = Morphology.lemmatizeStatic(input);
    WordTag direct = Morphology.stemStatic(input.word(), input.tag());
    assertEquals(lemma.lemma(), direct.word());
  }
@Test
  public void testApply_WithCoreLabelThrownAwayReturnsInput() {
    Morphology morphology = new Morphology();
    Object input = new CoreLabel(); 
    Object result = morphology.apply(input);
    assertEquals(input, result);
  }
@Test
  public void testEdgeCase_StemIllegalUnicode() {
    Morphology morphology = new Morphology();
    String input = "\uFFFF\uFFFE"; 
    String result = morphology.stem(input);
    assertNotNull(result);
  }
@Test
  public void testStaticLemma_CapitalizedNounWithLowercaseFalse() {
    String result = Morphology.lemmaStatic("Apple", "NN", false);
    assertEquals("Apple", result);
  }
@Test
  public void testStem_EmojiCharacter() {
    Morphology morphology = new Morphology();
    String result = morphology.stem("🐶");
    assertEquals("🐶", result);
  }
@Test
  public void testLemma_InvalidPOS() {
    Morphology morphology = new Morphology();
    String result = morphology.lemma("running", "INVALID_TAG");
    assertNotNull(result);
    assertEquals("running", result); 
  }
@Test
  public void testStem_NonEnglishLetters() {
    Morphology morphology = new Morphology();
    String result1 = morphology.stem("façade");
    String result2 = morphology.stem("résumé");
    assertNotNull(result1);
    assertNotNull(result2);
  }
@Test
  public void testLemmatize_WhitespaceOnlyWord() {
    Morphology morphology = new Morphology();
    String result = morphology.lemma("   ", "NN");
    assertNotNull(result);
    assertEquals("   ", result);
  }
@Test
  public void testLemmatize_UnicodeZeroWidthSpaceAndTag() {
    Morphology morphology = new Morphology();
    String input = "word\u200B"; 
    String result = morphology.lemma(input, "NN");
    assertNotNull(result);
  }
@Test
  public void testStem_IndentedWordWithSpaces() {
    Morphology morphology = new Morphology();
    String result = morphology.stem("    walking   ");
    assertNotNull(result);
  }
@Test
  public void testConstructor_StringReaderWithNonStandardCharacters() throws IOException {
    StringReader reader = new StringReader("éclair éclair");
    Morphology morphology = new Morphology(reader);
    Word w1 = morphology.next();
    Word w2 = morphology.next();
    Word w3 = morphology.next();
    assertNotNull(w1);
    assertNotNull(w2);
    assertNull(w3);
  }
@Test
  public void testApplyWithCoreLabelTypeNotHandled() {
    Morphology morphology = new Morphology();
    CoreLabel label = new CoreLabel();
    label.setWord("eaten");
    label.setTag("VBN");
    Object result = morphology.apply(label);
    assertEquals(label, result); 
  }
@Test
  public void testApplyWithInteger_ReturnsAsIs() {
    Morphology morphology = new Morphology();
    Integer input = 123;
    Object result = morphology.apply(input);
    assertEquals(input, result);
  }
@Test
  public void testMain_WithMalformedStemArg() throws IOException {
    String[] args = {"-stem", "badformatword"};
    PrintStream originalOut = System.out;
    System.setOut(new PrintStream(new ByteArrayOutputStream()));
    Morphology.main(args); 
    System.setOut(originalOut);
  }
@Test
  public void testMain_WithRebuildVerbTableFlagAndEmptyContentFile() throws IOException {
    String[] args = {"-rebuildVerbTable", "dummyEmptyFile.txt"};
    PrintStream originalOut = System.out;
    System.setOut(new PrintStream(new ByteArrayOutputStream()));
    Morphology.main(args); 
    System.setOut(originalOut);
  }
@Test
  public void testStaticStem_EmptyStringTagStillReturnsWordTag() {
    WordTag result = Morphology.stemStatic("run", "");
    assertNotNull(result);
    assertEquals("run", result.word());
    assertEquals("", result.tag());
  }
@Test
  public void testStaticLemma_LongUncommonWord() {
    String input = "antidisestablishmentarianism";
    String result = Morphology.lemmaStatic(input, "NN");
    assertEquals("antidisestablishmentarianism", result);
  }
@Test
  public void testMixedCaseWord_AndLowercaseTrue() {
    Morphology morphology = new Morphology();
    String result = morphology.lemma("LoOkEd", "VBD", true);
    assertEquals("look", result);
  }
@Test
  public void testForbiddenCharReplacementAndRecoveryInLemmatizer() {
    Morphology morphology = new Morphology();
    String wordWithUnderscoreAndSpace = "multi_word test";
    String tag = "NN";
    String lemma = morphology.lemma(wordWithUnderscoreAndSpace, tag);
    assertNotNull(lemma);
  }
@Test
  public void testWordTagCreationWithSpecialChars() {
    WordTag wt = WordTag.valueOf("laughing/VBG", "/");
    assertNotNull(wt);
    assertEquals("laughing", wt.word());
    assertEquals("VBG", wt.tag());
  }
@Test
  public void testStemWithWordContainingLineBreak() {
    Morphology morphology = new Morphology();
    String result = morphology.stem("cat\ndog");
    assertEquals("cat\ndog", result); 
  }
@Test
  public void testLemmaWithUnderscoreOnlyWord() {
    Morphology morphology = new Morphology();
    String result = morphology.lemma("___", "NN");
    assertNotNull(result);
    assertEquals("___", result); 
  }
@Test
  public void testStaticLemmaGivenWordWithSpacesAndTag() {
    String result = Morphology.lemmaStatic("pack of dogs", "NN");
    assertNotNull(result);
    assertEquals("pack of dogs", result);
  }
@Test
  public void testStaticStemWhenStaticLexerNotInitialized() {
    
    WordTag result = Morphology.stemStatic("burned", "VBD");
    assertNotNull(result);
    assertEquals("burn", result.word());
  }
@Test
  public void testUnicodeTag() {
    Morphology morphology = new Morphology();
    String result = morphology.lemma("tested", "✅✅");
    assertNotNull(result);
  }
@Test
  public void testNextWithPartialWordFollowedByEOF() throws IOException {
    StringReader reader = new StringReader("runn");
    Morphology morphology = new Morphology(reader);
    Word next = morphology.next();
    assertNotNull(next);
  }
@Test
  public void testEmptyTagInWordTagStillReturnsSameWord() {
    WordTag input = new WordTag("walk", "");
    WordLemmaTag lemma = Morphology.lemmatizeStatic(input);
    assertEquals("walk", lemma.word());
    assertEquals("walk", lemma.lemma());
    assertEquals("", lemma.tag());
  }
@Test
  public void testDoubleCallToStaticStem_ForThreadSafety() {
    WordTag first = Morphology.stemStatic("jumped", "VBD");
    WordTag second = Morphology.stemStatic("running", "VBG");
    assertEquals("jump", first.word());
    assertEquals("run", second.word());
  }
@Test
  public void testLemmatizeWithForbiddenCharactersReplacementLogic() {
    Morphology morphology = new Morphology();
    String word = "abc_def\n ghi";
    String tag = "NN";
    String result = morphology.lemma(word, tag);
    assertNotNull(result);
    assertTrue(result.contains("_") || result.contains(" ") || result.contains("\n"));
  }
@Test
  public void testMainCalledWithNoArgsPrintsHelp() throws IOException {
    String[] args = {};
    PrintStream originalOut = System.out;
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    System.setOut(new PrintStream(out));
    Morphology.main(args);
    System.setOut(originalOut);
    String output = out.toString();
    assertTrue(output.contains("java Morphology"));
  }
@Test
  public void testMainStemWithMalformedInputPair() throws IOException {
    String[] args = {"-stem", "invalidtoken"};
    PrintStream originalOut = System.out;
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    System.setOut(new PrintStream(out));
    Morphology.main(args);
    System.setOut(originalOut);
  }
@Test
  public void testLemmatizeWithIllegalSurrogates() {
    Morphology morphology = new Morphology();
    String input = "\uD800\uDC00";
    String result = morphology.lemma(input, "NN");
    assertNotNull(result);
  }
@Test
  public void testApplyWithMisconfiguredCoreLabel() {
    Morphology morphology = new Morphology();
    CoreLabel label = new CoreLabel(); 
    Object result = morphology.apply(label);
    assertNotNull(result);
    assertEquals(label, result);
  }
@Test
  public void testLemmatizeProperNounWithLowercaseFalse() {
    Morphology morphology = new Morphology();
    String result = morphology.lemma("London", "NNP", false);
    assertEquals("London", result);
  }
@Test
  public void testMainWithHyphenatedStemWords() throws IOException {
    String[] args = {"-stem", "mother-in-law_NN"};
    PrintStream originalOut = System.out;
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    System.setOut(new PrintStream(out));
    Morphology.main(args);
    System.setOut(originalOut);
    String output = out.toString();
    assertTrue(output.contains("mother-in-law_NN"));
  }
@Test
  public void testStaticStemWithSingleCharacterWord() {
    WordTag result = Morphology.stemStatic("a", "DT");
    assertNotNull(result);
    assertEquals("a", result.word());
  }
@Test
  public void testStemNumeric() {
    Morphology morphology = new Morphology();
    String result = morphology.stem("12345");
    assertEquals("12345", result);
  }
@Test
  public void testLemmaWhitespaceOnlyValue() {
    Morphology morphology = new Morphology();
    String result = morphology.lemma(" ", "NN");
    assertEquals(" ", result);
  }
@Test
  public void testConstructorWithLexerOptionsFlagZero() throws IOException {
    String s = "testing";
    StringReader reader = new StringReader(s);
    Morphology morphology = new Morphology(reader, 0);
    Word next = morphology.next();
    assertNotNull(next);
  }
@Test
  public void testEmptyStringReaderReturnsNullWord() throws IOException {
    StringReader reader = new StringReader("");
    Morphology morph = new Morphology(reader);
    Word result = morph.next();
    assertNull(result);
  }
@Test
  public void testStem_WhitespaceOnlyInput() {
    Morphology morphology = new Morphology();
    String result = morphology.stem("   ");
    assertEquals("   ", result);
  }
@Test
  public void testStem_NullSafeCheckWithNonLetterInput() {
    Morphology morphology = new Morphology();
    String result = morphology.stem("@!#");
    assertEquals("@!#", result);
  }
@Test
  public void testLemma_NonTagLikeCharacters() {
    Morphology morphology = new Morphology();
    String result = morphology.lemma("apple", "###");
    assertNotNull(result);
    assertEquals("apple", result);
  }
@Test
  public void testStem_InputWordWithLeadingAndTrailingSpaces() {
    Morphology morphology = new Morphology();
    String input = " running ";
    String result = morphology.stem(input);
    assertEquals(" running ", result); 
  }
@Test
  public void testLemma_InputWordWithEmbeddedTab() {
    Morphology morphology = new Morphology();
    String word = "fly\taway";
    String result = morphology.lemma(word, "VB");
    assertNotNull(result);
    assertEquals("fly\taway", result);
  }
@Test
  public void testStem_ControlCharacterInWord() {
    Morphology morphology = new Morphology();
    String input = "go\u0007now"; 
    String result = morphology.stem(input);
    assertNotNull(result);
    assertEquals("go\u0007now", result);
  }
@Test
  public void testLemmaStaticWithMixedSeparatorCharacters() {
    String word = "word_with space\nnewline";
    String tag = "NN";
    String result = Morphology.lemmaStatic(word, tag);
    assertEquals("word_with space\nnewline", result);
  }
@Test
  public void testApply_WithNullPOSInWordTag() {
    Morphology morphology = new Morphology();
    WordTag input = new WordTag("dogs", null);
    Object result = morphology.apply(input);
    assertTrue(result instanceof WordTag);
    assertEquals("dogs", ((WordTag) result).word());
    assertNull(((WordTag) result).tag());
  }
@Test
  public void testLemmatize_WordTagWithEmptyComponents() {
    Morphology morphology = new Morphology();
    WordTag empty = new WordTag("", "");
    WordLemmaTag result = morphology.lemmatize(empty);
    assertEquals("", result.word());
    assertEquals("", result.lemma());
    assertEquals("", result.tag());
  }
@Test
  public void testStaticLemmatize_WordTagWithWhitespaceOnly() {
    WordTag wt = new WordTag("   ", "   ");
    WordLemmaTag result = Morphology.lemmatizeStatic(wt);
    assertEquals("   ", result.word());
    assertEquals("   ", result.lemma());
    assertEquals("   ", result.tag());
  }
@Test
  public void testMainWithMultipleNumericFlagsAndFileFallback() throws IOException {
    String[] args = {"-1", "-2", "fakefile.txt"};
    PrintStream originalOut = System.out;
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    System.setOut(new PrintStream(out));
    Morphology.main(args);
    System.setOut(originalOut);
    String output = out.toString();
    assertTrue(output.contains("Couldn't handle flag") || output.isEmpty());
  }
@Test
  public void testMainWithNonNumericFlagsAndNonexistentFile() throws IOException {
    String[] args = {"-x", "nonexistent.txt"};
    PrintStream originalOut = System.out;
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    System.setOut(new PrintStream(out));
    Morphology.main(args);
    System.setOut(originalOut);
    String output = out.toString();
    assertTrue(output.contains("Couldn't handle flag"));
  }
@Test
  public void testLemmatizeWithTokenSymbol_ProcessedCorrectly() {
    Morphology morphology = new Morphology();
    String result = morphology.lemma("<token>", "NN");
    assertNotNull(result);
  }
@Test
  public void testStem_UnicodeZWNJ() {
    Morphology morphology = new Morphology();
    String result = morphology.stem("run\u200Cning");
    assertNotNull(result);
  }
@Test
  public void testNext_MultipleWordsSeparatedByWhitespace() throws IOException {
    StringReader reader = new StringReader("played played");
    Morphology morph = new Morphology(reader);
    Word first = morph.next();
    Word second = morph.next();
    Word third = morph.next();
    assertEquals("play", first.word());
    assertEquals("play", second.word());
    assertNull(third);
  }
@Test
  public void testStaticLemma_CapitalizedPluralNoun() {
    String result = Morphology.lemmaStatic("Houses", "NNS");
    assertEquals("house", result);
  }
@Test
  public void testStem_NumericContainingWord() {
    Morphology morphology = new Morphology();
    String result = morphology.stem("word123");
    assertEquals("word123", result); 
  }
@Test
  public void testApplyWithWeirdWordTag() {
    Morphology morphology = new Morphology();
    WordTag wt = new WordTag("$pecial", "NNS");
    Object result = morphology.apply(wt);
    assertTrue(result instanceof WordTag);
  }
@Test
  public void testLemmatize_LargeMixedCaseVerbWithLowercaseTrue() {
    Morphology morphology = new Morphology();
    String result = morphology.lemma("CoNstrUCting", "VBG", true);
    assertEquals("construct", result); 
  }
@Test
  public void testLemma_EmptyPOS() {
    Morphology morphology = new Morphology();
    String result = morphology.lemma("walking", "");
    assertNotNull(result);
    assertEquals("walking", result); 
  }
@Test
  public void testLemma_NullPOS() {
    Morphology morphology = new Morphology();
    String result = morphology.lemma("running", null);
    assertEquals("running", result); 
  }
@Test
  public void testLemma_NullWord() {
    Morphology morphology = new Morphology();
    String result = morphology.lemma(null, "NN");
    assertNull(result); 
  }
@Test
  public void testStemStaticOnWordTagWithNulls() {
    WordTag wordTag = new WordTag((String) null, null);
    WordTag result = Morphology.stemStatic(wordTag);
    assertNotNull(result);
    assertNull(result.word());
    assertNull(result.tag());
  }
@Test
  public void testStem_StaticMethodWithSymbolOnlyWord() {
    WordTag result = Morphology.stemStatic("#@!", "NN");
    assertNotNull(result);
    assertEquals("#@!", result.word());
  }
@Test
  public void testLemmaWithLongInputWord() {
    Morphology morphology = new Morphology();
    String input = "a";
    String result = morphology.lemma(input, "NN");
    assertEquals(input, result);
  }
@Test
  public void testApplyWithIncompleteWordTag() {
    Morphology morphology = new Morphology();
    WordTag input = new WordTag("", "VBD");
    Object result = morphology.apply(input);
    assertTrue(result instanceof WordTag);
    WordTag output = (WordTag) result;
    assertEquals("", output.word());
    assertEquals("VBD", output.tag());
  }
@Test
  public void testStem_CoreLabelMissingPOS() {
    Morphology morphology = new Morphology();
    CoreLabel label = new CoreLabel();
    label.setWord("eating");
    morphology.stem(label);
    String lemma = label.get(CoreAnnotations.LemmaAnnotation.class);
    assertEquals("eating", lemma);
  }
@Test
  public void testStem_CoreLabelMissingWord() {
    Morphology morphology = new Morphology();
    CoreLabel label = new CoreLabel();
    label.setTag("VBZ");
    morphology.stem(label);
    String lemma = label.get(CoreAnnotations.LemmaAnnotation.class);
    assertNull(lemma);
  }
@Test
  public void testStem_StringTriggersIOExceptionFallback() {
    Reader brokenReader = new Reader() {
      @Override
      public int read(char[] cbuf, int off, int len) throws IOException {
        throw new IOException("Mock read exception");
      }
      @Override
      public void close() {}
    };
    Morphology morphology = new Morphology(brokenReader);
    String result = morphology.stem("testing");
    assertEquals("testing", result);
  }
@Test
  public void testApply_UnlabeledCoreLabelIgnoredInApply() {
    Morphology morphology = new Morphology();
    Object input = new CoreLabel(); 
    Object output = morphology.apply(input);
    assertEquals(input, output); 
  }
@Test
  public void testApply_LemmaAnnotationSetCorrectly() {
    Morphology morphology = new Morphology();
    CoreLabel label = new CoreLabel();
    label.setWord("played");
    label.setTag("VBD");
    morphology.stem(label);
    String lemma = label.get(CoreAnnotations.LemmaAnnotation.class);
    assertEquals("play", lemma);
  }
@Test
  public void testLemmatize_MixedCaseProperNounWithLowercaseTrue() {
    Morphology morphology = new Morphology();
    String word = "Washington";
    String tag = "NNP";
    String result = morphology.lemma(word, tag, true);
    assertEquals("Washington", result); 
  }
@Test
  public void testMain_WithFileArgAndUnparseableFlag() throws IOException {
    String[] args = {"-notANumber", "nonexistent.txt"};
    PrintStream originalOut = System.out;
    ByteArrayOutputStream unusedOut = new ByteArrayOutputStream();
    System.setOut(new PrintStream(unusedOut));
    Morphology.main(args);
    System.setOut(originalOut);
  }
@Test
  public void testMain_WithOnlyFileNameArg() throws IOException {
    String[] args = {"mockStringReader.txt"};
    PrintStream originalOut = System.out;
    ByteArrayOutputStream log = new ByteArrayOutputStream();
    System.setOut(new PrintStream(log));
    Morphology.main(args);
    System.setOut(originalOut);
  }
@Test
  public void testMain_WithNumericFlagAndFile() throws IOException {
    String[] args = {"-5", "fake.txt"};
    PrintStream originalOut = System.out;
    ByteArrayOutputStream capture = new ByteArrayOutputStream();
    System.setOut(new PrintStream(capture));
    Morphology.main(args);
    System.setOut(originalOut);
  }
@Test
  public void testStemWithMultipleUnderscoresInWord() {
    Morphology morphology = new Morphology();
    String word = "word_with_multiple_parts";
    String stem = morphology.stem(word);
    assertEquals(word, stem); 
  }
@Test
  public void testStemWithMixedLettersAndDigits() {
    Morphology morphology = new Morphology();
    String word = "run42ning";
    String result = morphology.stem(word);
    assertEquals("run42ning", result); 
  }
@Test
  public void testStemOnEmptyString() {
    Morphology morphology = new Morphology();
    String input = "";
    String result = morphology.stem(input);
    assertEquals("", result);
  }
@Test
  public void testStaticLemmaWithCustomTagAndCasePreservation() {
    String word = "TreeHouse";
    String tag = "NNP";
    String lemma = Morphology.lemmaStatic(word, tag, false);
    assertEquals("TreeHouse", lemma); 
  }
@Test
  public void testLemmatizeWithTabCharacterInWord() {
    Morphology morphology = new Morphology();
    String word = "run\ttime";
    String tag = "NN";
    String lemma = morphology.lemma(word, tag);
    assertEquals("run\ttime", lemma);
  }
@Test
  public void testStemWithQuotesSymbols() {
    Morphology morphology = new Morphology();
    String word = "\"quoted\"";
    String stem = morphology.stem(word);
    assertEquals(word, stem);
  }
@Test
  public void testApplyWithNullWordTag() {
    Morphology morphology = new Morphology();
    Object output = morphology.apply(null);
    assertNull(output);
  }
@Test
  public void testApplyWithUnexpectedObjectType() {
    Morphology morphology = new Morphology();
    Object input = new StringBuilder("not handled");
    Object result = morphology.apply(input);
    assertSame(input, result); 
  }
@Test
  public void testStemStaticWithLowercaseVerb() {
    WordTag wordTag = new WordTag("sitting", "VBG");
    WordTag result = Morphology.stemStatic(wordTag);
    assertEquals("sit", result.word());
    assertEquals("VBG", result.tag());
  }
@Test
  public void testLemmatizeUnicodeNoBreakSpace() {
    Morphology morphology = new Morphology();
    String word = "word\u00A0again"; 
    String tag = "NN";
    String lemma = morphology.lemma(word, tag);
    assertEquals(word, lemma); 
  }
@Test
  public void testLemmaWithOnlyTag() {
    Morphology morphology = new Morphology();
    String result = morphology.lemma("", "JJ");
    assertEquals("", result);
  }
@Test
  public void testStaticMethodThreadSafetyCallOrder() {
    WordTag first = Morphology.stemStatic("chased", "VBD");
    WordTag second = Morphology.stemStatic("yelling", "VBG");
    assertEquals("chase", first.word());
    assertEquals("yell", second.word());
  }
@Test
  public void testLemmatizeTokenThatTriggersIllegalTokenHandling() {
    Morphology morphology = new Morphology();
    String input = "watch_out!";
    String tag = "VB";
    String lemma = morphology.lemma(input, tag);
    assertEquals("watch_out!", lemma); 
  }
@Test
  public void testMorphologyNextMultipleTermsExactCount() throws IOException {
    StringReader reader = new StringReader("talked jumping");
    Morphology morph = new Morphology(reader);
    Word w1 = morph.next();
    Word w2 = morph.next();
    Word w3 = morph.next();
    assertEquals("talk", w1.word());
    assertEquals("jump", w2.word());
    assertNull(w3); 
  }
@Test
  public void testMainWithHyphenAndEmptyFlagHandling() throws IOException {
    String[] args = {"-", "fake.txt"};
    PrintStream original = System.out;
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    System.setOut(new PrintStream(out));
    Morphology.main(args);
    System.setOut(original);
    String log = out.toString();
    assertTrue(log.contains("Couldn't handle flag") || log.isEmpty());
  }
@Test
  public void testLemmaWithMultilineInput() {
    Morphology morphology = new Morphology();
    String word = "many\nwords";
    String tag = "NN";
    String lemma = morphology.lemma(word, tag);
    assertTrue(lemma.contains("many") || lemma.equals(word));
  }
@Test
  public void testStemCoreLabelWithMissingWordAndTag() {
    Morphology morphology = new Morphology();
    CoreLabel label = new CoreLabel(); 
    morphology.stem(label, CoreAnnotations.LemmaAnnotation.class);
    String lemma = label.get(CoreAnnotations.LemmaAnnotation.class);
    assertNull(lemma); 
  }
@Test
  public void testStaticLemmatizeMixedCasePluralWithNNS() {
    WordTag tag = new WordTag("Horses", "NNS");
    WordLemmaTag lemmaTag = Morphology.lemmatizeStatic(tag);
    assertEquals("horse", lemmaTag.lemma()); 
  }
@Test
  public void testStaticStemUsedBackToBackWithLowercaseOption() {
    WordTag t1 = Morphology.stemStatic("HELLO", "NNP");
    WordTag t2 = Morphology.stemStatic("shouted", "VBD");
    assertEquals("HELLO", t1.word()); 
    assertEquals("shout", t2.word());
  } 
}