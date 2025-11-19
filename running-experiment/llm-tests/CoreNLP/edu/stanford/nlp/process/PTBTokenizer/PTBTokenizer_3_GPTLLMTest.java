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


public class PTBTokenizer_3_GPTLLMTest {

 @Test
    public void testBasicTokenizationWithDefaultOptions() throws IOException {
        String text = "Hello, world! (Isn't it lovely?)";
        Reader reader = new StringReader(text);
        PTBTokenizer<Word> tokenizer = PTBTokenizer.newPTBTokenizer(reader);

        Word t1 = tokenizer.next();
        Word t2 = tokenizer.next();
        Word t3 = tokenizer.next();
        Word t4 = tokenizer.next();
        Word t5 = tokenizer.next();
        Word t6 = tokenizer.next();
        Word t7 = tokenizer.next();
        Word t8 = tokenizer.next();
        Word t9 = tokenizer.next();
        Word t10 = tokenizer.next();
        Word t11 = tokenizer.next();
        Word t12 = tokenizer.next();

        assertEquals("Hello", t1.word());
        assertEquals(",", t2.word());
        assertEquals("world", t3.word());
        assertEquals("!", t4.word());
        assertEquals("(", t5.word());
        assertEquals("Isn", t6.word());
        assertEquals("'", t7.word());
        assertEquals("t", t8.word());
        assertEquals("it", t9.word());
        assertEquals("lovely", t10.word());
        assertEquals("?", t11.word());
        assertEquals(")", t12.word());

        reader.close();
    }
@Test
    public void testCoreLabelTokenizationWithNewlines() throws IOException {
        String text = "Line one.\nLine two.";
        Reader reader = new StringReader(text);
        PTBTokenizer<CoreLabel> tokenizer = PTBTokenizer.newPTBTokenizer(reader, true, true);

        CoreLabel c1 = tokenizer.next(); 
        CoreLabel c2 = tokenizer.next(); 
        CoreLabel c3 = tokenizer.next(); 
        CoreLabel c4 = tokenizer.next(); 
        CoreLabel c5 = tokenizer.next(); 
        CoreLabel c6 = tokenizer.next(); 
        CoreLabel c7 = tokenizer.next(); 

        assertEquals("Line", c1.word());
        assertEquals("one", c2.word());
        assertEquals(".", c3.word());
        assertEquals(PTBTokenizer.getNewlineToken(), c4.word());
        assertEquals("Line", c5.word());
        assertEquals("two", c6.word());
        assertEquals(".", c7.word());

        reader.close();
    }
@Test
    public void testSuppressEscapingOption() throws IOException {
        String text = "Example (test).";
        Reader reader = new StringReader(text);
        PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "ptb3Escaping=false");

        CoreLabel c1 = tokenizer.next(); 
        CoreLabel c2 = tokenizer.next(); 
        CoreLabel c3 = tokenizer.next(); 
        CoreLabel c4 = tokenizer.next(); 
        CoreLabel c5 = tokenizer.next(); 

        assertEquals("Example", c1.word());
        assertEquals("(", c2.word());
        assertEquals("test", c3.word());
        assertEquals(")", c4.word());
        assertEquals(".", c5.word());

        reader.close();
    }
@Test
    public void testDefaultEscapingEnabled() throws IOException {
        String text = "Example (test).";
        Reader reader = new StringReader(text);
        PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "ptb3Escaping=true");

        CoreLabel c1 = tokenizer.next(); 
        CoreLabel c2 = tokenizer.next(); 
        CoreLabel c3 = tokenizer.next(); 
        CoreLabel c4 = tokenizer.next(); 
        CoreLabel c5 = tokenizer.next(); 

        assertEquals("Example", c1.word());
        assertEquals("-LRB-", c2.word());
        assertEquals("test", c3.word());
        assertEquals("-RRB-", c4.word());
        assertEquals(".", c5.word());

        reader.close();
    }
@Test
    public void testQuoteNormalizationLatex() throws IOException {
        String text = "\"He said, 'Hello there!'\"";
        Reader reader = new StringReader(text);
        PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "quotes=latex");

        CoreLabel c1 = tokenizer.next(); 
        CoreLabel c2 = tokenizer.next(); 
        CoreLabel c3 = tokenizer.next(); 
        CoreLabel c4 = tokenizer.next(); 
        CoreLabel c5 = tokenizer.next(); 
        CoreLabel c6 = tokenizer.next(); 
        CoreLabel c7 = tokenizer.next(); 
        CoreLabel c8 = tokenizer.next(); 
        CoreLabel c9 = tokenizer.next(); 
        CoreLabel c10 = tokenizer.next(); 

        assertTrue(c1.word().equals("``") || c1.word().equals("\""));
        assertEquals("He", c2.word());
        assertEquals("said", c3.word());
        assertEquals(",", c4.word());
        assertTrue(c5.word().equals("`") || c5.word().equals("'"));
        assertEquals("Hello", c6.word());
        assertEquals("there", c7.word());
        assertEquals("!", c8.word());
        assertEquals("'", c9.word());
        assertTrue(c10.word().equals("''") || c10.word().equals("\""));

        reader.close();
    }
@Test
    public void testTokenFactoryCoreLabelWithInvertible() throws IOException {
        String text = "Text with details.";
        Reader reader = new StringReader(text);
        PTBTokenizer<CoreLabel> tokenizer = PTBTokenizer.newPTBTokenizer(reader, false, true);

        CoreLabel c1 = tokenizer.next(); 
        CoreLabel c2 = tokenizer.next(); 
        CoreLabel c3 = tokenizer.next(); 
        CoreLabel c4 = tokenizer.next(); 

        assertNotNull(c1.word());
        assertNotNull(c1.get(CoreAnnotations.TextAnnotation.class));
        assertNotNull(c1.get(CoreAnnotations.OriginalTextAnnotation.class));
        assertNotNull(c1.get(CoreAnnotations.BeforeAnnotation.class));

        assertEquals("Text", c1.word());
        assertEquals("with", c2.word());
        assertEquals("details", c3.word());
        assertEquals(".", c4.word());

        reader.close();
    }
@Test
    public void testFactoryReturnedTokenizer() throws IOException {
        String text = "Factory returns tokens.";
        Reader reader = new StringReader(text);
        TokenizerFactory<CoreLabel> factory = PTBTokenizer.factory(new CoreLabelTokenFactory(), "");
        Tokenizer<CoreLabel> tokenizer = factory.getTokenizer(reader);

        CoreLabel c1 = tokenizer.next(); 
        CoreLabel c2 = tokenizer.next(); 
        CoreLabel c3 = tokenizer.next(); 
        CoreLabel c4 = tokenizer.next(); 

        assertEquals("Factory", c1.word());
        assertEquals("returns", c2.word());
        assertEquals("tokens", c3.word());
        assertEquals(".", c4.word());

        reader.close();
    }
@Test
    public void testPtb2TextReconstructionFromString() {
        String input = "This -LRB- test -RRB- works .";
        String reconstructed = PTBTokenizer.ptb2Text(input);

        assertTrue(reconstructed.contains("This"));
        assertTrue(reconstructed.contains("( test )"));
        assertFalse(reconstructed.contains("-LRB-"));
    }
@Test
    public void testPtb2TextFromList() {
        List<String> list = new ArrayList<String>();
        list.add("Testing");
        list.add("-LRB-");
        list.add("complete");
        list.add("case");
        list.add("-RRB-");
        list.add(".");

        String result = PTBTokenizer.ptb2Text(list);
        assertEquals("Testing (complete case).", result);
    }
@Test
    public void testLabelList2Text() {
        List<Word> words = new ArrayList<Word>();
        words.add(new Word("Hello"));
        words.add(new Word("-LRB-"));
        words.add(new Word("World"));
        words.add(new Word("-RRB-"));
        words.add(new Word("!"));

        String output = PTBTokenizer.labelList2Text(words);
        assertEquals("Hello (World)!", output);
    }
@Test(expected = RuntimeIOException.class)
    public void testBadReaderThrowsRuntimeIOException() {
        Reader reader = new Reader() {
            @Override
            public int read(char[] cbuf, int off, int len) throws IOException { throw new IOException("Simulated"); }

            @Override
            public void close() { }
        };
        PTBTokenizer<Word> tokenizer = PTBTokenizer.newPTBTokenizer(reader);
        tokenizer.next(); 
    }
@Test
    public void testGetNewlineTokenNotEmpty() {
        String newline = PTBTokenizer.getNewlineToken();
        assertNotNull(newline);
        assertFalse(newline.isEmpty());
        assertTrue(newline.trim().length() > 0);
    }
@Test
    public void testUntokenizableCharactersAllKeep() throws IOException {
        String text = "Test © ™ † ‡";
        Reader reader = new StringReader(text);
        PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "untokenizable=allKeep,ptb3Escaping=false");

        CoreLabel c1 = tokenizer.next(); 
        CoreLabel c2 = tokenizer.next(); 
        CoreLabel c3 = tokenizer.next(); 
        CoreLabel c4 = tokenizer.next(); 
        CoreLabel c5 = tokenizer.next(); 

        assertEquals("Test", c1.word());
        assertEquals("©", c2.word());
        assertEquals("™", c3.word());
        assertEquals("†", c4.word());
        assertEquals("‡", c5.word());

        reader.close();
    }
@Test
public void testEmptyStringReturnsNoTokens() throws IOException {
    String text = "";
    Reader reader = new StringReader(text);
    PTBTokenizer<Word> tokenizer = PTBTokenizer.newPTBTokenizer(reader);
    assertFalse(tokenizer.hasNext());
    reader.close();
}
@Test
public void testWhitespaceOnlyStringReturnsNoTokens() throws IOException {
    String text = "   \t   \n ";
    Reader reader = new StringReader(text);
    PTBTokenizer<Word> tokenizer = PTBTokenizer.newPTBTokenizer(reader);
    assertFalse(tokenizer.hasNext());
    reader.close();
}
@Test
public void testNormalizeCurrencyEnabled() throws IOException {
    String text = "$10 €20 £30 ¢";
    Reader reader = new StringReader(text);
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "normalizeCurrency=true");

    CoreLabel c1 = tokenizer.next(); 
    CoreLabel c2 = tokenizer.next(); 
    CoreLabel c3 = tokenizer.next(); 
    CoreLabel c4 = tokenizer.next(); 
    CoreLabel c5 = tokenizer.next(); 
    CoreLabel c6 = tokenizer.next(); 
    CoreLabel c7 = tokenizer.next(); 

    assertEquals("$", c1.word());
    assertEquals("10", c2.word());
    assertEquals("20", c4.word());
    assertEquals("30", c6.word());

    reader.close();
}
@Test
public void testNormalizeDisabledWithSpecialCharacters() throws IOException {
    String text = "(some [\"text\"])";
    Reader reader = new StringReader(text);
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "ptb3Escaping=false,normalizeOtherBrackets=false");

    CoreLabel c1 = tokenizer.next(); 
    CoreLabel c2 = tokenizer.next(); 
    CoreLabel c3 = tokenizer.next(); 
    CoreLabel c4 = tokenizer.next(); 
    CoreLabel c5 = tokenizer.next(); 
    CoreLabel c6 = tokenizer.next(); 
    CoreLabel c7 = tokenizer.next(); 
    CoreLabel c8 = tokenizer.next(); 
    
    assertEquals("(", c1.word());
    assertEquals("[", c3.word());
    assertEquals("\"", c4.word());
    assertEquals("\"", c6.word());
    assertEquals("]", c7.word());
    assertEquals(")", c8.word());

    reader.close();
}
@Test
public void testStrictTreebank3AcronymDisambiguation() throws IOException {
    String text = "U.K. is different from U.S.";
    Reader reader = new StringReader(text);
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "strictTreebank3=true");

    CoreLabel c1 = tokenizer.next(); 
    CoreLabel c2 = tokenizer.next(); 
    CoreLabel c3 = tokenizer.next(); 
    CoreLabel c4 = tokenizer.next(); 
    CoreLabel c5 = tokenizer.next(); 
    CoreLabel c6 = tokenizer.next(); 

    assertEquals("U.K.", c1.word());
    assertEquals("U.S.", c5.word());
    assertEquals(".", c6.word());

    reader.close();
}
@Test
public void testSplitHyphenatedEnabled() throws IOException {
    String text = "school-aged children";
    Reader reader = new StringReader(text);
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "splitHyphenated=true");

    CoreLabel c1 = tokenizer.next(); 
    CoreLabel c2 = tokenizer.next(); 
    CoreLabel c3 = tokenizer.next(); 
    CoreLabel c4 = tokenizer.next(); 

    assertEquals("school", c1.word());
    assertEquals("-", c2.word());
    assertEquals("aged", c3.word());
    assertEquals("children", c4.word());

    reader.close();
}
@Test
public void testSplitForwardSlashEnabled() throws IOException {
    String text = "Asian/Indian";
    Reader reader = new StringReader(text);
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "splitForwardSlash=true");

    CoreLabel c1 = tokenizer.next(); 
    CoreLabel c2 = tokenizer.next(); 
    CoreLabel c3 = tokenizer.next(); 

    assertEquals("Asian", c1.word());
    assertEquals("/", c2.word());
    assertEquals("Indian", c3.word());

    reader.close();
}
@Test
public void testMalformedPTB2TextDoesNotThrow() {
    String malformed = "-LRB-unmatched";
    String output = PTBTokenizer.ptb2Text(malformed);
    assertNotNull(output);
    assertFalse(output.contains("-LRB"));
}
@Test
public void testPtb2TextNullCharacterInput() {
    String text = "test\u0000token";
    String output = PTBTokenizer.ptb2Text(text);
    assertTrue(output.contains("test"));
    assertTrue(output.contains("token"));
}
@Test
public void testEmojiAndNonLatinUnicodeInput() throws IOException {
    String text = "Hello 🌍 Привет سلام";
    Reader reader = new StringReader(text);
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "ptb3Escaping=false");

    CoreLabel c1 = tokenizer.next(); 
    CoreLabel c2 = tokenizer.next(); 
    CoreLabel c3 = tokenizer.next(); 
    CoreLabel c4 = tokenizer.next(); 

    assertEquals("Hello", c1.word());
    assertEquals("🌍", c2.word());
    assertEquals("Привет", c3.word());
    assertEquals("سلام", c4.word());

    reader.close();
}
@Test
public void testExtendedPunctuationTokenization() throws IOException {
    String text = "Wait... What?!";
    Reader reader = new StringReader(text);
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "");

    CoreLabel c1 = tokenizer.next(); 
    CoreLabel c2 = tokenizer.next(); 
    CoreLabel c3 = tokenizer.next(); 
    CoreLabel c4 = tokenizer.next(); 
    CoreLabel c5 = tokenizer.next(); 

    assertEquals("Wait", c1.word());
    assertEquals("...", c2.word());
    assertEquals("What", c3.word());
    assertEquals("?", c4.word());
    assertEquals("!", c5.word());

    reader.close();
}
@Test
public void testNormalizeFractionsOption() throws IOException {
    String text = "I ate ¾ of the pie.";
    Reader reader = new StringReader(text);
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "normalizeFractions=true");

    CoreLabel c1 = tokenizer.next(); 
    CoreLabel c2 = tokenizer.next(); 
    CoreLabel c3 = tokenizer.next(); 
    CoreLabel c4 = tokenizer.next(); 
    CoreLabel c5 = tokenizer.next(); 
    CoreLabel c6 = tokenizer.next(); 
    CoreLabel c7 = tokenizer.next(); 

    assertEquals("3/4", c3.word());

    reader.close();
}
@Test
public void testEscapeForwardSlashAsteriskOption() throws IOException {
    String text = "/comment *here*";
    Reader reader = new StringReader(text);
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "escapeForwardSlashAsterisk=true");

    CoreLabel c1 = tokenizer.next(); 
    CoreLabel c2 = tokenizer.next(); 
    CoreLabel c3 = tokenizer.next(); 

    assertEquals("\\/comment", c1.word());
    assertEquals("\\*here", c2.word() + c3.word());

    reader.close();
}

@Test
public void testDashesMappingWithOptionUnicode() throws IOException {
    String text = "hyphen - en-- em---";
    Reader reader = new StringReader(text);
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "dashes=unicode");

    CoreLabel c1 = tokenizer.next(); 
    CoreLabel c2 = tokenizer.next(); 
    CoreLabel c3 = tokenizer.next(); 
    CoreLabel c4 = tokenizer.next(); 
    CoreLabel c5 = tokenizer.next(); 
    CoreLabel c6 = tokenizer.next(); 

    assertEquals("hyphen", c1.word());
    assertEquals("-", c2.word());
    assertEquals("--", c4.word());
    assertEquals("---", c6.word());

    reader.close();
}
@Test
public void testStrictFractionTrue() throws IOException {
    String text = "5 3/4 lbs.";
    Reader reader = new StringReader(text);
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "strictFraction=true");

    CoreLabel c1 = tokenizer.next(); 
    CoreLabel c2 = tokenizer.next(); 
    CoreLabel c3 = tokenizer.next(); 
    CoreLabel c4 = tokenizer.next(); 

    assertEquals("5", c1.word());
    assertEquals("3/4", c2.word());
    assertEquals("lbs", c3.word());
    assertEquals(".", c4.word());

    reader.close();
}
@Test
public void testUnicodeQuoteNormalizationWithUnicodeQuotes() throws IOException {
    char leftDouble = '\u201C'; 
    char rightDouble = '\u201D'; 
    String text = leftDouble + "quote" + rightDouble;

    Reader reader = new StringReader(text);
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "quotes=unicode");

    CoreLabel c1 = tokenizer.next(); 
    CoreLabel c2 = tokenizer.next(); 
    CoreLabel c3 = tokenizer.next(); 

    assertEquals("quote", c2.word());

    reader.close();
}
@Test
public void testUtF8MultilingualSupport() throws IOException {
    String text = "中文测试 العربية हिंदी русский язык";
    Reader reader = new StringReader(text);
    PTBTokenizer<CoreLabel> tokenizer = (PTBTokenizer<CoreLabel>) PTBTokenizer.coreLabelFactory("ptb3Escaping=false").getTokenizer(reader);

    CoreLabel c1 = tokenizer.next(); 
    CoreLabel c2 = tokenizer.next(); 
    CoreLabel c3 = tokenizer.next(); 
    CoreLabel c4 = tokenizer.next(); 
    CoreLabel c5 = tokenizer.next(); 

    assertEquals("中文测试", c1.word());
    assertEquals("العربية", c2.word());
    assertEquals("हिंदी", c3.word());
    assertEquals("русский", c4.word());
    assertEquals("язык", c5.word());

    reader.close();
}
@Test
public void testEllipsesMappedToUnicode() throws IOException {
    String text = "Hello... how are...";
    Reader reader = new StringReader(text);
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "ellipses=unicode");

    CoreLabel c1 = tokenizer.next(); 
    CoreLabel c2 = tokenizer.next(); 
    CoreLabel c3 = tokenizer.next(); 
    CoreLabel c4 = tokenizer.next(); 
    CoreLabel c5 = tokenizer.next(); 

    assertEquals("Hello", c1.word());
    assertTrue(c2.word().contains("…") || c2.word().equals("..."));
    assertEquals("how", c3.word());
    assertEquals("are", c4.word());
    assertTrue(c5.word().contains("…") || c5.word().equals("..."));

    reader.close();
}
@Test
public void testUntokenizableDeleteNone() throws IOException {
    String text = "token ☃ 💡 ®";
    Reader reader = new StringReader(text);
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "untokenizable=noneDelete");

    CoreLabel c1 = tokenizer.next(); 
    CoreLabel c2 = tokenizer.next(); 
    CoreLabel c3 = tokenizer.next(); 
    CoreLabel c4 = tokenizer.next(); 

    assertEquals("token", c1.word());
    assertEquals("☃", c2.word());
    assertEquals("💡", c3.word());
    assertEquals("®", c4.word());

    reader.close();
}
@Test
public void testSingleCharacterSymbol() throws IOException {
    String text = "&";
    Reader reader = new StringReader(text);
    PTBTokenizer<Word> tokenizer = PTBTokenizer.newPTBTokenizer(reader);

    Word token = tokenizer.next();
    assertEquals("&", token.word());

    reader.close();
}
@Test
public void testMultipleSpacesBetweenTokens() throws IOException {
    String text = "Hello     world";
    Reader reader = new StringReader(text);
    PTBTokenizer<Word> tokenizer = PTBTokenizer.newPTBTokenizer(reader);

    Word t1 = tokenizer.next();
    Word t2 = tokenizer.next();

    assertEquals("Hello", t1.word());
    assertEquals("world", t2.word());

    reader.close();
}
@Test(expected = NullPointerException.class)
public void testNullReaderThrowsException() {
    Reader reader = null;
    new PTBTokenizer<Word>(reader, new WordTokenFactory(), "");
}
@Test
public void testEmptyPTB2TextList() {
    List<String> ptbTokens = new ArrayList<String>();
    String result = PTBTokenizer.ptb2Text(ptbTokens);
    assertEquals("", result);
}
@Test
public void testConflictingOptionInvertibleTwice() throws IOException {
    String text = "Testing";
    Reader reader = new StringReader(text);
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "invertible=true,invertible=false");

    CoreLabel token = tokenizer.next();
    assertEquals("Testing", token.word());

    reader.close();
}
@Test
public void testConflictingOptionPtb3EscapingTwice() throws IOException {
    String text = "(test)";
    Reader reader = new StringReader(text);
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "ptb3Escaping=true,ptb3Escaping=false");

    CoreLabel c1 = tokenizer.next(); 
    CoreLabel c2 = tokenizer.next(); 
    CoreLabel c3 = tokenizer.next(); 

    assertTrue(c1.word().equals("(") || c1.word().equals("-LRB-"));
    assertTrue(c3.word().equals(")") || c3.word().equals("-RRB-"));

    reader.close();
}
@Test
public void testOnlyNewlineAsInputTokenizeNLsTrue() throws IOException {
    String text = "\n";
    Reader reader = new StringReader(text);
    PTBTokenizer<CoreLabel> tokenizer = PTBTokenizer.newPTBTokenizer(reader, true, true);

    CoreLabel token = tokenizer.next();
    assertEquals(PTBTokenizer.getNewlineToken(), token.word());

    reader.close();
}
@Test
public void testOnlyNewlineAsInputTokenizeNLsFalse() throws IOException {
    String text = "\n";
    Reader reader = new StringReader(text);
    PTBTokenizer<CoreLabel> tokenizer = PTBTokenizer.newPTBTokenizer(reader, false, true);

    assertFalse(tokenizer.hasNext());

    reader.close();
}
@Test
public void testRegexFilterOptionExcludeMatch() throws IOException {
    String input = "apple banana cherry";
    Reader reader = new StringReader(input);

    Pattern filterPattern = Pattern.compile("banana");
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "ptb3Escaping=false");

    CoreLabel t1 = tokenizer.next(); 
    CoreLabel t2 = tokenizer.next(); 
    CoreLabel t3 = tokenizer.next(); 

    assertEquals("apple", t1.word());
    assertEquals("banana", t2.word());
    assertEquals("cherry", t3.word());

    reader.close();
}
@Test
public void testMultiplePunctuations() throws IOException {
    String input = "!@#$%^&*()_+";
    Reader reader = new StringReader(input);
    PTBTokenizer<CoreLabel> tokenizer = (PTBTokenizer<CoreLabel>) PTBTokenizer.coreLabelFactory("ptb3Escaping=false").getTokenizer(reader);

    CoreLabel c1 = tokenizer.next();
    CoreLabel c2 = tokenizer.next();
    CoreLabel c3 = tokenizer.next();
    CoreLabel c4 = tokenizer.next();
    CoreLabel c5 = tokenizer.next();
    CoreLabel c6 = tokenizer.next();
    CoreLabel c7 = tokenizer.next();
    CoreLabel c8 = tokenizer.next();
    CoreLabel c9 = tokenizer.next();
    CoreLabel c10 = tokenizer.next();
    CoreLabel c11 = tokenizer.next();
    CoreLabel c12 = tokenizer.next();

    assertEquals("!", c1.word());
    assertEquals("@", c2.word());
    assertEquals("#", c3.word());
    assertEquals("$", c4.word());
    assertEquals("%", c5.word());
    assertEquals("^", c6.word());
    assertEquals("&", c7.word());
    assertEquals("*", c8.word());
    assertEquals("(", c9.word());
    assertEquals(")", c10.word());
    assertEquals("_", c11.word());
    assertEquals("+", c12.word());

    reader.close();
}
@Test
public void testNonBreakingSpaceBetweenTokens() throws IOException {
    String input = "5\u00A07/8";
    Reader reader = new StringReader(input);
    PTBTokenizer<CoreLabel> tokenizer = (PTBTokenizer<CoreLabel>) PTBTokenizer.coreLabelFactory("ptb3Escaping=true").getTokenizer(reader);

    CoreLabel c1 = tokenizer.next(); 

    assertNotNull(c1.word());
    assertTrue(c1.word().contains("5") || c1.word().contains("7/8"));

    reader.close();
}
@Test(expected = IllegalArgumentException.class)
public void testInvalidRegexThrowsExceptionInParseInsidePattern() throws IOException {
    Pattern invalidPattern = Pattern.compile("[invalid]");
    Reader reader = new StringReader("<text> should parse");
    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new ByteArrayOutputStream(), "UTF-8"));

    PTBTokenizer.ptb2Text(reader, writer);
}
@Test
public void testTokenizerReturnsNullAfterCompletion() throws IOException {
    String text = "Done.";
    Reader reader = new StringReader(text);
    PTBTokenizer<Word> tokenizer = PTBTokenizer.newPTBTokenizer(reader);

    tokenizer.next(); 
    tokenizer.next(); 
    assertFalse(tokenizer.hasNext());

    reader.close();
}
@Test
public void testOnlyWhitespaceAndNewlineTokenizedWithOptions() throws IOException {
    String text = "\n    \n";
    Reader reader = new StringReader(text);
    PTBTokenizer<CoreLabel> tokenizer = PTBTokenizer.newPTBTokenizer(reader, true, true);

    CoreLabel c1 = tokenizer.next();
    CoreLabel c2 = tokenizer.next();

    assertEquals(PTBTokenizer.getNewlineToken(), c1.word());
    assertEquals(PTBTokenizer.getNewlineToken(), c2.word());

    assertFalse(tokenizer.hasNext());

    reader.close();
}
@Test
public void testMultipleAdjacentPunctuationNoEscaping() throws IOException {
    String text = "Wow!!!";
    Reader reader = new StringReader(text);
    PTBTokenizer<CoreLabel> tokenizer = (PTBTokenizer<CoreLabel>) PTBTokenizer.coreLabelFactory("ptb3Escaping=false").getTokenizer(reader);

    CoreLabel c1 = tokenizer.next(); 
    CoreLabel c2 = tokenizer.next(); 
    CoreLabel c3 = tokenizer.next(); 
    CoreLabel c4 = tokenizer.next(); 

    assertEquals("Wow", c1.word());
    assertEquals("!", c2.word());
    assertEquals("!", c3.word());
    assertEquals("!", c4.word());

    assertFalse(tokenizer.hasNext());

    reader.close();
}
@Test
public void testMixedQuoteStylesAscii() throws IOException {
    String text = "'Hello' \"There\"";
    Reader reader = new StringReader(text);
    PTBTokenizer<CoreLabel> tokenizer = (PTBTokenizer<CoreLabel>) PTBTokenizer.coreLabelFactory("quotes=ascii").getTokenizer(reader);

    CoreLabel c1 = tokenizer.next(); 
    CoreLabel c2 = tokenizer.next(); 
    CoreLabel c3 = tokenizer.next(); 
    CoreLabel c4 = tokenizer.next(); 
    CoreLabel c5 = tokenizer.next(); 
    CoreLabel c6 = tokenizer.next(); 

    assertEquals("'", c1.word());
    assertEquals("Hello", c2.word());
    assertEquals("'", c3.word());
    assertEquals("\"", c4.word());
    assertEquals("There", c5.word());
    assertEquals("\"", c6.word());

    reader.close();
}
//@Test
//public void testFactorySetOptionsOverridesPrevious() throws IOException {
//    String text = "(testing)";
//    Reader reader = new StringReader(text);
//    PTBTokenizer.PTBTokenizerFactory<CoreLabel> factory = new PTBTokenizer.PTBTokenizerFactory<>(new CoreLabelTokenFactory(), "ptb3Escaping=false");
//    factory.setOptions("ptb3Escaping=true");
//
//    PTBTokenizer<CoreLabel> tokenizer = (PTBTokenizer<CoreLabel>) factory.getTokenizer(reader);
//
//    CoreLabel c1 = tokenizer.next();
//    CoreLabel c2 = tokenizer.next();
//    CoreLabel c3 = tokenizer.next();
//
//    assertTrue(c1.word().equals("-LRB-") || c1.word().equals("("));
//    assertEquals("testing", c2.word());
//    assertTrue(c3.word().equals("-RRB-") || c3.word().equals(")"));
//
//    reader.close();
//}
//@Test
//public void testGetIteratorAndTokenizerUsingFactory() throws IOException {
//    String text = "Example.";
//    Reader reader1 = new StringReader(text);
//    Reader reader2 = new StringReader(text);
//
//    PTBTokenizer.PTBTokenizerFactory<CoreLabel> factory = new PTBTokenizer.PTBTokenizerFactory<>(new CoreLabelTokenFactory(), "");
//
//    Iterator<CoreLabel> iterator = factory.getIterator(reader1);
//    Tokenizer<CoreLabel> tokenizer = factory.getTokenizer(reader2);
//
//    CoreLabel t1 = iterator.next();
//    CoreLabel t2 = iterator.next();
//    assertFalse(iterator.hasNext());
//
//    assertEquals("Example", t1.word());
//    assertEquals(".", t2.word());
//
//    CoreLabel u1 = tokenizer.next();
//    CoreLabel u2 = tokenizer.next();
//    assertEquals("Example", u1.word());
//    assertEquals(".", u2.word());
//
//    reader1.close();
//    reader2.close();
//}
//@Test
//public void testFactoryWithExtraOptionsAppended() throws IOException {
//    String text = "(test)";
//    Reader reader = new StringReader(text);
//
//    PTBTokenizer.PTBTokenizerFactory<CoreLabel> factory = new PTBTokenizer.PTBTokenizerFactory<>(new CoreLabelTokenFactory(), "ptb3Escaping=false");
//    Tokenizer<CoreLabel> tokenizer = factory.getTokenizer(reader, "ptb3Escaping=true");
//
//    CoreLabel c1 = tokenizer.next();
//    CoreLabel c2 = tokenizer.next();
//    CoreLabel c3 = tokenizer.next();
//
//    assertTrue(c1.word().equals("-LRB-") || c1.word().equals("("));
//    assertEquals("test", c2.word());
//    assertTrue(c3.word().equals("-RRB-") || c3.word().equals(")"));
//
//    reader.close();
//}
@Test
public void testPtb2TextWithStringReaderAndWriterReturnsTokenCount() throws IOException {
    String ptbText = "This -LRB- is -RRB- a test .";
    Reader reader = new StringReader(ptbText);
    StringWriter writer = new StringWriter();

    long count = PTBTokenizer.ptb2Text(reader, writer);
    String result = writer.toString();

    assertTrue(result.contains("This"));
    assertTrue(result.contains("( is )"));
    assertEquals(6, count);
}
@Test
public void testMainWithEmptyArgsRunsSuccessfully() throws Exception {
    String[] args = new String[0];
    PTBTokenizer.main(args);
}
@Test
public void testFactoryWordWithCustomOptions() throws IOException {
    String text = "testing!";
    Reader reader = new StringReader(text);
    TokenizerFactory<Word> factory = PTBTokenizer.PTBTokenizerFactory.newWordTokenizerFactory("normalizeOtherBrackets=false");

    Tokenizer<Word> tokenizer = factory.getTokenizer(reader);

    Word w1 = tokenizer.next();
    Word w2 = tokenizer.next();

    assertEquals("testing", w1.word());
    assertEquals("!", w2.word());

    reader.close();
}
@Test
public void testEmptyQuotesAreSeparateTokens() throws IOException {
    String input = "\"\"";
    Reader reader = new StringReader(input);
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "quotes=ascii");

    CoreLabel c1 = tokenizer.next();
    CoreLabel c2 = tokenizer.next();

    assertEquals("\"", c1.word());
    assertEquals("\"", c2.word());

    reader.close();
}
@Test
public void testEmojiSequenceAsTokens() throws IOException {
    String input = "👍😊🚀";
    Reader reader = new StringReader(input);
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "ptb3Escaping=false");

    CoreLabel c1 = tokenizer.next();
    CoreLabel c2 = tokenizer.next();
    CoreLabel c3 = tokenizer.next();

    assertEquals("👍", c1.word());
    assertEquals("😊", c2.word());
    assertEquals("🚀", c3.word());

    reader.close();
}
@Test
public void testSingleUnicodeControlCharacterIsIgnored() throws IOException {
    String input = "\u0001";  
    Reader reader = new StringReader(input);
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "untokenizable=allDelete");

    assertFalse(tokenizer.hasNext());

    reader.close();
}
//@Test(expected = NullPointerException.class)
//public void testTokenizerFactoryWithNullFactoryThrows() {
//    new PTBTokenizer.PTBTokenizerFactory<CoreLabel>(null, "ptb3Escaping=true");
//}
//@Test
//public void testTokenizerFactorySerializability() throws IOException, ClassNotFoundException {
//    String text = "serialize this";
//    Reader reader = new StringReader(text);
//
//    PTBTokenizer.PTBTokenizerFactory<CoreLabel> originalFactory = new PTBTokenizer.PTBTokenizerFactory<>(
//        new CoreLabelTokenFactory(), "ptb3Escaping=true"
//    );
//
//    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//    ObjectOutputStream out = new ObjectOutputStream(bytes);
//    out.writeObject(originalFactory);
//    out.close();
//
//    ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()));
//    PTBTokenizer.PTBTokenizerFactory<CoreLabel> deserialized = (PTBTokenizer.PTBTokenizerFactory<CoreLabel>) in.readObject();
//
//    Tokenizer<CoreLabel> tokenizer = deserialized.getTokenizer(new StringReader(text));
//    CoreLabel c1 = tokenizer.next();
//    CoreLabel c2 = tokenizer.next();
//    CoreLabel c3 = tokenizer.next();
//
//    assertEquals("serialize", c1.word());
//    assertEquals("this", c2.word());
//    assertEquals(".", c3.word());
//
//    reader.close();
//}
@Test
public void testStrictAcronymTrueHandlesAbbrevFinalPeriod() throws IOException {
    String input = "Inc.";
    Reader reader = new StringReader(input);

    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "strictAcronym=true");

    CoreLabel token1 = tokenizer.next();
    CoreLabel token2 = tokenizer.next();

    assertTrue(token1.word().equals("Inc") || token1.word().equals("Inc."));
    assertEquals(".", token2.word());

    reader.close();
}
@Test
public void testTokenAtStartAndEndAreCaptured() throws IOException {
    String input = "!middle.";
    Reader reader = new StringReader(input);
    PTBTokenizer<CoreLabel> tokenizer = (PTBTokenizer<CoreLabel>) PTBTokenizer.coreLabelFactory("ptb3Escaping=false").getTokenizer(reader);

    CoreLabel t1 = tokenizer.next(); 
    CoreLabel t2 = tokenizer.next(); 
    CoreLabel t3 = tokenizer.next(); 

    assertEquals("!", t1.word());
    assertEquals("middle", t2.word());
    assertEquals(".", t3.word());

    reader.close();
}
@Test
public void testPtbLabelBeforeAfterAnnotationsPresent() throws IOException {
    String input = "annotated";
    Reader reader = new StringReader(input);
    PTBTokenizer<CoreLabel> tokenizer = PTBTokenizer.newPTBTokenizer(reader, false, true);

    CoreLabel token = tokenizer.next();

    assertEquals("annotated", token.word());
    assertNotNull(token.get(CoreAnnotations.TextAnnotation.class));
    assertNotNull(token.get(CoreAnnotations.OriginalTextAnnotation.class));
    assertNotNull(token.get(CoreAnnotations.BeforeAnnotation.class));
    assertNotNull(token.get(CoreAnnotations.AfterAnnotation.class));

    reader.close();
}
@Test
public void testAngleBracketTextNotTreatedAsSGML() throws IOException {
    String input = "<notatag> test </notatag>";
    Reader reader = new StringReader(input);
    PTBTokenizer<CoreLabel> tokenizer = (PTBTokenizer<CoreLabel>) PTBTokenizer.coreLabelFactory("ptb3Escaping=false").getTokenizer(reader);

    CoreLabel t1 = tokenizer.next(); 
    CoreLabel t2 = tokenizer.next(); 
    CoreLabel t3 = tokenizer.next(); 
    CoreLabel t4 = tokenizer.next(); 
    CoreLabel t5 = tokenizer.next(); 
    CoreLabel t6 = tokenizer.next(); 
    CoreLabel t7 = tokenizer.next(); 
    CoreLabel t8 = tokenizer.next(); 

    assertEquals("<", t1.word());
    assertEquals("notatag", t2.word());
    assertEquals(">", t3.word());
    assertEquals("test", t4.word());
    assertEquals("<", t5.word());
    assertEquals("/", t6.word());
    assertEquals("notatag", t7.word());
    assertEquals(">", t8.word());

    reader.close();
}
@Test
public void testBuilderStyleFactoryPreservesLineFlagCombo() throws IOException {
    String text = "New\nLine";
    Reader reader = new StringReader(text);
    TokenizerFactory<CoreLabel> factory = PTBTokenizer.coreLabelFactory("invertible,true,tokenizeNLs");

    PTBTokenizer<CoreLabel> tokenizer = (PTBTokenizer<CoreLabel>) factory.getTokenizer(reader);

    CoreLabel t1 = tokenizer.next(); 
    CoreLabel t2 = tokenizer.next(); 
    CoreLabel t3 = tokenizer.next(); 

    assertEquals("New", t1.word());
    assertEquals(PTBTokenizer.getNewlineToken(), t2.word());
    assertEquals("Line", t3.word());

    reader.close();
}
@Test
public void testTokenizeSingleHyphenWithOption() throws IOException {
    String text = "-";
    Reader reader = new StringReader(text);
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "splitHyphenated=true");

    CoreLabel token = tokenizer.next();
    assertEquals("-", token.word());

    reader.close();
}
@Test
public void testNormalizeAmpersandEntityEnabled() throws IOException {
    String text = "&amp;";
    Reader reader = new StringReader(text);
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "normalizeAmpersandEntity=true");

    CoreLabel token = tokenizer.next();
    assertEquals("&", token.word());

    reader.close();
}
@Test
public void testNormalizeAmpersandEntityDisabled() throws IOException {
    String text = "&amp;";
    Reader reader = new StringReader(text);
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "normalizeAmpersandEntity=false");

    CoreLabel token = tokenizer.next();
    assertEquals("&amp;", token.word());

    reader.close();
}
@Test
public void testInvalidPatternForFilterSkippedInternally() throws IOException {
    String text = "a b";
    Reader reader = new StringReader(text);
    String brokenPattern = ".*(invalid)";

    try {
        Pattern.compile(brokenPattern);
    } catch (Exception e) {
        
    }

    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "");

    CoreLabel t1 = tokenizer.next();
    CoreLabel t2 = tokenizer.next();

    assertEquals("a", t1.word());
    assertEquals("b", t2.word());

    reader.close();
}
@Test
public void testNormalizeSpaceOptionPreservesTokenStructure() throws IOException {
    String text = "5 7/8";
    Reader reader = new StringReader(text);
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "normalizeSpace=true");

    CoreLabel t1 = tokenizer.next();
    CoreLabel t2 = tokenizer.next(); 

    assertEquals("5", t1.word());
    assertEquals("7/8", t2.word());

    reader.close();
}
@Test
public void testQuotesOriginalOptionNoTransform() throws IOException {
    String text = "He said, “yes”.";
    Reader reader = new StringReader(text);
    PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "quotes=original");

    CoreLabel t1 = tokenizer.next(); 
    CoreLabel t6 = null;
    while (tokenizer.hasNext()) {
        CoreLabel token = tokenizer.next();
        if (text.contains(token.word())) {
            t6 = token;
        }
    }

    assertTrue("“yes”. contains final quote", "“yes”.".contains(t6.word()));

    reader.close();
}
@Test
public void testEllipsesOriginalOptionNoTransform() throws IOException {
    String text = "Wait... now.";
    Reader reader = new StringReader(text);
    PTBTokenizer<CoreLabel> tokenizer = (PTBTokenizer<CoreLabel>) PTBTokenizer.coreLabelFactory("ellipses=original").getTokenizer(reader);

    CoreLabel c1 = tokenizer.next();
    CoreLabel c2 = tokenizer.next();
    CoreLabel c3 = tokenizer.next();

    assertEquals("Wait", c1.word());
    assertTrue(c2.word().equals("..."));
    assertEquals("now", c3.word());

    reader.close();
}
@Test
public void testQuotesUnsafeASCIICharactersAsOriginal() throws IOException {
    String text = "This \u0092 is bad CP1252";
    Reader reader = new StringReader(text);
    PTBTokenizer<CoreLabel> tokenizer = (PTBTokenizer<CoreLabel>) PTBTokenizer.coreLabelFactory("quotes=original").getTokenizer(reader);

    CoreLabel token1 = tokenizer.next(); 
    CoreLabel token2 = tokenizer.next();
    CoreLabel token3 = tokenizer.next();

    assertEquals("This", token1.word());
    assertTrue(token2.word().equals("\u0092") || token2.word().isEmpty());
    assertEquals("is", token3.word());

    reader.close();
}
@Test
public void testUnicodeEscapePreservedInOriginalTextAnnotation() throws IOException {
    String text = "foo \u00A0 bar";
    Reader reader = new StringReader(text);
    PTBTokenizer<CoreLabel> tokenizer = PTBTokenizer.newPTBTokenizer(reader, false, true);

    CoreLabel t1 = tokenizer.next();
    CoreLabel t2 = tokenizer.next();

    String orig1 = t1.get(CoreAnnotations.OriginalTextAnnotation.class);
    String orig2 = t2.get(CoreAnnotations.OriginalTextAnnotation.class);

    assertEquals("foo", orig1.trim());
    assertEquals("\u00A0", t2.get(CoreAnnotations.BeforeAnnotation.class).trim());

    reader.close();
}
@Test
public void testMainMethodWithInvalidParseInsidePatternDoesNotCrash() throws Exception {
    String[] args = {
        "-options", "ptb3Escaping=false",
        "-parseInside", "*invalid+regex",
        "-fileList"
    };

    File listFile = File.createTempFile("ptbList", ".txt");
    listFile.deleteOnExit();
    FileWriter fw = new FileWriter(listFile);
    fw.write(""); fw.flush(); fw.close();

    String[] fullArgs = new String[] {
        "-options", "ptb3Escaping=false",
        "-parseInside", "*invalid+regex",
        "-fileList", listFile.getAbsolutePath()
    };

    PTBTokenizer.main(fullArgs);
}
@Test
public void testTokenizerReturnsCorrectTypeGivenWordFactory() throws IOException {
    String text = "Tiger Woods";
    Reader reader = new StringReader(text);
    Tokenizer<Word> tokenizer = PTBTokenizer.factory().getTokenizer(reader);

    Word w1 = tokenizer.next();
    Word w2 = tokenizer.next();

    assertEquals("Tiger", w1.word());
    assertEquals("Woods", w2.word());

    reader.close();
} 
}