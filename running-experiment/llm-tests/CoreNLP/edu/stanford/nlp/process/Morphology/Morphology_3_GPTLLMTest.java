package edu.stanford.nlp.process;

import edu.stanford.nlp.ling.*;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import static org.junit.Assert.*;

public class Morphology_3_GPTLLMTest {

 @Test
  public void testStemSimpleVerb() {
    Morphology morphology = new Morphology(new StringReader("running_VBG"));
    String result = morphology.lemma("running", "VBG");
    assertEquals("run", result);
  }
@Test
  public void testStemPluralNoun() {
    Morphology morphology = new Morphology(new StringReader("dogs_NNS"));
    String result = morphology.lemma("dogs", "NNS");
    assertEquals("dog", result);
  }
@Test
  public void testProperNounPreservesCase() {
    Morphology morphology = new Morphology(new StringReader("London_NNP"));
    String result = morphology.lemma("London", "NNP");
    assertEquals("London", result);
  }
@Test
  public void testStemWithLowercaseFalse() {
    Morphology morphology = new Morphology(new StringReader("Running_VBG"));
    String result = morphology.lemma("Running", "VBG", false);
    assertEquals("Run", result);
  }
@Test
  public void testStemWithLowercaseTrue() {
    Morphology morphology = new Morphology(new StringReader("Running_VBG"));
    String result = morphology.lemma("Running", "VBG", true);
    assertEquals("run", result);
  }
@Test
  public void testStemCoreLabelUsesLemmaAnnotation() {
    Morphology morphology = new Morphology(new StringReader("dogs_NNS"));
    CoreLabel label = new CoreLabel();
    label.setWord("dogs");
    label.setTag("NNS");
    morphology.stem(label);
    assertEquals("dog", label.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testStemCoreLabelWithCustomAnnotation() {
    Morphology morphology = new Morphology(new StringReader("babies_NNS"));
    CoreLabel label = new CoreLabel();
    label.setWord("babies");
    label.setTag("NNS");
    morphology.stem(label, CoreAnnotations.LemmaAnnotation.class);
    assertEquals("baby", label.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testLemmatizeReturnsCorrectResult() {
    Morphology morphology = new Morphology(new StringReader("running_VBG"));
    WordTag input = new WordTag("running", "VBG");
    WordLemmaTag result = morphology.lemmatize(input);
    assertEquals("running", result.word());
    assertEquals("run", result.lemma());
    assertEquals("VBG", result.tag());
  }
@Test
  public void testStemStaticWithStringWordTag() {
    WordTag result = Morphology.stemStatic("dogs", "NNS");
    assertEquals("dog", result.word());
    assertEquals("NNS", result.tag());
  }
@Test
  public void testStemStaticWithWordTagObject() {
    WordTag input = new WordTag("babies", "NNS");
    WordTag result = Morphology.stemStatic(input);
    assertEquals("baby", result.word());
    assertEquals("NNS", result.tag());
  }
@Test
  public void testLemmaStaticDefaultLowercase() {
    String result = Morphology.lemmaStatic("Running", "VBG");
    assertEquals("run", result);
  }
@Test
  public void testLemmaStaticLowercaseFalse() {
    String result = Morphology.lemmaStatic("Running", "VBG", false);
    assertEquals("Run", result);
  }
@Test
  public void testLemmatizeStaticReturnsCorrectResult() {
    WordTag input = new WordTag("running", "VBG");
    WordLemmaTag tag = Morphology.lemmatizeStatic(input);
    assertEquals("running", tag.word());
    assertEquals("run", tag.lemma());
    assertEquals("VBG", tag.tag());
  }
@Test
  public void testApplyHandlesWordTag() {
    Morphology morphology = new Morphology(new StringReader("raided_VBD"));
    WordTag input = new WordTag("raided", "VBD");
    Object result = morphology.apply(input);
    assertTrue(result instanceof WordTag);
    assertEquals("raid", ((WordTag) result).word());
    assertEquals("VBD", ((WordTag) result).tag());
  }
@Test
  public void testApplyHandlesWord() {
    Morphology morphology = new Morphology(new StringReader("walking"));
    Word input = new Word("walking");
    Object result = morphology.apply(input);
    assertTrue(result instanceof Word);
    assertEquals("walk", ((Word) result).word());
  }
@Test
  public void testApplyHandlesNonWordObject() {
    Morphology morphology = new Morphology(new StringReader("test"));
    Object input = 42;
    Object result = morphology.apply(input);
    assertEquals(input, result);
  }
@Test
  public void testStemIOExceptionFallbackReturnsOriginal() {
    Morphology morphology = new Morphology(new StringReader("")) {
      @Override
      public String stem(String word) {
        return super.stem("_invalid_token_");
      }
    };
    String result = morphology.stem("running");
    assertNotNull(result);
  }
@Test
  public void testLemmaHandlesUnderscoreReplacement() {
    Morphology morphology = new Morphology(new StringReader("word\\u1CF0with\\u1CF0underscore_NN"));
    String result = morphology.lemma("word_with_underscore", "NN");
    assertEquals("word_with_underscore", result);
  }
@Test
  public void testLemmaHandlesNewlineReplacement() {
    Morphology morphology = new Morphology(new StringReader("line\\u1CF2break_NN"));
    String result = morphology.lemma("line\nbreak", "NN");
    assertEquals("line\nbreak", result);
  }
@Test
  public void testLemmaEmptyStringReturnsEmpty() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.lemma("", "NN");
    assertEquals("", result);
  }
@Test
  public void testNextWordFromReader() throws IOException {
    Morphology morphology = new Morphology(new StringReader("stopped"));
    Word word = morphology.next();
    assertNotNull(word);
    assertEquals("stopped", word.word());
  }
@Test
  public void testNextReturnsNullAtEOF() throws IOException {
    Morphology morphology = new Morphology(new StringReader(""));
    Word word = morphology.next();
    assertNull(word);
  }
@Test
  public void testWordStemReturnsExpected() {
    Morphology morphology = new Morphology(new StringReader("talked"));
    Word input = new Word("talked");
    Word result = morphology.stem(input);
    assertEquals("talk", result.word());
  }
@Test
  public void testIndependentInstancesDoNotShareState() {
    Morphology morph1 = new Morphology(new StringReader("walked"));
    Morphology morph2 = new Morphology(new StringReader("jumped"));
    Word result1 = morph1.stem(new Word("walked"));
    Word result2 = morph2.stem(new Word("jumped"));
    assertEquals("walk", result1.word());
    assertEquals("jump", result2.word());
  }
@Test
  public void testStemNullInputReturnsNull() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.lemma(null, "NN");
    assertNull(result);
  }
@Test
  public void testConstructorWithOptionFlags() {
    Morphology morphology = new Morphology(new StringReader("tests_are_fun"), 1);
    assertNotNull(morphology);
  }
@Test
public void testStemWithUnknownPOSTagReturnsOriginal() {
  Morphology morphology = new Morphology(new StringReader("foobar_XX"));
  String result = morphology.lemma("foobar", "XX");
  assertEquals("foobar", result);  
}
@Test
public void testStemWithOnlyUnderscoreInWord() {
  Morphology morphology = new Morphology(new StringReader("_underscore_NN"));
  String result = morphology.lemma("_underscore", "NN");
  assertEquals("_underscore", result);
}
@Test
public void testStemWithOnlyNewLineInWord() {
  Morphology morphology = new Morphology(new StringReader("\n_NN"));
  String result = morphology.lemma("\n", "NN");
  assertEquals("\n", result);
}
@Test
public void testStemWithSpaceInWord() {
  Morphology morphology = new Morphology(new StringReader("two words_NN"));
  String result = morphology.lemma("two words", "NN");
  assertEquals("two words", result);
}
@Test
public void testStaticStemIsSynchronizedUnderConcurrency() throws InterruptedException {
  Thread t1 = new Thread(() -> {
    WordTag wt = Morphology.stemStatic("running", "VBG");
    assertEquals("run", wt.word());
  });

  Thread t2 = new Thread(() -> {
    WordTag wt = Morphology.stemStatic("walking", "VBG");
    assertEquals("walk", wt.word());
  });

  t1.start();
  t2.start();
  t1.join();
  t2.join();
}
@Test
public void testMalformedTagFormatInStemStatic() {
  WordTag result = Morphology.stemStatic("testing", "BAD_TAG");
  assertNotNull(result);
  assertEquals("test", result.word());  
}
@Test
public void testLemmaNullTagReturnsOriginalWord() {
  Morphology morphology = new Morphology(new StringReader("test_null"));
  String result = morphology.lemma("test", null);
  assertEquals("test", result);  
}
@Test
public void testStemFailureFallsBackToOriginal() {
  Morphology morphology = new Morphology(new StringReader("INVALID!!@@"));
  String result = morphology.stem("invalid_input");
  assertEquals("invalid_input", result);  
}
@Test
public void testApplyWithNullReturnsNull() {
  Morphology morphology = new Morphology(new StringReader(""));
  Object result = morphology.apply(null);
  assertNull(result);
}
@Test
public void testStemWithPartiallyMalformedTokenStillProcesses() {
  Morphology morphology = new Morphology(new StringReader("walked_"));
  String result = morphology.lemma("walked", "");
  assertEquals("walk", result);  
}
@Test
public void testApplyWithWordTagMissingPOS() {
  Morphology morphology = new Morphology(new StringReader("climbed_"));
  WordTag input = new WordTag("climbed", null);
  Object result = morphology.apply(input);
  assertTrue(result instanceof WordTag);
  assertEquals("climbed", ((WordTag) result).word());  
}
@Test
public void testMultipleSequentialLemmatizationCallsConsistency() {
  Morphology morphology = new Morphology(new StringReader("looked_VBD"));
  String result1 = morphology.lemma("looked", "VBD");
  String result2 = morphology.lemma("looked", "VBD");
  assertEquals("look", result1);
  assertEquals("look", result2);
}
@Test
public void testMainMethodHelpMessageNoArgs() throws IOException {
  String[] args = {};
  Morphology.main(args);
  
}
@Test
public void testMainMethodInvalidFlagIgnored() throws IOException {
  String[] args = {"-notAFlag", "input.txt"};
  Morphology.main(args);
  
}
@Test
public void testStemStaticHandlesSpecialCharacters() {
  WordTag wt = Morphology.stemStatic("cafés", "NNS");
  assertEquals("café", wt.word());
}
@Test
public void testStemHandlesCapitalizedProperNounWithLowercaseOptionTrue() {
  Morphology morphology = new Morphology(new StringReader("Eiffel_NNP"));
  String result = morphology.lemma("Eiffel", "NNP", true);
  assertEquals("Eiffel", result);  
}
@Test
public void testLemmaWithWhitespacePOS() {
  Morphology morphology = new Morphology(new StringReader("jumped_ "));
  String result = morphology.lemma("jumped", " ");
  assertEquals("jump", result);  
}
@Test
public void testUnicodeWordLemmatization() {
  Morphology morphology = new Morphology(new StringReader("clichés_NNS"));
  String result = morphology.lemma("clichés", "NNS");
  assertEquals("cliché", result);
}
@Test
public void testStemStaticWithEmptyWordReturnsEmpty() {
  WordTag wt = Morphology.stemStatic("", "NNS");
  assertEquals("", wt.word());
}
@Test
  public void testStemEmptyWordAndEmptyTag() {
    Morphology morphology = new Morphology(new StringReader("_"));
    WordTag wordTag = new WordTag("", "");
    WordTag stemmed = Morphology.stemStatic(wordTag);
    assertEquals("", stemmed.word());
    assertEquals("", stemmed.tag());
  }
@Test
  public void testStemWordContainingTabCharacter() {
    Morphology morphology = new Morphology(new StringReader("word\twith\ttabs_NN"));
    String input = "word\twith\ttabs";
    String result = morphology.lemma(input, "NN");
    assertEquals("word\twith\ttabs", result);
  }
@Test
  public void testStemWordEndingInPunctuation() {
    Morphology morphology = new Morphology(new StringReader("dogs._NNS"));
    String result = morphology.lemma("dogs.", "NNS");
    assertEquals("dog.", result);
  }
@Test
  public void testStemWordWithMultipleUnderscores() {
    Morphology morphology = new Morphology(new StringReader("word__tag"));
    String result = morphology.lemma("word_", "tag");
    assertEquals("word_", result); 
  }
@Test
  public void testApplyWithUnknownSubtypeObject() {
    Morphology morphology = new Morphology(new StringReader(""));
    Object input = new Object();
    Object result = morphology.apply(input);
    assertSame(input, result);
  }
@Test
  public void testLemmaWithNullWordAndNullTag() {
    Morphology morphology = new Morphology(new StringReader(""));
    String result = morphology.lemma(null, null);
    assertNull(result);
  }
@Test
  public void testStemProperNounWithoutLowercasing() {
    Morphology morphology = new Morphology(new StringReader("Paris_NNP"));
    String result = morphology.lemma("Paris", "NNP", false);
    assertEquals("Paris", result);
  }
@Test
  public void testStemProperNounWithLowercasing() {
    Morphology morphology = new Morphology(new StringReader("Paris_NNP"));
    String result = morphology.lemma("Paris", "NNP", true);
    assertEquals("Paris", result); 
  }
@Test
  public void testStemUpperCaseVerbWithLowercaseTrue() {
    Morphology morphology = new Morphology(new StringReader("WALKED_VBD"));
    String result = morphology.lemma("WALKED", "VBD", true);
    assertEquals("walk", result); 
  }
@Test
  public void testStemUpperCaseVerbWithLowercaseFalse() {
    Morphology morphology = new Morphology(new StringReader("WALKED_VBD"));
    String result = morphology.lemma("WALKED", "VBD", false);
    assertEquals("Walk", result); 
  }
@Test
  public void testLemmatizeStaticWithNullTag() {
    WordTag wordTag = new WordTag("dogs", null);
    WordLemmaTag result = Morphology.lemmatizeStatic(wordTag);
    assertEquals("dogs", result.word());
    assertEquals("dogs", result.lemma()); 
    assertNull(result.tag());
  }
@Test
  public void testStemStaticWordTagWithNullWord() {
    WordTag wordTag = new WordTag(null, "NNS");
    WordTag result = Morphology.stemStatic(wordTag);
    assertEquals(null, result.word()); 
    assertEquals("NNS", result.tag());
  }
@Test
  public void testConstructorWithReaderNullsOutLexerBehavior() {
    Morphology morphology = new Morphology((StringReader) null);
    String result = morphology.lemma("running", "VBG");
    assertEquals("running", result); 
  }
@Test
  public void testStemSpecialUnicodeInput() {
    Morphology morphology = new Morphology(new StringReader("mañanas_NNS"));
    String result = morphology.lemma("mañanas", "NNS");
    assertEquals("mañana", result);
  }
@Test
  public void testStemStaticWithLongWordReturnsSameIfNoLexicalRule() {
    WordTag result = Morphology.stemStatic("superextrahyperunusualword", "NN");
    assertEquals("superextrahyperunusualword", result.word()); 
    assertEquals("NN", result.tag());
  }
@Test
  public void testStemAsciiControlCharactersHandled() {
    Morphology morphology = new Morphology(new StringReader("dog\u0001_NN"));
    String wordWithControlChar = "dog\u0001";
    String result = morphology.lemma(wordWithControlChar, "NN");
    assertEquals(wordWithControlChar, result); 
  }
@Test
  public void testStaticApplyWithWordPreservesTagLogic() {
    Morphology morphology = new Morphology(new StringReader("ACTED_VBD"));
    WordTag wt = new WordTag("ACTED", "VBD");
    Object result = morphology.apply(wt);
    assertTrue(result instanceof WordTag);
    assertEquals("act", ((WordTag) result).word());
    assertEquals("VBD", ((WordTag) result).tag());
  }
@Test
  public void testNextWithLexerExceptionReturnsNull() throws IOException {
    Morphology morphology = new Morphology(new StringReader("")) {
      @Override
      public Word next() throws IOException {
        throw new IOException("Simulated IO error");
      }
    };
    try {
      Word result = morphology.next();
      fail("IOException expected");
    } catch (IOException e) {
      assertEquals("Simulated IO error", e.getMessage());
    }
  }
@Test
  public void testStemStaticHandlesLineBreaks() {
    WordTag result = Morphology.stemStatic("line\nbreak", "NN");
    assertEquals("line\nbreak", result.word()); 
    assertEquals("NN", result.tag());
  }
@Test
  public void testStemStaticHandlesSpaceInWord() {
    WordTag result = Morphology.stemStatic("state of art", "NN");
    assertEquals("state of art", result.word());
    assertEquals("NN", result.tag());
  }
@Test
  public void testStemPreservesWordWithDigits() {
    Morphology morphology = new Morphology(new StringReader("iPhones_NNS"));
    String result = morphology.lemma("iPhones", "NNS");
    assertEquals("iPhone", result); 
  }
@Test
  public void testApplyHandlesWordTagWithEmptyTag() {
    Morphology morphology = new Morphology(new StringReader("testing_"));
    WordTag input = new WordTag("testing", "");
    Object result = morphology.apply(input);
    assertTrue(result instanceof WordTag);
    assertEquals("test", ((WordTag) result).word());
    assertEquals("", ((WordTag) result).tag());
  }
@Test
  public void testStemWithSingleCharacterWord() {
    Morphology morphology = new Morphology(new StringReader("a_DT"));
    String result = morphology.lemma("a", "DT");
    assertEquals("a", result);
  }
@Test
  public void testStemWithTagContainingSpecialCharacters() {
    Morphology morphology = new Morphology(new StringReader("tested_@@@"));
    String result = morphology.lemma("tested", "@@@");
    assertEquals("test", result);
  }
@Test
  public void testStemWordWithOnlyWhitespace() {
    Morphology morphology = new Morphology(new StringReader("   _NN"));
    String result = morphology.lemma("   ", "NN");
    assertEquals("   ", result);
  }
@Test
  public void testStemEmptyTagSingleCharacterWord() {
    Morphology morphology = new Morphology(new StringReader("a_"));
    String result = morphology.lemma("a", "");
    assertEquals("a", result);
  }
@Test
  public void testLemmaWithTagContainingLineBreak() {
    Morphology morphology = new Morphology(new StringReader("walked_\n"));
    String result = morphology.lemma("walked", "\n");
    assertEquals("walk", result);
  }
@Test
  public void testApplyHandlesEmptyWordTag() {
    Morphology morphology = new Morphology(new StringReader("_"));
    WordTag input = new WordTag("", "");
    Object result = morphology.apply(input);
    assertTrue(result instanceof WordTag);
    assertEquals("", ((WordTag) result).word());
    assertEquals("", ((WordTag) result).tag());
  }
@Test
  public void testStemHandlesTabCharacter() {
    Morphology morphology = new Morphology(new StringReader("tab\tword_NN"));
    String result = morphology.lemma("tab\tword", "NN");
    assertEquals("tab\tword", result);
  }
@Test
  public void testStemProperNounWithNumbersPreserved() {
    Morphology morphology = new Morphology(new StringReader("Tesla3_NNP"));
    String result = morphology.lemma("Tesla3", "NNP");
    assertEquals("Tesla3", result);
  }
@Test
  public void testLemmaWithInvalidControlCharacters() {
    Morphology morphology = new Morphology(new StringReader("ab\u0007c_NN"));
    String result = morphology.lemma("ab\u0007c", "NN");
    assertEquals("ab\u0007c", result);
  }
@Test
  public void testStaticStemWithNullWordAndNonNullTag() {
    WordTag result = Morphology.stemStatic(null, "NN");
    assertNull(result.word());
    assertEquals("NN", result.tag());
  }
@Test
  public void testStaticStemWithNullWordAndNullTag() {
    WordTag result = Morphology.stemStatic(null, null);
    assertNull(result.word());
    assertNull(result.tag());
  }
@Test
  public void testStemHandlesMultipleSpacesAndUnderscoresInWord() {
    Morphology morphology = new Morphology(new StringReader("word__with__spaces_NN"));
    String result = morphology.lemma("word  with  spaces", "NN");
    assertEquals("word  with  spaces", result);
  }
@Test
  public void testStemWithLatexSymbolCharacters() {
    Morphology morphology = new Morphology(new StringReader("alpha$_$NN"));
    String result = morphology.lemma("alpha$", "$NN");
    assertEquals("alpha$", result);
  }
@Test
  public void testStemWithLongWordAndPunctuation() {
    Morphology morphology = new Morphology(new StringReader("unbelievable._JJ"));
    String result = morphology.lemma("unbelievable.", "JJ");
    assertEquals("unbelievable.", result);
  }
@Test
  public void testStemHandlesDuplicatePOSTagMarkers() {
    Morphology morphology = new Morphology(new StringReader("upper__NN_NN"));
    String result = morphology.lemma("upper_", "NN_NN");
    assertEquals("upper_", result);
  }
@Test
  public void testStemCoreLabelWithMissingTag() {
    Morphology morphology = new Morphology(new StringReader("missing_TAG"));
    CoreLabel label = new CoreLabel();
    label.setWord("missing");
    label.setTag(null);
    morphology.stem(label);
    assertEquals("missing", label.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testApplyWithCoreLabelContainingSpaceInToken() {
    Morphology morphology = new Morphology(new StringReader("machine learning_NN"));
    CoreLabel label = new CoreLabel();
    label.setWord("machine learning");
    label.setTag("NN");
    morphology.stem(label);
    assertEquals("machine learning", label.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testNullWordInWordLemmaTagInput() {
    Morphology morphology = new Morphology(new StringReader("_NN"));
    WordTag input = new WordTag(null, "NN");
    WordLemmaTag result = morphology.lemmatize(input);
    assertNull(result.word());
    assertEquals("NN", result.tag());
    assertEquals(null, result.lemma());
  }
@Test
  public void testStemStaticWithTabCharacterInWordTag() {
    WordTag wt = Morphology.stemStatic("field\t", "NN");
    assertEquals("field\t", wt.word());
    assertEquals("NN", wt.tag());
  }
@Test
  public void testStemWithWhitespaceTag() {
    Morphology morphology = new Morphology(new StringReader("talked_ "));
    String result = morphology.lemma("talked", " ");
    assertEquals("talk", result);
  }
@Test
  public void testApplyWithWordThatStartsWithUnderscore() {
    Morphology morphology = new Morphology(new StringReader("_hidden_NN"));
    Word word = new Word("_hidden");
    Object result = morphology.apply(word);
    assertTrue(result instanceof Word);
    assertEquals("_hidden", ((Word) result).word());
  }
@Test
  public void testApplyWithWordThatIsJustPunctuation() {
    Morphology morphology = new Morphology(new StringReader("._."));
    Word word = new Word(".");
    Object result = morphology.apply(word);
    assertTrue(result instanceof Word);
    assertEquals(".", ((Word) result).word());
  }
@Test
  public void testMultipleSequentialLemmaCallsWithDifferentTags() {
    Morphology morphology = new Morphology(new StringReader("running_VBG"));
    String result1 = morphology.lemma("running", "VBG");
    String result2 = morphology.lemma("running", "NN");
    assertEquals("run", result1);
    assertEquals("running", result2); 
  }
@Test
  public void testStaticStemWithWhitespaceOnlyWord() {
    WordTag result = Morphology.stemStatic("   ", "NN");
    assertEquals("   ", result.word());
    assertEquals("NN", result.tag());
  }
@Test
  public void testStaticLemmaWithNullWord() {
    String result = Morphology.lemmaStatic(null, "NN");
    assertNull(result);
  }
@Test
  public void testStaticLemmaWithNullTag() {
    String result = Morphology.lemmaStatic("running", null);
    assertEquals("running", result);
  }
@Test
  public void testStaticLemmaWithEmptyWordAndTag() {
    String result = Morphology.lemmaStatic("", "");
    assertEquals("", result);
  }
@Test
  public void testStaticLemmaHandlesSurrogatePairCharacters() {
    String emoji = "\uD83D\uDE00"; 
    String result = Morphology.lemmaStatic(emoji, "NN");
    assertEquals(emoji, result);
  }
@Test
  public void testApplyWordWithWhitespaceOnly() {
    Morphology morphology = new Morphology(new StringReader("   "));
    Word input = new Word("   ");
    Object result = morphology.apply(input);
    assertTrue(result instanceof Word);
    assertEquals("   ", ((Word) result).word());
  }
@Test
  public void testApplyWordTagWithOnlyWhitespaceFields() {
    Morphology morphology = new Morphology(new StringReader("   _   "));
    WordTag input = new WordTag("   ", "   ");
    Object result = morphology.apply(input);
    assertTrue(result instanceof WordTag);
    assertEquals("   ", ((WordTag) result).word());
    assertEquals("   ", ((WordTag) result).tag());
  }
@Test
  public void testStemWordTagWithUnderscoreAndNewlineCharacters() {
    Morphology morphology = new Morphology(new StringReader("a_b\nc_VBN"));
    String result = morphology.lemma("a_b\nc", "VBN");
    assertEquals("a_b\nc", result);  
  }
@Test
  public void testStemReturnsOriginalIfLexerThrowsIOException() {
    Morphology morphology = new Morphology(new Reader() {
      @Override public int read(char[] cbuf, int off, int len) throws IOException {
        throw new IOException("Simulated input failure");
      }
      @Override public void close() {}
    });
    String result = morphology.stem("failureTest");
    assertEquals("failureTest", result);
  }
@Test
  public void testMalformedWordTagInputThrowsGracefullyInMainStemMode() throws Exception {
    String[] args = {"-stem", "thisisbadformat"};
    Morphology.main(args);
    
  }
@Test
  public void testMainMethodRebuildVerbTableOutput() throws Exception {
    String[] args = {"-rebuildVerbTable", "src/test/resources/fakeVerbList.txt"};
    Morphology.main(args);
    
    
  }
@Test
  public void testMainHandlesNumericFlagParsing() throws Exception {
    String[] args = {"-1", "src/test/resources/tokenfile.txt"};
    Morphology.main(args);
    
  }
@Test
  public void testStemStaticWithGarbageControlCharacterInput() {
    WordTag result = Morphology.stemStatic("\u0003\u0004\u0005", "NN");
    assertNotNull(result);
    assertEquals("\u0003\u0004\u0005", result.word());
  }
@Test
  public void testStemWithMultiLinePOS() {
    Morphology morphology = new Morphology(new StringReader("wolves_\nNNS"));
    String result = morphology.lemma("wolves", "\nNNS");
    assertEquals("wolf", result);
  }
@Test
  public void testApplyHandlesNullTagOnWordTagGracefully() {
    Morphology morphology = new Morphology(new StringReader("valid_NN"));
    WordTag input = new WordTag("valid", null);
    Object result = morphology.apply(input);
    assertTrue(result instanceof WordTag);
    assertEquals("valid", ((WordTag) result).word());
  }
@Test
  public void testRepeatedStemCallsResetLexerCorrectly() {
    Morphology morphology = new Morphology(new StringReader("gathering_VBG"));
    String first = morphology.lemma("gathering", "VBG");
    String second = morphology.lemma("gathering", "VBG");
    assertEquals("gather", first);
    assertEquals("gather", second);
  }
@Test
  public void testStemWordWithEmojiCharacter() {
    Morphology morphology = new Morphology(new StringReader("😊_NN"));
    String result = morphology.lemma("😊", "NN");
    assertEquals("😊", result);  
  }
@Test
  public void testStemWithSurrogatePairCompoundWord() {
    Morphology morphology = new Morphology(new StringReader("𠮷野家_NN"));
    String input = "𠮷野家";
    String result = morphology.lemma(input, "NN");
    assertEquals(input, result);  
  }
@Test
  public void testLemmaWithControlCharacterInTagOnly() {
    Morphology morphology = new Morphology(new StringReader("bark_\u0007"));
    String result = morphology.lemma("bark", "\u0007");
    assertEquals("bark", result);
  }
@Test
  public void testStemHandlesUpperCaseWordsCorrectlyWithLowerCaseTrue() {
    Morphology morphology = new Morphology(new StringReader("WALKED_VBD"));
    String result = morphology.lemma("WALKED", "VBD", true);
    assertEquals("walk", result);
  }
@Test
  public void testCoreLabelStemFailureDoesNotThrow() {
    Morphology morphology = new Morphology(new StringReader("???_NN"));
    CoreLabel label = new CoreLabel();
    label.setWord("???");
    label.setTag("NN");
    morphology.stem(label);
    assertEquals("???", label.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testApplyReturnsUnchangedNonWordInput() {
    Morphology morphology = new Morphology(new StringReader("not used"));
    Object input = 123;
    Object output = morphology.apply(input);
    assertSame(input, output);
  }
@Test
  public void testStaticLemmatizeWithWordLackingTagStillReturnsIdentity() {
    WordTag wt = new WordTag("data", null);
    WordLemmaTag tag = Morphology.lemmatizeStatic(wt);
    assertEquals("data", tag.word());
    assertEquals("data", tag.lemma());
    assertNull(tag.tag());
  }
@Test
  public void testStemWordContainingSurrogatePairCharacters() {
    Morphology morphology = new Morphology(new StringReader("𐐷𐐯𐑌_NN"));
    String result = morphology.lemma("𐐷𐐯𐑌", "NN");
    assertEquals("𐐷𐐯𐑌", result);
  }
@Test
  public void testStemWordContainingNonBreakingSpace() {
    Morphology morphology = new Morphology(new StringReader("word\u00A0space_NN"));
    String result = morphology.lemma("word\u00A0space", "NN");
    assertEquals("word\u00A0space", result);
  }
@Test
  public void testWordWithSpaceAndUnderscoreTogether() {
    Morphology morphology = new Morphology(new StringReader("word_ with_underscore_NN"));
    String input = "word_ with_underscore";
    String result = morphology.lemma(input, "NN");
    assertEquals(input, result);
  }
@Test
  public void testStemInputWithControlCharactersInMiddle() {
    Morphology morphology = new Morphology(new StringReader("word\u0002tag_VB"));
    String input = "word\u0002tag";
    String result = morphology.lemma(input, "VB");
    assertEquals(input, result);
  }
@Test
  public void testStaticStemHandlesWhitespaceTag() {
    WordTag result = Morphology.stemStatic("jumped", " ");
    assertEquals("jump", result.word());
    assertEquals(" ", result.tag());
  }
@Test
  public void testStaticLemmaReturnsOriginalOnNulls() {
    String result = Morphology.lemmaStatic(null, null);
    assertNull(result);
  }
@Test
  public void testStaticLemmaMixedNullWord() {
    String result = Morphology.lemmaStatic(null, "NNS");
    assertNull(result);
  }
@Test
  public void testStaticLemmaMixedNullTag() {
    String result = Morphology.lemmaStatic("dogs", null);
    assertEquals("dogs", result); 
  }
@Test
  public void testApplyHandlesNonStringValue() {
    Morphology morphology = new Morphology(new StringReader("test_NN"));
    Object input = new Object();
    Object result = morphology.apply(input);
    assertEquals(input, result);
  }
@Test
  public void testStemCoreLabelWithNullWordStillSafe() {
    Morphology morphology = new Morphology(new StringReader("test_NN"));
    CoreLabel label = new CoreLabel();
    label.setWord(null);
    label.setTag("NN");
    morphology.stem(label);
    assertNull(label.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testStemCoreLabelWithBothWordAndTagNull() {
    Morphology morphology = new Morphology(new StringReader("abc_DEF"));
    CoreLabel label = new CoreLabel();
    label.setWord(null);
    label.setTag(null);
    morphology.stem(label);
    assertNull(label.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testStemStaticPreservesSpacing() {
    WordTag result = Morphology.stemStatic("hello world", "NN");
    assertEquals("hello world", result.word());
    assertEquals("NN", result.tag());
  }
@Test
  public void testStemHandlesOnlyNewlineWord() {
    Morphology morphology = new Morphology(new StringReader("\n_NN"));
    String result = morphology.lemma("\n", "NN");
    assertEquals("\n", result);
  }
@Test
  public void testLemmatizeStaticPreservesEmptyFields() {
    WordTag wt = new WordTag("", "");
    WordLemmaTag result = Morphology.lemmatizeStatic(wt);
    assertEquals("", result.word());
    assertEquals("", result.tag());
    assertEquals("", result.lemma());
  }
@Test
  public void testStemStaticWithWhitespaceWord() {
    WordTag result = Morphology.stemStatic(" \t ", "JJ");
    assertEquals(" \t ", result.word());
    assertEquals("JJ", result.tag());
  }
@Test
  public void testStemPreservesTagCase() {
    Morphology morphology = new Morphology(new StringReader("Games_NNS"));
    WordTag wt = new WordTag("Games", "NNS");
    WordTag result = (WordTag) morphology.apply(wt);
    assertEquals("game", result.word());
    assertEquals("NNS", result.tag());
  }
@Test
  public void testStemDigitsWithNounTag() {
    Morphology morphology = new Morphology(new StringReader("2023_NN"));
    String result = morphology.lemma("2023", "NN");
    assertEquals("2023", result);
  }
@Test
  public void testApplyWithEmptyCoreLabel() {
    Morphology morphology = new Morphology(new StringReader("blank_NN"));
    CoreLabel label = new CoreLabel();
    label.setWord("");
    label.setTag("");
    morphology.stem(label);
    assertEquals("", label.get(CoreAnnotations.LemmaAnnotation.class));
  }
@Test
  public void testApplyWithNullInputObject() {
    Morphology morphology = new Morphology(new StringReader("irrelevant_NN"));
    Object input = null;
    Object result = morphology.apply(input);
    assertNull(result);
  }
@Test
  public void testLemmatizeWithPunctuationOnlyTag() {
    Morphology morphology = new Morphology(new StringReader("testing_.."));
    String result = morphology.lemma("testing", "..");
    assertEquals("test", result); 
  }
@Test
  public void testStemWithHighSurrogatesAndLowSurrogatesInWord() {
    Morphology morphology = new Morphology(new StringReader("𠀋𠂤𠃌_NN"));
    String input = "𠀋𠂤𠃌";
    String result = morphology.lemma(input, "NN");
    assertEquals(input, result);
  }
@Test
  public void testStemWordContainingZeroWidthSpace() {
    Morphology morphology = new Morphology(new StringReader("zero\u200Bwidth_NN"));
    String input = "zero\u200Bwidth";
    String result = morphology.lemma(input, "NN");
    assertEquals(input, result);
  }
@Test
  public void testStemNumericLiteralWithVerbTag() {
    Morphology morphology = new Morphology(new StringReader("1234_VBD"));
    String result = morphology.lemma("1234", "VBD");
    assertEquals("1234", result);  
  }
@Test
  public void testApplyWithInvalidCustomTypeObject() {
    Morphology morphology = new Morphology(new StringReader("not_used_NN"));
    Object custom = new Object() {
      @Override public String toString() { return "invalid"; }
    };
    Object result = morphology.apply(custom);
    assertSame(custom, result);
  }
@Test
  public void testStemStaticWithMultiLangWord() {
    WordTag result = Morphology.stemStatic("schön", "JJ");
    assertEquals("schön", result.word());  
  }
@Test
  public void testApplyHandlesEmptyWordTagObject() {
    Morphology morphology = new Morphology(new StringReader("_"));
    WordTag input = new WordTag("", "");
    Object result = morphology.apply(input);
    assertTrue(result instanceof WordTag);
    assertEquals("", ((WordTag) result).word());
    assertEquals("", ((WordTag) result).tag());
  }
@Test
  public void testLemmatizeHandlesTagWithTrailingWhitespace() {
    Morphology morphology = new Morphology(new StringReader("kicked_VBD "));
    String result = morphology.lemma("kicked", "VBD ");
    assertEquals("kick", result);  
  }
@Test
  public void testStemStaticDoesNotCrashWithNullValues() {
    WordTag result = Morphology.stemStatic(null, null);
    assertNull(result.word());
    assertNull(result.tag());
  }
@Test
  public void testStemWithMixedNewlineSpaceUnderscoreInWord() {
    Morphology morphology = new Morphology(new StringReader("a b\n_c_d_NN"));
    String input = "a b\n_c_d";
    String result = morphology.lemma(input, "NN");
    assertEquals("a b\n_c_d", result);
  }
@Test
  public void testStemWithTagContainingDigits() {
    Morphology morphology = new Morphology(new StringReader("pushed_VERB123"));
    String result = morphology.lemma("pushed", "VERB123");
    assertEquals("push", result);  
  }
@Test
  public void testStemHandlesExceptionFromBrokenReader() {
    Morphology morphology = new Morphology(new Reader() {
      @Override public int read(char[] cbuf, int off, int len) throws IOException {
        throw new IOException("Simulated error");
      }
      @Override public void close() {}
    });
    String stemmed = morphology.stem("faultyToken");
    assertEquals("faultyToken", stemmed);  
  }
@Test
  public void testLemmatizeHandlesEmojiAndReturnsOriginal() {
    Morphology morphology = new Morphology(new StringReader("😂_RB"));
    String emoji = "😂";
    String result = morphology.lemma(emoji, "RB");
    assertEquals(emoji, result);
  }
@Test
  public void testLemmatizeReplacesEscapeSubstitutionsCorrectly() {
    Morphology morphology = new Morphology(new StringReader("foo\u1CF0bar\u1CF1baz\u1CF2qux_TAG"));
    String input = "foo_bar baz\nqux";
    String result = morphology.lemma(input, "TAG");
    assertEquals("foo_bar baz\nqux", result);
  }
@Test
  public void testLemmatizeStaticReturnsOriginalIfStemFails() {
    WordTag input = new WordTag(":::INVALID:::", "SYM");
    WordLemmaTag result = Morphology.lemmatizeStatic(input);
    assertEquals(":::INVALID:::", result.word());
    assertEquals(":::INVALID:::", result.lemma());
    assertEquals("SYM", result.tag());
  }
@Test
  public void testStemWithLeadingAndTrailingUnderscores() {
    Morphology morphology = new Morphology(new StringReader("_token__VB"));
    String result = morphology.lemma("_token_", "VB");
    assertEquals("_token_", result);
  }
@Test
  public void testStemEmptyStringWordAndValidTag() {
    Morphology morphology = new Morphology(new StringReader("_NN"));
    String result = morphology.lemma("", "NN");
    assertEquals("", result);
  }
@Test
  public void testStemValidWordAndEmptyStringTag() {
    Morphology morphology = new Morphology(new StringReader("walking_"));
    String result = morphology.lemma("walking", "");
    assertEquals("walk", result);
  }
@Test
  public void testApplyWithOnlyWhitespaceStringWordTag() {
    Morphology morphology = new Morphology(new StringReader("   _   "));
    WordTag wordTag = new WordTag("   ", "   ");
    Object result = morphology.apply(wordTag);
    assertTrue(result instanceof WordTag);
    assertEquals("   ", ((WordTag) result).word());
    assertEquals("   ", ((WordTag) result).tag());
  }
@Test
  public void testLemmatizeWordTagWithSurrogateCharacters() {
    Morphology morphology = new Morphology(new StringReader("\uD83D\uDC4D_VB"));
    WordTag input = new WordTag("👍", "VB");
    WordLemmaTag result = morphology.lemmatize(input);
    assertEquals("👍", result.word());
    assertEquals("👍", result.lemma());
    assertEquals("VB", result.tag());
  } 
}