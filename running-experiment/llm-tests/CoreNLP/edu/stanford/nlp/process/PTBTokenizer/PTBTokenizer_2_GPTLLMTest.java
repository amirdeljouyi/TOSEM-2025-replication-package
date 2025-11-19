package edu.stanford.nlp.process;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

import edu.stanford.nlp.io.RuntimeIOException;
import edu.stanford.nlp.util.StringUtils;
import org.junit.Test;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.Label;
import edu.stanford.nlp.ling.SentenceUtils;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.international.negra.NegraPennLanguagePack;


public class PTBTokenizer_2_GPTLLMTest {

 @Test
  public void testNewPTBTokenizer_basicWord() {
    String input = "It's a test.";
    Reader reader = new StringReader(input);
    PTBTokenizer<Word> tokenizer = PTBTokenizer.newPTBTokenizer(reader);
    
    Word token1 = tokenizer.next();
    assertEquals("It", token1.word());

    Word token2 = tokenizer.next();
    assertEquals("'s", token2.word());

    Word token3 = tokenizer.next();
    assertEquals("a", token3.word());

    Word token4 = tokenizer.next();
    assertEquals("test", token4.word());

    Word token5 = tokenizer.next();
    assertEquals(".", token5.word());

    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testNewPTBTokenizer_coreLabel_withNewlines() {
    String input = "Hello\nWorld.";
    Reader reader = new StringReader(input);
    PTBTokenizer<CoreLabel> tokenizer = PTBTokenizer.newPTBTokenizer(reader, true, true);
    
    CoreLabel token1 = tokenizer.next();
    CoreLabel token2 = tokenizer.next();

    assertEquals("Hello", token1.word());
    assertEquals(PTBTokenizer.getNewlineToken(), token2.word());

    CoreLabel token3 = tokenizer.next();
    assertEquals("World", token3.word());

    CoreLabel token4 = tokenizer.next();
    assertEquals(".", token4.word());

    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testPTB2Text_bracketsQuotes() {
    String ptb = "-LRB- Hello -RRB- ``Hi''";
    String decoded = PTBTokenizer.ptb2Text(ptb);
    
    assertTrue(decoded.contains("( Hello )"));
    assertTrue(decoded.contains("\"Hi\""));
  }
@Test
  public void testPTB2Text_emptyStringReturnsEmpty() {
    String decoded = PTBTokenizer.ptb2Text("");
    assertEquals("", decoded);
  }
@Test
  public void testPTBToken2Text_LRB_returnsLeftParen() {
    String token = PTBTokenizer.ptbToken2Text("-LRB-");
    assertEquals("(", token);
  }
@Test
  public void testPTB2Text_readerWriter_tokenCount() throws Exception {
    String input = "-LRB- test -RRB-";
    Reader reader = new StringReader(input);
    StringWriter writer = new StringWriter();
    long count = PTBTokenizer.ptb2Text(reader, writer);

    assertEquals(3, count);
    assertEquals("( test )", writer.toString());
  }
@Test
  public void testPTB2Text_listInputReturnsJoinedDecoded() {
    List<String> inputWords = new ArrayList<String>();
    inputWords.add("-LRB-");
    inputWords.add("Text");
    inputWords.add("-RRB-");

    String output = PTBTokenizer.ptb2Text(inputWords);
    assertEquals("(Text)", output);
  }
@Test
  public void testLabelList2Text_returnsJoinedText() {
    List<Word> list = new ArrayList<Word>();
    list.add(new Word("-LRB-"));
    list.add(new Word("hello"));
    list.add(new Word("-RRB-"));

    String result = PTBTokenizer.labelList2Text(list);
    assertEquals("(hello)", result);
  }
@Test
  public void testCoreLabelFactory_defaultOptions_returnsExpected() {
    String input = "This is (a test).";
    PTBTokenizer.PTBTokenizerFactory<CoreLabel> factory = (PTBTokenizer.PTBTokenizerFactory<CoreLabel>) PTBTokenizer.coreLabelFactory();
    Reader reader = new StringReader(input);
    PTBTokenizer<CoreLabel> tokenizer = (PTBTokenizer<CoreLabel>) factory.getTokenizer(reader);

    CoreLabel t1 = tokenizer.next();
    CoreLabel t2 = tokenizer.next();
    CoreLabel t3 = tokenizer.next();
    CoreLabel t4 = tokenizer.next();
    CoreLabel t5 = tokenizer.next();
    CoreLabel t6 = tokenizer.next();
    CoreLabel t7 = tokenizer.next();

    assertEquals("This", t1.word());
    assertEquals("is", t2.word());
    assertEquals("-LRB-", t3.word());
    assertEquals("a", t4.word());
    assertEquals("test", t5.word());
    assertEquals("-RRB-", t6.word());
    assertEquals(".", t7.word());
  }
@Test
  public void testFactory_wordFactory_returnsExpectedTokens() {
    String input = "simple-example token";
    PTBTokenizer.PTBTokenizerFactory<Word> factory = (PTBTokenizer.PTBTokenizerFactory<Word>) PTBTokenizer.factory();
    Reader reader = new StringReader(input);
    PTBTokenizer<Word> tokenizer = (PTBTokenizer<Word>) factory.getTokenizer(reader);

    Word t1 = tokenizer.next();
    Word t2 = tokenizer.next();

    assertEquals("simple-example", t1.word());
    assertEquals("token", t2.word());
  }
@Test
  public void testCoreLabelFactory_normalizeParenthesesTrue() {
    String input = "(test)";
    PTBTokenizer.PTBTokenizerFactory<CoreLabel> factory = (PTBTokenizer.PTBTokenizerFactory<CoreLabel>) PTBTokenizer.coreLabelFactory("normalizeParentheses=true");
    Reader reader = new StringReader(input);
    PTBTokenizer<CoreLabel> tokenizer = (PTBTokenizer<CoreLabel>) factory.getTokenizer(reader);

    CoreLabel t1 = tokenizer.next();
    CoreLabel t2 = tokenizer.next();
    CoreLabel t3 = tokenizer.next();

    assertEquals("-LRB-", t1.word());
    assertEquals("test", t2.word());
    assertEquals("-RRB-", t3.word());
  }
@Test
  public void testQuoteNormalization_defaultProducesQuoteTokens() {
    String input = "``hi'' \"there\"";
    PTBTokenizer.PTBTokenizerFactory<CoreLabel> factory = (PTBTokenizer.PTBTokenizerFactory<CoreLabel>) PTBTokenizer.coreLabelFactory();
    Reader reader = new StringReader(input);
    PTBTokenizer<CoreLabel> tokenizer = (PTBTokenizer<CoreLabel>) factory.getTokenizer(reader);

    CoreLabel t1 = tokenizer.next();
    CoreLabel t2 = tokenizer.next();
    CoreLabel t3 = tokenizer.next();
    CoreLabel t4 = tokenizer.next();

    assertEquals("``", t1.word());
    assertEquals("hi", t2.word());
    assertEquals("''", t3.word());
    assertEquals("\"", t4.word());
  }
@Test
  public void testSplitHyphenated_trueOptionSplitsTokens() {
    String input = "well-known-token";
    PTBTokenizer.PTBTokenizerFactory<CoreLabel> factory = (PTBTokenizer.PTBTokenizerFactory<CoreLabel>) PTBTokenizer.coreLabelFactory("splitHyphenated");
    Reader reader = new StringReader(input);
    PTBTokenizer<CoreLabel> tokenizer = (PTBTokenizer<CoreLabel>) factory.getTokenizer(reader);

    CoreLabel token1 = tokenizer.next();
    CoreLabel token2 = tokenizer.next();
    CoreLabel token3 = tokenizer.next();

    assertEquals("well", token1.word());
    assertEquals("-", token2.word());
    assertEquals("known-token", token3.word());
  }
@Test
  public void testUntokenizable_allKeepOptionRetainsInvalidChar() {
    String input = "foo\u0000bar";
    PTBTokenizer.PTBTokenizerFactory<CoreLabel> factory = (PTBTokenizer.PTBTokenizerFactory<CoreLabel>) PTBTokenizer.coreLabelFactory("untokenizable=allKeep");
    Reader reader = new StringReader(input);
    PTBTokenizer<CoreLabel> tokenizer = (PTBTokenizer<CoreLabel>) factory.getTokenizer(reader);

    CoreLabel token1 = tokenizer.next();
    CoreLabel token2 = tokenizer.next();

    assertEquals("foo", token1.word());
    assertEquals("\u0000", token2.word());
  }
@Test
  public void testEmptyReaderYieldsNoTokens() {
    Reader reader = new StringReader("");
    PTBTokenizer<Word> tokenizer = PTBTokenizer.newPTBTokenizer(reader);
    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testTokenizerWithOnlyWhitespace() {
    Reader reader = new StringReader("   \t  \n  ");
    PTBTokenizer<Word> tokenizer = PTBTokenizer.newPTBTokenizer(reader);
    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testTokenizerHandlesTabsNewlinesCarriageReturn() {
    Reader reader = new StringReader("Hello\tWorld\r\n!");
    PTBTokenizer<CoreLabel> tokenizer = PTBTokenizer.newPTBTokenizer(reader, true, true);
    CoreLabel token1 = tokenizer.next();
    assertEquals("Hello", token1.word());
    CoreLabel token2 = tokenizer.next();
    assertEquals("World", token2.word());
    CoreLabel token3 = tokenizer.next();
    assertEquals(PTBTokenizer.getNewlineToken(), token3.word());
    CoreLabel token4 = tokenizer.next();
    assertEquals("!", token4.word());
    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testSingleCharacterSymbol() {
    Reader reader = new StringReader("@");
    PTBTokenizer<Word> tokenizer = PTBTokenizer.newPTBTokenizer(reader);
    Word token = tokenizer.next();
    assertEquals("@", token.word());
    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testTokenizerHandlesNumbersFractions() {
    Reader reader = new StringReader("Today is 5 1/2 degrees.");
    PTBTokenizer<CoreLabel> tokenizer = PTBTokenizer.newPTBTokenizer(reader, false, false);
    CoreLabel t1 = tokenizer.next();
    assertEquals("Today", t1.word());
    CoreLabel t2 = tokenizer.next();
    assertEquals("is", t2.word());
    CoreLabel t3 = tokenizer.next();
    assertEquals("5", t3.word());
    CoreLabel t4 = tokenizer.next();
    assertEquals("1/2", t4.word());
    CoreLabel t5 = tokenizer.next();
    assertEquals("degrees", t5.word());
    CoreLabel t6 = tokenizer.next();
    assertEquals(".", t6.word());
    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testEmojiAndNonBMPCharactersHandled() {
    Reader reader = new StringReader("Emoji 😊 test");
    PTBTokenizer<CoreLabel> tokenizer = PTBTokenizer.newPTBTokenizer(reader, false, false);
    CoreLabel token1 = tokenizer.next();
    assertEquals("Emoji", token1.word());
    CoreLabel token2 = tokenizer.next();
    assertEquals("😊", token2.word());
    CoreLabel token3 = tokenizer.next();
    assertEquals("test", token3.word());
    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testTokenizingStringWithMultipleSpacesBetweenWords() {
    Reader reader = new StringReader("word1     word2");
    PTBTokenizer<CoreLabel> tokenizer = PTBTokenizer.newPTBTokenizer(reader, false, false);
    CoreLabel token1 = tokenizer.next();
    CoreLabel token2 = tokenizer.next();
    assertEquals("word1", token1.word());
    assertEquals("word2", token2.word());
    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testQuoteSequenceWithoutInnerText() {
    Reader reader = new StringReader("`` ''");
    PTBTokenizer<CoreLabel> tokenizer = PTBTokenizer.newPTBTokenizer(reader, false, false);
    CoreLabel token1 = tokenizer.next();
    assertEquals("``", token1.word());
    CoreLabel token2 = tokenizer.next();
    assertEquals("''", token2.word());
    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testInvalidOptionIgnoredAndTokenizerStillWorks() {
    Reader reader = new StringReader("Some text here.");
    PTBTokenizer.PTBTokenizerFactory<CoreLabel> factory =
            (PTBTokenizer.PTBTokenizerFactory<CoreLabel>) PTBTokenizer.coreLabelFactory("nonexistentOption=true,invertible=true");
    PTBTokenizer<CoreLabel> tokenizer = (PTBTokenizer<CoreLabel>) factory.getTokenizer(reader);

    CoreLabel token1 = tokenizer.next();
    assertEquals("Some", token1.word());

    CoreLabel token2 = tokenizer.next();
    assertEquals("text", token2.word());

    CoreLabel token3 = tokenizer.next();
    assertEquals("here", token3.word());

    CoreLabel token4 = tokenizer.next();
    assertEquals(".", token4.word());

    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testTokenizerHandlesContractionAtStartOfInput() {
    Reader reader = new StringReader("'tis the season");
    PTBTokenizer<CoreLabel> tokenizer = PTBTokenizer.newPTBTokenizer(reader, false, false);
    CoreLabel t1 = tokenizer.next();
    assertEquals("'tis", t1.word());
    CoreLabel t2 = tokenizer.next();
    assertEquals("the", t2.word());
    CoreLabel t3 = tokenizer.next();
    assertEquals("season", t3.word());
    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testTokenizerHandlesSeparatorOnlyInput() {
    Reader reader = new StringReader("|");
    PTBTokenizer<CoreLabel> tokenizer = PTBTokenizer.newPTBTokenizer(reader, false, false);
    CoreLabel t1 = tokenizer.next();
    assertEquals("|", t1.word());
    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testMultipleNewlineTokensWithTokenizeNLsTrue() {
    Reader reader = new StringReader("one\ntwo\nthree");
    PTBTokenizer<CoreLabel> tokenizer = PTBTokenizer.newPTBTokenizer(reader, true, false);
    CoreLabel t1 = tokenizer.next();
    assertEquals("one", t1.word());
    CoreLabel t2 = tokenizer.next();
    assertEquals(PTBTokenizer.getNewlineToken(), t2.word());
    CoreLabel t3 = tokenizer.next();
    assertEquals("two", t3.word());
    CoreLabel t4 = tokenizer.next();
    assertEquals(PTBTokenizer.getNewlineToken(), t4.word());
    CoreLabel t5 = tokenizer.next();
    assertEquals("three", t5.word());
    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testReuseOfTokenizerThrowsWhenReaderIsEmptyNextTime() {
    Reader reader = new StringReader("once");
    PTBTokenizer<Word> tokenizer = PTBTokenizer.newPTBTokenizer(reader);
    Word token = tokenizer.next();
    assertEquals("once", token.word());
    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testBracketSequenceOnly() {
    Reader reader = new StringReader("()[]{}");
    PTBTokenizer<CoreLabel> tokenizer = PTBTokenizer.newPTBTokenizer(reader, false, false);
    CoreLabel t1 = tokenizer.next();
    CoreLabel t2 = tokenizer.next();
    CoreLabel t3 = tokenizer.next();
    CoreLabel t4 = tokenizer.next();
    CoreLabel t5 = tokenizer.next();
    CoreLabel t6 = tokenizer.next();

    assertEquals("-LRB-", t1.word());
    assertEquals("-RRB-", t2.word());
    assertEquals("-LSB-", t3.word());
    assertEquals("-RSB-", t4.word());
    assertEquals("-LCB-", t5.word());
    assertEquals("-RCB-", t6.word());
    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testInputWithUnicodeLineSeparator() {
    String input = "line1\u2028line2";
    Reader reader = new StringReader(input);
    PTBTokenizer<CoreLabel> tokenizer = PTBTokenizer.newPTBTokenizer(reader, true, false);
    CoreLabel t1 = tokenizer.next();
    assertEquals("line1", t1.word());
    CoreLabel t2 = tokenizer.next();
    assertEquals(PTBTokenizer.getNewlineToken(), t2.word());
    CoreLabel t3 = tokenizer.next();
    assertEquals("line2", t3.word());
    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testInputWithControlCharacters() {
    String input = "control:\u0001\u0002";
    Reader reader = new StringReader(input);
    PTBTokenizer<CoreLabel> tokenizer = (PTBTokenizer<CoreLabel>) PTBTokenizer.coreLabelFactory("untokenizable=allKeep").getTokenizer(reader);
    CoreLabel t1 = tokenizer.next();
    assertEquals("control", t1.word());
    CoreLabel t2 = tokenizer.next();
    assertEquals(":", t2.word());
    CoreLabel t3 = tokenizer.next();
    assertEquals("\u0001", t3.word());
    CoreLabel t4 = tokenizer.next();
    assertEquals("\u0002", t4.word());
    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testSplitForwardSlash() {
    String input = "either/or";
    Reader reader = new StringReader(input);
    PTBTokenizer<CoreLabel> tokenizer = (PTBTokenizer<CoreLabel>) PTBTokenizer.coreLabelFactory("splitForwardSlash").getTokenizer(reader);
    CoreLabel t1 = tokenizer.next();
    assertEquals("either", t1.word());
    CoreLabel t2 = tokenizer.next();
    assertEquals("/", t2.word());
    CoreLabel t3 = tokenizer.next();
    assertEquals("or", t3.word());
    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testWithStrictTreebank3Option() {
    String input = "U.K.";
    Reader reader = new StringReader(input);
    PTBTokenizer<CoreLabel> tokenizer = (PTBTokenizer<CoreLabel>) PTBTokenizer.coreLabelFactory("strictTreebank3").getTokenizer(reader);
    CoreLabel t1 = tokenizer.next();
    assertEquals("U.K.", t1.word());
    CoreLabel t2 = tokenizer.next();
    assertEquals(".", t2.word());
    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testQuotesOptionAscii() {
    String input = "“hello”";
    Reader reader = new StringReader(input);
    PTBTokenizer<CoreLabel> tokenizer = (PTBTokenizer<CoreLabel>) PTBTokenizer.coreLabelFactory("quotes=ascii").getTokenizer(reader);
    CoreLabel t1 = tokenizer.next();
    assertEquals("\"", t1.word());
    CoreLabel t2 = tokenizer.next();
    assertEquals("hello", t2.word());
    CoreLabel t3 = tokenizer.next();
    assertEquals("\"", t3.word());
    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testEllipsesUnicodeNormalization() {
    String input = "Wait ... really?";
    Reader reader = new StringReader(input);
    PTBTokenizer<CoreLabel> tokenizer = (PTBTokenizer<CoreLabel>) PTBTokenizer.coreLabelFactory("ellipses=unicode").getTokenizer(reader);
    CoreLabel t1 = tokenizer.next();
    assertEquals("Wait", t1.word());
    CoreLabel t2 = tokenizer.next();
    assertEquals("\u2026", t2.word());
    CoreLabel t3 = tokenizer.next();
    assertEquals("really", t3.word());
    CoreLabel t4 = tokenizer.next();
    assertEquals("?", t4.word());
    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testDashesUnicodeConversion() {
    String input = "a -- b";
    Reader reader = new StringReader(input);
    PTBTokenizer<CoreLabel> tokenizer = (PTBTokenizer<CoreLabel>) PTBTokenizer.coreLabelFactory("dashes=unicode").getTokenizer(reader);
    CoreLabel t1 = tokenizer.next();
    assertEquals("a", t1.word());
    CoreLabel t2 = tokenizer.next();
    assertEquals("\u2014", t2.word()); 
    CoreLabel t3 = tokenizer.next();
    assertEquals("b", t3.word());
    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testAmericanizeOptionBritishSpelling() {
    String input = "colour";
    Reader reader = new StringReader(input);
    PTBTokenizer<CoreLabel> tokenizer = (PTBTokenizer<CoreLabel>) PTBTokenizer.coreLabelFactory("americanize=true").getTokenizer(reader);
    CoreLabel t1 = tokenizer.next();
    assertEquals("color", t1.word());
    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testEscapeForwardSlashAsteriskEnabled() {
    String input = "/*";
    Reader reader = new StringReader(input);
    PTBTokenizer<CoreLabel> tokenizer = (PTBTokenizer<CoreLabel>) PTBTokenizer.coreLabelFactory("escapeForwardSlashAsterisk=true").getTokenizer(reader);
    CoreLabel t1 = tokenizer.next();
    assertEquals("\\/", t1.word());
    CoreLabel t2 = tokenizer.next();
    assertEquals("\\*", t2.word());
    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testFilePatternFilterIgnoresToken() {
    String input = "keep omit";
    Reader reader = new StringReader(input);
    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out));
    Pattern filterPattern = Pattern.compile("omit");

    long tokenCount = 0;
    try {
      tokenCount = PTBTokenizer.ptb2Text(reader, writer);
    } catch (IOException e) {
      fail("Unexpected IOException");
    }

    assertTrue(tokenCount >= 1);
  }
@Test
  public void testOptionParsingIncludesTokenizeNLs() {
    Properties props = new Properties();
    props.setProperty("preserveLines", "true");
    StringBuilder optionBuilder = new StringBuilder();
    if (props.containsKey("preserveLines")) {
      optionBuilder.append(",tokenizeNLs");
    }
    assertTrue(optionBuilder.toString().contains("tokenizeNLs"));
  }
@Test
  public void testEmptyQuotesUnicodeSetting() {
    String input = "''";
    Reader reader = new StringReader(input);
    PTBTokenizer<CoreLabel> tokenizer = (PTBTokenizer<CoreLabel>) PTBTokenizer.coreLabelFactory("quotes=unicode").getTokenizer(reader);
    CoreLabel token1 = tokenizer.next();
    assertEquals("\u201D", token1.word()); 
    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testCurrencyNormalizationDisabled() {
    String input = "€";
    Reader reader = new StringReader(input);
    PTBTokenizer<CoreLabel> tokenizer = (PTBTokenizer<CoreLabel>) PTBTokenizer.coreLabelFactory("normalizeCurrency=false").getTokenizer(reader);
    CoreLabel token1 = tokenizer.next();
    assertEquals("€", token1.word());
    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testStrictFractionBreaksMixedFraction() {
    String input = "5 1/2";
    Reader reader = new StringReader(input);
    PTBTokenizer<CoreLabel> tokenizer = (PTBTokenizer<CoreLabel>) PTBTokenizer.coreLabelFactory("strictFraction").getTokenizer(reader);
    CoreLabel token1 = tokenizer.next();
    assertEquals("5", token1.word());
    CoreLabel token2 = tokenizer.next();
    assertEquals("1/2", token2.word());
    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testEmptyConstructorOptionString() {
    Reader reader = new StringReader("(a)");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "");
    CoreLabel c1 = tokenizer.next();
    CoreLabel c2 = tokenizer.next();
    CoreLabel c3 = tokenizer.next();
    assertEquals("-LRB-", c1.word());
    assertEquals("a", c2.word());
    assertEquals("-RRB-", c3.word());
    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testMainFilterPatternEnabled() throws Exception {
    String input = "filter this token";
    Reader reader = new StringReader(input);
    Pattern filterPattern = Pattern.compile("this");
    PTBTokenizer<CoreLabel> tokenizer = (PTBTokenizer<CoreLabel>) PTBTokenizer.coreLabelFactory("invertible").getTokenizer(reader);
    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new ByteArrayOutputStream()));
    CoreLabel t1 = tokenizer.next();
    assertEquals("filter", t1.word());
    CoreLabel t2 = tokenizer.next();
    assertEquals("this", t2.word());
    assertTrue(filterPattern.matcher("this").matches());
    writer.close();
  }
@Test
  public void testNewlineTokenIsCorrectConstant() {
    String newlineToken = PTBTokenizer.getNewlineToken();
    assertEquals("NEWLINE", newlineToken);
  }
@Test
  public void testRetainSlashWhenSplitSlashOptionDisabled() {
    String input = "man/bear";
    Reader reader = new StringReader(input);
    PTBTokenizer<CoreLabel> tokenizer = (PTBTokenizer<CoreLabel>) PTBTokenizer.coreLabelFactory("splitForwardSlash=false").getTokenizer(reader);
    CoreLabel token = tokenizer.next();
    assertEquals("man/bear", token.word());
    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testTokenizerHandlesTrailingWhitespaceGracefully() {
    String input = "trail ";
    Reader reader = new StringReader(input);
    PTBTokenizer<Word> tokenizer = PTBTokenizer.newPTBTokenizer(reader);
    Word token1 = tokenizer.next();
    assertEquals("trail", token1.word());
    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testTokenizerHandlesBlankLineCorrectly() {
    String input = "token1\n\ntoken2";
    Reader reader = new StringReader(input);
    PTBTokenizer<CoreLabel> tokenizer = PTBTokenizer.newPTBTokenizer(reader, true, false);
    CoreLabel t1 = tokenizer.next();
    assertEquals("token1", t1.word());
    CoreLabel newline1 = tokenizer.next();
    assertEquals(PTBTokenizer.getNewlineToken(), newline1.word());
    CoreLabel newline2 = tokenizer.next();
    assertEquals(PTBTokenizer.getNewlineToken(), newline2.word());
    CoreLabel t2 = tokenizer.next();
    assertEquals("token2", t2.word());
    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testTokenizerReadsAllCharactersWhenPreserveLinesDisabled() {
    String input = "a\nb";
    Reader reader = new StringReader(input);
    PTBTokenizer<CoreLabel> tokenizer = PTBTokenizer.newPTBTokenizer(reader, false, false);
    CoreLabel token1 = tokenizer.next();
    assertEquals("a", token1.word());
    CoreLabel token2 = tokenizer.next();
    assertEquals("b", token2.word());
    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testComplexOptionStringIsParsedCorrectly() {
    String input = "\"Example\"";
    Reader reader = new StringReader(input);
    PTBTokenizer<CoreLabel> tokenizer = (PTBTokenizer<CoreLabel>) PTBTokenizer.coreLabelFactory("invertible,normalizeAmpersandEntity=true,quotes=ascii").getTokenizer(reader);
    CoreLabel t1 = tokenizer.next();
    assertEquals("\"", t1.word());
    CoreLabel t2 = tokenizer.next();
    assertEquals("Example", t2.word());
    CoreLabel t3 = tokenizer.next();
    assertEquals("\"", t3.word());
    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testTokenizerWithSlashAndAsteriskWithoutEscape() {
    String input = "/* comment */";
    Reader reader = new StringReader(input);
    PTBTokenizer<CoreLabel> tokenizer = (PTBTokenizer<CoreLabel>) PTBTokenizer.coreLabelFactory("escapeForwardSlashAsterisk=false").getTokenizer(reader);
    CoreLabel t1 = tokenizer.next();
    assertEquals("/", t1.word());
    CoreLabel t2 = tokenizer.next();
    assertEquals("*", t2.word());
    CoreLabel t3 = tokenizer.next();
    assertEquals("comment", t3.word());
    CoreLabel t4 = tokenizer.next();
    assertEquals("*", t4.word());
    CoreLabel t5 = tokenizer.next();
    assertEquals("/", t5.word());
    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testSpecialBracketsAreMappedCorrectlyWhenNormalizeOtherBracketsTrue() {
    String input = "【awesome】";
    Reader reader = new StringReader(input);
    PTBTokenizer<CoreLabel> tokenizer = (PTBTokenizer<CoreLabel>) PTBTokenizer.coreLabelFactory("normalizeOtherBrackets=true").getTokenizer(reader);
    CoreLabel t1 = tokenizer.next();
    assertEquals("-LRB-", t1.word());
    CoreLabel t2 = tokenizer.next();
    assertEquals("awesome", t2.word());
    CoreLabel t3 = tokenizer.next();
    assertEquals("-RRB-", t3.word());
    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testOnlyPunctuationTokens() {
    String input = ", . ! \" ? ' : ; @ # $";
    Reader reader = new StringReader(input);
    PTBTokenizer<Word> tokenizer = PTBTokenizer.newPTBTokenizer(reader);
    Word t1 = tokenizer.next();
    assertEquals(",", t1.word());
    Word t2 = tokenizer.next();
    assertEquals(".", t2.word());
    Word t3 = tokenizer.next();
    assertEquals("!", t3.word());
    Word t4 = tokenizer.next();
    assertEquals("\"", t4.word());
    Word t5 = tokenizer.next();
    assertEquals("?", t5.word());
    Word t6 = tokenizer.next();
    assertEquals("'", t6.word());
    Word t7 = tokenizer.next();
    assertEquals(":", t7.word());
    Word t8 = tokenizer.next();
    assertEquals(";", t8.word());
    Word t9 = tokenizer.next();
    assertEquals("@", t9.word());
    Word t10 = tokenizer.next();
    assertEquals("#", t10.word());
    Word t11 = tokenizer.next();
    assertEquals("$", t11.word());
    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testMultipleConsecutiveNewlinesTokenizeNLsEnabled() {
    String input = "a\n\n\nb";
    Reader reader = new StringReader(input);
    PTBTokenizer<CoreLabel> tokenizer = PTBTokenizer.newPTBTokenizer(reader, true, false);
    CoreLabel t1 = tokenizer.next();
    assertEquals("a", t1.word());
    CoreLabel t2 = tokenizer.next();
    assertEquals(PTBTokenizer.getNewlineToken(), t2.word());
    CoreLabel t3 = tokenizer.next();
    assertEquals(PTBTokenizer.getNewlineToken(), t3.word());
    CoreLabel t4 = tokenizer.next();
    assertEquals(PTBTokenizer.getNewlineToken(), t4.word());
    CoreLabel t5 = tokenizer.next();
    assertEquals("b", t5.word());
    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testEmptyStringReturnsNoTokens() {
    String input = "";
    Reader reader = new StringReader(input);
    PTBTokenizer<CoreLabel> tokenizer = PTBTokenizer.newPTBTokenizer(reader, false, false);
    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testTokenizerWithNullOrEmptyOptions() {
    String input = "foo (bar)";
    Reader reader1 = new StringReader(input);
    PTBTokenizer<CoreLabel> tokenizer1 = new PTBTokenizer<>(reader1, new CoreLabelTokenFactory(), null);
    assertEquals("foo", tokenizer1.next().word());

    Reader reader2 = new StringReader(input);
    PTBTokenizer<CoreLabel> tokenizer2 = new PTBTokenizer<>(reader2, new CoreLabelTokenFactory(), "");
    assertEquals("foo", tokenizer2.next().word());
  }
@Test
  public void testOnlyQuotesToken() {
    String input = "\"\" ‘’ “";
    Reader reader = new StringReader(input);
    PTBTokenizer<CoreLabel> tokenizer = (PTBTokenizer<CoreLabel>) PTBTokenizer.coreLabelFactory("quotes=ascii").getTokenizer(reader);
    CoreLabel t1 = tokenizer.next();
    assertEquals("\"", t1.word());
    CoreLabel t2 = tokenizer.next();
    assertEquals("\"", t2.word());
    CoreLabel t3 = tokenizer.next();
    assertEquals("'", t3.word());
    CoreLabel t4 = tokenizer.next();
    assertEquals("'", t4.word());
    CoreLabel t5 = tokenizer.next();
    assertEquals("\"", t5.word());
    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testUnicodeEllipsisPreservedWhenEllipsesOptionOff() {
    String input = "\u2026";
    Reader reader = new StringReader(input);
    PTBTokenizer<CoreLabel> tokenizer = (PTBTokenizer<CoreLabel>) PTBTokenizer.coreLabelFactory("ellipses=original").getTokenizer(reader);
    CoreLabel token = tokenizer.next();
    assertEquals("\u2026", token.word());
    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testEmojiHandlingInText() {
    String input = "I ❤️ Stanford.";
    Reader reader = new StringReader(input);
    PTBTokenizer<CoreLabel> tokenizer = (PTBTokenizer<CoreLabel>) PTBTokenizer.coreLabelFactory("ptb3Escaping=false").getTokenizer(reader);
    CoreLabel t1 = tokenizer.next();
    assertEquals("I", t1.word());
    CoreLabel t2 = tokenizer.next();
    assertEquals("❤️", t2.word());
    CoreLabel t3 = tokenizer.next();
    assertEquals("Stanford", t3.word());
    CoreLabel t4 = tokenizer.next();
    assertEquals(".", t4.word());
    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testSingleHyphenTokenizedWithSplitOption() {
    String input = "one-two";
    Reader reader = new StringReader(input);
    PTBTokenizer<CoreLabel> tok = (PTBTokenizer<CoreLabel>) PTBTokenizer.coreLabelFactory("splitHyphenated").getTokenizer(reader);
    CoreLabel t1 = tok.next();
    assertEquals("one", t1.word());
    CoreLabel t2 = tok.next();
    assertEquals("-", t2.word());
    CoreLabel t3 = tok.next();
    assertEquals("two", t3.word());
    assertFalse(tok.hasNext());
  }
@Test
  public void testInvalidFlagIgnoredInFactoryOptions() {
    String input = "token";
    Reader reader = new StringReader(input);
    PTBTokenizer.PTBTokenizerFactory<CoreLabel> factory = (PTBTokenizer.PTBTokenizerFactory<CoreLabel>) PTBTokenizer.coreLabelFactory("unknownOption=true,invertible");
    PTBTokenizer<CoreLabel> tokenizer = (PTBTokenizer<CoreLabel>) factory.getTokenizer(reader);
    CoreLabel t1 = tokenizer.next();
    assertEquals("token", t1.word());
    assertTrue(t1.containsKey(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testIOExceptionHandlingThrowsRuntimeIOException() {
    Reader brokenReader = new Reader() {
      @Override
      public int read(char[] cbuf, int off, int len) throws IOException {
        throw new IOException("Simulated failure");
      }
      @Override
      public void close() throws IOException {}
    };

    PTBTokenizer<Word> tokenizer = PTBTokenizer.newPTBTokenizer(brokenReader);
    try {
      tokenizer.next();
      fail("Expected RuntimeIOException");
    } catch (RuntimeIOException ex) {
      assertTrue(ex.getMessage().contains("Simulated failure"));
    }
  }
@Test
  public void testTokenizerHandlesExtraneousWhitespace() {
    String input = "  The   quick\tbrown\nfox ";
    Reader reader = new StringReader(input);
    PTBTokenizer<CoreLabel> tokenizer = PTBTokenizer.newPTBTokenizer(reader, true, true);
    CoreLabel t1 = tokenizer.next();
    assertEquals("The", t1.word());
    CoreLabel t2 = tokenizer.next();
    assertEquals("quick", t2.word());
    CoreLabel t3 = tokenizer.next();
    assertEquals("brown", t3.word());
    CoreLabel t4 = tokenizer.next();
    assertEquals(PTBTokenizer.getNewlineToken(), t4.word());
    CoreLabel t5 = tokenizer.next();
    assertEquals("fox", t5.word());
    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testAmpersandEntityNormalization() {
    String input = "&amp;";
    Reader reader = new StringReader(input);
    PTBTokenizer<CoreLabel> tokenizer = (PTBTokenizer<CoreLabel>) PTBTokenizer.coreLabelFactory("normalizeAmpersandEntity").getTokenizer(reader);
    CoreLabel t1 = tokenizer.next();
    assertEquals("&", t1.word());
    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testUntokenizableNoneKeepIncludesUnknownChar() {
    String input = "a\u0001b";
    Reader reader = new StringReader(input);
    PTBTokenizer<CoreLabel> tokenizer = (PTBTokenizer<CoreLabel>) PTBTokenizer.coreLabelFactory("untokenizable=allKeep").getTokenizer(reader);
    CoreLabel t1 = tokenizer.next();
    assertEquals("a", t1.word());
    CoreLabel t2 = tokenizer.next();
    assertEquals("\u0001", t2.word());
    CoreLabel t3 = tokenizer.next();
    assertEquals("b", t3.word());
    assertFalse(tokenizer.hasNext());
  }
//@Test
//  public void testFactoryConstructorWithNullOptions() {
//    Reader reader = new StringReader("test");
//    PTBTokenizer.PTBTokenizerFactory<Word> factory = new PTBTokenizer.PTBTokenizerFactory<>(new WordTokenFactory(), null);
//    PTBTokenizer<Word> tokenizer = (PTBTokenizer<Word>) factory.getTokenizer(reader);
//    Word token = tokenizer.next();
//    assertEquals("test", token.word());
//  }
@Test
  public void testSlashSequenceSplitWhenOptionEnabled() {
    String input = "a/b/c";
    Reader reader = new StringReader(input);
    PTBTokenizer<CoreLabel> tokenizer = (PTBTokenizer<CoreLabel>) PTBTokenizer.coreLabelFactory("splitForwardSlash=true").getTokenizer(reader);
    CoreLabel t1 = tokenizer.next();
    assertEquals("a", t1.word());
    CoreLabel t2 = tokenizer.next();
    assertEquals("/", t2.word());
    CoreLabel t3 = tokenizer.next();
    assertEquals("b", t3.word());
    CoreLabel t4 = tokenizer.next();
    assertEquals("/", t4.word());
    CoreLabel t5 = tokenizer.next();
    assertEquals("c", t5.word());
    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testSingleCharacterWordToken() {
    Reader reader = new StringReader("a");
    PTBTokenizer<Word> tokenizer = PTBTokenizer.newPTBTokenizer(reader);
    Word token = tokenizer.next();
    assertEquals("a", token.word());
    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testSingleCharacterPunctuation() {
    Reader reader = new StringReader(".");
    PTBTokenizer<Word> tokenizer = PTBTokenizer.newPTBTokenizer(reader);
    Word token = tokenizer.next();
    assertEquals(".", token.word());
    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testEmptyParenthesesWithNormalization() {
    Reader reader = new StringReader("()");
    PTBTokenizer<CoreLabel> tokenizer = (PTBTokenizer<CoreLabel>) PTBTokenizer.coreLabelFactory("normalizeParentheses=true").getTokenizer(reader);
    CoreLabel token1 = tokenizer.next();
    CoreLabel token2 = tokenizer.next();
    assertEquals("-LRB-", token1.word());
    assertEquals("-RRB-", token2.word());
    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testConflictingOptionsInFactory() {
    Reader reader = new StringReader("text");
    PTBTokenizer.PTBTokenizerFactory<CoreLabel> factory = (PTBTokenizer.PTBTokenizerFactory<CoreLabel>) PTBTokenizer.coreLabelFactory("ptb3Escaping=false,normalizeParentheses=true");
    PTBTokenizer<CoreLabel> tokenizer = (PTBTokenizer<CoreLabel>) factory.getTokenizer(reader);
    CoreLabel token = tokenizer.next();
    assertEquals("text", token.word());
    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testUntokenizableKeepAllUnknownUTF8Character() {
    Reader reader = new StringReader("abc\u070Fdef");
    PTBTokenizer<CoreLabel> tokenizer = (PTBTokenizer<CoreLabel>) PTBTokenizer.coreLabelFactory("untokenizable=allKeep").getTokenizer(reader);
    CoreLabel t1 = tokenizer.next();
    assertEquals("abc", t1.word());
    CoreLabel t2 = tokenizer.next();
    assertEquals("\u070F", t2.word());
    CoreLabel t3 = tokenizer.next();
    assertEquals("def", t3.word());
    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testUnicodeFractionNormalizationEnabled() {
    Reader reader = new StringReader("½");
    PTBTokenizer<CoreLabel> tokenizer = (PTBTokenizer<CoreLabel>) PTBTokenizer.coreLabelFactory("normalizeFractions=true").getTokenizer(reader);
    CoreLabel token = tokenizer.next();
    assertEquals("1/2", token.word());
    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testUnicodeFractionNormalizationDisabled() {
    Reader reader = new StringReader("½");
    PTBTokenizer<CoreLabel> tokenizer = (PTBTokenizer<CoreLabel>) PTBTokenizer.coreLabelFactory("normalizeFractions=false").getTokenizer(reader);
    CoreLabel token = tokenizer.next();
    assertEquals("½", token.word());
    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testCustomQuoteWithOriginalQuotesOption() {
    Reader reader = new StringReader("“hello”");
    PTBTokenizer<CoreLabel> tokenizer = (PTBTokenizer<CoreLabel>) PTBTokenizer.coreLabelFactory("quotes=original").getTokenizer(reader);
    CoreLabel t1 = tokenizer.next();
    assertEquals("“", t1.word());
    CoreLabel t2 = tokenizer.next();
    assertEquals("hello", t2.word());
    CoreLabel t3 = tokenizer.next();
    assertEquals("”", t3.word());
    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testUntokenizableFirstKeepLogsSingleChar() {
    Reader reader = new StringReader("x\u070Fx");
    PTBTokenizer<CoreLabel> tokenizer = (PTBTokenizer<CoreLabel>) PTBTokenizer.coreLabelFactory("untokenizable=firstKeep").getTokenizer(reader);
    CoreLabel t1 = tokenizer.next();
    assertEquals("x", t1.word());
    CoreLabel t2 = tokenizer.next();
    assertEquals("\u070F", t2.word());
    CoreLabel t3 = tokenizer.next();
    assertEquals("x", t3.word());
    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testDashNormalizationToUnicodeEnabled() {
    Reader reader = new StringReader("--");
    PTBTokenizer<CoreLabel> tokenizer = (PTBTokenizer<CoreLabel>) PTBTokenizer.coreLabelFactory("dashes=unicode").getTokenizer(reader);
    CoreLabel token = tokenizer.next();
    assertEquals("\u2014", token.word()); 
    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testDashNormalizationToPTB3() {
    Reader reader = new StringReader("–");
    PTBTokenizer<CoreLabel> tokenizer = (PTBTokenizer<CoreLabel>) PTBTokenizer.coreLabelFactory("dashes=ptb3").getTokenizer(reader);
    CoreLabel token = tokenizer.next();
    assertEquals("--", token.word());
    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testNormalizeParenthesesFalseKeepsSymbol() {
    Reader reader = new StringReader("(test)");
    PTBTokenizer<CoreLabel> tokenizer = (PTBTokenizer<CoreLabel>) PTBTokenizer.coreLabelFactory("normalizeParentheses=false").getTokenizer(reader);
    CoreLabel t1 = tokenizer.next();
    assertEquals("(", t1.word());
    CoreLabel t2 = tokenizer.next();
    assertEquals("test", t2.word());
    CoreLabel t3 = tokenizer.next();
    assertEquals(")", t3.word());
    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testSplitAssimilationsKeepTogether() {
    Reader reader = new StringReader("gonna");
    PTBTokenizer<CoreLabel> tokenizer = (PTBTokenizer<CoreLabel>) PTBTokenizer.coreLabelFactory("splitAssimilations=false").getTokenizer(reader);
    CoreLabel token = tokenizer.next();
    assertEquals("gonna", token.word());
    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testSplitAssimilationsSplitMode() {
    Reader reader = new StringReader("gonna");
    PTBTokenizer<CoreLabel> tokenizer = (PTBTokenizer<CoreLabel>) PTBTokenizer.coreLabelFactory("splitAssimilations=true").getTokenizer(reader);
    CoreLabel t1 = tokenizer.next();
    assertEquals("gon", t1.word());
    CoreLabel t2 = tokenizer.next();
    assertEquals("na", t2.word());
    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testMiscBracketsNormalizationEnabled() {
    Reader reader = new StringReader("【text】");
    PTBTokenizer<CoreLabel> tokenizer = (PTBTokenizer<CoreLabel>) PTBTokenizer.coreLabelFactory("normalizeOtherBrackets=true").getTokenizer(reader);
    CoreLabel t1 = tokenizer.next();
    assertEquals("-LRB-", t1.word());
    CoreLabel t2 = tokenizer.next();
    assertEquals("text", t2.word());
    CoreLabel t3 = tokenizer.next();
    assertEquals("-RRB-", t3.word());
    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testMiscBracketsNormalizationDisabled() {
    Reader reader = new StringReader("【x】");
    PTBTokenizer<CoreLabel> tokenizer = (PTBTokenizer<CoreLabel>) PTBTokenizer.coreLabelFactory("normalizeOtherBrackets=false").getTokenizer(reader);
    CoreLabel t1 = tokenizer.next();
    assertEquals("【", t1.word());
    CoreLabel t2 = tokenizer.next();
    assertEquals("x", t2.word());
    CoreLabel t3 = tokenizer.next();
    assertEquals("】", t3.word());
    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testMultipleSequentialQuoteStyles() {
    Reader reader = new StringReader("``Hello'' “Hi” \"Quoted\"");
    PTBTokenizer<CoreLabel> tokenizer = (PTBTokenizer<CoreLabel>) PTBTokenizer.coreLabelFactory("quotes=ascii").getTokenizer(reader);
    CoreLabel t1 = tokenizer.next();
    assertEquals("\"", t1.word());
    CoreLabel t2 = tokenizer.next();
    assertEquals("Hello", t2.word());
    CoreLabel t3 = tokenizer.next();
    assertEquals("\"", t3.word());
    CoreLabel t4 = tokenizer.next();
    assertEquals("\"", t4.word());
    CoreLabel t5 = tokenizer.next();
    assertEquals("Hi", t5.word());
    CoreLabel t6 = tokenizer.next();
    assertEquals("\"", t6.word());
    CoreLabel t7 = tokenizer.next();
    assertEquals("\"", t7.word());
    CoreLabel t8 = tokenizer.next();
    assertEquals("Quoted", t8.word());
    CoreLabel t9 = tokenizer.next();
    assertEquals("\"", t9.word());
    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testSingleDigitAndSymbolSeparated() {
    Reader reader = new StringReader("3+2=5");
    PTBTokenizer<CoreLabel> tokenizer = (PTBTokenizer<CoreLabel>) PTBTokenizer.coreLabelFactory().getTokenizer(reader);
    CoreLabel t1 = tokenizer.next();
    assertEquals("3", t1.word());
    CoreLabel t2 = tokenizer.next();
    assertEquals("+", t2.word());
    CoreLabel t3 = tokenizer.next();
    assertEquals("2", t3.word());
    CoreLabel t4 = tokenizer.next();
    assertEquals("=", t4.word());
    CoreLabel t5 = tokenizer.next();
    assertEquals("5", t5.word());
    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testBackslashEscapedCharacters() {
    Reader reader = new StringReader("\\*escaped\\/");
    PTBTokenizer<CoreLabel> tokenizer = (PTBTokenizer<CoreLabel>) PTBTokenizer.coreLabelFactory("escapeForwardSlashAsterisk=false").getTokenizer(reader);
    CoreLabel t1 = tokenizer.next();
    assertEquals("\\", t1.word());
    CoreLabel t2 = tokenizer.next();
    assertEquals("*", t2.word());
    CoreLabel t3 = tokenizer.next();
    assertEquals("escaped", t3.word());
    CoreLabel t4 = tokenizer.next();
    assertEquals("\\", t4.word());
    CoreLabel t5 = tokenizer.next();
    assertEquals("/", t5.word());
    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testDefaultFactoryVsCoreLabelFactory() {
    Reader reader1 = new StringReader("abc");
    PTBTokenizer<Word> tok1 = (PTBTokenizer<Word>) PTBTokenizer.factory().getTokenizer(reader1);
    Word w = tok1.next();
    assertEquals("abc", w.word());

    Reader reader2 = new StringReader("xyz");
    PTBTokenizer<CoreLabel> tok2 = (PTBTokenizer<CoreLabel>) PTBTokenizer.coreLabelFactory().getTokenizer(reader2);
    CoreLabel cl = tok2.next();
    assertEquals("xyz", cl.word());
  }
@Test
  public void testNormalizeSpaceWithTokenContainingWhitespace() {
    Reader reader = new StringReader("1 2/3");
    PTBTokenizer<CoreLabel> tokenizer = (PTBTokenizer<CoreLabel>) PTBTokenizer.coreLabelFactory("normalizeSpace=true").getTokenizer(reader);
    CoreLabel t1 = tokenizer.next();
    assertEquals("1", t1.word());
    CoreLabel t2 = tokenizer.next();
    assertEquals("2/3", t2.word());
  }
@Test
  public void testUntokenizableAllDeleteRemovesUnknownChar() {
    String input = "x\u0378x"; 
    Reader reader = new StringReader(input);
    PTBTokenizer<CoreLabel> tokenizer = (PTBTokenizer<CoreLabel>) PTBTokenizer.coreLabelFactory("untokenizable=allDelete").getTokenizer(reader);
    CoreLabel t1 = tokenizer.next();
    assertEquals("x", t1.word());
    CoreLabel t2 = tokenizer.next();
    assertEquals("x", t2.word());
    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testStrictAcronymSeparatePeriod() {
    Reader reader = new StringReader("U.N.");
    PTBTokenizer<CoreLabel> tokenizer = (PTBTokenizer<CoreLabel>) PTBTokenizer.coreLabelFactory("strictAcronym=true").getTokenizer(reader);
    CoreLabel t1 = tokenizer.next();
    assertEquals("U.N.", t1.word());
    CoreLabel t2 = tokenizer.next();
    assertEquals(".", t2.word());
    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testNonAsciiQuoteCharacterNotRemapped() {
    Reader reader = new StringReader("\u00ABhello\u00BB"); 
    PTBTokenizer<CoreLabel> tokenizer = (PTBTokenizer<CoreLabel>) PTBTokenizer.coreLabelFactory("quotes=original").getTokenizer(reader);
    CoreLabel t1 = tokenizer.next();
    assertEquals("\u00AB", t1.word());
    CoreLabel t2 = tokenizer.next();
    assertEquals("hello", t2.word());
    CoreLabel t3 = tokenizer.next();
    assertEquals("\u00BB", t3.word());
    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testNormalizeCurrencyEnabledMapsSymbols() {
    Reader reader = new StringReader("€ £ ¢");
    PTBTokenizer<CoreLabel> tokenizer = (PTBTokenizer<CoreLabel>) PTBTokenizer.coreLabelFactory("normalizeCurrency=true").getTokenizer(reader);
    CoreLabel t1 = tokenizer.next();
    assertTrue(t1.word().equals("$") || t1.word().equals("cents"));
    CoreLabel t2 = tokenizer.next();
    assertTrue(t2.word().equals("$") || t2.word().equals("cents"));
    CoreLabel t3 = tokenizer.next();
    assertTrue(t3.word().equals("$") || t3.word().equals("cents"));
  }
@Test
  public void testLabelList2TextPreservesOriginalSpacing() {
    Word word1 = new Word("Hello");
    Word word2 = new Word(",");
    Word word3 = new Word("world");
    List<Word> words = new ArrayList<Word>();
    words.add(word1);
    words.add(word2);
    words.add(word3);
    String output = PTBTokenizer.labelList2Text(words);
    assertEquals("Hello,world", output); 
  }
@Test
  public void testDashHandlingWithHyphenMinusOnly() {
    Reader reader = new StringReader("-");
    PTBTokenizer<CoreLabel> tokenizer = (PTBTokenizer<CoreLabel>) PTBTokenizer.coreLabelFactory("dashes=not_cp1252").getTokenizer(reader);
    CoreLabel token = tokenizer.next();
    assertEquals("-", token.word()); 
    assertFalse(tokenizer.hasNext());
  }
@Test
  public void testParseInsidePatternWithNonMatchingToken() throws IOException {
    Pattern parseInside = Pattern.compile("</?text>");
    Pattern filterPattern = null;
    String input = "outside <text>inside</text>";
    Reader r = new StringReader(input);
    BufferedWriter w = new BufferedWriter(new OutputStreamWriter(new ByteArrayOutputStream()));
    long count = PTBTokenizer.ptb2Text(r, w);
    assertTrue(count > 0);
  }
@Test
  public void testOneLinePerElementWritesExpectedNewlines() throws IOException {
    String input = "<p>foo bar</p><p>baz bim</p>";
    Reader reader = new StringReader(input);
    Writer writer = new StringWriter();
    Pattern inside = Pattern.compile("<(/?)p>");
    Pattern filter = null;
    long tokens = PTBTokenizer.ptb2Text(reader, writer);
    assertTrue(tokens > 0);
  }
@Test
  public void testFactoryGetIteratorUsesGetTokenizer() {
    Reader reader = new StringReader("iterator test");
    PTBTokenizer.PTBTokenizerFactory<CoreLabel> factory = (PTBTokenizer.PTBTokenizerFactory<CoreLabel>) PTBTokenizer.coreLabelFactory("invertible");
    Iterator<CoreLabel> iterator = factory.getIterator(reader);
    assertTrue(iterator.hasNext());
    CoreLabel token = iterator.next();
    assertEquals("iterator", token.word());
  }
@Test
  public void testFactorySetOptionsModifiesBehavior() {
    PTBTokenizer.PTBTokenizerFactory<CoreLabel> factory = (PTBTokenizer.PTBTokenizerFactory<CoreLabel>) PTBTokenizer.coreLabelFactory("invertible=false");
    factory.setOptions("invertible=true,normalizeParentheses=true");
    Reader reader = new StringReader("(test)");
    PTBTokenizer<CoreLabel> tokenizer = (PTBTokenizer<CoreLabel>) factory.getTokenizer(reader);
    CoreLabel t1 = tokenizer.next();
    assertEquals("-LRB-", t1.word());
  }
@Test
  public void testMainMethodHelpSwitchPrintsUsage() throws Exception {
    String[] args = {"-help"};
    ByteArrayOutputStream errCapture = new ByteArrayOutputStream();
    PrintStream originalErr = System.err;
    System.setErr(new PrintStream(errCapture));
    PTBTokenizer.main(args);
    System.setErr(originalErr);
    String output = errCapture.toString("UTF-8");
    assertTrue(output.contains("Usage"));
  } 
}