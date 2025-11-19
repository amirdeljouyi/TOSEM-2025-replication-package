package edu.stanford.nlp.pipeline;

import java.util.*;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.SentenceUtils;
import edu.stanford.nlp.util.CoreMap;

import org.junit.Test;

import edu.stanford.nlp.util.PropertiesUtils;

import static org.junit.Assert.*;

public class WordsToSentencesAnnotator_2_GPTLLMTest {

 @Test
  public void testDefaultAnnotatorSingleSentence() {
    String text = "This is a sentence.";

    CoreLabel token1 = new CoreLabel();
    token1.setWord("This");
    token1.setValue("This");
    token1.set(CoreAnnotations.TextAnnotation.class, "This");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);
    token1.set(CoreAnnotations.BeforeAnnotation.class, " ");
    token1.set(CoreAnnotations.OriginalTextAnnotation.class, "This");
    token1.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("is");
    token2.setValue("is");
    token2.set(CoreAnnotations.TextAnnotation.class, "is");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 7);
    token2.set(CoreAnnotations.BeforeAnnotation.class, " ");
    token2.set(CoreAnnotations.OriginalTextAnnotation.class, "is");
    token2.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token3 = new CoreLabel();
    token3.setWord("a");
    token3.setValue("a");
    token3.set(CoreAnnotations.TextAnnotation.class, "a");
    token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 8);
    token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 9);
    token3.set(CoreAnnotations.BeforeAnnotation.class, " ");
    token3.set(CoreAnnotations.OriginalTextAnnotation.class, "a");
    token3.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token4 = new CoreLabel();
    token4.setWord("sentence");
    token4.setValue("sentence");
    token4.set(CoreAnnotations.TextAnnotation.class, "sentence");
    token4.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 10);
    token4.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 18);
    token4.set(CoreAnnotations.BeforeAnnotation.class, " ");
    token4.set(CoreAnnotations.OriginalTextAnnotation.class, "sentence");
    token4.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel token5 = new CoreLabel();
    token5.setWord(".");
    token5.setValue(".");
    token5.set(CoreAnnotations.TextAnnotation.class, ".");
    token5.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 18);
    token5.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 19);
    token5.set(CoreAnnotations.BeforeAnnotation.class, "");
    token5.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
    token5.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token1);
    tokens.add(token2);
    tokens.add(token3);
    tokens.add(token4);
    tokens.add(token5);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();
    annotator.annotate(annotation);

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);

    assertNotNull(sentences);
    assertEquals(1, sentences.size());
    assertEquals("This is a sentence.", sentences.get(0).get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testNewlineSplitterSplitsTwoSentences() {
    String text = "Hello\nWorld";

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Hello");
    token1.setValue("Hello");
    token1.set(CoreAnnotations.TextAnnotation.class, "Hello");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    token1.set(CoreAnnotations.BeforeAnnotation.class, "");
    token1.set(CoreAnnotations.OriginalTextAnnotation.class, "Hello");
    token1.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    CoreLabel tokenNewline = new CoreLabel();
    tokenNewline.setWord("\n");
    tokenNewline.setValue("\n");
    tokenNewline.set(CoreAnnotations.TextAnnotation.class, "\n");
    tokenNewline.set(CoreAnnotations.OriginalTextAnnotation.class, "\n");
    tokenNewline.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
    tokenNewline.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);
    tokenNewline.set(CoreAnnotations.IsNewlineAnnotation.class, true);

    CoreLabel token2 = new CoreLabel();
    token2.setWord("World");
    token2.setValue("World");
    token2.set(CoreAnnotations.TextAnnotation.class, "World");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 6);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 11);
    token2.set(CoreAnnotations.BeforeAnnotation.class, "");
    token2.set(CoreAnnotations.OriginalTextAnnotation.class, "World");
    token2.set(CoreAnnotations.IsNewlineAnnotation.class, false);

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token1);
    tokens.add(tokenNewline);
    tokens.add(token2);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    WordsToSentencesAnnotator annotator = WordsToSentencesAnnotator.newlineSplitter("\n");
    annotator.annotate(annotation);

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);

    assertNotNull(sentences);
    assertEquals(2, sentences.size());
    assertEquals("Hello", sentences.get(0).get(CoreAnnotations.TextAnnotation.class));
    assertEquals("World", sentences.get(1).get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testNonSplitterGivesSingleSentence() {
    String text = "One. Two.";

    CoreLabel token1 = new CoreLabel();
    token1.setWord("One");
    token1.setValue("One");
    token1.set(CoreAnnotations.TextAnnotation.class, "One");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 3);
    token1.set(CoreAnnotations.IsNewlineAnnotation.class, false);
    token1.set(CoreAnnotations.OriginalTextAnnotation.class, "One");

    CoreLabel token2 = new CoreLabel();
    token2.setWord(".");
    token2.setValue(".");
    token2.set(CoreAnnotations.TextAnnotation.class, ".");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 3);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);
    token2.set(CoreAnnotations.IsNewlineAnnotation.class, false);
    token2.set(CoreAnnotations.OriginalTextAnnotation.class, ".");

    CoreLabel token3 = new CoreLabel();
    token3.setWord("Two");
    token3.setValue("Two");
    token3.set(CoreAnnotations.TextAnnotation.class, "Two");
    token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
    token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 8);
    token3.set(CoreAnnotations.IsNewlineAnnotation.class, false);
    token3.set(CoreAnnotations.OriginalTextAnnotation.class, "Two");

    CoreLabel token4 = new CoreLabel();
    token4.setWord(".");
    token4.setValue(".");
    token4.set(CoreAnnotations.TextAnnotation.class, ".");
    token4.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 8);
    token4.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 9);
    token4.set(CoreAnnotations.IsNewlineAnnotation.class, false);
    token4.set(CoreAnnotations.OriginalTextAnnotation.class, ".");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token1);
    tokens.add(token2);
    tokens.add(token3);
    tokens.add(token4);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    WordsToSentencesAnnotator annotator = WordsToSentencesAnnotator.nonSplitter();
    annotator.annotate(annotation);

    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertNotNull(sentences);
    assertEquals(1, sentences.size());
    assertEquals("One. Two.", sentences.get(0).get(CoreAnnotations.TextAnnotation.class));
  }
@Test
  public void testMissingTokensAnnotationThrowsException() {
    Annotation annotation = new Annotation("Test text but no tokens");
    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();

    try {
      annotator.annotate(annotation);
      fail("Expected IllegalArgumentException due to missing TokensAnnotation");
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().contains("unable to find words/tokens"));
    }
  }
@Test
  public void testRedundantAnnotateCallDoesNotOverrideExistingSentences() {
    String text = "Hello world.";

    CoreLabel token1 = new CoreLabel();
    token1.setWord("Hello");
    token1.setValue("Hello");
    token1.set(CoreAnnotations.TextAnnotation.class, "Hello");
    token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
    token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
    token1.set(CoreAnnotations.IsNewlineAnnotation.class, false);
    token1.set(CoreAnnotations.OriginalTextAnnotation.class, "Hello");

    CoreLabel token2 = new CoreLabel();
    token2.setWord("world");
    token2.setValue("world");
    token2.set(CoreAnnotations.TextAnnotation.class, "world");
    token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 6);
    token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 11);
    token2.set(CoreAnnotations.IsNewlineAnnotation.class, false);
    token2.set(CoreAnnotations.OriginalTextAnnotation.class, "world");

    CoreLabel token3 = new CoreLabel();
    token3.setWord(".");
    token3.setValue(".");
    token3.set(CoreAnnotations.TextAnnotation.class, ".");
    token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 11);
    token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 12);
    token3.set(CoreAnnotations.IsNewlineAnnotation.class, false);
    token3.set(CoreAnnotations.OriginalTextAnnotation.class, ".");

    List<CoreLabel> tokens = new ArrayList<>();
    tokens.add(token1);
    tokens.add(token2);
    tokens.add(token3);

    Annotation annotation = new Annotation(text);
    annotation.set(CoreAnnotations.TextAnnotation.class, text);
    annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();
    annotator.annotate(annotation);

    List<CoreMap> firstCallSentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    assertNotNull(firstCallSentences);

    annotator.annotate(annotation);
    List<CoreMap> secondCallSentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);

    assertSame(firstCallSentences, secondCallSentences);
    assertEquals(1, secondCallSentences.size());
  }
@Test
public void testEmptyTokenListWithIsOneSentence() {
  String text = "";

  List<CoreLabel> tokens = new ArrayList<>();

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

  Properties props = new Properties();
  props.setProperty("ssplit.isOneSentence", "true");

  WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator(props);

  annotator.annotate(annotation);
  List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
  assertNotNull(sentences);
  assertEquals(0, sentences.size());
}
@Test
public void testBoundaryFollowersAndDiscardedTokensConfig() {
  String text = "This is a test.";

  CoreLabel token1 = new CoreLabel();
  token1.setWord("This");
  token1.setValue("This");
  token1.set(CoreAnnotations.TextAnnotation.class, "This");
  token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);
  token1.set(CoreAnnotations.IsNewlineAnnotation.class, false);
  token1.set(CoreAnnotations.OriginalTextAnnotation.class, "This");

  CoreLabel token2 = new CoreLabel();
  token2.setWord("is");
  token2.setValue("is");
  token2.set(CoreAnnotations.TextAnnotation.class, "is");
  token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
  token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 7);
  token2.set(CoreAnnotations.IsNewlineAnnotation.class, false);
  token2.set(CoreAnnotations.OriginalTextAnnotation.class, "is");

  CoreLabel token3 = new CoreLabel();
  token3.setWord("a");
  token3.setValue("a");
  token3.set(CoreAnnotations.TextAnnotation.class, "a");
  token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 8);
  token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 9);
  token3.set(CoreAnnotations.IsNewlineAnnotation.class, false);
  token3.set(CoreAnnotations.OriginalTextAnnotation.class, "a");

  CoreLabel token4 = new CoreLabel();
  token4.setWord("test");
  token4.setValue("test");
  token4.set(CoreAnnotations.TextAnnotation.class, "test");
  token4.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 10);
  token4.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 14);
  token4.set(CoreAnnotations.IsNewlineAnnotation.class, false);
  token4.set(CoreAnnotations.OriginalTextAnnotation.class, "test");

  CoreLabel token5 = new CoreLabel();
  token5.setWord(".");
  token5.setValue(".");
  token5.set(CoreAnnotations.TextAnnotation.class, ".");
  token5.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 14);
  token5.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 15);
  token5.set(CoreAnnotations.IsNewlineAnnotation.class, false);
  token5.set(CoreAnnotations.OriginalTextAnnotation.class, ".");

  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(token1);
  tokens.add(token2);
  tokens.add(token3);
  tokens.add(token4);
  tokens.add(token5);

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

  Properties props = new Properties();
  props.setProperty("ssplit.boundaryTokenRegex", "[.]");
  props.setProperty("ssplit.boundaryFollowersRegex", "[\"]");
  props.setProperty("ssplit.tokenPatternsToDiscard", "[\"]");
  props.setProperty("ssplit.verbose", "false");

  WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator(props);
  annotator.annotate(annotation);

  List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
  assertNotNull(sentences);
  assertEquals(1, sentences.size());
  assertEquals("This is a test.", sentences.get(0).get(CoreAnnotations.TextAnnotation.class));
}
@Test
public void testEmptyLinesSkippedWhenCountLineNumbersTrue() {
  String text = "First\n\nSecond";

  CoreLabel token1 = new CoreLabel();
  token1.setWord("First");
  token1.set(CoreAnnotations.TextAnnotation.class, "First");
  token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
  token1.set(CoreAnnotations.OriginalTextAnnotation.class, "First");
  token1.set(CoreAnnotations.IsNewlineAnnotation.class, false);

  CoreLabel newline1 = new CoreLabel();
  newline1.setWord("\n");
  newline1.set(CoreAnnotations.TextAnnotation.class, "\n");
  newline1.set(CoreAnnotations.OriginalTextAnnotation.class, "\n");
  newline1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
  newline1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);
  newline1.set(CoreAnnotations.IsNewlineAnnotation.class, true);

  CoreLabel newline2 = new CoreLabel();
  newline2.setWord("\n");
  newline2.set(CoreAnnotations.TextAnnotation.class, "\n");
  newline2.set(CoreAnnotations.OriginalTextAnnotation.class, "\n");
  newline2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 6);
  newline2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 7);
  newline2.set(CoreAnnotations.IsNewlineAnnotation.class, true);

  CoreLabel token2 = new CoreLabel();
  token2.setWord("Second");
  token2.set(CoreAnnotations.TextAnnotation.class, "Second");
  token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 7);
  token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 13);
  token2.set(CoreAnnotations.OriginalTextAnnotation.class, "Second");
  token2.set(CoreAnnotations.IsNewlineAnnotation.class, false);

  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(token1);
  tokens.add(newline1);
  tokens.add(newline2);
  tokens.add(token2);

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

  WordsToSentencesAnnotator annotator = WordsToSentencesAnnotator.newlineSplitter("\n");
  annotator.annotate(annotation);

  List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);

  assertNotNull(sentences);
  assertEquals(2, sentences.size());
  assertEquals(1, (int) sentences.get(0).get(CoreAnnotations.LineNumberAnnotation.class));
  assertEquals(3, (int) sentences.get(1).get(CoreAnnotations.LineNumberAnnotation.class));
}
@Test
public void testSentenceFallsOutsideSection() {
  String text = "Intro Body";

  CoreLabel token1 = new CoreLabel();
  token1.setWord("Intro");
  token1.set(CoreAnnotations.TextAnnotation.class, "Intro");
  token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
  token1.set(CoreAnnotations.IsNewlineAnnotation.class, false);
  token1.set(CoreAnnotations.OriginalTextAnnotation.class, "Intro");

  CoreLabel token2 = new CoreLabel();
  token2.setWord("Body");
  token2.set(CoreAnnotations.TextAnnotation.class, "Body");
  token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 6);
  token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 10);
  token2.set(CoreAnnotations.IsNewlineAnnotation.class, false);
  token2.set(CoreAnnotations.OriginalTextAnnotation.class, "Body");

  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(token1);
  tokens.add(token2);

  CoreMap section = new Annotation("Foo Section");
  section.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 12);  
  section.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 20);
  section.set(CoreAnnotations.QuotesAnnotation.class, new ArrayList<>());
  section.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());

  List<CoreMap> sections = new ArrayList<>();
  sections.add(section);

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
  annotation.set(CoreAnnotations.SectionsAnnotation.class, sections);

  WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();
  annotator.annotate(annotation);

  List<CoreMap> resultSections = annotation.get(CoreAnnotations.SectionsAnnotation.class);
  assertEquals(0, resultSections.get(0).get(CoreAnnotations.SentencesAnnotation.class).size());
}
@Test
public void testQuotedSentenceWithAuthor() {
  String text = "\"Hello world\" said John.";

  CoreLabel token1 = new CoreLabel();
  token1.setWord("\"");
  token1.set(CoreAnnotations.TextAnnotation.class, "\"");
  token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 1);
  token1.set(CoreAnnotations.IsNewlineAnnotation.class, false);
  token1.set(CoreAnnotations.OriginalTextAnnotation.class, "\"");

  CoreLabel token2 = new CoreLabel();
  token2.setWord("Hello");
  token2.set(CoreAnnotations.TextAnnotation.class, "Hello");
  token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 1);
  token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);
  token2.set(CoreAnnotations.IsNewlineAnnotation.class, false);
  token2.set(CoreAnnotations.OriginalTextAnnotation.class, "Hello");

  CoreLabel token3 = new CoreLabel();
  token3.setWord("world");
  token3.set(CoreAnnotations.TextAnnotation.class, "world");
  token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 7);
  token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 12);
  token3.set(CoreAnnotations.IsNewlineAnnotation.class, false);
  token3.set(CoreAnnotations.OriginalTextAnnotation.class, "world");

  CoreLabel token4 = new CoreLabel();
  token4.setWord("\"");
  token4.set(CoreAnnotations.TextAnnotation.class, "\"");
  token4.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 12);
  token4.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 13);
  token4.set(CoreAnnotations.IsNewlineAnnotation.class, false);
  token4.set(CoreAnnotations.OriginalTextAnnotation.class, "\"");

  CoreLabel token5 = new CoreLabel();
  token5.setWord("said");
  token5.set(CoreAnnotations.TextAnnotation.class, "said");
  token5.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 14);
  token5.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 18);
  token5.set(CoreAnnotations.IsNewlineAnnotation.class, false);
  token5.set(CoreAnnotations.OriginalTextAnnotation.class, "said");

  CoreLabel token6 = new CoreLabel();
  token6.setWord("John");
  token6.set(CoreAnnotations.TextAnnotation.class, "John");
  token6.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 19);
  token6.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 23);
  token6.set(CoreAnnotations.IsNewlineAnnotation.class, false);
  token6.set(CoreAnnotations.OriginalTextAnnotation.class, "John");

  CoreLabel token7 = new CoreLabel();
  token7.setWord(".");
  token7.set(CoreAnnotations.TextAnnotation.class, ".");
  token7.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 23);
  token7.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 24);
  token7.set(CoreAnnotations.IsNewlineAnnotation.class, false);
  token7.set(CoreAnnotations.OriginalTextAnnotation.class, ".");

  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(token1);
  tokens.add(token2);
  tokens.add(token3);
  tokens.add(token4);
  tokens.add(token5);
  tokens.add(token6);
  tokens.add(token7);

  CoreMap quote = new Annotation("\"Hello world\"");
  quote.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  quote.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 13);
  quote.set(CoreAnnotations.AuthorAnnotation.class, "John");

  CoreMap section = new Annotation("Quote Section");
  section.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  section.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 24);
  section.set(CoreAnnotations.QuotesAnnotation.class, Collections.singletonList(quote));
  section.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());

  List<CoreMap> sections = Collections.singletonList(section);

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
  annotation.set(CoreAnnotations.SectionsAnnotation.class, sections);

  WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();
  annotator.annotate(annotation);

  List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
  CoreMap sentence = sentences.get(0);

  assertTrue(sentence.get(CoreAnnotations.QuotedAnnotation.class));
  assertEquals("John", sentence.get(CoreAnnotations.AuthorAnnotation.class));
}
@Test
public void testAnnotationWithExistingSentencesSkipped() {
  String text = "Already split annotation.";

  CoreLabel token1 = new CoreLabel();
  token1.setWord("Already");
  token1.set(CoreAnnotations.TextAnnotation.class, "Already");
  token1.set(CoreAnnotations.OriginalTextAnnotation.class, "Already");
  token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 7);
  token1.set(CoreAnnotations.IsNewlineAnnotation.class, false);

  CoreLabel token2 = new CoreLabel();
  token2.setWord("split");
  token2.set(CoreAnnotations.TextAnnotation.class, "split");
  token2.set(CoreAnnotations.OriginalTextAnnotation.class, "split");
  token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 8);
  token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 13);
  token2.set(CoreAnnotations.IsNewlineAnnotation.class, false);

  CoreLabel token3 = new CoreLabel();
  token3.setWord("annotation");
  token3.set(CoreAnnotations.TextAnnotation.class, "annotation");
  token3.set(CoreAnnotations.OriginalTextAnnotation.class, "annotation");
  token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 14);
  token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 24);
  token3.set(CoreAnnotations.IsNewlineAnnotation.class, false);

  CoreLabel token4 = new CoreLabel();
  token4.setWord(".");
  token4.set(CoreAnnotations.TextAnnotation.class, ".");
  token4.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
  token4.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 24);
  token4.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 25);
  token4.set(CoreAnnotations.IsNewlineAnnotation.class, false);

  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(token1);
  tokens.add(token2);
  tokens.add(token3);
  tokens.add(token4);

  List<CoreMap> dummySentences = new ArrayList<>();
  CoreMap sentence = new Annotation("Already split annotation.");
  dummySentences.add(sentence);

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
  annotation.set(CoreAnnotations.SentencesAnnotation.class, dummySentences); 

  WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();
  annotator.annotate(annotation);

  List<CoreMap> result = annotation.get(CoreAnnotations.SentencesAnnotation.class);
  assertEquals(1, result.size());
  assertEquals("Already split annotation.", result.get(0).get(CoreAnnotations.TextAnnotation.class));
}
@Test
public void testEmptySentenceTokensThrowsIfLineNumberCountingDisabled() {
  String text = "";

  CoreLabel token = new CoreLabel(); 
  token.setWord("");
  token.set(CoreAnnotations.TextAnnotation.class, "");
  token.set(CoreAnnotations.OriginalTextAnnotation.class, "");
  token.set(CoreAnnotations.IsNewlineAnnotation.class, false);

  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(token);

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

  Properties properties = new Properties();
  properties.setProperty("ssplit.isOneSentence", "false");

  WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator(properties);

  try {
    annotator.annotate(annotation);
    fail("Expected IllegalStateException due to empty sentence");
  } catch (IllegalStateException e) {
    assertTrue(e.getMessage().contains("unexpected empty sentence"));
  }
}
@Test
public void testQuoteEnclosesOnlyPartialSentence_NoAuthorAnnotation() {
  String text = "She said “Hello there my friend” softly.";

  CoreLabel token1 = new CoreLabel();
  token1.setWord("She");
  token1.set(CoreAnnotations.TextAnnotation.class, "She");
  token1.set(CoreAnnotations.OriginalTextAnnotation.class, "She");
  token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 3);
  token1.set(CoreAnnotations.IsNewlineAnnotation.class, false);

  CoreLabel token2 = new CoreLabel();
  token2.setWord("said");
  token2.set(CoreAnnotations.TextAnnotation.class, "said");
  token2.set(CoreAnnotations.OriginalTextAnnotation.class, "said");
  token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 4);
  token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 8);
  token2.set(CoreAnnotations.IsNewlineAnnotation.class, false);

  CoreLabel token3 = new CoreLabel();
  token3.setWord("“");
  token3.set(CoreAnnotations.TextAnnotation.class, "“");
  token3.set(CoreAnnotations.OriginalTextAnnotation.class, "“");
  token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 9);
  token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 10);
  token3.set(CoreAnnotations.IsNewlineAnnotation.class, false);

  CoreLabel token4 = new CoreLabel();
  token4.setWord("Hello");
  token4.set(CoreAnnotations.TextAnnotation.class, "Hello");
  token4.set(CoreAnnotations.OriginalTextAnnotation.class, "Hello");
  token4.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 10);
  token4.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 15);
  token4.set(CoreAnnotations.IsNewlineAnnotation.class, false);

  CoreLabel token5 = new CoreLabel();
  token5.setWord("there");
  token5.set(CoreAnnotations.TextAnnotation.class, "there");
  token5.set(CoreAnnotations.OriginalTextAnnotation.class, "there");
  token5.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 16);
  token5.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 21);
  token5.set(CoreAnnotations.IsNewlineAnnotation.class, false);

  CoreLabel token6 = new CoreLabel();
  token6.setWord("my");
  token6.set(CoreAnnotations.TextAnnotation.class, "my");
  token6.set(CoreAnnotations.OriginalTextAnnotation.class, "my");
  token6.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 22);
  token6.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 24);
  token6.set(CoreAnnotations.IsNewlineAnnotation.class, false);

  CoreLabel token7 = new CoreLabel();
  token7.setWord("friend");
  token7.set(CoreAnnotations.TextAnnotation.class, "friend");
  token7.set(CoreAnnotations.OriginalTextAnnotation.class, "friend");
  token7.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 25);
  token7.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 31);
  token7.set(CoreAnnotations.IsNewlineAnnotation.class, false);

  CoreLabel token8 = new CoreLabel();
  token8.setWord("”");
  token8.set(CoreAnnotations.TextAnnotation.class, "”");
  token8.set(CoreAnnotations.OriginalTextAnnotation.class, "”");
  token8.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 31);
  token8.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 32);
  token8.set(CoreAnnotations.IsNewlineAnnotation.class, false);

  CoreLabel token9 = new CoreLabel();
  token9.setWord("softly");
  token9.set(CoreAnnotations.TextAnnotation.class, "softly");
  token9.set(CoreAnnotations.OriginalTextAnnotation.class, "softly");
  token9.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 33);
  token9.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 39);
  token9.set(CoreAnnotations.IsNewlineAnnotation.class, false);

  CoreLabel token10 = new CoreLabel();
  token10.setWord(".");
  token10.set(CoreAnnotations.TextAnnotation.class, ".");
  token10.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
  token10.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 39);
  token10.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 40);
  token10.set(CoreAnnotations.IsNewlineAnnotation.class, false);

  List<CoreLabel> tokens = Arrays.asList(
    token1, token2, token3, token4, token5, token6, token7, token8, token9, token10
  );

  CoreMap quote = new Annotation("“Hello there my friend”");
  quote.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 9);
  quote.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 32);
  quote.set(CoreAnnotations.AuthorAnnotation.class, null); 

  CoreMap section = new Annotation("dialogue");
  section.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  section.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 40);
  section.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());
  section.set(CoreAnnotations.QuotesAnnotation.class, Collections.singletonList(quote));

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
  annotation.set(CoreAnnotations.SectionsAnnotation.class, Collections.singletonList(section));

  WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();
  annotator.annotate(annotation);

  List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
  assertEquals(1, sentences.size());
  CoreMap sentence = sentences.get(0);
  assertTrue(sentence.containsKey(CoreAnnotations.QuotedAnnotation.class));
  assertNull(sentence.get(CoreAnnotations.AuthorAnnotation.class));
}
@Test
public void testNewlineResetBehaviorOnBeforeAndAfterTokens() {
  String text = "Hello\nworld";

  CoreLabel token1 = new CoreLabel();
  token1.setWord("Hello");
  token1.set(CoreAnnotations.TextAnnotation.class, "Hello");
  token1.set(CoreAnnotations.OriginalTextAnnotation.class, "Hello");
  token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
  token1.set(CoreAnnotations.AfterAnnotation.class, " ");
  token1.set(CoreAnnotations.IsNewlineAnnotation.class, false);

  CoreLabel newline = new CoreLabel();
  newline.setWord("\n");
  newline.set(CoreAnnotations.TextAnnotation.class, "\n");
  newline.set(CoreAnnotations.OriginalTextAnnotation.class, "\n");
  newline.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
  newline.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);
  newline.set(CoreAnnotations.IsNewlineAnnotation.class, true);

  CoreLabel token2 = new CoreLabel();
  token2.setWord("world");
  token2.set(CoreAnnotations.TextAnnotation.class, "world");
  token2.set(CoreAnnotations.OriginalTextAnnotation.class, "world");
  token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 6);
  token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 11);
  token2.set(CoreAnnotations.BeforeAnnotation.class, " ");
  token2.set(CoreAnnotations.IsNewlineAnnotation.class, false);

  List<CoreLabel> tokens = Arrays.asList(token1, newline, token2);

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

  WordsToSentencesAnnotator annotator = WordsToSentencesAnnotator.newlineSplitter("\n");
  annotator.annotate(annotation);

  List<CoreLabel> updatedTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
  CoreLabel updatedToken1 = updatedTokens.get(0);
  CoreLabel updatedToken2 = updatedTokens.get(1);

  assertEquals("\n", updatedToken1.get(CoreAnnotations.AfterAnnotation.class));
  assertEquals("\n", updatedToken2.get(CoreAnnotations.BeforeAnnotation.class));
}
@Test
public void testMultiTokenSentenceBoundaryRegexSplitting() {
  String text = "This is Mr. Smith. This is another sentence.";

  CoreLabel token1 = new CoreLabel();
  token1.setWord("This");
  token1.set(CoreAnnotations.TextAnnotation.class, "This");
  token1.set(CoreAnnotations.OriginalTextAnnotation.class, "This");
  token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);
  token1.set(CoreAnnotations.IsNewlineAnnotation.class, false);

  CoreLabel token2 = new CoreLabel();
  token2.setWord("is");
  token2.set(CoreAnnotations.TextAnnotation.class, "is");
  token2.set(CoreAnnotations.OriginalTextAnnotation.class, "is");
  token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
  token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 7);
  token2.set(CoreAnnotations.IsNewlineAnnotation.class, false);

  CoreLabel token3 = new CoreLabel();
  token3.setWord("Mr.");
  token3.set(CoreAnnotations.TextAnnotation.class, "Mr.");
  token3.set(CoreAnnotations.OriginalTextAnnotation.class, "Mr.");
  token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 8);
  token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 11);
  token3.set(CoreAnnotations.IsNewlineAnnotation.class, false);

  CoreLabel token4 = new CoreLabel();
  token4.setWord("Smith");
  token4.set(CoreAnnotations.TextAnnotation.class, "Smith");
  token4.set(CoreAnnotations.OriginalTextAnnotation.class, "Smith");
  token4.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 12);
  token4.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 17);
  token4.set(CoreAnnotations.IsNewlineAnnotation.class, false);

  CoreLabel token5 = new CoreLabel();
  token5.setWord(".");
  token5.set(CoreAnnotations.TextAnnotation.class, ".");
  token5.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
  token5.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 17);
  token5.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 18);
  token5.set(CoreAnnotations.IsNewlineAnnotation.class, false);

  CoreLabel token6 = new CoreLabel();
  token6.setWord("This");
  token6.set(CoreAnnotations.TextAnnotation.class, "This");
  token6.set(CoreAnnotations.OriginalTextAnnotation.class, "This");
  token6.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 19);
  token6.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 23);
  token6.set(CoreAnnotations.IsNewlineAnnotation.class, false);

  CoreLabel token7 = new CoreLabel();
  token7.setWord("is");
  token7.set(CoreAnnotations.TextAnnotation.class, "is");
  token7.set(CoreAnnotations.OriginalTextAnnotation.class, "is");
  token7.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 24);
  token7.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 26);
  token7.set(CoreAnnotations.IsNewlineAnnotation.class, false);

  CoreLabel token8 = new CoreLabel();
  token8.setWord("another");
  token8.set(CoreAnnotations.TextAnnotation.class, "another");
  token8.set(CoreAnnotations.OriginalTextAnnotation.class, "another");
  token8.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 27);
  token8.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 34);
  token8.set(CoreAnnotations.IsNewlineAnnotation.class, false);

  CoreLabel token9 = new CoreLabel();
  token9.setWord("sentence");
  token9.set(CoreAnnotations.TextAnnotation.class, "sentence");
  token9.set(CoreAnnotations.OriginalTextAnnotation.class, "sentence");
  token9.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 35);
  token9.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 43);
  token9.set(CoreAnnotations.IsNewlineAnnotation.class, false);

  CoreLabel token10 = new CoreLabel();
  token10.setWord(".");
  token10.set(CoreAnnotations.TextAnnotation.class, ".");
  token10.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
  token10.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 43);
  token10.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 44);
  token10.set(CoreAnnotations.IsNewlineAnnotation.class, false);

  List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4, token5,
                                         token6, token7, token8, token9, token10);

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

  Properties properties = new Properties();
  properties.setProperty("ssplit.boundaryMultiTokenRegex", "Mr\\.\\s+Smith\\.");

  WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator(properties);
  annotator.annotate(annotation);

  List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
  assertEquals(2, sentences.size());
  assertTrue(sentences.get(0).get(CoreAnnotations.TextAnnotation.class).contains("Mr. Smith"));
}
@Test
public void testSectionQuoteIgnoresOutOfBoundsOffsets() {
  String text = "Something simple.";

  CoreLabel token1 = new CoreLabel();
  token1.setWord("Something");
  token1.set(CoreAnnotations.TextAnnotation.class, "Something");
  token1.set(CoreAnnotations.OriginalTextAnnotation.class, "Something");
  token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 9);
  token1.set(CoreAnnotations.IsNewlineAnnotation.class, false);

  CoreLabel token2 = new CoreLabel();
  token2.setWord("simple");
  token2.set(CoreAnnotations.TextAnnotation.class, "simple");
  token2.set(CoreAnnotations.OriginalTextAnnotation.class, "simple");
  token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 10);
  token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 16);
  token2.set(CoreAnnotations.IsNewlineAnnotation.class, false);

  CoreLabel token3 = new CoreLabel();
  token3.setWord(".");
  token3.set(CoreAnnotations.TextAnnotation.class, ".");
  token3.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
  token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 16);
  token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 17);
  token3.set(CoreAnnotations.IsNewlineAnnotation.class, false);

  List<CoreLabel> tokens = Arrays.asList(token1, token2, token3);

  CoreMap quote = new Annotation("misplaced");
  quote.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 30); 
  quote.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 40);
  quote.set(CoreAnnotations.AuthorAnnotation.class, "Unknown");

  CoreMap section = new Annotation("s");
  section.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  section.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 17);
  section.set(CoreAnnotations.QuotesAnnotation.class, Collections.singletonList(quote));
  section.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
  annotation.set(CoreAnnotations.SectionsAnnotation.class, Collections.singletonList(section));

  WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();
  annotator.annotate(annotation);

  List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
  assertEquals(1, sentences.size());
  assertNull(sentences.get(0).get(CoreAnnotations.QuotedAnnotation.class));
}
@Test
public void testBoundaryToDiscardRemovesTokensButKeepsSentence() {
  String text = "What ?";

  CoreLabel token1 = new CoreLabel();
  token1.setWord("What");
  token1.set(CoreAnnotations.TextAnnotation.class, "What");
  token1.set(CoreAnnotations.OriginalTextAnnotation.class, "What");
  token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);
  token1.set(CoreAnnotations.IsNewlineAnnotation.class, false);

  CoreLabel token2 = new CoreLabel();
  token2.setWord("?");
  token2.set(CoreAnnotations.TextAnnotation.class, "?");
  token2.set(CoreAnnotations.OriginalTextAnnotation.class, "?");
  token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
  token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);
  token2.set(CoreAnnotations.IsNewlineAnnotation.class, false);

  List<CoreLabel> tokens = Arrays.asList(token1, token2);

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

  Properties props = new Properties();
  props.setProperty("ssplit.boundariesToDiscard", "\\?");

  WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator(props);
  annotator.annotate(annotation);

  List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
  assertEquals(1, sentences.size());
  assertTrue(sentences.get(0).get(CoreAnnotations.TextAnnotation.class).startsWith("What"));
}
@Test
public void testMultipleQuotesInSameSectionBothMatched() {
  String text = "\"Hello\" she said. \"Bye\" he replied.";

  CoreLabel token1 = new CoreLabel();
  token1.setWord("\"");
  token1.set(CoreAnnotations.TextAnnotation.class, "\"");
  token1.set(CoreAnnotations.OriginalTextAnnotation.class, "\"");
  token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 1);
  token1.set(CoreAnnotations.IsNewlineAnnotation.class, false);

  CoreLabel token2 = new CoreLabel();
  token2.setWord("Hello");
  token2.set(CoreAnnotations.TextAnnotation.class, "Hello");
  token2.set(CoreAnnotations.OriginalTextAnnotation.class, "Hello");
  token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 1);
  token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);
  token2.set(CoreAnnotations.IsNewlineAnnotation.class, false);

  CoreLabel token3 = new CoreLabel();
  token3.setWord("\"");
  token3.set(CoreAnnotations.TextAnnotation.class, "\"");
  token3.set(CoreAnnotations.OriginalTextAnnotation.class, "\"");
  token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 6);
  token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 7);
  token3.set(CoreAnnotations.IsNewlineAnnotation.class, false);

  CoreLabel token4 = new CoreLabel();
  token4.setWord("she");
  token4.set(CoreAnnotations.TextAnnotation.class, "she");
  token4.set(CoreAnnotations.OriginalTextAnnotation.class, "she");
  token4.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 8);
  token4.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 11);
  token4.set(CoreAnnotations.IsNewlineAnnotation.class, false);

  CoreLabel token5 = new CoreLabel();
  token5.setWord("said");
  token5.set(CoreAnnotations.TextAnnotation.class, "said");
  token5.set(CoreAnnotations.OriginalTextAnnotation.class, "said");
  token5.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 12);
  token5.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 16);
  token5.set(CoreAnnotations.IsNewlineAnnotation.class, false);

  CoreLabel token6 = new CoreLabel();
  token6.setWord(".");
  token6.set(CoreAnnotations.TextAnnotation.class, ".");
  token6.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
  token6.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 16);
  token6.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 17);
  token6.set(CoreAnnotations.IsNewlineAnnotation.class, false);

  CoreLabel token7 = new CoreLabel();
  token7.setWord("\"");
  token7.set(CoreAnnotations.TextAnnotation.class, "\"");
  token7.set(CoreAnnotations.OriginalTextAnnotation.class, "\"");
  token7.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 18);
  token7.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 19);
  token7.set(CoreAnnotations.IsNewlineAnnotation.class, false);

  CoreLabel token8 = new CoreLabel();
  token8.setWord("Bye");
  token8.set(CoreAnnotations.TextAnnotation.class, "Bye");
  token8.set(CoreAnnotations.OriginalTextAnnotation.class, "Bye");
  token8.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 19);
  token8.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 22);
  token8.set(CoreAnnotations.IsNewlineAnnotation.class, false);

  CoreLabel token9 = new CoreLabel();
  token9.setWord("\"");
  token9.set(CoreAnnotations.TextAnnotation.class, "\"");
  token9.set(CoreAnnotations.OriginalTextAnnotation.class, "\"");
  token9.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 22);
  token9.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 23);
  token9.set(CoreAnnotations.IsNewlineAnnotation.class, false);

  CoreLabel token10 = new CoreLabel();
  token10.setWord("he");
  token10.set(CoreAnnotations.TextAnnotation.class, "he");
  token10.set(CoreAnnotations.OriginalTextAnnotation.class, "he");
  token10.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 24);
  token10.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 26);
  token10.set(CoreAnnotations.IsNewlineAnnotation.class, false);

  CoreLabel token11 = new CoreLabel();
  token11.setWord("replied");
  token11.set(CoreAnnotations.TextAnnotation.class, "replied");
  token11.set(CoreAnnotations.OriginalTextAnnotation.class, "replied");
  token11.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 27);
  token11.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 34);
  token11.set(CoreAnnotations.IsNewlineAnnotation.class, false);

  CoreLabel token12 = new CoreLabel();
  token12.setWord(".");
  token12.set(CoreAnnotations.TextAnnotation.class, ".");
  token12.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
  token12.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 34);
  token12.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 35);
  token12.set(CoreAnnotations.IsNewlineAnnotation.class, false);

  List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4, token5, token6, 
                                         token7, token8, token9, token10, token11, token12);

  CoreMap quote1 = new Annotation("\"Hello\"");
  quote1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  quote1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 7);
  quote1.set(CoreAnnotations.AuthorAnnotation.class, "she");

  CoreMap quote2 = new Annotation("\"Bye\"");
  quote2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 18);
  quote2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 23);
  quote2.set(CoreAnnotations.AuthorAnnotation.class, "he");

  List<CoreMap> quotes = Arrays.asList(quote1, quote2);

  CoreMap section = new Annotation("Conversation");
  section.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  section.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 35);
  section.set(CoreAnnotations.QuotesAnnotation.class, quotes);
  section.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
  annotation.set(CoreAnnotations.SectionsAnnotation.class, Collections.singletonList(section));

  WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();
  annotator.annotate(annotation);

  List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
  assertEquals(2, sentences.size());
  assertEquals("she", sentences.get(0).get(CoreAnnotations.AuthorAnnotation.class));
  assertEquals("he", sentences.get(1).get(CoreAnnotations.AuthorAnnotation.class));
}
@Test
public void testNullValuesInQuotesAndSectionAreHandledGracefully() {
  String text = "This is a test sentence.";
  CoreLabel token1 = new CoreLabel();
  token1.setWord("This");
  token1.set(CoreAnnotations.TextAnnotation.class, "This");
  token1.set(CoreAnnotations.OriginalTextAnnotation.class, "This");
  token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);
  token1.set(CoreAnnotations.IsNewlineAnnotation.class, false);
  
  CoreLabel token2 = new CoreLabel();
  token2.setWord("is");
  token2.set(CoreAnnotations.TextAnnotation.class, "is");
  token2.set(CoreAnnotations.OriginalTextAnnotation.class, "is");
  token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
  token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 7);
  token2.set(CoreAnnotations.IsNewlineAnnotation.class, false);

  CoreLabel token3 = new CoreLabel();
  token3.setWord("a");
  token3.set(CoreAnnotations.TextAnnotation.class, "a");
  token3.set(CoreAnnotations.OriginalTextAnnotation.class, "a");
  token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 8);
  token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 9);
  token3.set(CoreAnnotations.IsNewlineAnnotation.class, false);

  CoreLabel token4 = new CoreLabel();
  token4.setWord("test");
  token4.set(CoreAnnotations.TextAnnotation.class, "test");
  token4.set(CoreAnnotations.OriginalTextAnnotation.class, "test");
  token4.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 10);
  token4.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 14);
  token4.set(CoreAnnotations.IsNewlineAnnotation.class, false);

  CoreLabel token5 = new CoreLabel();
  token5.setWord("sentence");
  token5.set(CoreAnnotations.TextAnnotation.class, "sentence");
  token5.set(CoreAnnotations.OriginalTextAnnotation.class, "sentence");
  token5.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 15);
  token5.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 23);
  token5.set(CoreAnnotations.IsNewlineAnnotation.class, false);

  CoreLabel token6 = new CoreLabel();
  token6.setWord(".");
  token6.set(CoreAnnotations.TextAnnotation.class, ".");
  token6.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
  token6.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 23);
  token6.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 24);
  token6.set(CoreAnnotations.IsNewlineAnnotation.class, false);

  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(token1);
  tokens.add(token2);
  tokens.add(token3);
  tokens.add(token4);
  tokens.add(token5);
  tokens.add(token6);

  CoreMap quote = new Annotation("null quote");
  quote.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, null);
  quote.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, null);
  quote.set(CoreAnnotations.AuthorAnnotation.class, "Unknown");

  CoreMap section = new Annotation("S");
  section.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  section.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 24);
  section.set(CoreAnnotations.QuotesAnnotation.class, Collections.singletonList(quote));
  section.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
  annotation.set(CoreAnnotations.SectionsAnnotation.class, Collections.singletonList(section));

  WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();
  annotator.annotate(annotation);

  List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
  assertEquals(1, sentences.size());
  assertNull(sentences.get(0).get(CoreAnnotations.QuotedAnnotation.class));
}
@Test
public void testTokenOffsetsOutOfTextBoundsHandledGracefully() {
  String text = "Short text";
  CoreLabel token1 = new CoreLabel();
  token1.setWord("Short");
  token1.set(CoreAnnotations.TextAnnotation.class, "Short");
  token1.set(CoreAnnotations.OriginalTextAnnotation.class, "Short");
  token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
  token1.set(CoreAnnotations.IsNewlineAnnotation.class, false);

  CoreLabel token2 = new CoreLabel();
  token2.setWord("text");
  token2.set(CoreAnnotations.TextAnnotation.class, "text");
  token2.set(CoreAnnotations.OriginalTextAnnotation.class, "text");
  token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 6);
  token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 100); 
  token2.set(CoreAnnotations.IsNewlineAnnotation.class, false);

  List<CoreLabel> tokens = new ArrayList<>();
  tokens.add(token1);
  tokens.add(token2);

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

  WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();

  try {
    annotator.annotate(annotation);
  } catch (Exception e) {
    fail("Annotator should handle token offsets exceeding text length without throwing");
  }
}
@Test
public void testAnnotationWithDocIDAnnotationIsCopiedToSentence() {
  String text = "A sentence.";
  CoreLabel token1 = new CoreLabel();
  token1.setWord("A");
  token1.set(CoreAnnotations.TextAnnotation.class, "A");
  token1.set(CoreAnnotations.OriginalTextAnnotation.class, "A");
  token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 1);
  token1.set(CoreAnnotations.IsNewlineAnnotation.class, false);

  CoreLabel token2 = new CoreLabel();
  token2.setWord("sentence");
  token2.set(CoreAnnotations.TextAnnotation.class, "sentence");
  token2.set(CoreAnnotations.OriginalTextAnnotation.class, "sentence");
  token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 2);
  token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 10);
  token2.set(CoreAnnotations.IsNewlineAnnotation.class, false);

  CoreLabel token3 = new CoreLabel();
  token3.setWord(".");
  token3.set(CoreAnnotations.TextAnnotation.class, ".");
  token3.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
  token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 10);
  token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 11);
  token3.set(CoreAnnotations.IsNewlineAnnotation.class, false);

  List<CoreLabel> tokens = Arrays.asList(token1, token2, token3);

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
  annotation.set(CoreAnnotations.DocIDAnnotation.class, "doc-123");

  WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();
  annotator.annotate(annotation);

  List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
  assertEquals("doc-123", sentences.get(0).get(CoreAnnotations.DocIDAnnotation.class));
}
@Test
public void testNewlineAfterLastTokenFixesPreviousTokenAfterText() {
  String text = "alpha\n";
  CoreLabel token1 = new CoreLabel();
  token1.setWord("alpha");
  token1.set(CoreAnnotations.TextAnnotation.class, "alpha");
  token1.set(CoreAnnotations.OriginalTextAnnotation.class, "alpha");
  token1.set(CoreAnnotations.IsNewlineAnnotation.class, false);
  token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
  token1.set(CoreAnnotations.AfterAnnotation.class, " "); 

  CoreLabel token2 = new CoreLabel();
  token2.setWord("\n");
  token2.set(CoreAnnotations.TextAnnotation.class, "\n");
  token2.set(CoreAnnotations.OriginalTextAnnotation.class, "\n");
  token2.set(CoreAnnotations.IsNewlineAnnotation.class, true);
  token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
  token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);

  List<CoreLabel> tokens = Arrays.asList(token1, token2);

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

  WordsToSentencesAnnotator annotator = WordsToSentencesAnnotator.newlineSplitter("\n");
  annotator.annotate(annotation);

  List<CoreLabel> updatedTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
  assertEquals(1, updatedTokens.size());
  CoreLabel updatedToken = updatedTokens.get(0);
  assertEquals("\n", updatedToken.get(CoreAnnotations.AfterAnnotation.class));
}
@Test
public void testSectionIndexAssignedInSentenceWhenWithinSectionBounds() {
  String text = "Inside section.";
  CoreLabel token1 = new CoreLabel();
  token1.setWord("Inside");
  token1.set(CoreAnnotations.TextAnnotation.class, "Inside");
  token1.set(CoreAnnotations.OriginalTextAnnotation.class, "Inside");
  token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);
  token1.set(CoreAnnotations.IsNewlineAnnotation.class, false);

  CoreLabel token2 = new CoreLabel();
  token2.setWord("section");
  token2.set(CoreAnnotations.TextAnnotation.class, "section");
  token2.set(CoreAnnotations.OriginalTextAnnotation.class, "section");
  token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 7);
  token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 14);
  token2.set(CoreAnnotations.IsNewlineAnnotation.class, false);

  CoreLabel token3 = new CoreLabel();
  token3.setWord(".");
  token3.set(CoreAnnotations.TextAnnotation.class, ".");
  token3.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
  token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 14);
  token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 15);
  token3.set(CoreAnnotations.IsNewlineAnnotation.class, false);

  List<CoreLabel> tokens = Arrays.asList(token1, token2, token3);

  CoreMap section = new Annotation("S");
  section.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  section.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 15);
  section.set(CoreAnnotations.QuotesAnnotation.class, new ArrayList<>());
  section.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());
  section.set(CoreAnnotations.SectionDateAnnotation.class, "2024-01-01");

  List<CoreMap> sections = Collections.singletonList(section);

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
  annotation.set(CoreAnnotations.SectionsAnnotation.class, sections);

  WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();
  annotator.annotate(annotation);

  List<CoreMap> updatedSentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
  assertEquals(1, updatedSentences.size());
  CoreMap sentence = updatedSentences.get(0);
  assertEquals(0, sentence.get(CoreAnnotations.SectionIndexAnnotation.class).intValue());
  assertEquals("2024-01-01", sentence.get(CoreAnnotations.SectionDateAnnotation.class));
}
@Test
public void testTokenWithMissingCharacterOffsetsSkipsSentenceWithoutException() {
  String text = "Hello world!";

  CoreLabel token1 = new CoreLabel();
  token1.setWord("Hello");
  token1.set(CoreAnnotations.TextAnnotation.class, "Hello");
  token1.set(CoreAnnotations.OriginalTextAnnotation.class, "Hello");
  token1.set(CoreAnnotations.IsNewlineAnnotation.class, false);
  
  CoreLabel token2 = new CoreLabel();
  token2.setWord("world");
  token2.set(CoreAnnotations.TextAnnotation.class, "world");
  token2.set(CoreAnnotations.OriginalTextAnnotation.class, "world");
  token2.set(CoreAnnotations.IsNewlineAnnotation.class, false);
  

  List<CoreLabel> tokens = Arrays.asList(token1, token2);

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

  try {
    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();
    annotator.annotate(annotation);
  } catch (Exception e) {
    fail("Annotator should skip sentence or throw meaningful error, not crash unexpectedly. Got: " + e.getMessage());
  }
}
@Test
public void testSingleNewlineTokenIsRemovedFromTokensList() {
  String text = "\n";

  CoreLabel token = new CoreLabel();
  token.setWord("\n");
  token.set(CoreAnnotations.TextAnnotation.class, "\n");
  token.set(CoreAnnotations.IsNewlineAnnotation.class, true);
  token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 1);
  token.set(CoreAnnotations.OriginalTextAnnotation.class, "\n");

  List<CoreLabel> tokens = Collections.singletonList(token);

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

  
  WordsToSentencesAnnotator annotator = WordsToSentencesAnnotator.newlineSplitter("\n");
  annotator.annotate(annotation);

  List<CoreLabel> remaining = annotation.get(CoreAnnotations.TokensAnnotation.class);
  assertNotNull(remaining);
  assertEquals(0, remaining.size());
}
@Test
public void testAfterAnnotationPreservedIfNotFollowingNewline() {
  String text = "one two";

  CoreLabel token1 = new CoreLabel();
  token1.setWord("one");
  token1.set(CoreAnnotations.TextAnnotation.class, "one");
  token1.set(CoreAnnotations.OriginalTextAnnotation.class, "one");
  token1.set(CoreAnnotations.AfterAnnotation.class, " "); 
  token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 3);
  token1.set(CoreAnnotations.IsNewlineAnnotation.class, false);

  CoreLabel token2 = new CoreLabel();
  token2.setWord("two");
  token2.set(CoreAnnotations.TextAnnotation.class, "two");
  token2.set(CoreAnnotations.OriginalTextAnnotation.class, "two");
  token2.set(CoreAnnotations.AfterAnnotation.class, ""); 
  token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 4);
  token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 7);
  token2.set(CoreAnnotations.IsNewlineAnnotation.class, false);

  List<CoreLabel> tokens = Arrays.asList(token1, token2);

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

  WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();
  annotator.annotate(annotation);

  List<CoreLabel> updated = annotation.get(CoreAnnotations.TokensAnnotation.class);
  assertEquals(" ", updated.get(0).get(CoreAnnotations.AfterAnnotation.class));
}
@Test
public void testEmptySectionsAnnotationHandledGracefully() {
  String text = "This is a sentence.";

  CoreLabel token1 = new CoreLabel();
  token1.setWord("This");
  token1.set(CoreAnnotations.TextAnnotation.class, "This");
  token1.set(CoreAnnotations.OriginalTextAnnotation.class, "This");
  token1.set(CoreAnnotations.IsNewlineAnnotation.class, false);
  token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 4);

  CoreLabel token2 = new CoreLabel();
  token2.setWord("is");
  token2.set(CoreAnnotations.TextAnnotation.class, "is");
  token2.set(CoreAnnotations.OriginalTextAnnotation.class, "is");
  token2.set(CoreAnnotations.IsNewlineAnnotation.class, false);
  token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 5);
  token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 7);

  CoreLabel token3 = new CoreLabel();
  token3.setWord("a");
  token3.set(CoreAnnotations.TextAnnotation.class, "a");
  token3.set(CoreAnnotations.OriginalTextAnnotation.class, "a");
  token3.set(CoreAnnotations.IsNewlineAnnotation.class, false);
  token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 8);
  token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 9);

  CoreLabel token4 = new CoreLabel();
  token4.setWord("sentence");
  token4.set(CoreAnnotations.TextAnnotation.class, "sentence");
  token4.set(CoreAnnotations.OriginalTextAnnotation.class, "sentence");
  token4.set(CoreAnnotations.IsNewlineAnnotation.class, false);
  token4.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 10);
  token4.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 18);

  CoreLabel token5 = new CoreLabel();
  token5.setWord(".");
  token5.set(CoreAnnotations.TextAnnotation.class, ".");
  token5.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
  token5.set(CoreAnnotations.IsNewlineAnnotation.class, false);
  token5.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 18);
  token5.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 19);

  List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4, token5);

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
  annotation.set(CoreAnnotations.SectionsAnnotation.class, new ArrayList<CoreMap>());

  WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();
  annotator.annotate(annotation);

  List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
  assertEquals(1, sentences.size());
}
@Test
public void testBeforeAnnotationOnFirstTokenUnchangedWhenNoPreviousNewline() {
  String text = "Alpha";

  CoreLabel token1 = new CoreLabel();
  token1.setWord("Alpha");
  token1.set(CoreAnnotations.TextAnnotation.class, "Alpha");
  token1.set(CoreAnnotations.OriginalTextAnnotation.class, "Alpha");
  token1.set(CoreAnnotations.BeforeAnnotation.class, " ");
  token1.set(CoreAnnotations.IsNewlineAnnotation.class, false);
  token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);

  List<CoreLabel> tokens = Collections.singletonList(token1);

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

  WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();
  annotator.annotate(annotation);

  List<CoreLabel> results = annotation.get(CoreAnnotations.TokensAnnotation.class);
  assertEquals(" ", results.get(0).get(CoreAnnotations.BeforeAnnotation.class));
}
@Test
public void testEmptyTextAnnotationHandledCorrectlyWithTokens() {
  String text = "";

  CoreLabel token = new CoreLabel();
  token.setWord("Hello");
  token.set(CoreAnnotations.TextAnnotation.class, "Hello");
  token.set(CoreAnnotations.OriginalTextAnnotation.class, "Hello");
  token.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
  token.set(CoreAnnotations.IsNewlineAnnotation.class, false);

  List<CoreLabel> tokens = Collections.singletonList(token);

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

  try {
    WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();
    annotator.annotate(annotation);
  } catch (Exception e) {
    fail("Annotator should not throw on empty text if tokens are provided");
  }
}
@Test
public void testSentenceWithStartAndEndSectionAnnotationTransfersMetadata() {
  String text = "Sectioned sentence.";

  CoreLabel token1 = new CoreLabel();
  token1.setWord("Sectioned");
  token1.set(CoreAnnotations.TextAnnotation.class, "Sectioned");
  token1.set(CoreAnnotations.OriginalTextAnnotation.class, "Sectioned");
  token1.set(CoreAnnotations.IsNewlineAnnotation.class, false);
  token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 9);

  CoreLabel token2 = new CoreLabel();
  token2.setWord("sentence");
  token2.set(CoreAnnotations.TextAnnotation.class, "sentence");
  token2.set(CoreAnnotations.OriginalTextAnnotation.class, "sentence");
  token2.set(CoreAnnotations.IsNewlineAnnotation.class, false);
  token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 10);
  token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 18);

  CoreLabel token3 = new CoreLabel();
  token3.setWord(".");
  token3.set(CoreAnnotations.TextAnnotation.class, ".");
  token3.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
  token3.set(CoreAnnotations.IsNewlineAnnotation.class, false);
  token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 18);
  token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 19);

  CoreMap section = new Annotation("S");
  section.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  section.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 19);
  section.set(CoreAnnotations.SectionDateAnnotation.class, "2030-05-01");
  section.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());
  section.set(CoreAnnotations.QuotesAnnotation.class, new ArrayList<>());

  token1.set(CoreAnnotations.SectionStartAnnotation.class, section);
  token3.set(CoreAnnotations.SectionEndAnnotation.class, "end");

  List<CoreLabel> tokens = Arrays.asList(token1, token2, token3);

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
  annotation.set(CoreAnnotations.SectionsAnnotation.class, Collections.singletonList(section));

  WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();
  annotator.annotate(annotation);

  List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
  CoreMap sentence = sentences.get(0);
  assertEquals("2030-05-01", sentence.get(CoreAnnotations.SectionDateAnnotation.class));
}
@Test
public void testMultipleSectionsAssignsCorrectSectionIndex() {
  String text = "First section. Second section.";

  CoreLabel token1 = new CoreLabel();
  token1.setWord("First");
  token1.set(CoreAnnotations.TextAnnotation.class, "First");
  token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);
  token1.set(CoreAnnotations.IsNewlineAnnotation.class, false);
  token1.set(CoreAnnotations.OriginalTextAnnotation.class, "First");

  CoreLabel token2 = new CoreLabel();
  token2.setWord("section");
  token2.set(CoreAnnotations.TextAnnotation.class, "section");
  token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 6);
  token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 13);
  token2.set(CoreAnnotations.IsNewlineAnnotation.class, false);
  token2.set(CoreAnnotations.OriginalTextAnnotation.class, "section");

  CoreLabel token3 = new CoreLabel();
  token3.setWord(".");
  token3.set(CoreAnnotations.TextAnnotation.class, ".");
  token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 13);
  token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 14);
  token3.set(CoreAnnotations.IsNewlineAnnotation.class, false);
  token3.set(CoreAnnotations.OriginalTextAnnotation.class, ".");

  CoreLabel token4 = new CoreLabel();
  token4.setWord("Second");
  token4.set(CoreAnnotations.TextAnnotation.class, "Second");
  token4.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 15);
  token4.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 21);
  token4.set(CoreAnnotations.IsNewlineAnnotation.class, false);
  token4.set(CoreAnnotations.OriginalTextAnnotation.class, "Second");

  CoreLabel token5 = new CoreLabel();
  token5.setWord("section");
  token5.set(CoreAnnotations.TextAnnotation.class, "section");
  token5.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 22);
  token5.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 29);
  token5.set(CoreAnnotations.IsNewlineAnnotation.class, false);
  token5.set(CoreAnnotations.OriginalTextAnnotation.class, "section");

  CoreLabel token6 = new CoreLabel();
  token6.setWord(".");
  token6.set(CoreAnnotations.TextAnnotation.class, ".");
  token6.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 29);
  token6.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 30);
  token6.set(CoreAnnotations.IsNewlineAnnotation.class, false);
  token6.set(CoreAnnotations.OriginalTextAnnotation.class, ".");

  List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4, token5, token6);

  CoreMap section1 = new Annotation("Sec1");
  section1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  section1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 14);
  section1.set(CoreAnnotations.SectionDateAnnotation.class, "2022-01-01");
  section1.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());
  section1.set(CoreAnnotations.QuotesAnnotation.class, new ArrayList<>());

  CoreMap section2 = new Annotation("Sec2");
  section2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 15);
  section2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 30);
  section2.set(CoreAnnotations.SectionDateAnnotation.class, "2022-02-01");
  section2.set(CoreAnnotations.SentencesAnnotation.class, new ArrayList<>());
  section2.set(CoreAnnotations.QuotesAnnotation.class, new ArrayList<>());

  List<CoreMap> sections = Arrays.asList(section1, section2);

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);
  annotation.set(CoreAnnotations.SectionsAnnotation.class, sections);

  WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();
  annotator.annotate(annotation);

  List<CoreMap> sentenceList = annotation.get(CoreAnnotations.SentencesAnnotation.class);
  assertEquals(2, sentenceList.size());

  assertEquals(0, sentenceList.get(0).get(CoreAnnotations.SectionIndexAnnotation.class).intValue());
  assertEquals("2022-01-01", sentenceList.get(0).get(CoreAnnotations.SectionDateAnnotation.class));
  assertEquals(1, sentenceList.get(1).get(CoreAnnotations.SectionIndexAnnotation.class).intValue());
  assertEquals("2022-02-01", sentenceList.get(1).get(CoreAnnotations.SectionDateAnnotation.class));
}
@Test
public void testNewlineAtBeginningAndEndDoesNotCreateInvalidSentence() {
  String text = "\nStart middle end\n";

  CoreLabel token1 = new CoreLabel();
  token1.setWord("\n");
  token1.set(CoreAnnotations.TextAnnotation.class, "\n");
  token1.set(CoreAnnotations.OriginalTextAnnotation.class, "\n");
  token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 1);
  token1.set(CoreAnnotations.IsNewlineAnnotation.class, true);

  CoreLabel token2 = new CoreLabel();
  token2.setWord("Start");
  token2.set(CoreAnnotations.TextAnnotation.class, "Start");
  token2.set(CoreAnnotations.OriginalTextAnnotation.class, "Start");
  token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 1);
  token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 6);
  token2.set(CoreAnnotations.IsNewlineAnnotation.class, false);

  CoreLabel token3 = new CoreLabel();
  token3.setWord("middle");
  token3.set(CoreAnnotations.TextAnnotation.class, "middle");
  token3.set(CoreAnnotations.OriginalTextAnnotation.class, "middle");
  token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 7);
  token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 13);
  token3.set(CoreAnnotations.IsNewlineAnnotation.class, false);

  CoreLabel token4 = new CoreLabel();
  token4.setWord("end");
  token4.set(CoreAnnotations.TextAnnotation.class, "end");
  token4.set(CoreAnnotations.OriginalTextAnnotation.class, "end");
  token4.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 14);
  token4.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 17);
  token4.set(CoreAnnotations.IsNewlineAnnotation.class, false);

  CoreLabel token5 = new CoreLabel();
  token5.setWord("\n");
  token5.set(CoreAnnotations.TextAnnotation.class, "\n");
  token5.set(CoreAnnotations.OriginalTextAnnotation.class, "\n");
  token5.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 17);
  token5.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 18);
  token5.set(CoreAnnotations.IsNewlineAnnotation.class, true);

  List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4, token5);

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

  WordsToSentencesAnnotator annotator = WordsToSentencesAnnotator.newlineSplitter("\n");
  annotator.annotate(annotation);

  List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
  assertEquals(1, sentences.size());
  assertEquals("Start middle end", sentences.get(0).get(CoreAnnotations.TextAnnotation.class));
}
@Test
public void testRetainsCorrectIndicesForTokensAfterSplitting() {
  String text = "First sentence. Second sentence.";

  CoreLabel token1 = new CoreLabel();
  token1.setWord("First");
  token1.set(CoreAnnotations.TextAnnotation.class, "First");
  token1.set(CoreAnnotations.OriginalTextAnnotation.class, "First");
  token1.set(CoreAnnotations.IsNewlineAnnotation.class, false);
  token1.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 0);
  token1.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 5);

  CoreLabel token2 = new CoreLabel();
  token2.setWord("sentence");
  token2.set(CoreAnnotations.TextAnnotation.class, "sentence");
  token2.set(CoreAnnotations.OriginalTextAnnotation.class, "sentence");
  token2.set(CoreAnnotations.IsNewlineAnnotation.class, false);
  token2.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 6);
  token2.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 14);

  CoreLabel token3 = new CoreLabel();
  token3.setWord(".");
  token3.set(CoreAnnotations.TextAnnotation.class, ".");
  token3.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
  token3.set(CoreAnnotations.IsNewlineAnnotation.class, false);
  token3.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 14);
  token3.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 15);

  CoreLabel token4 = new CoreLabel();
  token4.setWord("Second");
  token4.set(CoreAnnotations.TextAnnotation.class, "Second");
  token4.set(CoreAnnotations.OriginalTextAnnotation.class, "Second");
  token4.set(CoreAnnotations.IsNewlineAnnotation.class, false);
  token4.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 16);
  token4.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 22);

  CoreLabel token5 = new CoreLabel();
  token5.setWord("sentence");
  token5.set(CoreAnnotations.TextAnnotation.class, "sentence");
  token5.set(CoreAnnotations.OriginalTextAnnotation.class, "sentence");
  token5.set(CoreAnnotations.IsNewlineAnnotation.class, false);
  token5.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 23);
  token5.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 31);

  CoreLabel token6 = new CoreLabel();
  token6.setWord(".");
  token6.set(CoreAnnotations.TextAnnotation.class, ".");
  token6.set(CoreAnnotations.OriginalTextAnnotation.class, ".");
  token6.set(CoreAnnotations.IsNewlineAnnotation.class, false);
  token6.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, 31);
  token6.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, 32);

  List<CoreLabel> tokens = Arrays.asList(token1, token2, token3, token4, token5, token6);

  Annotation annotation = new Annotation(text);
  annotation.set(CoreAnnotations.TextAnnotation.class, text);
  annotation.set(CoreAnnotations.TokensAnnotation.class, tokens);

  WordsToSentencesAnnotator annotator = new WordsToSentencesAnnotator();
  annotator.annotate(annotation);

  List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
  assertEquals(2, sentences.size());

  
  List<CoreLabel> sent1Tokens = sentences.get(0).get(CoreAnnotations.TokensAnnotation.class);
  assertEquals(0, sent1Tokens.get(0).sentIndex());

  List<CoreLabel> sent2Tokens = sentences.get(1).get(CoreAnnotations.TokensAnnotation.class);
  assertEquals(1, sent2Tokens.get(0).sentIndex());
} 
}