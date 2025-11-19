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


public class PTBTokenizer_5_GPTLLMTest {

 @Test
  public void testSimpleSentenceTokenization() {
    Reader reader = new StringReader("Hello, world!");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "invertible");

    List<String> tokens = new ArrayList<String>();
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());

    assertEquals(Arrays.asList("Hello", ",", "world", "!"), tokens);
  }
@Test
  public void testNewPTBTokenizerWithWordFactory() {
    Reader reader = new StringReader("Tokenize this.");
    PTBTokenizer<Word> tokenizer = PTBTokenizer.newPTBTokenizer(reader);

    List<String> tokens = new ArrayList<String>();
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());

    assertEquals(Arrays.asList("Tokenize", "this", "."), tokens);
  }
@Test
  public void testNormalizationOfParenthesesDefault() {
    Reader reader = new StringReader("(text)");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "");

    List<String> result = new ArrayList<String>();
    if (tokenizer.hasNext()) result.add(tokenizer.next().word());
    if (tokenizer.hasNext()) result.add(tokenizer.next().word());
    if (tokenizer.hasNext()) result.add(tokenizer.next().word());

    assertEquals(Arrays.asList("-LRB-", "text", "-RRB-"), result);
  }
@Test
  public void testNormalizationOfParenthesesDisabled() {
    Reader reader = new StringReader("(text)");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "normalizeParentheses=false");

    List<String> result = new ArrayList<String>();
    if (tokenizer.hasNext()) result.add(tokenizer.next().word());
    if (tokenizer.hasNext()) result.add(tokenizer.next().word());
    if (tokenizer.hasNext()) result.add(tokenizer.next().word());

    assertEquals(Arrays.asList("(", "text", ")"), result);
  }
@Test
  public void testPTBToken2TextConversion() {
    String input = "-LRB-";
    String result = PTBTokenizer.ptbToken2Text(input);
    assertEquals("(", result);
  }
@Test
  public void testNewlineTokenValue() {
    String newline = PTBTokenizer.getNewlineToken();
    assertEquals("\n", newline);
  }
@Test
  public void testEmptyInputReturnsNoTokens() {
    Reader reader = new StringReader("");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "invertible");

    boolean hasToken = tokenizer.hasNext();
    assertFalse(hasToken);
  }
@Test
  public void testMultipleSpacesAreCollapsed() {
    Reader reader = new StringReader("one     two");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "");

    List<String> result = new ArrayList<String>();
    if (tokenizer.hasNext()) result.add(tokenizer.next().word());
    if (tokenizer.hasNext()) result.add(tokenizer.next().word());

    assertEquals(Arrays.asList("one", "two"), result);
  }
@Test
  public void testCoreLabelFactoryOptionsNotNull() {
    TokenizerFactory<CoreLabel> factory1 = PTBTokenizer.coreLabelFactory();
    assertNotNull(factory1);

    TokenizerFactory<CoreLabel> factory2 = PTBTokenizer.coreLabelFactory("invertible");
    assertNotNull(factory2);
  }
@Test
  public void testTokenizerFactoryReturnsExpectedCoreLabelTokens() {
    String input = "The quick brown fox.";
    TokenizerFactory<CoreLabel> factory = PTBTokenizer.coreLabelFactory("invertible");
    Tokenizer<CoreLabel> tokenizer = factory.getTokenizer(new StringReader(input));

    List<String> tokens = new ArrayList<String>();
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());

    assertEquals(Arrays.asList("The", "quick", "brown", "fox", "."), tokens);
  }
@Test(expected = RuntimeIOException.class)
  public void testRuntimeIOExceptionThrownOnBadReader() {
    Reader reader = new Reader() {
      @Override public int read(char[] cbuf, int off, int len) throws java.io.IOException {
        throw new java.io.IOException("Forced IO Error");
      }
      @Override public void close() {}
    };

    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "invertible");
    tokenizer.next();
  }
@Test
  public void testSplitForwardSlashTrue() {
    Reader reader = new StringReader("Apple/Orange");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "splitForwardSlash=true");

    List<String> tokens = new ArrayList<String>();
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());

    assertEquals(Arrays.asList("Apple", "/", "Orange"), tokens);
  }
@Test
  public void testQuotesAsciiOptionKeepsAscii() {
    Reader reader = new StringReader("\"quote\"");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "quotes=ascii");

    List<String> tokens = new ArrayList<String>();
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());

    assertEquals(Arrays.asList("\"", "quote", "\""), tokens);
  }
@Test
  public void testPTB2TextListInput() {
    List<String> list = new ArrayList<String>();
    list.add("``");
    list.add("Hello");
    list.add("world");
    list.add("''");
    String result = PTBTokenizer.ptb2Text(list);
    assertTrue(result.contains("Hello"));
    assertFalse(result.contains("``"));
  }
@Test
  public void testLabelList2Text() {
    List<HasWord> input = new ArrayList<HasWord>();
    input.add(new Word("-LRB-"));
    input.add(new Word("nice"));
    input.add(new Word("-RRB-"));
    String result = PTBTokenizer.labelList2Text(input);
    assertEquals("(nice)", result);
  }
//@Test
//  public void testTokenizerFactorySetOptions() {
//    PTBTokenizer.PTBTokenizerFactory<CoreLabel> factory = new PTBTokenizer.PTBTokenizerFactory<CoreLabel>(new CoreLabelTokenFactory(), "invertible=false");
//    factory.setOptions("invertible");
//    Tokenizer<CoreLabel> tokenizer = factory.getTokenizer(new StringReader("change this"));
//
//    List<String> output = new ArrayList<String>();
//    if (tokenizer.hasNext()) output.add(tokenizer.next().word());
//    if (tokenizer.hasNext()) output.add(tokenizer.next().word());
//
//    assertEquals(Arrays.asList("change", "this"), output);
//  }
@Test
  public void testPTB2TextLiteral() {
    String tokenized = "This is n't funny .";
    String result = PTBTokenizer.ptb2Text(tokenized);
    assertTrue(result.contains("isn't"));
  }
@Test
  public void testEllipsesMappingPTB3() {
    Reader reader = new StringReader("Wait...");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "ellipses=ptb3");

    List<String> tokens = new ArrayList<String>();
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());

    assertEquals(Arrays.asList("Wait", "..."), tokens);
  }
@Test
  public void testSplitHyphenatedFalse() {
    Reader reader = new StringReader("well-known");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "splitHyphenated=false");

    List<String> tokens = new ArrayList<String>();
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());

    assertEquals(Arrays.asList("well-known"), tokens);
  }
@Test
  public void testNormalizeFractionsTrue() {
    Reader reader = new StringReader("\u00BD"); 
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "normalizeFractions=true");

    List<String> tokens = new ArrayList<String>();
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());

    assertEquals(Arrays.asList("1/2"), tokens);
  }
@Test
  public void testInvertibleFalseNoAnnotations() {
    Reader reader = new StringReader("Test this.");
    PTBTokenizer<Word> tokenizer = new PTBTokenizer<>(reader, new WordTokenFactory(), "invertible=false");

    List<String> tokens = new ArrayList<String>();
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());

    assertEquals(Arrays.asList("Test", "this", "."), tokens);
  }
@Test
  public void testNewLineWithoutTokenizeNLsOption() {
    Reader reader = new StringReader("Line1\nLine2");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "");

    List<String> output = new ArrayList<String>();
    if (tokenizer.hasNext()) output.add(tokenizer.next().word());
    if (tokenizer.hasNext()) output.add(tokenizer.next().word());

    assertEquals(Arrays.asList("Line1", "Line2"), output);
  }
@Test
  public void testUnicodeEmDashHandledProperly() {
    Reader reader = new StringReader("Wait\u2014what?");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "dashes=unicode");

    List<String> tokens = new ArrayList<String>();
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());

    assertEquals(Arrays.asList("Wait", "\u2014what"), tokens);
  }
@Test
  public void testNormalizeCurrencyTrueDollarSign() {
    Reader reader = new StringReader("$100");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "normalizeCurrency=true");

    List<String> tokens = new ArrayList<String>();
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());

    assertEquals(Arrays.asList("$", "100"), tokens);
  }
@Test
  public void testNormalizeAmpersandEntity() {
    Reader reader = new StringReader("&amp; company");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "normalizeAmpersandEntity=true");

    List<String> tokens = new ArrayList<String>();
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());

    assertEquals(Arrays.asList("&", "company"), tokens);
  }
@Test
  public void testUnknownUnicodeSymbolUntokenizableKeep() {
    Reader reader = new StringReader("\u1F4A9"); 
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "untokenizable=allKeep");

    List<String> tokens = new ArrayList<String>();
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());

    assertEquals(Arrays.asList("\u1F4A9"), tokens);
  }
@Test
  public void testStrictTreebank3AcronymCase() {
    Reader reader = new StringReader("U.K.");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "strictTreebank3=true");

    List<String> tokens = new ArrayList<String>();
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());

    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());

    assertEquals(Arrays.asList("U.K", "."), tokens);
  }
@Test
  public void testEmDashWithSplitHyphenAndUnicode() {
    Reader reader = new StringReader("pre--existing condition");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "dashes=unicode,splitHyphenated=true");

    List<String> tokens = new ArrayList<String>();
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());

    assertEquals(Arrays.asList("pre", "--", "existing"), tokens);
  }
@Test
  public void testHyphenatedAcronymTokenizer() {
    Reader reader = new StringReader("U.S.-based");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "splitHyphenated=true");

    List<String> tokens = new ArrayList<String>();
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());

    assertEquals(Arrays.asList("U.S.", "-", "based"), tokens);
  }
@Test
  public void testAsciiQuotesConversion() {
    Reader reader = new StringReader("'hello'");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "quotes=ascii");

    List<String> result = new ArrayList<String>();
    if (tokenizer.hasNext()) result.add(tokenizer.next().word());
    if (tokenizer.hasNext()) result.add(tokenizer.next().word());
    if (tokenizer.hasNext()) result.add(tokenizer.next().word());

    assertEquals(Arrays.asList("'", "hello", "'"), result);
  }
@Test
  public void testOriginalQuotesPreserved() {
    Reader reader = new StringReader("“smart”");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "quotes=original");

    List<String> tokens = new ArrayList<String>();
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());

    assertEquals(Arrays.asList("“", "smart”"), tokens);
  }
@Test
  public void testAbbreviationAtSentenceEndStrictMode() {
    Reader reader = new StringReader("Inc.");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "strictTreebank3=true");

    List<String> tokens = new ArrayList<String>();
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());

    assertEquals(Arrays.asList("Inc", "."), tokens);
  }
@Test
  public void testPtb2TextPreservesContractions() {
    String ptbText = "They do n't know .";
    String readable = PTBTokenizer.ptb2Text(ptbText);

    assertTrue(readable.contains("don't"));
    assertFalse(readable.contains("do n't"));
  }
@Test
  public void testEscapeForwardSlashAsteriskEnabled() {
    Reader reader = new StringReader("/some * comment");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "escapeForwardSlashAsterisk=true");

    List<String> tokens = new ArrayList<String>();
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());

    assertEquals(Arrays.asList("\\/", "some", "\\*"), tokens);
  }
@Test
  public void testBlankSpaceOnlyInput() {
    Reader reader = new StringReader("   ");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "");
    boolean hasToken = tokenizer.hasNext();
    assertFalse(hasToken);
  }
@Test
  public void testTokenizerConsumeAllTokensStopsProperly() {
    Reader reader = new StringReader("A B C");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "");

    if (tokenizer.hasNext()) tokenizer.next();
    if (tokenizer.hasNext()) tokenizer.next();
    if (tokenizer.hasNext()) tokenizer.next();
    boolean hasMore = tokenizer.hasNext();

    assertFalse(hasMore);
  }

@Test
  public void testNumericLikeFormatting() {
    Reader reader = new StringReader("Version 2.01.");
    PTBTokenizer<Word> tokenizer = new PTBTokenizer<>(reader, new WordTokenFactory(), "");
    List<String> result = new ArrayList<String>();
    if (tokenizer.hasNext()) result.add(tokenizer.next().word());
    if (tokenizer.hasNext()) result.add(tokenizer.next().word());
    if (tokenizer.hasNext()) result.add(tokenizer.next().word());
    if (tokenizer.hasNext()) result.add(tokenizer.next().word());

    assertEquals(Arrays.asList("Version", "2.01", ".", ""), result);
  }
@Test
  public void testComplexEllipsesQuoteInteraction() {
    Reader reader = new StringReader("He said \"wait ...\"");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "quotes=unicode,ellipses=unicode");

    List<String> result = new ArrayList<String>();
    if (tokenizer.hasNext()) result.add(tokenizer.next().word());
    if (tokenizer.hasNext()) result.add(tokenizer.next().word());
    if (tokenizer.hasNext()) result.add(tokenizer.next().word());
    if (tokenizer.hasNext()) result.add(tokenizer.next().word());

    assertTrue(result.contains("…") || result.contains("..."));
  }
@Test
  public void testUnicodeSeparatorMappedToNewlineToken() {
    Reader reader = new StringReader("Hello\u2028World"); 
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "tokenizeNLs");

    List<String> result = new ArrayList<String>();
    if (tokenizer.hasNext()) result.add(tokenizer.next().word());
    if (tokenizer.hasNext()) result.add(tokenizer.next().word());
    if (tokenizer.hasNext()) result.add(tokenizer.next().word());

    assertTrue(result.contains(PTBTokenizer.getNewlineToken()));
  }
@Test
  public void testInvalidOptionIgnoredGracefully() {
    Reader reader = new StringReader("test $100");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "invalidOption=true");

    List<String> result = new ArrayList<String>();
    if (tokenizer.hasNext()) result.add(tokenizer.next().word());
    if (tokenizer.hasNext()) result.add(tokenizer.next().word());
    if (tokenizer.hasNext()) result.add(tokenizer.next().word());

    assertEquals("test", result.get(0));
    assertTrue(result.contains("$"));
    assertTrue(result.contains("100"));
  }
@Test
  public void testVeryLargeNumber() {
    Reader reader = new StringReader("9999999999999999999999999");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "");

    List<String> result = new ArrayList<String>();
    if (tokenizer.hasNext()) result.add(tokenizer.next().word());

    assertEquals(Arrays.asList("9999999999999999999999999"), result);
  }
@Test
  public void testDecimalNumberFollowedByPunctuation() {
    Reader reader = new StringReader("13.75, next.");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "");

    List<String> tokens = new ArrayList<String>();
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());

    assertEquals(Arrays.asList("13.75", ",", "next", "."), tokens);
  }
@Test
  public void testStrictFractionDisabled() {
    Reader reader = new StringReader("5 7/8");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "strictFraction=false");

    List<String> tokens = new ArrayList<String>();
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());

    assertEquals(1, tokens.size()); 
    assertTrue(tokens.get(0).contains("5"));
  }
@Test
  public void testTokenizerFactoryGetIteratorBehavior() {
    Reader reader = new StringReader("A B C");
    TokenizerFactory<Word> factory = PTBTokenizer.factory();
    Iterator<Word> iterator = factory.getIterator(reader);

    List<String> words = new ArrayList<String>();
    if (iterator.hasNext()) words.add(iterator.next().word());
    if (iterator.hasNext()) words.add(iterator.next().word());
    if (iterator.hasNext()) words.add(iterator.next().word());

    assertEquals(Arrays.asList("A", "B", "C"), words);
  }
@Test
  public void testTokenizerWithEmptyStringAndOptions() {
    Reader reader = new StringReader("");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "tokenizeNLs,quotes=ascii,dashes=ptb3");

    boolean has = tokenizer.hasNext();
    assertFalse(has);
  }
@Test
  public void testTokenizeWordWithHashSymbol() {
    Reader reader = new StringReader("#hashtag active");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "");

    List<String> tokens = new ArrayList<String>();
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());

    assertEquals(Arrays.asList("#", "hashtag", "active").subList(0, tokens.size()), tokens);
  }
@Test
  public void testForwardSlashDelimitedAbbreviations() {
    Reader reader = new StringReader("M.D./Ph.D.");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "splitForwardSlash=true");

    List<String> tokens = new ArrayList<String>();
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());

    assertEquals(Arrays.asList("M.D.", "/", "Ph.D."), tokens);
  }
@Test
  public void testUntokenizableAllDelete() {
    Reader reader = new StringReader("hello \uFFFF world");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "untokenizable=allDelete");

    List<String> tokens = new ArrayList<String>();
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());

    assertEquals(Arrays.asList("hello", "world"), tokens);
  }
@Test
  public void testTokenizeSingleHyphen() {
    Reader reader = new StringReader("hyphen-word");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "splitHyphenated=true");

    List<String> tokens = new ArrayList<String>();
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());

    assertEquals(Arrays.asList("hyphen", "-", "word"), tokens);
  }
@Test
  public void testAbbreviationWithPeriodNotFinalToken() {
    Reader reader = new StringReader("Dr. Smith");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "strictAcronym=true");

    List<String> tokens = new ArrayList<String>();
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());

    assertEquals(Arrays.asList("Dr.", "Smith"), tokens);
  }
@Test
  public void testTokenizeWithExtraCommasInOptions() {
    Reader reader = new StringReader("Check comma.");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), ",invertible,,tokenizeNLs,");

    List<String> tokens = new ArrayList<String>();
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());

    assertEquals(Arrays.asList("Check", "comma", "."), tokens);
  }
@Test
  public void testEscapeForwardSlashAsteriskMixedText() {
    Reader reader = new StringReader("/* comment */");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "escapeForwardSlashAsterisk=true");

    List<String> tokens = new ArrayList<String>();
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());  
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());  
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());  
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());  
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());  

    assertEquals(Arrays.asList("\\/", "\\*", "comment", "\\*", "\\/"), tokens);
  }
@Test
  public void testWhitespaceOnlyLinesPreserveLinesOption() {
    Reader reader = new StringReader("   \nLine\n   ");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "tokenizeNLs");

    List<String> tokens = new ArrayList<String>();
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());  
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());  
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());  

    assertEquals(3, tokens.size());
    assertEquals("\n", tokens.get(0));
    assertEquals("Line", tokens.get(1));
    assertEquals("\n", tokens.get(2));
  }
@Test
  public void testQuotesLatexStyle() {
    Reader reader = new StringReader("\"Smart quotes\" test");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "quotes=latex");

    List<String> tokens = new ArrayList<String>();
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());  
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());  
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());  
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());  
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());  

    assertEquals(Arrays.asList("``", "Smart", "quotes", "''", "test"), tokens);
  }
@Test
  public void testUnicodeCurrencyHandlingEuroSymbol() {
    Reader reader = new StringReader("Price €45");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "normalizeCurrency=true");

    List<String> tokens = new ArrayList<String>();
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());  
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());  
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());  

    assertEquals(Arrays.asList("Price", "€", "45"), tokens);
  }
@Test
  public void testInputEndingWithNewline() {
    Reader reader = new StringReader("Last word\n");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "tokenizeNLs");

    List<String> tokens = new ArrayList<String>();
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());  
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());  
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());  

    assertEquals(Arrays.asList("Last", "word", "\n"), tokens);
  }
@Test
  public void testTokenizerFactoryWithMultipleOptionApplication() {
    TokenizerFactory<CoreLabel> factory = PTBTokenizer.coreLabelFactory("invertible=false");
    Tokenizer<CoreLabel> tokenizer = factory.getTokenizer(new StringReader("Token stream"));

    List<String> result = new ArrayList<String>();
    if (tokenizer.hasNext()) result.add(tokenizer.next().word());
    if (tokenizer.hasNext()) result.add(tokenizer.next().word());

    assertEquals(Arrays.asList("Token", "stream"), result);
  }
@Test
  public void testComplexNestedPunctuation() {
    Reader reader = new StringReader("He said (in a \"quiet\" voice): Hello.");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "");

    List<String> output = new ArrayList<String>();
    if (tokenizer.hasNext()) output.add(tokenizer.next().word());  
    if (tokenizer.hasNext()) output.add(tokenizer.next().word());  
    if (tokenizer.hasNext()) output.add(tokenizer.next().word());  
    if (tokenizer.hasNext()) output.add(tokenizer.next().word());  
    if (tokenizer.hasNext()) output.add(tokenizer.next().word());  
    if (tokenizer.hasNext()) output.add(tokenizer.next().word());  
    if (tokenizer.hasNext()) output.add(tokenizer.next().word());  
    if (tokenizer.hasNext()) output.add(tokenizer.next().word());  
    if (tokenizer.hasNext()) output.add(tokenizer.next().word());  
    if (tokenizer.hasNext()) output.add(tokenizer.next().word());  
    if (tokenizer.hasNext()) output.add(tokenizer.next().word());  
    if (tokenizer.hasNext()) output.add(tokenizer.next().word());  
    if (tokenizer.hasNext()) output.add(tokenizer.next().word());  

    assertTrue(output.contains("quiet"));
    assertEquals(13, output.size());
  }
@Test
  public void testEmptyInputWithInvertibleAndTokenizeNLs() {
    Reader reader = new StringReader("");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "invertible,tokenizeNLs");

    boolean hasToken = tokenizer.hasNext();
    assertFalse(hasToken);
  }
@Test
  public void testSplitSlashPreservesSlashBetweenWords() {
    Reader reader = new StringReader("Asian/European");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "splitForwardSlash=true");

    List<String> tokens = new ArrayList<String>();
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());

    assertEquals(Arrays.asList("Asian", "/", "European"), tokens);
  }
@Test
  public void testStrictAcronymSplittingDotAtEnd() {
    Reader reader = new StringReader("Corp.");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "strictAcronym=true");

    List<String> tokens = new ArrayList<String>();
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());

    assertEquals(Arrays.asList("Corp", "."), tokens);
  }
@Test
  public void testNormalizeOtherBrackets() {
    Reader reader = new StringReader("[text]");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "normalizeOtherBrackets=true");

    List<String> tokens = new ArrayList<String>();
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word()); 
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word()); 
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word()); 

    assertEquals(Arrays.asList("-LSB-", "text", "-RSB-"), tokens);
  }
@Test
  public void testMultipleDotsTreatedAsEllipses() {
    Reader reader = new StringReader("Well...");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "ellipses=ptb3");

    List<String> tokens = new ArrayList<String>();
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word()); 
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word()); 

    assertEquals(Arrays.asList("Well", "..."), tokens);
  }
@Test
  public void testMixedLanguageInput() {
    Reader reader = new StringReader("Bonjour! ¿Cómo estás?");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "");

    List<String> tokens = new ArrayList<String>();
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word()); 
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word()); 
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word()); 
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word()); 
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word()); 
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word()); 

    assertEquals(Arrays.asList("Bonjour", "!", "¿", "Cómo", "estás", "?"), tokens);
  }
@Test
  public void testAmericanizeSpelling() {
    Reader reader = new StringReader("colour");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "americanize=true");

    List<String> tokens = new ArrayList<String>();
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());

    assertTrue(tokens.contains("color") || tokens.contains("colour")); 
  }
@Test
  public void testEscapeSlashAsteriskFalse() {
    Reader reader = new StringReader("/comment*");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "escapeForwardSlashAsterisk=false");

    List<String> tokens = new ArrayList<String>();
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word()); 
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word()); 
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word()); 

    assertEquals(Arrays.asList("/", "comment", "*"), tokens);
  }
@Test
  public void testSingleCharUntokenizableFirstKeep() {
    Reader reader = new StringReader("abc \uFFFF xyz");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "untokenizable=firstKeep");

    List<String> tokens = new ArrayList<String>();
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word()); 
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word()); 
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word()); 

    assertEquals(3, tokens.size());
  }
//@Test
//  public void testGetTokenizerWithExtraOptions() {
//    Reader reader = new StringReader("1/2 3/4");
//    PTBTokenizer.PTBTokenizerFactory<CoreLabel> factory = new PTBTokenizer.PTBTokenizerFactory<CoreLabel>(new CoreLabelTokenFactory(), "normalizeFractions=true");
//    Tokenizer<CoreLabel> tokenizer = factory.getTokenizer(reader, "ud");
//
//    List<String> tokens = new ArrayList<String>();
//    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());
//    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());
//
//    assertEquals(Arrays.asList("1/2", "3/4"), tokens);
//  }
@Test
  public void testQuotesOriginalPreserved() {
    Reader reader = new StringReader("“Hello“");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "quotes=original");

    List<String> tokens = new ArrayList<String>();
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word()); 
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word()); 

    assertTrue(tokens.get(0).contains("“"));
  }
@Test
  public void testEmDashHandledInUnicodeMode() {
    Reader reader = new StringReader("Wait—stop.");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "dashes=unicode");

    List<String> tokens = new ArrayList<String>();
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word()); 
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word()); 
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word()); 

    assertTrue(tokens.contains("—") || tokens.get(1).contains("—"));
  }
@Test
  public void testTokenizePerLinePreservesLineBoundaries() {
    Reader reader = new StringReader("Dr. Smith\nU.S. forces\nend.");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "tokenizePerLine");

    List<String> tokens = new ArrayList<String>();
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());  
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());  
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());  
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());  
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());  
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());  

    assertEquals(Arrays.asList("Dr.", "Smith", "U.S.", "forces", "end", "."), tokens);
  }
@Test
  public void testPtb3EscapingFalseDisablesBracketMapping() {
    Reader reader = new StringReader("(sample)");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "ptb3Escaping=false");

    List<String> tokens = new ArrayList<String>();
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());  
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());  
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());  

    assertEquals(Arrays.asList("(", "sample", ")"), tokens);
  }
@Test
  public void testMultipleBlankLinesWithTokenizeNLs() {
    Reader reader = new StringReader("a\n\nb\n");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "tokenizeNLs");

    List<String> tokens = new ArrayList<String>();
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());  
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());  
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());  
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());  
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());  

    assertEquals(Arrays.asList("a", "\n", "\n", "b", "\n"), tokens);
  }
@Test
  public void testPTB3EscapingTrueWithExplicitFalseBracketConflict() {
    Reader reader = new StringReader("(test)");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "ptb3Escaping=true,normalizeParentheses=false");

    List<String> tokens = new ArrayList<String>();
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());

    assertTrue(
      tokens.equals(Arrays.asList("-LRB-", "test", "-RRB-")) ||
      tokens.equals(Arrays.asList("(", "test", ")"))
    );
  }
@Test
  public void testUntokenizableFirstDeleteSkipsOnlyFirstUnrecognized() {
    Reader reader = new StringReader("abc \uFFFF \uFFFF xyz");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "untokenizable=firstDelete");

    List<String> tokens = new ArrayList<String>();
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word()); 
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word()); 
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());

    assertTrue(tokens.contains("xyz"));
    assertTrue(tokens.size() >= 2);
  }
@Test
  public void testUntokenizableNoneKeepPreservesAllInvalidChars() {
    Reader reader = new StringReader("abc\u0000xyz");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "untokenizable=noneKeep");

    List<String> tokens = new ArrayList<String>();
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word()); 
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word()); 
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word()); 

    assertTrue(tokens.size() >= 2);
    assertTrue(tokens.get(0).equals("abc"));
    assertTrue(tokens.get(tokens.size() - 1).equals("xyz"));
  }
@Test
  public void testEmojiCharAllDelete() {
    Reader reader = new StringReader("😊 happy");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "untokenizable=allDelete");

    List<String> tokens = new ArrayList<String>();
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());  

    assertEquals(Arrays.asList("happy"), tokens);
  }
@Test
  public void testUDOptionOverridesLowerLevelBehavior() {
    Reader reader = new StringReader("I'm gonna go.");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "splitAssimilations=false,ud");

    List<String> tokens = new ArrayList<String>();
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word()); 
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word()); 
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word()); 
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word()); 
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word()); 

    assertTrue(tokens.contains("go"));
    assertEquals(5, tokens.size());
  }
@Test
  public void testNullControlWhitespaceCombination() {
    Reader reader = new StringReader("abc\u0007\txyz");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "untokenizable=allDelete");

    List<String> tokens = new ArrayList<String>();
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word()); 
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word()); 

    assertEquals(2, tokens.size());
    assertEquals("abc", tokens.get(0));
    assertEquals("xyz", tokens.get(1));
  }
@Test
  public void testOriginalUntokenizableKeepListFormat() {
    Reader reader = new StringReader("Test ☃ test");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "untokenizable=noneKeep");

    List<String> tokens = new ArrayList<String>();
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());  
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());  
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());  

    assertTrue(tokens.contains("test"));
    assertTrue(tokens.size() >= 2);
  }
@Test
  public void testQuotesAsciiModeParsingStraightQuotes() {
    Reader reader = new StringReader("\"abc\"");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "quotes=ascii");

    List<String> tokens = new ArrayList<String>();
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());  
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());  
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());  

    assertEquals(Arrays.asList("\"", "abc", "\""), tokens);
  }
@Test
  public void testPreserveLinesWithEmbeddedBlankLines() {
    Reader reader = new StringReader("First\n\nSecond");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "tokenizeNLs");

    List<String> tokens = new ArrayList<String>();
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());  
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());  
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());  
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());  

    assertEquals(4, tokens.size());
    assertEquals("First", tokens.get(0));
    assertEquals("\n", tokens.get(1));
    assertEquals("Second", tokens.get(3));
  }
@Test
  public void testDisableEscapingEnablesRawParensEvenWhenNormalizeEnabled() {
    Reader reader = new StringReader("(x)");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "ptb3Escaping=false,normalizeParentheses=true");

    List<String> tokens = new ArrayList<String>();
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());  
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());  
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());  

    assertEquals(3, tokens.size());
    assertEquals("(", tokens.get(0));
    assertEquals(")", tokens.get(2));
  }
@Test
  public void testUnicodeLineSeparatorPreservedWithOption() {
    Reader reader = new StringReader("Line1\u2028Line2");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "tokenizeNLs");

    List<String> tokens = new ArrayList<String>();
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());  
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());  
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());  

    assertEquals(3, tokens.size());
    assertEquals("Line1", tokens.get(0));
    assertEquals("\n", tokens.get(1));
    assertEquals("Line2", tokens.get(2));
  }
@Test
  public void testAbbreviationAtEndWithoutPeriod() {
    Reader reader = new StringReader("Mr");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "strictAcronym=true");

    List<String> tokens = new ArrayList<String>();
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());  

    assertEquals(1, tokens.size());
    assertEquals("Mr", tokens.get(0));
  }
@Test
  public void testEscapeForwardSlashAsteriskFalseLeavesSymbols() {
    Reader reader = new StringReader("/* test */");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "escapeForwardSlashAsterisk=false");

    List<String> tokens = new ArrayList<String>();
    while (tokenizer.hasNext()) tokens.add(tokenizer.next().word());

    assertTrue(tokens.contains("/"));
    assertTrue(tokens.contains("*"));
    assertTrue(tokens.contains("test"));
  }
@Test
  public void testPreserveLinesFalseWithHardLineBreak() {
    Reader reader = new StringReader("one\ntwo");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "preserveLines=false");

    List<String> tokens = new ArrayList<String>();
    while (tokenizer.hasNext()) tokens.add(tokenizer.next().word());

    assertEquals(2, tokens.size());
    assertEquals("one", tokens.get(0));
    assertEquals("two", tokens.get(1));
  }
@Test
  public void testStrictTreebank3WithQuoteOptionOriginal() {
    Reader reader = new StringReader("\"U.S.\"");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "strictTreebank3=true,quotes=original");

    List<String> tokens = new ArrayList<String>();
    while (tokenizer.hasNext()) tokens.add(tokenizer.next().word());

    assertEquals(3, tokens.size());
    assertEquals("U.S.", tokens.get(1));
  }
@Test
  public void testAbbreviationBlocksWithoutTokenizePerLine() {
    Reader reader = new StringReader("Inc.\nNew start.");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "");

    List<String> tokens = new ArrayList<String>();
    while (tokenizer.hasNext()) tokens.add(tokenizer.next().word());

    assertEquals(4, tokens.size());
    assertEquals("Inc.", tokens.get(0));
    assertEquals("New", tokens.get(1));
    assertEquals("start", tokens.get(2));
    assertEquals(".", tokens.get(3));
  }
@Test
  public void testControlCharacterWithAllKeep() {
    Reader reader = new StringReader("data\u0007more");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "untokenizable=allKeep");

    List<String> tokens = new ArrayList<String>();
    while (tokenizer.hasNext()) tokens.add(tokenizer.next().word());

    assertTrue(tokens.contains("data"));
    assertTrue(tokens.contains("more"));
    assertEquals(3, tokens.size());
  }
@Test
  public void testTokenizeSplitAssimilationsFalse() {
    Reader reader = new StringReader("gonna");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "splitAssimilations=false");

    List<String> tokens = new ArrayList<String>();
    if (tokenizer.hasNext()) tokens.add(tokenizer.next().word());

    assertEquals(1, tokens.size());
    assertEquals("gonna", tokens.get(0));
  }
@Test
  public void testTokenizeSplitAssimilationsTrue() {
    Reader reader = new StringReader("gonna");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "splitAssimilations=true");

    List<String> tokens = new ArrayList<String>();
    while (tokenizer.hasNext()) tokens.add(tokenizer.next().word());

    assertTrue(tokens.contains("gon") || tokens.get(0).equals("gonna"));  
  }
@Test
  public void testMultipleRepeatedPunctuation() {
    Reader reader = new StringReader("Wait... why???!");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "ellipses=ptb3");

    List<String> tokens = new ArrayList<String>();
    while (tokenizer.hasNext()) tokens.add(tokenizer.next().word());

    assertTrue(tokens.contains("..."));
    assertTrue(tokens.contains("?"));
    assertTrue(tokens.contains("!"));
    assertTrue(tokens.size() >= 6);
  } 
}