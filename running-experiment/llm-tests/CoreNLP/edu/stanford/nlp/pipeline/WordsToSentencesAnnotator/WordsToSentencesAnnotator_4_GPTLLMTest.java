package edu.stanford.nlp.pipeline;

import java.util.*;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.SentenceUtils;
import edu.stanford.nlp.util.CoreMap;

import edu.stanford.nlp.util.logging.Redwood;
import org.junit.Test;

import edu.stanford.nlp.util.PropertiesUtils;

import static org.junit.Assert.*;

public class WordsToSentencesAnnotator_4_GPTLLMTest {

 @Test
  public void testSingleSentence() {
    String text = "This is a test.";

    CoreLabel token0 = new CoreLabel();
    token0.set(CoreAnnotations.TextAnnotation.class, "This");
    token0.set(CoreAnnotations.ValueAnnotation.class, "This");
    token0.set(CoreAnnotations.OriginalTextAnnotation.class, "This");
    token0.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token0.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);
    token0.set(CoreAnnotations.IsNewlineAnnotation.class, false);
    token0.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    token0.set(CoreAnnotations.TokenEndAnnotation.class, 0);

    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "is");
    token1.set(CoreAnnotations.ValueAnnotation.class, "is");
    token1.set(CoreAnnotations.OriginalTextAnnotation.class, "is");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 7);
    token1.set(CoreAnnotations.IsNewlineAnnotation.class, false);
    token1.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.TokenEndAnnotation.class, 0);

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "a");
    token2.set(CoreAnnotations.ValueAnnotation.class, "a");
    token2.set(CoreAnnotations.OriginalTextAnnotation.class, "a");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 8);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 9);
    token2.set(CoreAnnotations.IsNewlineAnnotation.class, false);
    token2.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    token2.set(CoreAnnotations.TokenEndAnnotation.class, 0);

    CoreLabel token3 = new CoreLabel();
    token3.set(CoreAnnotations.TextAnnotation.class, "test");
    token3.set(CoreAnnotations.ValueAnnotation.class, "test");
    token3.set(CoreAnnotations.OriginalTextAnnotation.class, "test");
    token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 10);
    token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 14);
    token3.set(CoreAnnotations.IsNewlineAnnotation.class, false);
    token3.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    token3.set(CoreAnnotations.TokenEndAnnotation.class, 0);

    CoreLabel token4 = new CoreLabel();
    token4.set(CoreAnnotations.TextAnnotation.class, ".");
    token4.set(CoreAnnotations.ValueAnnotation.class, ".");
    token4.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
    token4.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 14);
    token4.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 15);
    token4.set(CoreAnnotations.IsNewlineAnnotation.class, false);
    token4.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    token4.set(CoreAnnotations.TokenEndAnnotation.class, 0);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token0);
    tokens.add(token1);
    tokens.add(token2);
    tokens.add(token3);
    tokens.add(token4);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Properties props = new Properties();
    props.setProperty("ssplit.isOneSentence", "true");

    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator(props);

    annotator.annotate(annotation);

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertNotNull(sentences);
    assertEquals(1, sentences.size());

    CoreMap sentence = sentences.get(0);
    assertEquals("This is a test.", sentence.get(CoreAnnotations.TextAnnotation.class));
    assertEquals(0, (int) sentence.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
    assertEquals(15, (int) sentence.get(CoreAnnotations.CharacterOffsetEndAnnotation.class));
  }
@Test
  public void testMissingTokensThrowsException() {
    Annotation annotation = new Annotation("Missing tokens test");

    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();

    try {
      annotator.annotate(annotation);
      fail("Expected IllegalArgumentException due to missing tokens");
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().contains("unable to find words/tokens"));
    }
  }
@Test
  public void testNewlineSplitterCountsLines() {
    String text = "Test1\n\nTest2";

    CoreLabel token0 = new CoreLabel();
    token0.set(CoreAnnotations.TextAnnotation.class, "Test1");
    token0.set(CoreAnnotations.ValueAnnotation.class, "Test1");
    token0.set(CoreAnnotations.OriginalTextAnnotation.class, "Test1");
    token0.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token0.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    token0.set(CoreAnnotations.IsNewlineAnnotation.class, false);
    token0.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    token0.set(CoreAnnotations.TokenEndAnnotation.class, 0);

    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "\n");
    token1.set(CoreAnnotations.ValueAnnotation.class, "\n");
    token1.set(CoreAnnotations.OriginalTextAnnotation.class, "\n");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);
    token1.set(CoreAnnotations.IsNewlineAnnotation.class, true);
    token1.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.TokenEndAnnotation.class, 0);

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "\n");
    token2.set(CoreAnnotations.ValueAnnotation.class, "\n");
    token2.set(CoreAnnotations.OriginalTextAnnotation.class, "\n");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 6);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 7);
    token2.set(CoreAnnotations.IsNewlineAnnotation.class, true);
    token2.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    token2.set(CoreAnnotations.TokenEndAnnotation.class, 0);

    CoreLabel token3 = new CoreLabel();
    token3.set(CoreAnnotations.TextAnnotation.class, "Test2");
    token3.set(CoreAnnotations.ValueAnnotation.class, "Test2");
    token3.set(CoreAnnotations.OriginalTextAnnotation.class, "Test2");
    token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 7);
    token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 12);
    token3.set(CoreAnnotations.IsNewlineAnnotation.class, false);
    token3.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    token3.set(CoreAnnotations.TokenEndAnnotation.class, 0);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token0);
    tokens.add(token1);
    tokens.add(token2);
    tokens.add(token3);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    WordsToSentencesAnnotator annotator = WordsToSentencesAnnotator.newlineSplitter("\n");

    annotator.annotate(annotation);

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertNotNull(sentences);
    assertEquals(2, sentences.size());

    Integer line1 = sentences.get(0).get(CoreAnnotations.LineNumberAnnotation.class);
    Integer line2 = sentences.get(1).get(CoreAnnotations.LineNumberAnnotation.class);
    assertEquals(Integer.valueOf(1), line1);
    assertEquals(Integer.valueOf(3), line2);
  }
@Test
  public void testTokenDocIDPropagationToSentenceAndTokens() {
    String text = "Token doc propagation test.";
    CoreLabel token0 = new CoreLabel();
    token0.set(CoreAnnotations.TextAnnotation.class, "Token");
    token0.set(CoreAnnotations.ValueAnnotation.class, "Token");
    token0.set(CoreAnnotations.OriginalTextAnnotation.class, "Token");
    token0.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token0.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    token0.set(CoreAnnotations.IsNewlineAnnotation.class, false);
    token0.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    token0.set(CoreAnnotations.TokenEndAnnotation.class, 0);

    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "doc");
    token1.set(CoreAnnotations.ValueAnnotation.class, "doc");
    token1.set(CoreAnnotations.OriginalTextAnnotation.class, "doc");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 6);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 9);
    token1.set(CoreAnnotations.IsNewlineAnnotation.class, false);
    token1.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.TokenEndAnnotation.class, 0);

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "propagation");
    token2.set(CoreAnnotations.ValueAnnotation.class, "propagation");
    token2.set(CoreAnnotations.OriginalTextAnnotation.class, "propagation");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 10);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 21);
    token2.set(CoreAnnotations.IsNewlineAnnotation.class, false);
    token2.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    token2.set(CoreAnnotations.TokenEndAnnotation.class, 0);

    CoreLabel token3 = new CoreLabel();
    token3.set(CoreAnnotations.TextAnnotation.class, "test");
    token3.set(CoreAnnotations.ValueAnnotation.class, "test");
    token3.set(CoreAnnotations.OriginalTextAnnotation.class, "test");
    token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 22);
    token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 26);
    token3.set(CoreAnnotations.IsNewlineAnnotation.class, false);
    token3.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    token3.set(CoreAnnotations.TokenEndAnnotation.class, 0);

    CoreLabel token4 = new CoreLabel();
    token4.set(CoreAnnotations.TextAnnotation.class, ".");
    token4.set(CoreAnnotations.ValueAnnotation.class, ".");
    token4.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
    token4.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 26);
    token4.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 27);
    token4.set(CoreAnnotations.IsNewlineAnnotation.class, false);
    token4.set(CoreAnnotations.TokenBeginAnnotation.class, 0);
    token4.set(CoreAnnotations.TokenEndAnnotation.class, 0);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token0);
    tokens.add(token1);
    tokens.add(token2);
    tokens.add(token3);
    tokens.add(token4);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.DocIDAnnotation.class, "docABC");

    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();
    annotator.annotate(annotation);

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertNotNull(sentences);
    assertEquals(1, sentences.size());

    CoreMap sentence = sentences.get(0);
    assertEquals("docABC", sentence.get(CoreAnnotations.DocIDAnnotation.class));

    CoreLabel tokenCheck = sentence.get(CoreAnnotations.TokensAnnotation.class).get(0);
    assertEquals("docABC", tokenCheck.docID());
  }
@Test
  public void testAnnotateReturnsEarlyIfSentencesAlreadyExist() {
    String text = "Sentence A. Sentence B.";

    CoreLabel tokenA = new CoreLabel();
    tokenA.set(CoreAnnotations.TextAnnotation.class, "Sentence");
    tokenA.set(CoreAnnotations.OriginalTextAnnotation.class, "Sentence");
    tokenA.set(CoreAnnotations.ValueAnnotation.class, "Sentence");
    tokenA.set(CoreAnnotations.IsNewlineAnnotation.class, false);
    tokenA.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    tokenA.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 8);

    CoreLabel tokenB = new CoreLabel();
    tokenB.set(CoreAnnotations.TextAnnotation.class, "A");
    tokenB.set(CoreAnnotations.OriginalTextAnnotation.class, "A");
    tokenB.set(CoreAnnotations.ValueAnnotation.class, "A");
    tokenB.set(CoreAnnotations.IsNewlineAnnotation.class, false);
    tokenB.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 9);
    tokenB.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 10);

    CoreLabel tokenPeriod1 = new CoreLabel();
    tokenPeriod1.set(CoreAnnotations.TextAnnotation.class, ".");
    tokenPeriod1.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
    tokenPeriod1.set(CoreAnnotations.ValueAnnotation.class, ".");
    tokenPeriod1.set(CoreAnnotations.IsNewlineAnnotation.class, false);
    tokenPeriod1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 10);
    tokenPeriod1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 11);

    List<CoreLabel> tokens = Arrays.asList(tokenA, tokenB, tokenPeriod1);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    List<CoreMap> existingSentences = new ArrayList<>();
    annotation.set(CoreAnnotations.SentencesAnnotation.class, existingSentences);

    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();

    annotator.annotate(annotation);

    assertEquals(existingSentences, annotation.get(CoreAnnotations.SentencesAnnotation.class));
  }
@Test
  public void testEmptySentenceListWithCountLineNumbersFalseThrowsException() {
    String text = "";

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);

    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.TextAnnotation.class, "");
    token.set(CoreAnnotations.OriginalTextAnnotation.class, "");
    token.set(CoreAnnotations.ValueAnnotation.class, "");
    token.set(CoreAnnotations.IsNewlineAnnotation.class, false);
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 0);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token);

    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Properties props = new Properties();
    props.setProperty("ssplit.boundaryTokenRegex", "");

    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator(props);

    try {
      annotator.annotate(annotation);
      fail("Expected IllegalStateException due to empty sentence");
    } catch (IllegalStateException e) {
      assertTrue(e.getMessage().contains("unexpected empty sentence"));
    }
  }
@Test
  public void testCustomDiscardTokenNotIncludedInSentence() {
    String text = "Hello [discard] world.";

    CoreLabel token0 = new CoreLabel();
    token0.set(CoreAnnotations.TextAnnotation.class, "Hello");
    token0.set(CoreAnnotations.OriginalTextAnnotation.class, "Hello");
    token0.set(CoreAnnotations.ValueAnnotation.class, "Hello");
    token0.set(CoreAnnotations.IsNewlineAnnotation.class, false);
    token0.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token0.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);

    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "[discard]");
    token1.set(CoreAnnotations.OriginalTextAnnotation.class, "[discard]");
    token1.set(CoreAnnotations.ValueAnnotation.class, "[discard]");
    token1.set(CoreAnnotations.IsNewlineAnnotation.class, false);
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 6);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 15);

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "world");
    token2.set(CoreAnnotations.OriginalTextAnnotation.class, "world");
    token2.set(CoreAnnotations.ValueAnnotation.class, "world");
    token2.set(CoreAnnotations.IsNewlineAnnotation.class, false);
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 16);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 21);

    CoreLabel token3 = new CoreLabel();
    token3.set(CoreAnnotations.TextAnnotation.class, ".");
    token3.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
    token3.set(CoreAnnotations.ValueAnnotation.class, ".");
    token3.set(CoreAnnotations.IsNewlineAnnotation.class, false);
    token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 21);
    token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 22);

    List<CoreLabel> tokens = Arrays.asList(token0, token1, token2, token3);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Properties props = new Properties();
    props.setProperty("ssplit.tokenPatternsToDiscard", "\\[discard\\]");
    props.setProperty("ssplit.boundaryTokenRegex", "\\.");

    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator(props);

    annotator.annotate(annotation);

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertNotNull(sentences);
    assertEquals(1, sentences.size());

    CoreMap sentence = sentences.get(0);
    String sentenceText = sentence.get(CoreAnnotations.TextAnnotation.class);

    assertEquals("Hello [discard] world.", sentenceText);
    List<CoreLabel> sentenceTokens = sentence.get(CoreAnnotations.TokensAnnotation.class);
    
    assertEquals(4, sentenceTokens.size());
  }
@Test
  public void testQuotedAnnotationFromSectionQuotes() {
    String text = "\"Spoken text here.\"";

    CoreLabel token0 = new CoreLabel();
    token0.set(CoreAnnotations.TextAnnotation.class, "\"");
    token0.set(CoreAnnotations.OriginalTextAnnotation.class, "\"");
    token0.set(CoreAnnotations.ValueAnnotation.class, "\"");
    token0.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token0.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 1);
    token0.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "Spoken");
    token1.set(CoreAnnotations.OriginalTextAnnotation.class, "Spoken");
    token1.set(CoreAnnotations.ValueAnnotation.class, "Spoken");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 1);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 7);
    token1.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "text");
    token2.set(CoreAnnotations.OriginalTextAnnotation.class, "text");
    token2.set(CoreAnnotations.ValueAnnotation.class, "text");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 8);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 12);
    token2.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token3 = new CoreLabel();
    token3.set(CoreAnnotations.TextAnnotation.class, "here");
    token3.set(CoreAnnotations.OriginalTextAnnotation.class, "here");
    token3.set(CoreAnnotations.ValueAnnotation.class, "here");
    token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 13);
    token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 17);
    token3.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token4 = new CoreLabel();
    token4.set(CoreAnnotations.TextAnnotation.class, ".");
    token4.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
    token4.set(CoreAnnotations.ValueAnnotation.class, ".");
    token4.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 17);
    token4.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 18);
    token4.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token5 = new CoreLabel();
    token5.set(CoreAnnotations.TextAnnotation.class, "\"");
    token5.set(CoreAnnotations.OriginalTextAnnotation.class, "\"");
    token5.set(CoreAnnotations.ValueAnnotation.class, "\"");
    token5.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 18);
    token5.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 19);
    token5.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    List<CoreLabel> tokens = Arrays.asList(token0, token1, token2, token3, token4, token5);

    CoreMap quote = new Annotation("quote");
    quote.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    quote.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 19);
    quote.set(CoreAnnotations.AuthorAnnotation.class, "QuotedSpeaker");

    CoreMap section = new Annotation("section");
    section.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    section.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 19);
    section.set(CoreAnnotations.QuotesAnnotation.class, Collections.singletonList(quote));
    section.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.SectionsAnnotation.class, Collections.singletonList(section));

    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();

    annotator.annotate(annotation);

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertNotNull(sentences);
    assertEquals(1, sentences.size());

    CoreMap sentence = sentences.get(0);
    assertTrue(sentence.get(CoreAnnotations.QuotedAnnotation.class));
    assertEquals("QuotedSpeaker", sentence.get(CoreAnnotations.AuthorAnnotation.class));
  }
@Test
  public void testSingleTokenSentence() {
    String text = "Hello.";

    CoreLabel token0 = new CoreLabel();
    token0.set(CoreAnnotations.TextAnnotation.class, "Hello");
    token0.set(CoreAnnotations.OriginalTextAnnotation.class, "Hello");
    token0.set(CoreAnnotations.ValueAnnotation.class, "Hello");
    token0.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token0.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    token0.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, ".");
    token1.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
    token1.set(CoreAnnotations.ValueAnnotation.class, ".");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);
    token1.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    List<CoreLabel> tokens = Arrays.asList(token0, token1);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();

    annotator.annotate(annotation);

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertEquals(1, sentences.size());
    CoreMap sentence = sentences.get(0);
    String str = sentence.get(CoreAnnotations.TextAnnotation.class);
    assertEquals("Hello.", str);
  }
@Test
  public void testNullOffsetsInTokenDoesNotThrow() {
    String text = "Test with null offsets.";

    CoreLabel token0 = new CoreLabel();
    token0.set(CoreAnnotations.TextAnnotation.class, "Test");
    token0.set(CoreAnnotations.OriginalTextAnnotation.class, "Test");
    token0.set(CoreAnnotations.ValueAnnotation.class, "Test");
    token0.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, null);
    token0.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, null);
    token0.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, ".");
    token1.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
    token1.set(CoreAnnotations.ValueAnnotation.class, ".");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 24);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 25);
    token1.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    List<CoreLabel> tokens = Arrays.asList(token0, token1);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    try {
      WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();
      annotator.annotate(annotation);

      List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
      assertNotNull(sentences);
    } catch (Exception e) {
      fail("Annotator should not throw on null offsets");
    }
  }
@Test
  public void testNewlineIsSentenceBreakFalseDoesNotSplit() {
    String text = "Line one\nLine two";

    CoreLabel token0 = new CoreLabel();
    token0.set(CoreAnnotations.TextAnnotation.class, "Line");
    token0.set(CoreAnnotations.OriginalTextAnnotation.class, "Line");
    token0.set(CoreAnnotations.ValueAnnotation.class, "Line");
    token0.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token0.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);
    token0.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "one");
    token1.set(CoreAnnotations.OriginalTextAnnotation.class, "one");
    token1.set(CoreAnnotations.ValueAnnotation.class, "one");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 8);
    token1.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "\n");
    token2.set(CoreAnnotations.OriginalTextAnnotation.class, "\n");
    token2.set(CoreAnnotations.ValueAnnotation.class, "\n");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 8);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 9);
    token2.set(CoreAnnotations.IsNewlineAnnotation.class, true);

    CoreLabel token3 = new CoreLabel();
    token3.set(CoreAnnotations.TextAnnotation.class, "Line");
    token3.set(CoreAnnotations.OriginalTextAnnotation.class, "Line");
    token3.set(CoreAnnotations.ValueAnnotation.class, "Line");
    token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 9);
    token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 13);
    token3.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token4 = new CoreLabel();
    token4.set(CoreAnnotations.TextAnnotation.class, "two");
    token4.set(CoreAnnotations.OriginalTextAnnotation.class, "two");
    token4.set(CoreAnnotations.ValueAnnotation.class, "two");
    token4.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 14);
    token4.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 17);
    token4.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    List<CoreLabel> tokens = Arrays.asList(token0, token1, token2, token3, token4);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Properties props = new Properties();
    props.setProperty("ssplit.newlineIsSentenceBreak", "false");
    props.setProperty("ssplit.boundaryTokenRegex", "\\.");

    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator(props);

    annotator.annotate(annotation);
    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertEquals(1, sentences.size());
  }
@Test
  public void testMultipleSectionMatchAssignsCorrectIndex() {
    String text = "Sentence in section 0. Another in section 1.";

    CoreLabel token0 = new CoreLabel();
    token0.set(CoreAnnotations.TextAnnotation.class, "Sentence");
    token0.set(CoreAnnotations.OriginalTextAnnotation.class, "Sentence");
    token0.set(CoreAnnotations.ValueAnnotation.class, "Sentence");
    token0.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token0.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 8);
    token0.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "in");
    token1.set(CoreAnnotations.OriginalTextAnnotation.class, "in");
    token1.set(CoreAnnotations.ValueAnnotation.class, "in");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 9);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 11);
    token1.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "section");
    token2.set(CoreAnnotations.OriginalTextAnnotation.class, "section");
    token2.set(CoreAnnotations.ValueAnnotation.class, "section");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 12);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 19);
    token2.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token3 = new CoreLabel();
    token3.set(CoreAnnotations.TextAnnotation.class, "0");
    token3.set(CoreAnnotations.OriginalTextAnnotation.class, "0");
    token3.set(CoreAnnotations.ValueAnnotation.class, "0");
    token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 20);
    token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 21);
    token3.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token4 = new CoreLabel();
    token4.set(CoreAnnotations.TextAnnotation.class, ".");
    token4.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
    token4.set(CoreAnnotations.ValueAnnotation.class, ".");
    token4.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 21);
    token4.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 22);
    token4.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token5 = new CoreLabel();
    token5.set(CoreAnnotations.TextAnnotation.class, "Another");
    token5.set(CoreAnnotations.OriginalTextAnnotation.class, "Another");
    token5.set(CoreAnnotations.ValueAnnotation.class, "Another");
    token5.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 23);
    token5.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 30);
    token5.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token6 = new CoreLabel();
    token6.set(CoreAnnotations.TextAnnotation.class, "in");
    token6.set(CoreAnnotations.OriginalTextAnnotation.class, "in");
    token6.set(CoreAnnotations.ValueAnnotation.class, "in");
    token6.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 31);
    token6.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 33);
    token6.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token7 = new CoreLabel();
    token7.set(CoreAnnotations.TextAnnotation.class, "section");
    token7.set(CoreAnnotations.OriginalTextAnnotation.class, "section");
    token7.set(CoreAnnotations.ValueAnnotation.class, "section");
    token7.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 34);
    token7.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 41);
    token7.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token8 = new CoreLabel();
    token8.set(CoreAnnotations.TextAnnotation.class, "1");
    token8.set(CoreAnnotations.OriginalTextAnnotation.class, "1");
    token8.set(CoreAnnotations.ValueAnnotation.class, "1");
    token8.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 42);
    token8.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 43);
    token8.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token9 = new CoreLabel();
    token9.set(CoreAnnotations.TextAnnotation.class, ".");
    token9.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
    token9.set(CoreAnnotations.ValueAnnotation.class, ".");
    token9.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 43);
    token9.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 44);
    token9.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    List<CoreLabel> tokens = Arrays.asList(token0, token1, token2, token3, token4, token5, token6, token7, token8, token9);

    CoreMap section0 = new Annotation("section0");
    section0.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    section0.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 22);
    section0.set(CoreAnnotations.QuotesAnnotation.class, new ArrayList<>());
    section0.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());
    section0.set(CoreAnnotations.SectionDateAnnotation.class, "2024-04-01");

    CoreMap section1 = new Annotation("section1");
    section1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 23);
    section1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 44);
    section1.set(CoreAnnotations.QuotesAnnotation.class, new ArrayList<>());
    section1.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());
    section1.set(CoreAnnotations.SectionDateAnnotation.class, "2024-05-01");

    List<CoreMap> sections = Arrays.asList(section0, section1);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.SectionsAnnotation.class, sections);

    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();
    annotator.annotate(annotation);

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertEquals(2, sentences.size());

    Integer sectionIndex0 = sentences.get(0).get(CoreAnnotations.SectionIndexAnnotation.class);
    Integer sectionIndex1 = sentences.get(1).get(CoreAnnotations.SectionIndexAnnotation.class);
    assertEquals(Integer.valueOf(0), sectionIndex0);
    assertEquals(Integer.valueOf(1), sectionIndex1);

    String date0 = sentences.get(0).get(CoreAnnotations.SectionDateAnnotation.class);
    String date1 = sentences.get(1).get(CoreAnnotations.SectionDateAnnotation.class);
    assertEquals("2024-04-01", date0);
    assertEquals("2024-05-01", date1);
  }
@Test
  public void testWithNoSectionAnnotationPresent() {
    String text = "No section.";

    CoreLabel token0 = new CoreLabel();
    token0.set(CoreAnnotations.TextAnnotation.class, "No");
    token0.set(CoreAnnotations.OriginalTextAnnotation.class, "No");
    token0.set(CoreAnnotations.ValueAnnotation.class, "No");
    token0.set(CoreAnnotations.IsNewlineAnnotation.class, false);
    token0.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token0.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 2);

    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "section");
    token1.set(CoreAnnotations.OriginalTextAnnotation.class, "section");
    token1.set(CoreAnnotations.ValueAnnotation.class, "section");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 3);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 10);
    token1.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, ".");
    token2.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
    token2.set(CoreAnnotations.ValueAnnotation.class, ".");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 10);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 11);
    token2.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    List<CoreLabel> tokens = Arrays.asList(token0, token1, token2);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    

    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();
    annotator.annotate(annotation);

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertEquals(1, sentences.size());
    CoreMap sentence = sentences.get(0);
    assertNull(sentence.get(CoreAnnotations.SectionIndexAnnotation.class));
    assertNull(sentence.get(CoreAnnotations.SectionDateAnnotation.class));
  }
@Test
  public void testQuoteNotMatchedExcludedFromSentenceAnnotation() {
    String text = "Nothing here is quoted.";

    CoreLabel token0 = new CoreLabel();
    token0.set(CoreAnnotations.TextAnnotation.class, "Nothing");
    token0.set(CoreAnnotations.OriginalTextAnnotation.class, "Nothing");
    token0.set(CoreAnnotations.ValueAnnotation.class, "Nothing");
    token0.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token0.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 7);

    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "here");
    token1.set(CoreAnnotations.OriginalTextAnnotation.class, "here");
    token1.set(CoreAnnotations.ValueAnnotation.class, "here");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 8);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 12);

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "is");
    token2.set(CoreAnnotations.OriginalTextAnnotation.class, "is");
    token2.set(CoreAnnotations.ValueAnnotation.class, "is");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 13);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 15);

    CoreLabel token3 = new CoreLabel();
    token3.set(CoreAnnotations.TextAnnotation.class, "quoted");
    token3.set(CoreAnnotations.OriginalTextAnnotation.class, "quoted");
    token3.set(CoreAnnotations.ValueAnnotation.class, "quoted");
    token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 16);
    token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 22);

    CoreLabel token4 = new CoreLabel();
    token4.set(CoreAnnotations.TextAnnotation.class, ".");
    token4.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
    token4.set(CoreAnnotations.ValueAnnotation.class, ".");
    token4.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 22);
    token4.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 23);

    List<CoreLabel> tokens = Arrays.asList(token0, token1, token2, token3, token4);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    CoreMap quote = new Annotation("quote1");
    quote.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 100); 
    quote.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 110);
    quote.set(CoreAnnotations.AuthorAnnotation.class, "Ghost");

    CoreMap section = new Annotation("section");
    section.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    section.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 23);
    section.set(CoreAnnotations.QuotesAnnotation.class, Collections.singletonList(quote));
    section.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());
    section.set(CoreAnnotations.SectionDateAnnotation.class, "2024-06-30");

    annotation.set(CoreAnnotations.SectionsAnnotation.class, Collections.singletonList(section));

    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();
    annotator.annotate(annotation);

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    CoreMap sentence = sentences.get(0);

    assertNull(sentence.get(CoreAnnotations.QuotedAnnotation.class));
    assertNull(sentence.get(CoreAnnotations.AuthorAnnotation.class));
  }
@Test
  public void testAfterAnnotationFixedByNewlineToken() {
    String text = "First\nSecond";

    CoreLabel token0 = new CoreLabel();
    token0.set(CoreAnnotations.TextAnnotation.class, "First");
    token0.set(CoreAnnotations.OriginalTextAnnotation.class, "First");
    token0.set(CoreAnnotations.ValueAnnotation.class, "First");
    token0.set(CoreAnnotations.IsNewlineAnnotation.class, false);
    token0.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token0.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    token0.set(CoreAnnotations.AfterAnnotation.class, " ");

    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "\n");
    token1.set(CoreAnnotations.OriginalTextAnnotation.class, "\n");
    token1.set(CoreAnnotations.ValueAnnotation.class, "\n");
    token1.set(CoreAnnotations.IsNewlineAnnotation.class, true);
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "Second");
    token2.set(CoreAnnotations.OriginalTextAnnotation.class, "Second");
    token2.set(CoreAnnotations.ValueAnnotation.class, "Second");
    token2.set(CoreAnnotations.IsNewlineAnnotation.class, false);
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 6);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 12);
    token2.set(CoreAnnotations.BeforeAnnotation.class, " ");

    List<CoreLabel> tokens = Arrays.asList(token0, token1, token2);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    WordsToSentencesAnnotator annotator = WordsToSentencesAnnotator.newlineSplitter("\n");
    annotator.annotate(annotation);

    List<CoreLabel> finalTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(2, finalTokens.size());

    CoreLabel secondToken = finalTokens.get(1);
    String newBefore = secondToken.get(CoreAnnotations.BeforeAnnotation.class);
    assertEquals("\n", newBefore);

    CoreLabel firstToken = finalTokens.get(0);
    String fixedAfter = firstToken.get(CoreAnnotations.AfterAnnotation.class);
    assertEquals("\n", fixedAfter);
  }
@Test
  public void testTokenBeginAndEndOverridesCorrectlySet() {
    String text = "X Y Z.";

    CoreLabel token0 = new CoreLabel();
    token0.set(CoreAnnotations.TextAnnotation.class, "X");
    token0.set(CoreAnnotations.OriginalTextAnnotation.class, "X");
    token0.set(CoreAnnotations.ValueAnnotation.class, "X");
    token0.set(CoreAnnotations.IsNewlineAnnotation.class, false);
    token0.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token0.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 1);

    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "Y");
    token1.set(CoreAnnotations.OriginalTextAnnotation.class, "Y");
    token1.set(CoreAnnotations.ValueAnnotation.class, "Y");
    token1.set(CoreAnnotations.IsNewlineAnnotation.class, false);
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 2);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 3);

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "Z");
    token2.set(CoreAnnotations.OriginalTextAnnotation.class, "Z");
    token2.set(CoreAnnotations.ValueAnnotation.class, "Z");
    token2.set(CoreAnnotations.IsNewlineAnnotation.class, false);
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 4);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);

    CoreLabel token3 = new CoreLabel();
    token3.set(CoreAnnotations.TextAnnotation.class, ".");
    token3.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
    token3.set(CoreAnnotations.ValueAnnotation.class, ".");
    token3.set(CoreAnnotations.IsNewlineAnnotation.class, false);
    token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
    token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);

    List<CoreLabel> tokens = Arrays.asList(token0, token1, token2, token3);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();
    annotator.annotate(annotation);

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    CoreMap sentence = sentences.get(0);
    int tokenBegin = sentence.get(CoreAnnotations.TokenBeginAnnotation.class);
    int tokenEnd = sentence.get(CoreAnnotations.TokenEndAnnotation.class);
    assertEquals(0, tokenBegin);
    assertEquals(3, tokenEnd); 
  }
@Test
  public void testTokenPatternsToDiscardEmptyButPropertySet() {
    String text = "Word1 Word2.";

    CoreLabel token0 = new CoreLabel();
    token0.set(CoreAnnotations.TextAnnotation.class, "Word1");
    token0.set(CoreAnnotations.OriginalTextAnnotation.class, "Word1");
    token0.set(CoreAnnotations.ValueAnnotation.class, "Word1");
    token0.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token0.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    token0.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "Word2");
    token1.set(CoreAnnotations.OriginalTextAnnotation.class, "Word2");
    token1.set(CoreAnnotations.ValueAnnotation.class, "Word2");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 6);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 11);
    token1.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, ".");
    token2.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
    token2.set(CoreAnnotations.ValueAnnotation.class, ".");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 11);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 12);
    token2.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    List<CoreLabel> tokens = Arrays.asList(token0, token1, token2);

    Properties props = new Properties();
    props.setProperty("ssplit.tokenPatternsToDiscard", "");  
    props.setProperty("ssplit.boundaryTokenRegex", "\\.");

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator(props);
    annotator.annotate(annotation);

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertNotNull(sentences);
    assertEquals(1, sentences.size());
  }
@Test
  public void testExplicitMultiTokenBoundaryPatternSplit() {
    String text = "Item A. Item B -- and more.";

    CoreLabel token0 = new CoreLabel();
    token0.set(CoreAnnotations.TextAnnotation.class, "Item");
    token0.set(CoreAnnotations.OriginalTextAnnotation.class, "Item");
    token0.set(CoreAnnotations.ValueAnnotation.class, "Item");
    token0.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token0.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);
    token0.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "A");
    token1.set(CoreAnnotations.OriginalTextAnnotation.class, "A");
    token1.set(CoreAnnotations.ValueAnnotation.class, "A");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);
    token1.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, ".");
    token2.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
    token2.set(CoreAnnotations.ValueAnnotation.class, ".");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 6);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 7);
    token2.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token3 = new CoreLabel();
    token3.set(CoreAnnotations.TextAnnotation.class, "Item");
    token3.set(CoreAnnotations.OriginalTextAnnotation.class, "Item");
    token3.set(CoreAnnotations.ValueAnnotation.class, "Item");
    token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 8);
    token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 12);
    token3.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token4 = new CoreLabel();
    token4.set(CoreAnnotations.TextAnnotation.class, "B");
    token4.set(CoreAnnotations.OriginalTextAnnotation.class, "B");
    token4.set(CoreAnnotations.ValueAnnotation.class, "B");
    token4.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 13);
    token4.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 14);
    token4.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token5 = new CoreLabel();
    token5.set(CoreAnnotations.TextAnnotation.class, "--");
    token5.set(CoreAnnotations.OriginalTextAnnotation.class, "--");
    token5.set(CoreAnnotations.ValueAnnotation.class, "--");
    token5.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 15);
    token5.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 17);
    token5.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token6 = new CoreLabel();
    token6.set(CoreAnnotations.TextAnnotation.class, "and");
    token6.set(CoreAnnotations.OriginalTextAnnotation.class, "and");
    token6.set(CoreAnnotations.ValueAnnotation.class, "and");
    token6.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 18);
    token6.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 21);
    token6.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token7 = new CoreLabel();
    token7.set(CoreAnnotations.TextAnnotation.class, "more");
    token7.set(CoreAnnotations.OriginalTextAnnotation.class, "more");
    token7.set(CoreAnnotations.ValueAnnotation.class, "more");
    token7.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 22);
    token7.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 26);
    token7.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token8 = new CoreLabel();
    token8.set(CoreAnnotations.TextAnnotation.class, ".");
    token8.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
    token8.set(CoreAnnotations.ValueAnnotation.class, ".");
    token8.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 26);
    token8.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 27);
    token8.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    List<CoreLabel> tokens = Arrays.asList(
      token0, token1, token2, token3, token4, token5, token6, token7, token8
    );

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Properties props = new Properties();
    props.setProperty("ssplit.boundaryTokenRegex", "\\.");
    props.setProperty("ssplit.boundaryMultiTokenRegex", "B\\s+--");

    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator(props);
    annotator.annotate(annotation);

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertNotNull(sentences);
    assertEquals(2, sentences.size());
  }
@Test
  public void testNullValueWhenNoCharacterOffsetsInSentenceTextExtraction() {
    String text = "X Y Z";

    CoreLabel token0 = new CoreLabel();
    token0.set(CoreAnnotations.TextAnnotation.class, "X");
    token0.set(CoreAnnotations.OriginalTextAnnotation.class, "X");
    token0.set(CoreAnnotations.ValueAnnotation.class, "X");
    token0.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, null);
    token0.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, null);
    token0.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "Y");
    token1.set(CoreAnnotations.OriginalTextAnnotation.class, "Y");
    token1.set(CoreAnnotations.ValueAnnotation.class, "Y");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, null);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, null);
    token1.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "Z");
    token2.set(CoreAnnotations.OriginalTextAnnotation.class, "Z");
    token2.set(CoreAnnotations.ValueAnnotation.class, "Z");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, null);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, null);
    token2.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    List<CoreLabel> tokens = Arrays.asList(token0, token1, token2);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    try {
      WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();
      annotator.annotate(annotation);
      List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
      assertNotNull(sentences);
    } catch (Exception e) {
      fail("Annotator must not throw even if offsets are null: " + e.getMessage());
    }
  }
@Test
  public void testRedundantCallToAnnotateDoesNotDuplicateSentences() {
    String text = "Repeat me.";

    CoreLabel token0 = new CoreLabel();
    token0.set(CoreAnnotations.TextAnnotation.class, "Repeat");
    token0.set(CoreAnnotations.OriginalTextAnnotation.class, "Repeat");
    token0.set(CoreAnnotations.ValueAnnotation.class, "Repeat");
    token0.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token0.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);
    token0.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "me");
    token1.set(CoreAnnotations.OriginalTextAnnotation.class, "me");
    token1.set(CoreAnnotations.ValueAnnotation.class, "me");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 7);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 9);
    token1.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, ".");
    token2.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
    token2.set(CoreAnnotations.ValueAnnotation.class, ".");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 9);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 10);
    token2.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    List<CoreLabel> tokens = Arrays.asList(token0, token1, token2);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();

    annotator.annotate(annotation);
    List<CoreMap> originalSentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertNotNull(originalSentences);
    assertEquals(1, originalSentences.size());

    annotator.annotate(annotation);  

    List<CoreMap> sentencesAfterSecondRun = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertSame(originalSentences, sentencesAfterSecondRun);
    assertEquals(1, sentencesAfterSecondRun.size());
  }
@Test
  public void testTokenWithEmptyStringIsProcessed() {
    String text = " . ";

    CoreLabel token0 = new CoreLabel();
    token0.set(CoreAnnotations.TextAnnotation.class, "");
    token0.set(CoreAnnotations.OriginalTextAnnotation.class, " ");
    token0.set(CoreAnnotations.ValueAnnotation.class, "");
    token0.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token0.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 1);
    token0.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, ".");
    token1.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
    token1.set(CoreAnnotations.ValueAnnotation.class, ".");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 1);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 2);
    token1.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "");
    token2.set(CoreAnnotations.OriginalTextAnnotation.class, " ");
    token2.set(CoreAnnotations.ValueAnnotation.class, "");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 2);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 3);
    token2.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    List<CoreLabel> tokens = Arrays.asList(token0, token1, token2);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();
    annotator.annotate(annotation);

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertNotNull(sentences);
    assertEquals(1, sentences.size());
  }
@Test
  public void testSectionStartAndSectionEndOnSameSentence() {
    String text = "Begin middle end.";

    CoreLabel token0 = new CoreLabel();
    token0.set(CoreAnnotations.TextAnnotation.class, "Begin");
    token0.set(CoreAnnotations.OriginalTextAnnotation.class, "Begin");
    token0.set(CoreAnnotations.ValueAnnotation.class, "Begin");
    token0.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token0.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    token0.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "middle");
    token1.set(CoreAnnotations.OriginalTextAnnotation.class, "middle");
    token1.set(CoreAnnotations.ValueAnnotation.class, "middle");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 6);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 12);
    token1.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "end");
    token2.set(CoreAnnotations.OriginalTextAnnotation.class, "end");
    token2.set(CoreAnnotations.ValueAnnotation.class, "end");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 13);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 16);
    token2.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token3 = new CoreLabel();
    token3.set(CoreAnnotations.TextAnnotation.class, ".");
    token3.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
    token3.set(CoreAnnotations.ValueAnnotation.class, ".");
    token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 16);
    token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 17);
    token3.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreMap sectionAnnotation = new Annotation("section meta");
    sectionAnnotation.set(CoreAnnotations.AuthorAnnotation.class, "SectionAuthor");
    sectionAnnotation.set(CoreAnnotations.SectionDateAnnotation.class, "2024-07-01");

    token0.set(CoreAnnotations.SectionStartAnnotation.class, sectionAnnotation);
    token3.set(CoreAnnotations.SectionEndAnnotation.class, "true");

    List<CoreLabel> tokens = Arrays.asList(token0, token1, token2, token3);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();
    annotator.annotate(annotation);

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertEquals(1, sentences.size());

    String author = sentences.get(0).get(CoreAnnotations.AuthorAnnotation.class);
    assertEquals("SectionAuthor", author);
  }
@Test
  public void testExistingAuthorAnnotationPreserved() {
    String text = "Some quote being reused.";

    CoreLabel token0 = new CoreLabel();
    token0.set(CoreAnnotations.TextAnnotation.class, "Some");
    token0.set(CoreAnnotations.OriginalTextAnnotation.class, "Some");
    token0.set(CoreAnnotations.ValueAnnotation.class, "Some");
    token0.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token0.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);
    token0.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "quote");
    token1.set(CoreAnnotations.OriginalTextAnnotation.class, "quote");
    token1.set(CoreAnnotations.ValueAnnotation.class, "quote");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 10);

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "being");
    token2.set(CoreAnnotations.OriginalTextAnnotation.class, "being");
    token2.set(CoreAnnotations.ValueAnnotation.class, "being");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 11);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 16);

    CoreLabel token3 = new CoreLabel();
    token3.set(CoreAnnotations.TextAnnotation.class, "reused");
    token3.set(CoreAnnotations.OriginalTextAnnotation.class, "reused");
    token3.set(CoreAnnotations.ValueAnnotation.class, "reused");
    token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 17);
    token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 23);

    CoreLabel token4 = new CoreLabel();
    token4.set(CoreAnnotations.TextAnnotation.class, ".");
    token4.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
    token4.set(CoreAnnotations.ValueAnnotation.class, ".");
    token4.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 23);
    token4.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 24);

    List<CoreLabel> tokens = Arrays.asList(token0, token1, token2, token3, token4);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    CoreMap sectionAnnotation = new Annotation("section");
    sectionAnnotation.set(CoreAnnotations.AuthorAnnotation.class, "NewAuthor");

    token0.set(CoreAnnotations.SectionStartAnnotation.class, sectionAnnotation);
    token4.set(CoreAnnotations.SectionEndAnnotation.class, "true");

    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();
    annotator.annotate(annotation);

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    CoreMap sentence = sentences.get(0);

    sentence.set(CoreAnnotations.AuthorAnnotation.class, "OldAuthor");

    assertEquals("OldAuthor", sentence.get(CoreAnnotations.AuthorAnnotation.class));
  }
@Test
  public void testTokenWithNullOriginalTextAllowed() {
    String text = "Alpha Beta";

    CoreLabel token0 = new CoreLabel();
    token0.set(CoreAnnotations.TextAnnotation.class, "Alpha");
    token0.set(CoreAnnotations.ValueAnnotation.class, "Alpha");
    token0.set(CoreAnnotations.OriginalTextAnnotation.class, null);
    token0.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token0.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    token0.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "Beta");
    token1.set(CoreAnnotations.ValueAnnotation.class, "Beta");
    token1.set(CoreAnnotations.OriginalTextAnnotation.class, null);
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 6);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 10);
    token1.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    List<CoreLabel> tokens = Arrays.asList(token0, token1);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();
    annotator.annotate(annotation);

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertNotNull(sentences);
  }
@Test
  public void testBeforeAnnotationPreservedIfAlreadySet() {
    String text = "A\nB";

    CoreLabel token0 = new CoreLabel();
    token0.set(CoreAnnotations.TextAnnotation.class, "A");
    token0.set(CoreAnnotations.ValueAnnotation.class, "A");
    token0.set(CoreAnnotations.OriginalTextAnnotation.class, "A");
    token0.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token0.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 1);
    token0.set(CoreAnnotations.BeforeAnnotation.class, ">>>");
    token0.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "\n");
    token1.set(CoreAnnotations.ValueAnnotation.class, "\n");
    token1.set(CoreAnnotations.OriginalTextAnnotation.class, "\n");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 1);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 2);
    token1.set(CoreAnnotations.IsNewlineAnnotation.class, true);

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "B");
    token2.set(CoreAnnotations.ValueAnnotation.class, "B");
    token2.set(CoreAnnotations.OriginalTextAnnotation.class, "B");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 2);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 3);
    token2.set(CoreAnnotations.BeforeAnnotation.class, "  ");
    token2.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    List<CoreLabel> tokens = Arrays.asList(token0, token1, token2);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    WordsToSentencesAnnotator annotator = WordsToSentencesAnnotator.newlineSplitter("\n");
    annotator.annotate(annotation);

    List<CoreLabel> finalTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(">>>", finalTokens.get(0).get(CoreAnnotations.BeforeAnnotation.class));
  }
@Test
  public void testEmptyTokenListReturnsNoSentences() {
    String text = "";

    List<CoreLabel> tokens = new ArrayList<>();

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();

    annotator.annotate(annotation);

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);

    assertNotNull(sentences);
    assertEquals(0, sentences.size());
  }
@Test
  public void testTokenMissingIsNewlineFlagHandledGracefully() {
    String text = "Hello world.";

    CoreLabel token0 = new CoreLabel();
    token0.set(CoreAnnotations.TextAnnotation.class, "Hello");
    token0.set(CoreAnnotations.OriginalTextAnnotation.class, "Hello");
    token0.set(CoreAnnotations.ValueAnnotation.class, "Hello");
    token0.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token0.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);

    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "world");
    token1.set(CoreAnnotations.OriginalTextAnnotation.class, "world");
    token1.set(CoreAnnotations.ValueAnnotation.class, "world");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 6);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 11);

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, ".");
    token2.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
    token2.set(CoreAnnotations.ValueAnnotation.class, ".");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 11);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 12);
    

    List<CoreLabel> tokens = Arrays.asList(token0, token1, token2);
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();
    annotator.annotate(annotation);

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertNotNull(sentences);
    assertEquals(1, sentences.size());
  }
@Test
  public void testTokenWithMissingOriginalText() {
    String text = "Missing original.";

    CoreLabel token0 = new CoreLabel();
    token0.set(CoreAnnotations.TextAnnotation.class, "Missing");
    token0.set(CoreAnnotations.ValueAnnotation.class, "Missing");
    token0.set(CoreAnnotations.OriginalTextAnnotation.class, null); 
    token0.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token0.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 7);
    token0.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "original");
    token1.set(CoreAnnotations.ValueAnnotation.class, "original");
    token1.set(CoreAnnotations.OriginalTextAnnotation.class, null); 
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 8);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 16);
    token1.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, ".");
    token2.set(CoreAnnotations.ValueAnnotation.class, ".");
    token2.set(CoreAnnotations.OriginalTextAnnotation.class, "."); 
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 16);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 17);
    token2.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    List<CoreLabel> tokens = Arrays.asList(token0, token1, token2);
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();
    annotator.annotate(annotation);

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertNotNull(sentences);
    assertEquals(1, sentences.size());
  }
@Test
  public void testMultipleNewlinesAsEmptySentences() {
    String text = "\n\nWord.";

    CoreLabel token0 = new CoreLabel();
    token0.set(CoreAnnotations.TextAnnotation.class, "\n");
    token0.set(CoreAnnotations.OriginalTextAnnotation.class, "\n");
    token0.set(CoreAnnotations.ValueAnnotation.class, "\n");
    token0.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token0.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 1);
    token0.set(CoreAnnotations.IsNewlineAnnotation.class, true);

    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "\n");
    token1.set(CoreAnnotations.OriginalTextAnnotation.class, "\n");
    token1.set(CoreAnnotations.ValueAnnotation.class, "\n");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 1);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 2);
    token1.set(CoreAnnotations.IsNewlineAnnotation.class, true);

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "Word");
    token2.set(CoreAnnotations.OriginalTextAnnotation.class, "Word");
    token2.set(CoreAnnotations.ValueAnnotation.class, "Word");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 2);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);
    token2.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token3 = new CoreLabel();
    token3.set(CoreAnnotations.TextAnnotation.class, ".");
    token3.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
    token3.set(CoreAnnotations.ValueAnnotation.class, ".");
    token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 6);
    token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 7);
    token3.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    List<CoreLabel> tokens = Arrays.asList(token0, token1, token2, token3);
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    WordsToSentencesAnnotator annotator = WordsToSentencesAnnotator.newlineSplitter("\n");
    annotator.annotate(annotation);

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertNotNull(sentences);
    assertEquals(1, sentences.size()); 
    Integer line = sentences.get(0).get(CoreAnnotations.LineNumberAnnotation.class);
    assertEquals(Integer.valueOf(3), line);
  }
@Test
  public void testSentenceOverlappingButOutsideAllSections() {
    String text = "Outside section range completely.";

    CoreLabel token0 = new CoreLabel();
    token0.set(CoreAnnotations.TextAnnotation.class, "Outside");
    token0.set(CoreAnnotations.OriginalTextAnnotation.class, "Outside");
    token0.set(CoreAnnotations.ValueAnnotation.class, "Outside");
    token0.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token0.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 7);
    token0.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "section");
    token1.set(CoreAnnotations.OriginalTextAnnotation.class, "section");
    token1.set(CoreAnnotations.ValueAnnotation.class, "section");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 8);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 15);
    token1.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "range");
    token2.set(CoreAnnotations.OriginalTextAnnotation.class, "range");
    token2.set(CoreAnnotations.ValueAnnotation.class, "range");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 16);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 21);
    token2.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token3 = new CoreLabel();
    token3.set(CoreAnnotations.TextAnnotation.class, "completely");
    token3.set(CoreAnnotations.OriginalTextAnnotation.class, "completely");
    token3.set(CoreAnnotations.ValueAnnotation.class, "completely");
    token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 22);
    token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 32);
    token3.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token4 = new CoreLabel();
    token4.set(CoreAnnotations.TextAnnotation.class, ".");
    token4.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
    token4.set(CoreAnnotations.ValueAnnotation.class, ".");
    token4.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 32);
    token4.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 33);
    token4.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    List<CoreLabel> tokens = Arrays.asList(token0, token1, token2, token3, token4);

    CoreMap section = new Annotation("section");
    section.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 40);
    section.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 50);
    section.set(CoreAnnotations.QuotesAnnotation.class, new ArrayList<>());
    section.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());
    section.set(CoreAnnotations.SectionDateAnnotation.class, "2024-09-01");

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.SectionsAnnotation.class, Arrays.asList(section));

    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();
    annotator.annotate(annotation);

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertNotNull(sentences);
    assertEquals(1, sentences.size());
    CoreMap sentence = sentences.get(0);
    assertNull(sentence.get(CoreAnnotations.SectionIndexAnnotation.class));  
  }
@Test
  public void testQuotedAnnotationWithoutAuthorHandledGracefully() {
    String text = "\"Only quote with no author.\"";

    CoreLabel token0 = new CoreLabel();
    token0.set(CoreAnnotations.TextAnnotation.class, "\"");
    token0.set(CoreAnnotations.OriginalTextAnnotation.class, "\"");
    token0.set(CoreAnnotations.ValueAnnotation.class, "\"");
    token0.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token0.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 1);

    CoreLabel token1 = new CoreLabel();
    token1.set(CoreAnnotations.TextAnnotation.class, "Only");
    token1.set(CoreAnnotations.OriginalTextAnnotation.class, "Only");
    token1.set(CoreAnnotations.ValueAnnotation.class, "Only");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 1);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);

    CoreLabel token2 = new CoreLabel();
    token2.set(CoreAnnotations.TextAnnotation.class, "quote");
    token2.set(CoreAnnotations.OriginalTextAnnotation.class, "quote");
    token2.set(CoreAnnotations.ValueAnnotation.class, "quote");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 6);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 11);

    CoreLabel token3 = new CoreLabel();
    token3.set(CoreAnnotations.TextAnnotation.class, "with");
    token3.set(CoreAnnotations.OriginalTextAnnotation.class, "with");
    token3.set(CoreAnnotations.ValueAnnotation.class, "with");
    token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 12);
    token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 16);

    CoreLabel token4 = new CoreLabel();
    token4.set(CoreAnnotations.TextAnnotation.class, "no");
    token4.set(CoreAnnotations.OriginalTextAnnotation.class, "no");
    token4.set(CoreAnnotations.ValueAnnotation.class, "no");
    token4.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 17);
    token4.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 19);

    CoreLabel token5 = new CoreLabel();
    token5.set(CoreAnnotations.TextAnnotation.class, "author");
    token5.set(CoreAnnotations.OriginalTextAnnotation.class, "author");
    token5.set(CoreAnnotations.ValueAnnotation.class, "author");
    token5.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 20);
    token5.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 26);

    CoreLabel token6 = new CoreLabel();
    token6.set(CoreAnnotations.TextAnnotation.class, ".");
    token6.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
    token6.set(CoreAnnotations.ValueAnnotation.class, ".");
    token6.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 26);
    token6.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 27);

    CoreLabel token7 = new CoreLabel();
    token7.set(CoreAnnotations.TextAnnotation.class, "\"");
    token7.set(CoreAnnotations.OriginalTextAnnotation.class, "\"");
    token7.set(CoreAnnotations.ValueAnnotation.class, "\"");
    token7.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 27);
    token7.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 28);

    List<CoreLabel> tokens = Arrays.asList(
        token0, token1, token2, token3, token4, token5, token6, token7
    );

    CoreMap quote = new Annotation("quote");
    quote.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    quote.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 28);
    

    CoreMap section = new Annotation("section");
    section.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    section.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 28);
    section.set(CoreAnnotations.QuotesAnnotation.class, Collections.singletonList(quote));
    section.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.SectionsAnnotation.class, Collections.singletonList(section));

    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();
    annotator.annotate(annotation);

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertTrue(sentences.get(0).get(CoreAnnotations.QuotedAnnotation.class));
    assertNull(sentences.get(0).get(CoreAnnotations.AuthorAnnotation.class)); 
  } 
}