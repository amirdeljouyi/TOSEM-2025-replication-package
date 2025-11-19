package edu.illinois.cs.cogcomp.nlp.tokenizer;

import edu.illinois.cs.cogcomp.annotation.AnnotatorConfigurator;
import edu.illinois.cs.cogcomp.core.datastructures.IQueryable;
import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.*;
import edu.illinois.cs.cogcomp.core.utilities.configuration.Configurator;
import edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager;
import edu.illinois.cs.cogcomp.lbjava.classify.Feature;
import edu.illinois.cs.cogcomp.nlp.utilities.POSUtils;
import edu.illinois.cs.cogcomp.nlp.utilities.ParseTreeProperties;
import junit.framework.TestCase;
import org.junit.Test;
import java.io.File;
import java.io.IOException;
import java.util.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class TokenizerStateMachine_4_GPTLLMTest {

@Test
public void testSimpleSentenceTokenization() {
// SafeTokenizer tokenizer = new SafeTokenizer(true, true);
// tokenizer.tokenize("This is a test.");
// List<String> tokens = tokenizer.getTokensInline();
// assertEquals(5, tokens.size());
// assertEquals("This", tokens.get(0));
// assertEquals("is", tokens.get(1));
// assertEquals("a", tokens.get(2));
// assertEquals("test", tokens.get(3));
// assertEquals(".", tokens.get(4));
}

@Test
public void testEmailDetection() {
// SafeTokenizer tokenizer = new SafeTokenizer(true, true);
// tokenizer.tokenize("Contact: john.doe@example.com now.");
// List<String> tokens = tokenizer.getTokensInline();
// assertTrue(tokens.contains("john.doe@example.com"));
// assertTrue(tokens.contains("now"));
// assertTrue(tokens.contains("."));
}

@Test
public void testURLDetection() {
// SafeTokenizer tokenizer = new SafeTokenizer(true, true);
// tokenizer.tokenize("Visit http://example.com for more.");
// List<String> tokens = tokenizer.getTokensInline();
// assertTrue(tokens.contains("http://example.com"));
// assertTrue(tokens.contains("for"));
// assertTrue(tokens.contains("more"));
// assertTrue(tokens.contains("."));
}

@Test
public void testAcronymParsing() {
// SafeTokenizer tokenizer = new SafeTokenizer(true, true);
// tokenizer.tokenize("He works at U.S.A. Corp.");
// List<String> tokens = tokenizer.getTokensInline();
// assertEquals("He", tokens.get(0));
// assertEquals("works", tokens.get(1));
// assertEquals("at", tokens.get(2));
// assertEquals("U.S.A.", tokens.get(3));
// assertEquals("Corp", tokens.get(4));
// assertEquals(".", tokens.get(5));
}

@Test
public void testContractions() {
// SafeTokenizer tokenizer = new SafeTokenizer(true, true);
// tokenizer.tokenize("It's fine. She's okay. We're ready.");
// List<String> tokens = tokenizer.getTokensInline();
// assertTrue(tokens.contains("It's"));
// assertTrue(tokens.contains("fine"));
// assertTrue(tokens.contains("."));
// assertTrue(tokens.contains("She's"));
// assertTrue(tokens.contains("okay"));
// assertTrue(tokens.contains("We're"));
// assertTrue(tokens.contains("ready"));
}

@Test
public void testPossessiveS() {
// SafeTokenizer tokenizer = new SafeTokenizer(true, true);
// tokenizer.tokenize("That is John's book.");
// List<String> tokens = tokenizer.getTokensInline();
// assertTrue(tokens.contains("John"));
// assertTrue(tokens.contains("'s"));
// assertTrue(tokens.contains("book"));
// assertTrue(tokens.contains("."));
}

@Test
public void testHyphenSplitEnabled() {
// SafeTokenizer tokenizer = new SafeTokenizer(true, true);
// tokenizer.tokenize("state-of-the-art");
// List<String> tokens = tokenizer.getTokensInline();
// assertTrue(tokens.contains("state"));
// assertTrue(tokens.contains("of"));
// assertTrue(tokens.contains("the"));
// assertTrue(tokens.contains("art"));
}

@Test
public void testDoubleNewlineSplitsParagraphs() {
// SafeTokenizer tokenizer = new SafeTokenizer(true, true);
// tokenizer.tokenize("Line one.\n\nLine two.");
// List<String> tokens = tokenizer.getTokensInline();
// assertTrue(tokens.contains("Line"));
// assertTrue(tokens.contains("one"));
// assertTrue(tokens.contains("two"));
}

@Test
public void testDollarAmountTokenDetection() {
// SafeTokenizer tokenizer = new SafeTokenizer(true, true);
// tokenizer.tokenize("That costs $15.99 today.");
// List<String> tokens = tokenizer.getTokensInline();
// assertTrue(tokens.contains("$"));
// assertTrue(tokens.contains("15.99"));
// assertTrue(tokens.contains("today"));
}

@Test
public void testDecimalNumberHandling() {
// SafeTokenizer tokenizer = new SafeTokenizer(true, true);
// tokenizer.tokenize("Pi is approximately 3.14.");
// List<String> tokens = tokenizer.getTokensInline();
// assertTrue(tokens.contains("3.14"));
}

@Test
public void testAbbreviationDetection() {
// SafeTokenizer tokenizer = new SafeTokenizer(true, true);
// tokenizer.tokenize("Dr. Smith went to the U.K.");
// List<String> tokens = tokenizer.getTokensInline();
// assertTrue(tokens.contains("Dr."));
// assertTrue(tokens.contains("U.K."));
}

@Test
public void testSentenceEndingWithQuote() {
// SafeTokenizer tokenizer = new SafeTokenizer(true, true);
// tokenizer.tokenize("He said, \"Go away.\" Then he left.");
// List<String> tokens = tokenizer.getTokensInline();
// assertTrue(tokens.contains("He"));
// assertTrue(tokens.contains("said"));
// assertTrue(tokens.contains(","));
// assertTrue(tokens.contains("\""));
// assertTrue(tokens.contains("Go"));
// assertTrue(tokens.contains("away"));
// assertTrue(tokens.contains("."));
// assertTrue(tokens.contains("Then"));
}

@Test
public void testEllipsisContinuity() {
// SafeTokenizer tokenizer = new SafeTokenizer(true, true);
// tokenizer.tokenize("Wait... what?");
// List<String> tokens = tokenizer.getTokensInline();
// assertTrue(tokens.contains("Wait"));
// assertTrue(tokens.stream().anyMatch(t -> t.contains("...")));
// assertTrue(tokens.contains("what"));
// assertTrue(tokens.contains("?"));
}

@Test
public void testUnprintableCharacterSplit() {
// SafeTokenizer tokenizer = new SafeTokenizer(true, true);
// tokenizer.tokenize("Hello\u0000World");
// List<String> tokens = tokenizer.getTokensInline();
// assertTrue(tokens.contains("Hello"));
// assertTrue(tokens.contains("World"));
}

@Test
public void testSpecialCharactersOnly() {
// SafeTokenizer tokenizer = new SafeTokenizer(true, true);
// tokenizer.tokenize("!@#$%^&*()");
// List<String> tokens = tokenizer.getTokensInline();
// assertFalse(tokens.isEmpty());
// assertTrue(tokens.stream().anyMatch(t -> t.matches("[!@#$%^&*()]+")));
}

@Test
public void testTimeNotation() {
// SafeTokenizer tokenizer = new SafeTokenizer(true, true);
// tokenizer.tokenize("The train is at 10:45.");
// List<String> tokens = tokenizer.getTokensInline();
// assertTrue(tokens.contains("10:45"));
// assertTrue(tokens.contains("."));
}

@Test
public void testMultipleSentences() {
// SafeTokenizer tokenizer = new SafeTokenizer(true, true);
// tokenizer.tokenize("Hello. World!");
// List<String> tokens = tokenizer.getTokensInline();
// assertTrue(tokens.contains("Hello"));
// assertTrue(tokens.contains("."));
// assertTrue(tokens.contains("World"));
// assertTrue(tokens.contains("!"));
}

@Test
public void testTrailingWhitespaceDoesNotAffectTokens() {
// SafeTokenizer tokenizer = new SafeTokenizer(true, true);
// tokenizer.tokenize("Hello world.   ");
// List<String> tokens = tokenizer.getTokensInline();
// assertTrue(tokens.contains("Hello"));
// assertTrue(tokens.contains("world"));
// assertTrue(tokens.contains("."));
}

@Test
public void testEmptyInput() {
// SafeTokenizer tokenizer = new SafeTokenizer(true, true);
// tokenizer.tokenize("");
// List<String> tokens = tokenizer.getTokensInline();
// assertTrue(tokens.isEmpty());
}

@Test
public void testWhitespaceOnlyInput() {
// SafeTokenizer tokenizer = new SafeTokenizer(true, true);
// tokenizer.tokenize("   \n\t\r ");
// List<String> tokens = tokenizer.getTokensInline();
// assertTrue(tokens.isEmpty());
}

@Test
public void testMalformedEmailStillProcessesWords() {
// SafeTokenizer tokenizer = new SafeTokenizer(true, true);
// tokenizer.tokenize("This is invalid@com.");
// List<String> tokens = tokenizer.getTokensInline();
// assertTrue(tokens.contains("invalid"));
// assertTrue(tokens.contains("@"));
// assertTrue(tokens.contains("com"));
// assertTrue(tokens.contains("."));
}

@Test
public void testNumberWithCommasIsSingleToken() {
// SafeTokenizer tokenizer = new SafeTokenizer(true, true);
// tokenizer.tokenize("Revenue is 1,234,567.89 dollars.");
// List<String> tokens = tokenizer.getTokensInline();
// assertTrue(tokens.contains("1,234,567.89"));
// assertTrue(tokens.contains("dollars"));
}

@Test
public void testBacktickQuoteHandling() {
// SafeTokenizer tokenizer = new SafeTokenizer(true, true);
// tokenizer.tokenize("This is `quoted` text.");
// List<String> tokens = tokenizer.getTokensInline();
// assertTrue(tokens.contains("quoted"));
// assertTrue(tokens.contains("text"));
}

@Test
public void testContractionsAndAbbreviationTogether() {
// SafeTokenizer tokenizer = new SafeTokenizer(true, true);
// tokenizer.tokenize("It's U.S.A.'s responsibility.");
// List<String> tokens = tokenizer.getTokensInline();
// assertTrue(tokens.contains("It's"));
// assertTrue(tokens.contains("U.S.A."));
// assertTrue(tokens.contains("'s"));
// assertTrue(tokens.contains("responsibility"));
}

@Test
public void testUnicodeQuotes() {
// SafeTokenizer tokenizer = new SafeTokenizer(true, true);
// tokenizer.tokenize("He said ‘yes’ confidently.");
// List<String> tokens = tokenizer.getTokensInline();
// assertTrue(tokens.contains("‘"));
// assertTrue(tokens.contains("yes"));
// assertTrue(tokens.contains("’"));
// assertTrue(tokens.contains("confidently"));
}

@Test
public void testColonNonURLNonTime() {
// SafeTokenizer tokenizer = new SafeTokenizer(true, true);
// tokenizer.tokenize("Error: Null pointer exception.");
// List<String> tokens = tokenizer.getTokensInline();
// assertTrue(tokens.contains("Error"));
// assertTrue(tokens.contains(":"));
// assertTrue(tokens.contains("Null"));
// assertTrue(tokens.contains("pointer"));
// assertTrue(tokens.contains("exception"));
}

@Test
public void testColonWithWhitespaceShouldNotBeURL() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, true);
tokenizer.parseText("Protocol : notURL");
List<String> tokens = tokenizer.completed.stream().map(s -> s.getWord()).collect(java.util.stream.Collectors.toList());
assertTrue(tokens.contains(":"));
assertTrue(tokens.contains("notURL"));
}

@Test
public void testColonPrecededByNumberAndFollowedByNumberShouldBeTime() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, true);
tokenizer.parseText("Event at 13:45 today.");
List<String> tokens = tokenizer.completed.stream().map(s -> s.getWord()).collect(java.util.stream.Collectors.toList());
assertTrue(tokens.contains("13:45"));
assertTrue(tokens.contains("today"));
}

@Test
public void testAbbreviationEndingBeforeWhitespaceShouldBeWord() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, true);
tokenizer.parseText("Sent by U.S.");
List<String> tokens = tokenizer.completed.stream().map(s -> s.getWord()).collect(java.util.stream.Collectors.toList());
assertTrue(tokens.contains("U.S."));
}

@Test
public void testNumberFollowedByDollarShouldBeSeparateTokens() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, true);
tokenizer.parseText("Salary 5000$ only");
List<String> tokens = tokenizer.completed.stream().map(s -> s.getWord()).collect(java.util.stream.Collectors.toList());
assertTrue(tokens.contains("5000"));
assertTrue(tokens.contains("$"));
}

@Test
public void testSingleCharacterWordAtEndOfSentence() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, true);
tokenizer.parseText("He saw a bird f.");
List<String> tokens = tokenizer.completed.stream().map(s -> s.getWord()).collect(java.util.stream.Collectors.toList());
assertTrue(tokens.contains("f"));
assertTrue(tokens.contains("."));
}

@Test
public void testWordWithDigitsAndLettersTogether() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, true);
tokenizer.parseText("Model X3000 announced.");
List<String> tokens = tokenizer.completed.stream().map(s -> s.getWord()).collect(java.util.stream.Collectors.toList());
assertTrue(tokens.contains("X3000"));
}

@Test
public void testSymbolRepeatedShouldBeSingleSpecialToken() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, true);
tokenizer.parseText("#### warning");
List<String> tokens = tokenizer.completed.stream().map(s -> s.getWord()).collect(java.util.stream.Collectors.toList());
assertTrue(tokens.contains("####"));
assertTrue(tokens.contains("warning"));
}

@Test
public void testContractionWithGStrippedIng() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, true);
tokenizer.parseText("He was runnin' fast.");
List<String> tokens = tokenizer.completed.stream().map(s -> s.getWord()).collect(java.util.stream.Collectors.toList());
assertTrue(tokens.contains("runnin'"));
}

@Test
public void testIncompleteEmailShouldNotBeClassifiedAsEmail() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, true);
tokenizer.parseText("Reach me at example@.");
List<String> tokens = tokenizer.completed.stream().map(s -> s.getWord()).collect(java.util.stream.Collectors.toList());
assertTrue(tokens.contains("example"));
assertTrue(tokens.contains("@"));
}

@Test
public void testEmailAdjacentToPunctuation() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, true);
tokenizer.parseText("Contact me: robin@mail.com!");
List<String> tokens = tokenizer.completed.stream().map(s -> s.getWord()).collect(java.util.stream.Collectors.toList());
assertTrue(tokens.contains("robin@mail.com"));
assertTrue(tokens.contains("!"));
}

@Test
public void testNumericCommaFollowedByNonDigitIsSplit() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, true);
tokenizer.parseText("Number is 123,hello");
List<String> tokens = tokenizer.completed.stream().map(s -> s.getWord()).collect(java.util.stream.Collectors.toList());
assertTrue(tokens.contains("123"));
assertTrue(tokens.contains(","));
assertTrue(tokens.contains("hello"));
}

@Test
public void testMultiplePunctuationInsideQuotes() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, true);
tokenizer.parseText("\"Wait?!\" she said.");
List<String> tokens = tokenizer.completed.stream().map(s -> s.getWord()).collect(java.util.stream.Collectors.toList());
assertTrue(tokens.contains("Wait"));
assertTrue(tokens.contains("?"));
assertTrue(tokens.contains("!"));
assertTrue(tokens.contains("\""));
assertTrue(tokens.contains("she"));
assertTrue(tokens.contains("said"));
assertTrue(tokens.contains("."));
}

@Test
public void testBackToBackSentencesWithoutWhitespace() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, true);
tokenizer.parseText("First.Second.");
List<String> tokens = tokenizer.completed.stream().map(s -> s.getWord()).collect(java.util.stream.Collectors.toList());
assertTrue(tokens.contains("First"));
assertTrue(tokens.contains("."));
assertTrue(tokens.contains("Second"));
assertTrue(tokens.contains("."));
}

@Test
public void testQuoteAtBeginningAndEndOfSentence() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, true);
tokenizer.parseText("\"Hello world.\"");
List<String> tokens = tokenizer.completed.stream().map(s -> s.getWord()).collect(java.util.stream.Collectors.toList());
assertTrue(tokens.contains("\""));
assertTrue(tokens.contains("Hello"));
assertTrue(tokens.contains("world"));
assertTrue(tokens.contains("."));
assertEquals("\"", tokens.get(tokens.size() - 1));
}

@Test
public void testColonFollowedByWhitespaceIsNotURL() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, true);
tokenizer.parseText("text : value");
List<String> tokens = tokenizer.completed.stream().map(s -> s.getWord()).collect(java.util.stream.Collectors.toList());
assertTrue(tokens.contains("text"));
assertTrue(tokens.contains(":"));
assertTrue(tokens.contains("value"));
}

@Test
public void testFailedURLDetectionWithInvalidURI() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, true);
tokenizer.parseText("visit: https:::/invalid");
List<String> tokens = tokenizer.completed.stream().map(s -> s.getWord()).collect(java.util.stream.Collectors.toList());
assertTrue(tokens.contains("visit"));
assertTrue(tokens.contains(":"));
assertTrue(tokens.contains("https"));
assertTrue(tokens.contains(":::"));
assertTrue(tokens.contains("/"));
assertTrue(tokens.contains("invalid"));
}

@Test
public void testContractionNotMatchingKnownSuffixesShouldNotSplit() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, true);
tokenizer.parseText("That bea'y is rare.");
List<String> tokens = tokenizer.completed.stream().map(s -> s.getWord()).collect(java.util.stream.Collectors.toList());
assertTrue(tokens.contains("bea'y"));
}

@Test
public void testDashBetweenNumbersStaysAsWord() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, true);
tokenizer.parseText("Phone is 123-456.");
List<String> tokens = tokenizer.completed.stream().map(s -> s.getWord()).collect(java.util.stream.Collectors.toList());
assertTrue(tokens.contains("123-456"));
}

@Test
public void testAbbreviationFailsValidationNoPeriods() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, true);
tokenizer.parseText("ALPHA beta GAMMA");
List<String> tokens = tokenizer.completed.stream().map(s -> s.getWord()).collect(java.util.stream.Collectors.toList());
assertTrue(tokens.contains("ALPHA"));
assertFalse(tokens.get(0).contains("."));
}

@Test
public void testNestedQuotesWithEndPunctuation() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, true);
tokenizer.parseText("\"He said 'Go now.'\"");
List<String> tokens = tokenizer.completed.stream().map(s -> s.getWord()).collect(java.util.stream.Collectors.toList());
assertTrue(tokens.contains("He"));
assertTrue(tokens.contains("said"));
assertTrue(tokens.contains("'"));
assertTrue(tokens.contains("Go"));
assertTrue(tokens.contains("now"));
assertTrue(tokens.contains("."));
assertTrue(tokens.get(tokens.size() - 1).equals("\""));
}

@Test
public void testSingleSpecialCharacterTokenDoesNotSplit() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, true);
tokenizer.parseText("& symbol");
List<String> tokens = tokenizer.completed.stream().map(s -> s.getWord()).collect(java.util.stream.Collectors.toList());
assertTrue(tokens.contains("&"));
assertTrue(tokens.contains("symbol"));
}

@Test
public void testCustomUnrecognizedUnicodeCharIsUnprintable() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, true);
tokenizer.parseText("test\uFFFFUnicode");
List<String> tokens = tokenizer.completed.stream().map(s -> s.getWord()).collect(java.util.stream.Collectors.toList());
assertTrue(tokens.contains("test"));
assertTrue(tokens.contains("Unicode"));
}

@Test
public void testInvalidEmailFallbackToRegex2Match() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, true);
tokenizer.parseText("user@domain.com");
List<String> tokens = tokenizer.completed.stream().map(s -> s.getWord()).collect(java.util.stream.Collectors.toList());
assertTrue(tokens.contains("user@domain.com"));
}

@Test
public void testStatePopCreatesNewSentenceWhenStackIsEmpty() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, true);
tokenizer.parseText("One.\n\nTwo.");
List<String> tokens = tokenizer.completed.stream().map(s -> s.getWord()).collect(java.util.stream.Collectors.toList());
assertTrue(tokens.contains("One"));
assertTrue(tokens.contains("Two"));
assertEquals(".", tokens.get(1));
assertEquals(".", tokens.get(tokens.size() - 1));
}

@Test
public void testUnterminatedQuoteAtEndOfSentence() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, true);
tokenizer.parseText("He said, \"Hello world.");
List<String> tokens = tokenizer.completed.stream().map(s -> s.getWord()).collect(java.util.stream.Collectors.toList());
assertTrue(tokens.contains("He"));
assertTrue(tokens.contains("said"));
assertTrue(tokens.contains(","));
assertTrue(tokens.contains("\""));
assertTrue(tokens.contains("Hello"));
assertTrue(tokens.contains("world"));
assertTrue(tokens.contains("."));
}

@Test
public void testPeriodFollowedByLowercaseDoesNotEndSentence() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, true);
tokenizer.parseText("Fig. 4 illustrates the result.");
List<String> tokens = tokenizer.completed.stream().map(s -> s.getWord()).collect(java.util.stream.Collectors.toList());
assertTrue(tokens.contains("Fig."));
assertTrue(tokens.contains("4"));
assertTrue(tokens.contains("illustrates"));
assertTrue(tokens.contains("the"));
assertTrue(tokens.contains("result"));
assertTrue(tokens.contains("."));
}

@Test
public void testMultipleDotsOnlyShouldBeOneToken() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, true);
tokenizer.parseText("...");
List<String> tokens = tokenizer.completed.stream().map(s -> s.getWord()).collect(java.util.stream.Collectors.toList());
assertEquals(1, tokens.size());
assertEquals("...", tokens.get(0));
}

@Test
public void testSplitOnTwoNewlinesWithWhitespaceBetweenNewlines() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, true);
tokenizer.parseText("First line.\n \nSecond line.");
List<String> tokens = tokenizer.completed.stream().map(s -> s.getWord()).collect(java.util.stream.Collectors.toList());
assertTrue(tokens.contains("First"));
assertTrue(tokens.contains("line"));
assertTrue(tokens.contains("."));
assertTrue(tokens.contains("Second"));
}

@Test
public void testApostropheAfterNonLetterIsPartOfWord() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, true);
tokenizer.parseText("End.'s");
List<String> tokens = tokenizer.completed.stream().map(s -> s.getWord()).collect(java.util.stream.Collectors.toList());
assertTrue(tokens.contains("End"));
assertTrue(tokens.contains("."));
assertTrue(tokens.contains("'s"));
}

@Test
public void testURLWithHyphenatedDomainShouldBeSingleToken() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, true);
tokenizer.parseText("Go to https://example-site.com now.");
List<String> tokens = tokenizer.completed.stream().map(s -> s.getWord()).collect(java.util.stream.Collectors.toList());
assertTrue(tokens.contains("https://example-site.com"));
assertTrue(tokens.contains("now"));
assertTrue(tokens.contains("."));
}

@Test
public void testBacktickAtSentenceStartIsValidToken() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, true);
tokenizer.parseText("`Hello`");
List<String> tokens = tokenizer.completed.stream().map(s -> s.getWord()).collect(java.util.stream.Collectors.toList());
assertTrue(tokens.contains("`"));
assertTrue(tokens.contains("Hello"));
}

@Test
public void testWordWithUnicodeFancyQuoteIsRecognized() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, true);
tokenizer.parseText("He said ‘Hello’. Then left.");
List<String> tokens = tokenizer.completed.stream().map(s -> s.getWord()).collect(java.util.stream.Collectors.toList());
assertTrue(tokens.contains("‘"));
assertTrue(tokens.contains("Hello"));
assertTrue(tokens.contains("’"));
assertTrue(tokens.contains("."));
assertTrue(tokens.contains("Then"));
}

@Test
public void testSingleQuoteAsEntireToken() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, true);
tokenizer.parseText("He said ' yes.");
List<String> tokens = tokenizer.completed.stream().map(s -> s.getWord()).collect(java.util.stream.Collectors.toList());
assertTrue(tokens.contains("'"));
assertTrue(tokens.contains("yes"));
assertTrue(tokens.contains("."));
}

@Test
public void testSlashInsideNumericDateStyleTokenShouldBePreserved() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, true);
tokenizer.parseText("Today's date is 12/25/2023.");
List<String> tokens = tokenizer.completed.stream().map(s -> s.getWord()).collect(java.util.stream.Collectors.toList());
assertTrue(tokens.contains("12/25/2023"));
assertTrue(tokens.contains("."));
}

@Test
public void testQuoteFollowedByEndPunctuationTerminatesSentence() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, true);
tokenizer.parseText("\"Okay!\"");
List<String> tokens = tokenizer.completed.stream().map(s -> s.getWord()).collect(java.util.stream.Collectors.toList());
assertTrue(tokens.contains("Okay"));
assertTrue(tokens.contains("!"));
assertTrue(tokens.get(tokens.size() - 1).equals("\""));
}

@Test
public void testDotFollowedByCapitalDoesNotAssumeSentenceEndWithoutWhitespace() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, true);
tokenizer.parseText("U.S.A.Now");
List<String> tokens = tokenizer.completed.stream().map(s -> s.getWord()).collect(java.util.stream.Collectors.toList());
assertTrue(tokens.contains("U.S.A."));
assertTrue(tokens.contains("Now"));
}

@Test
public void testTimeColonWithoutDigitsBeforeShouldNotBeMerged() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, true);
tokenizer.parseText("Time :45 is invalid.");
List<String> tokens = tokenizer.completed.stream().map(s -> s.getWord()).collect(java.util.stream.Collectors.toList());
assertTrue(tokens.contains("Time"));
assertTrue(tokens.contains(":"));
assertTrue(tokens.contains("45"));
}

@Test
public void testURLMissingProtocolShouldNotBeRecognized() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, true);
tokenizer.parseText("Try www.example.com now.");
List<String> tokens = tokenizer.completed.stream().map(s -> s.getWord()).collect(java.util.stream.Collectors.toList());
assertTrue(tokens.contains("www"));
assertTrue(tokens.contains("."));
assertTrue(tokens.contains("example"));
assertTrue(tokens.contains("."));
assertTrue(tokens.contains("com"));
}

@Test
public void testUnicodeControlCharSplitToken() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, true);
String text = "start\u0003middle\u0004end";
tokenizer.parseText(text);
List<String> tokens = tokenizer.completed.stream().map(s -> s.getWord()).collect(java.util.stream.Collectors.toList());
assertTrue(tokens.contains("start"));
assertTrue(tokens.contains("middle"));
assertTrue(tokens.contains("end"));
}

@Test
public void testOnlyUnprintableCharactersReturnsNoTokens() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, true);
tokenizer.parseText("\u0000\u0001\u0002\u0003");
List<String> tokens = tokenizer.completed.stream().map(s -> s.getWord()).collect(java.util.stream.Collectors.toList());
assertTrue(tokens.isEmpty());
}

@Test
public void testMultipleHyphensAsOneTokenWhenNoSplitOnDash() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, true);
tokenizer.parseText("well-being");
List<String> tokens = tokenizer.completed.stream().map(s -> s.getWord()).collect(java.util.stream.Collectors.toList());
assertTrue(tokens.contains("well-being"));
}

@Test
public void testEmailWithoutUsernameShouldNotMatch() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, true);
tokenizer.parseText("@domain.com something");
List<String> tokens = tokenizer.completed.stream().map(s -> s.getWord()).collect(java.util.stream.Collectors.toList());
assertTrue(tokens.contains("@"));
assertTrue(tokens.contains("domain"));
assertTrue(tokens.contains("."));
assertTrue(tokens.contains("com"));
}

@Test
public void testAbbreviationHighCharCountFailsAcronymHeuristic() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, true);
tokenizer.parseText("SuperLONG.acronym.is.false");
List<String> tokens = tokenizer.completed.stream().map(s -> s.getWord()).collect(java.util.stream.Collectors.toList());
assertTrue(tokens.contains("SuperLONG"));
assertTrue(tokens.contains("."));
assertTrue(tokens.contains("acronym"));
assertTrue(tokens.contains("."));
assertTrue(tokens.contains("is"));
assertTrue(tokens.contains("."));
assertTrue(tokens.contains("false"));
}

@Test
public void testOpenParenthesisAsSeparateToken() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, true);
tokenizer.parseText("(example)");
List<String> tokens = tokenizer.completed.stream().map(s -> s.getWord()).collect(java.util.stream.Collectors.toList());
assertTrue(tokens.contains("("));
assertTrue(tokens.contains("example"));
assertTrue(tokens.contains(")"));
}

@Test
public void testCommaBetweenLettersShouldSplitAsPunctuation() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, true);
tokenizer.parseText("Word,Another");
List<String> tokens = tokenizer.completed.stream().map(s -> s.getWord()).collect(java.util.stream.Collectors.toList());
assertTrue(tokens.contains("Word"));
assertTrue(tokens.contains(","));
assertTrue(tokens.contains("Another"));
}

@Test
public void testFalsePositiveContractionNotSplit() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, true);
tokenizer.parseText("o'clock");
List<String> tokens = tokenizer.completed.stream().map(s -> s.getWord()).collect(java.util.stream.Collectors.toList());
assertTrue(tokens.contains("o'clock"));
}

@Test
public void testSingleLetterUpperCaseWord() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, true);
tokenizer.parseText("I am A B.");
List<String> tokens = tokenizer.completed.stream().map(s -> s.getWord()).collect(java.util.stream.Collectors.toList());
assertTrue(tokens.contains("I"));
assertTrue(tokens.contains("A"));
assertTrue(tokens.contains("B"));
assertTrue(tokens.contains("."));
}

@Test
public void testSentenceEndingEllipsisFollowedByCapitalIsNewSentence() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, true);
tokenizer.parseText("He disappeared... Then it rained.");
List<String> tokens = tokenizer.completed.stream().map(s -> s.getWord()).collect(java.util.stream.Collectors.toList());
assertTrue(tokens.contains("disappeared"));
assertTrue(tokens.contains("..."));
assertTrue(tokens.contains("Then"));
assertTrue(tokens.contains("rained"));
assertTrue(tokens.contains("."));
}

@Test
public void testColonFollowedByQuoteShouldNotBeURL() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("Example: \"quote");
List<String> tokens = tokenizer.completed.stream().map(s -> s.getWord()).collect(java.util.stream.Collectors.toList());
assertTrue(tokens.contains("Example"));
assertTrue(tokens.contains(":"));
assertTrue(tokens.contains("\""));
assertTrue(tokens.contains("quote"));
}

@Test
public void testColonFollowedByWhitespaceFailsURLHeuristic() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("Check this:  next");
List<String> tokens = tokenizer.completed.stream().map(s -> s.getWord()).collect(java.util.stream.Collectors.toList());
assertTrue(tokens.contains(":"));
assertTrue(tokens.contains("next"));
}

@Test
public void testQuoteInsideQuoteDoesNotAffectTermination() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, true);
tokenizer.parseText("\"He said, 'Hi.'\"");
List<String> tokens = tokenizer.completed.stream().map(s -> s.getWord()).collect(java.util.stream.Collectors.toList());
assertTrue(tokens.contains("He"));
assertTrue(tokens.contains("said"));
assertTrue(tokens.contains(","));
assertTrue(tokens.contains("'"));
assertTrue(tokens.contains("Hi"));
assertTrue(tokens.contains("."));
assertEquals("\"", tokens.get(tokens.size() - 1));
}

@Test
public void testDateWithSlashesRecognizedAsSingleToken() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, true);
tokenizer.parseText("Date: 01/01/2024");
List<String> tokens = tokenizer.completed.stream().map(s -> s.getWord()).collect(java.util.stream.Collectors.toList());
assertTrue(tokens.contains(":"));
assertTrue(tokens.contains("01/01/2024"));
}

@Test
public void testSeparatedEllipsesTokensWhenNonDotsIncluded() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, true);
tokenizer.parseText("Wait...?");
List<String> tokens = tokenizer.completed.stream().map(s -> s.getWord()).collect(java.util.stream.Collectors.toList());
assertTrue(tokens.contains("Wait"));
assertTrue(tokens.contains("..."));
assertTrue(tokens.contains("?"));
}

@Test
public void testAbbreviationWithHyphenNotDetectedAsAcronym() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("non-U.S. interactions");
List<String> tokens = tokenizer.completed.stream().map(s -> s.getWord()).collect(java.util.stream.Collectors.toList());
assertTrue(tokens.contains("non"));
assertEquals(".", tokens.get(3));
assertTrue(tokens.contains("interactions"));
}

@Test
public void testContractionWithCommaExtensionIsTreatedCorrectly() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("I'll, won't, can't");
List<String> tokens = tokenizer.completed.stream().map(s -> s.getWord()).collect(java.util.stream.Collectors.toList());
assertTrue(tokens.contains("I'll"));
assertTrue(tokens.contains(","));
assertTrue(tokens.contains("won't"));
assertTrue(tokens.contains("can't"));
}

@Test
public void testDashSurroundedBySpacesIsSpecialToken() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("That was - unexpected.");
List<String> tokens = tokenizer.completed.stream().map(s -> s.getWord()).collect(java.util.stream.Collectors.toList());
assertTrue(tokens.contains("-"));
assertTrue(tokens.contains("unexpected"));
}

@Test
public void testNegativeNumberWithDashSplitAsSpecial() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, true);
tokenizer.parseText("Value was -10.0");
List<String> tokens = tokenizer.completed.stream().map(s -> s.getWord()).collect(java.util.stream.Collectors.toList());
assertTrue(tokens.contains("-"));
assertTrue(tokens.contains("10.0"));
}

@Test
public void testSpecialPunctuationRunIsCollapsedCorrectly() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, true);
tokenizer.parseText("###warning");
List<String> tokens = tokenizer.completed.stream().map(s -> s.getWord()).collect(java.util.stream.Collectors.toList());
assertTrue(tokens.contains("###"));
assertTrue(tokens.contains("warning"));
}

@Test
public void testAbbreviationEndingWithCapitalLetterOnlyRejected() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("The variable X is defined.");
List<String> tokens = tokenizer.completed.stream().map(s -> s.getWord()).collect(java.util.stream.Collectors.toList());
assertTrue(tokens.contains("X"));
assertFalse("Assumed invalid acronym", tokens.get(2).endsWith("."));
}

@Test
public void testUnusualCurrencySymbolStillTokenizesCorrectly() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("Cost was €123.45");
List<String> tokens = tokenizer.completed.stream().map(s -> s.getWord()).collect(java.util.stream.Collectors.toList());
assertTrue(tokens.contains("€"));
assertTrue(tokens.contains("123.45"));
}

@Test
public void testWhitespaceOnlySurroundingSinglePunctuationShouldBePreserved() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("What ? ");
List<String> tokens = tokenizer.completed.stream().map(s -> s.getWord()).collect(java.util.stream.Collectors.toList());
assertTrue(tokens.contains("What"));
assertTrue(tokens.contains("?"));
}

@Test
public void testColonSurroundedByAlphaCharsIsNotURL() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("ratio:weights");
List<String> tokens = tokenizer.completed.stream().map(s -> s.getWord()).collect(java.util.stream.Collectors.toList());
assertTrue(tokens.contains("ratio"));
assertTrue(tokens.contains(":"));
assertTrue(tokens.contains("weights"));
}

@Test
public void testColonSurroundedBySpacesPrecludesTimeParsing() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("11 : 59");
List<String> tokens = tokenizer.completed.stream().map(s -> s.getWord()).collect(java.util.stream.Collectors.toList());
assertTrue(tokens.contains("11"));
assertTrue(tokens.contains(":"));
assertTrue(tokens.contains("59"));
}

@Test
public void testSingleQuotePrecedingNumberShouldNotTriggerContractionHandling() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("'99 reasons");
List<String> tokens = tokenizer.completed.stream().map(s -> s.getWord()).collect(java.util.stream.Collectors.toList());
assertTrue(tokens.contains("'"));
assertTrue(tokens.contains("99"));
assertTrue(tokens.contains("reasons"));
}

@Test
public void testTrailingSlashPreservesNumericWord() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("Version 1.0/");
List<String> tokens = tokenizer.completed.stream().map(s -> s.getWord()).collect(java.util.stream.Collectors.toList());
assertTrue(tokens.contains("1.0"));
assertTrue(tokens.contains("/"));
}

@Test
public void testAbbreviationFailsOnLowercaseLetters() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("e.g. is not a real acronym");
List<String> tokens = tokenizer.completed.stream().map(s -> s.getWord()).collect(java.util.stream.Collectors.toList());
assertTrue(tokens.contains("e.g."));
assertTrue(tokens.contains("is"));
assertTrue(tokens.contains("not"));
}

@Test
public void testNewlineInsideSentenceDoesNotBreakSentenceIfNoDoubleNewline() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("He said\nhello world.");
List<String> tokens = tokenizer.completed.stream().map(s -> s.getWord()).collect(java.util.stream.Collectors.toList());
assertTrue(tokens.contains("He"));
assertTrue(tokens.contains("said"));
assertTrue(tokens.contains("hello"));
assertTrue(tokens.contains("world"));
assertTrue(tokens.contains("."));
}

@Test
public void testSpecialCharactersMixedShouldFormSeparateTokens() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, true);
tokenizer.parseText("!@#$%");
List<String> tokens = tokenizer.completed.stream().map(s -> s.getWord()).collect(java.util.stream.Collectors.toList());
assertEquals(5, tokens.size());
assertTrue(tokens.contains("!"));
assertTrue(tokens.contains("@"));
assertTrue(tokens.contains("#"));
assertTrue(tokens.contains("$"));
assertTrue(tokens.contains("%"));
}

@Test
public void testSameSpecialCharactersClusteredAreKeptTogether() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("!!!");
List<String> tokens = tokenizer.completed.stream().map(s -> s.getWord()).collect(java.util.stream.Collectors.toList());
assertEquals(1, tokens.size());
assertEquals("!!!", tokens.get(0));
}

@Test
public void testExclamationInsideQuotesHandledCorrectly() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, true);
tokenizer.parseText("\"Unbelievable!\" he said.");
List<String> tokens = tokenizer.completed.stream().map(s -> s.getWord()).collect(java.util.stream.Collectors.toList());
assertTrue(tokens.contains("\""));
assertTrue(tokens.contains("Unbelievable"));
assertTrue(tokens.contains("!"));
assertTrue(tokens.contains("he"));
assertTrue(tokens.contains("said"));
assertTrue(tokens.contains("."));
}

@Test
public void testMultipleQuotesWithoutClosingHandledRobustly() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, true);
tokenizer.parseText("\"He said, \"Hello.");
List<String> tokens = tokenizer.completed.stream().map(s -> s.getWord()).collect(java.util.stream.Collectors.toList());
assertTrue(tokens.contains("He"));
assertTrue(tokens.contains("said"));
assertTrue(tokens.contains(","));
assertTrue(tokens.contains("\""));
assertTrue(tokens.contains("Hello"));
assertTrue(tokens.contains("."));
}

@Test
public void testUnicodeSmartQuotesAreHandledAsPunctuation() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, true);
tokenizer.parseText("He said ‘hello’.");
List<String> tokens = tokenizer.completed.stream().map(s -> s.getWord()).collect(java.util.stream.Collectors.toList());
assertTrue(tokens.contains("‘"));
assertTrue(tokens.contains("hello"));
assertTrue(tokens.contains("’"));
assertTrue(tokens.contains("."));
}

@Test
public void testConsecutiveUnprintableCharactersIgnored() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, true);
String input = "Word\u0000\u0001\u0002\u0003Done.";
tokenizer.parseText(input);
List<String> tokens = tokenizer.completed.stream().map(s -> s.getWord()).collect(java.util.stream.Collectors.toList());
assertTrue(tokens.contains("Word"));
assertTrue(tokens.contains("Done"));
assertTrue(tokens.contains("."));
}

@Test
public void testMultipleWhitespaceTokensIgnoredButPreserveMass() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, true);
tokenizer.parseText(" \n\t Foo \nBar ");
List<String> tokens = tokenizer.completed.stream().map(s -> s.getWord()).collect(java.util.stream.Collectors.toList());
assertTrue(tokens.contains("Foo"));
assertTrue(tokens.contains("Bar"));
}
}
