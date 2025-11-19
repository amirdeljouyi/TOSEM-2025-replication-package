package edu.stanford.nlp.process;

import static org.junit.Assert.*;

import edu.stanford.nlp.ling.*;
import java.io.*;
import java.util.*;
import org.junit.Test;

public class WordToSentenceProcessor_2_GPTLLMTest {

  @Test
  public void testSimpleSentenceSplitting() {
    WordToSentenceProcessor<CoreLabel> processor = new WordToSentenceProcessor<>();
    CoreLabel t1 = new CoreLabel();
    t1.setWord("This");
    t1.set(CoreAnnotations.TextAnnotation.class, "This");
    t1.set(CoreAnnotations.OriginalTextAnnotation.class, "This");
    CoreLabel t2 = new CoreLabel();
    t2.setWord("is");
    t2.set(CoreAnnotations.TextAnnotation.class, "is");
    t2.set(CoreAnnotations.OriginalTextAnnotation.class, "is");
    CoreLabel t3 = new CoreLabel();
    t3.setWord("one");
    t3.set(CoreAnnotations.TextAnnotation.class, "one");
    t3.set(CoreAnnotations.OriginalTextAnnotation.class, "one");
    CoreLabel t4 = new CoreLabel();
    t4.setWord("sentence");
    t4.set(CoreAnnotations.TextAnnotation.class, "sentence");
    t4.set(CoreAnnotations.OriginalTextAnnotation.class, "sentence");
    CoreLabel t5 = new CoreLabel();
    t5.setWord(".");
    t5.set(CoreAnnotations.TextAnnotation.class, ".");
    t5.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
    CoreLabel t6 = new CoreLabel();
    t6.setWord("This");
    t6.set(CoreAnnotations.TextAnnotation.class, "This");
    t6.set(CoreAnnotations.OriginalTextAnnotation.class, "This");
    CoreLabel t7 = new CoreLabel();
    t7.setWord("is");
    t7.set(CoreAnnotations.TextAnnotation.class, "is");
    t7.set(CoreAnnotations.OriginalTextAnnotation.class, "is");
    CoreLabel t8 = new CoreLabel();
    t8.setWord("second");
    t8.set(CoreAnnotations.TextAnnotation.class, "second");
    t8.set(CoreAnnotations.OriginalTextAnnotation.class, "second");
    CoreLabel t9 = new CoreLabel();
    t9.setWord(".");
    t9.set(CoreAnnotations.TextAnnotation.class, ".");
    t9.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
    List<CoreLabel> input = Arrays.asList(t1, t2, t3, t4, t5, t6, t7, t8, t9);
    List<List<CoreLabel>> output = processor.process(input);
    assertEquals(2, output.size());
    assertEquals(5, output.get(0).size());
    assertEquals("sentence", output.get(0).get(3).word());
    assertEquals(".", output.get(0).get(4).word());
  }

  @Test
  public void testSingleSentenceMode() {
    WordToSentenceProcessor<CoreLabel> processor = new WordToSentenceProcessor<>(true);
    CoreLabel a = new CoreLabel();
    a.setWord("This");
    a.set(CoreAnnotations.TextAnnotation.class, "This");
    a.set(CoreAnnotations.OriginalTextAnnotation.class, "This");
    CoreLabel b = new CoreLabel();
    b.setWord("is");
    b.set(CoreAnnotations.TextAnnotation.class, "is");
    b.set(CoreAnnotations.OriginalTextAnnotation.class, "is");
    CoreLabel c = new CoreLabel();
    c.setWord("a");
    c.set(CoreAnnotations.TextAnnotation.class, "a");
    c.set(CoreAnnotations.OriginalTextAnnotation.class, "a");
    CoreLabel d = new CoreLabel();
    d.setWord(".");
    d.set(CoreAnnotations.TextAnnotation.class, ".");
    d.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
    CoreLabel e = new CoreLabel();
    e.setWord("Single");
    e.set(CoreAnnotations.TextAnnotation.class, "Single");
    e.set(CoreAnnotations.OriginalTextAnnotation.class, "Single");
    CoreLabel f = new CoreLabel();
    f.setWord("sentence");
    f.set(CoreAnnotations.TextAnnotation.class, "sentence");
    f.set(CoreAnnotations.OriginalTextAnnotation.class, "sentence");
    CoreLabel g = new CoreLabel();
    g.setWord(".");
    g.set(CoreAnnotations.TextAnnotation.class, ".");
    g.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
    List<CoreLabel> tokens = Arrays.asList(a, b, c, d, e, f, g);
    List<List<CoreLabel>> result = processor.process(tokens);
    assertEquals(1, result.size());
    assertEquals(7, result.get(0).size());
  }

  @Test
  public void testForcedEndToken() {
    WordToSentenceProcessor<CoreLabel> processor = new WordToSentenceProcessor<>();
    CoreLabel a = new CoreLabel();
    a.setWord("Hello");
    a.set(CoreAnnotations.TextAnnotation.class, "Hello");
    a.set(CoreAnnotations.OriginalTextAnnotation.class, "Hello");
    CoreLabel b = new CoreLabel();
    b.setWord("World");
    b.set(CoreAnnotations.TextAnnotation.class, "World");
    b.set(CoreAnnotations.OriginalTextAnnotation.class, "World");
    CoreLabel end = new CoreLabel();
    end.setWord("¶");
    end.set(CoreAnnotations.TextAnnotation.class, "¶");
    end.set(CoreAnnotations.OriginalTextAnnotation.class, "¶");
    end.set(CoreAnnotations.ForcedSentenceEndAnnotation.class, true);
    CoreLabel c = new CoreLabel();
    c.setWord("Next");
    c.set(CoreAnnotations.TextAnnotation.class, "Next");
    c.set(CoreAnnotations.OriginalTextAnnotation.class, "Next");
    CoreLabel d = new CoreLabel();
    d.setWord(".");
    d.set(CoreAnnotations.TextAnnotation.class, ".");
    d.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
    List<CoreLabel> input = Arrays.asList(a, b, end, c, d);
    List<List<CoreLabel>> output = processor.process(input);
    assertEquals(2, output.size());
    assertEquals("Hello", output.get(0).get(0).word());
    assertEquals("Next", output.get(1).get(0).word());
  }

  @Test
  public void testNewlineAlwaysSplitting() {
    WordToSentenceProcessor<CoreLabel> processor =
        new WordToSentenceProcessor<>(WordToSentenceProcessor.NewlineIsSentenceBreak.ALWAYS);
    CoreLabel a = new CoreLabel();
    a.setWord("First");
    a.set(CoreAnnotations.TextAnnotation.class, "First");
    a.set(CoreAnnotations.OriginalTextAnnotation.class, "First");
    CoreLabel b = new CoreLabel();
    b.setWord(".");
    b.set(CoreAnnotations.TextAnnotation.class, ".");
    b.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
    CoreLabel c = new CoreLabel();
    c.setWord("\n");
    c.set(CoreAnnotations.TextAnnotation.class, "\n");
    c.set(CoreAnnotations.OriginalTextAnnotation.class, "\n");
    CoreLabel d = new CoreLabel();
    d.setWord("Second");
    d.set(CoreAnnotations.TextAnnotation.class, "Second");
    d.set(CoreAnnotations.OriginalTextAnnotation.class, "Second");
    CoreLabel e = new CoreLabel();
    e.setWord(".");
    e.set(CoreAnnotations.TextAnnotation.class, ".");
    e.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
    List<CoreLabel> tokens = Arrays.asList(a, b, c, d, e);
    List<List<CoreLabel>> output = processor.process(tokens);
    assertEquals(3, output.size());
    assertEquals("First", output.get(0).get(0).word());
    assertEquals("Second", output.get(1).get(0).word());
    assertEquals(".", output.get(2).get(0).word());
  }

  @Test
  public void testEmptyInputReturnsEmptyList() {
    WordToSentenceProcessor<CoreLabel> processor = new WordToSentenceProcessor<>();
    List<CoreLabel> empty = Collections.emptyList();
    List<List<CoreLabel>> result = processor.process(empty);
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidNewlineStringThrowsException() {
    WordToSentenceProcessor.stringToNewlineIsSentenceBreak("unsupported_mode");
  }

  @Test
  public void testMultiTokenPatternSimulated() {
    // SequencePattern<CoreLabel> fakePattern = new SequencePattern<CoreLabel>() {
    //
    // public SequenceMatcher<CoreLabel> getMatcher(List<? extends CoreLabel> list) {
    // return new SequenceMatcher<CoreLabel>() {
    //
    // private boolean found = false;
    //
    // public boolean find() {
    // boolean result = !found;
    // found = true;
    // return result;
    // }
    //
    // public List<CoreLabel> groupNodes() {
    // return list.subList(list.size() - 1, list.size());
    // }
    // };
    // }
    // };
    // WordToSentenceProcessor<CoreLabel> processor = new WordToSentenceProcessor<>("[.]", null, new
    // HashSet<>(Arrays.asList("\n")), null, null,
    // WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER, fakePattern, null, false, false);
    CoreLabel a = new CoreLabel();
    a.setWord("Line");
    a.set(CoreAnnotations.TextAnnotation.class, "Line");
    a.set(CoreAnnotations.OriginalTextAnnotation.class, "Line");
    CoreLabel b = new CoreLabel();
    b.setWord("break");
    b.set(CoreAnnotations.TextAnnotation.class, "break");
    b.set(CoreAnnotations.OriginalTextAnnotation.class, "break");
    CoreLabel c = new CoreLabel();
    c.setWord("now");
    c.set(CoreAnnotations.TextAnnotation.class, "now");
    c.set(CoreAnnotations.OriginalTextAnnotation.class, "now");
    List<CoreLabel> tokens = Arrays.asList(a, b, c);
    // List<List<CoreLabel>> output = processor.process(tokens);
    // assertEquals(2, output.size());
    // assertEquals("Line", output.get(0).get(0).word());
    // assertEquals("now", output.get(1).get(0).word());
  }

  @Test
  public void testSentenceWithBoundaryFollowerAtStart() {
    WordToSentenceProcessor<CoreLabel> processor = new WordToSentenceProcessor<>();
    CoreLabel a = new CoreLabel();
    a.setWord(".");
    a.set(CoreAnnotations.TextAnnotation.class, ".");
    a.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
    CoreLabel b = new CoreLabel();
    b.setWord(")");
    b.set(CoreAnnotations.TextAnnotation.class, ")");
    b.set(CoreAnnotations.OriginalTextAnnotation.class, ")");
    List<CoreLabel> tokens = Arrays.asList(a, b);
    List<List<CoreLabel>> result = processor.process(tokens);
    assertEquals(2, result.size());
    assertEquals(".", result.get(0).get(0).word());
    assertEquals(")", result.get(1).get(0).word());
  }

  @Test
  public void testQuoteCountingOddClosingQuoteNotAppended() {
    WordToSentenceProcessor<CoreLabel> processor = new WordToSentenceProcessor<>();
    CoreLabel a = new CoreLabel();
    a.setWord("'");
    a.set(CoreAnnotations.TextAnnotation.class, "'");
    a.set(CoreAnnotations.OriginalTextAnnotation.class, "'");
    CoreLabel b = new CoreLabel();
    b.setWord("'");
    b.set(CoreAnnotations.TextAnnotation.class, "'");
    b.set(CoreAnnotations.OriginalTextAnnotation.class, "'");
    CoreLabel c = new CoreLabel();
    c.setWord(".");
    c.set(CoreAnnotations.TextAnnotation.class, ".");
    c.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
    List<CoreLabel> list = Arrays.asList(a, b, c);
    List<List<CoreLabel>> output = processor.process(list);
    assertEquals(2, output.size());
    assertEquals("'", output.get(0).get(0).word());
    assertEquals("'", output.get(0).get(1).word());
    assertEquals(".", output.get(1).get(0).word());
  }

  @Test
  public void testSentenceEndingWithXmlBreakElement() {
    Set<String> discard = new HashSet<>(Collections.singletonList("\n"));
    Set<String> xmlBreak = new HashSet<>(Collections.singletonList("p"));
    WordToSentenceProcessor<CoreLabel> processor =
        new WordToSentenceProcessor<>(
            "\\.",
            "\\)",
            discard,
            xmlBreak,
            null,
            WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
            null,
            null,
            false,
            false);
    CoreLabel a = new CoreLabel();
    a.setWord("Hello");
    a.set(CoreAnnotations.TextAnnotation.class, "Hello");
    a.set(CoreAnnotations.OriginalTextAnnotation.class, "Hello");
    CoreLabel b = new CoreLabel();
    b.setWord("<p>");
    b.set(CoreAnnotations.TextAnnotation.class, "<p>");
    b.set(CoreAnnotations.OriginalTextAnnotation.class, "<p>");
    CoreLabel c = new CoreLabel();
    c.setWord("World");
    c.set(CoreAnnotations.TextAnnotation.class, "World");
    c.set(CoreAnnotations.OriginalTextAnnotation.class, "World");
    CoreLabel d = new CoreLabel();
    d.setWord(".");
    d.set(CoreAnnotations.TextAnnotation.class, ".");
    d.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
    List<CoreLabel> tokens = Arrays.asList(a, b, c, d);
    List<List<CoreLabel>> sentences = processor.process(tokens);
    assertEquals(2, sentences.size());
    assertEquals("Hello", sentences.get(0).get(0).word());
    assertEquals("World", sentences.get(1).get(0).word());
  }

  @Test
  public void testSentenceFinalTokenWithDisallowedRegion() {
    Set<String> discard = new HashSet<>(Collections.singletonList("\n"));
    WordToSentenceProcessor<CoreLabel> processor =
        new WordToSentenceProcessor<>(
            "\\.",
            "\\)",
            discard,
            null,
            "text",
            WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
            null,
            null,
            false,
            false);
    CoreLabel a = new CoreLabel();
    a.setWord("<text>");
    a.set(CoreAnnotations.TextAnnotation.class, "<text>");
    a.set(CoreAnnotations.OriginalTextAnnotation.class, "<text>");
    CoreLabel b = new CoreLabel();
    b.setWord("Valid");
    b.set(CoreAnnotations.TextAnnotation.class, "Valid");
    b.set(CoreAnnotations.OriginalTextAnnotation.class, "Valid");
    CoreLabel c = new CoreLabel();
    c.setWord(".");
    c.set(CoreAnnotations.TextAnnotation.class, ".");
    c.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
    CoreLabel d = new CoreLabel();
    d.setWord("</text>");
    d.set(CoreAnnotations.TextAnnotation.class, "</text>");
    d.set(CoreAnnotations.OriginalTextAnnotation.class, "</text>");
    CoreLabel e = new CoreLabel();
    e.setWord("Outside");
    e.set(CoreAnnotations.TextAnnotation.class, "Outside");
    e.set(CoreAnnotations.OriginalTextAnnotation.class, "Outside");
    List<CoreLabel> tokens = Arrays.asList(a, b, c, d, e);
    List<List<CoreLabel>> sentences = processor.process(tokens);
    assertEquals(1, sentences.size());
    assertEquals("Valid", sentences.get(0).get(0).word());
  }

  @Test
  public void testTokenMatchesDiscardPatternOnly() {
    Set<String> discard = new HashSet<>(Arrays.asList("###"));
    Set<String> regexToDiscard = new HashSet<>(Arrays.asList("###"));
    WordToSentenceProcessor<CoreLabel> processor =
        new WordToSentenceProcessor<>(
            "\\.",
            "\\)",
            discard,
            null,
            null,
            WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
            null,
            regexToDiscard,
            false,
            false);
    CoreLabel a = new CoreLabel();
    a.setWord("Keep");
    a.set(CoreAnnotations.TextAnnotation.class, "Keep");
    a.set(CoreAnnotations.OriginalTextAnnotation.class, "Keep");
    CoreLabel b = new CoreLabel();
    b.setWord("###");
    b.set(CoreAnnotations.TextAnnotation.class, "###");
    b.set(CoreAnnotations.OriginalTextAnnotation.class, "###");
    CoreLabel c = new CoreLabel();
    c.setWord("Me");
    c.set(CoreAnnotations.TextAnnotation.class, "Me");
    c.set(CoreAnnotations.OriginalTextAnnotation.class, "Me");
    CoreLabel d = new CoreLabel();
    d.setWord(".");
    d.set(CoreAnnotations.TextAnnotation.class, ".");
    d.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
    List<CoreLabel> tokens = Arrays.asList(a, b, c, d);
    List<List<CoreLabel>> sentences = processor.process(tokens);
    assertEquals(1, sentences.size());
    List<CoreLabel> result = sentences.get(0);
    assertEquals(3, result.size());
    assertEquals("Keep", result.get(0).word());
    assertEquals("Me", result.get(1).word());
    assertEquals(".", result.get(2).word());
  }

  @Test
  public void testAllowEmptySentenceTrueWithOnlyNewlines() {
    Set<String> newlineSet = new HashSet<>(Collections.singletonList("\n"));
    WordToSentenceProcessor<CoreLabel> processor =
        new WordToSentenceProcessor<>(
            "",
            "",
            newlineSet,
            null,
            null,
            WordToSentenceProcessor.NewlineIsSentenceBreak.ALWAYS,
            null,
            null,
            false,
            true);
    CoreLabel a = new CoreLabel();
    a.setWord("\n");
    a.set(CoreAnnotations.TextAnnotation.class, "\n");
    a.set(CoreAnnotations.OriginalTextAnnotation.class, "\n");
    CoreLabel b = new CoreLabel();
    b.setWord("\n");
    b.set(CoreAnnotations.TextAnnotation.class, "\n");
    b.set(CoreAnnotations.OriginalTextAnnotation.class, "\n");
    List<CoreLabel> tokenList = Arrays.asList(a, b);
    List<List<CoreLabel>> sentences = processor.process(tokenList);
    assertEquals(2, sentences.size());
    assertTrue(sentences.get(0).isEmpty());
    assertTrue(sentences.get(1).isEmpty());
  }

  @Test
  public void testOnlyDiscardTokens_NoOutputWhenEmptyNotAllowed() {
    Set<String> newlineSet = new HashSet<>(Collections.singletonList("\n"));
    WordToSentenceProcessor<CoreLabel> processor =
        new WordToSentenceProcessor<>(
            "",
            "",
            newlineSet,
            null,
            null,
            WordToSentenceProcessor.NewlineIsSentenceBreak.ALWAYS,
            null,
            null,
            false,
            false);
    CoreLabel a = new CoreLabel();
    a.setWord("\n");
    a.set(CoreAnnotations.TextAnnotation.class, "\n");
    a.set(CoreAnnotations.OriginalTextAnnotation.class, "\n");
    CoreLabel b = new CoreLabel();
    b.setWord("\n");
    b.set(CoreAnnotations.TextAnnotation.class, "\n");
    b.set(CoreAnnotations.OriginalTextAnnotation.class, "\n");
    List<CoreLabel> tokens = Arrays.asList(a, b);
    List<List<CoreLabel>> sentences = processor.process(tokens);
    assertEquals(0, sentences.size());
  }

  @Test
  public void testTwoConsecutiveNewlinesTriggersSplit() {
    WordToSentenceProcessor<CoreLabel> processor =
        new WordToSentenceProcessor<>(
            WordToSentenceProcessor.NewlineIsSentenceBreak.TWO_CONSECUTIVE);
    CoreLabel a = new CoreLabel();
    a.setWord("Sentence");
    a.set(CoreAnnotations.TextAnnotation.class, "Sentence");
    a.set(CoreAnnotations.OriginalTextAnnotation.class, "Sentence");
    CoreLabel b = new CoreLabel();
    b.setWord(".");
    b.set(CoreAnnotations.TextAnnotation.class, ".");
    b.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
    CoreLabel nl1 = new CoreLabel();
    nl1.setWord("\n");
    nl1.set(CoreAnnotations.TextAnnotation.class, "\n");
    nl1.set(CoreAnnotations.OriginalTextAnnotation.class, "\n");
    CoreLabel nl2 = new CoreLabel();
    nl2.setWord("\n");
    nl2.set(CoreAnnotations.TextAnnotation.class, "\n");
    nl2.set(CoreAnnotations.OriginalTextAnnotation.class, "\n");
    CoreLabel c = new CoreLabel();
    c.setWord("Next");
    c.set(CoreAnnotations.TextAnnotation.class, "Next");
    c.set(CoreAnnotations.OriginalTextAnnotation.class, "Next");
    CoreLabel d = new CoreLabel();
    d.setWord(".");
    d.set(CoreAnnotations.TextAnnotation.class, ".");
    d.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
    List<CoreLabel> input = Arrays.asList(a, b, nl1, nl2, c, d);
    List<List<CoreLabel>> output = processor.process(input);
    assertEquals(2, output.size());
    assertEquals("Sentence", output.get(0).get(0).word());
    assertEquals("Next", output.get(1).get(0).word());
  }

  @Test
  public void testTokenWithForcedSentenceUntilEndPreventsSplitUntilForcedEnd() {
    WordToSentenceProcessor<CoreLabel> processor = new WordToSentenceProcessor<>();
    CoreLabel a = new CoreLabel();
    a.setWord("Join");
    a.set(CoreAnnotations.TextAnnotation.class, "Join");
    a.set(CoreAnnotations.OriginalTextAnnotation.class, "Join");
    a.set(CoreAnnotations.ForcedSentenceUntilEndAnnotation.class, true);
    CoreLabel b = new CoreLabel();
    b.setWord("these");
    b.set(CoreAnnotations.TextAnnotation.class, "these");
    b.set(CoreAnnotations.OriginalTextAnnotation.class, "these");
    CoreLabel c = new CoreLabel();
    c.setWord(".");
    c.set(CoreAnnotations.TextAnnotation.class, ".");
    c.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
    CoreLabel d = new CoreLabel();
    d.setWord("Next");
    d.set(CoreAnnotations.TextAnnotation.class, "Next");
    d.set(CoreAnnotations.OriginalTextAnnotation.class, "Next");
    d.set(CoreAnnotations.ForcedSentenceEndAnnotation.class, true);
    List<CoreLabel> input = Arrays.asList(a, b, c, d);
    List<List<CoreLabel>> output = processor.process(input);
    assertEquals(1, output.size());
    assertEquals("Join", output.get(0).get(0).word());
    assertEquals("Next", output.get(0).get(3).word());
  }

  @Test
  public void testSentenceWithPunctuationFollowerWhenLastSentenceIsNull() {
    WordToSentenceProcessor<CoreLabel> processor = new WordToSentenceProcessor<>();
    CoreLabel follower = new CoreLabel();
    follower.setWord(")");
    follower.set(CoreAnnotations.TextAnnotation.class, ")");
    follower.set(CoreAnnotations.OriginalTextAnnotation.class, ")");
    List<CoreLabel> input = Collections.singletonList(follower);
    List<List<CoreLabel>> output = processor.process(input);
    assertEquals(1, output.size());
    assertEquals(")", output.get(0).get(0).word());
  }

  @Test
  public void testTokenIsCoreMapButNotAnnotationKeys() {
    WordToSentenceProcessor<CoreLabel> processor = new WordToSentenceProcessor<>();
    CoreLabel token = new CoreLabel();
    token.setWord("Hello");
    token.set(CoreAnnotations.TextAnnotation.class, "Hello");
    token.set(CoreAnnotations.OriginalTextAnnotation.class, "Hello");
    CoreLabel fake = new CoreLabel();
    fake.setWord("\u2029");
    fake.set(CoreAnnotations.TextAnnotation.class, "\u2029");
    fake.set(CoreAnnotations.OriginalTextAnnotation.class, "\u2029");
    List<CoreLabel> tokens = Arrays.asList(token, fake);
    List<List<CoreLabel>> result = processor.process(tokens);
    assertEquals(2, result.size());
    assertEquals("Hello", result.get(0).get(0).word());
    assertEquals("\u2029", result.get(1).get(0).word());
  }

  @Test
  public void testFallbackToPlainTextWhenNoAnnotationsSet() {
    WordToSentenceProcessor<Object> processor = new WordToSentenceProcessor<>();
    Object a = "Hello";
    Object b = ".";
    Object c = ")";
    List<Object> input = Arrays.asList(a, b, c);
    List<List<Object>> output = processor.process(input);
    assertEquals(1, output.size());
    assertEquals("Hello", output.get(0).get(0));
    assertEquals(".", output.get(0).get(1));
    assertEquals(")", output.get(0).get(2));
  }

  @Test
  public void testPreserveMultipleFollowedByFollowerWithoutSentenceBoundary() {
    WordToSentenceProcessor<CoreLabel> processor = new WordToSentenceProcessor<>();
    CoreLabel a = new CoreLabel();
    a.setWord(".");
    a.set(CoreAnnotations.TextAnnotation.class, ".");
    a.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
    CoreLabel b = new CoreLabel();
    b.setWord(")");
    b.set(CoreAnnotations.TextAnnotation.class, ")");
    b.set(CoreAnnotations.OriginalTextAnnotation.class, ")");
    CoreLabel c = new CoreLabel();
    c.setWord("]");
    c.set(CoreAnnotations.TextAnnotation.class, "]");
    c.set(CoreAnnotations.OriginalTextAnnotation.class, "]");
    List<CoreLabel> input = Arrays.asList(a, b, c);
    List<List<CoreLabel>> output = processor.process(input);
    assertEquals(1, output.size());
    assertEquals(3, output.get(0).size());
    assertEquals(".", output.get(0).get(0).word());
    assertEquals(")", output.get(0).get(1).word());
    assertEquals("]", output.get(0).get(2).word());
  }

  @Test
  public void testHandlesMultipleSentencesWithIrregularSpacingAndTags() {
    Set<String> discard = new HashSet<>(Collections.singletonList("<br>"));
    Set<String> xmlBreakTags = new HashSet<>(Collections.singletonList("b"));
    WordToSentenceProcessor<CoreLabel> processor =
        new WordToSentenceProcessor<>(
            "\\.",
            "\\p{Pe}",
            discard,
            xmlBreakTags,
            null,
            WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
            null,
            null,
            false,
            false);
    CoreLabel a = new CoreLabel();
    a.setWord("First");
    a.set(CoreAnnotations.TextAnnotation.class, "First");
    a.set(CoreAnnotations.OriginalTextAnnotation.class, "First");
    CoreLabel b = new CoreLabel();
    b.setWord(".");
    b.set(CoreAnnotations.TextAnnotation.class, ".");
    b.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
    CoreLabel tag = new CoreLabel();
    tag.setWord("<b>");
    tag.set(CoreAnnotations.TextAnnotation.class, "<b>");
    tag.set(CoreAnnotations.OriginalTextAnnotation.class, "<b>");
    CoreLabel c = new CoreLabel();
    c.setWord("Second");
    c.set(CoreAnnotations.TextAnnotation.class, "Second");
    c.set(CoreAnnotations.OriginalTextAnnotation.class, "Second");
    CoreLabel d = new CoreLabel();
    d.setWord(".");
    d.set(CoreAnnotations.TextAnnotation.class, ".");
    d.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
    CoreLabel spacer = new CoreLabel();
    spacer.setWord("<br>");
    spacer.set(CoreAnnotations.TextAnnotation.class, "<br>");
    spacer.set(CoreAnnotations.OriginalTextAnnotation.class, "<br>");
    List<CoreLabel> input = Arrays.asList(a, b, tag, c, d, spacer);
    List<List<CoreLabel>> output = processor.process(input);
    assertEquals(2, output.size());
    assertEquals("First", output.get(0).get(0).word());
    assertEquals("Second", output.get(1).get(0).word());
  }

  @Test
  public void testMultiTokenMentionSuppressesSplit() {
    WordToSentenceProcessor<CoreLabel> processor = new WordToSentenceProcessor<>();
    CoreLabel a = new CoreLabel();
    a.setWord("Barack");
    a.set(CoreAnnotations.TextAnnotation.class, "Barack");
    a.set(CoreAnnotations.OriginalTextAnnotation.class, "Barack");
    // a.set(CoreAnnotations.MentionTokenAnnotation.class, new MultiTokenTag(0, 1, false));
    CoreLabel b = new CoreLabel();
    b.setWord("Obama");
    b.set(CoreAnnotations.TextAnnotation.class, "Obama");
    b.set(CoreAnnotations.OriginalTextAnnotation.class, "Obama");
    // b.set(CoreAnnotations.MentionTokenAnnotation.class, new MultiTokenTag(0, 1, true));
    CoreLabel c = new CoreLabel();
    c.setWord(".");
    c.set(CoreAnnotations.TextAnnotation.class, ".");
    c.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
    List<CoreLabel> input = Arrays.asList(a, b, c);
    List<List<CoreLabel>> output = processor.process(input);
    assertEquals(1, output.size());
    assertEquals("Barack", output.get(0).get(0).word());
    assertEquals("Obama", output.get(0).get(1).word());
    assertEquals(".", output.get(0).get(2).word());
  }

  @Test
  public void testNestedQuoteNotAppendedToLastSentence() {
    WordToSentenceProcessor<CoreLabel> processor = new WordToSentenceProcessor<>();
    CoreLabel a = new CoreLabel();
    a.setWord("'");
    a.set(CoreAnnotations.TextAnnotation.class, "'");
    a.set(CoreAnnotations.OriginalTextAnnotation.class, "'");
    CoreLabel b = new CoreLabel();
    b.setWord("He");
    b.set(CoreAnnotations.TextAnnotation.class, "He");
    b.set(CoreAnnotations.OriginalTextAnnotation.class, "He");
    CoreLabel c = new CoreLabel();
    c.setWord("said");
    c.set(CoreAnnotations.TextAnnotation.class, "said");
    c.set(CoreAnnotations.OriginalTextAnnotation.class, "said");
    CoreLabel d = new CoreLabel();
    d.setWord("'");
    d.set(CoreAnnotations.TextAnnotation.class, "'");
    d.set(CoreAnnotations.OriginalTextAnnotation.class, "'");
    CoreLabel e = new CoreLabel();
    e.setWord(".");
    e.set(CoreAnnotations.TextAnnotation.class, ".");
    e.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
    CoreLabel f = new CoreLabel();
    f.setWord("'");
    f.set(CoreAnnotations.TextAnnotation.class, "'");
    f.set(CoreAnnotations.OriginalTextAnnotation.class, "'");
    List<CoreLabel> input = Arrays.asList(a, b, c, d, e, f);
    List<List<CoreLabel>> output = processor.process(input);
    assertEquals(1, output.size());
    assertEquals("'", output.get(0).get(0).word());
    assertEquals("He", output.get(0).get(1).word());
    assertEquals("said", output.get(0).get(2).word());
    assertEquals("'", output.get(0).get(3).word());
    assertEquals(".", output.get(0).get(4).word());
    assertEquals("'", output.get(0).get(5).word());
  }

  @Test
  public void testEmptyInputWithAllowEmptySentencesTrue() {
    WordToSentenceProcessor<CoreLabel> processor =
        new WordToSentenceProcessor<>(
            "\\.",
            "\\)",
            Collections.emptySet(),
            null,
            null,
            WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
            null,
            null,
            false,
            true);
    List<CoreLabel> empty = new ArrayList<>();
    List<List<CoreLabel>> output = processor.process(empty);
    assertNotNull(output);
    assertEquals(0, output.size());
  }

  @Test
  public void testOnlyFollowerTokenAtBeginning() {
    WordToSentenceProcessor<CoreLabel> processor = new WordToSentenceProcessor<>();
    CoreLabel input = new CoreLabel();
    input.setWord(")");
    input.set(CoreAnnotations.TextAnnotation.class, ")");
    input.set(CoreAnnotations.OriginalTextAnnotation.class, ")");
    List<CoreLabel> tokens = Collections.singletonList(input);
    List<List<CoreLabel>> output = processor.process(tokens);
    assertEquals(1, output.size());
    assertEquals(")", output.get(0).get(0).word());
  }

  @Test
  public void testXMLBreakElementTriggersSplitAndDiscard() {
    Set<String> xmlElements = new HashSet<>(Arrays.asList("div"));
    WordToSentenceProcessor<CoreLabel> processor =
        new WordToSentenceProcessor<>(
            "\\.",
            "\\)",
            Collections.emptySet(),
            xmlElements,
            null,
            WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
            null,
            null,
            false,
            false);
    CoreLabel a = new CoreLabel();
    a.setWord("Hello");
    a.set(CoreAnnotations.TextAnnotation.class, "Hello");
    a.set(CoreAnnotations.OriginalTextAnnotation.class, "Hello");
    CoreLabel b = new CoreLabel();
    b.setWord("<div>");
    b.set(CoreAnnotations.TextAnnotation.class, "<div>");
    b.set(CoreAnnotations.OriginalTextAnnotation.class, "<div>");
    CoreLabel c = new CoreLabel();
    c.setWord("World");
    c.set(CoreAnnotations.TextAnnotation.class, "World");
    c.set(CoreAnnotations.OriginalTextAnnotation.class, "World");
    CoreLabel d = new CoreLabel();
    d.setWord(".");
    d.set(CoreAnnotations.TextAnnotation.class, ".");
    d.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
    List<CoreLabel> input = Arrays.asList(a, b, c, d);
    List<List<CoreLabel>> result = processor.process(input);
    assertEquals(2, result.size());
    assertEquals("Hello", result.get(0).get(0).word());
    assertEquals("World", result.get(1).get(0).word());
    assertEquals(".", result.get(1).get(1).word());
  }

  @Test
  public void testRegionStartOmitsPreRegionToken() {
    WordToSentenceProcessor<CoreLabel> processor =
        new WordToSentenceProcessor<>(
            "\\.",
            "\\)",
            Collections.emptySet(),
            null,
            "myregion",
            WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
            null,
            null,
            false,
            false);
    CoreLabel a = new CoreLabel();
    a.setWord("FOO");
    a.set(CoreAnnotations.TextAnnotation.class, "FOO");
    a.set(CoreAnnotations.OriginalTextAnnotation.class, "FOO");
    CoreLabel b = new CoreLabel();
    b.setWord("<myregion>");
    b.set(CoreAnnotations.TextAnnotation.class, "<myregion>");
    b.set(CoreAnnotations.OriginalTextAnnotation.class, "<myregion>");
    CoreLabel c = new CoreLabel();
    c.setWord("Inside");
    c.set(CoreAnnotations.TextAnnotation.class, "Inside");
    c.set(CoreAnnotations.OriginalTextAnnotation.class, "Inside");
    CoreLabel d = new CoreLabel();
    d.setWord(".");
    d.set(CoreAnnotations.TextAnnotation.class, ".");
    d.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
    CoreLabel e = new CoreLabel();
    e.setWord("</myregion>");
    e.set(CoreAnnotations.TextAnnotation.class, "</myregion>");
    e.set(CoreAnnotations.OriginalTextAnnotation.class, "</myregion>");
    CoreLabel f = new CoreLabel();
    f.setWord("OUTSIDE");
    f.set(CoreAnnotations.TextAnnotation.class, "OUTSIDE");
    f.set(CoreAnnotations.OriginalTextAnnotation.class, "OUTSIDE");
    List<CoreLabel> tokens = Arrays.asList(a, b, c, d, e, f);
    List<List<CoreLabel>> result = processor.process(tokens);
    assertEquals(1, result.size());
    assertEquals("Inside", result.get(0).get(0).word());
    assertEquals(".", result.get(0).get(1).word());
  }

  @Test
  public void testNullDiscardSetUsesDefault() {
    WordToSentenceProcessor<CoreLabel> processor =
        new WordToSentenceProcessor<>(
            "\\.",
            "\\)",
            null,
            null,
            null,
            WordToSentenceProcessor.NewlineIsSentenceBreak.ALWAYS,
            null,
            null,
            false,
            true);
    CoreLabel a = new CoreLabel();
    a.setWord("Word");
    a.set(CoreAnnotations.TextAnnotation.class, "Word");
    a.set(CoreAnnotations.OriginalTextAnnotation.class, "Word");
    CoreLabel newline = new CoreLabel();
    newline.setWord("\n");
    newline.set(CoreAnnotations.TextAnnotation.class, "\n");
    newline.set(CoreAnnotations.OriginalTextAnnotation.class, "\n");
    CoreLabel b = new CoreLabel();
    b.setWord("Next");
    b.set(CoreAnnotations.TextAnnotation.class, "Next");
    b.set(CoreAnnotations.OriginalTextAnnotation.class, "Next");
    List<CoreLabel> input = Arrays.asList(a, newline, b);
    List<List<CoreLabel>> output = processor.process(input);
    assertEquals(2, output.size());
    assertEquals("Word", output.get(0).get(0).word());
    assertEquals("Next", output.get(1).get(0).word());
  }

  @Test
  public void testConstructorWithBoundaryToDiscardOnlyAndEmptyLineHandled() {
    Set<String> boundariesToDiscard = new HashSet<>();
    boundariesToDiscard.add("\n");
    WordToSentenceProcessor<CoreLabel> processor =
        new WordToSentenceProcessor<>(boundariesToDiscard);
    CoreLabel token1 = new CoreLabel();
    token1.setWord("\n");
    token1.set(CoreAnnotations.TextAnnotation.class, "\n");
    token1.set(CoreAnnotations.OriginalTextAnnotation.class, "\n");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("Text");
    token2.set(CoreAnnotations.TextAnnotation.class, "Text");
    token2.set(CoreAnnotations.OriginalTextAnnotation.class, "Text");
    List<CoreLabel> tokens = Arrays.asList(token1, token1, token2);
    List<List<CoreLabel>> result = processor.process(tokens);
    assertEquals(1, result.size());
    assertEquals("Text", result.get(0).get(0).word());
  }

  @Test
  public void testOnlyBoundaryFollowersWithoutBoundaryToken() {
    WordToSentenceProcessor<CoreLabel> processor = new WordToSentenceProcessor<>();
    CoreLabel follower = new CoreLabel();
    follower.setWord(")");
    follower.set(CoreAnnotations.TextAnnotation.class, ")");
    follower.set(CoreAnnotations.OriginalTextAnnotation.class, ")");
    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(follower);
    List<List<CoreLabel>> result = processor.process(tokens);
    assertEquals(1, result.size());
    assertEquals(")", result.get(0).get(0).word());
  }

  @Test
  public void testSingleTokenThatIsNotBoundaryOrFollowerStillFormsSentence() {
    WordToSentenceProcessor<CoreLabel> processor = new WordToSentenceProcessor<>();
    CoreLabel token = new CoreLabel();
    token.setWord("Sentence");
    token.set(CoreAnnotations.TextAnnotation.class, "Sentence");
    token.set(CoreAnnotations.OriginalTextAnnotation.class, "Sentence");
    List<CoreLabel> tokens = Collections.singletonList(token);
    List<List<CoreLabel>> result = processor.process(tokens);
    assertEquals(1, result.size());
    assertEquals("Sentence", result.get(0).get(0).word());
  }

  @Test
  public void testMultipleDiscardedTokensOnlyYieldsNoSentencesWhenNotAllowed() {
    Set<String> discards = new HashSet<>();
    discards.add("<!--");
    WordToSentenceProcessor<CoreLabel> processor =
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
    CoreLabel discard1 = new CoreLabel();
    discard1.setWord("<!--");
    discard1.set(CoreAnnotations.TextAnnotation.class, "<!--");
    discard1.set(CoreAnnotations.OriginalTextAnnotation.class, "<!--");
    CoreLabel discard2 = new CoreLabel();
    discard2.setWord("<!--");
    discard2.set(CoreAnnotations.TextAnnotation.class, "<!--");
    discard2.set(CoreAnnotations.OriginalTextAnnotation.class, "<!--");
    List<CoreLabel> tokens = Arrays.asList(discard1, discard2);
    List<List<CoreLabel>> result = processor.process(tokens);
    assertEquals(0, result.size());
  }

  @Test
  public void testBoundaryWithMultipleFollowersSpreadAcrossSentences() {
    WordToSentenceProcessor<CoreLabel> processor = new WordToSentenceProcessor<>();
    CoreLabel t1 = new CoreLabel();
    t1.setWord("He");
    t1.set(CoreAnnotations.TextAnnotation.class, "He");
    t1.set(CoreAnnotations.OriginalTextAnnotation.class, "He");
    CoreLabel t2 = new CoreLabel();
    t2.setWord("said");
    t2.set(CoreAnnotations.TextAnnotation.class, "said");
    t2.set(CoreAnnotations.OriginalTextAnnotation.class, "said");
    CoreLabel t3 = new CoreLabel();
    t3.setWord(":");
    t3.set(CoreAnnotations.TextAnnotation.class, ":");
    t3.set(CoreAnnotations.OriginalTextAnnotation.class, ":");
    CoreLabel t4 = new CoreLabel();
    t4.setWord("\"");
    t4.set(CoreAnnotations.TextAnnotation.class, "\"");
    t4.set(CoreAnnotations.OriginalTextAnnotation.class, "\"");
    CoreLabel t5 = new CoreLabel();
    t5.setWord("Yes");
    t5.set(CoreAnnotations.TextAnnotation.class, "Yes");
    t5.set(CoreAnnotations.OriginalTextAnnotation.class, "Yes");
    CoreLabel t6 = new CoreLabel();
    t6.setWord(".");
    t6.set(CoreAnnotations.TextAnnotation.class, ".");
    t6.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
    CoreLabel t7 = new CoreLabel();
    t7.setWord("\"");
    t7.set(CoreAnnotations.TextAnnotation.class, "\"");
    t7.set(CoreAnnotations.OriginalTextAnnotation.class, "\"");
    List<CoreLabel> tokens = Arrays.asList(t1, t2, t3, t4, t5, t6, t7);
    List<List<CoreLabel>> sentences = processor.process(tokens);
    assertEquals(1, sentences.size());
    assertEquals("\"", sentences.get(0).get(6).word());
  }

  @Test
  public void testMatchDiscardPatternFromRegexSet() {
    Set<String> discards = new HashSet<>(Collections.singletonList("<discard>"));
    Set<String> discardPatterns = new HashSet<>(Collections.singleton(".*discard.*"));
    WordToSentenceProcessor<CoreLabel> processor =
        new WordToSentenceProcessor<>(
            "\\.",
            "\\)",
            discards,
            null,
            null,
            WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
            null,
            discardPatterns,
            false,
            false);
    CoreLabel t1 = new CoreLabel();
    t1.setWord("<discard>");
    t1.set(CoreAnnotations.TextAnnotation.class, "<discard>");
    t1.set(CoreAnnotations.OriginalTextAnnotation.class, "<discard>");
    CoreLabel t2 = new CoreLabel();
    t2.setWord("Keep");
    t2.set(CoreAnnotations.TextAnnotation.class, "Keep");
    t2.set(CoreAnnotations.OriginalTextAnnotation.class, "Keep");
    CoreLabel t3 = new CoreLabel();
    t3.setWord(".");
    t3.set(CoreAnnotations.TextAnnotation.class, ".");
    t3.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
    List<CoreLabel> tokens = Arrays.asList(t1, t2, t3);
    List<List<CoreLabel>> result = processor.process(tokens);
    assertEquals(1, result.size());
    assertEquals("Keep", result.get(0).get(0).word());
  }

  @Test
  public void testSentenceBoundaryTokenAndFollowerSplitWithNoPreviousSentence() {
    WordToSentenceProcessor<CoreLabel> processor = new WordToSentenceProcessor<>();
    CoreLabel t1 = new CoreLabel();
    t1.setWord(".");
    t1.set(CoreAnnotations.TextAnnotation.class, ".");
    t1.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
    CoreLabel t2 = new CoreLabel();
    t2.setWord(")");
    t2.set(CoreAnnotations.TextAnnotation.class, ")");
    t2.set(CoreAnnotations.OriginalTextAnnotation.class, ")");
    CoreLabel t3 = new CoreLabel();
    t3.setWord("Text");
    t3.set(CoreAnnotations.TextAnnotation.class, "Text");
    t3.set(CoreAnnotations.OriginalTextAnnotation.class, "Text");
    List<CoreLabel> input = Arrays.asList(t1, t2, t3);
    List<List<CoreLabel>> result = processor.process(input);
    assertEquals(2, result.size());
    assertEquals(".", result.get(0).get(0).word());
    assertEquals(")", result.get(1).get(0).word());
  }

  @Test
  public void testOneSentenceConstructorSkipsSplitting() {
    WordToSentenceProcessor<CoreLabel> processor = new WordToSentenceProcessor<>(true);
    CoreLabel a = new CoreLabel();
    a.setWord("This");
    a.set(CoreAnnotations.TextAnnotation.class, "This");
    a.set(CoreAnnotations.OriginalTextAnnotation.class, "This");
    CoreLabel b = new CoreLabel();
    b.setWord(".");
    b.set(CoreAnnotations.TextAnnotation.class, ".");
    b.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
    CoreLabel c = new CoreLabel();
    c.setWord("Next");
    c.set(CoreAnnotations.TextAnnotation.class, "Next");
    c.set(CoreAnnotations.OriginalTextAnnotation.class, "Next");
    CoreLabel d = new CoreLabel();
    d.setWord(".");
    d.set(CoreAnnotations.TextAnnotation.class, ".");
    d.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
    List<CoreLabel> tokens = Arrays.asList(a, b, c, d);
    List<List<CoreLabel>> result = processor.process(tokens);
    assertEquals(1, result.size());
    assertEquals(4, result.get(0).size());
  }

  @Test
  public void testEmptyDiscardSetStillHandlesEndOfInputLogic() {
    Set<String> discard = Collections.emptySet();
    WordToSentenceProcessor<CoreLabel> processor =
        new WordToSentenceProcessor<>(
            "\\.",
            "\\)",
            discard,
            null,
            null,
            WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
            null,
            null,
            false,
            false);
    CoreLabel a = new CoreLabel();
    a.setWord("Final");
    a.set(CoreAnnotations.TextAnnotation.class, "Final");
    a.set(CoreAnnotations.OriginalTextAnnotation.class, "Final");
    List<CoreLabel> tokens = Arrays.asList(a);
    List<List<CoreLabel>> result = processor.process(tokens);
    assertEquals(1, result.size());
    assertEquals("Final", result.get(0).get(0).word());
  }

  @Test
  public void testQuoteUnmatchedStartSuppressesEndQuoteInNewSentence() {
    WordToSentenceProcessor<CoreLabel> processor = new WordToSentenceProcessor<>();
    CoreLabel q1 = new CoreLabel();
    q1.setWord("\"");
    q1.set(CoreAnnotations.TextAnnotation.class, "\"");
    q1.set(CoreAnnotations.OriginalTextAnnotation.class, "\"");
    CoreLabel text = new CoreLabel();
    text.setWord("Hello");
    text.set(CoreAnnotations.TextAnnotation.class, "Hello");
    text.set(CoreAnnotations.OriginalTextAnnotation.class, "Hello");
    CoreLabel dot = new CoreLabel();
    dot.setWord(".");
    dot.set(CoreAnnotations.TextAnnotation.class, ".");
    dot.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
    CoreLabel q2 = new CoreLabel();
    q2.setWord("\"");
    q2.set(CoreAnnotations.TextAnnotation.class, "\"");
    q2.set(CoreAnnotations.OriginalTextAnnotation.class, "\"");
    List<CoreLabel> tokens = Arrays.asList(q1, text, dot, q2);
    List<List<CoreLabel>> result = processor.process(tokens);
    assertEquals(1, result.size());
    assertEquals(4, result.get(0).size());
    assertEquals("\"", result.get(0).get(0).word());
    assertEquals("Hello", result.get(0).get(1).word());
    assertEquals(".", result.get(0).get(2).word());
    assertEquals("\"", result.get(0).get(3).word());
  }

  @Test
  public void testSentenceWithinRegionOnlyIncludedOnce() {
    WordToSentenceProcessor<CoreLabel> processor =
        new WordToSentenceProcessor<>(
            "\\.",
            "\\)",
            Collections.emptySet(),
            Collections.emptySet(),
            "region",
            WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
            null,
            null,
            false,
            false);
    CoreLabel outside = new CoreLabel();
    outside.setWord("Outside");
    outside.set(CoreAnnotations.TextAnnotation.class, "Outside");
    outside.set(CoreAnnotations.OriginalTextAnnotation.class, "Outside");
    CoreLabel regionStart = new CoreLabel();
    regionStart.setWord("<region>");
    regionStart.set(CoreAnnotations.TextAnnotation.class, "<region>");
    regionStart.set(CoreAnnotations.OriginalTextAnnotation.class, "<region>");
    CoreLabel inside = new CoreLabel();
    inside.setWord("Inside");
    inside.set(CoreAnnotations.TextAnnotation.class, "Inside");
    inside.set(CoreAnnotations.OriginalTextAnnotation.class, "Inside");
    CoreLabel dot = new CoreLabel();
    dot.setWord(".");
    dot.set(CoreAnnotations.TextAnnotation.class, ".");
    dot.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
    CoreLabel regionEnd = new CoreLabel();
    regionEnd.setWord("</region>");
    regionEnd.set(CoreAnnotations.TextAnnotation.class, "</region>");
    regionEnd.set(CoreAnnotations.OriginalTextAnnotation.class, "</region>");
    List<CoreLabel> tokens = Arrays.asList(outside, regionStart, inside, dot, regionEnd);
    List<List<CoreLabel>> result = processor.process(tokens);
    assertEquals(1, result.size());
    assertEquals(2, result.get(0).size());
    assertEquals("Inside", result.get(0).get(0).word());
    assertEquals(".", result.get(0).get(1).word());
  }

  @Test
  public void testXmlBreakElementWithLeadingSpaceAndSlashIsDiscarded() {
    Set<String> xmlBreaks = new HashSet<>();
    xmlBreaks.add("div");
    WordToSentenceProcessor<CoreLabel> processor =
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
    CoreLabel token1 = new CoreLabel();
    token1.setWord("First");
    token1.set(CoreAnnotations.TextAnnotation.class, "First");
    token1.set(CoreAnnotations.OriginalTextAnnotation.class, "First");
    CoreLabel xml = new CoreLabel();
    xml.setWord("</div>");
    xml.set(CoreAnnotations.TextAnnotation.class, "</div>");
    xml.set(CoreAnnotations.OriginalTextAnnotation.class, "</div>");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("Second");
    token2.set(CoreAnnotations.TextAnnotation.class, "Second");
    token2.set(CoreAnnotations.OriginalTextAnnotation.class, "Second");
    CoreLabel dot = new CoreLabel();
    dot.setWord(".");
    dot.set(CoreAnnotations.TextAnnotation.class, ".");
    dot.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
    List<CoreLabel> tokens = Arrays.asList(token1, xml, token2, dot);
    List<List<CoreLabel>> result = processor.process(tokens);
    assertEquals(2, result.size());
    assertEquals("First", result.get(0).get(0).word());
    assertEquals("Second", result.get(1).get(0).word());
  }

  @Test
  public void testMultiTokenBoundaryMatchBreaksSentence() {
    // SequencePattern<CoreLabel> pattern = new SequencePattern<CoreLabel>() {
    //
    // @Override
    // public SequenceMatcher<CoreLabel> getMatcher(List<? extends CoreLabel> tokens) {
    // return new SequenceMatcher<CoreLabel>() {
    //
    // boolean seen = false;
    //
    // @Override
    // public boolean find() {
    // boolean wasSeen = seen;
    // seen = true;
    // return !wasSeen;
    // }
    //
    // @Override
    // public List<CoreLabel> groupNodes() {
    // return tokens.subList(tokens.size() - 2, tokens.size());
    // }
    // };
    // }
    // };
    // WordToSentenceProcessor<CoreLabel> processor = new WordToSentenceProcessor<>("\\.", "\\)",
    // Collections.emptySet(), null, null, WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
    // pattern, null, false, false);
    CoreLabel a = new CoreLabel();
    a.setWord("multi");
    a.set(CoreAnnotations.TextAnnotation.class, "multi");
    a.set(CoreAnnotations.OriginalTextAnnotation.class, "multi");
    CoreLabel b = new CoreLabel();
    b.setWord("token");
    b.set(CoreAnnotations.TextAnnotation.class, "token");
    b.set(CoreAnnotations.OriginalTextAnnotation.class, "token");
    CoreLabel c = new CoreLabel();
    c.setWord("next");
    c.set(CoreAnnotations.TextAnnotation.class, "next");
    c.set(CoreAnnotations.OriginalTextAnnotation.class, "next");
    List<CoreLabel> tokens = Arrays.asList(a, b, c);
    // List<List<CoreLabel>> result = processor.process(tokens);
    // assertEquals(2, result.size());
    // assertEquals("multi", result.get(0).get(0).word());
    // assertEquals("token", result.get(0).get(1).word());
    // assertEquals("next", result.get(1).get(0).word());
  }

  @Test
  public void testNewlineTokenResetsLastTokenWasNewline() {
    WordToSentenceProcessor<CoreLabel> processor =
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
    CoreLabel t1 = new CoreLabel();
    t1.setWord("hello");
    t1.set(CoreAnnotations.TextAnnotation.class, "hello");
    t1.set(CoreAnnotations.OriginalTextAnnotation.class, "hello");
    CoreLabel t2 = new CoreLabel();
    t2.setWord(".");
    t2.set(CoreAnnotations.TextAnnotation.class, ".");
    t2.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
    CoreLabel newline = new CoreLabel();
    newline.setWord("\n");
    newline.set(CoreAnnotations.TextAnnotation.class, "\n");
    newline.set(CoreAnnotations.OriginalTextAnnotation.class, "\n");
    CoreLabel t3 = new CoreLabel();
    t3.setWord("\n");
    t3.set(CoreAnnotations.TextAnnotation.class, "\n");
    t3.set(CoreAnnotations.OriginalTextAnnotation.class, "\n");
    CoreLabel t4 = new CoreLabel();
    t4.setWord("next");
    t4.set(CoreAnnotations.TextAnnotation.class, "next");
    t4.set(CoreAnnotations.OriginalTextAnnotation.class, "next");
    CoreLabel t5 = new CoreLabel();
    t5.setWord(".");
    t5.set(CoreAnnotations.TextAnnotation.class, ".");
    t5.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
    List<CoreLabel> tokens = Arrays.asList(t1, t2, newline, t3, t4, t5);
    List<List<CoreLabel>> result = processor.process(tokens);
    assertEquals(2, result.size());
    assertEquals("hello", result.get(0).get(0).word());
    assertEquals("next", result.get(1).get(0).word());
  }

  @Test
  public void testBoundaryTokenInDiscardSetDoesNotTriggerSplitUnlessForced() {
    Set<String> discards = new HashSet<>();
    discards.add(".");
    WordToSentenceProcessor<CoreLabel> processor =
        new WordToSentenceProcessor<>(
            "\\.",
            "\\)",
            discards,
            null,
            null,
            WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
            null,
            null,
            false,
            false);
    CoreLabel a = new CoreLabel();
    a.setWord("X");
    a.set(CoreAnnotations.TextAnnotation.class, "X");
    a.set(CoreAnnotations.OriginalTextAnnotation.class, "X");
    CoreLabel dot = new CoreLabel();
    dot.setWord(".");
    dot.set(CoreAnnotations.TextAnnotation.class, ".");
    dot.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
    CoreLabel b = new CoreLabel();
    b.setWord("Y");
    b.set(CoreAnnotations.TextAnnotation.class, "Y");
    b.set(CoreAnnotations.OriginalTextAnnotation.class, "Y");
    List<CoreLabel> tokens = Arrays.asList(a, dot, b);
    List<List<CoreLabel>> result = processor.process(tokens);
    assertEquals(1, result.size());
    assertEquals("X", result.get(0).get(0).word());
    assertEquals("Y", result.get(0).get(1).word());
  }

  @Test
  public void testMultipleForcedEndInMiddleAvoidsPrematureSplit() {
    WordToSentenceProcessor<CoreLabel> processor = new WordToSentenceProcessor<>();
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Keep");
    token1.set(CoreAnnotations.TextAnnotation.class, "Keep");
    token1.set(CoreAnnotations.OriginalTextAnnotation.class, "Keep");
    token1.set(CoreAnnotations.ForcedSentenceUntilEndAnnotation.class, true);
    CoreLabel token2 = new CoreLabel();
    token2.setWord("together");
    token2.set(CoreAnnotations.TextAnnotation.class, "together");
    token2.set(CoreAnnotations.OriginalTextAnnotation.class, "together");
    CoreLabel token3 = new CoreLabel();
    token3.setWord(".");
    token3.set(CoreAnnotations.TextAnnotation.class, ".");
    token3.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
    CoreLabel token4 = new CoreLabel();
    token4.setWord("Split");
    token4.set(CoreAnnotations.TextAnnotation.class, "Split");
    token4.set(CoreAnnotations.OriginalTextAnnotation.class, "Split");
    CoreLabel forcedEnd = new CoreLabel();
    forcedEnd.setWord(".");
    forcedEnd.set(CoreAnnotations.TextAnnotation.class, ".");
    forcedEnd.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
    forcedEnd.set(CoreAnnotations.ForcedSentenceEndAnnotation.class, true);
    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4, forcedEnd);
    List<List<CoreLabel>> sentences = processor.process(tokens);
    assertEquals(2, sentences.size());
    assertEquals("Keep", sentences.get(0).get(0).word());
    assertEquals("Split", sentences.get(1).get(0).word());
  }

  @Test
  public void testRegionStartWithoutEndProcessesToEOF() {
    WordToSentenceProcessor<CoreLabel> processor =
        new WordToSentenceProcessor<>(
            "\\.",
            "\\)",
            Collections.emptySet(),
            null,
            "r",
            WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
            null,
            null,
            false,
            false);
    CoreLabel open = new CoreLabel();
    open.setWord("<r>");
    open.set(CoreAnnotations.TextAnnotation.class, "<r>");
    open.set(CoreAnnotations.OriginalTextAnnotation.class, "<r>");
    CoreLabel token = new CoreLabel();
    token.setWord("data");
    token.set(CoreAnnotations.TextAnnotation.class, "data");
    token.set(CoreAnnotations.OriginalTextAnnotation.class, "data");
    CoreLabel dot = new CoreLabel();
    dot.setWord(".");
    dot.set(CoreAnnotations.TextAnnotation.class, ".");
    dot.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
    List<CoreLabel> tokens = Arrays.asList(open, token, dot);
    List<List<CoreLabel>> result = processor.process(tokens);
    assertEquals(1, result.size());
    assertEquals("data", result.get(0).get(0).word());
    assertEquals(".", result.get(0).get(1).word());
  }

  @Test
  public void testUnexpectedAnnotationObjectAtEndReturnsFalseNoCrash() {
    WordToSentenceProcessor<Object> processor = new WordToSentenceProcessor<>();
    Object unknown = new Object();
    List<Object> input = Collections.singletonList(unknown);
    try {
      processor.process(input);
      fail("Expected RuntimeException for unknown token type.");
    } catch (RuntimeException e) {
      assertTrue(e.getMessage().toLowerCase().contains("expected token"));
    }
  }

  @Test
  public void testMultiTokenPatternMatchWithSingleTokenDoesNotCrash() {
    // SequencePattern<CoreLabel> fakePattern = new SequencePattern<CoreLabel>() {
    //
    // public SequenceMatcher<CoreLabel> getMatcher(List<? extends CoreLabel> list) {
    // return new SequenceMatcher<CoreLabel>() {
    //
    // boolean found = false;
    //
    // public boolean find() {
    // if (list.size() < 1)
    // return false;
    // boolean result = !found;
    // found = true;
    // return result;
    // }
    //
    // public List<CoreLabel> groupNodes() {
    // return list.subList(0, 1);
    // }
    // };
    // }
    // };
    // WordToSentenceProcessor<CoreLabel> processor = new WordToSentenceProcessor<>("\\.", "\\)",
    // Collections.emptySet(), null, null, WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
    // fakePattern, null, false, false);
    CoreLabel token = new CoreLabel();
    token.setWord("only");
    token.set(CoreAnnotations.TextAnnotation.class, "only");
    token.set(CoreAnnotations.OriginalTextAnnotation.class, "only");
    List<CoreLabel> tokens = Collections.singletonList(token);
    // List<List<CoreLabel>> output = processor.process(tokens);
    // assertEquals(1, output.size());
    // assertEquals("only", output.get(0).get(0).word());
  }

  @Test
  public void testLeadingDiscardTokenDoesNotEmitEmptySentence() {
    Set<String> discardSet = new HashSet<>();
    discardSet.add("<discard>");
    WordToSentenceProcessor<CoreLabel> processor =
        new WordToSentenceProcessor<>(
            "\\.",
            "\\)",
            discardSet,
            null,
            null,
            WordToSentenceProcessor.NewlineIsSentenceBreak.ALWAYS,
            null,
            null,
            false,
            false);
    CoreLabel discarded = new CoreLabel();
    discarded.setWord("<discard>");
    discarded.set(CoreAnnotations.TextAnnotation.class, "<discard>");
    discarded.set(CoreAnnotations.OriginalTextAnnotation.class, "<discard>");
    CoreLabel next = new CoreLabel();
    next.setWord("start");
    next.set(CoreAnnotations.TextAnnotation.class, "start");
    next.set(CoreAnnotations.OriginalTextAnnotation.class, "start");
    CoreLabel dot = new CoreLabel();
    dot.setWord(".");
    dot.set(CoreAnnotations.TextAnnotation.class, ".");
    dot.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
    List<CoreLabel> tokens = Arrays.asList(discarded, next, dot);
    List<List<CoreLabel>> result = processor.process(tokens);
    assertEquals(1, result.size());
    assertEquals("start", result.get(0).get(0).word());
  }

  @Test
  public void testConsecutiveSentenceBoundariesYieldsShortSentences() {
    WordToSentenceProcessor<CoreLabel> processor = new WordToSentenceProcessor<>();
    CoreLabel t1 = new CoreLabel();
    t1.setWord("A");
    t1.set(CoreAnnotations.TextAnnotation.class, "A");
    t1.set(CoreAnnotations.OriginalTextAnnotation.class, "A");
    CoreLabel dot1 = new CoreLabel();
    dot1.setWord(".");
    dot1.set(CoreAnnotations.TextAnnotation.class, ".");
    dot1.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
    CoreLabel dot2 = new CoreLabel();
    dot2.setWord(".");
    dot2.set(CoreAnnotations.TextAnnotation.class, ".");
    dot2.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
    CoreLabel t2 = new CoreLabel();
    t2.setWord("B");
    t2.set(CoreAnnotations.TextAnnotation.class, "B");
    t2.set(CoreAnnotations.OriginalTextAnnotation.class, "B");
    CoreLabel dot3 = new CoreLabel();
    dot3.setWord(".");
    dot3.set(CoreAnnotations.TextAnnotation.class, ".");
    dot3.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
    List<CoreLabel> input = Arrays.asList(t1, dot1, dot2, t2, dot3);
    List<List<CoreLabel>> sentences = processor.process(input);
    assertEquals(3, sentences.size());
    assertEquals(".", sentences.get(1).get(0).word());
    assertEquals("B", sentences.get(2).get(0).word());
  }

  @Test
  public void testEmptyDocumentStillReturnsListWithoutError() {
    WordToSentenceProcessor<CoreLabel> processor = new WordToSentenceProcessor<>();
    List<CoreLabel> empty = Collections.emptyList();
    List<List<CoreLabel>> result = processor.process(empty);
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  public void testNonSplitProcessorReturnsAllTokensTogether() {
    WordToSentenceProcessor<CoreLabel> processor = new WordToSentenceProcessor<>(true);
    CoreLabel a = new CoreLabel();
    a.setWord("End");
    a.set(CoreAnnotations.TextAnnotation.class, "End");
    a.set(CoreAnnotations.OriginalTextAnnotation.class, "End");
    CoreLabel b = new CoreLabel();
    b.setWord(".");
    b.set(CoreAnnotations.TextAnnotation.class, ".");
    b.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
    List<CoreLabel> list = Arrays.asList(a, b);
    List<List<CoreLabel>> output = processor.process(list);
    assertEquals(1, output.size());
    assertEquals("End", output.get(0).get(0).word());
    assertEquals(".", output.get(0).get(1).word());
  }
}
