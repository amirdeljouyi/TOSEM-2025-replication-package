package edu.stanford.nlp.process;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.Label;
import edu.stanford.nlp.ling.SentenceUtils;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.international.negra.NegraPennLanguagePack;


public class PTBTokenizer_1_GPTLLMTest {

 @Test
    public void testTokenizationWord() {
        Reader reader = new StringReader("Hello, world! This is a test.");
        PTBTokenizer<Word> tokenizer = PTBTokenizer.newPTBTokenizer(reader);

        List<Word> tokens = new ArrayList<>();
        tokens.add(tokenizer.next());
        tokens.add(tokenizer.next());
        tokens.add(tokenizer.next());
        tokens.add(tokenizer.next());
        tokens.add(tokenizer.next());
        tokens.add(tokenizer.next());
        tokens.add(tokenizer.next());

        assertNotNull(tokens);
        assertEquals(7, tokens.size());
        assertEquals("Hello", tokens.get(0).word());
        assertEquals(",", tokens.get(1).word());
        assertEquals("world", tokens.get(2).word());
        assertEquals("!", tokens.get(3).word());
        assertEquals("This", tokens.get(4).word());
        assertEquals("is", tokens.get(5).word());
        assertEquals("a", tokens.get(6).word());
    }
@Test
    public void testTokenizationCoreLabel() {
        Reader reader = new StringReader("Hello, world! This is a test.");
        PTBTokenizer<CoreLabel> tokenizer = PTBTokenizer.newPTBTokenizer(reader, false, true);

        List<CoreLabel> tokens = new ArrayList<>();
        tokens.add(tokenizer.next());
        tokens.add(tokenizer.next());
        tokens.add(tokenizer.next());
        tokens.add(tokenizer.next());
        tokens.add(tokenizer.next());
        tokens.add(tokenizer.next());
        tokens.add(tokenizer.next());

        assertNotNull(tokens);
        assertEquals(7, tokens.size());
        assertEquals("Hello", tokens.get(0).word());
        assertEquals(",", tokens.get(1).word());
        assertEquals("world", tokens.get(2).word());
        assertEquals("!", tokens.get(3).word());
        assertEquals("This", tokens.get(4).word());
        assertEquals("is", tokens.get(5).word());
        assertEquals("a", tokens.get(6).word());
    }
@Test
    public void testPtb2Text() {
        String ptbText = "Hello , world !";
        String converted = PTBTokenizer.ptb2Text(ptbText);
        assertEquals("Hello, world!", converted);
    }
@Test
    public void testPtbToken2Text() {
        assertEquals("(", PTBTokenizer.ptbToken2Text("-LRB-"));
        assertEquals(")", PTBTokenizer.ptbToken2Text("-RRB-"));
    }
@Test
    public void testLabelList2Text() {
        List<Word> words = new ArrayList<>();
        words.add(new Word("Hello"));
        words.add(new Word(","));
        words.add(new Word("world"));
        words.add(new Word("!"));

        String result = PTBTokenizer.labelList2Text(words);
        assertEquals("Hello, world!", result);
    }
@Test
    public void testGetNewlineToken() {
        assertEquals(AbstractTokenizer.NEWLINE_TOKEN, PTBTokenizer.getNewlineToken());
    }
@Test(expected = RuntimeException.class)
    public void testIOExceptionHandling() {
        Reader reader = new Reader() {
            @Override
            public int read(char[] cbuf, int off, int len) {
                throw new RuntimeException("Simulated IOException");
            }

            @Override
            public void close() {}
        };
        PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "invertible=true");
        tokenizer.getNext(); 
    }
@Test
    public void testFactoryMethods() {
        assertNotNull(PTBTokenizer.factory());
        assertNotNull(PTBTokenizer.coreLabelFactory());
        assertNotNull(PTBTokenizer.coreLabelFactory("invertible=true"));
    }
@Test
    public void testWhitespaceHandling() {
        Reader reader = new StringReader("This  is  a  test.");
        PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "invertible=true");

        List<CoreLabel> tokens = new ArrayList<>();
        tokens.add(tokenizer.next());
        tokens.add(tokenizer.next());
        tokens.add(tokenizer.next());
        tokens.add(tokenizer.next());
        tokens.add(tokenizer.next());
        tokens.add(tokenizer.next());

        assertEquals(6, tokens.size());
        assertEquals("This", tokens.get(0).word());
        assertEquals("test", tokens.get(4).word());
    }
@Test
    public void testEmptyInput() {
        Reader reader = new StringReader("");
        PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "invertible=true");
        assertFalse(tokenizer.hasNext());
    }
@Test
    public void testPtb2TextHandlesEscapedCharacters() {
        String ptbText = "`` Hello '' .";
        assertEquals("\"Hello\".", PTBTokenizer.ptb2Text(ptbText));
    }
@Test
    public void testPtb2TextHandlesComplexCases() {
        String ptbText = "-LRB- testing -RRB- .";
        assertEquals("(testing).", PTBTokenizer.ptb2Text(ptbText));
    }
@Test
    public void testLargeInput() {
        String input = "Test Test Test Test Test Test Test Test Test Test";
        Reader reader = new StringReader(input);
        PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "invertible=true");

        List<CoreLabel> tokens = new ArrayList<>();
        tokens.add(tokenizer.next());
        tokens.add(tokenizer.next());
        tokens.add(tokenizer.next());
        tokens.add(tokenizer.next());
        tokens.add(tokenizer.next());
        tokens.add(tokenizer.next());
        tokens.add(tokenizer.next());
        tokens.add(tokenizer.next());
        tokens.add(tokenizer.next());
        tokens.add(tokenizer.next());

        assertEquals(10, tokens.size());
        assertEquals("Test", tokens.get(0).word());
        assertEquals("Test", tokens.get(9).word());
    }
@Test
    public void testNewPTBTokenizerWithDifferentOptions() {
        Reader reader = new StringReader("Hello world");
        PTBTokenizer<CoreLabel> tokenizer = PTBTokenizer.newPTBTokenizer(reader, true, true);
        assertNotNull(tokenizer);
    }
@Test
    public void testFactoryCreatesTokenizer() {
        PTBTokenizer.PTBTokenizerFactory<CoreLabel> factory = (PTBTokenizer.PTBTokenizerFactory<CoreLabel>) PTBTokenizer.coreLabelFactory("invertible=true");
        Reader reader = new StringReader("Hello world");
        assertNotNull(factory.getTokenizer(reader));
    }
@Test
    public void testPreservingNewlines() {
        Reader reader = new StringReader("This is a test.\nNew line test.");
        PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "tokenizeNLs=true");

        List<CoreLabel> tokens = new ArrayList<>();
        tokens.add(tokenizer.next());
        tokens.add(tokenizer.next());
        tokens.add(tokenizer.next());
        tokens.add(tokenizer.next());
        tokens.add(tokenizer.next());
        tokens.add(tokenizer.next());
        tokens.add(tokenizer.next());
        tokens.add(tokenizer.next());

        assertEquals(8, tokens.size());
        assertTrue(tokens.stream().anyMatch(token -> token.word().equals(AbstractTokenizer.NEWLINE_TOKEN)));
    }
@Test
    public void testSingleWordInput() {
        Reader reader = new StringReader("Hello");
        PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "invertible=true");

        CoreLabel token = tokenizer.next();
        assertNotNull(token);
        assertEquals("Hello", token.word());
        assertFalse(tokenizer.hasNext());
    }
@Test
    public void testSingleCharacterPunctuation() {
        Reader reader = new StringReader("!");
        PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "");

        CoreLabel token = tokenizer.next();
        assertNotNull(token);
        assertEquals("!", token.word());
        assertFalse(tokenizer.hasNext());
    }
@Test
    public void testMultipleSpacesBetweenWords() {
        Reader reader = new StringReader("Hello    world");
        PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "invertible=true");

        CoreLabel token1 = tokenizer.next();
        CoreLabel token2 = tokenizer.next();

        assertEquals("Hello", token1.word());
        assertEquals("world", token2.word());
        assertFalse(tokenizer.hasNext());
    }
@Test
    public void testEmptyStringInput() {
        Reader reader = new StringReader("");
        PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "");

        assertFalse(tokenizer.hasNext());
    }
@Test
    public void testOnlyWhitespaceInput() {
        Reader reader = new StringReader("     ");
        PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "");

        assertFalse(tokenizer.hasNext());
    }
@Test
    public void testMultipleNewlines() {
        Reader reader = new StringReader("Line 1\n\nLine 3");
        PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "tokenizeNLs=true");

        CoreLabel token1 = tokenizer.next();
        CoreLabel token2 = tokenizer.next();
        CoreLabel token3 = tokenizer.next();
        CoreLabel token4 = tokenizer.next();

        assertEquals("Line", token1.word());
        assertEquals("1", token2.word());
        assertEquals(AbstractTokenizer.NEWLINE_TOKEN, token3.word());
        assertEquals("Line", token4.word());
    }
@Test
    public void testEdgeCaseWithDigitsAndSymbols() {
        Reader reader = new StringReader("$100 + 20%");
        PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "");

        List<String> expectedTokens = List.of("$", "100", "+", "20", "%");

        CoreLabel token1 = tokenizer.next();
        CoreLabel token2 = tokenizer.next();
        CoreLabel token3 = tokenizer.next();
        CoreLabel token4 = tokenizer.next();
        CoreLabel token5 = tokenizer.next();

        assertEquals(expectedTokens.get(0), token1.word());
        assertEquals(expectedTokens.get(1), token2.word());
        assertEquals(expectedTokens.get(2), token3.word());
        assertEquals(expectedTokens.get(3), token4.word());
        assertEquals(expectedTokens.get(4), token5.word());

        assertFalse(tokenizer.hasNext());
    }
@Test
    public void testUnicodeCharacters() {
        Reader reader = new StringReader("你好，世界！");
        PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "");

        CoreLabel token1 = tokenizer.next();
        CoreLabel token2 = tokenizer.next();
        CoreLabel token3 = tokenizer.next();

        assertEquals("你好", token1.word());
        assertEquals("，", token2.word());
        assertEquals("世界", token3.word());
    }
@Test(expected = RuntimeException.class)
    public void testInvalidReaderThrowsException() {
        Reader reader = new Reader() {
            @Override
            public int read(char[] cbuf, int off, int len) {
                throw new RuntimeException("Simulated Read Error");
            }

            @Override
            public void close() {
            }
        };
        PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "");
        tokenizer.getNext(); 
    }
@Test
    public void testHyphenatedWords() {
        Reader reader = new StringReader("state-of-the-art");
        PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "splitHyphenated=true");

        CoreLabel token1 = tokenizer.next();
        CoreLabel token2 = tokenizer.next();
        CoreLabel token3 = tokenizer.next();
        CoreLabel token4 = tokenizer.next();
        CoreLabel token5 = tokenizer.next();

        assertEquals("state", token1.word());
        assertEquals("-", token2.word());
        assertEquals("of", token3.word());
        assertEquals("-", token4.word());
        assertEquals("the", token5.word());
    }
@Test
    public void testSentenceWithMixedPunctuation() {
        Reader reader = new StringReader("Hello! Is this a test? Yes, it is.");
        PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "");

        List<String> expectedTokens = List.of("Hello", "!", "Is", "this", "a", "test", "?", "Yes", ",", "it", "is", ".");

        CoreLabel token1 = tokenizer.next();
        CoreLabel token2 = tokenizer.next();
        CoreLabel token3 = tokenizer.next();
        CoreLabel token4 = tokenizer.next();
        CoreLabel token5 = tokenizer.next();
        CoreLabel token6 = tokenizer.next();
        CoreLabel token7 = tokenizer.next();
        CoreLabel token8 = tokenizer.next();
        CoreLabel token9 = tokenizer.next();
        CoreLabel token10 = tokenizer.next();
        CoreLabel token11 = tokenizer.next();
        CoreLabel token12 = tokenizer.next();

        assertEquals(expectedTokens.get(0), token1.word());
        assertEquals(expectedTokens.get(1), token2.word());
        assertEquals(expectedTokens.get(2), token3.word());
        assertEquals(expectedTokens.get(3), token4.word());
        assertEquals(expectedTokens.get(4), token5.word());
        assertEquals(expectedTokens.get(5), token6.word());
        assertEquals(expectedTokens.get(6), token7.word());
        assertEquals(expectedTokens.get(7), token8.word());
        assertEquals(expectedTokens.get(8), token9.word());
        assertEquals(expectedTokens.get(9), token10.word());
        assertEquals(expectedTokens.get(10), token11.word());
        assertEquals(expectedTokens.get(11), token12.word());

        assertFalse(tokenizer.hasNext());
    }
@Test
    public void testAcronymsWithPeriods() {
        Reader reader = new StringReader("U.S.A. is a country.");
        PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "");

        CoreLabel token1 = tokenizer.next();
        CoreLabel token2 = tokenizer.next();
        CoreLabel token3 = tokenizer.next();
        CoreLabel token4 = tokenizer.next();
        CoreLabel token5 = tokenizer.next();
        CoreLabel token6 = tokenizer.next();

        assertEquals("U.S.A.", token1.word());
        assertEquals("is", token2.word());
        assertEquals("a", token3.word());
        assertEquals("country", token4.word());
        assertEquals(".", token5.word());

        assertFalse(tokenizer.hasNext());
    }
@Test
    public void testLongContinuousWord() {
        Reader reader = new StringReader("Supercalifragilisticexpialidocious");
        PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "");

        CoreLabel token = tokenizer.next();

        assertEquals("Supercalifragilisticexpialidocious", token.word());
        assertFalse(tokenizer.hasNext());
    }
@Test
    public void testAdjacentNumbersAndWords() {
        Reader reader = new StringReader("Version 1.0 is stable.");
        PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "");

        CoreLabel token1 = tokenizer.next();
        CoreLabel token2 = tokenizer.next();
        CoreLabel token3 = tokenizer.next();
        CoreLabel token4 = tokenizer.next();
        CoreLabel token5 = tokenizer.next();

        assertEquals("Version", token1.word());
        assertEquals("1.0", token2.word());
        assertEquals("is", token3.word());
        assertEquals("stable", token4.word());
        assertEquals(".", token5.word());
    }
@Test
    public void testMultipleDashes() {
        Reader reader = new StringReader("Well-known words like state-of-the-art should be tokenized.");
        PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "splitHyphenated=true");

        List<String> expectedTokens = List.of("Well", "-", "known", "words", "like", "state", "-", "of", "-", "the", "-", "art", "should", "be", "tokenized", ".");

        CoreLabel token1 = tokenizer.next();
        CoreLabel token2 = tokenizer.next();
        CoreLabel token3 = tokenizer.next();
        CoreLabel token4 = tokenizer.next();
        CoreLabel token5 = tokenizer.next();
        CoreLabel token6 = tokenizer.next();
        CoreLabel token7 = tokenizer.next();
        CoreLabel token8 = tokenizer.next();
        CoreLabel token9 = tokenizer.next();
        CoreLabel token10 = tokenizer.next();
        CoreLabel token11 = tokenizer.next();
        CoreLabel token12 = tokenizer.next();
        CoreLabel token13 = tokenizer.next();
        CoreLabel token14 = tokenizer.next();
        CoreLabel token15 = tokenizer.next();
        CoreLabel token16 = tokenizer.next();

        assertEquals(expectedTokens.get(0), token1.word());
        assertEquals(expectedTokens.get(1), token2.word());
        assertEquals(expectedTokens.get(2), token3.word());
        assertEquals(expectedTokens.get(3), token4.word());
        assertEquals(expectedTokens.get(4), token5.word());
        assertEquals(expectedTokens.get(5), token6.word());
        assertEquals(expectedTokens.get(6), token7.word());
        assertEquals(expectedTokens.get(7), token8.word());
        assertEquals(expectedTokens.get(8), token9.word());
        assertEquals(expectedTokens.get(9), token10.word());
        assertEquals(expectedTokens.get(10), token11.word());
        assertEquals(expectedTokens.get(11), token12.word());
        assertEquals(expectedTokens.get(12), token13.word());
        assertEquals(expectedTokens.get(13), token14.word());
        assertEquals(expectedTokens.get(14), token15.word());
        assertEquals(expectedTokens.get(15), token16.word());

        assertFalse(tokenizer.hasNext());
    }
@Test
    public void testSpecialCharacters() {
        Reader reader = new StringReader("@user, email@example.com, #hashtag!");
        PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "");

        CoreLabel token1 = tokenizer.next();
        CoreLabel token2 = tokenizer.next();
        CoreLabel token3 = tokenizer.next();
        CoreLabel token4 = tokenizer.next();
        CoreLabel token5 = tokenizer.next();
        CoreLabel token6 = tokenizer.next();

        assertEquals("@", token1.word());
        assertEquals("user", token2.word());
        assertEquals(",", token3.word());
        assertEquals("email@example.com", token4.word());
        assertEquals(",", token5.word());
        assertEquals("#", token6.word());
    }
@Test
    public void testNonBreakingSpace() {
        Reader reader = new StringReader("This is a non\u00A0breaking space test.");
        PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "");

        CoreLabel token1 = tokenizer.next();
        CoreLabel token2 = tokenizer.next();
        CoreLabel token3 = tokenizer.next();
        CoreLabel token4 = tokenizer.next();
        CoreLabel token5 = tokenizer.next();
        CoreLabel token6 = tokenizer.next();
        CoreLabel token7 = tokenizer.next();

        assertEquals("This", token1.word());
        assertEquals("is", token2.word());
        assertEquals("a", token3.word());
        assertEquals("non", token4.word());
        assertEquals("\u00A0", token5.word()); 
        assertEquals("breaking", token6.word());
        assertEquals("space", token7.word());
    }
@Test
    public void testRepeatedPunctuation() {
        Reader reader = new StringReader("Hello!!! How are you??");
        PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "");

        CoreLabel token1 = tokenizer.next();
        CoreLabel token2 = tokenizer.next();
        CoreLabel token3 = tokenizer.next();
        CoreLabel token4 = tokenizer.next();
        CoreLabel token5 = tokenizer.next();
        CoreLabel token6 = tokenizer.next();

        assertEquals("Hello", token1.word());
        assertEquals("!", token2.word());
        assertEquals("!", token3.word());
        assertEquals("!", token4.word());
        assertEquals("How", token5.word());
        assertEquals("are", tokenizer.next().word());
        assertEquals("you", tokenizer.next().word());
        assertEquals("?", tokenizer.next().word());
        assertEquals("?", tokenizer.next().word());
    }
@Test
    public void testComplicatedMixedSentence() {
        Reader reader = new StringReader("Well... this isn't easy-to-test, but let's do it!");
        PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "");

        CoreLabel token1 = tokenizer.next();
        CoreLabel token2 = tokenizer.next();
        CoreLabel token3 = tokenizer.next();
        CoreLabel token4 = tokenizer.next();
        CoreLabel token5 = tokenizer.next();
        CoreLabel token6 = tokenizer.next();
        CoreLabel token7 = tokenizer.next();
        CoreLabel token8 = tokenizer.next();
        CoreLabel token9 = tokenizer.next();
        CoreLabel token10 = tokenizer.next();
        CoreLabel token11 = tokenizer.next();
        CoreLabel token12 = tokenizer.next();

        assertEquals("Well", token1.word());
        assertEquals(".", token2.word());
        assertEquals(".", token3.word());
        assertEquals(".", token4.word());
        assertEquals("this", token5.word());
        assertEquals("is", token6.word());
        assertEquals("n't", token7.word());
        assertEquals("easy", token8.word());
        assertEquals("-", token9.word());
        assertEquals("to", token10.word());
        assertEquals("-", token11.word());
        assertEquals("test", tokenizer.next().word());
    }
@Test
    public void testUnicodeEmojisAndSymbols() {
        Reader reader = new StringReader("I ❤️ NLP! 😊");
        PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "");

        CoreLabel token1 = tokenizer.next();
        CoreLabel token2 = tokenizer.next();
        CoreLabel token3 = tokenizer.next();
        CoreLabel token4 = tokenizer.next();

        assertEquals("I", token1.word());
        assertEquals("❤️", token2.word());
        assertEquals("NLP", token3.word());
        assertEquals("!", token4.word());
        assertEquals("😊", tokenizer.next().word());
    }
@Test
    public void testExcessWhitespaceBetweenWords() {
        Reader reader = new StringReader("This    has   extra     spaces.");
        PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "");

        CoreLabel token1 = tokenizer.next();
        CoreLabel token2 = tokenizer.next();
        CoreLabel token3 = tokenizer.next();
        CoreLabel token4 = tokenizer.next();
        CoreLabel token5 = tokenizer.next();

        assertEquals("This", token1.word());
        assertEquals("has", token2.word());
        assertEquals("extra", token3.word());
        assertEquals("spaces", token4.word());
        assertEquals(".", token5.word());
    }
@Test
    public void testEdgeCaseNewlinePreservation() {
        Reader reader = new StringReader("First line\n\nSecond line.");
        PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "tokenizeNLs=true");

        CoreLabel token1 = tokenizer.next();
        CoreLabel token2 = tokenizer.next();
        CoreLabel token3 = tokenizer.next();
        CoreLabel token4 = tokenizer.next();
        CoreLabel token5 = tokenizer.next();
        CoreLabel token6 = tokenizer.next();

        assertEquals("First", token1.word());
        assertEquals("line", token2.word());
        assertEquals(AbstractTokenizer.NEWLINE_TOKEN, token3.word());
        assertEquals(AbstractTokenizer.NEWLINE_TOKEN, token4.word());
        assertEquals("Second", token5.word());
        assertEquals("line", token6.word());
    }
@Test
    public void testContractionSplitting() {
        Reader reader = new StringReader("He's going to tokenize.");
        PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "");

        CoreLabel token1 = tokenizer.next();
        CoreLabel token2 = tokenizer.next();
        CoreLabel token3 = tokenizer.next();
        CoreLabel token4 = tokenizer.next();
        CoreLabel token5 = tokenizer.next();

        assertEquals("He", token1.word());
        assertEquals("'s", token2.word());
        assertEquals("going", token3.word());
        assertEquals("to", token4.word());
        assertEquals("tokenize", token5.word());
    }
@Test
    public void testTokenizationWithSpecialCasesLikeURLs() {
        Reader reader = new StringReader("Check out https://example.com and www.test.org!");
        PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "");

        CoreLabel token1 = tokenizer.next();
        CoreLabel token2 = tokenizer.next();
        CoreLabel token3 = tokenizer.next();
        CoreLabel token4 = tokenizer.next();
        CoreLabel token5 = tokenizer.next();

        assertEquals("Check", token1.word());
        assertEquals("out", token2.word());
        assertEquals("https://example.com", token3.word());
        assertEquals("and", token4.word());
        assertEquals("www.test.org", token5.word());
    }
@Test
    public void testParenthesesAndBrackets() {
        Reader reader = new StringReader("(This is a [test] case).");
        PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "");

        CoreLabel token1 = tokenizer.next();
        CoreLabel token2 = tokenizer.next();
        CoreLabel token3 = tokenizer.next();
        CoreLabel token4 = tokenizer.next();
        CoreLabel token5 = tokenizer.next();
        CoreLabel token6 = tokenizer.next();
        CoreLabel token7 = tokenizer.next();
        CoreLabel token8 = tokenizer.next();

        assertEquals("(", token1.word());
        assertEquals("This", token2.word());
        assertEquals("is", token3.word());
        assertEquals("a", token4.word());
        assertEquals("[", token5.word());
        assertEquals("test", token6.word());
        assertEquals("]", token7.word());
        assertEquals("case", tokenizer.next().word());
    }
@Test
    public void testComplexNumbersAndFractions() {
        Reader reader = new StringReader("He ran 5 3/4 miles in 2:45.5!");
        PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "");

        CoreLabel token1 = tokenizer.next();
        CoreLabel token2 = tokenizer.next();
        CoreLabel token3 = tokenizer.next();
        CoreLabel token4 = tokenizer.next();
        CoreLabel token5 = tokenizer.next();
        CoreLabel token6 = tokenizer.next();
        CoreLabel token7 = tokenizer.next();
        CoreLabel token8 = tokenizer.next();

        assertEquals("He", token1.word());
        assertEquals("ran", token2.word());
        assertEquals("5", token3.word());
        assertEquals("3/4", token4.word());
        assertEquals("miles", token5.word());
        assertEquals("in", token6.word());
        assertEquals("2:45.5", token7.word());
        assertEquals("!", tokenizer.next().word());
    }
@Test
    public void testRepeatedPeriodsEllipsis() {
        Reader reader = new StringReader("Hello... How are you?");
        PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "ellipses=ptb3");

        CoreLabel token1 = tokenizer.next();
        CoreLabel token2 = tokenizer.next();
        CoreLabel token3 = tokenizer.next();
        CoreLabel token4 = tokenizer.next();
        CoreLabel token5 = tokenizer.next();

        assertEquals("Hello", token1.word());
        assertEquals("...", token2.word());
        assertEquals("How", token3.word());
        assertEquals("are", token4.word());
        assertEquals("you", token5.word());
    }
@Test
    public void testUnusualHyphenUsage() {
        Reader reader = new StringReader("T-shirt is different from co-op.");
        PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "splitHyphenated=true");

        CoreLabel token1 = tokenizer.next();
        CoreLabel token2 = tokenizer.next();
        CoreLabel token3 = tokenizer.next();
        CoreLabel token4 = tokenizer.next();
        CoreLabel token5 = tokenizer.next();
        CoreLabel token6 = tokenizer.next();
        CoreLabel token7 = tokenizer.next();
        CoreLabel token8 = tokenizer.next();

        assertEquals("T", token1.word());
        assertEquals("-", token2.word());
        assertEquals("shirt", token3.word());
        assertEquals("is", token4.word());
        assertEquals("different", token5.word());
        assertEquals("from", token6.word());
        assertEquals("co", token7.word());
        assertEquals("-", tokenizer.next().word());
    }
@Test
    public void testUnicodePunctuation() {
        Reader reader = new StringReader("“Hello” ‘world’ — good day!");
        PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "quotes=unicode,dashes=unicode");

        CoreLabel token1 = tokenizer.next();
        CoreLabel token2 = tokenizer.next();
        CoreLabel token3 = tokenizer.next();
        CoreLabel token4 = tokenizer.next();
        CoreLabel token5 = tokenizer.next();
        CoreLabel token6 = tokenizer.next();
        CoreLabel token7 = tokenizer.next();
        CoreLabel token8 = tokenizer.next();
        CoreLabel token9 = tokenizer.next();

        assertEquals("“", token1.word());
        assertEquals("Hello", token2.word());
        assertEquals("”", token3.word());
        assertEquals("‘", token4.word());
        assertEquals("world", token5.word());
        assertEquals("’", token6.word());
        assertEquals("—", token7.word());
        assertEquals("good", token8.word());
        assertEquals("day", tokenizer.next().word());
    }
@Test
    public void testMultipleContractions() {
        Reader reader = new StringReader("He'll've done it by now.");
        PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "");

        CoreLabel token1 = tokenizer.next();
        CoreLabel token2 = tokenizer.next();
        CoreLabel token3 = tokenizer.next();
        CoreLabel token4 = tokenizer.next();
        CoreLabel token5 = tokenizer.next();
        CoreLabel token6 = tokenizer.next();

        assertEquals("He", token1.word());
        assertEquals("'ll", token2.word());
        assertEquals("'ve", token3.word());
        assertEquals("done", token4.word());
        assertEquals("it", token5.word());
        assertEquals("by", token6.word());
    }
@Test
    public void testEmailTokenization() {
        Reader reader = new StringReader("Contact us at support@example.com.");
        PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "");

        CoreLabel token1 = tokenizer.next();
        CoreLabel token2 = tokenizer.next();
        CoreLabel token3 = tokenizer.next();
        CoreLabel token4 = tokenizer.next();
        CoreLabel token5 = tokenizer.next();
        CoreLabel token6 = tokenizer.next();

        assertEquals("Contact", token1.word());
        assertEquals("us", token2.word());
        assertEquals("at", token3.word());
        assertEquals("support@example.com", token4.word());
        assertEquals(".", token5.word());
    }
@Test
    public void testCryptoAddresses() {
        Reader reader = new StringReader("Bitcoin address: 1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa.");
        PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "");

        CoreLabel token1 = tokenizer.next();
        CoreLabel token2 = tokenizer.next();
        CoreLabel token3 = tokenizer.next();
        CoreLabel token4 = tokenizer.next();
        CoreLabel token5 = tokenizer.next();

        assertEquals("Bitcoin", token1.word());
        assertEquals("address", token2.word());
        assertEquals(":", token3.word());
        assertEquals("1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa", token4.word());
        assertEquals(".", token5.word());
    }
@Test
    public void testUnicodeMathematicalCharacters() {
        Reader reader = new StringReader("The equation a ≠ b + c² holds true.");
        PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "");

        CoreLabel token1 = tokenizer.next();
        CoreLabel token2 = tokenizer.next();
        CoreLabel token3 = tokenizer.next();
        CoreLabel token4 = tokenizer.next();
        CoreLabel token5 = tokenizer.next();
        CoreLabel token6 = tokenizer.next();
        CoreLabel token7 = tokenizer.next();
        CoreLabel token8 = tokenizer.next();

        assertEquals("The", token1.word());
        assertEquals("equation", token2.word());
        assertEquals("a", token3.word());
        assertEquals("≠", token4.word());
        assertEquals("b", token5.word());
        assertEquals("+", token6.word());
        assertEquals("c²", token7.word());
        assertEquals("holds", token8.word());
    }
@Test
    public void testEscapeForwardSlashAsteriskOption() {
        Reader reader = new StringReader("Use / and * carefully.");
        PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "escapeForwardSlashAsterisk=true");

        CoreLabel token1 = tokenizer.next();
        CoreLabel token2 = tokenizer.next();
        CoreLabel token3 = tokenizer.next();
        CoreLabel token4 = tokenizer.next();
        CoreLabel token5 = tokenizer.next();
        CoreLabel token6 = tokenizer.next();

        assertEquals("Use", token1.word());
        assertEquals("\\/", token2.word());
        assertEquals("and", token3.word());
        assertEquals("\\*", token4.word());
        assertEquals("carefully", token5.word());
    }
@Test
    public void testCurrencySymbolsWithoutSpaces() {
        Reader reader = new StringReader("The price is $10.99 or €9.50.");
        PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "normalizeCurrency=true");

        CoreLabel token1 = tokenizer.next();
        CoreLabel token2 = tokenizer.next();
        CoreLabel token3 = tokenizer.next();
        CoreLabel token4 = tokenizer.next();
        CoreLabel token5 = tokenizer.next();
        CoreLabel token6 = tokenizer.next();
        CoreLabel token7 = tokenizer.next();

        assertEquals("The", token1.word());
        assertEquals("price", token2.word());
        assertEquals("is", token3.word());
        assertEquals("$", token4.word());
        assertEquals("10.99", token5.word());
        assertEquals("or", token6.word());
        assertEquals("€", token7.word());
    }
@Test
    public void testTokenizationOfTabsAndWhitespace() {
        Reader reader = new StringReader("Word\t\tanother\tword  more   spaces");
        PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "");

        CoreLabel token1 = tokenizer.next();
        CoreLabel token2 = tokenizer.next();
        CoreLabel token3 = tokenizer.next();
        CoreLabel token4 = tokenizer.next();
        CoreLabel token5 = tokenizer.next();
        CoreLabel token6 = tokenizer.next();

        assertEquals("Word", token1.word());
        assertEquals("another", token2.word());
        assertEquals("word", token3.word());
        assertEquals("more", token4.word());
        assertEquals("spaces", token5.word());
        assertFalse(tokenizer.hasNext());
    }
@Test
    public void testTokenizationOfHTMLTags() {
        Reader reader = new StringReader("<html> This is <b>bold</b> text.");
        PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "");

        CoreLabel token1 = tokenizer.next();
        CoreLabel token2 = tokenizer.next();
        CoreLabel token3 = tokenizer.next();
        CoreLabel token4 = tokenizer.next();
        CoreLabel token5 = tokenizer.next();
        CoreLabel token6 = tokenizer.next();
        CoreLabel token7 = tokenizer.next();
        CoreLabel token8 = tokenizer.next();
        CoreLabel token9 = tokenizer.next();

        assertEquals("<html>", token1.word());
        assertEquals("This", token2.word());
        assertEquals("is", token3.word());
        assertEquals("<b>", token4.word());
        assertEquals("bold", token5.word());
        assertEquals("</b>", token6.word());
        assertEquals("text", token7.word());
        assertEquals(".", token8.word());
        assertFalse(tokenizer.hasNext());
    }
@Test
    public void testTokenizationOfMathematicalSymbols() {
        Reader reader = new StringReader("5 × 3 = 15 and a ≠ b.");
        PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "");

        CoreLabel token1 = tokenizer.next();
        CoreLabel token2 = tokenizer.next();
        CoreLabel token3 = tokenizer.next();
        CoreLabel token4 = tokenizer.next();
        CoreLabel token5 = tokenizer.next();
        CoreLabel token6 = tokenizer.next();
        CoreLabel token7 = tokenizer.next();
        CoreLabel token8 = tokenizer.next();
        CoreLabel token9 = tokenizer.next();
        CoreLabel token10 = tokenizer.next();

        assertEquals("5", token1.word());
        assertEquals("×", token2.word());
        assertEquals("3", token3.word());
        assertEquals("=", token4.word());
        assertEquals("15", token5.word());
        assertEquals("and", token6.word());
        assertEquals("a", token7.word());
        assertEquals("≠", token8.word());
        assertEquals("b", token9.word());
        assertEquals(".", token10.word());
        assertFalse(tokenizer.hasNext());
    }
@Test
    public void testTokenizationWithSpecialQuotes() {
        Reader reader = new StringReader("He said “Hello” to her.");
        PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "quotes=unicode");

        CoreLabel token1 = tokenizer.next();
        CoreLabel token2 = tokenizer.next();
        CoreLabel token3 = tokenizer.next();
        CoreLabel token4 = tokenizer.next();
        CoreLabel token5 = tokenizer.next();
        CoreLabel token6 = tokenizer.next();
        CoreLabel token7 = tokenizer.next();

        assertEquals("He", token1.word());
        assertEquals("said", token2.word());
        assertEquals("“", token3.word());
        assertEquals("Hello", token4.word());
        assertEquals("”", token5.word());
        assertEquals("to", token6.word());
        assertEquals("her", token7.word());
    }
@Test
    public void testTokenizationOfScientificNotation() {
        Reader reader = new StringReader("The speed of light is 3.0e8 m/s.");
        PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "");

        CoreLabel token1 = tokenizer.next();
        CoreLabel token2 = tokenizer.next();
        CoreLabel token3 = tokenizer.next();
        CoreLabel token4 = tokenizer.next();
        CoreLabel token5 = tokenizer.next();
        CoreLabel token6 = tokenizer.next();
        CoreLabel token7 = tokenizer.next();
        CoreLabel token8 = tokenizer.next();

        assertEquals("The", token1.word());
        assertEquals("speed", token2.word());
        assertEquals("of", token3.word());
        assertEquals("light", token4.word());
        assertEquals("is", token5.word());
        assertEquals("3.0e8", token6.word());
        assertEquals("m/s", token7.word());
        assertEquals(".", token8.word());
    }
@Test
    public void testTokenizationOfRepeatedCharacters() {
        Reader reader = new StringReader("Hmmm... Really????");
        PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "ellipses=ptb3");

        CoreLabel token1 = tokenizer.next();
        CoreLabel token2 = tokenizer.next();
        CoreLabel token3 = tokenizer.next();
        CoreLabel token4 = tokenizer.next();
        CoreLabel token5 = tokenizer.next();

        assertEquals("Hmmm", token1.word());
        assertEquals("...", token2.word());
        assertEquals("Really", token3.word());
        assertEquals("?", token4.word());
        assertEquals("?", token5.word());
    }
@Test
    public void testTokenizationOfCurrencySymbolsInsideText() {
        Reader reader = new StringReader("The total cost is $20 and €15.");
        PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "normalizeCurrency=true");

        CoreLabel token1 = tokenizer.next();
        CoreLabel token2 = tokenizer.next();
        CoreLabel token3 = tokenizer.next();
        CoreLabel token4 = tokenizer.next();
        CoreLabel token5 = tokenizer.next();
        CoreLabel token6 = tokenizer.next();
        CoreLabel token7 = tokenizer.next();

        assertEquals("The", token1.word());
        assertEquals("total", token2.word());
        assertEquals("cost", token3.word());
        assertEquals("is", token4.word());
        assertEquals("$", token5.word());
        assertEquals("20", token6.word());
        assertEquals("and", token7.word());
    }
@Test
    public void testTokenizationOfMixedDotUsage() {
        Reader reader = new StringReader("Mr. Smith went to Washington U.S.A.");
        PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "strictAcronym=false");

        CoreLabel token1 = tokenizer.next();
        CoreLabel token2 = tokenizer.next();
        CoreLabel token3 = tokenizer.next();
        CoreLabel token4 = tokenizer.next();
        CoreLabel token5 = tokenizer.next();
        CoreLabel token6 = tokenizer.next();

        assertEquals("Mr.", token1.word());
        assertEquals("Smith", token2.word());
        assertEquals("went", token3.word());
        assertEquals("to", token4.word());
        assertEquals("Washington", token5.word());
        assertEquals("U.S.A.", token6.word());
    }
@Test
    public void testTokenizationWithMixedLatinAndNonLatin() {
        Reader reader = new StringReader("Mi casa es su casa. 加油!");
        PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "");

        CoreLabel token1 = tokenizer.next();
        CoreLabel token2 = tokenizer.next();
        CoreLabel token3 = tokenizer.next();
        CoreLabel token4 = tokenizer.next();
        CoreLabel token5 = tokenizer.next();
        CoreLabel token6 = tokenizer.next();
        CoreLabel token7 = tokenizer.next();

        assertEquals("Mi", token1.word());
        assertEquals("casa", token2.word());
        assertEquals("es", token3.word());
        assertEquals("su", token4.word());
        assertEquals("casa", token5.word());
        assertEquals(".", token6.word());
        assertEquals("加油", token7.word());
    }
@Test
    public void testTokenizationOfMultipleConsecutiveNewlines() {
        Reader reader = new StringReader("First line\n\n\nSecond line.\n\nThird line.");
        PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "tokenizeNLs=true");

        CoreLabel token1 = tokenizer.next();
        CoreLabel token2 = tokenizer.next();
        CoreLabel token3 = tokenizer.next();
        CoreLabel token4 = tokenizer.next();
        CoreLabel token5 = tokenizer.next();
        CoreLabel token6 = tokenizer.next();
        CoreLabel token7 = tokenizer.next();

        assertEquals("First", token1.word());
        assertEquals("line", token2.word());
        assertEquals(AbstractTokenizer.NEWLINE_TOKEN, token3.word());
        assertEquals(AbstractTokenizer.NEWLINE_TOKEN, token4.word());
        assertEquals("Second", token5.word());
        assertEquals("line", token6.word());
        assertEquals(AbstractTokenizer.NEWLINE_TOKEN, tokenizer.next().word());
    }
@Test
    public void testAbbreviationsWithPeriods() {
        Reader reader = new StringReader("Dr. Smith went to Washington D.C.");
        PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "strictAcronym=false");

        CoreLabel token1 = tokenizer.next();
        CoreLabel token2 = tokenizer.next();
        CoreLabel token3 = tokenizer.next();
        CoreLabel token4 = tokenizer.next();
        CoreLabel token5 = tokenizer.next();
        CoreLabel token6 = tokenizer.next();

        assertEquals("Dr.", token1.word());
        assertEquals("Smith", token2.word());
        assertEquals("went", token3.word());
        assertEquals("to", token4.word());
        assertEquals("Washington", token5.word());
        assertEquals("D.C.", token6.word());
        assertFalse(tokenizer.hasNext());
    }
@Test
    public void testTokenizationWithSpecialSlashes() {
        Reader reader = new StringReader("Please read the T&Cs / FAQ / About Us page.");
        PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "splitForwardSlash=true");

        CoreLabel token1 = tokenizer.next();
        CoreLabel token2 = tokenizer.next();
        CoreLabel token3 = tokenizer.next();
        CoreLabel token4 = tokenizer.next();
        CoreLabel token5 = tokenizer.next();
        CoreLabel token6 = tokenizer.next();
        CoreLabel token7 = tokenizer.next();
        CoreLabel token8 = tokenizer.next();
        CoreLabel token9 = tokenizer.next();
        CoreLabel token10 = tokenizer.next();

        assertEquals("Please", token1.word());
        assertEquals("read", token2.word());
        assertEquals("the", token3.word());
        assertEquals("T&Cs", token4.word());
        assertEquals("/", token5.word());
        assertEquals("FAQ", token6.word());
        assertEquals("/", token7.word());
        assertEquals("About", token8.word());
        assertEquals("Us", token9.word());
        assertEquals("page", token10.word());
        assertEquals(".", tokenizer.next().word());
    }
@Test
    public void testTokenizationWithFilePaths() {
        Reader reader = new StringReader("The file is at /home/user/docs/file.txt.");
        PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "splitForwardSlash=false");

        CoreLabel token1 = tokenizer.next();
        CoreLabel token2 = tokenizer.next();
        CoreLabel token3 = tokenizer.next();
        CoreLabel token4 = tokenizer.next();
        CoreLabel token5 = tokenizer.next();
        CoreLabel token6 = tokenizer.next();

        assertEquals("The", token1.word());
        assertEquals("file", token2.word());
        assertEquals("is", token3.word());
        assertEquals("at", token4.word());
        assertEquals("/home/user/docs/file.txt", token5.word());
    }
@Test
    public void testTokenizationWithBulletPoints() {
        Reader reader = new StringReader("• First point\n• Second point\n• Third point.");
        PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "tokenizeNLs=true");

        CoreLabel token1 = tokenizer.next();
        CoreLabel token2 = tokenizer.next();
        CoreLabel token3 = tokenizer.next();
        CoreLabel token4 = tokenizer.next();
        CoreLabel token5 = tokenizer.next();
        CoreLabel token6 = tokenizer.next();
        CoreLabel token7 = tokenizer.next();
        CoreLabel token8 = tokenizer.next();
        CoreLabel token9 = tokenizer.next();
        CoreLabel token10 = tokenizer.next();

        assertEquals("•", token1.word());
        assertEquals("First", token2.word());
        assertEquals("point", token3.word());
        assertEquals(AbstractTokenizer.NEWLINE_TOKEN, token4.word());
        assertEquals("•", token5.word());
        assertEquals("Second", token6.word());
        assertEquals("point", token7.word());
        assertEquals(AbstractTokenizer.NEWLINE_TOKEN, token8.word());
        assertEquals("•", token9.word());
        assertEquals("Third", tokenizer.next().word());
    }
@Test
    public void testTokenizationWithMixedLetterCase() {
        Reader reader = new StringReader("iPhone, eBay, and McDonald's are popular brands.");
        PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "lowerCase=true");

        CoreLabel token1 = tokenizer.next();
        CoreLabel token2 = tokenizer.next();
        CoreLabel token3 = tokenizer.next();
        CoreLabel token4 = tokenizer.next();
        CoreLabel token5 = tokenizer.next();
        CoreLabel token6 = tokenizer.next();
        CoreLabel token7 = tokenizer.next();

        assertEquals("iphone", token1.word());
        assertEquals(",", token2.word());
        assertEquals("ebay", token3.word());
        assertEquals(",", token4.word());
        assertEquals("and", token5.word());
        assertEquals("mcdonald's", token6.word());
    }
@Test
    public void testTwitterMentionsAndHashtags() {
        Reader reader = new StringReader("Follow @user1 and @user2. Trending: #coding and #AI.");
        PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "");

        CoreLabel token1 = tokenizer.next();
        CoreLabel token2 = tokenizer.next();
        CoreLabel token3 = tokenizer.next();
        CoreLabel token4 = tokenizer.next();
        CoreLabel token5 = tokenizer.next();
        CoreLabel token6 = tokenizer.next();
        CoreLabel token7 = tokenizer.next();
        CoreLabel token8 = tokenizer.next();
        CoreLabel token9 = tokenizer.next();
        CoreLabel token10 = tokenizer.next();
        CoreLabel token11 = tokenizer.next();

        assertEquals("Follow", token1.word());
        assertEquals("@", token2.word());
        assertEquals("user1", token3.word());
        assertEquals("and", token4.word());
        assertEquals("@", token5.word());
        assertEquals("user2", token6.word());
        assertEquals(".", token7.word());
        assertEquals("Trending", token8.word());
        assertEquals(":", token9.word());
        assertEquals("#", token10.word());
        assertEquals("coding", tokenizer.next().word());
    }
@Test(expected = RuntimeException.class)
    public void testInvalidInputHandling() {
        Reader reader = new Reader() {
            @Override
            public int read(char[] cbuf, int off, int len) {
                throw new RuntimeException("Simulated Read Error");
            }

            @Override
            public void close() {}
        };
        PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "");
        tokenizer.getNext(); 
    } 
}