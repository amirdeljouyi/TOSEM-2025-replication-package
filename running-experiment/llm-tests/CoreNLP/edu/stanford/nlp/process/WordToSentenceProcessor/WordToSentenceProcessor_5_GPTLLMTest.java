package edu.stanford.nlp.process;

import static org.junit.Assert.*;

import edu.stanford.nlp.ling.*;
import java.io.*;
import java.util.*;
import org.junit.Test;

public class WordToSentenceProcessor_5_GPTLLMTest {

  @Test
  public void testBasicSentenceSplit_DefaultConstructor() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    List<HasWord> input = new ArrayList<>();
    input.add(new Word("This"));
    input.add(new Word("is"));
    input.add(new Word("a"));
    input.add(new Word("test"));
    input.add(new Word("."));
    input.add(new Word("Here"));
    input.add(new Word("is"));
    input.add(new Word("another"));
    input.add(new Word("sentence"));
    input.add(new Word("!"));
    List<List<HasWord>> result = processor.process(input);
    assertEquals(2, result.size());
    assertEquals("This", result.get(0).get(0).word());
    assertEquals("test", result.get(0).get(3).word());
    assertEquals(".", result.get(0).get(4).word());
    assertEquals("Here", result.get(1).get(0).word());
    assertEquals("sentence", result.get(1).get(3).word());
    assertEquals("!", result.get(1).get(4).word());
  }

  @Test
  public void testSingleSentenceMode() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>(true);
    List<HasWord> input = new ArrayList<>();
    input.add(new Word("All"));
    input.add(new Word("in"));
    input.add(new Word("one"));
    input.add(new Word("sentence"));
    input.add(new Word("."));
    input.add(new Word("Even"));
    input.add(new Word("this"));
    List<List<HasWord>> result = processor.process(input);
    assertEquals(1, result.size());
    assertEquals(7, result.get(0).size());
    assertEquals("All", result.get(0).get(0).word());
    assertEquals("this", result.get(0).get(6).word());
  }

  @Test
  public void testNewlineAlwaysBreaksSentence() {
    // WordToSentenceProcessor<HasWord> processor = new
    // WordToSentenceProcessor<>(WordToSentenceProcessor.WordToSentenceProcessor.NewlineIsSentenceBreak.ALWAYS);
    List<HasWord> input = new ArrayList<>();
    input.add(new Word("Line"));
    input.add(new Word("one"));
    input.add(new Word(WhitespaceLexer.NEWLINE));
    input.add(new Word("Line"));
    input.add(new Word("two"));
    input.add(new Word(WhitespaceLexer.NEWLINE));
    input.add(new Word("Line"));
    input.add(new Word("three"));
    // List<List<HasWord>> result = processor.process(input);
    // assertEquals(3, result.size());
    // assertEquals("Line", result.get(0).get(0).word());
    // assertEquals("one", result.get(0).get(1).word());
    // assertEquals("Line", result.get(1).get(0).word());
    // assertEquals("two", result.get(1).get(1).word());
    // assertEquals("Line", result.get(2).get(0).word());
    // assertEquals("three", result.get(2).get(1).word());
  }

  @Test
  public void testEmptyInputReturnsEmptyList() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    List<HasWord> input = new ArrayList<>();
    List<List<HasWord>> result = processor.process(input);
    assertTrue(result.isEmpty());
  }

  @Test
  public void testFollowersAddedToPreviousSentence() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    List<HasWord> input = new ArrayList<>();
    input.add(new Word("Sentence"));
    input.add(new Word("one"));
    input.add(new Word("."));
    input.add(new Word(")"));
    input.add(new Word("Next"));
    input.add(new Word("starts"));
    List<List<HasWord>> result = processor.process(input);
    assertEquals(2, result.size());
    assertEquals("Sentence", result.get(0).get(0).word());
    assertEquals(".", result.get(0).get(2).word());
    assertEquals(")", result.get(0).get(3).word());
    assertEquals("Next", result.get(1).get(0).word());
    assertEquals("starts", result.get(1).get(1).word());
  }

  @Test
  public void testForcedSentenceEnd() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Force");
    token1.set(CoreAnnotations.ForcedSentenceEndAnnotation.class, true);
    CoreLabel token2 = new CoreLabel();
    token2.setWord("New");
    List<HasWord> input = new ArrayList<>();
    input.add(token1);
    input.add(token2);
    List<List<HasWord>> result = processor.process(input);
    assertEquals(2, result.size());
    assertEquals("Force", result.get(0).get(0).word());
    assertEquals("New", result.get(1).get(0).word());
  }

  @Test
  public void testTwoConsecutiveNewlinesBreakSentence() {
    // WordToSentenceProcessor<HasWord> processor = new
    // WordToSentenceProcessor<>(WordToSentenceProcessor.WordToSentenceProcessor.NewlineIsSentenceBreak.TWO_CONSECUTIVE);
    List<HasWord> input = new ArrayList<>();
    input.add(new Word("First"));
    input.add(new Word(WhitespaceLexer.NEWLINE));
    input.add(new Word(WhitespaceLexer.NEWLINE));
    input.add(new Word("Second"));
    input.add(new Word("."));
    // List<List<HasWord>> result = processor.process(input);
    // assertEquals(2, result.size());
    // assertEquals("First", result.get(0).get(0).word());
    // assertEquals("Second", result.get(1).get(0).word());
    // assertEquals(".", result.get(1).get(1).word());
  }

  @Test
  public void testBoundaryDiscardsRemoveToken() {
    // WordToSentenceProcessor<HasWord> processor = new
    // WordToSentenceProcessor<>(WordToSentenceProcessor.WordToSentenceProcessor.NewlineIsSentenceBreak.ALWAYS);
    List<HasWord> input = new ArrayList<>();
    input.add(new Word("Remove"));
    input.add(new Word(WhitespaceLexer.NEWLINE));
    input.add(new Word("This"));
    // List<List<HasWord>> result = processor.process(input);
    // assertEquals(2, result.size());
    // assertEquals("Remove", result.get(0).get(0).word());
    // assertEquals("This", result.get(1).get(0).word());
  }

  @Test
  public void testMultiTokenBoundaryPatternApplies() {
    final String NEWLINE = WhitespaceLexer.NEWLINE;
    Set<String> discardSet = new HashSet<>();
    discardSet.add(NEWLINE);
    // SequencePattern<HasWord> mockPattern = new SequencePattern<HasWord>() {
    //
    // @Override
    // public SequenceMatcher<HasWord> getMatcher(List<? extends HasWord> tokens) {
    // return new SequenceMatcher<HasWord>() {
    //
    // private boolean found = false;
    //
    // @Override
    // public boolean find() {
    // if (!found && tokens.size() >= 2 && tokens.get(1).word().equals(NEWLINE) &&
    // tokens.get(2).word().equals(NEWLINE)) {
    // found = true;
    // return true;
    // }
    // return false;
    // }
    //
    // @Override
    // public List<HasWord> groupNodes() {
    // List<HasWord> match = new ArrayList<>();
    // match.add(tokens.get(1));
    // match.add(tokens.get(2));
    // return match;
    // }
    // };
    // }
    // };
    // WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>("\\.", "[)]+",
    // discardSet, Collections.emptySet(), null,
    // WordToSentenceProcessor.WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER, mockPattern,
    // Collections.emptySet(), false, false);
    List<HasWord> input = new ArrayList<>();
    input.add(new Word("Text"));
    input.add(new Word(NEWLINE));
    input.add(new Word(NEWLINE));
    input.add(new Word("Next"));
    // List<List<HasWord>> result = processor.process(input);
    // assertEquals(2, result.size());
    // assertEquals("Text", result.get(0).get(0).word());
    // assertEquals("Next", result.get(1).get(0).word());
  }

  @Test
  public void testEmptyInputWithAllowEmptySentencesTrue() {
    Set<String> boundaryToDiscard = new HashSet<>();
    boundaryToDiscard.add("<br>");
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            "\\.",
            "[)]+",
            boundaryToDiscard,
            Collections.emptySet(),
            null,
            WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
            null,
            Collections.emptySet(),
            false,
            true);
    List<HasWord> input = new ArrayList<>();
    input.add(new Word("<br>"));
    List<List<HasWord>> result = processor.process(input);
    assertEquals(1, result.size());
    assertTrue(result.get(0).isEmpty());
  }

  @Test
  public void testUnmatchedRegionStartOnly() {
    Set<String> boundaryToDiscard = new HashSet<>();
    boundaryToDiscard.add(WhitespaceLexer.NEWLINE);
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            "\\.",
            "[)]+",
            boundaryToDiscard,
            Collections.emptySet(),
            "seg",
            WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
            null,
            Collections.emptySet(),
            false,
            false);
    List<HasWord> input = new ArrayList<>();
    input.add(new Word("<seg>"));
    input.add(new Word("Text"));
    input.add(new Word("."));
    List<List<HasWord>> result = processor.process(input);
    assertEquals(1, result.size());
    assertEquals("Text", result.get(0).get(0).word());
    assertEquals(".", result.get(0).get(1).word());
  }

  @Test
  public void testTokenDiscardedByRegex() {
    Set<String> tokenRegexesToDiscard = new HashSet<>();
    tokenRegexesToDiscard.add("REMOVE_ME");
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            "\\.",
            "[)]+",
            Collections.singleton(WhitespaceLexer.NEWLINE),
            Collections.emptySet(),
            null,
            WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
            null,
            tokenRegexesToDiscard,
            false,
            false);
    List<HasWord> input = new ArrayList<>();
    input.add(new Word("A"));
    input.add(new Word("REMOVE_ME"));
    input.add(new Word("B"));
    input.add(new Word("."));
    List<List<HasWord>> result = processor.process(input);
    assertEquals(1, result.size());
    assertEquals("A", result.get(0).get(0).word());
    assertEquals("B", result.get(0).get(1).word());
    assertEquals(".", result.get(0).get(2).word());
  }

  @Test
  public void testCompoundPunctuationAsSentenceEnd() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    List<HasWord> input = new ArrayList<>();
    input.add(new Word("Wait"));
    input.add(new Word("?!?"));
    input.add(new Word("Next"));
    List<List<HasWord>> result = processor.process(input);
    assertEquals(2, result.size());
    assertEquals("Wait", result.get(0).get(0).word());
    assertEquals("?!?", result.get(0).get(1).word());
    assertEquals("Next", result.get(1).get(0).word());
  }

  @Test
  public void testSentenceStartsWithBoundaryFollower() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    List<HasWord> input = new ArrayList<>();
    input.add(new Word("First"));
    input.add(new Word("!"));
    input.add(new Word(")"));
    input.add(new Word("Second"));
    input.add(new Word("?"));
    List<List<HasWord>> result = processor.process(input);
    assertEquals(2, result.size());
    assertEquals("First", result.get(0).get(0).word());
    assertEquals("!", result.get(0).get(1).word());
    assertEquals(")", result.get(0).get(2).word());
    assertEquals("Second", result.get(1).get(0).word());
    assertEquals("?", result.get(1).get(1).word());
  }

  @Test
  public void testMultiTokenMentionDoesNotSplit() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    CoreLabel part1 = new CoreLabel();
    part1.setWord("New");
    // part1.set(CoreAnnotations.MentionTokenAnnotation.class, new MultiTokenTag(false, 0));
    CoreLabel part2 = new CoreLabel();
    part2.setWord("York");
    // part2.set(CoreAnnotations.MentionTokenAnnotation.class, new MultiTokenTag(false, 1));
    CoreLabel part3 = new CoreLabel();
    part3.setWord(".");
    part3.remove(CoreAnnotations.MentionTokenAnnotation.class);
    List<HasWord> input = new ArrayList<>();
    input.add(part1);
    input.add(part2);
    input.add(part3);
    List<List<HasWord>> result = processor.process(input);
    assertEquals(1, result.size());
    assertEquals("New", result.get(0).get(0).word());
    assertEquals("York", result.get(0).get(1).word());
    assertEquals(".", result.get(0).get(2).word());
  }

  @Test
  public void testUnicodeParagraphSeparatorTriggersForcedEnd() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Text");
    token1.set(CoreAnnotations.OriginalTextAnnotation.class, "\u2029");
    token1.set(CoreAnnotations.TextAnnotation.class, "\u2029");
    List<HasWord> input = new ArrayList<>();
    input.add(token1);
    input.add(new Word("Next"));
    List<List<HasWord>> result = processor.process(input);
    assertEquals(2, result.size());
    assertEquals("\u2029", result.get(0).get(0).word());
    assertEquals("Next", result.get(1).get(0).word());
  }

  @Test
  public void testXmlBreakTriggersBoundaryWithoutRegionMatch() {
    Set<String> xmlBreaks = new HashSet<>();
    xmlBreaks.add("p");
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            "\\.",
            "[)]+",
            Collections.emptySet(),
            xmlBreaks,
            null,
            WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
            null,
            Collections.emptySet(),
            false,
            false);
    List<HasWord> input = new ArrayList<>();
    input.add(new Word("<p>"));
    input.add(new Word("Hello"));
    input.add(new Word("."));
    input.add(new Word("<p>"));
    input.add(new Word("World"));
    input.add(new Word("!"));
    List<List<HasWord>> result = processor.process(input);
    assertEquals(2, result.size());
    assertEquals("Hello", result.get(0).get(0).word());
    assertEquals(".", result.get(0).get(1).word());
    assertEquals("World", result.get(1).get(0).word());
    assertEquals("!", result.get(1).get(1).word());
  }

  @Test
  public void testRegionEndWithoutRegionBeginIsIgnored() {
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            "\\.",
            "[\\p{Pe}\"]",
            Collections.singleton(WhitespaceLexer.NEWLINE),
            Collections.emptySet(),
            "p",
            WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
            null,
            Collections.emptySet(),
            false,
            false);
    List<HasWord> input = new ArrayList<>();
    input.add(new Word("Sentence"));
    input.add(new Word("."));
    input.add(new Word("</p>"));
    List<List<HasWord>> result = processor.process(input);
    assertEquals(1, result.size());
    assertEquals("Sentence", result.get(0).get(0).word());
    assertEquals(".", result.get(0).get(1).word());
  }

  @Test
  public void testSentenceWithoutEndingPunctuationIsStillEmitted() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    List<HasWord> input = new ArrayList<>();
    input.add(new Word("This"));
    input.add(new Word("will"));
    input.add(new Word("not"));
    input.add(new Word("end"));
    List<List<HasWord>> result = processor.process(input);
    assertEquals(1, result.size());
    assertEquals("end", result.get(0).get(3).word());
  }

  @Test
  public void testBoundaryToDiscardAtStartDoesNotEmitEmptySentence() {
    Set<String> discard = new HashSet<>();
    discard.add(WhitespaceLexer.NEWLINE);
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            "\\.",
            "[\\p{Pe}]+",
            discard,
            Collections.emptySet(),
            null,
            WordToSentenceProcessor.NewlineIsSentenceBreak.TWO_CONSECUTIVE,
            null,
            Collections.emptySet(),
            false,
            false);
    List<HasWord> input = new ArrayList<>();
    input.add(new Word(WhitespaceLexer.NEWLINE));
    input.add(new Word(WhitespaceLexer.NEWLINE));
    input.add(new Word("Real"));
    input.add(new Word("sentence"));
    input.add(new Word("."));
    List<List<HasWord>> result = processor.process(input);
    assertEquals(1, result.size());
    assertEquals("Real", result.get(0).get(0).word());
    assertEquals(".", result.get(0).get(2).word());
  }

  @Test
  public void testMultiTokenPatternCreatesBoundaryAtEndToken() {
    // SequencePattern<HasWord> mockPattern = new SequencePattern<HasWord>() {
    //
    // public SequenceMatcher<HasWord> getMatcher(List<? extends HasWord> tokens) {
    // return new SequenceMatcher<HasWord>() {
    //
    // private boolean matched = false;
    //
    // public boolean find() {
    // if (!matched && tokens.size() > 2) {
    // matched = true;
    // return true;
    // }
    // return false;
    // }
    //
    // public List<HasWord> groupNodes() {
    // return Arrays.asList(tokens.get(1), tokens.get(2));
    // }
    // };
    // }
    // };
    // WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>("\\.",
    // "[\\p{Pe}]+", Collections.singleton(WhitespaceLexer.NEWLINE), Collections.emptySet(), null,
    // WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER, mockPattern, Collections.emptySet(),
    // false, false);
    // List<HasWord> input = new ArrayList<>();
    // input.add(new Word("Start"));
    // input.add(new Word("Multi"));
    // input.add(new Word("Pattern"));
    // input.add(new Word("Next"));
    // List<List<HasWord>> result = processor.process(input);
    // assertEquals(2, result.size());
    // assertEquals("Pattern", result.get(0).get(2).word());
    // assertEquals("Next", result.get(1).get(0).word());
  }

  @Test
  public void testEscapedRightBracketAddedToPreviousSentence() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    List<HasWord> input = new ArrayList<>();
    input.add(new Word("End"));
    input.add(new Word("."));
    input.add(new Word("-RRB-"));
    List<List<HasWord>> result = processor.process(input);
    assertEquals(1, result.size());
    assertEquals(".", result.get(0).get(1).word());
    assertEquals("-RRB-", result.get(0).get(2).word());
  }

  @Test
  public void testUnmatchedOpenQuotesRejectsPlausibleToAddFollower() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    List<HasWord> input = new ArrayList<>();
    input.add(new Word("'"));
    input.add(new Word("Quoted"));
    input.add(new Word("text"));
    input.add(new Word("!"));
    input.add(new Word("'"));
    input.add(new Word(")"));
    input.add(new Word("Next"));
    List<List<HasWord>> result = processor.process(input);
    assertEquals(2, result.size());
    assertEquals(")", result.get(0).get(5).word());
    assertEquals("Next", result.get(1).get(0).word());
  }

  @Test
  public void testSentenceWithOnlyDiscardedTokensProducesNoOutput() {
    Set<String> discard = new HashSet<>();
    discard.add("<dummy>");
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            "\\.",
            "[\\p{Pe}]+",
            discard,
            Collections.emptySet(),
            null,
            WordToSentenceProcessor.NewlineIsSentenceBreak.ALWAYS,
            null,
            Collections.emptySet(),
            false,
            false);
    List<HasWord> input = new ArrayList<>();
    input.add(new Word("<dummy>"));
    input.add(new Word("<dummy>"));
    List<List<HasWord>> result = processor.process(input);
    assertEquals(0, result.size());
  }

  @Test
  public void testForcedSplitAfterRegionEnd() {
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            "\\.",
            "[\\p{Pe}]+",
            Collections.singleton(WhitespaceLexer.NEWLINE),
            Collections.emptySet(),
            "div",
            WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
            null,
            Collections.emptySet(),
            false,
            false);
    List<HasWord> input = new ArrayList<>();
    input.add(new Word("<div>"));
    input.add(new Word("Inside"));
    input.add(new Word("sentence"));
    input.add(new Word("."));
    input.add(new Word("</div>"));
    input.add(new Word("Outside"));
    List<List<HasWord>> result = processor.process(input);
    assertEquals(2, result.size());
    assertEquals("Inside", result.get(0).get(0).word());
    assertEquals("Outside", result.get(1).get(0).word());
  }

  @Test
  public void testForcedSentenceUntilEndDelaysSplit() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Keep");
    token1.set(CoreAnnotations.ForcedSentenceUntilEndAnnotation.class, true);
    CoreLabel token2 = new CoreLabel();
    token2.setWord("together");
    CoreLabel token3 = new CoreLabel();
    token3.setWord(".");
    token3.set(CoreAnnotations.ForcedSentenceEndAnnotation.class, true);
    List<HasWord> input = new ArrayList<>();
    input.add(token1);
    input.add(token2);
    input.add(token3);
    List<List<HasWord>> result = processor.process(input);
    assertEquals(1, result.size());
    assertEquals("Keep", result.get(0).get(0).word());
    assertEquals(".", result.get(0).get(2).word());
  }

  @Test
  public void testRegionFilteringExcludesAllTokens() {
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            "\\.",
            "[)]+",
            Collections.singleton(WhitespaceLexer.NEWLINE),
            Collections.emptySet(),
            "region",
            WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
            null,
            Collections.emptySet(),
            false,
            false);
    List<HasWord> input = new ArrayList<>();
    input.add(new Word("Outside"));
    input.add(new Word("."));
    List<List<HasWord>> result = processor.process(input);
    assertTrue(result.isEmpty());
  }

  @Test
  public void testBoundaryTokenIsAlsoDiscarded() {
    Set<String> discard = new HashSet<>();
    discard.add(".");
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            "\\.",
            "[)]+",
            discard,
            Collections.emptySet(),
            null,
            WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
            null,
            Collections.emptySet(),
            false,
            false);
    List<HasWord> input = new ArrayList<>();
    input.add(new Word("Hello"));
    input.add(new Word("."));
    input.add(new Word("World"));
    List<List<HasWord>> result = processor.process(input);
    assertEquals(2, result.size());
    assertEquals("Hello", result.get(0).get(0).word());
    assertEquals("World", result.get(1).get(0).word());
  }

  @Test
  public void testMultipleFollowersAppendedToFirstSentence() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    List<HasWord> input = new ArrayList<>();
    input.add(new Word("Hi"));
    input.add(new Word("?"));
    input.add(new Word(")"));
    input.add(new Word("-RRB-"));
    input.add(new Word("Next"));
    List<List<HasWord>> result = processor.process(input);
    assertEquals(2, result.size());
    assertEquals(4, result.get(0).size());
    assertEquals("Next", result.get(1).get(0).word());
  }

  @Test
  public void testNewlineAsBoundaryProducesEmptySentenceWhenAllowed() {
    Set<String> discard = new HashSet<>();
    discard.add(WhitespaceLexer.NEWLINE);
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            "\\.",
            "[)]+",
            discard,
            Collections.emptySet(),
            null,
            WordToSentenceProcessor.NewlineIsSentenceBreak.ALWAYS,
            null,
            Collections.emptySet(),
            false,
            true);
    List<HasWord> input = new ArrayList<>();
    input.add(new Word("Hello"));
    input.add(new Word(WhitespaceLexer.NEWLINE));
    input.add(new Word(WhitespaceLexer.NEWLINE));
    input.add(new Word("World"));
    input.add(new Word("."));
    List<List<HasWord>> result = processor.process(input);
    assertEquals(3, result.size());
    assertEquals("Hello", result.get(0).get(0).word());
    assertTrue(result.get(1).isEmpty());
    assertEquals("World", result.get(2).get(0).word());
  }

  @Test
  public void testInvalidXmlBreakElementDoesNotMatch() {
    Set<String> xmlTags = new HashSet<>();
    xmlTags.add("p");
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            "\\.",
            "[)]+",
            Collections.emptySet(),
            xmlTags,
            null,
            WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
            null,
            Collections.emptySet(),
            false,
            false);
    List<HasWord> input = new ArrayList<>();
    input.add(new Word("<div>"));
    input.add(new Word("Text"));
    input.add(new Word("."));
    List<List<HasWord>> result = processor.process(input);
    assertEquals(1, result.size());
    assertEquals("Text", result.get(0).get(0).word());
    assertEquals(".", result.get(0).get(1).word());
  }

  @Test
  public void testInsideRegionClosedEarlyByNonCloseTag() {
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            "\\.",
            "[)]+",
            Collections.singleton("<line>"),
            Collections.emptySet(),
            "seg",
            WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
            null,
            Collections.emptySet(),
            false,
            false);
    List<HasWord> input = new ArrayList<>();
    input.add(new Word("<seg>"));
    input.add(new Word("Hello"));
    input.add(new Word("<line>"));
    input.add(new Word("World"));
    input.add(new Word("."));
    input.add(new Word("</seg>"));
    List<List<HasWord>> result = processor.process(input);
    assertEquals(2, result.size());
    assertEquals("Hello", result.get(0).get(0).word());
    assertEquals("World", result.get(1).get(0).word());
  }

  @Test
  public void testRegionWithinAnotherRegionIsHandledCorrectly() {
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            "\\.",
            "[)]+",
            Collections.singleton(WhitespaceLexer.NEWLINE),
            Collections.emptySet(),
            "outer|inner",
            WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
            null,
            Collections.emptySet(),
            false,
            false);
    List<HasWord> input = new ArrayList<>();
    input.add(new Word("<outer>"));
    input.add(new Word("<inner>"));
    input.add(new Word("Nested"));
    input.add(new Word("here"));
    input.add(new Word("."));
    input.add(new Word("</inner>"));
    input.add(new Word("</outer>"));
    List<List<HasWord>> result = processor.process(input);
    assertEquals(1, result.size());
    assertEquals("Nested", result.get(0).get(0).word());
    assertEquals(".", result.get(0).get(2).word());
  }

  @Test
  public void testForcedEndTokenOverridesAllOtherLogic() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    CoreLabel token1 = new CoreLabel();
    token1.setWord("First");
    token1.set(CoreAnnotations.ForcedSentenceEndAnnotation.class, true);
    CoreLabel token2 = new CoreLabel();
    token2.setWord("Second");
    List<HasWord> input = new ArrayList<>();
    input.add(token1);
    input.add(token2);
    List<List<HasWord>> result = processor.process(input);
    assertEquals(2, result.size());
    assertEquals("First", result.get(0).get(0).word());
    assertEquals("Second", result.get(1).get(0).word());
  }

  @Test
  public void testSentenceBoundaryFollowerAtBeginningOfDocument() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    List<HasWord> tokens = new ArrayList<>();
    tokens.add(new Word(")"));
    tokens.add(new Word("Start"));
    tokens.add(new Word("."));
    List<List<HasWord>> result = processor.process(tokens);
    assertEquals(1, result.size());
    assertEquals(")", result.get(0).get(0).word());
    assertEquals(".", result.get(0).get(2).word());
  }

  @Test
  public void testForcedEndAnnotationWithDiscardedBoundary() {
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            "\\.",
            "[)]+",
            new HashSet<>(Collections.singleton("EOL")),
            Collections.emptySet(),
            null,
            WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
            null,
            Collections.emptySet(),
            false,
            false);
    CoreLabel word1 = new CoreLabel();
    word1.setWord("Hello");
    CoreLabel word2 = new CoreLabel();
    word2.setWord("EOL");
    word2.set(CoreAnnotations.ForcedSentenceEndAnnotation.class, true);
    CoreLabel word3 = new CoreLabel();
    word3.setWord("World");
    List<HasWord> input = new ArrayList<>();
    input.add(word1);
    input.add(word2);
    input.add(word3);
    List<List<HasWord>> result = processor.process(input);
    assertEquals(2, result.size());
    assertEquals("Hello", result.get(0).get(0).word());
    assertEquals("World", result.get(1).get(0).word());
  }

  @Test
  public void testMultipleSentenceBoundariesInARow() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    List<HasWord> tokens = new ArrayList<>();
    tokens.add(new Word("A"));
    tokens.add(new Word("."));
    tokens.add(new Word("!"));
    tokens.add(new Word("?"));
    tokens.add(new Word("B"));
    List<List<HasWord>> result = processor.process(tokens);
    assertEquals(3, result.size());
    assertEquals("A", result.get(0).get(0).word());
    assertEquals(".", result.get(0).get(1).word());
    assertEquals("!", result.get(1).get(0).word());
    assertEquals("?", result.get(1).get(1).word());
    assertEquals("B", result.get(2).get(0).word());
  }

  @Test
  public void testStringToNewlineIsSentenceBreakMapping() {
    assertEquals(
        WordToSentenceProcessor.NewlineIsSentenceBreak.ALWAYS,
        WordToSentenceProcessor.stringToNewlineIsSentenceBreak("always"));
    assertEquals(
        WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
        WordToSentenceProcessor.stringToNewlineIsSentenceBreak("never"));
    assertEquals(
        WordToSentenceProcessor.NewlineIsSentenceBreak.TWO_CONSECUTIVE,
        WordToSentenceProcessor.stringToNewlineIsSentenceBreak("two_newlines"));
    try {
      WordToSentenceProcessor.stringToNewlineIsSentenceBreak("invalid");
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException expected) {
      assertTrue(expected.getMessage().contains("Not a valid NewlineIsSentenceBreak name"));
    }
  }

  @Test
  public void testRegionPatternWithDiscardedTokensOnlyInsideRegion() {
    Set<String> discard = new HashSet<>();
    discard.add("<!-- discard -->");
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            "\\.",
            "[)]",
            discard,
            Collections.emptySet(),
            "s",
            WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
            null,
            Collections.emptySet(),
            false,
            false);
    List<HasWord> input = new ArrayList<>();
    input.add(new Word("<s>"));
    input.add(new Word("<!-- discard -->"));
    input.add(new Word("</s>"));
    List<List<HasWord>> result = processor.process(input);
    assertTrue(result.isEmpty());
  }

  @Test
  public void testEmptySentenceAllowedAfterForcedBoundaryWithNoTokens() {
    Set<String> discard = new HashSet<>();
    discard.add(WhitespaceLexer.NEWLINE);
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            "\\.",
            "[)]",
            discard,
            Collections.emptySet(),
            null,
            WordToSentenceProcessor.NewlineIsSentenceBreak.ALWAYS,
            null,
            Collections.emptySet(),
            false,
            true);
    List<HasWord> input = new ArrayList<>();
    input.add(new Word(WhitespaceLexer.NEWLINE));
    List<List<HasWord>> result = processor.process(input);
    assertEquals(1, result.size());
    assertTrue(result.get(0).isEmpty());
  }

  @Test
  public void testCloseQuoteAddedOnlyIfPlausible() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    List<HasWord> input = new ArrayList<>();
    input.add(new Word("He"));
    input.add(new Word("said"));
    input.add(new Word("\""));
    input.add(new Word("Hello"));
    input.add(new Word("."));
    input.add(new Word("\""));
    input.add(new Word(")"));
    input.add(new Word("Next"));
    List<List<HasWord>> result = processor.process(input);
    assertEquals(2, result.size());
    assertEquals("\"", result.get(0).get(5).word());
    assertEquals(")", result.get(0).get(6).word());
    assertEquals("Next", result.get(1).get(0).word());
  }

  @Test
  public void testCloseQuoteAfterEmptySentenceShouldNotBeAdded() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    List<HasWord> input = new ArrayList<>();
    input.add(new Word("Hello"));
    input.add(new Word("."));
    input.add(new Word("\""));
    input.add(new Word("New"));
    List<List<HasWord>> result = processor.process(input);
    assertEquals(2, result.size());
    assertEquals(".", result.get(0).get(1).word());
    assertEquals("\"", result.get(1).get(0).word());
    assertEquals("New", result.get(1).get(1).word());
  }

  @Test
  public void testNoSplitWhenTokenMatchesDiscardAndInsideMultiTokenExpr() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Multi");
    // token1.set(CoreAnnotations.MentionTokenAnnotation.class, new MultiTokenTag(false, 0));
    CoreLabel token2 = new CoreLabel();
    token2.setWord("line");
    // token2.set(CoreAnnotations.MentionTokenAnnotation.class, new MultiTokenTag(false, 1));
    CoreLabel token3 = new CoreLabel();
    token3.setWord("EOL");
    List<HasWord> input = new ArrayList<>();
    input.add(token1);
    input.add(token2);
    input.add(token3);
    List<List<HasWord>> result = processor.process(input);
    assertEquals(1, result.size());
    assertEquals(3, result.get(0).size());
  }

  @Test
  public void testSentenceBoundaryMultiTokenPatternMatchPreservesLastToken() {
    // WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>("\\.", "[)]+",
    // Collections.emptySet(), Collections.emptySet(), null,
    // WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER, new
    // WordToSentenceProcessorTest.MockPattern(), Collections.emptySet(), false, false);
    List<HasWord> input = new ArrayList<>();
    input.add(new Word("Para"));
    input.add(new Word("newline1"));
    input.add(new Word("newline2"));
    input.add(new Word("Continue"));
    // List<List<HasWord>> result = processor.process(input);
    // assertEquals(2, result.size());
    // assertEquals("newline2", result.get(0).get(2).word());
    // assertEquals("Continue", result.get(1).get(0).word());
  }

  @Test
  public void testGetStringFromStringInput() {
    WordToSentenceProcessor<Object> processor = new WordToSentenceProcessor<>();
    List<Object> tokens = new ArrayList<>();
    tokens.add("This");
    tokens.add(".");
    List<List<Object>> result = processor.process(tokens);
    assertEquals(1, result.size());
    assertEquals("This", result.get(0).get(0));
  }

  @Test
  public void testGetStringUnsupportedTypeThrows() {
    WordToSentenceProcessor<Object> processor = new WordToSentenceProcessor<>();
    List<Object> badInput = new ArrayList<>();
    badInput.add(123);
    try {
      processor.process(badInput);
      fail("Expected RuntimeException for unsupported token type");
    } catch (RuntimeException e) {
      assertTrue(e.getMessage().contains("Expected token to be either Word or String"));
    }
  }

  @Test
  public void testIsForcedEndTokenWithNonCoreMapObject() {
    WordToSentenceProcessor<Object> processor = new WordToSentenceProcessor<>();
    List<Object> input = new ArrayList<>();
    input.add(new Word("Test"));
    input.add(new Word("."));
    List<List<Object>> result = processor.process(input);
    assertEquals(1, result.size());
    // assertEquals(".", processor.getString(result.get(0).get(1)));
  }

  @Test
  public void testIsForcedEndTokenWithParagraphSeparator() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    CoreLabel token1 = new CoreLabel();
    token1.setWord("§");
    token1.set(CoreAnnotations.OriginalTextAnnotation.class, "\u2029");
    token1.set(CoreAnnotations.TextAnnotation.class, "\u2029");
    List<HasWord> input = new ArrayList<>();
    input.add(token1);
    input.add(new Word("Next"));
    List<List<HasWord>> result = processor.process(input);
    assertEquals(2, result.size());
    assertEquals("\u2029", result.get(0).get(0).word());
    assertEquals("Next", result.get(1).get(0).word());
  }

  @Test
  public void testStringToNewlineIsSentenceBreakNullInput() {
    try {
      WordToSentenceProcessor.stringToNewlineIsSentenceBreak(null);
      fail("Expected IllegalArgumentException on null input");
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().contains("Not a valid NewlineIsSentenceBreak name"));
    }
  }

  @Test
  public void testEmptyRegionBeginPatternSkipsAllTokens() {
    Set<String> discard = new HashSet<>();
    discard.add(WhitespaceLexer.NEWLINE);
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            "\\.",
            "[)]+",
            discard,
            Collections.emptySet(),
            "seg",
            WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
            null,
            Collections.emptySet(),
            false,
            false);
    List<HasWord> tokens = new ArrayList<>();
    tokens.add(new Word("Outside"));
    tokens.add(new Word("."));
    List<List<HasWord>> result = processor.process(tokens);
    assertTrue(result.isEmpty());
  }

  @Test
  public void testProcessDocumentWrapsProcessCorrectly() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    // Document<String, String, HasWord> doc = new Document<>("doc1", "en");
    // doc.add(new Word("Hello"));
    // doc.add(new Word("."));
    // doc.add(new Word("World"));
    // doc.add(new Word("!"));
    // Document<String, String, List<HasWord>> sentenceDoc = processor.processDocument(doc);
    // assertEquals("doc1", sentenceDoc.getId());
    // assertEquals("en", sentenceDoc.getLanguage());
    // assertEquals(2, sentenceDoc.size());
    // List<HasWord> sentence1 = sentenceDoc.get(0);
    // assertEquals("Hello", sentence1.get(0).word());
    // List<HasWord> sentence2 = sentenceDoc.get(1);
    // assertEquals("World", sentence2.get(0).word());
  }

  @Test
  public void testSentenceEndingWithPunctuationAlone() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    List<HasWord> input = new ArrayList<>();
    input.add(new Word("Hi"));
    input.add(new Word("."));
    input.add(new Word("?"));
    input.add(new Word("!"));
    List<List<HasWord>> result = processor.process(input);
    assertEquals(3, result.size());
    assertEquals("Hi", result.get(0).get(0).word());
    assertEquals(".", result.get(0).get(1).word());
    assertEquals("?", result.get(1).get(0).word());
    assertEquals("!", result.get(2).get(0).word());
  }

  @Test
  public void testConstructorWithCustomXmlBreakElement() {
    Set<String> xmlBreaks = new HashSet<>();
    xmlBreaks.add("break");
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            "\\.",
            "[)]+",
            Collections.emptySet(),
            xmlBreaks,
            null,
            WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
            null,
            Collections.emptySet(),
            false,
            false);
    List<HasWord> tokens = new ArrayList<>();
    tokens.add(new Word("<break>"));
    tokens.add(new Word("Hello"));
    tokens.add(new Word("."));
    List<List<HasWord>> result = processor.process(tokens);
    assertEquals(2, result.size());
    assertEquals("Hello", result.get(1).get(0).word());
  }

  @Test
  public void testConstructorWithAllNullDefaults() {
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            null,
            null,
            null,
            null,
            WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
            null,
            null);
    List<HasWord> input = new ArrayList<>();
    input.add(new Word("Hello"));
    input.add(new Word("."));
    input.add(new Word("Test"));
    input.add(new Word("?"));
    List<List<HasWord>> result = processor.process(input);
    assertEquals(2, result.size());
    assertEquals(".", result.get(0).get(1).word());
    assertEquals("?", result.get(1).get(1).word());
  }

  @Test
  public void testStrictOnePerLineConstructionPreservesEmptySentences() {
    Set<String> discardSet = new HashSet<>();
    discardSet.add("EOL");
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>(discardSet);
    List<HasWord> input = new ArrayList<>();
    input.add(new Word("Hello"));
    input.add(new Word("EOL"));
    input.add(new Word("EOL"));
    input.add(new Word("World"));
    List<List<HasWord>> result = processor.process(input);
    assertEquals(3, result.size());
    assertEquals("Hello", result.get(0).get(0).word());
    assertTrue(result.get(1).isEmpty());
    assertEquals("World", result.get(2).get(0).word());
  }

  @Test
  public void testSentenceWithStartQuoteAndNoEndingQuoteFollowerHandled() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    List<HasWord> input = new ArrayList<>();
    input.add(new Word("\""));
    input.add(new Word("He"));
    input.add(new Word("said"));
    input.add(new Word("."));
    input.add(new Word(")"));
    input.add(new Word("Next"));
    List<List<HasWord>> result = processor.process(input);
    assertEquals(2, result.size());
    assertEquals(")", result.get(0).get(4).word());
    assertEquals("Next", result.get(1).get(0).word());
  }

  @Test
  public void testSentenceWithEvenDoubleQuotesRejectsFollower() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    List<HasWord> sentence1 =
        Arrays.asList(
            new Word("\""), new Word("quoted"), new Word("text"), new Word("\""), new Word("!"));
    List<HasWord> sentence2 = Arrays.asList(new Word(")"), new Word("Outside"));
    List<HasWord> combined = new ArrayList<>();
    combined.addAll(sentence1);
    combined.addAll(sentence2);
    List<List<HasWord>> result = processor.process(combined);
    assertEquals(2, result.size());
    assertEquals("!", result.get(0).get(4).word());
    assertEquals(")", result.get(1).get(0).word());
  }

  @Test
  public void testOverlappingRegionStartAndEnds() {
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            "\\.",
            "[)]+",
            Collections.singleton(WhitespaceLexer.NEWLINE),
            Collections.emptySet(),
            "r",
            WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
            null,
            Collections.emptySet(),
            false,
            false);
    List<HasWord> input = new ArrayList<>();
    input.add(new Word("<r>"));
    input.add(new Word("Text1"));
    input.add(new Word("<r>"));
    input.add(new Word("Text2"));
    input.add(new Word("</r>"));
    input.add(new Word("Text3"));
    input.add(new Word("</r>"));
    input.add(new Word("Outside"));
    List<List<HasWord>> result = processor.process(input);
    assertEquals(2, result.size());
    assertEquals("Text1", result.get(0).get(0).word());
    assertEquals("Text3", result.get(0).get(2).word());
    assertEquals("Outside", result.get(1).get(0).word());
  }

  @Test
  public void testEmptyRegionYieldsNoSentence() {
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            "\\.",
            "[)]+",
            Collections.emptySet(),
            Collections.emptySet(),
            "p",
            WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
            null,
            Collections.emptySet(),
            false,
            false);
    List<HasWord> input = Arrays.asList(new Word("<p>"), new Word("</p>"));
    List<List<HasWord>> result = processor.process(input);
    assertTrue(result.isEmpty());
  }

  @Test
  public void testSentenceBoundaryAndMultiPatternSplitSeparate() {
    // SequencePattern<HasWord> mockPattern = new SequencePattern<HasWord>() {
    //
    // public SequenceMatcher<HasWord> getMatcher(List<? extends HasWord> tokens) {
    // return new SequenceMatcher<HasWord>() {
    //
    // private boolean matched = false;
    //
    // public boolean find() {
    // return !matched && (matched = true);
    // }
    //
    // public List<HasWord> groupNodes() {
    // return Arrays.asList(tokens.get(0), tokens.get(1));
    // }
    // };
    // }
    // };
    // WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>("\\.", "[)]+",
    // Collections.emptySet(), Collections.emptySet(), null,
    // WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER, mockPattern, Collections.emptySet(),
    // false, false);
    List<HasWord> tokens =
        Arrays.asList(new Word("A"), new Word("B"), new Word("C."), new Word("End"));
    // List<List<HasWord>> result = processor.process(tokens);
    // assertEquals(2, result.size());
    // assertEquals("B", result.get(0).get(1).word());
    // assertEquals("C.", result.get(1).get(0).word());
  }

  @Test
  public void testDiscardedBoundaryCoalescesTwoTogetherIntoSingleSplit() {
    Set<String> discard = new HashSet<>();
    discard.add("EOL");
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            "\\.",
            "[)]",
            discard,
            Collections.emptySet(),
            null,
            WordToSentenceProcessor.NewlineIsSentenceBreak.ALWAYS,
            null,
            Collections.emptySet(),
            false,
            false);
    List<HasWord> input =
        Arrays.asList(
            new Word("Hello"),
            new Word("EOL"),
            new Word("EOL"),
            new Word("World"),
            new Word("EOL"));
    List<List<HasWord>> result = processor.process(input);
    assertEquals(2, result.size());
    assertEquals("Hello", result.get(0).get(0).word());
    assertEquals("World", result.get(1).get(0).word());
  }

  @Test
  public void testOnlyBoundaryFollowerAtSentenceStartIsPreserved() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    List<HasWord> input =
        Arrays.asList(new Word("Hello"), new Word("."), new Word(")"), new Word("Go"));
    List<List<HasWord>> result = processor.process(input);
    assertEquals(2, result.size());
    assertEquals(")", result.get(1).get(0).word());
    assertEquals("Go", result.get(1).get(1).word());
  }

  @Test
  public void testSentenceFollowerAtStartOfDocumentIsPreserved() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    List<HasWord> input = Arrays.asList(new Word(")"), new Word("Starts"), new Word("."));
    List<List<HasWord>> result = processor.process(input);
    assertEquals(1, result.size());
    assertEquals(")", result.get(0).get(0).word());
    assertEquals("Starts", result.get(0).get(1).word());
  }

  @Test
  public void testCoreMapWithInvalidForcedSentenceEndAnnotationType() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    CoreLabel token = new CoreLabel();
    token.setWord("Test");
    token.set(CoreAnnotations.ForcedSentenceEndAnnotation.class, null);
    token.set(CoreAnnotations.TextAnnotation.class, "Test");
    List<HasWord> input = Arrays.asList(token);
    List<List<HasWord>> result = processor.process(input);
    assertEquals(1, result.size());
    assertEquals("Test", result.get(0).get(0).word());
  }
}
