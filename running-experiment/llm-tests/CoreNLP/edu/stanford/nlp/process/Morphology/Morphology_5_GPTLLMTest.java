package edu.stanford.nlp.process;

import edu.stanford.nlp.ling.*;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.*;

public class Morphology_5_GPTLLMTest {

 @Test
  public void testStemSimpleWord() {
    Morphology morphology = new Morphology();
    Word stemmed = morphology.stem(new Word("running"));
    assertEquals("run", stemmed.word());
  }
@Test
  public void testStemInflectedNoun() {
    Morphology morphology = new Morphology();
    Word stemmed = morphology.stem(new Word("dogs"));
    assertEquals("dog", stemmed.word());
  }
@Test
  public void testStemWordWithForbiddenCharsUnderscore() {
    Morphology morphology = new Morphology();
    String result = morphology.lemma("up_down", "NN");
    assertEquals("up_down", result);
  }
@Test
  public void testStemWordWithForbiddenCharsSpace() {
    Morphology morphology = new Morphology();
    String result = morphology.lemma("turn on", "VB");
    assertEquals("turn on", result);
  }
@Test
  public void testStemWordWithForbiddenCharsNewline() {
    Morphology morphology = new Morphology();
    String result = morphology.lemma("turn\non", "VB");
    assertEquals("turn\non", result);
  }
@Test
  public void testLemmaLowercaseTrue() {
    Morphology morphology = new Morphology();
    String lemma = morphology.lemma("Running", "VBG", true);
    assertEquals("run", lemma);
  }
@Test
  public void testLemmaLowercaseFalse() {
    Morphology morphology = new Morphology();
    String lemma = morphology.lemma("Running", "VBG", false);
    assertEquals("Run", lemma);
  }
@Test
  public void testLemmaStaticLowercase() {
    String lemma = Morphology.lemmaStatic("Played", "VBD");
    assertEquals("play", lemma);
  }
@Test
  public void testStemStaticFromWordTag() {
    WordTag wordTag = new WordTag("dogs", "NNS");
    WordTag stemmed = Morphology.stemStatic(wordTag);
    assertEquals("dog", stemmed.word());
    assertEquals("NNS", stemmed.tag());
  }
@Test
  public void testLemmatizeToWordLemmaTagInstance() {
    Morphology morphology = new Morphology();
    WordTag wordTag = new WordTag("running", "VBG");
    WordLemmaTag wlt = morphology.lemmatize(wordTag);
    assertEquals("running", wlt.word());
    assertEquals("run", wlt.lemma());
    assertEquals("VBG", wlt.tag());
  }
@Test
  public void testLemmatizeStaticReturnsCorrectResult() {
    WordTag wordTag = new WordTag("dogs", "NNS");
    WordLemmaTag wlt = Morphology.lemmatizeStatic(wordTag);
    assertEquals("dogs", wlt.word());
    assertEquals("dog", wlt.lemma());
    assertEquals("NNS", wlt.tag());
  }
@Test
  public void testStemAddsLemmaToCoreLabel() {
    Morphology morphology = new Morphology();
    CoreLabel label = new CoreLabel();
    label.setWord("swimming");
    label.setTag("VBG");
    morphology.stem(label);
    assertEquals("swim", label.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testStemIntoCustomAnnotationField() {
    Morphology morphology = new Morphology();
    CoreLabel label = new CoreLabel();
    label.setWord("happiest");
    label.setTag("JJS");
    morphology.stem(label, CoreAnnotations.LemmaAnnotation.class);
    assertEquals("happy", label.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testApplyMethodWithWordInstance() {
    Morphology morphology = new Morphology();
    Word input = new Word("eating");
    Object result = morphology.apply(input);
    assertTrue(result instanceof Word);
    assertEquals("eat", ((Word) result).word());
  }
@Test
  public void testApplyMethodWithWordTagInstance() {
    Morphology morphology = new Morphology();
    WordTag input = new WordTag("playing", "VBG");
    Object result = morphology.apply(input);
    assertTrue(result instanceof WordTag);
    assertEquals("play", ((WordTag) result).word());
    assertEquals("VBG", ((WordTag) result).tag());
  }
@Test
  public void testApplyMethodWithNonWordInstance() {
    Morphology morphology = new Morphology();
    Object input = new Object();
    Object result = morphology.apply(input);
    assertSame(input, result);
  }
@Test
  public void testNextMethodReturnsCorrectlyStemmedWords() throws IOException {
    String inputText = "jumping\ncats\n";
    Reader reader = new StringReader(inputText);
    Morphology morphology = new Morphology(reader);

    Word first = morphology.next();
    Word second = morphology.next();
    Word third = morphology.next();

    assertNotNull(first);
    assertEquals("jump", first.word());

    assertNotNull(second);
    assertEquals("cat", second.word());

    assertNull(third);
  }
@Test
  public void testStemStaticLemmatizesVerbCorrectly() {
    WordTag input = new WordTag("went", "VBD");
    WordTag result = Morphology.stemStatic(input.word(), input.tag());
    assertEquals("go", result.word());
    assertEquals("VBD", result.tag());
  }
@Test
  public void testMainDisplaysHelpWithNoArgs() throws Exception {
    String[] args = new String[0];
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    PrintStream originalOut = System.out;
    System.setOut(new PrintStream(outputStream));

    Morphology.main(args);

    System.setOut(originalOut);
    String output = outputStream.toString("UTF-8");
    assertTrue(output.contains("java Morphology [-rebuildVerbTable file|-stem word+|file+]"));
  }
@Test
  public void testMainStemArgumentPrintsExpectedOutput() throws Exception {
    String[] args = {"-stem", "running_VBG"};
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    PrintStream originalOut = System.out;
    InputStream originalIn = System.in;

    System.setOut(new PrintStream(outputStream));
    System.setIn(new ByteArrayInputStream("".getBytes())); 

    Morphology.main(args);

    System.setOut(originalOut);
    System.setIn(originalIn);

    String output = outputStream.toString("UTF-8");
    assertTrue(output.contains("running_VBG --> run_VBG"));
  }
@Test
  public void testStemEmptyStringInput() {
    Morphology morphology = new Morphology();
    String result = morphology.stem("");
    assertEquals("", result);
  }
@Test
  public void testLemmaEmptyStringAndNullTag() {
    Morphology morphology = new Morphology();
    String result = morphology.lemma("", null);
    assertEquals("", result);
  }
@Test
  public void testLemmaWithNullWordAndTag() {
    Morphology morphology = new Morphology();
    String result = morphology.lemma(null, null);
    assertEquals("null", result);
  }
@Test
  public void testConstructionWithReaderOnly() throws IOException {
    String input = "running\n";
    StringReader reader = new StringReader(input);
    Morphology morphology = new Morphology(reader);
    Word word = morphology.next();
    assertEquals("run", word.word());
  }
@Test
  public void testConstructionWithReaderAndFlags() throws IOException {
    String input = "talking\n";
    StringReader reader = new StringReader(input);
    Morphology morphology = new Morphology(reader, 1);
    Word word = morphology.next();
    assertEquals("talk", word.word());
  }
@Test
  public void testStemUnicodeInput() {
    Morphology morphology = new Morphology();
    Word input = new Word("überlaufen");
    Word result = morphology.stem(input);
    assertEquals("überlaufen", result.word()); 
  }
@Test
  public void testLemmatizeWithPunctuation() {
    Morphology morphology = new Morphology();
    WordTag wordTag = new WordTag(".", ".");
    WordLemmaTag result = morphology.lemmatize(wordTag);
    assertEquals(".", result.word());
    assertEquals(".", result.tag());
    assertEquals(".", result.lemma());
  }
@Test
  public void testNextMethodWithOnlyNewlineInput() throws IOException {
    String input = "\n";
    StringReader reader = new StringReader(input);
    Morphology morphology = new Morphology(reader);
    Word word = morphology.next();
    assertNull(word); 
  }
@Test
  public void testApplyMethodWithNull() {
    Morphology morphology = new Morphology();
    Object input = null;
    Object result = morphology.apply(input);
    assertNull(result);
  }
@Test
  public void testStemStaticWithWordContainingMultipleUnderscores() {
    WordTag wordTag = new WordTag("multi_part_name", "NN");
    WordTag result = Morphology.stemStatic(wordTag);
    assertEquals("multi_part_name", result.word());
  }
@Test
  public void testStemStaticWithEmptyWordAndTag() {
    WordTag wordTag = new WordTag("", "");
    WordTag result = Morphology.stemStatic(wordTag);
    assertEquals("", result.word());
    assertEquals("", result.tag());
  }
@Test
  public void testLemmatizeStaticWithSpecialCharacters() {
    WordTag wordTag = new WordTag("café", "NN");
    WordLemmaTag result = Morphology.lemmatizeStatic(wordTag);
    assertEquals("café", result.word());
    assertEquals("café", result.lemma()); 
    assertEquals("NN", result.tag());
  }
@Test
  public void testStemWithNullCoreLabelFields() {
    Morphology morphology = new Morphology();
    CoreLabel label = new CoreLabel();
    morphology.stem(label);
    assertEquals("null", label.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testStemCoreLabelWithMissingPOS() {
    Morphology morphology = new Morphology();
    CoreLabel label = new CoreLabel();
    label.setWord("running");
    morphology.stem(label);
    assertEquals("null", label.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testStemCoreLabelWithMissingWord() {
    Morphology morphology = new Morphology();
    CoreLabel label = new CoreLabel();
    label.setTag("VBG");
    morphology.stem(label);
    assertEquals("null", label.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testMainStemMultipleArgs() throws Exception {
    String[] args = {"-stem", "walking_VBG", "played_VBD"};
    java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(out));

    Morphology.main(args);

    System.setOut(originalOut);
    String result = out.toString("UTF-8");
    assertTrue(result.contains("walking_VBG --> walk_VBG"));
    assertTrue(result.contains("played_VBD --> play_VBD"));
  }
@Test
  public void testMainWithInvalidFlag() throws Exception {
    String[] args = {"-x", "ignored.txt"};
    java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(out));

    Morphology.main(args);

    System.setOut(originalOut);
    String result = out.toString("UTF-8");
    assertTrue(result.contains("Couldn't handle flag"));
  }
@Test
  public void testLemmatizeHandlesNonLetterCharacters() {
    Morphology morphology = new Morphology();
    String result = morphology.lemma("12345", "CD");
    assertEquals("12345", result);
  }
@Test
  public void testApplyWithNumberWord() {
    Morphology morphology = new Morphology();
    Word input = new Word("123");
    Object result = morphology.apply(input);
    assertTrue(result instanceof Word);
    assertEquals("123", ((Word) result).word());
  }
@Test
  public void testEmptyWordTagInApply() {
    Morphology morphology = new Morphology();
    WordTag input = new WordTag("", "");
    Object result = morphology.apply(input);
    assertTrue(result instanceof WordTag);
    assertEquals("", ((WordTag) result).word());
    assertEquals("", ((WordTag) result).tag());
  }
@Test
  public void testApplyWithNullWordAndTag() {
    Morphology morphology = new Morphology();
    WordTag input = new WordTag((String) null, null);
    Object result = morphology.apply(input);
    assertTrue(result instanceof WordTag);
    assertEquals("null", ((WordTag) result).word());
    assertEquals(null, ((WordTag) result).tag());
  }
@Test
  public void testMainWithInvalidArgsLength() throws Exception {
    String[] args = {"-rebuildVerbTable"};
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    PrintStream originalOut = System.out;
    System.setOut(new PrintStream(out));

    Morphology.main(args);

    System.setOut(originalOut);
    String printed = out.toString("UTF-8");
    assertTrue(printed.contains("java Morphology [-rebuildVerbTable file|-stem word+|file+]"));
  }
@Test
  public void testMainWithNonIntegerFlag() throws Exception {
    String[] args = {"-abc", "fakefile.txt"};
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    PrintStream originalOut = System.out;
    System.setOut(new PrintStream(out));

    Morphology.main(args);

    System.setOut(originalOut);
    String output = out.toString("UTF-8");
    assertTrue(output.contains("Couldn't handle flag"));
  }
@Test
  public void testStemStaticPreservesTag() {
    WordTag tagged = new WordTag("CATS", "NNS");
    WordTag result = Morphology.stemStatic(tagged);
    assertEquals("cat", result.word());
    assertEquals("NNS", result.tag());
  }
@Test
  public void testLemmaStaticLowercaseFalseCapitalizationPreserved() {
    String result = Morphology.lemmaStatic("Running", "VBG", false);
    assertEquals("Running", result);
  }
@Test
  public void testMisformattedMainArgsGracefulExit() throws Exception {
    String[] args = {"-stem", "incomplete_tag"};
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    PrintStream originalOut = System.out;
    System.setOut(new PrintStream(out));

    Morphology.main(args);

    System.setOut(originalOut);
    String result = out.toString("UTF-8");
    assertTrue(result.contains("incomplete_tag"));
  }
@Test
  public void testMainWithComplexMultiFlagArgs() throws Exception {
    String[] args = {"-stem", "walking_VBG", "-15", "cats_NNS"};
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    PrintStream originalOut = System.out;
    System.setOut(new PrintStream(out));

    Morphology.main(args);

    System.setOut(originalOut);
    String content = out.toString("UTF-8");
    assertTrue(content.contains("walking_VBG --> walk_VBG"));
  }
@Test
  public void testWordWithUnderscoreAndValidTag() {
    Morphology morphology = new Morphology();
    String word = "machine_learning";
    String tag = "NN";
    String result = morphology.lemma(word, tag);
    assertEquals("machine_learning", result);
  }
@Test
  public void testWordWithMultipleNewlines() {
    String sample = "jumping\njumped\n";
    Morphology morphology = new Morphology(new StringReader(sample));
    try {
      Word word1 = morphology.next();
      assertNotNull(word1);
      assertEquals("jump", word1.word());

      Word word2 = morphology.next();
      assertNotNull(word2);
      assertEquals("jump", word2.word());

      Word word3 = morphology.next();
      assertNull(word3);
    } catch (IOException e) {
      fail("IOException thrown in testWordWithMultipleNewlines");
    }
  }
@Test
  public void testApplyReturnsSameObjectIfNonWord() {
    Morphology morphology = new Morphology();
    Object input = 5;
    Object output = morphology.apply(input);
    assertSame(input, output);
  }
@Test
  public void testStemWithMixedCaseWords() {
    Morphology morphology = new Morphology();
    Word input1 = new Word("Running");
    Word input2 = new Word("RUNNING");
    Word stemmed1 = morphology.stem(input1);
    Word stemmed2 = morphology.stem(input2);
    assertEquals("run", stemmed1.word());
    assertEquals("run", stemmed2.word());
  }
@Test
  public void testStemStaticMixedCasePluralNoun() {
    WordTag input = new WordTag("CATS", "NNS");
    WordTag result = Morphology.stemStatic(input);
    assertEquals("cat", result.word());
  }
@Test
  public void testStemWithNonAsciiCharacters() {
    Morphology morphology = new Morphology();
    Word input = new Word("naïve");
    Word result = morphology.stem(input);
    assertEquals("naïve", result.word());
  }
@Test
  public void testStemWithEmoji() {
    Morphology morphology = new Morphology();
    Word input = new Word("🚀");
    Word result = morphology.stem(input);
    assertEquals("🚀", result.word());
  }
@Test
  public void testLemmaWordIsNullTagIsSet() {
    Morphology morphology = new Morphology();
    String result = morphology.lemma(null, "NN");
    assertEquals("null", result);
  }
@Test
  public void testLemmaWordIsSetTagIsNull() {
    Morphology morphology = new Morphology();
    String result = morphology.lemma("students", null);
    assertEquals("students", result);
  }
@Test
  public void testStemCoreLabelWithEmptyWordTag() {
    Morphology morphology = new Morphology();
    CoreLabel label = new CoreLabel();
    label.setWord("");
    label.setTag("");
    morphology.stem(label);
    assertEquals("", label.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testStemCoreLabelWithWhitespaceOnly() {
    Morphology morphology = new Morphology();
    CoreLabel label = new CoreLabel();
    label.setWord("   ");
    label.setTag("NN");
    morphology.stem(label);
    assertEquals("", label.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testStemCoreLabelWithTabCharacter() {
    Morphology morphology = new Morphology();
    CoreLabel label = new CoreLabel();
    label.setWord("\t");
    label.setTag("NN");
    morphology.stem(label);
    assertEquals("", label.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testLemmaStaticWithNullValues() {
    String result = Morphology.lemmaStatic(null, null);
    assertEquals("null", result);
  }
@Test
  public void testStemStaticWithNullWordAndTag() {
    WordTag input = new WordTag((String) null, null);
    WordTag result = Morphology.stemStatic(input);
    assertEquals("null", result.word());
    assertNull(result.tag());
  }
@Test
  public void testLemmatizeStaticWithEmptyWordAndTag() {
    WordTag input = new WordTag("", "");
    WordLemmaTag result = Morphology.lemmatizeStatic(input);
    assertEquals("", result.word());
    assertEquals("", result.lemma());
    assertEquals("", result.tag());
  }
@Test
  public void testLemmatizeStaticWithWhitespaceValues() {
    WordTag input = new WordTag("   ", "  ");
    WordLemmaTag result = Morphology.lemmatizeStatic(input);
    assertEquals("   ", result.word());
    assertEquals("", result.lemma());
    assertEquals("  ", result.tag());
  }
@Test
  public void testMainWithOnlyFlagChar() throws Exception {
    String[] args = {"-"};
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    PrintStream originalOut = System.out;
    System.setOut(new PrintStream(output));
    Morphology.main(args);
    System.setOut(originalOut);
    String result = output.toString("UTF-8");
    assertTrue(result.contains("Couldn't handle flag"));
  }
@Test
  public void testMainWithMalformedWordTagPair() throws Exception {
    String[] args = {"-stem", "incorrectTagPair"};
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    PrintStream originalOut = System.out;
    System.setOut(new PrintStream(output));
    Morphology.main(args);
    System.setOut(originalOut);
    String result = output.toString("UTF-8");
    assertTrue(result.contains("incorrectTagPair"));
  }
@Test
  public void testNextWithTrailingWhitespaceOnlyLine() throws IOException {
    StringReader reader = new StringReader("jumping\n   \ncats\n");
    Morphology morph = new Morphology(reader);
    Word first = morph.next();
    Word second = morph.next();
    Word third = morph.next();
    assertNotNull(first);
    assertEquals("jump", first.word());
    assertNotNull(second);
    assertEquals("", second.word());
    assertNotNull(third);
    assertEquals("cat", third.word());
  }
@Test
  public void testApplyWithCoreLabelReturnsOriginal() {
    Morphology morphology = new Morphology();
    CoreLabel coreLabel = new CoreLabel();
    Object result = morphology.apply(coreLabel);
    assertSame(coreLabel, result);
  }
@Test
  public void testNextReturnsNullWhenReaderExhausted() throws IOException {
    Morphology morphology = new Morphology(new StringReader(""));
    Word result = morphology.next();
    assertNull(result);
  }
@Test
  public void testStemExtremelyLongToken() {
    Morphology morphology = new Morphology();
    String longWord = new String(new char[10000]).replace('\0', 'a');
    Word result = morphology.stem(new Word(longWord));
    assertEquals(longWord, result.word());
  }
@Test
  public void testLemmatizeMixedCaseProperNounTag() {
    Morphology morphology = new Morphology();
    String result = morphology.lemma("Obama", "NNP");
    assertEquals("Obama", result);
  }
@Test
  public void testLemmatizeLowercaseProperNounTag() {
    Morphology morphology = new Morphology();
    String result = morphology.lemma("obama", "NNP");
    assertEquals("obama", result);
  }
@Test
  public void testStemStaticSpecialCaseProperNounPreservedCase() {
    WordTag input = new WordTag("California", "NNP");
    WordTag result = Morphology.stemStatic(input);
    assertEquals("California", result.word());
  }
@Test
  public void testStemWordWithTrailingSpaces() {
    Morphology morphology = new Morphology();
    Word word = new Word("walking   ");
    Word result = morphology.stem(word);
    assertEquals("walk", result.word());
  }
@Test
  public void testStemWordWithLeadingSpaces() {
    Morphology morphology = new Morphology();
    Word word = new Word("   walking");
    Word result = morphology.stem(word);
    assertEquals("walk", result.word());
  }
@Test
  public void testLemmatizeWithUnderscoreTagSeparatesCorrectly() {
    Morphology morphology = new Morphology();
    String result = morphology.lemma("jump_over", "VB");
    assertEquals("jump_over", result);
  }
@Test
  public void testStemStaticHandlesCapitalizedNounPlural() {
    WordTag wt = new WordTag("CATS", "NNS");
    WordTag result = Morphology.stemStatic(wt.word(), wt.tag());
    assertEquals("cat", result.word());
    assertEquals("NNS", result.tag());
  }
@Test
  public void testStemStaticEmptyInput() {
    WordTag wt = new WordTag("", "");
    WordTag result = Morphology.stemStatic(wt.word(), wt.tag());
    assertEquals("", result.word());
    assertEquals("", result.tag());
  }
@Test
  public void testLemmatizeProperNounWithLowercaseFalse() {
    Morphology morphology = new Morphology();
    String result = morphology.lemma("London", "NNP", false);
    assertEquals("London", result);
  }
@Test
  public void testLemmatizeProperNounWithLowercaseTrue() {
    Morphology morphology = new Morphology();
    String result = morphology.lemma("London", "NNP", true);
    assertEquals("London", result); 
  }
@Test
  public void testStemHandlesMultipleWordsAsOneToken() {
    Morphology morphology = new Morphology();
    Word word = new Word("kick_the_ball");
    Word result = morphology.stem(word);
    assertEquals("kick_the_ball", result.word());
  }
@Test
  public void testApplyReturnsSameInstanceWhenNullPassed() {
    Morphology morphology = new Morphology();
    Object input = null;
    Object result = morphology.apply(input);
    assertNull(result);
  }
@Test
  public void testStemHandlesEmptyCoreLabelTag() {
    Morphology morphology = new Morphology();
    CoreLabel label = new CoreLabel();
    label.setWord("walking");
    label.setTag("");
    morphology.stem(label);
    assertEquals("walking", label.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testApplyWithEmptyStringWord() {
    Morphology morphology = new Morphology();
    Word word = new Word("");
    Object result = morphology.apply(word);
    assertEquals("", ((Word) result).word());
  }
@Test
  public void testStemWithNewLineOnlyAsInput() {
    Morphology morphology = new Morphology(new StringReader("\n"));
    try {
      Word result = morphology.next();
      assertEquals("", result.word());
    } catch (IOException e) {
      fail("Should not throw IOException");
    }
  }
@Test
  public void testLemmaWithDigitsAndPunctuation() {
    Morphology morphology = new Morphology();
    String result = morphology.lemma("12.5", "CD");
    assertEquals("12.5", result);
  }
@Test
  public void testMainWithStemAndInvalidTokenFormat() throws Exception {
    String[] args = {"-stem", "missingTag"};
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    PrintStream originalOut = System.out;
    System.setOut(new PrintStream(out));
    Morphology.main(args);
    System.setOut(originalOut);
    String printed = out.toString("UTF-8");
    assertTrue(printed.contains("missingTag"));
  }
@Test
  public void testMainWithStemDashFormatToken() throws Exception {
    String[] args = {"-stem", "high-speed_NN"};
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    PrintStream originalOut = System.out;
    System.setOut(new PrintStream(out));
    Morphology.main(args);
    System.setOut(originalOut);
    String printed = out.toString("UTF-8");
    assertTrue(printed.contains("high-speed_NN --> high-speed_NN"));
  }
@Test
  public void testLemmaStaticWithHyphenatedWords() {
    String result = Morphology.lemmaStatic("check-in", "NN");
    assertEquals("check-in", result);
  }
@Test
  public void testLemmatizePreservesNewLineCharacter() {
    Morphology morphology = new Morphology();
    String result = morphology.lemma("first\nline", "NN");
    assertEquals("first\nline", result);
  }
@Test
  public void testStemUnicodeTokenWithTag() {
    WordTag wt = new WordTag("français", "JJ");
    WordTag result = Morphology.stemStatic(wt);
    assertEquals("français", result.word());
  }
@Test
  public void testStemTokenWithTabCharacter() {
    Morphology morphology = new Morphology();
    Word word = new Word("jump\ted");
    Word result = morphology.stem(word);
    assertEquals("jump\ted", result.word());
  }
@Test
  public void testStemSingleSpecialCharacter() {
    Morphology morphology = new Morphology();
    Word word = new Word("?");
    Word result = morphology.stem(word);
    assertEquals("?", result.word());
  }
@Test
  public void testStemNullWordObject() {
    Morphology morphology = new Morphology();
    Word word = new Word((String) null);
    Word result = morphology.stem(word);
    assertEquals("null", result.word());
  }
@Test
  public void testStemCoreLabelWithNullWordAndValidTag() {
    Morphology morphology = new Morphology();
    CoreLabel label = new CoreLabel();
    label.setWord(null);
    label.setTag("NN");
    morphology.stem(label);
    assertEquals("null", label.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testStemCoreLabelWithValidWordAndNullTag() {
    Morphology morphology = new Morphology();
    CoreLabel label = new CoreLabel();
    label.setWord("running");
    label.setTag(null);
    morphology.stem(label);
    assertEquals("running", label.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testStemTokenWithUnderscoreSpaceAndNewline() {
    Morphology morphology = new Morphology();
    String input = "a_b c\nd";
    String result = morphology.lemma(input, "NN");
    assertEquals("a_b c\nd", result);
  }
@Test
  public void testLemmatizeStaticDoesNotLowercaseProperNounWhenFalse() {
    String result = Morphology.lemmaStatic("London", "NNP", false);
    assertEquals("London", result);
  }
@Test
  public void testLemmatizeStaticLowercasesBecauseTagNotProperNoun() {
    String result = Morphology.lemmaStatic("Running", "VBG", true);
    assertEquals("run", result);
  }
@Test
  public void testMainWithMultipleValidStemArguments() throws Exception {
    String[] args = {"-stem", "talking_VBG", "dogs_NNS", "ran_VBD"};
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    PrintStream originalOut = System.out;
    System.setOut(new PrintStream(out));
    Morphology.main(args);
    System.setOut(originalOut);
    String output = out.toString("UTF-8");
    assertTrue(output.contains("talking_VBG --> talk_VBG"));
    assertTrue(output.contains("dogs_NNS --> dog_NNS"));
    assertTrue(output.contains("ran_VBD --> run_VBD"));
  }
@Test
  public void testMainWithEmptyStemArgumentValue() throws Exception {
    String[] args = {"-stem", ""};
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    PrintStream originalOut = System.out;
    System.setOut(new PrintStream(out));
    Morphology.main(args);
    System.setOut(originalOut);
    String output = out.toString("UTF-8");
    assertTrue(output.contains("--> _"));
  }
@Test
  public void testLemmaWithMixedCaseAndLowercaseTrue() {
    Morphology morphology = new Morphology();
    String result = morphology.lemma("Studied", "VBD", true);
    assertEquals("study", result);
  }
@Test
  public void testLemmaWithUppercaseAcronymTag() {
    Morphology morphology = new Morphology();
    String result = morphology.lemma("USA", "NNP", true);
    assertEquals("USA", result);
  }
@Test
  public void testLemmatizeStaticIgnoresLowercaseFlagOnProperNoun() {
    WordTag wordTag = new WordTag("Amazon", "NNP");
    WordLemmaTag result = Morphology.lemmatizeStatic(wordTag);
    assertEquals("Amazon", result.lemma());
  }
@Test
  public void testApplyWithStringInstance() {
    Morphology morphology = new Morphology();
    Object input = "plain string";
    Object result = morphology.apply(input);
    assertEquals("plain string", result);
  }
@Test
  public void testStemWithDigitsOnly() {
    Morphology morphology = new Morphology();
    Word result = morphology.stem(new Word("123456"));
    assertEquals("123456", result.word());
  }
@Test
  public void testNextReadsLineWithWhitespacePrefix() throws IOException {
    StringReader input = new StringReader("   walking\n");
    Morphology morphology = new Morphology(input);
    Word result = morphology.next();
    assertEquals("walk", result.word());
  }
@Test
  public void testNextWithConsecutiveBlankLines() throws IOException {
    StringReader input = new StringReader("\n\ncats\n\n");
    Morphology morphology = new Morphology(input);
    Word first = morphology.next();
    Word second = morphology.next();
    Word third = morphology.next();
    assertEquals("", first.word());
    assertEquals("", second.word());
    assertEquals("cat", third.word());
  }
@Test
  public void testStemWordWithUppercasePunctuation() {
    Morphology morphology = new Morphology();
    Word word = new Word("?");
    Word result = morphology.stem(word);
    assertEquals("?", result.word());
  }
@Test
  public void testStemStaticProperNounUpperCasePreserved() {
    WordTag wordTag = new WordTag("Microsoft", "NNP");
    WordTag result = Morphology.stemStatic(wordTag);
    assertEquals("Microsoft", result.word());
  }
@Test
  public void testLemmaProperNounAllLowerWithLowercaseTrue() {
    Morphology morphology = new Morphology();
    String result = morphology.lemma("paris", "NNP", true);
    assertEquals("paris", result);
  }
@Test
  public void testNextWithTokenizerReturningNull() throws IOException {
    Morphology morphology = new Morphology(new StringReader(""));
    Word word = morphology.next();
    assertNull(word);
  }
@Test
  public void testApplyWithNullWordTagObject() {
    Morphology morphology = new Morphology();
    Object input = null;
    Object output = morphology.apply(input);
    assertNull(output);
  }
@Test
  public void testLemmatizeNullWordWithValidTag() {
    Morphology morphology = new Morphology();
    String result = morphology.lemma(null, "NN");
    assertEquals("null", result);
  }
@Test
  public void testLemmatizeValidWordWithNullTag() {
    Morphology morphology = new Morphology();
    String result = morphology.lemma("dogs", null);
    assertEquals("dogs", result);
  }
@Test
  public void testLemmatizeStaticNullWordAndTag() {
    String result = Morphology.lemmaStatic(null, null);
    assertEquals("null", result);
  }
@Test
  public void testStemStaticNullTag() {
    WordTag wordTag = new WordTag("cats", null);
    WordTag result = Morphology.stemStatic(wordTag);
    assertEquals("cats", result.word());
    assertNull(result.tag());
  }
@Test
  public void testStemCoreLabelWithNullTagAndWord() {
    Morphology morphology = new Morphology();
    CoreLabel label = new CoreLabel();
    label.setWord(null);
    label.setTag(null);
    morphology.stem(label);
    assertEquals("null", label.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testStemCoreLabelWithEmptyTagAndWord() {
    Morphology morphology = new Morphology();
    CoreLabel label = new CoreLabel();
    label.setWord("");
    label.setTag("");
    morphology.stem(label);
    assertEquals("", label.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testWordWithAllWhitespaceOnly() {
    Morphology morphology = new Morphology();
    Word word = new Word("   ");
    Word result = morphology.stem(word);
    assertEquals("", result.word());
  }
@Test
  public void testStemSpecialCharactersOnly() {
    Morphology morphology = new Morphology();
    Word word = new Word("@#$%^&*");
    Word result = morphology.stem(word);
    assertEquals("@#$%^&*", result.word());
  }
@Test
  public void testApplyNonWordNonWordTagObject() {
    Morphology morphology = new Morphology();
    Object input = new Object();
    Object result = morphology.apply(input);
    assertSame(input, result);
  }
@Test
  public void testLemmatizeAcronymUppercase() {
    Morphology morphology = new Morphology();
    String result = morphology.lemma("NASA", "NNP", true);
    assertEquals("NASA", result);
  }
@Test
  public void testLemmatizeVerbEndingEd() {
    Morphology morphology = new Morphology();
    String result = morphology.lemma("walked", "VBD", true);
    assertEquals("walk", result);
  }
@Test
  public void testStemWordContainingTabs() {
    Morphology morphology = new Morphology();
    Word word = new Word("\tdogs\t");
    Word result = morphology.stem(word);
    assertEquals("dog", result.word());
  }
@Test
  public void testMainWithInvalidRebuildVerbTableFlagOnly() throws Exception {
    String[] args = {"-rebuildVerbTable"};
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    PrintStream original = System.out;
    System.setOut(new PrintStream(stream));
    Morphology.main(args);
    System.setOut(original);
    String result = stream.toString("UTF-8");
    assertTrue(result.contains("java Morphology [-rebuildVerbTable"));
  }
@Test
  public void testMainWithImproperFlagFormat() throws Exception {
    String[] args = {"-*brokenflag"};
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    PrintStream original = System.out;
    System.setOut(new PrintStream(stream));
    Morphology.main(args);
    System.setOut(original);
    String result = stream.toString("UTF-8");
    assertTrue(result.contains("Couldn't handle flag"));
  }
@Test
  public void testMainWithNonStemArgumentFallbackToFile() throws Exception {
    String[] args = {"-10someflag", "fakefile.txt"};
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    PrintStream originalOut = System.out;
    System.setOut(new PrintStream(output));
    Morphology.main(args);
    System.setOut(originalOut);
    String result = output.toString("UTF-8");
    assertTrue(result.contains("Couldn't handle flag") || result.isEmpty());
  }
@Test
  public void testStemExtremelyLongInputString() {
    Morphology morphology = new Morphology();
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < 10000; i++) {
      builder.append("a");
    }
    Word word = new Word(builder.toString());
    Word result = morphology.stem(word);
    assertEquals(builder.toString(), result.word());
  }
@Test
  public void testStemPreservesProperNounCapitalization() {
    WordTag wordTag = new WordTag("Stanford", "NNP");
    WordTag result = Morphology.stemStatic(wordTag);
    assertEquals("Stanford", result.word());
  }
@Test
  public void testNextSkipsBlankLine() throws IOException {
    StringReader reader = new StringReader("\n\nrunning\n");
    Morphology morphology = new Morphology(reader);
    Word w1 = morphology.next();
    assertEquals("", w1.word());
    Word w2 = morphology.next();
    assertEquals("", w2.word());
    Word w3 = morphology.next();
    assertEquals("run", w3.word());
  } 
}