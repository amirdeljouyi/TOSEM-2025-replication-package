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

public class TokenizerStateMachine_2_GPTLLMTest {

@Test
public void testSimpleSentenceSplitting() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
String inputText = "Hello world.";
tokenizer.parseText(inputText);
List<String> tokens = new ArrayList<>();
for (int i = 0; i < 3; i++) {
tokens.add(tokenizer.completed.get(i).getWord());
}
assertEquals("Hello", tokens.get(0));
assertEquals("world", tokens.get(1));
assertEquals(".", tokens.get(2));
assertEquals(3, tokens.size());
}

@Test
public void testEmailTokenization() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
String inputText = "Contact us at support@example.com today.";
tokenizer.parseText(inputText);
String token1 = tokenizer.completed.get(0).getWord();
String token2 = tokenizer.completed.get(1).getWord();
String token3 = tokenizer.completed.get(2).getWord();
String token4 = tokenizer.completed.get(3).getWord();
String token5 = tokenizer.completed.get(4).getWord();
String token6 = tokenizer.completed.get(5).getWord();
assertEquals("Contact", token1);
assertEquals("us", token2);
assertEquals("at", token3);
assertEquals("support@example.com", token4);
assertEquals("today", token5);
assertEquals(".", token6);
}

@Test
public void testUrlTokenization() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
String inputText = "Visit http://example.org now.";
tokenizer.parseText(inputText);
String token1 = tokenizer.completed.get(0).getWord();
String token2 = tokenizer.completed.get(1).getWord();
String token3 = tokenizer.completed.get(2).getWord();
String token4 = tokenizer.completed.get(3).getWord();
assertEquals("Visit", token1);
assertEquals("http://example.org", token2);
assertEquals("now", token3);
assertEquals(".", token4);
}

@Test
public void testContractionParsing() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
String inputText = "It's raining outside.";
tokenizer.parseText(inputText);
String token1 = tokenizer.completed.get(0).getWord();
String token2 = tokenizer.completed.get(1).getWord();
String token3 = tokenizer.completed.get(2).getWord();
String token4 = tokenizer.completed.get(3).getWord();
String token5 = tokenizer.completed.get(4).getWord();
assertEquals("It", token1);
assertEquals("'s", token2);
assertEquals("raining", token3);
assertEquals("outside", token4);
assertEquals(".", token5);
}

@Test
public void testDashSplitEnabled() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
String inputText = "High-quality tools.";
tokenizer.parseText(inputText);
String token1 = tokenizer.completed.get(0).getWord();
String token2 = tokenizer.completed.get(1).getWord();
String token3 = tokenizer.completed.get(2).getWord();
String token4 = tokenizer.completed.get(3).getWord();
String token5 = tokenizer.completed.get(4).getWord();
assertEquals("High", token1);
assertEquals("-", token2);
assertEquals("quality", token3);
assertEquals("tools", token4);
assertEquals(".", token5);
}

@Test
public void testDashSplitDisabled() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
String inputText = "High-quality tools.";
tokenizer.parseText(inputText);
String token1 = tokenizer.completed.get(0).getWord();
String token2 = tokenizer.completed.get(1).getWord();
String token3 = tokenizer.completed.get(2).getWord();
assertEquals("High-quality", token1);
assertEquals("tools", token2);
assertEquals(".", token3);
}

@Test
public void testNumbersWithCommasAndPeriods() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
String inputText = "The price is $1,234.56 today.";
tokenizer.parseText(inputText);
String token1 = tokenizer.completed.get(2).getWord();
String token2 = tokenizer.completed.get(3).getWord();
assertEquals("$", token1);
assertEquals("1,234.56", token2);
}

@Test
public void testMultipleNewlinesSplitsParagraph() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, true);
String inputText = "First paragraph.\n\nSecond paragraph.";
tokenizer.parseText(inputText);
String token1 = tokenizer.completed.get(0).getWord();
String token2 = tokenizer.completed.get(1).getWord();
String token3 = tokenizer.completed.get(2).getWord();
String token4 = tokenizer.completed.get(3).getWord();
String token5 = tokenizer.completed.get(4).getWord();
String token6 = tokenizer.completed.get(5).getWord();
assertEquals("First", token1);
assertEquals("paragraph", token2);
assertEquals(".", token3);
assertEquals("Second", token4);
assertEquals("paragraph", token5);
assertEquals(".", token6);
}

@Test
public void testApostropheInNonContraction() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
String inputText = "runnin' late again.";
tokenizer.parseText(inputText);
String token1 = tokenizer.completed.get(0).getWord();
String token2 = tokenizer.completed.get(1).getWord();
String token3 = tokenizer.completed.get(2).getWord();
String token4 = tokenizer.completed.get(3).getWord();
assertEquals("runnin'", token1);
assertEquals("late", token2);
assertEquals("again", token3);
assertEquals(".", token4);
}

@Test
public void testTimeColonFormatNotSplit() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
String inputText = "Meeting at 12:30.";
tokenizer.parseText(inputText);
String token1 = tokenizer.completed.get(2).getWord();
String token2 = tokenizer.completed.get(3).getWord();
assertEquals("12:30", token1);
assertEquals(".", token2);
}

@Test
public void testEmptyInputProducesNoTokens() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
String inputText = "";
tokenizer.parseText(inputText);
assertEquals(0, tokenizer.completed.size());
}

@Test
public void testSingleCharacterAlphabetic() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
String inputText = "A";
tokenizer.parseText(inputText);
assertEquals(1, tokenizer.completed.size());
assertEquals("A", tokenizer.completed.get(0).getWord());
}

@Test
public void testSinglePunctuationAsToken() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
String inputText = "!";
tokenizer.parseText(inputText);
assertEquals(1, tokenizer.completed.size());
assertEquals("!", tokenizer.completed.get(0).getWord());
}

@Test
public void testOnlyWhitespace() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
String inputText = "   \n\t  ";
tokenizer.parseText(inputText);
assertEquals(0, tokenizer.completed.size());
}

@Test
public void testWhitespaceBetweenSpecialCharacters() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
String inputText = "! ! !";
tokenizer.parseText(inputText);
assertEquals(3, tokenizer.completed.size());
assertEquals("!", tokenizer.completed.get(0).getWord());
assertEquals("!", tokenizer.completed.get(1).getWord());
assertEquals("!", tokenizer.completed.get(2).getWord());
}

@Test
public void testUnprintableCharactersInMiddle() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
String inputText = "Hello\u0000World";
tokenizer.parseText(inputText);
assertEquals(2, tokenizer.completed.size());
assertEquals("Hello", tokenizer.completed.get(0).getWord());
assertEquals("World", tokenizer.completed.get(1).getWord());
}

@Test
public void testUnusualApostropheCharacter() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
String inputText = "don’t stop";
tokenizer.parseText(inputText);
assertEquals("don’t", tokenizer.completed.get(0).getWord());
assertEquals("stop", tokenizer.completed.get(1).getWord());
}

@Test
public void testMultiplePeriodsAsEllipsis() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
String inputText = "Wait...";
tokenizer.parseText(inputText);
assertEquals("Wait", tokenizer.completed.get(0).getWord());
assertEquals("...", tokenizer.completed.get(1).getWord());
}

@Test
public void testColonNotFollowedByUrlButByWhitespace() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
String inputText = "Note: details to follow.";
tokenizer.parseText(inputText);
assertEquals("Note", tokenizer.completed.get(0).getWord());
assertEquals(":", tokenizer.completed.get(1).getWord());
assertEquals("details", tokenizer.completed.get(2).getWord());
}

@Test
public void testInvalidUrlStructureShouldNotBeTokenizedAsUrl() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
String inputText = "Invalid:htt p:/web";
tokenizer.parseText(inputText);
assertEquals("Invalid", tokenizer.completed.get(0).getWord());
assertEquals(":", tokenizer.completed.get(1).getWord());
assertEquals("htt", tokenizer.completed.get(2).getWord());
}

@Test
public void testAbbreviationLikeTermWithLowerCaseShouldNotBeAbbreviation() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
String inputText = "abc.def G.";
tokenizer.parseText(inputText);
assertEquals("abc", tokenizer.completed.get(0).getWord());
assertEquals(".", tokenizer.completed.get(1).getWord());
assertEquals("def", tokenizer.completed.get(2).getWord());
assertEquals("G", tokenizer.completed.get(3).getWord());
assertEquals(".", tokenizer.completed.get(4).getWord());
}

@Test
public void testMultipleSentenceTerminatorsBackToBack() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
String inputText = "What?!";
tokenizer.parseText(inputText);
assertEquals("What", tokenizer.completed.get(0).getWord());
assertEquals("?", tokenizer.completed.get(1).getWord());
assertEquals("!", tokenizer.completed.get(2).getWord());
}

@Test
public void testConsecutiveSpecialCharactersTreatedAsSingleTokens() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
String inputText = "@@ ## $$";
tokenizer.parseText(inputText);
assertEquals("@", tokenizer.completed.get(0).getWord());
assertEquals("@", tokenizer.completed.get(1).getWord());
assertEquals("#", tokenizer.completed.get(2).getWord());
assertEquals("#", tokenizer.completed.get(3).getWord());
assertEquals("$", tokenizer.completed.get(4).getWord());
assertEquals("$", tokenizer.completed.get(5).getWord());
}

@Test
public void testPopLeavesStackEmptyTriggersSentencePush() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
String inputText = "Hi.";
tokenizer.parseText(inputText);
TokenizerStateMachine.State lastState = tokenizer.stack.get(tokenizer.stack.size() - 1);
assertEquals(TokenizerState.IN_SENTENCE.ordinal(), lastState.stateIndex());
}

@Test
public void testContractionWithoutWhitespaceAfter() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("I won'tgo.");
assertEquals("I", tokenizer.completed.get(0).getWord());
assertEquals("won't", tokenizer.completed.get(1).getWord());
assertEquals("go", tokenizer.completed.get(2).getWord());
assertEquals(".", tokenizer.completed.get(3).getWord());
}

@Test
public void testPartialEmailShouldNotBeTokenizedAsEmail() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("Email me at john@ soon.");
assertEquals("john", tokenizer.completed.get(3).getWord());
assertEquals("@", tokenizer.completed.get(4).getWord());
assertEquals("soon", tokenizer.completed.get(5).getWord());
assertEquals(".", tokenizer.completed.get(6).getWord());
}

@Test
public void testColonWithInvalidURIFormat() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("data:this:is:not:a:URL");
assertEquals("data", tokenizer.completed.get(0).getWord());
assertEquals(":", tokenizer.completed.get(1).getWord());
assertEquals("this", tokenizer.completed.get(2).getWord());
assertEquals(":", tokenizer.completed.get(3).getWord());
assertEquals("is", tokenizer.completed.get(4).getWord());
}

@Test
public void testQuoteFollowedByTerminatorShouldCloseSentence() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("\"Wow.\" she said.");
assertEquals("\"", tokenizer.completed.get(0).getWord());
assertEquals("Wow", tokenizer.completed.get(1).getWord());
assertEquals(".", tokenizer.completed.get(2).getWord());
assertEquals("\"", tokenizer.completed.get(3).getWord());
assertEquals("she", tokenizer.completed.get(4).getWord());
assertEquals("said", tokenizer.completed.get(5).getWord());
assertEquals(".", tokenizer.completed.get(6).getWord());
}

@Test
public void testSentenceEnderInsideQuotePreservesAfterQuoteWord() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("He said \"No.\"Yes?");
assertEquals("He", tokenizer.completed.get(0).getWord());
assertEquals("said", tokenizer.completed.get(1).getWord());
assertEquals("\"", tokenizer.completed.get(2).getWord());
assertEquals("No", tokenizer.completed.get(3).getWord());
assertEquals(".", tokenizer.completed.get(4).getWord());
assertEquals("\"", tokenizer.completed.get(5).getWord());
assertEquals("Yes", tokenizer.completed.get(6).getWord());
assertEquals("?", tokenizer.completed.get(7).getWord());
}

@Test
public void testPeriodAtEndOfNumericShouldBeRecognized() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("That cost 5.99.");
assertEquals("5.99", tokenizer.completed.get(2).getWord());
assertEquals(".", tokenizer.completed.get(3).getWord());
}

@Test
public void testDecimalLeadingDotShouldBeTokenizedCorrectly() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("The value is .75 here.");
assertEquals(".", tokenizer.completed.get(3).getWord());
assertEquals("75", tokenizer.completed.get(4).getWord());
}

@Test
public void testMultipleQuotesInSequence() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("She said, \"Yes.\" \"No.\"");
assertEquals("\"", tokenizer.completed.get(2).getWord());
assertEquals("Yes", tokenizer.completed.get(3).getWord());
assertEquals(".", tokenizer.completed.get(4).getWord());
assertEquals("\"", tokenizer.completed.get(5).getWord());
assertEquals("\"", tokenizer.completed.get(6).getWord());
assertEquals("No", tokenizer.completed.get(7).getWord());
assertEquals(".", tokenizer.completed.get(8).getWord());
assertEquals("\"", tokenizer.completed.get(9).getWord());
}

@Test
public void testAbbreviationEndingWithPeriodBeforeWhitespace() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("I live in the U.S. today.");
assertEquals("U.S.", tokenizer.completed.get(4).getWord());
assertEquals("today", tokenizer.completed.get(5).getWord());
}

@Test
public void testNonAlphaQuotesAndDotsShouldSplitTokens() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("...'Quote'...Test");
assertEquals(".", tokenizer.completed.get(0).getWord());
assertEquals(".", tokenizer.completed.get(1).getWord());
assertEquals(".", tokenizer.completed.get(2).getWord());
assertEquals("\"", tokenizer.completed.get(3).getWord());
assertEquals("Quote", tokenizer.completed.get(4).getWord());
assertEquals("\"", tokenizer.completed.get(5).getWord());
assertEquals(".", tokenizer.completed.get(6).getWord());
assertEquals(".", tokenizer.completed.get(7).getWord());
assertEquals(".", tokenizer.completed.get(8).getWord());
assertEquals("Test", tokenizer.completed.get(9).getWord());
}

@Test
public void testIncompleteEmailWithoutDomain() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("User test@test");
assertEquals("User", tokenizer.completed.get(0).getWord());
assertEquals("test", tokenizer.completed.get(1).getWord());
assertEquals("@", tokenizer.completed.get(2).getWord());
assertEquals("test", tokenizer.completed.get(3).getWord());
}

@Test
public void testSpaceBetweenEmailAtSymbolSplitsTokens() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("Email test @domain.com");
assertEquals("test", tokenizer.completed.get(1).getWord());
assertEquals("@", tokenizer.completed.get(2).getWord());
assertEquals("domain", tokenizer.completed.get(3).getWord());
assertEquals(".", tokenizer.completed.get(4).getWord());
assertEquals("com", tokenizer.completed.get(5).getWord());
}

@Test
public void testURLFollowedBySpecialCharacter() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("Visit http://site.com!");
assertEquals("http://site.com", tokenizer.completed.get(1).getWord());
assertEquals("!", tokenizer.completed.get(2).getWord());
}

@Test
public void testMultipleConsecutiveDotsShouldBeOneToken() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("Wait.... that's unbelievable.");
assertEquals("Wait", tokenizer.completed.get(0).getWord());
assertEquals("....", tokenizer.completed.get(1).getWord());
assertEquals("that", tokenizer.completed.get(2).getWord());
}

@Test
public void testColonInNumericTime() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("The time is 08:45.");
assertEquals("08:45", tokenizer.completed.get(3).getWord());
assertEquals(".", tokenizer.completed.get(4).getWord());
}

@Test
public void testPunctuationBetweenWordsHandledCorrectly() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("Hello,world!Yes?");
assertEquals("Hello", tokenizer.completed.get(0).getWord());
assertEquals(",", tokenizer.completed.get(1).getWord());
assertEquals("world", tokenizer.completed.get(2).getWord());
assertEquals("!", tokenizer.completed.get(3).getWord());
assertEquals("Yes", tokenizer.completed.get(4).getWord());
assertEquals("?", tokenizer.completed.get(5).getWord());
}

@Test
public void testQuoteWrappedTermAtSentenceEnd() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("Did she say \"go\"?");
assertEquals("Did", tokenizer.completed.get(0).getWord());
assertEquals("she", tokenizer.completed.get(1).getWord());
assertEquals("say", tokenizer.completed.get(2).getWord());
assertEquals("\"", tokenizer.completed.get(3).getWord());
assertEquals("go", tokenizer.completed.get(4).getWord());
assertEquals("\"", tokenizer.completed.get(5).getWord());
assertEquals("?", tokenizer.completed.get(6).getWord());
}

@Test
public void testSymbolFollowedByAlphanumericTriggersWordTransition() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("#123abc!");
assertEquals("#", tokenizer.completed.get(0).getWord());
assertEquals("123abc", tokenizer.completed.get(1).getWord());
assertEquals("!", tokenizer.completed.get(2).getWord());
}

@Test
public void testDateLikeStructureWithSlashesStaysTogether() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("Today is 12/31/2024.");
assertEquals("12/31/2024", tokenizer.completed.get(2).getWord());
assertEquals(".", tokenizer.completed.get(3).getWord());
}

@Test
public void testHyphenBetweenDigitsStaysAsOneToken() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("Call 800-555-1212 now.");
assertEquals("800-555-1212", tokenizer.completed.get(1).getWord());
}

@Test
public void testAcronymLikeTokensWithoutDotsRejectedAsAbbr() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("XML is popular.");
assertEquals("XML", tokenizer.completed.get(0).getWord());
assertEquals("is", tokenizer.completed.get(1).getWord());
assertEquals("popular", tokenizer.completed.get(2).getWord());
assertEquals(".", tokenizer.completed.get(3).getWord());
}

@Test
public void testUppercaseWithPeriodsRecognizedAsAbbr() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("The U.S.A. economy...");
assertEquals("U.S.A.", tokenizer.completed.get(1).getWord());
assertEquals("economy", tokenizer.completed.get(2).getWord());
assertEquals("...", tokenizer.completed.get(3).getWord());
}

@Test
public void testPunctuationImmediatelyAfterQuoteAndDot() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("\"Wow.\"! Yes.");
assertEquals("\"", tokenizer.completed.get(0).getWord());
assertEquals("Wow", tokenizer.completed.get(1).getWord());
assertEquals(".", tokenizer.completed.get(2).getWord());
assertEquals("\"", tokenizer.completed.get(3).getWord());
assertEquals("!", tokenizer.completed.get(4).getWord());
assertEquals("Yes", tokenizer.completed.get(5).getWord());
assertEquals(".", tokenizer.completed.get(6).getWord());
}

@Test
public void testUnderscoreIsPartOfTextToken() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("snake_case_var");
assertEquals("snake_case_var", tokenizer.completed.get(0).getWord());
}

@Test
public void testTokenizationWithNullCharacterInCenter() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("abc\u0000def");
assertEquals(2, tokenizer.completed.size());
assertEquals("abc", tokenizer.completed.get(0).getWord());
assertEquals("def", tokenizer.completed.get(1).getWord());
}

@Test
public void testContractionNotInListUnbroken() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("He ain'gon do it.");
assertEquals("ain'gon", tokenizer.completed.get(1).getWord());
}

@Test
public void testPeriodFollowedByUppercaseWordEndsSentence() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("Stop.Continue");
assertEquals("Stop", tokenizer.completed.get(0).getWord());
assertEquals(".", tokenizer.completed.get(1).getWord());
assertEquals("Continue", tokenizer.completed.get(2).getWord());
}

@Test
public void testMultipleSentenceEndersWithoutWhitespace() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("What?!Now.");
assertEquals("What", tokenizer.completed.get(0).getWord());
assertEquals("?", tokenizer.completed.get(1).getWord());
assertEquals("!", tokenizer.completed.get(2).getWord());
assertEquals("Now", tokenizer.completed.get(3).getWord());
assertEquals(".", tokenizer.completed.get(4).getWord());
}

@Test
public void testSymbolTerminatedEmailShouldStopAtSymbol() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("Reach me at user@example.com!");
assertEquals("user@example.com", tokenizer.completed.get(3).getWord());
assertEquals("!", tokenizer.completed.get(4).getWord());
}

@Test
public void testPunctuationRightAfterAbbreviationDoesNotSplit() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("I know the U.S.Test passed.");
assertEquals("U.S.", tokenizer.completed.get(3).getWord());
assertEquals("Test", tokenizer.completed.get(4).getWord());
}

@Test
public void testDotFollowedByDigitExtendsNumeric() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("Version 2.5 is out.");
assertEquals("2.5", tokenizer.completed.get(1).getWord());
assertEquals("is", tokenizer.completed.get(2).getWord());
}

@Test
public void testColonWithInvalidProtoDoesNotFormURL() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("data : somethingElse");
assertEquals("data", tokenizer.completed.get(0).getWord());
assertEquals(":", tokenizer.completed.get(1).getWord());
assertEquals("somethingElse", tokenizer.completed.get(2).getWord());
}

@Test
public void testMultipleAtSymbolsPreventsEmailParsing() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("Send to name@@site.com");
assertEquals("name", tokenizer.completed.get(2).getWord());
assertEquals("@", tokenizer.completed.get(3).getWord());
assertEquals("@", tokenizer.completed.get(4).getWord());
assertEquals("site", tokenizer.completed.get(5).getWord());
}

@Test
public void testSpecialDollarFollowedByDigitStartsWord() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("The cost is $5.99.");
assertEquals("$", tokenizer.completed.get(3).getWord());
assertEquals("5.99", tokenizer.completed.get(4).getWord());
assertEquals(".", tokenizer.completed.get(5).getWord());
}

@Test
public void testSpecialDollarFollowedByNonDigitStaysSpecial() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("Handle the $value carefully.");
assertEquals("$", tokenizer.completed.get(3).getWord());
assertEquals("value", tokenizer.completed.get(4).getWord());
}

@Test
public void testHyphenNotSplittingBetweenDigits() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("Serial 2023-05-12");
assertEquals("2023-05-12", tokenizer.completed.get(1).getWord());
}

@Test
public void testDotBetweenAlphaAndDigitEndsWord() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("abc.123 is OK.");
assertEquals("abc", tokenizer.completed.get(0).getWord());
assertEquals(".", tokenizer.completed.get(1).getWord());
assertEquals("123", tokenizer.completed.get(2).getWord());
}

@Test
public void testUnclosedQuoteAllowsParsingToContinue() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("\"Unclosed quote here. Next");
assertEquals("\"", tokenizer.completed.get(0).getWord());
assertEquals("Unclosed", tokenizer.completed.get(1).getWord());
assertEquals("quote", tokenizer.completed.get(2).getWord());
assertEquals("here", tokenizer.completed.get(3).getWord());
assertEquals(".", tokenizer.completed.get(4).getWord());
assertEquals("Next", tokenizer.completed.get(5).getWord());
}

@Test
public void testPeekBoundaryNegativeOffsetReturnsNullChar() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("A");
char peeked = tokenizer.peek(-5);
assertEquals('\000', peeked);
}

@Test
public void testEmptyQuoteTokenShouldBeHandled() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("\"\"");
assertEquals("\"", tokenizer.completed.get(0).getWord());
assertEquals("\"", tokenizer.completed.get(1).getWord());
}

@Test
public void testPunctuationInsideQuoteAndFollowedByWhitespaceEndsSentence() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("\"End.\" Next");
assertEquals("\"", tokenizer.completed.get(0).getWord());
assertEquals("End", tokenizer.completed.get(1).getWord());
assertEquals(".", tokenizer.completed.get(2).getWord());
assertEquals("\"", tokenizer.completed.get(3).getWord());
assertEquals("Next", tokenizer.completed.get(4).getWord());
}

@Test
public void testUncommonUnicodeSymbolIgnoredAsUnprintable() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("Alpha \uFFF0 Omega");
assertEquals("Alpha", tokenizer.completed.get(0).getWord());
assertEquals("Omega", tokenizer.completed.get(1).getWord());
}

@Test
public void testMultipleSpacesBetweenTokensHandledCorrectly() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("Hello     world!");
assertEquals("Hello", tokenizer.completed.get(0).getWord());
assertEquals("world", tokenizer.completed.get(1).getWord());
assertEquals("!", tokenizer.completed.get(2).getWord());
}

@Test
public void testPunctuationBetweenNumbersWithoutSpaceIsParsedAsSpecial() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("123,456");
assertEquals("123,456", tokenizer.completed.get(0).getWord());
}

@Test
public void testPunctuationSplitsWordUnlessNumeric() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("value, here");
assertEquals("value", tokenizer.completed.get(0).getWord());
assertEquals(",", tokenizer.completed.get(1).getWord());
assertEquals("here", tokenizer.completed.get(2).getWord());
}

@Test
public void testEllipsisOfMoreThanThreePeriodsIsStillValid() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("Wait.....");
assertEquals("Wait", tokenizer.completed.get(0).getWord());
assertEquals(".....", tokenizer.completed.get(1).getWord());
}

@Test
public void testTextFollowedByColonWithoutURLShouldSplit() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("Note: this is important.");
assertEquals("Note", tokenizer.completed.get(0).getWord());
assertEquals(":", tokenizer.completed.get(1).getWord());
assertEquals("this", tokenizer.completed.get(2).getWord());
}

@Test
public void testSingleCharacterSpecialIsIsolatedToken() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("$");
assertEquals("$", tokenizer.completed.get(0).getWord());
}

@Test
public void testRegularWordFollowedByBacktickIsSeparated() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("test`code");
assertEquals("test", tokenizer.completed.get(0).getWord());
assertEquals("`", tokenizer.completed.get(1).getWord());
assertEquals("code", tokenizer.completed.get(2).getWord());
}

@Test
public void testColonSurroundedByDigitsInterpretedAsTime() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("It is 9:30.");
assertEquals("9:30", tokenizer.completed.get(2).getWord());
assertEquals(".", tokenizer.completed.get(3).getWord());
}

@Test
public void testPeriodAfterAbbreviationFollowedByCapitalLetterSplitsCorrectly() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("Dr.John is here.");
assertEquals("Dr.", tokenizer.completed.get(0).getWord());
assertEquals("John", tokenizer.completed.get(1).getWord());
}

@Test
public void testContractionInLowercasePreservesAsOneWhenUnknown() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("don'ta move");
assertEquals("don'ta", tokenizer.completed.get(0).getWord());
assertEquals("move", tokenizer.completed.get(1).getWord());
}

@Test
public void testMultipleUnprintablesOnlyReturnsWords() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("Hello\u0000\u0001World");
assertEquals("Hello", tokenizer.completed.get(0).getWord());
assertEquals("World", tokenizer.completed.get(1).getWord());
}

@Test
public void testWordWithSingleQuoteBetweenAlphabeticCharsWithoutWhitespaceIsUnbroken() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("jack'o'lantern");
assertEquals("jack'o'lantern", tokenizer.completed.get(0).getWord());
}

@Test
public void testPeekBeyondTextLengthReturnsNullChar() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("End");
char peeked = tokenizer.peek(10);
assertEquals('\000', peeked);
}

@Test
public void testHyphenSurroundedByLettersWithSplitOnDashTrueSplitsTokens() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("state-of-the-art");
assertEquals("state", tokenizer.completed.get(0).getWord());
assertEquals("-", tokenizer.completed.get(1).getWord());
assertEquals("of", tokenizer.completed.get(2).getWord());
assertEquals("-", tokenizer.completed.get(3).getWord());
assertEquals("the", tokenizer.completed.get(4).getWord());
assertEquals("-", tokenizer.completed.get(5).getWord());
assertEquals("art", tokenizer.completed.get(6).getWord());
}

@Test
public void testHyphenSurroundedByLettersWithSplitOnDashFalsePreservesWord() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("state-of-the-art");
assertEquals("state-of-the-art", tokenizer.completed.get(0).getWord());
}

@Test
public void testDoubleQuotesWithNoContentYieldsTwoTokens() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("\"\"");
assertEquals("\"", tokenizer.completed.get(0).getWord());
assertEquals("\"", tokenizer.completed.get(1).getWord());
}

@Test
public void testEmailWithTrailingDotStillValid() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("Contact user@domain.com.");
assertEquals("user@domain.com", tokenizer.completed.get(1).getWord());
assertEquals(".", tokenizer.completed.get(2).getWord());
}

@Test
public void testAcronymEndsWithDotFollowedByLowercaseDoesNotSplitSentence() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("From the U.S. embassy in Kenya.");
assertEquals("U.S.", tokenizer.completed.get(3).getWord());
assertEquals("embassy", tokenizer.completed.get(4).getWord());
}

@Test
public void testContractionWithWhitespaceBetweenQuoteAndNextWordSplitsCorrectly() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("We're going to win.");
assertEquals("We", tokenizer.completed.get(0).getWord());
assertEquals("'re", tokenizer.completed.get(1).getWord());
}

@Test
public void testPunctuationSequenceOfDifferentSymbolsTreatedAsSeparateTokens() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("Hello!?");
assertEquals("Hello", tokenizer.completed.get(0).getWord());
assertEquals("!", tokenizer.completed.get(1).getWord());
assertEquals("?", tokenizer.completed.get(2).getWord());
}

@Test
public void testDecimalStartingWithDotIsSplit() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("Cost is .75 today.");
assertEquals(".", tokenizer.completed.get(2).getWord());
assertEquals("75", tokenizer.completed.get(3).getWord());
}

@Test
public void testSpecialCharacterSequenceTriggersTransition() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("@@@");
assertEquals("@", tokenizer.completed.get(0).getWord());
assertEquals("@", tokenizer.completed.get(1).getWord());
assertEquals("@", tokenizer.completed.get(2).getWord());
}

@Test
public void testMultipleNewlinesBetweenWordsWithSplitOnTwoNewlinesTrueSplitsSentence() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, true);
tokenizer.parseText("Part one.\n\nPart two.");
assertEquals("Part", tokenizer.completed.get(0).getWord());
assertEquals("one", tokenizer.completed.get(1).getWord());
assertEquals(".", tokenizer.completed.get(2).getWord());
assertEquals("Part", tokenizer.completed.get(3).getWord());
assertEquals("two", tokenizer.completed.get(4).getWord());
assertEquals(".", tokenizer.completed.get(5).getWord());
}

@Test
public void testSpecialQuoteThenEllipsisWorksCorrectly() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("He said: \"Wait...\"");
assertEquals("He", tokenizer.completed.get(0).getWord());
assertEquals("said", tokenizer.completed.get(1).getWord());
assertEquals(":", tokenizer.completed.get(2).getWord());
assertEquals("\"", tokenizer.completed.get(3).getWord());
assertEquals("Wait", tokenizer.completed.get(4).getWord());
assertEquals("...", tokenizer.completed.get(5).getWord());
assertEquals("\"", tokenizer.completed.get(6).getWord());
}

@Test
public void testColonFollowedByWhitespaceNotTreatedAsURL() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("Reason: test begins");
assertEquals("Reason", tokenizer.completed.get(0).getWord());
assertEquals(":", tokenizer.completed.get(1).getWord());
assertEquals("test", tokenizer.completed.get(2).getWord());
}

@Test
public void testTimeLikeFormatWithColonPreserved() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("Arrive at 10:45.");
assertEquals("10:45", tokenizer.completed.get(2).getWord());
assertEquals(".", tokenizer.completed.get(3).getWord());
}

@Test
public void testMultipleDotsAndUppercaseNextWordEndsSentence() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("Wait... Start now.");
assertEquals("Wait", tokenizer.completed.get(0).getWord());
assertEquals("...", tokenizer.completed.get(1).getWord());
assertEquals("Start", tokenizer.completed.get(2).getWord());
assertEquals("now", tokenizer.completed.get(3).getWord());
assertEquals(".", tokenizer.completed.get(4).getWord());
}

@Test
public void testNumericWithCommaFollowedBySpaceIsSplitCorrectly() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("Amount: 1,000 dollars.");
assertEquals("1,000", tokenizer.completed.get(1).getWord());
}
}
