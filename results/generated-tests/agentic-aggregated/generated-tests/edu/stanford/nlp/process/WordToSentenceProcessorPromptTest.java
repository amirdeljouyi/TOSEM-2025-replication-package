package edu.stanford.nlp.process;

import edu.stanford.nlp.ling.BasicDocument;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.Document;
import edu.stanford.nlp.ling.MultiTokenTag;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.ling.tokensregex.TokenSequencePattern;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.PatternSyntaxException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class WordToSentenceProcessorPromptTest {

  @Test
  public void defaultProcessorSplitsEnglishAndUnicodeTerminators() {
    WordToSentenceProcessor<String> processor = new WordToSentenceProcessor<>();
    List<String> input = Arrays.asList("One", ".", "Two", "?!", "三", "。", "四", "！？");

    List<List<String>> result = processor.process(input);

    assertEquals(Arrays.asList(
        Arrays.asList("One", "."),
        Arrays.asList("Two", "?!"),
        Arrays.asList("三", "。"),
        Arrays.asList("四", "！？")), result);
  }

  @Test
  public void defaultProcessorKeepsBoundaryFollowersWithPreviousSentence() {
    WordToSentenceProcessor<String> processor = new WordToSentenceProcessor<>();
    List<String> input = Arrays.asList("He", "left", ".", ")", "''", "-RRB-", "Then", "?", "]", "Done");

    List<List<String>> result = processor.process(input);

    assertEquals(Arrays.asList(
        Arrays.asList("He", "left", ".", ")", "''", "-RRB-"),
        Arrays.asList("Then", "?", "]"),
        Collections.singletonList("Done")), result);
  }

  @Test
  public void followerAtDocumentStartRemainsInFirstSentence() {
    WordToSentenceProcessor<String> processor = new WordToSentenceProcessor<>();

    List<List<String>> result = processor.process(Arrays.asList(")", "Opening", "."));

    assertEquals(Collections.singletonList(Arrays.asList(")", "Opening", ".")), result);
  }

  @Test
  public void consecutiveBoundaryTokensProduceSeparateSentences() {
    WordToSentenceProcessor<String> processor = new WordToSentenceProcessor<>();

    List<List<String>> result = processor.process(Arrays.asList("Wait", ".", "!", "Why", "?"));

    assertEquals(Arrays.asList(
        Arrays.asList("Wait", "."),
        Collections.singletonList("!"),
        Arrays.asList("Why", "?")), result);
  }

  @Test
  public void unterminatedTailIsReturnedAtEndOfInput() {
    WordToSentenceProcessor<String> processor = new WordToSentenceProcessor<>();

    List<List<String>> result = processor.process(Arrays.asList("No", "terminator"));

    assertEquals(Collections.singletonList(Arrays.asList("No", "terminator")), result);
  }

  @Test
  public void ordinaryEmptyInputProducesNoSentences() {
    WordToSentenceProcessor<String> processor = new WordToSentenceProcessor<>();

    List<List<String>> result = processor.process(Collections.emptyList());

    assertTrue(result.isEmpty());
  }

  @Test
  public void oneSentenceModePreservesEverythingIncludingSeparators() {
    WordToSentenceProcessor<String> processor = new WordToSentenceProcessor<>(true);
    List<String> input = new ArrayList<>(Arrays.asList("One", ".", "*NL*", "Two", "!"));

    List<List<String>> result = processor.process(input);

    assertEquals(Collections.singletonList(input), result);
    assertNotSame(input, result.get(0));
  }

  @Test
  public void oneSentenceModeReturnsOneEmptySentenceForEmptyInput() {
    WordToSentenceProcessor<String> processor = new WordToSentenceProcessor<>(true);

    List<List<String>> result = processor.process(Collections.emptyList());

    assertEquals(1, result.size());
    assertTrue(result.get(0).isEmpty());
  }

  @Test
  public void neverNewlineModeDiscardsNewlinesWithoutSplitting() {
    WordToSentenceProcessor<String> processor =
        new WordToSentenceProcessor<>(WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER);

    List<List<String>> result = processor.process(Arrays.asList("alpha", "*NL*", "beta", "*NL*", "gamma"));

    assertEquals(Collections.singletonList(Arrays.asList("alpha", "beta", "gamma")), result);
  }

  @Test
  public void alwaysNewlineModeSplitsAtEveryNewline() {
    WordToSentenceProcessor<String> processor =
        new WordToSentenceProcessor<>(WordToSentenceProcessor.NewlineIsSentenceBreak.ALWAYS);

    List<List<String>> result = processor.process(Arrays.asList("alpha", "*NL*", "beta", "*NL*", "gamma"));

    assertEquals(Arrays.asList(
        Collections.singletonList("alpha"),
        Collections.singletonList("beta"),
        Collections.singletonList("gamma")), result);
  }

  @Test
  public void twoConsecutiveModeIgnoresSingleButSplitsOnPair() {
    WordToSentenceProcessor<String> processor =
        new WordToSentenceProcessor<>(WordToSentenceProcessor.NewlineIsSentenceBreak.TWO_CONSECUTIVE);

    List<List<String>> result = processor.process(Arrays.asList(
        "a", "*NL*", "b", "*NL*", "*NL*", "c", "*NL*", "d"));

    assertEquals(Arrays.asList(
        Arrays.asList("a", "b"),
        Arrays.asList("c", "d")), result);
  }

  @Test
  public void discardedBoundaryConstructorPreservesEmptyLines() {
    Set<String> discarded = new HashSet<>(Collections.singletonList("LINE"));
    WordToSentenceProcessor<String> processor = new WordToSentenceProcessor<>(discarded);

    List<List<String>> result = processor.process(Arrays.asList("first", "LINE", "LINE", "third", "LINE"));

    assertEquals(3, result.size());
    assertEquals(Collections.singletonList("first"), result.get(0));
    assertTrue(result.get(1).isEmpty());
    assertEquals(Collections.singletonList("third"), result.get(2));
  }

  @Test
  public void discardedBoundaryConstructorCanRepresentLeadingEmptyLine() {
    WordToSentenceProcessor<String> processor =
        new WordToSentenceProcessor<>(new HashSet<>(Collections.singletonList("CUT")));

    List<List<String>> result = processor.process(Arrays.asList("CUT", "value"));

    assertEquals(2, result.size());
    assertTrue(result.get(0).isEmpty());
    assertEquals(Collections.singletonList("value"), result.get(1));
  }

  @Test
  public void xmlBreakElementsAreCaseInsensitiveAndAcceptAttributes() {
    Set<String> xmlElements = new HashSet<>(Collections.singletonList("p"));
    WordToSentenceProcessor<String> processor = new WordToSentenceProcessor<>(
        "[.!?]", "[)]", Collections.singleton("*NL*"), xmlElements,
        WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER, null, null);

    List<List<String>> result = processor.process(Arrays.asList(
        "<P class='lead'>", "First", "</p>", "<p/>", "Second", "."));

    assertEquals(Arrays.asList(
        Collections.singletonList("First"),
        Arrays.asList("Second", ".")), result);
  }

  @Test
  public void regionFilteringDropsTagsAndAllOutsideTokens() {
    WordToSentenceProcessor<String> processor = new WordToSentenceProcessor<>(
        "[.!?]", "[)]", Collections.singleton("*NL*"), Collections.emptySet(),
        "(?:text|body)", WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
        null, null, false, false);

    List<List<String>> result = processor.process(Arrays.asList(
        "outside", ".", "<text id='1'>", "Inside", ".", "</text>",
        "ignored", "<body>", "More", "</body>", "trailing"));

    assertEquals(Arrays.asList(
        Arrays.asList("Inside", "."),
        Collections.singletonList("More")), result);
  }

  @Test
  public void tokenDiscardPatternsRemoveMatchesWithoutCreatingBoundaries() {
    Set<String> discardPatterns = new HashSet<>(Arrays.asList("<[^>]+>", "SKIP\\d+"));
    WordToSentenceProcessor<String> processor = new WordToSentenceProcessor<>(
        "[.!?]", "[)]", Collections.singleton("*NL*"), Collections.emptySet(),
        null, WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
        null, discardPatterns, false, false);

    List<List<String>> result = processor.process(Arrays.asList(
        "A", "<b>", "SKIP12", "B", ".", "SKIP9", "C"));

    assertEquals(Arrays.asList(
        Arrays.asList("A", "B", "."),
        Collections.singletonList("C")), result);
  }

  @Test
  public void customBoundaryAndFollowerRegexesAreHonored() {
    WordToSentenceProcessor<String> processor = new WordToSentenceProcessor<>(
        "END|STOP", "CLOSE", Collections.singleton("NL"), Collections.emptySet(),
        WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER, null, null);

    List<List<String>> result = processor.process(Arrays.asList(
        "a", ".", "END", "CLOSE", "b", "STOP", "c"));

    assertEquals(Arrays.asList(
        Arrays.asList("a", ".", "END", "CLOSE"),
        Arrays.asList("b", "STOP"),
        Collections.singletonList("c")), result);
  }

  @Test
  public void nullConvenienceArgumentsSelectDocumentedDefaults() {
    WordToSentenceProcessor<String> processor = new WordToSentenceProcessor<>(
        null, null, null, null,
        WordToSentenceProcessor.NewlineIsSentenceBreak.ALWAYS, null, null);

    List<List<String>> result = processor.process(Arrays.asList("A", ".", ")", "B", "*NL*", "C", "!"));

    assertEquals(Arrays.asList(
        Arrays.asList("A", ".", ")"),
        Collections.singletonList("B"),
        Arrays.asList("C", "!")), result);
  }

  @Test
  public void emptyDiscardSetInConvenienceConstructorAlsoSelectsDefaults() {
    WordToSentenceProcessor<String> processor = new WordToSentenceProcessor<>(
        "[.]", null, Collections.emptySet(), Collections.emptySet(),
        WordToSentenceProcessor.NewlineIsSentenceBreak.ALWAYS, null, null);

    List<List<String>> result = processor.process(Arrays.asList("A", "*NL*", "B"));

    assertEquals(Arrays.asList(Collections.singletonList("A"), Collections.singletonList("B")), result);
  }

  @Test
  public void forcedSentenceEndAnnotationSplitsEvenWithoutPunctuation() {
    CoreLabel first = CoreLabel.wordFromString("first");
    CoreLabel forced = CoreLabel.wordFromString("forced");
    forced.set(CoreAnnotations.ForcedSentenceEndAnnotation.class, true);
    CoreLabel last = CoreLabel.wordFromString("last");
    WordToSentenceProcessor<CoreLabel> processor = new WordToSentenceProcessor<>();

    List<List<CoreLabel>> result = processor.process(Arrays.asList(first, forced, last));

    assertEquals(2, result.size());
    assertEquals(Arrays.asList(first, forced), result.get(0));
    assertEquals(Collections.singletonList(last), result.get(1));
    assertSame(forced, result.get(0).get(1));
  }

  @Test
  public void paragraphSeparatorOriginalTextForcesSentenceEnd() {
    CoreLabel first = CoreLabel.wordFromString("first");
    CoreLabel separator = CoreLabel.wordFromString("visible");
    separator.setOriginalText("\u2029");
    CoreLabel last = CoreLabel.wordFromString("last");
    WordToSentenceProcessor<CoreLabel> processor = new WordToSentenceProcessor<>();

    List<List<CoreLabel>> result = processor.process(Arrays.asList(first, separator, last));

    assertEquals(Arrays.asList(Arrays.asList(first, separator), Collections.singletonList(last)), result);
  }

  @Test
  public void forcedUntilEndSuppressesPunctuationAndNewlineSplits() {
    CoreLabel first = CoreLabel.wordFromString("begin");
    first.set(CoreAnnotations.ForcedSentenceUntilEndAnnotation.class, true);
    CoreLabel punctuation = CoreLabel.wordFromString(".");
    CoreLabel newline = CoreLabel.wordFromString("*NL*");
    CoreLabel end = CoreLabel.wordFromString("finish");
    end.set(CoreAnnotations.ForcedSentenceEndAnnotation.class, true);
    CoreLabel tail = CoreLabel.wordFromString("tail");
    WordToSentenceProcessor<CoreLabel> processor =
        new WordToSentenceProcessor<>(WordToSentenceProcessor.NewlineIsSentenceBreak.ALWAYS);

    List<List<CoreLabel>> result = processor.process(Arrays.asList(first, punctuation, newline, end, tail));

    assertEquals(2, result.size());
    assertEquals(Arrays.asList(first, punctuation, end), result.get(0));
    assertEquals(Collections.singletonList(tail), result.get(1));
  }

  @Test
  public void multiTokenMentionSuppressesBoundaryUntilMentionEnd() {
    MultiTokenTag.Tag tag = new MultiTokenTag.Tag("entity", "kind", 2);
    CoreLabel mentionStart = CoreLabel.wordFromString("Acme.");
    mentionStart.set(CoreAnnotations.MentionTokenAnnotation.class, new MultiTokenTag(tag, 0));
    CoreLabel mentionEnd = CoreLabel.wordFromString("Ltd");
    mentionEnd.set(CoreAnnotations.MentionTokenAnnotation.class, new MultiTokenTag(tag, 1));
    CoreLabel punctuation = CoreLabel.wordFromString(".");
    WordToSentenceProcessor<CoreLabel> processor = new WordToSentenceProcessor<>(
        ".*\\.", "[)]", Collections.singleton("*NL*"), Collections.emptySet(),
        WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER, null, null);

    List<List<CoreLabel>> result = processor.process(Arrays.asList(mentionStart, mentionEnd, punctuation));

    assertEquals(1, result.size());
    assertEquals(Arrays.asList(mentionStart, mentionEnd, punctuation), result.get(0));
  }

  @Test
  public void tokenSequencePatternEndsSentenceAtLastMatchedToken() {
    CoreLabel first = CoreLabel.wordFromString("first");
    CoreLabel breakToken = CoreLabel.wordFromString("BREAK");
    CoreLabel hereToken = CoreLabel.wordFromString("HERE");
    CoreLabel last = CoreLabel.wordFromString("last");
    TokenSequencePattern pattern = TokenSequencePattern.compile("[{word:BREAK}] [{word:HERE}]");
    WordToSentenceProcessor<CoreLabel> processor = new WordToSentenceProcessor<>(
        "[.]", "[)]", Collections.singleton("*NL*"), Collections.emptySet(),
        WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER, pattern, null);

    List<List<CoreLabel>> result = processor.process(Arrays.asList(first, breakToken, hereToken, last));

    assertEquals(2, result.size());
    assertEquals(Arrays.asList(first, breakToken, hereToken), result.get(0));
    assertEquals(Collections.singletonList(last), result.get(1));
  }

  @Test
  public void quoteFollowerOnlyAttachesWhenItClosesAnOpenQuote() {
    Word open = new Word("\"");
    Word speech = new Word("Hello");
    Word period = new Word(".");
    Word close = new Word("\"");
    Word statement = new Word("Next");
    Word secondPeriod = new Word(".");
    Word orphanQuote = new Word("\"");
    Word tail = new Word("Tail");
    WordToSentenceProcessor<Word> processor = new WordToSentenceProcessor<>();

    List<List<Word>> result = processor.process(Arrays.asList(
        open, speech, period, close, statement, secondPeriod, orphanQuote, tail));

    assertEquals(3, result.size());
    assertEquals(Arrays.asList(open, speech, period, close), result.get(0));
    assertEquals(Arrays.asList(statement, secondPeriod), result.get(1));
    assertEquals(Arrays.asList(orphanQuote, tail), result.get(2));
  }

  @Test
  public void processDocumentPreservesMetadataAndDoesNotMutateInput() {
    Word one = new Word("One");
    Word period = new Word(".");
    Word two = new Word("Two");
    BasicDocument<String> input = new BasicDocument<>(Arrays.asList(one, period, two));
    input.setTitle("sample-title");
    WordToSentenceProcessor<Word> processor = new WordToSentenceProcessor<>();

    Document<String, Word, List<Word>> result = processor.processDocument(input);

    assertEquals("sample-title", result.title());
    assertEquals(2, result.size());
    assertEquals(Arrays.asList(one, period), result.get(0));
    assertEquals(Collections.singletonList(two), result.get(1));
    assertEquals(3, input.size());
    assertNotSame(input, result);
  }

  @Test
  public void processReturnsFreshSentenceListsOnRepeatedCalls() {
    WordToSentenceProcessor<String> processor = new WordToSentenceProcessor<>();
    List<String> input = new ArrayList<>(Arrays.asList("A", ".", "B"));

    List<List<String>> first = processor.process(input);
    List<List<String>> second = processor.process(input);
    first.get(0).add("changed");

    assertEquals(Arrays.asList("A", ".", "changed"), first.get(0));
    assertEquals(Arrays.asList("A", "."), second.get(0));
    assertFalse(second.get(0).contains("changed"));
  }

  @Test
  public void stringToNewlineModeRecognizesAllSupportedNames() {
    assertSame(WordToSentenceProcessor.NewlineIsSentenceBreak.ALWAYS,
        WordToSentenceProcessor.stringToNewlineIsSentenceBreak("always"));
    assertSame(WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
        WordToSentenceProcessor.stringToNewlineIsSentenceBreak("never"));
    assertSame(WordToSentenceProcessor.NewlineIsSentenceBreak.TWO_CONSECUTIVE,
        WordToSentenceProcessor.stringToNewlineIsSentenceBreak("two"));
    assertSame(WordToSentenceProcessor.NewlineIsSentenceBreak.TWO_CONSECUTIVE,
        WordToSentenceProcessor.stringToNewlineIsSentenceBreak("twoConsecutive"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void stringToNewlineModeRejectsUnknownName() {
    WordToSentenceProcessor.stringToNewlineIsSentenceBreak("sometimes");
  }

  @Test(expected = IllegalArgumentException.class)
  public void stringToNewlineModeRejectsNull() {
    WordToSentenceProcessor.stringToNewlineIsSentenceBreak(null);
  }

  @Test(expected = PatternSyntaxException.class)
  public void constructorRejectsInvalidBoundaryRegex() {
    new WordToSentenceProcessor<String>("[", WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER, false);
  }

  @Test(expected = NullPointerException.class)
  public void fullConstructorRejectsNullDiscardSet() {
    new WordToSentenceProcessor<String>(
        "[.]", "[)]", null, Collections.emptySet(), null,
        WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER, null, null, false, false);
  }

  @Test(expected = RuntimeException.class)
  public void processRejectsUnsupportedTokenType() {
    WordToSentenceProcessor<Integer> processor = new WordToSentenceProcessor<>();

    processor.process(Collections.singletonList(17));
  }
}
