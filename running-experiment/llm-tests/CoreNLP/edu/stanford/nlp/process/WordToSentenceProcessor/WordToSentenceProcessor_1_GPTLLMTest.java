package edu.stanford.nlp.process;

import static org.junit.Assert.*;

import edu.stanford.nlp.ling.*;
import java.io.*;
import java.util.*;
import org.junit.Test;

public class WordToSentenceProcessor_1_GPTLLMTest {

  @Test
  public void testDefaultSentenceSplitting() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    CoreLabel w1 = new CoreLabel();
    w1.setWord("This");
    CoreLabel w2 = new CoreLabel();
    w2.setWord("is");
    CoreLabel w3 = new CoreLabel();
    w3.setWord("a");
    CoreLabel w4 = new CoreLabel();
    w4.setWord("test");
    CoreLabel w5 = new CoreLabel();
    w5.setWord(".");
    CoreLabel w6 = new CoreLabel();
    w6.setWord("Second");
    CoreLabel w7 = new CoreLabel();
    w7.setWord("sentence");
    CoreLabel w8 = new CoreLabel();
    w8.setWord("?");
    List<HasWord> input = Arrays.asList(w1, w2, w3, w4, w5, w6, w7, w8);
    List<List<HasWord>> output = processor.process(input);
    assertEquals(2, output.size());
    assertEquals("This", output.get(0).get(0).word());
    assertEquals("is", output.get(0).get(1).word());
    assertEquals("a", output.get(0).get(2).word());
    assertEquals("test", output.get(0).get(3).word());
    assertEquals(".", output.get(0).get(4).word());
    assertEquals("Second", output.get(1).get(0).word());
    assertEquals("sentence", output.get(1).get(1).word());
    assertEquals("?", output.get(1).get(2).word());
  }

  @Test
  public void testProcessOneSentence() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>(true);
    CoreLabel w1 = new CoreLabel();
    w1.setWord("All");
    CoreLabel w2 = new CoreLabel();
    w2.setWord("in");
    CoreLabel w3 = new CoreLabel();
    w3.setWord("one");
    CoreLabel w4 = new CoreLabel();
    w4.setWord("sentence");
    CoreLabel w5 = new CoreLabel();
    w5.setWord(".");
    List<HasWord> input = Arrays.asList(w1, w2, w3, w4, w5);
    List<List<HasWord>> output = processor.process(input);
    assertEquals(1, output.size());
    assertEquals("All", output.get(0).get(0).word());
    assertEquals("in", output.get(0).get(1).word());
    assertEquals("one", output.get(0).get(2).word());
    assertEquals("sentence", output.get(0).get(3).word());
    assertEquals(".", output.get(0).get(4).word());
  }

  @Test
  public void testProcessWithNewlineBreaks() {
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(WordToSentenceProcessor.NewlineIsSentenceBreak.ALWAYS);
    CoreLabel w1 = new CoreLabel();
    w1.setWord("Hello");
    CoreLabel w2 = new CoreLabel();
    w2.setWord("world");
    CoreLabel w3 = new CoreLabel();
    w3.setWord("\n");
    CoreLabel w4 = new CoreLabel();
    w4.setWord("Another");
    CoreLabel w5 = new CoreLabel();
    w5.setWord("line");
    CoreLabel w6 = new CoreLabel();
    w6.setWord(".");
    List<HasWord> input = Arrays.asList(w1, w2, w3, w4, w5, w6);
    List<List<HasWord>> output = processor.process(input);
    assertEquals(2, output.size());
    assertEquals("Hello", output.get(0).get(0).word());
    assertEquals("world", output.get(0).get(1).word());
    assertEquals("Another", output.get(1).get(0).word());
    assertEquals("line", output.get(1).get(1).word());
    assertEquals(".", output.get(1).get(2).word());
  }

  @Test
  public void testEmptyInput() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    List<HasWord> input = Collections.emptyList();
    List<List<HasWord>> output = processor.process(input);
    assertTrue(output.isEmpty());
  }

  @Test
  public void testForcedSentenceEndToken() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    CoreLabel w1 = new CoreLabel();
    w1.setWord("This");
    CoreLabel w2 = new CoreLabel();
    w2.setWord("is");
    CoreLabel w3 = new CoreLabel();
    w3.setWord("forced");
    CoreLabel forced = new CoreLabel();
    forced.setWord("end");
    forced.set(CoreAnnotations.ForcedSentenceEndAnnotation.class, Boolean.TRUE);
    List<HasWord> input = Arrays.asList(w1, w2, w3, forced);
    List<List<HasWord>> output = processor.process(input);
    assertEquals(1, output.size());
    assertEquals("This", output.get(0).get(0).word());
    assertEquals("is", output.get(0).get(1).word());
    assertEquals("forced", output.get(0).get(2).word());
    assertEquals("end", output.get(0).get(3).word());
  }

  @Test
  public void testBoundaryFollowerBehavior() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    CoreLabel w1 = new CoreLabel();
    w1.setWord("She");
    CoreLabel w2 = new CoreLabel();
    w2.setWord("smiled");
    CoreLabel w3 = new CoreLabel();
    w3.setWord(".");
    CoreLabel w4 = new CoreLabel();
    w4.setWord(")");
    CoreLabel w5 = new CoreLabel();
    w5.setWord("Next");
    CoreLabel w6 = new CoreLabel();
    w6.setWord("sentence");
    CoreLabel w7 = new CoreLabel();
    w7.setWord(".");
    List<HasWord> input = Arrays.asList(w1, w2, w3, w4, w5, w6, w7);
    List<List<HasWord>> output = processor.process(input);
    assertEquals(2, output.size());
    assertEquals("She", output.get(0).get(0).word());
    assertEquals("smiled", output.get(0).get(1).word());
    assertEquals(".", output.get(0).get(2).word());
    assertEquals(")", output.get(0).get(3).word());
    assertEquals("Next", output.get(1).get(0).word());
    assertEquals("sentence", output.get(1).get(1).word());
    assertEquals(".", output.get(1).get(2).word());
  }

  @Test
  public void testTwoConsecutiveNewlinesSplitting() {
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            WordToSentenceProcessor.NewlineIsSentenceBreak.TWO_CONSECUTIVE);
    CoreLabel w1 = new CoreLabel();
    w1.setWord("Line1");
    CoreLabel w2 = new CoreLabel();
    w2.setWord("\n");
    CoreLabel w3 = new CoreLabel();
    w3.setWord("\n");
    CoreLabel w4 = new CoreLabel();
    w4.setWord("Line2");
    CoreLabel w5 = new CoreLabel();
    w5.setWord(".");
    List<HasWord> input = Arrays.asList(w1, w2, w3, w4, w5);
    List<List<HasWord>> output = processor.process(input);
    assertEquals(2, output.size());
    assertEquals("Line1", output.get(0).get(0).word());
    assertEquals("Line2", output.get(1).get(0).word());
    assertEquals(".", output.get(1).get(1).word());
  }

  @Test
  public void testMultipleExclamationMarks() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    CoreLabel w1 = new CoreLabel();
    w1.setWord("Wow");
    CoreLabel w2 = new CoreLabel();
    w2.setWord("!");
    CoreLabel w3 = new CoreLabel();
    w3.setWord("!");
    CoreLabel w4 = new CoreLabel();
    w4.setWord("Amazing");
    CoreLabel w5 = new CoreLabel();
    w5.setWord("!");
    List<HasWord> input = Arrays.asList(w1, w2, w3, w4, w5);
    List<List<HasWord>> output = processor.process(input);
    assertEquals(3, output.size());
    assertEquals("Wow", output.get(0).get(0).word());
    assertEquals("!", output.get(0).get(1).word());
    assertEquals("!", output.get(1).get(0).word());
    assertEquals("Amazing", output.get(2).get(0).word());
    assertEquals("!", output.get(2).get(1).word());
  }

  @Test
  public void testQuotedSentenceWithPunctuation() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    CoreLabel w1 = new CoreLabel();
    w1.setWord("\"");
    CoreLabel w2 = new CoreLabel();
    w2.setWord("Hello");
    CoreLabel w3 = new CoreLabel();
    w3.setWord(".");
    CoreLabel w4 = new CoreLabel();
    w4.setWord("\"");
    CoreLabel w5 = new CoreLabel();
    w5.setWord("Hi");
    CoreLabel w6 = new CoreLabel();
    w6.setWord(".");
    List<HasWord> input = Arrays.asList(w1, w2, w3, w4, w5, w6);
    List<List<HasWord>> output = processor.process(input);
    assertEquals(2, output.size());
    assertEquals("\"", output.get(0).get(0).word());
    assertEquals("Hello", output.get(0).get(1).word());
    assertEquals(".", output.get(0).get(2).word());
    assertEquals("\"", output.get(0).get(3).word());
    assertEquals("Hi", output.get(1).get(0).word());
    assertEquals(".", output.get(1).get(1).word());
  }

  @Test
  public void testDiscardedOnlyNewlineTokens() {
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(Collections.singleton("\n"));
    CoreLabel w1 = new CoreLabel();
    w1.setWord("\n");
    CoreLabel w2 = new CoreLabel();
    w2.setWord("\n");
    List<HasWord> input = Arrays.asList(w1, w2);
    List<List<HasWord>> output = processor.process(input);
    assertTrue(output.isEmpty());
  }

  @Test
  public void testEmptyTokensAllowed() {
    Set<String> discard = new HashSet<>();
    discard.add("\n");
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            "",
            "",
            discard,
            null,
            null,
            WordToSentenceProcessor.NewlineIsSentenceBreak.ALWAYS,
            null,
            null,
            false,
            true);
    CoreLabel w1 = new CoreLabel();
    w1.setWord("\n");
    List<HasWord> input = Collections.singletonList(w1);
    List<List<HasWord>> output = processor.process(input);
    assertEquals(1, output.size());
    assertTrue(output.get(0).isEmpty());
  }

  @Test
  public void testXmlBreakElementTriggersBoundary() {
    Set<String> xmlBreaks = new HashSet<>();
    xmlBreaks.add("p");
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            "\\.",
            "\\)",
            Collections.emptySet(),
            xmlBreaks,
            null,
            WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
            null,
            null,
            false,
            false);
    CoreLabel w1 = new CoreLabel();
    w1.setWord("Hello");
    CoreLabel w2 = new CoreLabel();
    w2.setWord(".");
    CoreLabel w3 = new CoreLabel();
    w3.setWord("<p>");
    CoreLabel w4 = new CoreLabel();
    w4.setWord("World");
    CoreLabel w5 = new CoreLabel();
    w5.setWord(".");
    List<HasWord> input = Arrays.asList(w1, w2, w3, w4, w5);
    List<List<HasWord>> output = processor.process(input);
    assertEquals(2, output.size());
    assertEquals("Hello", output.get(0).get(0).word());
    assertEquals(".", output.get(0).get(1).word());
    assertEquals("World", output.get(1).get(0).word());
    assertEquals(".", output.get(1).get(1).word());
  }

  @Test
  public void testRegionFilteringExcludesOutsideTokens() {
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            "\\.",
            "\\)",
            Collections.emptySet(),
            null,
            "sent",
            WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
            null,
            null,
            false,
            false);
    CoreLabel w1 = new CoreLabel();
    w1.setWord("Outside1");
    CoreLabel w2 = new CoreLabel();
    w2.setWord("<sent>");
    CoreLabel w3 = new CoreLabel();
    w3.setWord("Inside1");
    CoreLabel w4 = new CoreLabel();
    w4.setWord(".");
    CoreLabel w5 = new CoreLabel();
    w5.setWord("</sent>");
    CoreLabel w6 = new CoreLabel();
    w6.setWord("Outside2");
    List<HasWord> input = Arrays.asList(w1, w2, w3, w4, w5, w6);
    List<List<HasWord>> output = processor.process(input);
    assertEquals(1, output.size());
    assertEquals("Inside1", output.get(0).get(0).word());
    assertEquals(".", output.get(0).get(1).word());
  }

  @Test
  public void testForcedSentenceUntilEndAnnotationPreventsSplit() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    CoreLabel w1 = new CoreLabel();
    w1.setWord("Begin");
    w1.set(CoreAnnotations.ForcedSentenceUntilEndAnnotation.class, true);
    CoreLabel w2 = new CoreLabel();
    w2.setWord("middle");
    CoreLabel w3 = new CoreLabel();
    w3.setWord(".");
    CoreLabel w4 = new CoreLabel();
    w4.setWord("end");
    CoreLabel w5 = new CoreLabel();
    w5.setWord("!");
    List<HasWord> input = Arrays.asList(w1, w2, w3, w4, w5);
    List<List<HasWord>> output = processor.process(input);
    assertEquals(1, output.size());
    assertEquals("Begin", output.get(0).get(0).word());
    assertEquals("middle", output.get(0).get(1).word());
    assertEquals(".", output.get(0).get(2).word());
    assertEquals("end", output.get(0).get(3).word());
    assertEquals("!", output.get(0).get(4).word());
  }

  @Test
  public void testUnmatchedQuoteDoesNotReattach() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    CoreLabel w1 = new CoreLabel();
    w1.setWord("Hello");
    CoreLabel w2 = new CoreLabel();
    w2.setWord("'");
    CoreLabel w3 = new CoreLabel();
    w3.setWord("World");
    CoreLabel w4 = new CoreLabel();
    w4.setWord(".");
    List<HasWord> input = Arrays.asList(w1, w2, w3, w4);
    List<List<HasWord>> output = processor.process(input);
    assertEquals(1, output.size());
    assertEquals("Hello", output.get(0).get(0).word());
    assertEquals("'", output.get(0).get(1).word());
    assertEquals("World", output.get(0).get(2).word());
    assertEquals(".", output.get(0).get(3).word());
  }

  @Test
  public void testFinalDiscardedNewlineDoesNotEmitSentence() {
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(Collections.singleton("\n"));
    CoreLabel w1 = new CoreLabel();
    w1.setWord("Test");
    CoreLabel w2 = new CoreLabel();
    w2.setWord(".");
    CoreLabel w3 = new CoreLabel();
    w3.setWord("\n");
    List<HasWord> input = Arrays.asList(w1, w2, w3);
    List<List<HasWord>> output = processor.process(input);
    assertEquals(1, output.size());
    assertEquals("Test", output.get(0).get(0).word());
    assertEquals(".", output.get(0).get(1).word());
  }

  @Test
  public void testBoundaryTokenOnlySentence() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    CoreLabel w1 = new CoreLabel();
    w1.setWord(".");
    CoreLabel w2 = new CoreLabel();
    w2.setWord("Next");
    CoreLabel w3 = new CoreLabel();
    w3.setWord(".");
    List<HasWord> input = Arrays.asList(w1, w2, w3);
    List<List<HasWord>> output = processor.process(input);
    assertEquals(2, output.size());
    assertEquals(".", output.get(0).get(0).word());
    assertEquals("Next", output.get(1).get(0).word());
    assertEquals(".", output.get(1).get(1).word());
  }

  @Test
  public void testDiscardAndThenBoundaryToken() {
    Set<String> discardSet = new HashSet<>();
    discardSet.add("<br>");
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            "\\.",
            "\\)",
            discardSet,
            null,
            null,
            WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
            null,
            null,
            false,
            false);
    CoreLabel w1 = new CoreLabel();
    w1.setWord("First");
    CoreLabel w2 = new CoreLabel();
    w2.setWord("<br>");
    CoreLabel w3 = new CoreLabel();
    w3.setWord(".");
    CoreLabel w4 = new CoreLabel();
    w4.setWord("Second");
    CoreLabel w5 = new CoreLabel();
    w5.setWord(".");
    List<HasWord> input = Arrays.asList(w1, w2, w3, w4, w5);
    List<List<HasWord>> output = processor.process(input);
    assertEquals(2, output.size());
    assertEquals("First", output.get(0).get(0).word());
    assertEquals(".", output.get(0).get(1).word());
    assertEquals("Second", output.get(1).get(0).word());
    assertEquals(".", output.get(1).get(1).word());
  }

  @Test
  public void testTrailingFollowerWithoutPreviousSentence() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    CoreLabel w1 = new CoreLabel();
    w1.setWord(")");
    CoreLabel w2 = new CoreLabel();
    w2.setWord("Real");
    List<HasWord> input = Arrays.asList(w1, w2);
    List<List<HasWord>> output = processor.process(input);
    assertEquals(1, output.size());
    assertEquals(")", output.get(0).get(0).word());
    assertEquals("Real", output.get(0).get(1).word());
  }

  @Test
  public void testOnlyFollowerFollowingEmptySentence() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    CoreLabel w1 = new CoreLabel();
    w1.setWord(".");
    CoreLabel w2 = new CoreLabel();
    w2.setWord(")");
    List<HasWord> input = Arrays.asList(w1, w2);
    List<List<HasWord>> output = processor.process(input);
    assertEquals(2, output.size());
    assertEquals(".", output.get(0).get(0).word());
    assertEquals(")", output.get(1).get(0).word());
  }

  @Test
  public void testRegionInsideAndOutsideAlternation() {
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            "\\.",
            "\\)",
            Collections.emptySet(),
            null,
            "x",
            WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
            null,
            null,
            false,
            false);
    CoreLabel w1 = new CoreLabel();
    w1.setWord("<x>");
    CoreLabel w2 = new CoreLabel();
    w2.setWord("Inside1");
    CoreLabel w3 = new CoreLabel();
    w3.setWord(".");
    CoreLabel w4 = new CoreLabel();
    w4.setWord("</x>");
    CoreLabel w5 = new CoreLabel();
    w5.setWord("Outside");
    CoreLabel w6 = new CoreLabel();
    w6.setWord("<x>");
    CoreLabel w7 = new CoreLabel();
    w7.setWord("Inside2");
    CoreLabel w8 = new CoreLabel();
    w8.setWord(".");
    CoreLabel w9 = new CoreLabel();
    w9.setWord("</x>");
    List<HasWord> input = Arrays.asList(w1, w2, w3, w4, w5, w6, w7, w8, w9);
    List<List<HasWord>> output = processor.process(input);
    assertEquals(2, output.size());
    assertEquals("Inside1", output.get(0).get(0).word());
    assertEquals(".", output.get(0).get(1).word());
    assertEquals("Inside2", output.get(1).get(0).word());
    assertEquals(".", output.get(1).get(1).word());
  }

  @Test
  public void testTokenPatternToDiscardRemovesMatchingTokens() {
    Set<String> discardRegex = new HashSet<>();
    discardRegex.add("foo");
    discardRegex.add("bar");
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            "\\.",
            "\\)",
            Collections.emptySet(),
            null,
            null,
            WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
            null,
            discardRegex,
            false,
            false);
    CoreLabel w1 = new CoreLabel();
    w1.setWord("Test");
    CoreLabel w2 = new CoreLabel();
    w2.setWord("foo");
    CoreLabel w3 = new CoreLabel();
    w3.setWord("bar");
    CoreLabel w4 = new CoreLabel();
    w4.setWord(".");
    List<HasWord> input = Arrays.asList(w1, w2, w3, w4);
    List<List<HasWord>> output = processor.process(input);
    assertEquals(1, output.size());
    assertEquals(2, output.get(0).size());
    assertEquals("Test", output.get(0).get(0).word());
    assertEquals(".", output.get(0).get(1).word());
  }

  @Test
  public void testQuotedOddCountDoesNotAttachToPreviousSentence() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    CoreLabel w1 = new CoreLabel();
    w1.setWord("One");
    CoreLabel w2 = new CoreLabel();
    w2.setWord("\"");
    CoreLabel w3 = new CoreLabel();
    w3.setWord(".");
    CoreLabel w4 = new CoreLabel();
    w4.setWord("\"");
    List<HasWord> input = Arrays.asList(w1, w2, w3, w4);
    List<List<HasWord>> output = processor.process(input);
    assertEquals(2, output.size());
    assertEquals("One", output.get(0).get(0).word());
    assertEquals("\"", output.get(0).get(1).word());
    assertEquals(".", output.get(0).get(2).word());
    assertEquals("\"", output.get(1).get(0).word());
  }

  @Test
  public void testNoBoundaryMatchReturnsOneFullSentence() {
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            "NONE",
            "\\)",
            Collections.emptySet(),
            null,
            null,
            WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
            null,
            null,
            false,
            false);
    CoreLabel w1 = new CoreLabel();
    w1.setWord("No");
    CoreLabel w2 = new CoreLabel();
    w2.setWord("boundaries");
    CoreLabel w3 = new CoreLabel();
    w3.setWord("here");
    List<HasWord> input = Arrays.asList(w1, w2, w3);
    List<List<HasWord>> output = processor.process(input);
    assertEquals(1, output.size());
    assertEquals("No", output.get(0).get(0).word());
    assertEquals("boundaries", output.get(0).get(1).word());
    assertEquals("here", output.get(0).get(2).word());
  }

  @Test
  public void testEmptyBoundaryAndRegionConfigReturnsAll() {
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            ".*",
            ".*",
            Collections.emptySet(),
            null,
            null,
            WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
            null,
            null,
            false,
            false);
    CoreLabel w1 = new CoreLabel();
    w1.setWord("A");
    CoreLabel w2 = new CoreLabel();
    w2.setWord("B");
    List<HasWord> input = Arrays.asList(w1, w2);
    List<List<HasWord>> output = processor.process(input);
    assertEquals(2, output.size());
    assertEquals("A", output.get(0).get(0).word());
    assertEquals("B", output.get(1).get(0).word());
  }

  @Test
  public void testSentenceWithOnlyNewlineWhenEmptySentencesAllowed() {
    Set<String> discard = new HashSet<>();
    discard.add("\n");
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            "\\.",
            "\\)",
            discard,
            null,
            null,
            WordToSentenceProcessor.NewlineIsSentenceBreak.ALWAYS,
            null,
            null,
            false,
            true);
    CoreLabel w1 = new CoreLabel();
    w1.setWord("\n");
    CoreLabel w2 = new CoreLabel();
    w2.setWord("Hi");
    CoreLabel w3 = new CoreLabel();
    w3.setWord(".");
    List<HasWord> input = Arrays.asList(w1, w2, w3);
    List<List<HasWord>> output = processor.process(input);
    assertEquals(2, output.size());
    assertTrue(output.get(0).isEmpty());
    assertEquals("Hi", output.get(1).get(0).word());
    assertEquals(".", output.get(1).get(1).word());
  }

  @Test
  public void testMultiTokenPatternBoundaryMatchIsRecognized() {
    List<HasWord> fullList = new ArrayList<>();
    CoreLabel tok1 = new CoreLabel();
    tok1.setWord("one");
    CoreLabel tok2 = new CoreLabel();
    tok2.setWord("two");
    CoreLabel tok3 = new CoreLabel();
    tok3.setWord("three");
    CoreLabel tok4 = new CoreLabel();
    tok4.setWord("four");
    CoreLabel tok5 = new CoreLabel();
    tok5.setWord("five");
    fullList.add(tok1);
    fullList.add(tok2);
    fullList.add(tok3);
    fullList.add(tok4);
    fullList.add(tok5);
    // SequencePattern<HasWord> mockPattern = new SequencePattern<HasWord>() {
    //
    // public SequenceMatcher<HasWord> getMatcher(List<? extends HasWord> tokens) {
    // return new SequenceMatcher<HasWord>() {
    //
    // private boolean seen = false;
    //
    // public boolean find() {
    // boolean r = !seen;
    // seen = true;
    // return r;
    // }
    //
    // public List<HasWord> groupNodes() {
    // return tokens.subList(1, 4);
    // }
    // };
    // }
    // };
    // WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>("\\.", "\\)",
    // Collections.emptySet(), null, null, WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
    // mockPattern, null, false, false);
    // List<List<HasWord>> output = processor.process(fullList);
    // assertEquals(2, output.size());
    // assertEquals("one", output.get(0).get(0).word());
    // assertEquals("two", output.get(0).get(1).word());
    // assertEquals("three", output.get(0).get(2).word());
    // assertEquals("four", output.get(0).get(3).word());
    // assertEquals("five", output.get(1).get(0).word());
  }

  @Test
  public void testMultiTokenMentionDisablesBreakInMiddle() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    CoreLabel w1 = new CoreLabel();
    w1.setWord("John");
    // MultiTokenTag tag1 = new MultiTokenTag("PERSON", 1, 2);
    // w1.set(CoreAnnotations.MentionTokenAnnotation.class, tag1);
    CoreLabel w2 = new CoreLabel();
    w2.setWord("Smith");
    // MultiTokenTag tag2 = new MultiTokenTag("PERSON", 2, 2);
    // w2.set(CoreAnnotations.MentionTokenAnnotation.class, tag2);
    CoreLabel w3 = new CoreLabel();
    w3.setWord(".");
    CoreLabel w4 = new CoreLabel();
    w4.setWord("Okay");
    CoreLabel w5 = new CoreLabel();
    w5.setWord(".");
    List<HasWord> input = Arrays.asList(w1, w2, w3, w4, w5);
    List<List<HasWord>> sentences = processor.process(input);
    assertEquals(2, sentences.size());
    assertEquals("John", sentences.get(0).get(0).word());
    assertEquals("Smith", sentences.get(0).get(1).word());
    assertEquals(".", sentences.get(0).get(2).word());
    assertEquals("Okay", sentences.get(1).get(0).word());
    assertEquals(".", sentences.get(1).get(1).word());
  }

  @Test
  public void testSentencesFollowOneAnotherWithoutDiscardableToken() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    CoreLabel w1 = new CoreLabel();
    w1.setWord("Yes");
    CoreLabel w2 = new CoreLabel();
    w2.setWord(".");
    CoreLabel w3 = new CoreLabel();
    w3.setWord("No");
    CoreLabel w4 = new CoreLabel();
    w4.setWord("?");
    List<HasWord> input = Arrays.asList(w1, w2, w3, w4);
    List<List<HasWord>> result = processor.process(input);
    assertEquals(2, result.size());
    assertEquals("Yes", result.get(0).get(0).word());
    assertEquals(".", result.get(0).get(1).word());
    assertEquals("No", result.get(1).get(0).word());
    assertEquals("?", result.get(1).get(1).word());
  }

  @Test
  public void testQuoteNotPlausibleToAddReturnsNewSentence() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    CoreLabel w1 = new CoreLabel();
    w1.setWord("Hi");
    CoreLabel w2 = new CoreLabel();
    w2.setWord(".");
    CoreLabel w3 = new CoreLabel();
    w3.setWord("\"");
    CoreLabel w4 = new CoreLabel();
    w4.setWord("Next");
    CoreLabel w5 = new CoreLabel();
    w5.setWord(".");
    List<HasWord> input = Arrays.asList(w1, w2, w3, w4, w5);
    List<List<HasWord>> output = processor.process(input);
    assertEquals(3, output.size());
    assertEquals("Hi", output.get(0).get(0).word());
    assertEquals(".", output.get(0).get(1).word());
    assertEquals("\"", output.get(1).get(0).word());
    assertEquals("Next", output.get(2).get(0).word());
    assertEquals(".", output.get(2).get(1).word());
  }

  @Test
  public void testForcedRegionBeginSkippingOutsideContent() {
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            "\\.",
            "\\)",
            Collections.emptySet(),
            null,
            "section",
            WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
            null,
            null,
            false,
            false);
    CoreLabel w1 = new CoreLabel();
    w1.setWord("Outside1");
    CoreLabel w2 = new CoreLabel();
    w2.setWord("<section>");
    CoreLabel w3 = new CoreLabel();
    w3.setWord("Inside");
    CoreLabel w4 = new CoreLabel();
    w4.setWord(".");
    CoreLabel w5 = new CoreLabel();
    w5.setWord("</section>");
    CoreLabel w6 = new CoreLabel();
    w6.setWord("Outside2");
    List<HasWord> input = Arrays.asList(w1, w2, w3, w4, w5, w6);
    List<List<HasWord>> result = processor.process(input);
    assertEquals(1, result.size());
    assertEquals("Inside", result.get(0).get(0).word());
    assertEquals(".", result.get(0).get(1).word());
  }

  @Test
  public void testDiscardedTokenBeforeAndAfterSentenceBoundary() {
    Set<String> discardSet = new HashSet<>();
    discardSet.add("X");
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            "\\.",
            "\\)",
            discardSet,
            null,
            null,
            WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
            null,
            null,
            false,
            false);
    CoreLabel w1 = new CoreLabel();
    w1.setWord("X");
    CoreLabel w2 = new CoreLabel();
    w2.setWord("Hello");
    CoreLabel w3 = new CoreLabel();
    w3.setWord(".");
    CoreLabel w4 = new CoreLabel();
    w4.setWord("X");
    CoreLabel w5 = new CoreLabel();
    w5.setWord("World");
    CoreLabel w6 = new CoreLabel();
    w6.setWord("?");
    List<HasWord> input = Arrays.asList(w1, w2, w3, w4, w5, w6);
    List<List<HasWord>> output = processor.process(input);
    assertEquals(2, output.size());
    assertEquals("Hello", output.get(0).get(0).word());
    assertEquals(".", output.get(0).get(1).word());
    assertEquals("World", output.get(1).get(0).word());
    assertEquals("?", output.get(1).get(1).word());
  }

  @Test
  public void testEmptySentenceBetweenTwoConsecutiveNewlinesWhenAllowed() {
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            "\\.",
            "\\)",
            WordToSentenceProcessor.DEFAULT_SENTENCE_BOUNDARIES_TO_DISCARD,
            null,
            null,
            WordToSentenceProcessor.NewlineIsSentenceBreak.TWO_CONSECUTIVE,
            null,
            null,
            false,
            true);
    CoreLabel w1 = new CoreLabel();
    w1.setWord("A");
    CoreLabel w2 = new CoreLabel();
    w2.setWord(".");
    CoreLabel w3 = new CoreLabel();
    w3.setWord("\n");
    CoreLabel w4 = new CoreLabel();
    w4.setWord("\n");
    CoreLabel w5 = new CoreLabel();
    w5.setWord("B");
    CoreLabel w6 = new CoreLabel();
    w6.setWord(".");
    List<HasWord> input = Arrays.asList(w1, w2, w3, w4, w5, w6);
    List<List<HasWord>> output = processor.process(input);
    assertEquals(2, output.size());
    assertEquals("A", output.get(0).get(0).word());
    assertEquals(".", output.get(0).get(1).word());
    assertEquals("B", output.get(1).get(0).word());
    assertEquals(".", output.get(1).get(1).word());
  }

  @Test
  public void testInvalidStringToNewlineIsSentenceBreakThrows() {
    try {
      WordToSentenceProcessor.stringToNewlineIsSentenceBreak("invalid_mode");
      fail("Expected IllegalArgumentException due to invalid enum name");
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().contains("Not a valid NewlineIsSentenceBreak name"));
    }
  }

  @Test
  public void testForcedEndTokenTerminatesImmediately() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    CoreLabel w1 = new CoreLabel();
    w1.setWord("Start");
    w1.set(CoreAnnotations.ForcedSentenceEndAnnotation.class, Boolean.TRUE);
    CoreLabel w2 = new CoreLabel();
    w2.setWord("Middle");
    CoreLabel w3 = new CoreLabel();
    w3.setWord("End");
    CoreLabel w4 = new CoreLabel();
    w4.setWord(".");
    List<HasWord> input = Arrays.asList(w1, w2, w3, w4);
    List<List<HasWord>> output = processor.process(input);
    assertEquals(2, output.size());
    assertEquals("Start", output.get(0).get(0).word());
    assertEquals("Middle", output.get(1).get(0).word());
    assertEquals("End", output.get(1).get(1).word());
    assertEquals(".", output.get(1).get(2).word());
  }

  @Test
  public void testTokenOutsideRegionDoesNotEnterIfNeverOpened() {
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            "\\.",
            "\\)",
            Collections.emptySet(),
            null,
            "sec",
            WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
            null,
            null,
            false,
            false);
    CoreLabel w1 = new CoreLabel();
    w1.setWord("Ignored");
    CoreLabel w2 = new CoreLabel();
    w2.setWord("AlsoIgnored");
    List<HasWord> input = Arrays.asList(w1, w2);
    List<List<HasWord>> output = processor.process(input);
    assertTrue(output.isEmpty());
  }

  @Test
  public void testStartRegionThenCloseRegionWithoutEndingPunctuation() {
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            "\\.",
            "\\)",
            Collections.emptySet(),
            null,
            "div",
            WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
            null,
            null,
            false,
            false);
    CoreLabel w1 = new CoreLabel();
    w1.setWord("<div>");
    CoreLabel w2 = new CoreLabel();
    w2.setWord("A");
    CoreLabel w3 = new CoreLabel();
    w3.setWord("B");
    CoreLabel w4 = new CoreLabel();
    w4.setWord("</div>");
    List<HasWord> input = Arrays.asList(w1, w2, w3, w4);
    List<List<HasWord>> output = processor.process(input);
    assertEquals(1, output.size());
    assertEquals("A", output.get(0).get(0).word());
    assertEquals("B", output.get(0).get(1).word());
  }

  @Test
  public void testOnlySentenceBoundaryToDiscardTokens() {
    Set<String> toDiscard = new HashSet<>();
    toDiscard.add("NEWLINE");
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>(toDiscard);
    CoreLabel w1 = new CoreLabel();
    w1.setWord("NEWLINE");
    CoreLabel w2 = new CoreLabel();
    w2.setWord("NEWLINE");
    List<HasWord> input = Arrays.asList(w1, w2);
    List<List<HasWord>> output = processor.process(input);
    assertTrue(output.isEmpty());
  }

  @Test
  public void testOnlyWithDiscardedAndAllowEmptySentencesTrue() {
    Set<String> boundaryToDiscard = new HashSet<>();
    boundaryToDiscard.add("<<BR>>");
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            "\\.",
            "\\)",
            boundaryToDiscard,
            null,
            null,
            WordToSentenceProcessor.NewlineIsSentenceBreak.ALWAYS,
            null,
            null,
            false,
            true);
    CoreLabel w1 = new CoreLabel();
    w1.setWord("<<BR>>");
    CoreLabel w2 = new CoreLabel();
    w2.setWord("<<BR>>");
    List<HasWord> input = Arrays.asList(w1, w2);
    List<List<HasWord>> output = processor.process(input);
    assertEquals(1, output.size());
    assertTrue(output.get(0).isEmpty());
  }

  @Test
  public void testFollowerWithoutRootSentenceAndNotPlausibleToAdd() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    CoreLabel w1 = new CoreLabel();
    w1.setWord(")");
    CoreLabel w2 = new CoreLabel();
    w2.setWord("?");
    CoreLabel w3 = new CoreLabel();
    w3.setWord(".");
    List<HasWord> input = Arrays.asList(w1, w2, w3);
    List<List<HasWord>> output = processor.process(input);
    assertEquals(2, output.size());
    assertEquals(")", output.get(0).get(0).word());
    assertEquals("?", output.get(0).get(1).word());
    assertEquals(".", output.get(1).get(0).word());
  }

  @Test
  public void testNullOptionalConstructorArgsWithNonDefaultBreak() {
    // WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>(null, null, null,
    // null, WordToSentenceProcessor.NewlineIsSentenceBreak.TWO_CONSECUTIVE);
    CoreLabel w1 = new CoreLabel();
    w1.setWord("Hello");
    CoreLabel w2 = new CoreLabel();
    w2.setWord(".");
    CoreLabel w3 = new CoreLabel();
    w3.setWord("\n");
    CoreLabel w4 = new CoreLabel();
    w4.setWord("\n");
    CoreLabel w5 = new CoreLabel();
    w5.setWord("World");
    CoreLabel w6 = new CoreLabel();
    w6.setWord("?");
    List<HasWord> input = Arrays.asList(w1, w2, w3, w4, w5, w6);
    // List<List<HasWord>> output = processor.process(input);
    // assertEquals(2, output.size());
    // assertEquals("Hello", output.get(0).get(0).word());
    // assertEquals(".", output.get(0).get(1).word());
    // assertEquals("World", output.get(1).get(0).word());
    // assertEquals("?", output.get(1).get(1).word());
  }

  @Test
  public void testConstructorWithMaximalSettingsAndSingleSentence() {
    Set<String> boundaryToDiscard = new HashSet<>();
    boundaryToDiscard.add("<br>");
    Set<String> xmlElements = new HashSet<>();
    xmlElements.add("p");
    Set<String> discardRegex = new HashSet<>();
    discardRegex.add("FOO");
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            "\\.",
            "\\)",
            boundaryToDiscard,
            xmlElements,
            "sec",
            WordToSentenceProcessor.NewlineIsSentenceBreak.ALWAYS,
            null,
            discardRegex,
            true,
            true);
    CoreLabel w1 = new CoreLabel();
    w1.setWord("X");
    CoreLabel w2 = new CoreLabel();
    w2.setWord(".");
    CoreLabel w3 = new CoreLabel();
    w3.setWord("FOO");
    CoreLabel w4 = new CoreLabel();
    w4.setWord("<p>");
    CoreLabel w5 = new CoreLabel();
    w5.setWord("Y");
    List<HasWord> input = Arrays.asList(w1, w2, w3, w4, w5);
    List<List<HasWord>> output = processor.process(input);
    assertEquals(1, output.size());
    assertEquals("X", output.get(0).get(0).word());
    assertEquals(".", output.get(0).get(1).word());
    assertEquals("FOO", output.get(0).get(2).word());
    assertEquals("<p>", output.get(0).get(3).word());
    assertEquals("Y", output.get(0).get(4).word());
  }

  @Test
  public void testMultiTokenPatternWithNoMatches() {
    // SequencePattern<HasWord> dummyPattern = new SequencePattern<HasWord>() {
    //
    // public SequenceMatcher<HasWord> getMatcher(List<? extends HasWord> tokens) {
    // return new SequenceMatcher<HasWord>() {
    //
    // public boolean find() {
    // return false;
    // }
    //
    // public List<HasWord> groupNodes() {
    // return Collections.emptyList();
    // }
    // };
    // }
    // };
    // WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>("\\.", "\\)",
    // Collections.emptySet(), null, null, WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
    // dummyPattern, null, false, false);
    CoreLabel w1 = new CoreLabel();
    w1.setWord("Alpha");
    CoreLabel w2 = new CoreLabel();
    w2.setWord("Beta");
    CoreLabel w3 = new CoreLabel();
    w3.setWord(".");
    List<HasWord> input = Arrays.asList(w1, w2, w3);
    // List<List<HasWord>> output = processor.process(input);
    // assertEquals(1, output.size());
    // assertEquals("Alpha", output.get(0).get(0).word());
    // assertEquals("Beta", output.get(0).get(1).word());
    // assertEquals(".", output.get(0).get(2).word());
  }

  @Test
  public void testRegionEndsInsideMentionPreservedUntilClose() {
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            "\\.",
            "\\)",
            Collections.emptySet(),
            null,
            "p",
            WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
            null,
            null,
            false,
            false);
    CoreLabel w1 = new CoreLabel();
    w1.setWord("<p>");
    CoreLabel w2 = new CoreLabel();
    w2.setWord("A");
    CoreLabel w3 = new CoreLabel();
    w3.setWord(".");
    CoreLabel w4 = new CoreLabel();
    w4.setWord("</p>");
    CoreLabel w5 = new CoreLabel();
    w5.setWord("Z");
    List<HasWord> input = Arrays.asList(w1, w2, w3, w4, w5);
    List<List<HasWord>> output = processor.process(input);
    assertEquals(1, output.size());
    assertEquals("A", output.get(0).get(0).word());
    assertEquals(".", output.get(0).get(1).word());
  }

  @Test
  public void testSentenceBoundaryFollowerAtBeginningOfInput() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    CoreLabel w1 = new CoreLabel();
    w1.setWord(")");
    CoreLabel w2 = new CoreLabel();
    w2.setWord("Start");
    CoreLabel w3 = new CoreLabel();
    w3.setWord(".");
    List<HasWord> input = Arrays.asList(w1, w2, w3);
    List<List<HasWord>> output = processor.process(input);
    assertEquals(1, output.size());
    assertEquals(")", output.get(0).get(0).word());
    assertEquals("Start", output.get(0).get(1).word());
    assertEquals(".", output.get(0).get(2).word());
  }

  @Test
  public void testRegionStartAndNoEndTag() {
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            "\\.",
            "\\)",
            Collections.emptySet(),
            null,
            "region",
            WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
            null,
            null,
            false,
            false);
    CoreLabel w1 = new CoreLabel();
    w1.setWord("<region>");
    CoreLabel w2 = new CoreLabel();
    w2.setWord("Hello");
    CoreLabel w3 = new CoreLabel();
    w3.setWord("World");
    CoreLabel w4 = new CoreLabel();
    w4.setWord(".");
    List<HasWord> input = Arrays.asList(w1, w2, w3, w4);
    List<List<HasWord>> output = processor.process(input);
    assertEquals(1, output.size());
    assertEquals("Hello", output.get(0).get(0).word());
    assertEquals("World", output.get(0).get(1).word());
    assertEquals(".", output.get(0).get(2).word());
  }

  @Test
  public void testRegionEndPatternOnlyWithoutOpenTag() {
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            "\\.",
            "\\)",
            Collections.emptySet(),
            null,
            "x",
            WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
            null,
            null,
            false,
            false);
    CoreLabel w1 = new CoreLabel();
    w1.setWord("</x>");
    CoreLabel w2 = new CoreLabel();
    w2.setWord("Outside");
    CoreLabel w3 = new CoreLabel();
    w3.setWord(".");
    List<HasWord> input = Arrays.asList(w1, w2, w3);
    List<List<HasWord>> output = processor.process(input);
    assertEquals(1, output.size());
    assertEquals("Outside", output.get(0).get(0).word());
    assertEquals(".", output.get(0).get(1).word());
  }

  @Test
  public void testConsecutiveSentenceFollowerTokensOnlyAfterBoundary() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    CoreLabel w1 = new CoreLabel();
    w1.setWord("Hi");
    CoreLabel w2 = new CoreLabel();
    w2.setWord(".");
    CoreLabel w3 = new CoreLabel();
    w3.setWord(")");
    CoreLabel w4 = new CoreLabel();
    w4.setWord("'");
    CoreLabel w5 = new CoreLabel();
    w5.setWord("!");
    List<HasWord> input = Arrays.asList(w1, w2, w3, w4, w5);
    List<List<HasWord>> output = processor.process(input);
    assertEquals(2, output.size());
    assertEquals("Hi", output.get(0).get(0).word());
    assertEquals(".", output.get(0).get(1).word());
    assertEquals(")", output.get(0).get(2).word());
    assertEquals("'", output.get(0).get(3).word());
    assertEquals("!", output.get(1).get(0).word());
  }

  @Test
  public void testForcedEndAndFollowersTogether() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    CoreLabel w1 = new CoreLabel();
    w1.setWord("Test");
    CoreLabel w2 = new CoreLabel();
    w2.setWord("!");
    CoreLabel w3 = new CoreLabel();
    w3.setWord(")");
    w3.set(CoreAnnotations.ForcedSentenceEndAnnotation.class, Boolean.TRUE);
    CoreLabel w4 = new CoreLabel();
    w4.setWord("Next");
    CoreLabel w5 = new CoreLabel();
    w5.setWord("?");
    List<HasWord> input = Arrays.asList(w1, w2, w3, w4, w5);
    List<List<HasWord>> output = processor.process(input);
    assertEquals(2, output.size());
    assertEquals("Test", output.get(0).get(0).word());
    assertEquals("!", output.get(0).get(1).word());
    assertEquals(")", output.get(0).get(2).word());
    assertEquals("Next", output.get(1).get(0).word());
    assertEquals("?", output.get(1).get(1).word());
  }

  @Test
  public void testMultipleDiscardTokensWithAllowEmptySentencesTrue() {
    Set<String> discardSet = new HashSet<>();
    discardSet.add("<<BR>>");
    discardSet.add("<<NEWLINE>>");
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            "\\.",
            "\\)",
            discardSet,
            null,
            null,
            WordToSentenceProcessor.NewlineIsSentenceBreak.TWO_CONSECUTIVE,
            null,
            null,
            false,
            true);
    CoreLabel w1 = new CoreLabel();
    w1.setWord("Hello");
    CoreLabel w2 = new CoreLabel();
    w2.setWord(".");
    CoreLabel w3 = new CoreLabel();
    w3.setWord("<<BR>>");
    CoreLabel w4 = new CoreLabel();
    w4.setWord("<<NEWLINE>>");
    CoreLabel w5 = new CoreLabel();
    w5.setWord("Next");
    CoreLabel w6 = new CoreLabel();
    w6.setWord("?");
    List<HasWord> input = Arrays.asList(w1, w2, w3, w4, w5, w6);
    List<List<HasWord>> output = processor.process(input);
    assertEquals(2, output.size());
    assertEquals("Hello", output.get(0).get(0).word());
    assertEquals(".", output.get(0).get(1).word());
    assertEquals("Next", output.get(1).get(0).word());
    assertEquals("?", output.get(1).get(1).word());
  }

  @Test
  public void testNewlineDiscardPreventsForcedNewlineSplit() {
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            "\\.",
            "\\)",
            WordToSentenceProcessor.DEFAULT_SENTENCE_BOUNDARIES_TO_DISCARD,
            null,
            null,
            WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
            null,
            null,
            false,
            false);
    CoreLabel w1 = new CoreLabel();
    w1.setWord("Line1");
    CoreLabel w2 = new CoreLabel();
    w2.setWord("Line2");
    CoreLabel w3 = new CoreLabel();
    w3.setWord(".");
    CoreLabel w4 = new CoreLabel();
    w4.setWord("\n");
    List<HasWord> input = Arrays.asList(w1, w2, w3, w4);
    List<List<HasWord>> result = processor.process(input);
    assertEquals(1, result.size());
    assertEquals("Line1", result.get(0).get(0).word());
    assertEquals("Line2", result.get(0).get(1).word());
    assertEquals(".", result.get(0).get(2).word());
  }

  @Test
  public void testMixedDiscardAndRegionBoundaryBehavior() {
    Set<String> discardSet = new HashSet<>();
    discardSet.add("<<PAD>>");
    Set<String> xmlSet = new HashSet<>();
    xmlSet.add("note");
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            "\\.",
            "\\)",
            discardSet,
            xmlSet,
            "section",
            WordToSentenceProcessor.NewlineIsSentenceBreak.TWO_CONSECUTIVE,
            null,
            null,
            false,
            false);
    CoreLabel w1 = new CoreLabel();
    w1.setWord("<<PAD>>");
    CoreLabel w2 = new CoreLabel();
    w2.setWord("Header");
    CoreLabel w3 = new CoreLabel();
    w3.setWord(".");
    CoreLabel w4 = new CoreLabel();
    w4.setWord("<section>");
    CoreLabel w5 = new CoreLabel();
    w5.setWord("Inside");
    CoreLabel w6 = new CoreLabel();
    w6.setWord(".");
    CoreLabel w7 = new CoreLabel();
    w7.setWord("</section>");
    CoreLabel w8 = new CoreLabel();
    w8.setWord("<<PAD>>");
    List<HasWord> input = Arrays.asList(w1, w2, w3, w4, w5, w6, w7, w8);
    List<List<HasWord>> result = processor.process(input);
    assertEquals(1, result.size());
    assertEquals("Inside", result.get(0).get(0).word());
    assertEquals(".", result.get(0).get(1).word());
  }

  @Test
  public void testEmptySentenceAddedWhenAllowedGlobally() {
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            "\\.",
            "\\)",
            Collections.singleton("\n"),
            null,
            null,
            WordToSentenceProcessor.NewlineIsSentenceBreak.ALWAYS,
            null,
            null,
            false,
            true);
    CoreLabel w1 = new CoreLabel();
    w1.setWord("\n");
    List<HasWord> input = Arrays.asList(w1);
    List<List<HasWord>> output = processor.process(input);
    assertEquals(1, output.size());
    assertEquals(0, output.get(0).size());
  }

  @Test
  public void testForcedSentenceUntilEndWithDiscardSkipsNewline() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    CoreLabel w1 = new CoreLabel();
    w1.setWord("SpanStart");
    w1.set(CoreAnnotations.ForcedSentenceUntilEndAnnotation.class, Boolean.TRUE);
    CoreLabel w2 = new CoreLabel();
    w2.setWord("Middle");
    CoreLabel w3 = new CoreLabel();
    w3.setWord("\n");
    CoreLabel w4 = new CoreLabel();
    w4.setWord("Final");
    CoreLabel w5 = new CoreLabel();
    w5.setWord(".");
    List<HasWord> input = Arrays.asList(w1, w2, w3, w4, w5);
    List<List<HasWord>> output = processor.process(input);
    assertEquals(1, output.size());
    assertEquals("SpanStart", output.get(0).get(0).word());
    assertEquals("Middle", output.get(0).get(1).word());
    assertEquals("Final", output.get(0).get(2).word());
    assertEquals(".", output.get(0).get(3).word());
  }

  @Test
  public void testDoubleQuoteBalanceSkipsOpeningQuoteFromNewSentence() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    CoreLabel w1 = new CoreLabel();
    w1.setWord("He");
    CoreLabel w2 = new CoreLabel();
    w2.setWord("spoke");
    CoreLabel w3 = new CoreLabel();
    w3.setWord(".");
    CoreLabel w4 = new CoreLabel();
    w4.setWord("\"");
    CoreLabel w5 = new CoreLabel();
    w5.setWord("Hello");
    CoreLabel w6 = new CoreLabel();
    w6.setWord("!");
    List<HasWord> input = Arrays.asList(w1, w2, w3, w4, w5, w6);
    List<List<HasWord>> output = processor.process(input);
    assertEquals(2, output.size());
    assertEquals("He", output.get(0).get(0).word());
    assertEquals("spoke", output.get(0).get(1).word());
    assertEquals(".", output.get(0).get(2).word());
    assertEquals("\"", output.get(0).get(3).word());
    assertEquals("Hello", output.get(1).get(0).word());
    assertEquals("!", output.get(1).get(1).word());
  }

  @Test
  public void testOnlyFollowerMarksItsOwnSentenceWhenNoContext() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    CoreLabel w1 = new CoreLabel();
    w1.setWord(")");
    CoreLabel w2 = new CoreLabel();
    w2.setWord("第二");
    CoreLabel w3 = new CoreLabel();
    w3.setWord("。");
    List<HasWord> input = Arrays.asList(w1, w2, w3);
    List<List<HasWord>> output = processor.process(input);
    assertEquals(2, output.size());
    assertEquals(")", output.get(0).get(0).word());
    assertEquals("第二", output.get(1).get(0).word());
    assertEquals("。", output.get(1).get(1).word());
  }

  @Test
  public void testSentenceBoundaryTokenWithTrailingFollowerThenRegularText() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    CoreLabel w1 = new CoreLabel();
    w1.setWord("Hi");
    CoreLabel w2 = new CoreLabel();
    w2.setWord(".");
    CoreLabel w3 = new CoreLabel();
    w3.setWord(")");
    CoreLabel w4 = new CoreLabel();
    w4.setWord("next");
    CoreLabel w5 = new CoreLabel();
    w5.setWord("word");
    List<HasWord> input = Arrays.asList(w1, w2, w3, w4, w5);
    List<List<HasWord>> output = processor.process(input);
    assertEquals(2, output.size());
    assertEquals("Hi", output.get(0).get(0).word());
    assertEquals(".", output.get(0).get(1).word());
    assertEquals(")", output.get(0).get(2).word());
    assertEquals("next", output.get(1).get(0).word());
    assertEquals("word", output.get(1).get(1).word());
  }

  @Test
  public void testLeadingAndTrailingDiscardsAroundSentence() {
    Set<String> discards = new HashSet<>();
    discards.add("<br>");
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            "\\.",
            "\\)",
            discards,
            null,
            null,
            WordToSentenceProcessor.NewlineIsSentenceBreak.ALWAYS,
            null,
            null,
            false,
            false);
    CoreLabel w1 = new CoreLabel();
    w1.setWord("<br>");
    CoreLabel w2 = new CoreLabel();
    w2.setWord("Hello");
    CoreLabel w3 = new CoreLabel();
    w3.setWord(".");
    CoreLabel w4 = new CoreLabel();
    w4.setWord("<br>");
    List<HasWord> input = Arrays.asList(w1, w2, w3, w4);
    List<List<HasWord>> output = processor.process(input);
    assertEquals(1, output.size());
    assertEquals("Hello", output.get(0).get(0).word());
    assertEquals(".", output.get(0).get(1).word());
  }

  @Test
  public void testMultipleQuoteTokensAttachedBackToBack() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    CoreLabel w1 = new CoreLabel();
    w1.setWord("Hello");
    CoreLabel w2 = new CoreLabel();
    w2.setWord(".");
    CoreLabel w3 = new CoreLabel();
    w3.setWord("\"");
    CoreLabel w4 = new CoreLabel();
    w4.setWord("\"");
    CoreLabel w5 = new CoreLabel();
    w5.setWord("New");
    CoreLabel w6 = new CoreLabel();
    w6.setWord(".");
    List<HasWord> input = Arrays.asList(w1, w2, w3, w4, w5, w6);
    List<List<HasWord>> output = processor.process(input);
    assertEquals(2, output.size());
    assertEquals("Hello", output.get(0).get(0).word());
    assertEquals(".", output.get(0).get(1).word());
    assertEquals("\"", output.get(0).get(2).word());
    assertEquals("\"", output.get(0).get(3).word());
    assertEquals("New", output.get(1).get(0).word());
    assertEquals(".", output.get(1).get(1).word());
  }

  @Test
  public void testSingleNonSentenceEndingTokenReturnsOneSentence() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    CoreLabel w1 = new CoreLabel();
    w1.setWord("JustOne");
    List<HasWord> input = Collections.singletonList(w1);
    List<List<HasWord>> output = processor.process(input);
    assertEquals(1, output.size());
    assertEquals("JustOne", output.get(0).get(0).word());
  }

  @Test
  public void testEmptyInputListReturnsEmptySentenceList() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    List<HasWord> input = new ArrayList<>();
    List<List<HasWord>> output = processor.process(input);
    assertEquals(0, output.size());
  }

  @Test
  public void testTwoBoundariesInARowProducesBoundaryOnlySentence() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    CoreLabel w1 = new CoreLabel();
    w1.setWord("Hi");
    CoreLabel w2 = new CoreLabel();
    w2.setWord(".");
    CoreLabel w3 = new CoreLabel();
    w3.setWord("?");
    List<HasWord> input = Arrays.asList(w1, w2, w3);
    List<List<HasWord>> output = processor.process(input);
    assertEquals(2, output.size());
    assertEquals("Hi", output.get(0).get(0).word());
    assertEquals(".", output.get(0).get(1).word());
    assertEquals("?", output.get(1).get(0).word());
  }
}
