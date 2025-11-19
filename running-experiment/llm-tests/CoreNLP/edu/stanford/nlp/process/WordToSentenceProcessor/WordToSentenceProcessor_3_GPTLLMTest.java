package edu.stanford.nlp.process;

import static org.junit.Assert.*;

import edu.stanford.nlp.ling.*;
import java.io.*;
import java.util.*;
import org.junit.Test;

public class WordToSentenceProcessor_3_GPTLLMTest {

  @Test
  public void testSingleSentence() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    HasWord w1 = new Word("This");
    HasWord w2 = new Word("is");
    HasWord w3 = new Word("a");
    HasWord w4 = new Word("test");
    HasWord w5 = new Word(".");
    List<HasWord> input = Arrays.asList(w1, w2, w3, w4, w5);
    List<List<HasWord>> sentences = processor.process(input);
    assertEquals(1, sentences.size());
    List<HasWord> expected = Arrays.asList(w1, w2, w3, w4, w5);
    assertEquals(expected, sentences.get(0));
  }

  @Test
  public void testMultipleSentences() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    HasWord w1 = new Word("Hello");
    HasWord w2 = new Word("world");
    HasWord w3 = new Word(".");
    HasWord w4 = new Word("How");
    HasWord w5 = new Word("are");
    HasWord w6 = new Word("you");
    HasWord w7 = new Word("?");
    List<HasWord> input = Arrays.asList(w1, w2, w3, w4, w5, w6, w7);
    List<List<HasWord>> sentences = processor.process(input);
    assertEquals(2, sentences.size());
    assertEquals(Arrays.asList(w1, w2, w3), sentences.get(0));
    assertEquals(Arrays.asList(w4, w5, w6, w7), sentences.get(1));
  }

  @Test
  public void testNewlineAlwaysBreak() {
    Set<String> newlineSet = new HashSet<>();
    newlineSet.add("\n");
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>(newlineSet);
    HasWord w1 = new Word("First");
    HasWord w2 = new Word(".");
    HasWord nl1 = new Word("\n");
    HasWord w3 = new Word("Second");
    HasWord w4 = new Word("!");
    List<HasWord> input = Arrays.asList(w1, w2, nl1, w3, w4);
    List<List<HasWord>> sentences = processor.process(input);
    assertEquals(2, sentences.size());
    assertEquals(Arrays.asList(w1, w2), sentences.get(0));
    assertEquals(Arrays.asList(w3, w4), sentences.get(1));
  }

  @Test
  public void testForcedSentenceEndAnnotation() {
    WordToSentenceProcessor<CoreLabel> processor = new WordToSentenceProcessor<>();
    CoreLabel w1 = new CoreLabel();
    w1.setWord("Start");
    CoreLabel w2 = new CoreLabel();
    w2.setWord("End");
    w2.set(CoreAnnotations.ForcedSentenceEndAnnotation.class, true);
    CoreLabel w3 = new CoreLabel();
    w3.setWord("Next");
    List<CoreLabel> input = Arrays.asList(w1, w2, w3);
    List<List<CoreLabel>> sentences = processor.process(input);
    assertEquals(2, sentences.size());
    assertEquals(Arrays.asList(w1, w2), sentences.get(0));
    assertEquals(Collections.singletonList(w3), sentences.get(1));
  }

  @Test
  public void testIsOneSentenceMode() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>(true);
    HasWord w1 = new Word("Hello");
    HasWord w2 = new Word(".");
    HasWord w3 = new Word("How");
    HasWord w4 = new Word("are");
    HasWord w5 = new Word("you");
    HasWord w6 = new Word("?");
    List<HasWord> input = Arrays.asList(w1, w2, w3, w4, w5, w6);
    List<List<HasWord>> sentences = processor.process(input);
    assertEquals(1, sentences.size());
    assertEquals(input, sentences.get(0));
  }

  @Test
  public void testMultiTokenPatternSplit() {
    CoreLabel t1 = new CoreLabel();
    t1.setWord("Number");
    CoreLabel t2 = new CoreLabel();
    t2.setWord(":");
    CoreLabel t3 = new CoreLabel();
    t3.setWord("123");
    CoreLabel t4 = new CoreLabel();
    t4.setWord("Next");
    CoreLabel t5 = new CoreLabel();
    t5.setWord("Line");
    List<CoreLabel> input = Arrays.asList(t1, t2, t3, t4, t5);
    // SequencePattern<CoreLabel> pattern = SequencePattern.compile("[{word: \":\"}, {word:
    // \"123\"}]", false, null);
    // WordToSentenceProcessor<CoreLabel> processor = new
    // WordToSentenceProcessor<>(WordToSentenceProcessor.DEFAULT_BOUNDARY_REGEX,
    // WordToSentenceProcessor.DEFAULT_BOUNDARY_FOLLOWERS_REGEX,
    // WordToSentenceProcessor.DEFAULT_SENTENCE_BOUNDARIES_TO_DISCARD, null, null,
    // WordToSentenceProcessor.WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER, pattern, null,
    // false, false);
    // List<List<CoreLabel>> sentences = processor.process(input);
    // assertEquals(2, sentences.size());
    // assertEquals(Arrays.asList(t1, t2, t3), sentences.get(0));
    // assertEquals(Arrays.asList(t4, t5), sentences.get(1));
  }

  @Test
  public void testRegionFilter() {
    Set<String> xmlTags = new HashSet<>();
    xmlTags.add("div");
    // WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>("[.?!]", "[)]+",
    // WordToSentenceProcessor.DEFAULT_SENTENCE_BOUNDARIES_TO_DISCARD, xmlTags, "body",
    // WordToSentenceProcessor.WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER, null, null,
    // false, false);
    HasWord w1 = new Word("outside1");
    HasWord open = new Word("<body>");
    HasWord w2 = new Word("inside");
    HasWord w3 = new Word(".");
    HasWord close = new Word("</body>");
    HasWord w4 = new Word("outside2");
    List<HasWord> input = Arrays.asList(w1, open, w2, w3, close, w4);
    // List<List<HasWord>> sentences = processor.process(input);
    // assertEquals(1, sentences.size());
    // assertEquals(Arrays.asList(w2, w3), sentences.get(0));
  }

  @Test
  public void testEmptyInputReturnsEmptySentenceList() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    List<HasWord> input = Collections.emptyList();
    List<List<HasWord>> result = processor.process(input);
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidNewlineModeThrowsException() {
    WordToSentenceProcessor.stringToNewlineIsSentenceBreak("invalid_mode");
  }

  @Test
  public void testTokenRegexToDiscardOnly() {
    Set<String> discard = new HashSet<>();
    discard.add("<discard>");
    // WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>("[.?!]", "[)]+",
    // WordToSentenceProcessor.DEFAULT_SENTENCE_BOUNDARIES_TO_DISCARD, null, null,
    // WordToSentenceProcessor.WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER, null, discard,
    // false, false);
    HasWord w1 = new Word("This");
    HasWord discardWord = new Word("<discard>");
    HasWord w2 = new Word("sentence");
    HasWord w3 = new Word(".");
    List<HasWord> input = Arrays.asList(w1, discardWord, w2, w3);
    // List<List<HasWord>> result = processor.process(input);
    // assertEquals(1, result.size());
    // assertEquals(Arrays.asList(w1, w2, w3), result.get(0));
  }

  @Test
  public void testConsecutiveBoundaryTokens() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    HasWord w1 = new Word("Hello");
    HasWord w2 = new Word(".");
    HasWord w3 = new Word(".");
    HasWord w4 = new Word("World");
    HasWord w5 = new Word("!");
    List<HasWord> input = Arrays.asList(w1, w2, w3, w4, w5);
    List<List<HasWord>> result = processor.process(input);
    assertEquals(3, result.size());
    assertEquals(Arrays.asList(w1, w2), result.get(0));
    assertEquals(Collections.singletonList(w3), result.get(1));
    assertEquals(Arrays.asList(w4, w5), result.get(2));
  }

  @Test
  public void testSentenceBoundaryFollowerAtStart() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    HasWord w1 = new Word(")");
    HasWord w2 = new Word("Hello");
    HasWord w3 = new Word("!");
    List<HasWord> input = Arrays.asList(w1, w2, w3);
    List<List<HasWord>> result = processor.process(input);
    assertEquals(1, result.size());
    assertEquals(Arrays.asList(w1, w2, w3), result.get(0));
  }

  @Test
  public void testDiscardedBoundaryAtStartAndEnd() {
    Set<String> discard = new HashSet<>();
    discard.add("<p>");
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>(discard);
    HasWord w1 = new Word("<p>");
    HasWord w2 = new Word("Hello");
    HasWord w3 = new Word("!");
    HasWord w4 = new Word("<p>");
    List<HasWord> input = Arrays.asList(w1, w2, w3, w4);
    List<List<HasWord>> result = processor.process(input);
    assertEquals(1, result.size());
    assertEquals(Arrays.asList(w2, w3), result.get(0));
  }

  @Test
  public void testAllowEmptySentencesTrue() {
    Set<String> discard = new HashSet<>();
    discard.add("<break>");
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            "[.?!]",
            "[)]+",
            discard,
            null,
            null,
            WordToSentenceProcessor.NewlineIsSentenceBreak.ALWAYS,
            null,
            null,
            false,
            true);
    HasWord w1 = new Word("<break>");
    HasWord w2 = new Word("<break>");
    HasWord w3 = new Word("Content");
    HasWord w4 = new Word(".");
    List<HasWord> input = Arrays.asList(w1, w2, w3, w4);
    List<List<HasWord>> result = processor.process(input);
    assertEquals(2, result.size());
    assertTrue(result.get(0).isEmpty());
    assertEquals(Arrays.asList(w3, w4), result.get(1));
  }

  @Test
  public void testCoreLabelWithNullAnnotations() {
    WordToSentenceProcessor<CoreLabel> processor = new WordToSentenceProcessor<>();
    CoreLabel w1 = new CoreLabel();
    w1.setWord("Hello");
    CoreLabel w2 = new CoreLabel();
    CoreLabel w3 = new CoreLabel();
    w3.setWord("!");
    List<CoreLabel> input = Arrays.asList(w1, w2, w3);
    try {
      processor.process(input);
      fail("Expected RuntimeException for missing word() string");
    } catch (RuntimeException e) {
      assertTrue(e.getMessage().contains("Expected token to be either Word or String"));
    }
  }

  @Test
  public void testRegionEndBeforeBegin() {
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            "[.?!]",
            "[)]+",
            WordToSentenceProcessor.DEFAULT_SENTENCE_BOUNDARIES_TO_DISCARD,
            null,
            "wrongRegion",
            WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
            null,
            null,
            false,
            false);
    HasWord w1 = new Word("</wrongRegion>");
    HasWord w2 = new Word("Hello");
    HasWord w3 = new Word(".");
    List<HasWord> input = Arrays.asList(w1, w2, w3);
    List<List<HasWord>> result = processor.process(input);
    assertEquals(0, result.size());
  }

  @Test
  public void testForcedSentenceUntilEndAnnotation() {
    WordToSentenceProcessor<CoreLabel> processor = new WordToSentenceProcessor<>();
    CoreLabel w1 = new CoreLabel();
    w1.setWord("Begin");
    w1.set(CoreAnnotations.ForcedSentenceUntilEndAnnotation.class, true);
    CoreLabel w2 = new CoreLabel();
    w2.setWord("Keep1");
    CoreLabel w3 = new CoreLabel();
    w3.setWord("ForceEnd");
    w3.set(CoreAnnotations.ForcedSentenceEndAnnotation.class, true);
    CoreLabel w4 = new CoreLabel();
    w4.setWord("Second");
    CoreLabel w5 = new CoreLabel();
    w5.setWord(".");
    List<CoreLabel> input = Arrays.asList(w1, w2, w3, w4, w5);
    List<List<CoreLabel>> result = processor.process(input);
    assertEquals(2, result.size());
    assertEquals(Arrays.asList(w1, w2, w3), result.get(0));
    assertEquals(Arrays.asList(w4, w5), result.get(1));
  }

  @Test
  public void testNewlineWithNoBoundaryToken() {
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(WordToSentenceProcessor.NewlineIsSentenceBreak.ALWAYS);
    HasWord w1 = new Word("No");
    HasWord w2 = new Word("punct");
    HasWord w3 = new Word("\n");
    HasWord w4 = new Word("Still");
    HasWord w5 = new Word("split");
    List<HasWord> input = Arrays.asList(w1, w2, w3, w4, w5);
    List<List<HasWord>> result = processor.process(input);
    assertEquals(2, result.size());
    assertEquals(Arrays.asList(w1, w2), result.get(0));
    assertEquals(Arrays.asList(w4, w5), result.get(1));
  }

  @Test
  public void testOnlyXmlBreakElements() {
    Set<String> xmlTags = new HashSet<>();
    xmlTags.add("br");
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            "[.?!]",
            "[)]+",
            WordToSentenceProcessor.DEFAULT_SENTENCE_BOUNDARIES_TO_DISCARD,
            xmlTags,
            null,
            WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
            null,
            null,
            false,
            false);
    HasWord w1 = new Word("<br>");
    HasWord w2 = new Word("<br>");
    HasWord w3 = new Word("<br>");
    List<HasWord> input = Arrays.asList(w1, w2, w3);
    List<List<HasWord>> result = processor.process(input);
    assertTrue(result.isEmpty());
  }

  @Test
  public void testSingleTokenSentenceEndingWithPunctuation() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    HasWord w1 = new Word("!");
    List<HasWord> input = Collections.singletonList(w1);
    List<List<HasWord>> result = processor.process(input);
    assertEquals(1, result.size());
    assertEquals(Collections.singletonList(w1), result.get(0));
  }

  @Test
  public void testStartsWithBoundaryFollowerToken() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    HasWord w1 = new Word(")");
    HasWord w2 = new Word("Hello");
    HasWord w3 = new Word(".");
    List<HasWord> input = Arrays.asList(w1, w2, w3);
    List<List<HasWord>> result = processor.process(input);
    assertEquals(1, result.size());
    assertEquals(Arrays.asList(w1, w2, w3), result.get(0));
  }

  @Test
  public void testOnlySentenceBoundaryFollowers() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    HasWord w1 = new Word(")");
    HasWord w2 = new Word(">");
    HasWord w3 = new Word("''");
    List<HasWord> input = Arrays.asList(w1, w2, w3);
    List<List<HasWord>> result = processor.process(input);
    assertEquals(1, result.size());
    assertEquals(input, result.get(0));
  }

  @Test
  public void testDiscardedTokensMatchingBoundaryRegex() {
    Set<String> discard = new HashSet<>();
    discard.add("!");
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            "[.?!]",
            ")",
            discard,
            null,
            null,
            WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
            null,
            null,
            false,
            false);
    HasWord w1 = new Word("Test");
    HasWord w2 = new Word("!");
    List<HasWord> input = Arrays.asList(w1, w2);
    List<List<HasWord>> result = processor.process(input);
    assertEquals(1, result.size());
    assertEquals(Collections.singletonList(w1), result.get(0));
  }

  @Test
  public void testMalformedRegionCloseFirst() {
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            "[.?!]",
            "[)]+",
            WordToSentenceProcessor.DEFAULT_SENTENCE_BOUNDARIES_TO_DISCARD,
            null,
            "content",
            WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
            null,
            null,
            false,
            false);
    HasWord w1 = new Word("</content>");
    HasWord w2 = new Word("data");
    HasWord w3 = new Word(".");
    List<HasWord> input = Arrays.asList(w1, w2, w3);
    List<List<HasWord>> result = processor.process(input);
    assertEquals(0, result.size());
  }

  @Test
  public void testRegionWithAttributesIncluded() {
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            "[.?!]",
            "[)]+",
            WordToSentenceProcessor.DEFAULT_SENTENCE_BOUNDARIES_TO_DISCARD,
            null,
            "note",
            WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
            null,
            null,
            false,
            false);
    HasWord w1 = new Word("<note id='1'>");
    HasWord w2 = new Word("hello");
    HasWord w3 = new Word(".");
    HasWord w4 = new Word("</note>");
    List<HasWord> input = Arrays.asList(w1, w2, w3, w4);
    List<List<HasWord>> result = processor.process(input);
    assertEquals(1, result.size());
    assertEquals(Arrays.asList(w2, w3), result.get(0));
  }

  @Test
  public void testTokenWithEmptyString() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    HasWord w1 = new Word("");
    HasWord w2 = new Word("Token");
    HasWord w3 = new Word("?");
    List<HasWord> input = Arrays.asList(w1, w2, w3);
    List<List<HasWord>> result = processor.process(input);
    assertEquals(1, result.size());
    assertEquals(Arrays.asList(w1, w2, w3), result.get(0));
  }

  @Test
  public void testThreeConsecutiveNewlinesForceBreak() {
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            WordToSentenceProcessor.NewlineIsSentenceBreak.TWO_CONSECUTIVE);
    HasWord w1 = new Word("First");
    HasWord nl1 = new Word("\n");
    HasWord nl2 = new Word("\n");
    HasWord nl3 = new Word("\n");
    HasWord w2 = new Word("Second");
    HasWord w3 = new Word("!");
    List<HasWord> input = Arrays.asList(w1, nl1, nl2, nl3, w2, w3);
    List<List<HasWord>> result = processor.process(input);
    assertEquals(2, result.size());
    assertEquals(Collections.singletonList(w1), result.get(0));
    assertEquals(Arrays.asList(w2, w3), result.get(1));
  }

  @Test
  public void testForcedSentenceEndTokenAlsoMatchedByBoundaryPattern() {
    WordToSentenceProcessor<CoreLabel> processor = new WordToSentenceProcessor<>();
    CoreLabel w1 = new CoreLabel();
    w1.setWord("Hello");
    CoreLabel w2 = new CoreLabel();
    w2.setWord("!");
    w2.set(CoreAnnotations.ForcedSentenceEndAnnotation.class, true);
    CoreLabel w3 = new CoreLabel();
    w3.setWord("Next");
    List<CoreLabel> input = Arrays.asList(w1, w2, w3);
    List<List<CoreLabel>> result = processor.process(input);
    assertEquals(2, result.size());
    assertEquals(Arrays.asList(w1, w2), result.get(0));
    assertEquals(Collections.singletonList(w3), result.get(1));
  }

  @Test
  public void testSentenceWithNoBoundaryAtEnd() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    HasWord w1 = new Word("Hello");
    HasWord w2 = new Word("world");
    List<HasWord> input = Arrays.asList(w1, w2);
    List<List<HasWord>> result = processor.process(input);
    assertEquals(1, result.size());
    assertEquals(Arrays.asList(w1, w2), result.get(0));
  }

  @Test
  public void testMultipleNestedRegionsWithSameTag() {
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            "[.?!]",
            "[)]+",
            WordToSentenceProcessor.DEFAULT_SENTENCE_BOUNDARIES_TO_DISCARD,
            null,
            "quote",
            WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
            null,
            null,
            false,
            false);
    HasWord w1 = new Word("<quote>");
    HasWord w2 = new Word("Start");
    HasWord w3 = new Word(".");
    HasWord w4 = new Word("</quote>");
    HasWord w5 = new Word("<quote>");
    HasWord w6 = new Word("End");
    HasWord w7 = new Word("?");
    HasWord w8 = new Word("</quote>");
    List<HasWord> input = Arrays.asList(w1, w2, w3, w4, w5, w6, w7, w8);
    List<List<HasWord>> result = processor.process(input);
    assertEquals(2, result.size());
    assertEquals(Arrays.asList(w2, w3), result.get(0));
    assertEquals(Arrays.asList(w6, w7), result.get(1));
  }

  @Test
  public void testSentenceBoundaryTokenWithoutSentenceFollower() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    // HasWord w1 = new SimpleWord("Test");
    // HasWord w2 = new SimpleWord(".");
    // HasWord w3 = new SimpleWord("nonFollower");
    // List<HasWord> input = Arrays.asList(w1, w2, w3);
    // List<List<HasWord>> result = processor.process(input);
    // assertEquals(2, result.size());
    // assertEquals(Arrays.asList(w1, w2), result.get(0));
    // assertEquals(Collections.singletonList(w3), result.get(1));
  }

  @Test
  public void testBoundaryTokenImmediatelyFollowedByAnotherSentenceStarter() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    // HasWord w1 = new SimpleWord("First");
    // HasWord w2 = new SimpleWord(".");
    // HasWord w3 = new SimpleWord("Next");
    // List<HasWord> input = Arrays.asList(w1, w2, w3);
    // List<List<HasWord>> result = processor.process(input);
    // assertEquals(2, result.size());
    // assertEquals(Arrays.asList(w1, w2), result.get(0));
    // assertEquals(Collections.singletonList(w3), result.get(1));
  }

  @Test
  public void testForcedSentenceWithDiscardedBoundaryToken() {
    Set<String> discard = new HashSet<>();
    discard.add("<break>");
    WordToSentenceProcessor<CoreLabel> processor =
        new WordToSentenceProcessor<>(
            "[.?!]",
            "[)]+",
            discard,
            null,
            null,
            WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
            null,
            null,
            false,
            false);
    CoreLabel word1 = new CoreLabel();
    word1.setWord("Hello");
    CoreLabel breakToken = new CoreLabel();
    breakToken.setWord("<break>");
    breakToken.set(CoreAnnotations.ForcedSentenceEndAnnotation.class, true);
    CoreLabel word2 = new CoreLabel();
    word2.setWord("World");
    List<CoreLabel> input = Arrays.asList(word1, breakToken, word2);
    List<List<CoreLabel>> result = processor.process(input);
    assertEquals(2, result.size());
    assertEquals(Collections.singletonList(word1), result.get(0));
    assertEquals(Collections.singletonList(word2), result.get(1));
  }

  @Test
  public void testFollowerTokenWithNoPriorSentence() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    // HasWord w1 = new SimpleWord(")");
    // HasWord w2 = new SimpleWord("Content");
    // HasWord w3 = new SimpleWord(".");
    // List<HasWord> input = Arrays.asList(w1, w2, w3);
    // List<List<HasWord>> result = processor.process(input);
    // assertEquals(1, result.size());
    // assertEquals(Arrays.asList(w1, w2, w3), result.get(0));
  }

  @Test
  public void testDiscardOnlyTokensInInput() {
    Set<String> discard = new HashSet<>();
    discard.add("<discard>");
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>(discard);
    // HasWord d1 = new SimpleWord("<discard>");
    // HasWord d2 = new SimpleWord("<discard>");
    // List<HasWord> input = Arrays.asList(d1, d2);
    // List<List<HasWord>> result = processor.process(input);
    // assertEquals(0, result.size());
  }

  @Test
  public void testEmptyRegionBetweenOpenAndClose() {
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            "[.?!]",
            "[)]+",
            WordToSentenceProcessor.DEFAULT_SENTENCE_BOUNDARIES_TO_DISCARD,
            null,
            "region",
            WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
            null,
            null,
            false,
            false);
    // HasWord startTag = new SimpleWord("<region>");
    // HasWord endTag = new SimpleWord("</region>");
    // List<HasWord> input = Arrays.asList(startTag, endTag);
    // List<List<HasWord>> result = processor.process(input);
    // assertEquals(0, result.size());
  }

  @Test
  public void testQuotationMarkPlausibleToAddLogicTrueCase() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    // HasWord w1 = new SimpleWord("'");
    // HasWord w2 = new SimpleWord("Hello");
    // HasWord w3 = new SimpleWord(".");
    // HasWord w4 = new SimpleWord("'");
    // List<HasWord> input = Arrays.asList(w1, w2, w3, w4);
    // List<List<HasWord>> result = processor.process(input);
    // assertEquals(1, result.size());
    // assertEquals(Arrays.asList(w1, w2, w3, w4), result.get(0));
  }

  @Test
  public void testRegionFilteringSkipsTokensOutsideRegion() {
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            "[.?!]",
            "[)]+",
            WordToSentenceProcessor.DEFAULT_SENTENCE_BOUNDARIES_TO_DISCARD,
            null,
            "section",
            WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
            null,
            null,
            false,
            false);
    // HasWord w1 = new SimpleWord("Outside");
    // HasWord tagStart = new SimpleWord("<section>");
    // HasWord w2 = new SimpleWord("Inside");
    // HasWord w3 = new SimpleWord(".");
    // HasWord tagEnd = new SimpleWord("</section>");
    // HasWord w4 = new SimpleWord("Ignored");
    // List<HasWord> input = Arrays.asList(w1, tagStart, w2, w3, tagEnd, w4);
    // List<List<HasWord>> result = processor.process(input);
    // assertEquals(1, result.size());
    // assertEquals(Arrays.asList(w2, w3), result.get(0));
  }

  @Test
  public void testTokenMattersOnlyWithForcedEndAnnotationAndNoRealBoundary() {
    WordToSentenceProcessor<CoreLabel> processor = new WordToSentenceProcessor<>();
    CoreLabel w1 = new CoreLabel();
    w1.setWord("ForceThis");
    w1.set(CoreAnnotations.ForcedSentenceEndAnnotation.class, true);
    CoreLabel w2 = new CoreLabel();
    w2.setWord("Next");
    List<CoreLabel> input = Arrays.asList(w1, w2);
    List<List<CoreLabel>> result = processor.process(input);
    assertEquals(2, result.size());
    assertEquals(Collections.singletonList(w1), result.get(0));
    assertEquals(Collections.singletonList(w2), result.get(1));
  }

  @Test
  public void testSentenceEndingWithXmlBreakElementToDiscard() {
    Set<String> xmlTags = new HashSet<>();
    xmlTags.add("p");
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            "[.?!]",
            "[)]+",
            WordToSentenceProcessor.DEFAULT_SENTENCE_BOUNDARIES_TO_DISCARD,
            xmlTags,
            null,
            WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
            null,
            null,
            false,
            false);
    // HasWord w1 = new RobustTokenizer.WordToken("Hello");
    // HasWord w2 = new RobustTokenizer.WordToken("there");
    // HasWord w3 = new RobustTokenizer.WordToken(".");
    // HasWord xmlTag = new RobustTokenizer.WordToken("<p>");
    // List<HasWord> input = Arrays.asList(w1, w2, w3, xmlTag);
    // List<List<HasWord>> result = processor.process(input);
    // assertEquals(1, result.size());
    // assertEquals(Arrays.asList(w1, w2, w3), result.get(0));
  }

  @Test
  public void testSentenceEndsWithQuoteAndPlauseToAddIsFalse() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    // HasWord w1 = new RobustTokenizer.WordToken("He");
    // HasWord w2 = new RobustTokenizer.WordToken("said");
    // HasWord w3 = new RobustTokenizer.WordToken(".");
    // HasWord w4 = new RobustTokenizer.WordToken("\"");
    // List<HasWord> input = Arrays.asList(w1, w2, w3, w4);
    // List<List<HasWord>> result = processor.process(input);
    // assertEquals(2, result.size());
    // assertEquals(Arrays.asList(w1, w2, w3), result.get(0));
    // assertEquals(Collections.singletonList(w4), result.get(1));
  }

  @Test
  public void testNewlineInMiddleOfForcedUntilEndBlock() {
    WordToSentenceProcessor<CoreLabel> processor = new WordToSentenceProcessor<>();
    CoreLabel w1 = new CoreLabel();
    w1.setWord("A");
    w1.set(CoreAnnotations.ForcedSentenceUntilEndAnnotation.class, true);
    CoreLabel w2 = new CoreLabel();
    w2.setWord("\n");
    CoreLabel w3 = new CoreLabel();
    w3.setWord("B");
    CoreLabel w4 = new CoreLabel();
    w4.setWord("C");
    w4.set(CoreAnnotations.ForcedSentenceEndAnnotation.class, true);
    List<CoreLabel> input = Arrays.asList(w1, w2, w3, w4);
    List<List<CoreLabel>> result = processor.process(input);
    assertEquals(1, result.size());
    assertEquals(Arrays.asList(w1, w3, w4), result.get(0));
  }

  @Test
  public void testNewlineCountsOnlyOnceInTwoConsecutiveMode() {
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            WordToSentenceProcessor.NewlineIsSentenceBreak.TWO_CONSECUTIVE);
    // HasWord w1 = new RobustTokenizer.WordToken("A");
    // HasWord w2 = new RobustTokenizer.WordToken("\n");
    // HasWord w3 = new RobustTokenizer.WordToken("B");
    // List<HasWord> input = Arrays.asList(w1, w2, w3);
    // List<List<HasWord>> result = processor.process(input);
    // assertEquals(1, result.size());
    // assertEquals(Arrays.asList(w1, w3), result.get(0));
  }

  @Test
  public void testTokenMatchesTokenRegexToDiscardButNotExactMatch() {
    Set<String> discardRegex = new HashSet<>();
    discardRegex.add("<!--.*?-->");
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            "[.?!]",
            "[)]+",
            WordToSentenceProcessor.DEFAULT_SENTENCE_BOUNDARIES_TO_DISCARD,
            null,
            null,
            WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
            null,
            discardRegex,
            false,
            false);
    // HasWord w1 = new RobustTokenizer.WordToken("Hello");
    // HasWord w2 = new RobustTokenizer.WordToken("<!--comment-->");
    // HasWord w3 = new RobustTokenizer.WordToken("World");
    // HasWord w4 = new RobustTokenizer.WordToken(".");
    // List<HasWord> input = Arrays.asList(w1, w2, w3, w4);
    // List<List<HasWord>> result = processor.process(input);
    // assertEquals(1, result.size());
    // assertEquals(Arrays.asList(w1, w3, w4), result.get(0));
  }

  @Test
  public void testDiscardedBoundaryTokenWithEmptySentenceSuppressed() {
    Set<String> discardTokens = new HashSet<>();
    discardTokens.add("<br>");
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            "[.?!]",
            "[)]+",
            discardTokens,
            null,
            null,
            WordToSentenceProcessor.NewlineIsSentenceBreak.ALWAYS,
            null,
            null,
            false,
            false);
    // HasWord w1 = new RobustTokenizer.WordToken("<br>");
    // HasWord w2 = new RobustTokenizer.WordToken("Next");
    // HasWord w3 = new RobustTokenizer.WordToken(".");
    // List<HasWord> input = Arrays.asList(w1, w2, w3);
    // List<List<HasWord>> result = processor.process(input);
    // assertEquals(1, result.size());
    // assertEquals(Arrays.asList(w2, w3), result.get(0));
  }

  @Test
  public void testForcedSentenceEndOnlySentenceIsOutputted() {
    WordToSentenceProcessor<CoreLabel> processor = new WordToSentenceProcessor<>();
    CoreLabel w1 = new CoreLabel();
    w1.setWord("A");
    CoreLabel w2 = new CoreLabel();
    w2.setWord("B");
    w2.set(CoreAnnotations.ForcedSentenceEndAnnotation.class, true);
    List<CoreLabel> input = Arrays.asList(w1, w2);
    List<List<CoreLabel>> result = processor.process(input);
    assertEquals(1, result.size());
    assertEquals(Arrays.asList(w1, w2), result.get(0));
  }

  @Test
  public void testMultiTokenMatchFollowedByBoundaryFollower() {
    CoreLabel w1 = new CoreLabel();
    w1.setWord("end");
    CoreLabel w2 = new CoreLabel();
    w2.setWord("now");
    CoreLabel w3 = new CoreLabel();
    w3.setWord(")");
    CoreLabel w4 = new CoreLabel();
    w4.setWord("next");
    String patternStr = "[{word: \"end\"}, {word: \"now\"}]";
    // SequencePattern<CoreLabel> pattern = SequencePattern.compile(patternStr, false, null);
    // WordToSentenceProcessor<CoreLabel> processor = new WordToSentenceProcessor<>("[.?!]",
    // "[\\p{Pe}])", WordToSentenceProcessor.DEFAULT_SENTENCE_BOUNDARIES_TO_DISCARD, null, null,
    // WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER, pattern, null, false, false);
    // List<CoreLabel> result = new ArrayList<>();
    // result.add(w1);
    // result.add(w2);
    // result.add(w3);
    // result.add(w4);
    // List<List<CoreLabel>> sentences = processor.process(result);
    // assertEquals(2, sentences.size());
    // assertEquals(Arrays.asList(w1, w2, w3), sentences.get(0));
    // assertEquals(Collections.singletonList(w4), sentences.get(1));
  }

  @Test
  public void testNewlineThenBoundarySameToken() {
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(WordToSentenceProcessor.NewlineIsSentenceBreak.ALWAYS);
    // HasWord w1 = new HasWordImpl("Intro");
    // HasWord w2 = new HasWordImpl("\n");
    // HasWord w3 = new HasWordImpl("Middle");
    // HasWord w4 = new HasWordImpl(".");
    // HasWord w5 = new HasWordImpl("Next");
    // List<HasWord> input = Arrays.asList(w1, w2, w3, w4, w5);
    // List<List<HasWord>> result = processor.process(input);
    // assertEquals(2, result.size());
    // assertEquals(Arrays.asList(w1), result.get(0));
    // assertEquals(Arrays.asList(w3, w4, w5), result.get(1));
  }

  @Test
  public void testLeadingDiscardSequenceIgnored() {
    Set<String> discarded = new HashSet<>();
    discarded.add("<br>");
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>(discarded);
    // HasWord d1 = new HasWordImpl("<br>");
    // HasWord d2 = new HasWordImpl("<br>");
    // HasWord w1 = new HasWordImpl("Start");
    // HasWord w2 = new HasWordImpl(".");
    // List<HasWord> input = Arrays.asList(d1, d2, w1, w2);
    // List<List<HasWord>> result = processor.process(input);
    // assertEquals(1, result.size());
    // assertEquals(Arrays.asList(w1, w2), result.get(0));
  }

  @Test
  public void testTrailingDiscardSuppressed() {
    Set<String> discarded = new HashSet<>();
    discarded.add("<p>");
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>(discarded);
    // HasWord w1 = new HasWordImpl("Goodbye");
    // HasWord w2 = new HasWordImpl(".");
    // HasWord w3 = new HasWordImpl("<p>");
    // List<HasWord> input = Arrays.asList(w1, w2, w3);
    // List<List<HasWord>> result = processor.process(input);
    // assertEquals(1, result.size());
    // assertEquals(Arrays.asList(w1, w2), result.get(0));
  }

  @Test
  public void testRegionMarkerAlsoListedAsDiscarded() {
    Set<String> discard = new HashSet<>();
    discard.add("<section>");
    discard.add("</section>");
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            "[.?!]",
            "[)]+",
            discard,
            null,
            "section",
            WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
            null,
            null,
            false,
            false);
    // HasWord start = new HasWordImpl("<section>");
    // HasWord content = new HasWordImpl("A");
    // HasWord end = new HasWordImpl("</section>");
    // HasWord w2 = new HasWordImpl("B");
    // HasWord w3 = new HasWordImpl(".");
    // List<HasWord> input = Arrays.asList(start, content, end, w2, w3);
    // List<List<HasWord>> result = processor.process(input);
    // assertEquals(1, result.size());
    // assertEquals(Arrays.asList(content), result.get(0));
  }

  @Test
  public void testRegionEndMatchedWithoutContent() {
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            "[.?!]",
            "[)]+",
            WordToSentenceProcessor.DEFAULT_SENTENCE_BOUNDARIES_TO_DISCARD,
            null,
            "x",
            WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
            null,
            null,
            false,
            false);
    // HasWord w1 = new HasWordImpl("<x/>");
    // List<HasWord> input = Collections.singletonList(w1);
    // List<List<HasWord>> result = processor.process(input);
    // assertEquals(0, result.size());
  }

  @Test
  public void testCustomPunctBoundaryWithNoStandardMark() {
    Set<String> discard = WordToSentenceProcessor.DEFAULT_SENTENCE_BOUNDARIES_TO_DISCARD;
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            ",",
            "[)]+",
            discard,
            null,
            null,
            WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
            null,
            null,
            false,
            false);
    // HasWord w1 = new HasWordImpl("Test");
    // HasWord w2 = new HasWordImpl(",");
    // HasWord w3 = new HasWordImpl("Again");
    // List<HasWord> input = Arrays.asList(w1, w2, w3);
    // List<List<HasWord>> output = processor.process(input);
    // assertEquals(2, output.size());
    // assertEquals(Arrays.asList(w1, w2), output.get(0));
    // assertEquals(Collections.singletonList(w3), output.get(1));
  }

  @Test
  public void testTokenMatchesNone() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    // HasWord w1 = new HasWordImpl("plain");
    // HasWord w2 = new HasWordImpl("sequence");
    // List<HasWord> input = Arrays.asList(w1, w2);
    // List<List<HasWord>> result = processor.process(input);
    // assertEquals(1, result.size());
    // assertEquals(Arrays.asList(w1, w2), result.get(0));
  }

  @Test
  public void testMultiTokenPatternNegativeNoMatch() {
    String noMatchPattern = "[{word: \"one\"}, {word: \"two\"}]";
    // SequencePattern<CoreLabel> pattern = SequencePattern.compile(noMatchPattern, false, null);
    // WordToSentenceProcessor<CoreLabel> processor = new WordToSentenceProcessor<>("[.?!]", "[)]+",
    // WordToSentenceProcessor.DEFAULT_SENTENCE_BOUNDARIES_TO_DISCARD, null, null,
    // WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER, pattern, null, false, false);
    CoreLabel w1 = new CoreLabel();
    w1.setWord("hello");
    CoreLabel w2 = new CoreLabel();
    w2.setWord("world");
    List<CoreLabel> input = Arrays.asList(w1, w2);
    // List<List<CoreLabel>> result = processor.process(input);
    // assertEquals(1, result.size());
    // assertEquals(Arrays.asList(w1, w2), result.get(0));
  }

  @Test
  public void testNewlineNotInDiscardSetIgnored() {
    Set<String> discard = new HashSet<>();
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            "[.?!]",
            "[)]+",
            discard,
            null,
            null,
            WordToSentenceProcessor.NewlineIsSentenceBreak.ALWAYS,
            null,
            null,
            false,
            false);
    // HasWord w1 = new HasWordImpl("Line1");
    // HasWord w2 = new HasWordImpl("\n");
    // HasWord w3 = new HasWordImpl("Line2");
    // HasWord w4 = new HasWordImpl(".");
    // List<HasWord> input = Arrays.asList(w1, w2, w3, w4);
    // List<List<HasWord>> result = processor.process(input);
    // assertEquals(2, result.size());
    // assertEquals(Collections.singletonList(w1), result.get(0));
    // assertEquals(Arrays.asList(w2, w3, w4), result.get(1));
  }

  @Test
  public void testMultiTokenPatternStartMidSentenceIgnored() {
    String patternStr = "[{word: \"after\"}, {word: \"mid\"}]";
    // SequencePattern<CoreLabel> pattern = SequencePattern.compile(patternStr, false, null);
    // WordToSentenceProcessor<CoreLabel> processor = new WordToSentenceProcessor<>("[.?!]", "[)]+",
    // WordToSentenceProcessor.DEFAULT_SENTENCE_BOUNDARIES_TO_DISCARD, null, null,
    // WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER, pattern, null, false, false);
    CoreLabel w1 = new CoreLabel();
    w1.setWord("start");
    CoreLabel w2 = new CoreLabel();
    w2.setWord("after");
    CoreLabel w3 = new CoreLabel();
    w3.setWord("mid");
    CoreLabel w4 = new CoreLabel();
    w4.setWord("done");
    CoreLabel w5 = new CoreLabel();
    w5.setWord(".");
    List<CoreLabel> input = Arrays.asList(w1, w2, w3, w4, w5);
    // List<List<CoreLabel>> result = processor.process(input);
    // assertEquals(2, result.size());
    // assertEquals(Arrays.asList(w1, w2, w3, w4, w5), result.get(0));
  }

  @Test
  public void testMultiTokenMatchedButLastWordDiscarded() {
    Set<String> discard = new HashSet<>();
    discard.add("FOO");
    String patternStr = "[{word: \"BEGIN\"}, {word: \"FOO\"}]";
    // SequencePattern<CoreLabel> pattern = SequencePattern.compile(patternStr, false, null);
    // WordToSentenceProcessor<CoreLabel> processor = new WordToSentenceProcessor<>("[.?!]", "[)]+",
    // discard, null, null, WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER, pattern, null,
    // false, false);
    CoreLabel w1 = new CoreLabel();
    w1.setWord("BEGIN");
    CoreLabel w2 = new CoreLabel();
    w2.setWord("FOO");
    CoreLabel w3 = new CoreLabel();
    w3.setWord("Next");
    List<CoreLabel> input = Arrays.asList(w1, w2, w3);
    // List<List<CoreLabel>> result = processor.process(input);
    // assertEquals(1, result.size());
    // assertEquals(Arrays.asList(w1, w3), result.get(0));
  }

  @Test
  public void testAllowEmptySentencesTrueWithConsecutiveDiscardTokens() {
    Set<String> boundaryDiscard = new HashSet<>();
    boundaryDiscard.add("<p>");
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            "[.?!]",
            "[)]+",
            boundaryDiscard,
            null,
            null,
            WordToSentenceProcessor.NewlineIsSentenceBreak.ALWAYS,
            null,
            null,
            false,
            true);
    // HasWord d1 = new HasWordImpl("<p>");
    // HasWord d2 = new HasWordImpl("<p>");
    // HasWord content = new HasWordImpl("Text");
    // HasWord punct = new HasWordImpl(".");
    // List<HasWord> input = Arrays.asList(d1, d2, content, punct);
    // List<List<HasWord>> result = processor.process(input);
    // assertEquals(2, result.size());
    // assertTrue(result.get(0).isEmpty());
    // assertEquals(Arrays.asList(content, punct), result.get(1));
  }

  @Test
  public void testPlausibleToAddFalseWithEvenQuotes() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    // HasWord q1 = new HasWordImpl("\"");
    // HasWord q2 = new HasWordImpl("run");
    // HasWord q3 = new HasWordImpl(".");
    // HasWord q4 = new HasWordImpl("\"");
    // List<HasWord> input = Arrays.asList(q1, q2, q3, q4);
    // List<List<HasWord>> result = processor.process(input);
    // assertEquals(2, result.size());
    // assertEquals(Arrays.asList(q1, q2, q3), result.get(0));
    // assertEquals(Collections.singletonList(q4), result.get(1));
  }

  @Test
  public void testNewlineFollowedBySentenceFollowerIsAttachedToLast() {
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(WordToSentenceProcessor.NewlineIsSentenceBreak.ALWAYS);
    // HasWord w1 = new HasWordImpl("Test");
    // HasWord newline = new HasWordImpl("\n");
    // HasWord follower = new HasWordImpl(")");
    // List<HasWord> input = Arrays.asList(w1, newline, follower);
    // List<List<HasWord>> result = processor.process(input);
    // assertEquals(1, result.size());
    // assertEquals(Arrays.asList(w1, follower), result.get(0));
  }

  @Test
  public void testOnlySentenceFollowerAppearsFirst() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    // HasWord f1 = new HasWordImpl(")");
    // HasWord w2 = new HasWordImpl("hello");
    // HasWord w3 = new HasWordImpl("!");
    // List<HasWord> input = Arrays.asList(f1, w2, w3);
    // List<List<HasWord>> result = processor.process(input);
    // assertEquals(1, result.size());
    // assertEquals(Arrays.asList(f1, w2, w3), result.get(0));
  }

  @Test
  public void testGetStringReturnsNullTextAnnotationFallback() {
    WordToSentenceProcessor<CoreLabel> processor = new WordToSentenceProcessor<>();
    CoreLabel cl = new CoreLabel();
    cl.set(CoreAnnotations.TextAnnotation.class, null);
    cl.setWord("fallback");
    List<CoreLabel> input = Collections.singletonList(cl);
    List<List<CoreLabel>> result = processor.process(input);
    assertEquals(1, result.size());
    assertEquals(Collections.singletonList(cl), result.get(0));
  }

  @Test
  public void testXmlMatchesBreakPatternImplementationPath() {
    Set<String> xmlTags = new HashSet<>();
    xmlTags.add("line");
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            "[.?!]",
            "[)]+",
            WordToSentenceProcessor.DEFAULT_SENTENCE_BOUNDARIES_TO_DISCARD,
            xmlTags,
            null,
            WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
            null,
            null,
            false,
            false);
    // HasWord w1 = new HasWordImpl("Hello");
    // HasWord br = new HasWordImpl("<line>");
    // HasWord w2 = new HasWordImpl("Yes");
    // HasWord w3 = new HasWordImpl("?");
    // List<HasWord> input = Arrays.asList(w1, br, w2, w3);
    // List<List<HasWord>> result = processor.process(input);
    // assertEquals(2, result.size());
    // assertEquals(Collections.singletonList(w1), result.get(0));
    // assertEquals(Arrays.asList(w2, w3), result.get(1));
  }

  @Test
  public void testSentenceRegionUnclosedRegionNoEndPattern() {
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            "[.?!]",
            "[)]+",
            WordToSentenceProcessor.DEFAULT_SENTENCE_BOUNDARIES_TO_DISCARD,
            null,
            "comment",
            WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
            null,
            null,
            false,
            false);
    // HasWord regionOpen = new HasWordImpl("<comment>");
    // HasWord inner = new HasWordImpl("inside");
    // HasWord closeMissing = new HasWordImpl("ignored");
    // List<HasWord> input = Arrays.asList(regionOpen, inner, closeMissing);
    // List<List<HasWord>> result = processor.process(input);
    // assertEquals(1, result.size());
    // assertEquals(Collections.singletonList(inner), result.get(0));
  }

  @Test
  public void testForcedUntilEndSurvivesDiscardedTokens() {
    WordToSentenceProcessor<CoreLabel> processor = new WordToSentenceProcessor<>();
    CoreLabel w1 = new CoreLabel();
    w1.setWord("Token");
    w1.set(CoreAnnotations.ForcedSentenceUntilEndAnnotation.class, true);
    CoreLabel discard = new CoreLabel();
    discard.setWord("\n");
    CoreLabel regular = new CoreLabel();
    regular.setWord("More");
    CoreLabel end = new CoreLabel();
    end.setWord(".");
    end.set(CoreAnnotations.ForcedSentenceEndAnnotation.class, true);
    List<CoreLabel> input = Arrays.asList(w1, discard, regular, end);
    List<List<CoreLabel>> result = processor.process(input);
    assertEquals(1, result.size());
    assertEquals(Arrays.asList(w1, regular, end), result.get(0));
  }
}
