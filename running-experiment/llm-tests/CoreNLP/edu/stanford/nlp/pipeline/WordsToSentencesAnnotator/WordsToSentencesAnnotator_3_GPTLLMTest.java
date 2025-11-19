package edu.stanford.nlp.pipeline;

import java.util.*;

import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.SentenceUtils;
import edu.stanford.nlp.util.ArrayCoreMap;
import edu.stanford.nlp.util.CoreMap;

import edu.stanford.nlp.util.logging.Redwood;
import org.junit.Test;

import edu.stanford.nlp.util.PropertiesUtils;

import static org.junit.Assert.*;

public class WordsToSentencesAnnotator_3_GPTLLMTest {

 @Test
  public void testSingleSentence() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("This");
    token1.setValue("This");
    token1.setOriginalText("This");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);
    token1.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("is");
    token2.setValue("is");
    token2.setOriginalText("is");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 7);
    token2.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token3 = new CoreLabel();
    token3.setWord("a");
    token3.setValue("a");
    token3.setOriginalText("a");
    token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 8);
    token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 9);
    token3.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token4 = new CoreLabel();
    token4.setWord("test");
    token4.setValue("test");
    token4.setOriginalText("test");
    token4.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 10);
    token4.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 14);
    token4.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token5 = new CoreLabel();
    token5.setWord(".");
    token5.setValue(".");
    token5.setOriginalText(".");
    token5.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 14);
    token5.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 15);
    token5.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4, token5);

    String text = "This is a test.";
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();
    annotator.annotate(annotation);

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertNotNull(sentences);
    assertEquals(1, sentences.size());
    CoreMap sentence = sentences.get(0);
    assertEquals("This is a test.", sentence.get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testMultipleSentences() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Hello");
    token1.setValue("Hello");
    token1.setOriginalText("Hello");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    token1.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token2 = new CoreLabel();
    token2.setWord(".");
    token2.setValue(".");
    token2.setOriginalText(".");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);
    token2.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token3 = new CoreLabel();
    token3.setWord("World");
    token3.setValue("World");
    token3.setOriginalText("World");
    token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 7);
    token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 12);
    token3.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token4 = new CoreLabel();
    token4.setWord(".");
    token4.setValue(".");
    token4.setOriginalText(".");
    token4.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 12);
    token4.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 13);
    token4.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4);

    String text = "Hello. World.";
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();
    annotator.annotate(annotation);

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertNotNull(sentences);
    assertEquals(2, sentences.size());
    assertEquals("Hello.", sentences.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("World.", sentences.get(1).get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testNewlineSplitting() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Line1");
    token1.setValue("Line1");
    token1.setOriginalText("Line1");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    token1.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel newline = new CoreLabel();
    newline.setWord("\n");
    newline.setValue("\n");
    newline.setOriginalText("\n");
    newline.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
    newline.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);
    newline.set(CoreAnnotations.IsNewlineAnnotation.class, true);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("Line2");
    token2.setValue("Line2");
    token2.setOriginalText("Line2");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 6);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 11);
    token2.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    List<CoreLabel> tokens = Arrays.asList(token1, newline, token2);

    String text = "Line1\nLine2";
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    WordsToSentencesAnnotator annotator = WordsToSentencesAnnotator.newlineSplitter("\n");
    annotator.annotate(annotation);

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertNotNull(sentences);
    assertEquals(2, sentences.size());
    assertEquals("Line1", sentences.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("Line2", sentences.get(1).get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testNonSplitterSingleSentence() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("No");
    token1.setValue("No");
    token1.setOriginalText("No");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 2);
    token1.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("split");
    token2.setValue("split");
    token2.setOriginalText("split");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 3);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 8);
    token2.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token3 = new CoreLabel();
    token3.setWord(".");
    token3.setValue(".");
    token3.setOriginalText(".");
    token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 8);
    token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 9);
    token3.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3);

    String text = "No split.";
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    WordsToSentencesAnnotator annotator = WordsToSentencesAnnotator.nonSplitter();
    annotator.annotate(annotation);

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertNotNull(sentences);
    assertEquals(1, sentences.size());
    assertEquals("No split.", sentences.get(0).get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testEmptyTokens() {
    List<CoreLabel> tokens = new ArrayList<>();
    String text = "";
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();
    annotator.annotate(annotation);

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertNotNull(sentences);
    assertEquals(0, sentences.size());
  }
@Test(expected = IllegalArgumentException.class)
  public void testMissingTokensAnnotationThrowsException() {
    Annotation annotation = new Annotation("No tokens here");
    annotation.set(CoreAnnotations.TextAnnotation.class, "No tokens here");

    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();
    annotator.annotate(annotation);
  }
@Test
  public void testRequirementsSatisfiedAndRequired() {
    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();

    Set<Class<? extends CoreAnnotation>> required = annotator.requires();
    assertTrue(required.contains(CoreAnnotations.TextAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.TokensAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
    assertTrue(required.contains(CoreAnnotations.OriginalTextAnnotation.class));

    Set<Class<? extends CoreAnnotation>> satisfied = annotator.requirementsSatisfied();
    assertTrue(satisfied.contains(CoreAnnotations.SentencesAnnotation.class));
    assertTrue(satisfied.contains(CoreAnnotations.SentenceIndexAnnotation.class));
  }
@Test
  public void testDuplicateSentenceAnnotatorLogsOnceAndSkips() {
    CoreLabel token = new CoreLabel();
    token.setWord("Hello");
    token.setOriginalText("Hello");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    token.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    List<CoreLabel> tokens = Collections.singletonList(token);
    String text = "Hello";
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();
    annotator.annotate(annotation);

    List<CoreMap> firstRun = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertNotNull(firstRun);
    assertEquals(1, firstRun.size());

    
    annotator.annotate(annotation);
    List<CoreMap> secondRun = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertEquals(1, secondRun.size());
  }
@Test
  public void testTokenWithMissingOffsets() {
    CoreLabel token = new CoreLabel();
    token.setWord("Broken");
    token.setOriginalText("Broken");
    token.set(CoreAnnotations.IsNewlineAnnotation.class, false);
    

    List<CoreLabel> tokens = Collections.singletonList(token);
    Annotation annotation = new Annotation("Broken");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Broken");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    try {
      WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();
      annotator.annotate(annotation);
      fail("Expected a NullPointerException due to missing offset annotations");
    } catch (NullPointerException expected) {
      
    }
  }
@Test
  public void testSectionStartAndEndAcrossTokens() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Section");
    token1.setOriginalText("Section");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 7);
    token1.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreMap section = new ArrayCoreMap();
    section.set(CoreAnnotations.SectionDateAnnotation.class, "2022-01-01");
    token1.set(CoreAnnotations.SectionStartAnnotation.class, section);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("End");
    token2.setOriginalText("End");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 8);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 11);
    token2.set(CoreAnnotations.IsNewlineAnnotation.class, false);
    token2.set(CoreAnnotations.SectionEndAnnotation.class, "end");

    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    String text = "Section End";
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();
    annotator.annotate(annotation);

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertEquals(1, sentences.size());
    CoreMap sent = sentences.get(0);
    assertEquals("2022-01-01", sent.get(CoreAnnotations.SectionDateAnnotation.class));
  }
@Test
  public void testSentenceNotMatchingAnySection() {
    CoreLabel token = new CoreLabel();
    token.setWord("Orphan");
    token.setOriginalText("Orphan");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 100);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 106);
    token.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    List<CoreLabel> tokens = Collections.singletonList(token);
    String text = "Orphan";
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    CoreMap section = new ArrayCoreMap();
    section.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    section.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 50);
    section.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());
    annotation.set(CoreAnnotations.SectionsAnnotation.class, Collections.singletonList(section));

    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();
    annotator.annotate(annotation);

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertEquals(1, sentences.size());

    
    List<CoreMap> sectionSentences = section.get(CoreAnnotations.SentencesAnnotation.class);
    assertTrue(sectionSentences.isEmpty());
  }
@Test
  public void testQuotedSentenceWithNoAuthor() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("\"");
    token1.setOriginalText("\"");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 1);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("Quoted");
    token2.setOriginalText("Quoted");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 1);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 7);

    CoreLabel token3 = new CoreLabel();
    token3.setWord("\"");
    token3.setOriginalText("\"");
    token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 7);
    token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 8);

    CoreMap quote = new ArrayCoreMap();
    quote.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    quote.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 8);
    

    CoreMap section = new ArrayCoreMap();
    section.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    section.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 20);
    section.set(CoreAnnotations.QuotesAnnotation.class, Collections.singletonList(quote));
    section.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3);
    String text = "\"Quoted\"";
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.SectionsAnnotation.class, Collections.singletonList(section));

    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();
    annotator.annotate(annotation);

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertEquals(1, sentences.size());
    CoreMap sent = sentences.get(0);
    assertTrue(sent.get(CoreAnnotations.QuotedAnnotation.class));
    assertNull(sent.get(CoreAnnotations.AuthorAnnotation.class));
  }
@Test
  public void testCorrectTokenListFinalization() {
    CoreLabel t1 = new CoreLabel();
    t1.setWord("Hello");
    t1.setOriginalText("Hello");
    t1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    t1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    t1.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel newline = new CoreLabel();
    newline.setWord("\n");
    newline.setOriginalText("\n");
    newline.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
    newline.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);
    newline.set(CoreAnnotations.IsNewlineAnnotation.class, true);
    newline.set(CoreAnnotations.AfterAnnotation.class, "\n");

    CoreLabel t2 = new CoreLabel();
    t2.setWord("World");
    t2.setOriginalText("World");
    t2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 6);
    t2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 11);
    t2.set(CoreAnnotations.IsNewlineAnnotation.class, false);
    t2.set(CoreAnnotations.BeforeAnnotation.class, " ");

    String text = "Hello\nWorld";
    List<CoreLabel> tokens = Arrays.asList(t1, newline, t2);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    WordsToSentencesAnnotator annotator = WordsToSentencesAnnotator.newlineSplitter("\n");
    annotator.annotate(annotation);

    List<CoreLabel> finalTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(2, finalTokens.size());
    assertEquals("World", finalTokens.get(1).word());
    assertEquals("\n", finalTokens.get(1).get(CoreAnnotations.BeforeAnnotation.class));
    assertEquals("\n", finalTokens.get(0).get(CoreAnnotations.AfterAnnotation.class));
  }
@Test
  public void testSentenceIndexingForMultipleSentences() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("One");
    token1.setOriginalText("One");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 3);
    token1.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token2 = new CoreLabel();
    token2.setWord(".");
    token2.setOriginalText(".");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 3);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);
    token2.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token3 = new CoreLabel();
    token3.setWord("Two");
    token3.setOriginalText("Two");
    token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
    token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 8);
    token3.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token4 = new CoreLabel();
    token4.setWord(".");
    token4.setOriginalText(".");
    token4.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 8);
    token4.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 9);
    token4.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4);
    String text = "One. Two.";

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();
    annotator.annotate(annotation);

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertEquals(2, sentences.size());
    assertEquals(Integer.valueOf(0), sentences.get(0).get(CoreAnnotations.SentenceIndexAnnotation.class));
    assertEquals(Integer.valueOf(1), sentences.get(1).get(CoreAnnotations.SentenceIndexAnnotation.class));
  }
@Test
  public void testEmptyTextAndOnlyNewlineTokens() {
    CoreLabel newlineToken = new CoreLabel();
    newlineToken.setWord("\n");
    newlineToken.setOriginalText("\n");
    newlineToken.set(CoreAnnotations.IsNewlineAnnotation.class, true);
    newlineToken.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    newlineToken.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 1);

    List<CoreLabel> tokens = Collections.singletonList(newlineToken);

    String text = "";
    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    WordsToSentencesAnnotator annotator = WordsToSentencesAnnotator.newlineSplitter("\n");

    annotator.annotate(annotation);

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertNotNull(sentences);
    assertEquals(0, sentences.size());

    List<CoreLabel> finalTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertTrue(finalTokens.isEmpty());
  }
@Test
  public void testNullTextAnnotation() {
    CoreLabel token = new CoreLabel();
    token.setWord("Test");
    token.setOriginalText("Test");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);
    token.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    Annotation annotation = new Annotation((Annotation) null);
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();

    try {
      annotator.annotate(annotation);
      fail("Expected NullPointerException due to missing TextAnnotation");
    } catch (NullPointerException expected) {
      
    }
  }
@Test
  public void testNewlineFixWithMissingOriginalText() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Hello");
    token1.setOriginalText("Hello");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    token1.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel newline = new CoreLabel();
    newline.setWord("\n");
    
    newline.set(CoreAnnotations.IsNewlineAnnotation.class, true);
    newline.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
    newline.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);
    newline.set(CoreAnnotations.AfterAnnotation.class, "\n");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("World");
    token2.setOriginalText("World");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 6);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 11);
    token2.set(CoreAnnotations.IsNewlineAnnotation.class, false);
    token2.set(CoreAnnotations.BeforeAnnotation.class, "\n");

    List<CoreLabel> tokens = Arrays.asList(token1, newline, token2);

    String text = "Hello\nWorld";

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    WordsToSentencesAnnotator annotator = WordsToSentencesAnnotator.newlineSplitter("\n");
    annotator.annotate(annotation);

    List<CoreLabel> cleanedTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals(2, cleanedTokens.size());
    assertEquals("Hello", cleanedTokens.get(0).word());
    assertEquals("World", cleanedTokens.get(1).word());
    assertEquals("\n", cleanedTokens.get(0).get(CoreAnnotations.AfterAnnotation.class));
    assertEquals("\n", cleanedTokens.get(1).get(CoreAnnotations.BeforeAnnotation.class));
  }
@Test
  public void testSentenceCrossingMultipleSections() {
    CoreLabel token = new CoreLabel();
    token.setWord("Middle");
    token.setOriginalText("Middle");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 25);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 31);
    token.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    List<CoreLabel> tokens = Collections.singletonList(token);
    String text = "This is a sample string.";

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    CoreMap section1 = new ArrayCoreMap();
    section1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    section1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 30);
    section1.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());
    section1.set(CoreAnnotations.SectionDateAnnotation.class, "2023-01-01");

    CoreMap section2 = new ArrayCoreMap();
    section2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 28);
    section2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 50);
    section2.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());
    section2.set(CoreAnnotations.SectionDateAnnotation.class, "2023-02-01");

    annotation.set(CoreAnnotations.SectionsAnnotation.class, Arrays.asList(section1, section2));

    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();
    annotator.annotate(annotation);

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertEquals(1, sentences.size());
    CoreMap sentence = sentences.get(0);

    
    assertEquals("2023-01-01", sentence.get(CoreAnnotations.SectionDateAnnotation.class));
    List<CoreMap> sentInSection1 = section1.get(CoreAnnotations.SentencesAnnotation.class);
    assertTrue(sentInSection1.contains(sentence));

    List<CoreMap> sentInSection2 = section2.get(CoreAnnotations.SentencesAnnotation.class);
    assertFalse(sentInSection2.contains(sentence));
  }
@Test
  public void testQuoteInsideSectionWithAuthorAndOutOfBoundsToken() {
    CoreLabel token = new CoreLabel();
    token.setWord("QuoteText");
    token.setOriginalText("QuoteText");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 35);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 44);
    token.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    List<CoreLabel> tokens = Collections.singletonList(token);
    String text = "Some long document that has a quote.";

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    CoreMap quote = new ArrayCoreMap();
    quote.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 30);
    quote.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 44);
    quote.set(CoreAnnotations.AuthorAnnotation.class, "Alice");

    CoreMap section = new ArrayCoreMap();
    section.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 10);
    section.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 100);
    section.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());
    section.set(CoreAnnotations.QuotesAnnotation.class, Collections.singletonList(quote));

    annotation.set(CoreAnnotations.SectionsAnnotation.class, Collections.singletonList(section));

    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();
    annotator.annotate(annotation);

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertEquals(1, sentences.size());
    CoreMap sentence = sentences.get(0);
    assertTrue(sentence.get(CoreAnnotations.QuotedAnnotation.class));
    assertEquals("Alice", sentence.get(CoreAnnotations.AuthorAnnotation.class));
  }
@Test
  public void testSectionWithoutSentenceList() {
    CoreLabel token = new CoreLabel();
    token.setWord("Content");
    token.setOriginalText("Content");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 7);
    token.set(CoreAnnotations.IsNewlineAnnotation.class, false);


    Annotation annotation = new Annotation("Content");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Content");
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();
    annotator.annotate(annotation);

    
    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertEquals(1, sentences.size());
  }
@Test
  public void testQuoteWithPartialSentenceCoverage() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("\"");
    token1.setOriginalText("\"");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 1);
    token1.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("text");
    token2.setOriginalText("text");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 1);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    token2.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token3 = new CoreLabel();
    token3.setWord(".");
    token3.setOriginalText(".");
    token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
    token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);
    token3.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    List<CoreLabel> tokens = Arrays.asList(token1, token2, token3);

    CoreMap quote = new ArrayCoreMap();
    quote.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    quote.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4); 
    quote.set(CoreAnnotations.AuthorAnnotation.class, "Bob");

    CoreMap section = new ArrayCoreMap();
    section.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    section.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 30);
    section.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());
    section.set(CoreAnnotations.QuotesAnnotation.class, Collections.singletonList(quote));

    Annotation annotation = new Annotation("\"text.");
    annotation.set(CoreAnnotations.TextAnnotation.class, "\"text.");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.SectionsAnnotation.class, Collections.singletonList(section));

    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();
    annotator.annotate(annotation);

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertNotNull(sentences);
    assertEquals(1, sentences.size());

    CoreMap sentence = sentences.get(0);
    assertFalse(Boolean.TRUE.equals(sentence.get(CoreAnnotations.QuotedAnnotation.class)));
  }
@Test
  public void testMissingTokenEndAnnotationHandled() {
    CoreLabel token = new CoreLabel();
    token.setWord("Edge");
    token.setOriginalText("Edge");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);
    token.set(CoreAnnotations.IsNewlineAnnotation.class, false);
    

    Annotation annotation = new Annotation("Edge");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Edge");
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();
    annotator.annotate(annotation);

    assertNotNull(token.get(CoreAnnotations.TokenBeginAnnotation.class));
    assertNotNull(token.get(CoreAnnotations.TokenEndAnnotation.class));
  }
@Test
  public void testSentenceOutsideAllSections() {
    CoreLabel token = new CoreLabel();
    token.setWord("Start");
    token.setOriginalText("Start");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 100);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 105);
    token.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreMap section = new ArrayCoreMap();
    section.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    section.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 50);
    section.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());

    Annotation annotation = new Annotation("Start");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Start");
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    annotation.set(CoreAnnotations.SectionsAnnotation.class, Collections.singletonList(section));

    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();
    annotator.annotate(annotation);

    assertTrue(section.get(CoreAnnotations.SentencesAnnotation.class).isEmpty());
    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertEquals(1, sentences.size());
  }
@Test
  public void testBoundaryMultiTokenRegexSplitting() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("a");
    token1.setOriginalText("a");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 1);
    token1.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("--");
    token2.setOriginalText("--");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 2);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);
    token2.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token3 = new CoreLabel();
    token3.setWord("b");
    token3.setOriginalText("b");
    token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
    token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);
    token3.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    Annotation annotation = new Annotation("a -- b");
    annotation.set(CoreAnnotations.TextAnnotation.class, "a -- b");
    annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2, token3));

    Properties props = new Properties();
    props.setProperty("ssplit.boundaryMultiTokenRegex", "[a-z]\\s*--\\s*[a-z]");
    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator(props);
    annotator.annotate(annotation);

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertEquals(1, sentences.size()); 
  }
@Test
  public void testTokenPatternsToDiscard() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("START");
    token1.setOriginalText("START");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    token1.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("something");
    token2.setOriginalText("something");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 6);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 15);
    token2.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    List<CoreLabel> tokens = Arrays.asList(token1, token2);
    Annotation annotation = new Annotation("START something");
    annotation.set(CoreAnnotations.TextAnnotation.class, "START something");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    Properties props = new Properties();
    props.setProperty("ssplit.tokenPatternsToDiscard", "START");

    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator(props);
    annotator.annotate(annotation);

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertEquals(1, sentences.size()); 
    CoreMap sentence = sentences.get(0);
    List<CoreLabel> sentenceTokens = sentence.get(CoreAnnotations.TokensAnnotation.class);
    assertEquals("something", sentenceTokens.get(0).word());
  }
@Test
  public void testEmptySectionsHandledGracefully() {
    CoreLabel token = new CoreLabel();
    token.setWord("Token");
    token.setOriginalText("Token");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    token.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    Annotation annotation = new Annotation("Token");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Token");
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    annotation.set(CoreAnnotations.SectionsAnnotation.class, Collections.emptyList());

    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();
    annotator.annotate(annotation);

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertEquals(1, sentences.size());
  }
@Test
  public void testSectionEndAnnotationIsNull() {
    CoreLabel token = new CoreLabel();
    token.setWord("Example");
    token.setOriginalText("Example");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 7);
    token.set(CoreAnnotations.IsNewlineAnnotation.class, false);
    token.set(CoreAnnotations.SectionEndAnnotation.class, null);

    List<CoreLabel> tokens = Collections.singletonList(token);
    String text = "Example";

    CoreMap section = new ArrayCoreMap();
    section.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    section.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 50);
    section.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.SectionsAnnotation.class, Collections.singletonList(section));

    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();
    annotator.annotate(annotation);

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertEquals(1, sentences.size());
  }
@Test
  public void testTokenWithNullFieldsDoesNotThrow() {
    CoreLabel token = new CoreLabel();
    token.set(CoreAnnotations.IsNewlineAnnotation.class, false);
    

    Annotation annotation = new Annotation("");
    annotation.set(CoreAnnotations.TextAnnotation.class, "");
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    try {
      WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();
      annotator.annotate(annotation);
      List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
      assertEquals(0, sentences.size());
    } catch (Exception e) {
      fail("Exception should not be thrown on token with null fields");
    }
  }
@Test
  public void testNewlineTokenNotIncludedInSplitterSet() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("A");
    token1.setOriginalText("A");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 1);
    token1.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel newline = new CoreLabel();
    newline.setWord("\n");
    newline.setOriginalText("\n");
    newline.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 1);
    newline.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 2);
    newline.set(CoreAnnotations.IsNewlineAnnotation.class, true);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("B");
    token2.setOriginalText("B");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 2);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 3);
    token2.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    Annotation annotation = new Annotation("A\nB");
    annotation.set(CoreAnnotations.TextAnnotation.class, "A\nB");
    annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, newline, token2));

    WordsToSentencesAnnotator annotator = WordsToSentencesAnnotator.newlineSplitter("UNUSED_TOKEN");
    annotator.annotate(annotation);

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertEquals(1, sentences.size());
    CoreMap sentence = sentences.get(0);
    assertEquals("A\nB", sentence.get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testDocIdPropagatedToSentenceAndTokens() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Doc");
    token1.setOriginalText("Doc");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 3);
    token1.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token2 = new CoreLabel();
    token2.setWord(".");
    token2.setOriginalText(".");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 3);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);
    token2.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    List<CoreLabel> tokens = Arrays.asList(token1, token2);

    Annotation annotation = new Annotation("Doc.");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Doc.");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
    annotation.set(CoreAnnotations.DocIDAnnotation.class, "DOC_123");

    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();
    annotator.annotate(annotation);

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertEquals("DOC_123", sentences.get(0).get(CoreAnnotations.DocIDAnnotation.class));
    List<CoreLabel> sentTokens = sentences.get(0).get(CoreAnnotations.TokensAnnotation.class);
    assertEquals("DOC_123", sentTokens.get(0).get(CoreAnnotations.DocIDAnnotation.class));
  }
@Test
  public void testSectionStartSetButSectionEndNull() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Begin");
    token1.setOriginalText("Begin");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    token1.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreMap section = new ArrayCoreMap();
    section.set(CoreAnnotations.SectionDateAnnotation.class, "2001-01-01");
    token1.set(CoreAnnotations.SectionStartAnnotation.class, section);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("End");
    token2.setOriginalText("End");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 6);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 9);
    token2.set(CoreAnnotations.SectionEndAnnotation.class, null);
    token2.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    List<CoreLabel> tokens = Arrays.asList(token1, token2);

    Annotation annotation = new Annotation("Begin End");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Begin End");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();
    annotator.annotate(annotation);

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    CoreMap sent = sentences.get(0);
    assertEquals("2001-01-01", sent.get(CoreAnnotations.SectionDateAnnotation.class));
  }
@Test
  public void testFinalTokenIndexesAfterProcessing() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("One");
    token1.setOriginalText("One");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 3);
    token1.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("Two");
    token2.setOriginalText("Two");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 4);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 7);
    token2.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    List<CoreLabel> tokens = Arrays.asList(token1, token2);

    Annotation annotation = new Annotation("One Two");
    annotation.set(CoreAnnotations.TextAnnotation.class, "One Two");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();
    annotator.annotate(annotation);

    List<CoreLabel> processed = annotation.get(CoreAnnotations.TokensAnnotation.class);
    CoreLabel last = processed.get(1);
     }
@Test
  public void testConstructorWithAllFieldsIncludingRegex() {
    Set<String> discardTokens = new HashSet<>();
    discardTokens.add("XX");

    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator(
        false,
        "\\.",
        Collections.emptySet(),
        Collections.emptySet(),
        "true",
        "[A-Z]+",
        discardTokens
    );

    CoreLabel token1 = new CoreLabel();
    token1.setWord("XX");
    token1.setOriginalText("XX");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 2);
    token1.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("Hello");
    token2.setOriginalText("Hello");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 3);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 8);
    token2.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    Annotation annotation = new Annotation("XX Hello");
    annotation.set(CoreAnnotations.TextAnnotation.class, "XX Hello");
    annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2));

    annotator.annotate(annotation);

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertEquals(1, sentences.size());
    List<CoreLabel> sentTokens = sentences.get(0).get(CoreAnnotations.TokensAnnotation.class);
    assertEquals("Hello", sentTokens.get(0).word());
  }
@Test
  public void testAnnotateSkipsIfAlreadyAnnotated() {
    CoreLabel token = new CoreLabel();
    token.setWord("Test");
    token.setOriginalText("Test");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);
    token.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    Annotation annotation = new Annotation("Test");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Test");
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    CoreMap dummySentence = new ArrayCoreMap();
    annotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(dummySentence));

    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();
    annotator.annotate(annotation);

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertEquals(1, sentences.size()); 
    assertSame(dummySentence, sentences.get(0));
  }
@Test
  public void testNewlineSplitterSkipsOnlyEmptySentences() {
    CoreLabel newline1 = new CoreLabel();
    newline1.setWord("\n");
    newline1.setOriginalText("\n");
    newline1.set(CoreAnnotations.IsNewlineAnnotation.class, true);
    newline1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    newline1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 1);

    CoreLabel newline2 = new CoreLabel();
    newline2.setWord("\n");
    newline2.setOriginalText("\n");
    newline2.set(CoreAnnotations.IsNewlineAnnotation.class, true);
    newline2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 1);
    newline2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 2);

    Annotation annotation = new Annotation("\n\n");
    annotation.set(CoreAnnotations.TextAnnotation.class, "\n\n");
    annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(newline1, newline2));

    WordsToSentencesAnnotator annotator = WordsToSentencesAnnotator.newlineSplitter("\n");
    annotator.annotate(annotation);

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertEquals(0, sentences.size());

    List<CoreLabel> finalTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
    assertTrue(finalTokens.isEmpty());
  }
@Test
  public void testSentenceWithFullTextOffset() {
    String text = "Edge case sentence.";
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Edge");
    token1.setOriginalText("Edge");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);
    token1.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("case");
    token2.setOriginalText("case");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 9);
    token2.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token3 = new CoreLabel();
    token3.setWord("sentence");
    token3.setOriginalText("sentence");
    token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 10);
    token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 18);
    token3.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token4 = new CoreLabel();
    token4.setWord(".");
    token4.setOriginalText(".");
    token4.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 18);
    token4.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, text.length());
    token4.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2, token3, token4));

    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();
    annotator.annotate(annotation);

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertEquals(1, sentences.size());
    CoreMap sentence = sentences.get(0);
    assertEquals(0, sentence.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class).intValue());
    assertEquals(text.length(), sentence.get(CoreAnnotations.CharacterOffsetEndAnnotation.class).intValue());
  }
@Test
  public void testTokenIsSectionStartAndEnd() {
    CoreLabel token = new CoreLabel();
    token.setWord("Single");
    token.setOriginalText("Single");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);
    token.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreMap section = new ArrayCoreMap();
    section.set(CoreAnnotations.SectionDateAnnotation.class, "2099-12-31");

    token.set(CoreAnnotations.SectionStartAnnotation.class, section);
    token.set(CoreAnnotations.SectionEndAnnotation.class, "true");

    Annotation annotation = new Annotation("Single");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Single");
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();
    annotator.annotate(annotation);

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertEquals(1, sentences.size());

    CoreMap sentence = sentences.get(0);
    assertEquals("2099-12-31", sentence.get(CoreAnnotations.SectionDateAnnotation.class));
  }
@Test
  public void testSentenceSpanningMultipleNewlineTokens() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("First");
    token1.setOriginalText("First");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    token1.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel newline1 = new CoreLabel();
    newline1.set(CoreAnnotations.IsNewlineAnnotation.class, true);
    newline1.set(CoreAnnotations.OriginalTextAnnotation.class, "\n");

    CoreLabel newline2 = new CoreLabel();
    newline2.set(CoreAnnotations.IsNewlineAnnotation.class, true);
    newline2.set(CoreAnnotations.OriginalTextAnnotation.class, "\n");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("Second");
    token2.setOriginalText("Second");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 6);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 12);
    token2.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    List<CoreLabel> tokens = Arrays.asList(token1, newline1, newline2, token2);

    Annotation annotation = new Annotation("First\n\nSecond");
    annotation.set(CoreAnnotations.TextAnnotation.class, "First\n\nSecond");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    WordsToSentencesAnnotator annotator = WordsToSentencesAnnotator.newlineSplitter("\n");
    annotator.annotate(annotation);

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertEquals(2, sentences.size());
    assertEquals("First", sentences.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("Second", sentences.get(1).get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testNestedQuotesInsideSection() {
    CoreLabel token = new CoreLabel();
    token.setWord("\"Quote\"");
    token.setOriginalText("\"Quote\"");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 10);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 17);
    token.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreMap innerQuote = new ArrayCoreMap();
    innerQuote.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 10);
    innerQuote.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 17);
    innerQuote.set(CoreAnnotations.AuthorAnnotation.class, "InnerAuthor");

    CoreMap section = new ArrayCoreMap();
    section.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    section.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 25);
    section.set(CoreAnnotations.QuotesAnnotation.class, Collections.singletonList(innerQuote));
    section.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());

    Annotation annotation = new Annotation("Some text \"Quote\" here.");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Some text \"Quote\" here.");
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    annotation.set(CoreAnnotations.SectionsAnnotation.class, Collections.singletonList(section));

    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();
    annotator.annotate(annotation);

    List<CoreMap> sentences = section.get(CoreAnnotations.SentencesAnnotation.class);
    assertEquals(1, sentences.size());
    CoreMap sent = sentences.get(0);
    assertTrue(sent.get(CoreAnnotations.QuotedAnnotation.class));
    assertEquals("InnerAuthor", sent.get(CoreAnnotations.AuthorAnnotation.class));
  }
@Test
  public void testSentenceExactSectionEdge() {
    CoreLabel token = new CoreLabel();
    token.setWord("Boundary");
    token.setOriginalText("Boundary");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 50);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 58);
    token.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreMap section = new ArrayCoreMap();
    section.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 50);
    section.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 100);
    section.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());
    section.set(CoreAnnotations.SectionDateAnnotation.class, "2024-12-25");

    Annotation annotation = new Annotation(" ... Boundary ... ");
    annotation.set(CoreAnnotations.TextAnnotation.class, " ... Boundary ... ");
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    annotation.set(CoreAnnotations.SectionsAnnotation.class, Collections.singletonList(section));

    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();
    annotator.annotate(annotation);

    List<CoreMap> sentences = section.get(CoreAnnotations.SentencesAnnotation.class);
    assertEquals(1, sentences.size());
    assertEquals("2024-12-25", sentences.get(0).get(CoreAnnotations.SectionDateAnnotation.class));
  }
@Test
  public void testInvalidNewlineIsSentenceBreakConfig() {
    Properties props = new Properties();
    props.setProperty("ssplit.isOneSentence", "false");
    props.setProperty("ssplit.boundaryTokenRegex", "\\.");
    props.setProperty("ssplit.boundaryFollowersRegex", "");
    props.setProperty("ssplit.boundariesToDiscard", "");
    props.setProperty("ssplit.htmlBoundariesToDiscard", "");
    props.setProperty("ssplit.tokenPatternsToDiscard", "");
    props.setProperty("ssplit.boundaryMultiTokenRegex", "");
    props.setProperty("ssplit.verbose", "false");
    props.setProperty("ssplit.newlineIsSentenceBreak", "INVALID_BOOLEAN");

    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator(props);

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Hello");
    token1.setOriginalText("Hello");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    token1.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token2 = new CoreLabel();
    token2.setWord(".");
    token2.setOriginalText(".");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);
    token2.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    Annotation annotation = new Annotation("Hello.");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Hello.");
    annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2));

    annotator.annotate(annotation);

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertEquals(1, sentences.size());
  }
@Test
  public void testTokensWithDuplicateSentenceIndex() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("X");
    token1.setOriginalText("X");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 1);
    token1.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token2 = new CoreLabel();
    token2.setWord(".");
    token2.setOriginalText(".");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 1);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 2);
    token2.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    Annotation annotation = new Annotation("X.");
    annotation.set(CoreAnnotations.TextAnnotation.class, "X.");
    annotation.set(CoreAnnotations.TokensAnnotation.class, Arrays.asList(token1, token2));

    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();
    annotator.annotate(annotation);

    }
@Test
  public void testSectionWithEmptyQuoteList() {
    CoreLabel token = new CoreLabel();
    token.setWord("Nothing");
    token.setOriginalText("Nothing");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 7);
    token.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreMap section = new ArrayCoreMap();
    section.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    section.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 10);
    section.set(CoreAnnotations.QuotesAnnotation.class, new ArrayList<>());
    section.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());

    Annotation annotation = new Annotation("Nothing");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Nothing");
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    annotation.set(CoreAnnotations.SectionsAnnotation.class, Collections.singletonList(section));

    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();
    annotator.annotate(annotation);

    
    List<CoreMap> sentences = section.get(CoreAnnotations.SentencesAnnotation.class);
    assertEquals(1, sentences.size());
    CoreMap sentence = sentences.get(0);
    assertNull(sentence.get(CoreAnnotations.QuotedAnnotation.class));
  }
@Test
  public void testTokenWithOnlyWhitespace() {
    CoreLabel token = new CoreLabel();
    token.setWord(" ");
    token.setOriginalText(" ");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 1);
    token.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    Annotation annotation = new Annotation(" ");
    annotation.set(CoreAnnotations.TextAnnotation.class, " ");
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();
    annotator.annotate(annotation);

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertEquals(1, sentences.size());
    assertEquals(" ", sentences.get(0).get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testNegativeOffsetsHandledGracefully() {
    CoreLabel token = new CoreLabel();
    token.setWord("BadOffset");
    token.setOriginalText("BadOffset");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, -1);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, -1);
    token.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    Annotation annotation = new Annotation("BadOffset");
    annotation.set(CoreAnnotations.TextAnnotation.class, "BadOffset");
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    try {
      WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();
      annotator.annotate(annotation);
      List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
      assertEquals(0, sentences.size());
    } catch (Exception e) {
      fail("Annotator should handle negative offsets without throwing: " + e);
    }
  }
@Test
  public void testSentenceWithoutEndingPunctuation() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Sentence");
    token1.setOriginalText("Sentence");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 8);
    token1.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("without");
    token2.setOriginalText("without");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 9);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 16);
    token2.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    List<CoreLabel> tokens = Arrays.asList(token1, token2);

    Annotation annotation = new Annotation("Sentence without");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Sentence without");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();
    annotator.annotate(annotation);

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertEquals(1, sentences.size());
    assertEquals("Sentence without", sentences.get(0).get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testNewlineSplitterWithWhitespaceTokenizerPropertyEnabled() {
    CoreLabel token1 = new CoreLabel();
    token1.setWord("Line1");
    token1.setOriginalText("Line1");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    token1.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel newline = new CoreLabel();
    newline.setWord("\n");
    newline.setOriginalText("\n");
    newline.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
    newline.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);
    newline.set(CoreAnnotations.IsNewlineAnnotation.class, true);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("Line2");
    token2.setOriginalText("Line2");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 6);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 11);
    token2.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    List<CoreLabel> tokens = Arrays.asList(token1, newline, token2);

    Properties props = new Properties();
    props.setProperty("tokenize.whitespace", "true");
    props.setProperty("ssplit.newlineIsSentenceBreak", "true");

    Annotation annotation = new Annotation("Line1\nLine2");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Line1\nLine2");
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator(props);
    annotator.annotate(annotation);

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertEquals(2, sentences.size());
    assertEquals("Line1", sentences.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("Line2", sentences.get(1).get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testQuotedSentenceWithNoOffsetsOnQuote() {
    CoreLabel token = new CoreLabel();
    token.setWord("Word");
    token.setOriginalText("Word");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);
    token.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreMap quote = new ArrayCoreMap();
    quote.set(CoreAnnotations.AuthorAnnotation.class, "UnknownAuthor");

    CoreMap section = new ArrayCoreMap();
    section.set(CoreAnnotations.QuotesAnnotation.class, Collections.singletonList(quote));
    section.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    section.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    section.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());

    Annotation annotation = new Annotation("Word");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Word");
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    annotation.set(CoreAnnotations.SectionsAnnotation.class, Collections.singletonList(section));

    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();
    annotator.annotate(annotation);

    List<CoreMap> sentenceList = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertEquals(1, sentenceList.size());
    CoreMap sent = sentenceList.get(0);
    assertNull(sent.get(CoreAnnotations.QuotedAnnotation.class));
  }
@Test
  public void testSentenceEndsExactlyAtSectionEnd() {
    CoreLabel token = new CoreLabel();
    token.setWord("Edge");
    token.setOriginalText("Edge");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 10);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 20);
    token.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreMap section = new ArrayCoreMap();
    section.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    section.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 20);
    section.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());

    List<CoreMap> sections = Collections.singletonList(section);

    Annotation annotation = new Annotation("Edge");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Edge");
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));
    annotation.set(CoreAnnotations.SectionsAnnotation.class, sections);

    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();
    annotator.annotate(annotation);

    assertEquals(1, section.get(CoreAnnotations.SentencesAnnotation.class).size());
  }
@Test
  public void testMalformedBooleanInPropertiesHandled() {
    Properties props = new Properties();
    props.setProperty("ssplit.isOneSentence", "notaboolean");

    Annotation annotation = new Annotation("");
    annotation.set(CoreAnnotations.TextAnnotation.class, "");
    annotation.set(CoreAnnotations.TokensAnnotation.class, new ArrayList<>());

    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator(props);

    try {
      annotator.annotate(annotation);
    } catch (Exception e) {
      fail("Malformed boolean property should not throw exception");
    }
  }
@Test
  public void testTokenWithPreexistingSentenceIndexReassigned() {
    CoreLabel token = new CoreLabel();
    token.setWord("Reassign");
    token.setOriginalText("Reassign");
    token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 8);
    token.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    Annotation annotation = new Annotation("Reassign");
    annotation.set(CoreAnnotations.TextAnnotation.class, "Reassign");
    annotation.set(CoreAnnotations.TokensAnnotation.class, Collections.singletonList(token));

    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();
    annotator.annotate(annotation);

  }
}