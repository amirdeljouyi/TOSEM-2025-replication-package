package edu.stanford.nlp.process;

import static org.junit.Assert.*;

import edu.stanford.nlp.ling.*;
import java.io.*;
import java.util.*;
import org.junit.Test;

public class WordToSentenceProcessor_4_GPTLLMTest {

  @Test
  public void testSimpleSentence() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    List<HasWord> input = new ArrayList<>();
    HasWord w1 = new Word("This");
    HasWord w2 = new Word("is");
    HasWord w3 = new Word("a");
    HasWord w4 = new Word("test");
    HasWord w5 = new Word(".");
    input.add(w1);
    input.add(w2);
    input.add(w3);
    input.add(w4);
    input.add(w5);
    List<List<HasWord>> output = processor.process(input);
    assertEquals(1, output.size());
    List<HasWord> sentence = output.get(0);
    assertEquals(5, sentence.size());
    assertEquals("This", sentence.get(0).word());
    assertEquals("is", sentence.get(1).word());
    assertEquals("a", sentence.get(2).word());
    assertEquals("test", sentence.get(3).word());
    assertEquals(".", sentence.get(4).word());
  }

  @Test
  public void testSentenceBoundarySplit() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    List<HasWord> input = new ArrayList<>();
    input.add(new Word("First"));
    input.add(new Word("sentence"));
    input.add(new Word("."));
    input.add(new Word("Second"));
    input.add(new Word("sentence"));
    input.add(new Word("?"));
    List<List<HasWord>> output = processor.process(input);
    assertEquals(2, output.size());
    List<HasWord> firstSentence = output.get(0);
    assertEquals(3, firstSentence.size());
    assertEquals("First", firstSentence.get(0).word());
    assertEquals("sentence", firstSentence.get(1).word());
    assertEquals(".", firstSentence.get(2).word());
    List<HasWord> secondSentence = output.get(1);
    assertEquals(3, secondSentence.size());
    assertEquals("Second", secondSentence.get(0).word());
    assertEquals("sentence", secondSentence.get(1).word());
    assertEquals("?", secondSentence.get(2).word());
  }

  @Test
  public void testForcedSentenceEnd() {
    WordToSentenceProcessor<CoreLabel> processor = new WordToSentenceProcessor<>();
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Token1");
    token1.setValue("Token1");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("EndToken");
    token2.setValue("EndToken");
    token2.set(CoreAnnotations.ForcedSentenceEndAnnotation.class, true);
    List<CoreLabel> input = new ArrayList<>();
    input.add(token1);
    input.add(token2);
    List<List<CoreLabel>> output = processor.process(input);
    assertEquals(1, output.size());
    List<CoreLabel> sentence = output.get(0);
    assertEquals(2, sentence.size());
    assertEquals("Token1", sentence.get(0).word());
    assertEquals("EndToken", sentence.get(1).word());
  }

  @Test
  public void testTreatAllAsOneSentence() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>(true);
    List<HasWord> input = new ArrayList<>();
    input.add(new Word("Why"));
    input.add(new Word("no"));
    input.add(new Word("split"));
    input.add(new Word("?"));
    input.add(new Word("still"));
    input.add(new Word("no"));
    input.add(new Word("split"));
    List<List<HasWord>> output = processor.process(input);
    assertEquals(1, output.size());
    List<HasWord> sentence = output.get(0);
    assertEquals(7, sentence.size());
    assertEquals("Why", sentence.get(0).word());
    assertEquals("no", sentence.get(1).word());
    assertEquals("split", sentence.get(2).word());
    assertEquals("?", sentence.get(3).word());
    assertEquals("still", sentence.get(4).word());
    assertEquals("no", sentence.get(5).word());
    assertEquals("split", sentence.get(6).word());
  }

  @Test
  public void testEmptyInput() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    List<HasWord> input = new ArrayList<>();
    List<List<HasWord>> output = processor.process(input);
    assertEquals(0, output.size());
  }

  @Test
  public void testInvalidNewlineOption() {
    try {
      WordToSentenceProcessor.stringToNewlineIsSentenceBreak("invalid-option");
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException expected) {
      assertTrue(expected.getMessage().contains("Not a valid NewlineIsSentenceBreak name"));
    }
  }

  @Test
  public void testAllowEmptySentences() {
    Set<String> discard = new HashSet<>();
    discard.add("\n");
    // WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>(".", "", discard,
    // null, null, WordToSentenceProcessor.WordToSentenceProcessor.NewlineIsSentenceBreak.ALWAYS,
    // null, null, false, true);
    List<HasWord> input = new ArrayList<>();
    input.add(new Word("\n"));
    // List<List<HasWord>> output = processor.process(input);
    // assertEquals(1, output.size());
    // assertTrue(output.get(0).isEmpty());
  }

  @Test
  public void testRegionFiltering() {
    Set<String> discard = new HashSet<>();
    discard.add("<doc>");
    discard.add("</doc>");
    // WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>(".", "", discard,
    // null, "doc", WordToSentenceProcessor.WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
    // null, null, false, false);
    List<HasWord> input = new ArrayList<>();
    input.add(new Word("<notdoc>"));
    input.add(new Word("ignore"));
    input.add(new Word("<doc>"));
    input.add(new Word("Hello"));
    input.add(new Word("."));
    input.add(new Word("</doc>"));
    input.add(new Word("outside"));
    // List<List<HasWord>> output = processor.process(input);
    // assertEquals(1, output.size());
    // List<HasWord> sentence = output.get(0);
    // assertEquals(2, sentence.size());
    // assertEquals("Hello", sentence.get(0).word());
    // assertEquals(".", sentence.get(1).word());
  }

  @Test
  public void testBoundaryFollowerAtDocumentStart() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    List<HasWord> input = new ArrayList<>();
    input.add(
        new CoreLabel() {

          {
            setWord(")");
            setValue(")");
          }
        });
    input.add(
        new CoreLabel() {

          {
            setWord("Hello");
            setValue("Hello");
          }
        });
    input.add(
        new CoreLabel() {

          {
            setWord(".");
            setValue(".");
          }
        });
    List<List<HasWord>> output = processor.process(input);
    assertEquals(1, output.size());
    assertEquals(")", output.get(0).get(0).word());
    assertEquals("Hello", output.get(0).get(1).word());
    assertEquals(".", output.get(0).get(2).word());
  }

  @Test
  public void testDiscardTokenImmediatelyAfterBoundary() {
    Set<String> discard = new HashSet<>();
    discard.add("\n");
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            ".",
            ")",
            discard,
            null,
            null,
            WordToSentenceProcessor.NewlineIsSentenceBreak.ALWAYS,
            null,
            null,
            false,
            false);
    List<HasWord> input = new ArrayList<>();
    input.add(
        new CoreLabel() {

          {
            setWord("Hello");
            setValue("Hello");
          }
        });
    input.add(
        new CoreLabel() {

          {
            setWord(".");
            setValue(".");
          }
        });
    input.add(
        new CoreLabel() {

          {
            setWord("\n");
            setValue("\n");
          }
        });
    input.add(
        new CoreLabel() {

          {
            setWord("World");
            setValue("World");
          }
        });
    input.add(
        new CoreLabel() {

          {
            setWord(".");
            setValue(".");
          }
        });
    List<List<HasWord>> output = processor.process(input);
    assertEquals(2, output.size());
    assertEquals("Hello", output.get(0).get(0).word());
    assertEquals(".", output.get(0).get(1).word());
    assertEquals("World", output.get(1).get(0).word());
    assertEquals(".", output.get(1).get(1).word());
  }

  @Test
  public void testMisbalancedQuotesStillJoinToPriorSentence() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    List<HasWord> input = new ArrayList<>();
    input.add(
        new CoreLabel() {

          {
            setWord("He");
            setValue("He");
          }
        });
    input.add(
        new CoreLabel() {

          {
            setWord("said");
            setValue("said");
          }
        });
    input.add(
        new CoreLabel() {

          {
            setWord(".");
            setValue(".");
          }
        });
    input.add(
        new CoreLabel() {

          {
            setWord("\"");
            setValue("\"");
          }
        });
    List<List<HasWord>> output = processor.process(input);
    assertEquals(1, output.size());
    assertEquals(4, output.get(0).size());
    assertEquals("\"", output.get(0).get(3).word());
  }

  @Test
  public void testSentenceEndingWithoutPunctuationIsCollected() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    List<HasWord> input = new ArrayList<>();
    input.add(
        new CoreLabel() {

          {
            setWord("This");
            setValue("This");
          }
        });
    input.add(
        new CoreLabel() {

          {
            setWord("has");
            setValue("has");
          }
        });
    input.add(
        new CoreLabel() {

          {
            setWord("no");
            setValue("no");
          }
        });
    input.add(
        new CoreLabel() {

          {
            setWord("period");
            setValue("period");
          }
        });
    List<List<HasWord>> output = processor.process(input);
    assertEquals(1, output.size());
    assertEquals("This", output.get(0).get(0).word());
    assertEquals("period", output.get(0).get(3).word());
  }

  @Test
  public void testSentenceRegionEndsTriggerSplit() {
    Set<String> discard = new HashSet<>();
    discard.add("<p>");
    discard.add("</p>");
    // WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>(".", ")", discard,
    // null, "p", WordToSentenceProcessor.WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
    // null, null, false, false);
    List<HasWord> input = new ArrayList<>();
    input.add(
        new CoreLabel() {

          {
            setWord("<p>");
            setValue("<p>");
          }
        });
    input.add(
        new CoreLabel() {

          {
            setWord("Hello");
            setValue("Hello");
          }
        });
    input.add(
        new CoreLabel() {

          {
            setWord(".");
            setValue(".");
          }
        });
    input.add(
        new CoreLabel() {

          {
            setWord("</p>");
            setValue("</p>");
          }
        });
    input.add(
        new CoreLabel() {

          {
            setWord("<p>");
            setValue("<p>");
          }
        });
    input.add(
        new CoreLabel() {

          {
            setWord("Again");
            setValue("Again");
          }
        });
    input.add(
        new CoreLabel() {

          {
            setWord(".");
            setValue(".");
          }
        });
    input.add(
        new CoreLabel() {

          {
            setWord("</p>");
            setValue("</p>");
          }
        });
    // List<List<HasWord>> output = processor.process(input);
    // assertEquals(2, output.size());
    // assertEquals("Hello", output.get(0).get(0).word());
    // assertEquals("Again", output.get(1).get(0).word());
  }

  @Test
  public void testMultiTokenPatternTriggersSplit() {
    // SequencePattern<CoreLabel> sequencePattern = TokenSequencePattern.compile("([{word:This}
    // ({word:is}) ({word:a}) ({word:test})])");
    // Set<String> discard = new HashSet<>();
    // discard.add("\n");
    // WordToSentenceProcessor<CoreLabel> processor = new WordToSentenceProcessor<>(".", "",
    // discard, null, null, WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER, sequencePattern,
    // null, false, false);
    CoreLabel t1 = new CoreLabel();
    t1.setWord("This");
    t1.set(CoreAnnotations.TextAnnotation.class, "This");
    CoreLabel t2 = new CoreLabel();
    t2.setWord("is");
    t2.set(CoreAnnotations.TextAnnotation.class, "is");
    CoreLabel t3 = new CoreLabel();
    t3.setWord("a");
    t3.set(CoreAnnotations.TextAnnotation.class, "a");
    CoreLabel t4 = new CoreLabel();
    t4.setWord("test");
    t4.set(CoreAnnotations.TextAnnotation.class, "test");
    CoreLabel t5 = new CoreLabel();
    t5.setWord("!");
    t5.set(CoreAnnotations.TextAnnotation.class, "!");
    List<CoreLabel> input = Arrays.asList(t1, t2, t3, t4, t5);
    // List<List<CoreLabel>> output = processor.process(input);
    // assertEquals(2, output.size());
    // assertEquals(4, output.get(0).size());
    // assertEquals("This", output.get(0).get(0).word());
    // assertEquals("!", output.get(1).get(0).word());
  }

  @Test
  public void testDiscardViaRegexTokenPattern() {
    Set<String> discard = new HashSet<>();
    discard.add("\n");
    Set<String> regexToDiscard = new HashSet<>();
    regexToDiscard.add("^\\[.*\\]$");
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            ".",
            ")",
            discard,
            null,
            null,
            WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
            null,
            regexToDiscard,
            false,
            false);
    List<HasWord> input = new ArrayList<>();
    input.add(
        new CoreLabel() {

          {
            setWord("Hello");
            setValue("Hello");
          }
        });
    input.add(
        new CoreLabel() {

          {
            setWord("[SKIP]");
            setValue("[SKIP]");
          }
        });
    input.add(
        new CoreLabel() {

          {
            setWord("world");
            setValue("world");
          }
        });
    input.add(
        new CoreLabel() {

          {
            setWord(".");
            setValue(".");
          }
        });
    List<List<HasWord>> output = processor.process(input);
    assertEquals(1, output.size());
    assertEquals(3, output.get(0).size());
    assertEquals("Hello", output.get(0).get(0).word());
    assertEquals("world", output.get(0).get(1).word());
    assertEquals(".", output.get(0).get(2).word());
  }

  @Test
  public void testTwoConsecutiveBlankLinesOnlyTriggerOnSecond() {
    Set<String> discard = new HashSet<>();
    discard.add("\n");
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            ".",
            "",
            discard,
            null,
            null,
            WordToSentenceProcessor.NewlineIsSentenceBreak.TWO_CONSECUTIVE,
            null,
            null,
            false,
            false);
    List<HasWord> input = new ArrayList<>();
    input.add(
        new CoreLabel() {

          {
            setWord("A");
            setValue("A");
          }
        });
    input.add(
        new CoreLabel() {

          {
            setWord(".");
            setValue(".");
          }
        });
    input.add(
        new CoreLabel() {

          {
            setWord("\n");
            setValue("\n");
          }
        });
    input.add(
        new CoreLabel() {

          {
            setWord("\n");
            setValue("\n");
          }
        });
    input.add(
        new CoreLabel() {

          {
            setWord("B");
            setValue("B");
          }
        });
    input.add(
        new CoreLabel() {

          {
            setWord("?");
            setValue("?");
          }
        });
    List<List<HasWord>> output = processor.process(input);
    assertEquals(2, output.size());
    assertEquals("A", output.get(0).get(0).word());
    assertEquals("B", output.get(1).get(0).word());
  }

  @Test
  public void testEmptySentenceWithAllowEmptySuppressed() {
    Set<String> boundariesToDiscard = new HashSet<>();
    boundariesToDiscard.add("\n");
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            ".",
            "",
            boundariesToDiscard,
            null,
            null,
            WordToSentenceProcessor.NewlineIsSentenceBreak.ALWAYS,
            null,
            null,
            false,
            false);
    List<HasWord> input = new ArrayList<>();
    CoreLabel newline = new CoreLabel();
    newline.setWord("\n");
    input.add(newline);
    List<List<HasWord>> output = processor.process(input);
    assertEquals(0, output.size());
  }

  @Test
  public void testOnlyBoundaryFollowerAtBeginning() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    List<HasWord> input = new ArrayList<>();
    CoreLabel closeParen = new CoreLabel();
    closeParen.setWord(")");
    closeParen.setValue(")");
    CoreLabel token = new CoreLabel();
    token.setWord("Go");
    token.setValue("Go");
    input.add(closeParen);
    input.add(token);
    List<List<HasWord>> output = processor.process(input);
    assertEquals(1, output.size());
    assertEquals(2, output.get(0).size());
    assertEquals(")", output.get(0).get(0).word());
    assertEquals("Go", output.get(0).get(1).word());
  }

  @Test
  public void testForcedSentenceUntilEndAnnotation() {
    WordToSentenceProcessor<CoreLabel> processor = new WordToSentenceProcessor<>();
    CoreLabel start = new CoreLabel();
    start.setWord("Start");
    start.setValue("Start");
    start.set(CoreAnnotations.ForcedSentenceUntilEndAnnotation.class, true);
    CoreLabel middle = new CoreLabel();
    middle.setWord("middle");
    middle.setValue("middle");
    CoreLabel end = new CoreLabel();
    end.setWord("end");
    end.setValue("end");
    end.set(CoreAnnotations.ForcedSentenceEndAnnotation.class, true);
    List<CoreLabel> input = new ArrayList<>();
    input.add(start);
    input.add(middle);
    input.add(end);
    List<List<CoreLabel>> output = processor.process(input);
    assertEquals(1, output.size());
    assertEquals(3, output.get(0).size());
    assertEquals("Start", output.get(0).get(0).word());
    assertEquals("middle", output.get(0).get(1).word());
    assertEquals("end", output.get(0).get(2).word());
  }

  @Test
  public void testMultiTokenMentionDisablesSplit() {
    WordToSentenceProcessor<CoreLabel> processor = new WordToSentenceProcessor<>();
    CoreLabel start = new CoreLabel();
    start.setWord("Name");
    start.setValue("Name");
    CoreLabel middle = new CoreLabel();
    middle.setWord("Tag");
    middle.setValue("Tag");
    // middle.set(CoreAnnotations.MentionTokenAnnotation.class, new DummyMultiTokenTag(false));
    CoreLabel end = new CoreLabel();
    end.setWord(".");
    end.setValue(".");
    List<CoreLabel> input = new ArrayList<>();
    input.add(start);
    input.add(middle);
    input.add(end);
    List<List<CoreLabel>> output = processor.process(input);
    assertEquals(1, output.size());
    assertEquals(3, output.get(0).size());
  }

  @Test
  public void testRegionFilteringWithNullRegionEndPattern() {
    Set<String> xml = new HashSet<>();
    xml.add("region");
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            ".",
            "",
            new HashSet<>(),
            xml,
            null,
            WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
            null,
            null,
            false,
            false);
    List<HasWord> input = new ArrayList<>();
    CoreLabel xmlStart = new CoreLabel();
    xmlStart.setWord("<region>");
    input.add(xmlStart);
    CoreLabel text = new CoreLabel();
    text.setWord("Inside");
    input.add(text);
    CoreLabel punct = new CoreLabel();
    punct.setWord(".");
    input.add(punct);
    CoreLabel xmlEnd = new CoreLabel();
    xmlEnd.setWord("</badtag>");
    input.add(xmlEnd);
    List<List<HasWord>> output = processor.process(input);
    assertEquals(1, output.size());
    assertEquals(2, output.get(0).size());
    assertEquals("Inside", output.get(0).get(0).word());
    assertEquals(".", output.get(0).get(1).word());
  }

  @Test
  public void testLateDiscardTokenAfterFinish() {
    Set<String> discard = new HashSet<>();
    discard.add("\n");
    // WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>(".",
    // DEFAULT_BOUNDARY_FOLLOWERS_REGEX, discard, null, null,
    // WordToSentenceProcessor.NewlineIsSentenceBreak.ALWAYS, null, null, false, true);
    CoreLabel a = new CoreLabel();
    a.setWord("Hi");
    CoreLabel dot = new CoreLabel();
    dot.setWord(".");
    CoreLabel newline = new CoreLabel();
    newline.setWord("\n");
    List<HasWord> input = new ArrayList<>();
    input.add(a);
    input.add(dot);
    input.add(newline);
    // List<List<HasWord>> output = processor.process(input);
    // assertEquals(1, output.size());
    // assertEquals(2, output.get(0).size());
  }

  @Test
  public void testPatternCompilationFallbacks() {
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            null,
            null,
            null,
            null,
            null,
            WordToSentenceProcessor.NewlineIsSentenceBreak.ALWAYS,
            null,
            null,
            false,
            false);
    CoreLabel a = new CoreLabel();
    a.setWord("OK");
    CoreLabel b = new CoreLabel();
    b.setWord("now");
    CoreLabel c = new CoreLabel();
    c.setWord(".");
    List<HasWord> input = new ArrayList<>();
    input.add(a);
    input.add(b);
    input.add(c);
    List<List<HasWord>> output = processor.process(input);
    assertEquals(1, output.size());
    assertEquals("OK", output.get(0).get(0).word());
    assertEquals(".", output.get(0).get(2).word());
  }

  @Test
  public void testCloseQuoteFollowedByNewSentence() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    CoreLabel a = new CoreLabel();
    a.setWord("Hello");
    CoreLabel b = new CoreLabel();
    b.setWord("!");
    CoreLabel quote = new CoreLabel();
    quote.setWord("\"");
    CoreLabel next = new CoreLabel();
    next.setWord("Hi");
    CoreLabel finalDot = new CoreLabel();
    finalDot.setWord(".");
    List<HasWord> input = new ArrayList<>();
    input.add(a);
    input.add(b);
    input.add(quote);
    input.add(next);
    input.add(finalDot);
    List<List<HasWord>> output = processor.process(input);
    assertEquals(2, output.size());
    assertEquals("\"", output.get(0).get(2).word());
    assertEquals("Hi", output.get(1).get(0).word());
  }

  @Test
  public void testSuppressedDiscardPatternSet() {
    // WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>(".", null, null,
    // null, null, WordToSentenceProcessor.WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
    // null, null, false, false);
    CoreLabel a = new CoreLabel();
    a.setWord("A");
    CoreLabel b = new CoreLabel();
    b.setWord(".");
    List<HasWord> input = new ArrayList<>();
    input.add(a);
    input.add(b);
    // List<List<HasWord>> output = processor.process(input);
    // assertEquals(1, output.size());
  }

  @Test
  public void testSentenceOnlySurroundedByDiscardTokens() {
    Set<String> discard = new HashSet<>();
    discard.add("<p>");
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            ".",
            "",
            discard,
            null,
            null,
            WordToSentenceProcessor.NewlineIsSentenceBreak.ALWAYS,
            null,
            null,
            false,
            true);
    CoreLabel open = new CoreLabel();
    open.setWord("<p>");
    CoreLabel hello = new CoreLabel();
    hello.setWord("Hello");
    CoreLabel dot = new CoreLabel();
    dot.setWord(".");
    CoreLabel close = new CoreLabel();
    close.setWord("<p>");
    List<HasWord> input = new ArrayList<>();
    input.add(open);
    input.add(hello);
    input.add(dot);
    input.add(close);
    List<List<HasWord>> output = processor.process(input);
    assertEquals(1, output.size());
    assertEquals(2, output.get(0).size());
  }

  @Test
  public void testSentenceWithOnlyDiscardTokens() {
    Set<String> discard = new HashSet<>();
    discard.add("BR");
    discard.add("NL");
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            ".",
            "",
            discard,
            null,
            null,
            WordToSentenceProcessor.NewlineIsSentenceBreak.ALWAYS,
            null,
            null,
            false,
            true);
    CoreLabel br = new CoreLabel();
    br.setWord("BR");
    CoreLabel nl = new CoreLabel();
    nl.setWord("NL");
    List<HasWord> input = new ArrayList<>();
    input.add(br);
    input.add(nl);
    List<List<HasWord>> output = processor.process(input);
    assertEquals(1, output.size());
    assertEquals(0, output.get(0).size());
  }

  @Test
  public void testFollowingTokenWithoutBoundaryPrecedingIt() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Hello");
    CoreLabel closePar = new CoreLabel();
    closePar.setWord(")");
    List<HasWord> input = new ArrayList<>();
    input.add(token1);
    input.add(closePar);
    List<List<HasWord>> output = processor.process(input);
    assertEquals(1, output.size());
    assertEquals(")", output.get(0).get(1).word());
  }

  @Test
  public void testDiscardAndForceEndOverlap() {
    Set<String> discard = new HashSet<>();
    discard.add("#");
    WordToSentenceProcessor<CoreLabel> processor =
        new WordToSentenceProcessor<>(
            ".",
            "",
            discard,
            null,
            null,
            WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
            null,
            null,
            false,
            false);
    CoreLabel tokenA = new CoreLabel();
    tokenA.setWord("A");
    CoreLabel marker = new CoreLabel();
    marker.setWord("#");
    marker.set(CoreAnnotations.ForcedSentenceEndAnnotation.class, true);
    CoreLabel tokenB = new CoreLabel();
    tokenB.setWord("B");
    List<CoreLabel> input = new ArrayList<>();
    input.add(tokenA);
    input.add(marker);
    input.add(tokenB);
    List<List<CoreLabel>> output = processor.process(input);
    assertEquals(2, output.size());
    assertEquals("A", output.get(0).get(0).word());
    assertEquals("B", output.get(1).get(0).word());
  }

  @Test
  public void testMultipleDiscardPatterns() {
    Set<String> discard = new HashSet<>();
    discard.add("NL");
    Set<String> regexDiscard = new HashSet<>();
    regexDiscard.add("^<.*>$");
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            ".",
            ")",
            discard,
            null,
            null,
            WordToSentenceProcessor.NewlineIsSentenceBreak.ALWAYS,
            null,
            regexDiscard,
            false,
            false);
    CoreLabel word = new CoreLabel();
    word.setWord("Text");
    CoreLabel xml = new CoreLabel();
    xml.setWord("<xml>");
    CoreLabel dot = new CoreLabel();
    dot.setWord(".");
    List<HasWord> input = new ArrayList<>();
    input.add(word);
    input.add(xml);
    input.add(dot);
    List<List<HasWord>> output = processor.process(input);
    assertEquals(1, output.size());
    assertEquals("Text", output.get(0).get(0).word());
    assertEquals(".", output.get(0).get(1).word());
  }

  @Test
  public void testEndForcedAfterPlainPunctuation() {
    WordToSentenceProcessor<CoreLabel> processor = new WordToSentenceProcessor<>();
    CoreLabel a = new CoreLabel();
    a.setWord("X");
    CoreLabel ex = new CoreLabel();
    ex.setWord("!");
    CoreLabel forced = new CoreLabel();
    forced.setWord("E");
    forced.set(CoreAnnotations.ForcedSentenceEndAnnotation.class, true);
    List<CoreLabel> input = new ArrayList<>();
    input.add(a);
    input.add(ex);
    input.add(forced);
    List<List<CoreLabel>> output = processor.process(input);
    assertEquals(2, output.size());
    assertEquals("!", output.get(0).get(1).word());
    assertEquals("E", output.get(1).get(0).word());
  }

  @Test
  public void testXmlBreakMatchTriggersSplitAndDiscard() {
    Set<String> xmlBreaks = new HashSet<>();
    xmlBreaks.add("div");
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            ".",
            "",
            Collections.emptySet(),
            xmlBreaks,
            null,
            WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
            null,
            null,
            false,
            false);
    CoreLabel word1 = new CoreLabel();
    word1.setWord("Start");
    CoreLabel breakTag = new CoreLabel();
    breakTag.setWord("<div>");
    CoreLabel word2 = new CoreLabel();
    word2.setWord("Next");
    CoreLabel dot = new CoreLabel();
    dot.setWord(".");
    List<HasWord> input = new ArrayList<>();
    input.add(word1);
    input.add(breakTag);
    input.add(word2);
    input.add(dot);
    List<List<HasWord>> output = processor.process(input);
    assertEquals(2, output.size());
    assertEquals("Start", output.get(0).get(0).word());
    assertEquals("Next", output.get(1).get(0).word());
    assertEquals(".", output.get(1).get(1).word());
  }

  @Test
  public void testFallbackMultiTokenBoundaryNoMatches() {
    // SequencePattern<CoreLabel> pattern = new SequencePattern<CoreLabel>() {
    //
    // @Override
    // public boolean match(List<? extends CoreLabel> list, int start) {
    // return false;
    // }
    //
    // @Override
    // public edu.stanford.nlp.ling.tokensregex.SequenceMatcher<CoreLabel> getMatcher(List<? extends
    // CoreLabel> list) {
    // return new edu.stanford.nlp.ling.tokensregex.SequenceMatcher<CoreLabel>() {
    //
    // @Override
    // public boolean find() {
    // return false;
    // }
    //
    // @Override
    // public List<CoreLabel> groupNodes() {
    // return null;
    // }
    // };
    // }
    // };
    // WordToSentenceProcessor<CoreLabel> processor = new WordToSentenceProcessor<>(".", "",
    // Collections.emptySet(), null, null, WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
    // pattern, null, false, false);
    CoreLabel x = new CoreLabel();
    x.setWord("A");
    CoreLabel y = new CoreLabel();
    y.setWord(".");
    List<CoreLabel> input = new ArrayList<>();
    input.add(x);
    input.add(y);
    // List<List<CoreLabel>> output = processor.process(input);
    // assertEquals(1, output.size());
  }

  @Test
  public void testRegionEndMarksSplitWithoutEndTagMatch() {
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            ".",
            "",
            Collections.emptySet(),
            null,
            "seg",
            WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
            null,
            null,
            false,
            false);
    CoreLabel tagStart = new CoreLabel();
    tagStart.setWord("<seg>");
    CoreLabel token = new CoreLabel();
    token.setWord("Hi");
    CoreLabel dot = new CoreLabel();
    dot.setWord(".");
    CoreLabel tagEnd = new CoreLabel();
    tagEnd.setWord("</seg>");
    List<HasWord> input = new ArrayList<>();
    input.add(tagStart);
    input.add(token);
    input.add(dot);
    input.add(tagEnd);
    List<List<HasWord>> output = processor.process(input);
    assertEquals(1, output.size());
    assertEquals("Hi", output.get(0).get(0).word());
    assertEquals(".", output.get(0).get(1).word());
  }

  @Test
  public void testAllowEmptySentencesWithMultipleSplitTokens() {
    Set<String> discard = new HashSet<>();
    discard.add("<br>");
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            ".",
            "",
            discard,
            null,
            null,
            WordToSentenceProcessor.NewlineIsSentenceBreak.ALWAYS,
            null,
            null,
            false,
            true);
    CoreLabel br1 = new CoreLabel();
    br1.setWord("<br>");
    CoreLabel br2 = new CoreLabel();
    br2.setWord("<br>");
    CoreLabel text = new CoreLabel();
    text.setWord("Data");
    CoreLabel dot = new CoreLabel();
    dot.setWord(".");
    List<HasWord> input = new ArrayList<>();
    input.add(br1);
    input.add(br2);
    input.add(text);
    input.add(dot);
    List<List<HasWord>> output = processor.process(input);
    assertEquals(2, output.size());
    assertEquals(0, output.get(0).size());
    assertEquals("Data", output.get(1).get(0).word());
  }

  @Test
  public void testSingleBoundaryTokenOnly() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    CoreLabel token = new CoreLabel();
    token.setWord(".");
    token.setValue(".");
    List<HasWord> input = new ArrayList<>();
    input.add(token);
    List<List<HasWord>> output = processor.process(input);
    assertEquals(1, output.size());
    assertEquals(1, output.get(0).size());
    assertEquals(".", output.get(0).get(0).word());
  }

  @Test
  public void testSentenceWithOnlyFollowersAtStartIgnored() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    CoreLabel follower = new CoreLabel();
    follower.setWord(")");
    follower.setValue(")");
    CoreLabel main = new CoreLabel();
    main.setWord("Hello");
    main.setValue("Hello");
    CoreLabel dot = new CoreLabel();
    dot.setWord(".");
    dot.setValue(".");
    List<HasWord> input = new ArrayList<>();
    input.add(follower);
    input.add(main);
    input.add(dot);
    List<List<HasWord>> output = processor.process(input);
    assertEquals(1, output.size());
    assertEquals(")", output.get(0).get(0).word());
    assertEquals("Hello", output.get(0).get(1).word());
    assertEquals(".", output.get(0).get(2).word());
  }

  @Test
  public void testOnlyForceEndTokenTriggersSplitEvenWithoutBoundary() {
    WordToSentenceProcessor<CoreLabel> processor = new WordToSentenceProcessor<>();
    CoreLabel word1 = new CoreLabel();
    word1.setWord("Something");
    CoreLabel word2 = new CoreLabel();
    word2.setWord("ForceBreak");
    word2.set(CoreAnnotations.ForcedSentenceEndAnnotation.class, true);
    CoreLabel word3 = new CoreLabel();
    word3.setWord("Next");
    List<CoreLabel> input = new ArrayList<>();
    input.add(word1);
    input.add(word2);
    input.add(word3);
    List<List<CoreLabel>> output = processor.process(input);
    assertEquals(2, output.size());
    assertEquals(2, output.get(0).size());
    assertEquals("ForceBreak", output.get(0).get(1).word());
    assertEquals("Next", output.get(1).get(0).word());
  }

  @Test
  public void testRegionElementWithoutEndTagSkipsContent() {
    Set<String> discard = new HashSet<>();
    discard.add("<div>");
    // WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>(".", "", discard,
    // null, "div", WordToSentenceProcessor.WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
    // null, null, false, false);
    CoreLabel outside = new CoreLabel();
    outside.setWord("Outside");
    CoreLabel start = new CoreLabel();
    start.setWord("<div>");
    CoreLabel inside = new CoreLabel();
    inside.setWord("Inside");
    CoreLabel dot = new CoreLabel();
    dot.setWord(".");
    List<HasWord> input = new ArrayList<>();
    input.add(start);
    input.add(inside);
    input.add(dot);
    input.add(outside);
    // List<List<HasWord>> output = processor.process(input);
    // assertEquals(1, output.size());
    // assertEquals(1, output.get(0).size());
    // assertEquals("Outside", output.get(0).get(0).word());
  }

  @Test
  public void testTokenMatchingTokenPatternsToDiscard() {
    Set<String> discardSet = new HashSet<>();
    discardSet.add("\n");
    Set<String> tokenRegexes = new HashSet<>();
    tokenRegexes.add("^#[A-Z]+$");
    // WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>(".", "",
    // discardSet, null, null,
    // WordToSentenceProcessor.WordToSentenceProcessor.NewlineIsSentenceBreak.ALWAYS, null,
    // tokenRegexes, false, false);
    CoreLabel word1 = new CoreLabel();
    word1.setWord("Start");
    CoreLabel discardToken = new CoreLabel();
    discardToken.setWord("#TAG");
    CoreLabel word2 = new CoreLabel();
    word2.setWord("End");
    CoreLabel dot = new CoreLabel();
    dot.setWord(".");
    List<HasWord> input = Arrays.asList(word1, discardToken, word2, dot);
    // List<List<HasWord>> output = processor.process(input);
    // assertEquals(1, output.size());
    // assertEquals(3, output.get(0).size());
    // assertEquals("Start", output.get(0).get(0).word());
    // assertEquals("End", output.get(0).get(1).word());
    // assertEquals(".", output.get(0).get(2).word());
  }

  @Test
  public void testForcedUntilEndHandlesMultiLine() {
    WordToSentenceProcessor<CoreLabel> processor = new WordToSentenceProcessor<>();
    CoreLabel word1 = new CoreLabel();
    word1.setWord("John");
    word1.set(CoreAnnotations.ForcedSentenceUntilEndAnnotation.class, true);
    CoreLabel newline = new CoreLabel();
    newline.setWord("\n");
    CoreLabel word2 = new CoreLabel();
    word2.setWord("Smith");
    CoreLabel end = new CoreLabel();
    end.setWord(".");
    end.set(CoreAnnotations.ForcedSentenceEndAnnotation.class, true);
    List<CoreLabel> input = new ArrayList<>();
    input.add(word1);
    input.add(newline);
    input.add(word2);
    input.add(end);
    List<List<CoreLabel>> output = processor.process(input);
    assertEquals(1, output.size());
    assertEquals("John", output.get(0).get(0).word());
    assertEquals("Smith", output.get(0).get(1).word());
    assertEquals(".", output.get(0).get(2).word());
  }

  @Test
  public void testNoSentenceBoundaryReturnsUnit() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>(true);
    List<HasWord> input = new ArrayList<>();
    CoreLabel token1 = new CoreLabel();
    token1.setWord("This");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("is");
    CoreLabel token3 = new CoreLabel();
    token3.setWord("sentence");
    input.add(token1);
    input.add(token2);
    input.add(token3);
    List<List<HasWord>> output = processor.process(input);
    assertEquals(1, output.size());
    assertEquals(3, output.get(0).size());
    assertEquals("This", output.get(0).get(0).word());
    assertEquals("sentence", output.get(0).get(2).word());
  }

  @Test
  public void testMultipleBoundaryTokensTriggerMultipleSentences() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    CoreLabel t1 = new CoreLabel();
    t1.setWord("One");
    CoreLabel t2 = new CoreLabel();
    t2.setWord(".");
    CoreLabel t3 = new CoreLabel();
    t3.setWord(".");
    CoreLabel t4 = new CoreLabel();
    t4.setWord("!");
    CoreLabel t5 = new CoreLabel();
    t5.setWord("?");
    List<HasWord> input = new ArrayList<>();
    input.add(t1);
    input.add(t2);
    input.add(t3);
    input.add(t4);
    input.add(t5);
    List<List<HasWord>> output = processor.process(input);
    assertEquals(3, output.size());
    assertEquals("One", output.get(0).get(0).word());
    assertEquals(".", output.get(0).get(1).word());
    assertEquals(".", output.get(1).get(0).word());
    assertEquals("!", output.get(1).get(1).word());
    assertEquals("?", output.get(2).get(0).word());
  }

  @Test
  public void testRegionBeginWithoutRegionEndSkipsRemainingContent() {
    Set<String> discard = new HashSet<>();
    discard.add("<div>");
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            ".",
            "",
            discard,
            null,
            "div",
            WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
            null,
            null,
            false,
            false);
    CoreLabel enter = new CoreLabel();
    enter.setWord("<div>");
    CoreLabel a = new CoreLabel();
    a.setWord("Data");
    CoreLabel b = new CoreLabel();
    b.setWord("lost");
    List<HasWord> input = new ArrayList<>();
    input.add(a);
    input.add(b);
    input.add(enter);
    List<List<HasWord>> output = processor.process(input);
    assertEquals(1, output.size());
    assertEquals("Data", output.get(0).get(0).word());
    assertEquals("lost", output.get(0).get(1).word());
  }

  @Test
  public void testEmptyStringTokenHandledCorrectly() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    CoreLabel token1 = new CoreLabel();
    token1.setWord("");
    token1.setValue("");
    CoreLabel token2 = new CoreLabel();
    token2.setWord(".");
    token2.setValue(".");
    List<HasWord> input = new ArrayList<>();
    input.add(token1);
    input.add(token2);
    List<List<HasWord>> output = processor.process(input);
    assertEquals(1, output.size());
    assertEquals(2, output.get(0).size());
  }

  @Test
  public void testConsecutiveForcedEndTokensTriggerMultipleSplits() {
    WordToSentenceProcessor<CoreLabel> processor = new WordToSentenceProcessor<>();
    CoreLabel token1 = new CoreLabel();
    token1.setWord("A");
    token1.set(CoreAnnotations.ForcedSentenceEndAnnotation.class, true);
    CoreLabel token2 = new CoreLabel();
    token2.setWord("B");
    token2.set(CoreAnnotations.ForcedSentenceEndAnnotation.class, true);
    CoreLabel token3 = new CoreLabel();
    token3.setWord("C");
    List<CoreLabel> input = new ArrayList<>();
    input.add(token1);
    input.add(token2);
    input.add(token3);
    List<List<CoreLabel>> output = processor.process(input);
    assertEquals(3, output.size());
    assertEquals("A", output.get(0).get(0).word());
    assertEquals("B", output.get(1).get(0).word());
    assertEquals("C", output.get(2).get(0).word());
  }

  @Test
  public void testMultipleDiscardBoundaryTokensAreCoalesced() {
    Set<String> discard = new HashSet<>();
    discard.add("@BR");
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            ".",
            "",
            discard,
            null,
            null,
            WordToSentenceProcessor.NewlineIsSentenceBreak.ALWAYS,
            null,
            null,
            false,
            true);
    CoreLabel br1 = new CoreLabel();
    br1.setWord("@BR");
    CoreLabel br2 = new CoreLabel();
    br2.setWord("@BR");
    CoreLabel hello = new CoreLabel();
    hello.setWord("hello");
    CoreLabel dot = new CoreLabel();
    dot.setWord(".");
    List<HasWord> input = new ArrayList<>();
    input.add(br1);
    input.add(br2);
    input.add(hello);
    input.add(dot);
    List<List<HasWord>> output = processor.process(input);
    assertEquals(2, output.size());
    assertEquals(0, output.get(0).size());
    assertEquals(2, output.get(1).size());
  }

  @Test
  public void testBoundaryTokenWithoutFollowingPunctuationStillTriggersSplit() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Done");
    CoreLabel dot = new CoreLabel();
    dot.setWord(".");
    CoreLabel next = new CoreLabel();
    next.setWord("Then");
    List<HasWord> input = new ArrayList<>();
    input.add(token1);
    input.add(dot);
    input.add(next);
    List<List<HasWord>> output = processor.process(input);
    assertEquals(2, output.size());
    assertEquals("Done", output.get(0).get(0).word());
    assertEquals(".", output.get(0).get(1).word());
    assertEquals("Then", output.get(1).get(0).word());
  }

  @Test
  public void testStartWithDiscardAndBoundaryFollowBehavior() {
    Set<String> discard = new HashSet<>();
    discard.add("##");
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            ".",
            ")",
            discard,
            null,
            null,
            WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
            null,
            null,
            false,
            false);
    CoreLabel discard1 = new CoreLabel();
    discard1.setWord("##");
    CoreLabel starter = new CoreLabel();
    starter.setWord(".");
    CoreLabel follower = new CoreLabel();
    follower.setWord(")");
    List<HasWord> input = new ArrayList<>();
    input.add(discard1);
    input.add(starter);
    input.add(follower);
    List<List<HasWord>> output = processor.process(input);
    assertEquals(1, output.size());
    assertEquals(3, output.get(0).size());
  }

  @Test
  public void testRegionElementOnlyMatchesInsideTag() {
    Set<String> discard = new HashSet<>();
    discard.add("<section>");
    Set<String> xmlSet = new HashSet<>();
    xmlSet.add("section");
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            ".",
            "",
            discard,
            null,
            "section",
            WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
            null,
            null,
            false,
            false);
    CoreLabel outside = new CoreLabel();
    outside.setWord("outside");
    CoreLabel openRegion = new CoreLabel();
    openRegion.setWord("<section>");
    CoreLabel content = new CoreLabel();
    content.setWord("inside");
    CoreLabel closeRegion = new CoreLabel();
    closeRegion.setWord("</section>");
    List<HasWord> input = new ArrayList<>();
    input.add(outside);
    input.add(openRegion);
    input.add(content);
    input.add(closeRegion);
    List<List<HasWord>> output = processor.process(input);
    assertEquals(1, output.size());
    assertEquals(1, output.get(0).size());
    assertEquals("inside", output.get(0).get(0).word());
  }

  @Test
  public void testForcedAndNormalBoundaryTogetherTriggerSplitOnce() {
    WordToSentenceProcessor<CoreLabel> processor = new WordToSentenceProcessor<>();
    CoreLabel word1 = new CoreLabel();
    word1.setWord("Boom");
    CoreLabel force = new CoreLabel();
    force.setWord(".");
    force.set(CoreAnnotations.ForcedSentenceEndAnnotation.class, true);
    List<CoreLabel> input = new ArrayList<>();
    input.add(word1);
    input.add(force);
    List<List<CoreLabel>> output = processor.process(input);
    assertEquals(1, output.size());
    assertEquals("Boom", output.get(0).get(0).word());
    assertEquals(".", output.get(0).get(1).word());
  }

  @Test
  public void testProcessWithNullOptionalPatterns() {
    Set<String> discard = new HashSet<>();
    discard.add("\n");
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            ".",
            null,
            discard,
            null,
            null,
            WordToSentenceProcessor.NewlineIsSentenceBreak.ALWAYS,
            null,
            null,
            false,
            false);
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Hello");
    CoreLabel token2 = new CoreLabel();
    token2.setWord(".");
    CoreLabel token3 = new CoreLabel();
    token3.setWord("\n");
    CoreLabel token4 = new CoreLabel();
    token4.setWord("World");
    List<HasWord> input = new ArrayList<>();
    input.add(token1);
    input.add(token2);
    input.add(token3);
    input.add(token4);
    List<List<HasWord>> output = processor.process(input);
    assertEquals(2, output.size());
    assertEquals("Hello", output.get(0).get(0).word());
    assertEquals("World", output.get(1).get(0).word());
  }

  @Test
  public void testSingleRegionTokenSuppressAndRestore() {
    Set<String> xml = new HashSet<>();
    xml.add("block");
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            ".",
            null,
            new HashSet<>(),
            null,
            "block",
            WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
            null,
            null,
            false,
            false);
    CoreLabel rstart = new CoreLabel();
    rstart.setWord("<block>");
    CoreLabel inside = new CoreLabel();
    inside.setWord("inside");
    CoreLabel rend = new CoreLabel();
    rend.setWord("</block>");
    CoreLabel outside = new CoreLabel();
    outside.setWord("outside");
    List<HasWord> input = new ArrayList<>();
    input.add(rstart);
    input.add(inside);
    input.add(rend);
    input.add(outside);
    List<List<HasWord>> output = processor.process(input);
    assertEquals(2, output.size());
    assertEquals("inside", output.get(0).get(0).word());
    assertEquals("outside", output.get(1).get(0).word());
  }

  @Test
  public void testTrailingBoundaryFollowerAtDocumentEndIgnored() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Hello");
    CoreLabel dot = new CoreLabel();
    dot.setWord(".");
    CoreLabel follower = new CoreLabel();
    follower.setWord(")");
    List<HasWord> input = new ArrayList<>();
    input.add(token1);
    input.add(dot);
    input.add(follower);
    List<List<HasWord>> output = processor.process(input);
    assertEquals(1, output.size());
    assertEquals(")", output.get(0).get(2).word());
  }

  @Test
  public void testOnlyDiscardTokensWithSuppressEmptyFalse() {
    Set<String> discard = new HashSet<>();
    discard.add("@SEP");
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            ".",
            "",
            discard,
            null,
            null,
            WordToSentenceProcessor.NewlineIsSentenceBreak.ALWAYS,
            null,
            null,
            false,
            true);
    CoreLabel t1 = new CoreLabel();
    t1.setWord("@SEP");
    CoreLabel t2 = new CoreLabel();
    t2.setWord("@SEP");
    List<HasWord> input = new ArrayList<>();
    input.add(t1);
    input.add(t2);
    List<List<HasWord>> output = processor.process(input);
    assertEquals(1, output.size());
    assertTrue(output.get(0).isEmpty());
  }

  @Test
  public void testEmptyRegionEnclosedBetweenBeginAndEndSuppressed() {
    Set<String> discard = new HashSet<>();
    discard.add("<p>");
    discard.add("</p>");
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            ".",
            "",
            discard,
            null,
            "p",
            WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
            null,
            null,
            false,
            false);
    CoreLabel open = new CoreLabel();
    open.setWord("<p>");
    CoreLabel close = new CoreLabel();
    close.setWord("</p>");
    List<HasWord> input = new ArrayList<>();
    input.add(open);
    input.add(close);
    List<List<HasWord>> output = processor.process(input);
    assertTrue(output.isEmpty());
  }

  @Test
  public void testDiscardedBoundaryWithForcedEndIsRetainedAsSentenceEnd() {
    Set<String> discard = new HashSet<>();
    discard.add("END");
    WordToSentenceProcessor<CoreLabel> processor =
        new WordToSentenceProcessor<>(
            ".",
            "",
            discard,
            null,
            null,
            WordToSentenceProcessor.NewlineIsSentenceBreak.ALWAYS,
            null,
            null,
            false,
            false);
    CoreLabel word = new CoreLabel();
    word.setWord("hello");
    CoreLabel boundary = new CoreLabel();
    boundary.setWord("END");
    boundary.set(CoreAnnotations.ForcedSentenceEndAnnotation.class, true);
    List<CoreLabel> input = new ArrayList<>();
    input.add(word);
    input.add(boundary);
    List<List<CoreLabel>> output = processor.process(input);
    assertEquals(1, output.size());
    assertEquals("hello", output.get(0).get(0).word());
  }

  @Test
  public void testFollowerAddedWhenPreviousSentenceIsEmptyButForcedIsActive() {
    WordToSentenceProcessor<CoreLabel> processor = new WordToSentenceProcessor<>();
    CoreLabel follower = new CoreLabel();
    follower.setWord(")");
    follower.setValue(")");
    CoreLabel forced = new CoreLabel();
    forced.setWord("(");
    forced.set(CoreAnnotations.ForcedSentenceEndAnnotation.class, true);
    List<CoreLabel> input = new ArrayList<>();
    input.add(forced);
    input.add(follower);
    List<List<CoreLabel>> output = processor.process(input);
    assertEquals(1, output.size());
    assertEquals(2, output.get(0).size());
    assertEquals("(", output.get(0).get(0).word());
    assertEquals(")", output.get(0).get(1).word());
  }

  @Test
  public void testXmlBreakTriggersSplitEvenWithoutPunctuation() {
    Set<String> xmlBreaks = new HashSet<>();
    xmlBreaks.add("div");
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            ".",
            "",
            Collections.emptySet(),
            xmlBreaks,
            null,
            WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
            null,
            null,
            false,
            false);
    CoreLabel token1 = new CoreLabel();
    token1.setWord("part1");
    CoreLabel xmlBreak = new CoreLabel();
    xmlBreak.setWord("<div>");
    CoreLabel token2 = new CoreLabel();
    token2.setWord("part2");
    List<HasWord> input = new ArrayList<>();
    input.add(token1);
    input.add(xmlBreak);
    input.add(token2);
    List<List<HasWord>> output = processor.process(input);
    assertEquals(2, output.size());
    assertEquals("part1", output.get(0).get(0).word());
    assertEquals("part2", output.get(1).get(0).word());
  }

  @Test
  public void testTrailingDiscardBoundaryWithoutSplit() {
    Set<String> discard = new HashSet<>();
    discard.add("NL");
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            ".",
            "",
            discard,
            null,
            null,
            WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
            null,
            null,
            false,
            false);
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Test");
    CoreLabel token2 = new CoreLabel();
    token2.setWord(".");
    CoreLabel token3 = new CoreLabel();
    token3.setWord("NL");
    List<HasWord> input = new ArrayList<>();
    input.add(token1);
    input.add(token2);
    input.add(token3);
    List<List<HasWord>> output = processor.process(input);
    assertEquals(1, output.size());
    assertEquals(2, output.get(0).size());
  }

  @Test
  public void testMisbalancedQuotesPreventsSplitDueToOddCount() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    CoreLabel t1 = new CoreLabel();
    t1.setWord("\"");
    CoreLabel t2 = new CoreLabel();
    t2.setWord("Say");
    CoreLabel t3 = new CoreLabel();
    t3.setWord(".");
    CoreLabel t4 = new CoreLabel();
    t4.setWord("\"");
    List<HasWord> input = new ArrayList<>();
    input.add(t1);
    input.add(t2);
    input.add(t3);
    input.add(t4);
    List<List<HasWord>> output = processor.process(input);
    assertEquals(1, output.size());
    assertEquals(4, output.get(0).size());
  }

  @Test
  public void testRegionEndWithoutBeginIsIgnored() {
    Set<String> discard = new HashSet<>();
    discard.add("</body>");
    WordToSentenceProcessor<HasWord> processor =
        new WordToSentenceProcessor<>(
            ".",
            "",
            discard,
            null,
            "body",
            WordToSentenceProcessor.NewlineIsSentenceBreak.NEVER,
            null,
            null,
            false,
            false);
    CoreLabel endRegion = new CoreLabel();
    endRegion.setWord("</body>");
    CoreLabel valid = new CoreLabel();
    valid.setWord("Data");
    CoreLabel dot = new CoreLabel();
    dot.setWord(".");
    List<HasWord> input = new ArrayList<>();
    input.add(endRegion);
    input.add(valid);
    input.add(dot);
    List<List<HasWord>> output = processor.process(input);
    assertEquals(1, output.size());
    assertEquals("Data", output.get(0).get(0).word());
  }

  @Test
  public void testSingleSentenceWithoutExplicitBoundaryIncludedAsOne() {
    WordToSentenceProcessor<HasWord> processor = new WordToSentenceProcessor<>();
    CoreLabel one = new CoreLabel();
    one.setWord("Only");
    CoreLabel two = new CoreLabel();
    two.setWord("this");
    List<HasWord> input = new ArrayList<>();
    input.add(one);
    input.add(two);
    List<List<HasWord>> output = processor.process(input);
    assertEquals(1, output.size());
    assertEquals("Only", output.get(0).get(0).word());
    assertEquals("this", output.get(0).get(1).word());
  }
}
