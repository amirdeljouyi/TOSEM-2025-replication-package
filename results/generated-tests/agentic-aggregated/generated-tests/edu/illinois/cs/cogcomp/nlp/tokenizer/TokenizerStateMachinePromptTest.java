package edu.illinois.cs.cogcomp.nlp.tokenizer;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Test;

import edu.illinois.cs.cogcomp.core.datastructures.IntPair;
import edu.illinois.cs.cogcomp.core.datastructures.Pair;
import edu.illinois.cs.cogcomp.nlp.tokenizer.Tokenizer.Tokenization;

public class TokenizerStateMachinePromptTest {

    @Test
    public void constructorCreatesMachinesForEveryConfiguration() {
        TokenizerStateMachine defaultStyle = new TokenizerStateMachine(true, false);
        TokenizerStateMachine preserveDashAndSplitParagraphs =
                new TokenizerStateMachine(false, true);

        assertNotNull(defaultStyle);
        assertNotNull(preserveDashAndSplitParagraphs);
    }

    @Test
    public void tokenizesWordsPunctuationAndExactOffsets() {
        StatefulTokenizer tokenizer = new StatefulTokenizer(true, false);
        Pair<String[], IntPair[]> result = tokenizer.tokenizeSentence("Hello, world!");

        assertArrayEquals(new String[] {"Hello", ",", "world", "!"}, result.getFirst());
        assertArrayEquals(new IntPair[] {new IntPair(0, 5), new IntPair(5, 6),
                new IntPair(7, 12), new IntPair(12, 13)}, result.getSecond());
    }

    @Test
    public void emptyAndWhitespaceOnlyInputsProduceNoTokens() {
        StatefulTokenizer tokenizer = new StatefulTokenizer(true, false);
        Pair<String[], IntPair[]> empty = tokenizer.tokenizeSentence("");
        Pair<String[], IntPair[]> whitespace = tokenizer.tokenizeSentence(" \t\r\n");

        assertArrayEquals(new String[0], empty.getFirst());
        assertArrayEquals(new IntPair[0], empty.getSecond());
        assertArrayEquals(new String[0], whitespace.getFirst());
        assertArrayEquals(new IntPair[0], whitespace.getSecond());
    }

    @Test
    public void splitOnDashSeparatesAlphabeticButNotNumericRanges() {
        StatefulTokenizer tokenizer = new StatefulTokenizer(true, false);
        Pair<String[], IntPair[]> result = tokenizer.tokenizeSentence("well-known 10-20 A-1");

        assertArrayEquals(new String[] {"well", "-", "known", "10-20", "A", "-", "1"},
                result.getFirst());
    }

    @Test
    public void preserveDashKeepsCompoundWordsTogether() {
        StatefulTokenizer tokenizer = new StatefulTokenizer(false, false);
        Pair<String[], IntPair[]> result = tokenizer.tokenizeSentence("well-known A-1 10-20");

        assertArrayEquals(new String[] {"well-known", "A-1", "10-20"}, result.getFirst());
    }

    @Test
    public void separatesCommonContractionSuffixes() {
        StatefulTokenizer tokenizer = new StatefulTokenizer(true, false);
        Pair<String[], IntPair[]> result =
                tokenizer.tokenizeSentence("it's I'm we'd they're I've she'll can't");

        assertArrayEquals(new String[] {"it", "'s", "I", "'m", "we", "'d", "they", "'re",
                "I", "'ve", "she", "'ll", "ca", "n't"}, result.getFirst());
    }

    @Test
    public void preservesOrdinaryApostrophesAndDroppedGForms() {
        StatefulTokenizer tokenizer = new StatefulTokenizer(true, false);
        Pair<String[], IntPair[]> result =
                tokenizer.tokenizeSentence("O'Brien rock'n'roll goin' Goin'");

        assertArrayEquals(new String[] {"O'Brien", "rock'n'roll", "goin'", "Goin", "'"},
                result.getFirst());
    }

    @Test
    public void recognizesContractionBeginningWithApostrophe() {
        StatefulTokenizer tokenizer = new StatefulTokenizer(true, false);
        Pair<String[], IntPair[]> result = tokenizer.tokenizeSentence("'Tis the season 'xyz");

        assertArrayEquals(new String[] {"'", "Tis", "the", "season", "'", "xyz"},
                result.getFirst());
    }

    @Test
    public void keepsFormattedNumbersAndSplitsTrailingComma() {
        StatefulTokenizer tokenizer = new StatefulTokenizer(true, false);
        Pair<String[], IntPair[]> result =
                tokenizer.tokenizeSentence("1,234,567 3.1415 99, apples");

        assertArrayEquals(new String[] {"1,234,567", "3.1415", "99", ",", "apples"},
                result.getFirst());
    }

    @Test
    public void handlesLeadingDecimalAndCurrencyAmounts() {
        StatefulTokenizer tokenizer = new StatefulTokenizer(true, false);
        Pair<String[], IntPair[]> result =
                tokenizer.tokenizeSentence(".75 $5.00 $.50 $ value");

        assertArrayEquals(new String[] {".75", "$5.00", "$.50", "$", "value"},
                result.getFirst());
    }

    @Test
    public void keepsCompleteNumericDatesButSplitsOtherSlashes() {
        StatefulTokenizer tokenizer = new StatefulTokenizer(true, false);
        Pair<String[], IntPair[]> result =
                tokenizer.tokenizeSentence("12/31/2025 path/to 12/abc");

        assertArrayEquals(new String[] {"12/31/2025", "path", "/", "to", "12", "/", "abc"},
                result.getFirst());
    }

    @Test
    public void keepsTimeColonButSplitsLabelColon() {
        StatefulTokenizer tokenizer = new StatefulTokenizer(true, false);
        Pair<String[], IntPair[]> result = tokenizer.tokenizeSentence("Meet 10:30 label:value end:");

        assertArrayEquals(new String[] {"Meet", "10:30", "label:value", "end", ":"},
                result.getFirst());
    }

    @Test
    public void recognizesUrlsAsSingleTokens() {
        StatefulTokenizer tokenizer = new StatefulTokenizer(true, false);
        Pair<String[], IntPair[]> result = tokenizer.tokenizeSentence(
                "Visit http://example.com/a?q=1 and https://host.org now.");

        assertArrayEquals(new String[] {"Visit", "http://example.com/a?q=1", "and",
                "https://host.org", "now", "."}, result.getFirst());
    }

    @Test
    public void recognizesEmailAndSplitsInvalidAtSign() {
        StatefulTokenizer tokenizer = new StatefulTokenizer(true, false);
        Pair<String[], IntPair[]> result = tokenizer.tokenizeSentence(
                "a.b+tag@example-domain.com user@localhost bad@ address");

        assertArrayEquals(new String[] {"a.b", "+", "tag@example-domain.com", "user@localhost",
                "bad", "@", "address"}, result.getFirst());
    }

    @Test
    public void groupsRepeatedSpecialCharactersButSeparatesDifferentOnes() {
        StatefulTokenizer tokenizer = new StatefulTokenizer(true, false);
        Pair<String[], IntPair[]> result = tokenizer.tokenizeSentence("What?! wow... == ++");

        assertArrayEquals(new String[] {"What", "?", "!", "wow", "...", "==", "++"},
                result.getFirst());
    }

    @Test
    public void sentenceTokenizerFindsOrdinarySentenceEnds() {
        StatefulTokenizer tokenizer = new StatefulTokenizer(true, false);
        Tokenization result = tokenizer.tokenizeTextSpan("One. Two! Three?");

        assertArrayEquals(new String[] {"One", ".", "Two", "!", "Three", "?"},
                result.getTokens());
        assertArrayEquals(new int[] {2, 4, 6}, result.getSentenceEndTokenIndexes());
    }

    @Test
    public void ellipsisContinuesCurrentSentence() {
        StatefulTokenizer tokenizer = new StatefulTokenizer(true, false);
        Tokenization result = tokenizer.tokenizeTextSpan("Wait... still here. Done.");

        assertArrayEquals(new String[] {"Wait", "...", "still", "here", ".", "Done", "."},
                result.getTokens());
        assertArrayEquals(new int[] {5, 7}, result.getSentenceEndTokenIndexes());
    }

    @Test
    public void punctuationInsideClosingQuoteEndsSentence() {
        StatefulTokenizer tokenizer = new StatefulTokenizer(true, false);
        Tokenization result = tokenizer.tokenizeTextSpan("He said \"go.\" Next one.");

        assertArrayEquals(new String[] {"He", "said", "\"", "go", ".", "\"", "Next", "one", "."},
                result.getTokens());
        assertArrayEquals(new int[] {6, 9}, result.getSentenceEndTokenIndexes());
    }

    @Test
    public void knownAbbreviationDoesNotEndSentence() {
        StatefulTokenizer tokenizer = new StatefulTokenizer(true, false);
        Tokenization result = tokenizer.tokenizeTextSpan("Dr. smith arrived. Next.");

        assertArrayEquals(new String[] {"Dr.", "smith", "arrived", ".", "Next", "."},
                result.getTokens());
        assertArrayEquals(new int[] {4, 6}, result.getSentenceEndTokenIndexes());
    }

    @Test
    public void doubleNewlineOptionCreatesParagraphBoundary() {
        StatefulTokenizer tokenizer = new StatefulTokenizer(true, true);
        Tokenization result = tokenizer.tokenizeTextSpan("First line\n\nSecond line");

        assertArrayEquals(new String[] {"First", "line", "Second", "line"}, result.getTokens());
        assertArrayEquals(new int[] {2, 4}, result.getSentenceEndTokenIndexes());
    }

    @Test
    public void doubleNewlineDoesNotSplitWhenOptionDisabled() {
        StatefulTokenizer tokenizer = new StatefulTokenizer(true, false);
        Tokenization result = tokenizer.tokenizeTextSpan("First line\n\nSecond line");

        assertArrayEquals(new String[] {"First", "line", "Second", "line"}, result.getTokens());
        assertArrayEquals(new int[] {4}, result.getSentenceEndTokenIndexes());
    }

    @Test
    public void threeNewlinesCreateOnlyOneBoundary() {
        StatefulTokenizer tokenizer = new StatefulTokenizer(true, true);
        Tokenization result = tokenizer.tokenizeTextSpan("Alpha\n\n\nBeta");

        assertArrayEquals(new String[] {"Alpha", "Beta"}, result.getTokens());
        assertArrayEquals(new int[] {1, 2}, result.getSentenceEndTokenIndexes());
    }

    @Test
    public void unicodeLettersAndCurlyApostropheAreHandled() {
        StatefulTokenizer tokenizer = new StatefulTokenizer(true, false);
        Pair<String[], IntPair[]> result = tokenizer.tokenizeSentence("Café naïve it’s 東京");

        assertArrayEquals(new String[] {"Café", "naïve", "it", "’s", "東京"},
                result.getFirst());
    }

    @Test
    public void controlCharactersTerminateWordsWithoutBecomingTokens() {
        StatefulTokenizer tokenizer = new StatefulTokenizer(true, false);
        Pair<String[], IntPair[]> result = tokenizer.tokenizeSentence("abc\u0000def\u0007ghi");

        assertArrayEquals(new String[] {"abc", "def", "ghi"}, result.getFirst());
        assertArrayEquals(new IntPair[] {new IntPair(0, 3), new IntPair(4, 7),
                new IntPair(8, 11)}, result.getSecond());
    }

    @Test
    public void repeatedCallsOnTokenizerRemainIndependentAndConfigurationCanChange() {
        StatefulTokenizer tokenizer = new StatefulTokenizer(false, false);
        Pair<String[], IntPair[]> first = tokenizer.tokenizeSentence("state-of-art");
        tokenizer.setSplitOnDash(true);
        tokenizer.setSplitOnMultipleNewlines(true);
        Pair<String[], IntPair[]> second = tokenizer.tokenizeSentence("state-of-art");
        Tokenization third = tokenizer.tokenizeTextSpan("A\n\nB");

        assertArrayEquals(new String[] {"state-of-art"}, first.getFirst());
        assertArrayEquals(new String[] {"state", "-", "of", "-", "art"}, second.getFirst());
        assertArrayEquals(new int[] {1, 2}, third.getSentenceEndTokenIndexes());
        assertTrue(tokenizer.isSplitOnDash());
        assertTrue(tokenizer.isSplitOnMultipleNewlines());
    }

    @Test
    public void directParseSupportsPeekAtBothBoundaries() {
        TokenizerStateMachine machine = new TokenizerStateMachine(true, false);
        machine.parseText("abc");

        assertEquals('\u0000', machine.peek(0));
        assertEquals('\u0000', machine.peek(1));
        assertEquals('c', machine.peek(-1));
    }

    @Test
    public void tokenizerConfigurationAccessorsReflectBothFalseValues() {
        StatefulTokenizer tokenizer = new StatefulTokenizer(true, true);
        tokenizer.setSplitOnDash(false);
        tokenizer.setSplitOnMultipleNewlines(false);

        assertFalse(tokenizer.isSplitOnDash());
        assertFalse(tokenizer.isSplitOnMultipleNewlines());
    }

    @Test
    public void mainPrintsOnlyMatchAtBeginningBecausePatternIsAnchored() {
        PrintStream original = System.out;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(bytes));
            TokenizerStateMachine.main(new String[] {"ignored"});
        } finally {
            System.setOut(original);
        }

        String output = bytes.toString();
        assertTrue(output.contains("robert_serafin@kmz.com"));
        assertFalse(output.contains("robert-serafin@kmz.com"));
    }
}
