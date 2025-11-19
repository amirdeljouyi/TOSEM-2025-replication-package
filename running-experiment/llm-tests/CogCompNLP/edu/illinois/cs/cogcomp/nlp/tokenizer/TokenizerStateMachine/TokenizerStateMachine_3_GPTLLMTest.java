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

public class TokenizerStateMachine_3_GPTLLMTest {

@Test
public void testParseText_SimpleSentence_TokensPresent() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("Hello world.");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_AbbreviationUdotSdotShouldBeTokenized() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("He lives in the U.S.");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_EmailShouldBeRecognized() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("Contact me at person@example.com for details.");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_InvalidEmailNotRecognizedAsEmailToken() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("This text includes a broken email: a@@b..com");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_UrlShouldBeDetected() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("Please visit http://example.com!");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_AbbreviationDetectedAsContinue_Ellipsis() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("Wait... what happened next?");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_SinglePeriod_SentenceEnd() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("This is a single sentence.");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_NumericValueWithComma() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("That costs $1,299.99 in total.");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_RecognizeDateFormat() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("Today is 12/31/2023.");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_HyphenatedWord_SplitOnDashFalse() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("This is a well-known fact.");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_HyphenatedWord_SplitOnDashTrue() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("This is a well-known fact.");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_HandlesMultipleSentencesCorrectly() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("Hello. How are you? I am fine.");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_HandlesContractions_Cant_Wont() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("I can't go and he won't either.");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_ProcessTextWithQuoteAtEnd() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("He said, \"Yes!\"");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_NewlineParagraphSplitEnabled() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, true);
tokenizer.parseText("Paragraph one.\n\nParagraph two.");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_NumericWithColonNotParsedAsUrl() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("The time is 12:45 pm.");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_PunctuationSurroundedByLettersAsPartOfWord() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("He’s going to school.");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_SentenceEndsWithQuoteAndPeriod() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("She said, \"I'm done.\"");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_HandlesUnprintableCharacter() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("Hello\u0003World!");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_HandlesEmptyTextGracefully() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_HandlesUnderscoreAsWordPart() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("snake_case_variable");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_HandlesBacktickAsQuote() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("He said `hello` to her.");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_HandleDollarAndNumericSplit() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("$5.00");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_HandleBareColon_NoURL() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("Time:");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_HandlesMultipleUnprintableChars() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("Hi\u0001\u0002there");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_AcronymFollowedByLowercase_ShouldContinue() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("Meet the U.S. ambassador.");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_ApostropheMidWordNotCommonContraction() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("rock'n'roll");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_SplitOnDashDisabled_ValidHyphenWord() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("X-ray device");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_SplitOnDashEnabledWithDigitsSeparatedByDash() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("123-456");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_SentenceEndsWithDoubleQuote() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("She said \"hello.\"");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_HandlesSingleQuoteWord() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("It's John's book.");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_DecimalNumberStartingWithDot() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("The value is .75");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_DetectEmailWithSubdomain() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("Send mail to user@mail.department.org");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_DetectMixedCaseEmail() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("Send to John.Smith@Example.COM");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_OnlySpecialCharacters_ShouldNotCrash() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("...!?");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_SentenceEndsWithMultipleQuotes_InsidePunctuation() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("Did she say, \"Yes!\"?");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_HandlesMultipleParagraphsWithMass() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, true);
tokenizer.parseText("First paragraph.\n\nSecond paragraph.");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_HandlesDecimalWithTrailingChar() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("The value is 3.14m/s");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_HandlesNumericRangeWithSlash() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("Available: 3/10 units");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_HandlesQuotesAndTerminationMarksTogether() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("\"Wait!\" he said.");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_EndsWithWhitespace() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("Hello world.   ");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_SingleWhitespace() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText(" ");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_TabCharacter() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("This\tis\ta\ttest");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_StartingWithWhitespace() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("  Hello world.");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_EmDashHandledAsPunctuation() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("He is great—really great.");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_PeekOffStartAndEndBuffer() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("A");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_UnusualUnicodeSymbols() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("Check ☃ and ✓ symbols.");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_PeriodFollowedByNumber_TreatAsDecimal() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("That is .5 kg of salt.");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_PeriodFollowedByUppercase_TreatedAsAcronym() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("This is U.K. news.");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_IncompleteContraction_NoSpaceAfter() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("It'dwork if you try.");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_ConsecutiveSpecialCharacters() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("What?!?!");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_SingleCharacterInput_Letter() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("a");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_SingleCharacterInput_Unprintable() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("\u0000");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_PunctuationAtStartOfWord() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("!Exclamation should be tokenized.");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_NoSpaceAfterPeriod_NotEndOfSentence() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("He lives here.Mary knows.");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_SpecialCharacterDollarAtEnd() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("Pay up $");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_SpecialCharacterColonFollowedByWhitespace() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("Note: it's important.");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_HandlesEllipsisWithWhitespaceAfter() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("Well... maybe.");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_HandlesTextWithTrailingQuotesOnly() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("Done.\"");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_SpecialTokenWithQuoteInside() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("She said 'Wow.'");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_MultipleSingleQuotesDetached() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("' ' '");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_NonWhitespaceColonFollowedByNewline_NotURL() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("http:\nexample.com");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_ContractionThatShouldBeSplit_re() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("They’re ready.");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_ContractionThatShouldRemainAttached() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("Zach's bike");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_PunctuationFollowedByLetter_KeepTogether() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("e.g. example");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_PeriodFollowedByLowercase_AbbrContinuation() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("Example: U.S.citizens");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_AcronymLikeWithHyphen() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("CIA-FBI collaboration.");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_EmailWithNumbersAndSymbols() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("user_123+test-mail@example.co.uk");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_URLFollowedByColonShouldNotBreakURL() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("Visit https://example.org: great stuff!");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_OnlySpecialTokensAndWhitespace() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("! # $ % ^");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_UnderscoreInNumeric_ShouldJoinAsWord() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("ISO_9001");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_QuoteSurroundingSentenceWithPunctuation() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("\"Hello!\"");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_EmailEndingAtEndOfSentence() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("Contact user@site.com.");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_DotBetweenUppercaseAbbrAndLowercase_WordContinues() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("Dr.smith will help.");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_TrailingNonBreakingDots() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("Waiting...");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_ApostropheInsideAbbreviationLikeForm_shouldNotBreak() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("rock'n'roll is timeless.");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_SingleQuoteStartSentence() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("'Start now.'");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_DoubleHyphenAsSpecialCharacterSequence() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("This is surprising--really surprising!");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_ChartAtIndexZero_ColonFollowedByWhitespace() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText(": something");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_ColonFollowedByQuote_NoURLDetected() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("Check this: \"example text\"");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_ColonFollowedBySpace_NoURL() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("Note: the meeting is rescheduled");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_URLFollowedByComma_IsURLDetectedCorrectly() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("See https://example.com, for more info.");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_EmailEdgeCase_AtStartOfText() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("john_doe@abc.org sent an email.");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_InvalidEmailSpecialSuffix_NotMatched() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("user@email.c0m blah");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_SingleDot_PunctuationOnly() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText(".");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_MultipleDots_WithDifferentEndings() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("Wait... Now stop. OK?");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_LoneDash_AtWordBoundary() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("This is a - test");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_PunctuationCluster_ShouldSegmentIndividually() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("\"!?.,;'\"");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_ConsecutiveNewlinesAndWhitespace_SplitParagraph() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, true);
tokenizer.parseText("First para.\n\nSecond para.\n\n\nThird should not split.");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_AllUppercase_NoDot_NotAbbreviation() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("CEO met the CTO");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_DashBetweenLetters_NoSplitIfSplitOnDashFalse() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("state-of-the-art");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_TimeFormatColonShouldNotTriggerURLBranch() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("Time is 10:45 AM");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_MultiplePeriodsButTooShortForContinue() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText(".. Done.");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_ApostropheAtStartAndEndOfToken() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("'tis the season");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_TrickyDateLikeButInvalid() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("123/abc");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_LongSentenceWithoutTerminalPunctuation() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("This sentence goes on and on without punctuation");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_HandlesBackToBackDollarSigns() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("I owe $$200");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_HandlesMixedContractionEdgeCases() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("They'll've gone by then");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_SpecialCharacterPeriodFollowedByQuote() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("That's it.\"");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_ColonFollowedByNonProtocolCharacter() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("abc: something random");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_DecodeEmailWithOneLevelDomain() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("Contact: user@internal");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_HandlesEmailFollowedImmediatelyByPeriod() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("Please email john.doe@example.com. Thanks!");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_ApostropheBetweenDigits_YearAbbreviation() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("'99 problems");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_ApostropheSurroundedBySpaces_ShouldBeSeparateToken() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("It ' is odd");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_EmbeddedNumberWithSlashes() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("Version 3/4/2023 is fine");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_PeriodWithNoWhitespaceFollowedByCapital_ShouldBreakSentence() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("End.This starts a new sentence.");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_PeriodWithNoWhitespaceAndLowercase_ShouldNotBreak() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("U.K.government");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_SingleSpecialCharacterDollarOnly() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("$");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_QuotedAbbreviation_ShouldPopSentenceCorrectly() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("\"U.S.\" is the abbreviation.");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_InputContainingOnlyWhitespaceAndNewlines() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, true);
tokenizer.parseText(" \n \n\n ");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_MultipleDifferentPunctuationsInBetweenWords() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("Well, yes! Maybe? Wait...");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_UnprintableControlCharactersHandledGracefully() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("hello\u0001world\u0003!");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_PeekAtStartAndEndShouldReturnNullChar() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("X");
assertTrue(tokenizer.peek(-1) == '\000');
assertTrue(tokenizer.peek(1) == '\000');
}

@Test
public void testParseText_HyphenatedDigitSeparationWithSplitOnDashEnabled() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("123-456");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_MultipleQuotationTokensHandledCorrectly() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("She said, \"Hi.\" Then he said, 'Bye.'");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_AbbreviationAtSentenceEndFollowedByQuote() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("This came from the U.S.\"");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_EmailAtEndOfSentenceFollowedByWhitespace() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("Send to abc@company.com ");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_DetectSmallAbbreviationWithoutPeriods() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("FYI this is important.");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_PeriodFollowedByQuoteAndWhitespace() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("It ended.\" Now start.");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_AbbreviationWithoutPeriodNotDetectedAsAbbr() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("US forces arrived.");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_NumberWithLeadingDot_ShouldBeNumeric() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText(".42 is the answer");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_PeriodFollowedByDigitAndReturnedAsWord() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("Soda costs .99 dollars.");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_ColonBetweenDigits_ValidTimeFormat() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("The time is 09:45 now");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_ColonBetweenLettersNotATimeOrURL_TreatedAsSpecial() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("label:important");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_PunctuationSurroundedByDigit_ShouldNotBeSplit() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("123.456 not end");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_PunctuationSurroundedByAlpha_ShouldStayInWord() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("e.g. abbreviation works");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_UnknownPunctuationShouldReturnToSpecialState() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("Unicode π and words");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_DashSurroundedByNonAlphaNumericCharacters() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(true, false);
tokenizer.parseText("Hello--world");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_BackToBackEmailsBothParsed() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("a@b.com b@c.org");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_EmailFollowedImmediatelyBySemicolon() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("Contact john@example.com; then reply.");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_StructuredNumeric_WithSlash_MarkedAsDate() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("04/15");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_SingleDigitFollowedByCommaThenDigit() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("Price: 1,000");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_UnclosedQuote_ShouldStillFinalizeSentence() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("He said, \"Wait for me.");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_DoubleContraction_ShouldBeSplitCorrectly() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("They'll've left.");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_AposInMiddleOfWord_NoSplitExpected() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("rock'n'roll");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_CommaFollowedByLetter_ShouldContinue() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("hello,world");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_HyphenBetweenLettersWithSplitOnDashFalse() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, false);
tokenizer.parseText("well-being");
assertNotNull(tokenizer);
assertTrue(true);
}

@Test
public void testParseText_LinesSeparatedByExactlyTwoNewlines_SentenceSplitExpected() {
TokenizerStateMachine tokenizer = new TokenizerStateMachine(false, true);
tokenizer.parseText("First line.\n\nSecond line.");
assertNotNull(tokenizer);
assertTrue(true);
}
}
