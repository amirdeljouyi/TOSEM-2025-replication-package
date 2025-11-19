package edu.stanford.nlp.process;


import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

import edu.stanford.nlp.io.RuntimeIOException;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.util.StringUtils;
import org.junit.Test;

import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.international.negra.NegraPennLanguagePack;


public class PTBTokenizer_4_GPTLLMTest {

 @Test
  public void testTokenizeWithPTB3EscapingEnabled() {
    String input = "A (tokenized) sentence.";
    Reader reader = new StringReader(input);
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "ptb3Escaping=true");

    CoreLabel t1 = tokenizer.next();
    CoreLabel t2 = tokenizer.next();
    CoreLabel t3 = tokenizer.next();
    CoreLabel t4 = tokenizer.next();
    CoreLabel t5 = tokenizer.next();
    CoreLabel t6 = tokenizer.next();

    assertEquals("A", t1.word());
    assertEquals("-LRB-", t2.word());
    assertEquals("tokenized", t3.word());
    assertEquals("-RRB-", t4.word());
    assertEquals("sentence", t5.word());
    assertEquals(".", t6.word());
  }
@Test
  public void testTokenizeWithPTB3EscapingDisabled() {
    String input = "A (tokenized) sentence.";
    Reader reader = new StringReader(input);
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "ptb3Escaping=false");

    CoreLabel t1 = tokenizer.next();
    CoreLabel t2 = tokenizer.next();
    CoreLabel t3 = tokenizer.next();
    CoreLabel t4 = tokenizer.next();
    CoreLabel t5 = tokenizer.next();
    CoreLabel t6 = tokenizer.next();

    assertEquals("A", t1.word());
    assertEquals("(", t2.word());
    assertEquals("tokenized", t3.word());
    assertEquals(")", t4.word());
    assertEquals("sentence", t5.word());
    assertEquals(".", t6.word());
  }
@Test
  public void testTokenizeNewlineAsToken() {
    String input = "Line1\nLine2";
    Reader reader = new StringReader(input);
    PTBTokenizer<CoreLabel> tokenizer = PTBTokenizer.newPTBTokenizer(reader, true, false);

    CoreLabel t1 = tokenizer.next();
    CoreLabel t2 = tokenizer.next();
    CoreLabel t3 = tokenizer.next();

    assertEquals("Line1", t1.word());
    assertEquals(PTBTokenizer.getNewlineToken(), t2.word());
    assertEquals("Line2", t3.word());
  }
@Test
  public void testEmptyInputReturnsNoTokens() {
    String input = "";
    Reader reader = new StringReader(input);
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "ptb3Escaping=true");

    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testOnlyPunctuation() {
    String input = "!?";
    Reader reader = new StringReader(input);
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "ptb3Escaping=true");

    CoreLabel t1 = tokenizer.next();
    CoreLabel t2 = tokenizer.next();

    assertEquals("!", t1.word());
    assertEquals("?", t2.word());
  }
@Test
  public void testUnicodeFractionNormalizationEnabled() {
    String input = "½";
    Reader reader = new StringReader(input);
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "normalizeFractions=true");

    CoreLabel t1 = tokenizer.next();

    assertEquals("1/2", t1.word());
  }
@Test
  public void testQuotesRemainOriginalWhenOptionSet() {
    String input = "\"hello\"";
    Reader reader = new StringReader(input);
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "ptb3Escaping=false,quotes=original");

    CoreLabel t1 = tokenizer.next();
    CoreLabel t2 = tokenizer.next();
    CoreLabel t3 = tokenizer.next();

    assertEquals("\"", t1.word());
    assertEquals("hello", t2.word());
    assertEquals("\"", t3.word());
  }
@Test
  public void testSplitHyphenatedTokens() {
    String input = "school-aged";
    Reader reader = new StringReader(input);
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "splitHyphenated=true");

    CoreLabel t1 = tokenizer.next();
    CoreLabel t2 = tokenizer.next();
    CoreLabel t3 = tokenizer.next();

    assertEquals("school", t1.word());
    assertEquals("-", t2.word());
    assertEquals("aged", t3.word());
  }
@Test
  public void testPtbToken2TextReplacesBracketSymbol() {
    String input = "-LRB-";
    String converted = PTBTokenizer.ptbToken2Text(input);
    assertEquals("(", converted);
  }
@Test
  public void testPtb2TextReconstructsFromEscapedString() {
    String input = "Hello -LRB- world -RRB- !";
    String output = PTBTokenizer.ptb2Text(input);
    assertEquals("Hello (world)!", output);
  }
@Test
  public void testPtb2TextFromListOfStrings() {
    List<String> tokens = Arrays.asList("Goodbye", "-LRB-", "world", "-RRB-", "!");
    String output = PTBTokenizer.ptb2Text(tokens);
    assertEquals("Goodbye (world)!", output);
  }
@Test
  public void testLabelList2TextReconstructsOutput() {
    HasWord w1 = new Word("Try");
    HasWord w2 = new Word("-LRB-");
    HasWord w3 = new Word("this");
    HasWord w4 = new Word("-RRB-");
    HasWord w5 = new Word("out");

    List<HasWord> wordList = new ArrayList<>();
    wordList.add(w1);
    wordList.add(w2);
    wordList.add(w3);
    wordList.add(w4);
    wordList.add(w5);

    String output = PTBTokenizer.labelList2Text(wordList);
    assertEquals("Try (this) out", output);
  }
@Test
  public void testTokenizerFactoryGeneratesExpectedTokens() {
    String input = "Sample text.";
    Reader reader = new StringReader(input);

    PTBTokenizer.PTBTokenizerFactory<CoreLabel> factory =
        PTBTokenizer.PTBTokenizerFactory.newCoreLabelTokenizerFactory("invertible");

    PTBTokenizer<CoreLabel> tokenizer = (PTBTokenizer<CoreLabel>) factory.getTokenizer(reader);

    CoreLabel t1 = tokenizer.next();
    CoreLabel t2 = tokenizer.next();
    CoreLabel t3 = tokenizer.next();

    assertEquals("Sample", t1.word());
    assertEquals("text", t2.word());
    assertEquals(".", t3.word());
  }
@Test(expected = RuntimeIOException.class)
  public void testIOExceptionHandlingInTokenizer() {
    Reader badReader = new Reader() {
      public int read(char[] cbuf, int off, int len) throws IOException {
        throw new IOException("Forced read error");
      }
      public void close() {
      }
    };

    PTBTokenizer<Word> tokenizer = PTBTokenizer.newPTBTokenizer(badReader);
    tokenizer.next();
  }
@Test
  public void testEmojiCharacterAsUntokenizable() {
    String input = "This 😊 emoji";
    Reader reader = new StringReader(input);
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "untokenizable=noneKeep");

    CoreLabel t1 = tokenizer.next();
    CoreLabel t2 = tokenizer.next();
    CoreLabel t3 = tokenizer.next();

    assertEquals("This", t1.word());
    assertEquals("😊", t2.word());
    assertEquals("emoji", t3.word());
  }
@Test
  public void testFactorySetOptionsUpdatesBehavior() {
    String input = "Function (test)";
    Reader reader = new StringReader(input);

    PTBTokenizer.PTBTokenizerFactory<CoreLabel> factory =
        PTBTokenizer.PTBTokenizerFactory.newCoreLabelTokenizerFactory("ptb3Escaping=false");

    factory.setOptions("ptb3Escaping=true");

    PTBTokenizer<CoreLabel> tokenizer = (PTBTokenizer<CoreLabel>) factory.getTokenizer(reader);

    CoreLabel t1 = tokenizer.next(); 
    CoreLabel t2 = tokenizer.next(); 
    CoreLabel t3 = tokenizer.next(); 
    CoreLabel t4 = tokenizer.next(); 

    assertEquals("Function", t1.word());
    assertEquals("-LRB-", t2.word());
    assertEquals("test", t3.word());
    assertEquals("-RRB-", t4.word());
  }
@Test
public void testConsecutiveWhitespaceHandling() {
  String input = "This    is\t\t a  test.";
  Reader reader = new StringReader(input);
  PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "ptb3Escaping=true");

  CoreLabel t1 = tokenizer.next();
  CoreLabel t2 = tokenizer.next();
  CoreLabel t3 = tokenizer.next();
  CoreLabel t4 = tokenizer.next();
  CoreLabel t5 = tokenizer.next();

  assertEquals("This", t1.word());
  assertEquals("is", t2.word());
  assertEquals("a", t3.word());
  assertEquals("test", t4.word());
  assertEquals(".", t5.word());
}
@Test
public void testDashNormalizationToUnicode() {
  String input = "hyphen - dash -- em---dash";
  Reader reader = new StringReader(input);
  PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "dashes=unicode");

  CoreLabel t1 = tokenizer.next();
  CoreLabel t2 = tokenizer.next();
  CoreLabel t3 = tokenizer.next();
  CoreLabel t4 = tokenizer.next();
  CoreLabel t5 = tokenizer.next();
  CoreLabel t6 = tokenizer.next();

  assertEquals("hyphen", t1.word());
  assertEquals("–", t2.word());
  assertEquals("dash", t3.word());
  assertEquals("—", t4.word());
  assertEquals("em", t5.word());
  assertEquals("—", t6.word()); 
}
@Test
public void testAmpersandEntityNormalizationEnabled() {
  String input = "Fish &amp; Chips";
  Reader reader = new StringReader(input);
  PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "normalizeAmpersandEntity=true");

  CoreLabel t1 = tokenizer.next();
  CoreLabel t2 = tokenizer.next();
  CoreLabel t3 = tokenizer.next();
  CoreLabel t4 = tokenizer.next();

  assertEquals("Fish", t1.word());
  assertEquals("&", t2.word());
  assertEquals("Chips", t3.word());
}
@Test
public void testHasNextBehaviorAfterEOF() {
  String input = "Final.";
  Reader reader = new StringReader(input);
  PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "");

  CoreLabel t1 = tokenizer.next();
  CoreLabel t2 = tokenizer.next();

  assertEquals("Final", t1.word());
  assertEquals(".", t2.word());

  boolean hasMore = tokenizer.hasNext();
  assertFalse(hasMore);
}
@Test
public void testMixedQuotesTokenization() {
  String input = "He said, “That's ‘awesome’.”";
  Reader reader = new StringReader(input);
  PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "quotes=unicode");

  CoreLabel t1 = tokenizer.next(); 
  CoreLabel t2 = tokenizer.next(); 
  CoreLabel t3 = tokenizer.next(); 
  CoreLabel t4 = tokenizer.next(); 
  CoreLabel t5 = tokenizer.next(); 
  CoreLabel t6 = tokenizer.next(); 
  CoreLabel t7 = tokenizer.next(); 
  CoreLabel t8 = tokenizer.next(); 
  CoreLabel t9 = tokenizer.next(); 
  CoreLabel t10 = tokenizer.next(); 

  assertEquals("He", t1.word());
  assertEquals("“", t4.word());
  assertEquals("‘", t6.word());
  assertEquals("’", t8.word());
  assertEquals("”", t10.word());
}
@Test
public void testEndsWithNewline() {
  String input = "Ends with newline\n";
  Reader reader = new StringReader(input);
  PTBTokenizer<CoreLabel> tokenizer = PTBTokenizer.newPTBTokenizer(reader, true, false);

  CoreLabel t1 = tokenizer.next();
  CoreLabel t2 = tokenizer.next();
  CoreLabel t3 = tokenizer.next();
  CoreLabel t4 = tokenizer.next();

  assertEquals("Ends", t1.word());
  assertEquals("with", t2.word());
  assertEquals("newline", t3.word());
  assertEquals(PTBTokenizer.getNewlineToken(), t4.word());
}
@Test
public void testSingleLongToken() {
  String input = "supercalifragilisticexpialidocious!";
  Reader reader = new StringReader(input);
  PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "");

  CoreLabel t1 = tokenizer.next();
  CoreLabel t2 = tokenizer.next();

  assertEquals("supercalifragilisticexpialidocious", t1.word());
  assertEquals("!", t2.word());
}
@Test
public void testSuppressEscapingOption() {
  String input = "Euro sign € and ampersand &";
  Reader reader = new StringReader(input);
  PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "ptb3Escaping=false");

  CoreLabel t1 = tokenizer.next(); 
  CoreLabel t2 = tokenizer.next(); 
  CoreLabel t3 = tokenizer.next(); 
  CoreLabel t4 = tokenizer.next(); 
  CoreLabel t5 = tokenizer.next(); 
  CoreLabel t6 = tokenizer.next(); 

  assertEquals("€", t3.word());
  assertEquals("&", t6.word());
}
@Test
public void testSGMLTagInputPreservesLine() {
  String input = "<tag>valid</tag>";
  Reader reader = new StringReader(input);
  PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "");

  CoreLabel t1 = tokenizer.next(); 
  CoreLabel t2 = tokenizer.next(); 
  CoreLabel t3 = tokenizer.next(); 

  assertEquals("<tag>", t1.word());
  assertEquals("valid", t2.word());
  assertEquals("</tag>", t3.word());
}
@Test
public void testMultipleUntokenizableConfigModes() {
  String input = "@@";
  Reader readerDelete = new StringReader(input);
  Reader readerKeep = new StringReader(input);

  PTBTokenizer<CoreLabel> tokenizerDelete = new PTBTokenizer<>(readerDelete, new CoreLabelTokenFactory(), "untokenizable=allDelete");
  PTBTokenizer<CoreLabel> tokenizerKeep = new PTBTokenizer<>(readerKeep, new CoreLabelTokenFactory(), "untokenizable=allKeep");

  boolean hasTokenDelete = tokenizerDelete.hasNext();
  CoreLabel tokenKeep = tokenizerKeep.next();

  assertFalse(hasTokenDelete);
  assertEquals("@", tokenKeep.word());
}
@Test
public void testEmptyTokenStreamWithSpacesOnly() {
  String input = "     \t\n  ";
  Reader reader = new StringReader(input);
  PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "ptb3Escaping=true");

  boolean has = tokenizer.hasNext();
  assertFalse(has);
}
@Test
public void testEllipsesUnicodeNormalization() {
  String input = "Dot ... or space . . . before";
  Reader reader = new StringReader(input);
  PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "ellipses=unicode");

  CoreLabel t1 = tokenizer.next(); 
  CoreLabel t2 = tokenizer.next(); 
  CoreLabel t3 = tokenizer.next(); 
  CoreLabel t4 = tokenizer.next(); 
  CoreLabel t5 = tokenizer.next(); 
  CoreLabel t6 = tokenizer.next(); 

  assertEquals("Dot", t1.word());
  assertEquals("…", t2.word());
  assertEquals("or", t3.word());
  assertEquals("space", t4.word());
  assertEquals("…", t5.word());
  assertEquals("before", t6.word());
}
@Test
public void testUnicodeQuoteNotConvertedInOriginalMode() {
  String input = "“Quoted” word.";
  Reader reader = new StringReader(input);
  PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "quotes=original");

  CoreLabel t1 = tokenizer.next(); 
  CoreLabel t2 = tokenizer.next(); 
  CoreLabel t3 = tokenizer.next(); 
  CoreLabel t4 = tokenizer.next(); 
  CoreLabel t5 = tokenizer.next(); 

  assertEquals("“", t1.word());
  assertEquals("Quoted", t2.word());
  assertEquals("”", t3.word());
}

@Test
public void testStrictFractionModeSeparatesWholeAndFraction() {
  String input = "5 7/8 shares";
  Reader readerStrict = new StringReader(input);
  Reader readerLoose = new StringReader(input);

  PTBTokenizer<CoreLabel> tokenizerStrict = new PTBTokenizer<>(readerStrict, new CoreLabelTokenFactory(), "strictFraction=true");
  PTBTokenizer<CoreLabel> tokenizerLoose = new PTBTokenizer<>(readerLoose, new CoreLabelTokenFactory(), "strictFraction=false");

  CoreLabel s1 = tokenizerStrict.next();
  CoreLabel s2 = tokenizerStrict.next();
  CoreLabel s3 = tokenizerStrict.next();

  CoreLabel l1 = tokenizerLoose.next();
  CoreLabel l2 = tokenizerLoose.next();

  assertEquals("5", s1.word());
  assertEquals("7/8", s2.word());
  assertEquals("shares", s3.word());

  assertEquals("5\u00A07/8", l1.word()); 
  assertEquals("shares", l2.word());
}
@Test
public void testOnlySGMLTag() {
  String input = "<s>";
  Reader reader = new StringReader(input);
  PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "");

  CoreLabel t1 = tokenizer.next();

  assertEquals("<s>", t1.word());
  assertFalse(tokenizer.hasNext());
}
@Test
public void testCurrencyNormalizationEnabled() {
  String input = "£100 and ¥800";
  Reader reader = new StringReader(input);
  PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "normalizeCurrency=true");

  CoreLabel t1 = tokenizer.next(); 
  CoreLabel t2 = tokenizer.next(); 
  CoreLabel t3 = tokenizer.next(); 
  CoreLabel t4 = tokenizer.next(); 
  CoreLabel t5 = tokenizer.next(); 

  assertEquals("$", t1.word());
  assertEquals("#", t4.word());
}
@Test
public void testEscapeForwardSlashAsteriskEnabled() {
  String input = "/path *bold*";
  Reader reader = new StringReader(input);
  PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(
      reader,
      new CoreLabelTokenFactory(),
      "escapeForwardSlashAsterisk=true"
  );

  CoreLabel t1 = tokenizer.next(); 
  CoreLabel t2 = tokenizer.next(); 
  CoreLabel t3 = tokenizer.next(); 
  CoreLabel t4 = tokenizer.next(); 
  CoreLabel t5 = tokenizer.next(); 

  assertEquals("\\/", t1.word());
  assertEquals("\\*", t3.word());
  assertEquals("\\*", t5.word());
}
@Test
public void testSplitForwardSlashEnabled() {
  String input = "man/woman frog/toad";
  Reader reader = new StringReader(input);
  PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(
      reader,
      new CoreLabelTokenFactory(),
      "splitForwardSlash=true"
  );

  CoreLabel t1 = tokenizer.next(); 
  CoreLabel t2 = tokenizer.next(); 
  CoreLabel t3 = tokenizer.next(); 
  CoreLabel t4 = tokenizer.next(); 
  CoreLabel t5 = tokenizer.next(); 
  CoreLabel t6 = tokenizer.next(); 

  assertEquals("man", t1.word());
  assertEquals("/", t2.word());
  assertEquals("woman", t3.word());
  assertEquals("frog", t4.word());
  assertEquals("/", t5.word());
  assertEquals("toad", t6.word());
}
@Test(expected = RuntimeIOException.class)
public void testInvalidRegexForFilterThrowsNoCrash() {
  String input = "Hello world";
  String badRegex = "(.*invalid)";

  java.util.Properties props = new java.util.Properties();
  props.setProperty("filter", badRegex);

  
  java.util.regex.Pattern.compile(badRegex); 

  
}
@Test
public void testSingleCharacter() {
  String input = "x";
  Reader reader = new StringReader(input);
  PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "");

  CoreLabel t1 = tokenizer.next();
  assertEquals("x", t1.word());

  boolean hasMore = tokenizer.hasNext();
  assertFalse(hasMore);
}
@Test(expected = NoSuchElementException.class)
public void testRepeatedNextAfterEnd() {
  String input = "Done.";
  Reader reader = new StringReader(input);
  PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "");

  tokenizer.next(); 
  tokenizer.next(); 
  tokenizer.next(); 
}
@Test
public void testConsecutivePunctuation() {
  String input = "What?!";
  Reader reader = new StringReader(input);
  PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "");

  CoreLabel t1 = tokenizer.next();
  CoreLabel t2 = tokenizer.next();
  CoreLabel t3 = tokenizer.next();

  assertEquals("What", t1.word());
  assertEquals("?", t2.word());
  assertEquals("!", t3.word());
}
@Test
public void testOnlyPunctuationCharacters() {
  String input = ",.!?";
  Reader reader = new StringReader(input);
  PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "");

  CoreLabel t1 = tokenizer.next();
  CoreLabel t2 = tokenizer.next();
  CoreLabel t3 = tokenizer.next();
  CoreLabel t4 = tokenizer.next();

  assertEquals(",", t1.word());
  assertEquals(".", t2.word());
  assertEquals("!", t3.word());
  assertEquals("?", t4.word());
}
@Test
public void testUnicodeMathSymbols() {
  String input = "∑ ∞ ≠ ∂";
  Reader reader = new StringReader(input);
  PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "ptb3Escaping=false");

  CoreLabel t1 = tokenizer.next();
  CoreLabel t2 = tokenizer.next();
  CoreLabel t3 = tokenizer.next();
  CoreLabel t4 = tokenizer.next();

  assertEquals("∑", t1.word());
  assertEquals("∞", t2.word());
  assertEquals("≠", t3.word());
  assertEquals("∂", t4.word());
}
@Test
public void testVeryLongToken() {
  StringBuilder sb = new StringBuilder();
  for (int i = 0; i < 10000; i++) {
    sb.append('a');
  }
  String input = sb.toString();
  Reader reader = new StringReader(input);
  PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "");

  CoreLabel t1 = tokenizer.next();
  assertEquals(input, t1.word());
  assertFalse(tokenizer.hasNext());
}
@Test
public void testTokenizeNLsFalseSkipsNewlines() {
  String input = "Line1\nLine2";
  Reader reader = new StringReader(input);
  PTBTokenizer<CoreLabel> tokenizer = PTBTokenizer.newPTBTokenizer(reader, false, false);

  CoreLabel t1 = tokenizer.next();
  CoreLabel t2 = tokenizer.next();

  assertEquals("Line1", t1.word());
  assertEquals("Line2", t2.word());
  assertFalse(tokenizer.hasNext());
}
@Test
public void testWhitespaceAroundPunctuation() {
  String input = "Hello , world .";
  Reader reader = new StringReader(input);
  PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "");

  CoreLabel t1 = tokenizer.next(); 
  CoreLabel t2 = tokenizer.next(); 
  CoreLabel t3 = tokenizer.next(); 
  CoreLabel t4 = tokenizer.next(); 

  assertEquals("Hello", t1.word());
  assertEquals(",", t2.word());
  assertEquals("world", t3.word());
  assertEquals(".", t4.word());
}
@Test
public void testQuotePreservationWithUnicodeQuoteOption() {
  String input = "“Smart quotes” are nice.";
  Reader reader = new StringReader(input);
  PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(
      reader,
      new CoreLabelTokenFactory(),
      "quotes=unicode"
  );

  CoreLabel t1 = tokenizer.next(); 
  CoreLabel t2 = tokenizer.next(); 
  CoreLabel t3 = tokenizer.next(); 
  CoreLabel t4 = tokenizer.next(); 
  CoreLabel t5 = tokenizer.next(); 
  CoreLabel t6 = tokenizer.next(); 
  CoreLabel t7 = tokenizer.next(); 

  assertEquals("“", t1.word());
  assertEquals("”", t4.word());
  assertEquals(".", t7.word());
}
@Test
public void testNonBreakingSpaceNormalization() {
  String input = "Price 2\u00A099/100";
  Reader reader = new StringReader(input);
  PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(
      reader,
      new CoreLabelTokenFactory(),
      "normalizeSpace=true"
  );

  CoreLabel t1 = tokenizer.next(); 
  CoreLabel t2 = tokenizer.next(); 
  CoreLabel t3 = tokenizer.next(); 

  assertEquals("Price", t1.word());
  assertEquals("2", t2.word());
  assertTrue(t3.word().contains("99/100")); 
}
@Test
public void testSplitAssimilationsEnabled() {
  String input = "gonna";
  Reader reader = new StringReader(input);
  PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(
      reader,
      new CoreLabelTokenFactory(),
      "splitAssimilations=false"
  );

  CoreLabel t1 = tokenizer.next();
  assertEquals("gonna", t1.word());
  assertFalse(tokenizer.hasNext());
}
@Test
public void testUnsplitAssimilations() {
  String input = "gonna";
  Reader reader = new StringReader(input);
  PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(
      reader,
      new CoreLabelTokenFactory(),
      "splitAssimilations=true"
  );

  CoreLabel t1 = tokenizer.next();
  assertEquals("gon", t1.word());
  CoreLabel t2 = tokenizer.next();
  assertEquals("na", t2.word());
  assertFalse(tokenizer.hasNext());
}
@Test
public void testEscapeForwardSlashAsteriskFalse() {
  String input = "/start *end*";
  Reader reader = new StringReader(input);
  PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(
      reader,
      new CoreLabelTokenFactory(),
      "escapeForwardSlashAsterisk=false"
  );

  CoreLabel t1 = tokenizer.next(); 
  CoreLabel t2 = tokenizer.next(); 
  CoreLabel t3 = tokenizer.next(); 
  CoreLabel t4 = tokenizer.next(); 
  CoreLabel t5 = tokenizer.next(); 

  assertEquals("/", t1.word());
  assertEquals("*", t3.word());
  assertEquals("end", t4.word());
  assertEquals("*", t5.word());
}
@Test
public void testStrictAcronymOption() {
  String input = "U.S.";
  Reader reader = new StringReader(input);
  PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(
      reader,
      new CoreLabelTokenFactory(),
      "strictAcronym=true"
  );

  CoreLabel t1 = tokenizer.next();
  CoreLabel t2 = tokenizer.next();

  assertEquals("U.S.", t1.word());
  assertEquals(".", t2.word());
  assertFalse(tokenizer.hasNext());
}
@Test
public void testHtmlAmpEntityWithoutNormalization() {
  String input = "A &amp; B";
  Reader reader = new StringReader(input);
  PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(
      reader,
      new CoreLabelTokenFactory(),
      "normalizeAmpersandEntity=false"
  );

  CoreLabel t1 = tokenizer.next(); 
  CoreLabel t2 = tokenizer.next(); 
  CoreLabel t3 = tokenizer.next(); 

  assertEquals("A", t1.word());
  assertEquals("&amp;", t2.word());
  assertEquals("B", t3.word());
}
@Test
public void testTokenizePerLineEffectivelyDelimitsInput() {
  String input = "<s>A.B</s>\n<s>C.D</s>";
  Reader reader = new StringReader(input);
  PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(
      reader,
      new CoreLabelTokenFactory(),
      "tokenizePerLine=true"
  );

  CoreLabel t1 = tokenizer.next(); 
  CoreLabel t2 = tokenizer.next(); 
  CoreLabel t3 = tokenizer.next(); 
  CoreLabel t4 = tokenizer.next(); 
  CoreLabel t5 = tokenizer.next(); 
  CoreLabel t6 = tokenizer.next(); 

  assertEquals("<s>", t1.word());
  assertEquals("A.B", t2.word());
  assertEquals("</s>", t3.word());
  assertEquals("<s>", t4.word());
  assertEquals("C.D", t5.word());
  assertEquals("</s>", t6.word());
}
@Test
public void testAcronymFollowedBySentenceEndWithStrictTreebank() {
  String input = "Corp.";
  Reader reader = new StringReader(input);
  PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(
      reader,
      new CoreLabelTokenFactory(),
      "strictTreebank3=true"
  );

  CoreLabel t1 = tokenizer.next(); 
  CoreLabel t2 = tokenizer.next(); 

  assertEquals("Corp", t1.word());
  assertEquals(".", t2.word());
}
@Test
public void testMultipleTokensWithDifferentBrackets() {
  String input = "{foo} [bar] (baz)";
  Reader reader = new StringReader(input);
  PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(
      reader,
      new CoreLabelTokenFactory(),
      "normalizeOtherBrackets=true"
  );

  CoreLabel t1 = tokenizer.next(); 
  CoreLabel t2 = tokenizer.next(); 
  CoreLabel t3 = tokenizer.next(); 
  CoreLabel t4 = tokenizer.next(); 
  CoreLabel t5 = tokenizer.next(); 
  CoreLabel t6 = tokenizer.next(); 
  CoreLabel t7 = tokenizer.next(); 
  CoreLabel t8 = tokenizer.next(); 
  CoreLabel t9 = tokenizer.next(); 

  assertEquals("-LCB-", t1.word());
  assertEquals("-RCB-", t3.word());
  assertEquals("-LSB-", t4.word());
  assertEquals("-RSB-", t6.word());
  assertEquals("-LRB-", t7.word());
  assertEquals("-RRB-", t9.word());
}
@Test
public void testNormalizeSpaceDisabledAllowsRegularSpaces() {
  String input = "2 1/2";
  Reader reader = new StringReader(input);
  PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(
      reader,
      new CoreLabelTokenFactory(),
      "normalizeSpace=false"
  );

  CoreLabel t1 = tokenizer.next();
  CoreLabel t2 = tokenizer.next();

  assertEquals("2", t1.word());
  assertEquals("1/2", t2.word());
}
@Test
public void testNullOptionsStringDefaultsToPTB3EscapingTrue() {
  String input = "(text)";
  Reader reader = new StringReader(input);
  PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), null);

  CoreLabel t1 = tokenizer.next(); 
  CoreLabel t2 = tokenizer.next(); 
  CoreLabel t3 = tokenizer.next(); 

  assertEquals("-LRB-", t1.word());
  assertEquals("text", t2.word());
  assertEquals("-RRB-", t3.word());
}
@Test
public void testDynamicallySetTokenizerFactoryOptions() {
  String input = "@home";
  Reader reader = new StringReader(input);

  PTBTokenizer.PTBTokenizerFactory<CoreLabel> factory = (PTBTokenizer.PTBTokenizerFactory<CoreLabel>) PTBTokenizer.coreLabelFactory("");
  factory.setOptions("untokenizable=noneKeep");

  PTBTokenizer<CoreLabel> tokenizer = (PTBTokenizer<CoreLabel>) factory.getTokenizer(reader);

  CoreLabel t1 = tokenizer.next(); 
  CoreLabel t2 = tokenizer.next(); 

  assertEquals("@", t1.word());
  assertEquals("home", t2.word());
}
@Test
public void testTokenizeNewlinePreservesNewlineToken() {
  String input = "token1\ntoken2";
  Reader reader = new StringReader(input);
  PTBTokenizer<CoreLabel> tokenizer = PTBTokenizer.newPTBTokenizer(reader, true, false);

  CoreLabel t1 = tokenizer.next(); 
  CoreLabel t2 = tokenizer.next(); 
  CoreLabel t3 = tokenizer.next(); 

  assertEquals("token1", t1.word());
  assertEquals(PTBTokenizer.getNewlineToken(), t2.word());
  assertEquals("token2", t3.word());
}
@Test
public void testCarriageReturnVariantsAsNewline() {
  String input = "first\rsecond\r\nthird\nfourth";
  Reader reader = new StringReader(input);
  PTBTokenizer<CoreLabel> tokenizer = PTBTokenizer.newPTBTokenizer(reader, true, false);

  CoreLabel t1 = tokenizer.next();
  CoreLabel t2 = tokenizer.next(); 
  CoreLabel t3 = tokenizer.next();
  CoreLabel t4 = tokenizer.next(); 
  CoreLabel t5 = tokenizer.next();
  CoreLabel t6 = tokenizer.next(); 
  CoreLabel t7 = tokenizer.next();

  assertEquals("first", t1.word());
  assertEquals(PTBTokenizer.getNewlineToken(), t2.word());
  assertEquals("second", t3.word());
  assertEquals(PTBTokenizer.getNewlineToken(), t4.word());
  assertEquals("third", t5.word());
  assertEquals(PTBTokenizer.getNewlineToken(), t6.word());
  assertEquals("fourth", t7.word());
}
@Test
public void testUnicodeEllipsisCharacterPreservedIfNotNormalized() {
  String input = "Wait\u2026now";
  Reader reader = new StringReader(input);
  PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "ellipses=original");

  CoreLabel t1 = tokenizer.next(); 
  CoreLabel t2 = tokenizer.next(); 
  CoreLabel t3 = tokenizer.next(); 

  assertEquals("Wait", t1.word());
  assertEquals("\u2026", t2.word());
  assertEquals("now", t3.word());
}
@Test
public void testInvalidCP1252QuotesMappedUniquely() {
  String input = "quote“inner”quote";
  Reader reader = new StringReader(input);
  PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "quotes=not_cp1252");

  CoreLabel t1 = tokenizer.next(); 
  CoreLabel t2 = tokenizer.next(); 
  CoreLabel t3 = tokenizer.next(); 
  CoreLabel t4 = tokenizer.next(); 
  CoreLabel t5 = tokenizer.next(); 

  assertEquals("“", t2.word());
  assertEquals("”", t4.word());
}
@Test
public void testMultiByteUnicodeCharactersTokenized() {
  String input = "漢字テスト😀";
  Reader reader = new StringReader(input);
  PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "");

  CoreLabel t1 = tokenizer.next(); 
  CoreLabel t2 = tokenizer.next(); 

  assertEquals("漢字テスト", t1.word());
  assertEquals("😀", t2.word());
}
@Test
public void testPartialReadStreamDoesNotFail() {
  Reader reader = new Reader() {
    private final String data = "foo bar";
    private boolean first = true;

    public int read(char[] cbuf, int off, int len) {
      if (first) {
        first = false;
        cbuf[off] = data.charAt(0);
        return 1;
      } else {
        for (int i = 1; i < data.length(); i++) {
          cbuf[off + i - 1] = data.charAt(i);
        }
        return data.length() - 1;
      }
    }

    public void close() {}
  };

  PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "");

  CoreLabel t1 = tokenizer.next(); 
  CoreLabel t2 = tokenizer.next(); 

  assertEquals("foo", t1.word());
  assertEquals("bar", t2.word());
}
@Test
public void testWordTokenFactoryPreservesText() {
  String input = "one two";
  Reader reader = new StringReader(input);
  PTBTokenizer<Word> tokenizer = new PTBTokenizer<>(
      reader,
      new WordTokenFactory(),
      ""
  );

  Word w1 = tokenizer.next();
  Word w2 = tokenizer.next();

  assertEquals("one", w1.word());
  assertEquals("two", w2.word());
}
@Test
public void testUntokenizableControlCharacterKept() {
  String input = "A\u0001B";
  Reader reader = new StringReader(input);
  PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(
      reader,
      new CoreLabelTokenFactory(),
      "untokenizable=allKeep"
  );

  CoreLabel t1 = tokenizer.next(); 
  CoreLabel t2 = tokenizer.next(); 
  CoreLabel t3 = tokenizer.next(); 

  assertEquals("A", t1.word());
  assertEquals("\u0001", t2.word());
  assertEquals("B", t3.word());
}
@Test  
public void testOptionsStringEmpty() {
  String input = "a (b) c";
  Reader reader = new StringReader(input);
  PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "");

  CoreLabel t1 = tokenizer.next();
  CoreLabel t2 = tokenizer.next();
  CoreLabel t3 = tokenizer.next();
  CoreLabel t4 = tokenizer.next();
  CoreLabel t5 = tokenizer.next();

  assertEquals("a", t1.word());
  assertEquals("-LRB-", t2.word());
  assertEquals("b", t3.word());
  assertEquals("-RRB-", t4.word());
  assertEquals("c", t5.word());
}
@Test  
public void testNormalizeCurrencyAllMajorSymbols() {
  String input = "£200 €150 ¥300 ₩500";
  Reader reader = new StringReader(input);
  PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "normalizeCurrency=true");

  CoreLabel t1 = tokenizer.next(); 
  CoreLabel t2 = tokenizer.next();
  CoreLabel t3 = tokenizer.next(); 
  CoreLabel t4 = tokenizer.next();
  CoreLabel t5 = tokenizer.next(); 
  CoreLabel t6 = tokenizer.next();
  CoreLabel t7 = tokenizer.next(); 
  CoreLabel t8 = tokenizer.next();

  assertEquals("$", t1.word());
  assertEquals("200", t2.word());
  assertEquals("$", t3.word());
  assertEquals("150", t4.word());
  assertEquals("#", t5.word());
  assertEquals("300", t6.word());
  assertEquals("#", t7.word());
  assertEquals("500", t8.word());
}
@Test  
public void testOptionParsingIgnoresWhitespace() {
  String input = "hello(world)";
  String options = " tokenizeNLs , invertible , ptb3Escaping = true ";
  Reader reader = new StringReader(input);
  PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), options);

  CoreLabel t1 = tokenizer.next(); 
  CoreLabel t2 = tokenizer.next(); 
  CoreLabel t3 = tokenizer.next(); 
  CoreLabel t4 = tokenizer.next(); 

  assertEquals("hello", t1.word());
  assertEquals("-LRB-", t2.word());
  assertEquals("world", t3.word());
  assertEquals("-RRB-", t4.word());
}
@Test  
public void testSplitHyphenatedFalseTreatsAsSingleToken() {
  String input = "long-term";
  Reader reader = new StringReader(input);
  PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "splitHyphenated=false");

  CoreLabel t1 = tokenizer.next();
  assertEquals("long-term", t1.word());
  assertFalse(tokenizer.hasNext());
}
@Test  
public void testConsecutiveUntokenizableCharactersKeepAll() {
  String input = "A\u0000\u0001B";
  Reader reader = new StringReader(input);
  PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "untokenizable=allKeep");

  CoreLabel t1 = tokenizer.next(); 
  CoreLabel t2 = tokenizer.next(); 
  CoreLabel t3 = tokenizer.next(); 
  CoreLabel t4 = tokenizer.next(); 

  assertEquals("A", t1.word());
  assertEquals("\u0000", t2.word());
  assertEquals("\u0001", t3.word());
  assertEquals("B", t4.word());
}
@Test  
public void testQuotesAsciiPreservesOnlyStandardQuotes() {
  String input = "‘quote’ and “double”";
  Reader reader = new StringReader(input);
  PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "quotes=ascii");

  CoreLabel t1 = tokenizer.next(); 
  CoreLabel t2 = tokenizer.next(); 
  CoreLabel t3 = tokenizer.next(); 
  CoreLabel t4 = tokenizer.next(); 
  CoreLabel t5 = tokenizer.next(); 
  CoreLabel t6 = tokenizer.next(); 
  CoreLabel t7 = tokenizer.next(); 

  assertEquals("'", t1.word());
  assertEquals("'", t3.word());
  assertEquals("\"", t5.word());
  assertEquals("\"", t7.word());
}
//@Test
//public void testFactoryWithNoOptionsStillWorks() {
//  String input = "Simple test.";
//  PTBTokenizer.PTBTokenizerFactory<Word> factory = new PTBTokenizer.PTBTokenizerFactory<Word>(new WordTokenFactory(), "");
//  Tokenizer<Word> tokenizer = factory.getTokenizer(new StringReader(input));
//
//  Word t1 = tokenizer.next();
//  Word t2 = tokenizer.next();
//  Word t3 = tokenizer.next();
//
//  assertEquals("Simple", t1.word());
//  assertEquals("test", t2.word());
//  assertEquals(".", t3.word());
//}
@Test  
public void testWindowsNewlinePreservedWithTokenizeNlsTrue() {
  String input = "R1\r\nR2";
  Reader reader = new StringReader(input);
  PTBTokenizer<CoreLabel> tokenizer = PTBTokenizer.newPTBTokenizer(reader, true, false);

  CoreLabel t1 = tokenizer.next();  
  CoreLabel t2 = tokenizer.next();  
  CoreLabel t3 = tokenizer.next();  

  assertEquals("R1", t1.word());
  assertEquals(PTBTokenizer.getNewlineToken(), t2.word());
  assertEquals("R2", t3.word());
}
@Test  
public void testCRAndLFSeparatedLinesTokenizeWithNewlines() {
  String input = "a\r\nb\nc\rd";
  Reader reader = new StringReader(input);
  PTBTokenizer<CoreLabel> tokenizer = PTBTokenizer.newPTBTokenizer(reader, true, false);

  CoreLabel t1 = tokenizer.next(); 
  CoreLabel t2 = tokenizer.next(); 
  CoreLabel t3 = tokenizer.next(); 
  CoreLabel t4 = tokenizer.next(); 
  CoreLabel t5 = tokenizer.next(); 
  CoreLabel t6 = tokenizer.next(); 
  CoreLabel t7 = tokenizer.next(); 

  assertEquals("a", t1.word());
  assertEquals(PTBTokenizer.getNewlineToken(), t2.word());
  assertEquals("b", t3.word());
  assertEquals(PTBTokenizer.getNewlineToken(), t4.word());
  assertEquals("c", t5.word());
  assertEquals(PTBTokenizer.getNewlineToken(), t6.word());
  assertEquals("d", t7.word());
} 
}