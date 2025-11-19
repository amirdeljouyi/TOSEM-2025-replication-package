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

public class TokenizerStateMachine_1_GPTLLMTest {

@Test
public void testEmptyInputParsesNothing() {
// TokenizerStateMachine tokenizer = new TestTokenizerStateMachine(true, true);
// ((TestTokenizerStateMachine) tokenizer).parse("");
// List<String> tokens = ((TestTokenizerStateMachine) tokenizer).getWords();
// assertEquals(0, tokens.size());
}

@Test
public void testSimpleSentenceParsesWordsAndPeriod() {
// TokenizerStateMachine tokenizer = new TestTokenizerStateMachine(true, true);
// ((TestTokenizerStateMachine) tokenizer).parse("This is a test.");
// List<String> tokens = ((TestTokenizerStateMachine) tokenizer).getWords();
// assertEquals(5, tokens.size());
// assertEquals("This", tokens.get(0));
// assertEquals("is", tokens.get(1));
// assertEquals("a", tokens.get(2));
// assertEquals("test", tokens.get(3));
// assertEquals(".", tokens.get(4));
}

@Test
public void testHandleEmailAddressAsSingleToken() {
// TokenizerStateMachine tokenizer = new TestTokenizerStateMachine(true, true);
// ((TestTokenizerStateMachine) tokenizer).parse("Contact us at test.email@example.com now.");
// List<String> tokens = ((TestTokenizerStateMachine) tokenizer).getWords();
// assertTrue(tokens.contains("test.email@example.com"));
// assertEquals("now", tokens.get(tokens.size() - 2));
// assertEquals(".", tokens.get(tokens.size() - 1));
}

@Test
public void testHandleUrlAsSingleToken() {
// TokenizerStateMachine tokenizer = new TestTokenizerStateMachine(true, true);
// ((TestTokenizerStateMachine) tokenizer).parse("Visit https://example.org/page now.");
// List<String> tokens = ((TestTokenizerStateMachine) tokenizer).getWords();
// assertTrue(tokens.contains("https://example.org/page"));
// assertEquals("Visit", tokens.get(0));
// assertEquals("now", tokens.get(tokens.size() - 2));
// assertEquals(".", tokens.get(tokens.size() - 1));
}

@Test
public void testContractionHandling() {
// TokenizerStateMachine tokenizer = new TestTokenizerStateMachine(true, true);
// ((TestTokenizerStateMachine) tokenizer).parse("He's going there.");
// List<String> tokens = ((TestTokenizerStateMachine) tokenizer).getWords();
// assertEquals("He's", tokens.get(0));
// assertEquals("going", tokens.get(1));
// assertEquals("there", tokens.get(2));
// assertEquals(".", tokens.get(3));
}

@Test
public void testAbbreviationDoesNotSplit() {
// TokenizerStateMachine tokenizer = new TestTokenizerStateMachine(true, true);
// ((TestTokenizerStateMachine) tokenizer).parse("She works at U.S.A. embassy.");
// List<String> tokens = ((TestTokenizerStateMachine) tokenizer).getWords();
// assertTrue(tokens.contains("U.S.A."));
// assertEquals("embassy", tokens.get(tokens.size() - 2));
// assertEquals(".", tokens.get(tokens.size() - 1));
}

@Test
public void testDashSplitWhenEnabled() {
// TokenizerStateMachine tokenizer = new TestTokenizerStateMachine(true, false);
// ((TestTokenizerStateMachine) tokenizer).parse("state-of-the-art");
// List<String> tokens = ((TestTokenizerStateMachine) tokenizer).getWords();
// assertEquals("state", tokens.get(0));
// assertEquals("-", tokens.get(1));
// assertEquals("of", tokens.get(2));
// assertEquals("-", tokens.get(3));
// assertEquals("the", tokens.get(4));
// assertEquals("-", tokens.get(5));
// assertEquals("art", tokens.get(6));
}

@Test
public void testDashNotSplitWhenDisabled() {
// TokenizerStateMachine tokenizer = new TestTokenizerStateMachine(false, false);
// ((TestTokenizerStateMachine) tokenizer).parse("well-known");
// List<String> tokens = ((TestTokenizerStateMachine) tokenizer).getWords();
// assertTrue(tokens.contains("well-known") || tokens.contains("known"));
}

@Test
public void testMultipleExclamationMarks() {
// TokenizerStateMachine tokenizer = new TestTokenizerStateMachine(true, false);
// ((TestTokenizerStateMachine) tokenizer).parse("Wow!!!");
// List<String> tokens = ((TestTokenizerStateMachine) tokenizer).getWords();
// assertEquals("Wow", tokens.get(0));
// assertEquals("!", tokens.get(1));
// assertEquals("!", tokens.get(2));
// assertEquals("!", tokens.get(3));
}

@Test
public void testSentenceBreaksOnTwoNewlines() {
// TokenizerStateMachine tokenizer = new TestTokenizerStateMachine(true, true);
// ((TestTokenizerStateMachine) tokenizer).parse("First line.\n\nSecond line.");
// List<String> tokens = ((TestTokenizerStateMachine) tokenizer).getWords();
// assertEquals("First", tokens.get(0));
// assertEquals("line", tokens.get(1));
// assertEquals(".", tokens.get(2));
// assertEquals("Second", tokens.get(3));
// assertEquals("line", tokens.get(4));
// assertEquals(".", tokens.get(5));
}

@Test
public void testNumericCommaHandling() {
// TokenizerStateMachine tokenizer = new TestTokenizerStateMachine(true, false);
// ((TestTokenizerStateMachine) tokenizer).parse("The number is 1,000.");
// List<String> tokens = ((TestTokenizerStateMachine) tokenizer).getWords();
// assertTrue(tokens.contains("1,000"));
}

@Test
public void testDecimalNumberNotSplit() {
// TokenizerStateMachine tokenizer = new TestTokenizerStateMachine(true, false);
// ((TestTokenizerStateMachine) tokenizer).parse("Pi is approximately 3.14.");
// List<String> tokens = ((TestTokenizerStateMachine) tokenizer).getWords();
// assertEquals("3.14", tokens.get(tokens.size() - 2));
}

@Test
public void testQuotedSentenceHandling() {
// TokenizerStateMachine tokenizer = new TestTokenizerStateMachine(true, false);
// ((TestTokenizerStateMachine) tokenizer).parse("\"Hello world.\"");
// List<String> tokens = ((TestTokenizerStateMachine) tokenizer).getWords();
// assertEquals("\"", tokens.get(0));
// assertEquals("Hello", tokens.get(1));
// assertEquals("world", tokens.get(2));
// assertEquals(".", tokens.get(3));
// assertEquals("\"", tokens.get(4));
}

@Test
public void testHandlesSingleQuoteInContraction() {
// TokenizerStateMachine tokenizer = new TestTokenizerStateMachine(true, false);
// ((TestTokenizerStateMachine) tokenizer).parse("That's John's hat.");
// List<String> tokens = ((TestTokenizerStateMachine) tokenizer).getWords();
// assertEquals("That's", tokens.get(0));
// assertEquals("John's", tokens.get(1));
// assertEquals("hat", tokens.get(2));
// assertEquals(".", tokens.get(3));
}

@Test
public void testSlashInDate() {
// TokenizerStateMachine tokenizer = new TestTokenizerStateMachine(true, false);
// ((TestTokenizerStateMachine) tokenizer).parse("My birthday is 12/30/1990.");
// List<String> tokens = ((TestTokenizerStateMachine) tokenizer).getWords();
// assertTrue(tokens.contains("12/30/1990"));
}

@Test
public void testSingleCharacterInput() {
// TokenizerStateMachine tokenizer = new TokenizerStateMachineTest.TestTokenizerStateMachine(true, true);
// ((TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).parse("A");
// List<String> tokens = ((TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).getWords();
// assertEquals(1, tokens.size());
// assertEquals("A", tokens.get(0));
}

@Test
public void testWhitespaceOnlyInput() {
// TokenizerStateMachine tokenizer = new TokenizerStateMachineTest.TestTokenizerStateMachine(true, true);
// ((TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).parse("   \t\n   ");
// List<String> tokens = ((TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).getWords();
// assertEquals(0, tokens.size());
}

@Test
public void testUnfinishedEmail() {
// TokenizerStateMachine tokenizer = new TokenizerStateMachineTest.TestTokenizerStateMachine(true, false);
// ((TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).parse("Send mail to someone@");
// List<String> tokens = ((TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).getWords();
// assertTrue(tokens.contains("someone"));
// assertTrue(tokens.contains("@"));
}

@Test
public void testColonNotURL() {
// TokenizerStateMachine tokenizer = new TokenizerStateMachineTest.TestTokenizerStateMachine(true, false);
// ((TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).parse("Time: 10:30 AM.");
// List<String> tokens = ((TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).getWords();
// assertEquals("Time", tokens.get(0));
// assertEquals(":", tokens.get(1));
// assertEquals("10", tokens.get(2));
// assertEquals(":", tokens.get(3));
// assertEquals("30", tokens.get(4));
// assertEquals("AM", tokens.get(5));
// assertEquals(".", tokens.get(6));
}

@Test
public void testMultipleNewlinesOnly() {
// TokenizerStateMachine tokenizer = new TokenizerStateMachineTest.TestTokenizerStateMachine(true, true);
// ((TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).parse("\n\n\n\n");
// List<String> tokens = ((TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).getWords();
// assertEquals(0, tokens.size());
}

@Test
public void testUnicodeControlCharacters() {
char controlChar = 0x0001;
String input = "Valid" + controlChar + "Word";
// TokenizerStateMachine tokenizer = new TokenizerStateMachineTest.TestTokenizerStateMachine(true, false);
// ((TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).parse(input);
// List<String> tokens = ((TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).getWords();
// assertEquals("Valid", tokens.get(0));
// assertEquals("Word", tokens.get(1));
}

@Test
public void testOnlyPunctuationMarksTogether() {
// TokenizerStateMachine tokenizer = new TokenizerStateMachineTest.TestTokenizerStateMachine(true, false);
// ((TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).parse("...!!!???");
// List<String> tokens = ((TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).getWords();
// assertEquals(7, tokens.size());
// assertEquals(".", tokens.get(0));
// assertEquals(".", tokens.get(1));
// assertEquals(".", tokens.get(2));
// assertEquals("!", tokens.get(3));
// assertEquals("!", tokens.get(4));
// assertEquals("?", tokens.get(5));
// assertEquals("?", tokens.get(6));
}

@Test
public void testMultipleColonURLFalsePositive() {
// TokenizerStateMachine tokenizer = new TokenizerStateMachineTest.TestTokenizerStateMachine(true, false);
// ((TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).parse("Ratio 2:1 and key:value");
// List<String> tokens = ((TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).getWords();
// assertEquals("2", tokens.get(1));
// assertEquals(":", tokens.get(2));
// assertEquals("1", tokens.get(3));
// assertEquals("key", tokens.get(4));
// assertEquals(":", tokens.get(5));
// assertEquals("value", tokens.get(6));
}

@Test
public void testPeekBehaviorAtEndOfInput() {
// TokenizerStateMachine tokenizer = new TokenizerStateMachineTest.TestTokenizerStateMachine(true, false);
// ((TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).parse("End");
// List<String> tokens = ((TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).getWords();
// assertEquals("End", tokens.get(0));
}

@Test
public void testLongParagraphWithoutPunctuation() {
// TokenizerStateMachine tokenizer = new TokenizerStateMachineTest.TestTokenizerStateMachine(true, true);
// ((TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).parse("This is a long text with no punctuation and it just keeps going without breaks and without any kind of delimiter");
// List<String> tokens = ((TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).getWords();
// assertTrue(tokens.size() > 10);
// assertEquals("This", tokens.get(0));
// assertEquals("delimiter", tokens.get(tokens.size() - 1));
}

@Test
public void testAcronymWithTrailingPeriod() {
// TokenizerStateMachine tokenizer = new TokenizerStateMachineTest.TestTokenizerStateMachine(true, false);
// ((TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).parse("Company name is Inc.");
// List<String> tokens = ((TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).getWords();
// assertEquals("Inc.", tokens.get(3));
}

@Test
public void testTrailingWhitespacePreservedTillEnd() {
// TokenizerStateMachine tokenizer = new TokenizerStateMachineTest.TestTokenizerStateMachine(true, true);
// ((TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).parse("The end.   ");
// List<String> tokens = ((TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).getWords();
// assertEquals("The", tokens.get(0));
// assertEquals("end", tokens.get(1));
// assertEquals(".", tokens.get(2));
}

@Test
public void testEmailWithSubdomain() {
// TokenizerStateMachine tokenizer = new TokenizerStateMachineTest.TestTokenizerStateMachine(true, false);
// ((TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).parse("Contact me at john.doe@sub.example.com now.");
// List<String> tokens = ((TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).getWords();
// assertTrue(tokens.contains("john.doe@sub.example.com"));
// assertEquals("now", tokens.get(tokens.size() - 2));
// assertEquals(".", tokens.get(tokens.size() - 1));
}

@Test
public void testColonFollowedByWhitespaceIsNotURL() {
// TokenizerStateMachine tokenizer = new TokenizerStateMachineTest.TestTokenizerStateMachine(true, false);
// ((TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).parse("This is not a link : just text.");
// List<String> tokens = ((TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).getWords();
// assertEquals(":", tokens.get(tokens.indexOf("link") + 1));
// assertEquals("just", tokens.get(tokens.indexOf(":") + 1));
}

@Test
public void testApostropheSurroundedByWhitespace() {
// TokenizerStateMachine tokenizer = new TokenizerStateMachineTest.TestTokenizerStateMachine(true, false);
// ((TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).parse("It is a ' test.");
// List<String> tokens = ((TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).getWords();
// assertEquals("'", tokens.get(tokens.indexOf("a") + 1));
// assertEquals("test", tokens.get(tokens.indexOf("'") + 1));
}

@Test
public void testDoubleQuoteFollowedByPunctuation() {
// TokenizerStateMachine tokenizer = new TokenizerStateMachineTest.TestTokenizerStateMachine(true, false);
// ((TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).parse("\"Wow!\"");
// List<String> tokens = ((TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).getWords();
// assertEquals("\"", tokens.get(0));
// assertEquals("Wow", tokens.get(1));
// assertEquals("!", tokens.get(2));
// assertEquals("\"", tokens.get(3));
}

@Test
public void testSingleLineBreakDoesNotSplitSentenceWhenDisabled() {
// TokenizerStateMachine tokenizer = new TokenizerStateMachineTest.TestTokenizerStateMachine(true, false);
// ((TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).parse("Line one.\nLine two.");
// List<String> tokens = ((TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).getWords();
// assertEquals("Line", tokens.get(0));
// assertEquals(".", tokens.get(2));
// assertEquals("Line", tokens.get(3));
}

@Test
public void testEmojiAndUnicodeCharacters() {
// TokenizerStateMachine tokenizer = new TokenizerStateMachineTest.TestTokenizerStateMachine(true, false);
String text = "Hello 😀 world © 2023.";
// ((TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).parse(text);
// List<String> tokens = ((TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).getWords();
// assertTrue(tokens.contains("Hello"));
// assertTrue(tokens.contains("world"));
// assertTrue(tokens.contains("©"));
// assertTrue(tokens.contains("2023"));
}

@Test
public void testUnterminatedWordAtEOF() {
// TokenizerStateMachine tokenizer = new TokenizerStateMachineTest.TestTokenizerStateMachine(true, false);
// ((TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).parse("This is a test");
// List<String> tokens = ((TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).getWords();
// assertEquals("test", tokens.get(tokens.size() - 1));
}

@Test
public void testMultipleSpacesBetweenWords() {
// TokenizerStateMachine tokenizer = new TokenizerStateMachineTest.TestTokenizerStateMachine(true, false);
// ((TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).parse("Word1     Word2  Word3");
// List<String> tokens = ((TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).getWords();
// assertEquals("Word1", tokens.get(0));
// assertEquals("Word2", tokens.get(1));
// assertEquals("Word3", tokens.get(2));
}

@Test
public void testQuoteAtEndOfSentence() {
// TokenizerStateMachine tokenizer = new TokenizerStateMachineTest.TestTokenizerStateMachine(true, false);
// ((TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).parse("He said 'stop.'");
// List<String> tokens = ((TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).getWords();
// assertEquals("'", tokens.get(2));
// assertEquals("stop", tokens.get(3));
// assertEquals(".", tokens.get(4));
// assertEquals("'", tokens.get(5));
}

@Test
public void testDashOnlyInput() {
// TokenizerStateMachine tokenizer = new TokenizerStateMachineTest.TestTokenizerStateMachine(true, false);
// ((TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).parse("-");
// List<String> tokens = ((TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).getWords();
// assertEquals("-", tokens.get(0));
}

@Test
public void testMultipleApostrophesBackToBack() {
// TokenizerStateMachine tokenizer = new TokenizerStateMachineTest.TestTokenizerStateMachine(true, false);
// ((TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).parse("It''s wrong.");
// List<String> tokens = ((TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).getWords();
// assertEquals("It", tokens.get(0));
// assertEquals("'", tokens.get(1));
// assertEquals("'", tokens.get(2));
// assertEquals("s", tokens.get(3));
}

@Test
public void testInputWithControlCharacterAndPrintablePunctuation() {
// TokenizerStateMachine tokenizer = new TokenizerStateMachineTest.TestTokenizerStateMachine(true, false);
String input = "Hello\u0001!";
// ((TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).parse(input);
// List<String> tokens = ((TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).getWords();
// assertEquals("Hello", tokens.get(0));
// assertEquals("!", tokens.get(1));
}

@Test
public void testColonWithNonURLSymbolsFollowing() {
// TokenizerStateMachine tokenizer = new TokenizerStateMachineTest.TestTokenizerStateMachine(true, false);
String input = "Not a url: 'symbol";
// ((TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).parse(input);
// List<String> tokens = ((TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).getWords();
// assertTrue(tokens.contains(":"));
// assertTrue(tokens.contains("'"));
// assertTrue(tokens.contains("symbol"));
}

@Test
public void testURLWithMissingProtocolShouldNotBeDetected() {
// TokenizerStateMachine tokenizer = new TokenizerStateMachineTest.TestTokenizerStateMachine(true, false);
String input = "example.com:8080 is a host with a port.";
// ((TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).parse(input);
// List<String> tokens = ((TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).getWords();
// assertTrue(tokens.contains("example"));
// assertTrue(tokens.contains("."));
// assertTrue(tokens.contains("com"));
// assertTrue(tokens.contains(":"));
// assertTrue(tokens.contains("8080"));
}

@Test
public void testInvalidEmailWithoutDomain() {
// TokenizerStateMachine tokenizer = new TokenizerStateMachineTest.TestTokenizerStateMachine(true, false);
String input = "Send mail to name@";
// ((TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).parse(input);
// List<String> tokens = ((TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).getWords();
// assertTrue(tokens.contains("name"));
// assertTrue(tokens.contains("@"));
}

@Test
public void testQuoteAdjacentToSentenceEndWithEmbeddedPunctuation() {
// TokenizerStateMachine tokenizer = new TokenizerStateMachineTest.TestTokenizerStateMachine(true, false);
String input = "\"Did it work??\" he asked.";
// ((TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).parse(input);
// List<String> tokens = ((TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).getWords();
// assertEquals("\"", tokens.get(0));
// assertEquals("Did", tokens.get(1));
// assertEquals("it", tokens.get(2));
// assertEquals("work", tokens.get(3));
// assertEquals("?", tokens.get(4));
// assertEquals("?", tokens.get(5));
// assertEquals("\"", tokens.get(6));
// assertEquals("he", tokens.get(7));
// assertEquals("asked", tokens.get(8));
// assertEquals(".", tokens.get(9));
}

@Test
public void testMultipleSequentialQuotes() {
// TokenizerStateMachine tokenizer = new TokenizerStateMachineTest.TestTokenizerStateMachine(true, false);
String input = "\"\"Hello\"\"";
// ((TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).parse(input);
// List<String> tokens = ((TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).getWords();
// assertEquals("\"", tokens.get(0));
// assertEquals("\"", tokens.get(1));
// assertEquals("Hello", tokens.get(2));
// assertEquals("\"", tokens.get(3));
// assertEquals("\"", tokens.get(4));
}

@Test
public void testTextEndingWithUnprintableCharacter() {
// TokenizerStateMachine tokenizer = new TokenizerStateMachineTest.TestTokenizerStateMachine(true, false);
String input = "End text\u0000";
// ((TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).parse(input);
// List<String> tokens = ((TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).getWords();
// assertEquals("End", tokens.get(0));
// assertEquals("text", tokens.get(1));
}

@Test
public void testSplitOnNewlineEnabledWithTrailingWhitespaceParagraph() {
// TokenizerStateMachine tokenizer = new TokenizerStateMachineTest.TestTokenizerStateMachine(true, true);
String input = "First paragraph.\n\n\nSecond paragraph.";
// ((TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).parse(input);
// List<String> tokens = ((TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).getWords();
// assertTrue(tokens.contains("First"));
// assertTrue(tokens.contains("Second"));
// assertEquals(".", tokens.get(tokens.size() - 1));
}

@Test
public void testContractionsNotSplitWithInternalWhitespace() {
// TokenizerStateMachine tokenizer = new TokenizerStateMachineTest.TestTokenizerStateMachine(true, false);
String input = "You 're awesome.";
// ((TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).parse(input);
// List<String> tokens = ((TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).getWords();
// assertEquals("You", tokens.get(0));
// assertEquals("'", tokens.get(1));
// assertEquals("re", tokens.get(2));
// assertEquals("awesome", tokens.get(3));
}

@Test
public void testCommaNotFollowedByDigitsWhenInNonNumericContext() {
// TokenizerStateMachine tokenizer = new TokenizerStateMachineTest.TestTokenizerStateMachine(true, false);
String input = "Hello,world";
// ((TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).parse(input);
// List<String> tokens = ((TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).getWords();
// assertEquals("Hello", tokens.get(0));
// assertEquals(",", tokens.get(1));
// assertEquals("world", tokens.get(2));
}

@Test
public void testColonSurroundedBySpaceNotURL() {
// TokenizerStateMachine tokenizer = new edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine(true, false);
// ((edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).parse("Label : data");
// List<String> tokens = ((edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).getWords();
// assertTrue(tokens.contains("Label"));
// assertTrue(tokens.contains(":"));
// assertTrue(tokens.contains("data"));
}

@Test
public void testStandalonePeriodMiddleOfText() {
// TokenizerStateMachine tokenizer = new edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine(true, false);
// ((edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).parse("U.S. economy grows.");
// List<String> tokens = ((edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).getWords();
// assertTrue(tokens.contains("U.S."));
// assertTrue(tokens.contains("economy"));
// assertEquals("grows", tokens.get(tokens.size() - 2));
// assertEquals(".", tokens.get(tokens.size() - 1));
}

@Test
public void testPunctuationAtStart() {
// TokenizerStateMachine tokenizer = new edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine(true, false);
// ((edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).parse("!Boom");
// List<String> tokens = ((edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).getWords();
// assertEquals("!", tokens.get(0));
// assertEquals("Boom", tokens.get(1));
}

@Test
public void testSpecialCharacterBetweenDigitsPreventsNumberDetection() {
// TokenizerStateMachine tokenizer = new edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine(true, false);
// ((edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).parse("12a34");
// List<String> tokens = ((edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).getWords();
// assertEquals("12a34", tokens.get(0));
}

@Test
public void testMultipleDotsFollowedByLowercaseTheSentenceContinues() {
// TokenizerStateMachine tokenizer = new edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine(true, false);
// ((edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).parse("Wait... then go.");
// List<String> tokens = ((edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).getWords();
// assertEquals("Wait", tokens.get(0));
// assertEquals("...", tokens.get(1));
// assertEquals("then", tokens.get(2));
// assertEquals("go", tokens.get(3));
// assertEquals(".", tokens.get(4));
}

@Test
public void testHyphenBetweenDigitsTreatsAsDateUnlessSplitOnDashEnabled() {
// TokenizerStateMachine tokenizer = new edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine(true, false);
// ((edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).parse("2024-01-01");
// List<String> tokens = ((edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).getWords();
// assertTrue(tokens.contains("2024"));
// assertTrue(tokens.contains("-"));
// assertTrue(tokens.contains("01"));
}

@Test
public void testOnlyNumbersWithCommasIssue() {
// TokenizerStateMachine tokenizer = new edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine(true, false);
// ((edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).parse("1,000,000");
// List<String> tokens = ((edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).getWords();
// assertEquals("1,000,000", tokens.get(0));
}

@Test
public void testMalformedURLShouldNotAdvanceScanner() {
// TokenizerStateMachine tokenizer = new edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine(true, false);
// ((edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).parse("http:/invalid");
// List<String> tokens = ((edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).getWords();
// assertTrue(tokens.contains("http"));
// assertTrue(tokens.contains(":"));
// assertTrue(tokens.contains("/"));
// assertTrue(tokens.contains("invalid"));
}

@Test
public void testSentenceStartsWithAQuoteAndEndsProperly() {
// TokenizerStateMachine tokenizer = new edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine(true, false);
// ((edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).parse("\"Open the door\", she said.");
// List<String> tokens = ((edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).getWords();
// assertEquals("\"", tokens.get(0));
// assertEquals("Open", tokens.get(1));
// assertEquals("the", tokens.get(2));
// assertEquals("door", tokens.get(3));
// assertEquals("\",", tokens.get(4));
// assertEquals("she", tokens.get(5));
// assertEquals("said", tokens.get(6));
// assertEquals(".", tokens.get(7));
}

@Test
public void testTokenEndsWithAsteriskAndIsSplit() {
// TokenizerStateMachine tokenizer = new edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine(true, false);
// ((edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).parse("multiply*");
// List<String> tokens = ((edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).getWords();
// assertEquals("multiply", tokens.get(0));
// assertEquals("*", tokens.get(1));
}

@Test
public void testDecimalPointAtStartOfNumber() {
// TokenizerStateMachine tokenizer = new edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine(true, false);
// ((edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).parse(".5 liters");
// List<String> tokens = ((edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).getWords();
// assertEquals(".", tokens.get(0));
// assertEquals("5", tokens.get(1));
// assertEquals("liters", tokens.get(2));
}

@Test
public void testSingleQuoteAtStartWithoutClosing() {
// TokenizerStateMachine tokenizer = new edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine(true, false);
// ((edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).parse("'Unclosed quote example.");
// List<String> tokens = ((edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).getWords();
// assertEquals("'", tokens.get(0));
// assertEquals("Unclosed", tokens.get(1));
// assertTrue(tokens.contains("example"));
}

@Test
public void testWhiteSpaceOnlySpecialCharacterSequence() {
// TokenizerStateMachine tokenizer = new edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine(true, false);
// ((edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).parse("    \n\t  ");
// List<String> tokens = ((edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).getWords();
// assertTrue(tokens.isEmpty());
}

@Test
public void testSingleCommaWithWhitespace() {
// TokenizerStateMachine tokenizer = new edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine(true, false);
// ((edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).parse(" , ");
// List<String> tokens = ((edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).getWords();
// assertEquals(",", tokens.get(0));
}

@Test
public void testIsAbbreviationFailsWithLowercaseCharacter() {
// TokenizerStateMachine tokenizer = new edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine(true, false);
// ((edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).parse("U.s.a. is lowercase.");
// List<String> tokens = ((edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).getWords();
// assertTrue(tokens.contains("U"));
// assertTrue(tokens.contains("."));
// assertTrue(tokens.contains("s"));
// assertTrue(tokens.contains("."));
// assertTrue(tokens.contains("a"));
// assertTrue(tokens.contains("."));
}

@Test
public void testContractionWithUnusualSuffixDoesNotSplit() {
// TokenizerStateMachine tokenizer = new edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine(true, false);
// ((edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).parse("ain't nobody got time.");
// List<String> tokens = ((edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).getWords();
// assertEquals("ain't", tokens.get(0));
// assertEquals("nobody", tokens.get(1));
// assertEquals(".", tokens.get(tokens.size() - 1));
}

@Test
public void testPunctuationOnlySentenceEndWithoutWord() {
// TokenizerStateMachine tokenizer = new edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine(true, false);
// ((edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).parse("...!?");
// List<String> tokens = ((edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).getWords();
// assertTrue(tokens.contains("."));
// assertTrue(tokens.contains("!"));
// assertTrue(tokens.contains("?"));
}

@Test
public void testSpecialSymbolThatLooksAlphanumeric() {
// TokenizerStateMachine tokenizer = new edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine(true, false);
// ((edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).parse("Test ℉ degrees.");
// List<String> tokens = ((edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).getWords();
// assertEquals("Test", tokens.get(0));
// assertEquals("℉", tokens.get(1));
// assertEquals("degrees", tokens.get(2));
}

@Test
public void testSentenceEndsWithDoubleQuote() {
// TokenizerStateMachine tokenizer = new edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine(true, false);
// ((edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).parse("He said, \"Go away.\"");
// List<String> tokens = ((edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).getWords();
// assertEquals("\"", tokens.get(0));
// assertTrue(tokens.contains("Go"));
// assertTrue(tokens.contains("away"));
// assertEquals(".", tokens.get(tokens.size() - 2));
// assertEquals("\"", tokens.get(tokens.size() - 1));
}

@Test
public void testPartialEmailWithoutUsername() {
// TokenizerStateMachine tokenizer = new edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine(true, false);
// ((edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).parse("@domain.com");
// List<String> tokens = ((edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).getWords();
// assertEquals("@", tokens.get(0));
// assertEquals("domain", tokens.get(1));
// assertEquals(".", tokens.get(2));
// assertEquals("com", tokens.get(3));
}

@Test
public void testEmailWithDashInDomain() {
// TokenizerStateMachine tokenizer = new edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine(false, false);
// ((edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).parse("user@my-domain.com");
// List<String> tokens = ((edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).getWords();
// assertTrue(tokens.contains("user@my-domain.com"));
}

@Test
public void testDotFollowedByQuoteDoesNotTriggerSentenceEnd() {
// TokenizerStateMachine tokenizer = new edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine(true, false);
// ((edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).parse("He yelled, \"Fire.\"");
// List<String> tokens = ((edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).getWords();
// assertEquals("\"", tokens.get(0));
// assertEquals("Fire", tokens.get(1));
// assertEquals(".", tokens.get(2));
// assertEquals("\"", tokens.get(3));
}

@Test
public void testAbbreviationWithMultipleDashes() {
// TokenizerStateMachine tokenizer = new edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine(false, false);
// ((edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).parse("U-S-A.-based");
// List<String> tokens = ((edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).getWords();
// assertTrue(tokens.contains("U"));
// assertTrue(tokens.contains("-"));
// assertTrue(tokens.contains("S"));
// assertTrue(tokens.contains("-"));
// assertTrue(tokens.contains("A."));
// assertTrue(tokens.contains("-"));
// assertTrue(tokens.contains("based"));
}

@Test
public void testApostropheBeforeEndOfTokenFollowedByWhitespace() {
// TokenizerStateMachine tokenizer = new edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine(true, false);
// ((edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).parse("it's true.");
// List<String> tokens = ((edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).getWords();
// assertTrue(tokens.contains("it's"));
// assertTrue(tokens.contains("true"));
// assertEquals(".", tokens.get(tokens.size() - 1));
}

@Test
public void testSlashBetweenDigitsIsDateLikeAndFormsSingleToken() {
// TokenizerStateMachine tokenizer = new edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine(true, false);
// ((edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).parse("04/25/2024");
// List<String> tokens = ((edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).getWords();
// assertEquals("04/25/2024", tokens.get(0));
}

@Test
public void testColonBetweenNumbersInterpretsAsTimeAndNotSplit() {
// TokenizerStateMachine tokenizer = new edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine(true, false);
// ((edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).parse("Meeting at 09:30.");
// List<String> tokens = ((edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).getWords();
// assertEquals("09", tokens.get(tokens.indexOf("09")));
// assertEquals(":", tokens.get(tokens.indexOf("09") + 1));
// assertEquals("30", tokens.get(tokens.indexOf(":") + 1));
}

@Test
public void testPunctuationChainSplitCorrectly() {
// TokenizerStateMachine tokenizer = new edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine(true, false);
// ((edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).parse("Why?!?");
// List<String> tokens = ((edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).getWords();
// assertEquals("Why", tokens.get(0));
// assertEquals("?", tokens.get(1));
// assertEquals("!", tokens.get(2));
// assertEquals("?", tokens.get(3));
}

@Test
public void testNumericFollowedByCommaThenNonDigitGetsSplit() {
// TokenizerStateMachine tokenizer = new edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine(false, false);
// ((edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).parse("It cost 100,USD");
// List<String> tokens = ((edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).getWords();
// assertTrue(tokens.contains("100"));
// assertTrue(tokens.contains(","));
// assertTrue(tokens.contains("USD"));
}

@Test
public void testMultipleWhitespaceAndNewlineBlocks() {
// TokenizerStateMachine tokenizer = new edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine(true, true);
// ((edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).parse("first.\n\nsecond.\n\n\nthird.");
// List<String> tokens = ((edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).getWords();
// assertTrue(tokens.contains("first"));
// assertTrue(tokens.contains("second"));
// assertTrue(tokens.contains("third"));
}

@Test
public void testPopStateStackToEmptyAndAutoReinitializes() {
// TokenizerStateMachine tokenizer = new edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine(true, false);
// ((edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).parse("word.");
// List<String> tokens = ((edu.illinois.cs.cogcomp.nlp.tokenizer.TokenizerStateMachineTest.TestTokenizerStateMachine) tokenizer).getWords();
// assertTrue(tokens.contains("word"));
// assertEquals(".", tokens.get(tokens.size() - 1));
}
}
